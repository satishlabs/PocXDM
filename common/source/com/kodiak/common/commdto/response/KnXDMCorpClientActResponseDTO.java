/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpClientActResponseDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Sanjiv Acharyya        Nov 28, 2011           7.2
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnMailInfoDTO;
import com.kodiak.common.commdto.common.KnXDMCorpActivationDTO;
import com.kodiak.common.commdto.common.KnCorpDetailsDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.Map;

public class KnXDMCorpClientActResponseDTO extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = 7526471155622776129L;
    private String activationCode;
    private KnMailInfoDTO mailInfoDTO;
    private int clientType;
    private int pAMEmailAccId;
    private Collection<KnXDMCorpActivationDTO> actCodeList;
    private String activationTimestamp;
    private String expiryTimestamp;
    private String subscrName;
    private int reActivationCount;
    private boolean isSyncDisabled;
    private String mdn;
    private Long activeFS;
    private Map<String,Object> responseMap;
    private KnCorpDetailsDTO corpDetailsDTO;
    private Boolean oidcEnabled;
    private String swVersion;
    private String userId;
    private String mcId;
    private String deviceImpi;
    private String deviceImpu;
    private String deviceDigestPwd;
    private int mcpttCompliance;
    private String tempPwd;
    private String mcsLoginUrl;

    public String getMcsLoginUrl() {
        return mcsLoginUrl;
    }

    public void setMcsLoginUrl(String mcsLoginUrl) {
        this.mcsLoginUrl = mcsLoginUrl;
    }

    public String getTempPwd() {
        return tempPwd;
    }

    public void setTempPwd(String tempPwd) {
        this.tempPwd = tempPwd;
    }

    public int getMcpttCompliance() {
        return mcpttCompliance;
    }

    public void setMcpttCompliance(int mcpttCompliance) {
        this.mcpttCompliance = mcpttCompliance;
    }

    public String getUserId() {
        return userId;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceImpi() {
        return deviceImpi;
    }

    public void setDeviceImpi(String deviceImpi) {
        this.deviceImpi = deviceImpi;
    }

    public String getDeviceImpu() {
        return deviceImpu;
    }

    public void setDeviceImpu(String deviceImpu) {
        this.deviceImpu = deviceImpu;
    }

    public String getDeviceDigestPwd() {
        return deviceDigestPwd;
    }

    public void setDeviceDigestPwd(String deviceDigestPwd) {
        this.deviceDigestPwd = deviceDigestPwd;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public KnMailInfoDTO getMailInfoDTO() {
        return mailInfoDTO;
    }

    public void setMailInfoDTO(KnMailInfoDTO mailInfoDTO) {
        this.mailInfoDTO = mailInfoDTO;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getpAMEmailAccId() {
        return pAMEmailAccId;
    }

    public void setpAMEmailAccId(int pAMEmailAccId) {
        this.pAMEmailAccId = pAMEmailAccId;
    }

    public Collection<KnXDMCorpActivationDTO> getActCodeList() {
        return actCodeList;
    }

    public void setActCodeList(Collection<KnXDMCorpActivationDTO> actCodeList) {
        this.actCodeList = actCodeList;
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
    public boolean isSyncDisabled() {
        return isSyncDisabled;
    }

    public void setSyncDisabled(boolean isSyncDisabled) {
        this.isSyncDisabled = isSyncDisabled;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(Long activeFS) {
        this.activeFS = activeFS;
    }

    public Map<String, Object> getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(Map<String, Object> responseMap) {
        this.responseMap = responseMap;
    }

    public KnCorpDetailsDTO getCorpDetailsDTO() {
        return corpDetailsDTO;
    }

    public void setCorpDetailsDTO(KnCorpDetailsDTO corpDetailsDTO) {
        this.corpDetailsDTO = corpDetailsDTO;
    }

    public Boolean getOidcEnabled() {
        return oidcEnabled;
    }

    public void setOidcEnabled(Boolean oidcEnabled) {
        this.oidcEnabled = oidcEnabled;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", activationCode - ").append(activationCode)
                .append(", mailInfoDTO - ").append(mailInfoDTO)
                .append(", pAMEmailAccId - ").append(pAMEmailAccId)
                .append(", clientType - ").append(clientType)
                .append(", isSyncDisabled - ").append(isSyncDisabled)
                .append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", activeFS - ").append(activeFS)
                .append(", corpDetailsDTO - ").append(corpDetailsDTO)
                .append(", responseMap - ").append(responseMap)
                .append(", oidcEnabled - ").append(oidcEnabled)
                .append(", swVersion - ").append(swVersion)
                .append(", userId - ").append(userId)
                .append(", mcId - ").append(mcId)
                .append(", deviceImpi - ").append(deviceImpi)
                .append(", deviceImpu - ").append(deviceImpu)
                .append(", deviceDigestPwd - ").append(deviceDigestPwd)
                .append(", mcpttCompliance - ").append(mcpttCompliance)
                .append(", tempPwd - ").append(tempPwd)
                .append(", mcsLoginUrl - ").append(mcsLoginUrl);
        return sb.toString();

    }
}
