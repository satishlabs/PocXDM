/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCDATAController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubMCDATAManager;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;

public class KnPubMCDATAManager implements IPubMCDATAManager {

    private IPubMCDATAController mcdataController;

    public KnPubMCDATAManager() {
        mcdataController = KnPubBORegistry.createMCDATAController();
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataUEConfig(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCDATA_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCDATA_UE_CONFIG);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcdataController.getMCDataUEConfig(ipmcsDTO, persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataUserProfile(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCDATA_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCDATA_USER_PROFILE);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcdataController.getMCDataUserProfile(ipmcsDTO, persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataServiceConfig(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCDATA_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCDATA_SERVICE_CONFIG);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcdataController.getMCDataServiceConfig(ipmcsDTO, persisterTxn);
    }

}
