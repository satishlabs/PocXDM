/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.persistdat;

import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubsProfilePersistDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
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
public class KnSubsProfilePersistDTO extends KnSubsProfileDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676189L;

    IPersistenceDTO persistenceDTO;
    IInputDTO inputDTO;
    String profile;
    String operationType;
    String entityId;


    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String toString(){
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", PersistenceDTO - ").append(persistenceDTO);
        strBuffer.append(", InputDTO - ").append(inputDTO);
        strBuffer.append(", Profile - ").append(profile);
        strBuffer.append(", OperationType - ").append(operationType);
        strBuffer.append(", EntityId - ").append(entityId);

        return strBuffer.toString();
    }
    
}
