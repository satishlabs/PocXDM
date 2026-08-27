/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;


import java.util.List;

public class KnGroupUsageListDTO {

    protected String docEtag;

    protected List<KnGroupUsageDTO> list;
    protected int corpId;


    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

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

    @Override
    public String toString() {
        return "KnGroupUsageListDTO{" +
                "docEtag='" + docEtag + '\'' +
                ", list=" + list +
                ", corpId=" + corpId +
                '}';
    }


}
