/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMCorpUserDetailsRespDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Feb 22, 2017                8.3
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnXDMCorpUserDetailsRespDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676666L;

    @JsonProperty(value ="userid")
    private String userId;
    @JsonProperty(value ="usertype")
    private String userType;
    @JsonProperty(value ="mdn")
    private String mdn;
    @JsonProperty(value ="corpid")
    private String corpId;
    @JsonProperty(value ="corpname")
    private String corpName;
    @JsonProperty(value ="assigned-roles")
    private Collection<KnXDMAssignedRole> assignedRoles;
    @JsonProperty(value ="acc-state")
    private String accState;
    @JsonProperty(value ="lang")
    private String lang;
    @JsonProperty(value ="span-of-ctrl")
    private Collection<KnXDMSpanOfControl> spanOfCtrl;
    @JsonProperty(value ="span-type")
    private String spanType;
    @JsonProperty(value ="act-code")
    private String actCode;
    @JsonProperty(value ="creation-date")
    private String creationDate;
    @JsonProperty(value ="email")
    private String email;
    private String aliasMdn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public Collection<KnXDMAssignedRole> getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(Collection<KnXDMAssignedRole> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }

    public String getAccState() {
        return accState;
    }

    public void setAccState(String accState) {
        this.accState = accState;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Collection<KnXDMSpanOfControl> getSpanOfCtrl() {
        return spanOfCtrl;
    }

    public void setSpanOfCtrl(Collection<KnXDMSpanOfControl> spanOfCtrl) {
        this.spanOfCtrl = spanOfCtrl;
    }

    public String getSpanType() {
        return spanType;
    }

    public void setSpanType(String spanType) {
        this.spanType = spanType;
    }

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    @Override
    public String toString() {
        return "KnXDMCorpUserDetailsRespDTO{" +
                "userId='" + KnGDPRTemplate.userId(userId) + '\'' +
                ", userType='" + userType + '\'' +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", corpId='" + corpId + '\'' +
                ", corpName='" + corpName + '\'' +
                ", assignedRoles='" + assignedRoles + '\'' +
                ", accState='" + accState + '\'' +
                ", lang='" + lang + '\'' +
                ", spanOfCtrl=" + spanOfCtrl +
                ", spanType='" + spanType + '\'' +
                ", actCode='" + actCode + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", email='" + KnGDPRTemplate.email(email) + '\'' +
                ", aliasMdn='" + KnGDPRTemplate.mdn(aliasMdn) + '\'' +
                '}';
    }

    @Override
    public String getObjectId() {
        return creationDate;
    }
}
