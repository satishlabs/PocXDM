/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnAuthInfoRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 16, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpProfileInfoDTO;

import java.util.Map;


public class KnCorpAuthInfoRespDTO extends KnCorpResponseDTO {

    private KnCorpProfileInfoDTO profileInfoDTO;
    private Map<String, Object> customParamMap;

    public KnCorpProfileInfoDTO getProfileInfoDTO() {
        return profileInfoDTO;
    }

    public void setProfileInfoDTO(KnCorpProfileInfoDTO profileInfoDTO) {
        this.profileInfoDTO = profileInfoDTO;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("CorpProfileInfo - ").append(profileInfoDTO)
        .append("customParamMap - ").append(customParamMap);
        return sb.toString();
    }
}
