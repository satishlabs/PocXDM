/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by venkata sudhakar talluri on 28-12-2018
 */

public class KnTGSSListPersistDTO implements IPersistenceDTO {

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String profile;
    private String objectId;
    private Integer corpId;
    private String mdn;
    private Map<String, Integer> mdnCorpIdMap;
    private List<Integer> groupIdList;
    private String featureSet2;
    private int maxSDDSession;
    private int maxSDYSession;
    private List<String> memberList;
    private List<Integer> sharedCorpList;



    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
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

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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
        return objectId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public void setObjectId(String objectId) { this.objectId = objectId; }

    public Map<String, Integer> getMdnCorpIdMap() { return mdnCorpIdMap; }

    public void setMdnCorpIdMap(Map<String, Integer> mdnCorpIdMap) { this.mdnCorpIdMap = mdnCorpIdMap; }

    public List<Integer> getGroupIdList() { return groupIdList; }

    public void setGroupIdList(List<Integer> groupIdList) { this.groupIdList = groupIdList; }
    
    public String getFeatureSet2() {
		return featureSet2;
	}

	public void setFeatureSet2(String featureSet2) {
		this.featureSet2 = featureSet2;
	}

	public int getMaxSDDSession() { return maxSDDSession; }

    public void setMaxSDDSession(int maxSDDSession) { this.maxSDDSession = maxSDDSession; }

    public int getMaxSDYSession() { return maxSDYSession; }

    public void setMaxSDYSession(int maxSDYSession) { this.maxSDYSession = maxSDYSession; }

    public List<String> getMemberList() { return memberList; }

    public void setMemberList(List<String> memberList) { this.memberList = memberList; }

    public List<Integer> getSharedCorpList() { return sharedCorpList; }

    public void setSharedCorpList(List<Integer> sharedCorpList) { this.sharedCorpList = sharedCorpList; }

    @Override
    public String toString() {
        return "KnTGSSListPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", persistenceDTO=" + persistenceDTO +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", profile='" + profile + '\'' +
                ", objectId='" + objectId + '\'' +
                ", corpId=" + corpId +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", mdnCorpIdMap=" +KnGDPRTemplate.mapKeyMdn(mdnCorpIdMap) +
                ", groupIdList=" + groupIdList +
                ", featureSet2=" + featureSet2 +
                ", maxSDDSession=" + maxSDDSession +
                ", maxSDYSession=" + maxSDYSession +
                ", memberList=" + memberList +
                ", sharedCorpList=" + sharedCorpList +
                '}';
    }
}
