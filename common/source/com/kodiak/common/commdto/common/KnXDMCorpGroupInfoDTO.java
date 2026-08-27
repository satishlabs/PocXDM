/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
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

import java.util.Collection;


public class KnXDMCorpGroupInfoDTO extends KnXDMCorpGroupDTO {

    private static final long serialVersionUID = 7526471155622676126L;

    private Collection<KnXDMGroupMdnInfoDTO> groupMembers;
    private Collection<KnXDMGroupMdnInfoDTO> groupSupervisor;
    private Collection<KnXDMGroupMdnInfoDTO> modifiedMembers;

    public Collection<KnXDMGroupMdnInfoDTO> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Collection<KnXDMGroupMdnInfoDTO> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Collection<KnXDMGroupMdnInfoDTO> getGroupSupervisor() {
        return groupSupervisor;
    }

    public void setGroupSupervisor(Collection<KnXDMGroupMdnInfoDTO> groupSupervisor) {
        this.groupSupervisor = groupSupervisor;
    }

    public Collection<KnXDMGroupMdnInfoDTO> getModifiedMembers() {
        return modifiedMembers;
    }

    public void setModifiedMembers(Collection<KnXDMGroupMdnInfoDTO> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(", KnXDMCorpGroupDTO - ").append(super.toString())
                .append(", GroupMembers - ").append(groupMembers)
                .append(", groupSupervisor - ").append(groupSupervisor)
                .append(", modifiedMembers - ").append(modifiedMembers);
        return sb.toString();
    }
}
