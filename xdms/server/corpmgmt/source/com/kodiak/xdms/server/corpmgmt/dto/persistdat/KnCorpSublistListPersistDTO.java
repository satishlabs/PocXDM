/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistListDTO.java
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class KnCorpSublistListPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676203L;

    private int sublistId;
    private String mdn;
    private int corpId;
    private Collection<Integer> addedSublistIds;
    private Collection<Integer> removedSublistIds;
    private String privateContactList;
    private Set<Integer> completeSublistIds;

    public KnCorpSublistListPersistDTO() {
        completeSublistIds = new HashSet<Integer>();
    }

    public int getSublistId() {
        return sublistId;
    }

    public void setSublistId(int sublistId) {
        this.sublistId = sublistId;
        completeSublistIds.add(sublistId);
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Collection<Integer> getAddedSublistIds() {
        return addedSublistIds;
    }

    public void setAddedSublistIds(Collection<Integer> addedSublistIds) {
        this.addedSublistIds = addedSublistIds;
        completeSublistIds.addAll(addedSublistIds);
    }

    public Collection<Integer> getRemovedSublistIds() {
        return removedSublistIds;
    }

    public void setRemovedSublistIds(Collection<Integer> removedSublistIds) {
        this.removedSublistIds = removedSublistIds;
        completeSublistIds.addAll(removedSublistIds);
    }

    public String getPrivateContactList() {
        return privateContactList;
    }

    public void setPrivateContactList(String privateContactList) {
        this.privateContactList = privateContactList;
    }

    public Set<Integer> getCompleteSublistIds() {
        return completeSublistIds;
    }

    public void setCompleteSublistIds(Set<Integer> completeSublistIds) {
        this.completeSublistIds = completeSublistIds;
    }

    public Collection<Integer> getSublistIdsList() {
        return completeSublistIds;
    }


    public void setInputDTO(IInputDTO inputDTO) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public IInputDTO getInputDTO() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public IPersistenceDTO getPersistenceDTO() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setOperationType(String operationType) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getOperationType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getEntityId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setEntityId(String entityId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getProfile() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setProfile(String profile) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getObjectId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
     @Override
     public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("sublistId - ").append(sublistId)
                .append(", mdn").append(KnGDPRTemplate.mdn(mdn))
                .append(", corpId").append(corpId)
                .append(", addedSublistIds").append(addedSublistIds)
                .append(", removedSublistIds").append(removedSublistIds)
                .append(", privateContactList").append(privateContactList);
        return sb.toString();


    }
}



