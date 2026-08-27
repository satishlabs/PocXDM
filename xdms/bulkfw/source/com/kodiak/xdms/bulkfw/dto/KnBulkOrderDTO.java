/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dto;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkOrderDTO.java
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
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;

public class KnBulkOrderDTO implements IIdentifier {

    private static final long serialVersionUID = 6538099114235232782L;

    // stores the Bulk Order processing object details
    private Object bulkOrderObj;
    // stores the bulk order object version
    private String bulkOrderObjVersion;
    // stores the corporation Id
    private Integer corpId;
    // stores the change level
    private int changeLevel;
    //stores operation type
    private int operationType;

    private KnMessage bulkOrderReqObj;

    private IXDMResponseDTO bulkOrderRespObj;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public KnMessage getBulkOrderReqObj() {
        return bulkOrderReqObj;
    }

    public void setBulkOrderReqObj(KnMessage bulkOrderReqObj) {
        this.bulkOrderReqObj = bulkOrderReqObj;
    }

    public IXDMResponseDTO getBulkOrderRespObj() {
        return bulkOrderRespObj;
    }

    public void setBulkOrderRespObj(IXDMResponseDTO bulkOrderRespObj) {
        this.bulkOrderRespObj = bulkOrderRespObj;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    /**
     * @return the bulkOrderObj
     */
    public Object getBulkOrderObj() {
        return bulkOrderObj;
    }

    /**
     * @param bulkOrderObj the bulkOrderObj to set
     */
    public void setBulkOrderObj(Object bulkOrderObj) {
        this.bulkOrderObj = bulkOrderObj;
    }

    /**
     * @return the bulkOrderObjVersion
     */
    public String getBulkOrderObjVersion() {
        return bulkOrderObjVersion;
    }

    /**
     * @param bulkOrderObjVersion the bulOrderObjVersion to set
     */
    public void setBulkOrderObjVersion(String bulkOrderObjVersion) {
        if (bulkOrderObjVersion != null) {
            bulkOrderObjVersion = bulkOrderObjVersion.trim();
            if (bulkOrderObjVersion.equals("")) {
                bulkOrderObjVersion = null;
            }
        }
        this.bulkOrderObjVersion = bulkOrderObjVersion;
    }

    /**
     * @return the corpId
     */
    public Integer getCorpId() {
        return corpId;
    }

    /**
     * @param corpId the corpId to set
     */
    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    /**
     * @return the changeLevel
     */
    public int getChangeLevel() {
        return changeLevel;
    }

    /**
     * @param changeLevel the changeLevel to set
     */
    public void setChangeLevel(int changeLevel) {
        this.changeLevel = changeLevel;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KnBulkOrderDTO [");
        builder.append(" bulkOrderReqObj = ").append(bulkOrderReqObj);
        builder.append(", bulkOrderObjVersion = ").append(bulkOrderObjVersion);
        builder.append(", corpId = ").append(corpId);
        builder.append(", changeLevel = ").append(changeLevel);
        builder.append(", hierarchyType = ").append(hierarchyType);
        builder.append("]");
        return builder.toString();
    }

    public String getObjectId() {
        return this.bulkOrderObj.toString();
    }

}
