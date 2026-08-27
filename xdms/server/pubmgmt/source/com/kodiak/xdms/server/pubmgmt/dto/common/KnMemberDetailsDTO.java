/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnMemberDetailsDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 28, 2011           7.0
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
public class KnMemberDetailsDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676215L;

    private String uri;
    private String displayName;
    private String ufmi;

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(50);
        strBuffer.append(super.toString());
        strBuffer.append(", URI - ").append(uri);
        strBuffer.append(", DisplayName - ").append(KnGDPRTemplate.name(displayName));

        return strBuffer.toString();
    }


    public String getObjectId() {
        return uri;
    }
}
