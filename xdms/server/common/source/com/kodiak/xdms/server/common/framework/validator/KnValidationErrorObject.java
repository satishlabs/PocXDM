/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnValidationErrorObject.java
 * Subsystem:   Validator Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna        14-03-2007 6.0
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

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.xdms.server.common.KnXDMError;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * KnValidationErrorObject class
 */
public class KnValidationErrorObject implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676263L;

    // the error object
    private KnXDMError errorObj = null;

    protected boolean hasNestedErrors;
    /**
     * stores validation rule of error
     */
    protected String validationRule;
    /**
     * Error code
     */
    protected String errorCode;
    /**
     * Error message
     */
    protected String errorMessage;
    /**
     * The map for data which is failed to conform the validation rule with the received data.
     */
    protected Map failedDatas;
    /**
     * The data type of the failed data.
     */
    protected String keyDataType;
    protected String errorType;

    /**
     * Constructor with validation rule id & error code
     *
     * @param validationRule
     * @param errorCode
     */
    public KnValidationErrorObject(String validationRule, String errorCode) {
        this.validationRule = validationRule;
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code, rule, data type
     *
     * @param validationRule
     * @param errorCode
     * @param keyDataType
     * @param errorType
     */
    public KnValidationErrorObject(String validationRule, String errorCode, String keyDataType, String errorType) {
        this.validationRule = validationRule;
        this.errorCode = errorCode;
        this.keyDataType = keyDataType;
        this.errorType = errorType;
    }

    public boolean hasNestedErrors() {
        return hasNestedErrors;
    }

    /**
     * Returns the error code
     *
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the error code
     *
     * @return
     */
    public String getErrorCodeWithType() {
        if (keyDataType != null && !keyDataType.equals(""))
            return errorCode + "." + keyDataType;
        return errorCode;
    }

    /**
     * Sets the error code
     *
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns validation rule id
     *
     * @return
     */
    public String getValidationRule() {
        return validationRule;
    }

    /**
     * Sets the validation rule
     *
     * @param validationRule
     */
    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

    /**
     * Return the map of failed datas with recieved data
     *
     * @return
     */
    public Map getFailedDatas() {
        return failedDatas;
    }

    /**
     * returns the failed data type
     *
     * @return
     */
    public String getKeyDataType() {
        return keyDataType;
    }

    /**
     * Sets the data type of the failed data
     *
     * @param keyDataType
     */
    public void setKeyDataType(String keyDataType) {
        this.keyDataType = keyDataType;
    }

    /**
     * returns error message of an error object
     *
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * sets the error message to the error object
     *
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Adds to failed data list with recieved data
     *
     * @param keyDataValue
     * @param failedRuleValue
     */
    public void addTofailedData(String keyDataValue, String failedRuleValue) {
        if (failedDatas == null)
            failedDatas = new HashMap(5);
        failedDatas.put(keyDataValue, failedRuleValue);
    }

    /**
     * Returns the collection of failed data
     *
     * @return
     */
    public Collection getKeyDataList() {
        if (failedDatas != null)
            return failedDatas.keySet();
        return null;
    }

    /**
     * Rturns the comma separated string for all failed data
     *
     * @return
     */
    public String getFailedData() {
        if (failedDatas != null) {
            StringBuffer buffer = new StringBuffer(50);
            for (Iterator iterator = failedDatas.keySet().iterator(); iterator.hasNext();) {
                buffer.append(iterator.next()).append(",");
            }
            if (buffer.length() > 0)
                return (buffer.substring(0, buffer.length() - 1));
        }
        return null;
    }

    /**
     * Returns the comma separated string for all failed data with the corresponding received data
     *
     * @return
     */
    public String getFailedDataDetails() {
        if (failedDatas != null) {
            StringBuffer buffer = new StringBuffer(50);
            for (Iterator iterator = failedDatas.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                buffer.append(key).append("-").append(failedDatas.get(key)).append(",");
            }
            if (buffer.length() > 0) {
                return keyDataType + "(s) - " + buffer.substring(0, buffer.length() - 1);
            }
        }
        return null;
    }

    /**
     * returns the id
     *
     * @return id
     */
    public String getObjectId() {
        String objectId = validationRule + "." + errorCode;
        if (keyDataType != null) objectId += "." + keyDataType;
        return objectId;
    }

    /**
     * Over rides equals method
     *
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        KnValidationErrorObject that = (KnValidationErrorObject) obj;

        if (!errorCode.equals(that.getErrorCode())) return false;
        if (!validationRule.equals(that.getValidationRule())) return false;
        String thatFailedDataType = that.getKeyDataType();
        if (keyDataType != null ? !keyDataType.equals(thatFailedDataType) : thatFailedDataType != null)
            return false;
        return true;
    }

    /**
     * Over rides hashcode
     *
     * @return
     */
    public int hashCode() {
        return getObjectId().hashCode();
    }

    /**
     * Return the error object for the exception
     *
     * @return the error object
     */
    public KnXDMError getErrorObject() {
        return errorObj;
    }

    /**
     * Sets the error object for the exception
     *
     * @param errorObject
     */
    public void setErrorObject(KnXDMError errorObject) {
        this.errorObj = errorObject;
    }

    /**
     * this method is used to returns error object error code.
     *
     * @return error code of an error object
     */
    public String getCode() {
        return errorObj.getCode();
    }

    /**
     * Oer loads equals method
     *
     * @param validationRule
     * @param errorCode
     * @param dataType
     * @param errorType
     * @return
     */
    public boolean equals(String validationRule, String errorCode, String dataType, String errorType) {
        if (!this.errorCode.equals(errorCode)) return false;
        if (!this.validationRule.equals(validationRule)) return false;
        if (this.keyDataType != null ? !this.keyDataType.equals(dataType) : dataType != null)
            return false;
        if (this.errorType != null ? !this.errorType.equals(errorType) : errorType != null)
            return false;
        return true;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        String errorMsg = "VEO OBJ --> [ObjectID: " + getObjectId() + ", Rule: " + validationRule + ", ErrorCode: " + errorCode +
                ", ErrorMsg: " + errorMessage + ", KeyDataType: " + keyDataType + ", ErrorType: " + errorType +
                ", KeyDataValues Vs FailedRuleValues: " + failedDatas;
        return (errorMsg += "]");
    }

    /**
     * returns the errorcode with errorType
     * @return String returns errorCode with errorType.
     */
    public String getCodeWithType() {
        StringBuffer buffer = new StringBuffer(100);
        buffer.setLength(0);
        buffer.append(errorObj.getCode());
        if (errorType != null) {
            buffer.append(".").append(errorType);
        }
        return buffer.toString();
    }
}
