/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnSubsProfilePersistDTO.java
 * Subsystem:   Subscriber Management Lib
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       12/30/10   7.0
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

package com.kodiak.xdms.server.bulkops.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

public class KnSubsProfilePersistDTO extends KnSubsProfileDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676245L;

    private long profileCreationTime;
    private long lastProfileUpdateTime;


    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String objectId;

    private String profile;

    private boolean flagForPrivacy;
    private Integer privacyExecutorBasedonFlag;
    private Integer isAffiliationEnabled;
    private String profileMdn;
    private String profileMdnMcpttId;
    private String baseMdnMcpttId;

    private Integer previousServiceAuthStatusFromDB;

    private Integer previousServiceAuthStatusToStore;

    public Integer getIsAffiliationEnabled() {
		return isAffiliationEnabled;
	}

	public void setIsAffiliationEnabled(Integer isAffiliationEnabled) {
		this.isAffiliationEnabled = isAffiliationEnabled;
	}

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

    public Integer getPreviousServiceAuthStatusFromDB() {
        return previousServiceAuthStatusFromDB;
    }

    public void setPreviousServiceAuthStatusFromDB(Integer previousServiceAuthStatusFromDB) {
        this.previousServiceAuthStatusFromDB = previousServiceAuthStatusFromDB;
    }

    public Integer getPreviousServiceAuthStatusToStore() {
        return previousServiceAuthStatusToStore;
    }

    public void setPreviousServiceAuthStatusToStore(Integer previousServiceAuthStatusToStore) {
        this.previousServiceAuthStatusToStore = previousServiceAuthStatusToStore;
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
        return this.getMdn();
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnSubsProfilePersistDTO --> ");
        strBuffer.append(", Profile_Creation_Time - ").append(profileCreationTime)
                .append(", Last_Profile_Update_Time - ").append(lastProfileUpdateTime)

                .append(", Operation_Type - ").append(operationType)
                .append(", Entity_Id - ").append(entityId)
                .append(", InputDTO - ").append(inputDTO)
                .append(", profileMdn - ").append(KnGDPRTemplate.mdn(profileMdn))
                .append(", profileMdnMcpttId - ").append(KnGDPRTemplate.mcpttId(profileMdnMcpttId))
                .append(", baseMdnMcpttId - ").append(KnGDPRTemplate.mcpttId(baseMdnMcpttId))
                .append(", isAffiliationEnabled - ").append(isAffiliationEnabled)
                .append(", previousServiceAuthStatusFromDB - ").append(previousServiceAuthStatusFromDB)
                .append(", previousServiceAuthStatusToStore - ").append(previousServiceAuthStatusToStore).append("]");
     

        return strBuffer.toString();
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public boolean isFlagForPrivacy() {
        return flagForPrivacy;
    }

    public void setFlagForPrivacy(boolean flagForPrivacy) {
        this.flagForPrivacy = flagForPrivacy;
    }
    public Integer getPrivacyExecutorBasedonFlag() {
        return privacyExecutorBasedonFlag;
    }

    public void setPrivacyExecutorBasedonFlag(Integer privacyExecutorBasedonFlag) {
        this.privacyExecutorBasedonFlag = privacyExecutorBasedonFlag;
    }

    public String getProfileMdn() {
        return profileMdn;
    }

    public void setProfileMdn(String profileMdn) {
        this.profileMdn = profileMdn;
    }

    public String getProfileMdnMcpttId() {
        return profileMdnMcpttId;
    }

    public void setProfileMdnMcpttId(String profileMdnMcpttId) {
        this.profileMdnMcpttId = profileMdnMcpttId;
    }

    public String getBaseMdnMcpttId() {
        return baseMdnMcpttId;
    }

    public void setBaseMdnMcpttId(String baseMdnMcpttId) {
        this.baseMdnMcpttId = baseMdnMcpttId;
    }
}
