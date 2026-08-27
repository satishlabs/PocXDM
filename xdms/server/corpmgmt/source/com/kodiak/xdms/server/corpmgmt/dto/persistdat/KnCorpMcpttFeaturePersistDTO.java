/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.Map;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpMcpttFeaturePersistDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnCorpMcpttFeaturePersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676223L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;
    private String authorizedMdn;
    private int corpId;
    private int subsClientType;
    private Map<String, KnCorpSubscriberDTO> targetInfo;
    private Collection<String> targetNotInContactListOfAuthMdn;
    private Collection<String> targetMdnDoNotHaveAuthMdnAsContact;
    private String mdn;
    private boolean emergFeature;
    private boolean ambientListening;
    private boolean discreteListening;
    private boolean userCheck;
    private boolean userSvcCtrl;
    private Map<String, Integer> groupTypeMap;
    private Collection<String> destinations;
    private Map<String, String> mdnExistenceInGroup;
    private Map<String, Collection<String>> subsContactList;
    private Collection<Long> targetBits;
    private int serviceAuthStatus;
    private String activeFS2;
    private String subsFS2;
    private boolean mcVideoFeature;
    private boolean mcVideoUnCfrmPullFeature;
    private String mcpttId;
    private int mcpttCompliance;
    private boolean isUPMSharingEnabled;

    public boolean isUPMSharingEnabled() {
        return isUPMSharingEnabled;
    }

    public void setUPMSharingEnabled(boolean UPMSharingEnabled) {
        isUPMSharingEnabled = UPMSharingEnabled;
    }

    public int getMcpttCompliance() {
		return mcpttCompliance;
	}

	public void setMcpttCompliance(int mcpttCompliance) {
		this.mcpttCompliance = mcpttCompliance;
	}

	public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public String getObjectId() {
        return authorizedMdn;
    }

    public String getAuthorizedMdn() {
        return authorizedMdn;
    }

    public void setAuthorizedMdn(String authorizedMdn) {
        this.authorizedMdn = authorizedMdn;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(int subsClientType) {
        this.subsClientType = subsClientType;
    }

    public Map<String, KnCorpSubscriberDTO> getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(Map<String, KnCorpSubscriberDTO> targetInfo) {
        this.targetInfo = targetInfo;
    }

    public Collection<String> getTargetNotInContactListOfAuthMdn() {
        return targetNotInContactListOfAuthMdn;
    }

    public void setTargetNotInContactListOfAuthMdn(Collection<String> targetNotInContactListOfAuthMdn) {
        this.targetNotInContactListOfAuthMdn = targetNotInContactListOfAuthMdn;
    }

    public Collection<String> getTargetMdnDoNotHaveAuthMdnAsContact() {
        return targetMdnDoNotHaveAuthMdnAsContact;
    }

    public void setTargetMdnDoNotHaveAuthMdnAsContact(Collection<String> targetMdnDoNotHaveAuthMdnAsContact) {
        this.targetMdnDoNotHaveAuthMdnAsContact = targetMdnDoNotHaveAuthMdnAsContact;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public boolean isEmergFeature() {
        return emergFeature;
    }

    public void setEmergFeature(boolean emergFeature) {
        this.emergFeature = emergFeature;
    }

    public boolean isAmbientListening() {
        return ambientListening;
    }

    public void setAmbientListening(boolean ambientListening) {
        this.ambientListening = ambientListening;
    }

    public boolean isDiscreteListening() {
        return discreteListening;
    }

    public void setDiscreteListening(boolean discreteListening) {
        this.discreteListening = discreteListening;
    }

    public boolean isUserCheck() {
        return userCheck;
    }

    public void setUserCheck(boolean userCheck) {
        this.userCheck = userCheck;
    }

    public boolean isUserSvcCtrl() {
        return userSvcCtrl;
    }

    public void setUserSvcCtrl(boolean userSvcCtrl) {
        this.userSvcCtrl = userSvcCtrl;
    }

    public Map<String, Integer> getGroupTypeMap() {
        return groupTypeMap;
    }

    public void setGroupTypeMap(Map<String, Integer> groupTypeMap) {
        this.groupTypeMap = groupTypeMap;
    }

    public Collection<String> getDestinations() {
        return destinations;
    }

    public void setDestinations(Collection<String> destinations) {
        this.destinations = destinations;
    }

    public Map<String, String> getMdnExistenceInGroup() {
        return mdnExistenceInGroup;
    }

    public void setMdnExistenceInGroup(Map<String, String> mdnExistenceInGroup) {
        this.mdnExistenceInGroup = mdnExistenceInGroup;
    }

    public Map<String, Collection<String>> getSubsContactList() {
        return subsContactList;
    }

    public void setSubsContactList(Map<String, Collection<String>> subsContactList) {
        this.subsContactList = subsContactList;
    }

    public Collection<Long> getTargetBits() {
        return targetBits;
    }

    public void setTargetBits(Collection<Long> targetBits) {
        this.targetBits = targetBits;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

	public String getSubsFS2() {
		return subsFS2;
	}

	public void setSubsFS2(String subsFS2) {
		this.subsFS2 = subsFS2;
	}

    public boolean isMcVideoFeature() {
        return mcVideoFeature;
    }

    public void setMcVideoFeature(boolean mcVideoFeature) {
        this.mcVideoFeature = mcVideoFeature;
    }

    public boolean isMcVideoUnCfrmPullFeature() {
        return mcVideoUnCfrmPullFeature;
    }

    public void setMcVideoUnCfrmPullFeature(boolean mcVideoUnCfrmPullFeature) {
        this.mcVideoUnCfrmPullFeature = mcVideoUnCfrmPullFeature;
    }

    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

	@Override
	public String toString() {
		return "KnCorpMcpttFeaturePersistDTO [inputDTO=" + inputDTO + ", entityId=" + entityId + ", operationType="
				+ operationType + ", profile=" + profile + ", persistenceDTO=" + persistenceDTO + ", authorizedMdn="
				+ KnGDPRTemplate.mdn(authorizedMdn) + ", corpId=" + corpId + ", subsClientType=" + subsClientType + ", targetInfo="
				+ targetInfo + ", targetNotInContactListOfAuthMdn=" + KnGDPRTemplate.mdnList(targetNotInContactListOfAuthMdn)
				+ ", targetMdnDoNotHaveAuthMdnAsContact=" + KnGDPRTemplate.mdnList(targetMdnDoNotHaveAuthMdnAsContact) + ", mdn=" + KnGDPRTemplate.mdn(mdn)
				+ ", emergFeature=" + emergFeature + ", ambientListening=" + ambientListening + ", discreteListening="
				+ discreteListening + ", userCheck=" + userCheck + ", userSvcCtrl=" + userSvcCtrl + ", groupTypeMap="
				+ groupTypeMap + ", destinations=" + destinations + ", mdnExistenceInGroup=" + mdnExistenceInGroup
				+ ", subsContactList=" + subsContactList + ", targetBits=" + targetBits + ", serviceAuthStatus="
				+ serviceAuthStatus + ", activeFS2=" + activeFS2 + ", subsFS2=" + subsFS2 + ", mcVideoFeature="
				+ mcVideoFeature + ", mcVideoUnCfrmPullFeature=" + mcVideoUnCfrmPullFeature + ", mcpttId=" + KnGDPRTemplate.mcpttId(mcpttId)
				+ ", mcpttCompliance=" + mcpttCompliance
                + ", isUPMSharingEnabled=" + isUPMSharingEnabled +"]";
	}
}
