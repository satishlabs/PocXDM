/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 19, 2011      7.0
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

import com.kodiak.common.commdto.common.KnXDMCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfilelistDTO;

import java.util.Collection;


public class KnCorpUserProfileRespDTO extends KnCorpResponseDTO {


    private String corpId;
    //private String ownerMdn;
    private KnCorpUserProfileDTO userProfileInfo;
    // private List<KnXDMCorpContactDTO> contactInfoList;
    private Collection<KnCorpUserProfileDTO> userProfilelistLists;
    //private int totalContactsCount;
    //private int maxContactsLimitFlag;
    //private List<KnExternalSubsDetailsDTO> extSubsList;
    // private int count;


    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public KnCorpUserProfileDTO getUserProfileInfo() {
        return userProfileInfo;
    }

    public void setUserProfileInfo(KnCorpUserProfileDTO userProfileInfo) {
        this.userProfileInfo = userProfileInfo;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfilelistLists() {
        return userProfilelistLists;
    }

    public void setUserProfilelistLists(Collection<KnCorpUserProfileDTO> userProfilelistLists) {
        this.userProfilelistLists = userProfilelistLists;
    }
}
