/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnLinkedGroupInfoRespDTO.java
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

import java.util.List;


public class KnLinkedGroupInfoRespDTO extends KnCorpResponseDTO{
    List<KnLinkedGroupInfo> linkedGroupList;

    public List<KnLinkedGroupInfo> getLinkedGroupList() {
        return linkedGroupList;
    }

    public void setLinkedGroupList(List<KnLinkedGroupInfo> linkedGroupList) {
        this.linkedGroupList = linkedGroupList;
    }
}
