/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IConfigData.java
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

import org.w3c.dom.Element;

import java.io.Serializable;

import com.kodiak.xdms.server.common.configuration.init.KnInitConfigException;

/**
 * This is the base interface for all the configuration data classes
 */
public interface IConfigData extends Serializable {

    /**
     * This method will populateDto the fields from the dom element.
     * The element object passed to this method will be a dom
     * representation of the tag <loader-config-data>
     *
     * @param element dom object of the tag '<loader-config-data>'
     * @throws KnInitConfigException if the loader configuration data
     *                               is missing or invalid
     */
    void populate(Element element) throws KnInitConfigException;

}
