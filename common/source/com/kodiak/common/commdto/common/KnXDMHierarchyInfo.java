package com.kodiak.common.commdto.common;

import java.io.Serializable;

public class KnXDMHierarchyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ownerHierarchyId;
    private String sharedHierarchyId;
    private String sharedHierarchyName;

    public String getOwnerHierarchyId() { return ownerHierarchyId; }
    public void setOwnerHierarchyId(String ownerHierarchyId) { this.ownerHierarchyId = ownerHierarchyId; }

    public String getSharedHierarchyId() { return sharedHierarchyId; }
    public void setSharedHierarchyId(String sharedHierarchyId) { this.sharedHierarchyId = sharedHierarchyId; }

    public String getSharedHierarchyName() { return sharedHierarchyName; }
    public void setSharedHierarchyName(String sharedHierarchyName) { this.sharedHierarchyName = sharedHierarchyName; }

    @Override
    public String toString() {
        return "KnXDMHierarchyInfo{" +
                "ownerHierarchyId='" + ownerHierarchyId + '\'' +
                ", sharedHierarchyId='" + sharedHierarchyId + '\'' +
                ", sharedHierarchyName='" + sharedHierarchyName + '\'' +
                '}';
    }
}
