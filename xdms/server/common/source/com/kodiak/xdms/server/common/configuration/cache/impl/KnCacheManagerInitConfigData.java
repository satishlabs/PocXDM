/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheManagerInitConfigData.java
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
import com.kodiak.xdms.server.common.configuration.cache.ICacheManagerInitConfigData;
import com.kodiak.xdms.server.common.configuration.init.KnInitConfigException;
import com.kodiak.xdms.server.common.configuration.init.KnInitCacheConfigException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * This is the configuration data class for cache. Each cache implementation
 * will have its on configuration data class.
 * <p/>
 * The configuration xml schema for this implementation is
 * <p/>
 * <manager-config-data>
 * ...<key-generator-config>
 * ......<delimiter>_</delimiter>
 * ...</key-generator-config>
 * </manager-config-data>
 */
public class KnCacheManagerInitConfigData
        implements ICacheManagerInitConfigData {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCacheManagerInitConfigData.class);

    private static final String CLASS = KnCacheManagerInitConfigData.class.getName();
    /**
     * constants for tags
     */
    public static final String KEY_GEN_CONGIF_TAG = "key-generator-config";
    public static final String KEY_DELIMITER_TAG = "key-delimiter";

    /**
     * default delimiter constant
     */
    private static final String DEFAULT_KEY_DELIMITER = ".";

    /**
     * stores the delimiter for Cache Key generator
     */
    private String delimiter;

    /**
     * This method will populateDto the fields from the dom element.
     * The element object passed to this method will be a dom
     * representation of the tag <manager-config-data>
     *
     * @param element dom object of the tag '<manager-config-data>'
     * @throws KnInitConfigException if the loader configuration data
     *                               is missing or invalid
     */
    public void populate(Element element) throws KnInitConfigException {
        NodeList nodeList;
        Element keyGenConfigElement;
        String value;

        knLogger.debug( "populate",
                "populating cache config data...");
        try {
            nodeList = element.getElementsByTagName(KEY_GEN_CONGIF_TAG);
            if (nodeList.getLength() != 1) {
                knLogger.error( "populate",
                        "No <key-generator-onfig> tag.");
                throw new KnInitCacheConfigException(KnErrorCodes.CacheManager.CONFIG_ERROR,
                        "Parameter missing", KEY_GEN_CONGIF_TAG, null);
            } else {
                keyGenConfigElement = (Element) nodeList.item(0);
            }

            //delimiter configuration.
            nodeList = keyGenConfigElement.getElementsByTagName(KEY_DELIMITER_TAG);
            value = null;
            if (nodeList.getLength() > 0) {
                value = nodeList.item(0).getFirstChild().getNodeValue();
            }
            if (value == null || value.trim().equals("")) {
                knLogger.debug( "populate",
                        "The <delimiter> tag is empty. so setting the delimiter to " + DEFAULT_KEY_DELIMITER);
                this.delimiter = DEFAULT_KEY_DELIMITER;
            } else {
                this.delimiter = value.trim();
                knLogger.debug( "populate",
                        "delimiter set to " + this.delimiter);
            }
            knLogger.info( "populate",
                    "Cache config data populated successfully.");
        } finally {
            nodeList = null;
            keyGenConfigElement = null;
            value = null;
        }
    }

    /**
     * This will return the cache key delimiter
     *
     * @return the delimiter for cache key generator
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * This method will set the delimiter for cache keys
     *
     * @param delimiter the delimiter string for cache keys
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
