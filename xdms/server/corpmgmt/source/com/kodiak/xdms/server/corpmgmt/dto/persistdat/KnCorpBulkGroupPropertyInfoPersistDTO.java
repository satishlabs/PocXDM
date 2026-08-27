/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupPropertiesDTO;

import java.util.List;

public class KnCorpBulkGroupPropertyInfoPersistDTO extends KnCorpGroupInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155989676199L;

    private IInputDTO inputDTO;

    private String entityId;

    private String operationType;

    private String profile;

    private IPersistenceDTO performerDetails;

    private List<KnCorpGroupPropertiesDTO> groupPropertyInfoDTOS;

    private List<Integer> dbGroupIdList;

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    @Override
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {

    }

    @Override
    public IPersistenceDTO getPersistenceDTO() {
        return null;
    }

    public void setInputDTO(IInputDTO inputDTO) {
        this.inputDTO = inputDTO;
    }

    public List<KnCorpGroupPropertiesDTO> getGroupPropertyInfoDTOS() {
        return groupPropertyInfoDTOS;
    }

    public void setGroupPropertyInfoDTOS(List<KnCorpGroupPropertiesDTO> groupPropertyInfoDTOS) {
        this.groupPropertyInfoDTOS = groupPropertyInfoDTOS;
    }

    public List<Integer> getDbGroupIdList() {
        return dbGroupIdList;
    }

    public void setDbGroupIdList(List<Integer> dbGroupIdList) {
        this.dbGroupIdList = dbGroupIdList;
    }


    public IPersistenceDTO getPerformerDetails() {
        return performerDetails;
    }

    public void setPerformerDetails(IPersistenceDTO performerDetails) {
        this.performerDetails = performerDetails;
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
    public String toString() {
        return "KnCorpBulkGroupPropertyInfoPersistDTO{" +
                "inputDTO=" + inputDTO.toString() +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", performerDetails=" + performerDetails +
                ", groupPropertyInfoDTOS=" + groupPropertyInfoDTOS +
                ", dbGroupIdList=" + dbGroupIdList +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }



}
