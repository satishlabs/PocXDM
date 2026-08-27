/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dto;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnProvBatchProfileDTO.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;

import java.util.List;

public class KnProvBatchProfileDTO {

    private KnBatchDTO batchInfoDTO;
    private KnXDMPAMSubsProfInfoDTO defaultSubsProfile;
    private List<String> mdnList;

    public KnBatchDTO getBatchInfoDTO() {
        return batchInfoDTO;
    }

    public void setBatchInfoDTO(KnBatchDTO batchInfoDTO) {
        this.batchInfoDTO = batchInfoDTO;
    }

    public KnXDMPAMSubsProfInfoDTO getDefaultSubsProfile() {
        return defaultSubsProfile;
    }

    public void setDefaultSubsProfile(KnXDMPAMSubsProfInfoDTO defaultSubsProfile) {
        this.defaultSubsProfile = defaultSubsProfile;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    @Override
    public String toString() {
        return "KnProvBatchProfileDTO{" +
                "batchInfoDTO=" + batchInfoDTO +
                ", defaultSubsProfile=" + defaultSubsProfile +
                '}';
    }
}
