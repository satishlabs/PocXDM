/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnConfigurationsManager.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-04-2007 6.0
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
package com.kodiak.xdms.server.common.configuration.manager;


import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.configuration.init.IConfigInitializer;
import com.kodiak.xdms.server.common.configuration.init.impl.KnConfigInitializer;
import com.kodiak.xdms.server.common.configuration.*;
import com.kodiak.xdms.server.common.configuration.loader.IModuleLoader;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheCreationException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheData;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheStoreException;
import com.kodiak.xdms.server.common.util.KnClassLoader;
import com.kodiak.xdms.server.common.util.KnClassLoaderException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

/**
 * This is the entry point to Configuration module
 */
public class KnConfigurationsManager {
	private static final KnLogger knLogger = KnLogger.getLogger(KnConfigurationsManager.class);

    private static final String CLASS = KnConfigurationsManager.class.getName();
    // map holds the library configuration manager instance objects
    private static HashMap confMgrInsMap = new HashMap();
    // name of the global configuration manager
    private static final String GLOBAL_LIB_CONFMGR = "GlobalLibConf";
    // store the initialization xml (library.xml) file
    private String initFile;

    // flag which denotes whether its initialized or not
    private boolean isInitialized = false;

    private boolean isCacheInitialized = false;

    // store ConfigInitializer
    private IConfigInitializer configInitializer;

    /**
     * store cache configurations
     */
    private KnCacheManagerConfig cacheManagerConfig;

    /**
     * store cache manager
     */
    private ICacheManager cacheManager;

    /**
     * store modules configuration
     */
    private Collection modulesConfigList;

    // stores libraries configuration objects
    private Collection librariesConfigList;

    /**
     * init xml file constant
     */
    public static final String INIT_FILE = "libraryInitFile";

    /**
     * constructor
     */
    private KnConfigurationsManager() {
        // do nothing now.. will include high funda code later. :)
    }

    /**
     * Returns flag which denotes whether its initialized or not
     *
     * @return flag which denotes whether its initialized or not
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * This method will set the initialization file for configuration
     * manager. The initialization file will be gp-library.xml
     *
     * @param fileName the name of the file
     */
    private void setInitFile(String fileName) {
        this.initFile = fileName;
    }

    /**
     * This method will return the cache manager implementation object
     * @return the cache manager
     */
    public ICacheManager getCacheManager() throws KnConfigurationException {
        if (!this.isCacheInitialized) {
            knLogger.error( "getCacheManager",
                    "Configuration Manager not initialized.");
            throw new KnXDMSystemError(KnErrorCodes.ConfigManager.INTERNAL_ERROR,
                    "Configuration Manager not initialized.");
        }
        return cacheManager;
    }

    //TODO : need to check the performance impact for synchronization of method.
    /**
     * This method will return the configuration manager
     * @return the configuration manager
     */
    public static synchronized KnConfigurationsManager getInstance(String libraryName) {
        knLogger.debug( "getInstance(String)", "ENTRY : Library Name -> " + libraryName);
        // if library name is null then return the global configuration manager
        if (libraryName == null) return getInstance();
//        knLogger.debug( "getInstance(String)", "Configuration Manager Instance Map : " + confMgrInsMap);
        KnConfigurationsManager libConfMgr = (KnConfigurationsManager) confMgrInsMap.get(libraryName);
        if (libConfMgr == null) {
            //first need to get global configuration manager.
            KnConfigurationsManager globalConfMgr = getInstance();
            //get the library specific configuration manager object
            KnLibraryConfig libraryConfigObj = globalConfMgr.getLibraryConfigObject(libraryName);
            if (libraryConfigObj != null) {
                try {
                    libConfMgr = new KnConfigurationsManager();
                    libConfMgr.setInitFile(libraryConfigObj.getXmlFile());
                    libConfMgr.initialize();
                    confMgrInsMap.put(libraryConfigObj.getName(), libConfMgr);
                } catch (KnConfigurationException e) {
                    knLogger.fatal( "getInstance",
                            "Unable to initialize configuration manager. Stopping application..");
                    throw new KnXDMServerSystemException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "Initialization failed.", e);
                }
            } /*else {
                // throws approporiate error....
            } */
            }
        return libConfMgr;
    }

    public static synchronized KnConfigurationsManager getInstance(String libraryName, String xmlFile) {
        knLogger.debug( "getInstance(String, String)", "ENTRY : Library Name -> " + libraryName + ", File Name - " + xmlFile);
        // if library name is null then return the global configuration manager
        if (libraryName == null) return getInstance();
//        knLogger.debug( "getInstance(String)", "Configuration Manager Instance Map : " + confMgrInsMap);
        KnConfigurationsManager libConfMgr = (KnConfigurationsManager) confMgrInsMap.get(libraryName);
        if (libConfMgr == null) {
            //first need to get global configuration manager.
            KnConfigurationsManager globalConfMgr = getInstance();
            //get the library specific configuration manager object
            KnLibraryConfig libraryConfigObj = globalConfMgr.getLibraryConfigObject(libraryName);
            if (libraryConfigObj != null) {
                xmlFile = libraryConfigObj.getXmlFile();
                knLogger.info( "getInstance(String, String)", "Library Config not found in library.xml ");
            }
            try {
                libConfMgr = new KnConfigurationsManager();
                libConfMgr.setInitFile(xmlFile);
                libConfMgr.initialize();
                confMgrInsMap.put(libraryName, libConfMgr);
            } catch (KnConfigurationException e) {
                knLogger.fatal( "getInstance(String, String)",
                        "Unable to initialize configuration manager. Stopping application..");
                throw new KnXDMServerSystemException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "Initialization failed.", e);
            }
        }
        return libConfMgr;
    }

    /**
     *
     * @param libraryName
     * @return
     */
    private KnLibraryConfig getLibraryConfigObject(String libraryName) {
        for (Iterator iterator = this.librariesConfigList.iterator(); iterator.hasNext(); ) {
            KnLibraryConfig configObj = (KnLibraryConfig) iterator.next();
            if (libraryName.equals(configObj.getName())) {
                return configObj;
            }
        }
        return null;
    }

    /**
     * This method will return the common configuration manager instance obj.
     * @return the configuration manager
     */
    public static KnConfigurationsManager getInstance() {
        knLogger.debug( "getInstance()", "ENTRY : ");
        KnConfigurationsManager globalConfMgr = (KnConfigurationsManager) confMgrInsMap.get(GLOBAL_LIB_CONFMGR);
        if (globalConfMgr == null) {
            try {
                globalConfMgr = new KnConfigurationsManager();
                globalConfMgr.setInitFile("/library.xml");
                globalConfMgr.initialize();
                confMgrInsMap.put(GLOBAL_LIB_CONFMGR, globalConfMgr);
            } catch (KnConfigurationException e) {
                knLogger.fatal( "getInstance",
                        "Unable to initialize configuration manager. Stopping application..");
                throw new KnXDMServerSystemException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "Initialization failed.", e);
            }
        }
        return globalConfMgr;
    }

    /**
     * This method will initialize the Configuration.
     * This will do the following things
     * 1. parse the initialization file
     * 2. init and load the cache manager based on the configuration
     * 3. init and load each module in the configuration file
     *
     * @throws KnConfigurationException if there is any
     */
    private void initialize() throws KnConfigurationException {

        knLogger.info( "initialize",
                "Initializing Configuration Manager...");

        initConfigInitializer();
        knLogger.info( "initialize",
                "Configuration data populated successfully.");

        //initialize cache manager
        initCacheManager();
        knLogger.info( "initialize",
                "Cache Manager lodaed successfully.");

        //initialize modules
        initModules();
        knLogger.info( "initialize",
                "All Modules loaded successfully ");

        knLogger.info( "initialize",
                "Configuration Manager Initialized sucecssfully.");

        isInitialized = true;
    }

    /**
     * This method will initialize the configuration initializer
     *
     * @throws KnInitializationException if there is any error while
     *                                   initializing
     */
    private void initConfigInitializer() throws KnConfigurationException {
        knLogger.debug( "initConfigInitializer",
                "Loading Configuration Initializer...");
        this.configInitializer = new KnConfigInitializer();
        knLogger.debug( "initConfigInitializer",
                "Configuration file : " + this.initFile);
        this.configInitializer.setInitFile(this.initFile);
        this.configInitializer.initialize();
        knLogger.debug( "initConfigInitializer",
                "Configuration Initializer initialized.");

        this.cacheManagerConfig = this.configInitializer.getCacheManagerConfig();
        this.modulesConfigList = this.configInitializer.getModulesConfig();
        this.librariesConfigList = this.configInitializer.getLibrariesConfig();
    }

    /**
     * This method will initialize the cache manager
     */
    private void initCacheManager() throws KnConfigurationException {
        String cacheManagerClass = null;

        try {
            cacheManagerClass = this.cacheManagerConfig.getManagerClass();
            knLogger.debug( "initCacheManager",
                    "Initilizing " + cacheManagerClass + "...");
            this.cacheManager = (ICacheManager) KnClassLoader
                    .createInstance(cacheManagerClass);

            this.cacheManager.initialize(this.cacheManagerConfig
                    .getCacheManagerConfigData());
            this.isCacheInitialized = true;
        } catch (KnClassLoaderException e) {
            knLogger.error( "initCacheManager",
                    "Error while initilizing cache manager class.");
            throw new KnConfigurationException(KnErrorCodes.ConfigManager.CONFIG_ERROR,
                    "Cannot load cache manager class " + cacheManagerClass,
                    e);
        } catch (KnCacheCreationException e) {
            knLogger.error( "initCacheManager",
                    "Error while creating cache manager.");
            throw new KnConfigurationException(KnErrorCodes.ConfigManager.INTERNAL_ERROR,
                    "error while creating cache manager", e);
        } finally {
            cacheManagerClass = null;
        }
    }

    /**
     * This method will initialize the modules
     */
    private void initModules() throws KnConfigurationException {
        Iterator it;
        KnModuleConfig modConfig;
        ICacheData cacheData;
        String moduleName;

        try {
            for (it = this.modulesConfigList.iterator(); it.hasNext();) {
                modConfig = (KnModuleConfig) it.next();
                moduleName = modConfig.getName();
                knLogger.info( "initModules",
                        "Loading module " + moduleName + "...");
                try {
                    IModuleLoader loader = modConfig.getLoader();
                    if (modConfig.hasToCache()) {
                        knLogger.debug( "initModules",
                                "Caching " + moduleName + "...");
                        cacheData = (ICacheData) KnClassLoader.createInstance(
                                modConfig.getCacheDataClass());
                        loader.setCacheData(cacheData);
                        loader.load();
                        this.cacheManager.doCache(moduleName, cacheData);
                        knLogger.info( "initModules",
                                "Module " + moduleName + " cached successfully.");
                    } else {
                        loader.setCacheData(null);
                        loader.load();
                    }
                    knLogger.debug( "initModules",
                            "Module " + moduleName + " loaded successfully.");
                } catch (KnClassLoaderException e) {
                    knLogger.error( "initModules",
                            "Cannot load datastructure class for module " + moduleName);
                    if (modConfig.isSevere()) {
                        throw new KnConfigurationException(KnErrorCodes.ConfigManager.CONFIG_ERROR,
                                "Cannot load severe module - " + moduleName, e);
                    }
                    knLogger.info( "initModules",
                            "Error ignored since the mdoule " + moduleName + " is not severe");
                } catch (KnCacheStoreException e) {
                    knLogger.error( "initModules",
                            "Cannot store data to cache for module " + moduleName);
                    if (modConfig.isSevere()) {
                        throw e;
                    }
                    knLogger.info( "initModules",
                            "Error ignored since the mdoule " + moduleName + " is not severe");
                } catch (KnLoaderException e) {
                    knLogger.error( "initModules",
                            "Cannot load the module " + moduleName);
                    if (modConfig.isSevere()) {
                        throw e;
                    }
                    knLogger.info( "initModules",
                            "Error ignored since the mdoule " + moduleName + " is not severe");
                }
            }
        } finally {
            it = null;
            modConfig = null;
            cacheData = null;
            moduleName = null;
        }
    }
}
