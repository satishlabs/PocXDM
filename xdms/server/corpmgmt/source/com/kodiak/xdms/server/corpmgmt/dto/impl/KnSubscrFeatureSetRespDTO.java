/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import java.util.List;

/**
 * Created by asanjiv on 11/2/2016.
 */
public class KnSubscrFeatureSetRespDTO extends KnCorpResponseDTO{

    private List<KnCorpSubscrInfoDTO> subscrInfoDTOList;

    public List<KnCorpSubscrInfoDTO> getSubscrInfoDTOList() {
        return subscrInfoDTOList;
    }

    public void setSubscrInfoDTOList(List<KnCorpSubscrInfoDTO> subscrInfoDTOList) {
        this.subscrInfoDTOList = subscrInfoDTOList;
    }

}
