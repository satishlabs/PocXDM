/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.request.KnXDMCorpInfoRequestDTO;
import com.kodiak.common.commdto.request.KnXDMCorpSubscInfoRequestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpRespDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.helper.KnXDMProvMediator;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnAssignEXDMSNotifyDto;
import com.kodiak.xdms.server.common.dto.common.KnClientTypeConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnMDNDetailsDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpSublistInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpSubsProvInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpUserProfileUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnMcpttPermissionDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCreateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.ASSIGN_USER_PROFILE;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS;
import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;

public class KnAssignSubscriberTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAssignSubscriberTask.class);
    private KnGenInfoUtil genInfoUtil;
    private String userProfileId;
    private String baseMdn;
    private String corpId;
    private String defaultProfile;
    private IProvClientIntf provClientIntf;
    private KnXDMProvMediator provMediator;
    private ICorpClientIntf corpClientIntf;
    private KnXDMCorpMediator corpMediator;
    private KnXDMCommonMediator commonMediator;
    private KnOPSubsProfileInfoDTO subsProfileInfo;
    private KnCorpResponseDTO userProfile;
    private KnPersisterTxn assignSubsTxn;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnCorpUserProfileUtil userProfileUtil;
    private String payLoad;
    public KnAssignSubscriberTask(String userProfileId, String baseMdn
            , String corpId, String defaultProfile
            ,KnOPSubsProfileInfoDTO subsProfileInfo,KnCorpResponseDTO userProfile,String payLoad,KnPersisterTxn assignSubsTxn) {
        this.userProfileId = userProfileId;
        this.baseMdn = baseMdn;
        this.corpId = corpId;
        this.defaultProfile = defaultProfile;
        this.subsProfileInfo=subsProfileInfo;
        this.userProfile=userProfile;
        this.assignSubsTxn=assignSubsTxn;
        this.payLoad = payLoad;

        genInfoUtil = KnGenInfoUtil.getInstance();
        provClientIntf = KnProvClientImpl.getInstance();
        provMediator = KnXDMProvMediator.getInstance();
        corpClientIntf = new KnCorpClientImpl();
        corpMediator = KnXDMCorpMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        userProfileUtil = new KnCorpUserProfileUtil();
    }


    @Override
    public KnTaskResult executeTask() {
        String methodName = "createSubsTask_executeTask()";
        KnPersisterTxn createSubsTxn = assignSubsTxn;
        KnTaskResult taskResult=new KnTaskResult();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        String profileAuditMdn = null;
        try {
            KnOPSubsProfileInfoDTO subsProfileInfoDTO =subsProfileInfo;
            knLogger.debug(methodName, "baseMdn: ", KnGDPRTemplate.mdn(baseMdn)," userProfileId :",userProfileId
                    ," subsProfileInfo: ",subsProfileInfo);
            knLogger.debug(methodName," userProfile :",userProfile);
            knLogger.debug(methodName," userProfile TGSCMODE:",userProfile.getUserProfile().getTgscMode());
            //Retrieve hierarchyRoot for the userProfile
            String hierachyRoot = null;
            String hierarchyId = subsProfileInfoDTO.getHierarchyId();
            String pttServerID = genInfoUtil.retrieveLocalXDMPttServerId();
            if (hierarchyId != null) {
                hierachyRoot = KnCorpCommonInfoUtil.getHierachyRoot(Integer.parseInt(corpId), hierarchyId, createSubsTxn, pttServerID);
            }
            knLogger.info(methodName, "==>hierachyRoot : ", hierachyRoot);
            KnIPSubsProvInfoDTO subsProvInfoDTO = provMediator.populateSubsProvInfoDTOForAssignUser(subsProfileInfoDTO);
            subsProvInfoDTO.setHierarchyRoot(hierachyRoot);
            Integer subsClientType = subsProvInfoDTO.getSubsClientType();
            if(userProfile.getUserProfile().getEmergencyConfig().getEmergConfigTimer() != null){
                subsProvInfoDTO.setEmergConfigTimer(userProfile.getUserProfile().getEmergencyConfig().getEmergConfigTimer());
            }
            // checking if any the client type = null then add the default client type
            if (subsClientType == null) {
                subsClientType = KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value();
                subsProvInfoDTO.setSubsClientType(subsClientType);
            }
            knLogger.debug(methodName, "Subscriber Client type- ", subsClientType);
            //retriving from db/cache client type config check is enable or disable
            //LMR client type changes
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {

                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, createSubsTxn);
                if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                    knLogger.error(methodName, "Client type is disabled");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                }
            }
            // calling the Prov Library
            subsProvInfoDTO.setUserProfileId(userProfileId);
            KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

            ipUserProfileDTO.setCorpId(corpId);
            ipUserProfileDTO.setProfileId(userProfileId);
            KnIPUserProfileDTO ipUserProfilePermDTO=KnCorpCommonInfoUtil.jsonToObject(payLoad, KnIPUserProfileDTO.class);
            String hierarchyType = String.valueOf(ipUserProfilePermDTO.getHierarchyType().value());
            String reqDefaultProfile = ipUserProfilePermDTO.getIsDefaultProfile();
            knLogger.info(methodName, "hierarchyType: ", hierarchyType, "reqDefaultProfile: ", reqDefaultProfile);
            KnCorpResponseDTO userProfileDetails = userProfile;
            subsProvInfoDTO.setTgscMode(userProfile.getUserProfile().getTgscMode());
            subsProvInfoDTO.setUserProfileIndex(userProfileDetails.getUserProfile().getUserProfileIndex());
            if(reqDefaultProfile != null){
                subsProvInfoDTO.setIsDefaultProfile(Integer.parseInt(reqDefaultProfile));
            } else {
            subsProvInfoDTO.setIsDefaultProfile(Integer.parseInt(defaultProfile));
            }
            subsProvInfoDTO.setUserProfileFS2(userProfileDetails.getUserProfile().getFeatureBS());
            subsProvInfoDTO.setUserProfileName(userProfile.getUserProfile().getUserProfileName());
            subsProvInfoDTO.setSelfDnDPrivilege(userProfile.getUserProfile().getSelfDnDPrivilege());
            knLogger.debug(methodName," createSubscriberForAssignUserProfile :",subsProvInfoDTO);
            KnOPCreateSubsInfoDTO profileMdnCreatedResp = provClientIntf.createSubscriberForAssignUserProfile(subsProvInfoDTO, createSubsTxn);
            knLogger.debug(methodName, "Profile mdn created - ", profileMdnCreatedResp);
            String xdmPttServerId =null;
            try {
                 xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
            }
            List<String> allCommonContactMdns = sublistInfoUtil.getCommonContactListForMdns(baseMdn, xdmPttServerId, createSubsTxn);
            if(allCommonContactMdns!=null && !allCommonContactMdns.isEmpty()){
                Map<String, KnMcpttPermissionDTO> mcpttPermissions = corpSubsProvInfoUtil.getAuthUserPermissions(baseMdn, xdmPttServerId, createSubsTxn);
                List<KnMcpttPermissionDTO> permissions = new ArrayList<>(mcpttPermissions.values());
                if(!permissions.isEmpty()) {
                    Predicate<KnMcpttPermissionDTO> condition = perm -> !(perm.getCommonAu() != null && perm.getCommonAu() == 1);
                    permissions.removeIf(condition);
                    List<String> profileMdn = new ArrayList<>();
                    profileMdn.add(profileMdnCreatedResp.getAliasMdnForAssign());
                    corpSubsProvInfoUtil.insertIntoMcpttPermInfoForProfileMdns(permissions, profileMdn, xdmPttServerId, createSubsTxn);
                }
            }

            // verify if the Corporate Subscription type is enabled
            // if enabled check the pairing ind is enabled to perform the
            // internal contact pairing
            int corporateSubscriptionType = -1;
            corporateSubscriptionType = subsProvInfoDTO.getCorporateSubscriptionType();
            KnXDMCorpRespDTO corpResp = null;
            KnCorpResponseDTO corpResponseDTO = null;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {

                // calling corp library to perform Corp Auto pairing
                Boolean corpAutoParing = profileMdnCreatedResp.getCorpAutoPairing();
                Boolean isOldCorp = profileMdnCreatedResp.getIsOldCorp();

                if (corpAutoParing != null && isOldCorp != null) {
                    if (isOldCorp) {
                        knLogger.debug(methodName, "Corp exist. call enable/disble for corp Auto pairing");
                        if (corpAutoParing) {
                            KnXDMCorpSubscInfoRequestDTO contactRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
                            contactRequestDTO.setCorpId(String.valueOf(profileMdnCreatedResp.getCorpId()));
                            contactRequestDTO.setSubscriberMdn(subsProvInfoDTO.getMdn());
                            contactRequestDTO.setName(subsProvInfoDTO.getNetworkName());
                            contactRequestDTO.setHierarchyType(subsProvInfoDTO.getHierarchyType());
                            corpResp = corpMediator.addToPairingList(contactRequestDTO, createSubsTxn);
                        } else {
                            // disable auto pairing
                            KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                            corpInfoRequestDTO.setEnableAutoPair(Boolean.FALSE);
                            corpInfoRequestDTO.setCorpId(String.valueOf(profileMdnCreatedResp.getCorpId()));
                            corpInfoRequestDTO.setHierarchyType(subsProvInfoDTO.getHierarchyType());
                            corpResp = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, createSubsTxn);
                        }

                    } else { //corporate not exist
                        if (corpAutoParing) {
                            knLogger.debug(methodName, "Corp does exist hence created new. call enable auto pairing");
                            KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                            corpInfoRequestDTO.setEnableAutoPair(Boolean.TRUE);
                            corpInfoRequestDTO.setCorpId(String.valueOf(profileMdnCreatedResp.getCorpId()));
                            corpInfoRequestDTO.setHierarchyType(subsProvInfoDTO.getHierarchyType());
                            corpResp = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, createSubsTxn);
                        }
                    }

                    knLogger.debug(methodName, "Corp Autopairing response - ", corpResp);
                    if (corpResp != null && corpResp.getResponseStatus() != KnMediatorConstants.SUCCESS) {
                        throw new KnXDMServerException(corpResp.getResponseCode(), corpResp.getResponseMessage());
                    }
                }

            }

            String profileMdn = profileMdnCreatedResp.getAliasMdnForAssign();
             profileAuditMdn = profileMdnCreatedResp.getAliasMdnForAssign();

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, assignSubsTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = new HashMap<>();
            if (hierarchyType!=null && Integer.parseInt(hierarchyType) == com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY.value()) {
                knLogger.info(methodName,"entering into custom path");
                if (customParams != null) {
                    //customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, assignSubsTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    customParams.put(com.kodiak.common.resources.KnConstants.PROFILE_MDN, profileMdn);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.POP_ADDL_INFO);
                    ipUserProfileDTO.setMdn(baseMdn);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            throw new KnXDMServerException(responseDTO.getStatusCode(),responseDTO.getMessage());
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                throw new KnXDMServerException(responseDTO.getStatusCode(),responseDTO.getMessage());
                            }
                        }
                    }
                }
            }
            knLogger.info(methodName, "Done creating profile mdn :", KnGDPRTemplate.mdn(profileMdn));
            taskResult.setActiveFS2(profileMdnCreatedResp.getActiveFS2());
            taskResult.setLastUpdateprofileTime(profileMdnCreatedResp.getLastUpdateprofileTime());
            taskResult.setProfileMdn(profileMdn);
            taskResult.setTaskStatus(KnConstants.STATUS_SUCCESS);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileAuditMdn, "AssignSubscriberTask", KnAuditHelper.STATUS.FAILURE, "AssignSubscriberTask request failed"+":"+userProfile.getUserProfileId()+":"+e.getErrorCode());
            taskResult.setTaskStatus(KnConstants.STATUS_FAILURE);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed assign upm ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileAuditMdn, "AssignSubscriberTask", KnAuditHelper.STATUS.FAILURE, "AssignSubscriberTask request failed"+":"+userProfile.getUserProfileId()+":"+e.getErrorCode());
            taskResult.setTaskStatus(KnConstants.STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileAuditMdn, "AssignSubscriberTask", KnAuditHelper.STATUS.FAILURE, "AssignSubscriberTask request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(KnConstants.STATUS_FAILURE);
        }
        return taskResult;
    }

}
