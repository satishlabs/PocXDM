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

package com.kodiak.xdms.notificationmgr.resources;

import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.resources.KnThreadExecutors;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.mcsnotifymgr.resources.KnSendMCSNotify;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.*;

public class KnXcapNotifyProcessor implements Runnable, IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapNotifyProcessor.class);
    private static ExecutorService executor = null;

    public static KnXcapNotifyProcessor instance;

    private final KnXcapDiffNotifier xcapDiffNotifier;

    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();

    private boolean startUp = true;


    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;

    static {
        executor = KnThreadExecutors.newFixedThreadExecutor(20, "XCAP_NOTIFY_SENDER");
        knLogger.info("Intiaizing executor :", executor);
    }

    private KnXcapNotifyProcessor() {
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
    }

    public static KnXcapNotifyProcessor getInstance() {
        if (instance == null) {
            instance = new KnXcapNotifyProcessor();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.info("getInstance()", "registered for Status..");
        }
        return instance;
    }

    @Override
    public void run() {
        String methodName = "run()";
        Map<KnNotificationKeyDTO, Object> record = null;
        try {
            if (getCurrentRedundancyStatus() != KnStatusMgrConstants.CARD_STATES.ACTIVE) {
                return;
            }
            if (startUp) {
                xcapDiffNotifier.updateRecord();
                startUp = false;
            }
            record = xcapDiffNotifier.pollRecord();
            if (record == null || record.isEmpty()) {
                return;
            }
            process(record);
        } catch (Throwable e) {
            knLogger.error(methodName, "Exception while processing XCAP notifications. Scheduler will continue with next run.", e);
        }
    }


    public void process(Map<KnNotificationKeyDTO, Object> record) {
        String methodName = "process(Map<KnNotificationKeyDTO, Object> record)";
        knLogger.entry(methodName);
        List<String> mdnListTogetSublistInfo = new ArrayList<>();
        Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap = new HashMap<>();
        Map<String, Set<String>> baseMdnMap = new HashMap<>();

        try {
            xcapDiffNotifier.getmdnListTogetSublistInfo(record, mdnListTogetSublistInfo);
            knLogger.debug(methodName, "mdnListTogetSublistInfo size:", mdnListTogetSublistInfo.size());
            KnXDMSubsProfileRespDTO subsInfo = genInfoUtil.selectSubsProfileInfo(mdnListTogetSublistInfo, null);
            if (subsInfo != null && subsInfo.getSubsRespDTO() != null && !subsInfo.getSubsRespDTO().isEmpty()) {
                List<KnXDMSubsProvDTO> subslist = new ArrayList<>(subsInfo.getSubsRespDTO());
                for (KnXDMSubsProvDTO itr : subslist) {
                    mdnSubsInfoMap.put(itr.getMdn().trim(), itr);
                }
                knLogger.info("mdns set in mdnSubsInfoMap size", mdnSubsInfoMap.size());
            }
            baseMdnMap = genInfoUtil.getBaseMdnsMap(mdnListTogetSublistInfo, null);
            knLogger.debug(methodName, "baseMdnMap size :", baseMdnMap.size(), " mdnSubsInfoMap size: ", mdnSubsInfoMap.size());
        } catch (Exception e) {
            knLogger.error(methodName, "Exception : ", e);
            throw new RuntimeException(e);
        }
        for (Map.Entry<KnNotificationKeyDTO, Object> itr : record.entrySet()) {
                Object payload = itr.getValue();
                if (payload instanceof KnXcapDiffDirChgNotifyDTO knXcapDiffDirChgNotifyDTO) {
                    KnXcapSendNotification knXcapSendNotification = new KnXcapSendNotification(itr.getKey(), knXcapDiffDirChgNotifyDTO, mdnSubsInfoMap, baseMdnMap);
                    executor.submit(knXcapSendNotification);
                } else if (payload instanceof KnXcapDiffNotifyDTO knXcapDiffNotifyDTO) {
                    KnSendNotification knXcapSendNotification = new KnSendNotification(itr.getKey(), knXcapDiffNotifyDTO, mdnSubsInfoMap, baseMdnMap);
                    executor.submit(knXcapSendNotification);
                } else if (payload instanceof KnMCSNotifyDTO knMCSNotifyDTO) {
                    KnSendMCSNotify knSendMCSNotify = new KnSendMCSNotify(itr.getKey(), knMCSNotifyDTO);
                    executor.submit(knSendMCSNotify);
                }
        }
        xcapDiffNotifier.cleanUpRecord(record.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        knLogger.exit(methodName);
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
}
