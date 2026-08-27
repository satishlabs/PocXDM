/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     24/12/13         7.7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

import java.util.List;

public class KnIPCorpPAMSubsDTO implements IInputDTO {

    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    private int clientType;
    private String profile;
    private String performer;

    private int pamAccId;
    private int unUsedMdnCount;
    private String xdmsHome;
    private List<String> cleanUpMdnLst;
    private String extCorpId;

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getObjectId() {
        return null;
    }
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public int getUnUsedMdnCount() {
        return unUsedMdnCount;
    }

    public void setUnUsedMdnCount(int unUsedMdnCount) {
        this.unUsedMdnCount = unUsedMdnCount;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public List<String> getCleanUpMdnLst() {
        return cleanUpMdnLst;
    }

    public void setCleanUpMdnLst(List<String> cleanUpMdnLst) {
        this.cleanUpMdnLst = cleanUpMdnLst;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append(", pamAccId - ").append(pamAccId);
        sb.append(", unUsedMdnCount - ").append(unUsedMdnCount);
        sb.append(", clientType - ").append(clientType);
        sb.append(", cleanUpMdnLst - ").append(KnGDPRTemplate.mdnList(cleanUpMdnLst));
        sb.append(", extCorpId - ").append(extCorpId);
        return sb.toString();
    }
}
