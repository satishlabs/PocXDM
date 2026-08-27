/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.bulkops.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.dao.KnPOCSubscrInfoDAO;

import java.util.Objects;

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

public class KnSubsAliasInfoDTO implements IIdentifier {

    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCSubscrInfoDAO.class);
    private String pttServerId;

    private static final long serialVersionUID = 7526471105622678000L;

    private String mdn;

    private String aliasId;

    private String aliasIdIssuer;

    private Integer aliasIdType;

    public String getAliasId() {
        return aliasId;
    }

    public void setAliasId(String aliasId) {
        this.aliasId = aliasId;
    }

    public String getAliasIdIssuer() {
        return aliasIdIssuer;
    }

    public void setAliasIdIssuer(String aliasIdIssuer) {
        this.aliasIdIssuer = aliasIdIssuer;
    }

    public Integer getAliasIdType() {
        return aliasIdType;
    }

    public void setAliasIdType(Integer aliasIdType) {
        this.aliasIdType = aliasIdType;
    }

    public String getMdn() {  return mdn;  }

    public void setMdn(String mdn) { this.mdn = mdn; }

    @Override
    public String toString() {
        return "KnSubsAliasInfoDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                "aliasId='" + aliasId + '\'' +
                ", aliasIdIssuer='" + aliasIdIssuer + '\'' +
                ", aliasIdType='" + aliasIdType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnSubsAliasInfoDTO that = (KnSubsAliasInfoDTO) o;
        return aliasId.equals(that.aliasId) && aliasIdIssuer.equals(that.aliasIdIssuer) && aliasIdType.equals(that.aliasIdType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliasId, aliasIdIssuer, aliasIdType);
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
