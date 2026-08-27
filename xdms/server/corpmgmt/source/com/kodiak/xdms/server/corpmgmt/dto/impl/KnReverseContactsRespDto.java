/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Created by asanjiv on 10/3/15.
 */
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnMDNInfoDto;

import java.util.List;

public class KnReverseContactsRespDto extends KnCorpResponseDTO {
    List<KnMDNInfoDto> mdnInfoDtoList;
    boolean isReqMdnExtCont;

    public boolean isReqMdnExtCont() {
        return isReqMdnExtCont;
    }

    public void setReqMdnExtCont(boolean isReqMdnExtCont) {
        this.isReqMdnExtCont = isReqMdnExtCont;
    }

    public List<KnMDNInfoDto> getMdnInfoDtoList() {
        return mdnInfoDtoList;
    }

    public void setMdnInfoDtoList(List<KnMDNInfoDto> mdnInfoDtoList) {
        this.mdnInfoDtoList = mdnInfoDtoList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(300);
        sb.append(", mdnInfoDtoList - ").append(mdnInfoDtoList);
        sb.append(", isReqMdnExtCont - ").append(isReqMdnExtCont);
        sb.append(", mdnInfoDtoList").append(mdnInfoDtoList);
        return sb.toString();
    }
}
