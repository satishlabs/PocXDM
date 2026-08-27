/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnConfigInfoUtil.java
 * Subsystem:   GCCLib
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna        30-10-2010  6.4
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
package com.kodiak.library.activation.business.helper;

import com.kodiak.ems.base.itf.KnEMSConst;
import com.kodiak.library.activation.business.KnActBOException;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.library.activation.resources.KnCacheKeys;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.library.activation.resources.KnConstants;

import java.io.*;
import java.util.*;

/**
 * This class provides helper methods for reading configurations into cache manager.
 */
public class KnConfigInfoUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnConfigInfoUtil.class);

    public static final String CLASS = KnConfigInfoUtil.class.getName();
    private static KnConfigInfoUtil configInfoUtil;
    private static HashMap<String, Integer> serviceClientTypeMap = new HashMap<String, Integer>();

    private KnConfigInfoUtil() {
        serviceClientTypeMap.put(KnConstants.SERVICENAME_HANDSET, KnConstants.SUBS_CLIENTTYPE_HANDSET);
        serviceClientTypeMap.put(KnConstants.SERVICENAME_DESKTOP, KnConstants.SUBS_CLIENTTYPE_DESKTOP);
        serviceClientTypeMap.put(KnConstants.SERVICENAME_DISPATCHCLIENT, KnConstants.SUBS_CLIENTTYPE_DISPATCHCLIENT);
        serviceClientTypeMap.put(KnConstants.SERVICENAME_WIFIONLYCLIENT, KnConstants.SUBS_CLIENTTYPE_WIFIONLYCLIENT);
        serviceClientTypeMap.put(KnConstants.SERVICENAME_3RDPARTY_POCCLIENT, KnConstants.SUBS_CLIENTTYPE_3RDPARTY_POCCLIENT);
        serviceClientTypeMap.put(KnConstants.SERVICENAME_PTT_RADIO_CLIENT,KnConstants.SUBS_CLIENTTYPE_PTTRADIO_POCCLIENT);
    }

    public static KnConfigInfoUtil getInstance() {
        if (configInfoUtil == null) {
            knLogger.debug( "getInstance", "Creating a new instance...");
            configInfoUtil = new KnConfigInfoUtil();
        } else {
            knLogger.debug( "getInstance", "Returning existing instance...");
        }
        return configInfoUtil;
    }

    /**
     * Return Subscriber ClientType
     *
     * @return SubsClientType
     */

    public Integer getSubsClientType(String servicename) throws KnActBOException {
        String methodName = "getSubsClientType(String)";
        knLogger.debug( methodName, "ENTRY: Service Name - "+ servicename);
        Integer subsClientType = KnConstants.SUBS_CLIENTTYPE_UNKNOWN;
        try {
            if(serviceClientTypeMap != null){
                subsClientType = serviceClientTypeMap.get(servicename);
                if(subsClientType == null){
                    knLogger.error( methodName, "ServiceName not found in the Map");
                    throw new KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "ServiceName Not Valid");
                }
                knLogger.debug( methodName, "Fetched subs client type: " + subsClientType);
            } else{
                knLogger.debug( methodName, "Could not fetch subs client type");
            }
        } catch (Exception ioe) {
            knLogger.error( methodName, "IO Exception occured - " + ioe.getMessage());
            throw new KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "serviceClientTypeMap is Null", ioe);
        } finally {
            knLogger.debug( methodName, "EXIT : Subscriber Client Type -> " + subsClientType);
        }
        return subsClientType;
    }

    /**
     * Return Home PttServerId
     *
     * @return home ptt server id
     */
    public String getHomePttServerId() throws KnActBOException {
        String methodName = "getHomePttServerId()";
        knLogger.debug( methodName, "ENTRY : ");
        String homePttServerId = null;
        try {
            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            homePttServerId = (String) cacheManager.get(KnCacheKeys.CA_HOME_PTTSERVERID);
            if (homePttServerId == null) {
                Properties dbMgrProps = new Properties();
                String DB_MGR_FILE_PATH = KnEMSConst.getActiveReleasePath() + "/" + "dbmgr.props";
                FileInputStream fis = new FileInputStream(DB_MGR_FILE_PATH);
                dbMgrProps.load(fis);
                knLogger.debug( methodName, "Fetched from file. storing to Cache");
                homePttServerId = (String) dbMgrProps.get(KnConstants.WEB_PTTSERVERID);
                if (homePttServerId != null) {
                    homePttServerId = homePttServerId.trim();
                }
                cacheManager.put(KnCacheKeys.CA_HOME_PTTSERVERID, homePttServerId);
            } else {
                knLogger.debug( methodName, "Home Ptt Server Id found in Cache. Returning from there...");
            }
            return homePttServerId;
        } catch (IOException ioe) {
            knLogger.error( methodName, "IO Exception occured - " + ioe.getMessage());
            throw new KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "dbmgr.props file not found", ioe);
        } catch (KnConfigurationException e) {
            knLogger.error( methodName, "Configuration Exception occured - " + e.getMessage());
            throw new KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Error while retrieving dbmgr.props file data", e);
        } finally {
            knLogger.debug( methodName, "EXIT : Retrieve Home PttServer Id -> " + homePttServerId);
        }
    }

    /**
     * @return
     */
    public String getAppConfigPropValue(String propKey) throws KnActBOException {
        String methodName = "getAppConfigPropValue(String)";
        String propValue = null;
        try {
            propValue = (String) getAppConfigProps().get(propKey);
            knLogger.debug( methodName, "Prop Key : " + propKey + ", Value : " + propValue);
            if (propValue == null) {
                throw new KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "Prop values is not there in App Config for Key : " + propKey);
            }
            return propValue.trim();
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - " + e.getMessage());
            throw new KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Prop value not there in App Config file ", e);
        } finally {
            knLogger.debug( methodName, "EXIT : Prop Value -> " + propValue);
        }
    }

    /**
     * @return
     * @throws com.kodiak.library.activation.business.KnActBOException
     */
    public Properties getAppConfigProps() throws KnActBOException {
        String methodName = "getAppConfigProps";
        Properties appConfigProps = null;
        try {
            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            appConfigProps = (Properties) cacheManager.get(KnCacheKeys.APPLICATION_CONFIG_PROPS);
            knLogger.debug( methodName, "Application Config Properties : " + appConfigProps);
            if (appConfigProps == null) {
                appConfigProps = new Properties();
                String appConfigFilePath = System.getProperty(KnConstants.CONFIG_PATH_NAME);
                String appConfigFileWithPath = appConfigFilePath + "/" + KnConstants.APP_CONFIG_FILE_NAME;
                knLogger.debug( methodName, "Application Config Properties file with path : " + appConfigFileWithPath);
                try {
                    appConfigProps.load(new FileInputStream(appConfigFileWithPath));
                } catch (IOException ioe) {
                    knLogger.info( methodName, "Unable to load app config file properties.");
                    ioe.printStackTrace();
                }
                cacheManager.put(KnCacheKeys.APP_CONFIG_PROPS, appConfigProps);
            }
            return appConfigProps;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - " + e.getMessage());
            throw new KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Exception while loading App Config file ", e);
        } finally {
            knLogger.debug( methodName, "EXIT : App Config Prop -> ");
        }
    }
}