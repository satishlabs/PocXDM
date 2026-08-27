/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  ***********************************************************************
 *  File name:  KnCorpFeatureBitInfoDTO.java
 *  Subsystem:  PoCXDM
 *
 *    Name                 	    Date         	                  Release
 *    --------------------   -----------------------  -------------------------
 *    Chandrashekar HS          06/02/20, 12:39 PM                    10.0
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.motorolasolutions.com
 *  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of KodiakMotorola Solutions, Inc.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of the license agreement you entered into with Kodiak Motorola Solutions.
 *   ***********************************************************************
 */

package com.kodiak.common.commdto.common;

public class KnCorpFeatureBitInfoDTO {
    int corpId;
    long corpFs1;
    long opsCorpFs1;
    String corpFs2;
    String opsCorpFs2;
    String featureReleaseVersion;
    String xdmCorpfs2_set;

    public String getFeatureReleaseVersion() {
        return featureReleaseVersion;
    }

    public void setFeatureReleaseVersion(String featureReleaseVersion) {
        this.featureReleaseVersion = featureReleaseVersion;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public long getCorpFs1() {
        return corpFs1;
    }

    public void setCorpFs1(long corpFs1) {
        this.corpFs1 = corpFs1;
    }

    public long getOpsCorpFs1() {
        return opsCorpFs1;
    }

    public void setOpsCorpFs1(long opsCorpFs1) {
        this.opsCorpFs1 = opsCorpFs1;
    }

    public String getCorpFs2() {
        return corpFs2;
    }

    public void setCorpFs2(String corpFs2) {
        this.corpFs2 = corpFs2;
    }

    public String getOpsCorpFs2() {
        return opsCorpFs2;
    }

    public void setOpsCorpFs2(String opsCorpFs2) {
        this.opsCorpFs2 = opsCorpFs2;
    }

    public String getXdmCorpfs2_set() {
        return xdmCorpfs2_set;
    }

    public void setXdmCorpfs2_set(String xdmCorpfs2_set) {
        this.xdmCorpfs2_set = xdmCorpfs2_set;
    }

    @Override
    public String toString() {
        return "KnCorpFeatureBitInfoDTO{" +
                "corpId=" + corpId +
                ", corpFs1=" + corpFs1 +
                ", opsCorpFs1=" + opsCorpFs1 +
                ", corpFs2='" + corpFs2 + '\'' +
                ", opsCorpFs2='" + opsCorpFs2 + '\'' +
                ",xdmCorpfs2_set='" + xdmCorpfs2_set + '\'' +
                ", featureReleaseVersion='" + featureReleaseVersion + '\'' +
                '}';
    }
}
