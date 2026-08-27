/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;


import java.util.Collection;
import java.util.List;

/**
 * Created by hanwar on 07-06-2016.
 */
public class KnSysConfigRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622676237L;

    private List<String> roamingType;
    private long configFeatureSet;
    private String idmServiceFlag;


    public List<String> getRoamingType() {
        return roamingType;
    }

    public void setRoamingType(List<String> roamingType) {
        this.roamingType = roamingType;
    }

    public long getConfigFeatureSet() {
        return configFeatureSet;
    }

    public void setConfigFeatureSet(long configFeatureSet) {
        this.configFeatureSet = configFeatureSet;
    }

    public String getIdmServiceFlag() {
        return idmServiceFlag;
    }

    public void setIdmServiceFlag(String idmServiceFlag) {
        this.idmServiceFlag = idmServiceFlag;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnSysConfigRespDTO --> ");
        strBuffer.append(", roamingType - ").append(roamingType)
                .append(", featureSet - ").append(configFeatureSet)
                .append(", idmServiceFlag - ").append(idmServiceFlag)
                .append("]");

        return strBuffer.toString();
    }

    @Override
    public String getResponseCode() {
        return null;
    }

    @Override
    public void setResponseCode(String responseCode) {

    }

    @Override
    public int getResponseStatus() {
        return 0;
    }

    @Override
    public void setResponseStatus(int responseStatus) {

    }

    @Override
    public String getResponseMessage() {
        return null;
    }

    @Override
    public void setResponseMessage(String responseMessage) {

    }

    @Override
    public Collection getResponseDetails() {
        return null;
    }

    @Override
    public void setResponseDetails(Collection responseDetails) {

    }

    @Override
    public void setDestPttServerId(String destPttServerId) {

    }

    @Override
    public String getDestPttServerId() {
        return null;
    }

    @Override
    public void setDestQueueName(String destQueueName) {

    }

    @Override
    public String getDestQueueName() {
        return null;
    }

    @Override
    public String getTransactionId() {
        return null;
    }

    @Override
    public void setTransactionId(String transactionId) {

    }

    @Override
    public String getObjectId() {
        return null;
    }
}
