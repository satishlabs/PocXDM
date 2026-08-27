/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpDeviceController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpDeviceManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeviceInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceListRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpDeviceManager implements ICorpDeviceManager {

    private ICorpDeviceController deviceController;

    KnCorpDeviceManager() {
        deviceController = KnCorpBORegistry.createCorpDeviceController();
    }

    @Override
    public KnCorpDeviceListRespDTO getDeviceList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        corpInfoDTO.setEntityId(KnEntityTypes.CORP_DEVICE_MANAGER);
        corpInfoDTO.setOperationType(KnOperationTypes.GET_DEVICE_LIST);
        corpInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return deviceController.getDeviceList(corpInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO subscDistDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException {
        subscDistDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        subscDistDTO.setOperationType(KnOperationTypes.GET_DEVICE_DETAILS);
        subscDistDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return deviceController.getDeviceDetails(subscDistDTO, persisterTxn);
    }
}
