/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnXDMLITaskDetail {
    @JsonProperty("DID")
    private String[] did;
    private Integer serviceType;
    private Integer deliveryType;
    private Integer activationStatus;
    private Integer locationType;
    private Long timestamp;

    public String[] getDid() {
        return did;
    }

    public void setDid(String[] did) {
        this.did = did;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(Integer activationStatus) {
        this.activationStatus = activationStatus;
    }

    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "KnXDMLITaskDetail{" +
                " did=" + Arrays.toString(did) +
                ", serviceType=" + serviceType +
                ", deliveryType=" + deliveryType +
                ", activationStatus=" + activationStatus +
                ", locationType=" + locationType +
                ", timestamp=" + timestamp +
                '}';
    }
}
