/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.xmlmodifier.impl.KnXMLProcessor;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.business.IPubInfoController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubSubsDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubInfoController.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Feb 16, 2011           7.0
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
public class KnPubInfoController implements IPubInfoController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubInfoController.class);


    private static final String CLASSNAME = KnPubInfoController.class.getName();
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = new KnPubInfoUtil();
    private static KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private static KnXMLProcessor xmlProcessor = KnXMLProcessor.getInstance();


    /**
     *
     */
    public KnPubInfoController() {//throws KnFWException {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
    }


    /**
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void forceSync(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "forceSync(KnIPPubSubsDTO, persisterTxn)";
        boolean ownedTxn = false;
        KnSubscriberPersistDTO subsPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + subsInfoDTO);

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String ownerMdn = subsInfoDTO.getMdn();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    subsInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
            // String pocServerId = subsProfile.getPocHome();

            subsPersistDTO = new KnSubscriberPersistDTO();
            subsPersistDTO.setInputDTO(subsInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());

            subsPersistDTO.setPersistenceDTO(originator);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(subsPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");
            subsPersistDTO.setMdn(ownerMdn);

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // Sync Resource lists
            knLogger.debug(methodName, "Updating all Resource docs...");
            xdmServerDAO.syncAllContactDocs(ownerMdn, persisterTxn);

            // Sync Group docs
            knLogger.debug(methodName, "Updating all Group docs...");
            xdmServerDAO.syncAllGroupDocs(ownerMdn, persisterTxn);

            knLogger.debug(methodName, "Updated all docs for Mdn...");

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occured :" + aex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while doing sync for mdn: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "doing sync for mdn", ex);
        }
        return;

    }


    /**
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "deleteAllContactsAndGroups(KnIPPubSubsDTO, persisterTxn)";
        boolean ownedTxn = false;
        boolean readOnly = false;
        String xdmServerId = null;
        KnSubsProfileDTO subsProfile = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + subsInfoDTO);

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String ownerMdn = subsInfoDTO.getMdn();
            List<String> ownerMdnList = subsInfoDTO.getMdnList();
            if(null != ownerMdn) {
                 subsProfile = pubInfoUtil.getProfileDetails(ownerMdn, subsInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
                 xdmServerId = subsProfile.getXdmsHome();
            }

            //String pocServerId = subsProfile.getPocHome();
            if(null != ownerMdnList && !ownerMdnList.isEmpty()){
                xdmServerId = subsInfoDTO.getPttServerId();
            }
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // delete Resource list related data
            knLogger.debug(methodName, "Deleting all Contact List memebrs...");
            if(null != ownerMdnList && !ownerMdnList.isEmpty()){
                xdmServerDAO.deleteAllContacts(ownerMdnList,persisterTxn);
            }else {
                xdmServerDAO.deleteAllContacts(ownerMdn, persisterTxn);
            }

            //rqHistoryBasedPresence_23 : Populating the DG.publicContactCount table
            //delete Public Contact count table related data
            knLogger.debug(methodName, "Deleting public contact count record ..");
            if(null != ownerMdnList && !ownerMdnList.isEmpty()){
                xdmServerDAO.deleteContactCount(ownerMdnList, persisterTxn);
            } else {
                xdmServerDAO.deleteContactCount(ownerMdn, persisterTxn);
            }
            // delete Group related data
            knLogger.debug(methodName, "Deleting all Group docs...");
            if(null != ownerMdnList && !ownerMdnList.isEmpty()){
                xdmServerDAO.deleteAllGroups(ownerMdnList, persisterTxn);
            } else{
                xdmServerDAO.deleteAllGroups(ownerMdn, persisterTxn);
            }

            knLogger.debug(methodName, "Deleted all Public docs for Mdn...");

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while deleting contacts and groups for mdn: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "deleting contacts and groups for mdn", ex);
        }
        return;

    }


    /**
     * @param changeMdnDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void changeMdn(KnIPChangeMDNInfoDTO changeMdnDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "changeMdn(KnIPChangeMDNInfoDTO, persisterTxn)";
        boolean ownedTxn = false;
        KnSubscriberPersistDTO subsPersistDTO = null;
        boolean readOnly = false;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + changeMdnDTO);

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String mdn = changeMdnDTO.getOldMDN();
            String newMdn = changeMdnDTO.getNewMDN();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(newMdn,
                    changeMdnDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
            //String pocServerId = subsProfile.getPocHome();

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // Update Resource lists
            knLogger.debug(methodName, "Updating all Resource docs...");
            xdmServerDAO.updateAllContactDocs(mdn, newMdn, persisterTxn);

            //Update MDN in the Public Contact Count table
            knLogger.debug(methodName, "Updating MDN in the Public Contact Count table ...");
            int publicContactCount = xdmServerDAO.getPublicContactCount(mdn, persisterTxn);
            // if public contact count is -1 means for that mdn no contacts so no record exists in the public contact count
            if (publicContactCount != -1)
                xdmServerDAO.updateContactMDN(mdn, newMdn, publicContactCount, persisterTxn);
            // Update Group docs
            knLogger.debug(methodName, "Updating all Group docs...");
            Collection<KnPubGroupDTO> groupDocList = xdmServerDAO.getAllGroupDocsForMdn(mdn, persisterTxn);
            if (groupDocList != null && !groupDocList.isEmpty()) {
                for (KnPubGroupDTO groupDoc : groupDocList) {
                    String listServiceUri = groupDoc.getListServiceURI();
                    knLogger.debug(methodName, "List Service Uri: " + listServiceUri);
                    String updatedListServiceUri = listServiceUri.replace(mdn, newMdn);
                    knLogger.debug(methodName, "Updated List Service Uri: " + updatedListServiceUri);
                    String groupDocUri = groupDoc.getGroupDocURI();
                    knLogger.debug(methodName, "Group Doc Uri: " + groupDocUri);
                    String updatedGroupDocUri = groupDocUri.replace(mdn, newMdn);
                    knLogger.debug(methodName, "Updated Group Doc Uri: " + updatedGroupDocUri);

                    InputStream xmlDoc = groupDoc.getXmlDoc();
                    // Construct Xpath
                    String xpath = pubInfoUtil.constructChangeMdnXPath(listServiceUri);

                    // modify xml using xml modifier
                    InputStream updatedXmlDoc = xmlProcessor.updateListServiceUri(xmlDoc, updatedListServiceUri);
                    // Update the Group with new values
                    groupDoc.setOwner(newMdn);
                    groupDoc.setListServiceURI(updatedListServiceUri);
                    groupDoc.setGroupDocURI(updatedGroupDocUri);
                    groupDoc.setXmlDoc(updatedXmlDoc);
                }

                // update all oma group docs
                xdmServerDAO.updateAllOMAGroupDocs(groupDocList, persisterTxn);

                // update all other group docs
                xdmServerDAO.updateAllGroupDocs(mdn, newMdn, persisterTxn);

                knLogger.debug(methodName, "Updated all docs for Mdn...");
            } else {
                knLogger.debug(methodName, "No Group docs found for Mdn...");
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while doing changemdn: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "doing changemdn", ex);
        }
        return;

    }


    /**
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups4ListOfMdns(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "deleteAllContactsAndGroups4ListOfMdns(KnIPPubSubsDTO, persisterTxn)";
        knLogger.info(methodName, subsInfoDTO);

        try {
            String ownerMdn = subsInfoDTO.getMdnList().get(0);

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    subsInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
            //String pocServerId = subsProfile.getPocHome();

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // delete Resource list related data
            knLogger.debug(methodName, "Deleting all Contact List memebrs...");
            xdmServerDAO.deleteAllContacts(subsInfoDTO.getMdnList(), persisterTxn);

            //rqHistoryBasedPresence_23 : Populating the DG.publicContactCount table
            //delete Public Contact count table related data
            knLogger.debug(methodName, "Deleting public contact count record ..");
            xdmServerDAO.deleteContactCount(subsInfoDTO.getMdnList(), persisterTxn);
            // delete Group related data
            knLogger.debug(methodName, "Deleting all Group docs...");
            xdmServerDAO.deleteAllGroups(subsInfoDTO.getMdnList(), persisterTxn);

            knLogger.debug(methodName, "Deleted all Public docs for Mdn...");
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while deleting contacts and groups for mdn: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "deleting contacts and groups for mdn", ex);
        }
        knLogger.debug(methodName, "EXIT");

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
            knLogger.debug("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

}
