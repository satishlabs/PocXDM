/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnValidatorConfigCached.java
 * Subsystem:   Validator Framework
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
package com.kodiak.xdms.server.common.framework.validator;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheClientIntf;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.util.Arrays;

/**
 * validator config cache
 */
public class KnValidatorConfigCache implements ICacheClientIntf {
	private static final KnLogger knLogger = KnLogger.getLogger(KnValidatorConfigCache.class);

    private static final String CLASS = KnValidatorConfigCache.class.getName();
    protected String moduleRoot = null;
    protected String parents[] = null;
    protected String elementID = null;
    // holds the configuration manager object
    KnConfigurationsManager configManager = null;

    /**
     * this will construts the ConfigCache object with configuration manager.
     * @param configManager
     */
    public KnValidatorConfigCache(KnConfigurationsManager configManager) {
        this.configManager = configManager;
    }

    public void setModuleName(String moduleName) {
        this.moduleRoot = moduleName;
    }

    public String getModuleName() {
        return moduleRoot;
    }

    public String getParent() {
        return parents[0];
    }

    public void setId(String id) {
        this.elementID = id;
    }

    public String getId() {
        return elementID;
    }

    public void setParent(String parent) {
        this.parents = new String[1];
        this.parents[0] = parent;
    }

    public void setParents(String[] parent) {
        if(parent != null){
        this.parents = Arrays.copyOf(parent, parent.length);
    }
    }

    public String[] getParents() {
        return parents;
    }


    public Object getCachedValue(ICacheClientIntf configCache) {
        try {
            return this.configManager.getCacheManager().get(configCache);
        } catch (KnConfigurationException e) {
            knLogger.error( "KnGenValidatorOperation",
                    "Cache invokation Exception occured : module - " + moduleRoot + " : parents - " +
                            " : element id - " + elementID);
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.CONFIG_ERROR, "Cache invokation failed : module - " + moduleRoot +  " : parents - " +
                                                 " : element id - "+elementID, e);
        }
    }

}
