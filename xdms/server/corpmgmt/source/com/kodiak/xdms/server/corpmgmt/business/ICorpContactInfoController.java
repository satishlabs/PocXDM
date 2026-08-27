/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpContactInfoController.java
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubscContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnReverseContactsRespDto;

import java.util.List;


public interface ICorpContactInfoController {

    public KnCorpContactListRespDTO getCorpMasterList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpSubscContactListRespDTO getCorpResourceList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpSubscContactListRespDTO getCorpSubscContactList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO pushSublists(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO removeSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO addExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyExtContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO removeExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn);

    public KnCorpContactListRespDTO getExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn);

    public KnReverseContactsRespDto getSubscrReverseContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO removeSubscribersContacts(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpContactListRespDTO getCorpExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifyBulkCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getLiEvents(KnIPCorpSubscContactListDTO contactListDTO, List<String> mdns, Integer dbSublistId, KnPersisterTxn persisterTxn);
}
