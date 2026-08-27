/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnIPSubscriberInfoDTO.java
 * Subsystem:   Subscriber Management Lib
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       12/29/10   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.commdto.common.KnXDMSubsAliasInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAliasInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.intf.ISubscriberDTO;

import java.util.List;

public class KnIPSubscriberInfoDTO implements ISubscriberDTO {

    private static final long serialVersionUID = 7526471155622676227L;

    private String mdn;
    private List<String> mdnList;
    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private long ifMatch;
    private long ifNoneMatch;
    private String apnName;

    private String profile;
    private String aliasMdn;
    private String userId;
    private Integer corpId;
    private int pageId;
    private int maxPageSize;
    private String searchString;
    private String mcId;
    private String mcPttId;
   	private String mcVideoId;
   	private String mcDataId;
   	private List<String> mcDataIds;
    private List<String> mcPttIds;
    private List<String> userIds;
    private String mcPttIdFromToken;
	private String optStatusValue;
    private String extGatewayId;
    private List<KnSubsAliasInfoDTO> subsAliasInfoList;
    private List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList;
    private boolean upmCall;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private List<String> aliasMdnList;
    public String getExtGatewayId() {
        return extGatewayId;
    }

    public void setExtGatewayId(String extGatewayId) {
        this.extGatewayId = extGatewayId;
    }

    public List<String> getAliasMdnList() {
        return aliasMdnList;
    }

    public void setAliasMdnList(List<String> aliasMdnList) {
        this.aliasMdnList = aliasMdnList;
    }

    public String getMcPttIdFromToken() {
        return mcPttIdFromToken;
    }

    public void setMcPttIdFromToken(String mcPttIdFromToken) {
        this.mcPttIdFromToken = mcPttIdFromToken;
    }

    /**
     * getter method for Mdn
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for Mdn
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    /**
     * setter method for the Performer
     *
     * @param performer sets performer to subscriber
     */
    public void setPerformer(String performer) {
        if (performer != null) {
            performer = performer.trim();
            if (performer.equals("")) {
                performer = null;
            }
        }
        this.performer = performer;
    }

    /**
     * getter method for Performer
     *
     * @return String
     */
    public String getPerformer() {
        return performer;
    }

    /**
     * getter method for the Auth DTO
     *
     * @return IAuthDTO
     */
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    /**
     * setter method for the AuthDTO
     *
     * @param authDTO IAuth DTO
     */
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }


    /**
     * setter method for the clientType
     *
     * @param clientType int
     */
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    /**
     * getter method for the client Type
     *
     * @return int
     */
    public int getClientType() {
        return clientType;
    }

    /**
     * setter method for the Operation Type
     *
     * @param operationType the operation type
     */
    public void setOperationType(String operationType) {
        if (operationType != null) {
            operationType = operationType.trim();
            if (operationType.equals("")) {
                operationType = null;
            }
        }
        this.operationType = operationType;
    }

    /**
     * getter method for operation Type
     *
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * getter method for the Entity Id
     *
     * @return String
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * setter method for the entity Id
     *
     * @param entityId String
     */
    public void setEntityId(String entityId) {
        if (entityId != null) {
            entityId = entityId.trim();
            if (entityId.equals("")) {
                entityId = null;
            }
        }
        this.entityId = entityId;
    }

    public String getObjectId() {
        return mdn;
    }

    public String getProfile() {
        return profile;
    }


    public long getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(long ifMatch) {
        this.ifMatch = ifMatch;
    }

    public long getIfNoneMatch() {
        return ifNoneMatch;
    }

    public List<KnXDMSubsAliasInfoDTO> getAliasInfoDTOList() {
		return aliasInfoDTOList;
	}

	public void setAliasInfoDTOList(List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList) {
		this.aliasInfoDTOList = aliasInfoDTOList;
	}

	public void setIfNoneMatch(long ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

	public Integer getCorpId() {
		return corpId;
	}

	public void setCorpId(Integer corpId) {
		this.corpId = corpId;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public int getMaxPageSize() {
		return maxPageSize;
	}

	public void setMaxPageSize(int maxPageSize) {
		this.maxPageSize = maxPageSize;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public String getMcId() {
		return mcId;
	}

	public void setMcId(String mcId) {
		this.mcId = mcId;
	}

	public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	public String getMcVideoId() {
		return mcVideoId;
	}

	public void setMcVideoId(String mcVideoId) {
		this.mcVideoId = mcVideoId;
	}

	public String getMcDataId() {
		return mcDataId;
	}

	public void setMcDataId(String mcDataId) {
		this.mcDataId = mcDataId;
	}

    public List<String> getMcDataIds() {
        return mcDataIds;
    }

    public void setMcDataIds(List<String> mcDataIds) {
        this.mcDataIds = mcDataIds;
    }

	public String getOptStatusValue() {
        return optStatusValue;
    }

    public void setOptStatusValue(String optStatusValue) {
        this.optStatusValue = optStatusValue;
    }

    public List<String> getMcPttIds() {
        return mcPttIds;
    }

    public void setMcPttIds(List<String> mcPttIds) {
        this.mcPttIds = mcPttIds;
    }

    public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

    public List<KnSubsAliasInfoDTO> getSubsAliasInfoList() {
        return subsAliasInfoList;
    }

    public void setSubsAliasInfoList(List<KnSubsAliasInfoDTO> subsAliasInfoList) {
        this.subsAliasInfoList = subsAliasInfoList;
    }

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    @Override
    public String toString() {
        return "KnIPSubscriberInfoDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", mdnList=" + KnGDPRTemplate.mdnList(mdnList) +
                ", performer='" + performer + '\'' +
                ", authDTO=" + authDTO +
                ", clientType=" + clientType +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", ifMatch=" + ifMatch +
                ", ifNoneMatch=" + ifNoneMatch +
                ", apnName='" + apnName + '\'' +
                ", profile='" + profile + '\'' +
                ", aliasMdn='" + KnGDPRTemplate.mdn(aliasMdn) + '\'' +
                ", userId='" + KnGDPRTemplate.userId(userId) + '\'' +
                ", corpId='" + corpId + '\'' +
                ", pageId='" + pageId + '\'' +
                ", maxPageSize='" + maxPageSize + '\'' +
                ", searchString='" + searchString + '\'' +
                ", mcId='" + KnGDPRTemplate.mcId(mcId) + '\'' +
                ", mcPttId='" + KnGDPRTemplate.mcpttId(mcPttId) + '\'' +
                ", mcVideoId='" + KnGDPRTemplate.mcvideoId(mcVideoId) + '\'' +
                ", mcDataId='" + KnGDPRTemplate.mcdataId(mcDataId) + '\'' +
                ", mcDataIds='" + KnGDPRTemplate.mcDataIdList(mcDataIds) + '\'' +
                ", mcPttIds='" + KnGDPRTemplate.mcPttIdList(mcPttIds) + '\'' +
                ", userIds='" + KnGDPRTemplate.userIdList(userIds) + '\'' +
				", optStatusValue='" + optStatusValue + '\'' +
                ", mcPttIdFromToken='" + KnGDPRTemplate.mcpttId(mcPttIdFromToken) + '\'' +
                ", aliasMdnList='" + KnGDPRTemplate.mdnList(aliasMdnList) +'\''+
                ", subsAliasInfoList='" + subsAliasInfoList + '\'' +
                ", aliasInfoDTOList'='" + aliasInfoDTOList  + '\''+
                ", aliasMdnList='" + KnGDPRTemplate.mdnList(aliasMdnList) +'\''+
                ", isUpmCall'='" + isUpmCall()  + '\''+
                ", hierarchyType='" + hierarchyType + '\'' +
                '}';
    }

    public void validate() throws KnValidationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
