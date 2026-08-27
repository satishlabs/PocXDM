/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupInfoRespDTO.java
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

import com.kodiak.common.commdto.common.KnHierarchyMappingInfo;
import com.kodiak.common.commdto.request.KnCorpOperationStatusMesssageInfoDTO;
import com.kodiak.common.commdto.response.KnXDMCorpOSMInfoRespDTO;
import com.kodiak.common.ggcache.dto.KnCorpGrpLmrExtnDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;

import java.util.List;


public class KnCorpGroupInfoRespDTO extends KnCorpResponseDTO {

    private KnCorpGroupInfoDTO groupInfoDTO;

    private KnCorpOperationStatusMesssageInfoDTO OSMListDetails;

    private List<KnCorpGroupInfoDTO> groupInfoDTOList;

    private KnCorpGrpLmrExtnDTO grpLmrExtnDTO;

    /**
     * Shared hierarchy list for getGroupDetails response (Phase 6).
     * Semantic reuse of KnHierarchyMappingInfo: ownerHierarchyId=targetCorpId (ext), sharedHierarchyId=targetHierarchyId (name).
     */
    private List<KnHierarchyMappingInfo> sharedHierarchyList;

    public KnCorpGroupInfoDTO getGroupInfoDTO() {
        return groupInfoDTO;
    }

    public void setGroupInfoDTO(KnCorpGroupInfoDTO groupInfoDTO) {
        this.groupInfoDTO = groupInfoDTO;
    }

    public KnCorpOperationStatusMesssageInfoDTO getOSMListDetails() {
        return OSMListDetails;
    }

    public void setOSMListDetails(KnCorpOperationStatusMesssageInfoDTO OSMListDetails) {
        this.OSMListDetails = OSMListDetails;
    }

    public List<KnCorpGroupInfoDTO> getGroupInfoDTOList() { return groupInfoDTOList; }

    public void setGroupInfoDTOList(List<KnCorpGroupInfoDTO> groupInfoDTOList) { this.groupInfoDTOList = groupInfoDTOList; }

    public KnCorpGrpLmrExtnDTO getGrpLmrExtnDTO() {
        return grpLmrExtnDTO;
    }

    public void setGrpLmrExtnDTO(KnCorpGrpLmrExtnDTO grpLmrExtnDTO) {
        this.grpLmrExtnDTO = grpLmrExtnDTO;
    }

    public List<KnHierarchyMappingInfo> getSharedHierarchyList() { return sharedHierarchyList; }
    public void setSharedHierarchyList(List<KnHierarchyMappingInfo> sharedHierarchyList) { this.sharedHierarchyList = sharedHierarchyList; }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(400);
        sb.append(super.toString())
                .append(", groupInfoDTO - ").append(groupInfoDTO)
                .append(", OSMListDetails - ").append(OSMListDetails)
                .append(", groupInfoDTOList - ").append(groupInfoDTOList)
                .append(", grpLmrExtnDTO - ").append(grpLmrExtnDTO);
        return sb.toString();
    }
}
