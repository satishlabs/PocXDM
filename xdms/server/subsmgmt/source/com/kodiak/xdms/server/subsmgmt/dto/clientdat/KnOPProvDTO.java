/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPProvDTO.java
 * Subsystem:   Subscriber Management Lib
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

import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.dto.intf.IOutputDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpGwLinkedAccInfoDTO;

import java.util.Collection;
import java.util.List;

public class KnOPProvDTO implements IOutputDTO {

    private static final long serialVersionUID = 7526471155622676234L;
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

    //stores the notification Object
    KnOPDirChgDTO dirChgDTO;
    private List<KnOPDirChgDTO> dirChgDTOs;

    private String pocHome;
    private String presHome;

    private int aliasNGroupMDNCount;

    private List<String> mdnList;

    public String getCorpNNIRefId() {
        return corpNNIRefId;
    }

    public void setCorpNNIRefId(String corpNNIRefId) {
        this.corpNNIRefId = corpNNIRefId;
    }

    private String corpNNIRefId;

    public int getAliasNGroupMDNCount() {
        return aliasNGroupMDNCount;
    }

    public void setAliasNGroupMDNCount(int aliasNGroupMDNCount) {
        this.aliasNGroupMDNCount = aliasNGroupMDNCount;
    }

    public KnCorpGwLinkedAccInfoDTO getCorpGWLikedACCInfo() {
        return corpGWLikedACCInfo;
    }

    public void setCorpGWLikedACCInfo(KnCorpGwLinkedAccInfoDTO corpGWLikedACCInfo) {
        this.corpGWLikedACCInfo = corpGWLikedACCInfo;
    }

    private KnCorpGwLinkedAccInfoDTO corpGWLikedACCInfo;



    //success pegs
    Collection<Integer> successPegs;

    //stores the etag
       private long etag;
    /**
     * getter method for the Response Code
     *
     * @return String
     */
       
    private boolean deleteDeviceNotify;

   	private String deviceIMPI;
   	
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
     * getter method for the Notification DTO
     *
     * @return KnProvNotificationDTO
     */
    public KnOPDirChgDTO getDirChgDTO() {
        return dirChgDTO;
    }

    /**
     * setter method for the Notification DTO
     *
     * @param dirChgDTO KnOPDocumentDTO
     */
    public void setDirChgDTO(KnOPDirChgDTO dirChgDTO) {
        this.dirChgDTO = dirChgDTO;
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
     * @param errorObj KnErrorObject
     */
    public void setErrorObject(KnXDMError errorObj) {
        this.errorObject = errorObj;
    }

    public String getObjectId() {
        return responseCode;
    }

    public long getEtag() {
        return etag;
    }

    public void setEtag(long etag) {
        this.etag = etag;
    }

    /**
     * getter method for the List of Success pegs
     * @return Collection<Integer> list of pegs
     */
    public Collection<Integer> getSuccessPegs() {
        return successPegs;
    }

    /**
     * setter method for the list of Success pegs
     * @param successPegs Collection<Integer>
     */
    public void setSuccessPegs(Collection<Integer> successPegs) {
        this.successPegs = successPegs;
    }

    public void populate(IPopulate dtoObject) {
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public String getPresHome() {
        return presHome;
    }

    public void setPresHome(String presHome) {
        this.presHome = presHome;
    }

    
    public boolean isDeleteDeviceNotify() {
		return deleteDeviceNotify;
	}

	public void setDeleteDeviceNotify(boolean deleteDeviceNotify) {
		this.deleteDeviceNotify = deleteDeviceNotify;
	}

	public String getDeviceIMPI() {
		return deviceIMPI;
	}

	public void setDeviceIMPI(String deviceIMPI) {
		this.deviceIMPI = deviceIMPI;
	}

    public List<KnOPDirChgDTO> getDirChgDTOs() {
        return dirChgDTOs;
    }

    public void setDirChgDTOs(List<KnOPDirChgDTO> dirChgDTOs) {
        this.dirChgDTOs = dirChgDTOs;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("[KnOPProvDTO --> ");
        strBuffer.append(" Response_Code - ").append(responseCode)
                .append(", Response_Status - ").append(responseStatus)
                .append(", Response_Message - ").append(responseMessage)
                .append(", DTO_STatus - ").append(DTOStatus)
                .append(", Error_Object - ").append(errorObject)
                .append(", Notification_Object - ").append(dirChgDTO)
                .append(", dirChgDTOs - ").append(dirChgDTOs)
                .append(", Etag - ").append(etag)
                .append(", Succes_Pegs - ").append(successPegs)
                .append("]");
        return strBuffer.toString();
    }

    public void setDeleteDeviceNotifyMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public List<String> getDeleteDeviceNotifyMdnList() {
        return mdnList;
    }

}
