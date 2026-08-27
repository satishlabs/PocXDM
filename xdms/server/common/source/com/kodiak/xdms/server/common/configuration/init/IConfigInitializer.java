/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IConfigInitializer.java
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

package com.kodiak.xdms.server.common.configuration.init;


import com.kodiak.xdms.server.common.configuration.KnInitializationException;
import com.kodiak.xdms.server.common.configuration.KnCacheManagerConfig;

import java.util.Collection;

/**
 * This interface defines the methods for a Configuration initizer.
 */
public interface IConfigInitializer {

    /**
     * This method will set the init filename. Initialization file
     * will have configurations for each module.
     *
     * @param fileName
     */
    void setInitFile(String fileName);

    /**
     * This method will read the init file, parse it and populateDto the
     * module configuration data structures. These data structures
     * will be exposed to outside up on request.
     *
     * @throws KnInitializationException if there is any error while
     *                                   initialization
     */
    void initialize() throws KnInitializationException;

    /**
     * This method will return a collection of module configuration data.
     * Each object in that collection will have configuration data for
     * the loading of the respective module.
     *
     * @return Collection of Initialization data
     */
    Collection getModulesConfig();


    /**
     * This method will return the configuration data for Cache Manager
     *
     * @return return the configuration data for Cache Manager.
     */
    KnCacheManagerConfig getCacheManagerConfig();

    /**
     * This method will return a collection of library configuration data objects
     * Each object in collection will contains configuration data for library to initializes.
     * @return Collection of Library initialization data objects
     */
    Collection getLibrariesConfig();
}
