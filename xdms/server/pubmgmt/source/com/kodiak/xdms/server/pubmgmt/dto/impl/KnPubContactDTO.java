/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.impl;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.pubmgmt.dto.intf.IOwnerDTO;
import com.kodiak.xdms.server.pubmgmt.dto.intf.IContactListIdentifier;

import java.io.InputStream;
import java.util.List;

public class KnPubContactDTO implements IOwnerDTO, IContactListIdentifier, IIdentifier {

    private static final long serialVersionUID = 7526471155622676219L;

    private String objectId;
    private String owner;
    private int resourceListId;
    private int contactListId;
    private String contactListName;
    private String contactListDisplayName;
    private String contactEntryUri;
    private int contactListType;
    private InputStream xmlStream;
    private int etag;
    private int ifMatch;
    private int ifNoneMatch;
    private String strXml;
    private String acrtepc;
    private String mcpttId;
    private List<KnXDMMdnInfoDTO> contactsList;


	public List<KnXDMMdnInfoDTO> getContactsList() {
        return contactsList;
    }

    public void setContactsList(List<KnXDMMdnInfoDTO> contactsList) {
        this.contactsList = contactsList;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getResourceListId() {
        return resourceListId;
    }

    public void setResourceListId(int resourceListId) {
        this.resourceListId = resourceListId;
    }

    public int getContactListId() {
        return contactListId;
    }

    public void setContactListId(int contactListId) {
        this.contactListId = contactListId;
    }

    public int getContactListType() {
        return contactListType;
    }

    public void setContactListType(int contactListType) {
        this.contactListType = contactListType;
    }

    public String getContactListName() {
        return contactListName;
    }

    public void setContactListName(String contactListName) {
        this.contactListName = contactListName;
    }

    public String getContactListDisplayName() {
        return contactListDisplayName;
    }

    public void setContactListDisplayName(String contactListDisplayName) {
        this.contactListDisplayName = contactListDisplayName;
    }

    public InputStream getXmlStream() {
        return xmlStream;
    }

    public void setXmlStream(InputStream xmlStream) {
        this.xmlStream = xmlStream;
    }

    public int getEtag() {
        return etag;
    }

    public void setEtag(int etag) {
        this.etag = etag;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getStrXml() {
        return strXml;
    }

    public void setStrXml(String strXml) {
        this.strXml = strXml;
    }

    public String getContactEntryUri() {
        return contactEntryUri;
    }

    public void setContactEntryUri(String contactEntryUri) {
        this.contactEntryUri = contactEntryUri;
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


    public String getAcrtepc() {
		return acrtepc;
	}

	public void setAcrtepc(String acrtepc) {
		this.acrtepc = acrtepc;
	}

	public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnPubContactDTO{");
        sb.append("objectId='").append(objectId).append('\'');
        sb.append(", owner='").append(KnGDPRTemplate.mdn(owner)).append('\'');
        sb.append(", resourceListId=").append(resourceListId);
        sb.append(", contactListId=").append(contactListId);
        sb.append(", contactListName='").append(contactListName).append('\'');
        sb.append(", contactListDisplayName='").append(contactListDisplayName).append('\'');
        sb.append(", contactEntryUri='").append(contactEntryUri).append('\'');
        sb.append(", contactListType=").append(contactListType);
        sb.append(", xmlStream=").append(xmlStream);
        sb.append(", etag=").append(etag);
        sb.append(", ifMatch=").append(ifMatch);
        sb.append(", ifNoneMatch=").append(ifNoneMatch);
        sb.append(", contactsList=").append(contactsList);
        sb.append(", StrXml - ").append(strXml);
        sb.append(", acrtepc - ").append(acrtepc);
        sb.append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId));
        sb.append('}');
        return sb.toString();
    }
}