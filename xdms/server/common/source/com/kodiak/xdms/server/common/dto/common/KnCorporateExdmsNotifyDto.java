/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnCorporateExdmsNotifyDto extends KnEXDMSNotifyDto {
    private Integer corpid;
    private String extCorpId;
    private String corpName;
    private Integer lmrInteropFlag;

    public Integer getCorpid() {
        return corpid;
    }

    public void setCorpid(Integer corpid) {
        this.corpid = corpid;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public Integer getLmrInteropFlag() {
        return lmrInteropFlag;
    }

    public void setLmrInteropFlag(Integer lmrInteropFlag) {
        this.lmrInteropFlag = lmrInteropFlag;
    }

    @Override
    public String toString() {
        return "KnCorparateEXDMSNotifyDto{" +
                "corpid=" + corpid +
                ", extCorpId='" + extCorpId + '\'' +
                ", corpName='" + corpName + '\'' +
                ", lmrInteropFlag=" + lmrInteropFlag +
                '}';
    }
}
