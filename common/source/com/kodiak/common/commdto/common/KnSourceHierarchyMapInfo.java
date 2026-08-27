/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;
import java.util.List;

/**
 * DTO representing a source hierarchy and its associated target hierarchies.
 * Used in the hierarchyMap structure of getSharedCorpTrustMatrix response.
 * UCSPROVCONFIG-3193
 */
public class KnSourceHierarchyMapInfo implements Serializable {
    private static final long serialVersionUID = 6712345678901234502L;

    private String sourceHieracy;
    private List<KnTargetHierarchyInfo> targetHierachylist;

    public String getSourceHieracy() { return sourceHieracy; }
    public void setSourceHieracy(String sourceHieracy) { this.sourceHieracy = sourceHieracy; }

    public List<KnTargetHierarchyInfo> getTargetHierachylist() { return targetHierachylist; }
    public void setTargetHierachylist(List<KnTargetHierarchyInfo> targetHierachylist) { this.targetHierachylist = targetHierachylist; }

    @Override
    public String toString() {
        return "KnSourceHierarchyMapInfo{" +
                "sourceHieracy='" + sourceHieracy + '\'' +
                ", targetHierachylist=" + targetHierachylist +
                '}';
    }
}

