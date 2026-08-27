/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpSublistRequestDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.common.KnXDMCorpGroupDTO;

import java.util.Collection;


public class KnXDMCorpDistRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776165L;

    private Collection<KnXDMCorpContactDTO> contactList;
    private Collection<KnXDMCorpGroupDTO> groupList;

    public Collection<KnXDMCorpContactDTO> getContactList() {
        return contactList;
    }

    public void setContactList(Collection<KnXDMCorpContactDTO> contactList) {
        this.contactList = contactList;
    }

    public Collection<KnXDMCorpGroupDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Collection<KnXDMCorpGroupDTO> groupList) {
        this.groupList = groupList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("ObjectId - ").append(getObjectId())
                .append(", ContactList - ").append(contactList)
                .append(", SublistIds - ").append(groupList);
        return sb.toString();
    }
}