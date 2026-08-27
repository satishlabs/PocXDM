/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnPocSubsAddlInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpContactCloningController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

//import static com.kodiak.addlinfo.xdms.server.corpmgmt.resources.KnCustomProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil.convertStrToIntInRange;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SG_MDN_MEMBER_TYPE;

public class KnCorpContactGroupFeatureCloningController implements ICorpContactCloningController {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpContactGroupFeatureCloningController.class);

    private KnValidatorFramework validatorFW;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnGeneralUtil generalUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpUserProfileUtil corpUserProfileUtil;

    private KnCorpUserProfileUtil userProfileUtil;

    public KnCorpContactGroupFeatureCloningController() {
        contactInfoUtil = new KnCorpContactInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        generalUtil = new KnGeneralUtil();
        userProfileUtil=new KnCorpUserProfileUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    @Override
    public KnCorpResponseDTO cloneValidation(KnIPCorpSubscCloningListDTO cloningListDTO, KnPersisterTxn persisterTxn) {
        String methodName = "cloneValidation(cloningListDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", cloningListDTO.getCorpId() , cloningListDTO.getSourceMdn(), cloningListDTO.getSubscriberMdn());
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = cloningListDTO.getCorpId();
            //Step:get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //get the Subscriber profile details from cache for target and source
            knLogger.debug(methodName, "Fetch the subscProfile profile if cached or fetch from the DB the details");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(cloningListDTO.getSubscriberMdn(),
                    KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
            knLogger.info(methodName, "subscProfile Profile - ", subscProfile);

            knLogger.debug(methodName, "Fetch the subscProfile profile if cached or fetch from the DB the details");
            KnSubsProfileDTO sourceSubscProfile = commonInfoUtil.getProfileDetails(cloningListDTO.getSourceMdn(),
                    KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
            knLogger.info(methodName, "sourceSubscProfile Profile - ", sourceSubscProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            String userProfileId = subscProfile.getUserProfileId();
            knLogger.debug(methodName, " userProfileId:", userProfileId);
            boolean isUPMSharingEnable = false;
            ArrayList<String> upmId = new ArrayList<>();
            upmId.add(userProfileId);
            Map<String, Integer> upmOwnerList = contactInfoUtil.getUserProfileOwnerinfo(upmId, xdmsHomePttId, persisterTxn);
            if (!upmOwnerList.isEmpty()) {
                isUPMSharingEnable = true;
            }
            knLogger.debug(methodName, "isUPMSharingEnable :", isUPMSharingEnable);

            //Invoking custom hook
            knLogger.debug(methodName, "Custom Call");
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = cloningListDTO.getCustomParamMap();
            if (cloningListDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                knLogger.debug(methodName, "customParams after the change - ", customParams);
                cloningListDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.MODIFY_SUBSC_CONTACT);
                hookIPDTO.setData(cloningListDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook.Process Invoker - ", processInvoker);
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    knLogger.debug(methodName, "Call After Hook");
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }


            KnCloningPersistDTO cloningPersistDTO = new KnCloningPersistDTO();
            cloningPersistDTO.setSourceMdn(cloningListDTO.getSourceMdn());
            cloningPersistDTO.setTargetMdn(cloningListDTO.getSubscriberMdn());
            cloningPersistDTO.setValidationMap(cloningListDTO.getValidationMap());

            Map<String, Integer> mdnCorpIdMap = new HashMap<>();
            mdnCorpIdMap.put(cloningListDTO.getSubscriberMdn(), subscProfile.getCorpId());
            mdnCorpIdMap.put(cloningListDTO.getSourceMdn(), sourceSubscProfile.getCorpId());
            cloningPersistDTO.setMdnCorpIdMap(mdnCorpIdMap);


            cloningPersistDTO.setCloningBitset(cloningListDTO.getCloningBitSet());
            KnPocSubsAddlInfoDTO sourceAddlDetails = commonInfoUtil.getSubsAddlDetails(cloningListDTO.getSourceMdn(), persisterTxn);
            KnPocSubsAddlInfoDTO targetAddlDetails = commonInfoUtil.getSubsAddlDetails(cloningListDTO.getSubscriberMdn(), persisterTxn);

            cloningPersistDTO.setMdnTierPackageMap(Map.of(
                    cloningListDTO.getSourceMdn(), sourceAddlDetails.getTierPackage(),
                    cloningListDTO.getSubscriberMdn(), targetAddlDetails.getTierPackage()
            ));

            Map<String, Integer> subsMdnClientTypeMap = new HashMap<String, Integer>();
            subsMdnClientTypeMap.put(subscProfile.getMdn(), subscProfile.getClientType());
            subsMdnClientTypeMap.put(sourceSubscProfile.getMdn(), sourceSubscProfile.getClientType());
            cloningPersistDTO.setMdnClientTypeMap(subsMdnClientTypeMap);

            cloningPersistDTO.setInputDTO(cloningListDTO);
            KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO();
            subsDTO.setMdn(cloningListDTO.getSubscriberMdn());
            subsDTO.setCorpId(corpId);
            cloningPersistDTO.setSubscDto(subsDTO);

            int subscribersCount = 0;
            knLogger.debug(methodName, "Call to getSubscribersCount when isUPMSharingEnable- ", isUPMSharingEnable);
            if (isUPMSharingEnable) {
                subscribersCount = contactInfoUtil.getSubscribersCount(cloningListDTO.getSubscriberMdn(),
                        subscProfile.getCorpId(), xdmsHomePttId, persisterTxn);
            } else {
                subscribersCount = contactInfoUtil.getSubscribersCount(cloningListDTO.getSubscriberMdn(),
                        corpId, xdmsHomePttId, persisterTxn);
            }

            cloningPersistDTO.setSubscriberCount(subscribersCount);

            int cloningBitSet = Integer.parseInt(cloningListDTO.getCloningBitSet());
            var validationMap= cloningListDTO.getValidationMap();
            //Bit-position# 0:- contact Cloning
            String binaryString = Integer.toBinaryString(cloningBitSet);
            if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.CONTACT.value())
                    && validationMap.containsKey(CLONING_VALIDATION_BIT.CONTACT.value())
                    && validationMap.get(CLONING_VALIDATION_BIT.CONTACT.value())) {
                getKnContactCloningPersistDTO(persisterTxn, subscProfile,
                        cloningListDTO.getCorpSubscContactListDTO(), corpId, corpProfile, cloningPersistDTO);
            }


            //Bit-position# 1:- corporate groups and Bit-position# 2:- group-member properties
            if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.GROUP.value())
                    && validationMap.containsKey(CLONING_VALIDATION_BIT.GROUP.value())
                    && validationMap.get(CLONING_VALIDATION_BIT.GROUP.value())) {
                cloningPersistDTO = getPersisitDTOForValidateGroupCloning(cloningListDTO, persisterTxn, cloningPersistDTO);
            }

            //Bit-position# 3:- Scanlist
            //if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.CONTACT.value())) {}
            // Validation not needed for scanlist
            //Bit-position# 4:- features
            //if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.CONTACT.value())) {}
            // Validation not needed for features

            //Bit-position# 5:- Emergency configuration
            if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.EMERGENCY_ARRBT.value())
                    && validationMap.containsKey(CLONING_VALIDATION_BIT.EMERGENCY_ARRBT.value())
                    && validationMap.get(CLONING_VALIDATION_BIT.EMERGENCY_ARRBT.value())) {
                cloningPersistDTO = getEmergencyAttributesValidationPersistDTO(cloningListDTO.getIpEmergencyInfoDTO(), persisterTxn, cloningPersistDTO);
            }
            //Bit-position# 6:- Target Permissions
            if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.PERMISSION.value())
                    && validationMap.containsKey(CLONING_VALIDATION_BIT.PERMISSION.value())
                    && validationMap.get(CLONING_VALIDATION_BIT.PERMISSION.value())) {
                cloningPersistDTO = getTargetPermissionsValidationPersisiDTO(cloningListDTO.getIpAuthUserPermissionInfoDTO(), persisterTxn, cloningPersistDTO);
            }
            knLogger.info(methodName, "Invoking Validation FW - ", cloningPersistDTO);
            validatorFW.validate(cloningPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while cloning subscribers  - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while cloning subscribers  - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while cloning subscribers   - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }


    public KnCloningPersistDTO getTargetPermissionsValidationPersisiDTO(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn, KnCloningPersistDTO cloningPersistDTO) {
        String methodName = "setTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipAuthUserPermissionInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = String.valueOf(ipAuthUserPermissionInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            String authorizedMdn = ipAuthUserPermissionInfoDTO.getAuthorizedMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(authorizedMdn, PUBLIC_PROFILE, false, persisterTxn);
            if (subscProfile.getCorpId() <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", authorizedMdn);
                throw new KnCorpBOException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }
            boolean isUpmCall = ipAuthUserPermissionInfoDTO.isUpmCall();
            boolean isAllAuTask=ipAuthUserPermissionInfoDTO.isAllAuTask();

            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = cloningPersistDTO.getAuthUserListPersistDTO();
            Collection<String> targetMdnList = new ArrayList<>();
            Collection<String> authMdn = new ArrayList<>();
            authMdn.add(authorizedMdn);
            Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList();
            targetMdnList.addAll(targetMdnPermBitInfos.stream().map(KnTargetMdnPermBitInfo::getMdn).collect(Collectors.toList()));
            knLogger.debug(methodName, "targetMdnList - ", KnGDPRTemplate.mdnList(targetMdnList));
            Map<String, KnCorpSubscriberDTO> contDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(targetMdnList, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "contDetailsMap - ", KnGDPRTemplate.mapKeyMdn(contDetailsMap));
            Map<String, Collection<String>> authorizedMdnContactList = new HashMap<>();
            authorizedMdnContactList = contactInfoUtil.getSubscribersContactList(authMdn, xdmsHome, persisterTxn);
            if (cloningPersistDTO.getValidationMap().containsKey(CLONING_VALIDATION_BIT.CONTACT.value()) && cloningPersistDTO.getValidationMap().get(CLONING_VALIDATION_BIT.CONTACT.value())) {
                Collection<String> sourceMdn = new ArrayList<>();
                sourceMdn.add(cloningPersistDTO.getSourceMdn());
                Map<String, Collection<String>> tempMap = contactInfoUtil.getSubscribersContactList(sourceMdn, xdmsHome, persisterTxn);
                authorizedMdnContactList.put(authorizedMdn, tempMap.get(cloningPersistDTO.getSourceMdn()));
            }
            knLogger.debug(methodName, "authorizedMdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(authorizedMdnContactList));
            Collection<String> targetNotInContactListOfAuthMdn = null;
            if(authorizedMdnContactList != null && !authorizedMdnContactList.isEmpty()){
                Collection<String> authMdnContactList = authorizedMdnContactList.get(authorizedMdn);
                if (cloningPersistDTO.getValidationMap().containsKey(CLONING_VALIDATION_BIT.CONTACT.value())
                        && cloningPersistDTO.getValidationMap().get(CLONING_VALIDATION_BIT.CONTACT.value())) {
                    knLogger.debug(methodName, "No need to set targetNotInContactListOfAuthMdn as sourceMdn is added to authorizedMdnContactList");
                } else {
                    targetNotInContactListOfAuthMdn = targetMdnList.stream().filter(target -> !authMdnContactList.contains(target)).collect(Collectors.toList());

                }
                knLogger.debug(methodName, "targetNotInContactListOfAuthMdn - ", KnGDPRTemplate.mdnList(targetNotInContactListOfAuthMdn));
                Collection<String> targetInContactListOfAuthMdn = targetMdnList.stream().filter(authMdnContactList::contains).collect(Collectors.toList());
                knLogger.debug(methodName, "targetInContactListOfAuthMdn - ", KnGDPRTemplate.mdnList(targetInContactListOfAuthMdn));
                Collection<String> targetMdnDoNotHaveAuthMdnAsContact = new ArrayList<>();
                Map<String, Collection<String>> targetMdnContactList = contactInfoUtil.getSubscribersContactList(targetInContactListOfAuthMdn, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "targetMdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(targetMdnContactList));
                if (targetMdnContactList != null && !targetMdnContactList.isEmpty()) {
                    targetMdnDoNotHaveAuthMdnAsContact.addAll(targetMdnContactList.entrySet().stream()
                            .filter(map -> !map.getValue().contains(authorizedMdn))
                            .map(Map.Entry::getKey).collect(Collectors.toList()));
                    targetMdnDoNotHaveAuthMdnAsContact.addAll(targetInContactListOfAuthMdn.stream()
                            .filter(target -> !targetMdnContactList.keySet().contains(target)).collect(Collectors.toList()));
                    authUserListPersistDTO.setTargetMdnDoNotHaveAuthMdnAsContact(targetMdnDoNotHaveAuthMdnAsContact);
                } else {
                    authUserListPersistDTO.setTargetMdnDoNotHaveAuthMdnAsContact(targetInContactListOfAuthMdn);
                }
                knLogger.debug(methodName, "targetMdnDoNotHaveAuthMdnAsContact - ", KnGDPRTemplate.mdnList(targetMdnDoNotHaveAuthMdnAsContact));
                authUserListPersistDTO.setTargetNotInContactListOfAuthMdn(targetNotInContactListOfAuthMdn);
            } else {
                authUserListPersistDTO.setTargetNotInContactListOfAuthMdn(authMdn);
            }

            String userProfileId = subscProfile.getUserProfileId();
            knLogger.info(methodName, " userProfileId:", userProfileId);
            /*boolean isUPMSharingEnable = false;
            ArrayList<String> upmId = new ArrayList<>();
            upmId.add(userProfileId);
            Map<String, Integer> upmOwnerList = contactInfoUtil.getUserProfileOwnerinfo(upmId, xdmsHome, persisterTxn);
            if(!upmOwnerList.isEmpty()){
                isUPMSharingEnable = true;
            }
            knLogger.debug(methodName,"isUPMSharingEnable :",isUPMSharingEnable);*/

            List<KnUserprofileSharedlistDTO> upmSharedList = userProfileUtil.getUserProfileSharedListByUpmId(userProfileId, xdmsHome, persisterTxn);
            boolean isUPMSharingEnabled = false;
            if(upmSharedList != null) {
                // sharedCorpList should contains ownerCorpId and sharedCorpId then isUPMSharingEnabled is made true.
                for (KnUserprofileSharedlistDTO getUPMSharedDetails : upmSharedList) {
                    if (Objects.equals(getUPMSharedDetails.getOwnerCorpId(), Integer.valueOf(corpId)) && getUPMSharedDetails.getSharedCorpId() == subscProfile.getCorpId()) {
                        isUPMSharingEnabled = true;
                        break;
                    }
                }
            }
            knLogger.info(methodName, "isUPMSharingEnabled :", isUPMSharingEnabled);

            authUserListPersistDTO.setAuthorizedMdn(authorizedMdn);
            authUserListPersistDTO.setCorpId(subscProfile.getCorpId());
            authUserListPersistDTO.setSubsFS2(subscProfile.getSubscriberFS2());
            authUserListPersistDTO.setInputDTO(ipAuthUserPermissionInfoDTO);
            authUserListPersistDTO.setSubsClientType(subscProfile.getClientType());
            authUserListPersistDTO.setAmbientListening(corpProfile.getAmbientListening() == ENABLED);
            authUserListPersistDTO.setDiscreteListening(corpProfile.getDiscreteListening() == ENABLED);
            authUserListPersistDTO.setUserCheck(corpProfile.getUserCheck() == ENABLED);
            authUserListPersistDTO.setUserSvcCtrl(corpProfile.getUserSvcCtrl() == ENABLED);
            authUserListPersistDTO.setEmergFeature(corpProfile.getEmergFeature() == ENABLED);
            authUserListPersistDTO.setMcVideoFeature(corpProfile.getMcVideoEnabled() == ENABLED);
            authUserListPersistDTO.setMcVideoUnCfrmPullFeature(corpProfile.getMcVideoUnCfrmPullEnabled() == ENABLED);
            Collection<Long> targetBitLong = new ArrayList<>();
            for(KnTargetMdnPermBitInfo targetMdnPermBitInfo : targetMdnPermBitInfos){
                BitSet bitSet = new BitSet();
                corpSubsProvInfoUtil.integerToBitsConversion(bitSet, targetMdnPermBitInfo);
                targetBitLong.add(featureSetUtil.convertBitSetToLong(bitSet));
            }
            authUserListPersistDTO.setTargetBits(targetBitLong);
            Map<String,KnCorpSubscriberDTO> targetmdns=new HashMap<>();
            targetmdns.putAll(contDetailsMap);
            if(isUpmCall && ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList()!=null){
                for(KnTargetMdnPermBitInfo obj : ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList()){
                    if(ipAuthUserPermissionInfoDTO.getCorpId() != targetmdns.get(obj.getMdn()).getCorpId()){
                        //hence this is an external subsciber
                        if(!((obj.getAmbientListening() != null && obj.getAmbientListening()==0)&&
                                (obj.getDiscreteEnabled() != null && obj.getDiscreteEnabled()==0)&&
                                (obj.getDiscreteListening() != null&&obj.getDiscreteListening()==0)&&
                                (obj.getMcVideoUnConfirmedPull() != null&&obj.getMcVideoUnConfirmedPull()==0)&&
                                (obj.getUserCheck() != null && obj.getUserCheck() == 0)&&
                                (obj.getUserEnable() != null && obj.getUserEnable() == 0))){
                            targetmdns.remove(obj.getMdn());//removing the external subs which is not having any permissions(KnSubsMemShipValidationRule)
                        }
                    }
                }}
            authUserListPersistDTO.setTargetInfo(targetmdns);
            authUserListPersistDTO.setUPMSharingEnabled(isUPMSharingEnabled);

            knLogger.debug(methodName, "authUserListPersistDTO - ", authUserListPersistDTO);
            cloningPersistDTO.setAuthUserListPersistDTO(authUserListPersistDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in setTargetPermissions  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in setTargetPermissions ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return cloningPersistDTO;
    }
    public KnCloningPersistDTO getEmergencyAttributesValidationPersistDTO(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn, KnCloningPersistDTO cloningPersistDTO) {
        String methodName = "subsEmergencyAttributesValidation(KnIPEmergencyInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipEmergencyInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = String.valueOf(ipEmergencyInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            String mdn = ipEmergencyInfoDTO.getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
            if (subscProfile.getCorpId() <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", KnGDPRTemplate.mdn(mdn));
                throw new KnCorpBOException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipEmergencyInfoDTO.getCustomParamMap();
            boolean isHierarchyCall = ipEmergencyInfoDTO.isUpmHierarchyCall();
            //making IdType as 2 i.e. For mcxGroup sharing
            String idType = "2";
            KnCorpMcpttFeaturePersistDTO emergencyMcpttFeaturePersistDTO = cloningPersistDTO.getEmergencyMcpttFeaturePersistDTO();
            String priDest = ipEmergencyInfoDTO.getPriDestination();
            String secDest = ipEmergencyInfoDTO.getSecDestination();
            LinkedList<String> destinations = new LinkedList<>();
            destinations.add(priDest);
            destinations.add(secDest);
            emergencyMcpttFeaturePersistDTO.setDestinations(destinations);
            Collection<KnSubsDestEmergencyAttributes> insertToEmergAttributes = new ArrayList<>();
            Map<String, Integer> newPriority = new HashMap<>();
            if (ipEmergencyInfoDTO.getEmergDestType() == DESTINATION_TYPE.CAT_CONFIGURED_DESTINATION.value()
                    && ipEmergencyInfoDTO.getEmergInitPermission() != DISABLED) {
                KnCorpSubscriberDTO corpsubsriber = contactInfoUtil.selectPocSubscriberInfo(mdn, xdmsHome, persisterTxn);
                String userProfileId = corpsubsriber.getUserProfileId();
                knLogger.info(methodName, " userProfileId:", userProfileId);
                ArrayList<String> upmId = new ArrayList<>();
                upmId.add(userProfileId);
                Map<String, Integer> upmOwnerList = userProfileUtil.getUserProfileOwnerinfo(upmId, xdmsHome, persisterTxn);
                Integer groupOwnerCorpId = ipEmergencyInfoDTO.getCorpId();
                if (!upmOwnerList.isEmpty()) {
                    groupOwnerCorpId = upmOwnerList.get(userProfileId);
                }
                knLogger.debug(methodName, "ownerCorpId :", groupOwnerCorpId);
                Map<String, Integer> groupIdTypesMap = groupInfoUtil.getValidGrpTypeInCorp(destinations, groupOwnerCorpId,
                        xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupIdTypesMap - ", groupIdTypesMap);
                //changes for group sharing
                //check whether for the given corpId any shared groups are avilable or not
                //if any shared groups are avilable then add shared group info to dbGroupList to avoid rules
                Set<Integer> sharedCorpGrpIds = new HashSet<>(1);
                Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap = groupInfoUtil.selectGroupSharedCorpInfoBySharedCorpId(groupOwnerCorpId, xdmsHome, persisterTxn);

                if (sharedCorpGrpInfoMap != null && !sharedCorpGrpInfoMap.isEmpty()) {
                    sharedCorpGrpIds = sharedCorpGrpInfoMap.keySet();
                    knLogger.debug(methodName, "sharedCorpGrpIds", sharedCorpGrpIds);
                    //TODO:Currently only mcxGroups with standary type are allowed hence fixing grpType for shared group as 1
                    sharedCorpGrpIds.forEach(grpId -> groupIdTypesMap.put(String.valueOf(grpId), 1));

                }
                knLogger.debug(methodName, "groupIdTypesMap - ", groupIdTypesMap);


                /**
                 * This logic currently applicable for hiereiarchy path only incase if the requests are coming from hieriarcy path
                 */
                knLogger.debug(methodName, "isHierarchyCall - ", isHierarchyCall);
                if (isHierarchyCall) {
                    List<String> missingGrpIds = new ArrayList<>(destinations);
                    missingGrpIds.removeAll(groupIdTypesMap.keySet());
                    knLogger.info(methodName, " missingGrpIds ", missingGrpIds);
                    if (missingGrpIds != null && !missingGrpIds.isEmpty()) {
                        //if mdn is passed to convert we might get NFE as interger value can go out of range for mdn.
                        //to avoid that have added check to add only integer values,
                        //As if groupid will go out of integer range then it will be problem in the places(DAO),for now restricting the range
                        Collection<Integer> missingGrpIdsInt = convertStrToIntInRange(missingGrpIds);
                        Map<Integer, List<String>> grpOwnerMap = groupInfoUtil.getGroupOwnerList(new ArrayList<>(missingGrpIdsInt), idType, xdmsHome, false, persisterTxn);
                        if (grpOwnerMap != null && !grpOwnerMap.isEmpty()) {
                            //Currently only mcxGroups for hierarchy path are allowed for sharing hence fixing grpType for shared group as 1
                            grpOwnerMap.keySet().forEach(grpId -> groupIdTypesMap.put(String.valueOf(grpId), 1));
                        }
                    }
                    knLogger.debug(methodName, "HierarchyCall -> groupIdTypesMap - ", groupIdTypesMap);
                }


                Collection<Integer> validGroupId = new ArrayList<>(groupIdTypesMap.size());
                groupIdTypesMap.forEach((groupId, groupType) -> {
                    if (groupType != BROADCAST_GROUP) {
                        validGroupId.add(Integer.parseInt(groupId));
                    }
                });
                List<Integer> groupidList = new ArrayList<Integer>();
                List<Integer> mcxGroupIds = new ArrayList<Integer>();
                Map<String, String> mdnExistenceInGroup = new HashMap<String, String>();

                ArrayList<KnCorpGroupDTO> groupDetailsList = groupInfoUtil.getGroupBasicInfoList(validGroupId, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupDetailsList - ", groupDetailsList);
                if (groupDetailsList != null) {
                    for (KnCorpGroupDTO knCorpGroupDTO : groupDetailsList) {
                        if (knCorpGroupDTO.getMcxGrpInd() == 1) {

                            mcxGroupIds.add(knCorpGroupDTO.getGroupId());
                            mdnExistenceInGroup.put(String.valueOf(knCorpGroupDTO.getGroupId()), mdn);
                        }
                    }
                }
                knLogger.debug(methodName, "mdnExistenceInGroup - ", mdnExistenceInGroup);
                if (!mcxGroupIds.isEmpty()) {

                    if (corpsubsriber != null && corpsubsriber.getUserProfileId() != null) {

                        Set<KnCorpGroupListInfoDTO> knCorpGroupListInfoDTOList = userProfileUtil.retriveGroupProfileInfoByProfileId(corpsubsriber.getUserProfileId(), xdmsHome, persisterTxn);
                        if (knCorpGroupListInfoDTOList != null) {

                            for (KnCorpGroupListInfoDTO corpGrpinfo : knCorpGroupListInfoDTOList) {
                                if (mcxGroupIds.contains(corpGrpinfo.getGroupID()) && mdnExistenceInGroup.get(String.valueOf(corpGrpinfo.getGroupID())) == null) {
                                    mdnExistenceInGroup.put(String.valueOf(corpGrpinfo.getGroupID()), mdn);
                                }
                            }
                        }
                    }
                }
                Map<String, String> mdnExistenceInNormalGroup = groupInfoUtil.selectGroupMemberForGroupIds(validGroupId, mdn, xdmsHome, persisterTxn);
                if (commonInfoUtil.isBitPositionSet(Integer.parseInt(cloningPersistDTO.getCloningBitset()), CLONING_BIT_POSITION.GROUP.value())) {
                    for (var itr : validGroupId) {
                        mdnExistenceInGroup.put(String.valueOf(itr), mdn);
                    }
                }
                mdnExistenceInGroup.putAll(mdnExistenceInNormalGroup);
                knLogger.debug(methodName, "mdnExistenceInGroup - ", KnGDPRTemplate.mdnMap(mdnExistenceInGroup));
                emergencyMcpttFeaturePersistDTO.setGroupTypeMap(groupIdTypesMap);
                emergencyMcpttFeaturePersistDTO.setMdnExistenceInGroup(mdnExistenceInGroup);
                Collection<String> mdnList = new ArrayList<>();
                if (commonInfoUtil.isBitPositionSet(Integer.parseInt(cloningPersistDTO.getCloningBitset()), CLONING_BIT_POSITION.CONTACT.value())) {
                    mdnList.add(cloningPersistDTO.getSourceMdn());
                } else {
                    mdnList.add(mdn);
                }
                knLogger.debug(methodName, "mdnList - ", mdnList);
                Map<String, Collection<String>> mdnContactList = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHome, persisterTxn);
                if (commonInfoUtil.isBitPositionSet(Integer.parseInt(cloningPersistDTO.getCloningBitset()), CLONING_BIT_POSITION.CONTACT.value())) {
                    for (var itr : mdnContactList.entrySet()) {
                        if (itr.getKey().equals(cloningPersistDTO.getSourceMdn())) {
                            mdnContactList.put(mdn, itr.getValue());
                        }
                    }
                }
                knLogger.debug(methodName, "mdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(mdnContactList));
                emergencyMcpttFeaturePersistDTO.setSubsContactList(mdnContactList);
                int priority = 0;
                KnSubsDestEmergencyAttributes destEmergencyAttributes = null;
                for (String dest : destinations) {
                    if (mdnExistenceInGroup != null && mdnExistenceInGroup.keySet().contains(dest)) {
                        destEmergencyAttributes = new KnSubsDestEmergencyAttributes();
                        destEmergencyAttributes.setMdn(mdn);
                        destEmergencyAttributes.setEmergDestPriority(++priority);
                        destEmergencyAttributes.setEmergDestTypeMgmt(DESTINATION_TYPE_MGMT.GROUP.value());
                        destEmergencyAttributes.setEmergDest(dest);
                        insertToEmergAttributes.add(destEmergencyAttributes);
                        newPriority.put(dest, destEmergencyAttributes.getEmergDestPriority());
                    } else if (mdnContactList != null && mdnContactList.get(mdn) != null && mdnContactList.get(mdn).contains(dest)) {
                        destEmergencyAttributes = new KnSubsDestEmergencyAttributes();
                        destEmergencyAttributes.setMdn(mdn);
                        destEmergencyAttributes.setEmergDestPriority(++priority);
                        destEmergencyAttributes.setEmergDestTypeMgmt(DESTINATION_TYPE_MGMT.CONTACT.value());
                        destEmergencyAttributes.setEmergDest(dest);
                        insertToEmergAttributes.add(destEmergencyAttributes);
                        newPriority.put(dest, destEmergencyAttributes.getEmergDestPriority());
                    }
                }
            }
            emergencyMcpttFeaturePersistDTO.setCorpId(subscProfile.getCorpId());
            emergencyMcpttFeaturePersistDTO.setInputDTO(ipEmergencyInfoDTO);
            emergencyMcpttFeaturePersistDTO.setMdn(mdn);
            emergencyMcpttFeaturePersistDTO.setSubsFS2(subscProfile.getSubscriberFS2());
            emergencyMcpttFeaturePersistDTO.setEmergFeature(corpProfile.getEmergFeature() == ENABLED);


            List<KnUserprofileSharedlistDTO> upmSharedList = userProfileUtil.getUserProfileSharedListByUpmId(subscProfile.getUserProfileId(), xdmsHome, persisterTxn);
            boolean isUPMSharingEnabled = false;
            if (upmSharedList != null) {
                // sharedCorpList should contains ownerCorpId and sharedCorpId then isUPMSharingEnabled is made true.
                for (KnUserprofileSharedlistDTO getUPMSharedDetails : upmSharedList) {
                    if (Objects.equals(getUPMSharedDetails.getOwnerCorpId(), Integer.valueOf(corpId)) && getUPMSharedDetails.getSharedCorpId() == subscProfile.getCorpId()) {
                        isUPMSharingEnabled = true;
                        break;
                    }
                }
            }
            emergencyMcpttFeaturePersistDTO.setUPMSharingEnabled(isUPMSharingEnabled);
            knLogger.info(methodName, "isUPMSharingEnabled :", isUPMSharingEnabled);

            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", emergencyMcpttFeaturePersistDTO);
            cloningPersistDTO.setEmergencyMcpttFeaturePersistDTO(emergencyMcpttFeaturePersistDTO);
            return cloningPersistDTO;
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in setSubsEmergencyAttributes  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in setSubsEmergencyAttributes ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return cloningPersistDTO;
    }

    private void getKnContactCloningPersistDTO(KnPersisterTxn persisterTxn, KnSubsProfileDTO subscProfile,
                                               KnIPCorpSubscContactListDTO contactListDTO,
                                               int corpId, KnCorpProfileDTO corpProfile, KnCloningPersistDTO cloningPersistDTO) throws KnBOException {
        String methodName = "getKnContactCloningPersistDTO";
        /*int privateListId = subscProfile.getContactListId();*/
        //Step:
        //preparing persistDto
        KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
        corpMdnListPersistDto.setCorpId(corpId);
        corpMdnListPersistDto.setAddedMdnList(contactListDTO.getAddedMdnList());
        corpMdnListPersistDto.setMdn(contactListDTO.getSubscriberMdn());
        corpMdnListPersistDto.setRemovedMdnList(contactListDTO.getRemovedMdnList());

        String mdn = subscProfile.getMdn();
        //int subscriberDocEtag = contactInfoUtil.getSubscribersDocumentEtag(mdn, xdmsHomePttId, persisterTxn);
        //Step:
        //get mdn details for added mdn list, also filters out the external contacts present if any
        knLogger.debug(methodName, "Before getPoCSubscriberInfo for input corpMdnListPersistDto - ",
                corpMdnListPersistDto);


        //Prepare subscriberMDN - clientType Map for validation
        Map<String, Integer> subsMdnClientTypeMap = new HashMap<String, Integer>();
        subsMdnClientTypeMap.put(mdn, subscProfile.getClientType());
        KnContactDetailsPersistDTO contactCloningPersistDTO = cloningPersistDTO.getContactCloningPersistDTO();
        boolean isCommonContact = subscProfile.getSubscriberFS2() != null && KnGeneralUtil.getFeatureBitValue(subscProfile.getSubscriberFS2(), KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
        Boolean isCommonContactListSupport = Boolean.FALSE;
        //skip if the bit is disabled
        if (isCommonContact) {
            //common sublist modification allowed if system and corp flag is enabled
            final int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            final String systemCmnContactListSupport = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT) == null
                    ? "0" : microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT);
            final Integer commonContactListSupportFlagOfCorpProfile = corpProfile.getCommonContactListSupport();
            if (systemCmnContactListSupport.equals(BIT_ENABLED) || commonContactListSupportFlagOfCorpProfile == Integer.valueOf(ENABLED)) {
                isCommonContactListSupport = Boolean.TRUE;
            }
            knLogger.debug(methodName, " isCommonContact supported ", isCommonContactListSupport);
            contactCloningPersistDTO.setCommonContactEnabled(isCommonContactListSupport);
        } else {
            contactCloningPersistDTO.setCommonContactEnabled(isCommonContactListSupport);
        }

        knLogger.debug(methodName, "Before setting of the validation framework object.");
        //setting of the validation persist DTO
        //contactCloningPersistDTO.setSubscriberCount(subscribersCount);
        contactCloningPersistDTO.setMdnClientTypeMap(subsMdnClientTypeMap);
        Map<Integer, Integer> CCLSublistTypeInfo = new HashMap<>();
        for (Integer sublistId : contactListDTO.getAddedSublistIds()) {
            CCLSublistTypeInfo.put(sublistId, 6);
        }
        contactCloningPersistDTO.setSubListIdInfo(CCLSublistTypeInfo);
        cloningPersistDTO.setContactCloningPersistDTO(contactCloningPersistDTO);
    }

    public KnCloningPersistDTO getPersisitDTOForValidateGroupCloning(KnIPCorpSubscCloningListDTO cloningListDTO, KnPersisterTxn persisterTxn, KnCloningPersistDTO cloningPersistDTO) throws KnBOException, KnProcessInvokerException {
        String methodName = "getPersisitDTOForValidateGroupCloning(KnIPSubscriberInfoDTO, KnPersisterTxn, KnCloningPersistDTO)";
        knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", cloningListDTO.getBulkGroupCloningDTO().getPersistDTO());
        cloningPersistDTO.setGroupInfoPersistDTO(cloningListDTO.getBulkGroupCloningDTO().getPersistDTO());
        return cloningPersistDTO;
    }

    public int getCurrentEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getPreviousEtag";
        String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
        //commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
        knLogger.debug(methodName, "Successfully updated the xdm directory , previousEtag: ", previousEtag);
        return previousEtag;
    }
}
