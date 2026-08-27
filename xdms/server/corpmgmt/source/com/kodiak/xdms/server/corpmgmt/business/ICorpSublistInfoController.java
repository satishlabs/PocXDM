/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpSublistInfoController.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 10, 2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpAutoPairingResponse;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistDistributionRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistRespDTO;


public interface ICorpSublistInfoController {

    public KnCorpResponseDTO addToParingList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpSublistRespDTO createSublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifySublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteSublist(KnIPCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn);

    //todo
    //public KnCorpResponseDTO deleteSublistList(KnIPCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn);

    public KnCorpSublistRespDTO getSublistDetails(KnIPCorpSublistDTO sublistReqDto, KnPersisterTxn persisterTxn);

    public KnCorpSublistListRespDTO getAllSublist(KnIPCorpInfoDTO corpInfoDto, KnPersisterTxn persisterTxn);

    public KnCorpSublistDistributionRespDTO getDistributionList(KnIPCorpSublistDistDTO distributionInfoDto, KnPersisterTxn persisterTxn);

    public KnCorpSublistListRespDTO getSubscrSublists(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO removeSubscribersAllSublist(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpAutoPairingResponse updateCorpAutoPairing(KnIPCorpInfoDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpAutoPairingResponse addToPairingList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO assignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO unAssignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn);

    KnCorpResponseDTO unAssignCommonContactListForCloningContact(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn);
}
