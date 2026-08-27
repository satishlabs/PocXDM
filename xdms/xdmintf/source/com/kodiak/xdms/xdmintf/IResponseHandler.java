/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IResponseHandler.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 9, 2011      7.0
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
package com.kodiak.xdms.xdmintf;

import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.common.commdto.response.IXDMResponseDTO;


public interface IResponseHandler {

    public boolean processResponse(IXDMResponseDTO respDto,  KnMessage reqMsg);

    public boolean deleteAppIntfMsg(KnMessage reqMsg);

    public boolean processJsonResponse(IXDMResponseDTO respDto,  KnMessage reqMsg);

    public boolean processCorpBanFanJsonResponse(IXDMResponseDTO respDto,  KnMessage reqMsg);

    boolean processResponse(IXDMResponseDTO respDTO, KnMessage msgObj, boolean synch_response);
}
