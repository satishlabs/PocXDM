/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnLinkedGroupInfo.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date            Release
 * -------------------- ------------    -------------------------------------
 * Namita P Nair        April 10 2015      8.0
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
package com.kodiak.xdms.server.corpmgmt.dto.impl;


public class KnLinkedGroupInfo  {
    //The group Mdn part of the group
    private String groupMdn;
    //The group type of the group i.e standard(1),Dispatch(2),Broadcast(3)
    private int groupType;
    //The group NAme
    private String groupName;
    //The group id
    private int groupId;

    public String getGroupMdn() {
        return groupMdn;
    }

    public void setGroupMdn(String groupMdn) {
        this.groupMdn = groupMdn;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
