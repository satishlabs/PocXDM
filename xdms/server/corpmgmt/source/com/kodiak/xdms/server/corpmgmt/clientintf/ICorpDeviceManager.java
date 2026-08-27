/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.common.dao.*;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dto.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeviceInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceListRespDTO;

public interface ICorpDeviceManager {

    public KnCorpDeviceListRespDTO getDeviceList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO subscDistDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException;

}
