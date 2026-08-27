/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.IPAMAccController;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCreatePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPDeletePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdatePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMSubsProfInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpGwLinkedAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;


/**
 * *****************************************************************************
 * File name:   KnPAMAccController.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           09/02/2013       7.4
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
 * *******************************************************************************
 */

public class KnPAMAccController implements IPAMAccController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMAccController.class);

    private KnProvInfoUtil provInfoUtil;
    private KnValidatorFramework validatorFwk;
    private KnFeatureSetUtil featureSetUtil= null;
    private KnGenInfoUtil genInfoUtil;
    private String xdmPttServerId;
    public KnPAMAccController() {
        knLogger.info("Constructor", "in PAM Account controller");
        this.provInfoUtil = new KnProvInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        this.featureSetUtil=KnFeatureSetUtil.getInstance();
        this.validatorFwk = KnValidatorFramework.getInstance(KnProvConstants.LIBRARY_NAME);
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (KnBOException e) {
            knLogger.error("Constructor", "failed to retrieve xdm Ptt Sever Id");
            knLogger.error("Constructor", e);
        }
    }

    /**
     * This method will create the PAM Account for the received Input Parameters
     * This method will perform the following activities
     * -> Authenticate the User.
     * -> Validate the received input DTO data.
     * -> populate the subscriber info persistdat DTO's
     * -> Invoke the DAO Layer for DB Population
     *
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @return KnOPCreatePAMAccountDTO Object
     */
    public KnOPCreatePAMAccountDTO createPAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        final String methodName = "createPAMAccount(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        KnPAMAccPersistDTO pamAccPersistDTO;
        KnOPCreatePAMAccountDTO respDTO = null;
        knLogger.entry(methodName, pamAccInfoDTO);

        try {

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnOPPAMAccInfoDTO existingPAMAccInfo = null;
            try {
                existingPAMAccInfo = provXDMServerDAO.retrievePAMAccInfo(pamAccInfoDTO.getExtPamAccId(), persisterTxn);
            } catch (KnDAOException e) {
                knLogger.debug(methodName, "PAM Account not found   ", e);
            }
            if (existingPAMAccInfo != null) {
            	throw new KnDAOException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS, "PAM accoutn already exist!!");
            }

            // Validate requested Billing MDN is present PocSubscriInfo table as well as Pseudo pool table
            this.validateCreatePAMAccount(pamAccInfoDTO.getExtPamAccId(), persisterTxn);

            int maxSubs = pamAccInfoDTO.getTotalNoOfLines();
            String extPamAccId = pamAccInfoDTO.getExtPamAccId();
            int maxSubsPerAcc = this.provInfoUtil.retrievePAMSvcConfig(persisterTxn).getMaxSubPerAccount();
            if (maxSubs > maxSubsPerAcc) {
                knLogger.error(methodName, "Max subscriber per account limit reached !!! ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_PAMACCOUNT, "Max Subscriber per Account limit reached ");
            }

            //populate the Subscriber Prov Persist DTO.
            pamAccPersistDTO = new KnPAMAccPersistDTO();

            pamAccPersistDTO.setInputDTO(pamAccInfoDTO);
            pamAccPersistDTO.setExtPamAccId(extPamAccId);
            pamAccPersistDTO.setBillingName(pamAccInfoDTO.getBillingName());
            pamAccPersistDTO.setPamAccState(KnProvConstants.PAM_ACCOUNT_STATE.ACTIVE.value());
            // this is to set serviceauthstatus to suspend/resume(2/3) while creating PAM account in suspend/resume operation.(MDN does not exist case)
            int serviceAuthStatus = pamAccInfoDTO.getProfileDetails().getServiceAuthStatus();
            if (serviceAuthStatus != 0) {
            	pamAccPersistDTO.setPamAccState(pamAccInfoDTO.getProfileDetails().getServiceAuthStatus());
            }

            // check for NNI subsciber type 7/8 and set serviceauth status 2(Activated)
            int clientType = pamAccInfoDTO.getProfileDetails().getClient_Type();
            if (clientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() || clientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
            	// incase of suspend operation we are not setting ACTIVE state  to NNI subscribers
            	if (serviceAuthStatus != KnProvConstants.PAM_ACCOUNT_STATE.SUSPEND.value()) {
            		pamAccPersistDTO.setPamAccState(KnProvConstants.PAM_ACCOUNT_STATE.ACTIVE.value());
            	}
            }

            pamAccPersistDTO.setBillingMdn(pamAccInfoDTO.getBillingMdn());
            pamAccPersistDTO.setTotalNoOfLines(maxSubs);
            pamAccPersistDTO.setSubscriberCount(pamAccInfoDTO.getSubscriberCount());
            pamAccPersistDTO.setProfileDetails(pamAccInfoDTO.getProfileDetails());
            pamAccPersistDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());
            /* todo
            knLogger.debug(methodName, "PAM Account Persist DTO - ", pamAccPersistDTO);
            Validate the Persist DTO
            this.validatorFwk.validate(pamAccPersistDTO);
            knLogger.debug(methodName, "Successfully validated the data");*/
            /*//validate the subscription Types.
            int corporateSubscriptionType= pamAccInfoDTO.getProfileDetails().getCorpSubsType();
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                knLogger.error(methodName, "Corporate Subscription Types are required to be enabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Subscription types passed");
            }*/


            int pamAccId = provXDMServerDAO.createPAMAccountInfo(pamAccPersistDTO, persisterTxn);

            respDTO = new KnOPCreatePAMAccountDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_PAMACCOUNT_SUCCESS);
            respDTO.setPamAccId(pamAccId);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);

            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_ALREADY_EXISTS, "PAM account already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, respDTO);
        return respDTO;
    }

    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
    	final String methodName = "retrieveCorporationId(String, KnPersisterTxn)";
    	knLogger.entry(methodName, extCorpId);
    	int corpID = -1;
    	 try {
			IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
			corpID = provXDMServerDAO.retrieveCorporationId(extCorpId, persisterTxn);
			knLogger.debug(methodName, "after calling retrieveCorporationId method", corpID);
		}  catch (KnDAOException ex) {
			 knLogger.error(methodName, "DAO Exception occurred : ", ex);
			  throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
		}

    	 knLogger.exit(methodName, corpID);
    	 return corpID;
    }

    public KnOPUpdatePAMAccountDTO migratePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
    	 final String methodName = "migratePAMAccount(KnIPPAMAccInfoDTO, KnPersisterTxn)";
         KnOPUpdatePAMAccountDTO responseDTO;
         KnPAMAccPersistDTO pamAccPersistDTO;
         knLogger.entry(methodName, pamAccInfoDTO);

         try {

        	 String oldBillingMDN = trim(pamAccInfoDTO.getOldBillingNumber());
        	 String newBillingMDN = trim(pamAccInfoDTO.getBillingMdn());

        	 validateMigratePAMAccout(oldBillingMDN, newBillingMDN);
        	 
        	 validateCreatePAMAccount(newBillingMDN, persisterTxn);

             IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
             KnOPPAMAccInfoDTO oppamAccInfoDTO = null;
             try {
                 oppamAccInfoDTO = provXDMServerDAO.retrievePAMAccInfo(newBillingMDN, persisterTxn);
             } catch (KnDAOException ex) {
                 knLogger.debug(methodName, "new external pam account id does not exists", ex);
             }
             if (oppamAccInfoDTO != null && oppamAccInfoDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.SUCCESS.value()) {
                 knLogger.error(methodName, "New ext pam account id [", KnGDPRTemplate.mdn(newBillingMDN), "] already exists ");
                 throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_ALREADY_EXISTS, "New external Pam account id already exists - " + newBillingMDN);
             }
             //updating current pam account id to new pam account id.

             pamAccPersistDTO = new KnPAMAccPersistDTO();
             pamAccPersistDTO.setPamAccId(pamAccInfoDTO.getPamAccId());
             //billing MDN and ext pam account id will be always same
             pamAccPersistDTO.setExtPamAccId(newBillingMDN);
             pamAccPersistDTO.setBillingMdn(newBillingMDN);

             knLogger.debug(methodName, "PAM Account Persist DTO - ", pamAccPersistDTO);
             provXDMServerDAO.updatePAMBillingMDN(pamAccPersistDTO, persisterTxn);

             int newPamAccID = provXDMServerDAO.retrievePAMAccId(newBillingMDN, persisterTxn);

             //populate the response DTO
             responseDTO = new KnOPUpdatePAMAccountDTO();
             responseDTO.setPamAccId(newPamAccID);
             responseDTO.setPamAccState(pamAccInfoDTO.getPamAccState());
             responseDTO.setResponseMessage(KnProvConstants.UPDATE_PAMACCOUNT_SUCCESS);
             responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

         } catch (KnDBConnectionException ex) {
             knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
             throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

         } catch (KnDAOException ex) {
             knLogger.error(methodName, "DAO Exception occurred : ", ex);
             if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                 throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_NOT_FOUND, "PAM Account does not exists");
             }
             throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
         }

         knLogger.exit(methodName,  responseDTO);
         return responseDTO;
    }

    private void validateMigratePAMAccout(String oldBillingMDN, String newBillingMDN) throws KnProvBOException {
    	final String methodName = "validateMigratePAMAccout(String, String)";
    	knLogger.entry(methodName, KnGDPRTemplate.mdn(oldBillingMDN), KnGDPRTemplate.mdn(newBillingMDN));

    	if (newBillingMDN == null || newBillingMDN.equals(oldBillingMDN)) {
    		 knLogger.error(methodName, "new billing MDN is null or Both are same");
             throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHG_IN_PROFILE, "No change in profile observed");
    	}

    	knLogger.exit(methodName);
    }

    private void validateCreatePAMAccount(String billingMDN, KnPersisterTxn persisterTxn) throws KnProvBOException, KnDAOException {
    	final String methodName = "validateCreatePAMAccount(String, KnPersisterTxn)";
    	knLogger.debug(methodName, "validating Billind MDN in PSEUDO POOL table and POCSUBSINFO table", KnGDPRTemplate.mdn(billingMDN));
    	IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

    	boolean isExistInPocSubsTable = provXDMServerDAO.isMdnExistInPocSubsInfo(billingMDN, persisterTxn);
    	if (isExistInPocSubsTable) {
    		knLogger.error(methodName, "MDN is present as part of PocSubsInfo table");
    		  throw new KnProvBOException(KnErrorCodes.Validator.INVALID_REQUEST, "Requested MDN is already present in PocSubsInfo table");
    	}
    	KnOPSubsProfileInfoDTO subsProfile = null;
		try {
			subsProfile = provXDMServerDAO.selectSubscriberProfileByAliasMdn(billingMDN, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName, e);
		}
		if (subsProfile != null) {
			knLogger.error(methodName, "Mdn is present as aliasMdn");
			throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_ALIASMDN,
					"Mdn is present as aliasMdn");
		}
    }

    public void validateUpgradePAMAccount(int reqMaxSub, KnPersisterTxn persisterTxn) throws KnProvBOException {
    	String methodName = "validateUpgradePAMAccount(int)";
    	int maxSubsPerAcc = this.provInfoUtil.retrievePAMSvcConfig(persisterTxn).getMaxSubPerAccount();
    	if (reqMaxSub > maxSubsPerAcc) {
			knLogger.error(methodName, "Max subscriber per account limit reached !!! ");
            throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_PAMACCOUNT, "Max Subscriber per Account limit reached ");
		}
    }

    /**
     * method to delete the Subscriber Profile
     *
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *          BO Entity Exception
     */
    public KnOPDeletePAMAccountDTO deletePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "deletePAMAccount(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        KnPAMAccPersistDTO pamAccPersistDTO = new KnPAMAccPersistDTO();
        KnOPDeletePAMAccountDTO responseDTO;
        knLogger.entry(methodName, pamAccInfoDTO);

        try {

            String extPamAccId = pamAccInfoDTO.getBillingMdn();
            pamAccPersistDTO.setBillingMdn(extPamAccId);


            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //delete the subscriber profile
            provXDMServerDAO.deletePAMAccountInfo(pamAccPersistDTO, persisterTxn);

            //populate the response DTO
            responseDTO = new KnOPDeletePAMAccountDTO();

            responseDTO.setResponseMessage(KnProvConstants.DELETE_PAMACCOUNT_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_NOT_FOUND, "PAM account does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);

        }
        knLogger.exit(methodName, responseDTO);
        return responseDTO;

    }


    /**
     * This method will create the Subscriber Profile for the received Input Parameters
     * This method will perform the following activities
     * -> Authenticate the User.
     * -> Validate the received input DTO data.
     * -> populate the subscriber info persistdat DTO's
     * -> Invoke the DAO Layer for DB Population
     *
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @return KnOPCreatePAMAccountDTO Object
     */
    public KnOPProvDTO createPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        final String methodName = "createPAMSubsProfile(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        KnPAMAccPersistDTO pamAccPersistDTO;
        KnOPProvDTO respDTO = null;
        knLogger.entry(methodName," KnIPPAMAccInfoDTO pamAccInfoDTO :", pamAccInfoDTO);

        try {

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //populate the PAM Subscriber Profile Persist DTO.
            pamAccPersistDTO = new KnPAMAccPersistDTO();
            pamAccPersistDTO.setProfileDetails(pamAccInfoDTO.getProfileDetails());

            knLogger.debug(methodName, "PAM Account Persist DTO - ", pamAccPersistDTO);

            pamAccPersistDTO.getProfileDetails().setPamAccId(pamAccInfoDTO.getPamAccId());
            pamAccPersistDTO = this.provInfoUtil.populateDefaultProfile(pamAccPersistDTO,persisterTxn);
            provXDMServerDAO.createPAMSubsProfile(pamAccPersistDTO, persisterTxn);
            KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccPersistDTO.getProfileDetails();
            // add addon pkgs
            if(pamSubsProfInfoDTO.getAddOnPkgId()!=null && !pamSubsProfInfoDTO.getAddOnPkgId().isEmpty())
            {
            	provXDMServerDAO.createPAMSubAddOnPkgs(pamSubsProfInfoDTO.getPamAccId(), new ArrayList<>(pamSubsProfInfoDTO.getAddOnPkgId()), persisterTxn);
            }
            respDTO = new KnOPCreatePAMAccountDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_PAMSUBSPROF_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_ALREADY_EXISTS, "PAM Subs Profile already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }
        knLogger.exit(methodName, respDTO);
        return respDTO;
    }

    /**
     * This method will create the Subscriber Profile for the received Input Parameters
     * This method will perform the following activities
     * -> Authenticate the User.
     * -> Validate the received input DTO data.
     * -> populate the subscriber info persistdat DTO's
     * -> Invoke the DAO Layer for DB Population
     *
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @return KnOPCreatePAMAccountDTO Object
     */
    public KnOPProvDTO updatePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        final String methodName = "updatePAMSubsProfile(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        KnPAMAccPersistDTO pamAccPersistDTO;
        KnOPUpdatePAMAccountDTO respDTO = null;
        knLogger.entry(methodName, pamAccInfoDTO);

        Map<Integer,Integer> provFSMap= null;
        Map<Integer,Integer> existingProvFSMap= null;
        BitSet bitset = new BitSet();

        try {
            int pamAccId = pamAccInfoDTO.getPamAccId();
            boolean isPkgCodeChanged=false;
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            provFSMap = pamAccInfoDTO.getProfileDetails().getProvFSMap();
            knLogger.debug(methodName," provFSMap from profile :" ,provFSMap);
           
            Map<String, Map<String, Integer>>  pkgIdMap=pamAccInfoDTO.getProfileDetails().getPkgIdMap();
            
			KnPAMSubsProfInfoDTO existingSubsProf = provXDMServerDAO.getPAMSubsProfInfo(pamAccId, persisterTxn);
			List<String> addonPkgList = provXDMServerDAO.selectPAMSubAddOnPkgs(pamAccId, persisterTxn);
			existingSubsProf.setAddOnPkgId(addonPkgList);

			String existingTierPkg = existingSubsProf.getTierPkgCode();
			List<String> existingAddonPkgs = existingSubsProf.getAddOnPkgId();
			Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
			if (existingTierPkg != null)
				exsitingPkgIds.put(existingTierPkg, com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE);

			if (existingAddonPkgs != null) {
				for (String pkgCode : existingAddonPkgs) {
					exsitingPkgIds.put(pkgCode, com.kodiak.xdms.server.common.resources.KnConstants.ADDON_PKG_TYPE);
				}
			}
			Map<String, Integer> exsitingSubsPkgs = new HashMap<String, Integer>(exsitingPkgIds);
			knLogger.debug(methodName, " exsitingSubsPkgs " + exsitingSubsPkgs);
			Map<String, Integer> finalPkgIdsMap = exsitingPkgIds;
			knLogger.info(methodName, "PkgIdsMap ", pkgIdMap + " exsitingPkgIds " + exsitingPkgIds);

			if (pkgIdMap != null) {
				List<String> qppPkgCodes=featureSetUtil.getQPPPkgCodes(xdmPttServerId);
				if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION) != null
						&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION).isEmpty()) {
					if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
							&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION)
									.isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						boolean status = false;
						Map<String, Integer> removePkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION);
						for (String pkgCode : removePkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != removePkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
							
							finalPkgIdsMap.remove(pkgCode.trim(), removePkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							status=true;
						}

						Map<String, Integer> addPkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);
						
						for (String pkgCode : addPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
										.collect(Collectors.toList());
								if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
								{
									throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
											"qpp pkg already assign!!");
								}
								if (exsitingPkgIds.containsValue(
										com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue())
										&& addPkgIds.get(pkgCode)
												.intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue()) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}
								
								finalPkgIdsMap.put(pkgCode.trim(), addPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != addPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}

						if (status)
							isPkgCodeChanged = true;
					} else {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);
						boolean status = false;
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
										.collect(Collectors.toList());
								if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
								{
									throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
											"qpp pkg already assign!!");
								}
								if (exsitingPkgIds.containsValue(
										com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue())
										&& newPkgIds.get(pkgCode)
												.intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue()) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}
								
								finalPkgIdsMap.put(pkgCode.trim(), newPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != newPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}

						if (status)
							isPkgCodeChanged = true;
					}
				} else {
					if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
							&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION)
									.isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION);
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != newPkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
							
							finalPkgIdsMap.remove(pkgCode.trim(), newPkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							isPkgCodeChanged = true;
						}

						
					}
				}

			}

			int newSubsClientType = existingSubsProf.getClient_Type();
			int publicSubscriptionType = existingSubsProf.getPubSubsType();
			int corpSubscriptionType = existingSubsProf.getCorpSubsType();
			String newSubsFS2 = null;

			if (isPkgCodeChanged) {

				Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
				BitSet finalFSBitSet = new BitSet(Long.SIZE);
				
				String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
				String basePkgFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(publicSubscriptionType,
						corpSubscriptionType, newSubsClientType, basePkgCode, xdmPttServerId);
				knLogger.debug(methodName, "base pkg is applied - ", basePkgFS);
				BitSet basePkgBiSet = featureSetUtil.convertHexStringToBitSet(basePkgFS);
				finalFSBitSet.or(basePkgBiSet);
				String PkgCodeFS  = featureSetUtil.getDefSubsFeatureSetForPkgCodes(publicSubscriptionType,
						corpSubscriptionType, newSubsClientType, finalPkgIdsMap, xdmPttServerId);
				knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", PkgCodeFS);
				BitSet PkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(PkgCodeFS);
				finalFSBitSet.or(PkgCodeBiSet);
				int oldIntropBitStatus=findInterOPBitStatus(existingSubsProf, exsitingSubsPkgs, persisterTxn);

				if (finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_DISABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.clear(LMR_BIT);
				} else if (!finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_ENABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.set(LMR_BIT);
				}
				newSubsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);

			}
            String newSubsFS=null;
			if (isPkgCodeChanged) {
				newSubsFS = newSubsFS2;
			} else {
				newSubsFS = existingSubsProf.getSubscriberFS2();
			}
            knLogger.debug(methodName, "newSubsFS :",newSubsFS);
            //get the external corpID  by passing internal corpID
            existingSubsProf.setExtCorpId(provXDMServerDAO.retrieveExtCorporationId(existingSubsProf.getCorpID(), persisterTxn));
            pamSubProfileValidation(pamAccInfoDTO.getProfileDetails(), existingSubsProf);

                //populate the Subscriber Prov Persist DTO.
                pamAccPersistDTO = populatePersistSubsProfDetails(existingSubsProf);

            knLogger.debug(methodName, "Before Iterating provFSMap :", provFSMap);

            if(provFSMap != null) {
                bitset= featureSetUtil.convertHexStringToBitSet(newSubsFS);
            for(Map.Entry<Integer,Integer> entry: provFSMap.entrySet()){
                if(entry.getValue()== com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()){
                    bitset.set(entry.getKey());
                }else{
                    bitset.clear(entry.getKey());
                }
            }
                newSubsFS=featureSetUtil.convertBitSetToHexString(bitset);
            }
            knLogger.debug(methodName,"  bitset after clear/set ",bitset);
            String provFS2= featureSetUtil.convertBitSetToHexString(bitset);
            knLogger.debug(methodName,"  provFS2 ",provFS2);
            //reinitializing the PROVFS1 and provFSBitMask
            String provFS2BitMask= provFS2;
            
            String subsFS2 = featureSetUtil.generateSubsFeatureSet(newSubsFS, provFS2, provFS2BitMask);

            knLogger.debug(methodName, "changed subscriberfs is : ", subsFS2);
            pamAccPersistDTO.getProfileDetails().setSubscriberFS2(subsFS2);
            pamAccPersistDTO.setExtPamAccId(pamAccInfoDTO.getExtPamAccId());
            pamAccPersistDTO.getProfileDetails().setEmail(pamAccInfoDTO.getProfileDetails().getEmail());
            pamAccPersistDTO.getProfileDetails().setImei(pamAccInfoDTO.getProfileDetails().getImei());
            pamAccPersistDTO.getProfileDetails().setClient_Type(newSubsClientType);
            knLogger.debug(methodName, "PAM Account Persist DTO - ", pamAccPersistDTO);
            
               
			String tierPackageId = null;
			Map<String, Integer> addonPackageIds = new HashMap<>();
			for (Entry<String, Integer> entry : finalPkgIdsMap.entrySet()) {
				if (entry.getValue().intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
					tierPackageId = entry.getKey();
				} else if (entry.getValue() == KnConstants.ADDON_PKG_TYPE.intValue()) {
					addonPackageIds.put(entry.getKey(), entry.getValue());
				}
			}

			Integer qppPkgId = null;
			Integer profileId = null;
			Integer dataPkgId = null;
			if (!addonPackageIds.isEmpty()) {
				for (String addonPkgCode : addonPackageIds.keySet()) {
					profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
					if (profileId != null) {
						qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn).get(profileId);
						if (qppPkgId != null) {
							dataPkgId = qppPkgId;
							break;
						} else if (dataPkgId == null) {
							dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
									.get(profileId);
						}

					}
				}

			}
			if (qppPkgId == null) {
				qppPkgId = KnConstants.DEFAULT_QPP_ID;
			}
			if (dataPkgId == null) {
				dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
			}
			if (profileId == null) {
				profileId = KnConstants.DEFAULT_PROFILE_ID;
			}
			
			if (isPkgCodeChanged) {
				if(tierPackageId==null) {
					tierPackageId="";
				}
				knLogger.debug(methodName, "pkg code change with no tier pkg - ", tierPackageId);
				knLogger.debug(methodName, "deletePAMSubAddlOnPkgs - ", pamAccId);
				provXDMServerDAO.deletePAMSubAddlOnPkgs(pamAccId, persisterTxn);
				if (!addonPackageIds.isEmpty()) {
					provXDMServerDAO.createPAMSubAddOnPkgs(pamAccId, new ArrayList<>(addonPackageIds.keySet()),
							persisterTxn);
				}
			}
			
			pamAccPersistDTO.getProfileDetails().setTierPkgCode(tierPackageId);
			pamAccPersistDTO.getProfileDetails().setDataPkgId(dataPkgId);
			pamAccPersistDTO.getProfileDetails().setFirstNetIndicator(pamAccInfoDTO.getProfileDetails().getFirstNetIndicator());
			provXDMServerDAO.updatePAMSubsProfile(pamAccPersistDTO, persisterTxn);

            respDTO = new KnOPUpdatePAMAccountDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.UPDATE_PAMSUBSPROF_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException |KnFeatureSetException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_ALREADY_EXISTS, "PAM account already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnBOException ex) {
        	throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
		}
        knLogger.exit(methodName,"update Pam Profie ", respDTO);
        return respDTO;
    }

    public KnOPUpdatePAMAccountDTO validateUpdatePamAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        final String methodName = "validateUpdatePamAccount(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName, pamAccInfoDTO);
        KnOPUpdatePAMAccountDTO knOPUpdatePAMAccountDTO= new KnOPUpdatePAMAccountDTO();

        Map<Integer,Integer> provFSMap= null;
        Map<Integer,Integer> existingProvFSMap= null;
        Integer isprovFSEnabled= com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.DISABLED.value();

        try {
            KnPAMSubsProfInfoDTO pamSubsProfInfoDTO= pamAccInfoDTO.getProfileDetails();
            provFSMap = pamSubsProfInfoDTO.getProvFSMap();
            String firstNetIndicator=pamSubsProfInfoDTO.getFirstNetIndicator();
            
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnPAMSubsProfInfoDTO existingSubsProf = provXDMServerDAO.getPAMSubsProfInfo(pamAccInfoDTO.getPamAccId(), persisterTxn);
            List<String> addonPkgList =provXDMServerDAO.selectPAMSubAddOnPkgs(pamAccInfoDTO.getPamAccId(), persisterTxn);
            existingSubsProf.setAddOnPkgId(addonPkgList);
            //get the external corpID  by passing internal corpID
            existingSubsProf.setExtCorpId(provXDMServerDAO.retrieveExtCorporationId(existingSubsProf.getCorpID(), persisterTxn));
            pamSubProfileValidation(pamAccInfoDTO.getProfileDetails(), existingSubsProf);

            String existingSubsFs=existingSubsProf.getSubscriberFS2();
            knLogger.debug(methodName," existingSubsFs :" ,existingSubsFs);
            
            if ((firstNetIndicator != null) && !firstNetIndicator.trim().equalsIgnoreCase(existingSubsProf.getFirstNetIndicator())) {
  			  knLogger.debug(methodName, "Subscriber firstNetIndicator change observed");
  			  knOPUpdatePAMAccountDTO.setSubsFSUpdated(true);

  		  }
			Map<String, Map<String, Integer>> pkgIdMap = pamSubsProfInfoDTO.getPkgIdMap();
			String existingTierPkg = existingSubsProf.getTierPkgCode();
			List<String> existingAddonPkgs = existingSubsProf.getAddOnPkgId();
			Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
			Map<String, Integer> finalPkgIdsMap = new HashMap<String, Integer>();
			if (existingTierPkg != null)
				exsitingPkgIds.put(existingTierPkg, com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE);

			if (existingAddonPkgs != null) {
				for (String pkgCode : existingAddonPkgs) {
					exsitingPkgIds.put(pkgCode, com.kodiak.xdms.server.common.resources.KnConstants.ADDON_PKG_TYPE);
				}
			}
			knLogger.info(methodName, "PkgIdsMap ", pkgIdMap + " exsitingPkgIds " + exsitingPkgIds);

			if (pkgIdMap != null) {
				if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION) != null
						&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION).isEmpty()) {
					if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
							&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION)
									.isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						boolean status = false;
						Map<String, Integer> removePkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION);
						for (String pkgCode : removePkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != removePkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
							
							finalPkgIdsMap.remove(pkgCode.trim(), removePkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							status = true;
						}

						Map<String, Integer> addPkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);

						for (String pkgCode : addPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								if (exsitingPkgIds.containsValue(
										com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue())
										&& addPkgIds.get(pkgCode)
												.intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue()) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}
								
								finalPkgIdsMap.put(pkgCode.trim(), addPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != addPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}

						if (status)
							knOPUpdatePAMAccountDTO.setSubsFSUpdated(true);
					} else {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);
						boolean status = false;
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								if (exsitingPkgIds.containsValue(
										com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue())
										&& newPkgIds.get(pkgCode)
												.intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue()) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}
								
								finalPkgIdsMap.put(pkgCode.trim(), newPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != newPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}

						if (status)
							knOPUpdatePAMAccountDTO.setSubsFSUpdated(true);
					}
				} else {
					if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
							&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION)
									.isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = pkgIdMap
								.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION);
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != newPkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
							
							finalPkgIdsMap.remove(pkgCode.trim(), newPkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							knOPUpdatePAMAccountDTO.setSubsFSUpdated(true);
						}

					}
				}
			}
            
            
            // if feature bit is allowed then we will do ther validation for is updated or not
            if(isProvFSBitAllowed(provFSMap)){
            List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> availableProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
                for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT availableProvFS : availableProvFSList)
                {
                    boolean featureIsEnabled=KnGeneralUtil.getFeatureBitValue(existingSubsFs,availableProvFS.value());
                    if (featureIsEnabled ) {
                        // if feature be is true then set it enabled
                        isprovFSEnabled= com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value();
                        knLogger.debug( methodName, " featureIsEnabled :", featureIsEnabled);
                    }
                    if(provFSMap !=null ){
                        for (Integer provFsKey: provFSMap.keySet()) {
                            // if not equals to 1 that is enabled then
                            if (! provFSMap.get(provFsKey).equals(isprovFSEnabled)) {
                                //if both are not equal then  mark it for update call
                                knOPUpdatePAMAccountDTO.setSubsFSUpdated(true);
                                break;
                            }
                        }
                    }
                }
            }else {
                   knLogger.error(methodName, "Operation not allowed for feature bit(s)!");
                   throw new KnProvBOException(KnErrorCodes.Validator.INVALID_PROV_FS, "Requested feature bit(s) is/are not allowed to enable/disable via create/update.");
                }
            knLogger.debug(methodName," knOPUpdatePAMAccountDTO :" ,knOPUpdatePAMAccountDTO);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }
        knLogger.exit(methodName);
        return  knOPUpdatePAMAccountDTO;
    }


    /**
     * This method will create the Subscriber Profile for the received Input Parameters
     * This method will perform the following activities
     * -> Authenticate the User.
     * -> Validate the received input DTO data.
     * -> populate the subscriber info persistdat DTO's
     * -> Invoke the DAO Layer for DB Population
     *
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @return KnOPCreatePAMAccountDTO Object
     */
    public KnOPProvDTO deletePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        final String methodName = "deletePAMSubsProfile(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        KnPAMAccPersistDTO pamAccPersistDTO;
        KnOPCreatePAMAccountDTO respDTO = null;
        knLogger.entry(methodName, pamAccInfoDTO);

        try {
            pamAccPersistDTO = new KnPAMAccPersistDTO();

            pamAccPersistDTO.setProfileDetails(pamAccInfoDTO.getProfileDetails());

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            provXDMServerDAO.deletePAMSubsProfile(pamAccPersistDTO, persisterTxn);
            knLogger.debug(methodName, "Deleting PAM addon package list", pamAccPersistDTO.getProfileDetails().getPamAccId());
        	provXDMServerDAO.deletePAMSubAddlOnPkgs(pamAccPersistDTO.getProfileDetails().getPamAccId(), persisterTxn);
        	knLogger.debug(methodName, "PAM  addon package list is deleted..");
            respDTO = new KnOPCreatePAMAccountDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.DELETE_PAMSUBSPROF_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_ALREADY_EXISTS, "PAM account already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }
        knLogger.exit(methodName, respDTO);
        return respDTO;
    }


    /**
     * method to retrieve the Subscriber Details
     *
     * @param pamAccId     int
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPPAMAccInfoDTO getPAMAccInfoFromId(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "getPAMAccInfoFromId(int, boolean, KnPersisterTxn)";
        KnOPPAMAccInfoDTO respDTO;

        knLogger.entry(methodName, pamAccId);
        try {

            //retrieving the Subscriber Info
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrievePAMAccInfoFromId(pamAccId, readOnly, persisterTxn);


            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.GET_PAMACCOUNT_INFO_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "PAM Account doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,"DAO exception occurred", ex);
        }
        knLogger.exit(methodName, respDTO);
        return respDTO;
    }

    /**
     * method to retrieve the Subscriber Details
     *
     * @param extPAMAccId  String
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPPAMAccInfoDTO getPAMAccountInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "getPAMAccountInfo(String, KnPersisterTxn)";
        KnOPPAMAccInfoDTO respDTO;
        knLogger.entry(methodName, extPAMAccId);
        try {

            //retrieving the Subscriber Info
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrievePAMAccInfo(extPAMAccId, persisterTxn);


            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.GET_PAMACCOUNT_INFO_SUCCESS);

        } catch (KnDBConnectionException ex) {

            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "PAM Account doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, respDTO);
        return respDTO;
    }

    /**
     * method to retrieve the Subscriber Details
     *
     * @param pamAccInfoDTO int
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPPAMAccInfoDTO getPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "getPAMSubsProfile(int, KnPersisterTxn)";
        KnOPPAMAccInfoDTO respDTO = new KnOPPAMAccInfoDTO();

        knLogger.entry(methodName, pamAccInfoDTO);
        try {

            String extPamAccId = pamAccInfoDTO.getBillingMdn();
            knLogger.info(methodName, "extPamAccId :", extPamAccId, " extPamAccId len :", extPamAccId.length());

            //retrieving the Subscriber Info
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            int pamAccId = provXDMServerDAO.retrievePAMAccId(extPamAccId, persisterTxn);

            KnPAMSubsProfInfoDTO subsProfInfoDTO = provXDMServerDAO.getPAMSubsProfInfo(pamAccId, persisterTxn);
            List<String> addonPkgList = provXDMServerDAO.selectPAMSubAddOnPkgs(pamAccId, persisterTxn);
            subsProfInfoDTO.setAddOnPkgId(addonPkgList);

			String existingTierPkg = subsProfInfoDTO.getTierPkgCode();
			List<String> existingAddonPkgs = subsProfInfoDTO.getAddOnPkgId();
			Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
			if (existingTierPkg != null)
				exsitingPkgIds.put(existingTierPkg, com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE);

			if (existingAddonPkgs != null) {
				for (String pkgCode : existingAddonPkgs) {
					exsitingPkgIds.put(pkgCode, com.kodiak.xdms.server.common.resources.KnConstants.ADDON_PKG_TYPE);
				}
			}
			Map<String, Map<String, Integer>> existingPkgIdMap= new HashMap<>();
			existingPkgIdMap.put(KnConstants.ADD_ACTION, exsitingPkgIds);
			subsProfInfoDTO.setPkgIdMap(existingPkgIdMap);
           //get the external corpID  by passing internal corpID
            String extCorpID = provXDMServerDAO.retrieveExtCorporationId(subsProfInfoDTO.getCorpID(), persisterTxn);
            // get corp profile
            KnOPCorpProfileInfoDTO corpProfile = provXDMServerDAO.retrieveCorporateProfile(extCorpID, persisterTxn);
            subsProfInfoDTO.setExtCorpId(extCorpID);
            subsProfInfoDTO.setCorpName(corpProfile.getCorporateName());

            respDTO.setProfileDetails(subsProfInfoDTO);
            respDTO.setPamAccId(pamAccId);
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.GET_PAMSUBS_INFO_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "PAM Subs profile doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }
        knLogger.exit(methodName, respDTO);
        return respDTO;
    }

    /**
     * method to retrieve the Subscriber Details
     *
     * @param pamAccInfoDTO int
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPPAMAccInfoDTO retrievePAMSubsProfInfo(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "retrievePAMSubsProfInfo(int, KnPersisterTxn)";
        KnOPPAMAccInfoDTO respDTO = new KnOPPAMAccInfoDTO();

        knLogger.entry(methodName, pamAccInfoDTO);
        try {
        	int pamAccID = pamAccInfoDTO.getProfileDetails().getPamAccId();
            knLogger.info(methodName, "pamAccID :", pamAccID);

            //retrieving the Subscriber Info
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            KnPAMSubsProfInfoDTO subsProfInfoDTO = provXDMServerDAO.retrievePAMSubsProfInfo(pamAccID, persisterTxn);

            if (subsProfInfoDTO != null) {
            	//get the external corpID  by passing internal corpID
            	String extCorpID = provXDMServerDAO.retrieveExtCorporationId(subsProfInfoDTO.getCorpID(), persisterTxn);
            	// get corp profile
            	KnOPCorpProfileInfoDTO corpProfile = provXDMServerDAO.retrieveCorporateProfile(extCorpID, persisterTxn);
            	subsProfInfoDTO.setExtCorpId(extCorpID);
            	subsProfInfoDTO.setCorpName(corpProfile.getCorporateName());
            	List<String> addonPkgList =provXDMServerDAO.selectPAMSubAddOnPkgs(pamAccID, persisterTxn);
            	subsProfInfoDTO.setAddOnPkgId(addonPkgList);
            }

            respDTO.setProfileDetails(subsProfInfoDTO);
            respDTO.setPamAccId(pamAccID);
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.GET_PAMSUBS_INFO_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "PAM Subs profile doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }
        knLogger.exit(methodName, respDTO);
        return respDTO;
    }


    /**
     * method to retrieve the Subscriber Details
     *
     * @param pamAccInfoDTO int
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPUpdatePAMAccountDTO updatePAMAccState(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updatePAMAccState(int, KnPersisterTxn)";
        KnOPUpdatePAMAccountDTO respDTO = new KnOPUpdatePAMAccountDTO();
        knLogger.entry(methodName, pamAccInfoDTO);
        try {

            KnPAMAccPersistDTO pamAccPersistDTO = new KnPAMAccPersistDTO();
            pamAccPersistDTO.setBillingMdn(pamAccInfoDTO.getBillingMdn());
            pamAccPersistDTO.setPamAccState(pamAccInfoDTO.getPamAccState());

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.updatePAMAccState(pamAccPersistDTO, persisterTxn);

            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.UPDATE_PAMACCOUNT_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "PAM account  doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, respDTO);
        return respDTO;
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
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.", e);
        }
    }

    private void pamSubProfileValidation(KnPAMSubsProfInfoDTO inputDto, KnPAMSubsProfInfoDTO existingDBDto) throws KnProvBOException {
        final String methodName = "pamSubProfileValidation";

        //checking contractId
        if (!isEqual(trim(inputDto.getExtCorpId()), trim(existingDBDto.getExtCorpId()))) {
            //todo throw appropriate error
            knLogger.debug(methodName, "change in corpId");
            throwBOException(methodName, KnErrorCodes.BOEntity.CORPID_CHANGE_NOT_ALLOWED, "corpId Change not allowed !!! ");
        }
    }

    private String trim(String input) {
        return input != null ? input.trim() : input;
    }

    private static boolean isEqual(String input, String input1) {
        boolean flag = false;
        if (input != null)
            flag = input.equals(input1);
        else if (input1 == null)
            flag = true;
        return flag;
    }

    private void throwBOException(String methodName, String errorCode, String msg) throws KnProvBOException {
        knLogger.error(methodName, msg);
        throw new KnProvBOException(errorCode, msg);

    }

    private KnPAMAccPersistDTO populatePersistSubsProfDetails(KnPAMSubsProfInfoDTO inputDTO) {
        KnPAMAccPersistDTO persistDTO = new KnPAMAccPersistDTO();
        KnPAMSubsProfInfoDTO persistProfDet = new KnPAMSubsProfInfoDTO();
        persistProfDet.setClient_Type(inputDTO.getClient_Type());
        persistProfDet.setCorpName(inputDTO.getCorpName());
        persistProfDet.setCorpSubsType(inputDTO.getCorpSubsType());
        persistProfDet.setExtCorpId(inputDTO.getExtCorpId());
        persistProfDet.setPamAccId(inputDTO.getPamAccId());
        persistProfDet.setProfileName(inputDTO.getProfileName());
        persistProfDet.setPubSubsType(inputDTO.getPubSubsType());
        persistProfDet.setProfileId(inputDTO.getProfileId());
        persistProfDet.setServiceAuthStatus(inputDTO.getServiceAuthStatus());
        persistProfDet.setSubscriberFS2(inputDTO.getSubscriberFS2());
        persistProfDet.setCreationTime(inputDTO.getCreationTime());
        persistProfDet.setProvFSMap(inputDTO.getProvFSMap());
        persistDTO.setProfileDetails(persistProfDet);
        return persistDTO;
    }

    /**
     * method to retrieve the Subscriber Details
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPUpdatePAMAccountDTO updateCorpName(String extCorpId, String corpName, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateCorpName(corpId, corpName, KnPersisterTxn)";
        KnOPUpdatePAMAccountDTO respDTO = new KnOPUpdatePAMAccountDTO();
        knLogger.entry(methodName, extCorpId, corpName);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            provXDMServerDAO.updateCorpName(extCorpId, corpName, persisterTxn);

            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.UPDATE_PAMACCOUNT_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.CORP_PROFILE_ALREADY_EXISTS, "corp Profile doesn't exist");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, respDTO);
        return respDTO;
    }


    /**
     * method to retrieve the Subscriber Details
     *
     * @param pamAccInfoDTO int
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPUpdatePAMAccountDTO updatePAMAccMaxSub(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updatePAMAccMaxSubscriber(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        KnOPUpdatePAMAccountDTO respDTO = new KnOPUpdatePAMAccountDTO();
        knLogger.entry(methodName, pamAccInfoDTO);
        try {
            KnPAMAccPersistDTO pamAccPersistDTO = new KnPAMAccPersistDTO();
            pamAccPersistDTO.setBillingMdn(pamAccInfoDTO.getBillingMdn());
            pamAccPersistDTO.setTotalNoOfLines(pamAccInfoDTO.getTotalNoOfLines());

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.updatePAMAccMaxSub(pamAccPersistDTO, persisterTxn);

            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.UPDATE_PAMACCOUNT_SUCCESS);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "PAM account  doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, respDTO);
        return respDTO;
            }

    /**
     * method to Update PAM Acc Name
     *
     * @param pamAccInfoDTO int
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    public KnOPUpdatePAMAccountDTO updatePAMAccName(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updatePAMAccName(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        KnOPUpdatePAMAccountDTO respDTO = new KnOPUpdatePAMAccountDTO();
        knLogger.entry(methodName, pamAccInfoDTO);
        try {
            KnPAMAccPersistDTO pamAccPersistDTO = new KnPAMAccPersistDTO();
            pamAccPersistDTO.setBillingName(pamAccInfoDTO.getBillingName());
            pamAccPersistDTO.setPamAccId(pamAccInfoDTO.getPamAccId());

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.updatePAMAccName(pamAccPersistDTO, persisterTxn);

            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.UPDATE_PAMACCOUNT_SUCCESS);

        }  catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "PAM account  doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, respDTO);
        return respDTO;
    }

    public void updatePAMEtag(int pamAccID, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updatePAMEtag(KnIPPAMAccInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName, pamAccID);

        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnPAMSubsProfInfoDTO subsProfile = provXDMServerDAO.getPAMSubsProfInfo(pamAccID, persisterTxn);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();

            // update etag of pamaccountinfo
            provXDMServerDAO.updatePAMAccLastProfileUpdatedTime(pamAccID, lastProfileUpdateTime, persisterTxn);

            // update corp etag
            provXDMServerDAO.updateCorpProfileLastUpdateTime(subsProfile.getCorpID(), lastProfileUpdateTime, persisterTxn);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }
        knLogger.exit(methodName);
    }

    @Override
    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "retrievePAMAccIdForCorpId(int)";
        knLogger.debug(methodName, "ENTRY: corpId", corpId);
        //retrieving the Subscriber Info
        List<Integer> pamAccList = new ArrayList<>();
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            pamAccList = provXDMServerDAO.retrievePAMAccIdForCorpId(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        knLogger.debug(methodName, "EXIT", pamAccList);
        return pamAccList;
    }


    public KnOPProvDTO  updateEtagForNNISubscr(String extCorpId,int clientType,KnPersisterTxn persisterTxn)throws KnProvBOException{
        final String methodName="updateEtagForNNISubscr(String)";
        knLogger.debug(methodName,"ENTRY : extCorpId-",extCorpId);
        KnOPProvDTO response=new KnOPProvDTO();
        long lastProfileUpdateTime=System.currentTimeMillis();
        IProvXDMServerDAO provXDMServerDAO = null;
        try {
            provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnOPCorpProfileInfoDTO corpProfileInfoDTO=provXDMServerDAO.retrieveCorporateProfile(extCorpId,persisterTxn);
            int corpId=corpProfileInfoDTO.getCorpId();
            if(corpId != 0){
                knLogger.debug(methodName,"Corporate Exists");
                List<Integer> clientTypeList=new ArrayList<>();
                Collections.addAll(clientTypeList, com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value(),
                        com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value(), com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());
                int aliasNGroupMdnCount=provXDMServerDAO.getSubsCountOfClientTypeForCorp(corpId,clientTypeList,persisterTxn);
                if(aliasNGroupMdnCount==0){
                    knLogger.debug(methodName,"Alias&Group mdn are not present in the corp with extCorp =",extCorpId," ,Nullify LinkedGwKey ");
                    KnCorpProfilePersistDTO corpProfilePersistDTO=new KnCorpProfilePersistDTO();
                    corpProfilePersistDTO.setExtCorpId(extCorpId);
                    provXDMServerDAO.updateLinkedGwKeyOfCorp(corpProfilePersistDTO,persisterTxn);
                    knLogger.debug(methodName,"LinkedGwKey is nullified in corpInfo table");
                }else{
                    knLogger.debug(methodName,"Alias&Group mdn are  present in the corp with extCorp =",extCorpId);
                    if(corpProfileInfoDTO.getLinkedGwKey()!=null){
                        knLogger.debug(methodName,"Link exists for corpinfo table");
                        String linkedGwKey=corpProfileInfoDTO.getLinkedGwKey();
                        KnCorpGwLinkedAccInfoDTO corpGwLinkedAccInfoDTO=new KnCorpGwLinkedAccInfoDTO();
                        corpGwLinkedAccInfoDTO.setCorpNNIRefId(linkedGwKey);
                        corpGwLinkedAccInfoDTO.setLastUpdateTime(lastProfileUpdateTime);
                        knLogger.debug(methodName,"updating the etag for CorpGwLinkedAccountInfo table for corNniRefId = ",linkedGwKey);
                        provXDMServerDAO.updateEtag4corpNNIRefId(corpGwLinkedAccInfoDTO, persisterTxn);
                        knLogger.debug(methodName,"updated the etag for CorpGwLinkedAccountInfo table");
                    }else{
                        knLogger.info(methodName,"LinkedGwKey for the Corporate is null");
                    }
                }
            }else{
                knLogger.debug(methodName,"Corporate doesn't exist");
            }


            response=new KnOPProvDTO();
            response.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            response.setResponseMessage(KnProvConstants.UPDATE_ETAG_NNI_SUBSCR);
            response.setResponseCode(KnProvConstants.SUCCESS_CODE);

        }catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        knLogger.debug(methodName,"EXIT ",response);
        return response;

    }
    /**
     * This  method validate if the request feature bit is avalable in the default
     * feature bit configured in
     * KnConstants
     * @param inputMap
     * @return
     */

    private static boolean isProvFSBitAllowed(Map<Integer, Integer> inputMap){
        String methodName="isProvFSBitAllowed(Map<String, String>";
        knLogger.debug("Entered ", methodName, " inPutMap :", inputMap);
        boolean isProvFSBitValid= true;
        List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> declaredProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
        if(inputMap != null) {
        for(Map.Entry<Integer,Integer> reqProvEntry:inputMap.entrySet()){
            for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT declaredProvFS: declaredProvFSList)
            {
                if(!reqProvEntry.getKey().equals(declaredProvFS.value())){
                    isProvFSBitValid= false;
                    break;
                }
            }
        }
        }
        knLogger.debug( methodName, " If correct isProvFSBitValid should be true :", isProvFSBitValid);
        return isProvFSBitValid;
    }
    
    private int findInterOPBitStatus(KnPAMSubsProfInfoDTO existingSubsProf,	Map<String, Integer> existingPkgMap, KnPersisterTxn persisterTxn)
			throws KnFeatureSetException, KnBOException {
		String methodName = "findInterOPBitStatus(KnOPSubsProfileInfoDTO , long ,Map<String,Integer> )";
		knLogger.info(methodName, "Entry ", existingSubsProf, "existing map ", existingPkgMap,
				"existing subscriberfs ", existingSubsProf.getSubscriberFS2());
		BitSet existingFSBitSet = featureSetUtil.convertHexStringToBitSet(existingSubsProf.getSubscriberFS2());
		knLogger.debug(methodName, "existing subsFsBitSet - ", existingFSBitSet.toString());

		Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
		String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
		String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(
				existingSubsProf.getPubSubsType(),
				existingSubsProf.getCorpSubsType(), existingSubsProf.getClient_Type(),
				basePkgCode, xdmPttServerId);
		BitSet finalFSBitSet = new BitSet(Long.SIZE);
		BitSet basePkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
		finalFSBitSet.or(basePkgCodeBiSet);
		knLogger.info(methodName, "base pkg bitset - ", finalFSBitSet.toString());
		if (!existingPkgMap.isEmpty()) {
			String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(
					existingSubsProf.getPubSubsType(),
					existingSubsProf.getCorpSubsType(), existingSubsProf.getClient_Type(),
					existingPkgMap, xdmPttServerId);
			BitSet pkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
			knLogger.debug(methodName, "PkgIds bitset - ", pkgCodeBiSet);
			finalFSBitSet.or(pkgCodeBiSet);
		}
		knLogger.debug(methodName, "final PkgIds bitset  - ", finalFSBitSet.toString());
		
		int status = LMR_BIT_STATUS.NO_CHANGE.Value();
		if (existingFSBitSet.get(LMR_BIT) && !finalFSBitSet.get(LMR_BIT)) {
			status = LMR_BIT_STATUS.MANUALLY_ENABLED.Value();
		} else if (!existingFSBitSet.get(LMR_BIT) && finalFSBitSet.get(LMR_BIT)) {
			status = LMR_BIT_STATUS.MANUALLY_DISABLED.Value();
		}

		knLogger.info(methodName, "LMR bit status :", status);
		return status;
	}
}