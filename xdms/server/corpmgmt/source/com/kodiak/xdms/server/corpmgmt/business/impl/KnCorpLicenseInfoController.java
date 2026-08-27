/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpLicenseInfoController.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
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
 */package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnTPUserAccountDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.corpmgmt.business.ICorpLicenseInfoController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpContactInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpLicenseInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPLicenseSubsListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicensePackDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicenseSubDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpLicensePackListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpLicenseSubsListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;

public class KnCorpLicenseInfoController implements ICorpLicenseInfoController {

	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpLicenseInfoController.class);

	private static final int UNMARK = 2;
	private static final int MARK = 1;

	private static final int UNMARK_CSR = 1;
	private static final int MARK_CSR = 2;

	private KnCorpCommonInfoUtil commonInfoUtil;
	private KnCorpContactInfoUtil contactInfoUtil;
	private KnCorpLicenseInfoUtil licenseInfoUtil;

	private KnAASFramework authorizationFwk;
	private KnValidatorFramework validatorFW;

	public KnCorpLicenseInfoController() {
		commonInfoUtil = new KnCorpCommonInfoUtil();
		contactInfoUtil = new KnCorpContactInfoUtil();
		licenseInfoUtil = new KnCorpLicenseInfoUtil();
		authorizationFwk = KnAASFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
		validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
	}

	public KnCorpLicensePackListRespDTO getAllBillingMdns(
			KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {

		final String methodName = "getAllBillingMdns(KnIPCorpInfoDTO, KnPersisterTxn)";
		knLogger.entry(methodName, corpInfoDTO, persisterTxn);

		// Step:
		// creating Response DTO object
		KnCorpLicensePackListRespDTO respDTO = new KnCorpLicensePackListRespDTO();
		try {

			int corpId = corpInfoDTO.getCorpId();

			knLogger.debug(methodName, "Corp Id passed in the request is - ",
					corpId);


			// get the Corp profile details from cache
			knLogger.debug(methodName,
					"Fetch the corpProfile profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(
					String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
			knLogger.info(methodName, "Corporate Profile - ", corpProfile);

        	// Invoking custom hook
			KnCorpHookRespDTO responseDTO;
			Map<String, Object> customParams = corpInfoDTO.getCustomParamMap();
			if (corpInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
				customParams.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
				customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
				customParams.put(KnConstants.PTT_SERVER_ID, corpProfile.getXdmsHome());
				corpInfoDTO.setCustomParamMap(customParams);
				KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
				hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_BILLING_NAME);
				hookIPDTO.setData(corpInfoDTO);
				KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
				Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
				if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
					responseDTO = (KnCorpHookRespDTO) hookResp;
					if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
						knLogger.error(methodName, "Returning Failure response");
						populateXdmResponseFroomHook(responseDTO, respDTO);
						return respDTO;
					}
                    respDTO.setFailedDataList(responseDTO.getFailedDataList());
				}
			}

			// use the xdms home pttServerId from Corp profile
			String xdmsHomePttId = corpProfile.getXdmsHome();

			long etag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, true, persisterTxn);
			String etagStr = String.valueOf(etag);
			KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
			persistDTO.setEtag(String.valueOf(etag));
			persistDTO.setInputDTO(corpInfoDTO);

			persistDTO.setEtag(etagStr);
			knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
			validatorFW.validate(persistDTO);
			knLogger.debug(methodName, "Validation completed Successfully.");

			knLogger.debug(methodName, "Before DB call to get AllBillingMdns");
			Collection<KnLicensePackDTO> licensePackDTOs = licenseInfoUtil
					.getAllBillingMdns(corpId, corpProfile.getNetworkName(), xdmsHomePttId, true, persisterTxn);

			respDTO.setLicensePackList(licensePackDTOs);
			respDTO.setEtag(etagStr);
			populate(respDTO);
		} catch (KnValidationException e) {
			knLogger.error(
					methodName,
					"KnValidationException occured while retrieving AllBillingMdns - ",
					e);
			populate(respDTO, e);
		} catch (KnCorpBOException e) {
			knLogger.error(
					methodName,
					"KnCorpBOException occured while retrieving AllBillingMdns - ",
					e);
			populate(respDTO, e);
		} catch (Exception e) {
			knLogger.error(
					methodName,
					"Unexpected Exception occured while retrieving AllBillingMdns - ",
					new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e
							.getMessage(), e));
			populate(respDTO, e);
		}
		knLogger.exit(methodName, respDTO);
		return respDTO;
	}

	public KnCorpLicenseSubsListRespDTO getLicenseSubscribers(
			KnIPLicenseSubsListDTO ipLicenseSubsListDTO,
			KnPersisterTxn persisterTxn) {

		final String methodName = "getLicenseSubscribers(KnIPLicenseSubsListDTO, KnPersisterTxn)";
		knLogger.entry(methodName, ipLicenseSubsListDTO, persisterTxn);

		// Step:
		// creating Response DTO object
		KnCorpLicenseSubsListRespDTO respDTO = new KnCorpLicenseSubsListRespDTO();
		try {
			KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
			int corpId = ipLicenseSubsListDTO.getCorpId();

			knLogger.debug(methodName, "Corp Id passed in the request is - ",
					corpId);

			// Step:
			// get the Corp profile details from cache
			knLogger.debug(methodName,
					"Fetch the corp profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(
					String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
			knLogger.info(methodName, "Corporate Profile - ", corpProfile);

			// use the xdms home pttServerId from Corp profile
			String xdmsHomePttId = corpProfile.getXdmsHome();
			// Invoking custom hook
			KnCorpHookRespDTO responseDTO;
			Map<String, Object> customParams = ipLicenseSubsListDTO
					.getCustomParamMap();
			if (ipLicenseSubsListDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
				customParams
						.put(KnConstants.CURRENT_MASTER_LIST_ETAG,
								corpProfile.getCorpMasterListEtag());
				customParams.put(
						KnConstants.PERSISTER_TXN,
						persisterTxn);
				customParams.put(
						KnConstants.PTT_SERVER_ID,
						xdmsHomePttId);
				ipLicenseSubsListDTO.setCustomParamMap(customParams);
				KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
				hookIPDTO.setAction(KnActions.ACTIONS.GET_PSUEDOMDNS);
				hookIPDTO.setData(ipLicenseSubsListDTO);
				KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl
						.getInstance();
				Object hookResp = processInvoker
						.invokeHook(
								KnConstants.CUSTOM_CORP_USER_HOOK,
								hookIPDTO);
				if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
					responseDTO = (KnCorpHookRespDTO) hookResp;
					if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE
							.value() == responseDTO.getStatus()) {
						knLogger.error(methodName, "Returning Failure response");
						populateXdmResponseFroomHook(responseDTO, respDTO);
						return respDTO;
					}
                    respDTO.setFailedDataList(responseDTO.getFailedDataList());
				}
			}

			//Fetching pamAccId mapped to billing number
			KnCorpSubscriberDTO knCorpSubscriberDTO = licenseInfoUtil.getPamAccId(
					ipLicenseSubsListDTO.getBillingNumber(), xdmsHomePttId, true, persisterTxn);
			knLogger.debug(methodName, "pamAccId fetched", knCorpSubscriberDTO.getPamAccId());


			persistDTO.setEtag(String.valueOf(knCorpSubscriberDTO.getEtag()));
			persistDTO.setInputDTO(ipLicenseSubsListDTO);
			knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
			validatorFW.validate(persistDTO);
			knLogger.debug(methodName, "Validation completed Successfully.");
			//Fetching All pamAccIds present in Corp
			List<Integer> allPamAccIds = licenseInfoUtil
					.getAllPamAccountIdforCorp(corpId, xdmsHomePttId, true, persisterTxn);
			knLogger.debug(methodName, "All PamAccIds present in Corp ", allPamAccIds);

			//Validating the pamaccount Id mapped to billing number is exists in requested corp
			if (!allPamAccIds.contains(knCorpSubscriberDTO.getPamAccId())) {
				throw new KnCorpBOException(
						KnErrorCodes.BOEntity.BILLING_PROFILE_NOT_FOUND,
						"billing number not part of corp");
			}

			knLogger.debug(methodName, "Before DB call to get getLicenseSubscribers");

			Collection<KnLicenseSubDTO> licenseSubDTOs = licenseInfoUtil
					.getLicenseSubs(knCorpSubscriberDTO.getPamAccId(), corpId, xdmsHomePttId, true, persisterTxn);
			List<String> licenseMDNs = licenseInfoUtil.getMarkForDeletionPseudoMdns(
					knCorpSubscriberDTO.getPamAccId(), xdmsHomePttId, true, persisterTxn);

			List<String> tpMdnList = new ArrayList<>();
			int clientType = 0;
			for (KnLicenseSubDTO knLicenseSubDTO : licenseSubDTOs) {
				knLicenseSubDTO.setMarkStatus(MARK);
				if (licenseMDNs.contains(knLicenseSubDTO.getMdn())) {
					knLicenseSubDTO.setMarkStatus(UNMARK);
				}
				tpMdnList.add(knLicenseSubDTO.getMdn());
				clientType = knLicenseSubDTO.getSubsClientType();
			}

			// PAM subscribers will be having same client type across one PAM account. getting TP MDN map to get vendor and user id
			if ((com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() == clientType) ||
					(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() == clientType) ||
					(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() == clientType)) {

            	IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
				Map<String, KnTPUserAccountDTO> tpMdnMap = commonXDMServerDAO.retrieveTPUserAccountForMDNs(tpMdnList, true, persisterTxn);
            	knLogger.debug(methodName, "Got TP mdn details - ", KnGDPRTemplate.mdnSet(tpMdnMap.keySet()));

            	for (KnLicenseSubDTO knLicenseSubDTO : licenseSubDTOs) {
            		KnTPUserAccountDTO tpUserAccountDTO = tpMdnMap.get(knLicenseSubDTO.getMdn());
            		if (tpUserAccountDTO != null) {
            			knLicenseSubDTO.setTpUser(tpUserAccountDTO.getTpUser());
            			knLicenseSubDTO.setTpAccount(tpUserAccountDTO.getTpAccount());
            		}
            	}
			}

			respDTO.setLicenseSubsList(licenseSubDTOs);
			respDTO.setEtag(String.valueOf(knCorpSubscriberDTO.getEtag()));
			populate(respDTO);
		} catch (KnCorpBOException e) {
			knLogger.error(
					methodName,
					"KnCorpBOException occured while retrieving getLicenseSubscribers - ",
					e);
			populate(respDTO, e);
		} catch (Exception e) {
			knLogger.error(
					methodName,
					"Unexpected Exception occured while retrieving getLicenseSubscribers - ",
					new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e
							.getMessage(), e));
			populate(respDTO, e);
		}
		knLogger.exit(methodName, respDTO);
		return respDTO;
	}

	@Override
	public KnCorpResponseDTO updateBillingName( KnIPLicenseSubsListDTO ipLicenseSubsListDTO, KnPersisterTxn persisterTxn) {

		final String methodName = "updateBillingName(KnIPLicenseSubsListDTO, KnPersisterTxn)";
		knLogger.entry(methodName, ipLicenseSubsListDTO, persisterTxn);

		// Step:
		// creating Response DTO object
		KnCorpLicensePackListRespDTO respDTO = new KnCorpLicensePackListRespDTO();
		try {
			KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
			int corpId = ipLicenseSubsListDTO.getCorpId();
			knLogger.debug(methodName, "Corp Id passed in the request is - ", 	corpId);

			// Step:
			// get the Corp profile details from cache
			knLogger.debug(methodName, "Fetch the corp profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
			knLogger.info(methodName, "Corporate Profile - ", corpProfile);

			// use the xdms home pttServerId from Corp profile
			String xdmsHomePttId = corpProfile.getXdmsHome();
			// Invoking custom hook
			KnCorpHookRespDTO responseDTO;
			Map<String, Object> customParams = ipLicenseSubsListDTO.getCustomParamMap();
			if (ipLicenseSubsListDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
				customParams.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
				customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
				customParams.put(KnConstants.PTT_SERVER_ID, xdmsHomePttId);
				ipLicenseSubsListDTO.setCustomParamMap(customParams);
				KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
				hookIPDTO.setAction(KnActions.ACTIONS.UPDATE_BILLING_NAME);
				hookIPDTO.setData(ipLicenseSubsListDTO);
				KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
				Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
				if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
					responseDTO = (KnCorpHookRespDTO) hookResp;
					if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
						knLogger.error(methodName, "Returning Failure response");
						populateXdmResponseFroomHook(responseDTO, respDTO);
						return respDTO;
					}
                    respDTO.setFailedDataList(responseDTO.getFailedDataList());
				}
			}

			//Fetching pamAccId mapped to billing number
			KnCorpSubscriberDTO knCorpSubscriberDTO = licenseInfoUtil.getPamAccId(ipLicenseSubsListDTO.getBillingNumber(), xdmsHomePttId, false,
					persisterTxn);
			knLogger.debug(methodName, "pamAccId fetched", knCorpSubscriberDTO.getPamAccId());

			persistDTO.setEtag(String.valueOf(knCorpSubscriberDTO.getEtag()));
			persistDTO.setInputDTO(ipLicenseSubsListDTO);
			knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
			validatorFW.validate(persistDTO);
			knLogger.debug(methodName, "Validation completed Successfully.");

			//Fetching All pamAccIds present in Corp
			List<Integer> allPamAccIds = licenseInfoUtil.getAllPamAccountIdforCorp(corpId, xdmsHomePttId, false, persisterTxn);
			knLogger.debug(methodName, "All PamAccIds present in Corp ", allPamAccIds);

			//Validating the pamaccount Id mapped to billing number is exists in requested corp
			if (!allPamAccIds.contains(knCorpSubscriberDTO.getPamAccId())) {
				throw new KnCorpBOException(KnErrorCodes.BOEntity.BILLING_PROFILE_NOT_FOUND, "billing number not part of corp");
			}
			knLogger.debug(methodName, "Before DB call to update Billing Name");

			licenseInfoUtil.updateBillingName(ipLicenseSubsListDTO.getBillingNumber(), ipLicenseSubsListDTO.getBillingName(),
					xdmsHomePttId, persisterTxn);

			knLogger.debug(methodName, "Billing Name updated successfully. upadting PAM etag");
			long eTag = licenseInfoUtil.updatePamAccountEtag(knCorpSubscriberDTO.getPamAccId(), xdmsHomePttId, persisterTxn);

			respDTO.setEtag(String.valueOf(eTag));
			populate(respDTO);

		} catch (KnCorpBOException e) {
			knLogger.error(methodName, "KnCorpBOException occured while retrieving getLicenseSubscribers - ", e);
			populate(respDTO, e);

		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception occured while retrieving getLicenseSubscribers - ",
					new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
			populate(respDTO, e);
		}

		knLogger.exit(methodName, respDTO);
		return respDTO;
	}

	public KnCorpResponseDTO markForDelete(
			KnIPLicenseSubsListDTO licenseSubsListDTO,
			KnPersisterTxn persisterTxn) {

		final String methodName = "markForDelete(KnIPLicenseSubsListDTO, KnPersisterTxn)";
		knLogger.entry(methodName, licenseSubsListDTO, persisterTxn);
		KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
		// creating Response DTO object
		KnCorpLicensePackListRespDTO respDTO = new KnCorpLicensePackListRespDTO();
		try {

			int corpId = licenseSubsListDTO.getCorpId();

			knLogger.debug(methodName, "Corp Id passed in the request is - ",
					corpId);

			// get the Corp profile details from cache
			knLogger.debug(methodName,
					"Fetch the corpProfile profile if cached or fetch from the DB the details");
			KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(
					String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
			knLogger.debug(methodName, "Corporate Profile - ", corpProfile);

			// use the xdms home pttServerId from Corp profile
			String xdmsHomePttId = corpProfile.getXdmsHome();

			//long etag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId,persisterTxn);
			//String etagStr = String.valueOf(etag);

			// Invoking custom hook
			KnCorpHookRespDTO responseDTO;
			Map<String, Object> customParams = licenseSubsListDTO
					.getCustomParamMap();
			if (licenseSubsListDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
				customParams
						.put(KnConstants.CURRENT_MASTER_LIST_ETAG,
								corpProfile.getCorpMasterListEtag());
				customParams.put(
						KnConstants.PERSISTER_TXN,
						persisterTxn);
				customParams.put(
						KnConstants.PTT_SERVER_ID,
						xdmsHomePttId);
				licenseSubsListDTO.setCustomParamMap(customParams);
				KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
				hookIPDTO.setAction(KnActions.ACTIONS.MARK_UNMARK_PSUEDOMDNS);
				hookIPDTO.setData(licenseSubsListDTO);
				KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl
						.getInstance();
				Object hookResp = processInvoker
						.invokeHook(
								KnConstants.CUSTOM_CORP_USER_HOOK,
								hookIPDTO);
				if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
					responseDTO = (KnCorpHookRespDTO) hookResp;
					if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE
							.value() == responseDTO.getStatus()) {
						knLogger.error(methodName, "Returning Failure response");
						populateXdmResponseFroomHook(responseDTO, respDTO);
						return respDTO;
					}
                    respDTO.setFailedDataList(responseDTO.getFailedDataList());
				}
			}

			Collection<String> marklist = licenseSubsListDTO.getMarkList();
			Collection<String> unMarklist = licenseSubsListDTO.getUnmarkList();

			//validate request list exceeds the configured value
			licenseInfoUtil.checkListSize(marklist, unMarklist);

			KnCorpSubscriberDTO knCorpSubscriberDTO = licenseInfoUtil.getPamAccId(
					licenseSubsListDTO.getBillingNumber(), xdmsHomePttId, false, persisterTxn);
			knLogger.debug(methodName, "PamAccount Id fetched from DB", knCorpSubscriberDTO.getPamAccId());

			persistDTO.setEtag(String.valueOf(knCorpSubscriberDTO.getEtag()));
			persistDTO.setInputDTO(licenseSubsListDTO);
			knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
			validatorFW.validate(persistDTO);
			knLogger.debug(methodName, "Validation completed Successfully.");
			if(marklist!=null){
			licenseInfoUtil.updateMarkList(marklist, knCorpSubscriberDTO.getPamAccId(), xdmsHomePttId,
					persisterTxn);
			}
			if(unMarklist!=null){
			licenseInfoUtil.updateUnMarkList(unMarklist, knCorpSubscriberDTO.getPamAccId(),
					xdmsHomePttId, persisterTxn);
			}
            //TODO Update pamaccountId Etag
			long eTag = licenseInfoUtil.updatePamAccountEtag(knCorpSubscriberDTO.getPamAccId(),xdmsHomePttId,persisterTxn);
			respDTO.setEtag(String.valueOf(eTag));
			populate(respDTO);
		} catch (KnCorpBOException e) {
			knLogger.error(
					methodName,
					"KnCorpBOException occured while retrieving AllBillingMdns - ",
					e);
			populate(respDTO, e);
		} catch (Exception e) {
			knLogger.error(
					methodName,
					"Unexpected Exception occured while retrieving AllBillingMdns - ",
					new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e
							.getMessage(), e));
			populate(respDTO, e);
		}
		knLogger.exit(methodName, respDTO);
		return respDTO;
	}

	public KnCorpLicenseSubsListRespDTO getLicenseSubscribersForCSR(
			KnIPLicenseSubsListDTO ipLicenseSubsListDTO,
			KnPersisterTxn persisterTxn) {

		final String methodName = "getLicenseSubscribersForCSR(KnIPLicenseSubsListDTO, KnPersisterTxn)";
		knLogger.entry(methodName, ipLicenseSubsListDTO, persisterTxn);

		// Step:
		// creating Response DTO object
		KnCorpLicenseSubsListRespDTO respDTO = new KnCorpLicenseSubsListRespDTO();
		try {

			// use the xdms home pttServerId from Corp profile
			String xdmsHomePttId = KnGenInfoUtil.getInstance()
					.retrieveLocalXDMPttServerId();
			//Retriving Pam account Id mapped to billing Number
			KnCorpSubscriberDTO knCorpSubscriberDTO = licenseInfoUtil.getPamAccId(
					ipLicenseSubsListDTO.getBillingNumber(), xdmsHomePttId, true, persisterTxn);
			knLogger.debug(methodName, "PamAccount Id fetched from DB", knCorpSubscriberDTO.getPamAccId());


			//Retriving License Subscribers mapped to Pam account Id
			knLogger.debug(methodName, "Before DB call to get Billing Number");
			Collection<KnLicenseSubDTO> licenseSubDTOs = licenseInfoUtil
					.getLicenseSubs(knCorpSubscriberDTO.getPamAccId(), 0, xdmsHomePttId, true, persisterTxn);

			knLogger.debug(methodName, "Retrived License Subscribers ", licenseSubDTOs );

			List<String> licenseMDNs = licenseInfoUtil.getMarkForDeletionPseudoMdns(
					knCorpSubscriberDTO.getPamAccId(), xdmsHomePttId, true, persisterTxn);

			List<String> tpMdnList = new ArrayList<>();
			int clientType = 0;
			for (KnLicenseSubDTO knLicenseSubDTO : licenseSubDTOs) {
				knLicenseSubDTO.setMarkStatus(UNMARK_CSR);
				if (licenseMDNs.contains(knLicenseSubDTO.getMdn())) {
					knLicenseSubDTO.setMarkStatus(MARK_CSR);
				}
				tpMdnList.add(knLicenseSubDTO.getMdn());
				clientType = knLicenseSubDTO.getSubsClientType();
			}

			// PAM subscribers will be having same client type across one PAM account. getting TP MDN map to get vendor and user id
			if ((com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() == clientType) ||
					(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() == clientType) ||
					(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() == clientType)) {

				IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
				Map<String, KnTPUserAccountDTO> tpMdnMap = commonXDMServerDAO.retrieveTPUserAccountForMDNs(tpMdnList, true, persisterTxn);
            	knLogger.debug(methodName, "Got TP mdn details - ", KnGDPRTemplate.mdnSet(tpMdnMap.keySet()));

            	for (KnLicenseSubDTO knLicenseSubDTO : licenseSubDTOs) {
            		KnTPUserAccountDTO tpUserAccountDTO = tpMdnMap.get(knLicenseSubDTO.getMdn());
            		if (tpUserAccountDTO != null) {
            			knLicenseSubDTO.setTpUser(tpUserAccountDTO.getTpUser());
            			knLicenseSubDTO.setTpAccount(tpUserAccountDTO.getTpAccount());
            		}
            	}
			}

			knLogger.debug(methodName, "Retrived License By Status  ", licenseSubDTOs );
			respDTO.setLicenseSubsList(licenseSubDTOs);
			populate(respDTO);
		} catch (KnCorpBOException e) {
			knLogger.error(methodName,"KnCorpBOException occured while retrieving AllBillingMdns - ", e);
			populate(respDTO, e);

		} catch (KnBOException e) {
			knLogger.error(methodName, "KnBOException occured while retrieving AllBillingMdns - ",	e);
			populate(respDTO, e);

		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while retrieving AllBillingMdns - ",	e);
			populate(respDTO, e);
		}
		knLogger.exit(methodName, respDTO);
		return respDTO;
	}



}
