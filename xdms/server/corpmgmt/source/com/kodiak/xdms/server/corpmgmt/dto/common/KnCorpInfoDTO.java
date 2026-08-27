/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpInfoDTO.java
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
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnConstants;
import java.util.Map;


public class KnCorpInfoDTO {

    private int corpId;
    private String extCorpId;

    private String idKey;
    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String pttRecording;
    private String dataRecording;
    private String videoRecording;
    private String selfDnDPrivilege;
    private String largeAgencyDispatch;

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
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

    public Map<String, Object> getCustomParamMap() {
		return customParamMap;
	}

	public void setCustomParamMap(Map<String, Object> customParamMap) {
		this.customParamMap = customParamMap;
	}

    public String getPttRecording() { return pttRecording; }

    public void setPttRecording(String pttRecording) { this.pttRecording = pttRecording; }

    public String getDataRecording() { return dataRecording; }

    public void setDataRecording(String dataRecording) { this.dataRecording = dataRecording; }

    public String getVideoRecording() { return videoRecording; }

    public void setVideoRecording(String videoRecording) { this.videoRecording = videoRecording; }

    public String getSelfDnDPrivilege() {
        return selfDnDPrivilege;
    }

    public void setSelfDnDPrivilege(String selfDnDPrivilege) {
        this.selfDnDPrivilege = selfDnDPrivilege;
    }

    public String getLargeAgencyDispatch() { return largeAgencyDispatch; }

    public void setLargeAgencyDispatch(String largeAgencyDispatch) { this.largeAgencyDispatch = largeAgencyDispatch; }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnCorpInfoDTO [corpId=");
		builder.append(corpId);
		builder.append(", extCorpId=");
		builder.append(extCorpId);
        builder.append(", idKey=");
        builder.append(idKey);
		builder.append(", customParamMap=");
		builder.append(customParamMap);
		builder.append(", hierarchyType=");
		builder.append(hierarchyType);
        builder.append(", pttRecording=");
        builder.append(pttRecording);
        builder.append(", dataRecording=");
        builder.append(dataRecording);
        builder.append(", videoRecording=");
        builder.append(videoRecording);
        builder.append(", selfDndPrivilege=");
        builder.append(selfDnDPrivilege);
        builder.append(", largeAgencyDispatch=");
        builder.append(largeAgencyDispatch);
		builder.append("]");
		return builder.toString();
	}
}
