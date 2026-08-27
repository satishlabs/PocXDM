/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnUserProfileAssignedDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpUserProfileListRespDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ICorpUserProfileManager {

    public KnCorpResponseDTO createUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

	public KnCorpResponseDTO getUserProfileDetails(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn);

    public KnCorpUserProfileListRespDTO getUserProfileList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);
    
    public KnCorpUserProfileListRespDTO getUserProfileListByName(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);
    
    public KnCorpUserProfileListRespDTO getSubscriberUserProfileList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO assignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpUserProfileListRespDTO getUserProfileSubscriberList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateDefaultProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getProfileMdnByUPId(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyCBUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getProfileMdnEtag(List<String> userProfileIds, String corpId, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO unassignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateImpactedTuPerms(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO userProfileNotfication(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO userProfileNotficationForUpm(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteMcpttPermConfig(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getAsyncOpStatus(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn);

    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userprofileIds,String corpId, boolean readOnly, KnPersisterTxn persisterTxn);
}
