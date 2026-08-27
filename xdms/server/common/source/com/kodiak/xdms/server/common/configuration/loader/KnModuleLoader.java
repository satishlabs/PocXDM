/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnModuleLoader.java
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

/**
 * This is the base interface for all the loaders
 */
public abstract class KnModuleLoader implements IModuleLoader {

    /**
     * holds the cache data structure
     */
    private ICacheData cacheData;

    /**
     * It will set the Cache Datastructure. the data loaded by the
     * loader will stored in this data structure
     *
     * @param data the Cacheable data structure
     */
    public void setCacheData(ICacheData data) {
        this.cacheData = data;
    }

    /**
     * It will return the Cache Datastructure. the data loaded by the
     * loader will stored in this data structure
     *
     * @return the Cacheable data structure
     */
    public ICacheData getCacheData() {
        return this.cacheData;
    }
}
