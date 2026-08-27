/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:   ${FILE}
 * Subsystem:
 * <p/>
 * Name                 Date       Release
 * ------------------ ---------- ---------------------------------------
 * Rama Krishna       Apr 5, 2007    6.0
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

package com.kodiak.xdms.server.common.framework;

import com.kodiak.xdms.server.common.KnXDMServerException;

public class KnFWException extends KnXDMServerException {

    /**
     * this will accept error code and error message and constructs an Exception object
     *
     * @param errorCode    - error code for the error
     * @param errorMessage - message for logging
     */
    public KnFWException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * This will create Exception using error code, error message and another
     * Exception object.
     *
     * @param errorCode    - error code
     * @param errorMessage - message for logging
     * @param root         - root of an another exception
     */
    public KnFWException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }
}
