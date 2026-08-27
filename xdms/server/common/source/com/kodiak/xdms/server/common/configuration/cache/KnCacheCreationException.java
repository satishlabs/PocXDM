/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheCreationException.java
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


/**
 *
 */
public class KnCacheCreationException extends KnCacheException {

    /**
     * cache configuration
     */
    ICacheManagerInitConfigData initConfigData;

    /**
     * constructor
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     * @param config       the configuration for cache initialization
     */
    public KnCacheCreationException(String errorCode, String errorMessage,
                                    ICacheManagerInitConfigData config) {
        super(errorCode, errorMessage);
        this.initConfigData = config;
    }

    /**
     * constructor
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     * @param root         the root exception
     * @param config       the cache configuration
     */
    public KnCacheCreationException(String errorCode, String errorMessage,
                                    Exception root,
                                    ICacheManagerInitConfigData config) {
        super(errorCode, errorMessage, root);
        this.initConfigData = config;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Initalization Cofiguration Data : " + initConfigData;
    }
}
