/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IModuleLoaderConfigData.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         15-03-2007  6.0
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

import com.kodiak.xdms.server.common.configuration.IConfigData;


/**
 * This interface defines the basic methods all the source specific loader
 * config data classes should implement.
 * The configuration data for loader will be done in initialization xml
 * file as given below
 * <loader-config-data>
 *      loader specific tags
 * </loader-config-data>
 *
 * eg: for xml loader
 * <loader-config-data>
 * ..<xml>
 * ....<xmlFile>sample.xml</xmlFile>
 * ..</xml>
 * </loader-config-data>
 */
public interface IModuleLoaderConfigData extends IConfigData {

    /**
     * This method will set the module name
     *
     * @param moduleName the module name
     */
    void setModuleName(String moduleName);
}
