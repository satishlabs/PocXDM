/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IOutputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnExtSubscriberDTO;

import java.util.List;

/**
 * Created by Deepak on 8/4/14.
 */
public class KnExtSubscriberInfoDTO implements IInputDTO,IOutputDTO {

    private static final long serialVersionUID = 225095779126405519L;
    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;
    private List<KnExtSubscriberDTO> extSubs;
    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private int DTOStatus;
    private KnXDMError errorObject;

    public KnExtSubscriberInfoDTO() {
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

    public List<KnExtSubscriberDTO> getExtSubs() {
        return extSubs;
    }

    public void setExtSubs(List<KnExtSubscriberDTO> extSubs) {
        this.extSubs = extSubs;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getDTOStatus() {
        return DTOStatus;
    }

    public void setDTOStatus(int DTOStatus) {
        this.DTOStatus = DTOStatus;
    }

    public KnXDMError getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(KnXDMError errorObject) {
        this.errorObject = errorObject;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public void populate(IPopulate dtoObject) {

    }

    @Override
    public String toString() {
        return "KnExtSubscriberInfoDTO{" +
                "performer='" + performer + '\'' +
                ", authDTO=" + authDTO +
                ", clientType=" + clientType +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", profile='" + profile + '\'' +
                ", extSubs=" + extSubs +
                ", responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", DTOStatus=" + DTOStatus +
                ", errorObject=" + errorObject +
                '}';
    }
}
