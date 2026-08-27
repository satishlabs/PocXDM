/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnInitConfigException.java
 * Subsystem:   Configuration Exception
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         15/03/2007 6.0
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
package com.kodiak.xdms.server.common.configuration.init;

import com.kodiak.xdms.server.common.configuration.KnInitializationException;


/**
 *
 */
public class KnInitConfigException extends KnInitializationException {

    /**
     * stores the param name of the configuration file
     */
    private String param = null;

    /**
     * stores the value of param in the configuration file
     */
    private String value = null;

    /**
     * This method constructs new KnInitConfigException object with error code, error message,
     * param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param param        param name
     * @param value        param value
     */
    public KnInitConfigException(String errorCode, String errorMessage, String param,
                                 String value) {
        super(errorCode, errorMessage);
        this.param = param;
        this.value = value;
    }

    /**
     * This method constructs new KnInitConfigException object with error code,
     * error message, another exception object, param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param param        param name
     * @param value        param value
     */
    public KnInitConfigException(String errorCode, String errorMessage, Exception root,
                                 String param,
                                 String value) {
        super(errorCode, errorMessage, root);
        this.param = param;
        this.value = value;
    }

    /**
     * returns the param name from configuration file
     *
     * @return param
     */
    public String getParam() {
        return param;
    }

    /**
     * sets the param name
     *
     * @param param
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * returns the param value of the param tag in configuration file
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * sets the value to the param name
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Parameter Name : " + param + ", Parameter Value : " + value;
    }

}
