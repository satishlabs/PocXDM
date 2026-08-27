/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/


/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/24/10       7.0
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
 * *******************************************************************************
 */
package com.kodiak.common.exception;

public class KnSystemError extends Error {

    /** stores the error code */
    private String errorCode;

    /** stores the error message */
    private String errorMessage;

    private StringBuffer buffer = new StringBuffer(500);

    /**
     * this will accept error code and error message and
     * constructs an KnSystemException
     *
     * @param errorCode    error code for the error
     * @param errorMessage message for logging
     */
    public KnSystemError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * This will create KnSystemException using error code, error message and
     * another Exception object. This constructor will be used if another
     * exception should be encapsulated with by KnSystemException.
     *
     * @param errorCode    error code
     * @param errorMessage message for logging
     * @param root         root of an another exception
     */
    public KnSystemError(String errorCode, String errorMessage, Exception root) {
        super(root);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * This method will return the error code
     *
     * @return error code of the exception
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * This method will return the error message
     *
     * @return error message of the exception
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * This method will set the error code
     *
     * @param errorCode error code of the error
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * This method will set the error messages
     *
     * @param errorMessage error message string
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * This method will return the message of the exception
     * @return the message
     */
    public String getMessage() {
        buffer.setLength(0);
        buffer.append("Error Code :").append(errorCode);
        buffer.append(", Error Message : ").append(errorMessage);
        return buffer.toString();
    }
}
