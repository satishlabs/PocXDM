/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 19, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.common;


public class KnCorpUserProfilelistDTO extends KnCorpInfoDTO {

    private long eTag;
    private int userProfileId;
    private String userProfileName;
    private int userProfileType;
    private int distributionPolicy = -1;
    private int memberCount;
    private boolean distribution;
    private Integer userProfileListType;

    public long geteTag() {
        return eTag;
    }

    public void seteTag(long eTag) {
        this.eTag = eTag;
    }

    public int getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(int userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public int getUserProfileType() {
        return userProfileType;
    }

    public void setUserProfileType(int userProfileType) {
        this.userProfileType = userProfileType;
    }

    public int getDistributionPolicy() {
        return distributionPolicy;
    }

    public void setDistributionPolicy(int distributionPolicy) {
        this.distributionPolicy = distributionPolicy;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public boolean isDistribution() {
        return distribution;
    }

    public void setDistribution(boolean distribution) {
        this.distribution = distribution;
    }

    public Integer getUserProfileListType() {
        return userProfileListType;
    }

    public void setUserProfileListType(Integer userProfileListType) {
        this.userProfileListType = userProfileListType;
    }
}
