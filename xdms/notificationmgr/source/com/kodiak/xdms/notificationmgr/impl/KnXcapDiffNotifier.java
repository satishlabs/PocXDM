/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXcapDiffNotifier.java
 * Subsystem:  PoC XDMS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Harsha             06-Jan-2011       7.0
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
 * ************************************************************************
 */
package com.kodiak.xdms.notificationmgr.impl;

import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.OIDCXCAP_ALERTTYPE_FOR_IOS16;
import static com.kodiak.xdms.server.common.resources.KnConstants.ALERT_TYPE_HIGH_PRIORITY;
import static com.kodiak.xdms.server.common.resources.KnConstants.ALERT_TYPE_LOW_PRIORITY;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Objects;
import java.util.Calendar;
import java.util.stream.Collectors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.jaxb.beans.KnJAXBParser;
import com.kodiak.utilities.jaxb.beans.KnJAXBProcessException;
import com.kodiak.utilities.jaxb.beans.corp.xcapDiff.*;
import com.kodiak.utilities.tlvgenerator.generator.KnTLVDocDiffGenerator;
import com.kodiak.utilities.tlvgenerator.generator.dto.KnDeleteDeviceTlvDTO;
import com.kodiak.utilities.tlvgenerator.generator.dto.KnPoCSubsDeRegisterDTO;
import com.kodiak.utilities.tlvgenerator.generator.dto.KnPoCXcapDiffNotifyDTO;
import com.kodiak.utilities.tlvgenerator.generator.dto.KnRequestObject;
import com.kodiak.utilities.tlvgenerator.generator.dto.KnSubsProfileDTO;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnNotifyPayloadDTO;
import com.kodiak.xdms.notificationmgr.beans.KnNtfnApns;
import com.kodiak.xdms.notificationmgr.beans.KnNtfnHdr;
import com.kodiak.xdms.notificationmgr.beans.KnNtfnHdrDest;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnIOSWakeup;
import com.kodiak.xdms.notificationmgr.beans.KnIOSXdmWakeUpNtfnPayload;
import com.kodiak.xdms.notificationmgr.beans.KnKpnsNotification;
import com.kodiak.xdms.notificationmgr.beans.KnMcsxcapMdnDTO;

import com.kodiak.xdms.notificationmgr.resources.KnXcapNotifyConstants;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dto.clientdat.KnDeleteDeviceDTO;
import com.kodiak.xdms.server.common.dto.common.KnEmergencyAttributesDTO;
import com.kodiak.xdms.server.common.dto.common.KnEmergencyInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.dto.common.KnTargetPermsInfoDTO;
import com.kodiak.xdms.server.common.resources.KnCacheKeys;
import com.kodiak.xdms.server.common.resources.KnConstants;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class KnXcapDiffNotifier {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapDiffNotifier.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";
    private static KnXcapDiffNotifier xcapDiffNotifierObj = null;
    private static KnGenInfoUtil genInfoUtil = null;
    private KnGeneralUtil generalUtil = null;
    private static KnGeneralCacheUtil generalCacheUtil = null;
    private String homeRtxId = null;
    private final static String NOTIFICATION_QUE_NAME="XCAPNOTIFYQ";
    private final static String FEATURE_ID="FeatureId";
    private KnMCSXCAPNotifier knMCSXCAPNotifier;
    private final static String FROM="from";
    private final static String TO="to";
    private final static String REQUESTID="reqID";
    private final static String CTYPE="cType";
    private final static String MESSAGETYPE="messageType";
    private final static String SENDORCANCEL="sendOrCancel";
    private final static String DRGFLAG="drFlag";
    private final static String PROFILE_PREF="profile-preference";
    private final static String TTL="ttl";

    public static final String IOS_NM_HYBRID_HEADER_VER = "ver";

    public static final String IOS_NM_HYBRID_HEADER_NOTIFICATION_TYPE = "notificationType";

    public static final String IOS_NM_HYBRID_HEADER_FALLBACK_NOTIFICATION_TYPE = "fallback_notification_type";

    public static final String IOS_NM_APNS_EXPIRATION = "apns-expiration";

    public static final String RETRY_INTERVAL = "retryInterval";
    public static final Integer RETRY_INTERVAL_VALUE = 43200;

    private final static int RETRY_COUNT = 2;

    private final String NOTIFICATION_INSERT_QUERY = "INSERT INTO DG.XCAP_PENDING_NOTIFYQ " +
            "(INSERTION_TIME,DEST_ID,DEST_TYPE,PAYLOAD,NOTIFY_STATUS,PAYLOAD_VERSION,CID,OPS_CODE,PRIORITY,RETRY_COUNT,NOTIFY_TYPE,PROTOCOL,MSG_TYPE)" +
            " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String INSERT_SUPPRESSED_NOTIFY = "INSERT INTO DG.XCAP_PENDING_NOTIFYQ " +
            "(INSERTION_TIME,DEST_ID,DEST_TYPE,PAYLOAD,NOTIFY_STATUS,PAYLOAD_VERSION,CID,OPS_CODE,PRIORITY,RETRY_COUNT,NOTIFY_TYPE,PROTOCOL,MSG_TYPE,PARAM1)" +
            " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String SELECT_SUPPRESSED_NOTIFY = "SELECT FIRST 100 INSERTION_TIME, DEST_ID, DEST_TYPE, PAYLOAD, MSG_TYPE ,PARAM1, CID FROM DG.XCAP_PENDING_NOTIFYQ " +
            "WHERE NOTIFY_STATUS = ? AND DEST_TYPE = ? ORDER BY INSERTION_TIME ";

    // ═══════════════════════════════════════════════════════════════════════════════════
    // SQL constants – MDN_NOTIFY_TRACKER (Temporal Workflow epoch gating)
    //
    // Table layout:
    //   MDN              VARCHAR(32) PK  – watcher subscriber identifier
    //   LAST_NOTIFIED_TIME BIGINT NOT NULL – epoch-start millis; used for eligibility check
    //
    // Logic:
    //   INSERT-IF-ABSENT  → preserves original epoch start across rapid repeated changes
    //   UPDATE timestamp  → advances LAST_NOTIFIED_TIME after a watcher batch is claimed
    //   DELETE stale rows → housekeeping after notification period + safety margin
    // ═══════════════════════════════════════════════════════════════════════════════════

    /**
     * INSERT-IF-ABSENT into MDN_NOTIFY_TRACKER.
     *
     * Uses plain INSERT; duplicate PK is treated as success (epoch preserved).
     * Avoids FROM DUAL dialect issues across TimesTen/Oracle variants.
     */
    private final String INSERT_MDN_NOTIFY_TRACKER =
            "INSERT INTO DG.MDN_NOTIFY_TRACKER (MDN, LAST_NOTIFIED_TIME) VALUES (?, ?)";

    /**
     * Advances LAST_NOTIFIED_TIME after a watcher batch has been claimed for processing.
     * This opens a fresh epoch window for subsequent notifications that arrive after the
     * current batch is sent.
     */
    private final String UPDATE_MDN_NOTIFY_TRACKER_TS =
            "UPDATE DG.MDN_NOTIFY_TRACKER " +
            "SET LAST_NOTIFIED_TIME = ? " +
            "WHERE MDN = ?";

    /**
     * Removes tracker rows whose epoch has aged beyond the configured notification period.
     * Applied after a batch is claimed so that MDNs with no further pending work are not
     * unnecessarily re-evaluated on the next poll cycle.
     *
     * Condition: elapsed time since LAST_NOTIFIED_TIME >= XCAP_NOTIFICATION_PERIOD
     */
    private final String DELETE_MDN_NOTIFY_TRACKER_STALE =
            "DELETE FROM DG.MDN_NOTIFY_TRACKER t " +
            "WHERE (? - t.LAST_NOTIFIED_TIME) >= ? " +
            "AND NOT EXISTS (" +
            "  SELECT 1 FROM DG.XCAP_PENDING_NOTIFYQ q " +
            "  WHERE q.DEST_ID = t.MDN " +
            "    AND q.NOTIFY_STATUS IN (?, ?)" +
            ")";

    /**
     * constructor
     */
    private KnXcapDiffNotifier() {
        final String methodName = "Inside KnXcapDiffNotifier constructor";
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalUtil = new KnGeneralUtil();
        generalCacheUtil = new KnGeneralCacheUtil();
        knMCSXCAPNotifier = KnMCSXCAPNotifier.getInstance();
        try {
            homeRtxId = genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (KnBOException e) {
            knLogger.error(methodName, "exception occured in getting local xdm pttserver id ", e);
        }
    }

    /**
     * function to obtain the instance of the xcapDiffNotifier
     *
     * @return
     */
    public static synchronized KnXcapDiffNotifier getInstance() {
        final String methodName = "getInstance()";
        if (xcapDiffNotifierObj == null) {
            xcapDiffNotifierObj = new KnXcapDiffNotifier();
        }
        knLogger.debug(methodName, "Instance obtained");
        return xcapDiffNotifierObj;
    }


    /**
     * function to generate the directory notification XML for the given input using JibX library.
     *
     * @param xcapDiffNotifyObj
     * @return
     */
    public boolean generateDirNotification(KnXcapDiffNotifyDTO xcapDiffNotifyObj, Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap, Map<String,Set<String>> baseMdnMap) {
        final String methodName = "generateDirNotification";
        boolean notifyStatus = false;
        XcapDiffType xcapDiffTypeObj = new XcapDiffType();
        if (null != xcapDiffNotifyObj) {
            xcapDiffTypeObj.setXcapRoot(xcapDiffNotifyObj.getXcapRootUri());

            DocumentType docType = new DocumentType();
            docType.setSel(xcapDiffNotifyObj.getDirURI());
            docType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
            docType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
            Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = xcapDiffNotifyObj.getDocDiffObj();
            LinkedHashSet<KnMcsxcapMdnDTO> mdns = new LinkedHashSet<KnMcsxcapMdnDTO>();
            LinkedHashSet<String> iosMdns = new LinkedHashSet<String>();

            // Loop through all the doc change DTO and construct the appropriate docType with add, replace and remove docs
            if (xcapDiffDocObsList != null) {
                ArrayList<DiffAdd> diffAddList = new ArrayList<DiffAdd>();
                ArrayList<DiffReplace> diffReplaceList = new ArrayList<DiffReplace>();
                ArrayList<DiffReplaceChangeValue> diffReplaceChangeList = new ArrayList<DiffReplaceChangeValue>();
                ArrayList<DiffRemove> diffRemoveList = new ArrayList<DiffRemove>();
                for (KnXcapDiffDocDTO xcapDiffDocObj : xcapDiffDocObsList) {
                    boolean isPushNotifyEnabled = xcapDiffDocObj.isPushNotifyEnabled();
                    if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.ADD.value()) {
                        DiffAdd diffAddObj = new DiffAdd();
                        diffAddObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(xcapDiffDocObj.getDocUri());
                        entryTypeObj.setEtag(xcapDiffDocObj.getDocEtag());
                        // Set videoCallPermission
                        Integer videoPermission = xcapDiffDocObj.getVideoPermission();
                        if (videoPermission != null) {
                            entryTypeObj.setVideoCallPermission(String.valueOf(videoPermission));
                            knLogger.debug(methodName, "ADD: Setting video-call-permission - uri:", xcapDiffDocObj.getDocUri(), ", videoPermission:", videoPermission);
                        } else {
                            entryTypeObj.setVideoCallPermission(String.valueOf(KnConstants.VIDEO_PERMISSION_VALUE));
                            knLogger.debug(methodName, "ADD: videoPermission is NULL, setting default - uri:", xcapDiffDocObj.getDocUri(), ", default:", KnConstants.VIDEO_PERMISSION_VALUE);
                        }
                        entryList.add(entryTypeObj);
                        diffAddObj.setEntryType(entryList);
                        diffAddList.add(diffAddObj);


                        populateIosMdns(iosMdns, xcapDiffDocObj.getDocUri(), xcapDiffDocObj.getDocumentSelector(), isPushNotifyEnabled);

                        populateSystemProfileMdnsForDocChange(mdns, xcapDiffDocObj.getDocumentSelector());
                    } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REPLACE.value()) {
                        if (xcapDiffDocObj.getDocEtag() != null) {
                            DiffReplaceChangeValue diffReplaceObj = new DiffReplaceChangeValue();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            diffReplaceObj.setChangeValue(xcapDiffDocObj.getDocEtag());
                            diffReplaceChangeList.add(diffReplaceObj);
                            populateIosMdns(iosMdns, xcapDiffDocObj.getDocUri(), xcapDiffDocObj.getDocumentSelector(), isPushNotifyEnabled);

                            populateSystemProfileMdnsForDocChange(mdns, xcapDiffDocObj.getDocumentSelector());
                        } else {
                            DiffReplace diffReplaceObj = new DiffReplace();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            diffReplaceList.add(diffReplaceObj);
                            populateIosMdns(iosMdns, xcapDiffDocObj.getDocUri(), xcapDiffDocObj.getDocumentSelector(), isPushNotifyEnabled);

                            populateSystemProfileMdnsForDocChange(mdns, xcapDiffDocObj.getDocumentSelector());
                        }
//                ++replaceDocCount;
                    } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()) {
                        DiffRemove diffRemoveObj = new DiffRemove();
                        diffRemoveObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        diffRemoveObj.setWs(xcapDiffDocObj.getRemoveWs());
                        diffRemoveList.add(diffRemoveObj);
                        populateIosMdns(iosMdns, xcapDiffDocObj.getDocUri(), xcapDiffDocObj.getDocumentSelector(), isPushNotifyEnabled);

                        populateSystemProfileMdnsForDocChange(mdns, xcapDiffDocObj.getDocumentSelector());
//                ++removeDocCount;
                    } else {
                        knLogger.error(methodName, "Unknown Doc Change Type");
                        return notifyStatus;
                    }
                }
                if (!diffAddList.isEmpty()) {
                    docType.setDiffAddType(diffAddList);
                }
                if (!diffReplaceList.isEmpty()) {
                    docType.setDiffReplaceType(diffReplaceList);
                }
                if (!diffRemoveList.isEmpty()) {
                    docType.setDiffRemoveType(diffRemoveList);
                }
                if (!diffReplaceChangeList.isEmpty()) {
                    docType.setDiffReplaceChangeType(diffReplaceChangeList);
                }
            }
            xcapDiffTypeObj.setDocumentType(docType);
            if (!mdns.isEmpty()) {
                knLogger.debug(methodName, "sending System profiles Mcs XCAP Notifies for MDNs", mdns);
                knMCSXCAPNotifier.sendMCSXCAPNotification(mdns);
            }

            // generating XML string from the JibX object type
            String notifyXML;
            try {
                notifyXML = KnJAXBParser.convertBeanToXml(xcapDiffTypeObj, XcapDiffType.class);
                knLogger.debug(methodName, "Notification XML is:", notifyXML);

                // send the notification message to Subscription Proxy
                if (notifyXML != null) {
                    byte[] payLoad = generateXcapDiffNotification(xcapDiffNotifyObj, notifyXML);

                    if (!iosMdns.isEmpty()) {
                        for (String mdn : iosMdns) {
                            KnPersisterTxn persisterTxn = null;
                            try {
                                String payLods = Base64.getEncoder().encodeToString(notifyXML.getBytes());

                                persisterTxn = KnPersisterTxn.getPersisterTxn();
                                persisterTxn.open();
                                sendIOStoRMQNotification(null, mdn, payLods, KnXcapNotifyConstants.DOC_CHANGE_EVENT_FEATURE_ID,
                                        xcapDiffNotifyObj.getPresenceHome(), persisterTxn, mdnSubsInfoMap, baseMdnMap );
                                persisterTxn.save();
                            } catch (KnPersistenceException e) {
                                knLogger.error(methodName, "exception", e);

                                rollback(persisterTxn);

                            }

                        }
                    }


                    notifyStatus = sendNotification(KnXcapNotifyConstants.DOC_TYPE.SUBS_CONFIG_DOC.value(), payLoad, KnXcapNotifyConstants.DOC_CHANGE_EVENT_FEATURE_ID, xcapDiffNotifyObj.getPresenceHome());
                }
                // send deRegister notification if flag set
                if (xcapDiffNotifyObj.isDeRegisterNotify()) {
                    byte[] payLoad = generateDeRegisterNotification(xcapDiffNotifyObj);
                    notifyStatus = sendNotification(KnXcapNotifyConstants.DOC_TYPE.SUBS_DEREGISTER_NOTIFY_DOC.value(), payLoad, KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID, xcapDiffNotifyObj.getPocHome());
                }

                if (xcapDiffNotifyObj.isChangeMdnNotify()) {
                    byte[] payLoad = generateDeRegisterNotification(xcapDiffNotifyObj);
                    notifyStatus = sendNotification(KnXcapNotifyConstants.DOC_TYPE.SUBS_CHNAGE_MDN_DOC.value(),payLoad, KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID, xcapDiffNotifyObj.getPocHome());
                }

                // send profile change notification if flag set
                if (xcapDiffNotifyObj.isProfileNotify()) {
                    byte[] payLoad = generateProfileNotification(xcapDiffNotifyObj);
                    notifyStatus = sendNotification(KnXcapNotifyConstants.DOC_TYPE.SUBS_PROFILE_CHANGE_NOTIFY.value(),payLoad, KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID, xcapDiffNotifyObj.getPocHome());

                }
            } catch (KnJAXBProcessException ope) {
                knLogger.error(methodName, "Notification message construction failed:", ope);
                notifyStatus = false;
            }
        }
        return notifyStatus;
    }

    private boolean sendRMQNotification(byte[] payload, int featureId, String pttServerId) {
    	String methodName = "sendRMQNotification(byte[],int, String)";
        knLogger.info(methodName, "ENTRY: Send Notification - ", ", homeRTX ID - [", homeRtxId, "], Feature Id - ", featureId);
    	boolean status=false;
    	try{
        //getting instance for msg fw
            KnRmqMessagePublisher msgFw = KnRmqMessagePublisher.getInstance();
        int localClusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        String pocClusterId  = fetchClusterId(pttServerId,String.valueOf(localClusterId));
        if(pocClusterId  == null){
            knLogger.warn(methodName, "Notification not sending due to clusterId is null");
            return false;
        }
            //creating a message
        KnMessage msgObj = new KnMessage();
        msgObj.setPayLoad(payload);
        //create RMQ header.
        Map<String, Object> msgHeader=new HashMap<>();
        msgHeader.put(FEATURE_ID, String.valueOf(featureId));
        msgObj.setRmqMsgHeader(msgHeader);
        // generate dynamic routing key based on the cluster id and multsite deployment flag
        Map<String, String> microServicesParamNameValueMap = KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(localClusterId);
        String rountigKey = "prod.CBIfaceQueue1_.site" + pocClusterId + "._" + pttServerId + "_V1.kodiakptt.com";
        if (ENABLED_STRING.equals(microServicesParamNameValueMap.get(MUTLISITE_DEPLOYMENT_FLAG)) && !pocClusterId.equals(String.valueOf(localClusterId))) {
                msgObj.setSrcExchangeName(CROSSSITE_TOPIC_EXCHANGE);
        }
        knLogger.debug(methodName," Dynamic Routing Key generated for RMQ Notification is: ",
                rountigKey , "exchange name is: ", msgObj.getSrcExchangeName());
        msgObj.setDestRoutingKey(rountigKey);
        msgObj.setSync(false);
        //Default queue name
        msgObj.setDestQueueName(NOTIFICATION_QUE_NAME);
            status = sendMessage(msgObj);
            if (!status) {
                knLogger.debug(methodName, "Message Publish Failed - retrying");
                for (int i = 0; i < RETRY_COUNT; i++) {
                    status = sendMessage(msgObj);
                    if (status)
                        break;
                }
                knLogger.error(methodName, "Message Publish Failed - after retry also");
            }
    	}
    	catch (Exception e) {
            knLogger.error(methodName, "notification failed:", e);
            status=false;
		}
        /*if (status) {
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_OIDCXCAP_NOTIFY_PUBLISHED);
        }*/
    	return status;

    }

    /**
     * Fetches the cluster ID associated with a given PTT server ID.
     * <p>
     * This method first attempts to retrieve the cluster ID from the cache. If the cache is empty or does not contain
     * the required mapping, it fetches the cluster ID from the database and updates the cache.
     *
     * @param pttServerId    The PTT server ID for which the cluster ID is to be retrieved.
     * @return The cluster ID as an `String` if found, otherwise throws a `RuntimeException`.
     * @throws RuntimeException If a `KnConfigurationException` occurs during the process.
     */
    @SuppressWarnings("unchecked")
    private String fetchClusterId(String pttServerId, String localClusterSiteId) throws KnConfigurationException, KnDAOException {
        final String methodName = "fetchClusterId()";
        KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
        String clusterId;
        KnPersisterTxn knPersisterTxn = KnPersisterTxn.getPersisterTxn();
        try {
            Map<String, String> clusterIdMap;
            ICacheManager cacheManager = configManager.getCacheManager();
            // Attempt to retrieve the cluster ID map from the cache
            clusterIdMap = (Map<String, String>) cacheManager.get(KnCacheKeys.CLUSTER_ID_MAP);
            // If the cache is empty, fetch the cluster ID map from the database and update the cache
            if (clusterIdMap == null || clusterIdMap.isEmpty()) {
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(localClusterSiteId);
                clusterIdMap = xdmServerDAO.getClusterId(knPersisterTxn);
                cacheManager.put(KnCacheKeys.CLUSTER_ID_MAP, clusterIdMap);
            }
            // Retrieve the cluster ID for the given PTT server ID, or use the default from the environment variable
            clusterId = clusterIdMap.get(pttServerId);
            knLogger.debug(methodName, "Cluster ID fetched for PTT Server ID ", pttServerId, " is: ", clusterId);
        } catch (KnConfigurationException e) {
            knLogger.error("fetchClusterId(), Exception in fetching cluster id for pttserverid: " + pttServerId, e);
            throw e;
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Exception in fetching cluster id from database for pttserverid: " + pttServerId, e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw e;
        }
        return clusterId;
    }

    private boolean sendMessage(KnMessage msgObj) {
        final String methodName = "sendMessage()";
        boolean status = false;
        try {
            //getting instance for msg fw
            KnRmqMessagePublisher msgFw = KnRmqMessagePublisher.getInstance();
            msgFw.sendMessage(msgObj);
            status = true;
        } catch (KnMessageException e) {
            knLogger.error(methodName, "KnMessageException occurred while sending message - ", e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while sending message - ", e);
        }

        return status;
    }
	 private boolean sendIOStoRMQNotification(Map<String, KnXDMSubsProvDTO> mdnmap, String mdn,String payLods, int featureId,
                                              String pttServerId, KnPersisterTxn persisterTxn, Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap,
                                              Map<String,Set<String>> baseMdnMap) {
    	String methodName = "sendIOStoRMQNotification(mdn byte[],int, String)";
        knLogger.info(methodName, "ENTRY: Send Notification - ", ", homeRTX ID - [", homeRtxId, "], Feature Id - ", featureId);
    	boolean status=false;
    	try{
        //getting instance for msg fw
            KnRmqMessagePublisher msgFw = KnRmqMessagePublisher.getInstance();
        //creating a message
        KnMessage msgObj = new KnMessage();

        //create RMQ header.
        Map<String, Object> msgHeader=new HashMap<>();
        msgHeader.put(FEATURE_ID, String.valueOf(featureId));
        msgHeader.put(FROM,mdn);
        msgHeader.put(TO,mdn);
        Date d1=new Date();

        msgHeader.put(REQUESTID,d1.getTime());
        msgHeader.put(CTYPE, "APNS");
		msgHeader.put(SENDORCANCEL, 1);
		msgHeader.put(DRGFLAG, false);
		msgHeader.put(PROFILE_PREF, 0);
		msgHeader.put(TTL, 30);
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

            List<String> mdns = new ArrayList<>();
            mdns.add(msgHeader.get("to").toString());
            boolean isHybridIosBitEnabled = false;
            KnNtfnHdrDest hdrDest = new KnNtfnHdrDest();
            if (mdnmap != null && !mdnmap.isEmpty()
                    && null != mdnmap.get(msgHeader.get("to").toString())) {
                KnXDMSubsProvDTO knXDMSubsProvDTO = mdnmap.get(msgHeader.get("to").toString());
                if(null != knXDMSubsProvDTO.getDerivedKey())
                    knXDMSubsProvDTO.setDerivedKey(new String(KnGeneralUtil.hexStringToByteArray(knXDMSubsProvDTO.getDerivedKey())));
                hdrDest.setEncrypt_dk(knXDMSubsProvDTO.getDerivedKey());
                hdrDest.setEncrypt_ha1(knXDMSubsProvDTO.getClientPassword());
                isHybridIosBitEnabled = isHybridIosBitEnabled(knXDMSubsProvDTO.getActiveFS2());
            }else if (mdnSubsInfoMap != null && !mdnSubsInfoMap.isEmpty()
                    && null != mdnSubsInfoMap.get(msgHeader.get("to").toString())) {
                KnXDMSubsProvDTO knXDMSubsProvDTO = mdnSubsInfoMap.get(msgHeader.get("to").toString());
                if(null != knXDMSubsProvDTO.getDerivedKey())
                    knXDMSubsProvDTO.setDerivedKey(new String(KnGeneralUtil.hexStringToByteArray(knXDMSubsProvDTO.getDerivedKey())));
                hdrDest.setEncrypt_dk(knXDMSubsProvDTO.getDerivedKey());
                hdrDest.setEncrypt_ha1(knXDMSubsProvDTO.getClientPassword());
                isHybridIosBitEnabled = isHybridIosBitEnabled(knXDMSubsProvDTO.getActiveFS2());
            } else {
                //here seems always only one mdn will be there as it take from to.
                KnXDMSubsProfileRespDTO subsInfo = genInfoUtil.selectSubsProfileInfo(mdns, null);
                for (KnXDMSubsProvDTO sub : subsInfo.getSubsRespDTO()) {
                    if(null != sub.getDerivedKey())
                        sub.setDerivedKey(new String(KnGeneralUtil.hexStringToByteArray(sub.getDerivedKey())));
                    hdrDest.setEncrypt_dk(sub.getDerivedKey());
                    hdrDest.setEncrypt_ha1(sub.getClientPassword());
                    isHybridIosBitEnabled = isHybridIosBitEnabled(sub.getActiveFS2());
                }
            }
            if (isHybridIosBitEnabled) {
                String hybridIosNotificationType = paramNameValueMapCommon.get(OIDCXCAP_ALERTTYPE_FOR_IOS16);
                msgHeader.put(IOS_NM_HYBRID_HEADER_VER, "3.0");
                msgHeader.put(IOS_NM_HYBRID_HEADER_NOTIFICATION_TYPE, hybridIosNotificationType);
                if (hybridIosNotificationType != null && Integer.valueOf(hybridIosNotificationType) == ALERT_TYPE_HIGH_PRIORITY) {
                    msgHeader.put(IOS_NM_HYBRID_HEADER_FALLBACK_NOTIFICATION_TYPE, ALERT_TYPE_LOW_PRIORITY);
                }
                msgHeader.put(RETRY_INTERVAL, RETRY_INTERVAL_VALUE);
            }
            knLogger.info(methodName, "msgHeader : ", msgHeader.toString());
            String payload1 = constructKpnsMessage(msgHeader, payLods, "wakeup", persisterTxn, paramNameValueMapCommon, isHybridIosBitEnabled, hdrDest, baseMdnMap);

        msgObj.setPayLoad(payload1);

	  //  msgHeader.put("is_cri_client", headers.get(KnUserCheckConstant.IS_CRI_CLIENT));

        msgObj.setRmqMsgHeader(msgHeader);
        //need to check in other place generate routingKey
        String rountigKey=paramNameValueMapCommon.get("NM_ROUTING_KEY");;
        msgObj.setDestRoutingKey(rountigKey);
        msgObj.setSync(false);
        //Default queue name
        msgObj.setDestQueueName("NotifyMgrQueue1");
        knLogger.info(methodName, "Sending message to RMQ - ", msgObj);
            msgFw.sendMessage(msgObj);
        status=true;
    	}
    	catch (Exception e) {
            knLogger.error(methodName, "notification failed:", e);
            status=false;
		}
    	return status;

    }
	public boolean sendNotification(byte[] payload, int featureId, String pttServerId) {
   	 String methodName = "sendNotification(byte[],int, String)";
     knLogger.info(methodName, "ENTRY: Send Notification - ", ", homeRTX ID - [", homeRtxId, "], pttServerId -",pttServerId," Feature Id - ", featureId);
    //Check GG to find unupgraded PoCServer
        boolean status = this.isUnUpgradedPOCSERVER(pttServerId);
      knLogger.info(methodName, "Status - ", status);
		if(status)
		{
		    status= lagecySendNotification(payload, featureId, pttServerId)	;
		}
		else
		{
			status=sendRMQNotification(payload, featureId, pttServerId);
            addPegs(null, status);
		}

 	return status;

 }

    public boolean sendNotification(String docType, byte[] payload, int featureId, String pttServerId) {
        String methodName = "sendNotification(byte[],int, String)";
        knLogger.info(methodName, "ENTRY: Send Notification - ", ", homeRTX ID - [", homeRtxId, "], pttServerId -", pttServerId, " Feature Id - ", featureId);
        //Check GG to find unupgraded PoCServer
        boolean status = this.isUnUpgradedPOCSERVER(pttServerId);
        if (status) {
            status = lagecySendNotification(payload, featureId, pttServerId);
        } else {
            status = sendRMQNotification(payload, featureId, pttServerId);
            addPegs(docType, status);
        }
        return status;
    }
 private boolean lagecySendNotification(byte[] payload, int featureId, String pttServerId) {
     String methodName = "lagecySendNotification(byte[], int, String)";
     KnPersisterTxn persisterTxn = null;
        knLogger.info(methodName, "ENTRY: Send Notification - ", ", homeRTX ID - [", homeRtxId, "], Feature Id - ", featureId);

        StringBuffer payLoadStr = new StringBuffer();
        for (byte load : payload) {
            payLoadStr.append(load);
        }
        knLogger.debug(methodName, "PAYLOAD: ", payLoadStr);
        Connection conn = null;
        PreparedStatement pStmt = null;

        String query = "INSERT INTO DG.TRANSPORTSERVERSOURCEINFO VALUES(?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?)";

        int targetType = 1;    //destination address type
        String targetId = pttServerId + ":" + featureId;    //<destination RTXID>:<AppRouter featureID>
        int targetSubsystemId = KnConstants.SUBSYS_ID_APPROUTER;  //AppRouter subsystem ID
        int sourceSubsystemId = KnConstants.SUBSYS_ID_XDMDATAMGR;  //XDM-DataManager Subsystem ID
        byte[] tlvHeader;
        byte[] tlvBody;
        int seqNumber;
        int subnetId = KnConstants.XDMDATAMGR_INTERFACEID;  //subnetId .
        String nexThopRTX = pttServerId;   //destination RTXID
        int nexThopSubsSysId = KnConstants.SUBSYS_ID_APPROUTER;   //AppRouter subsystem ID
        int msgStatus = KnConstants.MSG_RESOLVED; //resolved

        knLogger.debug(methodName, "featureId:", featureId);
        //, "notbulkMyPayloadbytesToHexString:",
          //      bytesToHexString(payload), "endofpayload");

        boolean tlvInsertStatus = false;
        tlvHeader = Arrays.copyOfRange(payload, 0, KnConstants.HEADER_SIZE);
        tlvBody = Arrays.copyOfRange(payload, KnConstants.HEADER_SIZE, payload.length);
        seqNumber = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_TRANSPORT_SERVER_SOURCE_INFO, homeRtxId,
                KnConstants.TABLE_TRANS_SER_SRC_COL, false, null);


        try {
            //opening the transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "opening the transaction");
            persisterTxn.open();

            conn = persisterTxn.getDBConnection(homeRtxId, false);
            pStmt = conn.prepareStatement(query);

            InputStream isHeader = new ByteArrayInputStream(tlvHeader);
            InputStream isBody = new ByteArrayInputStream(tlvBody);
            pStmt.setInt(1, targetType);
            pStmt.setString(2, targetId);
            pStmt.setInt(3, targetSubsystemId);
            pStmt.setInt(4, sourceSubsystemId);
            pStmt.setBinaryStream(5, isHeader, isHeader.available());
            pStmt.setBinaryStream(6, isBody, isBody.available());
            pStmt.setInt(7, seqNumber);
            pStmt.setInt(8, subnetId);
            pStmt.setString(9, nexThopRTX);
            pStmt.setInt(10, nexThopSubsSysId);
            pStmt.setInt(11, msgStatus);

            knLogger.debug(methodName, "Query: Executing - ", query);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");

            knLogger.debug(methodName, "Saving the transaction ");
            persisterTxn.save();

            tlvInsertStatus = true;
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "KnPersistenceException occurred - ",
                    new KnException(e.getErrorCode(), e.getErrorMessage(), e));

            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred - ",
                    new KnException("", e.getMessage(), e));
            rollback(persisterTxn);
        } finally {
            KnDbUtil.closeStatement(pStmt);
                if(conn !=null) {
                    try {
                        conn.close();

                    } catch (SQLException e) {
                        knLogger.error(methodName, "SQL Exception occurred in closing connection - ", e);
                    }
                }
        }
        return tlvInsertStatus;
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
            knLogger.error("rollback(txn)", "Failed to rollback the transaction."+e);
        }
    }


    public boolean sendXcapDiffNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs,Map<String,KnXDMSubsProvDTO>  mdnSubsInfoMap,
                                             KnPersisterTxn persisterTxn, Map<String,Set<String>> baseMdnMap) {
        String methodName = "sendXcapDiffNotifications";
        boolean notifyStatus = false;
        knLogger.info(methodName, "ENTRY: sendXcapDiffNotifications");
        knLogger.debug(methodName, "sendXcapDiffNotifications - ", xcapDiffNotifyDTOs);
        try {
            //generate payloads for dir chg notifications
            List<KnNotifyPayloadDTO> listOfNotifyPayLoad = xcapDiffDirChgNotifications(xcapDiffNotifyDTOs, mdnSubsInfoMap, persisterTxn);

            int featureId = KnXcapNotifyConstants.DOC_CHANGE_EVENT_FEATURE_ID;

            if (listOfNotifyPayLoad != null && !listOfNotifyPayLoad.isEmpty()) {
                notifyStatus = sendBulkNotifications(listOfNotifyPayLoad, featureId, mdnSubsInfoMap, persisterTxn, baseMdnMap);
            } else {
                knLogger.error(methodName, "Failed to send the notifications. Payload is empty - ",
                        listOfNotifyPayLoad);
            }
        } catch (Throwable e) {
            knLogger.error(methodName, "Unexpected Exception occurred while sending bulk notifications - ",
                    new KnException("", e.getMessage(), e));
        } finally {
            knLogger.info(methodName, "EXIT: send XCAP Diff notifications");
        }
        return notifyStatus;
    }

    private List<KnNotifyPayloadDTO> xcapDiffDirChgNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs, Map<String,KnXDMSubsProvDTO> mdnActiveFsMap, KnPersisterTxn persisterTxn) {
        String methodName = "xcapDiffDirChgNotifications(xcapDiffNotifyDTOs)";
        ArrayList<KnNotifyPayloadDTO> listOfNotifyPayload = new ArrayList<KnNotifyPayloadDTO>();
        knLogger.debug(methodName, "ENTRY: generate xcap diff dir chg notifications");
        Map<String, String> maxAllowedMap = getMaxNotificationDocSize(persisterTxn);
        knLogger.info(methodName, "number of notifications to be sent - ", xcapDiffNotifyDTOs.size(),
                ", maxAllowedMap - ", maxAllowedMap);
        int maxAllowedSize = 800;
        int ALLOWED_SIPMSG_SIZE_ON_TCP = Integer.valueOf(maxAllowedMap.get(KnXcapNotifyConstants.ALLOWED_SIPMSG_SIZE_ON_TCP)) -
                Integer.valueOf(maxAllowedMap.get(KnXcapNotifyConstants.ALLOWED_SIPMSG_HEADERS_SIZE));
        int ALLOWED_SIPMSG_SIZE_ON_UDP = Integer.valueOf(maxAllowedMap.get(KnXcapNotifyConstants.ALLOWED_SIPMSG_SIZE_ON_UDP)) -
                Integer.valueOf(maxAllowedMap.get(KnXcapNotifyConstants.ALLOWED_SIPMSG_HEADERS_SIZE));

        for (KnXcapDiffDirChgNotifyDTO xcapDiffNotifyObj : xcapDiffNotifyDTOs) {
            try {
                XcapDiffType xcapDiffTypeObj = new XcapDiffType();
                xcapDiffTypeObj.setXcapRoot(xcapDiffNotifyObj.getXcapRootUri());
                //Constructing DocumentType
                DocumentType docType = new DocumentType();

                Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = xcapDiffNotifyObj.getDocDiffObj();
                String protocolVersion = xcapDiffNotifyObj.getProtocolVersion();

                //Populating additional DocumentType info
                List<DocumentType> addlDocTypeInfoList = null;
                if (xcapDiffNotifyObj.isNotfnCapability()) {
                    docType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
                    addlDocTypeInfoList = populateNewChangeLog(xcapDiffDocObsList, xcapDiffNotifyObj, protocolVersion);
                    maxAllowedSize = ALLOWED_SIPMSG_SIZE_ON_TCP;
                } else {
                    docType.setSel(xcapDiffNotifyObj.getDirURI());
                    docType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
                    docType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
                    addlDocTypeInfoList = populateChangeLog(xcapDiffDocObsList, protocolVersion);
                    maxAllowedSize = ALLOWED_SIPMSG_SIZE_ON_UDP;
                }
                knLogger.debug(methodName," addlDocTypeInfoList -",addlDocTypeInfoList );
                for (DocumentType addlDocTypeInfo : addlDocTypeInfoList) {
                        /*if ((addlDocTypeInfo.getDiffAddType() != null && !addlDocTypeInfo.getDiffAddType().isEmpty()) ||
                                (addlDocTypeInfo.getDiffReplaceType() != null && !addlDocTypeInfo.getDiffReplaceType().isEmpty()) ||
                                (addlDocTypeInfo.getDiffRemoveType() != null && !addlDocTypeInfo.getDiffRemoveType().isEmpty())) {*/
                    if (addlDocTypeInfo.getSel() != null && !addlDocTypeInfo.getSel().isEmpty()) {
                        docType.setSel(addlDocTypeInfo.getSel());
                    }
                    if (addlDocTypeInfo.getPreviousEtag() != null && !addlDocTypeInfo.getPreviousEtag().isEmpty()) {
                        docType.setPreviousEtag(addlDocTypeInfo.getPreviousEtag());
                    }
                    if (addlDocTypeInfo.getNewEtag() != null && !addlDocTypeInfo.getNewEtag().isEmpty()) {
                        docType.setNewEtag(addlDocTypeInfo.getNewEtag());
                    }

                    if (addlDocTypeInfo.getDiffAddType() != null) {
                        docType.setDiffAddType(addlDocTypeInfo.getDiffAddType());
                    } else {
                        docType.setDiffAddType(null);
                    }
                    if (addlDocTypeInfo.getDiffReplaceType() != null) {
                        docType.setDiffReplaceType(addlDocTypeInfo.getDiffReplaceType());
                    } else {
                        docType.setDiffReplaceType(null);
                    }
                    if (addlDocTypeInfo.getDiffReplaceChangeType() != null) {
                        docType.setDiffReplaceChangeType(addlDocTypeInfo.getDiffReplaceChangeType());
                    } else {
                        docType.setDiffReplaceChangeType(null);
                    }
                    if (addlDocTypeInfo.getDiffRemoveType() != null) {
                        docType.setDiffRemoveType(addlDocTypeInfo.getDiffRemoveType());
                    } else {
                        docType.setDiffRemoveType(null);
                    }
                    if (docType.getDiffAddType() == null && docType.getDiffReplaceType() == null && docType.getDiffRemoveType() == null && docType.getDiffReplaceChangeType() == null) {
                        docType.setPreviousEtag(null);
                        knLogger.debug(methodName, " sending forceSync notification");
                    }
                    xcapDiffTypeObj.setDocumentType(docType);

                    knLogger.debug("Notification object", xcapDiffTypeObj);
                    // generating XML string from the JibX object type
                    String notifyXML;
                    try {
                        notifyXML = KnJAXBParser.convertBeanToXml(xcapDiffTypeObj, XcapDiffType.class);
                        knLogger.debug(methodName, "[VIDEO-PERM] Final XCAP-DIFF Notification XML to be sent - ", notifyXML);
                        //Verifying notification size and truncating if required
                        if (maxAllowedSize > -1) {
                            byte[] xmlBytes = toByteArray(notifyXML);
                            knLogger.debug(methodName, "xmlBytes size - ", xmlBytes.length);
                            if (xmlBytes != null && xmlBytes.length > maxAllowedSize) {
                                knLogger.debug(methodName, "Truncating notification xml");

                                ArrayList<DiffAdd> addList = new ArrayList<DiffAdd>();
                                ArrayList<DiffReplace> replaceList = new ArrayList<DiffReplace>();
                                ArrayList<DiffRemove> removeList = new ArrayList<DiffRemove>();
                                docType.setDiffAddType(addList);
                                docType.setDiffReplaceType(replaceList);
                                docType.setDiffRemoveType(removeList);

                                docType.setSel(xcapDiffNotifyObj.getDirURI());
                                docType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
                                docType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
                                xcapDiffTypeObj.setDocumentType(docType);

                                knLogger.debug(methodName, "Regenerating truncated XML");
                                notifyXML = KnJAXBParser.convertBeanToXml(xcapDiffTypeObj, XcapDiffType.class);
                                knLogger.debug(methodName, "Truncated Notification XML - ", notifyXML);
                            }
                        }
                    } catch (KnJAXBProcessException ope) {
                        knLogger.error(methodName, "Notification message construction failed - " +
                                new KnException(ope.getErrorCode(), ope.getMessage(), ope));
                        continue; //Continuing with remaining messages
                    } catch (Exception e) {
                        knLogger.error(methodName, "Unexpected Exception occurred while processing notification - ",
                                new KnException("", e.getMessage(), e));
                        continue; //Continuing with remaining messages
                    }
                    // generating the payload for Subscription Proxy for App interface
                    KnTLVDocDiffGenerator tlvDocDiffGenerator = new KnTLVDocDiffGenerator();
                    KnRequestObject requestObj = new KnRequestObject();
                    byte[] payLoad;
                    int featureId = KnXcapNotifyConstants.DOC_CHANGE_EVENT_FEATURE_ID;

                    requestObj.setFeatureID(featureId);
                    requestObj.setHomeRTXId(homeRtxId);
                    requestObj.setMessageID(KnXcapNotifyConstants.XCAP_NOTIFICATION_MESSAGE_ID);
                    requestObj.setRTXVersion(KnXcapNotifyConstants.RTX_VERSION);
                    //We need to set protocol Version to 1 for docdiff,  else decoding will fail
                    requestObj.setProtocolVersion(1);
                    KnPoCXcapDiffNotifyDTO pocXcapDiffNotifyDTO = new KnPoCXcapDiffNotifyDTO();
                    pocXcapDiffNotifyDTO.setXcapURI(xcapDiffNotifyObj.getDirURI());
                    pocXcapDiffNotifyDTO.setXcapDiff(notifyXML);
                    //INT82147 Identify type of Notify listed below, Directory change Notify  - 1, Doc-Diff  Notify - 2
                    pocXcapDiffNotifyDTO.setTypeOfNotify(KnXcapNotifyConstants.DOC_DIFF_NOTIFY_TYPE);
                    //Setting ntfyOnAnyMDN=1 for ABDG group Type
                    pocXcapDiffNotifyDTO.setNtfyOnAnyMDN(xcapDiffNotifyObj.getNtfyOnAnyMDN());
                    requestObj.setRequestData(pocXcapDiffNotifyDTO);

                    knLogger.info(methodName, "TLV Request Object - ", KnGDPRTemplate.mdnUriTemplate(requestObj.toString()));
                    payLoad = tlvDocDiffGenerator.generateTLV(requestObj);
       //             knLogger.debug(methodName, "TLV Request Object Hex payLoad- ", bytesToHexString(payLoad));
                 // GG Check for registeredHome
                    String registeredHome=getRegisterPOCHomeByMDN(getMDNFromURI(xcapDiffNotifyObj.getDirURI()));
                    knLogger.debug(methodName, "registeredHome :", registeredHome);
                    if(registeredHome!=null&& !registeredHome.isEmpty())
                    {
                    	xcapDiffNotifyObj.setPocHome(registeredHome);
                    	xcapDiffNotifyObj.setPresenceHome(registeredHome);
                    }
                    KnNotifyPayloadDTO notifyPayloadDTO = new KnNotifyPayloadDTO();
                    notifyPayloadDTO.setPayload(payLoad);
                    notifyPayloadDTO.setDocType(addlDocTypeInfo.getDocType());
                    notifyPayloadDTO.setPttServerId(xcapDiffNotifyObj.getPresenceHome());
                    String documentSelector=null;
                    if(xcapDiffTypeObj.getDocumentType().getDiffAddType()!=null && !xcapDiffTypeObj.getDocumentType().getDiffAddType().isEmpty()) {
                    	documentSelector=xcapDiffTypeObj.getDocumentType().getDiffAddType().get(0).getSel();
                    }

                    if(xcapDiffTypeObj.getDocumentType().getDiffRemoveType()!=null && !xcapDiffTypeObj.getDocumentType().getDiffRemoveType().isEmpty()) {
                    	documentSelector=xcapDiffTypeObj.getDocumentType().getDiffRemoveType().get(0).getSel();
                    }


                    if(xcapDiffTypeObj.getDocumentType().getDiffReplaceType()!=null && !xcapDiffTypeObj.getDocumentType().getDiffReplaceType().isEmpty()) {
                    	documentSelector=xcapDiffTypeObj.getDocumentType().getDiffReplaceType().get(0).getSel();
                    }

                    if(xcapDiffTypeObj.getDocumentType().getDiffReplaceChangeType()!=null && !xcapDiffTypeObj.getDocumentType().getDiffReplaceChangeType().isEmpty()) {
                    	documentSelector=xcapDiffTypeObj.getDocumentType().getDiffReplaceChangeType().get(0).getSel();
                    }

                    knLogger.debug(methodName, "selector passing :", KnGDPRTemplate.mdnUriTemplate(documentSelector));
                    knLogger.debug(methodName, "selector passing URI :", KnGDPRTemplate.mdnUriTemplate(xcapDiffTypeObj.getDocumentType().getSel()));
                   Map<String,KnXDMSubsProvDTO> mdnMaps=new HashMap<String,KnXDMSubsProvDTO>();
                  String mdn= getIosMdns(mdnMaps,xcapDiffTypeObj.getDocumentType().getSel(),documentSelector, mdnActiveFsMap);
                  knLogger.debug(methodName, "mdn we got :", KnGDPRTemplate.mdn(mdn));

                 // byte[] xmlPaylod=Base64.getEncoder().encode(toByteArray(notifyXML));
                 String xmlPayload=Base64.getEncoder().encodeToString(notifyXML.getBytes());
                  notifyPayloadDTO.setXmlLoad(xmlPayload);
                    notifyPayloadDTO.setIosMdn(mdn);
                    notifyPayloadDTO.setMdnMaps(mdnMaps);
                    listOfNotifyPayload.add(notifyPayloadDTO);
                }
            } catch (Exception e) {
                knLogger.error(methodName, "Exception while generating payload - ",
                        new KnException("", e.getMessage(), e));
                //Will continue with the
            }
        }
        knLogger.info(methodName, "Number of Payloads - ", listOfNotifyPayload.size());
        return listOfNotifyPayload;
    }

    /**
     * function to send list of notification to DB (TRANSPORTSERVERSOURCEINFO)
     *
     * @param listOfNotifyPayLoad
     * @param featureId
     * @param persisterTxn
     * @return boolean
     */

    public boolean sendBulkNotifications(List<KnNotifyPayloadDTO> listOfNotifyPayLoad, int featureId, Map<String,KnXDMSubsProvDTO>  mdnSubsInfoMap,
                                         KnPersisterTxn persisterTxn, Map<String,Set<String>> baseMdnMap) {
        String methodName = "sendBulkNotifications(ArrayList<byte[]>, String, String)";
        knLogger.info(methodName, "ENTRY: Send Notification - ",
                ", homeRTX ID - [", homeRtxId, "], Feature Id - ", featureId);
        List<KnNotifyPayloadDTO> upgradedPocNotifyList = new ArrayList<>();
        List<KnNotifyPayloadDTO> lagecyPOCNotifyList = new ArrayList<>();

        for (KnNotifyPayloadDTO knNotifyPayloadDTO : listOfNotifyPayLoad) {
            //Check GG to find unupgraded PoCServer
            boolean lagecy = this.isUnUpgradedPOCSERVER(knNotifyPayloadDTO.getPttServerId());
            knLogger.info(methodName, "PttServerId", knNotifyPayloadDTO.getPttServerId(), "Status - ", lagecy);
            knLogger.info(methodName, "IOS-MDN in sendBULK ", KnGDPRTemplate.mdn(knNotifyPayloadDTO.getIosMdn()));
            if (knNotifyPayloadDTO.getIosMdn() != null && !"NOTIOS".equals(knNotifyPayloadDTO.getIosMdn())) {
                try {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    sendIOStoRMQNotification(knNotifyPayloadDTO.getMdnMaps(), knNotifyPayloadDTO.getIosMdn(), knNotifyPayloadDTO.getXmlLoad(),
                            featureId, knNotifyPayloadDTO.getPttServerId(), persisterTxn, mdnSubsInfoMap, baseMdnMap);
                    persisterTxn.save();
                } catch (KnPersistenceException e) {
                    knLogger.error("exception", e);
                    rollback(persisterTxn);

                }
            }

            if (lagecy) {
                lagecyPOCNotifyList.add(knNotifyPayloadDTO);
            } else {
                upgradedPocNotifyList.add(knNotifyPayloadDTO);
            }
        }

        boolean status = true;
        if (!lagecyPOCNotifyList.isEmpty()) {
            status = lagecySendBulkNotifications(lagecyPOCNotifyList, featureId, persisterTxn);
        }

        for (KnNotifyPayloadDTO knNotifyPayloadDTO : upgradedPocNotifyList) {
            int newFeatureId = -1;
            if (featureId == -1) {
                newFeatureId = knNotifyPayloadDTO.getFeatureId();
            } else {
                newFeatureId = featureId;
            }
            boolean status2 = sendRMQNotification(knNotifyPayloadDTO.getPayload(), newFeatureId, knNotifyPayloadDTO.getPttServerId());
            addPegs(knNotifyPayloadDTO.getDocType(), status2);
            status = status && status2;
        }
        knLogger.info(methodName, "status", status);
        return status;

    }

    private void addPegs(String docType, boolean status2) {
        String methodName = "addPegs(KnNotifyPayloadDTO,boolean)";
        if (status2) {
            knLogger.debug(methodName, "Success Pegs", docType);
            if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.CORP_CONTACT_DOC.value())) {
                //increase pegs counter for Corp_Contact_doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_CORP_CONTACT_DOC_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.SUBS_CONFIG_DOC.value())) {
                //increase pegs counter for Corp_Group_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_SUBS_CONFIG_DOC_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.EMERGENCY_DOC.value())) {
                //increase pegs counter for Corp_Group_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_EMERGENCY_DOC_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.AU_PERMISSION_DOC.value())) {
                //increase pegs counter for Corp_Group_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_AU_PERMISSION_DOC_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.TGSC_LIST_DOC.value())) {
                //increase pegs counter for Corp_Group_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_TGSC_LIST_DOC_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.XCAP_DIRECTORY_DOCUMENT.value())) {
                //increase pegs counter for Corp_Group_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_DIR_DOC_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.CORP_GROUP_DOC.value())) {
                //increase pegs counter for Corp_Group_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_CORP_GROUP_DOC_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.SUBS_DEREGISTER_NOTIFY_DOC.value())) {
                //increase pegs counter for Subs_Deregister_notify_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_SUBS_DE_REGISTER_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.SUBS_CHNAGE_MDN_DOC.value())) {
                //increase pegs counter for Subs_Chnage_MDN_Doc
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_CHANGE_MDN_OIDCXCAP_NOTIFY);
            } else if (Objects.equals(docType, KnXcapNotifyConstants.DOC_TYPE.SUBS_PROFILE_CHANGE_NOTIFY.value())) {
                //increase pegs counter for Subs_Profile_Change_notify
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_CHANGE_PROFILE_NOTIFY);
            }
            //increase pegs counter for Total count
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_OIDCXCAP_NOTIFY_PUBLISHED);
        } else {
            knLogger.error(methodName, "Failure Pegs", docType);
            //increase failure pegs count for all doc type
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_TOTAL_FAILURE_NOTIFY);
        }
    }

   private boolean lagecySendBulkNotifications(List<KnNotifyPayloadDTO> listOfNotifyPayLoad, int featureId, KnPersisterTxn persisterTxn) {

       String methodName = "lagecySendBulkNotifications(ArrayList<byte[]>, String, String)";
       knLogger.debug(methodName, "ENTRY: Sending bulk notifications");

        knLogger.info(methodName, "ENTRY: Send Notification - ",
                ", homeRTX ID - [", homeRtxId, "], Feature Id - ", featureId);

        Connection conn = null;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;

        String query = "INSERT INTO DG.TRANSPORTSERVERSOURCEINFO VALUES(?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?)";

        int targetType = 1;    //destination address type
        String targetId;   //<destination RTXID>:<AppRouter featureID>
        int targetSubsystemId = KnConstants.SUBSYS_ID_APPROUTER;  //AppRouter subsystem ID
        int sourceSubsystemId = KnConstants.SUBSYS_ID_XDMDATAMGR;  //XDM-DataManager Subsystem ID
        byte[] tlvHeader;
        byte[] tlvBody;
        int subnetId = KnConstants.XDMDATAMGR_INTERFACEID;  //subnetId mapped to "inter-card signaling" interface (InterfaceId 1) in DG.LogicalIPInterfaceDefn.
        String nexThopRTX;   //destination RTXID
        int nexThopSubsSysId = KnConstants.SUBSYS_ID_APPROUTER;   //AppRouter subsystem ID
        int msgStatus = KnConstants.MSG_RESOLVED; //resolved
        boolean tlvInsertStatus = false;
        byte[] payload;
        String pttServerId;
        int seqNumber;
        try {
            //opening the transaction
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(homeRtxId, false);
            pStmt = conn.prepareStatement(query);
            for (KnNotifyPayloadDTO notifyPayload : listOfNotifyPayLoad) {
                seqNumber = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_TRANSPORT_SERVER_SOURCE_INFO, homeRtxId,
                        KnConstants.TABLE_TRANS_SER_SRC_COL, false, null);

                payload = notifyPayload.getPayload();
                pttServerId = notifyPayload.getPttServerId();
                //if feature id given as -1 then take from DTO
              //fix for INT-534
                int newFeatureId = -1;
                if (featureId == -1) {
                    newFeatureId = notifyPayload.getFeatureId();
                }else {
                    newFeatureId= featureId;
                }
                knLogger.debug(methodName, "newFeatureId:", newFeatureId);
                //knLogger.debug(methodName, "featureId:", featureId, "bulkMyPayloadbytesToHexString:",
                //        bytesToHexString(payload), "endofpayload");
                targetId = pttServerId + ":" + newFeatureId;
                nexThopRTX = pttServerId;
                tlvHeader = Arrays.copyOfRange(payload, 0, KnConstants.HEADER_SIZE);
                tlvBody = Arrays.copyOfRange(payload, KnConstants.HEADER_SIZE, payload.length);
                InputStream isHeader = new ByteArrayInputStream(tlvHeader);
                InputStream isBody = new ByteArrayInputStream(tlvBody);
                pStmt.setInt(1, targetType);
                pStmt.setString(2, targetId);
                pStmt.setInt(3, targetSubsystemId);
                pStmt.setInt(4, sourceSubsystemId);
                pStmt.setBinaryStream(5, isHeader, isHeader.available());
                pStmt.setBinaryStream(6, isBody, isBody.available());
                pStmt.setInt(7, seqNumber);
                pStmt.setInt(8, subnetId);
                pStmt.setString(9, nexThopRTX);
                pStmt.setInt(10, nexThopSubsSysId);
                pStmt.setInt(11, msgStatus);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing - ", query);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Saving the transaction ");
            if (ownedTxn) {
                persisterTxn.save();
            }

            tlvInsertStatus = true;
        } catch (KnPersistenceException e) {
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            knLogger.error(methodName, "Persister Txn occurred - ",e);
        } catch (Exception e) {
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            knLogger.error(methodName, "Unexpected Exception occurred - ",e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    knLogger.error(methodName, "SQL Exception occurred in closing connection - ", e);
                }
            }
        }
        return tlvInsertStatus;
    }

    private List<DocumentType> populateChangeLog(Collection<KnXcapDiffDocDTO> xcapDiffDocObsFinalList,String protocolVersion) {

        String methodName = "populateChangeLog(Collection<KnXcapDiffDocDTO>)";
        Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = new HashSet(xcapDiffDocObsFinalList);
        knLogger.debug(methodName," xcapDiffDocObsFinalList :",xcapDiffDocObsFinalList," xcapDiffDocObsList:",xcapDiffDocObsList);
        // Loop through all the doc change DTO and construct the appropriate docType with add, replace and remove docs
        List<DocumentType> docTypeList = new ArrayList<DocumentType>();
        DocumentType docType = new DocumentType();
        LinkedHashSet<KnMcsxcapMdnDTO> mdns= new LinkedHashSet<KnMcsxcapMdnDTO>();

        if (xcapDiffDocObsList != null) {
            ArrayList<DiffAdd> addList = new ArrayList<DiffAdd>();
            ArrayList<DiffReplace> replaceList = new ArrayList<DiffReplace>();
            ArrayList<DiffReplaceChangeValue> diffReplaceChangeList = new ArrayList<DiffReplaceChangeValue>();
            ArrayList<DiffRemove> removeList = new ArrayList<DiffRemove>();
            for (KnXcapDiffDocDTO xcapDiffDocObj : xcapDiffDocObsList) {
                int pv = 0;
                if(protocolVersion!=null && protocolVersion.trim().length()>0) {
                    knLogger.debug(methodName,"protocol version",pv);
                    pv = Integer.parseInt(protocolVersion);
                }else {
                    knLogger.debug(methodName,"protocol version is null or empty setting PV to default = 0",pv);
                }
				if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.ADD.value()) {
					DiffAdd diffAddObj = new DiffAdd();
					diffAddObj.setSel(xcapDiffDocObj.getDocumentSelector());
					EntryType entryTypeObj = new EntryType();
					entryTypeObj.setUri(xcapDiffDocObj.getDocUri());
					entryTypeObj.setEtag(xcapDiffDocObj.getDocEtag());
                    // Set videoCallPermission - always log and set it
                    Integer videoPermission = xcapDiffDocObj.getVideoPermission();
                    if (videoPermission != null) {
                        entryTypeObj.setVideoCallPermission(String.valueOf(videoPermission));
                        knLogger.debug(methodName, "ADD: Setting video-call-permission in EntryType - uri:", xcapDiffDocObj.getDocUri(), ", videoPermission:", videoPermission);
                    } else {
                        // Even if null, set default to ensure it appears
                        entryTypeObj.setVideoCallPermission(String.valueOf(KnConstants.VIDEO_PERMISSION_VALUE));
                        knLogger.debug(methodName, "ADD: videoPermission is NULL, setting default in EntryType - uri:", xcapDiffDocObj.getDocUri(), ", default:", KnConstants.VIDEO_PERMISSION_VALUE);
                    }
                    if (pv >= KnConstants.PROTOCOL_VERSION_18_X) {
						entryTypeObj.setIsAbdgGroup(xcapDiffDocObj.getIsAbdgGroup());
					}
                    if (pv >= KnConstants.PROTOCOL_VERSION_23) {
                        entryTypeObj.setExternalCorpGroup(xcapDiffDocObj.getExternalCorpGroup());
                    }
                    if (pv >= KnConstants.PROTOCOL_VERSION_24) {
                        entryTypeObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                    }
					ArrayList<EntryType> entryList = new ArrayList<EntryType>();
					entryList.add(entryTypeObj);
					diffAddObj.setEntryType(entryList);
					addList.add(diffAddObj);
					populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
				} else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REPLACE.value()) {
                    if(xcapDiffDocObj.getDocEtag() != null){
                        DiffReplaceChangeValue diffReplaceObj = new DiffReplaceChangeValue();
                        diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        diffReplaceObj.setChangeValue(xcapDiffDocObj.getDocEtag());
                        if (pv >= KnConstants.PROTOCOL_VERSION_23) {
                            diffReplaceObj.setExternalCorpGroup(xcapDiffDocObj.getExternalCorpGroup());
                        }
                        if (pv >= KnConstants.PROTOCOL_VERSION_24) {
                            diffReplaceObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                        }
                        diffReplaceChangeList.add(diffReplaceObj);
                        populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                    }else {
                        DiffReplace diffReplaceObj = new DiffReplace();
                        diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        if (pv >= KnConstants.PROTOCOL_VERSION_23) {
                            diffReplaceObj.setExternalCorpGroup(xcapDiffDocObj.getExternalCorpGroup());
                        }
                        if (pv >= KnConstants.PROTOCOL_VERSION_24) {
                            diffReplaceObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                        }
                        replaceList.add(diffReplaceObj);
                        populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                    }
                } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value())
                {
                    DiffRemove diffRemoveObj = new DiffRemove();
                    diffRemoveObj.setSel(xcapDiffDocObj.getDocumentSelector());
                    if (pv >= KnConstants.PROTOCOL_VERSION_23) {
                        diffRemoveObj.setExternalCorpGroup(xcapDiffDocObj.getExternalCorpGroup());
                    }
                    if (pv >= KnConstants.PROTOCOL_VERSION_24) {
                        diffRemoveObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                    }
                    diffRemoveObj.setWs(xcapDiffDocObj.getRemoveWs());
                    removeList.add(diffRemoveObj);
                    populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                } else {
                    knLogger.error(methodName, "Unknown Doc Change Type");
                    break;
                }
            }
            if (!addList.isEmpty()) {
                docType.setDiffAddType(addList);
            }
            if (!replaceList.isEmpty()) {
                docType.setDiffReplaceType(replaceList);
            }
            if (!removeList.isEmpty()) {
                docType.setDiffRemoveType(removeList);
            }
            if (!diffReplaceChangeList.isEmpty()) {
                docType.setDiffReplaceChangeType(diffReplaceChangeList);
            }
        }
        docType.setDocType(KnXcapNotifyConstants.DOC_TYPE.XCAP_DIRECTORY_DOCUMENT.value());
        docTypeList.add(docType);
        knLogger.debug(methodName,"populateChangeLog-docTypeList: ",docTypeList);

        //sending System profiles Mcs XCAP Notifies
        for (DocumentType documentType : docTypeList) {
			populateSystemProfileMdnsForDocChange(mdns, documentType.getSel());

		}
		if(!mdns.isEmpty())
		{
            knLogger.debug(methodName,"sending System profiles Mcs XCAP Notifies for MDNs",mdns);
			knMCSXCAPNotifier.sendMCSXCAPNotification(mdns);
		}
		knLogger.debug(methodName," docTypeList -",docTypeList);
        return docTypeList;
    }

    void populateSystemProfileMdnsForDocChange(LinkedHashSet<KnMcsxcapMdnDTO> mdns, String documentSelector) {
		String methodName = "populateSystemProfileMdnsForDocChange";
        knLogger.debug(methodName, mdns == null ? mdns : mdns, KnGDPRTemplate.mdnUriTemplate(documentSelector));
		if (checkIfDocChage(documentSelector)&&documentSelector.contains("tel:+"))
		{
			String sel = documentSelector;
			String substring = sel.substring(sel.indexOf("tel:+"));
			String mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
            KnMcsxcapMdnDTO mcsxcapMdnDTO = new KnMcsxcapMdnDTO();
            mcsxcapMdnDTO.setMdn(mdn.trim());
            if (documentSelector.contains("kn-emergency-config")) {
                mcsxcapMdnDTO.setEmergencyFlag(true);
            }
            mdns.add(mcsxcapMdnDTO);

		}
	}

	/*
	 * private void populateIosMdns(LinkedHashSet<String> iosMdns, String
	 * documentSelector) { String methodName =
	 * "populateSystemProfileMdnsForDocChange"; //String documentSelector =
	 * xcapDiffDocObj.getDocumentSelector(); //String activeFs2 = xcapDiffDocObj.is
	 *
	 * knLogger.debug(methodName, iosMdns, documentSelector); if
	 * (checkIfDocChage(documentSelector)&&documentSelector.contains("tel:+")) {
	 * String sel = documentSelector;
	 *
	 * String substring = sel.substring(sel.indexOf("tel:+")); String mdn =
	 * substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
	 * if(checkIfDocisGroupChage(documentSelector) && isPushNotifyEnabled ) {
	 * knLogger.debug("MDN is IOS in docselector :-",mdn); iosMdns.add(mdn.trim());
	 * }
	 *
	 * } }
	 */
	private void populateIosMdns(LinkedHashSet<String> iosMdns, String uri,String documentSelector, boolean isPushNotifyEnabled) {
		String methodName = "populateIosMdn";
		//String documentSelector = xcapDiffDocObj.getDocumentSelector();
		//String activeFs2 = xcapDiffDocObj.is

		knLogger.debug(methodName, iosMdns == null ? iosMdns : KnGDPRTemplate.mdnList(iosMdns), KnGDPRTemplate.mdnUriTemplate(documentSelector)," URI:-",KnGDPRTemplate.mdnUriTemplate(uri));

			String mdn=null;

			if(uri!=null &&uri.contains("tel:+")) {
				String substring = uri.substring(uri.indexOf("tel:+"));
				 mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));

			}
			KnXDMSubsProvDTO knXDMSubsProvDTO=new KnXDMSubsProvDTO();
			if(mdn!=null && checkIfDocisAllowedChange(documentSelector,knXDMSubsProvDTO) && isPushNotifyEnabled ) {
				knLogger.debug(methodName,"MDN is IOS in docselector :-",KnGDPRTemplate.mdn(mdn));
				iosMdns.add(mdn.trim());
			}


	}
	private String getIosMdns( Map<String, KnXDMSubsProvDTO> mdnMaps, String uri,String documentSelector,Map<String,KnXDMSubsProvDTO> mdnActiveFsMap) {
		String methodName = "getIosMdns";
		knLogger.debug(methodName,  KnGDPRTemplate.mdnUriTemplate(documentSelector));
		String mdn=null;
        if (null == documentSelector && null != uri) {
            if (uri.contains(APP_UID_CORP_GROUP)
                    || uri.contains(APP_UID_AUTH_LIST)
                    || uri.contains(APP_UID_EMERG_CONFIG)) {
			String substring = uri.substring(uri.indexOf("tel:+"));
			 mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
			}
		}else if(documentSelector!=null) {
			String sel = uri;
			String substring = sel.substring(sel.indexOf("tel:+"));
			 mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
		}
			knLogger.info(methodName,  "MDN from doc:-",KnGDPRTemplate.mdn(mdn));
			List<String> mdns=new ArrayList<String>();
			if(mdn!=null) {
			mdns.add(mdn);
			try {
                KnXDMSubsProvDTO sub = null;
                if (mdnActiveFsMap != null && mdnActiveFsMap.containsKey(mdn)) {
                    sub = mdnActiveFsMap.get(mdn);
                    knLogger.debug("mdn found in cache");
                } else {
                    knLogger.debug("mdn not found in cache");
                    KnXDMSubsProfileRespDTO subsInfo = genInfoUtil.selectSubsProfileInfo(mdns, null);
                    if (subsInfo != null && subsInfo.getSubsRespDTO() != null && !subsInfo.getSubsRespDTO().isEmpty()) {
                        List<KnXDMSubsProvDTO> subslist = new ArrayList<>(subsInfo.getSubsRespDTO());
                        sub = subslist.get(0);
                    }
                }
			if(sub !=null) {
			mdnMaps.put(mdn, sub);
			boolean isAllowedOpertion=false;
			if(documentSelector!=null) {
				knLogger.debug(methodName,  "Doc selector seleceted:-",KnGDPRTemplate.mdn(mdn)," selector :-",KnGDPRTemplate.mdnUriTemplate(documentSelector));
				isAllowedOpertion= checkIfDocisAllowedChange(documentSelector,sub);
			}else if(uri!=null) {
				knLogger.debug(methodName,  "URI seleceted- for ",KnGDPRTemplate.mdn(mdn)," URI ",KnGDPRTemplate.mdnUriTemplate(uri));
				isAllowedOpertion= checkIfDocisAllowedChange(uri,sub);

			}
			if(isAllowedOpertion && isIosBitEnabled(mdn,sub.getActiveFS2()) ) {
				knLogger.debug(methodName,"MDN is IOS in docselector :-",KnGDPRTemplate.mdn(mdn));
				return mdn.trim();
			}
			}
			} catch (Exception e) {
				knLogger.error(methodName,"exception",e);
			}
			}
		return "NOTIOS";
	}

	private boolean isIosBitEnabled(String mdn, String activeFs2) {
		String methodName="isIosBitEnabled";
		knLogger.debug(methodName,"IOSMDN:-",KnGDPRTemplate.mdn(mdn),"ACTIVE -fs 2",activeFs2);
		boolean isIos=true;
		try {
       	   isIos = KnGeneralUtil.getFeatureBitValue(activeFs2,com.kodiak.xdms.server.common.resources.KnConstants.REMOTEPUSHNOTIfICATION);

		} catch (Exception  e) {
			knLogger.error("exception at ios check",e);
		}
		return isIos;
	}
//need to check usage-list
	private boolean checkIfDocisAllowedChange(String uri, KnXDMSubsProvDTO knXDMSubsProvDTO) {
		boolean isAllowedChange=false;
        if (null != uri && (uri.contains(APP_UID_CORP_GROUP)
                || uri.contains(APP_UID_AUTH_LIST)
                || uri.contains(APP_UID_EMERG_CONFIG))) {
            isAllowedChange = true;

        }
		return isAllowedChange;
	}

/*	private boolean isUserProfile(String uri, KnXDMSubsProvDTO knXDMSubsProvDTO) {
		return knXDMSubsProvDTO.getUserProfileId()!=null &&uri.contains("org.openmobilealliance.group-usage-list");
	}*/
private void populateSystemProfileMdnsForDocChange(LinkedHashSet<KnMcsxcapMdnDTO> mdns, String documentSelector,
			String docURI) {
		String methodName = "populateSystemProfileMdnsForDocChange";
    knLogger.debug(methodName, mdns == null ? mdns : mdns, "documentSelector--", KnGDPRTemplate.mdnUriTemplate(documentSelector), "docURI--", KnGDPRTemplate.mdnUriTemplate(docURI));
		if (checkIfDocChage(documentSelector) && documentSelector.contains("tel:+")) {
			String sel = documentSelector;
			String substring = sel.substring(sel.indexOf("tel:+"));
			String mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
            KnMcsxcapMdnDTO mcsxcapMdnDTO = new KnMcsxcapMdnDTO();
            mcsxcapMdnDTO.setMdn(mdn.trim());
            if (documentSelector.contains("kn-emergency-config")) {
                mcsxcapMdnDTO.setEmergencyFlag(true);
            }
            mdns.add(mcsxcapMdnDTO);

		} else if (checkIfDocChage(docURI) && docURI.contains("tel:+")) {
			String sel = docURI;
			String substring = sel.substring(sel.indexOf("tel:+"));
			String mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
            KnMcsxcapMdnDTO mcsxcapMdnDTO = new KnMcsxcapMdnDTO();
            mcsxcapMdnDTO.setMdn(mdn.trim());
            if (documentSelector.contains("kn-emergency-config")) {
                mcsxcapMdnDTO.setEmergencyFlag(true);
            }
            mdns.add(mcsxcapMdnDTO);
		}
	}

	private boolean checkIfDocChage(String uri) {
		return uri != null && (uri.contains("kn-authorization-list")
				|| uri.contains("kn-corp-resource-lists")
				|| uri.contains("kn-emergency-config") || uri.contains("kn-tgsc-list")
				|| uri.contains("kn-tgss-list")
				|| uri.contains("org.openmobilealliance.group-usage-list")
				|| uri.contains("kn-subscriber-config")
			    || uri.contains("kn-corp-groups"));
	}


    /**
     * This method is used to send the new notifictaions for the client which returns the doc diff notifications
     *
     * @param xcapDiffDocObsFinalList
     * @param xcapDiffNotifyObj
     * @return
     */
    private List<DocumentType> populateNewChangeLog(Collection<KnXcapDiffDocDTO> xcapDiffDocObsFinalList, KnXcapDiffDirChgNotifyDTO xcapDiffNotifyObj, String protocolVersion) {

        String methodName = "populateNewChangeLog(Collection<KnXcapDiffDocDTO>)";
        knLogger.debug(methodName, "Entry Point xcapDiffDocObsFinalList: ",xcapDiffDocObsFinalList);
        Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = new HashSet(xcapDiffDocObsFinalList);
        // Loop through all the doc change DTO and construct the appropriate docType with add, replace and remove docs
        List<DocumentType> docTypeList = new ArrayList<DocumentType>();
        LinkedHashSet<KnMcsxcapMdnDTO> mdns= new LinkedHashSet<KnMcsxcapMdnDTO>();
        if (xcapDiffDocObsList != null && !xcapDiffDocObsList.isEmpty()) {
            for (KnXcapDiffDocDTO xcapDiffDocObj : xcapDiffDocObsList) {
                int protocol = Integer.parseInt(protocolVersion);
                DocumentType docResourceType = new DocumentType();
                DocumentType docGroupType = new DocumentType();
                DocumentType oldDocType = new DocumentType();
                ArrayList<DiffAdd> addList = new ArrayList<DiffAdd>();
                ArrayList<DiffReplace> replaceList = new ArrayList<DiffReplace>();
                ArrayList<DiffReplaceChangeValue> diffReplaceChangeList = new ArrayList<DiffReplaceChangeValue>();
                ArrayList<DiffRemove> removeList = new ArrayList<DiffRemove>();
                if ((xcapDiffDocObj.getAddedContactList() != null && !xcapDiffDocObj.getAddedContactList().isEmpty()) ||
                        (xcapDiffDocObj.getRemovedContactList() != null && !xcapDiffDocObj.getRemovedContactList().isEmpty()) ||
                        (xcapDiffDocObj.getModifiedContactList() != null && !xcapDiffDocObj.getModifiedContactList().isEmpty())) {
                    if (xcapDiffDocObj.getAddedContactList() != null && !xcapDiffDocObj.getAddedContactList().isEmpty()) {
                        docResourceType.setSel(xcapDiffDocObj.getDocumentSelector());
                        docResourceType.setNewEtag(xcapDiffDocObj.getDocEtag());
                        docResourceType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                        DiffAdd diffAddObj = new DiffAdd();
                        ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                        Collection<KnSubscriberDTO> mdnList = xcapDiffDocObj.getAddedContactList();
                        if (mdnList != null) {
                            for (KnSubscriberDTO subsc : mdnList) {
                                EntryType entryTypeObj = new EntryType();
                                entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subsc.getMdn()));
                                DisplayName display = new DisplayName();
                                display.setValue(subsc.getNetworkName());
                                entryTypeObj.setDisplayNameEntry(display);
                                entryTypeObj.setActiveFS1(subsc.getActiveFS2());
                                if (protocol > KnConstants.PROTOCOL_VERSION_7_X) {

                                    // Logic Behind below 2 "if Looop", changes made with minimal efforts:
                                    // For Internal PoCSubscriber(Belongs to same corporation): ContactType = 0
                                    // For Internal PoCSubscriber(Belongs to Other corporation): ContactType = 2
                                    // For ExternalSubscriber: ContactType = 1
                                    // So InternalPoCSubscriber(Belongs to same corporation) is looping then whatever is value is there just pass it.
                                    // else it will fall into loop 2.

                                    //loop: 1
                                    if(subsc.getClientType() != 0) {
                                        ClientType clientType = new ClientType();
                                        clientType.setValue(String.valueOf(subsc.getClientType()));
                                        entryTypeObj.setClientType(clientType);
                                        ContactType contactType = new ContactType();
                                        contactType.setValue(String.valueOf(subsc.getContact_type()));
                                        entryTypeObj.setContactType(contactType);
                                    }
                                    //loop: 2
                                    // Added because of SDD: SDD-POC-R8.3-Part5:INT-7867 XCAP interface Enhn: SDD_R8_3_P5_F_4
                                    if(subsc.getContact_type() != 0){
                                        ClientType clientType = new ClientType();
                                        clientType.setValue(String.valueOf(KnConstants.UNKNOWN_CLIENT_TYPE));
                                        entryTypeObj.setClientType(clientType);
                                        ContactType contactType = new ContactType();
                                        contactType.setValue(String.valueOf(subsc.getContact_type()));
                                        entryTypeObj.setContactType(contactType);
                                    }
                                }
                                if (protocol > KnConstants.PROTOCOL_VERSION_10_X) {
                                    if(subsc.getUa() != null) {
                                        UA ua = new UA();
                                        ua.setValue(subsc.getUa());
                                        entryTypeObj.setUa(ua);
                                    }
                                }
                                if (protocol > KnConstants.PROTOCOL_VERSION_8_X){
                                    if(subsc.getCallPermission() != null){
                                        CallPermission callPermission = new CallPermission();
                                        callPermission.setValue(subsc.getCallPermission());
                                        entryTypeObj.setCallPermission(callPermission);
                                    }
                                }

                                if (protocol >= PROTOCOL_VERSION_29) {
                                    if(subsc.getVideoCallPermission() != null){
                                        entryTypeObj.setVideoCallPermission(subsc.getVideoCallPermission());
                                        knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                    } else {
                                        knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                    }
                                }

                                entryList.add(entryTypeObj);
                            }
                        }
                        diffAddObj.setEntryType(entryList);
                        addList.add(diffAddObj);
                        if (!addList.isEmpty()) {
                            docResourceType.setDiffAddType(addList);
                        }
                    }
                    if (xcapDiffDocObj.getRemovedContactList() != null && !xcapDiffDocObj.getRemovedContactList().isEmpty()) {
                        DiffRemove diffRemoveObj = new DiffRemove();
                        //diffRemoveObj.setSel("resource-list/list%5B@name=%22index%22%5D");
                        docResourceType.setSel(xcapDiffDocObj.getDocumentSelector());
                        docResourceType.setNewEtag(xcapDiffDocObj.getDocEtag());
                        docResourceType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                        Collection<String> mdnList = xcapDiffDocObj.getRemovedContactList();
                        ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                        if (mdnList != null) {
                            for (String mdn : mdnList) {
                                EntryType entryTypeObj = new EntryType();
                                entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(mdn));
                                entryList.add(entryTypeObj);
                            }
                        }
                        diffRemoveObj.setEntryType(entryList);
                        removeList.add(diffRemoveObj);
                        if (!removeList.isEmpty()) {
                            docResourceType.setDiffRemoveType(removeList);
                        }
                    }
                    if (xcapDiffDocObj.getModifiedContactList() != null && !xcapDiffDocObj.getModifiedContactList().isEmpty()) {
                        docResourceType.setSel(xcapDiffDocObj.getDocumentSelector());
                        docResourceType.setNewEtag(xcapDiffDocObj.getDocEtag());
                        docResourceType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                        DiffReplace diffReplaceObj = new DiffReplace();
                        ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                        Collection<KnSubscriberDTO> mdnList = xcapDiffDocObj.getModifiedContactList();
                        if (mdnList != null) {
                            for (KnSubscriberDTO subsc : mdnList) {
                                EntryType entryTypeObj = new EntryType();
                                entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subsc.getMdn()));
                                entryTypeObj.setActiveFS1(subsc.getActiveFS2());
                                DisplayName display = new DisplayName();
                                if(subsc.getNetworkName() != null) {
                                    display.setValue(subsc.getNetworkName());
                                    entryTypeObj.setDisplayNameEntry(display);
                                }
                                if (protocol > KnConstants.PROTOCOL_VERSION_7_X) {
                                    if(subsc.getClientType() != 0) {
                                        ClientType clientType = new ClientType();
                                        clientType.setValue(String.valueOf(subsc.getClientType()));
                                        entryTypeObj.setClientType(clientType);
                                        ContactType contactType = new ContactType();
                                        contactType.setValue(String.valueOf(subsc.getContact_type()));
                                        entryTypeObj.setContactType(contactType);
                                    }
                                    // Added because of SDD: SDD-POC-R8.3-Part5:INT-7867 XCAP interface Enhn: SDD_R8_3_P5_F_4
                                    if(subsc.getContact_type() != 0){
                                        ClientType clientType = new ClientType();
                                        clientType.setValue(String.valueOf(KnConstants.UNKNOWN_CLIENT_TYPE));
                                        entryTypeObj.setClientType(clientType);
                                        ContactType contactType = new ContactType();
                                        contactType.setValue(String.valueOf(subsc.getContact_type()));
                                        entryTypeObj.setContactType(contactType);
                                    }
                                }
                                if (protocol > KnConstants.PROTOCOL_VERSION_8_X){
                                    if(subsc.getCallPermission() != null){
                                        CallPermission callPermission = new CallPermission();
                                        callPermission.setValue(subsc.getCallPermission());
                                        entryTypeObj.setCallPermission(callPermission);
                                    }
                                }

                                if (protocol >= PROTOCOL_VERSION_29) {
                                    if(subsc.getVideoCallPermission() != null){
                                        entryTypeObj.setVideoCallPermission(subsc.getVideoCallPermission());
                                        knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                    } else {
                                        knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                    }
                                }
                                if (protocol > KnConstants.PROTOCOL_VERSION_10_X) {
                                    if(subsc.getUa() != null) {
                                        UA ua = new UA();
                                        ua.setValue(subsc.getUa());
                                        entryTypeObj.setUa(ua);
                                    }
                                }
                                entryList.add(entryTypeObj);
                            }
                        }
                        diffReplaceObj.setEntryType(entryList);
                        replaceList.add(diffReplaceObj);
                        if (!replaceList.isEmpty()) {
                            docResourceType.setDiffReplaceType(replaceList);
                        }
                    }
                    docResourceType.setDocType(KnXcapNotifyConstants.DOC_TYPE.CORP_CONTACT_DOC.value());
                    docTypeList.add(docResourceType);
                    //UCSPROVCONFIG-10093
                } else if (xcapDiffDocObj.getDocChangeType() != KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()
                        && ((xcapDiffDocObj.getAddedGroupMembers() != null && !xcapDiffDocObj.getAddedGroupMembers().isEmpty())
                        || (xcapDiffDocObj.getRemovedGroupMembers() != null && !xcapDiffDocObj.getRemovedGroupMembers().isEmpty())
                        || (xcapDiffDocObj.getModifiedGrpMembers() != null && !xcapDiffDocObj.getModifiedGrpMembers().isEmpty())
                        || (xcapDiffDocObj.getGroupName() != null && !xcapDiffDocObj.getGroupName().isEmpty())
                        || (xcapDiffDocObj.getGroupMemCount() > 0)
                        || (xcapDiffDocObj.getAvatar() != null)
                        || (xcapDiffDocObj.getVideoPermission() != null))) {
                    if (xcapDiffDocObj.isOsmListChanged()) {
                        knLogger.debug(methodName, "OSM list changed :", xcapDiffDocObj.isOsmListChanged());
                        oldDocType.setSel(xcapDiffNotifyObj.getDirURI());
                        oldDocType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
                        oldDocType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
                        if(xcapDiffDocObj.getDocEtag() != null){
                            DiffReplaceChangeValue diffReplaceObj = new DiffReplaceChangeValue();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            diffReplaceObj.setChangeValue(xcapDiffDocObj.getDocEtag());
                            diffReplaceChangeList.add(diffReplaceObj);
                        } else {
                            DiffReplace diffReplaceObj = new DiffReplace();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            replaceList.add(diffReplaceObj);
                        }
                        if (!replaceList.isEmpty()) {
                            oldDocType.setDiffReplaceType(replaceList);
                        }
                        if (!diffReplaceChangeList.isEmpty()) {
                            oldDocType.setDiffReplaceChangeType(diffReplaceChangeList);
                        }
                        docTypeList.add(oldDocType);
                    } else {
                        if (xcapDiffDocObj.getAddedGroupMembers() != null && !xcapDiffDocObj.getAddedGroupMembers().isEmpty()) {
                            DiffAdd diffAddObj = new DiffAdd();
                            docGroupType.setSel(xcapDiffDocObj.getDocumentSelector());
                            docGroupType.setNewEtag(xcapDiffDocObj.getDocEtag());
                            docGroupType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                            ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                            Collection<KnSubscriberDTO> mdnList = xcapDiffDocObj.getAddedGroupMembers();
                            if (mdnList != null) {
                                for (KnSubscriberDTO subsc : mdnList) {
                                    EntryType entryTypeObj = new EntryType();
                                    entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subsc.getMdn()));
                                    entryTypeObj.setActiveFS1(subsc.getActiveFS2());
                                    if (subsc.getNetworkName() != null && !subsc.getNetworkName().isEmpty()) {
                                        DisplayName display = new DisplayName();
                                        display.setValue(subsc.getNetworkName());
                                        entryTypeObj.setDisplayNameEntry(display);
                                    }
                                    if (subsc.getSupervisory() != -1)
                                    {
                                        Supervisory supervisory = new Supervisory();
                                        supervisory.setValue(String.valueOf(subsc.getSupervisory()));
                                        entryTypeObj.setSupervisoryEntry(supervisory);
                                    }
                                    if (protocol >= KnConstants.PROTOCOL_VERSION_16_X) {
                                        entryTypeObj.setIsOSMAuthorized(String.valueOf(subsc.getIsOSMAuthorize()));
                                    }
                                    if (protocol > KnConstants.PROTOCOL_VERSION_9_X) {
                                        if (subsc.getLocWatcher() != -1)
                                        {
                                            LocWatcher locWatcher = new LocWatcher();
                                            locWatcher.setValue(String.valueOf(subsc.getLocWatcher()));
                                            entryTypeObj.setLocWatcherEntry(locWatcher);
                                        }
                                    }

                                    if (protocol > KnConstants.PROTOCOL_VERSION_7_X) {
                                        if(subsc.getClientType() != 0) {
                                            ClientType clientType = new ClientType();
                                            clientType.setValue(String.valueOf(subsc.getClientType()));
                                            entryTypeObj.setClientType(clientType);
                                            ContactType contactType = new ContactType();
                                            contactType.setValue(String.valueOf(subsc.getContact_type()));
                                            entryTypeObj.setContactType(contactType);
                                        }
                                        // Added because of SDD: SDD-POC-R8.3-Part5:INT-7867 XCAP interface Enhn: SDD_R8_3_P5_F_4
                                        if(subsc.getContact_type() != 0){
                                            ClientType clientType = new ClientType();
                                            clientType.setValue(String.valueOf(KnConstants.UNKNOWN_CLIENT_TYPE));
                                            entryTypeObj.setClientType(clientType);
                                            ContactType contactType = new ContactType();
                                            contactType.setValue(String.valueOf(subsc.getContact_type()));
                                            entryTypeObj.setContactType(contactType);
                                        }
                                    }
                                    if (protocol > KnConstants.PROTOCOL_VERSION_8_X){
                                        if(subsc.getCallPermission() != null){
                                            CallPermission callPermission = new CallPermission();
                                            callPermission.setValue(subsc.getCallPermission());
                                            entryTypeObj.setCallPermission(callPermission);
                                        }
                                    }

                                    if (protocol >= PROTOCOL_VERSION_29) {
                                        if(subsc.getVideoCallPermission() != null){
                                            entryTypeObj.setVideoCallPermission(subsc.getVideoCallPermission());
                                            knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                        } else {
                                            knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                        }
                                    }
                                    if (protocol > KnConstants.PROTOCOL_VERSION_10_X) {
                                        if(subsc.getUa() != null) {
                                            UA ua = new UA();
                                            ua.setValue(subsc.getUa());
                                            entryTypeObj.setUa(ua);
                                        }
                                    }
                                    entryList.add(entryTypeObj);
                                }
                            }
                            diffAddObj.setEntryType(entryList);
                            addList.add(diffAddObj);
                            if (!addList.isEmpty()) {
                                docGroupType.setDiffAddType(addList);
                            }
                        }
                        if (xcapDiffDocObj.getRemovedGroupMembers() != null && !xcapDiffDocObj.getRemovedGroupMembers().isEmpty()) {
                            DiffRemove diffRemoveObj = new DiffRemove();
                            docGroupType.setSel(xcapDiffDocObj.getDocumentSelector());
                            docGroupType.setNewEtag(xcapDiffDocObj.getDocEtag());
                            docGroupType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                            Collection<String> mdnList = xcapDiffDocObj.getRemovedGroupMembers();
                            ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                            if (mdnList != null) {
                                for (String mdn : mdnList) {
                                    EntryType entryTypeObj = new EntryType();
                                    entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(mdn));
                                    entryList.add(entryTypeObj);
                                }
                            }
                            diffRemoveObj.setEntryType(entryList);
                            removeList.add(diffRemoveObj);
                            if (!removeList.isEmpty()) {
                                docGroupType.setDiffRemoveType(removeList);
                            }
                        }

                        if (xcapDiffDocObj.getModifiedGrpMembers() != null && !xcapDiffDocObj.getModifiedGrpMembers().isEmpty()) {
                            DiffReplace diffReplaceObj = new DiffReplace();
                            docGroupType.setSel(xcapDiffDocObj.getDocumentSelector());
                            docGroupType.setNewEtag(xcapDiffDocObj.getDocEtag());
                            docGroupType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                            Collection<KnSubscriberDTO> members = xcapDiffDocObj.getModifiedGrpMembers();
                            ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                            if (members != null) {
                                for (KnSubscriberDTO subsc : members) {
                                    EntryType entryTypeObj = new EntryType();
                                    entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subsc.getMdn()));
                                    entryTypeObj.setActiveFS1(subsc.getActiveFS2());
                                    if (subsc.getNetworkName() != null && !subsc.getNetworkName().isEmpty()) {
                                        DisplayName display = new DisplayName();
                                        display.setValue(subsc.getNetworkName());
                                        entryTypeObj.setDisplayNameEntry(display);
                                    }
                                    if (subsc.getSupervisory() != -1)
                                    {
                                        Supervisory supervisory = new Supervisory();
                                        supervisory.setValue(String.valueOf(subsc.getSupervisory()));
                                        entryTypeObj.setSupervisoryEntry(supervisory);
                                    }
                                    if (protocol >= KnConstants.PROTOCOL_VERSION_16_X) {
                                        entryTypeObj.setIsOSMAuthorized(String.valueOf(subsc.getIsOSMAuthorize()));
                                    }
                                    if (protocol > KnConstants.PROTOCOL_VERSION_9_X) {
                                        if (subsc.getLocWatcher() != -1)
                                        {
                                            LocWatcher locWatcher = new LocWatcher();
                                            locWatcher.setValue(String.valueOf(subsc.getLocWatcher()));
                                            entryTypeObj.setLocWatcherEntry(locWatcher);
                                        }
                                    }

                                    if (protocol > KnConstants.PROTOCOL_VERSION_7_X) {
                                        if(subsc.getClientType() != 0) {
                                            ClientType clientType = new ClientType();
                                            clientType.setValue(String.valueOf(subsc.getClientType()));
                                            entryTypeObj.setClientType(clientType);
                                            ContactType contactType = new ContactType();
                                            contactType.setValue(String.valueOf(subsc.getContact_type()));
                                            entryTypeObj.setContactType(contactType);
                                        }
                                        // Added because of SDD: SDD-POC-R8.3-Part5:INT-7867 XCAP interface Enhn: SDD_R8_3_P5_F_4
                                        if(subsc.getContact_type() != 0){
                                            ClientType clientType = new ClientType();
                                            clientType.setValue(String.valueOf(KnConstants.UNKNOWN_CLIENT_TYPE));
                                            entryTypeObj.setClientType(clientType);
                                            ContactType contactType = new ContactType();
                                            contactType.setValue(String.valueOf(subsc.getContact_type()));
                                            entryTypeObj.setContactType(contactType);
                                        }
                                    }
                                    if (protocol > KnConstants.PROTOCOL_VERSION_8_X){
                                        if(subsc.getCallPermission() != null){
                                            CallPermission callPermission = new CallPermission();
                                            callPermission.setValue(subsc.getCallPermission());
                                            entryTypeObj.setCallPermission(callPermission);
                                        }
                                    }

                                    if (protocol >= PROTOCOL_VERSION_29) {
                                        if(subsc.getVideoCallPermission() != null){
                                            entryTypeObj.setVideoCallPermission(subsc.getVideoCallPermission());
                                            knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                        } else {
                                            knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", subsc.getVideoCallPermission());
                                        }
                                    }
                                    if (protocol > KnConstants.PROTOCOL_VERSION_10_X) {
                                        if(subsc.getUa() != null) {
                                            UA ua = new UA();
                                            ua.setValue(subsc.getUa());
                                            entryTypeObj.setUa(ua);
                                        }
                                    }
                                    entryList.add(entryTypeObj);
                                }
                            }
                            diffReplaceObj.setEntryType(entryList);
                            replaceList.add(diffReplaceObj);
                            if (!replaceList.isEmpty()) {
                                docGroupType.setDiffReplaceType(replaceList);
                            }
                        }
                        if (xcapDiffDocObj.getGroupName() != null && !xcapDiffDocObj.getGroupName().isEmpty()) {
                            docGroupType.setSel(xcapDiffDocObj.getDocumentSelector());
                            docGroupType.setNewEtag(xcapDiffDocObj.getDocEtag());
                            docGroupType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                            ArrayList<DiffReplace> replaceObj = docGroupType.getDiffReplaceType();
                            DiffReplace diffReplaceObj = new DiffReplace();
                            GroupName groupName = new GroupName();
                            groupName.setValue(xcapDiffDocObj.getGroupName().trim());
                            if (replaceObj == null) {
                                replaceObj = new ArrayList<DiffReplace>();
                                replaceList.add(diffReplaceObj);
                                if (!replaceList.isEmpty()) {
                                    docGroupType.setDiffReplaceType(replaceList);
                                }
                            } else {
                                diffReplaceObj = replaceObj.get(0);
                            }
                            diffReplaceObj.setGroupName(groupName);
                            if (protocol >= KnConstants.PROTOCOL_VERSION_24) {
                                diffReplaceObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                            }
                        }

                        if(xcapDiffDocObj.getGroupMemCount() > 0){
                            docGroupType.setSel(xcapDiffDocObj.getDocumentSelector());
                            docGroupType.setNewEtag(xcapDiffDocObj.getDocEtag());
                            docGroupType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                            ArrayList<DiffReplace> replaceObj = docGroupType.getDiffReplaceType();
                            DiffReplace diffReplaceObj = new DiffReplace();
                            MemeberCount memberCount = new MemeberCount();
                            memberCount.setValue(String.valueOf(xcapDiffDocObj.getGroupMemCount()));
                            if (replaceObj == null) {
                                //replaceObj = new ArrayList<DiffReplace>();
                                replaceList.add(diffReplaceObj);
                                if (!replaceList.isEmpty()) {
                                    docGroupType.setDiffReplaceType(replaceList);
                                }
                            } else {
                                diffReplaceObj = replaceObj.get(0);
                            }
                            diffReplaceObj.setMemberCount(memberCount);
                        }

                        if(xcapDiffDocObj.getAvatar()!=null) {
                            if (protocol >= KnConstants.PROTOCOL_VERSION_9_X) {
                                docGroupType.setSel(xcapDiffDocObj.getDocumentSelector());
                                docGroupType.setNewEtag(xcapDiffDocObj.getDocEtag());
                                docGroupType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                                ArrayList<DiffReplace> replaceObj = docGroupType.getDiffReplaceType();
                                DiffReplace diffReplaceObj = new DiffReplace();
                                Avatar avatar = new Avatar();
                                avatar.setValue(String.valueOf(xcapDiffDocObj.getAvatar()));
                                if (replaceObj == null) {
                                    replaceList.add(diffReplaceObj);
                                    if (!replaceList.isEmpty()) {
                                        docGroupType.setDiffReplaceType(replaceList);
                                    }
                                } else {
                                    diffReplaceObj = replaceObj.get(0);
                                }
                                diffReplaceObj.setAvatar(avatar);
                            }
                        }

                        if(xcapDiffDocObj.getVideoPermission() != null) {
                            if (protocol >= PROTOCOL_VERSION_29) {
                                docGroupType.setSel(xcapDiffDocObj.getDocumentSelector());
                                docGroupType.setNewEtag(xcapDiffDocObj.getDocEtag());
                                docGroupType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                                ArrayList<DiffReplace> replaceObj = docGroupType.getDiffReplaceType();
                                DiffReplace diffReplaceObj = new DiffReplace();
                                VideoCallPermission videoCallPermission = new VideoCallPermission();
                                videoCallPermission.setValue(String.valueOf(xcapDiffDocObj.getVideoPermission()));
                                if (replaceObj == null) {
                                    replaceList.add(diffReplaceObj);
                                    if (!replaceList.isEmpty()) {
                                        docGroupType.setDiffReplaceType(replaceList);
                                    }
                                } else {
                                    diffReplaceObj = replaceObj.get(0);
                                }
                                diffReplaceObj.setVideoCallPermission(videoCallPermission);
                                knLogger.debug(methodName, "REPLACE: Setting video-call-permission for group - uri:",
                                               xcapDiffDocObj.getDocUri(), ", videoPermission:", xcapDiffDocObj.getVideoPermission());
                            }
                        }
                    }
                    docGroupType.setDocType(KnXcapNotifyConstants.DOC_TYPE.CORP_GROUP_DOC.value());
                    docTypeList.add(docGroupType);
                } else if ((xcapDiffDocObj.getAddedTargetList() != null && !xcapDiffDocObj.getAddedTargetList().isEmpty())
                        || (xcapDiffDocObj.getModifiedTargetList() != null && !xcapDiffDocObj.getModifiedTargetList().isEmpty())
                        || (xcapDiffDocObj.getRemovedTargetList() != null && !xcapDiffDocObj.getRemovedTargetList().isEmpty())) {
                    prepareAuthorizationDiffNotication(docTypeList, addList, replaceList, removeList, xcapDiffDocObj, protocol);
                } else if((xcapDiffDocObj.getAddedDestList() != null && !xcapDiffDocObj.getAddedDestList().isEmpty())
                        || (xcapDiffDocObj.getModifiedDestList() != null && !xcapDiffDocObj.getModifiedDestList().isEmpty())
                        || (xcapDiffDocObj.getRemovedDestList() != null && !xcapDiffDocObj.getRemovedDestList().isEmpty())
                        || (xcapDiffDocObj.getAddedEmergAttributes() != null) || (xcapDiffDocObj.getModifiedEmergAttributes() != null)
                        || (xcapDiffDocObj.getRemovedEmergAttributes() != null)) {
                    prepareEmergencyDiffNotication(docTypeList, addList, replaceList, removeList, xcapDiffDocObj, protocol);
                } else if((xcapDiffDocObj.getAddedAddlTGList() != null && !xcapDiffDocObj.getAddedAddlTGList().isEmpty())
                        || (xcapDiffDocObj.getModifyAddlTGList() != null && !xcapDiffDocObj.getModifyAddlTGList().isEmpty())
                        || (xcapDiffDocObj.getRemovedAddlTGList() != null && !xcapDiffDocObj.getRemovedAddlTGList().isEmpty())) {
                    prepareAddlTalkGroupDiffNotication(docTypeList, addList, replaceList, removeList, xcapDiffDocObj, protocol);
                } else {
                    if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.ADD.value()) {
                        oldDocType.setSel(xcapDiffNotifyObj.getDirURI());
                        oldDocType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
                        oldDocType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
                        DiffAdd diffAddObj = new DiffAdd();
                        diffAddObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(xcapDiffDocObj.getDocUri());
                        entryTypeObj.setEtag(xcapDiffDocObj.getDocEtag());
                        if (protocol >= KnConstants.PROTOCOL_VERSION_18_X) {
                        entryTypeObj.setIsAbdgGroup(xcapDiffDocObj.getIsAbdgGroup());
                        }
                        if (protocol >= KnConstants.PROTOCOL_VERSION_19_X) {
                        	entryTypeObj.setIsMcxGroup(xcapDiffDocObj.getIsMcxGroup());

                        }
                        if (protocol >= KnConstants.PROTOCOL_VERSION_23) {
                            entryTypeObj.setExternalCorpGroup(xcapDiffDocObj.getExternalCorpGroup());
                        }
                        if (protocol >= KnConstants.PROTOCOL_VERSION_24) {
                            entryTypeObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                        }
                        entryList.add(entryTypeObj);
                        diffAddObj.setEntryType(entryList);
                        addList.add(diffAddObj);
                        if (!addList.isEmpty()) {
                            oldDocType.setDiffAddType(addList);
                        }
                        populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                    } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REPLACE.value()) {
                        oldDocType.setSel(xcapDiffNotifyObj.getDirURI());
                        oldDocType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
                        oldDocType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
                        if(xcapDiffDocObj.getDocEtag()!=null){
                            DiffReplaceChangeValue diffReplaceObj = new DiffReplaceChangeValue();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            diffReplaceObj.setChangeValue(xcapDiffDocObj.getDocEtag());
                            diffReplaceChangeList.add(diffReplaceObj);
                        }else {
                            DiffReplace diffReplaceObj = new DiffReplace();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            replaceList.add(diffReplaceObj);
                        }
                        if (!replaceList.isEmpty()) {
                            oldDocType.setDiffReplaceType(replaceList);
                        }
                        if (!diffReplaceChangeList.isEmpty()) {
                            oldDocType.setDiffReplaceChangeType(diffReplaceChangeList);
                        }
                        populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                    } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()) {
                        oldDocType.setSel(xcapDiffNotifyObj.getDirURI());
                        oldDocType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
                        oldDocType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
                        DiffRemove diffRemoveObj = new DiffRemove();
                        diffRemoveObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        if (protocol >= KnConstants.PROTOCOL_VERSION_23) {
                            diffRemoveObj.setExternalCorpGroup(xcapDiffDocObj.getExternalCorpGroup());
                        }
                        if (protocol >= KnConstants.PROTOCOL_VERSION_24) {
                            knLogger.debug(methodName," inside remove :",protocol);
                            diffRemoveObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                        }
                        diffRemoveObj.setWs(xcapDiffDocObj.getRemoveWs());
                        removeList.add(diffRemoveObj);
                        if (!removeList.isEmpty()) {
                            oldDocType.setDiffRemoveType(removeList);
                        }
                        populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                    }
                    oldDocType.setDocType(KnXcapNotifyConstants.DOC_TYPE.XCAP_DIRECTORY_DOCUMENT.value());
                    docTypeList.add(oldDocType);
                }
            }
        } else if (xcapDiffDocObsList != null) {
            DocumentType docType = new DocumentType();
            docType.setSel(xcapDiffNotifyObj.getDirURI());
            docTypeList.add(docType);
        }
        knLogger.debug(methodName," populateNewChangeLog-docTypeList: ",docTypeList);

		for (DocumentType documentType : docTypeList) {
			populateSystemProfileMdnsForDocChange(mdns, documentType.getSel());

		}
		if(!mdns.isEmpty())
		{
            knLogger.debug(methodName,"sending System profiles Mcs XCAP Notifies for MDNs",mdns);
			knMCSXCAPNotifier.sendMCSXCAPNotification(mdns);
		}
		knLogger.debug(methodName,"docTypeList: ",docTypeList);
        return docTypeList;
    }

    public boolean generateDirNotification(List<KnXcapDiffNotifyDTO> xcapDiffNotifyObjs, Map<String,
            KnXDMSubsProvDTO> mdnSubsInfoMap, Map<String,Set<String>> baseMdnMap) {
        final String methodName = "generateDirNotification(List<KnXcapDiffNotifyDTO>)";
        boolean notifyStatus = false;
        List<KnNotifyPayloadDTO> listOfNotifyPayLoads = generateNotifications(xcapDiffNotifyObjs);

        //send feature id as -1 (feature id will be taken from KnNotifyPayloadDTO)
        int featureId = -1;
        if (listOfNotifyPayLoads != null && !listOfNotifyPayLoads.isEmpty()) {
            notifyStatus = sendBulkNotifications(listOfNotifyPayLoads, featureId, mdnSubsInfoMap,null, baseMdnMap);
        } else {
            knLogger.error(methodName, "Failed to send the notifications. Payload is empty - ", listOfNotifyPayLoads);
        }
        return notifyStatus;
    }

    /**
     * function to generate the directory notification XML for the given input using JibX library.
     *
     * @param xcapDiffNotifyObjs
     * @return
     */
    public List<KnNotifyPayloadDTO> generateNotifications(List<KnXcapDiffNotifyDTO> xcapDiffNotifyObjs) {
        final String methodName = "generateNotifications(List<KnXcapDiffNotifyDTO>)";

        List<KnNotifyPayloadDTO> listOfNotifyPayload = new ArrayList<KnNotifyPayloadDTO>();
        for (KnXcapDiffNotifyDTO xcapDiffNotifyObj : xcapDiffNotifyObjs) {
            // Resolve protocol once per subscriber so version-gated fields are deterministic.
            int protocol = 0;
            String protocolVersion = xcapDiffNotifyObj.getProtocolVersion();
            if (protocolVersion != null && !protocolVersion.trim().isEmpty()) {
                try {
                    protocol = Integer.parseInt(protocolVersion.trim());
                } catch (NumberFormatException nfe) {
                    knLogger.warn(methodName, "Invalid protocolVersion. Falling back to protocol=0. mdn="
                            + xcapDiffNotifyObj.getMdn() + " protocolVersion=" + protocolVersion);
                }
            }

            knLogger.debug(methodName, FLOW_TAG + " STEP-GN1 Building XCAP notification. mdn="
                    + xcapDiffNotifyObj.getMdn() + " protocol=" + protocol);

            XcapDiffType xcapDiffTypeObj = new XcapDiffType();
            xcapDiffTypeObj.setXcapRoot(xcapDiffNotifyObj.getXcapRootUri());

            DocumentType docType = new DocumentType();
            docType.setSel(xcapDiffNotifyObj.getDirURI());
            docType.setPreviousEtag(xcapDiffNotifyObj.getDirPrevEtag());
            docType.setNewEtag(xcapDiffNotifyObj.getDirNewEtag());
            Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = xcapDiffNotifyObj.getDocDiffObj();
            LinkedHashSet<KnMcsxcapMdnDTO> mdns= new LinkedHashSet<KnMcsxcapMdnDTO>();
            // Loop through all the doc change DTO and construct the appropriate docType with add, replace and remove docs
            if (xcapDiffDocObsList != null) {
                ArrayList<DiffAdd> diffAddList = new ArrayList<DiffAdd>();
                ArrayList<DiffReplace> diffReplaceList = new ArrayList<DiffReplace>();
                ArrayList<DiffReplaceChangeValue> diffReplaceChangeList = new ArrayList<DiffReplaceChangeValue>();
                ArrayList<DiffRemove> diffRemoveList = new ArrayList<DiffRemove>();
                for (KnXcapDiffDocDTO xcapDiffDocObj : xcapDiffDocObsList) {
                    if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.ADD.value()) {
                        DiffAdd diffAddObj = new DiffAdd();
                        diffAddObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(xcapDiffDocObj.getDocUri());
                        entryTypeObj.setEtag(xcapDiffDocObj.getDocEtag());
                        // Set videoCallPermission
                        Integer videoPermission = xcapDiffDocObj.getVideoPermission();
                        if (videoPermission != null) {
                            entryTypeObj.setVideoCallPermission(String.valueOf(videoPermission));
                            knLogger.debug(methodName, "ADD: Setting video-call-permission - uri:", xcapDiffDocObj.getDocUri(), ", videoPermission:", videoPermission);
                        } else {
                            entryTypeObj.setVideoCallPermission(String.valueOf(KnConstants.VIDEO_PERMISSION_VALUE));
                            knLogger.debug(methodName, "ADD: videoPermission is NULL, setting default - uri:", xcapDiffDocObj.getDocUri(), ", default:", KnConstants.VIDEO_PERMISSION_VALUE);
                        }
                        entryList.add(entryTypeObj);
                        diffAddObj.setEntryType(entryList);
                        diffAddList.add(diffAddObj);
                        populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                    } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REPLACE.value()) {
                        if(xcapDiffDocObj.getDocEtag()!=null){
                            DiffReplaceChangeValue diffReplaceObj = new DiffReplaceChangeValue();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            diffReplaceObj.setChangeValue(xcapDiffDocObj.getDocEtag());
                            diffReplaceChangeList.add(diffReplaceObj);
                            populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                        }else {
                            DiffReplace diffReplaceObj = new DiffReplace();
                            diffReplaceObj.setSel(xcapDiffDocObj.getDocumentSelector());
                            diffReplaceList.add(diffReplaceObj);
                            populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                        }
                    } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()) {
                        DiffRemove diffRemoveObj = new DiffRemove();
                        diffRemoveObj.setSel(xcapDiffDocObj.getDocumentSelector());
                        if (protocol >= KnConstants.PROTOCOL_VERSION_23) {
                            diffRemoveObj.setExternalCorpGroup(xcapDiffDocObj.getExternalCorpGroup());
                        }
                        if (protocol >= KnConstants.PROTOCOL_VERSION_24) {
                            knLogger.debug(methodName, FLOW_TAG
                                    + " STEP-GN2 REMOVE change has pre-config-group metadata. protocol=" + protocol
                                    + " selector=" + xcapDiffDocObj.getDocumentSelector());
                            diffRemoveObj.setIsPreConfigGroup(xcapDiffDocObj.getIsPreConfigGroup());
                        }
                        diffRemoveObj.setWs(xcapDiffDocObj.getRemoveWs());
                        diffRemoveList.add(diffRemoveObj);
                        populateSystemProfileMdnsForDocChange(mdns,xcapDiffDocObj.getDocumentSelector());
                    } else {
                        knLogger.error(methodName, "Unknown Doc Change Type");
                        return null;
                    }
                }
                if (!diffAddList.isEmpty()) {
                    docType.setDiffAddType(diffAddList);
                }
                if (!diffReplaceList.isEmpty()) {
                    docType.setDiffReplaceType(diffReplaceList);
                }
                if (!diffRemoveList.isEmpty()) {
                    docType.setDiffRemoveType(diffRemoveList);
                }
                if (!diffReplaceChangeList.isEmpty()) {
                    docType.setDiffReplaceChangeType(diffReplaceChangeList);
                }
            }
            xcapDiffTypeObj.setDocumentType(docType);
            if(!mdns.isEmpty())
    		{
                knLogger.debug(methodName,"sending System profiles Mcs XCAP Notifies for MDNs",mdns);
    			knMCSXCAPNotifier.sendMCSXCAPNotification(mdns);
    		}

            // generating XML string from the JibX object type
            String notifyXML;
            try {
                notifyXML = KnJAXBParser.convertBeanToXml(xcapDiffTypeObj, XcapDiffType.class);
                    knLogger.debug(methodName, "Notification XML is:", notifyXML);

                // send the notification message to Subscription Proxy
                if (notifyXML != null) {
                    KnNotifyPayloadDTO notifyPayloadDTO = new KnNotifyPayloadDTO();
                    notifyPayloadDTO.setPayload(generateXcapDiffNotification(xcapDiffNotifyObj, notifyXML));
                    notifyPayloadDTO.setPttServerId(xcapDiffNotifyObj.getPresenceHome());
                    notifyPayloadDTO.setFeatureId(KnXcapNotifyConstants.DOC_CHANGE_EVENT_FEATURE_ID);
                    notifyPayloadDTO.setDocType(KnXcapNotifyConstants.DOC_TYPE.SUBS_CONFIG_DOC.value());
                    listOfNotifyPayload.add(notifyPayloadDTO);
                    knLogger.debug(methodName, FLOW_TAG + " STEP-GN3 Added DOC_CHANGE payload. mdn="
                            + xcapDiffNotifyObj.getMdn() + " pttServerId=" + xcapDiffNotifyObj.getPresenceHome());

                }
                // send deRegister notification if flag set
                if (xcapDiffNotifyObj.isDeRegisterNotify()) {
                    KnNotifyPayloadDTO notifyPayloadDTO = new KnNotifyPayloadDTO();
                    notifyPayloadDTO.setPayload(generateDeRegisterNotification(xcapDiffNotifyObj));
                    notifyPayloadDTO.setPttServerId(xcapDiffNotifyObj.getPocHome());
                    notifyPayloadDTO.setFeatureId(KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID);
                    notifyPayloadDTO.setDocType(KnXcapNotifyConstants.DOC_TYPE.SUBS_DEREGISTER_NOTIFY_DOC.value());
                    listOfNotifyPayload.add(notifyPayloadDTO);
                    knLogger.debug(methodName, FLOW_TAG + " STEP-GN4 Added DEREG payload. mdn="
                            + xcapDiffNotifyObj.getMdn() + " pttServerId=" + xcapDiffNotifyObj.getPocHome());
                }

                // send profile change notification if flag set
                if (xcapDiffNotifyObj.isProfileNotify()) {
                    KnNotifyPayloadDTO notifyPayloadDTO = new KnNotifyPayloadDTO();
                    notifyPayloadDTO.setPayload(generateProfileNotification(xcapDiffNotifyObj));
                    notifyPayloadDTO.setPttServerId(xcapDiffNotifyObj.getPocHome());
                    notifyPayloadDTO.setFeatureId(KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID);
                    notifyPayloadDTO.setDocType(KnXcapNotifyConstants.DOC_TYPE.SUBS_PROFILE_CHANGE_NOTIFY.value());
                    listOfNotifyPayload.add(notifyPayloadDTO);
                    knLogger.debug(methodName, FLOW_TAG + " STEP-GN5 Added PROFILE payload. mdn="
                            + xcapDiffNotifyObj.getMdn() + " pttServerId=" + xcapDiffNotifyObj.getPocHome());
                }
            } catch (KnJAXBProcessException ope) {
                knLogger.error(methodName, "Notification message construction failed:", ope);
            }

        }
        knLogger.info(methodName, " listOfNotifyPayload:", listOfNotifyPayload.size());

        return listOfNotifyPayload;
    }

    /**
     * function to send the xcap diff notification XML.
     *
     * @param xcapDiffNotifyObj
     * @param notifyXML
     * @return boolean
     */
    private byte[] generateXcapDiffNotification(KnXcapDiffNotifyDTO xcapDiffNotifyObj, String notifyXML) {
        final String methodName = "generateXcapDiffNotification";

        KnTLVDocDiffGenerator tlvDocDiffGenerator = new KnTLVDocDiffGenerator();
        KnRequestObject requestObj = new KnRequestObject();
        byte[] payLoad = null;
        int featureId = KnXcapNotifyConstants.DOC_CHANGE_EVENT_FEATURE_ID;

        try {
            requestObj.setFeatureID(featureId);
            requestObj.setHomeRTXId(homeRtxId);
            requestObj.setMessageID(KnConstants.MESSAGE_TYPE.XDM_DIFF_NOTIFY.value());
            //We need to set protocol Version to 1 ,  else decoding will fail
            requestObj.setProtocolVersion(1);
         // GG Check for registeredHome
            String registeredHome=getRegisterPOCHomeByMDN(getMDNFromURI(xcapDiffNotifyObj.getDirURI()));
            knLogger.debug(methodName, "registeredHome :", registeredHome);
            if(registeredHome!=null&& !registeredHome.isEmpty())
            {
            	xcapDiffNotifyObj.setPocHome(registeredHome);
            	xcapDiffNotifyObj.setPresenceHome(registeredHome);
            }
            KnPoCXcapDiffNotifyDTO pocXcapDiffNotifyDTO = new KnPoCXcapDiffNotifyDTO();
            pocXcapDiffNotifyDTO.setXcapURI(xcapDiffNotifyObj.getDirURI());
            pocXcapDiffNotifyDTO.setXcapDiff(notifyXML);
            if (xcapDiffNotifyObj.getProtocolVersion() != null && xcapDiffNotifyObj.getProtocolVersion().matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX)) {
                pocXcapDiffNotifyDTO.setReason(xcapDiffNotifyObj.getReason());
            }
            //INT82147 Identify type of Notify listed below, Directory change Notify  - 1, Doc-Diff  Notify - 2
            pocXcapDiffNotifyDTO.setTypeOfNotify(KnXcapNotifyConstants.DIR_CHANGE_NOTIFY_TYPE);
           /* if(xcapDiffNotifyObj.isSubscribeNotify()){
                pocXcapDiffNotifyDTO.setTypeOfNotify(KnXcapNotifyConstants.SUBSCRIBE_NOTIFY_TYPE);
            }*/
            pocXcapDiffNotifyDTO.setNtfyOnAnyMDN(xcapDiffNotifyObj.getNtfyOnAnyMDN());
            requestObj.setRequestData(pocXcapDiffNotifyDTO);

            knLogger.debug(methodName, "TLV Request Object - ", requestObj);
            payLoad = tlvDocDiffGenerator.generateTLV(requestObj);
        } catch (Exception e) {
            knLogger.error(methodName, "Sending directory Notification failed:", e);
            knLogger.error(methodName, e);
        }
        return payLoad;

    }

    /**
     * function to send the subscriber profile change notification XML.
     *
     * @param xcapDiffNotifyObj
     * @return boolean
     */
    private byte[] generateProfileNotification(KnXcapDiffNotifyDTO xcapDiffNotifyObj) {
        String methodName = "generateProfileNotification";
        KnTLVDocDiffGenerator tlvDocDiffGenerator = new KnTLVDocDiffGenerator();
        KnRequestObject requestObj = new KnRequestObject();
        String mdn = xcapDiffNotifyObj.getProfileNotifyDTO().getMdn();
        String oldClientType = xcapDiffNotifyObj.getProfileNotifyDTO().getClientTypeChange();
        String newClientType = xcapDiffNotifyObj.getProfileNotifyDTO().getNewClientTypeChange();
        String lastUpdateTime = xcapDiffNotifyObj.getProfileNotifyDTO().getLastProfileUpdateTimestamp();
        int action = xcapDiffNotifyObj.getProfileNotifyDTO().getAction();
        byte[] payLoad = null;
        if (action == 0) {
            action = KnConstants.MESSAGE_TYPE.XDM_DIFF_NOTIFY.value();
        }
        try {
        	// GG Check for registeredHome
            String registeredHome=getRegisterPOCHomeByMDN(xcapDiffNotifyObj.getProfileNotifyDTO().getMdn());
            knLogger.debug(methodName, "registeredHome :", registeredHome);
            if(registeredHome!=null&& !registeredHome.isEmpty())
            {
            	xcapDiffNotifyObj.getProfileNotifyDTO().setPocHome(registeredHome);
            	xcapDiffNotifyObj.getProfileNotifyDTO().setPresenceHome(registeredHome);

            }
            int featureId = KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID;
            requestObj.setUserMDN(xcapDiffNotifyObj.getProfileNotifyDTO().getMdn());
            requestObj.setFeatureID(featureId);
            requestObj.setHomeRTXId(homeRtxId);
            requestObj.setMessageID(action);
            requestObj.setRTXVersion(KnXcapNotifyConstants.RTX_VERSION);
            //We need to set protocol Version to 1 ,  else decoding will fail
            requestObj.setProtocolVersion(1);
            KnSubsProfileDTO subsProfileDTO = new KnSubsProfileDTO();
            subsProfileDTO.setMdn(xcapDiffNotifyObj.getProfileNotifyDTO().getMdn());
            subsProfileDTO.setPresenceHome(xcapDiffNotifyObj.getProfileNotifyDTO().getPresenceHome());
            subsProfileDTO.setPocServerHome(xcapDiffNotifyObj.getProfileNotifyDTO().getPocHome());

            if (xcapDiffNotifyObj.getProfileNotifyDTO().getClientTypeChange() != null) {
                subsProfileDTO.setClientTypeChange(oldClientType);
            }
            if (xcapDiffNotifyObj.getProfileNotifyDTO().getNewClientTypeChange() != null) {
                subsProfileDTO.setNewClientTypeChange(newClientType);
            }
            if (xcapDiffNotifyObj.getProfileNotifyDTO().getActiveFeatureSetChange() != null) {
                subsProfileDTO.setActiveFeatureSetChange(mdn);
            }
            if (xcapDiffNotifyObj.getProfileNotifyDTO().getSubscriberNameChange() != null) {
                subsProfileDTO.setSubscriberNameChange(mdn);
            }
            if (xcapDiffNotifyObj.getProfileNotifyDTO().getSubscriptionTypeChange() != null) {
                subsProfileDTO.setSubscriptionTypeChange(mdn);
            }
            if (xcapDiffNotifyObj.getProfileNotifyDTO().getCredentialChange() != null) {
                subsProfileDTO.setCredentialChange(mdn);
            }
            if (xcapDiffNotifyObj.getProfileNotifyDTO().getLastProfileUpdateTimestamp() != null) {
                subsProfileDTO.setLastProfileUpdateTimestamp(lastUpdateTime);
            }
            requestObj.setRequestData(subsProfileDTO);
            knLogger.debug(methodName, "TLV Request Object - ", requestObj);
            payLoad = tlvDocDiffGenerator.generateTLV(requestObj);
        } catch (Exception e) {
            knLogger.error(methodName, "Sending directory Notification failed:", e);
        }
        return payLoad;
    }

    /**
     * function to send the Deregister notification .
     *
     * @param xcapDiffNotifyObj
     * @return boolean
     */
    private byte[] generateDeRegisterNotification(KnXcapDiffNotifyDTO xcapDiffNotifyObj) {
        String methodName = "generateDeRegisterNotification";
        KnTLVDocDiffGenerator tlvDocDiffGenerator = new KnTLVDocDiffGenerator();
        KnRequestObject requestObj = new KnRequestObject();
        byte[] payLoad = null;

        int action = xcapDiffNotifyObj.getDeRegisterNotifyDTO().getAction();
        //0 is for not setting action (take default as xcap diff notify)
        if (action == 0) {
            action = KnConstants.MESSAGE_TYPE.MDN_CHANGE.value();
        }
        //if pv is not 2.0 (will be considered as 1.0)
        if (xcapDiffNotifyObj.getProtocolVersion() != null && !(xcapDiffNotifyObj.getProtocolVersion().matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX)) && action == KnConstants.MESSAGE_TYPE.MDN_CHANGE.value()) {
            knLogger.debug(methodName, "DeRegister Notify for pv1.0 - action :", action);
            action = KnConstants.MESSAGE_TYPE.USER_DELETE.value();
        }

        knLogger.debug(methodName, "DeRegister Notify - action :", action);
        try {
        	// GG Check for registeredHome
            String registeredHome=getRegisterPOCHomeByMDN(xcapDiffNotifyObj.getDeRegisterNotifyDTO().getMdn());
            knLogger.debug(methodName, "registeredHome :", registeredHome);
            if(registeredHome!=null&& !registeredHome.isEmpty())
            {
                          xcapDiffNotifyObj.getDeRegisterNotifyDTO().setPocHome(registeredHome);
                          xcapDiffNotifyObj.getDeRegisterNotifyDTO().setPresenceHome(registeredHome);

            }
            int featureId = KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID;
            requestObj.setUserMDN(xcapDiffNotifyObj.getDeRegisterNotifyDTO().getMdn());
            requestObj.setFeatureID(featureId);
            requestObj.setHomeRTXId(homeRtxId);
            requestObj.setMessageID(action);
            requestObj.setRTXVersion(KnXcapNotifyConstants.RTX_VERSION);
            //We need to set protocol Version to 1 ,  else decoding will fail
            requestObj.setProtocolVersion(1);
            KnPoCSubsDeRegisterDTO poCSubsDeRegisterDTO = new KnPoCSubsDeRegisterDTO();
            poCSubsDeRegisterDTO.setMdn(xcapDiffNotifyObj.getDeRegisterNotifyDTO().getMdn());
            poCSubsDeRegisterDTO.setPresenceHome(xcapDiffNotifyObj.getDeRegisterNotifyDTO().getPresenceHome());
            poCSubsDeRegisterDTO.setPocServerHome(xcapDiffNotifyObj.getDeRegisterNotifyDTO().getPocHome());
            poCSubsDeRegisterDTO.setNewMdn(xcapDiffNotifyObj.getDeRegisterNotifyDTO().getNewMdn());
            requestObj.setRequestData(poCSubsDeRegisterDTO);
            knLogger.debug(methodName, "TLV Request Object - ", requestObj);
            payLoad = tlvDocDiffGenerator.generateTLV(requestObj);
        } catch (Exception e) {
            knLogger.error(methodName, "generate DeRegister Notification failed:", e);
            knLogger.error(methodName, e);
        }

        return payLoad;

    }


    public byte[] toByteArray(Object obj) {
        String methodName = "toByteArray(Object)";
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            knLogger.error(methodName, "IOExcept occurred - " +
                    new KnException("", ex.getMessage(), ex));
        }
        return bytes;
    }

    private Map<String, String> getMaxNotificationDocSize(KnPersisterTxn persisterTxn) {
        String methodName = "getMaxNotificationDocSize(persisterTxn)";
        Map<String, String> maxDocSize = new HashMap<String, String>();
        try {
            maxDocSize = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Exception occured while retrieving MaxNotificationDocSize", e);
        }
        return maxDocSize;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static String getMDNFromURI(String xcapDirURI) {
    	String methodName="getMDNFromURI(String xcapDirURI)";
    	knLogger.info(methodName, "Entry",KnGDPRTemplate.mdnUriTemplate(xcapDirURI));
    	String mdn=null;
    	try
    	{
    		mdn=xcapDirURI.split("tel:\\+")[1].split("/")[0];
    	}catch(Exception e)
    	{
    		knLogger.error(methodName,e);
    	}
    	knLogger.debug(methodName, "Exit",KnGDPRTemplate.mdn(mdn));
       return mdn;
    }

    public static String  getRegisterPOCHomeByMDN(String mdn) {
    	String methodName="getRegisterPOCHomeByMDN(String mdn)";
    	knLogger.info(methodName, "Entry",KnGDPRTemplate.mdn(mdn));
    	String registeredHome=null;
    /*	try
    	{
    	if(mdn!=null)
    	{
    	KnGGClientMgr clientMgr= KnGGClientMgr.getInstance();
    	IgniteCache<?, ?> registeredPOCHomeCache=clientMgr.getIgniteCache(KnGGCacheConstants.GG_CACHE_NAME.SIPREGISTRATIONSTATUSCACHE);
    	if(registeredPOCHomeCache!=null)
    	{
    	String query = "SELECT REGISTEREDHOME FROM \"" + KnGGCacheConstants.GG_CACHE_NAME.SIPREGISTRATIONSTATUSCACHE.value() + "\".SIPREGISTRATIONSTATUS where mdn = ?";
    	knLogger.info(methodName, "query",query);
    	List<List<?>> res=registeredPOCHomeCache.query(new SqlFieldsQuery(query).setArgs(mdn)).getAll();
    	if(res!=null && !res.isEmpty() && res.get(0)!=null && !res.get(0).isEmpty())
    	registeredHome=(String) res.get(0).get(0);
    	}
    	}
    	}
    	catch(Exception ex)
    	{
    		knLogger.error(methodName,ex);
    	}*/
    	knLogger.info(methodName, "registeredHome id  ",registeredHome);
    	return registeredHome;
     }

    public boolean isUnUpgradedPOCSERVER(String pttServerId) {
        String methodName = "isUnUpgradedPOCSERVER(String pttServerId)";
        knLogger.debug(methodName, "-----Entry", pttServerId);
        boolean status = true;
        try {
            status = genInfoUtil.isUnUpgradedPOCSERVER(pttServerId, null);
        } catch (KnBOException e) {
            knLogger.error(methodName, e);
        }
        return status;
    }

    /**
     * function to prepare the xcap diff notification XML - Authorization.
     *
     * @param docTypeList
     * @param addList
     * @param replaceList
     * @param removeList
     * @param xcapDiffDocObj
     */
    private void prepareAuthorizationDiffNotication(List<DocumentType> docTypeList, ArrayList<DiffAdd> addList,
                                                    ArrayList<DiffReplace> replaceList, ArrayList<DiffRemove> removeList,
                                                    KnXcapDiffDocDTO xcapDiffDocObj, int protocol) {
        final String methodName = "prepareAuthorizationDiffNotication";
        knLogger.debug(methodName, "docTypeList -- ",docTypeList, "addList -- ", addList, "replaceList -- ",
                replaceList, "removeList -- ", removeList, "xcapDiffDocObj -- ", xcapDiffDocObj, "protocol -", protocol);
        if(protocol >= KnConstants.PROTOCOL_VERSION_13_X){
            DocumentType authDocType = new DocumentType();
            if (xcapDiffDocObj.getAddedTargetList() != null && !xcapDiffDocObj.getAddedTargetList().isEmpty()) {
                authDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                authDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                authDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                DiffAdd diffAddObj = new DiffAdd();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                Collection<KnTargetPermsInfoDTO> addedTargetList = xcapDiffDocObj.getAddedTargetList();
                if (addedTargetList != null) {
                    addedTargetList.forEach(subs -> {
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subs.getMdn()));
                        entryTypeObj.setFper(subs.getFper());
                        entryTypeObj.setFsts(subs.getFsts());
                        entryList.add(entryTypeObj);
                    });
                    diffAddObj.setEntryType(entryList);
                    addList.add(diffAddObj);
                    if (!addList.isEmpty()) {
                        authDocType.setDiffAddType(addList);
                    }
                }
            }
            if (xcapDiffDocObj.getModifiedTargetList() != null && !xcapDiffDocObj.getModifiedTargetList().isEmpty()) {
                authDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                authDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                authDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                DiffReplace diffReplaceObj = new DiffReplace();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                Collection<KnTargetPermsInfoDTO> modifiedTargetList = xcapDiffDocObj.getModifiedTargetList();
                if (modifiedTargetList != null) {
                    modifiedTargetList.forEach(subs -> {
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subs.getMdn()));
                        entryTypeObj.setFper(subs.getFper());
                        entryTypeObj.setFsts(subs.getFsts());
                        entryList.add(entryTypeObj);
                    });
                    diffReplaceObj.setEntryType(entryList);
                    replaceList.add(diffReplaceObj);
                    if (!replaceList.isEmpty()) {
                        authDocType.setDiffReplaceType(replaceList);
                    }
                }
            }
            if (xcapDiffDocObj.getRemovedTargetList() != null && !xcapDiffDocObj.getRemovedTargetList().isEmpty()) {
                authDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                authDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                authDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                DiffRemove diffRemoveObj = new DiffRemove();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                Collection<String> removedTargetList = xcapDiffDocObj.getRemovedTargetList();
                if (removedTargetList != null) {
                    removedTargetList.forEach(subs -> {
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subs));
                        entryList.add(entryTypeObj);
                    });
                    diffRemoveObj.setEntryType(entryList);
                    removeList.add(diffRemoveObj);
                    if (!removeList.isEmpty()) {
                        authDocType.setDiffRemoveType(removeList);
                    }
                }
            }
            authDocType.setDocType(KnXcapNotifyConstants.DOC_TYPE.AU_PERMISSION_DOC.value());
            docTypeList.add(authDocType);
        }
        knLogger.debug(methodName, "docTypeList Exit -- ",docTypeList);
    }

    /**
     * function to prepare the xcap diff notification XML - Emergency.
     *
     * @param docTypeList
     * @param addList
     * @param replaceList
     * @param removeList
     * @param xcapDiffDocObj
     */
    private void prepareEmergencyDiffNotication(List<DocumentType> docTypeList, ArrayList<DiffAdd> addList,
                                                ArrayList<DiffReplace> replaceList, ArrayList<DiffRemove> removeList,
                                                KnXcapDiffDocDTO xcapDiffDocObj, int protocol) {
        final String methodName = "prepareEmergencyDiffNotication";
        knLogger.debug(methodName, "docTypeList -- ", docTypeList, "addList -- ", addList, "replaceList -- ",
                replaceList, "removeList -- ", removeList, "xcapDiffDocObj -- ", xcapDiffDocObj, "protocol -", protocol);
        if (protocol >= KnConstants.PROTOCOL_VERSION_13_X) {
            DocumentType emergDocType = new DocumentType();
            boolean addedDiff = false;
            boolean modifiedDiff = false;
            boolean removedDiff = false;
            if (xcapDiffDocObj.getAddedDestList() != null && !xcapDiffDocObj.getAddedDestList().isEmpty()) {
                addedDiff = true;
                emergDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                emergDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                emergDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag() != null ? xcapDiffDocObj.getPrevDocEtag() : String.valueOf(1));
                DiffAdd diffAddObj = new DiffAdd();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                if (xcapDiffDocObj.getAddedEmergAttributes() != null) {
                    KnEmergencyAttributesDTO emergencyAttributesDTO = xcapDiffDocObj.getAddedEmergAttributes();
                    if (emergencyAttributesDTO.getEmergInitPerm() != null)
                        diffAddObj.setEmergInitPerm(String.valueOf(emergencyAttributesDTO.getEmergInitPerm()));
                    if (emergencyAttributesDTO.getEmergCancelPerm() != null)
                        diffAddObj.setEmergCancelPerm(String.valueOf(emergencyAttributesDTO.getEmergCancelPerm()));
                    if (emergencyAttributesDTO.getEmergCallType() != null)
                        diffAddObj.setEmergCallType(String.valueOf(emergencyAttributesDTO.getEmergCallType()));
                    if (emergencyAttributesDTO.getEmergOrigBitSet() != null)
                        diffAddObj.setEmergOrigBitSet(String.valueOf(emergencyAttributesDTO.getEmergOrigBitSet()));
                    if (emergencyAttributesDTO.getEmergDestType() != null)
                        diffAddObj.setEmergDestType(String.valueOf(emergencyAttributesDTO.getEmergDestType()));
                    if( emergencyAttributesDTO.getEmergConfigTimer() != null)
                        diffAddObj.setEmergConfigTimer(String.valueOf(emergencyAttributesDTO.getEmergConfigTimer()));

                }
                Collection<KnEmergencyInfoDTO> addedDestList = xcapDiffDocObj.getAddedDestList();
                if (addedDestList != null) {
                    addedDestList.forEach(subsDest -> {
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subsDest.getDestination()));
                        if (subsDest.getDestPriority() != null)
                            entryTypeObj.setPriority(String.valueOf(subsDest.getDestPriority()));
                        if (subsDest.getDestType() != null)
                            entryTypeObj.setType(String.valueOf(subsDest.getDestType()));
                        entryList.add(entryTypeObj);
                    });
                    diffAddObj.setEntryType(entryList);
                    addList.add(diffAddObj);
                    if (!addList.isEmpty()) {
                        emergDocType.setDiffAddType(addList);
                    }
                }
                knLogger.debug(methodName, "emergDocType11 Exit - ", emergDocType);
            }
            if (xcapDiffDocObj.getModifiedDestList() != null && !xcapDiffDocObj.getModifiedDestList().isEmpty()) {
                modifiedDiff = true;
                emergDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                emergDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                emergDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag() != null ? xcapDiffDocObj.getPrevDocEtag() : String.valueOf(1));
                DiffReplace diffReplaceObj = new DiffReplace();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                if (xcapDiffDocObj.getModifiedEmergAttributes() != null) {
                    KnEmergencyAttributesDTO emergencyAttributesDTO = xcapDiffDocObj.getModifiedEmergAttributes();
                    if (emergencyAttributesDTO.getEmergInitPerm() != null)
                        diffReplaceObj.setEmergInitPerm(String.valueOf(emergencyAttributesDTO.getEmergInitPerm()));
                    if (emergencyAttributesDTO.getEmergCancelPerm() != null)
                        diffReplaceObj.setEmergCancelPerm(String.valueOf(emergencyAttributesDTO.getEmergCancelPerm()));
                    if (emergencyAttributesDTO.getEmergCallType() != null)
                        diffReplaceObj.setEmergCallType(String.valueOf(emergencyAttributesDTO.getEmergCallType()));
                    if (emergencyAttributesDTO.getEmergOrigBitSet() != null)
                        diffReplaceObj.setEmergOrigBitSet(String.valueOf(emergencyAttributesDTO.getEmergOrigBitSet()));
                    if (emergencyAttributesDTO.getEmergDestType() != null)
                        diffReplaceObj.setEmergDestType(String.valueOf(emergencyAttributesDTO.getEmergDestType()));
                    if( emergencyAttributesDTO.getEmergConfigTimer() != null)
                        diffReplaceObj.setEmergConfigTimer(String.valueOf(emergencyAttributesDTO.getEmergConfigTimer()));
                }
                Collection<KnEmergencyInfoDTO> modifiedDestList = xcapDiffDocObj.getModifiedDestList();
                if (modifiedDestList != null) {
                    modifiedDestList.forEach(subsDest -> {
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subsDest.getDestination()));
                        if (subsDest.getDestPriority() != null)
                            entryTypeObj.setPriority(String.valueOf(subsDest.getDestPriority()));
                        if (subsDest.getDestType() != null)
                            entryTypeObj.setType(String.valueOf(subsDest.getDestType()));
                        entryList.add(entryTypeObj);
                    });
                    diffReplaceObj.setEntryType(entryList);
                    replaceList.add(diffReplaceObj);
                    if (!replaceList.isEmpty()) {
                        emergDocType.setDiffReplaceType(replaceList);
                    }
                }
                knLogger.debug(methodName, "emergDocType22 Exit - ", emergDocType);
            }
            if (xcapDiffDocObj.getRemovedDestList() != null && !xcapDiffDocObj.getRemovedDestList().isEmpty()) {
                removedDiff = true;
                emergDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                emergDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                emergDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag() != null ? xcapDiffDocObj.getPrevDocEtag() : String.valueOf(1));
                DiffRemove diffRemoveObj = new DiffRemove();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                if (xcapDiffDocObj.getRemovedEmergAttributes() != null) {
                    KnEmergencyAttributesDTO emergencyAttributesDTO = xcapDiffDocObj.getRemovedEmergAttributes();
                    if (emergencyAttributesDTO.getEmergCancelPerm() != null)
                        diffRemoveObj.setEmergCancelPerm(String.valueOf(emergencyAttributesDTO.getEmergCancelPerm()));
                    if (emergencyAttributesDTO.getEmergCallType() != null)
                        diffRemoveObj.setEmergCallType(String.valueOf(emergencyAttributesDTO.getEmergCallType()));
                    if (emergencyAttributesDTO.getEmergDestType() != null)
                        diffRemoveObj.setEmergDestType(String.valueOf(emergencyAttributesDTO.getEmergDestType()));
                    if( emergencyAttributesDTO.getEmergConfigTimer() != null)
                        diffRemoveObj.setEmergConfigTimer(String.valueOf(emergencyAttributesDTO.getEmergConfigTimer()));
                }
                Collection<String> removedDestList = xcapDiffDocObj.getRemovedDestList();
                if (removedDestList != null) {
                    removedDestList.forEach(subsDest -> {
                        EntryType entryTypeObj = new EntryType();
                        entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subsDest));
                        entryList.add(entryTypeObj);
                    });
                    diffRemoveObj.setEntryType(entryList);
                    removeList.add(diffRemoveObj);
                    if (!removeList.isEmpty()) {
                        emergDocType.setDiffRemoveType(removeList);
                    }
                }
                knLogger.debug(methodName, "emergDocType33 Exit - ", emergDocType);
            }
            if (!addedDiff && xcapDiffDocObj.getAddedEmergAttributes() != null) {
                emergDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                emergDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                emergDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag() != null ? xcapDiffDocObj.getPrevDocEtag() : String.valueOf(1));
                DiffAdd diffAddObj = new DiffAdd();
                KnEmergencyAttributesDTO emergencyAttributesDTO = xcapDiffDocObj.getAddedEmergAttributes();
                if (emergencyAttributesDTO.getEmergInitPerm() != null)
                    diffAddObj.setEmergInitPerm(String.valueOf(emergencyAttributesDTO.getEmergInitPerm()));
                if (emergencyAttributesDTO.getEmergCancelPerm() != null)
                    diffAddObj.setEmergCancelPerm(String.valueOf(emergencyAttributesDTO.getEmergCancelPerm()));
                if (emergencyAttributesDTO.getEmergCallType() != null)
                    diffAddObj.setEmergCallType(String.valueOf(emergencyAttributesDTO.getEmergCallType()));
                if (emergencyAttributesDTO.getEmergOrigBitSet() != null)
                    diffAddObj.setEmergOrigBitSet(String.valueOf(emergencyAttributesDTO.getEmergOrigBitSet()));
                if (emergencyAttributesDTO.getEmergDestType() != null)
                    diffAddObj.setEmergDestType(String.valueOf(emergencyAttributesDTO.getEmergDestType()));
                if(emergencyAttributesDTO.getEmergConfigTimer() != null) {
                    diffAddObj.setEmergConfigTimer(String.valueOf(emergencyAttributesDTO.getEmergConfigTimer()));
                }
                addList.add(diffAddObj);
                if (!addList.isEmpty()) {
                    emergDocType.setDiffAddType(addList);
                }
                knLogger.debug(methodName, "emergDocType44 Exit - ", emergDocType);
            }
            if (!modifiedDiff && xcapDiffDocObj.getModifiedEmergAttributes() != null) {
                emergDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                emergDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                emergDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag() != null ? xcapDiffDocObj.getPrevDocEtag() : String.valueOf(1));
                DiffReplace diffReplaceObj = new DiffReplace();

                KnEmergencyAttributesDTO emergencyAttributesDTO = xcapDiffDocObj.getModifiedEmergAttributes();
                if (emergencyAttributesDTO.getEmergInitPerm() != null)
                    diffReplaceObj.setEmergInitPerm(String.valueOf(emergencyAttributesDTO.getEmergInitPerm()));
                if (emergencyAttributesDTO.getEmergCancelPerm() != null)
                    diffReplaceObj.setEmergCancelPerm(String.valueOf(emergencyAttributesDTO.getEmergCancelPerm()));
                if (emergencyAttributesDTO.getEmergCallType() != null)
                    diffReplaceObj.setEmergCallType(String.valueOf(emergencyAttributesDTO.getEmergCallType()));
                if (emergencyAttributesDTO.getEmergOrigBitSet() != null)
                    diffReplaceObj.setEmergOrigBitSet(String.valueOf(emergencyAttributesDTO.getEmergOrigBitSet()));
                if (emergencyAttributesDTO.getEmergDestType() != null)
                    diffReplaceObj.setEmergDestType(String.valueOf(emergencyAttributesDTO.getEmergDestType()));
                if(emergencyAttributesDTO.getEmergConfigTimer() != null) {
                    diffReplaceObj.setEmergConfigTimer(String.valueOf(emergencyAttributesDTO.getEmergConfigTimer()));
                }

                replaceList.add(diffReplaceObj);
                if (!replaceList.isEmpty()) {
                    emergDocType.setDiffReplaceType(replaceList);
                }
                knLogger.debug(methodName, "emergDocType55 Exit - ", emergDocType);
            }
            if (!removedDiff && xcapDiffDocObj.getRemovedEmergAttributes() != null) {
                emergDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                emergDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                emergDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag() != null ? xcapDiffDocObj.getPrevDocEtag() : String.valueOf(1));
                DiffRemove diffRemoveObj = new DiffRemove();

                KnEmergencyAttributesDTO emergencyAttributesDTO = xcapDiffDocObj.getRemovedEmergAttributes();
                if (emergencyAttributesDTO.getEmergCancelPerm() != null)
                    diffRemoveObj.setEmergCancelPerm(String.valueOf(emergencyAttributesDTO.getEmergCancelPerm()));
                if (emergencyAttributesDTO.getEmergCallType() != null)
                    diffRemoveObj.setEmergCallType(String.valueOf(emergencyAttributesDTO.getEmergCallType()));
                if (emergencyAttributesDTO.getEmergDestType() != null)
                    diffRemoveObj.setEmergDestType(String.valueOf(emergencyAttributesDTO.getEmergDestType()));
                if(emergencyAttributesDTO.getEmergConfigTimer() != null) {
                    diffRemoveObj.setEmergConfigTimer(String.valueOf(emergencyAttributesDTO.getEmergConfigTimer()));
                }
                removeList.add(diffRemoveObj);
                if (!removeList.isEmpty()) {
                    emergDocType.setDiffRemoveType(removeList);
                }
                knLogger.debug(methodName, "emergDocType66 Exit - ", emergDocType);
            }
            emergDocType.setDocType(KnXcapNotifyConstants.DOC_TYPE.EMERGENCY_DOC.value());
            docTypeList.add(emergDocType);
        }
        knLogger.debug(methodName, "docTypeList Exit - ", docTypeList);
    }

    /**
     * function to prepare the xcap diff notification XML - AddlTGList.
     *
     * @param docTypeList
     * @param addList
     * @param replaceList
     * @param removeList
     * @param xcapDiffDocObj
     */
    private void prepareAddlTalkGroupDiffNotication(List<DocumentType> docTypeList, ArrayList<DiffAdd> addList,
                                                    ArrayList<DiffReplace> replaceList, ArrayList<DiffRemove> removeList,
                                                    KnXcapDiffDocDTO xcapDiffDocObj, int protocol) {
        final String methodName = "prepareAddlTalkGroupDiffNotication";
        knLogger.debug(methodName, "docTypeList -- ", docTypeList, "addList -- ", addList, "replaceList -- ",
                replaceList, "removeList -- ", removeList, "xcapDiffDocObj -- ", xcapDiffDocObj, "protocol -", protocol);
        if (protocol >= KnConstants.PROTOCOL_VERSION_13_X) {
            DocumentType addlTgDocType = new DocumentType();
            Collection<KnXDMAddlTalkGroupInfoDTO> addedList = xcapDiffDocObj.getAddedAddlTGList();
            Collection<KnXDMAddlTalkGroupInfoDTO> modifiedList = xcapDiffDocObj.getModifyAddlTGList();
            Collection<KnXDMAddlTalkGroupInfoDTO> removedList = xcapDiffDocObj.getRemovedAddlTGList();
            if (protocol < KnConstants.PROTOCOL_VERSION_16_X && addedList != null && !addedList.isEmpty()) {
                addlTgDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                addlTgDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                addlTgDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                DiffAdd diffAddObj = new DiffAdd();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                addedList.forEach(subs -> {
                    EntryType entryTypeObj = new EntryType();
                    UriUsage uriUsage = new UriUsage();
                    CommonUsage commonUsage = new CommonUsage();
                    entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subs.getGroupUri()));
                    if(subs.getZoneId() != null) commonUsage.setZoneId(String.valueOf(subs.getZoneId()));
                    commonUsage.setZoneName(subs.getZoneName());
                    if(subs.getChannelId() != null) commonUsage.setChannelId(String.valueOf(subs.getChannelId()));
                    if (protocol > KnConstants.PROTOCOL_VERSION_13_X) {
                        if (subs.getAvatar() != null) commonUsage.setAvatarId(String.valueOf(subs.getAvatar()));
                        if (subs.getMemberCount() != null)
                            commonUsage.setMemberCount(String.valueOf(subs.getMemberCount()));
                        if (subs.getGroupType() != null && subs.getGroupType() != NOT_MODIFIED)
                            commonUsage.setGroupType(String.valueOf(KnConstants.mappGroupTypeToXcap(subs.getGroupType())));
                        if (subs.getCreatedBy() != null && subs.getCreatedBy() != NOT_MODIFIED)
                            commonUsage.setGroupCreatedBy(subs.getCreatedBy() == KnConstants.GROUP_CREATED_BY.ABDG.value()
                                    ? String.valueOf(ENABLED) : String.valueOf(DISABLED));
                        if (subs.getVideoPermission() != null)
                            commonUsage.setVideoCallPermission(String.valueOf(subs.getVideoPermission()));
                    }
                    uriUsage.setCommonUsage(commonUsage);
                    entryTypeObj.setUriUsage(uriUsage);
                    entryList.add(entryTypeObj);
                });
                diffAddObj.setEntryType(entryList);
                addList.add(diffAddObj);
                if (!addList.isEmpty()) {
                    addlTgDocType.setDiffAddType(addList);
                }
            }
            if (modifiedList != null && !modifiedList.isEmpty()) {
                addlTgDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                addlTgDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                addlTgDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                DiffReplace diffReplaceObj = new DiffReplace();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                modifiedList.forEach(subs -> {
                    EntryType entryTypeObj = new EntryType();
                    UriUsage uriUsage = new UriUsage();
                    CommonUsage commonUsage = new CommonUsage();
                    entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subs.getGroupUri()));
                    commonUsage.setZoneId(subs.getZones());
                    commonUsage.setZoneName(subs.getZoneName());
                    commonUsage.setChannelId(subs.getChannels());
                    if (protocol > KnConstants.PROTOCOL_VERSION_13_X) {
                        if (subs.getAvatar() != null) commonUsage.setAvatarId(String.valueOf(subs.getAvatar()));
                        if (subs.getMemberCount() != null)
                            commonUsage.setMemberCount(String.valueOf(subs.getMemberCount()));
                        if (subs.getGroupType() != null && subs.getGroupType() != NOT_MODIFIED)
                            commonUsage.setGroupType(String.valueOf(KnConstants.mappGroupTypeToXcap(subs.getGroupType())));
                        if (subs.getCreatedBy() != null && subs.getCreatedBy() != NOT_MODIFIED)
                            commonUsage.setGroupCreatedBy(subs.getCreatedBy() == KnConstants.GROUP_CREATED_BY.ABDG.value()
                                    ? String.valueOf(ENABLED) : String.valueOf(DISABLED));
                        if (subs.getVideoPermission() != null)
                            commonUsage.setVideoCallPermission(String.valueOf(subs.getVideoPermission()));
                    }
                    uriUsage.setCommonUsage(commonUsage);
                    entryTypeObj.setUriUsage(uriUsage);
                    entryList.add(entryTypeObj);
                });
                diffReplaceObj.setEntryType(entryList);
                replaceList.add(diffReplaceObj);
                if (!replaceList.isEmpty()) {
                    addlTgDocType.setDiffReplaceType(replaceList);
                }
            }
            if (removedList != null && !removedList.isEmpty()) {
                addlTgDocType.setSel(xcapDiffDocObj.getDocumentSelector());
                addlTgDocType.setNewEtag(xcapDiffDocObj.getDocEtag());
                addlTgDocType.setPreviousEtag(xcapDiffDocObj.getPrevDocEtag());
                DiffRemove diffRemoveObj = new DiffRemove();
                ArrayList<EntryType> entryList = new ArrayList<EntryType>();
                removedList.forEach(subs -> {
                    EntryType entryTypeObj = new EntryType();
                    entryTypeObj.setUri(KnXcapNotifyConstants.TELURI.concat(subs.getGroupUri()));
                    entryList.add(entryTypeObj);
                });
                diffRemoveObj.setEntryType(entryList);
                removeList.add(diffRemoveObj);
                if (!removeList.isEmpty()) {
                    addlTgDocType.setDiffRemoveType(removeList);
                }
            }
            addlTgDocType.setDocType(KnXcapNotifyConstants.DOC_TYPE.TGSC_LIST_DOC.value());
            docTypeList.add(addlTgDocType);
        }
        knLogger.debug(methodName, "docTypeList Exit - ", docTypeList);
    }

    public boolean generateDeleteDeviceNotification(KnDeleteDeviceDTO deleteDeviceDTO) {
        String methodName = "generateDeleteDeviceNotification";
        KnTLVDocDiffGenerator tlvDocDiffGenerator = new KnTLVDocDiffGenerator();
        KnRequestObject requestObj = new KnRequestObject();
       boolean notifyStatus=false;
        try {
            knLogger.debug(methodName,"Entry deleteDeviceDTO :",deleteDeviceDTO);
        	KnDeleteDeviceTlvDTO deleteDeviceTlvDTO= new KnDeleteDeviceTlvDTO();
            if(null != deleteDeviceDTO.getDeviceIMPIList() && !deleteDeviceDTO.getDeviceIMPIList().isEmpty()){
                deleteDeviceTlvDTO.setDeviceIMPIList(deleteDeviceDTO.getDeviceIMPIList());
            } else{
                deleteDeviceTlvDTO.setDeviceIMPI(deleteDeviceDTO.getDeviceIMPI());
            }
            int featureId = KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID;
            requestObj.setFeatureID(featureId);
            requestObj.setMessageID(KnConstants.MESSAGE_TYPE.DEVICE_DELETE.value());
            //We need to set protocol Version to 1 ,  else decoding will fail
            requestObj.setProtocolVersion(1);
            requestObj.setRequestData(deleteDeviceTlvDTO);
            knLogger.debug(methodName, "TLV Request Object - ", requestObj);
            byte[] payLoad = tlvDocDiffGenerator.generateTLV(requestObj);
            notifyStatus = sendNotification(payLoad, featureId, deleteDeviceDTO.getPocHome());
        } catch (Exception e) {
        	notifyStatus = false;
            knLogger.error(methodName, "Sending directory Notification failed:", e);
        }
        return notifyStatus;
    }


    public String constructKpnsMessage(Map<String, Object> headers, Object messageobj, String type, KnPersisterTxn persisterTxn2,
                                       Map<String, String> paramNameValueMapCommon, boolean isHybridIosBitEnabled,
                                       KnNtfnHdrDest hdrDest, Map<String,Set<String>> baseMdnsMap) {
        String updatePayload = null;
        String methodName = "constructKpnsMessage";
        try {
            KnKpnsNotification knKpnsNotification = new KnKpnsNotification();
            KnNtfnHdr hdr = new KnNtfnHdr();
            List<KnNtfnHdrDest> destList = new ArrayList<>();
            KnNtfnApns knNtfnApns = new KnNtfnApns();
            knKpnsNotification.setType(type);

            long currentTime = Calendar.getInstance().getTimeInMillis() / 1000L;
            Long apnsExpiryTime = 24 * 60 * 60L;//after 24 hrs from the current time
            long expiryTime = currentTime + apnsExpiryTime;

            List<String> mdns1 = new ArrayList<String>();
            mdns1.add(headers.get("to").toString());
            String realMdn = headers.get("to").toString();

            List<String> listMdn = new ArrayList<>(baseMdnsMap.getOrDefault(headers.get("to").toString(), new HashSet<>()));
            knLogger.info(methodName, "listMdn from baseMdnsMap", listMdn);
            if (listMdn == null || listMdn.isEmpty()) {
                knLogger.info(methodName, "No base mdn found for mdn in cache ", headers.get("to").toString());
                listMdn = genInfoUtil.getBaseMdn(mdns1, persisterTxn2);
                knLogger.info(methodName, "listMdn from db", listMdn);
            }
            if (!listMdn.isEmpty()) {
                realMdn = listMdn.get(0);
            }
            KnIOSXdmWakeUpNtfnPayload wakeUpNtfnPayload = new KnIOSXdmWakeUpNtfnPayload();
            wakeUpNtfnPayload.setDest_mdn(realMdn);
            KnIOSWakeup wakeUp = new KnIOSWakeup();
            wakeUp.setTr("dir-change-notify");
            wakeUp.setNtfPayLoad(messageobj);

            wakeUpNtfnPayload.setWakeup(wakeUp);
            ObjectMapper objmapper = new ObjectMapper();
            String notify = null;
            try {
                ObjectMapper ow = new ObjectMapper();
                notify = ow.writeValueAsString(wakeUpNtfnPayload);
                knLogger.info(methodName, "Push Notify Message", notify);
            } catch (Exception e) {
                knLogger.error(methodName, "Error while creating ios push notifcation message");
            }

            knLogger.info("payload before setting to notification ############# ", notify);
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject jsonObject = (JSONObject) parser.parse(notify);

            String apnsTopic = paramNameValueMapCommon.get("APNS_TOPIC");
            String notificationFlag = paramNameValueMapCommon.get("KPNS_NOTIFYENCFLAG");
            String routingKey = paramNameValueMapCommon.get("NM_ROUTING_KEY");

            hdr.setOrigRoutingKey(routingKey);
            hdr.setSrc(type);
            hdr.setTs(System.currentTimeMillis());
            hdr.setType(type);
            hdrDest.setMdn(realMdn);
            hdrDest.setPn_channel_type("apns");
            if (notificationFlag.equals("1") && isHybridIosBitEnabled) {
                hdrDest.setEncrypt_payload("PBKDF2_aes-256-cbc");
            } else {
                hdrDest.setEncrypt_payload("no_encrypt");
            }
            if (isHybridIosBitEnabled) {
                headers.put(IOS_NM_APNS_EXPIRATION, expiryTime);
                headers.put(MESSAGETYPE, "wakeup");
                knNtfnApns.setApns_expiration(expiryTime);
                knKpnsNotification.setNotification_payload(jsonObject);
            } else {
                knNtfnApns.setApns_expiration(12L);
                headers.put(MESSAGETYPE, "XDMIOS");
                knKpnsNotification.setVer("2.0");
                knKpnsNotification.setPayload(jsonObject);
            }

            hdrDest.setReq_id(System.currentTimeMillis() + "_" + hdrDest.getMdn());
            destList.add(hdrDest);
            hdr.setDest(destList);
            knKpnsNotification.setHdr(hdr);
            knNtfnApns.setApns_priority(9);
            knNtfnApns.setContent_available(true);
            knNtfnApns.setApns_topic(apnsTopic);
            //knNtfnApns.setApns_topic(apnsTopic);
            knKpnsNotification.setApnsParams(knNtfnApns);
            knLogger.info(methodName, "knKpnsNotification : ", knKpnsNotification);
            updatePayload = objmapper.writeValueAsString(knKpnsNotification).replaceAll("\\\\", "");
        } catch (Throwable e) {
            knLogger.error(methodName,"Exception", e);
        }
        knLogger.info(methodName, "updatePayload -", updatePayload);
        return updatePayload;
    }

    private boolean isHybridIosBitEnabled(String activeFs2) {
        String methodName = "isHybridIosBitEnabled(activeFs2)";
        knLogger.debug(methodName, "ACTIVE -fs 2", activeFs2);
        boolean isHybridIosBitEnabled = false;
        try {
            isHybridIosBitEnabled = KnGeneralUtil.getFeatureBitValue(activeFs2,
                    com.kodiak.xdms.server.common.resources.KnConstants.HYBRID_IOS_BIT);
        } catch (Exception e) {
            knLogger.error("exception at ios check", e);
        }
        knLogger.info(methodName, " isHybridIosBitEnabled:", isHybridIosBitEnabled);
        return isHybridIosBitEnabled;
    }
    /**
     * save suppressed mdns as payload in db
     *
     * @param mcsNotifyDTOs List<KnMCSNotifyDTO>
     * @param notificationParamDTO KnNotificationParamDTO
     * @return void
     */
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
                if(persisterTxn == null) {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    ownedTxn = true;
                }
                String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                connection = persisterTxn.getDBConnection(localPttId, false);
                pStmt = connection.prepareStatement(INSERT_SUPPRESSED_NOTIFY);
                long startTime = System.currentTimeMillis();
                for (KnMCSNotifyDTO mcsNotifyDTO : mcsNotifyDTOs) {
                    Set<String> suppressMdns = mcsNotifyDTO.getSuppressMdn();
                    Instant instant = Instant.now();
                    long timeInNanoSecond = instant.getNano() + instant.getEpochSecond() * 1000000000L;
                    pStmt.setLong(1, timeInNanoSecond);
                    pStmt.setString(2, "000000000");
                    pStmt.setInt(3, KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
                    byte[] data = KnGeneralUtil.toByteArray(mcsNotifyDTO);
                    knLogger.debug(methodName, "data length", data.length);
                    pStmt.setBytes(4, data);
                    knLogger.debug(methodName, "payload set",data);
                    pStmt.setInt(5, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                    pStmt.setInt(6, KnXcapNotifyConstants.PAYLOADVERSION.ONE.value());
                    pStmt.setString(7, notificationParamDTO.getCid());
                    pStmt.setInt(8, notificationParamDTO.getOpsCode());
                    pStmt.setInt(9, notificationParamDTO.getPriority());
                    pStmt.setInt(10, notificationParamDTO.getRetryCount());
                    pStmt.setInt(11, notificationParamDTO.getNotifyType());
                    pStmt.setInt(12, notificationParamDTO.getProtocol());
                    pStmt.setInt(13, notificationParamDTO.getMsgType());
                    pStmt.setInt(14, suppressMdns.size());
                    pStmt.addBatch();
                    count++;
                    if (count % KnConstants.BATCH_SIZE == 0) {
                        knLogger.debug(methodName, "Executing batch of 100");
                        pStmt.executeBatch();
                    }
                }
                knLogger.debug(methodName, "Executing remaining");
                pStmt.executeBatch();
                if(ownedTxn) {
                    persisterTxn.save();
                }
                long endTime = System.currentTimeMillis();
                knLogger.debug(methodName, "time testing notification : ", endTime - startTime);
                break;
            } catch (KnPersistenceException e) {
                if(ownedTxn) {
                    rollback(persisterTxn);
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
                if(ownedTxn) {
                    rollback(persisterTxn);
                }
                knLogger.error(methodName, "Unexpected Exception occurred - ", e);
                break;
            } finally {
                KnDbUtil.closeStatement(pStmt);
            }
        }
        knLogger.exit(methodName, "save the list of mdn as payload into db ends- ");
    }

    void saveNotification(KnPersisterTxn persisterTxn, List<KnXcapDiffDirChgNotifyDTO> knXcapDiffDirChgNotifyDTOs, KnNotificationParamDTO notificationParamDTO) {
        String methodName = "saveNotification(KnPersisterTxn, List<KnXcapDiffDirChgNotifyDTO>, KnNotificationParamDTO)";
        knLogger.info(methodName, FLOW_TAG + " STEP-SN1 Enter saveNotification(DirChg). inputCount="
                + (knXcapDiffDirChgNotifyDTOs != null ? knXcapDiffDirChgNotifyDTOs.size() : 0)
                + " cid=" + (notificationParamDTO != null ? notificationParamDTO.getCid() : null)
                + " msgType=" + (notificationParamDTO != null ? notificationParamDTO.getMsgType() : null));
        knLogger.entry(methodName);
        int batchSize = 100;
        int count = 0;
        int skippedLargePayload = 0;
        Connection connection = null;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        int MAX_RETRY = 2;
        for(int attempt = 1; attempt <= MAX_RETRY ; attempt++) {
            try {
                if(persisterTxn == null) {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    ownedTxn = true;
                }
                String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                connection = persisterTxn.getDBConnection(localPttId, false);
                pStmt = connection.prepareStatement(NOTIFICATION_INSERT_QUERY);
                long start = System.currentTimeMillis();
                for (KnXcapDiffDirChgNotifyDTO knXcapDiffDirChgNotifyDTO : knXcapDiffDirChgNotifyDTOs) {
                    Instant instant = Instant.now();
                    long timeInNanoSecond = instant.getNano() + instant.getEpochSecond() * 1000000000L;
                    pStmt.setLong(1, timeInNanoSecond);
                    pStmt.setString(2, getMDNFromURI(knXcapDiffDirChgNotifyDTO.getDirURI()));
                    pStmt.setInt(3, KnXcapNotifyConstants.DESTTYPE.MDN.value());
                    byte[] data = KnGeneralUtil.toByteArray(knXcapDiffDirChgNotifyDTO);
                    knLogger.debug(methodName, "data length", data.length);
                    if (data.length > 10000) {
                        skippedLargePayload++;
                        knLogger.info(methodName, FLOW_TAG + " STEP-SN2 Skipping oversized DirChg payload. mdn="
                                + getMDNFromURI(knXcapDiffDirChgNotifyDTO.getDirURI()) + " payloadLength=" + data.length);
                        continue;
                    }
                    pStmt.setBytes(4, data);
                    pStmt.setInt(5, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                    pStmt.setInt(6, KnXcapNotifyConstants.PAYLOADVERSION.ONE.value());
                    pStmt.setString(7,notificationParamDTO.getCid());
                    pStmt.setInt(8,notificationParamDTO.getOpsCode());
                    pStmt.setInt(9,notificationParamDTO.getPriority());
                    pStmt.setInt(10,notificationParamDTO.getRetryCount());
                    pStmt.setInt(11,notificationParamDTO.getNotifyType());
                    pStmt.setInt(12,notificationParamDTO.getProtocol());
                    pStmt.setInt(13,notificationParamDTO.getMsgType());
                    pStmt.addBatch();
                    count++;
                    if (count % batchSize == 0) {
                        knLogger.debug(methodName, "Executing batch of 100");
                        pStmt.executeBatch();
                    }
                }
                pStmt.executeBatch();
                if(ownedTxn) {
                    persisterTxn.save();
                }
                long end = System.currentTimeMillis();
                knLogger.debug(methodName, "time testing notification : ", end - start);
                knLogger.info(methodName, FLOW_TAG + " STEP-SN3 Completed saveNotification(DirChg). insertedCount="
                        + count + " skippedLargePayload=" + skippedLargePayload + " elapsedMs=" + (end - start));
                LinkedHashSet<String> snapshotMdns = knXcapDiffDirChgNotifyDTOs.stream()
                        .map(KnXcapDiffDirChgNotifyDTO::getDirURI)
                        .map(KnXcapDiffNotifier::getMDNFromURI)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                logQueueSnapshotForMdns("STEP-SN3A", snapshotMdns, persisterTxn);
                break;
            } catch (KnPersistenceException e) {
                if(ownedTxn) {
                    rollback(persisterTxn);
                }
                knLogger.error(methodName, "Persister Txn occurred - ", e);
                if (attempt < MAX_RETRY) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        knLogger.error(methodName, "InterruptedException ",ex.getMessage());
                    }
                } else {
                    knLogger.debug(methodName, "Max retry completed");
                }
            } catch (Exception e) {
                if(ownedTxn) {
                    rollback(persisterTxn);
                }
                knLogger.error(methodName, "Unexpected Exception occurred - ", e);
                break;
            } finally {
                KnDbUtil.closeStatement(pStmt);
            }
        }
    }

    void saveNotification(List<KnXcapDiffNotifyDTO> knXcapDiffNotifyDTOs, KnNotificationParamDTO notificationParamDTO,KnPersisterTxn persisterTxn) {
        String methodName = "saveNotification(List<KnXcapDiffNotifyDTO>, notificationParamDTO)";
        knLogger.info(methodName, FLOW_TAG + " STEP-SN1 Enter saveNotification(DiffList). inputCount="
                + (knXcapDiffNotifyDTOs != null ? knXcapDiffNotifyDTOs.size() : 0)
                + " cid=" + (notificationParamDTO != null ? notificationParamDTO.getCid() : null)
                + " msgType=" + (notificationParamDTO != null ? notificationParamDTO.getMsgType() : null));
        knLogger.entry(methodName);
        PreparedStatement pStmt = null;
        int batchSize = 100;
        String query = null;
        boolean ownedTxn = false;
        int count = 0;
        int skippedLargePayload = 0;
        Connection connection = null;
        int MAX_RETRY = 2;
        for(int attempt = 1; attempt <= MAX_RETRY ; attempt++) {
            try {
                if(persisterTxn == null) {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    ownedTxn = true;
                }
                String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                connection = persisterTxn.getDBConnection(localPttId, false);
                pStmt = connection.prepareStatement(NOTIFICATION_INSERT_QUERY);
                long start = System.currentTimeMillis();
                for (KnXcapDiffNotifyDTO knXcapDiffNotifyDTO : knXcapDiffNotifyDTOs) {
                    Instant instant = Instant.now();
                    long timeInNanoSecond = instant.getNano() + instant.getEpochSecond() * 1000000000L;
                    pStmt.setLong(1, timeInNanoSecond);
                    pStmt.setString(2, getMDNFromURI(knXcapDiffNotifyDTO.getDirURI()));
                    pStmt.setInt(3, KnXcapNotifyConstants.DESTTYPE.MDN.value());
                    byte[] data = KnGeneralUtil.toByteArray(knXcapDiffNotifyDTO);
                    knLogger.debug(methodName, "data length", data.length);
                    if (data.length > 10000) {
                        skippedLargePayload++;
                        knLogger.info(methodName, FLOW_TAG + " STEP-SN2 Skipping oversized Diff payload. mdn="
                                + getMDNFromURI(knXcapDiffNotifyDTO.getDirURI()) + " payloadLength=" + data.length);
                        continue;
                    }
                    pStmt.setBytes(4, data);
                    pStmt.setInt(5, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                    pStmt.setInt(6, KnXcapNotifyConstants.PAYLOADVERSION.ONE.value());
                    pStmt.setString(7,notificationParamDTO.getCid());
                    pStmt.setInt(8,notificationParamDTO.getOpsCode());
                    pStmt.setInt(9,notificationParamDTO.getPriority());
                    pStmt.setInt(10,notificationParamDTO.getRetryCount());
                    pStmt.setInt(11,notificationParamDTO.getNotifyType());
                    pStmt.setInt(12,notificationParamDTO.getProtocol());
                    pStmt.setInt(13,notificationParamDTO.getMsgType());
                    pStmt.addBatch();
                    count++;
                    if (count % batchSize == 0) {
                        knLogger.debug(methodName, "Executing batch of 100");
                        pStmt.executeBatch();
                    }
                }
                pStmt.executeBatch();
                if(ownedTxn) {
                    persisterTxn.save();
                }
                long end = System.currentTimeMillis();
                knLogger.debug(methodName, "time testing notification : ", end - start);
                knLogger.info(methodName, FLOW_TAG + " STEP-SN3 Completed saveNotification(DiffList). insertedCount="
                        + count + " skippedLargePayload=" + skippedLargePayload + " elapsedMs=" + (end - start));
                LinkedHashSet<String> snapshotMdns = knXcapDiffNotifyDTOs.stream()
                        .map(KnXcapDiffNotifyDTO::getDirURI)
                        .map(KnXcapDiffNotifier::getMDNFromURI)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                logQueueSnapshotForMdns("STEP-SN3A", snapshotMdns, persisterTxn);
                break;
            } catch (KnPersistenceException e) {
                if(ownedTxn) {
                    rollback(persisterTxn);
                }
                knLogger.error(methodName, "Persister Txn occurred - ", e);
                if (attempt < MAX_RETRY) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        knLogger.error(methodName, "InterruptedException ",ex.getMessage());
                    }
                } else {
                    knLogger.debug(methodName, "Max retry completed");
                }
            } catch (Exception e) {
                if(ownedTxn) {
                    rollback(persisterTxn);
                }
                knLogger.error(methodName, "Unexpected Exception occurred - ", e);
                break;
            } finally {
                KnDbUtil.closeStatement(pStmt);
            }
        }
    }

    public void cleanUpRecord(KnNotificationKeyDTO seqId) {
        String methodName = "process()";
        knLogger.debug(methodName);
        String query = null;
        KnPersisterTxn persisterTxn = null;
        Connection connection = null;
        PreparedStatement pStmt = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, false);
            query = "DELETE FROM DG.XCAP_PENDING_NOTIFYQ WHERE INSERTION_TIME = ? AND DEST_ID=? AND DEST_TYPE=?";
            pStmt = connection.prepareStatement(query);
            pStmt.setLong(1, seqId.getInsertionTime());
            pStmt.setString(2, seqId.getDestId());
            pStmt.setInt(3, seqId.getDestType());
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, " record cleaned ", count);
            persisterTxn.save();
            knLogger.exit(methodName);
        } catch (KnPersistenceException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public void cleanUpRecord(List<KnNotificationKeyDTO> seqIds) {
        String methodName = "process()";
        knLogger.info(methodName, "seqIds size- ", seqIds.size());
        knLogger.debug(methodName);
        String query = null;
        KnPersisterTxn persisterTxn = null;
        Connection connection = null;
        PreparedStatement pStmt = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, false);
            query = "DELETE FROM DG.XCAP_PENDING_NOTIFYQ WHERE INSERTION_TIME = ? AND DEST_ID=? AND DEST_TYPE=?";
            pStmt = connection.prepareStatement(query);
            for(KnNotificationKeyDTO seqId : seqIds) {
                pStmt.setLong(1, seqId.getInsertionTime());
                pStmt.setString(2, seqId.getDestId());
                pStmt.setInt(3, seqId.getDestType());
                pStmt.addBatch();
            }
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "All record cleaned ", count);
            persisterTxn.save();
            knLogger.exit(methodName);
        } catch (KnPersistenceException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Reverts claimed queue rows from {@code NOTIFY_INITIATED} back to {@code PENDING}
     * so a failed worker send can be retried on the next poll cycle.
     */
    public void revertRecordsToPending(List<KnNotificationKeyDTO> seqIds) {
        String methodName = "revertRecordsToPending";
        if (seqIds == null || seqIds.isEmpty()) {
            return;
        }
        KnPersisterTxn persisterTxn = null;
        PreparedStatement pStmt = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            Connection connection = persisterTxn.getDBConnection(localPttId, false);
            String updateQry =
                    "UPDATE DG.XCAP_PENDING_NOTIFYQ " +
                    "SET NOTIFY_STATUS = ? " +
                    "WHERE NOTIFY_STATUS = ? AND DEST_ID = ? AND INSERTION_TIME = ? AND DEST_TYPE = ?";
            pStmt = connection.prepareStatement(updateQry);
            for (KnNotificationKeyDTO seqId : seqIds) {
                pStmt.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                pStmt.setInt(2, KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());
                pStmt.setString(3, seqId.getDestId());
                pStmt.setLong(4, seqId.getInsertionTime());
                pStmt.setInt(5, seqId.getDestType());
                pStmt.addBatch();
            }
            int[] counts = pStmt.executeBatch();
            persisterTxn.save();
            knLogger.info(methodName, FLOW_TAG + " STEP-12B Reverted failed rows to PENDING. rowCount="
                    + seqIds.size() + " batchResults=" + counts.length);
        } catch (KnPersistenceException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, FLOW_TAG + " STEP-12B Failed to revert rows to PENDING", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, FLOW_TAG + " STEP-12B Unexpected error reverting rows to PENDING", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Makes watcher MDNs immediately eligible for the next optimized poll after a failed send
     * by backing {@code LAST_NOTIFIED_TIME} by one full notification period.
     */
    public void resetMdnNotifyTrackerEpochForRetry(Collection<String> mdns, long periodMillis) {
        String methodName = "resetMdnNotifyTrackerEpochForRetry";
        if (mdns == null || mdns.isEmpty()) {
            return;
        }
        long retryEligibleTs = System.currentTimeMillis() - periodMillis;
        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            Connection connection;
            try {
                connection = persisterTxn.getDBConnection(localPttId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } catch (Exception e) {
                connection = persisterTxn.getDBConnection(localPttId, false);
            }
            updateMdnNotifyTrackerTs(connection, mdns, retryEligibleTs);
            persisterTxn.save();
            knLogger.info(methodName, FLOW_TAG + " STEP-12C Tracker epoch reset for retry. mdnCount="
                    + mdns.size() + " retryEligibleTs=" + retryEligibleTs);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.warn(methodName, FLOW_TAG + " STEP-12C Tracker epoch reset failed", e);
        }
    }

    /**
     * Polls the {@code DG.XCAP_PENDING_NOTIFYQ} table and returns a snapshot of records
     * that are ready to be dispatched to watchers.
     *
     * <h3>Two execution paths</h3>
     * <ol>
     *   <li><b>Optimized mode ({@code XCAP_NOTIFICATION_OPTIMIZED=1})</b><br>
     *       Only MDNs whose epoch window has elapsed
     *       ({@code NOW − LAST_NOTIFIED_TIME >= XCAP_NOTIFICATION_PERIOD}) are eligible.
     *       This is the "Hold and Gather" gate that prevents premature delivery.
     *       At most 100 eligible MDNs are retrieved per poll cycle.
     *       After claim, tracker timestamps are advanced and stale tracker rows are pruned.</li>
     *   <li><b>Legacy mode ({@code XCAP_NOTIFICATION_OPTIMIZED=0})</b><br>
     *       Falls back to the original priority-queue query unchanged.</li>
     * </ol>
     *
     * @return a {@link LinkedHashMap} keyed by {@link KnNotificationKeyDTO};
     *         value is the deserialized notification payload object
     */
    public Map<KnNotificationKeyDTO, Object> pollRecord() {
        KnPersisterTxn persisterTxn = null;
        String methodName = "pollRecord()";
        String selectQry = null;
        String updateQry = null;
        Map<KnNotificationKeyDTO, Object> record       = new LinkedHashMap<>();
        Map<KnNotificationKeyDTO, Object> finalRecord  = new LinkedHashMap<>();
        Connection        connection = null;
        ResultSet         rs  = null;
        PreparedStatement pStmt  = null;
        ResultSet         rs2 = null;
        PreparedStatement pStmt2 = null;
        ResultSet         rs3 = null;
        PreparedStatement pStmt3 = null;
        PreparedStatement pStmt4 = null;

        // Epoch-mode tracking: keeps the watcher IDs whose rows we claim in optimized mode
        // so that we can update MDN_NOTIFY_TRACKER timestamps in a single batch after claim.
        Set<String> claimedOptimizedMdns = new LinkedHashSet<>();
        long   nowMillis    = 0L;
        long   periodMillis = 0L;
        boolean optimizedMode = false;

        try {
            // ─────────────────────────────────────────────────────────────────
            // Step 1: Load runtime configuration
            // ─────────────────────────────────────────────────────────────────
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap =
                    KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);

            int xacpNotifyCountPerAuditInterval = Integer.parseInt(
                    microServicesParamNameValueMap.get(XCAP_NOTIFY_COUNT_PER_AUDIT_INTRVAL) != null
                            ? microServicesParamNameValueMap.get(XCAP_NOTIFY_COUNT_PER_AUDIT_INTRVAL)
                            : "2");

            // Resolve temporal-workflow feature flag and epoch duration
            optimizedMode = ENABLED_STRING.equals(
                    microServicesParamNameValueMap.get(XCAP_NOTIFICATION_OPTIMIZED));

            String clusterIdEnv = System.getenv(CLUSTERID_ENV_NAME);
            String countryCodeEnv = System.getenv("COUNTRYCODE");
            String countryEnv = System.getenv("COUNTRY");
            String xcapOptimizedValue = microServicesParamNameValueMap != null
                    ? microServicesParamNameValueMap.get(XCAP_NOTIFICATION_OPTIMIZED)
                    : null;

            if (optimizedMode) {
                int notificationPeriodSeconds = Integer.parseInt(
                        microServicesParamNameValueMap.get(XCAP_NOTIFICATION_PERIOD) != null
                                ? microServicesParamNameValueMap.get(XCAP_NOTIFICATION_PERIOD)
                                : "120");
                periodMillis = (long) notificationPeriodSeconds * 1_000L;
                nowMillis    = System.currentTimeMillis();
                knLogger.debug(methodName,
                        "Optimized mode ON - epoch period=" + notificationPeriodSeconds + "s now=" + nowMillis);
            }

            knLogger.info(methodName, FLOW_TAG + " STEP-HG1 Poll start. clusterId=" + clusterIdEnv
                    + " countryCode=" + countryCodeEnv + " country=" + countryEnv
                    + " optimizedFlagRaw=" + xcapOptimizedValue + " optimizedMode=" + optimizedMode
                    + " notifyCountPerCycle=" + xacpNotifyCountPerAuditInterval + " epochMillis=" + periodMillis);

            // ─────────────────────────────────────────────────────────────────
            // Step 2: Open DB connection via persister transaction
            // ─────────────────────────────────────────────────────────────────
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, false);
            Connection trackerConnection = connection;
            if (optimizedMode) {
                try {
                    trackerConnection = persisterTxn.getDBConnection(localPttId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                    knLogger.info(methodName, FLOW_TAG + " STEP-HG1A Using secondary DB for MDN tracker operations. datastore="
                            + KnDBConst.DataStores.XDM_SHARED_DATA.getValue());
                } catch (Exception e) {
                    trackerConnection = connection;
                    knLogger.warn(methodName, FLOW_TAG + " STEP-HG1A Failed to open secondary DB for tracker; falling back to primary. cause="
                            + e.getMessage());
                }
            }

            List<KnNotificationKeyDTO> seqList      = new ArrayList<>();
            List<KnNotificationKeyDTO> finalSeqList = new ArrayList<>();

            // ─────────────────────────────────────────────────────────────────
            // Step 3a: OPTIMIZED PATH – epoch-gated MDN fetch
            // ─────────────────────────────────────────────────────────────────
            if (optimizedMode) {
                // Fetch at most 100 eligible watcher MDNs from MDN_NOTIFY_TRACKER.
                // "Eligible" means the epoch window has expired for that watcher.
                List<String> eligibleMdns =
                        fetchEligibleMdns(trackerConnection, nowMillis, periodMillis, 100);

                knLogger.debug(methodName,
                        "Eligible watcher MDN count for this cycle: " + eligibleMdns.size());
                knLogger.info(methodName, FLOW_TAG
                        + " STEP-HG2 Eligible watcher scan complete. eligibleMdns=" + eligibleMdns.size());
                logQueueSnapshotForMdns("STEP-HG2B", eligibleMdns, persisterTxn);

                if (!eligibleMdns.isEmpty()) {
                    // Build IN-clause placeholders dynamically
                    String inClause = String.join(",",
                            Collections.nCopies(eligibleMdns.size(), "?"));

                    selectQry =
                            "SELECT INSERTION_TIME, DEST_ID, DEST_TYPE, PAYLOAD, MSG_TYPE, CID " +
                            "FROM DG.XCAP_PENDING_NOTIFYQ " +
                            "WHERE NOTIFY_STATUS = ? AND DEST_TYPE != 3 " +
                            "  AND DEST_ID IN (" + inClause + ") " +
                            "ORDER BY INSERTION_TIME";

                    pStmt = connection.prepareStatement(selectQry);
                    pStmt.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                    for (int i = 0; i < eligibleMdns.size(); i++) {
                        pStmt.setString(i + 2, eligibleMdns.get(i));
                    }
                    rs = pStmt.executeQuery();
                    knLogger.debug(methodName, "OPTIMIZED QUERY executed: ", selectQry);

                    while (rs.next()) {
                        KnNotificationKeyDTO knNotificationKeyDTO = new KnNotificationKeyDTO(
                                rs.getLong(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getInt(5),
                                rs.getString(6));
                        seqList.add(knNotificationKeyDTO);
                        Object payload = KnGeneralUtil.byteArrayToObject(rs.getBytes(4));
                        record.put(knNotificationKeyDTO, payload);
                        // Track which MDNs are actually being claimed this cycle
                        claimedOptimizedMdns.add(knNotificationKeyDTO.getDestId());
                    }
                    knLogger.info(methodName, FLOW_TAG
                            + " STEP-HG3 Claimed pending queue rows for eligible watchers. claimedRows="
                            + seqList.size() + " claimedMdns=" + claimedOptimizedMdns.size());
                } else {
                    knLogger.info(methodName, FLOW_TAG + " STEP-HG3 No eligible watchers this cycle; nothing claimed");
                }
            }
            // ─────────────────────────────────────────────────────────────────
            // Step 3b: LEGACY PATH – original priority-queue polling (unchanged)
            // ─────────────────────────────────────────────────────────────────
            else {
                selectQry =
                        "SELECT ROWS 1 TO " + xacpNotifyCountPerAuditInterval +
                        " INSERTION_TIME, DEST_ID, DEST_TYPE, PAYLOAD, MSG_TYPE, CID " +
                        "FROM DG.XCAP_PENDING_NOTIFYQ " +
                        "WHERE NOTIFY_STATUS = ? AND PRIORITY = ? AND DEST_TYPE != 3 " +
                        "ORDER BY INSERTION_TIME";
                pStmt = connection.prepareStatement(selectQry);
                pStmt.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                pStmt.setInt(2, 0);
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "LEGACY QUERY executed: ", selectQry);
                while (rs.next()) {
                    KnNotificationKeyDTO knNotificationKeyDTO = new KnNotificationKeyDTO(
                            rs.getLong(1), rs.getString(2), rs.getInt(3),
                            rs.getInt(5), rs.getString(6));
                    seqList.add(knNotificationKeyDTO);
                    Object payload = KnGeneralUtil.byteArrayToObject(rs.getBytes(4));
                    record.put(knNotificationKeyDTO, payload);
                }

                if (record.size() < xacpNotifyCountPerAuditInterval) {
                    int restFetch = xacpNotifyCountPerAuditInterval - record.size();
                    selectQry =
                            "SELECT ROWS 1 TO " + restFetch +
                            " INSERTION_TIME, DEST_ID, DEST_TYPE, PAYLOAD, MSG_TYPE, CID " +
                            "FROM DG.XCAP_PENDING_NOTIFYQ " +
                            "WHERE NOTIFY_STATUS = ? AND PRIORITY = ? AND DEST_TYPE != 3 " +
                            "ORDER BY INSERTION_TIME";
                    pStmt2 = connection.prepareStatement(selectQry);
                    pStmt2.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                    pStmt2.setInt(2, 5);
                    rs2 = pStmt2.executeQuery();
                    while (rs2.next()) {
                        KnNotificationKeyDTO knNotificationKeyDTO = new KnNotificationKeyDTO(
                                rs2.getLong(1), rs2.getString(2), rs2.getInt(3),
                                rs2.getInt(5), rs2.getString(6));
                        seqList.add(knNotificationKeyDTO);
                        Object payload = KnGeneralUtil.byteArrayToObject(rs2.getBytes(4));
                        record.put(knNotificationKeyDTO, payload);
                    }
                }
                knLogger.info(methodName, FLOW_TAG + " STEP-HG2 Legacy claim complete. claimedRows=" + seqList.size());
            }

            // ─────────────────────────────────────────────────────────────────
            // Step 4: Subscriber-info enrichment  (same for both paths)
            // ─────────────────────────────────────────────────────────────────
            List<String>               mdnListTogetSublistInfo = new ArrayList<>();
            Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap       = new HashMap<>();
            Map<String, Set<String>>      baseMdnMap            = new HashMap<>();
            try {
                getmdnListTogetSublistInfo(record, mdnListTogetSublistInfo);
                knLogger.debug(methodName, "mdnListTogetSublistInfo size:", mdnListTogetSublistInfo.size());
                if (!mdnListTogetSublistInfo.isEmpty()) {
                    KnXDMSubsProfileRespDTO subsInfo =
                            genInfoUtil.selectSubsProfileInfo(mdnListTogetSublistInfo, null);
                    if (subsInfo != null && subsInfo.getSubsRespDTO() != null
                            && !subsInfo.getSubsRespDTO().isEmpty()) {
                        for (KnXDMSubsProvDTO itr : subsInfo.getSubsRespDTO()) {
                            mdnSubsInfoMap.put(itr.getMdn().trim(), itr);
                        }
                        knLogger.info("mdns set in mdnSubsInfoMap size", mdnSubsInfoMap.size());
                    }
                    baseMdnMap = genInfoUtil.getBaseMdnsMap(mdnListTogetSublistInfo, null);
                    knLogger.debug(methodName, "baseMdnMap:", baseMdnMap.size(),
                            " mdnSubsInfoMap:", mdnSubsInfoMap.size());
                } else {
                    knLogger.debug(methodName, FLOW_TAG
                            + " STEP-HG3A Skipping subscriber profile lookup — no MDNs in claimed batch");
                }
            } catch (Exception e) {
                knLogger.error(methodName, "Exception during subscriber info lookup: ", e);
                throw new RuntimeException(e);
            }

            // ─────────────────────────────────────────────────────────────────
            // Step 5: Payload-size gating & final record assembly
            // Optimized mode dispatches the full watcher bundle claimed in Step 3a (epic debulk).
            // Legacy mode retains the original audit-interval payload-unit cap.
            int currentSize = 0;
            for (Map.Entry<KnNotificationKeyDTO, Object> entry : record.entrySet()) {
                knLogger.debug(methodName, "Key:", entry.getKey(), " Value:", entry.getValue());
                Object payload   = entry.getValue();
                KnNotificationKeyDTO seqId = entry.getKey();
                int payloadSize  = 0;

                if (payload instanceof KnXcapDiffDirChgNotifyDTO) {
                    payloadSize = getXcapDiffDirChgNotifyCount(
                            Collections.singletonList((KnXcapDiffDirChgNotifyDTO) payload));
                    knLogger.debug(methodName, "KnXcapDiffDirChgNotifyDTO payload size:", payloadSize);
                } else if (payload instanceof KnXcapDiffNotifyDTO) {
                    payloadSize = (seqId.getMsgType() != 1)
                            ? countGeneratedNotifications(
                                    Collections.singletonList((KnXcapDiffNotifyDTO) payload))
                            : countGeneratedNotifications((KnXcapDiffNotifyDTO) payload);
                    knLogger.debug(methodName, "KnXcapDiffNotifyDTO payload size:", payloadSize);
                } else if (payload instanceof KnMCSNotifyDTO) {
                    payloadSize = 1;
                }

                if (!optimizedMode && currentSize > xacpNotifyCountPerAuditInterval) {
                    break;
                }
                finalSeqList.add(entry.getKey());
                finalRecord.put(entry.getKey(), payload);
                currentSize += payloadSize;
            }
            knLogger.info(methodName, FLOW_TAG
                    + " STEP-HG4 Final record assembly complete. finalSeqCount=" + finalSeqList.size()
                    + " computedPayloadUnits=" + currentSize);

            // ─────────────────────────────────────────────────────────────────
            // Step 6: Suppressed-MDN batch (legacy path only; optimized path
            //         does not mix suppressed rows into the epoch batch)
            // ─────────────────────────────────────────────────────────────────
            if (!optimizedMode) {
                pStmt3 = connection.prepareStatement(SELECT_SUPPRESSED_NOTIFY);
                pStmt3.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                pStmt3.setInt(2, KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
                rs3 = pStmt3.executeQuery();
                int suppressWatcherCount = 0;
                int suppressBatchCount = Integer.parseInt(
                        microServicesParamNameValueMap.get(XCAP_NOTIFY_SUPPRESS_MDN_BATCH) != null
                                ? microServicesParamNameValueMap.get(XCAP_NOTIFY_SUPPRESS_MDN_BATCH)
                                : "100");
                while (rs3.next()) {
                    int paramCount = rs3.getInt(6);
                    Object payload = KnGeneralUtil.byteArrayToObject(rs3.getBytes(4));
                    if (suppressWatcherCount + paramCount > suppressBatchCount) {
                        break;
                    }
                    suppressWatcherCount += paramCount;
                    KnNotificationKeyDTO k = new KnNotificationKeyDTO(
                            rs3.getLong(1), rs3.getString(2), rs3.getInt(3),
                            rs3.getInt(5), rs3.getString(6));
                    finalSeqList.add(k);
                    finalRecord.put(k, payload);
                }
            }

            // ─────────────────────────────────────────────────────────────────
            // Step 7: Mark claimed rows as NOTIFY_INITIATED
            // ─────────────────────────────────────────────────────────────────
            updateQry =
                    "UPDATE DG.XCAP_PENDING_NOTIFYQ " +
                    "SET NOTIFY_STATUS = ? " +
                    "WHERE DEST_ID = ? AND INSERTION_TIME = ? AND DEST_TYPE = ?";
            pStmt4 = connection.prepareStatement(updateQry);
            for (KnNotificationKeyDTO k : finalSeqList) {
                pStmt4.setInt(1,    KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());
                pStmt4.setString(2, k.getDestId());
                pStmt4.setLong(3,   k.getInsertionTime());
                pStmt4.setInt(4,    k.getDestType());
                pStmt4.addBatch();
            }
            int[] count = pStmt4.executeBatch();
            knLogger.debug(methodName, "NOTIFY_INITIATED update count:", count.length);
            knLogger.info(methodName, FLOW_TAG
                    + " STEP-HG5 Marked rows as NOTIFY_INITIATED. updatedRows=" + count.length);
            LinkedHashSet<String> queueSnapshotMdns = finalSeqList.stream()
                    .map(KnNotificationKeyDTO::getDestId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            logQueueSnapshotForMdns("STEP-HG5A", queueSnapshotMdns, persisterTxn);

            // ─────────────────────────────────────────────────────────────────
            // Step 8: Advance tracker timestamps + housekeeping (optimized only)
            // IMPORTANT: only advance timestamps for MDNs whose rows are in
            // finalSeqList (i.e. rows that actually passed the payload-cap check
            // and will be dispatched). Advancing the epoch for rows that were
            // fetched but then dropped by the cap would cause starvation because
            // those watchers would be silently deferred to the next epoch window.
            // ─────────────────────────────────────────────────────────────────
            if (optimizedMode) {
                Set<String> finalClaimedMdns = finalSeqList.stream()
                        .map(KnNotificationKeyDTO::getDestId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                if (!finalClaimedMdns.isEmpty()) {
                    // Advance LAST_NOTIFIED_TIME so the next epoch starts from NOW
                    updateMdnNotifyTrackerTs(trackerConnection, finalClaimedMdns, nowMillis);
                    knLogger.info(methodName,
                            "Tracker updated for ", finalClaimedMdns.size(), " MDN(s) (finalSeqList-scoped)");
                } else {
                    knLogger.info(methodName, FLOW_TAG
                            + " STEP-HG6A No dispatched MDNs this cycle; timestamp update skipped");
                }

                // Always run cleanup in optimized mode to age out stale tracker rows.
                cleanupMdnNotifyTracker(trackerConnection, nowMillis, periodMillis);

                knLogger.info(methodName, FLOW_TAG
                        + " STEP-HG6 Tracker maintenance complete. updatedMdns=" + claimedOptimizedMdns.size());
                logTrackerSnapshotForMdns("STEP-HG6C", claimedOptimizedMdns, persisterTxn);
            }

            persisterTxn.save();

        } catch (KnPersistenceException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeResultSet(rs2);
            KnDbUtil.closeStatement(pStmt2);
            KnDbUtil.closeResultSet(rs3);
            KnDbUtil.closeStatement(pStmt3);
            KnDbUtil.closeStatement(pStmt4);
        }

        knLogger.info(methodName, "finalRecord size:", finalRecord.size());
        knLogger.info(methodName, FLOW_TAG
                + " STEP-HG7 Poll cycle completed. finalDispatchableRecords=" + finalRecord.size());
        return finalRecord;
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    // MDN_NOTIFY_TRACKER – public API
    // ═══════════════════════════════════════════════════════════════════════════════════

    /**
     * Returns {@code true} when the temporal wait-and-bundle optimisation is active.
     *
     * <p>Runtime check against the microservices config map, allowing operators to flip the
     * feature without a restart.  On any failure the method defaults to {@code false} so
     * that the legacy notification path is used safely.
     *
     * @return {@code true} iff {@code XCAP_NOTIFICATION_OPTIMIZED = "1"}
     */
    public boolean isOptimizedNotificationEnabled() {
        String methodName = "isOptimizedNotificationEnabled";
        try {
            String clusterIdEnv = System.getenv(CLUSTERID_ENV_NAME);
            String countryCodeEnv = System.getenv("COUNTRYCODE");
            String countryEnv = System.getenv("COUNTRY");
            int clusterId = Integer.parseInt(clusterIdEnv);
            Map<String, String> configMap =
                    KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);
            String rawFlag = configMap != null ? configMap.get(XCAP_NOTIFICATION_OPTIMIZED) : null;
            boolean enabled = ENABLED_STRING.equals(rawFlag);
            knLogger.info(methodName, FLOW_TAG + " STEP-CONF1 Resolved XCAP optimization flag. clusterId="
                    + clusterIdEnv + " countryCode=" + countryCodeEnv + " country=" + countryEnv
                    + " rawFlag=" + rawFlag + " enabled=" + enabled);
            knLogger.debug(methodName, "XCAP_NOTIFICATION_OPTIMIZED =", enabled);
            return enabled;
        } catch (Exception e) {
            knLogger.warn(methodName,
                    "Unable to resolve XCAP_NOTIFICATION_OPTIMIZED flag - defaulting to false. clusterId="
                            + System.getenv(CLUSTERID_ENV_NAME)
                            + " countryCode=" + System.getenv("COUNTRYCODE")
                            + " country=" + System.getenv("COUNTRY")
                            + " cause=" + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts watcher MDN rows into {@code DG.MDN_NOTIFY_TRACKER} using INSERT-IF-ABSENT
     * (MERGE) semantics.
     *
     * <h3>Epoch preservation rule</h3>
     * If a row already exists for the given MDN the MERGE does <em>nothing</em>, preserving
     * the original {@code LAST_NOTIFIED_TIME}.  Burst updates for the same subscriber
     * therefore never reset the epoch clock.
     *
     * <h3>Feature-flag guard</h3>
     * The method is a no-op when optimised mode is OFF, keeping call-sites in
     * {@link KnXcapDiffNotifierImpl} clean.
     *
     * @param mdns         deduplicated set of watcher MDNs to register
     * @param persisterTxn existing open transaction or {@code null}
     */
    public void upsertMdnNotifyTracker(Collection<String> mdns, KnPersisterTxn persisterTxn) throws KnPersistenceException {
        String methodName = "upsertMdnNotifyTracker";

        if (mdns == null || mdns.isEmpty()) {
            knLogger.info(methodName, FLOW_TAG + " STEP-SF0 Skipping tracker upsert because MDN collection is empty");
            return;
        }
        // Legacy mode must not touch MDN_NOTIFY_TRACKER (XCAP-DEBULK-002 / rollback path).
        if (!isOptimizedNotificationEnabled()) {
            knLogger.info(methodName, FLOW_TAG
                    + " STEP-SF0 Optimized mode OFF – skipping tracker upsert. inputMdnCount=" + mdns.size());
            return;
        }

        boolean ownedTxn      = false;
        Connection        connection = null;
        PreparedStatement pStmt      = null;
        int validMdnCount            = 0;
        int skippedBlankCount        = 0;
        List<String> trackerMdnSample = new ArrayList<>();

        try {
            knLogger.info(methodName, FLOW_TAG + " STEP-SF1 Tracker upsert requested. inputMdnCount=" + mdns.size()
                    + " trackerTable=DG.MDN_NOTIFY_TRACKER");
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            boolean usedSecondary = true;
            try {
                connection = persisterTxn.getDBConnection(localPttId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.info(methodName, FLOW_TAG + " STEP-SF1A Using secondary DB for MDN tracker upsert. datastore="
                        + KnDBConst.DataStores.XDM_SHARED_DATA.getValue() + " localPttId=" + localPttId);
            } catch (Exception e) {
                usedSecondary = false;
                connection = persisterTxn.getDBConnection(localPttId, false);
                knLogger.warn(methodName, FLOW_TAG
                        + " STEP-SF1A Secondary DB unavailable for tracker upsert; falling back to primary. localPttId="
                        + localPttId + " cause=" + e.getMessage());
            }
            String selectExistingSql = "SELECT 1 FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = ?";
            PreparedStatement selectStmt = null;
            pStmt = connection.prepareStatement(INSERT_MDN_NOTIFY_TRACKER);
            selectStmt = connection.prepareStatement(selectExistingSql);

            long now = System.currentTimeMillis();
            int affectedRows = 0;
            int unchangedRows = 0;
            int failedRows = 0;

            try {
                for (String mdn : mdns) {
                    if (mdn == null || mdn.trim().isEmpty()) {
                        knLogger.warn(methodName, "Skipping blank MDN in tracker upsert batch");
                        skippedBlankCount++;
                        continue;
                    }
                    validMdnCount++;
                    String trimmedMdn = mdn.trim();
                    if (trackerMdnSample.size() < 10) {
                        trackerMdnSample.add(trimmedMdn);
                    }
                    try {
                        selectStmt.setString(1, trimmedMdn);
                        try (ResultSet rs = selectStmt.executeQuery()) {
                            if (rs.next()) {
                                unchangedRows++;
                                knLogger.debug(methodName, FLOW_TAG
                                        + " STEP-SF2A MDN already in tracker (insert-if-absent). mdn=" + trimmedMdn);
                                continue;
                            }
                        }
                        pStmt.setString(1, trimmedMdn);
                        pStmt.setLong(2, now);
                        int rows = pStmt.executeUpdate();
                        if (rows > 0) {
                            affectedRows += rows;
                        } else {
                            unchangedRows++;
                        }
                    } catch (SQLException rowEx) {
                        String msg = rowEx.getMessage() != null ? rowEx.getMessage().toLowerCase() : "";
                        boolean likelyDuplicate = msg.contains("unique") || msg.contains("duplicate")
                                || msg.contains("constraint") || msg.contains("primary key")
                                || msg.contains("already exists");
                        if (likelyDuplicate) {
                            unchangedRows++;
                            knLogger.debug(methodName, FLOW_TAG
                                    + " STEP-SF2A MDN already in tracker (race). mdn=" + trimmedMdn);
                        } else {
                            failedRows++;
                            knLogger.warn(methodName, FLOW_TAG
                                    + " STEP-SF2B Tracker insert failed for mdn=" + trimmedMdn
                                    + " cause=" + rowEx.getMessage(), rowEx);
                        }
                    }
                }
            } finally {
                KnDbUtil.closeStatement(selectStmt);
            }

            knLogger.info(methodName, FLOW_TAG + " STEP-SF1B Tracker MDN input snapshot. inputMdnCount="
                    + mdns.size() + " validMdnCount=" + validMdnCount + " blankSkippedCount=" + skippedBlankCount
                    + " sampleMdns=" + trackerMdnSample);

            if (validMdnCount == 0) {
                knLogger.info(methodName, FLOW_TAG + " STEP-SF0 No valid MDNs found after filtering; tracker upsert skipped.");
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                return;
            }

            knLogger.info(methodName, FLOW_TAG + " STEP-SF2 Tracker upsert completed. validMdnCount=" + validMdnCount
                    + " affectedRows=" + affectedRows + " unchangedRows=" + unchangedRows
                    + " failedRows=" + failedRows
                    + " sampleMdns=" + trackerMdnSample);

            if (ownedTxn) {
                if (failedRows > 0 && affectedRows == 0 && unchangedRows == 0) {
                    rollback(persisterTxn);
                    throw new KnPersistenceException(KnErrorCodes.DAO.SQL_EXCEPTION,
                            "All MDN tracker inserts failed. failedRows=" + failedRows,
                            localPttId, methodName);
                }
                persisterTxn.save();
                knLogger.info(methodName, FLOW_TAG + " STEP-SF3 Tracker upsert committed. affectedRows="
                        + affectedRows + " unchangedRows=" + unchangedRows + " usedSecondary=" + usedSecondary
                        + " datastore=" + (usedSecondary
                        ? KnDBConst.DataStores.XDM_SHARED_DATA.getValue() : "PRIMARY"));
            } else {
                knLogger.info(methodName, FLOW_TAG + " STEP-SF3 Tracker upsert staged in caller transaction. affectedRows="
                        + affectedRows + " unchangedRows=" + unchangedRows + " usedSecondary=" + usedSecondary
                        + " datastore=" + (usedSecondary
                        ? KnDBConst.DataStores.XDM_SHARED_DATA.getValue() : "PRIMARY"));
            }
            logTrackerSnapshotForMdns("STEP-SF4", mdns, persisterTxn);

        } catch (KnPersistenceException e) {
            if (ownedTxn) rollback(persisterTxn);
            knLogger.error(methodName, "KnPersistenceException during tracker upsert", e);
            throw e;
        } catch (SQLException e) {
            if (ownedTxn) rollback(persisterTxn);
            knLogger.error(methodName, "SQLException during tracker upsert", e);
            throw new KnPersistenceException(KnErrorCodes.DAO.SQL_EXCEPTION,
                    "SQLException during MDN tracker upsert", e);
        } catch (Exception e) {
            if (ownedTxn) rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected exception during tracker upsert", e);
            throw new KnPersistenceException(KnErrorCodes.DAO.INTERNAL_ERROR,
                    "Unexpected exception during MDN tracker upsert", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    // MDN_NOTIFY_TRACKER – private helpers (pollRecord() internals)
    // ═══════════════════════════════════════════════════════════════════════════════════

    /**
     * Queries eligible watchers from {@code DG.MDN_NOTIFY_TRACKER}.
     * "Eligible" means {@code (nowMillis - LAST_NOTIFIED_TIME) >= periodMillis}.
     *
     * @param connection   open JDBC connection (not closed here)
     * @param nowMillis    current time in ms
     * @param periodMillis configured epoch duration in ms
     * @param maxMdns      batch cap (typically 100)
     * @return list of eligible watcher MDNs
     * @throws SQLException on DB error
     */
    private List<String> fetchEligibleMdns(Connection connection,
                                            long nowMillis,
                                            long periodMillis,
                                            int maxMdns) throws SQLException {
        List<String> eligibleMdns = new ArrayList<>();
        String sql =
                "SELECT FIRST " + maxMdns + " MDN FROM DG.MDN_NOTIFY_TRACKER " +
                "WHERE (? - LAST_NOTIFIED_TIME) >= ? " +
                "ORDER BY LAST_NOTIFIED_TIME ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, nowMillis);
            ps.setLong(2, periodMillis);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    eligibleMdns.add(rs.getString(1));
                }
            }
        }
        knLogger.debug("fetchEligibleMdns",
                "Eligible MDN count (period>=" + periodMillis + "ms):" + eligibleMdns.size());
        knLogger.info("fetchEligibleMdns", FLOW_TAG
                + " STEP-HG2A Eligible MDNs fetched. maxMdns=" + maxMdns
                + " eligibleCount=" + eligibleMdns.size());
        return eligibleMdns;
    }

    /**
     * Advances {@code LAST_NOTIFIED_TIME} for claimed MDNs so a fresh epoch starts
     * after the current bundle is dispatched.
     *
     * @param connection open JDBC connection
     * @param mdns       watcher MDNs to update
     * @param nowMillis  timestamp to write
     * @throws SQLException on DB error
     */
    private void updateMdnNotifyTrackerTs(Connection connection,
                                          Collection<String> mdns,
                                          long nowMillis) throws SQLException {
        if (mdns == null || mdns.isEmpty()) return;
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_MDN_NOTIFY_TRACKER_TS)) {
            for (String mdn : mdns) {
                ps.setLong(1, nowMillis);
                ps.setString(2, mdn);
                ps.addBatch();
            }
            int[] r = ps.executeBatch();
            knLogger.debug("updateMdnNotifyTrackerTs", "Updated TS for " + r.length + " MDN(s)");
            knLogger.info("updateMdnNotifyTrackerTs", FLOW_TAG
                    + " STEP-HG6A Tracker timestamp update complete. updatedCount=" + r.length
                    + " nowMillis=" + nowMillis);
        }
    }

    /**
     * Deletes stale rows from {@code DG.MDN_NOTIFY_TRACKER} where the epoch age
     * has exceeded {@code periodMillis}.
     *
     * @param connection   open JDBC connection
     * @param nowMillis    current time in ms
     * @param periodMillis epoch duration in ms
     * @throws SQLException on DB error
     */
    private void cleanupMdnNotifyTracker(Connection connection,
                                         long nowMillis,
                                         long periodMillis) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_MDN_NOTIFY_TRACKER_STALE)) {
            ps.setLong(1, nowMillis);
            ps.setLong(2, periodMillis);
            ps.setInt(3, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
            ps.setInt(4, KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());
            int deleted = ps.executeUpdate();
            knLogger.debug("cleanupMdnNotifyTracker", "Stale tracker rows deleted:", deleted);
            knLogger.info("cleanupMdnNotifyTracker", FLOW_TAG
                    + " STEP-HG6B Tracker cleanup complete. deletedRows=" + deleted
                    + " periodMillis=" + periodMillis);
        }
    }

    public void logQueueSnapshotForMdns(String stepTag,
                                        Collection<String> mdns,
                                        KnPersisterTxn persisterTxn) {
        String methodName = "logQueueSnapshotForMdns";
        List<String> normalizedMdns = normalizeMdnsForSnapshot(mdns);
        if (normalizedMdns.isEmpty()) {
            knLogger.info(methodName, FLOW_TAG + " " + stepTag + " Queue snapshot skipped. reason=noValidMdns");
            return;
        }

        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            Connection connection = persisterTxn.getDBConnection(localPttId, false);
            String inClause = String.join(",", Collections.nCopies(normalizedMdns.size(), "?"));
            String sql = "SELECT NOTIFY_STATUS, COUNT(*) FROM DG.XCAP_PENDING_NOTIFYQ "
                    + "WHERE DEST_ID IN (" + inClause + ") GROUP BY NOTIFY_STATUS ORDER BY NOTIFY_STATUS";
            pStmt = connection.prepareStatement(sql);
            for (int i = 0; i < normalizedMdns.size(); i++) {
                pStmt.setString(i + 1, normalizedMdns.get(i));
            }
            rs = pStmt.executeQuery();
            Map<Integer, Integer> statusCounts = new LinkedHashMap<>();
            int totalRows = 0;
            while (rs.next()) {
                int status = rs.getInt(1);
                int count = rs.getInt(2);
                statusCounts.put(status, count);
                totalRows += count;
            }
            knLogger.info(methodName, FLOW_TAG + " " + stepTag
                    + " Queue snapshot. watcherCount=" + normalizedMdns.size()
                    + " totalRows=" + totalRows
                    + " statusCounts=" + statusCounts
                    + " sampleMdns=" + normalizedMdns.subList(0, Math.min(normalizedMdns.size(), 10)));
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (Exception e) {
            if (ownedTxn) rollback(persisterTxn);
            knLogger.warn(methodName, FLOW_TAG + " " + stepTag + " Queue snapshot failed. mdnCount="
                    + normalizedMdns.size(), e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public void logTrackerSnapshotForMdns(String stepTag,
                                          Collection<String> mdns,
                                          KnPersisterTxn persisterTxn) {
        String methodName = "logTrackerSnapshotForMdns";
        List<String> normalizedMdns = normalizeMdnsForSnapshot(mdns);
        if (normalizedMdns.isEmpty()) {
            knLogger.info(methodName, FLOW_TAG + " " + stepTag + " Tracker snapshot skipped. reason=noValidMdns");
            return;
        }

        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            Connection connection = persisterTxn.getDBConnection(localPttId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String inClause = String.join(",", Collections.nCopies(normalizedMdns.size(), "?"));
            String sql = "SELECT COUNT(*), COUNT(DISTINCT MDN), MIN(LAST_NOTIFIED_TIME), MAX(LAST_NOTIFIED_TIME) "
                    + "FROM DG.MDN_NOTIFY_TRACKER WHERE MDN IN (" + inClause + ")";
            pStmt = connection.prepareStatement(sql);
            for (int i = 0; i < normalizedMdns.size(); i++) {
                pStmt.setString(i + 1, normalizedMdns.get(i));
            }
            rs = pStmt.executeQuery();
            if (rs.next()) {
                int trackerRows = rs.getInt(1);
                int distinctMdns = rs.getInt(2);
                long minTs = rs.getLong(3);
                long maxTs = rs.getLong(4);
                int duplicateRows = trackerRows - distinctMdns;
                knLogger.info(methodName, FLOW_TAG + " " + stepTag
                        + " Tracker snapshot. watcherCount=" + normalizedMdns.size()
                        + " trackerRows=" + trackerRows
                        + " distinctMdns=" + distinctMdns
                        + " duplicateRows=" + duplicateRows
                        + " minLastNotifiedTime=" + minTs
                        + " maxLastNotifiedTime=" + maxTs
                        + " sampleMdns=" + normalizedMdns.subList(0, Math.min(normalizedMdns.size(), 10)));
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (Exception e) {
            if (ownedTxn) rollback(persisterTxn);
            knLogger.warn(methodName, FLOW_TAG + " " + stepTag + " Tracker snapshot failed. mdnCount="
                    + normalizedMdns.size(), e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
    }

    private List<String> normalizeMdnsForSnapshot(Collection<String> mdns) {
        if (mdns == null || mdns.isEmpty()) {
            return Collections.emptyList();
        }
        return mdns.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(mdn -> !mdn.isEmpty())
                .distinct()
                .limit(100)
                .collect(Collectors.toList());
    }

    public void getmdnListTogetSublistInfo(Map<KnNotificationKeyDTO, Object> record, List<String> mdnListTogetSublistInfo) {
        String methodName = "getmdnListTogetSublistInfo(Map<KnNotificationKeyDTO, Object>, List<String>)";
        for (Map.Entry<KnNotificationKeyDTO, Object> itr : record.entrySet()) {
            Object obj = itr.getValue();
            if (obj instanceof KnXcapDiffDirChgNotifyDTO) {
                KnXcapDiffDirChgNotifyDTO payload = (KnXcapDiffDirChgNotifyDTO) obj;
                if (itr.getKey().getDestType() == 2) {
                    mdnListTogetSublistInfo.add(itr.getKey().getDestId());
                }

                if (null != payload.getDocDiffObj()) {
                    Set<String> docsels = payload.getDocDiffObj().stream().map(KnXcapDiffDocDTO::getDocumentSelector).collect(Collectors.toSet());
                    docsels.removeIf(Objects::isNull);
                    docsels.forEach(e -> {
                        String substring = null;
                        int index = e.indexOf("tel:+");
                        if (index != -1) {
                            substring = e.substring(index);
                        }
                        if (null != substring) {
                            String mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
                            mdnListTogetSublistInfo.add(mdn);
                        }
                    });

                    Set<String> docuris = payload.getDocDiffObj().stream().map(KnXcapDiffDocDTO::getDocUri).collect(Collectors.toSet());
                    docuris.removeIf(Objects::isNull);
                    docuris.forEach(e -> {
                        String substring = null;
                        int index = e.indexOf("tel:+");
                        if (index != -1) {
                            substring = e.substring(index);
                        }
                        if (null != substring) {
                            String mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
                            mdnListTogetSublistInfo.add(mdn);
                        }
                    });
                }

                mdnListTogetSublistInfo.removeIf(Objects::isNull);
                mdnListTogetSublistInfo.add(payload.getMdn());
                knLogger.debug(methodName, "KnXcapDiffDirChgNotifyDTO mdnListTogetSublistInfo size: ", mdnListTogetSublistInfo.size());
            } else if (obj instanceof KnXcapDiffNotifyDTO) {
                KnXcapDiffNotifyDTO payload = (KnXcapDiffNotifyDTO) obj;
                if (itr.getKey().getDestType() == 2) {
                    mdnListTogetSublistInfo.add(itr.getKey().getDestId());
                }

                if (null != payload.getDocDiffObj()) {
                    Set<String> docsels = payload.getDocDiffObj().stream().map(KnXcapDiffDocDTO::getDocumentSelector).collect(Collectors.toSet());
                    docsels.removeIf(Objects::isNull);
                    docsels.forEach(e -> {
                        String substring = null;
                        int index = e.indexOf("tel:+");
                        if (index != -1) {
                            substring = e.substring(index);
                        }
                        if (null != substring) {
                            String mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
                            mdnListTogetSublistInfo.add(mdn);
                        }
                    });

                    Set<String> docuris = payload.getDocDiffObj().stream().map(KnXcapDiffDocDTO::getDocUri).collect(Collectors.toSet());
                    docuris.removeIf(Objects::isNull);
                    docuris.forEach(e -> {
                        String substring = null;
                        int index = e.indexOf("tel:+");
                        if (index != -1) {
                            substring = e.substring(index);
                        }
                        if (null != substring) {
                            String mdn = substring.substring(substring.indexOf("+") + 1, substring.indexOf("/"));
                            mdnListTogetSublistInfo.add(mdn);
                        }
                    });
                }
                mdnListTogetSublistInfo.add(payload.getMdn());
                mdnListTogetSublistInfo.removeIf(Objects::isNull);
                knLogger.debug(methodName, "KnXcapDiffNotifyDTO mdnListTogetSublistInfo size: ", mdnListTogetSublistInfo.size());
            }
        }
    }

    public void updateRecord() {
        KnPersisterTxn persisterTxn = null;
        String methodName = "updateRecord()";
        String updateQry = null;
        Map<KnNotificationKeyDTO, Object> record = new LinkedHashMap<>();
        Connection connection = null;
        PreparedStatement pStmt = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, false);

            updateQry = "UPDATE DG.XCAP_PENDING_NOTIFYQ SET NOTIFY_STATUS = ? where NOTIFY_STATUS = ?";
            pStmt = connection.prepareStatement(updateQry);
            pStmt.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
            pStmt.setInt(2, KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());

            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed - ", updateQry);
            knLogger.debug(methodName, "update result count", count);
            knLogger.info(methodName, FLOW_TAG + " STEP-2A Recovery reset complete. recoveredRows=" + count);
            persisterTxn.save();
        } catch (KnPersistenceException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public int recordCount(KnPersisterTxn persisterTxn) {
        String methodName = "recordCount()";
        String selectQry = null;
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        int count = 0;
        boolean ownedTxn = false;
        try {

            if(persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, true);

            selectQry ="SELECT COUNT(1) FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_TYPE IN (" + KnXcapNotifyConstants.DESTTYPE.MDN.value()+","+KnXcapNotifyConstants.DESTTYPE.GROUP.value()+")";
            pStmt = connection.prepareStatement(selectQry);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ", selectQry);
            knLogger.debug(methodName, "result - ", rs);
            while (rs.next()) {
                count = rs.getInt(1);
            }

            if(ownedTxn) {
                persisterTxn.save();
            }
            knLogger.debug(methodName, "record count", count);
        } catch (KnPersistenceException e) {
            if(ownedTxn) {
                rollback(persisterTxn);
            }
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            if(ownedTxn) {
                rollback(persisterTxn);
            }
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return count;
    }

    public int totalRecordCount() {
        KnPersisterTxn persisterTxn = null;
        String methodName = "totalRecordCount()";
        String selectQry = null;
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        int count = 0;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
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
            persisterTxn.save();
            knLogger.debug(methodName, "record count", count);
        } catch (KnPersistenceException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Persister Txn occurred - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return count;
    }

    int getXcapDiffDirChgNotifyCount(List<KnXcapDiffDirChgNotifyDTO> xcapDiffDirChgNotifyList) {
        String methodName = "getXcapDiffDirChgNotifyCount(List<KnXcapDiffDirChgNotifyDTO>)";
        int payloadSize = 0;
        for (KnXcapDiffDirChgNotifyDTO xcapDiffDirChgNotifyDTO : xcapDiffDirChgNotifyList) {
            if (xcapDiffDirChgNotifyDTO.isNotfnCapability()) {
                payloadSize += countNewChangeLog(xcapDiffDirChgNotifyDTO.getDocDiffObj(), Integer.parseInt(xcapDiffDirChgNotifyDTO.getProtocolVersion()));
            } else {
                payloadSize += countChangeLog(xcapDiffDirChgNotifyDTO.getDocDiffObj());
            }
        }
        knLogger.debug(methodName, "payload size for KnXcapDiffDirChgNotifyDTO ", payloadSize);
        return payloadSize;
    }
    public int countGeneratedNotifications(List<KnXcapDiffNotifyDTO> xcapDiffNotifyObjs) {
        final String methodName = "countGeneratedNotifications(List<KnXcapDiffNotifyDTO>)";
        int count = 0;
        for (KnXcapDiffNotifyDTO xcapDiffNotifyObj : xcapDiffNotifyObjs) {
            Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = xcapDiffNotifyObj.getDocDiffObj();
            if (xcapDiffDocObsList != null) {
                for (KnXcapDiffDocDTO xcapDiffDocObj : xcapDiffDocObsList) {
                    if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.ADD.value() ||
                            xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REPLACE.value() ||
                            xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()) {
                        count++;
                    }
                }
            }
            // Increment count for deRegister notification if the flag is set
            if (xcapDiffNotifyObj.isDeRegisterNotify()) {
                count++;
            }
            // Increment count for profile change notification if the flag is set
            if (xcapDiffNotifyObj.isProfileNotify()) {
                count++;
            }
        }
        knLogger.info(methodName, "Total count of KnNotifyPayloadDTO objects:", count);
        return count;
    }

    public int countGeneratedNotifications(KnXcapDiffNotifyDTO xcapDiffNotifyObj) {
        String methodName = "countGeneratedNotifications(KnXcapDiffNotifyDTO)";
        int count = 0;
        if (null != xcapDiffNotifyObj &&
                null != xcapDiffNotifyObj.getPresenceHome() &&
                !isUnUpgradedPOCSERVER(xcapDiffNotifyObj.getPresenceHome())) {
            count++;
            if (xcapDiffNotifyObj.isDeRegisterNotify()) count++;
            if (xcapDiffNotifyObj.isChangeMdnNotify()) count++;
            if (xcapDiffNotifyObj.isProfileNotify()) count++;
        }
        knLogger.info(methodName, "Total count of KnNotifyPayloadDTO objects:", count);
        return count;
    }
    private int countNewChangeLog(Collection<KnXcapDiffDocDTO> xcapDiffDocObsFinalList, int protocol) {
        String methodName = "countNewChangeLog(Collection<KnXcapDiffDocDTO>)";
        knLogger.debug(methodName, "Entry Point xcapDiffDocObsFinalList: ", xcapDiffDocObsFinalList);
        Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = new HashSet<>(xcapDiffDocObsFinalList);
        int count = 0;
        if (xcapDiffDocObsList != null && !xcapDiffDocObsList.isEmpty()) {
            for (KnXcapDiffDocDTO xcapDiffDocObj : xcapDiffDocObsList) {
                if ((xcapDiffDocObj.getAddedContactList() != null && !xcapDiffDocObj.getAddedContactList().isEmpty()) ||
                        (xcapDiffDocObj.getRemovedContactList() != null && !xcapDiffDocObj.getRemovedContactList().isEmpty()) ||
                        (xcapDiffDocObj.getModifiedContactList() != null && !xcapDiffDocObj.getModifiedContactList().isEmpty())) {
                    count++;
                } else if (xcapDiffDocObj.getDocChangeType() != KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()
                        && ((xcapDiffDocObj.getAddedGroupMembers() != null && !xcapDiffDocObj.getAddedGroupMembers().isEmpty())
                        || (xcapDiffDocObj.getRemovedGroupMembers() != null && !xcapDiffDocObj.getRemovedGroupMembers().isEmpty())
                        || (xcapDiffDocObj.getModifiedGrpMembers() != null && !xcapDiffDocObj.getModifiedGrpMembers().isEmpty())
                        || (xcapDiffDocObj.getGroupName() != null && !xcapDiffDocObj.getGroupName().isEmpty())
                        || (xcapDiffDocObj.getGroupMemCount() > 0)
                        || (xcapDiffDocObj.getAvatar() != null)
                        || (xcapDiffDocObj.getVideoPermission() != null))) {
                    if (xcapDiffDocObj.isOsmListChanged()) {
                        count++;
                    }
                    count++;
                } else if ((xcapDiffDocObj.getAddedTargetList() != null && !xcapDiffDocObj.getAddedTargetList().isEmpty()) ||
                        (xcapDiffDocObj.getModifiedTargetList() != null && !xcapDiffDocObj.getModifiedTargetList().isEmpty()) ||
                        (xcapDiffDocObj.getRemovedTargetList() != null && !xcapDiffDocObj.getRemovedTargetList().isEmpty())) {
                    if (protocol >= KnConstants.PROTOCOL_VERSION_13_X) {
                        count++;
                    }
                } else if ((xcapDiffDocObj.getAddedDestList() != null && !xcapDiffDocObj.getAddedDestList().isEmpty()) ||
                        (xcapDiffDocObj.getModifiedDestList() != null && !xcapDiffDocObj.getModifiedDestList().isEmpty()) ||
                        (xcapDiffDocObj.getRemovedDestList() != null && !xcapDiffDocObj.getRemovedDestList().isEmpty()) ||
                        (xcapDiffDocObj.getAddedEmergAttributes() != null) ||
                        (xcapDiffDocObj.getModifiedEmergAttributes() != null) ||
                        (xcapDiffDocObj.getRemovedEmergAttributes() != null)) {
                    if (protocol >= KnConstants.PROTOCOL_VERSION_13_X) {
                        count++;
                    }
                } else if ((xcapDiffDocObj.getAddedAddlTGList() != null && !xcapDiffDocObj.getAddedAddlTGList().isEmpty()) ||
                        (xcapDiffDocObj.getModifyAddlTGList() != null && !xcapDiffDocObj.getModifyAddlTGList().isEmpty()) ||
                        (xcapDiffDocObj.getRemovedAddlTGList() != null && !xcapDiffDocObj.getRemovedAddlTGList().isEmpty())) {
                    if (protocol >= KnConstants.PROTOCOL_VERSION_13_X) {
                        count++;
                    }
                } else if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.ADD.value() ||
                        xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REPLACE.value() ||
                        xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()) {
                    count++;
                }
            }
        } else if (xcapDiffDocObsList != null) {
            count++;
        }
        knLogger.debug(methodName, "Total count of docTypeList: ", count);
        return count;
    }

    private int countChangeLog(Collection<KnXcapDiffDocDTO> xcapDiffDocObsFinalList) {
        String methodName = "countChangeLog(Collection<KnXcapDiffDocDTO>)";
        Collection<KnXcapDiffDocDTO> xcapDiffDocObsList = new HashSet<>(xcapDiffDocObsFinalList);
        knLogger.debug(methodName, "xcapDiffDocObsFinalList: ", xcapDiffDocObsFinalList, " xcapDiffDocObsList: ", xcapDiffDocObsList);
        int count = 0;
        for (KnXcapDiffDocDTO xcapDiffDocObj : xcapDiffDocObsList) {
            if (xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.ADD.value() ||
                    xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REPLACE.value() ||
                    xcapDiffDocObj.getDocChangeType() == KnXcapNotifyConstants.DIRECTORY_CHG_LOG_TYPE.REMOVE.value()) {
                count = 1;
            } else {
                knLogger.error(methodName, "Unknown Doc Change Type");
                break;
            }
        }
        if (xcapDiffDocObsList.isEmpty()) {
            count = 1;
        }
        knLogger.debug(methodName, "Total count of docTypeList: ", count);
        return count;
    }

    public void sendMicroserviceNotificationforEtagNotify(LinkedHashSet<String> mdns) {
        String methodName = "sendMicroserviceNotificationforEtagNotify(knMCSNotifyDTO)";
        knLogger.info(methodName, FLOW_TAG + " STEP-OUT1 Downstream etag emit requested. source=MCS watcherCount="
                + (mdns != null ? mdns.size() : 0) + " sampleMdns=" + summarizeMdns(mdns));
        KnMCSXCAPNotifier.sendEtagNotification(mdns);
    }

    public void sendXcapDiffMicroserviceNotificationforEtagNotify(List<KnXcapDiffNotifyDTO> knXcapDiffNotifyDTOs) {
        String methodName = "sendXcapDiffMicroserviceNotificationforEtagNotify(List<KnXcapDiffNotifyDTO>)";
        LinkedHashSet<String> mdnSet = knXcapDiffNotifyDTOs.stream()
                .map(e -> getMDNFromURI(e.getDirURI()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        knLogger.info(methodName, FLOW_TAG + " STEP-OUT1 Downstream etag emit requested. source=DiffList watcherCount="
                + mdnSet.size() + " docPayloadCount=" + knXcapDiffNotifyDTOs.size()
                + " sampleMdns=" + summarizeMdns(mdnSet));
        KnMCSXCAPNotifier.sendEtagNotification(mdnSet);

        Collection<KnXcapDiffDocDTO> docDiffObj = knXcapDiffNotifyDTOs.stream()
                .filter(dto -> dto.getDocDiffObj() != null && !dto.getDocDiffObj().isEmpty())
                .flatMap(dto -> dto.getDocDiffObj().stream())
                .collect(Collectors.toList());

        //findOtherEtagChanges(docDiffObj);
    }

    public void sendXcapDiffDirMicroserviceNotificationforEtagNotify(List<KnXcapDiffDirChgNotifyDTO> knXcapDiffDirChgNotifyDTOs) {
        String methodName = "sendXcapDiffDirMicroserviceNotificationforEtagNotify(List<KnXcapDiffDirChgNotifyDTO>)";
        LinkedHashSet<String> mdnSet = knXcapDiffDirChgNotifyDTOs.stream()
                .map(e -> getMDNFromURI(e.getDirURI()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        long inlineDocPayloadCount = knXcapDiffDirChgNotifyDTOs.stream()
                .filter(dto -> dto.getDocDiffObj() != null && !dto.getDocDiffObj().isEmpty())
                .count();
        knLogger.info(methodName, FLOW_TAG + " STEP-OUT1 Downstream etag emit requested. source=DirChg watcherCount="
                + mdnSet.size() + " bundleCount=" + knXcapDiffDirChgNotifyDTOs.size()
                + " inlineDocPayloadCount=" + inlineDocPayloadCount
                + " sampleMdns=" + summarizeMdns(mdnSet));
        KnMCSXCAPNotifier.sendEtagNotification(mdnSet);

        Collection<KnXcapDiffDocDTO> docDiffObj = knXcapDiffDirChgNotifyDTOs.stream()
                .filter(dto -> dto.getDocDiffObj() != null && !dto.getDocDiffObj().isEmpty())
                .flatMap(dto -> dto.getDocDiffObj().stream())
                .collect(Collectors.toList());

        //findOtherEtagChanges(docDiffObj);
    }

    private void findOtherEtagChanges(Collection<KnXcapDiffDocDTO> docDiffObj) {
        String methodName = "findOtherEtagChanges(KnXcapDiffDirChgNotifyDTO)";
        LinkedHashSet<String> mdnSetForDoc = new LinkedHashSet<>();
        for (KnXcapDiffDocDTO knXcapDiffDocDTO : docDiffObj) {
            String uri = knXcapDiffDocDTO.getDocumentSelector();
            String seperator = "_";
            if (checkIfDocChage(uri)) {
                String mdn = getMDNFromURI(uri);
                String groupId = null;
                if (knXcapDiffDocDTO.getDocUri() != null) {
                    String[] number = knXcapDiffDocDTO.getDocUri().split("/");
                    groupId = number[number.length - 1].split("\\.")[0];
                }
                String docEtag = knXcapDiffDocDTO.getDocEtag();
                if (uri.contains("kn-authorization-list")) {
                    mdnSetForDoc.add(mdn + seperator + "kn-authorization-list" + seperator + docEtag);
                }
                if (uri.contains("kn-corp-resource-lists")) {
                    mdnSetForDoc.add(mdn + seperator + "kn-corp-resource-lists" + seperator + docEtag);
                }
                if (uri.contains("kn-emergency-config")) {
                    mdnSetForDoc.add(mdn + seperator + "kn-emergency-config" + seperator + docEtag);
                }
                if (uri.contains("kn-tgsc-list")) {
                    mdnSetForDoc.add(mdn + seperator + "kn-tgsc-list" + seperator + docEtag);
                }
                if (uri.contains("kn-tgss-list")) {
                    mdnSetForDoc.add(mdn + seperator + "kn-tgss-list" + seperator + docEtag);
                }
                if (uri.contains("org.openmobilealliance.group-usage-list")) {
                    mdnSetForDoc.add(mdn + seperator + "group-usage-list" + seperator + docEtag);
                }
                if (uri.contains("kn-subscriber-config")) {
                    mdnSetForDoc.add(mdn + seperator + "kn-subscriber-config" + seperator + docEtag);
                }
                if (uri.contains("kn-corp-groups")) {
                    mdnSetForDoc.add(groupId + seperator + "kn-corp-groups" + seperator + docEtag);
                }
            }
        }

        knLogger.info(methodName, "mdnSetForDoc - ", mdnSetForDoc);
    }

    private List<String> summarizeMdns(Collection<String> mdns) {
        if (mdns == null || mdns.isEmpty()) {
            return Collections.emptyList();
        }
        return mdns.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(mdn -> !mdn.isEmpty())
                .limit(5)
                .collect(Collectors.toList());
    }
}

