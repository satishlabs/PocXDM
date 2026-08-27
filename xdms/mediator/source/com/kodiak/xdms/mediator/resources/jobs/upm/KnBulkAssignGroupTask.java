package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
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
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpBasicInfoRespDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpListInfoRespDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.TRANSACTION_COUNT;


public class KnBulkAssignGroupTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkAssignGroupTask.class);

    public static final int DISPATCHER_GROUP_TYPE = 2;
    private static final int MCX_GROUP_TYPE = 1;
    private ICorpClientIntf corpClientIntf;
    private KnXDMCorpMediator corpMediator;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpResponseDTO userProfile;
    private KnPersisterTxn assignSubsTxn;
    private KnUPMJobScheduler upmJobScheduler;
    private String corpId;
    private String profileMdn;
    private String userProfileId;
    private KnGeneralCacheUtil generalCacheUtil;

    public KnBulkAssignGroupTask(String profileMdn, String corpId, String userProfileId
            , KnCorpResponseDTO userProfile, KnPersisterTxn assignSubsTxn) {
        corpClientIntf = new KnCorpClientImpl();
        corpMediator = KnXDMCorpMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        genInfoUtil = KnGenInfoUtil.getInstance();

        this.profileMdn = profileMdn;
        this.corpId = corpId;
        this.userProfileId = userProfileId;
        this.userProfile = userProfile;
        this.assignSubsTxn = assignSubsTxn;
        this.upmJobScheduler = KnUPMJobScheduler.getInstance();
        this.generalCacheUtil = KnGeneralCacheUtil.getInstance();

    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName = "assignBulkGroup_executeTask()";
        KnPersisterTxn assignGroupTxn = assignSubsTxn;
        KnTaskResult taskResult = new KnTaskResult();
        KnAuditHelper audit = KnAuditHelper.getAuditLogger("4002");
        int BROADCASTER = 3;
        try {
            knLogger.entry(methodName, "assignProfileMdnToGroup :userProfileId ", userProfileId, "corpId ", corpId);
            knLogger.debug(methodName, " userProfile :", userProfile);

            //Check the Valid request
            if (userProfile.getUserProfile() == null || userProfile.getUserProfile().getGroupList() == null
                    || userProfile.getUserProfile().getGroupList().isEmpty()) {
                knLogger.exit(methodName, "Exit - User Profile or Group List is unavailable- ", userProfile);
                return taskResult;
            }

            Set<KnCorpGroupListInfoDTO> upmGroups = userProfile.getUserProfile().getGroupList();
            knLogger.debug(methodName, "assigning group :", upmGroups, " for profile mdn :", KnGDPRTemplate.mdn(profileMdn));

            //Getting all group info in upm
            KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
            List<Integer> upmGroupIds = upmGroups.stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList());
            groupInfoDTO.setGroupIds(upmGroupIds);

            KnCorpGrpListInfoRespDto groupInfoResp = corpClientIntf.getListOfGroupInfo(groupInfoDTO, assignGroupTxn);
            knLogger.debug(methodName, "groupInfoResp :", groupInfoResp);

            Map<Integer, Integer> groupIdNTypeMap = groupInfoResp.getGroupListInfo().stream().collect(
                    Collectors.toMap(KnCorpGrpBasicInfoRespDto::getGroupId, KnCorpGrpBasicInfoRespDto::getGrpType));

            var locWatcherList = new HashSet<String>();
            var dispatcherList = new HashSet<String>();
            var grpLocWatcherMap = new HashMap<Integer, Boolean>();

            KnXDMBulkCorpGroupInfoRequestDTO xdmRequestDto = new KnXDMBulkCorpGroupInfoRequestDTO();
            xdmRequestDto.setCorpId(corpId);
            xdmRequestDto.setClientType(CLIENT_TYPE_CAT_UI);
            xdmRequestDto.setETag("-9999");
            xdmRequestDto.setUserProfileId(userProfileId);
            var groupZoneIdMap = new HashMap<Integer, Integer>();
            var groupPriorityIdMap = new HashMap<Integer, Integer>();
            var groupChannelIdMap = new HashMap<Integer, Integer>();

            for (KnCorpGroupListInfoDTO groupInfo : upmGroups) {
                Integer groupId = groupInfo.getGroupID();

                Integer priority = groupInfo.getGroupPriority();
                Integer zoneId = groupInfo.getGroupZone();
                Integer channelId = groupInfo.getGroupChannel();
                Integer locWatcher = groupInfo.getGrpMemProps().getIsLocSupervisor();

                if (zoneId != null) groupZoneIdMap.put(groupId, zoneId);
                if (priority != null) groupPriorityIdMap.put(groupId, priority);
                if (channelId != null) groupChannelIdMap.put(groupId, channelId);

                if (locWatcher != null && locWatcher.equals(ENABLED)) {
                    locWatcherList.add(profileMdn);
                    grpLocWatcherMap.put(groupId, Boolean.TRUE);
                } else if (groupIdNTypeMap.get(groupId).equals(DISPATCHER_GROUP_TYPE)) {
                    grpLocWatcherMap.put(groupId, Boolean.FALSE);
                    dispatcherList.add(profileMdn);
                } else {
                    grpLocWatcherMap.put(groupId, Boolean.FALSE);
                }

                KnXDMCorpGroupInfoRequestDTO xdmCorpGroupInfoDTO = new KnXDMCorpGroupInfoRequestDTO();

                //Add the Group members
                KnXDMGroupMdnInfoDTO groupMdnInfoDTO = new KnXDMGroupMdnInfoDTO();
                List<KnXDMGroupMdnInfoDTO> addedGroupMdnList = new ArrayList<>();
                groupMdnInfoDTO.setMdn(profileMdn);
                groupMdnInfoDTO.setSupervisor(groupInfo.getGrpMemProps().getIsSupervisor());
                groupMdnInfoDTO.setLocWatcher(locWatcher);
                groupMdnInfoDTO.setIsOSMAuthorize(groupInfo.getGrpMemProps().getIsOSMAuthorized());
                groupMdnInfoDTO.setInCallPermission(groupInfo.getGrpMemProps().getIncallAllowed());
                groupMdnInfoDTO.setCallReceivePermission(groupInfo.getGrpMemProps().getCallTerminateAllowed());
                groupMdnInfoDTO.setCallInitiatePermission(groupInfo.getGrpMemProps().getCallInitiateAllowed());
                groupMdnInfoDTO.setBroadcaster(groupInfo.getGrpMemProps().getIsBroadcaster());
                addedGroupMdnList.add(groupMdnInfoDTO);
                xdmCorpGroupInfoDTO.setGroupMembers(addedGroupMdnList);

                xdmCorpGroupInfoDTO.setCorpId(corpId);
                xdmRequestDto.setClientType(CLIENT_TYPE_CAT_UI);
                xdmCorpGroupInfoDTO.setETag("-9999");
                xdmCorpGroupInfoDTO.setUserProfileId(userProfileId);

                if (xdmRequestDto.getGroupInfoMap() == null) {
                    var xDMGroupPropertyInfoDTOList = new HashMap<Integer, KnXDMCorpGroupInfoRequestDTO>();
                    xDMGroupPropertyInfoDTOList.put(groupId, xdmCorpGroupInfoDTO);
                    xdmRequestDto.setGroupInfoMap(xDMGroupPropertyInfoDTOList);
                } else {
                    xdmRequestDto.getGroupInfoMap().put(groupId, xdmCorpGroupInfoDTO);
                }

                //Group properties
                KnXDMAddlTalkGroupInfoDTO xdmGroupPropertyInfoDTO = new KnXDMAddlTalkGroupInfoDTO();
                xdmGroupPropertyInfoDTO.setGroupId(groupId);
                xdmGroupPropertyInfoDTO.setPriority(groupInfo.getGroupPriority());
                xdmGroupPropertyInfoDTO.setZoneId(groupInfo.getGroupZone());
                xdmGroupPropertyInfoDTO.setChannelId(groupInfo.getGroupChannel());

                if (xdmRequestDto.getAddlTalkGroupInfoMap() == null) {
                    var xDMGroupPropertyInfoDTOList = new HashMap<Integer, KnXDMAddlTalkGroupInfoDTO>();
                    xDMGroupPropertyInfoDTOList.put(groupId, xdmGroupPropertyInfoDTO);
                    xdmRequestDto.setAddlTalkGroupInfoMap(xDMGroupPropertyInfoDTOList);
                } else {
                    xdmRequestDto.getAddlTalkGroupInfoMap().put(groupId, xdmGroupPropertyInfoDTO);
                }

            }

            xdmRequestDto.setGrpLocWatcherMap(grpLocWatcherMap);
            KnCorpResponseDTO assignGroupResp = corpMediator.modifyBulkCorpGroup(xdmRequestDto, assignGroupTxn);
            knLogger.debug(methodName, "assignGroupResp :", assignGroupResp);
            knLogger.debug(methodName, "groupIdNTypeMap ", groupIdNTypeMap);
            Map<Integer, Boolean> groupIdNLargeGrpMap = groupInfoResp.getGroupListInfo().stream().filter(e->e.getGrpType()==DISPATCH_APP_ID).collect(
                    Collectors.toMap(KnCorpGrpBasicInfoRespDto::getGroupId, KnCorpGrpBasicInfoRespDto::isLargeGroup));

            if (assignGroupResp.getEnabledDispatchMemListMap() != null) {
                Collection<String> enabledDispatcherMembers = assignGroupResp.getEnabledDispatchMemListMap().values().stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                assignGroupResp.setEnabledDispatchMemList(enabledDispatcherMembers);
            }
            for (KnCorpGroupListInfoDTO groupInfo : upmGroups) {
                Integer groupId = groupInfo.getGroupID();
                Integer locWatcher = groupInfo.getGrpMemProps().getIsLocSupervisor();
                if (assignGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && assignGroupResp.getIsDispacherPresent()) {
                    dispatcherList.add(profileMdn);
                } else if ((assignGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && locWatcher != null && locWatcher.equals(ENABLED) &&
                        assignGroupResp.getLocwatcherCount() == 1) ||
                        (assignGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && assignGroupResp.getLocwatcherCount() >= 1)) {
                    dispatcherList.addAll(assignGroupResp.getProfileMDNs());
                } else if (locWatcher != null && locWatcher.equals(ENABLED)) {
                    if (assignGroupResp.getEnabledDispatchMemList() != null) {
                        dispatcherList.addAll(assignGroupResp.getEnabledDispatchMemList());
                    }
                    locWatcherList.add(profileMdn);
                } else if (groupIdNTypeMap.get(groupId).equals(DISPATCHER_GROUP_TYPE)) {
                    dispatcherList.add(profileMdn);
                }
            }

            //DG.SUBSCRPTTRADIOTGLIST
            if (!groupZoneIdMap.isEmpty() && !groupChannelIdMap.isEmpty()) {
                KnXDMAddlTalkGroupRequestDTO addlTalkGrpDTO = new KnXDMAddlTalkGroupRequestDTO();

                List<KnXDMAddlTalkGroupInfoDTO> addedTGList = new ArrayList<>();
                Collection<KnXDMAddlTalkGroupInfoDTO> modifyTGList = new ArrayList<>();
                for (var entry : groupZoneIdMap.entrySet()) {
                    int groupId = entry.getKey();
                    if (!groupChannelIdMap.containsKey(groupId)) continue;
                    KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                    tgGroupInfo.setMdn(profileMdn);
                    tgGroupInfo.setGroupId(groupId);
                    tgGroupInfo.setZoneId(entry.getValue());
                    //addlTGInfoDTO.setZoneName(addTG.get);
                    tgGroupInfo.setChannelId(groupChannelIdMap.get(groupId));
                    addedTGList.add(tgGroupInfo);
                }
                addlTalkGrpDTO.setCorpId(corpId);
                addlTalkGrpDTO.setAddedAddlTgList(addedTGList);
                addlTalkGrpDTO.setModifiedAddlTgList(modifyTGList);
                addlTalkGrpDTO.setMdn(profileMdn);
                addlTalkGrpDTO.setUpmCall(true);
                knLogger.debug(methodName, "assign zone/channel request: ", addlTalkGrpDTO);
                KnCorpResponseDTO assignZoneChannelResp = corpMediator.modifyBulkSubscriberTGList(addlTalkGrpDTO, assignGroupTxn);
                knLogger.debug(methodName, "assignZoneChannelResp :", assignZoneChannelResp);
                if (RESPONSE_STATUS.FAILURE.value() == assignZoneChannelResp.getStatus()) {
                    knLogger.debug(methodName, "assign zone/channel Operation Failed :");
                    throw new KnXDMServerException(assignZoneChannelResp.getStatusCode(), assignZoneChannelResp.getMessage());
                }
                //notifation
              /*  Collection<KnXcapDiffDirChgNotifyDTO> tgGrpxcapDiffList = commonMediator.prepareNotification(assignZoneChannelResp);
                notifier.setMaxNotfnsPerJob(2);
                boolean tgListRespNotified = notifier.sendXcapDiffNotifications(tgGrpxcapDiffList, assignGroupTxn);
                knLogger.debug(methodName, " tgGrpxcapDiffList talkGroupResp notified- ", tgListRespNotified);*/
            }

            //DG.CAMPEDGROUPINFO
            if (!groupZoneIdMap.isEmpty() && !groupChannelIdMap.isEmpty() && !groupPriorityIdMap.isEmpty() && !groupIdNTypeMap.isEmpty()) {
                KnXDMTalkGroupRequestDTO campedGroupReq = new KnXDMTalkGroupRequestDTO();
                List<KnXDMTalkGroupInfoDTO> addedCampGrpList = new ArrayList<>();
                List<KnXDMTalkGroupInfoDTO> modifyTalkGroupInfo = new ArrayList<>();
                List<KnXDMTalkGroupInfoDTO> removeTalkGroupInfo = new ArrayList<>();
                for (var entry : groupZoneIdMap.entrySet()) {
                    int groupId = entry.getKey();
                    if (!groupChannelIdMap.containsKey(groupId) && !groupPriorityIdMap.isEmpty()) continue;
                     KnXDMTalkGroupInfoDTO CampGrpInfo = new KnXDMTalkGroupInfoDTO();
                    CampGrpInfo.setPriority(groupPriorityIdMap.get(groupId));
                    CampGrpInfo.setGroupId(groupId);
                    addedCampGrpList.add(CampGrpInfo);
                }
                campedGroupReq.setCorpId(corpId);
                campedGroupReq.setMdn(profileMdn);
                String tgscMode = userProfile.getTgscMode();
                if (tgscMode != null) {
                    campedGroupReq.setMode(Integer.parseInt(tgscMode));
                }
                campedGroupReq.setETag("-9999");
                campedGroupReq.setAddedCampGrpList(addedCampGrpList);
                campedGroupReq.setModifiedCampGrpList(modifyTalkGroupInfo);
                campedGroupReq.setRemovedCampGrpList(removeTalkGroupInfo);
                campedGroupReq.setUpmCall(true);
                knLogger.debug(methodName, "assign priority request:", campedGroupReq);
                KnCorpResponseDTO assignPriorityResp = corpMediator.modifyBulkSubscriberScanList(campedGroupReq, assignGroupTxn);
                knLogger.debug(methodName, "assignPriorityResp :", assignPriorityResp);
                if (RESPONSE_STATUS.FAILURE.value() == assignPriorityResp.getStatus()) {
                    knLogger.debug(methodName, "assign priority Operation Failed :");
                    throw new KnXDMServerException(assignPriorityResp.getStatusCode(), assignPriorityResp.getMessage());
                }
                //notifation
              /*  Collection<KnXcapDiffDirChgNotifyDTO> scanListRespxcapDiffList = commonMediator.prepareNotification(assignPriorityResp);
                notifier.setMaxNotfnsPerJob(2);
                boolean scanListRespisNotified = notifier.sendXcapDiffNotifications(scanListRespxcapDiffList, assignGroupTxn);
                knLogger.debug(methodName, " scanListRespxcapDiffList scanListRespisNotified- ", scanListRespisNotified);*/

            }
            KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
            Set<Integer> uniqueGroupIds = new HashSet<>(upmGroupIds);
            int count = generalCacheUtil.getTransactionCountBasedOnStatus();
            if (Objects.equals(assignGroupResp.getStatusCode(), "00000") && count < TRANSACTION_COUNT) {
                UUID uuid = UUID.randomUUID();
                ipUserProfileDTO.setAddedMember(profileMdn);
                ipUserProfileDTO.setAddedGroupIds(uniqueGroupIds);
                String assignUpmJsonString = KnCorpCommonInfoUtil.ObjToJson(ipUserProfileDTO);
                KnAsyncJobDTO knAsyncJobWatcherDTO = commonMediator.createJobNotifyDTO(corpId, uuid.toString(), userProfileId,
                        com.kodiak.xdms.server.common.resources.KnConstants.UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_ASSIGN.Value(),
                        profileMdn, com.kodiak.xdms.server.common.resources.KnConstants.UPM_RESOURCE_TYPE.MDN.Value(),
                        assignUpmJsonString, com.kodiak.xdms.server.common.resources.KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_ASSIGN.Value(),
                        null);
                knLogger.debug(methodName, "knAsyncJobWatcherDTO-->", knAsyncJobWatcherDTO);
                upmJobScheduler.addJob(knAsyncJobWatcherDTO);
            }
         
            if (!locWatcherList.isEmpty()) {
                taskResult.setAddedLocWatcherList(locWatcherList);
            }
            if (!dispatcherList.isEmpty()) {
                taskResult.setEnabledDispatchMemList(dispatcherList);
            }

            //if any large dispatch group is there will add all member from assignGroupResp.getDisabledDispatchMemList()
            // to taskResult.setDisabledDispatchMemList so it will recalculate and remove all members from the group
            // which are part of only large disaptch group
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId);
            String locationFeatureFlagForLG = microServicesParamNameValueMap.get("LOCATION_FEATURE_FOR_LG_FLAG") != null ? microServicesParamNameValueMap.get("LOCATION_FEATURE_FOR_LG_FLAG") : "0";
            knLogger.debug("locationFeatureFlagForLG", locationFeatureFlagForLG,"groupIdNLargeGrpMap", groupIdNLargeGrpMap, "!groupIdNLargeGrpMap.isEmpty()", !groupIdNLargeGrpMap.isEmpty());
            if (Objects.equals(locationFeatureFlagForLG, "0") && !groupIdNLargeGrpMap.isEmpty()) {
                // find all member of large dispatch group and disable them and add them to taskResult.setDisabledDispatchMemList
                // set enable dispatch list to empty
                taskResult.setDisabledDispatchMemList(assignGroupResp.getDisabledDispatchMemList());
                if (taskResult.getDisabledDispatchMemList() != null) {
                    taskResult.getDisabledDispatchMemList().addAll(dispatcherList);
                }
                if (taskResult.getEnabledDispatchMemList() != null && !taskResult.getEnabledDispatchMemList().isEmpty()) {

                    taskResult.getEnabledDispatchMemList().clear();
                }
            }
            taskResult.setAssignGroupResp(assignGroupResp);
            taskResult.setGroupIds(assignGroupResp.getGroupIds());
            taskResult.setMcxGrpInd(assignGroupResp.getMcxGrpInd());
            taskResult.setUpmGroups(upmGroups);

        }/* catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignUPM", KnAuditHelper.STATUS.FAILURE, "Assign request failed" + ":" + userProfile.getUserProfileId() + ":" + e.getMessage());
            rollback(assignGroupTxn);
        } */ catch (KnXDMServerException e) {
            knLogger.error(methodName, "KnXDMServerException Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignUPM", KnAuditHelper.STATUS.FAILURE, "Assign request failed" + ":" + userProfile.getUserProfileId() + ":" + e.getMessage());
            rollback(assignGroupTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignUPM", KnAuditHelper.STATUS.FAILURE, "Assign request failed" + ":" + userProfile.getUserProfileId() + ":" + e.getMessage());
            rollback(assignGroupTxn);
        }
        knLogger.debug(methodName, "Done assigning group for profile mdn :", KnGDPRTemplate.mdn(profileMdn), " taskResult :", taskResult);
        return taskResult;
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }
}
