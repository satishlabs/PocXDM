/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserEmergencyAttributes;

import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * <p>
 * File name:  KnIPEmergencyInfoDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 19, 2017                9.0
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

public class KnIPEmergencyInfoDTO extends KnCorpUserEmergencyAttributes implements IInputDTO {

    private static final long serialVersionUID = 7526471134622676192L;

    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    private int clientType;
    private String profile;
    private String performer;
    private int corpId;
    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String mcptt_id;
    private boolean isUpmHierarchyCall;

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

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getMcptt_id() {
		return mcptt_id;
	}

	public void setMcptt_id(String mcptt_id) {
		this.mcptt_id = mcptt_id;
	}

    public boolean isUpmHierarchyCall() { return isUpmHierarchyCall; }

    public void setUpmHierarchyCall(boolean upmHierarchyCall) { isUpmHierarchyCall = upmHierarchyCall; }


    @Override
    public String toString() {
        return "KnIPEmergencyInfoDTO{" +
                "corpId=" + corpId +
                ", customParamMap=" + customParamMap +
                ", hierarchyType=" + hierarchyType +
                ", isUpmHierarchyCall=" + isUpmHierarchyCall +
                ", KnCorpUserEmergencyAttributes=" + super.toString() +
                '}';
    }
}
