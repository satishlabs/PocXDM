/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/************************************************************************
 * File name:   IGenDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       Dec 15, 2010   7.0
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
 ***************************************************************************/

package com.kodiak.xdms.server.common.dto.intf;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.xdms.server.common.KnXDMError;

public interface IGenDTO extends IIdentifier, IPopulate {

    // returns the status of the DTO object
    int getDTOStatus();

    // sets the status to tht DTO
    void setDTOStatus(int dtoStatus);

    // returns the error object
    KnXDMError getErrorObject();

    // sets error object
    void setErrorObject(KnXDMError errorObj);
}
