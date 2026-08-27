/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpSublistListRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 16, 2011      7.0
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMCorpSublistDTO;

import java.util.Collection;


public class KnXDMCorpSublistListRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776154L;

    private Collection<KnXDMCorpSublistDTO> sublistLists;

    private String count;

    public Collection<KnXDMCorpSublistDTO> getSublistLists() {
        return sublistLists;
    }

    public void setSublistLists(Collection<KnXDMCorpSublistDTO> sublistLists) {
        this.sublistLists = sublistLists;
    }


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", SublistList - ").append(sublistLists)
                .append(", Count -").append(count);
        return sb.toString();
    }
}
