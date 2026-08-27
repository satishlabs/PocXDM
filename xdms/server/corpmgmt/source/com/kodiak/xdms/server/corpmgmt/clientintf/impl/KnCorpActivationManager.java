/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpActivationManager.java
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
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpActivationInfoController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpActivationManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpActivationRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;


public class KnCorpActivationManager implements ICorpActivationManager {

    private ICorpActivationInfoController activationInfoController;

    KnCorpActivationManager() {

        activationInfoController = KnCorpBORegistry.createCorpActivationInfoController();
    }

    public KnCorpActivationRespDTO getSubscriberEmailId(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        clientActRequestDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        clientActRequestDTO.setOperationType(KnOperationTypes.GET_SUBS_EMAIL);
        clientActRequestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.getSubscribersEmailId(clientActRequestDTO, persisterTxn);
    }

    public KnCorpActivationRespDTO generateActivationCodes(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        clientActRequestDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        clientActRequestDTO.setOperationType(KnOperationTypes.GENEARTE_ACTIVATION_CODES);
        clientActRequestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.generateActivationCodes(clientActRequestDTO, persisterTxn);
    }

    public KnCorpActivationRespDTO saveClientActivationCode(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        clientActRequestDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        clientActRequestDTO.setOperationType(KnOperationTypes.SAVE_ACT_CADE);
        clientActRequestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.saveClientActivationMail(clientActRequestDTO, persisterTxn);
    }

    public KnCorpActivationRespDTO getMailInfo(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        clientActRequestDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        clientActRequestDTO.setOperationType(KnOperationTypes.GET_EMAIL_INFO);
        clientActRequestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.getMailInfo(clientActRequestDTO, persisterTxn);
    }

     public KnCorpActivationRespDTO sendActivationMail(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        clientActRequestDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        clientActRequestDTO.setOperationType(KnOperationTypes.SEND_ACTIVATION_MAIL);
        clientActRequestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.sendActivationMail(clientActRequestDTO, persisterTxn);
    }

    public KnCorpActivationRespDTO sendMail(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        clientActRequestDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        clientActRequestDTO.setOperationType(KnOperationTypes.SEND_ACTIVATION_MAIL);
        clientActRequestDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.sendMail(clientActRequestDTO, persisterTxn);
    }


    @Override
    public KnCorpActivationRespDTO getSubscrActivationCode(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        activationDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        activationDTO.setOperationType(KnOperationTypes.GET_SUBSCR_ACTIVATION_CODE);
        activationDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.getSubscrActivationCode(activationDTO, persisterTxn);
    }

    @Override
    public KnCorpActivationRespDTO getTempPwdForLegacy(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        activationDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        activationDTO.setOperationType(KnOperationTypes.GET_TMP_PWD_FOR_LEGACY);
        activationDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.getTempPwdForLegacy(activationDTO, persisterTxn);
    }

    @Override
    public KnCorpActivationRespDTO generateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        activationDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        activationDTO.setOperationType(KnOperationTypes.GENERATE_OTP);
        activationDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.generateOTP(activationDTO, persisterTxn);
    }

    @Override
    public KnCorpActivationRespDTO validateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        activationDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        activationDTO.setOperationType(KnOperationTypes.VALIDATE_OTP);
        activationDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.validateOTP(activationDTO, persisterTxn);
    }

    @Override
    public KnCorpActivationRespDTO getCorpBanFanDetails(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        activationDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        activationDTO.setOperationType(KnOperationTypes.GET_CORP_BAN_FAN_DETAILS);
        activationDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.getCorpBanFanDetails(activationDTO, persisterTxn);
    }

    @Override
    public KnCorpActivationRespDTO generateActivationCodeIDMIntf(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        activationDTO.setEntityId(KnEntityTypes.CORP_ACT_MANAGER);
        activationDTO.setOperationType(KnOperationTypes.GET_REST_SUBSCR_ACTIVATION_CODE);
        activationDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return activationInfoController.generateActivationCodeIDMIntf(activationDTO, persisterTxn);
    }

}
