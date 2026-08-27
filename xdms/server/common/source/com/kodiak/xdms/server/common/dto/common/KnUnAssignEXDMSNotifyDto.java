/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnUnAssignEXDMSNotifyDto extends KnEXDMSNotifyDto{
	private Integer corpid;
	private Integer clientType;
	private String pv;
	private KnMDNDetailsDTO baseMdn;
	private List<String> profileMdns;
	public Integer getCorpid() {
		return corpid;
	}
	public void setCorpid(Integer corpid) {
		this.corpid = corpid;
	}
	public Integer getClientType() {
		return clientType;
	}
	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	public String getPv() {
		return pv;
	}
	public void setPv(String pv) {
		this.pv = pv;
	}
	public KnMDNDetailsDTO getBaseMdn() {
		return baseMdn;
	}
	public void setBaseMdn(KnMDNDetailsDTO baseMdn) {
		this.baseMdn = baseMdn;
	}
	public List<String> getProfileMdns() {
		return profileMdns;
	}
	public void setProfileMdns(List<String> profileMdns) {
		this.profileMdns = profileMdns;
	}
	
}
