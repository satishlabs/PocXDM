/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpContactListDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 15, 2011      7.0
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
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.Collection;


public class KnXDMCorpContactListDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676124L;

    private String ownerMdn;
    private KnXDMCorpSublistInfoDTO contactListInfo;
    private Collection<KnXDMCorpContactDTO> contactInfoList;
    private Collection<KnXDMCorpSublistDTO> sublistLists;

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
    }

    public KnXDMCorpSublistInfoDTO getContactListInfo() {
        return contactListInfo;
    }

    public void setContactListInfo(KnXDMCorpSublistInfoDTO contactListInfo) {
        this.contactListInfo = contactListInfo;
    }

    public Collection<KnXDMCorpContactDTO> getContactInfoList() {
        return contactInfoList;
    }

    public void setContactInfoList(Collection<KnXDMCorpContactDTO> contactInfoList) {
        this.contactInfoList = contactInfoList;
    }

    public Collection<KnXDMCorpSublistDTO> getSublistLists() {
        return sublistLists;
    }

    public void setSublistLists(Collection<KnXDMCorpSublistDTO> sublistLists) {
        this.sublistLists = sublistLists;
    }

    public String getObjectId() {
        return ownerMdn;
    }
}
