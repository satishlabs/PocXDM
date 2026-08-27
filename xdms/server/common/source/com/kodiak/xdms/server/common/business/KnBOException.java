/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnBOException.java
 * Subsystem:   Server Common
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/3/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.common.business;

import com.kodiak.common.exception.KnException;
import com.kodiak.xdms.server.common.KnXDMServerException;

public class KnBOException extends KnXDMServerException {
    private static final String BOEntity = "BOEntity.";
    /**
     * Constructs the new KnBOException object with error code, error message.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     */
    public KnBOException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructs the new KnBOException object with error code, error message and with
     * another exception object.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     * @param root         root of an another exception object
     */
    public KnBOException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    /**
     * Constructs the new KnBOException object with error message and with
     * another exception object.
     * This construtor is used to wrap the exception with new BOException.
     * It will be used the same error code but changing orignator to BOEntity
     * This will be used whenever we expect multiple exceptions from the calling method.
     * @param errorMessage error message string
     * @param root         root of an another exception object
     */
    public KnBOException(String errorMessage, KnXDMServerException root) {
        super(BOEntity + root.getErrorCode(), errorMessage, root);
    }

    /**
     * Constructs the new KnBOException object with error code, error message and with
     * another exception object.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     * @param root         root of an another exception object
     */
    public KnBOException(KnException root) {
        super(root.getErrorCode(), root);
    }

    /**
     *Constructs the new KnBOException object with error code and with
     * another exception object.
     * This construtor is used to wrap the exception with new BOException.
     * It will be used with the different error code
     * @param errorCode error code string
     * @param root      root of an another exception object
     */
    public KnBOException(String errorCode, Exception root) {
        super(errorCode, root);
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
