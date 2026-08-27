/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpContactBean.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Gunjan Kumar      April 10, 2014      7.9
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.List;
import java.util.Map;

public class KnCorpContactBean {
	Map<String, KnCorpSubscriberDTO> corpSubscriberDTOMap;
	List<String> provisionedMdnList;
	//includes the thirdparty client, mobile clients
	List<String> thirdPartyUsers;
	public Map<String, KnCorpSubscriberDTO> getCorpSubscriberDTOMap() {
		return corpSubscriberDTOMap;
	}
	public void setCorpSubscriberDTOMap(
			Map<String, KnCorpSubscriberDTO> corpSubscriberDTOMap) {
		this.corpSubscriberDTOMap = corpSubscriberDTOMap;
	}
	public List<String> getProvisionedMdnList() {
		return provisionedMdnList;
	}
	public void setProvisionedMdnList(List<String> provisionedMdnList) {
		this.provisionedMdnList = provisionedMdnList;
	}

	public List<String> getThirdPartyUsers() {
		return thirdPartyUsers;
	}

	public void setThirdPartyUsers(List<String> thirdPartyUsers) {
		this.thirdPartyUsers = thirdPartyUsers;
	}
}
