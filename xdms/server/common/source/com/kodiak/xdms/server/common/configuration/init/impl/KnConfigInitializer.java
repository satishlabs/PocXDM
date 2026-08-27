/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnConfigInitializer.java
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

package com.kodiak.xdms.server.common.configuration.init.impl;


import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.configuration.init.*;
import com.kodiak.xdms.server.common.configuration.*;
import com.kodiak.xdms.server.common.configuration.loader.IModuleLoader;
import com.kodiak.xdms.server.common.configuration.loader.IModuleLoaderConfigData;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManagerInitConfigData;
import com.kodiak.xdms.server.common.util.KnClassLoader;
import com.kodiak.xdms.server.common.util.KnClassLoaderException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * This class is responsible for the initilization of modules.
 * It will parse the initialization xml and build a data structure
 * as per the configurations given in the xml. The configuration
 * manager will use this data structure for loading of the modules.
 * <p/>
 * the schema of the xml will be
 * <gp-library>
 * ..<cache>
 * ....<manager-class> KnCacheManager </manager-class>
 * ....<manager-config-class>KnCacheConfigData</manager-config-class>
 * ....<manager-config-data>
 * ............ manager config data specific tags
 * ....</manager-config-data>
 * ..</cache>
 * ..<modules>
 * ....<module>
 * ......<name>VALIDATOR</name>
 * ......<loader-class> KnXMLConfigLoader </loader-class-name>
 * ........<cache-data-class>KnXMLData</cache-data-class>
 * ........<loader-config-data>
 * ............ loader config data specific tags
 * ........</loader-config-data>
 * ........<severity/>
 * ....</module>
 * ..</modules>
 * </gp-library>
 */
public class KnConfigInitializer implements IConfigInitializer {
	private static final KnLogger knLogger = KnLogger.getLogger(KnConfigInitializer.class);

    private static final String CLASS = KnConfigInitializer.class.getName();
    // the below constants used for parsing initialization xml (gp-library.xml)

    /**
     * cache related tag constants.
     */
    public static final String CACHE_TAG = "cache";
    public static final String CACHE_MANAGER_CLASS_TAG = "manager-class";
    public static final String CACHE_MANAGER_CONFIG_CLASS_TAG = "manager-config-class";
    public static final String CACHE_MANAGER_CONFIG_DATA_TAG = "manager-config-data";

    /**
     * module related tag constants
     */
    public static final String MODULES_TAG = "modules";
    public static final String MODULE_TAG = "module";
    public static final String MODULE_NAME_TAG = "name";
    public static final String MODULE_LOADER_CLASS_TAG = "loader-class";
    public static final String MODULE_LOADER_CONFIG_DATA_TAG = "loader-config-data";
    public static final String MODULE_SEVERE_TAG = "severe";
    public static final String MODULE_CACHE_DATA_CLASS_TAG = "cache-datastructure-class";

    public static final String LIBRARIES_TAG = "libraries";
    public static final String LIBRARY_TAG = "library";
    public static final String LIBRARY_NAME_TAG = "name";
    public static final String LIBRARY_CONFIG_CLASS_TAG = "library-config-class";
    public static final String LIBRARY_CONFIG_DATA_TAG = "library-config-data";

    /**
     * This holds the path of initialization xml
     */
    private String initFile;

    /**
     * This stores the collection of Module configuration data
     */
    private Collection moduleConfigDataList;

    // stores the collection of library configuration data
    private Collection libraryConfigDataList;

    /**
     * This stores the Cache manager configuration data
     */
    private KnCacheManagerConfig cacheManagerConfigData;

    /**
     * this method will set the initialization xml file
     *
     * @param initFile the initialization xml file path
     */
    public void setInitFile(String initFile) {
        this.initFile = initFile;
    }

    /**
     * This method will return the data structure.
     *
     * @return collection of Module Configuration data
     */
    public Collection getModulesConfig() {
        return moduleConfigDataList;
    }

    /**
     * this method will return the library configuration objects
     *
     * @return collection of library configuration data objects
     */
    public Collection getLibrariesConfig() {
        return libraryConfigDataList;
    }

    /**
     * This method will return the configuration data for Cache Manager
     *
     * @return return the configuration data for Cache Manager.
     */
    public KnCacheManagerConfig getCacheManagerConfig() {
        return cacheManagerConfigData;
    }

    /**
     * This method will do the XML parsing and populateDto the data structures.
     * These data structures will be exposed to outside up on request.
     *
     * @throws KnInitializationException if there is any error while
     *                                   initialization
     */
    public void initialize() throws KnInitializationException {
        DocumentBuilder domBuilder;
        Document document;
        Element configurationElement;
        NodeList cacheNodeList;
        NodeList modulesNodeList;
        NodeList librariesNodeList;
        knLogger.debug( "initialize",
                "Initializing....");
        try {

            knLogger.debug( "initialize",
                    "Parsing the file : " + this.initFile);
            domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = domBuilder.parse(getClass().getResourceAsStream(this.initFile));
            configurationElement = document.getDocumentElement();
            knLogger.debug( "initialize",
                    "File parsed successfully. rootElement : " + configurationElement);

            //read cache configurations
            knLogger.debug( "initialize",
                    "Fetching Cache configurations...");
            cacheNodeList = configurationElement.getElementsByTagName(CACHE_TAG);
            readCacheConfig((Element) cacheNodeList.item(0));

            //read module configurations
            knLogger.debug( "initialize",
                    "Fetching Modules configurations...");
            modulesNodeList = configurationElement.getElementsByTagName(MODULES_TAG);
            readModulesConfig((Element) modulesNodeList.item(0));

            //read library configurations
            knLogger.debug( "initialize",
                    "Fetching Libraries configuration data...");
            librariesNodeList = configurationElement.getElementsByTagName(LIBRARIES_TAG);
            if (librariesNodeList.getLength() == 1) {
                readLibrariesConfig((Element) librariesNodeList.item(0));
            } else if (librariesNodeList.getLength() > 1) {
                knLogger.error( "initialize",
                        "<" + LIBRARIES_TAG + "> tag is repeating in " + this.initFile + ", Tag Count : " + librariesNodeList.getLength());
                throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                        "<" + LIBRARIES_TAG + "> tag is repeating in " + this.initFile,
                        LIBRARIES_TAG, null, librariesNodeList.item(0).getFirstChild().getNodeValue());
            }

            knLogger.debug( "initialize",
                    "Initialization Completed Sucessfully.");
        } catch (ParserConfigurationException e) {
            knLogger.error( "initialize",
                    "XML ParserConfiguration error - " + e.getMessage());
            throw new KnInitializationException(KnErrorCodes.ConfigInitializer.INTERNAL_ERROR,
                    "Parser Configuration not available", e);
        } catch (IOException e) {
            knLogger.error( "initialize",
                    "IOException - " + e.getMessage());
            throw new KnInitFileException(KnErrorCodes.ConfigInitializer.INTERNAL_ERROR,
                    "IO Error while Initializing",
                    e, initFile);
        } catch (SAXException e) {
            knLogger.error( "initialize",
                    "SAX Exception - " + e.getMessage());
            throw new KnInitFileException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                    "Parsing error while Initializing",
                    e, initFile);
        } catch (Exception e) {
            knLogger.error( "initialize",
                    e.getMessage());
            throw new KnInitFileException(KnErrorCodes.ConfigInitializer.INTERNAL_ERROR,
                    "Error while initializing",
                    e, initFile);
        } finally {
            domBuilder = null;
            document = null;
            configurationElement = null;
            cacheNodeList = null;
            modulesNodeList = null;
        }
    }

    /**
     * This method will read the library configuration data for the library initialization from XML file.
     * The schema will be as follows
     * <libraries>
     * ..<library>
     * .................
     * ..</library>
     * ..<library>
     * .................
     * ..</library>
     * </libraries>
     *
     * @param element
     * @throws KnInitLibraryConfigException
     */
    private void readLibrariesConfig(Element element) throws KnInitLibraryConfigException {
        String methodName = "readLibrariesConfig";
        NodeList nodeList;
        NodeList libraryList;
        Element libraryElement;
        Node node;
        String value;

        String libraryName;
        String libraryConfigClass;
        KnLibraryInitConfigData configData = null;

        knLogger.debug( "readLibrariesConfig",
                "Reading Libraries configuration data...");
        this.libraryConfigDataList = new ArrayList();
        try {
            libraryList = element.getElementsByTagName(LIBRARY_TAG);
            for (int i = 0, len = libraryList.getLength(); i < len; i++) {
                libraryElement = (Element) libraryList.item(i);

                knLogger.debug( methodName,
                        "Reading Library...");
                nodeList = libraryElement.getElementsByTagName(LIBRARY_NAME_TAG);

                if (nodeList.getLength() == 1) {
                    node = nodeList.item(0);
                    value = node.getFirstChild().getNodeValue();
                    if (value == null || node.toString().equals("")) {
                        knLogger.error( methodName,
                                "Empty value for <" + LIBRARY_NAME_TAG + "> tag under <" + LIBRARY_TAG + "> tag in " + this.initFile);
                        throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "Library Name empty in " + this.initFile,
                                LIBRARY_NAME_TAG, value, null);
                    } else {
                        libraryName = value;
                    }
                } else if (nodeList.getLength() == 0) {
                    knLogger.error( methodName,
                            "<" + LIBRARY_NAME_TAG + "> tag under <" + LIBRARY_TAG + "> tag is missing in " + this.initFile);
                    throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "Module Config Parameter missing in " + this.initFile,
                            LIBRARY_NAME_TAG, null, null);
                } else {
                    knLogger.error( methodName,
                            "<" + LIBRARY_NAME_TAG + "> tag under <" + LIBRARY_TAG + "> tag is repeating in " + this.initFile);
                    throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "Module Config Parameter repeating in " + this.initFile,
                            LIBRARY_NAME_TAG, null, null);
                }

                knLogger.debug( methodName,
                        "Reading Library " + libraryName);
                nodeList = libraryElement.getElementsByTagName(LIBRARY_CONFIG_CLASS_TAG);
                if (nodeList.getLength() == 1) {
                    node = nodeList.item(0);
                    value = node.getFirstChild().getNodeValue();
                    if (value == null || node.toString().equals("")) {
                        knLogger.error( methodName,
                                "Empty <" + LIBRARY_CONFIG_CLASS_TAG + "> value for library " + libraryName);
                        throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "the <" + LIBRARY_CONFIG_CLASS_TAG + "> tag is empty",
                                LIBRARY_CONFIG_CLASS_TAG, value, libraryName);
                    } else {
                        libraryConfigClass = value;
                    }
                } else if (nodeList.getLength() == 0) {
                    knLogger.error( methodName,
                            "<" + LIBRARY_CONFIG_CLASS_TAG + "> is missing for library " + libraryName);
                    throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "the <" + LIBRARY_CONFIG_CLASS_TAG + "> tag is missing",
                            LIBRARY_CONFIG_CLASS_TAG, null, libraryName);
                } else {
                    knLogger.error( methodName,
                            "More than one <" + LIBRARY_CONFIG_CLASS_TAG + "> tag for library " + libraryName);
                    throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "the <" + LIBRARY_CONFIG_CLASS_TAG + "> tag under <" + LIBRARY_TAG + "> tag is repeating",
                            LIBRARY_CONFIG_CLASS_TAG, null, libraryName);
                }

                nodeList = libraryElement.getElementsByTagName(LIBRARY_CONFIG_DATA_TAG);
                if (nodeList.getLength() == 1) {
                    try {
                        configData = new KnLibraryInitConfigData();
                        configData.setLibraryName(libraryName);
                        configData.populate((Element) nodeList.item(0));
                    } catch (KnInitConfigException e) {
                        knLogger.error( methodName,
                                e.getMessage());
                        throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "Error while populating the library config",
                                e, LIBRARY_CONFIG_DATA_TAG, null, libraryName);
                    }
                } else if (nodeList.getLength() == 0) {
                    knLogger.error( methodName,
                            "<" + LIBRARY_CONFIG_DATA_TAG + "> tag is missing for library " + libraryName);
                    throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "tag <" + LIBRARY_CONFIG_DATA_TAG + "> is missing",
                            LIBRARY_CONFIG_DATA_TAG, null, libraryName);
                } else {
                    knLogger.error( methodName,
                            "<" + LIBRARY_CONFIG_DATA_TAG + "> tag is repeating for library " + libraryName);
                    throw new KnInitLibraryConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "tag <" + LIBRARY_CONFIG_DATA_TAG + "> is repeating",
                            LIBRARY_CONFIG_DATA_TAG, null, libraryName);
                }

                KnLibraryConfig libraryConfig = new KnLibraryConfig();
                libraryConfig.setName(libraryName);
                libraryConfig.setConfigClass(libraryConfigClass);
                libraryConfig.setConfigData(configData);

                knLogger.debug( methodName,
                        "Library '" + libraryName + "' read successfully.");
                this.libraryConfigDataList.add(libraryConfig);
            }
            knLogger.info( methodName,
                    "Libraries configuration read successfully");
        } finally {
            nodeList = null;
            libraryList = null;
            libraryElement = null;
            node = null;
            value = null;

            libraryName = null;
            libraryConfigClass = null;
        }
    }

    /**
     * This method will read the cache configuration data from the XML.
     * The schema will be as follows
     * <cache>
     * ..<manager-class> KnCacheManager </manager-class>
     * ..<manager-config-class>KnCacheConfigData</manager-config-class>
     * ..<manager-config-data>
     * ........manager config data specific tags
     * ..</manager-config-data>
     * </cache>
     *
     * @param element refers the '<cache>' tag
     * @throws KnInitCacheConfigException
     */
    private void readCacheConfig(Element element) throws KnInitCacheConfigException {
        NodeList managerClassList;
        NodeList configClassList;
        NodeList configDataList;
        Node node;
        String value;
        knLogger.debug( "readCacheConfig",
                "Reading Cache configurations...");
        try {
            cacheManagerConfigData = new KnCacheManagerConfig();

            //get the manager class
            managerClassList = element.getElementsByTagName(CACHE_MANAGER_CLASS_TAG);
            if (managerClassList.getLength() == 1) {
                node = managerClassList.item(0);
                value = node.getFirstChild().getNodeValue();
                knLogger.debug( "readCacheConfig",
                        "manager class : " + value);
                cacheManagerConfigData.setManagerClass(value);
            } else if (managerClassList.getLength() == 0) {
                knLogger.error( "readCacheConfig",
                        "<" + CACHE_MANAGER_CLASS_TAG + "> tag under <" + CACHE_TAG + "> tag is missing in " + this.initFile);
                throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                        "Cache Config Parameter is missing in " + this.initFile,
                        CACHE_MANAGER_CLASS_TAG, null);
            } else {
                knLogger.error( "readCacheConfig",
                        "<" + CACHE_MANAGER_CLASS_TAG + "> tag under <" + CACHE_TAG + "> tag is missing in " + this.initFile);
                throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                        "Cache Config Parameter is repeating in " + this.initFile,
                        CACHE_MANAGER_CLASS_TAG, null);
            }

            // get the configuration class name
            configClassList = element
                    .getElementsByTagName(CACHE_MANAGER_CONFIG_CLASS_TAG);
            if (configClassList.getLength() == 1) {
                node = configClassList.item(0);
                value = node.getFirstChild().getNodeValue();
                knLogger.debug( "readCacheConfig",
                        "config class : " + value);
                cacheManagerConfigData.setManagerConfigClass(value);
            } else if (configClassList.getLength() == 0) {
                knLogger.error( "readCacheConfig",
                        "<" + CACHE_MANAGER_CONFIG_CLASS_TAG + "> tag under <" + CACHE_TAG + "> tag is missing in " + this.initFile);
                throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                        "Cache Config Parameter is missing in " + this.initFile,
                        CACHE_MANAGER_CONFIG_CLASS_TAG, null);
            } else {
                knLogger.error( "readCacheConfig",
                        "<" + CACHE_MANAGER_CONFIG_CLASS_TAG + "> tag under <" + CACHE_TAG + "> tag is missing in " + this.initFile);
                throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                        "Cache Config Parameter is repeating in " + this.initFile,
                        CACHE_MANAGER_CONFIG_CLASS_TAG, null);
            }

            // populateDto the configuration data
            configDataList = element.getElementsByTagName(CACHE_MANAGER_CONFIG_DATA_TAG);
            if (configDataList.getLength() == 1) {
                ICacheManagerInitConfigData configData;
                try {
                    configData = (ICacheManagerInitConfigData) KnClassLoader.createInstance(cacheManagerConfigData.getManagerConfigClass());
                    knLogger.debug( "readCacheConfig",
                            "populating cache config data");
                    configData.populate((Element) configDataList.item(0));
                    cacheManagerConfigData.setCacheManagerConfigData(configData);
                } catch (KnClassLoaderException e) {
                    knLogger.error( "readCacheConfig",
                            e.getMessage());
                    throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "Cannot instantiate cache configuration data class ",
                            e, CACHE_MANAGER_CONFIG_CLASS_TAG,
                            cacheManagerConfigData.getManagerConfigClass());
                } catch (KnInitConfigException e) {
                    knLogger.error( "readCacheConfig",
                            e.getMessage());
                    throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "Error while populating caching configuration data " + this.initFile,
                            e, CACHE_MANAGER_CONFIG_CLASS_TAG,
                            cacheManagerConfigData.getManagerConfigClass());
                } finally {
                    configData = null;
                }
            } else if (configDataList.getLength() == 0) {
                knLogger.error( "readCacheConfig",
                        "<" + CACHE_MANAGER_CONFIG_DATA_TAG + "> tag under <" + CACHE_TAG + "> tag is missing in " + this.initFile);
                throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                        "Cache Config Parameter is missing in " + this.initFile,
                        CACHE_MANAGER_CONFIG_DATA_TAG, null);
            } else {
                knLogger.error( "readCacheConfig",
                        "<" + CACHE_MANAGER_CONFIG_DATA_TAG + "> tag under <" + CACHE_TAG + "> tag is repeating in " + this.initFile);
                throw new KnInitCacheConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                        "Cache Config Parameter is repeating in  " + this.initFile,
                        CACHE_MANAGER_CONFIG_DATA_TAG, null);
            }
            knLogger.debug( "readCacheConfig",
                    "Cache configurations : " + cacheManagerConfigData);
            knLogger.info( "readCacheConfig",
                    "Cache configurations read successfully");
        } finally {
            managerClassList = null;
            configClassList = null;
            configDataList = null;
            value = null;
        }
    }

    /**
     * This method will read the module configuration data from the xml
     * the schema will be as follows
     * <modules>
     * ..<module>
     * .................
     * ..</module>
     * ..<module>
     * .................
     * ..</module>
     * </modules>
     *
     * @param element refers the <modules> tag
     * @throws KnInitModuleConfigException
     */
    private void readModulesConfig(Element element) throws KnInitModuleConfigException {
        NodeList nodeList;
        NodeList moduleList;
        Element moduleElement;
        Node node;
        String value;

        String moduleName;
        String loaderClass;
        IModuleLoader loader;
        boolean severe = false;
        String cacheDataClass;

        knLogger.debug( "readModulesConfig",
                "Reading Modules configuration...");
        this.moduleConfigDataList = new ArrayList();
        try {
            moduleList = element.getElementsByTagName(MODULE_TAG);
            for (int i = 0, len = moduleList.getLength(); i < len; i++) {
                moduleElement = (Element) moduleList.item(i);

                knLogger.debug( "readModulesConfig",
                        "Reading Module...");
                nodeList = moduleElement.getElementsByTagName(MODULE_NAME_TAG);

                if (nodeList.getLength() == 1) {
                    node = nodeList.item(0);
                    value = node.getFirstChild().getNodeValue();
                    if (value == null || node.toString().equals("")) {
                        knLogger.error( "readModulesConfig",
                                "Empty value for <" + MODULE_NAME_TAG + "> tag under <" + MODULE_TAG + "> tag in " + this.initFile);
                        throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "Module Name empty in " + this.initFile,
                                MODULE_NAME_TAG, value, null);
                    } else {
                        moduleName = value;
                    }
                } else if (nodeList.getLength() == 0) {
                    knLogger.error( "readModulesConfig",
                            "<" + MODULE_NAME_TAG + "> tag under <" + MODULE_TAG + "> tag is missing in " + this.initFile);
                    throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "Module Config Parameter missing in " + this.initFile,
                            MODULE_NAME_TAG, null, null);
                } else {
                    knLogger.error( "readModulesConfig",
                            "<" + MODULE_NAME_TAG + "> tag under <" + MODULE_TAG + "> tag is repeating in " + this.initFile);
                    throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "Module Config Parameter repeating in " + this.initFile,
                            MODULE_NAME_TAG, null, null);
                }

                knLogger.debug( "readModulesConfig",
                        "Reading Module " + moduleName);
                nodeList = moduleElement.getElementsByTagName(MODULE_LOADER_CLASS_TAG);
                if (nodeList.getLength() == 1) {
                    node = nodeList.item(0);
                    value = node.getFirstChild().getNodeValue();
                    if (value == null || node.toString().equals("")) {
                        knLogger.error( "readModulesConfig",
                                "Empty <" + MODULE_LOADER_CLASS_TAG + "> value for module " + moduleName);
                        throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "the <" + MODULE_LOADER_CLASS_TAG + "> tag is empty",
                                MODULE_LOADER_CLASS_TAG, value, moduleName);
                    } else {
                        loaderClass = value;
                    }
                } else if (nodeList.getLength() == 0) {
                    knLogger.error( "readModulesConfig",
                            "<" + MODULE_LOADER_CLASS_TAG + "> is missing for module " + moduleName);
                    throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "the <" + MODULE_LOADER_CLASS_TAG + "> tag is missing",
                            MODULE_LOADER_CLASS_TAG, null, moduleName);
                } else {
                    knLogger.error( "readModulesConfig",
                            "More than one <" + MODULE_LOADER_CLASS_TAG + "> tag for module " + moduleName);
                    throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "the <" + MODULE_LOADER_CLASS_TAG + "> tag under <" + MODULE_TAG + "> tag is repeating",
                            MODULE_LOADER_CLASS_TAG, null, moduleName);
                }

                nodeList = moduleElement.getElementsByTagName(MODULE_LOADER_CONFIG_DATA_TAG);
                if (nodeList.getLength() == 1) {
                    try {
                        loader = (IModuleLoader) KnClassLoader.createInstance(loaderClass);
                        IModuleLoaderConfigData configData = loader.getConfigData();
                        if (configData != null) {
                            configData.setModuleName(moduleName);
                            configData.populate((Element) nodeList.item(0));
                        }
                    } catch (KnClassLoaderException e) {
                        knLogger.error( "readModulesConfig",
                                e.getMessage());
                        throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "Error while Creating the loader class",
                                e, MODULE_LOADER_CONFIG_DATA_TAG, null, moduleName);
                    } catch (KnInitConfigException e) {
                        knLogger.error( "readModulesConfig",
                                e.getMessage());
                        throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "Error while populating the module loader config",
                                e, MODULE_LOADER_CONFIG_DATA_TAG, null, moduleName);
                    }
                } else if (nodeList.getLength() == 0) {
                    knLogger.error( "readModulesConfig",
                            "<" + MODULE_LOADER_CONFIG_DATA_TAG + "> tag is missing for module " + moduleName);
                    throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "tag <" + MODULE_LOADER_CONFIG_DATA_TAG + "> is missing",
                            MODULE_LOADER_CONFIG_DATA_TAG, null, moduleName);
                } else {
                    knLogger.error( "readModulesConfig",
                            "<" + MODULE_LOADER_CONFIG_DATA_TAG + "> tag is repeating for module " + moduleName);
                    throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "tag <" + MODULE_LOADER_CONFIG_DATA_TAG + "> is repeating",
                            MODULE_LOADER_CONFIG_DATA_TAG, null, moduleName);
                }

                nodeList = moduleElement.getElementsByTagName(MODULE_SEVERE_TAG);
                severe = false;
                if (nodeList.getLength() >= 1) {
                    severe = true;
                }

                cacheDataClass = null;
                nodeList = moduleElement
                        .getElementsByTagName(MODULE_CACHE_DATA_CLASS_TAG);
                if (nodeList.getLength() == 1) {
                    node = nodeList.item(0);
                    value = node.getFirstChild().getNodeValue();
                    if (value == null || node.toString().equals("")) {
                        knLogger.error( "readModulesConfig",
                                "<" + MODULE_CACHE_DATA_CLASS_TAG + "> is empty for module " + moduleName);
                        throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                                "tag <" + MODULE_CACHE_DATA_CLASS_TAG + "> is empty or null",
                                MODULE_CACHE_DATA_CLASS_TAG, value, moduleName);
                    } else {
                        cacheDataClass = value;
                    }
                } else if (nodeList.getLength() > 1) {
                    knLogger.error( "readModulesConfig",
                            "<" + MODULE_CACHE_DATA_CLASS_TAG + "> is repeating for module " + moduleName);
                    throw new KnInitModuleConfigException(KnErrorCodes.ConfigInitializer.CONFIG_ERROR,
                            "tag <" + MODULE_CACHE_DATA_CLASS_TAG + "> is repeating",
                            MODULE_CACHE_DATA_CLASS_TAG, null, moduleName);
                }

                KnModuleConfig moduleConfig = new KnModuleConfig();
                moduleConfig.setName(moduleName);
                moduleConfig.setLoaderClass(loaderClass);
                moduleConfig.setLoader(loader);
                moduleConfig.setSevere(severe);
                moduleConfig.setCacheDataClass(cacheDataClass);
                knLogger.debug( "readModulesConfig",
                        "Module '" + moduleName + "' read successfully.");
                this.moduleConfigDataList.add(moduleConfig);
            }
            knLogger.info( "readModulesConfig",
                    "Modules configuration read successfully");
        } finally {
            nodeList = null;
            moduleList = null;
            moduleElement = null;
            node = null;
            value = null;

            moduleName = null;
            loaderClass = null;
            loader = null;
            cacheDataClass = null;
        }
    }
}
