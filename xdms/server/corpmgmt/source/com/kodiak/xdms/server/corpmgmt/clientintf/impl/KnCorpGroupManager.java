/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupManagerImpl.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 10, 2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGrpBasicInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpGroupInfoController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpGroupManager;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class KnCorpGroupManager implements ICorpGroupManager {

    private ICorpGroupInfoController groupInfoController;

    KnCorpGroupManager() {
        groupInfoController = KnCorpBORegistry.createCorpGroupInfoController();
    }


    public KnCorpGroupInfoRespDTO createGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.createGroup(groupInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO  modifyGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.MODIFY_GROUP);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
    }

    public KnCorpResponseDTO upmModifyGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.UPM_MODIFY_GROUP);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
    }

    public KnCorpResponseDTO deleteGroup(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.DELETE_GROUP);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.deleteGroup(groupInfoDTO, persisterTxn);
    }

    public KnCorpGroupInfoRespDTO getGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_GROUP_DETAILS);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getGroupDetails(groupInfoDTO, persisterTxn);
    }

    public KnCorpGroupInfoRespDTO getSubsGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_SUBS_GROUP_DETAILS);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubsGroupDetails(groupInfoDTO, persisterTxn);
    }

    public KnCorpGroupInfoRespDTO getSubsCorpGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_SUBS_GROUP_DETAILS_XDMDATA_INTF);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubsCorpGroupDetails(groupInfoDTO, persisterTxn);
    }

    public KnCorpGroupListRespDTO getGroupList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        corpInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        corpInfoDTO.setOperationType(KnOperationTypes.GET_GROUP_LIST);
        corpInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getGroupList(corpInfoDTO, persisterTxn);
    }

    public KnCorpGroupListRespDTO getSubscriberGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
        subscriberCorpInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        subscriberCorpInfo.setOperationType(KnOperationTypes.GET_SUBSGROUP_LIST);
        subscriberCorpInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubscriberGroupList(subscriberCorpInfo, persisterTxn);
    }

    public KnCorpGroupListRespDTO getSubscriberLocWatcherGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
        subscriberCorpInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        subscriberCorpInfo.setOperationType(KnOperationTypes.GET_SUBSGROUP_LIST_XDMDATA_INTF);
        subscriberCorpInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubscriberLocWatcherGroupList(subscriberCorpInfo, persisterTxn);
    }

    public KnCorpDispatchGrpMemInfoRespDTO getSubscDetailsToUpdateIsDispatchMem(KnIPCorpDispatchGrpMemInfoDto groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_SUBSGROUP_LIST);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubscDetailsToUpdateIsDispatchMem(groupInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifySubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.MODIFY_SUBSCRIBER_SCAN_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }

    public KnCorpResponseDTO upmModifySubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.UPM_MODIFY_SUBSCRIBER_SCAN_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }

    public KnCorpResponseDTO upmModifyBulkSubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        //ipTalkGroupDTO.setOperationType(KnOperationTypes.UPM_MODIFY_SUBSCRIBER_SCAN_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBulkSubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifySubscriberScanAndChannelList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.MODIFY_SUBSCRIBER_SCAN_AND_CHANNEL_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }
    public KnCorpResponseDTO modifySubscriberScanListXcap(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.MODIFY_SUBSCRIBER_SCAN_LIST_XCAP);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }
    public KnCorpResponseDTO modifySubscriberScanListXcapClients(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.MODIFY_SUBSCRIBER_SCAN_LIST_XCAP_CLIENTS);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }

    public KnXDMTalkGroupServerRespDTO getSubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.GET_SUBSCRIBER_SCAN_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }

    public KnXDMTalkGroupServerRespDTO getSubscriberScanListXcap(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.GET_SUBSCRIBER_SCAN_LIST_XCAP);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubscriberScanList(ipTalkGroupDTO, persisterTxn);
    }

	@Override
	public KnCorpTalkGroupRespDTO cleanUpSubsCampedGrps(KnIPSubsDTO ipSubsDTO, KnPersisterTxn persisterTxn) {
		ipSubsDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
		ipSubsDTO.setOperationType(KnOperationTypes.CLEANUP_SUBS_CAMPED_GRPS);
		ipSubsDTO.setProfile(KnProfileTypes.CORP_PROFILE);
		return groupInfoController.deleteSubsCampedGrps(ipSubsDTO, persisterTxn);
	}

    @Override
    public KnCorpTalkGroupRespDTO deleteSubsScanlist(KnIPSubsDTO ipSubsDTO, KnPersisterTxn persisterTxn) {

        ipSubsDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipSubsDTO.setOperationType(KnOperationTypes.DELETE_SUBS_CAMPED_GRPS);
        ipSubsDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.deleteSubsCampedGrps(ipSubsDTO, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO  deleteSubscriberScanList (KnIPTalkGroupDTO iPTalkGroupDTO, KnPersisterTxn persisterTxn){
        iPTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        iPTalkGroupDTO.setOperationType(KnOperationTypes.DELETE_SUBS_CAMPED_GRPS);
        iPTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.deleteSubscriberScanList(iPTalkGroupDTO, persisterTxn);
    }

    public KnCorpGroupInfoRespDTO createBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.CREATE_BROADCAST_GROUP);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.createBCGroup(groupInfoDTO, persisterTxn);
    }

    public KnCorpGrpBasicInfoRespDto getBasicGrpInfo(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_BASIC_GROUP_INFO);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getBasicGrpInfo(groupInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifyBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.MODIFY_BROADCAST_GROUP);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBCGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
    }

    public KnCorpResponseDTO upmModifyBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.UPM_MODIFY_BROADCAST_GROUP);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBCGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
    }

    public KnCorpResponseDTO updateGrpBroadcasters(String mdn, KnPersisterTxn persisterTxn) {
        return groupInfoController.updateGrpBroadcasters(mdn, persisterTxn);
    }

    @Override
    /**
     * This methid is used get the group mdns group list
     */
    public KnLinkedGroupInfoRespDTO getPocLinkedGroupList(KnIPLinkedGroupInfoDTO pocGrpListDTO, KnPersisterTxn persisterTxn) {
        pocGrpListDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        pocGrpListDTO.setOperationType(KnOperationTypes.GET_POC_LINKED_GROUP_LIST);
        pocGrpListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getPoCLinkedGroupList(pocGrpListDTO, persisterTxn);
    }


    public KnCorpResponseDTO removeSubscribersAllGroups(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        subscriberInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        subscriberInfoDTO.setOperationType(KnOperationTypes.REMOVE_SUBSCRIBERS_ALL_GROUPS);
        subscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.removeSubscribersAllGroups(subscriberInfoDTO, persisterTxn);
    }

    public KnCorpGroupListRespDTO getTpSubscriberGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
        subscriberCorpInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        subscriberCorpInfo.setOperationType(KnOperationTypes.GET_SUBSGROUP_LIST_DYANMIC_INTF);
        subscriberCorpInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getTpSubscriberGroupList(subscriberCorpInfo, persisterTxn);
    }

    public KnCorpGroupInfoRespDTO getTpSubsCorpGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_SUBS_GROUP_DETAILS_DYNAMIC_INTF);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getTpSubsCorpGroupDetails(groupInfoDTO, persisterTxn);
    }

    public KnCorpGrpBasicInfoRespDto getBasicGrpInfoByGrpName(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_BASIC_GROUP_INFO);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getBasicGrpInfoByGrpName(groupInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifySubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.MODIFY_SUBSCRIBER_TG_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifySubscriberTGList(ipTalkGroupDTO, persisterTxn);
    }

    public KnCorpResponseDTO upmModifySubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.UPM_MODIFY_SUBSCRIBER_TG_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifySubscriberTGList(ipTalkGroupDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifyBulkSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.UPM_MODIFY_SUBSCRIBER_TG_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBulkSubscriberTGList(ipTalkGroupDTO, persisterTxn);
    }

    public KnXDMTalkGroupServerRespDTO getSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.GET_SUBSCRIBER_TG_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubscriberTGList(ipTalkGroupDTO, persisterTxn);
    }

    public KnXDMTalkGroupServerRespDTO getSubsTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.GET_SUBS_TG_LIST_XCAP);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubsTGList(ipTalkGroupDTO, persisterTxn);
    }

    public KnCorpResponseDTO deleteTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.DELETE_TG_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.deleteTGList(ipTalkGroupDTO, persisterTxn);
    }
	
	public KnCorpGroupListRespDTO getSubsGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
        subscriberCorpInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        subscriberCorpInfo.setOperationType(KnOperationTypes.GET_SUBSGROUP_LIST);
        subscriberCorpInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getSubsGroupList(subscriberCorpInfo, persisterTxn);
    }

    public KnCorpGroupListRespDTO getMobileSyncLocSupervisors(KnIPSubscriberInfoDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
        subscriberCorpInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        subscriberCorpInfo.setOperationType(KnOperationTypes.GET_MOBILE_SYNC_LOC_SUPERVISORS);
        subscriberCorpInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getMobileSyncLocSupervisors(subscriberCorpInfo, persisterTxn);
    }

    @Override
    public Map<Integer,Integer> getUpIndexCountMap(Collection<Integer> userprofileIndexes, String corpId, KnPersisterTxn persisterTxn){
        return groupInfoController.getUpIndexCountMap(userprofileIndexes,corpId, persisterTxn);
    }

	@Override
	public KnCorpResponseDTO modifyMCXGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo,
			KnPersisterTxn persisterTxn) {
		 groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
	        groupInfoDTO.setOperationType(KnOperationTypes.MODIFY_MCX_GROUP);
	        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
		return groupInfoController.modifyMCXGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
	}

    @Override
    public KnCorpGrpListInfoRespDto getListOfGroupInfo(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_BASIC_GROUP_INFO);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getListOfGroupInfo(groupInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpGroupInfoRespDTO createBulkCorpGroup(KnIPCorpBulkGroupDTO bulkGroupIPDto, KnPersisterTxn persisterTxn) {
        bulkGroupIPDto.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
        bulkGroupIPDto.setOperationType(KnOperationTypes.CREATE_BULK_CORP_GROUP);
        bulkGroupIPDto.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.createBulkCorpGroup(bulkGroupIPDto, persisterTxn);
    }

    @Override
    public KnCorpGroupListRespDTO getProfileGroupList(KnIPCorpGroupProfileDTO groupProfileDTO, KnPersisterTxn persisterTxn) {
        groupProfileDTO.setEntityId(KnEntityTypes.CORP_GROUP_PROFILE_MANAGER);
        groupProfileDTO.setOperationType(KnOperationTypes.GET_PROFILE_CORP_GROUP_LIST);
        groupProfileDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getProfileGroupList(groupProfileDTO, persisterTxn);
    }

    public KnCorpResponseDTO groupNotifyOnActiveFsChange(int corpId,String mdn,String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn) {
        return groupInfoController.groupNotifyOnActiveFsChange(corpId,mdn,newActiveFS,oldActiveFS, persisterTxn);
    }

    public KnCorpGroupInfoRespDTO getMdnAuthorizationForGroupId(KnIPCorpContactDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GET_MDN_AUTHORIZATION_FOR_GROUPID);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getMdnAuthorizationForGroupId(groupInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO modifyGroupsUGWConfig(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.MODIFY_GROUPS_UGW_CONFIG);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyGroupsUGWConfig(groupInfoDTO, persisterTxn);
    }
    @Override
    public KnCorpGroupInfoRespDTO getCorpGrpLmrExtn(KnIPCorpGroupInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        requestDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        requestDTO.setOperationType(KnOperationTypes.GET_CORP_GRP_LMR_EXTN);
        requestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        KnCorpGroupInfoRespDTO respDto = groupInfoController.getCorpGrpLmrExtn(requestDTO, persisterTxn);
        return respDto;
    }

    public KnCorpResponseDTO modifyBulkGroupProperties(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.MODIFY_BULK_GROUP_PROPERTIES);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBulkGroupProperties(groupInfoDTO, persisterTxn);
    }

    public KnCorpGroupListRespDTO getGroupsDetailsWithoutMembers(KnIPCorpBulkGroupDTO bulkGroupDTO, KnPersisterTxn persisterTxn) {
        bulkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        bulkGroupDTO.setOperationType(KnOperationTypes.GET_GROUP_DETAILS_WITHOUT_MEMBERS);
        bulkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getGroupsDetailsWithoutMembers(bulkGroupDTO, persisterTxn);
    }

    public KnCorpBulkGrpBasicInfoRespDto getBulkBasicGrpInfo(KnIPCorpBulkGroupDTO bulkGroupInfo, int clientType, String userProfileId, KnPersisterTxn persisterTxn) {
        bulkGroupInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        bulkGroupInfo.setOperationType(KnOperationTypes.GET_BULK_BASIC_GROUP_INFO);
        bulkGroupInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.getBulkBasicGrpInfo(bulkGroupInfo, clientType, userProfileId, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO modifyBulkMCXGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> vLargerGrpBasicInfoMap,
                                                KnPersisterTxn persisterTxn) {
        bulkGroupInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        bulkGroupInfo.setOperationType(KnOperationTypes.MODIFY_MCX_GROUP);
        bulkGroupInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBulkMCXGroup(bulkGroupInfo, vLargerGrpBasicInfoMap, persisterTxn);
    }
    public KnCorpResponseDTO upmBulkModifyBCGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> broadCastGrpBasicInfoMap, KnPersisterTxn persisterTxn) {
        bulkGroupInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        bulkGroupInfo.setOperationType(KnOperationTypes.UPM_MODIFY_BROADCAST_GROUP);
        bulkGroupInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBulkBCGroup(bulkGroupInfo, broadCastGrpBasicInfoMap, persisterTxn);
    }
    public KnCorpResponseDTO upmBulkModifyGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> grpBasicInfoMap, KnPersisterTxn persisterTxn) {
        bulkGroupInfo.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        bulkGroupInfo.setOperationType(KnOperationTypes.UPM_MODIFY_GROUP);
        bulkGroupInfo.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBulkGroup(bulkGroupInfo, grpBasicInfoMap, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO groupWatcherNotifyForAddGroup(KnAsyncJobDTO groupInfoDTO) throws KnXDMServerException {
        return groupInfoController.groupWatcherNotifyForAddGroup(groupInfoDTO);
    }

    @Override
    public KnCorpResponseDTO groupWatcherNotifyForModifyGroup(KnAsyncJobDTO groupInfoDTO) throws KnXDMServerException {
        return groupInfoController.groupWatcherNotifyForModifyGroup(groupInfoDTO);
    }

    @Override
    public KnCorpResponseDTO groupWatcherNotifyforRemoveGroup(KnAsyncJobDTO groupInfoDTO) throws KnXDMServerException {
        return groupInfoController.groupWatcherNotifyforRemoveGroup(groupInfoDTO);
    }

    public KnCorpResponseDTO upmModifyBulkSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        ipTalkGroupDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        ipTalkGroupDTO.setOperationType(KnOperationTypes.UPM_MODIFY_SUBSCRIBER_TG_LIST);
        ipTalkGroupDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.modifyBulkSubscriberTGList(ipTalkGroupDTO, persisterTxn);
    }

    public Map<Integer, KnCorpGroupDTO> getGroupDetailsMap(List<Integer> groups, String xdmsHome, KnPersisterTxn persisterTxn) {
        try {
            return groupInfoController.getGroupDetailsMap(groups, xdmsHome, persisterTxn);
        } catch (KnCorpBOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KnCorpResponseDTO groupRehome(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
        groupInfoDTO.setOperationType(KnOperationTypes.GROUP_REHOME);
        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return groupInfoController.groupRehome(groupInfoDTO, persisterTxn);
    }
}
