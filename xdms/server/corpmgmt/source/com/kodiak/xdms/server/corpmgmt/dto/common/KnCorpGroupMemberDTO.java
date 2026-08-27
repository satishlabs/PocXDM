/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnGroupMemberDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.common;


public class KnCorpGroupMemberDTO extends KnCorpContactDTO {

    private int memberGroupsCount;
    private int groupEtag;
    private int groupId;
    private KnCorpGroupContactDTO groupContactDTO;

    public KnCorpGroupMemberDTO() {}

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public KnCorpGroupMemberDTO(String mdn) {
     super(mdn);
    }

    public int getMemberGroupsCount() {
        return memberGroupsCount;
    }

    public void setMemberGroupsCount(int memberGroupsCount) {
        this.memberGroupsCount = memberGroupsCount;
    }

    public int getGroupEtag() {
        return groupEtag;
    }

    public void setGroupEtag(int groupEtag) {
        this.groupEtag = groupEtag;
    }

    public KnCorpGroupContactDTO getGroupContactDTO() {
        return groupContactDTO;
    }

    public void setGroupContactDTO(KnCorpGroupContactDTO groupContactDTO) {
        this.groupContactDTO = groupContactDTO;
    }
}
