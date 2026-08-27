/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheClient.java
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

import com.kodiak.xdms.server.common.configuration.cache.ICacheClientIntf;

import java.util.Arrays;

/**
 * ICacheClientIntf implementation. Classes those do not implement
 * ICacheClientIntf and still may need to access Cache can use this
 * class to access the cache using this class.
 * <p/>
 * Usage :
 * <p/>
 *    KnCacheClient cacheClient = new KnCacheClient("VALIDATOR",
 *                                                  new String[] {"validator"},
 *                                                  "contactValidator");
 *    Object value = cacheManager.get(cacheClient);
 */
public class KnCacheClient implements ICacheClientIntf {

    /**
     * store the module name
     */
    private String moduleName;
    /**
     * store the parents list
     */
    private String[] parents;
    /**
     * store the id of the element
     */
    private String id;

    /**
     * constructs a cache client from module name, parents array and id
     *
     * @param moduleName the name of the module
     * @param parent     the array of parents
     * @param id         the id of the element
     */
    public KnCacheClient(String moduleName, String[] parent, String id) {
        this.moduleName = moduleName;
        if (parent != null) {
            this.parents = Arrays.copyOf(parent, parent.length);
        }
        this.id = id;
    }

    /**
     * return the module name
     *
     * @return the module name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * set the module name
     *
     * @param moduleName the name of the module
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * return the parents
     *
     * @return the parent array
     */
    public String[] getParents() {
        return parents;
    }

    /**
     * set the parents array
     *
     * @param parent the parents array
     */
    public void setParents(String[] parent) {
        if (parent != null) {
            this.parents = Arrays.copyOf(parent, parent.length);
    }
    }

    /**
     * return the id of the element
     *
     * @return the id of the element
     */
    public String getId() {
        return id;
    }

    /**
     * set the id of the element
     *
     * @param id the id of the element
     */
    public void setId(String id) {
        this.id = id;
    }
}
