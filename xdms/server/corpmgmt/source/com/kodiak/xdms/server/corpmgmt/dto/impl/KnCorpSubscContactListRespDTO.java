/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSubscContactListRespDTO.java
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

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;

import java.util.Collection;


public class KnCorpSubscContactListRespDTO extends KnCorpContactListRespDTO {

    private Collection<KnCorpSublistDTO> sublistList;
    private int totalContacts;
    private int maxContactLimitFlag;
    private int totalGroups;
    private int totalUpms;

    public Collection<KnCorpSublistDTO> getSublistList() {
        return sublistList;
    }

    public void setSublistList(Collection<KnCorpSublistDTO> sublistList) {
        this.sublistList = sublistList;
    }

    public int getTotalContacts() {
        return totalContacts;
    }

    public void setTotalContacts(int totalContacts) {
        this.totalContacts = totalContacts;
    }

    public int getMaxContactLimitFlag() {
        return maxContactLimitFlag;
    }

    public void setMaxContactLimitFlag(int maxContactLimitFlag) {
        this.maxContactLimitFlag = maxContactLimitFlag;
    }

    public int getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(int totalGroups) {
        this.totalGroups = totalGroups;
    }

    public int getTotalUpms() {
        return totalUpms;
    }

    public void setTotalUpms(int totalUpms) {
        this.totalUpms = totalUpms;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
                .append(", SublistList - ").append(sublistList)
                .append(", totalContacts - ").append(totalContacts)
                .append(", maxContactLimitFlag - ").append(maxContactLimitFlag)
                .append(", totalGroups - ").append(totalGroups)
                .append(", totalUpms - ").append(totalUpms);
        return sb.toString();

    }
}
