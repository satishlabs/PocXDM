/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***********************************************************************
 * <p/>
 * File name:  KnClientInfoDTO.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rama              Nov 3, 2010       6.4
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
 * ***********************************************************************
 */

package com.kodiak.library.activation.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.library.activation.dto.IClientInfoDTO;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.intf.ISubscriberDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;


public abstract class KnClientInfoDTO implements IClientInfoDTO, ISubscriberDTO {

    private static final long serialVersionUID = 7526471155622676270L;
    //stores the clientId
    private String clientId = null;
    //stores the client type
    private int clientType;
    //stores the feature id
    private String featureId;
    //stores the operation type
    private String operationType;
    //stores the object id
    private String objectId;
    //stores the mdn
    private String mdn;
    //stores the name
    private String name;
    //stores the subscriber type
    private int subsType;
    //stores the service status
    private int serviceStatus;
    //stores dto status
    private int dtoStatus;
    //stores error object
    private KnXDMError errorObj;

    private StringBuffer toStringBuffer = new StringBuffer();

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

    public String getObjectId() {
        return objectId;
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    // returns the status of the DTO object
    public int getDTOStatus() {
        return this.dtoStatus;
    }

    // sets the status to tht DTO
    public void setDTOStatus(int dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    public int getSubsType() {
        return subsType;
    }

    public void setSubsType(int subsType) {
        this.subsType = subsType;
    }

    public int getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(int serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public int getDtoStatus() {
        return dtoStatus;
    }

    public void setDtoStatus(int dtoStatus) {
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
        if (dtoObject instanceof KnClientInfoDTO) {
            KnClientInfoDTO clientInfoDTO = (KnClientInfoDTO) dtoObject;
            clientId = clientInfoDTO.getClientId();
            mdn = clientInfoDTO.getMdn();
            name = clientInfoDTO.getName();
            serviceStatus = clientInfoDTO.getServiceAuthStatus();
            subsType = clientInfoDTO.getSubscriberType();
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

    public void setNetworkName(String networkName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getNetworkName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPubSubscriptionType(int pubSubscriptionType) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getPubSubscriptionType() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setCorpSubscriptionType(int corpSubscriptionType) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getCorpSubscriptionType() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
//        toStringBuffer.setLength(0);
//        toStringBuffer.append(super.toString());
//        return toStringBuffer.toString();

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("MDN - ").append(KnGDPRTemplate.mdn(mdn));
        return strBuffer.toString();
    }
}
