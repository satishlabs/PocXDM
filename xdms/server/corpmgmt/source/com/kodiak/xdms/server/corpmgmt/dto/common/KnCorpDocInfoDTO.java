/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpAppInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Mar 11, 2011      7.0
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


public class KnCorpDocInfoDTO {

    private String etag;
    private String userId;
    private String docName;
    private String xcapRoot;
    private int groupType;
    private int groupCreatedBy;
    private int mcxGroupInd;
    private int externalCorpGroup;
    private int isPreConfiguredGroup;
    private Integer videoPermission;

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }
    public int getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(int isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public int getExternalCorpGroup() {
        return externalCorpGroup;
    }

    public void setExternalCorpGroup(int externalCorpGroup) {
        this.externalCorpGroup = externalCorpGroup;
    }

    public int getMcxGroupInd() {
        return mcxGroupInd;
    }

    public void setMcxGroupInd(int mcxGroupInd) {
        this.mcxGroupInd = mcxGroupInd;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getXcapRoot() {
        return xcapRoot;
    }

    public void setXcapRoot(String xcapRoot) {
        this.xcapRoot = xcapRoot;
    }
    public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	public int getGroupCreatedBy() {
		return groupCreatedBy;
	}

	public void setGroupCreatedBy(int groupCreatedBy) {
		this.groupCreatedBy = groupCreatedBy;
	}

	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
                .append(", userId - ").append(userId)
                .append(", docName - ").append(docName)
                .append(", etag - ").append(etag)
                .append(", xcapRoot - ").append(xcapRoot)
                .append(", groupType - ").append(groupType)
                .append(", groupCreatedBy - ").append(groupCreatedBy)
                .append(", mcxGroupInd - ").append(mcxGroupInd)
                .append(", externalCorpGroup - ").append(externalCorpGroup)
                .append(", isPreConfiguredGroup - ").append(isPreConfiguredGroup)
                .append(", videoPermission - ").append(videoPermission);
        return sb.toString();
    }
}
