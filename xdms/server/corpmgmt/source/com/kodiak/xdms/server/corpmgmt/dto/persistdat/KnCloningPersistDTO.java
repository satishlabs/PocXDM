/**
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 * <p>
 * ************************************************************************
 * <p/>
 * File name:  KnContactDetailsPersistDTO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        24-01-2011      7.0
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
 * <p>
 * ************************************************************************
 * <p/>
 * File name:  KnContactDetailsPersistDTO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        24-01-2011      7.0
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
/**
 * ************************************************************************
 * <p/>
 * File name:  KnContactDetailsPersistDTO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        24-01-2011      7.0
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

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.HashMap;
import java.util.Map;


public class KnCloningPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676197L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;

    private String profile;

    private IPersistenceDTO persistenceDTO;


    private KnCorpSubscriberDTO subscDto;

    public KnCorpSubscriberDTO getSubscDto() {
        return subscDto;
    }

    public void setSubscDto(KnCorpSubscriberDTO subscDto) {
        this.subscDto = subscDto;
    }

    @Override
    public String getObjectId() {
        return subscDto.getMdn();
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

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

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
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;

    }


    //common

    private int subscriberCount;

    private String sourceMdn;

    private String targetMdn;

    public String getSourceMdn() {
        return sourceMdn;
    }

    public void setSourceMdn(String sourceMdn) {
        this.sourceMdn = sourceMdn;
    }

    public String getTargetMdn() {
        return targetMdn;
    }

    public void setTargetMdn(String targetMdn) {
        this.targetMdn = targetMdn;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }
    private String cloningBitset;

    private Map<String, Integer> mdnClientTypeMap;

    Map<String, Integer> mdnCorpIdMap;

    Map<String, String> mdnTierPackageMap;

    Map<Integer, Boolean> validationMap = new HashMap<>();

    public Map<Integer, Boolean> getValidationMap() {
        return validationMap;
    }

    public void setValidationMap(Map<Integer, Boolean> validationMap) {
        this.validationMap = validationMap;
    }
    //For contact validation

    private KnContactDetailsPersistDTO contactCloningPersistDTO = new KnContactDetailsPersistDTO();


    //For updating corp admin validation

    private KnContactDetailsPersistDTO updateCorpAdminFsPersistDTO = new KnContactDetailsPersistDTO();

    //for group validation
    private KnCorpBulkGroupInfoPersistDTO groupInfoPersistDTO = new KnCorpBulkGroupInfoPersistDTO();

    //emergency attribute validation
    KnCorpMcpttFeaturePersistDTO emergencyMcpttFeaturePersistDTO = new KnCorpMcpttFeaturePersistDTO();

    //for permission validation persist dto
    KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = new KnCorpMcpttFeaturePersistDTO();

    //Getters and Setters


    public KnCorpMcpttFeaturePersistDTO getAuthUserListPersistDTO() {
        return authUserListPersistDTO;
    }

    public void setAuthUserListPersistDTO(KnCorpMcpttFeaturePersistDTO authUserListPersistDTO) {
        this.authUserListPersistDTO = authUserListPersistDTO;
    }

    public KnCorpMcpttFeaturePersistDTO getEmergencyMcpttFeaturePersistDTO() {
        return emergencyMcpttFeaturePersistDTO;
    }

    public void setEmergencyMcpttFeaturePersistDTO(KnCorpMcpttFeaturePersistDTO emergencyMcpttFeaturePersistDTO) {
        this.emergencyMcpttFeaturePersistDTO = emergencyMcpttFeaturePersistDTO;
    }

    public KnCorpBulkGroupInfoPersistDTO getGroupInfoPersistDTO() {
        return groupInfoPersistDTO;
    }

    public void setGroupInfoPersistDTO(KnCorpBulkGroupInfoPersistDTO groupInfoPersistDTO) {
        this.groupInfoPersistDTO = groupInfoPersistDTO;
    }


    public Map<String, Integer> getMdnClientTypeMap() {
        return mdnClientTypeMap;
    }

    public void setMdnClientTypeMap(Map<String, Integer> mdnClientTypeMap) {
        this.mdnClientTypeMap = mdnClientTypeMap;
    }

    public Map<String, String> getMdnTierPackageMap() {
        return mdnTierPackageMap;
    }

    public void setMdnTierPackageMap(Map<String, String> mdnTierPackageMap) {
        this.mdnTierPackageMap = mdnTierPackageMap;
    }

    public Map<String, Integer> getMdnCorpIdMap() {
        return mdnCorpIdMap;
    }

    public void setMdnCorpIdMap(Map<String, Integer> mdnCorpIdMap) {
        this.mdnCorpIdMap = mdnCorpIdMap;
    }


    public KnContactDetailsPersistDTO getContactCloningPersistDTO() {
        return contactCloningPersistDTO;
    }

    public void setContactCloningPersistDTO(KnContactDetailsPersistDTO contactCloningPersistDTO) {
        this.contactCloningPersistDTO = contactCloningPersistDTO;
    }

    public KnContactDetailsPersistDTO getUpdateCorpAdminFsPersistDTO() {
        return updateCorpAdminFsPersistDTO;
    }

    public void setUpdateCorpAdminFsPersistDTO(KnContactDetailsPersistDTO updateCorpAdminFsPersistDTO) {
        this.updateCorpAdminFsPersistDTO = updateCorpAdminFsPersistDTO;
    }


    public String getCloningBitset() {
        return cloningBitset;
    }

    public void setCloningBitset(String cloningBitset) {
        this.cloningBitset = cloningBitset;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("KnCloningPersistDTO{");
        sb.append("inputDTO=").append(inputDTO);
        sb.append(", entityId='").append(entityId).append('\'');
        sb.append(", operationType='").append(operationType).append('\'');
        sb.append(", profile='").append(profile).append('\'');
        sb.append(", persistenceDTO=").append(persistenceDTO);
        sb.append(", subscDto=").append(subscDto);
        sb.append(", subscriberCount=").append(subscriberCount);
        sb.append(", cloningBitset='").append(cloningBitset).append('\'');
        sb.append(", mdnClientTypeMap=").append(mdnClientTypeMap);
        sb.append(", mdnCorpIdMap=").append(mdnCorpIdMap);
        sb.append(", mdnTierPackageMap=").append(mdnTierPackageMap);
        sb.append(", validationMap=").append(validationMap);
        sb.append(", contactCloningPersistDTO=").append(contactCloningPersistDTO);
        sb.append(", updateCorpAdminFsPersistDTO=").append(updateCorpAdminFsPersistDTO);
        sb.append(", groupInfoPersistDTO=").append(groupInfoPersistDTO);
        sb.append(", emergencyMcpttFeaturePersistDTO=").append(emergencyMcpttFeaturePersistDTO);
        sb.append(", authUserListPersistDTO=").append(authUserListPersistDTO);
        sb.append('}');
        return sb.toString();
    }
}
