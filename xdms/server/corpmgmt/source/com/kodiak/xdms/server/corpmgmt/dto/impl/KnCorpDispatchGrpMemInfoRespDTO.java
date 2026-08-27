/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/5/11
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnCorpDispatchGrpMemInfoRespDTO extends KnCorpResponseDTO {
    private List<KnXDMSubsProvInfoDTO> subscIsDispMemDetailsList;
    private int corpId;

    public List<KnXDMSubsProvInfoDTO> getSubscIsDispMemDetailsList() {
        return subscIsDispMemDetailsList;
    }

    public void setSubscIsDispMemDetailsList(List<KnXDMSubsProvInfoDTO> subscIsDispMemDetailsList) {
        this.subscIsDispMemDetailsList = subscIsDispMemDetailsList;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("subscIsDispMemDetailsList - ").append(subscIsDispMemDetailsList)
                .append(", corpId - ").append(corpId);
        return sb.toString();
    }
}
