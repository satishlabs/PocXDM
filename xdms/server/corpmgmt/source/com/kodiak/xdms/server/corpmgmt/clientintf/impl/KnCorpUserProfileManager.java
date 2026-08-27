/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpUserProfileController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpUserProfileManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnUserProfileAssignedDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpUserProfileListRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Collection;
import java.util.List;

public class KnCorpUserProfileManager implements ICorpUserProfileManager {

    private ICorpUserProfileController corpUserProfileController;

    public KnCorpUserProfileManager(){
        corpUserProfileController= KnCorpBORegistry.createCorpUserProfileController();
    }

    @Override
    public KnCorpResponseDTO createUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.CREATE_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.createUserProfile(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.UPDATE_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.updateUserProfile(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO deleteUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.DELETE_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.deleteUserProfile(ipUserProfileDTO, persisterTxn);
    }
	
	    @Override
    public KnCorpResponseDTO getUserProfileDetails(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.GET_USER_PROFILE_DETAILS);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.getUserProfileDetails(ipUserProfileDTO, readOnly, persisterTxn);
    }

    @Override
    public KnCorpUserProfileListRespDTO getUserProfileList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.GET_USER_PROFILE_LIST);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.getUserProfileList(ipUserProfileDTO, persisterTxn);
    }

	@Override
	public KnCorpUserProfileListRespDTO getUserProfileListByName(KnIPUserProfileDTO ipUserProfileDTO,
                                                                 KnPersisterTxn persisterTxn) {
		ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
		ipUserProfileDTO.setOperationType(KnOperationTypes.GET_USER_PROFILE_LIST_BY_NAME);
		ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpUserProfileController.getUserProfileListByName(ipUserProfileDTO, persisterTxn);
	}

	@Override
	public KnCorpUserProfileListRespDTO getSubscriberUserProfileList(KnIPUserProfileDTO ipUserProfileDTO,
                                                                     KnPersisterTxn persisterTxn) {
		ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
		ipUserProfileDTO.setOperationType(KnOperationTypes.GET_SUBSCRIBER_USER_PROFILE_LIST);
		ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpUserProfileController.getSubscriberUserProfileList(ipUserProfileDTO, persisterTxn);
	}

    @Override
    public KnCorpResponseDTO assignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.ASSIGN_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.assignUserProfile(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpUserProfileListRespDTO getUserProfileSubscriberList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.GET_USER_PROFILE_SUBSCRIBERLIST);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.getUserProfileSubscriberList(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateDefaultProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.UPDATE_DEFAULT_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.updateDefaultProfile(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getProfileMdnByUPId(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.GET_PROFILE_MDN_BY_UPM_ID);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.getProfileMdnByUPId(ipUserProfileDTO, readOnly, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO modifyCBUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.UPDATE_CB_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.modifyCBUserProfile(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getProfileMdnEtag(List<String> userProfileIds, String corpId, KnPersisterTxn persisterTxn) {
        return corpUserProfileController.getProfileMdnEtag(userProfileIds, corpId, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO unassignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.UNASSIGN_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.unassignUserProfile(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateImpactedTuPerms(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.UPDATE_IMPACTED_TU_PERMS);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.updateImpactedTuPerms(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO userProfileNotfication(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.USER_PROFILE_NOTIFICATION);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.userProfileNotfication(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO userProfileNotficationForUpm(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.USER_PROFILE_NOTIFICATION);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.userProfileNotficationForUpm(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO deleteMcpttPermConfig(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.UNASSIGN_USER_PROFILE);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.deleteMcpttPermConfig(ipUserProfileDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getAsyncOpStatus(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        ipUserProfileDTO.setEntityId(KnEntityTypes.CORP_USER_PROFILE_MANAGER);
        ipUserProfileDTO.setOperationType(KnOperationTypes.GET_ASYNC_OP_STATUS);
        ipUserProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return corpUserProfileController.getAsyncOpStatus(ipUserProfileDTO, persisterTxn);
    }
    @Override
    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userprofileIds, String corpId, boolean readOnly, KnPersisterTxn persisterTxn){
        return corpUserProfileController.getUserProfileSubsCount(userprofileIds, corpId, readOnly, persisterTxn);
    }

}
