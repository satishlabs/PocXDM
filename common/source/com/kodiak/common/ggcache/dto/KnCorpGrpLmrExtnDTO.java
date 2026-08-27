/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

import java.util.Arrays;

public class KnCorpGrpLmrExtnDTO {

    private Integer corpGroupId;
    private Integer corpId;
    private byte[] lmrExtn;

    public Integer getCorpGroupId() {
        return corpGroupId;
    }

    public void setCorpGroupId(Integer corpGroupId) {
        this.corpGroupId = corpGroupId;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public byte[] getLmrExtn() {
        return lmrExtn;
    }

    public void setLmrExtn(byte[] lmrExtn) {
        this.lmrExtn = lmrExtn;
    }

    @Override
    public String toString() {
        return "KnCorpGrpLmrExtnDTO{" +
                "corpGroupId=" + corpGroupId +
                ", corpId=" + corpId +
                ", lmrExtn=" + Arrays.toString(lmrExtn) +
                '}';
    }
}
