/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnGroupStatsRespDTO;


import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubsStatsRespDTO;

public interface ICorpStatsManager {

    public KnGroupStatsRespDTO getGroupStats(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO subscDistDTO, KnPersisterTxn persisterTxn);

    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, KnPersisterTxn persisterTxn);

}
