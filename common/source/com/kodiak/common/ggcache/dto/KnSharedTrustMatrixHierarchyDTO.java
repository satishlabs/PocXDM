/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

/**
 * DTO for a row in NEW_SHARED_TRUST_MATRIX_HIERARCHY.
 * MEM_FEATURES_ALLOWED and SHARING_FEATURE_ALLOWED are nullable — NULL means inherit from parent.
 */
public class KnSharedTrustMatrixHierarchyDTO {

    private String recId;
    private String ownerHierarchyId;
    private String sharedHierarchyId;
    private Long memFeaturesAllowed;
    private Long sharingFeatureAllowed;

    public String getRecId() { return recId; }
    public void setRecId(String recId) { this.recId = recId; }

    public String getOwnerHierarchyId() { return ownerHierarchyId; }
    public void setOwnerHierarchyId(String ownerHierarchyId) { this.ownerHierarchyId = ownerHierarchyId; }

    public String getSharedHierarchyId() { return sharedHierarchyId; }
    public void setSharedHierarchyId(String sharedHierarchyId) { this.sharedHierarchyId = sharedHierarchyId; }

    public Long getMemFeaturesAllowed() { return memFeaturesAllowed; }
    public void setMemFeaturesAllowed(Long memFeaturesAllowed) { this.memFeaturesAllowed = memFeaturesAllowed; }

    public Long getSharingFeatureAllowed() { return sharingFeatureAllowed; }
    public void setSharingFeatureAllowed(Long sharingFeatureAllowed) { this.sharingFeatureAllowed = sharingFeatureAllowed; }

    @Override
    public String toString() {
        return "KnSharedTrustMatrixHierarchyDTO{" +
                "recId='" + recId + '\'' +
                ", ownerHierarchyId='" + ownerHierarchyId + '\'' +
                ", sharedHierarchyId='" + sharedHierarchyId + '\'' +
                ", memFeaturesAllowed=" + memFeaturesAllowed +
                ", sharingFeatureAllowed=" + sharingFeatureAllowed +
                '}';
    }
}
