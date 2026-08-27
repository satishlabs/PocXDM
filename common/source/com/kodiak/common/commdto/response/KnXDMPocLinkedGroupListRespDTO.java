/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMPocLinkedGroupListRespDTO.java
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


import java.util.List;

public class KnXDMPocLinkedGroupListRespDTO extends  KnXDMCorpRespDTO{
List<KnSubscCorpGroupDTO> subscGroupList;

    public List<KnSubscCorpGroupDTO> getSubscGroupList() {
        return subscGroupList;
    }

    public void setSubscGroupList(List<KnSubscCorpGroupDTO> subscGroupList) {
        this.subscGroupList = subscGroupList;
    }
}
