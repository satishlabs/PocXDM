/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 */
package com.kodiak.xdms.notificationmgr.beans;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnNtfnHdrDest 
{
	
	private String mdn;
	
	private String req_id;
	
	
	
	private String pn_channel_type;
	
	private int device_token_ind=2;
	
	private String encrypt_payload;
	
	private String encrypt_dk;
	
	private String encrypt_ha1;
	/**
	 * @return the mdn
	 */
	public String getMdn() {
		return mdn;
	}
	/**
	 * @param mdn the mdn to set
	 */
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	/**
	 * @return the req_id
	 */
	public String getReq_id() {
		return req_id;
	}
	/**
	 * @param req_id the req_id to set
	 */
	public void setReq_id(String req_id) {
		this.req_id = req_id;
	}
	/**
	 * @return the device_token
	 */

	/**
	 * @return the pn_channel_type
	 */
	public String getPn_channel_type() {
		return pn_channel_type;
	}
	/**
	 * @param pn_channel_type the pn_channel_type to set
	 */
	public void setPn_channel_type(String pn_channel_type) {
		this.pn_channel_type = pn_channel_type;
	}
	
	/**
	 * @return the device_token_ind
	 */
	public int getDevice_token_ind() {
		return device_token_ind;
	}
	/**
	 * @param device_token_ind the device_token_ind to set
	 */
	public void setDevice_token_ind(int device_token_ind) {
		this.device_token_ind = device_token_ind;
	}
	/**
	 * @return the encrypt_payload
	 */
	public String getEncrypt_payload() {
		return encrypt_payload;
	}
	/**
	 * @param encrypt_payload the encrypt_payload to set
	 */
	public void setEncrypt_payload(String encrypt_payload) {
		this.encrypt_payload = encrypt_payload;
	}
	/**
	 * @return the encrypt_dk
	 */
	public String getEncrypt_dk() {
		return encrypt_dk;
	}
	/**
	 * @param encrypt_dk the encrypt_dk to set
	 */
	public void setEncrypt_dk(String encrypt_dk) {
		this.encrypt_dk = encrypt_dk;
	}
	/**
	 * @return the encrypt_ha1
	 */
	public String getEncrypt_ha1() {
		return encrypt_ha1;
	}
	/**
	 * @param encrypt_ha1 the encrypt_ha1 to set
	 */
	public void setEncrypt_ha1(String encrypt_ha1) {
		this.encrypt_ha1 = encrypt_ha1;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KnNtfnHdrDest [mdn=" + KnGDPRTemplate.mdn(mdn) + ", req_id=" + req_id
				+ ",  pn_channel_type="
				+ pn_channel_type + ", device_token_ind=" + device_token_ind
				+ ", encrypt_payload=" + encrypt_payload + ", encrypt_dk="
				+ encrypt_dk + ", encrypt_ha1=" + encrypt_ha1 + "]";
	}
	
	
}
