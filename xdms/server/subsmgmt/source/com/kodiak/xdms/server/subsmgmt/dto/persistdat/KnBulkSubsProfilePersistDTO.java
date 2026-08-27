/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnBulkSubsProfilePersistDTO.java
 * Subsystem:   Subscriber Management Lib
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ajit Kumar            Feb 19 2013  7.4
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

package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;

import java.util.List;

public class KnBulkSubsProfilePersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676245L;

    List<String> mdns;
    private long profileCreationTime;
    private long lastProfileUpdateTime;
    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String objectId;

    private String profile;

    KnSubsProfileDTO subsProfile;

    /**
     * getter method for Profile Creation Time
     *
     * @return String
     */
    public long getProfileCreationTime() {
        return profileCreationTime;
    }

    /**
     * setter method for the profile creation Time
     *
     * @param profileCreationTime String
     */
    public void setProfileCreationTime(long profileCreationTime) {
        this.profileCreationTime = profileCreationTime;
    }

    /**
     * getter method for the last Profile Update Time
     *
     * @return String
     */
    public long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    /**
     * setter method for the last Profile Update time
     *
     * @param lastProfileUpdateTime String
     */
    public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

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
        return String.valueOf(this.profileCreationTime);
    }

    public KnSubsProfileDTO getSubsProfile() {
        return subsProfile;
    }

    public void setSubsProfile(KnSubsProfileDTO subsProfile) {
        this.subsProfile = subsProfile;
    }

    public List<String> getMdns() {
        return mdns;
    }

    public void setMdns(List<String> mdns) {
        this.mdns = mdns;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnBulkSubsProfilePersistDTO --> ");
        strBuffer.append(", Profile_Creation_Time - ").append(profileCreationTime)
                .append(", Last_Profile_Update_Time - ").append(lastProfileUpdateTime)
                .append(", Operation_Type - ").append(operationType)
                .append(", Entity_Id - ").append(entityId)
                .append(", InputDTO - ").append(inputDTO)
                   .append(", mdns size - ").append(mdns.size())
                .append(", subsProfile - ").append(subsProfile)
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
