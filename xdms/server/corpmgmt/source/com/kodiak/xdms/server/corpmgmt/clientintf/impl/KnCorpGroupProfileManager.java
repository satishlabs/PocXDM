/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpGroupProfileController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGorupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeleteBulkCorpGrpDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupProfileResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpGroupProfileManager implements ICorpGroupProfileManager{
	 private ICorpGroupProfileController corpGroupProfileController;

	    public KnCorpGroupProfileManager(){
	        corpGroupProfileController= KnCorpBORegistry.createCorpGroupProfileController();
	    }
	@Override
	public KnCorpResponseDTO createGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                KnPersisterTxn persisterTxn) {
		groupProfileIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
		groupProfileIPDto.setOperationType(KnOperationTypes.CREATE_CORP_GROUP_PROFILE);
		groupProfileIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpGroupProfileController.createGroupProfile(groupProfileIPDto, persisterTxn);
	}
	@Override
	public KnCorpGroupProfileResponseDTO getGroupProfileList(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                             KnPersisterTxn persisterTxn) {
		groupProfileIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
		groupProfileIPDto.setOperationType(KnOperationTypes.GET_GROUP_PROFILE_LIST);
		groupProfileIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpGroupProfileController.getGroupProfileList(groupProfileIPDto, persisterTxn);
	}
	@Override
	public KnCorpGroupProfileResponseDTO getGroupProfileDetails(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                                KnPersisterTxn persisterTxn) {
		groupProfileIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
		groupProfileIPDto.setOperationType(KnOperationTypes.GET_GROUP_PROFILE_DETAILS);
		groupProfileIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpGroupProfileController.getGroupProfileDetails(groupProfileIPDto, persisterTxn);
	}

	@Override
	public KnCorpGroupProfileResponseDTO searchGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		groupProfileIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
		groupProfileIPDto.setOperationType(KnOperationTypes.SEARCH_GROUP_PROFILE);
		groupProfileIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpGroupProfileController.searchGroupProfile(groupProfileIPDto, persisterTxn);
	}

	@Override
	public KnCorpResponseDTO modifyGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		groupProfileIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
		groupProfileIPDto.setOperationType(KnOperationTypes.MODIFY_GROUP_PROFILE);
		groupProfileIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpGroupProfileController.modifyGroupProfile(groupProfileIPDto, persisterTxn);
	}

	@Override
	public KnCorpResponseDTO deleteGrpProfileGroupList(KnIPDeleteBulkCorpGrpDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		groupProfileIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
		groupProfileIPDto.setOperationType(KnOperationTypes.DELETE_GROUP_PROFILE_GROUP_LIST);
		groupProfileIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpGroupProfileController.deleteGrpProfileGroupList(groupProfileIPDto, persisterTxn);
	}

	@Override
	public KnCorpResponseDTO deleteGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		groupProfileIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
		groupProfileIPDto.setOperationType(KnOperationTypes.DELETE_GROUP_PROFILE);
		groupProfileIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
		return corpGroupProfileController.deleteGroupProfile(groupProfileIPDto, persisterTxn);
	}
}
