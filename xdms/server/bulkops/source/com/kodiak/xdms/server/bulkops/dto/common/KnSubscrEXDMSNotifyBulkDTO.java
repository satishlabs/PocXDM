package com.kodiak.xdms.server.bulkops.dto.common;

import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;

import java.util.List;

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

