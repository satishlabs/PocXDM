/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.commdto.common.KnXDMHierarchyInfo;

import java.util.List;

public class KnCorpSharedCorpInfo {

    private Integer corpId;
    //Ext-CorpId for the corporate Profile which is referred by the CAT tool
    private String extCorpId;

    private int ownerCorpId;

    private KnCorpGroupContactDTO grpMemProps;

    private Long memFeaturesAllowed;

    private int corpGroupId;

    /** Internal hierarchy ID of the owner group's hierarchy (populated before validation in createGroup/modifyGroup). */
    private String sourceHierarchyId;

    /** Internal hierarchy ID of the target corp's hierarchy scope (resolved from name before validation). */
    private String targetHierarchyId;

    /**
     * Hierarchy list for group sharing — carries multiple (ownerHierarchyId, sharedHierarchyId, sharedHierarchyName)
     * pairs as received from CAT Admin. sharedHierarchyId is always present and used directly.
     * ownerHierarchyId is used in trust matrix pair validation (TC-TML-020): the controller injects
     * sourceHierarchyId from the group's hierarchy context, which the validator uses as the effective
     * ownerHierarchyId to look up allowed sharedHierarchyIds in NEW_SHARED_TRUST_MATRIX_HIERARCHY.
     */
    private List<KnXDMHierarchyInfo> hierarchyList;

    public Integer getCorpId() { return corpId; }

    public void setCorpId(Integer corpId) { this.corpId = corpId; }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public KnCorpGroupContactDTO getGrpMemProps() {
        return grpMemProps;
    }

    public void setGrpMemProps(KnCorpGroupContactDTO grpMemProps) {
        this.grpMemProps = grpMemProps;
    }

    public Long getMemFeaturesAllowed() { return memFeaturesAllowed; }

    public void setMemFeaturesAllowed(Long memFeaturesAllowed) {  this.memFeaturesAllowed = memFeaturesAllowed; }

    public int getOwnerCorpId() { return ownerCorpId; }

    public void setOwnerCorpId(int ownerCorpId) { this.ownerCorpId = ownerCorpId; }

    public int getCorpGroupId() {
        return corpGroupId;
    }

    public void setCorpGroupId(int corpGroupId) {
        this.corpGroupId = corpGroupId;
    }

    public String getSourceHierarchyId() { return sourceHierarchyId; }
    public void setSourceHierarchyId(String sourceHierarchyId) { this.sourceHierarchyId = sourceHierarchyId; }

    public String getTargetHierarchyId() { return targetHierarchyId; }
    public void setTargetHierarchyId(String targetHierarchyId) { this.targetHierarchyId = targetHierarchyId; }

    public List<KnXDMHierarchyInfo> getHierarchyList() { return hierarchyList; }
    public void setHierarchyList(List<KnXDMHierarchyInfo> hierarchyList) { this.hierarchyList = hierarchyList; }

    @Override
    public String toString() {
        return "KnCorpSharedCorpInfo{" +
                "corpId=" + corpId +
                ", extCorpId='" + extCorpId + '\'' +
                ", ownerCorpId=" + ownerCorpId +
                ", grpMemProps=" + grpMemProps +
                ", corpGroupId=" + corpGroupId +
                ", memFeaturesAllowed=" + memFeaturesAllowed +
                ", sourceHierarchyId='" + sourceHierarchyId + '\'' +
                ", targetHierarchyId='" + targetHierarchyId + '\'' +
                ", hierarchyList=" + hierarchyList +
                '}';
    }
}
