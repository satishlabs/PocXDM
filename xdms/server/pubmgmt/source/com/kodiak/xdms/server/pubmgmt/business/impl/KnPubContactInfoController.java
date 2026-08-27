/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.request.KnPUBContactAddonAliasDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpGpInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.pubmgmt.business.IPubContactInfoController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.KnPubDBTablesRegistry;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnPOCContactListDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOPPubDirResponse;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.common.*;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubContactInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import static com.kodiak.xdms.server.common.resources.KnConstants.USER_PROFILE_MGMT_BIT;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.*;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubContactInfoController.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnPubContactInfoController implements IPubContactInfoController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubContactInfoController.class);

    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = new KnPubInfoUtil();
    private KnGeneralUtil generalUtil = new KnGeneralUtil();
    private static KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    int clusterId = 0;

    /**
     *
     */
    public KnPubContactInfoController() {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
    }


    /**
     * @param ipContactInfoDTO
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse addContacts(KnIPPubContactInfoDTO ipContactInfoDTO,
                                       KnPersisterTxn persisterTxn) throws KnXDMServerException {

        final String methodName = "addContacts(KnIPPubContactInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean readOnly = false;
        KnPubContactInfoPersistDTO contactPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactInfoDTO);

        try {

            String ownerMdn = ipContactInfoDTO.getOwner();
            int indexDocEtag = ipContactInfoDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipContactInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicCntMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_CNT_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();
            // String pocServerId = subsProfile.getPocHome();

            contactPersistDTO = new KnPubContactInfoPersistDTO();
            contactPersistDTO.setInputDTO(ipContactInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            contactPersistDTO.setPersistenceDTO(originator);
            contactPersistDTO.setAcrtepc(allowPublicCntMgmtFlag);


            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmServerId);
            //Fetch the client type of members
            List<KnMemberDTO> listMems = new ArrayList<KnMemberDTO>();
            listMems = ipContactInfoDTO.getMembers();
            ArrayList<String> memberList = new ArrayList<>();
            for (KnMemberDTO mem : listMems) {
                memberList.add(mem.getMemberMdn());
            }

            List<KnMemberDTO> memberDTOs = xdmServerDAO.getMembersClientType(memberList, persisterTxn);

            contactPersistDTO.setPoCMembers(memberDTOs);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(contactPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");


            // Populate contact persist dto with input dto
            knLogger.debug(methodName, "Populating PersistDTO:");
            pubInfoUtil.populateContactInfoPersistDTO(ipContactInfoDTO, contactPersistDTO);
            knLogger.debug(methodName, "Populated PersistDTO:");
            // Populate contact member(s)
            contactPersistDTO.setContactMembers(ipContactInfoDTO.getMembers());

            validatorFwk.validate(contactPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");

            // Get current etag from contact list doc map
            int etag = xdmServerDAO.getResourceListEtagForMdn(ownerMdn, persisterTxn);
            if (indexDocEtag > 0 && etag != indexDocEtag) {
                knLogger.info(methodName, "Etag mismatch while add Contact : ", etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "ContactList modified");
            }

            // get contactlist id using ownerMdn from contactlist docmap
            int contactListId = xdmServerDAO.getContactListIdForMDN(ownerMdn, readOnly, persisterTxn);
            //  since only one member details in the list
            Collection<String> contactMDNs = new ArrayList<String>();
            for (KnMemberDTO members : ipContactInfoDTO.getMembers()) {
                contactMDNs.add(members.getMemberMdn());
            }
            String memMdn = ipContactInfoDTO.getMembers().iterator().next().getMemberMdn();
            if (contactMDNs.size() == 1) {
                if (xdmServerDAO.checkContactDetails(contactListId, memMdn, readOnly, persisterTxn)) {
                    knLogger.info(methodName, "Mem Already Exists in the ContactList :", KnGDPRTemplate.mdn(memMdn));
                    throw new KnPubBOException(KnErrorCodes.BOEntity.CONTACT_ALREADY_EXISTS, "Contact Already exists");
                }
            }

            // get current etag for xdm directory
            int dirDocEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);

            // update etag of directory
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);

            // update etag of contactlist docmap
            xdmServerDAO.updateContactListDocMapEtagsForMdn(ownerMdn, persisterTxn);


            knLogger.info(methodName, "Delete contacts before adding new , for bulk contact addition support");
            if (contactMDNs.size() > 1) {
                xdmServerDAO.deleteAllContacts(ownerMdn, persisterTxn);
                knLogger.info(methodName, "Delete all contact since its replce resource list doc");
            } else {
                knLogger.info(methodName, "Delete individual contact since its one contact addition");
                //Delete contacts before adding new list
                xdmServerDAO.deleteContacts(contactListId, contactMDNs, persisterTxn);
                // deleteing contacts in poc contact list
                xdmServerDAO.deleteContactsFromPOCContactList(ownerMdn, contactMDNs, persisterTxn);
            }

            knLogger.debug(methodName, "Adding contacts in the Relational Tables");
            // add contact in contact list member
            xdmServerDAO.addContacts(contactListId, ipContactInfoDTO.getMembers(), persisterTxn);
            String ufmi = null;
            // add contact in poc contact list
            xdmServerDAO.addContactsToPOCContactList(ownerMdn, ipContactInfoDTO.getMembers(), persisterTxn);

//========== //req :  rqHistoryBasedPresence_23 : Populating the DG.publicContactCount table
            int publicContactCount = xdmServerDAO.getPublicContactCount(ownerMdn, persisterTxn);
            if (publicContactCount == -1) {
                //insert
                publicContactCount = contactMDNs.size();
                xdmServerDAO.addContactCount(ownerMdn, publicContactCount, persisterTxn);
            } else if (publicContactCount >= 0) {
                if (contactMDNs.size() > 1) {
                    publicContactCount = contactMDNs.size();
                } else {
                    publicContactCount = publicContactCount + 1;
                }
                xdmServerDAO.updateContactCount(ownerMdn, publicContactCount, persisterTxn);
            }

            knLogger.debug(methodName, "Successfully Added contacts in the " +
                    "Relational Tables");

            if (subsProfile.getClientMajorVersion() >= KnConstants.PROTOCOL_VERSION_19_X && ipContactInfoDTO.getContactAddonAliasMap() != null && ipContactInfoDTO.getContactAddonAliasMap().size() > 0) {
                //Call to Grid Gain to update the aliasAddOnIds
                KnGeneralCacheUtil.insertPUBContactAddonAliasInfo(ipContactInfoDTO.getContactAddonAliasMap());
            } else {
                knLogger.info(methodName, "Either Owner MDN does not have required Protocol Version to perform new put AddonAlias operation or put request for contact does not have addonAliasIds. This OwnerMdn would populate the contact in older way.");
            }

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String contactDocUri = pubInfoUtil.constructContactListSelUri(ownerMdn);
            docChgDTO.setDocUri(contactDocUri);
            docChgDTO.setNewEtag(String.valueOf(etag + 1));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirDocEtag));
            int newEtag = dirDocEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            boolean subsUpmBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subsProfile.getActiveFS2(), USER_PROFILE_MGMT_BIT);
            knLogger.debug(methodName,"subsUpmBit :",subsUpmBit);
            if(subsUpmBit){
                //owner mdn is  0 - Base Mdn,1 - Profile Mdn
                dirChgDTO.setNtfyOnAnyMDN(subsProfile.getUserProfileIndex()>0?1:0);
            }
            result = pubInfoUtil.populateSuccessResponse();

            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(etag + 1));
        } catch (KnAASException | KnPubBOException aex) {
            knLogger.error(methodName, " Exception occured :", aex);
            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnPubBOValidationException ex) {
            knLogger.error(methodName, "Validation Exception occured :", ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_ALREADY_EXISTS, "Contact already exists");
            } else if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_DOES_NOT_EXISTS, "Contact does not exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while adding Contact: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "adding contact", ex);
        }
        knLogger.debug(methodName,"result :",result);
        return result;
    }


    /**
     * @param ipContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse modifyContacts(KnIPPubContactInfoDTO ipContactInfoDTO,
                                          KnPersisterTxn persisterTxn) throws KnXDMServerException {

        final String methodName = "modifyContacts(KnIPPubContactInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean readOnly = false;
        KnPubContactInfoPersistDTO contactPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactInfoDTO);

        try {

            String ownerMdn = ipContactInfoDTO.getOwner();
            int indexDocEtag = ipContactInfoDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipContactInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicCntMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_CNT_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();

            contactPersistDTO = new KnPubContactInfoPersistDTO();
            contactPersistDTO.setInputDTO(ipContactInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setInputDTO(ipContactInfoDTO);
            contactPersistDTO.setPersistenceDTO(originator);
            contactPersistDTO.setAcrtepc(allowPublicCntMgmtFlag);

            //Fetch the client type of members
            List<KnMemberDTO> listMems = new ArrayList<KnMemberDTO>();
            listMems = ipContactInfoDTO.getMembers();
            ArrayList<String> memberList = new ArrayList<>();
            String ufmi = null;
            String contactName = null;
            String contactMdn = null;
            for (KnMemberDTO mem : listMems) {
                memberList.add(mem.getMemberMdn());
                contactName = mem.getMemberName();
                ufmi = mem.getUfmi();
                contactMdn = mem.getMemberMdn();
            }

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmServerId);


            List<KnMemberDTO> memberDTOs = xdmServerDAO.getMembersClientType(memberList, persisterTxn);


            contactPersistDTO.setPoCMembers(memberDTOs);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(contactPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            // Populate contact persist dto with input dto
            pubInfoUtil.populateContactInfoPersistDTO(ipContactInfoDTO, contactPersistDTO);
            // Populate contact member(s)
            contactPersistDTO.setContactMembers(ipContactInfoDTO.getMembers());

            validatorFwk.validate(contactPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");


            // Get current etag from contact list doc map
            int etag = xdmServerDAO.getResourceListEtagForMdn(ownerMdn, persisterTxn);

            if (indexDocEtag > 0 && etag != indexDocEtag) {
                knLogger.info(methodName, "Etag mismatch while modify Contact : ", etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "ContactList modified");
            }

            int dirDocEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);

            // get contact list id from contact list doc map
            int contactListId = xdmServerDAO.getContactListIdForMDN(ownerMdn, readOnly, persisterTxn);

            // update etag for contactlist doc map
            xdmServerDAO.updateContactListDocMapEtagsForMdn(ownerMdn, persisterTxn);
            // Update contact name(s) in contactlistmember table.
            if (contactName != null) {
                xdmServerDAO.modifyContacts(contactListId, ipContactInfoDTO.getMembers(), persisterTxn);
            }
            if (ufmi != null) {
                xdmServerDAO.updateContactUfmi(ownerMdn, contactMdn, ufmi, persisterTxn);
            }


            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String contactDocUri = pubInfoUtil.constructContactListSelUri(ownerMdn);
            docChgDTO.setDocUri(contactDocUri);
            docChgDTO.setNewEtag(String.valueOf(etag + 1));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirDocEtag));
            int newEtag = dirDocEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            boolean subsUpmBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subsProfile.getActiveFS2(), USER_PROFILE_MGMT_BIT);
            knLogger.debug(methodName,"subsUpmBit :",subsUpmBit);
            if(subsUpmBit){
                //owner mdn is  0 - Base Mdn,1 - Profile Mdn
                dirChgDTO.setNtfyOnAnyMDN(subsProfile.getUserProfileIndex()>0?1:0);
            }
            result = pubInfoUtil.populateSuccessResponse();

            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(etag + 1));

        } catch (KnAASException | KnPubBOException aex) {
            knLogger.error(methodName, "Authorization Exception occured :", aex);
            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnPubBOValidationException ex) {
            knLogger.error(methodName, "Validation Exception occured :", ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_DOES_NOT_EXISTS,
                        "Contact Doesn't exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while modifying Contact: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "modifying  contact", ex);
        }
        knLogger.debug(methodName,"result :",result);
        return result;
    }


    /**
     * @param ipContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteContacts(KnIPPubContactInfoDTO ipContactInfoDTO,
                                          KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "deleteContacts(KnIPPubContactInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        KnPubContactInfoPersistDTO contactPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactInfoDTO);

        try {
            String ownerMdn = ipContactInfoDTO.getOwner();
            int indexDocEtag = ipContactInfoDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipContactInfoDTO.getProfile(),
                    true, KnConstants.FALSE, persisterTxn);
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicCntMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_CNT_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();
            // String pocServerId = subsProfile.getPocHome();

            contactPersistDTO = new KnPubContactInfoPersistDTO();
            contactPersistDTO.setInputDTO(ipContactInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(ownerMdn);
            originator.setInputDTO(ipContactInfoDTO);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            contactPersistDTO.setPersistenceDTO(originator);
            contactPersistDTO.setAcrtepc(allowPublicCntMgmtFlag);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(contactPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            // Populate contact persist dto with input dto
            pubInfoUtil.populateContactInfoPersistDTO(ipContactInfoDTO, contactPersistDTO);
            // Populate contact member(s)
            contactPersistDTO.setContactMembers(ipContactInfoDTO.getMembers());

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXDMServerDAO(xdmServerId);

            // get current etag from contact lsit doc map
            int etag = xdmServerDAO.getResourceListEtagForMdn(ownerMdn, persisterTxn);
            if (indexDocEtag > 0 && etag != indexDocEtag) {
                knLogger.info(methodName, "Etag mismatch while Delete Contact : ", etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "ContactList modified");
            }

            // get conatct list id from contact lsit doc map
            int contactListId = xdmServerDAO.getContactListIdForMDN(ownerMdn, false, persisterTxn);

            // update updated doc in db
            int dirDocEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);

            // delete contact in contact list member
            Collection<String> memberMdnList = new ArrayList<String>();
            Collection<KnMemberDTO> contactMembers = ipContactInfoDTO.getMembers();
            for (KnMemberDTO member : contactMembers) {
                memberMdnList.add(member.getMemberMdn());
            }
            xdmServerDAO.deleteContacts(contactListId, memberMdnList, persisterTxn);
            // deleteing contacts in poc contact list
            Collection<String> contactMDNs = new ArrayList<String>();
            ArrayList<KnPUBContactAddonAliasDTO> pubContactAddonAliasDTOS=new ArrayList<>();
            //Populating the dto to delete from GridGain Also
            for (KnMemberDTO members : ipContactInfoDTO.getMembers()) {
                contactMDNs.add(members.getMemberMdn());
                KnPUBContactAddonAliasDTO pubContactAddonAliasDTO=new KnPUBContactAddonAliasDTO();
                pubContactAddonAliasDTO.setContactMDN(members.getMemberMdn());
                pubContactAddonAliasDTO.setOwnerMDN(ownerMdn);
                pubContactAddonAliasDTOS.add(pubContactAddonAliasDTO);
            }
            xdmServerDAO.deleteContactsFromPOCContactList(ownerMdn, contactMDNs, persisterTxn);
            //Call to Grid Gain to delete data for AddOnAliasIds
            KnGeneralCacheUtil.deletePUBContactAddonAliasInfo(pubContactAddonAliasDTOS);
            // update etag of contactlist docmap
            xdmServerDAO.updateContactListDocMapEtagsForMdn(ownerMdn, persisterTxn);

//============= req :==  rqHistoryBasedPresence_23 : Populating the DG.publicContactCount table
            int publicContactCount = xdmServerDAO.getPublicContactCount(ownerMdn, persisterTxn);
            if (publicContactCount == 1) {
                xdmServerDAO.deleteContactCount(ownerMdn, persisterTxn);
            } else if (publicContactCount > 0) {
                //decrement
                int newCount = publicContactCount - 1;
                xdmServerDAO.updateContactCount(ownerMdn, newCount, persisterTxn);
            }

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String contactDocUri = pubInfoUtil.constructContactListSelUri(ownerMdn);
            docChgDTO.setDocUri(contactDocUri);
            docChgDTO.setNewEtag(String.valueOf(etag + 1));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirDocEtag));
            int newEtag = dirDocEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            boolean subsUpmBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subsProfile.getActiveFS2(), USER_PROFILE_MGMT_BIT);
            knLogger.debug(methodName,"subsUpmBit :",subsUpmBit);
            if(subsUpmBit){
                //owner mdn is  0 - Base Mdn,1 - Profile Mdn
                dirChgDTO.setNtfyOnAnyMDN(subsProfile.getUserProfileIndex()>0?1:0);
            }
            result = pubInfoUtil.populateSuccessResponse();

            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(etag + 1));
        } catch (KnAASException | KnPubBOException aex) {
            knLogger.error(methodName, " Exception occured :", aex);
            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_DOES_NOT_EXISTS, "Contact doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);

        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while deleting Contact: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "deleting contact", ex);
        }
        knLogger.debug(methodName,"result :",result);
        return result;
    }


    /**
     * @param ipContactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnPubContactDTO getContactListDetails(KnIPPubContactDTO ipContactDTO,
                                                 KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getContactList(KnIPPubContactInfoDTO, persisterTxn)";
        KnPubContactDTO result = null;
        KnPubContactPersistDTO contactPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactDTO);

        try {


            String ownerMdn = ipContactDTO.getOwner();
            int indexDocEtag = ipContactDTO.getIfNoneMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipContactDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
            contactPersistDTO = new KnPubContactPersistDTO();
            contactPersistDTO.setInputDTO(ipContactDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipContactDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            contactPersistDTO.setPersistenceDTO(originator);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(contactPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            // Populate contact persist dto with input dto
            pubInfoUtil.populateContactPersistDTO(ipContactDTO, contactPersistDTO);

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            result = pubInfoUtil.populateContactListSuccessResponse(ipContactDTO);
            int etag = xdmServerDAO.getResourceListEtagForMdn(ownerMdn, true, persisterTxn);

            result.setContactListName(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.LIST_NAME);
            result.setContactListDisplayName(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.LIST_DISPLAY_NAME);
            result.setEtag(etag);

            if (indexDocEtag > 0 && indexDocEtag == etag) {
                knLogger.info(methodName, "Contact Document not modifed :", indexDocEtag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_NOT_MODIFIED, "ContactList not modified");
            }
            List<KnXDMMdnInfoDTO> listOfXdmMdnInfoDTOs = new ArrayList<KnXDMMdnInfoDTO>();
            List<String> contactMdns = new ArrayList<>();
            Collection<Integer> contactListIds = xdmServerDAO.getContactListIdForMDN(ownerMdn, persisterTxn);
            Map<Integer, Collection<KnMemberDTO>> members = xdmServerDAO.getContactListMemberInfos(contactListIds, true, persisterTxn);

            Map<String, KnSubsProfilePersistDTO> subsMap = new HashMap<>();
            List<String> mdnList = new ArrayList<>();
            if (members != null) {
                for (Collection<KnMemberDTO> memberDTOs : members.values()) {
                    for (KnMemberDTO memberDTO : memberDTOs) {
                        mdnList.add(memberDTO.getMemberMdn());
                    }
                }
                //getting additional details for mdn.
                subsMap = pubInfoUtil.getSubscriberDetails(mdnList, true, persisterTxn);
                knLogger.debug(methodName, "subsMap : ", KnGDPRTemplate.mapKeyMdn(subsMap));
            } else {
                knLogger.debug(methodName, "NO contact member found for the subscriber");
            }

            if (members != null) {
                for (Collection<KnMemberDTO> memberDTOs : members.values()) {
                    for (KnMemberDTO memberDTO : memberDTOs) {
                        KnXDMMdnInfoDTO xdmMdnInfoDTO = new KnXDMMdnInfoDTO();
                        xdmMdnInfoDTO.setMdn(memberDTO.getMemberMdn().trim());
                        xdmMdnInfoDTO.setName(memberDTO.getMemberName().trim());
                        if (subsMap.containsKey(memberDTO.getMemberMdn().trim())) {
                            xdmMdnInfoDTO.setUserId((subsMap.get(memberDTO.getMemberMdn()).getUserId()));
                            xdmMdnInfoDTO.setAliasMdn((subsMap.get(memberDTO.getMemberMdn()).getAliasMdn()));
                            xdmMdnInfoDTO.setUfmi((subsMap.get(memberDTO.getMemberMdn()).getUfmi()));
                        }
                        listOfXdmMdnInfoDTOs.add(xdmMdnInfoDTO);
                        contactMdns.add(memberDTO.getMemberMdn().trim());
                    }
                }
            } else {
                knLogger.debug(methodName, "NO contact member found for the subscriber");
            }
            //Fetch ufmi from DG.XDM_POCCONTACTLIST
            if (subsProfile.getClientMajorVersion() > 11 || ipContactDTO.getClientType() == com.kodiak.common.resources.KnConstants.PTX_XDMDATA_INTF) {
                Map<String, String> ufmiForContactMap = xdmServerDAO.getUfmiForContactMDNs(contactMdns, true, persisterTxn);
                ListIterator<KnXDMMdnInfoDTO> contactMdnIterator = listOfXdmMdnInfoDTOs.listIterator();
                while (contactMdnIterator.hasNext()) {
                    KnXDMMdnInfoDTO dto = contactMdnIterator.next();
                    String contactMdn = dto.getMdn();
                    dto.setUfmi(ufmiForContactMap.get(contactMdn));
                }
            }

            //added for backward compatibility i.e web server in 7.9 and xdm in 7.10
            //generating resource list xml in xdm and setting to response DTO  this will be removed for future releases
            //String xmlDoc = pubInfoUtil.generateResourceListXML(listOfXdmMdnInfoDTOs);
            //result.setStrXml(xmlDoc)

            //Fetching the data from GG if PV is greater than or equal to 19
            if (subsProfile.getClientMajorVersion() >= KnConstants.PROTOCOL_VERSION_19_X) {
                //Call to Grid Gain to fetch the AddonAliasIds value(PBX Contact Type)
                KnPUBContactAddonAliasDTO contactAddonAliasReqDTO = new KnPUBContactAddonAliasDTO();
                contactAddonAliasReqDTO.setOwnerMDN(ownerMdn);
                ArrayList<KnPUBContactAddonAliasDTO> contactAddonAliasDTOS = KnGeneralCacheUtil.getPUBContactAddonAliasInfo(contactAddonAliasReqDTO);
                for (KnXDMMdnInfoDTO dto : listOfXdmMdnInfoDTOs) {
                    String contactMdn = dto.getMdn();
                    //flag to check whether for same contact MDN , UFMI is added or not
                    boolean flag = false;
                    for (KnPUBContactAddonAliasDTO aliasDTO : contactAddonAliasDTOS) {
                        if (aliasDTO.getContactMDN().trim().equals(contactMdn)) {
                            if (dto.getUfmi() != null && !flag) {
                                dto.getAddOnAliasIds().add(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.ONE + DELIM + dto.getUfmi());
                            }
                            dto.getAddOnAliasIds().add(aliasDTO.getKey() + DELIM + aliasDTO.getValue());
                            dto.setContactType(String.valueOf(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.ONE));
                            flag = true;
                        }
                    }
                    //For Pv19 owner MDN , as we are not using ufmi as aliadId, it should be under addonAliadIds.
                    // It is applicable for those contact for which data is not stored in GG
                    if (dto.getAddOnAliasIds().size() == 0 && dto.getUfmi() != null) {
                        dto.getAddOnAliasIds().add(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.ONE + DELIM + dto.getUfmi());
                        dto.setContactType(String.valueOf(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.ONE));
                    }
                    dto.setUfmi(null);
                }
            } else {
                knLogger.info(methodName, "Owner MDN does not have required Protocol Version to perform getContact AddonAliasID operation. so ownerMDN would recieve older valid response");
            }
            knLogger.debug(methodName, "listOfXdmMdnInfoDTOs : ", listOfXdmMdnInfoDTOs);
            result.setContactsList(listOfXdmMdnInfoDTOs);

        } catch (KnAASException | KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : ", ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_LIST_DOES_NOT_EXISTS, "Contact List dosn't exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while getting Contact list: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "fetching contact list", ex);
        }
        return result;

    }


    /**
     * @param ipContactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public Collection<KnPubContactInfoDTO> getAllContactLists(KnIPPubContactDTO ipContactDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getAllContactLists(KnIPPubContactDTO, persisterTxn)";
        Collection<KnPubContactInfoDTO> contactLists = null;
        KnPubContactPersistDTO contactPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactDTO);

        try {

            String ownerMdn = ipContactDTO.getOwner();
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipContactDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
            contactPersistDTO = new KnPubContactPersistDTO();
            contactPersistDTO.setInputDTO(ipContactDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            contactPersistDTO.setPersistenceDTO(originator);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(contactPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            // Populate contact persist dto with input dto
            pubInfoUtil.populateContactPersistDTO(ipContactDTO, contactPersistDTO);

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);


            int clientType = ipContactDTO.getClientType();
            if (com.kodiak.common.resources.KnConstants.CLIENT_TYPE_SOAP == clientType) {

                contactLists = new ArrayList<>();
                Collection<Integer> contactListIds = xdmServerDAO.getContactListIdForMDN(ownerMdn, persisterTxn);
                Map<Integer, Collection<KnMemberDTO>> members = xdmServerDAO.getContactListMemberInfos(contactListIds, false, persisterTxn);
                if (members != null) {
                    for (Collection<KnMemberDTO> memberDTOs : members.values()) {
                        KnPubContactInfoDTO pubContactInfoDTO = new KnPubContactInfoDTO();
                        pubContactInfoDTO.setContactMembers(memberDTOs);
                        contactLists.add(pubContactInfoDTO);
                    }
                } else {
                    knLogger.debug(methodName, "NO contact member found for the subscriber");
                }
            }
            knLogger.debug(methodName, "ContactLists :", contactLists);


        } catch (KnAASException | KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : ", ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_LIST_DOES_NOT_EXISTS,
                        "No Contact List exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while getting Contact list: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "fetching contact list", ex);
        }
        return contactLists;

    }


    /**
     * @param ipContactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOPPubDirResponse getIndexDetails(KnIPPubContactDTO ipContactDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getIndexDetails(KnIPPubContactDTO, persisterTxn)";
        KnOPPubDirResponse dirRespDTO = null;
        KnPubContactPersistDTO contactPersistDTO = null;
        Collection<KnPubGroupDTO> groupList = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactDTO);
        knLogger.debug(methodName, "ENTRY -> Mcpttid : ", KnGDPRTemplate.mcpttId(ipContactDTO.getMcpttId()));

        try {
            String ownerMdn = ipContactDTO.getOwner();
            int dirDocEtag = ipContactDTO.getIfNoneMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipContactDTO.getProfile(), true, true, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
            contactPersistDTO = new KnPubContactPersistDTO();
            contactPersistDTO.setInputDTO(ipContactDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipContactDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            contactPersistDTO.setPersistenceDTO(originator);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(contactPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            // Populate contact persist dto with input dto
            pubInfoUtil.populateContactPersistDTO(ipContactDTO, contactPersistDTO);

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // Fetch current etag for Dir
            int dirEtag = xdmServerDAO.getCurrentEtagForDirDoc(ownerMdn, true, persisterTxn);

            if (dirDocEtag > 0 && dirDocEtag == dirEtag) {
                knLogger.info(methodName, "Directory Document not modified :", dirDocEtag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_NOT_MODIFIED, "Directory not modified");
            }

            List<String> mdnList = new ArrayList<>();
            mdnList.add(ownerMdn);
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmServerId);
            Map<String, String> profileMdnBaseMdnMap = commonXdmDAO.getProfileMdnBaseMdnMap(mdnList, true, persisterTxn);

            // get contactlists from resourcelist doc
            int etag = xdmServerDAO.getResourceListEtagForMdn(ownerMdn, true, persisterTxn);

            int rlsEtag = 1;
            dirRespDTO = new KnOPPubDirResponse();
            KnPubContactDTO contactDTO = new KnPubContactDTO();
            contactDTO.setOwner(ownerMdn);
            contactDTO.setEtag(etag);

            dirRespDTO.setContactListDTO(contactDTO);
            dirRespDTO.setDirEtag(dirEtag);
            dirRespDTO.setXcapRootUri(genInfoUtil.getXCAPRootURI(ownerMdn, persisterTxn));
            dirRespDTO.setSubsUpdateTime(subsProfile.getProfileLastUpdated());
            dirRespDTO.setProtocolVersion(subsProfile.getProtocolVersion());
            dirRespDTO.setActiveFS1(com.kodiak.common.resources.KnGeneralUtil.convertHexStringToLong(subsProfile.getActiveFS2()));
            dirRespDTO.setActiveFS2(subsProfile.getActiveFS2());
            dirRespDTO.setCorpId(subsProfile.getCorpId());
            dirRespDTO.setUserProfileId(subsProfile.getUserProfileId());
            dirRespDTO.setXdmsHome(subsProfile.getXdmsHome());
            dirRespDTO.setRlsEtag(rlsEtag);
            dirRespDTO.setProtocolVersion(subsProfile.getProtocolVersion());
            dirRespDTO.setUserAgent(subsProfile.getUserAgent());
            dirRespDTO.setBaseMdn(!profileMdnBaseMdnMap.isEmpty() && profileMdnBaseMdnMap.get(ownerMdn) != null
                    ? profileMdnBaseMdnMap.get(ownerMdn) : ownerMdn);
            dirRespDTO.setSubscriberFS2(subsProfile.getSubscriberFS2());
            dirRespDTO.setClientType(subsProfile.getClientType());
            // Public Group related
            // get grouplist
            groupList = xdmServerDAO.getAllGroupsForMdn(ownerMdn, true, persisterTxn);
            //for the pulbic only subscriber adding the external groups to the response data is fetched from memberlist table
            if (subsProfile.getPublicSubscriptionType() == 1 && subsProfile.getCorpSubscriptionType() == 0) { //public only
                IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                        createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
                Collection<KnCorpGpInfoDTO> subsGroupList = xdmDAO.getSubscriberGroupList(ownerMdn, true, persisterTxn);
                knLogger.debug(methodName, " Corporate group list ", subsGroupList);
                dirRespDTO.setCorporateGroupList(subsGroupList);
            }
            dirRespDTO.setGroupList(groupList);
            // Public Group related
            dirRespDTO.setPublicSubsType(subsProfile.getPublicSubscriptionType());

            // Authorization Doc
            KnAuthDocDTO authDocDTO = null;
            try {
                authDocDTO = xdmServerDAO.getAuthorizationDocDetails(ownerMdn,true, persisterTxn);
            } catch (Exception e) {
                knLogger.info( methodName, "No Authorization doc Info found");
            }
            dirRespDTO.setAuthDocDTO(authDocDTO);

            //Emergency Doc
            KnEmergencyDocDTO emergencyDocDTO = null;
            try {
                emergencyDocDTO = xdmServerDAO.getEmergencyDocDetails(ownerMdn, true, persisterTxn);
            } catch (Exception e) {
                knLogger.info( methodName, "No Emergency doc Info found");
            }
            dirRespDTO.setEmergencyDocDTO(emergencyDocDTO);

            //Emergency Doc
            KnGroupUsageListDocDTO usageListDocDTO = null;
            try {
                usageListDocDTO = xdmServerDAO.getGroupUsageListDocDetails(ownerMdn, true, persisterTxn);
            } catch (Exception e) {
                knLogger.error(methodName, "No Group usage  doc Info found", e);
            }
            dirRespDTO.setUsageListDocDTO(usageListDocDTO);

            KnTGSSDocDTO tgssDocDTO = null;

            try {
                tgssDocDTO = xdmServerDAO.getTGSSDoc(ownerMdn, true, persisterTxn);
            } catch (Exception e) {
                knLogger.info( methodName, "No TGS doc Info found");
            }
            dirRespDTO.setTgssDocDTO(tgssDocDTO);


        } catch (KnAASException | KnPubBOException aex) {
            knLogger.error(methodName, " Exception occured :", aex);

            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_LIST_DOES_NOT_EXISTS,
                        "No Contact List exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, ":x: ",
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "fetching contact list Index Details", ex);
        }
        return dirRespDTO;

    }

    /**
     * Interface to add/modify/remove subscribers dynamic contacts
     *
     * @param ipContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse modifyDynamicContacts(KnIPPubContactInfoDTO ipContactInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        final String methodName = "modifyDynamicContacts(KnIPPubContactInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        KnDynamicContactPersistDTO dynamicContactPersistDTO = new KnDynamicContactPersistDTO();
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactInfoDTO);

        try {
            /*
            1. Owner MDN must be third party client () and of C&P subscription type and contact can be Corporate OR C&P.
            2. All contact MDN and owner MDN must belong to the request corporation.
            3. All third party client contact MDN and owner MDN must belong to the requested verdor ID.
            4. Max contact limit validation
            5. Modifying contact must exist as contact to the owner MDN.
             */

            dynamicContactPersistDTO.setInputDTO(ipContactInfoDTO);
            String ownerMdn = ipContactInfoDTO.getOwner();
            //Get the profile for ownerMDN.
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn, ipContactInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            knLogger.debug(methodName, "Subscriber Profile - ", subsProfile);
            String xdmServerId = subsProfile.getXdmsHome();


            KnPOCContactListDAO pocContactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPOCContactListDAO(xdmServerId);
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            //Get the third party ID from DG.THIRD_PARTY_ACCOUNT_INFO by THIRD_PARTY_ACCT_ID (request vendorId)
            int tpID = pubInfoUtil.getTPId(ipContactInfoDTO.getVendorId(), persisterTxn);
            knLogger.info(methodName, "tpID - ", tpID);

            //Get the existing contact list
            List<String> mdnList = new ArrayList<>();
            mdnList.add(ownerMdn);
            Map<String, Collection<String>> existingContactsMap = pocContactListDAO.getContactMDNsForMDNs(mdnList, true, persisterTxn);
            knLogger.debug(methodName, "existingContactsMap - ", KnGDPRTemplate.mapKeyValueListMdn(existingContactsMap));
            //Get the subscriber profile from DG.POCSUBSCRINFO table for all added contacts
            List<String> addedMdnList = new ArrayList<>();
            if (ipContactInfoDTO.getMembers() != null) {
                for (KnMemberDTO mem : ipContactInfoDTO.getMembers()) {
                    addedMdnList.add(mem.getMemberMdn());
                }
            }
            List<KnMemberDTO> addedContactDetailList = new ArrayList<>();
            if (!addedMdnList.isEmpty()) {
                addedContactDetailList = xdmServerDAO.getMembersDetails(addedMdnList, persisterTxn);
            }
            knLogger.debug(methodName, "addedContactDetailList - ", addedContactDetailList);

            //Get the list of MDN in the request vendorID from DG.THIRD_PARTY_USER_MDN_MAP table
            int tpClientType = KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value();
            int tpDispatchClientType = KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value();
            int mobileAPIClient = KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value();
            List<String> tpMdnList = new ArrayList<>();
            tpMdnList.add(ownerMdn);
            List<String> extContactList = new ArrayList<>();
            for (KnMemberDTO member : addedContactDetailList) {
                int memberClientType = Integer.parseInt(member.getClientType());
                if ((tpClientType == memberClientType) || (tpDispatchClientType == memberClientType)
                        || (mobileAPIClient == memberClientType)) {
                    tpMdnList.add(member.getMemberMdn());
                }
                if (subsProfile.getCorpId() != member.getCorpId()) {
                    extContactList.add(member.getMemberMdn());
                }
            }
            Map<String, Integer> mdnTPidMap = xdmServerDAO.getMdnTPidMap(tpMdnList, persisterTxn);
            knLogger.debug(methodName, "mdnTPidMap - ", KnGDPRTemplate.mapKeyMdn(mdnTPidMap));

            //Get the valid external contact in the corporation
            List<String> validExtConts = xdmServerDAO.getExtContactList(extContactList, subsProfile.getCorpId(), persisterTxn);


            dynamicContactPersistDTO.setSubsProfileDTO(subsProfile);
            if (existingContactsMap != null) {
                dynamicContactPersistDTO.setExistingContList(new ArrayList<>(existingContactsMap.get(ownerMdn)));
            }
            dynamicContactPersistDTO.setAddedContactDetailList(addedContactDetailList);
            dynamicContactPersistDTO.setTpID(tpID);
            dynamicContactPersistDTO.setMdnTPidMap(mdnTPidMap);
            dynamicContactPersistDTO.setReqExtContactList(extContactList);
            dynamicContactPersistDTO.setValidReqExtContactList(validExtConts);
            dynamicContactPersistDTO.setMaxNumberOfMembers(pubInfoUtil.getXdmsServiceConfig().getMaxPublicContactsPerSubs());
            dynamicContactPersistDTO.setAddedContList(addedMdnList);

            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
            boolean dynamicFlag = false;
            if (null != dynamicBasedFlgValue) {
                dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
            }

            dynamicContactPersistDTO.setDynAPIServFlag(dynamicFlag);
            knLogger.debug(methodName, "Invoking validation - ", dynamicContactPersistDTO);
            validatorFwk.validate(dynamicContactPersistDTO);
            knLogger.debug(methodName, "validation successful ");

            // get contactlist id using ownerMdn from contactlist docmap
            int contactListId = xdmServerDAO.getContactListIdForMDN(ownerMdn, true, persisterTxn);

            // add contact in contact list member
            if (ipContactInfoDTO.getMembers() != null && !ipContactInfoDTO.getMembers().isEmpty()) {
                knLogger.info(methodName, "Adding contacts");
                xdmServerDAO.addContacts(contactListId, ipContactInfoDTO.getMembers(), persisterTxn);

                // add contact in poc contact list
                Collection<String> contactMDNs = new ArrayList<String>();
                for (KnMemberDTO members : ipContactInfoDTO.getMembers()) {
                    contactMDNs.add(members.getMemberMdn());
                }
                // Adding to DG.XDM_POCCONTACTLIST
                xdmServerDAO.addContactsToPOCContactList(ownerMdn, ipContactInfoDTO.getMembers(), persisterTxn);

            }
            //delete all removed contact
            if (ipContactInfoDTO.getRemovedContList() != null && !ipContactInfoDTO.getRemovedContList().isEmpty()) {
                knLogger.info(methodName, "Removing");
                xdmServerDAO.deleteContacts(contactListId, ipContactInfoDTO.getRemovedContList(), persisterTxn);

                //Removing from DG.XDM_POCCONTACTLIST
                xdmServerDAO.deleteContactsFromPOCContactList(ownerMdn, ipContactInfoDTO.getRemovedContList(), persisterTxn);
            }
            //Modify all modified contacts
            if (ipContactInfoDTO.getModifiedContList() != null && !ipContactInfoDTO.getModifiedContList().isEmpty()) {
                knLogger.info(methodName, "Modifying");
                xdmServerDAO.modifyContacts(contactListId, ipContactInfoDTO.getModifiedContList(), persisterTxn);
            }

            //Adding/Updating the public contact count
            int publicContactCount = xdmServerDAO.getPublicContactCount(ownerMdn, persisterTxn);
            if (publicContactCount == -1) {
                //insert
                publicContactCount = addedContactDetailList.size();
                xdmServerDAO.addContactCount(ownerMdn, publicContactCount, persisterTxn);
            } else if (publicContactCount >= 0) {
                //increment
                List<String> contAferRemove = new ArrayList<>();
                if (dynamicContactPersistDTO.getExistingContList() != null) {
                    contAferRemove.addAll(dynamicContactPersistDTO.getExistingContList());
                }
                if (ipContactInfoDTO.getRemovedContList() != null) {
                    contAferRemove.removeAll(ipContactInfoDTO.getRemovedContList());
                }
                int addedCount = 0;
                if (ipContactInfoDTO.getMembers() != null) {
                    addedCount = ipContactInfoDTO.getMembers().size();
                }
                int newCount = addedCount + contAferRemove.size();
                xdmServerDAO.updateContactCount(ownerMdn, newCount, persisterTxn);
            }

            result = pubInfoUtil.populateSuccessResponse();

        } catch (KnPubBOException aex) {
            knLogger.error(methodName, " KnPubBOException occured :", aex);
            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnPubBOValidationException ex) {
            knLogger.error(methodName, "Validation Exception occured :", ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_ALREADY_EXISTS, "Contact already exists");
            } else if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_DOES_NOT_EXISTS, "Contact does not exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while adding Contact: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "adding contact", ex);
        }
        return result;
    }

    /**
     * Interface to delete third party clients' dynamic contacts
     *
     * @param ipContactInfoDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnPubContactDTO deleteDynamicContacts(KnIPPubContactInfoDTO ipContactInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "deleteDynamicContacts(KnIPPubContactInfoDTO, persisterTxn)";
        KnPubContactDTO result = new KnPubContactDTO();
        KnDynamicContactPersistDTO dynamicContactPersistDTO = new KnDynamicContactPersistDTO();
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactInfoDTO);

        try {
            /*
            1. Owner MDN must be third party client () and of C&P subscription type.
            2. Validate the request MDN belongs to the request vendor ID.
             */

            dynamicContactPersistDTO.setInputDTO(ipContactInfoDTO);
            String ownerMdn = ipContactInfoDTO.getOwner();
            //Get the profile for ownerMDN.
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn, ipContactInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            knLogger.debug(methodName, "Subscriber Profile - ", subsProfile);
            String xdmServerId = subsProfile.getXdmsHome();

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            boolean dynamicFlag = false;
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
            if (null != dynamicBasedFlgValue) {
                dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
            }
            dynamicContactPersistDTO.setDynAPIServFlag(dynamicFlag);
            dynamicContactPersistDTO.setSubsProfileDTO(subsProfile);
            validatorFwk.validate(dynamicContactPersistDTO);
            knLogger.debug(methodName, "validation successful ");

            // get contactlist id using ownerMdn from contactlist docmap
            int contactListId = xdmServerDAO.getContactListIdForMDN(ownerMdn, true, persisterTxn);
            knLogger.debug(methodName, "contactListId - ", contactListId);

            knLogger.debug(methodName, "Retrieving -");
            //deleting the public contact count
            Collection<Integer> idList = new ArrayList<>();
            idList.add(contactListId);
            Map<Integer, Collection<KnMemberDTO>> conListMap = xdmServerDAO.getContactListMemberInfos(idList, true, persisterTxn);
            if (conListMap != null && conListMap.get(contactListId) != null) {
                List<KnXDMMdnInfoDTO> contactList = new ArrayList<>();
                for (KnMemberDTO member : conListMap.get(contactListId)) {
                    KnXDMMdnInfoDTO mdnInfo = new KnXDMMdnInfoDTO();
                    mdnInfo.setMdn(member.getMemberMdn());
                    contactList.add(mdnInfo);
                }
                result.setContactsList(contactList);
            }

            knLogger.debug(methodName, "Deleteing contacts in the Relational Tables");
            //deleting the public contact count
            xdmServerDAO.deleteContactCount(ownerMdn, persisterTxn);

            // add contact in contact list member
            xdmServerDAO.deleteAllContacts(ownerMdn, persisterTxn);

        } catch (KnPubBOException aex) {
            knLogger.error(methodName, " KnPubBOException occured :", aex);
            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnPubBOValidationException ex) {
            knLogger.error(methodName, "Validation Exception occured :", ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_ALREADY_EXISTS, "Contact already exists");
            } else if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_DOES_NOT_EXISTS, "Contact does not exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while adding Contact: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "adding contact", ex);
        }
        return result;
    }


    public KnPubContactDTO getDynamicContacts(KnIPPubContactInfoDTO ipContactInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getDynamicContacts(KnIPPubContactInfoDTO, persisterTxn)";
        KnPubContactDTO result = new KnPubContactDTO();
        KnDynamicContactPersistDTO dynamicContactPersistDTO = new KnDynamicContactPersistDTO();
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : ", ipContactInfoDTO);

        try {
            /*
            1. Owner MDN must be third party client () and of C&P subscription type.
            2. Validate the request MDN belongs to the request vendor ID.
             */

            dynamicContactPersistDTO.setInputDTO(ipContactInfoDTO);
            String ownerMdn = ipContactInfoDTO.getOwner();
            //Get the profile for ownerMDN.
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn, ipContactInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            knLogger.debug(methodName, "Subscriber Profile - ", subsProfile);
            String xdmServerId = subsProfile.getXdmsHome();
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            dynamicContactPersistDTO.setSubsProfileDTO(subsProfile);

            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
            boolean dynamicFlag = false;
            if (null != dynamicBasedFlgValue) {
                dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
            }
            dynamicContactPersistDTO.setDynAPIServFlag(dynamicFlag);
            validatorFwk.validate(dynamicContactPersistDTO);
            knLogger.debug(methodName, "validation successful ");

            // get contactlist id using ownerMdn from contactlist docmap
            int contactListId = xdmServerDAO.getContactListIdForMDN(ownerMdn, true, persisterTxn);
            knLogger.debug(methodName, "contactListId - ", contactListId);
            knLogger.debug(methodName, "Retrieving -");
            //deleting the public contact count
            Collection<Integer> idList = new ArrayList<>();
            idList.add(contactListId);
            Map<Integer, Collection<KnMemberDTO>> conListMap = xdmServerDAO.getContactListMemberInfos(idList, true, persisterTxn);
            knLogger.debug(methodName, "conListMap -", conListMap);
            if (conListMap != null) {
                List<KnXDMMdnInfoDTO> respContList = null;
                Collection<KnMemberDTO> contactList = conListMap.get(contactListId);
                if (contactList != null) {
                    respContList = new ArrayList<>();
                    for (KnMemberDTO memberDTO : contactList) {
                        KnXDMMdnInfoDTO member = new KnXDMMdnInfoDTO();
                        member.setMdn(memberDTO.getMemberMdn());
                        member.setName(memberDTO.getMemberName());
                        respContList.add(member);
                    }
                }
                result.setContactsList(respContList);
            }
            result.setOwner(ownerMdn);

        } catch (KnPubBOException aex) {
            knLogger.error(methodName, " KnPubBOException occured :", aex);
            throw new KnXDMServerException(aex.getErrorCode(), aex.getErrorMessage());
        } catch (KnPubBOValidationException ex) {
            knLogger.error(methodName, "Validation Exception occured :", ex);
            throw new KnXDMServerException(ex.getErrorCode(), ex.getErrorMessage());
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : ", ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CONTACT_DOES_NOT_EXISTS, "Contact does not exists");
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while adding Contact: ", ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "adding contact", ex);
        }
        return result;
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
