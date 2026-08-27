/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnDynamicGroupPersistDTO extends KnPubGroupInfoPersistDTO {

    private KnSubsProfileDTO subsProfile;

    private int tpID;

    private List<KnMemberDTO> addedMemberDetailList;

    private List<String> addedMemList;

    private List<KnMemberDTO> modifiedMemberDetailList;

    private Map<String, Integer> mdnTPidMap;

    private List<String> reqExtContactList;

    private List<String> validReqExtContactList;

    private List<String> removedMembers;

    private Collection<KnGroupMemberDTO> existingMembers;

    private Boolean dynAPIServFlag;

    public int getTpID() {
        return tpID;
    }

    public void setTpID(int tpID) {
        this.tpID = tpID;
    }

    public Map<String, Integer> getMdnTPidMap() {
        return mdnTPidMap;
    }

    public void setMdnTPidMap(Map<String, Integer> mdnTPidMap) {
        this.mdnTPidMap = mdnTPidMap;
    }

    public List<String> getReqExtContactList() {
        return reqExtContactList;
    }

    public void setReqExtContactList(List<String> reqExtContactList) {
        this.reqExtContactList = reqExtContactList;
    }

    public List<String> getValidReqExtContactList() {
        return validReqExtContactList;
    }

    public void setValidReqExtContactList(List<String> validReqExtContactList) {
        this.validReqExtContactList = validReqExtContactList;
    }

    public KnSubsProfileDTO getSubsProfile() {
        return subsProfile;
    }

    public void setSubsProfile(KnSubsProfileDTO subsProfile) {
        this.subsProfile = subsProfile;
    }

    public List<KnMemberDTO> getAddedMemberDetailList() {
        return addedMemberDetailList;
    }

    public void setAddedMemberDetailList(List<KnMemberDTO> addedMemberDetailList) {
        this.addedMemberDetailList = addedMemberDetailList;
    }

    public List<String> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(List<String> removedMembers) {
        this.removedMembers = removedMembers;
    }

    public List<KnMemberDTO> getModifiedMemberDetailList() {
        return modifiedMemberDetailList;
    }

    public void setModifiedMemberDetailList(List<KnMemberDTO> modifiedMemberDetailList) {
        this.modifiedMemberDetailList = modifiedMemberDetailList;
    }

    public Collection<KnGroupMemberDTO> getExistingMembers() {
        return existingMembers;
    }

    public void setExistingMembers(Collection<KnGroupMemberDTO> existingMembers) {
        this.existingMembers = existingMembers;
    }

    public Boolean getDynAPIServFlag() {
        return dynAPIServFlag;
    }

    public void setDynAPIServFlag(Boolean dynAPIServFlag) {
        this.dynAPIServFlag = dynAPIServFlag;
    }

    public List<String> getAddedMemList() {
        return addedMemList;
    }

    public void setAddedMemList(List<String> addedMemList) {
        this.addedMemList = addedMemList;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", tpID - ").append(tpID);
        strBuffer.append(", mdnTPidMap - ").append(KnGDPRTemplate.mapKeyMdn(mdnTPidMap));
        strBuffer.append(", reqExtContactList - ").append(KnGDPRTemplate.mdnList(reqExtContactList));
        strBuffer.append(", validReqExtContactList - ").append(KnGDPRTemplate.mdnList(validReqExtContactList));
        strBuffer.append(", addedMemberDetailList - ").append(addedMemberDetailList);
        strBuffer.append(", modifiedMemberDetailList - ").append(modifiedMemberDetailList);
        strBuffer.append(", removedMembers - ").append(KnGDPRTemplate.mdnList(removedMembers));
        strBuffer.append(", existingMembers - ").append(existingMembers);
        strBuffer.append(", dynAPIServFlag - ").append(dynAPIServFlag);
        strBuffer.append(super.toString());
        return strBuffer.toString();
    }
}
