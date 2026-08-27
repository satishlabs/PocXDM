/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpContactCloningController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpAllCloningManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscCloningListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpAllCloningManager implements ICorpAllCloningManager {

    private ICorpContactCloningController contactInfoController;

    KnCorpAllCloningManager() {
        contactInfoController = KnCorpBORegistry.createCorpContactCloningController();
    }
    @Override
    public KnCorpResponseDTO cloneValidation(KnIPCorpSubscCloningListDTO
                                                     cloningListDTO, KnPersisterTxn persisterTxn) {
        cloningListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        cloningListDTO.setOperationType(KnOperationTypes.CLONE_CONTACTS_GROUP_FEATURES_VALIDATION);
        cloningListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.cloneValidation(cloningListDTO, persisterTxn);
    }

    public int getCurrentEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        return contactInfoController.getCurrentEtag(mdn, persisterTxn);
    }


}
