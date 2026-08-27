/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnUserProfileAssignedDTO {

    private String userProfileId;
    private int corpId;
    private String mdn;

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    @Override
    public String toString() {
        return "KnUserProfileAssignedDTO{" +
                "userProfileId='" + userProfileId + '\'' +
                ", corpId=" + corpId +
                ", mdn='" + mdn + '\'' +
                '}';
    }
}
