/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpActivationPersistDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya      Nov 28, 2011      7.2
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
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpMailInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.sql.Timestamp;
import java.util.Collection;

public class KnCorpActivationPersistDTO extends KnCorpInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676198L;

    private String subscriberEmail;
    private KnCorpMailInfoDTO mailInfoDTO;
    private String lang;
    private String activationCode;
    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;
    private int serviceAuthStatus;
    private int clientType;
    private int subscriberCount;
    Collection<KnCorpSubscriberDTO> subscriberAuthStatusList;
    private Collection<String> pocMdnList;
    private String allowedClientTypes;
    private String dbOTP;
    private boolean isDispatchTypeEnabled;
    private int dispatchType;
    private int licenseType;
    private Timestamp expTimeStamp;
    private int deviceSharingFlag;
    private int clientPVMajorVersion;

    public String getSubscriberEmail() {
        return subscriberEmail;
    }

    public void setSubscriberEmail(String subscriberEmail) {
        this.subscriberEmail = subscriberEmail;
    }

    public KnCorpMailInfoDTO getMailInfoDTO() {
        return mailInfoDTO;
    }

    public void setMailInfoDTO(KnCorpMailInfoDTO mailInfoDTO) {
        this.mailInfoDTO = mailInfoDTO;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

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
        return this.persistenceDTO;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return this.operationType;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getObjectId() {
        return "" + super.getCorpId();
    }

    public String getProfile() {
        return this.profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public Collection<KnCorpSubscriberDTO> getSubscriberAuthStatusList() {
        return subscriberAuthStatusList;
    }

    public void setSubscriberAuthStatusList(Collection<KnCorpSubscriberDTO> subscriberAuthStatusList) {
        this.subscriberAuthStatusList = subscriberAuthStatusList;
    }

    public Collection<String> getPocMdnList() {
        return pocMdnList;
    }

    public void setPocMdnList(Collection<String> pocMdnList) {
        this.pocMdnList = pocMdnList;
    }

    public Timestamp getExpTimeStamp() {
        return expTimeStamp;
    }

    public void setExpTimeStamp(Timestamp expTimeStamp) {
        this.expTimeStamp = expTimeStamp;
    }

    public String getAllowedClientTypes() {
        return allowedClientTypes;
    }

    public void setAllowedClientTypes(String allowedClientTypes) {
        this.allowedClientTypes = allowedClientTypes;
    }

    public String getDbOTP() {
        return dbOTP;
    }

    public void setDbOTP(String dbOTP) {
        this.dbOTP = dbOTP;
    }

    public boolean isDispatchTypeEnabled() {
        return isDispatchTypeEnabled;
    }

    public void setDispatchTypeEnabled(boolean dispatchTypeEnabled) {
        isDispatchTypeEnabled = dispatchTypeEnabled;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public int getDeviceSharingFlag() {
        return deviceSharingFlag;
    }

    public void setDeviceSharingFlag(int deviceSharingFlag) {
        this.deviceSharingFlag = deviceSharingFlag;
    }

    public int getClientPVMajorVersion() {
        return clientPVMajorVersion;
    }

    public void setClientPVMajorVersion(int clientPVMajorVersion) {
        this.clientPVMajorVersion = clientPVMajorVersion;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", subscriberEmail - ").append(KnGDPRTemplate.email(subscriberEmail))
                .append(", mailInfoDTO - ").append(mailInfoDTO)
                .append(", lang - ").append(lang)
                .append(", activationCode - ").append(activationCode)
                .append(", InputDTO - ").append(inputDTO)
                .append(", EntityId - ").append(entityId)
                .append(", OperationType - ").append(operationType)
                .append(", Profile - ").append(profile)
                .append(", serviceAuthStatus - ").append(serviceAuthStatus)
                .append(", clientType - ").append(clientType)
                .append(", subscriberCount - ").append(subscriberCount)
                .append(", PersistDTO - ").append(persistenceDTO)
                .append(", dbOTP - ").append(dbOTP)
                .append(", dispatchType - ").append(dispatchType)
                .append(", licenseType - ").append(licenseType)
                .append(", deviceSharingFlag - ").append(deviceSharingFlag)
                .append(", clientPVMajorVersion - ").append(clientPVMajorVersion);
        return sb.toString();
    }
}
