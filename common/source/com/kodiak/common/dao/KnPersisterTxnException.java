/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnPersisterTxnException.java
 * Subsystem:   Common DAO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
 * *******************************************************************************
 */

package com.kodiak.common.dao;

public class KnPersisterTxnException extends KnDAOException {
    /**
     * Constructs new KnPersisterTxnException object with error code, error message and persistence
     * type of the error.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     */
    public KnPersisterTxnException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructs new KnPersisterTxnException object with error code, error message, persistence
     * type and instance of another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root of an another exception instance
     */
    public KnPersisterTxnException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }
}
