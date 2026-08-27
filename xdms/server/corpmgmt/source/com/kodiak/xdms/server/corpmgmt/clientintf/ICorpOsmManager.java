/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.common.commdto.response.KnXDMCorpOSMListRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPOsmDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMGroupListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListDetailsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

public interface ICorpOsmManager {

    public KnCorpResponseDTO createOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn);

    public KnCorpOSMInfoListRespDTO getOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn);

    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetails(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO assignOSMIdToGroup(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn);

    public KnCorpOSMGroupListRespDTO getOSMGroupList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn);
}
