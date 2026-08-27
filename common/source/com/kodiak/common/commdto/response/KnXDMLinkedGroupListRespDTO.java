/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMLinkedGroupListRespDTO.java
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


import com.kodiak.common.commdto.common.KnXDMCorpGroupDTO;

import java.util.Collection;

public class KnXDMLinkedGroupListRespDTO extends  KnXDMCorpRespDTO{
    private static final long serialVersionUID = 7526471155622623450L;

    Collection<KnXDMCorpGroupDTO> groupList;

    public Collection<KnXDMCorpGroupDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Collection<KnXDMCorpGroupDTO> groupList) {
        this.groupList = groupList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("groupList - ").append(groupList);
        return sb.toString();
    }
}
