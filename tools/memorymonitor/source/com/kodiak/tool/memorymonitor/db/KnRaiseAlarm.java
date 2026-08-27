/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.tool.memorymonitor.db;

import com.kodiak.tool.memorymonitor.KnMemoryMonitor;
import com.kodiak.tool.memorymonitor.util.KnDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

public class KnRaiseAlarm {


    private static final KnRaiseAlarm instance = new KnRaiseAlarm();
    private static String SIGNALING_CARD_ID_QUERY = new StringBuilder(200)
            .append("select SIGNALINGCARDID from DG.SIGNALINGCARDINFO where IPADDRESS=? and PTTSERVERID=?")
            .toString();
    private static final String COLON = ":";
    public static final int OUT_OF_MEMORY = 6134;
    private static final String IP = "IP";
    private static final String TOOL = "MEM_TOOL";
    private static String CHECK_ALARM_EXIST = new StringBuilder(200)
            .append("select count(ALARMCODE) from dg.alarm where alarmCode=? and SEVERITYLEVEL=? and MANAGEDOBJECTINSTANCE=?").toString();
    private static final String INSERT_ALARM = "insert into DG.ALARM values (?,?,?,?,?,?,?)";

    public enum ALARM_SEVERITY {
        CRITICAL(0), MAJOR(1), MINOR(2), WARNING(3), CLEAR(4);
        int value;

        private ALARM_SEVERITY(int value) {
            this.value = value;
        }
    }

    private KnRaiseAlarm() {
    }

    public static KnRaiseAlarm getInstance() {
        return instance;
    }

    public static Integer MANAGEDOBJECT_INSTANCE_ID = null;

    public static synchronized boolean generateAlarmUtil(int alarmCode, ALARM_SEVERITY severityLevel) {
        final String methodName = "generateAlarmUtil(int, int)";
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean flag = false;
        try {
            connection = KnDBUtil.getConnection();
            if (!checkIfAlarmExists(alarmCode, severityLevel.value)) {
                KnMemoryMonitor.logger.log(Level.WARNING, methodName + ", Alarm [" + alarmCode + "]  Alarm does not exists ... ");
                String moInfo = new StringBuilder(50).append(TOOL).append(COLON).append(IP).append(COLON).append(KnDBUtil.getLocalIPAddress()).append(COLON).toString();
                //RMManager:IP:172.27.9.22:
                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                pstmt = connection.prepareStatement(INSERT_ALARM);
                pstmt.setInt(1, alarmCode);
                pstmt.setInt(2, severityLevel.value);
                pstmt.setInt(3, getInstance().getMoInstId());
                pstmt.setTimestamp(4, timeStamp);
                pstmt.setInt(5, 5);
                pstmt.setString(6, KnDBUtil.getPttserVerId());
                pstmt.setString(7, moInfo);
                // since the alarm does not exist trying to insert in the
                // database

                KnMemoryMonitor.logger.log(Level.INFO, methodName + ",QUERY executed when alarm does not exists " + INSERT_ALARM);
                int i = pstmt.executeUpdate();
                KnMemoryMonitor.logger.log(Level.INFO, methodName + ",Query No of  record Executed Successfully " + i);
                flag = true;
            } else {
                KnMemoryMonitor.logger.log(Level.INFO, methodName + ",Not updating record  as Alarm already exists... ");
            }
        } catch (Exception e) {
            KnMemoryMonitor.logger.log(Level.SEVERE, methodName + ", Exception : occurred - ",
                    e.getMessage());
            e.printStackTrace();
        } finally {
            KnDBUtil.closeResultSet(rs);
            KnDBUtil.closeStatement(pstmt);
            KnDBUtil.closeConnection(connection);
        }
        System.out.println("returning flag" + flag);
        return flag;
    }

    private static boolean checkIfAlarmExists(int alarmCode, int severityLevel) throws Exception {
        final String methodName = "checkIfAlarmExists()";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        boolean flag = false;
        try {
            connection = KnDBUtil.getConnection();
            ps = connection.prepareStatement(CHECK_ALARM_EXIST);
            ps.setInt(1, alarmCode);
            ps.setInt(2, severityLevel);
            ps.setInt(3, KnRaiseAlarm.getInstance().getMoInstId());
            rs = ps.executeQuery();
            if (rs.next()) {
                int value = rs.getInt(1);
                if (1 == value) {
                    KnMemoryMonitor.logger.log(Level.INFO, methodName + ", Alarm Already exists " + value);
                    flag = true;
                }
            }
        } catch (Exception e) {
            KnMemoryMonitor.logger.log(Level.SEVERE, methodName + ", Exception : occurred - ",
                    e.getMessage());

        } finally {
            KnDBUtil.closeResultSet(rs);
            KnDBUtil.closeStatement(ps);
            KnDBUtil.closeConnection(connection);
        }
        return flag;
    }

    private int getMoInstId() {

        if (MANAGEDOBJECT_INSTANCE_ID == null) {
            MANAGEDOBJECT_INSTANCE_ID = getMOInstanceId();
        }

        return MANAGEDOBJECT_INSTANCE_ID;
    }

    private Integer getMOInstanceId() {
        final String methodName = "getMOInstanceId()";
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        Connection conn = null;
        Integer moId = null;
        try {
            conn = KnDBUtil.getConnection();
            pStmt = conn.prepareStatement(SIGNALING_CARD_ID_QUERY);
            pStmt.setString(1, KnDBUtil.getLocalIPAddress());
            pStmt.setString(2, KnDBUtil.getPttserVerId());
            KnMemoryMonitor.logger.log(Level.INFO, methodName + "QUERY: Executing - "
                    + SIGNALING_CARD_ID_QUERY);

            resultSet = pStmt.executeQuery();
            KnMemoryMonitor.logger.log(Level.INFO, methodName + "QUERY: Executed - ");

            while (resultSet.next()) {
                moId = resultSet.getInt(1);
            }
            KnMemoryMonitor.logger.log(Level.INFO, methodName + "moId:- " + moId);
        } catch (Exception ex) {
            KnMemoryMonitor.logger.log(Level.SEVERE, methodName + ", Exception : occurredt - ",
                    ex.getMessage());

        } finally {
            KnDBUtil.closeResultSet(resultSet);
            KnDBUtil.closeStatement(pStmt);
            KnDBUtil.closeConnection(conn);
        }
        return moId;
    }

}