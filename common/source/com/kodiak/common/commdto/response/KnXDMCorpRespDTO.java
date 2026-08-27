/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class KnXDMCorpRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622776153L;

    private String objectId;
    private int responseStatus = 1;
    private String responseCode;
    private String responseMessage;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private Collection<String> disabledDispatchMemList;
    private Collection<String> enabledDispatchMemList;
    private Collection<KnXDMFailureRespDTO> failureDetails;
    private String etag;
    private boolean isSublistExists;
    private Map<String, Object> customParamMap;
    private String callPermission;

    private String mdn;
    private String recordingStatus;
    private int pamEmailAccId;
    private String catAccessPermUpdateTS;
    private String osmListId;

    private String zonePositionAssignmentStatus;
    // Field for tracking unassigned MDNs during auto-assignment
    private List<String> unassignedMdns;
    private Map<String, Object> additionalInfo;
    public List<String> getUnassignedMdns() {
        return unassignedMdns;
    }

    public void setUnassignedMdns(List<String> unassignedMdns) {
        this.unassignedMdns = unassignedMdns;
    }

    public String getZonePositionAssignmentStatus() {
        return zonePositionAssignmentStatus;
    }

    public void setZonePositionAssignmentStatus(String zonePositionAssignmentStatus) {
        this.zonePositionAssignmentStatus = zonePositionAssignmentStatus;
    }

    private String pttSettingDocId;

    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
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

    public String getOsmListId() {
        return osmListId;
    }

    public void setOsmListId(String osmListId) {
        this.osmListId = osmListId;
    }

    public Collection getResponseDetails() {
        return null; //todo not be used for corporate
    }

    public void setResponseDetails(Collection responseDetails) {
        //todo not be used for corporate
    }

    public String getObjectId() {
        return this.objectId;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Collection<KnXDMFailureRespDTO> getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(Collection<KnXDMFailureRespDTO> failureDetails) {
        this.failureDetails = failureDetails;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public Collection<String> getDisabledDispatchMemList() {
        return disabledDispatchMemList;
    }

    public void setDisabledDispatchMemList(Collection<String> disabledDispatchMemList) {
        this.disabledDispatchMemList = disabledDispatchMemList;
    }

    public Collection<String> getEnabledDispatchMemList() {
        return enabledDispatchMemList;
    }

    public void setEnabledDispatchMemList(Collection<String> enabledDispatchMemList) {
        this.enabledDispatchMemList = enabledDispatchMemList;
    }

    public boolean isSublistExists() {
        return isSublistExists;
    }

    public void setSublistExists(boolean isSublistExists) {
        this.isSublistExists = isSublistExists;
    }

    public String getCallPermission() {
        return callPermission;
    }

    public void setCallPermission(String callPermission) {
        this.callPermission = callPermission;
    }

    public void setMdn(String mdn){ this.mdn = mdn; };

    public String getMdn() {
        return mdn;
    }

    public void setRecordingStatus(String recordingStatus) { this.recordingStatus = recordingStatus; }

    public String getRecordingStatus() { return recordingStatus; }

    public int getpamEmailAccId() {
        return pamEmailAccId;
    }

    public void setpamEmailAccId(int pamEmailAccId) {
        this.pamEmailAccId = pamEmailAccId;
    }

    public String getCatAccessPermUpdateTS() {
        return catAccessPermUpdateTS;
    }

    public void setCatAccessPermUpdateTS(String catAccessPermUpdateTS) {
        this.catAccessPermUpdateTS = catAccessPermUpdateTS;
    }

    public String getPttSettingDocId() {
        return pttSettingDocId;
    }

    public void setPttSettingDocId(String pttSettingDocId) {
        this.pttSettingDocId = pttSettingDocId;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("ObjectId - ").append(objectId)
                .append(", ResponseStatus - ").append(responseStatus)
                .append(", ResponseCode - ").append(responseCode)
                .append(", ResponseMsg - ").append(responseMessage)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", transactionId - ").append(transactionId)
                .append(", Failure Details - ").append(failureDetails)
                .append(", etag - ").append(etag)
                .append(", enabledDispatchMemList - ").append(enabledDispatchMemList)
                .append(", disabledDispatchMemList - ").append(disabledDispatchMemList)
                .append(", customParamMap - ").append(customParamMap)
                .append(", isSublistExists - ").append(isSublistExists)
                .append(", callPermission - ").append(callPermission)
                .append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", recordingStatus - ").append(recordingStatus)
                .append(", pamEmailAccId - ").append(pamEmailAccId)
                .append(", catAccessPermUpdateTS - ").append(catAccessPermUpdateTS)
                .append(", zonePositionAssignmentStatus - ").append(zonePositionAssignmentStatus)
                .append(", unassignedMdns - ").append(unassignedMdns)
                .append(", catAccessPermUpdateTS - ").append(catAccessPermUpdateTS)
                .append(", pttSettingDocId - ").append(pttSettingDocId)
                .append(", additionalInfo - ").append(additionalInfo)
                .append(", osmListId - ").append(osmListId)
        ;
        return sb.toString();


    }
}
