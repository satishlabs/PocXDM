/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * ************************************************************************
 * <p>
 * File name:  KnIDMUserInfo.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Feb 06, 2017                8.3
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

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnIDMUserInfo {

    @JsonProperty(value ="userinfo")
    private KnXDMCorpUserDetailsRespDTO idmSubscriberDTO;

    public KnXDMCorpUserDetailsRespDTO getIdmSubscriberDTO() {
        return idmSubscriberDTO;
    }

    public void setIdmSubscriberDTO(KnXDMCorpUserDetailsRespDTO idmSubscriberDTO) {
        this.idmSubscriberDTO = idmSubscriberDTO;
    }

    @Override
    public String toString() {
        return "KnIDMUserInfo{" +
                "idmSubscriberDTO=" + idmSubscriberDTO +
                '}';
    }
}
