/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.common.KnCorpGroupContactDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.ggcache.dto.KnUserProfileInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.common.commdto.common.KnUserEmergencyAttributes;
import com.kodiak.xdms.server.common.dto.common.KnTalkGrpScanModeDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.corpmgmt.business.ICorpUserProfileController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import java.util.concurrent.atomic.AtomicBoolean;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpUserProfileListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.GROUP_SIZE_TYPE.VLARGE_GROUP;
import static com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil.convertStrToIntColl;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.MAX_USERPROFILES_PERSUB;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.MAX_USER_PROFILES;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.IDLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.POCHOME_AUTOASSIGN_FLAG;
import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_USER_PROFILE_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.EMERGCONFIGTIMER_FEATURE_DISABLED;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.NON_FIRSTNET_FAN;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.ASSIGN_USER_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;

public class KnCorpUserProfileController implements ICorpUserProfileController {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpUserProfileController.class);

    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnValidatorFramework validatorFW;
    private KnCorpUserProfileUtil userProfileUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnGeneralCacheUtil generalCacheUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnFeatureSetUtil featureSetUtil;
    private ICorpClientIntf corpClientIntf;

    public KnCorpUserProfileController(){
        commonInfoUtil = new KnCorpCommonInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        userProfileUtil=new KnCorpUserProfileUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        corpSubsProvInfoUtil= new KnCorpSubsProvInfoUtil();
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        featureSetUtil = KnFeatureSetUtil.getInstance();
        corpClientIntf = new KnCorpClientImpl();
    }

    @Override
    public KnCorpResponseDTO createUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "createUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            int corpId=Integer.parseInt(ipUserProfileDTO.getCorpId());
            List<String> ownerFanIdList = ipUserProfileDTO.getOwnerIdList();
            Collection<Integer> idListInt = null;
            String idType = null;
            boolean isHierarchyFlow = false;

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    customParams.put(com.kodiak.common.resources.KnConstants.ADD_GROUP_LIST, ipUserProfileDTO.getUserProfileDTO().getGroupList());

                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.CREATE_USER_PROFILE);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                        Collection<String> idListStr = (Collection<String>) ipUserProfileDTO.getCustomParamMap().get(KnConstants.IDLIST);
                        if (idListStr != null) {
                            List idListNew=new ArrayList<String>();
                            idListNew.addAll(idListStr);
                            if(ownerFanIdList!=null && !ownerFanIdList.isEmpty()) {
                                idListNew.addAll(ownerFanIdList);
                            }
                            idListInt = convertStrToIntColl(idListNew);
                        }
                        idType = (String) ipUserProfileDTO.getCustomParamMap().get(KnConstants.IDTYPE);
                        isHierarchyFlow = true;
                    }
                }
            }
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            KnSubsEmergencyConfigDTO emeConfig = ipUserProfileDTO.getUserProfileDTO().getEmergencyConfig();
            if (null != emeConfig.getEmergConfigTimer()) {
                String emergencyFeatureFlag = microServicesParamNameValueMap.get(EMERGENCY_CONF_TIMER_FEATURE);
                boolean emergencyBit = Optional.ofNullable(corpProfile.getXdmCorpFS2Set())
                        .map(set -> KnGeneralUtil.getFeatureBitValue(set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value()))
                        .orElse(false);
                knLogger.debug(methodName, "emergencyFeatureFlag is :-", emergencyFeatureFlag, "emergencyBit is :-", emergencyBit);
                if (!emergencyFeatureFlag.equals(ENABLED_STRING) || !emergencyBit) {
                    knLogger.error("System level or Corporate Level EMERGENCY_CONF_TIMER_FEATURE flag is disabled");
                    throw new KnCorpBOValidationException(EMERGCONFIGTIMER_FEATURE_DISABLED, "EMERGCONFIGTIMER_FEATURE flag is disabled", "");
                }
                Float emeConfTimer = Float.valueOf(emeConfig.getEmergConfigTimer());
                Float minEmeTimer = Float.valueOf(microServicesParamNameValueMap.getOrDefault(MIN_EMERGENCY_TIME, "0"));
                Float maxEmeTimer = Float.valueOf(microServicesParamNameValueMap.getOrDefault(MAX_EMERGENCY_TIME,"5"));
                knLogger.debug(methodName, "emeConfTimer is :-", emeConfTimer, "minEmeTimer is :-", minEmeTimer, "maxEmeTimer is :-", maxEmeTimer);
                if (emeConfTimer < minEmeTimer || emeConfTimer > maxEmeTimer) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGCONFIGTIMER_NOT_IN_RANGE, "Emergency Timer value not in range", "");
                }
                emeConfig.setEmergConfigTimer(String.valueOf(emeConfTimer));
            } /*else { // TODO: Need to check if this is required as already handled in fetch request
                emeConfig.setEmergConfigTimer(microServicesParamNameValueMap.get(DEFAULT_EMERGENCY_TIMER));
            }*/
            //settting operation and entity id
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            //max allowed userProfile
            final String sysMaxUserProfile = microServicesParamNameValueMap.get(MAX_USER_PROFILES );

            final String corpMaxUserProfile = corpProfile.getMaxUserProfiles() == null ? null
                    : String.valueOf(corpProfile.getMaxUserProfiles());
            String configuredMaxUP="0";
            if(corpMaxUserProfile ==null){
                configuredMaxUP=sysMaxUserProfile;
            }else if(corpMaxUserProfile!=null&&sysMaxUserProfile!=null){
                configuredMaxUP=corpMaxUserProfile;
            }
            knLogger.debug(methodName,"sysMaxUserProfile ",sysMaxUserProfile,"corpMaxUserProfile ",corpMaxUserProfile,"configuredMaxUP ",configuredMaxUP);
            String upmIndex = userProfileUtil.getUserProfileIndexSeqence(corpId,xdmsHome, persisterTxn);
            Integer upmCountPerCorp = userProfileUtil.getMaxTotalCountByCorpId(String.valueOf(corpId), xdmsHome, persisterTxn);
            userProfilePersistDTO.setMaxUserProfile(configuredMaxUP);
            String dbmax="0";
            if(upmCountPerCorp!=null){
                dbmax=upmCountPerCorp.toString();
            }
            userProfilePersistDTO.setDbMaxUserProfile(dbmax);
            //contact list autoAssign disabled
            if(ipUserProfileDTO.getUserProfileDTO().getContactListID()!=null){
                int commonContactListRejectionId = sublistInfoUtil.getCommonContactListReject(ipUserProfileDTO.getUserProfileDTO().getContactListID().intValue(),corpId,xdmsHome,persisterTxn);
                knLogger.debug(methodName,"commonContactListRejectionId:",commonContactListRejectionId);
                userProfilePersistDTO.setCommonContactListRejectionId(commonContactListRejectionId);
                KnCorpSublistDTO sublistDetails = sublistInfoUtil
                        .getSublistInfo(ipUserProfileDTO.getUserProfileDTO().getContactListID().intValue(),
                                corpId, xdmsHome, persisterTxn);
                userProfilePersistDTO.setAutoAssign(sublistDetails.getDistributionPolicy());
            }
            //check if profileName already exists to maintain profileName unique
            String dbProfileName = userProfileUtil
                    .getProfileName(corpProfile.getCorpId(),
                            ipUserProfileDTO.getUserProfileDTO().getUserProfileName(), xdmsHome, persisterTxn);
            userProfilePersistDTO.setDbProfileName(dbProfileName);
            userProfilePersistDTO.setReqProfileName(ipUserProfileDTO.getUserProfileDTO().getUserProfileName());

            //group part of same corp: groupExistsInCorp
            KnIPCorpInfoDTO corpInfoDTO=new KnIPCorpInfoDTO();
            corpInfoDTO.setCorpId(corpId);
            Collection<KnCorpGroupInfoPersistDTO> groupList = groupInfoUtil.getAllGroupList(corpInfoDTO, 100, xdmsHome, persisterTxn);
            //Get the shared groups.
            Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap = groupInfoUtil.selectGroupSharedCorpInfoBySharedCorpId(corpId,xdmsHome,persisterTxn);
            userProfilePersistDTO.setSharedCorpGrpInfoMap(sharedCorpGrpInfoMap);
            List<KnCorpGroupInfoPersistDTO> sharedGroupList = groupInfoUtil.getSharedGroupDetails(sharedCorpGrpInfoMap, xdmsHome,persisterTxn);

            List<KnCorpGroupInfoPersistDTO> ownerGroupList = null;
            if(ipUserProfileDTO.getUserProfileDTO().getGroupList()!=null && isHierarchyFlow){
                Set<KnCorpGroupListInfoDTO> reqGroupList = ipUserProfileDTO.getUserProfileDTO().getGroupList();
                List<Integer> missingGrpIds = new ArrayList<>();
                for(KnCorpGroupListInfoDTO group : reqGroupList) {
                    int grpId =group.getGroupID().intValue();
                    if( !groupList.contains(grpId) && !sharedGroupList.contains(grpId)){
                        missingGrpIds.add(grpId);
                    }
                }
                if(!missingGrpIds.isEmpty()) {
                    knLogger.info(methodName,"groupids that belogs diffent corp -",missingGrpIds);
                    Map<Integer, Integer> grpIdOwnerMap = groupInfoUtil.isValidGroupForIdList(missingGrpIds, new ArrayList<>(idListInt), idType, xdmsHome, false, persisterTxn);
                    if(grpIdOwnerMap!=null && !grpIdOwnerMap.isEmpty()) {
                        ownerGroupList = groupInfoUtil.getOwnerGroupDetails(grpIdOwnerMap.keySet(), xdmsHome,persisterTxn);
                        groupList.addAll(ownerGroupList);
                    }

                }
            }
            groupList.addAll(sharedGroupList);
            userProfilePersistDTO.setCorpGroupList(groupList);
            userProfilePersistDTO.setUserProfileDTO(ipUserProfileDTO.getUserProfileDTO());

            //get the Allowed member feature list
            Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap = userProfileUtil.getTrustMatrixMap(corpProfile.getExtCorpId(),
                    generalCacheUtil, commonInfoUtil, xdmsHome, persisterTxn);
            userProfilePersistDTO.setTrustMatrixMap(trustMatrixMap);
            //create bit set with targentMdn
            String uuid = commonInfoUtil.getUUID();
            ipUserProfileDTO.setProfileId(uuid);
            ipUserProfileDTO.getUserProfileDTO().set_id(uuid);
            ipUserProfileDTO.getUserProfileDTO().setUserProfileIndex(Integer.valueOf(upmIndex)+1);
            ipUserProfileDTO.getUserProfileDTO().setCorporateID(corpId);
            //XDM-9155: selfDnDPrivilege
            ipUserProfileDTO = commonInfoUtil.selfDndPrivilegeCheck(ipUserProfileDTO, corpProfile, microServicesParamNameValueMap);
            KnUserProfileFSDTO ipUpmFs = ipUserProfileDTO.getUserProfileFSDto();

            if (ipUpmFs != null) {
                String defaultUpmFs = featureSetUtil.getDefFinalUserProfileFS();
                String finalUpmFsUsingDefUpmFs = userProfileUtil.getUpmFsBasedOnDefUpmFs(defaultUpmFs, ipUpmFs);
                String userProfileFeatureSet = featureSetUtil.generateUserProfileFeatureSet(finalUpmFsUsingDefUpmFs);
                knLogger.debug(methodName, "userProfileFeatureSet-->UserProfileFS", userProfileFeatureSet);
                ipUserProfileDTO.getUserProfileDTO().setFeatureBS(userProfileFeatureSet);
            } else {
                //if UPMFS null in request setting defaultUpmFs
                String defaultUpmFs = featureSetUtil.getDefFinalUserProfileFS();
                String userProfileFeatureSet = featureSetUtil.generateUserProfileFeatureSet(defaultUpmFs);
                knLogger.debug(methodName, "defaultUPMFS-->", userProfileFeatureSet);
                ipUserProfileDTO.getUserProfileDTO().setFeatureBS(userProfileFeatureSet);
            }
            //set zone/channle/priority and camped group validation
            int maxZone = corpProfile.getMaxZones();
            userProfilePersistDTO.setMaxZone(maxZone);
            int maxChannelsPerZone = corpProfile.getMaxChannelsPerZone();
            userProfilePersistDTO.setMaxChannelsPerZone(maxChannelsPerZone);
            int maxPriority = corpProfile.getMaxPriority();
            userProfilePersistDTO.setMaxPriority(maxPriority);
            int pttRadioScanSize = corpProfile.getMaxPttRadioScanSize();
            userProfilePersistDTO.setMaxPttRadioScanSize(pttRadioScanSize);

            userProfilePersistDTO.setUserProfileDTO(ipUserProfileDTO.getUserProfileDTO());

            KnCorpContactInfoUtil contactInfoUtiln= new KnCorpContactInfoUtil();
            Map<String,BitSet> externalPemMap= new HashMap<>();
            if(!ipUserProfileDTO.getUserProfileDTO().getMcpttPermissionsConfig().isEmpty()) {
                Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfig= new HashSet<>();
                mcpttPermissionsConfig=ipUserProfileDTO.getUserProfileDTO().getMcpttPermissionsConfig();
                List<String> mdnList=new ArrayList<>();
                List<String> externalmebers=new ArrayList<>();
                for(KnCorpUserProfileMCPTTConfig obj:mcpttPermissionsConfig){
                    mdnList.add(obj.getMdn());
                }
                
                knLogger.debug("mdnList-->"+ KnGDPRTemplate.mdnList(mdnList));
                Map<String, KnCorpSubscriberDTO> mdnListInfo = contactInfoUtiln.getSubsribersCorporateDetails(mdnList, xdmsHome, persisterTxn);
                for(Map.Entry<String, KnCorpSubscriberDTO> obj : mdnListInfo.entrySet()){
                    if((corpId) != obj.getValue().getCorpId()){
                        externalmebers.add(obj.getKey());
                    }
                }
                if(!externalmebers.isEmpty()){
                    for(KnCorpUserProfileMCPTTConfig obj:mcpttPermissionsConfig){
                        if(externalmebers.contains(obj.getMdn())){
                            BitSet permSet=KnGeneralUtil.convertLongToBitSet(obj.getPermBitSet());
                            externalPemMap.put(obj.getMdn(),permSet);
                        }
                    }
                }}
            userProfilePersistDTO.setExternalPemMap(externalPemMap);
            String corpid=String.valueOf(corpId);
            if(ipUserProfileDTO.getUserProfileDTO().getContactListID() != null){
            Integer contactListId=ipUserProfileDTO.getUserProfileDTO().getContactListID();
            Map<String,Integer> memberInfo=userProfileUtil.getSublistMemberCorpDetails(contactListId,corpid,xdmsHome,persisterTxn);
            userProfilePersistDTO.setSubslistMemCorpInfo(memberInfo);
            }
            userProfilePersistDTO.setCorpId(corpid);

            String sysuserProfileSharingFlag=microServicesParamNameValueMap.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.USER_PROFILE_SHARING_FLAG);
            String dbUPMSharingFlag = String.valueOf(corpProfile.getUserProfileSharingFeature() == null ? Integer.parseInt(sysuserProfileSharingFlag) : corpProfile.getUserProfileSharingFeature());
            String reqUPMSharingFlag = ipUserProfileDTO.getSharingEnabled();
            userProfilePersistDTO.setDbUPMSharingFlag(dbUPMSharingFlag);
            userProfilePersistDTO.setReqUPMSharingFlag(reqUPMSharingFlag);

            List<Integer> groupIdList = new ArrayList<>();
            Set<KnCorpGroupListInfoDTO> addedGroupList = ipUserProfileDTO.getUserProfileDTO().getGroupList();
            if (null != addedGroupList && !addedGroupList.isEmpty()) {
                addedGroupList.forEach(knCorpGroupListInfoDTO -> {
                    groupIdList.add(knCorpGroupListInfoDTO.getGroupID());
                });
            }
            int sizeofGrpList = groupIdList.size();
            userProfilePersistDTO.setGroupListSize(sizeofGrpList);
            userProfilePersistDTO.setMaxGroupsPerMemberCount(corpProfile.getMaxGroupsPerSubsc());

            List<String> reqUserProfileSharedCorpList = ipUserProfileDTO.getUserProfileSharedCorpList();
            List<KnCorpTrustMatrixDTO> dbTrustMatrixDTOList = generalCacheUtil.getSharedCorpMatrixByExtAndSharingFeature(corpProfile.getExtCorpId());
            List<String> dbTrustMatrixSharedExtCorpIdList = dbTrustMatrixDTOList.stream().map(e -> e.getSharedExtCorpId()).collect(Collectors.toList());
            Map<String,Integer> extIntCorpIDMap = commonInfoUtil.getCorpIdMap(new HashSet<>(dbTrustMatrixSharedExtCorpIdList),null,persisterTxn);
            List<String> dbUserProfileSharedCorpList = extIntCorpIDMap.values().stream().map(String::valueOf).collect(Collectors.toList());
            userProfilePersistDTO.setReqUserProfileSharedCorpList(reqUserProfileSharedCorpList);
            userProfilePersistDTO.setDbUserProfileSharedCorpList(dbUserProfileSharedCorpList);
            List<Integer> groupIdsList = ipUserProfileDTO.getUserProfileDTO().getGroupList().stream().map(e -> e.getGroupID()).collect(Collectors.toList());
            List<Integer> preConfigParamList = groupInfoUtil.getIsPreConfiguredParamList(groupIdsList,xdmsHome,persisterTxn);
            userProfilePersistDTO.setIsPreConfiguredGroupList(preConfigParamList);
            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.info(methodName, "Validation Successfull");
            userProfileUtil.createUserProfile(uuid,ipUserProfileDTO.getUserProfileDTO(),xdmsHome,persisterTxn);
            //insert to DG.USERPROFILE_SHAREDLIST
            if(reqUserProfileSharedCorpList!=null&&!reqUserProfileSharedCorpList.isEmpty()){
                List<KnUserprofileSharedlistDTO> upmShardCorpList=new ArrayList<>();
                for(String sharedCorpId:reqUserProfileSharedCorpList){
                    upmShardCorpList.add(new KnUserprofileSharedlistDTO(ipUserProfileDTO.getUserProfileDTO().get_id(),
                            ipUserProfileDTO.getUserProfileDTO().getCorporateID()
                            ,Integer.parseInt(sharedCorpId)));
                }
                userProfileUtil.insertUserProfileSharedList(upmShardCorpList,xdmsHome, persisterTxn);
            }

            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY&&(ownerFanIdList!=null&&!ownerFanIdList.isEmpty())) {
                userProfileUtil.insertUserProfileHiearchyMap(uuid, ownerFanIdList,idType, xdmsHome, persisterTxn);
            }

            // Set userProfileId in response - this should always be returned as profile is created
            respDTO.setUserProfileId(uuid);
            knLogger.info(methodName, "UserProfile created successfully with ID: ", uuid);

            if(ipUserProfileDTO.getUserProfileDTO().getGroupList() != null) {
                List<Integer> groupIds = ipUserProfileDTO.getUserProfileDTO().getGroupList().stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList());
                List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHome, persisterTxn);
                List<Integer> mcxGroupIds = groupInfoList.stream().filter(x -> x.getMcxGrpInd() == 1).map(KnCorpGroupDTO::getGroupId).collect(Collectors.toList());
                if(!mcxGroupIds.isEmpty()) {
                    Set<KnCorpGroupListInfoDTO> groupListForProfileInfo = ipUserProfileDTO.getUserProfileDTO().getGroupList().stream().filter(x -> mcxGroupIds.contains(x.getGroupID())).collect(Collectors.toSet());
                    knLogger.info(methodName, "insert ProfileGroupInfo completed ");
                    // For list of groups get the properties and add to DG.PROFILE_GROUP_INFO
                    userProfileUtil.insertProfileGroupInfo(uuid, groupListForProfileInfo, xdmsHome, persisterTxn);
                    knLogger.debug(methodName, "insert ProfileGroupInfo completed ");
                    Map<Integer, KnCorpGroupContactDTO > mcxGrpMemberShipMap = new HashMap<>();
                    for(KnCorpGroupListInfoDTO infoDTO : groupListForProfileInfo){
                        mcxGrpMemberShipMap.put(infoDTO.getGroupID(), new KnCorpGroupContactDTO(infoDTO.getGrpMemProps().getIsSupervisor(),
                                infoDTO.getGrpMemProps().getIsBroadcaster(), infoDTO.getGrpMemProps().getIsLocSupervisor(), infoDTO.getGrpMemProps().getIsOSMAuthorized(),
                                infoDTO.getGrpMemProps().getCallInitiateAllowed(), infoDTO.getGrpMemProps().getCallTerminateAllowed(), infoDTO.getGrpMemProps().getIncallAllowed(),infoDTO.getGrpMemProps().getVideoCallInitiateAllowed(), infoDTO.getGrpMemProps().getVideoCallReceiveAllowed(), infoDTO.getGrpMemProps().getVideoInCallAllowed()));
                    }
                    respDTO.setMcxGrpInd(1);
                    respDTO.setMcxGrpMemberShipMap(mcxGrpMemberShipMap);
                }
                populate(respDTO);
            }

        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occurred", e);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occurred", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO updateUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();

        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId = ipUserProfileDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            Collection<Integer> idListInt = null;
            String idType = null;
            boolean isHierarchyFlow = false;
            List<String> addedOwnerIDList = ipUserProfileDTO.getAddedOwnerIDList();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    customParams.put(com.kodiak.common.resources.KnConstants.ADD_GROUP_LIST
                            , ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList());
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_USER_PROFILE);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                        Collection<String> idListStr = (Collection<String>) ipUserProfileDTO.getCustomParamMap().get(KnConstants.IDLIST);
                        if (idListStr != null) {
                            List idListNew=new ArrayList<String>();
                            idListNew.addAll(idListStr);
                            if(addedOwnerIDList!=null && !addedOwnerIDList.isEmpty()) {
                                idListNew.addAll(addedOwnerIDList);
                            }
                            idListInt = convertStrToIntColl(idListNew);
                        }
                        idType = (String) ipUserProfileDTO.getCustomParamMap().get(KnConstants.IDTYPE);
                        isHierarchyFlow = true;
                    }
                }
            }
            //settting operation and entity id
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            //userProfileExists
            String userProfileId = ipUserProfileDTO.getProfileId();
            Integer conatctListId = ipUserProfileDTO.getModifiedUserProfileDTO().getContactListID();
            KnCorpUserProfileDTO userProfile = userProfileUtil.getUserProfile(userProfileId , xdmsHome, persisterTxn);
            userProfilePersistDTO.setDbUserProfileDTO(userProfile);
            //contact list autoAssign disabled
            if(conatctListId!=null&&conatctListId!=-1){
                int commonContactListRejectionId = sublistInfoUtil.getCommonContactListReject(conatctListId.intValue(),Integer.parseInt(corpId),xdmsHome,persisterTxn);
                knLogger.debug("commonContactListRejectionIdforUPM:",commonContactListRejectionId);
                userProfilePersistDTO.setCommonContactListRejectionId(commonContactListRejectionId);
                KnCorpSublistDTO sublistDetails = sublistInfoUtil
                        .getSublistInfo(conatctListId.intValue(),
                                Integer.parseInt(corpId), xdmsHome, persisterTxn);
                userProfilePersistDTO.setAutoAssign(sublistDetails.getDistributionPolicy());
            }
            //check if profileName already exists to maintain profileName unique
            if(ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileName()!=null){
                String dbProfileName = userProfileUtil
                        .getProfileName(corpProfile.getCorpId(),
                                ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileName(), xdmsHome, persisterTxn);
                userProfilePersistDTO.setDbProfileName(dbProfileName);
                userProfilePersistDTO.setReqProfileName(ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileName());
            }
            boolean skipValidation = false;
            long mcpttProfileMdnCnt = 0;
            boolean isMcpttEnabled = false;
            List<Integer> groupIdList = new ArrayList<>();
            int mcxGrpCnt = 0;
            Set<KnCorpGroupListInfoDTO> addedGroupList = ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList();
            if(addedGroupList!=null && !addedGroupList.isEmpty()) {
                addedGroupList.forEach(knCorpGroupListInfoDTO -> {
                    groupIdList.add(knCorpGroupListInfoDTO.getGroupID());
                });

                knLogger.debug(methodName, "groupIdList - ", groupIdList);

                Map<Integer, KnCorpGroupDTO> groupBasicDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIdList, xdmsHome, persisterTxn);
                List<KnCorpGroupDTO> mcxGroupList = groupBasicDetailsMap.values().stream().
                        filter(value -> (value.getMcxGrpInd() != null && value.getMcxGrpInd() == VLARGE_GROUP.value())).
                        collect(Collectors.toList());
                if (mcxGroupList != null && !mcxGroupList.isEmpty()) {
                    mcxGrpCnt = mcxGroupList.size();
                    List<KnCorpSubscriberDTO> profileMdnList = userProfileUtil.getProfileMdnInfoByUPId(corpId, userProfileId, xdmsHome, persisterTxn);
                    StringBuffer nonComplianceMdnList = new StringBuffer();
                    if(profileMdnList!=null && !profileMdnList.isEmpty()) {
                        mcpttProfileMdnCnt = profileMdnList.stream().filter(pMdn -> {
                                    if ((KnGeneralUtil.getFeatureBitValue(pMdn.getSubscriberFs2(), USER_PROFILE_MGMT_BIT) && KnGeneralUtil.getFeatureBitValue(pMdn.getSubscriberFs2(), VERY_LARGE_GROUP))) {
                                        return true;
                                    } else {
                                        nonComplianceMdnList.append(pMdn.getMdn()).append(",");
                                        return false;
                                    }
                                }).
                                count();
                        knLogger.info(methodName, "mcpttProfileMdnCnt", mcpttProfileMdnCnt);
                        isMcpttEnabled = (profileMdnList.size() == mcpttProfileMdnCnt);
                        knLogger.info(methodName, "isMcpttEnabled", isMcpttEnabled);
                        if (!nonComplianceMdnList.isEmpty()) {
                            knLogger.debug("nonCompliance MDN list are:", nonComplianceMdnList.substring(0, nonComplianceMdnList.length() - 1));
                        }
                    }else {
                        knLogger.info(methodName,"user profile is not assigned to any MDN",userProfileId);
                        knLogger.debug(methodName,"setting mcpttCompliance as true to by pass validation rule");
                        skipValidation = true;

                    }
                }
            }
            //group part of same corp: groupExistsInCorp;
            KnIPCorpInfoDTO corpInfoDTO=new KnIPCorpInfoDTO();
            corpInfoDTO.setCorpId(Integer.parseInt(corpId));
            Collection<KnCorpGroupInfoPersistDTO> groupList = groupInfoUtil.getAllGroupList(corpInfoDTO, 100, xdmsHome, persisterTxn);
            //Get the shared groups.
            Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap = groupInfoUtil.selectGroupSharedCorpInfoBySharedCorpId(Integer.parseInt(corpId),xdmsHome,persisterTxn);
            userProfilePersistDTO.setSharedCorpGrpInfoMap(sharedCorpGrpInfoMap);
            List<KnCorpGroupInfoPersistDTO> sharedGroupList = groupInfoUtil.getSharedGroupDetails(sharedCorpGrpInfoMap, xdmsHome,persisterTxn);
            groupList.addAll(sharedGroupList);

            List<KnCorpGroupInfoPersistDTO> ownerGroupList = null;
            if(ipUserProfileDTO.getModifiedUserProfileDTO()!=null && isHierarchyFlow){
                Set<KnCorpGroupListInfoDTO> reqGroupList = new HashSet<>();
                if(ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedGroupList()!=null)
                    reqGroupList.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedGroupList());
                if(ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList()!=null)
                    reqGroupList.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList());
                if(ipUserProfileDTO.getModifiedUserProfileDTO().getGroupList()!=null)
                    reqGroupList.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getGroupList());
                List<Integer> missingGrpIds = new ArrayList<>();
                for(KnCorpGroupListInfoDTO group : reqGroupList) {
                    int grpId =group.getGroupID().intValue();
                    if( !groupList.contains(grpId) && !sharedGroupList.contains(grpId)){
                        missingGrpIds.add(grpId);
                    }
                }
                if(ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList()!=null){
                    List<String> removedIds = new ArrayList<String>(ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList());
                    for(String grpId : removedIds) {
                        if( !groupList.contains(grpId) && !sharedGroupList.contains(grpId)){
                            missingGrpIds.add(Integer.parseInt(grpId));
                        }
                    }
                }
                if(!missingGrpIds.isEmpty()) {
                    knLogger.info(methodName,"groupids that belogs diffent corp -",missingGrpIds);
                    Map<Integer, Integer> grpIdOwnerMap = groupInfoUtil.isValidGroupForIdList(missingGrpIds, new ArrayList<>(idListInt), idType, xdmsHome, false, persisterTxn);
                    if(grpIdOwnerMap!=null && !grpIdOwnerMap.isEmpty()) {
                        ownerGroupList = groupInfoUtil.getOwnerGroupDetails(grpIdOwnerMap.keySet(), xdmsHome,persisterTxn);
                        groupList.addAll(ownerGroupList);
                    }

                }
            }

            userProfilePersistDTO.setUserProfileId(userProfileId);
            userProfilePersistDTO.setCorpGroupList(groupList);
            userProfilePersistDTO.setModifyUserProfileDTO(ipUserProfileDTO.getModifiedUserProfileDTO());

            //get the Allowed member feature list
            Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap = userProfileUtil.getTrustMatrixMap(corpProfile.getExtCorpId(),
                    generalCacheUtil, commonInfoUtil, xdmsHome, persisterTxn);
            userProfilePersistDTO.setTrustMatrixMap(trustMatrixMap);

            //Check if any active job exists for the profile.
            List<Integer> jobStatusList = generalCacheUtil.getJobStatusByProfileId(userProfileId);
            userProfilePersistDTO.setJobStatusList(jobStatusList);
            userProfilePersistDTO.setUserProfileId(userProfileId);
            userProfilePersistDTO.setMcxGroupCount(mcxGrpCnt);
            userProfilePersistDTO.setMcpttCompliance(isMcpttEnabled);
            userProfilePersistDTO.setSkipValidation(skipValidation);

            //set zone/channle/priority and camped group validation
            int maxZone = corpProfile.getMaxZones();
            userProfilePersistDTO.setMaxZone(maxZone);
            int maxChannelsPerZone = corpProfile.getMaxChannelsPerZone();
            userProfilePersistDTO.setMaxChannelsPerZone(maxChannelsPerZone);
            int maxPriority = corpProfile.getMaxPriority();
            userProfilePersistDTO.setMaxPriority(maxPriority);
            int pttRadioScanSize = corpProfile.getMaxPttRadioScanSize();
            userProfilePersistDTO.setMaxPttRadioScanSize(pttRadioScanSize);

            KnCorpContactInfoUtil contactInfoUtiln= new KnCorpContactInfoUtil();
            Map<String,BitSet> externalPemMap= new HashMap<>();
            if((!ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedPermissionsConfig().isEmpty()) ||
                    !ipUserProfileDTO.getModifiedUserProfileDTO().getAddedMcpttPermissionsConfig().isEmpty()) {
                Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfig= new HashSet<>();
                if(!ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedPermissionsConfig().isEmpty()){
                    mcpttPermissionsConfig=ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedPermissionsConfig();}
                else{
                    mcpttPermissionsConfig=ipUserProfileDTO.getModifiedUserProfileDTO().getAddedMcpttPermissionsConfig();
                }
                List<String> mdnList=new ArrayList<>();
                List<String> externalmebers=new ArrayList<>();
            for(KnCorpUserProfileMCPTTConfig obj:mcpttPermissionsConfig){
                mdnList.add(obj.getMdn());
            }
            Map<String, KnCorpSubscriberDTO> mdnListInfo = contactInfoUtiln.getSubsribersCorporateDetails(mdnList, xdmsHome, persisterTxn);
            for(Map.Entry<String, KnCorpSubscriberDTO> obj : mdnListInfo.entrySet()){
                if(Integer.parseInt(corpId) != obj.getValue().getCorpId()){
                    externalmebers.add(obj.getKey());
                }
            }
            if(!externalmebers.isEmpty()){
                //external subscriber exists
                for(KnCorpUserProfileMCPTTConfig obj:mcpttPermissionsConfig){
                    if(externalmebers.contains(obj.getMdn())){
                        BitSet permSet=KnGeneralUtil.convertLongToBitSet(obj.getPermBitSet());
                        externalPemMap.put(obj.getMdn(),permSet);
                    }
                }
            }}
            userProfilePersistDTO.setExternalPemMap(externalPemMap);
            if (ipUserProfileDTO.getModifiedUserProfileDTO().getContactListID() != null ||
                            (ipUserProfileDTO.getModifiedUserProfileDTO().getEmergencyConfig().getDestAttributes() != null &&
                                    !userProfilePersistDTO.getModifyUserProfileDTO().getEmergencyConfig().getDestAttributes().isEmpty()) && null != userProfile.getContactListID()) {

                Integer contactListId = ipUserProfileDTO.getModifiedUserProfileDTO().getContactListID() != null
                        ? ipUserProfileDTO.getModifiedUserProfileDTO().getContactListID() :
                        userProfile.getContactListID();
                Map<String, Integer> memberInfo = userProfileUtil.getSublistMemberCorpDetails(contactListId, corpId, xdmsHome, persisterTxn);
                userProfilePersistDTO.setSubslistMemCorpInfo(memberInfo);
            }
            userProfilePersistDTO.setCorpId(corpId);
            List<KnCorpContactDTO> sharedAssignedMdns = userProfileUtil.getUserProfileAllSubscriberListDetails(userProfileId, xdmsHome, persisterTxn);
            List<Integer> preConfigParamList = new ArrayList<>();
            List<Integer> largeGroupIdsExceeded=new ArrayList<>();
            if(null != ipUserProfileDTO.getModifiedUserProfileDTO() && null!=ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList()) {
                List<Integer> groupIdsList = ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList().stream().map(e -> e.getGroupID()).collect(Collectors.toList());
                preConfigParamList = groupInfoUtil.getIsPreConfiguredParamList(groupIdsList,xdmsHome,persisterTxn);

                knLogger.debug(methodName," sharedAssignedMdns :",sharedAssignedMdns);
                //large group validation
                if(sharedAssignedMdns.size()>0) {
                    List<Integer> largeGroupIds=new ArrayList<>();
                    List<Integer> largeBCGroupIds=new ArrayList<>();
                    int corpMaxLarge = corpProfile.getMaxLrgGrpPerCorp();
                    int corpMaxLargeBCGroup = corpProfile.getMaxLrgBGrpPerCorp();
                    int corpLargeGroupCount = groupInfoUtil.getLargeGroupCounts(corpProfile.getCorpId(), persisterTxn, xdmsHome).getLrgGrpCountCorp();
                    int corpLargeBCGroupCount = groupInfoUtil.getLargeGroupCounts(corpProfile.getCorpId(), persisterTxn, xdmsHome).getLargeBCGrpCountCorp();

                    List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIdList, xdmsHome, persisterTxn);
                    //Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(groupIdList, xdmsHome, persisterTxn);
                    //Map<String, KnCorpGroupMemberDTO> grpMemMap = grpMemberDetails.stream().collect(Collectors.toMap(KnCorpGroupMemberDTO::getMdn, exist -> exist));
                    Map<Integer, Integer> memberCount = groupInfoUtil.getGroupMemCount(groupIdList, xdmsHome, persisterTxn);
                    for (KnCorpGroupDTO groupInfo : groupInfoList) {
                        //present groupMember +(already assigned group members)
                        int exisitngGroupMembersCount = 0;
                        if (null != memberCount.get(groupInfo.getGroupId())) {
                            exisitngGroupMembersCount = memberCount.get(groupInfo.getGroupId());
                        }
                        int groupMemberCount = exisitngGroupMembersCount + sharedAssignedMdns.size();
                        if (com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP == groupInfo.getGroupType()
                                && groupMemberCount > corpProfile.getMaxMemPerCorpGroup() && !groupInfo.isLargeGroup()) {
                            largeGroupIds.add(groupInfo.getGroupId());
                        } else if (com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DISPATCH_GROUP == groupInfo.getGroupType()
                                && groupMemberCount > corpProfile.getMaxMembersPerDispatchGroup() && !groupInfo.isLargeGroup()) {
                            largeGroupIds.add(groupInfo.getGroupId());
                        } else if (com.kodiak.xdms.server.corpmgmt.resources.KnConstants.BROADCAST_GROUP == groupInfo.getGroupType()
                                && groupMemberCount > corpProfile.getMaxMemPerBCGrp() && !groupInfo.isLargeGroup()) {
                            largeBCGroupIds.add(groupInfo.getGroupId());
                        }
                    }
                    knLogger.debug(methodName," largeGroupIds :",largeGroupIds," largeBCGroupIds:",largeBCGroupIds);
                    knLogger.debug(methodName,"SIZE--->"," largeGroupIds :",largeGroupIds.size()," largeBCGroupIds:",largeBCGroupIds.size());
                    int totalLargeCount = corpLargeGroupCount + largeGroupIds.size();
                    int totalLargeBCCount=corpLargeBCGroupCount+largeBCGroupIds.size();
                    if(totalLargeCount>corpMaxLarge||totalLargeBCCount>corpMaxLargeBCGroup){
                        largeGroupIdsExceeded.addAll(largeGroupIds);
                        largeGroupIdsExceeded.addAll(largeBCGroupIds);
                    }

                }
            }
            userProfilePersistDTO.setIsPreConfiguredGroupList(preConfigParamList);

            Boolean isOwnerCorpReq = userProfile.getCorporateID().equals(Integer.parseInt(corpId));
            userProfilePersistDTO.setOwnerCorpReq(isOwnerCorpReq);

            List<String> reqUserProfileSharedCorpList = ipUserProfileDTO.getModifiedUserProfileDTO().getAddUserProfileSharedCorpList();
            List<KnCorpTrustMatrixDTO> dbTrustMatrixDTOList = generalCacheUtil.getSharedCorpMatrixByExtAndSharingFeature(corpProfile.getExtCorpId());
            List<String> dbTrustMatrixSharedExtCorpIdList = dbTrustMatrixDTOList.stream().map(e -> e.getSharedExtCorpId()).collect(Collectors.toList());
            Map<String,Integer> extIntCorpIDMap = commonInfoUtil.getCorpIdMap(new HashSet<>(dbTrustMatrixSharedExtCorpIdList),null,persisterTxn);
            List<String> dbUserProfileSharedCorpList = extIntCorpIDMap.values().stream().map(String::valueOf).collect(Collectors.toList());
            userProfilePersistDTO.setReqUserProfileSharedCorpList(reqUserProfileSharedCorpList);
            userProfilePersistDTO.setDbUserProfileSharedCorpList(dbUserProfileSharedCorpList);

            //checking assigned shared corp members incase of disabling of userProfile flag
            String sharingFlag = ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileSharingEnabled();
            List<String> removedCorps = ipUserProfileDTO.getModifiedUserProfileDTO().getRemoveUserProfileSharedCorpList();
            if(sharingFlag != null && sharingFlag.equals("0") || (removedCorps!=null && !removedCorps.isEmpty())){
            List<String> assignedMdnList = new ArrayList<>();
            for(KnCorpContactDTO mdnDetails : sharedAssignedMdns){
                if(sharingFlag != null && sharingFlag.equals("0")){
                    //XDM-8493
                if(!dbUserProfileSharedCorpList.contains(String.valueOf(corpProfile.getCorpId())) && dbUserProfileSharedCorpList.contains(String.valueOf(mdnDetails.getCorpId()))){
                    assignedMdnList.add(mdnDetails.getMdn());
                }}
                if(removedCorps!=null && !removedCorps.isEmpty() && removedCorps.contains(String.valueOf(mdnDetails.getCorpId()))
                && !assignedMdnList.contains(mdnDetails.getMdn())){
                    assignedMdnList.add(mdnDetails.getMdn());
                }
            }
            userProfilePersistDTO.setSharedUpm(Boolean.TRUE);
            userProfilePersistDTO.setAssignedMdnList(assignedMdnList);
            }

            int existingGrpSize = 0;
            Set<Integer> uniqueGroupIds = new HashSet<>();
            Set<KnCorpGroupListInfoDTO> groupListFromCbs = userProfilePersistDTO.getDbUserProfileDTO().getGroupList();
            if (null != groupListFromCbs) {
                for (KnCorpGroupListInfoDTO group : groupListFromCbs) {
                    int groupId = group.getGroupID();
                    if (!uniqueGroupIds.contains(groupId)) {
                        uniqueGroupIds.add(groupId);
                    }
                }
            }
            existingGrpSize = uniqueGroupIds.size();
            int removedGroupSize = 0;
            if (null != ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList() && !ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList().isEmpty()) {
                removedGroupSize = ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList().size();
            }
            int modifiedGrpsize = groupIdList.size() - removedGroupSize;
            int newgrpSize = existingGrpSize + modifiedGrpsize;
            userProfilePersistDTO.setGroupListSize(newgrpSize);
            userProfilePersistDTO.setMaxGroupsPerMemberCount(corpProfile.getMaxGroupsPerSubsc());

            userProfilePersistDTO.setLargeGroupIds(largeGroupIdsExceeded);
            //system level large group
            if(largeGroupIdsExceeded.size()>0){
                int LrgGrpCountSystem = groupInfoUtil.getLargeGroupCounts(corpProfile.getCorpId(), persisterTxn, xdmsHome).getLrgGrpCountSystem();
                int MaxLargeGrpSystem = Integer.parseInt(genInfoUtil.retrieveRTXConfigValues(persisterTxn).get(NUM_OF_LG_SUPPORTED));
                if(LrgGrpCountSystem>=MaxLargeGrpSystem){
                    userProfilePersistDTO.setSystemLevelLargeGroupLimitReached(true);
                }
            }
            //XDM-9156: selfDnDPrivilege
            final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            if (null != ipUserProfileDTO.getModifiedUserProfileDTO().getEmergencyConfig().getEmergConfigTimer()) {
                String emergencyFeatureFlag = microServicesParamNameValueMap.get(EMERGENCY_CONF_TIMER_FEATURE);
                boolean emergencyBit = Optional.ofNullable(corpProfile.getXdmCorpFS2Set())
                        .map(set -> KnGeneralUtil.getFeatureBitValue(set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value()))
                        .orElse(false);
                knLogger.debug(methodName, "emergencyFeatureFlag is :-", emergencyFeatureFlag, "emergencyBit is :-", emergencyBit);
                if (!emergencyFeatureFlag.equals(ENABLED_STRING) || !emergencyBit) {
                    knLogger.error("System level or Corporate Level EMERGENCY_CONF_TIMER_FEATURE flag is disabled");
                    throw new KnCorpBOValidationException(EMERGCONFIGTIMER_FEATURE_DISABLED, "EMERGCONFIGTIMER_FEATURE flag is disabled", "");
                }
                Float emeConfTimer = Float.valueOf(ipUserProfileDTO.getModifiedUserProfileDTO().getEmergencyConfig().getEmergConfigTimer());
                Float minEmergenTimer = Float.valueOf(microServicesParamNameValueMap.getOrDefault(MIN_EMERGENCY_TIME, "0"));
                Float maxEmergenTimer = Float.valueOf(microServicesParamNameValueMap.getOrDefault(MAX_EMERGENCY_TIME,"5"));
                knLogger.debug(methodName, "emeConfTimer is :-", emeConfTimer, "minEmeTimer is :-", minEmergenTimer, "maxEmeTimer is :-", maxEmergenTimer);
                if (emeConfTimer < minEmergenTimer || emeConfTimer > maxEmergenTimer) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGCONFIGTIMER_NOT_IN_RANGE, "Emergency Timer value not in range", "");
                }
            }
            final String sysSelfDndFeature = microServicesParamNameValueMap.get(SELF_DND_FEATURE);
            String corpFS2 = corpProfile.getCorpFS2();
            BitSet corpAdminBitSet = KnGeneralUtil.convertHexStringToBitSet(corpFS2);
            boolean selfDndPrivilegeBit = corpAdminBitSet.get(KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value());

            userProfilePersistDTO.setSysSelfDndPrivilege(ENABLED_STRING.equals(sysSelfDndFeature));
            userProfilePersistDTO.setCorpSelfDndPrivilege(selfDndPrivilegeBit);
            if (null != ipUserProfileDTO.getModifiedUserProfileDTO() &&
                    null != ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileFSDto() &&
                    null != ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileFSDto().getSelfDnDPrivilege() &&
                    !ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileFSDto().getSelfDnDPrivilege().isEmpty()) {
                userProfilePersistDTO.setReqSelfDndPrivilege(Integer.parseInt(
                        ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileFSDto().getSelfDnDPrivilege()));
            }

            //To check the MAX LOC WATCHER PER GROUP validation
            Set<KnCorpGroupListInfoDTO> knCorpGroupListInfoDTOS1 = ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedGroupList();
            Set<KnCorpGroupListInfoDTO> knCorpGroupListInfoDTOS = ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList();
            List<KnCorpSubscriberDTO> profileMdnList = userProfileUtil.getProfileMdnInfoByUPId(corpId, userProfileId, xdmsHome, persisterTxn);
            int MaxLocWachersPerGroup = Integer.parseInt(genInfoUtil.retrieveRTXConfigValues(persisterTxn).get(MAX_LOC_WATCHERS_PER_GRP));
            boolean MaxLocWachersPerGroupReached = false;
            if (null != addedGroupList && !addedGroupList.isEmpty()) {
                Map<Integer, Integer> lockWathercount = groupInfoUtil.getMaxLocWatchersCount(groupIdList, xdmsHome , persisterTxn);
                List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIdList, xdmsHome, persisterTxn);
                if (null != knCorpGroupListInfoDTOS && !knCorpGroupListInfoDTOS.isEmpty()) {
                    for (KnCorpGroupListInfoDTO groupInfo : knCorpGroupListInfoDTOS) {
                        for (KnCorpGroupDTO groupInfoDto : groupInfoList) {
                            if (groupInfo.getGrpMemProps().getIsLocSupervisor() == 1 && com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP == groupInfoDto.getGroupType()) {
                                for (int groupID : groupIdList) {
                                    if (lockWathercount.containsKey(groupID) && ((lockWathercount.get(groupID) + profileMdnList.size()) > MaxLocWachersPerGroup)
                                            || (profileMdnList.size() > MaxLocWachersPerGroup)) {
                                        MaxLocWachersPerGroupReached = true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (null != knCorpGroupListInfoDTOS1 && !knCorpGroupListInfoDTOS1.isEmpty()) {
                    for (KnCorpGroupListInfoDTO groupInfo : knCorpGroupListInfoDTOS1) {
                        Collection<Integer> myCollection = new ArrayList<>();
                        myCollection.add(groupInfo.getGroupID());
                        Map<Integer, Integer> lockWathercount = groupInfoUtil.getMaxLocWatchersCount(myCollection, xdmsHome , persisterTxn);
                        List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(myCollection, xdmsHome, persisterTxn);
                        for (KnCorpGroupDTO groupInfoDto : groupInfoList) {
                            if (groupInfo.getGrpMemProps().getIsLocSupervisor() == 1 && com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP == groupInfoDto.getGroupType()) {
                                if (lockWathercount.containsKey(groupInfo.getGroupID()) && ((lockWathercount.get(groupInfo.getGroupID()) + profileMdnList.size()) > MaxLocWachersPerGroup)
                                        || (profileMdnList.size() > MaxLocWachersPerGroup)) {
                                    MaxLocWachersPerGroupReached = true;
                                }
                            }
                        }
                    }
                }
            }
            userProfilePersistDTO.setMaxLocWachersPerGroupReached(MaxLocWachersPerGroupReached);
            //emergency validation
            KnSubsEmergencyConfigDTO ipEmergencyInfoDTO = ipUserProfileDTO.getModifiedUserProfileDTO().getEmergencyConfig();
            Set<KnDestinationAttributeDTO> destAttributes = ipUserProfileDTO.getModifiedUserProfileDTO().getEmergencyConfig().getDestAttributes();
            KnUserEmergencyAttributes emergencyAttributes = new KnUserEmergencyAttributes();
            KnCorpContactInfoUtil contactInfoUtil = new KnCorpContactInfoUtil();
            Map<String, Collection<String>> mdnContactList = new HashMap<>();
            Map<String, String> mdnExistenceInNormalGroup = new HashMap<>();
            Collection<String> groupExist = new ArrayList<>();
            Map<String, Integer> groupIdTypesMap = new HashMap<>();
            Collection<String> broadcastGroup = new ArrayList<>();
            Collection<String> contactDoesNotExists = new ArrayList<>();
            Collection<String> groupIds = new ArrayList<>();
            Collection<String> profileMdnCollection = new ArrayList();
            Collection<Integer> groupDoesNotExists = new ArrayList<>();
            Set<KnCorpGroupListInfoDTO> mcxGroupList = new HashSet<>();
            int mcxGroupCount = 0;
            List<KnCorpGroupDTO> groupsInfoList = new ArrayList<>();
            boolean primaryDestinationIsEmpty = false;
            boolean allowRequestGroupId = false;
            if (null != profileMdnList && !profileMdnList.isEmpty() && (null != ipEmergencyInfoDTO.getPermission() && ipEmergencyInfoDTO.getPermission() == 1)) {
                for (KnCorpSubscriberDTO profileMdn : profileMdnList) {
                    profileMdnCollection.add(profileMdn.getMdn());
                }
                for (KnDestinationAttributeDTO knDestinationAttributeDTO : destAttributes) {
                    if (null != knDestinationAttributeDTO.getDestType() && knDestinationAttributeDTO.getDestType() == 1
                            && null != knDestinationAttributeDTO.getDestURI()) {
                        groupIds.add(knDestinationAttributeDTO.getDestURI());
                    } else if (null != knDestinationAttributeDTO.getDestType() && knDestinationAttributeDTO.getDestType() == 2) {
                        mdnContactList = contactInfoUtil.getSubscribersContactList(profileMdnCollection, xdmsHome, persisterTxn);
                    }
                }
                Collection<Integer> groupIdslist = groupIds.stream()
                        .map(Integer::parseInt)
                        .toList();
                for (String profileMdn : profileMdnCollection) {
                    mdnExistenceInNormalGroup = groupInfoUtil.selectGroupMemberForGroupIds(groupIdslist, profileMdn, xdmsHome, persisterTxn);
                    break;
                }
                //Adding groups to the upm and same group as destination
                if (mdnExistenceInNormalGroup.isEmpty() && addedGroupList != null && !addedGroupList.isEmpty()) {
                    allowRequestGroupId = true;
                }
                mcxGroupList = userProfileUtil.retriveGroupProfileInfoByProfileId(userProfileId, xdmsHome, true, persisterTxn);
                if (null != mcxGroupList && !mcxGroupList.isEmpty()) {
                    for (KnCorpGroupListInfoDTO group : mcxGroupList) {
                        if (null != group.getGroupID() && groupIdslist.contains(group.getGroupID())) {
                            groupExist.add(group.getGroupID().toString());
                        }
                    }
                }
                groupIdTypesMap = groupInfoUtil.getValidGrpTypeInCorp(groupIds, Integer.parseInt(corpId),
                        xdmsHome, persisterTxn);
                groupsInfoList = groupInfoUtil.getGroupBasicInfoList(groupIdslist, xdmsHome, persisterTxn);

            }

            if (null != ipEmergencyInfoDTO.getPermission() && ipEmergencyInfoDTO.getPermission() == 1 && KnConstants.DESTINATION_TYPE.CAT_CONFIGURED_DESTINATION.value() ==
                    ipEmergencyInfoDTO.getDestType() && null != profileMdnList && !profileMdnList.isEmpty()) {
                if (destAttributes != null && !destAttributes.isEmpty()) {
                    for (KnDestinationAttributeDTO emgDestAttr : destAttributes) {
                        if (emgDestAttr != null && emgDestAttr.getDestCat() != null) {
                            if (emgDestAttr.getDestCat().equals(1)) {
                                emergencyAttributes.setPriDestination(emgDestAttr.getDestURI());
                            } else {
                                emergencyAttributes.setSecDestination(emgDestAttr.getDestURI());
                            }
                        }
                    }
                }
                if (null == emergencyAttributes.getPriDestination()) {
                    primaryDestinationIsEmpty = true;
                }
                for (KnDestinationAttributeDTO inputDest : destAttributes) {
                    if (inputDest != null) {
                        if (groupIdTypesMap != null && groupIdTypesMap.containsKey(inputDest.getDestURI())) {
                            if (groupIdTypesMap.get(inputDest.getDestURI()) == 3) {
                                broadcastGroup.add(inputDest.getDestURI());
                            }
                        }
                    }
                }
                if (!broadcastGroup.isEmpty()) {
                    userProfilePersistDTO.setDestIsBroadCastGroup(true);
                }
                for (KnDestinationAttributeDTO knDestinationAttributeDTO : destAttributes) {
                    if (null != knDestinationAttributeDTO.getDestType() && knDestinationAttributeDTO.getDestType() == 1
                            && null != knDestinationAttributeDTO.getDestURI()
                            && mdnExistenceInNormalGroup.isEmpty() && !allowRequestGroupId) {
                        groupDoesNotExists.add(Integer.parseInt(knDestinationAttributeDTO.getDestURI()));
                    } else if (null != knDestinationAttributeDTO.getDestType() && knDestinationAttributeDTO.getDestType() == 2 && null !=
                            knDestinationAttributeDTO.getDestURI() && !mdnContactList.isEmpty()) {
                        for (Collection<String> values : mdnContactList.values()) {
                            if (!values.contains(knDestinationAttributeDTO.getDestURI())) {
                                contactDoesNotExists.add(knDestinationAttributeDTO.getDestURI());
                                userProfilePersistDTO.setSublistDoesNotBelogsToUPM(true);
                                break;
                            }
                        }
                    }
                }

                for(KnCorpGroupDTO knCorpGroupDTO : groupsInfoList){
                    if(groupExist.contains(String.valueOf(knCorpGroupDTO.getGroupId())) && knCorpGroupDTO.getMcxGrpInd() == MCX_GROUP_INDICATOR_VALUE){
                        mcxGroupCount++;
                    }
                }

                if (!groupDoesNotExists.isEmpty() && mcxGroupCount == MCX_GROUP_COUNT) {
                    userProfilePersistDTO.setGroupDoesNotBelogsToUPM(true);
                }
                if (!contactDoesNotExists.isEmpty()) {
                    userProfilePersistDTO.setSublistDoesNotBelogsToUPM(true);
                }
            }
            userProfilePersistDTO.setPrimaryDestinationIsEmpty(primaryDestinationIsEmpty);

            //Max member per group validation
            List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIdList, xdmsHome, persisterTxn);
//            Map<Integer, Integer> membersCount = groupInfoUtil.getGroupMemsCounts(groupIdList, xdmsHome , persisterTxn);
//            Map<Integer, Integer> memberCount = groupInfoUtil.getGroupMemCount(groupIdList, xdmsHome, null);
            int exisitngGroupMembersCountFromMemList = 0;
            int groupMemberCounts = 0;
            boolean maxMembersAllowedPerGroupIsReached = false;
            int groupType = 0;
            int maxMembersAllowedPerGroup = 0;
            for (KnCorpGroupDTO groupInfo : groupInfoList) {
//                if (null != memberCount.get(groupInfo.getGroupId())) {
//                    exisitngGroupMembersCountFromMemList = memberCount.get(groupInfo.getGroupId());
//                }
                List<String> existingMemList = sublistInfoUtil.selectGroupMemberList(groupInfo.getGroupId(), xdmsHome, persisterTxn);
                List<String> allMemList = new ArrayList<>(existingMemList);
                allMemList.addAll(profileMdnList.stream().map(KnCorpSubscriberDTO::getMdn).toList());
                List<String> realMdns = groupInfoUtil.getRealMdns(allMemList, xdmsHome, true, persisterTxn);
                groupMemberCounts = realMdns.size();

                if (groupInfo.getGroupType() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP) {
                    if (groupInfo.isLargeGroup() || corpProfile.getLargeGrpSupported() == 1) {
                        maxMembersAllowedPerGroup = corpProfile.getMaxMemPerLrgGrp();
                    } else {
                        maxMembersAllowedPerGroup = corpProfile.getMaxMemPerCorpGroup();
                    }
                } else if (groupInfo.getGroupType() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DISPATCH_GROUP) {
                    if (groupInfo.isLargeGroup() || corpProfile.getLargeGrpSupported() == 1) {
                        maxMembersAllowedPerGroup = corpProfile.getMaxMemPerLrgGrp();
                    } else {
                        maxMembersAllowedPerGroup = corpProfile.getMaxMembersPerDispatchGroup();
                    }
                } else if (groupInfo.getGroupType() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.BROADCAST_GROUP) {
                    if (groupInfo.isLargeGroup() || corpProfile.getLargeGrpSupported() == 1) {
                        maxMembersAllowedPerGroup = corpProfile.getMaxMemPerLrgBGrp();
                    } else {
                        maxMembersAllowedPerGroup = corpProfile.getMaxMemPerBCGrp();
                    }
                }
                if (groupMemberCounts > maxMembersAllowedPerGroup
                        && (groupInfo.getMcxGrpInd() != null && groupInfo.getMcxGrpInd() != 1)) {
                    knLogger.debug("groupMemberCounts::",groupMemberCounts,"maxMembersAllowedPerGroup::",maxMembersAllowedPerGroup);
                    maxMembersAllowedPerGroupIsReached = true;
                    groupType = groupInfo.getGroupType();
                }
            }
            userProfilePersistDTO.setMaxMembersAllowedPerGroupIsReached(maxMembersAllowedPerGroupIsReached);
            userProfilePersistDTO.setGroupType(groupType);

            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.info(methodName, "Validation Successfull");
            respDTO.setTgscMode(ipUserProfileDTO.getModifiedUserProfileDTO().getTgscMode());
            //setting tgsc_mode=0 when all groups are removed from upm
            Set<String> reqRemovedGroupIds =ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList();
            Set<KnCorpGroupListInfoDTO> upmDBgroupList = userProfile.getGroupList();
            knLogger.debug(methodName," reqRemovedGroupIds :",reqRemovedGroupIds," upmDBgroupList :",upmDBgroupList);
            if (reqRemovedGroupIds != null && upmDBgroupList != null && (addedGroupList == null || addedGroupList.isEmpty())) {
                Set<String> dbGroupList = upmDBgroupList.stream().map(e -> String.valueOf(e.getGroupID())).collect(Collectors.toSet());
                Set<String> actualGroups = new HashSet<>(dbGroupList);
                actualGroups.removeAll(reqRemovedGroupIds);
                if (actualGroups.isEmpty()) {
                    respDTO.setTgscMode("0");
                }
            }



            /*List<String> profileIds = new ArrayList<>();
            profileIds.add(userProfileId);
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = userProfileUtil.profileMdnEtagUpdate(profileIds, corpId,null, xdmsHome, persisterTxn);
            respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if(profileMdnEtagMap != null){
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }*/
            populate(respDTO);
        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO modifyCBUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "modifyCBUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId = ipUserProfileDTO.getCorpId();
            String userProfileId=ipUserProfileDTO.getProfileId();

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();

            //XDM-9156: selfDnDPrivilege
            final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            int localClusterId = clusterId;
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            ipUserProfileDTO = commonInfoUtil.selfDndPrivilegeCheck(ipUserProfileDTO, corpProfile, microServicesParamNameValueMap);

            KnUserProfileFSDTO reqUpmFs = ipUserProfileDTO.getUserProfileFSDto();
            knLogger.info(methodName, "reqUpmFs :", reqUpmFs);
            if (reqUpmFs != null) {
                String defaultUpmFs = featureSetUtil.getDefFinalUserProfileFS();
                String finalUpmFsUsingDefUpmFs = userProfileUtil.getUpmFsBasedOnDefUpmFs(defaultUpmFs, reqUpmFs);
                String userProfileFeatureSet = featureSetUtil.generateUserProfileFeatureSet(finalUpmFsUsingDefUpmFs);
                knLogger.info(methodName, "userProfileFeatureSet-->UserProfileFS", userProfileFeatureSet);
                ipUserProfileDTO.getModifiedUserProfileDTO().setFeatureBS(userProfileFeatureSet);
            } else {
                //if UPMFS null in request setting defaultUpmFs
                String defaultUpmFs = featureSetUtil.getDefFinalUserProfileFS();
                String userProfileFeatureSet = featureSetUtil.generateUserProfileFeatureSet(defaultUpmFs);
                knLogger.info(methodName, "defaultUPMFS-->", userProfileFeatureSet);
                ipUserProfileDTO.getModifiedUserProfileDTO().setFeatureBS(userProfileFeatureSet);
            }

            userProfileUtil.updateUserProfile(corpId,userProfileId,ipUserProfileDTO.getModifiedUserProfileDTO(),xdmsHome,persisterTxn);
            KnUserProfileInfoDTO fetchedUserProfileInfo=generalCacheUtil.getUserProfileInfoByProfileId(userProfileId);
            if(fetchedUserProfileInfo==null){
                KnCorpUserProfileDTO userProfile = userProfileUtil.getUserProfile(corpId,userProfileId , xdmsHome, persisterTxn);
                //Inserting to DG.USER_PROFILE_INFO
                KnUserProfileInfoDTO userProfileInfo=new KnUserProfileInfoDTO();
                if(ipUserProfileDTO.getModifiedUserProfileDTO().getTgscMode()!=null){
                userProfileInfo.setTgscMode(Integer.valueOf(ipUserProfileDTO.getModifiedUserProfileDTO().getTgscMode()));
                }
                userProfileInfo.setUserProfileId(userProfileId);
                userProfileInfo.setUserprofileIndex(userProfile.getUserProfileIndex());
                generalCacheUtil.insertUserProfileInfo(userProfileInfo);
            }else if(ipUserProfileDTO.getModifiedUserProfileDTO().getTgscMode()!=null){
                //Updating DG.USER_PROFILE_INFO
                KnUserProfileInfoDTO userProfileInfo=new KnUserProfileInfoDTO();
                userProfileInfo.setTgscMode(Integer.valueOf(ipUserProfileDTO.getModifiedUserProfileDTO().getTgscMode()));
                userProfileInfo.setUserProfileId(userProfileId);
                generalCacheUtil.updateUserProfileInfo(userProfileInfo);
            }

            List<Integer> groupIds = new ArrayList<>();
            Set<KnCorpGroupListInfoDTO> groupListForProfileInfo = new HashSet<>();
            if(ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList()!= null){
                groupIds.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList().stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList()));
                groupListForProfileInfo.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getAddedGroupList());
            }
            if(ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedGroupList()!= null){
                groupIds.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedGroupList().stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList()));
                groupListForProfileInfo.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getModifiedGroupList());
            }
            if(ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList() != null){
                groupIds.addAll(ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedGroupIdsList().stream().map(s->Integer.parseInt(s)).collect(Collectors.toList()));
            }

            knLogger.debug(methodName, "List of groupIds", groupIds);
            if(!groupIds.isEmpty()) {
                List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHome, persisterTxn);
                List<Integer> mcxGroupIds = groupInfoList.stream().filter(x -> x.getMcxGrpInd() == 1).map(KnCorpGroupDTO::getGroupId).collect(Collectors.toList());
                groupListForProfileInfo.removeIf(x -> !(mcxGroupIds.contains(x.getGroupID())));
                if(!mcxGroupIds.isEmpty()){
                    userProfileUtil.deleteProfileGroupInfo(userProfileId,mcxGroupIds, xdmsHome, persisterTxn);
                    knLogger.debug(methodName, "delete ProfileGroupInfo completed ");
                    if(!groupListForProfileInfo.isEmpty()){
                        knLogger.debug(methodName, "insert ProfileGroupInfo groupListForProfileInfo ", groupListForProfileInfo);
                        // For list of groups get the properties and add to DG.PROFILE_GROUP_INFO
                        userProfileUtil.insertProfileGroupInfo(userProfileId, groupListForProfileInfo, xdmsHome, persisterTxn);
                        knLogger.debug(methodName, "insert ProfileGroupInfo completed ");
                        Map<Integer, KnCorpGroupContactDTO > mcxGrpMemberShipMap = new HashMap<>();
                        for(KnCorpGroupListInfoDTO infoDTO : groupListForProfileInfo){
                            mcxGrpMemberShipMap.put(infoDTO.getGroupID(), new KnCorpGroupContactDTO(infoDTO.getGrpMemProps().getIsSupervisor(),
                                    infoDTO.getGrpMemProps().getIsBroadcaster(), infoDTO.getGrpMemProps().getIsLocSupervisor(), infoDTO.getGrpMemProps().getIsOSMAuthorized(),
                                    infoDTO.getGrpMemProps().getCallInitiateAllowed(), infoDTO.getGrpMemProps().getCallTerminateAllowed(), infoDTO.getGrpMemProps().getIncallAllowed()
                            ,   infoDTO.getGrpMemProps().getVideoCallInitiateAllowed(),  infoDTO.getGrpMemProps().getVideoCallReceiveAllowed(),  infoDTO.getGrpMemProps().getVideoInCallAllowed()));
                        }
                        respDTO.setMcxGrpInd(1);
                        respDTO.setMcxGrpMemberShipMap(mcxGrpMemberShipMap);
                        respDTO.setUserProfileId(userProfileId);
                        String pochomeAutoAssignValue = microServicesParamNameValueMap.get(POCHOME_AUTOASSIGN_FLAG);
                        boolean pochomeAutoAssignFlag = true;
                        if (pochomeAutoAssignValue != null) {
                            try {
                                pochomeAutoAssignFlag = Integer.parseInt(pochomeAutoAssignValue) == ENABLED;
                            } catch (NumberFormatException e) {
                                knLogger.warn(methodName, "Invalid pochomeAutoAssignValue: ", pochomeAutoAssignValue);
                            }
                        }
                        knLogger.debug(methodName," pochomeAutoAssignFlag : ",pochomeAutoAssignFlag);
                        if(!pochomeAutoAssignFlag){
                            // MCX group pochome lazy assignment
                            // groupListForProfileInfo already contains only MCX groups (added + modified)
                            List<Integer> mcxGrpIds = groupListForProfileInfo.stream()
                                    .map(KnCorpGroupListInfoDTO::getGroupID)
                                    .collect(Collectors.toList());
                            knLogger.debug(methodName, " modifyCBUserProfile: userProfileId=", userProfileId, " mcxGrpIds=", mcxGrpIds);
                            List<Integer> nullPocHomeGrpIds = groupInfoUtil.getGroupsWithNullPocHome(mcxGrpIds, xdmsHome, persisterTxn);
                            knLogger.debug(methodName, "groupsWithNullPocHome=", nullPocHomeGrpIds);
                            if (!nullPocHomeGrpIds.isEmpty()) {
                                List<String> profileMdns = userProfileUtil.getProfileMdnByUPId(corpId, userProfileId, xdmsHome, persisterTxn);
                                knLogger.debug(methodName, "profileMdns=", profileMdns);
                                if (profileMdns != null && !profileMdns.isEmpty()) {
                                    // find first profile MDN that has a valid pocHome — single-MDN call is a direct lookup, no cluster algorithm
                                    Map<String, Object> pocMap = null;
                                    for (String mdn : profileMdns) {
                                        Map<String, Object> tempMap = groupInfoUtil.getPocHomeAndClusterIdForCorpGroup(
                                                Collections.singletonList(mdn), xdmsHome, persisterTxn);
                                        knLogger.debug(methodName, "checking mdn=", mdn, " => pocHome=", tempMap.get(POCHOME), " clusterId=", tempMap.get(CLUSTERID));
                                        if (tempMap.get(POCHOME) != null) {
                                            pocMap = tempMap;
                                            knLogger.debug(methodName, "found valid pocHome from mdn=", mdn);
                                            break;
                                        }
                                    }
                                    if (pocMap != null) {
                                        String clusterIdValue = null;
                                        Integer clusterIdIntValue = (Integer)pocMap.get(CLUSTERID);
                                        if(pocMap.get(POCHOME) != null && clusterIdIntValue == null){
                                            clusterIdValue = groupInfoUtil.fetchClusterId((String) pocMap.get(POCHOME), String.valueOf(localClusterId));
                                            clusterIdIntValue = clusterIdValue != null ? Integer.parseInt(clusterIdValue) : null;
                                        }
                                        for (Integer grpId : nullPocHomeGrpIds) {
                                            knLogger.debug(methodName, "updating groupId=", grpId, " pocHome=", pocMap.get(POCHOME), " clusterId=", clusterIdIntValue);
                                            if(pocMap.get(POCHOME) != null && clusterIdIntValue != null){
                                                groupInfoUtil.updatePocHome((String) pocMap.get(POCHOME),
                                                        clusterIdIntValue, grpId, xdmsHome, persisterTxn);
                                            }
                                        }
                                    } else {
                                        knLogger.warn(methodName, "no profile MDN has valid pocHome — skipping pochome update for groups: ", nullPocHomeGrpIds);
                                    }
                                } else {
                                    knLogger.warn(methodName, "no profileMdns found for userProfileId=", userProfileId);
                                }
                            } else {
                                knLogger.debug(methodName, "all MCX groups already have pocHome — nothing to update");
                            }
                        }
                    }

                }
            }
            //updating USERPROFILE_HIERARCHY_MAP
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                List<String> addedOwnerIDListReq = ipUserProfileDTO.getAddedOwnerIDList();
                List<String> removedOwnerIDListReq = ipUserProfileDTO.getRemovedOwnerIDList();

                if(addedOwnerIDListReq!=null&&!addedOwnerIDListReq.isEmpty()){
                    String idType = (String) customParams.get(KnConstants.IDTYPE);
                    userProfileUtil.insertUserProfileHiearchyMap(userProfileId, addedOwnerIDListReq,idType, xdmsHome, persisterTxn);
                }

                if(removedOwnerIDListReq!=null&&!removedOwnerIDListReq.isEmpty()){
                    String idType = (String) customParams.get(KnConstants.IDTYPE);
                    userProfileUtil.removeUserProfileHiearchyMapByOwnerIds(userProfileId, removedOwnerIDListReq,idType, xdmsHome, persisterTxn);
                }
            }
            List<String> reqUserProfileSharedCorpList = ipUserProfileDTO.getModifiedUserProfileDTO().getAddUserProfileSharedCorpList();
            String sharingFlag = ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileSharingEnabled();

            if(sharingFlag != null && sharingFlag.equals("0")){
                // there should not be any other corp subs tpo perform this operation
                knLogger.debug(methodName, "profile sharing is disabled hence deleting data from the DB");
                userProfileUtil.deleteProfileSharedList(userProfileId, xdmsHome, persisterTxn);
            }
            List<String> sharedCorpLisToBeRemoved = ipUserProfileDTO.getModifiedUserProfileDTO().getRemoveUserProfileSharedCorpList();
            if(sharedCorpLisToBeRemoved!=null && !sharedCorpLisToBeRemoved.isEmpty()){
                knLogger.debug(methodName, "Shared corpId(s) are removing from the DB ");
                userProfileUtil.deleteCorpInfoFromUserProfileSharedList(userProfileId,sharedCorpLisToBeRemoved,xdmsHome,persisterTxn);
            }
            if(reqUserProfileSharedCorpList!=null&&!reqUserProfileSharedCorpList.isEmpty()){
                knLogger.debug(methodName, "Adding new corpId(s) to the shared list");
                List<KnUserprofileSharedlistDTO> upmShardCorpList=new ArrayList<>();
                for(String sharedCorpId:reqUserProfileSharedCorpList){
                    upmShardCorpList.add(new KnUserprofileSharedlistDTO(userProfileId,
                            Integer.parseInt(ipUserProfileDTO.getCorpId())
                            ,Integer.parseInt(sharedCorpId)));
                }
                userProfileUtil.insertUserProfileSharedList(upmShardCorpList,xdmsHome, persisterTxn);
            }

            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO deleteUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "deleteUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId = ipUserProfileDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.DELETE_USER_PROFILE);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            //settting operation and entity id
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            //userProfileExists
            String userProfileId = ipUserProfileDTO.getProfileId();
            KnCorpUserProfileDTO userProfile = userProfileUtil.getUserProfile(userProfileId , xdmsHome, persisterTxn);
            knLogger.debug(methodName, "userProfile: "+userProfile);
            userProfilePersistDTO.setDbUserProfileDTO(userProfile);
            userProfilePersistDTO.setCorpId(corpId);
            boolean sharedUpm = userProfile.getCorporateID().equals(Integer.parseInt(corpId));
            knLogger.debug(methodName,"sharedUpm: " +sharedUpm);
            userProfilePersistDTO.setSharedUpm(sharedUpm);
            List<Integer> sharedCorpListId = userProfileUtil.getSharedCorpIdFromUserProfielId(ipUserProfileDTO.getProfileId(), xdmsHome, persisterTxn);
            //User profile assigned to subs check
            Collection<KnMDNInfoDto> assignedMdns  = userProfileUtil.getUserProfileSubscriberList(Integer.parseInt(corpId),
                    userProfileId,0,0,xdmsHome, false, persisterTxn);
            List<String> assignedMdnList = new ArrayList<>();
            if ( sharedCorpListId != null && !sharedCorpListId.isEmpty() ) {
                userProfilePersistDTO.setSharedCorpListId(sharedCorpListId);
                Collection<KnMDNInfoDto> sharedAssignedMdns = userProfileUtil.getUserProfileAllSubscriberList(userProfileId,
                        0,0
                        , xdmsHome, false, persisterTxn);
                knLogger.debug(methodName, "sharedAssignedMdns: " +sharedAssignedMdns);
                if( sharedAssignedMdns != null && !sharedAssignedMdns.isEmpty() ) {
                    for (KnMDNInfoDto assignedMdn : sharedAssignedMdns) {
                        assignedMdnList.add(assignedMdn.getMdn());
                        knLogger.debug(methodName, "assignedMdnList: " + assignedMdnList);
                    }
                }
            }
            if(assignedMdns!=null && !assignedMdns.isEmpty()) {
                for (KnMDNInfoDto assignedMdn : assignedMdns) {
                    assignedMdnList.add(assignedMdn.getMdn());
                }
            }
            userProfilePersistDTO.setAssignedMdnList(assignedMdnList);
            //Check if any active job exists for the profile.
            List<Integer> jobStatusList = generalCacheUtil.getJobStatusByProfileId(userProfileId);
            userProfilePersistDTO.setJobStatusList(jobStatusList);
            userProfilePersistDTO.setUserProfileId(userProfileId);

            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            userProfileUtil.deleteUserProfile(ipUserProfileDTO.getCorpId(),ipUserProfileDTO.getProfileId(),xdmsHome,persisterTxn);
            //removing from DG.USER_PROFILE_INFO
            generalCacheUtil.deleteUserProfileInfo(ipUserProfileDTO.getProfileId());

            // For list of groups get the properties and delete to DG.PROFILE_GROUP_INFO
            userProfileUtil.deleteProfileGroupInfoByProfileId(ipUserProfileDTO.getProfileId(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "delete from ProfileGroupInfo completed ");
            // Delete data from deleteProfileSharedList i.e.,DG.USERPROFILE_SHAREDLIST
            userProfileUtil.deleteProfileSharedList(ipUserProfileDTO.getProfileId(), xdmsHome, persisterTxn);
            //Delete data from DG.USERPROFILE_HIERARCHY_MAP when heirarchy flag is enabled
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY){
                userProfileUtil.deleteUserProfileHiearchyMap(ipUserProfileDTO.getProfileId(),xdmsHome,persisterTxn);
                knLogger.debug(methodName, "delete from user profile heirarchy is  done ");
            }
            //Deleting the Job from UPM after UPM deletion MINT-15798
            generalCacheUtil.deleteStaleFromGG(userProfileId);
            populate(respDTO);
        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

	public KnCorpResponseDTO getUserProfileDetails(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        String methodName = "getUserProfileDetails(KnIPUserProfileDTO,boolean,KnPersisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.info(methodName, "--->clusterId - ", clusterId);
        final KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "--->ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId = ipUserProfileDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Collection<Integer> idListInt = null;
            String idType = null;
            boolean isHierarchyFlow = false;
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_USER_PROFILE_DETAILS);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                    Collection<String> idListStr = (Collection<String>) ipUserProfileDTO.getCustomParamMap().get(KnConstants.IDLIST);
                    if (idListStr != null) {
                         idListInt = convertStrToIntColl(idListStr);
                    }
                    idType = (String) ipUserProfileDTO.getCustomParamMap().get(KnConstants.IDTYPE);
                    isHierarchyFlow = true;
                    userProfileUtil.validateUPMRequest(ipUserProfileDTO.getProfileId(),idListInt,xdmsHome,persisterTxn);

                }
            }
            List<Integer> sharedCorpListId = userProfileUtil.getSharedCorpIdFromUserProfielId(ipUserProfileDTO.getProfileId(), xdmsHome, readOnly, persisterTxn);
            List<String> sharedCorpIdListString = new ArrayList<>();
            if(sharedCorpListId != null && !sharedCorpListId.isEmpty()) {
                sharedCorpIdListString = sharedCorpListId.stream().map(each -> String.valueOf(each)).collect(Collectors.toList());
            }

            //settting operation and entity id
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            String userProfileId = ipUserProfileDTO.getProfileId();
            //user profile exits
            //KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(corpId,userProfileId,xdmsHome,persisterTxn);
            KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(userProfileId,xdmsHome,persisterTxn);

            userProfilePersistDTO.setUserProfileDTO(userProfile);
            userProfilePersistDTO.setUserProfileId(userProfileId);
            userProfilePersistDTO.setDbUserProfileSharedCorpList(sharedCorpIdListString);
            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.info(methodName, "Validation Successfull");

       /*     String inputCorpId = ipUserProfileDTO.getCorpId();
            if (!inputCorpId.equalsIgnoreCase(String.valueOf(userProfile.getCorporateID()))
                    && sharedCorpListId.contains(inputCorpId)) {
                userProfile.setSharingEnabled(Boolean.TRUE);
            } else {
                userProfile.setSharingEnabled(Boolean.FALSE);
            }*/
            // Change added for MINT-25965
            KnIPUserProfileDTO modifyDto = new KnIPUserProfileDTO();
            String newUserProfileFS = featureSetUtil.generateUserProfileFeatureSet(userProfile.getFeatureBS());
            boolean areFeatureBitsEqual = userProfileUtil.compareHexBits(userProfile.getFeatureBS(), newUserProfileFS);
            if (!areFeatureBitsEqual) {
                if (modifyDto.getModifiedUserProfileDTO() == null) {
                    modifyDto.setModifiedUserProfileDTO(new KnCorpModifyUserProfileDTO());
                }
                modifyDto.getModifiedUserProfileDTO().setFeatureBS(newUserProfileFS);
                userProfileUtil.updateUserProfile(corpId, userProfileId, modifyDto.getModifiedUserProfileDTO(), xdmsHome, persisterTxn);
                userProfile.setFeatureBS(newUserProfileFS);
            }
            KnCorpUserProfileDTO staleUserProfileElements=new KnCorpUserProfileDTO();
            Set<KnCorpGroupListInfoDTO> cbsStaleGroupSet=new HashSet<>();
            List<KnCorpGroupListInfoDTO> duplicateGroupDTOs = new ArrayList<>();
            if (userProfile.getGroupList() != null && !userProfile.getGroupList().isEmpty()) {
                //Map<Integer, KnCorpGroupListInfoDTO> allGroupMap = userProfile.getGroupList().stream().collect(Collectors.toMap(KnCorpGroupListInfoDTO::getGroupID, grp->grp));
                Map<Integer, KnCorpGroupListInfoDTO> allGroupMap = new ConcurrentHashMap<>();
                for (KnCorpGroupListInfoDTO knCorpGroupListInfoDTO : userProfile.getGroupList()) {
                    if (!allGroupMap.containsKey(knCorpGroupListInfoDTO.getGroupID())) {
                        allGroupMap.put(knCorpGroupListInfoDTO.getGroupID(), knCorpGroupListInfoDTO);
                    } else if (null == knCorpGroupListInfoDTO.getGroupChannel() || null == knCorpGroupListInfoDTO.getGroupZone() || 0 == knCorpGroupListInfoDTO.getGroupChannel() || 0 == knCorpGroupListInfoDTO.getGroupZone()) {
                        if (null == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupChannel() || null == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupZone() || 0 == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupZone() || 0 == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupChannel()) {
                            duplicateGroupDTOs.add(allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()));
                            allGroupMap.remove(knCorpGroupListInfoDTO.getGroupID());
                            allGroupMap.put(knCorpGroupListInfoDTO.getGroupID(), knCorpGroupListInfoDTO);
                        } else {
                            duplicateGroupDTOs.add(knCorpGroupListInfoDTO);
                        }
                    } else if (null != knCorpGroupListInfoDTO.getGroupChannel() || null != knCorpGroupListInfoDTO.getGroupZone() || 0 != knCorpGroupListInfoDTO.getGroupChannel() || 0 != knCorpGroupListInfoDTO.getGroupZone()) {
                        if (null == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupChannel() || null == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupZone() || 0 == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupZone() || 0 == allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()).getGroupChannel()) {
                            duplicateGroupDTOs.add(allGroupMap.get(knCorpGroupListInfoDTO.getGroupID()));
                            allGroupMap.remove(knCorpGroupListInfoDTO.getGroupID());
                            allGroupMap.put(knCorpGroupListInfoDTO.getGroupID(), knCorpGroupListInfoDTO);
                        } else {
                            duplicateGroupDTOs.add(knCorpGroupListInfoDTO);
                        }
                    }
                }
                // Map<Integer, KnCorpGroupListInfoDTO> allGroupMap = userProfile.getGroupList().stream().collect(Collectors.toMap(KnCorpGroupListInfoDTO::getGroupID, grp->grp));

                knLogger.debug(methodName, "Groups from profile - ", allGroupMap.keySet());
                ArrayList<String> upmId = new ArrayList<>();
                upmId.add(userProfileId);
                Map<String, Integer> upmOwnerList = userProfileUtil.getUserProfileOwnerinfo(upmId, xdmsHome, readOnly, persisterTxn);
                Integer ownerCorpId=corpProfile.getCorpId();
                if(!upmOwnerList.isEmpty()){
                    ownerCorpId=upmOwnerList.get(userProfileId);
                }
                knLogger.info(methodName, "ownerCorpId :", ownerCorpId);
                Map<Integer, Integer> ttGroups = groupInfoUtil.getValidGrpInCorp(new ArrayList<>(allGroupMap.keySet()),ownerCorpId , xdmsHome, readOnly, persisterTxn);
                Map<Integer,List<KnCorpSharedCorpInfo>> sharedGroupInfo = groupInfoUtil.selectGroupSharedCorpInfoBySharedCorpId(corpProfile.getCorpId(), xdmsHome, readOnly, persisterTxn);
                knLogger.debug(methodName, "--->TT groups  - ", ttGroups.keySet()," --> shared corp groups - ",sharedGroupInfo.keySet());
                if(sharedGroupInfo!=null && !sharedGroupInfo.isEmpty()){
                    allGroupMap.keySet().forEach(k->{
                        if(sharedGroupInfo.containsKey(k)){
                            ttGroups.put(k,k);
                        }

                    });
                }
               knLogger.debug(methodName, "--->TT groups after adding shared groups - ", ttGroups.keySet());

               knLogger.info(methodName,"isHierarchyFlow - ",isHierarchyFlow,"ttGrps -",ttGroups);
                if(isHierarchyFlow) {
                   Map<Integer, Integer> grpIdOwnerMap = groupInfoUtil.isValidGroupForIdList
                            (new ArrayList<>(allGroupMap.keySet()), new ArrayList<>(idListInt), idType, xdmsHome, readOnly, persisterTxn);
                    if (grpIdOwnerMap != null && !grpIdOwnerMap.isEmpty()) {
                        allGroupMap.keySet().forEach(k -> {
                            if (grpIdOwnerMap.containsKey(k)) {
                                ttGroups.put(k, k);
                            }
                        });
                    }
                    knLogger.debug(methodName, "ttGroups afert adding shared grps ->", ttGroups);
                }
                if(ttGroups.size() != allGroupMap.size()){
                    knLogger.info(methodName, "Size mismatch, filtering the groups, ttGroup size ", ttGroups.size(), " Profile size - ",allGroupMap.size());
                    userProfile.getGroupList().clear();
                    allGroupMap.forEach((k,v)->{
                        if(ttGroups.containsKey(k)){
                            userProfile.getGroupList().add(v);
                        }else{
                            cbsStaleGroupSet.add(v);
                        }
                    });
                }
                knLogger.debug(methodName, "Duplicate knCorpGroupListInfoDTOs", duplicateGroupDTOs);
                if(!duplicateGroupDTOs.isEmpty()){
                    for(KnCorpGroupListInfoDTO dto :duplicateGroupDTOs){
                        userProfile.getGroupList().remove(dto);
                    }
                }
            }
            if(userProfile.getTgscMode() == null){
                KnCorpResponseDTO corpResponseDTO = corpClientIntf.getProfileMdnByUPId(ipUserProfileDTO, readOnly, persisterTxn);
                List<String> mdnList = corpResponseDTO.getMdnList();
                if(mdnList != null && mdnList.size() != 0){
                    KnTalkGrpScanModeDTO dto = genInfoUtil.getSubsTalkGrpScanMode(mdnList.get(0), readOnly, persisterTxn);
                    if(dto != null && dto.getMode() != null){
                        int intTgscMode = dto.getMode();
                        userProfile.setTgscMode(String.valueOf(intTgscMode));
                    }
                }
            }
            boolean sharingEnabledFlag = userProfile.getSharingEnabled() != null ? userProfile.getSharingEnabled() : false;
            if (!sharedCorpIdListString.isEmpty()) {
                sharingEnabledFlag = true;
            }
            staleUserProfileElements.set_id(userProfileId);
            Integer cbsContactListId = userProfile.getContactListID();

            try {
                if (cbsContactListId != null) {
                    String ownerCorpid = sharingEnabledFlag ?
                            (userProfile.getCorporateID() != null ? String.valueOf(userProfile.getCorporateID()) : "0") :
                            corpId;
                    sublistInfoUtil.getSublistInfo(cbsContactListId, Integer.parseInt(ownerCorpid), xdmsHome, readOnly, persisterTxn);
                }
                Set<KnCorpUserProfileMCPTTConfig> mcpttPermConfig = userProfile.getMcpttPermissionsConfig();
                knLogger.debug(methodName, "mcpttPermConfig details to validate cbandttMdns ->", mcpttPermConfig);
                List<String> cbsMcpttMdn = mcpttPermConfig.stream().map(KnCorpUserProfileMCPTTConfig::getMdn).collect(Collectors.toList());

                KnCorpSublistRespDTO sublistDetails = sublistInfoUtil.getSublistDetails( ipUserProfileDTO.getOperationType(), cbsContactListId != null ? cbsContactListId : -1, Integer.parseInt(corpId),
                        corpProfile.getMaxContactsPerSubsc(), xdmsHome, readOnly, persisterTxn);
                List<String> ttContactMdn = sublistDetails.getMemberList().stream().map(KnCorpContactDTO::getMdn).collect(Collectors.toList());

                List<String> nonExistingMdnIntt = cbsMcpttMdn.stream()
                        .filter(element -> !ttContactMdn.contains(element))
                        .collect(Collectors.toList());
                List<String> nonExistingMdnIncb = ttContactMdn.stream()
                        .filter(element -> !cbsMcpttMdn.contains(element))
                        .collect(Collectors.toList());

                Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfig = new HashSet<>();
                Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfigforCB = new HashSet<>();
                //IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                for (String mdn : nonExistingMdnIntt) {
                    mcpttPermissionsConfig.add(new KnCorpUserProfileMCPTTConfig(mdn, 0));
                }
                for (String mdn : nonExistingMdnIncb) {
                        mcpttPermissionsConfigforCB.add(new KnCorpUserProfileMCPTTConfig(mdn, 0));
                }
                staleUserProfileElements.setMcpttPermissionsConfig(mcpttPermissionsConfig);

                staleUserProfileElements.setMcpttPermissionsConfigIncb(mcpttPermissionsConfigforCB);
                knLogger.info(methodName, "mcpttPermissionsConfigforCB details to validate mcpttPermissionsConfigforCB ->", mcpttPermissionsConfigforCB);
            } catch (KnCorpBOException ex) {
                knLogger.error(ex.getMessage());
                staleUserProfileElements.setContactListID(cbsContactListId);
                staleUserProfileElements.setMcpttPermissionsConfig(userProfile.getMcpttPermissionsConfig());
            }

            List<Integer> groupList = new ArrayList<>();
            List<KnCorpGroupListInfoDTO> groupDTO = List.copyOf(userProfile.getGroupList());
            for(KnCorpGroupListInfoDTO ids : groupDTO){
                groupList.add(ids.getGroupID());
            }
            Map<Integer, KnCorpGroupDTO> groupInfoMap = groupInfoUtil.getGroupBasicDetailsMap(groupList, xdmsHome, readOnly, persisterTxn);
            for(KnCorpGroupListInfoDTO groups : userProfile.getGroupList()){
                groups.setGroupName(groupInfoMap.get(groups.getGroupID()).getGroupDisplayName());
                groups.setGroupType(groupInfoMap.get(groups.getGroupID()).getGroupType());

            }
            knLogger.debug(methodName, "--->userProfile - ", userProfile);
            respDTO.setUserProfile(userProfile);
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                List<String> ownerIdList = userProfileUtil.getOwnerIDByUserProfileId(ipUserProfileDTO.getProfileId(), xdmsHome, readOnly, persisterTxn);
                respDTO.setOwnerIDList(ownerIdList);
            }
           /* Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String sysuserProfileSharingFlag=microServicesParamNameValueMap.get(USER_PROFILE_SHARING_FLAG);*/

            String sharingEnabled = "0";
            if(!sharedCorpIdListString.isEmpty())
            {
                sharingEnabled = "1";
                respDTO.setUserProfileSharedCorpList(sharedCorpIdListString);
            }
            respDTO.setSharingEnabled(sharingEnabled);
            respDTO.setOwnerCorpId(userProfile.getCorporateID() + "");
            Map<String, Integer> corpIdMap = commonInfoUtil.getCorpIdMap(null, Set.of(userProfile.getCorporateID()), readOnly, persisterTxn);
             for(Map.Entry<String, Integer> entry : corpIdMap.entrySet())
             {
                 if(entry.getValue() == userProfile.getCorporateID())
                     respDTO.setOwnerExtCorpId(entry.getKey());
             }
            staleUserProfileElements.setGroupList(cbsStaleGroupSet);
            knLogger.debug(methodName, " staleUserProfileElements - ", staleUserProfileElements);
            if(staleUserProfileElements.getContactListID()!=null||!staleUserProfileElements.getMcpttPermissionsConfigIncb().isEmpty()
                    ||!staleUserProfileElements.getMcpttPermissionsConfig().isEmpty()
                    ||!staleUserProfileElements.getGroupList().isEmpty()){
                String staleUpmJsonString = KnCorpCommonInfoUtil.ObjToJson(staleUserProfileElements);
                KnAsyncJobDTO JobReqDTO = new KnAsyncJobDTO();
                JobReqDTO.setTxnId(String.valueOf(System.nanoTime()));
                JobReqDTO.setCreationTime(String.valueOf(Instant.now().toEpochMilli()));
                JobReqDTO.setUserProfileId(userProfileId);
                JobReqDTO.setOpType(UPM_OPERATION_TYPE.STALE_UPM_ELEMENTS.Value());
                JobReqDTO.setOpStatus(com.kodiak.xdms.server.common.resources.KnConstants.UPM_JOB_STATUS.STALE.Value());
                JobReqDTO.setResourceEntity(null);
                JobReqDTO.setResourceType(UPM_RESOURCE_TYPE.MDN.Value());
                JobReqDTO.setCorpId(Integer.parseInt(corpId));
                JobReqDTO.setUpdationTime(String.valueOf(Instant.now().toEpochMilli()));
                JobReqDTO.setPayLoad(staleUpmJsonString);
                //delete the userProfile stale entry and insert to maintain single entry per user profile stale
                generalCacheUtil.deleteStaleAsyncJob(JobReqDTO);
                generalCacheUtil.createAsyncJob(JobReqDTO);
            }

            //self dnd
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String systemSelfDndFeature = microServicesParamNameValueMap.get(SELF_DND_FEATURE);
            boolean corpSelfDndPrivilege = KnGeneralUtil.getFeatureBitValue(corpProfile.getCorpFS2(), KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value());
            if (systemSelfDndFeature != null && systemSelfDndFeature.equals(ENABLED_STRING) && corpSelfDndPrivilege) {
                Map<Integer, Integer> selfDndBit = new HashMap<>();
                if (userProfile.getSelfDnDPrivilege() == null || userProfile.getSelfDnDPrivilege().equals(ENABLED_STRING)) {
                    selfDndBit.put(KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value(), ENABLE);
                } else if (userProfile.getSelfDnDPrivilege().equals(DISABLED_STRING)) {
                    selfDndBit.put(KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value(), DISABLED);
                }
                userProfile.setFeatureBS(KnGeneralUtil.updateFeatureBit(userProfile.getFeatureBS(), selfDndBit));
            }

            String sysEmergencyConfigTimerFeature = microServicesParamNameValueMap.get(EMERGENCY_CONF_TIMER_FEATURE) != null ? microServicesParamNameValueMap.get(EMERGENCY_CONF_TIMER_FEATURE) :
                    DISABLED_STRING;
            boolean corpEmergencyConfigTimerFeature = corpProfile.getXdmCorpFS2Set() != null &&
                    KnGeneralUtil.getFeatureBitValue(corpProfile.getXdmCorpFS2Set(), XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value());

            knLogger.debug(methodName," sysEmergencyConfigTimerFeature - ",sysEmergencyConfigTimerFeature," corpEmergencyConfigTimerFeature - ",corpEmergencyConfigTimerFeature);
            boolean isEmergencyFeatureEnabled = Integer.parseInt(sysEmergencyConfigTimerFeature) == ENABLE && corpEmergencyConfigTimerFeature;

            if (isEmergencyFeatureEnabled) {
                if(userProfile.getEmergencyConfig() !=null & isNullOrEmpty(userProfile.getEmergencyConfig().getEmergConfigTimer())) {
                    userProfile.getEmergencyConfig().setEmergConfigTimer(microServicesParamNameValueMap.get(DEFAULT_EMERGENCY_TIMER));
                }
            }else {
                if(userProfile.getEmergencyConfig() !=null){
                    userProfile.getEmergencyConfig().setEmergConfigTimer(null);
                }
            }

            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    public KnCorpUserProfileListRespDTO getUserProfileList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getUserProfileList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

      //  KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfileListRespDTO respDTO = new KnCorpUserProfileListRespDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        final KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "--->ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipUserProfileDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_USER_PROFILE_LIST);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            //settting operation and entity id
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            //user profile exits
            /*KnCorpUserProfileDTO userProfile = userProfileUtil.getUserProfile(ipUserProfileDTO.getCorpId(), ipUserProfileDTO.getProfileId(), xdmsHome, persisterTxn);
            userProfilePersistDTO.setUserProfileDTO(userProfile);
            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.debug(methodName, "Validation Successfull");*/
          //  KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(ipUserProfileDTO.getCorpId(),ipUserProfileDTO.getProfileId(),xdmsHome,persisterTxn);
            userProfilePersistDTO.setStartIndex(ipUserProfileDTO.getStartIndex());
            userProfilePersistDTO.setFetchSize(ipUserProfileDTO.getFetchSize());
            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
           // validatorFW.validate(userProfilePersistDTO);
            knLogger.debug(methodName, "Validation Successfull");

            Collection<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
            List<String> userProfileIds = new ArrayList<>();
            int userProfileIdsCount = 0;
            String hierarchyId = ipUserProfileDTO.getHierarchyId();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                int idType = Integer.parseInt((String) customParams.get(IDTYPE));
                if (idType == 1) {
                    knLogger.warn(methodName,"UPM not allowed for BAN");
                    throw new KnCorpBOValidationException(NON_FIRSTNET_FAN,
                            "Invalid owner context ids", "", "", "", "DataType", "");
                }
                List<String> ID_VALUE = (List<String>) ipUserProfileDTO.getCustomParamMap().get(IDLIST);
                userProfileIds = userProfileUtil.userProfileIdsHiearchyMap(persisterTxn, xdmsHome, ID_VALUE, true);
                //get userProfileIds based on the startindex and fetchsize from DG.USERPROFILE_HIERARCHY_MAP.
                //userProfileIds = userProfileUtil.userProfileIdsHiearchyMapFetchSize(persisterTxn, ipUserProfileDTO.getFetchSize(), ipUserProfileDTO.getStartIndex(), xdmsHome, ID_VALUE);
                //Passing userProfileIds and fetching userProfilelist from CB
                userProfileList = userProfileUtil.getUserProfile(userProfileIds, ipUserProfileDTO.getFetchSize(), ipUserProfileDTO.getStartIndex(), xdmsHome, persisterTxn);
                userProfileIdsCount = userProfileIds.size();
            } else {
                //product flow
                if (hierarchyId != null) {
                    userProfileList = userProfileUtil.getUserProfileListByCorpAndHierarchyId(
                            ipUserProfileDTO.getCorpId(), hierarchyId,
                            ipUserProfileDTO.getFetchSize(), ipUserProfileDTO.getStartIndex(),
                            xdmsHome, persisterTxn);
                } else {
                    userProfileList = userProfileUtil.getUserProfileList(ipUserProfileDTO.getCorpId(), ipUserProfileDTO.getFetchSize(), ipUserProfileDTO.getStartIndex(), xdmsHome, persisterTxn);
                }
            }
            int maxTotalCount;
            if (hierarchyId != null) {
                maxTotalCount = userProfileUtil.getMaxTotalCountByCorpAndHierarchyId(
                        ipUserProfileDTO.getCorpId(), hierarchyId, xdmsHome, persisterTxn);
            } else {
                maxTotalCount = userProfileUtil.getMaxTotalCountByCorpId(ipUserProfileDTO.getCorpId(), xdmsHome, persisterTxn);
            }
            knLogger.debug(methodName, "--->userProfileList - ", userProfileList);
            //Get User Profile Ids from XDM Table USERPROFILE_SHAREDLIST based on SHAREDCORPID
            List<String> userProfileIdsFromSharedCorpId = userProfileUtil.getXdmUserProfileIdsBySharedCorpId(ipUserProfileDTO.getCorpId(), xdmsHome, true, persisterTxn);

            //Fetch the corresponded user profile details from couchbase based on the user profile Id List
            List<KnCorpUserProfileDTO> userProfileListFromShareCorpId = userProfileUtil.getUserProfile(userProfileIdsFromSharedCorpId, xdmsHome, persisterTxn);
            if (userProfileListFromShareCorpId != null && !userProfileListFromShareCorpId.isEmpty()) {
                userProfileList.addAll(userProfileListFromShareCorpId);
            }
            knLogger.debug(methodName, "All userProfileList : ",userProfileList);
            //User profile sharing changes
            Map<String,Integer> userProfileOwnerInfoMap= new HashMap<String,Integer>();
            ArrayList<String> profileIds = new ArrayList<String>();
            if (userProfileListFromShareCorpId != null && !userProfileListFromShareCorpId.isEmpty()) {
                for(KnCorpUserProfileDTO profile :  userProfileListFromShareCorpId){
                    profileIds.add(profile.get_id());
                }
                maxTotalCount = maxTotalCount + userProfileListFromShareCorpId.size();
            }

            if (profileIds != null && !profileIds.isEmpty()) {
                userProfileOwnerInfoMap = userProfileUtil.getUserProfileOwnerinfo(profileIds, xdmsHome, true, persisterTxn);
                knLogger.debug("owners added : ",userProfileOwnerInfoMap);
            }

            Set<Integer> corpIds = new HashSet<>();
            corpIds.add(Integer.parseInt(ipUserProfileDTO.getCorpId()));
            //dummy code ends
            userProfileIds.clear();
            for(KnCorpUserProfileDTO profile : userProfileList){
                //update the userProfileIds as per pagination
                userProfileIds.add(profile.get_id());

                if(profile.getCorporateID() == Integer.parseInt(ipUserProfileDTO.getCorpId()))
                    continue;
                profile.setSharingEnabled(Boolean.FALSE);
                if(userProfileOwnerInfoMap.containsKey(profile.get_id())){
                         profile.setSharingEnabled(Boolean.TRUE);
                }
                //collect the corpId
                corpIds.add(profile.getCorporateID());
            }
            Map<String, Integer> corpIdMap = commonInfoUtil.getCorpIdMap(null, corpIds, true, persisterTxn);
            Map<Integer, String> corpIntIdVsExtCoprId = new HashMap<>();
            if(corpIdMap != null && ! corpIdMap.isEmpty())
            {
                for(Map.Entry<String, Integer> entry :corpIdMap.entrySet())
                    corpIntIdVsExtCoprId.put(entry.getValue(), entry.getKey());
            }
            respDTO.setCorpIdMap(corpIntIdVsExtCoprId);
            //Integer maxTotalCount = (respDTO.getUserProfileList() != null) ? respDTO.getUserProfileList().size() : 0;
            //Integer maxTotalCount = userProfileUtil.getMaxTotalCountByCorpId(ipUserProfileDTO.getCorpId(),xdmsHome,persisterTxn);
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                maxTotalCount = 0;
                Map<String,List<String>> ownerList = userProfileUtil.getUserProfileOwnerList(userProfileIds, xdmsHome, true, persisterTxn);
                List<KnCorpUserProfileDTO> profileDTOs = new ArrayList<KnCorpUserProfileDTO>();

                for(KnCorpUserProfileDTO userProfileId:userProfileList) {
                    if(!userProfileIds.contains(userProfileId.get_id())) {
                        profileDTOs.add(userProfileId);
                    }
                    if(userProfileIds.contains(userProfileId.get_id())) {
                        List<String> ownerid = ownerList.get(userProfileId.get_id());
                        userProfileId.setOwnerIdList(ownerid);
                    }
                }
                userProfileList.removeAll(profileDTOs);

                //maxTotalCount = new HashSet<>(userProfileIdsCount).size();
                maxTotalCount = userProfileIdsCount;
            }
            knLogger.info(methodName, "--->maxTotalCount - ", maxTotalCount);
            respDTO.setMaxtotalcount(maxTotalCount);
            respDTO.setUserProfileList(userProfileList);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        } /*catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        }*/ catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpUserProfileListRespDTO getUserProfileListByName(KnIPUserProfileDTO ipUserProfileDTO,
                                                                 KnPersisterTxn persisterTxn) {
        String methodName = "getUserProfileListByName(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

      //  KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfileListRespDTO respDTO = new KnCorpUserProfileListRespDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        final KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "--->ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipUserProfileDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_USER_PROFILE_LIST_BY_NAME);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            Collection<KnCorpUserProfileDTO> userProfileList = userProfileUtil.getUserProfileListByUserProfileNamePattern(ipUserProfileDTO.getCorpId(), ipUserProfileDTO.getUserProfileName(), ipUserProfileDTO.getFetchSize(), ipUserProfileDTO.getStartIndex(), ipUserProfileDTO.getIsCaseSensitiveSearch(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "--->userProfileList - ", userProfileList);
            if(userProfileList == null)
            {
                userProfileList =  new ArrayList<>();
            }
            //Get list of UserProfile Ids from USERPROFILE_SHAREDLIST based on SHAREDCORPID
            List<String> userProfileIdsFromSharedCorpId = userProfileUtil.getXdmUserProfileIdsBySharedCorpId(ipUserProfileDTO.getCorpId(), xdmsHome, true, persisterTxn);

            //Fetching list of Userprofile from couchbase based on the user profile Id List
            List<KnCorpUserProfileDTO> userProfileListShared = userProfileUtil.getUserProfileListByUserProfileNamePatternSharedCorp(userProfileIdsFromSharedCorpId, ipUserProfileDTO.getUserProfileName(), ipUserProfileDTO.getFetchSize(), ipUserProfileDTO.getStartIndex(), ipUserProfileDTO.getIsCaseSensitiveSearch(), xdmsHome, persisterTxn);
            if (userProfileListShared != null && !userProfileListShared.isEmpty()) {
                userProfileList.addAll(userProfileListShared);
            }
            //User profile sharing changes
            Map<String,Integer> userProfileOwnerInfoMap= new HashMap<String,Integer>();
            ArrayList<String> profileIds = new ArrayList<String>();
            if (userProfileListShared != null && !userProfileListShared.isEmpty()) {
                for(KnCorpUserProfileDTO profile :  userProfileListShared){
                    profileIds.add(profile.get_id());
                }
            }
            userProfileOwnerInfoMap = userProfileUtil.getUserProfileOwnerinfo(profileIds, xdmsHome, true, persisterTxn);
            knLogger.debug("owners added : ",userProfileOwnerInfoMap);

            Set<Integer> corpIds = new HashSet<>();
            corpIds.add(Integer.parseInt(ipUserProfileDTO.getCorpId()));

            for(KnCorpUserProfileDTO profile : userProfileList){
                if(profile.getCorporateID() == Integer.parseInt(ipUserProfileDTO.getCorpId()))
                    continue;
                profile.setSharingEnabled(Boolean.FALSE);
                if(userProfileOwnerInfoMap.containsKey(profile.get_id())){
                    profile.setSharingEnabled(Boolean.TRUE);
                }
                //collect the corpId
                corpIds.add(profile.getCorporateID());
            }
            Map<String, Integer> corpIdMap = commonInfoUtil.getCorpIdMap(null, corpIds, true, persisterTxn);
            Map<Integer, String> corpIntIdVsExtCoprId = new HashMap<>();
            if(corpIdMap != null && ! corpIdMap.isEmpty())
            {
                for(Map.Entry<String, Integer> entry :corpIdMap.entrySet())
                    corpIntIdVsExtCoprId.put(entry.getValue(), entry.getKey());
            }
            respDTO.setCorpIdMap(corpIntIdVsExtCoprId);
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                //1.All userProfileId of that fan/ban including shared ban/fan from TT db
                List<String> ID_VALUE = (List<String>) ipUserProfileDTO.getCustomParamMap().get(IDLIST);
                int idType = Integer.parseInt((String) customParams.get(IDTYPE));
                if (idType == 1) {
                    knLogger.warn(methodName,"UPM not allowed for BAN");
                    throw new KnCorpBOValidationException(NON_FIRSTNET_FAN,
                            "Invalid owner context ids", "", "", "", "DataType", "");
                }
                knLogger.info(methodName, "ID_VALUE : ", ID_VALUE);
                List<String> allUserProfileIdsForBanFan = userProfileUtil.userProfileIdsHiearchyMap(persisterTxn, xdmsHome, ID_VALUE, true);
                knLogger.debug(methodName, "allUserProfileIdsForBanFan : ", allUserProfileIdsForBanFan);
                knLogger.info(methodName, "allUserProfileIdsForBanFan.size() : ", allUserProfileIdsForBanFan.size());
                if (allUserProfileIdsForBanFan.size() > VERY_LARGE_UPM) {
                    //2.get AllUserProfiledata from couchbase based on filter like name and userProfileId(from above result 1).. this will provide maxTotal
                    List<List<String>> userProfileSplitLists = KnGeneralUtil.splitList(allUserProfileIdsForBanFan, VERY_LARGE_UPM);
                    List<KnCorpUserProfileDTO> totalUserProfileList = new ArrayList<>();
                    for (List<String> userProfileSplitList : userProfileSplitLists) {
                        Collection<KnCorpUserProfileDTO> userProfiles = userProfileUtil.getUserProfileListForNamePattern(ipUserProfileDTO.getCorpId(), ipUserProfileDTO.getUserProfileName(), ipUserProfileDTO.getIsCaseSensitiveSearch(), userProfileSplitList, xdmsHome, persisterTxn);
                        totalUserProfileList.addAll(userProfiles);
                    }
                    knLogger.debug(methodName, "totalUserProfileList : ", totalUserProfileList);
                    knLogger.info(methodName, "totalUserProfileList.size() : ", totalUserProfileList.size());
                    //3.get Profiledata based on fetch size based on filter like name and userProfileId(from above totalUserProfileList).. this will provide data for dispaly
                    List<KnCorpUserProfileDTO> userProfileListWithFetchSize = new ArrayList<>();
                    int startIndex =  Integer.parseInt(ipUserProfileDTO.getStartIndex());
                    if (startIndex < totalUserProfileList.size()) {
                        int endIndex = Math.min(startIndex + Integer.parseInt(ipUserProfileDTO.getFetchSize()), totalUserProfileList.size());
                        userProfileListWithFetchSize = totalUserProfileList.subList(startIndex, endIndex);
                        knLogger.info(methodName, "userProfileListWithFetchSize.size() : ", userProfileListWithFetchSize.size());
                    } else {
                        knLogger.info(methodName, "Offset value is out of range.");
                    }
                    List<String> userProfileIds = userProfileListWithFetchSize.stream().map(KnCorpUserProfileDTO::get_id).collect(Collectors.toList());
                    Map<String, List<String>> ownerList = userProfileUtil.getUserProfileOwnerList(userProfileIds, xdmsHome, true, persisterTxn);
                    userProfileListWithFetchSize.forEach(userProfileDto -> userProfileDto.setOwnerIdList(ownerList.get(userProfileDto.get_id())));
                    respDTO.setUserProfileList(userProfileListWithFetchSize);
                    respDTO.setMaxtotalcount(totalUserProfileList.size());
                } else {
                    //2.maxTotalCount from couchbase based on filter like name and userProfileId(from above result 1)
                    Integer maxTotalCount = userProfileUtil.getUserProfileCountByUserProfileNamePatternAndUserprofileIds(ipUserProfileDTO.getCorpId(), ipUserProfileDTO.getUserProfileName(), ipUserProfileDTO.getIsCaseSensitiveSearch(), allUserProfileIdsForBanFan, xdmsHome, persisterTxn);
                    knLogger.info(methodName, "maxTotalCount : ", maxTotalCount);
                    //3.get Profiledata based on fetch size based on filter like name and userProfileId(from above uery).. this will provide data for dispaly
                    Collection<KnCorpUserProfileDTO> userProfileListWithFetchSize = userProfileUtil.getUserProfileListForNamePattern(ipUserProfileDTO.getCorpId(), ipUserProfileDTO.getUserProfileName(), ipUserProfileDTO.getFetchSize(), ipUserProfileDTO.getStartIndex(), ipUserProfileDTO.getIsCaseSensitiveSearch(), allUserProfileIdsForBanFan, xdmsHome, persisterTxn);
                    knLogger.info(methodName, "userProfileListWithFetchSize : ", userProfileListWithFetchSize);
                    List<String> userProfileIds = userProfileListWithFetchSize.stream().map(KnCorpUserProfileDTO::get_id).collect(Collectors.toList());
                    Map<String,List<String>> ownerList = userProfileUtil.getUserProfileOwnerList(userProfileIds, xdmsHome, true, persisterTxn);
                    userProfileListWithFetchSize.forEach(userProfileDto-> userProfileDto.setOwnerIdList(ownerList.get(userProfileDto.get_id())));
                    respDTO.setUserProfileList(userProfileListWithFetchSize);
                    respDTO.setMaxtotalcount(maxTotalCount);
                }
            } else {
                respDTO.setUserProfileList(userProfileList);
                Integer maxTotalCount = userProfileUtil.getMaxTotalCountByCorpIdAndProfileNamePattern(ipUserProfileDTO.getCorpId(), ipUserProfileDTO.getUserProfileName(), xdmsHome, persisterTxn);
                respDTO.setMaxtotalcount(maxTotalCount);
            }
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }/* catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        }
         */catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpUserProfileListRespDTO getSubscriberUserProfileList(KnIPUserProfileDTO ipUserProfileDTO,
                                                                     KnPersisterTxn persisterTxn) {
        String methodName = "getSubscriberUserProfileList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

      //  KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfileListRespDTO respDTO = new KnCorpUserProfileListRespDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);

        knLogger.debug(methodName, "--->ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipUserProfileDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_SUBSCRIBER_USER_PROFILE_LIST);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(ipUserProfileDTO.getMdn(), PUBLIC_PROFILE,
                    false, persisterTxn);
            Map<String,Integer> subscriberInfoMap = userProfileUtil.getSubscriberUserProfileList(subscProfile.getMcId(), xdmsHome, true, persisterTxn);
            Collection<KnSubscriberUserProfileDTO> subscriberUserProfileList = new ArrayList<>();
            if (!subscriberInfoMap.isEmpty()) {
                List<String> userProfileIds = new ArrayList<>();
                userProfileIds.addAll(subscriberInfoMap.keySet());
                userProfileIds.removeIf(Objects::isNull);
                subscriberUserProfileList = userProfileUtil.getUserProfileListByProfileIds(userProfileIds, xdmsHome, persisterTxn);
            }

            for (KnSubscriberUserProfileDTO knCorpUserProfileDTO : subscriberUserProfileList) {
                if (subscriberInfoMap.get(knCorpUserProfileDTO.get_id()) != null) {
                    knCorpUserProfileDTO.setIsDefaultProfile(
                            subscriberInfoMap.get(knCorpUserProfileDTO.get_id()));
                }
            }
            knLogger.debug(methodName, "--->userProfileList - ", subscriberUserProfileList);
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                List<String> ID_VALUE = (List<String>) ipUserProfileDTO.getCustomParamMap().get("IDLIST");
                    List<String> userProfileIds = userProfileUtil.userProfileIdsHiearchyMap(persisterTxn,xdmsHome,ID_VALUE, true);
                Map<String,List<String>> ownerList = userProfileUtil.getUserProfileOwnerList(userProfileIds, xdmsHome, true, persisterTxn);
                List<KnCorpUserProfileDTO> profileDTOs = new ArrayList<KnCorpUserProfileDTO>();

                for(KnSubscriberUserProfileDTO userProfileId:subscriberUserProfileList) {
                    if(!userProfileIds.contains(userProfileId.get_id())) {
                        profileDTOs.add(userProfileId);
                    }
                    if(userProfileIds.contains(userProfileId.get_id())) {
                        List<String> ownerid = ownerList.get(userProfileId.get_id());
                        knLogger.debug(methodName, "ownerid :", ownerid);
                        userProfileId.setOwnerIdList(ownerid);
                    }
                }
                subscriberUserProfileList.removeAll(profileDTOs);
            }
            respDTO.setSubscriberUserProfileList(subscriberUserProfileList);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }/* catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        }
         */catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO assignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        final String methodName="assignUserProfile()";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipUserProfileDTO.getCorpId()),
                    CORP_PROFILE, false, null);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.ASSIGN_USER_PROFILE);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            //settting operation and entity id
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            String baseMdn=ipUserProfileDTO.getMdn();
            String profileId=ipUserProfileDTO.getProfileId();
            String corpId = ipUserProfileDTO.getCorpId();

            //max allowed userProfile per subscriber
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, null);
            final String sysMaxUserProfilePerSubs = microServicesParamNameValueMap.get(MAX_USERPROFILES_PERSUB );
            final String corpMaxUserProfilePerSub = corpProfile.getMaxAssignProfiles() == null ? null
                    : String.valueOf(corpProfile.getMaxAssignProfiles());
            String configuredMaxUPPerSubs="0";
            if(corpMaxUserProfilePerSub ==null){
                configuredMaxUPPerSubs=sysMaxUserProfilePerSubs;
            }else if(corpMaxUserProfilePerSub!=null&&sysMaxUserProfilePerSubs!=null){
                configuredMaxUPPerSubs=corpMaxUserProfilePerSub;
            }
            knLogger.debug(methodName,"sysMaxUserProfilePerSubs ",sysMaxUserProfilePerSubs
                    ,"corpMaxUserProfilePerSub ",corpMaxUserProfilePerSub
                    ,"configuredMaxUPPerSubs ",configuredMaxUPPerSubs);

            List<String> profileMdnList=corpSubsProvInfoUtil.getProfileMdnByBaseMdn(baseMdn,xdmsHome,null);

            userProfilePersistDTO.setMaxUserProfilePerSub(configuredMaxUPPerSubs);
            userProfilePersistDTO.setDbProfileMdnList(profileMdnList);

            //SUBSCRIBERFS2 76	UserProfileMgmt bit is enabled
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(baseMdn, PUBLIC_PROFILE,
                    false, null);
            String subsFs2 = subscProfile.getSubscriberFS2();
            boolean upmBit = KnGeneralUtil.getFeatureBitValue(subsFs2,USER_PROFILE_MGMT_BIT);
            knLogger.debug("upmBit ",upmBit,"subsFs2 ",subsFs2);
            userProfilePersistDTO.setUserProfileMgmtBit(upmBit);
            userProfilePersistDTO.setClientType(subscProfile.getClientType());

           // boolean userProfileMgmt = KnGeneralUtil.getFeatureBitValue(subsFs2, USER_PROFILE_MGMT_BIT);
           // boolean veryLargeGroup = KnGeneralUtil.getFeatureBitValue(subsFs2, VERY_LARGE_GROUP);
           // userProfilePersistDTO.setMcpttCompliance(userProfileMgmt == veryLargeGroup);
            boolean veryLargeGroupBit = KnGeneralUtil.getFeatureBitValue(subsFs2, VERY_LARGE_GROUP);
            userProfilePersistDTO.setMcpttCompliance(veryLargeGroupBit);

            List<Integer> asyncJobStatusList = generalCacheUtil.getAsyncJobstatusList(ipUserProfileDTO.getProfileId(), ipUserProfileDTO.getMdn());
            if (asyncJobStatusList.contains(KnConstants.UPM_JOB_NEW_STATUS) || asyncJobStatusList.contains(KnConstants.UPM_JOB_INPROGRESS_STATUS)) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UP_ASYNC_JOB_EXISTS,
                        "Async job is already present for the userProfileId and mdn", CORP_USER_PROFILE_MANAGER, ASSIGN_USER_PROFILE, ipUserProfileDTO.getMdn(),
                        "", "");
            }
            String profileMdn = userProfileUtil.getProfileMdnByMdnUPID(ipUserProfileDTO.getProfileId(),ipUserProfileDTO.getMdn(), xdmsHome, null);
            if (profileMdn != null) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_IS_ALREADY_ASSIGNED,
                        "User Profile is already assigned", CORP_USER_PROFILE_MANAGER, ASSIGN_USER_PROFILE, ipUserProfileDTO.getMdn(),
                        profileMdn, "");
            }

            //request upId exists in corp.
            //fetching profile only based on the profile id
            KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(profileId,xdmsHome,null);
            knLogger.debug("userProfile ",userProfile);

            userProfilePersistDTO.setUserProfileDTO(userProfile);
            userProfilePersistDTO.setUserProfileId(profileId);
            int mcxGroupCount = 0;
            List<Integer> mcxGroupIdList = new ArrayList<>();
            List<Integer> largeGroupIdsExceeded=new ArrayList<>();
            if (userProfile.getGroupList()!= null && userProfile.getGroupList().size() > 0) {
                List<Integer> groupIdList = userProfile.getGroupList().stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList());
                List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIdList, xdmsHome, null);
                //Map<Integer, Integer> membersCount = groupInfoUtil.getGroupMemsCounts(groupIdList, xdmsHome , null);
                boolean maxMembersAllowedPerGroupIsReached = false;
                int groupType = 0;
                int maxMembersAllowedPerGroup = 0;
                //large group validation
                List<Integer> largeGroupIds = new ArrayList<>();
                List<Integer> largeBCGroupIds = new ArrayList<>();
                int corpMaxLarge = corpProfile.getMaxLrgGrpPerCorp();
                int corpMaxLargeBCGroup = corpProfile.getMaxLrgBGrpPerCorp();
                int corpLargeGroupCount = groupInfoUtil.getLargeGroupCounts(corpProfile.getCorpId(), null, xdmsHome).getLrgGrpCountCorp();
                int corpLargeBCGroupCount = groupInfoUtil.getLargeGroupCounts(corpProfile.getCorpId(), null, xdmsHome).getLargeBCGrpCountCorp();
                Map<Integer, Integer> memberCount = groupInfoUtil.getGroupMemCount(groupIdList, xdmsHome, null);
                for (KnCorpGroupDTO groupInfo : groupInfoList) {
                    //present groupMember +1(assign group mdn)
                    int exisitngGroupMembersCount = memberCount.getOrDefault(groupInfo.getGroupId(), 0);
                    int groupMemberCount = exisitngGroupMembersCount + 1;

                    if (exisitngGroupMembersCount == 250) {
                        List<String> groupMembersList = sublistInfoUtil.selectGroupMemberList(groupInfo.getGroupId(), xdmsHome, persisterTxn);
                        //fetch mcids of the group members
                        if (groupMembersList != null && groupMembersList.contains(ipUserProfileDTO.getMdn())) {
                            groupMemberCount = exisitngGroupMembersCount;
                        } else {
                            Collection<String> singleMdn = new ArrayList<>();
                            singleMdn.add(ipUserProfileDTO.getMdn());
                            Collection<String> uniqueMcIds = corpSubsProvInfoUtil.getMcidsByMdnList(groupMembersList, xdmsHome, persisterTxn, true);
                            Collection<String> mcid = corpSubsProvInfoUtil.getMcidsByMdnList(singleMdn, xdmsHome, persisterTxn, true);
                            if (uniqueMcIds != null && uniqueMcIds.contains(mcid)) {
                                groupMemberCount = exisitngGroupMembersCount;
                            }
                        }
                    }

                    if (com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP == groupInfo.getGroupType()
                            && groupMemberCount > corpProfile.getMaxMemPerCorpGroup() && !groupInfo.isLargeGroup()) {
                        largeGroupIds.add(groupInfo.getGroupId());
                    } else if (com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DISPATCH_GROUP == groupInfo.getGroupType()
                            && groupMemberCount > corpProfile.getMaxMembersPerDispatchGroup() && !groupInfo.isLargeGroup()) {
                        largeGroupIds.add(groupInfo.getGroupId());
                    } else if (com.kodiak.xdms.server.corpmgmt.resources.KnConstants.BROADCAST_GROUP == groupInfo.getGroupType()
                            && groupMemberCount > corpProfile.getMaxMemPerBCGrp() && !groupInfo.isLargeGroup()) {
                        largeBCGroupIds.add(groupInfo.getGroupId());
                    }

                    if (groupInfo.getGroupType() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP) {
                        if (groupInfo.isLargeGroup() || corpProfile.getLargeGrpSupported() == 1) {
                            maxMembersAllowedPerGroup = corpProfile.getMaxMemPerLrgGrp();
                        } else {
                            maxMembersAllowedPerGroup = corpProfile.getMaxMemPerCorpGroup();
                        }
                    } else if (groupInfo.getGroupType() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DISPATCH_GROUP) {
                        if (groupInfo.isLargeGroup() || corpProfile.getLargeGrpSupported() == 1) {
                            maxMembersAllowedPerGroup = corpProfile.getMaxMemPerLrgGrp();
                        } else {
                            maxMembersAllowedPerGroup = corpProfile.getMaxMembersPerDispatchGroup();
                        }
                    } else if (groupInfo.getGroupType() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.BROADCAST_GROUP) {
                        if (groupInfo.isLargeGroup() || corpProfile.getLargeGrpSupported() == 1) {
                            maxMembersAllowedPerGroup = corpProfile.getMaxMemPerLrgBGrp();
                        } else {
                            maxMembersAllowedPerGroup = corpProfile.getMaxMemPerBCGrp();
                        }
                    }
                    if (groupMemberCount > maxMembersAllowedPerGroup) {
                        maxMembersAllowedPerGroupIsReached = true;
                        groupType = groupInfo.getGroupType();
                    }
                }
                userProfilePersistDTO.setMaxMembersAllowedPerGroupIsReached(maxMembersAllowedPerGroupIsReached);
                userProfilePersistDTO.setGroupType(groupType);
                knLogger.debug(methodName," largeGroupIds :",largeGroupIds," largeBCGroupIds:",largeBCGroupIds);
                int totalLargeCount = corpLargeGroupCount + largeGroupIds.size();
                int totalLargeBCCount=corpLargeBCGroupCount+largeBCGroupIds.size();
                if(totalLargeCount>corpMaxLarge||totalLargeBCCount>corpMaxLargeBCGroup){
                    largeGroupIdsExceeded.addAll(largeGroupIds);
                    largeGroupIdsExceeded.addAll(largeBCGroupIds);
                }

                Long grpCount = groupInfoList.stream().filter(groupDetails -> groupDetails.getMcxGrpInd().intValue() == VLARGE_GROUP.value()).count();
                mcxGroupCount=grpCount.intValue();

                List<Integer> collectedMcxIds = groupInfoList.stream()
                        .filter(g -> g.getMcxGrpInd() != null && g.getMcxGrpInd().intValue() == VLARGE_GROUP.value())
                        .map(KnCorpGroupDTO::getGroupId)
                        .collect(Collectors.toList());
                mcxGroupIdList.addAll(collectedMcxIds);
            }
            userProfilePersistDTO.setMcxGroupCount(mcxGroupCount);
            //upm shared with the mdn corpId
            String systemUPMShareFlag = microServicesParamNameValueMap.get("USER_PROFILE_SHARING_FLAG");
            int upmShareFlag = corpProfile.getUserProfileSharingFeature() == null ? Integer.parseInt(systemUPMShareFlag) : corpProfile.getUserProfileSharingFeature();
            boolean upmShareEnabled=(upmShareFlag==ENABLE);
            userProfilePersistDTO.setUpmShareEnabled(upmShareEnabled);
            List<Integer> sharedCorpListId = userProfileUtil.getSharedCorpIdFromUserProfielId(ipUserProfileDTO.getProfileId(), xdmsHome, null);
            boolean sharedUpmForMdnCorp = sharedCorpListId.contains(subscProfile.getCorpId());
            userProfilePersistDTO.setSharedUpmForMdnCorp(sharedUpmForMdnCorp);

            //Shared User Profile is not applicable to Corporate only subscribers in the shared agency
            int pubSubsType = subscProfile.getPublicSubscriptionType();
            int corpSubsType = subscProfile.getCorpSubscriptionType();
            boolean onlyCorpSubs = corpSubsType == ENABLE && pubSubsType == DISABLE;
            boolean sharedUpm = !userProfile.getCorporateID().equals(subscProfile.getCorpId());
            userProfilePersistDTO.setOnlyCorpSubs(onlyCorpSubs);
            userProfilePersistDTO.setSharedUpm(sharedUpm);

            //Assign upm is allowed for self corp
            Integer upmCorpId = userProfile.getCorporateID();
            int mdnCorpId = subscProfile.getCorpId();
            boolean upmShared = (upmCorpId != mdnCorpId);
            int requestCorpId = Integer.parseInt(corpId);
            boolean assignUpmAllowed = (upmShared && (mdnCorpId!=requestCorpId));
            userProfilePersistDTO.setAssignUPMAllowed(assignUpmAllowed);

            userProfilePersistDTO.setLargeGroupIds(largeGroupIdsExceeded);

            //system level large group
            if(largeGroupIdsExceeded.size()>0){
                int LrgGrpCountSystem = groupInfoUtil.getLargeGroupCounts(corpProfile.getCorpId(), null, xdmsHome).getLrgGrpCountSystem();
                int MaxLargeGrpSystem = Integer.parseInt(genInfoUtil.retrieveRTXConfigValues(null).get(NUM_OF_LG_SUPPORTED));
                if(LrgGrpCountSystem>=MaxLargeGrpSystem){
                    userProfilePersistDTO.setSystemLevelLargeGroupLimitReached(true);
                }
            }

            //max loc per group
            int MaxLocWachersPerGroup = Integer.parseInt(genInfoUtil.retrieveRTXConfigValues(null).get(MAX_LOC_WATCHERS_PER_GRP));
            //KnCorpResponseDTO userProfileDetails = corpClientIntf.getUserProfileDetails(ipUserProfileDTO, false, persisterTxn);
            Set<KnCorpGroupListInfoDTO> groupList = userProfile.getGroupList();
            AtomicBoolean isStandardGroup = new AtomicBoolean(false);
            boolean MaxLocWachersPerGroupReached = false;
            Collection<Integer> gropIdList = new ArrayList<>();
            Collection<Integer> StandarGropIdList = new ArrayList<>();
            Map<Integer, Integer> lockWathercount;
            if (null != groupList && !groupList.isEmpty()) {
                for (KnCorpGroupListInfoDTO group : groupList) {
                    com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupContactDTO groupContact = group.getGrpMemProps();
                    int isLocSupervisor = groupContact.getIsLocSupervisor();
                    if (isLocSupervisor == 1) {
                        gropIdList.add(group.getGroupID());
                    }
                }
                lockWathercount = groupInfoUtil.getMaxLocWatchersCount(gropIdList, xdmsHome, null);
                List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(gropIdList, xdmsHome, null);
                groupInfoList.forEach(group -> {
                    if (group.getGroupType() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP) {
                        StandarGropIdList.add(group.getGroupId());
                    }
                });
                for (int groupId : StandarGropIdList) {
                    Integer mem = lockWathercount.get(groupId);
                    if (mem != null && (mem + 1) > MaxLocWachersPerGroup) {
                        MaxLocWachersPerGroupReached = true;
                    }
                }
            }
            userProfilePersistDTO.setMaxLocWachersPerGroupReached(MaxLocWachersPerGroupReached);
            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            // MCX group pochome lazy assignment — PATH 1: first subscriber assigned to a profile with MCX groups
            String pochomeAutoAssignValue = microServicesParamNameValueMap.get(POCHOME_AUTOASSIGN_FLAG);
            boolean pochomeAutoAssignFlag = true;
            if (pochomeAutoAssignValue != null) {
                try {
                    pochomeAutoAssignFlag = Integer.parseInt(pochomeAutoAssignValue) == ENABLED;
                } catch (NumberFormatException e) {
                    knLogger.warn(methodName, "Invalid pochomeAutoAssignValue: ", pochomeAutoAssignValue);
                }
            }
            knLogger.debug(methodName," pochomeAutoAssignFlag : ",pochomeAutoAssignFlag);
            if (!pochomeAutoAssignFlag && mcxGroupCount > 0) {
                knLogger.debug(methodName, "assignUserProfile: baseMdn=", baseMdn, " mcxGroupCount=", mcxGroupCount, " mcxGroupIdList=", mcxGroupIdList);
                List<Integer> nullPocHomeGroupIds = groupInfoUtil.getGroupsWithNullPocHome(mcxGroupIdList, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupsWithNullPocHome=", nullPocHomeGroupIds);
                if (!nullPocHomeGroupIds.isEmpty()) {
                    Map<String, Object> pocMap = groupInfoUtil.getPocHomeAndClusterIdForCorpGroup(
                            Collections.singletonList(baseMdn), xdmsHome, persisterTxn);
                    knLogger.debug(methodName, "pocMap for baseMdn=", baseMdn, " => pocHome=", pocMap.get(POCHOME), " clusterId=", pocMap.get(CLUSTERID));
                    if (pocMap.get(POCHOME) != null && pocMap.get(CLUSTERID) != null) {
                        for (Integer groupId : nullPocHomeGroupIds) {
                            groupInfoUtil.updatePocHome((String) pocMap.get(POCHOME),
                                    (Integer) pocMap.get(CLUSTERID), groupId, xdmsHome, persisterTxn);
                        }
                    } else {
                        knLogger.warn(methodName, "baseMdn has no valid pocHome/clusterId — skipping pochome update for groups: ", nullPocHomeGroupIds);
                    }
                } else {
                    knLogger.debug(methodName, " all MCX groups already have pocHome — nothing to update");
                }
            }
            // Change added for MINT-25965
            KnIPUserProfileDTO modifyDto = new KnIPUserProfileDTO();
            String newUserProfileFS = featureSetUtil.generateUserProfileFeatureSet(userProfile.getFeatureBS());
            boolean areFeatureBitsEqual = userProfileUtil.compareHexBits(userProfile.getFeatureBS(), newUserProfileFS);
            if (!areFeatureBitsEqual) {
                if (modifyDto.getModifiedUserProfileDTO() == null) {
                    modifyDto.setModifiedUserProfileDTO(new KnCorpModifyUserProfileDTO());
                }
                modifyDto.getModifiedUserProfileDTO().setFeatureBS(newUserProfileFS);
                userProfileUtil.updateUserProfile(corpId, profileId, modifyDto.getModifiedUserProfileDTO(), xdmsHome, persisterTxn);
            }
            //create subscriber profile
            //assign contact task
            //assign group
            //set target perms
            //set emergency attribute
            //profile mdn,set target perms for all AU,where this base mdn is TU
            //profile Notification to subscriber with the user profiles;

            populate(respDTO);
        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpUserProfileListRespDTO getUserProfileSubscriberList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getUserProfileSubscriberList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpUserProfileListRespDTO respDTO = new KnCorpUserProfileListRespDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO = new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "--->ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipUserProfileDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_USERPROFILE_SUBSCRIBER_LIST);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);


            //request upId exists in corp.
            //KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(ipUserProfileDTO.getCorpId()
              //      ,ipUserProfileDTO.getProfileId(),xdmsHome,persisterTxn);
            KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(ipUserProfileDTO.getProfileId(),xdmsHome,persisterTxn);
            knLogger.debug("userProfile ",userProfile);
            userProfilePersistDTO.setUserProfileDTO(userProfile);
            userProfilePersistDTO.setUserProfileId(ipUserProfileDTO.getProfileId());

            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.debug(methodName, "Validation Successfull");

            List<KnMDNInfoDto> mdnList = new ArrayList<KnMDNInfoDto>();
            Collection<KnMDNInfoDto> mdnCollection = new ArrayList<>();
            if(userProfile.getCorporateID().equals(Integer.parseInt(ipUserProfileDTO.getCorpId()))) {
                //owner corp will get all the members
                mdnCollection = userProfileUtil.getUserProfileAllSubscriberList(ipUserProfileDTO.getProfileId(),
                        Integer.parseInt(ipUserProfileDTO.getStartIndex()), Integer.parseInt(ipUserProfileDTO.getFetchSize())
                        , xdmsHome, true, persisterTxn);
            }else{
                //shared corp will get only self members
                mdnCollection = userProfileUtil.getUserProfileSubscriberList(Integer.parseInt(ipUserProfileDTO.getCorpId()),ipUserProfileDTO.getProfileId(),
                        Integer.parseInt(ipUserProfileDTO.getStartIndex()), Integer.parseInt(ipUserProfileDTO.getFetchSize())
                        , xdmsHome, true, persisterTxn);
            }

            if(null!=mdnCollection && !mdnCollection.isEmpty()) {
                mdnList.addAll(mdnCollection);
            }

            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                Collection<String> idListStr = (Collection<String>) ipUserProfileDTO.getCustomParamMap().get(KnConstants.IDLIST);
                Set<String> dbFanMdns = corpSubsProvInfoUtil.getMDNListByFanIds(new HashSet<>(convertStrToIntColl(idListStr))
                        , xdmsHome, true, persisterTxn);
                Set<String> fanBaseMdn = corpSubsProvInfoUtil.getBaseMdnByProfileMdns(new ArrayList<>(dbFanMdns), xdmsHome, true, persisterTxn);
                //List<KnMDNInfoDto> res = mdnList.stream().filter(q -> dbFanMdns.contains(q.getMdn())).collect(Collectors.toList());
                mdnList.removeIf(q->!fanBaseMdn.contains(q.getMdn()));
            }

            knLogger.debug(methodName, "--->mdnList - ", mdnList);
            respDTO.setMdnInfoCollection(mdnList);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        }
         catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    /**
     * 1)Get Current Default Profile MDN for the given userProfile & mdn
     * 2)Update default Profile in new ProfileMdn that
     * @param ipUserProfileDTO
     * @param persisterTxn
     * @return
     */

    @Override
    public KnCorpResponseDTO updateDefaultProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        {
            String methodName = "updateDefaultProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

            KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
            KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
            knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
            try {
                KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipUserProfileDTO.getCorpId()),
                        CORP_PROFILE, false, persisterTxn);
                knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
                String xdmsHome = corpProfile.getXdmsHome();
                //Invoking custom hook
                KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
                Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
                if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                    if (customParams != null) {
                        customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                        customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                        customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                        ipUserProfileDTO.setCustomParamMap(customParams);
                        KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                        hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_DEFAULT_PROFILE);
                        hookIPDTO.setData(ipUserProfileDTO);
                        KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                        Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                        if (hookResp instanceof KnCorpHookRespDTO) {
                            responseDTO = (KnCorpHookRespDTO) hookResp;
                            if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                                knLogger.error(methodName, "Returning Failure response");
                                populateXdmResponseFroomHook(responseDTO, respDTO);
                                return respDTO;
                            } else {
                                if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                    respDTO.setFailedDataList(responseDTO.getFailedDataList());
                                }
                            }
                        }
                    }
                }
                userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
                KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(ipUserProfileDTO.getCorpId()
                        ,ipUserProfileDTO.getProfileId(),xdmsHome,persisterTxn);
                String userProfilemdn = userProfileUtil.getProfileMdnByMdnUPID(ipUserProfileDTO.getProfileId(),ipUserProfileDTO.getMdn(), xdmsHome, persisterTxn);
                userProfile.setUserProfilebelongstomdn((userProfilemdn!=null)?true:false);
                knLogger.debug("userProfile ",userProfile);
                userProfilePersistDTO.setUserProfileDTO(userProfile);
                userProfilePersistDTO.setUserProfileId(ipUserProfileDTO.getProfileId());
                knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
                validatorFW.validate(userProfilePersistDTO);
                knLogger.debug(methodName, "Validation Successful");
                if(ipUserProfileDTO.getIsDefaultProfile().equals("1"))
                {
                    String defaultProfileMdn = userProfileUtil.getDefaultProfileMdnByMdn(ipUserProfileDTO.getMdn(), 1,
                            xdmsHome, persisterTxn);
                //If default Profile MDN found then reset isDefault Profile MDN value as 0 for that profileMDN and Base Mdn
                ArrayList<String> mdns=new ArrayList<String>();
                mdns.add(ipUserProfileDTO.getMdn().trim());
                if(null!=defaultProfileMdn && defaultProfileMdn.length()>0){
                    mdns.add(defaultProfileMdn);
                }
                userProfileUtil.updateDefaultProfile(ipUserProfileDTO.getCorpId(),ipUserProfileDTO.getProfileId() , mdns,
                        0 ,xdmsHome, persisterTxn);
                }
                else if(ipUserProfileDTO.getIsDefaultProfile().equals("0"))
                {
                    ArrayList<String> mdns=new ArrayList<String>();
                    mdns.add(ipUserProfileDTO.getMdn().trim());
                    userProfileUtil.updateDefaultProfile(ipUserProfileDTO.getCorpId(),ipUserProfileDTO.getProfileId() , mdns,
                            1 ,xdmsHome, persisterTxn);
                }
                String profileMdn = userProfileUtil.getProfileMdnByMdnUPID(ipUserProfileDTO.getProfileId(),ipUserProfileDTO.getMdn(), xdmsHome, persisterTxn);
                ArrayList<String> userProfileMdns=new ArrayList<String>();
                userProfileMdns.add(profileMdn);
                userProfileUtil.updateDefaultProfile(ipUserProfileDTO.getCorpId(),ipUserProfileDTO.getProfileId() , userProfileMdns,
                        Integer.parseInt(ipUserProfileDTO.getIsDefaultProfile()) ,xdmsHome, persisterTxn);
                

                populate(respDTO);
            } catch (KnValidationException e) {
                populate(respDTO, e);
                knLogger.error(methodName, "KnValidationException occurred", e);
            } catch (KnCorpBOException e) {
                populate(respDTO, e);
                knLogger.error(methodName, "KnCorpBOException occurred", e);
            } catch (Exception e) {
                populate(respDTO, e);
                knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
                        com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            }
            knLogger.debug(methodName, "Success response", respDTO);
            return respDTO;
        }
    }

    @Override
    public KnCorpResponseDTO getProfileMdnByUPId(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        String methodName = "getProfileMdnByUPId(KnIPUserProfileDTO, boolean, KnPersisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId=ipUserProfileDTO.getCorpId();
            String userProfileId = ipUserProfileDTO.getProfileId();

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();

            List<String> profileMdnList = userProfileUtil.getProfileMdnByUPId(corpId,userProfileId, xdmsHome, readOnly, persisterTxn);
            respDTO.setMdnList(profileMdnList);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO getProfileMdnEtag(List<String> userProfileIds, String corpId, KnPersisterTxn persisterTxn) {
        String methodName = "getProfileMdnEtag(List<String>, String, KnPersisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        knLogger.debug(methodName, "userProfileIds - ", userProfileIds, "corpId: ", corpId);
        boolean ownedTxn = false;
        try {
            if(persisterTxn == null){
                ownedTxn = true;
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
            }
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = userProfileUtil.profileMdnEtagUpdate(userProfileIds, corpId,null
                    ,null, corpProfile.getXdmsHome(), persisterTxn);
            respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
            if(ownedTxn){
                persisterTxn.save();
            }
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO unassignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unassignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpUserProfilePersistDTO userProfilePersistDTO=new KnCorpUserProfilePersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId = ipUserProfileDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipUserProfileDTO.getCustomParamMap();
            if (ipUserProfileDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipUserProfileDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.UNASSIGN_USER_PROFILE);
                    hookIPDTO.setData(ipUserProfileDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                respDTO.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            //settting operation and entity id
            userProfilePersistDTO.setInputDTO(ipUserProfileDTO);
            //userProfileExists
            String userProfileId = ipUserProfileDTO.getProfileId();
            //request upId exists in corp.
            KnCorpUserProfileDTO userProfile=userProfileUtil.getUserProfile(userProfileId,xdmsHome,persisterTxn);
            userProfilePersistDTO.setUserProfileDTO(userProfile);
            //validation for mdn exists
            String mdn=ipUserProfileDTO.getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE,false, persisterTxn);
            //Check if any active job exists for the profile.
            List<Integer> jobStatusList = generalCacheUtil.getJobStatusByProfileId(userProfileId);
            userProfilePersistDTO.setJobStatusList(jobStatusList);
            userProfilePersistDTO.setUserProfileId(userProfileId);
            //upmcorp!=mdn corp and upmcorpid not present in shared+own corp list
            //owner of self corp of mdn is allowed to unassign
            Integer upmCorpId = userProfile.getCorporateID();
            int mdnCorpId = subscProfile.getCorpId();
            boolean upmShared = (upmCorpId != mdnCorpId);
            int requestCorpId = Integer.parseInt(corpId);
            List<Integer> upmSharedAllCorpList=new ArrayList<>();
            List<KnUserprofileSharedlistDTO> upmSharedList = userProfileUtil.getUserProfileSharedListByUpmId(userProfileId, xdmsHome, persisterTxn);
            List<Integer> upmOwnerCorpList = upmSharedList.stream().map(KnUserprofileSharedlistDTO::getOwnerCorpId).collect(Collectors.toList());
            List<Integer> upmSharedCorpList = upmSharedList.stream().map(KnUserprofileSharedlistDTO::getSharedCorpId).collect(Collectors.toList());
            upmSharedAllCorpList.addAll(upmOwnerCorpList);
            upmSharedAllCorpList.addAll(upmSharedCorpList);

            boolean unassignUpmAllowed = (upmShared && ((!upmSharedAllCorpList.contains(requestCorpId))
                    ||(mdnCorpId!=requestCorpId&&!upmOwnerCorpList.contains(requestCorpId))));
            userProfilePersistDTO.setUnassignUPMAllowed(unassignUpmAllowed);

            knLogger.debug(methodName, "Before Validation", userProfilePersistDTO);
            validatorFW.validate(userProfilePersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            //profile notification is being sent from KnCorpGenericInfoController.deleteSubscriber
            populate(respDTO);
        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured", e);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO updateImpactedTuPerms(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "controller.updateImpactedTuPerms()";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            int corpId = Integer.parseInt(ipUserProfileDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(ipUserProfileDTO.getCorpId(),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();

            Integer sublistId = ipUserProfileDTO.getModifiedUserProfileDTO().getContactListID();
            List<Integer> sublistIds=new ArrayList<>();
            sublistIds.add(sublistId);
            Set<KnCorpUserProfileMCPTTConfig> addedMcpttPerms = ipUserProfileDTO.getModifiedUserProfileDTO().getAddedMcpttPermissionsConfig();
            Set<KnCorpUserProfileMCPTTConfig> removedMcpttPerms=ipUserProfileDTO.getModifiedUserProfileDTO().getRemovedMcpttPermissionsConfig();
            Set<String> addedMdn=new HashSet<>();
            Set<String> removedMdn=new HashSet<>();

            for(KnCorpUserProfileMCPTTConfig addMdn:addedMcpttPerms){
                addedMdn.add(addMdn.getMdn());
            }

            for(KnCorpUserProfileMCPTTConfig removeMdn:removedMcpttPerms){
                removedMdn.add(removeMdn.getMdn());
            }

            Map<String, Integer> upmIdnContactListIdMap = userProfileUtil.getUserProfileIdFromSublistIds(ipUserProfileDTO.getCorpId()
                    , sublistIds, xdmsHome, persisterTxn);


            Set<String> upmIds = upmIdnContactListIdMap.keySet().stream()
                    .sorted()
                    .collect(Collectors.toSet());
            knLogger.debug(methodName," upmIds :",upmIds);
            userProfileUtil.updateImpactedTuPerms(corpId,addedMdn,removedMdn,upmIds,xdmsHome);

            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO userProfileNotfication(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "controller.userProfileNotfication()";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId = ipUserProfileDTO.getCorpId();
            String userProfileId=ipUserProfileDTO.getProfileId();

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();

            KnUserProfileFSDTO reqUpmFs = ipUserProfileDTO.getUserProfileFSDto();
            knLogger.debug(methodName,"reqUpmFs :",reqUpmFs);

            List<String> userProfileIds = new ArrayList<>();
            userProfileIds.add(userProfileId);
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = userProfileUtil.profileMdnEtagUpdate(userProfileIds, corpId,null
                    ,null, xdmsHome, persisterTxn);
            respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO userProfileNotficationForUpm(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "controller.userProfileNotficationForUpm()";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        knLogger.debug(methodName, "ipUserProfileDTO - ", ipUserProfileDTO);
        try {
            String corpId = ipUserProfileDTO.getCorpId();
            String userProfileId=ipUserProfileDTO.getProfileId();

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();

            KnUserProfileFSDTO reqUpmFs = ipUserProfileDTO.getUserProfileFSDto();
            knLogger.debug(methodName,"reqUpmFs :",reqUpmFs);

            List<String> userProfileIds = new ArrayList<>();
            userProfileIds.add(userProfileId);
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = userProfileUtil.profileMdnEtagUpdateForUpm(userProfileIds, corpId,null
                    ,null, xdmsHome, persisterTxn);
            respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO deleteMcpttPermConfig(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        final String methodName="deleteMcpttPermConfig(ipUserProfileDTO,persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try{
            knLogger.debug(methodName,"Entry :",ipUserProfileDTO);
            String mdn=ipUserProfileDTO.getMdn();
            String corpId = ipUserProfileDTO.getCorpId();
            String xdmsHome = ipUserProfileDTO.getPocPttServerId();
            List<String> mdnList = ipUserProfileDTO.getMdnList();
            userProfileUtil.deleteMcpttPermConfig(mdn,corpId,xdmsHome);
            if(null != mdnList && !mdnList.isEmpty()){
                userProfileUtil.deleteMcpttPermConfig(mdnList,corpId,xdmsHome);
            }
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO getAsyncOpStatus(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getAsyncOpStatus(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnGeneralCacheUtil util= new KnGeneralCacheUtil();
        knLogger.debug(methodName, "--->ipUserProfileDTO - ", ipUserProfileDTO);
        try{
        String corpId = ipUserProfileDTO.getCorpId();
        KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                CORP_PROFILE, false, persisterTxn);
        List<KnAsyncJobDTO> jobList=util.getJobsByTxnIds(ipUserProfileDTO.getTxnList());
        respDTO.setJobStatus(jobList);
        populate(respDTO);
        knLogger.debug(methodName, "--->respDTO - ", respDTO);

    } catch (KnCorpBOException e) {
        populate(respDTO, e);
        knLogger.error(methodName, "KnCorpBOException occured", e);
    } catch (Exception e) {
        populate(respDTO, e);
        knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
    }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }
    @Override
    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userProfileIds, String corpId, boolean readOnly, KnPersisterTxn persisterTxn) {
        String methodName = "getUserProfileSubsCount(Collection<String>, boolean, KnPersisterTxn)";
        List<KnUserProfileAssignedDTO> userProfileAssignedDTOMap = new ArrayList<>();
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            userProfileAssignedDTOMap = userProfileUtil.getUserProfileSubsCount(userProfileIds, xdmsHome, readOnly, persisterTxn);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Response ", userProfileAssignedDTOMap);
        return userProfileAssignedDTOMap;
    }
}