/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dto;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnCorpGroupDistInfoDAO;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnBatchDTO.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
public class KnBatchDTO {

    private int batchExeId;
    private int opertaionType;
    private int bulkOrderId;
    private long startMdn;
    private long endMdn;
    private long cycleMdn;
    private long newCycleMdn;
    private int status;
    private int batchExeType;
    private int clientType;
    private int batchSize;

    public int getBatchExeId() {
        return batchExeId;
    }

    public void setBatchExeId(int batchExeId) {
        this.batchExeId = batchExeId;
    }

    public int getOpertaionType() {
        return opertaionType;
    }

    public void setOpertaionType(int opertaionType) {
        this.opertaionType = opertaionType;
    }

    public int getBulkOrderId() {
        return bulkOrderId;
    }

    public void setBulkOrderId(int bulkOrderId) {
        this.bulkOrderId = bulkOrderId;
    }

    public long getStartMdn() {
        return startMdn;
    }

    public void setStartMdn(long startMdn) {
        this.startMdn = startMdn;
    }

    public long getEndMdn() {
        return endMdn;
    }

    public void setEndMdn(long endMdn) {
        this.endMdn = endMdn;
    }

    public long getCycleMdn() {
        return cycleMdn;
    }

    public void setCycleMdn(long cycleMdn) {
        this.cycleMdn = cycleMdn;
    }

    public long getNewCycleMdn() {
        return newCycleMdn;
    }

    public void setNewCycleMdn(long newCycleMdn) {
        this.newCycleMdn = newCycleMdn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBatchExeType() {
        return batchExeType;
    }

    public void setBatchExeType(int batchExeType) {
        this.batchExeType = batchExeType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return "KnProvBatchJobDTO{" +
                "batchExeId=" + batchExeId +
                ", opertaionType=" + opertaionType +
                ", bulkOrderId=" + bulkOrderId +
                ", startMdn=" + KnGDPRTemplate.mdn(String.valueOf(startMdn)) +
                ", endMdn=" + KnGDPRTemplate.mdn(String.valueOf(endMdn)) +
                ", cycleMdn=" + KnGDPRTemplate.mdn(String.valueOf(cycleMdn)) +
                ", newCycleMdn=" + KnGDPRTemplate.mdn(String.valueOf(newCycleMdn)) +
                ", status=" + status +
                ", batchExeType=" + batchExeType +
                ", clientType=" + clientType +
                ", batchSize=" + batchSize +
                '}';
    }
}
