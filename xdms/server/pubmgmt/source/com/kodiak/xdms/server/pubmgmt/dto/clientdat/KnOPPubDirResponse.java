/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.dto.common.KnCorpGpInfoDTO;
import com.kodiak.xdms.server.common.dto.intf.IOutputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnAuthDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnEmergencyDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupUsageListDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnTGSSDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnOPPubDirResponse.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Feb 5, 2011           7.0
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
public class KnOPPubDirResponse implements IOutputDTO {

    private static final long serialVersionUID = 7526471155622676212L;

    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private int dtoStatus;
    private KnXDMError errorObject;

    private int dirEtag;
    private String xcapRootUri;
    private long subsUpdateTime;
    private int rlsEtag;
    private int publicSubsType;

    private KnPubContactDTO contactListDTO;
    private Collection<KnPubGroupDTO> groupList = null;
    private String protocolVersion;
    private long activeFS1;
    private int corpId;
    private String xdmsHome;
    private KnAuthDocDTO authDocDTO;
    private KnEmergencyDocDTO emergencyDocDTO;
    private KnGroupUsageListDocDTO usageListDocDTO;
    private String userAgent;
    private KnTGSSDocDTO tgssDocDTO;
    private String activeFS2;
    private String baseMdn;
    private String userProfileId;
    private Collection<KnCorpGpInfoDTO> corporateGroupList = null;
    private String subscriberFS2;
    int clientType;

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public KnGroupUsageListDocDTO getUsageListDocDTO() {
        return usageListDocDTO;
    }

    public void setUsageListDocDTO(KnGroupUsageListDocDTO usageListDocDTO) {
        this.usageListDocDTO = usageListDocDTO;
    }

    public KnAuthDocDTO getAuthDocDTO() {
        return authDocDTO;
    }

    public void setAuthDocDTO(KnAuthDocDTO authDocDTO) {
        this.authDocDTO = authDocDTO;
    }

    public KnEmergencyDocDTO getEmergencyDocDTO() {
        return emergencyDocDTO;
    }

    public void setEmergencyDocDTO(KnEmergencyDocDTO emergencyDocDTO) {
        this.emergencyDocDTO = emergencyDocDTO;
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

    public int getDirEtag() {
        return dirEtag;
    }

    public void setDirEtag(int dirEtag) {
        this.dirEtag = dirEtag;
    }

    public KnPubContactDTO getContactListDTO() {
        return contactListDTO;
    }

    public void setContactListDTO(KnPubContactDTO contactListDTO) {
        this.contactListDTO = contactListDTO;
    }

    public void populate(IPopulate dtoObject) {

    }



    public String getObjectId() {
        return Integer.toString(dirEtag);
    }

    public long getSubsUpdateTime() {
        return subsUpdateTime;
    }

    public void setSubsUpdateTime(long subsUpdateTime) {
        this.subsUpdateTime = subsUpdateTime;
    }

    public String getXcapRootUri() {
        return xcapRootUri;
    }

    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

    public int getRlsEtag() {
        return rlsEtag;
    }

    public void setRlsEtag(int rlsEtag) {
        this.rlsEtag = rlsEtag;
    }

    public Collection<KnPubGroupDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Collection<KnPubGroupDTO> groupList) {
        this.groupList = groupList;
    }


    public int getDtoStatus() {
        return dtoStatus;
    }

    public void setDtoStatus(int dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    public int getPublicSubsType() {
        return publicSubsType;
    }

    public void setPublicSubsType(int publicSubsType) {
        this.publicSubsType = publicSubsType;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public long getActiveFS1() {
        return activeFS1;
    }

    public void setActiveFS1(long activeFS1) {
        this.activeFS1 = activeFS1;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public KnTGSSDocDTO getTgssDocDTO() { return tgssDocDTO; }

    public void setTgssDocDTO(KnTGSSDocDTO tgssDocDTO) { this.tgssDocDTO = tgssDocDTO; }

    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

    public String getBaseMdn() {
        return baseMdn;
    }

    public void setBaseMdn(String baseMdn) {
        this.baseMdn = baseMdn;
    }

    public Collection<KnCorpGpInfoDTO> getCorporateGroupList() { return corporateGroupList; }

    public void setCorporateGroupList(Collection<KnCorpGpInfoDTO> corporateGroupList) { this.corporateGroupList = corporateGroupList; }

    public String getSubscriberFS2() {
        return subscriberFS2;
    }

    public void setSubscriberFS2(String subscriberFS2) {
        this.subscriberFS2 = subscriberFS2;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }
    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", ResponseCode - ").append(responseCode);
        strBuffer.append(", ResponseStatus - ").append(responseStatus);
        strBuffer.append(", ResponseMessage - ").append(responseMessage);
        strBuffer.append(", DtoStatus - ").append(dtoStatus);
        strBuffer.append(", ErrorObject - ").append(errorObject);
        strBuffer.append(", Dir Etag - ").append(dirEtag);
        strBuffer.append(", Contact List DTO - ").append(contactListDTO);
        strBuffer.append(", Subscriber Last Update Time - ").append(subsUpdateTime);
        strBuffer.append(", XCAP Root URI - ").append(xcapRootUri);
        strBuffer.append(", RLS Etag - ").append(rlsEtag);
        strBuffer.append(", Pub Group List -").append(groupList);
        strBuffer.append(" Authorization doc ").append(authDocDTO);
        strBuffer.append(" Emergency doc ").append(emergencyDocDTO);
        strBuffer.append(", Pub Subscription Type -").append(publicSubsType);
        strBuffer.append(", protocolVersion -").append(protocolVersion);
        strBuffer.append(",activeFS1 -").append(activeFS1);
        strBuffer.append(",corpId -").append(corpId);
        strBuffer.append(",userAgent -").append(userAgent);
        strBuffer.append(",TGSSDocDTO -").append(tgssDocDTO);
        strBuffer.append(",activeFS2 -").append(activeFS2);
        strBuffer.append(",baseMdn -").append(KnGDPRTemplate.mdn(baseMdn));
        strBuffer.append(", userProfileId - ").append(userProfileId);
        strBuffer.append(", corporateGroupList - ").append(corporateGroupList);
        return strBuffer.toString();
    }

}
