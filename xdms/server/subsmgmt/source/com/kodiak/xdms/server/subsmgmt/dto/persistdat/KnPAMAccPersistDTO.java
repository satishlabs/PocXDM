/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMAccInfoDTO;

/**
 * *********************************************************************
 * File name:   KnPAMAccPersistDTO.java
 * Subsystem:   Subs Prov Lib
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ajit Kumar         92/02/13         7.4
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
 * *************************************************************************
 */
public class KnPAMAccPersistDTO extends KnPAMAccInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622678008L;

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String objectId;
    private String profile;

    /**
     * setter method for the IInput DTO
     *
     * @param inputDTO input dto object
     */
    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    /**
     * getter method for the IInput DTO
     *
     * @return IInputDTO
     */
    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    /**
     * setter method dao DTO
     *
     * @param persistenceDTO IPersistenceDTO
     */
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    /**
     * getter method dao DTO
     *
     * @return IPersistenceDTO
     */
    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    /**
     * setter method for operation Type
     *
     * @param operationType the operation type
     */
    public void setOperationType(String operationType) {
        if (operationType != null) {
            operationType = operationType.trim();
            if (operationType.equals("")) {
                operationType = null;
            }
        }
        this.operationType = operationType;
    }

    /**
     * getter method for operation Type
     *
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * getter method for entity Id
     *
     * @return String
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * setter method for entity Id
     *
     * @param entityId String
     */
    public void setEntityId(String entityId) {
        if (entityId != null) {
            entityId = entityId.trim();
            if (entityId.equals("")) {
                entityId = null;
            }
        }
        this.entityId = entityId;
    }

    /**
     * getter method for the Object Id
     *
     * @return String
     */
    public String getObjectId() {
        return this.getExtPamAccId();
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append("[KnChangeMDNPersistDTO --> ");
        strBuffer.append(", Operation_Type - ").append(operationType)
                .append(", Entity_Id - ").append(entityId)
                .append(", InputDTO - ").append(inputDTO)
                .append("]");

        return strBuffer.toString();
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
