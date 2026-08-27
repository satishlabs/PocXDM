/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMSublistInfoDTO.java
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

import java.util.Collection;
import java.util.List;

//can be used for Sublist details retrieval and create sublist
public class KnXDMCorpSublistInfoDTO extends KnXDMCorpSublistDTO {

    private static final long serialVersionUID = 7526471155622676129L;

    private Collection<KnXDMCorpContactDTO> memberList;
    private List<String> assignedSubsList;

    public Collection<KnXDMCorpContactDTO> getMemberList() {
        return memberList;
    }

    public void setMemberList(Collection<KnXDMCorpContactDTO> memberList) {
        this.memberList = memberList;
    }

    public List<String> getAssignedSubsList() {
        return assignedSubsList;
    }

    public void setAssignedSubsList(List<String> assignedSubsList) {
        this.assignedSubsList = assignedSubsList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(300);
        sb.append(super.toString())
                .append(", MemberList - ").append(memberList)
                .append(", assignedSubsList - ").append(assignedSubsList);
        return sb.toString();
    }
}
