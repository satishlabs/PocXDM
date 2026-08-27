/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPTGSSListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;

/**
 Created by venkata sudhakar talluri on 28-12-2018
 */

public interface IpubTGSSListManger {

    public KnIPTGSSListDTO getTGSSList(KnIPTGSSListDTO tgssList, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    public KnOpPubResponse updateTGSSList(KnIPTGSSListDTO tgssList, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    public KnOpPubResponse deleteTGSSList(KnIPTGSSListDTO tgssList, KnPersisterTxn persisterTxn) throws KnXDMServerException;

}
