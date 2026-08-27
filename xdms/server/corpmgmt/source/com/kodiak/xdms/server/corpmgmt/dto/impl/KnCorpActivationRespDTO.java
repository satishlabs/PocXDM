/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpActivationRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya      Nov 28, 2011      7.2
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

import com.kodiak.common.commdto.common.KnXDMCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpMailInfoDTO;

import java.util.Collection;
import java.util.Map;

public class KnCorpActivationRespDTO extends KnCorpResponseDTO {

    private String activationCode;
    private KnCorpMailInfoDTO mailInfoDTO;
    private int clientType;
    private int pamAccId;
    private Map<String, Object> customParamMap;
    private String activationTimestamp;
    private String expiryTimestamp;
    private Collection<KnXDMCorpActivationDTO> activationCodeList;
    private String subscrName;
    private int reActivationCount;

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public KnCorpMailInfoDTO getMailInfoDTO() {
        return mailInfoDTO;
    }

    public void setMailInfoDTO(KnCorpMailInfoDTO mailInfoDTO) {
        this.mailInfoDTO = mailInfoDTO;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public Collection<KnXDMCorpActivationDTO> getActivationCodeList() {
        return activationCodeList;
    }

    public void setActivationCodeList(Collection<KnXDMCorpActivationDTO> activationCodeList) {
        this.activationCodeList = activationCodeList;
    }

    public String getActivationTimestamp() {
        return activationTimestamp;
    }

    public void setActivationTimestamp(String activationTimestamp) {
        this.activationTimestamp = activationTimestamp;
    }

    public String getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(String expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    public String getSubscrName() {
        return subscrName;
    }

    public void setSubscrName(String subscrName) {
        this.subscrName = subscrName;
    }

    public int getReActivationCount() {
        return reActivationCount;
    }

    public void setReActivationCount(int reActivationCount) {
        this.reActivationCount = reActivationCount;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("activationCode - ").append(activationCode)
                .append("mailInfoDTO - ").append(mailInfoDTO)
                .append("clientType - ").append(clientType)
                .append("pamAccId - ").append(pamAccId)
                .append("customParamMap - ").append(customParamMap)
                .append("activationCodeList - ").append(activationCodeList)
                .append("reActivationCount - ").append(reActivationCount);
        return sb.toString();
    }
}
