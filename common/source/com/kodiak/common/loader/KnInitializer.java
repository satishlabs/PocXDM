/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnInitializer.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Rama Krishna         15-Jan-2010   7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************/
package com.kodiak.common.loader;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.ems.base.itf.KnEMSConst;
import com.kodiak.ems.base.utils.KnLicenseInfoReader;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnTTDBProps;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * This is the initialization class that initializes resources which needs to
 * come up the service
 */
public class KnInitializer {
    private static final KnLogger knLogger = KnLogger.getLogger(KnInitializer.class);

    public static final String EMSIPADDRESS = "IPADDRESS";
    public static final String EMSIPADDRESS2 = "IPADDRESS2";
    public static final String EMSIPADDRESS3 = "IPADDRESS3";
    public static final String EMSDBUSERID = "DBUSERID";
    public static final String EMSDBPASSWORD = "DBPASSWORD";
    public static final String EMSDBDSN = "DBDSN";
    private static KnInitializer instance = new KnInitializer();

    protected KnInitializer() {
        init();
    }

    /**
     * initialization
     *
     * @return
     */
    public static KnInitializer getInstance() {
        return instance;
    }

    /**
     * Initializes the  start-up resources. This method will be invoked by all the services before
     * invoking actual operation.
     * <p/>
     * Initialization need to be done in the below order -
     * <p/>
     * 1. Log4j Initialization
     * 2. License Initialization
     * 3. DB Manager Initialization
     */
    private void init() {
        String methodName = "init()";
        /** Initialize Log Manager **/
        //get the values required for initializing logger
        //initialize the log manager                    
        System.out.println("Log Manager Initializing...");
        KnLogger.init();
        knLogger.info(methodName, "*  Log Manager Initialized. *");


        KnEMSConst.setActiveReleasePath(System.getProperty("activeRelDir"));

        //Initialising DB Manager
        KnDBManager.getDBManagerInstance().initDBManager(KnEMSConst.wgpClientID);
        knLogger.info(methodName, "*** DB Manager Initialized. ***");
        //initialize the license parser
        KnLicenseInfoReader licReader = KnLicenseInfoReader.getInstance();
        if (licReader.parserLicense(KnLicenseInfoReader.otherCard, KnGeneralUtil.getLocalDbMgrProps(), null)) {
            KnEMSConst.setLicenseInfo(licReader.getLicenseInfo());
            //license valid and loaded properly
            knLogger.info(methodName, "*** License Info:", KnEMSConst.getLicenseInfo().toString(), ". ***");
        } else {
            //license or some parameter provided is invalid
            knLogger.fatal(methodName, "*** License is not valid. Stopping the application  ***");
            System.exit(-1);
        }
    }

    /**
     * This method is used to connect to local db to identify the primary, secondary and
     * geo EMS node information
     * <p/>
     * It will uses local db connection to establish connection to local db
     *
     * @return returns properties which contains primary, secondary and geo ems ip-addresses.
     */
    private Properties getEMSProps() {
        String methodName = "getEMSProps()";
        knLogger.debug(methodName, "Entry :");
        Connection conn = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        String activeReleasePath = System.getProperty("activeRelDir");
        Properties emsProps = new Properties();
        try {
            FileInputStream ipStream = new FileInputStream(activeReleasePath + File.separator + "dbmgr.props");
            Properties props = new Properties();
            props.load(ipStream);
            ipStream.close();
            String tcpPort = props.getProperty("TCP_PORT");
            Class.forName("com.timesten.jdbc.TimesTenDriver");
            knLogger.debug(methodName, "TimesTen driver loaded successfully");
            String lDbUrl = KnDbUtil.getDBConfigInfo().getConnectionURL();

            knLogger.debug(methodName, "DB URL - " + lDbUrl);
            conn = DriverManager.getConnection(lDbUrl, KnTTDBProps.getInstance().getUserID(), KnTTDBProps.getInstance().getPassword());
            String sqlQuery = "SELECT IPADDRESS, DBDATASOURCENAME, DBUID, DBPWD, ISACTIVE FROM DG.EMSINFO";
            knLogger.debug(methodName, "Query - ", sqlQuery);
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(sqlQuery);
            while (resultSet.next()) {
                String emsIP = resultSet.getString(1);
                emsProps.setProperty(EMSDBDSN, resultSet.getString(2));
                emsProps.setProperty(EMSDBUSERID, resultSet.getString(3));
                emsProps.setProperty(EMSDBPASSWORD, resultSet.getString(4));
                String isActive = resultSet.getString(5);
                String primary = emsProps.getProperty(EMSIPADDRESS);
                String secondary = emsProps.getProperty(EMSIPADDRESS2);
                String geo = emsProps.getProperty(EMSIPADDRESS3);
                if (primary == null && ("Y").equals(isActive)) {
                    emsProps.setProperty(EMSIPADDRESS, emsIP);
                } else if (secondary == null) {
                    emsProps.setProperty(EMSIPADDRESS2, emsIP);
                } else if (geo == null) {
                    emsProps.setProperty(EMSIPADDRESS3, emsIP);
                } else if (primary == null) {
                    emsProps.setProperty(EMSIPADDRESS, emsIP);
                }
            }
            emsProps.setProperty("TCP_PORT", tcpPort);
            knLogger.debug(methodName, "EMS Properties : ", emsProps);
        } catch (Exception ex) {
            knLogger.debug(methodName, "Exception :", ex.getMessage());
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(stmt);
            KnDbUtil.closeConnection(conn);
        }
        return emsProps;
    }
}
