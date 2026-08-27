/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.request.KnCorpOperationStatusMesssageInfoDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpOSMInfoRespDTO;
import com.kodiak.common.commdto.response.KnXDMCorpOSMListRespDTO;
import com.kodiak.common.commdto.response.KnXDMOSMInfoRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.corpmgmt.business.ICorpOSMController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPOsmDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMGroupListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListDetailsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.MAX_STATUS_MSG_PER_OSM_LIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.MAX_STATUS_SHORT_TEXT_LENGTH;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.MAX_STATUS_MSG_LENGTH;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.CORP_OSM_INFO_TABLE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.CORP_OSM_INFO_TABLE_PK;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DUAL_DATA_STORE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;

public class KnCorpOSMController implements ICorpOSMController {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpOSMController.class);
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnValidatorFramework validatorFW;
    private KnCorpOSMInfoUtil corpOSMInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpGroupProfileUtil groupProfilUtil;
    private KnCorpUserProfileUtil corpUserProfileUtil;

    public KnCorpOSMController(){
    	contactInfoUtil = new KnCorpContactInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        corpOSMInfoUtil= new KnCorpOSMInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        groupProfilUtil = new KnCorpGroupProfileUtil();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
    }

    @Override
    public KnCorpResponseDTO createOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        String methodName = "createOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpOSMPersistDTO corpOSMPersistDTO=new KnCorpOSMPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil=KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipOsmDTO - ", ipOsmDTO);
        knLogger.info(methodName, "ipOSMDTO1111 - ", ipOsmDTO.getHierarchyId());
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipOsmDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipOsmDTO.getCustomParamMap();
            if (ipOsmDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipOsmDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.CREATE_OSM_LIST);
                    hookIPDTO.setData(ipOsmDTO);
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
            corpOSMPersistDTO.setInputDTO(ipOsmDTO);

            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            final String maxStatusMsgPerOsmList = microServicesParamNameValueMap.get(MAX_STATUS_MSG_PER_OSM_LIST);
            final String maxStatusShortTextLength = microServicesParamNameValueMap.get(MAX_STATUS_SHORT_TEXT_LENGTH);
            final String maxStatusMsgLength = microServicesParamNameValueMap.get(MAX_STATUS_MSG_LENGTH);

            corpOSMPersistDTO.setMaxStatusMsgPerOsmList(maxStatusMsgPerOsmList);
            corpOSMPersistDTO.setMaxStatusShortTextLength(maxStatusShortTextLength);
            corpOSMPersistDTO.setMaxStatusMsgLength(maxStatusMsgLength);

            final int osmListId = genInfoUtil.retrieveIdForTable(CORP_OSM_INFO_TABLE, xdmsHome,CORP_OSM_INFO_TABLE_PK, false, DUAL_DATA_STORE);
            corpOSMPersistDTO.setAddedOSMMsgList(ipOsmDTO.getAddedOSMMsgList());
            corpOSMPersistDTO.setOSMListName(ipOsmDTO.getOSMListName());
            corpOSMPersistDTO.setIsDefault(ipOsmDTO.getIsDefault());
            corpOSMPersistDTO.setInternalCorpId(ipOsmDTO.getCorpId());
            corpOSMPersistDTO.setOSMListId(Integer.toString(osmListId));
            corpOSMPersistDTO.setHierarchyId(ipOsmDTO.getHierarchyId());

            //validating OSMListIdExists
            //If hierarchyId exists we fetch it based on hierarchyId
            Map<Integer, Integer> osmListIdAndDefaultMap;
            knLogger.info("Before checking for hierarchyId ");
            if(ipOsmDTO.getHierarchyId()!=null && !ipOsmDTO.getHierarchyId().isEmpty()){
                knLogger.info("HierarchyId is not null or empty. Inside if condition. hierarchyId {}",ipOsmDTO.getHierarchyId());
                osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMapByHierarchyId(ipOsmDTO.getCorpId(), xdmsHome, persisterTxn,ipOsmDTO.getHierarchyId());
            }else{
                knLogger.info("HierarchyId is null or empty. Inside else condition. hierarchyId {}",ipOsmDTO.getHierarchyId());
                osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMap(ipOsmDTO.getCorpId(), xdmsHome, persisterTxn);
            }
            corpOSMPersistDTO.setOsmListIdAndDefultMap(osmListIdAndDefaultMap);

            //validating osm list name
            KnCorpOSMInfoListRespDTO CorpOsmList = corpOSMInfoUtil.getOSMListByCorp(ipOsmDTO.getCorpId(), xdmsHome, false, persisterTxn);
            corpOSMPersistDTO.setCorpOSMList(CorpOsmList.getOSMListInfo());

            knLogger.debug(methodName, "Before Validation", corpOSMPersistDTO);
            validatorFW.validate(corpOSMPersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            corpOSMInfoUtil.createOSMList(corpOSMPersistDTO,xdmsHome,persisterTxn);
            populate(respDTO);
            respDTO.setOsmListId(Integer.toString(osmListId));
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
    public KnCorpResponseDTO updateOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpOSMPersistDTO corpOSMPersistDTO=new KnCorpOSMPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil=KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipOsmDTO - ", ipOsmDTO);
        try {
            String corpId = ipOsmDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipOsmDTO.getCustomParamMap();
            if (ipOsmDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipOsmDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_OSM_LIST);
                    hookIPDTO.setData(ipOsmDTO);
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
            corpOSMPersistDTO.setInputDTO(ipOsmDTO);

            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //validation
            final String maxStatusMsgPerOsmList = microServicesParamNameValueMap.get(MAX_STATUS_MSG_PER_OSM_LIST);
            final String maxStatusMsgLength = microServicesParamNameValueMap.get(MAX_STATUS_MSG_LENGTH);
            final String maxStatusCodeLength = microServicesParamNameValueMap.get(MAX_STATUS_SHORT_TEXT_LENGTH);
            corpOSMPersistDTO.setMaxStatusMsgPerOsmList(maxStatusMsgPerOsmList);
            corpOSMPersistDTO.setMaxStatusShortTextLength(maxStatusCodeLength);
            corpOSMPersistDTO.setMaxStatusMsgLength(maxStatusMsgLength);

            corpOSMPersistDTO.setAddedOSMMsgList(ipOsmDTO.getAddedOSMMsgList());
            corpOSMPersistDTO.setModifiedOSMMsgList(ipOsmDTO.getModifiedOSMMsgList());
            corpOSMPersistDTO.setRemovedOSMMsgList(ipOsmDTO.getRemovedOSMMsgList());
            corpOSMPersistDTO.setOSMListName(ipOsmDTO.getOSMListName());
            corpOSMPersistDTO.setOSMListId(ipOsmDTO.getOSMListId());
            corpOSMPersistDTO.setIsDefault(ipOsmDTO.getIsDefault());
            corpOSMPersistDTO.setInternalCorpId(corpId);

            //validating OSMListIdExists
            Map<Integer, Integer> osmListIdAndDefaultMap;
            if (ipOsmDTO.getHierarchyId() != null && !ipOsmDTO.getHierarchyId().isEmpty()) {
                knLogger.info("HierarchyId is not null or empty. Inside if condition. hierarchyId {}", ipOsmDTO.getHierarchyId());
                osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMapByHierarchyId(ipOsmDTO.getCorpId(), xdmsHome, persisterTxn, ipOsmDTO.getHierarchyId());
            } else {
                knLogger.info("HierarchyId is null or empty. Inside else condition. hierarchyId {}", ipOsmDTO.getHierarchyId());
                osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMap(ipOsmDTO.getCorpId(), xdmsHome, persisterTxn);
            }
            corpOSMPersistDTO.setOsmListIdAndDefultMap(osmListIdAndDefaultMap);
            //validating OSMInfo Unique Fields
            List<KnXDMOSMInfoRequestDTO> osmInfoList = corpOSMInfoUtil.getUniqueFlieldsOSMInfoList(ipOsmDTO.getOSMListId(), xdmsHome, persisterTxn);
            corpOSMPersistDTO.setOsmInfoList(osmInfoList);

            //validating osm list name
            KnCorpOSMInfoListRespDTO CorpOsmList = corpOSMInfoUtil.getOSMListByCorp(corpId, xdmsHome, false, persisterTxn);
            corpOSMPersistDTO.setCorpOSMList(CorpOsmList.getOSMListInfo());
            KnCorpOSMInfoListDetailsRespDTO osmDetails = corpOSMInfoUtil
                    .getOSMListDetailsByListId(corpId, ipOsmDTO.getOSMListId(), xdmsHome, false, persisterTxn);
            corpOSMPersistDTO.setOSMListDetails(osmDetails.getOSMListDetails());
            //validating modified osm list
            final AtomicLong counter = new AtomicLong();
            if (corpOSMPersistDTO.getModifiedOSMMsgList() != null&&!corpOSMPersistDTO.getModifiedOSMMsgList().isEmpty()) {
                for (KnXDMOSMInfoRequestDTO modifiedOSMListReq : corpOSMPersistDTO.getModifiedOSMMsgList()) {
                    for(KnXDMOSMInfoRequestDTO dbOsm:osmInfoList) {
                        if (dbOsm.getMsgId().equals(modifiedOSMListReq.getMsgId())
                                ||dbOsm.getMsgOrderId().equals(modifiedOSMListReq.getMsgOrderId())
                                ||dbOsm.getMsgShortText().equals(modifiedOSMListReq.getMsgShortText())
                                ||dbOsm.getMsg().equals(modifiedOSMListReq.getMsg())) {
                            counter.incrementAndGet();
                        }
                    }
                }
            }
            corpOSMPersistDTO.setUniqueOSMInfoCount(counter.intValue());
            knLogger.debug(methodName, "Before Validation", corpOSMPersistDTO);
            validatorFW.validate(corpOSMPersistDTO);
            knLogger.debug(methodName, "Validation Successfull");

            corpOSMInfoUtil.updateOSMList(corpOSMPersistDTO,xdmsHome,persisterTxn);
            Set<Integer> groupIds = groupInfoUtil.getCorpGroupIdByOsmListId(Integer.parseInt(ipOsmDTO.getOSMListId()), xdmsHome, persisterTxn);
            if(groupIds!=null&&!groupIds.isEmpty()){
                //Adding check,As can update osm even though not assigned to group.
                //Updating Group's etag for osmlistIds.groupId, etag.
                Map<Integer, Collection<String>> addedGrpMemMap = new HashMap<Integer, Collection<String>>();
                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupEtagMap",groupEtagMap);
                Set<String> mdnList= new HashSet<>();
				for (Integer groupid : groupIds) {
					List<String> members = sublistInfoUtil.selectGroupMemberList(groupid, xdmsHome, persisterTxn);
					addedGrpMemMap.put(groupid, members);
					mdnList.addAll(members);
				}
				//for profile mdn in mcxGroup
                //get groupInfo
                ArrayList<KnCorpGroupDTO> groupBasicInfo = groupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHome, persisterTxn);
                //filter mcxGroup
				List<KnCorpGroupDTO> mcxGroups = groupBasicInfo.stream().filter(e -> e.getMcxGrpInd().equals(KnConstants.MCX_GROUP_INDICATOR)).collect(Collectors.toList());
                knLogger.debug(methodName," mcxGroups :",mcxGroups);
                for(KnCorpGroupDTO mcxGroupsInfo:mcxGroups){
                    Set<String> upmIds = corpUserProfileUtil.retriveGroupProfileInfoByGroupId(mcxGroupsInfo.getGroupId(), xdmsHome, persisterTxn).keySet();
                    List<String> profileMdnList=new ArrayList<>();
                    for(String userProfileId:upmIds){
                        List<String> profileMdnListDB = corpUserProfileUtil.getProfileMdnByUPId(corpId,userProfileId, xdmsHome, persisterTxn);
                        profileMdnList.addAll(profileMdnListDB);
                    }
                    if(!profileMdnList.isEmpty()&&addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).isEmpty()){
                        addedGrpMemMap.put(mcxGroupsInfo.getGroupId(), profileMdnList);
                        mdnList.addAll(profileMdnList);
                    }else if(!profileMdnList.isEmpty()&&!addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).isEmpty()){
                        //for CRI ,SG mdns
                        addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).addAll(profileMdnList);
                        mdnList.addAll(profileMdnList);
                    }
                }
                knLogger.debug(methodName," mdnList :",KnGDPRTemplate.mdnList(mdnList)
                        ," addedGrpMemMap :",KnGDPRTemplate.mapMdnAsValue(addedGrpMemMap));
				Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubscribersDirectory(mdnList, null, xdmsHome,
						persisterTxn);
                //mdn, dirChgDto
				etagMap = commonInfoUtil.formSubscriberNotification(addedGrpMemMap, groupEtagMap,
						com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap,
						null, null);
				knLogger.debug(methodName, "etagMap", KnGDPRTemplate.mapKeyMdn(etagMap));
				respDTO.setChangeLogMap(etagMap);
            }
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
    public KnCorpResponseDTO deleteOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        String methodName = "deleteOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpOSMPersistDTO corpOSMPersistDTO=new KnCorpOSMPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil=KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipOsmDTO - ", ipOsmDTO);
        try {
            String corpId=ipOsmDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipOsmDTO.getCustomParamMap();
            if (ipOsmDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipOsmDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.DELETE_OSM_LIST);
                    hookIPDTO.setData(ipOsmDTO);
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
            corpOSMPersistDTO.setInputDTO(ipOsmDTO);
            //Validating osm list id exists.
            Map<Integer, Integer> osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMap(corpId, xdmsHome, persisterTxn);
            corpOSMPersistDTO.setOsmListIdAndDefultMap(osmListIdAndDefaultMap);

            corpOSMPersistDTO.setOSMListId(ipOsmDTO.getOSMListId());

            knLogger.debug(methodName, "Before Validation", corpOSMPersistDTO);
            validatorFW.validate(corpOSMPersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            //deleting by corp id and osm list id.
            Set<Integer> groupIds = groupInfoUtil.getCorpGroupIdByOsmListId(Integer.parseInt(ipOsmDTO.getOSMListId()), xdmsHome, persisterTxn);
            corpOSMInfoUtil.deleteOSMList(corpId,ipOsmDTO.getOSMListId(),xdmsHome,persisterTxn);
            knLogger.debug(methodName,"fetching groupProfileIds by osmListId");
            Set<Integer> groupProfileIds = groupProfilUtil.getGroupProfileDetailByOsmListId(Integer.parseInt(corpId),Integer.parseInt(ipOsmDTO.getOSMListId())
                    ,xdmsHome,persisterTxn);
            knLogger.info(methodName,"groupProfileIds.size - ",groupProfileIds.size());
            if(groupProfileIds!=null && !groupProfileIds.isEmpty()){
                groupProfilUtil.deleteOSMListFromProfile(Integer.parseInt(ipOsmDTO.getOSMListId()),Integer.parseInt(corpId),xdmsHome,persisterTxn);
            }
            if(groupIds!=null&&!groupIds.isEmpty()){
                //Adding check,As can update osm even though not assigned to group.
                //Updating Group's etag for osmlistIds.groupId, etag.
                Map<Integer, Collection<String>> addedGrpMemMap = new HashMap<Integer, Collection<String>>();
                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupEtagMap",groupEtagMap);
                Set<String> mdnList= new HashSet<>();
				for (Integer groupid : groupIds) {
					List<String> members = sublistInfoUtil.selectGroupMemberList(groupid, xdmsHome, persisterTxn);
					addedGrpMemMap.put(groupid, members);
					mdnList.addAll(members);
				}
                //for profile mdn in mcxGroup
                //get groupInfo
                ArrayList<KnCorpGroupDTO> groupBasicInfo = groupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHome, persisterTxn);
                //filter mcxGroup
                List<KnCorpGroupDTO> mcxGroups = groupBasicInfo.stream().filter(e -> e.getMcxGrpInd().equals(KnConstants.MCX_GROUP_INDICATOR)).collect(Collectors.toList());
                knLogger.debug(methodName," mcxGroups :",mcxGroups);
                for(KnCorpGroupDTO mcxGroupsInfo:mcxGroups){
                    Set<String> upmIds = corpUserProfileUtil.retriveGroupProfileInfoByGroupId(mcxGroupsInfo.getGroupId(), xdmsHome, persisterTxn).keySet();
                    List<String> profileMdnList=new ArrayList<>();
                    for(String userProfileId:upmIds){
                        List<String> profileMdnListDB = corpUserProfileUtil.getProfileMdnByUPId(corpId,userProfileId, xdmsHome, persisterTxn);
                        profileMdnList.addAll(profileMdnListDB);
                    }
                    if(!profileMdnList.isEmpty()&&addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).isEmpty()){
                        addedGrpMemMap.put(mcxGroupsInfo.getGroupId(), profileMdnList);
                        mdnList.addAll(profileMdnList);
                    }else if(!profileMdnList.isEmpty()&&!addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).isEmpty()){
                        //for CRI ,SG mdns
                        addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).addAll(profileMdnList);
                        mdnList.addAll(profileMdnList);
                    }
                }
                knLogger.debug(methodName," mdnList :",KnGDPRTemplate.mdnList(mdnList)
                        ," addedGrpMemMap :",KnGDPRTemplate.mapMdnAsValue(addedGrpMemMap));
				Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubscribersDirectory(mdnList, null, xdmsHome,
						persisterTxn);
				// mdn, dirChgDto
				etagMap = commonInfoUtil.formSubscriberNotification(addedGrpMemMap, groupEtagMap,
						com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap,
						null, null);
				knLogger.debug(methodName, "etagMap", KnGDPRTemplate.mapKeyMdn(etagMap));
				respDTO.setChangeLogMap(etagMap);
            }
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
    public KnCorpOSMInfoListRespDTO getOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn)";

        KnCorpOSMInfoListRespDTO response = new KnCorpOSMInfoListRespDTO();
        KnCorpOSMPersistDTO corpOSMPersistDTO=new KnCorpOSMPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil=KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipOsmDTO - ", ipOsmDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipOsmDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipOsmDTO.getCustomParamMap();
            if (ipOsmDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipOsmDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_OSM_LIST);
                    hookIPDTO.setData(ipOsmDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, response);
                            return response;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                response.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            //settting operation and entity id
            corpOSMPersistDTO.setInputDTO(ipOsmDTO);

            if(ipOsmDTO.getHierarchyId() != null){
                response = corpOSMInfoUtil.getOSMListByCorpAndHierarchyId(ipOsmDTO.getCorpId(), ipOsmDTO.getHierarchyId(),xdmsHome, true, persisterTxn);
            }else {
                response = corpOSMInfoUtil.getOSMListByCorp(ipOsmDTO.getCorpId(), xdmsHome, true, persisterTxn);
            }
            populate(response);
        } catch (KnCorpBOException e) {
            populate(response, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(response, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", response);
        return response;
    }

    @Override
    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetails(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getOSMListDetails(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn)";

        KnCorpOSMInfoListDetailsRespDTO respDTO = new KnCorpOSMInfoListDetailsRespDTO();
        KnCorpOSMPersistDTO corpOSMPersistDTO=new KnCorpOSMPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil=KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipOsmDTO - ", ipOsmDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipOsmDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipOsmDTO.getCustomParamMap();
            if (ipOsmDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipOsmDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_OSM_LIST_DETAILS);
                    hookIPDTO.setData(ipOsmDTO);
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
            corpOSMPersistDTO.setInputDTO(ipOsmDTO);

            //Validating osm list id exists.
            Map<Integer, Integer> osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMap(ipOsmDTO.getCorpId(), xdmsHome, true, persisterTxn);
            corpOSMPersistDTO.setOsmListIdAndDefultMap(osmListIdAndDefaultMap);
            corpOSMPersistDTO.setOSMListId(ipOsmDTO.getOSMListId());

            knLogger.debug(methodName, "Before Validation", corpOSMPersistDTO);
            validatorFW.validate(corpOSMPersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            //getting osm list details
            respDTO = corpOSMInfoUtil.getOSMListDetailsByListIdReadonly(ipOsmDTO.getCorpId(), ipOsmDTO.getOSMListId(), xdmsHome, false, persisterTxn);
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
    public KnCorpResponseDTO assignOSMIdToGroup(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        String methodName = "assignOSMIdToGroup(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpOSMPersistDTO corpOSMPersistDTO=new KnCorpOSMPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil=KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipOsmDTO - ", ipOsmDTO);
        try {
            String corpId = ipOsmDTO.getCorpId();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipOsmDTO.getCustomParamMap();
            if (ipOsmDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipOsmDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.ASSIGN_OSM_TO_GROUP);
                    hookIPDTO.setData(ipOsmDTO);
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
            corpOSMPersistDTO.setInputDTO(ipOsmDTO);
            //Validating osm list id exists.
            Map<Integer, Integer> osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMap(corpId, xdmsHome, persisterTxn);
            corpOSMPersistDTO.setOsmListIdAndDefultMap(osmListIdAndDefaultMap);
            corpOSMPersistDTO.setOSMListId(ipOsmDTO.getOSMListId());

            Collection<String> assignGroupIds = ipOsmDTO.getAssignedOSMIdToGroupIds();
            Collection<String> removeAssignedOsmGroupIds = ipOsmDTO.getRemovedOSMIdFromGroupIds();

            List<Integer> reqGroupIds=new ArrayList<>();
            if(assignGroupIds!=null)
            reqGroupIds.addAll(assignGroupIds.stream().map(a->Integer.parseInt(a)).collect(Collectors.toList()));
            if(removeAssignedOsmGroupIds!=null)
            reqGroupIds.addAll(removeAssignedOsmGroupIds.stream().map(a->Integer.parseInt(a)).collect(Collectors.toList()));

            ArrayList<KnCorpGroupDTO> groupBasicInfo = groupInfoUtil.getGroupBasicInfoList(reqGroupIds, xdmsHome, persisterTxn);

            List<Integer> dbGroupIds = groupBasicInfo.stream().filter(g->g.getCorpId()==Integer.parseInt(corpId)).map(q -> q.getGroupId()).collect(Collectors.toList());
            List<Integer> groupIdsdiff = new ArrayList<>(reqGroupIds);
            groupIdsdiff.removeAll(dbGroupIds);
            corpOSMPersistDTO.setGroupIds(groupIdsdiff);
            List<KnCorpGroupDTO> corpGroupInfo = groupBasicInfo.stream().filter(g -> g.getCorpId() == Integer.parseInt(corpId)).toList();
            corpOSMPersistDTO.setCorpGroupInfoList(corpGroupInfo);
            knLogger.debug(methodName, "Before Validation", corpOSMPersistDTO);
            validatorFW.validate(corpOSMPersistDTO);
            knLogger.debug(methodName, "Validation Successfull");
            corpOSMInfoUtil.assignOSMIdToGroup(corpId,
                    ipOsmDTO.getOSMListId(),assignGroupIds,
                    removeAssignedOsmGroupIds,xdmsHome,persisterTxn);

            Set<Integer> groupIds = Stream.concat(assignGroupIds!=null?assignGroupIds.stream():Stream.of()
                    , removeAssignedOsmGroupIds!=null?removeAssignedOsmGroupIds.stream():Stream.of())
                    .map(ip->Integer.parseInt(ip))
                    .collect(Collectors.toSet());
            knLogger.debug(methodName, "groupIds to notify",groupIds);
            if(!groupIds.isEmpty()){
                //Adding check,As can update osm even though not assigned to group.
                //Updating Group's etag for osmlistIds.groupId, etag.
                Map<Integer, Collection<String>> addedGrpMemMap = new HashMap<Integer, Collection<String>>();
                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "groupEtagMap",groupEtagMap);
                Set<String> mdnList= new HashSet<>();
                for (Integer groupid : groupIds) {
                    List<String> members = sublistInfoUtil.selectGroupMemberList(groupid, xdmsHome, persisterTxn);
                    addedGrpMemMap.put(groupid, members);
                    mdnList.addAll(members);
                }
                //for profile mdn in mcxGroup
                //get groupInfo
                ArrayList<KnCorpGroupDTO> groupBasicInfoDb = groupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHome, persisterTxn);
                //filter mcxGroup
                List<KnCorpGroupDTO> mcxGroups = groupBasicInfoDb.stream().filter(e -> e.getMcxGrpInd().equals(KnConstants.MCX_GROUP_INDICATOR)).collect(Collectors.toList());
                knLogger.debug(methodName," mcxGroups :",mcxGroups);
                for(KnCorpGroupDTO mcxGroupsInfo:mcxGroups){
                    Set<String> upmIds = corpUserProfileUtil.retriveGroupProfileInfoByGroupId(mcxGroupsInfo.getGroupId(), xdmsHome, persisterTxn).keySet();
                    List<String> profileMdnList=new ArrayList<>();
                    for(String userProfileId:upmIds){
                        List<String> profileMdnListDB = corpUserProfileUtil.getProfileMdnByUPId(corpId,userProfileId, xdmsHome, persisterTxn);
                        profileMdnList.addAll(profileMdnListDB);
                    }
                    if(!profileMdnList.isEmpty()&&addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).isEmpty()){
                        addedGrpMemMap.put(mcxGroupsInfo.getGroupId(), profileMdnList);
                        mdnList.addAll(profileMdnList);
                    }else if(!profileMdnList.isEmpty()&&!addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).isEmpty()){
                        //for CRI ,SG mdns
                        addedGrpMemMap.get(mcxGroupsInfo.getGroupId()).addAll(profileMdnList);
                        mdnList.addAll(profileMdnList);
                    }
                }
                knLogger.debug(methodName," mdnList :",KnGDPRTemplate.mdnList(mdnList)
                        ," addedGrpMemMap :",KnGDPRTemplate.mapMdnAsValue(addedGrpMemMap));
                Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubscribersDirectory(mdnList, null, xdmsHome,
                        persisterTxn);
                //mdn, dirChgDto
                etagMap = commonInfoUtil.formSubscriberNotification(addedGrpMemMap, groupEtagMap,
                        com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap,
                        null, null);
                knLogger.debug(methodName, "etagMap", KnGDPRTemplate.mapKeyMdn(etagMap));
                respDTO.setChangeLogMap(etagMap);
            }
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
    public KnCorpOSMGroupListRespDTO getOSMGroupList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getOSMGroupList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn)";

        KnCorpOSMGroupListRespDTO response = new KnCorpOSMGroupListRespDTO();
        KnCorpOSMPersistDTO corpOSMPersistDTO=new KnCorpOSMPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil=KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipOsmDTO - ", ipOsmDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipOsmDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipOsmDTO.getCustomParamMap();
            if (ipOsmDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                if (customParams != null) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHome);
                    ipOsmDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_OSM_GROUP_LIST);
                    hookIPDTO.setData(ipOsmDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, response);
                            return response;
                        } else {
                            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                                response.setFailedDataList(responseDTO.getFailedDataList());
                            }
                        }
                    }
                }
            }
            //settting operation and entity id
            corpOSMPersistDTO.setInputDTO(ipOsmDTO);
            //Not validating osmListid as it will return empty if not present.
            response = corpOSMInfoUtil.getOSMGroupListByCorpAndOSMId(ipOsmDTO.getCorpId(),ipOsmDTO.getOSMListIds(), xdmsHome, true, persisterTxn);
            populate(response);
        } catch (KnCorpBOException e) {
            populate(response, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(response, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", response);
        return response;
    }
}
