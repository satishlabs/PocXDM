/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPSubscriberDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;

import java.util.List;
import java.util.Map;

public class KnIPCorpContactDTO extends KnCorpContactDTO implements IInputDTO {

   private static final long serialVersionUID = 7526471155622676186L;

    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    //private int clientType;
    private String profile;
    private String performer;
    private String newMdn;
    private int newSubscriptionType;
    private int newClientType;
    private int publicSubscriptionType;
    private int newPublicSubscriptionType;
    private int corpSubscriptionType;
    private int newCorpSubscriptionType;
    private int newCorpId;
    private Boolean autoPairingFlag;
    private String xdmsHome;
    private String protocolVersion;
    private boolean bulkReq;
    private String subsActiveFS2;
    private Integer groupId;
    private List<String> groupURIList;
    private Integer nextToken;
    private Integer fetchSize;

    private Map<String, Object> customParamMap;

    public int getNewCorpId() {
        return newCorpId;
    }

    public void setNewCorpId(int newCorpId) {
        this.newCorpId = newCorpId;
    }

    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
        int subsType = mapPubSubscriptionType(publicSubscriptionType, this.corpSubscriptionType);
        setSubscriptionType(subsType);
    }

    public void setCorpSubscriptionType(int corpSubscriptionType) {
        this.corpSubscriptionType = corpSubscriptionType;
        int subsType = mapCorpSubscriptionType(corpSubscriptionType, this.publicSubscriptionType);
        setSubscriptionType(subsType);
    }

    public void setNewPublicSubscriptionType(int newPublicSubscriptionType) {
        this.newPublicSubscriptionType = newPublicSubscriptionType;
        int subsType = mapPubSubscriptionType(newPublicSubscriptionType, this.newCorpSubscriptionType);
        setNewSubscriptionType(subsType);
    }

    public void setNewCorpSubscriptionType(int newCorpSubscriptionType) {
        this.newCorpSubscriptionType = newCorpSubscriptionType;
        int subsType = mapCorpSubscriptionType(newCorpSubscriptionType, this.newPublicSubscriptionType);
        setNewSubscriptionType(subsType);
    }

    private int mapPubSubscriptionType(int pubSubsType, int corpSubsType) {
        int existingType = getSubscriptionType();
        //int corpSubsType = corpSubscriptionType;
        int newSubsType = pubSubsType + corpSubsType;
        switch (newSubsType) {
            case 0:
                existingType = 0;
                break;
            case 1:
                switch (corpSubsType) {
                    case 0:
                        existingType = 0;
                        break;
                    case 1:
                        existingType = 1;
                        break;
                }
                break;
            case 2:
                existingType = 2;
                break;
            default:
                existingType = newSubsType;
        }
        return existingType;
    }

    private int mapCorpSubscriptionType(int corpSubsType, int pubSubsType) {
        int existingType = getSubscriptionType();
        //int pubSubsType = publicSubscriptionType;
        int newSubsType = pubSubsType + corpSubsType;
        switch (newSubsType) {
            case 0:
                existingType = 0;
                break;
            case 1:
                switch (pubSubsType) {
                    case 0:
                        existingType = 1;
                        break;
                    case 1:
                        existingType = 0;
                        break;
                }
                break;
            case 2:
                existingType = 2;
                break;
            default:
                existingType = newSubsType;
        }
        return existingType;
    }

    public int getNewSubscriptionType() {
        return newSubscriptionType;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /* public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }*/

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getNewMdn() {
        return newMdn;
    }

    public void setNewMdn(String newMdn) {
        this.newMdn = newMdn;
    }

    public String getObjectId() {
        return "" + getCorpId();
    }

    public void setNewSubscriptionType(int newSubscriptionType) {
        this.newSubscriptionType = newSubscriptionType;
    }

    public int getNewClientType() {
        return newClientType;
    }

    public void setNewClientType(int newClientType) {
        this.newClientType = newClientType;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public Boolean getAutoPairingFlag() {
        return autoPairingFlag;
    }

    public void setAutoPairingFlag(Boolean autoPairingFlag) {
        this.autoPairingFlag = autoPairingFlag;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean getBulkReq() {
        return bulkReq;
    }

    public void setBulkReq(boolean bulkReq) {
        this.bulkReq = bulkReq;
    }

    @Override
    public String getSubsActiveFS2() {
        return subsActiveFS2;
    }

    @Override
    public void setSubsActiveFS2(String subsActiveFS2) {
        this.subsActiveFS2 = subsActiveFS2;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<String> getGroupURIList() {
        return groupURIList;
    }

    public void setGroupURIList(List<String> groupURIList) {
        this.groupURIList = groupURIList;
    }

    public Integer getNextToken() {
        return nextToken;
    }

    public void setNextToken(Integer nextToken) {
        this.nextToken = nextToken;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
               /* .append(", AuthDTO - ").append(authDTO)
                .append(", EntityId - ").append(entityId)
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(getClientType())
                .append(", Profile - ").append(profile)
                .append(", Performer - ").append(performer)*/
                .append(", NewMdn - ").append(KnGDPRTemplate.mdn(newMdn))
                .append(", newClientType - ").append(newClientType)
                .append(", newSubscriptionType - ").append(newSubscriptionType)
                .append(", newCorpId - ").append(newCorpId)
                .append(", publicSubscriptionType - ").append(publicSubscriptionType)
                .append(", newPublicSubscriptionType - ").append(newPublicSubscriptionType)
                .append(", corpSubscriptionType - ").append(corpSubscriptionType)
                .append(", newCorpSubscriptionType - ").append(newCorpSubscriptionType)
                .append(", customParamMap - ").append(customParamMap)
                /*.append(", xdmsHome - ").append(xdmsHome)*/
                .append(", autoPairingFlag - ").append(autoPairingFlag)
                .append(", protocolVersion - ").append(protocolVersion)
                .append(", subsActiveFS2 - ").append(subsActiveFS2)
                .append(", groupId - ").append(groupId)
                .append(", groupURIList - ").append(groupURIList)
                .append(", nextToken - ").append(nextToken)
                .append(", fetchSize - ").append(fetchSize);
        return sb.toString();
    }
}
