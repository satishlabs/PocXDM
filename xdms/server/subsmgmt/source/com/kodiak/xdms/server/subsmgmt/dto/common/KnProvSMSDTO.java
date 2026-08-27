/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name: KnProvSMSDTO.java
 * Subsystem:  welcome SMS
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * 			             17-Aug-2012       7.4
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

package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnProvSMSDTO {

    private String mdn;
    private int recipientType;
    private int mesgValidity;
    private int msgNotificationId;
    private short dataEncodingScheme;


    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    public int getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(int recipientType) {
        this.recipientType = recipientType;
    }

    public int getMesgValidity() {
        return mesgValidity;
    }

    public void setMesgValidity(int mesgValidity) {
        this.mesgValidity = mesgValidity;
    }

    public int getMsgNotificationId() {
        return msgNotificationId;
    }

    public void setMsgNotificationId(int msgNotificationId) {
        this.msgNotificationId = msgNotificationId;
    }

    public short getDataEncodingScheme() {
        return dataEncodingScheme;
    }

    public void setDataEncodingScheme(short dataEncodingScheme) {
        this.dataEncodingScheme = dataEncodingScheme;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("KnProvSMSDTO - [");
        stringBuilder.append(" MDN: - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", RECIEPIENT_TYPE - ").append(recipientType)
                .append(", MSG_VALIDITY - ").append(mesgValidity)
                .append(", MSG_NOTIFICATION_ID - ").append(msgNotificationId)
                .append(", DATA_ENCODING_SCHEME - ").append(dataEncodingScheme)
                .append("]");

        return stringBuilder.toString();
    }
} 
