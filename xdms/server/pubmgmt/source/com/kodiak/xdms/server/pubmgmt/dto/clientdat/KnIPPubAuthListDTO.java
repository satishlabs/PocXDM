/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnTargetMDNInfoDTO;

import java.util.List;

/**
 * Created by schandra on 19-12-2017.
 */
public class KnIPPubAuthListDTO implements IInputDTO {

    private String objectId;
    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;
    private String authMDN;
    private int ifMatch;
    private int ifNoneMatch;
    private String docEtag;
    private String mcpttId;
    private List<KnTargetMDNInfoDTO> targetMDNInfoDTOS;

    public List<KnTargetMDNInfoDTO> getTargetMDNInfoDTOS() {
        return targetMDNInfoDTOS;
    }

    public void setTargetMDNInfoDTOS(List<KnTargetMDNInfoDTO> targetMDNInfoDTOS) {
        this.targetMDNInfoDTOS = targetMDNInfoDTOS;
    }

    public String getAuthMDN() {
        return authMDN;
    }

    public void setAuthMDN(String authMDN) {
        this.authMDN = authMDN;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
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

    public int getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(int ifMatch) {
        this.ifMatch = ifMatch;
    }

    public int getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(int ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

	@Override
    public String toString() {
        return "KnIPPubAuthListDTO{" +
                "objectId='" + objectId + '\'' +
                ", performer='" + performer + '\'' +
                ", authDTO=" + authDTO +
                ", clientType=" + clientType +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", profile='" + profile + '\'' +
                ", authMDN='" + KnGDPRTemplate.mdn(authMDN) + '\'' +
                ", ifMatch=" + ifMatch +
                ", ifNoneMatch=" + ifNoneMatch +
                ", docEtag='" + docEtag + '\'' +
                ", mcpttId='" + KnGDPRTemplate.mcpttId(mcpttId) + '\'' +
                ", targetMDNInfoDTOS=" + targetMDNInfoDTOS +
                '}';
    }
}
