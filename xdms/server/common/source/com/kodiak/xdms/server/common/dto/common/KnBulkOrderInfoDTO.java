/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 5/3/13
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */

public class KnBulkOrderInfoDTO {

    private static final long serialVersionUID = 478502475272280021L;


    private int bulkOrderId;
    private int bulkOrderType;
    private int changeLevel;
    private Integer corpId;
    private Object bulkOrderReqObj;
    private String bulkOrderReqObjVersion;
    private Object bulkOrderRespObj;
    private Long insertionTime;
    private Integer status;
    private Long completionTime;

    public int getBulkOrderId() {
        return bulkOrderId;
    }

    public void setBulkOrderId(int bulkOrderId) {
        this.bulkOrderId = bulkOrderId;
    }

    public int getBulkOrderType() {
        return bulkOrderType;
    }

    public void setBulkOrderType(int bulkOrderType) {
        this.bulkOrderType = bulkOrderType;
    }

    public int getChangeLevel() {
        return changeLevel;
    }

    public void setChangeLevel(int changeLevel) {
        this.changeLevel = changeLevel;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Object getBulkOrderReqObj() {
        return bulkOrderReqObj;
    }

    public void setBulkOrderReqObj(Object bulkOrderReqObj) {
        this.bulkOrderReqObj = bulkOrderReqObj;
    }

    public String getBulkOrderReqObjVersion() {
        return bulkOrderReqObjVersion;
    }

    public void setBulkOrderReqObjVersion(String bulkOrderReqObjVersion) {
        this.bulkOrderReqObjVersion = bulkOrderReqObjVersion;
    }

    public Object getBulkOrderRespObj() {
        return bulkOrderRespObj;
    }

    public void setBulkOrderRespObj(Object bulkOrderRespObj) {
        this.bulkOrderRespObj = bulkOrderRespObj;
    }

    public Long getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Long insertionTime) {
        this.insertionTime = insertionTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Long completionTime) {
        this.completionTime = completionTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("KnBulkOrderInfoDTO [bulkOrderId=");
        builder.append(bulkOrderId);
        builder.append(", insertionTime=").append(insertionTime);
        builder.append(", status=").append(status);
        builder.append(", completionTime=").append(completionTime);
        builder.append(", bulkOrderType=").append(bulkOrderType);
        builder.append("]");
        return builder.toString();
    }


}
