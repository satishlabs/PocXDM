/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IModuleLoader.java
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
package com.kodiak.xdms.server.common.configuration.loader;

import com.kodiak.xdms.server.common.configuration.cache.ICacheData;
import com.kodiak.xdms.server.common.configuration.KnLoaderException;


/**
 * This is the base interface for module loader
 */
public interface IModuleLoader {

    /**
     * This method will load the module.
     *
     * @return the cache element. it will return null if no caching is required.
     * @throws KnLoaderException if any error
     */
    ICacheData load() throws KnLoaderException;

    /**
     * It will return the Module loader configuration object
     *
     * @return the configuration object
     */
    IModuleLoaderConfigData getConfigData();

    /**
     * It will set the Cache Datastructure. the data loaded by the
     * loader will stored in this data structure
     *
     * @param data the Cacheable data structure
     */
    void setCacheData(ICacheData data);

    /**
     * It will return the Cache Datastructure. the data loaded by the
     * loader will stored in this data structure
     *
     * @return the Cacheable data structure
     */
    ICacheData getCacheData();
}
