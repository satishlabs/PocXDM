/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMemberDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 28, 2011           7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
public class KnXDMMemberDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676134L;

    String uri;
    String displayName;
    String ufmi;
    private String entryType;
    private String contactType;
    private String clientType;
    private String userAgent;
    private String activeFS;
    private Integer unConfirmedPull;
    private Integer cameraType;
    private Boolean preConfiguredGroupUseOnly;

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

    public String getObjectId() {
        return null;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(String activeFS) {
        this.activeFS = activeFS;
    }

    public Integer getUnConfirmedPull() {
        return unConfirmedPull;
    }

    public void setUnConfirmedPull(Integer unConfirmedPull) {
        this.unConfirmedPull = unConfirmedPull;
    }

    public Integer getCameraType() {return cameraType; }

    public void setCameraType(Integer cameraType) {this.cameraType = cameraType; }

    public Boolean getPreConfiguredGroupUseOnly() {
        return preConfiguredGroupUseOnly;
    }

    public void setPreConfiguredGroupUseOnly(Boolean preConfiguredGroupUseOnly) {
        this.preConfiguredGroupUseOnly = preConfiguredGroupUseOnly;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(50);
        strBuffer.append(" URI - ").append(uri)
                .append(", DisplayName - ").append(displayName)
                .append(", entryType - ").append(entryType)
                .append(", contactType - ").append(contactType)
                .append(", clientType - ").append(clientType)
                .append(", userAgent - ").append(userAgent)
                .append(", activeFS - ").append(activeFS)
                .append(", unConfirmedPull - ").append(unConfirmedPull)
                .append("ufmi - ").append(ufmi)
                .append("cameraType - ").append(cameraType)
                .append("preConfiguredGroupUseOnly - ").append(preConfiguredGroupUseOnly);
        return strBuffer.toString();
    }

}
