/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActBOException.java
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
package com.kodiak.library.activation.business;

import com.kodiak.library.activation.KnActException;
import com.kodiak.library.activation.resources.KnConstants;
import com.kodiak.xdms.server.common.KnXDMServerException;

public class KnActBOException extends KnActException {
    private static final String ORGINATOR = KnConstants.LIBRARY_NAME + "." + "ORGINATOR.";

    /**
     * Constructs the new KnActBOException object with error code, error message.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     */
    public KnActBOException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * @param errorCode
     * @param statusCode
     * @param errorMessage
     */
    public KnActBOException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
        super(errorCode, statusCode, errorMessage);
    }

    /**
     * Constructs the new KnActBOException object with error code, error message and with
     * another exception object.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     * @param root         root of an another exception object
     */
    public KnActBOException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    /**
     * Constructs the new KnActBOException object with error message and with
     * another exception object.
     * This construtor is used to wrap the exception with new BOException.
     * It will be used the same error code but changing orignator to ORGINATOR
     * This will be used whenever we expect multiple exceptions from the calling method.
     *
     * @param errorMessage error message string
     * @param root         root of an another exception object
     */
    public KnActBOException(String errorMessage, KnXDMServerException root) {
        super(ORGINATOR + root.getErrorCode(), errorMessage, root);
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
