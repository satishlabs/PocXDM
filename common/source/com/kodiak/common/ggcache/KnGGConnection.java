/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache;

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import static com.kodiak.common.ggcache.KnGGCacheConstants.DRIVER_NAME;
import static com.kodiak.common.ggcache.KnGGCacheConstants.GG_JDBC_URL;
import static com.kodiak.common.ggcache.KnGGCacheConstants.Is_gg_auth_enabled;
import static com.kodiak.common.ggcache.KnGGCacheConstants.gg_authId;
import static com.kodiak.common.ggcache.KnGGCacheConstants.gg_authPwd;
import static com.kodiak.common.ggcache.KnGGCacheConstants.gg_ssl_enabled;
import static com.kodiak.common.ggcache.KnGGCacheConstants.ggFqdn;
import static com.kodiak.common.ggcache.KnGGCacheConstants.CLIENT_JKS_PATH;
import static com.kodiak.common.ggcache.KnGGCacheConstants.TRUST_JKS_PATH;
import static com.kodiak.common.ggcache.KnGGCacheConstants.MERGED_GG_TRUST_JKS_PATH;
import static com.kodiak.common.ggcache.KnGGCacheConstants.CLIENT_TRUST_PWD;
import static com.kodiak.common.ggcache.KnGGCacheConstants.GG_CONN_TESTER_CLASS_NAME;
import static com.kodiak.common.ggcache.KnTruststoreMerger.mergeTruststore;
import static com.kodiak.common.resources.KnConstants.XDMMANAGEDOBJECT_CLASSTYPE;

/**
 * *****************************************************************************
 * File name:   KnGGConnection
 * Subsystem:   Utility
 * Description: GG connection pool
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

public class KnGGConnection {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGGConnection.class);

    private static DataSource dataSource;

    static {
        dataSource = setupDataSource();
    }

    public static Connection getDBConnection() throws SQLException {
     try {
         knLogger.info(" num busy conn:" + ((ComboPooledDataSource) dataSource).getNumBusyConnections() + " num idle conn:" + ((ComboPooledDataSource) dataSource).getNumIdleConnections());
         return dataSource.getConnection();
     }catch(SQLException e){
         knLogger.error(" num busy conn:" + ((ComboPooledDataSource) dataSource).getNumBusyConnections() + " num idle conn:" + ((ComboPooledDataSource) dataSource).getNumIdleConnections());
         knLogger.error("Exception while getting connection to", e);
         knLogger.error("Exception while getting connection to", e.getMessage());
         KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_GET_CONNECTION_FROM_GG, KnAlarmConstants.SEVERITY_CRITICAL,
                 XDMMANAGEDOBJECT_CLASSTYPE, "KnGGConnection");
         throw new SQLException("Server is busy. Please try later.");
     }
    }

    private static DataSource setupDataSource() {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(DRIVER_NAME);
            knLogger.info("DRIVER_NAME", DRIVER_NAME);

        } catch (PropertyVetoException e) {
            knLogger.error("Exception", e);
        }
        String decriptedPassword = null;
        try {
            decriptedPassword = gg_authPwd;
        }catch (Exception e){
            knLogger.error("Decryption failed gg_authPwd:- ", decriptedPassword);
        }
        if(Is_gg_auth_enabled.equals(KnGGCacheConstants.ENABLED)){
            knLogger.info("GridGain Authentication is Enabled");
            cpds.setUser(gg_authId);
            cpds.setPassword(decriptedPassword);
        }else{
            knLogger.info("GridGain Authentication is Disabled");
        }

        if(gg_ssl_enabled.equals(KnGGCacheConstants.ENABLED)){
            knLogger.info("GridGain TLS Authentication Enabled");
            generateGGTrust();
            String GG_SSL_JDBC_URL_PWD=getSSLJdbcUrl(decriptedPassword,gg_authId);
            knLogger.info("URL", GG_SSL_JDBC_URL_PWD);
            cpds.setJdbcUrl(GG_SSL_JDBC_URL_PWD);
        }else{
            knLogger.info("GridGain TLS Authentication Disabled");
            knLogger.info("URL", GG_JDBC_URL);
            cpds.setJdbcUrl(GG_JDBC_URL);
        }
        try {
            cpds.setConnectionTesterClassName(GG_CONN_TESTER_CLASS_NAME);
        } catch (PropertyVetoException e) {
            knLogger.error("Exception", e);
        }

        cpds.setMinPoolSize(5);
        cpds.setInitialPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(25);
        cpds.setMaxStatements(0);
        cpds.setMaxIdleTime(120);
        cpds.setMaxIdleTimeExcessConnections(3600);
        cpds.setIdleConnectionTestPeriod(0);
        cpds.setPropertyCycle(900);
        cpds.setTestConnectionOnCheckout(false);
        cpds.setAutoCommitOnClose(false);
        cpds.setForceIgnoreUnresolvedTransactions(false);
        cpds.setNumHelperThreads(6);
        cpds.setFactoryClassLocation(null);
       // cpds.setCheckoutTimeout(12000);
   //     cpds.setAcquireRetryAttempts(15);
    //    cpds.setAcquireRetryDelay(10000);
      // cpds.setBreakAfterAcquireFailure(true);
        return cpds;

    }

    private static void generateGGTrust() {
        var methodName = "generateGGTrust()";
        try {
            knLogger.info(methodName, "Merging truststore for GG client");
            mergeTruststore(TRUST_JKS_PATH, CLIENT_TRUST_PWD,MERGED_GG_TRUST_JKS_PATH, CLIENT_TRUST_PWD);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to merge truststore files:", e);
        }
    }

    private static String getSSLJdbcUrl(String decryptedPassword, String ggAuthId) {
        return "jdbc:ignite:thin://" +
                ggFqdn +
                "?sslMode=require&sslClientCertificateKeyStoreUrl=" + CLIENT_JKS_PATH + "&sslClientCertificateKeyStorePassword=" +
                CLIENT_TRUST_PWD +
                "&sslTrustCertificateKeyStoreUrl=" + MERGED_GG_TRUST_JKS_PATH + "&sslTrustCertificateKeyStorePassword=" +
                CLIENT_TRUST_PWD +
                "&user=" +
                ggAuthId +
                "&password=" +
                decryptedPassword;
    }

    public static void resetDdataSource() {
        final String methodName = "resetDdataSource()";
        try {
            knLogger.info(methodName, "Entry");
            ((ComboPooledDataSource) dataSource).softResetAllUsers();
            knLogger.info(methodName, "Exit");
        } catch (SQLException e) {
            knLogger.error(methodName, "e.getMessage() - " + e.getMessage(), " e.getCause() - " + e.getCause(),
                    "  e.getErrorCode() - " + e.getErrorCode(), "e.getSQLState() - " + e.getSQLState());
            knLogger.error(methodName, "Unexpected Exception while resetting datasource connection pool", e);
        }
    }
}