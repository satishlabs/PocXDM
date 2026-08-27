/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.common.KnCorpGroupContactDTO;
import com.kodiak.common.commdto.common.KnXDMCorpActivationDTO;
import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.request.KnXDMSubsAliasDetailsReqDTO;
import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.commdto.response.KnCorpGetCorpFSResponse;
import com.kodiak.common.commdto.response.KnXDMFailureRespDTO;
import com.kodiak.common.commdto.response.KnXDMSubsAliasDetailsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants;
import com.kodiak.frameworks.dbfw.collectors.KnSqlJobCollector;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDispatchDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpSubscrProfileController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;

import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnTPUserPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpPTTSettingsUtil;


import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.IDTYPE;
import static com.kodiak.common.resources.KnGeneralUtil.convertHexStringToBitSet;
import static com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil.convertStrToIntColl;
import static com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil.convertStrToIntInRange;
import static com.kodiak.xdms.server.common.resources.KnConstants.DISPATCH_TYPE_WEB;
import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnConstants.MCSCOMPLIANCE;
import static com.kodiak.xdms.server.common.resources.KnConstants.NUM_OF_LG_SUPPORTED;
import static com.kodiak.xdms.server.common.resources.KnConstants.ENABLE;
import static com.kodiak.xdms.server.common.resources.KnConstants.DISABLE;
import static com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.PRE_PROVISIONED;
import static com.kodiak.xdms.server.common.resources.KnConstants.USER_PROFILE_INDEX;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SELF_DND_FEATURE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.TRUE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_SUBS_PROFILE_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.UPDATE_SUBSCR_CORPADMIN_FEATURESET;
import static com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants.POC_HOME_NOT_ASSIGNED;

import org.springframework.http.HttpMethod;

/**
 * Created by asanjiv on 11/2/2016.
 */
public class KnCorpSubscrProfileController implements ICorpSubscrProfileController {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSubscrProfileController.class);

    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    //private KnAASFramework authorizationFwk;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnValidatorFramework validatorFW;
    private KnGeneralUtil generalUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpLicenseInfoUtil licenseInfoUtil;
    private KnCorpUserProfileUtil userProfileUtil;

    private KnProvInfoUtil provInfoUtil;

    private KnCorpPTTSettingsUtil pttSettingsUtil;


    public KnCorpSubscrProfileController() {
        commonInfoUtil = new KnCorpCommonInfoUtil();
        contactInfoUtil = new KnCorpContactInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        licenseInfoUtil = new KnCorpLicenseInfoUtil();
        generalUtil = new KnGeneralUtil();
        //authorizationFwk = KnAASFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        genInfoUtil = KnGenInfoUtil.getInstance();
        userProfileUtil=new KnCorpUserProfileUtil();

        provInfoUtil = new KnProvInfoUtil();

        pttSettingsUtil = new KnCorpPTTSettingsUtil();

    }

    @Override
    public KnSubscrFeatureSetRespDTO getAllCorpSubscrFeatureSets(KnIPCorpAuthInfoDTO ipCorpAuthInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getAllCorpSubscrFeatureSets()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed with corpId- ", ipCorpAuthInfoDTO.getCorpId());
        KnSubscrFeatureSetRespDTO respDTO = new KnSubscrFeatureSetRespDTO();
        try {

            int corpId = ipCorpAuthInfoDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
            hookIPDTO.setAction(KnActions.ACTIONS.GET_ALL_CORP_SUBSRIBER_FEATURE_SETS);
            hookIPDTO.setData(ipCorpAuthInfoDTO);
            Map<String, Object> customParams = ipCorpAuthInfoDTO.getCustomParamMap();
            if (ipCorpAuthInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipCorpAuthInfoDTO.setCustomParamMap(customParams);
                knLogger.debug(methodName, "corpProfile.getCorpMasterListEtag() - ", corpProfile.getCorpMasterListEtag());
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook");
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    knLogger.debug(methodName, "Call After Hook");
                    respDTO = (KnSubscrFeatureSetRespDTO) responseDTO.getCustomParamMap().get(GET_SUBSCR_FEATURE_CUSTOM_HOOK_CALL_DATA);

                }
            } else {
                respDTO = corpSubsProvInfoUtil.getSubscriberFeatureSets(corpId, xdmsHome, true, persisterTxn);
            }
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
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
    public KnCorpResponseDTO updateCorpAdminFS(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateCorpAdminFS()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscrFeatureInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        Map<String, KnOPDirChgDTO> etagMap = new HashMap<>();
        Map<String, KnOPDispatchDirChgDTO> msDtoMap = new HashMap<>();
        List <String> profileMdnList = new ArrayList<String>();
        try {
            int corpId = ipSubscrFeatureInfoDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
            hookIPDTO.setAction(KnActions.ACTIONS.MODIFY_SUBSCR_CORP_ADMINFS);
            hookIPDTO.setData(ipSubscrFeatureInfoDTO);
            Map<String, Object> customParams = ipSubscrFeatureInfoDTO.getCustomParamMap();
            if (ipSubscrFeatureInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                List<String> mdnList = ipSubscrFeatureInfoDTO.getSubscrFeatureInfoDTOList().stream().map(KnSubscrFeatureInfoDTO::getMdn).collect(Collectors.toList());
                contactInfoUtil.checkValidHierarchySubs(mdnList, customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscrFeatureInfoDTO.setCustomParamMap(customParams);
                knLogger.debug(methodName, "corpProfile.getCorpMasterListEtag() - ", corpProfile.getCorpMasterListEtag());
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook");
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
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
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of locWatcherConfig", paramNameValueMapCommon);
            String bulkLimit = paramNameValueMapCommon.get(CORPADMINFSBULKAPILIMIT);

            String priRecordingServerIP = paramNameValueMapCommon.get(MSI_PRIMARY_WAVE_RECORDING_SERVER_IP);
            String priRecordingServerPort = paramNameValueMapCommon.get(MSI_PRIMARY_WAVE_RECORDING_SERVER_PORT);
            String geoRecordingServerIP = paramNameValueMapCommon.get(MSI_GEO_WAVE_RECORDING_SERVER_IP);
            String geoRecordingServerPort = paramNameValueMapCommon.get(MSI_GEO_WAVE_RECORDING_SERVER_PORT);



            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            List<KnSubscrFeatureInfoDTO> resSubscrList = ipSubscrFeatureInfoDTO.getSubscrFeatureInfoDTOList();
            List<String> mdnList = new ArrayList<>();
            Map<String, KnSubscrFeatureInfoDTO> reqSubscrInfoMap = new HashMap<>();
            for (KnSubscrFeatureInfoDTO subscrDto : resSubscrList) {
                reqSubscrInfoMap.put(subscrDto.getMdn(), subscrDto);
                mdnList.add(subscrDto.getMdn());
            }
            if (bulkLimit != null && reqSubscrInfoMap.size() > Integer.parseInt(bulkLimit)) {
                knLogger.error(methodName, "Max Configured Sized Exceeded for Input Request");
                throw new KnCorpBOValidationException(MAX_REQUEST_LIMIT_EXCEEDS,
                        "Max Configured Sized Exceeded for Input Request --", CORP_SUBS_PROFILE_MANAGER,
                        UPDATE_SUBSCR_CORPADMIN_FEATURESET, "", Arrays.asList(bulkLimit).toString(), "");
            }

            String pttRecordingVal = paramNameValueMapCommon.get(PTT_RECORDING_FLAG);
            String dataRecordingVal = paramNameValueMapCommon.get(DATA_RECORDING_FLAG);
            String videoRecordingVal = paramNameValueMapCommon.get(VIDEO_RECORDING_FLAG);
            String selfDndPrivilege = paramNameValueMapCommon.get(SELF_DND_FEATURE);
            String largeAgencyDispatch = paramNameValueMapCommon.get(LARGE_AGENCY_DISPATCH_FEATURE);

            boolean pttRecordingFlag = ENABLE.toString().equalsIgnoreCase(pttRecordingVal);
            boolean dataRecordingFlag = ENABLE.toString().equalsIgnoreCase(dataRecordingVal);
            boolean videoRecordingFlag = ENABLE.toString().equalsIgnoreCase(videoRecordingVal);
            boolean selfDndPrivilegeFlag = ENABLE.toString().equals(selfDndPrivilege);
            boolean largeAgencyDispatchFlag = ENABLE.toString().equals(largeAgencyDispatch);

            /* Max allowed Corp groups for Standard Dispatch Client i.e. DG.XDMS_SVC_CONFIG:MaxCorpGrpsPerSubscr */
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            int maxCorpGroupPerSubs = xdmsServiceConfigDTO.getMaxCorpGrpsPerSubs();

            /* Get Subscriber Group Ids */
            Map<Integer, List<String>> subscGroupIdMap = groupInfoUtil.getSubscriberGroupIds(mdnList, xdmsHome, true, persisterTxn);
            Set<Integer> groupIds = subscGroupIdMap.keySet();
            List<Integer> grpIds = new ArrayList<Integer>(groupIds);
            int grpIdCount = grpIds.size();

            /* Get Subscriber Group Id in ABDG Group*/
            Map<Integer, List<String>> corpGroupIdMap = groupInfoUtil.getCorpGroupIds(groupIds, xdmsHome, true, persisterTxn);
            Set<Integer> corpGroupIdSet = corpGroupIdMap.keySet();
            List<Integer> corpGrpIds = new ArrayList<Integer>(corpGroupIdSet);
            int corpGrpIdCount = corpGrpIds.size();

            int subsCorpGrpCount = grpIdCount - corpGrpIdCount;

            knLogger.debug(methodName, "reqSubscrInfoMap", KnGDPRTemplate.mapKeyMdn(reqSubscrInfoMap));
            Map<String, KnCorpSubscriberDTO> subsProfileMap = corpSubsProvInfoUtil.getSubscriberProfileDetails(reqSubscrInfoMap.keySet(), corpId, persisterTxn, xdmsHome);
            KnCorpSubscriberDTO corpSubscriber = new KnCorpSubscriberDTO();

            for (String mdn : mdnList) {
                corpSubscriber = subsProfileMap.get(mdn);
            }
            int clientType = corpSubscriber.getClientType();
            KnContactDetailsPersistDTO corpGroupInfoPersistDTO = new KnContactDetailsPersistDTO();
            corpGroupInfoPersistDTO.setInputDTO(ipSubscrFeatureInfoDTO);
            corpGroupInfoPersistDTO.setContactCorpDetails(subsProfileMap);
            corpGroupInfoPersistDTO.setPttRecordingFlag(pttRecordingFlag);
            corpGroupInfoPersistDTO.setDataRecordingFlag(dataRecordingFlag);
            corpGroupInfoPersistDTO.setVideoRecordingFlag(videoRecordingFlag);
            corpGroupInfoPersistDTO.setSysSelfDndPrivilege(selfDndPrivilegeFlag);
            corpGroupInfoPersistDTO.setCorpSelfDndPrivilege(KnGeneralUtil.getFeatureBitValue(corpProfile.getCorpFS2(), FEATURE_SET.SELF_DND_PRIVILEGE.value()));
            List<Integer> selfDndPrivilegeList= new ArrayList<>();
            reqSubscrInfoMap.values().forEach(fsBits -> selfDndPrivilegeList.add(fsBits.getSelfDnDPrivilege()));
            corpGroupInfoPersistDTO.setReqSelfDndPrivilegeList(selfDndPrivilegeList);

            corpGroupInfoPersistDTO.setSysLargeAgencyDispatch(largeAgencyDispatchFlag);
            corpGroupInfoPersistDTO.setCorpLargeAgencyDispatch(
                    KnGeneralUtil.getFeatureBitValue(corpProfile.getCorpFS2(), FEATURE_SET.LARGE_AGENCY_DISPATCH.value()));
            List<Integer> largeAgencyDispatchList= new ArrayList<>();
            reqSubscrInfoMap.values().forEach(fsBits -> largeAgencyDispatchList.add(fsBits.getLargeAgencyDispatch()));
            corpGroupInfoPersistDTO.setReqLargeAgencyDispatchList(largeAgencyDispatchList);

            corpGroupInfoPersistDTO.setMaxCorpGroupPerSubs(maxCorpGroupPerSubs);
            corpGroupInfoPersistDTO.setSubsCorpGrpCount(subsCorpGrpCount);
            corpGroupInfoPersistDTO.setClientType(clientType);

            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", corpGroupInfoPersistDTO);
            validatorFW.validate(corpGroupInfoPersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            Map<String, KnCorpSubscriberDTO> updateSubscrMap = new HashMap<>(reqSubscrInfoMap.size());
            //List<String> locPublishMdn = new ArrayList<>();
            KnOPDispatchDirChgDTO msDto = null;
            //boolean locPublishChanged = false;
            Map<String, Long> subsProfileEtag = new HashMap<>();
            List<String> mdnListFailed = new ArrayList<>();
            Collection<String> disabledPttVideoFeatureList=new ArrayList<>();
            Collection<KnRecordingTargetInfoDTO> reqRecordingTargetInfoDTO=new ArrayList<>();
            for (Map.Entry<String, KnSubscrFeatureInfoDTO> entry : reqSubscrInfoMap.entrySet()) {
                boolean isProfileMdnUpdate = false;
                msDto = new KnOPDispatchDirChgDTO();
                String mdn = entry.getKey();
                KnSubscrFeatureInfoDTO reqCorpAdminFeature = entry.getValue();
                KnCorpSubscriberDTO corpSubscriberDTO = subsProfileMap.get(mdn);
                String corpAdminFSDB = corpSubscriberDTO.getCorpAdminFS2();
                knLogger.debug(methodName, "corpAdminFSDB - ", corpAdminFSDB);
                BitSet corpAdminBitSet = featureSetUtil.convertHexStringToBitSet(corpAdminFSDB);
                knLogger.debug(methodName, "corpAdminBitSet - ", corpAdminBitSet);
                if (reqCorpAdminFeature.getPtxBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.PUSHTOTEXT.value(), reqCorpAdminFeature.getPtxBit() == 1);
                }
                if (reqCorpAdminFeature.getPtmdBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.PUSHTOMULTIMEDIA.value(), reqCorpAdminFeature.getPtmdBit() == 1);
                    corpAdminBitSet.set(FEATURE_SET.MCDATA_FD_FEATURE.value(), reqCorpAdminFeature.getPtmdBit() == 1);
                }
                if (reqCorpAdminFeature.getPtlocBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.PUSHTOLOCATION.value(), reqCorpAdminFeature.getPtlocBit() == 1);
                }
                //MINT-20082 - Setting the SDS FEATURE bit (64th) based on the PTX and PTLOC feature
                corpAdminBitSet.set(FEATURE_SET.MCDATA_SDS_FEATURE.value(),
                        (corpAdminBitSet.get(FEATURE_SET.PUSHTOTEXT.value()) ||
                                corpAdminBitSet.get(FEATURE_SET.PUSHTOLOCATION.value())));

                if (reqCorpAdminFeature.getTgscClientBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.TLKGRPSCANCLIENT.value(), reqCorpAdminFeature.getTgscClientBit() == 1);
                }
                if (reqCorpAdminFeature.getGeofncBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.GEOFENCEFEATURE.value(), reqCorpAdminFeature.getGeofncBit() == 1);
                }
                if (reqCorpAdminFeature.getBrdcrmbBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.BREADCUMB.value(), reqCorpAdminFeature.getBrdcrmbBit() == 1);
                }
                if (reqCorpAdminFeature.getAmbientListeningBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.AMBIENTLISTENING.value(), reqCorpAdminFeature.getAmbientListeningBit() == 1);
                }
                if (reqCorpAdminFeature.getDiscreteListeningBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.DISCRETELISTENING.value(), reqCorpAdminFeature.getDiscreteListeningBit() == 1);
                }
                if (reqCorpAdminFeature.getUserCheckCorpBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.USERCHECK.value(), reqCorpAdminFeature.getUserCheckCorpBit() == 1);
                }
                if (reqCorpAdminFeature.getUserEnableCorpBit() != null) {
                    corpAdminBitSet.set(FEATURE_SET.USERENABLEDISABLE.value(), reqCorpAdminFeature.getUserEnableCorpBit() == 1);
                    isProfileMdnUpdate = true;
                }

                if (reqCorpAdminFeature.getLocPublishCorpBit() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.ONDEMLOCATION.value(), reqCorpAdminFeature.getLocPublishCorpBit() == 1);
                }

                if (reqCorpAdminFeature.getMcVideoTx() != null) {
                    corpAdminBitSet.set(FEATURE_SET.MCVIDEOTX.value(), reqCorpAdminFeature.getMcVideoTx() == 1);
                }
                if (reqCorpAdminFeature.getMcVideoRx() != null) {
                    corpAdminBitSet.set(FEATURE_SET.MCVIDEORX.value(), reqCorpAdminFeature.getMcVideoRx() == 1);
                }
                if (reqCorpAdminFeature.getMcVideoGroupRx() != null) {
                    corpAdminBitSet.set(FEATURE_SET.MCVIDEOGROUPRX.value(), reqCorpAdminFeature.getMcVideoGroupRx() == 1);
                }
                if (reqCorpAdminFeature.getMcVideoConfirmedPull() != null) {
                    corpAdminBitSet.set(FEATURE_SET.MCVIDEOCONFIRMEDPULL.value(), reqCorpAdminFeature.getMcVideoConfirmedPull() == 1);
                }
                if (reqCorpAdminFeature.getMcDevice() != null) {
                    if (!(convertHexStringToBitSet(corpSubscriberDTO.getSubscriberFs2()).get(FEATURE_SET.MCDEVICE.value()))) {
                        mdnListFailed.add(mdn);
                    }
                    corpAdminBitSet.set(FEATURE_SET.MCDEVICE.value(), reqCorpAdminFeature.getMcDevice() == 1);
                    isProfileMdnUpdate = true;
                }
                if (reqCorpAdminFeature.getWdsPatching() != null) {
                    corpAdminBitSet.set(FEATURE_SET.WDSPATCHING.value(), reqCorpAdminFeature.getWdsPatching() == 1);
                    isProfileMdnUpdate = true;
                }
                if (reqCorpAdminFeature.getWdsRecording() != null) {
                    corpAdminBitSet.set(FEATURE_SET.WDSRECORDING.value(), reqCorpAdminFeature.getWdsRecording() == 1);
                    isProfileMdnUpdate = true;
                }
                if (reqCorpAdminFeature.getPttRecording() != null) {
                    corpAdminBitSet.set(FEATURE_SET.PTT_RECORDING_FLAG.value(), reqCorpAdminFeature.getPttRecording() == 1);
                    isProfileMdnUpdate = true;
                }
                if (reqCorpAdminFeature.getDataRecording() != null) {
                    corpAdminBitSet.set(FEATURE_SET.DATA_RECORDING_FLAG.value(), reqCorpAdminFeature.getDataRecording() == 1);
                    isProfileMdnUpdate = true;
                }
                if (reqCorpAdminFeature.getVideoRecording() != null) {
                    corpAdminBitSet.set(FEATURE_SET.VIDEO_RECORDING_FLAG.value(), reqCorpAdminFeature.getVideoRecording() == 1);
                    isProfileMdnUpdate = true;
                }
                //Recording feature
                if ((reqCorpAdminFeature.getPttRecording() != null && reqCorpAdminFeature.getPttRecording() == 0)
                        && (reqCorpAdminFeature.getVideoRecording() != null && reqCorpAdminFeature.getVideoRecording() == 0)) {
                    disabledPttVideoFeatureList.add(mdn);
                }
                if (reqCorpAdminFeature.getSelfDnDPrivilege() != null) {
                    corpAdminBitSet.set(FEATURE_SET.SELF_DND_PRIVILEGE.value(), reqCorpAdminFeature.getSelfDnDPrivilege() == 1);
                }
                if (reqCorpAdminFeature.getLargeAgencyDispatch() != null) {
                    corpAdminBitSet.set(FEATURE_SET.LARGE_AGENCY_DISPATCH.value(), reqCorpAdminFeature.getLargeAgencyDispatch() == 1);
                }

                int recType = 0;
                if ((reqCorpAdminFeature.getPttRecording() != null && reqCorpAdminFeature.getPttRecording() == 1)
                        && (reqCorpAdminFeature.getVideoRecording() != null && reqCorpAdminFeature.getVideoRecording() == 0)) {
                    recType=REC_TYPE.AUDIO.value();
                }
                if ((reqCorpAdminFeature.getPttRecording() != null && reqCorpAdminFeature.getPttRecording() == 0)
                        && (reqCorpAdminFeature.getVideoRecording() != null && reqCorpAdminFeature.getVideoRecording() == 1)) {
                    recType=REC_TYPE.VIDEO.value();
                }
                if ((reqCorpAdminFeature.getPttRecording() != null && reqCorpAdminFeature.getPttRecording() == 1)
                        && (reqCorpAdminFeature.getVideoRecording() != null && reqCorpAdminFeature.getVideoRecording() == 1)) {
                    recType=REC_TYPE.BOTH.value();
                }

                if (recType != 0) {
                    if (!((null != priRecordingServerPort && null != priRecordingServerIP) &&
                            (null != geoRecordingServerPort && null != geoRecordingServerIP))) {
                        knLogger.error(methodName, "Recording configurations are not configured correctly");
                        throw new KnCorpBOValidationException(CONFIGURATIONS_NOT_VALID_FOR_RECORDING,
                                "Recording configurations are not configured correctly", "Configurations missing in DB");
                    }

                    reqRecordingTargetInfoDTO.add(new KnRecordingTargetInfoDTO()
                            .setTarget(mdn)
                            .setPriRecIp(priRecordingServerIP)
                            .setPriRecPort(null != priRecordingServerPort ? Integer.parseInt(priRecordingServerPort) : null)
                            .setSecRecIp(geoRecordingServerIP)
                            .setSecRecPort(null != geoRecordingServerPort ? Integer.parseInt(geoRecordingServerPort) : null)
                            .setRecType(recType)
                    );
                }

                if (mdnListFailed.isEmpty()) {
                    knLogger.debug(methodName, "corpAdminBitSet - ", corpAdminBitSet);
                    String corpAdminFSReq = featureSetUtil.convertBitSetToHexString(corpAdminBitSet);
                    knLogger.debug(methodName, "corpAdminFSReq - ", corpAdminFSReq);
                    String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmsHome, corpSubscriberDTO.getClientPVmajorVer());
                    String xdmsFS2 = corpSubscriberDTO.getXdmsFs2();
                    String userProfileFS2 = corpSubscriberDTO.getUserProfileFS2();
                    if (userProfileFS2 == null) {
                        userProfileFS2 = featureSetUtil.getDefFinalUserProfileFS();
                    }
                    String modifiedActiveFS = featureSetUtil.generateActiveFeatBitSet(corpSubscriberDTO.getPocHome(), corpSubscriberDTO.getPresenceHome(),
                            corpSubscriberDTO.getXdmsHome(), corpSubscriberDTO.getClientFs2(), corpSubscriberDTO.getSubscriberFs2(), corpProfile.getCorpFS2(),
                            corpSubscriberDTO.getOpsFs2(), corpAdminFSReq, clientCapOverrideBitMask, xdmsFS2, userProfileFS2);
                    knLogger.debug(methodName, "modifiedActiveFS - ", modifiedActiveFS);
                    msDto.setOldActiveFS(corpSubscriberDTO.getSubsActiveFS2());
                    msDto.setActiveFSChanged(!corpSubscriberDTO.getSubsActiveFS2().equals(modifiedActiveFS));
                    msDto.setActiveFS2(modifiedActiveFS);
                    msDto.setProtoVersion(String.valueOf(subsProfileMap.get(mdn).getClientPVmajorVer()));
                    msDto.setCorpId(corpId);
                    KnCorpSubscriberDTO modifiedCorpSubscriberDTO = new KnCorpSubscriberDTO();
                    modifiedCorpSubscriberDTO.setCorpAdminFS2(corpAdminFSReq);
                    modifiedCorpSubscriberDTO.setSubsActiveFS2(modifiedActiveFS);
                    long lastProfileEtag = System.currentTimeMillis();
                    subsProfileEtag.put(mdn, lastProfileEtag);
                    modifiedCorpSubscriberDTO.setLastProfileUpdateTime(lastProfileEtag);
                    modifiedCorpSubscriberDTO.setMdn(mdn);
                    msDto.setLastProfileUpdateTime(modifiedCorpSubscriberDTO.getLastProfileUpdateTime());
                    msDtoMap.put(mdn, msDto);
                    updateSubscrMap.put(mdn, modifiedCorpSubscriberDTO);
                    if (isProfileMdnUpdate) {
                        Map<String, KnCorpSubscriberDTO> mdnUpmFsMap = corpSubsProvInfoUtil.getProfileMdnAndUpmfsByBaseMdn(mdn, xdmsHome,
                                persisterTxn);
                        mdnUpmFsMap.remove(mdn);
                        //recalculate corpAdminFS for profileMDN with request data
                        corpSubsProvInfoUtil.prepareProfileMDNCorpAdminFs(mdnUpmFsMap, reqCorpAdminFeature);
                        respDTO.setMdnUpmFsMap(mdnUpmFsMap);
                        if (mdnUpmFsMap.size() > 0) {
                            Map<String, String> profileMdnActiveFsMap = commonInfoUtil.calculateActiveFS2WithUPMFS(1,
                                    corpSubscriberDTO.getPocHome(), corpSubscriberDTO.getPresenceHome(),
                                    corpSubscriberDTO.getXdmsHome(), corpSubscriberDTO.getClientFs2(),
                                    corpSubscriberDTO.getSubscriberFs2(), corpProfile.getCorpFS2(),
                                    corpSubscriberDTO.getOpsFs2(), clientCapOverrideBitMask,
                                    mdnUpmFsMap);
                            profileMdnList.addAll(profileMdnActiveFsMap.keySet());
                            KnCorpSubscriberDTO corpSubscriberProfileMdnDTO = new KnCorpSubscriberDTO();
                            corpSubscriberProfileMdnDTO.setServiceAuthStatus(corpSubscriberDTO.getServiceAuthStatus());
                            corpSubscriberProfileMdnDTO.setLastProfileUpdateTime(lastProfileEtag);
                            corpSubscriberProfileMdnDTO.setIMEI(corpSubscriberDTO.getIMEI());
                            corpSubscriberProfileMdnDTO.setClientPassword(corpSubscriberDTO.getClientPassword());
                            corpSubscriberProfileMdnDTO.setUserAgent(corpSubscriberDTO.getUserAgent());
                            corpSubscriberProfileMdnDTO.setVocoderId(corpSubscriberDTO.getVocoderId());
                            corpSubscriberProfileMdnDTO.setClientPVmajorVer(corpSubscriberDTO.getClientPVmajorVer());
                            corpSubscriberProfileMdnDTO.setClientPVminorVer(corpSubscriberDTO.getClientPVminorVer());
                            corpSubscriberProfileMdnDTO.setLastActivationTime(corpSubscriberDTO.getLastActivationTime());
                            corpSubscriberProfileMdnDTO.setSwType(corpSubscriberDTO.getSwType());
                            corpSubscriberProfileMdnDTO.setPlatformType(corpSubscriberDTO.getPlatformType());
                            corpSubscriberProfileMdnDTO.setDynamicQosFlag(corpSubscriberDTO.getDynamicQosFlag());
                            if (corpSubscriberDTO.getDerivedKey() != null)
                                corpSubscriberProfileMdnDTO.setDerivedKey(KnGeneralUtil.convertHexToAscii(corpSubscriberDTO.getDerivedKey()));
                            corpSubscriberProfileMdnDTO.setServiceStatusOp(corpSubscriberDTO.getServiceStatusOp());
                            corpSubscriberProfileMdnDTO.setClientType(corpSubscriberDTO.getClientType());
                            corpSubscriberProfileMdnDTO.setSubscriberFs2(corpSubscriberDTO.getSubscriberFs2());
                            corpSubscriberProfileMdnDTO.setClientFs2(corpSubscriberDTO.getClientFs2());
                            corpSubscriberProfileMdnDTO.setOpsFs2(corpSubscriberDTO.getOpsFs2());
                            corpSubscriberProfileMdnDTO.setXdmsFs2(corpSubscriberDTO.getXdmsFs2());
                            corpSubscriberProfileMdnDTO.setCorpAdminFS2(corpSubscriberDTO.getCorpAdminFS2());
                            corpSubscriberProfileMdnDTO.setLicenseType(corpSubscriberDTO.getLicenseType());
                            corpSubsProvInfoUtil.updateProfileMdnDetails(corpSubscriberProfileMdnDTO, profileMdnActiveFsMap,
                                    xdmsHome, mdnUpmFsMap, persisterTxn);
                            respDTO.setProfileMdnActiveFsMap(profileMdnActiveFsMap);
                        }
                    }
                }
            }
            if (!mdnListFailed.isEmpty()) {
                knLogger.error(methodName, "MC Device but in SubscrberFS is disabled");
                throw new KnCorpBOValidationException(MC_DEVICE_DISABLED,
                        "MC Device Bit is disabled for the subscriber --", CORP_SUBS_PROFILE_MANAGER,
                        UPDATE_SUBSCR_CORPADMIN_FEATURESET, "", mdnListFailed.toString(), "");
            }

            corpSubsProvInfoUtil.updateSubscrProfiles(updateSubscrMap, xdmsHome, persisterTxn);

            //RECORDING_TARGET_INFO
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String targetInfoOptimization = microServicesParamNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.TARGET_INFO_OPTIMIZATION);
            if (!com.kodiak.xdms.server.common.util.KnGeneralUtil.isTargetInfoOptimizationEnabled(targetInfoOptimization)) {
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHome);

                Set<String> targetMdnSet = reqSubscrInfoMap.keySet();
                targetMdnSet.removeAll(disabledPttVideoFeatureList);

                Collection<KnRecordingTargetInfoDTO> dbRecordingTargetList = commonXDMServerDAO
                        .getRecordingInfoTargetByTarget(targetMdnSet, persisterTxn);
                knLogger.debug(methodName, " dbRecordingTargetList :", dbRecordingTargetList);

                List<String> dbTargetList = dbRecordingTargetList.stream().map(KnRecordingTargetInfoDTO::getTarget).collect(Collectors.toList());

                Map<String, Integer> reqUpdateTargetRecTypeMap = reqRecordingTargetInfoDTO.stream()
                        .collect(Collectors.toMap(KnRecordingTargetInfoDTO::getTarget, KnRecordingTargetInfoDTO::getRecType));
                reqUpdateTargetRecTypeMap.entrySet().removeIf(s -> !dbTargetList.contains(s.getKey()));
                knLogger.debug(methodName, " reqUpdateTargetRecTypeMap :", reqUpdateTargetRecTypeMap);
                //update recordingTargetInfo
                if (reqUpdateTargetRecTypeMap.size() > 0) {
                    commonXDMServerDAO.updateRecordingInfoTargetByTarget(reqUpdateTargetRecTypeMap, persisterTxn);
                }

                reqRecordingTargetInfoDTO.removeIf(s -> dbTargetList.contains(s.getTarget()));
                knLogger.debug(methodName, " reqRecordingTargetInfoDTO:", reqRecordingTargetInfoDTO);
                //insert to recordingTargetInfo
                if (!reqRecordingTargetInfoDTO.isEmpty()) {
                    commonXDMServerDAO.createRecordingInfoForTarget(reqRecordingTargetInfoDTO, persisterTxn);
                }

                knLogger.debug(methodName, " disabledPttVideoFeatureList:", disabledPttVideoFeatureList);
                //delete from recordingTargetInfo
                if (disabledPttVideoFeatureList.size() > 0) {
                    commonXDMServerDAO.deleteRecordingInfoTargetByTarget(disabledPttVideoFeatureList, persisterTxn);
                }
            }
            //if (!locPublishMdn.isEmpty()) {
            Map<String, List<String>> subsCantLstMap = contactInfoUtil.getSubsContactList(reqSubscrInfoMap.keySet(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subsCantLstMap - ", subsCantLstMap);
            Collection<String> subsMDNs = contactInfoUtil.getPocSubscribersDetails(subsCantLstMap.keySet(), corpId, xdmsHome, persisterTxn);
            Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
            Collection<KnCorpSubscriberDTO> contactLists = null;
            if (subsMDNs != null && !subsMDNs.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : subsCantLstMap.entrySet()) {
                    contactLists = new ArrayList<>();
                    for (String contact : entry.getValue()) {
                        KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                        subscriberDTO.setMdn(contact);
                        subscriberDTO.setSubsActiveFS2(updateSubscrMap.get(contact).getSubsActiveFS2());
                        contactLists.add(subscriberDTO);
                    }
                    modifiedContactMap.put(entry.getKey(), contactLists);
                }
                etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHome, subsMDNs, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap after updateSubcribersResourceListIndexDoc - ", etagMap);
            }
            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, modifiedContactMap, null);
            respDTO.setChangeLogMap(etagMap);

			Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = userProfileUtil.profileMdnEtagUpdate(null,
					String.valueOf(corpId), profileMdnList, null, xdmsHome, persisterTxn);
			respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
			if (profileMdnEtagMap != null && profileMdnEtagMap.isEmpty()) {
				respDTO.setMcsXcapRootUriMap(
						genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
			}
            //}
            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            Map<String, Integer> subsrDirecEtagMap = commonInfoUtil.updateAndGetDirecEtag(reqSubscrInfoMap.keySet(), persisterTxn, xdmsHome);
            knLogger.debug(methodName, "subsrDirecEtagMap - ", subsrDirecEtagMap);
            for (String mdn : reqSubscrInfoMap.keySet()) {
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(subsProfileEtag.get(mdn) != null ? subsProfileEtag.get(mdn)
                        : subsProfileMap.get(mdn).getLastProfileUpdateTime()));
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                chgDocList.add(docChgDTO);
                //populating the XDM Directory DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                dirChgDTO.setPocHome(subsProfileMap.get(mdn).getPocHome());
                dirChgDTO.setPresenceHome(subsProfileMap.get(mdn).getPresenceHome());
                dirChgDTO.setDocChgDTO(chgDocList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag(String.valueOf(subsrDirecEtagMap.get(mdn)));
                int newEtag = subsrDirecEtagMap.get(mdn) + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                dirChgDTO.setProtoVersion(subsProfileMap.get(mdn).getClientPVmajorVer() + ".0");
                //populating the Dir chg DTO to response
                dirChgDTOs.add(dirChgDTO);
            }
            knLogger.debug(methodName, "dirChgDTOs - ", dirChgDTOs);
            respDTO.setMsDtoMap(msDtoMap);
            respDTO.setDirChgDTOs(dirChgDTOs);

            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while updating subscriber corpAdminFS Info - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOValidationException e) {
            knLogger.error(methodName, "KnCorpBOValidationException occured while updating subscriber corpAdminFS Info -", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating subscriber corpAdminFS Info - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpActivationRespDTO getActivationCode(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getActivationCode(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY : Input DTO Passed ipSubscriberInfoDTO - ", ipSubscriberInfoDTO);

        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            Collection<String> mdnList = ipSubscriberInfoDTO.getMdnList();
            knLogger.debug( methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            knLogger.debug( methodName, "Retrieved Corp Profile details - ", corpProfile);
            int maxMembersAllowed = corpProfile.getMaxSubsAllowedGenActvReq();
            String xdmsHomePttId = corpProfile.getXdmsHome();
            KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
            corpMdnListPersistDto.setCorpId(Integer.parseInt(corpId));
            corpMdnListPersistDto.setAddedMdnList(mdnList);
            KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto, xdmsHomePttId, true, persisterTxn);
            Collection<String> pocMdnList = pocMdnPersistDto.getMdnList();
            Collection<KnCorpSubscriberDTO> extMdnList = pocMdnPersistDto.getExternalMdnList();
            knLogger.debug( methodName, "pocMdnList- ", pocMdnList);
            //get the subscriber client_type, auth status.  filter out the external contacts
            Collection<KnCorpSubscriberDTO> subscProfile = contactInfoUtil.getSubscriberProfileInfo(mdnList, Integer.valueOf(corpId), xdmsHomePttId, true, persisterTxn);

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs((List<String>) mdnList, customParams, xdmsHomePttId, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHomePttId);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_ACTIVATION_CODE);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Map<String, Object> customRespMap = null;
                knLogger.debug( methodName, "customParams Before hook invokation - ", customParams);
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    customRespMap = responseDTO.getCustomParamMap();
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error( methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
                knLogger.debug( methodName, "customRespMap after hook invokation - ", customRespMap);
                respDTO.setCustomParamMap(customRespMap);
            }
            long corpEtag = corpProfile.getCorpMasterListEtag();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of commonConfig", microServicesParamNameValueMap);
            String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            persistDTO.setPocSubscMdnList(pocMdnList);
            persistDTO.setAddedMdnDTO(subscProfile);
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setContactCountInRequest(mdnList.size());
            persistDTO.setMaxAllowedContactCountPerRequest(corpProfile.getMaxContactsPerRequest());
            persistDTO.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");

            //variable to hold the distinct client types.
            Set<Integer> clientTypes = new HashSet<>();
            List<String> filteredMdns = new ArrayList<>();
            for (KnCorpSubscriberDTO subscriberDTO : subscProfile) {
                if(subscriberDTO.getClientType() != com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.SG_CLIENT.value() &&
                        subscriberDTO.getClientType() != com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.SU_CLIENT.value())
                    filteredMdns.add(subscriberDTO.getMdn());
            }
            knLogger.info(methodName, "after filtering SU and SG mdns",filteredMdns );
            Collection<KnXDMCorpActivationDTO> activationCodeList = new ArrayList<KnXDMCorpActivationDTO>();
            Map<String, KnIPCorpActivationDTO> activationCodeDetailsMap = contactInfoUtil.getActivationCodeForCorpoateSubscriber(filteredMdns, xdmsHomePttId, true, persisterTxn);
            for (String mdn : filteredMdns) {
                KnIPCorpActivationDTO actDTO = activationCodeDetailsMap.get(mdn);
                KnXDMCorpActivationDTO corpActivationDTO = new KnXDMCorpActivationDTO();
                corpActivationDTO.setMdn(mdn);
                if (actDTO != null) {
                    String actCode = actDTO.getActivationCode();
                    if (actCode != null) {
                        corpActivationDTO.setActivationCode(actCode);
                    }

                    Timestamp exTime = actDTO.getExpiryTimestamp();
                    if (exTime != null) {
                        exTime.getTime();
                        corpActivationDTO.setExpiryTimestamp(String.valueOf(exTime.getTime()));
                    }
                    Timestamp activationTime = actDTO.getActivationTimestamp();
                    if (activationTime != null) {
                        corpActivationDTO.setActivationTimestamp(String.valueOf(activationTime.getTime()));
                    }
                }
                activationCodeList.add(corpActivationDTO);
            }
            respDTO.setActivationCodeList(activationCodeList);

            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
            populate(respDTO);

        } catch (KnValidationException e) {
            knLogger.error( methodName, "KnValidationException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while generating activation code - ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        } finally {
            knLogger.debug( methodName, "Returning Response - ", respDTO);
            knLogger.info( methodName, "EXIT:");
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO getCorpSubscriberDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        String methodName = "getCorpSubscriberDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, boolean, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        //Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            Integer mode = 0;
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(ipSubscriberInfoDTO.getMdn()), customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_SUBSCRIBER_DETAILS);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
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
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            if(ipSubscriberInfoDTO.getAliasMdn() != null){
                List<String> aliasMdnList = new ArrayList<>();
                aliasMdnList.add(ipSubscriberInfoDTO.getAliasMdn());
                Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHome, readOnly, persisterTxn);
                knLogger.debug(methodName, "aliasMdnList: - ", KnGDPRTemplate.mdnList(aliasMdnList), "aliasMdnMap: ", KnGDPRTemplate.mdnMap(aliasMdnMap));
                if(!aliasMdnMap.isEmpty()){
                    ipSubscriberInfoDTO.setMdn(aliasMdnMap.get(ipSubscriberInfoDTO.getAliasMdn()));
                }
                persistDTO.setAliasMdnList(aliasMdnList);
                persistDTO.setAliasMdnMap(aliasMdnMap);
            } else if(ipSubscriberInfoDTO.getUserId() != null){
                List<String> userIds = new ArrayList<>();
                userIds.add(ipSubscriberInfoDTO.getUserId());
                Map<String, String> userIdMap = contactInfoUtil.getMdnByUsingUserId(userIds, xdmsHome, readOnly, persisterTxn);
                knLogger.debug(methodName, "userIds: - ", userIds, "userIdMap: ", userIdMap);
                if(!userIdMap.isEmpty()){
                    ipSubscriberInfoDTO.setMdn(userIdMap.get(ipSubscriberInfoDTO.getUserId()));
                }
                persistDTO.setUserIdList(userIds);
                persistDTO.setUserIdMap(userIdMap);
            }
            String mdn = ipSubscriberInfoDTO.getMdn();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHome, persisterTxn);
            }
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            if(mdn != null){
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
                persistDTO.setSubsCorpId(subscProfile.getCorpId());
            } else {
                persistDTO.setSubsCorpId(Integer.parseInt(corpId));
            }
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
            respDTO.setMdn(ipSubscriberInfoDTO.getMdn());
            populate(respDTO);
            knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while updating subscriber corpAdminFS Info - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating subscriber corpAdminFS Info - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO.toString());
        return respDTO;
    }


    /**
     * This method deletes the external contact of all corporates.
     *
     * @param persisterTxn
     * @param etagMap      - contains the data structures for sending notifications.   @return etagMap after adding notifications data structures of affected MDN.
     */
    private Map<String, KnOPDirChgDTO> deleteExtContactDataFrmOtherCorp(KnSubsProfileDTO subsProfile, int corpId , KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap, HIERARCHY_TYPE hierarchyType) throws KnException {
        String methodName = "deleteExtContactDataFrmOtherCorp(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>, KnConstants.HIERARCHY_TYPE)";
        String mdn = subsProfile.getMdn();
        String xdmsHomePttId = subsProfile.getXdmsHome();
        LinkedList<String> mdnList = new LinkedList<String>();
        mdnList.add(subsProfile.getMdn());
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
            knLogger.info(methodName, "ContactList - ",KnGDPRTemplate.mdnList(contactMDNs));
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
            HashMap<Integer, String> groupNameMap = new HashMap<>();
            // Delete corp groups with < 2 members
            if (groupListStatus.get(DELETED) != null) {
                Set<Integer> delGroupIdList = groupListStatus.get(DELETED).keySet();
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

                    KnCorpInOutParamDTO inOutParamDTO = new KnCorpInOutParamDTO();
                    if (HIERARCHY_TYPE.HIERARCHY == hierarchyType) {
                        // Delete all the groups from GrpHierarchy tables before deleting from corpGroupInfo table.
                        groupInfoUtil.deleteAllGrpHierarchy(delGroupIdList, xdmsHomePttId, persisterTxn);
                    }
                    groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, inOutParamDTO, persisterTxn);
                    etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                    groupNameMap.putAll(groupListStatus.get(DELETED));
                    etagMap = commonInfoUtil.formTGSCDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                    etagMap = commonInfoUtil.formTGSSDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                }
            }
            if (groupListStatus.get(MODIFIED) != null) {
                Set<Integer> modGroupIdList = groupListStatus.get(MODIFIED).keySet();
                if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                    Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : modGroupIdList) {
                        Collection<String> modMembersList = groupDistList.get(grpId);
                        modifiedMembersMap.put(grpId, modMembersList);
                        //for the contact added to update the directory
                        contactMDNs.addAll(modMembersList);
                    }
                    etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                    groupNameMap.putAll(groupListStatus.get(MODIFIED));
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
        knLogger.debug(methodName, "etagMap ", etagMap);
        etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
        knLogger.debug(methodName, "etagMap after auth mapping modification- ", etagMap);
        //respDTO.setChangeLogMap(etagMap);
        // populate(respDTO);

        knLogger.info(methodName, "EXIT : Returning Response - ", etagMap.size());
        return etagMap;
    }

    /**
     * Below Method will do the validation for convergedClient; E.g below:
     * Step 1: Verify Corp Profile.
     * Step 2: Verify MasterListEtag.
     * Step 3: CONV_CLIENT_ENABLED from RTX Env Configuration.
     * Step 4: Subscriber does not exist.
     * Step 5: Corp ID to the Subscriber association.
     * Step 6: No Change in Client-Type.
     * Step 7: Invalid Client Type.
     *
     * @param ipSubscrFeatureInfoDTO
     * @param persisterTxn
     */
    @Override
    public KnCorpResponseDTO switchConvergedClient(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "switchConvergedClient()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscrFeatureInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = ipSubscrFeatureInfoDTO.getCorpId();
            knLogger.debug(methodName, "Fetch the corpProfile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Corporate Profile - ", corpProfile);
            knLogger.debug(methodName, "Fetch the subscProfile profile if cached or fetch from the DB the details");
            String mdn = ipSubscrFeatureInfoDTO.getSubsDetailsDTO().getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, true, persisterTxn);
            knLogger.debug(methodName, "subscProfile Profile - ", subscProfile);
            if (ipSubscrFeatureInfoDTO.getCorpId() > 0 && subscProfile.getCorpId() != ipSubscrFeatureInfoDTO.getCorpId()) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Subscribers Does not belong to the corporation.");
            }
            String xdmsHomePttId = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
            hookIPDTO.setAction(KnActions.ACTIONS.SWITCH_CONVERGED_CLIENT);
            hookIPDTO.setData(ipSubscrFeatureInfoDTO);
            Map<String, Object> customParams = ipSubscrFeatureInfoDTO.getCustomParamMap();
            if (ipSubscrFeatureInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHomePttId, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHomePttId);
                ipSubscrFeatureInfoDTO.setCustomParamMap(customParams);
                knLogger.debug(methodName, "corpProfile.getCorpMasterListEtag() - ", corpProfile.getCorpMasterListEtag());
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook");
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
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
            //Checking the CONV_CLIENT_ENABLED value from RTX variable.
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            knLogger.debug(methodName, "param value of CONV_CLIENT_ENABLED column", paramNameValueMap);
            String convClientEnabled = paramNameValueMap.get(CONV_CLIENT_ENABLED);
            Integer subsClientType = subscProfile.getClientType();
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            Map<String, KnCorpSubscriberDTO> subsAddlMap = corpSubsProvInfoUtil.getSubscriberAdditionalDetails(mdnList, xdmsHomePttId, persisterTxn);
            String commandPkgCode = null;
            if(subsAddlMap.get(mdn) != null){
                commandPkgCode = subsAddlMap.get(mdn).getCommandPackageCode();
            }
            KnConvergedClientPersistDTO convergedClientPersistDTO = new KnConvergedClientPersistDTO();
            convergedClientPersistDTO.setInputDTO(ipSubscrFeatureInfoDTO);
            convergedClientPersistDTO.setSubsMdn(mdn);
            convergedClientPersistDTO.setConvClientEnabled(Integer.parseInt(convClientEnabled));
            convergedClientPersistDTO.setSubsCorpId(subscProfile.getCorpId());
            convergedClientPersistDTO.setSubsClientType(subsClientType);
            convergedClientPersistDTO.setEnabledPttRadio(ipSubscrFeatureInfoDTO.getSubsDetailsDTO().isEnabledPttRadio());
            convergedClientPersistDTO.setLicenseType(subscProfile.getLicenseType());
            convergedClientPersistDTO.setCommandPackageCode(commandPkgCode);
            if (subsClientType == SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                    || subsClientType == SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                    || subsClientType == SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                convergedClientPersistDTO.setClientConfigEnabled(clientTypeConfigDTO.getIsEnable());
            }
            validatorFW.validate(convergedClientPersistDTO);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while switchConvergedClient Operation - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO updateCorpSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateCorpSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }

            /** Operation:
             *  SubscriberName 	 == null; then no need to call the updateName; 		SubscriberName is empty won't come from request.
             *  SubscriptionType == null; then no need to call the updateSubsType; 	SubscriptionType is empty won't come from request.
             *  SubscriberName   == null; then no need to call the updateEmail; 	SubscriberName is empty then nullify the email in DB.
             */

            String mdn = ipSubscriberInfoDTO.getMdn();
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(ipSubscriberInfoDTO.getMdn(), PUBLIC_PROFILE, false, persisterTxn);
            KnPocSubsAddlInfoDTO subsAddlDetails = commonInfoUtil.getSubsAddlDetails(ipSubscriberInfoDTO.getMdn(), persisterTxn);
            /*String oldPttSettingDocId = subsAddlDetails != null ? subsAddlDetails.getPttSettingDocId() : null;
            String newPttSettingDocId = ipSubscriberInfoDTO.getPttSettingDocId();
            if (!Objects.equals(oldPttSettingDocId, newPttSettingDocId)) {
                knLogger.debug(methodName, "Updating Subscriber PTT setting DocId", newPttSettingDocId);
                pttSettingsUtil.updateSubscriberPttSettingDocId(mdn, newPttSettingDocId, xdmsHome, persisterTxn);
            }*/
            if (subscProfile.getMcId() == null) {
                subscProfile.setMcId(TELURI + mdn);
                subscProfile.setMcpttId(TELURI + mdn);
                subscProfile.setMcVideoId(TELURI + mdn);
                subscProfile.setMcDataId(TELURI + mdn);
                knLogger.debug(methodName, "updating Subscriber MCSIds");
                corpSubsProvInfoUtil.updateSubscriberMCSIds(subscProfile, xdmsHome, persisterTxn);
            }
            String xdmsHomePttId = subscProfile.getXdmsHome();
            String mdnForRequestUserId = corpSubsProvInfoUtil.getUserIdProfile(ipSubscriberInfoDTO, xdmsHome, persisterTxn);
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            if(subsAddlDetails != null){
                persistDTO.setOnBoardingEmailReqd(subsAddlDetails.getOnBoardingEmailReqd());
            }
            persistDTO.setSubsCorpId(subscProfile.getCorpId());
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setDbDispatchType(subscProfile.getDispatchType());
            persistDTO.setDbUserID(subscProfile.getUserId());
            persistDTO.setWebDispatcherEnabled(corpProfile.getWebDispatchEnabled());
            persistDTO.setAllowedClientTypes(String.valueOf(subscProfile.getClientType()));
            persistDTO.setServiceAuthStatus(subscProfile.getServiceAuthStatus());
            if(ipSubscriberInfoDTO.getDispatchType() != null){
                persistDTO.setWebDispatcherFlag(true);
            }
            if(mdnForRequestUserId != null && !mdnForRequestUserId.isEmpty()){
                persistDTO.setUserIdExists(true);
            }
            persistDTO.setServiceAuthStatusAU(ipSubscriberInfoDTO.getServiceAuthStatusAU());
            persistDTO.setLicenseType(subscProfile.getLicenseType());
            persistDTO.setAliasMdn(ipSubscriberInfoDTO.getAliasMdn());
            String mdnForRequestAliasMdn = corpSubsProvInfoUtil.getAliasMdnProfile(ipSubscriberInfoDTO, xdmsHome, persisterTxn);
            KnCorpSubscriberDTO subscriberDTO = contactInfoUtil.selectSubsInfo(ipSubscriberInfoDTO.getAliasMdn(), xdmsHome, persisterTxn);
            KnCorpSubscriberDTO pamSubsDTO = licenseInfoUtil.getPamAccountId(ipSubscriberInfoDTO.getAliasMdn(), xdmsHomePttId, persisterTxn);
            String extMdn = contactInfoUtil.getExtSubsrProfile(ipSubscriberInfoDTO.getAliasMdn(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "mdnForRequestAliasMdn: ", KnGDPRTemplate.mdn(mdnForRequestAliasMdn),
                    "subscriberDTO: ", subscriberDTO, "pamSubsDTO: ", pamSubsDTO, "extMdn: ", KnGDPRTemplate.mdn(extMdn));
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            if(mdnForRequestAliasMdn != null || subscriberDTO != null || pamSubsDTO != null || extMdn != null){
                persistDTO.setAliasMdnExists(true);
            }
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            //verifying if network name is changed
            String newNetworkName = ipSubscriberInfoDTO.getSubscrName();
            String oldNetworkName = subscProfile.getNetworkName();
            int subscriptionType = subscProfile.getCorpSubscriptionType() + subscProfile.getPublicSubscriptionType();
            int newSubscriptionType = ipSubscriberInfoDTO.getSubscriptionType();
            String reqEmail = ipSubscriberInfoDTO.getSubscriberEmail();
            String dbEmail = subscProfile.getSubscriberEmail();
            knLogger.debug(methodName, "existiong subscriptionType - ", subscriptionType);
            knLogger.debug(methodName, "newSubscriptionType - ", newSubscriptionType);
            String name = oldNetworkName;
            int pubSubsType = subscProfile.getPublicSubscriptionType();
            int corpSubsType = subscProfile.getCorpSubscriptionType();
            String email = dbEmail;
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();

            /**
             *  .________________________________________________.
             *  |	  Name/SubscriptionType Operation:			 |
             *  |------------------------------------------------|
             *  |		#DB#    |    #Request#		: #Result#	 |
             *  |------------------------------------------------|
             *  | 1. Value1 	|    Value1	 		: No Change  |
             *  | 2. Value1 	|    Null	 		: No Change	 |
             *  |------------------------------------------------|
             *  | 3. Value1 	|    Value2 		: Change	 |
             *  |________________________________________________|
             *
             */

            //SubscriberName Changes:
            int max_etag_update_notification = genInfoUtil.getXdmMaxNotificationCount();
            boolean nameChange = false;
            Set<String> contactMDNForDirectoryChange = new HashSet<>();
            Set<String> checkForLeftOutMDN = new HashSet<>();
            Collection<Integer> groupIds = new ArrayList<Integer>();
            if ((oldNetworkName != null && newNetworkName != null)) {
                if (!oldNetworkName.trim().equals(newNetworkName.trim())) {
                    name = newNetworkName.trim();
                    nameChange = true;
                    //use the xdms home pttServerId from Corp profile
                    //Subscriber mdns where mdn belongs as a contact
                    Collection<String> subscriberList = contactInfoUtil.getContactMDNs(mdn, xdmsHomePttId, persisterTxn);
                    Collection<String> contactMDNs = contactInfoUtil.getPocSubscribersDetails(subscriberList, Integer.parseInt(corpId), xdmsHomePttId, persisterTxn);
                    if (contactMDNs != null && !contactMDNs.isEmpty()) {
                        List<String> profileMdnsForBasemdn= corpSubsProvInfoUtil
                                  .getProfileMdnByBaseMdns(new ArrayList<>(contactMDNs), xdmsHomePttId, persisterTxn);
                        contactMDNs.addAll(profileMdnsForBasemdn);
                        contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, null, persisterTxn);
                    }
                    KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
                    contactDTO.setMdn(mdn);
                    contactDTO.setCorpId(Integer.parseInt(corpId));
                    contactDTO.setName(newNetworkName);
                    //Retrieving groups where mdn is part of
                    Collection<KnCorpGroupInfoPersistDTO> groupList = groupInfoUtil.getSubsGroupListForXcap(contactDTO, 0,
                            xdmsHomePttId, persisterTxn);
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
                        groupMemberMap.forEach((key, value) -> value.forEach(groupMemberDTO -> {
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
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                    }
                    contactMDNs.add(mdn);

                    if (etagMap.size() <= max_etag_update_notification) {
                        if (contactMDNs != null || !groupIds.isEmpty()) {
                            etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, groupIds, etagMap, xdmsHomePttId, persisterTxn);
                        }
                        KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                        subsc.setMdn(contactDTO.getMdn());
                        subsc.setName(contactDTO.getName());
                        subsc.setUa(KnGeneralUtil.getUA(subscProfile.getUserAgent(), subscProfile.getClientMajorVersion()));
                        Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                        Collection<KnCorpSubscriberDTO> corpSubscriberList = new ArrayList<KnCorpSubscriberDTO>();
                        corpSubscriberList.add(subsc);

                        Collection<String> mdnList = List.of(mdn);
                        etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, mdnList, persisterTxn, etagMap);
                        knLogger.debug(methodName, "Updating Directory Etags", etagMap);

                        for (String contact : contactMDNs) {
                            modifiedContactMap.put(contact, corpSubscriberList);
                        }

                        for (Map.Entry<Integer, Collection<KnCorpGroupMemberDTO>> entry : groupMemberMap.entrySet()) {
                            Collection<KnCorpGroupMemberDTO> groupMember = entry.getValue();
                            for (KnCorpGroupMemberDTO subscriber : groupMember) {
                                subscriber.setName(contactDTO.getName());
                            }
                        }
                        etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, modifiedContactMap, groupMemberMap);

                    } else {
                        if (contactMDNs != null || !groupIds.isEmpty()) {
                            Collection<String> selfMdnList = List.of(mdn);
                            etagMap = contactInfoUtil.updateDistinctSubcribersETags(contactMDNs, selfMdnList, groupIds, etagMap,
                                    true, xdmsHomePttId, persisterTxn);
                        }
                    }
                    contactMDNForDirectoryChange.addAll(contactMDNs);
                    checkForLeftOutMDN.addAll(contactMDNs);
                    contactMDNs.remove(mdn);
                }
            }
            //SubscriptionType Changes:
            boolean subscriptionTypeChange = false;
            if (subscriptionType == SUBSCRIPTION_TYPE_PUBLIC_CORP && newSubscriptionType == SUBSCRIPTION_TYPE_CORPORATE) {
                Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
                boolean removeExtContact = !corpIdExtContactNameMap.isEmpty();
                respDTO.setRemoveExtContact(removeExtContact);
                deleteExtContactDataFrmOtherCorp(subscProfile, Integer.parseInt(corpId), persisterTxn, etagMap, ipSubscriberInfoDTO.getHierarchyType());
            }
            if(subscriptionType != ipSubscriberInfoDTO.getSubscriptionType()) {
                if (ipSubscriberInfoDTO.getSubscriptionType() == SUBSCRIPTION_TYPE_PUBLIC_CORP) {
                    pubSubsType = 1;
                    corpSubsType = SUBSCRIPTION_TYPE_CORPORATE;
                    subscriptionTypeChange = true;
                } else if (ipSubscriberInfoDTO.getSubscriptionType() == SUBSCRIPTION_TYPE_CORPORATE) {
                    pubSubsType = SUBSCRIPTION_TYPE_PUBLIC;
                    corpSubsType = SUBSCRIPTION_TYPE_CORPORATE;
                    subscriptionTypeChange = true;
                }
            }

            /**
             *  .________________________________________________.
             *  |			   Email/AliasMdn/UserId Operation:					 |
             *  |------------------------------------------------|
             *  |		#DB#    |    #Request#		: #Result#	 |
             *  |------------------------------------------------|
             *  | 1. Value1   	|    Null           : No Change	 |
             *  | 2. Null 	    |    Null/Empty	 	: No Change	 |
             *  | 3. Value1 	|    Value1	 		: No Change	 |
             *  |------------------------------------------------|
             *  | 4. Null   	|    Value1 		: Change	 | elseIf
             *  | 5. Value1 	|    Value2 		: Change	 | if
             *  | 6. Value2 	|    Empty 			: Change	 | elseIf
             *  |________________________________________________|
             *
             */


            //EmailChanges:
            boolean emailChange = false;
            if ((dbEmail != null && reqEmail != null)) {
                if (!(dbEmail.trim().equals(reqEmail.trim()))) {
                    emailChange = true;
                    email = reqEmail;
                }
            } else if (!(dbEmail != null) && reqEmail != null) {
                emailChange = true;
                email = reqEmail;
            }

            int dbDispatchType = subscProfile.getDispatchType();
            int dispatchType = dbDispatchType;
            String dbClientPassword = subscProfile.getClientPassowrd();
            boolean dispatchTypeChange = false;

            if (ipSubscriberInfoDTO.getDispatchType() != null && (dbDispatchType != Integer.parseInt(ipSubscriberInfoDTO.getDispatchType()))) {
                dispatchType = Integer.parseInt(ipSubscriberInfoDTO.getDispatchType());
                dispatchTypeChange = true;
            }

            String reqAlias = ipSubscriberInfoDTO.getAliasMdn();
            String dbAlias = subscProfile.getAliasMdn();
            String alias = dbAlias;
            //AliasChanges:
            boolean aliasChange = false;
            if ((dbAlias != null && reqAlias != null)) {
                if (!(dbAlias.trim().equals(reqAlias.trim()))) {
                    aliasChange = true;
                    alias = reqAlias;
                }
            } else if (!(dbAlias != null) && reqAlias != null) {
                aliasChange = true;
                alias = reqAlias;
            }

            if (reqAlias != null && dbAlias != null && !reqAlias.equals(dbAlias)) {
                respDTO.setAliasOverriden(true);
                responseMap.put(OLD_ALIAS_MDN, dbAlias);
            }

            String reqUserId = ipSubscriberInfoDTO.getUserId();
            String dbUserId = subscProfile.getUserId();
            String user = dbUserId;
            //UserChanges:
            boolean userIdChange = false;
            if ((dbUserId != null && reqUserId != null)) {
                if (!(dbUserId.trim().equals(reqUserId.trim()))) {
                    userIdChange = true;
                    user = reqUserId;
                }
            } else if (!(dbUserId != null) && reqUserId != null) {
                userIdChange = true;
                user = reqUserId;
            }

            if (reqUserId != null && dbUserId != null && !reqUserId.equals(dbUserId)) {
                respDTO.setUserOverriden(true);
                responseMap.put(OLD_USER_ID, dbUserId);
            }

            boolean serviceAuthStatusChanged = false;
            int serviceAuthStatus = subscProfile.getServiceAuthStatus();
            int serviceAuthStatusAU = subscProfile.getServiceAuthStatusAU();
            int serviceAuthStatusOP = subscProfile.getServiceAuthStatusOP();
            if(ipSubscriberInfoDTO.getServiceAuthStatusAU() != 0){
                if(ipSubscriberInfoDTO.getServiceAuthStatusAU() == SERVICE_AUTH_STATUS.DEACTIVATED.value()
                        || serviceAuthStatusOP == SERVICE_AUTH_STATUS.DEACTIVATED.value()){
                    serviceAuthStatus = SERVICE_AUTH_STATUS.DEACTIVATED.value();
                } else if(ipSubscriberInfoDTO.getServiceAuthStatusAU() == SERVICE_AUTH_STATUS.ACTIVATED.value()
                        && serviceAuthStatusOP == SERVICE_AUTH_STATUS.ACTIVATED.value()){
                    serviceAuthStatus = SERVICE_AUTH_STATUS.ACTIVATED.value();
                } else {
                    serviceAuthStatus = SERVICE_AUTH_STATUS.PROVISIONED.value();
                }
                serviceAuthStatusAU = ipSubscriberInfoDTO.getServiceAuthStatusAU();
                etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTables(xdmsHomePttId, null, mdn, subscProfile.getCorpId(), persisterTxn, etagMap);
                knLogger.debug(methodName, "-- etagMap", KnGDPRTemplate.mapKeyMdn(etagMap));
                serviceAuthStatusChanged = true;
            }

            knLogger.debug(methodName, "nameChange - ", nameChange, "subscriptionTypeChange - ", subscriptionTypeChange,
                    "emailChange - ", emailChange, "dispatchTypeChange - ", dispatchTypeChange, "userIdChange - ", userIdChange,
                    "serviceAuthStatusChanged -", serviceAuthStatusChanged, "aliasChange - ", aliasChange);
            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            Map<String, String> updateTimeMap = new HashMap<>();
            List<String> profileMdnList=null;
            if(nameChange || subscriptionTypeChange || emailChange || dispatchTypeChange || userIdChange || serviceAuthStatusChanged || aliasChange) {
                respDTO.setProfileChanged(true);
                contactMDNForDirectoryChange.add(mdn);
                checkForLeftOutMDN.add(mdn);
                if ((!contactMDNForDirectoryChange.isEmpty()) || (!groupIds.isEmpty())) {
                    if (etagMap.size() <= max_etag_update_notification) {
                        etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNForDirectoryChange, groupIds, etagMap, xdmsHomePttId, persisterTxn);
                    } else {
                        // updateDistinctSubcribersETags already updated for nameChange
                        if ((!nameChange) || (!contactMDNForDirectoryChange.containsAll(checkForLeftOutMDN))) {
                            etagMap = contactInfoUtil.updateDistinctSubcribersETags(contactMDNForDirectoryChange, List.of(mdn), groupIds, etagMap,
                                    false, xdmsHomePttId, persisterTxn);
                        }
                    }
                }
                ipSubscriberInfoDTO.setMdn(mdn);
                ipSubscriberInfoDTO.setSubscrName(name);
                ipSubscriberInfoDTO.setSubscriberEmail((email != null && !email.isEmpty()) ? email : null);
                ipSubscriberInfoDTO.setSubscriptionType(pubSubsType + corpSubsType);
                ipSubscriberInfoDTO.setLastProfileUpdateTime(System.currentTimeMillis());
                ipSubscriberInfoDTO.setDispatchType(String.valueOf(dispatchType));
                ipSubscriberInfoDTO.setUserId((user != null && !user.isEmpty()) ? user : null);
                ipSubscriberInfoDTO.setClientPassword(userIdChange ? null : dbClientPassword);
                ipSubscriberInfoDTO.setServiceAuthStatus(serviceAuthStatus);
                ipSubscriberInfoDTO.setServiceAuthStatusAU(serviceAuthStatusAU);
                ipSubscriberInfoDTO.setAliasMdn((alias != null && !alias.isEmpty()) ? alias : null);
                corpSubsProvInfoUtil.updateCorpSubscriber(ipSubscriberInfoDTO,null, xdmsHome, persisterTxn);
                profileMdnList = corpSubsProvInfoUtil.getProfileMdnByBaseMdn(mdn,xdmsHome,
						persisterTxn);
                knLogger.debug(methodName, "profileMdnList - ", KnGDPRTemplate.mdnList(profileMdnList));
                if(!profileMdnList.isEmpty())
                {
                corpSubsProvInfoUtil.updateCorpSubscriber(ipSubscriberInfoDTO,profileMdnList, xdmsHome, persisterTxn);
                }

                // Subscriber Config Profile Notification:
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(ipSubscriberInfoDTO.getLastProfileUpdateTime()));
                updateTimeMap.put(mdn, String.valueOf(ipSubscriberInfoDTO.getLastProfileUpdateTime()));
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                chgDocList.add(docChgDTO);
                //populating the XDM Directory DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                dirChgDTO.setPocHome(subscProfile.getPocHome());
                dirChgDTO.setPresenceHome(subscProfile.getPresenceHome());
                dirChgDTO.setDocChgDTO(chgDocList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirPrevEtag() : null);
                dirChgDTO.setDirNewEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirNewEtag() : null);
                dirChgDTO.setProtoVersion(subscProfile.getProtocolVersion());
                dirChgDTOs.add(dirChgDTO);
                if(!profileMdnList.isEmpty())
				{
					List<KnOPDirChgDTO> profileMdnDirChgDTOs = new ArrayList<KnOPDirChgDTO>();
					for (String profileMdn : profileMdnList) {
						// Subscriber Config Profile Notification:
						KnOPDocChgDTO profilemdnDocChgDTO = new KnOPDocChgDTO();
						profilemdnDocChgDTO.setDocumentChgType(
								com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
						String profileMdnSubsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(profileMdn);
						profilemdnDocChgDTO.setDocUri(profileMdnSubsConfigDocUri);
						profilemdnDocChgDTO.setNewEtag(String.valueOf(ipSubscriberInfoDTO.getLastProfileUpdateTime()));
						Collection<KnOPDocChgDTO> profileMdnChgDocList = new ArrayList<KnOPDocChgDTO>();
						profileMdnChgDocList.add(profilemdnDocChgDTO);
						// populating the XDM Directory DTO
						KnOPDirChgDTO profileMdnDirChgDTO = new KnOPDirChgDTO();
						profileMdnDirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(profileMdn, persisterTxn));
						profileMdnDirChgDTO.setPocHome(subscProfile.getPocHome());
						profileMdnDirChgDTO.setPresenceHome(subscProfile.getPresenceHome());
						profileMdnDirChgDTO.setDocChgDTO(profileMdnChgDocList);
						String profileMdnDirDocUri = genInfoUtil.generateDirDocUri(profileMdn);
						profileMdnDirChgDTO.setDirUri(profileMdnDirDocUri);
						profileMdnDirChgDTO
								.setDirPrevEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirPrevEtag() : null);
						profileMdnDirChgDTO
								.setDirNewEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirNewEtag() : null);
						profileMdnDirChgDTO.setProtoVersion(subscProfile.getProtocolVersion());
                        profileMdnDirChgDTO.setMdn(profileMdn);
						profileMdnDirChgDTOs.add(profileMdnDirChgDTO);
					}
					respDTO.setProfileMdnDirChgDTOs(profileMdnDirChgDTOs);
				}

            }
            knLogger.debug(methodName, "dirChgDTOs - ", dirChgDTOs);
            respDTO.setDirChgDTOs(dirChgDTOs);
        	Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = userProfileUtil.profileMdnEtagUpdate(null,
					String.valueOf(corpId), profileMdnList, null, xdmsHome, persisterTxn);
			respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
			if (profileMdnEtagMap != null && profileMdnEtagMap.isEmpty()) {
				respDTO.setMcsXcapRootUriMap(
						genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
			}


            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
            respDTO.setUpdateTimeMap(updateTimeMap);
            respDTO.setChangeLogMap(etagMap);
            respDTO.setMdnCorpId(Integer.parseInt(corpId));
            respDTO.setMdn(mdn);
            respDTO.setActiveFS2(subscProfile.getActiveFS2());
            respDTO.setDispatchType(dispatchType);
            respDTO.setSubsDeactivated(serviceAuthStatus == SERVICE_AUTH_STATUS.DEACTIVATED.value() && serviceAuthStatusAU == SERVICE_AUTH_STATUS.DEACTIVATED.value());
            respDTO.setAliasMdnChanged(aliasChange);
            respDTO.setUserIdChanged(userIdChange);
            respDTO.setPublicSubscriptionType(subscProfile.getPublicSubscriptionType());
            respDTO.setMcId(subscProfile.getMcId());
            respDTO.setMcDataId(subscProfile.getMcDataId());
            respDTO.setMcpttId(subscProfile.getMcpttId());
            respDTO.setMcVideoId(subscProfile.getMcVideoId());
            if(subsAddlDetails != null) respDTO.setOnBoardingMailReqd(subsAddlDetails.getOnBoardingEmailReqd());
            populate(respDTO);
            responseMap.put(USER_ID, user != null && !user.isEmpty() ? user : null);
            responseMap.put(USER_TYPE, OTHER);
            responseMap.put(CORP_ID, corpProfile.getExtCorpId().trim());
            if(userIdChange) responseMap.put(MDN_AS_USER_ID, mdn);
            if(aliasChange) responseMap.put(MDN_AS_USER_ID, reqAlias != null && !reqAlias.isEmpty() ? reqAlias : mdn);
            responseMap.put(MDN_IDM, mdn);
            responseMap.put(MCPTT_ID_OIDC, subscProfile.getMdn());
            responseMap.put(MCVIDEO_ID_OIDC, subscProfile.getMcVideoId());
            responseMap.put(MCDATA_ID_OIDC, subscProfile.getMcDataId());
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in updateCorpSubscriber  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in updateCorpSubscriber ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        respDTO.setResponseMap(responseMap);
        return respDTO;
    }

    /**
     * Below Method will do the validation for getCorpSubsUserProfile; E.g below:
     * Step 1: Verify Corp Profile.
     * Step 2: Verify MasterListEtag.
     * Step 3: WEB_DISPATCH_ENABLED corpLevel or systemLevel.
     * Step 4: UserId does not exist.
     * Step 5: Corp ID to the UserId association.
     *
     * @param ipSubscriberInfoDTO
     * @param persisterTxn
     */
    @Override
    public KnCorpResponseDTO getCorpSubsUserProfile(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getCorpSubsUserProfile(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            if(ipSubscriberInfoDTO.getAliasMdn() != null){
                List<String> aliasMdnList = new ArrayList<>();
                aliasMdnList.add(ipSubscriberInfoDTO.getAliasMdn());
                Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHome, true, persisterTxn);
                knLogger.debug(methodName, "aliasMdnList: - ", KnGDPRTemplate.mdnList(aliasMdnList), "aliasMdnMap: ", KnGDPRTemplate.mdnMap(aliasMdnMap));
                if(!aliasMdnMap.isEmpty()){
                    ipSubscriberInfoDTO.setMdn(aliasMdnMap.get(ipSubscriberInfoDTO.getAliasMdn()));
                }
                persistDTO.setAliasMdnList(aliasMdnList);
                persistDTO.setAliasMdnMap(aliasMdnMap);
            } else if(ipSubscriberInfoDTO.getUserId() != null){
                List<String> userIds = new ArrayList<>();
                userIds.add(ipSubscriberInfoDTO.getUserId());
                Map<String, String> userIdMap = contactInfoUtil.getMdnByUsingUserId(userIds, xdmsHome, true, persisterTxn);
                knLogger.debug(methodName, "userIds: - ", userIds, "userIdMap: ", userIdMap);
                if(!userIdMap.isEmpty()){
                    ipSubscriberInfoDTO.setMdn(userIdMap.get(ipSubscriberInfoDTO.getUserId()));
                }
                persistDTO.setUserIdList(userIds);
                persistDTO.setUserIdMap(userIdMap);
            }
            String mdn = ipSubscriberInfoDTO.getMdn();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHome, persisterTxn);
            }
            KnSubsProfileDTO subscProfile = null;
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            if(mdn != null){
                subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
                persistDTO.setAllowedClientTypes(String.valueOf(subscProfile.getClientType()));
                if(subscProfile.getClientType() == DISPATCH_CLIENT) persistDTO.setWebDispatcherFlag(true);
                persistDTO.setSubsCorpId(subscProfile.getCorpId());
                persistDTO.setLicenseType(subscProfile.getLicenseType());
            } else {
                persistDTO.setSubsCorpId(Integer.parseInt(corpId));
            }
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setWebDispatcherEnabled(corpProfile.getWebDispatchEnabled());
            persistDTO.setAliasMdn(ipSubscriberInfoDTO.getAliasMdn());
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getCorpSubsUserProfile  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getCorpSubsUserProfile ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    /**
     * Below Method will do the validation for resetCorpSubsUserPassword; E.g below:
     * Step 1: Verify Corp Profile.
     * Step 2: Verify MasterListEtag.
     * Step 3: WEB_DISPATCH_ENABLED corpLevel or systemLevel.
     * Step 4: UserId does not exist.
     * Step 5: Corp ID to the UserId association.
     * Step 6: Subscriber should not be de-activated.
     * @param ipSubscriberInfoDTO
     * @param persisterTxn
     */
    @Override
    public KnCorpResponseDTO resetCorpSubsUserPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "resetCorpSubsUserPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnCorpSubscriberDTO mdnProfile = corpSubsProvInfoUtil.getUserProfile(ipSubscriberInfoDTO, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "Fetch the subscriber profile mdnProfile", mdnProfile);
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdnProfile.getMdn()), customParams, xdmsHome, persisterTxn);
            }
            KnPocSubsAddlInfoDTO subsAddlDetails = commonInfoUtil.getSubsAddlDetails(mdnProfile.getMdn(), persisterTxn);
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            persistDTO.setSubsCorpId(mdnProfile.getCorpId());
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setWebDispatcherEnabled(corpProfile.getWebDispatchEnabled());
            persistDTO.setServiceAuthStatus(mdnProfile.getServiceAuthStatus());
            persistDTO.setAllowedClientTypes(String.valueOf(mdnProfile.getClientType()));
            persistDTO.setWebDispatcherFlag(true);
            if(subsAddlDetails != null){
                persistDTO.setOnBoardingEmailReqd(subsAddlDetails.getOnBoardingEmailReqd());
            }
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in resetCorpSubsUserPassword  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in resetCorpSubsUserPassword ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    /**
     * Below Method will do the validation for resendCorpSubsVerificationEmail; E.g below:
     * Step 1: Verify Corp Profile.
     * Step 2: Verify MasterListEtag.
     * Step 3: WEB_DISPATCH_ENABLED corpLevel or systemLevel.
     * Step 4: UserId does not exist.
     * Step 5: Corp ID to the UserId association.
     * Step 6: Subscriber should not be de-activated.
     * @param ipSubscriberInfoDTO
     * @param persisterTxn
     */
    @Override
    public KnCorpResponseDTO resendCorpSubsVerificationEmail(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "resetCorpSubsUserPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnCorpSubscriberDTO mdnProfile = corpSubsProvInfoUtil.getUserProfile(ipSubscriberInfoDTO, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "Fetch the subscriber profile mdnProfile", mdnProfile);
            KnPocSubsAddlInfoDTO subsAddlDetails = commonInfoUtil.getSubsAddlDetails(mdnProfile.getMdn(), persisterTxn);
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            persistDTO.setSubsCorpId(mdnProfile.getCorpId());
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setWebDispatcherEnabled(corpProfile.getWebDispatchEnabled());
            persistDTO.setServiceAuthStatus(mdnProfile.getServiceAuthStatus());
            persistDTO.setAllowedClientTypes(String.valueOf(mdnProfile.getClientType()));
            persistDTO.setWebDispatcherFlag(true);
            persistDTO.setMcpttCompliance(mdnProfile.getMcpttCompliance());
            if(subsAddlDetails != null){
                persistDTO.setOnBoardingEmailReqd(subsAddlDetails.getOnBoardingEmailReqd());
            }
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            respDTO.setMcpttCompliance(mdnProfile.getMcpttCompliance());
            respDTO.setMdn(mdnProfile.getMdn());
            respDTO.setMcId(mdnProfile.getMcId());
            respDTO.setMcDataId(mdnProfile.getMcDataId());
            respDTO.setMcVideoId(mdnProfile.getMcVideoId());
            respDTO.setMcpttId(mdnProfile.getMcpttId());
            respDTO.setAliasMdn(mdnProfile.getAliasMdn());
            respDTO.setNetworkName(mdnProfile.getName());

            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in resetCorpSubsUserPassword  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in resetCorpSubsUserPassword ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    /**
     * MCPTT feature permission Users:
     *
     * Validation:
     * - Verify if the Authorized MDNs, Target MDNs exists and are associated with the same Corporate
     * - Verify if the Authorized MDN or Target MDNs does not exist as External Contacts to the Corporate
     * - Verify the Feature control capabilities as defined in MCPTT Feature Info xls(MCPTT_FeatureInfo.xlsx)
     * - Verify if the Authorized MDNs and/or Target MDNs are not of client types SU_MDN(7), SG_MDN(8), SG_MDN_PATCH(17).
     * - Verify if the Authorized MDN and Target MDN exists as contact in each other's contact list
     *
     * Business: XDM Data Mgr shall perform the below as part of API processing:
     * - Update the received request info into the table DG.MCPTT_PERM_INFO I.e, Create/Update the entry in
     * DG.MCPTT_PERM_INFO. OR Delete the entry from DG.MCPTT_PERM_INFO if all the permissions are set to disabled.
     * - If Discreet Listening permission is disabled then set DG.MCPTT_PERM_INFO:DISCREET_ENABLED as false to
     * the respective entries of that Authorized MDN, Target MDN set.
     * - If Ambient Listening permission is set to enabled/disabled then update the corresponding Target MDN' s Corp
     * resource list doc etag, xdm directory etag and notify the Target MDN's.
     * - Create/Update entry in the DG.AUTHORIZATION_DOC for Authorized MDNs only and notify the client (Authorized MDNs)
     * - If the MDN does not have any of the MCPTT Permissions on any of the Target (i.e, there is no entry for the
     * authorized MDN in the DG.MCPTT_PERM_INFO then remove the entry from DG.AUTHORIZATION_DOC and notify the Client
     * (as xdm directory etag change and authorization doc removed)
     *
     * @param ipAuthUserPermissionInfoDTO
     * @param persisterTxn
     */
    @Override
    public KnCorpResponseDTO setTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
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
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipAuthUserPermissionInfoDTO.getCustomParamMap();
            if (ipAuthUserPermissionInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(authorizedMdn), customParams, xdmsHome, persisterTxn);
                //TODO check for all TU from same fan/ban as incontext
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipAuthUserPermissionInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SET_AUTH_USER_PERMISSIONS);
                hookIPDTO.setData(ipAuthUserPermissionInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = new KnCorpMcpttFeaturePersistDTO();
            Collection<String> targetMdnList = new ArrayList<>();
            Collection<String> authMdn = new ArrayList<>();
            authMdn.add(authorizedMdn);
            Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList();
            targetMdnList.addAll(targetMdnPermBitInfos.stream().map(KnTargetMdnPermBitInfo::getMdn).collect(Collectors.toList()));
            knLogger.debug(methodName, "targetMdnList - ", KnGDPRTemplate.mdnList(targetMdnList));
            Map<String, KnCorpSubscriberDTO> contDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(targetMdnList, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "contDetailsMap - ", KnGDPRTemplate.mapKeyMdn(contDetailsMap));
            Map<String, Collection<String>> authorizedMdnContactList = contactInfoUtil.getSubscribersContactList(authMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "authorizedMdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(authorizedMdnContactList));
            Collection<String> targetNotInContactListOfAuthMdn = null;
            if(authorizedMdnContactList != null && !authorizedMdnContactList.isEmpty()){
                Collection<String> authMdnContactList = authorizedMdnContactList.get(authorizedMdn);
                targetNotInContactListOfAuthMdn = targetMdnList.stream().filter(target -> !authMdnContactList.contains(target)).collect(Collectors.toList());
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

            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", authUserListPersistDTO);
            validatorFW.validate(authUserListPersistDTO);
            knLogger.info(methodName, "Validation completed Successfully.");
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(authorizedMdn);
            Map<String, Long> authEtagMap = corpSubsProvInfoUtil.seleteFromAuthDoc(mdnList, xdmsHome, persisterTxn);
            // Business Logics:
            Collection<KnMcpttPermissionDTO> insertToMcpttInfo = new ArrayList<>();
            Collection<KnMcpttPermissionDTO> updateToMcpttInfo = new ArrayList<>();
            Collection<String> targetMdnForDelete = new ArrayList<>();
            Map<String, Integer> targetForResourceListUpdate = new HashMap<>();

            Collection<KnMcpttPermissionDTO> insertToMcpttInfoForProfileMdns = new ArrayList<>();
            Collection<KnMcpttPermissionDTO> updateToMcpttInfoForProfileMdns = new ArrayList<>();
            Collection<String> targetMdnForDeleteForProfileMdns = new ArrayList<>();

            List<String> profileMdnList =new ArrayList<>();
            profileMdnList = ipAuthUserPermissionInfoDTO.getTargetProfileMdnPermissionBitInfoList().stream().map(KnTargetMdnPermBitInfo::getMdn).collect(Collectors.toList());

            List<String> baseTuList = new ArrayList<>(targetMdnList);
            Map<String, List<String>> targetBaseMdnsProfileMdnsMapping = userProfileUtil.getProfileMdnListByBaseMdnsList(baseTuList, xdmsHome, false, persisterTxn);


            Map<String, KnMcpttPermissionDTO> mcpttPermissionMap = corpSubsProvInfoUtil.getAuthUserPermissions(authorizedMdn, xdmsHome, persisterTxn);
            List<String> mdns = new ArrayList<>();
            knLogger.debug(methodName, "mcpttPermissionMap - ", mcpttPermissionMap);
            //adding profile mdns skipping au and tu contact validation.
            Collection<KnTargetMdnPermBitInfo> targetProfileMdnPermissionBitInfoList
                    = ipAuthUserPermissionInfoDTO.getTargetProfileMdnPermissionBitInfoList();
            knLogger.debug("targetProfileMdnPermissionBitInfoList :",targetProfileMdnPermissionBitInfoList);
            if(targetProfileMdnPermissionBitInfoList!=null&&!targetProfileMdnPermissionBitInfoList.isEmpty()){
                targetMdnPermBitInfos.addAll(targetProfileMdnPermissionBitInfoList);


                targetMdnList.addAll(profileMdnList);
                knLogger.debug(methodName,"profileMdnList :-",KnGDPRTemplate.mdnList(profileMdnList));
                Map<String, KnCorpSubscriberDTO> subsDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(profileMdnList, xdmsHome, persisterTxn);
                contDetailsMap.putAll(subsDetailsMap);
            }
            Collection<String> insertTargetMdnList = new ArrayList<>(targetMdnList);
            knLogger.debug(methodName,"insertTargetMdnList :-",KnGDPRTemplate.mdnList(insertTargetMdnList));
            // Update
       	    Map<String, Integer> mapListForPrivacy = new HashMap<>();
            knLogger.debug(methodName, "---> corpProfile.getPrivacyAmbDiscListenFlag() - ", corpProfile.getPrivacyAmbDiscListenFlag());
            //update and delete
            List<String> privacyEnabledMdns=new ArrayList<>();

            //fetching only the list of common contact mdns
            List<String> allCommonContactMdns = sublistInfoUtil.getCommonContactListForMdns(authorizedMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName,"allCommonContactMdns :-",KnGDPRTemplate.mdnList(allCommonContactMdns));
            //fetching all the contacts of the subscriber duplicate also
            List<String> listOfAllContactMdns = sublistInfoUtil.getAllSublistContactMdns(authorizedMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName,"listOfAllContactMdns :-",KnGDPRTemplate.mdnList(listOfAllContactMdns));


            List<String> commonContactMdns = new ArrayList<>();
            for(KnTargetMdnPermBitInfo obj : targetMdnPermBitInfos){
                if(!allCommonContactMdns.isEmpty() && allCommonContactMdns.contains(obj.getMdn())){
                            commonContactMdns.add(obj.getMdn());
                }
            }
            knLogger.debug(methodName,"common contacts present in the request :-",KnGDPRTemplate.mdnList(commonContactMdns));
            Map<String,Integer> commonContactsAndCommonAUMapping = new HashMap<>();

            ////MINT-14813 - ConcurrentModificationException observed on authMdnsProfileMdns variable hence changed to CopyOnWriteArrayList
            List<String> authMdnProfileMdnList = corpSubsProvInfoUtil.getProfileMdnByBaseMdn(authorizedMdn, xdmsHome,
                    persisterTxn);
            CopyOnWriteArrayList<String> authMdnsProfileMdns = new CopyOnWriteArrayList<>(authMdnProfileMdnList);
            for(KnTargetMdnPermBitInfo target : targetMdnPermBitInfos){
                if (commonContactMdns.contains(target.getMdn()) && mcpttPermissionMap.get(target.getMdn())!=null) {
                    Integer reqCommonAU = target.getCommonContact();
                    Integer dbCommonAU = mcpttPermissionMap.get(target.getMdn()).getCommonAu();
                    String targetMdn = target.getMdn();
                    if((DISABLE.equals(reqCommonAU)&&ENABLE.equals(dbCommonAU) &&Collections.frequency(listOfAllContactMdns, target.getMdn())>1)){
                        commonContactsAndCommonAUMapping.put(target.getMdn(), DISABLE);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), DISABLE);
                    }else if((ENABLE.equals(reqCommonAU)&&DISABLE.equals(dbCommonAU))&&commonContactMdns.contains(targetMdn)) {
                        commonContactsAndCommonAUMapping.put(target.getMdn(), ENABLE);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), ENABLE);
                    }else if((ENABLE.equals(reqCommonAU)&&ENABLE.equals(dbCommonAU)) || (reqCommonAU==null &&ENABLE.equals(dbCommonAU))){
                        //null
                        commonContactsAndCommonAUMapping.put(target.getMdn(), null);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), null);
                    }
                    knLogger.debug(methodName,"after the mapping population ",commonContactsAndCommonAUMapping);
                }
            }
            //update scenario for the upm call
            //fetching the auth permissinos for the aus profile mdns
            List<String> targetMdns = new ArrayList<>(mcpttPermissionMap.keySet());
            Map<String, List<KnMcpttPermissionDTO>> mcpttPermissionAsProfileMdns = corpSubsProvInfoUtil.getTargetMdnListPermissions(authMdnsProfileMdns, xdmsHome, persisterTxn);

            //aus profile
            //if that profile mdn is having permissions
            if(!commonContactMdns.isEmpty()){
                for (String profileMdn : authMdnsProfileMdns) {
                    if(mcpttPermissionAsProfileMdns.get(profileMdn) != null) {
                        for (KnMcpttPermissionDTO perm : mcpttPermissionAsProfileMdns.get(profileMdn)) {
                            if (perm.getCommonAu() == 0) {
                                authMdnsProfileMdns.remove(profileMdn);
                            }
                        }
                    }
                }
            }


            if (mcpttPermissionMap != null && !mcpttPermissionMap.isEmpty()) {
                targetMdnPermBitInfos.stream().filter(target -> mcpttPermissionMap.get(target.getMdn()) != null && mcpttPermissionMap.keySet().contains(target.getMdn())).forEach(target -> {
                    KnMcpttPermissionDTO dbTargetInfo = mcpttPermissionMap.get(target.getMdn()); // this is the data from the DB
                    mdns.add(target.getMdn());
                    //logic for insertion case of tus
                    Integer reqCommonAU = target.getCommonContact();
                    Integer dbCommonAU = mcpttPermissionMap.get(target.getMdn()).getCommonAu();

                    BitSet targetDBPermsBit = featureSetUtil.convertLongToBitSet(dbTargetInfo.getMcpttPerms());
                    int existingAmbientBit = targetDBPermsBit.get(MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0;
                    if (target.getAmbientListening() != existingAmbientBit) {
                        targetForResourceListUpdate.put(target.getMdn(), target.getAmbientListening());
                    }
                    corpSubsProvInfoUtil.integerToBitsConversion(targetDBPermsBit, target);
                    if (target.getDiscreteListening() == 0) {
                        dbTargetInfo.setDiscreteEnabled(DISABLED);
                    } else {
                        dbTargetInfo.setDiscreteEnabled(dbTargetInfo.getDiscreteEnabled());
                    }

                    if(commonContactsAndCommonAUMapping.containsKey(target.getMdn())){
                        if(commonContactsAndCommonAUMapping.get(target.getMdn()) == null) {
                            dbTargetInfo.setCommonAu(dbCommonAU);
                        }else{
                            dbTargetInfo.setCommonAu(commonContactsAndCommonAUMapping.get(target.getMdn()));
                        }
                    }
                    if(isUpmCall && ENABLE.equals(dbCommonAU) && reqCommonAU == null){

                        dbTargetInfo.setCommonAu(null);
                    }

                    long targetBit = featureSetUtil.convertBitSetToLong(targetDBPermsBit);
                    if (targetBit != 0) {
                        if(dbTargetInfo.getMcpttPerms() != targetBit || (!Objects.equals(reqCommonAU, dbCommonAU))){
                            dbTargetInfo.setMcpttPerms(targetBit);
                            dbTargetInfo.setServiceAuthUserAU(contDetailsMap.get(target.getMdn()).getServiceAuthStatusAU());
                            updateToMcpttInfo.add(dbTargetInfo);
                            if(commonContactsAndCommonAUMapping.containsKey(target.getMdn()) && dbTargetInfo.getCommonAu()==1 ){
                                updateToMcpttInfoForProfileMdns.add(dbTargetInfo);
                            }
                        }
                    } else {
                        if(!(isUpmCall && ENABLE.equals(dbCommonAU) && reqCommonAU == null)){
                            targetMdnForDelete.add(target.getMdn());
                            if (commonContactsAndCommonAUMapping.containsKey(target.getMdn())) {
                                targetMdnForDeleteForProfileMdns.add(target.getMdn());
                            }
                        }
                    }
                    if(DISABLE.equals(commonContactsAndCommonAUMapping.get(target.getMdn()))){
                        targetMdnForDeleteForProfileMdns.add(target.getMdn());
                    }
                    if(ENABLE.equals(commonContactsAndCommonAUMapping.get(target.getMdn()))){
                        insertToMcpttInfoForProfileMdns.add(dbTargetInfo);
                    }

                    insertTargetMdnList.remove(target.getMdn());
                    if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1 && contDetailsMap.get(target.getMdn()) != null && contDetailsMap.get(target.getMdn()).getClientPVmajorVer() >= PROTOCOL_VERSION_16) {
                    knLogger.debug(methodName, "--->target.getAmbientListening() - ", target.getAmbientListening());
                    knLogger.debug(methodName, "--->target.getDiscreteListening() - ", target.getDiscreteListening());
                    mapListForPrivacy.put(target.getMdn(), (target.getAmbientListening() == ENABLED
                            || target.getDiscreteListening() == ENABLED) ? DISABLED : ENABLED);
                    if(mapListForPrivacy.get(target.getMdn()) == DISABLED)
                    {
                        knLogger.debug(methodName, "--->privacyFlag - ", mapListForPrivacy.get(target.getMdn()) ,"target.getMdn()-" + KnGDPRTemplate.mdn(target.getMdn()));
                        privacyEnabledMdns.add(target.getMdn());
                    }
                }
                });
                knLogger.debug(methodName, "updateToMcpttInfo - ", updateToMcpttInfo); // update to MCPTT.
                knLogger.debug(methodName, "targetMdnForDelete - ", KnGDPRTemplate.mdnList(targetMdnForDelete)); // delete from MCPTT and Authorization.
                knLogger.debug(methodName, "targetForResourceListUpdate - ", targetForResourceListUpdate); // update to CorpResourceList

                knLogger.debug(methodName, "updateToMcpttInfoForProfileMdns - ", updateToMcpttInfoForProfileMdns); // update to MCPTT.
                knLogger.debug(methodName, "deleteFromMcpttPermInfoForProfileMdns - ", KnGDPRTemplate.mdnList(targetMdnForDeleteForProfileMdns));
                if (!updateToMcpttInfo.isEmpty()) {
                    corpSubsProvInfoUtil.updateToMcpttPermInfo(updateToMcpttInfo, xdmsHome, persisterTxn);
                }
                if(!updateToMcpttInfoForProfileMdns.isEmpty()){
                    corpSubsProvInfoUtil.updateToMcpttPermInfoForProfileMdns(updateToMcpttInfoForProfileMdns,authMdnsProfileMdns, xdmsHome, persisterTxn);
                }
                if (!targetMdnForDelete.isEmpty()) {
                    corpSubsProvInfoUtil.deleteFromMcpttPermInfo(authorizedMdn, targetMdnForDelete, xdmsHome, persisterTxn);
                }
                if(!targetMdnForDeleteForProfileMdns.isEmpty()){
                    corpSubsProvInfoUtil.deleteFromMcpttPermInfoForProfileMdns(authMdnsProfileMdns, targetMdnForDeleteForProfileMdns, xdmsHome, persisterTxn);
                }
            }
            // Insert
            commonContactsAndCommonAUMapping.clear();
            for(KnTargetMdnPermBitInfo insertTarget : targetMdnPermBitInfos){
                if((ENABLE.equals(insertTarget.getCommonContact())&&
                    commonContactMdns.contains(insertTarget.getMdn())) ||
                        (commonContactMdns.contains(insertTarget.getMdn()) &&
                                (Collections.frequency(listOfAllContactMdns, insertTarget.getMdn()))==1)){
                commonContactsAndCommonAUMapping.put(insertTarget.getMdn(),1);
                commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping,targetBaseMdnsProfileMdnsMapping.get(insertTarget.getMdn()),1);
            }
            }
            knLogger.debug(methodName, "after the population ", commonContactsAndCommonAUMapping);
            knLogger.debug(methodName,"targetMdnPermBitInfos :",targetMdnPermBitInfos," insertTargetMdnList :",KnGDPRTemplate.mdnList(insertTargetMdnList));
            targetMdnPermBitInfos.stream().filter(insertTarget -> insertTargetMdnList.contains(insertTarget.getMdn())).forEach(insertTarget -> {
                KnMcpttPermissionDTO insertPermissionDTO = new KnMcpttPermissionDTO();
                insertPermissionDTO.setCorpid(subscProfile.getCorpId()); //setting the authorized mdns corpid //sharing of roles
                insertPermissionDTO.setCorpid(subscProfile.getCorpId()); //setting the authorized mdns corpid //sharig of roles
                insertPermissionDTO.setAuthMdn(authorizedMdn);
                insertPermissionDTO.setTargetMdn(insertTarget.getMdn());
                //write the default value for the column got introduced newly

                if(commonContactsAndCommonAUMapping.containsKey(insertTarget.getMdn())){
                    insertPermissionDTO.setCommonAu(commonContactsAndCommonAUMapping.get(insertTarget.getMdn()));
                }
                mdns.add(insertTarget.getMdn());
                BitSet targetInsertBitSet = new BitSet();
                corpSubsProvInfoUtil.integerToBitsConversion(targetInsertBitSet, insertTarget);
                if(insertTarget.getDiscreteListening() == 0){
                    insertPermissionDTO.setDiscreteEnabled(DISABLED);
                }
                long permBit = featureSetUtil.convertBitSetToLong(targetInsertBitSet);
                if(permBit != 0){
                    targetForResourceListUpdate.put(insertTarget.getMdn(), targetInsertBitSet.get(MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
                    insertPermissionDTO.setMcpttPerms(permBit);
                    KnCorpSubscriberDTO subscriberDTO=contDetailsMap.get(insertTarget.getMdn());
                    if(subscriberDTO!=null) {
                        insertPermissionDTO.setServiceAuthUserAU(subscriberDTO.getServiceAuthStatusAU());
                        insertToMcpttInfo.add(insertPermissionDTO);
                        if(commonContactsAndCommonAUMapping.containsKey(insertTarget.getMdn())){
                            insertToMcpttInfoForProfileMdns.add(insertPermissionDTO);
                        }
                    }else {
                        knLogger.info(methodName,"subscriber info not found for mdn -",KnGDPRTemplate.mdn(insertTarget.getMdn()));
                    }
                }
                if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1 && contDetailsMap.get(insertTarget.getMdn()) != null && contDetailsMap.get(insertTarget.getMdn()).getClientPVmajorVer() >= PROTOCOL_VERSION_16) {
                knLogger.debug(methodName, "--->insertTarget.getAmbientListening() - ", insertTarget.getAmbientListening());
                knLogger.debug(methodName, "--->insertTarget.getDiscreteListening() - ", insertTarget.getDiscreteListening());
                mapListForPrivacy.put(insertTarget.getMdn(), (insertTarget.getAmbientListening() == ENABLED
                        || insertTarget.getDiscreteListening() == ENABLED) ? DISABLED : ENABLED);
                if(mapListForPrivacy.get(insertTarget.getMdn())== DISABLED)
                {
                    knLogger.debug(methodName, "--->privacyFlag - ", mapListForPrivacy.get(insertTarget.getMdn()) ,"insertTarget.getMdn()-" + KnGDPRTemplate.mdn(insertTarget.getMdn()));
                    privacyEnabledMdns.add(insertTarget.getMdn());
                }
            }
            });
            knLogger.debug(methodName, "insertToMcpttInfo - ", insertToMcpttInfo); // insert to CorpResourceList
            knLogger.debug(methodName, "insertToMcpttInfoForProfileMdns - ", insertToMcpttInfoForProfileMdns);
            if(!insertToMcpttInfo.isEmpty()) {
                corpSubsProvInfoUtil.insertIntoMcpttPermInfo(insertToMcpttInfo, xdmsHome, persisterTxn);
            }
            if(!insertToMcpttInfoForProfileMdns.isEmpty()){
                corpSubsProvInfoUtil.insertIntoMcpttPermInfoForProfileMdns(insertToMcpttInfoForProfileMdns,authMdnsProfileMdns, xdmsHome, persisterTxn);
            }

            Map<String, Collection<KnMcpttPermissionDTO>> insertTargetMdnListMap = new HashMap<>();
            Map<String, Collection<KnMcpttPermissionDTO>> updateToMcpttInfoMap = new HashMap<>();
            if(isUpmCall && !targetMdnForDelete.isEmpty()){
                Set<String> emergencyBaseMdn = corpSubsProvInfoUtil.getBaseMdnByProfileMdns(new ArrayList<>(authMdn), xdmsHome, persisterTxn);
                Map<String, KnMcpttPermissionDTO> mcpttPermissions = corpSubsProvInfoUtil.getAuthUserPermissions(emergencyBaseMdn.iterator().next(), xdmsHome, persisterTxn);
                List<KnMcpttPermissionDTO> permissions = new ArrayList<>(mcpttPermissions.values());
                if(!permissions.isEmpty()) {
                    Predicate<KnMcpttPermissionDTO> removeNonCommonMdns = perm -> !(perm.getCommonAu() != null && perm.getCommonAu() == 1);
                    permissions.removeIf(removeNonCommonMdns);
                    Predicate<KnMcpttPermissionDTO> removeMdnsExeptDeletedTu = perm -> !(targetMdnForDelete.contains(perm.getTargetMdn()));
                    permissions.removeIf(removeMdnsExeptDeletedTu);
                    List<String> profileMdn = new ArrayList<>();
                    profileMdn.add(authorizedMdn);
                    corpSubsProvInfoUtil.insertIntoMcpttPermInfoForProfileMdns(permissions, profileMdn, xdmsHome, persisterTxn);
                    //insertTargetMdnListMap.put(authorizedMdn,permissions);
                    if(!permissions.isEmpty()){
                        updateToMcpttInfoMap.put(authorizedMdn,permissions);
                        permissions.forEach(perm -> targetMdnForDelete.remove(perm.getTargetMdn()));
                    }

                }
                    //for notifiy
            }

            Map<String, KnOPDirChgDTO> etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTables(xdmsHome, authorizedMdn, null,
                    subscProfile.getCorpId(), persisterTxn, null);
            if(!insertToMcpttInfoForProfileMdns.isEmpty()||!targetMdnForDeleteForProfileMdns.isEmpty()||!updateToMcpttInfoForProfileMdns.isEmpty()){
                Map<String, KnOPDirChgDTO> etagMapForProfileMdn = corpSubsProvInfoUtil.updateAuthorizationImpactedTablesForProfileMdns(xdmsHome, authMdnsProfileMdns, null,
                        subscProfile.getCorpId(), persisterTxn, null);
                etagMap.putAll(etagMapForProfileMdn);
            }
       	    knLogger.info(methodName, "mapListForPrivacy:: ", mapListForPrivacy);
            if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1) {
                if(isAllAuTask){
                    //for KnAssignTargetPermsToAllAUTask case when tu profile mdn is set to au
                    //PRIVACY_OPT_STATUS should be copied from base mdn.to keep the flag sync for both.
                    //Find the Real Mdns for the profile Mdns
                    ArrayList<String> permProfileMdnList = new ArrayList<>(mapListForPrivacy.keySet());
                    List<String> realMdns = groupInfoUtil.getRealMdns(permProfileMdnList, xdmsHome, persisterTxn);
                    //getting map of profile to base mdn.
                    //Map<String, List<String>> profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns, xdmsHome, persisterTxn);
                    Map<String, Integer> mdnPrivOptsMap = corpSubsProvInfoUtil.getPrivacyOptStatus(realMdns, xdmsHome, persisterTxn);
                    //As only one profile mdn will come in the request in this flow,so get by index and override the opts value for profile mdn.
                    Integer mdnOpts = mdnPrivOptsMap.get(realMdns.get(0));
                    knLogger.debug(methodName," Base mdn mdnOpts :",mdnOpts);
                    mapListForPrivacy.put(permProfileMdnList.get(0),mdnOpts);
                }
        	corpSubsProvInfoUtil.updateToPrivacyOptStatus(mapListForPrivacy, xdmsHome, persisterTxn);
            }
            mdns.add(authorizedMdn);
            Map<String, KnCorpSubsEntitiesDTO> corpSubsEntitiesDTOMap = contactInfoUtil.getSubsEntitiesDetails(mdns, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "etagMap 111 - ", KnGDPRTemplate.mapKeyMdn(etagMap));

            updateToMcpttInfoMap.put(authorizedMdn, updateToMcpttInfo);

            insertTargetMdnListMap.put(authorizedMdn, insertToMcpttInfo);
            Map<String, Collection<String>> targetMdnForDeleteMap = new HashMap<>();
            targetMdnForDeleteMap.put(authorizedMdn, targetMdnForDelete);
            //notifications for the profile mdns
            if(!insertToMcpttInfoForProfileMdns.isEmpty()){
                authMdnsProfileMdns.forEach(profileMdn -> insertTargetMdnListMap.put(profileMdn,insertToMcpttInfoForProfileMdns));
            }
            if(!updateToMcpttInfoForProfileMdns.isEmpty()){
                authMdnsProfileMdns.forEach(profileMdn -> updateToMcpttInfoMap.put(profileMdn,updateToMcpttInfoForProfileMdns));
            }
            if(!targetMdnForDeleteForProfileMdns.isEmpty()){
                authMdnsProfileMdns.forEach(profileMdn -> targetMdnForDeleteMap.put(profileMdn,targetMdnForDeleteForProfileMdns));
            }
            if(!targetForResourceListUpdate.isEmpty()) etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHome, targetForResourceListUpdate.keySet(), persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap 222 - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            etagMap = KnCorpCommonInfoUtil.formXcapAuthDiffNotification(etagMap, insertTargetMdnListMap, updateToMcpttInfoMap,
                    targetMdnForDeleteMap, targetForResourceListUpdate, corpSubsEntitiesDTOMap, authorizedMdn, authEtagMap.isEmpty());
            knLogger.debug(methodName, "etagMap 333 - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            // todo: LI Notification Preparation. // no SDD requirements.
            respDTO.setChangeLogMap(etagMap);
            knLogger.debug(methodName, "--->privacyEnabledMdns - ", KnGDPRTemplate.mdnList(privacyEnabledMdns));

            //etag calculation starts for privacy
            Set<String> privacyEnabledMdnsSet = mapListForPrivacy.keySet();
            knLogger.debug(methodName, "--->privacyEnabledMdnsSet - ", KnGDPRTemplate.mdnSet(privacyEnabledMdnsSet));
            Map<String,Integer> etagMapPrivacy=commonInfoUtil.updateAndGetDirecEtag(privacyEnabledMdnsSet,persisterTxn,xdmsHome);
            knLogger.debug(methodName, "--->etagMapPrivacy - ", etagMapPrivacy);
            //etag calculation ends for privacy
            //notification on success starts
            if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1){
                knLogger.info(methodName, "--->notification on success starts in controller");
            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            KnOPDocChgDTO docChgDTO;
            KnOPDirChgDTO dirChgDTO;
            int i = 0;
             for (String mdn : mapListForPrivacy.keySet()) {
                dirChgDTO = new KnOPDirChgDTO();
                docChgDTO = new KnOPDocChgDTO();
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();

                 if (contDetailsMap.get(mdn) != null){
                     knLogger.info(methodName, "contDetailsMap.get(mdn)::" + contDetailsMap.get(mdn) + " mdn::" + KnGDPRTemplate.mdn(mdn));
                 docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                 String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
                 docChgDTO.setDocUri(subsConfigDocUri);
                 docChgDTO.setNewEtag(String.valueOf(System.currentTimeMillis()));
                 chgDocList.add(docChgDTO);
                 //populating the XDM Directory DTO
                 dirChgDTO.setDocChgDTO(chgDocList);
                 dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                 dirChgDTO.setPocHome(contDetailsMap.get(mdn).getPocHome());
                 dirChgDTO.setPresenceHome(contDetailsMap.get(mdn).getPresenceHome());
                 String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                 dirChgDTO.setDirUri(dirDocUri);

                 if (privacyEnabledMdns.contains(mdn)) {
                     knLogger.debug(methodName, "--->Privacy Opt In reason code will be appearing for - " +KnGDPRTemplate.mdn(mdn));
                     dirChgDTO.setPrivacyReasonRequired(true);
                    // dirChgDTO.setProfileChanged(true);
                 }

                 // dirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                 //int newEtag = previousEtags.get(i) + 1;
                 //dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                 //
                 dirChgDTO.setDirPrevEtag(etagMapPrivacy.get(mdn).toString());
                 Integer newTag=etagMapPrivacy.get(mdn)+1;
                 dirChgDTO.setDirNewEtag(newTag.toString());
                 //
                 dirChgDTO.setProtoVersion(String.valueOf(contDetailsMap.get(mdn).getClientPVmajorVer()));
                 dirChgDTO.setClientType(contDetailsMap.get(mdn).getClientType());
                     knLogger.info(methodName, "--->subsConfigDocUri::" + subsConfigDocUri + " docChgDTO::" + docChgDTO + " dirChgDTO::" + dirChgDTO + " dirDocUri::" + dirDocUri + " etagMapPrivacy::" + etagMapPrivacy);
                     knLogger.debug(methodName, " --->Comparsion==>etagMapPrivacy.get(mdn)::" + etagMapPrivacy.get(mdn) + " newTag::" + newTag);

             }
                //populating the Dir chg DTO to response
                dirChgDTOs.add(dirChgDTO);
                i++;
                knLogger.info(methodName, " docChgDTO::" + docChgDTO + " dirChgDTO::" + dirChgDTO + " etagMapPrivacy::" + etagMapPrivacy);
            }
            respDTO.setDirChgDTOs(dirChgDTOs);
                /*if (mapListForPrivacy.size() > 0) {
                    respDTO.setProfileChanged(true);
                }*/
		}

            // notification ends
            populate(respDTO);
            //
            //pv related check at starting of logic functionality



            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in setTargetPermissions  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in setTargetPermissions ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    /**
     * Retrieve MCPTT Permission info:
     *
     * Validation:
     * - Validate the Corp ID, Id Context Info association
     * - Validate if the MDN exists as a Corp or Corp&Public Subscriber in the provided Corp ID On validation failure
     * reject the operation with appropriate error
     *
     * Business: XDM Data Mgr shall perform the below as part of API processing:
     * - Retrieve the data from DG.MCPTT_PERM_INFO and respond to the request
     *
     * @param ipAuthUserPermissionInfoDTO
     * @param persisterTxn
     */
    @Override
    public KnCorpUserPermissionRespDTO getTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDTO = new KnCorpUserPermissionRespDTO();
        try {
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            String corpId = String.valueOf(ipAuthUserPermissionInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            String authorizedMdn = ipAuthUserPermissionInfoDTO.getAuthorizedMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(authorizedMdn, PUBLIC_PROFILE, false, persisterTxn);
            if (subscProfile.getCorpId() <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", KnGDPRTemplate.mdn(authorizedMdn));
                throw new KnCorpBOException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipAuthUserPermissionInfoDTO.getCustomParamMap();
            if (ipAuthUserPermissionInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(authorizedMdn), customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipAuthUserPermissionInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_AUTH_USER);
                hookIPDTO.setData(ipAuthUserPermissionInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = new KnCorpMcpttFeaturePersistDTO();
            authUserListPersistDTO.setCorpId(subscProfile.getCorpId());
            authUserListPersistDTO.setInputDTO(ipAuthUserPermissionInfoDTO);
            authUserListPersistDTO.setSubsClientType(subscProfile.getClientType());
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", authUserListPersistDTO);
            validatorFW.validate(authUserListPersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            Map<String, Long> targetMdnPermBitMap = corpSubsProvInfoUtil.getTargetUserPermissions(ipAuthUserPermissionInfoDTO, xdmsHome, true, persisterTxn);
            knLogger.debug(methodName, "Fetch the target user permission profile: ", targetMdnPermBitMap);
            Map<String, Long> finaltargetMdnPermBitMap=new HashMap<>();
            //logic to convert profile mdn in TU to corresponding basemdn.
            if(targetMdnPermBitMap!=null&&!targetMdnPermBitMap.isEmpty()){
                List<String> tuList = targetMdnPermBitMap.keySet().stream()
                        .sorted()
                        .collect(Collectors.toList());
                //getting realm mdn if profile mdn present in TU list
                List<String> baseMdns = groupInfoUtil.getRealMdns(tuList, xdmsHome, true, persisterTxn);
                //getting map of profile to base mdn.
                Map<String, List<String>> profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(baseMdns, xdmsHome, true, persisterTxn);
                //OR of all profile mdns perm_bits
                for(Map.Entry<String, List<String>> baseMap:profileMdnOfBaseMap.entrySet()){
                    BitSet finalPermBits = new BitSet(Long.SIZE);
                    //base mdn
                    if(targetMdnPermBitMap.get(baseMap.getKey())!=null){
                        finalPermBits.or(featureSetUtil.convertLongToBitSet(targetMdnPermBitMap.get(baseMap.getKey())));
                    }
                    for(String profileMdn:baseMap.getValue()){
                        //there can be case where profile mdn of base is not part of TU
                        if(targetMdnPermBitMap.get(profileMdn.trim())!=null){
                            finalPermBits.or(featureSetUtil.convertLongToBitSet(targetMdnPermBitMap.get(profileMdn.trim())));
                        }
                    }
                    finaltargetMdnPermBitMap.put(baseMap.getKey(),(long)featureSetUtil.convertBitSetToLong(finalPermBits));
                }
            }
            knLogger.debug(methodName,"finaltargetMdnPermBitMap :",finaltargetMdnPermBitMap);
            ArrayList<String> authMdns = new ArrayList<String>(finaltargetMdnPermBitMap.keySet());
            Map<String, Integer> targetMdnCommonContactInfo = corpSubsProvInfoUtil.getCommonContactInfoFromMcpttPerm(ipAuthUserPermissionInfoDTO.getAuthorizedMdn(), xdmsHome, true, persisterTxn);
            Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
            if (finaltargetMdnPermBitMap != null) {
                finaltargetMdnPermBitMap.forEach((targetMdn, permissions) -> {
                    KnTargetMdnPermBitInfo targetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
                    BitSet mcpttPermissions = featureSetUtil.convertLongToBitSet(permissions);
                    knLogger.debug(methodName, "targetMdn: ", KnGDPRTemplate.mdn(targetMdn), " mcpttPermissions: ", mcpttPermissions);
                    targetMdnPermBitInfo.setMdn(targetMdn);
                    corpSubsProvInfoUtil.bitsToIntegerConversion(mcpttPermissions, targetMdnPermBitInfo);
                    if (null != targetMdnCommonContactInfo.get(targetMdn) && targetMdnCommonContactInfo.get(targetMdn) == COMMON_CONTACT) {
                        targetMdnPermBitInfo.setCommonContact(targetMdnCommonContactInfo.get(targetMdn));
                    }
                    targetMdnPermBitInfos.add(targetMdnPermBitInfo);
                });
            }
            respDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getTargetPermissions  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getTargetPermissions ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    /**
     * Retrieve MCPTT Permission info: XDMDataIntf
     *
     * No Validation.
     * Business: XDM Data Mgr shall perform the below as part of API processing:
     * - Retrieve the data from DG.MCPTT_PERM_INFO and respond to the request
     *
     * @param ipAuthUserPermissionInfoDTO
     * @param persisterTxn
     */
    @Override
    public KnCorpUserPermissionRespDTO getSubsTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubsTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDTO = new KnCorpUserPermissionRespDTO();
        try {
            String authorizedMdn = ipAuthUserPermissionInfoDTO.getAuthorizedMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(authorizedMdn, PUBLIC_PROFILE, false, persisterTxn);
            String xdmsHome = subscProfile.getXdmsHome();
            Map<String, KnMcpttPermissionDTO> mcpttPermissionMap = corpSubsProvInfoUtil.getAuthUserPermissions(authorizedMdn, xdmsHome,true, persisterTxn);
            knLogger.debug(methodName, "Fetch the target user permission profile: ", KnGDPRTemplate.mapKeyMdn(mcpttPermissionMap));
            Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            mcpttPermissionMap.forEach((targetMdn, value) -> {
                KnTargetMdnPermBitInfo targetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
                targetMdnPermBitInfo.setMdn(targetMdn);
                targetMdnPermBitInfo.setPermBitSet(value.getMcpttPerms());
                targetMdnPermBitInfo.setDiscreteEnabled(value.getDiscreteEnabled());
                BitSet mcpttPermissions = featureSetUtil.convertLongToBitSet(value.getMcpttPerms());
                knLogger.debug(methodName, "targetMdn: ", KnGDPRTemplate.mdn(targetMdn), " mcpttPermissions: ", mcpttPermissions);
                corpSubsProvInfoUtil.bitsToIntegerConversion(mcpttPermissions, targetMdnPermBitInfo);
                if(value.getCommonAu() != null) {
                    targetMdnPermBitInfo.setCommonContact(value.getCommonAu());
                }
                targetMdnPermBitInfos.add(targetMdnPermBitInfo);
            });
            respDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getSubsTargetPermissions  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getSubsTargetPermissions ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpUserPermissionRespDTO getAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        String methodName = "getAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDTO = new KnCorpUserPermissionRespDTO();
        try {
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            String corpId = String.valueOf(ipAuthUserPermissionInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            String targetMdn = ipAuthUserPermissionInfoDTO.getTargetMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(targetMdn, PUBLIC_PROFILE, false, persisterTxn);
            if (subscProfile.getCorpId() <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", KnGDPRTemplate.mdn(targetMdn));
                throw new KnCorpBOException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipAuthUserPermissionInfoDTO.getCustomParamMap();
            if (ipAuthUserPermissionInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(targetMdn), customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipAuthUserPermissionInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_AUTH_USER);
                hookIPDTO.setData(ipAuthUserPermissionInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = new KnCorpMcpttFeaturePersistDTO();
            authUserListPersistDTO.setCorpId(subscProfile.getCorpId());
            authUserListPersistDTO.setInputDTO(ipAuthUserPermissionInfoDTO);
            authUserListPersistDTO.setSubsClientType(subscProfile.getClientType());
            authUserListPersistDTO.setMdn(targetMdn);
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", authUserListPersistDTO);
            validatorFW.validate(authUserListPersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            Map<String, Integer> authMdnPermMap = corpSubsProvInfoUtil.getAuthMdnList(ipAuthUserPermissionInfoDTO, xdmsHome, readOnly, persisterTxn);
            knLogger.debug(methodName, "Fetch the Auth permissions profile: ", authMdnPermMap);
            Map<String, Integer>  finaltargetMdnPermBitMap=new HashMap<>();
            //logic to convert profile mdn in TU to corresponding basemdn.
            if (authMdnPermMap != null && !authMdnPermMap.isEmpty()) {
                List<String> auList = new ArrayList<>(authMdnPermMap.keySet());
                //getting realm mdn if profile mdn present in TU list
                List<String> baseMdns = groupInfoUtil.getRealMdns(auList, xdmsHome, readOnly, persisterTxn);
                //getting map of profile to base mdn.
                Map<String, List<String>> profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(baseMdns, xdmsHome, readOnly, persisterTxn);
                //OR of all profile mdns perm_bits
                for (Map.Entry<String, List<String>> baseMap : profileMdnOfBaseMap.entrySet()) {
                    BitSet finalPermBits = new BitSet(Long.SIZE);
                    //Only base mdn should be returned in the reponse and exclude the profile mdn if present as AU
                    if (null != authMdnPermMap.get(baseMap.getKey())) {
                        finalPermBits.or(featureSetUtil.convertLongToBitSet(authMdnPermMap.get(baseMap.getKey())));
                        finaltargetMdnPermBitMap.put(baseMap.getKey(), (int)featureSetUtil.convertBitSetToLong(finalPermBits));
                    }
                }
            }
            knLogger.debug(methodName,"finaltargetMdnPermBitMap :",finaltargetMdnPermBitMap);

            Set<String> authMdnList = finaltargetMdnPermBitMap.keySet();
            Map<String, String> subscNameList = contactInfoUtil.getSubscribersName(authMdnList, xdmsHome, readOnly, persisterTxn);
            knLogger.debug(methodName, "subscNameList: ", KnGDPRTemplate.mdnMap(subscNameList));

            Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
            finaltargetMdnPermBitMap.forEach((auth, permissions) -> {
                KnTargetMdnPermBitInfo targetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
                BitSet mcpttPermissions = featureSetUtil.convertLongToBitSet(permissions);
                knLogger.debug(methodName, "auth: ", auth, " mcpttPermissions: ", mcpttPermissions);
                targetMdnPermBitInfo.setMdn(auth);
                targetMdnPermBitInfo.setSubsName(subscNameList.get(auth));
                corpSubsProvInfoUtil.bitsToIntegerConversion(mcpttPermissions, targetMdnPermBitInfo);
                targetMdnPermBitInfos.add(targetMdnPermBitInfo);
            });
            respDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getAuthorizedMdnList  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getAuthorizedMdnList ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO setSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "setSubsEmergencyAttributes(KnIPEmergencyInfoDTO, KnPersisterTxn)";
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
            if (ipEmergencyInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipEmergencyInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SUBS_EMERGENCY_ATTRIBUTES);
                hookIPDTO.setData(ipEmergencyInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnCorpMcpttFeaturePersistDTO emergencyMcpttFeaturePersistDTO = new KnCorpMcpttFeaturePersistDTO();
            String priDest = ipEmergencyInfoDTO.getPriDestination();
            String secDest = ipEmergencyInfoDTO.getSecDestination();
            LinkedList<String> destinations = new LinkedList<>();
            destinations.add(priDest);
            destinations.add(secDest);
            emergencyMcpttFeaturePersistDTO.setDestinations(destinations);
            Collection<KnSubsDestEmergencyAttributes> insertToEmergAttributes = new ArrayList<>();
            Map<String, Integer> newPriority = new HashMap<>();
            Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
            if(ipEmergencyInfoDTO.getEmergDestType() == DESTINATION_TYPE.CAT_CONFIGURED_DESTINATION.value()
                    && ipEmergencyInfoDTO.getEmergInitPermission() != DISABLED){
                KnCorpSubscriberDTO corpsubsriber=contactInfoUtil.selectPocSubscriberInfo(mdn, xdmsHome, persisterTxn);
                String userProfileId = corpsubsriber.getUserProfileId();
                knLogger.info(methodName, " userProfileId:", userProfileId);
                ArrayList<String> upmId = new ArrayList<>();
                upmId.add(userProfileId);
                Map<String, Integer> upmOwnerList = userProfileUtil.getUserProfileOwnerinfo(upmId, xdmsHome, persisterTxn);
                Integer groupOwnerCorpId=ipEmergencyInfoDTO.getCorpId();
                if(!upmOwnerList.isEmpty()){
                    groupOwnerCorpId=upmOwnerList.get(userProfileId);
                }
                knLogger.debug(methodName,"ownerCorpId :",groupOwnerCorpId);
                Map<String, Integer> groupIdTypesMap = groupInfoUtil.getValidGrpTypeInCorp(destinations, groupOwnerCorpId,
                        xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupIdTypesMap - ", groupIdTypesMap);
                //changes for group sharing
                //check whether for the given corpId any shared groups are avilable or not
                //if any shared groups are avilable then add shared group info to dbGroupList to avoid rules
                Set<Integer> sharedCorpGrpIds  = new HashSet<>(1);
                Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap = groupInfoUtil.selectGroupSharedCorpInfoBySharedCorpId(groupOwnerCorpId,xdmsHome,persisterTxn);

                if(sharedCorpGrpInfoMap!=null && !sharedCorpGrpInfoMap.isEmpty()) {
                    sharedCorpGrpIds = sharedCorpGrpInfoMap.keySet();
                    knLogger.debug(methodName, "sharedCorpGrpIds", sharedCorpGrpIds);
                    //TODO:Currently only mcxGroups with standary type are allowed hence fixing grpType for shared group as 1
                    sharedCorpGrpIds.forEach(grpId -> groupIdTypesMap.put(String.valueOf(grpId),1));
                    sharedCorpGrpInfoMap.forEach((grpId, sharedInfoList) -> {
                        if (!sharedInfoList.isEmpty()) groupCorpIdMap.put(grpId, sharedInfoList.get(0).getOwnerCorpId());
                    });
                }
                knLogger.debug(methodName, "groupIdTypesMap - ", groupIdTypesMap);


                /**
                 * This logic currently applicable for hiereiarchy path only incase if the requests are coming from hieriarcy path
                 */
                knLogger.debug(methodName, "isHierarchyCall - ", isHierarchyCall);
                if(isHierarchyCall) {
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
                    if(groupType != BROADCAST_GROUP){
                        validGroupId.add(Integer.parseInt(groupId));
                    }
                });
                List<Integer> groupidList=new ArrayList<Integer>();
                List<Integer> mcxGroupIds=new ArrayList<Integer>();
                Map<String, String> mdnExistenceInGroup =new HashMap<String, String>();

                ArrayList<KnCorpGroupDTO> groupDetailsList=    groupInfoUtil.getGroupBasicInfoList(validGroupId, xdmsHome, persisterTxn);
                if(groupDetailsList!=null) {
                	for(KnCorpGroupDTO knCorpGroupDTO:groupDetailsList) {
                		if(knCorpGroupDTO.getMcxGrpInd()==1) {

                			mcxGroupIds.add(knCorpGroupDTO.getGroupId());
                			mdnExistenceInGroup.put(String.valueOf(knCorpGroupDTO.getGroupId()),mdn);
                		}
                	}
                }
                if(!mcxGroupIds.isEmpty()) {

                  if(corpsubsriber!=null&&corpsubsriber.getUserProfileId()!=null) {

               Set<KnCorpGroupListInfoDTO> knCorpGroupListInfoDTOList= userProfileUtil.retriveGroupProfileInfoByProfileId(corpsubsriber.getUserProfileId(), xdmsHome, persisterTxn);
                     if(knCorpGroupListInfoDTOList!=null) {

                    	 for(KnCorpGroupListInfoDTO corpGrpinfo:knCorpGroupListInfoDTOList) {
                    		 if(mcxGroupIds.contains(corpGrpinfo.getGroupID()) && mdnExistenceInGroup.get(String.valueOf(corpGrpinfo.getGroupID()))==null) {

                    				 mdnExistenceInGroup.put(String.valueOf(corpGrpinfo.getGroupID()),mdn);


                    		 }
                    	 }
                     }
                }
                }
                Map<String, String> mdnExistenceInNormalGroup = groupInfoUtil.selectGroupMemberForGroupIds(validGroupId, mdn, xdmsHome, persisterTxn);
                mdnExistenceInGroup.putAll(mdnExistenceInNormalGroup);
                knLogger.debug(methodName, "mdnExistenceInGroup - ", KnGDPRTemplate.mdnMap(mdnExistenceInGroup));
                emergencyMcpttFeaturePersistDTO.setGroupTypeMap(groupIdTypesMap);
                emergencyMcpttFeaturePersistDTO.setMdnExistenceInGroup(mdnExistenceInGroup);
                Collection<String> mdnList = new ArrayList<>();
                mdnList.add(mdn);
                Map<String, Collection<String>> mdnContactList = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "mdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(mdnContactList));
                emergencyMcpttFeaturePersistDTO.setSubsContactList(mdnContactList);
                int priority = 0;
                KnSubsDestEmergencyAttributes destEmergencyAttributes = null;
                for(String dest : destinations){
                    if(mdnExistenceInGroup != null && mdnExistenceInGroup.keySet().contains(dest)){
                        destEmergencyAttributes = new KnSubsDestEmergencyAttributes();
                        destEmergencyAttributes.setMdn(mdn);
                        destEmergencyAttributes.setEmergDestPriority(++priority);
                        destEmergencyAttributes.setEmergDestTypeMgmt(DESTINATION_TYPE_MGMT.GROUP.value());
                        destEmergencyAttributes.setEmergDest(dest);
                        insertToEmergAttributes.add(destEmergencyAttributes);
                        newPriority.put(dest, destEmergencyAttributes.getEmergDestPriority());
                    } else if(mdnContactList != null && mdnContactList.get(mdn) != null && mdnContactList.get(mdn).contains(dest)){
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
            if(upmSharedList != null) {
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
            validatorFW.validate(emergencyMcpttFeaturePersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            Map<String, Long> emergEtagMap = corpSubsProvInfoUtil.seleteFromEmergDoc(mdnList, xdmsHome, persisterTxn);
            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributes = corpSubsProvInfoUtil.getEmergAttributes(mdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "mdnEmergAttributes - ", mdnEmergAttributes);
            Map<String, Integer> oldPriority = mdnEmergAttributes.stream().collect(Collectors.toMap(KnSubsDestEmergencyAttributes::getEmergDest, KnSubsDestEmergencyAttributes::getEmergDestPriority));
            KnSubsEmergencyAttributes subsEmergencyAttributes = corpSubsProvInfoUtil.getEmergSubsAttributes(mdn, ipEmergencyInfoDTO.getCorpId(), xdmsHome, false, persisterTxn);
            knLogger.debug(methodName, "subsEmergencyAttributes - ", subsEmergencyAttributes);
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

            if (null != ipEmergencyInfoDTO.getEmergConfigTimer()) {
                String emergencyFeatureFlag = microServicesParamNameValueMap.get(EMERGENCY_CONF_TIMER_FEATURE);
                boolean emergencyBit = Optional.ofNullable(corpProfile.getXdmCorpFS2Set())
                        .map(set -> KnGeneralUtil.getFeatureBitValue(set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value()))
                        .orElse(false);
                knLogger.debug(methodName, "emergencyFeatureFlag is :-", emergencyFeatureFlag, "emergencyBit is :-", emergencyBit);
                if (!emergencyFeatureFlag.equals(ENABLED_STRING) || !emergencyBit) {
                    knLogger.error("System level or Corporate Level EMERGENCY_CONF_TIMER_FEATURE flag is disabled");
                    throw new KnCorpBOValidationException(EMERGCONFIGTIMER_FEATURE_DISABLED, "EMERGCONFIGTIMER_FEATURE flag is disabled", "");
                }
                Float emeConfTimer = Float.valueOf(ipEmergencyInfoDTO.getEmergConfigTimer());
                Float minEmeTimer = Float.valueOf(microServicesParamNameValueMap.getOrDefault(MIN_EMERGENCY_TIME, "0"));
                Float maxEmeTimer = Float.valueOf(microServicesParamNameValueMap.getOrDefault(MAX_EMERGENCY_TIME,"5"));
                knLogger.debug(methodName, "emeConfTimer is :-", emeConfTimer, "minEmeTimer is :-", minEmeTimer, "maxEmeTimer is :-", maxEmeTimer);
                if (emeConfTimer < minEmeTimer || emeConfTimer > maxEmeTimer) {
                    throw new KnCorpBOValidationException(EMERGCONFIGTIMER_NOT_IN_RANGE, "Emergency Timer value not in range", "");
                }
            }
            KnSubsEmergencyAttributes updateSubsEmergencyAttributes = null;
            boolean subsProfileChanged = false;
            if (mdnEmergAttributes.size() > 0) {
                corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdn, xdmsHome, persisterTxn);
            }
            if (ipEmergencyInfoDTO.getEmergInitPermission() == DISABLED) {
                updateSubsEmergencyAttributes = new KnSubsDestEmergencyAttributes();
                updateSubsEmergencyAttributes.setMdn(mdn);
                updateSubsEmergencyAttributes.setEmergInitPermission(ipEmergencyInfoDTO.getEmergInitPermission() != null ?
                        ipEmergencyInfoDTO.getEmergInitPermission() : subsEmergencyAttributes.getEmergInitPermission());
                updateSubsEmergencyAttributes.setEmergLmrBehaviour(ipEmergencyInfoDTO.getEmergLMRBehavior() != null ?
                        ipEmergencyInfoDTO.getEmergLMRBehavior() : subsEmergencyAttributes.getEmergLmrBehaviour());
                updateSubsEmergencyAttributes.setEmergOriginBitSet(ipEmergencyInfoDTO.getEmergOriginBitSet() != null ?
                        ipEmergencyInfoDTO.getEmergOriginBitSet() : subsEmergencyAttributes.getEmergOriginBitSet());
                updateSubsEmergencyAttributes.setEmergTermBitSet(ipEmergencyInfoDTO.getEmergTermBitSet() != null ?
                        ipEmergencyInfoDTO.getEmergTermBitSet() : subsEmergencyAttributes.getEmergTermBitSet());
            }
            if ((mdnEmergAttributes.size() > 0 && ipEmergencyInfoDTO.getEmergInitPermission() != DISABLED)
                    || (ipEmergencyInfoDTO.getEmergInitPermission() != DISABLED && mdnEmergAttributes.isEmpty())) {
                updateSubsEmergencyAttributes = new KnSubsDestEmergencyAttributes();
                updateSubsEmergencyAttributes.setMdn(mdn);
                updateSubsEmergencyAttributes.setEmergDestTypeIntf(ipEmergencyInfoDTO.getEmergDestType());
                updateSubsEmergencyAttributes.setEmergCallType(ipEmergencyInfoDTO.getEmergCallType());
                updateSubsEmergencyAttributes.setEmergCnclPermission(ipEmergencyInfoDTO.getEmergCancelPermission());
                if(ipEmergencyInfoDTO.getEmergLMRBehavior() != null){
                    updateSubsEmergencyAttributes.setEmergLmrBehaviour(ipEmergencyInfoDTO.getEmergLMRBehavior());
                } else {
                    updateSubsEmergencyAttributes.setEmergLmrBehaviour(subsEmergencyAttributes.getEmergLmrBehaviour());
                }
                if(ipEmergencyInfoDTO.getEmergInitPermission() != null && ipEmergencyInfoDTO.getEmergInitPermission() != EMERGENCY_INIT_NOT_MODIFIED){
                    updateSubsEmergencyAttributes.setEmergInitPermission(ipEmergencyInfoDTO.getEmergInitPermission());
                } else {
                    updateSubsEmergencyAttributes.setEmergInitPermission(subsEmergencyAttributes.getEmergInitPermission());
                }
                if(ipEmergencyInfoDTO.getEmergOriginBitSet() != null){
                    updateSubsEmergencyAttributes.setEmergOriginBitSet(ipEmergencyInfoDTO.getEmergOriginBitSet());
                } else {
                    updateSubsEmergencyAttributes.setEmergOriginBitSet(subsEmergencyAttributes.getEmergOriginBitSet());
                }
                if(ipEmergencyInfoDTO.getEmergTermBitSet() != null){
                    updateSubsEmergencyAttributes.setEmergTermBitSet(ipEmergencyInfoDTO.getEmergTermBitSet());
                } else {
                    updateSubsEmergencyAttributes.setEmergTermBitSet(subsEmergencyAttributes.getEmergTermBitSet());
                }
                updateSubsEmergencyAttributes.setEmergConfigTimer(ipEmergencyInfoDTO.getEmergConfigTimer() != null ?
                        ipEmergencyInfoDTO.getEmergConfigTimer() : subsEmergencyAttributes.getEmergConfigTimer());
            }
            corpSubsProvInfoUtil.updateToEmergSubsDestInfo(updateSubsEmergencyAttributes, xdmsHome, persisterTxn);
            if(!insertToEmergAttributes.isEmpty() ){
                corpSubsProvInfoUtil.insertIntoEmergSubsAttributes(insertToEmergAttributes, xdmsHome, persisterTxn);
            }
            Map<String, KnOPDirChgDTO> etagMap = corpSubsProvInfoUtil.updateEmergencyImpactedTables(xdmsHome, mdn, subscProfile.getCorpId(),
                    persisterTxn, null);
            boolean isEmergencyAttributesChanged = !updateSubsEmergencyAttributes.equals(subsEmergencyAttributes) || !newPriority.equals(oldPriority);
            respDTO.setEmergencyAttributesChanged(isEmergencyAttributesChanged);
            knLogger.debug(methodName, "etagMap 111 - ", KnGDPRTemplate.mapKeyMdn(etagMap),"isEmergencyAttributesChanged::", isEmergencyAttributesChanged);
            if((ipEmergencyInfoDTO.getEmergCallType() != subscProfile.getEmergCallType())
                    || (ipEmergencyInfoDTO.getEmergCancelPermission() != subscProfile.getEmergCancelPermission())
                    || (ipEmergencyInfoDTO.getEmergLMRBehavior() != null && (ipEmergencyInfoDTO.getEmergLMRBehavior() != subscProfile.getEmergLmrBehaviour()))
            || !Objects.equals(ipEmergencyInfoDTO.getEmergConfigTimer(), subsEmergencyAttributes.getEmergConfigTimer()) || isEmergencyAttributesChanged){
                subsProfileChanged = true;
            }
            etagMap = KnCorpCommonInfoUtil.formXcapEmergDiffNotification(etagMap, insertToEmergAttributes, mdnEmergAttributes,
                    oldPriority, newPriority, updateSubsEmergencyAttributes, subsEmergencyAttributes, mdn, corpId, emergEtagMap.isEmpty(),
                    groupCorpIdMap);
            knLogger.debug(methodName, "etagMap 333 - ", KnGDPRTemplate.mapKeyMdn(etagMap));

            // todo: LI Notification Preparation. // no SDD requirements.
            respDTO.setChangeLogMap(etagMap);
            respDTO.setProfileChanged(subsProfileChanged);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in setSubsEmergencyAttributes  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in setSubsEmergencyAttributes ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpUserEmergencyAttributesRespDTO getSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubsEmergencyAttributes(KnIPEmergencyInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipEmergencyInfoDTO);
        KnCorpUserEmergencyAttributesRespDTO respDTO = new KnCorpUserEmergencyAttributesRespDTO();
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
            if (ipEmergencyInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipEmergencyInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SUBS_EMERGENCY_ATTRIBUTES);
                hookIPDTO.setData(ipEmergencyInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnCorpMcpttFeaturePersistDTO emergencyMcpttFeaturePersistDTO = new KnCorpMcpttFeaturePersistDTO();
            emergencyMcpttFeaturePersistDTO.setCorpId(subscProfile.getCorpId());
            emergencyMcpttFeaturePersistDTO.setInputDTO(ipEmergencyInfoDTO);
            emergencyMcpttFeaturePersistDTO.setMdn(mdn);
            emergencyMcpttFeaturePersistDTO.setActiveFS2(subscProfile.getActiveFS2());
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", emergencyMcpttFeaturePersistDTO);
            validatorFW.validate(emergencyMcpttFeaturePersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributes = corpSubsProvInfoUtil.getEmergAttributes(mdn, xdmsHome, true, persisterTxn);
            knLogger.debug(methodName, "mdnEmergAttributes - ", mdnEmergAttributes);
            KnSubsEmergencyAttributes subsEmergencyAttributes = corpSubsProvInfoUtil.getEmergSubsAttributes(mdn, ipEmergencyInfoDTO.getCorpId(), xdmsHome, true, persisterTxn);
            knLogger.debug(methodName, "subsEmergencyAttributes - ", subsEmergencyAttributes);
            Map<String, Integer> destPriority = mdnEmergAttributes.stream().collect(Collectors.toMap(KnSubsDestEmergencyAttributes::getEmergDest, KnSubsDestEmergencyAttributes::getEmergDestPriority));
            KnCorpUserEmergencyAttributes corpUserEmergencyAttributes = new KnCorpUserEmergencyAttributes();
            corpUserEmergencyAttributes.setEmergInitPermission(subsEmergencyAttributes.getEmergInitPermission());
            corpUserEmergencyAttributes.setEmergCancelPermission(subsEmergencyAttributes.getEmergCnclPermission());
            corpUserEmergencyAttributes.setEmergCallType(subsEmergencyAttributes.getEmergCallType());
            corpUserEmergencyAttributes.setEmergOriginBitSet(subsEmergencyAttributes.getEmergOriginBitSet());
            corpUserEmergencyAttributes.setEmergDestType(subsEmergencyAttributes.getEmergDestTypeIntf());
            destPriority.forEach((dest, priority) -> {
                if (priority == DESTINATION_PRIORITY.FIRST_PRIORITY.value()) {
                    corpUserEmergencyAttributes.setPriDestination(dest);
                } else if (priority == DESTINATION_PRIORITY.SECOND_PRIORITY.value()) {
                    corpUserEmergencyAttributes.setSecDestination(dest);
                }
            });
            corpUserEmergencyAttributes.setMdn(mdn);
            corpUserEmergencyAttributes.setEmergTermBitSet(subsEmergencyAttributes.getEmergTermBitSet());
            corpUserEmergencyAttributes.setEmergLMRBehavior(subsEmergencyAttributes.getEmergLmrBehaviour());
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String emergencyFeatureFlag = microServicesParamNameValueMap.get(EMERGENCY_CONF_TIMER_FEATURE);
            boolean emergencyBit = Optional.ofNullable(corpProfile.getXdmCorpFS2Set())
                    .map(set -> KnGeneralUtil.getFeatureBitValue(set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value()))
                    .orElse(false);
            if (emergencyFeatureFlag.equals(ENABLED_STRING) && emergencyBit) {
                String emergenConfTimer = null != subsEmergencyAttributes.getEmergConfigTimer() ? subsEmergencyAttributes.getEmergConfigTimer() : microServicesParamNameValueMap.get(DEFAULT_EMERGENCY_TIMER);
                corpUserEmergencyAttributes.setEmergConfigTimer(emergenConfTimer);
            }
            knLogger.debug(methodName, "emergencyFeatureFlag is :-", emergencyFeatureFlag, "emergencyBit is :-", emergencyBit);
            respDTO.setCorpUserEmergencyAttributes(corpUserEmergencyAttributes);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getSubsEmergencyAttributes  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getSubsEmergencyAttributes ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnEmergUserDestRespDTO getUserEmergDest(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getUserEmergDest(KnIPEmergencyInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipEmergencyInfoDTO);
        KnEmergUserDestRespDTO respDTO = new KnEmergUserDestRespDTO();
        try {
            String corpId = String.valueOf(ipEmergencyInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            int emergDestType = ipEmergencyInfoDTO.getEmergDestType();
            String destination = ipEmergencyInfoDTO.getPriDestination();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipEmergencyInfoDTO.getCustomParamMap();
            if (ipEmergencyInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                //TODO check first if destination can be external or not
                if(emergDestType == KnConstants.DESTINATION_TYPE_MGMT.CONTACT.value()) {
                    contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(destination), customParams, xdmsHome, persisterTxn);
                } else if(emergDestType == KnConstants.DESTINATION_TYPE_MGMT.GROUP.value()) {
                    //TODO validation check provided group are from incontext fan/ban
                    if (null != customParams) {
                        Collection<String> idListStr = (Collection<String>) customParams.get(com.kodiak.common.resources.KnConstants.IDLIST);
                        Object idTypeObj = customParams.get(IDTYPE);
                        int idTypeValues;
                        if (idTypeObj instanceof Integer) {
                            idTypeValues = (Integer) idTypeObj;
                        } else if (idTypeObj instanceof String) {
                            idTypeValues = Integer.parseInt((String) idTypeObj);
                        } else {
                            throw new IllegalArgumentException("Unsupported type for IDTYPE: " + idTypeObj.getClass().getName());
                        }
                        if (null != idListStr) {
                            List<Integer> idListInt = (List<Integer>) convertStrToIntColl(idListStr);
                            knLogger.debug("idListInt::", idListInt);
                            commonInfoUtil.validateGroupParams(Collections.singletonList(destination), corpId, idListInt, xdmsHome, idTypeValues, persisterTxn);
                        }
                    }
                }
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipEmergencyInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SUBS_EMERGENCY_ATTRIBUTES);
                hookIPDTO.setData(ipEmergencyInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            if (emergDestType == KnConstants.DESTINATION_TYPE_MGMT.GROUP.value()) {
                commonInfoUtil.validateCorpAndGroupParams(Collections.singletonList(destination), corpId, xdmsHome, persisterTxn);
            }
            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributesForDestination =
                    corpSubsProvInfoUtil.getEmergDestAttributesForDestination(destination, xdmsHome, true, persisterTxn);
            knLogger.debug(methodName, "mdnEmergAttributesForDestination- ", mdnEmergAttributesForDestination);
            List<KnMDNInfoDto> filteredEmergUser = new ArrayList<>();
            if(mdnEmergAttributesForDestination != null && !mdnEmergAttributesForDestination.isEmpty()){
                Collection<String> emergUsers = mdnEmergAttributesForDestination.stream().filter(emergDto ->
                        emergDto.getEmergDestTypeMgmt() == emergDestType).map(KnSubsDestEmergencyAttributes::getMdn).collect(Collectors.toList());
                Set<String> emergencyBaseMdn = corpSubsProvInfoUtil.getBaseMdnByProfileMdns(new ArrayList<>(emergUsers), xdmsHome, true, persisterTxn);
                for(String emergMdn : emergencyBaseMdn){
                    KnMDNInfoDto mdnInfoDto = new KnMDNInfoDto();
                    mdnInfoDto.setMdn(emergMdn);
                    filteredEmergUser.add(mdnInfoDto);
                }
            }
            respDTO.setMdnInfoDtoList(filteredEmergUser);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getUserEmergDest  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getUserEmergDest ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO updateSubsAliasEntities(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateSubsAliasEntities(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }

            String mdn = ipSubscriberInfoDTO.getMdn();
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(ipSubscriberInfoDTO.getMdn(), PUBLIC_PROFILE, false, persisterTxn);
            String xdmsHomePttId = subscProfile.getXdmsHome();
            String mdnForRequestUserId = corpSubsProvInfoUtil.getUserIdProfile(ipSubscriberInfoDTO, xdmsHome, persisterTxn);
            String mdnForRequestAliasMdn = corpSubsProvInfoUtil.getAliasMdnProfile(ipSubscriberInfoDTO, xdmsHome, persisterTxn);
            KnCorpSubscriberDTO subscriberDTO = contactInfoUtil.selectSubsInfo(ipSubscriberInfoDTO.getAliasMdn(), xdmsHome, persisterTxn);
            KnCorpSubscriberDTO pamSubsDTO = licenseInfoUtil.getPamAccountId(ipSubscriberInfoDTO.getAliasMdn(), xdmsHomePttId, persisterTxn);
            String extMdn = contactInfoUtil.getExtSubsrProfile(ipSubscriberInfoDTO.getAliasMdn(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "mdnForRequestUserId: ", mdnForRequestUserId, "mdnForRequestAliasMdn: ", KnGDPRTemplate.mdn(mdnForRequestAliasMdn),
                    "subscriberDTO: ", subscriberDTO, "pamSubsDTO: ", pamSubsDTO, "extMdn: ", KnGDPRTemplate.mdn(extMdn));
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            persistDTO.setSubsCorpId(subscProfile.getCorpId());
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setLicenseType(subscProfile.getLicenseType());
            persistDTO.setDbUserID(subscProfile.getUserId());
            if(mdnForRequestUserId != null && !mdnForRequestUserId.isEmpty()){
                persistDTO.setUserIdExists(true);
            }
            if(mdnForRequestAliasMdn != null || subscriberDTO != null || pamSubsDTO != null || extMdn != null){
                persistDTO.setAliasMdnExists(true);
            }
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            //verifying if network name is changed
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();

            String dbUserId = subscProfile.getUserId();
            String userId = dbUserId;
            String dbAliasMdn = subscProfile.getAliasMdn();
            String aliasMdn = dbAliasMdn;
            boolean aliasMdnChange = false;
            boolean userIdChange = false;

            if (ipSubscriberInfoDTO.getUserId() != null && !ipSubscriberInfoDTO.getUserId().equals(dbUserId)) {
                userId = ipSubscriberInfoDTO.getUserId();
                userIdChange = true;
            }
            if (ipSubscriberInfoDTO.getUserId() != null && dbUserId != null) {
                respDTO.setUserOverriden(true);
                responseMap.put(OLD_USER_ID, dbUserId);
            }
            if (ipSubscriberInfoDTO.getAliasMdn() != null && !ipSubscriberInfoDTO.getAliasMdn().equals(dbAliasMdn)) {
                aliasMdn = ipSubscriberInfoDTO.getAliasMdn();
                aliasMdnChange = true;
            }
            if (ipSubscriberInfoDTO.getAliasMdn() != null && dbAliasMdn != null) {
                respDTO.setAliasOverriden(true);
                responseMap.put(OLD_ALIAS_MDN, dbAliasMdn);
            }
            respDTO.setUserIdChanged(userIdChange);
            respDTO.setAliasMdnChanged(aliasMdnChange);
            knLogger.debug(methodName, "userIdChange - ", userIdChange, "aliasMdnChange -", aliasMdnChange);
            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            Set<String> contactMDNForDirectoryChange = new HashSet<>();
            if(userIdChange || aliasMdnChange) {
                respDTO.setProfileChanged(true);
                contactMDNForDirectoryChange.add(mdn);
                if (!contactMDNForDirectoryChange.isEmpty()) {
                    knLogger.debug(methodName, "Updating Directory Etags contactMDNForDirectoryChange", KnGDPRTemplate.mdnSet(contactMDNForDirectoryChange));
                    etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNForDirectoryChange, null, etagMap, xdmsHomePttId, persisterTxn);
                }
                ipSubscriberInfoDTO.setMdn(mdn);
                ipSubscriberInfoDTO.setLastProfileUpdateTime(System.currentTimeMillis());
                if(userId.isEmpty()){
                    ipSubscriberInfoDTO.setUserId(null);
                } else {
                    ipSubscriberInfoDTO.setUserId(userId);
                }
                if(aliasMdn.isEmpty()){
                    ipSubscriberInfoDTO.setAliasMdn(null);
                } else {
                    ipSubscriberInfoDTO.setAliasMdn(aliasMdn);
                }
                corpSubsProvInfoUtil.updateSubsEntities(ipSubscriberInfoDTO, xdmsHome, persisterTxn);
                // Subscriber Config Profile Notification:
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(ipSubscriberInfoDTO.getLastProfileUpdateTime()));
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                chgDocList.add(docChgDTO);
                //populating the XDM Directory DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                dirChgDTO.setPocHome(subscProfile.getPocHome());
                dirChgDTO.setPresenceHome(subscProfile.getPresenceHome());
                dirChgDTO.setDocChgDTO(chgDocList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirPrevEtag() : null);
                dirChgDTO.setDirNewEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirNewEtag() : null);
                dirChgDTO.setProtoVersion(subscProfile.getProtocolVersion());
                dirChgDTOs.add(dirChgDTO);
            }
            knLogger.debug(methodName, "dirChgDTOs - ", dirChgDTOs);
            respDTO.setDirChgDTOs(dirChgDTOs);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
            respDTO.setChangeLogMap(etagMap);
            respDTO.setMdnCorpId(Integer.parseInt(corpId));
            respDTO.setMdn(mdn);
            respDTO.setActiveFS2(subscProfile.getActiveFS2());
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in updateCorpSubscriber  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in updateCorpSubscriber ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        respDTO.setResponseMap(responseMap);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO generateTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "generateTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(ipSubscriberInfoDTO.getMdn()), customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            String mdn = ipSubscriberInfoDTO.getMdn();
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
            KnPocSubsAddlInfoDTO subsAddlDetails = commonInfoUtil.getSubsAddlDetails(subscProfile.getMdn(),persisterTxn);
            if (null != ipSubscriberInfoDTO.getCorpId() && Integer.parseInt(ipSubscriberInfoDTO.getCorpId()) > 0 && subscProfile.getCorpId() != Integer.parseInt(ipSubscriberInfoDTO.getCorpId())) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Subscribers Does not belong to the corporation.");
            }
            knLogger.debug(methodName,"subsAddlDetails: ",subsAddlDetails);
            persistDTO.setAllowedClientTypes(String.valueOf(subscProfile.getClientType()));
            if (subscProfile.getClientType() == DISPATCH_CLIENT) persistDTO.setWebDispatcherFlag(true);
            persistDTO.setSubsCorpId(subscProfile.getCorpId());
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setLicenseType(subscProfile.getLicenseType());
            persistDTO.setClientDBPassword(subscProfile.getClientPassowrd());
            persistDTO.setServiceAuthStatus(subscProfile.getServiceAuthStatus());
            persistDTO.setAllowedClientTypes(String.valueOf(subscProfile.getClientType()));
            if(subsAddlDetails != null){
                persistDTO.setOnBoardingEmailReqd(subsAddlDetails.getOnBoardingEmailReqd());
            }
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpSubscriberDTO();
            corpSubscriberDTO.setMdn(mdn);
            corpSubscriberDTO.setLastProfileUpdateTime(System.currentTimeMillis());
            corpSubsProvInfoUtil.updateSubsProfileLastUpdateTime(corpSubscriberDTO, xdmsHome, persisterTxn);
            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            Map<String, String> updateTimeMap = new HashMap<>();
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubscribersDirectory(mdnList, null, xdmsHome, persisterTxn);
            // Subscriber Config Profile Notification:
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
            docChgDTO.setDocUri(subsConfigDocUri);
            docChgDTO.setNewEtag(String.valueOf(corpSubscriberDTO.getLastProfileUpdateTime()));
            updateTimeMap.put(mdn, String.valueOf(corpSubscriberDTO.getLastProfileUpdateTime()));
            Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
            chgDocList.add(docChgDTO);
            //populating the XDM Directory DTO
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
            dirChgDTO.setPocHome(subscProfile.getPocHome());
            dirChgDTO.setPresenceHome(subscProfile.getPresenceHome());
            dirChgDTO.setDocChgDTO(chgDocList);
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirPrevEtag() : null);
            dirChgDTO.setDirNewEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirNewEtag() : null);
            dirChgDTO.setProtoVersion(subscProfile.getProtocolVersion());
            //getting UPM bit for sending profile Notification on temp pwd change
            boolean upmBit = KnGeneralUtil.getFeatureBitValue(subscProfile.getActiveFS2(), FEATURE_SET.USER_PROFILE_MGMT_BIT.value());
            knLogger.debug(methodName,"upmBit :",upmBit);
            if(upmBit){
                dirChgDTO.setNtfyOnAnyMDN(subscProfile.getUserProfileIndex()>0?1:0);
            }
            dirChgDTOs.add(dirChgDTO);
            //profile mdn
            String xcapRooturi = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);
            List<String> baseMdns = new ArrayList<String>();
            baseMdns.add(mdn);
            List<KnOPDirChgDTO> profileMdnDirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            Map<String, List<String>> profileBaseMdnMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(baseMdns, xdmsHome, persisterTxn);
            if (null != profileBaseMdnMap && !profileBaseMdnMap.isEmpty()) {
                List<String> profileMdns = profileBaseMdnMap.get(mdn);
                for (String profileMdn : profileMdns) {
                    KnOPDocChgDTO profileMdnDocChgDTO = new KnOPDocChgDTO();
                    profileMdnDocChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    String profileMdnSubsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(profileMdn);
                    profileMdnDocChgDTO.setDocUri(profileMdnSubsConfigDocUri);
                    profileMdnDocChgDTO.setNewEtag(String.valueOf(corpSubscriberDTO.getLastProfileUpdateTime()));

                    Collection<KnOPDocChgDTO> profileMdnDocChgList = new ArrayList<>();
                    profileMdnDocChgList.add(profileMdnDocChgDTO);
                    //populating Dir Document change DTO
                    KnOPDirChgDTO profileMdnDirChgDTO = new KnOPDirChgDTO();
                    profileMdnDirChgDTO.setXcapRootURI(xcapRooturi);
                    profileMdnDirChgDTO.setPocHome(subscProfile.getPocHome());
                    profileMdnDirChgDTO.setPresenceHome(subscProfile.getPresenceHome());
                    profileMdnDirChgDTO.setDocChgDTO(profileMdnDocChgList);
                    String profileMdnDirDocUri = genInfoUtil.generateDirDocUri(profileMdn);
                    profileMdnDirChgDTO.setDirUri(profileMdnDirDocUri);
                    profileMdnDirChgDTO.setDirPrevEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirPrevEtag() : PREV_DIR_ETAG);
                    profileMdnDirChgDTO.setDirNewEtag((etagMap.get(mdn) != null) ? etagMap.get(mdn).getDirNewEtag() : NEW_DIR_ETAG);
                    profileMdnDirChgDTO.setNtfyOnAnyMDN(NTFY_ON_ANY_MDN);
                    profileMdnDirChgDTO.setMdn(profileMdn);
                    profileMdnDirChgDTOs.add(profileMdnDirChgDTO);
                }
            }
            knLogger.debug(methodName, "dirChgDTOs - ", dirChgDTOs, " profileMdnDirChgDTOs -", profileMdnDirChgDTOs);
            respDTO.setDirChgDTOs(dirChgDTOs);
            respDTO.setProfileMdnDirChgDTOs(profileMdnDirChgDTOs);
            respDTO.setUpdateTimeMap(updateTimeMap);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in generateTempPassword  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in generateTempPassword ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO sendTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "sendTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            if(ipSubscriberInfoDTO.getAliasMdn() != null){
                List<String> aliasMdnList = new ArrayList<>();
                aliasMdnList.add(ipSubscriberInfoDTO.getAliasMdn());
                Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "aliasMdnList: - ", KnGDPRTemplate.mdnList(aliasMdnList), "aliasMdnMap: ", KnGDPRTemplate.mdnMap(aliasMdnMap));
                if(!aliasMdnMap.isEmpty()){
                    ipSubscriberInfoDTO.setMdn(aliasMdnMap.get(ipSubscriberInfoDTO.getAliasMdn()));
                }
                persistDTO.setAliasMdnList(aliasMdnList);
                persistDTO.setAliasMdnMap(aliasMdnMap);
            }
            String mdn = ipSubscriberInfoDTO.getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            KnPocSubsAddlInfoDTO subsAddlDetails = commonInfoUtil.getSubsAddlDetails(subscProfile.getMdn(),persisterTxn);
            knLogger.debug(methodName,"subsAddlDetails: ",subsAddlDetails);
            if(mdn != null){
                subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
                persistDTO.setAllowedClientTypes(String.valueOf(subscProfile.getClientType()));
                if(subscProfile.getClientType() == DISPATCH_CLIENT) persistDTO.setWebDispatcherFlag(true);
                persistDTO.setSubsCorpId(subscProfile.getCorpId());
                persistDTO.setLicenseType(subscProfile.getLicenseType());
            } else {
                persistDTO.setSubsCorpId(Integer.parseInt(corpId));
            }
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            if(subscProfile != null) persistDTO.setLicenseType(subscProfile.getLicenseType());
            persistDTO.setClientDBPassword(subscProfile.getClientPassowrd());
            persistDTO.setServiceAuthStatus(subscProfile.getServiceAuthStatus());
            persistDTO.setAllowedClientTypes(String.valueOf(subscProfile.getClientType()));
            if(subsAddlDetails != null){
                persistDTO.setOnBoardingEmailReqd(subsAddlDetails.getOnBoardingEmailReqd());
            }
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in sendTempPassword  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in sendTempPassword ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO getSubsEmergencyDetails(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubsEmergencyDetails(KnIPEmergencyInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipEmergencyInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String mdn = ipEmergencyInfoDTO.getMdn();
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
            KnCorpMcpttFeaturePersistDTO emergencyMcpttFeaturePersistDTO = new KnCorpMcpttFeaturePersistDTO();
            emergencyMcpttFeaturePersistDTO.setInputDTO(ipEmergencyInfoDTO);
            emergencyMcpttFeaturePersistDTO.setMdn(mdn);
            emergencyMcpttFeaturePersistDTO.setMcpttId(subsProfile.getMcpttId());
            emergencyMcpttFeaturePersistDTO.setMcpttCompliance(subsProfile.getMcpttCompliance());
            emergencyMcpttFeaturePersistDTO.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", emergencyMcpttFeaturePersistDTO);
            validatorFW.validate(emergencyMcpttFeaturePersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getSubsEmergencyAttributes  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getSubsEmergencyAttributes ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO addBulkGroupsToSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "addBulkGroupsToSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            List<String> groupIdList = new ArrayList<>();
            if(null  != ipSubscriberInfoDTO.getGroupIds()){
                groupIdList = ipSubscriberInfoDTO.getGroupIds().stream().map(String::valueOf).collect(Collectors.toList());
            }
            commonInfoUtil.validateCorpAndGroupParams(groupIdList, String.valueOf(corpId), xdmsHome, persisterTxn);
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                List<String> mdnList = new ArrayList<>();
                if (null != ipSubscriberInfoDTO.getToMdn()) {
                    mdnList.add(ipSubscriberInfoDTO.getToMdn());
                }
                if (null != ipSubscriberInfoDTO.getFromMdn()) {
                    mdnList.add(ipSubscriberInfoDTO.getFromMdn());
                }
                contactInfoUtil.checkValidHierarchySubs(mdnList, customParams, xdmsHome, persisterTxn);
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_CORP_SUBSCRIBER);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
                Collection<String> idListStr = (Collection<String>) customParams.get(com.kodiak.common.resources.KnConstants.IDLIST);
                Object idTypeObj = customParams.get(IDTYPE);
                int idTypeValues;
                if (idTypeObj instanceof Integer) {
                    idTypeValues = (Integer) idTypeObj;
                } else if (idTypeObj instanceof String) {
                    idTypeValues = Integer.parseInt((String) idTypeObj);
                } else {
                    throw new IllegalArgumentException("Unsupported type for IDTYPE: " + idTypeObj.getClass().getName());
                }
                if (idListStr != null) {
                    List<Integer> idListInt = (List<Integer>) convertStrToIntColl(idListStr);
                    commonInfoUtil.validateGroupParams(groupIdList, String.valueOf(corpId), idListInt, xdmsHome, idTypeValues, persisterTxn);
                }
            }
            //StandardGroup Related Config: Type1
            //DispatchGroup Related Config: Type2
            //Broadcast Group Related Config: Type3
            //Group Related one Config: Applicable for Standard, Dispatch, Broadcast: Type4
            //System Related Config: Type5
            //Subscriber Related Config: Type6
            KnCorpBulkGroupInfoPersistDTO persistDTO = new KnCorpBulkGroupInfoPersistDTO();
            String toMdn = ipSubscriberInfoDTO.getToMdn();
            String fromMdn = ipSubscriberInfoDTO.getFromMdn();
            List<String> mdnList = new ArrayList<>();
            mdnList.add(toMdn);
            int maxLrgGrpCountSystem = Integer.parseInt(genInfoUtil.retrieveRTXConfigValues(persisterTxn).get(NUM_OF_LG_SUPPORTED));
            KnSubsProfileDTO toMdnSubsProfile = commonInfoUtil.getProfileDetails(toMdn, PUBLIC_PROFILE, false, persisterTxn);
            persistDTO.setToMdnMcpttCompliance(toMdnSubsProfile.getMcpttCompliance());
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String maxGroupsPerCRIClient = paramNameValueMapCommon.get(MAX_GROUPS_PER_CRI_CLIENT);
            if(null != maxGroupsPerCRIClient){
                persistDTO.setMaxGrpsPerCriClient(Integer.parseInt(maxGroupsPerCRIClient));
            }

            KnIPCorpContactDTO corpContactDTO = new KnIPCorpContactDTO();
            corpContactDTO.setMdn(toMdn);
            corpContactDTO.setCorpId(Integer.parseInt(corpId));
            corpContactDTO.setBulkReq(true);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = groupInfoUtil.getSubsGroupList(corpContactDTO,
                    corpProfile.getMaxMemPerCorpGroup(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subsGroupList: ", subsGroupList);
            persistDTO.setGrpNotExistsForToMdn(subsGroupList.isEmpty()); //Type6
            Collection<Integer> toMdnGroupList = subsGroupList.stream().map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toList());
            Map<String, Integer> memberGroupCnt = groupInfoUtil.getMembersGroupCount(mdnList, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "memberGroupCnt: ", memberGroupCnt);
            Map<String, Integer> subscGrpCount = groupInfoUtil.getMembersGroupCount(mdnList, Integer.parseInt(corpId), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subscGrpCount --", subscGrpCount.size());
            persistDTO.setGroupMdnGrpCnt(subscGrpCount);//Type4
            Map<String, Integer> interOpGrpCnt = groupInfoUtil.getExternalSubscriberGroupCount(mdnList, Integer.parseInt(corpId), xdmsHome, persisterTxn);
            persistDTO.setInterOPSubscGrpCnt(interOpGrpCnt);
            KnSubsProfileDTO fromMdnSubsProfile = null;
            Collection<Integer> ipGroupIds = null;
            if(fromMdn != null){
                fromMdnSubsProfile = commonInfoUtil.getProfileDetails(fromMdn, PUBLIC_PROFILE, false, persisterTxn);
                knLogger.debug(methodName, "fromMdnSubsProfile --", fromMdnSubsProfile);
                KnIPCorpContactDTO corpContactDTOFromMdn = new KnIPCorpContactDTO();
                corpContactDTOFromMdn.setMdn(fromMdn);
                corpContactDTOFromMdn.setCorpId(Integer.parseInt(corpId));
                Collection<KnCorpGroupInfoPersistDTO> subsGroupListFromMdn = groupInfoUtil.getSubsGroupList(corpContactDTOFromMdn,
                        corpProfile.getMaxMemPerCorpGroup(), xdmsHome, persisterTxn);
                knLogger.debug(methodName, "subsGroupListFromMdn: ", subsGroupListFromMdn);
                ipGroupIds = subsGroupListFromMdn.stream().filter(grpDetails -> grpDetails.getGroupCreatedBy() != CREATED_BY.ABDG.value())
                        .map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toList());
            } else {
                ipGroupIds = new ArrayList<>(ipSubscriberInfoDTO.getGroupIds());
            }
            knLogger.debug(methodName, "ipGroupIds: ", ipGroupIds);
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(ipGroupIds, corpProfile.getXdmsHome(), persisterTxn);
            knLogger.debug(methodName, "groupDetailsMap: ", groupDetailsMap);
            Map<String, KnCorpSubscriberDTO> subsDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(mdnList, xdmsHome, persisterTxn);
            Collection<Integer> grpIdNotExists = new ArrayList<>();
            if(!groupDetailsMap.isEmpty()){
                grpIdNotExists = groupDetailsMap.entrySet().stream().filter(grpMap -> grpMap.getValue()
                        .getCorpId() != Integer.parseInt(corpId.trim())).map(Map.Entry::getKey).collect(Collectors.toList());
                grpIdNotExists.addAll(ipGroupIds.stream().filter(grp -> !groupDetailsMap.containsKey(grp)).collect(Collectors.toList()));
            } else {
                grpIdNotExists = ipGroupIds;
            }
            knLogger.debug(methodName, "subsDetailsMap: grpIdNotExists: ", KnGDPRTemplate.mapKeyMdn(subsDetailsMap), grpIdNotExists);
            Map<Integer, KnCorpGroupDTO> dbGroupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(subsGroupList.stream()
                    .map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toList()), corpProfile.getXdmsHome(), persisterTxn);
            knLogger.debug(methodName, "dbGroupDetailsMap: ", dbGroupDetailsMap.size());
            if(groupDetailsMap != null && !groupDetailsMap.isEmpty()){
                groupDetailsMap.putAll(dbGroupDetailsMap);
            }
            List<Integer> totalGrps = new ArrayList<>(groupDetailsMap.keySet());
            totalGrps.removeAll(grpIdNotExists); //FinalGroup which will supposed to go in modify cycle.
            knLogger.debug(methodName, "totalGrps2: ", totalGrps);
            grpIdNotExists.forEach(groupDetailsMap::remove);
            knLogger.debug(methodName, "groupDetailsMap.size: ", groupDetailsMap.size());
            LinkedHashMap<Integer, LinkedList<String>> dispatcherSubscriberMap = groupInfoUtil.getGroupDispatcherSubscriber(totalGrps, DISPATCHER, xdmsHome, persisterTxn);
            LinkedHashMap<Integer, LinkedList<String>> interopSubscriberMap = groupInfoUtil.getGroupDispatcherSubscriber(totalGrps, INTER_OP, xdmsHome, persisterTxn);
            LinkedHashMap<Integer, LinkedList<String>> groupMdnSubscriberMap = groupInfoUtil.getGroupMdnSubscriber(totalGrps,
                    SG_MDN_MEMBER_TYPE, xdmsHome, persisterTxn);
            LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchSubscriberMap = groupInfoUtil.getGroupMdnSubscriber(totalGrps,
                    SG_MDN_PATCH_MEMBER_TYPE, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "dispatcherSubscriberMap.size: ", dispatcherSubscriberMap.size(), "interopSubscriberMap.size: ", interopSubscriberMap.size(),
                    "groupMdnSubscriberMap.size: ", groupMdnSubscriberMap, "groupMdnPatchSubscriberMap.size: ", groupMdnPatchSubscriberMap.size());
            persistDTO.setGroupDetailsMap(groupDetailsMap);
            persistDTO.setGroupIdNotExists(grpIdNotExists);//Type6 // Extra Validation 2: Groups not exists(Either in requested corp or in DB). todo
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setCorpId(toMdnSubsProfile.getCorpId());
            persistDTO.setMaxMemPerLargeGroup(corpProfile.getMaxMemPerLrgGrp());//Type1+Type2: Max Member Per Large Group(Standard, Dispatch)
            persistDTO.setLargeGroupSupported(corpProfile.getLargeGrpSupported());//Type4:
            persistDTO.setMaxMemPerLargeBCGroup(corpProfile.getMaxMemPerLrgBGrp());//Type4 : Max Member Per Large Broadcast group
            persistDTO.setInputMdnsProfile(subsDetailsMap);//Type6 // Validation Needed todo
            persistDTO.setMaxNumberOfMembers(corpProfile.getMaxMemPerCorpGroup());//Type1
            persistDTO.setMultipleDispatcherAllowed(KnConstants.TRUE);//Type2
            persistDTO.setMaxDispatchGroup(corpProfile.getMaxDispatchGroup());//Type2: Max Dispatch Group Allowed
            persistDTO.setMaxSubscriberPerDispatchGroup(corpProfile.getMaxMembersPerDispatchGroup());//Type2: Max Member Per Dispatch Group
            persistDTO.setMaxDispatchMembersPerDispatchGroup(corpProfile.getMaxDispatchMembersPerDispatchGroup()); // Max Dispatch Member Per Dispatch Group
            persistDTO.setDispatchEnabled(corpProfile.getDispatchEnabled());//Type2
            persistDTO.setSubsGroupsCount(memberGroupCnt);//Type1+Type4: Subscriber's Group Count.
            persistDTO.setMaxGroupsPerMemberCount(corpProfile.getMaxGroupsPerSubsc());//Type1+Type4: Max Groups allowed per subscriber.
            persistDTO.setMaxAllowedMemPerBCG(corpProfile.getMaxMemPerBCGrp());//Type3: Max Member allowed per broadcast group.
            persistDTO.setSysBCGFeatue(Boolean.TRUE);//Type2
            persistDTO.setMaxSGPerGrp(corpProfile.getMaxSGPerGrp());//Type4
            persistDTO.setMaxSGMdnPatchPerGroup(corpProfile.getMaxSGPatchPerGrp());//Type4
            persistDTO.setMaxLargeGrpSystem(maxLrgGrpCountSystem);
            persistDTO.setAllowedClientTypes(String.valueOf(toMdnSubsProfile.getClientType()));
            persistDTO.setDispatcherListCount(dispatcherSubscriberMap);
            persistDTO.setInteropMdnListCount(interopSubscriberMap);
            persistDTO.setGroupMdnListCount(groupMdnSubscriberMap);
            persistDTO.setGroupMdnPatchListCount(groupMdnPatchSubscriberMap);
            persistDTO.setMaxLargeBCGrpCorp(corpProfile.getMaxLrgBGrpPerCorp());
            persistDTO.setMaxLargeGrpCorp(corpProfile.getMaxLrgGrpPerCorp());

            // 1. toMdn(Subscriber can be part of N Groups: Validation)
            // 2. Max Member in a group after adding toMdn: Separate for Standard, Dispatch, Broadcast.
            Map<Integer, Integer> groupIdTypesMap = groupInfoUtil.getValidGrpInCorp(totalGrps, Integer.parseInt(corpId),
                    xdmsHome, persisterTxn);
            Collection<Integer> standardGrpList = new ArrayList<>();
            Collection<Integer> dispatchGrpList = new ArrayList<>();
            Collection<Integer> broadCastGrpList = new ArrayList<>();
            groupIdTypesMap.forEach((GrpId, GrpType) -> {
                if (GrpType == STANDARD_GROUP){
                    standardGrpList.add(GrpId);
                } else if(GrpType == DISPATCH_GROUP){
                    dispatchGrpList.add(GrpId);
                } else {
                    broadCastGrpList.add(GrpId);
                }
            });
            persistDTO.setGrpIds(standardGrpList);
            knLogger.debug(methodName, "standardGrpList - ", standardGrpList, "dispatchGrpList: ", dispatchGrpList, "broadCastGrpList: ", broadCastGrpList);
            corpSubsProvInfoUtil.boundaryConditionValidationPopulation(persistDTO, groupDetailsMap, toMdnGroupList);
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            //Business Logic:
            Map<String, String> mdnExistenceInGroup = groupInfoUtil.selectGroupMemberForGroupIds(totalGrps, toMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "mdnExistenceInGroup: ", KnGDPRTemplate.mdnMap(mdnExistenceInGroup));
            Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(totalGrps, xdmsHome, persisterTxn);
            Map<Integer, KnCorpGroupDTO> groupListLrgExcluded = new HashMap<>();
            Collection<KnCorpSubscriberDTO> groupPrivateList = new ArrayList<>();
            groupDetailsMap.forEach((key, value) -> {
                if(!mdnExistenceInGroup.containsKey(String.valueOf(key))){
                    if(!value.isLargeGroup()){
                        groupListLrgExcluded.put(key, value);
                    }
                    groupPrivateList.add(new KnCorpSubscriberDTO(toMdn, value.getGrpMemListId(), Integer.parseInt(corpId)));
                }
            });
            knLogger.debug(methodName, "groupListLrgExcluded: ", groupListLrgExcluded);
            Set<String> mdnListForDirectory = new HashSet<>();
            Set<String> mdnListForLrgGrp = new HashSet<>();
            groupDistList.forEach((grpId, grpMems) -> {
                mdnListForDirectory.addAll(new ArrayList<>(grpMems));
                mdnListForDirectory.add(toMdn);
                if(!groupListLrgExcluded.containsKey(grpId)){
                    mdnListForLrgGrp.addAll(new ArrayList<>(grpMems));
                }
            });
            contactInfoUtil.addPrivateContactList(groupPrivateList, xdmsHome, persisterTxn);
            Map<Integer, String> groupDistPopulation = new HashMap<>();
            Map<Integer, KnCorpContactDTO> groupMemberInsertList = new HashMap<Integer, KnCorpContactDTO>();
            totalGrps.forEach(grp -> {
                if(!mdnExistenceInGroup.containsKey(String.valueOf(grp))){
                    KnCorpContactDTO grpMemDto = new KnCorpContactDTO();
                    if(!broadCastGrpList.contains(grp)){
                        groupDistPopulation.put(grp, toMdn);
                    }
                    grpMemDto.setMdn(toMdn);
                    grpMemDto.setCorpId(Integer.parseInt(ipSubscriberInfoDTO.getCorpId()));
                    if(toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value()){
                        grpMemDto.setSupervisory(DISPATCHER);
                    } else if(toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.POC_DONOR_RADIO.value()){
                        grpMemDto.setSupervisory(INTER_OP);
                    } else if(toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.GROUPMDN.value()){
                        grpMemDto.setMemberType(SG_MDN_MEMBER_TYPE);
                    } else if(toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()){
                        grpMemDto.setMemberType(SG_MDN_PATCH_MEMBER_TYPE);
                    }
                    /*if(toMdnSubsProfile.getClientMajorVersion()>=com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18  && toMdnSubsProfile.getClientType()>=KnConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() && toMdnSubsProfile.getClientType()<=KnConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                    	grpMemDto.setIsAffiliationEnabled(ENABLED);
                    }else {
                    	grpMemDto.setIsAffiliationEnabled(DISABLED);
                    }*/

                    boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(toMdnSubsProfile.getActiveFS2(), FEATURE_SET.AFFILIATIONFEATURE.value());
                    if (toMdnSubsProfile.getClientMajorVersion() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                            && bitEnabled && checkClientType(toMdnSubsProfile.getClientType(), toMdnSubsProfile.getMcpttCompliance())) {
                        grpMemDto.setIsAffiliationEnabled(ENABLED);
                    } else {
                        grpMemDto.setIsAffiliationEnabled(DISABLED);
                    }

                    groupMemberInsertList.put(grp, grpMemDto);
                }
            });
            groupInfoUtil.insertIntoCorpGrpDistInfo(groupDistPopulation, xdmsHome, persisterTxn);
            Map<Integer, Integer> grpIdNormalToLrgTran = persistDTO.getGrpIdNormalToLrgTran();
            Map<Integer, Integer> groupEtag = groupInfoUtil.updateGroupListEtag(groupDetailsMap.entrySet().stream()
                    .filter(map -> !mdnExistenceInGroup.containsKey(String.valueOf(map.getKey()))).collect(Collectors.toMap(Map.Entry::getKey,
                    map -> (map.getValue().isLargeGroup() == TRUE ? ENABLED : (grpIdNormalToLrgTran.keySet().contains(map.getKey())
                            ? ENABLED : DISABLED)))), xdmsHome, persisterTxn);
            groupInfoUtil.insertBulkIntoCorpGroupMemberList(groupMemberInsertList, xdmsHome, persisterTxn);
            Collection<Integer> sublistIdListLrgStand = new ArrayList<>();
            Collection<Integer> sublistIdListStand = new ArrayList<>();
            Collection<Integer> sublistIdListLrgDisp = new ArrayList<>();
            Collection<Integer> sublistIdListDisp = new ArrayList<>();
            Collection<Integer> sublistIdListLrgBC = new ArrayList<>();
            Collection<Integer> sublistIdListBC = new ArrayList<>();
            groupDetailsMap.forEach((key, value) -> {
                if(!mdnExistenceInGroup.containsKey(String.valueOf(key))){
                    if (value.isLargeGroup()) {
                        if (value.getGroupType() == STANDARD_GROUP) {
                            sublistIdListLrgStand.add(value.getGrpMemListId());
                        } else if (value.getGroupType() == DISPATCH_GROUP) {
                            sublistIdListLrgDisp.add(value.getGrpMemListId());
                        } else {
                            sublistIdListLrgBC.add(value.getGrpMemListId());
                        }
                    } else {
                        if (value.getGroupType() == STANDARD_GROUP) {
                            sublistIdListStand.add(value.getGrpMemListId());
                        } else if (value.getGroupType() == DISPATCH_GROUP) {
                            sublistIdListDisp.add(value.getGrpMemListId());
                        } else {
                            sublistIdListBC.add(value.getGrpMemListId());
                        }
                    }
                }
            });
            knLogger.debug(methodName, "sublistIdListLrgStand: ", sublistIdListLrgStand, "sublistIdListLrgDisp: ",
                    sublistIdListLrgDisp, "sublistIdListLrgBC: ", sublistIdListLrgBC, "sublistIdListStand: ", sublistIdListStand,
                    "sublistIdListDisp: ", sublistIdListDisp, "sublistIdListBC: ", sublistIdListBC);
            if(!sublistIdListStand.isEmpty()){
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListStand, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        corpProfile.getMaxMemPerLrgGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            }
            if(!sublistIdListDisp.isEmpty()){
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListDisp, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, corpProfile.getMaxMembersPerDispatchGroup(), xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            }
            if(!sublistIdListBC.isEmpty()){
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListBC, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        corpProfile.getMaxMemPerBCGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            }
            if(!sublistIdListLrgStand.isEmpty()){
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListLrgStand, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        corpProfile.getMaxMemPerLrgGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            }
            if(!sublistIdListLrgDisp.isEmpty()){
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListLrgDisp, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, corpProfile.getMaxMemPerLrgGrp(), xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            }
            if(!sublistIdListLrgBC.isEmpty()){
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListLrgBC, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        corpProfile.getMaxMemPerLrgBGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            }
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubscribersDirectory(mdnListForDirectory, null, xdmsHome, persisterTxn);
            Map<String, KnOPDirChgDTO> finalEtagMap = etagMap;
            mdnListForLrgGrp.forEach(lrgGrpMdn -> {
                if(!mdnListForDirectory.contains(lrgGrpMdn)){
                    finalEtagMap.remove(lrgGrpMdn);
                }
            });
            broadCastGrpList.forEach(groupEtag::remove);
            //removing the existing group member , As existing member getting directory notify
            List<String> exisitngGroupMembers =new ArrayList<>();
            for(Collection<String> grpMembers:groupDistList.values()) {
                exisitngGroupMembers.addAll(grpMembers);
            }
            etagMap.keySet().removeIf(e -> exisitngGroupMembers.contains(e));

            etagMap = commonInfoUtil.formSubscriberNotification(toMdn, groupEtag, DOC_CHANGE_TYPE.REPLACE.value(), etagMap);
            respDTO.setChangeLogMap(etagMap);
            respDTO.setMdnCorpId(toMdnSubsProfile.getCorpId());
            respDTO.setGroupIds(groupEtag.keySet().stream().filter(integer -> !mdnExistenceInGroup.containsKey(String.valueOf(integer))).collect(Collectors.toList()));
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
            //If the corporate is already linked i.e linked gateway key is not null get the refernce id
            if (corpProfile.getLinkedGwKey() != null && (toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.GROUPMDN.value()
                    || toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.SGMDNPATCH.value())) {
                KnCorpGWLinkedAccountInfoDTO accountInfo = commonInfoUtil.getCorporateLinkedAccountInfo(corpProfile.getLinkedGwKey(), xdmsHome, persisterTxn);
                Map<String, String> asyncInput = new HashMap<>();
                asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, xdmsHome);
                asyncInput.put(KnDbSyncFwConstants.NNI_REF_ID, accountInfo.getNniRefId());
                knLogger.debug(methodName, " Async inputs", asyncInput);
                KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
                int serviceType = KnDbSyncFwConstants.EXECUTOR.ETAG_UPDATE_NNI.value();
                collector.collect(serviceType, asyncInput);
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in addBulkGroupsToSubscriber  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in addBulkGroupsToSubscriber ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    /**
     * This method is used to clone bulk groups to a subscriber.
     * It performs several operations such as fetching profile details, validating input data,
     * updating group distribution information, updating group member list, updating group list Etag,
     * updating sublist subscriber count, and updating subscribers directory.
     *
     * @param ipSubscriberInfoDTO This is the first parameter to cloneBulkGroupsToSubscriberDataPrepration method which is an instance of KnIPSubscriberInfoDTO.
     * @param persisterTxn        This is the second parameter to cloneBulkGroupsToSubscriberDataPrepration method which is an instance of KnPersisterTxn.
     * @return KnCorpResponseDTO This returns an instance of KnCorpResponseDTO containing the response data.
     */
    @Override
    public KnBulkGroupCloningDTO cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnBulkGroupCloningDTO bulkGroupCloningDTO = new KnBulkGroupCloningDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            //StandardGroup Related Config: Type1
            //DispatchGroup Related Config: Type2
            //Broadcast Group Related Config: Type3
            //Group Related one Config: Applicable for Standard, Dispatch, Broadcast: Type4
            //System Related Config: Type5
            //Subscriber Related Config: Type6
            KnCorpBulkGroupInfoPersistDTO persistDTO = new KnCorpBulkGroupInfoPersistDTO();
            String toMdn = ipSubscriberInfoDTO.getToMdn();
            String fromMdn = ipSubscriberInfoDTO.getFromMdn();
            List<String> mdnList = new ArrayList<>();
            mdnList.add(toMdn);
            int maxLrgGrpCountSystem = Integer.parseInt(genInfoUtil.retrieveRTXConfigValues(persisterTxn).get(NUM_OF_LG_SUPPORTED));
            KnSubsProfileDTO toMdnSubsProfile = commonInfoUtil.getProfileDetails(toMdn, PUBLIC_PROFILE, false, persisterTxn);
            persistDTO.setToMdnMcpttCompliance(toMdnSubsProfile.getMcpttCompliance());
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String maxGroupsPerCRIClient = paramNameValueMapCommon.get(MAX_GROUPS_PER_CRI_CLIENT);
            if (null != maxGroupsPerCRIClient) {
                persistDTO.setMaxGrpsPerCriClient(Integer.parseInt(maxGroupsPerCRIClient));
            }

            //Get subscriber group list for toMdn
            KnIPCorpContactDTO corpContactDTO = new KnIPCorpContactDTO();
            corpContactDTO.setMdn(toMdn);
            corpContactDTO.setCorpId(Integer.parseInt(corpId));
            corpContactDTO.setBulkReq(true);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = groupInfoUtil.getSubsGroupList(corpContactDTO,
                    corpProfile.getMaxMemPerCorpGroup(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subsGroupList: ", subsGroupList);
            //persistDTO.setGrpNotExistsForToMdn(subsGroupList.isEmpty()); //Type6
            Collection<Integer> toMdnGroupList = subsGroupList.stream().map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toList());
            Map<String, Integer> memberGroupCnt = groupInfoUtil.getMembersGroupCount(mdnList, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "memberGroupCnt: ", memberGroupCnt);
            Map<String, Integer> subscGrpCount = groupInfoUtil.getMembersGroupCount(mdnList, Integer.parseInt(corpId), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subscGrpCount --", subscGrpCount.size());
            persistDTO.setGroupMdnGrpCnt(subscGrpCount);//Type4
            Map<String, Integer> interOpGrpCnt = groupInfoUtil.getExternalSubscriberGroupCount(mdnList, Integer.parseInt(corpId), xdmsHome, persisterTxn);
            persistDTO.setInterOPSubscGrpCnt(interOpGrpCnt);
            KnSubsProfileDTO fromMdnSubsProfile = null;
            Collection<Integer> sourceIpGroupIds = null;
            Collection<Integer> targetIpGroupIds = null;
            if (fromMdn != null) {
                fromMdnSubsProfile = commonInfoUtil.getProfileDetails(fromMdn, PUBLIC_PROFILE, false, persisterTxn);
                knLogger.debug(methodName, "fromMdnSubsProfile --", fromMdnSubsProfile);
                KnIPCorpContactDTO corpContactDTOFromMdn = new KnIPCorpContactDTO();
                corpContactDTOFromMdn.setMdn(fromMdn);
                corpContactDTOFromMdn.setCorpId(Integer.parseInt(corpId));
                corpContactDTOFromMdn.setBulkReq(true);
                Collection<KnCorpGroupInfoPersistDTO> subsGroupListFromMdn = groupInfoUtil.getSubsGroupList(corpContactDTOFromMdn,
                        corpProfile.getMaxMemPerCorpGroup(), xdmsHome, persisterTxn);
                knLogger.debug(methodName, "subsGroupListFromMdn: ", subsGroupListFromMdn);
                sourceIpGroupIds = subsGroupListFromMdn.stream().filter(grpDetails -> grpDetails.getGroupCreatedBy() != CREATED_BY.ABDG.value())
                        .map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toList());
            }

            if (toMdn != null) {
                fromMdnSubsProfile = commonInfoUtil.getProfileDetails(toMdn, PUBLIC_PROFILE, false, persisterTxn);
                knLogger.debug(methodName, "fromMdnSubsProfile --", fromMdnSubsProfile);
                KnIPCorpContactDTO corpContactDTOFromMdn = new KnIPCorpContactDTO();
                corpContactDTOFromMdn.setMdn(toMdn);
                corpContactDTOFromMdn.setCorpId(Integer.parseInt(corpId));
                corpContactDTOFromMdn.setBulkReq(true);
                Collection<KnCorpGroupInfoPersistDTO> subsGroupListFromMdn = groupInfoUtil.getSubsGroupList(corpContactDTOFromMdn,
                        corpProfile.getMaxMemPerCorpGroup(), xdmsHome, persisterTxn);
                knLogger.debug(methodName, "subsGroupListFromMdn: ", subsGroupListFromMdn);
                targetIpGroupIds = subsGroupListFromMdn.stream().filter(grpDetails -> grpDetails.getGroupCreatedBy() != CREATED_BY.ABDG.value())
                        .map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toList());
            }
            Collection<Integer> finalGroupToBeRemovedFromTargetMdn = new ArrayList<>(targetIpGroupIds);
            Collection<Integer> finalGroupToBeUpdateMemberProperties = new ArrayList<>(targetIpGroupIds);
            Collection<Integer> finalGroupToBeClonedToTargetMdn = new ArrayList<>(sourceIpGroupIds);
            finalGroupToBeRemovedFromTargetMdn.removeAll(sourceIpGroupIds);
            finalGroupToBeClonedToTargetMdn.removeAll(targetIpGroupIds);
            finalGroupToBeUpdateMemberProperties.removeAll(finalGroupToBeRemovedFromTargetMdn);
            //knLogger.debug(methodName, "ipGroupIds: ", ipGroupIds);
            knLogger.debug(methodName, "sourceIpGroupIds: ", sourceIpGroupIds);
            knLogger.debug(methodName, "targetIpGroupIds: ", targetIpGroupIds);
            knLogger.debug(methodName, "finalGroupToBeRemovedFromTargetMdn: ", finalGroupToBeRemovedFromTargetMdn);
            knLogger.debug(methodName, "finalGroupToBeClonedToTargetMdn: ", finalGroupToBeClonedToTargetMdn);
            knLogger.debug(methodName, "finalGroupToBeUpdateMemberProperties: ", finalGroupToBeUpdateMemberProperties);
            List<Integer> allGroupsInvolved = new ArrayList<>(sourceIpGroupIds);
            allGroupsInvolved.addAll(targetIpGroupIds);

            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(allGroupsInvolved, corpProfile.getXdmsHome(), persisterTxn);
            Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeClonedToTargetMdn = groupDetailsMap.entrySet().stream()
                    .filter(map -> finalGroupToBeClonedToTargetMdn.contains(map.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn = groupDetailsMap.entrySet().stream()
                    .filter(map -> finalGroupToBeRemovedFromTargetMdn.contains(map.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            knLogger.debug(methodName, "groupDetailsMap: ", groupDetailsMap);
            knLogger.debug(methodName, "groupDetailsMapOfFinalGroupToBeClonedToTargetMdn: ", groupDetailsMapOfFinalGroupToBeClonedToTargetMdn);
            knLogger.debug(methodName, "groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn: ", groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn);

            //toMdn Subscriber Details Map
            Map<String, KnCorpSubscriberDTO> subsDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(mdnList, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subsDetailsMap: : ", KnGDPRTemplate.mapKeyMdn(subsDetailsMap));

            LinkedHashMap<Integer, LinkedList<String>> dispatcherSubscriberMap = groupInfoUtil.getGroupDispatcherSubscriber(finalGroupToBeClonedToTargetMdn, DISPATCHER, xdmsHome, persisterTxn);
            LinkedHashMap<Integer, LinkedList<String>> interopSubscriberMap = groupInfoUtil.getGroupDispatcherSubscriber(finalGroupToBeClonedToTargetMdn, INTER_OP, xdmsHome, persisterTxn);
            LinkedHashMap<Integer, LinkedList<String>> groupMdnSubscriberMap = groupInfoUtil.getGroupMdnSubscriber(finalGroupToBeClonedToTargetMdn,
                    SG_MDN_MEMBER_TYPE, xdmsHome, persisterTxn);
            LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchSubscriberMap = groupInfoUtil.getGroupMdnSubscriber(finalGroupToBeClonedToTargetMdn,
                    SG_MDN_PATCH_MEMBER_TYPE, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "dispatcherSubscriberMap.size: ", dispatcherSubscriberMap.size(), "interopSubscriberMap.size: ", interopSubscriberMap.size(),
                    "groupMdnSubscriberMap.size: ", groupMdnSubscriberMap, "groupMdnPatchSubscriberMap.size: ", groupMdnPatchSubscriberMap.size());

            persistDTO.setGroupDetailsMap(groupDetailsMapOfFinalGroupToBeClonedToTargetMdn);
            //persistDTO.setGroupIdNotExists(grpIdNotExists);//Type6 // Extra Validation 2: Groups not exists(Either in requested corp or in DB). todo
            //persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setCorpId(toMdnSubsProfile.getCorpId());
            persistDTO.setMaxMemPerLargeGroup(corpProfile.getMaxMemPerLrgGrp());//Type1+Type2: Max Member Per Large Group(Standard, Dispatch)
            persistDTO.setLargeGroupSupported(corpProfile.getLargeGrpSupported());//Type4:
            persistDTO.setMaxMemPerLargeBCGroup(corpProfile.getMaxMemPerLrgBGrp());//Type4 : Max Member Per Large Broadcast group
            persistDTO.setInputMdnsProfile(subsDetailsMap);//Type6 // Validation Needed todo
            persistDTO.setMaxNumberOfMembers(corpProfile.getMaxMemPerCorpGroup());//Type1
            persistDTO.setMultipleDispatcherAllowed(KnConstants.TRUE);//Type2
            persistDTO.setMaxDispatchGroup(corpProfile.getMaxDispatchGroup());//Type2: Max Dispatch Group Allowed
            persistDTO.setMaxSubscriberPerDispatchGroup(corpProfile.getMaxMembersPerDispatchGroup());//Type2: Max Member Per Dispatch Group
            persistDTO.setMaxDispatchMembersPerDispatchGroup(corpProfile.getMaxDispatchMembersPerDispatchGroup()); // Max Dispatch Member Per Dispatch Group
            persistDTO.setDispatchEnabled(corpProfile.getDispatchEnabled());//Type2
            persistDTO.setSubsGroupsCount(memberGroupCnt);//Type1+Type4: Subscriber's Group Count.
            //persistDTO.setMaxGroupsPerMemberCount(corpProfile.getMaxGroupsPerSubsc());//Type1+Type4: Max Groups allowed per subscriber.
            persistDTO.setMaxAllowedMemPerBCG(corpProfile.getMaxMemPerBCGrp());//Type3: Max Member allowed per broadcast group.
            persistDTO.setSysBCGFeatue(Boolean.TRUE);//Type2
            persistDTO.setMaxSGPerGrp(corpProfile.getMaxSGPerGrp());//Type4
            persistDTO.setMaxSGMdnPatchPerGroup(corpProfile.getMaxSGPatchPerGrp());//Type4
            persistDTO.setMaxLargeGrpSystem(maxLrgGrpCountSystem);
            persistDTO.setAllowedClientTypes(String.valueOf(toMdnSubsProfile.getClientType()));
            persistDTO.setDispatcherListCount(dispatcherSubscriberMap);
            persistDTO.setInteropMdnListCount(interopSubscriberMap);
            persistDTO.setGroupMdnListCount(groupMdnSubscriberMap);
            persistDTO.setGroupMdnPatchListCount(groupMdnPatchSubscriberMap);
            persistDTO.setMaxLargeBCGrpCorp(corpProfile.getMaxLrgBGrpPerCorp());
            persistDTO.setMaxLargeGrpCorp(corpProfile.getMaxLrgGrpPerCorp());

            // 1. toMdn(Subscriber can be part of N Groups: Validation)
            // 2. Max Member in a group after adding toMdn: Separate for Standard, Dispatch, Broadcast.
            Map<Integer, Integer> groupIdTypesMap = groupInfoUtil.getValidGrpInCorp(new ArrayList<>(finalGroupToBeRemovedFromTargetMdn), Integer.parseInt(corpId),
                    xdmsHome, persisterTxn);
            Collection<Integer> standardGrpList = new ArrayList<>();
            Collection<Integer> dispatchGrpList = new ArrayList<>();
            Collection<Integer> broadCastGrpList = new ArrayList<>();
            groupIdTypesMap.forEach((GrpId, GrpType) -> {
                if (GrpType == STANDARD_GROUP) {
                    standardGrpList.add(GrpId);
                } else if (GrpType == DISPATCH_GROUP) {
                    dispatchGrpList.add(GrpId);
                } else {
                    broadCastGrpList.add(GrpId);
                }
            });
            persistDTO.setGrpIds(standardGrpList);
            knLogger.debug(methodName, "standardGrpList - ", standardGrpList, "dispatchGrpList: ", dispatchGrpList, "broadCastGrpList: ", broadCastGrpList);
            corpSubsProvInfoUtil.boundaryConditionValidationPopulation(persistDTO, groupDetailsMapOfFinalGroupToBeClonedToTargetMdn, toMdnGroupList);
            /*knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");*/
            //Business Logic:
            Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(finalGroupToBeClonedToTargetMdn, xdmsHome, persisterTxn);
            Map<Integer, KnCorpGroupDTO> groupListLrgExcluded = new HashMap<>();
            Collection<KnCorpSubscriberDTO> groupPrivateList = new ArrayList<>();
            groupDetailsMapOfFinalGroupToBeClonedToTargetMdn.forEach((key, value) -> {
                if (!value.isLargeGroup()) {
                    groupListLrgExcluded.put(key, value);
                }
                groupPrivateList.add(new KnCorpSubscriberDTO(toMdn, value.getGrpMemListId(), Integer.parseInt(corpId)));
            });
            knLogger.debug(methodName, "groupListLrgExcluded: ", groupListLrgExcluded);
            Set<String> mdnListForDirectory = new HashSet<>();
            Set<String> mdnListForLrgGrp = new HashSet<>();
            groupDistList.forEach((grpId, grpMems) -> {
                mdnListForDirectory.addAll(new ArrayList<>(grpMems));
                mdnListForDirectory.add(toMdn);
                if (!groupListLrgExcluded.containsKey(grpId)) {
                    mdnListForLrgGrp.addAll(new ArrayList<>(grpMems));
                }
            });
            //contactInfoUtil.addPrivateContactList(groupPrivateList, xdmsHome, persisterTxn);
            Map<Integer, String> groupDistPopulationToAddToTarget = new HashMap<>();
            Map<Integer, List<String>> groupDistPopulationToRemoveFromTarget = new HashMap<>();
            Map<Integer, KnCorpContactDTO> groupMemberInsertList = new HashMap<>();
            LinkedHashMap<Integer, LinkedList<String>> groupMemberDeleteList = new LinkedHashMap<>();
            Map<Integer, KnCorpGroupMemberDTO> bulkGroupMembersListMap = groupInfoUtil.getBulkGroupMembersListMap(allGroupsInvolved, fromMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "bulkGroupMembersListMap: ", bulkGroupMembersListMap);
            finalGroupToBeClonedToTargetMdn.forEach(grp -> {
                KnCorpContactDTO grpMemDto = new KnCorpContactDTO();
                if (!broadCastGrpList.contains(grp)) {
                    groupDistPopulationToAddToTarget.put(grp, toMdn);
                }
                grpMemDto.setMdn(toMdn);
                grpMemDto.setCorpId(Integer.parseInt(ipSubscriberInfoDTO.getCorpId()));
                if (toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value()) {
                    grpMemDto.setSupervisory(DISPATCHER);
                }
                //add member properties if bit 2 enabled along with 1
                int cloningBitSet = Integer.parseInt(ipSubscriberInfoDTO.getCloningBitset());
                if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.GROUP_MEMBER_PROP.value())) {
                    addMemberProperties(grp, bulkGroupMembersListMap, grpMemDto, toMdnSubsProfile);
                }
                groupMemberInsertList.put(grp, grpMemDto);
            });

            Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap = new HashMap<>();

            finalGroupToBeUpdateMemberProperties.forEach(grp -> {
                Collection<KnCorpGroupMemberDTO> grpMemDtoList = new ArrayList<>();
                KnCorpGroupMemberDTO grpMemDto = new KnCorpGroupMemberDTO();
                grpMemDto.setMdn(toMdn);
                grpMemDto.setCorpId(Integer.parseInt(ipSubscriberInfoDTO.getCorpId()));
                if (toMdnSubsProfile.getClientType() == SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value()) {
                    grpMemDto.setSupervisory(DISPATCHER);
                }
                //add member properties if bit 2 enabled along with 1
                int cloningBitSet = Integer.parseInt(ipSubscriberInfoDTO.getCloningBitset());
                if (commonInfoUtil.isBitPositionSet(cloningBitSet, CLONING_BIT_POSITION.GROUP_MEMBER_PROP.value())){
                    addMemberProperties(grp, bulkGroupMembersListMap, grpMemDto, toMdnSubsProfile);
                }
                grpMemDtoList.add(grpMemDto);
                supervisorMemberListMap.put(grp, grpMemDtoList);
            });


            //remove toMdn from the group member list and privateContactList for the groups which are getting removed from toMdn
            finalGroupToBeRemovedFromTargetMdn.forEach(grp -> {
                groupDistPopulationToRemoveFromTarget.put(grp, Collections.singletonList(toMdn));
                LinkedList<String> mdnListToRemove = new LinkedList<String>();
                mdnListToRemove.add(toMdn);
                groupMemberDeleteList.put(grp, mdnListToRemove);
            });

            bulkGroupCloningDTO.setGroupPrivateList(groupPrivateList);
            bulkGroupCloningDTO.setGroupDistPopulationToAddToTarget(groupDistPopulationToAddToTarget);
            bulkGroupCloningDTO.setGroupMemberInsertList(groupMemberInsertList);
            bulkGroupCloningDTO.setGroupDistPopulationToRemoveFromTarget(groupDistPopulationToRemoveFromTarget);
            bulkGroupCloningDTO.setGroupMemberDeleteList(groupMemberDeleteList);
            bulkGroupCloningDTO.setSupervisorMemberListMap(supervisorMemberListMap);
            bulkGroupCloningDTO.setGroupDetailsMapOfFinalGroupToBeClonedToTargetMdn(groupDetailsMapOfFinalGroupToBeClonedToTargetMdn);
            bulkGroupCloningDTO.setGroupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn(groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn);
            bulkGroupCloningDTO.setCorpProfile(corpProfile);
            bulkGroupCloningDTO.setMdnListForDirectory(mdnListForDirectory);
            bulkGroupCloningDTO.setMdnListForLrgGrp(mdnListForLrgGrp);
            bulkGroupCloningDTO.setFinalGroupToBeRemovedFromTargetMdn(finalGroupToBeRemovedFromTargetMdn);
            bulkGroupCloningDTO.setBroadCastGrpList(broadCastGrpList);
            bulkGroupCloningDTO.setGroupDistList(groupDistList);
            bulkGroupCloningDTO.setToMdnSubsProfile(toMdnSubsProfile);
            bulkGroupCloningDTO.setPersistDTO(persistDTO);
            bulkGroupCloningDTO.setXdmsHome(xdmsHome);
            bulkGroupCloningDTO.setToMdn(toMdn);
            bulkGroupCloningDTO.setGroupDetailsMap(groupDetailsMap);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in addBulkGroupsToSubscriber  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in addBulkGroupsToSubscriber ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.exit(methodName);
        return bulkGroupCloningDTO;
    }

    @Override
    public KnCorpResponseDTO cloneBulkGroupsToSubscriberProcessing(KnBulkGroupCloningDTO bulkGroupCloningDTO, KnPersisterTxn persisterTxn) {
        String methodName = "cloneBulkGroupsToSubscriberProcessing(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        try {
            responseDTO = finalOperation(bulkGroupCloningDTO, persisterTxn);
        } catch (KnCorpBOException e) {
            populate(responseDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in addBulkGroupsToSubscriber  ", e);
        } catch (Exception e) {
            populate(responseDTO, e);
            knLogger.error(methodName, "Exception occured in addBulkGroupsToSubscriber ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return responseDTO;
    }

    private KnCorpResponseDTO finalOperation(KnBulkGroupCloningDTO bulkGroupCloningDTO, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "finalOperation(KnBulkGroupCloningDTO bulkGroupCloningDTO, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        modifyCloneOperationProcessing(persisterTxn, bulkGroupCloningDTO.getGroupPrivateList(), bulkGroupCloningDTO.getXdmsHome(), bulkGroupCloningDTO.getFinalGroupToBeRemovedFromTargetMdn(), bulkGroupCloningDTO.getToMdn(), bulkGroupCloningDTO.getGroupDetailsMap(),
                bulkGroupCloningDTO.getGroupDistPopulationToAddToTarget(), bulkGroupCloningDTO.getGroupMemberInsertList(), bulkGroupCloningDTO.getGroupDistPopulationToRemoveFromTarget(), bulkGroupCloningDTO.getGroupMemberDeleteList(), bulkGroupCloningDTO.getSupervisorMemberListMap());

        Map<Integer, Integer> groupEtag = updateGroupEtagMap(persisterTxn, bulkGroupCloningDTO.getPersistDTO(), bulkGroupCloningDTO.getGroupDetailsMapOfFinalGroupToBeClonedToTargetMdn(), bulkGroupCloningDTO.getGroupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn(), bulkGroupCloningDTO.getXdmsHome());

        //Update Sublist Subscriber Count
        updateSublistSubscriberCount(persisterTxn, bulkGroupCloningDTO.getGroupDetailsMapOfFinalGroupToBeClonedToTargetMdn(), bulkGroupCloningDTO.getGroupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn(), bulkGroupCloningDTO.getCorpProfile(), bulkGroupCloningDTO.getXdmsHome());


        Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubscribersDirectory(bulkGroupCloningDTO.getMdnListForDirectory(), null, bulkGroupCloningDTO.getXdmsHome(), persisterTxn);
        Map<String, KnOPDirChgDTO> finalEtagMap = etagMap;
        bulkGroupCloningDTO.getMdnListForLrgGrp().forEach(lrgGrpMdn -> {
            if (!bulkGroupCloningDTO.getMdnListForDirectory().contains(lrgGrpMdn)) {
                finalEtagMap.remove(lrgGrpMdn);
            }
        });
        bulkGroupCloningDTO.getBroadCastGrpList().forEach(groupEtag::remove);
        //removing the existing group member , As existing member getting directory notify
        List<String> exisitngGroupMembers = new ArrayList<>();
        for (Collection<String> grpMembers : bulkGroupCloningDTO.getGroupDistList().values()) {
            exisitngGroupMembers.addAll(grpMembers);
        }
        etagMap.keySet().removeIf(exisitngGroupMembers::contains);

        //etagMap = commonInfoUtil.formSubscriberNotification(bulkGroupCloningDTO.getToMdn(), groupEtag, DOC_CHANGE_TYPE.REPLACE.value(), etagMap);
        //respDTO.setChangeLogMap(etagMap);
        respDTO.setMdnCorpId(bulkGroupCloningDTO.getToMdnSubsProfile().getCorpId());
        respDTO.setGroupIds(groupEtag.keySet()/*.stream().filter(integer -> !mdnExistenceInGroup.containsKey(String.valueOf(integer))).collect(Collectors.toList())*/);
        populate(respDTO);
        /*if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
            respDTO.setFailedDataList(responseDTO.getFailedDataList());
        }*/
        //If the corporate is already linked i.e linked gateway key is not null get the refernce id
        /*if (bulkGroupCloningDTO.getCorpProfile().getLinkedGwKey() != null && (bulkGroupCloningDTO.getToMdnSubsProfile().getClientType() == SUBSCR_CLIENT_TYPE.GROUPMDN.value()
                || bulkGroupCloningDTO.getToMdnSubsProfile().getClientType() == SUBSCR_CLIENT_TYPE.SGMDNPATCH.value())) {
            KnCorpGWLinkedAccountInfoDTO accountInfo = commonInfoUtil.getCorporateLinkedAccountInfo(bulkGroupCloningDTO.getCorpProfile().getLinkedGwKey(), bulkGroupCloningDTO.getXdmsHome(), persisterTxn);
            Map<String, String> asyncInput = new HashMap<>();
            asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, bulkGroupCloningDTO.getXdmsHome());
            asyncInput.put(KnDbSyncFwConstants.NNI_REF_ID, accountInfo.getNniRefId());
            knLogger.debug(methodName, " Async inputs", asyncInput);
            KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
            int serviceType = KnDbSyncFwConstants.EXECUTOR.ETAG_UPDATE_NNI.value();
            collector.collect(serviceType, asyncInput);
        }*/
        knLogger.exit(methodName);
        return respDTO;
    }

    private void updateSublistSubscriberCount(KnPersisterTxn persisterTxn, Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeClonedToTargetMdn, Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn, KnCorpProfileDTO corpProfile, String xdmsHome) throws KnCorpBOException {
        String methodName = "updateSublistSubscriberCount";
        Collection<Integer> sublistIdListLrgStand = new ArrayList<>();
        Collection<Integer> sublistIdListStand = new ArrayList<>();
        Collection<Integer> sublistIdListLrgDisp = new ArrayList<>();
        Collection<Integer> sublistIdListDisp = new ArrayList<>();
        Collection<Integer> sublistIdListLrgBC = new ArrayList<>();
        Collection<Integer> sublistIdListBC = new ArrayList<>();
        groupDetailsMapOfFinalGroupToBeClonedToTargetMdn.forEach((key, value) -> {
            if (value.isLargeGroup()) {
                if (value.getGroupType() == STANDARD_GROUP) {
                    sublistIdListLrgStand.add(value.getGrpMemListId());
                } else if (value.getGroupType() == DISPATCH_GROUP) {
                    sublistIdListLrgDisp.add(value.getGrpMemListId());
                } else {
                    sublistIdListLrgBC.add(value.getGrpMemListId());
                }
            } else {
                if (value.getGroupType() == STANDARD_GROUP) {
                    sublistIdListStand.add(value.getGrpMemListId());
                } else if (value.getGroupType() == DISPATCH_GROUP) {
                    sublistIdListDisp.add(value.getGrpMemListId());
                } else {
                    sublistIdListBC.add(value.getGrpMemListId());
                }
            }
        });

        groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn.forEach((key, value) -> {
            if (value.isLargeGroup()) {
                if (value.getGroupType() == STANDARD_GROUP) {
                    sublistIdListLrgStand.add(value.getGrpMemListId());
                } else if (value.getGroupType() == DISPATCH_GROUP) {
                    sublistIdListLrgDisp.add(value.getGrpMemListId());
                } else {
                    sublistIdListLrgBC.add(value.getGrpMemListId());
                }
            } else {
                if (value.getGroupType() == STANDARD_GROUP) {
                    sublistIdListStand.add(value.getGrpMemListId());
                } else if (value.getGroupType() == DISPATCH_GROUP) {
                    sublistIdListDisp.add(value.getGrpMemListId());
                } else {
                    sublistIdListBC.add(value.getGrpMemListId());
                }
            }
        });

        knLogger.debug(methodName, "sublistIdListLrgStand: ", sublistIdListLrgStand, "sublistIdListLrgDisp: ",
                sublistIdListLrgDisp, "sublistIdListLrgBC: ", sublistIdListLrgBC, "sublistIdListStand: ", sublistIdListStand,
                "sublistIdListDisp: ", sublistIdListDisp, "sublistIdListBC: ", sublistIdListBC);
        if (!sublistIdListStand.isEmpty()) {
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListStand, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    corpProfile.getMaxMemPerLrgGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        }
        if (!sublistIdListDisp.isEmpty()) {
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListDisp, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    MAX_LIMIT_VALIDATION_NOT_REQUIRED, corpProfile.getMaxMembersPerDispatchGroup(), xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        }
        if (!sublistIdListBC.isEmpty()) {
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListBC, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    corpProfile.getMaxMemPerBCGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        }
        if (!sublistIdListLrgStand.isEmpty()) {
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListLrgStand, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    corpProfile.getMaxMemPerLrgGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        }
        if (!sublistIdListLrgDisp.isEmpty()) {
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListLrgDisp, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    MAX_LIMIT_VALIDATION_NOT_REQUIRED, corpProfile.getMaxMemPerLrgGrp(), xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        }
        if (!sublistIdListLrgBC.isEmpty()) {
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdListLrgBC, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    corpProfile.getMaxMemPerLrgBGrp(), MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHome, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        }
    }

    private Map<Integer, Integer> updateGroupEtagMap(KnPersisterTxn persisterTxn, KnCorpBulkGroupInfoPersistDTO persistDTO, Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeClonedToTargetMdn, Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn, String xdmsHome) throws KnCorpBOException {
        //Update Group List Etag
        Map<Integer, Integer> grpIdNormalToLrgTran = persistDTO.getGrpIdNormalToLrgTran();
        Map<Integer, KnCorpGroupDTO> impactedGroupDetailsMap = new HashMap<>();
        impactedGroupDetailsMap.putAll(groupDetailsMapOfFinalGroupToBeClonedToTargetMdn);
        impactedGroupDetailsMap.putAll(groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn);
        Map<Integer, Integer> groupIdLst = impactedGroupDetailsMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                map -> (map.getValue().isLargeGroup() == TRUE ? ENABLED : (grpIdNormalToLrgTran.containsKey(map.getKey())
                        ? ENABLED : DISABLED))));
        return groupInfoUtil.updateGroupListEtag(groupIdLst, xdmsHome, persisterTxn);
    }

    private void modifyCloneOperationProcessing(KnPersisterTxn persisterTxn, Collection<KnCorpSubscriberDTO> groupPrivateList, String xdmsHome, Collection<Integer> finalGroupToBeRemovedFromTargetMdn, String toMdn, Map<Integer, KnCorpGroupDTO> groupDetailsMap, Map<Integer, String> groupDistPopulationToAddToTarget, Map<Integer, KnCorpContactDTO> groupMemberInsertList, Map<Integer, List<String>> groupDistPopulationToRemoveFromTarget, LinkedHashMap<Integer, LinkedList<String>> groupMemberDeleteList, Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap) throws KnCorpBOException {
        String methodName = "modifyCloneOperationProcessing";
        contactInfoUtil.addPrivateContactList(groupPrivateList, xdmsHome, persisterTxn);

        if (null != finalGroupToBeRemovedFromTargetMdn && !finalGroupToBeRemovedFromTargetMdn.isEmpty()) {
            finalGroupToBeRemovedFromTargetMdn.forEach(grp -> {
                try {
                    contactInfoUtil.deleteSubscPrivateContactList(Collections.singleton(toMdn), groupDetailsMap.get(grp).getGrpMemListId(), xdmsHome, persisterTxn);
                } catch (KnCorpBOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        knLogger.debug(methodName, "groupDistPopulationToAddToTarget: ", groupDistPopulationToAddToTarget);
        knLogger.debug(methodName, "groupMemberInsertList: ", groupMemberInsertList);
        knLogger.debug(methodName, "groupDistPopulationToRemoveFromTarget: ", groupDistPopulationToRemoveFromTarget);
        knLogger.debug(methodName, "groupMemberDeleteList: ", groupMemberDeleteList);
        knLogger.debug(methodName, "supervisorMemberListMap: ", supervisorMemberListMap);

        if (null != groupDistPopulationToRemoveFromTarget && !groupDistPopulationToRemoveFromTarget.isEmpty())
            groupInfoUtil.deleteGroupDistInfo(groupDistPopulationToRemoveFromTarget, xdmsHome, persisterTxn);
        if (null != groupDistPopulationToAddToTarget && !groupDistPopulationToAddToTarget.isEmpty())
            groupInfoUtil.insertIntoCorpGrpDistInfo(groupDistPopulationToAddToTarget, xdmsHome, persisterTxn);
        if (null != groupMemberDeleteList && !groupMemberDeleteList.isEmpty())
            groupInfoUtil.deleteCorpGroupMemberList(groupMemberDeleteList, xdmsHome, persisterTxn);
        if (null != groupMemberInsertList && !groupMemberInsertList.isEmpty())
            groupInfoUtil.insertBulkIntoCorpGroupMemberList(groupMemberInsertList, xdmsHome, persisterTxn);
        if (null != supervisorMemberListMap && !supervisorMemberListMap.isEmpty())
            groupInfoUtil.updateBulkGroupMemberListSupervisorList(supervisorMemberListMap, xdmsHome, persisterTxn);
    }

    private static void addMemberProperties(Integer grp, Map<Integer, KnCorpGroupMemberDTO> bulkGroupMembersListMap,
                                            KnCorpContactDTO grpMemDto, KnSubsProfileDTO toMdnSubsProfile) {
        KnCorpGroupMemberDTO knCorpGroupMemberDTO = bulkGroupMembersListMap.get(grp);
        String targetActiveFS2 = toMdnSubsProfile.getActiveFS2();//based on ActiveFS2 of target subscriber
        if (KnGeneralUtil.getFeatureBitValue(targetActiveFS2, FEATURE_SET.AFFILIATIONFEATURE.value())) {
            grpMemDto.setIsAffiliationEnabled(1);
        } else {
            grpMemDto.setIsAffiliationEnabled(0);
        }
        grpMemDto.setCallInitiatePermission(knCorpGroupMemberDTO.getCallInitiatePermission());
        grpMemDto.setCallReceivePermission(knCorpGroupMemberDTO.getCallReceivePermission());
        grpMemDto.setInCallPermission(knCorpGroupMemberDTO.getInCallPermission());
    }


    @Override
    public KnCorpResponseDTO createSubsATGScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        String methodName = "createSubsATGScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn)";
        KnXDMTalkGroupServerRespDTO respDTO = new KnXDMTalkGroupServerRespDTO();
        knLogger.debug(methodName, "ipTalkGroupDTO - ", ipTalkGroupDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipTalkGroupDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            String mdn = ipTalkGroupDTO.getMdn();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            List<String> combinedList = new ArrayList<>();
            Map<String, Object> customParams = ipTalkGroupDTO.getCustomParamMap();
            if (ipTalkGroupDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHome, persisterTxn);
                    customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(PERSISTER_TXN, persisterTxn);
                    customParams.put(PTT_SERVER_ID, xdmsHome);
                    ipTalkGroupDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_SCAN_LIST);
                    hookIPDTO.setData(ipTalkGroupDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
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
                // Combine Added, Modified, and Deleted group IDs into a single set
                Set<Integer> combinedGroupIds = Stream.of(ipTalkGroupDTO.getAddedAddlTgList(), ipTalkGroupDTO.getModifiedAddlTgList(), ipTalkGroupDTO.getRemovedAddlTgList())
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .map(KnCorpGroupDTO::getGroupId)
                        .collect(Collectors.toSet());
                knLogger.debug(methodName, "Combined Group IDs: ", combinedGroupIds);
                // Proceed if there are any group IDs to process
                if (!combinedGroupIds.isEmpty()) {
                    Collection<String> idListStr = (Collection<String>) customParams.get(com.kodiak.common.resources.KnConstants.IDLIST);
                    Object idTypeObj = customParams.get(IDTYPE);
                    int idTypeValues = idTypeObj instanceof Integer ? (Integer) idTypeObj : Integer.parseInt((String) idTypeObj);
                    if (idListStr != null) {
                        List<Integer> idListInt = (List<Integer>) convertStrToIntColl(idListStr);
                        // Convert combinedGroupIds to String list for validation
                        combinedList = combinedGroupIds.stream()
                                .map(Object::toString)
                                .collect(Collectors.toList());
                        commonInfoUtil.validateGroupParams(combinedList, String.valueOf(ipTalkGroupDTO.getCorpId()), idListInt, xdmsHome, idTypeValues, persisterTxn);
                    }
                }
            }
            commonInfoUtil.validateCorpAndGroupParams( combinedList, String.valueOf(ipTalkGroupDTO.getCorpId()), xdmsHome, persisterTxn);
            KnCorpTGSPersistDTO corpTGSPersistDTO = new KnCorpTGSPersistDTO();
            //Subscriber's Profile:
            KnCorpSubscriberDTO subscriberDTO = contactInfoUtil.selectPocSubscriberInfo(mdn, xdmsHome, persisterTxn);
            if (null != ipTalkGroupDTO.getCorpId() && Integer.parseInt(ipTalkGroupDTO.getCorpId()) > 0 && subscriberDTO.getCorpId() != Integer.parseInt(ipTalkGroupDTO.getCorpId())) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Subscribers Does not belong to the corporation.");
            }
            //Existing Addl TG List from DB for Input MDN:
            Collection<KnCorpAddlTGInfoDTO> existingSubsAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "existingSubsAddlTGList - ", existingSubsAddlTGList);
            corpTGSPersistDTO.setAlradyAddlGrpLst(existingSubsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getGroupId).collect(Collectors.toList()));
            //Added: Input
            Collection<KnCorpAddlTGInfoDTO> requestSubsAddlTGList = ipTalkGroupDTO.getAddedAddlTgList();
            knLogger.debug("requestSubsAddlTGList :",requestSubsAddlTGList);

            Set<Integer> reqAddlGroupIds = requestSubsAddlTGList.stream().map(req->req.getGroupId()).collect(Collectors.toSet());
            knLogger.debug(methodName, "reqAddlGroupIds - ", reqAddlGroupIds);
            List<Integer> groupIds = new ArrayList<>(reqAddlGroupIds);
            boolean extCnt = contactInfoUtil.isExternalSubscriber(ipTalkGroupDTO.getMdn(), Integer.parseInt(ipTalkGroupDTO.getCorpId()),
                    xdmsHome, persisterTxn);
            Map<Integer, Integer> groupIdTypesMap = groupInfoUtil.getValidGrpInCorp(groupIds, Integer.parseInt(ipTalkGroupDTO.getCorpId()),
                    xdmsHome, persisterTxn);
            List<Integer> inputBGGroup = groupIdTypesMap.entrySet().stream().filter(map -> map.getValue().equals(BROADCAST_GROUP))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            List<Integer> grpIdListInCorp = new ArrayList<>(groupIdTypesMap.keySet());
            Set<Integer> grpTypeSet = new HashSet<>(groupIdTypesMap.values());
            List<Integer> subsGrpList = groupInfoUtil.getSubsGroupIdList(grpIdListInCorp, ipTalkGroupDTO.getMdn(), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subsGrpList ", subsGrpList);
            List<Integer> brdcstGrpIds = groupInfoUtil.getBroadcstGroupList(ipTalkGroupDTO.getMdn(), xdmsHome, persisterTxn);
            Map<Integer, Collection<Integer>> zoneChannelMap = groupInfoUtil.getZoneChannelMap(xdmsHome, persisterTxn);
            knLogger.debug(methodName, "zoneChannelMap ", zoneChannelMap);
            Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(inputBGGroup, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "groupDistList ", groupDistList);
            int subsPVMajorVer = subscriberDTO.getClientPVmajorVer();
            knLogger.debug(methodName, "subsPVMajorVer :", subsPVMajorVer);
            corpTGSPersistDTO.setZoneChannelMap(zoneChannelMap);
            corpTGSPersistDTO.setMaxZone(corpProfile.getMaxZones());
            corpTGSPersistDTO.setMaxChannelPerZone(corpProfile.getMaxChannelsPerZone());
            corpTGSPersistDTO.setInputDTO(ipTalkGroupDTO);
            corpTGSPersistDTO.setSubsCorpId(subscriberDTO.getCorpId());
            corpTGSPersistDTO.setSubsClientType(subscriberDTO.getClientType());
            corpTGSPersistDTO.setDbAddlTGList(existingSubsAddlTGList);
            corpTGSPersistDTO.setSubsExternalCont(extCnt);
            corpTGSPersistDTO.setGrpListInCorp(grpIdListInCorp);
            corpTGSPersistDTO.setReqGroupTypes(grpTypeSet);
            corpTGSPersistDTO.setGrpIdTypeMap(groupIdTypesMap);
            corpTGSPersistDTO.setSubsGroupList(subsGrpList);
            corpTGSPersistDTO.setBroadcastGrpIds(new HashSet<Integer>(brdcstGrpIds));
            corpTGSPersistDTO.setBgMemberMap(groupDistList);
            corpTGSPersistDTO.setClientMajorVersion(subsPVMajorVer);

            Optional<KnCorpAddlTGInfoDTO> priorityExists = requestSubsAddlTGList.stream()
                    .filter(subsReqDto -> subsReqDto.getPriority() != null).findAny();
            List<KnXDMTalkGroupInfoDTO> newCampGrpList = null;
            KnTalkGrpScanMode talkGrpScanMode = null;
            corpTGSPersistDTO.setPriorityExists(priorityExists.isPresent());
            boolean pttRadioClient = subscriberDTO.getClientType() == PTTRADIOHANDSETCLIENT || subscriberDTO.getClientType() == PTTRADIOCROSSCARRIERCLIENT
                    || subscriberDTO.getClientType() == PTTRADIOWIFIONLYCLIENT;
            corpTGSPersistDTO.setSubsClientType(subscriberDTO.getClientType());
            corpTGSPersistDTO.setActiveFS2(subscriberDTO.getSubsActiveFS2());
            if(priorityExists.isPresent()){
                newCampGrpList = requestSubsAddlTGList.stream().filter(reqDto -> reqDto.getPriority() != null)
                        .map(KnCorpSubsProvInfoUtil::populateTalkGroup).collect(Collectors.toList());
                knLogger.debug("newCampGrpList :-",newCampGrpList);
                Collection<KnXDMTalkGroupInfoDTO> uniqueGroupIdsNPriority = newCampGrpList.stream()
                        .<Map<Integer, KnXDMTalkGroupInfoDTO>>collect(HashMap::new, (m, e) -> m.put(e.getGroupId(), e), Map::putAll)
                        .values();
                knLogger.debug("uniqueGroupIdsNPriority :-",uniqueGroupIdsNPriority);
                if (corpProfile.getSystemTGSBit() == ENABLED) {
                    corpTGSPersistDTO.setSysTGSBitEanble(true);
                }
                // Etag Validation, Etag in request and Etag in DB must be same
                talkGrpScanMode = groupInfoUtil.getSubsTalkGrpScanMode(ipTalkGroupDTO.getMdn(), xdmsHome, persisterTxn);
                if (talkGrpScanMode != null) {
                    corpTGSPersistDTO.setEtag(String.valueOf(talkGrpScanMode.getEtag()));
                }
                // Verify if Max TGSc size limit is not reached
                corpTGSPersistDTO.setMaxCampedGrpLmt(corpProfile.getMaxCampedGrp());
                // Verify that only one group is allowed to be set for each of the priority range. No Two groups shall have same priority other than 0
                // Verify the max priority limit Column: MAXCAMPEDGROUPS Table:DG.XDMS_SVC_CONFIG
                corpTGSPersistDTO.setMaxPriority(corpProfile.getMaxPriority());
                List<KnCorpTGSPersistDTO> corpTGSPersistDTOs = groupInfoUtil.getSubsCampedGrp(ipTalkGroupDTO, xdmsHome, persisterTxn);
                corpTGSPersistDTO.setDbGrpsInScanList(corpTGSPersistDTOs.stream().map(KnCorpTGSPersistDTO::getGroupId).collect(Collectors.toList()));
                if(pttRadioClient) {
                    corpTGSPersistDTO.setNewOnlyCampedGroups(new ArrayList<>(uniqueGroupIdsNPriority));
                    corpTGSPersistDTO.setMaxPttRadioScanGrpLmt(corpProfile.getMaxPttRadioScanSize());
                    corpTGSPersistDTO.setMaxPttRadioChannelGrpLmt(corpProfile.getMaxPttRadioChannelSize());
                }
                corpTGSPersistDTO.setNewCampedGroups(new ArrayList<>(uniqueGroupIdsNPriority));
            } else {
                corpTGSPersistDTO.setMaxPttRadioChannelGrpLmt(corpProfile.getMaxPttRadioChannelSize());
                corpTGSPersistDTO.setNewCampedGroups(requestSubsAddlTGList.stream().map(KnCorpSubsProvInfoUtil::populateTalkGroup).collect(Collectors.toList()));
            }
            //getting flags from common config
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowGroupAcrossZones = microServicesParamNameValueMap.get(ALLOW_GROUP_ACROSS_ZONES);
            String skipChannelPerZoneValidation = microServicesParamNameValueMap.get(CHANNELPERZONE_VALIDATION);
            String defaultScanModeValue = microServicesParamNameValueMap.get(DEF_TGSC_MODE);

            corpTGSPersistDTO.setAllowGroupAcrossZones(allowGroupAcrossZones!=null?Integer.parseInt(allowGroupAcrossZones):0);
            if(skipChannelPerZoneValidation!=null)
                corpTGSPersistDTO.setSkipChannelPerZoneValidation(Integer.valueOf(skipChannelPerZoneValidation));

            knLogger.debug(methodName, "Before Validation", corpTGSPersistDTO);
            validatorFW.validate(corpTGSPersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            // Business Logics:
            // Insert Call:
            if(!requestSubsAddlTGList.isEmpty()) groupInfoUtil.insertSubsAddlTGList(requestSubsAddlTGList, xdmsHome, persisterTxn);
            // Notification Preparation:
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);

            //GroupIds:
            Set<Integer> groupIdList = new HashSet<>(reqAddlGroupIds);
            KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
            contactDTO.setMdn(mdn);
            contactDTO.setCorpId(Integer.parseInt(ipTalkGroupDTO.getCorpId()));
            Collection<KnCorpGroupInfoPersistDTO> groupList = groupInfoUtil.getSubsGroupListForXcap(contactDTO, 0, xdmsHome, persisterTxn);
            Map<Integer, KnCorpGroupInfoPersistDTO> groupDetailsMap = groupList.stream().filter(grpList -> groupIdList.contains(grpList.getGroupId()))
                    .collect(Collectors.toMap(KnCorpGroupInfoPersistDTO::getGroupId, request -> request));
            knLogger.debug(methodName, "groupDetailsMap -- ", groupDetailsMap);
            requestSubsAddlTGList.forEach(reqList -> {
                KnCorpGroupInfoPersistDTO reqData = groupDetailsMap.get(reqList.getGroupId());
                if(reqData != null){
                    reqList.setGroupType(reqData.getGroupType());
                    reqList.setGroupMemCount(reqData.getGroupMemberCount());
                    reqList.setGroupCreatedBy(reqData.getGroupCreatedBy());
                    reqList.setAvatar(reqData.getAvatar());
                }
            });
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<>();
            if(!priorityExists.isPresent() && talkGrpScanMode== null)
            {
                KnTalkGrpScanMode scanModeDTO = groupInfoUtil.getSubsTalkGrpScanMode(ipTalkGroupDTO.getMdn(), xdmsHome, persisterTxn);
                Integer mode;
                Boolean modeUpdated=Boolean.FALSE;
                knLogger.debug(methodName,"Mode send in the request-->"+ipTalkGroupDTO.getMode());
                if(ipTalkGroupDTO.getMode() == null){
                    if(defaultScanModeValue == null)
                    {
                         knLogger.info(methodName,"DEF_TGCS_MODE is not configured");
                         mode = 1;
                    }else{
                        mode=Integer.parseInt(defaultScanModeValue);
                    }
                }else{
                    mode=ipTalkGroupDTO.getMode();
                }

                if(scanModeDTO == null){
                    groupInfoUtil.createSubsTalkGrpScanMode(ipTalkGroupDTO.getMdn(), mode, xdmsHome, persisterTxn);
                    modeUpdated=Boolean.TRUE;
                }else if(scanModeDTO.getMode() == null){
                    groupInfoUtil.updateSubsTalkGrpScanMode(ipTalkGroupDTO.getMdn(), mode, xdmsHome, persisterTxn);
                    modeUpdated=Boolean.TRUE;
                }
                if(modeUpdated) {
                    Map<String, KnTGSModeChgDTO> tgsModeChgMap = new HashMap<String, KnTGSModeChgDTO>();
                    KnTGSModeChgDTO value = new KnTGSModeChgDTO();
                    value.setPocHome(subscriberDTO.getPocHome());
                    value.setPresenceHome(subscriberDTO.getPresenceHome());
                    value.setTgsMode(mode);
                    tgsModeChgMap.put(ipTalkGroupDTO.getMdn(), value);
                    respDTO.setTgsModeChgMap(tgsModeChgMap);
                    //set Etag for xcap reaponse for new tgsc list
                    respDTO.setEtag("1");
                    etagMap = commonInfoUtil.formMdnTGSCReplaceNotification(ipTalkGroupDTO.getMdn(), etagMap, xdmsHome, persisterTxn);
                }
            }
            if(priorityExists.isPresent()){
                List<KnCorpTalkGrpInfoDTO> newGrpList = newCampGrpList.stream().distinct()
                        .map(reqDto -> new KnCorpTalkGrpInfoDTO(reqDto.getGroupId(), reqDto.getPriority()))
                            .collect(Collectors.toList());
                if (talkGrpScanMode == null && newGrpList.size() > 0) {
                    // Preparing Map<String, KnTGSModeChgDTO> for sending SEH
                    // Notification, This Map will be use to send notification
                    // once transaction is successful
                    Integer mode = ipTalkGroupDTO.getMode();
                    if (mode == null) {
                        mode = 0;
                    }
                    groupInfoUtil.createSubsTalkGrpScanMode(ipTalkGroupDTO.getMdn(), mode, xdmsHome, persisterTxn);
                    Map<String, KnTGSModeChgDTO> tgsModeChgMap = new HashMap<String, KnTGSModeChgDTO>();
                    KnTGSModeChgDTO value = new KnTGSModeChgDTO();
                    value.setPocHome(subscriberDTO.getPocHome());
                    value.setPresenceHome(subscriberDTO.getPresenceHome());
                    value.setTgsMode(mode);
                    tgsModeChgMap.put(ipTalkGroupDTO.getMdn(), value);
                    respDTO.setTgsModeChgMap(tgsModeChgMap);
                    //set Etag for xcap reaponse for new tgsc list
                    respDTO.setEtag("1");
                    etagMap = commonInfoUtil.formMdnTGSCReplaceNotification(ipTalkGroupDTO.getMdn(), etagMap, xdmsHome, persisterTxn);
                    //respDTO.setChangeLogMap(etagMap);
                }
                if(!newGrpList.isEmpty()){
                    groupInfoUtil.createSubsCampedGrps(ipTalkGroupDTO.getMdn(), newGrpList, CAMPED_BY_CORPORATE_ADMIN, xdmsHome, persisterTxn);
                }
                contactInfoUtil.updateSubscribersDirectory(mdnList, etagMap, xdmsHome, persisterTxn);
            }
            etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHome, mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
            if (requestSubsAddlTGList != null && !requestSubsAddlTGList.isEmpty()) {
                groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) requestSubsAddlTGList, xdmsHome, persisterTxn);
            }
            knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
            if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                if (subsPVMajorVer >= PROTOCOL_VERSION_16) {
                    etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, requestSubsAddlTGList,
                            null, groupCorpIdMap);
                } else {
                    etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, requestSubsAddlTGList, null,
                            null, groupCorpIdMap);
                }
            }
            knLogger.debug(methodName, "etagMap 333 - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            // todo: LI Notification Preparation. // no SDD requirements.
            respDTO.setChangeLogMap(etagMap);
            populate(respDTO);
        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occured while createSubsATGScanList", e);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured while createSubsATGScanList", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured while createSubsATGScanList ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpBulkGroupJobRespDTO contactPairingForBulkGroupProcess(KnIPCorpDispatchGrpMemInfoDto groupInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "contactPairingForBulkGroupProcess(KnIPCorpDispatchGrpMemInfoDto,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", groupInfoDTO);
        KnCorpBulkGroupJobRespDTO respDto = new KnCorpBulkGroupJobRespDTO();
        try {
            int corpId = groupInfoDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            String ipMdn = groupInfoDTO.getToMdn();
            Map<Integer, KnCorpGroupDTO> groupInfoMap = groupInfoUtil.getGroupBasicDetailsMap(groupInfoDTO.getGrpIds(), xdmsHome, persisterTxn);
            KnSubsProfileDTO subsProfileDTO = commonInfoUtil.getProfileDetails(ipMdn, PUBLIC_PROFILE, false, persisterTxn);
            Collection<String> dbLocWatcherAndDispatcher = groupInfoUtil.getLocWatcherAndDispatcher(groupInfoDTO.getGrpIds(), xdmsHome, persisterTxn);
            Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupInfoDTO.getGrpIds(), xdmsHome, persisterTxn);
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<>();
            Set<String> mdnList = new HashSet<>();
            knLogger.debug(methodName," Data from TimesTen :", groupDistList , groupInfoMap, dbLocWatcherAndDispatcher);
            Map<String, Collection<KnCorpSubscriberDTO>> finalContactListMap = null;
            if (subsProfileDTO.getClientType() != SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()) {
                //to collect supervisors, dispatchers from target removed groups.
                LinkedList<String> contactListMembers = null;

                //target mdn is missing in mdnList while adding in dispatch group with no dispatchers/supervisor in that group.
                if (dbLocWatcherAndDispatcher.isEmpty()) {
                    for (Integer groupId : groupDistList.keySet()) {
                        if (groupDistList.get(groupId).contains(ipMdn) && groupInfoMap.get(groupId).getGroupType() == 2) {
                            mdnList.add(ipMdn);
                            break;
                        }
                    }
                } else {
                    contactListMembers = new LinkedList<>();
                    for (String watcher : dbLocWatcherAndDispatcher) {
                        for (Integer groupId : groupDistList.keySet()) {
                            if (groupDistList.get(groupId).contains(watcher) && !groupDistList.get(groupId).contains(ipMdn)) {
                                contactListMembers.add(watcher);
                            }
                        }
                    }
                    knLogger.debug(methodName, " contactListMembers :", contactListMembers);
                    dbLocWatcherAndDispatcher.removeAll(contactListMembers);
                }
                //sending contactListMembers to delete contactlist of target mdn - supervisor/dispatcher after removing from group.
                finalContactListMap = contactInfoUtil.contactListPreparationForNonDispatcher(ipMdn, dbLocWatcherAndDispatcher, contactListMembers,
                        corpId, corpProfile.getMaxContactsPerSubsc(), etagMap, corpProfile.getXdmsHome(), persisterTxn);
            } else {
                Set<String> totalMdn = new HashSet<>();
                groupDistList.forEach((grp, grpMems) -> {
                    totalMdn.addAll(new ArrayList<>(grpMems));
                });
                finalContactListMap = contactInfoUtil.contactListPreparationForDispatcher(ipMdn, totalMdn, corpId,
                        corpProfile.getMaxContactsPerSubsc(), etagMap, corpProfile.getXdmsHome(), persisterTxn);
            }
            knLogger.debug(methodName, "finalContactListMap - ", KnGDPRTemplate.mapKeyMdn(finalContactListMap)," Added members :", mdnList);
            if(finalContactListMap != null && !finalContactListMap.isEmpty()){
                finalContactListMap.forEach((key, contList) -> {
                    mdnList.addAll(contList.stream().map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList()));
                });
            }
            //To collect members of all groups from which target mdn got removed.
            Set<String> removedGroupsMembers = new HashSet<>();
            for (Integer groupId : groupDistList.keySet()) {
                if (!groupDistList.get(groupId).contains(ipMdn) && groupInfoMap.get(groupId).getGroupType() != 2) {
                    removedGroupsMembers.addAll(groupDistList.get(groupId));
                    //adding self mdn in case if he was supervisor or dispatcher in that case missing target to disable 27th bit.
                    removedGroupsMembers.add(ipMdn);
                }
            }
            Set<String> memberList = new HashSet<>(mdnList);
            memberList.addAll(removedGroupsMembers);
            List<KnXDMSubsProvInfoDTO> subscListToUpdate = new ArrayList<KnXDMSubsProvInfoDTO>();
            //get the mdn that are internal and are not dispatcher type
            ArrayList<String> intNonDispMembers = contactInfoUtil.getInternalNonDispatchMember(memberList, corpId, xdmsHome, persisterTxn);
            //This variable will be not null only in case update subscribers .Added since the updated mdn was missed in the internal subscriber fetch.
            if (groupInfoDTO.getUpdatedMdn() != null && !groupInfoDTO.getUpdatedMdn().isEmpty()) {
                intNonDispMembers.add(groupInfoDTO.getUpdatedMdn());
            }
            Map<String, KnCorpSubscriberDTO> subscBasicDetails = contactInfoUtil.getSubscIsMemOfDispGrpDetails(intNonDispMembers, xdmsHome, persisterTxn);
            ArrayList<String> pocHomes = new ArrayList<String>();
            for (Map.Entry<String, KnCorpSubscriberDTO> entry : subscBasicDetails.entrySet()) {
                if (entry.getValue() != null) {
                    pocHomes.add(entry.getValue().getPocHome());
                }
            }
            Collection<KnXDMCorpContactDTO> addedMemberList = new ArrayList<KnXDMCorpContactDTO>();
            mdnList.forEach(contMdn -> {
                KnXDMCorpContactDTO contactDTO = new KnXDMCorpContactDTO();
                contactDTO.setMdn(contMdn);
                addedMemberList.add(contactDTO);
            });
            groupInfoDTO.setAddedGroupMembers(addedMemberList);
            Map<String, Boolean> locationPubEnabled = contactInfoUtil.getLocationPubFeaturebit(pocHomes, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "subscBasicDetails - ", KnGDPRTemplate.mapKeyMdn(subscBasicDetails));
            knLogger.debug(methodName, "groupInfoDTO.getAddedGroupMembers() - ", groupInfoDTO.getAddedGroupMembers());
            if (groupInfoDTO.getAddedGroupMembers() != null && groupInfoDTO.getAddedGroupMembers().size() > 0) {
                for (KnXDMCorpContactDTO subsc : groupInfoDTO.getAddedGroupMembers()) {
                    knLogger.debug(methodName, "subsc - ", subsc);
                    if (subscBasicDetails != null && subscBasicDetails.get(subsc.getMdn()) != null &&
                            locationPubEnabled != null && locationPubEnabled.get(subscBasicDetails.get(subsc.getMdn()).getPocHome())) {
                        Integer dispMemInd = subscBasicDetails.get(subsc.getMdn()).getDispatchGrpmember();
                        if (dispMemInd == 0) {
                            KnXDMSubsProvInfoDTO subsProvInfoDTO = new KnXDMSubsProvInfoDTO();
                            subsProvInfoDTO.setMdn(subsc.getMdn());
                            subsProvInfoDTO.setDispatchGroupMember(1);
                            subsProvInfoDTO.setPublicSubscriptionType(-1);
                            subsProvInfoDTO.setCorporateSubscriptionType(-1);
                            subscListToUpdate.add(subsProvInfoDTO);
                        }
                    }
                }
            }
            knLogger.debug(methodName, " Removed members list :", removedGroupsMembers);
            Collection<KnXDMCorpContactDTO> removedMembersList = new ArrayList<KnXDMCorpContactDTO>();
            removedGroupsMembers.forEach(contMdn -> {
                KnXDMCorpContactDTO contactDTO = new KnXDMCorpContactDTO();
                contactDTO.setMdn(contMdn);
                removedMembersList.add(contactDTO);
            });
            groupInfoDTO.setDeletedGroupMembers(removedMembersList);
            //For the deleted guy check if its part of any other dispatch grp or the dispatch contact
            if (groupInfoDTO.getDeletedGroupMembers() != null && groupInfoDTO.getDeletedGroupMembers().size() > 0) {
                ArrayList<String> deletedMemList = new ArrayList<String>();
                for (KnXDMCorpContactDTO subsc : groupInfoDTO.getDeletedGroupMembers()) {
                    if (subscBasicDetails != null && subscBasicDetails.get(subsc.getMdn()) != null) {
                        deletedMemList.add(subsc.getMdn());
                    }
                }
                //This variable will be not null only in case update subscribers
                if (groupInfoDTO.getUpdatedMdn() != null && !groupInfoDTO.getUpdatedMdn().isEmpty()) {
                    deletedMemList.add(groupInfoDTO.getUpdatedMdn());
                }
                //Checking if any deleted is part of a dispatch group
                Collection<String> nonDispGrpMember = groupInfoUtil.getSubsNotInDispatchGroupFromMDNList(deletedMemList, corpId, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "nonDispGrpMember --", KnGDPRTemplate.mdnList(nonDispGrpMember));
                //Checking if the delete subscriber belongs to  a dispatcher contactList
                Collection<String> nonDispContact = contactInfoUtil.getDispContacts(nonDispGrpMember, corpId, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "nonDispContact -- ", KnGDPRTemplate.mdnList(nonDispContact));
                // checking if the deleted members belong to any locWatcher.
                Collection<String> nonLocWatcherMember = groupInfoUtil.getLocWatcherMdn(nonDispGrpMember, corpId, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "nonLocWatcherMember -- ", KnGDPRTemplate.mdnList(nonLocWatcherMember));
                nonLocWatcherMember.addAll(nonDispContact);
                // nonDispGrpMember == null; means all the deletedMemList are part of other dispatchGroupList. so no need to disable the locationBit.
                // nonDispContact; this variable will return the mdn which is a contacts of dispatcher in the corporate by providing the residue input nonDispGrpMember.
                // nonLocWatcherMember; this variable will return mdnList, where ever locWatcher is available along with the mdnList.
                if (nonDispGrpMember != null) {
                    for (String mdn : nonDispGrpMember) {
                        if (subscBasicDetails != null && subscBasicDetails.get(mdn) != null) {
                            Integer dispMemInd = subscBasicDetails.get(mdn).getDispatchGrpmember();
                            if (dispMemInd != null && dispMemInd == 1) {
                                if (!nonLocWatcherMember.contains(mdn)){
                                    KnXDMSubsProvInfoDTO subsProvInfoDTO = new KnXDMSubsProvInfoDTO();
                                    subsProvInfoDTO.setMdn(mdn);
                                    subsProvInfoDTO.setDispatchGroupMember(0);
                                    subsProvInfoDTO.setPublicSubscriptionType(-1);
                                    subsProvInfoDTO.setCorporateSubscriptionType(-1);
                                    subscListToUpdate.add(subsProvInfoDTO);
                                }
                            }
                        }
                    }
                }
            }
            respDto.setSubscIsDispMemDetailsList(subscListToUpdate);
            respDto.setChangeLogMap(etagMap);
            populate(respDto);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving non dispatch group members - ", e);
            populate(respDto, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving non dispatch group memberst - ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDto, e);
        }
        knLogger.debug(methodName, "non dispatch group members Response - ", respDto);
        knLogger.debug(methodName, "EXIT :");
        return respDto;
    }
    public KnCorpResponseDTO getSubsGroupMemberShipDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubsGroupMemberShipDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        KnCorpProfileDTO corpProfile;
        ArrayList<Integer> mcxGroups = new ArrayList<Integer>();
        ArrayList<Integer> nonMcxGroups = new ArrayList<Integer>();
        ArrayList<Integer> allGroups = new ArrayList<Integer>(ipSubscriberInfoDTO.getGroupIds());
        Map<Integer, Integer> grpMemberShipMap = new HashMap<Integer, Integer>();
        Map<Integer, KnCorpGroupContactDTO > mcxGrpMemberShipMap = new HashMap<Integer, KnCorpGroupContactDTO >();
        KnCorpResponseDTO corpresponse = new KnCorpResponseDTO();
        try {
            knLogger.debug(methodName, "ENTRY - ", ipSubscriberInfoDTO);
            corpProfile = commonInfoUtil.getProfileDetails(ipSubscriberInfoDTO.getCorpId(), CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            ArrayList<KnCorpGroupDTO> cropList = groupInfoUtil.getGroupBasicInfoList(ipSubscriberInfoDTO.getGroupIds(), xdmsHome,true, persisterTxn);
            for (KnCorpGroupDTO corpDto : cropList) {
                if (corpDto.getMcxGrpInd() == 1) {
                    mcxGroups.add(corpDto.getGroupId());
                } else {
                    nonMcxGroups.add(corpDto.getGroupId());
                }
            }
            knLogger.debug(methodName, "mcsGroups - ", mcxGroups, "nonMcsGroups - ", nonMcxGroups);
            Collection<KnCorpGroupMemberDTO> groupmembers = groupInfoUtil.getCorpGroupMembersList(ipSubscriberInfoDTO.getGroupIds(), xdmsHome,true, persisterTxn);
            for (KnCorpGroupMemberDTO member : groupmembers) {
                if (member.getMdn().equals(ipSubscriberInfoDTO.getMdn())) {
                    if (!grpMemberShipMap.containsKey(member.getGroupId()) && nonMcxGroups.contains(member.getGroupId())) {
                        grpMemberShipMap.put(member.getGroupId(), 1);
                    }
                    if(!mcxGrpMemberShipMap.containsKey(member.getGroupId()) && mcxGroups.contains(member.getGroupId())){
                        grpMemberShipMap.put(member.getGroupId(), 1);
                        KnCorpGroupContactDTO pDTO= new KnCorpGroupContactDTO();
                        pDTO.setCallInitiateAllowed(member.getCallInitiatePermission());
                        pDTO.setCallTerminateAllowed(member.getCallReceivePermission());
                        pDTO.setIncallAllowed(member.getInCallPermission());
                        pDTO.setIsBroadcaster(member.getBroadcaster());
                        pDTO.setIsLocSupervisor(member.getLocWatcher());
                        pDTO.setIsOSMAuthorized(member.getIsOSMAuthorize());
                        pDTO.setIsSupervisor(member.getSupervisory());
                        mcxGrpMemberShipMap.put(member.getGroupId(), pDTO);
                    }
                }

            }
            knLogger.debug(methodName, "grpMemberShipMap after non mcx group ", grpMemberShipMap);
            knLogger.debug(methodName, "fetch UserProfile Detaiils - ", ipSubscriberInfoDTO.getUserProfileId());
        //    KnCorpUserProfileDTO knCorpUserProfileDTO = userProfileUtil.getUserProfile(String.valueOf(ipSubscriberInfoDTO.getCorpId()), ipSubscriberInfoDTO.getUserProfileId()
          //          , xdmsHome, persisterTxn);
			Set<KnCorpGroupListInfoDTO> mcxGroupList=userProfileUtil.retriveGroupProfileInfoByProfileId(ipSubscriberInfoDTO.getUserProfileId(), xdmsHome,true, persisterTxn);

            if (mcxGroupList != null && mcxGroupList != null) {
                for (KnCorpGroupListInfoDTO ldto : mcxGroupList) {
                    if (mcxGroups.contains(ldto.getGroupID())) {
                        if (!mcxGrpMemberShipMap.containsKey(ldto.getGroupID())) {
                        	grpMemberShipMap.put(ldto.getGroupID(), 1);
                        	KnCorpGroupContactDTO pDTO= new KnCorpGroupContactDTO();
                        	pDTO.setCallInitiateAllowed(ldto.getGrpMemProps().getCallInitiateAllowed());
                        	pDTO.setCallTerminateAllowed(ldto.getGrpMemProps().getCallTerminateAllowed());
                           pDTO.setIncallAllowed(ldto.getGrpMemProps().getIncallAllowed());
                           pDTO.setIsBroadcaster(ldto.getGrpMemProps().getIsBroadcaster());
                           pDTO.setIsLocSupervisor(ldto.getGrpMemProps().getIsLocSupervisor());
                           pDTO.setIsOSMAuthorized(ldto.getGrpMemProps().getIsOSMAuthorized());
                            pDTO.setIsSupervisor(ldto.getGrpMemProps().getIsSupervisor());
                        	mcxGrpMemberShipMap.put(ldto.getGroupID(), pDTO);

                        }
                    }
                }

            }
            knLogger.debug(methodName, "mcxGrpMemberShipMap after mcx group ", mcxGrpMemberShipMap);
            knLogger.debug(methodName, "removing mcsGroups & non-mcxGroups where mebership found");
            if (grpMemberShipMap != null && !grpMemberShipMap.isEmpty() || mcxGrpMemberShipMap!=null && !mcxGrpMemberShipMap.isEmpty()) {
                knLogger.debug(methodName, "allGroups ", allGroups);
                allGroups.removeAll(grpMemberShipMap.keySet());
                allGroups.removeAll(mcxGrpMemberShipMap.keySet());
                knLogger.debug(methodName, "populating default memebership for those groups not exsists ", allGroups);
            }
            if (allGroups != null && !allGroups.isEmpty()) {
                allGroups.forEach(grpId -> grpMemberShipMap.put(grpId, 0));
            }

            knLogger.debug(methodName, "grpMemberShipMap", grpMemberShipMap);
            corpresponse.setGrpMemberShipMap(grpMemberShipMap);
            corpresponse.setMcxGrpMemberShipMap(mcxGrpMemberShipMap);
            corpresponse.setMdn(ipSubscriberInfoDTO.getMdn());
            knLogger.debug(methodName, "Exit");

        } catch (KnCorpBOException | KnDAOException e) {
            knLogger.error(methodName, "exception occured getting groupmember ", e);

        }

        return corpresponse;
    }

    @Override
    public KnCorpUserPermissionRespDTO getAllAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn, boolean upmFlag) {
        String methodName = "controller.getAllAuthorizedMdnList";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDTO = new KnCorpUserPermissionRespDTO();
        try {
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            String corpId = String.valueOf(ipAuthUserPermissionInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            String targetMdn = ipAuthUserPermissionInfoDTO.getTargetMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(targetMdn, PUBLIC_PROFILE, false, persisterTxn);
            if (subscProfile.getCorpId() <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", KnGDPRTemplate.mdn(targetMdn));
                throw new KnCorpBOException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }

            //No validation/hierarchy support is added as this is util method.
            Map<String, Integer> authMdnPermMap = corpSubsProvInfoUtil.getAuthMdnList(ipAuthUserPermissionInfoDTO, xdmsHome, true, persisterTxn);
            knLogger.debug(methodName, "Fetch the Auth permissions profile: ", KnGDPRTemplate.mapKeyMdn(authMdnPermMap));

            if (upmFlag) {
                Set<String> authMdnList = authMdnPermMap.keySet();
                List<String> authMdnSet = new ArrayList<>(authMdnList);
                Map<String, KnCorpSubscriberDTO> baseMdnsOnly = contactInfoUtil.getOnlyBaseMdn(authMdnSet, xdmsHome, corpProfile.getCorpId(), false, persisterTxn);
                Map<String, Integer> filteredAuthMdnPermMap = authMdnPermMap.entrySet().stream()
                        .filter(entry -> baseMdnsOnly.containsKey(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, String> subscNameList = contactInfoUtil.getSubscribersName(filteredAuthMdnPermMap.keySet(), xdmsHome, persisterTxn);
                knLogger.debug(methodName, "subscNameList: ", KnGDPRTemplate.mapKeyMdn(subscNameList));

                Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
                filteredAuthMdnPermMap.forEach((auth, permissions) -> {
                    KnTargetMdnPermBitInfo targetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
                    BitSet mcpttPermissions = featureSetUtil.convertLongToBitSet(permissions);
                    targetMdnPermBitInfo.setMdn(auth);
                    targetMdnPermBitInfo.setSubsName(subscNameList.get(auth));
                    corpSubsProvInfoUtil.bitsToIntegerConversion(mcpttPermissions, targetMdnPermBitInfo);
                    targetMdnPermBitInfos.add(targetMdnPermBitInfo);
                });
                knLogger.debug(methodName, "targetMdnPermBitInfos :", targetMdnPermBitInfos);
                respDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
            } else {
                Set<String> authMdnList = authMdnPermMap.keySet();
                Map<String, String> subscNameList = contactInfoUtil.getSubscribersName(authMdnList, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "subscNameList: ", KnGDPRTemplate.mapKeyMdn(subscNameList));

                Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
                authMdnPermMap.forEach((auth, permissions) -> {
                    KnTargetMdnPermBitInfo targetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
                    BitSet mcpttPermissions = featureSetUtil.convertLongToBitSet(permissions);
                    targetMdnPermBitInfo.setMdn(auth);
                    targetMdnPermBitInfo.setSubsName(subscNameList.get(auth));
                    corpSubsProvInfoUtil.bitsToIntegerConversion(mcpttPermissions, targetMdnPermBitInfo);
                    targetMdnPermBitInfos.add(targetMdnPermBitInfo);
                });
                knLogger.debug(methodName, "targetMdnPermBitInfos :", targetMdnPermBitInfos);
                respDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
            }

            populate(respDTO);
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in getAuthorizedMdnList  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in getAuthorizedMdnList ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO sendTrkMaterial(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "sendTrkMaterial(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = ipSubscriberInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            String subscriberEmail = null;
            Integer mode = 0;
            KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
            subscriberDTO.setMdn(ipSubscriberInfoDTO.getMdn());
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipSubscriberInfoDTO.getCustomParamMap();
            if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipSubscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_SUBSCRIBER_DETAILS);
                hookIPDTO.setData(ipSubscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
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
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            if(ipSubscriberInfoDTO.getAliasMdn() != null){
                List<String> aliasMdnList = new ArrayList<>();
                aliasMdnList.add(ipSubscriberInfoDTO.getAliasMdn());
                Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "aliasMdnList: - ", KnGDPRTemplate.mdnList(aliasMdnList), "aliasMdnMap: ", KnGDPRTemplate.mdnMap(aliasMdnMap));
                if(!aliasMdnMap.isEmpty()){
                    ipSubscriberInfoDTO.setMdn(aliasMdnMap.get(ipSubscriberInfoDTO.getAliasMdn()));
                }
                persistDTO.setAliasMdnList(aliasMdnList);
                persistDTO.setAliasMdnMap(aliasMdnMap);
            }
            String mdn = ipSubscriberInfoDTO.getMdn();
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            if(mdn != null){
                if (ipSubscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                    contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHome, persisterTxn);
                }
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);
                persistDTO.setSubsCorpId(subscProfile.getCorpId());
                persistDTO.setMcpttCompliance(subscProfile.getMcpttCompliance());
                persistDTO.setAliasMdn(subscProfile.getAliasMdn());
                subscriberEmail = subscProfile.getUserId();
                respDTO.setMdn(subscProfile.getMdn());
                respDTO.setSubscriberEmailId(subscriberEmail);
                respDTO.setAliasMdn(subscProfile.getAliasMdn());
                respDTO.setMdnCorpId(subscProfile.getCorpId());
                respDTO.setMcpttCompliance(subscProfile.getMcpttCompliance());
            } else {
                persistDTO.setSubsCorpId(Integer.parseInt(corpId));
            }
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setSubscDto(subscriberDTO);
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

            populate(respDTO);
            knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while fetching subscriber information for sendTrkMaterial - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while fetching subscriber information for sendTrkMaterial - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }
    private boolean checkClientType(Integer clientType,int mcsCompliance)
    {
        return clientType == SUBSCR_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                || clientType == SUBSCR_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                || clientType == SUBSCR_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                || clientType == SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value()
                || clientType == SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                || mcsCompliance == MCPTT_COMPLIANCE;
    }


    @Override
    public KnCorpResponseDTO validateSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnValidationException {
        String methodName = "validateSubscrClient(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(ipSubscriberInfoDTO.getCorpId(), CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            int maxMembersAllowed = corpProfile.getMaxSubsAllowedGenActvReq();
            String xdmsHomePttId = corpProfile.getXdmsHome();

            KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
            corpMdnListPersistDto.setCorpId(Integer.parseInt(ipSubscriberInfoDTO.getCorpId()));
            corpMdnListPersistDto.setAddedMdnList(ipSubscriberInfoDTO.getMdnList());
            KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto, xdmsHomePttId, persisterTxn);
            Collection<String> pocMdnList = pocMdnPersistDto.getMdnList();
            Collection<KnCorpSubscriberDTO> extMdnList = pocMdnPersistDto.getExternalMdnList();
            knLogger.debug(methodName, "pocMdnList- ", pocMdnList);
            pocMdnPersistDto=contactInfoUtil.getPoCSubscriberDetails(ipSubscriberInfoDTO.getMdnList(),xdmsHomePttId,persisterTxn);
            persistDTO.setWebDispatcherEnabled(corpProfile.getWebDispatchEnabled());
            if(ipSubscriberInfoDTO.getDispatchType() != null){
                persistDTO.setWebDispatcherFlag(true);
            }

            persistDTO.setPocSubscMdnList(pocMdnList);
            persistDTO.setMdnClientTypeMap(pocMdnPersistDto.getMdnClientTypeMap());
            //
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setContactCountInRequest(ipSubscriberInfoDTO.getMdnList().size());
            persistDTO.setSubsCorpId(Integer.parseInt(ipSubscriberInfoDTO.getCorpId()));

            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);

           } catch (KnValidationException e) {
            knLogger.error( methodName, "Unexpected Exception occured while setting subscriber client settings - ");

            populate(respDTO, e);
            knLogger.error( methodName, "Validation Exception occured while setting subscriber client settings - "+respDTO);
        } catch (KnCorpBOException e) {
        knLogger.error(methodName, "KnCorpBOException occured while fetching subscriber information for sendTrkMaterial - ", e);
        populate(respDTO, e);
            knLogger.error( methodName, "KBO Exception occured while setting subscriber client settings - "+respDTO);
       } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while setting subscriber client settings - ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            knLogger.error( methodName, "KnEX Exception occured while setting subscriber client settings - "+respDTO);
        }

        return respDTO;

        }

    @Override
    public KnCorpResponseDTO validateGetSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnValidationException {
        String methodName = "validateGetSubscrClient(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details" + ipSubscriberInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(ipSubscriberInfoDTO.getCorpId(), CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            int maxMembersAllowed = corpProfile.getMaxSubsAllowedGenActvReq();
            String xdmsHomePttId = corpProfile.getXdmsHome();

            KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
            corpMdnListPersistDto.setCorpId(Integer.parseInt(ipSubscriberInfoDTO.getCorpId()));
            corpMdnListPersistDto.setAddedMdnList(ipSubscriberInfoDTO.getMdnList());
            KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto, xdmsHomePttId,true, persisterTxn);
            Collection<String> pocMdnList = pocMdnPersistDto.getMdnList();
            Collection<KnCorpSubscriberDTO> extMdnList = pocMdnPersistDto.getExternalMdnList();
            knLogger.debug(methodName, "pocMdnList- ", pocMdnList);
            pocMdnPersistDto = contactInfoUtil.getPoCSubscriberDetails(ipSubscriberInfoDTO.getMdnList(), xdmsHomePttId, true, persisterTxn);

            persistDTO.setPocSubscMdnList(pocMdnList);
            persistDTO.setMdnClientTypeMap(pocMdnPersistDto.getMdnClientTypeMap());
            persistDTO.setInputDTO(ipSubscriberInfoDTO);
            persistDTO.setContactCountInRequest(ipSubscriberInfoDTO.getMdnList().size());
            persistDTO.setSubsCorpId(Integer.parseInt(ipSubscriberInfoDTO.getCorpId()));

            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);

        } catch (KnValidationException e) {
            knLogger.error( methodName, "Unexpected Exception occured while setting subscriber client settings - ");
            populate(respDTO, e);
            knLogger.error( methodName, "Validation Exception occured while setting subscriber client settings - "+respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while fetching subscriber information - ", e);
            populate(respDTO, e);
            knLogger.error( methodName, "KBO Exception occured while setting subscriber client settings - "+respDTO);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while setting subscriber client settings - ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            knLogger.error( methodName, "KnEX Exception occured while setting subscriber client settings - "+respDTO);
        }
        knLogger.debug(methodName,"After FW validate " + respDTO);
        return respDTO;

    }

    @Override
    public KnCorpGetCorpFSResponse getCorporateFS(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getCorporateFS(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed ipSubscriberInfoDTO - ", ipSubscriberInfoDTO);

        KnCorpGetCorpFSResponse respDTO = new KnCorpGetCorpFSResponse();

        try {
            int corpId = Integer.valueOf(ipSubscriberInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(ipSubscriberInfoDTO.getCorpId(), CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            String corporateFS = commonInfoUtil.getCorporateFS(corpId, corpProfile.getXdmsHome(), true, persisterTxn);

            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            Boolean pttRecBit = KnGeneralUtil.getFeatureBitValue(corporateFS, FEATURE_SET.PTT_RECORDING_FLAG.value());
            Boolean dataRecBit = KnGeneralUtil.getFeatureBitValue(corporateFS, FEATURE_SET.DATA_RECORDING_FLAG.value());
            Boolean videoRecBit = KnGeneralUtil.getFeatureBitValue(corporateFS, FEATURE_SET.VIDEO_RECORDING_FLAG.value());
            Boolean selfDnDPrivilegeBit = KnGeneralUtil.getFeatureBitValue(corporateFS, FEATURE_SET.SELF_DND_PRIVILEGE.value());
            if (pttRecBit) {
                respDTO.setPttRecording(ENABLED_STRING);
            } else {
                respDTO.setPttRecording(DISABLED_STRING);
            }
            if (dataRecBit) {
                respDTO.setDataRecording(ENABLED_STRING);
            } else {
                respDTO.setDataRecording(DISABLED_STRING);
            }
            if (videoRecBit) {
                respDTO.setVideoRecording(ENABLED_STRING);
            } else {
                respDTO.setVideoRecording(DISABLED_STRING);
            }
            if (selfDnDPrivilegeBit) {
                respDTO.setSelfDnDPrivilege(ENABLED_STRING);
            } else {
                respDTO.setSelfDnDPrivilege(DISABLED_STRING);
            }

            populate(respDTO);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occurred while generating activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while generating activation code - ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        } finally {
            knLogger.debug(methodName, "Returning Response - ", respDTO);
            knLogger.info(methodName, "EXIT:");
        }
        return respDTO;
    }

    public KnCorpResponseDTO setUserProfileEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "setUserProfileEmergencyAttributes(KnIPEmergencyInfoDTO, KnPersisterTxn)";
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
            if (ipEmergencyInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipEmergencyInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SUBS_EMERGENCY_ATTRIBUTES);
                hookIPDTO.setData(ipEmergencyInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            String priDest = ipEmergencyInfoDTO.getPriDestination();
            String secDest = ipEmergencyInfoDTO.getSecDestination();
            LinkedList<String> destinations = new LinkedList<>();
            destinations.add(priDest);
            destinations.add(secDest);
            Collection<KnSubsDestEmergencyAttributes> insertToEmergAttributes = new ArrayList<>();
            Map<String, Integer> newPriority = new HashMap<>();
            if(ipEmergencyInfoDTO.getEmergDestType() == DESTINATION_TYPE.CAT_CONFIGURED_DESTINATION.value()
                    && ipEmergencyInfoDTO.getEmergInitPermission() != DISABLED){
                KnCorpSubscriberDTO corpsubsriber=contactInfoUtil.selectPocSubscriberInfo(mdn, xdmsHome, persisterTxn);
                String userProfileId = corpsubsriber.getUserProfileId();
                knLogger.info(methodName, " userProfileId:", userProfileId);
                ArrayList<String> upmId = new ArrayList<>();
                upmId.add(userProfileId);
                Map<String, Integer> upmOwnerList = userProfileUtil.getUserProfileOwnerinfo(upmId, xdmsHome, persisterTxn);
                Integer groupOwnerCorpId=ipEmergencyInfoDTO.getCorpId();
                if(!upmOwnerList.isEmpty()){
                    groupOwnerCorpId=upmOwnerList.get(userProfileId);
                }
                knLogger.debug(methodName,"ownerCorpId :",groupOwnerCorpId);
                Map<String, Integer> groupIdTypesMap = groupInfoUtil.getValidGrpTypeInCorp(destinations, groupOwnerCorpId,
                        xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupIdTypesMap - ", groupIdTypesMap);
                //changes for group sharing
                //check whether for the given corpId any shared groups are avilable or not
                //if any shared groups are avilable then add shared group info to dbGroupList to avoid rules
                Set<Integer> sharedCorpGrpIds  = new HashSet<>(1);
                Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap = groupInfoUtil.selectGroupSharedCorpInfoBySharedCorpId(groupOwnerCorpId,xdmsHome,persisterTxn);

                if(sharedCorpGrpInfoMap!=null && !sharedCorpGrpInfoMap.isEmpty()) {
                    sharedCorpGrpIds = sharedCorpGrpInfoMap.keySet();
                    knLogger.debug(methodName, "sharedCorpGrpIds", sharedCorpGrpIds);
                    sharedCorpGrpIds.forEach(grpId -> groupIdTypesMap.put(String.valueOf(grpId),1));

                }
                knLogger.debug(methodName, "groupIdTypesMap - ", groupIdTypesMap);


                /**
                 * This logic currently applicable for hiereiarchy path only incase if the requests are coming from hieriarcy path
                 */
                knLogger.debug(methodName, "isHierarchyCall - ", isHierarchyCall);
                if(isHierarchyCall) {
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
                    if(groupType != BROADCAST_GROUP){
                        validGroupId.add(Integer.parseInt(groupId));
                    }
                });
                List<Integer> mcxGroupIds=new ArrayList<Integer>();
                Map<String, String> mdnExistenceInGroup =new HashMap<String, String>();

                ArrayList<KnCorpGroupDTO> groupDetailsList=    groupInfoUtil.getGroupBasicInfoList(validGroupId, xdmsHome, persisterTxn);
                if(groupDetailsList!=null) {
                    for(KnCorpGroupDTO knCorpGroupDTO:groupDetailsList) {
                        if(knCorpGroupDTO.getMcxGrpInd()==1) {

                            mcxGroupIds.add(knCorpGroupDTO.getGroupId());
                            mdnExistenceInGroup.put(String.valueOf(knCorpGroupDTO.getGroupId()),mdn);
                        }
                    }
                }
                if(!mcxGroupIds.isEmpty()) {

                    if(corpsubsriber!=null&&corpsubsriber.getUserProfileId()!=null) {

                        Set<KnCorpGroupListInfoDTO> knCorpGroupListInfoDTOList= userProfileUtil.retriveGroupProfileInfoByProfileId(corpsubsriber.getUserProfileId(), xdmsHome, persisterTxn);
                        if(knCorpGroupListInfoDTOList!=null) {

                            for(KnCorpGroupListInfoDTO corpGrpinfo:knCorpGroupListInfoDTOList) {
                                if(mcxGroupIds.contains(corpGrpinfo.getGroupID()) && mdnExistenceInGroup.get(String.valueOf(corpGrpinfo.getGroupID()))==null) {

                                    mdnExistenceInGroup.put(String.valueOf(corpGrpinfo.getGroupID()),mdn);


                                }
                            }
                        }
                    }
                }
                Map<String, String> mdnExistenceInNormalGroup = groupInfoUtil.selectGroupMemberForGroupIds(validGroupId, mdn, xdmsHome, persisterTxn);
                mdnExistenceInGroup.putAll(mdnExistenceInNormalGroup);
                knLogger.debug(methodName, "mdnExistenceInGroup - ", KnGDPRTemplate.mdnMap(mdnExistenceInGroup));
                Collection<String> mdnList = new ArrayList<>();
                mdnList.add(mdn);
                Map<String, Collection<String>> mdnContactList = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "mdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(mdnContactList));
                int priority = 0;
                KnSubsDestEmergencyAttributes destEmergencyAttributes = null;
                for(String dest : destinations){
                    if(mdnExistenceInGroup != null && mdnExistenceInGroup.keySet().contains(dest)){
                        destEmergencyAttributes = new KnSubsDestEmergencyAttributes();
                        destEmergencyAttributes.setMdn(mdn);
                        destEmergencyAttributes.setEmergDestPriority(++priority);
                        destEmergencyAttributes.setEmergDestTypeMgmt(DESTINATION_TYPE_MGMT.GROUP.value());
                        destEmergencyAttributes.setEmergDest(dest);
                        insertToEmergAttributes.add(destEmergencyAttributes);
                        newPriority.put(dest, destEmergencyAttributes.getEmergDestPriority());
                    } else if(mdnContactList != null && mdnContactList.get(mdn) != null && mdnContactList.get(mdn).contains(dest)){
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
            //validatorFW.validate(emergencyMcpttFeaturePersistDTO);
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributes = corpSubsProvInfoUtil.getEmergAttributes(mdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "mdnEmergAttributes - ", mdnEmergAttributes);
            Map<String, Integer> oldPriority = mdnEmergAttributes.stream().collect(Collectors.toMap(KnSubsDestEmergencyAttributes::getEmergDest, KnSubsDestEmergencyAttributes::getEmergDestPriority));
            KnSubsEmergencyAttributes subsEmergencyAttributes = corpSubsProvInfoUtil.getEmergSubsAttributes(mdn, ipEmergencyInfoDTO.getCorpId(), xdmsHome, false, persisterTxn);
            knLogger.debug(methodName, "subsEmergencyAttributes - ", subsEmergencyAttributes);
            KnSubsEmergencyAttributes updateSubsEmergencyAttributes = null;
            boolean subsProfileChanged = false;
            if (mdnEmergAttributes.size() > 0) {
                corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdn, xdmsHome, persisterTxn);
            }
            if (ipEmergencyInfoDTO.getEmergInitPermission() == DISABLED) {
                updateSubsEmergencyAttributes = new KnSubsEmergencyAttributes();
                updateSubsEmergencyAttributes.setMdn(mdn);
                updateSubsEmergencyAttributes.setEmergInitPermission(ipEmergencyInfoDTO.getEmergInitPermission() != null ?
                        ipEmergencyInfoDTO.getEmergInitPermission() : subsEmergencyAttributes.getEmergInitPermission());
                updateSubsEmergencyAttributes.setEmergLmrBehaviour(ipEmergencyInfoDTO.getEmergLMRBehavior() != null ?
                        ipEmergencyInfoDTO.getEmergLMRBehavior() : subsEmergencyAttributes.getEmergLmrBehaviour());
                updateSubsEmergencyAttributes.setEmergOriginBitSet(ipEmergencyInfoDTO.getEmergOriginBitSet() != null ?
                        ipEmergencyInfoDTO.getEmergOriginBitSet() : subsEmergencyAttributes.getEmergOriginBitSet());
                updateSubsEmergencyAttributes.setEmergTermBitSet(ipEmergencyInfoDTO.getEmergTermBitSet() != null ?
                        ipEmergencyInfoDTO.getEmergTermBitSet() : subsEmergencyAttributes.getEmergTermBitSet());
            }
            if ((mdnEmergAttributes.size() > 0 && ipEmergencyInfoDTO.getEmergInitPermission() != DISABLED)
                    || (ipEmergencyInfoDTO.getEmergInitPermission() != DISABLED && mdnEmergAttributes.isEmpty())) {
                updateSubsEmergencyAttributes = new KnSubsEmergencyAttributes();
                updateSubsEmergencyAttributes.setMdn(mdn);
                updateSubsEmergencyAttributes.setEmergDestTypeIntf(ipEmergencyInfoDTO.getEmergDestType());
                updateSubsEmergencyAttributes.setEmergCallType(ipEmergencyInfoDTO.getEmergCallType());
                updateSubsEmergencyAttributes.setEmergCnclPermission(ipEmergencyInfoDTO.getEmergCancelPermission());
                if(ipEmergencyInfoDTO.getEmergLMRBehavior() != null){
                    updateSubsEmergencyAttributes.setEmergLmrBehaviour(ipEmergencyInfoDTO.getEmergLMRBehavior());
                } else {
                    updateSubsEmergencyAttributes.setEmergLmrBehaviour(subsEmergencyAttributes.getEmergLmrBehaviour());
                }
                if(ipEmergencyInfoDTO.getEmergInitPermission() != null && ipEmergencyInfoDTO.getEmergInitPermission() != EMERGENCY_INIT_NOT_MODIFIED){
                    updateSubsEmergencyAttributes.setEmergInitPermission(ipEmergencyInfoDTO.getEmergInitPermission());
                    subsProfileChanged = true;
                } else {
                    updateSubsEmergencyAttributes.setEmergInitPermission(subsEmergencyAttributes.getEmergInitPermission());
                }
                if(ipEmergencyInfoDTO.getEmergOriginBitSet() != null){
                    updateSubsEmergencyAttributes.setEmergOriginBitSet(ipEmergencyInfoDTO.getEmergOriginBitSet());
                } else {
                    updateSubsEmergencyAttributes.setEmergOriginBitSet(subsEmergencyAttributes.getEmergOriginBitSet());
                }
                if(ipEmergencyInfoDTO.getEmergTermBitSet() != null){
                    updateSubsEmergencyAttributes.setEmergTermBitSet(ipEmergencyInfoDTO.getEmergTermBitSet());
                } else {
                    updateSubsEmergencyAttributes.setEmergTermBitSet(subsEmergencyAttributes.getEmergTermBitSet());
                }
                updateSubsEmergencyAttributes.setEmergConfigTimer(ipEmergencyInfoDTO.getEmergConfigTimer() != null ?
                        ipEmergencyInfoDTO.getEmergConfigTimer() : subsEmergencyAttributes.getEmergConfigTimer());
            }
            corpSubsProvInfoUtil.updateToEmergSubsDestInfo(updateSubsEmergencyAttributes, xdmsHome, persisterTxn);
            if(!insertToEmergAttributes.isEmpty() ){
                corpSubsProvInfoUtil.insertIntoEmergSubsAttributes(insertToEmergAttributes, xdmsHome, persisterTxn);
            }

            respDTO.setProfileChanged(subsProfileChanged);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in setSubsEmergencyAttributes  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in setSubsEmergencyAttributes ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO setBulkTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "setBulkTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)";
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
            boolean isAllAuTask = ipAuthUserPermissionInfoDTO.isAllAuTask();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipAuthUserPermissionInfoDTO.getCustomParamMap();
            if (ipAuthUserPermissionInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(PERSISTER_TXN, persisterTxn);
                customParams.put(PTT_SERVER_ID, xdmsHome);
                ipAuthUserPermissionInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SET_AUTH_USER_PERMISSIONS);
                hookIPDTO.setData(ipAuthUserPermissionInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            Collection<String> targetMdnList = new ArrayList<>();
            Collection<String> authMdn = new ArrayList<>();
            authMdn.add(authorizedMdn);
            Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList();
            targetMdnList.addAll(targetMdnPermBitInfos.stream().map(KnTargetMdnPermBitInfo::getMdn).collect(Collectors.toList()));
            knLogger.debug(methodName, "targetMdnList - ", KnGDPRTemplate.mdnList(targetMdnList));
            Map<String, KnCorpSubscriberDTO> contDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(targetMdnList, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "contDetailsMap - ", KnGDPRTemplate.mapKeyMdn(contDetailsMap));
            String userProfileId = subscProfile.getUserProfileId();
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(authorizedMdn);
            //Map<String, Long> authEtagMap = corpSubsProvInfoUtil.seleteFromAuthDoc(mdnList, xdmsHome, persisterTxn);
            // Business Logics:
            Collection<KnMcpttPermissionDTO> insertToMcpttInfo = new ArrayList<>();
            Collection<KnMcpttPermissionDTO> updateToMcpttInfo = new ArrayList<>();
            Collection<String> targetMdnForDelete = new ArrayList<>();
            Map<String, Integer> targetForResourceListUpdate = new HashMap<>();

            Collection<KnMcpttPermissionDTO> insertToMcpttInfoForProfileMdns = new ArrayList<>();
            Collection<KnMcpttPermissionDTO> updateToMcpttInfoForProfileMdns = new ArrayList<>();
            Collection<String> targetMdnForDeleteForProfileMdns = new ArrayList<>();

            List<String> profileMdnList = new ArrayList<>();
            profileMdnList = ipAuthUserPermissionInfoDTO.getTargetProfileMdnPermissionBitInfoList().stream().map(KnTargetMdnPermBitInfo::getMdn).collect(Collectors.toList());

            List<String> baseTuList = new ArrayList<>(targetMdnList);
            Map<String, List<String>> targetBaseMdnsProfileMdnsMapping = userProfileUtil.getProfileMdnListByBaseMdnsList(baseTuList, xdmsHome, false, persisterTxn);


            Map<String, KnMcpttPermissionDTO> mcpttPermissionMap = corpSubsProvInfoUtil.getAuthUserPermissions(authorizedMdn, xdmsHome, persisterTxn);
            List<String> mdns = new ArrayList<>();
            knLogger.debug(methodName, "mcpttPermissionMap - ", mcpttPermissionMap);
            //adding profile mdns skipping au and tu contact validation.
            Collection<KnTargetMdnPermBitInfo> targetProfileMdnPermissionBitInfoList
                    = ipAuthUserPermissionInfoDTO.getTargetProfileMdnPermissionBitInfoList();
            knLogger.debug("targetProfileMdnPermissionBitInfoList :", targetProfileMdnPermissionBitInfoList);
            if (targetProfileMdnPermissionBitInfoList != null && !targetProfileMdnPermissionBitInfoList.isEmpty()) {
                targetMdnPermBitInfos.addAll(targetProfileMdnPermissionBitInfoList);


                targetMdnList.addAll(profileMdnList);
                knLogger.debug(methodName, "profileMdnList :-", KnGDPRTemplate.mdnList(profileMdnList));
                Map<String, KnCorpSubscriberDTO> subsDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(profileMdnList, xdmsHome, persisterTxn);
                contDetailsMap.putAll(subsDetailsMap);
            }
            Collection<String> insertTargetMdnList = new ArrayList<>(targetMdnList);
            knLogger.debug(methodName, "insertTargetMdnList :-", KnGDPRTemplate.mdnList(insertTargetMdnList));
            // Update
            Map<String, Integer> mapListForPrivacy = new HashMap<>();
            knLogger.debug(methodName, "---> corpProfile.getPrivacyAmbDiscListenFlag() - ", corpProfile.getPrivacyAmbDiscListenFlag());
            //update and delete
            List<String> privacyEnabledMdns = new ArrayList<>();

            //fetching only the list of common contact mdns
            List<String> allCommonContactMdns = sublistInfoUtil.getCommonContactListForMdns(authorizedMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "allCommonContactMdns :-", KnGDPRTemplate.mdnList(allCommonContactMdns));
            //fetching all the contacts of the subscriber duplicate also
            List<String> listOfAllContactMdns = sublistInfoUtil.getAllSublistContactMdns(authorizedMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "listOfAllContactMdns :-", KnGDPRTemplate.mdnList(listOfAllContactMdns));


            List<String> commonContactMdns = new ArrayList<>();
            for (KnTargetMdnPermBitInfo obj : targetMdnPermBitInfos) {
                if (!allCommonContactMdns.isEmpty() && allCommonContactMdns.contains(obj.getMdn())) {
                    commonContactMdns.add(obj.getMdn());
                }
            }
            knLogger.debug(methodName, "common contacts present in the request :-", KnGDPRTemplate.mdnList(commonContactMdns));
            Map<String, Integer> commonContactsAndCommonAUMapping = new HashMap<>();

            ////MINT-14813 - ConcurrentModificationException observed on authMdnsProfileMdns variable hence changed to CopyOnWriteArrayList
            List<String> authMdnProfileMdnList = corpSubsProvInfoUtil.getProfileMdnByBaseMdn(authorizedMdn, xdmsHome,
                    persisterTxn);
            CopyOnWriteArrayList<String> authMdnsProfileMdns = new CopyOnWriteArrayList<>(authMdnProfileMdnList);
            for (KnTargetMdnPermBitInfo target : targetMdnPermBitInfos) {
                if (commonContactMdns.contains(target.getMdn()) && mcpttPermissionMap.get(target.getMdn()) != null) {
                    Integer reqCommonAU = target.getCommonContact();
                    Integer dbCommonAU = mcpttPermissionMap.get(target.getMdn()).getCommonAu();
                    String targetMdn = target.getMdn();
                    if ((DISABLE.equals(reqCommonAU) && ENABLE.equals(dbCommonAU) && Collections.frequency(listOfAllContactMdns, target.getMdn()) > 1)) {
                        commonContactsAndCommonAUMapping.put(target.getMdn(), DISABLE);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), DISABLE);
                    } else if ((ENABLE.equals(reqCommonAU) && DISABLE.equals(dbCommonAU)) && commonContactMdns.contains(targetMdn)) {
                        commonContactsAndCommonAUMapping.put(target.getMdn(), ENABLE);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), ENABLE);
                    } else if ((ENABLE.equals(reqCommonAU) && ENABLE.equals(dbCommonAU)) || (reqCommonAU == null && ENABLE.equals(dbCommonAU))) {
                        //null
                        commonContactsAndCommonAUMapping.put(target.getMdn(), null);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), null);
                    }
                    knLogger.debug(methodName, "after the mapping population ", commonContactsAndCommonAUMapping);
                }
            }
            //update scenario for the upm call
            //fetching the auth permissinos for the aus profile mdns
            List<String> targetMdns = new ArrayList<>(mcpttPermissionMap.keySet());
            Map<String, List<KnMcpttPermissionDTO>> mcpttPermissionAsProfileMdns = corpSubsProvInfoUtil.getTargetMdnListPermissions(authMdnsProfileMdns, xdmsHome, persisterTxn);

            //aus profile
            //if that profile mdn is having permissions
            if (!commonContactMdns.isEmpty()) {
                for (String profileMdn : authMdnsProfileMdns) {
                    if (mcpttPermissionAsProfileMdns.get(profileMdn) != null) {
                        for (KnMcpttPermissionDTO perm : mcpttPermissionAsProfileMdns.get(profileMdn)) {
                            if (perm.getCommonAu() == 0) {
                                authMdnsProfileMdns.remove(profileMdn);
                            }
                        }
                    }
                }
            }


            if (mcpttPermissionMap != null && !mcpttPermissionMap.isEmpty()) {
                targetMdnPermBitInfos.stream().filter(target -> mcpttPermissionMap.get(target.getMdn()) != null && mcpttPermissionMap.keySet().contains(target.getMdn())).forEach(target -> {
                    KnMcpttPermissionDTO dbTargetInfo = mcpttPermissionMap.get(target.getMdn()); // this is the data from the DB
                    mdns.add(target.getMdn());
                    //logic for insertion case of tus
                    Integer reqCommonAU = target.getCommonContact();
                    Integer dbCommonAU = mcpttPermissionMap.get(target.getMdn()).getCommonAu();

                    BitSet targetDBPermsBit = featureSetUtil.convertLongToBitSet(dbTargetInfo.getMcpttPerms());
                    int existingAmbientBit = targetDBPermsBit.get(MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0;
                    if (target.getAmbientListening() != existingAmbientBit) {
                        targetForResourceListUpdate.put(target.getMdn(), target.getAmbientListening());
                    }
                    corpSubsProvInfoUtil.integerToBitsConversion(targetDBPermsBit, target);
                    if (target.getDiscreteListening() == 0) {
                        dbTargetInfo.setDiscreteEnabled(DISABLED);
                    } else {
                        dbTargetInfo.setDiscreteEnabled(dbTargetInfo.getDiscreteEnabled());
                    }

                    if (commonContactsAndCommonAUMapping.containsKey(target.getMdn())) {
                        if (commonContactsAndCommonAUMapping.get(target.getMdn()) == null) {
                            dbTargetInfo.setCommonAu(dbCommonAU);
                        } else {
                            dbTargetInfo.setCommonAu(commonContactsAndCommonAUMapping.get(target.getMdn()));
                        }
                    }
                    if (isUpmCall && ENABLE.equals(dbCommonAU) && reqCommonAU == null) {

                        dbTargetInfo.setCommonAu(null);
                    }

                    long targetBit = featureSetUtil.convertBitSetToLong(targetDBPermsBit);
                    if (targetBit != 0) {
                        if (dbTargetInfo.getMcpttPerms() != targetBit || (!Objects.equals(reqCommonAU, dbCommonAU))) {
                            dbTargetInfo.setMcpttPerms(targetBit);
                            dbTargetInfo.setServiceAuthUserAU(contDetailsMap.get(target.getMdn()).getServiceAuthStatusAU());
                            updateToMcpttInfo.add(dbTargetInfo);
                            if (commonContactsAndCommonAUMapping.containsKey(target.getMdn()) && dbTargetInfo.getCommonAu() == 1) {
                                updateToMcpttInfoForProfileMdns.add(dbTargetInfo);
                            }
                        }
                    } else {
                        if (!(isUpmCall && ENABLE.equals(dbCommonAU) && reqCommonAU == null)) {
                            targetMdnForDelete.add(target.getMdn());
                            if (commonContactsAndCommonAUMapping.containsKey(target.getMdn())) {
                                targetMdnForDeleteForProfileMdns.add(target.getMdn());
                            }
                        }
                    }
                    if (DISABLE.equals(commonContactsAndCommonAUMapping.get(target.getMdn()))) {
                        targetMdnForDeleteForProfileMdns.add(target.getMdn());
                    }
                    if (ENABLE.equals(commonContactsAndCommonAUMapping.get(target.getMdn()))) {
                        insertToMcpttInfoForProfileMdns.add(dbTargetInfo);
                    }

                    insertTargetMdnList.remove(target.getMdn());
                    if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1 && contDetailsMap.get(target.getMdn()) != null && contDetailsMap.get(target.getMdn()).getClientPVmajorVer() >= PROTOCOL_VERSION_16) {
                        knLogger.debug(methodName, "--->target.getAmbientListening() - ", target.getAmbientListening());
                        knLogger.debug(methodName, "--->target.getDiscreteListening() - ", target.getDiscreteListening());
                        mapListForPrivacy.put(target.getMdn(), (target.getAmbientListening() == ENABLED
                                || target.getDiscreteListening() == ENABLED) ? DISABLED : ENABLED);
                        if (mapListForPrivacy.get(target.getMdn()) == DISABLED) {
                            knLogger.debug(methodName, "--->privacyFlag - ", mapListForPrivacy.get(target.getMdn()), "target.getMdn()-" + KnGDPRTemplate.mdn(target.getMdn()));
                            privacyEnabledMdns.add(target.getMdn());
                        }
                    }
                });
                knLogger.debug(methodName, "updateToMcpttInfo - ", updateToMcpttInfo); // update to MCPTT.
                knLogger.debug(methodName, "targetMdnForDelete - ", KnGDPRTemplate.mdnList(targetMdnForDelete)); // delete from MCPTT and Authorization.
                knLogger.debug(methodName, "targetForResourceListUpdate - ", targetForResourceListUpdate); // update to CorpResourceList

                knLogger.debug(methodName, "updateToMcpttInfoForProfileMdns - ", updateToMcpttInfoForProfileMdns); // update to MCPTT.
                knLogger.debug(methodName, "deleteFromMcpttPermInfoForProfileMdns - ", KnGDPRTemplate.mdnList(targetMdnForDeleteForProfileMdns));
                if (!updateToMcpttInfo.isEmpty()) {
                    corpSubsProvInfoUtil.updateToMcpttPermInfo(updateToMcpttInfo, xdmsHome, persisterTxn);
                }
                if (!updateToMcpttInfoForProfileMdns.isEmpty()) {
                    corpSubsProvInfoUtil.updateToMcpttPermInfoForProfileMdns(updateToMcpttInfoForProfileMdns, authMdnsProfileMdns, xdmsHome, persisterTxn);
                }
                if (!targetMdnForDelete.isEmpty()) {
                    corpSubsProvInfoUtil.deleteFromMcpttPermInfo(authorizedMdn, targetMdnForDelete, xdmsHome, persisterTxn);
                }
                if (!targetMdnForDeleteForProfileMdns.isEmpty()) {
                    corpSubsProvInfoUtil.deleteFromMcpttPermInfoForProfileMdns(authMdnsProfileMdns, targetMdnForDeleteForProfileMdns, xdmsHome, persisterTxn);
                }
            }
            // Insert
            commonContactsAndCommonAUMapping.clear();
            for (KnTargetMdnPermBitInfo insertTarget : targetMdnPermBitInfos) {
                if ((ENABLE.equals(insertTarget.getCommonContact()) &&
                        commonContactMdns.contains(insertTarget.getMdn())) ||
                        (commonContactMdns.contains(insertTarget.getMdn()) &&
                                (Collections.frequency(listOfAllContactMdns, insertTarget.getMdn())) == 1)) {
                    commonContactsAndCommonAUMapping.put(insertTarget.getMdn(), 1);
                    commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(insertTarget.getMdn()), 1);
                }
            }
            knLogger.debug(methodName, "after the population ", commonContactsAndCommonAUMapping);
            knLogger.debug(methodName, "targetMdnPermBitInfos :", targetMdnPermBitInfos, " insertTargetMdnList :", KnGDPRTemplate.mdnList(insertTargetMdnList));
            targetMdnPermBitInfos.stream().filter(insertTarget -> insertTargetMdnList.contains(insertTarget.getMdn())).forEach(insertTarget -> {
                KnMcpttPermissionDTO insertPermissionDTO = new KnMcpttPermissionDTO();
                insertPermissionDTO.setCorpid(subscProfile.getCorpId()); //setting the authorized mdns corpid //sharing of roles
                insertPermissionDTO.setAuthMdn(authorizedMdn);
                insertPermissionDTO.setTargetMdn(insertTarget.getMdn());
                //write the default value for the column got introduced newly

                if (commonContactsAndCommonAUMapping.containsKey(insertTarget.getMdn())) {
                    insertPermissionDTO.setCommonAu(commonContactsAndCommonAUMapping.get(insertTarget.getMdn()));
                }
                mdns.add(insertTarget.getMdn());
                BitSet targetInsertBitSet = new BitSet();
                corpSubsProvInfoUtil.integerToBitsConversion(targetInsertBitSet, insertTarget);
                if (insertTarget.getDiscreteListening() == 0) {
                    insertPermissionDTO.setDiscreteEnabled(DISABLED);
                }
                long permBit = featureSetUtil.convertBitSetToLong(targetInsertBitSet);
                if (permBit != 0) {
                    targetForResourceListUpdate.put(insertTarget.getMdn(), targetInsertBitSet.get(MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
                    insertPermissionDTO.setMcpttPerms(permBit);
                    KnCorpSubscriberDTO subscriberDTO = contDetailsMap.get(insertTarget.getMdn());
                    if (subscriberDTO != null) {
                        insertPermissionDTO.setServiceAuthUserAU(subscriberDTO.getServiceAuthStatusAU());
                        insertToMcpttInfo.add(insertPermissionDTO);
                        if (commonContactsAndCommonAUMapping.containsKey(insertTarget.getMdn())) {
                            insertToMcpttInfoForProfileMdns.add(insertPermissionDTO);
                        }
                    } else {
                        knLogger.info(methodName, "subscriber info not found for mdn -", KnGDPRTemplate.mdn(insertTarget.getMdn()));
                    }
                }
                if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1 && contDetailsMap.get(insertTarget.getMdn()) != null && contDetailsMap.get(insertTarget.getMdn()).getClientPVmajorVer() >= PROTOCOL_VERSION_16) {
                    knLogger.debug(methodName, "--->insertTarget.getAmbientListening() - ", insertTarget.getAmbientListening());
                    knLogger.debug(methodName, "--->insertTarget.getDiscreteListening() - ", insertTarget.getDiscreteListening());
                    mapListForPrivacy.put(insertTarget.getMdn(), (insertTarget.getAmbientListening() == ENABLED
                            || insertTarget.getDiscreteListening() == ENABLED) ? DISABLED : ENABLED);
                    if (mapListForPrivacy.get(insertTarget.getMdn()) == DISABLED) {
                        knLogger.debug(methodName, "--->privacyFlag - ", mapListForPrivacy.get(insertTarget.getMdn()), "insertTarget.getMdn()-" + KnGDPRTemplate.mdn(insertTarget.getMdn()));
                        privacyEnabledMdns.add(insertTarget.getMdn());
                    }
                }
            });
            knLogger.debug(methodName, "insertToMcpttInfo - ", insertToMcpttInfo); // insert to CorpResourceList
            knLogger.debug(methodName, "insertToMcpttInfoForProfileMdns - ", insertToMcpttInfoForProfileMdns);
            if (!insertToMcpttInfo.isEmpty()) {
                corpSubsProvInfoUtil.insertIntoMcpttPermInfo(insertToMcpttInfo, xdmsHome, persisterTxn);
            }
            if (!insertToMcpttInfoForProfileMdns.isEmpty()) {
                corpSubsProvInfoUtil.insertIntoMcpttPermInfoForProfileMdns(insertToMcpttInfoForProfileMdns, authMdnsProfileMdns, xdmsHome, persisterTxn);
            }

            Map<String, Collection<KnMcpttPermissionDTO>> insertTargetMdnListMap = new HashMap<>();
            Map<String, Collection<KnMcpttPermissionDTO>> updateToMcpttInfoMap = new HashMap<>();
            if (isUpmCall && !targetMdnForDelete.isEmpty()) {
                Set<String> emergencyBaseMdn = corpSubsProvInfoUtil.getBaseMdnByProfileMdns(new ArrayList<>(authMdn), xdmsHome, persisterTxn);
                Map<String, KnMcpttPermissionDTO> mcpttPermissions = corpSubsProvInfoUtil.getAuthUserPermissions(emergencyBaseMdn.iterator().next(), xdmsHome, persisterTxn);
                List<KnMcpttPermissionDTO> permissions = new ArrayList<>(mcpttPermissions.values());
                if (!permissions.isEmpty()) {
                    Predicate<KnMcpttPermissionDTO> removeNonCommonMdns = perm -> !(perm.getCommonAu() != null && perm.getCommonAu() == 1);
                    permissions.removeIf(removeNonCommonMdns);
                    Predicate<KnMcpttPermissionDTO> removeMdnsExeptDeletedTu = perm -> !(targetMdnForDelete.contains(perm.getTargetMdn()));
                    permissions.removeIf(removeMdnsExeptDeletedTu);
                    List<String> profileMdn = new ArrayList<>();
                    profileMdn.add(authorizedMdn);
                    corpSubsProvInfoUtil.insertIntoMcpttPermInfoForProfileMdns(permissions, profileMdn, xdmsHome, persisterTxn);
                    //insertTargetMdnListMap.put(authorizedMdn,permissions);
                    if (!permissions.isEmpty()) {
                        updateToMcpttInfoMap.put(authorizedMdn, permissions);
                        permissions.forEach(perm -> targetMdnForDelete.remove(perm.getTargetMdn()));
                    }

                }
                //for notifiy
            }
            knLogger.info(methodName, "mapListForPrivacy:: ", mapListForPrivacy);
            if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1) {
                if (isAllAuTask) {
                    //for KnAssignTargetPermsToAllAUTask case when tu profile mdn is set to au
                    //PRIVACY_OPT_STATUS should be copied from base mdn.to keep the flag sync for both.
                    //Find the Real Mdns for the profile Mdns
                    ArrayList<String> permProfileMdnList = new ArrayList<>(mapListForPrivacy.keySet());
                    List<String> realMdns = groupInfoUtil.getRealMdns(permProfileMdnList, xdmsHome, persisterTxn);
                    //getting map of profile to base mdn.
                    //Map<String, List<String>> profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns, xdmsHome, persisterTxn);
                    Map<String, Integer> mdnPrivOptsMap = corpSubsProvInfoUtil.getPrivacyOptStatus(realMdns, xdmsHome, persisterTxn);
                    //As only one profile mdn will come in the request in this flow,so get by index and override the opts value for profile mdn.
                    Integer mdnOpts = mdnPrivOptsMap.get(realMdns.get(0));
                    knLogger.debug(methodName, " Base mdn mdnOpts :", mdnOpts);
                    mapListForPrivacy.put(permProfileMdnList.get(0), mdnOpts);
                }
                corpSubsProvInfoUtil.updateToPrivacyOptStatus(mapListForPrivacy, xdmsHome, persisterTxn);
            }
            mdns.add(authorizedMdn);
            Map<String, KnCorpSubsEntitiesDTO> corpSubsEntitiesDTOMap = contactInfoUtil.getSubsEntitiesDetails(mdns, xdmsHome, persisterTxn);

            updateToMcpttInfoMap.put(authorizedMdn, updateToMcpttInfo);

            insertTargetMdnListMap.put(authorizedMdn, insertToMcpttInfo);
            Map<String, Collection<String>> targetMdnForDeleteMap = new HashMap<>();
            targetMdnForDeleteMap.put(authorizedMdn, targetMdnForDelete);
            //notifications for the profile mdns
            if (!insertToMcpttInfoForProfileMdns.isEmpty()) {
                authMdnsProfileMdns.forEach(profileMdn -> insertTargetMdnListMap.put(profileMdn, insertToMcpttInfoForProfileMdns));
            }
            if (!updateToMcpttInfoForProfileMdns.isEmpty()) {
                authMdnsProfileMdns.forEach(profileMdn -> updateToMcpttInfoMap.put(profileMdn, updateToMcpttInfoForProfileMdns));
            }
            if (!targetMdnForDeleteForProfileMdns.isEmpty()) {
                authMdnsProfileMdns.forEach(profileMdn -> targetMdnForDeleteMap.put(profileMdn, targetMdnForDeleteForProfileMdns));
            }
            knLogger.debug(methodName, "--->privacyEnabledMdns - ", KnGDPRTemplate.mdnList(privacyEnabledMdns));

            populate(respDTO);

            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in setBulkTargetPermissions  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in setBulkTargetPermissions ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    /**
     * Allocates subscribers to a corporation based on the provided allocation details.
     * This method performs the following:
     * - Validates the mapping between the corporation and hierarchy.
     * - Extracts geocodes and MDNs from the input DTO for further validation.
     * - Handles the allocation logic for the given subscribers.
     *
     * @param allocateSubscriberDTO DTO containing subscriber allocation details.
     * @param persisterTxn          Database transaction object.
     * @return KnCorpResponseDTO Response containing the result of the allocation operation.
     */
    public KnCorpResponseDTO allocateSubs(KnIPAllocateSubscriberDTO allocateSubscriberDTO, KnPersisterTxn persisterTxn) {
        String methodName = "allocateSubs(KnIPAllocateSubscriberDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", allocateSubscriberDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            if (allocateSubscriberDTO == null) {
                throw new KnCorpBOException(INVALID_REQUEST, "allocateSubscriberDTO is null");
            }
            String corpId = allocateSubscriberDTO.getCorpId();
            String hierarchyId = allocateSubscriberDTO.getHierarchyId();
            boolean isHierarchyCorpMapped = false;
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            String hierachyRoot= KnCorpCommonInfoUtil.getHierachyRoot(Integer.parseInt(corpId), hierarchyId, persisterTxn,xdmPttServerId);
            knLogger.info(methodName, "==>hierachyRoot : ", hierachyRoot);
            isHierarchyCorpMapped = commonInfoUtil.isCorpHierarchyMapped(Integer.parseInt(corpId), hierarchyId, xdmPttServerId, persisterTxn);
            Map<String, Set<String>> hierarchyGeoMappedGeocode = commonInfoUtil.getHierarchyGeoMappedGeocode(Integer.parseInt(corpId), hierarchyId, persisterTxn, xdmPttServerId);
            //Extracting geocodes and mdns for validation
            List<KnIPAllocateSubscriberDTO.KnMdnDetailsDTO> mdnGeoCodeList = allocateSubscriberDTO.getMdnList();
            if (mdnGeoCodeList == null || mdnGeoCodeList.isEmpty()) {
                throw new KnCorpBOException(INVALID_REQUEST, "MDN list is null or empty");
            }
            mdnGeoCodeList = mdnGeoCodeList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (mdnGeoCodeList.isEmpty()) {
                throw new KnCorpBOException(INVALID_REQUEST, "MDN list contains only null entries");
            }
            Set<String> reqGeoCodes = mdnGeoCodeList.stream()
                    .map(KnIPAllocateSubscriberDTO.KnMdnDetailsDTO::getGeoCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            knLogger.debug(methodName, "Extracted GeoCodes for validation: ", reqGeoCodes);
            List<String> reqMdns = mdnGeoCodeList.stream()
                    .map(KnIPAllocateSubscriberDTO.KnMdnDetailsDTO::getMdn)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (reqMdns.isEmpty()) {
                throw new KnCorpBOException(INVALID_REQUEST, "No MDNs found in request");
            }
            knLogger.debug(methodName, "Extracted MDNs for validation: ", KnGDPRTemplate.mdnList(reqMdns));

            // Separate MDNs with and without geocodes
            List<String> mdnsWithoutGeoCode = mdnGeoCodeList.stream()
                    .filter(mdnDetails -> mdnDetails.getGeoCode() == null && mdnDetails.getMdn() != null && !mdnDetails.getMdn().isEmpty())
                    .flatMap(mdnDetails -> mdnDetails.getMdn().stream())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<String> mdnsWithGeoCode = mdnGeoCodeList.stream()
                    .filter(mdnDetails -> mdnDetails.getGeoCode() != null && mdnDetails.getMdn() != null && !mdnDetails.getMdn().isEmpty())
                    .flatMap(mdnDetails -> mdnDetails.getMdn().stream())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!mdnsWithoutGeoCode.isEmpty()) {
                knLogger.info(methodName, "MDNs without geoCode will only update hierarchyId: ", KnGDPRTemplate.mdnList(mdnsWithoutGeoCode));
            }

            Map<String, KnCorpSubscriberDTO> subsDetails = commonInfoUtil.getSubscriberDetails(reqMdns, xdmPttServerId, true, persisterTxn);
            Collection<String> mdnListInDb = new ArrayList<String>();
            for (KnCorpSubscriberDTO subsDTO : subsDetails.values()) {
                mdnListInDb.add(subsDTO.getMdn());
            }

            // Check if all existing subscribers can legally be moved to the requested hierarchyId.
            // A subscriber currently in hierarchy H can be moved to any descendant of H (or H itself).
            // We query the ancestor set of the target hierarchy once and check each subscriber's
            // current hierarchy against it — O(depth) DB call, not O(descendants).
            boolean isHierarchyIdMatching = true;
            if (!subsDetails.isEmpty()) {
                Map<String, Integer> ancestorMap = null;
                for (KnCorpSubscriberDTO subscriber : subsDetails.values()) {
                    String dbHierarchyId = subscriber.getHierarchyId();
                    if (dbHierarchyId != null && !dbHierarchyId.trim().isEmpty()
                            && !dbHierarchyId.equals(hierarchyId)) {
                        // Lazy-fetch ancestors of target hierarchy on first mismatch
                        if (ancestorMap == null) {
                            ancestorMap = commonInfoUtil.fetchAllHierarchyWithDepth(
                                    corpId, hierarchyId, xdmPttServerId, persisterTxn);
                            knLogger.debug(methodName, "Fetched ancestor map for hierarchyId: ", hierarchyId, " -> ", ancestorMap.keySet());
                            if (ancestorMap.isEmpty()) {
                                // Target hierarchy has no depth records — invalid hierarchy or missing self-reference row
                                isHierarchyIdMatching = false;
                                knLogger.error(methodName, "Target hierarchyId: ", hierarchyId,
                                        " has no records in CORP_HIERARCHY_DEPTH - hierarchy may not exist or depth table is incomplete");
                                break;
                            }
                        }
                        if (!ancestorMap.containsKey(dbHierarchyId)) {
                            isHierarchyIdMatching = false;
                            knLogger.debug(methodName, "HierarchyId mismatch - subscriber DB hierarchyId: ", dbHierarchyId,
                                    " is not an ancestor of request hierarchyId: ", hierarchyId);
                            break;
                        }
                        knLogger.debug(methodName, "HierarchyId descendant check passed - DB: ", dbHierarchyId,
                                " is ancestor of request: ", hierarchyId);
                    }
                }
            } else {
                knLogger.debug(methodName, "No existing subscribers found, allowing request hierarchyId: ", hierarchyId);
            }

            allocateSubscriberDTO.setReqMdnList(reqMdns);
            allocateSubscriberDTO.setReqGeoCodes(reqGeoCodes);

            knLogger.info(methodName, "MDN allocation split - With geoCode: {}, Without geoCode: {}", 
                    mdnsWithGeoCode.size(), mdnsWithoutGeoCode.size());

            // Track MDNs that fail in PATH 1 for proper error reporting
            List<String> failedGeocodedMdns = new ArrayList<>();
            String geoCodedFailureMsg = null;

            // === PROCESSING PATH 1: MDNs with geocodes (full allocation) ===
            // This path is independent with error handling and won't block non-geocoded MDN processing
            if (!reqGeoCodes.isEmpty() && !mdnsWithGeoCode.isEmpty()) {
                try {
                    knLogger.info(methodName, "Processing {} MDNs with geocodes for full allocation", mdnsWithGeoCode.size());
                    
                    //geoCode and clusterId map
                    Map<String, Integer> clusterIdMap = commonInfoUtil.getClusterId(reqGeoCodes, xdmPttServerId, persisterTxn);
                    Collection<String> dbGeoCodeList = new ArrayList<>(clusterIdMap.keySet());
                    Set<String> hierarchyGeocodeList = hierarchyGeoMappedGeocode.get(hierarchyId);
                    // Removing Geocode which is not present in for the corporate
                    hierarchyGeocodeList.removeIf(gc -> !dbGeoCodeList.contains(gc));
                    knLogger.debug(methodName, "GeoCode List for Validation: DB Geocode ", dbGeoCodeList, "HierarchyGeoCode:", hierarchyGeocodeList);

                    // Filter mdnListInDb to only include MDNs with geocodes for validation
                    Collection<String> mdnListInDbWithGeoCode = mdnListInDb.stream()
                            .filter(mdnsWithGeoCode::contains)
                            .collect(Collectors.toList());

                    // Create filtered allocation DTO containing only geocoded MDN entries ===
                    KnIPAllocateSubscriberDTO allocateDTOForGeoCoded = new KnIPAllocateSubscriberDTO();
                    allocateDTOForGeoCoded.setAuthDTO(allocateSubscriberDTO.getAuthDTO());
                    allocateDTOForGeoCoded.setEntityId(allocateSubscriberDTO.getEntityId());
                    allocateDTOForGeoCoded.setOperationType(allocateSubscriberDTO.getOperationType());
                    allocateDTOForGeoCoded.setClientType(allocateSubscriberDTO.getClientType());
                    allocateDTOForGeoCoded.setProfile(allocateSubscriberDTO.getProfile());
                    allocateDTOForGeoCoded.setPerformer(allocateSubscriberDTO.getPerformer());
                    allocateDTOForGeoCoded.setCorpId(allocateSubscriberDTO.getCorpId());
                    allocateDTOForGeoCoded.setCustomParamMap(allocateSubscriberDTO.getCustomParamMap());
                    allocateDTOForGeoCoded.setHierarchyType(allocateSubscriberDTO.getHierarchyType());
                    allocateDTOForGeoCoded.setHierarchyId(allocateSubscriberDTO.getHierarchyId());
                    
                    // Extract only MDN details that have geocodes
                    List<KnIPAllocateSubscriberDTO.KnMdnDetailsDTO> geocodedMdnDetails = mdnGeoCodeList.stream()
                            .filter(mdnDetails -> mdnDetails.getGeoCode() != null)
                            .collect(Collectors.toList());
                    allocateDTOForGeoCoded.setMdnList(geocodedMdnDetails);
                    
                    // Set only geocodes that are present (excluding nulls)
                    Set<String> geocodesForValidation = new HashSet<>();
                    for (KnIPAllocateSubscriberDTO.KnMdnDetailsDTO mdnDetail : geocodedMdnDetails) {
                        if (mdnDetail.getGeoCode() != null && !mdnDetail.getGeoCode().trim().isEmpty()) {
                            geocodesForValidation.add(mdnDetail.getGeoCode());
                        }
                    }
                    allocateDTOForGeoCoded.setReqGeoCodes(geocodesForValidation);
                    allocateDTOForGeoCoded.setReqMdnList(mdnsWithGeoCode);

                    KnAllocateSubsPersistDTO valPersistDTO = new KnAllocateSubsPersistDTO();
                    valPersistDTO.setPocSubscMdnList(mdnListInDbWithGeoCode);
                    valPersistDTO.setGeoCodes(hierarchyGeocodeList);
                    valPersistDTO.setHierarchyCorpMapped(isHierarchyCorpMapped);
                    valPersistDTO.setHierarchyIdMatching(isHierarchyIdMatching);
                    // Use the filtered DTO containing only geocoded entries
                    valPersistDTO.setInputDTO(allocateDTOForGeoCoded);
                    
                    knLogger.debug(methodName, "Invoking ValidationFW for geocoded MDNs. DTO - ", valPersistDTO);
                    validatorFW.validate(valPersistDTO);
                    knLogger.info(methodName, "Geocoded MDN validation completed successfully.");

                    Map<Integer, Integer> countPerClusterMap = new HashMap<>();
                    for(KnIPAllocateSubscriberDTO.KnMdnDetailsDTO mdnDetailsDTO : mdnGeoCodeList){
                        if(mdnDetailsDTO != null){
                            if(mdnDetailsDTO.getGeoCode() != null && mdnDetailsDTO.getMdn() != null
                                    && !mdnDetailsDTO.getMdn().isEmpty() && clusterIdMap.containsKey(mdnDetailsDTO.getGeoCode())){
                                Integer clusterId = clusterIdMap.get(mdnDetailsDTO.getGeoCode());
                                int currentSubsCount = countPerClusterMap.getOrDefault(clusterId, 0);
                                countPerClusterMap.put(clusterId, currentSubsCount + mdnDetailsDTO.getMdn().size());
                            }
                        }
                    }

                    List<Integer> clusterIds = new ArrayList<>(clusterIdMap.values());
                    //clusterId-pochome map from AnchorPocInfo table
                    Map<Integer, String> pocHomeClusterIdMap = commonInfoUtil.getPocHome(Integer.parseInt(corpId), allocateSubscriberDTO.getHierarchyId(), clusterIds, xdmPttServerId, persisterTxn);
                    int subscriberCount = mdnsWithGeoCode.size(); // Use only MDNs with geocodes for count
                    // Iterate over all clusterIds and update missing PoC Home
                    for (Integer clusterId : clusterIds) {
                        String pocHomeValue = pocHomeClusterIdMap.get(clusterId);
                        int subsCountForCluster = countPerClusterMap.getOrDefault(clusterId, 0);
                        knLogger.debug(methodName,"Processing clusterId: ", clusterId, " with PoC Home: ", pocHomeValue, " and subscriber count: ", subsCountForCluster);
                        if (subsCountForCluster <= 0) continue;
                        if (pocHomeValue == null || pocHomeValue.isEmpty()) {
                            // calc the PocHome for the clusterId
                            knLogger.debug(methodName,"PoC Home not found for clusterId: ", clusterId, ". Fetching from getSubsPoCHomeByClusterID.");
                            String fetchedPocHome = null;
                            fetchedPocHome = provInfoUtil.getSubsPoCHomeByClusterID(clusterId, subsCountForCluster, persisterTxn);
                            if (fetchedPocHome != null && !fetchedPocHome.isEmpty()) {
                                knLogger.debug(methodName,"Fetched PoC Home for clusterId: ", clusterId, " is ", fetchedPocHome, ". Inserting into AnchorPocInfo.");
                                commonInfoUtil.insertAnchorPocInfo(corpId, hierarchyId, clusterId, fetchedPocHome, xdmPttServerId, persisterTxn);
                                pocHomeClusterIdMap.put(clusterId, fetchedPocHome);
                            }
                        }else{
                            if(!provInfoUtil.isPocHomeValidForAllocation(pocHomeValue,subsCountForCluster,persisterTxn)){
                                knLogger.debug(methodName,"Existing PoC Home for clusterId: ", clusterId, " is invalid for allocation. Fetching new PoC Home.");
                                String fetchedPocHome = null;
                                fetchedPocHome = provInfoUtil.getSubsPoCHomeByClusterID(clusterId, subsCountForCluster, persisterTxn);
                                if (fetchedPocHome != null && !fetchedPocHome.isEmpty()) {
                                    knLogger.debug(methodName,"fetched PoC Home for clusterId: ", clusterId, " is ", fetchedPocHome, ". Updating AnchorPocInfo.");
                                    commonInfoUtil.updateAnchorPocInfo(corpId, hierarchyId, clusterId, fetchedPocHome, xdmPttServerId, persisterTxn);
                                    pocHomeClusterIdMap.put(clusterId, fetchedPocHome);
                                }
                            }
                        }
                        knLogger.debug(methodName, "Final selected PoC Home for clusterid : ", clusterId +" is " + pocHomeClusterIdMap.get(clusterId));
                    }
                    knLogger.debug(methodName, "Final PoC Home Cluster ID Map: ", pocHomeClusterIdMap);

                    Map<String, Integer> mdnToClusterIdMap = mapMdnToClusterId(mdnGeoCodeList, clusterIdMap);
                    KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
                    String opsCorpFS2 = commonInfoUtil.getOpsCorpFS(Integer.parseInt(corpId), xdmPttServerId, true, persisterTxn);
                    int defaultClientPVMajorVersion = 1;
                    String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, defaultClientPVMajorVersion);
                    
                    // Filter subsDetails to only include geocoded MDNs
                    Map<String, KnCorpSubscriberDTO> geocodedSubsDetails = subsDetails.entrySet().stream()
                            .filter(e -> mdnToClusterIdMap.containsKey(e.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    
                    Map<String, String> mdnToActiveFS2 = generateActiveFS2(
                            geocodedSubsDetails, mdnToClusterIdMap, pocHomeClusterIdMap,
                            xdmPttServerId, opsCorpFS2, clientCapOverrideBitMask, featureSetUtil
                    );

                    List<KnAllocatePocSubsUpdateDTO> updatePocSubs = new ArrayList<>();

                    // Process MDNs with geocodes - full allocation
                    for (Map.Entry<String, Integer> entry : mdnToClusterIdMap.entrySet()) {
                        String mdn = entry.getKey();
                        int clusterId = entry.getValue();
                        String pocHome = pocHomeClusterIdMap.get(clusterId);
                        String activeFS2 = mdnToActiveFS2.get(mdn);
                        KnAllocatePocSubsUpdateDTO allocatePocSubsUpdateDTO = new KnAllocatePocSubsUpdateDTO();
                        allocatePocSubsUpdateDTO.setMdn(mdn);
                        allocatePocSubsUpdateDTO.setClusterId(clusterId);
                        allocatePocSubsUpdateDTO.setPocHome(pocHome);
                        allocatePocSubsUpdateDTO.setPresenceHome(pocHome);
                        // Determine serviceAuthStatus based on client type (mirrors createSubscriber logic)
                        KnCorpSubscriberDTO existingSub = subsDetails.get(mdn);
                        int currentAuthStatus = (existingSub != null) ? existingSub.getServiceAuthStatus() : SERVICE_AUTH_STATUS.PRE_PROVISIONED.value();
                        if (currentAuthStatus == SERVICE_AUTH_STATUS.PRE_PROVISIONED.value()) {
                            int subsClientType = (existingSub != null) ? existingSub.getClientType() : 0;
                            // Client types 7(NNI Alias), 8(NNI Group), 17(SGMDNPatch), 18(DataGroupMDN), 19(StandaloneCamera)
                            // are auto-activated at creation — apply same during allocation
                            if (subsClientType == SUBS_CLIENT_TYPE.ALIASMDN.value()
                                    || subsClientType == SUBS_CLIENT_TYPE.GROUPMDN.value()
                                    || subsClientType == SUBS_CLIENT_TYPE.SGMDNPATCH.value()
                                    || subsClientType == SUBS_CLIENT_TYPE.DATAGROUPMDN.value()
                                    || subsClientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.STANDALONECAMERA.value()) {
                                allocatePocSubsUpdateDTO.setServiceAuthStatus(SERVICE_AUTH_STATUS.ACTIVATED.value());
                            } else if ((subsClientType == SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
                                    || subsClientType == SUBS_CLIENT_TYPE.CROSSCARRIER.value())
                                    && existingSub != null && existingSub.getMcpttCompliance() == MCSCOMPLIANCE) {
                                // WiFi-only(5) and Cross-carrier(10) are auto-activated when MCS compliant
                                allocatePocSubsUpdateDTO.setServiceAuthStatus(SERVICE_AUTH_STATUS.ACTIVATED.value());
                            } else {
                                allocatePocSubsUpdateDTO.setServiceAuthStatus(SERVICE_AUTH_STATUS.PROVISIONED.value());
                            }
                        } else {
                            allocatePocSubsUpdateDTO.setServiceAuthStatus(currentAuthStatus);
                        }
                        allocatePocSubsUpdateDTO.setActiveFS2(activeFS2);
                        allocatePocSubsUpdateDTO.setHierarchyId(hierarchyId);
                        allocatePocSubsUpdateDTO.setHierarchyRoot(hierachyRoot);
                        allocatePocSubsUpdateDTO.setLastProfileUpdateTime(System.currentTimeMillis());
                        updatePocSubs.add(allocatePocSubsUpdateDTO);
                    }
                    commonInfoUtil.updatePocSubsInfoBatch(updatePocSubs, xdmPttServerId, persisterTxn);
                    knLogger.info(methodName, "Successfully processed {} geocoded MDNs", mdnsWithGeoCode.size());
                    
                } catch (KnValidationException e) {
                    // Mark all geocoded MDNs as failed
                    failedGeocodedMdns.addAll(mdnsWithGeoCode);
                    geoCodedFailureMsg = e.getMessage();
                    knLogger.error(methodName, "Validation failed for geocoded MDNs: {}, but will attempt hierarchy-only update for non-geocoded MDNs", 
                            mdnsWithGeoCode, e);
                    // Continue to non-geocoded processing instead of throwing immediately
                } catch (Exception e) {
                    // Mark all geocoded MDNs as failed
                    failedGeocodedMdns.addAll(mdnsWithGeoCode);
                    geoCodedFailureMsg = e.getMessage();
                    knLogger.error(methodName, "Unexpected error processing geocoded MDNs: {}, Error: {}, will attempt hierarchy-only updates for non-geocoded MDNs",
                            mdnsWithGeoCode, e.getMessage(), e);
                    // Continue to non-geocoded processing
                }
            } else if (!reqGeoCodes.isEmpty() && mdnsWithGeoCode.isEmpty()) {
                knLogger.warn(methodName, "GeoCodes found ({}) but no MDNs matched them", reqGeoCodes);
            } else if (mdnsWithGeoCode.isEmpty() && !reqGeoCodes.isEmpty()) {
                knLogger.debug(methodName, "No MDNs with geocodes to process");
            }

            // === PROCESSING PATH 2: MDNs without geocodes (hierarchy-only update) ===
            // This path executes INDEPENDENTLY regardless of geocoded MDN processing success/failure
            // This ensures non-geocoded MDNs can be updated even if geocoded MDN processing fails
            if (!mdnsWithoutGeoCode.isEmpty()) {
                try {
                    knLogger.info(methodName, "Processing {} MDNs without geoCode for hierarchyId update only", mdnsWithoutGeoCode.size());
                    List<KnAllocatePocSubsUpdateDTO> hierarchyOnlyUpdates = new ArrayList<>();

                    for (String mdn : mdnsWithoutGeoCode) {
                        // Check if MDN exists in database
                        if (mdnListInDb.contains(mdn)) {
                            KnAllocatePocSubsUpdateDTO updateDTO = new KnAllocatePocSubsUpdateDTO();
                            updateDTO.setMdn(mdn);
                            updateDTO.setHierarchyId(hierarchyId);
                            updateDTO.setHierarchyRoot(hierachyRoot);
                            updateDTO.setPocHome(POC_HOME_NOT_ASSIGNED);
                            updateDTO.setPresenceHome(POC_HOME_NOT_ASSIGNED);
                            updateDTO.setServiceAuthStatus((SERVICE_AUTH_STATUS.PRE_PROVISIONED).value());
                            updateDTO.setLastProfileUpdateTime(System.currentTimeMillis());
                            // Only update hierarchy ID - let the DAO handle preserving other values
                            hierarchyOnlyUpdates.add(updateDTO);
                        } else {
                            knLogger.warn(methodName, "MDN {} not found in database (skipping)", mdn);
                        }
                    }

                    if (!hierarchyOnlyUpdates.isEmpty()) {
                        commonInfoUtil.updatePocSubsInfoBatch(hierarchyOnlyUpdates, xdmPttServerId, persisterTxn);
                        knLogger.info(methodName, "Successfully updated hierarchy ID for {} non-geocoded MDNs", hierarchyOnlyUpdates.size());
                    } else {
                        knLogger.warn(methodName, "No valid non-geocoded MDNs found in database for update");
                    }
                } catch (Exception e) {
                    knLogger.error(methodName, "Error updating hierarchy ID for non-geocoded MDNs: {}, Error: {}",
                            mdnsWithoutGeoCode, e.getMessage(), e);
                    // Log error but rethrow - at this point, geocoded processing already succeeded if we got here
                    throw e;
                }
            }
            
            // Add failed geocoded MDNs to response failedDataList for proper notification handling
            if (!failedGeocodedMdns.isEmpty()) {
                KnCorpFailedData failedData = new KnCorpFailedData();
                failedData.setAttribute("MDN");
                List<KnXDMFailureRespDTO> failureDetails = new ArrayList<>();
                for (String failedMdn : failedGeocodedMdns) {
                    failedData.addValue(failedMdn);
                    KnXDMFailureRespDTO failureResp = new KnXDMFailureRespDTO();
                    // Keep MDN in both attribute and value to support all downstream readers.
                    failureResp.setAttribute(failedMdn);
                    failureResp.setValue(failedMdn);
                    failureResp.setFcode("GEOCODED_MDN_ALLOCATION_FAILED");
                    failureResp.setMsg(geoCodedFailureMsg != null ? geoCodedFailureMsg : "Geocoded MDN processing failed");
                    failureDetails.add(failureResp);
                }
                failedData.setMsg(geoCodedFailureMsg != null ? geoCodedFailureMsg : "Geocoded MDN processing failed");
                failedData.setCode("GEOCODED_MDN_ALLOCATION_FAILED");
                
                if (respDTO.getFailedDataList() == null) {
                    respDTO.setFailedDataList(new ArrayList<>());
                }
                respDTO.getFailedDataList().add(failedData);
                respDTO.setFailureDetails(failureDetails);
                
                // If there are failed MDNs, set status to failure
                if (!failedGeocodedMdns.isEmpty()) {
                    respDTO.setStatus(1); // 1 = failure
                    respDTO.setStatusCode("PARTIAL_SUCCESS");
                    respDTO.setMessage("Geocoded MDN allocation failed, but non-geocoded MDNs were processed");
                }
            }
            
            // Preserve partial-success response set above; populate success only when no custom status is set
            if (failedGeocodedMdns.isEmpty()) {
                populate(respDTO);
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occurred in allocateSubscriber  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occurred while allocating Subscriber ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    public KnCorpResponseDTO unAllocateSubs(KnIPUnAllocateSubscriberDTO unAllocateSubscriberDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unAllocateSubs(KnIPUnAllocateSubscriberDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", unAllocateSubscriberDTO);
        KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = unAllocateSubscriberDTO.getCorpId();
            HIERARCHY_TYPE hierarchyType = unAllocateSubscriberDTO.getHierarchyType();
            String hierarchyId = unAllocateSubscriberDTO.getHierarchyId();
            boolean isHierarchyCorpMapped = false;
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            isHierarchyCorpMapped = commonInfoUtil.isCorpHierarchyMapped(Integer.parseInt(corpId), hierarchyId, xdmPttServerId, persisterTxn);
            //Extracting mdns for validation
            List<String> mdnList = unAllocateSubscriberDTO.getMdnList();
            knLogger.debug(methodName, "Extracted MDNs for validation: ", KnGDPRTemplate.mdnList(mdnList));

            Map<String, KnCorpSubscriberDTO> subsDetails = commonInfoUtil.getSubscriberDetails(mdnList, xdmPttServerId, true, persisterTxn);
            Collection<String> mdnListInDb = new ArrayList<String>();
            for (KnCorpSubscriberDTO subsDTO : subsDetails.values()) {
                mdnListInDb.add(subsDTO.getMdn());
            }

            // Delete subscriber details from all related groups, contacts and UPM
            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            List<String> allUserProfileMdns = new ArrayList<>();
            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<>();

            for (String mdn : mdnListInDb) {
                knLogger.debug(methodName, "Processing subscriber for deletion: ", KnGDPRTemplate.mdn(mdn));

                // Get subscriber profile to retrieve xdmsHome
                KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, true, persisterTxn);
                String xdmsHomePttId = subsProfile.getXdmsHome();
                int privateContactListId = subsProfile.getContactListId();
                int corpSubscriptionType = subsProfile.getCorpSubscriptionType();
                int subsClientType = subsProfile.getClientType();
                LinkedList<String> singleMdnList = new LinkedList<String>();
                singleMdnList.add(mdn);

                // Delete XDM Directory
                IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

                // Capture directory etag BEFORE any deletions/updates for deRegister notification
                int dirEtagBeforeDeletion = commonXdmServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);

                // Delete the subscriber's own contact list first to avoid foreign key constraints
                contactInfoUtil.deleteSubscribersContactList(mdn, xdmsHomePttId, persisterTxn);
                contactInfoUtil.updateSubscribersContactCount(singleMdnList, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);

                // Get ProvXDMServerDAO for additional deletions
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

                if (subsProfile.getUserProfileIndex() == null || subsProfile.getUserProfileIndex().equals(USER_PROFILE_INDEX)) {
                    List<String> linkedProfileMdns = corpSubsProvInfoUtil.getProfileMdnByBaseMdn(mdn, xdmsHomePttId, persisterTxn);
                    linkedProfileMdns.remove(mdn);
                    if (!linkedProfileMdns.isEmpty()) {
                        provXDMServerDAO.clearUserProfileAssignment(linkedProfileMdns, persisterTxn);

                        KnSubsProfilePersistDTO linkedProfilePersistDTO = new KnSubsProfilePersistDTO();
                        linkedProfilePersistDTO.setMdn(mdn);
                        linkedProfilePersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value());
                        linkedProfilePersistDTO.setServiceStatusOp(com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_STATUS_OP.DEACTIVATED.value());
                        linkedProfilePersistDTO.setLastProfileUpdateTime(Calendar.getInstance().getTimeInMillis());
                        linkedProfilePersistDTO.setMdnList(linkedProfileMdns);
                        provXDMServerDAO.updateServiceAuthStatusForUPM(linkedProfilePersistDTO, persisterTxn);
                        allUserProfileMdns.addAll(linkedProfileMdns);
                        knLogger.debug(methodName, "Cleared user profile assignment for linked profile mdns - ",
                                KnGDPRTemplate.mdnList(linkedProfileMdns));
                    }
                }

                // TODO: Explicit Delete device info if applicable
                boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfile.getActiveFS2(), FEATURE_SET.MCDEVICE.value());
                String deviceCreatedAs = commonXdmServerDAO.getDeviceInfo(mdn, persisterTxn);
                boolean implicitDevice = KnProvConstants.IMPLICIT_DEVICE.equals(deviceCreatedAs);
                if (MCSCOMPLIANCE == subsProfile.getMcpttCompliance() || bitEnabled ||
                    subsProfile.getLicenseType() == KnProvConstants.LICENSEN_TYPE_STANDARD || implicitDevice) {
                    com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO deviceInfoPersistDTO = new com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO();
                    deviceInfoPersistDTO.setDeviceId(mdn);
                    commonXdmServerDAO.deleteDeviceImpiInfo(deviceInfoPersistDTO, persisterTxn);
                    commonXdmServerDAO.deleteDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                    knLogger.debug(methodName, "Deleted Device Info for MDN - ", KnGDPRTemplate.mdn(mdn));
                }

                // Delete Subscriber MCPTT profile
                provXDMServerDAO.deleteMCPTTProfile(mdn, persisterTxn);
                knLogger.debug(methodName, "Deleted Subscriber MCPTT Profile for MDN - ", KnGDPRTemplate.mdn(mdn));

                // Delete subscriber Authorization Doc profile
                provXDMServerDAO.deleteAuthorizationDocProfile(mdn, persisterTxn);
                knLogger.debug(methodName, "Deleted Subscriber Authorization Doc Profile for MDN - ", KnGDPRTemplate.mdn(mdn));

                // Delete emergency destination mappings and emergency doc data
                corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.deleteFromEmergInfoForDest(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.deleteFromEmergDoc(singleMdnList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Deleted emergency destination mappings for MDN - ", KnGDPRTemplate.mdn(mdn));

                // Delete TGSS Doc & SSChannel Info
                provXDMServerDAO.deleteSSChannelGrpInfo(mdn, persisterTxn);
                provXDMServerDAO.deleteTGSSDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "Deleted TGSS Doc and SSChannel Info for MDN - ", KnGDPRTemplate.mdn(mdn));

                // Delete Talk Group Scan Mode (TGSC_MODE) from DG.XDMS_TGSC table
                groupInfoUtil.deleteSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Deleted Talk Group Scan Mode (TGSC_MODE) for MDN - ", KnGDPRTemplate.mdn(mdn));

                /*// Delete from SubscrAddlInfo for hierarchy
                if (hierarchyType == HIERARCHY_TYPE.HIERARCHY) {
                    provXDMServerDAO.deleteSubsAddlInfo(mdn, persisterTxn);
                    knLogger.debug(methodName, "Deleted Subscriber Additional Info for MDN - ", KnGDPRTemplate.mdn(mdn));
                }*/

               /* // Delete the subscriber profile
                provXDMServerDAO.deleteSubscriberProfile(mdn, persisterTxn);
                knLogger.debug(methodName, "Deleted Subscriber Profile for MDN - ", KnGDPRTemplate.mdn(mdn));*/

                // Delete additional talk group document
                groupInfoUtil.deleteSubsAddlTalkGroupDoc(singleMdnList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Deleted Additional Talk Group Document for MDN - ", KnGDPRTemplate.mdn(mdn));

                // Get and delete additional talk groups
                Collection<KnCorpAddlTGInfoDTO> subsMdnAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
                if (subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()) {
                    groupInfoUtil.deleteSubsAddlTalkGroup(singleMdnList, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Deleted Additional Talk Groups for MDN - ", KnGDPRTemplate.mdn(mdn));
                }

                // 1. Delete from all sublists where this MDN is a member
                Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.getSubcriberSublistMemberShipListForAllCorporate(singleMdnList, xdmsHomePttId, persisterTxn);
                Collection<String> contactMDNs = new ArrayList<String>();

                if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                    Collection<Integer> sublistIdList = sublistMemberMap.keySet();
                    sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Deleted from all sublists for MDN: ", KnGDPRTemplate.mdn(mdn));
                    sublistInfoUtil.fetchAndUpdateSublistEtag(sublistIdList, xdmsHomePttId, persisterTxn);

                    knLogger.debug(methodName, "Getting sublist distribution list - ", sublistMemberMap);
                    Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                            sublistMemberMap.keySet(), xdmsHomePttId, persisterTxn);

                    for (int sublistId : sublistMemberMap.keySet()) {
                        Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                        if (contactList != null && !contactList.isEmpty()) {
                            for (KnCorpSubscriberDTO contact : contactList) {
                                if (!singleMdnList.contains(contact.getMdn())) {
                                    contactMDNs.add(contact.getMdn());
                                }
                            }
                        }
                    }

                    // Delete empty sublists after removing the MDN from all sublists, but only if the sublist is now empty
                    if (sublistIdList != null && !sublistIdList.isEmpty()) {
                        ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sublistIdList, xdmsHomePttId, persisterTxn);
                        ArrayList<Integer> nonSharedSublists = sublistInfoUtil.getNonSharedSublistFromList(sublistIdList, xdmsHomePttId, persisterTxn);
                        ArrayList<Integer> subList= new ArrayList<Integer>();
                        subList.addAll(sharedSublists);
                        subList.addAll(nonSharedSublists);
                        ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(subList, xdmsHomePttId, persisterTxn);
                        if (emptySublist != null && !emptySublist.isEmpty()) {
                            sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
                        }
                    }
                }

                // Delete CORPLISTDISTINFO entries where this MDN is a RECIPIENT for hierarchy/public sublists,
                // ensuring stale visibility is removed when MDN is re-allocated to a different hierarchy.
                sublistInfoUtil.deleteCorpListDistributionForMdn(singleMdnList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Deleted CORPLISTDISTINFO recipient entries for MDN: ", KnGDPRTemplate.mdn(mdn));

                // Preserve private contact-list rows during unallocate, but keep the rest of deleteSublist cleanup.
                if (privateContactListId > 0) {
                    List<Integer> privateSublistIdList = Collections.singletonList(privateContactListId);
                    Map<Integer, KnCorpSublistDTO> privateContactListMap = sublistInfoUtil.filterSublists(
                            privateSublistIdList,
                            Integer.parseInt(corpId),
                            SUBLIST_TYPE_PRIVATE_CONTACTLIST,
                            xdmsHomePttId,
                            true,
                            persisterTxn);
                    if (privateContactListMap.containsKey(privateContactListId)) {
                        sublistInfoUtil.deleteAllSublistMembers(privateContactListId, xdmsHomePttId, persisterTxn);
                        sublistInfoUtil.updateSublistsSubscribersContactCount(privateSublistIdList,
                                MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                                MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                                MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                                xdmsHomePttId, persisterTxn,
                                MAX_LIMIT_VALIDATION_NOT_REQUIRED);
                        sublistInfoUtil.deleteCorpListDistReference(privateContactListId, xdmsHomePttId, persisterTxn);
                        sublistInfoUtil.deleteCorpListDistGroupReference(privateContactListId, xdmsHomePttId, persisterTxn);
                        knLogger.debug(methodName, "Deleted private contact list cleanup data except CORPLISTINFO for MDN - ", KnGDPRTemplate.mdn(mdn),
                                " SublistId - ", privateContactListId);
                    } else {
                        sublistInfoUtil.deleteSublist(privateContactListId, Integer.parseInt(corpId), xdmsHomePttId, persisterTxn);
                        knLogger.debug(methodName, "Deleted non-private sublist for stored contactListId for MDN - ", KnGDPRTemplate.mdn(mdn));
                    }
                }

                // 2. Delete from contact lists
                LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
                LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;

                if (!contactMDNs.isEmpty()) {
                    for (String subscMdn : contactMDNs) {
                        removeMdnListMap.put(subscMdn, singleMdnList);
                    }
                    delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Deleted from contact lists for MDN: ", KnGDPRTemplate.mdn(mdn));
                }

                // 3. Delete from all groups
                LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
                Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
                LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus = null;
                Map<Integer, Integer> groupEtagMap = null;
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = null;

                Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(singleMdnList, xdmsHomePttId, persisterTxn);

                if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
                    Collection<Integer> allGroupIdList = groupMemberMap.keySet();

                    // Get group details
                    groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(allGroupIdList, xdmsHomePttId, persisterTxn);

                    // Delete members from group
                    for (int grpId : allGroupIdList) {
                        removeGrpMdnListMap.put(grpId, singleMdnList);
                    }

                    delGrpMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Deleted from all groups for MDN: ", KnGDPRTemplate.mdn(mdn));

                    // Delete from CORPGROUPDISTINFO table
                    Map<Integer, Map<String, Collection<String>>> groupMemDelMap = new HashMap<>();
                    for (int grpId : allGroupIdList) {
                        Map<String, Collection<String>> deletedMemberMap = new HashMap<>();
                        deletedMemberMap.put(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DELTED_MEMBERS, removeGrpMdnListMap.get(grpId));
                        groupMemDelMap.put(grpId, deletedMemberMap);
                    }
                    groupInfoUtil.deleteFrmCorpGroupDistInfo(groupMemDelMap, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Deleted from CORPGROUPDISTINFO table for MDN: ", KnGDPRTemplate.mdn(mdn));

                    // Update sublist contact count
                    sublistInfoUtil.updateSublistsSubscribersContactCount(sublistMemberMap.keySet(),
                            MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                            MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                            MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                            xdmsHomePttId, persisterTxn,
                            MAX_LIMIT_VALIDATION_NOT_REQUIRED);

                    // Update group etags
                    groupEtagMap = groupInfoUtil.updateGroupListEtag(allGroupIdList, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Updated group etags for MDN: ", KnGDPRTemplate.mdn(mdn));

                    // Get group status to determine deleted/modified groups
                    Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(allGroupIdList, xdmsHomePttId, persisterTxn);
                    groupDistList = groupInfoUtil.getGroupSubscriberDistList(allGroupIdList, xdmsHomePttId, persisterTxn);

                    // Collect additional contacts from deleted groups
                    Set<Integer> delGroupIdList = new HashSet<>();
                    if (groupListStatus.get(DELETED) != null) {
                        delGroupIdList = groupListStatus.get(DELETED).keySet();
                        if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                            for (Integer grpId : delGroupIdList) {
                                Collection<String> delMembersList = groupDistList.get(grpId);
                                if (delMembersList != null) {
                                    contactMDNs.addAll(delMembersList);
                                }
                                removeGrpMdnListMap.remove(grpId);
                                delGrpMemStatus.remove(grpId);
                            }
                        }
                    }
                    //empty group support:if last subscriber is deleted then group will not be deleted
                    //Updating  MEMBERCOUNT = 0, in DG.CORPGROUPMEMBERCOUNT when last subscriber is deleted from the group
                    //groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                    groupInfoUtil.emptyGroup(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);


                    // Collect additional contacts from modified groups
                    if (groupListStatus.get(MODIFIED) != null) {
                        Set<Integer> modGroupIdList = groupListStatus.get(MODIFIED).keySet();
                        if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                            for (Integer grpId : modGroupIdList) {
                                Collection<String> modMembersList = groupDistList.get(grpId);
                                if (modMembersList != null) {
                                    contactMDNs.addAll(modMembersList);
                                }
                            }
                        }
                    }
                }

                // 4. Update contact counts
                if (!contactMDNs.isEmpty()) {
                    contactInfoUtil.updateSubscribersContactCount(contactMDNs, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Updated contact counts for impacted subscribers due to deletion of MDN: ", KnGDPRTemplate.mdn(mdn));
                }

                // 5. Delete from external contact tables in other corporations (includes sublists and groups)
                etagMap = deleteExtContactDataFrmOtherCorp(subsProfile, Integer.parseInt(corpId), persisterTxn, etagMap, hierarchyType);
                knLogger.debug(methodName, "Deleted from external contact tables in other corporations for MDN: ", KnGDPRTemplate.mdn(mdn));

                // ========== CONSOLIDATED ETAG UPDATE SECTION ==========
                knLogger.info(methodName, "Updating all etags for subscriber: ", KnGDPRTemplate.mdn(mdn));

                // Update additional talk group notification
                etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);

                // Update resource list etags for contacts
                if (!contactMDNs.isEmpty()) {
                    etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, etagMap, persisterTxn);
                    knLogger.debug(methodName, "Updated resource list etags for contacts for MDN: ", KnGDPRTemplate.mdn(mdn));
                }

                // Update directory etags
                if (!contactMDNs.isEmpty()) {
                    etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Updated directory etags for contacts for MDN: ", KnGDPRTemplate.mdn(mdn));
                }

                // Form group notifications (deleted and modified)
                if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
                    Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(groupMemberMap.keySet(), xdmsHomePttId, persisterTxn);

                    // Handle deleted groups
                    if (groupListStatus.get(DELETED) != null) {
                        Set<Integer> delGroupIdList = groupListStatus.get(DELETED).keySet();
                        if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                            Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
                            for (Integer grpId : delGroupIdList) {
                                Collection<String> delMembersList = groupDistList.get(grpId);
                                deletedMembersMap.put(grpId, delMembersList);
                            }
                            etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                    com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(),
                                    etagMap, groupDetailsMap, null);
                        }
                    }

                    // Handle modified groups
                    if (groupListStatus.get(MODIFIED) != null) {
                        Set<Integer> modGroupIdList = groupListStatus.get(MODIFIED).keySet();
                        if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                            Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();
                            for (Integer grpId : modGroupIdList) {
                                Collection<String> modMembersList = groupDistList.get(grpId);
                                modifiedMembersMap.put(grpId, modMembersList);
                            }
                            etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                                    com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(),
                                    etagMap, groupDetailsMap, null);
                        }
                    }
                }

               /* // Form XCAP diff notification
                etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap, null, null,
                        removeGrpMdnListMap, null, null, groupDistList, delContactStatus, delGrpMemStatus, null);*/

                // Update auth impacted tables for removed contact
                etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, Integer.parseInt(corpId), persisterTxn, etagMap);

                knLogger.info(methodName, "Completed etag updates for subscriber: ", KnGDPRTemplate.mdn(mdn));
                // ========== END OF CONSOLIDATED ETAG UPDATE SECTION ==========

                // Build deRegister dirChgDTO for this MDN
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setMdn(mdn);
                dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                dirChgDTO.setPocHome(subsProfile.getPocHome());
                dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());
                dirChgDTO.setDirUri(genInfoUtil.generateDirDocUri(mdn));
                dirChgDTO.setDirPrevEtag(String.valueOf(dirEtagBeforeDeletion));
                dirChgDTO.setDirNewEtag(String.valueOf(dirEtagBeforeDeletion + 1));
                dirChgDTO.setProtoVersion(String.valueOf(subsProfile.getClientPVmajorVer()));
                dirChgDTO.setClientType(subsClientType);
                // DocChgDTO intentionally not set - deRegister is self-notification, not doc change notification
                dirChgDTOs.add(dirChgDTO);

                knLogger.debug(methodName, "Completed deletion for subscriber: ", KnGDPRTemplate.mdn(mdn));
            }

            // Notify IDM to disable account status for each unallocated subscriber
            String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
            for (String mdn : mdnListInDb) {
                KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
                String appId = subsProfileInfoDTO.getDispatchType() == DISPATCH_TYPE_WEB ? APP_ID.DISPATCHER.value() : APP_ID.HANDSET_STANDARD.value();
                KnXDMSubsAliasDetailsReqDTO oidcSubsDto = new KnXDMSubsAliasDetailsReqDTO();
                KnXDMSubsAliasDetailsRespDTO oidcRespDto = null;
                Map<String, Object> attributes = new HashMap<>();
                int state = com.kodiak.xdms.server.common.resources.KnConstants.IDM_ACCOUNT_STATUS.DISABLE.value();
                attributes.put(ACCOUNT_STATUS, String.valueOf(state));
                oidcSubsDto.setUserid(mdn);
                oidcSubsDto.setAttributes(attributes);
                oidcRespDto=KnManageSyncUserProfileUtil.getInstance()
                        .notifyIDMIntfForOIDCMgmt(oidcSubsDto, appId, idmFqdn, CHANGE_SERVICE_AUTH_STATUS, HttpMethod.PUT);
                knLogger.debug(methodName, "Notified IDM to disable account for MDN: ", KnGDPRTemplate.mdn(mdn));
                if (oidcRespDto != null && oidcRespDto.getStatus() == com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                    knLogger.error(methodName, "Error for updating Updating pwd profile in OidcIDM - ", oidcRespDto.getStatus());
                }
            }

            // Reset CLUSTERID, HIERARCHY_ID to NULL and POCHOME to 0 in POCSUBSCRINFO table
            knLogger.info(methodName, "Resetting CLUSTERID, HIERARCHY_ID to NULL and POCHOME to 0 for unallocated subscribers");
            List<KnAllocatePocSubsUpdateDTO> resetPocSubsList = new ArrayList<>();
            for (String mdn : mdnListInDb) {
                KnAllocatePocSubsUpdateDTO resetDTO = new KnAllocatePocSubsUpdateDTO();
                resetDTO.setMdn(mdn);
                resetDTO.setClusterId(0); // This will be set to NULL in DAO
                resetDTO.setPocHome(POC_HOME_NOT_ASSIGNED); // Reset POCHOME to 0
                resetDTO.setPresenceHome(POC_HOME_NOT_ASSIGNED); // Reset PRESENCEHOME to 0
                resetDTO.setServiceAuthStatus(PRE_PROVISIONED.value());
                resetDTO.setHierarchyId(null); // Set HIERARCHY_ID to NULL
                resetDTO.setHierarchyRoot(null); // Set HIERARCHY_ROOT to NULL
                resetDTO.setLastProfileUpdateTime(System.currentTimeMillis());
                resetPocSubsList.add(resetDTO);
            }
            commonInfoUtil.updatePocSubsInfoBatch(resetPocSubsList, xdmPttServerId, persisterTxn);
            knLogger.debug(methodName, "Successfully reset CLUSTERID, HIERARCHY_ID, and POCHOME for unallocated subscribers");

            respDTO.setChangeLogMap(etagMap);
            respDTO.setUserProfileMdns(allUserProfileMdns);
            respDTO.setDirChgDTOs(dirChgDTOs);
            populate(respDTO);
            knLogger.info(methodName, "EXIT : Successfully unallocated subscribers");

        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occurred in unAllocateSubs  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occurred while unallocating Subscriber ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }

    private Map<String, Integer> mapMdnToClusterId(List<KnIPAllocateSubscriberDTO.KnMdnDetailsDTO> mdnGeoCodeList, Map<String, Integer> clusterIdMap) {
        String methodName = "mapMdnToClusterId(List<KnIPAllocateSubscriberDTO.KnMdnDetailsDTO>, Map<String, String>)";
        knLogger.debug(methodName, "ENTRY : mdnGeoCodeList - ", mdnGeoCodeList, ", clusterIdMap - ", clusterIdMap);
        Map<String, Integer> mdnToClusterIdMap = new HashMap<>();
        for (KnIPAllocateSubscriberDTO.KnMdnDetailsDTO mdnDetails : mdnGeoCodeList) {
            String geoCode = mdnDetails.getGeoCode();
            Integer clusterIdForGeo = clusterIdMap.get(geoCode);
            if (clusterIdForGeo != null && mdnDetails.getMdn() != null) {
                for (String mdn : mdnDetails.getMdn()) {
                    mdnToClusterIdMap.put(mdn, clusterIdForGeo);
                }
            }
        }
        knLogger.debug(methodName, "EXIT : mdnToClusterIdMap - ", mdnToClusterIdMap);
        return mdnToClusterIdMap;
    }

    private Map<String, String> generateActiveFS2(
            Map<String, KnCorpSubscriberDTO> subsDetails,
            Map<String, Integer> mdnToClusterIdMap,
            Map<Integer, String> pocHomeClusterIdMap,
            String xdmPttServerId,
            String opsCorpFS2,
            String clientCapOverrideBitMask,
            KnFeatureSetUtil featureSetUtil) throws KnFeatureSetException {
        String methodName = "generateActiveFS2(Map<String, KnCorpSubscriberDTO>,Map<String, Integer>,Map<Integer, String>,String,String,String,KnFeatureSetUtil)";
        knLogger.debug(methodName, "ENTRY : subsDetails - ", subsDetails,
                ", mdnToClusterIdMap - ", mdnToClusterIdMap,
                ", pocHomeClusterIdMap - ", pocHomeClusterIdMap,
                ", xdmPttServerId - ", xdmPttServerId,
                ", opsCorpFS2 - ", opsCorpFS2,
                ", clientCapOverrideBitMask - ", clientCapOverrideBitMask);
        Map<String, String> mdnToActiveFS2 = new HashMap<>();
        Map<Integer, String> clientTypeToActiveFS2 = new HashMap<>();

        for (Map.Entry<String, KnCorpSubscriberDTO> entry : subsDetails.entrySet()) {
            String mdn = entry.getKey();
            KnCorpSubscriberDTO subsDTO = entry.getValue();
            Integer clientType = subsDTO.getClientType();
            int clusterId = mdnToClusterIdMap.get(mdn);
            String pocHome = pocHomeClusterIdMap.get(clusterId);
            String presencePttServerId = subsDTO.getPresenceHome();
            String clientFS2 = subsDTO.getClientFs2();
            String subsFS2 = subsDTO.getSubscriberFs2();
            String opsFS2 = subsDTO.getOpsFs2();
            String corpAdminFS2 = subsDTO.getCorpAdminFS2();
            String xdmsFs2 = subsDTO.getXdmsFs2();
            String userProfileFS2 = subsDTO.getUserProfileFS2();

            String activeFS2 = clientTypeToActiveFS2.get(clientType);
            if (activeFS2 == null) {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(
                        pocHome, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2, opsCorpFS2, corpAdminFS2,
                        clientCapOverrideBitMask, xdmsFs2, userProfileFS2
                );
                clientTypeToActiveFS2.put(clientType, activeFS2);
            }
            mdnToActiveFS2.put(mdn, activeFS2);
        }
        knLogger.debug(methodName, "EXIT : mdnToActiveFS2 - ", mdnToActiveFS2);
        return mdnToActiveFS2;
    }

}
