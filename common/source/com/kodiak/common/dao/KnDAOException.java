/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnDAOException.java
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

import com.kodiak.common.exception.KnException;

public class KnDAOException extends KnException {

    private static final String DAO = "DAO.";

    /**
     * Constructs new KnDAOException object with error code, error message and dao
     * type of the error.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     */
    public KnDAOException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructs new KnDAOException object with error code, error message, dao
     * type and instance of another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root of an another exception instance
     */
    public KnDAOException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    /**
     * Constructs the new KnDAOException object with error message and with
     * another exception object.
     * This construtor is used to wrap the exception with new DAOException.
     * It will be used the same error code but changing orignator to DAO.
     * This will be used whenever we expect multiple exceptions from the calling method.
     *
     * @param errorMessage error message string
     * @param root         root of an another exception object
     */
    public KnDAOException(String errorMessage, KnException root) {
        super(DAO + root.getErrorCode(), errorMessage, root);
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
