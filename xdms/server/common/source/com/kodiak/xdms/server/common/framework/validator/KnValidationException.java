/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnValidationException.java
 * Subsystem:   Business Exceptions
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14/03/2007 6.0
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 ***************************************************************************/
package com.kodiak.xdms.server.common.framework.validator;

import com.kodiak.xdms.server.common.framework.KnFWException;


/**
 * This is base class for all types of validation exceptions in business layer. It holds
 * the validation information like received data and validation rule. Based on the information
 * it will execute validation exceptions.
 */

public class KnValidationException extends KnFWException {

    /**
     * stores validation rule of error
     */
    private String validationRule;

    /**
     * stores data about error
     */
    private String keyDataValue;
    /**
     * The data which is failed to conform the validation rule.
     */
    private String failedRuleValue;
    /**
     * The data type of the failed data.
     */
    private String keyDataType;

    // stores the errorType
    private String errorType;

    /**
     * Allocates new KnValidationException object.
     *
     * @param errorCode    error code for the error
     * @param errorMessage string representation of the error message
     */
    public KnValidationException(String errorCode, String errorMessage, String rule) {
        super(errorCode, errorMessage);
        this.validationRule = rule;
    }

    /**
     * Allocates new KnValidationException object with failed dto details.
     *
     * @param errorCode    error code for the error
     * @param errorMessage string representation of the error message
     * @param rule         validation rule of the error
     * @param errorType    error Type
     */
    public KnValidationException(String errorCode, String errorMessage, String rule,
                                 String errorType) {
        super(errorCode, errorMessage);
        this.validationRule = rule;
        this.errorType = errorType;
    }

    /* Allocates new KnValidationException object with failed dto details.
     *
     * @param errorCode       error code for the error
     * @param errorMessage    string representation of the error message
     * @param rule            validation rule of the error
     * @param errorType       error Type
     */
    public KnValidationException(String errorCode, String errorMessage, String rule,
                                 String keyDataValue, String errorType) {
        super(errorCode, errorMessage);
        this.validationRule = rule;
        this.keyDataValue = keyDataValue;
        this.errorType = errorType;

    }

    /**
     * Allocates new KnValidationException object with failed dto details.
     *
     * @param errorCode       error code for the error
     * @param errorMessage    string representation of the error message
     * @param rule            validation rule of the error
     * @param failedRuleValue receive data
     */
    public KnValidationException(String errorCode, String errorMessage, String rule,
                                 String keyDataType, String keyDataValue, String failedRuleValue, String errorType) {
        super(errorCode, errorMessage);
        this.validationRule = rule;
        this.keyDataValue = keyDataValue;
        this.keyDataType = keyDataType;
        this.failedRuleValue = failedRuleValue;
        this.errorType = errorType;
    }

    /**
     * Allocates new KnValidationException object.
     *
     * @param errorCode    error code of the error
     * @param errorMessage string representation of the error message
     * @param root         root of an another exception
     * @param rule         validation rule
     * @param data         receive data
     */
    public KnValidationException(String errorCode, String errorMessage, Exception root,
                                 String rule, String data) {
        super(errorCode, errorMessage, root);
        this.validationRule = rule;
        this.keyDataValue = data;
    }

    /**
     * Returns the validation rule of the exception.
     *
     * @return validationRule   validation rule of an exception
     */
    public String getValidationRule() {
        return validationRule;
    }

    /**
     * Return the received date of the exception
     *
     * @return receivedDate data about the error
     */
    public String getKeyDataValue() {
        return keyDataValue;
    }

    /**
     * This method sets the receive data about the error
     *
     * @param keyDataValue received data about the error
     */
    public void setKeyDataValue(String keyDataValue) {
        this.keyDataValue = keyDataValue;
    }

    /**
     * Returns the failed data
     *
     * @return
     */
    public String getFailedRuleValue() {
        return failedRuleValue;
    }

    /**
     * Returns the failed data type
     *
     * @return
     */
    public String getKeyDataType() {
        return keyDataType;
    }

    /**
     * Sets the failed data type
     *
     * @param dataType
     */
    public void setKeyDataType(String dataType) {
        this.keyDataType = dataType;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * returns the errorCode with errorTyp.
     * @return String errorCode with errorTypes
     */
    public String getCodeWithType() {
        StringBuffer buffer = new StringBuffer(100);
        buffer.setLength(0);
        buffer.append(super.getErrorCode()); // changed getCode() to getErrorCode
        if (errorType != null) {
            buffer.append(".").append(errorType);
        }
        return buffer.toString();
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        if (validationRule == null)
            return super.getMessage();
        return super.getMessage() + ", Rule : " + validationRule + ", KeyDataValue : " + keyDataValue +
                ", KeyDataType : " + keyDataType + ", FailedRuleValue : " + failedRuleValue + ", ErrorType : " + errorType;
    }

}
