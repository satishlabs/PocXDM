/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupProfileInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpInOutParamDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.getDirectoryURI;

public class KnCorpGroupProfileUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileUtil.class);

	public void createGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, String xdmsHome,
                                   KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "createGroupProfile()";
		knLogger.debug(methodName, "ENTRY :",groupProfilePersistDTO);
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.createGroupProfile(groupProfilePersistDTO, persisterTxn);
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

	public List<KnCorpGroupProfileInfo> getGroupProfileList(int corpId, Integer startIndex, Integer fetchSize,
                                                            String xdmsHome, KnPersisterTxn persisterTxn,String hierarchyId) throws KnCorpBOException {
		final String methodName = "getGroupProfileList()";
		knLogger.debug(methodName, "ENTRY :"," corpId ",corpId," startIndex ",startIndex," fetchSize ",fetchSize);
		List<KnCorpGroupProfileInfo> groupProfileList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            if(hierarchyId!=null && !hierarchyId.isEmpty()){
                groupProfileList = xdmDAO.getGroupProfileListByHierarchyId(corpId, startIndex, fetchSize, persisterTxn,hierarchyId);
            }else {
                groupProfileList = xdmDAO.getGroupProfileList(corpId, startIndex, fetchSize, persisterTxn);
            }
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," groupProfileList ",groupProfileList);
		return groupProfileList;
		
	}

	public KnCorpGroupProfileInfo getGroupProfileDetail(Integer profileId, String profileName, int corpId,
														String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
		return getGroupProfileDetail(profileId, profileName, corpId, xdmsHome, false, persisterTxn);
	}

	public KnCorpGroupProfileInfo getGroupProfileDetail(Integer profileId, String profileName, int corpId,
														String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
		return getGroupProfileDetail(profileId, profileName, corpId, xdmsHome, readOnly, persisterTxn, null);
	}

	public KnCorpGroupProfileInfo getGroupProfileDetail(Integer profileId, String profileName, int corpId,
														String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn, String hierarchyId) throws KnCorpBOException {
		final String methodName = "getGroupProfileDetail()";
		knLogger.debug(methodName, "ENTRY :"," profileId ",profileId," profileName ",profileName," corpId ",corpId," hierarchyId ",hierarchyId);
		KnCorpGroupProfileInfo profileInfo = null;
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			if (hierarchyId != null && !hierarchyId.isEmpty()) {
				if (profileId != null) {
					profileInfo = xdmDAO.getGroupProfileDetailByIdAndHierarchyId(profileId, corpId, hierarchyId, readOnly, persisterTxn);
				} else if (null != profileName) {
					profileInfo = xdmDAO.getGroupProfileDetailByNameAndHierarchyId(profileName, corpId, hierarchyId, readOnly, persisterTxn);
				}
			} else {
				if (profileId != null) {
					profileInfo = xdmDAO.getGroupProfileDetailById(profileId, corpId, readOnly, persisterTxn);
				} else if (null != profileName) {
					profileInfo = xdmDAO.getGroupProfileDetailByName(profileName, corpId, readOnly, persisterTxn);
				}
			}
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," profileInfo ",profileInfo);
		return profileInfo;
	}

	public Map<String, Integer> selectGroupCountByGroupProfileId(List<String> groupProfileIds, String xdmsHome,
																 KnPersisterTxn persisterTxn) throws KnCorpBOException {
		return selectGroupCountByGroupProfileId(groupProfileIds, xdmsHome, false, persisterTxn);
	}

	public Map<String, Integer> selectGroupCountByGroupProfileId(List<String> groupProfileIds, String xdmsHome, boolean readOnly,
																 KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "selectGroupCountByGroupProfileId()";
		knLogger.debug(methodName, "ENTRY :"," groupProfileIds ",groupProfileIds);
		Map<String, Integer> groupProfileCountMap = new HashMap<String, Integer>();
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			groupProfileCountMap = xdmDAO.selectGroupCountByGroupProfileId(groupProfileIds, readOnly, persisterTxn);

		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," groupProfileCountMap ",groupProfileCountMap);
		return groupProfileCountMap;
	}

	public Integer getGroupProfileCountByCorpId(int corpId, String xdmsHome, KnPersisterTxn persisterTxn,String hierarchyId)
			throws KnCorpBOException {
		final String methodName = "getGroupProfileDetail()";
		knLogger.debug(methodName, "ENTRY :"," corpId ",corpId);
		int profileCount = 0;
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            if(hierarchyId!=null && !hierarchyId.isEmpty()){
                profileCount = xdmDAO.getGroupProfileCountByCorpIdByHierarchyId(corpId, xdmsHome, persisterTxn,hierarchyId);
            }else {
                profileCount = xdmDAO.getGroupProfileCountByCorpId(corpId, xdmsHome, persisterTxn);
            }

		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," profileCount ",profileCount);
		return profileCount;
	}

	public List<KnCorpGroupProfileInfo> searchGroupProfileByNameAndType(int corpId, Integer startIndex, Integer fetchSize, String grpProfileName, Integer grpType,
                                                                        String xdmsHome, KnPersisterTxn persisterTxn, String hierarchyId) throws KnCorpBOException {
		final String methodName = "searchGroupProfileByNameAndType()";
		knLogger.debug(methodName, "ENTRY :"," corpId ",corpId," startIndex ",startIndex," fetchSize ",fetchSize," grpProfileName ",grpProfileName," grpType",grpType);
		List<KnCorpGroupProfileInfo> groupProfileList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			groupProfileList = xdmDAO.searchGroupProfileByNameAndType(corpId, startIndex, fetchSize, grpType,grpProfileName,persisterTxn, hierarchyId);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while search GroupProfile By Name And Type ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," groupProfileList ",groupProfileList);
		return groupProfileList;

	}
	public List<KnCorpGroupProfileInfo> searchGroupProfileByGpType(int corpId, Integer startIndex, Integer fetchSize, Integer grpType,
                                                                   String xdmsHome, KnPersisterTxn persisterTxn, String hierarchyId) throws KnCorpBOException {
		final String methodName = "searchGroupProfileByGpType()";
		knLogger.debug(methodName, "ENTRY :"," corpId ",corpId," startIndex ",startIndex," fetchSize ",fetchSize," grpType ",grpType);
		List<KnCorpGroupProfileInfo> groupProfileList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			groupProfileList = xdmDAO.searchGroupProfileByGpType(corpId, startIndex, fetchSize,grpType,persisterTxn, hierarchyId);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while search GroupProfile By Name And Type ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," groupProfileList ",groupProfileList);
		return groupProfileList;

	}
	public List<KnCorpGroupProfileInfo> searchGroupProfileByProfileName(int corpId, Integer startIndex, Integer fetchSize, String grpProfileName,
                                                                        String xdmsHome, KnPersisterTxn persisterTxn, String hierarchyId) throws KnCorpBOException {
		final String methodName = "searchGroupProfileByProfileName()";
		knLogger.debug(methodName, "ENTRY :"," corpId ",corpId," startIndex ",startIndex," fetchSize ",fetchSize," grpProfileName",grpProfileName);
		List<KnCorpGroupProfileInfo> groupProfileList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			groupProfileList = xdmDAO.searchGroupProfileByProfileName(corpId, startIndex, fetchSize, grpProfileName,persisterTxn, hierarchyId);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while search GroupProfile By Name And Type ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," groupProfileList ",groupProfileList);
		return groupProfileList;

	}

    public void updateGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.updateGroupProfile(groupProfilePersistDTO, persisterTxn);
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
    }

    public void formGroupPropChangeNotify(Map<String, KnOPDirChgDTO> eTags, Map<String, List<Integer>>
			subscriberGrpListMap, Map<Integer, KnCorpGroupDTO> allGroupMap, Integer newAvatar, boolean
			isOSMListModified, int documntChngType) {
		final String methodName = "formGroupPropChangeNotify()";
		if (eTags != null && !eTags.isEmpty()) {
			for (Map.Entry<String, KnOPDirChgDTO> entry : eTags.entrySet()) {
				String mdn = entry.getKey();
				KnOPDirChgDTO dirChgDto = entry.getValue();


				if (KnCorpUtil.isObjectNull(dirChgDto)) {
					dirChgDto = new KnOPDirChgDTO();
					dirChgDto.setDirUri(getDirectoryURI(mdn));
				}
				Collection<KnOPDocChgDTO> dirDocLst = dirChgDto.getDocChgDTO();
				if (KnCorpUtil.isObjectNull(dirDocLst)) {
					dirDocLst = new ArrayList<>();
				}

				List<Integer> subsGroupList = subscriberGrpListMap.get(mdn);
				if(subsGroupList !=null ){
					for (Integer grpId : subsGroupList) {
						int prevEtag = allGroupMap.get(grpId).getETag();
						int etag = prevEtag + 1;
						KnOPDocChgDTO docChgDto = new KnOPDocChgDTO();
						docChgDto.setDocType(documntChngType);
						docChgDto.setDocumentChgType(documntChngType);
						docChgDto.setNewEtag(String.valueOf(etag));
						docChgDto.setPrevEtag(String.valueOf(prevEtag));
						docChgDto.setDocUri(KnCorpCommonInfoUtil.getGroupDocumentURI(mdn, grpId, documntChngType));
						docChgDto.setGroupId(grpId);
						if (null != newAvatar) {
							docChgDto.setAvatar(newAvatar);
						}
						docChgDto.setOsmListChanged(isOSMListModified);
						docChgDto.setEntryUri(KnCorpCommonInfoUtil.getAddGroupEntryUri(mdn, grpId));
						dirDocLst.add(docChgDto);
					}
					dirChgDto.setDocChgDTO(dirDocLst);
				}
				eTags.put(mdn, dirChgDto);
			}
		}
	}

	public void updateAddlTalkGroupTable(String xdmsHome, Collection<String> mdnList, KnPersisterTxn persisterTxn) {
		final String methodName = "updateAddlTalkGroupTable(mdnList)";
		knLogger.debug(methodName, "ENTRY :");
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
			corpXdmDao.insertOrUpdateAddlTGInfo(mdnList, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
					" due to some contact changes- ", e);
		}
	}

	public void updateAddlTalkGroupTables(String xdmsHome, Collection<String> mdnList, KnPersisterTxn persisterTxn,
										  Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
		final String methodName = "updateAddlTalkGroupTables()";
		knLogger.debug(methodName, "ENTRY :");
		try {
			Map<String, KnOPDocChgDTO> addlTGMap;
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
			addlTGMap = corpXdmDao.insertOrUpdateAddlTGInfo(mdnList, persisterTxn);
			knLogger.debug(methodName, " addlTGMap :", addlTGMap);

			for (String subs : mdnList) {
				Collection<KnOPDocChgDTO> docLists;
				KnOPDirChgDTO directory = etagMap.get(subs);
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
			}
		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
					" due to some contact changes- ", e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

	public void deleteGroupProfile(Integer profileId, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.deleteGroupProfile(profileId, persisterTxn);
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

	public Set<String> getAllMdn(Map<String, List<Integer>> subscriberGrpListMap, Collection<KnCorpAddlTGInfoDTO>
			subsAddlTGList, KnCorpInOutParamDTO corpInOutParamDTO) {
		String methodName = "getAllMdn";
		Set<String> uniqueMDNList = new HashSet<>();
		if(subscriberGrpListMap!=null && !subscriberGrpListMap.isEmpty()) {
			uniqueMDNList.addAll(subscriberGrpListMap.keySet());
		}
		if(subsAddlTGList != null){
			Set<String> mdnList = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).collect(Collectors.toSet());
			knLogger.debug(methodName,"mdnList - ", KnGDPRTemplate.mdnList(mdnList));
			uniqueMDNList.addAll(mdnList);
		}
		if(corpInOutParamDTO.getMdnTgscEtag() != null){
			uniqueMDNList.addAll(corpInOutParamDTO.getMdnTgscEtag().keySet());
		}
		if(corpInOutParamDTO.getMdnTgssEtag() != null){
			uniqueMDNList.addAll(corpInOutParamDTO.getMdnTgssEtag().keySet());
		}
		if(corpInOutParamDTO.getTgsModeChgMap() != null){
			uniqueMDNList.addAll(corpInOutParamDTO.getTgsModeChgMap().keySet());
		}
		knLogger.debug(methodName,"uniqueMDNList - ",uniqueMDNList);
		return uniqueMDNList;
	}

	/**
	 *
	 * @param corpId
	 * @param osmListId
	 * @param xdmsHome
	 * @param persisterTxn
	 * @return
	 * @throws KnCorpBOException
	 */
	public Set<Integer>  getGroupProfileDetailByOsmListId(int corpId, int osmListId,
																		String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "getGroupProfileDetailByOsmListId()";
		knLogger.debug(methodName, "ENTRY :"," corpId ",corpId," osmListId ",osmListId);
		Set<Integer> groupProfileIds = new HashSet<>();
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			groupProfileIds = xdmDAO.getGroupProfileDetailByOsmListId(corpId,osmListId,persisterTxn);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while search GroupProfile By Name And Type ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :"," groupProfileIds ",groupProfileIds);
		return groupProfileIds;

	}

	/**
	 *
	 * @param corpId
	 * @param osmListId
	 * @param xdmsHome
	 * @param persisterTxn
	 * @return
	 * @throws KnCorpBOException
	 */
	public void deleteOSMListFromProfile(int osmListId,int corpId, String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "deleteOSMListFromProfile()";
		knLogger.debug(methodName, "ENTRY :"," corpId ",corpId," osmListId ",osmListId);
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.deleteOSMListFromProfile(osmListId,corpId,persisterTxn);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while search GroupProfile By Name And Type ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :");
	}

	public int getGroupProfileCountByGrpType(int corpId, Integer grpType, String xdmsHome, KnPersisterTxn persisterTxn, String hierarchyId) throws KnCorpBOException {
		final String methodName = "getGroupProfileCountByGrpType()";
		knLogger.debug(methodName, "ENTRY :","corpid - ",corpId,"grpType -",grpType);
		int count = 0;
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			count = xdmDAO.getGroupProfileCountByGrpType(corpId,grpType,persisterTxn, hierarchyId);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while getGroupProfileCountByGrpType ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :");
		return count;
	}

	public int getGroupProfileCountByName(int corpId, String grpProfileName, String xdmsHome, KnPersisterTxn persisterTxn, String hierarchyId) throws KnCorpBOException{
		final String methodName = "getGroupProfileCountByName()";
		knLogger.debug(methodName, "ENTRY :","corpid - ",corpId,"grpProfileName -",grpProfileName);
		int count = 0;
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			count = xdmDAO.getGroupProfileCountByName(corpId,grpProfileName,persisterTxn, hierarchyId);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while getGroupProfileCountByName ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "Exit :");
		return count;
	}
	public void createGroupProfileSharedCorpInfo(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, String xdmsHome,
								   KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "createGroupProfileSharedCorpInfo()";
		knLogger.debug(methodName, "ENTRY :",groupProfilePersistDTO);
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.createGroupProfileSharedCorpInfo(groupProfilePersistDTO, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while creating shared corp info for group profile  ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

	public List<KnCorpSharedCorpInfo> selectGroupProfileSharedCorpInfo(int ownedCorpId, Integer grpProfileId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "selectGroupProfileSharedCorpInfo()";
		knLogger.debug(methodName, "ENTRY :", grpProfileId, "ownedCorpId - ", ownedCorpId);
		List<KnCorpSharedCorpInfo> sharedCorpInfoList = null;
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			sharedCorpInfoList = xdmDAO.selectGroupProfileSharedCorpInfo(ownedCorpId, grpProfileId, readOnly, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "exception while fetching shared corp info for group profile  ", e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		return sharedCorpInfoList;
	}

	public Map<Integer,List<KnCorpSharedCorpInfo>> selectGroupProfileSharedCorpInfo(int ownedCorpId,Collection<Integer> grpProfileIds,String xdmsHome, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "selectGroupProfileSharedCorpInfo()";
		knLogger.debug(methodName, "ENTRY :",grpProfileIds,"ownedCorpId - ",ownedCorpId);
		Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpInfoList = null;
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			sharedCorpInfoList = xdmDAO.selectGroupProfileSharedCorpInfo(ownedCorpId,grpProfileIds, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while fetching shared corp info for group profile  ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		return sharedCorpInfoList;
	}

	public void deleteGroupProfileSharedInfo(Integer profileId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
		final String methodName = "deleteGroupProfileSharedInfo()";
		knLogger.debug(methodName, "ENTRY :", profileId);
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.deleteGroupProfileSharedInfo(profileId, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "exception while deleting shared corp info for group profile  ", e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

	public void deleteGroupProfileSharedInfoByOwnedCorpId(Integer ownedCorpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
		final String methodName = "deleteGroupProfileSharedInfoByOwnedCorpId()";
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.deleteGroupProfileSharedInfoByOwnedCorpId(ownedCorpId, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "exception while deleting shared corp info for group profile  ", e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

	public void deleteGroupProfileByCorpId(Integer corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
		final String methodName = "getGroupProfileCountByName()";
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			xdmDAO.deleteGroupProfileByCorpId(corpId,persisterTxn);

		} catch (KnDAOException e) {
			knLogger.error(methodName,"exception while getGroupProfileCountByName ",e.getMessage());
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}
}
