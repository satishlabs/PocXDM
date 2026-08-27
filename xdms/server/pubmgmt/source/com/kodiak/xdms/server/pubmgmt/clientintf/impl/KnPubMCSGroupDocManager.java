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
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCSGroupController;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubMCSGroupDocManager;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;

public class KnPubMCSGroupDocManager implements IPubMCSGroupDocManager {

    private IPubMCSGroupController mcsGroupController;

    public KnPubMCSGroupDocManager() {
        mcsGroupController = KnPubBORegistry.createMCSGroupController();
    }
    @Override
    public KnMCSXCAPRespDTO getMCSGroupDoc(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCSGROUP_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCS_GROUP_DOC);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcsGroupController.getMCSGroupDoc(ipmcsDTO, persisterTxn);
    }
}
