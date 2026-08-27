/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;

import java.util.List;

public class KnXDMDeleteBulkGroupReqDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

    private static final long serialVersionUID = -5184657554797864125L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;


    private String profileName;
    private Integer profileId;
    private List<Integer> groupIdList;


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



    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public List<Integer> getGroupIdList() { return groupIdList; }

    public void setGroupIdList(List<Integer> groupIdList) { this.groupIdList = groupIdList; }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(super.toString())
                .append("profileId - ").append(profileId)
                .append(", profileName - ").append(profileName)
                .append(", groupIdList - ").append(groupIdList);
        return sb.toString();
    }
}
