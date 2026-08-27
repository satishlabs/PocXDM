/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.xdms.mcsnotifymgr.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KnSendMCSNotify implements Runnable {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSendMCSNotify.class);

    private KnMCSNotifyDTO notifyDto;
    private static int RETRY_COUNT = 2;
    private static String MCS_NOTIFY = "XCAPSNNotifyQueue1";
    private KnRmqMessagePublisher messagingFwr;
    private String defaultJobName = "MCSNOTIFYJOB";
    private int TTLINMS = 30000;

    private KnNotificationKeyDTO seqId;

    public KnSendMCSNotify(KnNotificationKeyDTO seqId, KnMCSNotifyDTO notifyDto) {
        this.seqId = seqId;
        this.notifyDto = notifyDto;
        messagingFwr = KnRmqMessagePublisher.getInstance();
    }

    @Override
    public void run() {
        String methodName = "executeTask()";
        boolean status = false;
        knLogger.info(methodName, "Entry-->");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            knLogger.debug(methodName, "opening the transaction ");
            String routingKey = KnRmqConfig.getInstance().getMcsNotificationBindingKey();
            String json = objectMapper.writeValueAsString(notifyDto);
            knLogger.info(methodName, "Publishing json - ", json, ", routingKey - ", routingKey);
            KnMessage request = new KnMessage();
            request.setDestRoutingKey(routingKey);
            request.setSync(Boolean.FALSE);
            request.setPayLoad(json);
            request.setDestQueueName(MCS_NOTIFY);
            request.setMessageTTL(TTLINMS);
            status = sendMessage(request);
            if (!status) {
                knLogger.debug(methodName, "Message Publish Failed - retrying");
                for (int i = 0; i < RETRY_COUNT; i++) {
                    status = sendMessage(request);
                    if (status)
                        break;
                }
                knLogger.error(methodName, "Message Publish Failed - after retry also");
            }
            //cleanUpRecord(seqId);
            if (status) {
                cleanUpRecord(seqId);
            } else {
                knLogger.warn(methodName, "Cleanup skipped because publish failed. CID=" + seqId.getCid());
            }
            knLogger.info(methodName, "CID", seqId.getCid(), "Message Published");
            if (status) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_MCSXCAP_NOTIFY_PUBLISHED);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred ", e);
        }

    }

    private boolean sendMessage(KnMessage request) {
        final String methodName = "sendMessage()";
        boolean status = false;
        try {
            messagingFwr.sendMessage(request);
            status = true;
        } catch (KnMessageException e) {
            knLogger.error(methodName, "KnMessageException occurred while sending message - ", e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while sending message - ", e);
        }

        return status;
    }

    private void cleanUpRecord(KnNotificationKeyDTO seqId) throws KnPersistenceException {
        String methodName = "process()";
        knLogger.debug(methodName);
        KnPersisterTxn persisterTxn = null;
        String query = null;
        Connection connection = null;
        PreparedStatement pStmt = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, false);
            query = "DELETE FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_ID=? AND INSERTION_TIME = ? AND  DEST_TYPE=?";
            pStmt = connection.prepareStatement(query);
            pStmt.setString(1, seqId.getDestId());
            pStmt.setLong(2, seqId.getInsertionTime());
            pStmt.setInt(3, seqId.getDestType());
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, " record cleaned ", count);
            persisterTxn.save();
            knLogger.debug(methodName);
        } catch (KnPersistenceException e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    knLogger.error(methodName, "SQL Exception occurred in closing connection - ", e);
                }
            }
        }
    }
}
