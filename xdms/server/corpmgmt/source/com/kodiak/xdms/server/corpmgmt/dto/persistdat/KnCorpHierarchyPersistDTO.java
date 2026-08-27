/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.List;
import java.util.Map;

public class KnCorpHierarchyPersistDTO implements IPersistenceDTO {
    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;
    private List<String> geoCodeList;
    private List<String> inputHierarchyNameList;
    private List<String> corpHierarchyNameList;
    private int maxHierarchyHorozontalLevel;
    private int maxHierarchyVerticalLevel;
    private Map<String,Integer> systemClusterId;




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
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
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
    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    @Override
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public List<String> getGeoCodeList() {
        return geoCodeList;
    }

    public void setGeoCodeList(List<String> geoCodeList) {
        this.geoCodeList = geoCodeList;
    }

    @Override
    public String getObjectId() {
        return "";
    }

    public int getMaxHierarchyHorozontalLevel() {
        return maxHierarchyHorozontalLevel;
    }

    public void setMaxHierarchyHorozontalLevel(int maxHierarchyHorozontalLevel) {
        this.maxHierarchyHorozontalLevel = maxHierarchyHorozontalLevel;
    }

    public int getMaxHierarchyVerticalLevel() {
        return maxHierarchyVerticalLevel;
    }

    public void setMaxHierarchyVerticalLevel(int maxHierarchyVerticalLevel) {
        this.maxHierarchyVerticalLevel = maxHierarchyVerticalLevel;
    }

    public List<String> getInputHierarchyNameList() {
        return inputHierarchyNameList;
    }

    public void setInputHierarchyNameList(List<String> inputHierarchyNameList) {
        this.inputHierarchyNameList = inputHierarchyNameList;
    }

    public List<String> getCorpHierarchyNameList() {
        return corpHierarchyNameList;
    }

    public void setCorpHierarchyNameList(List<String> corpHierarchyNameList) {
        this.corpHierarchyNameList = corpHierarchyNameList;
    }

    public Map<String, Integer> getSystemClusterId() {
        return systemClusterId;
    }

    public void setSystemClusterId(Map<String, Integer> systemClusterId) {
        this.systemClusterId = systemClusterId;
    }

    @Override
    public String toString() {
        return "KnCorpHierarchyPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", persistenceDTO=" + persistenceDTO +
                ", geoCodeList=" + geoCodeList +
                ", inputHierarchyNameList=" + inputHierarchyNameList +
                ", corpHierarchyNameList=" + corpHierarchyNameList +
                ", maxHierarchyHorozontalLevel=" + maxHierarchyHorozontalLevel +
                ", maxHierarchyVerticalLevel=" + maxHierarchyVerticalLevel +
                ", systemClusterId=" + systemClusterId +
                '}';
    }
}
