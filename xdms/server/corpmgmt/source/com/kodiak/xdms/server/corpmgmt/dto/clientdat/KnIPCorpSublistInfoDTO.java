/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPCorpSublistInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 19, 2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;


public class KnIPCorpSublistInfoDTO extends KnCorpSublistDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676194L;

    private String newSublistName;
    private Collection<String> addedMdnList;
    private LinkedList<String> removedMdnList;
    private Collection<Integer> addedSublistIds;
    private Collection<KnCorpSubscriberDTO> externalContacts;
    private Collection<KnCorpSubscriberDTO> finalMemberList;

    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    private int clientType;
    private String profile;
    private String performer;
    private String hierarchyId;


    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    private Map<String, Object> customParamMap;

    public String getNewSublistName() {
        return newSublistName;
    }

    public void setNewSublistName(String newSublistName) {
        this.newSublistName = newSublistName;
    }

    public Collection<String> getAddedMdnList() {
        return addedMdnList;
    }

    public void setAddedMdnList(Collection<String> addedMdnList) {
        this.addedMdnList = addedMdnList;
    }

    public LinkedList<String> getRemovedMdnList() {
        return removedMdnList;
    }

    public void setRemovedMdnList(LinkedList<String> removedMdnList) {
        this.removedMdnList = removedMdnList;
    }

    public Collection<Integer> getAddedSublistIds() {
        return addedSublistIds;
    }

    public void setAddedSublistIds(Collection<Integer> addedSublistIds) {
        this.addedSublistIds = addedSublistIds;
    }

    public Collection<KnCorpSubscriberDTO> getExternalContacts() {
        return externalContacts;
    }

    public void setExternalContacts(Collection<KnCorpSubscriberDTO> externalContacts) {
        this.externalContacts = externalContacts;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getClientType() {
        return clientType;
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
        return "" + getCorpId();
    }

    public Collection<KnCorpSubscriberDTO> getFinalMemberList() {
        return finalMemberList;
    }

    public void setFinalMemberList(Collection<KnCorpSubscriberDTO> finalMemberList) {
        this.finalMemberList = finalMemberList;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
                .append(", AuthDTO - ").append(authDTO)
                .append(", EntityId - ").append(entityId)
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", Profile - ").append(profile)
                .append(", Performer - ").append(performer)
                .append(", newSublistName - ").append(newSublistName)
                .append(", addedMdnList - ").append(KnGDPRTemplate.mdnList(addedMdnList))
                .append(", removedMdnList - ").append(KnGDPRTemplate.mdnList(removedMdnList))
                .append(", addedSublistIds - ").append(addedSublistIds)
                .append(", removedMdnList - ").append(KnGDPRTemplate.mdnList(removedMdnList))
                .append(", finalMemberList - ").append(finalMemberList)
                .append(", customParamMap - ").append(customParamMap);
        return sb.toString();
    }
}
