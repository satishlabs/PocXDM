/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheStoreException.java
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


/**
 *
 */
public class KnCacheStoreException extends KnCacheException {

    /**
     * store module name
     */
    private String moduleName;

    /**
     * store key
     */
    private Object key;

    /**
     * store value
     */
    private Object value;

    /**
     * This method constructs new KnCacheStoreExceptionException object with error code and error message
     * parameters.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param moduleName   the module name
     * @param key          the cache key
     * @param value        the value to be storred in cache
     */
    public KnCacheStoreException(String errorCode, String errorMessage,
                                 String moduleName, Object key, Object value) {
        super(errorCode, errorMessage);
        this.moduleName = moduleName;
        this.key = key;
        this.value = value;
    }

    /**
     * This method constructs new KnCacheStoreExceptionException object with error code, error message and
     * another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root object of another exception class
     * @param moduleName   the module name
     * @param key          the cache key
     * @param value        the value to be storred in cache
     */
    public KnCacheStoreException(String errorCode, String errorMessage,
                                 Exception root, String moduleName,
                                 Object key, Object value) {
        super(errorCode, errorMessage, root);
        this.moduleName = moduleName;
        this.key = key;
        this.value = value;
    }

    /**
     * This method will return the name of the module
     *
     * @return the name of the moddule
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * This method will set the name of the module
     *
     * @param moduleName the name of the module
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * This method will return the key
     *
     * @return the key
     */
    public Object getKey() {
        return key;
    }

    /**
     * This method will set the key
     *
     * @param key the key to be set
     */
    public void setKey(Object key) {
        this.key = key;
    }

    /**
     * This method will return the cached value
     *
     * @return the cached value
     */
    public Object getValue() {
        return value;
    }

    /**
     * This method will set the cached value
     *
     * @param value the cached value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Module Name : " + moduleName + ", key : " + key + ", value : " + value;
    }

}
