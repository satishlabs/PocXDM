/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpProfileDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 24, 2011      7.0
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
package com.kodiak.xdms.server.common.dto.common;


public class KnQPPProfileInfoDTO extends KnProfileDTO {

    private static final long serialVersionUID = 7526471155622676171L;

    //Ext-CorpId for the corporate Profile which is referred by the CAT tool
    private Integer recordId;
    //This is the paired contactListId for the corp
    private Integer qpppackId;
    //This is the max CorporateList that a corporate can have
    private Integer apnId;
    //This is the max members per CorporateList that a corporate can have
    private Integer userMode;
    //This is the max Groups that a Corporate can have
    private Integer callTypeId;
    //This is the max Members Per Groups that a Corporate can have
    private Integer isDefault;

    private Integer qpppcrfprofileId;

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getQpppackId() {
        return qpppackId;
    }

    public void setQpppackId(Integer qpppackId) {
        this.qpppackId = qpppackId;
    }

    public Integer getApnId() {
        return apnId;
    }

    public void setApnId(Integer apnId) {
        this.apnId = apnId;
    }

    public Integer getUserMode() {
        return userMode;
    }

    public void setUserMode(Integer userMode) {
        this.userMode = userMode;
    }

    public Integer getCallTypeId() {
        return callTypeId;
    }

    public void setCallTypeId(Integer callTypeId) {
        this.callTypeId = callTypeId;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getQpppcrfprofileId() {
        return qpppcrfprofileId;
    }

    public void setQpppcrfprofileId(Integer qpppcrfprofileId) {
        this.qpppcrfprofileId = qpppcrfprofileId;
    }

    @Override
    public String toString() {
        return "KnQPPProfileInfoDTO{" +
                "recordId=" + recordId +
                ", qpppackId=" + qpppackId +
                ", apnId=" + apnId +
                ", userMode=" + userMode +
                ", callTypeId=" + callTypeId +
                ", isDefault=" + isDefault +
                ", qpppcrfprofileId=" + qpppcrfprofileId +
                '}';
    }
}
