/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache;

import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.logger.KnLogger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static com.kodiak.common.resources.KnConstants.*;

/**
 * *****************************************************************************
 * File name:   KnGGCacheConstants
 * Subsystem:   Utility
 * Description: GG constants
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           08/06/18        9.0
 * <p/>
 * <p/>
 * Copyright (c) 2018  Kodiak , A Motorola Solutions Company
 * 9th floor, MFar, Manayata Tech Park,
 * Greenheart Phase IV, Nagawara,
 * Bengaluru, Karnataka 560045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak,A Motorola Solutions Company
 * You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak,A Motorola Solutions Company.
 * *******************************************************************************
 */

public class KnGGCacheConstants {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGGCacheConstants.class);

    public static final String DG_SCHEMA = "DG.";
    /*public static final String DRIVER_NAME = "org.apache.ignite.IgniteJdbcDriver";
     public static String GG_CONFIG_FILE_NAME = ACTIVE_RELEASE_DIR + File.separator + "ignite-xdms.xml";
     public static String GG_JDBC_URL = "jdbc:ignite:cfg://cache=CORP_SERVICES_MAP:transactionsAllowed=true@file:///"+GG_CONFIG_FILE_NAME;*/
    public static final String COMMONCONFIGFILE = "CommonConfig.properties";
    public static final String GG_FQDN = "GG_FQDN";
    public static String IS_GRIDGAIN_AUTH_ENABLED = "IS_GRIDGAIN_AUTH_ENABLED";
    public static String GRIDGAIN_AUTHID = "U";
    public static String GRIDGAIN_AUTHPWD ="P";
    public static String CLIENT_TRUST_PWD = "msikodiak";

    public static final String CLIENT_JKS_PATH = "/DG/activeRelease/dat/certs/client.jks";
    public static final String TRUST_JKS_PATH = "/DG/activeRelease/dat/certs/truststore.jks";
    public static final String MERGED_GG_TRUST_JKS_PATH = "/DG/activeRelease/dat/certs/merged-truststore.jks";

    public static String IS_GG_SSL_ENABLED ="IS_GG_SSL_ENABLED";

    //public static String commonConfigFile = ACTIVE_RELEASE_DIR + File.separator + COMMONCONFIGFILE;
    public static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;
    public static Properties commonConfigProps = new Properties();
    public static String ggFqdn = null;
    public static String Is_gg_auth_enabled = null;
    public static String gg_authId = null;
    public static String gg_authPwd = null;
    public static String gg_ssl_enabled = null;

    public static String ENABLED="1";
    public static Integer ONLINE=1;

    static {
        FileInputStream fileInputStream = null;
        try {
            knLogger.debug("commonConfigFile - ", commonConfigFile);
            fileInputStream = new FileInputStream(commonConfigFile);
            commonConfigProps.load(fileInputStream);
            ggFqdn = commonConfigProps.getProperty(GG_FQDN);
            knLogger.debug("ggFqdn - ", ggFqdn);
            Is_gg_auth_enabled = commonConfigProps.getProperty(IS_GRIDGAIN_AUTH_ENABLED);
            gg_authId = KnCommonVaultUtil.getKeyFromVault(GRIDGAIN_AUTHID_NAME_PATH, GRIDGAIN_AUTHID);
            gg_authPwd = KnCommonVaultUtil.getKeyFromVault(GRIDGAIN_AUTHPWD_NAME_PATH, GRIDGAIN_AUTHPWD);
            gg_ssl_enabled = commonConfigProps.getProperty(IS_GG_SSL_ENABLED);

        } catch (Exception e) {
            knLogger.debug("Exception Occured - ", e);
        }
    }

    public static final String DRIVER_NAME = "org.apache.ignite.IgniteJdbcThinDriver";
    public static final String GG_CONN_TESTER_CLASS_NAME = "com.kodiak.common.ggcache.KnGGConnectionTester";
    public static final String GG_JDBC_URL = "jdbc:ignite:thin://" + ggFqdn; // "127.0.0.1: Need to replace property file"

    public static enum GG_CACHE_NAME {

        CORPSERVICESMAPCACHE("CORP_SERVICES_MAP"),
        SERVICEGROUPINFOCACHE("SERVICE_GROUP_INFO"),
        UNUPGRADEDPOCSERVERLISTCACHE("UNUPGRADED_POCSERVER_LIST"),
        OIDC_CLIENT_ID_SECRET("OIDC_CLIENT_ID_SECRET"),
        SUBSCRIBERADDLINFOCACHE("SUBSCRIBERADDLINFO"),
        KOD_UID_CONFIG("KOD_UID_CONFIG"),
        MCS_CLIENT_INFO("MCS_CLIENT_INFO"),
        ASYNC_JOB_NOTIFY("ASYNC_JOB_NOTIFY"),
        ASYNC_JOB_TASK("ASYNC_JOB_TASK"),
        CAT_ASYNC_TXN_INFO("CAT_ASYNC_TXN_INFO"),
        SIPREGISTRATIONSTATUS("SIPREGISTRATIONSTATUS.SIPREGISTRATIONSTATUS"),
		PUB_CONTACT_ADDONALIASIDS("PUBCONTACTADDONALIASIDS"),
        CORP_SHARING_TRUST_MATRIX("CORP_SHARING_TRUST_MATRIX"),
        NEW_SHARED_TRUST_MATRIX_HIERARCHY("SHARED_TRUST_MATRIX_HIERARCHY"),
        PRE_ACTIVATED_CLIENTINFO("PRE_ACTIVATED_CLIENTINFO"),
        USER_PROFILE_INFO("USER_PROFILE_INFO"),
        FEATUREBIT_ROLLBACK_INFO("FEATUREBIT_ROLLBACK_INFO"),
        CORPGRP_LMREXTN("CORPGRP_LMREXTN"),
        SUBSCREMERGSTATEINFO("SUBSCREMERGSTATEINFO"),

        PENDING_TXN_INFO("PENDING_TXN_INFO");

        private String cacheName;

        GG_CACHE_NAME(String eventType) {
            this.cacheName = eventType;
        }

        public String value() {
            return cacheName;
        }
    }

    enum GG_ERROR_CODES {
        ERR_CODE_08006("08006");
        private String ggErrroCode;
        GG_ERROR_CODES(String ggErrroCode) {
            this.setGgErrroCode(ggErrroCode);
        }
        public void setGgErrroCode(String ggErrroCode) {
            this.ggErrroCode = ggErrroCode;
        }
        public String getGgErrroCode() {
            return ggErrroCode;
        }
    }
}
