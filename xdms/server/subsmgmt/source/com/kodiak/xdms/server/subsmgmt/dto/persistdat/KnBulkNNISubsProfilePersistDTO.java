/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnNNISubsProfileDTO;

import java.util.List;

/**
 * Created by kdeepak on 07-05-2015.
 */
public class KnBulkNNISubsProfilePersistDTO implements IPersistenceDTO {

    List<String> mdns;
    KnNNISubsProfileDTO nniSubsProfile;

    @Override
    public void setInputDTO(IInputDTO inputDTO) {

    }

    @Override
    public IInputDTO getInputDTO() {
        return null;
    }

    @Override
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {

    }

    @Override
    public IPersistenceDTO getPersistenceDTO() {
        return null;
    }

    @Override
    public void setOperationType(String operationType) {

    }

    @Override
    public String getOperationType() {
        return null;
    }

    @Override
    public String getEntityId() {
        return null;
    }

    @Override
    public void setEntityId(String entityId) {

    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String getProfile() {
        return null;
    }

    @Override
    public void setProfile(String profile) {

    }

    public List<String> getMdns() {
        return mdns;
    }

    public void setMdns(List<String> mdns) {
        this.mdns = mdns;
    }

    public KnNNISubsProfileDTO getNniSubsProfile() {
        return nniSubsProfile;
    }

    public void setNniSubsProfile(KnNNISubsProfileDTO nniSubsProfile) {
        this.nniSubsProfile = nniSubsProfile;
    }
}
