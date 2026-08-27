/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.common.dto;

public class KnProcessInfoDTO {

    private String entityAction;

    private String entityId;

    private String entityOffSet;

    private String processedEntity;

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


    public String getProcessedEntity() {
        return processedEntity;
    }

    public void setProcessedEntity(String processedEntity) {
        this.processedEntity = processedEntity;
    }

    @Override
    public String toString() {
        return "KnProcessInfoDTO{" +
                "entityAction='" + entityAction + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityOffSet='" + entityOffSet + '\'' +
                ", processedCount='" + processedCount + '\'' +
                '}';
    }
}
