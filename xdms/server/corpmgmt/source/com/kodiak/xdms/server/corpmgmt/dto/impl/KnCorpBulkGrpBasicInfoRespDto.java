package com.kodiak.xdms.server.corpmgmt.dto.impl;


import java.util.Map;

public class KnCorpBulkGrpBasicInfoRespDto extends KnCorpResponseDTO {
    private Map<Integer, KnCorpGrpBasicInfoRespDto> corpGrpBasicInfoRespDtoMap;

    public Map<Integer, KnCorpGrpBasicInfoRespDto> getCorpGrpBasicInfoRespDtoMap() {
        return corpGrpBasicInfoRespDtoMap;
    }

    public void setCorpGrpBasicInfoRespDtoMap(Map<Integer, KnCorpGrpBasicInfoRespDto> corpGrpBasicInfoRespDtoMap) {
        this.corpGrpBasicInfoRespDtoMap = corpGrpBasicInfoRespDtoMap;
    }

    @Override
    public String toString() {
        return "KnCorpBulkGrpBasicInfoRespDto{" +
                "corpGrpBasicInfoRespDtoMap=" + corpGrpBasicInfoRespDtoMap +
                '}';
    }
}
