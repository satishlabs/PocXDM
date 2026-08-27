/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.clientdat;

import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnCommonIpDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair         12/12/14      7.10
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
public class KnCommonIpDTO implements IInputDTO {

    //The entity id
    private String entityId;
    //The operation Type
    private String operationType;
    //The profile
    private String profile;
    //The clientType
    private int clientType;
    //The performer
    private String performer;
    //The Auth DTO
    private IAuthDTO authDTO;

    /**
     * Getter of the entity Id
     * @return String
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Setter of the entity Id
     * @param entityId
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Getter of the operation type
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Setter of the Operation Type
     * @param operationType
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }


    /**
     * Getter of the Profile
     * @return
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Setter of the profile
     * @param profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
