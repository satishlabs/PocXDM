/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnExtSubscriberDTO;

import java.util.List;

/**
 * Created by Deepak on 8/4/14.
 */
public class KnExtSubsPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = -9094541985322756328L;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String profile;
    private List<KnExtSubscriberDTO> extSubs;

    public KnExtSubsPersistDTO() {
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    @Override
    public void setInputDTO(IInputDTO inputDTO) {

    }

    @Override
    public IInputDTO getInputDTO() {
        return null;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<KnExtSubscriberDTO> getExtSubs() {
        return extSubs;
    }

    public void setExtSubs(List<KnExtSubscriberDTO> extSubs) {
        this.extSubs = extSubs;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnExtSubsPersistDTO{" +
                "persistenceDTO=" + persistenceDTO +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", profile='" + profile + '\'' +
                ", extSubs=" + extSubs +
                '}';
    }
}
