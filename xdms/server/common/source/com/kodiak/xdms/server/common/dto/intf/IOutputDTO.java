/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.intf;

/**
 * *****************************************************************************
 * File name:   IOutputDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/23/10       7.0
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
public interface IOutputDTO extends IGenDTO {

    public String getResponseCode();

    public void setResponseCode(String responseCode);

    public int getResponseStatus();

    public void setResponseStatus(int responseStatus);

    public String getResponseMessage();

    public void setResponseMessage(String responseMessage);
}
