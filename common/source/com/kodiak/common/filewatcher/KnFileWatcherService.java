/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.filewatcher;

import java.io.IOException;

public interface KnFileWatcherService {

	/**
	 * Starts the service. This method blocks until the service has completely started.
	 */
	void start() throws Exception;

	/**
	 * Stops the service. This method blocks until the service has completely shut down.
	 */
	void stop();

	void register(OnFileChangeListener listener, String dirPath, String... globPatterns) throws IOException;

	interface OnFileChangeListener 
	{
		default void onFileCreate(String filePath, String fileName)
		{
		}

		default void onFileModify(String filePath, String fileName)
		{
		}

		default void onFileDelete(String filePath)
		{
		}
	}
}
