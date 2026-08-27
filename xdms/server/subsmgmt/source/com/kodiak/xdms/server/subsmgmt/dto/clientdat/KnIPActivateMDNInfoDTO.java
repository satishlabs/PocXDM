/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnIPActivateMDNInfoDTO.java
 * Subsystem:
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/10/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscriberProvDTO;

import java.util.Map;

public class KnIPActivateMDNInfoDTO extends KnSubscriberProvDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676225L;

    private String realm;

    private String performer;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;

    private IAuthDTO authDTO;

    private String userAgent;
    private KnUserAgentDTO userAgentDTO;

    private String apnName;

    private Map<Integer,Integer> vocoderIdMap;

    private String authKey;

    /**
     * getter method for the realm
     *
     * @return String
     */
    public String getRealm() {
        return realm;
    }

    /**
     * setter method for the realm
     *
     * @param realm String
     */
    public void setRealm(String realm) {
        if (realm != null) {
            realm = realm.trim();
            if (realm.equals("")) {
                realm = null;
            }
        }
        this.realm = realm;
    }

    /**
     * setter method for the Performer
     *
     * @param performer sets performer to subscriber
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
     * getter method for Performer
     *
     * @return String
     */
    public String getPerformer() {
        return performer;
    }

    /**
     * setter method for the Auth DTO
     *
     * @param authDTO String
     */
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    /**
     * getter method for the Auth DTO
     *
     * @return IAuthDTO
     */
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }


    /**
     * setter method for the clientType
     *
     * @param clientType int
     */
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    /**
     * getter method for the client Type
     *
     * @return int
     */
    public int getClientType() {
        return clientType;
    }

    /**
     * setter method for the Operation Type
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
     * getter method for operation Type
     *
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * getter method for the Entity Id
     *
     * @return String
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * setter method for the entity Id
     *
     * @param entityId String
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

    public String getObjectId() {
        return super.getMdn();
    }

    /**
     * getter method for the User Agent
     *
     * @return String
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * setter method for the User Agent
     *
     * @param userAgent String
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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

    public Map<Integer, Integer> getVocoderIdMap() {
        return vocoderIdMap;
    }

    public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
        this.vocoderIdMap = vocoderIdMap;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append(", KnIPActivateMdnInfoDTO :: ");
        strBuffer.append(", Realm - ").append(realm)
                .append(", AuthDTO - ").append(authDTO)
                .append(", Operation_TYpe - ").append(operationType)
                .append(", Client_Type - ").append(clientType)
                .append(", Entity_ID - ").append(entityId)
                .append(", User Agent - ").append(userAgent)
                .append(", User Agent DTO - ").append(userAgentDTO)
                .append(", apnName - ").append(apnName)
                .append(", vocoderIdMap - ").append(vocoderIdMap);

        return strBuffer.toString();
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void validate() throws KnValidationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
