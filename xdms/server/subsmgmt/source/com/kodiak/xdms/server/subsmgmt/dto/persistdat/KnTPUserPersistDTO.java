/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.List;

public class KnTPUserPersistDTO implements IPersistenceDTO {

	private static final long serialVersionUID = 1406482473057129831L;
	
	private IPersistenceDTO persistenceDTO;
	private String operationType;
	private String entityId;
	private String profile;
	
	private int tpAccountId;
	private int tpUserId;
	private String tpUser;
	private String mdn;
	private long creationTime;
    private List<String> mdnList;
	

	public int getTpAccountId() {
		return tpAccountId;
	}

	public void setTpAccountId(int tpAccountId) {
		this.tpAccountId = tpAccountId;
	}

	public int getTpUserId() {
		return tpUserId;
	}

	public void setTpUserId(int tpUserId) {
		this.tpUserId = tpUserId;
	}

	public String getTpUser() {
		return tpUser;
	}

	public void setTpUser(String tpUser) {
		this.tpUser = tpUser;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
		
	}

	@Override
	public IInputDTO getInputDTO() {
		return null;
	}

	@Override
	public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
		this.persistenceDTO = persistenceDTO;
	}

	@Override
	public IPersistenceDTO getPersistenceDTO() {
		return persistenceDTO;
	}

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    @Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnTPUserPersistDTO [ ")
		.append("tpAccountId-").append(tpAccountId)
		.append(", tpUserId-").append(tpUserId)
		.append(", tpUser-").append(tpUser)
		.append(", mdn-").append(KnGDPRTemplate.mdn(mdn))
		.append(" ]");
		
		return builder.toString();
	}

}
