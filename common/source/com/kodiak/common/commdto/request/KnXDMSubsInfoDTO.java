/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMSubsInfoDTO.java
 * Subsystem:   Common DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/22/10       7.0
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
 * *******************************************************************************
 */

package com.kodiak.common.commdto.request;


import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMSubsAliasInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;
import java.util.Map;

public class KnXDMSubsInfoDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676143L;
    //stores the MDN
    private String mdn;

    private List<String> mdnList;

    //stores the Subscriber IMEI
    private String IMEI;

    private String operationType;
    private int clientType;

    private IAuthDTO authDTO;

    private String transactionId;

    private long ifMatch;
    private long ifNoneMatch;

    private Map<String, Object> customParamMap;

    private String destPttServerId;

    private String destQueueName;

    private String apnName;

    private String corpId;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private String aliasMdn;

    private String userId;

    private String version;

    private String mcId;

	private String mcPttId;

	private String mcVideoId;

	private String mcDataId;
    private String extGatewayId;

    private List<String> mcDataIds;
    private List<String> mcPttIds;
    private List<String> userIDs;
    private List<String> aliasMdnList;
    private List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList;

    private boolean isUpmCall;

	@Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
    }


    public List<String> getAliasMdnList() {
        return aliasMdnList;
    }

    public void setAliasMdnList(List<String> aliasMdnList) {
        this.aliasMdnList = aliasMdnList;
    }
    /**
     * getter method for the MDN
     *
     * @return String MDN
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for the MDN
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

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    /**
     * @return
     */
    public String getIMEI() {
        return IMEI;
    }

    /**
     * setter method for the IMEI
     *
     * @param IMEI String
     */
    public void setIMEI(String IMEI) {
        if (IMEI != null) {
            IMEI = IMEI.trim();
            if (IMEI.equals("")) {
                IMEI = null;
            }
        }
        this.IMEI = IMEI;
    }

    /**
     * getter method for Operation Type
     *
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * setter method for Operation Type
     *
     * @param operationType String
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
     * getter method for client Type
     *
     * @return int
     */
    public int getClientType() {
        return clientType;
    }

    /**
     * setter method for client Type
     *
     * @param clientType int
     */
    public void setClientType(int clientType) {
        this.clientType = clientType;
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
     * setter method for the Auth DTO
     *
     * @param authDTO IAuthDTO
     */
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    /**
     * @return
     */
    public long getIfMatch() {
        return ifMatch;
    }

    /**
     * @param ifMatch
     */
    public void setIfMatch(long ifMatch) {
        this.ifMatch = ifMatch;
    }

    /**
     * @return
     */
    public long getIfNoneMatch() {
        return ifNoneMatch;
    }

    /**
     * @param ifNoneMatch
     */
    public void setIfNoneMatch(long ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    /**
     * getter method for transaction ID
     *
     * @return String
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * setter method for Transaction Id
     *
     * @param transactionId String
     */
    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        if (destPttServerId != null) {
            destPttServerId = destPttServerId.trim();
            if (destPttServerId.equals("")) {
                destPttServerId = null;
            }
        }
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        if (destQueueName != null) {
            destQueueName = destQueueName.trim();
            if (destQueueName.equals("")) {
                destQueueName = null;
            }
        }
        this.destQueueName = destQueueName;
    }

    public String getObjectId() {
        return transactionId;
    }


    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public List<String> getMcPttIds() {
        return mcPttIds;
    }

    public void setMcPttIds(List<String> mcPttIds) {
        this.mcPttIds = mcPttIds;
    }

    public List<String> getUserIDs() {
		return userIDs;
	}

	public void setUserIDs(List<String> userIDs) {
		this.userIDs = userIDs;
	}

    public List<KnXDMSubsAliasInfoDTO> getAliasInfoDTOList() { return aliasInfoDTOList; }

    public void setAliasInfoDTOList(List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList) {
        this.aliasInfoDTOList = aliasInfoDTOList;
    }
    public String getExtGatewayId() {
        return extGatewayId;
    }

    public void setExtGatewayId(String extGatewayId) {
        this.extGatewayId = extGatewayId;
    }

    public boolean isUpmCall() { return isUpmCall; }

    public void setUpmCall(boolean upmCall) { isUpmCall = upmCall; }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append("MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", IMEI - ").append(IMEI)
                .append(", AuthDTO - ").append(authDTO)
                .append(", Operation_Type - ").append(operationType)
                .append(", Client_Type - ").append(clientType)
                .append(", If-Match - ").append(ifMatch)
                .append(", If-None-Match - ").append(ifNoneMatch)
                .append(", Transaction_ID - ").append(transactionId)
                .append(", Object_Id - ").append(getObjectId())
                .append(", apn NAme ").append(apnName)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", mdnList ").append(KnGDPRTemplate.mdnList(mdnList))
                .append(", corpId ").append(corpId)
                .append(", aliasMdn ").append(KnGDPRTemplate.mdn(aliasMdn))
                .append(", userId ").append(KnGDPRTemplate.userId(userId))
                .append(", version ").append(version)
                .append(", mcId - ").append(KnGDPRTemplate.mcId(mcId))
                .append(", mcPttId - ").append(KnGDPRTemplate.mcpttId(mcPttId))
                .append(", mcPttIds - ").append(KnGDPRTemplate.mcPttIdList(mcPttIds))
                .append(", mcVideoId - ").append(KnGDPRTemplate.mcvideoId(mcVideoId))
                .append(", mcDataId - ").append(KnGDPRTemplate.mcdataId(mcDataId))
                .append(", mcDataIds - ").append(KnGDPRTemplate.mcDataIdList(mcDataIds))
                .append(", userIDs - ").append(KnGDPRTemplate.userIdList(userIDs))
                .append(", aliasInfoDTOList - ").append(aliasInfoDTOList)
                .append(", aliasMdnList - ").append(KnGDPRTemplate.mdnList(aliasMdnList))
                .append(", extGatewayId - ").append(extGatewayId)
                .append(", isUpmCall - ").append(isUpmCall);

        return strBuffer.toString();
    }


}
