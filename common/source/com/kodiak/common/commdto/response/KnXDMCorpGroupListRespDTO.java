/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.common.KnXDMCorpGroupDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupListResponseDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 10, 2011      7.0
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
public class KnXDMCorpGroupListRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776151L;

    private Collection<KnXDMCorpGroupDTO> groupList;
    private String subscrPV;
    private boolean isReqExtContact;
    private Map<Integer, List<KnXDMCorpContactDTO>> fleetMemList;
    private Map<String, Collection<Integer>> fleetMemsGroups;
    private int totalGrpCount;
    private String count;

    public String getSubscrPV() {
        return subscrPV;
    }

    public void setSubscrPV(String subscrPV) {
        this.subscrPV = subscrPV;
    }

    public Collection<KnXDMCorpGroupDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Collection<KnXDMCorpGroupDTO> groupList) {
        this.groupList = groupList;
    }

    public boolean isReqExtContact() {
        return isReqExtContact;
    }

    public void setReqExtContact(boolean isReqExtContact) {
        this.isReqExtContact = isReqExtContact;
    }

    public Map<Integer, List<KnXDMCorpContactDTO>> getFleetMemList() {
        return fleetMemList;
    }

    public void setFleetMemList(Map<Integer, List<KnXDMCorpContactDTO>> fleetMemList) {
        this.fleetMemList = fleetMemList;
    }

    public Map<String, Collection<Integer>> getFleetMemsGroups() {
        return fleetMemsGroups;
    }

    public void setFleetMemsGroups(Map<String, Collection<Integer>> fleetMemsGroups) {
        this.fleetMemsGroups = fleetMemsGroups;
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
        return "KnXDMCorpGroupListRespDTO{" +
                "groupList= " + groupList +
                ", subscrPV= '" + subscrPV + '\'' +
                ", isReqExtContact= " + isReqExtContact +
                ", fleetMemList= " + fleetMemList +
                ", fleetMemsGroups= " + fleetMemsGroups +
                ", totalGrpCount= " + totalGrpCount +
                ", count= " + count +
                '}';
    }
}
