/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

/**
 * Created by nnamita on 4/13/15.
 */
public class KnPoCLinkedGroupListPersistDTO implements IPersistenceDTO {

    //poc nni interface enabled indicator
    private boolean pocIntfEnabled;
    //Account Id
    private int accountId;
    //etag of the gateway linked
    private long gwEtag;
    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;

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

    public boolean isPocIntfEnabled() {
        return pocIntfEnabled;
    }

    public void setPocIntfEnabled(boolean pocIntfEnabled) {
        this.pocIntfEnabled = pocIntfEnabled;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public long getGwEtag() { return gwEtag; }

    public void setGwEtag(long gwEtag) { this.gwEtag = gwEtag; }
}
