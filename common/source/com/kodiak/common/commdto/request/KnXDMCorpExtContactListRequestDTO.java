/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpExtContactListRequestDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 17, 2011      7.0
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
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.Collection;

public class KnXDMCorpExtContactListRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622776159L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String objectId;
    private Collection<KnXDMMdnInfoDTO> externalContactList;
    private Collection<KnXDMMdnInfoDTO> externalAliasMdnContactList;
    private Collection<KnXDMMdnInfoDTO> externalUserIdContactList;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
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

    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Collection<KnXDMMdnInfoDTO> getExternalContactList() {
        return externalContactList;
    }

    public void setExternalContactList(Collection<KnXDMMdnInfoDTO> externalContactList) {
        this.externalContactList = externalContactList;
    }

    public Collection<KnXDMMdnInfoDTO> getExternalAliasMdnContactList() {
        return externalAliasMdnContactList;
    }

    public void setExternalAliasMdnContactList(Collection<KnXDMMdnInfoDTO> externalAliasMdnContactList) {
        this.externalAliasMdnContactList = externalAliasMdnContactList;
    }

    public Collection<KnXDMMdnInfoDTO> getExternalUserIdContactList() {
        return externalUserIdContactList;
    }

    public void setExternalUserIdContactList(Collection<KnXDMMdnInfoDTO> externalUserIdContactList) {
        this.externalUserIdContactList = externalUserIdContactList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", AuthDTO - ").append(authDTO)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", transactionId - ").append(transactionId)
                .append(", ObjectId - ").append(objectId)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", External contacts - ").append(externalContactList)
                .append(", External contacts AliasMdns- ").append(externalAliasMdnContactList)
                .append(", External contacts UserIds- ").append(externalUserIdContactList);
        return sb.toString();
    }
}

