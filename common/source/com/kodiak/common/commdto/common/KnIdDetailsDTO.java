/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIdDetailsDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya      Dec 01, 2011      7.2
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
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.List;

public class KnIdDetailsDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622776197L;
    private int idKey;
    private String hierarchyId;
    private String extId;
    private String idName;
    private int idType;
    private KnIdDetailsListDTO idDetailsListDto;
    private int isStandAlone;
    private int corpId;
    private String objectId;
    private int parentId;
    private int nxtGenCatEnabled;
    private String segmentIndicator;

    private String alias;

    private String status;

    private String parentIdName;

    private List<String> geoCode;
    private List<String> addedGeoCode;
    private List<String> removedGeoCode;


    public int getIdKey() {
        return idKey;
    }

    public void setIdKey(int idKey) {
        this.idKey = idKey;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public KnIdDetailsListDTO getIdDetailsListDto() {
        return idDetailsListDto;
    }

    public void setIdDetailsListDto(KnIdDetailsListDTO idDetailsListDto) {
        this.idDetailsListDto = idDetailsListDto;
    }

    public int getStandAlone() {
        return isStandAlone;
    }

    public void setStandAlone(int standAlone) {
        isStandAlone = standAlone;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getObjectId() {
        return objectId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getNxtGenCatEnabled() {
        return nxtGenCatEnabled;
    }

    public void setNxtGenCatEnabled(int nxtGenCatEnabled) {
        this.nxtGenCatEnabled = nxtGenCatEnabled;
    }

    public String getSegmentIndicator() { return segmentIndicator; }

    public void setSegmentIndicator(String segmentIndicator) { this.segmentIndicator = segmentIndicator; }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParentIdName() {
        return parentIdName;
    }

    public void setParentIdName(String parentIdName) {
        this.parentIdName = parentIdName;
    }

    public List<String> getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(List<String> geoCode) {
        this.geoCode = geoCode;
    }

    public List<String> getAddedGeoCode() {
        return addedGeoCode;
    }

    public void setAddedGeoCode(List<String> addedGeoCode) {
        this.addedGeoCode = addedGeoCode;
    }

    public List<String> getRemovedGeoCode() {
        return removedGeoCode;
    }

    public void setRemovedGeoCode(List<String> removedGeoCode) {
        this.removedGeoCode = removedGeoCode;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(" idKey - ").append(idKey)
                .append(", hierarchyId - ").append(hierarchyId)
                .append(", extId - ").append(extId)
                .append(", idName - ").append(idName)
                .append(", idType - ").append(idType)
                .append(", idDetailsListDto - ").append(idDetailsListDto)
                .append(", isStandAlone - ").append(isStandAlone)
                .append(", corpId - ").append(corpId)
                .append(", parentId - ").append(parentId)
                .append(", Object_Id - ").append(objectId)
                .append(", nxtGenCatEnabled - ").append(nxtGenCatEnabled)
                .append(", Segment_Indicator - ").append(segmentIndicator)
                .append(", alias - ").append(alias)
                .append(", status - ").append(status)
                .append(", geoCode - ").append(geoCode)
                .append(", addedGeoCodes - ").append(addedGeoCode)
                .append(", removedGeoCodes - ").append(removedGeoCode)
                .append(", parentIdName - ").append(parentIdName);
        return strBuffer.toString();
    }
}
