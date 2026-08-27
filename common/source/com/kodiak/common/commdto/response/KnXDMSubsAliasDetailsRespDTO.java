/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.resources.KnGDPRTemplate;
import org.springframework.http.HttpStatus;

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
public class KnXDMSubsAliasDetailsRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526778855622676150L;

    @JsonProperty(value = "userid")
    private String userid;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "accstate")
    private String accstate;
    @JsonProperty(value = "attributes")
    private Map<String, String> attributes;

    private int statusCode;
    private int status;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccstate() {
        return accstate;
    }

    public void setAccstate(String accstate) {
        this.accstate = accstate;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "KnXDMSubsAliasDetailsRespDTO{" +
                "userid='" + KnGDPRTemplate.userId(userid) + '\'' +
                ", email='" + KnGDPRTemplate.email(email) + '\'' +
                ", attributes='" + attributes + '\'' +
                ", statusCode=" + statusCode +
                ", status=" + status +
                '}';
    }
}
