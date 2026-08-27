/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCVideoController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubMCVideoManager;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;

public class KnPubMCVideoManager implements IPubMCVideoManager {

    private IPubMCVideoController mcvideoController;

    public KnPubMCVideoManager() {
        mcvideoController = KnPubBORegistry.createMCVideoController();
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoUEConfig(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCVIDEO_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCVIDEO_UE_CONFIG);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcvideoController.getMCVideoUEConfig(ipmcsDTO, persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoUserProfile(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCVIDEO_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCVIDEO_USER_PROFILE);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcvideoController.getMCVideoUserProfile(ipmcsDTO, persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoServiceConfig(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCVIDEO_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCVIDEO_SERVICE_CONFIG);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcvideoController.getMCVideoServiceConfig(ipmcsDTO, persisterTxn);
    }

}
