/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf;

import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;

/**
 * ************************************************************************
 * <p/>
 * File name:  IPubMCPTTManager.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Puneet Singhania       June 27,2019       9.1.1
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

public interface IPubMCPTTManager {

    /**
     *
     * @param ipmcsdto
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnMCSXCAPRespDTO getMCPTTUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    public KnMCSXCAPRespDTO getMCPTTUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    public KnMCSXCAPRespDTO getMCPTTServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException;

}
