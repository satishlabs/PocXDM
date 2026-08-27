/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubsStatsRespDTO;

public interface ICorpXdmStatsDAO {

    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO corpInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

}