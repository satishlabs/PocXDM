/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPDirChgDTO.java
 * Subsystem:   Server common doc
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

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

public class KnOPDirChgDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676166L;
    //stores the XCAP Root URI
    private String xcapRootURI;
    //stores the Directory URI
    private String dirUri;
    //stores the Directory Previous Etag
    private String dirPrevEtag;
    //stores the Directory new Etag
    private String dirNewEtag;
    //stores the Document Change DTO
    private Collection<KnOPDocChgDTO> docChgDTO;

    private String protoVersion;
    //stores the PoC Home
    private String pocHome;
    //stores the presence Home
    private String presenceHome;

    //added to indicate that the client is capable or not to handle the new notifications
    private boolean notfnCapability;
    
    private boolean isPushNotifyEnabled;
    // ClientType added for microServices Notification:
    private Integer clientType;

    private boolean isPrivacyReasonRequired;

    private boolean profileChanged;
    
    private boolean isLargeGroup;
    //owner mdn is  0 - Base Mdn,1 - Profile Mdn
    private Integer ntfyOnAnyMDN;

    private String mdn;

    public boolean isLargeGroup() {
		return isLargeGroup;
	}

	public void setLargeGroup(boolean isLargeGroup) {
		this.isLargeGroup = isLargeGroup;
	}

	/**
     * getter method for the Directory Uri
     *
     * @return String
     */
    public String getDirUri() {
        return dirUri;
    }

    /**
     * setter method for the Directory Uri
     *
     * @param dirUri String
     */
    public void setDirUri(String dirUri) {
        this.dirUri = dirUri;
    }

    /**
     * getter method for the Directory Previous Etag
     *
     * @return String
     */
    public String getDirPrevEtag() {
        return dirPrevEtag;
    }

    /**
     * setter method for the Directory Previous Etag
     *
     * @param dirPrevEtag String
     */
    public void setDirPrevEtag(String dirPrevEtag) {
        this.dirPrevEtag = dirPrevEtag;
    }

    /**
     * getter method for the Directory New Etag
     *
     * @return String
     */
    public String getDirNewEtag() {
        return dirNewEtag;
    }

    /**
     * setter method for the Directory New Etag
     *
     * @param dirNewEtag String
     */
    public void setDirNewEtag(String dirNewEtag) {
        this.dirNewEtag = dirNewEtag;
    }

    /**
     * getter method for the Document change DTO
     *
     * @return KnOPDocChgDTO
     */
    public Collection<KnOPDocChgDTO> getDocChgDTO() {
        return docChgDTO;
    }

    /**
     * setter method for the Document change DTO
     *
     * @param docChgDTO KnOPDocChgDTO
     */
    public void setDocChgDTO(Collection<KnOPDocChgDTO> docChgDTO) {
        this.docChgDTO = docChgDTO;
    }

    /**
     * getter method for the XCAP Root URI
     *
     * @return String
     */
    public String getXcapRootURI() {
        return xcapRootURI;
    }

    /**
     * setter method for the XCAP Root URI
     *
     * @param xcapRootURI String
     */
    public void setXcapRootURI(String xcapRootURI) {
        this.xcapRootURI = xcapRootURI;
    }

    public String getProtoVersion() {
        return protoVersion;
    }

    public void setProtoVersion(String protoVersion) {
        this.protoVersion = protoVersion;
    }

    public String getPocHome() {
            return pocHome;
        }

        public void setPocHome(String pocHome) {
            this.pocHome = pocHome;
        }

        public String getPresenceHome() {
            return presenceHome;
        }

        public void setPresenceHome(String presenceHome) {
            this.presenceHome = presenceHome;
        }

    public boolean isNotfnCapability() {
        return notfnCapability;
    }

    public void setNotfnCapability(boolean notfnCapability) {
        this.notfnCapability = notfnCapability;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public boolean isPrivacyReasonRequired() {
        return isPrivacyReasonRequired;
    }

    public void setPrivacyReasonRequired(boolean privacyReasonRequired) {
        isPrivacyReasonRequired = privacyReasonRequired;
    }

    public boolean isProfileChanged() {
        return profileChanged;
    }

    public void setProfileChanged(boolean profileChanged) {
        this.profileChanged = profileChanged;
    }

    public Integer getNtfyOnAnyMDN() {
        return ntfyOnAnyMDN;
    }

    public void setNtfyOnAnyMDN(Integer ntfyOnAnyMDN) {
        this.ntfyOnAnyMDN = ntfyOnAnyMDN;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("[KnOPDirChgDTO -> ");
        sb.append("XCAP_ROOT_URI - ").append(xcapRootURI)
                .append(", Directory_Uri - ").append(KnGDPRTemplate.mdnUriTemplate(dirUri))
                .append(", Directory_Previous_Etag - ").append(dirPrevEtag)
                .append(", Directory_New_Etag - ").append(dirNewEtag)
                .append(", Document_chg_DTO - ").append(docChgDTO)
                .append(", notfnCapability - ").append(notfnCapability)
                .append(", protoVersion - ").append(protoVersion)
                .append(", clientType - ").append(clientType)
                .append(", isPrivacyReasonRequired - ").append(isPrivacyReasonRequired)
                .append(", profileChanged - ").append(profileChanged)
                .append(", isLargeGroup - ").append(isLargeGroup)
                .append(", ntfyOnAnyMDN - ").append(ntfyOnAnyMDN)
                .append(", pocHome - ").append(pocHome)
                .append(", mdn - ").append(KnGDPRTemplate.mdn(KnGDPRTemplate.mdn(mdn)))
                .append(super.toString()).append("]");

        return sb.toString();
    }

    public boolean isPushNotifyEnabled() {
		return isPushNotifyEnabled;
	}

	public void setPushNotifyEnabled(boolean isPushNotifyEnabled) {
		this.isPushNotifyEnabled = isPushNotifyEnabled;
	}

	public String getObjectId() {
        return this.dirUri;
    }
}