/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.xdms.auditjobs.recoveryaudit;

import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KnLargeGroupRecovery implements Runnable, IStatusMgrNotifyIntf {

    private static final KnLogger knLogger = KnLogger.getLogger(KnLargeGroupRecovery.class);

    public static KnLargeGroupRecovery instance;

    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;


    private static String pttServerId;

    private KnLargeGroupRecovery() {
        pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
    }

    public static KnLargeGroupRecovery getInstance() {
        if (instance == null) {
            instance = new KnLargeGroupRecovery();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.info("getInstance()", "registered for Status..");
        }
        return instance;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState, "; Current State - ", currentState + "]");
        this.currentRedState = currentState;

        knLogger.debug(methodName, "Current State ", currentState);
    }

    public KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        String methodName = "getCurrentRedundancyStatus";
        knLogger.debug(methodName, "Current Card Redundancy Status ", this.currentRedState);
        return this.currentRedState;
    }

    @Override
    public void run() {
        String methodName = "run()";
        try {
            if (getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
                startLargeGroupRecoverAudit();
            }
        } catch (Throwable e) {
            knLogger.error(methodName, "Throwable : ", e);
        }
        knLogger.info("isLargeGroupRecovery ");
    }

    private void startLargeGroupRecoverAudit() throws KnPersistenceException {
        String methodName = "printLargeGroupQueryResults";
        KnLogger knLogger = KnLogger.getLogger(KnLargeGroupRecovery.class);
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection connection;
        String query1 = "SELECT CORPGROUPID, MEMBERCOUNT FROM DG.CORPGROUPMEMBERCOUNT " +
                "WHERE CORPGROUPID IN (SELECT CORPGROUPID FROM DG.CORPGROUPINFO WHERE IS_LARGEGROUP='1') " +
                "AND MEMBERCOUNT <= 250";

        String query2 = "SELECT CORPGROUPID, MEMBERCOUNT FROM DG.CORPGROUPMEMBERCOUNT " +
                "WHERE CORPGROUPID IN (SELECT CORPGROUPID FROM DG.CORPGROUPINFO WHERE IS_LARGEGROUP='0') " +
                "AND MEMBERCOUNT > 250";

        String updateToZero = "UPDATE DG.CORPGROUPINFO SET IS_LARGEGROUP='0' WHERE CORPGROUPID=?";
        String updateToOne = "UPDATE DG.CORPGROUPINFO SET IS_LARGEGROUP='1' WHERE CORPGROUPID=?";

        persisterTxn.open();
        try {
            connection = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
        } catch (KnConnectionException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement stmt1 = connection.prepareStatement(query1);
             PreparedStatement stmt2 = connection.prepareStatement(query2);
             PreparedStatement updateStmtToZero = connection.prepareStatement(updateToZero);
             PreparedStatement updateStmtToOne = connection.prepareStatement(updateToOne)) {

            knLogger.info(methodName, "Executing Query 1: " + query1);
            try (ResultSet rs1 = stmt1.executeQuery()) {
                while (rs1.next()) {
                    String corpGroupId = rs1.getString("CORPGROUPID");
                    knLogger.info(methodName, "Query 1 Result - CORPGROUPID: " + corpGroupId +
                            ", MEMBERCOUNT: " + rs1.getInt("MEMBERCOUNT"));

                    // Update IS_LARGEGROUP to 0
                    updateStmtToZero.setString(1, corpGroupId);
                    updateStmtToZero.executeUpdate();
                    knLogger.info(methodName, "Updated IS_LARGEGROUP to 0 for CORPGROUPID: " + corpGroupId);
                }
            }

            knLogger.info(methodName, "Executing Query 2: " + query2);
            try (ResultSet rs2 = stmt2.executeQuery()) {
                while (rs2.next()) {
                    String corpGroupId = rs2.getString("CORPGROUPID");
                    knLogger.info(methodName, "Query 2 Result - CORPGROUPID: " + corpGroupId +
                            ", MEMBERCOUNT: " + rs2.getInt("MEMBERCOUNT"));

                    // Update IS_LARGEGROUP to 1
                    updateStmtToOne.setString(1, corpGroupId);
                    updateStmtToOne.executeUpdate();
                    knLogger.info(methodName, "Updated IS_LARGEGROUP to 1 for CORPGROUPID: " + corpGroupId);
                }
            }
            persisterTxn.save();

        } catch (SQLException | KnPersistenceException e) {
            knLogger.error(methodName, "Error executing queries or updates: ", e);
        }
    }
}
