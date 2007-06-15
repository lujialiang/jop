// XXX continue
package yaffs2.port;

import static yaffs2.utils.Utils.*;
import static yaffs2.utils.Constants.*;
import static yaffs2.assertions.Assert.*;
import yaffs2.utils.*;
import static yaffs2.utils.CStandard.*;
import static yaffs2.port.devextras.*;


import static yaffs2.port.yaffs_ObjectHeader.*;
import static yaffs2.port.Guts_H.*;
import static yaffs2.port.yaffs_BlockInfo.*;
import static yaffs2.port.yaffs_Spare.*;
import static yaffs2.port.yaffs_ExtendedTags.*;
import static yaffs2.port.yaffs_Tags.*;
import static yaffs2.port.yaffs_ChunkCache.*;
import static yaffs2.port.CFG_H.*;
import static yaffs2.port.yaffsfs_DeviceConfiguration.*;
import static yaffs2.port.ECC_C.*;
import static yaffs2.port.yaffs_ECCOther.*;
import static yaffs2.port.yportenv.*;
import static yaffs2.port.ydirectenv.*;


public class yaffscfg_C
{
	/*
	 * YAFFS: Yet Another Flash File System. A NAND-flash specific file system.
	 *
	 * Copyright (C) 2002-2007 Aleph One Ltd.
	 *   for Toby Churchill Ltd and Brightstar Engineering
	 *
	 * Created by Charles Manning <charles@aleph1.co.uk>
	 *
	 * This program is free software; you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License version 2 as
	 * published by the Free Software Foundation.
	 */

	/*
	 * yaffscfg.c  The configuration for the "direct" use of yaffs.
	 *
	 * This file is intended to be modified to your requirements.
	 * There is no need to redistribute this file.
	 */

	/*unsigned */long yaffs_traceMask = intAsUnsignedInt(0xFFFFFFFF);


	public static void yaffsfs_SetError(int err)
	{
		//Do whatever to set error
	//	errno = err;
	}

	public static void yaffsfs_Lock()
	{
	}

	public static void yaffsfs_Unlock()
	{
	}

	public static int yaffsfs_CurrentTime()
	{
		return 0;
	}

	public static void yaffsfs_LocalInitialisation()
	{
		// Define locking semaphore.
	}

//	 Configuration for:
//	 /ram  2MB ramdisk
//	 /boot 2MB boot disk (flash)
//	 /flash 14MB flash disk (flash)
//	 NB Though /boot and /flash occupy the same physical device they
//	 are still disticnt "yaffs_Devices. You may think of these as "partitions"
//	 using non-overlapping areas in the same device.
//	 

//	#include "yaffs_ramdisk.h"
//	#include "yaffs_flashif.h"

	protected static yaffs_Device ramDev = new yaffs_Device();
	protected static yaffs_Device bootDev = new yaffs_Device();
	protected static yaffs_Device flashDev = new yaffs_Device();

	protected yaffsfs_DeviceConfiguration[] yaffsfs_config = {

		new yaffsfs_DeviceConfiguration( "/ram", ramDev),
		new yaffsfs_DeviceConfiguration( "/boot", bootDev),
		new yaffsfs_DeviceConfiguration( "/flash", flashDev),
		new yaffsfs_DeviceConfiguration( null,null)
	};


	int yaffs_StartUp()
	{
		// Stuff to configure YAFFS
		// Stuff to initialise anything special (eg lock semaphore).
		yaffsfs_LocalInitialisation();
		
		// Set up devices

		// /ram
		ramDev.nBytesPerChunk = 512;	// XXX where is this defined?
		ramDev.nChunksPerBlock = 32;
		ramDev.nReservedBlocks = 2; // Set this smaller for RAM
		ramDev.startBlock = 1; // Can't use block 0
		ramDev.endBlock = 127; // Last block in 2MB.	
		ramDev.useNANDECC = 1;
		ramDev.nShortOpCaches = 0;	// Disable caching on this device.
		ramDev.genericDevice = (void *) 0;	// Used to identify the device in fstat.
		ramDev.writeChunkWithTagsToNAND = yramdisk_WriteChunkWithTagsToNAND;
		ramDev.readChunkWithTagsFromNAND = yramdisk_ReadChunkWithTagsFromNAND;
		ramDev.eraseBlockInNAND = yramdisk_EraseBlockInNAND;
		ramDev.initialiseNAND = yramdisk_InitialiseNAND;

		// /boot
		bootDev.nBytesPerChunk = 612;
		bootDev.nChunksPerBlock = 32;
		bootDev.nReservedBlocks = 5;
		bootDev.startBlock = 1; // Can't use block 0
		bootDev.endBlock = 127; // Last block in 2MB.	
		bootDev.useNANDECC = 0; // use YAFFS's ECC
		bootDev.nShortOpCaches = 10; // Use caches
		bootDev.genericDevice = (void *) 1;	// Used to identify the device in fstat.
		bootDev.writeChunkToNAND = yflash_WriteChunkToNAND;
		bootDev.readChunkFromNAND = yflash_ReadChunkFromNAND;
		bootDev.eraseBlockInNAND = yflash_EraseBlockInNAND;
		bootDev.initialiseNAND = yflash_InitialiseNAND;

			// /flash
		flashDev.nBytesPerChunk =  512;
		flashDev.nChunksPerBlock = 32;
		flashDev.nReservedBlocks = 5;
		flashDev.startBlock = 128; // First block after 2MB
		flashDev.endBlock = 1023; // Last block in 16MB
		flashDev.useNANDECC = 0; // use YAFFS's ECC
		flashDev.nShortOpCaches = 10; // Use caches
		flashDev.genericDevice = (void *) 2;	// Used to identify the device in fstat.
		flashDev.writeChunkToNAND = yflash_WriteChunkToNAND;
		flashDev.readChunkFromNAND = yflash_ReadChunkFromNAND;
		flashDev.eraseBlockInNAND = yflash_EraseBlockInNAND;
		flashDev.initialiseNAND = yflash_InitialiseNAND;

		yaffs_initialise(yaffsfs_config);
		
		return 0;
	}
}