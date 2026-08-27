/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;

/**
 * Created by schandra on 09-11-2016.
 */
public class KnCorpSubsResquestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

    private static final long serialVersionUID = 2654525373434268389L;
    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String subscriberMdn;
    private String subscriberName;
    private String subscriptionType;
    private List<String> mdnList;
    private String email;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String dispatchType;
    private String userId;
    private String appId;
    private String serviceAuthStatus;
    private String aliasMdn;
    private int tmpPwdMode;
    private String tmpPwd;
    private String tmpPwdExpiry;
    private String toMdn;
    private String fromMdn;
    private Collection<Integer> groupIds;
    private String mcId;
    private String mcPttId;
    private String mcVideoId;
    private String mcDataId;

    private Integer recordingStatus;

    private String deviceId;

    private int sendAccountMail;

    private String cloningBitset;
    private boolean upmFlag;
    private String pttSettingDocId;

    public String getCloningBitset() {
        return cloningBitset;
    }

    public void setCloningBitset(String cloningBitset) {
        this.cloningBitset = cloningBitset;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(Integer recodingStatus) {
        this.recordingStatus = recodingStatus;
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

    public String getSubscriberMdn() {
        return subscriberMdn;
    }

    public void setSubscriberMdn(String subscriberMdn) {
        this.subscriberMdn = subscriberMdn;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

	@Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(String dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(String serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public int getTmpPwdMode() {
        return tmpPwdMode;
    }

    public void setTmpPwdMode(int tmpPwdMode) {
        this.tmpPwdMode = tmpPwdMode;
    }

    public String getTmpPwd() {
        return tmpPwd;
    }

    public void setTmpPwd(String tmpPwd) {
        this.tmpPwd = tmpPwd;
    }

    public String getTmpPwdExpiry() {
        return tmpPwdExpiry;
    }

    public void setTmpPwdExpiry(String tmpPwdExpiry) {
        this.tmpPwdExpiry = tmpPwdExpiry;
    }

    public String getToMdn() {
        return toMdn;
    }

    public void setToMdn(String toMdn) {
        this.toMdn = toMdn;
    }

    public String getFromMdn() {
        return fromMdn;
    }

    public void setFromMdn(String fromMdn) {
        this.fromMdn = fromMdn;
    }

    public Collection<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Collection<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public String getMcPttId() {
        return mcPttId;
    }

    public void setMcPttId(String mcPttId) {
        this.mcPttId = mcPttId;
    }

    public String getMcVideoId() {
        return mcVideoId;
    }

    public void setMcVideoId(String mcVideoId) {
        this.mcVideoId = mcVideoId;
    }

    public String getMcDataId() {
        return mcDataId;
    }

    public void setMcDataId(String mcDataId) {
        this.mcDataId = mcDataId;
    }

    public int getSendAccountMail() {
        return sendAccountMail;
    }

    public void setSendAccountMail(int sendAccountMail) {
        this.sendAccountMail = sendAccountMail;
    }

    public boolean isUpmFlag() {
        return upmFlag;
    }

    public void setUpmFlag(boolean upmFlag) {
        this.upmFlag = upmFlag;
    }

    public String getPttSettingDocId() {
        return pttSettingDocId;
    }

    public void setPttSettingDocId(String pttSettingDocId) {
        this.pttSettingDocId = pttSettingDocId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(super.toString())
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", AuthDto - ").append(authDTO)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", subscriberMdn - ").append(KnGDPRTemplate.mdn(subscriberMdn))
                .append(", transactionId - ").append(transactionId)
                .append(", subscriberMdn - ").append(KnGDPRTemplate.mdn(subscriberMdn))
                .append(", subscriberName - ").append(KnGDPRTemplate.name(subscriberName))
                .append(", subscriptionType - ").append(subscriptionType)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", mdnList - ").append(KnGDPRTemplate.mdnList(mdnList))
                .append(", email - ").append(KnGDPRTemplate.email(email))
                .append(", dispatchType - ").append(dispatchType)
                .append(", userId - ").append(KnGDPRTemplate.userId(userId))
                .append(", appId - ").append(appId)
                .append(", serviceAuthStatus - ").append(serviceAuthStatus)
                .append(", aliasMdn - ").append(KnGDPRTemplate.mdn(aliasMdn))
                .append(", tmpPwdMode - ").append(tmpPwdMode)
                .append(", tmpPwd - ").append(tmpPwd)
                .append(", tmpPwdExpiry - ").append(tmpPwdExpiry)
                .append(", toMdn - ").append(KnGDPRTemplate.mdn(KnGDPRTemplate.mdn(toMdn)))
                .append(", fromMdn - ").append(KnGDPRTemplate.mdn(KnGDPRTemplate.mdn(fromMdn)))
                .append(", groupIds - ").append(groupIds)
                .append(", mcId - ").append(KnGDPRTemplate.mcId(mcId))
                .append(", mcPttId - ").append(KnGDPRTemplate.mcpttId(mcPttId))
                .append(", mcVideoId - ").append(KnGDPRTemplate.mcvideoId(mcVideoId))
                .append(", recordingStatus - ").append(recordingStatus)
                .append(", sendAccountMail - ").append(sendAccountMail)
                .append(", pttSettingDocId - ").append(pttSettingDocId)
                .append(", mcDataId - ").append(KnGDPRTemplate.mcdataId(mcDataId));
        return sb.toString();
    }
}
