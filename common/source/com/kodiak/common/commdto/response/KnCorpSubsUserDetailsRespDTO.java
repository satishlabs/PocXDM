/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.commdto.common.KnXDMCorpUserDetailsRespDTO;
import org.springframework.http.HttpStatus;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpSubsUserDetailsRespDTO.java
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
public class KnCorpSubsUserDetailsRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526478855622676150L;

    @JsonProperty(value ="userinfo")
    private KnXDMCorpUserDetailsRespDTO idmSubscriberDTO;
    private int statusCode;
    private int status;

    public KnXDMCorpUserDetailsRespDTO getIdmSubscriberDTO() {
        return idmSubscriberDTO;
    }

    public void setIdmSubscriberDTO(KnXDMCorpUserDetailsRespDTO idmSubscriberDTO) {
        this.idmSubscriberDTO = idmSubscriberDTO;
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
        return "KnCorpSubsUserDetailsRespDTO{" +
                "idmSubscriberDTO=" + idmSubscriberDTO +
                ", statusCode=" + statusCode +
                ", status=" + status +
                '}';
    }
}
