/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 */
package com.kodiak.xdms.notificationmgr.beans;

/**
 * @author chethan
 *
 */
public class KnIOSXdmWakeUpNtfnPayload 
{
	private String dest_mdn;
	private KnIOSWakeup wakeup;
	
	
	/**
	 * @return the dest_mdn
	 */
	public String getDest_mdn() {
		return dest_mdn;
	}
	/**
	 * @param dest_mdn the dest_mdn to set
	 */
	public void setDest_mdn(String dest_mdn) {
		this.dest_mdn = dest_mdn;
	}
	/**
	 * @return the wakeup
	 */
	public KnIOSWakeup getWakeup() {
		return wakeup;
	}
	/**
	 * @param wakeup the wakeup to set
	 */
	public void setWakeup(KnIOSWakeup wakeup) {
		this.wakeup = wakeup;
	}
	
	
	
}
