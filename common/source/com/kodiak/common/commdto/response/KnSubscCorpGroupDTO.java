/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnSubscCorpGroupDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * NAmita P Nair        April  9 2015       8.0
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
 * *******************************************************************************
 */
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;

public class KnSubscCorpGroupDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676150L;

    //The groupMDN
    String groupMdn;
    //The group type
    String groupType;
    //The group Name
    String groupName;
    //The group Type
    String groupId;

    /**
     * Getter of the group Mdn
     * @return
     */
    public String getGroupMdn() {
        return groupMdn;
    }

    /**
     * Setter of the group Mdn
     * @param groupMdn
     */
    public void setGroupMdn(String groupMdn) {
        this.groupMdn = groupMdn;
    }

    /**
     * Getter of the group Type
     * @return
     * @params
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * Setter of the group Type
     * @param groupType
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * GEtter of the Group Name
     * @return
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Setter of the group Name
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Getter of the group Id
     * @return
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Setter of the group Id
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getObjectId() {
        return groupId;
    }
}
