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
import net.minidev.json.JSONObject;


@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class KnKpnsNotification
{
	
	private String type;
	
	private String ver;
	
	private KnNtfnHdr hdr;
	
	private JSONObject payload;
	
	private KnNtfnApns apnsParams;

	private JSONObject notification_payload;


	/**
	 * @return the hdr
	 */
	public KnNtfnHdr getHdr() {
		return hdr;
	}
	/**
	 * @param hdr the hdr to set
	 */
	public void setHdr(KnNtfnHdr hdr) {
		this.hdr = hdr;
	}
	/**
	 * @return the payload
	 */
	public JSONObject getPayload() {
		return payload;
	}
	/**
	 * @param payload the payload to set
	 */
	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}
	/**
	 * @return the apnsParams
	 */
	public KnNtfnApns getApnsParams() {
		return apnsParams;
	}
	/**
	 * @param apnsParams the apnsParams to set
	 */
	public void setApnsParams(KnNtfnApns apnsParams) {
		this.apnsParams = apnsParams;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public JSONObject getNotification_payload() {
		return notification_payload;
	}

	public void setNotification_payload(JSONObject notification_payload) {
		this.notification_payload = notification_payload;
	}

	@Override
	public String toString() {
		return "KnKpnsNotification{" +
				"type='" + type + '\'' +
				", ver='" + ver + '\'' +
				", hdr=" + hdr +
				", payload='" + payload + '\'' +
				", apnsParams=" + apnsParams +
				", notification_Payload=" + notification_payload +
				'}';
	}

}
