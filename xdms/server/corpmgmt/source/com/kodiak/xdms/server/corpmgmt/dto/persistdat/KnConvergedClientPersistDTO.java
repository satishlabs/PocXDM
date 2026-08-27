/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

/**
 * ************************************************************************
 * <p>
 * File name:  KnConvergedClientPersistDTO.java
 * Subsystem:  PoCXDM
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 15, 2016                8.1.2
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnConvergedClientPersistDTO implements IPersistenceDTO {

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;

    private String subsMdn;
    private int subsClientType;
    private int subsCorpId;
    private int convClientEnabled;
    private boolean enabledPttRadio;
    private int clientConfigEnabled;
    private int licenseType;
    private String commandPackageCode;

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
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

    public String getObjectId() {
        return null;
    }

    public String getSubsMdn() {
        return subsMdn;
    }

    public void setSubsMdn(String subsMdn) {
        this.subsMdn = subsMdn;
    }

    public int getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(int subsClientType) {
        this.subsClientType = subsClientType;
    }

    public int getSubsCorpId() {
        return subsCorpId;
    }

    public void setSubsCorpId(int subsCorpId) {
        this.subsCorpId = subsCorpId;
    }

    public int getConvClientEnabled() {
        return convClientEnabled;
    }

    public void setConvClientEnabled(int convClientEnabled) {
        this.convClientEnabled = convClientEnabled;
    }

    public boolean isEnabledPttRadio() {
        return enabledPttRadio;
    }

    public void setEnabledPttRadio(boolean enabledPttRadio) {
        this.enabledPttRadio = enabledPttRadio;
    }

    public int getClientConfigEnabled() {
        return clientConfigEnabled;
    }

    public void setClientConfigEnabled(int clientConfigEnabled) {
        this.clientConfigEnabled = clientConfigEnabled;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public String getCommandPackageCode() {
        return commandPackageCode;
    }

    public void setCommandPackageCode(String commandPackageCode) {
        this.commandPackageCode = commandPackageCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("InputDTO - ").append(inputDTO)
                .append(", subsMdn - ").append(KnGDPRTemplate.mdn(subsMdn))
                .append(", subsClientType - ").append(subsClientType)
                .append(", subsCorpId - ").append(subsCorpId)
                .append(", convClientEnabled - ").append(convClientEnabled)
                .append(", enabledPttRadio - ").append(enabledPttRadio)
                .append(", clientConfigEnabled - ").append(clientConfigEnabled)
                .append(", licenseType - ").append(licenseType)
                .append(", commandPackageCode - ").append(commandPackageCode);
        return sb.toString();
    }
}