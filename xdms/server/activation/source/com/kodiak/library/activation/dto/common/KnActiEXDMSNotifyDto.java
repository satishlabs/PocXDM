/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.library.activation.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;

/**
 * Created by abhishek on 25/10/16.
 */
public class KnActiEXDMSNotifyDto extends KnEXDMSNotifyDto {
    private String mdn;
    private Integer corpId;
    private Long activeFS;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(Long activeFS) {
        this.activeFS = activeFS;
    }

    @Override
    public String toString() {
        return "KnActiEXDMSNotifyDto{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", corpId=" + corpId +
                ", activeFS=" + activeFS +
                '}';
    }
}
