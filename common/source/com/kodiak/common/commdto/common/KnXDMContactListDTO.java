/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.commdto.request.KnPUBContactAddonAliasDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMContactListDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 14, 2011           7.0
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
public class KnXDMContactListDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676122L;

    private String ownerMdn;
    private String contactListName;
    private String contactListDisplayName;
    private String contactEntryUri;
    private int contactListType;
    private int etag;
    private int ifMatch;
    private int ifNoneMatch;
    private List<KnXDMMdnInfoDTO> contactsList;
    private String strXml;
    private String mcpttId;
    private HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> contactAddonAliasMap;

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
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

    public int getContactListType() {
        return contactListType;
    }

    public void setContactListType(int contactListType) {
        this.contactListType = contactListType;
    }

    public int getEtag() {
        return etag;
    }

    public void setEtag(int etag) {
        this.etag = etag;
    }


    public List<KnXDMMdnInfoDTO> getContactsList() {
        return contactsList;
    }

    public void setContactsList(List<KnXDMMdnInfoDTO> contactsList) {
        this.contactsList = contactsList;
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

    public HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> getContactAddonAliasMap() {return contactAddonAliasMap; }

    public void setContactAddonAliasMap(HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> contactAddonAliasMap) {this.contactAddonAliasMap = contactAddonAliasMap; }

    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnXDMContactListDTO{");
        sb.append("ownerMdn='").append(KnGDPRTemplate.mdn(ownerMdn)).append('\'');
        sb.append(", contactListName='").append(contactListName).append('\'');
        sb.append(", contactListDisplayName='").append(contactListDisplayName).append('\'');
        sb.append(", contactEntryUri='").append(contactEntryUri).append('\'');
        sb.append(", contactListType=").append(contactListType);
        sb.append(", etag=").append(etag);
        sb.append(", ifMatch=").append(ifMatch);
        sb.append(", ifNoneMatch=").append(ifNoneMatch);
        sb.append(", contactsList=").append(contactsList);
        sb.append(", contactAddonAliasMap= ").append(contactAddonAliasMap);
        sb.append(", StrXml - ").append(strXml);
        sb.append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId));
        sb.append('}');
        return sb.toString();
    }

    public String getObjectId() {
        return ownerMdn;
    }
}
