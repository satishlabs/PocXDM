/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;


import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.Map;

public class KnXDMCorpProfileInfoDTO extends KnXDMCorpProfileDTO implements IXDMRequestDTO{

    private static final long serialVersionUID = 3996349825029985017L;
    //stores the Client Type
    private int clientType;

    //stores the operation Type
    private String operationType;

    private String transactionId;

    private IAuthDTO authDTO;

    private long ifMatch;
    private long ifNoneMatch;

    private KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus;

    private String destPttServerId;

    private String destQueueName;
    private KnUserAgentDTO userAgentDTO;

    private String srcRoutingKey;

    private String srcQueueName;

    /**
     * stores the correlation Id that is used to correlate a response with a request
     */
    private String correlationId;

    /**
     * Stores the srcIPAddress of the original request machine
     */
    private String srcIPAddress;


    private Map<Integer,Integer> vocoderIdMap;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private String  version;

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public long getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(long ifMatch) {
        this.ifMatch = ifMatch;
    }

    public long getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(long ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public KnConstants.SERVICE_AUTH_STATUS getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
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

    public KnUserAgentDTO getUserAgentDTO() {
        return userAgentDTO;
    }

    public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
        this.userAgentDTO = userAgentDTO;
    }

    public String getSrcRoutingKey() {
        return srcRoutingKey;
    }

    public void setSrcRoutingKey(String srcRoutingKey) {
        this.srcRoutingKey = srcRoutingKey;
    }

    public String getSrcQueueName() {
        return srcQueueName;
    }

    public void setSrcQueueName(String srcQueueName) {
        this.srcQueueName = srcQueueName;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getSrcIPAddress() {
        return srcIPAddress;
    }

    public void setSrcIPAddress(String srcIPAddress) {
        this.srcIPAddress = srcIPAddress;
    }

    public Map<Integer, Integer> getVocoderIdMap() {
        return vocoderIdMap;
    }

    public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
        this.vocoderIdMap = vocoderIdMap;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "KnCorpProfileInfoDTO{" +
                "clientType=" + clientType +
                ", operationType='" + operationType + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", authDTO=" + authDTO +
                ", ifMatch=" + ifMatch +
                ", ifNoneMatch=" + ifNoneMatch +
                ", serviceAuthStatus=" + serviceAuthStatus +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", userAgentDTO=" + userAgentDTO +
                ", srcRoutingKey='" + srcRoutingKey + '\'' +
                ", srcQueueName='" + srcQueueName + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", srcIPAddress='" + srcIPAddress + '\'' +
                ", vocoderIdMap=" + vocoderIdMap +
                ", hierarchyType=" + hierarchyType +
                ", version='" + version + '\'' +
                ", ['" + super.toString() + '\'' +
                '}';
    }
}
