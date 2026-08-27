/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSGrpMemberDto;

import java.util.List;

/**
 * ************************************************************************
 * <p>
 * File name:  KnPubEXDMSNotifyDTO.java
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

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnPubEXDMSNotifyDTO extends KnEXDMSNotifyDto {

    private String grpName;
    private Integer grpId;
    private Integer etag;
    private Integer previousEtag;
    private List<KnEXDMSGrpMemberDto> addedMemebrs;
    private List<KnEXDMSGrpMemberDto> modifiedMembers;
    private List<String> removedMembers;
    private String ownerMdn;
    private Integer clientType;

    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public Integer getGrpId() {
        return grpId;
    }

    public void setGrpId(Integer grpId) {
        this.grpId = grpId;
    }

    public Integer getEtag() {
        return etag;
    }

    public void setEtag(Integer etag) {
        this.etag = etag;
    }

    public Integer getPreviousEtag() {
        return previousEtag;
    }

    public void setPreviousEtag(Integer previousEtag) {
        this.previousEtag = previousEtag;
    }

    public List<KnEXDMSGrpMemberDto> getAddedMemebrs() {
        return addedMemebrs;
    }

    public void setAddedMemebrs(List<KnEXDMSGrpMemberDto> addedMemebrs) {
        this.addedMemebrs = addedMemebrs;
    }

    public List<KnEXDMSGrpMemberDto> getModifiedMembers() {
        return modifiedMembers;
    }

    public void setModifiedMembers(List<KnEXDMSGrpMemberDto> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    public List<String> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(List<String> removedMembers) {
        this.removedMembers = removedMembers;
    }

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return "KnPubEXDMSNotifyDto{" +
                "grpName='" + grpName + '\'' +
                ", grpId=" + grpId +
                ", etag=" + etag +
                ", previousEtag=" + previousEtag +
                ", addedMemebrs=" + addedMemebrs +
                ", modifiedMembers=" + modifiedMembers +
                ", removedMembers=" + KnGDPRTemplate.mdnList(removedMembers) +
                ", ownerMdn='" +KnGDPRTemplate.mdn(ownerMdn) + '\'' +
                ", clientType=" + clientType +
                '}';
    }
}
