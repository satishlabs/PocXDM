/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.KnCorpGroupContactDTO;
import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.request.KnXDMAddlTalkGroupRequestDTO;
import com.kodiak.common.commdto.request.KnXDMCorpGroupInfoRequestDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupRequestDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpEXDMSNotifyDto;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpBasicInfoRespDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpListInfoRespDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnAssignGroupTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAssignGroupTask.class);

    public static final int DISPATCHER_GROUP_TYPE = 2;
    private static final int MCX_GROUP_TYPE = 1;
    private ICorpClientIntf corpClientIntf;
    private KnXDMCorpMediator corpMediator;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpResponseDTO userProfile;
    private KnPersisterTxn assignSubsTxn;

    private String corpId;
    private String profileMdn;
    private String userProfileId;

    public KnAssignGroupTask(String profileMdn, String corpId, String userProfileId
    ,KnCorpResponseDTO userProfile,KnPersisterTxn assignSubsTxn) {
        corpClientIntf = new KnCorpClientImpl();
        corpMediator = KnXDMCorpMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        genInfoUtil = KnGenInfoUtil.getInstance();

        this.profileMdn = profileMdn;
        this.corpId = corpId;
        this.userProfileId = userProfileId;
        this.userProfile=userProfile;
        this.assignSubsTxn=assignSubsTxn;

    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName = "executeTask()";
        KnPersisterTxn assignGroupTxn = assignSubsTxn;
        KnTaskResult taskResult=new KnTaskResult();
       KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        int BROADCASTER=3;
        try {
            knLogger.info(methodName, "assignProfileMdnToGroup :userProfileId ", userProfileId, "corpId ", corpId);
            knLogger.debug(methodName," userProfile :",userProfile);

            KnCorpResponseDTO userProfileDetails = userProfile;
            String tgscMode = userProfileDetails.getTgscMode();

            List<KnCorpEXDMSNotifyDto> microserviceNotify = new ArrayList<>();
            if (userProfileDetails.getUserProfile() != null
                    && userProfileDetails.getUserProfile().getGroupList() != null
                    && !userProfileDetails.getUserProfile().getGroupList().isEmpty()) {
                Set<KnCorpGroupListInfoDTO> upmGroups = userProfileDetails.getUserProfile().getGroupList();
                knLogger.debug(methodName, "assigning group :",upmGroups," for profile mdn :", KnGDPRTemplate.mdn(profileMdn));

                //getAll mcsXcapUri's for sending MCS Group notifications
                Set<String> allMcsXcapUris = genInfoUtil.getMCSXCAPRootURIs(assignGroupTxn);

                //getting all group infos in upm
                KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                List<Integer> upmGroupIds = upmGroups.stream().map(KnCorpGroupListInfoDTO::getGroupID).collect(Collectors.toList());
                groupInfoDTO.setGroupIds(upmGroupIds);
                KnCorpGrpListInfoRespDto groupInfoResp = corpClientIntf.getListOfGroupInfo(groupInfoDTO, assignGroupTxn);
                knLogger.debug(methodName, "groupInfoResp :", groupInfoResp);
                List<KnCorpGrpBasicInfoRespDto> groupInfoList = groupInfoResp.getGroupListInfo();
                Map<Integer, Integer> groupIdNTypeMap = groupInfoList.stream().collect(
                        Collectors.toMap(KnCorpGrpBasicInfoRespDto::getGroupId, KnCorpGrpBasicInfoRespDto::getGrpType));
                knLogger.debug(methodName, "groupIdNTypeMap ", groupIdNTypeMap);
                Collection<String> locWatcherList = new HashSet<>();
                Collection<String> dispatcherList = new HashSet<>();
                Map<Integer, Boolean> grpLocWatcherMap = upmGroups.stream().collect(Collectors.toMap(grpList -> grpList.getGroupID(), grpList -> grpList.getGrpMemProps().getIsLocSupervisor() == 1));

                for (KnCorpGroupListInfoDTO groupInfo : upmGroups) {
                    KnXDMCorpGroupInfoRequestDTO xdmRequestDto = new KnXDMCorpGroupInfoRequestDTO();
                    KnXDMGroupMdnInfoDTO groupMdnInfoDTO = new KnXDMGroupMdnInfoDTO();
                    Integer groupId = groupInfo.getGroupID();
                    Integer priority = groupInfo.getGroupPriority();
                    Integer zoneId = groupInfo.getGroupZone();
                    Integer channelId = groupInfo.getGroupChannel();

                    List<KnXDMGroupMdnInfoDTO> addedGroupMdnList = new ArrayList<>();
                    groupMdnInfoDTO.setMdn(profileMdn);
                    groupMdnInfoDTO.setSupervisor(groupInfo.getGrpMemProps().getIsSupervisor());
                    Integer locWatcher = groupInfo.getGrpMemProps().getIsLocSupervisor();
                    groupMdnInfoDTO.setLocWatcher(locWatcher);
                    groupMdnInfoDTO.setIsOSMAuthorize(groupInfo.getGrpMemProps().getIsOSMAuthorized());
                    groupMdnInfoDTO.setInCallPermission(groupInfo.getGrpMemProps().getIncallAllowed());
                    groupMdnInfoDTO.setCallReceivePermission(groupInfo.getGrpMemProps().getCallTerminateAllowed());
                    groupMdnInfoDTO.setCallInitiatePermission(groupInfo.getGrpMemProps().getCallInitiateAllowed());
                    groupMdnInfoDTO.setBroadcaster(groupInfo.getGrpMemProps().getIsBroadcaster());
                    addedGroupMdnList.add(groupMdnInfoDTO);
                    xdmRequestDto.setGroupMembers(addedGroupMdnList);
                    xdmRequestDto.setGroupId(groupId.toString());
                    xdmRequestDto.setCorpId(corpId);
                    xdmRequestDto.setClientType(CLIENT_TYPE_CAT_UI);
                    xdmRequestDto.setETag("-9999");
                    xdmRequestDto.setUpmCall(true);
                    xdmRequestDto.setUserProfileId(userProfileId);
                    xdmRequestDto.setGrpLocWatcherMap(grpLocWatcherMap);
                    knLogger.debug(methodName,"assign upm group request:",xdmRequestDto);
                    KnCorpResponseDTO assignGroupResp = corpMediator.modifyCorpGroup(xdmRequestDto, assignGroupTxn);
                    knLogger.debug(methodName,"assignGroupResp :", assignGroupResp);

                    if (assignGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && assignGroupResp.getIsDispacherPresent()) {
                        dispatcherList.add(profileMdn);
                    } else if ((assignGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && locWatcher != null && locWatcher.equals(ENABLED) &&
                            assignGroupResp.getLocwatcherCount() == 1) ||
                            (assignGroupResp.getMcxGrpInd() == MCX_GROUP_TYPE && assignGroupResp.getLocwatcherCount() >= 1)) {
                        dispatcherList.addAll(assignGroupResp.getProfileMDNs());
                    } else if (locWatcher != null && locWatcher.equals(ENABLED)) {
                        if(assignGroupResp.getEnabledDispatchMemList() != null) {
                            dispatcherList.addAll(assignGroupResp.getEnabledDispatchMemList());
                        }
                        locWatcherList.add(profileMdn);
                    } else if (groupIdNTypeMap.get(groupId).equals(DISPATCHER_GROUP_TYPE)) {
                        dispatcherList.add(profileMdn);
                    }

                    if (KnConstants.RESPONSE_STATUS.FAILURE.value() == assignGroupResp.getStatus()) {
                        knLogger.debug(methodName, "assign upm group Operation Failed");
                        throw new KnXDMServerException(assignGroupResp.getStatusCode(), assignGroupResp.getMessage());
                    }
                    if (assignGroupResp.getMcxGrpInd() == 1) {
                        KnCorpGroupContactDTO memberProps =new KnCorpGroupContactDTO(groupInfo.getGrpMemProps().getIsSupervisor(),
                                groupInfo.getGrpMemProps().getIsBroadcaster(), groupInfo.getGrpMemProps().getIsLocSupervisor(), groupInfo.getGrpMemProps().getIsOSMAuthorized(),
                                groupInfo.getGrpMemProps().getCallInitiateAllowed(), groupInfo.getGrpMemProps().getCallTerminateAllowed(), groupInfo.getGrpMemProps().getIncallAllowed()
                                , groupInfo.getGrpMemProps().getVideoCallInitiateAllowed(), groupInfo.getGrpMemProps().getVideoCallReceiveAllowed(), groupInfo.getGrpMemProps().getVideoInCallAllowed()
                        );
                        List<KnCorpEXDMSNotifyDto> grpNotifyDtoList = commonMediator.getModifyMcxGrpMicroSrvNotifyDto(assignGroupResp, Integer.parseInt(corpId),
                                groupInfo.getGroupID(), userProfileId, memberProps, assignGroupResp.getOldLmrInteropFlag());
                        microserviceNotify.addAll(grpNotifyDtoList);
                    } else {
                        //  KnMqServiceConfig rmqInfoDto = commonMediator.retrieveServerConfDetails(assignGroupTxn);
                        commonMediator.startNotifyMicroServicesJob(assignGroupResp.getChangeLogMap(), assignGroupResp.getMdnCorpId(), assignGroupResp.getGroupCreatedBy(),
                                assignGroupResp.getLmrIntropCapable(), assignGroupResp.getMcxGrpInd(), assignGroupResp.getOldLmrInteropFlag());
                    }
                    Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(assignGroupResp);
                    boolean groupMempropChnageNotify = notifier.sendXcapDiffNotifications(xcapDiffList, assignGroupTxn);
                    knLogger.info(methodName, "groupMempropChnageNotify Notification status - ", groupMempropChnageNotify);
                    //send MCSGroup notification
                    boolean status = commonMediator.sendMCSGRPNotification(assignGroupResp,allMcsXcapUris,Integer.parseInt(corpId));
                    knLogger.debug(methodName, "sending Group notification Status: ", status);
                    KnLIEventHandler.logLI(assignGroupResp.getLiEventList());

                    //DG.SUBSCRPTTRADIOTGLIST
                    if(zoneId!=null&&channelId!=null){
                        KnXDMAddlTalkGroupRequestDTO addlTalkGrpDTO = new KnXDMAddlTalkGroupRequestDTO();
                        KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                        List<KnXDMAddlTalkGroupInfoDTO> addedTGList = new ArrayList<>();
                        Collection<KnXDMAddlTalkGroupInfoDTO> modifyTGList = new ArrayList<>();
                        tgGroupInfo.setMdn(profileMdn);
                        tgGroupInfo.setGroupId(groupId);
                        tgGroupInfo.setZoneId(zoneId);
                        //addlTGInfoDTO.setZoneName(addTG.get);
                        tgGroupInfo.setChannelId(channelId);
                        addedTGList.add(tgGroupInfo);
                        addlTalkGrpDTO.setCorpId(corpId);
                        addlTalkGrpDTO.setAddedAddlTgList(addedTGList);
                        addlTalkGrpDTO.setModifiedAddlTgList(modifyTGList);
                        addlTalkGrpDTO.setMdn(profileMdn);
                        addlTalkGrpDTO.setUpmCall(true);
                        knLogger.debug(methodName,"assign zone/channel request: ",addlTalkGrpDTO);
                        KnCorpResponseDTO assignZoneChannelResp = corpMediator.modifySubscriberTGList(addlTalkGrpDTO, assignGroupTxn);
                        knLogger.debug(methodName, "assignZoneChannelResp :", assignZoneChannelResp);
                        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == assignZoneChannelResp.getStatus()) {
                            knLogger.debug(methodName, "assign zone/channel Operation Failed :");
                            throw new KnXDMServerException(assignZoneChannelResp.getStatusCode(), assignZoneChannelResp.getMessage());
                        }
                        //notifation
                        Collection<KnXcapDiffDirChgNotifyDTO> tgGrpxcapDiffList = commonMediator.prepareNotification(assignZoneChannelResp);
                        notifier.setMaxNotfnsPerJob(2);
                        boolean tgListRespNotified = notifier.sendXcapDiffNotifications(tgGrpxcapDiffList, assignGroupTxn);
                        knLogger.debug(methodName, " tgGrpxcapDiffList talkGroupResp notified- ", tgListRespNotified);
                    }
                    //DG.CAMPEDGROUPINFO
                    if(zoneId!=null&&channelId!=null&&priority!=null
                            &&!groupIdNTypeMap.isEmpty()
                            &&!groupIdNTypeMap.get(groupId).equals(BROADCASTER)){
                        KnXDMTalkGroupRequestDTO campedGroupReq = new KnXDMTalkGroupRequestDTO();
                        List<KnXDMTalkGroupInfoDTO> addedCampGrpList = new ArrayList<>();
                        List<KnXDMTalkGroupInfoDTO> modifyTalkGroupInfo = new ArrayList<>();
                        List<KnXDMTalkGroupInfoDTO> removeTalkGroupInfo = new ArrayList<>();
                        KnXDMTalkGroupInfoDTO CampGrpInfo = new KnXDMTalkGroupInfoDTO();
                        CampGrpInfo.setPriority(priority);
                        CampGrpInfo.setGroupId(groupId);
                        //CampGrpInfo.setChannel(groupInfo.getGroupChannel());
                        //CampGrpInfo.setGroupName(addTG.);
                        addedCampGrpList.add(CampGrpInfo);
                        campedGroupReq.setCorpId(corpId);
                        campedGroupReq.setMdn(profileMdn);
                        if (tgscMode != null) {
                            campedGroupReq.setMode(Integer.parseInt(tgscMode));
                        }
                        campedGroupReq.setETag("-9999");
                        campedGroupReq.setAddedCampGrpList(addedCampGrpList);
                        campedGroupReq.setModifiedCampGrpList(modifyTalkGroupInfo);
                        campedGroupReq.setRemovedCampGrpList(removeTalkGroupInfo);
                        campedGroupReq.setUpmCall(true);
                        knLogger.debug(methodName,"assign priority request:",campedGroupReq);
                        KnCorpResponseDTO assignPriorityResp = corpMediator.modifySubscriberScanList(campedGroupReq, assignGroupTxn);
                        knLogger.debug(methodName, "assignPriorityResp :", assignPriorityResp);
                        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == assignPriorityResp.getStatus()) {
                            knLogger.debug(methodName, "assign priority Operation Failed :");
                            throw new KnXDMServerException(assignPriorityResp.getStatusCode(), assignPriorityResp.getMessage());
                        }
                        //notifation
                        Collection<KnXcapDiffDirChgNotifyDTO> scanListRespxcapDiffList = commonMediator.prepareNotification(assignPriorityResp);
                        notifier.setMaxNotfnsPerJob(2);
                        boolean scanListRespisNotified = notifier.sendXcapDiffNotifications(scanListRespxcapDiffList, assignGroupTxn);
                        knLogger.debug(methodName, " scanListRespxcapDiffList scanListRespisNotified- ", scanListRespisNotified);

                    }
                }

                if(!locWatcherList.isEmpty()){
                    taskResult.setAddedLocWatcherList(locWatcherList);
                }
                if (!dispatcherList.isEmpty()) {
                    taskResult.setEnabledDispatchMemList(dispatcherList);
                }

                if (!microserviceNotify.isEmpty()){
                  //  KnMqServiceConfig rmqInfoDto = commonMediator.retrieveServerConfDetails(assignGroupTxn);
                    commonMediator.startNotifyMicroServicesJob(microserviceNotify);
                }
            }
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignUPM", KnAuditHelper.STATUS.FAILURE, "Assign request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "KnXDMServerException Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignUPM", KnAuditHelper.STATUS.FAILURE, "Assign request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignUPM", KnAuditHelper.STATUS.FAILURE, "Assign request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        knLogger.info(methodName, "Done assigning group for profile mdn :", KnGDPRTemplate.mdn(profileMdn), " taskResult :", taskResult);
        return taskResult;
    }

}
