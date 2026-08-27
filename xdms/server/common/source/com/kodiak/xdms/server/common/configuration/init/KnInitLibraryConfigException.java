/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnInitLibraryConfigException.java
 * Subsystem:   Configuration Excptions
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         26/04/2006 5.7
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
public class KnInitLibraryConfigException extends KnInitConfigException {

    /**
     * stores the library name
     */
    private String libraryName = null;

    /**
     * This method constructs new KnInitLibraryConfigException object with error code, error message,
     * param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param param        param name
     * @param value        param value
     * @param libraryName   library name
     */
    public KnInitLibraryConfigException(String errorCode, String errorMessage,
                                       String param, String value,
                                       String libraryName) {
        super(errorCode, errorMessage, param, value);
        this.libraryName = libraryName;
    }

    /**
     * This method constructs new KnInitLibraryConfigException object with error code,
     * error message, another exception object, param name and param value.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param param        param name
     * @param value        param value
     * @param libraryName   library name
     */
    public KnInitLibraryConfigException(String errorCode, String errorMessage,
                                       Exception root, String param,
                                       String value, String libraryName) {
        super(errorCode, errorMessage, root, param, value);
        this.libraryName = libraryName;
    }

    /**
     * returns the error occured library name
     *
     * @return libraryName
     */
    public String getLibraryName() {
        return libraryName;
    }

    /**
     * set the library name
     *
     * @param libraryName
     */
    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Library Name : " + libraryName;
    }

}
