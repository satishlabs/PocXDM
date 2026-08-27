/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

public class KnIPCorpGroupProfileDTO extends KnIPCorpInfoDTO {

    private Integer profileId;
    private String profileName;

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        //sb.append(super.toString());
        sb.append(super.toString())
                .append(", profileId - ").append(profileId)
                .append(", profileName - ").append(profileName);
        return sb.toString();
    }
}
