/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpOSMController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpOsmManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPOsmDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMGroupListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListDetailsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpOsmManager implements ICorpOsmManager {

    private ICorpOSMController corpOSMController;

    public KnCorpOsmManager(){
        corpOSMController = KnCorpBORegistry.createCorpOSMController();
    }

    @Override
    public KnCorpResponseDTO createOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn){
        ipOsmDTO.setEntityId(KnEntityTypes.CORP_OSM_MANAGER);
        ipOsmDTO.setOperationType(KnOperationTypes.CREATE_OSM_LIST);
        ipOsmDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpOSMController.createOSMList(ipOsmDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn){
        ipOsmDTO.setEntityId(KnEntityTypes.CORP_OSM_MANAGER);
        ipOsmDTO.setOperationType(KnOperationTypes.UPDATE_OSM_LIST);
        ipOsmDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpOSMController.updateOSMList(ipOsmDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO deleteOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn){
        ipOsmDTO.setEntityId(KnEntityTypes.CORP_OSM_MANAGER);
        ipOsmDTO.setOperationType(KnOperationTypes.DELETE_OSM_LIST);
        ipOsmDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpOSMController.deleteOSMList(ipOsmDTO, persisterTxn);
    }

    @Override
    public KnCorpOSMInfoListRespDTO getOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn){
        ipOsmDTO.setEntityId(KnEntityTypes.CORP_OSM_MANAGER);
        ipOsmDTO.setOperationType(KnOperationTypes.GET_OSM_LIST);
        ipOsmDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpOSMController.getOSMList(ipOsmDTO, persisterTxn);
    }

    @Override
    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetails(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn){
        ipOsmDTO.setEntityId(KnEntityTypes.CORP_OSM_MANAGER);
        ipOsmDTO.setOperationType(KnOperationTypes.GET_OSM_LIST_DETAILS);
        ipOsmDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpOSMController.getOSMListDetails(ipOsmDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO assignOSMIdToGroup(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        ipOsmDTO.setEntityId(KnEntityTypes.CORP_OSM_MANAGER);
        ipOsmDTO.setOperationType(KnOperationTypes.ASSIGN_OSM_TO_GROUP);
        ipOsmDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpOSMController.assignOSMIdToGroup(ipOsmDTO, persisterTxn);
    }

    @Override
    public KnCorpOSMGroupListRespDTO getOSMGroupList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        ipOsmDTO.setEntityId(KnEntityTypes.CORP_OSM_MANAGER);
        ipOsmDTO.setOperationType(KnOperationTypes.GET_OSM_GROUP_LIST);
        ipOsmDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpOSMController.getOSMGroupList(ipOsmDTO, persisterTxn);
    }
}
