/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

public class KnUserProfileInfoDTO {

    private String userProfileId;
    private Integer userprofileIndex;
    private Integer tgscMode;

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public Integer getUserprofileIndex() {
        return userprofileIndex;
    }

    public void setUserprofileIndex(Integer userprofileIndex) {
        this.userprofileIndex = userprofileIndex;
    }

    public Integer getTgscMode() {
        return tgscMode;
    }

    public void setTgscMode(Integer tgscMode) {
        this.tgscMode = tgscMode;
    }

    @Override
    public String toString() {
        return "KnUserProfileInfoDTO{" +
                "userProfileId='" + userProfileId + '\'' +
                ", userprofileIndex=" + userprofileIndex +
                ", tgscMode=" + tgscMode +
                '}';
    }
}
