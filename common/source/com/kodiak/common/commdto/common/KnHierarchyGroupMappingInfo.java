/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Request DTO for one grouped hierarchy mapping in updateTrustMatrix.
 * One ownerHierarchyName maps to multiple sharedHierarchyName entries.
 * Each sharedHierarchyName element results in one row in NEW_SHARED_TRUST_MATRIX_HIERARCHY.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KnHierarchyGroupMappingInfo implements Serializable {

    private static final long serialVersionUID = 7712345678901234567L;

    @JsonProperty(value = "ownerHierarchyName")
    private String ownerHierarchyName;

    @JsonProperty(value = "sharedHierarchyName")
    private List<String> sharedHierarchyName;

    public String getOwnerHierarchyName() { return ownerHierarchyName; }
    public void setOwnerHierarchyName(String ownerHierarchyName) { this.ownerHierarchyName = ownerHierarchyName; }

    public List<String> getSharedHierarchyName() { return sharedHierarchyName; }
    public void setSharedHierarchyName(List<String> sharedHierarchyName) { this.sharedHierarchyName = sharedHierarchyName; }

    @Override
    public String toString() {
        return "KnHierarchyGroupMappingInfo{" +
                "ownerHierarchyName='" + ownerHierarchyName + '\'' +
                ", sharedHierarchyName=" + sharedHierarchyName +
                '}';
    }
}
