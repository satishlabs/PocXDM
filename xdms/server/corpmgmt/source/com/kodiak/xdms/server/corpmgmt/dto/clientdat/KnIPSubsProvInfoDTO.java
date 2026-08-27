/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

import java.util.Map;

public class KnIPSubsProvInfoDTO implements IInputDTO {

	private static final long serialVersionUID = 987123457786798009L;
	private String performer;
	private IAuthDTO authDTO;
	private int clientType;
	private String operationType;
	private String entityId;
	private String profile;
	private String mdn;
	private String corpId;
	private Map<String, Object> customParamMap;
	private String subFS2;
	private long corpAdminFS2;
	private int vocoderId;
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

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public Map<String, Object> getCustomParamMap() {
		return customParamMap;
	}

	public void setCustomParamMap(Map<String, Object> customParamMap) {
		this.customParamMap = customParamMap;
	}

	public int getVocoderId() {
		return vocoderId;
	}

	public void setVocoderId(int vocoderId) {
		this.vocoderId = vocoderId;
	}

	@Override
	public String getObjectId() {
		return null;
	}
	

	public String getSubFS2() {
		return subFS2;
	}

	public void setSubFS2(String subFS2) {
		this.subFS2 = subFS2;
	}

	public long getCorpAdminFS2() {
		return corpAdminFS2;
	}

	public void setCorpAdminFS2(long corpAdminFS2) {
		this.corpAdminFS2 = corpAdminFS2;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnIPSubsProvInfoDTO [performer=").append(performer).append(", authDTO=").append(authDTO).append(", clientType=")
				.append(clientType).append(", operationType=").append(operationType).append(", entityId=").append(entityId).append(", profile=")
				.append(profile).append(", mdn=").append(KnGDPRTemplate.mdn(mdn)).append(", corpId=").append(corpId).append(", customParamMap=").append(customParamMap)
				.append(", subFS2=").append(subFS2).append(", corpAdminFS2=").append(corpAdminFS2).append(", vocoderId=").append(vocoderId).append("]");
		return builder.toString();
	}

}
