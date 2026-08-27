/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnCorpProfileController.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/2/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.business.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.business.ICorpProfileController;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.util.Calendar;

public class KnCorpProfileController implements ICorpProfileController {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpProfileController.class);
    private static final String className = KnCorpProfileController.class.getName();
    private String xdmPttServerId;

    private KnGenInfoUtil genInfoUtil;

    public KnCorpProfileController() throws KnProvBOException {
        genInfoUtil = KnGenInfoUtil.getInstance();
    }


    public KnOPCorpProfileInfoDTO createCorporateProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "createCorporateProfile(KnIPCorpProfileInfoDTO, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY: received Input DTO - " + corpProfileInfoDTO);
        boolean ownedTxn = false;
        KnOPCorpProfileInfoDTO respDTO;

        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                this.xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error( methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error( methodName, e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();

            String extCorpId = corpProfileInfoDTO.getExtCorpId();
            corpProfilePersistDTO.setExtCorpId(extCorpId);

            corpProfilePersistDTO.setXDMSHome(xdmPttServerId);
            if (corpProfileInfoDTO.getCorporateName() != null) {
                corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
            }
            long createProfileCreationTime = Calendar.getInstance().getTimeInMillis();
            corpProfilePersistDTO.setProfileCreationTime(createProfileCreationTime);
            corpProfilePersistDTO.setLastProfileUpdateTime(createProfileCreationTime);

            corpProfilePersistDTO.setMaxCorpGroups(corpProfileInfoDTO.getMaxCorpGroups());
            corpProfilePersistDTO.setMaxCorpLists(corpProfileInfoDTO.getMaxCorpLists());
            corpProfilePersistDTO.setMaxSubscribers(corpProfileInfoDTO.getMaxSubscribers());
            corpProfilePersistDTO.setMaxMembersPerCorpGroup(corpProfileInfoDTO.getMaxMembersPerCorpGroup());
            corpProfilePersistDTO.setMaxMembersPerCorpList(corpProfileInfoDTO.getMaxMembersPerCorpList());

            //validate the Persist DTO


            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            int corpId = xdmServerDAO.createCorporateProfile(corpProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction");
                persisterTxn.save();
            }

            respDTO = new KnOPCorpProfileInfoDTO();
            respDTO.setCorpId(corpId);
            respDTO.setExtCorpId(extCorpId);
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_CORP_PROFILE_SUCCESS);

            return respDTO;

        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occurred");
            if (ownedTxn) rollback(persisterTxn);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.CORP_PROFILE_ALREADY_EXISTS, "Corporate profile already exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occurred :" + ex);
                throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (Exception e) {
            knLogger.error( methodName, "Exception occurred while Create corporate Profile");
            if (ownedTxn) rollback(persisterTxn);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while create corporate profile", e);
        } finally {
            knLogger.info( methodName, "EXIT: Create Corporate Profile ");
        }
    }

    public KnOPProvDTO deleteCorporateProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        return null;
    }


    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveCorporationId(KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY: retrive Corporation Id ");
        boolean ownedTxn = false;
        int corporationId = -1;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                this.xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error( methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error( methodName, e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            corporationId = xdmServerDAO.retrieveCorporationId(extCorpId, persisterTxn);

            if (ownedTxn){
                knLogger.debug( methodName, "saving the transaction");
                persisterTxn.save();

            }


        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occurred");
            if (ownedTxn) rollback(persisterTxn);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.CORP_PROFILE_ALREADY_EXISTS, "Corporate profile already exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occurred :" + ex);
                throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (Exception e) {
            knLogger.error( methodName, "Exception occurred while Create corporate Profile");
            if (ownedTxn) rollback(persisterTxn);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while create corporate profile", e);
        } finally {
            knLogger.info( methodName, "EXIT: Create Corporate Profile ");
        }

        return corporationId;
    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            txn.rollback();
        } catch (Exception e) {
            knLogger.error( "rollback(txn)", "Failed to rollback the transaction.");
        }
    }
}
