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
 * Ajit kumar           Jab 15, 2011       7.0
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
public class KnXDMSDocSubPrxConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676181L;

    private String pttServerId;
    private String primaryDocSubPrxURI;
    private String geoDocSubPrxURI;
    private int docSubscriptionValidity;
    private int maxDocNtfyMsgBodySize;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getPrimaryDocSubPrxURI() {
        return primaryDocSubPrxURI;
    }

    public void setPrimaryDocSubPrxURI(String primaryDocSubPrxURI) {
        this.primaryDocSubPrxURI = primaryDocSubPrxURI;
    }

    public String getGeoDocSubPrxURI() {
        return geoDocSubPrxURI;
    }

    public void setGeoDocSubPrxURI(String geoDocSubPrxURI) {
        this.geoDocSubPrxURI = geoDocSubPrxURI;
    }

    public int getDocSubscriptionValidity() {
        return docSubscriptionValidity;
    }

    public void setDocSubscriptionValidity(int docSubscriptionValidity) {
        this.docSubscriptionValidity = docSubscriptionValidity;
    }

    public int getMaxDocNtfyMsgBodySize() {
        return maxDocNtfyMsgBodySize;
    }

    public void setMaxDocNtfyMsgBodySize(int maxDocNtfyMsgBodySize) {
        this.maxDocNtfyMsgBodySize = maxDocNtfyMsgBodySize;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", primaryDocSubPrxURI - ").append(primaryDocSubPrxURI);
        strBuffer.append(", geoDocSubPrxURI - ").append(geoDocSubPrxURI);
        strBuffer.append(", docSubscriptionValidity - ").append(docSubscriptionValidity);
        strBuffer.append(", maxDocNtfyMsgBodySize - ").append(maxDocNtfyMsgBodySize);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
