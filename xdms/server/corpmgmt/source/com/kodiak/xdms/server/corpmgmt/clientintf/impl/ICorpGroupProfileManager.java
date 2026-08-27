/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGorupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeleteBulkCorpGrpDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupProfileResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

public interface ICorpGroupProfileManager {
	
	KnCorpResponseDTO createGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn);
	
	KnCorpGroupProfileResponseDTO getGroupProfileList(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn);
	
	KnCorpGroupProfileResponseDTO getGroupProfileDetails(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn);

	KnCorpGroupProfileResponseDTO searchGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO modifyGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO deleteGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO deleteGrpProfileGroupList(KnIPDeleteBulkCorpGrpDTO groupProfileIPDto, KnPersisterTxn persisterTxn);
}
