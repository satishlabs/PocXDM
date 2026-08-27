/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnLoaderValidation.java
 * Subsystem:   Validation Exceptions
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

package com.kodiak.xdms.server.common.configuration.loader;

import com.kodiak.xdms.server.common.configuration.KnLoaderException;


/**
 * validation exception
 */
public class KnLoaderValidationException extends KnLoaderException {

    /**
     * store parameter
     */
    private String parameter;

    /**
     * store value
     */
    private String value;

    /**
     * This method constructs new KnLoaderValidationException object with error code and error message
     * parameters.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param configData   the Module loader configuration
     * @param param        the parameter name
     * @param value        the parameter value
     */
    public KnLoaderValidationException(String errorCode, String errorMessage,
                                       IModuleLoaderConfigData configData,
                                       String param, String value) {
        super(errorCode, errorMessage, configData);
        this.parameter = param;
        this.value = value;
    }

    /**
     * This method constructs new KnLoaderValidationException object with error code, error message and
     * another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param configData   the Module loader configuration
     * @param param        the parameter name
     * @param value        the parameter value
     */
    public KnLoaderValidationException(String errorCode, String errorMessage,
                                       Exception root,
                                       IModuleLoaderConfigData configData,
                                       String param, String value) {
        super(errorCode, errorMessage, root, configData);
        this.parameter = param;
        this.value = value;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Parameter Name : " + parameter + ", Parameter Value : " + value;
    }
}
