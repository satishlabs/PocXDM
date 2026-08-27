/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistDistributionRespDTO.java
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

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;

import java.util.Collection;

public class KnCorpSublistDistributionRespDTO extends KnCorpResponseDTO {

    private Collection<KnCorpContactDTO> contactList;
    private Collection<KnCorpGroupInfoDTO> groupList;

    public Collection<KnCorpContactDTO> getContactList() {
        return contactList;
    }

    public void setContactList(Collection<KnCorpContactDTO> contactList) {
        this.contactList = contactList;
    }

    public Collection<KnCorpGroupInfoDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Collection<KnCorpGroupInfoDTO> groupList) {
        this.groupList = groupList;
    }
}