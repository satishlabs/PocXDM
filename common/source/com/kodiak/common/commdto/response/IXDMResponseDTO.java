/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   IXDMResponseDTO.java
 * Subsystem:   Common Communication DTO
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/9/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;

import java.util.Collection;

public interface IXDMResponseDTO extends IIdentifier {

    public String getResponseCode();

    public void setResponseCode(String responseCode);

    public int getResponseStatus();

    public void setResponseStatus(int responseStatus);

    public String getResponseMessage();

    public void setResponseMessage(String responseMessage);

    public Collection getResponseDetails();

    public void setResponseDetails(Collection responseDetails);

    public void setDestPttServerId(String destPttServerId);

    public String getDestPttServerId();

    public void setDestQueueName(String destQueueName);

    public String getDestQueueName();

    public String getTransactionId();

    public void setTransactionId(String transactionId);

}
