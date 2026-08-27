/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnGroupUsageDTO;

import java.util.List;

public class KnXDMGroupUsageListResponseDTO extends KnXDMRespDTO {
    protected List<KnGroupUsageDTO> list;
    private String docEtag;

    protected int corpId;

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public List<KnGroupUsageDTO> getList() {
        return list;
    }

    public void setList(List<KnGroupUsageDTO> list) {
        this.list = list;
    }

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    @Override
    public String toString() {
        return "KnXDMGroupUsageListResponseDTO{" +
                "list=" + list +
                ", docEtag='" + docEtag + '\'' +
                ", corpId=" + corpId +
                '}';
    }
}
