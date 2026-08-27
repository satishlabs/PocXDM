/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;

import com.kodiak.common.commdto.request.KnPUBContactAddonAliasDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDetailsDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPPubContactInfoDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 10, 2011        7.0
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
public class KnIPPubContactInfoDTO extends KnIPPubContactDTO {

    private static final long serialVersionUID = 7526471155622676208L;

    private List<KnMemberDTO> members = null;
    private Collection<KnMemberDetailsDTO> memberDetails = null;
    private int membersCount;
    private List<KnMemberDTO> modifiedContList;
    private List<String> removedContList;
    private HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> contactAddonAliasMap;


    private String vendorId;

    public List<KnMemberDTO> getModifiedContList() {
        return modifiedContList;
    }

    public void setModifiedContList(List<KnMemberDTO> modifiedContList) {
        this.modifiedContList = modifiedContList;
    }

    public List<String> getRemovedContList() {
        return removedContList;
    }

    public void setRemovedContList(List<String> removedContList) {
        this.removedContList = removedContList;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public List<KnMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<KnMemberDTO> members) {
        this.members = members;
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

    public HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> getContactAddonAliasMap() {return contactAddonAliasMap; }

    public void setContactAddonAliasMap(HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> contactAddonAliasMap) {this.contactAddonAliasMap = contactAddonAliasMap; }

    public String toString(){
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", MembersCount - ").append(membersCount);
        strBuffer.append(", Members - ").append(members);
        strBuffer.append(", MemberDetails - ").append(memberDetails);
        strBuffer.append(", modifiedContList - ").append(modifiedContList);
        strBuffer.append(", removedContList - ").append(KnGDPRTemplate.mdnList(removedContList));
        strBuffer.append(", vendorId - ").append(vendorId);
        strBuffer.append(", contactAddonAliasMap - ").append(contactAddonAliasMap);

        return strBuffer.toString();
    }

}