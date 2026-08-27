package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.List;

public class KnXDMPTTSettingHierarchyListDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155822676133L;


    private String hierarchyId;
    private List<String> pttSettingDocList;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public List<String> getPttSettingDocList() {
        return pttSettingDocList;
    }

    public void setPttSettingDocList(List<String> pttSettingDocList) {
        this.pttSettingDocList = pttSettingDocList;
    }

    @Override
    public String toString() {
        return "KnXDMPTTSettingHierarchyListDTO{" +
                "hierarchyId='" + hierarchyId + '\'' +
                ", pttSettingDocList=" + pttSettingDocList +
                '}';
    }

    @Override
    public String getObjectId() {
        return "";
    }
}
