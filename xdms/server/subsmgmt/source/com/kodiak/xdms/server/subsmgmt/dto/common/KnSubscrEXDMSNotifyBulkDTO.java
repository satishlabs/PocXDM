/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;


import java.util.List;

/**
 * Created by abhishek on 26/10/16.
 */

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnSubscrEXDMSNotifyBulkDTO extends KnEXDMSNotifyDto {


    // name has been modified to meet the requirement of jsonbody
    List<KnSubscrEXDMSNotifyDto> mdnList;

    public List<KnSubscrEXDMSNotifyDto> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<KnSubscrEXDMSNotifyDto> mdnList) {
        this.mdnList = mdnList;
    }

    @Override
    public String toString() {
        return "KnSubscrEXDMSNotifyBulkDTO{" +
                "mdnList=" + mdnList +
                '}';
    }
}
