/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPUserDTO.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
 * ************************************************************************
 */

package com.kodiak.library.activation.dto.clientdat;

import com.kodiak.library.activation.dto.common.KnClientInfoDTO;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;


public class KnIPUserDTO extends KnClientInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676269L;
    //stores the performer
    private String performer;
    //stores the operationType
    private String operationType;
    //stores the entityId
    private String entityId;
    //stores the profile
    private String profile;
    //stores dto status
    private int dtoStatus;
    //stores error object
    private KnXDMError errorObj;
    //stores the authDTO
    private IAuthDTO authDTO;
    //stores the object id
    private String objectId;
    //stores the client type
    private int clientType;
    //stores the message id
    private int messageId;
    //stores the feature id
    private String featureId;
    //stores the clientId
    private String clientId;
    //stores the mdn
    private String mdn;
    //stores the name
    private String name;
    //stores the subscriber type
    private int subsType;
    //stores the service status
    private int serviceStatus;

    private StringBuffer toStringBuffer = new StringBuffer();

    /**
     * sets performer
     *
     * @param performer
     */
    public void setPerformer(String performer) {
        if (performer != null) {
            performer = performer.trim();
            if (performer.equals("")) {
                performer = null;
            }
        }
        this.performer = performer;
    }

    /**
     * returns the performer
     *
     * @return performer
     */
    public String getPerformer() {
        return this.performer;
    }

    /**
     * sets the Auth DTO
     *
     * @param authDTO
     */
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    /**
     * retuns the authDTO
     *
     * @return returns authDTO
     */
    public IAuthDTO getAuthDTO() {
        return this.authDTO;
    }

    /**
     * sets the operation type to be executed.
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
     * returns the operation type set in the dto
     *
     * @return the operation type
     */
    public String getOperationType() {
        return this.operationType;
    }

    /**
     * returns the ID of the entity.
     *
     * @return the entity id
     */
    public String getEntityId() {
        return this.entityId;
    }

    /**
     * sets the ID of the entity.
     *
     * @param entityId
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

    public void validate() throws KnValidationException {
        // As of now we assume there would be no data validations here.
    }

    /**
     * This method will return the profile id
     *
     * @return the profile
     */
    public String getProfile() {
        return this.profile;
    }

    /**
     * This method will set the profile
     *
     * @param profile
     */
    public void setProfile(String profile) {
        if (profile != null) {
            profile = profile.trim();
            if (profile.equals("")) {
                profile = null;
            }
        }
        this.profile = profile;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setClientId(String clientId) {
        if (clientId != null) {
            clientId = clientId.trim();
            if (clientId.equals("")) {
                clientId = null;
            }
        }
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public int getClientType() {
        return this.clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    // returns the status of the DTO object
    public int getDTOStatus() {
        return this.dtoStatus;
    }

    // sets the status to tht DTO
    public void setDTOStatus(int dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    // returns the error object
    public KnXDMError getErrorObject() {
        return this.errorObj;
    }

    // sets error object
    public void setErrorObject(KnXDMError errorObj) {
        this.errorObj = errorObj;
    }

    public void populate(IPopulate dtoObject) {
        if (dtoObject instanceof KnIPUserDTO) {
            KnIPUserDTO ipUserDTO = (KnIPUserDTO) dtoObject;
            clientId = ipUserDTO.getClientId();
            mdn = ipUserDTO.getMdn();
            name = ipUserDTO.getName();
            serviceStatus = ipUserDTO.getServiceAuthStatus();
            subsType = ipUserDTO.getSubscriberType();
        }
    }

    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    public String getMdn() {
        return this.mdn;
    }

    public void setName(String name) {
        if (name != null) {
            name = name.trim();
            if (name.equals("")) {
                name = null;
            }
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSubscriberType(int subType) {
        this.subsType = subType;
    }

    public int getSubscriberType() {
        return this.subsType;
    }

    public void setServiceAuthStatus(int serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public int getServiceAuthStatus() {
        return this.serviceStatus;
    }

    public String toString() {
        toStringBuffer.setLength(0);
        toStringBuffer.append(super.toString());
        return toStringBuffer.toString();
    }
}
