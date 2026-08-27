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

public interface IPubMCVideoManager {
    public KnMCSXCAPRespDTO getMCVideoUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException;
    public KnMCSXCAPRespDTO getMCVideoUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException;
    public KnMCSXCAPRespDTO getMCVideoServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException;
}
