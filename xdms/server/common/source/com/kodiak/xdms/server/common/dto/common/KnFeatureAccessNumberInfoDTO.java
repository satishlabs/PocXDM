/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * *****************************************************************************
 * <p/>
 * Subsystem:   POC
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit kumar           Aug 06, 2012       7.2
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
public class KnFeatureAccessNumberInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676191L;


    private String pttServerId;
    private String accessNumber;
    private int accessNumberIndex;
    private int featureAccessIndex;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getAccessNumber() {
        return accessNumber;
    }

    public void setAccessNumber(String accessNumber) {
        this.accessNumber = accessNumber;
    }

    public int getAccessNumberIndex() {
        return accessNumberIndex;
    }

    public void setAccessNumberIndex(int accessNumberIndex) {
        this.accessNumberIndex = accessNumberIndex;
    }

    public int getFeatureAccessIndex() {
        return featureAccessIndex;
    }

    public void setFeatureAccessIndex(int featureAccessIndex) {
        this.featureAccessIndex = featureAccessIndex;
    }



    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", accessNumber - ").append(accessNumber);
        strBuffer.append(", accessNumberIndex - ").append(accessNumberIndex);
        strBuffer.append(", featureAccessIndex - ").append(featureAccessIndex);
        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

