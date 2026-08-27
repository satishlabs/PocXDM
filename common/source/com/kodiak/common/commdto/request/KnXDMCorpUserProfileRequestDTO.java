/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.commdto.common.KnXDMUserProfileInfoDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;

public class KnXDMCorpUserProfileRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

    private static final long serialVersionUID = -7152562662191833838L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;

    private String corpId;
    private String fetchSize;
    private String startIndex;
    private String userProfileId;
    private String userProfileName;
    private String mdn;
    private KnXDMUserProfileInfoDTO userProfileInfo;
	private String isDefaultProfile;
    private List<String> txnList;
    private List<String> ownerIdList;
    private List<String> addedOwnerIdList;
    private List<String> removedOwnerIdList;
    private String sharingEnabled;
    private List<String> userProfileSharedCorpList;
    private String isCaseSensitiveSearch;
    private String hierarchyId;


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

    @Override
    public String getCorpId() {
        return corpId;
    }

    @Override
    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public KnXDMUserProfileInfoDTO getUserProfileInfo() {
        return userProfileInfo;
    }

    public void setUserProfileInfo(KnXDMUserProfileInfoDTO userProfileInfo) {
        this.userProfileInfo = userProfileInfo;
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	public String getIsDefaultProfile() {
        return isDefaultProfile;
    }

    public void setIsDefaultProfile(String isDefaultProfile) {
        this.isDefaultProfile = isDefaultProfile;
    }

    public List<String> getTxnList() {   return txnList; }

    public void setTxnList(List<String> txnList) {    this.txnList = txnList;  }

    public List<String> getOwnerIdList() {
        return ownerIdList;
    }

    public void setOwnerIdList(List<String> ownerIdList) {
        this.ownerIdList = ownerIdList;
    }

    public List<String> getAddedOwnerIdList() {
        return addedOwnerIdList;
    }

    public void setAddedOwnerIdList(List<String> addedOwnerIdList) {
        this.addedOwnerIdList = addedOwnerIdList;
    }

    public List<String> getRemovedOwnerIdList() {
        return removedOwnerIdList;
    }

    public void setRemovedOwnerIdList(List<String> removedOwnerIdList) {
        this.removedOwnerIdList = removedOwnerIdList;
    }

    public String getSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(String sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }

    public List<String> getUserProfileSharedCorpList() {
        return userProfileSharedCorpList;
    }

    public void setUserProfileSharedCorpList(List<String> userProfileSharedCorpList) {
        this.userProfileSharedCorpList = userProfileSharedCorpList;
    }

    public String getIsCaseSensitiveSearch() {
        return isCaseSensitiveSearch;
    }

    public void setIsCaseSensitiveSearch(String isCaseSensitiveSearch) {
        this.isCaseSensitiveSearch = isCaseSensitiveSearch;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    @Override
    public String toString() {
        return "KnXDMCorpUserProfileRequestDTO{" +
                "operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", corpId='" + corpId + '\'' +
                ", userProfileId='" + userProfileId + '\'' +
                ", userProfileName='" + userProfileName + '\'' +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", userProfileInfo=" + userProfileInfo +
                ", txnList=" + txnList +
                ", ownerIdList=" + ownerIdList +
                ", addedOwnerIdList=" + addedOwnerIdList +
                ", removedOwnerIdList=" + removedOwnerIdList +
                ", sharingEnabled=" + sharingEnabled +
                ", isCaseSensitiveSearch=" + isCaseSensitiveSearch +
                ", userProfileSharedCorpList=" + userProfileSharedCorpList +
                ", hierarchyId='" + hierarchyId + '\'' +
                '}';
    }
}
