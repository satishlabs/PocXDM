/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpContactManagerImpl.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 7, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpContactInfoController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpContactManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubscContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnReverseContactsRespDto;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.List;


public class KnCorpContactManager implements ICorpContactManager {

    private ICorpContactInfoController contactInfoController;

    KnCorpContactManager() {
        contactInfoController = KnCorpBORegistry.createCorpContactInfoController();
    }


    public KnCorpContactListRespDTO getCorpMasterList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {

        corpInfoDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        corpInfoDTO.setOperationType(KnOperationTypes.GET_MASTER_LIST);
        corpInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.getCorpMasterList(corpInfoDTO, persisterTxn);
    }

    public KnCorpSubscContactListRespDTO getCorpResourceList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.GET_CORP_RESOURCE_LIST);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.getCorpResourceList(contactDTO, persisterTxn);
    }

    public KnCorpSubscContactListRespDTO getCorpSubscContactList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.GET_SUBS_CONTACT_LIST);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.getCorpSubscContactList(contactDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifyCorpSubscContacts(KnIPCorpSubscContactListDTO
            contactListDTO, KnPersisterTxn persisterTxn) {
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.MODIFY_SUBS_CONTACT_LIST);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.modifyCorpSubscContacts(contactListDTO, persisterTxn);
    }

    public KnCorpResponseDTO cloneCorpSubscContacts(KnIPCorpSubscContactListDTO
                                                             contactListDTO, KnPersisterTxn persisterTxn) {
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.CLONE_SUBS_CONTACT_LIST);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.modifyCorpSubscContacts(contactListDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifyCorpSubscContactsUpmCall(KnIPCorpSubscContactListDTO
                                                             contactListDTO, KnPersisterTxn persisterTxn) {
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.UPM_MODIFY_SUBS_CONTACT_LIST);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.modifyCorpSubscContacts(contactListDTO, persisterTxn);
    }

    public KnCorpResponseDTO pushSublists(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn) {
        distDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        distDTO.setOperationType(KnOperationTypes.PUSH_SUBLISTS);
        distDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.pushSublists(distDTO, persisterTxn);
    }

    public KnCorpResponseDTO removeSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn) {
        subsRequestDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        subsRequestDTO.setOperationType(KnOperationTypes.REMOVE_SUBLIST);
        subsRequestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.removeSublist(subsRequestDTO, persisterTxn);
    }

    public KnCorpResponseDTO addExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.ADD_EXT_CONTACTS);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.addExtContacts(contactListDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifyExtContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.MODIFY_EXT_CONTACTS);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.modifyExtContacts(contactDTO, persisterTxn);
    }

    public KnCorpResponseDTO removeExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.REMOVE_EXT_CONTACTS);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.removeExtContacts(contactListDTO, persisterTxn);
    }

    public KnCorpContactListRespDTO getExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.GET_EXT_CONTACTS);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.getExtContactDetails(contactListDTO, persisterTxn);
    }

    public KnReverseContactsRespDto getSubscrReverseContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.GET_SUBSCR_REVERSE_CONTACTS);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.getSubscrReverseContacts(contactDTO, persisterTxn);
    }

    public KnCorpResponseDTO removeSubscribersContacts(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        subscriberInfoDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        subscriberInfoDTO.setOperationType(KnOperationTypes.REMOVE_SUBSCRIBERS_CONTACTS);
        subscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.removeSubscribersContacts(subscriberInfoDTO, persisterTxn);
    }

    public KnCorpContactListRespDTO getCorpExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn){
        contactListDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactListDTO.setOperationType(KnOperationTypes.GET_CORP_EXT_CONTACTS);
        contactListDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return contactInfoController.getCorpExtContactDetails(contactListDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifyBulkCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        return contactInfoController.modifyBulkCorpSubscContacts(contactListDTO, persisterTxn);
    }

    public KnCorpResponseDTO getLiEvents(KnIPCorpSubscContactListDTO contactListDTO, List<String> mdns, Integer dbSublistId, KnPersisterTxn persisterTxn) {
        return contactInfoController.getLiEvents(contactListDTO, mdns, dbSublistId, persisterTxn);
    }
}
