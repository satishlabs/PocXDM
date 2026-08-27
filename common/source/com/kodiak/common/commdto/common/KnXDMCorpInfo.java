/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpInfo.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
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
import com.kodiak.common.resources.KnConstants;

import java.util.Map;

public class KnXDMCorpInfo implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676127L;

    private String corpId;
    private String extCorpId;

    private String idKey;
    private String eTag;
    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String version;
    private String corpName;
    private String corpFS2;
    private String xdmCorpFS2Set;

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getObjectId() {
        return corpId;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getCorpFS2() {
        return corpFS2;
    }

    public void setCorpFS2(String corpFS2) {
        this.corpFS2 = corpFS2;
    }

    public String getXdmCorpFS2Set() {
        return xdmCorpFS2Set;
    }

    public void setXdmCorpFS2Set(String xdmCorpFS2Set) {
        this.xdmCorpFS2Set = xdmCorpFS2Set;
    }

    @Override
    public String toString() {
        return "KnXDMCorpInfo{" +
                "corpId='" + corpId + '\'' +
                ", extCorpId='" + extCorpId + '\'' +
                ", idKey='" + idKey + '\'' +
                ", eTag='" + eTag + '\'' +
                ", customParamMap=" + customParamMap +
                ", hierarchyType=" + hierarchyType +
                ", version='" + version + '\'' +
                ", corpName='" + corpName + '\'' +
                ", corpFS2='" + corpFS2 + '\'' +
                ", xdmCorpFS2Set='" + xdmCorpFS2Set + '\'' +
                '}';
    }
}
