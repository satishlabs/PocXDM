/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Created by asanjiv on 11/3/15.
 */
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

public class KnReverseContactPersistDto implements IPersistenceDTO {


    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;

    private int subscriberCount;
    private boolean isExternalContact;
    private String mcpttId;
    private int mcpttCompliance;


    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getObjectId() {
        return null;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public boolean isExternalContact() {
        return isExternalContact;
    }

    public void setExternalContact(boolean isExternalContact) {
        this.isExternalContact = isExternalContact;
    }


    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}
	
	

	public int getMcpttCompliance() {
		return mcpttCompliance;
	}

	public void setMcpttCompliance(int mcpttCompliance) {
		this.mcpttCompliance = mcpttCompliance;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("InputDTO - ").append(inputDTO);
        sb.append(", subscriberCount - ").append(subscriberCount);
        sb.append(", isExternalContact - ").append(isExternalContact);
        sb.append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId));
        sb.append(", mcpttCompliance - ").append(mcpttCompliance);
        return sb.toString();
    }
}
