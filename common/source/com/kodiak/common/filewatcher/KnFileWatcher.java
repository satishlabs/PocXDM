/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.filewatcher;

import java.io.File;

import com.kodiak.logger.KnLogger;




public class KnFileWatcher 
{
	private static final KnLogger knLogger = KnLogger.getLogger(KnFileWatcher.class);

	public static void initWatcher(String activeReleasePath, String[] fileName, KnFilePropertiesUpdate propertiesUpdate) throws Exception
	{
		String method = "initWatcher()";
		knLogger.debug(method, "Inside init " + activeReleasePath);
		KnFileWatcherService watchService = null;
		try 
		{
			watchService = new KnFileWatcherServiceImpl();
			watchService.register(new KnFileWatcherService.OnFileChangeListener() 
			{
				@Override
				public void onFileCreate(String activeReleasePath, String fileName)
				{
					knLogger.info(method, "Properties file created. File Path: ",  activeReleasePath + File.separator + fileName );
					propertiesUpdate.updateProperties(activeReleasePath, fileName);
					// File created
				}

				@Override
				public void onFileModify(String activeReleasePath, String fileName)
				{
					// File modified
					knLogger.info(method, "Properties file updated. File Path: ", activeReleasePath + File.separator + fileName);
					propertiesUpdate.updateProperties(activeReleasePath, fileName);

				}

				@Override
				public void onFileDelete(String activeReleasePath)
				{
					knLogger.info(method, "Properties file deleted. File Path: " , activeReleasePath);
					// File deleted
				}
			}, activeReleasePath, // Directory to watch
					fileName// E.g. ["file1", "file2", "file3"] As many files as you like
					);

			watchService.start();
		} 
		catch (Exception e) 
		{
			knLogger.error(method, "Unable to register file change listener file " + fileName, e);
			if(watchService != null)
				watchService.stop();
			throw e;
		}
	}

}
