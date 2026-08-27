/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IResponseDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto;

import com.kodiak.common.commdto.response.KnXDMFailureRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpFailedData;

import java.util.Collection;


public interface ICorpResponseDTO {

    public void setStatus(int status);

    public int getStatus();

    public void setStatusCode(String statusCode);

    public String getStatusCode();

    public void setMessage(String message);

    public String getMessage();

    public Collection<KnCorpFailedData> getFailedDataList();

    public void setFailedDataList(Collection<KnCorpFailedData> failedDataList);

    public Collection<KnXDMFailureRespDTO> getFailureDetails();

    public void setFailureDetails(Collection<KnXDMFailureRespDTO> failureDetails);
}
