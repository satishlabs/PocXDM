/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpActivationInfoController.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya      Nov 28, 2011      7.2
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpActivationRespDTO;

public interface ICorpActivationInfoController {

    public KnCorpActivationRespDTO getSubscribersEmailId(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO generateActivationCodes(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO saveClientActivationMail(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO getMailInfo(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO sendActivationMail(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO sendMail(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO getSubscrActivationCode(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO getTempPwdForLegacy(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO generateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO validateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO getCorpBanFanDetails(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);

    public KnCorpActivationRespDTO generateActivationCodeIDMIntf(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn);
}
