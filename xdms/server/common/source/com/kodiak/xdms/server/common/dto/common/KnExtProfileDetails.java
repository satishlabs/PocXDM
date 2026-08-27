/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     11/4/14         7.7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.common.dto.common;

import java.util.Map;

/**
 * This DTO is to hold the profile details from DG.ExtSubscrProfileInfo table.
 */
public class KnExtProfileDetails {
    private int profileId;
    private int profileType;
    private boolean defaultProfile;
    private String profileName;
    private Map<Integer, Boolean> featureSetMap;

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getProfileType() {
        return profileType;
    }

    public void setProfileType(int profileType) {
        this.profileType = profileType;
    }

    public boolean isDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Map<Integer, Boolean> getFeatureSetMap() {
        return featureSetMap;
    }

    public void setFeatureSetMap(Map<Integer, Boolean> featureSetMap) {
        this.featureSetMap = featureSetMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(" profileId - ").append(profileId);
        sb.append(" profileType - ").append(profileType);
        sb.append(" profileName - ").append(profileName);
        sb.append(" defaultProfile - ").append(defaultProfile);
        sb.append(" featureSetMap - ").append(featureSetMap);
        return sb.toString();
    }
}
