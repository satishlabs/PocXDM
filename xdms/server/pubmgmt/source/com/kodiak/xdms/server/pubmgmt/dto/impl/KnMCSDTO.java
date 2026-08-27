/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.impl;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.pubmgmt.dto.intf.IGroupIdentifier;
import com.kodiak.xdms.server.pubmgmt.dto.intf.IOwnerDTO;

import java.io.InputStream;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnMCSDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Puneet Singhania       June 27,2019       9.1.1
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

public class KnMCSDTO implements IOwnerDTO, IIdentifier {

    private static final long serialVersionUID = 7526471155622676220L;

    private String objectId;
    private String owner;
    private int ifMatch;
    private int ifNoneMatch;


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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


    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", ObjectId - ").append(objectId);
        strBuffer.append(", OwnerMdn - ").append(KnGDPRTemplate.mdn(owner));
        strBuffer.append(", IfMatch - ").append(ifMatch);
        strBuffer.append(", IfNoneMatch - ").append(ifNoneMatch);

        return strBuffer.toString();
    }

}