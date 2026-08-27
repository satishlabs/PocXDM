/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
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

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class KnMdnDetailsPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676205L;

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String mdn;
    private Collection<String> mdnList;
    private Collection<KnCorpSubscriberDTO> externalMdnList;
    private int contactCount;
    private String entityId;
    private String operationType;
    private String profile;
    private Collection<KnCorpSubscriberDTO> addedMdnDTO;
    private List<String> nniSubscrList;
    private boolean grpMdnPresent;
    private List<String> groupMdnList;
    private boolean onlyAddedGroupMember;
    private List<String> tpMdnList;
    private Map<String,Integer> mdnClientTypeMap;

    public Map<String, Integer> getMdnClientTypeMap() {
        return mdnClientTypeMap;
    }

    public void setMdnClientTypeMap(Map<String, Integer> mdnClientTypeMap) {
        this.mdnClientTypeMap = mdnClientTypeMap;
    }

    public List<String> getTpMdnList() {
		return tpMdnList;
	}


	public void setTpMdnList(List<String> tpMdnList) {
		this.tpMdnList = tpMdnList;
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
        return persistenceDTO;
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
        return null;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Collection<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(Collection<String> mdnList) {
        this.mdnList = mdnList;
    }

    public Collection<KnCorpSubscriberDTO> getExternalMdnList() {
        return externalMdnList;
    }

    public void setExternalMdnList(Collection<KnCorpSubscriberDTO> externalMdnList) {
        this.externalMdnList = externalMdnList;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public Collection<KnCorpSubscriberDTO> getAddedMdnDTO() {
        return addedMdnDTO;
    }

    public void setAddedMdnDTO(Collection<KnCorpSubscriberDTO> addedMdnDTO) {
        this.addedMdnDTO = addedMdnDTO;
    }

    public List<String> getNniSubscrList() {
        return nniSubscrList;
    }

    public void setNniSubscrList(List<String> nniSubscrList) {
        this.nniSubscrList = nniSubscrList;
    }

    public boolean isGrpMdnPresent() {
        return grpMdnPresent;
    }

    public void setGrpMdnPresent(boolean grpMdnPresent) {
        this.grpMdnPresent = grpMdnPresent;
    }

    public List<String> getGroupMdnList() {
        return groupMdnList;
    }

    public void setGroupMdnList(List<String> groupMdnList) {
        this.groupMdnList = groupMdnList;
    }

    public boolean isOnlyAddedGroupMember() {
        return onlyAddedGroupMember;
    }

    public void setOnlyAddedGroupMember(boolean onlyAddedGroupMember) {
        this.onlyAddedGroupMember = onlyAddedGroupMember;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("InputDTO - ").append(inputDTO);
        sb.append("groupMdnList - ").append(KnGDPRTemplate.mdnList(groupMdnList));
              //  .append(", persistenceDTO - ").append(persistenceDTO)
               // .append(", Mdn - ").append(KnGDPRTemplate.mdn(mdn))
                //.append(", MdnList - ").append(KnGDPRTemplate.mdnList(mdnList))
               // .append(", ExternalMdnList - ").append(externalMdnList)
                //.append(", ContactCount - ").append(contactCount)
                //.append(", entityId - ").append(entityId)
               // .append(", operationType - ").append(operationType)
               // .append(", profile - ").append(profile)
                //.append(", addedMdnDTO - ").append(addedMdnDTO);
        return sb.toString();
    }
}
