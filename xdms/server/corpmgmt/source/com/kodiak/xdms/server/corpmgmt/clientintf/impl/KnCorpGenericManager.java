/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpAuthManager.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 24, 2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.commdto.request.KnXDMCorpInfoDTO;
import com.kodiak.common.commdto.request.KnIPCatPermissionSetDTO;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.corpmgmt.business.ICorpGenericInfoController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpGenericManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.common.dao.KnPersisterTxn;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class KnCorpGenericManager implements ICorpGenericManager {

    private ICorpGenericInfoController genericInfoController;

    KnCorpGenericManager() {
        genericInfoController = KnCorpBORegistry.createCorpAuthInfoController();
    }

    public KnCorpAuthInfoRespDTO authenticate(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {

        authInfoDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        authInfoDTO.setOperationType(KnOperationTypes.AUTHENTICATE);
        authInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.authenticate(authInfoDTO, persisterTxn);
    }

    public KnCorpDirInfoRespDTO getSubsDirectory(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.GET_SUBS_DIR);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.getSubsDirectory(contactDTO, persisterTxn);
    }

    public KnCorpInfoResDTO updateSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.UPDATE_SUBSCRIBER);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.updateSubscriber(contactDTO, persisterTxn);
    }

    public KnCorpResponseDTO forceSync(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn){
       contactDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.FORCE_SYNC);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.forceSync(contactDTO, persisterTxn);
     }

    public KnCorpInfoResDTO deleteSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.DELETE_SUBSCRIBER);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.deleteSubscriber(contactDTO, persisterTxn);
    }

    public KnCorpResponseDTO changeMdn(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.CHANGE_MDN);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.changeMdn(contactDTO, persisterTxn);
    }

     public KnCorpResponseDTO createSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.CREATE_SUBSCRIBER);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.createSubscriber(contactDTO, persisterTxn);
    }

    public KnCorpPAMSubsDTO getUnusedSubsList(KnIPCorpPAMSubsDTO pamSubsDTO, KnPersisterTxn persisterTxn) {
        pamSubsDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        pamSubsDTO.setOperationType(KnOperationTypes.GET_UNUSED_MDN_LIST);
        pamSubsDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.getUnusedSubsList(pamSubsDTO, persisterTxn);
    }

    public KnCorpResponseDTO cleanCorpData(KnIPCorpPAMSubsDTO pamSubsDTO, KnPersisterTxn persisterTxn) {
        pamSubsDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        pamSubsDTO.setOperationType(KnOperationTypes.CLEAN_CORP_DATA);
        pamSubsDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.cleanCorpData(pamSubsDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifySubsCorpFeature(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) {
    	subsProvInfoDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
    	subsProvInfoDTO.setOperationType(KnOperationTypes.MODIFY_SUBS_CORP_FEATURE);
    	subsProvInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.modifySubsCorpFeature(subsProvInfoDTO, persisterTxn);
    }

    public KnCorpProfileInfoRespDTO getCorporateProfile(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) {
        subsProvInfoDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        subsProvInfoDTO.setOperationType(KnOperationTypes.GET_CORPORATE_PROFILE_XDMDATA_INTF);
        subsProvInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.getCorporateProfile(subsProvInfoDTO, persisterTxn);
    }

    public Long calculateFeatureBit(int corpid,KnPersisterTxn persisterTxn)throws Exception {
        Long bitValue = genericInfoController.calculateClientTypeFeatureBit(corpid, persisterTxn);
        return bitValue;
    }

    public KnCorpLITargetInfoRespDTO getLITargetInfo(KnPersisterTxn persisterTxn){
        return genericInfoController.getLITargetInfo(persisterTxn);
    }

    public void subsEtagUpdate(String mdn, String operationType){
        genericInfoController.subsEtagUpdate(mdn,operationType);
    }
    
    public KnCorpAuthInfoRespDTO getCorpProfileByEntities(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {

        authInfoDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        authInfoDTO.setOperationType(KnOperationTypes.GET_CORP_PROFILE_BY_ENTITIES);
        authInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.getCorpProfileByEntities(authInfoDTO, persisterTxn);
    }

    public KnCorpPoCSvcConfigRespDTO getPoCConfig(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn){
        return genericInfoController.getPoCConfig(corpInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpSharedList getSharedCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        requestDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        requestDTO.setOperationType(KnOperationTypes.GET_CORP_SHARED_TRUST_MATRIX);
        requestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.getSharedCorpTrustMatrix(requestDTO, persisterTxn);
    }

    public KnCorpResponseDTO updateCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        requestDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        requestDTO.setOperationType(KnOperationTypes.UPDATE_CORP_TRUST_MATRIX);
        requestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.updateCorpTrustMatrix(requestDTO, persisterTxn);
    }

    public KnCorpResponseDTO deleteCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        requestDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        requestDTO.setOperationType(KnOperationTypes.DELETE_CORP_TRUST_MATRIX);
        requestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.deleteCorpTrustMatrix(requestDTO, persisterTxn);
    }

    public KnCorpResponseDTO updateCorporateFS(KnIPCorpInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {

        authInfoDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        authInfoDTO.setOperationType(KnOperationTypes.MODIFY_CORPORATE_FEATURESET);
        authInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.updateCorporateFS(authInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO setCATAccessPermission(KnIPCatPermissionSetDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        authInfoDTO.setEntityId(KnEntityTypes.CORP_GEN_MANAGER);
        authInfoDTO.setOperationType(KnOperationTypes.MODIFY_CORPORATE_PER_SET);
        authInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.setCATAccessPermission(authInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO deleteCorporateData(KnXDMCorpInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        return genericInfoController.deleteCorporateData(authInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO updateSelfEtag(KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn) {
        return genericInfoController.updateSelfEtag(userProfileDetails, persisterTxn);
    }

    public void updateSelfEtagForClone(KnCorpResponseDTO assignGroupResp,String corpIdString,String mdn,KnPersisterTxn persisterTxn) {
        genericInfoController.updateSelfEtagForClone(assignGroupResp, corpIdString, mdn, persisterTxn);
    }

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnSet, String corpId, KnPersisterTxn persisterTxn) {
        return genericInfoController.updateSubsTS(mdnSet, corpId, persisterTxn);
    }

    public KnCorpResponseDTO updateEtag(int corpId, String profileMdn, KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) {
        return genericInfoController.updateEtag(corpId,profileMdn, userProfileDetails, persisterTxn, etagMap);
    }

    @Override
    public KnCorpResponseDTO deleteHierarchy(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return genericInfoController.deleteHierarchy(corpId, removedHierarchy, persisterTxn);
    }
}
