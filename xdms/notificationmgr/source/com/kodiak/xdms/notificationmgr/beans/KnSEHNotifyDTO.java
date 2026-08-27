/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     28/5/14         7.7.0
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
package com.kodiak.xdms.notificationmgr.beans;

public class KnSEHNotifyDTO extends KnCommonNotifyDTO {
    //boolean flag to identify whether to send TGS Mode change SEH notification or not.
    private boolean tgsModeNotify;
    //This variable is to hold the tgs mode value of the subscriber used while sending tgs mode change SEH notification
    private Integer tgsMode;
    //to hold TGS Mode change details
    private KnTGSModeNotifyDTO tgsModeNotifyDTO;

    public KnTGSModeNotifyDTO getTgsModeNotifyDTO() {
        return tgsModeNotifyDTO;
    }

    public void setTgsModeNotifyDTO(KnTGSModeNotifyDTO tgsModeNotifyDTO) {
        this.tgsModeNotifyDTO = tgsModeNotifyDTO;
    }

    public Integer getTgsMode() {
        return tgsMode;
    }

    public void setTgsMode(Integer tgsMode) {
        this.tgsMode = tgsMode;
    }

    public boolean isTgsModeNotify() {
        return tgsModeNotify;
    }

    public void setTgsModeNotify(boolean tgsModeNotify) {
        this.tgsModeNotify = tgsModeNotify;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" [KnSEHNotifyDTO - ")
                .append(", TGS_Notify_DTO - ").append(tgsModeNotifyDTO)
                .append(", tgsModeNotify - ").append(tgsModeNotify)
                .append(", tgsMode - ").append(tgsMode)
                .append("]");
        return strBuffer.toString();
    }
}
