/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMSubsAliasInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Venkata Sudhakar     May 6, 2021      11.3
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, MFar Greenheart Phase IV
 * Manyata Tech Park, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
public class KnXDMSubsCameraInfo implements Serializable {


    private static final long serialVersionUID = 4770547127660685875L;

    private String ipIdentifier;

    private String cameraSerialId;

    public KnXDMSubsCameraInfo(String ipIdentifier, String cameraSerialId) {
        this.ipIdentifier = ipIdentifier;
        this.cameraSerialId = cameraSerialId;
    }

    public KnXDMSubsCameraInfo() { };

    public String getIpIdentifier() {
        return ipIdentifier;
    }

    public void setIpIdentifier(String ipIdentifier) {
        this.ipIdentifier = ipIdentifier;
    }

    public String getCameraSerialId() {
        return cameraSerialId;
    }

    public void setCameraSerialId(String cameraSerialId) {
        this.cameraSerialId = cameraSerialId;
    }

    @Override
    public String toString() {
        return "KnXDMSubsCameraInfo{" +
                "ipIdentifier='" + ipIdentifier + '\'' +
                ", cameraSerialId='" + cameraSerialId + '\'' +
                '}';
    }
}
