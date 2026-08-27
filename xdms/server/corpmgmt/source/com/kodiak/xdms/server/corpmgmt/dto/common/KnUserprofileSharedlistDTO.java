/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;
public class KnUserprofileSharedlistDTO {

    private String userprofileId;
    private Integer sharedCorpId;
    private Integer ownerCorpId;

    public KnUserprofileSharedlistDTO(String userprofileId,Integer ownerCorpId, Integer sharedCorpId ) {
        this.userprofileId = userprofileId;
        this.ownerCorpId = ownerCorpId;
        this.sharedCorpId = sharedCorpId;
    }

    public String getUserprofileId() {
        return userprofileId;
    }

    public void setUserprofileId(String userprofileId) {
        this.userprofileId = userprofileId;
    }

    public Integer getSharedCorpId() {
        return sharedCorpId;
    }

    public void setSharedCorpId(Integer sharedCorpId) {
        this.sharedCorpId = sharedCorpId;
    }

    public Integer getOwnerCorpId() {
        return ownerCorpId;
    }

    public void setOwnerCorpId(Integer ownerCorpId) {
        this.ownerCorpId = ownerCorpId;
    }

    @Override
    public String toString() {
        return "KnUserprofileSharedlistDTO{" +
                "userprofileId='" + userprofileId + '\'' +
                ", sharedCorpId=" + sharedCorpId +
                ", ownerCorpId=" + ownerCorpId +
                '}';
    }
}
