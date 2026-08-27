/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;

import java.util.List;
import java.util.Map;

public class KnDynamicContactPersistDTO extends KnPubContactInfoPersistDTO {

    private List<String> existingContList;

    private List<KnMemberDTO> addedContactDetailList;

    private List<String> addedContList;

    private int tpID;

    private Map<String, Integer> mdnTPidMap;

    private KnSubsProfileDTO subsProfileDTO;

    private List<String> reqExtContactList;

    private List<String> validReqExtContactList;

    private Boolean dynAPIServFlag;

    public List<String> getExistingContList() {
        return existingContList;
    }

    public void setExistingContList(List<String> existingContList) {
        this.existingContList = existingContList;
    }

    public List<KnMemberDTO> getAddedContactDetailList() {
        return addedContactDetailList;
    }

    public void setAddedContactDetailList(List<KnMemberDTO> addedContactDetailList) {
        this.addedContactDetailList = addedContactDetailList;
    }

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

    public KnSubsProfileDTO getSubsProfileDTO() {
        return subsProfileDTO;
    }

    public void setSubsProfileDTO(KnSubsProfileDTO subsProfileDTO) {
        this.subsProfileDTO = subsProfileDTO;
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

    public Boolean getDynAPIServFlag() {
        return dynAPIServFlag;
    }

    public void setDynAPIServFlag(Boolean dynAPIServFlag) {
        this.dynAPIServFlag = dynAPIServFlag;
    }

    public List<String> getAddedContList() {
        return addedContList;
    }

    public void setAddedContList(List<String> addedContList) {
        this.addedContList = addedContList;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", existingContList - ").append(KnGDPRTemplate.mdnList(existingContList));
        strBuffer.append(", addedContactDetailList - ").append(addedContactDetailList);
        strBuffer.append(", tpID - ").append(tpID);
        strBuffer.append(", mdnTPidMap - ").append(KnGDPRTemplate.mapKeyMdn(mdnTPidMap));
        strBuffer.append(", reqExtContactList - ").append(KnGDPRTemplate.mdnList(reqExtContactList));
        strBuffer.append(", validReqExtContactList - ").append(KnGDPRTemplate.mdnList(validReqExtContactList));
        strBuffer.append(", dynAPIServFlag - ").append(dynAPIServFlag);
        strBuffer.append(super.toString());
        return strBuffer.toString();
    }
}
