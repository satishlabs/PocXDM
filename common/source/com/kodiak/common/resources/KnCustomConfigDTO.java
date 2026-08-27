/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnCustomConfigDTO
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/29/11       7.0
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
package com.kodiak.common.resources;

public class KnCustomConfigDTO {

    private boolean systemCustomFlag;
    private boolean installCustomFlag;

    public boolean isSystemCustomFlag() {
        return systemCustomFlag;
    }

    public void setSystemCustomFlag(boolean systemCustomFlag) {
        this.systemCustomFlag = systemCustomFlag;
    }

    public boolean isInstallCustomFlag() {
        return installCustomFlag;
    }

    public void setInstallCustomFlag(boolean installCustomFlag) {
        this.installCustomFlag = installCustomFlag;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("SYSTEM_CUSTOM_FLAG - ").append(systemCustomFlag);
        strBuffer.append(", INSTALL_CUSTOM_FLAG - ").append(installCustomFlag);

        return strBuffer.toString();
    }
}
