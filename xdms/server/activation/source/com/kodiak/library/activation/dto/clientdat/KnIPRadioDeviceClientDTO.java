/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPClientRegistryDTO.java
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

import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.library.activation.dto.common.KnClientInfoDTO;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;

public class KnIPRadioDeviceClientDTO extends KnClientInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676268L;

    private String performer;
    //stores the operationType
    private String operationType;
    //stores the entityId
    private String entityId;
    //stores the authDTO
    private IAuthDTO authDTO;
    private KnXDMError errorObj;
    private int clientType;

    //stores the mdn
    private String mdn;
    //stores the name
    private String name;
    //stores the service status
    private int serviceStatus;
    //stores the subscriber type
    private int subsType;

    private String userAgent;
    private String realm;
    private String serviceName;
    private String profile;
    private KnUserAgentDTO userAgentDTO;

    private String deviceId;

    private String devicePassword;

    private String deviceClientId;



    private StringBuffer toStringBuffer = new StringBuffer();

    public KnIPRadioDeviceClientDTO() {
    }


    @Override
    public void setPerformer(String performer) {

    }

    @Override
    public String getPerformer() {
        return null;
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        if (userAgent != null) {
            userAgent = userAgent.trim();
            if (userAgent.equals("")) {
                userAgent = null;
            }
        }
        this.userAgent = userAgent;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        if (realm != null) {
            realm = realm.trim();
            if (realm.equals("")) {
                realm = null;
            }
        }
        this.realm = realm;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        if (serviceName != null) {
            serviceName = serviceName.trim();
            if (serviceName.equals("")) {
                serviceName = null;
            }
        }
        this.serviceName = serviceName;
    }

    // returns the error object
    public KnXDMError getErrorObject() {
        return this.errorObj;
    }

    // sets error object
    public void setErrorObject(KnXDMError errorObj) {
        this.errorObj = errorObj;
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

    public KnUserAgentDTO getUserAgentDTO() {
        return userAgentDTO;
    }

    public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
        this.userAgentDTO = userAgentDTO;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevicePassword() {
        return devicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        this.devicePassword = devicePassword;
    }

    public String getDeviceClientId() {
        return deviceClientId;
    }

    public void setDeviceClientId(String deviceClientId) {
        this.deviceClientId = deviceClientId;
    }


    @Override
    public String toString() {
        return super.toString() +
                "KnIPRadioDeviceClientDTO{" +
                "performer='" + performer + '\'' +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", authDTO=" + authDTO +
                ", errorObj=" + errorObj +
                ", clientType=" + clientType +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", name='" + KnGDPRTemplate.name(name) + '\'' +
                ", serviceStatus=" + serviceStatus +
                ", subsType=" + subsType +
                ", userAgent='" + userAgent + '\'' +
                ", realm='" + realm + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", profile='" + profile + '\'' +
                ", userAgentDTO=" + userAgentDTO +
                ", deviceId='" + deviceId + '\'' +
                ", devicePassword='" + devicePassword + '\'' +
                ", deviceClientId='" + deviceClientId + '\'' +
                ", toStringBuffer=" + toStringBuffer +
                '}';
    }
}
