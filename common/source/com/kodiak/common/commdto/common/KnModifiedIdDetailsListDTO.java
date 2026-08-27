/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.io.Serial;
import java.util.List;

public class KnModifiedIdDetailsListDTO implements IIdentifier {

    @Serial
    private static final long serialVersionUID = 6584487182715217659L;

    private String idKey;
    private String idName;
    private String alias;

    private KnAddedChildRelationDTO addedChildRelation;
    private List<String> removedChildRelation;
    private List<String> removedGeoCode;
    private List<String> addedGeoCode;

    @Override
    public String getObjectId() {
        return "";
    }

    public KnModifiedIdDetailsListDTO() {
    }

    public KnModifiedIdDetailsListDTO(String idKey, String idName, String alias,
                                      KnAddedChildRelationDTO addedChildRelation, List<String> removedChildRelation,
                                      List<String> removedGeoCode, List<String> addedGeoCode) {
        this.idKey = idKey;
        this.idName = idName;
        this.alias = alias;
        this.addedChildRelation = addedChildRelation;
        this.removedChildRelation = removedChildRelation;
        this.removedGeoCode = removedGeoCode;
        this.addedGeoCode = addedGeoCode;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public KnAddedChildRelationDTO getAddedChildRelation() {
        return addedChildRelation;
    }

    public void setAddedChildRelation(KnAddedChildRelationDTO addedChildRelation) {
        this.addedChildRelation = addedChildRelation;
    }

    public List<String> getRemovedChildRelation() {
        return removedChildRelation;
    }

    public void setRemovedChildRelation(List<String> removedChildRelation) {
        this.removedChildRelation = removedChildRelation;
    }

    public List<String> getRemovedGeoCode() {
        return removedGeoCode;
    }

    public void setRemovedGeoCode(List<String> removedGeoCode) {
        this.removedGeoCode = removedGeoCode;
    }

    public List<String> getAddedGeoCode() {
        return addedGeoCode;
    }

    public void setAddedGeoCode(List<String> addedGeoCode) {
        this.addedGeoCode = addedGeoCode;
    }

    @Override
    public String toString() {
        return "KnModifiedIdDetailsListDTO{" +
                "idKey='" + idKey + '\'' +
                ", idName='" + idName + '\'' +
                ", alias='" + alias + '\'' +
                ", addedChildRelation=" + addedChildRelation +
                ", removedChildRelation=" + removedChildRelation +
                ", removedGeoCode=" + removedGeoCode +
                ", addedGeoCode=" + addedGeoCode +
                '}';
    }
}
