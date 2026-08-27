/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMSublistDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 15, 2011      7.0
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
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.Map;

public class KnXDMCorpSublistDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676128L;

    private String corpId;
    private String sublistId;
    private String sublistName;
    private String sublistType;
    private String eTag;
    private String memberCount;
    private Map<String, Object> customParamMap;
    private Integer userProfileListType;
    private Integer listDistribution;

    public KnXDMCorpSublistDTO() {

    }

    public KnXDMCorpSublistDTO(String corpId, String sublistId, String sublistName, String sublistType) {
        this.corpId = corpId;
        this.sublistId = sublistId;
        this.sublistName = sublistName;
        this.sublistType = sublistType;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getSublistId() {
        return sublistId;
    }

    public void setSublistId(String sublistId) {
        this.sublistId = sublistId;
    }

    public String getSublistName() {
        return sublistName;
    }

    public void setSublistName(String sublistName) {
        this.sublistName = sublistName;
    }

    public String getSublistType() {
        return sublistType;
    }

    public void setSublistType(String sublistType) {
        this.sublistType = sublistType;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getObjectId() {
        return sublistId;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
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

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("CorpId - ").append(corpId)
                .append(", SublistId - ").append(sublistId)
                .append(", SublistName - ").append(sublistName)
                .append(", SublistyType - ").append(sublistType)
                .append(", eTag - ").append(eTag)
                .append(", memberCount - ").append(memberCount)
                .append(", customParammap - ").append(customParamMap)
                .append(", listDistribution - ").append(listDistribution);
        return sb.toString();
    }
}

