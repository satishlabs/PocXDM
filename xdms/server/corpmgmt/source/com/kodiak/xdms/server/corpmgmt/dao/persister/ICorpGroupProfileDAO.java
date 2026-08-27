/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.commdto.response.KnCORPGroupStatsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupProfileInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICorpGroupProfileDAO {
	KnCorpGroupProfileInfo getGroupProfileDetailById(Integer profileId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnDAOException;

	KnCorpGroupProfileInfo getGroupProfileDetailByName(String profileName, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnDAOException;

	KnCorpGroupProfileInfo getGroupProfileDetailByIdAndHierarchyId(Integer profileId, int corpId, String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnDAOException;

	KnCorpGroupProfileInfo getGroupProfileDetailByNameAndHierarchyId(String profileName, int corpId, String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnDAOException;

	void createGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

	List<KnCorpGroupProfileInfo> getGroupProfileList(int corpId, Integer startIndex, Integer fetchSize, KnPersisterTxn persisterTxn) throws KnDAOException;

    List<KnCorpGroupProfileInfo> getGroupProfileListByHierarchyId(int corpId, Integer startIndex, Integer fetchSize, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException;

    int getGroupProfileCountByCorpId(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    int getGroupProfileCountByCorpIdByHierarchyId(int corpId, String xdmsHome, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException;

    List<KnCorpGroupProfileInfo> searchGroupProfileByNameAndType(int corpId, Integer startIndex, Integer fetchSize, Integer grpType, String grpProfileName,
																 KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException;

	List<KnCorpGroupProfileInfo> searchGroupProfileByGpType(int corpId, Integer startIndex, Integer fetchSize, Integer grpType, KnPersisterTxn persisterTxn, String hierarchyId)
			throws KnDAOException;

	List<KnCorpGroupProfileInfo> searchGroupProfileByProfileName(int corpId, Integer startIndex, Integer fetchSize, String grpProfileName, KnPersisterTxn persisterTxn, String hierarchyId)
			throws KnDAOException;

	void updateGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

	void deleteGroupProfile(Integer profileId, KnPersisterTxn persisterTxn) throws KnDAOException;

	Set<Integer> getGroupProfileDetailByOsmListId(int corpId, int osmListId, KnPersisterTxn persisterTxn) throws KnDAOException;

	public void deleteOSMListFromProfile(int osmListId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

	int getGroupProfileCountByGrpType(int corpId, Integer grpType, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException;

	int getGroupProfileCountByName(int corpId, String grpProfileName, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException;

	void createGroupProfileSharedCorpInfo(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

	List<KnCorpSharedCorpInfo> selectGroupProfileSharedCorpInfo(int ownedCorpId, Integer grpProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

	Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupProfileSharedCorpInfo(int ownedCorpId, Collection<Integer> grpProfileIds, KnPersisterTxn persisterTxn)
			throws KnDAOException;

	void deleteGroupProfileSharedInfo(Integer profileId, KnPersisterTxn persisterTxn) throws KnDAOException;

	void deleteGroupProfileSharedInfoByOwnedCorpId(Integer ownedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

	void deleteGroupProfileByCorpId(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

	List<KnCORPGroupStatsRespDTO> getGroupStats(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
	public KnCorpGroupDTO ifGroupExistsForCorpId(String groupId, String corpId);
	public boolean sharedCorpCheck(String groupId, String sharedCorpId, String ownerCorpId);
	public int isGroupValidRequest(int groupId, Collection<Integer> idContextIdList, int idType, List<String> existingMemLists, KnPersisterTxn persisterTxn) throws KnDAOException;
	public Map<String, List<String>> groupIdinContextIdMap(int groupId, List<Integer> inContextIds, String xdmsHome, KnPersisterTxn persisterTxn);
	public Map<Integer, List<KnCorpGroupDTO>> sharedGroupIdContextIdMap(int groupId, Map<Integer, List<Integer>> idIncontextIdMap, String xdmsHome, KnPersisterTxn persisterTxn);
}
