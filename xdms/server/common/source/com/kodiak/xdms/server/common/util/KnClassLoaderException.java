/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnClassLoaderException.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         27-03-2007 6.0
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
package com.kodiak.xdms.server.common.util;

/**
 * This is the exception thrown if there is any error while
 * creating an object using KnClassLoader
 */
public class KnClassLoaderException extends Exception {

    /**
     * store class name
     */
    private String className;

    /**
     * constructor
     *
     * @param message   the message
     * @param className the class name
     */
    public KnClassLoaderException(String message, String className) {
        super(message);
        this.className = className;
    }

    /**
     * constructor
     *
     * @param message   the message
     * @param cause     the cause exception object
     * @param className the name of the class
     */
    public KnClassLoaderException(String message, Throwable cause, String className) {
        super(message, cause);
        this.className = className;
    }

    /**
     * returns the exception string
     *
     * @return the exception string
     */
    public String getMessage() {
        return super.getMessage() + ", Class Name : " + this.className;
    }
}
