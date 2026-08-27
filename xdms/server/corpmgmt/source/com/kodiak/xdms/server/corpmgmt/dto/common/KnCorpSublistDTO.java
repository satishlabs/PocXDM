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


public class KnCorpSublistDTO extends KnCorpInfoDTO {

    private long eTag;
    private int sublistId;
    private String sublistName;
    private int sublistType;
    private int distributionPolicy = -1;
    private int memberCount;
    private boolean distribution;
    private Integer userProfileListType;
    private Integer listDistribution;
    private String hierarchyId;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public long getETag() {
        return eTag;
    }

    public void setETag(long eTag) {
        this.eTag = eTag;
    }

    public int getSublistId() {
        return sublistId;
    }

    public void setSublistId(int sublistId) {
        this.sublistId = sublistId;
    }

    public String getSublistName() {
        return sublistName;
    }

    public void setSublistName(String sublistName) {
        this.sublistName = sublistName;
    }

    public int getSublistType() {
        return sublistType;
    }

    public void setSublistType(int sublistType) {
        this.sublistType = sublistType;
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

    public Integer getListDistribution() {
        return listDistribution;
    }

    public void setListDistribution(Integer listDistribution) {
        this.listDistribution = listDistribution;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", SublistId - ").append(sublistId)
                .append(", SublistName - ").append(sublistName)
                .append(", SublistType - ").append(sublistType)
                .append(", Dist policy - ").append(distributionPolicy)
                .append(", memberCount - ").append(memberCount)
                .append(", etag - ").append(eTag)
                .append(", distribution - ").append(distribution)
                .append(", userProfileListType - ").append(userProfileListType)
                .append(", listDistribution - ").append(listDistribution);
        return sb.toString();
    }
}
