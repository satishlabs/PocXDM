/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupProfileInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class KnCorpGroupProfilePersistDTO implements IPersistenceDTO {

	private static final long serialVersionUID = 5597981255656162645L;
	private IInputDTO inputDTO;
	private String entityId;
	private String operationType;
	private String profile;
	private IPersistenceDTO persistenceDTO;

	private Integer corpId;
	private String grpProfileName;
	private Integer grpType;
	private Integer grpAvatar;
	private Integer grpServiceType;
	private String grpOSMListId;
	private Integer audioCutIn;
	private Integer mcxGroup;
	private Integer grpProfileId;
	private Integer startIndex;
	private Integer fetchSize;
	private Long createTimeStamp;
	private Long updateTimeStamp;
	private Integer grpProfileStatus;
	private Integer featureAllowed;
	private Map<String, String> paramNameValueMapCommon;
	private boolean isProfileExist;
	private KnCorpProfileDTO corpProfile;
	private Integer grpProfileCount;
	private Map<Integer, Integer> osmListIdAndDefultMap;
	private Integer overrideDND;
	private KnCorpGroupProfileInfo newGrpNmaeProfile;

	private List<Integer> requestGroupList;
	private List<Integer> dbGroupList;
	private int groupCnt;
	private boolean mcxGrpIndChanged;
	private boolean grpTypeChanged;
	private Integer grpShared;
	private List<KnCorpSharedCorpInfo> corpSharedCorpInfoList;
	private Map<String,Integer> extIntCorpIdMap;
	private List<KnCorpTrustMatrixDTO> trustMatrixDTOList;
	private boolean grpSharedChanged;
	private boolean skipGrpSharedUpdate;
	private Collection<KnCorpGroupMemberDTO> exsistingGroupMemberList;
	private String ugwInterop;
	private String ugwInteropSystemConfig;
	private Integer videoCallPermission;
    private String hierarchyId;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

	@Override
	public void setInputDTO(IInputDTO inputDTO) {
		setProfile(inputDTO.getProfile());
		setEntityId(inputDTO.getEntityId());
		setOperationType(inputDTO.getOperationType());
		this.inputDTO = inputDTO;
	}

	@Override
	public IInputDTO getInputDTO() {
		return inputDTO;
	}

	@Override
	public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
		this.persistenceDTO = persistenceDTO;
	}

	@Override
	public IPersistenceDTO getPersistenceDTO() {
		return persistenceDTO;
	}

	@Override
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	@Override
	public String getOperationType() {
		return operationType;
	}

	@Override
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	public String getObjectId() {
		return grpProfileName;
	}

	@Override
	public String getProfile() {
		return profile;
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Integer getCorpId() {
		return corpId;
	}

	public void setCorpId(Integer corpId) {
		this.corpId = corpId;
	}

	public String getGrpProfileName() {
		return grpProfileName;
	}

	public void setGrpProfileName(String grpProfileName) {
		this.grpProfileName = grpProfileName;
	}

	public Integer getGrpType() {
		return grpType;
	}

	public void setGrpType(Integer grpType) {
		this.grpType = grpType;
	}

	public Integer getGrpAvatar() {
		return grpAvatar;
	}

	public void setGrpAvatar(Integer grpAvatar) {
		this.grpAvatar = grpAvatar;
	}

	public Integer getGrpServiceType() {
		return grpServiceType;
	}

	public void setGrpServiceType(Integer grpServiceType) {
		this.grpServiceType = grpServiceType;
	}

	public String getGrpOSMListId() {
		return grpOSMListId;
	}

	public void setGrpOSMListId(String grpOSMListId) {
		this.grpOSMListId = grpOSMListId;
	}

	public Integer getAudioCutIn() {
		return audioCutIn;
	}

	public void setAudioCutIn(Integer audioCutIn) {
		this.audioCutIn = audioCutIn;
	}

	public Integer getMcxGroup() {
		return mcxGroup;
	}

	public void setMcxGroup(Integer mcxGroup) {
		this.mcxGroup = mcxGroup;
	}

	public Integer getGrpProfileId() {
		return grpProfileId;
	}

	public void setGrpProfileId(Integer grpProfileId) {
		this.grpProfileId = grpProfileId;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}

	public Long getCreateTimeStamp() {
		return createTimeStamp;
	}

	public void setCreateTimeStamp(Long createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	public Long getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	public void setUpdateTimeStamp(Long updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

	public Integer getGrpProfileStatus() {
		return grpProfileStatus;
	}

	public void setGrpProfileStatus(Integer grpProfileStatus) {
		this.grpProfileStatus = grpProfileStatus;
	}

	public Integer getFeatureAllowed() {
		return featureAllowed;
	}

	public void setFeatureAllowed(Integer featureAllowed) {
		this.featureAllowed = featureAllowed;
	}

	public Map<String, String> getParamNameValueMapCommon() {
		return paramNameValueMapCommon;
	}

	public void setParamNameValueMapCommon(Map<String, String> paramNameValueMapCommon) {
		this.paramNameValueMapCommon = paramNameValueMapCommon;
	}

	public boolean isProfileExist() {
		return isProfileExist;
	}

	public void setProfileExist(boolean isProfileExist) {
		this.isProfileExist = isProfileExist;
	}

	public KnCorpProfileDTO getCorpProfile() {
		return corpProfile;
	}

	public void setCorpProfile(KnCorpProfileDTO corpProfile) {
		this.corpProfile = corpProfile;
	}

	public Integer getGrpProfileCount() {
		return grpProfileCount;
	}

	public void setGrpProfileCount(Integer grpProfileCount) {
		this.grpProfileCount = grpProfileCount;
	}
	
	

	public Map<Integer, Integer> getOsmListIdAndDefultMap() {
		return osmListIdAndDefultMap;
	}

	public void setOsmListIdAndDefultMap(Map<Integer, Integer> osmListIdAndDefultMap) {
		this.osmListIdAndDefultMap = osmListIdAndDefultMap;
	}

	public Integer getOverrideDND() { return overrideDND; }

	public void setOverrideDND(Integer overrideDND) { this.overrideDND = overrideDND; }

	public KnCorpGroupProfileInfo getNewGrpNmaeProfile() {
		return newGrpNmaeProfile;
	}

	public void setNewGrpNmaeProfile(KnCorpGroupProfileInfo newGrpNmaeProfile) {
		this.newGrpNmaeProfile = newGrpNmaeProfile;
	}

	public List<Integer> getRequestGroupList() {
		return requestGroupList;
	}

	public void setRequestGroupList(List<Integer> requestGroupList) {
		this.requestGroupList = requestGroupList;
	}

	public List<Integer> getDbGroupList() {
		return dbGroupList;
	}

	public void setDbGroupList(List<Integer> dbGroupList) {
		this.dbGroupList = dbGroupList;
	}

	public int getGroupCnt() { return groupCnt; }

	public void setGroupCnt(int groupCnt) { this.groupCnt = groupCnt; }

	public boolean isMcxGrpIndChanged() {  return mcxGrpIndChanged; }

	public void setMcxGrpIndChanged(boolean mcxGrpIndChanged) { this.mcxGrpIndChanged = mcxGrpIndChanged; }

	public boolean isGrpTypeChanged() { return grpTypeChanged; }

	public void setGrpTypeChanged(boolean grpTypeChanged) { this.grpTypeChanged = grpTypeChanged; }

	public Integer getGrpShared() { return grpShared; }

	public void setGrpShared(Integer grpShared) { this.grpShared = grpShared; }

	public List<KnCorpSharedCorpInfo> getCorpSharedCorpInfoList() {
		return corpSharedCorpInfoList;
	}

	public void setCorpSharedCorpInfoList(List<KnCorpSharedCorpInfo> corpSharedCorpInfoList) {
		this.corpSharedCorpInfoList = corpSharedCorpInfoList;
	}

	public Map<String, Integer> getExtIntCorpIdMap() { return extIntCorpIdMap; }

	public void setExtIntCorpIdMap(Map<String, Integer> extIntCorpIdMap) { this.extIntCorpIdMap = extIntCorpIdMap; }

	public List<KnCorpTrustMatrixDTO> getTrustMatrixDTOList() { return trustMatrixDTOList; }

	public void setTrustMatrixDTOList(List<KnCorpTrustMatrixDTO> trustMatrixDTOList) { this.trustMatrixDTOList = trustMatrixDTOList; }

	public boolean isGrpSharedChanged() { return grpSharedChanged; }

	public void setGrpSharedChanged(boolean grpSharedChanged) { this.grpSharedChanged = grpSharedChanged; }

	public boolean isSkipGrpSharedUpdate() { return skipGrpSharedUpdate; }

	public void setSkipGrpSharedUpdate(boolean skipGrpSharedUpdate) { this.skipGrpSharedUpdate = skipGrpSharedUpdate; }

	public Collection<KnCorpGroupMemberDTO> getExsistingGroupMemberList() { return exsistingGroupMemberList; }

	public void setExsistingGroupMemberList(Collection<KnCorpGroupMemberDTO> exsistingGroupMemberList) { this.exsistingGroupMemberList = exsistingGroupMemberList; 	}

	public String getUgwInterop() {
		return ugwInterop;
	}

	public void setUgwInterop(String ugwInterop) {
		this.ugwInterop = ugwInterop;
	}

	public String getUgwInteropSystemConfig() {
		return ugwInteropSystemConfig;
	}

	public void setUgwInteropSystemConfig(String ugwInteropSystemConfig) {
		this.ugwInteropSystemConfig = ugwInteropSystemConfig;
	}

	public Integer getVideoCallPermission() {
		return videoCallPermission;
	}

	public void setVideoCallPermission(Integer videoCallPermission) {
		this.videoCallPermission = videoCallPermission;
	}

	@Override
	public String toString() {
		return "KnCorpGroupProfilePersistDTO [inputDTO=" + inputDTO + ", entityId=" + entityId + ", operationType="
				+ operationType + ", profile=" + profile + ", persistenceDTO=" + persistenceDTO + ", corpId=" + corpId
				+ ", grpProfileName=" + grpProfileName + ", grpType=" + grpType + ", grpAvatar=" + grpAvatar
				+ ", grpServiceType=" + grpServiceType + ", grpOSMListId=" + grpOSMListId + ", audioCutIn=" + audioCutIn
				+ ", mcxGroup=" + mcxGroup + ", grpProfileId=" + grpProfileId + ", startIndex=" + startIndex
				+ ", fetchSize=" + fetchSize + ", createTimeStamp=" + createTimeStamp + ", updateTimeStamp="
				+ updateTimeStamp + ", grpProfileStatus=" + grpProfileStatus + ", featureAllowed=" + featureAllowed
				+ ", isProfileExist=" + isProfileExist
				+ ", corpProfile=" + corpProfile + ", grpProfileCount=" + grpProfileCount + ", osmListIdAndDefultMap="
				+ osmListIdAndDefultMap + ", overrideDND="+ overrideDND + ", groupCnt="+ groupCnt +", mcxGrpIndChanged="+ mcxGrpIndChanged+
				", grpTypeChanged = "+grpTypeChanged+" , grpShared = "+grpShared+" , extIntCorpIdMap = "+extIntCorpIdMap
				
				+ " , grpSharedChanged = " + grpSharedChanged + " , ugwInterop = " + ugwInterop + " , ugwInteropSystemConfig = " + ugwInteropSystemConfig + " , hierarchyId = "+ hierarchyId + " , videoCallPermission = " + videoCallPermission + "]";
	}


}
