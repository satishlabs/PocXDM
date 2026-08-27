/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnConsolidatedErrorObject.java
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

import java.util.*;

/**
 * KnConsolidatedErrorObject class
 */
public class KnConsolidatedErrorObject extends KnValidationErrorObject {
    private static final long serialVersionUID = 7526471155622676273L;

    private LinkedList nestedErrorObjects = new LinkedList<>();

    /**
     * Constructor with validation rule id & error code
     *
     * @param validationRule
     * @param errorCode
     */
    public KnConsolidatedErrorObject(String validationRule, String errorCode) {
        super(validationRule, errorCode);
    }

    /**
     * Constructor with error code, rule, data type
     *
     * @param validationRule
     * @param errorCode
     * @param keyDataType
     * @param errorType
     */
    public KnConsolidatedErrorObject(String validationRule, String errorCode, String keyDataType, String errorType) {
        super(validationRule, errorCode, keyDataType, errorType);
    }

    /**
     * Adds a nested error object to the consolidated list
     *
     * @param errorObject
     */
    public void addErrorObject(KnValidationErrorObject errorObject) {
        nestedErrorObjects.add(errorObject);
        hasNestedErrors = true;
    }

    /**
     * Add multiple error objects to the consolidated list
     *
     * @param errorObjects
     */
    public void addErrorObjects(Collection errorObjects) {
        nestedErrorObjects.addAll(errorObjects);
        hasNestedErrors = true;
    }

    /**
     * Checks the error object present in the consolidated list for the same validation rule, error code
     * and failed data type. If present returns the corresponding error object.
     *
     * @param validationRule
     * @param errorCode
     * @param keyDataType
     * @param errorType
     * @return
     */
    public KnValidationErrorObject contains(String validationRule, String errorCode, String keyDataType, String errorType) {
        if (this.equals(validationRule, errorCode, keyDataType, errorType))
            return this;
        for (Iterator iter = nestedErrorObjects.iterator(); iter.hasNext();) {
            KnValidationErrorObject errorObject = (KnValidationErrorObject) iter.next();
            if (errorObject.equals(validationRule, errorCode, keyDataType, errorType))
                return errorObject;
        }
        return null;
    }

    /**
     * Sets the list of nested error objects
     *
     * @param nestedErrorObjects
     */
    public void setNestedErrorObjects(LinkedList nestedErrorObjects) {
        this.nestedErrorObjects = nestedErrorObjects;
    }

    /**
     * returns the collection of nested validation error objects.
     *
     * @return
     */
    public Collection getAllNestedErrors() {
        return nestedErrorObjects;
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
        Map codeVsData = new HashMap(5);
        for (Iterator iter = getAllErrorCodesRecursively(withType).iterator(); iter.hasNext();) {
            String errorCode = (String) iter.next();
            codeVsData.put(errorCode, getAllKeyData(errorCode));
        }
        return codeVsData;
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
        Map dataVsCode = new HashMap(5);
        for (Iterator iter = getAllKeyData().iterator(); iter.hasNext();) {
            String data = (String) iter.next();
            dataVsCode.put(data, getAllErrorCodesForData(data, withType));
        }
        return dataVsCode;
    }

    /**
     * Returns the collection of all failed datas for the given error code.
     * This method will try to find all the failed data recursively for the given error code,
     * if there is any consolidated error objects present in the error objects list.
     *
     * @param errorCode The error code
     * @return collection of failed data.
     */
    public Collection getAllKeyData(String errorCode) {

        KnValidationErrorObject errorObject = null;
        Collection dataList = new HashSet(5);
        if (errorCode != null) {
            for (Iterator iter = nestedErrorObjects.iterator(); iter.hasNext();) {
                errorObject = (KnValidationErrorObject) iter.next();
                if (errorObject instanceof KnConsolidatedErrorObject) {
                    if (errorCode.equals(errorObject.getCode()) || errorCode.equals(errorObject.getCodeWithType()))
                        dataList.addAll((((KnConsolidatedErrorObject) errorObject)).getAllKeyData());
                    dataList.addAll((((KnConsolidatedErrorObject) errorObject)).getAllKeyData(errorCode));
                } else if (errorCode.equals(errorObject.getCode()) || errorCode.equals(errorObject.getCodeWithType()))
                    dataList.addAll(errorObject.getKeyDataList());
            }
        }
        return dataList;
    }

    /**
     * Returns the collection of all failed datas for all the rules which have been failed.
     * This method will try to find all the failed data recursively, if there is any
     * consolidated error objects present in the error objects list.
     *
     * @return collection of failed data.
     */
    public Collection getAllKeyData() {
        Collection dataList = new HashSet(5);
        KnValidationErrorObject errorObject = null;
        for (Iterator iter = nestedErrorObjects.iterator(); iter.hasNext();) {
            errorObject = (KnValidationErrorObject) iter.next();
            if (errorObject instanceof KnConsolidatedErrorObject) {
                dataList.addAll(((KnConsolidatedErrorObject) errorObject).getAllKeyData());
            } else
                dataList.addAll(errorObject.getKeyDataList());
        }
        return dataList;
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
        Collection codeList = new HashSet(5);
        KnValidationErrorObject errorObject = null;
        for (Iterator iter = nestedErrorObjects.iterator(); iter.hasNext();) {
            errorObject = (KnValidationErrorObject) iter.next();
            if (withType)
                codeList.add(errorObject.getCodeWithType());
            else
                codeList.add(errorObject.getCode());
        }
        return codeList;
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
    public Collection getAllErrorCodesRecursively(boolean withType) {
        Collection errorCodeList = new HashSet(5);
        KnValidationErrorObject errorObject = null;
        for (Iterator iter = nestedErrorObjects.iterator(); iter.hasNext();) {
            errorObject = (KnValidationErrorObject) iter.next();
            if (errorObject instanceof KnConsolidatedErrorObject)
                errorCodeList.addAll(((KnConsolidatedErrorObject) errorObject).getAllErrorCodesRecursively(withType));
            if (withType)
                errorCodeList.add(errorObject.getCodeWithType());
            else
                errorCodeList.add(errorObject.getCode());
        }
        return errorCodeList;
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
    public Collection getAllErrorCodesForData(String keyDataValue, boolean withType) {
        Collection codeList = new HashSet(5);
        KnValidationErrorObject errorObject = null;
        for (Iterator iter = nestedErrorObjects.iterator(); iter.hasNext();) {
            errorObject = (KnValidationErrorObject) iter.next();
            if (errorObject instanceof KnConsolidatedErrorObject)
                codeList.addAll(((KnConsolidatedErrorObject) errorObject).getAllErrorCodesForData(keyDataValue, withType));
            else if (errorObject.getKeyDataList().contains(keyDataValue)) {
                if (withType)
                    codeList.add(errorObject.getCodeWithType());
                else
                    codeList.add(errorObject.getCode());
            }
        }
        return codeList;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        String errorMsg = "\nCEO OBJ --> [ObjectID: " + getObjectId() + ", Rule: " + validationRule + ", ErrorCode: " + errorCode +
                ", ErrorMsg: " + errorMessage + ", KeyDataType: " + keyDataType + ", ErrorType: " + errorType +
                ", Nested Errors(size:" + nestedErrorObjects.size() + "): {" + nestedErrorObjects + "} ";
        if (failedDatas != null) {
            errorMsg = errorMsg + ", KeyDataValues Vs FailedRuleValues: " + failedDatas;
        }
        //if (keyDataType != null) errorMsg = errorMsg + ", DataType: "+ keyDataType;
        return (errorMsg += "]<--CEO");
    }
}
