/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnError.java
 * Subsystem:   COMMON ERROR
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
package com.kodiak.common.error;

import com.kodiak.common.dto.IIdentifier;



public class KnError implements IIdentifier{
    private static final long serialVersionUID = 7526471155622676252L;
       /* the error code */
    private String code;
    /* the error message */
    private String errorMessage;
    /* the originator of the error */
    private String originator;
    /* the error level */
    private String level;
    /* the cause of the error */
    private String cause;
    /* the error context */
    private String errorContext;

    private String objectId;

    /**
     * Construct an error object using the given error code.
     * @param errorCode
     */
    public KnError(String errorCode) {
        this.code = errorCode;
    }

    /**
     * Returns the error code
     *
     * @return the error code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the error code
     *
     * @param code the error code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the error message
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the originator of the error
     *
     * @return the originator of the error
     */
    public String getOriginator() {
        return originator;
    }

    /**
     * Sets the originator of the error
     *
     * @param originator the originator of the error
     */
    public void setOriginator(String originator) {
        this.originator = originator;
    }

    /**
     * Returns the error level
     *
     * @return the error level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the error level
     *
     * @param level the error level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Returns the cause of the error
     *
     * @return the cause of the error
     */
    public String getCause() {
        return cause;
    }

    /**
     * Sets the cause of the error
     *
     * @param cause the cause of the error
     */
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * Returns the error context
     *
     * @return the error context
     */
    public String getErrorContext() {
        return errorContext;
    }

    /**
     * Sets the error context
     *
     * @param errorContext the error context
     */
    public void setErrorContext(String errorContext) {
        this.errorContext = errorContext;
    }

     public String getObjectId() {
        return objectId;  //To change body of implemented methods use File | Settings | File Templates.
    }
    /**
     * Return the string representation of the object
     * @return the string representation of the object
     */
    public String toString() {
        return "[KnError-> Code: " + code +
                ", Msg: " + errorMessage +
                ", Originator: " + originator +
                ", Level: " + level +
                ", Cause: " + cause +
                ", Error Context: " + errorContext +
                "]";
    }
}
