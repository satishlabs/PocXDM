/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXdmServerInfoPersistDTO.java
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

import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.library.activation.dto.common.KnClientInfoDTO;

public class KnXdmServerInfoPersistDTO extends KnClientInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676273L;

    private String profile = null;
    private String operationType = null;
    private IPersistenceDTO performerDto = null;
    private IInputDTO inputDTO;
    private String entityId;
    private int dtoStatus;
    private KnXDMError errorObj;

    private String mdn = null;
    private String xdmServerId = null;

    private int subType;
    private int serviceStatus;

    private StringBuffer toStringBuffer = new StringBuffer(200);

    public KnXdmServerInfoPersistDTO() {
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

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.performerDto = persistenceDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
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

    public String toString() {
        toStringBuffer.setLength(0);
        return toStringBuffer.append(super.toString())
                .append(", Entity Id : ").append(entityId)
                .append(", Operation Type : ").append(operationType)
                .append(", Performer DTO : ").append(performerDto)
                .toString();
    }

    public String getObjectId() {
        return null;
    }

    /**
     * sets the mdn of the subscriber
     *
     * @param mdn mdn of an subscriber
     */
    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    /**
     * returns the mdn of the subscriber
     *
     * @return mdn mdn of an subscriber
     */
    public String getMdn() {
        return this.mdn;
    }

    /**
     * sets the xdmServerId of the subscriber
     *
     * @param xdmServerId of an subscriber
     */
    public void setXdmServerId(String xdmServerId) {
        if (xdmServerId != null) {
            xdmServerId = xdmServerId.trim();
            if (xdmServerId.equals("")) {
                xdmServerId = null;
            }
        }
        this.xdmServerId = xdmServerId;
    }

    /**
     * returns the xdmServerId of the subscriber
     *
     * @return mdn xdmServerId of an subscriber
     */
    public String getXdmServerId() {
        return this.xdmServerId;
    }

    public void setSubscriberType(int subType) {
        this.subType = subType;
    }

    public int getSubscriberType() {
        return this.subType;
    }

    public void setServiceAuthStatus(int serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public int getServiceAuthStatus() {
        return this.serviceStatus;
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
        super.populate(dtoObject);
    }
}
