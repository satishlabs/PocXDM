/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPDocChgDTO.java
 * Subsystem:   Server Common
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/14/11       7.0
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
 * *******************************************************************************
 */
package com.kodiak.xdms.server.common.dto.clientdat;

import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnEmergencyAttributesDTO;
import com.kodiak.xdms.server.common.dto.common.KnEmergencyInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.dto.common.KnTargetPermsInfoDTO;

import java.util.Collection;

public class KnOPDocChgDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676167L;
    //stores the document sel uri of the notification Object
    private String docUri;
    //stores the document new Etag
    private String newEtag;
    //stores the document previous Etag
    private String prevEtag;
    //stores the entry uri of the notification Object - used only in case of Add Diff
    private String entryUri;

    private int documentChgType;

    private int docType;

    //Added variables for the xcap-doc diff support
    private String groupName;

    private int groupId;

    private Collection<KnSubscriberDTO> addedContactList;

    private Collection<String> removedContactList;

    //will be applicable for the modify external contact
    private Collection<KnSubscriberDTO> modifiedContactMembers;

    private Collection<KnSubscriberDTO> addedGroupMembers;

    private Collection<String> removedGroupMembers;

    private Collection<KnSubscriberDTO> modifiedGrpMembers;

    private int groupMemCount;

    private int groupType;

    private Integer avatar;

    private Collection<KnTargetPermsInfoDTO> modifiedTargetList;

    private Collection<KnTargetPermsInfoDTO> addedTargetList;

    private Collection<String> removedTargetList;

    private Collection<KnEmergencyInfoDTO> addedDestList;

    private Collection<KnEmergencyInfoDTO> modifiedDestList;

    private Collection<String> removedDestList;

    private Collection<KnXDMAddlTalkGroupInfoDTO> addedAddlTGList;

    private Collection<KnXDMAddlTalkGroupInfoDTO> modifiedAddlTGList;

    private Collection<KnXDMAddlTalkGroupInfoDTO> removedAddlTGList;

    private KnEmergencyAttributesDTO addedEmerAttributes;

    private KnEmergencyAttributesDTO modifiedEmerAttributes;

    private KnEmergencyAttributesDTO removedEmerAttributes;

    private boolean osmListChanged;
    
    private boolean isLargeGroup;
    
    private Integer isAbdgGroup;

    private Integer isMcxGroup;
    private Integer externalCorpGroup;
    private Integer isPreConfigGroup;
    private Integer videoPermission;
    private Collection<KnSubscriberDTO> addedEmptyGroupMembers;

    private String groupDisplayName;

    private String recordingFs;

    public Integer getIsMcxGroup() {
		return isMcxGroup;
	}

	public void setIsMcxGroup(Integer isMcxGroup) {
		this.isMcxGroup = isMcxGroup;
	}
    public boolean isLargeGroup() {
		return isLargeGroup;
	}

	public void setLargeGroup(boolean isLargeGroup) {
		this.isLargeGroup = isLargeGroup;
	}

	public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public int getGroupMemCount() {
        return groupMemCount;
    }

    public void setGroupMemCount(int groupMemCount) {
        this.groupMemCount = groupMemCount;
    }

    /**
     * getter method for the DocUri
     *
     * @return String
     */
    public String getDocUri() {
        return docUri;
    }

    /**
     * setter method for the Doc Uri
     *
     * @param docUri String
     */
    public void setDocUri(String docUri) {
        this.docUri = docUri;
    }

    /**
     * getter method for the Etag
     *
     * @return String
     */
    public String getNewEtag() {
        return newEtag;
    }

    /**
     * setter method for the Etag
     *
     * @param newEtag String
     */
    public void setNewEtag(String newEtag) {
        this.newEtag = newEtag;
    }

    public String getPrevEtag() {
        return prevEtag;
    }

    public void setPrevEtag(String prevEtag) {
        this.prevEtag = prevEtag;
    }

    /**
     * getter method for the document change type
     *
     * @return int
     */
    public int getDocumentChgType() {
        return documentChgType;
    }

    /**
     * setter method for the document change type
     *
     * @param documentChgType int
     */
    public void setDocumentChgType(int documentChgType) {
        this.documentChgType = documentChgType;
    }

    public String getEntryUri() {
        return entryUri;
    }

    public void setEntryUri(String entryUri) {
        this.entryUri = entryUri;
    }

    public int getDocType() {
        return docType;
    }

    public void setDocType(int docType) {
        this.docType = docType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Collection<KnSubscriberDTO> getAddedContactList() {
        return addedContactList;
    }

    public void setAddedContactList(Collection<KnSubscriberDTO> addedContactList) {
        this.addedContactList = addedContactList;
    }

    public Collection<String> getRemovedContactList() {
        return removedContactList;
    }

    public void setRemovedContactList(Collection<String> removedContactList) {
        this.removedContactList = removedContactList;
    }

    public Collection<KnSubscriberDTO> getModifiedContactMembers() {
        return modifiedContactMembers;
    }

    public void setModifiedContactMembers(Collection<KnSubscriberDTO> modifiedContactMembers) {
        this.modifiedContactMembers = modifiedContactMembers;
    }

    public Collection<KnSubscriberDTO> getAddedGroupMembers() {
        return addedGroupMembers;
    }

    public void setAddedGroupMembers(Collection<KnSubscriberDTO> addedGroupMembers) {
        this.addedGroupMembers = addedGroupMembers;
    }

    public Collection<String> getRemovedGroupMembers() {
        return removedGroupMembers;
    }

    public void setRemovedGroupMembers(Collection<String> removedGroupMembers) {
        this.removedGroupMembers = removedGroupMembers;
    }

    public Collection<KnSubscriberDTO> getModifiedGrpMembers() {
        return modifiedGrpMembers;
    }

    public void setModifiedGrpMembers(Collection<KnSubscriberDTO> modifiedGrpMembers) {
        this.modifiedGrpMembers = modifiedGrpMembers;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Collection<KnTargetPermsInfoDTO> getModifiedTargetList() {
        return modifiedTargetList;
    }

    public void setModifiedTargetList(Collection<KnTargetPermsInfoDTO> modifiedTargetList) {
        this.modifiedTargetList = modifiedTargetList;
    }

    public Collection<KnTargetPermsInfoDTO> getAddedTargetList() {
        return addedTargetList;
    }

    public void setAddedTargetList(Collection<KnTargetPermsInfoDTO> addedTargetList) {
        this.addedTargetList = addedTargetList;
    }

    public Collection<String> getRemovedTargetList() {
        return removedTargetList;
    }

    public void setRemovedTargetList(Collection<String> removedTargetList) {
        this.removedTargetList = removedTargetList;
    }

    public Collection<KnEmergencyInfoDTO> getAddedDestList() {
        return addedDestList;
    }

    public void setAddedDestList(Collection<KnEmergencyInfoDTO> addedDestList) {
        this.addedDestList = addedDestList;
    }

    public Collection<KnEmergencyInfoDTO> getModifiedDestList() {
        return modifiedDestList;
    }

    public void setModifiedDestList(Collection<KnEmergencyInfoDTO> modifiedDestList) {
        this.modifiedDestList = modifiedDestList;
    }

    public Collection<String> getRemovedDestList() {
        return removedDestList;
    }

    public void setRemovedDestList(Collection<String> removedDestList) {
        this.removedDestList = removedDestList;
    }

    public Collection<KnXDMAddlTalkGroupInfoDTO> getAddedAddlTGList() {
        return addedAddlTGList;
    }

    public void setAddedAddlTGList(Collection<KnXDMAddlTalkGroupInfoDTO> addedAddlTGList) {
        this.addedAddlTGList = addedAddlTGList;
    }

    public Collection<KnXDMAddlTalkGroupInfoDTO> getModifiedAddlTGList() {
        return modifiedAddlTGList;
    }

    public void setModifiedAddlTGList(Collection<KnXDMAddlTalkGroupInfoDTO> modifiedAddlTGList) {
        this.modifiedAddlTGList = modifiedAddlTGList;
    }

    public Collection<KnXDMAddlTalkGroupInfoDTO> getRemovedAddlTGList() {
        return removedAddlTGList;
    }

    public void setRemovedAddlTGList(Collection<KnXDMAddlTalkGroupInfoDTO> removedAddlTGList) {
        this.removedAddlTGList = removedAddlTGList;
    }

    public KnEmergencyAttributesDTO getAddedEmerAttributes() {
        return addedEmerAttributes;
    }

    public void setAddedEmerAttributes(KnEmergencyAttributesDTO addedEmerAttributes) {
        this.addedEmerAttributes = addedEmerAttributes;
    }

    public KnEmergencyAttributesDTO getModifiedEmerAttributes() {
        return modifiedEmerAttributes;
    }

    public void setModifiedEmerAttributes(KnEmergencyAttributesDTO modifiedEmerAttributes) {
        this.modifiedEmerAttributes = modifiedEmerAttributes;
    }

    public KnEmergencyAttributesDTO getRemovedEmerAttributes() {
        return removedEmerAttributes;
    }

    public void setRemovedEmerAttributes(KnEmergencyAttributesDTO removedEmerAttributes) {
        this.removedEmerAttributes = removedEmerAttributes;
    }

    public boolean isOsmListChanged() {
        return osmListChanged;
    }

    public void setOsmListChanged(boolean osmListChanged) {
        this.osmListChanged = osmListChanged;
    }
    
    public Integer getIsAbdgGroup() {
		return isAbdgGroup;
	}

	public void setIsAbdgGroup(Integer isAbdgGroup) {
		this.isAbdgGroup = isAbdgGroup;
	}

    public Collection<KnSubscriberDTO> getAddedEmptyGroupMembers() {
        return addedEmptyGroupMembers;
    }

    public void setAddedEmptyGroupMembers(Collection<KnSubscriberDTO> addedEmptyGroupMembers) {
        this.addedEmptyGroupMembers = addedEmptyGroupMembers;
    }

    public Integer getExternalCorpGroup() {
        return externalCorpGroup;
    }

    public void setExternalCorpGroup(Integer externalCorpGroup) {
        this.externalCorpGroup = externalCorpGroup;
    }

    public Integer getIsPreConfigGroup() {
        return isPreConfigGroup;
    }

    public void setIsPreConfigGroup(Integer isPreConfigGroup) {
        this.isPreConfigGroup = isPreConfigGroup;
    }

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }

    public String getGroupDisplayName() {
        return groupDisplayName;
    }

    public void setGroupDisplayName(String groupDisplayName) {
        this.groupDisplayName = groupDisplayName;
    }

    public String getRecordingFs() {
        return recordingFs;
    }

    public void setRecordingFs(String recordingFs) {
        this.recordingFs = recordingFs;
    }

    public String getObjectId() {
        return docUri;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(" DOC_SEL_URI - ").append(KnGDPRTemplate.mdnUriTemplate(docUri))
                .append(", DOC_ETAG - ").append(newEtag)
                .append(", DOC_PREVIUOS_ETAG - ").append(prevEtag)
                .append(", DOC_ENTRY_URI - ").append(entryUri)
                .append(", Doc_Chg_type - ").append(documentChgType)
                .append(", Doc_type - ").append(docType)
                .append(", groupName - ").append(groupName)
                .append(", avatar - ").append(avatar)
                .append(", groupId - ").append(groupId)
                .append(", addedContactList - ").append(addedContactList)
                .append(", removedContactList - ").append(removedContactList)
                .append(", addedGroupMembers - ").append(addedGroupMembers)
                .append(", modifiedContactList - ").append(modifiedContactMembers)
                .append(", removedGroupMembers - ").append(removedGroupMembers)
                .append(", groupMemCount - ").append(groupMemCount)
                .append(", modifiedGrpMembers - ").append(modifiedGrpMembers)
                .append(", modifiedTargetMdp - ").append(modifiedTargetList)
                .append(", addedTargetMdp - ").append(addedTargetList)
                .append(", removedTargetList - ").append(removedTargetList)
                .append(", addedDestList - ").append(addedDestList)
                .append(", modifiedDestList - ").append(modifiedDestList)
                .append(", removedDestList - ").append(removedDestList)
                .append(", addedAddlTGList - ").append(addedAddlTGList)
                .append(", modifiedAddlTGList - ").append(modifiedAddlTGList)
                .append(", removedAddlTGList - ").append(removedAddlTGList)
                .append(", addedEmerAttributes - ").append(addedEmerAttributes)
                .append(", modifiedEmerAttributes - ").append(modifiedEmerAttributes)
                .append(", removedEmerAttributes - ").append(removedEmerAttributes)
                .append(", osmListChanged - ").append(osmListChanged)
                .append(", isLargeGroup - ").append(isLargeGroup)
                .append(", isAbdgGroup - ").append(isAbdgGroup)
                .append(", addedEmptyGroupMembers - ").append(addedEmptyGroupMembers)
                .append(", isMcxGroup - ").append(isMcxGroup)
                .append(", externalCorpGroup - ").append(externalCorpGroup)
                .append(", groupDisplayName - ").append(groupDisplayName)
                .append(", recordingFs - ").append(recordingFs)
                .append(", isPreConfigGroup - ").append(isPreConfigGroup)
                .append(", videoPermission - ").append(videoPermission);
        return sb.toString();
    }

}
