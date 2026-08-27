package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpPTTSettingController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpPTTSettingManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpPTTSettingManager implements ICorpPTTSettingManager {

    private ICorpPTTSettingController corpPTTSettingController;

    public KnCorpPTTSettingManager(){
        corpPTTSettingController = KnCorpBORegistry.createCorpPTTSettingController();
    }

    @Override
    public KnCorpResponseDTO createPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.CREATE_CORP_PTTSETTING_DOC);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.createPTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getPTTSettingDocList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.GET_ALL_CORP_PTTSETTING_DOC_LIST);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.getAllPTTSettingDocList(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpPTTSettingDTO getPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.GET_CORP_PTTSETTING_DOC);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.getPTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO setDefaultPttSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.SET_DEFAULT_PTTSETTING_DOC);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.setDefaultPttSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO deletePTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.DELETE_CORP_PTTSETTING_DOC);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.deletePTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO assignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.ASSIGN_PTTSETTING_DOC_TO_HIERARCHY);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.assignPttSettingToHierarchy(ipCorpPTTSettingDTO, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO unassignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.UNASSIGN_PTTSETTING_DOC_TO_HIERARCHY);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.unassignPttSettingToHierarchy(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO assignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.ASSIGN_PTTSETTING_DOC_TO_MDNLIST);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.assignPttSettingDocToMdns(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO unassignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.UNASSIGN_PTTSETTING_DOC_TO_MDNLIST);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.unassignPttSettingDocToMdns(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getPttSettingDocMdnList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.GET_MDN_LIST_FOR_PTT_SETTING_DOC);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.getPttSettingDocMdnList(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getMDNCountForPttSettingDocID(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.GET_MDN_COUNT_FOR_PTT_SETTINGID);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.getMDNCountForPttSettingDocID(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO assignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.ASSIGN_PTTSETTING_DOC_TO_CORP);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.assignPttSettingToCorp(ipCorpPTTSettingDTO, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO unassignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.UNASSIGN_PTTSETTING_DOC_TO_CORP);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.unassignPttSettingToCorp(ipCorpPTTSettingDTO, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO modifyPTTSettingTemplate(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        ipCorpPTTSettingDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        ipCorpPTTSettingDTO.setOperationType(KnOperationTypes.CREATE_CORP_PTTSETTING_DOC);
        ipCorpPTTSettingDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpPTTSettingController.modifyPTTSettingTemplate(ipCorpPTTSettingDTO, persisterTxn);
    }
}
