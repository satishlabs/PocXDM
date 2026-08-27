/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupRespDTO.java
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

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class KnCorpGroupListRespDTO extends KnCorpResponseDTO {

    private Collection<KnCorpGroupInfoDTO> groupList;

    private Map<Integer, List<KnCorpGroupMemberDTO>> groupMembers;

    private Map<String, Collection<Integer>> fleetMemGroupIds;

    private int totalGrpCount;

    private String count;

    public Collection<KnCorpGroupInfoDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Collection<KnCorpGroupInfoDTO> groupList) {
        this.groupList = groupList;
    }

    public Map<Integer, List<KnCorpGroupMemberDTO>> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Map<Integer, List<KnCorpGroupMemberDTO>> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Map<String, Collection<Integer>> getFleetMemGroupIds() {
        return fleetMemGroupIds;
    }

    public void setFleetMemGroupIds(Map<String, Collection<Integer>> fleetMemGroupIds) {
        this.fleetMemGroupIds = fleetMemGroupIds;
    }

    public int getTotalGrpCount() { return totalGrpCount; }

    public void setTotalGrpCount(int totalGrpCount) { this.totalGrpCount = totalGrpCount; }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(" groupList - ").append(groupList);
        sb.append(" groupMembers - ").append(groupMembers);
        sb.append(" fleetMemGroupIds - ").append(fleetMemGroupIds);
        sb.append(" totalGrpCount - ").append(totalGrpCount);
        sb.append(" count - ").append(count);
        return sb.toString();
    }
}
