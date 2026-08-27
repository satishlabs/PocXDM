/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMSelectProfileDTO extends KnXDMSubsInfoDTO {
    private static final long serialVersionUID = -1771983879409968303L;

    private String profileMdn;

    public String getProfileMdn() {
        return profileMdn;
    }

    public void setProfileMdn(String profileMdn) {
        this.profileMdn = profileMdn;
    }

    @Override
    public String toString() {
        return "KnXDMSelectProfileDTO{" +
                "profileMdn='" + KnGDPRTemplate.mdn(profileMdn) + '\'' +
                '}';
    }
}
