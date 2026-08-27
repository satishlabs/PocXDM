/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.tool.memorymonitor.util;

import com.kodiak.tool.memorymonitor.KnMemoryMonitor;

import java.sql.*;
import java.util.logging.Level;


public class KnDBUtil {


    protected static String driverName = "com.timesten.jdbc.TimesTenDriver";
    private static final String PTT_SERVER_ID = "PTTSERVERID";
    private static final String LOCAL_IP_ADDRESS = "LOCAL_IP_ADDRESS";
    private static String pttServerId = null;
    private static String localIPAdd = null;
    private static final String directConnectionUrl = "jdbc:timesten:direct:DSN=DG_";
    private static String dbUrl = null;

    static {
        try {
            Class.forName(driverName);
            pttServerId = System.getenv(PTT_SERVER_ID);
            KnMemoryMonitor.logger.log(Level.SEVERE, "KnDBUtil, pttServerId  " + pttServerId);
            localIPAdd = System.getenv(LOCAL_IP_ADDRESS);
            KnMemoryMonitor.logger.log(Level.SEVERE, "KnDBUtil, localIPAdd  " + localIPAdd);
            dbUrl = new StringBuilder(50).append(directConnectionUrl).append(pttServerId).append(";").toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            KnMemoryMonitor.logger.log(Level.SEVERE, "KnDBUtil, TimesTenDriver class not found exception  ", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, KnTTDBProps.getInstance().getUserID(), KnTTDBProps.getInstance().getPassword());
        } catch (Exception e) {
            KnMemoryMonitor.logger.log(Level.INFO, "KnDBUtil, getConnection, error while logging dbUrl - ", e.getMessage());
        }
        return null;
   }

    public static String getPttserVerId() {
        return pttServerId;
    }

    public static String getLocalIPAddress() {
        return localIPAdd;
    }


    /**
     * Close a connection object.
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            KnMemoryMonitor.logger.log(Level.WARNING, "closeConnection, error while closing conn object - ", e.getMessage()
            );
        }
    }

    /**
     * Close a statement object.
     *
     * @param stmt
     */
    public static void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            KnMemoryMonitor.logger.log(Level.WARNING, "closeStatement, error while closing statement object - ", e.getMessage()
            );
        }
    }

    /**
     * Close a result set object.
     *
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            KnMemoryMonitor.logger.log(Level.WARNING, "closeResultSet, error while closing resultset object - ", e.getMessage()
            );
        }
    }


}
