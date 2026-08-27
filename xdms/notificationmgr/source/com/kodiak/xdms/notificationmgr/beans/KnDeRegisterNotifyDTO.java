/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnDeRegisterNotifyDTO.java
 * Subsystem:   XCAP Notification Mgr
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           20/03/12        7.2
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
package com.kodiak.xdms.notificationmgr.beans;

public class KnDeRegisterNotifyDTO extends KnCommonNotifyDTO {

    private static final long serialVersionUID = 7526471155622676248L;
    //stores the newMdn
    private String newMdn;

    public String getNewMdn() {
        return newMdn;
    }

    public void setNewMdn(String newMdn) {
        this.newMdn = newMdn;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());

        return strBuffer.toString();
    }
}