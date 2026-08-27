package com.kodiak.xdms.server.common.dto.common;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnGroupPropertiesDTO {
    private Integer grpId;
    private String recordingFs;
    private String ugwInterop;

    public Integer getGrpId() {
        return grpId;
    }

    public void setGrpId(Integer grpId) {
        this.grpId = grpId;
    }

    public String getRecordingFs() {
        return recordingFs;
    }

    public void setRecordingFs(String recordingFs) {
        this.recordingFs = recordingFs;
    }

    public String getUgwInterop() {
        return ugwInterop;
    }

    public void setUgwInterop(String ugwInterop) {
        this.ugwInterop = ugwInterop;
    }

    @Override
    public String toString() {
        return "KnGroupPropetiesDTO{" +
                "grpId=" + grpId +
                ", recordingFs='" + recordingFs + '\'' +
                ", ugwInterop='" + ugwInterop + '\'' +
                '}';
    }
}
