/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import java.util.Collection;

public class KnIPCatPermissionSetDTO {
    private static final long serialVersionUID = 2208275347127086522L;
    private String extCorpId;
    private String entityId;
    private String operationType;
    private String profile;
    private String catAccessPermSet;
    private String catAccessPermUpdateTS;
    private int idType;
    private Collection<String> extIdList;

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getCatAccessPermSet() {
        return catAccessPermSet;
    }

    public void setCatAccessPermSet(String catAccessPermSet) {
        this.catAccessPermSet = catAccessPermSet;
    }

    public String getCatAccessPermUpdateTS() {
        return catAccessPermUpdateTS;
    }

    public void setCatAccessPermUpdateTS(String catAccessPermUpdateTS) {
        this.catAccessPermUpdateTS = catAccessPermUpdateTS;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public Collection<String> getExtIdList() {
        return extIdList;
    }

    public void setExtIdList(Collection<String> extIdList) {
        this.extIdList = extIdList;
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

    @Override
    public String toString() {
        return "KnIPCatPermissionSetDTO{" +
                "extCorpId='" + extCorpId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", catAccessPermSet='" + catAccessPermSet + '\'' +
                ", catAccessPermUpdateTS='" + catAccessPermUpdateTS + '\'' +
                ", idType=" + idType +
                ", extIdList=" + extIdList +
                '}';
    }
}
