/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.io.Serializable;

/**
 * Created by schandra on 18-12-2017.
 */
public class KnTargetMdnInfoDTO implements Serializable {
    private String targetMdn;
    private String featurePermission;
    private String featureStatus;

    public String getTargetMdn() {
        return targetMdn;
    }

    public void setTargetMdn(String targetMdn) {
        this.targetMdn = targetMdn;
    }

    public String getFeaturePermission() {
        return featurePermission;
    }

    public void setFeaturePermission(String featurePermission) {
        this.featurePermission = featurePermission;
    }

    public String getFeatureStatus() {
        return featureStatus;
    }

    public void setFeatureStatus(String featureStatus) {
        this.featureStatus = featureStatus;
    }

    @Override
    public String toString() {
        return "KnTargetMdnInfoDTO{" +
                "targetMdn='" + KnGDPRTemplate.mdn(targetMdn) + '\'' +
                ", featurePermission='" + featurePermission + '\'' +
                ", featureStatus='" + featureStatus + '\'' +
                '}';
    }
}
