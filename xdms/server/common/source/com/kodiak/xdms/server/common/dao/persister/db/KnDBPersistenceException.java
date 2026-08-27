/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   Kn.java
 * Subsystem:
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
package com.kodiak.xdms.server.common.dao.persister.db;

import com.kodiak.common.dao.KnPersistenceException;

public class KnDBPersistenceException extends KnPersistenceException {
    private String query;

    /**
     * Constructs new KnDBPersistenceException object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param pttServerId  dao type
     * @param source       the source
     * @param query        the query
     */
    public KnDBPersistenceException(String errorCode, String errorMessage, String pttServerId,
                                    String source, String query) {
        super(errorCode, errorMessage, pttServerId, source);
        this.query = query;
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
    public KnDBPersistenceException(String errorCode, String errorMessage, Exception root,
                                    String pttServerId, String source, String query) {
        super(errorCode, errorMessage, root, pttServerId, source);
        this.query = query;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + (query == null ? "" : ", Query : " + query);
    }
}
