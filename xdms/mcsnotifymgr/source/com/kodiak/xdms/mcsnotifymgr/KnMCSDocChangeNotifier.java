/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mcsnotifymgr;

import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.mcsnotifymgr.resources.KnMCSNotifyConstants;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.time.Instant;
import java.util.*;

public class KnMCSDocChangeNotifier {

    /**
     * Optional hook registered by notificationmgr at XDM startup for optimized debulk tracker upserts.
     */
    public interface IMdnTrackerRegistrar {
        void registerWatcherMdnsIfOptimized(Collection<String> watcherMdns);
    }

    private static final KnLogger knLogger = KnLogger.getLogger(KnMCSDocChangeNotifier.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";
    private static KnMCSDocChangeNotifier mcsDocChangeNotifierObj = null;
    private static volatile IMdnTrackerRegistrar trackerRegistrar = null;
    private KnGenInfoUtil genInfoUtil = null;
    private KnGeneralUtil generalUtil = null;
    private static KnGeneralCacheUtil generalCacheUtil = null;
    private String homeRtxId = null;
    private KnJobSchedulerImpl scheduler;
    //The value indicates the max notifications thresold per Job
    private int maxNotfnsPerJob = -1;
    private final static String NOTIFICATION_QUE_NAME = "XCAPNOTIFYQ";
    private final static String FEATURE_ID = "FeatureId";

    private final String NOTIFICATION_INSERT_QUERY_WITHOUT_PARAM = "INSERT INTO DG.XCAP_PENDING_NOTIFYQ " +
            "(INSERTION_TIME,DEST_ID,DEST_TYPE,PAYLOAD,NOTIFY_STATUS,PAYLOAD_VERSION)" +
            " VALUES(?,?,?,?,?,?)";

    private final String NOTIFICATION_INSERT_QUERY_WITH_PARAM = "INSERT INTO DG.XCAP_PENDING_NOTIFYQ " +
            "(INSERTION_TIME,DEST_ID,DEST_TYPE,PAYLOAD,NOTIFY_STATUS,PAYLOAD_VERSION,CID,OPS_CODE,PRIORITY,RETRY_COUNT,NOTIFY_TYPE,PROTOCOL,MSG_TYPE)" +
            " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     * constructor
     */
    private KnMCSDocChangeNotifier() {
        final String methodName = "Inside KnMCSDocChangeNotifier constructor";
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalUtil = new KnGeneralUtil();
        generalCacheUtil = new KnGeneralCacheUtil();
        scheduler = KnJobSchedulerImpl.getInstance();
        try {
            homeRtxId = genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (KnBOException e) {
            knLogger.error(methodName, "exception occured in getting local xdm pttserver id ", e);
        }


    }

    /**
     * function to obtain the instance of the KnMCSDocChangeNotifier
     *
     * @return
     */
    public static synchronized KnMCSDocChangeNotifier getInstance() {
        final String methodName = "getInstance()";
        if (mcsDocChangeNotifierObj == null) {
            mcsDocChangeNotifierObj = new KnMCSDocChangeNotifier();
        }
        knLogger.debug(methodName, "Instance obtained");
        return mcsDocChangeNotifierObj;
    }

    /**
     * Registers the optional tracker hook supplied by notificationmgr at XDM startup.
     */
    public static void setTrackerRegistrar(IMdnTrackerRegistrar registrar) {
        trackerRegistrar = registrar;
    }


    public boolean generateMCSNotification(KnMCSNotifyDTO mcsNotifyDTO) {

        /*String methodName = "generateMCSNotification(KnMCSNotifyDTO)";
        List<KnMCSNotifyJob> jobList = new ArrayList<KnMCSNotifyJob>(1);
        KnMCSNotifyJob notificationJob = new KnMCSNotifyJob(mcsNotifyDTO);
        boolean isSuccess = false;

        jobList.add(notificationJob);

        try {
            knLogger.debug(methodName, "Scheduling the Jobs - ", jobList);
            scheduler.addRamJob(jobList, KnMCSNotifyConstants.MCS_GRPNAME);
            isSuccess = true;
        } catch (KnJobSchedulerException e) {
            knLogger.error(methodName, "KnJobSchedulerException occurred while ",
                    "submitting Notification Job to Scheduler - ", e);
        }


        return isSuccess;*/

        return generateMCSNotification(Collections.singletonList(mcsNotifyDTO), null, null);
    }

    public boolean generateMCSNotification(List<KnMCSNotifyDTO> mcsNotifyDTOs, KnNotificationParamDTO notificationParamDTO, KnPersisterTxn persisterTxn) {
        String methodName = "generateMCSNotification(KnMCSNotifyDTO)";
        boolean isSuccess = false;
        knLogger.info(methodName, FLOW_TAG + " STEP-MCS-G0 MCS notification batch received. batchSize="
                + (mcsNotifyDTOs != null ? mcsNotifyDTOs.size() : 0)
                + " priority=" + (notificationParamDTO != null ? notificationParamDTO.getPriority() : null));
        int notificationCount = mcsNotifyDTOs != null && !mcsNotifyDTOs.isEmpty() ? mcsNotifyDTOs.size() : 0;
        boolean ownedTxn = false;
        try {
            if (mcsNotifyDTOs == null || mcsNotifyDTOs.isEmpty()) {
                knLogger.info(methodName, FLOW_TAG + " STEP-MCS-SKIP Empty MCS batch; no queue insert attempted");
                return true;
            }
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            if (notificationParamDTO == null) {
                notificationParamDTO = new KnNotificationParamDTO();
            }
            if (isSaveNotification(notificationParamDTO, notificationCount, persisterTxn)) {
                // Save-first: queue rows must commit independently of caller txn (epic / XCAP-DEBULK-001).
                saveNotification(mcsNotifyDTOs, notificationParamDTO, null);
                knLogger.info(methodName, FLOW_TAG + " STEP-MCS-SAVE Queue persist completed for MCS batch. batchSize="
                        + mcsNotifyDTOs.size() + " priority=" + notificationParamDTO.getPriority());
                registerWatcherMdnsAfterQueueSave(mcsNotifyDTOs);
            } else if (isSaveSuppressNotification(persisterTxn)) {
                knLogger.info(methodName, "Notification get suppressed for mcsxcap notify");
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_MCSXCAP_NOTIFY_SUPPRESSED);
                LinkedHashSet<String> mdns = new LinkedHashSet<>();
                for (KnMCSNotifyDTO mcsNotifyDTO : mcsNotifyDTOs) {
                    for (KnDocumentChangeDTO knDocumentChangeDTO : mcsNotifyDTO.getDocumentChange()) {
                        mdns.add(knDocumentChangeDTO.getMdn());
                    }
                }
                sendMdnsToDB(mdns, notificationParamDTO, persisterTxn);
            } else {
                knLogger.info(methodName, "No Records are getting saved in DG.XCAP_PENDING_NOTIFYQ");
            }
            isSuccess = true;
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "KnPersistenceException occurred while ", "submitting Notification Job to Scheduler - ", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while ", "submitting Notification Job to Scheduler - ", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
        }
        return isSuccess;
    }

    public void sendMdnsToDB(LinkedHashSet<String> mdns, KnNotificationParamDTO notificationParamDTO, KnPersisterTxn persisterTxn) {
        String methodName = "sendMdnsToDB(LinkedHashSet<String>,KnNotificationParamDTO )";
        knLogger.info(methodName, "sending mdns to save in db ");
        List<KnMCSNotifyDTO> mcsNotifyDTOS = new ArrayList<>();
        var mdnListArray = new ArrayList<>(mdns);
        var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, KnConstants.BATCH_SIZE);
        for (var mdnBatchList : mdnSplitList) {
            KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
            mcsNotifyDTO.setSuppressMdn(new HashSet<>(mdnBatchList));
            mcsNotifyDTOS.add(mcsNotifyDTO);
        }
        saveEtagMdnNotification(mcsNotifyDTOS, notificationParamDTO, persisterTxn);
    }

    private void registerWatcherMdnsAfterQueueSave(List<KnMCSNotifyDTO> mcsNotifyDTOs) {
        if (trackerRegistrar == null) {
            return;
        }
        LinkedHashSet<String> watcherMdns = extractWatcherMdnsFromMcsNotify(mcsNotifyDTOs);
        if (!watcherMdns.isEmpty()) {
            trackerRegistrar.registerWatcherMdnsIfOptimized(watcherMdns);
        }
    }

    private LinkedHashSet<String> extractWatcherMdnsFromMcsNotify(List<KnMCSNotifyDTO> mcsNotifyDTOs) {
        LinkedHashSet<String> watcherMdns = new LinkedHashSet<>();
        if (mcsNotifyDTOs == null) {
            return watcherMdns;
        }
        for (KnMCSNotifyDTO mcsNotifyDTO : mcsNotifyDTOs) {
            if (mcsNotifyDTO == null || mcsNotifyDTO.getDocumentChange() == null) {
                continue;
            }
            for (KnDocumentChangeDTO docChange : mcsNotifyDTO.getDocumentChange()) {
                if (docChange == null) {
                    continue;
                }
                if (docChange.getDocType() == KnMCSNotifyConstants.DOCTYPE.MDN.value()) {
                    String mdn = docChange.getMdn();
                    if (mdn != null && !mdn.trim().isEmpty()) {
                        watcherMdns.add(mdn.trim());
                    }
                }
            }
        }
        return watcherMdns;
    }

    private boolean isSaveNotification(KnNotificationParamDTO notificationParamDTO, int notificationCount, KnPersisterTxn persisterTxn) {
        String methodName = "isSaveNotification";
        boolean isSaveNotification = notificationParamDTO.getPriority() == 0 ||
                (!genInfoUtil.isSuppressWaterMark(notificationCount, recordCount(persisterTxn))
                        & notificationCount < genInfoUtil.getXdmMaxNotificationCount());
        knLogger.info(methodName, "isSaveNotification value : ", isSaveNotification);
        return isSaveNotification;
    }

    private boolean isSaveSuppressNotification(KnPersisterTxn persisterTxn) {
        String methodName = "isSaveSuppressNotification";
        return totalRecordCount(persisterTxn) < genInfoUtil.getMaxPendingNotifySize() * 10;
    }

    private void saveNotification(List<KnMCSNotifyDTO> mcsNotifyDTOs, KnNotificationParamDTO notificationParamDTO, KnPersisterTxn persisterTxn) {
        String methodName = "saveNotificationToDB(KnMCSNotifyDTO)";
        knLogger.entry(methodName);
        Connection connection = null;
        int count = 0;
        int batchSize = 100;
        int MAX_RETRY = 2;
        boolean ownedTxn = false;
        PreparedStatement pStmt = null;
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                if (persisterTxn == null) {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    ownedTxn = true;
                }
                String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                connection = persisterTxn.getDBConnection(localPttId, false);
                if (notificationParamDTO != null) {
                    pStmt = connection.prepareStatement(NOTIFICATION_INSERT_QUERY_WITH_PARAM);
                } else {
                    pStmt = connection.prepareStatement(NOTIFICATION_INSERT_QUERY_WITHOUT_PARAM);
                }
                for (KnMCSNotifyDTO mcsNotifyDTO : mcsNotifyDTOs) {
                    byte[] data = KnGeneralUtil.toByteArray(mcsNotifyDTO);
                    knLogger.debug(methodName, "data length", data.length);
                    if (data.length > 10000) {
                        knLogger.info(methodName, "Data length is more than 10000 bytes, so dropping the notification");
                        continue;
                    }

                    Instant instant = Instant.now();
                    long timeInNanoSecond = instant.getNano() + instant.getEpochSecond() * 1000000000L;
                    pStmt.setLong(1, timeInNanoSecond);

                    if (null == mcsNotifyDTO.getDocumentChange() || mcsNotifyDTO.getDocumentChange().isEmpty()) {
                        knLogger.error(methodName, "DocumentChange is empty");
                        continue;
                    }
                    KnDocumentChangeDTO docChange = mcsNotifyDTO.getDocumentChange().get(0);
                    int docType = docChange.getDocType();

                    if (docType == KnMCSNotifyConstants.DOCTYPE.MDN.value()) {
                        pStmt.setString(2, docChange.getMdn());
                        pStmt.setInt(3, KnMCSNotifyConstants.DESTTYPE.MDN.value());
                    } else if (docType == KnMCSNotifyConstants.DOCTYPE.GROUP.value()) {
                        if (!docChange.getDocChangeList().isEmpty() && docChange.getDocChangeList().get(0).getDocUri() != null) {
                            pStmt.setString(2, getGroupFromURI(docChange.getDocChangeList().get(0).getDocUri()));
                        } else {
                            pStmt.setString(2, "UNKNOWN");
                        }
                        pStmt.setInt(3, KnMCSNotifyConstants.DESTTYPE.GROUP.value());
                    }
                    pStmt.setBytes(4, data);
                    pStmt.setInt(5, KnMCSNotifyConstants.NOTIFYSTATUS.PENDING.value());
                    pStmt.setInt(6, KnMCSNotifyConstants.PAYLOADVERSION.ONE.value());

                    if (notificationParamDTO != null) {
                        pStmt.setString(7, notificationParamDTO.getCid());
                        pStmt.setInt(8, notificationParamDTO.getOpsCode());
                        pStmt.setInt(9, notificationParamDTO.getPriority());
                        pStmt.setInt(10, notificationParamDTO.getRetryCount());
                        pStmt.setInt(11, notificationParamDTO.getNotifyType());
                        pStmt.setInt(12, notificationParamDTO.getProtocol());
                        pStmt.setInt(13, notificationParamDTO.getMsgType());
                    }
                    pStmt.addBatch();
                    count++;
                    if (count % batchSize == 0) {
                        knLogger.debug(methodName, "Executing batch of 100");
                        pStmt.executeBatch();
                    }
                }
                pStmt.executeBatch();
                if (ownedTxn) {
                    persisterTxn.save();
                }
                knLogger.info(methodName, FLOW_TAG + " STEP-MCS-SN3 MCS queue insert complete. insertedCount="
                        + count + " priority=" + (notificationParamDTO != null ? notificationParamDTO.getPriority() : null));
                knLogger.exit(methodName);
                break;
            } catch (KnPersistenceException e) {
                if (ownedTxn) {
                    KnDbUtil.rollback(persisterTxn);
                }
                knLogger.error(methodName, FLOW_TAG + " STEP-MCS-ERR MCS queue insert failed (persister)", e);
                knLogger.error(methodName, "Persister Txn occurred - ", e);
                if (attempt < MAX_RETRY) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        knLogger.error(methodName, "InterruptedException ", ex.getMessage());
                    }
                } else {
                    knLogger.debug(methodName, "Max retry completed");
                }
            } catch (Exception e) {
                if (ownedTxn) {
                    KnDbUtil.rollback(persisterTxn);
                }
                knLogger.error(methodName, FLOW_TAG + " STEP-MCS-ERR MCS queue insert failed (unexpected)", e);
                knLogger.error(methodName, "Unexpected Exception occurred - ", e);
                break;
            } finally {
                KnDbUtil.closeStatement(pStmt);
            }
        }
    }

    void saveEtagMdnNotification(List<KnMCSNotifyDTO> mcsNotifyDTOs, KnNotificationParamDTO notificationParamDTO, KnPersisterTxn persisterTxn) {
        String methodName = "saveEtagMdnNotification(List<KnMCSNotifyDTO>, KnNotificationParamDTO)";
        knLogger.entry(methodName, "save the list of mdn as payload into db starts- ");
        PreparedStatement pStmt = null;
        int count = 0;
        boolean ownedTxn = false;
        Connection connection = null;
        int MAX_RETRY = 2;
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {

                if (persisterTxn == null) {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    ownedTxn = true;
                }
                String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                connection = persisterTxn.getDBConnection(localPttId, false);
                if (notificationParamDTO != null) {
                    pStmt = connection.prepareStatement(NOTIFICATION_INSERT_QUERY_WITH_PARAM);
                } else {
                    pStmt = connection.prepareStatement(NOTIFICATION_INSERT_QUERY_WITHOUT_PARAM);
                }

                long startTime = System.currentTimeMillis();
                for (KnMCSNotifyDTO mcsNotifyDTO : mcsNotifyDTOs) {
                    Instant instant = Instant.now();
                    long timeInNanoSecond = instant.getNano() + instant.getEpochSecond() * 1000000000L;
                    pStmt.setLong(1, timeInNanoSecond);
                    pStmt.setString(2, "000000000");
                    pStmt.setInt(3, KnMCSNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
                    byte[] data = KnGeneralUtil.toByteArray(mcsNotifyDTO);
                    pStmt.setBytes(4, data);
                    pStmt.setInt(5, KnMCSNotifyConstants.NOTIFYSTATUS.PENDING.value());
                    pStmt.setInt(6, KnMCSNotifyConstants.PAYLOADVERSION.ONE.value());
                    if (notificationParamDTO != null) {
                        pStmt.setString(7, notificationParamDTO.getCid());
                        pStmt.setInt(8, notificationParamDTO.getOpsCode());
                        pStmt.setInt(9, notificationParamDTO.getPriority());
                        pStmt.setInt(10, notificationParamDTO.getRetryCount());
                        pStmt.setInt(11, notificationParamDTO.getNotifyType());
                        pStmt.setInt(12, notificationParamDTO.getProtocol());
                        pStmt.setInt(13, notificationParamDTO.getMsgType());
                    }
                    pStmt.addBatch();
                    count++;
                    if (count % KnConstants.BATCH_SIZE == 0) {
                        pStmt.executeBatch();
                    }
                }
                pStmt.executeBatch();
                if (ownedTxn) {
                    persisterTxn.save();
                }
                long endTime = System.currentTimeMillis();
                knLogger.debug(methodName, "time testing notification : ", endTime - startTime);
                break;
            } catch (KnPersistenceException e) {
                if (ownedTxn) {
                    KnDbUtil.rollback(persisterTxn);
                }
                knLogger.error(methodName, "Persister Txn occurred - ", e);
                if (attempt < MAX_RETRY) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        knLogger.error(methodName, "InterruptedException ", ex.getMessage());
                    }
                } else {
                    knLogger.debug(methodName, "Max retry completed");
                }
            } catch (Exception e) {
                if (ownedTxn) {
                    KnDbUtil.rollback(persisterTxn);
                }
                knLogger.error(methodName, FLOW_TAG + " STEP-MCS-ERR MCS queue insert failed (unexpected)", e);
                knLogger.error(methodName, "Unexpected Exception occurred - ", e);
                break;
            } finally {
                KnDbUtil.closeStatement(pStmt);
            }
        }
        knLogger.exit(methodName, "save the list of mdn as payload into db ends- ");
    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction." + e);
        }
    }

    public static String getGroupFromURI(String xcapDirURI) {
        String methodName = "getGroupFromURI(String)";
        knLogger.info(methodName, "Entry", KnGDPRTemplate.mdnUriTemplate(xcapDirURI));
        String corpGroupId = null;
        try {
            corpGroupId = xcapDirURI.split("sip:")[1].split("@")[0];
        } catch (Exception e) {
            knLogger.error(methodName, e);
        }
        knLogger.info(methodName, "Exit : corpGroupId", corpGroupId);
        return corpGroupId;
    }

    public int recordCount(KnPersisterTxn persisterTxn) {
        String methodName = "recordCount()";
        String selectQry = null;
        Connection connection = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        int count = 0;
        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, true);
            //selectQry = "SELECT COUNT(1) FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_TYPE != " + KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value();
            selectQry = "SELECT COUNT(1) FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_TYPE IN (" + KnMCSNotifyConstants.DOCTYPE.MDN.value() + "," + KnMCSNotifyConstants.DOCTYPE.GROUP.value() + ")";
            pStmt = connection.prepareStatement(selectQry);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ", selectQry);
            knLogger.debug(methodName, "result - ", rs);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info(methodName, "record count", count);
        } catch (KnPersistenceException e) {
            if (ownedTxn) {
                KnDbUtil.rollback(persisterTxn);
            }
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            if (ownedTxn) {
                KnDbUtil.rollback(persisterTxn);
            }
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return count;
    }

    public int totalRecordCount(KnPersisterTxn persisterTxn) {
        String methodName = "totalRecordCount()";
        String selectQry = null;
        Connection connection = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        int count = 0;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, true);
            selectQry = "SELECT COUNT(1) FROM DG.XCAP_PENDING_NOTIFYQ";
            pStmt = connection.prepareStatement(selectQry);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ", selectQry);
            knLogger.debug(methodName, "result - ", rs);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info(methodName, "record count", count);
        } catch (KnPersistenceException e) {
            if (ownedTxn) {
                KnDbUtil.rollback(persisterTxn);
            }
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            if (ownedTxn) {
                KnDbUtil.rollback(persisterTxn);
            }
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return count;
    }
}

