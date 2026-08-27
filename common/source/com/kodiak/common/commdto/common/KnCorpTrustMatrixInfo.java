/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;
import java.util.List;

public class KnCorpTrustMatrixInfo implements Serializable {

    private static final long serialVersionUID = 1122334455667788L;

    private String extCorpId;
    private String sharedExtCorpId;
    private long memFeaturesAllowed;
    private long sharingFeatureAllowed;
    private String recId;
    private List<KnHierarchyMappingInfo> hierarchyMappings;
    /** UCSPROVCONFIG-3193: Grouped hierarchy map — sourceHieracy → targetHierachylist[] */
    private List<KnSourceHierarchyMapInfo> hierarchyMap;

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getSharedExtCorpId() {
        return sharedExtCorpId;
    }

    public void setSharedExtCorpId(String sharedExtCorpId) {
        this.sharedExtCorpId = sharedExtCorpId;
    }

    public long getMemFeaturesAllowed() {
        return memFeaturesAllowed;
    }

    public void setMemFeaturesAllowed(long memFeaturesAllowed) {
        this.memFeaturesAllowed = memFeaturesAllowed;
    }

    public long getSharingFeatureAllowed() {
        return sharingFeatureAllowed;
    }

    public void setSharingFeatureAllowed(long sharingFeatureAllowed) {
        this.sharingFeatureAllowed = sharingFeatureAllowed;
    }

    public String getRecId() {
        return recId;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    public List<KnHierarchyMappingInfo> getHierarchyMappings() {
        return hierarchyMappings;
    }

    public void setHierarchyMappings(List<KnHierarchyMappingInfo> hierarchyMappings) {
        this.hierarchyMappings = hierarchyMappings;
    }

    public List<KnSourceHierarchyMapInfo> getHierarchyMap() { return hierarchyMap; }
    public void setHierarchyMap(List<KnSourceHierarchyMapInfo> hierarchyMap) { this.hierarchyMap = hierarchyMap; }

    @Override
    public String toString() {
        return "KnCorpTrustMatrixInfo{" +
                "extCorpId='" + extCorpId + '\'' +
                ", sharedExtCorpId='" + sharedExtCorpId + '\'' +
                ", memFeaturesAllowed=" + memFeaturesAllowed +
                ", sharingFeatureAllowed=" + sharingFeatureAllowed +
                ", recId='" + recId + '\'' +
                ", hierarchyMappings=" + hierarchyMappings +
                ", hierarchyMap=" + hierarchyMap +
                '}';
    }
}
