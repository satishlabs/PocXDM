/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMSubsAliasInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Venkata Sudhakar     May 6, 2021      11.3
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, MFar Greenheart Phase IV
 * Manyata Tech Park, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMSubsAliasInfoDTO implements Serializable {

    private static final long serialVersionUID = -8418369228789571340L;

    private String aliasId;

    private String aliasIdIssuer;

    private Integer aliasIdType;

    public KnXDMSubsAliasInfoDTO(String aliasId, String aliasIdIssuer, Integer aliasIdType) {
        this.aliasId = aliasId;
        this.aliasIdIssuer = aliasIdIssuer;
        this.aliasIdType = aliasIdType;
    }

    public KnXDMSubsAliasInfoDTO(String aliasId, String aliasIdIssuer){
        this.aliasId = aliasId;
        this.aliasIdIssuer = aliasIdIssuer;
    }


    public String getAliasId() { return aliasId; }

    public void setAliasId(String aliasId) { this.aliasId = aliasId; }

    public String getAliasIdIssuer() { return aliasIdIssuer; }

    public void setAliasIdIssuer(String aliasIdIssuer) { this.aliasIdIssuer = aliasIdIssuer; }


    public Integer getAliasIdType() { return aliasIdType; }

    public void setAliasIdType(Integer aliasIdType) { this.aliasIdType = aliasIdType; }

    @Override
    public String toString() {
        return "KnXDMSubsAliasInfoDTO{" +
                "aliasId='" + aliasId + '\'' +
                ", aliasIdIssuer='" + aliasIdIssuer + '\'' +
                ", aliasIdType='" + aliasIdType + '\'' +
                '}';
    }
}
