/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnPubNotifyDetailsDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 12, 2017                8.3
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnPubNotifyDetailsDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471154442676216L;

    private String ownerMdn;
    private Integer clientType;
    private Integer grpId;
    private String grpName;
    private Collection<KnGroupMemberDTO> addedMembers;
    private Collection<KnGroupMemberDTO> modifiedMembers;
    private Collection<String> removedMembers;
    private String docNewEtag;
    private String docOldEtag;
    private Integer groupState;

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
    }

    public Integer getGrpId() {
        return grpId;
    }

    public void setGrpId(Integer grpId) {
        this.grpId = grpId;
    }

    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public Collection<KnGroupMemberDTO> getAddedMembers() {
        return addedMembers;
    }

    public void setAddedMembers(Collection<KnGroupMemberDTO> addedMembers) {
        this.addedMembers = addedMembers;
    }

    public Collection<KnGroupMemberDTO> getModifiedMembers() {
        return modifiedMembers;
    }

    public void setModifiedMembers(Collection<KnGroupMemberDTO> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    public Collection<String> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(Collection<String> removedMembers) {
        this.removedMembers = removedMembers;
    }

    public String getDocNewEtag() {
        return docNewEtag;
    }

    public void setDocNewEtag(String docNewEtag) {
        this.docNewEtag = docNewEtag;
    }

    public String getDocOldEtag() {
        return docOldEtag;
    }

    public void setDocOldEtag(String docOldEtag) {
        this.docOldEtag = docOldEtag;
    }

    public Integer getGroupState() {
        return groupState;
    }

    public void setGroupState(Integer groupState) {
        this.groupState = groupState;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return "KnPubNotifyDetailsDTO{" +
                "ownerMdn='" + KnGDPRTemplate.mdn(ownerMdn) + '\'' +
                ", grpId=" + grpId +
                ", grpName='" + grpName + '\'' +
                ", addedMembers=" + addedMembers +
                ", modifiedMembers=" + modifiedMembers +
                ", removedMembers=" + KnGDPRTemplate.mdnList(removedMembers) +
                ", docNewEtag='" + docNewEtag + '\'' +
                ", docOldEtag='" + docOldEtag + '\'' +
                ", groupState=" + groupState +
                ", clientType=" + clientType +
                '}';
    }

    @Override
    public String getObjectId() {
        return ownerMdn;
    }
}
