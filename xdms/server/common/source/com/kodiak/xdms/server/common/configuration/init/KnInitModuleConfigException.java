/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnInitModuleConfigException.java
 * Subsystem:   Configuration Excptions
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

/**
 *
 */
public class KnInitModuleConfigException extends KnInitConfigException {

    /**
     * stores the module name
     */
    private String moduleName = null;

    /**
     * This method constructs new KnInitModuleConfigException object with error code, error message,
     * param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param param        param name
     * @param value        param value
     * @param moduleName   module name
     */
    public KnInitModuleConfigException(String errorCode, String errorMessage,
                                       String param, String value,
                                       String moduleName) {
        super(errorCode, errorMessage, param, value);
        this.moduleName = moduleName;
    }

    /**
     * This method constructs new KnInitModuleConfigException object with error code,
     * error message, another exception object, param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param param        param name
     * @param value        param value
     * @param moduleName   module name
     */
    public KnInitModuleConfigException(String errorCode, String errorMessage,
                                       Exception root, String param,
                                       String value, String moduleName) {
        super(errorCode, errorMessage, root, param, value);
        this.moduleName = moduleName;
    }

    /**
     * returns the error occured module name
     *
     * @return moduleName
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * set the module name
     *
     * @param moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Module Name : " + moduleName;
    }

}
