/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnResponseDTO.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
package com.kodiak.library.activation.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.library.activation.dto.IResponseDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.KnXDMError;

/**
 *
 */
public class KnResponseDTO implements IResponseDTO {

    private static final long serialVersionUID = 7526471155622676271L;

    // Response status code from Request Response Framework.
    private int responseCode;
    // Owner Mdn
    private String mdn;
    // Message Id from the RTX
    private String messageId;
    // Operation Status from the RTX
    private String responseStatus;
    // Transaction Id, Unique number
    private String transactionId;
    // feature Id of the operation
    private String featureId;
    // Xcap Root URI of the mdn
    private String xcapRootURI;
    // XUI of the mdn
    private String xui;
    // stores the status of dto
    private int dtoStatus = -1;
    // holds the error object
    private KnXDMError errorObject = null;
    private String objectId = null;
    StringBuffer buffer = new StringBuffer(500);

    /**
     *
     */
    public KnResponseDTO() {

    }

    /**
     * @return
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * @param mdn
     */
    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    /**
     * @return
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return
     */
    public String getResponseStatus() {
        return responseStatus;
    }

    /**
     * @param responseStatus
     */
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * @return
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return
     */
    public String getFeatureId() {
        return featureId;
    }

    /**
     * @param featureId
     */
    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    /**
     * @return
     */
    public String getXcapRootURI() {
        return xcapRootURI;
    }

    /**
     * @param xcapRootURI
     */
    public void setXcapRootURI(String xcapRootURI) {
        this.xcapRootURI = xcapRootURI;
    }

    /**
     * @return
     */
    public String getXui() {
        return xui;
    }

    /**
     * @param xui
     */
    public void setXui(String xui) {
        this.xui = xui;
    }

    /**
     * @return
     */
    public int getDTOStatus() {
        return dtoStatus;
    }

    /**
     * @param dtoStatus
     */
    public void setDTOStatus(int dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    /**
     * @return
     */
    public KnXDMError getErrorObject() {
        return errorObject;
    }

    /**
     * @param errorObject
     */
    public void setErrorObject(KnXDMError errorObject) {
        this.errorObject = errorObject;
    }

    /**
     * This method populate the dto object
     *
     * @param dtoObject
     */
    public void populate(IPopulate dtoObject) {
    }

    /**
     * @return
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String toString() {
        buffer.setLength(0);
        buffer.append("Response Code : ").append(responseCode)
                .append(", MDN : ").append(KnGDPRTemplate.mdn(mdn))
                .append(", Message Id : ").append(messageId)
                .append(", Response Status : ").append(responseStatus)
                .append(", Transaction Id : ").append(transactionId)
                .append(", Feature Id : ").append(featureId)
                .append(", Xcap Root Uri : ").append(xcapRootURI)
                .append(", Xui : ").append(xui);
        return buffer.toString();
    }
}
