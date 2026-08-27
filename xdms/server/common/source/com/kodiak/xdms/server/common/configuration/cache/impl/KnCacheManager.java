/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheManager.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         15-03-2007 6.0
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
package com.kodiak.xdms.server.common.configuration.cache.impl;



import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.configuration.cache.*;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheAttributes;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class manages all the cache related operations.
 */
public class KnCacheManager implements ICacheManager {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCacheManager.class);

    public static final String CLASS = KnCacheManager.class.getName();

    public static final String GLOBAL_CACHE = "GPLibGlobalCache";

    /**
     * This will store the module caches
     */
    private Map cacheMap;

    /**
     * This will store the cache key generator
     */
    KnCacheKeyGenerator keyGenerator;

    /**
     * This will initialize the cache manager
     *
     * @param cacheManagerInitConfigData
     */
    public void initialize(ICacheManagerInitConfigData cacheManagerInitConfigData)
            throws KnCacheCreationException {
        String mName = "initialize";
        KnCacheManagerInitConfigData configData = null;
        knLogger.debug( mName, "Configuring Cache manager...");
        try {

            this.cacheMap = new HashMap();

            knLogger.debug( mName, "Default Cache initialized successfully.");

            configData = (KnCacheManagerInitConfigData) cacheManagerInitConfigData;
            this.keyGenerator = new KnCacheKeyGenerator();
            this.keyGenerator.setDelimiter(configData.getDelimiter());

            knLogger.info( mName, "Cache manager configured successfully.");
        } catch (ClassCastException e) {
            knLogger.error( mName, "Invalid configuration data passed.");
            throw new KnCacheCreationException(KnErrorCodes.CacheManager.CONFIG_ERROR,
                    "Invalid Cache Config data class", e, cacheManagerInitConfigData);
        } finally {
            configData = null;
        }

    }

    /**
     * this method cache the data
     *
     * @param moduleName
     * @param cacheData
     */
    public void doCache(String moduleName, ICacheData cacheData)
            throws KnCacheStoreException {
        String mName = "doCache";
        knLogger.debug( mName, "Starting caching of data for module " + moduleName + " value " + cacheData);

        Map moduleCache = getModuleCache(moduleName);
        if (cacheData instanceof KnCacheElement) {
            Collection elements = ((KnCacheElement)cacheData).getElements();
            if (elements.size() >= 1) {
                cacheElement(moduleName, moduleCache, null,
                        (KnCacheElement) elements.iterator().next());
            }
        }/* else if (cacheData instanceof Map) {
            // todo : to be implemented once Map is supported
            //cacheMap(moduleCache, doCache);
        }  */
        knLogger.debug( mName, "Completed caching of data for module " + moduleName);
    }

    /**
     * This method will return the key generator for this Cache manager
     *
     * @return the Cache Key Generator object
     */
    public ICacheKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * Put a data to the global cache
     *
     * @param key       the cache key
     * @param value     the value to be cached
     * @throws KnCacheStoreException
     */
    public void put(String key, Object value) throws KnCacheStoreException {
        Map globalCache = getModuleCache(GLOBAL_CACHE);
        if (globalCache != null) {
            globalCache.put(key, value);
        } else {
            throw new KnCacheStoreException(KnErrorCodes.CacheManager.INTERNAL_ERROR,
                    "Global Cache not found.", "GLOBAL CACHE", key, value);
        }
        //knLogger.debug( "put", "successfully cached the data for key - " + key);
    }

    /**
     * Fetch the cached data for a key.
     *
     * @param key       the cache key
     * @return  the cached data
     * @throws KnCacheInvocationException
     */
    public Object get(String key) throws KnCacheInvocationException {
        Map globalCache = getModuleCache(GLOBAL_CACHE);
        if (globalCache == null) {
            throw new KnCacheInvocationException(KnErrorCodes.CacheManager.INTERNAL_ERROR, "Global Cache not found", key);
        } else {
            Object value = globalCache.get(key);
            //knLogger.debug( "get", "Successfully fetched data for key - " + key + ", value - " + value);
            return value;
        }
    }

    /**
     * This will return the cached data
     *
     * @param config the configuration object
     * @return the cached object
     * @throws KnCacheInvocationException
     */
    public Object get(ICacheClientIntf config) throws KnCacheInvocationException {
        String moduleName = config.getModuleName();
        String[] parent = config.getParents();
        String id = config.getId();
        Object key = null;
        try {
            knLogger.debug( "get", "Key : Module -> " + moduleName + ", Parents -> " +
                KnGeneralUtil.convertArrayToString(parent) + ", id -> " + id);
            Map moduleCache = getModuleCache(moduleName);
            if (moduleCache == null) {
                throw new KnCacheInvocationException(KnErrorCodes.CacheManager.INVALID_MODULE, "Module Cache not found for module: " + moduleName, null);
            }
            key = this.keyGenerator.generate(moduleName, parent, id);
            knLogger.debug( "get", "Key : " + key);
            Object data = moduleCache.get(key);
            knLogger.debug( "get", "Data : " + data);
            return data;
        } catch (KnCacheInvocationException e) {
            throw e;
        } catch (KnCacheKeyGenerationException e) {
            throw new KnCacheInvocationException(e.getErrorCode(),  "Error while generating key", e, key);
        } catch (Exception e) {
            throw new KnXDMServerSystemException(KnErrorCodes.CacheManager.INTERNAL_ERROR, "Error while geting data from the cache", e);
        }
    }

    /**
     * This method will cache the KnElement data
     *
     * @param moduleName
     * @param moduleCache
     * @param parent
     * @param cacheElement
     */
    private void cacheElement(String moduleName, Map moduleCache,
                              String parent, KnCacheElement cacheElement)
            throws KnCacheStoreException {

        try {
            String id = cacheElement.getId();
            //generate the key
            String key;
            if (parent == null) {
                key = keyGenerator.generate(new String[]{id});
            } else {
                key = keyGenerator.generate(new String[]{parent, id});
            }

            knLogger.debug( "cacheElement",
                    "Key : " + key + " Value : " + cacheElement);
            cacheObject(moduleCache, key, cacheElement);

            Collection elements = cacheElement.getElements();
            for (Iterator elementsIt = elements.iterator();
                 elementsIt.hasNext();) {
                KnCacheElement element = (KnCacheElement) elementsIt.next();
                cacheElement(moduleName, moduleCache, key, element);
            }

            KnCacheAttributes attributes = cacheElement.getAttributes();
            for (Iterator nameIt = attributes.getNameIterator();
                 nameIt.hasNext();) {

                String attrName = (String)nameIt.next();

                key = keyGenerator.generate(
                        new String[]{parent, id, attrName});
                knLogger.debug( "cacheElement",
                        "Key : " + key + " Value : " + attributes.get(attrName));
                cacheObject(moduleCache, key, attributes.get(attrName));
            }
        } catch (KnCacheKeyGenerationException e) {
            new KnCacheStoreException(e.getErrorCode(),
                    "Error while generating the cache key",
                    e, moduleName, parent, null);
        } catch (KnCacheStoreException e) {
            e.setModuleName(moduleName);
            throw e;
        }
    }

    /**
     * This method will store the object to the hashmap passed.
     * It will check if the key is already existing. if yes, it will
     * throw KnCacheStoreException
     *
     * @param moduleCache
     * @param key
     * @param cacheElement
     * @throws KnCacheStoreException
     */
    private void cacheObject(Map moduleCache, Object key, Object cacheElement)
            throws KnCacheStoreException {

        Object obj = moduleCache.get(key);
        if (obj == null) {
            moduleCache.put(key, cacheElement);
        } else if (obj != cacheElement) {
            throw new KnCacheStoreException(KnErrorCodes.CacheManager.CONFIG_ERROR,
                    "The key already used for caching with value " + obj,
                    "", key, cacheElement);
        }

    }

    /**
     * This method will return the Cache for a given module
     *
     * @param moduleName
     * @return the cache for the module
     */
    private Map getModuleCache(String moduleName) {
        Map moduleCache = (Map) this.cacheMap.get(moduleName);

        if (moduleCache == null) {
            moduleCache = new HashMap();
            this.cacheMap.put(moduleName, moduleCache);
        }
        return moduleCache;
    }
}
