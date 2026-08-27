package com.kodiak.xdms.server.common.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnBulkGroupPropertiesDTO extends KnEXDMSNotifyDto {

    private List<KnGroupPropertiesDTO> corpGpInfoList;

    public List<KnGroupPropertiesDTO> getCorpGpInfoList() {
        return corpGpInfoList;
    }

    public void setCorpGpInfoList(List<KnGroupPropertiesDTO> corpGpInfoList) {
        this.corpGpInfoList = corpGpInfoList;
    }

    @Override
    public String toString() {
        return "KnBulkGroupPropertiesDTO{" +
                "corpGpInfoList=" + corpGpInfoList +
                '}';
    }
}
