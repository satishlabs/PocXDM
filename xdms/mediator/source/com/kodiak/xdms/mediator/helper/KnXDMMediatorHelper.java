/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMCorpGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMGroupListInfoDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.request.KnXDMBulkCorpGroupInfoRequestDTO;
import com.kodiak.common.commdto.request.KnXDMCorpGroupInfoRequestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.logger.KnLogger;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CLIENT_TYPE_CAT_UI;
import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.xdms.mediator.resources.jobs.upm.KnBulkAssignGroupTask.DISPATCHER_GROUP_TYPE;

public class KnXDMMediatorHelper {
    KnLogger knLogger = KnLogger.getLogger(KnXDMMediatorHelper.class);
    private static KnXDMMediatorHelper instance = null;
    private KnXDMMediatorHelper() {
    }

    public static KnXDMMediatorHelper getInstance() {
        if (instance == null) {
            instance = new KnXDMMediatorHelper();
        }
        return instance;
    }

    public KnXDMBulkCorpGroupInfoRequestDTO objectForModifyBulkGroup(KnXDMCorpUserProfileRespDTO userProfile) {
        String methodName = "objectForModifyBulkGroup(userProfile)";
        String corpId = userProfile.getCorpId();
        String userProfileId = userProfile.getUserProfileInfo().getProfileId();
        String profileMdn = userProfile.getProfileMdn();
        Set<KnXDMGroupListInfoDTO> groupListInfo = userProfile.getUserProfileInfo().getGroupListInfo();

        KnXDMBulkCorpGroupInfoRequestDTO xdmRequestDto = new KnXDMBulkCorpGroupInfoRequestDTO();
        xdmRequestDto.setCorpId(corpId);
        xdmRequestDto.setClientType(CLIENT_TYPE_CAT_UI);
        xdmRequestDto.setETag("-9999");
        xdmRequestDto.setUserProfileId(userProfileId);
        var groupZoneIdMap = new HashMap<Integer, Integer>();
        var groupPriorityIdMap = new HashMap<Integer, Integer>();
        var groupChannelIdMap = new HashMap<Integer, Integer>();
        var locWatcherList = new HashSet<String>();
        var dispatcherList = new HashSet<String>();
        var grpLocWatcherMap = new HashMap<Integer, Boolean>();
        Map<String, String> groupIdNTypeMap = userProfile.getUserProfileInfo().getGroupListInfo().stream().collect(
                Collectors.toMap(KnXDMGroupListInfoDTO::getGroupId, KnXDMGroupListInfoDTO::getGroupType));
        knLogger.debug(methodName, "groupListInfo:", groupListInfo);

        for (KnXDMGroupListInfoDTO groupInfo : groupListInfo) {
            Integer groupId = groupInfo.getGroupId() == null ? null : Integer.parseInt(groupInfo.getGroupId());

            Integer priority = groupInfo.getPriority()   == null ? null : Integer.parseInt(groupInfo.getPriority());
            Integer zoneId = groupInfo.getZoneId() == null ? null : Integer.parseInt(groupInfo.getZoneId());
            Integer channelId = groupInfo.getChannelId() == null ? null : Integer.parseInt(groupInfo.getChannelId());
            Integer locWatcher = groupInfo.getGroupMemProp().getSupervisor();

            if (zoneId != null) groupZoneIdMap.put(groupId, zoneId);
            if (priority != null) groupPriorityIdMap.put(groupId, priority);
            if (channelId != null) groupChannelIdMap.put(groupId, channelId);

            if (locWatcher != null && locWatcher.equals(ENABLED)) {
                locWatcherList.add(profileMdn);
                grpLocWatcherMap.put(groupId, Boolean.TRUE);
            } else if (groupIdNTypeMap.get(groupId) != null && groupIdNTypeMap.get(groupId).equals(DISPATCHER_GROUP_TYPE)) {
                grpLocWatcherMap.put(groupId, Boolean.FALSE);
                dispatcherList.add(profileMdn);
            } else {
                grpLocWatcherMap.put(groupId, Boolean.FALSE);
            }

            KnXDMCorpGroupInfoRequestDTO xdmCorpGroupInfoDTO = new KnXDMCorpGroupInfoRequestDTO();

            KnXDMGroupMdnInfoDTO groupMdnInfoDTO = new KnXDMGroupMdnInfoDTO();
            groupMdnInfoDTO.setMdn(profileMdn);
            groupMdnInfoDTO.setLocWatcher(groupInfo.getGroupMemProp().getLocWatcher());
            groupMdnInfoDTO.setSupervisor(groupInfo.getGroupMemProp().getSupervisor());
            groupMdnInfoDTO.setIsOSMAuthorize(groupInfo.getGroupMemProp().getIsOSMAuthorize());
            groupMdnInfoDTO.setInCallPermission(groupInfo.getGroupMemProp().getInCallPermission());
            groupMdnInfoDTO.setCallReceivePermission(groupInfo.getGroupMemProp().getCallReceivePermission());
            groupMdnInfoDTO.setCallInitiatePermission(groupInfo.getGroupMemProp().getCallInitiatePermission());
            groupMdnInfoDTO.setBroadcaster(groupInfo.getGroupMemProp().getBroadcaster());
            //groupMdnInfoDTO = groupInfo.getGroupMemProp();
            knLogger.debug(methodName, "groupMdnInfoDTO: ", groupMdnInfoDTO);
            List<KnXDMGroupMdnInfoDTO> addedGroupMdnList = new ArrayList<>();

            addedGroupMdnList.add(groupMdnInfoDTO);
            xdmCorpGroupInfoDTO.setGroupMembers(addedGroupMdnList);

            xdmCorpGroupInfoDTO.setCorpId(corpId);
            //setClientType(CLIENT_TYPE_CAT_UI);
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
            xdmGroupPropertyInfoDTO.setPriority(priority);
            xdmGroupPropertyInfoDTO.setZoneId(zoneId);
            xdmGroupPropertyInfoDTO.setChannelId(channelId);

            if (xdmRequestDto.getAddlTalkGroupInfoMap() == null) {
                var xDMGroupPropertyInfoDTOList = new HashMap<Integer, KnXDMAddlTalkGroupInfoDTO>();
                xDMGroupPropertyInfoDTOList.put(groupId, xdmGroupPropertyInfoDTO);
                xdmRequestDto.setAddlTalkGroupInfoMap(xDMGroupPropertyInfoDTOList);
            } else {
                xdmRequestDto.getAddlTalkGroupInfoMap().put(groupId, xdmGroupPropertyInfoDTO);
            }

        }

        xdmRequestDto.setGrpLocWatcherMap(grpLocWatcherMap);
        knLogger.debug(methodName, "requestDTO for group in assignUserProfile: ", xdmRequestDto);
        return xdmRequestDto;
    }
}
