/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.dto.intf.IOutputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubNotifyDetailsDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnOpPubResponse.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnOpPubResponse implements IOutputDTO {

    private static final long serialVersionUID = 7526471155622676213L;

    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private int dtoStatus;
    private KnXDMError errorObject;
    private String objectId;
    private String docEtag;
    private boolean replaceGrp;
    private int serviceAuthStatus;
    private int serviceStatusAuthUser;
    private int serviceStatusAuthOP;
    private boolean isServiceAuthChange;
    private String mdn;
    private List<String> mdnList;
    private List<String> profileMdnList;
    private boolean isDiscreetChanged;
    private String activeFS2;
    private Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap;
    private Map<String, String> mcsXcapRootUriMap;
    private int dispatchType;

    

	//stores the notification Object
    KnOPDirChgDTO dirChgDTO;

    //stores the notifications Object
    private List<KnOPDirChgDTO> dirChgDTOs;

    // Added for microServices Notification:
    private KnPubNotifyDetailsDTO pubNotifyDetailsDTO;

    public boolean isDiscreetChanged() {
        return isDiscreetChanged;
    }

    public void setDiscreetChanged(boolean discreetChanged) {
        isDiscreetChanged = discreetChanged;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public boolean isServiceAuthChange() {
        return isServiceAuthChange;
    }

    public void setServiceAuthChange(boolean serviceAuthChange) {
        isServiceAuthChange = serviceAuthChange;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getServiceStatusAuthUser() {
        return serviceStatusAuthUser;
    }

    public void setServiceStatusAuthUser(int serviceStatusAuthUser) {
        this.serviceStatusAuthUser = serviceStatusAuthUser;
    }

    public int getServiceStatusAuthOP() {
        return serviceStatusAuthOP;
    }

    public void setServiceStatusAuthOP(int serviceStatusAuthOP) {
        this.serviceStatusAuthOP = serviceStatusAuthOP;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getDTOStatus() {
        return dtoStatus;
    }

    public void setDTOStatus(int dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    public KnXDMError getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(KnXDMError errorObject) {
        this.errorObject = errorObject;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void populate(IPopulate dtoObject) {

    }

    public KnOPDirChgDTO getDirChgDTO() {
        return dirChgDTO;
    }

    public void setDirChgDTO(KnOPDirChgDTO dirChgDTO) {
        this.dirChgDTO = dirChgDTO;
    }

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    public boolean isReplaceGrp() {
        return replaceGrp;
    }

    public void setReplaceGrp(boolean replaceGrp) {
        this.replaceGrp = replaceGrp;
    }

    public KnPubNotifyDetailsDTO getPubNotifyDetailsDTO() {
        return pubNotifyDetailsDTO;
    }

    public void setPubNotifyDetailsDTO(KnPubNotifyDetailsDTO pubNotifyDetailsDTO) {
        this.pubNotifyDetailsDTO = pubNotifyDetailsDTO;
    }

    public List<KnOPDirChgDTO> getDirChgDTOs() {
        return dirChgDTOs;
    }

    public void setDirChgDTOs(List<KnOPDirChgDTO> dirChgDTOs) {
        this.dirChgDTOs = dirChgDTOs;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
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


    public List<String> getProfileMdnList() {
		return profileMdnList;
	}

	public void setProfileMdnList(List<String> profileMdnList) {
		this.profileMdnList = profileMdnList;
	}

    public int getDispatchType() { return dispatchType; }

    public void setDispatchType(int dispatchType) { this.dispatchType = dispatchType; }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", ResponseCode - ").append(responseCode);
        strBuffer.append(", ResponseStatus - ").append(responseStatus);
        strBuffer.append(", ResponseMessage - ").append(responseMessage);
        strBuffer.append(", DtoStatus - ").append(dtoStatus);
        strBuffer.append(", ErrorObject - ").append(errorObject);
        strBuffer.append(", ObjectId - ").append(objectId);
        strBuffer.append(", Doc Etag - ").append(docEtag);
        strBuffer.append(", DirChangeDTO - ").append(dirChgDTO);
        strBuffer.append(", replaceGroup -").append(replaceGrp);
        strBuffer.append(", pubNotifyDetailsDTO -").append(pubNotifyDetailsDTO);
        strBuffer.append(", dirChgDTOs -").append(dirChgDTOs);
        strBuffer.append(", isServiceAuthChange").append(isServiceAuthChange);
        strBuffer.append(", activeFS2").append(activeFS2);
        strBuffer.append(", profileMdnEtagMap").append(KnGDPRTemplate.mapKeyMdn(profileMdnEtagMap));
        strBuffer.append(", mcsXcapRootUriMap").append(KnGDPRTemplate.mapKeyMdn(mcsXcapRootUriMap));
        strBuffer.append(", profileMdnList").append(KnGDPRTemplate.mdnList(profileMdnList));
        return strBuffer.toString();
    }

}
