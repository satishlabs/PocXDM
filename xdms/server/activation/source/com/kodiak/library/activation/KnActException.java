/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActException.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.library.activation;

import com.kodiak.xdms.server.common.KnXDMServerException;

public class KnActException extends KnXDMServerException {

    /**
     * this will accept error code and error message and constructs an Exception object
     *
     * @param errorCode    - error code for the error
     * @param errorMessage - message for logging
     */
    public KnActException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     *
     * @param errorCode
     * @param statusCode
     * @param errorMessage
     */
    public KnActException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
        super(errorCode, statusCode, errorMessage);
    }

    /**
     * This will create Buddy Group Exception using error code, error message and another
     * Exception object.
     *
     * @param errorCode    - error code
     * @param errorMessage - message for logging
     * @param root         - root of an another exception
     */
    public KnActException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

}
