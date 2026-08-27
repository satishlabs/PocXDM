/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPDocumentDTO.java
 * Subsystem:   Subscriber Management Lib
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/29/10       7.0
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

public class KnOPDocumentDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676168L;
    //stores the xcap diff of the notification
    private String xcapDiff;
    //stores the sel value of the document
    private String xui;
    //stores the Previous etag of the document
    private String previous_etag;
    //stores the new etag of the document
    private String new_etag;

    /**
     * getter method for the XCAP diff
     *
     * @return String
     */
    public String getXcapDiff() {
        return xcapDiff;
    }

    /**
     * setter method for the XCAP Diff
     *
     * @param xcapDiff String
     */
    public void setXcapDiff(String xcapDiff) {
        this.xcapDiff = xcapDiff;
    }

    /**
     * getter method for the XUI or Sel of the document
     *
     * @return String
     */
    public String getXui() {
        return xui;
    }

    /**
     * setter method for the XUI or sel of the document
     *
     * @param xui String
     */
    public void setXui(String xui) {
        this.xui = xui;
    }

    /**
     * getter method for the Previous E-tag
     *
     * @return String
     */
    public String getPreviousEtag() {
        return previous_etag;
    }

    /**
     * setter method for the Previous E-tag
     *
     * @param previous_etag String
     */
    public void setPreviousEtag(String previous_etag) {
        this.previous_etag = previous_etag;
    }

    /**
     * getter method for the New E-Tag
     *
     * @return String
     */
    public String getNewEtag() {
        return new_etag;
    }

    /**
     * setter method for the new E-Tag
     *
     * @param new_etag String
     */
    public void setNew_etag(String new_etag) {
        this.new_etag = new_etag;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("XCAP_DIFF - ").append(xcapDiff)
                .append(", XUI - ").append(xui)
                .append(", Previous_eTag - ").append(previous_etag)
                .append(", New_Etag - ").append(new_etag);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return xui;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
