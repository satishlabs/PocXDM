/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dto;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkDTO.java
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
public class KnBulkDTO extends KnBulkOrderDTO {

    private static final long serialVersionUID = 478502475272299833L;

    private int bulkOrderId;
    private long insertionTime;
    private Integer status;
    private Long completionTime;
    private int bulkOrderType;
    private boolean rollback = false;
    private int priority;

    public KnBulkDTO() {
    }


    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    /**
     * @return the bulkOrderId
     */
    public int getBulkOrderId() {
        return bulkOrderId;
    }

    /**
     * @param bulkOrderId the bulkOrderId to set
     */
    public void setBulkOrderId(int bulkOrderId) {
        this.bulkOrderId = bulkOrderId;
    }

    /**
     * @return the insertionTime
     */
    public long getInsertionTime() {
        return insertionTime;
    }

    /**
     * @param insertionTime the insertionTime to set
     */
    public void setInsertionTime(long insertionTime) {
        this.insertionTime = insertionTime;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }


    /**
     * @return the completionTime
     */
    public Long getCompletionTime() {
        return completionTime;
    }

    /**
     * @param completionTime the completionTime to set
     */
    public void setCompletionTime(Long completionTime) {
        this.completionTime = completionTime;
    }


    /**
     * @return the bulkOrderType
     */
    public int getBulkOrderType() {
        return bulkOrderType;
    }

    /**
     * @param bulkOrderType the bulkOrderType to set
     */
    public void setBulkOrderType(int bulkOrderType) {
        this.bulkOrderType = bulkOrderType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


}
