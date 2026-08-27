/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;

public class KnXDMCorporateFSRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {
    private static final long serialVersionUID = -1540086788311269483L;
    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private Integer filterType;
    private Integer nextToken;
    private Integer fetchSize;
    private Integer sortType;
    private Boolean enableAutoPair;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String pttRecording;
    private String dataRecording;
    private String videoRecording;
    private String selfDnDPrivilege;
    private String largeAgencyDispatch;

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

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {

    }

    public Integer getFilterType() {
        return filterType;
    }

    public void setFilterType(Integer filterType) {
        this.filterType = filterType;
    }

    public Integer getNextToken() {
        return nextToken;
    }

    public void setNextToken(Integer nextToken) {
        this.nextToken = nextToken;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public Boolean getEnableAutoPair() {
        return enableAutoPair;
    }

    public void setEnableAutoPair(Boolean enableAutoPair) {
        this.enableAutoPair = enableAutoPair;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public String getPttRecording() {
        return pttRecording;
    }

    public void setPttRecording(String pttRecording) {
        this.pttRecording = pttRecording;
    }

    public String getDataRecording() {
        return dataRecording;
    }

    public void setDataRecording(String dataRecording) {
        this.dataRecording = dataRecording;
    }

    public String getVideoRecording() {
        return videoRecording;
    }

    public void setVideoRecording(String videoRecording) {
        this.videoRecording = videoRecording;
    }

    public String getSelfDnDPrivilege() {
        return selfDnDPrivilege;
    }

    public void setSelfDnDPrivilege(String selfDnDPrivilege) {
        this.selfDnDPrivilege = selfDnDPrivilege;
    }

    public String getLargeAgencyDispatch() { return largeAgencyDispatch; }

    public void setLargeAgencyDispatch(String largeAgencyDispatch) { this.largeAgencyDispatch = largeAgencyDispatch; }

    @Override
    public String toString() {
        return "KnXDMCorporateFSRequestDTO{" +
                "operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", filterType=" + filterType +
                ", nextToken=" + nextToken +
                ", fetchSize=" + fetchSize +
                ", sortType=" + sortType +
                ", enableAutoPair=" + enableAutoPair +
                ", hierarchyType=" + hierarchyType +
                ", pttRecording='" + pttRecording + '\'' +
                ", dataRecording='" + dataRecording + '\'' +
                ", videoRecording='" + videoRecording + '\'' +
                ", selfDndPrivilege='" + selfDnDPrivilege + '\'' +
                ", largeAgencyDispatch='" + largeAgencyDispatch + '\'' +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}

