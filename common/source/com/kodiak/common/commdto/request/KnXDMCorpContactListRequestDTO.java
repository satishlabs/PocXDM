/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpContactListRequestDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 12, 2011      7.0
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
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.Map;


public class KnXDMCorpContactListRequestDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622776163L;

    private String corpId;
    private long etag;
    private String ownerMdn;
    private String contactListName;
    private String contactListDisplayName;
    private String contactEntryUri;
    private int ifMatch;
    private int ifNoneMatch;
    private String strXml;
    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String objectId;
    private Collection<String> addedMdnList;
    private Collection<String> removedMdnList;
    private Collection<Integer> addedSublistIds;
    private Collection<Integer> removedSublistIds;
    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String version;
    private boolean upmCall;

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public long getEtag() {
        return etag;
    }


    public int getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(int ifMatch) {
        this.ifMatch = ifMatch;
    }

    public int getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(int ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public String getStrXml() {
        return strXml;
    }

    public void setStrXml(String strXml) {
        this.strXml = strXml;
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

    public void setEtag(long etag) {
        this.etag = etag;
    }

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
    }

    public String getContactListName() {
        return contactListName;
    }

    public void setContactListName(String contactListName) {
        this.contactListName = contactListName;
    }

    public String getContactListDisplayName() {
        return contactListDisplayName;
    }

    public void setContactListDisplayName(String contactListDisplayName) {
        this.contactListDisplayName = contactListDisplayName;
    }

    public String getContactEntryUri() {
        return contactEntryUri;
    }

    public void setContactEntryUri(String contactEntryUri) {
        this.contactEntryUri = contactEntryUri;
    }

    public Collection<String> getAddedMdnList() {
        return addedMdnList;
    }

    public void setAddedMdnList(Collection<String> addedMdnList) {
        this.addedMdnList = addedMdnList;
    }

    public Collection<String> getRemovedMdnList() {
        return removedMdnList;
    }

    public void setRemovedMdnList(Collection<String> removedMdnList) {
        this.removedMdnList = removedMdnList;
    }

    public Collection<Integer> getAddedSublistIds() {
        return addedSublistIds;
    }

    public void setAddedSublistIds(Collection<Integer> addedSublistIds) {
        this.addedSublistIds = addedSublistIds;
    }

    public Collection<Integer> getRemovedSublistIds() {
        return removedSublistIds;
    }

    public void setRemovedSublistIds(Collection<Integer> removedSublistIds) {
        this.removedSublistIds = removedSublistIds;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append(super.toString())
                .append(", CorpId - ").append(corpId)
                .append(", OperationType : ").append(operationType)
                .append(", ClientType : ").append(clientType)
                .append(", AuthDTO : ").append(authDTO)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", transactionId - ").append(transactionId)
                .append(", ObjectId : ").append(objectId)
                .append(" OwnerMdn - ").append(KnGDPRTemplate.mdn(ownerMdn))
                .append(", ContactListName - ").append(contactListName)
                .append(", ContactListDisplayName - ").append(contactListDisplayName)
                .append(", Etag - ").append(etag)
                .append(", If-Match - ").append(ifMatch)
                .append(", If-None-Match - ").append(ifNoneMatch)
                .append(", ContactEntryUri - ").append(contactEntryUri)
                .append(", StrXml - ").append(strXml)
                .append(", addedMdnList - ").append(KnGDPRTemplate.mdnList(addedMdnList))
                .append(", removedMdnList - ").append(KnGDPRTemplate.mdnList(removedMdnList))
                .append(", addedSublistIds - ").append(addedSublistIds)
                .append(", removedSublistIds - ").append(removedSublistIds)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", customParammap - ").append(customParamMap)
                .append(", upmCall - ").append(upmCall)
                .append(", version - ").append(version);
        return sb.toString();
    }
}
