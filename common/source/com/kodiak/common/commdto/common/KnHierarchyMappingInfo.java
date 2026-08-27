/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;

/**
 * Transport DTO for a single hierarchy-pair row in NEW_SHARED_TRUST_MATRIX_HIERARCHY.
 * Carried inside KnCorpTrustMatrixInfo.hierarchyMappings.
 */
public class KnHierarchyMappingInfo implements Serializable {
    private static final long serialVersionUID = 8812345678901234567L;

    private String ownerHierarchyId;
    private String sharedHierarchyId;
    /** UCSPROVCONFIG-3193: hierarchy names for response enrichment */
    private String ownerHierarchyName;
    private String sharedHierarchyName;

    public String getOwnerHierarchyId() { return ownerHierarchyId; }
    public void setOwnerHierarchyId(String ownerHierarchyId) { this.ownerHierarchyId = ownerHierarchyId; }

    public String getSharedHierarchyId() { return sharedHierarchyId; }
    public void setSharedHierarchyId(String sharedHierarchyId) { this.sharedHierarchyId = sharedHierarchyId; }

    public String getOwnerHierarchyName() { return ownerHierarchyName; }
    public void setOwnerHierarchyName(String ownerHierarchyName) { this.ownerHierarchyName = ownerHierarchyName; }

    public String getSharedHierarchyName() { return sharedHierarchyName; }
    public void setSharedHierarchyName(String sharedHierarchyName) { this.sharedHierarchyName = sharedHierarchyName; }

    @Override
    public String toString() {
        return "KnHierarchyMappingInfo{" +
                "ownerHierarchyId='" + ownerHierarchyId + '\'' +
                ", sharedHierarchyId='" + sharedHierarchyId + '\'' +
                ", ownerHierarchyName='" + ownerHierarchyName + '\'' +
                ", sharedHierarchyName='" + sharedHierarchyName + '\'' +
                '}';
    }
}
