/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.common.dto;

public class KnProcessInfoIP {

    private String entityAction;

    private String entityId;

    private String entityOffSet;

    private String processedCount;

    public String getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(String entityAction) {
        this.entityAction = entityAction;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityOffSet() {
        return entityOffSet;
    }

    public void setEntityOffSet(String entityOffSet) {
        this.entityOffSet = entityOffSet;
    }

    public String getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(String processedCount) {
        this.processedCount = processedCount;
    }

    @Override
    public String toString() {
        return "KnProcessInfoIP{" +
                "entityAction='" + entityAction + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityOffSet='" + entityOffSet + '\'' +
                ", processedCount='" + processedCount + '\'' +
                '}';
    }
}
