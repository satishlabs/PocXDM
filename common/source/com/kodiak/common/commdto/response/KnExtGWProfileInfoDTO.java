/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

public class KnExtGWProfileInfoDTO implements com.kodiak.common.dto.IIdentifier {
    public KnExtGWProfileInfoDTO() {}

    private static final long serialVersionUID = -7533061164632163337L;
    private String gwId;


    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    @Override
    public String toString() {
        return "KnExtGWProfileInfoDTO{" +
                "gwId='" + gwId + '\'' +
                '}';
    }

    @Override
    public String getObjectId() {
        return gwId;
    }
}
