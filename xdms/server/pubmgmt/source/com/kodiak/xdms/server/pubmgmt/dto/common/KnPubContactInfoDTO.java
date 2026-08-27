/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubContactInfoDTO.java
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
public class KnPubContactInfoDTO extends KnPubContactDTO {

    private static final long serialVersionUID = 7526471155622676217L;

    private Collection<KnMemberDTO> contactMembers;

    private int contactMemberCount = 0;
    private int maxNumberOfMembers = -1;


    public Collection<KnMemberDTO> getContactMembers() {
        return contactMembers;
    }

    public void setContactMembers(Collection<KnMemberDTO> contactMembers) {
        this.contactMembers = contactMembers;
    }

    public int getContactMemberCount() {
        return contactMemberCount;
    }

    public void setContactMemberCount(int contactMemberCount) {
        this.contactMemberCount = contactMemberCount;
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
        strBuffer.append(", ContactMembers - ").append(contactMembers);
        strBuffer.append(", ContactMemberCount - ").append(contactMemberCount);
        strBuffer.append(", MaxNumberOfMembers - ").append(maxNumberOfMembers);

        return strBuffer.toString();
    }
}