/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator;

import com.kodiak.xdms.server.common.framework.validator.KnValidationException;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubBOValidationException.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 15, 2011           7.0
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
public class KnPubBOValidationException extends KnValidationException {

    private String validationEntity;
    private String operation;

    /**
     * Creates KnBOValidationException object using error code, error message and validtion
     * rule.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param entity       the validating entity name
     * @param operation    the operation
     * @param rule         validation rule
     */
    public KnPubBOValidationException(String errorCode, String errorMessage, String entity,
                                     String operation, String rule, String keyDataValue) {
        super(errorCode, errorMessage, rule, keyDataValue, null);
        this.validationEntity = entity;
        this.operation = operation;
    }

    /**
     * Creates KnBOValidationException object using error code, error message and validtion
     * rule.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param entity       the validating entity name
     * @param operation    the operation
     * @param rule         validation rule
     * @param errorType    Error Type
     */
    public KnPubBOValidationException(String errorCode, String errorMessage, String entity,
                                     String operation, String rule, String keyDataValue, String errorType) {
        super(errorCode, errorMessage, rule, keyDataValue, errorType);
        this.validationEntity = entity;
        this.operation = operation;
    }

    /**
     * Creates KnBOValidationException object using error code, error message and validtion
     * rule.
     *
     * @param errorCode       error code of error
     * @param errorMessage    error message string
     * @param entity          the validating entity name
     * @param operation       the operation
     * @param rule            validation rule
     * @param failedRuleValue receive data
     */
    public KnPubBOValidationException(String errorCode, String errorMessage, String entity,
                                     String operation, String rule, String keyDataType, String keyDataValue, String failedRuleValue) {
        super(errorCode, errorMessage, rule, keyDataType, keyDataValue, failedRuleValue, null);
        this.validationEntity = entity;
        this.operation = operation;
    }

    /**
     * Creates KnBOValidationException object using error code, error message and validtion
     * rule.
     *
     * @param errorCode       error code of error
     * @param errorMessage    error message string
     * @param entity          the validating entity name
     * @param operation       the operation
     * @param rule            validation rule
     * @param failedRuleValue receive data
     * @param errorType       Error Type
     */
    public KnPubBOValidationException(String errorCode, String errorMessage, String entity,
                                     String operation, String rule, String keyDataType, String keyDataValue, String failedRuleValue, String errorType) {
        super(errorCode, errorMessage, rule, keyDataType, keyDataValue, failedRuleValue, errorType);
        this.validationEntity = entity;
        this.operation = operation;
    }

    /**
     * Creates KnBOValidationException object using error code, error message, validtion
     * rule and another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param entity       the validating entity name
     * @param operation    the operation
     * @param root         root of an another exception
     * @param rule         validation rule
     * @param data         receive data
     */
    public KnPubBOValidationException(String errorCode, String errorMessage, Exception root,
                                     String entity, String operation, String rule, String data) {
        super(errorCode, errorMessage, root, rule, data);
        this.validationEntity = entity;
        this.operation = operation;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Validation Entity : " + validationEntity + ", Operation : " + operation;
    }

}
