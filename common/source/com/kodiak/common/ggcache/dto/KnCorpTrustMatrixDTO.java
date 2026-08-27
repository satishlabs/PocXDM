/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

import com.kodiak.common.commdto.common.KnHierarchyMappingInfo;

import java.util.List;

public class KnCorpTrustMatrixDTO {

    private String extCorpId;
    private String sharedExtCorpId;
    private long memFeaturesAllowed;
    private long sharingFeatureAllowed;
    private int typeOfSharingFeatureAllowed;
    private String recId;
    private List<KnHierarchyMappingInfo> hierarchyMappings;

    public String getRecId() { return recId; }

    public void setRecId(String recId) { this.recId = recId; }

    public int getTypeOfSharingFeatureAllowed() {
        return typeOfSharingFeatureAllowed;
    }

    public void setTypeOfSharingFeatureAllowed(int typeOfSharingFeatureAllowed) {
        this.typeOfSharingFeatureAllowed = typeOfSharingFeatureAllowed;
    }

    public String getExtCorpId() { return extCorpId; }

    public void setExtCorpId(String extCorpId) { this.extCorpId = extCorpId; }

    public String getSharedExtCorpId() { return sharedExtCorpId; }

    public void setSharedExtCorpId(String sharedExtCorpId) { this.sharedExtCorpId = sharedExtCorpId; }

    public long getMemFeaturesAllowed() { return memFeaturesAllowed; }

    public void setMemFeaturesAllowed(long memFeaturesAllowed) { this.memFeaturesAllowed = memFeaturesAllowed; }

    public long getSharingFeatureAllowed() {
        return sharingFeatureAllowed;
    }

    public void setSharingFeatureAllowed(long sharingFeatureAllowed) {
        this.sharingFeatureAllowed = sharingFeatureAllowed;
    }

    public List<KnHierarchyMappingInfo> getHierarchyMappings() { return hierarchyMappings; }

    public void setHierarchyMappings(List<KnHierarchyMappingInfo> hierarchyMappings) { this.hierarchyMappings = hierarchyMappings; }

    @Override
    public String toString() {
        return "KnCorpTrustMatrixDTO{" +
                "extCorpId='" + extCorpId + '\'' +
                ", sharedExtCorpId='" + sharedExtCorpId + '\'' +
                ", sharingFeatureAllowed='" + sharingFeatureAllowed + '\'' +
                ", memFeaturesAllowed=" + memFeaturesAllowed +
                ", typeOfSharingFeatureAllowed=" + typeOfSharingFeatureAllowed +
                ", recId='" + recId + '\'' +
                ", hierarchyMappings=" + hierarchyMappings +
                '}';
    }
}
