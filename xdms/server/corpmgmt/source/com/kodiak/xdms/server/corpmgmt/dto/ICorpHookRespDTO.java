/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpHookRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      11/25/11      7.2
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


import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpFailedData;

import java.util.Collection;
import java.util.Map;

public interface ICorpHookRespDTO {

    public void setResponse(Object response);

    public Object getResponse();

    public void setData(Object data);

    public Object getData();

    public int getStatus();

    public void setStatus(int status);

    public String getStatusCode();

    public void setStatusCode(String statusCode);

    public String getMessage();

    public void setMessage(String message);

    public Collection<KnCorpFailedData> getFailedDataList();

    public void setFailedDataList(Collection<KnCorpFailedData> failedDataList);

    public Map<String, KnOPDirChgDTO> getChangeLogMap();

    public void setChangeLogMap(Map<String, KnOPDirChgDTO> changeLogMap);
}
