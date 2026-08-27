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

public class KnIPMCSDTO implements IInputDTO {

    private String objectId;
    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String userAgent;
    private String corpID;
    private String groupID;
    private String gmsFQDN;
    private String operationType;
    private String entityId;
    private String profile;
    private String mcId;
    private String mcpttID;
    private int ifMatch;
    private int ifNoneMatch;
    private String docEtag;
    private String fileName;
    private String clientFS2;

    public String getObjectId() { return objectId; }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public String getUserAgent() {return userAgent; }

    public void setUserAgent(String userAgent) {this.userAgent = userAgent; }

    public String getCorpID() {return corpID; }

    public void setCorpID(String corpID) {
        this.corpID = corpID;
    }

    public String getGroupID() {return groupID; }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGmsFQDN() {return gmsFQDN; }

    public void setGmsFQDN(String gmsFQDN) {
        this.gmsFQDN = gmsFQDN;
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

    public String getMcId() {return mcId; }

    public void setMcId(String mcId) {this.mcId = mcId; }

    public String getMcpttID() {return mcpttID; }

    public void setMcpttID(String mcpttID) {
        this.mcpttID = mcpttID;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClientFS2() { return clientFS2; }

    public void setClientFS2(String clientFS2) {this.clientFS2 = clientFS2; }

    @Override
    public String toString() {
        return "KnIPMCSDTO{" +
                "objectId='" + objectId + '\'' +
                ", performer='" + performer + '\'' +
                ", authDTO=" + authDTO +
                ", clientType=" + clientType +
                ", userAgent='" + userAgent + '\'' +
                ", corpID='" + corpID + '\'' +
                ", groupID='" + groupID + '\'' +
                ", gmsFQDN='" + gmsFQDN + '\'' +
                ", operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", profile='" + profile + '\'' +
                ", mcId='" + KnGDPRTemplate.mcId(mcId) + '\'' +
                ", mcpttID='" + KnGDPRTemplate.mcpttId(mcpttID) + '\'' +
                ", ifMatch=" + ifMatch +
                ", ifNoneMatch=" + ifNoneMatch +
                ", docEtag='" + docEtag + '\'' +
                ", fileName='" + fileName + '\'' +
                ", clientFS2='" + clientFS2 + '\'' +
                '}';
    }
}
