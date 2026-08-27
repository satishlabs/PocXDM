/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistListRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 20, 2011      7.0
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

import java.util.Collection;
import java.util.Map;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnMDNInfoDto;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubscriberUserProfileDTO;


public class KnCorpUserProfileListRespDTO extends KnCorpResponseDTO {

    private Collection<KnCorpUserProfileDTO> userProfilelistList;

	private Collection<KnSubscriberUserProfileDTO> subscriberUserProfileList;

    private Integer maxtotalcount;

    private Collection<KnMDNInfoDto> mdnInfoCollection;
    private Map<Integer, String> corpIdMap;

    public Collection<KnCorpUserProfileDTO> getUserProfileList() {
        return userProfilelistList;
    }

    public void setUserProfileList(Collection<KnCorpUserProfileDTO> userProfilelistList) {
        this.userProfilelistList = userProfilelistList;
    }

    public Collection<KnSubscriberUserProfileDTO> getSubscriberUserProfileList() {
    	return subscriberUserProfileList;
    }
    
    public void setSubscriberUserProfileList(Collection<KnSubscriberUserProfileDTO> subscriberUserProfileList) {
    	this.subscriberUserProfileList = subscriberUserProfileList;
    }
    
    public Integer getMaxtotalcount() {
        return maxtotalcount;
    }

    public void setMaxtotalcount(Integer maxtotalcount) {
        this.maxtotalcount = maxtotalcount;
    }

    public Collection<KnMDNInfoDto> getMdnInfoCollection() { return mdnInfoCollection; }

    public void setMdnInfoCollection(Collection<KnMDNInfoDto> mdnInfoCollection) { this.mdnInfoCollection = mdnInfoCollection; }

    public Map<Integer, String> getCorpIdMap() {
        return corpIdMap;
    }

    public void setCorpIdMap(Map<Integer, String> corpIdMap) {
        this.corpIdMap = corpIdMap;
    }
}
