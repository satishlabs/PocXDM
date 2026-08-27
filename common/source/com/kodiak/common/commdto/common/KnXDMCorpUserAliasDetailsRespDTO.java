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

import java.util.Arrays;
import java.util.Map;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMCorpUserAliasDetailsRespDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 05, 2018                9.0
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
public class KnXDMCorpUserAliasDetailsRespDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622699666L;

    @JsonProperty(value = "userid")
    private String userid;
    @JsonProperty(value = "pwd")
    private String pwd;
    @JsonProperty(value = "temppwd")
    private Boolean temppwd;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "emailVerified")
    private Boolean emailVerified;
    @JsonProperty(value = "attributes")
    private Map<String, String> attributes;
    @JsonProperty(value = "actions")
    private String[] actions;
    @JsonProperty(value = "profiletype")
    private String profiletype;
    @JsonProperty(value = "enabled")
    private Boolean enabled;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Boolean getTemppwd() {
        return temppwd;
    }

    public void setTemppwd(Boolean temppwd) {
        this.temppwd = temppwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String[] getActions() {
        return actions;
    }

    public void setActions(String[] actions) {
        this.actions = actions;
    }

    public String getProfiletype() {
        return profiletype;
    }

    public void setProfiletype(String profiletype) {
        this.profiletype = profiletype;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "KnXDMCorpUserAliasDetailsRespDTO{" +
                "userid='" + KnGDPRTemplate.userId(userid) + '\'' +
                ", pwd='" + pwd + '\'' +
                ", temppwd=" + temppwd +
                ", email='" + KnGDPRTemplate.email(email) + '\'' +
                ", emailVerified=" + emailVerified +
                ", attributes='" + attributes + '\'' +
                ", actions=" + Arrays.toString(actions) +
                ", profiletype='" + profiletype + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
