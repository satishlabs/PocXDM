/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by schandra on 19-12-2017.
 */
public class KnTargetMDNInfoDTO implements IIdentifier {

    private String targetMdn;
    private String featurePermissions;
    private String featureStatus;


    public String getTargetMdn() {
        return targetMdn;
    }

    public void setTargetMdn(String targetMdn) {
        this.targetMdn = targetMdn;
    }

    public String getFeaturePermissions() {
        return featurePermissions;
    }

    public void setFeaturePermissions(String featurePermissions) {
        this.featurePermissions = featurePermissions;
    }

    public String getFeatureStatus() {
        return featureStatus;
    }

    public void setFeatureStatus(String featureStatus) {
        this.featureStatus = featureStatus;
    }

    public String getObjectId() {
        return targetMdn;
    }

    @Override
    public String toString() {
        return "KnTargetMDNInfoDTO{" +
                "targetMdn='" + KnGDPRTemplate.mdn(targetMdn) + '\'' +
                ", featurePermissions='" + featurePermissions + '\'' +
                ", featureStatus='" + featureStatus + '\'' +
                '}';
    }
}
