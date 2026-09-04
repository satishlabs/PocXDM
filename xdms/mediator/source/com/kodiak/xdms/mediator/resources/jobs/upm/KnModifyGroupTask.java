/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnUPMJobScheduler;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpGroupInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpBasicInfoRespDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpListInfoRespDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;

public class KnModifyGroupTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyGroupTask.class);

    private static final int DISPATCHER_GROUP_TYPE = 2;
    private static final int MCX_GROUP_TYPE = 1;
    private String userProfileId;
    private String corpId;
    private String payLoad;

    private ICorpClientIntf corpClientIntf;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;
    private KnXDMCorpMediator corpMediator;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnPersisterTxn modifyUpmTranxn;
    private String profileMdn;
    private KnCorpResponseDTO userProfileDetails;
    private KnUPMJobScheduler upmJobScheduler;
    private KnGeneralCacheUtil generalCacheUtil;

    public KnModifyGroupTask(String corpId, String userProfileId, String payLoad
            , KnPersisterTxn modifyUpmTranxn, String profileMdn, KnCorpResponseDTO userProfileDetails) {
        corpClientIntf = new KnCorpClientImpl();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        corpMediator = KnXDMCorpMediator.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        this.corpId = corpId;
        this.userProfileId = userProfileId;
        this.payLoad = payLoad;
        this.modifyUpmTranxn = modifyUpmTranxn;
        this.profileMdn = profileMdn;
        this.userProfileDetails = userProfileDetails;
        this.upmJobScheduler = KnUPMJobScheduler.getInstance();
        this.generalCacheUtil = KnGeneralCacheUtil.getInstance();

    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName = "KnModifyGroupTask()";
        KnPersisterTxn modifyUpmGroupTxn = modifyUpmTranxn;
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
        KnTaskResult taskResult = new KnTaskResult();
        KnAuditHelper audit = KnAuditHelper.getAuditLogger("4002");
        int BROADCASTER = 3;
        knLogger.info(methodName, "ENTRY::-------------------->");
        try {
            //List<KnCorpEXDMSNotifyDto> microserviceNotify = new ArrayList<>();
            ipUserProfileDTO.setCorpId(corpId);
            ipUserProfileDTO.setProfileId(userProfileId);
            KnIPUserProfileDTO ipUserProfilePermDTO = KnCorpCommonInfoUtil.jsonToObject(payLoad, KnIPUserProfileDTO.class);
            KnCorpModifyUserProfileDTO modifyUpmReq = ipUserProfilePermDTO.getModifiedUserProfileDTO();
            knLogger.debug(methodName, "modifyUpmReq :", ipUserProfilePermDTO);
            Set<KnCorpGroupListInfoDTO> addUpmGroup = modifyUpmReq.getAddedGroupList();
            Set<KnCorpGroupListInfoDTO> modifyUpmGroup = modifyUpmReq.getModifiedGroupList();
            Set<String> removeGroupIds = modifyUpmReq.getRemovedGroupIdsList();
            taskResult.setAddUpmGroup(addUpmGroup);
            taskResult.setModifyUpmGroup(modifyUpmGroup);
            Set<KnCorpGroupListInfoDTO> groupList = new HashSet<>();
            List<Integer> upmGroupIds = new ArrayList<>();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId);
            String locationFeatureFlag = paramNameValueMapCommon.get("LOCATION_FEATURE_FLAG") != null ? paramNameValueMapCommon.get("LOCATION_FEATURE_FLAG") : "0";
            knLogger.debug("locationFeatureFlag", locationFeatureFlag);
            if (userProfileDetails != null) {
                groupList = userProfileDetails.getUserProfile().getGroupList();
                upmGroupIds = groupList.stream().map(e -> e.getGroupID()).collect(Collectors.toList());
            }
            boolean locwatcherUPM = false;
            for (int countUPM : userProfileDetails.getUpmCount()) {
                if (countUPM >= 2) {
                    locwatcherUPM = true;
                }
            }
            //getting all group infos in upm
            Set<KnCorpGroupListInfoDTO> allUpmGroup = new HashSet<>();
            if (addUpmGroup != null && !addUpmGroup.isEmpty()) {
                upmGroupIds.addAll(addUpmGroup.stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList()));
            }
            KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
            groupInfoDTO.setGroupIds(upmGroupIds);
            KnCorpGrpListInfoRespDto groupInfoResp = corpClientIntf.getListOfGroupInfo(groupInfoDTO, modifyUpmGroupTxn);
            knLogger.debug(methodName, "groupInfoResp :", groupInfoResp);
            List<KnCorpGrpBasicInfoRespDto> groupInfoList = groupInfoResp.getGroupListInfo();
            Map<Integer, Integer> groupIdNTypeMap = groupInfoList.stream().collect(
                    Collectors.toMap(KnCorpGrpBasicInfoRespDto::getGroupId, KnCorpGrpBasicInfoRespDto::getGrpType));
            Set<String> locWatcherGroupIdList = new HashSet<>();
            Set<String> existignLocWatcherGroupIdList = new HashSet<>();
            //exisitng group
            if (!groupList.isEmpty()) {
                for (KnCorpGroupListInfoDTO corpGroupListInfoDTO : groupList) {
                    if (corpGroupListInfoDTO.getGrpMemProps().getIsLocSupervisor() == ENABLED
                            || (groupIdNTypeMap.get(corpGroupListInfoDTO.getGroupID()) != null
                            && groupIdNTypeMap.get(corpGroupListInfoDTO.getGroupID()).equals(DISPATCHER_GROUP_TYPE))) {

                        locWatcherGroupIdList.add(corpGroupListInfoDTO.getGroupID().toString());
                        existignLocWatcherGroupIdList.add(corpGroupListInfoDTO.getGroupID().toString());
                    }
                }
            }

            //getAll mcsXcapUri's for sending MCS Group notifications
            Set<String> allMcsXcapUris = genInfoUtil.getMCSXCAPRootURIs(modifyUpmGroupTxn);
            taskResult.setAllMcsXcapUris(allMcsXcapUris);

            Set<String> addedLocWatcherList = new HashSet<>();
            Set<String> removedLocWatcherList = new HashSet<>();
            Set<String> addedDispatcher = new HashSet<>();
            Set<String> removedDispatcher = new HashSet<>();
            Map<Integer, Boolean> grpLocWatcherMap = new HashMap<>();
            var dispatcherList = new HashSet<String>();
            KnXDMTalkGroupRequestDTO campedGroupReq = null;
            List<KnXDMTalkGroupInfoDTO> addedCampGrpList = new ArrayList<>();
            List<KnXDMTalkGroupInfoDTO> modifyTalkGroupInfo = new ArrayList<>();
            List<KnXDMTalkGroupInfoDTO> removeTalkGroupInfo = new ArrayList<>();

            KnXDMBulkCorpGroupInfoRequestDTO xdmBulkRequestDtoForAdd = new KnXDMBulkCorpGroupInfoRequestDTO();
            KnXDMCorpGroupInfoRequestDTO xdmRequestDtoForAdd = new KnXDMCorpGroupInfoRequestDTO();
            Integer groupIds = null;
            if (addUpmGroup != null && !addUpmGroup.isEmpty()) {
                knLogger.debug(methodName, "adding member :----------->", KnGDPRTemplate.mdn(profileMdn), "for group:", addUpmGroup);
                var groupZoneIdMap = new HashMap<Integer, Integer>();
                var groupPriorityIdMap = new HashMap<Integer, Integer>();
                var groupChannelIdMap = new HashMap<Integer, Integer>();
                var locWatcherList = new HashSet<String>();
                //getting transaction count
                int count = generalCacheUtil.getTransactionCountBasedOnStatus();
                for (KnCorpGroupListInfoDTO groupInfo : addUpmGroup) {
                    groupIds = groupInfo.getGroupID();
                    Integer zoneId = groupInfo.getGroupZone();
                    Integer channelId = groupInfo.getGroupChannel();
                    Integer priority = groupInfo.getGroupPriority();
                    if (zoneId != null) groupZoneIdMap.put(groupIds, zoneId);
                    if (priority != null) groupPriorityIdMap.put(groupIds, priority);
                    if (channelId != null) groupChannelIdMap.put(groupIds, channelId);
                    KnXDMGroupMdnInfoDTO groupMdnInfoDTO = new KnXDMGroupMdnInfoDTO();
                    List<KnXDMGroupMdnInfoDTO> addedGroupMdnList = new ArrayList<>();
                    groupMdnInfoDTO.setMdn(profileMdn);
                    groupMdnInfoDTO.setSupervisor(groupInfo.getGrpMemProps().getIsSupervisor());
                    Integer locWatcher = groupInfo.getGrpMemProps().getIsLocSupervisor();
                    groupMdnInfoDTO.setLocWatcher(locWatcher);
                    groupMdnInfoDTO.setIsOSMAuthorize(groupInfo.getGrpMemProps().getIsOSMAuthorized());
                    groupMdnInfoDTO.setInCallPermission(groupInfo.getGrpMemProps().getIncallAllowed());
                    groupMdnInfoDTO.setCallReceivePermission(groupInfo.getGrpMemProps().getCallTerminateAllowed());
                    groupMdnInfoDTO.setCallInitiatePermission(groupInfo.getGrpMemProps().getCallInitiateAllowed());
                    groupMdnInfoDTO.setVideoCallInitiatePermission(groupInfo.getGrpMemProps().getVideoCallInitiateAllowed());
                    groupMdnInfoDTO.setVideoCallReceivePermission(groupInfo.getGrpMemProps().getVideoCallReceiveAllowed());
                    groupMdnInfoDTO.setVideoInCallPermission(groupInfo.getGrpMemProps().getVideoInCallAllowed());
                    groupMdnInfoDTO.setBroadcaster(groupInfo.getGrpMemProps().getIsBroadcaster());
                    addedGroupMdnList.add(groupMdnInfoDTO);
                    xdmRequestDtoForAdd.setGroupMembers(addedGroupMdnList);
                    xdmRequestDtoForAdd.setGroupId(groupIds.toString());
                    xdmRequestDtoForAdd.setCorpId(corpId);
                    xdmRequestDtoForAdd.setClientType(CLIENT_TYPE_CAT_UI);
                    xdmRequestDtoForAdd.setETag("-9999");
                    xdmRequestDtoForAdd.setUpmCall(true);
                    xdmRequestDtoForAdd.setUserProfileId(userProfileId);

                    if (xdmBulkRequestDtoForAdd.getGroupInfoMap() == null) {
                        var xDMGroupPropertyInfoDTOList = new HashMap<Integer, KnXDMCorpGroupInfoRequestDTO>();
                        xDMGroupPropertyInfoDTOList.put(groupIds, xdmRequestDtoForAdd);
                        xdmBulkRequestDtoForAdd.setGroupInfoMap(xDMGroupPropertyInfoDTOList);
                    } else {
                        xdmBulkRequestDtoForAdd.getGroupInfoMap().put(groupIds, xdmRequestDtoForAdd);
                    }

                    if (locWatcher != null && locWatcher.equals(ENABLED)) {
                        locWatcherList.add(profileMdn);
                        grpLocWatcherMap.put(groupIds, Boolean.TRUE);
                    } else if (groupIdNTypeMap.get(groupIds).equals(DISPATCHER_GROUP_TYPE)) {
                        grpLocWatcherMap.put(groupIds, Boolean.FALSE);
                        dispatcherList.add(profileMdn);
                    } else {
                        grpLocWatcherMap.put(groupIds, Boolean.FALSE);
                    }
                }
                xdmBulkRequestDtoForAdd.setCorpId(corpId);
                xdmBulkRequestDtoForAdd.setUserProfileId(userProfileId);
                xdmBulkRequestDtoForAdd.setGrpLocWatcherMap(grpLocWatcherMap);
                KnCorpResponseDTO addGroupResp = corpMediator.modifyBulkCorpGroup(xdmBulkRequestDtoForAdd, modifyUpmGroupTxn);
                knLogger.debug("addGroupResp :", addGroupResp);
                taskResult.setGroupIds(addGroupResp.getGroupIds());
                if (addGroupResp.getEnabledDispatchMemListMap() != null) {
                    Collection<String> enabledDispatcherMembers = addGroupResp.getEnabledDispatchMemListMap().values().stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    addGroupResp.setEnabledDispatchMemList(enabledDispatcherMembers);
                }


                if (Objects.equals(Integer.parseInt(locationFeatureFlag), DISABLE_LOCATION_FEATURE_FOR_LG_FLAG)) {
                    // find all member of large dispatch group and disable them and add them to taskResult.setDisabledDispatchMemList
                    // set enable dispatch list to empty
                    removedDispatcher.clear();
                    removedDispatcher.add(profileMdn);
                    if (null != addGroupResp.getDisabledDispatchMemList()) {
                        removedDispatcher.addAll(addGroupResp.getDisabledDispatchMemList());
                    }
                    addedDispatcher.clear();
                }

                knLogger.info("removedDispatcher size", removedDispatcher.size());

                if (addGroupResp.getAddedGroupMembersMap()!=null){
                    addGroupResp.setAddedGroupMembersMap(addGroupResp.getAddedGroupMembersMap());
                }
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == addGroupResp.getStatus()) {
                    knLogger.debug(methodName, "modifyCorpGroup Operation Failed");
                    throw new KnXDMServerException(addGroupResp.getStatusCode(), addGroupResp.getMessage());
                }
                //should have only add locwatcher or dispatcher
                for (KnCorpGroupListInfoDTO groupInfo : addUpmGroup) {
                    Integer groupID = groupInfo.getGroupID();
                    Integer locWatcher = groupInfo.getGrpMemProps().getIsLocSupervisor();
                    if (locWatcher == ENABLED) {
                        locWatcherGroupIdList.add(groupID.toString());
                        addedLocWatcherList.add(profileMdn);
                        //this below setting is for only mcx group
                        if (addGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && null != addGroupResp.getProfileMDNs() &&
                                !addGroupResp.getProfileMDNs().isEmpty()) {
                            addedDispatcher.addAll(addGroupResp.getProfileMDNs());
                        }
                        if (addGroupResp.getEnabledDispatchMemList() != null) {
                            addedDispatcher.addAll(addGroupResp.getEnabledDispatchMemList());
                        }
                    } else if (groupIdNTypeMap.get(groupID) != null && groupIdNTypeMap.get(groupID).equals(DISPATCHER_GROUP_TYPE)) {
                        locWatcherGroupIdList.add(groupID.toString());
                        addedDispatcher.add(profileMdn);
                    } else if (addGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && addGroupResp.getIsDispacherPresent()) {
                        locWatcherGroupIdList.add(groupID.toString());
                        addedDispatcher.add(profileMdn);
                    } else if (addGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && addGroupResp.getUserProfileCount() >= 1 &&
                            addGroupResp.getLocwatcherCount() >= 1) {
                        locWatcherGroupIdList.add(groupID.toString());
                        addedDispatcher.add(profileMdn);
                    }
                }
                if (Objects.equals(addGroupResp.getStatusCode(), "00000") && count < TRANSACTION_COUNT) {
                    KnIPUserProfileDTO ipUserProfilesDTO = new KnIPUserProfileDTO();
                    Set<KnCorpGroupListInfoDTO> addUpmGroupIds = new HashSet<>();
                    addUpmGroupIds.addAll(addUpmGroup);
                    ipUserProfilesDTO.setAddedGroupList(addUpmGroupIds);
                    ipUserProfilesDTO.setAddedMember(profileMdn);
                    String assignUpmJsonString = commonInfoUtil.ObjToJson(ipUserProfilesDTO);
                    //generating random transactionId
                    UUID uuid = UUID.randomUUID();
                    //insertion the data into the GG .
                    KnAsyncJobDTO knAsyncJobWatcherDTO = commonMediator.createJobNotifyDTO(corpId, uuid.toString(), userProfileId,
                            UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value(), profileMdn, UPM_RESOURCE_TYPE.MDN.Value(),
                            assignUpmJsonString, UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value(),
                            null);
                    knLogger.debug(methodName, "knAsyncJobWatcherDTOAddCase-->", knAsyncJobWatcherDTO);
                    upmJobScheduler.addJob(knAsyncJobWatcherDTO);
                }
                taskResult.setAddGroupResp(addGroupResp);
                //CAMPEDGROUPINFO
                if (groupZoneIdMap != null && !groupZoneIdMap.isEmpty() && groupPriorityIdMap != null &&
                        !groupPriorityIdMap.isEmpty() && groupZoneIdMap != null && !groupZoneIdMap.isEmpty()
                        && !groupIdNTypeMap.isEmpty()) {
                    if (null == campedGroupReq)
                        campedGroupReq = new KnXDMTalkGroupRequestDTO();
                    for (var entry : groupZoneIdMap.entrySet()) {
                        int groupIDs = entry.getKey();
                        if (!groupChannelIdMap.containsKey(groupIDs) && !groupPriorityIdMap.isEmpty()) continue;
                        KnXDMTalkGroupInfoDTO CampGrpInformation = new KnXDMTalkGroupInfoDTO();
                        CampGrpInformation.setPriority(groupPriorityIdMap.get(groupIDs));
                        CampGrpInformation.setGroupId(groupIDs);
                        addedCampGrpList.add(CampGrpInformation);
                    }
                    campedGroupReq.setCorpId(corpId);
                    campedGroupReq.setMdn(profileMdn);
                    if (ipUserProfilePermDTO.getModifiedUserProfileDTO() != null && ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode() != null) {
                        campedGroupReq.setMode(Integer.parseInt(ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode()));
                        knLogger.debug(methodName, "Getting mode from couchbase: ", ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode());
                    }
                    campedGroupReq.setETag("-9999");
                    campedGroupReq.setAddedCampGrpList(addedCampGrpList);
                    campedGroupReq.setModifiedCampGrpList(modifyTalkGroupInfo);
                    campedGroupReq.setRemovedCampGrpList(removeTalkGroupInfo);
                    campedGroupReq.setUpmCall(true);

                }
            }

            if (modifyUpmGroup != null && !modifyUpmGroup.isEmpty()) {
                knLogger.debug(methodName, "modifying member:----->", KnGDPRTemplate.mdn(profileMdn), " for group :", modifyUpmGroup);
                KnXDMBulkCorpGroupInfoRequestDTO xdmBulkRequestDto = new KnXDMBulkCorpGroupInfoRequestDTO();
                Collection<String> locWatcherList = new ArrayList<>();
                var groupZoneIdMap = new HashMap<Integer, Integer>();
                var groupPriorityIdMap = new HashMap<Integer, Integer>();
                var groupChannelIdMap = new HashMap<Integer, Integer>();
                //getting transaction count
                int count = generalCacheUtil.getTransactionCountBasedOnStatus();
                for (KnCorpGroupListInfoDTO groupInfo : modifyUpmGroup) {
                    Integer groupId = groupInfo.getGroupID();
                    Integer zoneId = groupInfo.getGroupZone();
                    Integer channelId = groupInfo.getGroupChannel();
                    Integer priority = groupInfo.getGroupPriority();
                    Integer locWatcher = groupInfo.getGrpMemProps().getIsLocSupervisor();
                    if (zoneId != null) groupZoneIdMap.put(groupId, zoneId);
                    if (priority != null) groupPriorityIdMap.put(groupId, priority);
                    if (channelId != null) groupChannelIdMap.put(groupId, channelId);
                    if (locWatcher != null && locWatcher.equals(ENABLED)) {
                        locWatcherList.add(profileMdn);
                        grpLocWatcherMap.put(groupId, Boolean.TRUE);
                    } else if (groupIdNTypeMap.get(groupId).equals(DISPATCHER_GROUP_TYPE)) {
                        grpLocWatcherMap.put(groupId, Boolean.FALSE);
                        //dispatcherList.add(profileMdn);
                    } else {
                        grpLocWatcherMap.put(groupId, Boolean.FALSE);
                    }
                    KnXDMCorpGroupInfoRequestDTO xdmRequestDto = new KnXDMCorpGroupInfoRequestDTO();
                    KnXDMGroupMdnInfoDTO groupMdnInfoDTO = new KnXDMGroupMdnInfoDTO();
                    List<KnXDMGroupMdnInfoDTO> addedGroupMdnList = new ArrayList<>();
                    groupMdnInfoDTO.setMdn(profileMdn);

                    groupMdnInfoDTO.setSupervisor(groupInfo.getGrpMemProps().getIsSupervisor());
                    groupMdnInfoDTO.setLocWatcher(locWatcher);
                    groupMdnInfoDTO.setIsOSMAuthorize(groupInfo.getGrpMemProps().getIsOSMAuthorized());
                    groupMdnInfoDTO.setInCallPermission(groupInfo.getGrpMemProps().getIncallAllowed());
                    groupMdnInfoDTO.setCallReceivePermission(groupInfo.getGrpMemProps().getCallTerminateAllowed());
                    groupMdnInfoDTO.setCallInitiatePermission(groupInfo.getGrpMemProps().getCallInitiateAllowed());
                    groupMdnInfoDTO.setVideoCallInitiatePermission(groupInfo.getGrpMemProps().getVideoCallInitiateAllowed());
                    groupMdnInfoDTO.setVideoCallReceivePermission(groupInfo.getGrpMemProps().getVideoCallReceiveAllowed());
                    groupMdnInfoDTO.setVideoInCallPermission(groupInfo.getGrpMemProps().getVideoInCallAllowed());
                    groupMdnInfoDTO.setBroadcaster(groupInfo.getGrpMemProps().getIsBroadcaster());
                    groupMdnInfoDTO.setCorpID(Integer.parseInt(corpId));
                    addedGroupMdnList.add(groupMdnInfoDTO);

                    xdmRequestDto.setModifiedMembers(addedGroupMdnList);
                    xdmRequestDto.setGroupId(groupId.toString());
                    xdmRequestDto.setCorpId(corpId);
                    xdmRequestDto.setClientType(CLIENT_TYPE_CAT_UI);
                    xdmRequestDto.setETag("-9999");
                    xdmRequestDto.setUpmCall(true);
                    xdmRequestDto.setUserProfileId(userProfileId);
                    xdmRequestDto.setGrpLocWatcherMap(grpLocWatcherMap);


                    xdmBulkRequestDto.setCorpId(corpId);
                    xdmBulkRequestDto.setUserProfileId(userProfileId);
                    if (xdmBulkRequestDto.getGroupInfoMap() == null) {
                        var xDMGroupPropertyInfoDTOList = new HashMap<Integer, KnXDMCorpGroupInfoRequestDTO>();
                        xDMGroupPropertyInfoDTOList.put(groupId, xdmRequestDto);
                        xdmBulkRequestDto.setGroupInfoMap(xDMGroupPropertyInfoDTOList);
                    } else {
                        xdmBulkRequestDto.getGroupInfoMap().put(groupId, xdmRequestDto);
                    }
                }
                knLogger.debug(methodName, "xdmBulkRequestDto2 :", xdmBulkRequestDto);
                KnCorpResponseDTO modifyGroupMemPropResp = corpMediator.modifyBulkCorpGroup(xdmBulkRequestDto, modifyUpmGroupTxn);
                Collection<KnXDMGroupMdnInfoDTO> modifiedMembers = new ArrayList<>();
                Collection<KnCorpGroupMemberDTO> modifiedGrpMembers = new ArrayList<>();
                if (xdmBulkRequestDto.getGroupInfoMap() != null) {
                    for (KnXDMCorpGroupInfoRequestDTO reqDto : xdmBulkRequestDto.getGroupInfoMap().values()) {
                        if (reqDto != null && reqDto.getModifiedMembers() != null) {
                            modifiedMembers.addAll(reqDto.getModifiedMembers());
                        }
                    }
                }
                for (KnXDMGroupMdnInfoDTO mem : modifiedMembers) {
                    KnCorpGroupMemberDTO contact = new KnCorpGroupMemberDTO();
                    contact.setMdn(mem.getMdn());
                    contact.setSupervisory(mem.getSupervisor());
                    contact.setBroadcaster(mem.getBroadcaster());
                    contact.setCallInitiatePermission(mem.getCallInitiatePermission());
                    contact.setCallReceivePermission(mem.getCallReceivePermission());
                    contact.setInCallPermission(mem.getInCallPermission());
                    contact.setVideoCallInitiatePermission(mem.getVideoCallInitiatePermission());
                    contact.setVideoCallReceivePermission(mem.getVideoCallReceivePermission());
                    contact.setVideoInCallPermission(mem.getVideoInCallPermission());
                    contact.setLocWatcher(mem.getLocWatcher());
                    contact.setIsOSMAuthorize(mem.getIsOSMAuthorize());
                    modifiedGrpMembers.add(contact);
                }
                // KnCorpResponseDTO modifyGroupMemPropResp = corpMediator.modifyCorpGroup(xdmRequestDto, modifyUpmGroupTxn);
                taskResult.setGroupIds(modifyGroupMemPropResp.getGroupIds());
                if (modifyGroupMemPropResp.getEnabledDispatchMemListMap() != null) {
                    Collection<String> enabledDispatcherMembers = modifyGroupMemPropResp.getEnabledDispatchMemListMap().values().stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    modifyGroupMemPropResp.setEnabledDispatchMemList(enabledDispatcherMembers);
                }
                knLogger.info("addedDispatcher size ", addedDispatcher.size());
                knLogger.info("removedDispatcher size ", removedDispatcher.size());
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == modifyGroupMemPropResp.getStatus()) {
                    knLogger.debug(methodName, "modifyCorpGroup Operation Failed");
                    throw new KnXDMServerException(modifyGroupMemPropResp.getStatusCode(), modifyGroupMemPropResp.getMessage());
                }
                for (KnCorpGroupListInfoDTO groupInfo : modifyUpmGroup) {
                    Integer groupId = groupInfo.getGroupID();
                    Integer zoneId = groupInfo.getGroupZone();
                    Integer channelId = groupInfo.getGroupChannel();
                    Integer priority = groupInfo.getGroupPriority();
                    Integer locWatcher = groupInfo.getGrpMemProps().getIsLocSupervisor();
                    taskResult.setUpmCount(modifyGroupMemPropResp.getUserProfileCount());
                    //to enable 27bit for all the profile mdns .
                    if (modifyGroupMemPropResp.getMcxGrpInd() == MCX_GROUP_TYPE) {
                        modifyGroupMemPropResp.setModifiedGrpMembers(modifiedGrpMembers);
                        if (locWatcher == ENABLED) {
                            grpLocWatcherMap.put(groupId, Boolean.TRUE);
                            locWatcherGroupIdList.add(groupId.toString());
                            addedLocWatcherList.addAll(modifyGroupMemPropResp.getProfileMDNs());
                        } else if (!locwatcherUPM && modifyGroupMemPropResp.getIsLocationDisabled()) {
                            locWatcherGroupIdList.remove(groupId.toString());
                            grpLocWatcherMap.put(groupId, Boolean.FALSE);
                        }
                    } else {
                        if (locWatcher == ENABLED) {
                            grpLocWatcherMap.put(groupId, Boolean.TRUE);
                            locWatcherGroupIdList.add(groupId.toString());
                            addedLocWatcherList.add(profileMdn);
                            if (modifyGroupMemPropResp.getEnabledDispatchMemList() != null) {
                                addedDispatcher.addAll(modifyGroupMemPropResp.getEnabledDispatchMemList());
                            }
                        } else if (groupIdNTypeMap.get(groupId) != null && groupIdNTypeMap.get(groupId).equals(DISPATCHER_GROUP_TYPE)) {
                            grpLocWatcherMap.put(groupId, Boolean.TRUE);
                            locWatcherGroupIdList.add(groupId.toString());
                            addedDispatcher.add(profileMdn);
                        } else {
                            locWatcherGroupIdList.remove(groupId.toString());
                            grpLocWatcherMap.put(groupId, Boolean.FALSE);
                        }
                    }
                    knLogger.debug(methodName, "modifyGroupMemPropResp:", modifyGroupMemPropResp);

                    //setting zone and channel as 0 ,as isBroadcaster=0,As CAT is not setting zone & channel as null for isBroadcaster=0
                    if (modifyGroupMemPropResp.getActualDeletedBroadcaster() != null
                            && !modifyGroupMemPropResp.getActualDeletedBroadcaster().isEmpty()) {
                        zoneId = null;
                        channelId = null;
                        knLogger.info(" setting zone and channel as 0 ,as isBroadcaster=0", modifyGroupMemPropResp.getActualDeletedBroadcaster());
                    }

                    if (modifyGroupMemPropResp.getMcxGrpInd() == MCX_GROUP_TYPE && modifyGroupMemPropResp.getIsLocationDisabled()) {
                        if (existignLocWatcherGroupIdList.contains(groupId.toString())
                                && (grpLocWatcherMap.get(groupId).equals(Boolean.FALSE))
                                && locWatcherGroupIdList.size() == 0) {
                            removedLocWatcherList.addAll(modifyGroupMemPropResp.getProfileMDNs());
                        }
                    } else if (existignLocWatcherGroupIdList.contains(groupId.toString()) && null != grpLocWatcherMap.get(groupId)
                            && (grpLocWatcherMap.get(groupId).equals(Boolean.FALSE))
                            && locWatcherGroupIdList.size() == 0) {
                        removedLocWatcherList.add(profileMdn);
                    }
                    taskResult.setModifyGroupMemPropResp(modifyGroupMemPropResp);
                    //CAMPEDGROUPINFO
                    if (zoneId != null && channelId != null
                            && !groupIdNTypeMap.isEmpty()
                            && !groupIdNTypeMap.get(groupId).equals(BROADCASTER)) {

                        if (null == campedGroupReq)
                            campedGroupReq = new KnXDMTalkGroupRequestDTO();

                        KnXDMTalkGroupInfoDTO CampGrpInfo = new KnXDMTalkGroupInfoDTO();
                        if (priority != null) {
                            CampGrpInfo.setPriority(priority);
                            CampGrpInfo.setGroupId(groupId);
                            CampGrpInfo.setChannel(channelId);
                            CampGrpInfo.setZone(zoneId);
                            modifyTalkGroupInfo.add(CampGrpInfo);
                        } else {
                            //if not in scan list is set
                            CampGrpInfo.setGroupId(Integer.valueOf(groupId));
                            removeTalkGroupInfo.add(CampGrpInfo);
                        }

                        campedGroupReq.setCorpId(corpId);
                        campedGroupReq.setMdn(profileMdn);
                        if (ipUserProfilePermDTO.getModifiedUserProfileDTO() != null && ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode() != null) {
                            campedGroupReq.setMode(Integer.parseInt(ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode()));
                            knLogger.debug(methodName, "Getting mode from couchbase: ", ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode());
                        }
                        knLogger.debug(methodName, "Profile MDN= ", profileMdn);
                        campedGroupReq.setETag("-9999");
                        campedGroupReq.setAddedCampGrpList(addedCampGrpList);
                        campedGroupReq.setModifiedCampGrpList(modifyTalkGroupInfo);
                        campedGroupReq.setRemovedCampGrpList(removeTalkGroupInfo);
                        campedGroupReq.setUpmCall(true);

                    } else if (null == zoneId && null == channelId && priority == null) {
                        if (null == campedGroupReq)
                            campedGroupReq = new KnXDMTalkGroupRequestDTO();
                        KnXDMTalkGroupInfoDTO CampGrpInfo = new KnXDMTalkGroupInfoDTO();
                        CampGrpInfo.setGroupId(Integer.valueOf(groupId));
                        removeTalkGroupInfo.add(CampGrpInfo);
                        campedGroupReq.setETag("-9999");
                        campedGroupReq.setAddedCampGrpList(addedCampGrpList);
                        campedGroupReq.setModifiedCampGrpList(modifyTalkGroupInfo);
                        campedGroupReq.setRemovedCampGrpList(removeTalkGroupInfo);
                        campedGroupReq.setUpmCall(true);
                        campedGroupReq.setCorpId(corpId);
                        campedGroupReq.setMdn(profileMdn);
                        knLogger.debug(methodName, "All null adding to removed list");
                    }
                }

                if (Objects.equals(modifyGroupMemPropResp.getStatusCode(), "00000") && count < TRANSACTION_COUNT) {
                    KnIPUserProfileDTO ipUserProfilesDTO = new KnIPUserProfileDTO();
                    ipUserProfilesDTO.setAddedGroupList(modifyUpmGroup);
                    ipUserProfilesDTO.setAddedMember(profileMdn);
                    String assignUpmJsonString = commonInfoUtil.ObjToJson(ipUserProfilesDTO);
                    //generating random transactionId
                    UUID uuid = UUID.randomUUID();
                    //insertion the data into the GG .
                    KnAsyncJobDTO knAsyncJobWatcherDTO = commonMediator.createJobNotifyDTO(corpId, uuid.toString(), userProfileId,
                            UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value(), profileMdn, UPM_RESOURCE_TYPE.MDN.Value(),
                            assignUpmJsonString, UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value(),
                            null);
                    knLogger.debug(methodName, "knAsyncJobWatcherDTO-->", knAsyncJobWatcherDTO);
                    upmJobScheduler.addJob(knAsyncJobWatcherDTO);
                }
            }

            LinkedList<String> removeMdnList = new LinkedList<>();
            Integer removeGroupIds1 = null;
            KnXDMBulkCorpGroupInfoRequestDTO xdmBulkRequestDto = new KnXDMBulkCorpGroupInfoRequestDTO();
            if (removeGroupIds != null && !removeGroupIds.isEmpty()) {
                knLogger.debug(methodName, "removing member:---->", KnGDPRTemplate.mdn(profileMdn), "from group :", removeGroupIds);
                KnCorpResponseDTO removeGroupMemPropResp = new KnCorpGrpBasicInfoRespDto();
                int count = generalCacheUtil.getTransactionCountBasedOnStatus();
                for (String groupId : removeGroupIds) {
                    removeGroupIds1 = Integer.parseInt(groupId);
                    KnXDMCorpGroupInfoRequestDTO xdmRequestDto = new KnXDMCorpGroupInfoRequestDTO();
                    removeMdnList.add(profileMdn);
                    xdmRequestDto.setRemovedMdnList(removeMdnList);
                    xdmRequestDto.setGroupId(groupId);
                    xdmRequestDto.setCorpId(corpId);
                    xdmRequestDto.setClientType(CLIENT_TYPE_CAT_UI);
                    xdmRequestDto.setETag("-9999");
                    xdmRequestDto.setUpmCall(true);
                    xdmRequestDto.setUserProfileId(userProfileId);
                    xdmBulkRequestDto.setCorpId(corpId);
                    xdmBulkRequestDto.setUserProfileId(userProfileId);
                    if (xdmBulkRequestDto.getGroupInfoMap() == null) {
                        var xDMGroupPropertyInfoDTOList = new HashMap<Integer, KnXDMCorpGroupInfoRequestDTO>();
                        xDMGroupPropertyInfoDTOList.put(Integer.valueOf(groupId), xdmRequestDto);
                        xdmBulkRequestDto.setGroupInfoMap(xDMGroupPropertyInfoDTOList);
                    } else {
                        xdmBulkRequestDto.getGroupInfoMap().put(Integer.valueOf(groupId), xdmRequestDto);
                    }
                }
                knLogger.debug("xdmBulkRequestDto3:", xdmBulkRequestDto);
                removeGroupMemPropResp = corpMediator.modifyBulkCorpGroup(xdmBulkRequestDto, modifyUpmGroupTxn);
                knLogger.debug(methodName, "removeGroupMemPropResp :", removeGroupMemPropResp);
                if (removeGroupMemPropResp.getDisabledDispatchMemListMap() != null) {
                    Collection<String> disabledDispatchMembers = removeGroupMemPropResp.getDisabledDispatchMemListMap().values().stream()
                            .flatMap(List::stream)
                            .collect(Collectors.toList());
                    removeGroupMemPropResp.setDisabledDispatchMemList(disabledDispatchMembers);
                }
                if (Objects.equals(Integer.parseInt(locationFeatureFlag), DISABLE_LOCATION_FEATURE_FOR_LG_FLAG)) {
                    // find all member of large dispatch group and disable them and add them to taskResult.setDisabledDispatchMemList
                    // set enable dispatch list to empty
                    addedDispatcher.clear();
                    addedDispatcher.add(profileMdn);
                    if (null != removeGroupMemPropResp.getEnabledDispatchMemList()) {
                        addedDispatcher.addAll(removeGroupMemPropResp.getEnabledDispatchMemList());
                    }
                    removedDispatcher.clear();
                    knLogger.info("addedDispatcher size", addedDispatcher.size());
                }
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == removeGroupMemPropResp.getStatus()) {
                    knLogger.debug(methodName, "removeGroupMemPropResp Operation Failed");
                    throw new KnXDMServerException(removeGroupMemPropResp.getStatusCode(), removeGroupMemPropResp.getMessage());
                }
                taskResult.setGroupIds(removeGroupMemPropResp.getGroupIds());
                taskResult.setUpmCount(removeGroupMemPropResp.getUserProfileCount());
                taskResult.setRemoveGroupResp(removeGroupMemPropResp);
                if (Objects.equals(removeGroupMemPropResp.getStatusCode(), "00000") && count < TRANSACTION_COUNT) {
                    Set<String> removedGroupIds = new HashSet<>();
                    KnIPUserProfileDTO ipUserProfilesDTO = new KnIPUserProfileDTO();
                    removedGroupIds.addAll(removeGroupIds);
                    ipUserProfilesDTO.setRemovedGroupList(removedGroupIds);
                    LinkedList<String> removedMDNList = new LinkedList<>();
                    removedMDNList.add(profileMdn);
                    ipUserProfilesDTO.setRemovedMDNList(removedMDNList);
                    String assignUpmJsonString = commonInfoUtil.ObjToJson(ipUserProfilesDTO);
                    //generating random transactionId
                    UUID uuid = UUID.randomUUID();
                    //insertion the data into the GG .
                    KnAsyncJobDTO knAsyncJobWatcherDTO = commonMediator.createJobNotifyDTO(corpId, uuid.toString()
                            , userProfileId, UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value(), profileMdn,
                            UPM_RESOURCE_TYPE.MDN.Value(), assignUpmJsonString, UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value(),
                            null);
                    knLogger.debug(methodName, "knAsyncJobWatcherDTORemoveCase-->", knAsyncJobWatcherDTO);
                    upmJobScheduler.addJob(knAsyncJobWatcherDTO);
                }
                for (String groupId : removeGroupIds) {
                    if (null == campedGroupReq)
                        campedGroupReq = new KnXDMTalkGroupRequestDTO();

                    KnXDMTalkGroupInfoDTO CampGrpInfo = new KnXDMTalkGroupInfoDTO();
                    CampGrpInfo.setGroupId(Integer.valueOf(groupId));
                    removeTalkGroupInfo.add(CampGrpInfo);

                    campedGroupReq.setCorpId(corpId);
                    campedGroupReq.setMdn(profileMdn);
                    if (ipUserProfilePermDTO.getModifiedUserProfileDTO() != null && ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode() != null) {
                        campedGroupReq.setMode(Integer.parseInt(ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode()));
                        knLogger.debug(methodName, "Getting mode from couchbase: ", ipUserProfilePermDTO.getModifiedUserProfileDTO().getTgscMode());
                    }
                    campedGroupReq.setETag("-9999");
                    campedGroupReq.setAddedCampGrpList(addedCampGrpList);
                    campedGroupReq.setModifiedCampGrpList(modifyTalkGroupInfo);
                    campedGroupReq.setRemovedCampGrpList(removeTalkGroupInfo);
                    campedGroupReq.setUpmCall(true);
                }
                boolean locWatcherExisted = !existignLocWatcherGroupIdList.isEmpty();
                locWatcherGroupIdList.removeAll(removeGroupIds);
                if (removeGroupMemPropResp.getMcxGrpInd() == MCX_GROUP_TYPE && removeGroupMemPropResp.getIsDispacherPresent()) {
                    removedLocWatcherList.add(profileMdn);
                } else if (locWatcherExisted && removeGroupMemPropResp.getMcxGrpInd() == MCX_GROUP_TYPE && removeGroupMemPropResp.getUserProfileCount() == 1 &&
                        removeGroupMemPropResp.getLocwatcherCount() >= 1 && !removeGroupMemPropResp.getIsDispacherPresent() && !locwatcherUPM) {
                    removedLocWatcherList.addAll(removeGroupMemPropResp.getProfileMDNs());
                    removedLocWatcherList.add(profileMdn);
                } else if (removeGroupMemPropResp.getMcxGrpInd() == MCX_GROUP_TYPE && removeGroupMemPropResp.getUserProfileCount() >= 1 &&
                        removeGroupMemPropResp.getLocwatcherCount() >= 1 && !removeGroupMemPropResp.getIsDispacherPresent() && !locWatcherExisted) {
                    removedLocWatcherList.add(profileMdn);
                } else if (locWatcherExisted && locWatcherGroupIdList.isEmpty() && removeGroupMemPropResp.getMcxGrpInd() == MCX_GROUP_TYPE) {
                    removedLocWatcherList.add(profileMdn);
                } else if (locWatcherExisted && locWatcherGroupIdList.isEmpty()) {
                    removedLocWatcherList.add(profileMdn);
                    if (removeGroupMemPropResp != null && removeGroupMemPropResp.getDisabledDispatchMemListMap() != null) {
                        removedDispatcher.addAll(removeGroupMemPropResp.getDisabledDispatchMemList());
                    }
                }
            }

            KnXDMAddlTalkGroupRequestDTO addlTalkGrpDTONew = getAddlTgRequest(addUpmGroup, modifyUpmGroup, removeGroupIds,
                    modifyUpmGroupTxn, upmGroupIds);
            if (!addlTalkGrpDTONew.getAddedAddlTgList().isEmpty() || !addlTalkGrpDTONew.getModifiedAddlTgList().isEmpty() ||
                    null != addlTalkGrpDTONew.getRemovedAddlTgList()) {

                KnCorpResponseDTO modifyTGListResp = corpMediator.modifyBulkSubscriberTGList(addlTalkGrpDTONew, modifyUpmGroupTxn);// todo update call
                knLogger.debug(methodName, "modifyTGListResp :", modifyTGListResp);
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == modifyTGListResp.getStatus()) {
                    knLogger.debug(methodName, "modifyBulkSubscriberTGList Operation Failed");
                    throw new KnXDMServerException(modifyTGListResp.getStatusCode(), modifyTGListResp.getMessage());
                }
            }

            if (null != campedGroupReq) {
                knLogger.debug(methodName, "addedCampGrpList - ", addedCampGrpList);
                knLogger.debug(methodName, "modifyTalkGroupInfo - ", modifyTalkGroupInfo);
                knLogger.debug(methodName, "removeTalkGroupInfo - ", removeTalkGroupInfo);
                KnCorpResponseDTO modifyScanListResp = corpMediator.modifyBulkSubscriberScanList(campedGroupReq, modifyUpmGroupTxn);
                knLogger.debug(methodName, "modifyScanListResp :", modifyScanListResp);
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == modifyScanListResp.getStatus()) {
                    knLogger.debug(methodName, "modifyBulkSubscriberScanList Operation Failed");
                    throw new KnXDMServerException(modifyScanListResp.getStatusCode(), modifyScanListResp.getMessage());
                }
            }

            if ((!addlTalkGrpDTONew.getAddedAddlTgList().isEmpty() || !addlTalkGrpDTONew.getModifiedAddlTgList().isEmpty() || null != addlTalkGrpDTONew.getRemovedAddlTgList())
                    || (removeGroupIds != null && !removeGroupIds.isEmpty()) || (modifyUpmGroup != null && !modifyUpmGroup.isEmpty()) || (addUpmGroup != null && !addUpmGroup.isEmpty())) {
                taskResult.setGroupEtagToBeUpdated(true);
            }
            if ((!addlTalkGrpDTONew.getAddedAddlTgList().isEmpty() || !addlTalkGrpDTONew.getModifiedAddlTgList().isEmpty() || null != addlTalkGrpDTONew.getRemovedAddlTgList())
                    || (removeGroupIds != null && !removeGroupIds.isEmpty()) || (modifyUpmGroup != null && !modifyUpmGroup.isEmpty()) || (addUpmGroup != null && !addUpmGroup.isEmpty())) {
                taskResult.setGroupEtagToBeUpdated(true);
            }
            taskResult.setAddedLocWatcherList(addedLocWatcherList);
            taskResult.setAddedLocWatcherList(addedLocWatcherList);
            taskResult.setRemovedLocWatcherList(removedLocWatcherList);
            taskResult.setEnabledDispatchMemList(addedDispatcher);
            taskResult.setDisabledDispatchMemList(removedDispatcher);
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "KnXDMServerException Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "ModifyUPMGroupTask", KnAuditHelper.STATUS.FAILURE, "ModifyUPMGroupTask request failed" + e.getErrorCode());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "ModifyUPMGroupTask", KnAuditHelper.STATUS.FAILURE, "ModifyUPMGroupTask request failed" + e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        knLogger.info(methodName, "Done modifying upm group taskResult :", taskResult);
        return taskResult;
    }

    private KnXDMAddlTalkGroupRequestDTO getAddlTgRequest(Set<KnCorpGroupListInfoDTO> addUpmGroup
            , Set<KnCorpGroupListInfoDTO> modifyUpmGroup, Set<String> removeGroupIds, KnPersisterTxn modifyUpmGroupTxn
            , List<Integer> upmGroupIds) throws KnCorpBOException {

        String methodName = "getAddlTgRequest()";

        KnXDMAddlTalkGroupRequestDTO addlTalkGrpRequestDTO = new KnXDMAddlTalkGroupRequestDTO();
        addlTalkGrpRequestDTO.setCorpId(corpId);
        addlTalkGrpRequestDTO.setMdn(profileMdn);

        Collection<KnXDMAddlTalkGroupInfoDTO> modifyTGList = new ArrayList<>();
        List<KnXDMAddlTalkGroupInfoDTO> addedTGList = new ArrayList<>();
        List<KnXDMAddlTalkGroupInfoDTO> removeTGList = null;

        if (addUpmGroup != null && !addUpmGroup.isEmpty()) {
            for (KnCorpGroupListInfoDTO groupInfo : addUpmGroup) {
                knLogger.debug(methodName, "Add block For groupInfo -", groupInfo);
                Integer groupId = groupInfo.getGroupID();
                Integer zoneId = groupInfo.getGroupZone();
                Integer channelId = groupInfo.getGroupChannel();
                if (zoneId != null && channelId != null) {
                    KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                    tgGroupInfo.setMdn(profileMdn);
                    tgGroupInfo.setGroupId(groupId);
                    tgGroupInfo.setZoneId(zoneId);
                    tgGroupInfo.setChannelId(channelId);
                    addedTGList.add(tgGroupInfo);

                }
            }
        }
        knLogger.debug(methodName, "addedTGList - ", addedTGList);

        if (modifyUpmGroup != null && !modifyUpmGroup.isEmpty()) {

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, modifyUpmGroupTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlTGList(profileMdn, xdmsHome, modifyUpmGroupTxn);
            Set<Integer> dbGroupIds = subsAddlTGList.stream().map(knCorpAddlTGInfoDTO -> knCorpAddlTGInfoDTO.getGroupId()).collect(Collectors.toSet());
            knLogger.debug(methodName, "dbGroupIds -", dbGroupIds, " upmGroupIDS - ", upmGroupIds);
            for (KnCorpGroupListInfoDTO groupInfo : modifyUpmGroup) {
                knLogger.debug(methodName, "Modify block For groupInfo -", groupInfo);
                Integer groupId = groupInfo.getGroupID();
                Integer zoneId = groupInfo.getGroupZone();
                Integer channelId = groupInfo.getGroupChannel();
                if (zoneId != null && channelId != null) {
                    if (!subsAddlTGList.isEmpty()) {

                        dbGroupIds.addAll(upmGroupIds);
                        knLogger.debug(methodName, "after adding UPM groupids to dbGroupIds -", dbGroupIds);

                        if (dbGroupIds.contains(groupId)) {
                            knLogger.debug(methodName, "subsAddlTGList already contains groupId,channelId,zoneId - ", groupId, channelId, zoneId);
                            KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                            tgGroupInfo.setMdn(profileMdn);
                            tgGroupInfo.setGroupId(groupId);
                            tgGroupInfo.setZoneId(zoneId);
                            tgGroupInfo.setChannelId(channelId);
                            modifyTGList.add(tgGroupInfo);
                            knLogger.debug(methodName, "modifyTGList::", modifyTGList);
                        } else {
                            knLogger.debug(methodName, "1-subsAddlTGList not contains groupId,channelId,zoneId - ", groupId, channelId, zoneId);
                            KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                            tgGroupInfo.setMdn(profileMdn);
                            tgGroupInfo.setGroupId(groupId);
                            tgGroupInfo.setZoneId(zoneId);
                            tgGroupInfo.setChannelId(channelId);
                            addedTGList.add(tgGroupInfo);
                            knLogger.debug(methodName, "1-addedTGList::", addedTGList);
                        }

                    } else {
                        knLogger.debug(methodName, "2-subsAddlTGList Empty so adding data to groupId,channelId,zoneId - ", groupId, channelId, zoneId);
                        KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                        tgGroupInfo.setMdn(profileMdn);
                        tgGroupInfo.setGroupId(groupId);
                        tgGroupInfo.setZoneId(zoneId);
                        tgGroupInfo.setChannelId(channelId);
                        addedTGList.add(tgGroupInfo);
                        knLogger.debug(methodName, "::2-addedTGList::", addedTGList);
                    }
                }
                if (zoneId == null && channelId == null) {
                    knLogger.debug(methodName, "Check-", dbGroupIds.contains(groupId));
                    if (dbGroupIds.contains(groupId)) {
                        knLogger.debug(methodName, "Adding to remove", groupId);
                        if (null == removeTGList)
                            removeTGList = new ArrayList<>();
                        KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                        tgGroupInfo.setGroupId(Integer.valueOf(groupId));
                        removeTGList.add(tgGroupInfo);
                        knLogger.debug(methodName, "removeTGList In FOR -", removeTGList);
                    }
                }
            }
        }

        if (removeGroupIds != null && !removeGroupIds.isEmpty()) {
            if (null == removeTGList)
                removeTGList = new ArrayList<>();
            for (String groupId : removeGroupIds) {
                knLogger.debug(methodName, "Remove block For groupId -", groupId);
                KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                tgGroupInfo.setGroupId(Integer.valueOf(groupId));
                removeTGList.add(tgGroupInfo);
            }
        }
        addlTalkGrpRequestDTO.setAddedAddlTgList(addedTGList);
        addlTalkGrpRequestDTO.setModifiedAddlTgList(modifyTGList);
        addlTalkGrpRequestDTO.setRemovedAddlTgList(removeTGList);
        addlTalkGrpRequestDTO.setUpmCall(true);
        knLogger.debug(methodName, "addedTGList - ", addedTGList);
        knLogger.debug(methodName, "modifyTGList - ", modifyTGList);
        knLogger.debug(methodName, "removeTGList - ", removeTGList);
        return addlTalkGrpRequestDTO;
    }

}
