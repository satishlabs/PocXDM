/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheKeyGenerationException.java
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

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Cache key generator exception
 */
public class KnCacheKeyGenerationException extends KnCacheException {

    /**
     * store keys
     */
    private String[] keys;

    /**
     * This method constructs new KnCacheKeyGenerateExceptionException object
     * with error code and error message parameters.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param key          the keys array this is used to create the caching key
     */
    public KnCacheKeyGenerationException(String errorCode, String errorMessage,
                                         String[] key) {
        super(errorCode, errorMessage);
        if (key != null) {
            this.keys = Arrays.copyOf(key, key.length);
    }
    }

    /**
     * This method constructs new KnCacheKeyGenerateExceptionException object
     * with error code, error message and another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param key          the keys array this is used to create the caching key
     */
    public KnCacheKeyGenerationException(String errorCode, String errorMessage,
                                         Exception root,
                                         String[] key) {
        super(errorCode, errorMessage, root);
        if (key != null) {
            this.keys = Arrays.copyOf(key, key.length);
    }
    }

    /**
     * This will return the strings for the Key generation
     *
     * @return the key generation strings
     */
    public String[] getKeys() {
        return keys;
    }

    /**
     * This will set the strings for key generation
     *
     * @param key the strings for key generation
     */
    public void setKeys(String[] key) {
        if (key != null) {
            this.keys = Arrays.copyOf(key, key.length);
    }
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Keys : " + keys.toString();
    }

}
