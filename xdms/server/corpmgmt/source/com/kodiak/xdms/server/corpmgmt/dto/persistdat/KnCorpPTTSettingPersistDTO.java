package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;

import java.util.Collection;
import java.util.List;

public class KnCorpPTTSettingPersistDTO extends KnCorpPTTSettingDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471111622676200L;

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String profile;

    private String dbPTTSettingName;
    private String reqPTTSettingName;

    private List<String> reqPTTdocIds;
    private List<KnPTTSettingDocInfoDTO> pttSettingDTOList;

    public String getDbPTTSettingName() {
        return dbPTTSettingName;
    }

    public void setDbPTTSettingName(String dbPTTSettingName) {
        this.dbPTTSettingName = dbPTTSettingName;
    }

    public String getReqPTTSettingName() {
        return reqPTTSettingName;
    }

    public void setReqPTTSettingName(String reqPTTSettingName) {
        this.reqPTTSettingName = reqPTTSettingName;
    }

    public List<String> getReqPTTdocIds() {
        return reqPTTdocIds;
    }

    public void setReqPTTdocIds(List<String> reqPTTdocIds) {
        this.reqPTTdocIds = reqPTTdocIds;
    }

    public List<KnPTTSettingDocInfoDTO> getPttSettingDTOList() {
        return pttSettingDTOList;
    }

    public void setPttSettingDTOList(List<KnPTTSettingDocInfoDTO> pttSettingDTOList) {
        this.pttSettingDTOList = pttSettingDTOList;
    }

    @Override
    public String getObjectId() {
        return null;
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
    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    @Override
    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    @Override
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    @Override
    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
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
    public String toString() {
        return "KnCorpPTTSettingPersistDTO{" +
                "dbPTTSettingName='" + dbPTTSettingName + '\'' +
                ", reqPTTSettingName='" + reqPTTSettingName + '\'' +
                ", reqPTTdocIds=" + reqPTTdocIds +
                ", pttSettingDTOList=" + pttSettingDTOList +
                '}';
    }
}
