/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnInitLoaderConfigException.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         15-03-2007 6.0
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
package com.kodiak.xdms.server.common.configuration.init;

/**
 * Loader configuration exception
 */
public class KnInitLoaderConfigException extends KnInitModuleConfigException {

    /**
     * constructor
     *
     * @param errorCode
     * @param errorMessage
     * @param param
     * @param value
     * @param moduleName
     */
    public KnInitLoaderConfigException(String errorCode, String errorMessage,
                                       String param, String value, String moduleName) {
        super(errorCode, errorMessage, param, value, moduleName);
    }

    /**
     * constructor
     *
     * @param errorCode
     * @param errorMessage
     * @param root
     * @param param
     * @param value
     * @param moduleName
     */
    public KnInitLoaderConfigException(String errorCode, String errorMessage,
                                       Exception root, String param, String value,
                                       String moduleName) {
        super(errorCode, errorMessage, root, param, value, moduleName);
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
