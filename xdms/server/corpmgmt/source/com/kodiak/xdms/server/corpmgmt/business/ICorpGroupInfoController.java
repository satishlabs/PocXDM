/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpGroupInfoController.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 10, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGrpBasicInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface ICorpGroupInfoController {

    public KnCorpGroupInfoRespDTO createGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteGroup(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    //todo
    //public KnCorpResponseDTO deleteGroupList(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupInfoRespDTO getGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupInfoRespDTO getSubsGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupInfoRespDTO getSubsCorpGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupListRespDTO getGroupList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupListRespDTO getSubscriberGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn);

    public KnCorpGroupListRespDTO getSubscriberLocWatcherGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn);

    public KnCorpDispatchGrpMemInfoRespDTO getSubscDetailsToUpdateIsDispatchMem(KnIPCorpDispatchGrpMemInfoDto groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifySubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO modifyBulkSubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

    public KnXDMTalkGroupServerRespDTO getSubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

	public KnCorpTalkGroupRespDTO deleteSubsCampedGrps(KnIPSubsDTO ipSubsDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteSubscriberScanList (KnIPTalkGroupDTO ipSubsDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupInfoRespDTO createBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGrpBasicInfoRespDto getBasicGrpInfo(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateGrpBroadcasters(String mdn, KnPersisterTxn persisterTxn);

    public KnLinkedGroupInfoRespDTO getPoCLinkedGroupList(KnIPLinkedGroupInfoDTO pocGrpListDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO removeSubscribersAllGroups(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupListRespDTO getTpSubscriberGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn);

    public KnCorpGroupInfoRespDTO getTpSubsCorpGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGrpBasicInfoRespDto getBasicGrpInfoByGrpName(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifySubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyBulkSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

    public KnXDMTalkGroupServerRespDTO getSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

    public KnXDMTalkGroupServerRespDTO getSubsTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn);
	
	public KnCorpGroupListRespDTO getSubsGroupList(KnIPCorpContactDTO corpContactDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupListRespDTO getMobileSyncLocSupervisors(KnIPSubscriberInfoDTO subscriberCorpInfo, KnPersisterTxn persisterTxn);

    public Map<Integer,Integer> getUpIndexCountMap(Collection<Integer> userprofileIndexes, String corpId, KnPersisterTxn persisterTxn);

	public KnCorpResponseDTO modifyMCXGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo,
			KnPersisterTxn persisterTxn);

    public KnCorpGrpListInfoRespDto getListOfGroupInfo(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    KnCorpGroupInfoRespDTO createBulkCorpGroup(KnIPCorpBulkGroupDTO bulkGroupIPDto, KnPersisterTxn persisterTxn);

    KnCorpGroupListRespDTO getProfileGroupList(KnIPCorpGroupProfileDTO groupProfileDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO groupNotifyOnActiveFsChange(int corpId,String mdn,String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn);

    public KnCorpGroupInfoRespDTO getMdnAuthorizationForGroupId(KnIPCorpContactDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyGroupsUGWConfig(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpGroupInfoRespDTO getCorpGrpLmrExtn(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyBulkGroupProperties(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);

    KnCorpGroupListRespDTO getGroupsDetailsWithoutMembers(KnIPCorpBulkGroupDTO bulkGroupDTO, KnPersisterTxn persisterTxn);
    KnCorpBulkGrpBasicInfoRespDto getBulkBasicGrpInfo(KnIPCorpBulkGroupDTO bulkGroupInfo, int clientType, String userProfileId, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO modifyBulkMCXGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> vLargerGrpBasicInfoMap, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO modifyBulkBCGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> broadCastGrpBasicInfoMap, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO modifyBulkGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> grpBasicInfoMap, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO groupWatcherNotifyForAddGroup(KnAsyncJobDTO groupInfoDTO) throws KnXDMServerException;

    public KnCorpResponseDTO groupWatcherNotifyForModifyGroup(KnAsyncJobDTO groupInfoDTO) throws KnXDMServerException;

    public KnCorpResponseDTO groupWatcherNotifyforRemoveGroup(KnAsyncJobDTO groupInfoDTO) throws KnXDMServerException;

    public Map<Integer, KnCorpGroupDTO> getGroupDetailsMap(List<Integer> groups, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException;

    public KnCorpResponseDTO groupRehome(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn);
}