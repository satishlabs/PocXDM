/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistManager.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 10, 2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.business.ICorpSublistInfoController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpSublistManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpAutoPairingResponse;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistDistributionRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;


public class KnCorpSublistManager implements ICorpSublistManager {

    private ICorpSublistInfoController sublistInfoController;

    KnCorpSublistManager() {
        sublistInfoController = KnCorpBORegistry.createCorpSublistInfoController();
    }

    public KnCorpResponseDTO pairCorpContact(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_CONTACT_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.PAIR_CORP_CONTACT);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.addToParingList(contactDTO, persisterTxn);
    }

    public KnCorpSublistRespDTO createSublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn) {
        sublistInfoDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        sublistInfoDTO.setOperationType(KnOperationTypes.CREATE_SUBLIST);
        sublistInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.createSublist(sublistInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifySublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn) {
        sublistInfoDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        sublistInfoDTO.setOperationType(KnOperationTypes.MODIFY_SUBLIST);
        sublistInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.modifySublist(sublistInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO deleteSublist(KnIPCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn) {
        sublistDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        sublistDTO.setOperationType(KnOperationTypes.DELETE_SUBLIST);
        sublistDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.deleteSublist(sublistDTO, persisterTxn);
    }

    public KnCorpSublistRespDTO getSublistDetails(KnIPCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn) {
        sublistDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        sublistDTO.setOperationType(KnOperationTypes.GET_SUBLIST_DETAILS);
        sublistDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.getSublistDetails(sublistDTO, persisterTxn);
    }

    public KnCorpSublistListRespDTO getAllSublist(KnIPCorpInfoDTO corpInfoDto, KnPersisterTxn persisterTxn) {
        corpInfoDto.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        corpInfoDto.setOperationType(KnOperationTypes.GET_ALL_SUBLISTS);
        corpInfoDto.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.getAllSublist(corpInfoDto, persisterTxn);
    }

    public KnCorpSublistDistributionRespDTO getDistributionList(KnIPCorpSublistDistDTO distributionInfoDto, KnPersisterTxn persisterTxn) {
        distributionInfoDto.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        distributionInfoDto.setOperationType(KnOperationTypes.GET_DIST_LIST);
        distributionInfoDto.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.getDistributionList(distributionInfoDto, persisterTxn);
    }

    public KnCorpSublistListRespDTO getSubscrSublists(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.GET_SUBSCR_SUBLISTS);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.getSubscrSublists(contactDTO, persisterTxn);
    }

    public KnCorpResponseDTO removeSubscribersAllSublist(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        subscriberInfoDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        subscriberInfoDTO.setOperationType(KnOperationTypes.REMOVE_SUBSCRIBERS_ALL_SUBLIST);
        subscriberInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.removeSubscribersAllSublist(subscriberInfoDTO, persisterTxn);
    }

    /**
     * This method will update the auto pair contact id of the corporate send in the request which will be via the OP CLI or the audit
     * based on the indicator passed i.e anabke or disable
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpAutoPairingResponse updateCorpAutoPairing(KnIPCorpInfoDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.UPDATE_CORP_AUTO_PAITING);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.updateCorpAutoPairing(contactDTO, persisterTxn);
    }

    /**
     * This method will be used to add the subscriber sent in the request to the pairing list of the corporate
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpAutoPairingResponse addToPairingList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        contactDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.ADD_TO_PAIRINF_LIST);
        contactDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.addToPairingList(contactDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO assignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        subscDistDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        subscDistDTO.setOperationType(KnOperationTypes.ASSIGN_COMMON_CONTACT_LIST);
        subscDistDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.assignCommonContactList(subscDistDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO unAssignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        subscDistDTO.setEntityId(KnEntityTypes.CORP_SUBLIST_MANAGER);
        subscDistDTO.setOperationType(KnOperationTypes.UNASSIGN_COMMON_CONTACT_LIST);
        subscDistDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return sublistInfoController.unAssignCommonContactList(subscDistDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO unAssignCommonContactListForCloningContact(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {

        return sublistInfoController.unAssignCommonContactListForCloningContact(subscDistDTO, persisterTxn);
    }
}
