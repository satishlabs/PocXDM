/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnAppInfoDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 15, 2011           7.0
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
public class KnAppInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676147L;

    private String auid;
    private Collection<KnAppDetailsDTO> appDetailsList;


    public String getAuid() {
        return auid;
    }

    public void setAuid(String auid) {
        this.auid = auid;
    }

    public Collection<KnAppDetailsDTO> getAppDetailsList() {
        return appDetailsList;
    }

    public void setAppDetailsList(Collection<KnAppDetailsDTO> appDetailsList) {
        this.appDetailsList = appDetailsList;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(50);
        strBuffer.append(super.toString());
        strBuffer.append(" Auid - ").append(auid)
                .append(", AppDetailsList - ").append(appDetailsList);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return auid;
    }
}
