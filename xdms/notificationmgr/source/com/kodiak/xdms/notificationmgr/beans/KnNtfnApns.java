/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 */
package com.kodiak.xdms.notificationmgr.beans;

public class KnNtfnApns 
{
	
	int apns_priority;
	
	long apns_expiration;
	
	String apns_topic;
	
	//String apns_collapse_id;
	
	boolean content_available;
	
	/**
	 * @return the apns_priority
	 */
	public int getApns_priority() {
		return apns_priority;
	}
	/**
	 * @param apns_priority the apns_priority to set
	 */
	public void setApns_priority(int apns_priority) {
		this.apns_priority = apns_priority;
	}
	/**
	 * @return the apns_expiration
	 */
	public long getApns_expiration() {
		return apns_expiration;
	}
	/**
	 * @param apns_expiration the apns_expiration to set
	 */
	public void setApns_expiration(long apns_expiration) {
		this.apns_expiration = apns_expiration;
	}
	/**
	 * @return the apns_topic
	 */
	public String getApns_topic() {
		return apns_topic;
	}
	/**
	 * @param apns_topic the apns_topic to set
	 */
	public void setApns_topic(String apns_topic) {
		this.apns_topic = apns_topic;
	}
	
	
	/**
	 * @return the content_available
	 */
	public boolean isContent_available() {
		return content_available;
	}
	/**
	 * @param content_available the content_available to set
	 */
	public void setContent_available(boolean content_available) {
		this.content_available = content_available;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KnNtfnApns [apns_priority=" + apns_priority
				+ ", apns_expiration=" + apns_expiration + ", apns_topic="
				+ apns_topic + ", "
				+ ", content_available=" + content_available + "]";
	}
	
	
	
}
