/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubGroupInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnMCSDTO;

import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubMCSXCAPPersistDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Shashank Tewari       June 27,2019         9.1.1
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
public class KnPubMCSXCAPPersistDTO extends KnMCSDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676223L;

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String profile;
    private String mdn;
    private int protocolVersion;
    private int serviceAuthStatus;
    private List<String> groupMemberList;
    private List<String> mdns;
    private boolean reGroupBit;
    private String isPreConfiguredGroup;


    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public List<String> getGroupMemberList() {return groupMemberList; }

    public void setGroupMemberList(List<String> groupMemberList) {this.groupMemberList = groupMemberList; }

    public List<String> getMdns() { return mdns; }

    public void setMdns(List<String> mdns) {  this.mdns = mdns; }

    public boolean isReGroupBit() {
        return reGroupBit;
    }

    public void setReGroupBit(boolean reGroupBit) {
        this.reGroupBit = reGroupBit;
    }

    public String getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(String isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public String toString(){
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", InputDTO - ").append(inputDTO);
        strBuffer.append(", PersistenceDTO - ").append(persistenceDTO);
        strBuffer.append(", OperationType - ").append(operationType);
        strBuffer.append(", EntityId - ").append(entityId);
        strBuffer.append(", Profile - ").append(profile);
        strBuffer.append(", mdn - ").append(KnGDPRTemplate.mdn(mdn));
        strBuffer.append(", protocolVersion - ").append(protocolVersion);
        strBuffer.append(", serviceAuthStatus - ").append(serviceAuthStatus);
        strBuffer.append(", reGroupBit - ").append(reGroupBit);
        strBuffer.append(", isPreConfiguredGroup - ").append(isPreConfiguredGroup);
        return strBuffer.toString();
    }
    
}
