/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnModuleConfig
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
package com.kodiak.xdms.server.common.configuration;


import com.kodiak.common.dto.IIdentifier;
import com.kodiak.xdms.server.common.configuration.loader.IModuleLoader;

/**
 * This class will hold the configuration data for a module.
 */
public class KnModuleConfig implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676261L;

    /**
     * This holds the name of the module
     */
    private String name;

    /**
     * This holds class name for module loader
     */
    private String loaderClass;

    /**
     * This holds the module Loader
     */
    private IModuleLoader loader;

    /**
     * this holds the severity level of the moduel
     */
    private boolean severe;

    /**
     * this holds the cache data class
     */
    private String cacheDataClass;
    private String obectId;

    public KnModuleConfig() {
        this.name = null;
        this.loaderClass = null;
        this.loader = null;
        this.severe = false;
        this.cacheDataClass = null;
    }

    /**
     * This method will return the name of the module
     *
     * @return the name of the module
     */
    public String getName() {
        return name;
    }

    /**
     * This method will set the module name
     *
     * @param name name of the module
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method will return the full qualified name of the module
     * loader class
     *
     * @return name of the loader class
     */
    public String getLoaderClass() {
        return loaderClass;
    }

    /**
     * This method will set the name of the loader class
     *
     * @param loaderClass full qualified name of the loader class
     */
    public void setLoaderClass(String loaderClass) {
        this.loaderClass = loaderClass;
    }

    /**
     * This method will return the module loader
     *
     * @return the module loader
     */
    public IModuleLoader getLoader() {
        return loader;
    }

    /**
     * This method will set the module loader
     *
     * @param loader the module loader
     */
    public void setLoader(IModuleLoader loader) {
        this.loader = loader;
    }

    /**
     * This method will set the severity of the module
     *
     * @param severe
     */
    public void setSevere(boolean severe) {
        this.severe = severe;
    }

    /**
     * This method will return the severity of the module
     *
     * @return the severity of the module
     */
    public boolean isSevere() {
        return severe;
    }

    /**
     * This method will return the cache data class
     *
     * @return the cache data class
     */
    public String getCacheDataClass() {
        return cacheDataClass;
    }

    /**
     * This method will set the cache data class
     *
     * @param cacheDataClass data class
     */
    public void setCacheDataClass(String cacheDataClass) {
        this.cacheDataClass = cacheDataClass;
    }

    /**
     * This method return a which indicates whether caching is required
     * for this module or not
     *
     * @return the boolean
     */
    public boolean hasToCache() {
        if (this.cacheDataClass == null
                || this.cacheDataClass.trim().equals("")) {
            return false;
        }
        return true;
    }

    public String getObjectId() {
        return obectId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
