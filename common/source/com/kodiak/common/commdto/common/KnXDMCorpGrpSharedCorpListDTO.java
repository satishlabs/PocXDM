/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.List;

public class KnXDMCorpGrpSharedCorpListDTO implements IIdentifier {

    private static final long serialVersionUID = 8272529141897281630L;

    private String extCorpID;
    private Integer intCorpID;
    private KnXDMGroupMdnInfoDTO groupMemberProps;
    /** Target hierarchy ID (internal) for hierarchy-scoped sharing. Null for corp-level sharing. */
    private String targetHierarchyId;
    private List<KnXDMHierarchyInfo> hierarchyList;

    public String getExtCorpID() {
        return extCorpID;
    }

    public void setExtCorpID(String extCorpID) {
        this.extCorpID = extCorpID;
    }

    public Integer getIntCorpID() {
        return intCorpID;
    }

    public void setIntCorpID(Integer intCorpID) {
        this.intCorpID = intCorpID;
    }

    public KnXDMGroupMdnInfoDTO getGroupMemberProps() {
        return groupMemberProps;
    }

    public void setGroupMemberProps(KnXDMGroupMdnInfoDTO groupMemberProps) {
        this.groupMemberProps = groupMemberProps;
    }

    public String getTargetHierarchyId() { return targetHierarchyId; }
    public void setTargetHierarchyId(String targetHierarchyId) { this.targetHierarchyId = targetHierarchyId; }

    public List<KnXDMHierarchyInfo> getHierarchyList() { return hierarchyList; }
    public void setHierarchyList(List<KnXDMHierarchyInfo> hierarchyList) { this.hierarchyList = hierarchyList; }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnXDMCorpGrpSharedCorpListDTO{" +
                "extCorpID='" + extCorpID + '\'' +
                ", intCorpID=" + intCorpID +
                ", groupMemberProps=" + groupMemberProps +
                ", targetHierarchyId='" + targetHierarchyId + '\'' +
                ", hierarchyList=" + hierarchyList +
                '}';
    }
}
