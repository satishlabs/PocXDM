/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnAppDetailsDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 15, 2011           7.0
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
public class KnAppDetailsDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676146L;

    private String auid;
    private String xcapRoot;
    private String docName;
    private Integer isAbdgGroup;
    private Integer mcxGroupInd;
    private Integer externalCorpGroup;
    private Integer isPreConfiguredGroup;
    private Integer groupType;
    private Integer groupCreatedBy;

    public Integer getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(Integer isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public Integer getExternalCorpGroup() {
        return externalCorpGroup;
    }

    public void setExternalCorpGroup(Integer externalCorpGroup) {
        this.externalCorpGroup = externalCorpGroup;
    }

    public Integer getMcxGroupInd() {
        return mcxGroupInd;
    }

    public void setMcxGroupInd(Integer mcxGroupInd) {
        this.mcxGroupInd = mcxGroupInd;
    }

    private long etag;


    public String getAuid() {
        return auid;
    }

    public void setAuid(String auid) {
        this.auid = auid;
    }

    public String getXcapRoot() {
        return xcapRoot;
    }

    public void setXcapRoot(String xcapRoot) {
        this.xcapRoot = xcapRoot;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public long getEtag() {
        return etag;
    }

    public void setEtag(long etag) {
        this.etag = etag;
    }

	public Integer getIsAbdgGroup() {
		return isAbdgGroup;
	}

	public void setIsAbdgGroup(Integer isAbdgGroup) {
		this.isAbdgGroup = isAbdgGroup;
	}

    public Integer getGroupType() { return groupType; }

    public void setGroupType(Integer groupType) { this.groupType = groupType; }

    public Integer getGroupCreatedBy() { return groupCreatedBy; }

    public void setGroupCreatedBy(Integer groupCreatedBy) { this.groupCreatedBy = groupCreatedBy; }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(super.toString());
        strBuffer.append(" auid - ").append(auid)
                .append(", xcapRoot - ").append(xcapRoot)
                .append(", docName - ").append(docName)
                .append(", Etag - ").append(etag)
        		.append(", isAbdgGroup - ").append(isAbdgGroup)
        		.append(", mcxGroupInd - ").append(mcxGroupInd)
                .append(", externalCorpGroup - ").append(externalCorpGroup)
                .append(", isPreConfiguredGroup - ").append(isPreConfiguredGroup)
                .append(", groupType - ").append(groupType)
                .append(", groupCreatedBy - ").append(groupCreatedBy);
        return strBuffer.toString();
    }

    public String getObjectId() {
        return auid;  
    }
}
