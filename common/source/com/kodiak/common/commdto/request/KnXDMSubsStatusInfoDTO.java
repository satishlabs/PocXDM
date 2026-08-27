/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMSubsStatusInfoDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/22/10       7.0
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

package com.kodiak.common.commdto.request;

public class KnXDMSubsStatusInfoDTO extends KnXDMSubsInfoDTO {

    private static final long serialVersionUID = 7526471155622676145L;
    //stores the service auth status
    private int serviceAuthStatus;

    /**
     * getter method for Service Auth Status
     * @return int
     */
    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    /**
     * setter method for Service Auth Status
     * @param serviceAuthStatus int
     */
    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(50);
        strBuffer.append(super.toString());
        strBuffer.append(", Service_Auth_Status - ").append(serviceAuthStatus);

        return strBuffer.toString();
    }

}
