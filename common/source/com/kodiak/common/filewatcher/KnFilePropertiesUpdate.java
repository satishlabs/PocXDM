/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.filewatcher;

@FunctionalInterface
public interface KnFilePropertiesUpdate {
	public  void updateProperties(String activeReleasePath, String fileName);
}
