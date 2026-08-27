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
import com.kodiak.library.activation.dto.common.KnClientInfoDTO;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.KnXDMError;
import java.util.Map;

public class KnIPClientRegistryDTO extends KnClientInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676268L;

    //stores the performer
    private String performer;
    //stores the operationType
    private String operationType;
    //stores the entityId
    private String entityId;
    //stores the profile
    private String profile;
    //stores the authDTO
    private IAuthDTO authDTO;
    private String objectId;
    private int dtoStatus;
    private KnXDMError errorObj;
    private int clientType;
    private String featureId;

    //stores the clientId
    private String clientId;
    //stores the mdn
    private String mdn;
    //stores the name
    private String name;
    //stores the service status
    private int serviceStatus;
    //stores the subscriber type
    private int subsType;

    private String activationKey;
    private String authKey;
    private String imeiNum;
    private String userAgent;
    private String realm;
    private String serviceName;
    private String clientCapabilitySet;
    private Integer subsClientType;
    private KnUserAgentDTO userAgentDTO;
    private String apnName;
    private String swType;
    private String platformType;
    private Map<Integer,Integer> vocoderIdMap;

    private StringBuffer toStringBuffer = new StringBuffer();

    public KnIPClientRegistryDTO() {
    }

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

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        if (activationKey != null) {
            activationKey = activationKey.trim();
            if (activationKey.equals("")) {
                activationKey = null;
            }
        }
        this.activationKey = activationKey;
    }

    /**
     * Get the 'imeiNum' element value.
     *
     * @return value
     */
    public String getImeiNum() {
        return imeiNum;
    }

    /**
     * Set the 'imeiNum' element value.
     *
     * @param imeiNum
     */
    public void setImeiNum(String imeiNum) {
        if (imeiNum != null) {
            imeiNum = imeiNum.trim();
            if (imeiNum.equals("")) {
                imeiNum = null;
            }
        }
        this.imeiNum = imeiNum;
    }


    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        if (authKey != null) {
            authKey = authKey.trim();
            if (authKey.equals("")) {
                authKey = null;
            }
        }
        this.authKey = authKey;
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

    public String getClientCapabilitySet() {
        return clientCapabilitySet;
    }

    public void setClientCapabilitySet(String clientCapabilitySet) {
        this.clientCapabilitySet = clientCapabilitySet;
    }

    public Integer getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(Integer subsClientType) {
        this.subsClientType = subsClientType;
    }

    public int getClientType() {
        return this.clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
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
        if (dtoObject instanceof KnIPClientRegistryDTO) {
            KnIPClientRegistryDTO ipClntRegDTO = (KnIPClientRegistryDTO) dtoObject;
            clientId = ipClntRegDTO.getClientId();
            mdn = ipClntRegDTO.getMdn();
            name = ipClntRegDTO.getName();
            serviceStatus = ipClntRegDTO.getServiceAuthStatus();
            subsType = ipClntRegDTO.getSubscriberType();
            activationKey = ipClntRegDTO.getActivationKey();
            authKey = ipClntRegDTO.getAuthKey();
            imeiNum = ipClntRegDTO.getImeiNum();
            realm = ipClntRegDTO.getRealm();
        }
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

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public String getSwType() {
        return swType;
    }

    public void setSwType(String swType) {
        this.swType = swType;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public Map<Integer, Integer> getVocoderIdMap() {
        return vocoderIdMap;
    }

    public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
        this.vocoderIdMap = vocoderIdMap;
    }

    public String toString() {
        toStringBuffer.setLength(0);
        toStringBuffer.append("apn Name...").append(apnName);
        toStringBuffer.append(super.toString());
        return toStringBuffer.toString();
    }


}
