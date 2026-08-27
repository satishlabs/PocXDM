/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

public class KnIPMCPTTPermissionDTO implements IInputDTO {

    private static final long serialVersionUID = 8296457337031481810L;

    private String performer;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;

    private String mdn;
    private String targetUser;
    private String mcptt_id;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    @Override
    public void setPerformer(String performer) {
        if (performer != null) {
            performer = performer.trim();
            if (performer.equals("")) {
                performer = null;
            }
        }
        this.performer = performer;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

    }

    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public void setOperationType(String operationType) {
        if (operationType != null) {
            operationType = operationType.trim();
            if (operationType.equals("")) {
                operationType = null;
            }
        }
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
        if (entityId != null) {
            entityId = entityId.trim();
            if (entityId.equals("")) {
                entityId = null;
            }
        }
        this.entityId = entityId;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    public String getMcptt_id() {
		return mcptt_id;
	}

	public void setMcptt_id(String mcptt_id) {
		this.mcptt_id = mcptt_id;
	}

	@Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
	public String toString() {
		return "KnIPMCPTTPermissionDTO [performer=" + performer + ", clientType=" + clientType + ", operationType="
				+ operationType + ", entityId=" + entityId + ", profile=" + profile + ", mdn=" + KnGDPRTemplate.mdn(mdn) + ", targetUser="
				+ targetUser + ", mcptt_id=" + KnGDPRTemplate.mcpttId(mcptt_id) + "]";
	}
}
