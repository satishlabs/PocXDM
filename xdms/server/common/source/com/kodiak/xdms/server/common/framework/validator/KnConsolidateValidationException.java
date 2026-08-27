/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnConsolidateValidationException.java
 * Subsystem:   Validator Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-03-2007 6.0
 *
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
 **************************************************************************/
package com.kodiak.xdms.server.common.framework.validator;



import com.kodiak.logger.KnLogger;

import java.util.Collection;
import java.util.Map;

/**
 * KnConsolidateValidationException class
 * This Exception class is for consolidating all the validation errors and throws all the validation
 * Exceptions all together as per the validation rules configuration.This may consolidates all the validation error
 * objects for all dto's exception thrown for a rule and also may extends to all the rules for an operation.
 */
public class KnConsolidateValidationException extends KnValidationException {
	private static final KnLogger knLogger = KnLogger.getLogger(KnConsolidateValidationException.class);

    public static final String CLASS = KnConsolidateValidationException.class.getName();
    private boolean errorOccured = false;
    private KnConsolidatedErrorObject consolidatedErrorObject = null;

    /**
     * Allocates new KnValidationException object.
     *
     * @param errorCode    error code for the error
     * @param errorMessage string representation of the error message
     * @param rule         Validation rule|expression.
     */
    public KnConsolidateValidationException(String errorCode, String errorMessage, String rule) {
        super(errorCode, errorMessage, rule);
        consolidatedErrorObject = new KnConsolidatedErrorObject(rule, errorCode);
    }

    /**
     * Allocates new KnValidationException object.
     *
     * @param errorCode    error code for the error
     * @param errorMessage string representation of the error message
     * @param rule         Validation rule|expression.
     */
    public KnConsolidateValidationException(String errorCode, String errorMessage, String rule, String errorType) {
        super(errorCode, errorMessage, rule, errorType);
        //setErrorType(errorType);
        consolidatedErrorObject = new KnConsolidatedErrorObject(rule, errorCode, null, errorType);
    }

    /**
     * Adds the validation error object by extracting info from Validation Exception.A New validationErrorObject
     * will be created for each unique combination of validation rule, error code & failed data type.
     *
     * @param vex
     */
    public void addValidationError(KnValidationException vex) {
        String validationRule = vex.getValidationRule();
        String errorCode = vex.getErrorCode();
        String dataType = vex.getKeyDataType();
        String errorType = vex.getErrorType();
        KnValidationErrorObject errorObject = null;
        errorObject = consolidatedErrorObject.contains(validationRule, errorCode, dataType, errorType);
        //Checking the exception thrown is an instance of KnConsolidateValidationException. If true creating a corr.
        //consolidatederror object and add all validation errors to it.If the validation error object is already present, it will
        //add the entire validation error objects to the existing  one.
        if (vex instanceof KnConsolidateValidationException) {
            if (errorObject == null)
                errorObject = new KnConsolidatedErrorObject(validationRule, errorCode, dataType, errorType);
            errorObject.setErrorMessage(((KnConsolidateValidationException) vex).getErrorMessage());
            ((KnConsolidatedErrorObject) errorObject).addErrorObjects(((KnConsolidateValidationException) vex).getAllValidationErrors());
        } else {
            // Checking the exception thrown is an instance of KnValidationException. If true creating a corr.
            // validation error object and add all failed data to it. If the validation error object is already present, it will
            // add the entire failed data to the existing  one.
            if (errorObject == null)
                errorObject = new KnValidationErrorObject(validationRule, errorCode, dataType, errorType);
            errorObject.addTofailedData(vex.getKeyDataValue(), vex.getFailedRuleValue());
            errorObject.setErrorMessage(vex.getErrorMessage());
            knLogger.debug( "addValidationError", "Failed Data : " + errorObject.getKeyDataList());
        }
        //TODO check the usage of getErrorObject
        //errorObject.setErrorObject(vex.getErrorObject());
        //If the error object != this add the error object to the consolidated list
        if (consolidatedErrorObject != errorObject)
            consolidatedErrorObject.addErrorObject(errorObject);
        errorOccured = true;
    }

    /**
     * Returns the consolidated error object which represents the consolidate exception
     *
     * @return
     */
    public KnConsolidatedErrorObject getConsolidatedErrorObject() {
        return consolidatedErrorObject;
    }

    /**
     * Returns the Map with the list of failed data for each error code.
     * The key will be the error code( the actual code (with out originator) of the error if any validation
     * rule failed) and the values is a collection of failed data for that error code.
     * This method will try to find all the failed data recursively for a given error code,
     * if there is any consolidated error objects present in the error objects list.
     *
     * @param withType boolean to append with error code.
     * @return Map of Error code Vs Collection of Failed data
     */
    public Map getErrorCodeVsFailedData(boolean withType) {
        return consolidatedErrorObject.getErrorCodeVsFailedData(withType);
    }

    /**
     * Returns the Map with the list of error codes thrown for each failed data.
     * The key will be the failed data (if failed for any of the rule) and the values is a collection
     * of error codes (the actual code (with out originator) thrown for validation failures) for that data.
     * This method will try to find all the errorcodes recursively for the given data,
     * if there is any consolidated error objects present in the error objects list.
     *
     * @param withType boolean to append with error code.
     * @return Map of Failed data Vs Collection of Error Codes
     */
    public Map getFailedDataVsErrorCodes(boolean withType) {
        return consolidatedErrorObject.getFailedDataVsErrorCodes(withType);
    }

    /**
     * returns the collection of validation error objects.
     *
     * @return
     */
    public Collection getAllValidationErrors() {
        return consolidatedErrorObject.getAllNestedErrors();
    }

    /**
     * Returns the collection of all failed datas for the given error code.
     * This method will try to find all the failed data recursively for the given error code,
     * if there is any consolidated error objects present in the error objects list. The error code can be with type
     * or error code itself.
     *
     * @param errorCode The error code
     * @return collection of failed data.
     */
    public Collection getAllFailedData(String errorCode) {
        return consolidatedErrorObject.getAllKeyData(errorCode);
    }

    /**
     * Returns the collection of all failed datas for all the rules which have been failed.
     * This method will try to find all the failed data recursively, if there is any
     * consolidated error objects present in the error objects list.
     *
     * @return collection of failed data.
     */
    public Collection getAllFailedData() {
        return consolidatedErrorObject.getAllKeyData();
    }

    /**
     * Returns the collection of all error codes (The actual code (without originator))
     * failed for different validation rules. If any consolidated error object is present , it will
     * add only the consolidated error object's code to the list.
     *
     * @param withType boolean to append type with error code
     * @return Collection of error codes
     */
    public Collection getAllErrorCodes(boolean withType) {
        return consolidatedErrorObject.getAllErrorCodes(withType);
    }

    /**
     * Returns the collection of all error codes (The actual code (without originator))
     * failed for different validation rules. This method will try to find all the errorcodes recursively,
     * if there is any consolidated error objects present in the error objects list. Apart from the recursive
     * error codes, this method will add consolidated error code also to the list.
     *
     * @return collection of error codes
     */
    public Collection getAllErrorCodesRecursively(boolean withType) {
        return consolidatedErrorObject.getAllErrorCodesRecursively(withType);
    }

    /**
     * Returns the collection of all error codes (The actual code (without originator))
     * failed for different validation rules. This method will try to find all the errorcodes recursively,
     * if there is any consolidated error objects present in the error objects list. Apart from the recursive
     * error codes, this method will add consolidated error code also to the list.
     *
     * @param withType boolean to append type with error code
     * @return collection of error codes
     */
    public Collection getAllErrorCodesForData(String failedData, boolean withType) {
        return consolidatedErrorObject.getAllErrorCodesForData(failedData, withType);
    }

    /**
     * Returns any error has added as part of the consolidated list
     *
     * @return
     */
    public boolean isErrorOccured() {
        return errorOccured;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", List of Validation Failures(Size:" +
                consolidatedErrorObject.getAllNestedErrors().size() + ") : " +
                consolidatedErrorObject.getAllNestedErrors();
    }
}
