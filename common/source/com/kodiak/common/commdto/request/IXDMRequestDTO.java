/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   IXDMRequestDTO.java
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
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;

public interface IXDMRequestDTO extends IIdentifier {

    public String getOperationType();

    public void setOperationType(String operationType);

    public int getClientType();

    public void setClientType(int clientType);

    public IAuthDTO getAuthDTO();

    public void setAuthDTO(IAuthDTO authDTO);

    public void setDestPttServerId(String destPttServerId);

    public String getDestPttServerId();

    public void setDestQueueName(String destQueueName);

    public String getDestQueueName();

    public String getTransactionId();

    public void setTransactionId(String transactionId);

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType);

    public KnConstants.HIERARCHY_TYPE getHierarchyType();


}
