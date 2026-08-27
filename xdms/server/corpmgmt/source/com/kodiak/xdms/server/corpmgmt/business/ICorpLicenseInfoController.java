/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpLicenseInfoController.java
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
package com.kodiak.xdms.server.corpmgmt.business;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPLicenseSubsListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpLicensePackListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpLicenseSubsListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

public interface ICorpLicenseInfoController {


    public KnCorpLicensePackListRespDTO getAllBillingMdns(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpLicenseSubsListRespDTO getLicenseSubscribers(KnIPLicenseSubsListDTO ipLicenseSubsListDTO,
                                                              KnPersisterTxn persisterTxn);
    
    public KnCorpResponseDTO updateBillingName(KnIPLicenseSubsListDTO ipLicenseSubsListDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO markForDelete(KnIPLicenseSubsListDTO ipLicenseSubsListDTO, KnPersisterTxn persisterTxn);

    public KnCorpLicenseSubsListRespDTO getLicenseSubscribersForCSR(KnIPLicenseSubsListDTO ipLicenseSubsListDTO,
                                                              KnPersisterTxn persisterTxn);

}
