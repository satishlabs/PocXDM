/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscCloningListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

public interface ICorpAllCloningManager {

    public KnCorpResponseDTO cloneValidation(KnIPCorpSubscCloningListDTO contactListDTO, KnPersisterTxn persisterTxn);

    int getCurrentEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException;

}
