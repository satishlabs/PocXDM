/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:KnCorpGenericInfoController.java
 * Subsystem:PoCKnGenInfoUtil.java
 * <p/>
 * Name Date Release
 * ---------------------------------------------------------------------
 * Upananda Singha Feb 16,2011 7.0
 * <p/>
 * <p/>
 * Copyright(c)2006Kodiak Networks(India)Pvt.Ltd.
 * #401,4th Floor,'Prestige Sigma'
 * No.3,Vittal Mallya Road
 * Bangalore-560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks,Inc.You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.common.commdto.request.KnXDMCorpInfoDTO;
import com.kodiak.common.commdto.response.KnCorporateProfilepersistDTO1;
import com.kodiak.common.commdto.response.KnDialPlanDTO;
import com.kodiak.common.commdto.request.KnIPCatPermissionSetDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.ggcache.dto.KnSharedTrustMatrixHierarchyDTO;
import com.kodiak.common.ggcache.KnGGCache;
import com.kodiak.common.commdto.common.KnHierarchyMappingInfo;
import com.kodiak.common.commdto.common.KnHierarchyGroupMappingInfo;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants;
import com.kodiak.frameworks.dbfw.collectors.KnSqlJobCollector;
import com.kodiak.frameworks.normalizationfw.KnNormException;
import com.kodiak.frameworks.normalizationfw.clientIntf.INormalizeIntf;
import com.kodiak.frameworks.normalizationfw.clientIntf.impl.KnNormalizeImpl;
import com.kodiak.frameworks.normalizationfw.dto.KnDialPlanConfigDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpGenericInfoController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.common.ggcache.dao.KnSharedTrustMatrixHierarchyDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnXDMGroupHiearchyMapDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnCorpXDMTablesRegistry;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.MCVIDEOTX;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.MCVIDEOUNCONFIRMEDPULL;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_23;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.xdms.server.common.resources.KnConstants.COMMON_CONTACTLIST_PERSUB;
import static com.kodiak.xdms.server.common.resources.KnConstants.COMMON_CONTACTLIST_SIZE;
import static com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.MAXMEM_NONAP_SUBLIST;
import static com.kodiak.xdms.server.common.resources.KnConstants.MAX_STATUS_MSG_LENGTH;
import static com.kodiak.xdms.server.common.resources.KnConstants.MAX_STATUS_MSG_PER_OSM_LIST;
import static com.kodiak.xdms.server.common.resources.KnConstants.MAX_STATUS_SHORT_TEXT_LENGTH;
import static com.kodiak.xdms.server.common.resources.KnConstants.MAX_USER_PROFILES;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SELF_DND_FEATURE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.SYSTEM_LEVEL_SELF_DND_PRIVILEGE_FLAG_DISABLED;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.*;

//import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
//import com.kodiak.xdms.server.corpmgmt.resources.KnActions;


public class KnCorpGenericInfoController implements ICorpGenericInfoController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGenericInfoController.class);

    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnAASFramework authorizationFwk;
    KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnValidatorFramework validatorFW;
    private KnGeneralUtil generalUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpActivationInfoUtil activationInfoUtil;
    private KnCorpUserProfileUtil corpUserProfileUtil;
    private KnCorpGroupProfileUtil groupProfilUtil;
    private KnCorpUserProfileUtil userProfileUtil;
    private KnCorpOSMInfoUtil corpOSMInfoUtil;
    private KnCorpHierarchyInfoUtil hierarchyInfoUtil;

    public KnCorpGenericInfoController() {
        commonInfoUtil = new KnCorpCommonInfoUtil();
        contactInfoUtil = new KnCorpContactInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        generalUtil = new KnGeneralUtil();
        authorizationFwk = KnAASFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        genInfoUtil = KnGenInfoUtil.getInstance();
        activationInfoUtil = new KnCorpActivationInfoUtil();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        groupProfilUtil = new KnCorpGroupProfileUtil();
        userProfileUtil=new KnCorpUserProfileUtil();
        corpOSMInfoUtil = new KnCorpOSMInfoUtil();
        hierarchyInfoUtil = new KnCorpHierarchyInfoUtil();
    }

    public KnCorpAuthInfoRespDTO authenticate(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {

        String methodName = "authenticate(authInfoDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", authInfoDTO.getExtCorpId());
        KnCorpAuthInfoRespDTO respDTO = new KnCorpAuthInfoRespDTO();
        try {
            String extCorpId = authInfoDTO.getExtCorpId();
            INormalizeIntf normalize = KnNormalizeImpl.getInstance();
            // Invoking custom hook
            Map<String, Object> customParams = authInfoDTO.getCustomParamMap();
            Object hookResp = null;
            boolean isGWEnabled = Boolean.FALSE;
            int nxtGenCatAccess_FAN_BAN = 0;
//            boolean isFAN_BANParameter = false;
            if (authInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                //customParams.put(KnPersisterConstants.PTT_SERVER_ID, corpProfile.getXdmsHome());
                authInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.AUTHENTICATE);
                hookIPDTO.setData(authInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook");
                //get if any Alias MDN or the Group MDN are present in the incontext
                hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                knLogger.debug(methodName, "Call After Hook");
                // If hook reaponse is null and is instance of KnCorpHookRespDTO - that means response recieved is
                // from corporate custom module else response is loop back response
                if (hookResp instanceof KnCorpHookRespDTO) {
                    KnCorpHookRespDTO responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    } else {
                        //Modified for 8.1.1 Release
                        nxtGenCatAccess_FAN_BAN  = (Integer)responseDTO.getCustomParamMap().get(KnConstants.NXT_GEN_CAT_ENABLED);
                        //isFAN_BANParameter = true;
                        knLogger.debug(methodName, "Returning Hook Success response - ", responseDTO);
                        extCorpId = responseDTO.getExtCorpId();
                        KnIdDetailsListDTO idDetailsListDTO = (KnIdDetailsListDTO) responseDTO.getData();
                        knLogger.debug(methodName, "Returning Hook idDetailsListDTO response - ", idDetailsListDTO);
                        Map<String, Object> respCustomMap = responseDTO.getCustomParamMap();
                        knLogger.debug(methodName, "Returning Hook respCustomMap response - ", respCustomMap);
                        respCustomMap.put(com.kodiak.common.resources.KnConstants.IDDETAILSLIST, idDetailsListDTO);
                        knLogger.debug(methodName, "Returning Hook respCustomMap response - ", respCustomMap);
                        respDTO.setCustomParamMap(respCustomMap);
                        if (respDTO.getCustomParamMap() != null && respDTO.getCustomParamMap().get(KnConstants.GW_ENABLED_INDICATOR) != null) {
                            isGWEnabled = ((Boolean) respDTO.getCustomParamMap().get(KnConstants.GW_ENABLED_INDICATOR));
                        }
                        knLogger.debug(methodName, "Gateway enabled indicator - ", isGWEnabled);
                    }
                }

            }
            //Checking the Blocked Corporate
            //List<String> extCorpID = commonInfoUtil.getExtCorpId(persisterTxn);
            Map<String, String> configMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            knLogger.debug(methodName, "Blocked Corporate MAP", configMap);
            String extIds = configMap.get(KnConstants.IDS_BLOCKED_CAT_CORP_LIST);
            List<String> extCorpIdList = new ArrayList<>();
            if (extIds != null) {
                String[] extCorpValue = extIds.split(";");
                extCorpIdList.addAll(Arrays.asList(extCorpValue));
            }
            knLogger.debug(methodName, "Blocked Corporate list ", extCorpIdList);
            knLogger.debug(methodName, "extCorpId ", extCorpId);
            for (String id : extCorpIdList) {
                if (id.equals(extCorpId.trim())) {
                    knLogger.error(methodName, "unauthorized Corporate for Authenticate ");
                    throw new KnCorpBOException(KnErrorCodes.Authorizer.UNAUTHORISED_CORPORATE, "CORPORATE_IS_BLOCKED");
                }
            }
            //Custom hook invocation completed.
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            int corpId = commonInfoUtil.getCorpId(extCorpId, persisterTxn);
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, true, persisterTxn);
            if (authInfoDTO.getHierarchyType() == HIERARCHY_TYPE.NON_HIERARCHY) {
                //its a product flow check if there are any SG/SU mdn in the corpoerate
                isGWEnabled = contactInfoUtil.getIsGWEnabledForCorp(corpId, corpProfile.getXdmsHome(), persisterTxn);
            }
            knLogger.debug(methodName, "After get the profile details");
            KnCorpProfilePersistDTO corpPersistDTO = new KnCorpProfilePersistDTO();
            corpPersistDTO.setInputDTO(authInfoDTO);
            corpPersistDTO.setPairedContactListId(corpProfile.getPairedContactListId());
            knLogger.debug(methodName, "Before Authorization DTO passed - ", corpPersistDTO);
            authorizationFwk.authorize(corpPersistDTO);
            knLogger.debug(methodName, "After Authorization ");
            KnCorpProfileInfoDTO profileInfoDto = new KnCorpProfileInfoDTO();
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));

            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of locWatcherConfig", microServicesParamNameValueMap);
            String locWatcherConfigValue = microServicesParamNameValueMap.get(KnConstants.GRPMEM_LOC_SVC_ENABLED);
            String bulkExtContAllowed = microServicesParamNameValueMap.get(KnConstants.BLK_EXT_CONT_LMT);
            String deviceSharingFlag = microServicesParamNameValueMap.get(KnConstants.DEVICE_SHARING_FEATURE_FLAG);
            String bulkLimitCorpAdminFs = microServicesParamNameValueMap.get(CORPADMINFSBULKAPILIMIT);
            String osmFeatureFlag = microServicesParamNameValueMap.get(OSMFEATUREFLAG);
            String userProfileMgmt = microServicesParamNameValueMap.get(USER_PROFILE_MGMT);
            String maxUserProfiles = microServicesParamNameValueMap.get(MAX_USER_PROFILES);
            String maxMemNonAPSubList = microServicesParamNameValueMap.get(MAXMEM_NONAP_SUBLIST);
            String maxUserProfilesPerSub = microServicesParamNameValueMap.get(MAX_USERPROFILES_PERSUB);
            String allowGroupAcrossZones = microServicesParamNameValueMap.get(ALLOW_GROUP_ACROSS_ZONES);
            String isVLrgGrpEnabled = microServicesParamNameValueMap.get(VLARGE_GROUP_SUPPORTED);
            String sysGroupProfileMgmt = microServicesParamNameValueMap.get(GROUP_PROFILE_MGMT);
            String sysMaxGroupProfileMgmt = microServicesParamNameValueMap.get(MAX_GROUP_PROFILES);
            String sysMaxGroupPerCRIClient = microServicesParamNameValueMap.get(MAX_GROUPS_PER_CRI_CLIENT);
            String sysGroupSharing = microServicesParamNameValueMap.get(GROUP_SHARING_FLAG);
            String mcxGroupReGroupFlag = microServicesParamNameValueMap.get(MCX_GROUP_REGROUP_FLAG);
            String sysTrkMail = microServicesParamNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.TRK_EMAIL_FLAG);
            String sysEmergDestAll = microServicesParamNameValueMap.get(EMERG_DEST_ALL);
            String sysuserProfileSharingFlag=microServicesParamNameValueMap.get(USER_PROFILE_SHARING_FLAG);
            String sysCommonContactListSupport = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT);
            String sysPocGwSynEnable=microServicesParamNameValueMap.get(POC_GW_SYNC_ENABLE);
            String sysLargeAgencyDispatch=microServicesParamNameValueMap.get(LARGE_AGENCY_DISPATCH_FEATURE);

            //corp value
            Integer corpGroupProfileMgmt = corpProfile.getGroupProfileMgmt();
            Integer corpMaxGroupProfileMgmt = corpProfile.getMaxgGroupProfiles();

            //Checking the allowSUContact value from RTXvariable.
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            knLogger.debug(methodName, "param value of allowed SU or not", paramNameValueMap);
            String paramValue = paramNameValueMap.get(KnConstants.XDMS_ALLOW_SUMDN_AS_CONTACT);
            String maxLocWatcherPerGroup = paramNameValueMap.get(KnConstants.MAX_LOC_WATCHERS_PER_GRP);
            String convClientEnabled = paramNameValueMap.get(KnConstants.CONV_CLIENT_ENABLED);

            if (paramValue != null) {
                profileInfoDto.setAllowSUContact(Integer.parseInt(paramValue));
            }
            knLogger.debug(methodName, "corpProfile.getPairedContactListId()", corpProfile.getPairedContactListId());
            if (corpProfile.getPairedContactListId() > 0) {
                knLogger.debug(methodName, "Inside to update the pairecontactlistid");
                sublistInfoUtil.updateCorpPairedContListId(corpId, 0, corpProfile.getXdmsHome(), persisterTxn);
            }
            //Modified for 8.1.1 Release
            int nxtGenCatAccess =  Integer.parseInt(paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.XDM_NXT_GEN_CAT_ENABLED));
//           nxtGenCatAccess = (nxtGenCatAccess==0) ? (!isFAN_BANParameter) ? (corpProfile.getNxtGenCatEnabled()==0)?0:1:nxtGenCatAccess_FAN_BAN:1;
            nxtGenCatAccess = (nxtGenCatAccess==1) ? 1 : (nxtGenCatAccess_FAN_BAN==1) ? 1 : (corpProfile.getNxtGenCatEnabled()==1) ? 1 :0;

            final String maxStatusMsgPerOsmList = microServicesParamNameValueMap.get(MAX_STATUS_MSG_PER_OSM_LIST);
            final String maxStatusShortTextLength = microServicesParamNameValueMap.get(MAX_STATUS_SHORT_TEXT_LENGTH);
            final String maxStatusMsgLength = microServicesParamNameValueMap.get(MAX_STATUS_MSG_LENGTH);
            final String maxCommonContactLisSize = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SIZE);
            final String maxCommonContactlistPerSub = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_PERSUB);

            profileInfoDto.setMaxStatusMsgPerOsmList(maxStatusMsgPerOsmList);
            profileInfoDto.setMaxStatusShortTextLength(maxStatusShortTextLength);
            profileInfoDto.setMaxStatusMsgLength(maxStatusMsgLength);

            profileInfoDto.setCorpId(String.valueOf(corpProfile.getCorpId()));
            profileInfoDto.setExtCorpId(extCorpId);
            profileInfoDto.setMaxContactsPerSubsc(corpProfile.getMaxContactsPerSubsc());
            profileInfoDto.setMaxCorpGroups(corpProfile.getMaxCorpGroups());
            profileInfoDto.setMaxCorpLists(corpProfile.getMaxCorpLists());
            profileInfoDto.setMaxMemPerCorpGroup(corpProfile.getMaxMemPerCorpGroup());
            profileInfoDto.setMaxMemPerCorpList(corpProfile.getMaxMemPerCorpList());
            profileInfoDto.setMaxExtContactsPerCorp(corpProfile.getMaxExtContactsPerCorp());
            profileInfoDto.setSupervisoryOverrideEnabled(corpProfile.getSupervisoryOverrideEnabled());
            profileInfoDto.setMaxDispatchGroup(corpProfile.getMaxDispatchGroup());
            profileInfoDto.setMaxMembersPerDispatchGroup(corpProfile.getMaxMembersPerDispatchGroup());
            profileInfoDto.setCorpName(corpProfile.getNetworkName());
            profileInfoDto.setMaxContactsPerRequest(corpProfile.getMaxContactsPerRequest());
            KnDialPlanConfigDTO dialPlanInfo = normalize.getDefaultDialPlanInfo();
            profileInfoDto.setDialPlanList(commonInfoUtil.getDialPlanDto(dialPlanInfo));
            profileInfoDto.setDispatchEnabled(corpProfile.getDispatchEnabled());
            profileInfoDto.setEnablePocDonorRadioSupport(corpProfile.getEnablePocDonorRadioSupport());
            profileInfoDto.setMaxSubsAllowedGenActvReq(corpProfile.getMaxSubsAllowedGenActvReq());
            profileInfoDto.setEnableTalkGroup(corpProfile.getSystemTGSBit());
            profileInfoDto.setMaxPriority(corpProfile.getMaxPriority());
            profileInfoDto.setMaxScanListSize(corpProfile.getMaxCampedGrp());
            profileInfoDto.setMaxNniSubscrPerCorp(corpProfile.getMaxExtSubsPerCorp());
            profileInfoDto.setMaxMemPerBCGrp(corpProfile.getMaxMemPerBCGrp());
            profileInfoDto.setEnableBCGFeature(corpProfile.getEnableBCGrpFeature());
            profileInfoDto.setMaxSGPerGrp(corpProfile.getMaxSGPerGrp());
            profileInfoDto.setGWEnabled(isGWEnabled);
            profileInfoDto.setMaxDisptcherPerDispatchGrp(corpProfile.getMaxDispatchMembersPerDispatchGroup());
            profileInfoDto.setIsLocWatcher(Integer.parseInt(locWatcherConfigValue));
            profileInfoDto.setOsmFeatureFlag(osmFeatureFlag!=null?Integer.parseInt(osmFeatureFlag):0);
            profileInfoDto.setConvClientEnabled(Integer.parseInt(convClientEnabled));
            profileInfoDto.setTextMsgFlag(corpProfile.getTextMsgFlag());
            profileInfoDto.setMultiMediaMsgFlag(corpProfile.getMultiMediaMsgFlag());
            profileInfoDto.setLocationMsgFlag(corpProfile.getLocationMsgFlag());
            profileInfoDto.setUrgentMsgFlag(corpProfile.getUrgentMsgFlag());
            profileInfoDto.setWebDispatcherEnabled(corpProfile.getWebDispatchEnabled());
            profileInfoDto.setInteropLicenceType(corpProfile.getInteropLicenceType());
            profileInfoDto.setAmbientListening(corpProfile.getAmbientListening());
            profileInfoDto.setDiscreteListening(corpProfile.getDiscreteListening());
            profileInfoDto.setUserCheck(corpProfile.getUserCheck());
            profileInfoDto.setUserSvcCtrl(corpProfile.getUserSvcCtrl());
            profileInfoDto.setEmergFeature(corpProfile.getEmergFeature());
            profileInfoDto.setLargeGrpSupport(corpProfile.getLargeGrpSupported());
            profileInfoDto.setMcVideoEnabled(corpProfile.getMcVideoEnabled());
            if(isVLrgGrpEnabled != null) profileInfoDto.setIsVLrgGrpEnabled(Integer.parseInt(isVLrgGrpEnabled));
            profileInfoDto.setMcVideoUnCfrmPullEnabled(corpProfile.getMcVideoUnCfrmPullEnabled());
            profileInfoDto.setOpsCorpFs(corpProfile.getOpsCorpFs2());
            if(deviceSharingFlag != null) profileInfoDto.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
			if (corpProfile.getUserProfileMgmt() == null && userProfileMgmt != null) {
				profileInfoDto.setUserProfileMgmt(Integer.parseInt(userProfileMgmt));
			} else {
				if (userProfileMgmt != null && userProfileMgmt.equals("1")
						&& corpProfile.getUserProfileMgmt().equals(1)) {
					profileInfoDto.setUserProfileMgmt(1);
				} else {
					profileInfoDto.setUserProfileMgmt(0);
				}
			}
			if (corpProfile.getMaxUserProfiles() == null && maxUserProfiles != null) {
				profileInfoDto.setMaxUserProfiles(Integer.valueOf(maxUserProfiles));
			} else {
				profileInfoDto.setMaxUserProfiles(corpProfile.getMaxUserProfiles());
			}
			if (corpProfile.getMaxAssignProfiles() == null && maxUserProfilesPerSub != null) {
				profileInfoDto.setMaxAssignProfiles(Integer.valueOf(maxUserProfilesPerSub));
			} else {
				profileInfoDto.setMaxAssignProfiles(corpProfile.getMaxAssignProfiles());
			}
            profileInfoDto.setAllowGroupAcrossZones(allowGroupAcrossZones!=null?Integer.parseInt(allowGroupAcrossZones):0);

			profileInfoDto.setMaxMemNonAPSubList(Integer.parseInt(maxMemNonAPSubList));

            profileInfoDto.setGroupProfileMgmt(corpGroupProfileMgmt==null?Integer.parseInt(sysGroupProfileMgmt):corpGroupProfileMgmt);
            profileInfoDto.setGroupSharingFeature(corpProfile.getGroupSharingFeature()==null?Integer.parseInt(sysGroupSharing):corpProfile.getGroupSharingFeature());
            profileInfoDto.setUpmSharingFlag(corpProfile.getUserProfileSharingFeature()==null||corpProfile.getUserProfileSharingFeature()==0 ?Integer.parseInt(sysuserProfileSharingFlag):corpProfile.getUserProfileSharingFeature());
            knLogger.debug(methodName, "syscommoncontactlistsupport: " +sysCommonContactListSupport, "corpProfilecommoncontactlstsupport:  " +corpProfile.getCommonContactListSupport());
           if (corpProfile.getCommonContactListSupport() != null || sysCommonContactListSupport != null) {
               profileInfoDto.setCommonContactListSupport(corpProfile.getCommonContactListSupport() == null ? Integer.parseInt(sysCommonContactListSupport) : corpProfile.getCommonContactListSupport());
           }
            profileInfoDto.setCatAccessPermSet(corpProfile.getCatAccessPermSet());
            profileInfoDto.setCatAccessPermUpdateTS(corpProfile.getCatAccessPermUpdateTS());
            //trkMail Supported flag
            int trkMailSupported = sysTrkMail!=null ? Integer.parseInt(sysTrkMail):0;
            profileInfoDto.setTrkMailSupported(trkMailSupported);
            if(mcxGroupReGroupFlag != null)
               profileInfoDto.setMcxGroupReGroupFlag(mcxGroupReGroupFlag);
            //EMERG_DEST_ALL flag SFR 11.2
            int emergDestAll = sysEmergDestAll!=null ? Integer.parseInt(sysEmergDestAll):0;
            profileInfoDto.setEmergDestAll(emergDestAll);
            knLogger.debug(methodName, "setting EmergDestAll.." +emergDestAll );

            int pocGwSynEnable = sysPocGwSynEnable!=null ? Integer.parseInt(sysPocGwSynEnable):0;
            profileInfoDto.setPocGwSynEnable(pocGwSynEnable);

            int sysLargeAgencyDispatchFlag = (sysLargeAgencyDispatch!=null) ? Integer.parseInt(sysLargeAgencyDispatch):0;

            //ConfigFeatureSet
            profileInfoDto.setConfigFeatureSet(calculateConfigFeatureBit(corpId, isGWEnabled, profileInfoDto, corpProfile.getXdmsHome(), persisterTxn, nxtGenCatAccess));
            profileInfoDto.setPttRadioScanListSize(corpProfile.getPttRadioScanListSize());
            profileInfoDto.setPttRadioChannelListSize(corpProfile.getPttRadioChannelListSize());
            profileInfoDto.setPttRadioDefScanMode(corpProfile.getPttRadioDefScanMode());
            profileInfoDto.setMaxLocWatcherPerGrp(Integer.parseInt(maxLocWatcherPerGroup));
            profileInfoDto.setMaxSGPatchPerGrp(corpProfile.getMaxSGPatchPerGrp());
            profileInfoDto.setMaxChannelsPerZone(corpProfile.getMaxChannelsPerZone());
            profileInfoDto.setMaxRadioChannels(corpProfile.getMaxRadioChannels());
            profileInfoDto.setMaxZones(corpProfile.getMaxZones());
            profileInfoDto.setMaxLrgGrpPerCorp(corpProfile.getMaxLrgGrpPerCorp());
            profileInfoDto.setMaxMemPerLrgGrp(corpProfile.getMaxMemPerLrgGrp());
            profileInfoDto.setMaxLrgBGrpPerCorp(corpProfile.getMaxLrgBGrpPerCorp());
            profileInfoDto.setMaxMemPerLrgBGrp(corpProfile.getMaxMemPerLrgBGrp());
            profileInfoDto.setHierarchyType(corpProfile.getHierarchyType());
            if(bulkExtContAllowed != null) profileInfoDto.setBulkExtContAllowed(Integer.parseInt(bulkExtContAllowed));
            if(bulkLimitCorpAdminFs != null) profileInfoDto.setMaxBulkCorpAdminFsUpdateAllowed(bulkLimitCorpAdminFs);
            profileInfoDto.setMaxGrpProfiles(corpMaxGroupProfileMgmt==null?sysMaxGroupProfileMgmt:String.valueOf(corpMaxGroupProfileMgmt));
            profileInfoDto.setMaxGrpsPerCriClient(sysMaxGroupPerCRIClient != null ? Integer.parseInt(sysMaxGroupPerCRIClient) : 0);
            if(maxCommonContactLisSize != null) profileInfoDto.setMaxCommonContactLisSize(maxCommonContactLisSize);
            if(maxCommonContactlistPerSub != null) profileInfoDto.setMaxCommonContactlistPerSub(maxCommonContactlistPerSub);
            String sysSelfDndFeature = microServicesParamNameValueMap.get(SELF_DND_FEATURE);
            boolean selfDndPrivilegeBit = KnGeneralUtil.getFeatureBitValue(corpProfile.getCorpFS2(), FEATURE_SET.SELF_DND_PRIVILEGE.value());
            if (null != sysSelfDndFeature && (sysSelfDndFeature.equalsIgnoreCase(ENABLED_STRING)) && selfDndPrivilegeBit) {
                profileInfoDto.setSelfDnDPrivilege(ENABLED);
            } else {
                profileInfoDto.setSelfDnDPrivilege(DISABLED);
            }

            if ((sysLargeAgencyDispatchFlag == 1) &&
                    (KnGeneralUtil.getFeatureBitValue(corpProfile.getCorpFS2(), FEATURE_SET.LARGE_AGENCY_DISPATCH.value()))) {
                profileInfoDto.setLargeAgencyDispatch(ENABLED);
            }
            else
            {
                profileInfoDto.setLargeAgencyDispatch(DISABLED);
            }
            //Adding emergency config timer feature in authenticate api
            String systemEmergConfigTimer = microServicesParamNameValueMap.get(EMERGENCY_CONF_TIMER_FEATURE);
            boolean emergencyBit = Optional.ofNullable(corpProfile.getXdmCorpFS2Set())
                    .map(set -> KnGeneralUtil.getFeatureBitValue(set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value()))
                    .orElse(false);
            knLogger.debug(methodName, "Systemlevel Emergency config value - ", systemEmergConfigTimer, "emergencyBit - ", emergencyBit);
            profileInfoDto.setEmergConfigTimerFeature(
                    systemEmergConfigTimer.equalsIgnoreCase(ENABLED_STRING) && emergencyBit ? ENABLED : DISABLED);

            respDTO.setProfileInfoDTO(profileInfoDto);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving CorpProfile Info - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving CorpProfile Info - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT-->");
        return respDTO;
    }

    public KnCorpDirInfoRespDTO getSubsDirectory(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {

        String methodName = "getSubsDirectory(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO.getMdn());
        KnCorpDirInfoRespDTO respDTO = new KnCorpDirInfoRespDTO();
        try {
            /* As part of performance tuning activity removed below codes:-
                1. call to get subscriber profile as xdmsHome is sending from mediator.
                2. call to get corporate profile since it was used for getting maxContPerSubs which is not required.
                3. etag validation since XCAP module is already doing the same.
                4. Not returning the currect directory etag since its of no use
             */
            knLogger.debug(methodName, "Fetch the subscribers profile if cached or fetch from the DB the details");
            String mdn = contactDTO.getMdn();
            String xdmsHomePttId = contactDTO.getXdmsHome();
            List<KnCorpFolderInfoDTO> foldersList = new ArrayList<KnCorpFolderInfoDTO>();
            knLogger.debug(methodName, "Before DB call to get the subscribers resource List details.");
            //1.only ListName and etag
            int resourceListEtag = contactInfoUtil.getSubscriberResourceListEtag(mdn, xdmsHomePttId, true, persisterTxn);
            KnCorpFolderInfoDTO resourceFolder = new KnCorpFolderInfoDTO();
            resourceFolder.setAuid(APP_UID_CORP_RESOURCE_LIST);
            KnCorpDocInfoDTO resourceDocument = new KnCorpDocInfoDTO();
            resourceDocument.setEtag(String.valueOf(resourceListEtag));
            resourceDocument.setDocName(XCAP_INDEX);
            List<KnCorpDocInfoDTO> resourceDocumentList = new ArrayList<KnCorpDocInfoDTO>();
            resourceDocumentList.add(resourceDocument);
            resourceFolder.setEntries(resourceDocumentList);
            foldersList.add(resourceFolder);
            knLogger.debug(methodName, "Before DB call to get the group Documents details.");
            //request upId exists in corp.
            String corpId = String.valueOf(contactDTO.getCorpId());
            String userProfileId = contactDTO.getUserProfileId();
            List<Integer> groupIdListFromProfile = new ArrayList<>();
            knLogger.debug(methodName, "corpId ", corpId, "userProfileId - ", userProfileId);
            if (userProfileId != null && contactDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_19){
              //  KnCorpUserProfileDTO userProfile = corpUserProfileUtil.getUserProfile(corpId, userProfileId, xdmsHomePttId, persisterTxn);
				Set<KnCorpGroupListInfoDTO> mcxGroupList=corpUserProfileUtil.retriveGroupProfileInfoByProfileId(userProfileId, xdmsHomePttId, persisterTxn);

                knLogger.debug(methodName, "userProfile ", mcxGroupList);
                if (mcxGroupList != null && mcxGroupList.size() > 0) {
                    groupIdListFromProfile = mcxGroupList.stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList());
                }
            }

            List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIdListFromProfile, xdmsHomePttId, persisterTxn);
            List<Integer> mcxGroups = groupInfoList.stream()
                    .filter(groupDetails -> groupDetails.getMcxGrpInd() == 1).map(KnCorpGroupDTO::getGroupId).collect(Collectors.toList());
            knLogger.debug(methodName,"mcxGroups :",mcxGroups);
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            Map<String, List<Integer>> memberGroupIdsMap = null;
            boolean MCXGRPREGRP = KnGeneralUtil.getFeatureBitValue(contactDTO.getSubsActiveFS2(), FEATURE_SET.MCX_GROUP_REGROUP_FLAG_BIT.value());
            knLogger.debug("MCXGRPREGRP: "+MCXGRPREGRP);
            //TODO: Uncomment this if (reGroupFeatureEnable) {
            List<Integer> cpgroupIds = new ArrayList<>();
            if (MCXGRPREGRP) {
                knLogger.debug("if true: ");
                List<Integer> preConfigCpgroupIds = xdmDAO.selectOwnerPreConfigCorpGroupId(Integer.parseInt(corpId), persisterTxn);
                cpgroupIds.addAll(preConfigCpgroupIds);
                knLogger.debug(methodName, "cpgroupIds_preConfigCpgroupIds: "+preConfigCpgroupIds);
                Map<Integer,List<Integer>> ownerCorpIdAndPreConfigGrps = xdmDAO.selectOwnerCorpIdAndPreConfigGrps(Integer.parseInt(corpId),persisterTxn);
                knLogger.debug(methodName, "ownerCorpIdAndPreConfigGrps: "+ownerCorpIdAndPreConfigGrps);
                for(Map.Entry<Integer,List<Integer>> entry : ownerCorpIdAndPreConfigGrps.entrySet())
                {
                    boolean isBelongedToGroup = groupInfoUtil.subscrBelongsToSharedPreConfGroup(entry.getKey(),List.of(Integer.parseInt(corpId)), mdn,xdmDAO, persisterTxn);
                    if(isBelongedToGroup)
                        cpgroupIds.addAll(entry.getValue());
                    knLogger.debug(methodName, "isBelongedToGroup: "+isBelongedToGroup, "cpgroupIds: "+cpgroupIds);
                }
                if (userProfileId != null) {
                    List<Integer> ownedCorpPreConfGrpForProfile = xdmDAO.selectOwnerPreConfigCorpGroupId(Integer.parseInt(corpId), persisterTxn);
                    if(ownedCorpPreConfGrpForProfile != null && !ownedCorpPreConfGrpForProfile.isEmpty())
                        cpgroupIds.addAll(ownedCorpPreConfGrpForProfile);
                    knLogger.debug(methodName, "ownedCorpPreConfGrpForProfile: "+ownedCorpPreConfGrpForProfile);

                    List<Integer> sharedCorpPreConfGrpForProfile = corpUserProfileUtil.getUserProfileGroupIdBySharedCorpId(Integer.parseInt(corpId),xdmDAO,persisterTxn);
                    knLogger.debug(methodName, "sharedCorpPreConfGrpForProfile: "+sharedCorpPreConfGrpForProfile);
                    if(sharedCorpPreConfGrpForProfile != null && !sharedCorpPreConfGrpForProfile.isEmpty())
                        cpgroupIds.addAll(sharedCorpPreConfGrpForProfile);
                    knLogger.debug(methodName, "cpgroupIds: "+cpgroupIds);
                }

            }
            if (!isNullOrEmpty(userProfileId)) {
                KnCorpGroupInfoUtil knCorpGroupInfoUtil = new KnCorpGroupInfoUtil();
                List<KnCorpGroupInfoPersistDTO> groupList = knCorpGroupInfoUtil.getProfileMdnsAbdgGroupList(mdn, xdmsHomePttId, persisterTxn);
                if (!groupList.isEmpty()) {
                    List<Integer> abdgGroups = groupList.stream().map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toList());
                    cpgroupIds.addAll(abdgGroups);
                }
            }
            List<String> tgssGroupString = xdmDAO.getTgssGroupExtM(mdn, persisterTxn);
            List<Integer> tgssGroups = tgssGroupString.stream().map(Integer::valueOf).collect(Collectors.toList());
            Map<Integer, KnCorpGrpBasicInfoDTO> groupEtagMap = groupInfoUtil.getSubsGrpLstForGetDir(contactDTO,mcxGroups, xdmsHomePttId, persisterTxn, cpgroupIds);
            KnCorpFolderInfoDTO groupFolder = new KnCorpFolderInfoDTO();

            groupFolder.setAuid(APP_UID_CORP_GROUP);
            if (!groupEtagMap.isEmpty()) {
                List<KnCorpDocInfoDTO> groupDocumentList = new ArrayList<KnCorpDocInfoDTO>();
                List<Map.Entry<Integer, KnCorpGrpBasicInfoDTO>> groupList = new ArrayList<>(groupEtagMap.entrySet().stream().toList());
                if ((contactDTO.getClientType() == SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()) || (contactDTO.getClientType() == SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())) {
                    // Remove ABDG groups from the list
                    List<Map.Entry<Integer, KnCorpGrpBasicInfoDTO>> abdgGroupsList = new ArrayList<>(groupList.stream().filter(entry -> entry.getValue().getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()).toList());
                    groupList.removeIf(entry -> entry.getValue().getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value());
                    // Remove TGSS groups from the list
                    List<Map.Entry<Integer, KnCorpGrpBasicInfoDTO>> tgssGroupList = new ArrayList<>(groupList.stream().toList().stream().filter(entry -> tgssGroups.contains(entry.getKey())).toList());
                    groupList.removeIf(entry -> tgssGroups.contains(entry.getKey()));
                    // Alphanumeric ordering
                    groupList.sort((e1, e2) -> e1.getValue().getGrpDisplayName().compareTo(e2.getValue().getGrpDisplayName()));
                    // Add TGSS groups to the list
                    groupList.addAll(0, tgssGroupList);
                    boolean IsLargeAgencyDispatch = KnGeneralUtil.getFeatureBitValue(contactDTO.getSubsActiveFS2(), FEATURE_SET.LARGE_AGENCY_DISPATCH.value());
                    if (!IsLargeAgencyDispatch) {
                        String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
                        KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                        int maxCorpGroupPerSubs = xdmsServiceConfigDTO.getMaxCorpGrpsPerSubs();
                        if (groupList.size() > maxCorpGroupPerSubs) {
                            groupList.subList(maxCorpGroupPerSubs, groupList.size()).clear();
                        }
                    }
                    // Add ABDG groups to the list
                    groupList.addAll(abdgGroupsList);
                }

                for (Map.Entry<Integer, KnCorpGrpBasicInfoDTO> entry : groupList) {
                    KnCorpDocInfoDTO groupDocument = new KnCorpDocInfoDTO();
                    int groupId = entry.getKey();
                    Boolean externalCorpGroup = groupInfoUtil.validateExtCorpGroup(userProfileId, Integer.parseInt(corpId), entry.getValue().getCorpId(), groupId, xdmsHomePttId, true, persisterTxn);
                    if (contactDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_23 && externalCorpGroup) {
                        groupDocument.setExternalCorpGroup(KnConstants.EXTERNAL_SUBSCRIBER);
                    }
                    groupDocument.setEtag(String.valueOf(entry.getValue().getGrpEtag()));
                    groupDocument.setGroupType(entry.getValue().getGrpType());
                    groupDocument.setGroupCreatedBy(entry.getValue().getGroupCreatedBy());
                    groupDocument.setDocName(String.valueOf(groupId));
                    groupDocument.setMcxGroupInd(entry.getValue().getMcxGroupInd());
                    groupDocument.setIsPreConfiguredGroup(entry.getValue().getIsPreConfiguredGroup());
                    groupDocument.setVideoPermission(entry.getValue().getVideoPermission());
                    knLogger.debug(methodName, "group value -", groupId, groupDocument);
                    groupDocumentList.add(groupDocument);
                }
                groupFolder.setEntries(groupDocumentList);
            }
            foldersList.add(groupFolder);

            knLogger.debug(methodName, "Before DB call to get the ScanList Documents details.");
            KnTalkGrpScanMode talkGrpScanListMode = groupInfoUtil.getSubsTalkGrpScanMode(mdn, xdmsHomePttId, persisterTxn);
            KnCorpFolderInfoDTO scanListFolder = new KnCorpFolderInfoDTO();
            scanListFolder.setAuid(APP_UID_PUB_TGSC);
            if (talkGrpScanListMode != null) {
                KnCorpDocInfoDTO scanListDocument = new KnCorpDocInfoDTO();
                scanListDocument.setEtag(String.valueOf(talkGrpScanListMode.getEtag()));
                scanListDocument.setDocName(XCAP_INDEX);
                List<KnCorpDocInfoDTO> scanListDocumentList = new ArrayList<KnCorpDocInfoDTO>();
                scanListDocumentList.add(scanListDocument);
                scanListFolder.setEntries(scanListDocumentList);
            }
            foldersList.add(scanListFolder);
            respDTO.setFolders(foldersList);
            respDTO.setXcapRoot(commonInfoUtil.getXcapUri(mdn, persisterTxn));
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the ",
                    "subscribers contact list - " + e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving the subscribers contact list - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT ");
        return respDTO;
    }

    public KnCorpInfoResDTO updateSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateSubscriber(KnIPCorpContactDTO, KnPersisterTxn)";
        //Step:
        //creating Response DTO object
        KnCorpInfoResDTO respDTO = new KnCorpInfoResDTO();
        Set<String> deletedDisGrpMemberList = new HashSet<String>();
        try {
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();

            String mdn = contactDTO.getMdn();
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
            int corpId = contactDTO.getCorpId();
            respDTO.setCorpId(corpId);
            /* No need to throw this exception during update subscriber call
           if (corpId <= 0) {
                knLogger.error( methodName, "Invalid Corporate Subscriber passed. MDN - " , mdn);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }*/
            contactDTO.setCorpId(corpId);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = subsProfile.getXdmsHome();
            knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            //Subscriber mdns where mdn belongs as a contact

            //get the groups where the subscriber is a member ireespective of the corporates -

            HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();
            if (contactDTO.getName() != null && !contactDTO.getName().equals(contactDTO.getOldName())) {
                // subscriber name is changed
                updateSubscriberName(contactDTO, subsProfile, persisterTxn, etagMap);
            }

            if (contactDTO.getName() == null) {
                contactDTO.setName(contactDTO.getOldName());
            }
            if (contactDTO.getSubscriptionType() != contactDTO.getNewSubscriptionType()) {

                // subscribtion type is changed
                KnCorpResponseDTO resp = updateSubscSubscriptionType(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                knLogger.debug(methodName, "etagMap after updateSubscSubscriptionType - ",KnGDPRTemplate.mapKeyMdn(etagMap));
                if (resp.getDisabledDispatchMemList() != null && !resp.getDisabledDispatchMemList().isEmpty()) {
                    deletedDisGrpMemberList.addAll(resp.getDisabledDispatchMemList());
                }
                if ((contactDTO.getNewSubscriptionType()) != KnConstants.SUBSCRIPTION_TYPE_PUBLIC) {
                    Map<String, KnTGSModeChgDTO> map = commonInfoUtil.consrtuctTgsModeChgMap(subsProfile);
                    respDTO.setTgsModeChgMap(map);
                }
            }
            if (contactDTO.getClientType() != contactDTO.getNewClientType()) {
                // Subscribers client type is changed.
                Collection<String> disGrpFleetMembers = updateSubscribersClientType(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                Set<String> tempSet = deletedDisGrpMemberList;
                deletedDisGrpMemberList = new HashSet<String>(disGrpFleetMembers);
                deletedDisGrpMemberList.addAll(tempSet);
                //deletedDisGrpMemberList.addAll(disGrpFleetMembers);
                knLogger.debug(methodName, "etagMap after client type change - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                if (((contactDTO.getNewClientType()) != KnConstants.CLIENT_TYPE_DISPATCH) || ((contactDTO.getNewClientType()) != KnConstants.INTER_OP_CLIENT_TYPE)) {
                    Map<String, KnTGSModeChgDTO> map = commonInfoUtil.consrtuctTgsModeChgMap(subsProfile);
                    respDTO.setTgsModeChgMap(map);
                }
            }

            // if (corpCleanUpReq) {
            if (contactDTO.getCorpId() != contactDTO.getNewCorpId()) {
                // Subscribers corporate id changed.
                KnCorpResponseDTO updateCorpIdResponseDTO = updateSubscribersCorpId(contactDTO, subsProfile, persisterTxn, corpInOutParamDTO, etagMap, groupNameMap);
                respDTO.setPairedContactListId(updateCorpIdResponseDTO.getPairedContactListId());
                // etagMap = updateCorpIdResponseDTO.getChangeLogMap();
                Collection<String> disGrpFleetMembers = updateCorpIdResponseDTO.getDisabledDispatchMemList();
                if (disGrpFleetMembers != null) {
                    // if (contactDTO.getClientType() == contactDTO.getNewClientType()) {
                    Set<String> tempSet = deletedDisGrpMemberList;
                    deletedDisGrpMemberList = new HashSet<String>(disGrpFleetMembers);
                    deletedDisGrpMemberList.addAll(tempSet);
                    //}
                }
                knLogger.debug(methodName, "etagMap after updateSubscribersCorpId - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                Map<String, KnTGSModeChgDTO> map = commonInfoUtil.consrtuctTgsModeChgMap(subsProfile);
                respDTO.setTgsModeChgMap(map);
            }

           /* if(contactDTO.getSubsActiveFS2()!=null && !contactDTO.getSubsActiveFS2().equals(subsProfile.getActiveFS2()) ){
                BitSet newMdnSubsFsBitSet = featureSetUtil.convertHexStringToBitSet(subsProfile.getActiveFS2());
                BitSet oldMdnSubsFsBitSet = featureSetUtil.convertHexStringToBitSet(contactDTO.getSubsActiveFS2());
                if ((oldMdnSubsFsBitSet.get(ABDG_GROUP_OWNER.value()) && !newMdnSubsFsBitSet.get(ABDG_GROUP_OWNER.value()))
                        || (oldMdnSubsFsBitSet.get(ABDG_GROUP_MEMBER.value()) && !newMdnSubsFsBitSet.get(ABDG_GROUP_MEMBER.value()))) {
                    KnCorpResponseDTO resp = deleteSubscribersGroupData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                    Collection<String> disGrpFleetMembers = resp.getDisabledDispatchMemList();
                    if (disGrpFleetMembers != null) {
                        Set<String> tempSet = deletedDisGrpMemberList;
                        deletedDisGrpMemberList = new HashSet<String>(disGrpFleetMembers);
                        deletedDisGrpMemberList.addAll(tempSet);
                    }
                    knLogger.debug(methodName, "etagMap after updateSubscribersCorpId - ", etagMap);
                    Map<String, KnTGSModeChgDTO> map = commonInfoUtil.consrtuctTgsModeChgMap(subsProfile);
                    respDTO.setTgsModeChgMap(map);
                }
            }
            
            */
            // }
            respDTO.setDisabledDispatchMemList(deletedDisGrpMemberList);
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, mdnList, persisterTxn, etagMap); // Move this to ASYNC
            //Adding the changing MDN for notification
            /*contactMDNs.add(mdn);
                if (contactDTO.getNewSubscriptionType() != KnConstants.SUBSCRIPTION_TYPE_PUBLIC) {
                    //Don't update resource list if new subscription type of MDN is public as public MDN don't have entry in corp resouce list table.
                contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, null, persisterTxn);
                }
                knLogger.info( methodName, "Updating Directory Etags");
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
*/
            knLogger.debug(methodName, "etagMap at exist - ",  KnGDPRTemplate.mapKeyMdn(etagMap));
            HashMap<String, KnOPDirChgDTO> liEtagMap = new HashMap<>(etagMap);
            knLogger.debug(methodName,"liEtagMap : ",liEtagMap);
            //liEtagMap.remove(mdn);
            knLogger.debug(methodName,"liEtagMap after mdn removal : ",liEtagMap);

            Set<String> mdns = etagMap.keySet();
            List<String> grpMdns = contactInfoUtil.getGroupMdns(new ArrayList<String>(mdns), corpId, xdmsHomePttId, persisterTxn);
            for (String mdnStr : grpMdns) {
                etagMap.remove(mdnStr);
            }
            KnOPDirChgDTO dir = etagMap.get(mdn);
            if(dir !=null) {
                knLogger.debug(methodName, "dir - ", dir);
                knLogger.debug(methodName, "document - ", dir.getDocChgDTO());
                if(dir.getDocChgDTO() == null){
                    dir.setDocChgDTO(new ArrayList<KnOPDocChgDTO>());
                }
            }
            respDTO.setChangeLogMap(etagMap);
            /* long currentCorpProfileEtag = contactInfoUtil.updateCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            respDTO.setEtag(String.valueOf(currentCorpProfileEtag));*/
            //populate Success response

            // Move this to ASYNC ###
            knLogger.debug(methodName, "Populatedata for LI  - ", KnGDPRTemplate.mapKeyMdn(liEtagMap));
            LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(liEtagMap, xdmsHomePttId, groupNameMap, null, null,persisterTxn);

            knLogger.debug(methodName, "liEventList - ", liEventList);
            respDTO.setLiEventList(liEventList);
            respDTO.setMdnCorpId(corpId);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while updating subscriber - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating subscriber - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);

        }
        knLogger.info(methodName, "EXIT-->");
        return respDTO;
    }

    public KnCorpInfoResDTO deleteSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "deleteSubscriber(KnIPCorpContactDTO, KnPersisterTxn)";
        //Step:
        //creating Response DTO object
        KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
        KnCorpInfoResDTO respDTO = new KnCorpInfoResDTO();
        Set<String> groupFleetMembers = new HashSet<String>();

        //Collection<String> contactMDNs;
        try {
            String mdn = contactDTO.getMdn().trim();
            knLogger.debug(methodName," Entry :",contactDTO);
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn); // not required
            String xdmsHomePttId = subsProfile.getXdmsHome();
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String locationFeatureFlagForLG = microServicesParamNameValueMap.get(LOCATION_FEATURE_FOR_LG_FLAG) != null ?
                    microServicesParamNameValueMap.get(LOCATION_FEATURE_FOR_LG_FLAG) : "0";
			if (contactDTO.getMcpttCompliance() == 1) {
				KnCorpProfileDTO corpProfileDetails = commonInfoUtil
						.getProfileDetails(String.valueOf(subsProfile.getCorpId()), CORP_PROFILE, false, persisterTxn);
                // xx
				Integer maxGroupsPerCRIClient = corpProfileDetails.getMaxGroupsPerSubsc();
				knLogger.debug(methodName, "value of max group per subscriber ", maxGroupsPerCRIClient);
				Integer gourpCount = groupInfoUtil.getSubsScrGroupCount(Arrays.asList(mdn), xdmsHomePttId, persisterTxn)
						.get(mdn); // mm
				if (gourpCount == null) {
					gourpCount = 0;
				}
				if (maxGroupsPerCRIClient != null) {
					knLogger.debug(methodName, "gourpCount  - ", gourpCount, "maxGroupsPerCRIClient  - ",
							maxGroupsPerCRIClient);
					if (gourpCount.compareTo(Integer.valueOf(maxGroupsPerCRIClient)) > 0) {
						throw new KnCorpBOException(KnErrorCodes.Validator.MAX_GROUPS_PER_CRI_CLIENT_EXCEED,
								"max groups per CRI client exceed.");
					}
				}
			}
            int corpId = subsProfile.getCorpId();
            KnCorpProfileDTO corpProfile = new KnCorpProfileDTO();
            respDTO.setCorpId(corpId);
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();

            boolean upmCall = contactDTO.isUpmCall();
            knLogger.debug(methodName," upmCall :",upmCall);


            LinkedList<String> mdnList = new LinkedList<String>();
            mdnList.add(mdn);
            //need this for LI
            Map<String, Collection<String>> mdnContactMap = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName," mdnContactMap :",mdnContactMap);

            groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);// yy
            // Addl Talk Group Doc
            etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);// xx
            Collection<KnCorpAddlTGInfoDTO> subsMdnAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "subsAddlTGList - ", subsMdnAddlTGList);
            if (subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()) {
                groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);// yy
                knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                Map<Integer, Integer> groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsMdnAddlTGList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                    etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, null,
                            subsMdnAddlTGList, groupCorpIdMap);// xx
                    knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                }
            }
            if (corpId <= 0) { // notes move this entire block to async path
                // notes delete the ext table data
                //Get corpId where MDN exist as ext contact
                Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
                if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                    knLogger.debug(methodName, "Subscriber passed if of public subscription type MDN - ", KnGDPRTemplate.mdn(mdn));
                    etagMap = deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                    knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                    respDTO.setChangeLogMap(etagMap);

                }
                return respDTO;
            }
            contactDTO.setCorpId(corpId);
            //use the xdms home pttServerId from Corp profile

            int privateContactListId = subsProfile.getContactListId();
            knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);
            //determine the no of members for the corporate
            int count = contactInfoUtil.getCorpSubscriberCount(corpId, xdmsHomePttId, persisterTxn);
            corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, true, persisterTxn);
            int corpProfileCleanUp = corpProfile.getMaxPredefinedTmpltCnt();
            int deviceCount = genInfoUtil.getDeviceCountForCorpId(corpId, persisterTxn);
            int deviceCountMdn = genInfoUtil.getDeviceCountByMdnAndCorpId(mdn, corpId, persisterTxn);
            knLogger.info(methodName, "corpProfileCleanUp flag- ", corpProfileCleanUp, " deviceCount - ", deviceCount, " deviceCountMdn - ", deviceCountMdn, " subscriber count - ", count);
            if (count <= 1 && corpProfileCleanUp != 1 &&
                    (deviceCount == 0 || (deviceCount == 1 && deviceCountMdn == 1))) {
                // since cleanUpCorporateProfile() methos is cleaning its corp data.
                // Before cleaning corp data, delete external data from other corp.
                deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                deleteMcpttDoc(mdn, xdmsHomePttId, etagMap, persisterTxn);
                respDTO = cleanUpCorporateProfile(contactDTO, contactDTO.getCorpId(), corpInOutParamDTO, persisterTxn, etagMap);
                respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
                return respDTO;
            }
            //1. Delete this subscriber from contact list of all the subscribers to whom he belongs to
            //2. Delete this subscriber from all groups he belongs to
            //3. Delete association from all the Sublists pushed to him
            //4. Delete this subscriber from all the Sublists where he is a member
            //5. Delete his Private ConatctList Sublist

            // get list of MDNs where he is present as contact.
            // Collection<String> contactMDNs = contactInfoUtil.getContactMDNs(mdn, xdmsHomePttId, persisterTxn);
            //Get list of sublist ids distributed to this subscriber.
            // sending corpListId = 0 , for retrieving the private list id also.
            Collection<KnCorpSublistDTO> listOfDistSublistIds = new ArrayList();
            if(upmCall) {
                String userProfileId = subsProfile.getUserProfileId();
                KnIPCorpContactDTO contactObj = new KnIPCorpContactDTO();
                ArrayList<String> ids = new ArrayList<String>();
                ids.add(subsProfile.getUserProfileId());
                Map<String,Integer> onwerInfo = corpUserProfileUtil.getUserProfileOwnerinfo(ids,xdmsHomePttId,persisterTxn);
                if(onwerInfo!=null && !onwerInfo.isEmpty() && onwerInfo.get(userProfileId) != subsProfile.getCorpId()){
                    //upm sharing enabled and is shared
                    contactObj.setMdn(mdn);
                    contactObj.setCorpId(onwerInfo.get(userProfileId));
                    listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactObj,
                            0, xdmsHomePttId, persisterTxn);
                }else{
                    //upm is not shared or upm is shared and is the owner
                    listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactDTO,
                            0, xdmsHomePttId, persisterTxn);
                }

            }else {
                 listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactDTO,
                        0, xdmsHomePttId, persisterTxn);
            }
            if (listOfDistSublistIds != null && !listOfDistSublistIds.isEmpty()) {
                Collection<Integer> removeListIds = new ArrayList<Integer>();
                for (KnCorpSublistDTO corpSubDto : listOfDistSublistIds) {
                    removeListIds.add(corpSubDto.getSublistId());
                }
                KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
                corpSubsDto.setMdn(mdn);
                contactInfoUtil.removeSublistMappingForSubscriber(removeListIds, corpSubsDto,
                        xdmsHomePttId, persisterTxn);
            }

            // UCSPROVCONFIG-8605: Delete ALL CORPLISTDISTINFO entries for this MDN (including cross-corp references)
            // This ensures no FK violation when the subscriber profile is deleted from POCSUBSCRINFO
            try {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
                List<String> mdnListForCleanup = new ArrayList<>();
                mdnListForCleanup.add(mdn);
                corpXdmDao.deleteCorpListDistribution(new ArrayList<Integer>(), mdnListForCleanup, persisterTxn);
                knLogger.debug(methodName, "Deleted all CORPLISTDISTINFO entries for MDN - ", KnGDPRTemplate.mdn(mdn));
            } catch (KnDAOException e) {
                knLogger.error(methodName, "Failed to delete CORPLISTDISTINFO entries for MDN - ", KnGDPRTemplate.mdn(mdn), e);
                throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }

            Map<String, Collection<String>> dispContactList = null;
            if (subsProfile.getClientType() == DISPATCH_CLIENT.value()
                    || subsProfile.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                dispContactList = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);
            }
            contactInfoUtil.deleteSubscribersContactList(mdn, xdmsHomePttId, persisterTxn);
            contactInfoUtil.updateSubscribersContactCount(mdnList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            // get all sublist ids where this MDN is member in all corporation.
            Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.
                    getSubcriberSublistMemberShipListForAllCorporate(mdnList, xdmsHomePttId, persisterTxn);

            Collection<Integer> sublistList = sublistMemberMap.keySet();
            Collection<String> contactMDNs = new ArrayList<String>();
            Map<Integer, Integer> subListForProfileUpdate = new HashMap<>();
            if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                Collection<Integer> sublistIdList = sublistMemberMap.keySet();
                sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
                sublistInfoUtil.fetchAndUpdateSublistEtag(sublistIdList,
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Calling sublist List Distribution list - ", sublistMemberMap);
                Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                        sublistList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
                for (int sublistId : sublistList) {
                    Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                    if (contactList != null && !contactList.isEmpty()) {
                        for (KnCorpSubscriberDTO contact : contactList) {
                            if (!mdnList.contains(contact.getMdn())) {
                                contactMDNs.add(contact.getMdn());
                            }
                        }
                    }
                }
                knLogger.info(methodName, "ContactList - ", KnGDPRTemplate.mdnList(contactMDNs).size());
            }

            // delete the privatecontactlist id
            if (privateContactListId > 0) {
                sublistInfoUtil.deleteSublist(privateContactListId, corpId, xdmsHomePttId, persisterTxn);
            }

            LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
            LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
            if (!contactMDNs.isEmpty()) {
                //deleting members from the contact list table
                for (String subscMdn : contactMDNs) {
                    removeMdnListMap.put(subscMdn, mdnList);
                }
                delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                knLogger.info(methodName, "Updating ResourceList Etags");
                etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, etagMap, persisterTxn);
            }
            knLogger.debug(methodName, "subsProfile.getClientType() - ", subsProfile.getClientType());
            LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
            LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
            Map<Integer, Collection<String>> groupDistributionList = new HashMap<Integer, Collection<String>>();

            Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
            if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
                if (corpIdList != null && !corpIdList.isEmpty()) {
                    // update the corporate etag.
                    knLogger.debug(methodName, "corpIdList - ", corpIdList);
                    contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
                }
            }

            //get all group ids where this mdn is member in all corporation.
            Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName,"groupMemberMap :",groupMemberMap);
            Map<Integer, String> dispGrpNameMap = groupInfoUtil.getSupervisorGroupsAndName(mdn, KnConstants.GROUP_DISPATCHER, xdmsHomePttId, persisterTxn);
            List<Integer> dispatcherGrpList = new ArrayList<>(dispGrpNameMap.keySet());

          /*  //check if any other dispatcher exists for the group.
            //get the list of dispatchers leaving this mdn if no more exists then add to the deleted group list
            Set<Integer> nonDelDispGrp =  groupInfoUtil.getGrpDispMemList(dispatcherGrpList, mdn, xdmsHomePttId, persisterTxn);
            // nonDelDispGrp should contain the group id against the list of otherDispatchers
            dispatcherGrpList.removeAll(nonDelDispGrp);*/

            Collection<String> disabledOdlMember = new HashSet<>();
            Map<Integer, Integer> groupMapListForProfileUpdate = new HashMap<>();
            if (!groupMemberMap.keySet().isEmpty()) {
                Collection<Integer> allGroupIdList = groupMemberMap.keySet();
                //Variable to hold all group ids other than dispatch group.
                List<Integer> otherGrpIdList = new ArrayList<>(allGroupIdList);
                // otherGrpIdList.removeAll(dispatcherGrpList);
                //delete members from the corpgroupmemberlist table

                Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(allGroupIdList, xdmsHomePttId, persisterTxn);
                Map<Integer, Collection<KnCorpAddlTGInfoDTO>> existingAddlTGList = new HashMap<>();
                Collection<KnCorpAddlTGInfoDTO> delAddlTGList = new ArrayList<>();
                // Additional Talk Group:
                for (KnCorpAddlTGInfoDTO addlTg : subsAddlTGList) {
                    Collection<KnCorpAddlTGInfoDTO> existAddl = null;
                    if (existingAddlTGList.get(addlTg.getGroupId()) != null) {
                        existAddl = existingAddlTGList.get(addlTg.getGroupId());
                        existAddl.add(addlTg);
                    } else {
                        existAddl = new ArrayList<>();
                        existAddl.add(addlTg);
                        existingAddlTGList.put(addlTg.getGroupId(), existAddl);
                    }
                }
                /*
                    Get the group member count and group type for the groupIds and set these values into etag Map
                 */
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(allGroupIdList, xdmsHomePttId, persisterTxn);

                Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(allGroupIdList, xdmsHomePttId, persisterTxn);
                for (KnCorpGroupMemberDTO grpMem : grpMemberDetails) {
                    disabledOdlMember.add(grpMem.getMdn());
                }

                for (int grpId : otherGrpIdList) {
                    removeGrpMdnListMap.put(grpId, mdnList);
                    groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.ETAG_CHANGE.value());
                }

                Collection<Integer> groupInfoUpdate  = new ArrayList<>();
                if(subsProfile.getClientType() == SUBSCR_CLIENT_TYPE.GROUPMDN.value() || subsProfile.getClientType() == SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()){
                    Map<Integer, Integer> sgMDNCountDetailsMap = groupInfoUtil.getSGCorpGroupMembersCount(allGroupIdList, xdmsHomePttId, persisterTxn);
                    if(sgMDNCountDetailsMap != null) {
                        sgMDNCountDetailsMap.forEach((corpGroupId, SGMemberCount) -> {
                            if (SGMemberCount == 1) {
                                groupInfoUpdate.add(corpGroupId);
                            }
                        });
                    }
                    groupInfoUtil.modifyGroupLmrInteropCapable(LMR_INTEROP_NON_CAPABLE, groupInfoUpdate, xdmsHomePttId, persisterTxn);
                }

                delGroupMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
                //Update the group member count
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistMemberMap.keySet(), MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);

                //Update IS_LARGE group flag from 1 to 0 in case of Delete Subscriber procedure
                // when group member count becomes less than or equal to maximum normal talk group count.
                Map<Integer, Integer> currentGrpMemCount = groupInfoUtil.getGroupMemCount(allGroupIdList, xdmsHomePttId, persisterTxn);
                Map<Integer, Integer> isLargeGrpDisable = new HashMap<>();
                for (Map.Entry<Integer,Integer> entry : currentGrpMemCount.entrySet()) {
                    int memCount = entry.getValue();
                    if ((groupDetailsMap.get(entry.getKey()).getGroupType()) == KnConstants.STANDARD_GROUP &&
                            memCount <= corpProfile.getMaxMemPerCorpGroup() && groupDetailsMap.get(entry.getKey()).isLargeGroup()) {
                        isLargeGrpDisable.put(entry.getKey(), 0);
                    }else if ((groupDetailsMap.get(entry.getKey()).getGroupType()) == KnConstants.DISPATCH_GROUP &&
                            memCount <= corpProfile.getMaxMembersPerDispatchGroup() && groupDetailsMap.get(entry.getKey()).isLargeGroup()) {
                        isLargeGrpDisable.put(entry.getKey(), 0);
                    }
                }
                if (!isLargeGrpDisable.isEmpty()) {
                    groupInfoUtil.updateIsLargeGrpFlag(isLargeGrpDisable, xdmsHomePttId, persisterTxn);
                }

                //Filter out the actually added members and deleted members as same member can be already present in the group via sublist etc
                //DAO calls to delete from the dg.corpgroupdistinfo table
                Map<Integer, Map<String, Collection<String>>> groupMemDelMap = new HashMap<Integer, Map<String, Collection<String>>>();
                for (int grpId : otherGrpIdList) {
                    Map<String, Collection<String>> deletedMemberMap = new HashMap<String, Collection<String>>();
                    deletedMemberMap.put(KnConstants.DELTED_MEMBERS, removeGrpMdnListMap.get(grpId));
                    groupMemDelMap.put(grpId, deletedMemberMap);
                }
                //DAO calls to delete from the dg.corpgroupdistinfo table
                groupInfoUtil.deleteFrmCorpGroupDistInfo(groupMemDelMap, xdmsHomePttId, persisterTxn);
                //Retrieving the current group members i.e internal members from dg.corpgroupdistinfo
                //updating groupId etag
                knLogger.info(methodName, "Updating Group Etags");
                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(otherGrpIdList, xdmsHomePttId, persisterTxn);
                //Determine the deleted and the modified groups
                Map<String, HashMap<Integer, String>> groupListStatus =
                        groupInfoUtil.getGroupListStatus(otherGrpIdList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName,"groupListStatus :",groupListStatus);
                //retrieval of members
                groupDistributionList = groupInfoUtil.getGroupSubscriberDistList(allGroupIdList, xdmsHomePttId, persisterTxn);
                Collection<Integer> ownerGroupIds = groupInfoUtil.getOwnerGroupIds(mdn, corpId, xdmsHomePttId, persisterTxn);
                knLogger.info(methodName, "ownerGroupIds: ", ownerGroupIds);
                // Delete corp groups with < 2 members
                Set<Integer> delGroupIdList = new HashSet<>();
                if (groupListStatus.get(KnConstants.DELETED) != null || !ownerGroupIds.isEmpty()) {
                    delGroupIdList = new HashSet<>();
                    if (groupListStatus.get(KnConstants.DELETED) != null) {
                        delGroupIdList.addAll(groupListStatus.get(KnConstants.DELETED).keySet());
                    }
                    delGroupIdList.addAll(ownerGroupIds);
                    //delGroupIdList.addAll(dispatcherGrpList);
                    if (!delGroupIdList.isEmpty()) {
                        //Addint the dispatch group fleet members
                        for (int dispGrpId : dispatcherGrpList) {
                            if (null != groupDistributionList.get(dispGrpId)) {
                                groupFleetMembers.addAll(groupDistributionList.get(dispGrpId));
                            }
                        }
                        Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
                        for (Integer grpId : delGroupIdList) {
                            Collection<String> delMembersList = groupDistributionList.get(grpId);
                            deletedMembersMap.put(grpId, delMembersList);
                            //for the contact added to update the directory
                            contactMDNs.addAll(delMembersList);
                            removeGrpMdnListMap.remove(grpId);
                            delGroupMemStatus.remove(grpId);
                            if(existingAddlTGList.get(grpId) != null) delAddlTGList.addAll(existingAddlTGList.get(grpId));
                            groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
                        }
                        if (groupListStatus.get(KnConstants.DELETED) != null) {
                            groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                        }
                        groupNameMap.putAll(dispGrpNameMap);
                        etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                    }
                }

                // Additional Talk Group:
                if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                    Collection<String> mdnForAddlTg = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
                    groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmsHomePttId, persisterTxn);
                    subsAddlTGList.removeAll(delAddlTGList);
                    subsAddlTGList.forEach(subsAddl -> {
                        if(groupDetailsMap.get(subsAddl.getGroupId()) != null) subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                    });
                    etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnForAddlTg, persisterTxn, etagMap,upmCall, true);
                    knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                    Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                    if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                        groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                    }
                    knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                    if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                        etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, subsAddlTGList, delAddlTGList, groupCorpIdMap);
                        knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                    }
                }
                //empty group support:if last subscriber is deleted then group will not be delted
                //groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                groupInfoUtil.emptyGroup(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);


                if(contactDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY && groupListStatus.get(KnConstants.DELETED) != null){
                        List<Integer> groupIdsToBeDeleted = new CopyOnWriteArrayList<>();
                        groupIdsToBeDeleted.addAll(groupListStatus.get(KnConstants.DELETED).keySet());
                        knLogger.debug(methodName,"Empty groups ",groupIdsToBeDeleted);
                        for (Integer groupId : groupIdsToBeDeleted) {
                            KnCorpGroupDTO groupDetailsMaps =groupDetailsMap.get(groupId);
                            if(groupDetailsMaps.getMcxGrpInd() == 1){
                                groupIdsToBeDeleted.remove(groupId);
                            }
                        }
                        if(!groupIdsToBeDeleted.isEmpty()) {
                            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
                            corpXdmDao.deleteAllGrpHierarchy(groupIdsToBeDeleted,persisterTxn);
                            groupInfoUtil.deleteAllGroups(groupIdsToBeDeleted, xdmsHomePttId, new KnCorpInOutParamDTO(), persisterTxn);
                        }
                }


                etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);

                if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                    Set<Integer> modGroupIdList = groupListStatus.get(KnConstants.MODIFIED).keySet();
                    if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                        Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                        for (Integer grpId : modGroupIdList) {
                            Collection<String> modMembersList = groupDistributionList.get(grpId);
                            modifiedMembersMap.put(grpId, modMembersList);
                            //for the contact added to update the directory
                            contactMDNs.addAll(modMembersList);
                        }
                        etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                                DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                        groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                    }
                }


                boolean anyDispatcherGroup = false;
                for (Map.Entry<Integer, KnCorpGroupDTO> itr : groupDetailsMap.entrySet()) {
                    if (itr.getValue().getGroupType() == DISPATCH_GROUP) {
                        anyDispatcherGroup = true;
                        break;
                    }
                }

                if (Objects.equals(Integer.parseInt(locationFeatureFlagForLG), DISABLE_LOCATION_FEATURE_FOR_LG_FLAG) && upmCall && anyDispatcherGroup) {
                    Map<Integer, Collection<String>> currentGroupMember = groupInfoUtil.selectGroupMemberListForGroupIds(allGroupIdList, xdmsHomePttId, persisterTxn); // current group member list entries
                    disabledOdlMember.clear();
                    if (null != respDTO.getEnabledDispatchMemList()) {
                        respDTO.getEnabledDispatchMemList().clear();
                    }
                    if (null != respDTO.getDisabledDispatchMemList()) {
                        respDTO.getDisabledDispatchMemList().clear();
                    }
                    for (Integer grpId : allGroupIdList) {
                        int newFlagLargeGroup = isLargeGrpDisable.get(grpId) !=null ? isLargeGrpDisable.get(grpId) : 0;
                        KnCorpGroupDTO grpBasicInfo = groupDetailsMap.get(grpId);
                        boolean oldFlagLargeGroup = grpBasicInfo.isLargeGroup();
                        int grpType = grpBasicInfo.getGroupType();
                        knLogger.info("grpBasicInfo", grpBasicInfo, "oldFlagLargeGroup", oldFlagLargeGroup,
                                "newFlagLargeGroup", newFlagLargeGroup, "grpType", grpType);

                        if (grpType == DISPATCH_GROUP) {
                            if (!oldFlagLargeGroup && newFlagLargeGroup == 1) {
                                // normal to large dispatch
                                if (null != respDTO.getDisabledDispatchMemList()) {
                                    respDTO.getDisabledDispatchMemList().addAll(currentGroupMember.get(grpId));
                                } else {
                                    respDTO.setDisabledDispatchMemList(currentGroupMember.get(grpId));
                                }
                                knLogger.debug("normal to large dispatch");
                            } else if (oldFlagLargeGroup && newFlagLargeGroup == 0) {
                                // large to normal dispatch
                                if (null != respDTO.getEnabledDispatchMemList()) {
                                    respDTO.getEnabledDispatchMemList().addAll(currentGroupMember.get(grpId));
                                } else {
                                    respDTO.setEnabledDispatchMemList(currentGroupMember.get(grpId));
                                }
                                knLogger.debug("large to normal dispatch");
                            } else {
                                knLogger.info("else dipatch");
                            }
                        }
                    }
                }
            }
            Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
            if (sharedSublistSet != null && !sharedSublistSet.isEmpty()) {
                ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);
                ArrayList<Integer> nonSharedSublists = sublistInfoUtil.getNonSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);
                ArrayList<Integer> subList= new ArrayList<Integer>();
                subList.addAll(sharedSublists);
                subList.addAll(nonSharedSublists);
                ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(subList, xdmsHomePttId, persisterTxn);
                if (emptySublist != null && !emptySublist.isEmpty()) {
                    sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
                    if (emptySublist.contains(corpProfile.getPairedContactListId())) {
                        //set the corporate profile paried linked id to  null
                        sublistInfoUtil.updateCorpPairedContListId(corpId, 0, corpProfile.getXdmsHome(), persisterTxn);
                    }
                }
                for(Integer sId : sharedSublistSet){
                    int val = KnConstants.CB_MAPPING.ETAG_CHANGE.value();
                    if(emptySublist != null && !emptySublist.isEmpty() && emptySublist.contains(sId)){
                        val = KnConstants.CB_MAPPING.NEED_TO_REMOVE.value();
                    }
                    subListForProfileUpdate.put(sId, val);
                }
            }
            //profile notification
            List<String> profileMdnList=new ArrayList<>();
            profileMdnList.add(mdn);
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                    null
                    , String.valueOf(corpId),profileMdnList
                    ,"0", xdmsHomePttId, persisterTxn);
             respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }

            if (!contactMDNs.isEmpty()) {
                knLogger.debug(methodName, "Updating Directory Etags");
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId,upmCall, persisterTxn);
                contactInfoUtil.updateSubscribersContactCount(mdnList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            }
            //Delete the MDN from External contact table.
            if (!corpIdExtContactNameMap.isEmpty()) {
                contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
            }


            if (dispContactList != null && dispContactList.get(mdn) != null) {
                Collection<String> overAllList = new ArrayList<String>();
                Collection<String> updateDispMemList = respDTO.getDisabledDispatchMemList();
                if (updateDispMemList != null) {
                    overAllList.addAll(updateDispMemList);
                }
                overAllList.addAll(dispContactList.get(mdn));
                knLogger.info("overAllList size", overAllList.size());
                respDTO.setDisabledDispatchMemList(overAllList);
            }

            if(respDTO.getDisabledDispatchMemList() != null){
                disabledOdlMember.addAll(respDTO.getDisabledDispatchMemList());
            }
            respDTO.setDisabledDispatchMemList(disabledOdlMember);

            //To Disabled 27bit when there is no loc wathcer  is present in the UPM , below changes are applicable only for MCX group type
            String inputMdn = contactDTO.getMdn().trim();
            Set<Integer> groupIds = commonInfoUtil.getGroupIdBasedOnMdn(inputMdn, persisterTxn, xdmsHomePttId);
            LinkedHashMap<Integer, LinkedList<String>> groupDiaptacherMap = groupInfoUtil.getGroupDispatcherSubscriber(new ArrayList<>(groupIds), DISPATCHER, xdmsHomePttId, persisterTxn);
            int mdnCount = 0;
            Set<String> profileMdnSet = new HashSet<>();
            if (null != groupIds && !groupIds.isEmpty() && groupIds.size() != 0) {
                mdnCount = commonInfoUtil.getMdnCountBasedOnUPMID(new ArrayList<>(groupIds), persisterTxn, xdmsHomePttId);
                profileMdnSet = commonInfoUtil.getProfileMdns(groupIds, persisterTxn, xdmsHomePttId);
            }
            if (mdnCount == 1 && null != profileMdnSet && !profileMdnSet.isEmpty() && profileMdnSet.size() != 0
                    && (null == groupDiaptacherMap || groupDiaptacherMap.isEmpty() || groupDiaptacherMap.size() == 0)) {
                respDTO.setDisabledDispatchMemList(profileMdnSet);
                knLogger.info("overAllList 2 respDTO.getDisabledDispatchMemList()", respDTO.getDisabledDispatchMemList());
            }

            int max_etag_update_notification = genInfoUtil.getXdmMaxNotificationCount();
            //Delete MCPTT related document.
            etagMap = deleteMcpttDoc(mdn, xdmsHomePttId, etagMap, persisterTxn);
            // Delete Subscriber case no need to send the SEH notification,
            // hence return type of the cleanUpSubsCampedGrps is irrelevant
            groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
            // update GROUP_OWNER column of DG.CORPGROUPINFO to null whereever this mdn is as group owner
            //.. introduced in dynamic contacts and group feature
            this.groupInfoUtil.updateGroupOwner(mdn, null, corpId, xdmsHomePttId, persisterTxn);
            activationInfoUtil.deleteActivationCodeForMDN(mdn, xdmsHomePttId, persisterTxn);
            etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
            if (etagMap.size() <= max_etag_update_notification) {
                etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap,
                        null, null, removeGrpMdnListMap, null, null,
                        groupDistributionList, delContactStatus, delGroupMemStatus, null);
            }
            knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
            HashMap<String, KnOPDirChgDTO> liEtagMap = new HashMap<>(etagMap);
            knLogger.debug(methodName,"liEtagMap - ",liEtagMap);
            etagMap.remove(mdn);
            LinkedList<KnLIEventDTO> liEventList =new LinkedList<>();
            if(!upmCall){
                liEventList = KnCorpCommonInfoUtil.populateLIData(liEtagMap, xdmsHomePttId, groupNameMap,
                        null, null,persisterTxn);
            }else{
                //contact
                if(!mdnContactMap.isEmpty()){
                    LinkedList<KnLIEventDTO> contactLiEvent = KnCorpCommonInfoUtil.upmContactPopulateLIData(mdn,mdnContactMap.get(mdn), xdmsHomePttId, persisterTxn);
                    if(!contactLiEvent.isEmpty()){
                        liEventList.addAll(contactLiEvent);
                    }
                }
                //group
                LinkedList<KnLIEventDTO> groupLiEvent = KnCorpCommonInfoUtil.upmUnassignUserProfilePopulateLIData(liEtagMap
                        , xdmsHomePttId, groupNameMap,mdn, persisterTxn);
                if(!groupLiEvent.isEmpty()){
                    liEventList.addAll(groupLiEvent);
                }
            }

            knLogger.debug(methodName," liEventList :",liEventList);
            respDTO.setLiEventList(liEventList);
            Set<String> mdns = etagMap.keySet();
            List<String> grpMdns = contactInfoUtil.getGroupMdns(new ArrayList<String>(mdns), corpId, xdmsHomePttId, persisterTxn);
            for (String mdnStr : grpMdns) {
                etagMap.remove(mdnStr);
            }
            knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            respDTO.setChangeLogMap(etagMap);
            respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
            respDTO.setMdnCorpId(corpId);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while deleting subscriber (corp association) - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while deleting subscriber (corp association) - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);

        }
        knLogger.info(methodName, "EXIT : Returning Response - ");
        return respDTO;
    }

    public KnCorpResponseDTO changeMdn(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "changeMdn(contactDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : - ", contactDTO);
        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        //Collection<String> contactMDNs;
        try {
            String mdn = contactDTO.getMdn();
            String newMDN = contactDTO.getNewMdn();

            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);

            int corpId = subsProfile.getCorpId();
            //Below check is not required as if corpid=0, in case of public subscribers
            // need to change the ext contact details.
            /* if (corpId <= 0) {
                knLogger.error( methodName, "Invalid Corporate Subscriber passed. MDN - " , mdn);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }*/
            contactDTO.setCorpId(corpId);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = subsProfile.getXdmsHome();
            knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);
			if (contactDTO.getMcpttCompliance() == 1) {
				KnCorpProfileDTO corpProfileDetails = commonInfoUtil
						.getProfileDetails(String.valueOf(subsProfile.getCorpId()), CORP_PROFILE, false, persisterTxn);
				Integer maxGroupsPerCRIClient = corpProfileDetails.getMaxGroupsPerSubsc();
				knLogger.debug(methodName, "value of max group per subscriber ", maxGroupsPerCRIClient);
				Integer gourpCount = groupInfoUtil.getSubsScrGroupCount(Arrays.asList(mdn), xdmsHomePttId, persisterTxn)
						.get(mdn);
				if (gourpCount == null) {
					gourpCount = 0;
				}
				if (maxGroupsPerCRIClient != null) {
					knLogger.debug(methodName, "gourpCount  - ", gourpCount, "maxGroupsPerCRIClient  - ",
							maxGroupsPerCRIClient);
					if (gourpCount.compareTo(Integer.valueOf(maxGroupsPerCRIClient)) > 0) {
						throw new KnCorpBOException(KnErrorCodes.Validator.MAX_GROUPS_PER_CRI_CLIENT_EXCEED,
								"max groups per CRI client exceed.");
					}
				}
			}
            //Steps
            //1. Update this subscriber in contact list of all the subscribers where he belongs to
            //2. Update this subscriber in all the groups he belongs to
            //3. Update association for all the Sublists pushed to him
            //4. Update this subscriber in all the Sublists where he is a member
            //5. Update his mdn in Private ConatctList

            // get list of MDNs where he is present as contact.

            Collection<String> requestMdnList = new ArrayList<String>();
            requestMdnList.add(newMDN);
            knLogger.info(methodName, "requestMdnList - ", KnGDPRTemplate.mdnList(requestMdnList));
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            //Additional Talk Group:
            Map<String, Long> subsEtagMap = groupInfoUtil.getSubsAddlEtagMap(mdnList, xdmsHomePttId, persisterTxn);
            Map<String, Long> newSubsEtagMap = new HashMap<>();
            newSubsEtagMap.put(newMDN, subsEtagMap.get(mdn));
            if(subsEtagMap.get(mdn) != null) {
                groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);
                groupInfoUtil.insertSubsAddlTGDoc(newSubsEtagMap, xdmsHomePttId, persisterTxn);
            }
            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
            if(subsAddlTGList != null && !subsAddlTGList.isEmpty()){
                groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);
                subsAddlTGList.forEach(addlTGInfoDTO -> {
                    addlTGInfoDTO.setMdn(newMDN);
                });
                //etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);
                groupInfoUtil.insertSubsAddlTGList(subsAddlTGList, xdmsHomePttId, persisterTxn);
            }
            Collection<String> newMdnLists = new ArrayList<String>();
            newMdnLists.add(newMDN);
            etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, newMdnLists, persisterTxn, etagMap);
            //Update the private list name for the MDn as the Name has to be PrivateListNEWMDNNO
            int privateListId = subsProfile.getContactListId();
            if (privateListId > 0) {
                String privateListName = (KnConstants.PRIVATE_LIST_NAME).concat(newMDN);
                sublistInfoUtil.modifySublistName(privateListName, privateListId, xdmsHomePttId, persisterTxn);
            }
            //get all sublist mapped to him and remove the association and push to new mdn
            Collection<KnCorpSublistDTO> listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactDTO,
                    0, xdmsHomePttId, persisterTxn);
            if (listOfDistSublistIds != null && !listOfDistSublistIds.isEmpty()) {
                Collection<Integer> updateListIds = new ArrayList<Integer>();
                for (KnCorpSublistDTO corpSubDto : listOfDistSublistIds) {
                    updateListIds.add(corpSubDto.getSublistId());
                }
                knLogger.info(methodName, "updateListIds - ", updateListIds);
                KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
                corpSubsDto.setMdn(mdn);
                contactInfoUtil.removeSublistMappingForSubscriber(updateListIds, corpSubsDto,
                        xdmsHomePttId, persisterTxn);
                KnIPCorpSublistSubscDistDTO distDTO = new KnIPCorpSublistSubscDistDTO();
                distDTO.setSublistIds(updateListIds);
                knLogger.info(methodName, "newMdnLists - ", KnGDPRTemplate.mdnList(newMdnLists));
                distDTO.setMdnList(newMdnLists);
                contactInfoUtil.pushSublistListToSubscriberList(distDTO, xdmsHomePttId, persisterTxn);
            }


            // get all sublist ids where this MDN is member across the corporate.
            Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.getSubcriberSublistMemberShipListForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "sublistMemberMap - ", sublistMemberMap);

            // Get all group ids where this MDN is member across the corporate.
            Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "groupMemberMap - ", groupMemberMap);

            Collection<Integer> sublistList = sublistMemberMap.keySet();
            Collection<String> contactMemberList = new ArrayList<String>();
            Collection<KnCorpSubscriberDTO> contactMdnList = new ArrayList<KnCorpSubscriberDTO>();  // this variable holds all the MDNs having old MDN as contact
            if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                //Deleting old MDN from the sublist and adding new MDN to it.
                sublistInfoUtil.changeMDNImpactInSublist(sublistMemberMap, newMDN, corpId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Calling sublist List Distribution list - ", sublistMemberMap);
                //Get all the MDNs to whom these sublist are distributed
                Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                        sublistList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
                for (int sublistId : sublistList) {
                    Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                    if (contactList != null && !contactList.isEmpty()) {
                        for (KnCorpSubscriberDTO contact : contactList) {
                            if (!mdnList.contains(contact.getMdn())) {
                                contactMemberList.add(contact.getMdn());
                                contactMdnList.add(contact);
                            }
                        }
                    }
                }
            }

            //update the owner mdn in the contact list table
            contactInfoUtil.updateOwnerMdnInContactList(contactDTO, xdmsHomePttId, persisterTxn);

            //remove the old mdn member as its directory and the resource list entries will not be found in DB
            /*  if (contactMemberList.contains(mdn)) {
                contactMemberList.remove(mdn);
                contactMemberList.add(newMDN);
            }*/

            //Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();

            LinkedList<String> removeMdnList = new LinkedList<String>();
            removeMdnList.add(mdn);

            LinkedHashMap<String, LinkedList<Integer>> delContMemStatus = new LinkedHashMap<String, LinkedList<Integer>>();
            LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
            LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
            LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();

            int max_etag_update_notification = genInfoUtil.getXdmMaxNotificationCount();
            if (contactMemberList != null && !contactMemberList.isEmpty()) {
                int extCorpId = 0;
                ///add and remove members from the corpcontactlist table
                //remove mdns
                for (String contactMdn : contactMemberList) {
                    removeMdnListMap.put(contactMdn, removeMdnList);
                }
                delContMemStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                //add members
                Map<String, Collection<String>> subsContactMap =
                        contactInfoUtil.getSubscribersContactList(contactMemberList, xdmsHomePttId, persisterTxn);
                Map<String, Collection<KnCorpSubscriberDTO>> addedMdnListMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                for (KnCorpSubscriberDTO subscriberDTO : contactMdnList) {
                    ArrayList<KnCorpSubscriberDTO> newMdnList = new ArrayList<KnCorpSubscriberDTO>();
                    KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                    subsc.setMdn(newMDN);
                    subsc.setName(subsProfile.getNetworkName());
                    subsc.setCorpId(corpId);
                    if (subscriberDTO.getCorpId() != corpId) {
                        Collection<KnCorpSubscriberDTO> externalContactList = new ArrayList<KnCorpSubscriberDTO>();
                        extCorpId = subscriberDTO.getCorpId();
                        externalContactList.add(new KnCorpSubscriberDTO(mdn));
                        KnMdnDetailsPersistDTO mdnDetailsPersistDTO = contactInfoUtil.getExternalConatctsInfo(extCorpId, externalContactList, xdmsHomePttId, persisterTxn);
                        Collection<KnCorpSubscriberDTO> externalLIst = mdnDetailsPersistDTO.getExternalMdnList();
                        if (externalLIst != null && !externalLIst.isEmpty()) {
                            for (KnCorpSubscriberDTO corpSubscriberDTO : externalLIst) {
                                subsc.setName(corpSubscriberDTO.getName());
                            }
                        }
                        subsc.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT);
                        subsc.setClientType(subsProfile.getClientType());
                    }
                    newMdnList.add(subsc);
                    addedMdnListMap.put(subscriberDTO.getMdn(), newMdnList);
                }
                Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts =
                        commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, addedMdnListMap);
                contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContacts, xdmsHomePttId, persisterTxn);
                knLogger.info(methodName, "Updating ResourceList Etags");
                etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMemberList, xdmsHomePttId, etagMap, persisterTxn);
                if (etagMap.size() <= max_etag_update_notification) {
                    etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, finalMissingContacts, removeMdnListMap,
                            null, null, null, null,
                            null, null, delContMemStatus, null, null);
                }
                knLogger.debug(methodName, "etagMap ", KnGDPRTemplate.mapKeyMdn(etagMap));

                //Commented below method call to avoid deletion of data from mcptt_perm_info table. Because we updating mcptt_perm_info with new MDN.
                //etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
            }

            // get all groupIds where this MDN is member.
            Collection<Integer> groupIdLst = groupMemberMap.keySet();

            if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
                // update old mdn to new mdn in  CorpGroupMemberList table.
                groupInfoUtil.updateCorpGroupMemberList(groupIdLst, mdn, newMDN, xdmsHomePttId, persisterTxn);
            }

            Map<Integer, Collection<String>> groupMemberDistMap = groupInfoUtil.getAllSubscribersGroupDistForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            LinkedList<Integer> status = new LinkedList<Integer>();
            Map<Integer, Collection<KnCorpContactDTO>> addedGroupMembers = new HashMap<Integer, Collection<KnCorpContactDTO>>();
            HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();
            Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
            if (groupIdLst != null) {
                //updating in dg.corpgroupdistinfo table
                Collection<Integer> groupDistIdLst = groupMemberDistMap.keySet();
                status.add(1);
                if (groupDistIdLst.size() > 0) {
                    Map<Integer, Map<String, Collection<String>>> memberInDistDetailsList = new HashMap<Integer, Map<String, Collection<String>>>();
                    for (int id : groupDistIdLst) {
                        Map<String, Collection<String>> memDelDistMap = new HashMap<String, Collection<String>>();
                        memDelDistMap.put(KnConstants.DELTED_MEMBERS, removeMdnList);
                        memDelDistMap.put(KnConstants.ADDED_MEMBERS, requestMdnList);
                        memberInDistDetailsList.put(id, memDelDistMap);
                    }
                    groupInfoUtil.deleteFrmCorpGroupDistInfo(memberInDistDetailsList, xdmsHomePttId, persisterTxn);
                    groupInfoUtil.insertIntoCorpGroupDistInfo(memberInDistDetailsList, xdmsHomePttId, persisterTxn);
                }
                groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIdLst, xdmsHomePttId, persisterTxn);
                ArrayList<KnCorpGroupDTO> groupNameEtagList = groupInfoUtil.getGrpsNameEtagInfo(groupIdLst, xdmsHomePttId, persisterTxn);
                Map<Integer, Integer> groupEtagMap = new HashMap<Integer, Integer>();
                for (KnCorpGroupDTO groupData : groupNameEtagList) {
                    groupEtagMap.put(groupData.getGroupId(), groupData.getETag());
                    groupNameMap.put(groupData.getGroupId(), (String.valueOf(groupData.getCorpId()).concat("_").concat(groupData.getGroupDisplayName())));
                }

                /*
                    Get the group member count and group type for the groupIds and set these values into etag Map
                 */
                Map<Integer, Integer> groupMemCountMap = groupInfoUtil.getGroupMemCount(groupIdLst, xdmsHomePttId, persisterTxn);
                ArrayList<KnCorpGroupDTO> groupDetailList = groupInfoUtil.getGrpsNameEtagInfo(groupIdLst, xdmsHomePttId, persisterTxn);
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = new HashMap<>(groupDetailList.size());
                for (KnCorpGroupDTO groupDTO : groupDetailList) {
                    if (null != groupMemCountMap.get(groupDTO.getGroupId())) {
                        groupDTO.setGroupMemCount(groupMemCountMap.get(groupDTO.getGroupId()));
                    }
                    groupDetailsMap.put(groupDTO.getGroupId(), groupDTO);
                }
                etagMap = commonInfoUtil.formSubscriberNotification(groupDistList, groupEtagMap,
                        DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);

            }
            if ((groupMemberMap != null) && (groupMemberMap.size() <= max_etag_update_notification)) {
                int extCorpId = 0;
                for (Map.Entry<Integer, Collection<KnCorpGroupMemberDTO>> entry : groupMemberMap.entrySet()) {
                    int grpId = entry.getKey();
                    removeGrpMdnListMap.put(grpId, removeMdnList);
                    delGrpMemStatus.put(grpId, status);
                    ArrayList<KnCorpContactDTO> grpMemList = new ArrayList<KnCorpContactDTO>();
                    Collection<KnCorpGroupMemberDTO> members = entry.getValue();
                    KnCorpContactDTO grpMemDTO = new KnCorpContactDTO();
                    grpMemDTO.setMdn(newMDN);
                    grpMemDTO.setName(subsProfile.getNetworkName());
                    grpMemDTO.setCorpId(corpId);
                    for (KnCorpGroupMemberDTO groupMemberDTO : members) {
                        if (groupMemberDTO.getCorpId() != corpId) {
                            extCorpId = groupMemberDTO.getCorpId();
                            Collection<KnCorpSubscriberDTO> externalContactList = new ArrayList<KnCorpSubscriberDTO>();
                            externalContactList.add(new KnCorpSubscriberDTO(mdn));
                            KnMdnDetailsPersistDTO mdnDetailsPersistDTO = contactInfoUtil.getExternalConatctsInfo(extCorpId, externalContactList, xdmsHomePttId, persisterTxn);
                            Collection<KnCorpSubscriberDTO> externalLIst = mdnDetailsPersistDTO.getExternalMdnList();
                            if (externalLIst != null && !externalLIst.isEmpty()) {
                                for (KnCorpSubscriberDTO corpSubscriberDTO : externalLIst) {
                                    grpMemDTO.setName(corpSubscriberDTO.getName());
                                }
                            }
                        }
                        grpMemDTO.setSupervisory(groupMemberDTO.getSupervisory());
                        grpMemDTO.setLocWatcher(groupMemberDTO.getLocWatcher());
                    }
                    grpMemList.add(grpMemDTO);
                    addedGroupMembers.put(grpId, grpMemList);

                    etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null,
                            null, null, addedGroupMembers, removeGrpMdnListMap,
                            null, null, groupDistList, null, delGrpMemStatus, null);
                }
            }
            knLogger.info(methodName, "Updating owner MDN");
            //update the owner mdn in the contact list table
            contactInfoUtil.updateOwnerMdnInContactList(contactDTO, xdmsHomePttId, persisterTxn);

            if ((contactMemberList != null && !contactMemberList.isEmpty()) || (groupIdLst != null && !groupIdLst.isEmpty())) {
                knLogger.info(methodName, "Updating Directory Etags");
                Set<String> memberSet = new HashSet<String>();
                memberSet.addAll(contactMemberList);
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(memberSet, groupIdLst, etagMap, xdmsHomePttId, persisterTxn);
            }
            knLogger.info(methodName, "Contact MDNs for subscriber - ", contactMemberList != null ? contactMemberList.size() : 0);

            contactMemberList.add(mdn);
            contactMemberList.add(newMDN);
            contactInfoUtil.updateSubscribersContactCount(contactMemberList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            long currentCorpProfileEtag = contactInfoUtil.updateCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            respDTO.setEtag(String.valueOf(currentCorpProfileEtag));
            knLogger.debug(methodName, "etagMap - ", etagMap);
            HashMap<String, KnOPDirChgDTO> liEtagMap = new HashMap<>(etagMap);
            knLogger.debug(methodName, "liEtagMap - ", liEtagMap);
            etagMap.remove(newMDN);
            respDTO.setChangeLogMap(etagMap);
            //update the corp id for the other corporates where he is a external subscriber
            // contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(String.valueOf(corpId), newMDN, xdmsHomePttId, persisterTxn);
            //contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(null, mdn, xdmsHomePttId, persisterTxn);
            Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap = contactInfoUtil.getCorpIdContactTypeWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
            if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
                if (corpIdList != null && !corpIdList.isEmpty()) {
                    // update the corporate etag.
                    knLogger.debug(methodName, "corpIdList - ", corpIdList);
                    contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
                }
                //Delete oldMDN and insert new MDN in External contact table.
                contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
                contactInfoUtil.addExternalContactsInAllCorp(corpIdExtContactNameMap, newMDN, corpId, xdmsHomePttId, persisterTxn);
            }
            /**
             * Updating the CAMPEDGROUPINFO table.
             * To change the MDN here first delete the existing old mdn entries and insert with new mdn for each groupId.
             */

            KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
            ipTalkGroupDTO.setMdn(mdn);
            KnTalkGrpScanMode grpScanMode = groupInfoUtil.getSubsTalkGrpScanMode(mdn, xdmsHomePttId, persisterTxn);
            if (grpScanMode != null) {
                List<KnCorpTGSPersistDTO> corpTGSPersistDTOs = groupInfoUtil.getSubsCampedGrp(ipTalkGroupDTO, xdmsHomePttId, persisterTxn);
                List<KnCorpTalkGrpInfoDTO> newGrpList = new ArrayList<KnCorpTalkGrpInfoDTO>();

                for (KnCorpTGSPersistDTO corpTGSPersistDTO : corpTGSPersistDTOs) {
                    KnCorpTalkGrpInfoDTO corpTalkGrpInfoDTO = new KnCorpTalkGrpInfoDTO();
                    corpTalkGrpInfoDTO.setGroupId(corpTGSPersistDTO.getGroupId());
                    corpTalkGrpInfoDTO.setGroupName(corpTGSPersistDTO.getGroupName());
                    corpTalkGrpInfoDTO.setPriority(corpTGSPersistDTO.getPriority());
                    newGrpList.add(corpTalkGrpInfoDTO);
                }

                List<KnCorpTGSPersistDTO> corpChannelPersistDTOs = groupInfoUtil.getSubsChannelGrp(ipTalkGroupDTO, xdmsHomePttId, persisterTxn);
                List<KnCorpTalkGrpInfoDTO> newChannelGrpList = new ArrayList<KnCorpTalkGrpInfoDTO>();

                for (KnCorpTGSPersistDTO corpTGSPersistDTO : corpChannelPersistDTOs) {
                    KnCorpTalkGrpInfoDTO corpTalkGrpInfoDTO = new KnCorpTalkGrpInfoDTO();
                    corpTalkGrpInfoDTO.setGroupId(corpTGSPersistDTO.getGroupId());
                    corpTalkGrpInfoDTO.setGroupName(corpTGSPersistDTO.getGroupName());
                    corpTalkGrpInfoDTO.setChannel(corpTGSPersistDTO.getChannel());
                    newChannelGrpList.add(corpTalkGrpInfoDTO);
                }

                groupInfoUtil.createSubsChannelGrps(newMDN, newChannelGrpList, xdmsHomePttId, persisterTxn);
                groupInfoUtil.createSubsCampedGrps(newMDN, newGrpList, com.kodiak.common.resources.KnConstants.CAMPED_BY_CORPORATE_ADMIN,
                        xdmsHomePttId, persisterTxn);
                groupInfoUtil.createSubsTalkGrpScanMode(newMDN, grpScanMode.getMode(), xdmsHomePttId, persisterTxn);
                // Delete Subscriber case no need to send the SEH notification,
                // hence return type of the cleanUpSubsCampedGrps is irrelevant
                groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
                etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
            }
            // Authorization related Changes: Ambient & Discrete Listening
            Map<String, KnMcpttPermissionDTO> mcpttAuthPermissionMap = corpSubsProvInfoUtil.getAuthUserPermissions(mdn,
                    xdmsHomePttId, persisterTxn);
            Map<String, KnMcpttPermissionDTO> mcpttTargPermissionMap = corpSubsProvInfoUtil.getTargUserPermissions(mdn,
                    xdmsHomePttId, persisterTxn);
            Map<String, Long> authEtagMap = corpSubsProvInfoUtil.seleteFromAuthDoc(mdnList, xdmsHomePttId, persisterTxn);
            if(!authEtagMap.isEmpty()){
                Map<String, Long> newAuthEtagMap = new HashMap<>();
                newAuthEtagMap.put(newMDN, authEtagMap.get(mdn));
                corpSubsProvInfoUtil.deleteFromAuthDoc(mdnList, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoAuthDoc(null, newAuthEtagMap, xdmsHomePttId, persisterTxn);
                etagMap = commonInfoUtil.formMdnAuthDeleteNotification(mdn, etagMap);
            }
            boolean mcpttUpdate = false;
            if (mcpttAuthPermissionMap != null && !mcpttAuthPermissionMap.isEmpty()) {
                mcpttUpdate = true;
                Collection<KnMcpttPermissionDTO> mcpttPermissionDTOS = mcpttAuthPermissionMap.values();
                mcpttPermissionDTOS.forEach(mcpttDto -> {
                    mcpttDto.setAuthMdn(newMDN);
                });
                corpSubsProvInfoUtil.deleteFromMcpttPermInfoAuthMdn(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoMcpttPermInfo(mcpttPermissionDTOS, xdmsHomePttId, persisterTxn);
                etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTables(xdmsHomePttId, newMDN, null,
                        corpId, persisterTxn, etagMap);
            }
            if (mcpttTargPermissionMap != null && !mcpttTargPermissionMap.isEmpty()) {
                mcpttUpdate = true;
                Collection<KnMcpttPermissionDTO> mcpttPermissionDTOS = mcpttTargPermissionMap.values();
                mcpttPermissionDTOS.forEach(mcpttDto -> {
                    mcpttDto.setTargetMdn(newMDN);
                });
                corpSubsProvInfoUtil.deleteFromMcpttPermInfoTargetMdn(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoMcpttPermInfo(mcpttPermissionDTOS, xdmsHomePttId, persisterTxn);
                etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTables(xdmsHomePttId, null, newMDN,
                        corpId, persisterTxn, etagMap);
            }
            if (mcpttUpdate) {
                KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
                ipSubscriberInfoDTO.setMdn(mdn);
                ipSubscriberInfoDTO.setServiceAuthStatus(DISABLED);
                ipSubscriberInfoDTO.setServiceAuthStatusAU(DEFAULT_AU);
                ipSubscriberInfoDTO.setCorpId(String.valueOf(corpId));
                corpSubsProvInfoUtil.updateSubscriberServiceAuthStatus(ipSubscriberInfoDTO, xdmsHomePttId, persisterTxn);
            }

            //Emergency related Changes:
            //Emergency to be set POCSUBSCR_ADDLINFO,POCSUBSCRINFO
            KnSubsEmergencyAttributes oldMdnsubsEmergencyAttributes = corpSubsProvInfoUtil
                    .getEmergSubsAttributes(mdn, corpId, xdmsHomePttId, false, persisterTxn);

            KnSubsEmergencyAttributes updateSubsEmergencyAttributes = null;
            if (null != oldMdnsubsEmergencyAttributes.getEmergInitPermission() &&
                    oldMdnsubsEmergencyAttributes.getEmergInitPermission() != com.kodiak.common.resources.KnConstants.DISABLED) {
                updateSubsEmergencyAttributes = new KnSubsEmergencyAttributes();
                updateSubsEmergencyAttributes.setMdn(newMDN);
                updateSubsEmergencyAttributes.setEmergDestTypeIntf(oldMdnsubsEmergencyAttributes.getEmergDestTypeIntf());
                updateSubsEmergencyAttributes.setEmergCallType(oldMdnsubsEmergencyAttributes.getEmergCallType());
                updateSubsEmergencyAttributes.setEmergCnclPermission(oldMdnsubsEmergencyAttributes.getEmergCnclPermission());
                if (null != oldMdnsubsEmergencyAttributes.getEmergLmrBehaviour()) {
                    updateSubsEmergencyAttributes.setEmergLmrBehaviour(oldMdnsubsEmergencyAttributes.getEmergLmrBehaviour());
                }
                if (null != oldMdnsubsEmergencyAttributes.getEmergInitPermission()
                        && oldMdnsubsEmergencyAttributes.getEmergInitPermission() != EMERGENCY_INIT_NOT_MODIFIED) {
                    updateSubsEmergencyAttributes.setEmergInitPermission(oldMdnsubsEmergencyAttributes.getEmergInitPermission());
                }
                if (null != oldMdnsubsEmergencyAttributes.getEmergOriginBitSet()) {
                    updateSubsEmergencyAttributes.setEmergOriginBitSet(oldMdnsubsEmergencyAttributes.getEmergOriginBitSet());
                }
                if (null != oldMdnsubsEmergencyAttributes.getEmergTermBitSet()) {
                    updateSubsEmergencyAttributes.setEmergTermBitSet(oldMdnsubsEmergencyAttributes.getEmergTermBitSet());
                }
            }

            if (null != updateSubsEmergencyAttributes) {
                corpSubsProvInfoUtil.updateToEmergSubsDestInfo(updateSubsEmergencyAttributes, xdmsHomePttId, persisterTxn);
            }

            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributes =
                    corpSubsProvInfoUtil.getEmergAttributes(mdn, xdmsHomePttId, persisterTxn);
            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributesForDestination =
                    corpSubsProvInfoUtil.getEmergDestAttributesForDestination(mdn, xdmsHomePttId, false, persisterTxn);
            Map<String, Long> emergEtagMap = corpSubsProvInfoUtil.seleteFromEmergDoc(mdnList, xdmsHomePttId, persisterTxn);
            if(!emergEtagMap.isEmpty()){
                Map<String, Long> newEmergEtagMap = new HashMap<>();
                newEmergEtagMap.put(newMDN, emergEtagMap.get(mdn));
                corpSubsProvInfoUtil.deleteFromEmergDoc(mdnList, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoEmergDoc(null, newEmergEtagMap, xdmsHomePttId, persisterTxn);
                etagMap = commonInfoUtil.formMdnEmergDeleteNotification(mdn, etagMap);
            }
            if(mdnEmergAttributes != null && !mdnEmergAttributes.isEmpty()){
                mdnEmergAttributes.forEach(emergDestDto -> {
                    emergDestDto.setMdn(newMDN);
                });
                corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoEmergSubsAttributes(mdnEmergAttributes, xdmsHomePttId, persisterTxn);
                etagMap = corpSubsProvInfoUtil.updateEmergencyImpactedTables(xdmsHomePttId, newMDN, corpId, persisterTxn, etagMap);
            }
            if (mdnEmergAttributesForDestination != null && !mdnEmergAttributesForDestination.isEmpty()) {
                mdnEmergAttributesForDestination.forEach(emergDestDto -> {
                    emergDestDto.setEmergDest(newMDN);
                });
                corpSubsProvInfoUtil.updateEmergSubsAttributes(mdnEmergAttributesForDestination, xdmsHomePttId, persisterTxn);
                for (KnSubsDestEmergencyAttributes emergencyDestination : mdnEmergAttributesForDestination) {
                    etagMap = corpSubsProvInfoUtil.updateEmergencyImpactedTables(xdmsHomePttId, emergencyDestination.getMdn(),
                            corpId, persisterTxn, etagMap);
                }
            }
            // update GROUP_OWNER column of DG.CORPGROUPINFO to null where ever this mdn is as group owner
            //.. introduced in dynamic contacts and group feature
            this.groupInfoUtil.updateGroupOwner(mdn, newMDN, corpId, xdmsHomePttId, persisterTxn);

            knLogger.debug(methodName, "LI Data  - ");
            LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(liEtagMap
                    , xdmsHomePttId, groupNameMap, null, null,persisterTxn);
            respDTO.setLiEventList(liEventList);


        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while changeMdn (corp association) - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while changeMdn (corp association) - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);

        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpResponseDTO forceSync(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "forceSync(contactDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String subscriberMDN = contactDTO.getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(subscriberMDN, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            int corpId = subscProfile.getCorpId();
            knLogger.debug(methodName, "Corp Id :- ", corpId);
            if (corpId <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", KnGDPRTemplate.mdn(subscriberMDN));
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }
            knLogger.info(methodName, "SubscriberMDN Profile :- ", subscProfile);
            String xdmsHomePttId = subscProfile.getXdmsHome();
            knLogger.debug(methodName, "xdmsHomePttId :- ", xdmsHomePttId);
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = contactDTO.getCustomParamMap();
            if (contactDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(subscriberMDN), customParams, xdmsHomePttId, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                contactDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.FORCE_SYNC);
                hookIPDTO.setData(contactDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            if(subscProfile.getCorpId() != contactDTO.getCorpId()){
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Subscribers Does not belong to the corporation.");
            }
            //update etag in resourcelistindex and directory tables
            Collection<String> collMdnList = new ArrayList<String>();
            collMdnList.add(subscriberMDN);
            knLogger.info(methodName, "Before calling to update resourcelistindexdoc and directory tables");
            contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, collMdnList, persisterTxn, null);
            knLogger.info(methodName, "After update resourcelistindexdoc and directory tables is called.");
            //get subs group list
            knLogger.info(methodName, "Before calling to Update Groups Member Etags");
            Collection<Integer> groupList = groupInfoUtil.getGroupHavingMember(contactDTO, xdmsHomePttId, persisterTxn);
            groupInfoUtil.updateGroupListEtag(groupList, xdmsHomePttId, persisterTxn);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured during forceSyunc ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured during forceSyunc ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);

        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpInfoResDTO cleanUpCorporateProfile(KnIPCorpContactDTO corpContactDTO, int corpId,
                                                    KnCorpInOutParamDTO corpInOutParamDTO, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) {
        String methodName = "cleanUpCorporateProfile(corpContactDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpContactDTO);


        KnCorpInfoResDTO respDTO = new KnCorpInfoResDTO();
        try {
            //get MDN
//            int corpId = corpContactDTO.getCorpId();
            String mdn = corpContactDTO.getMdn();

            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, false, persisterTxn);

            String xdmsHome = corpProfile.getXdmsHome();

            KnIPCorpInfoDTO corpInfoDTO = new KnIPCorpInfoDTO();
            corpInfoDTO.setCorpId(corpId);

            //delete Subscriber Contact List
            contactInfoUtil.deleteSubscribersContactList(mdn, xdmsHome, persisterTxn);

            //Determine all the sublist for the corportae irrespective of the distribution policy or list type

            //Determine all the groups for the corporate
            Collection<KnCorpGroupInfoPersistDTO> groupList =
                    groupInfoUtil.getAllGroupList(corpInfoDTO, corpProfile.getMaxMemPerCorpGroup(), xdmsHome, persisterTxn);

            //Deleting the subscriber from shared groups.
            String xdmsHomePttId = corpProfile.getXdmsHome();
            Set<Integer> groupIds = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(Collections.singletonList(mdn),
                    xdmsHomePttId, persisterTxn).keySet();
            groupIds.removeAll(groupList.stream().map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toSet()));
            knLogger.debug(methodName, " Shared groups after filteration  ", groupIds);
            if (!groupIds.isEmpty()) {
                List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHome, persisterTxn);
                Map<Integer, Collection<String>> memberMdnSublistMap = new HashMap<>();
                groupInfoList.forEach(groupDto -> memberMdnSublistMap.put(groupDto.getGrpMemListId(), Collections.singletonList(mdn)));
                knLogger.debug(methodName, " memberMdnSublistMap - ", memberMdnSublistMap);
                //delete the member from sublist
                sublistInfoUtil.deleteMembersFromAllSublist(memberMdnSublistMap, xdmsHome, persisterTxn);
                LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<>();
                LinkedList<String> mdnList = new LinkedList<>();
                mdnList.add(mdn);
                groupIds.forEach(groupId -> removeGrpMdnListMap.put(groupId, mdnList));
                //delete from memberList table
                groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
                // Update the group member count
                sublistInfoUtil.updateSublistsSubscribersContactCount(groupIds, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
                Map<Integer, Map<String, Collection<String>>> groupMemDelMap = new HashMap<Integer, Map<String, Collection<String>>>();
                for (int grpId : groupIds) {
                    Map<String, Collection<String>> deletedMemberMap = new HashMap<String, Collection<String>>();
                    deletedMemberMap.put(KnConstants.DELTED_MEMBERS, removeGrpMdnListMap.get(grpId));
                    groupMemDelMap.put(grpId, deletedMemberMap);
                }
                //deleting from DistInfo table
                groupInfoUtil.deleteFrmCorpGroupDistInfo(groupMemDelMap, xdmsHomePttId, persisterTxn);
            }

            knLogger.debug(methodName, "Retrieved CorpGroup List - ", groupList);
            if (groupList != null && !groupList.isEmpty()) {
                Collection<Integer> groupIdsList = new ArrayList<Integer>();
                Collection<String> mdnList = new ArrayList<String>();
                mdnList.add(mdn);
                Map<Integer, Collection<String>> removeGrpMdnListMap = new HashMap<Integer, Collection<String>>();
                for (KnCorpGroupInfoPersistDTO groupPersistTO : groupList) {
                    int grpId = groupPersistTO.getGroupId();
                    groupIdsList.add(groupPersistTO.getGroupId());
                    removeGrpMdnListMap.put(grpId, mdnList);
                }
                //getting the record from DG.RECORDING_TARGET_INFO.
                List<String> target = new ArrayList<>();
                for (int groupId : groupIdsList) {
                    target.add(corpId + ("_") + groupId);
                }
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHome);
                //deleting the record from DG.RECORDING_TARGET_INFO.
                commonXDMServerDAO.deleteRecordingInfoTargetByTarget(target, persisterTxn);
                //Remove all the groups list ref entries
                groupInfoUtil.deleteAllGroups(groupIdsList, xdmsHome, corpInOutParamDTO, persisterTxn);
                etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHome, persisterTxn);
                etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHome, persisterTxn);
                respDTO.setChangeLogMap(etagMap);
            }
            //getting shared groups of owned corp
            List<KnCorpSharedCorpInfo> ownedCorpGroupInfo = groupInfoUtil.selectGroupSharedCorpInfoByOwnedCorpId(corpId, xdmsHome, persisterTxn);
            respDTO.setGroupIds(ownedCorpGroupInfo.stream().map(KnCorpSharedCorpInfo::getCorpGroupId).collect(Collectors.toList()));
            respDTO.setMdnCorpId(corpId);
            //deleting group sharing data
            groupProfilUtil.deleteGroupProfileSharedInfoByOwnedCorpId(corpId, xdmsHome, persisterTxn);
            groupProfilUtil.deleteGroupProfileByCorpId(corpId, xdmsHome, persisterTxn);
            Collection<String> upmIds = userProfileUtil.getUserProfileIdsByCorpId(corpId, xdmsHome, persisterTxn);
            groupInfoUtil.deleteProfileGroupInfoByProfileIds(new ArrayList<>(upmIds), xdmsHome, persisterTxn);
            groupInfoUtil.deleteSharedGroupsByOwnedCorp(corpId, xdmsHome, persisterTxn);
            KnGeneralCacheUtil.getInstance().deleteCorpMatrixByOwnedCorpId(corpProfile.getExtCorpId().trim());

            //Determine all the sublist for the corportae irrespective of the distribution policy or list type
            Collection<Integer> sublistIdsList = sublistInfoUtil.getCorpSublistIdList(corpInfoDTO, xdmsHome, persisterTxn);
            if (sublistIdsList != null && !sublistIdsList.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(sublistIdsList, xdmsHome, persisterTxn);
            }
            //Deleting the external guys for the corporate
            contactInfoUtil.deleteCorporateExternalMembers(corpInfoDTO, xdmsHome, persisterTxn);

            //Nullify the corpid for the corp members in the external table
//            contactInfoUtil.nullifyContactCorpIdInExtTable(corpId, xdmsHome, persisterTxn);

            //deleting all user profiles for the corporate
            userProfileUtil.deleteUserProfilesByCorpId(String.valueOf(corpId),xdmsHome,persisterTxn);
            //Step:
            //prepare Group List respone
            populate(respDTO);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured during forceSyunc ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured during forceSyunc ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);

        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpResponseDTO createSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "createSubscriber(contactDTO, persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
        try {
            String mdn = contactDTO.getMdn();

            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);

            String xdmsHomePttId = subsProfile.getXdmsHome();
            int corpId = contactDTO.getCorpId();
            knLogger.debug(methodName, "Corp Id :- ", corpId);
            /* if (corpId <= 0) {
                knLogger.error( methodName, "Invalid Corporate Subscriber passed. MDN - " , mdn);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }*/
            boolean subscExists = contactInfoUtil.checkExternalContactExists(contactDTO, corpId, xdmsHomePttId, persisterTxn);
            Map<Integer, String> groupNameMap = new HashMap<>();
            if (subscExists) {
                LinkedList<String> requestMdnList = new LinkedList<String>();
                requestMdnList.add(mdn);
                Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.getSubcriberSublistMemberShipList(
                        requestMdnList, corpId, xdmsHomePttId, persisterTxn);

                Collection<Integer> sublistLists = sublistMemberMap.keySet();
                Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupList(requestMdnList,
                        corpId, xdmsHomePttId, persisterTxn);
                Collection<Integer> groupIds = groupMemberMap.keySet();

                Collection<Integer> sublistList = sublistMemberMap.keySet();
                Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(sublistList,
                        xdmsHomePttId, persisterTxn);
                if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                    sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
                    sublistInfoUtil.fetchAndUpdateSublistEtag(sublistLists, xdmsHomePttId, persisterTxn);
                }
                Collection<String> contactMemberList = new ArrayList<String>();
                if (sublistList != null && !sublistList.isEmpty()) {
                    for (int sublistId : sublistList) {
                        Collection<KnCorpSubscriberDTO> mdnList = contactMemberMap.get(sublistId);
                        if (mdnList != null && !mdnList.isEmpty()) {
                            for (KnCorpSubscriberDTO mdnStr : mdnList) {
                                if (!requestMdnList.contains(mdnStr.getMdn())) {
                                    contactMemberList.add(mdnStr.getMdn());
                                }
                            }
                        }
                    }
                }

                Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
                if (contactMemberList != null && !contactMemberList.isEmpty()) {
                    LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
                    for (String contactMdn : contactMemberList) {
                        removeMdnListMap.put(contactMdn, requestMdnList);
                    }
                    contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                    etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMemberList,
                            xdmsHomePttId, null, persisterTxn);
                }
                if (groupIds != null && !groupIds.isEmpty()) {
                    Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);

                    LinkedHashMap<Integer, LinkedList<String>> removeMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
                    for (int groupId : groupIds) {
                        removeMdnListMap.put(groupId, requestMdnList);
                    }
                    groupInfoUtil.deleteCorpGroupMemberList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                    Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(groupIds, xdmsHomePttId, persisterTxn);
                    if (groupListStatus.get(KnConstants.MODIFIED) != null && !(groupListStatus.get(KnConstants.MODIFIED)).isEmpty()) {
                        Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
                        /*etagMap = commonInfoUtil.formSubscriberNotification(groupDistList, groupEtagMap,
                                DOC_CHANGE_TYPE.REPLACE.value(), etagMap);*/
                        groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                    }
                    if (groupListStatus.get(KnConstants.DELETED) != null && !(groupListStatus.get(KnConstants.DELETED)).isEmpty()) {
                        groupInfoUtil.deleteAllGroups(groupListStatus.get(KnConstants.DELETED).keySet(), xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                        etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                        etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                        groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                        respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
                    }
                }

                Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
                ArrayList<Integer> sharedSublistList = new ArrayList<Integer>(sharedSublistSet);
                ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(sharedSublistList, xdmsHomePttId, persisterTxn);
                if (emptySublist != null && !emptySublist.isEmpty()) {
                    sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
                }

                if ((!contactMemberList.isEmpty()) || (!groupIds.isEmpty())) {
                    etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMemberList, groupIds, etagMap, xdmsHomePttId,
                            persisterTxn);
                    sublistInfoUtil.updateSublistsSubscribersContactCount(sublistLists, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                            MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
                }

                contactInfoUtil.deleteExtMember(requestMdnList, corpId, xdmsHomePttId, persisterTxn);
                //long currentCorpProfileEtag = contactInfoUtil.updateCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
                // respDTO.setEtag(String.valueOf(currentCorpProfileEtag));
                respDTO.setChangeLogMap(etagMap);
                knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(etagMap
                        , xdmsHomePttId, groupNameMap, null, null,persisterTxn);
                respDTO.setLiEventList(liEventList);
            }
            //update the corp id for the other corporates where he is a external subscriber
            contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(String.valueOf(corpId), mdn, xdmsHomePttId, persisterTxn);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while removing internal subscriber external data for the corporate - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while removing internal subscriber external data for the corporate - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);

        }
        knLogger.info(methodName, "EXIT : Returning Response - ");
        return respDTO;
    }


    public KnCorpResponseDTO updateSubscSubscriptionType(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap, KnCorpInOutParamDTO corpInOutParamDTO, HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "updateSubscriptionType(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO);

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
        }
        String xdmsHomePttId = subsProfile.getXdmsHome();
        //int corpId = contactDTO.getCorpId();
        int newCorpId = contactDTO.getNewCorpId();
        int subscriptionType = contactDTO.getSubscriptionType();
        int newSubscriptionType = contactDTO.getNewSubscriptionType();
        knLogger.debug(methodName, "subscriptionType - ", subscriptionType);
        knLogger.debug(methodName, "newSubscriptionType - ", newSubscriptionType);
        Set<String> mdnList = new HashSet<String>();
        switch (subscriptionType) {

            case KnConstants.SUBSCRIPTION_TYPE_PUBLIC: {
                knLogger.debug(methodName, " Changing from Public - ");
                switch (newSubscriptionType) {
                    case KnConstants.SUBSCRIPTION_TYPE_CORPORATE: {
                        // changing from public to corporate
                        // need to delete external data of all corporation
                        // because corporate subsc can not exist as external contact.
                        knLogger.debug(methodName, " Changing from Public to Corporate ");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        // Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
                        //knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
                       /* if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                            Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
                            if (corpIdList != null && !corpIdList.isEmpty()) {
                                // update the corporate etag.
                                //knLogger.debug(methodName, "corpIdList - ", corpIdList);
                               // contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
                            }
                           // contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
                        }        */
                        break;
                    }
                    case KnConstants.SUBSCRIPTION_TYPE_PUBLIC_CORP: {
                        // changing from public to public & corporate
                        // update the corpid in ext contact table and list member table.
                        knLogger.debug(methodName, " Changing from Public to Corporate&Public ");
                        contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(String.valueOf(newCorpId), mdn, xdmsHomePttId, persisterTxn);
                        break;
                    }
                } // inner switch close
            } // first case close
            break;

            case KnConstants.SUBSCRIPTION_TYPE_CORPORATE: {
                knLogger.debug(methodName, " Changing from Corporate - ");
                switch (newSubscriptionType) {
                    case KnConstants.SUBSCRIPTION_TYPE_PUBLIC: {
                        // Changing from corporate to public
                        // Need to clean corporate data.
                        knLogger.debug(methodName, " Changing from Corporate to Public - ");
                        KnCorpResponseDTO resp = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        //  set null for corpid in ext contact and list member table.
                        contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(null, mdn, xdmsHomePttId, persisterTxn);
                        //determine the no of members for the corporate
                        int count = contactInfoUtil.getCorpSubscriberCount(contactDTO.getCorpId(), xdmsHomePttId, persisterTxn);
                        // cleanup the corporate profile if he is the last subs in corp
                        if (count == 0) {
                            cleanUpCorporateProfile(contactDTO, contactDTO.getCorpId(), corpInOutParamDTO, persisterTxn, etagMap);
                        }
                        mdnList.add(mdn);
                        if (resp.getDisabledDispatchMemList() != null) {
                            mdnList.addAll(resp.getDisabledDispatchMemList());
                        }
                        respDTO.setDisabledDispatchMemList(mdnList);
                        break;
                    }
                    case KnConstants.SUBSCRIPTION_TYPE_PUBLIC_CORP: {
                        // Changing from corporate to corp & public
                        // No change required
                        knLogger.debug(methodName, " Changing from Corporate to Public&Corp So no change required - ");
                        break;
                    }
                }   //inner switch end
            } // second case end
            break;

            case KnConstants.SUBSCRIPTION_TYPE_PUBLIC_CORP: {
                knLogger.debug(methodName, " Changing from Corporate&Public - ");
                switch (newSubscriptionType) {
                    case KnConstants.SUBSCRIPTION_TYPE_CORPORATE: {
                        // Changing from Public & corporate to Corporate
                        // need to delete ext contact table
                        knLogger.debug(methodName, " Changing from Corporate&Public to Corporate ");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        /*Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
                        knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
                        if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                            Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
                            if (corpIdList != null && !corpIdList.isEmpty()) {
                                // update the corporate etag.
                                knLogger.debug(methodName, "corpIdList - ", corpIdList);
                                contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
                            }
                            contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
                        }*/
                        break;
                    }
                    case KnConstants.SUBSCRIPTION_TYPE_PUBLIC: {
                        // Changing from Public&Corporate to Public
                        // Need to delete corporate data and set corpid to null in ext and list
                        // member table.
                        knLogger.debug(methodName, " Changing from Corporate&Public to Public ");
                        KnCorpResponseDTO resp = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(null, mdn, xdmsHomePttId, persisterTxn);
                        //determine the no of members for the corporate
                        int count = contactInfoUtil.getCorpSubscriberCount(contactDTO.getCorpId(), xdmsHomePttId, persisterTxn);
                        // cleanup the corporate profile if he is the last subs in corp
                        if (count == 0) {
                            cleanUpCorporateProfile(contactDTO, contactDTO.getCorpId(), corpInOutParamDTO, persisterTxn, etagMap);
                        }
                        mdnList.add(mdn);
                        if (resp.getDisabledDispatchMemList() != null) {
                            mdnList.addAll(resp.getDisabledDispatchMemList());
                        }
                        respDTO.setDisabledDispatchMemList(mdnList);
                        break;
                    }

                } // inner switch end
            } // third case end
            break;

        }// end of upper most switch

        respDTO.setChangeLogMap(etagMap);
        knLogger.info(methodName, "EXIT : Returning Response - ");
        return respDTO;
    }

    /**
     * This method deletes the corporate data of the subscriber in its corporation
     *
     * @param contactDTO
     * @param subsProfile
     * @param persisterTxn
     * @param etagMap      @return
     */
    private KnCorpResponseDTO deleteSubscribersCorpData(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnCorpInOutParamDTO corpInOutParamDTO, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap, HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "deleteSubscribersCorpData(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        Set<String> groupFleetMembers = new HashSet<String>();
        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        //Collection<String> contactMDNs;

        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
        }
        int corpId = contactDTO.getCorpId();
        KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
        /* No need to throw this
       if (corpId <= 0) {
            knLogger.error( methodName, "Invalid Corporate Subscriber passed. MDN - " , mdn);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
        }*/
        //contactDTO.setCorpId(corpId);
        //use the xdms home pttServerId from Corp profile
        String xdmsHomePttId = subsProfile.getXdmsHome();
        int privateContactListId = subsProfile.getContactListId();
        knLogger.debug(methodName, "xdmsHomePttId - ", xdmsHomePttId);
        //determine the no of members for the corporate
        /* int count = contactInfoUtil.getCorpSubscriberCount(corpId, xdmsHomePttId, persisterTxn);
        if (count <= 1) {
            return cleanUpCorporateProfile(contactDTO, persisterTxn);
        }*/
        //1. Delete this subscriber from contact list of all the subscribers to whom he belongs to
        //2. Delete this subscriber from all groups he belongs to
        //3. Delete association from all the Sublists pushed to him
        //4. Delete this subscriber from all the Sublists where he is a member
        //5. Delete his Private ConatctList Sublist

        // get list of MDNs where he is present as contact.
        // Collection<String> contactMDNs = contactInfoUtil.getContactMDNs(mdn, xdmsHomePttId, persisterTxn);
        //Get list of sublist ids distributed to this subscriber.
        // sending corpListId = 0 , for retrieving the private list id also.

        // delete the privatecontactlist id
        if (privateContactListId > 0) {
            sublistInfoUtil.deleteSublist(privateContactListId, corpId, xdmsHomePttId, persisterTxn);
            KnCorpSublistDTO sublistDto = new KnCorpSublistDTO();
            sublistDto.setSublistId(0);
            contactInfoUtil.updateSubscrinberCorpListId(mdn, sublistDto, xdmsHomePttId, persisterTxn);
        }

        Collection<KnCorpSublistDTO> listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactDTO,
                0, xdmsHomePttId, persisterTxn);
        if (listOfDistSublistIds != null && !listOfDistSublistIds.isEmpty()) {
            Collection<Integer> removeListIds = new ArrayList<Integer>();
            for (KnCorpSublistDTO corpSubDto : listOfDistSublistIds) {
                removeListIds.add(corpSubDto.getSublistId());
            }
            KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
            corpSubsDto.setMdn(mdn);
            contactInfoUtil.removeSublistMappingForSubscriber(removeListIds, corpSubsDto,
                    xdmsHomePttId, persisterTxn);
        }
        LinkedList<String> mdnList = new LinkedList<String>();
        mdnList.add(mdn);
        Map<String, Collection<String>> dispContactList = null;
        if (contactDTO.getClientType() == DISPATCH_CLIENT.value()
                || contactDTO.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
            dispContactList = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "Contacts -" + KnGDPRTemplate.mapKeyValueListMdn(dispContactList));
        }
        //Delete MCPTT related document.
        //etagMap = deleteMcpttDoc(mdn, xdmsHomePttId, etagMap, persisterTxn);
        // Delete SubsAddl Talk Group:
        Collection<KnCorpAddlTGInfoDTO> subsMdnAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "subsMdnAddlTGList - ", subsMdnAddlTGList);
        if(subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()){
            groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);
            //groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);
            //etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);
            etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
            if (subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()) {
                groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsMdnAddlTGList, xdmsHomePttId, persisterTxn);
            }
            knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
            if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, null,
                        subsMdnAddlTGList, groupCorpIdMap);
                knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            }
        }
        contactInfoUtil.deleteSubscribersContactList(mdn, xdmsHomePttId, persisterTxn);
        contactInfoUtil.updateSubscribersContactCount(mdnList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
        // get all sublist ids where this MDN is member in the corporation.
        Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.
                getSubcriberSublistMemberShipList(mdnList, corpId, xdmsHomePttId, persisterTxn);
        //get all group ids where this mdn is member in the corporation.
        Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupList(mdnList, corpId,
                xdmsHomePttId, persisterTxn);

        //Get the Dispatch groups with name where the subscriber is dispatcher.
        Map<Integer, String> dispGrpNameMap = groupInfoUtil.getSupervisorGroupsAndName(mdn, KnConstants.GROUP_DISPATCHER, xdmsHomePttId, persisterTxn);
        List<Integer> dispatchGrpList = new ArrayList<>(dispGrpNameMap.keySet());

        //Get the all group Ids into groupIds variable and remove dispatch group Ids.
        //As dispatch group will be deleted and other will be modified.
        Set<Integer> grIds = groupMemberMap.keySet();
        Collection<Integer> groupIds = new ArrayList<Integer>(grIds);

       /* //check if any other dispatcher is existing if exits then remove from dispatcherGrpList.
        Set<Integer> nonDelDispGrp =  groupInfoUtil.getGrpDispMemList(dispatchGrpList, mdn, xdmsHomePttId, persisterTxn);
        // nonDelDispGrp should contain the group id against the list of otherDispatchers
        dispatchGrpList.removeAll(nonDelDispGrp);*/

        /*//check the group ids with respective to count.
        Set<Integer> nonDeleteGroupIds =  groupInfoUtil.getGrpIdsHavingAtleastOneMember(dispatchGrpList, mdn, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "dispatchGrpList - ", dispatchGrpList);
        knLogger.debug(methodName, "nonDeleteGroupIds - ", nonDeleteGroupIds);
        // nonDeleteGroupIds should contain the group id having more than 1 member.
        dispatchGrpList.removeAll(nonDeleteGroupIds);*/

        /*//groupIds variable will be holding only non-dispatch group Ids.
        groupIds.removeAll(dispatchGrpList);*/

        Collection<Integer> groupInfoUpdate  = new ArrayList<>();
        if(subsProfile.getClientType() == SUBSCR_CLIENT_TYPE.GROUPMDN.value() || subsProfile.getClientType() == SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()){
            Map<Integer, Integer> sgMDNCountDetailsMap = groupInfoUtil.getSGCorpGroupMembersCount(grIds, xdmsHomePttId, persisterTxn);
            if(sgMDNCountDetailsMap != null) {
                sgMDNCountDetailsMap.forEach((corpGroupId, SGMemberCount) -> {
                    if (SGMemberCount == 1) {
                        groupInfoUpdate.add(corpGroupId);
                    }
                });
            }
            groupInfoUtil.modifyGroupLmrInteropCapable(LMR_INTEROP_NON_CAPABLE, groupInfoUpdate, xdmsHomePttId, persisterTxn);
        }

        Collection<Integer> sublistList = sublistMemberMap.keySet();
        Collection<String> contactMDNs = new ArrayList<String>();
        if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
            Collection<Integer> sublistIdList = sublistMemberMap.keySet();
            sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
            sublistInfoUtil.fetchAndUpdateSublistEtag(sublistIdList,
                    xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "Calling sublist List Distribution list - ", sublistMemberMap);
            Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                    sublistList, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
            for (int sublistId : sublistList) {
                Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                if (contactList != null && !contactList.isEmpty()) {
                    for (KnCorpSubscriberDTO corpSubscriberDTO : contactList) {
                        String contact = corpSubscriberDTO.getMdn();
                        if (!mdnList.contains(contact)) {
                            contactMDNs.add(contact);
                        }
                    }
                }
            }
            knLogger.info(methodName, "ContactList - ", KnGDPRTemplate.mdnList(contactMDNs));
        }

        //Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
        LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
        LinkedHashMap<String, LinkedList<Integer>> delContactStatus = new LinkedHashMap<String, LinkedList<Integer>>();
        if (!contactMDNs.isEmpty()) {
            //deleting members from the contact list table
            for (String subscMdn : contactMDNs) {
                removeMdnListMap.put(subscMdn, mdnList);
            }
            delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Updating ResourceList Etags");
            etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, etagMap, persisterTxn);
        }
        Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
        if (!sharedSublistSet.isEmpty()) {
            ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "XX sharedSublists - ", sharedSublists);
            ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(sharedSublists, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "XX emptySublist - ", emptySublist);
            if (emptySublist != null && !emptySublist.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
                KnCorpProfileDTO corpProfileInfo = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, true, persisterTxn);
                int pairedContactListId = corpProfileInfo.getPairedContactListId();
                if (emptySublist.contains(pairedContactListId)) {
                    sublistInfoUtil.updateCorpPairedContListId(corpId, 0, xdmsHomePttId, persisterTxn);
                }
            }
        }

        LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
        LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
        Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
        Collection<String> disabledOdlMember = new HashSet<>();
        if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
            //delete members from the corpgroupmemberlist table
            Map<Integer, Map<String, Collection<String>>> memberDetailsList = new HashMap<Integer, Map<String, Collection<String>>>();
            for (int grpId : groupIds) {
                removeGrpMdnListMap.put(grpId, mdnList);
                Map<String, Collection<String>> memDelListMap = new HashMap<String, Collection<String>>();
                memDelListMap.put(KnConstants.DELTED_MEMBERS, mdnList);
                knLogger.info(methodName, "XX memDelListMap = ", KnGDPRTemplate.mapKeyValueListMdn(memDelListMap));
                memberDetailsList.put(grpId, memDelListMap);
            }
            knLogger.info(methodName, "XX memberDetailsList - ", memberDetailsList);

            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(groupIds, xdmsHomePttId, persisterTxn);
            Map<Integer, Collection<KnCorpAddlTGInfoDTO>> existingAddlTGList = new HashMap<>();
            Collection<KnCorpAddlTGInfoDTO> delAddlTGList = new ArrayList<>();
            // Additional Talk Group:
            for (KnCorpAddlTGInfoDTO addlTg : subsAddlTGList) {
                Collection<KnCorpAddlTGInfoDTO> existAddl = null;
                if (existingAddlTGList.get(addlTg.getGroupId()) != null) {
                    existAddl = existingAddlTGList.get(addlTg.getGroupId());
                    existAddl.add(addlTg);
                } else {
                    existAddl = new ArrayList<>();
                    existAddl.add(addlTg);
                    existingAddlTGList.put(addlTg.getGroupId(), existAddl);
                }
            }
            Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(groupIds, xdmsHomePttId, persisterTxn);
            for(KnCorpGroupMemberDTO grpMem : grpMemberDetails){
                disabledOdlMember.add(grpMem.getMdn());
            }
            knLogger.info(methodName, "XX grpMemberDetails - ", grpMemberDetails);
            delGroupMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
            groupInfoUtil.deleteFrmCorpGroupDistInfo(memberDetailsList, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Deleting members from Sublists.");
            knLogger.info(methodName, "Updating Group Etags");
            Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Updating Subs Group Etags");
            //Determine the deleted and the modified groups
            Map<String, HashMap<Integer, String>> groupListStatus =
                    groupInfoUtil.getGroupListStatus(groupIds, xdmsHomePttId, persisterTxn);

            // Get Distribution list for dispatch groups and non-dispatch groups
            List<Integer> allGroupIdList = new ArrayList<>(groupIds);
            //allGroupIdList.addAll(dispatchGrpList);
            groupDistList = groupInfoUtil.getGroupSubscriberDistList(allGroupIdList, xdmsHomePttId, persisterTxn);
            //Adding fleet members from dispatch group into groupFleetMembers variable.
            for (int dispGrpId : dispatchGrpList) {
                if (null != groupDistList.get(dispGrpId)) {
                    groupFleetMembers.addAll(groupDistList.get(dispGrpId));
                }
            }
            //Updating the group member count
            Collection<Integer> sublistIdList = sublistMemberMap.keySet();
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdList, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            //Get the group member count and group type for the groupIds and set these values into etag Map
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupMemberMap.keySet(), xdmsHomePttId, persisterTxn);
            //Get group member count after updating the group
            Map<Integer, Integer> updatedGrpMemCount = groupInfoUtil.getGroupMemCount(groupIds, xdmsHomePttId, persisterTxn);

            Collection<Integer> ownerGroupIds = groupInfoUtil.getOwnerGroupIds(mdn, corpId, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "ownerGroupIds: ", ownerGroupIds);
            // Delete corp groups with < 2 members
            Set<Integer> delGroupIdList = new HashSet<>();
            if (groupListStatus.get(KnConstants.DELETED) != null || !ownerGroupIds.isEmpty()) {
                delGroupIdList = new HashSet<>();
                if (groupListStatus.get(KnConstants.DELETED) != null) {
                    delGroupIdList.addAll(groupListStatus.get(KnConstants.DELETED).keySet());
                }
                //delGroupIdList.addAll(dispatchGrpList);
                delGroupIdList.addAll(ownerGroupIds);
                if (!delGroupIdList.isEmpty()) {
                    Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : delGroupIdList) {
                        Collection<String> delMembersList = groupDistList.get(grpId);
                        deletedMembersMap.put(grpId, delMembersList);
                        //for the contact added to update the directory
                        contactMDNs.addAll(delMembersList);
                        removeGrpMdnListMap.remove(grpId);
                        delGroupMemStatus.remove(grpId);
                        if(existingAddlTGList.get(grpId) != null) delAddlTGList.addAll(existingAddlTGList.get(grpId));
                    }
                    etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                    if (groupListStatus != null && groupListStatus.get(KnConstants.DELETED) != null) {
                        groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                    }
                    groupNameMap.putAll(dispGrpNameMap);
                }
            }
            // Additional Talk Group:
            if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                Collection<String> mdnForAddlTg = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
                groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmsHomePttId, persisterTxn);
                subsAddlTGList.removeAll(delAddlTGList);
                subsAddlTGList.forEach(subsAddl -> {
                    if(groupDetailsMap.get(subsAddl.getGroupId()) != null) subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                });
                etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnForAddlTg, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                    groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                }
                knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                    etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, subsAddlTGList, delAddlTGList, groupCorpIdMap);
                    knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                }
            }
            groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
            etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
            etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
            if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                Set<Integer> modGroupIdList = groupListStatus.get(KnConstants.MODIFIED).keySet();
                if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                    Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : modGroupIdList) {
                        KnCorpGroupDTO initialGrpInfo = groupDetailsMap.get(grpId);
                        Collection<String> modMembersList = groupDistList.get(grpId);
                        if(!initialGrpInfo.isLargeGroup()){
                            modifiedMembersMap.put(grpId, modMembersList);
                        }
                        //for the contact added to update the directory
                        contactMDNs.addAll(modMembersList);
                    }
                    etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                            DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                    groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                }
            }
            //Find the group IDs which was large group and after the operation it become small.
            Map<Integer, Integer> isLargeGroupFlagCngMap = new HashMap<>();
            for(KnCorpGroupDTO groupDTO : groupDetailsMap.values()){
                if(groupDTO.isLargeGroup()){
                    if(groupDTO.getGroupType() == BROADCAST_GROUP && updatedGrpMemCount.get(groupDTO.getGroupId()) <= corpProfile.getMaxMemPerLrgBGrp()){
                        isLargeGroupFlagCngMap.put(groupDTO.getGroupId(), 0);
                    }
                }
            }
            if(!isLargeGroupFlagCngMap.isEmpty()){
               groupInfoUtil.updateIsLargeGrpFlag(isLargeGroupFlagCngMap, xdmsHomePttId, persisterTxn);
            }
        }
        if (contactMDNs != null && !contactMDNs.isEmpty()) {
            knLogger.info(methodName, "Updating Directory Etags");
            etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
            contactInfoUtil.updateSubscribersContactCount(contactMDNs, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
        }
        knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
        etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap, null, null, removeGrpMdnListMap, null, null, groupDistList, delContactStatus, delGroupMemStatus, null);
        Collection<String> memberMDNs = new ArrayList<String>();
        memberMDNs.add(mdn);
        if (contactDTO.getNewSubscriptionType() != KnConstants.SUBSCRIPTION_TYPE_PUBLIC) {
            //Don't update resource list if new subscription type of MDN is public as public MDN don't have entry in corp resouce list table.
            contactInfoUtil.updateSubcribersResourceListIndexDoc(memberMDNs, xdmsHomePttId, null, persisterTxn);
        }
        knLogger.debug(methodName, "etagMap ", KnGDPRTemplate.mapKeyMdn(etagMap));
        etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
        knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
        knLogger.info(methodName, "Updating Directory Etags");
        etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(memberMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
        Map<String, KnCorpSubscriberDTO> subscDetails = contactInfoUtil.getSubscIsMemOfDispGrpDetails(memberMDNs, xdmsHomePttId, persisterTxn);
        Integer dispGrpMebBit = subscDetails.get(mdn).getDispatchGrpmember();
        List<String> overAllList = new ArrayList<String>();
        if (dispGrpMebBit == 1) {
            groupFleetMembers.add(mdn);
            overAllList.addAll(groupFleetMembers);
        }
        if (dispContactList != null && dispContactList.get(mdn) != null) {
            overAllList.addAll(dispContactList.get(mdn));
        }
        respDTO.setDisabledDispatchMemList(overAllList);
        if(respDTO.getDisabledDispatchMemList() != null) {
            disabledOdlMember.addAll(respDTO.getDisabledDispatchMemList());
        }
        respDTO.setDisabledDispatchMemList(disabledOdlMember);

        if(corpProfile.getLargeGrpSupported() == 1){
            //If large group is supported, remove the entry from etagMap if the doc change dto list is null or empty
            Set<String> mdnSet = new HashSet<>(etagMap.keySet());
            for(String subsMdn : mdnSet){
                KnOPDirChgDTO chgDTO = etagMap.get(subsMdn);
                if(chgDTO.getDocChgDTO() == null || chgDTO.getDocChgDTO().isEmpty()){
                    etagMap.remove(subsMdn);
                }
            }
        }

        respDTO.setChangeLogMap(etagMap);
        /**
         * Deleting the entries for the subscriber fromDG.CAMPEDGROUPINFO table
         */
        // Delete Subscriber case no need to send the SEH notification, hence
        // return type of the cleanUpSubsCampedGrps is irrelevant
        groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);

        // update GROUP_OWNER column of DG.CORPGROUPINFO to null where ever this mdn is as group owner
        //.. introduced in dynamic contacts and group feature
        this.groupInfoUtil.updateGroupOwner(mdn, null, corpId, xdmsHomePttId, persisterTxn);

        etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
        respDTO.setChangeLogMap(etagMap);
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    /**
     * This method deletes the external contact of all corporates.
     *
     * @param contactDTO
     * @param subsProfile
     * @param persisterTxn
     * @param etagMap      - contains the data structures for sending notifications.   @return etagMap after adding notifications data structures of affected MDN.
     */
    private Map<String, KnOPDirChgDTO> deleteExtContactDataFrmOtherCorp(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap, KnCorpInOutParamDTO inOutParamDTO, HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "deleteExtContactDataFrmOtherCorp(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        //Collection<String> contactMDNs;

        String mdn = contactDTO.getMdn();

        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
        }

        int corpId = contactDTO.getCorpId();
        //need to clean up ext data for public subscribers also so commenting below check
        /*if (corpId <= 0) {
            knLogger.error( methodName, "Invalid Corporate Subscriber passed. MDN - " , mdn);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
        }*/
        //contactDTO.setCorpId(corpId);
        //use the xdms home pttServerId from Corp profile
        String xdmsHomePttId = subsProfile.getXdmsHome();

        knLogger.debug(methodName, "xdmsHomePttId - ", xdmsHomePttId);

        //1. Delete this subscriber from contact list of all the subscribers to whom he belongs to
        //2. Delete this subscriber from all groups he belongs to
        //3. Delete association from all the Sublists pushed to him
        //4. Delete this subscriber from all the Sublists where he is a member
        //5. Delete his Private ConatctList Sublist

        LinkedList<String> mdnList = new LinkedList<String>();
        mdnList.add(mdn);
        //determine the corporates where the mdn is an external member.
        //Collection<Integer> corpList = contactInfoUtil.getExtCorpForSub(mdn, xdmsHomePttId, persisterTxn);
        Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
        //if (corpList == null || corpList.size() <= 0) {
        if (corpIdExtContactNameMap == null || corpIdExtContactNameMap.isEmpty()) {
            return etagMap;
        }
        //}
        // deleting the ext contact from ext contact table
//        Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
        //if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
        Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
        //if (corpIdList != null && !corpIdList.isEmpty()) {
        // update the corporate etag.
        knLogger.debug(methodName, "corpIdList - ", corpIdList);
        //contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
        //}
        //}
        // get all sublist ids where this MDN is member in other corporation as ext contact.
        Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.
                getSubcriberSublistMemberShipListAsExtContact(mdnList, corpId, xdmsHomePttId, persisterTxn);
        //get all group ids where this mdn is member in other corporation as ext contact.
        Map<Integer, Collection<String>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListAsExtContact(mdnList, corpId,
                xdmsHomePttId, persisterTxn);
        Collection<Integer> groupIds = groupMemberMap.keySet();
        Collection<Integer> sublistList = sublistMemberMap.keySet();
        Collection<String> contactMDNs = new ArrayList<String>();
        if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
            Collection<Integer> sublistIdList = sublistMemberMap.keySet();
            sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
            sublistInfoUtil.fetchAndUpdateSublistEtag(sublistIdList,
                    xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "Calling sublist List Distribution list - ", sublistMemberMap);
            Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                    sublistList, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
            for (int sublistId : sublistList) {
                Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                if (contactList != null && !contactList.isEmpty()) {
                    for (KnCorpSubscriberDTO corpSubscriberDTO : contactList) {
                        String contact = corpSubscriberDTO.getMdn();
                        if (!mdnList.contains(contact)) {
                            contactMDNs.add(contact);
                        }
                    }
                }
            }
            knLogger.info(methodName, "ContactList - ", KnGDPRTemplate.mdnList(contactMDNs));
        }
        LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
        LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus = null;
        LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
        // Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
        if (contactMDNs != null && !contactMDNs.isEmpty()) {
            //deleting members from the contact list table
            for (String subscMdn : contactMDNs) {
                removeMdnListMap.put(subscMdn, mdnList);
            }
            delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Updating ResourceList Etags");
            etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, etagMap, persisterTxn);
        }
        //
        LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
        Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
        if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
            //Updating the groups member count.
            Collection<Integer> sublistIdList = sublistMemberMap.keySet();
            // this method will update the contact count and group member count.
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdList, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            //Get the group member count and group type for the groupIds and set these values into etag Map
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupMemberMap.keySet(), xdmsHomePttId, persisterTxn);

            //delete members from the corpgroupmemberlist table
            for (int grpId : groupIds) {
                removeGrpMdnListMap.put(grpId, mdnList);
            }
            delGrpMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Updating Group Etags");
            Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Updating Subs Group Etags");
            //Determine the deleted and the modified groups
            Map<String, HashMap<Integer, String>> groupListStatus =
                    groupInfoUtil.getGroupListStatus(groupIds, xdmsHomePttId, persisterTxn);
            groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
            // Delete corp groups with < 2 members
            if (groupListStatus.get(KnConstants.DELETED) != null) {
                Set<Integer> delGroupIdList = groupListStatus.get(KnConstants.DELETED).keySet();
                if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                    Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : delGroupIdList) {
                        Collection<String> delMembersList = groupDistList.get(grpId);
                        deletedMembersMap.put(grpId, delMembersList);
                        //for the contact added to update the directory
                        contactMDNs.addAll(delMembersList);
                        removeGrpMdnListMap.remove(grpId);
                        delGrpMemStatus.remove(grpId);
                    }
                    groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, inOutParamDTO, persisterTxn);
                    etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                    groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                    etagMap = commonInfoUtil.formTGSCDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                    etagMap = commonInfoUtil.formTGSSDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                }
            }
            if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                Set<Integer> modGroupIdList = groupListStatus.get(KnConstants.MODIFIED).keySet();
                if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                    Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : modGroupIdList) {
                        Collection<String> modMembersList = groupDistList.get(grpId);
                        modifiedMembersMap.put(grpId, modMembersList);
                        //for the contact added to update the directory
                        contactMDNs.addAll(modMembersList);
                    }
                    etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                            DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                    groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                }
            }
        }

        Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
        if (sharedSublistSet != null && !sharedSublistSet.isEmpty()) {
            ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);
            ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(sharedSublists, xdmsHomePttId, persisterTxn);
            if (emptySublist != null && !emptySublist.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
            }
        }

        if (!contactMDNs.isEmpty()) {
            knLogger.info(methodName, "Updating Directory Etags");
            contactInfoUtil.updateSubscribersContactCount(contactMDNs, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
        }
        // deleting the ext contact from ext contact table
        if (!corpIdExtContactNameMap.isEmpty()) {
            contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
            contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
        }
        etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap, null, null, removeGrpMdnListMap, null, null, groupDistList, delContactStatus, delGrpMemStatus, null);
        knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
        etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
        knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
        //respDTO.setChangeLogMap(etagMap);
        // populate(respDTO);

        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return etagMap;
    }

    /**
     * This methos will be called when corp id of a subscriber get changed.
     * updateSubscriber() method needs to invoke this method.
     *
     * @param contactDTO
     * @param subsProfile
     * @param persisterTxn
     * @param etagMap      - containing the data structures for sending notification.   @return
     */
    private KnCorpResponseDTO updateSubscribersCorpId(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnPersisterTxn
            persisterTxn, KnCorpInOutParamDTO corpInOutParamDTO, Map<String, KnOPDirChgDTO> etagMap, HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "updateSubscribersCorpId(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO);
        //Set<String> groupFleetMembers = new HashSet<String>();
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ",KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
        }
        int corpId = contactDTO.getCorpId();
        int newCorpId = contactDTO.getNewCorpId();
        String xdmsHomePttId = subsProfile.getXdmsHome();
        int pairedContactListId = 0;
        KnCorpProfileDTO corpProfile;
        if (newCorpId != 0) {
            corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(newCorpId), CORP_PROFILE,
                    true, persisterTxn);
            knLogger.info(methodName, "Corporate Profile :- ", corpProfile);
            pairedContactListId = corpProfile.getPairedContactListId();

        }

        if (corpId != 0) {
            respDTO = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
            etagMap = respDTO.getChangeLogMap();
            knLogger.info(methodName, "Etag Map 1111 ", etagMap);
            if (newCorpId != 0) {
                contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(String.valueOf(newCorpId), mdn, xdmsHomePttId, persisterTxn);
                // set dispatch member flag to 0 in subsinfo table
                // delete ext contact in new corpid if he exist
                etagMap = deleteExtContactDetailInCorp(contactDTO, persisterTxn, etagMap, xdmsHomePttId, corpInOutParamDTO, groupNameMap);
                knLogger.info(methodName, "Etag Map 2222 ", etagMap);
            } else {
                contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(null, mdn, xdmsHomePttId, persisterTxn);
                // set dispatch member flag to 0 in subsinfo table
            }
        } else {
            if (newCorpId != 0) {
                contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(String.valueOf(newCorpId), mdn, xdmsHomePttId, persisterTxn);
                // set dispatch member flag to 0 in subsinfo table
                // delete ext contact in new corpid if he exist
                etagMap = deleteExtContactDetailInCorp(contactDTO, persisterTxn, etagMap, xdmsHomePttId, corpInOutParamDTO, groupNameMap);
            } /*else {
                contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(null, mdn, xdmsHomePttId, persisterTxn);
            }*/
        }

        //if (pairedContactListId != 0) {
        //    etagMap = addToParingListForCorpIdChange(contactDTO, persisterTxn, corpProfile, etagMap);
        // }
        //determine the no of members for the corporate
        int count = contactInfoUtil.getCorpSubscriberCount(corpId, xdmsHomePttId, persisterTxn);
        // cleanup the corporate profile if he is the last subs in corp
        if (corpId != 0 && count == 0) {
            cleanUpCorporateProfile(contactDTO, corpId, corpInOutParamDTO, persisterTxn, etagMap);
        }

        //removing the notification for the subscriber being changing
        // etagMap.remove(mdn);
        respDTO.setChangeLogMap(etagMap);
        knLogger.debug(methodName, "pairedContactListId :- ", pairedContactListId);
        respDTO.setPairedContactListId(pairedContactListId);
        knLogger.info(methodName, "EXIT : Returning Response - ", etagMap);
        return respDTO;
    }

    private Collection<String> updateSubscribersClientType(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap, KnCorpInOutParamDTO corpInOutParamDTO, HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "updateSubscribersClientType(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn,  Map<String, KnOPDirChgDTO>)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO.getMdn());

        Collection<String> affectedDispGrpMembers = new HashSet<String>();
        KnCorpResponseDTO respFrmDeleteDispatch;

        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
        }
        int clientType = contactDTO.getClientType();
        int newClientType = contactDTO.getNewClientType();
        knLogger.debug(methodName, "clientType - ", clientType);
        knLogger.debug(methodName, "newClientType - ", newClientType);

        switch (clientType) {
            case KnConstants.CLIENT_TYPE_UNKNOWN:
                knLogger.debug(methodName, "Changing from unknown ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Client Type is changing from unknown to dispatch
                        // need to delete all corporate data of the corporation and
                        // ext contact data of other corporation
                        knLogger.debug(methodName, "Changing from unknown to Distatch.");
                        deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        affectedDispGrpMembers.add(mdn);
                        break;

                    case KnConstants.CLIENT_TYPE_HANDSET:
                        // Changing from unknown to handset.
                        // No change required
                        knLogger.debug(methodName, "Changing from unknown to Handset- no change required ");
                        break;

                    case KnConstants.CLIENT_TYPE_DESKTOP:
                        // Changing from unknown to Desktop.
                        // No change required
                        knLogger.debug(methodName, "Changing from unknown to Desktop- no change required ");
                        break;

                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from unknown to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from unknown to interOP subscriber ");
                        deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        affectedDispGrpMembers.add(mdn);
                        break;

                }// inner switch end
                // first case end
                break;

            case KnConstants.CLIENT_TYPE_HANDSET:
                knLogger.debug(methodName, "Changing from Handset ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_UNKNOWN:
                        // Changing from Handset to Unknown.
                        // No change required
                        knLogger.debug(methodName, "Changing from Handset to Unknown- no change required ");
                        break;

                    case KnConstants.CLIENT_TYPE_DESKTOP:
                        // Changing from Handset to Desktop.
                        // No change required
                        knLogger.debug(methodName, "Changing from Handset to Desktop- no change required ");
                        break;

                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Handset to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from Handset to Dispatch");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        break;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from handset to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from handset to interOP subscriber ");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        respFrmDeleteDispatch = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        affectedDispGrpMembers = respFrmDeleteDispatch.getDisabledDispatchMemList();
                        affectedDispGrpMembers.add(mdn);
                        break;
                    case KnConstants.PTTRADIOHANDSETCLIENT:
                        // Changing from handset to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from handset to PttHandset subscriber ");
                        switchConvergedClient(contactDTO, subsProfile, persisterTxn, etagMap);
                        break;
                } // inner switch end
                // second case end
                break;

            case KnConstants.CLIENT_TYPE_DESKTOP:
                knLogger.debug(methodName, "Changing from Desktopt ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_UNKNOWN:
                        // Changing from Desktopt to Unknown.
                        // No change required
                        knLogger.debug(methodName, "Changing from Desktopt to Unknown- no change required ");
                        break;

                    case KnConstants.CLIENT_TYPE_HANDSET:
                        // Changing from Desktopt to Handset.
                        // No change required
                        knLogger.debug(methodName, "Changing from Desktopt to Handset- no change required ");
                        break;

                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Desktopt to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from Desktopt to Dispatch");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        affectedDispGrpMembers.add(mdn);
                        break;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from desktop to interOP subscriber ");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        respFrmDeleteDispatch = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        affectedDispGrpMembers = respFrmDeleteDispatch.getDisabledDispatchMemList();
                        affectedDispGrpMembers.add(mdn);
                        break;
                } // inner switch end
                // third case end
                break;

            case KnConstants.CLIENT_TYPE_DISPATCH:
                knLogger.debug(methodName, "Changing from Dispatch to Any ");
                respFrmDeleteDispatch = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                if (affectedDispGrpMembers != null && respFrmDeleteDispatch.getDisabledDispatchMemList() != null) {
                    affectedDispGrpMembers.addAll(respFrmDeleteDispatch.getDisabledDispatchMemList());
                } else if (respFrmDeleteDispatch.getDisabledDispatchMemList() != null) {
                    affectedDispGrpMembers = respFrmDeleteDispatch.getDisabledDispatchMemList();
                }
                break;

            case KnConstants.INTER_OP_CLIENT_TYPE:
                knLogger.debug(methodName, "Changing from InterOP to any other client ");
                // Deleteing the subscriber from group private sublist and group where he is.
                respFrmDeleteDispatch = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                affectedDispGrpMembers = respFrmDeleteDispatch.getDisabledDispatchMemList();
                break;
            case KnConstants.WIFI_CLIENT_TYPE:
                knLogger.debug(methodName, "Changing from WIFI Client ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Desktopt to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from Wifi to Dispatch");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        affectedDispGrpMembers.add(mdn);
                        break;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from Wifi to interOP subscriber ");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        respFrmDeleteDispatch = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        affectedDispGrpMembers = respFrmDeleteDispatch.getDisabledDispatchMemList();
                        affectedDispGrpMembers.add(mdn);
                        break;
                    case KnConstants.PTTRADIOWIFIONLYCLIENT:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from Wifi to PttWifi subscriber ");
                        switchConvergedClient(contactDTO, subsProfile, persisterTxn, etagMap);
                        break;
                }

            case KnConstants.THIRD_PARTYPOC_CLIENT_TYPE:
                knLogger.debug(methodName, "Changing from 3rd party PoC Client ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Desktopt to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from 3rdpoc to Dispatch");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        affectedDispGrpMembers.add(mdn);
                        break;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from 3rdpoc to interOP subscriber ");
                        deleteExtContactDataFrmOtherCorp(contactDTO, subsProfile, persisterTxn, etagMap, corpInOutParamDTO, groupNameMap);
                        respFrmDeleteDispatch = deleteSubscribersCorpData(contactDTO, subsProfile, corpInOutParamDTO, persisterTxn, etagMap, groupNameMap);
                        affectedDispGrpMembers = respFrmDeleteDispatch.getDisabledDispatchMemList();
                        affectedDispGrpMembers.add(mdn);
                        break;
                }

            case KnConstants.CLIENT_TYPE_CROSSCARRIER:
                knLogger.debug(methodName, "Changing from CrossCarrier ");
                switch (newClientType) {
                    case KnConstants.PTTRADIOCROSSCARRIERCLIENT:
                           /*  Changing from crossCarrier to pttCrossCarrier:
                            1. Need to Remove the Channel List/Scan list associated with the Subscriber.
                            2. Send Group/contact doc change notify clients where Subscr is a member of.
                            3. Send tgsc doc change notify to Client if applicable.
                            4. Send Group doc change event notifies to Micro Services. */
                        knLogger.debug(methodName, "Changing from CrossCarrier to PttCrossCarrier");
                        switchConvergedClient(contactDTO, subsProfile, persisterTxn, etagMap);
                        break;
                }

            case KnConstants.PTTRADIOHANDSETCLIENT:
            case KnConstants.PTTRADIOCROSSCARRIERCLIENT:
            case KnConstants.PTTRADIOWIFIONLYCLIENT:
                knLogger.debug(methodName, "Changing from pttHandset, pttCrossCarrier, pttWifi");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_HANDSET:
                    case KnConstants.CLIENT_TYPE_CROSSCARRIER:
                    case KnConstants.WIFI_CLIENT_TYPE:
                        /*  Changing from PttRadio to Non-PttRadio:
                            1. Need to Remove the Channel List/Scan list associated with the Subscriber.
                            2. Send Group/contact doc change notify clients where Subscr is a member of.
                            3. Send tgsc doc change notify to Client if applicable.
                            4. Send Group doc change event notifies to Micro Services. */
                        knLogger.debug(methodName, "Changing from PttRadio to Non-PttRadio");
                        if (etagMap == null) {
                            etagMap = new HashMap<>();
                        }
                        KnOPDirChgDTO directory = etagMap.get(mdn);
                        if (directory == null) {
                            directory = new KnOPDirChgDTO();
                        }
                        directory.setNtfyOnAnyMDN(NOTIFY_ON_ANY_MDN.BASE_MDN.value());
                        etagMap.put(mdn, directory);
                        knLogger.debug(methodName, "etagMap : -- ", etagMap);
                        switchConvergedClient(contactDTO, subsProfile, persisterTxn, etagMap);
                        break;
                }
                knLogger.debug(methodName, "Changing from 3rdpoc to interOP subscriber ");
                break;

            default:
                knLogger.debug(methodName, "Client Type passed is invalid ");
        } // switch end
        knLogger.info(methodName, "EXIT : Returning Response - ", etagMap);
        return affectedDispGrpMembers;
    }

    private KnCorpResponseDTO deleteDispatchGroupForDisSubscriber(String dispatchMdn, int corpId, String
            xdmsHomePttId, KnCorpInOutParamDTO corpInOutParamDTO, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap, HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "deleteDispatchGroupForDisSubscriber(mdnList, corpId, xdmsHomePttId, persisterTxn)";
        knLogger.info(methodName, "ENTRY : dispatchMdnd - ", KnGDPRTemplate.mdn(dispatchMdn), ", corpId - ", corpId);

        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        //Collection<String> contactMDNs;
        Set<String> groupFleetMembers = new HashSet<String>();
        Collection<String> mdnList = new ArrayList<String>();
        mdnList.add(dispatchMdn);
        //get all group ids where this mdn is member in the corporation.
        Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupList(mdnList, corpId,
                xdmsHomePttId, persisterTxn);
        Collection<Integer> groupIds = groupMemberMap.keySet();
        if (groupIds != null && !groupIds.isEmpty()) {
            Map<Integer, Integer> groupEtagMap = new HashMap<Integer, Integer>();//groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            ArrayList<KnCorpGroupDTO> grpsBasicInfo = groupInfoUtil.getGrpsNameEtagInfo(groupIds, xdmsHomePttId, persisterTxn);
            for (KnCorpGroupDTO groupData : grpsBasicInfo) {
                groupEtagMap.put(groupData.getGroupId(), groupData.getETag());
                groupNameMap.put(groupData.getGroupId(), (String.valueOf(groupData.getCorpId()).concat("_").concat(groupData.getGroupDisplayName())));
            }
            Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "groupDistList - ", groupDistList);
            etagMap = commonInfoUtil.formSubscriberNotification(groupDistList, groupEtagMap,
                    com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, null, null);

            knLogger.debug(methodName, "etagMap after prepare group noti - ", etagMap);
            Collection<String> groupDispatcher = new ArrayList<String>();
            groupDispatcher.add(dispatchMdn);

            for (int groupId : groupIds) {
                Collection<String> members = groupDistList.get(groupId);
                if (members != null && members.size() > 0) {
                    for (String mdn : members) {
                        groupFleetMembers.add(mdn);
                    }
                }
            }
        }
        knLogger.debug(methodName, "groupFleetMembers 1 - ", groupFleetMembers);
        groupFleetMembers.remove(dispatchMdn);
        knLogger.debug(methodName, "groupFleetMembers 1 - ", groupFleetMembers, "etagMap -", etagMap);

        etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(groupFleetMembers, null, etagMap, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "etagMap afterupdateDistinctSubcribersDirectory - ", etagMap);
        // delete all groups
        groupInfoUtil.deleteAllGroups(groupIds, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
        etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
        etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
        respDTO.setDisabledDispatchMemList(groupFleetMembers);
        respDTO.setChangeLogMap(etagMap);
        // deasign the contact association
        // de-assinging contact is not required in case of delete/update dispatch subscriber
        // deAssignContact(groupFleetMembers, groupDispatcher, etagMap, xdmsHomePttId);

        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    private Map<String, KnOPDirChgDTO> deleteExtContactDetailInCorp(KnIPCorpContactDTO contactDTO, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap, String xdmsHomePttId, KnCorpInOutParamDTO inOutParamDTO,
                                                                    HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "deleteExtContactDetailInCorp()";
        KnIPCorpContactDTO contactDTONewObj = contactDTO;
        String mdn = contactDTO.getMdn();
        int newCorpId = contactDTO.getNewCorpId();
        int corpId = contactDTO.getCorpId();
        if (newCorpId != corpId) {
            contactDTONewObj.setCorpId(newCorpId);
            corpId = newCorpId;
        }
        boolean subscExists = contactInfoUtil.checkExternalContactExists(contactDTONewObj, corpId, xdmsHomePttId, persisterTxn);
        LinkedList<String> requestMdnList = new LinkedList<String>();
        requestMdnList.add(mdn);
        knLogger.debug(methodName, "requestMdnList - ", KnGDPRTemplate.mdnList(requestMdnList));
        knLogger.debug(methodName, "subscExists - ", subscExists);
        if (subscExists) {
            //removing the mdn external data for the corporate
            Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.getSubcriberSublistMemberShipList(
                    requestMdnList, corpId, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "sublistMemberMap - ", sublistMemberMap);

            Collection<Integer> sublistLists = sublistMemberMap.keySet();
            Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupList(requestMdnList,
                    corpId, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "sublistLists - ", sublistLists);
            Collection<Integer> groupIds = groupMemberMap.keySet();

            Collection<Integer> sublistList = sublistMemberMap.keySet();
            Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(sublistList,
                    xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
            if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
                sublistInfoUtil.fetchAndUpdateSublistEtag(sublistLists, xdmsHomePttId, persisterTxn);
            }
            /* if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
                groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            }*/
            Set<String> contactMemberList = new HashSet<String>();
            if (sublistList != null && !sublistList.isEmpty()) {
                for (int sublistId : sublistList) {
                    Collection<KnCorpSubscriberDTO> mdnList = contactMemberMap.get(sublistId);
                    if (mdnList != null && !mdnList.isEmpty()) {
                        for (KnCorpSubscriberDTO corpSubscriberDTO : mdnList) {
                            String mdnStr = corpSubscriberDTO.getMdn();
                            if (!requestMdnList.contains(mdnStr)) {
                                contactMemberList.add(mdnStr);
                            }
                        }
                    }
                }
            }
            LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
            LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus = null;
            LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
            if (contactMemberList != null && !contactMemberList.isEmpty()) {
                for (String contactMdn : contactMemberList) {
                    removeMdnListMap.put(contactMdn, requestMdnList);
                }
                delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);

                etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMemberList,
                        xdmsHomePttId, etagMap, persisterTxn);
            }
            LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
            Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
            if (groupIds != null && !groupIds.isEmpty()) {
                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
                for (int groupId : groupIds) {
                    removeGrpMdnListMap.put(groupId, requestMdnList);
                }
                delGrpMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
                //Updating the group member counts
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistLists, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
                //Get the group member count and group type for the groupIds and set these values into etag Map
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIds, xdmsHomePttId, persisterTxn);

                groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
                //Determine the deleted and the modified groups
                Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(groupIds, xdmsHomePttId, persisterTxn);
                // Delete corp groups with < 2 members
                if (groupListStatus.get(KnConstants.DELETED) != null) {
                    Set<Integer> delGroupIdList = groupListStatus.get(KnConstants.DELETED).keySet();
                    if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                        Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();

                        for (Integer grpId : delGroupIdList) {
                            Collection<String> delMembersList = groupDistList.get(grpId);
                            deletedMembersMap.put(grpId, delMembersList);
                            //for the contact added to update the directory
                            contactMemberList.addAll(delMembersList);
                        }
                        groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, inOutParamDTO, persisterTxn);
                        etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                        etagMap = commonInfoUtil.formTGSCDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                        etagMap = commonInfoUtil.formTGSSDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                        groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                    }
                }
                if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                    Set<Integer> modGroupIdList = groupListStatus.get(KnConstants.MODIFIED).keySet();
                    if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                        Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                        for (Integer grpId : modGroupIdList) {
                            Collection<String> modMembersList = groupDistList.get(grpId);
                            modifiedMembersMap.put(grpId, modMembersList);
                            //for the contact added to update the directory
                            contactMemberList.addAll(modMembersList);
                        }
                        etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                                DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                        groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                    }
                }
            }

            Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
            ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);
            ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(sharedSublists, xdmsHomePttId, persisterTxn);
            if (emptySublist != null && !emptySublist.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
                KnCorpProfileDTO corpProfileInfo = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, true, persisterTxn);
                if (emptySublist.contains(corpProfileInfo.getPairedContactListId())) {
                    sublistInfoUtil.updateCorpPairedContListId(corpId, 0, xdmsHomePttId, persisterTxn);
                }
            }

            if (!contactMemberList.isEmpty()) {
                /*etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMemberList, groupIds, etagMap, xdmsHomePttId,
                persisterTxn);*/
                Set<String> memberList = new HashSet<String>();
                memberList.addAll(contactMemberList);
                //get the group members from the distribution table since we need not get the external subscriber
                /*  Map<Integer, Collection<String>> groupMemberListMap = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
                for (int groupId : groupMemberListMap.keySet()) {
                    Collection<String> groupMemberList = groupMemberListMap.get(groupId);
                    for (String memMdn : groupMemberList) {
                        memberList.add(memMdn);
                    }
                }*/
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(memberList, null, etagMap, xdmsHomePttId, persisterTxn);
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistLists, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            }
            contactInfoUtil.deleteExtMember(requestMdnList, corpId, xdmsHomePttId, persisterTxn);
            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap, null, null, removeGrpMdnListMap, null, null, groupDistList, delContactStatus, delGrpMemStatus, null);
            knLogger.debug(methodName, "etagMap ", etagMap);
            etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap after auth mapping modification- ", etagMap);
        }
        knLogger.info(methodName, "EXIT : Returning Response - ");
        return etagMap;
    }

    public Map<String, KnOPDirChgDTO> updateSubscriberName(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnException {
        String methodName = "updateSubscriberName(KnIPCorpContactDTO, KnPersisterTxn)";
        //Step:
        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
        }
        KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + subsProfile.getCorpId(), CORP_PROFILE, false, persisterTxn);
        String xdmsHomePttId = subsProfile.getXdmsHome();
        //use the xdms home pttServerId from Corp profile
        knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);

        //Subscriber mdns where mdn belongs as a contact
        Collection<String> subscriberList = contactInfoUtil.getContactMDNs(mdn, xdmsHomePttId, persisterTxn);
        Collection<String> contactMDNs = contactInfoUtil.getPocSubscribersDetails(subscriberList, contactDTO.getCorpId(), xdmsHomePttId, persisterTxn);
        if (contactMDNs != null && !contactMDNs.isEmpty()) {
            etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, etagMap, persisterTxn);
            knLogger.debug(methodName, "etagMap after updateSubcribersResourceListIndexDoc - ", etagMap);
        }
        //Retrieving groups where mdn is part of
        Collection<KnCorpGroupInfoPersistDTO> groupList = groupInfoUtil.getSubsGroupListForXcap(contactDTO, 0,
                xdmsHomePttId, persisterTxn);
        Collection<Integer> groupIds = new ArrayList<Integer>();
        for (KnCorpGroupInfoPersistDTO groupInfoPersistDTO : groupList) {
            groupIds.add(groupInfoPersistDTO.getGroupId());
        }
        Map<Integer, Collection<String>> groupDistInfo;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = new HashMap<Integer, Collection<KnCorpGroupMemberDTO>>();
        if (!groupIds.isEmpty()) {
            Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);
            groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            //Start --- INT-19198
            groupMemberMap.entrySet().stream().forEach(entry->entry.getValue().stream().forEach(groupMemberDTO->{
                groupMemberDTO.setSupervisory(-1);
                groupMemberDTO.setLocWatcher(-1);
                groupMemberDTO.setUa(null);
            }));
            //end --- INT-19198
            /*
             Get the group member count and group type for the groupIds and set these values into etag Map
             */
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIds, xdmsHomePttId, persisterTxn);

            groupDistInfo = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
            etagMap = commonInfoUtil.formSubscriberNotification(groupDistInfo, groupEtagMap,
                    DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
        }
        contactMDNs.add(mdn);
        if ((!contactMDNs.isEmpty()) || (!groupIds.isEmpty())) {
            knLogger.info(methodName, "Updating Directory Etags");
            contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, groupIds, new HashMap<>(), xdmsHomePttId, persisterTxn);
        }
        contactMDNs.remove(mdn);

        KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
        subsc.setMdn(contactDTO.getMdn());
        subsc.setName(contactDTO.getName());
        Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        Collection<KnCorpSubscriberDTO> corpSubscriberList = new ArrayList<KnCorpSubscriberDTO>();
        corpSubscriberList.add(subsc);

        for (String contact : contactMDNs) {
            modifiedContactMap.put(contact, corpSubscriberList);
        }

        for (Map.Entry<Integer, Collection<KnCorpGroupMemberDTO>> entry : groupMemberMap.entrySet()) {
            Collection<KnCorpGroupMemberDTO> groupMember = entry.getValue();
            for (KnCorpGroupMemberDTO subscriber : groupMember) {
                subscriber.setName(contactDTO.getName());
            }
        }
        int max_etag_update_notification = genInfoUtil.getXdmMaxNotificationCount();
        if (etagMap.size() <= max_etag_update_notification)
        {
            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, modifiedContactMap, groupMemberMap);
        }
        //If the corporate is already linked i.e linked gateway key is not null get the refernce id
        if (corpProfile.getLinkedGwKey() != null) {
            //get the refernce id from the account info table.
            KnCorpGWLinkedAccountInfoDTO accountInfo = commonInfoUtil.getCorporateLinkedAccountInfo(corpProfile.getLinkedGwKey(), xdmsHomePttId, persisterTxn);
            //update etag in the account info table
            //update etag
            Map<String, String> asyncInput = new HashMap<>();
            asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, xdmsHomePttId);
            asyncInput.put(KnDbSyncFwConstants.NNI_REF_ID, accountInfo.getNniRefId());
            knLogger.debug(methodName, " Async inputs", asyncInput);
            KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
            int serviceType = KnDbSyncFwConstants.EXECUTOR.ETAG_UPDATE_NNI.value();
            collector.collect(serviceType, asyncInput);
        }
        return etagMap;
    }

    /**
     * This method returns the unused MDN list in a corporation for a PAM account id.
     * returns failure is free MDN is less then required MDN count.
     *
     * @param corpPAMSubsDTO
     * @param persisterTxn
     * @return
     */

    public KnCorpPAMSubsDTO getUnusedSubsList(KnIPCorpPAMSubsDTO corpPAMSubsDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getUnusedSubsList(KnIPCorpPAMSubsDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpPAMSubsDTO - ", corpPAMSubsDTO);
        KnCorpPAMSubsDTO respDTO = new KnCorpPAMSubsDTO();
        try {
            int corpId = commonInfoUtil.getCorpId(corpPAMSubsDTO.getExtCorpId(), persisterTxn);
            int clientType = corpPAMSubsDTO.getClientType();
            int pamAccId = corpPAMSubsDTO.getPamAccId();
            int mdnCount = corpPAMSubsDTO.getUnUsedMdnCount();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            String xdmHome = corpProfile.getXdmsHome();
            List<String> mdnListWithNoGrps = contactInfoUtil.getCorpSubsWithNoGrps(pamAccId, clientType, xdmHome, persisterTxn);
            List<String> mdnListWithNoConts = contactInfoUtil.getCorpSubsWithNoConts(pamAccId, clientType, xdmHome, persisterTxn);
            List<String> freeMDNList = new ArrayList<String>(mdnListWithNoGrps);
            freeMDNList.retainAll(mdnListWithNoConts);
            if (freeMDNList.size() > mdnCount) {
                freeMDNList = freeMDNList.subList(0, mdnCount);
            }
            knLogger.debug(methodName, "freeMDNList size ", freeMDNList.size());
            respDTO.setFreePAMSubsList(freeMDNList);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured in getUnusedSubsList  ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured in getUnusedSubsList ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpResponseDTO cleanCorpData(KnIPCorpPAMSubsDTO corpPAMSubsDTO, KnPersisterTxn persisterTxn) {
        String methodName = "cleanCorpData(KnIPCorpPAMSubsDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpPAMSubsDTO - ", corpPAMSubsDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
            int corpId = commonInfoUtil.getCorpId(corpPAMSubsDTO.getExtCorpId(), persisterTxn);
            List<String> mdnList = corpPAMSubsDTO.getCleanUpMdnLst();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            String xdmHome = corpProfile.getXdmsHome();

            //Get all the MDN who has any one of deleting MDN list as contact
            Map<String, List<String>> subsCantLstMap = contactInfoUtil.getSubsContactList(mdnList, xdmHome, persisterTxn);
            knLogger.debug(methodName, "subsCantLstMap - ", KnGDPRTemplate.mapKeyMdn(subsCantLstMap));
            //contChngedMdnList is the MDN list whose contact list is getting change.
            List<String> ownerMdnList = new ArrayList<String>(subsCantLstMap.keySet());
            knLogger.debug(methodName, "ownerMdnList - ", KnGDPRTemplate.mdnList(ownerMdnList));
            //Get the private contact list id of the deleting mdn list
            Map<String, Integer> pvtContLstMap = contactInfoUtil.getSubscribersPrivateListId(mdnList, xdmHome, persisterTxn);
            pvtContLstMap = contactInfoUtil.removeDummyEntry(pvtContLstMap);
            knLogger.debug(methodName, "pvtContLstMap - ", pvtContLstMap);
            List<Integer> pvtContLstIdList = new ArrayList<Integer>(pvtContLstMap.values());
            //Get the list of Sublists where any one of deleting MDN exist.
            Map<Integer, List<String>> subsSublistMap = sublistInfoUtil.getSubsSublistList(mdnList, xdmHome, persisterTxn);
            knLogger.debug(methodName, "subsSublistMap - ", subsSublistMap);
            List<Integer> allSubListList = new ArrayList<Integer>(subsSublistMap.keySet());

            //Get the list of group ids where any one of deleting subscribers exist.
            Map<Integer, List<KnCorpGrpMemListDTO>> subsGrouplistMap = groupInfoUtil.getSubsGroupIdListMap(mdnList, xdmHome, persisterTxn);
            knLogger.debug(methodName, "subsGrouplistMap - ", subsGrouplistMap);
            List<Integer> dispatchGrpList = groupInfoUtil.getDispatchGroups(subsGrouplistMap);
            knLogger.debug(methodName, "dispatchGrpList - ", dispatchGrpList);
            List<Integer> allGroupList = new ArrayList<Integer>(subsGrouplistMap.keySet());
            Map<Integer, List<String>> deletedGrpMemMap = groupInfoUtil.getDeleteGrpMemMap(subsGrouplistMap);

            knLogger.debug(methodName, "Deleting corporate data");
            //Delete corporate data for deleting subscribers.
            //Delete private contact list of deleting subscribers
            if (!pvtContLstIdList.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(pvtContLstIdList, xdmHome, persisterTxn);
            }
            //Deleting from corp list member
            sublistInfoUtil.deleteCorpSublistMemList(mdnList, xdmHome, persisterTxn);
            //Deleting from group member list
            groupInfoUtil.deleteGrpMemList(mdnList, xdmHome, persisterTxn);
            //Deleting from corp contact list
            contactInfoUtil.deleteCorpContactList(mdnList, xdmHome, persisterTxn);
            //Updating the subscribers contact count where deleting MDNs exist as contact and the group member count.
            sublistInfoUtil.updateSublistsSubscribersContactCount(allSubListList, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            //Deleting the deleting subscribers contact count.
            contactInfoUtil.updateSubscribersContactCount(mdnList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmHome, persisterTxn);
            //Deleting from group dist info
            groupInfoUtil.deleteGrpDistList(mdnList, xdmHome, persisterTxn);
            //Get shared sublist from allSublistList
            List<Integer> sharedSubLists = sublistInfoUtil.getSharedSublistFromList(allSubListList, xdmHome, persisterTxn);
            //Update etag of shared sublist
            sublistInfoUtil.fetchAndUpdateSublistEtag(sharedSubLists, xdmHome, persisterTxn);
            //Get empty sublists
            List<Integer> emptySublistList = sublistInfoUtil.getEmptySublistIds(sharedSubLists, xdmHome, persisterTxn);
            //Deleting from group list ref
            groupInfoUtil.deleteCorpListDistGroupReference(emptySublistList, xdmHome, persisterTxn);
            //Deleting from corp list dist info
            sublistInfoUtil.deleteCorpListDistribution(emptySublistList, mdnList, xdmHome, persisterTxn);
            //Deleting from corp list info
            sublistInfoUtil.deleteSublistInfoList(emptySublistList, xdmHome, persisterTxn);
            //Get the list of corpId where mdnList exist as external contacts
            List<Integer> mdnListExtCorpIdLst = contactInfoUtil.getCorpIdList(mdnList, xdmHome, persisterTxn);
            //Deleting from external contact table.
            contactInfoUtil.deleteSusbcribersFromExtContactTables(mdnList, xdmHome, persisterTxn);
            //Update the corporate etag for corpIds where mdnList exist as external contact.
            contactInfoUtil.updateCorporateEtagForIdList(mdnListExtCorpIdLst, xdmHome, persisterTxn);
            //Updating the resource list etag for ownerMdnList. Removing mdnList from ownerMdnList if exist
            ownerMdnList.removeAll(mdnList);
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(ownerMdnList, xdmHome, null, persisterTxn);
            //uniqueMdnList used for updating the directory etag.
            Set<String> uniqueMdnList = new HashSet<String>(ownerMdnList);
            if (!subsGrouplistMap.isEmpty()) {
                /*
                 Get the group member count and group type for the groupIds and set these values into etag Map
                 */
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(subsGrouplistMap.keySet(), xdmHome, persisterTxn);

                //Get group status whose member count is less then 2
                Map<String, HashMap<Integer, String>> groupStatusMap = groupInfoUtil.getGroupListStatus(allGroupList, xdmHome, persisterTxn);
                knLogger.debug(methodName, "groupStatusMap - ", KnGDPRTemplate.mapKeyMdn(groupStatusMap));
                //Update and get the group etag.
                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(subsGrouplistMap.keySet(), xdmHome, persisterTxn);
                //Get the current group distribution list.
                Map<Integer, Collection<String>> grpDistMap = groupInfoUtil.getGroupSubscriberDistList(subsGrouplistMap.keySet(), xdmHome, persisterTxn);
                knLogger.debug(methodName, "grpDistMap - ", grpDistMap);
                if (groupStatusMap.get(KnConstants.MODIFIED) != null) {
                    Set<Integer> modGroupIdList = groupStatusMap.get(KnConstants.MODIFIED).keySet();
                    if (null != modGroupIdList && !modGroupIdList.isEmpty()) {
                        Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();
                        for (Integer grpId : modGroupIdList) {
                            if (grpDistMap.get(grpId) != null) {
                                List<String> modMembersList = new ArrayList<String>(grpDistMap.get(grpId));
                                modifiedMembersMap.put(grpId, modMembersList);
                                //for the contact added to update the directory
                                uniqueMdnList.addAll(modMembersList);
                            }
                        }
                        etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                        knLogger.debug(methodName, "etagMap for modified groups - ", etagMap);
                    }
                }
                if (groupStatusMap.get(KnConstants.DELETED) != null) {
                    Set<Integer> delGroupIdList = groupStatusMap.get(KnConstants.DELETED).keySet();
                    if (null != delGroupIdList && !delGroupIdList.isEmpty()) {
                        delGroupIdList.addAll(dispatchGrpList);
                        Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
                        for (Integer grpId : delGroupIdList) {
                            if (null != grpDistMap.get(grpId)) {
                                List<String> delMembersList = new ArrayList<String>(grpDistMap.get(grpId));
                                deletedMembersMap.put(grpId, delMembersList);
                                //for the contact added to update the directory
                                uniqueMdnList.addAll(delMembersList);
                            }
                        }
                        groupInfoUtil.deleteAllGroups(delGroupIdList, xdmHome, corpInOutParamDTO, persisterTxn);
                        etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                        etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmHome, persisterTxn);
                        etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmHome, persisterTxn);
                        knLogger.debug(methodName, "etagMap with deleted groups - ", etagMap);
                    }
                }
            }

            //Remove mdnList from uniqueMdnList before updating the directory etag as notification is not being sent to mdnList.
            uniqueMdnList.removeAll(mdnList);
            if (!uniqueMdnList.isEmpty()) {
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(uniqueMdnList, null, etagMap, xdmHome,
                        persisterTxn);
            }
            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotiRemovedMembers(etagMap, subsCantLstMap, deletedGrpMemMap);
            knLogger.debug(methodName, "etagMap with xcap doc diff - ", etagMap);
            respDTO.setChangeLogMap(etagMap);
            respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured in cleanCorpData  ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured in cleanCorpData ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO modifySubsCorpFeature(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "modifySubsCorpFeature(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        //Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
        try {

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(subsProvInfoDTO.getCorpId(), CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            Integer mode = 0;
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = subsProvInfoDTO.getCustomParamMap();
            if (subsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(subsProvInfoDTO.getMdn()), customParams, xdmsHome, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                subsProvInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.MODIFY_SUBS_CORP_FEATURE);
                hookIPDTO.setData(subsProvInfoDTO);
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
            KnCorpTGSPersistDTO corpTGSPersistDTO = new KnCorpTGSPersistDTO();
            corpTGSPersistDTO.setInputDTO(subsProvInfoDTO);

            KnCorpSubscriberDTO corpSubscriberDTO = contactInfoUtil.getSubscriberDetail(subsProvInfoDTO.getMdn(), xdmsHome, persisterTxn);

            // Validate if TGS Server feature bit is enabled.(Subscriber TGS bit), Rule: KnSubsTGSServerBitValidation
            boolean tgsServerBit = KnGeneralUtil.getFeatureBitValue(corpSubscriberDTO.getSubsActiveFS2(), 20);
            corpTGSPersistDTO.setTGSSerBitEanble(tgsServerBit);

            corpTGSPersistDTO.setSubsCorpId(corpSubscriberDTO.getCorpId());

            // Validate if MDN's Client Type is not Inter-op Or Dispatch
            corpTGSPersistDTO.setSubsClientType(corpSubscriberDTO.getClientType());
            // Calling Validation Framework for DTO validation
            knLogger.debug(methodName, "Before Validation", corpTGSPersistDTO);
            validatorFW.validate(corpTGSPersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();

            boolean tgscClientBitRq = KnGeneralUtil.getFeatureBitValue(subsProvInfoDTO.getSubFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value());
            BitSet corpAdminBitSet = featureSetUtil.convertHexStringToBitSet(corpSubscriberDTO.getCorpAdminFS2());
            if (tgscClientBitRq) {
                //enable 28th bit in db value
                corpAdminBitSet.set(com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value(), true);
            } else {
                // disable 28th in db value
                corpAdminBitSet.set(com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value(), false);
            }
            String corpAdminFSReq = featureSetUtil.convertBitSetToHexString(corpAdminBitSet);
            knLogger.debug(methodName, "corpAdminFSReq - ", corpAdminFSReq);

            // Recalculate Active Feature Set
            int clientPVMajorVersion = corpSubscriberDTO.getClientPVmajorVer();
            String xdmsPttServerId = corpSubscriberDTO.getXdmsHome();
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmsPttServerId, clientPVMajorVersion);
            String xdmsFS2 = corpSubscriberDTO.getXdmsFs2();
            String userProfileFS2 = corpSubscriberDTO.getUserProfileFS2();
            if(userProfileFS2==null)
            {
            	userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
            }
            String activeFs2 = featureSetUtil.generateActiveFeatBitSet(corpSubscriberDTO.getPocHome(), corpSubscriberDTO.getPresenceHome(),
                    corpSubscriberDTO.getXdmsHome(), corpSubscriberDTO.getClientFs2(), corpSubscriberDTO.getSubscriberFs2(), corpProfile.getCorpFS2(),
                    corpSubscriberDTO.getOpsFs2(), corpAdminFSReq, clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
            corpSubscriberDTO.setMdn(subsProvInfoDTO.getMdn());
            corpSubscriberDTO.setCorpAdminFS2(corpAdminFSReq);
            corpSubscriberDTO.setLastProfileUpdateTime(System.currentTimeMillis());
            corpSubscriberDTO.setSubsActiveFS2(activeFs2);
            //Update CORPADMINFS1, ACTIVEFS1 and LASTPROFILEUPDATETIME in DG.POCSUBSCRIBERINFO
            corpSubsProvInfoUtil.modifySubsCorpFeatureSet(corpSubscriberDTO, xdmsHome, persisterTxn);
            boolean tgscClientBit = KnGeneralUtil.getFeatureBitValue(activeFs2, 28);

            // Send SEH notification when client feature Bit (28) is disabled
            if (tgscClientBit == false) {
                // Invoke Delete TGSs List
                groupInfoUtil.cleanUpSubsCampedGrps(subsProvInfoDTO.getMdn(), xdmsHome, persisterTxn);
                /*etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(subsProvInfoDTO.getMdn(), etagMap, xdmsHome, persisterTxn);
                respDTO.setChangeLogMap(etagMap);*/
                Map<String, KnTGSModeChgDTO> tgsModeChgMap = new HashMap<String, KnTGSModeChgDTO>();
                KnTGSModeChgDTO value = new KnTGSModeChgDTO();
                value.setPocHome(corpSubscriberDTO.getPocHome());
                value.setPresenceHome(corpSubscriberDTO.getPresenceHome());
                value.setTgsMode(mode);
                tgsModeChgMap.put(corpSubscriberDTO.getMdn(), value);
                respDTO.setTgsModeChgMap(tgsModeChgMap);
            }


            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in modifySubsCorpFeature  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in modifySubsCorpFeature ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpProfileInfoRespDTO getCorporateProfile(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getCorporateProfile(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn)";
        KnCorpProfileInfoRespDTO respDTO = new KnCorpProfileInfoRespDTO();
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(subsProvInfoDTO.getCorpId(), CORP_PROFILE, false, persisterTxn);
            respDTO.setCorpId(String.valueOf(corpProfile.getCorpId()));
            respDTO.setExtCorpId(corpProfile.getExtCorpId().trim());
            if(corpProfile.getNetworkName() != null){
                respDTO.setCorpName(corpProfile.getNetworkName().trim());
            }
            respDTO.setMaxTextMsgSize(String.valueOf(corpProfile.getMaxTextMsgSize()));
            respDTO.setMaxMmmsgSizeCell(String.valueOf(corpProfile.getMaxMmmsgSizeCell()));
            respDTO.setMaxMmmsgSizeWifi(String.valueOf(corpProfile.getMaxMmmsgSizeWifi()));
            respDTO.setDeliveryReceiptFlag(String.valueOf(corpProfile.getDeliveryReceiptFlag()));
            respDTO.setReadReportFlag(String.valueOf(corpProfile.getReadReportFlag()));
            respDTO.setMsgTtl(String.valueOf(corpProfile.getMsgTtl()));
            respDTO.setMaxPredefinedMsgCnt(String.valueOf(corpProfile.getMaxPredefinedMsgCnt()));
            respDTO.setMaxPredefinedTmpltCnt(String.valueOf(corpProfile.getMaxPredefinedTmpltCnt()));
            respDTO.setMaxUserDefinedMsgCnt(String.valueOf(corpProfile.getMaxUserDefinedMsgCnt()));
            respDTO.setFleetMemberGeoTagFlag(String.valueOf(corpProfile.getFleetMemberGeoTagFlag()));
            respDTO.setMaxAbdgTalkGroup(String.valueOf(corpProfile.getMaxAbdgTalkGrp()));
            respDTO.setMaxLrGabTalkGroup(String.valueOf(corpProfile.getMaxLrGabTalkGroup()));
            respDTO.setMaxUsrLrGabGroup(String.valueOf(corpProfile.getMaxUsrLrGabGroup()));
            respDTO.setMaxAbdgGrpPerOwner(String.valueOf(corpProfile.getMaxAbdgPerGrpOwner()));
            respDTO.setMaxAbdgGrpPerMem(String.valueOf(corpProfile.getMaxAbdgPerGrpMember()));
            respDTO.setLastProfileUpdateTime(String.valueOf(corpProfile.getProfileLastUpdated()));
            respDTO.setLmrIntropFlag(corpProfile.getLmrIntropFlag());

            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(xdmPttServerId);
            int corpId= Integer.parseInt(subsProvInfoDTO.getCorpId());
            KnCorpConfigInfoDto corpConfigInfoDto = xdmDAO.selectCorpConfigureInfo(corpId, UGWINTEROP, true, persisterTxn);
            if(corpConfigInfoDto != null && corpConfigInfoDto.getParamValue() != null)
            respDTO.setUgwInteropFlag(Integer.valueOf(corpConfigInfoDto.getParamValue()));
            respDTO.setXdmCorpFs2Set(corpProfile.getXdmCorpFS2Set());
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getCorporateProfile  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getCorporateProfile ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    private KnCorpResponseDTO switchConvergedClient(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnException {
        String methodName = "switchConvergedClient(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
        }
        String xdmsHomePttId = subsProfile.getXdmsHome();
        knLogger.debug(methodName, "xdmsHomePttId - ", xdmsHomePttId);
        //Subscriber mdns where mdn belongs as a contact
        Collection<String> subscriberList = contactInfoUtil.getContactMDNs(mdn, xdmsHomePttId, persisterTxn);
        Collection<String> contactMDNs = contactInfoUtil.getPocSubscribersDetails(subscriberList, contactDTO.getCorpId(), xdmsHomePttId, persisterTxn);
        /**
         * INT-95992 - Optimization for switch convergent client
         *           1.Etag update-> The Etag update (XDM_DIRECTORY,CORPGROUPINFO,XDM_CORPRESOURCELISTINDEXDOC) is already handled by Etag-management thread.
         *           So avoiding for duplicate update.
         * 		    2.Notification -> Self notification is not disturbed. The belonging contact and supervisory group notification is cut down. Now on client refresh, the update clienttype data will be populated
         */
      /* if (subscriberList != null && !subscriberList.isEmpty()) {
           etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(subscriberList, xdmsHomePttId, etagMap, persisterTxn);
            knLogger.debug(methodName, "etagMap after updateSubcribersResourceListIndexDoc - ", KnGDPRTemplate.mapKeyMdn(etagMap));
        } */
        //Retrieving groups where mdn is part of
        Collection<KnCorpGroupInfoPersistDTO> groupList = groupInfoUtil.getSubsGroupListForXcap(contactDTO, 0,
                xdmsHomePttId, persisterTxn);
        Collection<Integer> groupIds = new ArrayList<Integer>();
        for (KnCorpGroupInfoPersistDTO groupInfoPersistDTO : groupList) {
            groupIds.add(groupInfoPersistDTO.getGroupId());
        }
        Map<Integer, Collection<String>> groupDistInfo;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = new HashMap<Integer, Collection<KnCorpGroupMemberDTO>>();
        /**
         * INT-95992 - Optimization for switch convergent client
         *           1.Etag update-> The Etag update (XDM_DIRECTORY,CORPGROUPINFO,XDM_CORPRESOURCELISTINDEXDOC) is already handled by Etag-management thread.
         *           So avoiding for duplicate update.
         * 		  2.Notification -> Self notification is not disturbed. The belonging contact and supervisory group notification is cut down. Now on client refresh, the update clienttype data will be populated
         */
      /* if (!groupIds.isEmpty()) {
            Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);
            groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList, xdmsHomePttId, persisterTxn);
            *//*
             Get the group member count and group type for the groupIds and set these values into etag Map
             *//*
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIds, xdmsHomePttId, persisterTxn);
            groupDistInfo = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
            etagMap = commonInfoUtil.formSubscriberNotification(groupDistInfo, groupEtagMap,
                    DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
        }*/
        /*subscriberList.add(mdn);
        if ((!subscriberList.isEmpty()) || (!groupIds.isEmpty())) {
            knLogger.info(methodName, "Updating Directory Etags");
           etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(subscriberList, groupIds, etagMap, xdmsHomePttId, persisterTxn);
        }
        subscriberList.remove(mdn);*/
        KnCorpSubscriberDTO subscInternal = new KnCorpSubscriberDTO();
        KnCorpSubscriberDTO subscExternal = new KnCorpSubscriberDTO();
        subscExternal.setMdn(contactDTO.getMdn());
        subscExternal.setName(subsProfile.getNetworkName());
        subscExternal.setClientType(contactDTO.getNewClientType());
        subscExternal.setContact_type(XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
        subscInternal.setMdn(contactDTO.getMdn());
        subscInternal.setName(subsProfile.getNetworkName());
        subscInternal.setClientType(contactDTO.getNewClientType());
        subscInternal.setUa(KnGeneralUtil.getUA(subsProfile.getUserAgent(), subsProfile.getClientMajorVersion()));
        Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        Collection<KnCorpSubscriberDTO> corpSubscriberListInternal = new ArrayList<KnCorpSubscriberDTO>();
        Collection<KnCorpSubscriberDTO> corpSubscriberListExternal = new ArrayList<KnCorpSubscriberDTO>();
        corpSubscriberListInternal.add(subscInternal);
        corpSubscriberListExternal.add(subscExternal);
        for (String contact : subscriberList) {
            if (contactMDNs.contains(contact)) {
                modifiedContactMap.put(contact, corpSubscriberListInternal);
            } else {
                modifiedContactMap.put(contact, corpSubscriberListExternal);
            }
        }

        for (Map.Entry<Integer, Collection<KnCorpGroupMemberDTO>> entry : groupMemberMap.entrySet()) {
            Collection<KnCorpGroupMemberDTO> groupMember = entry.getValue();
            for (KnCorpGroupMemberDTO subscriber : groupMember) {
                subscriber.setName(subsProfile.getNetworkName());
                subscriber.setClientType(contactDTO.getNewClientType());
                subscriber.setUa(KnGeneralUtil.getUA(subsProfile.getUserAgent(), subsProfile.getClientMajorVersion()));
            }
        }
        Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "subsAddlTGList - ", subsAddlTGList);
        if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
            if ((contactDTO.getClientType() == SUBSCR_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() && contactDTO.getNewClientType() == HANDSET.value()) ||
                    (contactDTO.getClientType() == SUBSCR_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() && contactDTO.getNewClientType() == CROSSCARRIER.value()) ||
                    (contactDTO.getClientType() == SUBSCR_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value() && contactDTO.getNewClientType() == POC_WIFIONLY.value())) {
                Collection<String> mdnList = new ArrayList<>();
                mdnList.add(mdn);
                groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);
                //groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);
                //etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);
                etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnList, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                    groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                }
                knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                    etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, null,
                            subsAddlTGList, groupCorpIdMap);
                    knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                }
            }
        }
        etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, modifiedContactMap, groupMemberMap);
        activationInfoUtil.deleteActivationCodeForMDN(mdn, xdmsHomePttId, persisterTxn);
        boolean tgscCleanUp = groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
        if(tgscCleanUp){
            etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
        }
        respDTO.setChangeLogMap(etagMap);
        knLogger.debug(methodName, "etagMap - ", etagMap);
        return respDTO;
    }


    /**
     * This method is used to Subscriber which is available in LI_TARGET_INFO table.
     *
     * @param persisterTxn
     * @return
     * @throws Exception
     */
    @Override
    public KnCorpLITargetInfoRespDTO getLITargetInfo(KnPersisterTxn persisterTxn) {
        String methodName = "getLITargetInfo(KnPersisterTxn persisterTxn)";
        KnCorpLITargetInfoRespDTO respDTO = new KnCorpLITargetInfoRespDTO();
        try {
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            Collection<KnCorpLITargetProfile> liTargetInfo = contactInfoUtil.getLITargetInfo(xdmPttServerId, persisterTxn);
            knLogger.debug(methodName, "liTargetInfo - ", liTargetInfo);
            respDTO.setLiTargetProfileList(liTargetInfo);
            populate(respDTO);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getLITargetInfo ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }


    /**
     * This method is used to detmine and calculate the configuration bit set's decimal equivalent.Refer to the ICD to get the
     * bit location information
     *
     * @param corpId
     * @param gwEnabled
     * @param profileInfoDto
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    private Long calculateConfigFeatureBit(int corpId, boolean gwEnabled, KnCorpProfileInfoDTO profileInfoDto, String xdmsHomePttId, KnPersisterTxn persisterTxn, int nxtGenCatAccess) throws Exception {
        String methodName = "calculateConfigFeatureBit";
        knLogger.debug(methodName, "Entry to the method ");
        Set<Integer> pamClientTypes = contactInfoUtil.getCorporatePamClientTypes(corpId, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "pamClientList - ", pamClientTypes);
        BitSet bits = new BitSet();
        int client_type;
        for (SUBSCR_CLIENT_TYPE subs_client_type : SUBSCR_CLIENT_TYPE.values()) {
            knLogger.debug(methodName, "client_type - ", subs_client_type);
            client_type = subs_client_type.value();
            KnClientTypeConfigDTO clientConfig = genInfoUtil.getClientTypeConfig(client_type, persisterTxn);
            if (client_type == SUBSCR_CLIENT_TYPE.MOBILEAPI.value()) {
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(9);
                }
            } else if (client_type <= SUBSCR_CLIENT_TYPE.PDVCONNECT.value()) {
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(client_type);
                }
            } else if (client_type == THIRDPARTYDISPATCHERCLIENT.value()) {
                //The client type 12 will be set as the 17 bit
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(17);
                }
            }
            //LMR client type changes-starts
            else if (client_type == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                //The client type 14 will be set as the 21 bit
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(21);
                }
            } else if (client_type == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) {
                //The client type 15 will be set as the 22 bit
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(22);
                }
            } else if (client_type == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                //The client type 16 will be set as the 23 bit
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(23);
                }
            } else if (client_type == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                //The client type 16 will be set as the 23 bit
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(31);
                }
            } else if (client_type == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.DGMDN.value()) {
                //client type 18 DGMDN type
                if (clientConfig.getIsEnable() == BIT_TRUE) {
                    bits.set(43);
                }
            }
            //LMR client type changes-ends
        }


        // Its only for the clients upto 11 from 12 client type onwards the bits
        // would get changed
        if (pamClientTypes != null && !pamClientTypes.isEmpty()) {
            for (int pamClient : pamClientTypes) {
                if (pamClient == SUBSCR_CLIENT_TYPE.MOBILEAPI.value()) {
                    if (bits.get(9)) {
                        knLogger.debug(methodName, "lmt to be enabled");
                        bits.set(13);
                        break;
                    }
                } else if (pamClient <= SUBS_CLIENT_TYPE.PDVCONNECT.value()) {
                    if (bits.get(pamClient)) {
                        knLogger.debug(methodName, "lmt to be enabled");
                        bits.set(13);
                        break;
                    }
                } else if (pamClient == THIRDPARTYDISPATCHERCLIENT.value()) {
                    if (bits.get(17)) {
                        knLogger.debug(methodName, "lmt to be enabled");
                        bits.set(13);
                        break;
                    }
                } else if (pamClient == SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                    if (bits.get(21)) {
                        bits.set(13);
                        break;
                    }
                } else if (pamClient == SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) {
                    if (bits.get(22)) {
                        bits.set(13);
                        break;
                    }
                } else if (pamClient == SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                    if (bits.get(23)) {
                        bits.set(13);
                        break;
                    }
                } else if (pamClient == SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                    if (bits.get(31)) {
                        bits.set(13);
                        break;
                    }
                }

            }
        }


        if (gwEnabled) {
            if (bits.get(7) || bits.get(8) || bits.get(31)) {
                bits.set(12);
            }
        }

        if (profileInfoDto.getSupervisoryOverrideEnabled() == 1) {
            bits.set(14);
        }

        if (profileInfoDto.getEnableTalkGroup() == 1) {
            bits.set(15);
        }
        if (profileInfoDto.getAllowSUContact() == 1) {
            bits.set(16);
        }

        if(nxtGenCatAccess==1)
            bits.set(20);

        if(profileInfoDto.getIsLocWatcher() == 1){
            bits.set(24);
        }
        if(profileInfoDto.getConvClientEnabled() == 1){
            bits.set(25);
        }
        if(profileInfoDto.getTextMsgFlag() == 1){
            bits.set(26);
        }
        if(profileInfoDto.getMultiMediaMsgFlag() == 1){
            bits.set(27);
        }
        if(profileInfoDto.getLocationMsgFlag() == 1){
            bits.set(28);
        }
        if(profileInfoDto.getWebDispatcherEnabled() == 1){
            bits.set(29);
        }
        if(profileInfoDto.getInteropLicenceType() == 1){
            bits.set(30);
        }
        if(profileInfoDto.getAmbientListening() == 1){
            bits.set(32);
        }
        if(profileInfoDto.getDiscreteListening() == 1){
            bits.set(33);
        }
        if(profileInfoDto.getUserCheck() == 1){
            bits.set(34);
        }
        if(profileInfoDto.getUserSvcCtrl() == 1){
            bits.set(35);
        }
        if(profileInfoDto.getEmergFeature() == 1){
            bits.set(36);
        }
        if(profileInfoDto.getLargeGrpSupport() == 1){
            bits.set(37);
        }
        if(profileInfoDto.getDeviceSharingFlag() == 1){
            bits.set(38);
        }
        if(profileInfoDto.getMcVideoEnabled() == 1 && KnGeneralUtil.getFeatureBitValue(profileInfoDto.getOpsCorpFs(), MCVIDEOTX.value())){
            bits.set(39);
        }
        if(profileInfoDto.getMcVideoUnCfrmPullEnabled() == 1 && KnGeneralUtil.getFeatureBitValue(profileInfoDto.getOpsCorpFs(), MCVIDEOUNCONFIRMEDPULL.value())){
            bits.set(40);
        }
        if(profileInfoDto.getOsmFeatureFlag() == 1){
            bits.set(41);
        }
        if(profileInfoDto.getAllowGroupAcrossZones() == 1){
            bits.set(42);
        }
        if(profileInfoDto.getUserProfileMgmt() == 1){
            bits.set(44);
        }
        if(profileInfoDto.getIsVLrgGrpEnabled()==1) {
            bits.set(45);
        }
        if(profileInfoDto.getGroupProfileMgmt()==1) {
            bits.set(46);
        }
        if(profileInfoDto.getGroupSharingFeature()==1) {
            bits.set(47);
        }
        if(profileInfoDto.getTrkMailSupported()==1) {
            bits.set(49);
        }
        if(profileInfoDto.getEmergDestAll()==1) {
            bits.set(51);
        }
        if(profileInfoDto.getUpmSharingFlag()==1) {
            bits.set(52);
        }
        
        String mcxGroupReGroupFlag = profileInfoDto.getMcxGroupReGroupFlag();
        knLogger.debug(methodName, " mcxGroupReGroupFlag- ", mcxGroupReGroupFlag);
        if( mcxGroupReGroupFlag != null && Integer.parseInt(mcxGroupReGroupFlag) == BIT_TRUE) {
            bits.set(53);
        }
        knLogger.debug(methodName, "CommonContactListSupport: "+profileInfoDto.getCommonContactListSupport());
        if(profileInfoDto.getCommonContactListSupport() == BIT_TRUE) {
            bits.set(54);
        }
        if(profileInfoDto.getPocGwSynEnable()==1) {
            bits.set(55);
        }
        knLogger.debug(methodName, "bits value - ", bits);
        Long bitsLongVal = KnCorpCommonInfoUtil.convertBitSetToLong(bits);
        knLogger.debug(methodName, " bitsLongVal - ", bitsLongVal);
        return bitsLongVal;
    }

    public Long calculateClientTypeFeatureBit(int corpId, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "calculateClientTypeFeatureBit()";
        BitSet bits = new BitSet();
        int client_type;
        for(SUBSCR_CLIENT_TYPE subs_client_type : SUBSCR_CLIENT_TYPE.values()){
            knLogger.debug(methodName, "client_type - ", subs_client_type);
            client_type = subs_client_type.value();
            knLogger.debug(methodName,"client_type :: ", client_type);
            KnClientTypeConfigDTO clientConfig = genInfoUtil.getClientTypeConfig(client_type, persisterTxn);
            knLogger.debug(methodName,"Client Type IsEnable :: ", clientConfig.getIsEnable(), "Client Type :: ", clientConfig.getClientType());
            if (clientConfig.getIsEnable() == BIT_TRUE) {
                bits.set(clientConfig.getClientType());
            }
        }
        /*knLogger.debug(methodName, "bits value - ", bits);
        long longValue = 1L;
        for (int i = 0; i <bits.size(); ++i) {
            longValue += bits.get(i) ? (1L << i) : 0L;
        }
        knLogger.debug(methodName, " bitsLongVal - ", longValue);
        return longValue;*/
        knLogger.debug(methodName, "bits value - ", bits);
        Long bitsLongVal = KnCorpCommonInfoUtil.convertBitSetToLong(bits);
        knLogger.debug(methodName, " bitsLongVal - ", bitsLongVal);
        return bitsLongVal;
    }

    @Override
    public void subsEtagUpdate(String mdn, String operationType) {
        String methodName = "subsEtagUpdate()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", KnGDPRTemplate.mdn(mdn));
        try {
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, true, null);
            knLogger.debug(methodName, "Subscriber Profile - ", subsProfile);
            String xdmsHomePttId = subsProfile.getXdmsHome();
            knLogger.debug(methodName, "xdmsHomePttId - ", xdmsHomePttId);
            contactInfoUtil.subsEtagUpdateUtil(subsProfile, subsProfile.getCorpId(),operationType,
                    xdmsHomePttId);
            knLogger.debug(methodName, "Successfully updated the eTag:");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating subs etag",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "EXIT: - ");
    }
    /**
     * This method deletes the corporate group data of the subscriber in its corporation
     *
     * @param contactDTO
     * @param subsProfile
     * @param persisterTxn
     * @param etagMap      @return
     */
    private KnCorpResponseDTO deleteSubscribersGroupData(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnCorpInOutParamDTO corpInOutParamDTO, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap, HashMap<Integer, String> groupNameMap) throws KnException {
        String methodName = "deleteSubscribersGroupData(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        Set<String> groupFleetMembers = new HashSet<String>();
        //Step:
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
        }
        int corpId = contactDTO.getCorpId();
        String xdmsHomePttId = subsProfile.getXdmsHome();
        knLogger.debug(methodName, "xdmsHomePttId - ", xdmsHomePttId);
        LinkedList<String> mdnList = new LinkedList<String>();
        mdnList.add(mdn);
        Map<String, Collection<String>> dispContactList = null;
        if (contactDTO.getClientType() == DISPATCH_CLIENT.value() || contactDTO.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
            dispContactList = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "Contacts -" + KnGDPRTemplate.mapKeyValueListMdn(dispContactList));
        }
        Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupList(mdnList, corpId,
                xdmsHomePttId, persisterTxn);

        Collection<KnCorpAddlTGInfoDTO> subsMdnAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "subsMdnAddlTGList - ", subsMdnAddlTGList);
        if(subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()){
            groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);
            //groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);
            //etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);
            etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap - ", etagMap);
            Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
            if (subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()) {
                groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsMdnAddlTGList, xdmsHomePttId, persisterTxn);
            }
            knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
            if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, null,
                        subsMdnAddlTGList, groupCorpIdMap);
                knLogger.debug(methodName, "etagMap AddlTG -diff - ", etagMap);
            }
        }
        //Get the Dispatch groups with name where the subscriber is dispatcher.
        Map<Integer, String> dispGrpNameMap = groupInfoUtil.getSupervisorGroupsAndName(mdn, KnConstants.GROUP_DISPATCHER, xdmsHomePttId, persisterTxn);
        List<Integer> dispatchGrpList = new ArrayList<>(dispGrpNameMap.keySet());
        //Get the all group Ids into groupIds variable and remove dispatch group Ids.
        //As dispatch group will be deleted and other will be modified.
        Set<Integer> grIds = groupMemberMap.keySet();
        Collection<Integer> groupIds = new ArrayList<Integer>(grIds);

        Collection<Integer> groupInfoUpdate  = new ArrayList<>();
        if(subsProfile.getClientType() == SUBSCR_CLIENT_TYPE.GROUPMDN.value() || subsProfile.getClientType() == SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()){
            Map<Integer, Integer> sgMDNCountDetailsMap = groupInfoUtil.getSGCorpGroupMembersCount(grIds, xdmsHomePttId, persisterTxn);
            if(sgMDNCountDetailsMap != null) {
                sgMDNCountDetailsMap.forEach((corpGroupId, SGMemberCount) -> {
                    if (SGMemberCount == 1) {
                        groupInfoUpdate.add(corpGroupId);
                    }
                });
            }
            groupInfoUtil.modifyGroupLmrInteropCapable(LMR_INTEROP_NON_CAPABLE, groupInfoUpdate, xdmsHomePttId, persisterTxn);
        }

        Collection<String> contactMDNs = new ArrayList<String>();
        LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
        LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
        LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
        Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
        Collection<String> disabledOdlMember = new HashSet<>();
        if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(groupIds, xdmsHomePttId, persisterTxn);
            Map<Integer, Collection<KnCorpAddlTGInfoDTO>> existingAddlTGList = new HashMap<>();
            Collection<KnCorpAddlTGInfoDTO> delAddlTGList = new ArrayList<>();
            // Additional Talk Group:
            for (KnCorpAddlTGInfoDTO addlTg : subsAddlTGList) {
                Collection<KnCorpAddlTGInfoDTO> existAddl = null;
                if (existingAddlTGList.get(addlTg.getGroupId()) != null) {
                    existAddl = existingAddlTGList.get(addlTg.getGroupId());
                    existAddl.add(addlTg);
                } else {
                    existAddl = new ArrayList<>();
                    existAddl.add(addlTg);
                    existingAddlTGList.put(addlTg.getGroupId(), existAddl);
                }
            }
            //delete members from the corpgroupmemberlist table
            Map<Integer, Map<String, Collection<String>>> memberDetailsList = new HashMap<Integer, Map<String, Collection<String>>>();
            for (int grpId : groupIds) {
                removeGrpMdnListMap.put(grpId, mdnList);
                Map<String, Collection<String>> memDelListMap = new HashMap<String, Collection<String>>();
                memDelListMap.put(KnConstants.DELTED_MEMBERS, mdnList);
                knLogger.info(methodName, "XX memDelListMap = ", memDelListMap);
                memberDetailsList.put(grpId, memDelListMap);
            }
            knLogger.info(methodName, "XX memberDetailsList - ", memberDetailsList);

            Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(groupIds, xdmsHomePttId, persisterTxn);
            for(KnCorpGroupMemberDTO grpMem : grpMemberDetails){
                disabledOdlMember.add(grpMem.getMdn());
            }
            knLogger.info(methodName, "XX grpMemberDetails - ", grpMemberDetails);
            delGroupMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
            groupInfoUtil.deleteFrmCorpGroupDistInfo(memberDetailsList, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Deleting members from Sublists.");
            knLogger.info(methodName, "Updating Group Etags");
            Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "Updating Subs Group Etags");
            //Determine the deleted and the modified groups
            Map<String, HashMap<Integer, String>> groupListStatus =
                    groupInfoUtil.getGroupListStatus(groupIds, xdmsHomePttId, persisterTxn);

            // Get Distribution list for dispatch groups and non-dispatch groups
            List<Integer> allGroupIdList = new ArrayList<>(groupIds);
            //allGroupIdList.addAll(dispatchGrpList);
            groupDistList = groupInfoUtil.getGroupSubscriberDistList(allGroupIdList, xdmsHomePttId, persisterTxn);
            //Adding fleet members from dispatch group into groupFleetMembers variable.
            for (int dispGrpId : dispatchGrpList) {
                if (null != groupDistList.get(dispGrpId)) {
                    groupFleetMembers.addAll(groupDistList.get(dispGrpId));
                }
            }
            //Get the group member count and group type for the groupIds and set these values into etag Map
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupMemberMap.keySet(), xdmsHomePttId, persisterTxn);
            Collection<Integer> ownerGroupIds = groupInfoUtil.getOwnerGroupIds(mdn, corpId, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "ownerGroupIds: ", ownerGroupIds);
            // Delete corp groups with < 2 members
            Set<Integer> delGroupIdList = new HashSet<>();
            if (groupListStatus.get(KnConstants.DELETED) != null || !ownerGroupIds.isEmpty()) {
               delGroupIdList = new HashSet<>();
                if (groupListStatus.get(KnConstants.DELETED) != null) {
                    delGroupIdList.addAll(groupListStatus.get(KnConstants.DELETED).keySet());
                }
                //delGroupIdList.addAll(dispatchGrpList);
                delGroupIdList.addAll(ownerGroupIds);
                if (!delGroupIdList.isEmpty()) {
                    Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : delGroupIdList) {
                        Collection<String> delMembersList = groupDistList.get(grpId);
                        deletedMembersMap.put(grpId, delMembersList);
                        //for the contact added to update the directory
                        contactMDNs.addAll(delMembersList);
                        removeGrpMdnListMap.remove(grpId);
                        delGroupMemStatus.remove(grpId);
                        if(existingAddlTGList.get(grpId) != null) delAddlTGList.addAll(existingAddlTGList.get(grpId));
                    }

                    etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                    if (groupListStatus != null && groupListStatus.get(KnConstants.DELETED) != null) {
                        groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                    }
                    groupNameMap.putAll(dispGrpNameMap);
                }
            }
            if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                Collection<String> mdnForAddlTg = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
                groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmsHomePttId, persisterTxn);
                subsAddlTGList.removeAll(delAddlTGList);
                subsAddlTGList.forEach(subsAddl -> {
                    if(groupDetailsMap.get(subsAddl.getGroupId()) != null) subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                });
                etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnForAddlTg, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                    groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                }
                knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                    etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, subsAddlTGList, delAddlTGList, groupCorpIdMap);
                    knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                }
            }
            groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
            etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
            etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);

            if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                Set<Integer> modGroupIdList = groupListStatus.get(KnConstants.MODIFIED).keySet();
                if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                    Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : modGroupIdList) {
                        Collection<String> modMembersList = groupDistList.get(grpId);
                        modifiedMembersMap.put(grpId, modMembersList);
                        //for the contact added to update the directory
                        contactMDNs.addAll(modMembersList);
                    }
                    etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                            DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                    groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                }
            }
        }

        if (contactMDNs != null && !contactMDNs.isEmpty()) {
            knLogger.info(methodName, "Updating Directory Etags");
            etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
        }
        knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
        etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap, null,
                null, removeGrpMdnListMap, null, null, groupDistList, null, delGroupMemStatus, null);
        Collection<String> memberMDNs = new ArrayList<String>();
        memberMDNs.add(mdn);
        knLogger.debug(methodName, "etagMap ", KnGDPRTemplate.mapKeyMdn(etagMap));
        etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
        knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
        knLogger.info(methodName, "Updating Directory Etags");
        etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(memberMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
        Map<String, KnCorpSubscriberDTO> subscDetails = contactInfoUtil.getSubscIsMemOfDispGrpDetails(memberMDNs, xdmsHomePttId, persisterTxn);
        Integer dispGrpMebBit = subscDetails.get(mdn).getDispatchGrpmember();
        List<String> overAllList = new ArrayList<String>();
        if (dispGrpMebBit == 1) {
            groupFleetMembers.add(mdn);
            overAllList.addAll(groupFleetMembers);
        }
        if (dispContactList != null && dispContactList.get(mdn) != null) {
            overAllList.addAll(dispContactList.get(mdn));
        }
        respDTO.setDisabledDispatchMemList(overAllList);
        if(respDTO.getDisabledDispatchMemList() != null) {
            disabledOdlMember.addAll(respDTO.getDisabledDispatchMemList());
        }
        respDTO.setDisabledDispatchMemList(disabledOdlMember);
        respDTO.setChangeLogMap(etagMap);
        groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
        this.groupInfoUtil.updateGroupOwner(mdn, null, corpId, xdmsHomePttId, persisterTxn);
        etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
        respDTO.setChangeLogMap(etagMap);
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    private Map<String, KnOPDirChgDTO> deleteMcpttDoc(String mdn, String xdmsHomePttId, Map<String, KnOPDirChgDTO> etagMap,
                                                      KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteMcpttDoc(String, String, Map<String, KnOPDirChgDTO>, KnPersisterTxn)";
        LinkedList<String> mdnList = new LinkedList<String>();
        mdnList.add(mdn);
        Map<String, KnMcpttPermissionDTO> mcpttTargPermissionMap = corpSubsProvInfoUtil.getTargUserPermissions(mdn, xdmsHomePttId, persisterTxn);
        Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributesForDestination =
                corpSubsProvInfoUtil.getEmergDestAttributesForDestination(mdn, xdmsHomePttId, false, persisterTxn);
        corpSubsProvInfoUtil.deleteFromMcpttPermInfoAuthMdn(mdn, xdmsHomePttId, persisterTxn);
        corpSubsProvInfoUtil.deleteFromMcpttPermInfoTargetMdn(mdn, xdmsHomePttId, persisterTxn);
        corpSubsProvInfoUtil.deleteFromAuthDoc(mdnList, xdmsHomePttId, persisterTxn);
        corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdn, xdmsHomePttId, persisterTxn);
        corpSubsProvInfoUtil.deleteFromEmergInfoForDest(mdn, xdmsHomePttId, persisterTxn);
        corpSubsProvInfoUtil.deleteFromEmergDoc(mdnList, xdmsHomePttId, persisterTxn);
        if(mcpttTargPermissionMap != null && !mcpttTargPermissionMap.isEmpty()){
            for(String authMdn : mcpttTargPermissionMap.keySet()){
                etagMap = commonInfoUtil.formMdnAuthDeleteNotification(authMdn, etagMap);
            }
        }
        if(mdnEmergAttributesForDestination != null && !mdnEmergAttributesForDestination.isEmpty()){
            for(KnSubsDestEmergencyAttributes emergencyDestination : mdnEmergAttributesForDestination){
                etagMap = commonInfoUtil.formMdnEmergDeleteNotification(emergencyDestination.getMdn(), etagMap);
            }
        }
        etagMap = commonInfoUtil.formMdnAuthDeleteNotification(mdn, etagMap);
        etagMap = commonInfoUtil.formMdnEmergDeleteNotification(mdn, etagMap);
        knLogger.info(methodName, "EXIT : Returning Response - ", KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }
    
    public KnCorpAuthInfoRespDTO getCorpProfileByEntities(KnIPCorpAuthInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {

        final String methodName = "getCorpProfileByEntities()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpInfoDTO);
        KnCorpAuthInfoRespDTO respDTO = new KnCorpAuthInfoRespDTO();
        try {
            String extCorpId = corpInfoDTO.getExtCorpId();
            Map<String, Object> customParams = corpInfoDTO.getCustomParamMap();
            Object hookResp = null;
            if (corpInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                corpInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GETEXTCORPID);
                hookIPDTO.setData(corpInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook");
                //get if any Alias MDN or the Group MDN are present in the incontext
                hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                knLogger.debug(methodName, "Call After Hook");
                // If hook reaponse is null and is instance of KnCorpHookRespDTO - that means response recieved is
                // from corporate custom module else response is loop back response
                if (hookResp instanceof KnCorpHookRespDTO) {
                    KnCorpHookRespDTO responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    } else {
                        knLogger.debug(methodName, "Returning Hook Success response - ", responseDTO);
                        extCorpId = responseDTO.getExtCorpId();
                    }
                }
            }
            int corpId = commonInfoUtil.getCorpId(extCorpId, true, persisterTxn);
            KnCorpProfileInfoDTO profileInfoDto = new KnCorpProfileInfoDTO();
            profileInfoDto.setCorpId(String.valueOf(corpId));
            profileInfoDto.setExtCorpId(extCorpId);
            respDTO.setProfileInfoDTO(profileInfoDto);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving CorpProfile Info - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving CorpProfile Info - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpPoCSvcConfigRespDTO getPoCConfig(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getPoCConfig(KnIPCorpInfoDTO, KnPersisterTxn)";
        KnCorpPoCSvcConfigRespDTO respDTO = new KnCorpPoCSvcConfigRespDTO();
        try {
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            Collection<KnSIPProxySvcConfigDTO> sipProxySvcConfigInfoDTO = commonInfoUtil.getSipProxySvcConfig(xdmPttServerId, true, persisterTxn);
            Collection<KnAPNConfigDTO> apnConfigDTOS = commonInfoUtil.getAPNProfileConfig(xdmPttServerId, persisterTxn);
            Collection<String> pttServerIds = corpInfoDTO.getPttServerIds();
            respDTO.setSipProxySvcConfig((pttServerIds != null && !pttServerIds.isEmpty())
                    ? sipProxySvcConfigInfoDTO.stream().filter(sipProxy -> pttServerIds.contains(sipProxy.getPttServerId()))
                    .collect(Collectors.toList()) : sipProxySvcConfigInfoDTO);
            respDTO.setApnConfig((pttServerIds != null && !pttServerIds.isEmpty())
                    ? apnConfigDTOS.stream().filter(apnConfig -> pttServerIds.contains(apnConfig.getPttServerId()))
                    .collect(Collectors.toList()) : apnConfigDTOS);
            knLogger.debug(methodName, "respDTO - ", respDTO);
            populate(respDTO);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getPoCConfig ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpSharedList getSharedCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        KnCorpSharedList respDTO=new KnCorpSharedList();
        KnGeneralCacheUtil generalCacheUtil=new KnGeneralCacheUtil();
        String methodName = "getCorpTrustMatrix(reqDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", requestDTO);
        try{
            String extCorpId = requestDTO.getExtCorpId();
            knLogger.debug("corpid fetched from the input is ",extCorpId);
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");

            //check wheather the corpid is present in the DB or not
            int corpId = commonInfoUtil.getCorpId(extCorpId, true, persisterTxn);
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, true, persisterTxn);
            //getting the data from the DB
            knLogger.debug("CorpProfile obtained from the system",corpProfile);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String sysGroupSharing = microServicesParamNameValueMap.get(GROUP_SHARING_FLAG);
            String sysUserProfileSharing = microServicesParamNameValueMap.get(USER_PROFILE_SHARING_FLAG);
            Integer sysGroupSharingval=Integer.parseInt(sysGroupSharing);
            Integer sysUserProfileSharingval = Integer.parseInt(sysUserProfileSharing);
            Boolean isEnbaled=false;
            if(corpProfile.getGroupSharingFeature() != null){
                if(corpProfile.getGroupSharingFeature() == 1 ){
                    isEnbaled=Boolean.TRUE;
                }
            }else if(sysGroupSharingval != null){
                if(sysGroupSharingval == 1){
                    isEnbaled=Boolean.TRUE;
                }
            }
            if(corpProfile.getUserProfileSharingFeature() !=null){
                if(corpProfile.getUserProfileSharingFeature() == 1){
                    isEnbaled=Boolean.TRUE;
                }
            }else if(sysUserProfileSharingval != null){
                if(sysUserProfileSharingval == 1){
                    isEnbaled=Boolean.TRUE;
                }
            }
            if(isEnbaled){  //groupSharing feature is enabled
                List<KnCorpTrustMatrixDTO> trustMatrixDTOList = generalCacheUtil.getSharedCorpMatrix(extCorpId.trim());
                knLogger.debug("Trust matrix list obtained from the DB is-->",trustMatrixDTOList);
                Set<String> extCorpIds=new HashSet<>();
                Set<Integer> corpids=new HashSet<>();
                List<KnCorpTrustMatrixDTO> responseList;
                for(KnCorpTrustMatrixDTO ids: trustMatrixDTOList)
                {
                    extCorpIds.add(ids.getSharedExtCorpId());//getting the shared corpids to get the correspoding int Corpid
                }
                Map<String, Integer> extIntCorpIDMap = commonInfoUtil.getCorpIdMap(extCorpIds, corpids, true, persisterTxn);
                knLogger.debug(methodName,"extIntCorpIDMap - ",extIntCorpIDMap);
                //Adding the shared corps external corpid and the corresponding internal corpid
                // UCSPROVCONFIG-3193: Build owner hierarchy id→name map for name resolution
                Map<String, String> ownerIdToNameMap = null;
                try {
                    ownerIdToNameMap = hierarchyInfoUtil.getHierarchyNameList(corpId, corpProfile.getXdmsHome(), persisterTxn);
                    // getHierarchyNameList returns name→id; we need id→name
                    Map<String, String> reversed = new HashMap<>();
                    if (ownerIdToNameMap != null) {
                        for (Map.Entry<String, String> entry : ownerIdToNameMap.entrySet()) {
                            reversed.put(entry.getValue(), entry.getKey());
                        }
                    }
                    ownerIdToNameMap = reversed;
                } catch (Exception e) {
                    knLogger.warn(methodName, "UCSPROVCONFIG-3193: Failed to load owner hierarchy names for corpId=", corpId, " - ", e.getMessage());
                    ownerIdToNameMap = new HashMap<>();
                }

                for(KnCorpTrustMatrixDTO ids: trustMatrixDTOList)
                {
                    Integer corpid = extIntCorpIDMap.get(ids.getSharedExtCorpId());
                    ids.setExtCorpId(ids.getSharedExtCorpId());
                    ids.setSharedExtCorpId(String.valueOf(corpid));

                    // UCSPROVCONFIG-3193: Build shared corp hierarchy id→name map
                    Map<String, String> sharedIdToNameMap = new HashMap<>();
                    if (corpid != null) {
                        try {
                            KnCorpProfileDTO sharedCorpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpid), CORP_PROFILE, true, persisterTxn);
                            if (sharedCorpProfile != null) {
                                Map<String, String> sharedNameToId = hierarchyInfoUtil.getHierarchyNameList(corpid, sharedCorpProfile.getXdmsHome(), persisterTxn);
                                if (sharedNameToId != null) {
                                    for (Map.Entry<String, String> entry : sharedNameToId.entrySet()) {
                                        sharedIdToNameMap.put(entry.getValue(), entry.getKey());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            knLogger.warn(methodName, "UCSPROVCONFIG-3193: Failed to load shared hierarchy names for corpId=", corpid, " - ", e.getMessage());
                        }
                    }

                    // P8-TM: Populate hierarchyMappings for hierarchy-scoped trust entries (TC-TM-GET-002)
                    if (ids.getRecId() != null) {
                        try {
                            List<KnSharedTrustMatrixHierarchyDTO> childRows =
                                    KnGGCache.sharedTrustMatrixHierarchyDAO.getAllHierarchiesByRecId(ids.getRecId());
                            List<KnHierarchyMappingInfo> mappings = new ArrayList<>();
                            for (KnSharedTrustMatrixHierarchyDTO child : childRows) {
                                KnHierarchyMappingInfo mapping = new KnHierarchyMappingInfo();
                                mapping.setOwnerHierarchyId(child.getOwnerHierarchyId());
                                mapping.setSharedHierarchyId(child.getSharedHierarchyId());
                                // UCSPROVCONFIG-3193: Resolve hierarchy names
                                mapping.setOwnerHierarchyName(ownerIdToNameMap.get(child.getOwnerHierarchyId()));
                                mapping.setSharedHierarchyName(sharedIdToNameMap.get(child.getSharedHierarchyId()));
                                mappings.add(mapping);
                            }
                            ids.setHierarchyMappings(mappings);
                        } catch (Exception e) {
                            knLogger.warn(methodName, "P8-TM: Failed to load hierarchyMappings for recId=",
                                    ids.getRecId(), " - ", e.getMessage());
                            ids.setHierarchyMappings(new ArrayList<>());
                        }
                    } else {
                        ids.setHierarchyMappings(new ArrayList<>());
                    }
                }
                knLogger.debug("List data obtained is --"+trustMatrixDTOList);
                respDTO.setList(trustMatrixDTOList);
                populate(respDTO);

            }else { //group sharing feature is not enabled
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARING_FEATURE_NOT_ALLOWED,
                        "Group sharing feature disabled.", "", "", "", "", "");
            }}
        catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the ",
                    "getCorpTrustMatrix of the corporate " + e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving the get corp matrix - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug("returing the response from the controller layer",respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO updateCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        String methodName = "updateCorpTrustMatrix(reqDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", requestDTO);
        try {
            Map<String, Object> params = requestDTO.getCustomParamMap();
            String sourceExtCorpId = requestDTO.getExtCorpId();
            String targetExtCorpId = (String) params.get("TARGET_EXT_CORP_ID");
            long memFeaturesAllowed = params.get("MEM_FEATURES_ALLOWED") != null ? ((Number) params.get("MEM_FEATURES_ALLOWED")).longValue() : 0L;
            long sharingFeatureAllowed = params.get("SHARING_FEATURE_ALLOWED") != null ? ((Number) params.get("SHARING_FEATURE_ALLOWED")).longValue() : 1L;
            @SuppressWarnings("unchecked")
            List<KnHierarchyGroupMappingInfo> hierarchyMappings = (List<KnHierarchyGroupMappingInfo>) params.get("HIERARCHY_MAPPINGS");

            commonInfoUtil.getCorpId(sourceExtCorpId, true, persisterTxn);
            commonInfoUtil.getCorpId(targetExtCorpId, true, persisterTxn);

            // Resolve hierarchy names to internal IDs; expand each group entry into flat pairs
            List<String[]> resolvedPairs = null; // each entry: [ownerInternalId, sharedInternalId]
            if (hierarchyMappings != null && !hierarchyMappings.isEmpty()) {
                int sourceCorporateId = commonInfoUtil.getCorpId(sourceExtCorpId, false, persisterTxn);
                int targetCorporateId = commonInfoUtil.getCorpId(targetExtCorpId, false, persisterTxn);
                KnCorpProfileDTO srcCorpProfile = commonInfoUtil.getProfileDetails(String.valueOf(sourceCorporateId), CORP_PROFILE, false, persisterTxn);
                KnCorpProfileDTO tgtCorpProfile = commonInfoUtil.getProfileDetails(String.valueOf(targetCorporateId), CORP_PROFILE, false, persisterTxn);
                Map<String, String> srcNameToId = hierarchyInfoUtil.getHierarchyNameList(sourceCorporateId, srcCorpProfile.getXdmsHome(), persisterTxn);
                Map<String, String> tgtNameToId = hierarchyInfoUtil.getHierarchyNameList(targetCorporateId, tgtCorpProfile.getXdmsHome(), persisterTxn);
                resolvedPairs = new ArrayList<>();
                for (KnHierarchyGroupMappingInfo mapping : hierarchyMappings) {
                    String resolvedOwnerHierarchyId = srcNameToId.get(mapping.getOwnerHierarchyName());
                    if (resolvedOwnerHierarchyId == null) {
                        knLogger.error(methodName, "sourceHierarchyId not found: name=", mapping.getOwnerHierarchyName(), " corp=", sourceExtCorpId);
                        populate(respDTO, KnErrorCodes.BOEntity.SOURCE_HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION, "sourceHierarchyId not found: " + mapping.getOwnerHierarchyName() + " for corp " + sourceExtCorpId);
                        return respDTO;
                    }
                    if (mapping.getSharedHierarchyName() == null || mapping.getSharedHierarchyName().isEmpty()) {
                        knLogger.error(methodName, "sharedHierarchyName list is null or empty for ownerHierarchyName=", mapping.getOwnerHierarchyName());
                        populate(respDTO, KnErrorCodes.BOEntity.TARGET_HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION, "sharedHierarchyName list is empty for ownerHierarchyName: " + mapping.getOwnerHierarchyName());
                        return respDTO;
                    }
                    for (String sharedName : mapping.getSharedHierarchyName()) {
                        String resolvedSharedHierarchyId = tgtNameToId.get(sharedName);
                        if (resolvedSharedHierarchyId == null) {
                            knLogger.error(methodName, "targetHierarchyId not found: name=", sharedName, " corp=", targetExtCorpId);
                            populate(respDTO, KnErrorCodes.BOEntity.TARGET_HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION, "targetHierarchyId not found: " + sharedName + " for corp " + targetExtCorpId);
                            return respDTO;
                        }
                        resolvedPairs.add(new String[]{resolvedOwnerHierarchyId, resolvedSharedHierarchyId});
                    }
                }
                knLogger.info(methodName, "Resolved ", resolvedPairs.size(), " hierarchy pair(s) to internal IDs");
            }

            KnGeneralCacheUtil generalCacheUtil = new KnGeneralCacheUtil();
            KnCorpTrustMatrixDTO existing = generalCacheUtil.getExactCorpTrustMatrixEntry(sourceExtCorpId, targetExtCorpId);

            // track generated recId to return in response
            String generatedRecId = null;

            if (existing == null) {
                String recId = KnGGCache.trustMatrixDAO.insertWithRecId(sourceExtCorpId, targetExtCorpId, memFeaturesAllowed, sharingFeatureAllowed);
                generatedRecId = recId;
                knLogger.info(methodName, "Inserted new trust matrix row recId=", recId);
                if (resolvedPairs != null && !resolvedPairs.isEmpty()) {
                    for (String[] pair : resolvedPairs) {
                        KnGGCache.sharedTrustMatrixHierarchyDAO.insert(recId, pair[0], pair[1]);
                    }
                }
            } else if (existing.getRecId() == null) {
                if (resolvedPairs != null && !resolvedPairs.isEmpty()) {
                    knLogger.error(methodName, "Cannot add hierarchy mappings to legacy trust matrix row (REC_ID IS NULL)");
                    populate(respDTO, KnErrorCodes.BOEntity.UNSUPPORTED_OPERATION, "Cannot add hierarchy scoping to existing legacy trust matrix entry. Use deleteCorpTrustMatrix first.");
                    return respDTO;
                }
                KnGGCache.trustMatrixDAO.updateByCorpPair(sourceExtCorpId, targetExtCorpId, memFeaturesAllowed, sharingFeatureAllowed);
                knLogger.info(methodName, "Updated legacy trust matrix row by corp-pair");
            } else {
                String recId = existing.getRecId();
                generatedRecId = recId;
                KnGGCache.trustMatrixDAO.updateParentRow(recId, memFeaturesAllowed, sharingFeatureAllowed);
                knLogger.info(methodName, "Updated trust matrix parent row recId=", recId);
                if (resolvedPairs != null && !resolvedPairs.isEmpty()) {
                    for (String[] pair : resolvedPairs) {
                        KnGGCache.sharedTrustMatrixHierarchyDAO.insert(recId, pair[0], pair[1]);
                    }
                }
            }
            if (generatedRecId != null) {
                Map<String, Object> additionalInfo = new java.util.HashMap<>();
                additionalInfo.put("GENERATED_REC_ID", generatedRecId);
                respDTO.setAdditionalInfo(additionalInfo);
            }
            populate(respDTO);
            knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occurred in updateCorpTrustMatrix", new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO deleteCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        String methodName = "deleteCorpTrustMatrix(reqDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", requestDTO);
        try {
            Map<String, Object> params = requestDTO.getCustomParamMap();
            String sourceExtCorpId = requestDTO.getExtCorpId();
            String targetExtCorpId = (String) params.get("TARGET_EXT_CORP_ID");
            int deleteMode = params.get("DELETE_MODE") != null ? ((Number) params.get("DELETE_MODE")).intValue() : 1;
            String ownerHierarchyId = (String) params.get("OWNER_HIERARCHY_ID");
            @SuppressWarnings("unchecked")
            List<String> targetHierarchyIds = (List<String>) params.get("TARGET_HIERARCHY_IDS");

            commonInfoUtil.getCorpId(sourceExtCorpId, true, persisterTxn);
            commonInfoUtil.getCorpId(targetExtCorpId, true, persisterTxn);

            KnGeneralCacheUtil generalCacheUtil = new KnGeneralCacheUtil();
            KnCorpTrustMatrixDTO existing = generalCacheUtil.getExactCorpTrustMatrixEntry(sourceExtCorpId, targetExtCorpId);

            if (existing == null) {
                knLogger.error(methodName, "Trust matrix entry not found for corpPair sourceExtCorpId=", sourceExtCorpId, " targetExtCorpId=", targetExtCorpId);
                populate(respDTO, KnErrorCodes.BOEntity.TRUST_MATRIX_ENTRY_NOT_FOUND, "Trust matrix entry not found for specified corp pair.");
                return respDTO;
            }

            if (deleteMode == 1) {
                // block delete if active group sharing exists
                try {
                    int sourceIntCorpId = commonInfoUtil.getCorpId(sourceExtCorpId, false, persisterTxn);
                    int targetIntCorpId = commonInfoUtil.getCorpId(targetExtCorpId, false, persisterTxn);
                    KnCorpProfileDTO srcProfile = commonInfoUtil.getProfileDetails(String.valueOf(sourceIntCorpId), CORP_PROFILE, false, persisterTxn);
                    int activeGroupCount = groupInfoUtil.countActiveGroupsByCorpPair(sourceIntCorpId, targetIntCorpId, srcProfile.getXdmsHome(), persisterTxn);
                    if (activeGroupCount > 0) {
                        knLogger.info(methodName, "Blocking delete — activeGroupCount=", activeGroupCount, " for corpPair=", sourceExtCorpId, "/", targetExtCorpId);
                        populate(respDTO, KnErrorCodes.BOEntity.CANNOT_DELETE_TRUST_MATRIX, "Groups are already shared using this trust matrix entry. Please clean up (unshare groups) before deleting.");
                        return respDTO;
                    }
                } catch (Exception e) {
                    knLogger.error(methodName, "Failed checking active groups - ", e);
                    populate(respDTO, KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to validate active group sharing before delete.");
                    return respDTO;
                }

                if (existing.getRecId() != null) {
                    KnGGCache.sharedTrustMatrixHierarchyDAO.deleteByRecId(existing.getRecId());
                    KnGGCache.trustMatrixDAO.deleteByRecId(existing.getRecId());
                    knLogger.info(methodName, "Deleted all child rows and parent row for recId=", existing.getRecId());
                } else {
                    KnGGCache.trustMatrixDAO.deleteByCorpPair(sourceExtCorpId, targetExtCorpId);
                    knLogger.info(methodName, "Deleted legacy trust matrix row by corp-pair");
                }
            } else {
                if (existing.getRecId() == null) {
                    knLogger.error(methodName, "not applicable for legacy row (REC_ID IS NULL)");
                    populate(respDTO, KnErrorCodes.BOEntity.UNSUPPORTED_OPERATION, "Targeted hierarchy delete not applicable for legacy flat trust matrix entry.");
                    return respDTO;
                }
                String recId = existing.getRecId();
                // resolve hierarchy names (from OPSCLI) to internal IDs before delete
                String resolvedOwnerHierarchyId = ownerHierarchyId;
                List<String> resolvedTargetHierarchyIds = targetHierarchyIds;
                if (ownerHierarchyId != null) {
                    int sourceCorporateId = commonInfoUtil.getCorpId(sourceExtCorpId, false, persisterTxn);
                    KnCorpProfileDTO srcCorpProfile = commonInfoUtil.getProfileDetails(String.valueOf(sourceCorporateId), CORP_PROFILE, false, persisterTxn);
                    Map<String, String> srcNameToId = hierarchyInfoUtil.getHierarchyNameList(sourceCorporateId, srcCorpProfile.getXdmsHome(), persisterTxn);
                    resolvedOwnerHierarchyId = srcNameToId.get(ownerHierarchyId);
                    if (resolvedOwnerHierarchyId == null) {
                        knLogger.error(methodName, "sourceHierarchyId not found: name=", ownerHierarchyId, " corp=", sourceExtCorpId);
                        populate(respDTO, KnErrorCodes.BOEntity.SOURCE_HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION, "sourceHierarchyId not found: " + ownerHierarchyId + " for corp " + sourceExtCorpId);
                        return respDTO;
                    }
                    if (targetHierarchyIds != null && !targetHierarchyIds.isEmpty()) {
                        int targetCorporateId = commonInfoUtil.getCorpId(targetExtCorpId, false, persisterTxn);
                        KnCorpProfileDTO tgtCorpProfile = commonInfoUtil.getProfileDetails(String.valueOf(targetCorporateId), CORP_PROFILE, false, persisterTxn);
                        Map<String, String> tgtNameToId = hierarchyInfoUtil.getHierarchyNameList(targetCorporateId, tgtCorpProfile.getXdmsHome(), persisterTxn);
                        resolvedTargetHierarchyIds = new ArrayList<>();
                        for (String tgtHierName : targetHierarchyIds) {
                            String resolvedTgt = tgtNameToId.get(tgtHierName);
                            if (resolvedTgt == null) {
                                knLogger.error(methodName, " TargetHierarchyId not found: name=", tgtHierName, " corp=", targetExtCorpId);
                                populate(respDTO, KnErrorCodes.BOEntity.TARGET_HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION, "targetHierarchyId not found: " + tgtHierName + " for corp " + targetExtCorpId);
                                return respDTO;
                            }
                            resolvedTargetHierarchyIds.add(resolvedTgt);
                        }
                    }
                    knLogger.info(methodName, "Resolved hierarchy IDs for CASE 2 delete");
                }
                if (resolvedTargetHierarchyIds == null || resolvedTargetHierarchyIds.isEmpty()) {
                    KnGGCache.sharedTrustMatrixHierarchyDAO.deleteByRecIdAndOwner(recId, resolvedOwnerHierarchyId);
                    knLogger.info(methodName, "Deleted all child rows for recId=", recId, " ownerHierarchyId=", resolvedOwnerHierarchyId);
                } else {
                    KnGGCache.sharedTrustMatrixHierarchyDAO.deleteByRecIdOwnerAndShared(recId, resolvedOwnerHierarchyId, resolvedTargetHierarchyIds);
                    knLogger.info(methodName, "Deleted specific child rows for recId=", recId, " ownerHierarchyId=", resolvedOwnerHierarchyId, " count=", resolvedTargetHierarchyIds.size());
                }
            }
            populate(respDTO);
            knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occurred in deleteCorpTrustMatrix", new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO updateCorporateFS(KnIPCorpInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateCorporateFS(reqDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", requestDTO.getCorpId());
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {

            Integer corpId = requestDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, true, persisterTxn);
            //getting the data from the DB
            knLogger.debug("CorpProfile obtained from the system", corpProfile);
            KnIPCorpInfoDTO reqDTO = (KnIPCorpInfoDTO) requestDTO;

            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String pttRecordingFlag = microServicesParamNameValueMap.get(KnConstants.PTT_RECORDING_FLAG);
            String dataRecordingFlag = microServicesParamNameValueMap.get(KnConstants.DATA_RECORDING_FLAG);
            String videoRecordingFlag = microServicesParamNameValueMap.get(KnConstants.VIDEO_RECORDING_FLAG);
            String selfDnDPrivilege = microServicesParamNameValueMap.get(SELF_DND_FEATURE);
            String sysLargeAgencyDispatchFlag = microServicesParamNameValueMap.get(LARGE_AGENCY_DISPATCH_FEATURE);

            KnCorpRecordingFsPersistDTO persistDTO = new KnCorpRecordingFsPersistDTO();
            persistDTO.setInputDTO(reqDTO);
            //setting the system level value
            persistDTO.setSysPttRecordingFlag(ENABLED == Integer.parseInt(pttRecordingFlag));
            persistDTO.setSysDataRecordingFlag(ENABLED == Integer.parseInt(dataRecordingFlag));
            persistDTO.setSysVideoRecordingFlag(ENABLED == Integer.parseInt(videoRecordingFlag));
            persistDTO.setSysSelfDnDPrivilege(ENABLED == Integer.parseInt(selfDnDPrivilege));
            persistDTO.setSysLargeAgencyDispatch(ENABLED == Integer.parseInt(sysLargeAgencyDispatchFlag));
            //setting the corp level value
            String reqPttRecordingFlag = reqDTO.getPttRecording();
            String reqDataRecordingFlag = reqDTO.getDataRecording();
            String reqVideoRecordingFlag = requestDTO.getVideoRecording();
            String reqSelfDnDPrivilege = requestDTO.getSelfDnDPrivilege();
            String RecordingFsVal =  KnGeneralUtil.getRecordingFs(reqPttRecordingFlag,
                    reqDataRecordingFlag, reqVideoRecordingFlag);
            String reqLargeAgencyDispatch = requestDTO.getLargeAgencyDispatch();

            persistDTO.setReqSelfDnDPrivilege(reqSelfDnDPrivilege);
            persistDTO.setReqRecordingFs(RecordingFsVal);
            persistDTO.setReqLargeAgencyDispatch(reqLargeAgencyDispatch);

            // Calling Validation Framework for DTO validation
            knLogger.debug(methodName, "Before Validation", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed");

            //updating the FS value in the corp
            String corpFS2 = corpProfile.getCorpFS2();
            knLogger.info(methodName," old corp fs from DB ",corpFS2);
            BitSet corpAdminBitSet = KnGeneralUtil.convertHexStringToBitSet(corpFS2);
            if (reqPttRecordingFlag != null) {
                corpAdminBitSet.set(com.kodiak.common.resources.KnConstants.FEATURE_SET.PTT_RECORDING_FLAG.value(), reqPttRecordingFlag.equals(ENABLED_STRING));
            }
            if (reqDataRecordingFlag != null) {
                corpAdminBitSet.set(com.kodiak.common.resources.KnConstants.FEATURE_SET.DATA_RECORDING_FLAG.value(), reqDataRecordingFlag.equals(ENABLED_STRING));
            }
            if (reqVideoRecordingFlag != null) {
                corpAdminBitSet.set(com.kodiak.common.resources.KnConstants.FEATURE_SET.VIDEO_RECORDING_FLAG.value(), reqVideoRecordingFlag.equals(ENABLED_STRING));
            }
            if (null != reqSelfDnDPrivilege) {
                corpAdminBitSet.set(FEATURE_SET.SELF_DND_PRIVILEGE.value(), reqSelfDnDPrivilege.equals(ENABLED_STRING));
            }
            if (null != reqLargeAgencyDispatch) {
                corpAdminBitSet.set(FEATURE_SET.LARGE_AGENCY_DISPATCH.value(), reqLargeAgencyDispatch.equals(ENABLED_STRING));
            }
            String corpAdminFSReq = KnGeneralUtil.convertBitSetToHexString(corpAdminBitSet);
            knLogger.info(methodName, "corpAdminBitSet - after the operation ", corpAdminFSReq);
            //updating the corpFs
            KnCorpCommonInfoUtil.updateCorpFs(corpAdminFSReq,String.valueOf(corpId),corpProfile.getXdmsHome(), persisterTxn);

            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(corpProfile.getXdmsHome());
            String target = corpId + "_*";
            int recType = 0;
            if (ENABLED_STRING.equals(reqPttRecordingFlag)) {
                recType = REC_TYPE.AUDIO.value();
            } else if (ENABLED_STRING.equals(reqVideoRecordingFlag)) {
                recType = REC_TYPE.VIDEO.value();
            }
            if (ENABLED_STRING.equals(reqPttRecordingFlag) && ENABLED_STRING.equals(reqVideoRecordingFlag)) {
                recType = REC_TYPE.BOTH.value();
            }

            //updating SipRecordingFlag
            if (null != reqPttRecordingFlag || null != reqVideoRecordingFlag) {
                Integer sipRecordingFlag = SIP_RECORDING_FLAG.NONE.value();
                if (ENABLED_STRING.equals(reqPttRecordingFlag) || ENABLED_STRING.equals(reqVideoRecordingFlag)) {
                    sipRecordingFlag = SIP_RECORDING_FLAG.ANY.value();
                }
                commonXDMServerDAO.updateSipRecordingFlag(sipRecordingFlag, corpId, persisterTxn);
            }
            //RECORDING_TARGET_INFO
            String targetInfoOptimization = microServicesParamNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.TARGET_INFO_OPTIMIZATION);
            if (!com.kodiak.xdms.server.common.util.KnGeneralUtil.isTargetInfoOptimizationEnabled(targetInfoOptimization)) {
                knLogger.debug(methodName, "TARGET_INFO_OPTIMIZATION disabled or not set - Populating RECORDING_TARGET_INFO (BAU)");
                KnRecordingTargetInfoDTO recordingTargetInfoDTO = new KnRecordingTargetInfoDTO();
                recordingTargetInfoDTO.setTarget(target);
                Collection<String> corpIdCollection = List.of(target);
                // Getting RecordingInfoTarget by current Corp ID
                Collection<KnRecordingTargetInfoDTO> recordingInfoTargetByTarget = commonXDMServerDAO.getRecordingInfoTargetByTarget(corpIdCollection, persisterTxn);
                // Checking if any RecordingInfoTarget is present with current Corp ID

                if ((reqPttRecordingFlag != null) || (reqVideoRecordingFlag != null) || (reqDataRecordingFlag != null)) {
                    if (null != recordingInfoTargetByTarget && 0 == recordingInfoTargetByTarget.size() &&
                            (ENABLED_STRING.equals(reqPttRecordingFlag) || ENABLED_STRING.equals(reqVideoRecordingFlag))) {
                        if (!((null != microServicesParamNameValueMap.get(KnConstants.MSI_PRIMARY_WAVE_RECORDING_SERVER_PORT) &&
                                null != microServicesParamNameValueMap.get(KnConstants.MSI_PRIMARY_WAVE_RECORDING_SERVER_IP)) &&
                                (null != microServicesParamNameValueMap.get(KnConstants.MSI_GEO_WAVE_RECORDING_SERVER_PORT) &&
                                        null != microServicesParamNameValueMap.get(KnConstants.MSI_GEO_WAVE_RECORDING_SERVER_IP)))) {
                            knLogger.error(methodName, "Recording configurations are not configured correctly");
                            throw new KnCorpBOValidationException(CONFIGURATIONS_NOT_VALID_FOR_RECORDING,
                                    "Recording configurations are not configured correctly", "Configurations missing in DB");
                        }
                        // If not present then inserting a new recordingTargetInfo
                        recordingTargetInfoDTO.setPriRecIp(microServicesParamNameValueMap.get(KnConstants.MSI_PRIMARY_WAVE_RECORDING_SERVER_IP));
                        String priRecPort = microServicesParamNameValueMap.get(KnConstants.MSI_PRIMARY_WAVE_RECORDING_SERVER_PORT);
                        recordingTargetInfoDTO.setPriRecPort(null != priRecPort ? Integer.parseInt(priRecPort) : null);
                        recordingTargetInfoDTO.setSecRecIp(microServicesParamNameValueMap.get(KnConstants.MSI_GEO_WAVE_RECORDING_SERVER_IP));
                        String secRecPort = microServicesParamNameValueMap.get(KnConstants.MSI_GEO_WAVE_RECORDING_SERVER_PORT);
                        recordingTargetInfoDTO.setSecRecPort(null != secRecPort ? Integer.parseInt(secRecPort) : null);
                        recordingTargetInfoDTO.setRecType(recType);

                        // Adding recordingTargetInfoDTO in a Collection as create method accepts Collection
                        Collection<KnRecordingTargetInfoDTO> recordingTargetInfoDTOS = List.of(recordingTargetInfoDTO);
                        //create logic
                        commonXDMServerDAO.createRecordingInfoForTarget(recordingTargetInfoDTOS, persisterTxn);
                        //TO-DO: Need to pull out the recording feature code from here{Line No. 4750 (after updateCorpFS)} and add it to private method based on the flag.
                    } else {
                        if (!((null != microServicesParamNameValueMap.get(KnConstants.MSI_PRIMARY_WAVE_RECORDING_SERVER_PORT) &&
                                null != microServicesParamNameValueMap.get(KnConstants.MSI_PRIMARY_WAVE_RECORDING_SERVER_IP)) &&
                                (null != microServicesParamNameValueMap.get(KnConstants.MSI_GEO_WAVE_RECORDING_SERVER_PORT) &&
                                        null != microServicesParamNameValueMap.get(KnConstants.MSI_GEO_WAVE_RECORDING_SERVER_IP)))) {
                            knLogger.error(methodName, "Recording configurations are not configured correctly");
                            throw new KnCorpBOValidationException(CONFIGURATIONS_NOT_VALID_FOR_RECORDING,
                                    "Recording configurations are not configured correctly", "Configurations missing in DB");
                        }
                        //Update Logic
                        int existingRecType = !recordingInfoTargetByTarget.isEmpty() ? new ArrayList<>(recordingInfoTargetByTarget).get(0).getRecType() : 0;

                        // Update rectype for the target if target is already present
                        //If a single enabled value is part of request
                        if ((null == reqPttRecordingFlag && ENABLED_STRING.equals(reqVideoRecordingFlag)) || (null == reqVideoRecordingFlag && ENABLED_STRING.equals(reqPttRecordingFlag))) {
                            commonXDMServerDAO.updateRecordingInfoTargetByTarget(Map.of(target, existingRecType | recType), persisterTxn);
                            //If a single disabled value is part of exit
                            // request
                        } else if ((null == reqPttRecordingFlag && DISABLED_STRING.equals(reqVideoRecordingFlag)) || (null == reqVideoRecordingFlag && DISABLED_STRING.equals(reqPttRecordingFlag))) {
                            if (REC_TYPE.BOTH.value() == existingRecType && DISABLED_STRING.equals(reqVideoRecordingFlag)) {
                                commonXDMServerDAO.updateRecordingInfoTargetByTarget(Map.of(target, REC_TYPE.AUDIO.value()), persisterTxn);
                            }
                            if (REC_TYPE.BOTH.value() == existingRecType && DISABLED_STRING.equals(reqPttRecordingFlag)) {
                                commonXDMServerDAO.updateRecordingInfoTargetByTarget(Map.of(target, REC_TYPE.VIDEO.value()), persisterTxn);
                            }
                            //If both enabled values are part of request
                        } else if (null != reqPttRecordingFlag && null != reqVideoRecordingFlag) {
                            commonXDMServerDAO.updateRecordingInfoTargetByTarget(Map.of(target, recType), persisterTxn);
                        }

                        //Delete logic
                        if ((REC_TYPE.AUDIO.value() == existingRecType && (DISABLED_STRING.equals(reqPttRecordingFlag) && null == reqVideoRecordingFlag)) ||
                                (REC_TYPE.VIDEO.value() == existingRecType && (DISABLED_STRING.equals(reqVideoRecordingFlag) && null == reqPttRecordingFlag)) ||
                                (DISABLED_STRING.equals(reqPttRecordingFlag) && DISABLED_STRING.equals(reqVideoRecordingFlag))) {
                            commonXDMServerDAO.deleteRecordingInfoTargetByTarget(corpIdCollection, persisterTxn);
                        }
                    }
                }
            }
            populate(respDTO);
        }
        catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while updating the ",
                    "corpFs2 of the corporate " + e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating the get corpFs - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug("returing : - ",respDTO);
        return respDTO;
    }

    public KnCorporateProfilepersistDTO1 dial() {
        String methodName = "dial";
        KnCorporateProfilepersistDTO1 profilepersistDTO = new KnCorporateProfilepersistDTO1();
        INormalizeIntf normalize = null;
        KnDialPlanConfigDTO dialPlanInfo = null;
        try {
            normalize = KnNormalizeImpl.getInstance();
            dialPlanInfo = normalize.getDefaultDialPlanInfo();
        } catch (KnNormException e) {
            e.printStackTrace();
        }
        KnCorpProfileInfoDTO profileInfoDto = new KnCorpProfileInfoDTO();
        profileInfoDto.setDialPlanList(commonInfoUtil.getDialPlanDto(dialPlanInfo));
        Collection<KnDialPlanInfoDTO> dialPlanInfoList = profileInfoDto.getDialPlanList();
        Collection<KnDialPlanDTO> dialPlanList = new ArrayList<KnDialPlanDTO>();
        if (dialPlanInfoList != null) {
            for (KnDialPlanInfoDTO dialPlanInfoDTO : dialPlanInfoList) {
                KnDialPlanDTO dialPlanDTO = new KnDialPlanDTO();
                dialPlanDTO.setCountryCode(dialPlanInfoDTO.getCountryCode());
                knLogger.debug(methodName, "countycode", dialPlanInfoDTO.getCountryCode());
                dialPlanDTO.setInternationalDialPrefix(dialPlanInfoDTO.getInternationalDialPrefix());
                knLogger.debug(methodName, "dialprefix", dialPlanInfoDTO.getInternationalDialPrefix());
                dialPlanDTO.setNationalDialPrefix(dialPlanInfoDTO.getNationalDialPrefix());
                knLogger.debug(methodName, "national dialprefix", dialPlanInfoDTO.getNationalDialPrefix());
                knLogger.debug(methodName, "dial plan fianl object", dialPlanDTO);
                dialPlanList.add(dialPlanDTO);
            }
        }
        profilepersistDTO.setDialPlanList((List<KnDialPlanDTO>) dialPlanList);
        knLogger.debug(methodName, "dial plan fianl object passing to mediator", profilepersistDTO);
        return profilepersistDTO;
    }

    /**
     * Method deletes corporate groups, sublists and osm configurations in requesxted corporate account.
     * @param authInfoDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpResponseDTO deleteCorporateData(KnXDMCorpInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "deleteCorporateData(KnXDMCorpInfoDTO, KnPersisterTxn)";
        knLogger.debug(methodName," Entry : input DTO passed :",authInfoDTO);
        KnCorpResponseDTO responseDTO = new KnCorpInfoResDTO();
        try {
            String extCorpId = authInfoDTO.getAccountId();
            int corpId = commonInfoUtil.getCorpId(extCorpId, persisterTxn);
            knLogger.debug(methodName, " Fetched the corpId :", corpId);
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            int subscriberCount = contactInfoUtil.getCorpSubscriberCount(corpId, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "corp Subscriber count - ", subscriberCount);
            if (subscriberCount == 0) {
                //delete all target records from RECORDING_TARGET_INFO using corpid.
                String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
                IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                xdmDAO.deleteRecordingInfoByCorpId(corpId, persisterTxn);
                //get groupIds using corpId
                Collection<Integer> groupIdList = groupInfoUtil.getGroupIdList(corpId, xdmsHome, persisterTxn);
                knLogger.debug(methodName, " Group Ids to delete are :", groupIdList);
                //delete all OSM lists in corporate account.
                corpOSMInfoUtil.deleteAllOSMLists(corpId, xdmsHome, persisterTxn);
                //delete group records from CORPGROUPMEMBERCOUNT, CORPGROUP_LISTREF, CORPGROUPINFO
                groupInfoUtil.deleteAllCorpGroups(groupIdList, xdmsHome, persisterTxn);
                //call to delete sublists from corplistinfo using corpId.
                sublistInfoUtil.deleteAllSublistsInCorp(corpId, xdmsHome, persisterTxn);

            } else {
                knLogger.error(methodName, "Corporate account deletion is not allowed since corporate has active subscribers");
                //if subscribers present in the pocsubscrinfo table then deleteCorpAccount will throw this error
                throw new KnCorpBOException(KnErrorCodes.BOEntity.ACTIVE_SUBSCRIBERS_PRESENT, "Active Subscribers present in the corporate");
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while deleting the corp data - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(responseDTO, e);
        }
        knLogger.debug("Returing the response from the controller layer :",responseDTO);
        return responseDTO;
    }

    /**
     * This method is used to set the CAT (Call Access Type) access permission for a corporate entity.
     * It fetches the corporate profile details either from the cache or the database.
     * It also performs various checks and updates based on the hierarchy type and other parameters.
     *
     * @param reqDTO This is the input data transfer object containing the CAT access permission details.
     * @param persisterTxn This is the transaction object used for database operations.
     * @return KnCorpResponseDTO This returns the response data transfer object containing the result of the operation.
     * @throws KnCorpBOException If there is any business logic related exception.
     * @throws KnDAOException If there is any exception while performing database operations.
     */
    @Override
    public KnCorpResponseDTO setCATAccessPermission(KnIPCatPermissionSetDTO reqDTO, KnPersisterTxn persisterTxn) {
        String methodName = "setCATAccessPermission(reqDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", reqDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String extCorpId = reqDTO.getExtCorpId();
            String catAccessPermSet = reqDTO.getCatAccessPermSet();
            int corpId = commonInfoUtil.getCorpId(extCorpId, persisterTxn);
            long catAccessPermUpdateTSDbValue = commonInfoUtil.getCorporateEtagOnCorpId(corpId, persisterTxn);

            if (!reqDTO.getCatAccessPermUpdateTS().equals(String.valueOf(catAccessPermUpdateTSDbValue))) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.CAT_ACCESS_PER_VALUE_TS_IS_NOT_VALID, "AccessPermUpdateTS is not valid");
            }

            String perSetLastUpdateTime = commonInfoUtil.updateCatAccessPermSet(extCorpId, catAccessPermSet, persisterTxn);
            if (null != perSetLastUpdateTime && !perSetLastUpdateTime.isEmpty()) {
                respDTO.setCatAccessPermUpdateTS(perSetLastUpdateTime);
            }
            populate(respDTO);
            knLogger.debug(methodName, "respDTO ",respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while updating CATAccessPermission ", e);
            populate(respDTO, e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating CATAccessPermission ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving the get corp matrix - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        return respDTO;
    }

    public KnCorpResponseDTO updateSelfEtag(KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn) {
        String methodName = "updateSelfEtag(KnCorpResponseDTO, KnPersisterTxn)";
        KnCorpResponseDTO corpResponseDTO = new KnCorpResponseDTO();
        try {
            int corpId = userProfileDetails.getIntCorpId();
            //Step:get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            String baseMdn = userProfileDetails.getMdn();
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(baseMdn);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            KnTalkGrpScanModeDTO talkGrpScanModeDTO = new KnTalkGrpScanModeDTO();
            talkGrpScanModeDTO.setMdn(userProfileDetails.getProfileMdn());
            if (null != userProfileDetails.getUserProfile() && userProfileDetails.getUserProfile().getTgscMode() != null) {
                talkGrpScanModeDTO.setMode(Integer.parseInt(userProfileDetails.getUserProfile().getTgscMode()));
            } else {
                talkGrpScanModeDTO.setMode(0);
            }
            talkGrpScanModeDTO.setEtag(1);
            genInfoUtil.insertSubsTalkGrpScanMode(talkGrpScanModeDTO, persisterTxn);
            /*String extCorpId = userProfileReqDto.getExtCorpId();
            if (extCorpId != null) {
                knLogger.debug(methodName, "Calling Update Etag for Corp = ", extCorpId);
                provClientIntf.updateEtagForNNISubscr(extCorpId, persisterTxn);
            }*/
            Map<String, KnOPDirChgDTO> etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(corpProfile.getXdmsHome(), mdnList, persisterTxn, null);
            knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            //ETAG update for xdm directory and resourcelist
            etagMap = contactInfoUtil.updateSubcribersImpactedTablesForUpm(corpProfile.getXdmsHome(), mdnList, persisterTxn, etagMap);
            List<String> authMdnsProfileMdns = new ArrayList<>();
            authMdnsProfileMdns.add(userProfileDetails.getProfileMdn());
            //select, update/insert into DG.AUTHORIZATION_DOC
            etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTablesForProfileMdnsForUpm(xdmsHomePttId, authMdnsProfileMdns, null,
                    userProfileDetails.getIntCorpId(), persisterTxn, etagMap);
            //select, update/insert into DG.EMERGENCY_DOC.
            etagMap = corpSubsProvInfoUtil.updateEmergencyImpactedTablesForUpm(xdmsHomePttId, baseMdn, userProfileDetails.getIntCorpId(),
                    persisterTxn, etagMap);
            //updating group etag
            //Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(userProfileDetails.getGroupIds(), xdmsHomePttId, persisterTxn);
            corpResponseDTO.setChangeLogMap(etagMap);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating the self etag - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return corpResponseDTO;
    }

    public void updateSelfEtagForClone(KnCorpResponseDTO assignGroupResp, String corpIdString, String mdn, KnPersisterTxn persisterTxn) {
        String methodName = "updateSelfEtagForClone";
        try {
            int corpId = Integer.parseInt(corpIdString);
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            //Step:get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            //ETAG update for xdm directory and resourcelist
            contactInfoUtil.updateSubcribersImpactedTables(corpProfile.getXdmsHome(), mdnList, persisterTxn, null);

            //ETAG update for DG.SUBSCRPTTRADIOGROUPLISTDOC    (UCSPROVCONFIG-3535)
            groupProfilUtil.updateAddlTalkGroupTable(corpProfile.getXdmsHome(), mdnList, persisterTxn);

            //group etag update not required already updated
            //Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(assignGroupResp.getGroupIds(), xdmsHomePttId, persisterTxn);

            //List<String> authMdnsProfileMdns = new ArrayList<>();
            //authMdnsProfileMdns.add(userProfile.getProfileMdn());
            //select, update/insert into DG.AUTHORIZATION_DOC
            //etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTablesForProfileMdns(xdmsHomePttId, authMdnsProfileMdns, null,
                    //Integer.parseInt(userProfile.getCorpId()), persisterTxn, null);
            //select, update/insert into DG.EMERGENCY_DOC.
            //etagMap = corpSubsProvInfoUtil.updateEmergencyImpactedTables(xdmsHomePttId, mdn, Integer.parseInt(userProfile.getCorpId()),
                    //persisterTxn, null);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating the self etag - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
    }

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnSet, String corpId, KnPersisterTxn persisterTxn) {
        String methodName = "updateSubsTS(mdn,corpId,persisterTxn)";
        knLogger.debug(methodName, " mdnSet :", mdnSet);
        Map<String, Collection<KnDocChangeListDTO>> docUriMap = new HashMap<>();
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            String xdmsHomePttId = corpProfile.getXdmsHome();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            var docUriMapRes = corpXdmDao.updateSubsTS(mdnSet, null, persisterTxn);
            if (null != docUriMapRes && !docUriMapRes.isEmpty()) {
                docUriMap.putAll(docUriMapRes);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating the self etag - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, " docUriMap :-", docUriMap);
        return docUriMap;
    }

    public KnCorpResponseDTO updateEtag(int corpId, String profileMdn, KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) {
        String methodName = "updateEtag(int,String,KnCorpResponseDTO, KnPersisterTxn,Map<String,KnOPDirChgDTO>)";
        KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
        try {
            int groupCreatedBy = 0;
            //Step:get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Corporate Profile - ", corpProfile, "userProfileDetails: ", userProfileDetails);
            Set<KnCorpGroupListInfoDTO> groupList = new HashSet<>();
            var upmGroupIds = userProfileDetails.getGroupIds();
            knLogger.debug(methodName, "GroupIds to be Updated: ", upmGroupIds);
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(profileMdn);
            //using the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            IXDMServerDAO xdmServerDA0 = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
            //ETAG update for xdm directory DG.XDM_DIRECTORY ===> applicable for all the tasks
            corpSubsProvInfoUtil.updateDirectoryEtag(mdnList, xdmsHomePttId, etagMap, persisterTxn);
            List<String> authMdnsProfileMdns = new ArrayList<>();
            authMdnsProfileMdns.add(profileMdn);
            //DG.XDM_CORPRESOURCELISTINDEXDOC and DG.AUTHORIZATION_DOC ===> only applicable for contact or permission
            if (userProfileDetails.isContactEtagToBeUpdated() || userProfileDetails.isPermissionEtagToBeUpdated()) {
                contactInfoUtil.updateSubcribersResourceListIndexDoc(mdnList, xdmsHomePttId, null, persisterTxn);
                //select, update/insert into DG.AUTHORIZATION_DOC
                corpSubsProvInfoUtil.updateEtagAuthTable(xdmsHomePttId, authMdnsProfileMdns, null,
                        corpId, persisterTxn);
            }
            //select, update/insert into DG.EMERGENCY_DOC.===>  applicable only for emergency permission
            if (userProfileDetails.isEmergencyEtagToBeUpdated()) {
                corpXdmDao.insertOrUpdateEmergencyInfo(authMdnsProfileMdns, persisterTxn);
            }
            Map<Integer, Integer> groupEtagMap = new HashMap<>();
            Map<Integer, Collection<String>> groupDistMap = new HashMap<>();
            LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
            //updating group etag ===> applicable only for groups
            if (userProfileDetails.isGroupEtagToBeUpdated()) {
                Map<Integer, Collection<String>> currentGroupMember = groupInfoUtil.selectGroupMemberListForGroupIds(upmGroupIds, xdmsHomePttId, null);
                groupDistMap = groupInfoUtil.getGroupSubscriberDistList(upmGroupIds, xdmsHomePttId, null);
                groupInfoUtil.updateAddlTalkGroupImpactedTable(xdmsHomePttId, mdnList, persisterTxn, null);
                groupEtagMap = groupInfoUtil.updateGroupListEtag(upmGroupIds, xdmsHomePttId, persisterTxn);
                for (int groupID : upmGroupIds) {
                    Map<Integer, Collection<String>> currentGroupMemberListMap = new HashMap<>();
                    currentGroupMemberListMap.put(groupID, mdnList);
                    groupDistMap.get(groupID).addAll(mdnList);
                    if (userProfileDetails.getDeletedGrpMembers() != null) {
                        LinkedList<Integer> list = new LinkedList<>();
                        list.add(1);
                        delGroupMemStatus.put(groupID, list);
                    }
                    Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupInfoList(corpId, groupID, xdmsHomePttId, true, persisterTxn);
                    etagMap = commonInfoUtil.formSubscriberNotification(currentGroupMember, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(),
                            etagMap, groupDetailsMap, groupCreatedBy);
                    etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, null,
                            null, userProfileDetails.getAddedGroupMembersMap(), userProfileDetails.getDeletedGrpMembers(), groupDetailsMap.get(groupID).getGroupDisplayName(),
                            userProfileDetails.getModifiedGrpMembers(), groupDistMap, null, delGroupMemStatus, groupDetailsMap.get(groupID).getAvatar(),
                            false, false, null, null);
                }
            }
            int currentEtag = xdmServerDA0.getCurrentEtagForDirDoc(profileMdn, persisterTxn);
            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            String xcapRootUri = genInfoUtil.getXCAPRootURI(profileMdn, persisterTxn);
            dirChgDTO.setXcapRootURI(xcapRootUri);
            String dirDocUri = genInfoUtil.generateDirDocUri(profileMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setPocHome(corpProfile.getPocHome());
            dirChgDTO.setPresenceHome(corpProfile.getPresenceHome());
            dirChgDTO.setDirNewEtag(String.valueOf(currentEtag));
            dirChgDTOs.add(dirChgDTO);
            userProfileDetails.setDirChgDTOs(dirChgDTOs);
            userProfileDetails.setChangeLogMap(etagMap);
            knLogger.info(methodName, "Exit: ", " Notification dto for modifyUPM ", dirChgDTOs);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating the modifyUPM etag - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return userProfileDetails;
    }

    @Override
    public KnCorpResponseDTO deleteHierarchy(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteHierarchy(corpId, removedHierarchy, persisterTxn)";
        knLogger.info(methodName, " ENTRY : corpId - ", corpId, " removedHierarchy - ", removedHierarchy);
        KnCorpResponseDTO respDto = new KnCorpResponseDTO();
        try {
            String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
            commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            commonInfoUtil.getHierarchyDetails(corpId, removedHierarchy, persisterTxn);
            /*
            Getting the child nodes and adding them to the list
             */
            Map<String, KnHierarchyDepthInfoDTO> childNodeInfo = hierarchyInfoUtil.getChileNodesBasedOnHierarchyIds(removedHierarchy, xdmsHome, persisterTxn);
            Map<String, String> hierarchyNameMap = hierarchyInfoUtil.getHierarchyNameAndIdList(Integer.parseInt(corpId), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "childNodeInfo for the hierarchy to be removed - ", childNodeInfo);
            if(childNodeInfo != null && !childNodeInfo.isEmpty()){
                List<String> childHierarchyIds = childNodeInfo.values().stream().map(KnHierarchyDepthInfoDTO::getDescendantHierId).collect(Collectors.toList());
                removedHierarchy.addAll(childHierarchyIds);
            }
            commonInfoUtil.getSubscriberCountForHierarchyDeletion(corpId, removedHierarchy, persisterTxn);
            commonInfoUtil.getGroupCountForHierarchyId(corpId, removedHierarchy, persisterTxn);
            commonInfoUtil.validateAdditionalHierarchyResources(corpId, removedHierarchy, hierarchyNameMap,
                    Collections.singletonList(Integer.parseInt(corpId)), xdmsHome, persisterTxn);

            // Check each hierarchy node: reject if it is a shared node (ID_TYPE=4), clean up trust matrix otherwise
            KnXDMGroupHiearchyMapDAO groupHierarchyMapDAO = new KnCorpXDMTablesRegistry().createXDMGroupHiearchyMapDAO(xdmsHome);
            KnSharedTrustMatrixHierarchyDAO sharedTrustMatrixHierarchyDAO = new KnSharedTrustMatrixHierarchyDAO();
            for (String hierarchyId : removedHierarchy) {
                String hierarchyName = hierarchyNameMap.getOrDefault(hierarchyId,"");
                if (groupHierarchyMapDAO.isSharedHierarchyNode(hierarchyId, persisterTxn)) {
                    knLogger.error(methodName, "Cannot delete hierarchyId:", hierarchyId, " — SharedGroup exists for this node");
                    throw new KnXDMServerException(KnErrorCodes.BOEntity.HIERARCHY_HAS_SHARED_GROUP,
                            "SharedGroup exists for the organization '" + hierarchyName + "'. Remove the shared group before deleting the hierarchy.");
                }
                sharedTrustMatrixHierarchyDAO.deleteByHierarchyId(hierarchyId);
            }

            commonInfoUtil.deleteHierarchyInfo(corpId, removedHierarchy, persisterTxn);
            KnHierarchyDepthInfoDTO rootDepthInfo = hierarchyInfoUtil.getRootNodeBasedOnCorpId(Integer.valueOf(corpId), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "Root node info after deletion - ", rootDepthInfo);
            if(rootDepthInfo != null && rootDepthInfo.getDescendantHierId() != null) {
                knLogger.debug(methodName, "Checking if root node has any child nodes ", rootDepthInfo.getDescendantHierId());
                List<String> childNodes = hierarchyInfoUtil.getDescendantExtIds(rootDepthInfo.getDescendantHierId(), xdmsHome, persisterTxn);
                knLogger.debug(methodName, "Child nodes for root node - ", childNodes);
                if(childNodes == null || childNodes.isEmpty()){
                    knLogger.info(methodName, "No child nodes hence deleting the hierarchy");
                    List<String> nodesToBeDeleted = new ArrayList<>();
                    nodesToBeDeleted.add(rootDepthInfo.getAncestorHierId());
                    nodesToBeDeleted.add(rootDepthInfo.getDescendantHierId());
                    commonInfoUtil.deleteHierarchyInfo(corpId, nodesToBeDeleted, persisterTxn);
                }
            }
            populate(respDto);
        } catch (KnXDMServerException e) {
            populate(respDto, e);
        } catch (KnDAOException e) {
            knLogger.info(methodName, "KnDAOException occured while deleting deleteHierarchy - ", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION, "Error while deleting hierarchy", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleteHierarchy - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDto, e);
        }
        knLogger.info(methodName, "EXIT : deleteHierarchy Response - ", respDto);
        return respDto;
    }
}
