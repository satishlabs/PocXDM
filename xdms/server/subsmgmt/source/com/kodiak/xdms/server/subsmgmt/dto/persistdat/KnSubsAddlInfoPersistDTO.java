/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAddlInfoDTO;

/**
 * Created by hanwar on 24-02-2017.
 */
public class KnSubsAddlInfoPersistDTO extends KnSubsAddlInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622616245L;


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
        setOperationType(inputDTO.getOperationType());
        setEntityId(inputDTO.getEntityId());
        setProfile(inputDTO.getProfile());
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
        return this.getMdn();
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnSubsAddlInfoPersistDTO --> ");
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
