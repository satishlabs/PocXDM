/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

/**
 * Created by kdeepak on 07-05-2015.
 */
public class KnNNISubsProfileDTO implements IPersistenceDTO {


    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String profile;
    private String mdn;
    private String externalId;
    private Integer profileId;
    private Long nniActiveFs;


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

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public Long getNniActiveFs() {
        return nniActiveFs;
    }

    public void setNniActiveFs(Long nniActiveFs) {
        this.nniActiveFs = nniActiveFs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnNNISubsProfileDTO{");
        sb.append("nniActiveFs=").append(nniActiveFs);
        sb.append(", profileId=").append(profileId);
        sb.append(", externalId='").append(externalId).append('\'');
        sb.append(", mdn='").append(KnGDPRTemplate.mdn(mdn)).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

