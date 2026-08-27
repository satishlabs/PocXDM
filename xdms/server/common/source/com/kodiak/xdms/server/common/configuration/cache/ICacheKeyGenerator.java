/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   ICacheKeyGenerator.java
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
 * This is the base interface for Cache Key generator. All the
 * Cache key generators should implement this interface
 */
public interface ICacheKeyGenerator {

    /**
     * This will generate the key by accepting the key params as string array
     *
     * @param value
     * @return the key
     * @throws KnCacheKeyGenerationException if there is any error while
     *                                       generating the cache key
     */
    String generate(String[] value)
            throws KnCacheKeyGenerationException;

    /**
     * This will generate the key by accepting the parent and id of the current
     * @param parent the parent list
     * @param id
     * @return
     * @throws KnCacheKeyGenerationException
     */
    String generate(String[] parent, String id)
            throws KnCacheKeyGenerationException;

    /**
     * generate the key
     * @param moduleName
     * @param parent
     * @param id
     * @return
     * @throws KnCacheKeyGenerationException
     */
    String generate(String moduleName, String[] parent, String id)
            throws KnCacheKeyGenerationException;
}
