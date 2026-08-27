/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnInitFileException.java
 * Subsystem:   Configuration Exceptions
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
public class KnInitFileException extends KnInitializationException {

    /**
     * store initFileName
     */
    private String initFile;

    /**
     * This method constructs new KnInitFileException object with error code and
     * error message parameters.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param initFile     the initFile name
     */
    public KnInitFileException(String errorCode, String errorMessage,
                               String initFile) {
        super(errorCode, errorMessage);
        this.initFile = initFile;
    }

    /**
     * This method constructs new KnInitFileException object with error code,
     * error message and another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param initFile     the initFile name
     */
    public KnInitFileException(String errorCode, String errorMessage,
                               Exception root, String initFile) {
        super(errorCode, errorMessage, root);
        this.initFile = initFile;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Initialization File : " + initFile;
    }

}
