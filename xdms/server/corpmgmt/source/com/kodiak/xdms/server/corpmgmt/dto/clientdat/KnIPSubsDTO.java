/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

public class KnIPSubsDTO implements IInputDTO {

	private String performer;
	private IAuthDTO authDTO;
	private int clientType;
	private String operationType;
	private String entityId;
	private String profile;

	private String mdn;
	private KnConstants.HIERARCHY_TYPE hierarchyType;

	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
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

	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
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

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getObjectId() {
		return null;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

}
