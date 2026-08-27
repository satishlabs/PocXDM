/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheDataException.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-03-2007 6.0
 *
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
 **************************************************************************/
package com.kodiak.xdms.server.common.configuration.cache;


import java.util.List;

/**
 * This exception will be thrown if there is any error while building
 * the cache data structure
 */
public class KnCacheDataException extends KnCacheException {

    /**
     * store the keys
     */
    private List keys;

    /**
     * store the value
     */
    private String value;


    /**
     * constructor
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     * @param keys         the list of keys
     * @param value        the value
     */
    public KnCacheDataException(String errorCode, String errorMessage, List keys,
                                String value) {
        super(errorCode, errorMessage);
        this.keys = keys;
        this.value = value;
    }

    /**
     * constructor
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     * @param root
     * @param keys         the list of keys
     * @param value        the value
     */
    public KnCacheDataException(String errorCode, String errorMessage, Exception root,
                                List keys, String value) {
        super(errorCode, errorMessage, root);
        this.keys = keys;
        this.value = value;
    }

    /**
     * return the message of the exception
     * @return the exception message
     */
    public String getMessage() {
        return super.getMessage() + ", keys : " + keys + ", value : " + value;
    }
}
