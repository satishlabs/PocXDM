/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnUserEmergencyAttributes;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpSubsEmergencyRequestDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnCorpSubsEmergencyRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO  {
    private static final long serialVersionUID = 7526471155622776456L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private KnUserEmergencyAttributes emergencyAttributes;
    private boolean upmCall;
    private boolean isUpmHierarchyCall;

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    @Override
    public String getDestPttServerId() {
        return destPttServerId;
    }

    @Override
    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    @Override
    public String getDestQueueName() {
        return destQueueName;
    }

    @Override
    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public KnUserEmergencyAttributes getEmergencyAttributes() {
        return emergencyAttributes;
    }

    public void setEmergencyAttributes(KnUserEmergencyAttributes emergencyAttributes) {
        this.emergencyAttributes = emergencyAttributes;
    }

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    public boolean isUpmHierarchyCall() { return isUpmHierarchyCall; }

    public void setUpmHierarchyCall(boolean upmHierarchyCall) { isUpmHierarchyCall = upmHierarchyCall; }

    @Override
    public String toString() {
        return "KnCorpSubsEmergencyRequestDTO{" +
                "emergencyAttributes=" + emergencyAttributes +
                "upmCall=" + upmCall +
                "isUpmHierarchyCall=" + isUpmHierarchyCall +
                '}';
    }
}
