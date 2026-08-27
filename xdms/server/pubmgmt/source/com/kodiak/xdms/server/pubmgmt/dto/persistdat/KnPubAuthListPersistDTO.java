/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnTargetMDNInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by schandra on 21-12-2017.
 */
public class KnPubAuthListPersistDTO implements IPersistenceDTO {

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String profile;
    private String objectId;
    private String mdn;
    private Integer corpId;
    private List<KnTargetMDNInfoDTO> targetMDNInfoDTOS;
    private Map<String, Integer> mdnCorpIdMap;
    private List<KnMemberDTO> pocMembers;

    public List<KnMemberDTO> getPocMembers() {
        return pocMembers;
    }

    public void setPocMembers(List<KnMemberDTO> pocMembers) {
        this.pocMembers = pocMembers;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
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

    public String getObjectId() {
        return objectId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public List<KnTargetMDNInfoDTO> getTargetMDNInfoDTOS() {
        return targetMDNInfoDTOS;
    }

    public void setTargetMDNInfoDTOS(List<KnTargetMDNInfoDTO> targetMDNInfoDTOS) {
        this.targetMDNInfoDTOS = targetMDNInfoDTOS;
    }

    public Map<String, Integer> getMdnCorpIdMap() {
        return mdnCorpIdMap;
    }

    public void setMdnCorpIdMap(Map<String, Integer> mdnCorpIdMap) {
        this.mdnCorpIdMap = mdnCorpIdMap;
    }

    @Override
    public String toString() {
        return "KnPubAuthListPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", persistenceDTO=" + persistenceDTO +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", profile='" + profile + '\'' +
                ", objectId='" + objectId + '\'' +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", corpId=" + corpId +
                ", targetMDNInfoDTOS=" + targetMDNInfoDTOS +
                ", mdnCorpIdMap=" + KnGDPRTemplate.mapKeyMdn(mdnCorpIdMap) +
                ", pocmembers=" + pocMembers +
                '}';
    }
}
