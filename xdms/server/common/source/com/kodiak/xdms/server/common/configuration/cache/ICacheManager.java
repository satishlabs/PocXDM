/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   ICacheManager.java
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

import com.kodiak.xdms.server.common.configuration.cache.ICacheManagerInitConfigData;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheCreationException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheData;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheStoreException;


/**
 * The cache manager interface. All the cache managers should implement
 * this interface.
 */
public interface ICacheManager {

    /**
     * This will do the initialization of cache manager.
     *
     * @param cacheManagerInitConfigData the cache manager configuration data
     * @throws KnCacheCreationException the cache creation exception
     */
    void initialize(ICacheManagerInitConfigData cacheManagerInitConfigData)
            throws KnCacheCreationException;

    /**
     * This method will cache the data for a module
     *
     * @param moduleName the name of the module
     * @param cacheData  the element to be cached
     */
    void doCache(String moduleName, ICacheData cacheData)
            throws KnCacheStoreException;

    /**
     * This method will return the key generator for this Cache manager
     *
     * @return the Cache Key Generator object
     */
    ICacheKeyGenerator getKeyGenerator();

    /**
     * this method will return the cached configuration data for the
     * Cacheable configuration object
     *
     * @param config the cacheable configuration object
     * @return the value;
     */
    Object get(ICacheClientIntf config) throws KnCacheInvocationException;

    /**
     * Put a data to the global cache
     *
     * @param key       the cache key
     * @param value     the value to be cached
     * @throws KnCacheStoreException
     */
    void put(String key, Object value) throws KnCacheStoreException;

    /**
     * Fetch the cached data for a key.
     *
     * @param key       the cache key
     * @return  the cached data
     * @throws KnCacheInvocationException
     */
    Object get(String key) throws KnCacheInvocationException;
}
