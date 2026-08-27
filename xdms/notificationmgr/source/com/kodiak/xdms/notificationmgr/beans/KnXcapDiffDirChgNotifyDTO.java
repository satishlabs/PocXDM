/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnXcapDiffDirChgNotifyDTO.java
 * Subsystem:   XCAP notification mgr
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       3/8/11         7.0.2
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
 * *************************************************************************
 */
package com.kodiak.xdms.notificationmgr.beans;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

public class KnXcapDiffDirChgNotifyDTO extends KnCommonNotifyDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676250L;
    //stores the xcap root uri
    private String xcapRootUri;

    /**
     * directory URI
     */
    private String dirURI;
    /**
     * directory previous etag
     */
    private String dirPrevEtag;
    /**
     * directory new etag
     */
    private String dirNewEtag;
    /**
     * the doc change details
     */
    private Collection<KnXcapDiffDocDTO> docDiffObj;
    /**
     * The thresold for initiating truncation of big notifications
     */
    private int docLenThresold = -1;

    /**
     * Notification paramter added to check if the subscriber is capable of handing new document
     * diff notifications
     */
    private boolean notfnCapability;
    
    
    private boolean isPushNotifyEnabled;

    //The protocol version i.e subscriber major version PV
    private String protocolVersion;

    //0 - Notify for Base+Profile Mdn,1 - Notify Only for Profile Mdn
    private Integer ntfyOnAnyMDN;

    /**
     * Default Constructor
     */
    public KnXcapDiffDirChgNotifyDTO() {
    }

    /**
     * Parameterized Constructor
     *
     * @param docLenThresold
     */
    public KnXcapDiffDirChgNotifyDTO(int docLenThresold) {
        this.docLenThresold = docLenThresold;
    }

    public String getObjectId() {
        return dirURI;
    }

    /**
     * getter method for the XCAP Root URI
     *
     * @return String
     */
    public String getXcapRootUri() {
        return xcapRootUri;
    }

    /**
     * setter method for the XCAP Root URI
     *
     * @param xcapRootUri String
     */
    public void setXcapRootUri(String xcapRootUri) {
        if (xcapRootUri != null) {
            xcapRootUri = xcapRootUri.trim();
            if (xcapRootUri.equals("")) {
                xcapRootUri = null;
            }
        }
        this.xcapRootUri = xcapRootUri;
    }

    /**
     * getter method for the Directory URI
     *
     * @return String
     */
    public String getDirURI() {
        return dirURI;
    }

    /**
     * setter method fo the Directory URI
     *
     * @param dirURI String
     */
    public void setDirURI(String dirURI) {
        if (dirURI != null) {
            dirURI = dirURI.trim();
            if (dirURI.equals("")) {
                dirURI = null;
            }
        }
        this.dirURI = dirURI;
    }

    /**
     * getter method fo the Directory Previous Etag
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
        if (dirPrevEtag != null) {
            dirPrevEtag = dirPrevEtag.trim();
            if (dirPrevEtag.equals("")) {
                dirPrevEtag = null;
            }
        }
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
     * setter method for the Directory Dir New Etag
     *
     * @param dirNewEtag String
     */
    public void setDirNewEtag(String dirNewEtag) {
        if (dirNewEtag != null) {
            dirNewEtag = dirNewEtag.trim();
            if (dirNewEtag.equals("")) {
                dirNewEtag = null;
            }
        }
        this.dirNewEtag = dirNewEtag;
    }

    /**
     * getter method for the Collection of Doc Diff Objects
     *
     * @return Collection<KnXcapDiffDocDTO>
     */
    public Collection<KnXcapDiffDocDTO> getDocDiffObj() {
        return docDiffObj;
    }

    /**
     * setter method for the Collection of Dod Diff Objects
     *
     * @param docDiffObj Collection<KnXcapDiffDocDTO>
     */
    public void setDocDiffObj(Collection<KnXcapDiffDocDTO> docDiffObj) {
        truncateDocs(docDiffObj);
    }

    public int getDocLenThresold() {
        return docLenThresold;
    }

    public boolean isNotfnCapability() {
        return notfnCapability;
    }

    public void setNotfnCapability(boolean notfnCapability) {
        this.notfnCapability = notfnCapability;
    }

    public void setDocLenThresold(int docLenThresold) {
        this.docLenThresold = docLenThresold;
    }

    private void truncateDocs(Collection<KnXcapDiffDocDTO> docDiffObj) {
        if (docDiffObj != null) {
            if (docLenThresold <= -1 || docDiffObj.size() <= docLenThresold) {
                this.docDiffObj = docDiffObj;
            }
        }
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean isPushNotifyEnabled() {
		return isPushNotifyEnabled;
	}

	public void setPushNotifyEnabled(boolean isPushNotifyEnabled) {
		this.isPushNotifyEnabled = isPushNotifyEnabled;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    public Integer getNtfyOnAnyMDN() {
        return ntfyOnAnyMDN;
    }

    public void setNtfyOnAnyMDN(Integer ntfyOnAnyMDN) {
        this.ntfyOnAnyMDN = ntfyOnAnyMDN;
    }

	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" KnXcapDiffDirChgNotifyDTO --> [")
                .append(" XCAP_ROOT_URI - ").append(xcapRootUri)
                .append(", Directory_URI - ").append(KnGDPRTemplate.mdnUriTemplate(dirURI))
                .append(", Directory_Prev_Etag - ").append(dirPrevEtag)
                .append(", Directory_New_Etag - ").append(dirNewEtag)
                .append(", Doc_Diff_Object - ").append(docDiffObj)
                .append(", docLenThresold - ").append(docLenThresold)
                .append(", notfnCapability - ").append(notfnCapability)
                .append(", protocolVersion - ").append(protocolVersion)
                .append(", ntfyOnAnyMDN - ").append(ntfyOnAnyMDN)
                .append(super.toString())
                .append("]");

        return sb.toString();
    }
}
