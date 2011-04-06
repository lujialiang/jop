/*
 * This file is part of JOP, the Java Optimized Processor
 *   see <http://www.jopdesign.com/>
 *
 * Copyright (C) 2011, Stefan Hepp (stefan@stefant.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jopdesign.jcopter;

import com.jopdesign.common.AppInfo;
import com.jopdesign.common.MethodInfo;
import com.jopdesign.common.code.CallGraph;
import com.jopdesign.common.code.CallGraph.DUMPTYPE;
import com.jopdesign.common.code.ExecutionContext;
import com.jopdesign.common.config.BooleanOption;
import com.jopdesign.common.config.Config;
import com.jopdesign.common.config.EnumOption;
import com.jopdesign.common.config.Option;
import com.jopdesign.common.config.OptionGroup;
import com.jopdesign.common.graphutils.MethodTraverser;
import com.jopdesign.common.graphutils.MethodTraverser.MethodVisitor;
import com.jopdesign.common.misc.AppInfoError;
import com.jopdesign.common.tools.ClinitOrder;
import com.jopdesign.common.tools.ConstantPoolRebuilder;
import com.jopdesign.jcopter.optimize.LoadStoreOptimizer;
import com.jopdesign.jcopter.optimize.PeepholeOptimizer;
import com.jopdesign.jcopter.optimize.RelinkInvokesuper;
import com.jopdesign.jcopter.optimize.UnusedCodeRemover;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class executes various optimizations and analyses in the appropriate order, depending on the configuration.
 *
 * @author Stefan Hepp (stefan@stefant.org)
 */
public class PhaseExecutor {

    public static final Logger logger = Logger.getLogger(JCopter.LOG_ROOT + ".PhaseExecutor");

    public static final BooleanOption REMOVE_UNUSED_MEMBERS =
            new BooleanOption("remove-unused-members", "Remove unreachable code", true);

    public static final EnumOption<DUMPTYPE> DUMP_CALLGRAPH =
            new EnumOption<DUMPTYPE>("dump-callgraph", "Dump the app callgraph (with or without callstrings)", CallGraph.DUMPTYPE.merged);

    public static final EnumOption<DUMPTYPE> DUMP_JVM_CALLGRAPH =
            new EnumOption<DUMPTYPE>("dump-jvm-callgraph", "Dump the jvm callgraph (with or without callstrings)", CallGraph.DUMPTYPE.off);

    public static final BooleanOption DUMP_NOIM_CALLS =
            new BooleanOption("dump-noim-calls", "Include calls to JVMHelp.noim() in the jvm callgraph dump", false);


    public static final Option[] phaseOptions = {
            DUMP_CALLGRAPH, DUMP_JVM_CALLGRAPH, DUMP_NOIM_CALLS, CallGraph.CALLGRAPH_DIR,
        };
    public static final Option[] optimizeOptions = {
            REMOVE_UNUSED_MEMBERS
        };

    public static final String GROUP_OPTIMIZE = "opt";
    public static final String GROUP_INLINE   = "inline";

    public static void registerOptions(OptionGroup options) {
        // Add phase options
        options.addOptions(phaseOptions);

        // Add options of all used optimizations
        OptionGroup opt = options.getGroup(GROUP_OPTIMIZE);
        opt.addOptions(optimizeOptions);
        opt.addOptions(UnusedCodeRemover.optionList);

    }

    private final JCopter jcopter;
    private final OptionGroup options;
    private final AppInfo appInfo;

    public PhaseExecutor(JCopter jcopter, OptionGroup options) {
        this.jcopter = jcopter;
        this.options = options;
        appInfo = AppInfo.getSingleton();
    }

    public Config getConfig() {
        return options.getConfig();
    }

    public OptionGroup getPhaseOptions() {
        return options;
    }

    public OptionGroup getOptimizeOptions() {
        return options.getGroup(GROUP_OPTIMIZE);
    }

    public OptionGroup getInlineOptions() {
        return options.getGroup(GROUP_INLINE);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Dump Callgraph
    /////////////////////////////////////////////////////////////////////////////////////

    public void dumpCallgraph(String graphName) {
        if (getConfig().getOption(DUMP_CALLGRAPH) == CallGraph.DUMPTYPE.off &&
            getConfig().getOption(DUMP_JVM_CALLGRAPH) == CallGraph.DUMPTYPE.off)
        {
            return;
        }

        try {
            // Dumping the full graph is a bit much, we split it into several graphs
            Set<ExecutionContext> appRoots = new HashSet<ExecutionContext>();
            Set<ExecutionContext> jvmRoots = new HashSet<ExecutionContext>();
            Set<ExecutionContext> clinitRoots = new HashSet<ExecutionContext>();

            Set<String> jvmClasses = new HashSet<String>();
            if (appInfo.getProcessorModel() != null) {
                jvmClasses.addAll( appInfo.getProcessorModel().getJVMClasses() );
                jvmClasses.addAll( appInfo.getProcessorModel().getNativeClasses() );
            }

            CallGraph graph = appInfo.getCallGraph();

            for (ExecutionContext ctx : graph.getRootNodes()) {
                if (ctx.getMethodInfo().getMethodSignature().equals(ClinitOrder.clinitSig)) {
                    clinitRoots.add(ctx);
                } else if (jvmClasses.contains(ctx.getMethodInfo().getClassName())) {
                    jvmRoots.add(ctx);
                } else {
                    // Should we dump Runnable.run() into another graph?
                    appRoots.add(ctx);
                }
            }

            Config config = getConfig();

            graph.dumpCallgraph(config, graphName, "app", appRoots, config.getOption(DUMP_CALLGRAPH), false);
            graph.dumpCallgraph(config, graphName, "clinit", clinitRoots, config.getOption(DUMP_CALLGRAPH), false);
            graph.dumpCallgraph(config, graphName, "jvm", jvmRoots, config.getOption(DUMP_JVM_CALLGRAPH),
                                                                   !config.getOption(DUMP_NOIM_CALLS));

        } catch (IOException e) {
            throw new AppInfoError("Unable to export to .dot file", e);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Perform analyses
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reduce the callgraph stored with AppInfo.
     * {@link AppInfo#buildCallGraph(boolean)} must have been called first.
     */
    public void reduceCallGraph() {
        // TODO perform callgraph thinning analysis
        // logger.info("Starting callgraph reduction");

        // logger.info("Finished callgraph reduction");
    }

    /**
     * Mark all InvokeSites which are safe to inline, or store info
     * about what needs to be done in order to inline them.
     * To get better results, reduce the callgraph first as much as possible.
     */
    public void markInlineCandidates() {
        // TODO call invoke candidate finder
    }


    /////////////////////////////////////////////////////////////////////////////////////
    // Perform optimizations
    /////////////////////////////////////////////////////////////////////////////////////

    public void relinkInvokesuper() {
        appInfo.iterate(new RelinkInvokesuper());
    }

    /**
     * Inline all methods which do not increase the code size.
     * {@link #markInlineCandidates()} must have been run first.
     */
    public void performSimpleInline() {
    }

    /**
     * Inline all InvokeSites which are marked for inlining by an inline strategy.
     */
    public void performInline() {
    }

    /**
     * Run some simple optimizations to cleanup the bytecode without increasing its size.
     */
    public void cleanupMethodCode() {
        logger.info("Starting code cleanup");

        // perform some simple and safe peephole optimizations
        new PeepholeOptimizer(jcopter).optimize();
        
        // optimize load/store
        // TODO implement this ..
        new LoadStoreOptimizer(jcopter).optimize();

        // (more complex optimizations (dead-code elimination, constant-folding,..) should
        //  go into another method..)
        logger.info("Finished code cleanup");
    }

    public void removeDebugAttributes() {
        logger.info("Starting removal of debug attributes");

        MethodVisitor visitor = new MethodVisitor() {
            @Override
            public void visitMethod(MethodInfo method) {
                method.getCode().removeDebugAttributes();
            }
        };
        appInfo.iterate(new MethodTraverser(visitor, true));

        logger.info("Finished removal of debug attributes");
    }

    /**
     * Find and remove unused classes, methods and fields
     */
    public void removeUnusedMembers() {

        if (!getPhaseOptions().getOption(REMOVE_UNUSED_MEMBERS)) {
            return;
        }

        logger.info("Starting removal of unused members");

        new UnusedCodeRemover(jcopter, getOptimizeOptions()).execute();

        logger.info("Finished removal of unused members");
    }

    /**
     * Rebuild all constant pools.
     */
    public void cleanupConstantPool() {
        logger.info("Starting cleanup of constant pools");

        appInfo.iterate(new ConstantPoolRebuilder());

        logger.info("Finished cleanup of constant pools");
    }
}
