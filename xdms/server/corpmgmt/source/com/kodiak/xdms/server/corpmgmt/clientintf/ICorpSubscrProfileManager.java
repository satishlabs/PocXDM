/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.common.commdto.response.KnCorpGetCorpFSResponse;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;

/**
 * Created by asanjiv on 11/2/2016.
 */
public interface ICorpSubscrProfileManager {

    public KnSubscrFeatureSetRespDTO getAllCorpSubscrFeatureSets(KnIPCorpAuthInfoDTO ipCorpAuthInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateCorpAdminFS(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO cloneCorpAdminFS(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGetCorpFSResponse getCorporateFS(KnIPSubscriberInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO getActivationCode(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getCorpSubscriberDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO switchConvergedClient(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateCorpSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO validateSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnFWException, KnException;

    public KnCorpResponseDTO validateGetSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnFWException, KnException;

    public KnCorpResponseDTO getCorpSubsUserProfile(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO resetCorpSubsUserPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO resendCorpSubsVerificationEmail(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO setTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO upmSetTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpUserPermissionRespDTO getTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpUserPermissionRespDTO getSubsTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpUserPermissionRespDTO getAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO setSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO upmSetSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn);

    public KnEmergUserDestRespDTO getUserEmergDest(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpUserEmergencyAttributesRespDTO getSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateSubsAliasEntities(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO generateTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO sendTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getSubsEmergencyDetails(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO addBulkGroupsToSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnBulkGroupCloningDTO cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO cloneBulkGroupsToSubscriberProcessing(KnBulkGroupCloningDTO bulkGroupCloningDTO, KnPersisterTxn persisterTxn);


    public KnCorpResponseDTO createSubsATGScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

    public KnCorpBulkGroupJobRespDTO contactPairingForBulkGroupProcess(KnIPCorpDispatchGrpMemInfoDto groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getSubsGroupMemberShipDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpUserPermissionRespDTO getAllAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn, boolean upmFlag);

    KnCorpResponseDTO sendTrkMaterial(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO setUserProfileEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO setBulkTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO allocateSubs(KnIPAllocateSubscriberDTO allocateSubscriberDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO unAllocateSubs(KnIPUnAllocateSubscriberDTO unAllocateSubscriberDTO, KnPersisterTxn persisterTxn);

}
