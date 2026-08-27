/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.commdto.common.KnXDMSubsAliasInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubscrFeatureInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by asanjiv on 11/3/2016.
 */
public class KnIPSubscrFeatureInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676191L;

    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    private int clientType;
    private String profile;
    private String performer;

    private int corpId;
    private List<KnSubscrFeatureInfoDTO> subscrFeatureInfoDTOList;
    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private KnSubscrFeatureInfoDTO subsDetailsDTO;
    private List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList;

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
    }


    public List<KnSubscrFeatureInfoDTO> getSubscrFeatureInfoDTOList() {
        return subscrFeatureInfoDTOList;
    }

    public void setSubscrFeatureInfoDTOList(List<KnSubscrFeatureInfoDTO> subscrFeatureInfoDTOList) {
        this.subscrFeatureInfoDTOList = subscrFeatureInfoDTOList;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
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
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    @Override
    public void setPerformer(String performer) {
        this.performer = performer;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    public List<KnXDMSubsAliasInfoDTO> getAliasInfoDTOList() {
		return aliasInfoDTOList;
	}

	public void setAliasInfoDTOList(List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList) {
		this.aliasInfoDTOList = aliasInfoDTOList;
	}

	public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public KnSubscrFeatureInfoDTO getSubsDetailsDTO() {
        return subsDetailsDTO;
    }

    public void setSubsDetailsDTO(KnSubscrFeatureInfoDTO subsDetailsDTO) {
        this.subsDetailsDTO = subsDetailsDTO;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
                .append(", AuthDTO - ").append(authDTO)
                .append(", EntityId - ").append(entityId)
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", Profile - ").append(profile)
                .append(", Performer - ").append(performer)
                .append(", corpId - ").append(corpId)
                .append(", subscrFeatureInfoDTOList - ").append(subscrFeatureInfoDTOList)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", subsDetailsDTO - ").append(subsDetailsDTO)
                .append(", customParamMap - ").append(customParamMap);
        return sb.toString();
    }
}
