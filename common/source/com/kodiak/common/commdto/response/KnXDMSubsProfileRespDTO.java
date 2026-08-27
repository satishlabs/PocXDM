/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMSubsProfileRespDTO.java
 * Subsystem:   COMMON DTO
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

package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;

import java.util.Collection;
import java.util.Map;

public class KnXDMSubsProfileRespDTO extends KnXDMSubsProvDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622676164L;
    //stores the XDMS Home of the Subscriber
    public String XDMSHome;
    //stores the poC Home of the subscriber
    public String poCHome;
    //stores the Presence Home of the Subscriber
    public String presenceHome;
    //stores the Service Auth Status of the Subscriber
    public int serviceAuthStatus;

    public int internalCorpId;
    //stores the response Code
    private String responseCode;
    //stores the responseStatus
    private int responseStatus;
    //stores the response Message
    private String responseMessage;

    private Collection responseDetails;

    private String destPttServerId;

    private String destQueueName;

    private String transactionId;
    //stores User Agent
    private String userAgent;

    //Stores Subscriber creation Time
    private long subsCreationTime;

    private Collection<KnXDMSubsProvDTO> subsRespDTO;

    private Map<String,String> profileMdnUserProfileIdMap;

    /**
     * getter method for the XDMS Home
     *
     * @return String
     */
    public String getXDMSHome() {
        return XDMSHome;
    }

    /**
     * setter method for XDMS Home
     *
     * @param XDMSHome String
     */
    public void setXDMSHome(String XDMSHome) {
        if (XDMSHome != null) {
            XDMSHome = XDMSHome.trim();
            if (XDMSHome.equals("")) {
                XDMSHome = null;
            }
        }
        this.XDMSHome = XDMSHome;
    }

    /**
     * getter method for Poc Home of the subscriber
     *
     * @return String
     */
    public String getPoCHome() {
        return poCHome;
    }

    /**
     * setter method for Poc Home of the subscriber
     *
     * @param poCHome String
     */
    public void setPoCHome(String poCHome) {
        if (poCHome != null) {
            poCHome = poCHome.trim();
            if (poCHome.equals("")) {
                poCHome = null;
            }
        }
        this.poCHome = poCHome;
    }

    /**
     * getter method for Presence Home of the subscriber
     *
     * @return String
     */
    public String getPresenceHome() {
        return presenceHome;
    }

    /**
     * setter method for Presence Home of the subscriber
     *
     * @param presenceHome String
     */
    public void setPresenceHome(String presenceHome) {
        if (presenceHome != null) {
            presenceHome = presenceHome.trim();
            if (presenceHome.equals("")) {
                presenceHome = null;
            }
        }
        this.presenceHome = presenceHome;
    }

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

    /**
     * getter method for the Service Auth Status
     *
     * @return int
     */
    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    /**
     * setter method for the Service Auth Status
     *
     * @param serviceAuthStatus int
     */
    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }


    public int getInternalCorpId() {
        return internalCorpId;
    }

    public void setInternalCorpId(int internalCorpId) {
        this.internalCorpId = internalCorpId;
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getSubsCreationTime() {
        return subsCreationTime;
    }

    public void setSubsCreationTime(long subsCreationTime) {
        this.subsCreationTime = subsCreationTime;
    }

    public Collection<KnXDMSubsProvDTO> getSubsRespDTO() {
        return subsRespDTO;
    }

    public void setSubsRespDTO(Collection<KnXDMSubsProvDTO> subsRespDTO) {
        this.subsRespDTO = subsRespDTO;
    }

    public Map<String, String> getProfileMdnUserProfileIdMap() {
        return profileMdnUserProfileIdMap;
    }

    public void setProfileMdnUserProfileIdMap(Map<String, String> profileMdnUserProfileIdMap) {
        this.profileMdnUserProfileIdMap = profileMdnUserProfileIdMap;
    }

    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(", XDMS_HOME - ").append(XDMSHome)
                .append(", POC_HOME - ").append(poCHome)
                .append(", PRESENCE_HOME - ").append(presenceHome)
                .append(", Service_Auth_Status - ").append(serviceAuthStatus)
                .append(", Internal_Corp_ID - ").append(internalCorpId)
                .append(", User_Agent - ").append(userAgent)
                .append(", Subs_Creation_Time - ").append(subsCreationTime)
                .append(", Dest_PTT_Server_ID - ").append(destPttServerId)
                .append(", Dest_Queue_Name - ").append(destQueueName)
                .append(", Transaction_ID - ").append(transactionId)
                .append(", Response_Code - ").append(responseCode)
                .append(", Response_Status - ").append(responseStatus)
                .append(", Response_Details - ").append(responseDetails)
                .append(", subsRespDTO - ").append(subsRespDTO)
                .append(", profileMdnUserProfileIdMap - ").append(profileMdnUserProfileIdMap);
        return strBuffer.toString();
    }

}
