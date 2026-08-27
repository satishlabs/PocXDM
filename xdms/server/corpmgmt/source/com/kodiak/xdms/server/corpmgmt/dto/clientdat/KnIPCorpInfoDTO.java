/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPCorpInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpInfoDTO;

import java.util.List;


public class KnIPCorpInfoDTO extends KnCorpInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676191L;

    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    private int clientType;
    private String profile;
    private String performer;
    private String eTag;
    private Integer filterType;
    private Integer nextToken;
    private Integer fetchSize;
    private Integer sortType;
    private Boolean enableAutoPair;
    private List<String> pttServerIds;
    private Integer startIndex;
    private String hierarchyId;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getClientType() {
        return clientType;
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
        return "" + getCorpId();
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
	public Integer getFilterType() {
		return filterType;
	}

	public void setFilterType(Integer filterType) {
		this.filterType = filterType;
	}

	public Integer getNextToken() {
		return nextToken;
	}

	public void setNextToken(Integer nextToken) {
		this.nextToken = nextToken;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}

	public Integer getSortType() {
		return sortType;
	}

	public void setSortType(Integer sortType) {
		this.sortType = sortType;
	}

    public Boolean getEnableAutoPair() {
        return enableAutoPair;
    }

    public void setEnableAutoPair(Boolean enableAutoPair) {
        this.enableAutoPair = enableAutoPair;
    }

    public List<String> getPttServerIds() {
        return pttServerIds;
    }

    public void setPttServerIds(List<String> pttServerIds) {
        this.pttServerIds = pttServerIds;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
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
                .append(", eTag - ").append(eTag)
                .append(", filterType - ").append(filterType)
                .append(", nextToken - ").append(nextToken)
                .append(", fetchSize - ").append(fetchSize)
                .append(", sortType - ").append(sortType)
                .append(", pttServerIds - ").append(pttServerIds);
        return sb.toString();
    }
}
