/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   KnPersistenceException.java
 * Subsystem:   Common DAO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 18, 2010        7.0
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
 * ******************************************************************************
 */

package com.kodiak.common.dao;

public class KnPersistenceException extends KnDAOException {
    /// the push to talk server id
    private String pttServerId;
    // the source where which the exception actually thrown
    private String source;

    private String query;

    /**
     * Constructs new KnpersistenceException object with error code, error message and
     * dao type.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param pttServerId  dao type of error
     * @param source       the source of the exception
     */
    public KnPersistenceException(String errorCode, String errorMessage,
                                  String pttServerId, String source) {
        super(errorCode, errorMessage);
        this.pttServerId = pttServerId;
        this.source = source;
    }

    /**
     * Constructs new KnpersistenceException object with error code, error message,
     * dao type and another instance of exception class.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root of an another exception
     * @param pttServerId  dao type of error
     * @param source       the source of exception
     */
    public KnPersistenceException(String errorCode, String errorMessage, Exception root,
                                  String pttServerId, String source) {
        super(errorCode, errorMessage, root);
        this.pttServerId = pttServerId;
        this.source = source;
    }
    /**
     * Constructs new KnDBPersistenceException object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root of an another exception
     * @param pttServerId  the rtx pttserver id
     * @param source       the source
     * @param query        the query
     */
    public KnPersistenceException(String errorCode, String errorMessage, Exception root,
                                    String pttServerId, String source, String query) {
        super(errorCode, errorMessage, root);
        this.pttServerId = pttServerId;
        this.source = source;
        this.query = query;
    }

    /**
     * Constructs new KnpersistenceException object with error code, error message,
     * dao type and another instance of exception class.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root of an another exception
     */
    public KnPersistenceException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", PttServerId : " + pttServerId + ", Source : " + source;
    }

    /**
     * Returns the push to talk server id
     *
     * @return the push to talk server id
     */
    public String getPttServerId() {
        return pttServerId;
    }

    /**
     * Sets the push to talk server id
     *
     * @param pttServerId the push to talk server id
     */
    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * Returns the source where which the exception actually thrown
     *
     * @return the source where which the exception actually thrown
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source where which the exception actually thrown
     *
     * @param source the source where which the exception actually thrown
     */
    public void setSource(String source) {
        this.source = source;
    }
}
