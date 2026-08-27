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
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnXcapSendNotification implements Runnable {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapSendNotification.class);

    private KnXcapDiffDirChgNotifyDTO knXcapDiffDirChgNotifyDTO;
    private KnXcapDiffNotifier xcapDiffNotifier;

    private KnNotificationKeyDTO seqId;

    private Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap;

    private Map<String, Set<String>> baseMdnsMap;


    public KnXcapSendNotification(KnNotificationKeyDTO seqId, KnXcapDiffDirChgNotifyDTO knXcapDiffDirChgNotifyDTO,
                                  Map<String,KnXDMSubsProvDTO> mdnSubsInfoMap, Map<String, Set<String>> baseMdnsMap) {
        this.seqId = seqId;
        this.knXcapDiffDirChgNotifyDTO = knXcapDiffDirChgNotifyDTO;
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        this.mdnSubsInfoMap = mdnSubsInfoMap;
        this.baseMdnsMap = baseMdnsMap;
    }

    @Override
    public void run() {
        String methodName = "run()";
        knLogger.debug(methodName, "run method", knXcapDiffDirChgNotifyDTO);
        List<KnXcapDiffDirChgNotifyDTO> knXcapDiffDirChgNotifyDTOList = new ArrayList<>();
        knXcapDiffDirChgNotifyDTOList.add(knXcapDiffDirChgNotifyDTO);
        boolean isSuccess = xcapDiffNotifier.sendXcapDiffNotifications(knXcapDiffDirChgNotifyDTOList, mdnSubsInfoMap, null, baseMdnsMap);
        if (isSuccess) {
            xcapDiffNotifier.cleanUpRecord(seqId);
        } else {
            knLogger.warn(methodName, "Cleanup skipped because send failed. CID :" + seqId.getCid());
        }
        knLogger.info(methodName, "CID :", seqId.getCid(), "isSuccess : ", isSuccess);
    }
}
