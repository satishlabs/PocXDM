/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

/*
 *
 * ***********************************************************************
 *  File name:  KnKsekVaultInfo.java
 *  Subsystem:  XDM
 *
 *  Name                 	Date         	   Release
 *  -------------------- -------------------- -------------------------------
 *  Shashank             15/03/21      11.2
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.kodiakptt.com
 *  All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of Kodiak
 *  Networks, Inc. You shall not disclose such confidential information and
 *  shall use it only in accordance with the terms of the license agreement
 *  you entered into with Kodiak Networks.
 * ***********************************************************************
 */

public class KnKsekVaultInfo {
    private String ksek;
    private String ksekId;
    private String iv;
    private Boolean status;
    private Long validFrom;
    private Long validTo;
    private Long recordCreationTime;
    private Long recordUpdateTime;


    public String getKsek() {
        return ksek;
    }

    public void setKsek(String ksek) {
        this.ksek = ksek;
    }

    public String getKsekId() {
        return ksekId;
    }

    public void setKsekId(String ksekId) {
        this.ksekId = ksekId;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    public Long getValidTo() {
        return validTo;
    }

    public void setValidTo(Long validTo) {
        this.validTo = validTo;
    }

    public Long getRecordCreationTime() {
        return recordCreationTime;
    }

    public void setRecordCreationTime(Long recordCreationTime) {
        this.recordCreationTime = recordCreationTime;
    }

    public Long getRecordUpdateTime() {
        return recordUpdateTime;
    }

    public void setRecordUpdateTime(Long recordUpdateTime) {
        this.recordUpdateTime = recordUpdateTime;
    }

    @Override
    public String toString() {
        return "KnKsekVaultInfo{" +
                "ksek='" + ksek + '\'' +
                ", ksekId='" + ksekId + '\'' +
                ", iv='" + iv + '\'' +
                ", status=" + status +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", recordCreationTime=" + recordCreationTime +
                ", recordUpdateTime=" + recordUpdateTime +
                '}';
    }
}
