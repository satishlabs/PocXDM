/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;

public class KnXDMEmergencyDestAttributes implements Serializable {
    private static final long serialVersionUID = 2411909754782005677L;

    private String destAttributeType;
    private String destAttributeUri;
    private String destAttributeCategory;

    /*public KnXDMEmergencyDestAttributes(String destAttributeType,
                                        String destAttributeUri, String destAttributeCategory) {
        this.destAttributeType = destAttributeType;
        this.destAttributeUri = destAttributeUri;
        this.destAttributeCategory = destAttributeCategory;
    }*/

    public String getDestAttributeType() {
        return destAttributeType;
    }

    public void setDestAttributeType(String destAttributeType) {
        this.destAttributeType = destAttributeType;
    }

    public String getDestAttributeUri() {
        return destAttributeUri;
    }

    public void setDestAttributeUri(String destAttributeUri) {
        this.destAttributeUri = destAttributeUri;
    }

    public String getDestAttributeCategory() {
        return destAttributeCategory;
    }

    public void setDestAttributeCategory(String destAttributeCategory) {
        this.destAttributeCategory = destAttributeCategory;
    }

    @Override
    public String toString() {
        return "KnXDMEmergencyDestAttributes{" +
                "destAttributeType='" + destAttributeType + '\'' +
                ", destAttributeUri='" + destAttributeUri + '\'' +
                ", destAttributeCategory='" + destAttributeCategory + '\'' +
                '}';
    }
}
