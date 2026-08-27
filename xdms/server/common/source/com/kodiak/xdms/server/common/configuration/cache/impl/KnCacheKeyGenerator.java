/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheKeyGenerator.java
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
import com.kodiak.xdms.server.common.configuration.cache.ICacheKeyGenerator;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheKeyGenerationException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;


/**
 * This class will generate the Cache keys
 */
public class KnCacheKeyGenerator implements ICacheKeyGenerator {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCacheKeyGenerator.class);

    private static final String CLASS = KnCacheKeyGenerator.class.getName();
    /**
     * this will store the delimiter
     */
    private String delimiter;

    /**
     * This method will return the delimiter
     *
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * This method will set the delimiter
     *
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * This method will generate the Cache key
     *
     * @param parents the data for generating the key
     * @return the generated key
     */
    public String generate(String[] parents)
            throws KnCacheKeyGenerationException {
        if (parents == null || parents.length == 0) {
            throw new KnCacheKeyGenerationException(KnErrorCodes.CacheManager.EMPTY_KEYS,
                    "Empty Parent Keys", null, null);
        }
        try {
            StringBuffer buffer = new StringBuffer(500);
            for (int i = 0, len = parents.length; i < len; i++) {
                buffer.append(parents[i].trim());
                buffer.append(this.delimiter);
            }

            int len = buffer.length();


            knLogger.debug( "generate",
                    "buffer : " + buffer + " Len : " + len + " delimiter : " + this.delimiter.length());

            buffer.delete(len - this.delimiter.length(), len);

            knLogger.debug( "generate",
                    "buffer : " + buffer + " Len : " + len + " delimiter : " + this.delimiter.length());

            return buffer.toString();
        } catch (Exception e) {
            throw new KnCacheKeyGenerationException(KnErrorCodes.CacheManager.INTERNAL_ERROR,
                    "Error while generating the key", e, parents);
        }
    }

    /**
     * This method will generate the cache key.
     *
     * @param parents the parent list
     * @param id      the id of the element
     * @return the key for geting the data from cache
     * @throws KnCacheKeyGenerationException
     */
    public String generate(String[] parents, String id) throws
            KnCacheKeyGenerationException {
        if (parents == null || parents.length == 0) {
            return id;
        }
        return generate(parents) + this.delimiter + id;
    }

    /**
     * This method will generate the cache key.
     *
     * @param moduleName the name of the module
     * @param parents    the parent key list
     * @param id         the id of the leaf
     * @return the key
     * @throws KnCacheKeyGenerationException
     *
     */
    public String generate(String moduleName, String[] parents, String id) throws
            KnCacheKeyGenerationException {
        knLogger.debug( "generate(String, String[], String)",
                "moduleName : " + moduleName + " parents length : " + (parents == null ? 0 : parents.length) + " id : " + id);
        //return moduleName + delimiter + generate(parents, id);
        return generate(parents, id);
    }
}
