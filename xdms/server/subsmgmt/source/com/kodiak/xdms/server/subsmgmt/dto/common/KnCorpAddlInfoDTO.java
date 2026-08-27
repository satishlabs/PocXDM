/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

/**
 * Created by hanwar on 24-02-2017.
 */
public class KnCorpAddlInfoDTO {

    private static final long serialVersionUID = 7526471155622676252L;

    private Integer corpId;
    private String drxValueList;
    private Integer timeSlotType;
    private Integer drxValue;
    private Integer drxThreshCount;
    private Integer drxMO;
    private Integer drxMT;
    private Integer callHistoryDuration;
    private Integer dataPurgeDuration;
    private Long fixedtsStartTime;
    private Long fixedtsEndTime;


    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getDrxValueList() {
        return drxValueList;
    }

    public void setDrxValueList(String drxValueList) {
        this.drxValueList = drxValueList;
    }

    public Integer getTimeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(Integer timeSlotType) {
        this.timeSlotType = timeSlotType;
    }

    public Integer getDrxValue() {
        return drxValue;
    }

    public void setDrxValue(Integer drxValue) {
        this.drxValue = drxValue;
    }

    public Integer getDrxThreshCount() {
        return drxThreshCount;
    }

    public void setDrxThreshCount(Integer drxThreshCount) {
        this.drxThreshCount = drxThreshCount;
    }

    public Integer getDrxMO() {
        return drxMO;
    }

    public void setDrxMO(Integer drxMO) {
        this.drxMO = drxMO;
    }

    public Integer getDrxMT() {
        return drxMT;
    }

    public void setDrxMT(Integer drxMT) {
        this.drxMT = drxMT;
    }

    public Integer getCallHistoryDuration() {
        return callHistoryDuration;
    }

    public void setCallHistoryDuration(Integer callHistoryDuration) {
        this.callHistoryDuration = callHistoryDuration;
    }

    public Integer getDataPurgeDuration() {
        return dataPurgeDuration;
    }

    public void setDataPurgeDuration(Integer dataPurgeDuration) {
        this.dataPurgeDuration = dataPurgeDuration;
    }

    public Long getfixedtsStartTime() {
        return fixedtsStartTime;
    }

    public void setfixedtsStartTime(Long fixedtsStartTime) {
        this.fixedtsStartTime = fixedtsStartTime;
    }

    public Long getFixedtsEndTime() {
        return fixedtsEndTime;
    }

    public void setFixedtsEndTime(Long fixedtsEndTime) {
        this.fixedtsEndTime = fixedtsEndTime;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append("[KnCorpAddlInfoDTO --> ");
        strBuffer.append("corpId - ").append(corpId)
                .append(", timeSlotType - ").append(timeSlotType)
                .append(", drxValueList - ").append(drxValueList)
                .append(", drxThreshCount - ").append(drxThreshCount)
                .append(", drxValue - ").append(drxValue)
                .append(", drxMO - ").append(drxMO)
                .append(", drxMT - ").append(drxMT)
                .append(", callHistoryDuration - ").append(callHistoryDuration)
                .append(", dataPurgeDuration - ").append(dataPurgeDuration)
                .append(", fixedtsStartTime - ").append(fixedtsStartTime)
                .append(", fixedtsEndTime - ").append(fixedtsEndTime)
                .append("]");
        return strBuffer.toString();
    }
}
