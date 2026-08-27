/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 22/5/13
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnCorpGenActivationPersistDTO extends KnCorpInfoDTO implements IPersistenceDTO {
    private static final long serialVersionUID = 7526471155622676329L;
    private int maxMembersAllowed;
    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;
    private Collection<KnCorpSubscriberDTO> extMdnList;
    private int subsCount;
    private Collection<KnCorpSubscriberDTO> subscriberAuthStatusList;
    private Collection<String> pocMdnList;
    private String allowedClientTypes;
    private int deviceSharingFlag;

    public Collection<KnCorpSubscriberDTO> getSubscriberInfoList() {
        return subscriberAuthStatusList;
    }

    public void setSubscriberInfoList(Collection<KnCorpSubscriberDTO> subscriberInfoList) {
        this.subscriberAuthStatusList = subscriberInfoList;
    }

    public Collection<KnCorpSubscriberDTO> getSubscriberAuthStatusList() {
        return subscriberAuthStatusList;
    }

    public void setSubscriberAuthStatusList(Collection<KnCorpSubscriberDTO> subscriberAuthStatusList) {
        this.subscriberAuthStatusList = subscriberAuthStatusList;
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

    public Collection<String> getPocMdnList() {
        return pocMdnList;
    }

    public void setPocMdnList(Collection<String> pocMdnList) {
        this.pocMdnList = pocMdnList;
    }

    public Collection<KnCorpSubscriberDTO> getExtMdnList() {
        return extMdnList;
    }

    public void setExtMdnList(Collection<KnCorpSubscriberDTO> extMdnList) {
        this.extMdnList = extMdnList;
    }

    public int getSubsCount() {
        return subsCount;
    }

    public void setSubsCount(int subsCount) {
        this.subsCount = subsCount;
    }

    public int getMaxMembersAllowed() {
        return maxMembersAllowed;
    }

    public void setMaxMembersAllowed(int maxMembersAllowed) {
        this.maxMembersAllowed = maxMembersAllowed;
    }

    public String getAllowedClientTypes() {
        return allowedClientTypes;
    }

    public void setAllowedClientTypes(String allowedClientTypes) {
        this.allowedClientTypes = allowedClientTypes;
    }

    public int getDeviceSharingFlag() {
        return deviceSharingFlag;
    }

    public void setDeviceSharingFlag(int deviceSharingFlag) {
        this.deviceSharingFlag = deviceSharingFlag;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", subscriberInfoList - ").append(subscriberAuthStatusList)
                .append(", InputDTO - ").append(inputDTO)
                .append(", EntityId - ").append(entityId)
                .append(", OperationType - ").append(operationType)
                .append(", Profile - ").append(profile)
                .append(", PersistDTO - ").append(persistenceDTO)
                .append(", pocMdnList - ").append(KnGDPRTemplate.mdnList(pocMdnList))
                .append(", extMdnList - ").append(extMdnList)
                .append(", deviceSharingFlag - ").append(deviceSharingFlag);
        return sb.toString();
    }
}
