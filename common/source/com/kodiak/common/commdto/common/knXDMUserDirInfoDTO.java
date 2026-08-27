/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

public class knXDMUserDirInfoDTO implements IIdentifier {
    private static final long serialVersionUID = 8212871104950409146L;

    private String userProfileName;
    private Integer userProfileIndex;
    private String userProfileEtag;
    private String mcId;
    private Boolean defaultExtM;

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public Integer getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(Integer userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
    }

    public String getUserProfileEtag() {
        return userProfileEtag;
    }

    public void setUserProfileEtag(String userProfileEtag) {
        this.userProfileEtag = userProfileEtag;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public Boolean getDefaultExtM() {
        return defaultExtM;
    }

    public void setDefaultExtM(Boolean defaultExtM) {
        this.defaultExtM = defaultExtM;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnXDMMCUserDirRespDTO{" +
                "userProfileName='" + userProfileName + '\'' +
                ", userProfileIndex=" + userProfileIndex +
                ", userProfileEtag='" + userProfileEtag + '\'' +
                ", defaultExtM='" + defaultExtM + '\'' +
                ", mcId='" + KnGDPRTemplate.mcId(mcId) + '\'' +
                '}';
    }


}
