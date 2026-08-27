/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheManagerConfig.java
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
package com.kodiak.xdms.server.common.configuration;


import com.kodiak.common.dto.IIdentifier;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManagerInitConfigData;

/**
 * This class contains the configurations for creating Cache Manager.
 * The configuration manager will use this information for creating and initilization
 * of the cache manager.
 */
public class KnCacheManagerConfig implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676259L;

    /**
     * stores the cache manager implementation class name
     */
    private String managerClass;

    /**
     * stores the cache manager configuration implementation class name
     */
    private String managerConfigClass;

    /**
     * store the configuration data for cache manager implementation
     */
    private ICacheManagerInitConfigData cacheManagerInitConfigData;
    private String objectID;

    /**
     * This method will return the class name for cache manager
     *
     * @return the Cache manager class name
     */
    public String getManagerClass() {
        return managerClass;
    }

    /**
     * This method will set the cache manager class name
     *
     * @param managerClass the class name of cache manager
     */
    public void setManagerClass(String managerClass) {
        this.managerClass = managerClass;
    }

    /**
     * This methid will return the name of cache manager config data class
     *
     * @return the name of cache manager config data class
     */
    public String getManagerConfigClass() {
        return managerConfigClass;
    }

    /**
     * This method will set the name of cache manager config data class
     *
     * @param managerConfigClass
     */
    public void setManagerConfigClass(String managerConfigClass) {
        this.managerConfigClass = managerConfigClass;
    }

    /**
     * This method will return the cache manager configuration data
     *
     * @return the Cache manager configuration daa
     */
    public ICacheManagerInitConfigData getCacheManagerConfigData() {
        return cacheManagerInitConfigData;
    }

    /**
     * This method will set the cache manager configuration data
     *
     * @param cacheManagerInitConfigData the cache manager configuration data
     */
    public void setCacheManagerConfigData(
            ICacheManagerInitConfigData cacheManagerInitConfigData) {
        this.cacheManagerInitConfigData = cacheManagerInitConfigData;
    }

    public String getObjectId() {
        return objectID;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
