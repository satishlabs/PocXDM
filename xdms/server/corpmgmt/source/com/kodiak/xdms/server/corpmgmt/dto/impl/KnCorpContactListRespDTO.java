/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpContactListRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import java.util.List;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnTPUserAccountDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnExtSubsDetailsDTO;

import java.util.Collection;
import java.util.Map;


public class KnCorpContactListRespDTO extends KnCorpResponseDTO {

    private Map<String, KnCorpSubscriberDTO> extSubsMap;
	private List<KnCorpSubscriberDTO> contactList;
	private List<KnExtSubsDetailsDTO> extSubsList;
	private int count;
    private List<String> thirdPartySubsc;
    private Map<String,KnTPUserAccountDTO> thirdPartyDetails;


    public List<KnCorpSubscriberDTO> getContactList() {
        return contactList;
    }

	public void setContactList(List<KnCorpSubscriberDTO> contactList) {
        this.contactList = contactList;
    }

    public Map<String, KnCorpSubscriberDTO> getExtSubsMap() {
        return extSubsMap;
    }

    public void setExtSubsMap(Map<String, KnCorpSubscriberDTO> extSubsMap) {
        this.extSubsMap = extSubsMap;
    }

	public List<KnExtSubsDetailsDTO> getExtSubsList() {
		return extSubsList;
	}

	public void setExtSubsList(List<KnExtSubsDetailsDTO> extSubsList) {
		this.extSubsList = extSubsList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

    public List<String> getThirdPartySubsc() {
        return thirdPartySubsc;
    }

    public void setThirdPartySubsc(List<String> thirdPartySubsc) {
        this.thirdPartySubsc = thirdPartySubsc;
    }

    public Map<String, KnTPUserAccountDTO> getThirdPartyDetails() {
        return thirdPartyDetails;
    }

    public void setThirdPartyDetails(Map<String, KnTPUserAccountDTO> thirdPartyDetails) {
        this.thirdPartyDetails = thirdPartyDetails;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString());
        sb.append(", ContactList - ").append(contactList);
        sb.append(", extSubsMap - ").append(KnGDPRTemplate.mapKeyMdn(extSubsMap));
		sb.append(", extSubsList - ").append(extSubsList);
        sb.append(", count - ").append(count);
        sb.append(", thirdPartySubsc - ").append(KnGDPRTemplate.mdnList(thirdPartySubsc));
        sb.append(", thirdPartyDetails - ").append(KnGDPRTemplate.mapKeyMdn(thirdPartyDetails));
        return sb.toString();
    }
}
