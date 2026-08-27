/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   ICacheClientIntf.java
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
package com.kodiak.xdms.server.common.configuration.cache;

/**
 * this is the interface for accessing the cache manager. All the client
 * programs which needs to access the cache should implement this interface.
 */
public interface ICacheClientIntf {

    /**
     * This method will set the module name.
     * @param moduleName the name of the module
     */
    public void setModuleName(String moduleName);

    /**
     * This method will return the module name.
     * @return the name of the module
     */
    public String getModuleName();

    /**
     * This method will set the parents
     * @param args the parents array
     */
    public void setParents(String[] args);

    /**
     * This method will return the parents
     * @return the parent array
     */
    public String[] getParents();

    /**
     * This method will set the id of the object
     * @param id the id
     */
    public void setId(String id);

    /**
     * This method will return the id
     * @return the id
     */
    public String getId();

}
