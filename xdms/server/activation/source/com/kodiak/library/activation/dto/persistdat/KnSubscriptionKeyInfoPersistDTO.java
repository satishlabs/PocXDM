/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubscriptionKeyInfoPersistDTO.java
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
package com.kodiak.library.activation.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;

public class KnSubscriptionKeyInfoPersistDTO extends KnSubscriberPersistDTO {

    private static final long serialVersionUID = 7526471155622676272L;

    private String profile = null;
    private String operationType = null;
    private IPersistenceDTO performerDto = null;
    private IInputDTO inputDTO;
    private String entityId;
    private StringBuffer toStringBuffer = new StringBuffer(200);

    private long expiryTime = 0;
    private String mdn = null;
    private String activationKey = null;
    private String serviceName = null;

    public KnSubscriptionKeyInfoPersistDTO() {
    }

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setPerformerDetails(IPersistenceDTO persistenceDTO) {
        this.performerDto = persistenceDTO;
    }

    public IPersistenceDTO getPerformerDetails() {
        return performerDto;
    }

    public void setOperationType(String operationType) {
        if (operationType != null) {
            operationType = operationType.trim();
            if (operationType.equals("")) {
                operationType = null;
            }
        }
        this.operationType = operationType;
    }

    public String getOperationType() {
        return this.operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        if (entityId != null) {
            entityId = entityId.trim();
            if (entityId.equals("")) {
                entityId = null;
            }
        }
        this.entityId = entityId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        if (profile != null) {
            profile = profile.trim();
            if (profile.equals("")) {
                profile = null;
            }
        }
        this.profile = profile;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getMdn() {
        return mdn;
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

    public String toString() {
        toStringBuffer.setLength(0);
        return toStringBuffer.append(super.toString())
                .append(", Entity Id : ").append(entityId)
                .append(", Operation Type : ").append(operationType)
                .append(", Performer DTO : ").append(performerDto)
                .append(", expiryTime: ").append(expiryTime)
                .toString();
    }

    /**
     * returns the id
     *
     * @return id
     */
    public String getObjectId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
