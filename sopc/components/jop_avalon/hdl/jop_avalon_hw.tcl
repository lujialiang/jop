# TCL File Generated by Component Editor on: 
# Fri Sep 14 10:06:24 CEST 2007
# DO NOT MODIFY

set_source_file "jop_avalon.vhd"
set_module "jop_avalon"
set_module_description ""
set_module_property instantiateInSystemModule true
set_module_property version "1.0"
set_module_property group "JOP"
set_module_property editable true
set_module_property simulationFiles {
  "jop_avalon.vhd"}

# Module parameters
add_parameter "addr_bits" "integer" "24" ""
add_parameter "jpc_width" "integer" "12" ""
add_parameter "block_bits" "integer" "4" ""

# Wire Interface global_signals_export
add_interface "global_signals_export" "conduit" "output" "asynchronous"
# Ports in interface global_signals_export
add_port_to_interface "global_signals_export" "ser_txd" "export"
add_port_to_interface "global_signals_export" "ser_rxd" "export"
add_port_to_interface "global_signals_export" "wd" "export"

# Clock Interface avalon_master_clock
add_clock_interface "avalon_master_clock" 
set_interface_property "avalon_master_clock" "externallyDriven" "false"
set_interface_property "avalon_master_clock" "clockRateKnown" "false"
set_interface_property "avalon_master_clock" "clockRate" "0"
# Ports in interface avalon_master_clock
add_port_to_interface "avalon_master_clock" "clk" "clk"
add_port_to_interface "avalon_master_clock" "reset" "reset"

# Interface avalon_master
add_interface "avalon_master" "avalon" "master" "avalon_master_clock"
set_interface_property "avalon_master" "interleaveBursts" "false"
set_interface_property "avalon_master" "burstOnBurstBoundariesOnly" "false"
set_interface_property "avalon_master" "doStreamReads" "false"
set_interface_property "avalon_master" "isBigEndian" "false"
set_interface_property "avalon_master" "isWriteable" "false"
set_interface_property "avalon_master" "isAsynchronous" "false"
set_interface_property "avalon_master" "registerOutgoingSignals" "false"
set_interface_property "avalon_master" "maxAddressWidth" "32"
set_interface_property "avalon_master" "registerIncomingSignals" "false"
set_interface_property "avalon_master" "dBSBigEndian" "false"
set_interface_property "avalon_master" "alwaysBurstMaxBurst" "false"
set_interface_property "avalon_master" "linewrapBursts" "false"
set_interface_property "avalon_master" "addressGroup" "0"
set_interface_property "avalon_master" "doStreamWrites" "false"
set_interface_property "avalon_master" "isReadable" "false"
# Ports in interface avalon_master
add_port_to_interface "avalon_master" "address" "address"
add_port_to_interface "avalon_master" "writedata" "writedata"
add_port_to_interface "avalon_master" "byteenable" "byteenable"
add_port_to_interface "avalon_master" "readdata" "readdata"
add_port_to_interface "avalon_master" "read" "read"
add_port_to_interface "avalon_master" "write" "write"
add_port_to_interface "avalon_master" "waitrequest" "waitrequest"
