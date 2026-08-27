/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

public class KnIPSelectProfileMdnDTO implements IInputDTO {
    private static final long serialVersionUID = -7295090453532031403L;

    private String performer;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;

    private String mdn;
    private String profileMdn;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getProfileMdn() {
        return profileMdn;
    }

    public void setProfileMdn(String profileMdn) {
        this.profileMdn = profileMdn;
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

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "KnIPSelectProfileMdnDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", profileMdn='" + KnGDPRTemplate.mdn(profileMdn) + '\'' +
                '}';
    }
}
