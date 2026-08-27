/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.commdto.response.KnCorpGetCorpFSResponse;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpSubscrProfileController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpSubscrProfileManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

/**
 * Created by asanjiv on 11/2/2016.
 */
public class KnCorpSubscrProfileManager implements ICorpSubscrProfileManager {

    private ICorpSubscrProfileController subscrProfileController;

    KnCorpSubscrProfileManager() {
        subscrProfileController = KnCorpBORegistry.createCorpSubscrProfileController();
    }

    @Override
    public KnSubscrFeatureSetRespDTO getAllCorpSubscrFeatureSets(KnIPCorpAuthInfoDTO ipCorpAuthInfoDTO, KnPersisterTxn persisterTxn) {
        ipCorpAuthInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipCorpAuthInfoDTO.setOperationType(KnOperationTypes.GET_ALL_CORP_SUBSCR_FEATURESET);
        ipCorpAuthInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getAllCorpSubscrFeatureSets(ipCorpAuthInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateCorpAdminFS(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        ipSubscrFeatureInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscrFeatureInfoDTO.setOperationType(KnOperationTypes.UPDATE_SUBSCR_CORPADMIN_FEATURESET);
        ipSubscrFeatureInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.updateCorpAdminFS(ipSubscrFeatureInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO cloneCorpAdminFS(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        ipSubscrFeatureInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscrFeatureInfoDTO.setOperationType(KnOperationTypes.CLONE_SUBSCR_CORPADMIN_FEATURESET);
        ipSubscrFeatureInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.updateCorpAdminFS(ipSubscrFeatureInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpActivationRespDTO getActivationCode(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.GET_ACTIVATION_CODES);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getActivationCode(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getCorpSubscriberDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.GET_CORP_SUBS_DETAILS);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getCorpSubscriberDetails(ipSubscriberInfoDTO, readOnly, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO switchConvergedClient(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscrFeatureInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscrFeatureInfoDTO.setOperationType(KnOperationTypes.SWITCH_CONVERGED_CLIENT);
        ipSubscrFeatureInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.switchConvergedClient(ipSubscrFeatureInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateCorpSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.UPDATE_CORP_SUBSCRIBER);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.updateCorpSubscriber(ipSubscriberInfoDTO, persisterTxn);
    }


    @Override
    public KnCorpResponseDTO validateSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnException, KnValidationException {
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.VALIDATE_SUBS_CLIENT_SETTINGS);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        KnCorpResponseDTO knCorpResponseDTO=subscrProfileController.validateSubscrClient(ipSubscriberInfoDTO, persisterTxn);
        return knCorpResponseDTO;

    }
    @Override
    public KnCorpResponseDTO validateGetSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnException, KnValidationException {
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.VALIDATE_GET_SUBS_CLIENT_SETTINGS);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        KnCorpResponseDTO knCorpResponseDTO=subscrProfileController.validateGetSubscrClient(ipSubscriberInfoDTO, persisterTxn);
        return knCorpResponseDTO;

    }
    @Override
    public KnCorpResponseDTO getCorpSubsUserProfile(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.GET_CORP_SUBS_USER_PROFILE);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getCorpSubsUserProfile(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO resetCorpSubsUserPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.RESET_CORP_SUBS_USER_PASSOWRD);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.resetCorpSubsUserPassword(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO resendCorpSubsVerificationEmail(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.RESEND_CORP_SUBS_VERIFICATION_EMAIL);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.resendCorpSubsVerificationEmail(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO setTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        ipAuthUserPermissionInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipAuthUserPermissionInfoDTO.setOperationType(KnOperationTypes.SET_TARGET_PERMISSION);
        ipAuthUserPermissionInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.setTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO upmSetTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        ipAuthUserPermissionInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipAuthUserPermissionInfoDTO.setOperationType(KnOperationTypes.UPM_SET_TARGET_PERMISSION);
        ipAuthUserPermissionInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.setTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpUserPermissionRespDTO getTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        ipAuthUserPermissionInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipAuthUserPermissionInfoDTO.setOperationType(KnOperationTypes.GET_TARGET_PERMISSION);
        ipAuthUserPermissionInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpUserPermissionRespDTO getAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        ipAuthUserPermissionInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipAuthUserPermissionInfoDTO.setOperationType(KnOperationTypes.GET_AUTH_USER_LIST);
        ipAuthUserPermissionInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getAuthorizedMdnList(ipAuthUserPermissionInfoDTO, readOnly, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO setSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        ipEmergencyInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipEmergencyInfoDTO.setOperationType(KnOperationTypes.SET_SUBS_EMERGENCY_ATTRIBUTES);
        ipEmergencyInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.setSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO upmSetSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        ipEmergencyInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipEmergencyInfoDTO.setOperationType(KnOperationTypes.UPM_SET_SUBS_EMERGENCY_ATTRIBUTES);
        ipEmergencyInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.setSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpUserEmergencyAttributesRespDTO getSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        ipEmergencyInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipEmergencyInfoDTO.setOperationType(KnOperationTypes.GET_SUBS_EMERGENCY_ATTRIBUTES);
        ipEmergencyInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
    }

    public KnEmergUserDestRespDTO getUserEmergDest(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn){
        ipEmergencyInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipEmergencyInfoDTO.setOperationType(KnOperationTypes.GET_USER_EMERGENCY_DESTINATION);
        ipEmergencyInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getUserEmergDest(ipEmergencyInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpUserPermissionRespDTO getSubsTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        ipAuthUserPermissionInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipAuthUserPermissionInfoDTO.setOperationType(KnOperationTypes.GET_TARGET_PERMISSION_XDMDATA_INTF);
        ipAuthUserPermissionInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getSubsTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateSubsAliasEntities(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.UPDATE_SUBS_ALIAS_ENTITIES);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.updateSubsAliasEntities(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO generateTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.GENERATE_TEMP_PASSWORD);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.generateTempPassword(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO sendTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.SEND_TEMP_PASSWORD);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.sendTempPassword(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getSubsEmergencyDetails(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        ipEmergencyInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipEmergencyInfoDTO.setOperationType(KnOperationTypes.GET_SUBS_EMERGENCY_DETAILS);
        ipEmergencyInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getSubsEmergencyDetails(ipEmergencyInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO addBulkGroupsToSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.ADD_BULK_GROUPS_TO_SUBSCRIBER);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.addBulkGroupsToSubscriber(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnBulkGroupCloningDTO cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        return subscrProfileController.cloneBulkGroupsToSubscriberDataPrepration(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO cloneBulkGroupsToSubscriberProcessing(KnBulkGroupCloningDTO bulkGroupCloningDTO, KnPersisterTxn persisterTxn){
        return subscrProfileController.cloneBulkGroupsToSubscriberProcessing(bulkGroupCloningDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO createSubsATGScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.CREATE_SUBS_ATG_SCAN_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.createSubsATGScanList(ipTalkGroupDTO, persisterTxn);
    }

    @Override
    public KnCorpBulkGroupJobRespDTO contactPairingForBulkGroupProcess(KnIPCorpDispatchGrpMemInfoDto groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.ADD_BULK_GROUPS_TO_SUBSCRIBER);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.contactPairingForBulkGroupProcess(groupInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getSubsGroupMemberShipDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.GET_GRP_MEMBERSHIP_DETAILS);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getSubsGroupMemberShipDetails(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpUserPermissionRespDTO getAllAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn, boolean upmFlag) {
        ipAuthUserPermissionInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipAuthUserPermissionInfoDTO.setOperationType(KnOperationTypes.GET_ALL_AUTH_USER_LIST);
        ipAuthUserPermissionInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getAllAuthorizedMdnList(ipAuthUserPermissionInfoDTO, persisterTxn, upmFlag);
    }

    @Override
    public KnCorpResponseDTO sendTrkMaterial(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        ipSubscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscriberInfoDTO.setOperationType(KnOperationTypes.SEND_TRK_MATERIAL);
        ipSubscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.sendTrkMaterial(ipSubscriberInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpGetCorpFSResponse getCorporateFS(KnIPSubscriberInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        ipSubscrFeatureInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipSubscrFeatureInfoDTO.setOperationType(KnOperationTypes.GET_CORPORATE_FEATURESET);
        ipSubscrFeatureInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.getCorporateFS(ipSubscrFeatureInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO setUserProfileEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        ipEmergencyInfoDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        ipEmergencyInfoDTO.setOperationType(KnOperationTypes.SET_SUBS_EMERGENCY_ATTRIBUTES);
        ipEmergencyInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.setUserProfileEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO setBulkTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        return subscrProfileController.setBulkTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO allocateSubs(KnIPAllocateSubscriberDTO allocateSubscriberDTO, KnPersisterTxn persisterTxn) {
        allocateSubscriberDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        allocateSubscriberDTO.setOperationType(KnOperationTypes.ALLOCATE_SUBSCRIBER);
        allocateSubscriberDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.allocateSubs(allocateSubscriberDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO unAllocateSubs(KnIPUnAllocateSubscriberDTO unAllocateSubscriberDTO, KnPersisterTxn persisterTxn) {
        unAllocateSubscriberDTO.setEntityId(KnEntityTypes.CORP_SUBS_PROFILE_MANAGER);
        unAllocateSubscriberDTO.setOperationType(KnOperationTypes.UNALLOCATE_SUBSCRIBER);
        unAllocateSubscriberDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return subscrProfileController.unAllocateSubs(unAllocateSubscriberDTO, persisterTxn);
    }

}
