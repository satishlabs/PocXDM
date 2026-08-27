/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnEmergencyConfigDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubAuthListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;

/**
 * Created by schandra on 19-12-2017.
 */
public interface IpubAuthManger {

    public KnIPPubAuthListDTO getAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    public KnOpPubResponse updateAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    public KnEmergencyConfigDocDTO getEmergencyConfigDoc(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

}
