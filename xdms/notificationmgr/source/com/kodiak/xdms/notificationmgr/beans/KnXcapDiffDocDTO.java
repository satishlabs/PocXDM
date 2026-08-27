/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXcapDiffDocDTO.java
 * Subsystem:  PoC XDMS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Harsha             06-Jan-2011       7.0
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
package com.kodiak.xdms.notificationmgr.beans;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnEmergencyAttributesDTO;
import com.kodiak.xdms.server.common.dto.common.KnEmergencyInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.dto.common.KnTargetPermsInfoDTO;

import java.io.Serializable;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KnXcapDiffDocDTO implements Comparable<KnXcapDiffDocDTO>, IIdentifier {
    /**
     * holds the document URI to identify
     * 1. folder in case of new document (group) getting added to directory
     * 2. replace/remove case, the actual document URI
     */
    private static final long serialVersionUID = -3760803821796915055L;
    private String documentSelector;
    /**
     * holds the actual document that was added, used as part of add document directory notiffication
     */
    private String docUri;
    /**
     * document etag - updated etag or new etag
     */
    private String docEtag;
    /**
     * identifies the type of change in the directory
     * 1-ADD, 2-REPLACE, 3-REMOVE/DELETE
     */
    private int docChangeType;
    /**
     * this will have the value "after" as per client ICD
     */
    private String removeWs;
    /**
     * Added parameters to support the dodument diff changes which specfies the members
     * added and removed from a document.Pramater for the notificationCapability etags and groupame changes
     */

    private boolean isPushNotifyEnabled; 
    
    private Collection<KnSubscriberDTO> addedContactList;

    private Collection<String> removedContactList;

    private Collection<KnSubscriberDTO> modifiedContactList;

    private Collection<KnSubscriberDTO> addedGroupMembers;

    private Collection<String> removedGroupMembers;

    private Collection<KnSubscriberDTO> modifiedGrpMembers;

    private Collection<KnEmergencyInfoDTO> addedDestList;

    private Collection<KnEmergencyInfoDTO> modifiedDestList;

    private Collection<String> removedDestList;

    private String prevDocEtag;

    private int groupId;

    private String groupName;

    private int groupMemCount;

    private Integer avatar;

    private Collection<KnTargetPermsInfoDTO> addedTargetList;

    private Collection<KnTargetPermsInfoDTO> modifiedTargetList;

    private Collection<String> removedTargetList;

    private Collection<KnXDMAddlTalkGroupInfoDTO> addedAddlTGList;

    private Collection<KnXDMAddlTalkGroupInfoDTO> modifyAddlTGList;

    private Collection<KnXDMAddlTalkGroupInfoDTO> removedAddlTGList;

    private KnEmergencyAttributesDTO addedEmergAttributes;

    private KnEmergencyAttributesDTO modifiedEmergAttributes;

    private KnEmergencyAttributesDTO removedEmergAttributes;

    private boolean osmListChanged;

    private Integer isAbdgGroup;

    private Integer isMcxGroup;
    private Integer externalCorpGroup;
    private Integer isPreConfigGroup;
    private Integer videoPermission;

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }
    public Integer getIsMcxGroup() {
		return isMcxGroup;
	}

	public void setIsMcxGroup(Integer isMcxGroup) {
		this.isMcxGroup = isMcxGroup;
	}

	public int getGroupMemCount() {
        return groupMemCount;
    }

    public void setGroupMemCount(int groupMemCount) {
        this.groupMemCount = groupMemCount;
    }

    public String getDocumentSelector() {
        return documentSelector;
    }

    public void setDocumentSelector(String documentSelector) {
        this.documentSelector = documentSelector;
    }

    public String getDocUri() {
        return docUri;
    }

    public void setDocUri(String docUri) {
        this.docUri = docUri;
    }

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    public int getDocChangeType() {
        return docChangeType;
    }

    public void setDocChangeType(int docChangeType) {
        this.docChangeType = docChangeType;
    }

    public String getRemoveWs() {
        return removeWs;
    }

    public void setRemoveWs(String removeWs) {
        this.removeWs = removeWs;
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

    public Collection<KnSubscriberDTO> getModifiedContactList() {
        return modifiedContactList;
    }

    public void setModifiedContactList(Collection<KnSubscriberDTO> modifiedContactList) {
        this.modifiedContactList = modifiedContactList;
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

    public String getPrevDocEtag() {
        return prevDocEtag;
    }

    public void setPrevDocEtag(String prevDocEtag) {
        this.prevDocEtag = prevDocEtag;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Collection<KnSubscriberDTO> getModifiedGrpMembers() {
        return modifiedGrpMembers;
    }

    public void setModifiedGrpMembers(Collection<KnSubscriberDTO> modifiedGrpMembers) {
        this.modifiedGrpMembers = modifiedGrpMembers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Collection<KnTargetPermsInfoDTO> getAddedTargetList() {
        return addedTargetList;
    }

    public void setAddedTargetList(Collection<KnTargetPermsInfoDTO> addedTargetList) {
        this.addedTargetList = addedTargetList;
    }

    public Collection<KnTargetPermsInfoDTO> getModifiedTargetList() {
        return modifiedTargetList;
    }

    public void setModifiedTargetList(Collection<KnTargetPermsInfoDTO> modifiedTargetList) {
        this.modifiedTargetList = modifiedTargetList;
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

    public Collection<KnXDMAddlTalkGroupInfoDTO> getModifyAddlTGList() {
        return modifyAddlTGList;
    }

    public void setModifyAddlTGList(Collection<KnXDMAddlTalkGroupInfoDTO> modifyAddlTGList) {
        this.modifyAddlTGList = modifyAddlTGList;
    }

    public Collection<KnXDMAddlTalkGroupInfoDTO> getRemovedAddlTGList() {
        return removedAddlTGList;
    }

    public void setRemovedAddlTGList(Collection<KnXDMAddlTalkGroupInfoDTO> removedAddlTGList) {
        this.removedAddlTGList = removedAddlTGList;
    }

    public KnEmergencyAttributesDTO getAddedEmergAttributes() {
        return addedEmergAttributes;
    }

    public void setAddedEmergAttributes(KnEmergencyAttributesDTO addedEmergAttributes) {
        this.addedEmergAttributes = addedEmergAttributes;
    }

    public KnEmergencyAttributesDTO getModifiedEmergAttributes() {
        return modifiedEmergAttributes;
    }

    public void setModifiedEmergAttributes(KnEmergencyAttributesDTO modifiedEmergAttributes) {
        this.modifiedEmergAttributes = modifiedEmergAttributes;
    }

    public KnEmergencyAttributesDTO getRemovedEmergAttributes() {
        return removedEmergAttributes;
    }

    public void setRemovedEmergAttributes(KnEmergencyAttributesDTO removedEmergAttributes) {
        this.removedEmergAttributes = removedEmergAttributes;
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

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KnXcapDiffDocDTO that = (KnXcapDiffDocDTO) o;
        if (documentSelector != null ? !documentSelector.equals(that.documentSelector) : that.documentSelector != null) {
            return false;
        } else if (((Long.parseLong(that.getDocEtag())) != (Long.parseLong(docEtag)))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return documentSelector != null ? documentSelector.hashCode() : 0;
    }

    public int compareTo(KnXcapDiffDocDTO o) {
        String compEtag = o.getDocEtag();
        if(compEtag != null && docEtag != null) {
            if (compEtag.compareTo(docEtag) > 0) {
                return 1;
            } else if (compEtag.compareTo(docEtag) == 0) {
                return 0;
            } else {
                return -1;
            }
        }
        return -1;
    }

    public boolean isPushNotifyEnabled() {
		return isPushNotifyEnabled;
	}

	public void setPushNotifyEnabled(boolean isPushNotifyEnabled) {
		this.isPushNotifyEnabled = isPushNotifyEnabled;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" KnXcapDiffDocDTO --> [")
                .append(" documentSelector - ").append(KnGDPRTemplate.mdnUriTemplate(documentSelector))
                .append(", docUri - ").append(KnGDPRTemplate.mdnUriTemplate(docUri))
                .append(", docEtag - ").append(docEtag)
                .append(", docChangeType - ").append(docChangeType)
                .append(", removeWs - ").append(removeWs)
                .append(", addedContactList - ").append(addedContactList)
                .append(", removedContactList - ").append(KnGDPRTemplate.mdnList(removedContactList))
                .append(", modifiedContactMembers - ").append(modifiedContactList)
                .append(", addedGroupMembers - ").append(addedGroupMembers)
                .append(", removedGroupMembers - ").append(KnGDPRTemplate.mdnList(removedGroupMembers))
                .append(", modifiedGrpMembers - ").append(modifiedGrpMembers)
                .append(", groupName - ").append(groupName)
                .append(", groupId - ").append(groupId)
                .append(", avatar - ").append(avatar)
                .append(", addedTargetList - ").append(addedTargetList)
                .append(", modifiedTargetList - ").append(modifiedTargetList)
                .append(", removedTargetList - ").append(KnGDPRTemplate.mdnList(removedTargetList))
                .append(", addedDestList - ").append(addedDestList)
                .append(", modifiedDestList - ").append(modifiedDestList)
                .append(", removedDestList - ").append(removedDestList)
                .append(", addedAddlTGList - ").append(addedAddlTGList)
                .append(", modifyAddlTGList - ").append(modifyAddlTGList)
                .append(", removedAddlTGList - ").append(removedAddlTGList)
                .append(", addedEmergAttributes - ").append(addedEmergAttributes)
                .append(", modifiedEmergAttributes - ").append(modifiedEmergAttributes)
                .append(", removedEmergAttributes - ").append(removedEmergAttributes)
                .append(", osmListChanged - ").append(osmListChanged)
                .append(", isAbdgGroup - ").append(isAbdgGroup)
                .append(", isMcxGroup - ").append(isMcxGroup)
                .append(", externalCorpGroup - ").append(externalCorpGroup)
                .append(", isPreConfigGroup - ").append(isPreConfigGroup)
                .append(", videoPermission - ").append(videoPermission)
                .append("]");

        return sb.toString();
    }

    @Override
    @JsonIgnore
    public String getObjectId() {
        return null;
    }

}
