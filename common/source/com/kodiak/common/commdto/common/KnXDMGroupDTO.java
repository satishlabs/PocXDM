/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMGroupDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 17, 2011           7.0
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
public class KnXDMGroupDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676131L;

    private String ownerMdn;
    private String groupName;
    private String groupDisplayName;
    private int pocGroupId;
    private int groupType;
    private int etag;
    private String docURI;
    private String listServiceUri;
    private String strXml;
    private int ifMatch;
    private int ifNoneMatch;
    private Integer groupmemberCount;
    private Integer avatarId;
    private Integer isAbdgGroup;
    private String abdgGroupOwnerUri;
    private String abdgGroupOwnerDispName;

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
    }

    public int getPocGroupId() {
        return pocGroupId;
    }

    public void setPocGroupId(int pocGroupId) {
        this.pocGroupId = pocGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDisplayName() {
        return groupDisplayName;
    }

    public void setGroupDisplayName(String groupDisplayName) {
        this.groupDisplayName = groupDisplayName;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public int getEtag() {
        return etag;
    }

    public void setEtag(int etag) {
        this.etag = etag;
    }

    public String getDocURI() {
        return docURI;
    }

    public void setDocURI(String docURI) {
        this.docURI = docURI;
    }

    public String getListServiceUri() {
        return listServiceUri;
    }

    public void setListServiceUri(String listServiceUri) {
        this.listServiceUri = listServiceUri;
    }

    public String getStrXml() {
        return strXml;
    }

    public void setStrXml(String strXml) {
        this.strXml = strXml;
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

    public Integer getGroupmemberCount() {
        return groupmemberCount;
    }

    public void setGroupmemberCount(Integer groupmemberCount) {
        this.groupmemberCount = groupmemberCount;
    }

    public Integer getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }

    public Integer getIsAbdgGroup() {
        return isAbdgGroup;
    }

    public void setIsAbdgGroup(Integer isAbdgGroup) {
        this.isAbdgGroup = isAbdgGroup;
    }

    public String getAbdgGroupOwnerUri() {
        return abdgGroupOwnerUri;
    }

    public void setAbdgGroupOwnerUri(String abdgGroupOwnerUri) {
        this.abdgGroupOwnerUri = abdgGroupOwnerUri;
    }

    public String getAbdgGroupOwnerDispName() {
        return abdgGroupOwnerDispName;
    }

    public void setAbdgGroupOwnerDispName(String abdgGroupOwnerDispName) {
        this.abdgGroupOwnerDispName= abdgGroupOwnerDispName;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" OwnerMdn - ").append(KnGDPRTemplate.mdn(ownerMdn))
                .append(", POCGroupId - ").append(pocGroupId)
                .append(", GroupName - ").append(groupName)
                .append(", GroupDisplayName - ").append(groupDisplayName)
                .append(", GroupType - ").append(groupType)
                .append(", Etag - ").append(etag)
                .append(", List Service URI - ").append(listServiceUri)
                .append(", XML dump - ").append(strXml)
                .append(", ResourceURI - ").append(docURI)
                .append(", If Match - ").append(ifMatch)
                .append(", groupmemberCount - ").append(groupmemberCount)
                .append(", avatarId - ").append(avatarId)
                .append(", isAbdgGroup - ").append(isAbdgGroup)
                .append(", abdgGroupOwnerUri - ").append(abdgGroupOwnerUri)
                .append(", abdgGroupOwnerDispName - ").append(abdgGroupOwnerDispName)
                .append(", If None match - ").append(ifNoneMatch);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return ownerMdn;  
    }
}
