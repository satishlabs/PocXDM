/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpBulkGroupJobRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Jan 23, 2019      9.03
 * <p/>
 * <p/>
 * 9th Floor, MFar Manyata Tech Park
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
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

import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;

import java.util.List;

public class KnCorpBulkGroupJobRespDTO extends KnCorpResponseDTO {
    private static final long serialVersionUID = 7526471155622776139L;

    private int corpId;
    private List<KnXDMSubsProvInfoDTO> subscIsDispMemDetailsList;

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public List<KnXDMSubsProvInfoDTO> getSubscIsDispMemDetailsList() {
        return subscIsDispMemDetailsList;
    }

    public void setSubscIsDispMemDetailsList(List<KnXDMSubsProvInfoDTO> subscIsDispMemDetailsList) {
        this.subscIsDispMemDetailsList = subscIsDispMemDetailsList;
    }

    @Override
    public String toString() {
        return "KnXDMCorpBulkGroupJobRespDTO{" +
                "corpId=" + corpId +
                ", subscIsDispMemDetailsList=" + subscIsDispMemDetailsList +
                '}';
    }
}
