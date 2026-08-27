/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMSubsProvInfoDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/23/10       7.0
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
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.Map;

public class KnXDMSubsProvInfoDTO extends KnXDMSubsProvDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676144L;
    //stores the Client Type
    private int clientType;

    //stores the operation Type
    private String operationType;

    private String transactionId;

    private IAuthDTO authDTO;

    private long ifMatch;
    private long ifNoneMatch;

    private KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus;

    private String destPttServerId;

    private String destQueueName;
    private KnUserAgentDTO userAgentDTO;

    private String srcRoutingKey;

    private String srcQueueName;

    /**
     * stores the correlation Id that is used to correlate a response with a request
     */
    private String correlationId;

    /**
     * Stores the srcIPAddress of the original request machine
     */
    private String srcIPAddress;


    private Map<Integer,Integer> vocoderIdMap;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private String countryCode;

    private int subsDefPttRadio;

    private Map<Integer,Integer> provFSMap;
    
    private String  version;

    // This enum value corresponds to the client(custom code specific to client)
    private KnConstants.BUSINESS_CLIENT_TYPE businessClientType;

	/**
     * This method returns the correlationId of the message
     *
     * @return the correlationId
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * @return
     */
    public String getSrcIPAddress() {
        return srcIPAddress;
    }

    /**
     * @param srcIPAddress
     */
    public void setSrcIPAddress(String srcIPAddress) {
        this.srcIPAddress = srcIPAddress;
    }

    /**
     * This method sets the correlationId of the message
     *
     * @param correlationId the correlationId of the message
     */
    public void setCorrelationId(String correlationId) {
        if (correlationId != null) {
            correlationId = correlationId.trim();
            if (correlationId.equals("")) {
                correlationId = null;
            }
        }
        this.correlationId = correlationId;
    }

    /**
     * setter method for clientType
     *
     * @param clientType int
     */
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    /**
     * getter method for the AuthDTO Object
     *
     * @return IAuthDTO Object
     */
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    /**
     * setter method for the AuthDTO Object
     *
     * @param authDTO IAuthDTO
     */
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    /**
     * getter method for Client Type
     *
     * @return int
     */
    public int getClientType() {
        return clientType;
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
     * getter method for Operation Type
     *
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * getter method for Object Id
     *
     * @return String
     */
    public String getObjectId() {
        return transactionId;
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
     * getter method to for Service auth Status
     *
     * @return serviceAuthStatus
     */
    public KnConstants.SERVICE_AUTH_STATUS getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    /**
     * setter method for service auth status
     *
     * @param serviceAuthStatus KnConstants.SERVICE_AUTH_STATUS
     */
    public void setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
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

    public KnUserAgentDTO getUserAgentDTO() {
        return userAgentDTO;
    }

    public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
        this.userAgentDTO = userAgentDTO;
    }

    @Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

	//@Override
    public String getSrcRoutingKey() {
		return srcRoutingKey;
	}

    //@Override
	public void setSrcRoutingKey(String srcRoutingKey) {
		this.srcRoutingKey = srcRoutingKey;
	}

	public KnConstants.BUSINESS_CLIENT_TYPE getBusinessClientType() {
		return businessClientType;
	}

	public void setBusinessClientType(KnConstants.BUSINESS_CLIENT_TYPE businessClientType) {
		this.businessClientType = businessClientType;
	}

	//@Override
	public String getSrcQueueName() {
		return srcQueueName;
    }


	//@Override
	public void setSrcQueueName(String srcQueueName) {
		this.srcQueueName = srcQueueName;
	}


    public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
        this.vocoderIdMap = vocoderIdMap;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Map<Integer, Integer> getVocoderIdMap() {
        return vocoderIdMap;
    }
    
    public int getSubsDefPttRadio() {
		return subsDefPttRadio;
	}

	public void setSubsDefPttRadio(int subsDefPttRadio) {
		this.subsDefPttRadio = subsDefPttRadio;
	}

    public Map<Integer, Integer> getProvFSMap() {
        return provFSMap;
    }

    public void setProvFSMap(Map<Integer, Integer> provFSMap) {
        this.provFSMap = provFSMap;
    }

    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(super.toString());
        strBuffer.append(", ClientType - ").append(clientType)
                .append(", UserAgent_DTO - ").append(userAgentDTO)
                .append(", operation_Type - ").append(operationType)
                .append(", IAuthDTO_object - ").append(authDTO)
                .append(", If-Match - ").append(ifMatch)
                .append(", If-None-Match - ").append(ifNoneMatch)
                .append(", Dest_PTT_Server_ID - ").append(destPttServerId)
                .append(", Dest_Queue_Name - ").append(destQueueName)
                .append(", vocoderIdMap - ").append(vocoderIdMap)
                .append(", transaction_Id - ").append(transactionId)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", transaction_Id - ").append(transactionId)
                .append(", countryCode - ").append(countryCode)
        		.append(", srcRoutingKey - ").append(srcRoutingKey)
        		.append(", srcQueueName - ").append(srcQueueName)
                .append(", provFSMap - ").append(provFSMap)
        		.append(", subsDefPttRadio - ").append(subsDefPttRadio)
        		.append(", version - ").append(version)
                .append(", businessClientType - ").append(businessClientType);

        return strBuffer.toString();
    }
}
