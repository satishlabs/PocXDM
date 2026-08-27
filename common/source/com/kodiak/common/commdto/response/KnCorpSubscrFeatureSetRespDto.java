/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnSubscriberFeatureBitInfo;

import java.util.List;

/**
 * Created by asanjiv on 10/27/2016.
 */
public class KnCorpSubscrFeatureSetRespDto extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776153L;

    private List<KnSubscriberFeatureBitInfo> subsFeatureInfoList;

    public List<KnSubscriberFeatureBitInfo> getSubsFeatureInfoList() {
        return subsFeatureInfoList;
    }

    public void setSubsFeatureInfoList(List<KnSubscriberFeatureBitInfo> subsFeatureInfoList) {
        this.subsFeatureInfoList = subsFeatureInfoList;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("subsFeatureInfoList - ").append(subsFeatureInfoList);
        return sb.toString();
    }
}
