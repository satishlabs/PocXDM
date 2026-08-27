package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.List;
import java.util.Map;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonProperty;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

public class KnCorpPTTSettingDTO extends KnCorpResponseDTO {

    @JsonProperty(value ="id")
    private String _id;
    private String templateName;
    private long createTimeStamp;
    private long updateTimeStamp;
    private String type;
    private String ver;
    private Map<String, List<Integer>> pttSettingDocDetail;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public long getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public long getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(long updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Map<String, List<Integer>> getPttSettingDocDetail() {
        return pttSettingDocDetail;
    }

    public void setPttSettingDocDetail(Map<String, List<Integer>> pttSettingDocDetail) {this.pttSettingDocDetail = pttSettingDocDetail;}


    @Override
    public String toString() {
        return "KnCorpPTTSettingDTO{" +
                "_id='" + _id + '\'' +
                ", templateName='" + templateName +
                ", createTimeStamp=" + createTimeStamp +
                ", updateTimeStamp=" + updateTimeStamp +
                ", type='" + type +
                ", ver='" + ver +
                ", pttSettingDocDetail=" + pttSettingDocDetail +
                '}';
    }
}
