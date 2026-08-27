/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

public class KnIPSubClientSettingsDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676188L;


    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private String objectId;
    private String profile;
    private String mdn;
    private String recordingStatus;

    private String corpid;

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(String recordingStatus) {
        this.recordingStatus = recordingStatus;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }



    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType=operationType;
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
        this.entityId=entityId;
    }

    @Override
    public void setPerformer(String performer) {
        this.performer=performer;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO=authDTO;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType=clientType;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile=profile;
    }

    @Override
    public String toString() {
        return "KnSubClientSettings{" +
                "performer='" + performer + '\'' +
                ", authDTO=" + authDTO +
                ", clientType=" + clientType +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", profile='" + profile + '\'' +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", recordingStatus='" + recordingStatus + '\'' +
                ", corpid='" + corpid + '\'' +
                '}';
    }


}
