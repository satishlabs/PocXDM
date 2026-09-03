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
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnSendNotification implements Runnable {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSendNotification.class);

    private KnXcapDiffNotifyDTO knXcapDiffNotifyDTO;
    private KnXcapDiffNotifier xcapDiffNotifier;

    private KnNotificationKeyDTO seqId;

    private Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap;

    private Map<String, Set<String>> baseMdnsMap;


    public KnSendNotification(KnNotificationKeyDTO seqId, KnXcapDiffNotifyDTO knXcapDiffNotifyDTO, Map<String,
            KnXDMSubsProvDTO> mdnSubsInfoMap, Map<String, Set<String>> baseMdnsMap) {
        this.seqId = seqId;
        this.knXcapDiffNotifyDTO = knXcapDiffNotifyDTO;
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        this.mdnSubsInfoMap = mdnSubsInfoMap;
        this.baseMdnsMap = baseMdnsMap;
    }

    @Override
    public void run() {
        String methodName = "run()";
        knLogger.debug(methodName, "run method", knXcapDiffNotifyDTO);
        boolean isSuccess = false;
        if (seqId.getMsgType() == 1) {
            isSuccess = xcapDiffNotifier.generateDirNotification(knXcapDiffNotifyDTO, mdnSubsInfoMap, baseMdnsMap);
        } else {
            List<KnXcapDiffNotifyDTO> knXcapDiffNotifyDTOArrayList = new ArrayList<>();
            knXcapDiffNotifyDTOArrayList.add(knXcapDiffNotifyDTO);
            isSuccess = xcapDiffNotifier.generateDirNotification(knXcapDiffNotifyDTOArrayList, mdnSubsInfoMap, baseMdnsMap);
        }
        if (isSuccess) {
            xcapDiffNotifier.cleanUpRecord(seqId);
        } else {
            // Revert to PENDING so the row can be retried on the next poll cycle
            // without waiting for a service restart (startup recovery only).
            knLogger.warn(methodName, "Send failed; reverting row to PENDING for retry. CID=" + seqId.getCid());
            xcapDiffNotifier.revertRecordsToPending(Collections.singletonList(seqId));
        }
        knLogger.info(methodName, "CID :", seqId.getCid(), "isSuccess : ", isSuccess);
    }
}
