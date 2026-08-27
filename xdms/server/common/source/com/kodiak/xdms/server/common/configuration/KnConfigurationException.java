/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnConfigExceptions.java
 * Subsystem:   Common Exceptions
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
package com.kodiak.xdms.server.common.configuration;

import com.kodiak.xdms.server.common.KnXDMServerException;

/**
 * This class represents Configuration Exception object. All Configuration methods in Configuration
 * module throw this or sub-class of this exceptions. This is a base class of all configuration
 * exceptions.
 */
public class KnConfigurationException extends KnXDMServerException {

    /**
     * This method constructs new KnConfigurationException object with error code and error message
     * parameters.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     */
    public KnConfigurationException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * This method constructs new KnConfigurationException object with error code, error message and
     * another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     */
    public KnConfigurationException(String errorCode, String errorMessage,
                                    Exception root) {
        super(errorCode, errorMessage, root);
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage();
    }
}
