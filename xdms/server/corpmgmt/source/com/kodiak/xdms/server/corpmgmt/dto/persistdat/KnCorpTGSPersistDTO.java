/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     6/11/13         7.7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnCorpTGSPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155765647329L;

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private List<Integer> subsGroupList;
    private List<Integer> grpListInCorp;
    private boolean isSubsExternalCont;
    private boolean isSysTGSBitEanble;
    private boolean isTGSSerBitEanble;
    private List<Integer> alradyCampedGrpLst;
    private int subsCorpId;
    private int subsClientType;
    private int maxCampedGrpLmt;
    private boolean isTGSCorpAdminBitEanble;
    private Integer groupId;
    private String groupName;
    private Integer priority;
    private int maxPriority;
    private Integer channel;
    private int maxPttRadioScanGrpLmt;
    private int maxPttRadioChannelGrpLmt;
    private List<KnXDMTalkGroupInfoDTO> dbCampedGroups;
    private List<KnXDMTalkGroupInfoDTO> dbChannelGroups;
    private List<KnXDMTalkGroupInfoDTO> newCampedGroups;
    private List<KnXDMTalkGroupInfoDTO> newOnlyCampedGroups;
    private String etag;
    private Map<Integer,KnExtProfileDetails> configuredProfileMap;
    private List<Integer> inputProfileIdList;
    private int serviceAuthStatus;
    private int scanMode;
    private Set<Integer> reqGroupTypes;
    private boolean isSystemTgscanningClientBitEanble;
    private String allowedClientTypes;
    private Set<Integer> broadcastGrpIds;
    private Map<Integer,Integer> grpIdTypeMap;
    private Collection<KnCorpAddlTGInfoDTO> dbAddlTGList;
    private Collection<KnCorpAddlTGInfoDTO> newDbAddlTGList;
    private Collection<KnCorpAddlTGInfoDTO> sameZoneChannelList;
    private List<Integer> alradyAddlGrpLst;
    private Map<Integer, Collection<Integer>> zoneChannelMap;
    private int maxZone;
    private int maxChannelPerZone;
    private int clientMajorVersion;
    private Map<Integer, Collection<String>> bgMemberMap;
    private List<Integer> dbGrpsInScanList;
    private boolean priorityExists;
    private String activeFS2;
    private int allowGroupAcrossZones;
    private boolean calledFromModifyUPM;
    private boolean isUPMCall;
    private Integer skipChannelPerZoneValidation;
    private String mcPttId;
    private int mcpttCompliance;
    private boolean isUPMSharingEnabled;

    public boolean isUPMSharingEnabled() {
        return isUPMSharingEnabled;
    }

    public void setUPMSharingEnabled(boolean UPMSharingEnabled) {
        isUPMSharingEnabled = UPMSharingEnabled;
    }

    public String getMcPttId() {
		return mcPttId;
	}

	public int getMcpttCompliance() {
		return mcpttCompliance;
	}

	public void setMcpttCompliance(int mcpttCompliance) {
		this.mcpttCompliance = mcpttCompliance;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }
	
	public Map<Integer, Integer> getGrpIdTypeMap() {
		return grpIdTypeMap;
	}


	public void setGrpIdTypeMap(Map<Integer, Integer> grpIdTypeMap) {
		this.grpIdTypeMap = grpIdTypeMap;
	}

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getObjectId() {
        return null;
    }

    public List<Integer> getSubsGroupList() {
        return subsGroupList;
    }

    public void setSubsGroupList(List<Integer> subsGroupList) {
        this.subsGroupList = subsGroupList;
    }

    public boolean isSubsExternalCont() {
        return isSubsExternalCont;
    }

    public void setSubsExternalCont(boolean subsExternalCont) {
        isSubsExternalCont = subsExternalCont;
    }

    public boolean isSysTGSBitEanble() {
        return isSysTGSBitEanble;
    }

    public void setSysTGSBitEanble(boolean sysTGSBitEanble) {
        isSysTGSBitEanble = sysTGSBitEanble;
    }

    public boolean isTGSSerBitEanble() {
        return isTGSSerBitEanble;
    }

    public void setTGSSerBitEanble(boolean TGSSerBitEanble) {
        isTGSSerBitEanble = TGSSerBitEanble;
    }

    public List<Integer> getAlradyCampedGrpLst() {
        return alradyCampedGrpLst;
    }

    public void setAlradyCampedGrpLst(List<Integer> alradyCampedGrpLst) {
        this.alradyCampedGrpLst = alradyCampedGrpLst;
    }

    public int getSubsCorpId() {
        return subsCorpId;
    }

    public void setSubsCorpId(int subsCorpId) {
        this.subsCorpId = subsCorpId;
    }

    public int getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(int subsClientType) {
        this.subsClientType = subsClientType;
    }

    public int getMaxCampedGrpLmt() {
        return maxCampedGrpLmt;
    }

    public void setMaxCampedGrpLmt(int maxCampedGrpLmt) {
        this.maxCampedGrpLmt = maxCampedGrpLmt;
    }

    public List<Integer> getGrpListInCorp() {
        return grpListInCorp;
    }

    public void setGrpListInCorp(List<Integer> grpListInCorp) {
        this.grpListInCorp = grpListInCorp;
    }
    
    public boolean isTGSCorpAdminBitEanble() {
		return isTGSCorpAdminBitEanble;
	}

	public void setTGSCorpAdminBitEanble(boolean isTGSCorpAdminBitEanble) {
		this.isTGSCorpAdminBitEanble = isTGSCorpAdminBitEanble;
	}
	
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public int getMaxPriority() {
		return maxPriority;
	}

	public void setMaxPriority(int maxPriority) {
		this.maxPriority = maxPriority;
	}

	public List<KnXDMTalkGroupInfoDTO> getDbCampedGroups() {
		return dbCampedGroups;
	}

	public void setDbCampedGroups(List<KnXDMTalkGroupInfoDTO> dbCampedGroups) {
		this.dbCampedGroups = dbCampedGroups;
	}

	public List<KnXDMTalkGroupInfoDTO> getNewCampedGroups() {
		return newCampedGroups;
	}

	public void setNewCampedGroups(List<KnXDMTalkGroupInfoDTO> newCampedGroups) {
		this.newCampedGroups = newCampedGroups;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}


    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

	public Map<Integer, KnExtProfileDetails> getConfiguredProfileMap() {
        return configuredProfileMap;
    }

    public void setConfiguredProfileMap(Map<Integer, KnExtProfileDetails> configuredProfileMap) {
        this.configuredProfileMap = configuredProfileMap;
    }

    public List<Integer> getInputProfileIdList() {
        return inputProfileIdList;
    }

    public void setInputProfileIdList(List<Integer> inputProfileIdList) {
        this.inputProfileIdList = inputProfileIdList;
    }

    public int getServiceAuthStatus() {
		return serviceAuthStatus;
	}


	public void setServiceAuthStatus(int serviceAuthStatus) {
		this.serviceAuthStatus = serviceAuthStatus;
	}


	public int getScanMode() {
		return scanMode;
	}


	public void setScanMode(int scanMode) {
		this.scanMode = scanMode;
	}

    public boolean isSystemTgscanningClientBitEanble() { return isSystemTgscanningClientBitEanble; }

    public void setSystemTgscanningClientBitEanble(boolean isSystemTgscanningClientBitEanble) { this.isSystemTgscanningClientBitEanble = isSystemTgscanningClientBitEanble; }
	
    public Set<Integer> getReqGroupTypes() {
        return reqGroupTypes;
    }

    public void setReqGroupTypes(Set<Integer> reqGroupTypes) {
        this.reqGroupTypes = reqGroupTypes;
    }

    public String getAllowedClientTypes() {
        return allowedClientTypes;
    }

    public void setAllowedClientTypes(String allowedClientTypes) {
        this.allowedClientTypes = allowedClientTypes;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public int getMaxPttRadioScanGrpLmt() {
        return maxPttRadioScanGrpLmt;
    }

    public void setMaxPttRadioScanGrpLmt(int maxPttRadioScanGrpLmt) {
        this.maxPttRadioScanGrpLmt = maxPttRadioScanGrpLmt;
    }

    public int getMaxPttRadioChannelGrpLmt() {
        return maxPttRadioChannelGrpLmt;
    }

    public void setMaxPttRadioChannelGrpLmt(int maxPttRadioChannelGrpLmt) {
        this.maxPttRadioChannelGrpLmt = maxPttRadioChannelGrpLmt;
    }

    public List<KnXDMTalkGroupInfoDTO> getDbChannelGroups() {
        return dbChannelGroups;
    }

    public void setDbChannelGroups(List<KnXDMTalkGroupInfoDTO> dbChannelGroups) {
        this.dbChannelGroups = dbChannelGroups;
    }

    public List<KnXDMTalkGroupInfoDTO> getNewOnlyCampedGroups() {
        return newOnlyCampedGroups;
    }

    public void setNewOnlyCampedGroups(List<KnXDMTalkGroupInfoDTO> newOnlyCampedGroups) {
        this.newOnlyCampedGroups = newOnlyCampedGroups;
    }


    public Set<Integer> getBroadcastGrpIds() {
        return broadcastGrpIds;
    }

    public void setBroadcastGrpIds(Set<Integer> broadcastGrpIds) {
        this.broadcastGrpIds = broadcastGrpIds;
    }

    public Collection<KnCorpAddlTGInfoDTO> getDbAddlTGList() {
        return dbAddlTGList;
    }

    public void setDbAddlTGList(Collection<KnCorpAddlTGInfoDTO> dbAddlTGList) {
        this.dbAddlTGList = dbAddlTGList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getNewDbAddlTGList() {
        return newDbAddlTGList;
    }

    public void setNewDbAddlTGList(Collection<KnCorpAddlTGInfoDTO> newDbAddlTGList) {
        this.newDbAddlTGList = newDbAddlTGList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getSameZoneChannelList() {
        return sameZoneChannelList;
    }

    public void setSameZoneChannelList(Collection<KnCorpAddlTGInfoDTO> sameZoneChannelList) {
        this.sameZoneChannelList = sameZoneChannelList;
    }

    public List<Integer> getAlradyAddlGrpLst() {
        return alradyAddlGrpLst;
    }

    public void setAlradyAddlGrpLst(List<Integer> alradyAddlGrpLst) {
        this.alradyAddlGrpLst = alradyAddlGrpLst;
    }

    public Map<Integer, Collection<Integer>> getZoneChannelMap() {
        return zoneChannelMap;
    }

    public void setZoneChannelMap(Map<Integer, Collection<Integer>> zoneChannelMap) {
        this.zoneChannelMap = zoneChannelMap;
    }

    public int getMaxZone() {
        return maxZone;
    }

    public void setMaxZone(int maxZone) {
        this.maxZone = maxZone;
    }

    public int getMaxChannelPerZone() {
        return maxChannelPerZone;
    }

    public void setMaxChannelPerZone(int maxChannelPerZone) {
        this.maxChannelPerZone = maxChannelPerZone;
    }

    public int getClientMajorVersion() {
        return clientMajorVersion;
    }

    public void setClientMajorVersion(int clientMajorVersion) {
        this.clientMajorVersion = clientMajorVersion;
    }

    public Map<Integer, Collection<String>> getBgMemberMap() {
        return bgMemberMap;
    }

    public void setBgMemberMap(Map<Integer, Collection<String>> bgMemberMap) {
        this.bgMemberMap = bgMemberMap;
    }

    public List<Integer> getDbGrpsInScanList() {
        return dbGrpsInScanList;
    }

    public void setDbGrpsInScanList(List<Integer> dbGrpsInScanList) {
        this.dbGrpsInScanList = dbGrpsInScanList;
    }

    public boolean isPriorityExists() {
        return priorityExists;
    }

    public void setPriorityExists(boolean priorityExists) {
        this.priorityExists = priorityExists;
    }

    public int getAllowGroupAcrossZones() {
        return allowGroupAcrossZones;
    }

    public void setAllowGroupAcrossZones(int allowGroupAcrossZones) {
        this.allowGroupAcrossZones = allowGroupAcrossZones;
    }

    public boolean isCalledFromModifyUPM() {
		return calledFromModifyUPM;
	}

	public void setCalledFromModifyUPM(boolean calledFromModifyUPM) {
		this.calledFromModifyUPM = calledFromModifyUPM;
	}

    public boolean isUPMCall() {
        return isUPMCall;
    }

    public void setUPMCall(boolean UPMCall) {
        isUPMCall = UPMCall;
    }

    public Integer getSkipChannelPerZoneValidation() {
        return skipChannelPerZoneValidation;
    }

    public void setSkipChannelPerZoneValidation(Integer skipChannelPerZoneValidation) {
        this.skipChannelPerZoneValidation = skipChannelPerZoneValidation;
    }

    @Override
    public String toString() {
        return "KnCorpTGSPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", persistenceDTO=" + persistenceDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", subsGroupList=" + subsGroupList +
                ", grpListInCorp=" + grpListInCorp +
                ", isSubsExternalCont=" + isSubsExternalCont +
                ", isSysTGSBitEanble=" + isSysTGSBitEanble +
                ", isTGSSerBitEanble=" + isTGSSerBitEanble +
                ", alradyCampedGrpLst=" + alradyCampedGrpLst +
                ", subsCorpId=" + subsCorpId +
                ", subsClientType=" + subsClientType +
                ", maxCampedGrpLmt=" + maxCampedGrpLmt +
                ", isTGSCorpAdminBitEanble=" + isTGSCorpAdminBitEanble +
                ", groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", priority=" + priority +
                ", maxPriority=" + maxPriority +
                ", channel=" + channel +
                ", maxPttRadioScanGrpLmt=" + maxPttRadioScanGrpLmt +
                ", maxPttRadioChannelGrpLmt=" + maxPttRadioChannelGrpLmt +
                ", dbCampedGroups=" + dbCampedGroups +
                ", dbChannelGroups=" + dbChannelGroups +
                ", newCampedGroups=" + newCampedGroups +
                ", newOnlyCampedGroups=" + newOnlyCampedGroups +
                ", etag='" + etag + '\'' +
                ", activeFS2=" + activeFS2 +
                ", configuredProfileMap=" + configuredProfileMap +
                ", inputProfileIdList=" + inputProfileIdList +
                ", serviceAuthStatus=" + serviceAuthStatus +
                ", scanMode=" + scanMode +
                ", reqGroupTypes=" + reqGroupTypes +
                ", isSystemTgscanningClientBitEanble=" + isSystemTgscanningClientBitEanble +
                ", allowedClientTypes='" + allowedClientTypes + '\'' +
                ", broadcastGrpIds=" + broadcastGrpIds +
                ", grpIdTypeMap=" + grpIdTypeMap +
                ", dbAddlTGList=" + dbAddlTGList +
                ", newDbAddlTGList=" + newDbAddlTGList +
                ", sameZoneChannelList=" + sameZoneChannelList +
                ", alradyAddlGrpLst=" + alradyAddlGrpLst +
                ", zoneChannelMap=" + zoneChannelMap +
                ", maxZone=" + maxZone +
                ", maxChannelPerZone=" + maxChannelPerZone +
                ", clientMajorVersion=" + clientMajorVersion +
                ", bgMemberMap=" + bgMemberMap +
                ", dbGrpsInScanList=" + dbGrpsInScanList +
                ", priorityExists=" + priorityExists +
                ", allowGroupAcrossZones=" + allowGroupAcrossZones +
                ", calledFromModifyUPM=" + calledFromModifyUPM +
                ", isUPMCall=" + isUPMCall +
                ", skipChannelPerZoneValidation=" + skipChannelPerZoneValidation +
                ", isUPMSharingEnabled=" + isUPMSharingEnabled +
                '}';
    }
}
