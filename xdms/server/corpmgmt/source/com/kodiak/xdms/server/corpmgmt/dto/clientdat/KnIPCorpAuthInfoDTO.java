/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPCorpAuthInfoDTO.java
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAuthInfoDTO;

import java.util.Map;

public class KnIPCorpAuthInfoDTO extends KnCorpAuthInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676185L;

    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    private int clientType;
    private String profile;
    private String performer;
    private Map<String, Object> customParamMap;

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

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
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

    public String getObjectId() {
        return "" + super.getCorpId();
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", AuthDTO - ").append(authDTO)
                .append(", EntityId - ").append(entityId)
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", Profile - ").append(profile)
                .append(", Performer - ").append(performer)
                .append(", CustomParamMap - ").append(customParamMap);
        return sb.toString();
    }
}
