package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.commdto.common.KnXDMPTTSettingHierarchyListDTO;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;

import java.util.List;
import java.util.Map;

public class KnIPCorpPTTSettingDTO implements IInputDTO {

    private String entityId;
    private String operationType;
    private String profile;
    private String performer;

    private String corpId;
    private String hierarchyId;
    private String pttSettingId;
    private String pttSettingName;
    private String mdn;
    private KnCorpPTTSettingDTO pttSettingInfo;
    private List<KnXDMPTTSettingHierarchyListDTO> pttSettingDocList;
    private List<String> mdnList;
    private List<String> pttSettingIdList;

    public KnIPCorpPTTSettingDTO() {
        this.pttSettingInfo = new KnCorpPTTSettingDTO();
    }
    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getPttSettingId() {
        return pttSettingId;
    }

    public void setPttSettingId(String pttSettingId) {
        this.pttSettingId = pttSettingId;
    }

    public String getPttSettingName() {
        return pttSettingName;
    }

    public void setPttSettingName(String pttSettingName) {
        this.pttSettingName = pttSettingName;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public KnCorpPTTSettingDTO getPttSettingInfo() {
        if (pttSettingInfo == null) {
            pttSettingInfo = new KnCorpPTTSettingDTO();
        }
        return pttSettingInfo;
    }

    public void setPttSettingInfo(KnCorpPTTSettingDTO pttSettingInfo) {
        this.pttSettingInfo = pttSettingInfo;
    }

    public List<KnXDMPTTSettingHierarchyListDTO> getPttSettingDocList() {
        return pttSettingDocList;
    }

    public void setPttSettingDocList(List<KnXDMPTTSettingHierarchyListDTO> pttSettingDocList) {
        this.pttSettingDocList = pttSettingDocList;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public List<String> getPttSettingIdList() {
        return pttSettingIdList;
    }

    public void setPttSettingIdList(List<String> pttSettingIdList) {
        this.pttSettingIdList = pttSettingIdList;
    }

    @Override
    public void setPerformer(String performer) {
        this.performer = performer;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

    }

    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }

    @Override
    public void setClientType(int clientType) {

    }

    @Override
    public int getClientType() {
        return 0;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }


    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String getObjectId() {
        return "";
    }

    @Override
    public String toString() {
        return "KnIPCorpPTTSettingDTO{" +
                "entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", performer='" + performer + '\'' +
                ", corpId='" + corpId + '\'' +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", pttSettingId='" + pttSettingId + '\'' +
                ", pttSettingName='" + pttSettingName + '\'' +
                ", mdn='" + mdn + '\'' +
                ", pttSettingInfo=" + pttSettingInfo +
                ", pttSettingDocList=" + pttSettingDocList +
                ", mdnList=" + mdnList +
                '}';
    }
}
