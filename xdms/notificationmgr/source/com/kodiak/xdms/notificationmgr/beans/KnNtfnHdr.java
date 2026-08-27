/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 */
package com.kodiak.xdms.notificationmgr.beans;



import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class KnNtfnHdr 
{
	
	private String src;
		private long ts;
	
	private String type;
	
	private String origRoutingKey;
	
	private List<KnNtfnHdrDest> dest;
	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	/**
	 * @return the ts
	 */
	public long getTs() {
		return ts;
	}
	/**
	 * @param ts the ts to set
	 */
	public void setTs(long ts) {
		this.ts = ts;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the origRoutingKey
	 */
	public String getOrigRoutingKey() {
		return origRoutingKey;
	}
	/**
	 * @param origRoutingKey the origRoutingKey to set
	 */
	public void setOrigRoutingKey(String origRoutingKey) {
		this.origRoutingKey = origRoutingKey;
	}
	/**
	 * @return the dest
	 */
	public List<KnNtfnHdrDest> getDest() {
		return dest;
	}
	/**
	 * @param dest the dest to set
	 */
	public void setDest(List<KnNtfnHdrDest> dest) {
		this.dest = dest;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KnNtfnHdr [src=" + src + ", ts=" + ts + ", type=" + type
				+ ", origRoutingKey=" + origRoutingKey + ", dest=" + dest + "]";
	}
	
	
}
