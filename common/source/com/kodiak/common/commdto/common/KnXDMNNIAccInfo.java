/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMNNIAccInfo.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Deepak kumar        23-12-2014      8.0
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
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;


public class KnXDMNNIAccInfo implements IIdentifier {

    private static final long serialVersionUID = 4462240168142980582L;
    private String objectId;

    private String nniRefId;
    private String accId;

    public String getNniRefId() {
        return nniRefId;
    }

    public void setNniRefId(String nniRefId) {
        this.nniRefId = nniRefId;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }
}
