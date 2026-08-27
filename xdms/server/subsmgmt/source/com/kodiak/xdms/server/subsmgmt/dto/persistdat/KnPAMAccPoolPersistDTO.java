/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

import java.util.List;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMAccPoolUsageDTO;

public class KnPAMAccPoolPersistDTO implements IPersistenceDTO{
	
	private IInputDTO inputDTO;
	private IPersistenceDTO persistenceDTO;
	private String operationType;
	private String entityId;
	private String profile;
	private List<String> mdns;
	private KnPAMAccPoolUsageDTO pamAccPoolUsage;

	public List<String> getMdns() {
		return mdns;
	}

	public void setMdns(List<String> mdns) {
		this.mdns = mdns;
	}

	public KnPAMAccPoolUsageDTO getPamAccPoolUsage() {
		return pamAccPoolUsage;
	}

	public void setPamAccPoolUsage(KnPAMAccPoolUsageDTO pamAccPoolUsage) {
		this.pamAccPoolUsage = pamAccPoolUsage;
	}

	@Override
	public void setOperationType(String operationType) {
		this.operationType = operationType;		
	}

	@Override
	public String getOperationType() {
		return operationType;
	}

	@Override
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	public String getProfile() {
		return profile;
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile;
		
	}

	@Override
	public String getObjectId() {
		return null;
	}

	@Override
	public void setInputDTO(IInputDTO inputDTO) {
		this.inputDTO = inputDTO;
	}

	@Override
	public IInputDTO getInputDTO() {
		return inputDTO;
	}

	@Override
	public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
		this.persistenceDTO = persistenceDTO;
	}

	@Override
	public IPersistenceDTO getPersistenceDTO() {
		return persistenceDTO;
	}
	
	@Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.mdns != null) {
			builder.append("KnPAMAccPoolPersistDTO [ ")
			.append("MDN size-").append(this.mdns.size())
			.append(" ]");
		}
		
		return builder.toString();
	}
}