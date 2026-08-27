/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/5/11
 * Time: 6:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnXDMCorpDispatchGrpMemberJobRespDTO extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = 7526471155622776130L;
    private int corpId;
    private List<KnXDMSubsProvInfoDTO> subscUpdateList;

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public List<KnXDMSubsProvInfoDTO> getSubscUpdateList() {
        return subscUpdateList;
    }

    public void setSubscUpdateList(List<KnXDMSubsProvInfoDTO> subscUpdateList) {
        this.subscUpdateList = subscUpdateList;
    }
}
