package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.Collection;


public class KnAllocateSubsPersistDTO implements IPersistenceDTO {
    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;

    private String profile;

    private IPersistenceDTO persistenceDTO;
    private Collection<String> pocSubscMdnList;
    private Collection<String> geoCodes;
    private boolean isHierarchyCorpMapped;
    private boolean isHierarchyIdMatching;

    public boolean isHierarchyIdMatching() {
        return isHierarchyIdMatching;
    }

    public void setHierarchyIdMatching(boolean hierarchyIdMatching) {
        isHierarchyIdMatching = hierarchyIdMatching;
    }

    public boolean isHierarchyCorpMapped() {
        return isHierarchyCorpMapped;
    }

    public void setHierarchyCorpMapped(boolean hierarchyCorpMapped) {
        isHierarchyCorpMapped = hierarchyCorpMapped;
    }

    public Collection<String> getGeoCodes() {
        return geoCodes;
    }

    public void setGeoCodes(Collection<String> geoCodes) {
        this.geoCodes = geoCodes;
    }

    public Collection<String> getPocSubscMdnList() {
        return pocSubscMdnList;
    }

    public void setPocSubscMdnList(Collection<String> pocSubscMdnList) {
        this.pocSubscMdnList = pocSubscMdnList;
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
    public String getObjectId() {
        return "";
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
        return "KnAllocateSubsPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", persistenceDTO=" + persistenceDTO +
                ", pocSubscMdnList=" + pocSubscMdnList +
                ", geoCodes=" + geoCodes +
                ", isHierarchyCorpMapped=" + isHierarchyCorpMapped +
                ", isHierarchyIdMatching=" + isHierarchyIdMatching +
                '}';
    }
}
