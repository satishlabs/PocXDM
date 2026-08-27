/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

/**
 * *********************************************************************
 * File name:   KnXDMScanlistResponseDTO.java
 * Subsystem:   PoCXDM
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Hemanta Tahu          5/8/14      7.5
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

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;

public class KnXDMScanlistResponseDTO extends KnXDMRespDTO {

    private static final long serialVersionUID = 2765996681123741538L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;

    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String mdn;
    private List<KnXDMTalkGroupInfoDTO> scanList;
    private long corpId;

    private Integer enabled;
    private Integer campModeCap;


    public long getCorpId() {
        return corpId;
    }

    public void setCorpId(long corpId) {
        this.corpId = corpId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public List<KnXDMTalkGroupInfoDTO> getScanList() {
        return scanList;
    }

    public void setScanList(List<KnXDMTalkGroupInfoDTO> scanList) {
        this.scanList = scanList;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getCampModeCap() {
        return campModeCap;
    }

    public void setCampModeCap(Integer campModeCap) {
        this.campModeCap = campModeCap;
    }

    @Override
    public String toString() {
        return "KnXDMScanlistResponseDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", scanList=" + scanList +
                ", corpId=" + corpId +
                ", enabled=" + enabled +
                ", campModeCap=" + campModeCap +
                '}';
    }
}
