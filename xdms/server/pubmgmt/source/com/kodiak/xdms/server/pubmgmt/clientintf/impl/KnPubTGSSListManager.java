/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.pubmgmt.business.ITGSSController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.clientintf.IpubTGSSListManger;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPTGSSListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;
/**
 * Created by venkata sudhakar talluri on 28-12-2018
 */

public class KnPubTGSSListManager implements IpubTGSSListManger {

    ITGSSController pubAuthInfoController;

    public KnPubTGSSListManager() {
        pubAuthInfoController = KnPubBORegistry.createTGSSController();
    }

    @Override
    public KnIPTGSSListDTO getTGSSList(KnIPTGSSListDTO tgssListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        tgssListDTO.setEntityId(KnEntityTypes.TGSSLIST_MANAGER);
        tgssListDTO.setOperationType(KnOperationTypes.GET_TGSSLIST);
        tgssListDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return pubAuthInfoController.getTGSSList(tgssListDTO, persisterTxn);

    }

    @Override
    public KnOpPubResponse updateTGSSList(KnIPTGSSListDTO tgssListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        tgssListDTO.setEntityId(KnEntityTypes.TGSSLIST_MANAGER);
        tgssListDTO.setOperationType(KnOperationTypes.UPDATE_TGSSLIST);
        tgssListDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return pubAuthInfoController.updateTGSSList(tgssListDTO, persisterTxn);
    }

    @Override
    public KnOpPubResponse deleteTGSSList(KnIPTGSSListDTO tgssListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
            tgssListDTO.setEntityId(KnEntityTypes.TGSSLIST_MANAGER);
            tgssListDTO.setOperationType(KnOperationTypes.DELETE_TGSSLIST);
            tgssListDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
            return pubAuthInfoController.deleteTGSSList(tgssListDTO, persisterTxn);
        }
}
