/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.impl;


import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSIPProxySvcConfigDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.ICorpGroupProfileController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGorupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeleteBulkCorpGrpDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupProfileResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.common.resources.KnConstants.IDTYPE;
import static com.kodiak.common.resources.KnConstants.HIER_CTX_ID_TYPE;
import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_GROUP_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.DELETE_GROUP;

public class KnCorpGroupProfileController implements ICorpGroupProfileController {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileController.class);
	private KnGenInfoUtil genInfoUtil;
	private KnValidatorFramework validatorFW;
	private KnCorpGroupProfileUtil groupProfilUtil;
	private KnCorpCommonInfoUtil commonInfoUtil;
	private KnCorpOSMInfoUtil corpOSMInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
	private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
	private KnGeneralCacheUtil generalCacheUtil;
	private KnCorpUserProfileUtil corpUserProfileUtil;

	public KnCorpGroupProfileController() {
		validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
		genInfoUtil = KnGenInfoUtil.getInstance();
		groupProfilUtil = new KnCorpGroupProfileUtil();
		commonInfoUtil = new KnCorpCommonInfoUtil();
		corpOSMInfoUtil = new KnCorpOSMInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        contactInfoUtil = new KnCorpContactInfoUtil();
		corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
		generalCacheUtil = KnGeneralCacheUtil.getInstance();
		corpUserProfileUtil = new KnCorpUserProfileUtil();
	}

	@Override
	public KnCorpResponseDTO createGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                KnPersisterTxn persisterTxn) {
		String methodName = "createGroupProfile(KnIPCorpGorupProfileDTO , KnPersisterTxn )";
		KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
		KnCorpGroupProfilePersistDTO groupProfilePersistDTO = new KnCorpGroupProfilePersistDTO();
		final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
		knLogger.debug(methodName, "groupProfileIPDto - ", groupProfileIPDto);
		String xdmsHome = "";
		try {

			knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + groupProfileIPDto.getCorpId(),	CORP_PROFILE, false, persisterTxn);
			xdmsHome = corpProfile.getXdmsHome();
			int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

			Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
			//knLogger.debug(methodName, "paramNameValueMapCommon", paramNameValueMapCommon);

			Integer grpProfileCount = groupProfilUtil.getGroupProfileCountByCorpId(groupProfileIPDto.getCorpId(), xdmsHome, persisterTxn,null);
			knLogger.debug(methodName, "Retrieved group profile count in corp  - ", groupProfileIPDto.getCorpId(), grpProfileCount);

			groupProfilePersistDTO.setInputDTO(groupProfileIPDto);
			groupProfilePersistDTO.setGrpProfileCount(grpProfileCount);
			groupProfilePersistDTO.setCorpProfile(corpProfile);
			groupProfilePersistDTO.setParamNameValueMapCommon(paramNameValueMapCommon);
			groupProfilePersistDTO.setOperationType(groupProfileIPDto.getOperationType());
			groupProfilePersistDTO.setGrpOSMListId(groupProfileIPDto.getGrpOSMListId());
			groupProfilePersistDTO.setGrpShared(groupProfileIPDto.getGrpShared());
			groupProfilePersistDTO.setCorpSharedCorpInfoList(groupProfileIPDto.getSharedCorpList());

			Boolean interopFlagCheck = (null != groupProfileIPDto.getUgwInterop() && groupProfileIPDto.getUgwInterop().equals("7"));
			if (interopFlagCheck) {
				String ugwInteropFlag = paramNameValueMapCommon.get(UGWINTEROP);
				knLogger.debug(methodName, "Retrieved ugwInterop flag from system config - ", ugwInteropFlag);
				groupProfilePersistDTO.setUgwInteropSystemConfig(ugwInteropFlag);
				groupProfilePersistDTO.setUgwInterop(groupProfileIPDto.getUgwInterop());
			}

			// validating group profile
			KnCorpGroupProfileInfo corpgroupProfile = groupProfilUtil.getGroupProfileDetail( groupProfileIPDto.getGrpProfileId(), groupProfileIPDto.getGrpProfileName(),
					groupProfileIPDto.getCorpId(), xdmsHome, persisterTxn);
			knLogger.debug(methodName, "Retrieved corp group profile details - ", corpgroupProfile);

			if (corpgroupProfile != null) {
				groupProfilePersistDTO.setProfileExist(true);
			}

			// validating OSMListIdExists
			Map<Integer, Integer> osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMap(String.valueOf(groupProfileIPDto.getCorpId()), xdmsHome, persisterTxn);
			groupProfilePersistDTO.setOsmListIdAndDefultMap(osmListIdAndDefaultMap);
			groupProfilePersistDTO.setMcxGroup(groupProfileIPDto.getMcxGroup());

			if(groupProfileIPDto.getGrpShared()!=null && groupProfileIPDto.getGrpShared() == GROUP_SHARING_ENABLE_INDICATOR){
				knLogger.debug(methodName, "grp sharing enabled ", groupProfilePersistDTO);
				List<KnCorpSharedCorpInfo> sharedCorpList = groupProfileIPDto.getSharedCorpList();
				if(sharedCorpList!=null && !sharedCorpList.isEmpty()){
					Set<String> extCorpIds = sharedCorpList.stream().map(KnCorpSharedCorpInfo::getExtCorpId).collect(Collectors.toSet());
					Set<Integer> intCorpIds = sharedCorpList.stream().map(KnCorpSharedCorpInfo::getCorpId).collect(Collectors.toSet());
					knLogger.debug(methodName,"Fetching shared corp information - ",extCorpIds," intCorpIds - ",intCorpIds);
					Map<String,Integer> extIntCorpIDMap = commonInfoUtil.getCorpIdMap(extCorpIds,intCorpIds,persisterTxn);
					knLogger.debug(methodName,"extIntCorpIDMap - ",extIntCorpIDMap);
					groupProfilePersistDTO.setExtIntCorpIdMap(extIntCorpIDMap);
					List<KnCorpTrustMatrixDTO> trustMatrixDTOList = generalCacheUtil.getSharedCorpMatrix(corpProfile.getExtCorpId().trim());
					groupProfilePersistDTO.setTrustMatrixDTOList(trustMatrixDTOList);

				}
			}
			knLogger.debug(methodName, "Before Validation", groupProfilePersistDTO);
			validatorFW.validate(groupProfilePersistDTO);
			knLogger.debug(methodName, "Validation Successfull");
			
			final int grpProfileId =genInfoUtil.retrieveIdForTable(CORP_GROUP_PROFILE_TABLE, xdmsHome, CORP_GROUP_PROFILE_TABLE_PK, false, null);
			groupProfilePersistDTO.setGrpProfileId(grpProfileId);
			groupProfilePersistDTO.setCorpId(groupProfileIPDto.getCorpId());
			groupProfilePersistDTO.setGrpProfileName(groupProfileIPDto.getGrpProfileName());
			
			if (groupProfileIPDto.getGrpType() != null) {
				groupProfilePersistDTO.setGrpType(groupProfileIPDto.getGrpType());
			} else {
				groupProfilePersistDTO.setGrpType(DEFAULT_GRP_TYPE);
			}
			groupProfilePersistDTO.setGrpAvatar(groupProfileIPDto.getGrpAvatar());
			
			if (groupProfileIPDto.getGrpServiceType() != null) {
				groupProfilePersistDTO.setGrpServiceType(groupProfileIPDto.getGrpServiceType());
			} else {
				groupProfilePersistDTO.setGrpServiceType(CORP_GROUP_DEFAULT_SERVICE_TYPE);
			}
			
			groupProfilePersistDTO.setAudioCutIn(groupProfileIPDto.getAudioCutIn());
			long profileCreationTime = Calendar.getInstance().getTimeInMillis();
	           
			groupProfilePersistDTO.setCreateTimeStamp(profileCreationTime);
			groupProfilePersistDTO.setUpdateTimeStamp(profileCreationTime);
			groupProfilePersistDTO.setGrpProfileStatus(GRP_PROFILE_STATUS.ACTIVE.value());
			groupProfilePersistDTO.setFeatureAllowed(CORP_GROUP_DEFAULT_ALLOWED_FEATURE);
			groupProfilePersistDTO.setOverrideDND(groupProfileIPDto.getOverrideDND());
			groupProfilePersistDTO.setHierarchyId(groupProfileIPDto.getHierarchyId());
			groupProfilUtil.createGroupProfile(groupProfilePersistDTO, xdmsHome, persisterTxn);

			if(groupProfileIPDto.getGrpShared() != null && groupProfileIPDto.getGrpShared() == GROUP_SHARING_ENABLE_INDICATOR){

				groupProfilUtil.createGroupProfileSharedCorpInfo(groupProfilePersistDTO, xdmsHome, persisterTxn);
			}

			int corpId = groupProfileIPDto.getCorpId();
			if (interopFlagCheck) {
				boolean isGroupProfileNotifyRequired = commonInfoUtil.isGroupProfileNotifyRequired(corpId, persisterTxn);
				if (isGroupProfileNotifyRequired) {
					//Update the UGWINTEROP
					commonInfoUtil.updateLmrInterOpInDB(corpId, ENABLED, persisterTxn);
				}
			}

            respDTO.setGrpProfileId(groupProfilePersistDTO.getGrpProfileId());
			populate(respDTO);
		} catch (KnValidationException e) {
			populate(respDTO, e);
			knLogger.error(methodName, "KnValidationException occurred", e);
		} catch (KnCorpBOException e) {
			populate(respDTO, e);
			knLogger.error(methodName, "KnCorpBOException occurred", e);
		} catch (Exception e) {
			populate(respDTO, e);
			knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
					com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
		}
		knLogger.debug(methodName, "Success response", respDTO);
		return respDTO;
	}

	@Override
	public KnCorpGroupProfileResponseDTO getGroupProfileList(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                             KnPersisterTxn persisterTxn) {
		String methodName = "getGroupProfileList(KnIPCorpGorupProfileDTO , KnPersisterTxn )";
		KnCorpGroupProfileResponseDTO respDTO = new KnCorpGroupProfileResponseDTO();
		KnCorpGroupProfilePersistDTO groupProfilePersistDTO = new KnCorpGroupProfilePersistDTO();
		final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
		knLogger.debug(methodName, "groupProfileIPDto - ", groupProfileIPDto);
		String xdmsHome = "";
		try {

			knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + groupProfileIPDto.getCorpId(),	CORP_PROFILE, false, persisterTxn);
			xdmsHome = corpProfile.getXdmsHome();
			int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

			Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
			//knLogger.debug(methodName, "paramNameValueMapCommon", paramNameValueMapCommon);


			groupProfilePersistDTO.setInputDTO(groupProfileIPDto);
			groupProfilePersistDTO.setCorpProfile(corpProfile);
			groupProfilePersistDTO.setParamNameValueMapCommon(paramNameValueMapCommon);
			groupProfilePersistDTO.setOperationType(groupProfileIPDto.getOperationType());
            groupProfilePersistDTO.setHierarchyId(groupProfilePersistDTO.getHierarchyId());

			knLogger.debug(methodName, "Before Validation", groupProfilePersistDTO);
			validatorFW.validate(groupProfilePersistDTO);
			knLogger.debug(methodName, "Validation Successfull");
			List<KnCorpGroupProfileInfo> groupProfileList = groupProfilUtil.getGroupProfileList(groupProfileIPDto.getCorpId(),groupProfileIPDto.getStartIndex(),groupProfileIPDto.getFetchSize(), xdmsHome, persisterTxn, groupProfileIPDto.getHierarchyId());
			List<String> groupProfileIds=groupProfileList.stream().map(t-> String.valueOf(t.getProfileId())).collect(Collectors.toList());
			Map<String,Integer> groupProfileCountMap = groupProfilUtil.selectGroupCountByGroupProfileId(groupProfileIds, xdmsHome, persisterTxn);

			Integer grpProfileCount = groupProfilUtil.getGroupProfileCountByCorpId(groupProfileIPDto.getCorpId(), xdmsHome, persisterTxn, groupProfileIPDto.getHierarchyId());
			knLogger.debug(methodName, "Retrieved group profile count in corp  - ", groupProfileIPDto.getCorpId(), grpProfileCount);

			if(groupProfileList!=null && !groupProfileList.isEmpty()){
				List<Integer> grpProfileIds=groupProfileList.stream().map(t-> t.getProfileId()).collect(Collectors.toList());
				knLogger.debug(methodName,"fetching shared corpinfo for groupProfileIds ",groupProfileIds);
				Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = groupProfilUtil.selectGroupProfileSharedCorpInfo(groupProfileIPDto.getCorpId(),
						grpProfileIds,xdmsHome,persisterTxn);

				Set<Integer> sharedInCorpIdSet = new HashSet<>();
				//TODO:convert into streams
				if(sharedCorpInfoMap!=null && !sharedCorpInfoMap.isEmpty()){
					sharedCorpInfoMap.entrySet().stream().forEach(e-> {
						List<KnCorpSharedCorpInfo> dbInfoList = e.getValue();
						if(dbInfoList!=null && !dbInfoList.isEmpty()){
							dbInfoList.forEach(dbInfo ->{
								sharedInCorpIdSet.add(dbInfo.getCorpId());
							});
						}
					});
				}

				knLogger.debug(methodName,"Fetching external corpids for sharedInCorpIdSet ",sharedInCorpIdSet);

				Map<String,Integer> extIntCorpidMap = commonInfoUtil.getCorpIdMap(null,sharedInCorpIdSet,persisterTxn);

				Map<Integer, String> intExtCorpIdMap = new HashMap<>();
				if(extIntCorpidMap!=null && !extIntCorpidMap.isEmpty()){
					 intExtCorpIdMap .putAll(extIntCorpidMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
				}

				knLogger.debug(methodName,"after interchaing external corpid map",intExtCorpIdMap);

				groupProfileList.forEach(grpProfile ->{
					if(grpProfile.getGrpShared()!=null && grpProfile.getGrpShared() == GROUP_SHARING_ENABLE_INDICATOR){
						List<KnCorpSharedCorpInfo> dbSharedCorpInfoList = sharedCorpInfoMap.get(grpProfile.getProfileId());
						if(dbSharedCorpInfoList!=null && !dbSharedCorpInfoList.isEmpty()) {
							List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
							dbSharedCorpInfoList.forEach(dbSharedCorpInfo ->{;
							KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
							sharedCorpInfo.setExtCorpId(intExtCorpIdMap.get(dbSharedCorpInfo.getCorpId()));
							sharedCorpInfo.setCorpId(dbSharedCorpInfo.getCorpId());
							sharedCorpInfoList.add(sharedCorpInfo);
							});
							grpProfile.setSharedCorpList(sharedCorpInfoList);
						}


					}

				});

			}


			respDTO.setGroupProfileList(groupProfileList);
			respDTO.setGroupProfileCountMap(groupProfileCountMap);
			respDTO.setTotalGroupProfilesCount(grpProfileCount);
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
	public KnCorpGroupProfileResponseDTO getGroupProfileDetails(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                                KnPersisterTxn persisterTxn) {
		String methodName = "getGroupProfileDetails(KnIPCorpGorupProfileDTO , KnPersisterTxn )";
		KnCorpGroupProfileResponseDTO respDTO = new KnCorpGroupProfileResponseDTO();
		KnCorpGroupProfilePersistDTO groupProfilePersistDTO = new KnCorpGroupProfilePersistDTO();
		final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
		knLogger.debug(methodName, "groupProfileIPDto - ", groupProfileIPDto);
		String xdmsHome = "";
		List<KnCorpSharedCorpInfo> sharedCorpInfoList = null;
		try {

			knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + groupProfileIPDto.getCorpId(),	CORP_PROFILE, false, persisterTxn);
			xdmsHome = corpProfile.getXdmsHome();
			int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

			Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
			//knLogger.debug(methodName, "paramNameValueMapCommon", paramNameValueMapCommon);


			groupProfilePersistDTO.setInputDTO(groupProfileIPDto);
			groupProfilePersistDTO.setCorpProfile(corpProfile);
			groupProfilePersistDTO.setParamNameValueMapCommon(paramNameValueMapCommon);
			groupProfilePersistDTO.setOperationType(groupProfileIPDto.getOperationType());

			// validating group profile
			KnCorpGroupProfileInfo corpgroupProfile = groupProfilUtil.getGroupProfileDetail( groupProfileIPDto.getGrpProfileId(), groupProfileIPDto.getGrpProfileName(),
					groupProfileIPDto.getCorpId(), xdmsHome, true, persisterTxn, groupProfileIPDto.getHierarchyId());
			knLogger.debug(methodName, "Retrieved corp group profile details - ", corpgroupProfile);

			if (corpgroupProfile != null) {
				groupProfilePersistDTO.setProfileExist(true);
				corpgroupProfile.setGrpOwnerCorpId(corpProfile.getCorpId());
				corpgroupProfile.setGrpOwnerExtCorpId(corpProfile.getExtCorpId().trim());
				if (corpgroupProfile.getGrpShared() != null && corpgroupProfile.getGrpShared() == GROUP_SHARING_ENABLE_INDICATOR) {
					knLogger.info(methodName, "group sharing is enabled for the profile fetching shared corp info for profileid- ", groupProfileIPDto.getGrpProfileId());
					sharedCorpInfoList = groupProfilUtil.
							selectGroupProfileSharedCorpInfo(corpProfile.getCorpId(), groupProfileIPDto.getGrpProfileId(), xdmsHome, true, persisterTxn);
					knLogger.info(methodName, "sharedCorpInfoList - ", sharedCorpInfoList);
					Set<Integer> sharedIntCorpIds = sharedCorpInfoList.stream().map(KnCorpSharedCorpInfo::getCorpId).collect(Collectors.toSet());
					knLogger.info(methodName, "converting shared int corpids to external corpids", sharedIntCorpIds);
					Map<String, Integer> extIntCorpIDMap = commonInfoUtil.getCorpIdMap(null, sharedIntCorpIds, true, persisterTxn);

					Map<Integer, String> intExtCorpIdMap = new HashMap<>();
					if (extIntCorpIDMap != null && !extIntCorpIDMap.isEmpty()) {
						intExtCorpIdMap.putAll(extIntCorpIDMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
					}
					if (sharedCorpInfoList != null && !sharedCorpInfoList.isEmpty()) {
						sharedCorpInfoList.forEach(info -> {
							info.setExtCorpId(intExtCorpIdMap.get(info.getCorpId()));
						});
						knLogger.debug(methodName, "after converting shared int corpids to external corpids", sharedCorpInfoList);
						corpgroupProfile.setSharedCorpList(sharedCorpInfoList);
						groupInfoUtil.populateGroupMemProperties(sharedCorpInfoList, generalCacheUtil, corpProfile.getExtCorpId().trim(), true);
				}
				}
			}

			knLogger.debug(methodName, "Before Validation", groupProfilePersistDTO);
			validatorFW.validate(groupProfilePersistDTO);
			knLogger.debug(methodName, "Validation Successfull");
			List<KnCorpGroupProfileInfo> groupProfileList = new ArrayList<KnCorpGroupProfileInfo>();
			groupProfileList.add(corpgroupProfile);
			List<String> groupProfileIds=groupProfileList.stream().map(t-> String.valueOf(t.getProfileId())).collect(Collectors.toList());
			Map<String,Integer> groupProfileCountMap = groupProfilUtil.selectGroupCountByGroupProfileId(groupProfileIds, xdmsHome, true, persisterTxn);
			respDTO.setGroupProfileList(groupProfileList);
			respDTO.setGroupProfileCountMap(groupProfileCountMap);
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
	@SuppressWarnings("unchecked")
	public KnCorpGroupProfileResponseDTO searchGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		String methodName = "searchGroupProfile(KnIPCorpGorupProfileDTO , KnPersisterTxn )";
		KnCorpGroupProfileResponseDTO respDTO = new KnCorpGroupProfileResponseDTO();
		KnCorpGroupProfilePersistDTO groupProfilePersistDTO = new KnCorpGroupProfilePersistDTO();
		final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
		knLogger.debug(methodName, "groupProfileIPDto - ", groupProfileIPDto);
		String xdmsHome = "";
		String hierarchyId = null;
		try {

			knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + groupProfileIPDto.getCorpId(),	CORP_PROFILE, false, persisterTxn);
			xdmsHome = corpProfile.getXdmsHome();
			int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

			Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
			knLogger.debug(methodName, "Group profile management flag ", paramNameValueMapCommon.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.GROUP_PROFILE_MGMT));

			groupProfilePersistDTO.setInputDTO(groupProfileIPDto);
			groupProfilePersistDTO.setCorpProfile(corpProfile);
			groupProfilePersistDTO.setParamNameValueMapCommon(paramNameValueMapCommon);
			groupProfilePersistDTO.setOperationType(groupProfileIPDto.getOperationType());
			Map<String, Object> customParamMap = groupProfileIPDto.getCustomParamMap();
			Object idObj = customParamMap.get(IDLIST);
			if (idObj instanceof Collection<?>) {
				List<String> idList = (List<String>) idObj;
				if (!idList.isEmpty()) {
					hierarchyId = idList.getFirst();
				}

			}
			knLogger.debug(methodName, "Before Validation", groupProfilePersistDTO);
			validatorFW.validate(groupProfilePersistDTO);
			knLogger.debug(methodName, "Validation Successfull");
			List<KnCorpGroupProfileInfo> groupProfileList = new ArrayList<>();
			List<String> groupProfileIds = new ArrayList<>();
			int grpProfileCount = 0;
			int grpProfileUnfilteredCount = 0;
			if(groupProfileIPDto.getGrpType()!=null && groupProfileIPDto.getGrpProfileName()!=null){
				knLogger.debug(methodName,"In grpType & grpProfileName flow");
				groupProfileList = groupProfilUtil.searchGroupProfileByNameAndType(groupProfileIPDto.getCorpId(),groupProfileIPDto.getStartIndex(),groupProfileIPDto.getFetchSize(),
						groupProfileIPDto.getGrpProfileName(), groupProfileIPDto.getGrpType(),xdmsHome, persisterTxn, hierarchyId);
				groupProfileIds.addAll(groupProfileList.stream().map(t -> String.valueOf(t.getProfileId())).collect(Collectors.toList()));
				knLogger.debug(methodName,"groupProfileList.size()",groupProfileList.size());
				grpProfileCount = groupProfilUtil.getGroupProfileCountByGrpType(groupProfileIPDto.getCorpId(),groupProfileIPDto.getGrpType(),xdmsHome, persisterTxn, hierarchyId);
				knLogger.debug(methodName, "Retrieved group profile count by name",groupProfileIPDto.getGrpType(), grpProfileCount);
			}else if(groupProfileIPDto.getGrpType()!=null){
					knLogger.debug(methodName,"In only grpType flow");
				groupProfileList = groupProfilUtil.searchGroupProfileByGpType(groupProfileIPDto.getCorpId(),groupProfileIPDto.getStartIndex(),groupProfileIPDto.getFetchSize(),
						groupProfileIPDto.getGrpType(),xdmsHome, persisterTxn, hierarchyId);
				groupProfileIds.addAll(groupProfileList.stream().map(t -> String.valueOf(t.getProfileId())).collect(Collectors.toList()));
				knLogger.debug(methodName,"groupProfileList.size()",groupProfileList.size());
				grpProfileCount = groupProfilUtil.getGroupProfileCountByGrpType(groupProfileIPDto.getCorpId(),groupProfileIPDto.getGrpType(),xdmsHome, persisterTxn, hierarchyId);
				knLogger.debug(methodName, "Retrieved group profile count by name",groupProfileIPDto.getGrpType(), grpProfileCount);

			}else if(groupProfileIPDto.getGrpProfileName()!=null) {
				knLogger.debug(methodName,"In only grpProfileName flow");
				groupProfileList = groupProfilUtil.searchGroupProfileByProfileName(groupProfileIPDto.getCorpId(),groupProfileIPDto.getStartIndex(),groupProfileIPDto.getFetchSize(),
						groupProfileIPDto.getGrpProfileName(),xdmsHome, persisterTxn, hierarchyId);
				groupProfileIds.addAll(groupProfileList.stream().map(t -> String.valueOf(t.getProfileId())).collect(Collectors.toList()));
				knLogger.debug(methodName,"groupProfileList.size()",groupProfileList.size());
				grpProfileCount = groupProfilUtil.getGroupProfileCountByName(groupProfileIPDto.getCorpId(),groupProfileIPDto.getGrpProfileName(),xdmsHome, persisterTxn, hierarchyId);
				knLogger.debug(methodName, "Retrieved group profile count by name",groupProfileIPDto.getGrpProfileName(), grpProfileCount);

			}
			Map<String,Integer> groupProfileCountMap = groupProfilUtil.selectGroupCountByGroupProfileId(groupProfileIds, xdmsHome, persisterTxn);
			knLogger.info(methodName, "Retrived group count for group profiles   - ", groupProfileIPDto.getCorpId(),groupProfileList,groupProfileCountMap);


			grpProfileUnfilteredCount= groupProfilUtil.getGroupProfileCountByCorpId(groupProfileIPDto.getCorpId(), xdmsHome, persisterTxn,hierarchyId);
			knLogger.debug(methodName, "Retrieved group profile unfiltered count by corpId", grpProfileUnfilteredCount);

			if(groupProfileList!=null && !groupProfileList.isEmpty()){
				List<Integer> grpProfileIds=groupProfileList.stream().map(t-> t.getProfileId()).collect(Collectors.toList());
				knLogger.debug(methodName,"fetching shared corpinfo for groupProfileIds ",groupProfileIds);
				Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = groupProfilUtil.selectGroupProfileSharedCorpInfo(groupProfileIPDto.getCorpId(),
						grpProfileIds,xdmsHome,persisterTxn);

				Set<Integer> sharedInCorpIdSet = new HashSet<>();
				//TODO:convert into streams
				if(sharedCorpInfoMap!=null && !sharedCorpInfoMap.isEmpty()){
					sharedCorpInfoMap.entrySet().stream().forEach(e-> {
						List<KnCorpSharedCorpInfo> dbInfoList = e.getValue();
						if(dbInfoList!=null && !dbInfoList.isEmpty()){
							dbInfoList.forEach(dbInfo ->{
								sharedInCorpIdSet.add(dbInfo.getCorpId());
							});
						}
					});
				}
				knLogger.debug(methodName,"Fetching external corpids for sharedInCorpIdSet ",sharedInCorpIdSet);
				Map<String,Integer> extIntCorpidMap = commonInfoUtil.getCorpIdMap(null,sharedInCorpIdSet,persisterTxn);
				Map<Integer, String> intExtCorpIdMap = new HashMap<>();
				if(extIntCorpidMap!=null && !extIntCorpidMap.isEmpty()){
					intExtCorpIdMap .putAll(extIntCorpidMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
				}
				knLogger.debug(methodName,"after interchanging external corpid map",intExtCorpIdMap);
				groupProfileList.forEach(grpProfile ->{
					if(grpProfile.getGrpShared()!=null && grpProfile.getGrpShared().intValue() == GROUP_SHARING_ENABLE_INDICATOR){
						List<KnCorpSharedCorpInfo> dbSharedCorpInfoList = sharedCorpInfoMap.get(grpProfile.getProfileId());
						if(dbSharedCorpInfoList!=null && !dbSharedCorpInfoList.isEmpty()) {
							List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
							dbSharedCorpInfoList.forEach(dbSharedCorpInfo ->{
								KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
								sharedCorpInfo.setExtCorpId(intExtCorpIdMap.get(dbSharedCorpInfo.getCorpId()));
								sharedCorpInfo.setCorpId(dbSharedCorpInfo.getCorpId());
								sharedCorpInfoList.add(sharedCorpInfo);
							});
							grpProfile.setSharedCorpList(sharedCorpInfoList);
						}
					}
				});
			}

			knLogger.debug(methodName,"groupProfileList - ",groupProfileList);
			respDTO.setGroupProfileList(groupProfileList);
			respDTO.setGroupProfileCountMap(groupProfileCountMap);
			respDTO.setTotalGroupProfilesCount(grpProfileCount);
			respDTO.setTotalGroupProfilesUnfilteredCount(grpProfileUnfilteredCount);
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
	public KnCorpResponseDTO modifyGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		String methodName = "modifyGroupProfile(KnIPCorpGorupProfileDTO , KnPersisterTxn )";
		KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
		KnCorpGroupProfilePersistDTO groupProfilePersistDTO = new KnCorpGroupProfilePersistDTO();
		final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
		knLogger.debug(methodName, "groupProfileIPDto - ", groupProfileIPDto);
		String xdmsHome = "";
		try {
			Integer mcxGroupInd = groupProfileIPDto.getMcxGroup();
			int corpId = groupProfileIPDto.getCorpId();

			Map<String, Object> reqCustomParamMap = groupProfileIPDto.getCustomParamMap();
			boolean isHierarchyContext = false;
			if (reqCustomParamMap != null) {
				String idType = (String) reqCustomParamMap.get(IDTYPE);
				if (HIER_CTX_ID_TYPE.equalsIgnoreCase(idType)) {
					isHierarchyContext = true;
				}
			}
			knLogger.debug(methodName, "Fetch the corpProfile profile");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, false, persisterTxn);
			xdmsHome = corpProfile.getXdmsHome();
			String corpPocHome = corpProfile.getPocHome();
			int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

			Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
			groupProfilePersistDTO.setInputDTO(groupProfileIPDto);
			groupProfilePersistDTO.setCorpProfile(corpProfile);
			groupProfilePersistDTO.setParamNameValueMapCommon(paramNameValueMapCommon);
			groupProfilePersistDTO.setOperationType(groupProfileIPDto.getOperationType());
			groupProfilePersistDTO.setGrpOSMListId(groupProfileIPDto.getGrpOSMListId());
			groupProfilePersistDTO.setMcxGroup(mcxGroupInd);
			groupProfilePersistDTO.setGrpShared(groupProfileIPDto.getGrpShared());
			groupProfilePersistDTO.setCorpSharedCorpInfoList(groupProfileIPDto.getSharedCorpList());

			Boolean interopFlagCheck = (null != groupProfileIPDto.getUgwInterop() && groupProfileIPDto.getUgwInterop().equals("7"));
			if (interopFlagCheck) {
				String ugwInteropFlag = paramNameValueMapCommon.get(UGWINTEROP);
				knLogger.debug(methodName, "Retrieved ugwInterop flag from system config - ", ugwInteropFlag);
				groupProfilePersistDTO.setUgwInteropSystemConfig(ugwInteropFlag);
				groupProfilePersistDTO.setUgwInterop(groupProfileIPDto.getUgwInterop());
			}

			// validating group profile
			KnCorpGroupProfileInfo corpgroupProfile = groupProfilUtil.getGroupProfileDetail(groupProfileIPDto.getGrpProfileId(), groupProfileIPDto.getGrpProfileName(),
					corpId, xdmsHome, persisterTxn);
			knLogger.debug(methodName, "Retrieved corp group profile details - ", corpgroupProfile);

			if (corpgroupProfile != null) {
				groupProfilePersistDTO.setProfileExist(true);
			}
			if (corpgroupProfile != null) {
				//Check if name is changing
				if ((groupProfileIPDto.getNewGrpProfileName() != null) && (!groupProfileIPDto.getNewGrpProfileName().equals(corpgroupProfile.getProfileName()))) {
					knLogger.debug(methodName, "Group profile name changed");
					KnCorpGroupProfileInfo newNameGroupProfile = groupProfilUtil.getGroupProfileDetail(null, groupProfileIPDto.getNewGrpProfileName(),
							corpId, xdmsHome, persisterTxn);
					groupProfilePersistDTO.setNewGrpNmaeProfile(newNameGroupProfile);
				}
				knLogger.info(methodName, "mcx group indicator ",mcxGroupInd,corpgroupProfile.getMcxGrp());
				if((mcxGroupInd!=null) && (mcxGroupInd.intValue()!=corpgroupProfile.getMcxGrp().intValue())){
					knLogger.debug(methodName, "mcx group indicator is changed ",mcxGroupInd,corpgroupProfile.getMcxGrp());
					groupProfilePersistDTO.setMcxGrpIndChanged(true);
				}
				knLogger.info(methodName, "group Type changed ",groupProfileIPDto.getGrpType(),corpgroupProfile.getGrpType());
				if((groupProfileIPDto.getGrpType()!=null) && (groupProfileIPDto.getGrpType().intValue()!=corpgroupProfile.getGrpType().intValue())){
					knLogger.debug(methodName, "group Type changed ",groupProfileIPDto.getGrpType(),corpgroupProfile.getGrpType());
					groupProfilePersistDTO.setGrpTypeChanged(true);
				}
				knLogger.info(methodName, "group Sharing flag changed ",groupProfileIPDto.getGrpShared(),corpgroupProfile.getGrpShared());
				if(!isHierarchyContext && (groupProfileIPDto.getGrpShared()!=null) && (groupProfileIPDto.getGrpShared().intValue()!=corpgroupProfile.getGrpShared().intValue())){
					knLogger.info(methodName, "group Sharing flag changed ",groupProfileIPDto.getGrpShared(),corpgroupProfile.getGrpShared());
					groupProfilePersistDTO.setGrpSharedChanged(true);
				}
			}
			// validating OSMListIdExists
			Map<Integer, Integer> osmListIdAndDefaultMap = corpOSMInfoUtil.getOSMListIdAndDefaultMap(String.valueOf(corpId), xdmsHome, persisterTxn);
			groupProfilePersistDTO.setOsmListIdAndDefultMap(osmListIdAndDefaultMap);

			//validate sharedCorp data recived in the request whether those corp exsists in system or not or whether corp is part of trust matrix
				if(!isHierarchyContext && groupProfileIPDto.getGrpShared()!=null && groupProfileIPDto.getGrpShared() == GROUP_SHARING_ENABLE_INDICATOR){
				knLogger.debug(methodName, "grp sharing enabled ", groupProfilePersistDTO);
				List<KnCorpSharedCorpInfo> sharedCorpList = groupProfileIPDto.getSharedCorpList();
				if(sharedCorpList!=null && !sharedCorpList.isEmpty()){
					Set<String> extCorpIds = sharedCorpList.stream().map(KnCorpSharedCorpInfo::getExtCorpId).collect(Collectors.toSet());
					Set<Integer> intCorpIds = sharedCorpList.stream().map(KnCorpSharedCorpInfo::getCorpId).collect(Collectors.toSet());
					knLogger.debug(methodName,"Fetching shared corp information - ",extCorpIds," intCorpIds - ",intCorpIds);
					Map<String,Integer> extIntCorpIDMap = commonInfoUtil.getCorpIdMap(extCorpIds,intCorpIds,persisterTxn);
					knLogger.debug(methodName,"extIntCorpIDMap - ",extIntCorpIDMap);
					groupProfilePersistDTO.setExtIntCorpIdMap(extIntCorpIDMap);
					List<KnCorpTrustMatrixDTO> trustMatrixDTOList = generalCacheUtil.getSharedCorpMatrix(corpProfile.getExtCorpId().trim());
					groupProfilePersistDTO.setTrustMatrixDTOList(trustMatrixDTOList);

				}
			}
			// get all groups
			Map<Integer, KnCorpGroupDTO> allGroupMap = new HashMap<>();
			if(corpgroupProfile != null && corpgroupProfile.getProfileId() != null){
			allGroupMap = groupInfoUtil.getGroupsByGrpProfileId(corpgroupProfile.getProfileId(),
					corpId, persisterTxn, xdmsHome);
				knLogger.debug(methodName, "allGroupMap - ", allGroupMap);
			Collection<KnCorpGroupMemberDTO>  exsistingGroupMemberList = groupInfoUtil.getCorpGroupMembersList(allGroupMap.keySet(),xdmsHome,persisterTxn);
			knLogger.debug(methodName,"exsistingGroupMember count - ",exsistingGroupMemberList.size());
			groupProfilePersistDTO.setExsistingGroupMemberList(exsistingGroupMemberList);

			// UCSPROVCONFIG-8560 fix: mark existing members as external contacts using UNION of
			// DB shared corp list + request shared corp list for the profile modify path.
			Set<Integer> markingCorpIds = new HashSet<>();
				if (!isHierarchyContext && !allGroupMap.isEmpty() && ((groupProfileIPDto.getGrpShared() != null && groupProfileIPDto.getGrpShared() == GROUP_SHARING_ENABLE_INDICATOR)
					|| (corpgroupProfile != null && corpgroupProfile.getGrpShared() != null
					&& corpgroupProfile.getGrpShared() == GROUP_SHARING_ENABLE_INDICATOR))) {
				Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = groupInfoUtil.selectGroupSharedCorpInfo(corpId,
						allGroupMap.keySet(), xdmsHome, false, persisterTxn);
				if (sharedCorpInfoMap != null && !sharedCorpInfoMap.isEmpty()) {
					sharedCorpInfoMap.values().forEach(sharedCorpInfoList -> {
						if (sharedCorpInfoList != null && !sharedCorpInfoList.isEmpty()) {
							sharedCorpInfoList.forEach(item -> markingCorpIds.add(item.getCorpId()));
						}
					});
				}
			}
			// Add request shared corp list to the union
			if (groupProfileIPDto.getSharedCorpList() != null) {
				groupProfileIPDto.getSharedCorpList().forEach(item -> markingCorpIds.add(item.getCorpId()));
			}
			knLogger.debug(methodName, "markingCorpIds (DB+Request union) -", markingCorpIds, ", ownerCorpId -", corpId);
			if (!markingCorpIds.isEmpty()) {
				for (KnCorpGroupMemberDTO member : exsistingGroupMemberList) {
					if (member.getCorpId() != 0 && (!markingCorpIds.contains(member.getCorpId())) && (member.getCorpId() != corpId)) {
						knLogger.debug(methodName, "Marking external contact -", member.getMdn(), ", memberCorpId -", member.getCorpId());
						member.setExternalContact(Boolean.TRUE);
					}
				}
			}
		}

			knLogger.debug(methodName, "Before Validation", groupProfilePersistDTO);
			validatorFW.validate(groupProfilePersistDTO);
			knLogger.debug(methodName, "Validation Successfull");
			boolean isOSMListModified = false;
			Integer newAvatar = null;
			boolean isGroupPropChanges = false;
			boolean isXcapNotifyRequired = false;
			boolean isGroupSharingChanged = false;

			groupProfilePersistDTO.setGrpProfileId(corpgroupProfile.getProfileId());
			groupProfilePersistDTO.setCorpId(corpId);

			if (groupProfileIPDto.getNewGrpProfileName() != null) {
				groupProfilePersistDTO.setGrpProfileName(groupProfileIPDto.getNewGrpProfileName());
			} else {
				groupProfilePersistDTO.setGrpProfileName(corpgroupProfile.getProfileName());
			}
			if (groupProfileIPDto.getGrpAvatar() != null) {
				newAvatar = groupProfileIPDto.getGrpAvatar();
				groupProfilePersistDTO.setGrpAvatar(newAvatar);
				isXcapNotifyRequired = true;
				isGroupPropChanges = true;
			} else {
				groupProfilePersistDTO.setGrpAvatar(corpgroupProfile.getAvatar());
			}
			if (groupProfileIPDto.getGrpServiceType() != null) {
				groupProfilePersistDTO.setGrpServiceType(groupProfileIPDto.getGrpServiceType());
				isGroupPropChanges = true;
			} else {
				groupProfilePersistDTO.setGrpServiceType(corpgroupProfile.getServiceType());
			}
			if (groupProfileIPDto.getAudioCutIn() != null) {
				groupProfilePersistDTO.setAudioCutIn(groupProfileIPDto.getAudioCutIn());
				isGroupPropChanges = true;
			} else {
				groupProfilePersistDTO.setAudioCutIn(corpgroupProfile.getAudioCutIn());
			}
			if (groupProfileIPDto.getOverrideDND() != null) {
				groupProfilePersistDTO.setOverrideDND(groupProfileIPDto.getOverrideDND());
				isGroupPropChanges = true;
			} else {
				groupProfilePersistDTO.setOverrideDND(corpgroupProfile.getOverrideDnd());
			}
			// Handle group-level videoCallPermission
			if (groupProfileIPDto.getVideoCallPermission() != null) {
				groupProfilePersistDTO.setVideoCallPermission(groupProfileIPDto.getVideoCallPermission());
				isGroupPropChanges = true;
				isXcapNotifyRequired = true;
				knLogger.debug(methodName, "Group-level videoCallPermission set to:", groupProfileIPDto.getVideoCallPermission());
			} else {
				groupProfilePersistDTO.setVideoCallPermission(corpgroupProfile.getVideoCallPermission());
			}
			if (groupProfileIPDto.getGrpOSMListId() != null) {
				groupProfilePersistDTO.setGrpOSMListId(groupProfileIPDto.getGrpOSMListId());
				if (!groupProfileIPDto.getGrpOSMListId().equals(String.valueOf(corpgroupProfile.getOsmListId()))) {
					isOSMListModified = true;
					isXcapNotifyRequired = true;
					isGroupPropChanges = true;
				}
			} else {
				if(corpgroupProfile.getOsmListId()!=null && groupProfileIPDto.getGrpOSMListId() == null ) {
					knLogger.debug(methodName, "corpgroupProfile.getOsmListId() - ", corpgroupProfile.getOsmListId());
					groupProfilePersistDTO.setGrpOSMListId(groupProfileIPDto.getGrpOSMListId());
					isOSMListModified = true;
					isXcapNotifyRequired = true;
					isGroupPropChanges = true;
				}
			}
			if(!isHierarchyContext && groupProfileIPDto.getGrpShared()!=null && groupProfilePersistDTO.isGrpSharedChanged()){
				groupProfilePersistDTO.setGrpShared(groupProfileIPDto.getGrpShared());
				isGroupPropChanges = true;
				isGroupSharingChanged = true;
			}
			groupProfilePersistDTO.setFeatureAllowed(CORP_GROUP_DEFAULT_ALLOWED_FEATURE);
			long profileUpdateTime = Calendar.getInstance().getTimeInMillis();
			groupProfilePersistDTO.setUpdateTimeStamp(profileUpdateTime);
			groupProfilUtil.updateGroupProfile(groupProfilePersistDTO, xdmsHome, persisterTxn);
			knLogger.debug(methodName, "Group Profile updated");

			if (interopFlagCheck) {
				boolean isGroupProfileNotifyRequired = commonInfoUtil.isGroupProfileNotifyRequired(corpId, persisterTxn);
				if (isGroupProfileNotifyRequired) {
					//Update the UGWINTEROP
					commonInfoUtil.updateLmrInterOpInDB(corpId, ENABLED, persisterTxn);
				}
				respDTO.setLmrIntropCapable(ENABLED);

				Collection<KnSIPProxySvcConfigDTO> sipProxySvcConfigInfoDTO = commonInfoUtil.getSipProxySvcConfig(xdmsHome, persisterTxn);
				String groupSipUri = sipProxySvcConfigInfoDTO.stream()
						.filter(sipProxy -> sipProxy.getPttServerId().equals(corpPocHome)).findFirst()
						.map(KnSIPProxySvcConfigDTO::getSipProxyURI).orElse(null);
				respDTO.setGrpSIPUri(groupSipUri);
			}
			//Input shared corp List is not null then replace all shared corpList for the given in input group profile id
			//1. Fetch all the groups that are created with the profile Id and cleanup sharedCorpList from group level
			//2. delete sharedCorpInfo from groupProfile level
			//3. insert sharedCorpInfo at groupProfile level
			//4. insert sharedCorpInfo at group level
				if(!isHierarchyContext && groupProfileIPDto.getSharedCorpList() != null) {
				knLogger.debug(methodName,"groupProfileIPDto.getSharedCorpList() is not null");
				List<Integer> groups = new ArrayList<>(1);
				groups.addAll(allGroupMap.keySet());
				groupInfoUtil.deleteSharedGroups(groups, xdmsHome, persisterTxn);
				groupProfilUtil.deleteGroupProfileSharedInfo(corpgroupProfile.getProfileId(), xdmsHome, persisterTxn);
				if (!groupProfileIPDto.getSharedCorpList().isEmpty()) {
					knLogger.debug(methodName,"groupProfileIPDto.getSharedCorpList() is not empty");
				//	Map<Integer,Integer> groupSharedMap = new HashMap<>(1);
					groupProfilUtil.createGroupProfileSharedCorpInfo(groupProfilePersistDTO, xdmsHome, persisterTxn);
					List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList = new ArrayList<>();
					groups.forEach(groupId -> {
						KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
						groupPersistDTO.setCorpId(corpId);
						groupPersistDTO.setGroupId(groupId);
						groupPersistDTO.setCorpSharedCorpInfoList(groupProfileIPDto.getSharedCorpList());
						groupInfoPersistDTOList.add(groupPersistDTO);
						//groupSharedMap.put(groupId,groupProfileIPDto.getGrpShared());
					});
					groupInfoUtil.createGroupSharedCorpInfo(groupInfoPersistDTOList, xdmsHome, persisterTxn);
					setGroupSharedMapInResponse(groupProfileIPDto, respDTO, interopFlagCheck, allGroupMap);
				}
			}

			if (isGroupPropChanges) {
				// update groupinfo as per group profile param and etag.
					if (isHierarchyContext) {
						knLogger.info(methodName, "isHierarchyContext=true: preserving existing grpShared, not updating from request");
						groupProfilePersistDTO.setSkipGrpSharedUpdate(true);
					}
					groupInfoUtil.updateGroupInfoProperties(allGroupMap, groupProfilePersistDTO, xdmsHome, persisterTxn);
				knLogger.debug(methodName, "GROUP INFO updated");
				//MCS event for groupSharing flag enabled & disabled but group doesn't have any memeber

				if(isGroupSharingChanged) {
					setGroupSharedMapInResponse(groupProfileIPDto, respDTO, interopFlagCheck, allGroupMap);
				}

				if (isXcapNotifyRequired) {
                    //get group group member list for mcx group since the group member is not stored which evever members are present in group member list notifies will sent to that member only
                    Collection<KnCorpGroupMemberDTO> groupMembersList = groupInfoUtil.getCorpGroupMembersList(allGroupMap.keySet(), xdmsHome, persisterTxn);
					Map<String, List<Integer>> subscriberGrpListMap = groupMembersList.
							                                                stream().collect(Collectors.groupingBy(
							                                                		KnCorpGroupMemberDTO::getMdn,
							                                                        Collectors.mapping(KnCorpGroupMemberDTO::getGroupId,Collectors.toList())));

					knLogger.debug(methodName, "subscriberGrpListMap - ", KnGDPRTemplate.mapKeyMdn(subscriberGrpListMap));
					//update subscribers directory etag
					Map<String, KnOPDirChgDTO> etagMap = new HashMap<>();
					contactInfoUtil.updateDistinctSubcribersDirectory(subscriberGrpListMap.keySet(), null, etagMap, xdmsHome, persisterTxn);
					knLogger.debug(methodName, "etagMap -", KnGDPRTemplate.mapKeyMdn(etagMap));
					//prepare notification
					groupProfilUtil.formGroupPropChangeNotify(etagMap, subscriberGrpListMap, allGroupMap, newAvatar, isOSMListModified,
							KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
					knLogger.debug(methodName, "etagMap -", KnGDPRTemplate.mapKeyMdn(etagMap));
					respDTO.setChangeLogMap(etagMap);

					if(mcxGroupInd != null && mcxGroupInd.equals(MCX_GROUP_INDICATOR)){
						Set<Integer> groupIds = allGroupMap.keySet();
						List<String> profileMdnList=new ArrayList<>();
						knLogger.debug(methodName," updating etag for all group mdns groupIds",groupIds);

						for(Integer mcxGroupId:groupIds){
							Set<String> upmIds = corpUserProfileUtil.retriveGroupProfileInfoByGroupId(mcxGroupId, xdmsHome, persisterTxn).keySet();
							for(String userProfileId:upmIds){
								List<String> profileMdnListDB = corpUserProfileUtil.getProfileMdnByUPId(String.valueOf(corpId),userProfileId, xdmsHome, persisterTxn);
								profileMdnList.addAll(profileMdnListDB);
							}
						}
						if(!profileMdnList.isEmpty()){
							Set<String> completeMdns=new HashSet<>(profileMdnList);
							//get base mdn of the profile mdn
							Set<String> BaseMdns =corpSubsProvInfoUtil.getBaseMdnByProfileMdns(profileMdnList, xdmsHome, persisterTxn);
							completeMdns.addAll(BaseMdns);
							//updating ETAG in XDM_DIRECTORY for base mdn
							commonInfoUtil.getAndUpdateBulkDirectoryEtag(completeMdns, persisterTxn, xdmsHome);
							//updating LASTPROFILEUPDATETIME in POCSUBSCRINFO for base mdn
							commonInfoUtil.updateBulkSubsTS(completeMdns, persisterTxn, xdmsHome);
						}
					}
				}
				respDTO.setOldLmrInteropFlag(corpgroupProfile.getUgwInterop());
				respDTO.setMdnCorpId(corpId);
				if(mcxGroupInd!=null)
				   respDTO.setMcxGrpInd(mcxGroupInd);
			}
			populate(respDTO);
		} catch (KnValidationException e) {
			populate(respDTO, e);
			knLogger.error(methodName, "KnValidationException occurred", e);
		} catch (KnCorpBOException e) {
			populate(respDTO, e);
			knLogger.error(methodName, "KnCorpBOException occurred", e);
		} catch (Exception e) {
			populate(respDTO, e);
			knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
					com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
		}
		knLogger.debug(methodName, "Success response", respDTO);
		return respDTO;
	}

	/**
	 * Method to delete the list of groups Ids associated with the group profile
	 *
	 * @param groupProfileIPDto
	 * @param persisterTxn
	 * @return
	 */
	@Override
	public KnCorpResponseDTO deleteGrpProfileGroupList(KnIPDeleteBulkCorpGrpDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		String methodName = "deleteGrpProfileGroupList(KnIPCorpGorupProfileDTO , KnPersisterTxn )";
		KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
		KnCorpGroupProfilePersistDTO groupProfilePersistDTO = new KnCorpGroupProfilePersistDTO();
		final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
		knLogger.debug(methodName, "groupProfileIPDto - ", groupProfileIPDto);
		String xdmsHome = "";
		try {
			List<Integer> groupIdList = groupProfileIPDto.getGroupIdList();
			knLogger.debug(methodName, "Fetch the corpProfile profile");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + groupProfileIPDto.getCorpId(), CORP_PROFILE, false, persisterTxn);
			int corpId = corpProfile.getCorpId();
			xdmsHome = corpProfile.getXdmsHome();
			int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

			Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

			// validating group profile
			KnCorpGroupProfileInfo corpgroupProfile = groupProfilUtil.getGroupProfileDetail(groupProfileIPDto.getGrpProfileId(), groupProfileIPDto.getGrpProfileName(),
					groupProfileIPDto.getCorpId(), xdmsHome, persisterTxn);
			knLogger.debug(methodName, "Retrieved corp group profile details - ", corpgroupProfile);

			if (corpgroupProfile != null) {
				groupProfilePersistDTO.setProfileExist(true);
			}

			// get all groups
			Map<Integer, KnCorpGroupDTO> allGroupMap = new HashMap<>();
			if(corpgroupProfile != null){
                allGroupMap = groupInfoUtil.getGroupsByGrpProfileId(corpgroupProfile.getProfileId(),
                        groupProfileIPDto.getCorpId(), persisterTxn, xdmsHome);
            }
			knLogger.debug(methodName, "allGroupMap - ", allGroupMap);

			if((groupIdList == null || groupIdList.isEmpty()) && allGroupMap != null){
				groupIdList = new ArrayList<>(allGroupMap.keySet());
			}

			groupProfilePersistDTO.setInputDTO(groupProfileIPDto);
			groupProfilePersistDTO.setCorpProfile(corpProfile);
			groupProfilePersistDTO.setParamNameValueMapCommon(paramNameValueMapCommon);
			groupProfilePersistDTO.setOperationType(groupProfileIPDto.getOperationType());
			if(null != allGroupMap){
				groupProfilePersistDTO.setDbGroupList(new ArrayList<>(allGroupMap.keySet()));
			}
			groupProfilePersistDTO.setRequestGroupList(groupIdList);

			if(groupIdList != null){
				Collection<String> groupIdAsDestination = new ArrayList<>();
				groupIdList.forEach(id -> groupIdAsDestination.add(String.valueOf(id)));
				Map<String, Collection<String>> groupFailurePairingMap = corpSubsProvInfoUtil.getEmergDestUserMap(groupIdAsDestination,
						xdmsHome, persisterTxn);
				if (!groupFailurePairingMap.isEmpty()) {
					knLogger.error(methodName, "Emergency Destination Mapping Exists - Group");
					throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_GROUP,
							"Emergency Destination Mapping Exists for Group--", CORP_GROUP_MANAGER,
							DELETE_GROUP, "", groupFailurePairingMap.keySet().toString(), "");
				}
			}

			knLogger.debug(methodName, "Before Validation", groupProfilePersistDTO);
			validatorFW.validate(groupProfilePersistDTO);
			knLogger.debug(methodName, "Validation Successfull");

			if(groupIdList != null && !groupIdList.isEmpty()){

				//get group distribution list
				Map<String, List<Integer>> subscriberGrpListMap = groupInfoUtil.getSubscriberDistGroupList(new HashSet<>(groupIdList),
						xdmsHome, persisterTxn);
				knLogger.debug(methodName, "subscriberGrpListMap - ", subscriberGrpListMap);

				Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(groupIdList, xdmsHome, persisterTxn);
				knLogger.debug(methodName, "subsAddlTGList - ", subsAddlTGList);

				KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
				groupInfoUtil.cleanUpCampedGrp(groupIdList, corpInOutParamDTO, xdmsHome, persisterTxn);

				groupInfoUtil.cleanUpTGSSGrp(groupIdList, corpInOutParamDTO, xdmsHome, persisterTxn);

				Set<String> allMDNList = groupProfilUtil.getAllMdn(subscriberGrpListMap, subsAddlTGList, corpInOutParamDTO);
				knLogger.debug(methodName, "allMDNList - ", KnGDPRTemplate.mdnSet(allMDNList));
				//update subscribers directory etag
				Map<String, KnOPDirChgDTO> etagMap = new HashMap<>();
				contactInfoUtil.updateDistinctSubcribersDirectory(allMDNList, null, etagMap, xdmsHome, persisterTxn);

				if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
					Collection<String> mdnList = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
					groupInfoUtil.deleteSubsAddlTGList(subsAddlTGList, xdmsHome, persisterTxn);
					 groupProfilUtil.updateAddlTalkGroupTables(xdmsHome, mdnList, persisterTxn, etagMap);
					knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
					Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
					if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
						groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHome, persisterTxn);
					}
					knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
					if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
						etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, null,
								subsAddlTGList, groupCorpIdMap);
						knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
					}
				}

				knLogger.debug(methodName, "etagMap -", KnGDPRTemplate.mapKeyMdn(etagMap));
				knLogger.debug(methodName, "Preparing notification for TGSC List");
				etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHome, persisterTxn);

				knLogger.debug(methodName, "Preparing notification for TGSS List");
				etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHome, persisterTxn);

				//get the list of corporate where the groups are shared
				respDTO.setGroupIdSharedCorpListMap(groupInfoUtil.getGroupIsSharedCorpListMap(groupIdList, xdmsHome, persisterTxn));

				List<Integer> privateGrpListIds = new ArrayList<>();
				for(int grpid : groupIdList){
					privateGrpListIds.add(allGroupMap.get(grpid).getGrpMemListId());
				}
				groupInfoUtil.deleteBulkGroup(groupIdList, privateGrpListIds, xdmsHome, persisterTxn);

				//prepare notification
				groupProfilUtil.formGroupPropChangeNotify(etagMap, subscriberGrpListMap, allGroupMap, null, false,
						KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
				knLogger.debug(methodName, "etagMap -", KnGDPRTemplate.mapKeyMdn(etagMap));
				respDTO.setChangeLogMap(etagMap);
				respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
				LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateBulkDeleteGroupLIData(respDTO.getChangeLogMap(),
						xdmsHome, subscriberGrpListMap, allGroupMap);
				respDTO.setLiEventList(liEventList);
				respDTO.setMdnCorpId(corpId);
				respDTO.setGroupIds(groupIdList);
				respDTO.setGroupCreatedBy(CREATED_BY.CAT_ADMIN.value());
				respDTO.setPocHome(corpProfile.getPocHome());

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

	/**
	 * 1.Get the groupProfile details by name or ID if both name & ID exsists then ID is the prcedence if profile not exsists then reject the request
	 * 2.If groupProfile exsists then get no.of groups exsists for profileId if groupCount is greater than zero then reject the request
	 * 3.If validations are sucessful then deleteGroupProfile from DB
	 *
	 * @param groupProfileIPDto
	 * @param persisterTxn
	 * @return
	 */
	@Override
	public KnCorpResponseDTO deleteGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
		String methodName = "deleteGroupProfile(KnIPCorpGorupProfileDTO , KnPersisterTxn )";
		KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
		KnCorpGroupProfilePersistDTO groupProfilePersistDTO = new KnCorpGroupProfilePersistDTO();
		final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
		knLogger.info(methodName, "groupProfileIPDto - ", groupProfileIPDto);
		String xdmsHome = "";
		try {

			knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + groupProfileIPDto.getCorpId(), CORP_PROFILE, false, persisterTxn);
			xdmsHome = corpProfile.getXdmsHome();
			int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

			Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
			knLogger.debug(methodName, "Group profile management flag ", paramNameValueMapCommon.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.GROUP_PROFILE_MGMT));

			groupProfilePersistDTO.setInputDTO(groupProfileIPDto);
			groupProfilePersistDTO.setCorpProfile(corpProfile);
			groupProfilePersistDTO.setParamNameValueMapCommon(paramNameValueMapCommon);
			groupProfilePersistDTO.setOperationType(groupProfileIPDto.getOperationType());

			KnCorpGroupProfileInfo corpgroupProfile = groupProfilUtil.getGroupProfileDetail(groupProfileIPDto.getGrpProfileId(), groupProfileIPDto.getGrpProfileName(),
					groupProfileIPDto.getCorpId(), xdmsHome, persisterTxn);
			knLogger.debug(methodName, "Retrieved corp group profile details - ", corpgroupProfile);

			if (corpgroupProfile != null) {
				groupProfilePersistDTO.setProfileExist(true);

				List<String> grpProfiledList = new ArrayList<>();
				grpProfiledList.add(String.valueOf(corpgroupProfile.getProfileId()));

				Map<String, Integer> groupCountByGroupProfileId = groupProfilUtil.selectGroupCountByGroupProfileId(grpProfiledList, xdmsHome, persisterTxn);
				knLogger.debug(methodName, "groupCountByGroupProfileId - ", groupCountByGroupProfileId);
				groupProfilePersistDTO.setGroupCnt(groupCountByGroupProfileId.size());
			}

			knLogger.debug(methodName, "Before Validation", groupProfilePersistDTO);
			validatorFW.validate(groupProfilePersistDTO);
			knLogger.debug(methodName, "Validation Successfull");
			groupProfilUtil.deleteGroupProfileSharedInfo(corpgroupProfile.getProfileId(), xdmsHome, persisterTxn);
			groupProfilUtil.deleteGroupProfile(corpgroupProfile.getProfileId(), groupProfileIPDto.getCorpId(), xdmsHome, persisterTxn);
			knLogger.info(methodName, "GroupProfile Deleted");
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

	private void setGroupSharedMapInResponse(KnIPCorpGorupProfileDTO groupProfileIPDto, KnCorpResponseDTO respDTO, Boolean interopFlagCheck, Map<Integer, KnCorpGroupDTO> allGroupMap) {
		Map<Integer, KnCorpGroupInfoDTO> groupSharedMap = new HashMap<>(1);
		allGroupMap.values().forEach(group -> {
			KnCorpGroupInfoDTO groupInfoDTO = new KnCorpGroupInfoDTO();
			int groupId = group.getGroupId();
			groupInfoDTO.setGroupId(groupId);
			groupInfoDTO.setCorpId(group.getCorpId());
			groupInfoDTO.setETag(group.getETag()+1);
			groupInfoDTO.setGroupDisplayName(group.getGroupDisplayName());
			groupInfoDTO.setGroupProfileId(group.getGroupProfileId());
			groupInfoDTO.setGrpShared(groupProfileIPDto.getGrpShared());
			groupInfoDTO.setMcxGrpInd(group.getMcxGrpInd());
			groupInfoDTO.setGroupType(group.getGroupType());
			if (interopFlagCheck) {
				groupInfoDTO.setUgwInterop(group.getUgwInterop());
			}
			groupSharedMap.put(groupId, groupInfoDTO);
		});
		respDTO.setGroupSharedMap(groupSharedMap);
	}
}
