/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubGroupInfoDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 13, 2011           7.0
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
public class KnPubGroupInfoDTO extends KnPubGroupDTO {

    private static final long serialVersionUID = 7526471155622676218L;

    private Collection<KnGroupMemberDTO> groupMembers;

    private int groupMemberCount = 0;
    private int maxNumberOfMembers = -1;


    public Collection<KnGroupMemberDTO> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Collection<KnGroupMemberDTO> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public int getGroupMemberCount() {
        return groupMemberCount;
    }

    public void setGroupMemberCount(int groupMemberCount) {
        this.groupMemberCount = groupMemberCount;
    }

    public int getMaxNumberOfMembers() {
        return maxNumberOfMembers;
    }

    public void setMaxNumberOfMembers(int maxNumberOfMembers) {
        this.maxNumberOfMembers = maxNumberOfMembers;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", GroupMembers - ").append(groupMembers);
        strBuffer.append(", GroupMemberCount - ").append(groupMemberCount);
        strBuffer.append(", MaxNumberOfMembers - ").append(maxNumberOfMembers);

        return strBuffer.toString();
    }
}
