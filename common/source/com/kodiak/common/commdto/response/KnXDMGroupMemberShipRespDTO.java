/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Collection;
import java.util.Map;

import com.kodiak.common.commdto.common.KnCorpGroupContactDTO;

public class KnXDMGroupMemberShipRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 6984479254379540511L;

    private String responseCode;

    private int responseStatus;

    private String responseMessage;

    private Collection responseDetails;

    private String destPttServerId;

    private String destQueueName;

    private String transactionId;

    private Map<Integer,Integer> grpMemberShipMap;
    private Map<Integer, KnCorpGroupContactDTO > mcxGrpMemberShipMap;


    /**
     * getter method for clientdat code
     *
     * @return String
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * setter method for Response code
     *
     * @param responseCode String
     */
    public void setResponseCode(String responseCode) {
        if (responseCode != null) {
            responseCode = responseCode.trim();
            if (responseCode.equals("")) {
                responseCode = null;
            }
        }
        this.responseCode = responseCode;
    }

    /**
     * getter method for Response Status
     *
     * @return String
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * setter method for Response Status
     *
     * @param responseStatus String
     */
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * getter method for the Response Message
     *
     * @return String
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * setter method for the Response Message
     *
     * @param responseMessage String
     */
    public void setResponseMessage(String responseMessage) {
        if (responseMessage != null) {
            responseMessage = responseMessage.trim();
            if (responseMessage.equals("")) {
                responseMessage = null;
            }
        }
        this.responseMessage = responseMessage;
    }

    /**
     * getter method for the Response Details
     *
     * @return Collection
     */
    public Collection getResponseDetails() {
        return responseDetails;
    }

    /**
     * setter method for the Response Details
     *
     * @param responseDetails Collection
     */
    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails = responseDetails;
    }

    public String getObjectId() {
        return transactionId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }

    public Map<Integer, Integer> getGrpMemberShipMap() { return grpMemberShipMap; }

    public void setGrpMemberShipMap(Map<Integer, Integer> grpMemberShipMap) { this.grpMemberShipMap = grpMemberShipMap; }

    public Map<Integer, KnCorpGroupContactDTO > getMcxGrpMemberShipMap() {
		return mcxGrpMemberShipMap;
	}

	public void setMcxGrpMemberShipMap(Map<Integer, KnCorpGroupContactDTO > mcxGrpMemberShipMap) {
		this.mcxGrpMemberShipMap = mcxGrpMemberShipMap;
	}

	@Override
    public String toString() {
        return "KnXDMGroupMemberShipRespDTO{" +
                "responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", grpMemberShipMap=" + grpMemberShipMap +
                ", mcxGrpMemberShipMap=" + mcxGrpMemberShipMap +
                '}';
    }
}
