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
public class KnIOSWakeup 
{
	private String tr;
	private Object ntfPayLoad;

	/**
	 * @return the tr
	 */
	public String getTr() {
		return tr;
	}

	/**
	 * @param tr the tr to set
	 */
	public void setTr(String tr) {
		this.tr = tr;
	}

	public Object getNtfPayLoad() {
		return ntfPayLoad;
	}

	public void setNtfPayLoad(Object ntfPayLoad) {
		this.ntfPayLoad = ntfPayLoad;
	}
	
	
}
