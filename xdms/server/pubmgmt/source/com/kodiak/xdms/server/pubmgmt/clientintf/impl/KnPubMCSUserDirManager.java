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
import com.kodiak.xdms.server.pubmgmt.business.IPubMCSUserDirController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubMCSUserDirManager;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;


public class KnPubMCSUserDirManager implements IPubMCSUserDirManager {

    private IPubMCSUserDirController mcsUserDirController;

    public KnPubMCSUserDirManager(){mcsUserDirController= KnPubBORegistry.createMCSUserDirController();}

    @Override
    public KnMCSXCAPRespDTO getMCSUserDir(KnIPMCSDTO ipmcsDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        ipmcsDTO.setEntityId(KnEntityTypes.MCSUSERDIR_MANAGER);
        ipmcsDTO.setOperationType(KnOperationTypes.GET_MCS_USER_DIR);
        ipmcsDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return mcsUserDirController.getMCSUserDir(ipmcsDTO, persisterTxn);
    }
}
