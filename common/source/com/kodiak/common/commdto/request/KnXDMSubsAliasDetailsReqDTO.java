/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMCorpRespDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMSubsAliasDetailsRespDTO.java
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
public class KnXDMSubsAliasDetailsReqDTO implements IIdentifier {

    private static final long serialVersionUID = 7526778855622676150L;

    @JsonProperty(value = "userid")
    private String userid;
    @JsonProperty(value = "newuserid")
    private String newuserid;
    @JsonProperty(value = "pwd")
    private String pwd;
    @JsonProperty(value = "temppwd")
    private Boolean temppwd;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "corpId")
    private String corpId;
    @JsonProperty(value = "newemail")
    private String newemail;
    @JsonProperty(value = "attributes")
    private Map<String, Object> attributes;
    @JsonProperty(value = "pwdexpiry")
    private Long pwdexpiry;
    @JsonProperty(value = "generatepwd")
    private Boolean generatepwd;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCorpId() { return corpId; }

    public void setCorpId(String corpId) { this.corpId = corpId; }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Boolean isTemppwd() {
        return temppwd;
    }

    public void setTemppwd(Boolean temppwd) {
        this.temppwd = temppwd;
    }

    public Long getPwdexpiry() {
        return pwdexpiry;
    }

    public void setPwdexpiry(Long pwdexpiry) {
        this.pwdexpiry = pwdexpiry;
    }

    public String getNewuserid() {
        return newuserid;
    }

    public void setNewuserid(String newuserid) {
        this.newuserid = newuserid;
    }

    public String getNewemail() {
        return newemail;
    }

    public void setNewemail(String newemail) {
        this.newemail = newemail;
    }

    public Boolean getGeneratepwd() {
        return generatepwd;
    }

    public void setGeneratepwd(Boolean generatepwd) {
        this.generatepwd = generatepwd;
    }

    @Override
    public String toString() {
        return "KnXDMSubsAliasDetailsRespDTO{" +
                "userid='" + KnGDPRTemplate.userId(userid) + '\'' +
                ", pwd='" + pwd + '\'' +
                ", temppwd=" + temppwd +
                ", email='" + KnGDPRTemplate.email(email) + '\'' +
                ", attributes='" + attributes + '\'' +
                ", newuserid='" + KnGDPRTemplate.userId(newuserid) + '\'' +
                ", newemail='" + KnGDPRTemplate.email(newemail) + '\'' +
                ", pwdexpiry='" + pwdexpiry + '\'' +
                ", generatepwd='" + generatepwd + '\'' +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
