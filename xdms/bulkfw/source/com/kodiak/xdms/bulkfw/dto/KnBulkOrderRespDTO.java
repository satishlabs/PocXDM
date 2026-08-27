/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dto;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkOrderRespDTO.java
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
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.REQUEST_STATUS;

public class KnBulkOrderRespDTO implements IIdentifier {

    private static final long serialVersionUID = 4044925131216663980L;

    private REQUEST_STATUS reqStatus;
    private Integer bulkOrderId;
    private String responseMsg;

    /**
     * @return the reqStatus
     */
    public REQUEST_STATUS getReqStatus() {
        return reqStatus;
    }

    /**
     * @param reqStatus the reqStatus to set
     */
    public void setReqStatus(REQUEST_STATUS reqStatus) {
        this.reqStatus = reqStatus;
    }

    /**
     * @return the bulkOrderId
     */
    public Integer getBulkOrderId() {
        return bulkOrderId;
    }

    /**
     * @param bulkOrderId the bulkOrderId to set
     */
    public void setBulkOrderId(Integer bulkOrderId) {
        this.bulkOrderId = bulkOrderId;
    }

    /**
     * @return the responseMsg
     */
    public String getResponseMsg() {
        return responseMsg;
    }

    /**
     * @param responseMsg the responseMsg to set
     */
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }


    public String getObjectId() {
        return String.valueOf(this.bulkOrderId);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KnBulkOrderRespDTO [reqStatus - ");
        builder.append(reqStatus);
        builder.append(", bulkOrderId - ");
        builder.append(bulkOrderId);
        builder.append(", responseMsg - ");
        builder.append(responseMsg);
        builder.append("]");
        return builder.toString();
    }

}
