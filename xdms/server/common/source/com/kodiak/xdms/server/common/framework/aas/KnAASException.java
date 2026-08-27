/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnAASException.java
 * Subsystem:   WGP
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      19/10/2006 5.7
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
package com.kodiak.xdms.server.common.framework.aas;

import com.kodiak.xdms.server.common.framework.KnFWException;

public class KnAASException extends KnFWException {

    private String profile;

    /**
     * Constructs new KnGPException object with error code and error message.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     * @param profile      the profile
     */
    public KnAASException(String errorCode, String errorMessage, String profile) {
        super(errorCode, errorMessage);
        this.profile = profile;
    }

    /**
     * Constructs new KnGPException object with error code, error message
     * and instance of another exception class.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     * @param root         root of an another exception
     * @param profile      the profile
     */
    public KnAASException(String errorCode, String errorMessage, Exception root, String profile) {
        super(errorCode, errorMessage, root);
        this.profile = profile;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Profile : " + profile;
    }
}
