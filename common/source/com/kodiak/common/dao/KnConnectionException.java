/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnConnectionException.java
 * Subsystem:   COMMON DAO
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

public class KnConnectionException extends KnDAOException {
    private String pttServerId;

    /**
     * Constructs new Connection Exception with error code, error message and dao type
     * of the exception.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     * @param pttServerId  dao type of the error
     */
    public KnConnectionException(String errorCode, String errorMessage,
                                 String pttServerId) {
        super(errorCode, errorMessage);
        this.pttServerId = pttServerId;
    }

    /**
     * Constructs new Connection Exception object with error code, error message, dao
     * type and instance of another exception.
     *
     * @param errorCode    error code of the error
     * @param errorMessage error message string
     * @param root         root of an another exception instance
     * @param pttServerId  dao type of the error
     */
    public KnConnectionException(String errorCode, String errorMessage, Exception root,
                                 String pttServerId) {
        super(errorCode, errorMessage, root);
        this.pttServerId = pttServerId;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", PttServerId : " + pttServerId;
    }
}
