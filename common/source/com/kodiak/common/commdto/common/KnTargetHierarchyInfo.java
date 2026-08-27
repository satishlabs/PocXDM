/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;

/**
 * DTO representing a single target hierarchy entry with ID and name.
 * Used in the hierarchyMap structure of getSharedCorpTrustMatrix response.
 * UCSPROVCONFIG-3193
 */
public class KnTargetHierarchyInfo implements Serializable {
    private static final long serialVersionUID = 6712345678901234501L;

    private String hierarchyId;
    private String hierarchyName;

    public KnTargetHierarchyInfo() {}

    public KnTargetHierarchyInfo(String hierarchyId, String hierarchyName) {
        this.hierarchyId = hierarchyId;
        this.hierarchyName = hierarchyName;
    }

    public String getHierarchyId() { return hierarchyId; }
    public void setHierarchyId(String hierarchyId) { this.hierarchyId = hierarchyId; }

    public String getHierarchyName() { return hierarchyName; }
    public void setHierarchyName(String hierarchyName) { this.hierarchyName = hierarchyName; }

    @Override
    public String toString() {
        return "KnTargetHierarchyInfo{" +
                "hierarchyId='" + hierarchyId + '\'' +
                ", hierarchyName='" + hierarchyName + '\'' +
                '}';
    }
}

