/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDetailsDTO;

import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  ICommonInboundIntf.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 7, 2011        7.0
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

public class KnIPPubGroupInfoDTO extends KnIPPubGroupDTO {

    private static final long serialVersionUID = 7526471155622676210L;

    private List<KnGroupMemberDTO> groupMembers = null;
    private Collection<KnMemberDetailsDTO> memberDetails = null;
    private int membersCount;

    private String vendorId;
    private String newGrpName;
    private List<KnGroupMemberDTO> modifiedMembers;
    private List<String> removedMembers;

    public List<KnGroupMemberDTO> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<KnGroupMemberDTO> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public Collection<KnMemberDetailsDTO> getMemberDetails() {
        return memberDetails;
    }

    public void setMemberDetails(Collection<KnMemberDetailsDTO> memberDetails) {
        this.memberDetails = memberDetails;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getNewGrpName() {
        return newGrpName;
    }

    public void setNewGrpName(String newGrpName) {
        this.newGrpName = newGrpName;
    }

    public List<KnGroupMemberDTO> getModifiedMembers() {
        return modifiedMembers;
    }

    public void setModifiedMembers(List<KnGroupMemberDTO> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    public List<String> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(List<String> removedMembers) {
        this.removedMembers = removedMembers;
    }

    public String toString(){
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", groupMembers - ").append(groupMembers);
        strBuffer.append(", membersCount - ").append(membersCount);
        strBuffer.append(", MemberDetails - ").append(memberDetails);
        strBuffer.append(", vendorId - ").append(vendorId);
        strBuffer.append(", newGrpName - ").append(newGrpName);
        strBuffer.append(", modifiedMembers - ").append(modifiedMembers);
        strBuffer.append(", removedMembers - ").append(KnGDPRTemplate.mdnList(removedMembers));
        return strBuffer.toString();
    }

}
