/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpStatsController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpStatsManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnGroupStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubsStatsRespDTO;

public class KnCorpStatsManager implements ICorpStatsManager {

    private ICorpStatsController statsController;

    KnCorpStatsManager(){
        statsController = KnCorpBORegistry.createCorpStatsController();
    }


    @Override
    public KnGroupStatsRespDTO getGroupStats(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        corpInfoDTO.setEntityId(KnEntityTypes.CORP_STATS_MANAGER);
        corpInfoDTO.setOperationType(KnOperationTypes.GET_GROUP_STATS);
        corpInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return statsController.getGroupStats(corpInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        subscDistDTO.setEntityId(KnEntityTypes.CORP_STATS_MANAGER);
        subscDistDTO.setOperationType(KnOperationTypes.GET_SUBSCRIBER_STATS);
        subscDistDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return statsController.getSubscriberStats(subscDistDTO, persisterTxn);
    }

    @Override
    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, KnPersisterTxn persisterTxn) {
        /*corpInfoDTO.setEntityId(KnEntityTypes.CORP_STATS_MANAGER);
        corpInfoDTO.setOperationType(KnOperationTypes.GET_DEVICE_STATS);
        corpInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);*/
        return statsController.getDeviceStats(corpId, persisterTxn);    }
}
