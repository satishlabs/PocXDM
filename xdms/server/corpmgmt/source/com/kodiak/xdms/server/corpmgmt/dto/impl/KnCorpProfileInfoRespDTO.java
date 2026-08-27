/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpProfileInfoRespDTO.java
 * Subsystem:  WebApps
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 13, 2017               8.3
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */


public class KnCorpProfileInfoRespDTO extends KnCorpResponseDTO {

    private String corpId;
    private String extCorpId;
    private String corpName;
    private String maxTextMsgSize;
    private String maxMmmsgSizeCell;
    private String maxMmmsgSizeWifi;
    private String deliveryReceiptFlag;
    private String readReportFlag;
    private String msgTtl;
    private String maxPredefinedMsgCnt;
    private String maxPredefinedTmpltCnt;
    private String maxUserDefinedMsgCnt;
    private String fleetMemberGeoTagFlag;
    private String maxAbdgTalkGroup;
    private String maxLrGabTalkGroup;
    private String maxUsrLrGabGroup;
    private String maxAbdgGrpPerOwner;
    private String maxAbdgGrpPerMem;
    private String lastProfileUpdateTime;
    private Integer lmrIntropFlag;
    private Integer ugwInteropFlag;
    private String xdmCorpFs2Set;

    public Integer getUgwInteropFlag() {
        return ugwInteropFlag;
    }

    public void setUgwInteropFlag(Integer ugwInteropFlag) {
        this.ugwInteropFlag = ugwInteropFlag;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getMaxTextMsgSize() {
        return maxTextMsgSize;
    }

    public void setMaxTextMsgSize(String maxTextMsgSize) {
        this.maxTextMsgSize = maxTextMsgSize;
    }

    public String getMaxMmmsgSizeCell() {
        return maxMmmsgSizeCell;
    }

    public void setMaxMmmsgSizeCell(String maxMmmsgSizeCell) {
        this.maxMmmsgSizeCell = maxMmmsgSizeCell;
    }

    public String getMaxMmmsgSizeWifi() {
        return maxMmmsgSizeWifi;
    }

    public void setMaxMmmsgSizeWifi(String maxMmmsgSizeWifi) {
        this.maxMmmsgSizeWifi = maxMmmsgSizeWifi;
    }

    public String getDeliveryReceiptFlag() {
        return deliveryReceiptFlag;
    }

    public void setDeliveryReceiptFlag(String deliveryReceiptFlag) {
        this.deliveryReceiptFlag = deliveryReceiptFlag;
    }

    public String getMsgTtl() {
        return msgTtl;
    }

    public void setMsgTtl(String msgTtl) {
        this.msgTtl = msgTtl;
    }

    public String getMaxPredefinedMsgCnt() {
        return maxPredefinedMsgCnt;
    }

    public void setMaxPredefinedMsgCnt(String maxPredefinedMsgCnt) {
        this.maxPredefinedMsgCnt = maxPredefinedMsgCnt;
    }

    public String getMaxPredefinedTmpltCnt() {
        return maxPredefinedTmpltCnt;
    }

    public void setMaxPredefinedTmpltCnt(String maxPredefinedTmpltCnt) {
        this.maxPredefinedTmpltCnt = maxPredefinedTmpltCnt;
    }

    public String getMaxUserDefinedMsgCnt() {
        return maxUserDefinedMsgCnt;
    }

    public void setMaxUserDefinedMsgCnt(String maxUserDefinedMsgCnt) {
        this.maxUserDefinedMsgCnt = maxUserDefinedMsgCnt;
    }

    public String getFleetMemberGeoTagFlag() {
        return fleetMemberGeoTagFlag;
    }

    public void setFleetMemberGeoTagFlag(String fleetMemberGeoTagFlag) {
        this.fleetMemberGeoTagFlag = fleetMemberGeoTagFlag;
    }

    public String getReadReportFlag() {
        return readReportFlag;
    }

    public void setReadReportFlag(String readReportFlag) {
        this.readReportFlag = readReportFlag;
    }

    public String getMaxAbdgTalkGroup() {
        return maxAbdgTalkGroup;
    }

    public void setMaxAbdgTalkGroup(String maxAbdgTalkGroup) {
        this.maxAbdgTalkGroup = maxAbdgTalkGroup;
    }

    public String getMaxLrGabTalkGroup() {
        return maxLrGabTalkGroup;
    }

    public void setMaxLrGabTalkGroup(String maxLrGabTalkGroup) {
        this.maxLrGabTalkGroup = maxLrGabTalkGroup;
    }

    public String getMaxUsrLrGabGroup() {
        return maxUsrLrGabGroup;
    }

    public void setMaxUsrLrGabGroup(String maxUsrLrGabGroup) {
        this.maxUsrLrGabGroup = maxUsrLrGabGroup;
    }

    public String getMaxAbdgGrpPerOwner() {
        return maxAbdgGrpPerOwner;
    }

    public void setMaxAbdgGrpPerOwner(String maxAbdgGrpPerOwner) {
        this.maxAbdgGrpPerOwner = maxAbdgGrpPerOwner;
    }

    public String getMaxAbdgGrpPerMem() {
        return maxAbdgGrpPerMem;
    }

    public void setMaxAbdgGrpPerMem(String maxAbdgGrpPerMem) {
        this.maxAbdgGrpPerMem = maxAbdgGrpPerMem;
    }

    public String getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(String lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    public Integer getLmrIntropFlag() {
        return lmrIntropFlag;
    }

    public void setLmrIntropFlag(Integer lmrIntropFlag) {
        this.lmrIntropFlag = lmrIntropFlag;
    }

    public String getXdmCorpFs2Set() {
        return xdmCorpFs2Set;
    }
    public void setXdmCorpFs2Set(String xdmCorpFs2Set) {
        this.xdmCorpFs2Set = xdmCorpFs2Set;
    }

    @Override
    public String toString() {
        return "KnCorpProfileInfoRespDTO{" +
                "corpId='" + corpId + '\'' +
                ", extCorpId='" + extCorpId + '\'' +
                ", corpName='" + corpName + '\'' +
                ", maxTextMsgSize='" + maxTextMsgSize + '\'' +
                ", maxMmmsgSizeCell='" + maxMmmsgSizeCell + '\'' +
                ", maxMmmsgSizeWifi='" + maxMmmsgSizeWifi + '\'' +
                ", deliveryReceiptFlag='" + deliveryReceiptFlag + '\'' +
                ", readReportFlag='" + readReportFlag + '\'' +
                ", msgTtl='" + msgTtl + '\'' +
                ", maxPredefinedMsgCnt='" + maxPredefinedMsgCnt + '\'' +
                ", maxPredefinedTmpltCnt='" + maxPredefinedTmpltCnt + '\'' +
                ", maxUserDefinedMsgCnt='" + maxUserDefinedMsgCnt + '\'' +
                ", fleetMemberGeoTagFlag='" + fleetMemberGeoTagFlag + '\'' +
                ", maxAbdgTalkGroup='" + maxAbdgTalkGroup + '\'' +
                ", maxLrGabTalkGroup='" + maxLrGabTalkGroup + '\'' +
                ", maxUsrLrGabGroup='" + maxUsrLrGabGroup + '\'' +
                ", maxAbdgGrpPerOwner='" + maxAbdgGrpPerOwner + '\'' +
                ", maxAbdgGrpPerMem='" + maxAbdgGrpPerMem + '\'' +
                ", lastProfileUpdateTime='" + lastProfileUpdateTime + '\'' +
                ", lmrIntropFlag='" + lmrIntropFlag + '\'' +
                ", ugwInteropFlag='" + ugwInteropFlag + '\'' +
                ", xdmCorpFs2Set='" + xdmCorpFs2Set + '\'' +
                '}';
    }
}
