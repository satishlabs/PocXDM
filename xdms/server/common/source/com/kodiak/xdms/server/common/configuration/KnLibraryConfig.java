/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnLibraryConfig
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         09-05-2007 6.0
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

/**
 * This class will hold the configuration data for a library.
 */
public class KnLibraryConfig implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676260L;

    /**
     * This holds the name of the library
     */
    private String name;

    private String configClass;

    /**
     * This holds class name for
     */
    private KnLibraryInitConfigData configData;
    private String objectId;

    public KnLibraryConfig() {
        this.name = null;
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
     * this method will returns the library configuration data
     * @return config object
     */
    public KnLibraryInitConfigData getConfigData() {
        return configData;
    }

    /**
     * this method will set the library configuration data.
     * @param configData
     */
    public void setConfigData(KnLibraryInitConfigData configData) {
        this.configData = configData;
    }

    public String getConfigClass() {
        return configClass;
    }

    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    /**
     * return the xml file name of the library object
     * @return returns library-name
     */
    public String getXmlFile() {
        return this.configData.getXmlFile();
    }

    public String getObjectId() {
        return objectId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
