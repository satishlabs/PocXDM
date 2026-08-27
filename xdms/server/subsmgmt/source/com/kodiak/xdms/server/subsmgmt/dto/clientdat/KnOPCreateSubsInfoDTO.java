/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Jan 11, 2011        7.0
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
 * ******************************************************************************
 */

package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.commdto.request.KnXDMSubsAliasDetailsReqDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpProfilePersistDTO;

public class KnOPCreateSubsInfoDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676232L;

    private int corpId;
    private Boolean corpAutoPairing;
    private Boolean isOldCorp;
    private String aliasMdnForAssign;
    private Long lastUpdateprofileTime;
    private String activeFS2;
    private String subsFS2;
    private String mcId;
    private String mcDataId;
    private String mcPttId;
    private String mcVideoId;
    private String networkName;
    private Integer serviceAuthStatus;
    private String internalDeviceId;
    private String extCorpId;
    private String xdmsHome;

    private String corpName;

    public int getIsExistingCorp() {
        return isExistingCorp;
    }

    public void setIsExistingCorp(int isExistingCorp) {
        this.isExistingCorp = isExistingCorp;
    }

    private int isExistingCorp;

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Boolean getIsOldCorp() {
        return isOldCorp;
    }

    public void setIsOldCorp(Boolean isOldCorp) {
        this.isOldCorp = isOldCorp;
    }

    public Boolean getCorpAutoPairing() {
        return corpAutoPairing;
    }

    public void setCorpAutoPairing(Boolean corpAutoPairing) {
        this.corpAutoPairing = corpAutoPairing;
    }

    public String getAliasMdnForAssign() {
        return aliasMdnForAssign;
    }

    public void setAliasMdnForAssign(String aliasMdnForAssign) {
        this.aliasMdnForAssign = aliasMdnForAssign;
    }

    public Long getLastUpdateprofileTime() {
        return lastUpdateprofileTime;
    }

    public void setLastUpdateprofileTime(Long lastUpdateprofileTime) {
        this.lastUpdateprofileTime = lastUpdateprofileTime;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public String getSubsFS2() {
        return subsFS2;
    }

    public void setSubsFS2(String subsFS2) {
        this.subsFS2 = subsFS2;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public String getMcDataId() {
        return mcDataId;
    }

    public void setMcDataId(String mcDataId) {
        this.mcDataId = mcDataId;
    }

    public String getMcPttId() {
        return mcPttId;
    }

    public void setMcPttId(String mcPttId) {
        this.mcPttId = mcPttId;
    }

    public String getMcVideoId() {
        return mcVideoId;
    }

    public void setMcVideoId(String mcVideoId) {
        this.mcVideoId = mcVideoId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public Integer getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(Integer serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String getInternalDeviceId() {
        return internalDeviceId;
    }

    public void setInternalDeviceId(String internalDeviceId) {
        this.internalDeviceId = internalDeviceId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnOPCreateSubsInfoDTO --> ")
                .append(", CORP_ID - ").append(corpId)
                .append(", corpAutoPairing - ").append(corpAutoPairing)
                .append(", isOldCorp - ").append(isOldCorp)
                .append(", aliasMdnForAssign - ").append(aliasMdnForAssign)
                .append(", subsFS2 - ").append(subsFS2)
                .append(", mcId - ").append(mcId)
                .append(", mcDataId - ").append(mcDataId)
                .append(", mcPttId - ").append(mcPttId)
                .append(", mcVideoId - ").append(mcVideoId)
                .append(", networkName - ").append(networkName)
                .append(", serviceAuthStatus - ").append(serviceAuthStatus)
                .append(", extCorpId - ").append(extCorpId)
                .append(", internalDeviceId - ").append(internalDeviceId)
                .append(", corpName - ").append(corpName)
                .append("]");

        return strBuffer.toString();
    }
}
