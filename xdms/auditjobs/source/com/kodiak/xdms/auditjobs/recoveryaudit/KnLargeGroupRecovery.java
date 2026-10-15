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
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private Map<Integer, Integer> buildGroupTypeThresholdMap(KnPersisterTxn persisterTxn) {
        String methodName = "buildGroupTypeThresholdMap(KnPersisterTxn)";
        try {
            return KnCorpCommonInfoUtil.getGroupTypeThresholdMap(pttServerId, persisterTxn);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "Failed to load group-type thresholds from DG.XDMS_SVC_CONFIG - ", e);
            return Collections.emptyMap();
        }
    }

    private void startLargeGroupRecoverAudit() throws KnPersistenceException {
        String methodName = "startLargeGroupRecoverAudit";
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection connection;

        String downgradeSelect = "SELECT FIRST 100 mc.CORPGROUPID, mc.MEMBERCOUNT "
                + "FROM DG.CORPGROUPMEMBERCOUNT mc "
                + "INNER JOIN DG.CORPGROUPINFO info ON mc.CORPGROUPID = info.CORPGROUPID "
                + "WHERE info.IS_LARGEGROUP = '1' AND info.GROUPTYPE = ? AND mc.MEMBERCOUNT < ?";

        String upgradeSelect = "SELECT FIRST 100 mc.CORPGROUPID, mc.MEMBERCOUNT "
                + "FROM DG.CORPGROUPMEMBERCOUNT mc "
                + "INNER JOIN DG.CORPGROUPINFO info ON mc.CORPGROUPID = info.CORPGROUPID "
                + "WHERE info.IS_LARGEGROUP = '0' AND info.GROUPTYPE = ? AND mc.MEMBERCOUNT > ?";

        String updateToZero = "UPDATE DG.CORPGROUPINFO SET IS_LARGEGROUP='0' WHERE CORPGROUPID=?";
        String updateToOne  = "UPDATE DG.CORPGROUPINFO SET IS_LARGEGROUP='1' WHERE CORPGROUPID=?";


        persisterTxn.open();
        try {
            connection = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
        } catch (KnConnectionException e) {
            throw new RuntimeException(e);
        }

        Map<Integer, Integer> groupTypeThresholdMap = buildGroupTypeThresholdMap(persisterTxn);

        try (PreparedStatement downgradeStmt    = connection.prepareStatement(downgradeSelect);
             PreparedStatement upgradeStmt      = connection.prepareStatement(upgradeSelect);
             PreparedStatement updateStmtToZero = connection.prepareStatement(updateToZero);
             PreparedStatement updateStmtToOne  = connection.prepareStatement(updateToOne)) {

            for (Map.Entry<Integer, Integer> entry : groupTypeThresholdMap.entrySet()) {
                int groupType = entry.getKey();
                int threshold = entry.getValue();

                knLogger.info(methodName, "Auditing GROUPTYPE=", groupType, " threshold=", threshold);

                downgradeStmt.setInt(1, groupType);
                downgradeStmt.setInt(2, threshold);
                try (ResultSet rs = downgradeStmt.executeQuery()) {
                    while (rs.next()) {
                        int corpGroupId = rs.getInt("CORPGROUPID");
                        int memberCount = rs.getInt("MEMBERCOUNT");
                        knLogger.info(methodName, "Downgrade candidate GROUPTYPE=", groupType,
                                " CORPGROUPID=", corpGroupId, " MEMBERCOUNT=", memberCount);

                        updateStmtToZero.setInt(1, corpGroupId);
                        updateStmtToZero.executeUpdate();
                        knLogger.info(methodName, "Updated IS_LARGEGROUP to 0 for CORPGROUPID: ", corpGroupId);
                    }
                }

                upgradeStmt.setInt(1, groupType);
                upgradeStmt.setInt(2, threshold);
                try (ResultSet rs = upgradeStmt.executeQuery()) {
                    while (rs.next()) {
                        int corpGroupId = rs.getInt("CORPGROUPID");
                        int memberCount = rs.getInt("MEMBERCOUNT");
                        knLogger.info(methodName, "Upgrade candidate GROUPTYPE=", groupType,
                                " CORPGROUPID=", corpGroupId, " MEMBERCOUNT=", memberCount);

                        updateStmtToOne.setInt(1, corpGroupId);
                        updateStmtToOne.executeUpdate();
                        knLogger.info(methodName, "Updated IS_LARGEGROUP to 1 for CORPGROUPID: ", corpGroupId);
                    }
                }
            }

            persisterTxn.save();

        } catch (SQLException | KnPersistenceException e) {
            knLogger.error(methodName, "Error executing queries or updates: ", e);
        }
    }
}
