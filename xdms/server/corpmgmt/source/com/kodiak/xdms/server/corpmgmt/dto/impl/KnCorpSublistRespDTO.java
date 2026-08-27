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

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;

import java.util.Collection;
import java.util.List;


public class KnCorpSublistRespDTO extends KnCorpResponseDTO {


    private KnCorpSublistDTO sublistDTO;
    private Collection<KnCorpContactDTO> memberList;
    private List<String> assignedSubsList;


    public KnCorpSublistDTO getSublistDTO() {
        return sublistDTO;
    }

    public void setSublistDTO(KnCorpSublistDTO sublistDTO) {
        this.sublistDTO = sublistDTO;
    }

    public Collection<KnCorpContactDTO> getMemberList() {
        return memberList;
    }

    public void setMemberList(Collection<KnCorpContactDTO> memberList) {
        this.memberList = memberList;
    }

    public List<String> getAssignedSubsList() {
        return assignedSubsList;
    }

    public void setAssignedSubsList(List<String> assignedSubsList) {
        this.assignedSubsList = assignedSubsList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(400);
        sb.append(super.toString())
                .append(", SublistDto - ").append(sublistDTO)
                .append(", MemberList - ").append(memberList)
                .append(", assignedSubsList - ").append(assignedSubsList);
        return sb.toString();
    }
}
