/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpGenericInfoController;
import com.kodiak.xdms.server.corpmgmt.business.ICorpLicenseInfoController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpLicenseManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpAuthInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPLicenseSubsListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpAuthInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpLicensePackListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpLicenseSubsListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpLicenseManager.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
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

public class KnCorpLicenseManager implements ICorpLicenseManager {

    private ICorpLicenseInfoController licenseInfoController;
    private ICorpGenericInfoController genericInfoController;

    KnCorpLicenseManager() {
        licenseInfoController = KnCorpBORegistry.createCorpLicenseInfoController();
        genericInfoController = KnCorpBORegistry.createCorpAuthInfoController();

    }

    public KnCorpAuthInfoRespDTO licenseAuthenticate(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {

        authInfoDTO.setEntityId(KnEntityTypes.CORP_LICENSE_MANAGER);
        authInfoDTO.setOperationType(KnOperationTypes.AUTHENTICATE);
        authInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return genericInfoController.authenticate(authInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpLicensePackListRespDTO getAllBillingMdns(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn){
        corpInfoDTO.setEntityId(KnEntityTypes.CORP_LICENSE_MANAGER);
        corpInfoDTO.setOperationType(KnOperationTypes.GET_BILLING_MDNS);
        corpInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return licenseInfoController.getAllBillingMdns(corpInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO markForDelete(KnIPLicenseSubsListDTO licenseSubsListDTO,  KnPersisterTxn persisterTxn) {
        licenseSubsListDTO.setEntityId(KnEntityTypes.CORP_LICENSE_MANAGER);
        licenseSubsListDTO.setOperationType(KnOperationTypes.MARK_SUBS_FOR_DELETION);
        licenseSubsListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return licenseInfoController.markForDelete(licenseSubsListDTO, persisterTxn);
    }

    @Override
    public KnCorpLicenseSubsListRespDTO getLicenseSubscribers(KnIPLicenseSubsListDTO licenseSubsListDTO, KnPersisterTxn persisterTxn) {
        licenseSubsListDTO.setEntityId(KnEntityTypes.CORP_LICENSE_MANAGER);
        licenseSubsListDTO.setOperationType(KnOperationTypes.GET_LICENSE_SUBS);
        licenseSubsListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return licenseInfoController.getLicenseSubscribers(licenseSubsListDTO, persisterTxn);
    }
    
    @Override
    public KnCorpResponseDTO updateBillingName(KnIPLicenseSubsListDTO licenseSubsListDTO, KnPersisterTxn persisterTxn) {
        licenseSubsListDTO.setEntityId(KnEntityTypes.CORP_LICENSE_MANAGER);
        licenseSubsListDTO.setOperationType(KnOperationTypes.UPDATE_BILLING_NAME);
        licenseSubsListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return licenseInfoController.updateBillingName(licenseSubsListDTO, persisterTxn);
    }

    @Override
    public KnCorpLicenseSubsListRespDTO getLicenseSubscribersForCSR(KnIPLicenseSubsListDTO licenseSubsListDTO, KnPersisterTxn persisterTxn) {
        licenseSubsListDTO.setEntityId(KnEntityTypes.CORP_LICENSE_MANAGER);
        licenseSubsListDTO.setOperationType(KnOperationTypes.GET_LICENSE_SUBS_CSR);
        licenseSubsListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return licenseInfoController.getLicenseSubscribersForCSR(licenseSubsListDTO, persisterTxn);
    }
}
