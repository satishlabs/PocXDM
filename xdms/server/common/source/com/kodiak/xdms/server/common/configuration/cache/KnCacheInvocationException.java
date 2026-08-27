/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheInvocationException.java
 * Subsystem:   Cache Exceptions
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14/03/2007 6.0
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

package com.kodiak.xdms.server.common.configuration.cache;

import com.kodiak.xdms.server.common.configuration.cache.KnCacheException;

/**
 * cache invocation exception.
 */
public class KnCacheInvocationException extends KnCacheException {

    /**
     * store the key
     */
    private Object key;

    /**
     * This method constructs new KnCacheInvocationException object with error code and error message
     * parameters.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param key          the key
     */
    public KnCacheInvocationException(String errorCode, String errorMessage,
                                      Object key) {
        super(errorCode, errorMessage);
        this.key = key;
    }

    /**
     * This method constructs new KnCacheInvocationException object with error code, error message and
     * another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param key          the key
     */
    public KnCacheInvocationException(String errorCode, String errorMessage,
                                      Exception root, Object key) {
        super(errorCode, errorMessage, root);
        this.key = key;
    }

    /**
     * 
     * @return
     */
    public String getMessage() {
        return super.getMessage() + ", Key : " + key;
    }
}
