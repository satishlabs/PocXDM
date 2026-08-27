/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpMdnListDTO.java
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

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class KnCorpMdnListPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676202L;

    private String mdn;
    private int corpId;
    private Collection<String> addedMdnList;
    private Collection<String> removedMdnList;
    private Collection<KnCorpSubscriberDTO> addedExternalMdnList;
    private Collection<String> removedExternalMdnList;
    private Set<String> completeMdns;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;

    public KnCorpMdnListPersistDTO() {
        completeMdns = new HashSet<String>();
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
        completeMdns.add(mdn);
    }


    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Collection<String> getAddedMdnList() {
        return addedMdnList;
    }

    public void setAddedMdnList(Collection<String> addedMdnList) {
        this.addedMdnList = addedMdnList;
        if (addedMdnList != null) {
            completeMdns.addAll(addedMdnList);
        }
    }

    public Collection<String> getRemovedMdnList() {
        return removedMdnList;
    }

    public void setRemovedMdnList(Collection<String> removedMdnList) {
        this.removedMdnList = removedMdnList;
        if (removedMdnList != null) {
            completeMdns.addAll(removedMdnList);
        }
    }

    public Collection<String> getMdnList() {
        return completeMdns;
    }

    public Collection<KnCorpSubscriberDTO> getAddedExternalMdnList() {
        return addedExternalMdnList;
    }

    public void setAddedExternalMdnList(Collection<KnCorpSubscriberDTO> addedExternalMdnList) {
        this.addedExternalMdnList = addedExternalMdnList;
    }

    public Collection<String> getRemovedExternalMdnList() {
        return removedExternalMdnList;
    }

    public void setRemovedExternalMdnList(Collection<String> removedExternalMdnList) {
        this.removedExternalMdnList = removedExternalMdnList;
    }

    public Set<String> getCompleteMdns() {
        return completeMdns;
    }

    public void setCompleteMdns(Set<String> completeMdns) {
        this.completeMdns = completeMdns;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    public String getObjectId() {
        return mdn;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(500);
        sb.append("InputDto - ").append(inputDTO)
                .append(", EntityId - ").append(entityId)
                .append(", OpetrationType - ").append(operationType)
                .append(", Profile - ").append(profile)
                .append(", PersostenceDTO - ").append(persistenceDTO)
                .append(", MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", CorpId - ").append(corpId)
                .append(", AddedMdnList - ").append(KnGDPRTemplate.mdnList(addedMdnList))
                .append(", RemovedMdnList - ").append(KnGDPRTemplate.mdnList(removedMdnList))
                .append(", AddedExtMdnList - ").append(addedExternalMdnList)
                .append(", RemovedExtMdnList - ").append(KnGDPRTemplate.mdnList(removedExternalMdnList))
                .append(", AllMdns - ").append(KnGDPRTemplate.mdnSet(completeMdns));
        return sb.toString();
    }
}
