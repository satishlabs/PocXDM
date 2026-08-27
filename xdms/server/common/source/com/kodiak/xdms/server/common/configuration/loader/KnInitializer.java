/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * File name:   KnInitializer.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- -------
 * Rama Krishna        15-03-2007  6.0
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
 * you entered into with Kodiak Networks..
 */

package com.kodiak.xdms.server.common.configuration.loader;

import com.kodiak.logger.KnLogger;
import com.kodiak.ems.base.itf.KnEMSConst;
import com.kodiak.ems.base.utils.KnLicenseInfoReader;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.xdms.server.common.configuration.KnLoaderException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * This class shall be invoked from KnConfigurationsManager.
 * It shall initialize the following, based on initialization
 * information loaded by KnInitializerConfigData:
 * DB, License Manager.
 * NB: Log manager is being initialized seperately from client-interface
 * as it needs to be initialized 'first', as all the other initialization
 * information needs to be written to logs.
 */
public class KnInitializer extends KnModuleLoader {
	private static final KnLogger knLogger = KnLogger.getLogger(KnInitializer.class);

    private static final String CLASS = KnInitializer.class.getName();
    /**
     * store the loader configuration data
     */
    KnInitializerConfigData loaderConfigData;


    public KnInitializer() {
        this.loaderConfigData = new KnInitializerConfigData();
    }


    /**
     * This method shall get the values required for initialization,
     * from the KnInitializerConfigData instance, and initialize
     * the different module(s) based on these values
     *
     * @return null
     * @throws KnLoaderException
     */
    public ICacheData load() throws KnLoaderException {
        try {
            //Initialize the License manager
            KnLicenseInfoReader licReader = KnLicenseInfoReader.getInstance();
            int cardType = loaderConfigData.getCardType();
            System.out.println("DB File path: " + loaderConfigData.getDbFilePath());
            System.out.println("License File path: " + loaderConfigData.getLicParserPath());
            System.out.println("Card Type : " + cardType);

            //initialize the db if it is not type of ems or rtx
            if (cardType == KnLicenseInfoReader.otherCard) {
                //Initializing DB manager.
               // KnFetchDBDetails.getInstance().initWGP(KnEMSConst.wgpClientID, loaderConfigData.getRmiPort());
                KnDBManager.getDBManagerInstance().initDBManager(KnEMSConst.wgpClientID);
                knLogger.info( "load",
                        "*** DB Manager Initialized. ***");
            }
            
            //check whether license is initialized or not if not initialize it.
            if (KnEMSConst.getLicenseInfo() == null) {
                if (licReader.parserLicense(loaderConfigData.getCardType(), loaderConfigData.getDbFilePath(), loaderConfigData.getLicParserPath()) == false) {
                    knLogger.fatal( "load",
                            "*** License is not valid. Stopping the application  ***");
                    System.exit(-1);
                } else {
                    KnEMSConst.setLicenseInfo(licReader.getLicenseInfo());
                    knLogger.info( "load", "*** License Info:"
                            + KnEMSConst.getLicenseInfo().toString() + ". ***");
                    knLogger.info( "load",
                            "*** License is valid. ***");
                }
            }

           /* knLogger.info( "load", "Initializing Interface Monitor API.");
            KnInterfaceMonitor.initialize(loaderConfigData.getMonitorFilePath());
            knLogger.info( "load", "Interface Monitor API initialized.");*/

            // Loading the config data for auth module in CLG part. Will load properties file and convert it into Map.
            try {
                FileInputStream inputStream = new FileInputStream(loaderConfigData.getConfigPath());
                Properties configProps = new Properties();

                knLogger.info( "load", "Loading the CLG config properties file located at : " +
                        loaderConfigData.getConfigPath());
                configProps.load(inputStream);
                knLogger.info( "load", "Loaded CLG config properties : " + configProps);

                // TODO commented as this is not required for PoC... to be removed later
                /*knLogger.debug( "load", "Initializing Profile Manager Utility.");
                int refreshInterval = Integer.parseInt((String) configProps.get(KnConstants.REFRESH_INTERVAL));
                KnProfileInfoUtil.getInstance(KnConstants.PROFILE_MANAGER_CACHE_SIZE, refreshInterval);
                knLogger.debug( "load", "Initialization of Profile Manager Utility completed. Refresh time ->" +
                        refreshInterval);

                Map identityMgrConfigMap = new HashMap();

                identityMgrConfigMap.put(KnConstants.SESSION_TIMEOUT_DURATION, configProps.get(KnConstants.SESSION_TIMEOUT_DURATION));
                identityMgrConfigMap.put(KnConstants.FORCED_TIMEOUT_DURATION, configProps.get(KnConstants.FORCED_TIMEOUT_DURATION));
                identityMgrConfigMap.put(KnConstants.SESSION_TIMEOUT_DELAY, configProps.get(KnConstants.SESSION_TIMEOUT_DELAY));
                identityMgrConfigMap.put(KnConstants.FORCED_TIMEOUT_DELAY, configProps.get(KnConstants.FORCED_TIMEOUT_DELAY));
                //if (configProps.get(KnConstants.AUTH_REQUIRED) != null) {
                //    identityMgrConfigMap.put(KnConstants.AUTH_REQUIRED, configProps.get(KnConstants.AUTH_REQUIRED));
                //}
                knLogger.debug( "load", "Initializing identity handler FW with : " + identityMgrConfigMap);
                KnIdentityHandlerFW.init(identityMgrConfigMap);
                knLogger.debug( "load", "Initialization of identity handler FW completed.");*/
            } catch (FileNotFoundException fnfe) {
                knLogger.debug( "load", "Could not find CLC AppConfig file! Identity Manager not initialized!");
            }

        } catch (Exception e) {
            knLogger.fatal( "load", "Exception occured during initialization :"
                    + e.getMessage());

            //Stopping the Jboss server
            System.exit(-1);
        }
        return null;
    }

    /**
     * Returns the instance of KnInitializerConfigData
     *
     * @return loaderConfigData
     */
    public IModuleLoaderConfigData getConfigData() {
        return this.loaderConfigData;
    }
}
