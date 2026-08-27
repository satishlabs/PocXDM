/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPSubsProfileInfoDTO.java
 * Subsystem:   Subscriber Provisioning Lib
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/29/10       7.0
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
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.dto.KnPayloadIP;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.dto.intf.IOutputDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnOPSubsProfileInfoDTO extends KnSubsProfileDTO implements IOutputDTO {

    private static final long serialVersionUID = 7526471155622676236L;
    //stores the clientdat code
    private String responseCode;
    //stores the clientdat Status
    private int responseStatus;
    //stores the clientdat Message
    private String responseMessage;
    //stores the DTO Status
    private int DTOStatus;
    //stores the error Object
    private KnXDMError errorObject;

    private KnOPDirChgDTO dirChgtDTO;
    
    private List<KnOPDirChgDTO> dirChgDTOs;
    
    private Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap;
    
    private Map<String, String> mcsXcapRootUriMap;

    private Map<String, KnSubsProfileDTO> subsRespMap;

    private Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> subsRespMapCommon;
    
    private Integer totalPages;
    private Integer pageSize;

    private boolean performPrivacyOperation;

    private Integer privacyValueToExecute;

    private Map<String,String> profileMdnUserProfileIdMap;

    private String oldActiveFS;

    private int sendAccountMail;

    private Integer previousServiceAuthStatus;

    private KnPayloadIP knPayloadIP;

    /**
     * getter method for the document DTO
     *
     * @return KnOPDocumentDTO Object
     */
    public KnOPDirChgDTO getDirChgtDTO() {
        return dirChgtDTO;
    }

    /**
     * setter method for the document DTO
     *
     * @param dirChgtDTO KnOPDocumentDTO
     */
    public void setDirChgtDTO(KnOPDirChgDTO dirChgtDTO) {
        this.dirChgtDTO = dirChgtDTO;
    }

    /**
     * getter method for the Response Code
     *
     * @return String
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * setter method for the Response Code
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
     * getter method for the clientdat Status
     *
     * @return int
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * setter method for the clientdat status
     *
     * @param responseStatus int
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
     * getter method for the DTO Status
     *
     * @return int
     */
    public int getDTOStatus() {
        return DTOStatus;
    }

    /**
     * setter method for the DTO Status
     *
     * @param dtoStatus int
     */
    public void setDTOStatus(int dtoStatus) {
        this.DTOStatus = dtoStatus;
    }

    /**
     * getter method for the Error Object
     *
     * @return KnErrorObject Object
     */
    public KnXDMError getErrorObject() {
        return errorObject;
    }

    /**
     * setter method for the Error Object
     *
     * @param errorObj KnXDMError Object
     */
    public void setErrorObject(KnXDMError errorObj) {
        this.errorObject = errorObj;
    }

    public String getObjectId() {
        return this.getMdn();
    }

    public void populate(IPopulate dtoObject) {
    }
    public Map<String, KnSubsProfileDTO> getSubsRespMap() {
        return subsRespMap;
    }

    public void setSubsRespMap(Map<String, KnSubsProfileDTO> subsRespMap) {
        this.subsRespMap = subsRespMap;
    }

    public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

    public boolean isPerformPrivacyOperation() {
        return performPrivacyOperation;
    }

    public void setPerformPrivacyOperation(boolean performPrivacyOperation) {
        this.performPrivacyOperation = performPrivacyOperation;
    }

    public Integer getPrivacyValueToExecute() {
        return privacyValueToExecute;
    }

    public void setPrivacyValueToExecute(Integer privacyValueToExecute) {
        this.privacyValueToExecute = privacyValueToExecute;
    }
    
    public List<KnOPDirChgDTO> getDirChgDTOs() {
		return dirChgDTOs;
	}

	public void setDirChgDTOs(List<KnOPDirChgDTO> dirChgDTOs) {
		this.dirChgDTOs = dirChgDTOs;
	}
	
	 public Map<String, Collection<KnDocChangeListDTO>> getProfileMdnEtagMap() {
			return profileMdnEtagMap;
		}

		public void setProfileMdnEtagMap(Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap) {
			this.profileMdnEtagMap = profileMdnEtagMap;
		}
		
		public Map<String, String> getMcsXcapRootUriMap() {
			return mcsXcapRootUriMap;
		}

		public void setMcsXcapRootUriMap(Map<String, String> mcsXcapRootUriMap) {
			this.mcsXcapRootUriMap = mcsXcapRootUriMap;
		}

    public Map<String, String> getProfileMdnUserProfileIdMap() {
        return profileMdnUserProfileIdMap;
    }

    public void setProfileMdnUserProfileIdMap(Map<String, String> profileMdnUserProfileIdMap) {
        this.profileMdnUserProfileIdMap = profileMdnUserProfileIdMap;
    }

    public String getOldActiveFS() {
        return oldActiveFS;
    }

    public void setOldActiveFS(String oldActiveFS) {
        this.oldActiveFS = oldActiveFS;
    }

    public int getSendAccountMail() {
        return sendAccountMail;
    }

    public void setSendAccountMail(int sendAccountMail) {
        this.sendAccountMail = sendAccountMail;
    }

    public Integer getPreviousServiceAuthStatus() {
        return previousServiceAuthStatus;
    }

    public void setPreviousServiceAuthStatus(Integer previousServiceAuthStatus) {
        this.previousServiceAuthStatus = previousServiceAuthStatus;
    }

    public KnPayloadIP getKnPayloadIP() { return knPayloadIP; }

    public void setKnPayloadIP(KnPayloadIP knPayloadIP) { this.knPayloadIP = knPayloadIP; }

    public Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> getSubsRespMapCommon() {
        return subsRespMapCommon;
    }

    public void setSubsRespMapCommon(Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> subsRespMapCommon) {
        this.subsRespMapCommon = subsRespMapCommon;
    }

    public String toString() {
    	StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(super.toString());
        strBuffer.append("KnOPSubsProfileInfoDTO{" +
                "responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", DTOStatus=" + DTOStatus +
                ", errorObject=" + errorObject +
                ", dirChgtDTO=" + dirChgtDTO +
                ", subsRespMap=" + KnGDPRTemplate.mapKeyMdn(subsRespMap) +
                ", totalPages=" + totalPages +
                ", pageSize=" + pageSize +
                ", performPrivacyOperation=" + performPrivacyOperation +
                ", privacyValueToExecute=" + privacyValueToExecute +
                ", dirChgDTOs=" + dirChgDTOs +
                ", mcsXcapRootUriMap=" + mcsXcapRootUriMap +
                ", profileMdnEtagMap=" + profileMdnEtagMap +
                ", profileMdnUserProfileIdMap=" + profileMdnUserProfileIdMap +
                ", oldActiveFS=" + oldActiveFS +
                ", sendAccountMail=" + sendAccountMail +
                ", previousServiceAuthStatus=" + previousServiceAuthStatus +
                ", knPayloadIP=" + knPayloadIP +
                '}');
        return strBuffer.toString();
    } 
}
