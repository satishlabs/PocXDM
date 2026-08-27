/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpDistributionListRespDTO.java
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
import com.kodiak.common.commdto.common.KnXDMCorpSublistDTO;

import java.util.Collection;


public class KnXDMCorpDistributionListRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622776149L;

    private Collection<KnXDMCorpContactDTO> contactList;
    private Collection<KnXDMCorpSublistDTO> sublistList;


    public Collection<KnXDMCorpContactDTO> getContactList() {
        return contactList;
    }

    public void setContactList(Collection<KnXDMCorpContactDTO> contactList) {
        this.contactList = contactList;
    }

    public Collection<KnXDMCorpSublistDTO> getSublistList() {
        return sublistList;
    }

    public void setSublistList(Collection<KnXDMCorpSublistDTO> sublistList) {
        this.sublistList = sublistList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", ContactList - ").append(contactList)
                .append(", SublistList - ").append(sublistList);
        return sb.toString();
    }
}
