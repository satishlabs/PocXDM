/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeviceInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceListRespDTO;

public interface ICorpDeviceController {

    public KnCorpDeviceListRespDTO getDeviceList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO deviceInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException;

}
