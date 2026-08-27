/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupInfoUtil.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 1, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.request.KnXDMGroupPropertyInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dao.KnCorpGrpLmrExtnDAO;
import com.kodiak.common.ggcache.dto.KnCorpGrpLmrExtnDTO;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMSubscriberInfoDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnAddlTGInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.resources.KnCacheKeys;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;

import static com.kodiak.common.resources.KnConstants.OWNER_FAN_TYPE;
import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.common.resources.KnConstants.NUM_OF_LG_SUPPORTED;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.INCONTEXT_ID_NOT_IN_CORP;

public class KnCorpGroupInfoUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupInfoUtil.class);

    /**
    private KnCorpCommonInfoUtil commonInfoUtil;

    public KnCorpGroupInfoUtil(){
        commonInfoUtil= new KnCorpCommonInfoUtil();
    }
     */

    public KnCorpGroupInfoPersistDTO getGroupInfo(KnIPCorpGroupInfoDTO groupInfoDTO, String xdmsHome,
                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {

        // retrieve corpGroupCount
        // retrieve subList distinct members count
        //retrieve groupInfo if exists for groupName/groupDisplayName for the same corpId
        // retrieve each members' group count
        String methodName = "getGroupInfo(KnIPCorpGroupInfoDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName);
        KnCorpGroupInfoPersistDTO groupPersistDTO = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            int corpId = groupInfoDTO.getCorpId();
            List<Integer> groupIdList = new ArrayList<>();
            if (KnConstants.STANDARD_GROUP == groupInfoDTO.getGroupType()
                    || KnConstants.BROADCAST_GROUP == groupInfoDTO.getGroupType()) {
                groupIdList.add(KnConstants.STANDARD_GROUP);
                groupIdList.add(KnConstants.BROADCAST_GROUP);
            } else {
                groupIdList.add(KnConstants.DISPATCH_GROUP);
            }
            int groupCount = xdmDAO.getCorpGroupCount(corpId, groupIdList, persisterTxn);
            groupPersistDTO = xdmDAO.selectGroupNameInfo(corpId,
                    groupInfoDTO.getGroupDisplayName(), persisterTxn);
            if (groupPersistDTO == null) {
                groupPersistDTO = new KnCorpGroupInfoPersistDTO();
            } else {
                groupPersistDTO.setExistingGroupCount(1);
            }
            groupPersistDTO.setCorpGroupCount(groupCount);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group info - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return groupPersistDTO;
    }

    public Collection<KnCorpGroupInfoPersistDTO> getGroupList(KnIPCorpInfoDTO corpInfoDTO, int maxMemPerGroup, String
            xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getGroupList(KnIPCorpInfoDTO, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName);
        try {
            int corpId = corpInfoDTO.getCorpId();
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.selectGroupList(corpId, maxMemPerGroup, xdmsHome, readOnly, persisterTxn, corpInfoDTO.getHierarchyId());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getGroupListPaginated(KnIPCorpInfoDTO corpInfoDTO, int maxMemPerGroup, int nextToken, int fetchSize, String
            xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGroupListPaginated(KnIPCorpInfoDTO, int, int, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        try {
            int corpId = corpInfoDTO.getCorpId();
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.selectGroupListPaginated(corpId, maxMemPerGroup, nextToken, fetchSize, xdmsHome, readOnly, persisterTxn,corpInfoDTO.getHierarchyId());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<Integer> getGroupListCount(int corpId,String hierarchyId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getGroupListCount(int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.selectGroupListCount(corpId,hierarchyId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupList(KnIPCorpContactDTO corpContactDTO, int maxMemPerGroup,
                                                                  String xdmsHome,
                                                                  KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsGroupList(KnIPCorpContactDTO, int, String, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = xdmDAO.selectSubsGroupList(corpContactDTO.getMdn(), corpContactDTO.getCorpId(),
                    maxMemPerGroup, false, persisterTxn);
            if (!corpContactDTO.getBulkReq() && (subsGroupList == null || subsGroupList.isEmpty())) {
                knLogger.error(methodName, "No Groups Found For Subscribers - ", KnGDPRTemplate.mdn(corpContactDTO.getMdn()));
                throw new KnCorpBOException(KnErrorCodes.BOEntity.NO_GROUP_EXISTS_FOR_SUBSCRIBER,
                        "Subscribers Does not belong to any group.");
            }
            return subsGroupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupList(KnIPCorpContactDTO corpContactDTO, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsGroupList(KnIPCorpContactDTO, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = xdmDAO.selectSubsLocGroupList(corpContactDTO.getMdn(), corpContactDTO.getCorpId(), readOnly, persisterTxn);
            if ((subsGroupList == null || subsGroupList.isEmpty())) {
                knLogger.error(methodName, "No Groups Founf For Subscribers - ", KnGDPRTemplate.mdn(corpContactDTO.getMdn()));
                throw new KnCorpBOException(KnErrorCodes.BOEntity.NO_GROUP_EXISTS_FOR_SUBSCRIBER,
                        "Subscribers Does not belong to any group.");
            }
            return subsGroupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpGroupInfoPersistDTO getGroupDetails(KnIPCorpGroupDTO groupInfoDto, String xdmsHome,
                                                     KnPersisterTxn persisterTxn,Boolean hiearchyCall)
            throws KnCorpBOException , KnXDMServerException{

        String methodName = "getGroupDetails(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        // retrieve group details
        knLogger.debug(methodName);
        try {
            int corpId = groupInfoDto.getCorpId();
            int groupId = groupInfoDto.getGroupId();
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.selectGroupInfo(groupId, corpId, groupInfoDto.getClientType(), persisterTxn,hiearchyCall);
        } catch (KnDAOException e) {
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpGroupInfoPersistDTO getGroupDetailsByGroupId(KnIPCorpGroupDTO groupInfoDto, String xdmsHome, boolean readOnly,
                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupDetailsByGroupId(KnIPCorpGroupDTO, String, boolean, KnPersisterTxn)";
        // retrieve group details
        knLogger.debug(methodName);
        try {
            int groupId = groupInfoDto.getGroupId();
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.selectGroupInfoByGroupId(groupId, groupInfoDto.getClientType(), readOnly, persisterTxn);
        } catch (KnDAOException e) {
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void createGroupInfoDetails(KnCorpGroupInfoPersistDTO groupInfoPersistDTO,
                                       String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "createGroupInfoDetails(KnCorpGroupInfoPersistDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createGroupInfo(groupInfoPersistDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while creating group basic info - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void addSublistsToGroup(int groupId, Collection<Integer> sublistIds,
                                   String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        // add to DG.CorpGroup_ListRef
        String methodName = "addSublistsToGroup(int, Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId, " , sublistIds - ", sublistIds);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertGroupListRefInfo(groupId, sublistIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting the sublist for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public void deleteCorpListDistGroupReference(int sublistId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "deleteCorpListDistGroupReference(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.deleteCorpListDistGroupReference(sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting the sublist for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getGroupCountByName(String groupName, int corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupCountByName(String, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupName - ", groupName, " , corpId - ", corpId);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getGroupCountByName(groupName, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieveing group count by group name - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteGroup(int groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteGroup(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        KnCorpGrpLmrExtnDAO corpGrpLmrExtnDAO = new KnCorpGrpLmrExtnDAO();
        try {
            corpXdmDao.deleteGroupSublistRef(groupId, persisterTxn);
            int corpListId = corpXdmDao.getGroupPrivateListId(groupId, persisterTxn);
            corpXdmDao.deleteAllSublistMembers(corpListId, persisterTxn);
            corpXdmDao.deleteGroupDistribution(groupId, persisterTxn);
            //the table has beendepricated insertIntoCorpGroupDistInfo()  7.2 release
            // corpXdmDao.deleteXDMCorpGroupEntry(groupId, persisterTxn);
            Collection<Integer> groupIdList = new ArrayList<Integer>();
            groupIdList.add(groupId);
            corpXdmDao.deleteCorpGroupMemberCountEntry(groupId, persisterTxn);
            corpXdmDao.deleteGroupHierarchy(groupId, persisterTxn);
            corpXdmDao.deleteSharedGroups(groupIdList, persisterTxn);
            corpXdmDao.deleteGroup(groupId, persisterTxn);
            corpXdmDao.deleteGroupPrivateList(corpListId, persisterTxn);
            corpXdmDao.deleteAllCorpGroupMemberList(groupIdList, persisterTxn);
            corpGrpLmrExtnDAO.deleteGrpLmrExtn(groupId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        catch (SQLException sqlEx){
            knLogger.debug("SQLException Occured: ",sqlEx.getMessage());
        }
    }

    public void modifyGroupDetails(KnIPCorpGroupInfoDTO groupInfoDTO,
                                   String pttServerId,
                                   KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "modifyGroupDetails(KnIPCorpGroupInfoDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY---------------------> :");
        knLogger.debug(methodName, "ENTRY : groupInfoDTO",groupInfoDTO);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            if (groupInfoDTO.getVideoPermission() != null) {
                modifyGroupVideoPermission(groupInfoDTO.getVideoPermission(), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
            if (groupInfoDTO.getGroupDisplayName() != null) {
                modifyGroupName(groupInfoDTO.getGroupDisplayName(), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
            if (groupInfoDTO.isGroupTypeChanged()) {
                updateGroupType(groupInfoDTO.getGroupType(), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
            if (groupInfoDTO.getOverrideDnd() != KnConstants.OVERRIDE_DND_NOT_MODIFIED) {
                modifyGroupOverrdeDND(groupInfoDTO.getOverrideDnd(), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
            if (groupInfoDTO.getAvatar()!=null && groupInfoDTO.getAvatar() != KnConstants.AVATAR_NOT_MODIFIED) {
                modifyGroupAvatar(groupInfoDTO.getAvatar(), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
            if(groupInfoDTO.getOSMListId()!=null){
                corpXdmDao.modifyGroupOSMListId(groupInfoDTO.getCorpId(),
                        groupInfoDTO.getGroupId(),groupInfoDTO.getOSMListId(), persisterTxn);
            }
            if (groupInfoDTO.isLmrInteropFeatureChanged()) {
                Collection<Integer> groupIds = new ArrayList<>();
                groupIds.add(groupInfoDTO.getGroupId());
                modifyGroupLmrInteropCapable(groupInfoDTO.getLmrInteropCapable(), groupIds, pttServerId, persisterTxn);
            }

            if (null != groupInfoDTO.getUgwInterop() && (groupInfoDTO.getUgwInterop().equals(7) || groupInfoDTO.getUgwInterop().equals(0))) {
                modifyGroupUGWParameter(groupInfoDTO.getUgwInterop(), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
            if (null != groupInfoDTO.getRecordingFs()) {
                modifyGroupRecordingFsParameter(Integer.parseInt(groupInfoDTO.getRecordingFs()), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
            modifyGroupEmergAttributes(groupInfoDTO, pttServerId, persisterTxn);
            if(groupInfoDTO.getGrpShared() != null){
                Map<Integer, Integer> groupIdSharedFlagMap = new HashMap<>(1);
                groupIdSharedFlagMap.put(groupInfoDTO.getGroupId(), groupInfoDTO.getGrpShared());
                corpXdmDao.modifyGroup_GrpSharedFlag(groupIdSharedFlagMap, pttServerId, persisterTxn);
            }
            // Added contacts to the private List
            Collection<KnCorpGroupMemberDTO> groupMembers = groupInfoDTO.getGroupMembers();
            Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
            for (KnCorpGroupMemberDTO grpMember : groupMembers) {
                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                subsc.setMdn(grpMember.getMdn());
                subsc.setCorpId(grpMember.getCorpId());
                subscribersList.add(subsc);
            }
            int groupPrivateList = groupInfoDTO.getGroupMemberListId();
            if (!subscribersList.isEmpty()) {
                corpXdmDao.addPrivateContactList(subscribersList, groupPrivateList, persisterTxn);
            }
            // Remove contacts from private list
            Collection<String> removedMdnsList = groupInfoDTO.getRemovedMemberMdns();
            if (removedMdnsList != null && !removedMdnsList.isEmpty()) {
                corpXdmDao.deleteSubscPrivateContactList(removedMdnsList, groupPrivateList, persisterTxn);
            }
            // Add sublists to the group
            Collection<Integer> addedSublistIds = groupInfoDTO.getAddedSublistIds();
            if (addedSublistIds != null && !addedSublistIds.isEmpty()) {
                addSublistsToGroup(groupInfoDTO.getGroupId(), groupInfoDTO.getAddedSublistIds(), pttServerId, persisterTxn);
            }
            // Delete sublist from the Group
            Collection<Integer> removeSublistIds = groupInfoDTO.getRemovedSublistIds();
            if (removeSublistIds != null && !removeSublistIds.isEmpty()) {
                removeSublistListsMappingFromGroup(removeSublistIds, groupInfoDTO.getGroupId(), pttServerId
                        , persisterTxn);
            }
            if (null != groupInfoDTO.getAuthorizedLargeTG()) {
                modifyAuthorizedLargeTGParameter(groupInfoDTO.getAuthorizedLargeTG(), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying group details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    private void removeSublistListsMappingFromGroup(Collection<Integer> removedSublistIds,
                                                    int groupId,
                                                    String pttServerId,
                                                    KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "removeSublistListsMappingFromGroup(Collection<Integer>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.removeSublistListsMappingFromGroup(removedSublistIds, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while removing sublist mapping for the group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void addToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO> actualMdnToBeAddedToGroup,
                                       int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "addToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.addToCorpGroupDistInfo(actualMdnToBeAddedToGroup, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while adding the corporate group dist info - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<Integer> getCorpGroupIdByOsmListId(int OsmListId,String xdmsHomePttId,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName="getCorpGroupIdByOsmListId()";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getCorpGroupIdByOsmListId(OsmListId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }
    public Map<Integer, Integer> updateGroupListEtag(Collection<Integer> groupIdLst, String xdmsHomePttId,
                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateGroupListEtag(Collection<Integer>, String, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.updateGroupListEtag(groupIdLst, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int updateIsOSMAuthorize(Set<Integer> groupIds, String mdn,String isOSMAuthorize,String xdmsHomePttId,
                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateIsOSMAuthorize()";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
           return corpXdmDao.updateIsOSMAuthorize(groupIds,mdn,isOSMAuthorize, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> updateGroupListEtag(Map<Integer, Integer> groupIdLst, String xdmsHomePttId,
                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateGroupListEtag(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.updateGroupListEtag(groupIdLst, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<Integer> getGroupHavingMember(KnIPCorpContactDTO contactDTO, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getGroupHavingMember(KnIPCorpContactDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getGroupHavingMember(contactDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getGroupsAllMemberList(int groupId, int maxGroupMemberLimit,
                                                                  String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getGroupsAllMemberList(int, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            Collection<KnCorpSubscriberDTO> groupMembersList = corpXdmDao.getGroupsAllMemberList(groupId,
                    maxGroupMemberLimit, persisterTxn);
            return groupMembersList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving all the group members list - ",
                    e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpGroupDTO getGroupDetails(int groupId, int clientIntf, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getGroupDetails(int, String, boolean, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupBasicInfo(groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Integer getMemberCountFromMemberList(int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getMemberCountFromMemberList(int, String, boolean, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getMemberCountFromMemberList(groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpGroupDTO getGroupBasicInfo(int groupId, int clientIntf, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getGroupBasicInfo(groupId, clientIntf, xdmsHome, false, persisterTxn);
    }

    public KnCorpGroupDTO getGroupBasicInfo(int groupId, int clientIntf, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getGroupBasicInfo(int, String, boolean, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            KnCorpGroupDTO corpGroupDTO = corpXdmDao.getGroupBasicInfo(groupId, readOnly, persisterTxn);
            knLogger.debug(methodName, "corpGroupDTOcorpGroupDTO ", corpGroupDTO.getGroupCreatedBy(), "clientIntf--", clientIntf);
            if ((clientIntf == CLIENT_TYPE_SOAP || clientIntf == CLIENT_TYPE_CAT_UI)
                    && corpGroupDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()) {
                knLogger.error(methodName, "Group Doesn't exist. Rethrowing Exception - CAT/SOAP not authorized to view ABDG Group");
                throw new KnCorpBOException(KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist");
            } else {
                return corpGroupDTO;
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, HashMap<String, Collection<String>>> getAllSubscribersGroupList(Collection<String> mdnList, int corpId, String
            xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getAllSubscribersGroupList(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getAllSubscribersGroupList(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group list information- ",
                    e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getMembersGroupCount(Collection<String> finalGroupMembersinDB, String
            xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMembersGroupCount(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.selectSubscriberGroupCounts(finalGroupMembersinDB, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final group members group count - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupListForXcap(KnIPCorpContactDTO corpContactDTO, int
            maxMemPerGroup, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSubsGroupListForXcap(corpContactDTO, maxMemPerGroup, xdmsHome, false, persisterTxn);
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupListForXcap(KnIPCorpContactDTO corpContactDTO, int
            maxMemPerGroup, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubsGroupListForXcap(KnIPCorpContactDTO, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.selectSubsGroupList(corpContactDTO.getMdn(), corpContactDTO.getCorpId(), maxMemPerGroup, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final group list for XCAP - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupListForXcapOnMdn(KnIPCorpContactDTO corpContactDTO, int
            maxMemPerGroup, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubsGroupListForXcapOnMdn(KnIPCorpContactDTO, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsGroupListForXcapOnMdn(corpContactDTO.getMdn() , maxMemPerGroup, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final group list for XCAP - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getNewlyAddedMembersForGroup(Collection<String> requestMembers,
                                                           int groupId, String xdmsHome,
                                                           KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getNewlyAddedMembersForGroup(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : requestMembers - ", requestMembers == null ? requestMembers : KnGDPRTemplate.mdnList(requestMembers), " groupId - ", groupId);
        Collection<String> memberList = getGroupMemberList(groupId, xdmsHome, persisterTxn);
        Collection<String> memberToAdd = new ArrayList<String>();
        for (String mdn : requestMembers) {
            if (!memberList.contains(mdn)) {
                memberToAdd.add(mdn);
            }
        }
        return memberToAdd;
    }

    /**
     * Method is used to retrieve the members of the group
     * Changed the return type of the method to maintain the order of the data retrieved
     *
     * @param groupId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public LinkedList<String> getGroupMemberList(int groupId, String xdmsHome,
                                                 KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupMemberList(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : groupId - ", groupId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupMemberList(groupId, xdmsHome, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving members of group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyGroupVideoPermission(Integer videoPermission, int groupId, String pttServerId,
                                           KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifyGroupVideoPermission(Integer videoPermission, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            // add the members
            corpXdmDao.modifyGroupVideoPermission(videoPermission, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group name.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyGroupName(String groupName, int groupId, String pttServerId,
                                KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifyGroupName(String, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            // add the members
            corpXdmDao.modifyGroupName(groupName, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group name.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifyGroupAvatar(Integer avatar, int groupId, String pttServerId,
                                KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifyGroupAvatar(int, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            // add the members
            corpXdmDao.modifyGroupAvatar(avatar, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group avatar.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getAllGroupList(KnIPCorpInfoDTO corpInfoDTO, int maxMemPerGroup, String xdmsHome,
                                                                 KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getAllGroupList(KnIPCorpInfoDTO, int, String, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName, "ENTRY :");
        try {
            int corpId = corpInfoDTO.getCorpId();
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Collection<KnCorpGroupInfoPersistDTO> groupList = xdmDAO.selectGroupList(corpId, maxMemPerGroup,
                    xdmsHome, false, persisterTxn,corpInfoDTO.getHierarchyId());
            return groupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting all the group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean subscrBelongsToSharedPreConfGroup(int ownedCorpId, List<Integer> sharedCorpId, String reqMdn, IXDMServerDAO xdmDAO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "subscrBelongsToSharedPreConfGroup(int, List<Integer>, String, IXDMServerDAO, KnPersisterTxn)";
        boolean returnValue =  false;
        List<String> memberMdns = xdmDAO.getSharedGroupMemberBySharedAndOwnCorpids(ownedCorpId,sharedCorpId,persisterTxn);
        for(String mdn : memberMdns)
        {
            knLogger.debug(methodName, " mdn-" , mdn , " reqMdn ", reqMdn);
            if(mdn.trim().equalsIgnoreCase(reqMdn.trim()))
                returnValue = true;
        }

        knLogger.debug(methodName, " Subscriber is member of shared GroupId. ownedCorpId -", ownedCorpId, " sharedCorpId" ,sharedCorpId,
                " reqMdn ", reqMdn , " returnValue ", returnValue);
        return  returnValue;
    }



    public void deleteAllGroups(Collection<Integer> groupIdsList, String xdmsHome, KnCorpInOutParamDTO corpInOutParamDTO, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "deleteAllGroups(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdsList :", groupIdsList);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        KnCorpGrpLmrExtnDAO corpGrpLmrExtnDAO = new KnCorpGrpLmrExtnDAO();
        try {
            ArrayList<Integer> privateGroupSublistList = corpXdmDao.getAllGroupsPrivateList(groupIdsList, persisterTxn);
            knLogger.debug(methodName, "privateGroupSublistList :", privateGroupSublistList);
            corpXdmDao.deleteAllGroupsSublistRef(groupIdsList, persisterTxn);
            corpXdmDao.deleteAllGroupsDistribution(groupIdsList, persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String corpGroupHierarchy = microServicesParamNameValueMap.get(CORP_HIERARCHY);
            if(CORP_HIERARCHY_ENABLED.equals(corpGroupHierarchy)){
                corpXdmDao.deleteAllGrpHierarchy(groupIdsList, persisterTxn);
            }
            corpXdmDao.deleteAllGroupsCorpGroupMemberCountEntry(groupIdsList, persisterTxn);
            corpXdmDao.deleteAllCorpGroupMemberList(groupIdsList, persisterTxn);
            List<Integer> grpList;
            if (groupIdsList instanceof List) {
                grpList = (List<Integer>) groupIdsList;
            } else {
                grpList = new ArrayList<>(groupIdsList);
            }
            if (null == corpInOutParamDTO) {
                corpInOutParamDTO = new KnCorpInOutParamDTO();
            }
            cleanUpCampedGrp(grpList, corpInOutParamDTO, xdmsHome, persisterTxn);
            cleanUpTGSSGrp(grpList, corpInOutParamDTO, xdmsHome, persisterTxn);
            deleteAllTGListGrpIds(grpList, xdmsHome, persisterTxn);
            corpXdmDao.deleteAllGroups(groupIdsList, persisterTxn);
            corpXdmDao.deleteAllCorpSublistMembers(privateGroupSublistList, persisterTxn);
            corpXdmDao.deleteAllSublistInfo(privateGroupSublistList, persisterTxn);
            //TODO: instead of calling one by one, need to change it to call using the IN clause.
            for (int groupId : groupIdsList) {
                corpGrpLmrExtnDAO.deleteGrpLmrExtn(groupId);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleting all group from list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (SQLException sqlEx) {
            knLogger.error(methodName, "SQLException occurred: ", sqlEx.getMessage());
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occurred: ", e.getMessage());
        }
    }

    public void deleteMDNsFromGroups(Collection<Integer> groupIdsList, String xdmsHome, KnCorpInOutParamDTO corpInOutParamDTO, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "deleteMDNsFromGroups(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdsList :", groupIdsList);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        KnCorpGrpLmrExtnDAO corpGrpLmrExtnDAO = new KnCorpGrpLmrExtnDAO();
        try {
            ArrayList<Integer> privateGroupSublistList = corpXdmDao.getAllGroupsPrivateList(groupIdsList, persisterTxn);
            knLogger.debug(methodName, "privateGroupSublistList :", privateGroupSublistList);
            corpXdmDao.deleteAllGroupsDistribution(groupIdsList, persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String corpGroupHierarchy = microServicesParamNameValueMap.get(CORP_HIERARCHY);
            if(CORP_HIERARCHY_ENABLED.equals(corpGroupHierarchy)){
                corpXdmDao.deleteAllGrpHierarchy(groupIdsList, persisterTxn);
            }
            corpXdmDao.deleteAllCorpGroupMemberList(groupIdsList, persisterTxn);
            List<Integer> grpList;
            if (groupIdsList instanceof List) {
                grpList = (List<Integer>) groupIdsList;
            } else {
                grpList = new ArrayList<>(groupIdsList);
            }
            if (null == corpInOutParamDTO) {
                corpInOutParamDTO = new KnCorpInOutParamDTO();
            }
            cleanUpCampedGrp(grpList, corpInOutParamDTO, xdmsHome, persisterTxn);
            cleanUpTGSSGrp(grpList, corpInOutParamDTO, xdmsHome, persisterTxn);
            deleteAllTGListGrpIds(grpList, xdmsHome, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleting all group from list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occurred: ", e.getMessage());
        }
    }

    /**
     * This method returns a Map of group member MDN and its is_supervisor value
     *
     * @param groupIdsList
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void deleteAllGrpHierarchy(Collection<Integer> groupIdsList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllGrpHierarchy(Collection<Integer>, String, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteAllGrpHierarchy(groupIdsList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleting all group from Hierarchy list - ", e);
            throw e;
        }
    }

    public void emptyGroup(Collection<Integer> groupIdsList, String xdmsHome, KnCorpInOutParamDTO corpInOutParamDTO, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "emptyGroup()";
        knLogger.debug(methodName, "ENTRY : groupIdsList :",groupIdsList);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            //SELECT GROUPMEMBERLISTID FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN(1417);
            ArrayList<Integer> privateGroupSublistList = corpXdmDao.getAllGroupsPrivateList(groupIdsList, persisterTxn);
            knLogger.debug(methodName,"privateGroupSublistList :",privateGroupSublistList);
            //DELETE FROM DG.CORPGROUP_LISTREF WHERE CORPGROUPID IN (GROUPIDS)
            //corpXdmDao.deleteAllGroupsSublistRef(groupIdsList, persisterTxn);
            //DELETE FROM DG.CORPGROUPDISTINFO WHERE CORPGROUPID IN (GROUPIDS)
            corpXdmDao.deleteAllGroupsDistribution(groupIdsList, persisterTxn);
            //DELETE FROM DG.CORPGROUPMEMBERCOUNT WHERE CORPGROUPID IN (GROUPIDS)
            corpXdmDao.updateAllGroupsCorpGroupMemberCountEntry(groupIdsList, persisterTxn);
            //DELETE FROM DG.CORPGROUPMEMBERLIST WHERE CORPGROUPID IN (GROUPIDS)
            corpXdmDao.deleteAllCorpGroupMemberList(groupIdsList, persisterTxn);
            List<Integer> grpList;
            if (groupIdsList instanceof List) {
                grpList = (List<Integer>) groupIdsList;
            } else {
                grpList = new ArrayList<Integer>(groupIdsList);
            }
            //DELETE FROM DG.CAMPEDGROUPINFO WHERE GROUPID IN (GROUPIDS)
            cleanUpCampedGrp(grpList, corpInOutParamDTO, xdmsHome, persisterTxn);
            //DELETE FROM DG.SSCHANNELGROUPINFO WHERE GROUPID IN (GROUPIDS)
            cleanUpTGSSGrp(grpList, corpInOutParamDTO, xdmsHome, persisterTxn);
            //DELETE FROM DG.SUBSCRPTTRADIOTGLIST WHERE GROUPID = ?;
            deleteAllTGListGrpIds(grpList, xdmsHome, persisterTxn);
            //DELETE FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN (GROUPIDS)
            //corpXdmDao.deleteAllGroups(groupIdsList, persisterTxn);
            //DELETE FROM DG.CORPLISTMEMBER WHERE CORPLISTID IN (SUBLISTID)
            corpXdmDao.deleteAllCorpSublistMembers(privateGroupSublistList, persisterTxn);
            //DELETE FROM DG.CORPLISTINFO WHERE CORPLISTID IN (SUBLISTID)
            //corpXdmDao.deleteAllSublistInfo(privateGroupSublistList, persisterTxn);
            Collection<Integer> allSublistInGroups = corpXdmDao.getAllSublistForGroupIds(groupIdsList, persisterTxn);
            knLogger.debug(methodName,"allSublistInGroups -",allSublistInGroups);
            //removing privateGroupSublistList for empty group,as CORPGROUPINFO.GROUPMEMBERLISTID cannot be null.
            allSublistInGroups.removeAll(privateGroupSublistList);

            corpXdmDao.deleteAllSublist(allSublistInGroups, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting all group from list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>> groupMemberList, String xdmsHome,
                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.insertIntoCorpGroupMemberList(groupMemberList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting into corp groupmemberlist table - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertBulkIntoCorpGroupMemberList(Map<Integer, KnCorpContactDTO> groupMemberList, String xdmsHome,
                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.insertBulkIntoCorpGroupMemberList(groupMemberList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting into corp groupmemberlist table - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method deletes the members from the group member list table
     * CHanged the return type to maintain the order of the data retrieved from DB after the opertaion
     *
     * @param groupMemberList
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public LinkedHashMap<Integer, LinkedList<Integer>> deleteCorpGroupMemberList(LinkedHashMap<Integer, LinkedList<String>> groupMemberList, String xdmsHome,
                                                                                 KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteCorpGroupMemberList(Map<Integer, Collection<String>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.deleteCorpGroupMemberList(groupMemberList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting from corp groupmemberlist table - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<String>> selectGroupMemberListForGroupIds(Collection<Integer> groupList,
                                                                             String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        String methodName = "selectGroupMemberListForGroupIds(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        Map<Integer, Collection<String>> groupMemberList;
        try {
            groupMemberList = corpXdmDao.selectGroupMemberListForGroupIds(groupList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving from corp groupmemberlist table - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return groupMemberList;
    }

    public int getGroupMemSize(Collection<Integer> groupList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        String methodName = "getGroupMemSize(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        int count = 0;
        try {
            count = corpXdmDao.getGroupMemSize(groupList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving from corp groupmemberlist table - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return count;
    }

    public int getMCXGroupMemSize(Collection<Integer> groupList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getMCXGroupMemSize(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        int count = 0;
        try {
            count = corpXdmDao.getMCXGroupMemSize(groupList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving from corp groupmemberlist table - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return count;
    }

    public KnCorpGroupInfoPersistDTO getGroupBasicInfoDetails(KnIPCorpGroupInfoDTO groupInfoDTO,
                                                              String xdmsHome,
                                                              KnPersisterTxn persisterTxn,
                                                              Boolean hiearchyCall)
            throws KnCorpBOException {
        String methodName = "getGroupBasicInfoDetails(KnIPCorpGroupInfoDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            int corpId = groupInfoDTO.getCorpId();
            KnCorpGroupInfoPersistDTO groupPersistDTO = xdmDAO.selectGroupBasicInfo(groupInfoDTO.getGroupId(), corpId, groupInfoDTO.getClientType(), persisterTxn,hiearchyCall);
            return groupPersistDTO;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group info details- ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Group Doesn't exist. Rethrowing Exception - ", e);
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public KnCorpGroupInfoPersistDTO getGroupBasicInfoDetailsWithoutCorpId(KnIPCorpGroupInfoDTO groupInfoDTO,
                                                              String xdmsHome,
                                                              KnPersisterTxn persisterTxn,
                                                              Boolean hiearchyCall)
            throws KnCorpBOException {
        String methodName = "getGroupBasicInfoDetails(KnIPCorpGroupInfoDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            int corpId = groupInfoDTO.getCorpId();
            KnCorpGroupInfoPersistDTO groupPersistDTO = xdmDAO.getGroupBasicInfoDetailsWithoutCorpId(groupInfoDTO.getGroupId(), groupInfoDTO.getClientType(), persisterTxn,hiearchyCall);
            return groupPersistDTO;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group info details- ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Group Doesn't exist. Rethrowing Exception - ", e);
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpGroupMemberDTO> getGroupSupervisorMembers(int groupId,
                                                                       String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupSupervisorMembers(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupSupervisorMembers(groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getExternalSubscriberGroupCount(Collection<String> externalMdnList,
                                                                int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getExternalSubscriberGroupCount(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            Map<String, Integer> subscGroupCountMap = new HashMap<String, Integer>();
            if (externalMdnList != null && !externalMdnList.isEmpty()) {
                ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
                subscGroupCountMap = xdmDAO.getExternalSubscriberGroupCount(externalMdnList, corpId, persisterTxn);
            }
            return subscGroupCountMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExternalSubscriberGroupCount- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateGroupMemberListSupervisorList(Collection<KnCorpGroupMemberDTO> supervisorMemberList,
                                                    int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateGroupMemberListSupervisorList(Collection<KnCorpGroupMemberDTO>, int, String)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateGroupMemberListSupervisorList(supervisorMemberList, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateGroupMemberListSupervisorList- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, HashMap<Integer, String>> getGroupListStatus(Collection<Integer> groupIdLst,
                                                                    String xdmsHomePttId,
                                                                    KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupListStatus(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, HashMap<Integer, String>> groupStatustMap = xdmDAO.getGroupListStatus(groupIdLst, persisterTxn);
            return groupStatustMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Retrieves the group members who are dispatchers.Linked list to maintain the order of the members fetched.
     *
     * @param groupIdList
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public LinkedHashMap<Integer, LinkedList<String>> getGroupDispatcherSubscriber(Collection<Integer> groupIdList, int supervisor, String xdmsHome,
                                                                                   KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupDispatcherSubscriber(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupInfoDTO - ", groupIdList);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            LinkedHashMap<Integer, LinkedList<String>> groupStatustMap = xdmDAO.getGroupDispatcherSubscriber(groupIdList, supervisor, persisterTxn);
            return groupStatustMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupMdnSubscriber(Collection<Integer> groupIdList, int memberType, String xdmsHome,
                                                                            KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupMdnSubscriber(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupInfoDTO - ", groupIdList);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupMdnSubscriber(groupIdList, memberType, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<String>> getGroupLocWatcherSubscriber(Collection<Integer> groupIdList, String xdmsHome,
                                                                         KnPersisterTxn persisterTxn) throws KnCorpBOException{
        String methodName = "getGroupLocWatcherSubscriber(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupInfoDTO - ", groupIdList);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Map<Integer, Collection<String>> groupStatustMap = xdmDAO.getGroupLocWatcherSubscriber(groupIdList, persisterTxn);
            return groupStatustMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateGroupType(int groupType, int groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateGroupType(int, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupType - ", groupType);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            xdmDAO.updateGroupType(groupType, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getAllSubscribersGroupListForAllCorporate(Collection<String> mdnList,
                                                                                                    String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getAllSubscribersGroupListForAllCorporate(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getAllSubscribersGroupListForAllCorporate(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group list information- ",
                    e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateCorpGroupMemberList(Collection<Integer> groupList, String oldMdn, String newMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateCorpGroupMemberList(Collection<Integer>, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateCorpGroupMemberList(groupList, oldMdn, newMdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting into corp groupmemberlist table - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<String>> getAllSubscribersGroupListAsExtContact(Collection<String> mdnList, int corpId, String
            xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getAllSubscribersGroupListAsExtContact(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getAllSubscribersGroupListAsExtContact(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group list information- ",
                    e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<String>> getGroupSubscriberDistList(Collection<Integer> grpIdList, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getGroupSubscriberDistList(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupSubscriberDistList(grpIdList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group subscriber dist list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> adddedmembers, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "insertIntoCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            if (adddedmembers != null && !adddedmembers.isEmpty()) {
                xdmDAO.insertIntoCorpGroupDistInfo(adddedmembers, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while insertIntoCorpGroupDistInfo- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoCorpGrpDistInfo(Map<Integer, String> adddedmembers, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "insertIntoCorpGrpDistInfo(Map<Integer, Map<String, Collection<String>>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            if (adddedmembers != null && !adddedmembers.isEmpty()) {
                xdmDAO.insertIntoCorpGrpDistInfo(adddedmembers, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while insertIntoCorpGrpDistInfo- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFrmCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> deletedmembers, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "deleteFrmCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            if (deletedmembers != null && !deletedmembers.isEmpty()) {
                xdmDAO.deleteFrmCorpGroupDistInfo(deletedmembers, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleteFrmCorpGroupDistInfo- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getSubsNotInDispatchGroupFromMDNList(Collection<String> mdnList, int corpId, String
            xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsNotInDispatchGroupFromMDNList(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getSubsNotInDispatchGroupFromMDNList(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group list information- ",
                    e);
            throw e;
        }
    }

    public Collection<String> getSubsNotInNormalDispatchGroupFromMDNList(Collection<String> mdnList, int corpId, String
            xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsNotInNormalDispatchGroupFromMDNList(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getSubsNotInNormalDispatchGroupFromMDNList(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group list information- ",
                    e);
            throw e;
        }
    }

    public Collection<String> getLocWatcherMdn(Collection<String> mdnList, int corpId, String
            xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getLocWatcherMdn(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getLocWatcherMdn(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group list information- ",
                    e);
            throw e;
        }
    }

    public int getCorpGroupCount(int corpId, List<Integer> groupTypeList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupCount(int, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupCount(corpId, groupTypeList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count",
                    e);
            throw e;
        }
    }

    public boolean isSubscriberPartOfGroup(int groupId, String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "isSubscriberPartOfGroup(int, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.isSubscriberPartOfGroup(groupId, mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count",
                    e);
            throw e;
        }
    }

    public Map<String, KnCorpGroupMemberDTO> getGroupMemberDetailsListInfo(int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMemberDetailsListInfo(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMemberDetailsListInfo(groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getGroupMemberDetailsListInfo",
                    e);
            throw e;
        }
    }

    public Map<Integer, Collection<String>> getAllSubscribersGroupDistForAllCorporate(Collection<String> mdnList,
                                                                                      String xdmsHomePttId,
                                                                                      KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getAllSubscribersGroupDistForAllCorporate(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getAllSubscribersGroupDistForAllCorporate(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group dist information- ",
                    e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This methoc is used to fetch the specific subscribers for the group
     *
     * @param groupIds
     * @param supervisorType
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<String>> getGroupSpecificSubsc(Collection<Integer> groupIds, int supervisorType,
                                                                  String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupSpecificSubsc(Collection<Integer>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupSpecificSubsc(groupIds, supervisorType, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getGroupSpecificSubsc", e);
            throw e;
        }
    }

    /**
     * This methoc is used to fetch the the specific privilege members of the groups for thr corporate
     *
     * @param supervisorTypes
     * @param corpId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getCoporateGrpSpecificSubsc(Collection<Integer> supervisorTypes, int corpId,
                                                                                      String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCoporateGrpSpecificSubsc(Collection<Integer>, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCoporateGrpSpecificSubsc(supervisorTypes, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    /**
     * This methoc is used to fetch the the specific privilege members of the groups for thr corporate
     *
     * @param supervisorTypes
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSpecificSubsc(Collection<Integer> supervisorTypes, Collection<Integer>
            groupIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGrpSpecificSubsc(Collection<Integer>, Collection<Integer>, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGrpSpecificSubsc(supervisorTypes, groupIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    /**
     * This methoc is used to fetch the the SG privilege members of the groups for thr corporate
     *
     * @param groupMDNList
     * @param corpId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGroupListForGroupMDN(Collection<String> groupMDNList, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupListForGroupMDN(List<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupListForGroupMDN(groupMDNList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }


    /**
     * This method returns the external member list in a group.
     *
     * @param groupId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public LinkedList<String> getExternalGrpMembers(int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getExternalGrpMembers(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY - ", groupId);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getExternalGrpMembers(groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExternalGrpMembers", e);
            throw e;
        }
    }

    /**
     * Methos to get the list of group Ids where the mdnList exist
     *
     * @param mdnList
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, Collection<Integer>> getGroupListForSubs(Collection<String> mdnList, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getGroupListForSubs(Collection<String>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupListForSubs(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group subscriber dist list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Returns a map of groupId to corpId for all groups the given MDN belongs to,
     * using a single JOIN query on DG.CORPGROUPMEMBERLIST and DG.CORPGROUPINFO.
     */
    public Map<Integer, Integer> getGroupListForSubsWithCorpId(Collection<String> mdnList, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getGroupListForSubsWithCorpId(Collection<String>, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return ((KnCorpXdmDAO) xdmDAO).getGroupListForSubsWithCorpId(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getting group-corp list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the group ids and etag where the subscriber exist
     *
     * @param corpContactDTO
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<Integer, KnCorpGrpBasicInfoDTO> getSubsGrpLstForGetDir(KnIPCorpContactDTO corpContactDTO, List<Integer> groupIdFromProfile,
                                                                      String xdmsHome, KnPersisterTxn persisterTxn,List<Integer> cpgroupIds) throws KnCorpBOException {
        String methodName = "getSubsGrpLstForGetDir(KnIPCorpContactDTO)";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            final Integer BROADCAST_GROUP_TYPE = 2;
            String mdn = corpContactDTO.getMdn();
            List<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);

            List<Integer> subsGroupIdList = xdmDAO.getSubsGroupIdList(mdn, persisterTxn);
            subsGroupIdList.addAll(groupIdFromProfile);
            subsGroupIdList.addAll(cpgroupIds);
            knLogger.debug(methodName, "cpgroupids: "+cpgroupIds);
            // If nonbroadcaster member is part of group,Adding broadcast groups to the directory applicable above pv 20
            if (corpContactDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_20) {
                List<Integer> broadCastGroupIds = xdmDAO.getGroupIdsByMemberAndGroupType(mdn, BROADCAST_GROUP_TYPE, persisterTxn);
                subsGroupIdList.addAll(broadCastGroupIds);
            }
            if (corpContactDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_23) {
                Map<Integer, List<KnCorpGrpMemListDTO>> extGroupIdList = getSubsGroupIdListMap(mdnList, xdmsHome, persisterTxn);
                knLogger.debug("extGroupIdList: " +extGroupIdList.keySet());
                subsGroupIdList.addAll(extGroupIdList.keySet());
            }
            return xdmDAO.getGrpDetForGetDir(subsGroupIdList, corpContactDTO.getCorpId(), persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a Map of group member MDN and its is_supervisor value
     *
     * @param groupId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpGroupMemberDTO> getGroupMembersList(int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        return getGroupMembersList(groupId, xdmsHome, false, persisterTxn);
    }

    public Map<String, KnCorpGroupMemberDTO> getGroupMembersList(int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMembersList(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMembersList(groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public Map<String, KnCorpGroupMemberDTO> getGroupMembersListReadOnly(int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMembersList(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMembersList(groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public Collection<KnCorpGroupMemberDTO> getCorpGroupMembersList(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        return getCorpGroupMembersList(groupIds, xdmsHome, false, persisterTxn);
    }

    /**
     * This method returns a List of DTO of group member MDN and its is_supervisor and LocWatcher value.
     *
     * @param groupIds
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpGroupMemberDTO> getCorpGroupMembersList(Collection<Integer> groupIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMembersList(Collection<Integer>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY -readOnly: ", readOnly);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupMembersList(groupIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public Collection<KnCorpGroupMemberDTO> getCorpGroupMemberForLocWatcher(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMemberForLocWatcher(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupMemberForLocWatcher(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public Collection<String> getCorpGroupMemberIsLocWatcher(Collection<Integer> groupIds, int isLocwatcher, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMemberForLocWatcher(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupMemberIsLocWatcher(groupIds, isLocwatcher, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public Map<Integer, List<String>> getCorpGroupMemberForLocSupervisor(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMemberForLocWatcher(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupMemberForLocSupervisor(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    /**
     * This method calls a SP to get the truncated group members
     *
     * @param groupId
     * @param maxGroupMemberLimit
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<String> getTruncatedGroupMems(int groupId, int maxGroupMemberLimit, String
            xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getTruncatedGroupMems(int, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getTruncatedGroupMems(groupId, maxGroupMemberLimit, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is used for querying corpcontactlist table and returns a Map of mdn and contact cout.
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, Integer> getSubsContactCount(List<String> mdnList, String pttServerId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubsContactCount(List<String>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubsContactCount(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the the group members belongs to private list with member corpid.
     *
     * @param groupInfoDto
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, Integer> getGroupPrivtMemLst(KnIPCorpGroupDTO groupInfoDto, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getGroupPrivtMemLst(KnIPCorpGroupDTO, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            int groupId = groupInfoDto.getGroupId();
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupPrivtMemLst(groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the group internal members details.
     *
     * @param internalMemLst
     * @param contCountmap
     * @param corpId
     * @param maxContact
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Collection<KnCorpGroupMemPersistDTO> getGrpMemDetails(List<String> internalMemLst, Map<String, Integer> contCountmap, int
            corpId, int maxContact, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGrpMemDetails(List<String>, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getGrpMemDetails(internalMemLst, contCountmap, corpId, maxContact, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the group members details.
     *
     * @param memberList
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<String> getGrpMemDetails(List<String> memberList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getGrpMemDetails(memberList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the MDNs which are less the 13 PV.
     *
     * @param mdns
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<String> getMdnsLessThanThirteenPv(List<String> mdns, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getMdnsLessThanThirteenPv(mdns, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method query the dg.corpextcontact table and returns the List of  KnCorpGroupMemPersistDTO
     *
     * @param extMemLst
     * @param corpId
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Collection<KnCorpGroupMemPersistDTO> getExtGrpMemDetails(List<String> extMemLst, int corpId, String
            pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getExtGrpMemDetails(List<String>, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getExtGrpMemDetails(extMemLst, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getValidGrpInCorp(List<Integer> grpIdList, int corpId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getValidGrpInCorp(grpIdList, corpId, pttServerId, false, persisterTxn);
    }

    public Map<Integer, Integer> getValidGrpInCorp(List<Integer> grpIdList, int corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getValidGrpInCorp(List<Integer>, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getValidGrpInCorp(grpIdList, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getValidGrp(List<Integer> grpIdList, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getValidGrp(List<Integer>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getValidGrp(grpIdList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method filters out the groupId from a list of groupids where mdn doest not exist as member and return the subset
     * of groupIds from input list where mdn exist as member.
     *
     * @param grpIdList
     * @param mdn
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<Integer> getSubsGroupIdList(List<Integer> grpIdList, String mdn, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubsGroupIdList(List<Integer>, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubsGroupIdList(grpIdList, mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a Map which contains groupId and the list of deleted members present in the groupId.
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<Integer, List<KnCorpGrpMemListDTO>> getSubsGroupIdListMap(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubsGroupIdListMap(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a list of dispatch group from subsGrplstMap
     *
     * @param subsGrpLstMap
     * @return
     */
    public List<Integer> getDispatchGroups(Map<Integer, List<KnCorpGrpMemListDTO>> subsGrpLstMap) {
        List<Integer> dispGrpList = new ArrayList<Integer>();
        for (int grpId : subsGrpLstMap.keySet()) {
            for (KnCorpGrpMemListDTO grpMemListDTO : subsGrpLstMap.get(grpId)) {
                if (grpMemListDTO.getSuperVisor() == KnConstants.DISPATCHER) {
                    dispGrpList.add(grpId);
                    break;
                }
            }
        }
        return dispGrpList;
    }

    /**
     * This method returns a Map of group id and the deleted member list.
     *
     * @param subsGrpLstMap
     * @return
     */
    public Map<Integer, List<String>> getDeleteGrpMemMap(Map<Integer, List<KnCorpGrpMemListDTO>> subsGrpLstMap) {
        Map<Integer, List<String>> deletdGrpMemMap = new HashMap<Integer, List<String>>();
        for (int grpId : subsGrpLstMap.keySet()) {
            List<String> deletedMem = new ArrayList<String>();
            for (KnCorpGrpMemListDTO grpMemListDTO : subsGrpLstMap.get(grpId)) {
                deletedMem.add(grpMemListDTO.getGroupMem());
            }
            deletdGrpMemMap.put(grpId, deletedMem);
        }
        return deletdGrpMemMap;
    }

    /**
     * This method deleted the group members in mdnList from dg.corpgroupmemberlist table.
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteGrpMemList(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteGrpMemList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deleted the corp group distribution entries for mdnList.
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteGrpDistList(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteGrpDistList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deleted the Group_Sublist entries for sublistId.
     *
     * @param sublistId
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteCorpListDistGroupReference(List<Integer> sublistId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.deleteCorpListDistGroupReference(sublistId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    private boolean getFeatureBitValue(long clientFeatureSet, int bitNumber) {
        String methodName = "getFeatureBit(long,int )";
        knLogger.debug(methodName, "ENTRY: get Feature Bit data ");
        BitSet bitSet = convertLongToBitSet(clientFeatureSet);
        boolean bitValue = bitSet.get(bitNumber);
        knLogger.debug(methodName, "EXIT: Feature Bit Value - ", bitValue);
        return bitValue;
    }

    private BitSet convertLongToBitSet(long longValue) {
        String methodName = "convertLongToBitSet(long)";
        knLogger.debug(methodName, "ENTRY: Received long value to convert bit set is - ", longValue);

        long value = longValue;

        BitSet bitSet = new BitSet(Long.SIZE);
        int index = 0;
        while (value != 0) {
            if (value % 2L != 0) {
                bitSet.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);

        return bitSet;
    }


    public void deleteCampedGrps(String mdn, List<Integer> grpIdList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteCampedGrps(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteCampedGrps(mdn, grpIdList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void createSubsCampedGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, int campedBy, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "createSubsCampedGrps(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createSubsCampedGrps(mdn, grpList, campedBy, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void createSubsChannelGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "createSubsChannelGrps(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createSubsChannelGrps(mdn, grpList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void createSubsTalkGrpScanMode(String mdn, Integer mode, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "createSubsTalkGrpScanMode(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createSubsTalkGrpScanMode(mdn, mode, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSubsTalkGrpScanMode(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteSubsTalkGrpScanMode(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSubsTalkGrpScanMode(mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnCorpTGSPersistDTO> getSubsCampedGrp(KnIPTalkGroupDTO ipTalkGroupDTO, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getSubsCampedGrp(ipTalkGroupDTO, xdmsHome, false, persisterTxn);
    }

    public List<KnCorpTGSPersistDTO> getSubsCampedGrp(KnIPTalkGroupDTO ipTalkGroupDTO, String xdmsHome,boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getSubsCampedGrp(KnIPCorpGroupDTO, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsCampedGrp(ipTalkGroupDTO, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public List<KnCorpTGSPersistDTO> getSubsChannelGrp(KnIPTalkGroupDTO ipTalkGroupDTO, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getSubsChannelGrp(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsChannelGrp(ipTalkGroupDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, KnCorpGroupInfoPersistDTO> getGroupDisplayNameCorpIdMapInfo(List<Integer> groupIdLst, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getGroupDisplayNameCorpIdMapInfo(groupIdLst, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");

        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupDisplayNameCorpIdMapInfo(groupIdLst, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteAllCampedGroups(String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupPrivtMemLst(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteAllCampedGroups(persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubsTalkGrpScanMode(String mdn, Integer mode, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "updateSubsTalkGrpScanMode(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateSubsTalkGrpScanMode(mdn, mode, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubsTalkGrpScanEtag(String mdn, Integer etag, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "updateSubsTalkGrpScanMode(KnIPCorpGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateSubsTalkGrpScanEtag(mdn, etag, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnTalkGrpScanMode getSubsTalkGrpScanMode(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSubsTalkGrpScanMode(mdn, xdmsHome, false, persisterTxn);
    }
    public KnTalkGrpScanMode getSubsTalkGrpScanMode(String mdn, String xdmsHome,boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getSubsTalkGrpScanMode(KnIPCorpGroupDTO, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsTalkGrpScanMode(mdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /** this will cleanup camped group and channel group**/
    public void cleanUpCampedGrp(Map<Integer, List<String>> map, KnCorpInOutParamDTO corpInOutParamDTO, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "cleanUpCampedGrp(Map<Integer, List<String>> map, String xdmsHome, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            KnCorpInOutParamDTO corpDTO = xdmDAO.cleanUpCampedGrp(map, persisterTxn);
            corpInOutParamDTO.putTGSModeChg(corpDTO.getTgsModeChgMap());
            corpInOutParamDTO.setMdnTgscEtag(corpDTO.getMdnTgscEtag());
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void cleanUpCampedGrp(List<Integer> deletedGrpIds, KnCorpInOutParamDTO corpInOutParamDTO, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "cleanUpCampedGrp(deletedGrpId, xdmsHome, persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            KnCorpInOutParamDTO corpInputParamDTo = xdmDAO.cleanUpCampedGrp(deletedGrpIds, persisterTxn);
            corpInOutParamDTO.putTGSModeChg(corpInputParamDTo.getTgsModeChgMap());
            corpInOutParamDTO.setMdnTgscEtag(corpInputParamDTo.getMdnTgscEtag());
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean cleanUpSubsCampedGrps(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "cleanUpSubsCampedGrps(mdn, xdmsHome, persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.cleanUpSubsCampedGrps(mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean cleanUpSubsCampedGrps(List<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "cleanUpSubsCampedGrps(mdn, xdmsHome, persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.cleanUpSubsCampedGrps(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSubsCampedGrps(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteSubsCampedGrps(mdn, xdmsHome, persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSubsCampedGrps(mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSubsChannelGrps(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteSubsChannelGrps(mdn, xdmsHome, persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSubsChannelGrps(mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public ArrayList<KnCorpGroupDTO> getGrpsNameEtagInfo(Collection<Integer> groupIds, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGrpsNameEtagInfo(Collection<Integer>, String, KnPersisterTxn)";
        //retrieve groupn name and etag list
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getGrpsNameEtagInfo(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group name and etag- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void addToCorpGroupDistInfo(List<String> distMdnList,
                                       int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "addToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.addToCorpGroupDistInfo(distMdnList, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while adding the corporate group dist info - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method will itterate through a list and add into another if not exist and return a distinct member list.
     *
     * @param finalSublistDistMember
     * @param privtLstMemToBeAdded
     * @return
     */
    public List<KnCorpSubscriberDTO> getFinalBCGrpMemList(List<KnCorpSubscriberDTO> finalSublistDistMember,
                                                          List<KnCorpSubscriberDTO> privtLstMemToBeAdded) {
        String methodName = "getFinalBCGrpMemList(List, List)";
        knLogger.debug(methodName, "ENTRY :", finalSublistDistMember.size(), " - ", privtLstMemToBeAdded.size());
        List<KnCorpSubscriberDTO> memList = new ArrayList<>(finalSublistDistMember);
        for (KnCorpSubscriberDTO subscriberDTO : privtLstMemToBeAdded) {
            if (!memList.contains(subscriberDTO)) {
                memList.add(subscriberDTO);
            }
        }
        knLogger.debug(methodName, "Exit :", memList.size());
        return memList;
    }

    public List<Integer> getGroupSublistIds(int grpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            List<Integer> allSublistIds = new ArrayList<>();
            allSublistIds.addAll(corpXdmDao.getGroupsSublistListFromDB(grpId, persisterTxn));
            return allSublistIds;
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> deleteGroupDistInfo(List<String> mdnList, int groupId, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.deleteGroupDistInfo(mdnList, groupId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, List<String>> getActualDeletedGrpMembers(LinkedHashMap<Integer, LinkedList<String>>
                                                                         deletedGrpMemMap, LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus) {
        String methodName = "getActualDeletedGrpMembers(LinkedHashMap<Integer, LinkedList<String>>, " +
                "LinkedHashMap<Integer, LinkedList<String>>)";
        Map<Integer, List<String>> deletedMemMap = new HashMap<>(deletedGrpMemMap.size());
        for (int grpId : deletedGrpMemMap.keySet()) {
            LinkedList<String> delMemList = deletedGrpMemMap.get(grpId);
            knLogger.debug(methodName, "delMemList - ", KnGDPRTemplate.mdnList(delMemList));
            if (null != delMemList) {
                LinkedList<Integer> statusList = delGroupMemStatus.get(grpId);
                knLogger.debug(methodName, "statusList - ", statusList);
                List<String> deletedMems = new ArrayList<>();
                int index = 0;
                for (String mdn : delMemList) {
                    if (statusList.get(index++) == 1) {
                        deletedMems.add(mdn);
                    }
                }
                knLogger.debug(methodName, "groupId - ", grpId, " - deletedMems - ", KnGDPRTemplate.mdnList(deletedMems));
                deletedMemMap.put(grpId, deletedMems);
            }
        }
        return deletedMemMap;
    }

    public void deleteGroupDistInfo(Map<Integer, List<String>> groupMemMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.deleteGroupDistInfo(groupMemMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<Integer> getBroadcstGroupList(String mdn, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getBroadcstGroupList(mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateGrpBroadcasters(Map<Integer, List<String>> groupMemMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.updateGrpBroadcasters(groupMemMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getGroupMemCount(Collection<Integer> groupIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getGroupMemCount(groupIds, pttServerId, false, persisterTxn);
    }

    public Map<Integer, Integer> getGroupMemCount(Collection<Integer> groupIds, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getGroupMemCount(groupIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<Integer> getBroadcstGroups(Collection<Integer> groupids, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        List<Integer> broadcastGrpList = new ArrayList<>();
        try {
            ArrayList<KnCorpGroupDTO> groupDTOs = getGrpsNameEtagInfo(groupids, xdmsHomePttId, persisterTxn);
            for (KnCorpGroupDTO groupDto : groupDTOs) {
                if (groupDto.getGroupType() == KnConstants.BROADCAST_GROUP) {
                    broadcastGrpList.add(groupDto.getGroupId());
                }
            }
        } catch (KnCorpBOException e) {
            throw e;
        }
        return broadcastGrpList;
    }

    public Map<Integer, KnCorpGroupDTO> getGroupBasicDetailsMap(Collection<Integer> groupIdList, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return  getGroupBasicDetailsMap(groupIdList, xdmsHomePttId, false, persisterTxn);
    }

    public Map<Integer, KnCorpGroupDTO> getGroupBasicDetailsMap(Collection<Integer> groupIdList, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        Map<Integer, KnCorpGroupDTO> groupDetailsMap = new HashMap<>(groupIdList.size());
        try {
            Map<Integer, Integer> groupMemCountMap = getGroupMemCount(groupIdList, xdmsHomePttId, readOnly, persisterTxn);
            ArrayList<KnCorpGroupDTO> groupDetailList = getGroupBasicInfoList(groupIdList, xdmsHomePttId, readOnly, persisterTxn);
            for (KnCorpGroupDTO groupDTO : groupDetailList) {
                if (null != groupMemCountMap.get(groupDTO.getGroupId())) {
                    groupDTO.setGroupMemCount(groupMemCountMap.get(groupDTO.getGroupId()));
                }
                groupDetailsMap.put(groupDTO.getGroupId(), groupDTO);
            }
        } catch (KnCorpBOException e) {
            throw e;
        }
        return groupDetailsMap;
    }

    public Map<Integer, String> getSupervisorGroupsAndName(String mdn, int supervisor, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<Integer, String> grpIdNameMap = new HashMap<>();
        try {
            List<Integer> superGrpList = corpXdmDao.getSupervisorGroups(mdn, supervisor, persisterTxn);
            List<KnCorpGroupDTO> grpDetailsList = getGrpsNameEtagInfo(superGrpList, pttServerId, persisterTxn);
            for (KnCorpGroupDTO groupDTO : grpDetailsList) {
                grpIdNameMap.put(groupDTO.getGroupId(), groupDTO.getGroupDisplayName());
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return grpIdNameMap;
    }

    public Map<Integer, String> getSupervisorGroupsAndName(List<String> mdnList, int groupDispatcher, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        Map<Integer, String> grpIdNameMap = new HashMap<>();
        try {
            List<Integer> superGrpList = corpXdmDao.getSupervisorGroups(mdnList, groupDispatcher, persisterTxn);
            List<KnCorpGroupDTO> grpDetailsList = getGrpsNameEtagInfo(superGrpList, xdmsHomePttId, persisterTxn);
            for (KnCorpGroupDTO groupDTO : grpDetailsList) {
                grpIdNameMap.put(groupDTO.getGroupId(), groupDTO.getGroupDisplayName());
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return grpIdNameMap;
    }

    public Set<Integer> getGrpDispMemList(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Set<Integer> grpIdNameMap;
        try {
            grpIdNameMap = corpXdmDao.getGrpDispMemList(dispatcherGrpList, MDN, pttServerId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return grpIdNameMap;
    }

    public Set<Integer> getGrpIdsHavingAtleastOneMember(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Set<Integer> grpIdNameMap;
        try {
            grpIdNameMap = corpXdmDao.getGrpIdsHavingAtleastOneMember(dispatcherGrpList, MDN, pttServerId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return grpIdNameMap;
    }


    public void updateGrpMemListProperties(Collection<KnCorpGroupMemberDTO> modifiedMembers, int grpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.updateGrpMemListProperties(modifiedMembers, grpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifyGroupOverrdeDND(int overrideDnd, int groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyGroupOverrdeDND(overrideDnd, groupId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public ArrayList<KnCorpGroupDTO> getGroupBasicInfoList(Collection<Integer> groupIds, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getGroupBasicInfoList(groupIds, xdmsHomePttId, false, persisterTxn);
    }

    public ArrayList<KnCorpGroupDTO> getGroupBasicInfoList(Collection<Integer> groupIds, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGropBasicInfoList(Collection<Integer>, String,boolean, KnPersisterTxn)";
        //retrieve groupn name and etag list
        knLogger.debug(methodName, "ENTRY readOnly :", readOnly);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getGroupBasicInfoList(groupIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group name and etag- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method will be used to get the group mdn and the group id it is associated to
     *
     * @param mdnList
     * @param xdmsHome
     * @param persisterTxn
     * @return
     */
    public Map<Integer, List<String>> getSubscriberGroupIds(List<String> mdnList, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubscriberGroupIds(List<String>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubscriberGroupIds(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group name and etag- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method will be used to get the corp group id and the corp id for ABDG Group it is associated to
     *
     * @param corpGroupIdList
     * @param xdmsHome
     * @param persisterTxn
     * @return
     */
    public Map<Integer, List<String>> getCorpGroupIds(Collection<Integer> corpGroupIdList, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCorpGroupIds(Collection<Integer>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupIds(corpGroupIdList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group name and etag- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


   public Map<String, Integer> getMembersGroupCount(List<String> finalGroupMembersinDB, int corpId, String
            xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMembersGroupCount(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            //Collection<Integer> groupList = corpXdmDao.selectGroupIdList(corpId, xdmsHome, persisterTxn);
            return corpXdmDao.selectSubscrGrpCounts(finalGroupMembersinDB, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final group members group count - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public Map<String,Integer> getSubscCampGrpCount(List<String> mdnList, String xdmHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName = "subscCampGrpCount(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
        try {
            Map<String,Integer> subscCampedGroupCount  = corpXdmDao.getCampedGroupCount(mdnList, persisterTxn);
            return subscCampedGroupCount;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final group members group count - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    /**
     * THis method is used to get the Subsc tgsc document etag
     * @param mdnList
     * @param xdmHome
     * @param persisterTxn
     * @return
     */
    public Map<String, Integer> getSubscTgscDocETag(List<String> mdnList,String xdmHome,KnPersisterTxn persisterTxn)throws KnCorpBOException{
        final String methodName = "getSubscTgscDocETag(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
        try {
            Map<String,Integer> subscTgscDocEtag  = corpXdmDao.getSubscTgscDocEtag(mdnList, persisterTxn);
            return subscTgscDocEtag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the Subsc TGSC document etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<Integer> groupsPushedToSublists(Collection<Integer> removeSublistIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Set<Integer> grpIds;
        try {
            grpIds = corpXdmDao.groupsPushedToSublists(removeSublistIds, pttServerId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return grpIds;
    }

    public ArrayList<Integer> getAllGroupsPrivateList(Collection<Integer> groupIdsList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getAllGroupsPrivateList(List<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ArrayList<Integer> groupPrivateListIds;
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            groupPrivateListIds = corpXdmDao.getAllGroupsPrivateList(groupIdsList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while Fetching records from DG.CORPGROUPINFO table ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return groupPrivateListIds;
    }

    public Set<Integer> getGroupSupervisorList(Collection<Integer> groupIds,
                                                                       String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupSupervisorMembers(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupSupervisorList(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is used to fetch the group details from the DB with the specific name for the corporate
     * @param grpPrefix
     * @param corpId
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     */
    public KnCorpGroupDTO getGroupDetailsByName(String grpPrefix, int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "getGroupDetailsByName(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            KnCorpGroupDTO groupDetails  = corpXdmDao.getGroupDetailsByName(grpPrefix,corpId, persisterTxn);
            return groupDetails;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the group details by name   - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifyGroupLmrInteropCapable(int lmrInteropCapable, Collection<Integer> groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyGroupLmrInteropCapable(int, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyGroupLmrInteropCapable(lmrInteropCapable, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group lmrInteropCapable.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifyGroupUGWParameter(int ugwInterop, int groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyGroupUGWParameter(int, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyGroupUGWParameter(ugwInterop, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group ugwInterop.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifyGroupRecordingFsParameter(int recordingFs, int groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyGroupRecordingFsParameter(int, int, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyGroupRecordingFsParameter(recordingFs, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while modifying the group recordingFs.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyAuthorizedLargeTGParameter(int authorizedLargeTG, int groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyAuthorizedLargeTGParameter(int, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyAuthorizedLargeTGParameter(authorizedLargeTG, groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group authorizedLargeTG.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public Map<Integer, Integer> getSGCorpGroupMembersCount(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSGCorpGroupMembersCount(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        return corpXdmDao.getSGCorpGroupMembersCount(groupIds, persisterTxn);
    }

    public Map<String, Integer> getValidGrpTypeInCorp(Collection<String> grpIdList, int corpId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getValidGrpTypeInCorp(List<Integer>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getValidGrpTypeInCorp(grpIdList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> selectGroupMemberForGroupIds(Collection<Integer> groupIds, String memberMdn, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "selectGroupMemberForGroupIds(List<Integer>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.selectGroupMemberForGroupIds(groupIds, memberMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifyGroupEmergAttributes(KnIPCorpGroupInfoDTO groupInfoDTO, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyGroupEmergAttributes(int, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyGroupEmergAttributes(groupInfoDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group modifyGroupEmergAttributes.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectTpGroupList(String mdn, int corpId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "selectTpGroupList(KnIPCorpContactDTO, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Collection<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOS = new ArrayList<>();
            List<Integer> subsGroupIdList = xdmDAO.getSubsGroupIdList(mdn, persisterTxn);
            knLogger.debug(methodName, "subsGroupIdList :", subsGroupIdList);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = xdmDAO.selectTpGroupList(corpId, readOnly, persisterTxn);
            if (!subsGroupList.isEmpty()) {
                subsGroupList.forEach(subsGrpList -> {
                    if(subsGrpList.getTpGroupOwner() != null && subsGrpList.getTpGroupOwner().equals(mdn)){
                        groupInfoPersistDTOS.add(subsGrpList);
                    } else if(subsGroupIdList.contains(subsGrpList.getGroupId())){
                        groupInfoPersistDTOS.add(subsGrpList);
                    }
                });
            }
            knLogger.debug(methodName, "groupInfoPersistDTOS :", groupInfoPersistDTOS);
            if (groupInfoPersistDTOS.isEmpty()) {
                knLogger.error(methodName, "No Groups Found For Subscribers - ", KnGDPRTemplate.mdn(mdn));
                throw new KnCorpBOException(KnErrorCodes.BOEntity.NO_GROUP_EXISTS_FOR_SUBSCRIBER,
                        "Subscribers Does not belong to any group.");
            }
            return groupInfoPersistDTOS;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Integer selectTpGroup(int corpId, String groupDisplayName, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "selectTpGroups(KnIPCorpContactDTO, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = xdmDAO.selectTpGroupList(corpId, readOnly, persisterTxn);
            knLogger.debug(methodName, "subsGroupList :", subsGroupList);
            Map<String, Integer> groupDisplayMap = subsGroupList.stream().collect(Collectors.toMap(KnCorpGroupInfoPersistDTO::getGroupDisplayName, KnCorpGroupInfoPersistDTO::getGroupId));
            if(groupDisplayMap != null && groupDisplayMap.keySet().contains(groupDisplayName)){
                return groupDisplayMap.get(groupDisplayName);
            } else {
                knLogger.error(methodName, "No Groups Found For Subscribers - ", groupDisplayName);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.NO_GROUP_EXISTS_FOR_SUBSCRIBER,
                        "Subscribers Does not belong to any group.");
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpGroupDTO getGroupIdByName(String grpPrefix, int clientIntf, String ownerMdn, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "getGroupIdByName(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getGroupIdByName(grpPrefix, clientIntf, ownerMdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group info details- ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Group Doesn't exist. Rethrowing Exception - ", e);
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getGroupMembersClientTypeMap(Collection<String> mdnList, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGroupMembersClientTypeMap(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMembersClientTypeMap(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean updateGroupOwner(String oldGroupOwner, String newGroupOwner, int corpID, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateGroupOwner(String, String, int, KnPersisterTxn)";
        boolean status = false;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            status = xdmDAO.updateGroupOwner(oldGroupOwner, newGroupOwner, corpID,  persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return status;
    }

    public boolean updateGroupOwner(List<String> oldGroupOwner, String newGroupOwner, int corpID, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateGroupOwner(String, String, int, KnPersisterTxn)";
        boolean status = false;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            status = xdmDAO.updateGroupOwner(oldGroupOwner, newGroupOwner, corpID,  persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return status;
    }

    public Collection<String> getSubscribersGroupList(Collection<Integer> groupIds, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubscribersGroupList(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getSubscribersGroupList(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subsc group dist information- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getGroupMembersClientType(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGroupMembersClientType(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMembersClientType(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<Integer> getCorpGroupCountIntf(int corpId, int intf, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorpGroupCountIntf(int, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupCountIntf(corpId, intf, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getCorpGroupCountIntfPerOwner(int corpId, int intf, String grpOwner, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorpGroupCountIntfPerOwner(int, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCorpGroupCountIntfPerOwner(corpId, intf, grpOwner, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getMembersAbdgGroupCount(Collection<Integer> groupIds, String
            xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMembersAbdgGroupCount(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.selectSubscriberAbdgGroupCounts(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final group members group count - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getExternalSubscriberAbdgGroupCount(Collection<String> externalMdnList,
                                                                int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getExternalSubscriberAbdgGroupCount(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            Map<String, Integer> subscGroupCountMap = new HashMap<String, Integer>();
            if (externalMdnList != null && !externalMdnList.isEmpty()) {
                ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
                subscGroupCountMap = xdmDAO.getExternalSubscriberAbdgGroupCount(externalMdnList, corpId, persisterTxn);
            }
            return subscGroupCountMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExternalSubscriberGroupCount- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<Integer> getOwnerGroupIds(String mdn, int corpId, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getOwnerGroupIds(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getOwnerGroupIds(mdn, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group subscriber dist list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<Integer> getOwnerGroupIds(List<String> mdnList, int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getOwnerGroupIds(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHomePttId);
            return xdmDAO.getOwnerGroupIds(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group subscriber dist list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(String mdn, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getSubsAddlTGList(mdn, xdmsHome, false, persisterTxn);
    }

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(String mdn, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getSubsAddlTGList(KnIPCorpGroupDTO, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsAddlTGList(mdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(Collection<String> mdnList,String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlTGList(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        return xdmDAO.getSubsAddlTGList(mdnList, persisterTxn);
    }

    public void deleteSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "deleteSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSubsAddlTGList(corpAddlTGInfoDTOS, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "insertSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertSubsAddlTGList(corpAddlTGInfoDTOS, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSubsAddlTalkGroup(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "deleteSubsAddlTalkGroup(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSubsAddlTalkGroup(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<Integer>> getZoneChannelMap(String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "getZoneChannelMap(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getZoneChannelMap(persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSubsAddlTalkGroupDoc(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteSubsAddlTalkGroupDoc(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSubsAddlTalkGroupDoc(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlDetails(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getSubsAddlDetails(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsAddlDetails(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Long> getSubsAddlEtagMap(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSubsAddlEtagMap(mdnList, xdmsHome, false, persisterTxn);
    }

    public Map<String, Long> getSubsAddlEtagMap(Collection<String> mdnList, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getSubsAddlEtagMap(Collection<String>, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsAddlEtagMap(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertSubsAddlTGDoc(Map<String, Long> mdnEtagMap, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "insertSubsAddlTGDoc(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertSubsAddlTGDoc(mdnEtagMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateAddlTalkGroupImpactedTables(String pttServerId, Collection<String> mdnList, KnPersisterTxn persisterTxn,
                                                                        Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateAddlTalkGroupImpactedTables(String, String, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            Map<String, KnOPDocChgDTO> addlTGMap = new HashMap<>();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            KnCorpCommonInfoUtil commonInfoUtil= new KnCorpCommonInfoUtil();
           /* Set<String> updateMdn = new HashSet<>();
            Set<String> deleteMdn = new HashSet<>();
            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = corpXdmDao.getSubsAddlTGList(mdnList, persisterTxn);
            if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                for(KnCorpAddlTGInfoDTO subsTG : subsAddlTGList){
                    if(mdnList.contains(subsTG.getMdn())){
                        updateMdn.add(subsTG.getMdn());
                    } else {
                        deleteMdn.add(subsTG.getMdn());
                    }
                }
            } else {
                deleteMdn.addAll(mdnList);
            }*/
            Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.updateEtag(mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, " currdirectoryEtag :", currdirectoryEtag);
            if (!mdnList.isEmpty()) {
                addlTGMap = corpXdmDao.insertOrUpdateAddlTGInfo(mdnList, persisterTxn);
            }
           /* if (!deleteMdn.isEmpty()) {
                addlTGMap = corpXdmDao.deleteFromSubsAddInfoInfo(deleteMdn, addlTGMap, persisterTxn);
            }*/
            knLogger.debug(methodName, " addlTGMap :", KnGDPRTemplate.mapKeyMdn(addlTGMap));
            Map<String, KnCorpSubscriberDTO> subscrInfoMap = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
            for (String subs : currdirectoryEtag.keySet()) {
                Collection<KnOPDocChgDTO> docLists = null;
                subs = subs.trim();
                KnOPDirChgDTO directory = currdirectoryEtag.get(subs);
                if (directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()) {
                    docLists = directory.getDocChgDTO();
                    if (addlTGMap.get(subs) != null) {
                        docLists.add(addlTGMap.get(subs));
                    }
                } else {
                    docLists = new ArrayList<>();
                    if (addlTGMap.get(subs) != null) {
                        docLists.add(addlTGMap.get(subs));
                    }
                }
                directory.setDocChgDTO(docLists);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(subs);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                }
            }
            List<String> tempList = new ArrayList<>(mdnList);
            tempList.removeAll(currdirectoryEtag.keySet());
            knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
            knLogger.debug(methodName, "etagMap :", currdirectoryEtag);
            currdirectoryEtag = commonInfoUtil.setXapRootUri(currdirectoryEtag, pttServerId, persisterTxn);
            return currdirectoryEtag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateAddlTalkGroupImpactedTables(String pttServerId, Collection<String> mdnList, KnPersisterTxn persisterTxn,
                                                                        Map<String, KnOPDirChgDTO> etagMap, boolean upmDocNotify, boolean isDeleteSubsCall) throws KnCorpBOException {
        final String methodName = "updateAddlTalkGroupImpactedTables(String, String, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>,boolean)";
        knLogger.debug(methodName, "ENTRY : mdnList:", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), " etagMap :", etagMap, " upmDocNotify :", upmDocNotify, "isDeleteSubsCall :", isDeleteSubsCall);
        try {
            Map<String, KnOPDocChgDTO> addlTGMap = new HashMap<>();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            KnCorpCommonInfoUtil commonInfoUtil= new KnCorpCommonInfoUtil();
            Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.updateEtag(mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, " currdirectoryEtag :", currdirectoryEtag);
            if (!mdnList.isEmpty() && !isDeleteSubsCall) {
                addlTGMap = corpXdmDao.insertOrUpdateAddlTGInfo(mdnList, persisterTxn);
            }
            knLogger.debug(methodName, " addlTGMap :", KnGDPRTemplate.mapKeyMdn(addlTGMap));
            Map<String, KnCorpSubscriberDTO> subscrInfoMap = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
            for (String subs : currdirectoryEtag.keySet()) {
                Collection<KnOPDocChgDTO> docLists = null;
                subs = subs.trim();
                KnOPDirChgDTO directory = currdirectoryEtag.get(subs);
                if (directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()) {
                    docLists = directory.getDocChgDTO();
                    if (addlTGMap.get(subs) != null) {
                        docLists.add(addlTGMap.get(subs));
                    }
                } else {
                    docLists = new ArrayList<>();
                    if (addlTGMap.get(subs) != null) {
                        docLists.add(addlTGMap.get(subs));
                    }
                }
                directory.setDocChgDTO(docLists);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(subs);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(!upmDocNotify&&subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                }
            }
            List<String> tempList = new ArrayList<>(mdnList);
            tempList.removeAll(currdirectoryEtag.keySet());
            knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
            knLogger.debug(methodName, "etagMap :", currdirectoryEtag);
            currdirectoryEtag = commonInfoUtil.setXapRootUri(currdirectoryEtag, pttServerId, persisterTxn);
            return currdirectoryEtag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateAddlTalkGroupImpactedTablesForUpm(String pttServerId, Collection<String> mdnList, KnPersisterTxn persisterTxn,
                                                                        Map<String, KnOPDirChgDTO> etagMap, boolean upmDocNotify, boolean isDeleteSubsCall) throws KnCorpBOException {
        final String methodName = "updateAddlTalkGroupImpactedTablesForUpm(String, String, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>,boolean)";
        knLogger.debug(methodName, "ENTRY : mdnList:", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), " etagMap :", etagMap, " upmDocNotify :", upmDocNotify, "isDeleteSubsCall :", isDeleteSubsCall);
        try {
            Map<String, KnOPDocChgDTO> addlTGMap = new HashMap<>();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            KnCorpCommonInfoUtil commonInfoUtil= new KnCorpCommonInfoUtil();
            Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.updateEtagForUpm(mdnList, null, etagMap);
            knLogger.debug(methodName, " currdirectoryEtag :", currdirectoryEtag);
            if (!mdnList.isEmpty() && !isDeleteSubsCall) {
                addlTGMap = corpXdmDao.insertOrUpdateAddlTGInfo(mdnList, persisterTxn);
            }
            knLogger.debug(methodName, " addlTGMap :", KnGDPRTemplate.mapKeyMdn(addlTGMap));
            Map<String, KnCorpSubscriberDTO> subscrInfoMap = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
            for (String subs : currdirectoryEtag.keySet()) {
                Collection<KnOPDocChgDTO> docLists = null;
                subs = subs.trim();
                KnOPDirChgDTO directory = currdirectoryEtag.get(subs);
                if (directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()) {
                    docLists = directory.getDocChgDTO();
                    if (addlTGMap.get(subs) != null) {
                        docLists.add(addlTGMap.get(subs));
                    }
                } else {
                    docLists = new ArrayList<>();
                    if (addlTGMap.get(subs) != null) {
                        docLists.add(addlTGMap.get(subs));
                    }
                }
                directory.setDocChgDTO(docLists);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(subs);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(!upmDocNotify&&subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                }
            }
            List<String> tempList = new ArrayList<>(mdnList);
            tempList.removeAll(currdirectoryEtag.keySet());
            knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
            knLogger.debug(methodName, "etagMap :", currdirectoryEtag);
            currdirectoryEtag = commonInfoUtil.setXapRootUri(currdirectoryEtag, pttServerId, persisterTxn);
            return currdirectoryEtag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnLargeGrpCountDTO getLargeGroupCounts(int corpId, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException{
        final String methodName = "getLargeGroupCounts()";
        KnLargeGrpCountDTO largeGrpCountDTO = new KnLargeGrpCountDTO();
        try{
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            List<KnCorpGroupDTO> groupList = corpXdmDao.getAllLargeGroups(persisterTxn);
            int lrgGroupSystem = 0;
            int lrgGroupCorp = 0;
            int lrgBCGroupCorp = 0;
            for(KnCorpGroupDTO corpGroupDTO : groupList){
                lrgGroupSystem++;
                if(corpId == corpGroupDTO.getCorpId()){
                    if(corpGroupDTO.getGroupType() == KnConstants.BROADCAST_GROUP){
                        lrgBCGroupCorp++;
                    }else{
                        lrgGroupCorp++;
                    }
                }
            }
            largeGrpCountDTO.setLrgGrpCountCorp(lrgGroupCorp);
            largeGrpCountDTO.setLrgGrpCountSystem(lrgGroupSystem);
            largeGrpCountDTO.setLargeBCGrpCountCorp(lrgBCGroupCorp);
        }catch (KnDAOException e){
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "largeGrpCountDTO :", largeGrpCountDTO);
        return largeGrpCountDTO;
    }

    public void validateLargeGroupCounts(KnCorpProfileDTO corpProfileDTO, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException, KnBOException{
        final String methodName = "validateLargeGroupCounts()";
        KnLargeGrpCountDTO largeGrpCountDTO = getLargeGroupCounts(corpProfileDTO.getCorpId(), persisterTxn, xdmsHome);
        if(largeGrpCountDTO.getLrgGrpCountCorp() > corpProfileDTO.getMaxLrgGrpPerCorp()){
            knLogger.error(methodName, "Max large group per corporate exceeded");
            throw new KnCorpBOException(KnErrorCodes.BOEntity.MAX_LARGE_GROUP_PER_CORP_EXCEEDED,
                    "Max large group per corporate exceeded");
        }
        if(largeGrpCountDTO.getLargeBCGrpCountCorp() > corpProfileDTO.getMaxLrgBGrpPerCorp()){
            knLogger.error(methodName, "Max large broadcast group per corporate exceeded");
            throw new KnCorpBOException(KnErrorCodes.BOEntity.MAX_LARGE_BCGROUP_PER_CORP_EXCEEDED,
                    "Max large broadcast group per corporate exceeded");
        }
        KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
        int maxLrgGrpCountSystem = Integer.parseInt(genInfoUtil.retrieveRTXConfigValues(persisterTxn).get(NUM_OF_LG_SUPPORTED));
        if(largeGrpCountDTO.getLrgGrpCountSystem() > maxLrgGrpCountSystem){
            knLogger.error(methodName, "Max large group in the system exceeded");
            throw new KnCorpBOException(KnErrorCodes.BOEntity.MAX_LARGE_GROUP_PER_SYSTEM_EXCEEDED,
                    "Max large group in the system exceeded");
        }
    }

    public void updateGrpMemListLocWatchers(List<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateGrpMemListLocWatchers(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateIsLargeGrpFlag(Map<Integer, Integer> groupLrgGrpFlagMap, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateIsLargeGrpFlag(groupLrgGrpFlagMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getLrgAbdgGroupCount(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getLrgAbdgGroupCount(corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteAllTGListGrpIds(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteAllTGListGrpIds(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void cleanUpTGSSGrp(List<Integer> deletedGrpIds, KnCorpInOutParamDTO corpInOutParamDTO, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        try {
            KnCorpInOutParamDTO corpInputParamDTo = xdmDAO.cleanUpTGSSGrp(deletedGrpIds,persisterTxn );
            corpInOutParamDTO.setMdnTgssEtag(corpInputParamDTo.getMdnTgssEtag());
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public void cleanUpTGSSGrpMdn(Integer deletedGrpId,List<String> mdns, KnCorpInOutParamDTO corpInOutParamDTO, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        try {
            KnCorpInOutParamDTO corpInputParamDTo = xdmDAO.cleanUpTGSSGrpMdn(deletedGrpId,mdns,persisterTxn );
            corpInOutParamDTO.setMdnTgssEtag(corpInputParamDTo.getMdnTgssEtag());
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public Collection<String> getLocWatcherAndDispatcher(Collection<Integer> grpIds, String xdmsHomePttId,
                                                         KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getLocWatcherAndDispatcher(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getLocWatcherAndDispatcher(grpIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSubsc(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGrpSubsc(Collection<Integer>, Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGrpSubsc(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    /**
     * This method returns a Map of group member MDN and its is_supervisor value
     *
     * @param groupId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpGroupMemberDTO> getGroupMembersListForWcsrOrCatUi(int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMembersListForWcsrOrCatUi(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMembersListForWcsrOrCatUi(groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public List<String> getRealMdns(List<String> mdns, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        return getRealMdns(mdns, xdmsHome, false, persisterTxn);
    }

    /**
     * This method returns List of real Mdns
     *
     * @param mdns
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getRealMdns(List<String> mdns, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getRealMdns(List<String>,String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getRealMdns(mdns, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public Set<String> getUniqueMcpttIds(Collection<String> mdns, String
            xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUniqueMcpttIds(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getUniqueMcpttIds(mdns, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final group members group count - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpGroupProfileInfo getGroupProfileDetail(Integer profileId, String profileName, int corpId, String xdmsHome, boolean readOnly,
                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        KnCorpGroupProfileInfo profileInfo = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            if (profileId != null) {
                profileInfo = xdmDAO.getGroupProfileDetailById(profileId, corpId, readOnly, persisterTxn);
            } else if (null != profileName) {
                profileInfo = xdmDAO.getGroupProfileDetailByName(profileName, corpId, readOnly, persisterTxn);
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return profileInfo;
    }

    public List<String> getExistingGroupName(int corpId, List<String> grpNameList, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getExistingGroupName(grpNameList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void createBulkGroupInfoDetails(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, String xdmsHome,
                                           KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createBulkGroupInfoDetails(groupInfoPersistDTOList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void addBulkSublistsToGroup(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, String xdmsHome,
                                       KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.addBulkSublistsToGroup(groupInfoPersistDTOList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public List<KnCorpGroupInfoPersistDTO> getProfileGroupList(KnIPCorpGroupProfileDTO groupProfileDTO, int
            maxMemPerCorpGroup, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getProfileGroupList()";
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.selectProfileGroupList(groupProfileDTO, maxMemPerCorpGroup, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getSubsScrGroupCount(List<String> mdns, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsScrGroupCount(List<String>, KnPersisterTxn)";
         knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubsScrGroupCount(mdns, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count",
                    e);
            throw e;
        }
    }

    public Map<String, Integer> getSubsScrGroupCount(List<String> mdns, String xdmsHome, KnPersisterTxn persisterTxn, int corpId) throws KnDAOException {
        final String methodName = "getSubsScrGroupCount(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubsScrGroupCount(mdns, persisterTxn, corpId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count",
                    e);
            throw e;
        }
    }

    public Map<String, Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsScrGroupCountExceptABDG(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubsScrGroupCountExceptABDG(mdns, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count",
                    e);
            throw e;
        }
    }

    public Map<String, Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, String xdmsHome, KnPersisterTxn persisterTxn, int corpId) throws KnDAOException {
        final String methodName = "getSubsScrGroupCountExceptABDG(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubsScrGroupCountExceptABDG(mdns, persisterTxn, corpId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the standard group count",
                    e);
            throw e;
        }
    }

    public Map<Integer, KnCorpGroupDTO> getGroupsByGrpProfileId(Integer profileId, int corpId, KnPersisterTxn
            persisterTxn, String xdmsHome) throws KnCorpBOException {
        final String methodName = "getGroupsByGrpProfileId()";
        knLogger.debug(methodName, "ENTRY, profileId", profileId);
        KnIPCorpGroupProfileDTO groupProfileDTO = new KnIPCorpBulkGroupDTO();
        groupProfileDTO.setCorpId(corpId);
        groupProfileDTO.setProfileId(profileId);
        List<KnCorpGroupInfoPersistDTO> grpList = getProfileGroupList(groupProfileDTO, 250, xdmsHome, false, persisterTxn);
        Map<Integer, KnCorpGroupDTO> resp = new HashMap<>();
        for (KnCorpGroupInfoPersistDTO groupInfo : grpList) {
            KnCorpGroupDTO grpDto = new KnCorpGroupDTO();
            grpDto.setGroupId(groupInfo.getGroupId());
            grpDto.setETag(groupInfo.getETag());
            grpDto.setGrpMemListId(groupInfo.getGroupMemberListId());
            resp.put(groupInfo.getGroupId(), grpDto);
        }
        knLogger.debug(methodName, "Exit, resp", resp);
        return resp;
    }

    public Map<String, List<Integer>> getSubscriberDistGroupList(Set<Integer> groupList, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubscriberDistGroupList(groupList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateGroupInfoProperties(Map<Integer, KnCorpGroupDTO> groupEtagList, KnCorpGroupProfilePersistDTO groupProfilePersistDTO, String
            xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateGroupInfoProperties(groupEtagList, groupProfilePersistDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }


    public void deleteBulkGroup(List<Integer> groupIdList, List<Integer> privateGrpListIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteGroup(int, String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY :", groupIdList != null ? groupIdList.size() : null);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.deleteAllGrpHierarchy(groupIdList, persisterTxn);
            corpXdmDao.deleteAllGroupsSublistRef(groupIdList, persisterTxn);
            corpXdmDao.deleteAllCorpSublistMembers(privateGrpListIds, persisterTxn);
            corpXdmDao.deleteAllGroupsDistribution(groupIdList, persisterTxn);
            corpXdmDao.deleteAllGroupsCorpGroupMemberCountEntry(groupIdList, persisterTxn);
            corpXdmDao.deleteBulkProfileGroupInfo(groupIdList, persisterTxn);
            corpXdmDao.deleteSharedGroups(groupIdList, persisterTxn);
            corpXdmDao.deleteAllGroups(groupIdList, persisterTxn);
            corpXdmDao.deleteAllSublistInfo(new ArrayList<>(privateGrpListIds), persisterTxn);
            corpXdmDao.deleteAllCorpGroupMemberList(groupIdList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void createGroupSharedCorpInfo(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, String xdmsHome,
                                           KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createGroupSharedCorpInfo(groupInfoPersistDTOList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("createGroupSharedCorpInfo", "KnDAOException occured while creating shared corp info for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfo(int ownedCorpId, int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return selectGroupSharedCorpInfo(ownedCorpId, groupId, xdmsHome, false, persisterTxn);
    }

    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfo(int ownedCorpId, int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        List<KnCorpSharedCorpInfo> sharedCorpInfoList = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            sharedCorpInfoList = xdmDAO.selectGroupSharedCorpInfo(ownedCorpId, groupId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("selectGroupSharedCorpInfo", "KnDAOException occured while fetching shared corp info for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return sharedCorpInfoList;
    }

    public void populateGroupMemProperties(List<KnCorpSharedCorpInfo> sharedCorpInfos, KnGeneralCacheUtil generalCacheUtil, String extCorpId, boolean isOwnedCorp) throws KnDAOException {
        final String methodName = "populateGroupMemProperties(List<KnCorpSharedCorpInfo>)";
        knLogger.info(methodName, "Entry -fetching trust matrix info", extCorpId,"isOwnedCorp -",isOwnedCorp);
        List<KnCorpTrustMatrixDTO> trustMatrixDTOList = new ArrayList<>();
        if(isOwnedCorp)
            trustMatrixDTOList = generalCacheUtil.getSharedCorpMatrix(extCorpId);
        else
            trustMatrixDTOList = generalCacheUtil.getSharedCorpMatrixBySharedCorpId(extCorpId);

        for (KnCorpSharedCorpInfo sharedCorpInfo : sharedCorpInfos) {
            if (sharedCorpInfo.getMemFeaturesAllowed() != null) {
                KnCorpGroupContactDTO corpGroupContactDTO = new KnCorpGroupContactDTO();
                BitSet bitSet = KnGeneralUtil.convertLongToBitSet(sharedCorpInfo.getMemFeaturesAllowed());
                corpGroupContactDTO.setIsSupervisor(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.SUPERVISOR.value()) ? 1 : 0);
                corpGroupContactDTO.setIsBroadcaster(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_BROADCASTER.value()) ? 1 : 0);
                corpGroupContactDTO.setCallInitiateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.CALL_INITIATE_PERMISSION.value()) ? 1 : 0);
                corpGroupContactDTO.setCallTerminateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.CALL_RECEIVE_PERMISSION.value()) ? 1 : 0);
                corpGroupContactDTO.setIncallAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.INCALL_PERMISSION.value()) ? 1 : 0);
                corpGroupContactDTO.setCallInitiateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.VIDEO_CALL_INITIATE_PERMISSION.value()) ? 1 : 0);
                corpGroupContactDTO.setCallTerminateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.VIDEO_CALL_RECEIVE_PERMISSION.value()) ? 1 : 0);
                corpGroupContactDTO.setIncallAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.VIDEO_INCALL_PERMISSION.value()) ? 1 : 0);
                corpGroupContactDTO.setIsLocSupervisor(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_LOCWATCHER.value()) ? 1 : 0);
                corpGroupContactDTO.setIsOSMAuthorized(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_OSMAUTHORIZED.value()) ? 1 : 0);
                sharedCorpInfo.setGrpMemProps(corpGroupContactDTO);
            } else {
                knLogger.debug(methodName, "memberProperties in sharedCOrpInfo are null hence fetching from trust matrix");
                Optional<Long> memOptionalFeatureAllowed = trustMatrixDTOList.stream().
                        filter(e -> e.getSharedExtCorpId().trim().equalsIgnoreCase(sharedCorpInfo.getExtCorpId().trim())).
                        map(KnCorpTrustMatrixDTO::getMemFeaturesAllowed).
                        findFirst();
                if (memOptionalFeatureAllowed.isPresent()) {
                    knLogger.debug(methodName, "memberProperties from trust matrix is =", memOptionalFeatureAllowed.get()," for corpid", sharedCorpInfo.getCorpId());
                    KnCorpGroupContactDTO corpGroupContactDTO = new KnCorpGroupContactDTO();
                    BitSet bitSet = KnGeneralUtil.convertLongToBitSet(memOptionalFeatureAllowed.get());
                    corpGroupContactDTO.setIsSupervisor(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.SUPERVISOR.value()) ? 1 : 0);
                    corpGroupContactDTO.setIsBroadcaster(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_BROADCASTER.value()) ? 1 : 0);
                    corpGroupContactDTO.setCallInitiateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.CALL_INITIATE_PERMISSION.value()) ? 1 : 0);
                    corpGroupContactDTO.setCallTerminateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.CALL_RECEIVE_PERMISSION.value()) ? 1 : 0);
                    corpGroupContactDTO.setIncallAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.INCALL_PERMISSION.value()) ? 1 : 0);
                    corpGroupContactDTO.setCallInitiateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.VIDEO_CALL_INITIATE_PERMISSION.value()) ? 1 : 0);
                    corpGroupContactDTO.setCallTerminateAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.VIDEO_CALL_RECEIVE_PERMISSION.value()) ? 1 : 0);
                    corpGroupContactDTO.setIncallAllowed(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.VIDEO_INCALL_PERMISSION.value()) ? 1 : 0);
                    corpGroupContactDTO.setIsLocSupervisor(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_LOCWATCHER.value()) ? 1 : 0);
                    corpGroupContactDTO.setIsOSMAuthorized(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_OSMAUTHORIZED.value()) ? 1 : 0);
                    sharedCorpInfo.setGrpMemProps(corpGroupContactDTO);
                }
            }
        }
        knLogger.info(methodName, "Exit - ");
    }

    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfo(int ownedCorpId, Collection<Integer> groupIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            sharedCorpInfoMap = xdmDAO.selectGroupSharedCorpInfo(ownedCorpId, groupIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("selectGroupSharedCorpInfo", "KnDAOException occured while fetching shared corp info for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return sharedCorpInfoMap;
    }

    public Map<Integer,List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoBySharedCorpId(int sharedCorpId,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException{
        return selectGroupSharedCorpInfoBySharedCorpId(sharedCorpId, xdmsHome, false, persisterTxn);
    }

    public Map<Integer,List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoBySharedCorpId(int sharedCorpId,String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException{
        Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            sharedCorpInfoMap = xdmDAO.selectGroupSharedCorpInfoBySharedCorpId(sharedCorpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("selectGroupSharedCorpInfoBySharedCorpId", "KnDAOException occured while fetching shared corp info for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return sharedCorpInfoMap;
    }

    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoByGroupId(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return selectGroupSharedCorpInfoByGroupId(groupIds, xdmsHome, false, persisterTxn);
    }

    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoByGroupId(Collection<Integer> groupIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            sharedCorpInfoMap = xdmDAO.selectGroupSharedCorpInfoByGroupId(groupIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("selectGroupSharedCorpInfoByGroupId", "KnDAOException occured while fetching shared corp info for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return sharedCorpInfoMap;
    }

    /**
     * Method to filter out the other corporate members froma shared group.
     * @param grpMemberMap
     * @param requestingCorpId
     * @param corpId
     */
    public void filterGroupMembers(Map<String, KnCorpGroupMemberDTO> grpMemberMap, int requestingCorpId, int corpId) {
        String methodName = "filterGroupMembers()";
        if(grpMemberMap == null || grpMemberMap.isEmpty())
            return;
        Set<String> keys = new HashSet<>(grpMemberMap.keySet());
        if(requestingCorpId == corpId){
            knLogger.info(methodName, "Request from own corporate mdn");
            for(String mdn : keys){
                KnCorpGroupMemberDTO memberDTO = grpMemberMap.get(mdn);
                if(memberDTO.getCorpId() !=0 && memberDTO.getCorpId() != requestingCorpId){
                    grpMemberMap.remove(mdn);
                }
            }

        }else{
            knLogger.info(methodName, "Request from shared corporate mdn");
            for(String mdn : keys){
                KnCorpGroupMemberDTO memberDTO = grpMemberMap.get(mdn);
                if(memberDTO.getCorpId() == 0 || memberDTO.getCorpId() != requestingCorpId){
                    grpMemberMap.remove(mdn);
                }
            }
        }
        knLogger.debug(methodName, "After filter - ", KnGDPRTemplate.mapKeyMdn(grpMemberMap));
    }

    public List<KnCorpGroupInfoPersistDTO> getSharedGroupDetails(Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap,
                                                                 String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSharedGroupDetails(sharedCorpGrpInfoMap, xdmsHome, false, persisterTxn);
    }

    public List<KnCorpGroupInfoPersistDTO> getSharedGroupDetails(Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap,
                                                                 String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getSharedGroupDetails()";
        Map<Integer, KnCorpGroupDTO> sharedGrpInfoMap = getGroupBasicDetailsMap(sharedCorpGrpInfoMap.keySet(), xdmsHome, readOnly, persisterTxn);
        List<KnCorpGroupInfoPersistDTO> sharedGroupList = sharedGrpInfoMap.values().stream().map(knCorpGroupDTO -> {
            KnCorpGroupInfoPersistDTO persistDTO = new KnCorpGroupInfoPersistDTO();
            persistDTO.setCorpId(knCorpGroupDTO.getCorpId());
            persistDTO.setGroupId(knCorpGroupDTO.getGroupId());
            persistDTO.setGrpShared(knCorpGroupDTO.getGrpShared());
            persistDTO.setMcxGrpInd(knCorpGroupDTO.getMcxGrpInd());
            persistDTO.setGroupCreatedBy(knCorpGroupDTO.getGroupCreatedBy());
            persistDTO.setGroupDisplayName(knCorpGroupDTO.getGroupDisplayName());
            persistDTO.setGroupType(knCorpGroupDTO.getGroupType());
            persistDTO.setPocHome(knCorpGroupDTO.getPocHome());
            persistDTO.setUgwInterop(knCorpGroupDTO.getUgwInterop());
            if(knCorpGroupDTO.getGroupMemCount()!=null) {
                persistDTO.setMemberCount(knCorpGroupDTO.getGroupMemCount());
            }
            persistDTO.setHierarchyId(knCorpGroupDTO.getHierarchyId());
            return persistDTO;
        }).collect(Collectors.toList());
        knLogger.debug(methodName, "Returning - ", sharedGroupList);
        return sharedGroupList;
    }

    public void deleteProfileGroupInfoByProfileIds(List<String> profileIds, String xdmsHome,
                                          KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteProfileGroupInfoByProfileIds(profileIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("deleteProfileGroupInfoByProfileIds", "KnDAOException occured while - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSharedGroupsByOwnedCorp(Integer ownedCorpId, String xdmsHome,
                                                   KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSharedGroupsByOwnedCorp(ownedCorpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("deleteSharedGroupsByOwnedCorp", "KnDAOException occured while - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public void deleteSharedGroups(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteSharedGroups(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, List<Integer>> getGroupIsSharedCorpListMap(List<Integer> groupIdList, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        Map<Integer,List<Integer>> groupSharedCorpListMap = new HashMap<>();
        Map<Integer,List<KnCorpSharedCorpInfo>> sharedList = selectGroupSharedCorpInfoByGroupId(groupIdList, xdmsHome, persisterTxn);
        if(sharedList!=null){
            for(Integer grpId : sharedList.keySet()){
                List<Integer> corpList = new ArrayList<>();
                sharedList.get(grpId).forEach(item -> corpList.add(item.getCorpId()));
                groupSharedCorpListMap.put(grpId, corpList);
            }
        }
        return groupSharedCorpListMap;
    }

    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfoByOwnedCorpId(int ownedCorpId,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException{
        List<KnCorpSharedCorpInfo> ownedCorpGroupInfo = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            ownedCorpGroupInfo = xdmDAO.selectGroupSharedCorpInfoByOwnedCorpId(ownedCorpId,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("selectGroupSharedCorpInfoByOwnedCorpId()", "KnDAOException occured while fetching owned corp info for group - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return ownedCorpGroupInfo;
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupList(String mdn, int maxMemPerGroup,
                                                                  String xdmsHome, boolean readOnly,
                                                                  KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsGroupList(String, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = xdmDAO.selectSubsGroupList(mdn, maxMemPerGroup, readOnly, persisterTxn);
            if (subsGroupList == null || subsGroupList.isEmpty()) {
                knLogger.error(methodName, "No Groups Founf For Subscribers - ", KnGDPRTemplate.mdn(mdn));
                throw new KnCorpBOException(KnErrorCodes.BOEntity.NO_GROUP_EXISTS_FOR_SUBSCRIBER,
                        "Subscribers Does not belong to any group.");
            }
            return subsGroupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupListPaginated(String mdn, int maxMemPerGroup,
                                                                  int fetchSize, int nextToken,
                                                                  String xdmsHome, boolean readOnly,
                                                                  KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsGroupList(String, int, int, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Collection<KnCorpGroupInfoPersistDTO> subsGroupList = xdmDAO.selectSubsGroupListPaginated(mdn, maxMemPerGroup,
                    fetchSize, nextToken, readOnly, persisterTxn);
            if (subsGroupList == null || subsGroupList.isEmpty()) {
                knLogger.error(methodName, "No Groups Found For Subscribers - ", KnGDPRTemplate.mdn(mdn));
            }
            return subsGroupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getGroupPrivateListId(int groupId, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupPrivateListId(groupId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertGroupHiearchyMap(Integer groupId, List<String> ownerFanIds, String idType, String xdmsHome,
                                       KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubsGroupList(String, int, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Set<String> existingIds = xdmDAO.getOwnerAndSharedContextIdByGroupIds(List.of(groupId), idType, false, persisterTxn).keySet();
            ownerFanIds.removeIf(existingIds::contains);
            knLogger.debug(methodName, " Valid ownerIds to be inserted ", ownerFanIds);
            if (null != ownerFanIds && !ownerFanIds.isEmpty()) {
                xdmDAO.insertGroupHiearchyMap(groupId, ownerFanIds, idType, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "insertGroupHiearchyMap", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertGroupHiearchyMap(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, List<String> ownerFanIds, String idType, String xdmsHome,
                                       KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertGroupHiearchyMap(groupInfoPersistDTOList, ownerFanIds, idType, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("insertGroupHiearchyMap", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void removeGroupHiearchyMapByOwnerIds(Integer groupId, List<String> ownerFanIds, String idType, String xdmsHome,
                                                 KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.removeGroupHiearchyMapByOwnerIds(groupId,ownerFanIds,idType, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("removeGroupHiearchyMapByOwnerIds", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void removeGroupHiearchyMapByGroupId(Integer groupId, String xdmsHome,
                                                KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.removeGroupHiearchyMapByGroupId(groupId,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("removeGroupHiearchyMapByGroupId", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, List<String>> getGroupOwnerList(List<Integer> groupIds, String idType, String xdmsHome, boolean readOnly,
                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupOwnerList(groupIds, idType, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("getGroupOwnerList", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, List<String>> getGroupSharedIdList(List<Integer> groupIds, String idType, String xdmsHome,
                                                           boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupSharedIdList(groupIds, idType, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("getGroupSharedIdList", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> isValidGroupForIdList(List<Integer> groupIds,List<Integer> idList,String idType, String xdmsHome, boolean readOnly,
                                                       KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.isValidGroupForIdList(groupIds, idList, idType, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("isValidGroupForIdList", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnCorpGroupInfoPersistDTO> getOwnerGroupDetails(Set<Integer> groupIds,
                                                                String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        String methodName = "getOwnerGroupDetails()";
        Map<Integer,KnCorpGroupDTO> ownerGrpInfoMap = getGroupBasicDetailsMap(groupIds,xdmsHome,persisterTxn);
        List<KnCorpGroupInfoPersistDTO> ownerGroupList = ownerGrpInfoMap.values().stream().map(knCorpGroupDTO -> {
            KnCorpGroupInfoPersistDTO persistDTO = new KnCorpGroupInfoPersistDTO();
            persistDTO.setCorpId(knCorpGroupDTO.getCorpId());
            persistDTO.setGroupId(knCorpGroupDTO.getGroupId());
            persistDTO.setGrpShared(knCorpGroupDTO.getGrpShared());
            persistDTO.setMcxGrpInd(knCorpGroupDTO.getMcxGrpInd());
            persistDTO.setGroupCreatedBy(knCorpGroupDTO.getGroupCreatedBy());
            persistDTO.setGroupDisplayName(knCorpGroupDTO.getGroupDisplayName());
            persistDTO.setGroupType(knCorpGroupDTO.getGroupType());
            persistDTO.setPocHome(knCorpGroupDTO.getPocHome());
            return persistDTO;
        }).collect(Collectors.toList());
        knLogger.debug(methodName, "Returning - ", ownerGroupList);
        return ownerGroupList;
    }

    public int getSystemPreConfigGroupCont(String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSystemPreConfigGroupCont( String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSystemPreConfigGroupCont(persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the preConfig group count",
                    e);
            throw e;
        }
    }

    public List<String> getSharedGroupMemberBySharedAndOwnCorpids(int ownedCorpId,List<Integer> sharedCorpids, String xdmsHome,
                                          KnPersisterTxn persisterTxn) throws KnCorpBOException{
        List<String> groupMemberList = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            groupMemberList=xdmDAO.getSharedGroupMemberBySharedAndOwnCorpids(ownedCorpId,sharedCorpids, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("getSharedGroupMemberBySharedAndOwnCorpids", "KnDAOException occured", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return groupMemberList;
    }

    public Map<Integer,KnCorpGroupInfoPersistDTO> getAllPreconfigGroupInfo(int corpId, String xdmsHome,
                                                              KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        String methodName = "getAllPreconfigGroupInfo()";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getAllPreconfigGroupInfo(corpId, persisterTxn);
        } catch (KnDAOException e) {
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<Integer> getIsPreConfiguredParamList(List<Integer> groupIdList,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getIsPreConfiguredParamList(groupIdList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("getIsPreConfiguredParamList", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getPreConfGrpCorpMem(Integer grpShared,int grpId, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName ="getPreConfGrpCorpMem(Integer,int, int corpId,  String xdmsHome, KnPersisterTxn )" ;
        knLogger.debug(methodName," grpShared :",grpShared," grpId :",grpId," corpId :",corpId);
        List<Integer> intSharedCorpIds =new ArrayList<>();
        KnCorpCommonInfoUtil commonInfoUtil= new KnCorpCommonInfoUtil();
        if(grpShared != null && grpShared.equals(ENABLED)){
            List<Integer> groupIdList = new ArrayList<>();
            groupIdList.add(grpId);
            //get the list of corporate where the group is shared
            Map<Integer, List<Integer>> corpSharedCorpInfos = getGroupIsSharedCorpListMap(groupIdList, xdmsHome, persisterTxn);
            if(corpSharedCorpInfos != null) {
                intSharedCorpIds = corpSharedCorpInfos.get(grpId);
            }
        }
        return commonInfoUtil.getAllPreConfigGroupMdns(corpId,grpShared,intSharedCorpIds,xdmsHome, persisterTxn);
    }

    public String getUgwConfig(int corpId, int groupId, String activeFS2, KnFeatureSetUtil featureSetUtil, KnGeneralCacheUtil generalCacheUtil) throws KnDAOException {
        String methodName = "getUgwConfig(int, int, KnFeatureSetUtil , KnGeneralCacheUtil)";
        String ugwConfig = null;
        boolean astroFreqSelectActiveFs = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.ASTRO_FREQUENCT_SELECT_BIT.value());
        boolean astroCodedAndClearActiveFs = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.ASTRO_CODED_AND_CLEAR_BIT.value());
        if (astroFreqSelectActiveFs || astroCodedAndClearActiveFs) {
            KnCorpGrpLmrExtnDTO corpGrpLmrExtnDTO = generalCacheUtil.getCorpGrpLmrExtn(corpId, groupId);
            knLogger.debug(methodName, "corpGrpLmrExtnDTO obtained from the DB is-->", corpGrpLmrExtnDTO);
            if (corpGrpLmrExtnDTO != null && corpGrpLmrExtnDTO.getLmrExtn() != null && corpGrpLmrExtnDTO.getLmrExtn().length != 0) {
                ugwConfig = new String(corpGrpLmrExtnDTO.getLmrExtn());
            }
        }
        knLogger.debug(methodName, "ugwConfig is-->", ugwConfig);
        return ugwConfig;
    }

    public Boolean validateExtCorpGroup(String userProfileId, int subscCorpID, int groupOwnerCorpId, int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateExtCorpGroup()";
        knLogger.info(methodName, "subscCorpID", subscCorpID, "groupOwnerCorpId", groupOwnerCorpId,
                "groupId", groupId, "userProfileId", userProfileId);
        boolean isExtCorpGroup;
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        if (groupOwnerCorpId == subscCorpID) {
            return false;
        }
        List<Integer> sharedCorpIds = xdmDAO.getSharedCorpIds(groupId, xdmsHome, readOnly, persisterTxn);
        knLogger.debug(methodName, "Shared Corp IDs: ", sharedCorpIds, " for groupId - ", groupId);
        isExtCorpGroup = (sharedCorpIds != null && !sharedCorpIds.contains(subscCorpID));
        List<Integer> sharedCorpIdsUserProfile = null;
        if (userProfileId != null) {
            sharedCorpIdsUserProfile = xdmDAO.getUpmSharedCorpIds(userProfileId, xdmsHome, readOnly, persisterTxn);
            knLogger.debug(methodName, "Shared Corp IDs from UserProfile: ", sharedCorpIdsUserProfile,
                    " for userProfileId - ", userProfileId);
            isExtCorpGroup = (sharedCorpIds != null && !sharedCorpIds.contains(subscCorpID)) &&
                    (sharedCorpIdsUserProfile != null && !sharedCorpIdsUserProfile.contains(subscCorpID));
        }
        knLogger.info(methodName, "isExtCorpGroup: ", isExtCorpGroup);
        return isExtCorpGroup;
    }

    public void modifyBulkGroupProperties(List<KnXDMGroupPropertyInfoDTO> bulkGroupProperties, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifyBulkGroupProperties(int, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyBulkGroupProperties(bulkGroupProperties, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while modifying the group recordingFs.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpGroupInfoPersistDTO getGroupBasicDetails(int groupId, String xdmsHome,
                                                                      KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        String methodName = "getGroupBasicDetails(int, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupBasicDetails(groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "failed to fetch the group details ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnCorpGroupInfoPersistDTO> getProfileMdnsAbdgGroupList(String profileMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException {
        String methodName = "getProfileMdnsAbdgGroupList()";
        knLogger.debug(methodName, " profileMdns ", KnGDPRTemplate.mdn(profileMdn));
        KnCorpSubsProvInfoUtil subsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        List<String> profileMdns = new ArrayList<>();
        List<KnCorpGroupInfoPersistDTO> abdgGroups = new ArrayList<>();
        profileMdns.add(profileMdn);
        Set<String> baseMdn;
        Collection<KnCorpGroupInfoPersistDTO> groupList;
        try {
            baseMdn = subsProvInfoUtil.getBaseMdnByProfileMdns(profileMdns, xdmsHome, persisterTxn);
            groupList = getSubsAbdgGroupList(baseMdn.iterator().next(), xdmsHome, persisterTxn);
            abdgGroups = new ArrayList<>(groupList);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, " failed to fetch the ABDG group details ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, " ABDG groups from the DB ", abdgGroups);
        return abdgGroups;
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsAbdgGroupList(String mdn, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsAbdgGroupList(String , String, KnPersisterTxn)";
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSubsAbdgGroupList(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method to get All the group IDs of carporate account.
     * @param corpId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Collection<Integer> getGroupIdList(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupIdList(int, String, KnPersisterTxn)";
        knLogger.debug(methodName," Entry -",corpId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Collection<Integer> groupIdList = corpXdmDao.selectGroupIdList(corpId, xdmsHome, persisterTxn);
            knLogger.debug(methodName," Retrieved group Ids are :",groupIdList);
            return groupIdList;
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving group Ids", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void deleteAllCorpGroups(Collection<Integer> groupIdList,String  xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteAllCorpGroups(groupIdList, persisterTxn)";
        knLogger.debug(methodName, " Groups to delete -",groupIdList);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            //delete group records from CORPGROUP_LISTREF table.
            corpXdmDao.deleteAllGroupsSublistRef(groupIdList, persisterTxn);
            //delete group records from CORPGROUPMEMBERCOUNT table.
            corpXdmDao.deleteAllGroupsCorpGroupMemberCountEntry(groupIdList, persisterTxn);
            //delete group records from CORPGROUPINFO tabel.
            corpXdmDao.deleteAllGroups(groupIdList,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting all groups of carporate account- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName," Group records deleted successfully ");
    }

    public List<String> getFirstNetFanIdsByFanIds(List<String> fanIds, String xdmsHomePttId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getFirstNetFanIdsByFanIds(fanIds, persisterTxn, readOnly);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Validates the list of FAN IDs for the specified corporation ID.
     *
     * @param corpId the ID of the corporation
     * @param ownerIdList the list of owner FAN IDs to validate
     * @throws KnCorpBOException if a business operation error occurs during validation
     */
    public void validateFanIds(int corpId, List<String> ownerIdList, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "validateFanIds(int ,List<String>,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY, corpId :", corpId, "ownerIdList :", ownerIdList);
        try {
            Collection<Integer> idListInRequest = ownerIdList.stream().map(Integer::parseInt).collect(Collectors.toList());
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Collection<Integer> idListFromDB = corpXdmDao.getFanList(corpId, idListInRequest, persisterTxn);
            idListInRequest.removeAll(idListFromDB);
            if (!idListInRequest.isEmpty()) {
                knLogger.error(methodName, "Context ids does not belong to owner corporate." + idListInRequest);
                throw new KnCorpBOValidationException(INCONTEXT_ID_NOT_IN_CORP, "InContextId does not belong to Corporation "  + idListInRequest, "", "", "", "DataType", "");
            }
        } catch (KnDAOException | KnCorpBOValidationException e) {
            knLogger.error(methodName, "Exception occured while validating FAN's-  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.info(methodName, "Validation successful!");
    }

    public Map<Integer, Integer> getIdValueGroupIdMap(List<Integer> groupIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getIdValueGroupIdMap(groupId, Collection<Integer>, int, pttServerId, boolean, persisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupId - ", groupIds);
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        Map<Integer, Integer> idValueGroupMap = xdmDAO.getIdValueGroupIdMap(groupIds, xdmsHome, readOnly, persisterTxn);
        return idValueGroupMap;
    }

    public boolean isOwner(Map<Integer, Integer> idValInfoMap, List<Integer> inContextIds) {
        return inContextIds.stream().anyMatch(fanId ->
                (idValInfoMap.containsKey(fanId) && (idValInfoMap.get(fanId) == Integer.parseInt(OWNER_FAN_TYPE)||
                        idValInfoMap.get(fanId) == Integer.parseInt(OWNER_BAN_TYPE))));
    }

    public Map<String, Integer> getOwnerAndSharedContextIdByGroupIds(List<Integer> groupIds, String idType, String xdmsHome,
                                                                     boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getOwnerAndSharedContextIdByGroupIds(groupIds, idType, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error("getOwnerAndSharedContextIdByGroupIds", "KnDAOException occured  - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    /**
     * Method to return the Group Hierarchy Map for given list of group Ids
     * @param groupIds
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getGroupHierarchyMap(List<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupHierarchyMap()";
        knLogger.debug(methodName, "ENTRY Point : groupId - ", groupIds);
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        return xdmDAO.getGroupHierarchyMap(groupIds, xdmsHome, persisterTxn);
    }

    /**
     * Method to identify is the request received id from Group's owner or shared based on incontext and group hierarchy map.
     * @param idListValues
     * @param idType
     * @param groupHirarchyMap
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean isOwnerRequest(List<Integer> idListValues, int idType, Map<Integer, Integer> groupHirarchyMap,
                                  String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "isOwnerRequest()";
        knLogger.debug(methodName, "Entry - ", idListValues, idType, groupHirarchyMap);
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        List<KnIdDetailsDTO> banDetails = xdmDAO.getBanDetailsByBanFanId(idListValues, idType, persisterTxn);
        knLogger.debug(methodName, "banDetails - ", banDetails);
        boolean isOwner = false;
        for (KnIdDetailsDTO banFanInfo : banDetails) {
            if ((groupHirarchyMap.containsKey(banFanInfo.getParentId()) && groupHirarchyMap.get(banFanInfo.getParentId()) == Integer.parseInt(OWNER_FAN_TYPE)) ||
                    ((!groupHirarchyMap.containsKey(banFanInfo.getParentId())) &&
                            (groupHirarchyMap.containsKey(banFanInfo.getIdKey()) && groupHirarchyMap.get(banFanInfo.getIdKey()) == Integer.parseInt(OWNER_BAN_TYPE)))) {
                isOwner = true;
                break;
            }
        }
        return isOwner;
    }

    /**
     * Method to return the incontext mdn list from idList and Fan-BAN info
     * @param mdnFanBanInfo
     * @param idListValues
     * @param idType
     * @return
     */
    public List<String> getIncontextMemList(Map<String, Map<String, Integer>> mdnFanBanInfo, List<Integer> idListValues, int idType) {
        final String methodName = "getIncontextMemList()";
        List<String> inContextMdnList = new ArrayList<>();
        mdnFanBanInfo.forEach((mdn, banFanMap) -> {
            if ((Integer.parseInt(BAN_TYPE) == idType) &&
                    (idListValues.contains(banFanMap.get(com.kodiak.common.resources.KnConstants.BAN_ID)))) {
                inContextMdnList.add(mdn);
            } else if ((Integer.parseInt(FAN_TYPE) == idType) &&
                    (idListValues.contains(banFanMap.get(com.kodiak.common.resources.KnConstants.FAN_ID)))) {
                inContextMdnList.add(mdn);
            }
        });
        knLogger.debug(methodName, "incontext mdn list - ", inContextMdnList);
        return inContextMdnList;
    }

    /**
     * Returns MDNs whose subscriber HIERARCHY_ID matches one of the requesting hierarchy IDs.
     * Used for hierarchy-based (idType=3) member filtering: shared hierarchy sees only its own subscribers.
     */
    public List<String> getIncontextMemListByHierarchy(Map<String, String> mdnHierarchyInfo, List<String> requestingHierarchyIds) {
        final String methodName = "getIncontextMemListByHierarchy()";
        Set<String> requestingHierarchySet = new HashSet<>();
        for (String id : requestingHierarchyIds) {
            if (id != null) requestingHierarchySet.add(id.trim());
        }
        List<String> inContextMdnList = new ArrayList<>();
        mdnHierarchyInfo.forEach((mdn, hierarchyId) -> {
            if (hierarchyId != null && !hierarchyId.isEmpty() && requestingHierarchySet.contains(hierarchyId)) {
                inContextMdnList.add(mdn);
            }
        });
        knLogger.debug(methodName, "incontext mdn list by hierarchy - ", inContextMdnList);
        return inContextMdnList;
    }

    public Map<Integer,KnCorpGroupInfoPersistDTO> getBulkGroupBasicInfoDetailsWithoutCorpId(List<Integer> groupIds, int clientType,
                                                                                            String xdmsHome,
                                                                                            KnPersisterTxn persisterTxn,
                                                                                            Boolean hiearchyCall)
            throws KnCorpBOException {
        String methodName = "getGroupBasicInfoDetails(KnIPCorpGroupInfoDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            var groupPersistDTO = xdmDAO.getBulkGroupBasicInfoDetailsWithoutCorpId(groupIds, clientType, persisterTxn,hiearchyCall);
            return groupPersistDTO;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group info details- ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Group Doesn't exist. Rethrowing Exception - ", e);
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, KnCorpGroupInfoPersistDTO> getBulkGroupBasicInfoDetails(Map<Integer, Integer> groupCorpMap,
                                                                                int clientType, String xdmsHome,
                                                                                KnPersisterTxn persisterTxn,
                                                                                Boolean hiearchyCall)
            throws KnCorpBOException {
        String methodName = "getGroupBasicInfoDetails(KnIPCorpGroupInfoDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            var groupPersistDTOs = xdmDAO.selectBulkGroupBasicInfo(groupCorpMap, clientType, persisterTxn,hiearchyCall);
            return groupPersistDTOs;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group info details- ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Group Doesn't exist. Rethrowing Exception - ", e);
                throw new KnCorpBOException(KnErrorCodes.
                        BOEntity.GROUP_DOES_NOT_EXIST, "Group Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifyBulkGroupDetails(KnIPCorpBulkGroupDTO bulkGroupInfo,
                                       String pttServerId,
                                       KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "modifyBulkGroupDetails(KnIPCorpBulkGroupDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : bulkGroupInfo",bulkGroupInfo);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            //update avatar
            var tempGrpIdvsDisplayName = new HashMap<Integer, String>();
            var tempGrpIdvsIsGroupTypeChanged = new HashMap<Integer, Integer>();
            var tempGrpIdvsOverrideDnd = new HashMap<Integer, Integer>();
            var tempGrpIdvsAvatar = new HashMap<Integer, Integer>();
            var tempGrpIdvsOSMListId = new HashMap<Integer, KnIPCorpGroupInfoDTO>();
            var tempGrpIdvsIsLmrInteropFeatureChanged = new HashMap<Integer, Integer>();
            var tempGrpIdvsUgwInterop= new HashMap<Integer, Integer>();
            var tempGrpIdvsRecordingFs = new HashMap<Integer, String>();
            var tempGrpIdvsGrpShared = new HashMap<Integer, Integer>();
            for(var entry: bulkGroupInfo.getIpCorpGroupInfoDTOMap().entrySet()) {
                var groupId = entry.getKey();
                var groupInfoDTO = entry.getValue();
                var displayName = groupInfoDTO.getGroupDisplayName();
                var isGroupTypeChanged = groupInfoDTO.isGroupTypeChanged();
                var overrideDnd = groupInfoDTO.getOverrideDnd();
                var avatar = groupInfoDTO.getAvatar();
                var oSMListId = groupInfoDTO.getOSMListId();
                var isLmrInteropFeatureChanged = groupInfoDTO.isLmrInteropFeatureChanged();
                var ugwInterop = groupInfoDTO.getUgwInterop();
                var recordingFs = groupInfoDTO.getRecordingFs();
                var grpShared = groupInfoDTO.getGrpShared();
                if (displayName != null) {
                    tempGrpIdvsDisplayName.put(groupId, displayName);
                }
                if (isGroupTypeChanged) {
                    tempGrpIdvsIsGroupTypeChanged.put(groupId, groupInfoDTO.getGroupType());
                }
                if (overrideDnd != KnConstants.OVERRIDE_DND_NOT_MODIFIED) {
                    tempGrpIdvsOverrideDnd.put(groupId, overrideDnd);
                }
                if (avatar != null && avatar != KnConstants.AVATAR_NOT_MODIFIED) {
                    tempGrpIdvsAvatar.put(groupInfoDTO.getGroupId(), avatar);
                }
                if(oSMListId !=null) {
                    tempGrpIdvsOSMListId.put(groupId, groupInfoDTO);
                }
                if (isLmrInteropFeatureChanged) {
                    tempGrpIdvsIsLmrInteropFeatureChanged.put(groupId, groupInfoDTO.getLmrInteropCapable());
                }
                if (null != ugwInterop && (ugwInterop.equals(7) || ugwInterop.equals(0))) {
                    tempGrpIdvsUgwInterop.put(groupId, ugwInterop);
                    // modifyGroupUGWParameter(ugwInterop, groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
                }
                if (null != recordingFs) {
                    tempGrpIdvsRecordingFs.put(groupId, recordingFs);
                    //modifyGroupRecordingFsParameter(Integer.parseInt(recordingFs), groupInfoDTO.getGroupId(), pttServerId, persisterTxn);
                }
                // modifyGroupEmergAttributes(groupInfoDTO, pttServerId, persisterTxn);
                if(grpShared != null){
                    tempGrpIdvsGrpShared.put(groupInfoDTO.getGroupId(), grpShared);
                    //corpXdmDao.modifyGroup_GrpSharedFlag(groupIdSharedFlagMap, pttServerId, persisterTxn);
                }
            }

            if (!tempGrpIdvsDisplayName.isEmpty())
                modifyBulkGroupName(tempGrpIdvsDisplayName, pttServerId, persisterTxn);
            if (!tempGrpIdvsIsGroupTypeChanged.isEmpty())
                updateBulkGroupType(tempGrpIdvsIsGroupTypeChanged, pttServerId, persisterTxn);
            if (!tempGrpIdvsOverrideDnd.isEmpty())
                modifyBulkGroupOverrdeDND(tempGrpIdvsOverrideDnd, pttServerId, persisterTxn);
            if (!tempGrpIdvsAvatar.isEmpty())
                modifyBulkGroupAvatar(tempGrpIdvsAvatar, pttServerId, persisterTxn);
            if (!tempGrpIdvsOSMListId.isEmpty())
                corpXdmDao.modifyBulkGroupOSMListId(tempGrpIdvsOSMListId, persisterTxn);
            if(!tempGrpIdvsIsLmrInteropFeatureChanged.isEmpty())
                modifyBulkGroupLmrInteropCapable(tempGrpIdvsIsLmrInteropFeatureChanged, pttServerId, persisterTxn);
            if(!tempGrpIdvsUgwInterop.isEmpty())
                modifyBulkGroupUGWParameter(tempGrpIdvsUgwInterop, pttServerId, persisterTxn);
            if(!tempGrpIdvsRecordingFs.isEmpty())
                modifyBulkGroupRecordingFsParameter(tempGrpIdvsUgwInterop, pttServerId, persisterTxn);

            modifyBulkGroupEmergAttributes(bulkGroupInfo.getIpCorpGroupInfoDTOMap(), pttServerId, persisterTxn);


            var subscribersList = new ArrayList<KnCorpSubscriberDTO>();// Added contacts to private list
            var tempGroupIdVsRemovedMdnsList = new HashMap<Integer, List<String>>(); // Remove contacts from private list
            var tempGroupIdVsAddedSublistIds = new HashMap<Integer, List<Integer>>(); // Add sublists to the group
            var tempGroupIdVsRemoveSublistIds = new HashMap<Integer, List<Integer>>(); // Delete sublist from the Group
            var tempGroupIdVsGroupPrivateList = new HashMap<Integer, Integer>();

            for (var entry : bulkGroupInfo.getIpCorpGroupInfoDTOMap().entrySet()) {
                var groupId = entry.getKey();
                var groupInfoDTO = entry.getValue();
                var groupMembers = groupInfoDTO.getGroupMembers();
                tempGroupIdVsGroupPrivateList.put(groupId,groupInfoDTO.getGroupMemberListId());

                for (KnCorpGroupMemberDTO grpMember : KnGeneralUtil.emptyIfNull(groupMembers)) {
                    var subsc = new KnCorpSubscriberDTO();
                    subsc.setMdn(grpMember.getMdn());
                    subsc.setCorpId(grpMember.getCorpId());
                    subsc.setGrpSublistId(groupInfoDTO.getGroupMemberListId());
                    subscribersList.add(subsc);
                }

                List<String> removedMdnsList = groupInfoDTO.getRemovedMemberMdns();
                if (removedMdnsList != null && !removedMdnsList.isEmpty()) {
                    tempGroupIdVsRemovedMdnsList.put(groupId,removedMdnsList);
                    //corpXdmDao.deleteSubscPrivateContactList(removedMdnsList, groupPrivateList, persisterTxn);
                }

                // Add sublists to the group
                Collection<Integer> addedSublistIds = groupInfoDTO.getAddedSublistIds();
                if (addedSublistIds != null && !addedSublistIds.isEmpty()) {
                    tempGroupIdVsAddedSublistIds.put(groupId,new ArrayList<>(addedSublistIds));
                    //addSublistsToGroup(groupInfoDTO.getGroupId(), groupInfoDTO.getAddedSublistIds(), pttServerId, persisterTxn);
                }
                // Delete sublist from the Group
                Collection<Integer> removeSublistIds = groupInfoDTO.getRemovedSublistIds();
                if (removeSublistIds != null && !removeSublistIds.isEmpty()) {
                    tempGroupIdVsRemoveSublistIds.put(groupId,new ArrayList<>(removeSublistIds));
                    // removeSublistListsMappingFromGroup(removeSublistIds, groupInfoDTO.getGroupId(), pttServerId
                    //       , persisterTxn);
                }

            }

            if (!subscribersList.isEmpty()) {
                corpXdmDao.addPrivateContactList(subscribersList, persisterTxn);
            }
            if (!tempGroupIdVsRemovedMdnsList.isEmpty())
                corpXdmDao.deleteBulkSubscPrivateContactList(tempGroupIdVsRemovedMdnsList, tempGroupIdVsGroupPrivateList, persisterTxn);
            // Remove contacts from private list
          /*  Collection<String> removedMdnsList = groupInfoDTO.getRemovedMemberMdns();
            if (removedMdnsList != null && !removedMdnsList.isEmpty()) {
                corpXdmDao.deleteSubscPrivateContactList(removedMdnsList, groupPrivateList, persisterTxn);
            }*/
            // Add sublists to the group
            /*Collection<Integer> addedSublistIds = groupInfoDTO.getAddedSublistIds();
            if (addedSublistIds != null && !addedSublistIds.isEmpty()) {
                addSublistsToGroup(groupInfoDTO.getGroupId(), groupInfoDTO.getAddedSublistIds(), pttServerId, persisterTxn);
            }*/
            // Delete sublist from the Group
          /*  Collection<Integer> removeSublistIds = groupInfoDTO.getRemovedSublistIds();
            if (removeSublistIds != null && !removeSublistIds.isEmpty()) {
                removeSublistListsMappingFromGroup(removeSublistIds, groupInfoDTO.getGroupId(), pttServerId
                        , persisterTxn);
            }*/
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying group details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a map of GroupId VS List of DTO of group member MDN and its is_supervisor and LocWatcher value.
     *
     * @param groupIds
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer,Collection<KnCorpGroupMemberDTO>> getCorpGroupMembersListMap(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMembersListMap(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIds - " , groupIds);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        var returnMap = new HashMap<Integer, Collection<KnCorpGroupMemberDTO>>();
        try {
            groupIds.forEach(e -> returnMap.put(e, new ArrayList<>()));

            var corpGroupMembersList = corpXdmDao.getCorpGroupMembersList(groupIds,false, persisterTxn);
            if (corpGroupMembersList != null && !corpGroupMembersList.isEmpty())
                corpGroupMembersList.forEach(e -> returnMap.get(e.getGroupId()).add(e));

        } catch (KnDAOException e) {
            throw e;
        }
        return returnMap;
    }
    public Map<String,Integer> getGroupCountByNameForGroupsName(Collection<String> groupNames, int corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupCountByNameForGroupsName(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupNames - ", groupNames, " , corpId - ", corpId);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getGroupCountByNameForGroupsName(groupNames, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieveing group count by group name - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public Set<Integer> getCorpGroupByOSMListIdMap(Map<Integer, Integer> groupOSMListIdMap,String xdmsHomePttId,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName="getCorpGroupByOSMListIdMap(Map<Integer, Integer>,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getCorpGroupByOSMListIdMap(groupOSMListIdMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }
    public Map<Integer, List<String>> getNewlyAddedMembersForGroupMap(Map<Integer,List<String>> requestMembersMap,
                                                                      String xdmsHome,
                                                                      KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getNewlyAddedMembersForGroup(Collection<String>, int, String, KnPersisterTxn)";

        if (requestMembersMap.isEmpty())
            return new HashMap<>();

        var memberToAddMap = new HashMap<Integer, List<String>>();
        knLogger.debug(methodName, "Entry Point : requestMembers - ", requestMembersMap == null ? requestMembersMap : KnGDPRTemplate.mdnLists(requestMembersMap.values()));
        var memberListMap = getGroupSubscriberDistList(new ArrayList<>(requestMembersMap.keySet()), xdmsHome, persisterTxn);
        for (var entry : requestMembersMap.entrySet()) {
            var groupId = entry.getKey();
            var requestMembers = entry.getValue();

            Collection<String> memberToAdd = new ArrayList<String>();
            for (String mdn : requestMembers) {
                if (!memberListMap.get(groupId).contains(mdn)) {
                    memberToAdd.add(mdn);
                }
            }
            memberToAddMap.put(groupId, new ArrayList<>(memberToAdd));
        }
        return memberToAddMap;
    }
    public void modifyBulkGroupName(Map<Integer, String> grpIdvsDisplayName, String pttServerId,
                                    KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifyGroupName(String, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            // add the members
            corpXdmDao.modifyBulkGroupName(grpIdvsDisplayName, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group name.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyBulkGroupAvatar(Map<Integer, Integer> tempGrpIdvsAvatar, String pttServerId,
                                      KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifyBulkGroupAvatar(Map, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            // add the members
            corpXdmDao.modifyBulkGroupAvatar(tempGrpIdvsAvatar, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group avatar.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void updateBulkGroupType(Map<Integer, Integer> grpIdvsIsGroupTypeChanged , String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateBulkGroupType(Map<Integer, Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : GrpIdvsIsGroupTypeChanged - ", grpIdvsIsGroupTypeChanged);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(pttServerId);
            xdmDAO.updateBulkGroupType(grpIdvsIsGroupTypeChanged, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    /**
     * This method returns a Map of group member MDN and its is_supervisor value
     *
     * @param groupIds
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Map<String, KnCorpGroupMemberDTO>> getBulkGroupMembersList(List<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getBulkGroupMembersList(List<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getBulkGroupMembersList(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw e;
        }
    }

    public Map<Integer, KnCorpGroupMemberDTO> getBulkGroupMembersListMap(List<Integer> groupIds, String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException {
        final String methodName = "getBulkGroupMembersList(List<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getBulkGroupMembersListMap(groupIds, mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyBulkGroupOverrdeDND(Map<Integer, Integer> groupIdVsOverrideDnd, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyBulkGroupOverrdeDND(groupIdVsOverrideDnd, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyBulkGroupLmrInteropCapable(Map<Integer, Integer> grpIdvsIsLmrInteropFeatureChangedMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyGroupLmrInteropCapable(int, int, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyBulkGroupLmrInteropCapable(grpIdvsIsLmrInteropFeatureChangedMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group lmrInteropCapable.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyBulkGroupUGWParameter(Map<Integer, Integer> groupIdVsUgwParameterMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyBulkGroupUGWParameter(Map<Integer, Integer>, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyBulkGroupUGWParameter(groupIdVsUgwParameterMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group ugwInterop.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyBulkGroupRecordingFsParameter(Map<Integer, Integer> groupIdVsRecordingFsParameterMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyBulkGroupRecordingFsParameter(Map<Integer, Integer>, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyBulkGroupRecordingFsParameter(groupIdVsRecordingFsParameterMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while modifying the group recordingFs.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void modifyBulkGroupEmergAttributes(Map<Integer, KnIPCorpGroupInfoDTO> groupInfoDTOMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "modifyBulkGroupEmergAttributes(Map<Integer, KnIPCorpGroupInfoDTO>, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.modifyBulkGroupEmergAttributes(groupInfoDTOMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group modifyGroupEmergAttributes.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void updateBulkGroupMemberListSupervisorList(Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap,
                                                        String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateBulkGroupMemberListSupervisorList(Map<Integer, List<KnCorpGroupMemberDTO>>, String)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateBulkGroupMemberListSupervisorList(supervisorMemberListMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateGroupMemberListSupervisorList- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getMaxLocWatchersCount(Collection<Integer> groupIds, String xdmsHomePttId,
                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getMaxLocWatchersCount()";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getMaxLocWatchersCount(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getGroupMemsCounts(Collection<Integer> groupIds, String pttServerId , KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getGroupMemsCounts(groupIds , persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getCorpIdfromGroupID(List<String> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpIdfromGroupID()";
        knLogger.debug(methodName, "ENTRY Point : groupId - ", groupIds);
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
        var groupList = KnGeneralUtil.splitList(groupIds, BULK_UPDATE_SIZE);
        Map<Integer, Integer> groupListMap = new HashMap<>();
        for (var groupLists : groupList) {
            var map = xdmDAO.getCorpIdfromGroupID(groupLists, xdmsHome, persisterTxn);
            groupListMap.putAll(map);
        }
        return groupListMap;
    }

    public Map<Integer, Integer> getGroupCorpIdMap(List<KnCorpAddlTGInfoDTO> subsAddlTGList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
        if (null != subsAddlTGList) {
            List<String> corpGroupIds = subsAddlTGList.stream()
                    .map(group -> Integer.toString(group.getGroupId()))
                    .collect(Collectors.toSet()).stream().collect(Collectors.toList());
            groupCorpIdMap = getCorpIdfromGroupID(corpGroupIds, xdmsHome, persisterTxn);
        }
        knLogger.debug("getGroupCorpIdMap", "groupCorpId - ", groupCorpIdMap);
        return groupCorpIdMap;
    }


    public Map<Integer, KnCorpGroupDTO> getGroupInfoList(int corpId, int gropIdList, String
            xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getGroupList(KnIPCorpInfoDTO, int, String, boolean, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getGroupInfoList(corpId, gropIdList, xdmsHome, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpAddlTGInfoDTO> getAddlTGList(Collection<KnXDMAddlTalkGroupInfoDTO> addedAddlTgList) {
        String methodName = "getAddlTGList()";
        Collection<KnCorpAddlTGInfoDTO> tgGroupInfoList = new ArrayList<>();
        if (addedAddlTgList != null && !addedAddlTgList.isEmpty()) {
            for (KnXDMAddlTalkGroupInfoDTO addlTG : addedAddlTgList) {
                KnCorpAddlTGInfoDTO tgGroupInfo = new KnCorpAddlTGInfoDTO();
                tgGroupInfo.setMdn(addlTG.getMdn());
                tgGroupInfo.setGroupId(addlTG.getGroupId());
                tgGroupInfo.setZoneId(addlTG.getZoneId());
                tgGroupInfo.setChannelId(addlTG.getChannelId());
                tgGroupInfoList.add(tgGroupInfo);
            }
        }
        return tgGroupInfoList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getModifylTGList(Collection<KnXDMAddlTalkGroupInfoDTO> modifiedAddlTgList) {
        String methodName = "getAddlTGList()";
        Collection<KnCorpAddlTGInfoDTO> tgGroupInfoList = new ArrayList<>();
        if (modifiedAddlTgList != null && !modifiedAddlTgList.isEmpty()) {
            for (KnXDMAddlTalkGroupInfoDTO addlTG : modifiedAddlTgList) {
                KnCorpAddlTGInfoDTO tgGroupInfo = new KnCorpAddlTGInfoDTO();
                tgGroupInfo.setMdn(addlTG.getMdn());
                tgGroupInfo.setGroupId(addlTG.getGroupId());
                tgGroupInfo.setZoneId(addlTG.getZoneId());
                tgGroupInfo.setChannelId(addlTG.getChannelId());
                tgGroupInfoList.add(tgGroupInfo);
            }
        }
        return tgGroupInfoList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getRemovelTGList(Collection<KnXDMAddlTalkGroupInfoDTO> removedAddlTgList) {
        String methodName = "getAddlTGList()";
        Collection<KnCorpAddlTGInfoDTO> tgGroupInfoList = new ArrayList<>();
        if (removedAddlTgList != null && !removedAddlTgList.isEmpty()) {
            for (KnXDMAddlTalkGroupInfoDTO addlTG : removedAddlTgList) {
                KnCorpAddlTGInfoDTO tgGroupInfo = new KnCorpAddlTGInfoDTO();
                tgGroupInfo.setMdn(addlTG.getMdn());
                tgGroupInfo.setGroupId(addlTG.getGroupId());
                tgGroupInfo.setZoneId(addlTG.getZoneId());
                tgGroupInfo.setChannelId(addlTG.getChannelId());
                tgGroupInfoList.add(tgGroupInfo);
            }
        }
        return tgGroupInfoList;
    }

    public void updateAddlTalkGroupImpactedTable(String pttServerId, Collection<String> mdnList, KnPersisterTxn persisterTxn,
                                                 Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateAddlTalkGroupImpactedTables2(String, String, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            Map<String, KnOPDocChgDTO> addlTGMap = new HashMap<>();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            if (!mdnList.isEmpty()) {
                addlTGMap = corpXdmDao.insertOrUpdateAddlTGInfo(mdnList, persisterTxn);
            }
            knLogger.debug(methodName, " addlTGMap :", KnGDPRTemplate.mapKeyMdn(addlTGMap));
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getSubscriberBroadcastGroupCount(List<String> mdns, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberBroadcastGroupCount(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubscriberBroadcastGroupCount(mdns, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving the broadcast group count",
                    e);
            throw e;
        }
    }

    public Set<String> getGroupMemsList(Collection<Integer> groupIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getGroupMemsList(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public Integer getTotalMemberCountFromGroupList(List<Integer> groupIds,boolean readOnly, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getTotalMemberCountFromGroupList(List<Integer>,boolean, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getTotalMemberCountFromGroupList(groupIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getTotalMemberCountFromGroupList",
                    e);
            throw e;
        }
    }

    public Map<String, Integer> getPaginatedSubscriberData(Integer groupId , Integer firstIndex, Integer lastIndex, boolean readOnly,String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPaginatedSubscriberData(Integer, Integer, Integer, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getPaginatedSubscriberData(groupId,firstIndex,lastIndex,readOnly,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getPaginatedSubscriberData",
                    e);
            throw e;
        }
    }

    public Integer getGroupIdBasedonRowNumber(List<String> mdnList,int rowNumber, boolean readOnly,String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupIdBasedonRowNumber(List<String>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupIdBasedonRowNumber(mdnList,rowNumber,readOnly,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getPaginatedSubscriberData",
                    e);
            throw e;
        }
    }

    public Map<String,KnCorpGroupMemberDTO> getGroupSubsBasicInfo(List<Integer> groupIdList, List<String> mdnList,String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupIdBasedonRowNumber(List<String>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getGroupSubsBasicInfo(groupIdList,mdnList,readOnly,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getPaginatedSubscriberData",
                    e);
            throw e;
        }
    }

    public void updateSubsOsmAuthorizeInAllGroups(String mdn,String isOSMAuthorize,String xdmsHomePttId,
                                    KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateSubsOsmAuthorizeInAllGroups()";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            corpXdmDao.updateSubsOsmAuthorizeInAllGroups(mdn,isOSMAuthorize, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.warn(methodName, "KnDAOException occured while updating the groups etag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a list of MCX group ids filtering from the given group ids.
     *
     * @param groupIds
     * @param pttServerId
     * @param persisterTxn
     * @return List of MCX group ids
     * @throws KnCorpBOException
     */
    public List<Integer> getMcxGroups(List<Integer> groupIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getMcxGroups(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Assigns zones and channels to groups for subscribers based on the new requirements.
     * @param groupIds         A list of group IDs to be assigned
     * @param mdnClientTypeMap Map of MDN to client type for all subscribers to be assigned
     * @param extCorpId        The external corporate ID associated with the subscriber.
     * @param pttServerId      The push to talk server id
     * @param persisterTxn     The transaction object used for database operations.
     * @return Map of MDN to assignment status (true if assigned successfully, false otherwise)
     * @throws KnDAOException  If a data access error occurs during processing.
     */
    public Map<String, Boolean> assignZonesAndChannels_V2(List<Integer> groupIds, Map<String, Integer> mdnClientTypeMap,
                                                           String extCorpId, String pttServerId, KnPersisterTxn persisterTxn, Set<String> extMdnSet) throws KnDAOException {

        final String methodName = "assignZonesAndChannels_V2()";
        knLogger.debug(methodName, "Entry, groupIds:", groupIds, " mdnClientTypeMap:", mdnClientTypeMap);
        Map<String, Boolean> mdnResults = new HashMap<>();

        try {
            // Validate input parameters
            if (groupIds == null || groupIds.isEmpty() || mdnClientTypeMap == null || mdnClientTypeMap.isEmpty()) {
                knLogger.info(methodName, "Skipping zone/channel assignment. Reason: No groups or MDNs provided.");
                // Mark all MDNs as not assigned
                for (String mdn : mdnClientTypeMap.keySet()) {
                    mdnResults.put(mdn, false);
                }
                return mdnResults;
            }

            // Validate client types
            List<Integer> validClientTypes = Arrays.asList(
                SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value(),
                SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value(),
                SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
            );

            // Process each MDN
            for (Map.Entry<String, Integer> entry : mdnClientTypeMap.entrySet()) {
                String mdn = entry.getKey();
                Integer clientType = entry.getValue();
                boolean assigned = false;

                try {
                    // Validate client type
                    if (!validClientTypes.contains(clientType)) {
                        knLogger.info(methodName, "Skipping zone/channel assignment for MDN. Reason: Invalid client type.", mdn, clientType);
                        mdnResults.put(mdn, false);
                        continue;
                    }

                    // External user check
                    if(extMdnSet != null && !extMdnSet.isEmpty()){
                        if(extMdnSet.contains(mdn)){
                            knLogger.info(methodName, "Skipping zone/channel assignment for MDN. Reason: External User.", mdn);
                            mdnResults.put(mdn, false);
                            continue;
                        }
                    }

                    // Process auto-assignment for this MDN
                    Map<Integer, Boolean> assignmentResults = processAutoAssignZoneAndChannel_V2(
                        new ArrayList<>(groupIds), mdn, extCorpId, pttServerId, persisterTxn
                    );

                    // Check if all group IDs were assigned successfully for this MDN
                    assigned = groupIds.stream().allMatch(id -> assignmentResults.getOrDefault(id, false));
                    mdnResults.put(mdn, assigned);

                    if (assigned) {
                        knLogger.info(methodName, "Successfully assigned zones/channels for MDN:", mdn);
                    } else {
                        knLogger.warn(methodName, "Failed to assign all zones/channels for MDN:", mdn, " assignmentResults:", assignmentResults);
                    }

                } catch (KnDAOException e) {
                    knLogger.error(methodName, "Error while assigning zones and channels for MDN:", mdn, e);
                    mdnResults.put(mdn, false);
                }
            }

            knLogger.debug(methodName, "Exit. Final mdnResults:", mdnResults);
            if(mdnResults != null && !mdnResults.isEmpty()){
            long successCount = mdnResults.values().stream()
                    .filter(Boolean::booleanValue)
                    .count();

            if (successCount > 0) {
                List<String> successfulMdns = mdnResults.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());


                if (successfulMdns != null &&!successfulMdns.isEmpty()) {
                    updateAutoAssignTracker(successfulMdns, AUTO_ASSIGN_TRACKER.AUTO_ASSIGNED.value(),
                            persisterTxn, pttServerId);
                }
            }}

            return mdnResults;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected error during zone/channel assignment:", e);
            // Mark any unprocessed MDNs as failed before returning
            if (mdnClientTypeMap != null && !mdnClientTypeMap.isEmpty()) {
                for (String mdn : mdnClientTypeMap.keySet()) {
                    // Only add if not already processed (preserves partial success)
                    mdnResults.putIfAbsent(mdn, false);
                }
            }
            knLogger.error(methodName, "Returning partial results due to exception. Processed: " +
                    mdnResults.size() + "/" + (mdnClientTypeMap != null ? mdnClientTypeMap.size() : 0) );
            // Return partial results instead of throwing - allows caller to see what succeeded
            return mdnResults;
        }
    }

    public void updateAutoAssignTracker(List<String> mdns, Integer trackerValueToUpdate,
                                        KnPersisterTxn persisterTxn, String pttServerId) throws KnCorpBOException {
        final String methodName = "updateAutoAssignTracker()";

        try {
            KnXDMSubscriberInfoDAO subscriberInfoDAO = new KnXDMSubscriberInfoDAO(pttServerId);
            if (mdns != null && !mdns.isEmpty()) {
                // Get existing tracker values for all MDNs in one call
                Map<String, String> existingTrackerMap = subscriberInfoDAO.getAutoAssignTrackerForMdns(mdns, persisterTxn);
                List<String> mdnsToUpdate = new ArrayList<>();

                if (existingTrackerMap != null && !existingTrackerMap.isEmpty()) {

                    mdnsToUpdate = mdns.stream()
                            .filter(mdn -> {
                                String trackerValue = existingTrackerMap.get(mdn);
                                // Only update if tracker is null, unassigned, or currently auto-assigned but we want to change it to a manual assignment
                                return (trackerValue == null ||  String.valueOf(AUTO_ASSIGN_TRACKER.UNASSIGNED.value()).equals(trackerValue) || (String.valueOf(AUTO_ASSIGN_TRACKER.AUTO_ASSIGNED.value()).equals(trackerValue) && !trackerValueToUpdate.equals(AUTO_ASSIGN_TRACKER.AUTO_ASSIGNED.value())));
                            })
                            .collect(Collectors.toList());
                }
                if (mdnsToUpdate != null && !mdnsToUpdate.isEmpty()) {
                    subscriberInfoDAO.updateAutoAssignTrackerBulk(mdnsToUpdate, trackerValueToUpdate, persisterTxn);
                    knLogger.info(methodName, "Updated AUTO_ASSIGN_TRACKER for ", mdnsToUpdate.size(), " MDNs (out of ", mdns.size(), " successful assignments)");

                    int skippedCount = mdns.size() - mdnsToUpdate.size();
                    if (skippedCount > 0) {
                        knLogger.info(methodName, "Skipped ", skippedCount, " MDNs that already have AUTO_ASSIGN_TRACKER value set (1=auto-assigned or 2=manually-assigned)");
                    }
                } else {
                    knLogger.info(methodName, "All ", mdns.size(), " successful MDNs already have AUTO_ASSIGN_TRACKER value set, skipping update");
                }

                }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updateAutoAssignTrackerBulk", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }
                    }


    /**
     * Processes the auto-assignment of zones and channels for groups based on the new requirements.
     *
     * @param groupIds     A list of group IDs to be assigned.
     * @param mdn          The mobile directory number (MDN) of the subscriber.
     * @param extCorpId    The external corporate ID associated with the subscriber.
     * @param pttServerId  The push to talk server id
     * @param persisterTxn The transaction object used for database operations.
     * @throws KnDAOException    If a data access error occurs during processing.
     * @throws KnCorpBOException if a business operation error occurs during validation
     */

    private Map<Integer, Boolean> processAutoAssignZoneAndChannel_V2(List<Integer> groupIds, String mdn, String extCorpId, String pttServerId,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "processAutoAssignZoneAndChannel_V2";
        knLogger.debug(methodName, "ENTRY Point : -");
        knLogger.info(methodName, "Started auto-assign for MDN, groupIds, extCorpId: ", mdn, groupIds, extCorpId);
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(pttServerId);

        Map<String, List<Map<Integer, String>>> groupInfoMap = xdmServerDAO.getGroupInfo(groupIds, persisterTxn);
        knLogger.debug(methodName, "Fetched groupInfoMap: ", groupInfoMap);

        Map<String, Integer> zoneAndChannelConfigValues = xdmServerDAO.getZoneAndChannerConfigValues(extCorpId, persisterTxn);
        knLogger.debug(methodName, "zoneAndChannelConfigValues: ", zoneAndChannelConfigValues);

        String deviceInfo = xdmServerDAO.getDeviceInfo(mdn, persisterTxn);
        if (deviceInfo != null && deviceInfo.contains("{")) {
            int noOfChannelsPerZone = Integer.parseInt(extractValue(deviceInfo, "NoOfChannelsPerZone"));
            boolean isMultiZone = Boolean.parseBoolean(extractValue(deviceInfo, "IsMultiZone"));
            knLogger.debug(methodName, "DeviceInfo found. noOfChannelsPerZone, isMultiZone: ", noOfChannelsPerZone, isMultiZone);
            if (!isMultiZone) {
                zoneAndChannelConfigValues.put("MAXZONES", ZONE_TYPE.SINGLE_ZONE.value());
                zoneAndChannelConfigValues.put("MAXCHANNELSPERZONE", noOfChannelsPerZone);
            }
        }

        // Prepare assignment variables
        List<String> groupTypeOrder = Arrays.asList(TG_GROUP_TYPE.DISPATCHER_GROUP.value(), TG_GROUP_TYPE.STD_GROUP.value(), TG_GROUP_TYPE.BCG_GROUP.value());
        int numZones = zoneAndChannelConfigValues.get("MAXZONES");
        int numChannels = zoneAndChannelConfigValues.get("MAXCHANNELSPERZONE");
        List<KnAddlTGInfoDTO> addlTGInfoDTOS = new ArrayList<>();
        Set<String> usedZoneChannelPairs = new HashSet<>();
        Map<Integer, Boolean> assignmentResults = new HashMap<>();

        knLogger.debug(methodName, "Assignment loop: numZones, numChannels: ", numZones, numChannels);

        // Fetch existing assignments from DB for MDN
        List<KnAddlTGInfoDTO> existingAssignments = xdmServerDAO.getSubsAddlTGList(mdn, persisterTxn);

        for (KnAddlTGInfoDTO dto : existingAssignments) {
            usedZoneChannelPairs.add(dto.getZoneId() + "-" + dto.getChannelId());
        }
        knLogger.debug(methodName, "MDN, Existing zone-channel pairs: ", mdn, usedZoneChannelPairs.size());

        // Assignment logic
        for (String groupType : groupTypeOrder) {
            if (!groupInfoMap.containsKey(groupType)) continue;
            List<Map<Integer, String>> groupDetails = groupInfoMap.get(groupType);
            knLogger.debug(methodName, "Processing groupType, groupDetails: ", groupType, groupDetails);
            Map<Integer, String> filteredGroupMap = new HashMap<>();
            for (Map<Integer, String> groupDetail : groupDetails) {
                filteredGroupMap.putAll(groupDetail);
            }

            // handling Broadcaster Groups
            if (groupType.equals(TG_GROUP_TYPE.BCG_GROUP.value())) {
                List<Integer> allCorpGroupIds = new ArrayList<>(filteredGroupMap.keySet());
                if (allCorpGroupIds.isEmpty()) {
                    knLogger.debug(methodName, "No Broadcaster Group IDs found for group type 2");
                    continue;
                }
                Set<Integer> validBroadcasterGroups = new HashSet<>(xdmServerDAO.getBroadcasterGroupIds(mdn, persisterTxn));
                knLogger.debug(methodName, "MDN, Valid Broadcaster Groups: ", mdn, validBroadcasterGroups);
                filteredGroupMap.entrySet().removeIf(entry -> !validBroadcasterGroups.contains(entry.getKey()));
            }

            // Sort group names
            List<String> sortedGroupNames = new ArrayList<>(filteredGroupMap.values());
            Collections.sort(sortedGroupNames);
            Set<String> processedGroupNames = new HashSet<>();

            for (String groupName : sortedGroupNames) {
                if (processedGroupNames.contains(groupName)) {
                    continue;
                }
                processedGroupNames.add(groupName);

                // Get all group IDs for this group name
                List<Integer> corpGroupIds = filteredGroupMap.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(groupName))
                        .map(Map.Entry::getKey)
                        .distinct()
                        .collect(Collectors.toList());
                if (corpGroupIds.isEmpty()) continue;

                for (Integer corpGroupId : corpGroupIds) {
                    boolean assigned = false;
                    // Gap-filling logic: assign first available (zone, channel)
                    outer:
                    for (int z = 1; z <= numZones; z++) {
                        for (int c = 1; c <= numChannels; c++) {
                            String pairKey = z + "-" + c;
                            if (!usedZoneChannelPairs.contains(pairKey)) {
                                 KnAddlTGInfoDTO dto = new KnAddlTGInfoDTO();
                                dto.setGroupId(corpGroupId);
                                dto.setZoneId(z);
                                dto.setChannelId(c);
                                dto.setMdn(mdn);
                                addlTGInfoDTOS.add(dto);
                                usedZoneChannelPairs.add(pairKey);
                                assigned = true;
                                knLogger.info(methodName, "Assigned groupId, zone, channel,MDN: ", corpGroupId, z, c, mdn);
                                break outer;
                            }
                        }
                    }
                    assignmentResults.put(corpGroupId, assigned);
                    if (!assigned) {
                        knLogger.warn(methodName, "No available (zone, channel) pair for groupId: ", corpGroupId);
                    }
                }
            }
        }

        // Persist assignments if any
        if (!addlTGInfoDTOS.isEmpty()) {
            knLogger.info(methodName, "MDN, Persisting new assignments: ",  mdn, addlTGInfoDTOS.size());
            xdmServerDAO.insertSubsAddlTGList(addlTGInfoDTOS, persisterTxn);
        }
        knLogger.info(methodName, "Completed auto-assign for MDN, assignmentResults: ", mdn, assignmentResults);
        knLogger.debug(methodName, "EXIT Point : ");
        return assignmentResults;
    }

    private static String extractValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        int colonIndex = json.indexOf(":", keyIndex);
        int commaIndex = json.indexOf(",", colonIndex);
        int endIndex = commaIndex == -1 ? json.indexOf("}", colonIndex) : commaIndex;
        return json.substring(colonIndex + 1, endIndex).trim().replace("\"", "");
    }

    public Map<String, Object> getPocHomeAndClusterIdForCorpGroup(List<String> memberList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPocHomeAndClusterIdForCorpGroup";
        knLogger.debug(methodName, "ENTRY : memberList size - ", memberList != null ? memberList.size() : 0);
        Map<String, Object> resultMap = new HashMap<>();

        String selectedPocHome = null;
        Integer selectedClusterId = null;

        try {
            // 1. Fetch details for all members
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Map<String, KnCorpSubscriberDTO> memberDetailsMap =
                    corpXdmDao.getSubscriberDetails(memberList, false, persisterTxn);

            if (memberDetailsMap == null || memberDetailsMap.isEmpty()) {
                knLogger.warn(methodName, "No member details found.");
                resultMap.put(POCHOME, selectedPocHome);
                resultMap.put(CLUSTERID, selectedClusterId);
                return resultMap;
            }

            //If only 1 member, return its details immediately
            if (memberList.size() == 1) {
                KnCorpSubscriberDTO subscriber = memberDetailsMap.values().iterator().next();
                selectedPocHome = subscriber.getPocHome() != null ? subscriber.getPocHome().trim() : null;
                selectedClusterId = subscriber.getClusterId();

                if (selectedPocHome == null || selectedPocHome.isEmpty() || selectedPocHome.equals("0")) {
                    selectedPocHome = null;
                    selectedClusterId = null;
                }

                resultMap.put(POCHOME, selectedPocHome);
                resultMap.put(CLUSTERID, selectedClusterId);
                return resultMap;
            }

            // 2. Frequency Map for PocHome only
            Map<String, Integer> pocHomeFrequency = new HashMap<>();

            Map<String, Integer> pocHomeToClusterLookup = new HashMap<>();

            for (KnCorpSubscriberDTO sub : memberDetailsMap.values()) {
                String ph = sub.getPocHome() != null ? sub.getPocHome().trim() : null;
                Integer cid = sub.getClusterId();
                knLogger.info(methodName, "chekcing member details member MDN: " + sub.getMdn() + " PocHome: " + ph + " ClusterID: " + cid);
                if (ph != null && !ph.isEmpty() && !ph.equals("0")) {
                    pocHomeFrequency.put(ph, pocHomeFrequency.getOrDefault(ph, 0) + 1);
                    if (cid != null) {
                        pocHomeToClusterLookup.put(ph, cid);
                    }
                }
            }

            int maxCount = -1;

            for (Map.Entry<String, Integer> entry : pocHomeFrequency.entrySet()) {
                // "Strict Greater Than" ensures we keep the first one found in case of a tie
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    selectedPocHome = entry.getKey();
                }
            }

            if (selectedPocHome != null) {
                selectedClusterId = pocHomeToClusterLookup.get(selectedPocHome);
                if (selectedClusterId == null) {
                    knLogger.warn(methodName, "Found PocHome: " + selectedPocHome +
                            " but NO associated ClusterID found in lookup");
                }else{
                    knLogger.info(methodName, "Selected PocHome: " + selectedPocHome +
                            " (Count: " + maxCount + ")" +
                            " ClusterID: " + selectedClusterId);
                }
            } else {
                knLogger.warn(methodName, "No valid PocHomes found among members.");
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while calculating Pochome and clusterId - ", e);
            throw e;
        }

        resultMap.put(POCHOME, selectedPocHome);
        resultMap.put(CLUSTERID, selectedClusterId);

        return resultMap;
    }

    public String getSubscriberHierarchyId(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberHierarchyId";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Map<String, KnCorpSubscriberDTO> subscriberMap = corpXdmDao.getSubscriberDetails(
                    Collections.singletonList(mdn), true, persisterTxn);
            KnCorpSubscriberDTO subscriber = subscriberMap != null ? subscriberMap.get(mdn) : null;
            String hierarchyId = subscriber != null ? subscriber.getHierarchyId() : null;
            knLogger.debug(methodName, "EXIT : hierarchyId - ", hierarchyId);
            return hierarchyId;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving subscriber hierarchyId - ", e);
            throw e;
        }
    }

    public String fetchGroupPocHome(int corpGroupId, String pttServerId,
                                    KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updatePocHome(String, int, int,,String, KnPersisterTxn)";
        String pocHome = null;
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return pocHome = corpXdmDao.fetchGroupPocHome(corpGroupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group name.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<Integer> getGroupsWithNullPocHome(List<Integer> groupIds, String pttServerId,
                                                   KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGroupsWithNullPocHome(List<Integer>, String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getGroupsWithNullPocHome(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while fetching groups with null pocHome.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /** Inserts ID_TYPE=4 hierarchy map rows for hierarchy-scoped group sharing (Phase 6). */
    public void insertSharedGroupHierarchyMap(Integer groupId, List<String> hierarchyIds, List<Integer> corpIds,
                                              String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "insertSharedGroupHierarchyMap()";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertSharedGroupHierarchyMap(groupId, hierarchyIds, corpIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /** Returns all ID_TYPE=4 {ID_VALUE, CORPID} rows for a group (Phase 6). */
    public List<int[]> getSharedGroupHierarchyMappings(Integer groupId, String xdmsHome,
                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSharedGroupHierarchyMappings()";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSharedGroupHierarchyMappings(groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /** Counts CORPGRP_SHAREDLIST rows for the given corp pair used for TC-XDM-DTM-010 (Phase 6). */
    public int countActiveGroupsByCorpPair(Integer ownedCorpId, Integer sharedCorpId, String xdmsHome,
                                            KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "countActiveGroupsByCorpPair()";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.countActiveGroupsByCorpPair(ownedCorpId, sharedCorpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updatePocHome(String pocHome, int clusterId, int corpGroupId, String pttServerId,
                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updatePocHome(String, int, int,,String, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.updatePocHome(pocHome, clusterId, corpGroupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the group name.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String fetchClusterId(String pttServerId, String localClusterSiteId) throws KnConfigurationException, KnDAOException {
        final String methodName = "fetchClusterId()";
        knLogger.entry(methodName , "pttServerId: ", pttServerId, " localClusterSiteId: ", localClusterSiteId);
        KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
        String clusterId;
        KnPersisterTxn knPersisterTxn = KnPersisterTxn.getPersisterTxn();
        try {
            Map<String, String> clusterIdMap;
            ICacheManager cacheManager = configManager.getCacheManager();
            // Attempt to retrieve the cluster ID map from the cache
            clusterIdMap = (Map<String, String>) cacheManager.get(KnCacheKeys.CLUSTER_ID_MAP);
            // If the cache is empty, fetch the cluster ID map from the database and update the cache
            if (clusterIdMap == null || clusterIdMap.isEmpty()) {
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(localClusterSiteId);
                clusterIdMap = xdmServerDAO.getClusterId(knPersisterTxn);
                cacheManager.put(KnCacheKeys.CLUSTER_ID_MAP, clusterIdMap);
            }
            // Retrieve the cluster ID for the given PTT server ID, or use the default from the environment variable
            clusterId = clusterIdMap.get(pttServerId);
            knLogger.exit(methodName, "Cluster ID fetched for PTT Server ID ", pttServerId, " is: ", clusterId);
        } catch (KnConfigurationException e) {
            knLogger.error("fetchClusterId(), Exception in fetching cluster id for pttserverid: " + pttServerId, e);
            throw e;
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Exception in fetching cluster id from database for pttserverid: " + pttServerId, e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw e;
        }
        return clusterId;
    }

}
