/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.impl;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.pubmgmt.dto.intf.IOwnerDTO;
import com.kodiak.xdms.server.pubmgmt.dto.intf.IGroupIdentifier;

import java.io.InputStream;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubGroupDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 10, 2011        7.0
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

public class KnPubGroupDTO implements IOwnerDTO, IGroupIdentifier, IIdentifier {

    private static final long serialVersionUID = 7526471155622676220L;

    private String objectId;
    private String owner;
    private int groupId;
    private int groupDocId;
    private String groupDisplayName;
    private String groupName;
    private int groupType;
    private String listServiceURI;
    private String groupDocURI;
    private String docSelectorURI;
    private String nodeSelectorURI;
    private String strXml;
    private InputStream xmlDoc;
    private int groupDocEtag;
    private int ifMatch;
    private int ifNoneMatch;
    private String acrtepg;
    private String mcPttId;


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getGroupDocId() {
        return groupDocId;
    }

    public void setGroupDocId(int groupDocId) {
        this.groupDocId = groupDocId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupDisplayName() {
        return groupDisplayName;
    }

    public void setGroupDisplayName(String groupDisplayName) {
        this.groupDisplayName = groupDisplayName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getListServiceURI() {
        return listServiceURI;
    }

    public void setListServiceURI(String listServiceURI) {
        this.listServiceURI = listServiceURI;
    }

    public String getDocSelectorURI() {
        return docSelectorURI;
    }

    public void setDocSelectorURI(String docSelectorURI) {
        this.docSelectorURI = docSelectorURI;
    }

    public String getNodeSelectorURI() {
        return nodeSelectorURI;
    }

    public void setNodeSelectorURI(String nodeSelectorURI) {
        this.nodeSelectorURI = nodeSelectorURI;
    }

    public String getStrXml() {
        return strXml;
    }

    public void setStrXml(String strXml) {
        this.strXml = strXml;
    }

    public int getGroupDocEtag() {
        return groupDocEtag;
    }

    public void setGroupDocEtag(int groupDocEtag) {
        this.groupDocEtag = groupDocEtag;
    }

	public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(int ifMatch) {
        this.ifMatch = ifMatch;
    }

    public int getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(int ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public String getGroupDocURI() {
        return groupDocURI;
    }

    public void setGroupDocURI(String groupDocURI) {
        this.groupDocURI = groupDocURI;
    }

    public InputStream getXmlDoc() {
        return xmlDoc;
    }

    public void setXmlDoc(InputStream xmlDoc) {
        this.xmlDoc = xmlDoc;
    }

	public String getAcrtepg() {
		return acrtepg;
	}

	public void setAcrtepg(String acrtepg) {
		this.acrtepg = acrtepg;
	}

	public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", ObjectId - ").append(objectId);
        strBuffer.append(", OwnerMdn - ").append(KnGDPRTemplate.mdn(owner));
        strBuffer.append(", GroupId - ").append(groupId);
        strBuffer.append(", GroupDocId - ").append(groupDocId);
        strBuffer.append(", GroupDisplayName - ").append(groupDisplayName);
        strBuffer.append(", GroupName - ").append(groupName);
        strBuffer.append(", GroupType - ").append(groupType);
        strBuffer.append(", ListServiceURI - ").append(listServiceURI);
        strBuffer.append(", Group Doc URI - ").append(groupDocURI);
        strBuffer.append(", DocSelector - ").append(docSelectorURI);
        strBuffer.append(", NodeSelector - ").append(nodeSelectorURI);
        strBuffer.append(", XmlStream - ").append(strXml);
        strBuffer.append(", GroupDocEtag - ").append(groupDocEtag);
        strBuffer.append(", IfMatch - ").append(ifMatch);
        strBuffer.append(", IfNoneMatch - ").append(ifNoneMatch);
        strBuffer.append(", xmlDoc - ").append(xmlDoc);
        strBuffer.append(", acrtepg - ").append(acrtepg);

        return strBuffer.toString();
    }

}