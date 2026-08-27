/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupInfoRespDTO.java
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.common.KnXDMCorpGrpSharedCorpListDTO;
import com.kodiak.common.commdto.common.KnXDMCorpSublistDTO;

import java.util.Collection;
import java.util.List;

public class KnXDMCorpGroupUgwConfigInfoRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 8014389307535968080L;
    private String corpId;
    private String groupId;
    private String ugwConfig;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUgwConfig() {
        return ugwConfig;
    }

    public void setUgwConfig(String ugwConfig) {
        this.ugwConfig = ugwConfig;
    }

    @Override
    public String toString() {
        return "KnXDMCorpGroupUgwConfigInfoRespDTO{" +
                "corpId='" + corpId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", ugwConfig='" + ugwConfig + '\'' +
                '}';
    }

}
