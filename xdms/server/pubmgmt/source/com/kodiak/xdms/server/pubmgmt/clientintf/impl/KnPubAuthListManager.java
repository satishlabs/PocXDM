/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.pubmgmt.business.IPubAuthInfoController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.clientintf.IpubAuthManger;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnEmergencyConfigDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubAuthListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;

/**
 * Created by schandra on 19-12-2017.
 */
public class KnPubAuthListManager implements IpubAuthManger{

    private IPubAuthInfoController pubAuthInfoController;

    public KnPubAuthListManager() {
        pubAuthInfoController = KnPubBORegistry.createPubAuthInfoController();;
    }

    @Override
    public KnIPPubAuthListDTO getAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        authListDTO.setEntityId(KnEntityTypes.AUTHLIST_MANAGER);
        authListDTO.setOperationType(KnOperationTypes.GET_AUTHLIST);
        authListDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return pubAuthInfoController.getAuthorizationList(authListDTO, persisterTxn);
    }

    @Override
    public KnOpPubResponse updateAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        authListDTO.setEntityId(KnEntityTypes.AUTHLIST_MANAGER);
        authListDTO.setOperationType(KnOperationTypes.UPDATE_AUTHLIST);
        authListDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return pubAuthInfoController.updateAuthorizationList(authListDTO, persisterTxn);
    }

    @Override
    public KnEmergencyConfigDocDTO getEmergencyConfigDoc(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        authListDTO.setEntityId(KnEntityTypes.AUTHLIST_MANAGER);
        authListDTO.setOperationType(KnOperationTypes.GET_EMERGENCY_CONFIG_DOC);
        authListDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return pubAuthInfoController.getEmergencyConfigDoc(authListDTO, persisterTxn);
    }
}
