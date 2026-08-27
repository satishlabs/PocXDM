/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMGroupDetailDTO.java
 * Subsystem:  Common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Harsha A             Feb 3, 2011           7.0
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
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import java.util.Collection;

public class KnXDMGroupDetailDTO extends KnXDMGroupDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676130L;

    private Collection<KnXDMGroupMdnInfoDTO> grpMembers;

    private int memberCount;

    public Collection<KnXDMGroupMdnInfoDTO> getGrpMembers() {
        return grpMembers;
    }

    public void setGrpMembers(Collection<KnXDMGroupMdnInfoDTO> grpMembers) {
        this.grpMembers = grpMembers;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(this.getClass().getName() + " - ")
                .append(" Member Info - ").append(grpMembers)
                .append(", Member Count - ").append(memberCount);

        return strBuffer.toString();
    }
}
