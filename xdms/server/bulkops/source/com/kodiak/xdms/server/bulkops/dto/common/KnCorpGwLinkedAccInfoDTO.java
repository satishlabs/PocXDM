/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.bulkops.dto.common;

/**
 * Created by kdeepak on 27-04-2015.
 */
public class KnCorpGwLinkedAccInfoDTO {
    private Integer gwLinkedId ;
    private Integer nniGwAccId;
    private String corpNNIRefId;
    private long lastUpdateTime;

    public Integer getGwLinkedId() {
        return gwLinkedId;
    }

    public void setGwLinkedId(Integer gwLinkedId) {
        this.gwLinkedId = gwLinkedId;
    }

    public Integer getNniGwAccId() {
        return nniGwAccId;
    }

    public void setNniGwAccId(Integer nniGwAccId) {
        this.nniGwAccId = nniGwAccId;
    }

    public String getCorpNNIRefId() {
        return corpNNIRefId;
    }

    public void setCorpNNIRefId(String corpNNIRefId) {
        this.corpNNIRefId = corpNNIRefId;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
