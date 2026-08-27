/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnInitCacheConfigException.java
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
 * This exception will be thrown if there is any error while parsing the
 * cache configuration data
 */
public class KnInitCacheConfigException extends KnInitConfigException {

    /**
     * This method constructs new KnInitCacheConfigException object with error code, error message,
     * param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param param        param name
     * @param value        param value
     */
    public KnInitCacheConfigException(String errorCode, String errorMessage,
                                      String param, String value) {
        super(errorCode, errorMessage, param, value);
    }

    /**
     * This method constructs new KnInitCacheConfigException object with error code,
     * error message, another exception object, param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param param        param name
     * @param value        param value
     */
    public KnInitCacheConfigException(String errorCode, String errorMessage,
                                      Exception root, String param,
                                      String value) {
        super(errorCode, errorMessage, root, param, value);
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
