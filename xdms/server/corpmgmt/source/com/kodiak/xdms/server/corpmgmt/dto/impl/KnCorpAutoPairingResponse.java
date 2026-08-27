/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

/**
 * Created by kodiak on 24-11-2015.
 */
public class KnCorpAutoPairingResponse extends KnCorpResponseDTO {

    private Boolean groupPairing;
    private int groupId;
    private String groupName;

    public Boolean getGroupPairing() {
        return groupPairing;
    }

    public void setGroupPairing(Boolean groupPairing) {
        this.groupPairing = groupPairing;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        super.toString();
        StringBuffer sb = new StringBuffer(100);
        sb.append("groupPairing - ").append(groupPairing)
                .append(", groupId - ").append(groupId)
                .append(", groupName - ").append(groupName);
        return sb.toString();
    }
}
