/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpLicenseInfoUtil.java
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
 */

package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicensePackDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicenseSubDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;
import java.util.List;

public class KnCorpLicenseInfoUtil {

	private static final KnLogger knLogger = KnLogger
			.getLogger(KnCorpLicenseInfoUtil.class);

	public KnCorpLicenseInfoUtil() {

	}

	public Collection<KnLicensePackDTO> getAllBillingMdns(int corpId,
														  String corpName, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "getAllBillingMdns(int,  String,boolean, KnPersisterTxn)";
		knLogger.entry(methodName, corpId, corpName, pttServerId, persisterTxn);
		Collection<KnLicensePackDTO> licensePackDTOs = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
			licensePackDTOs = corpXdmDao.getLicenseProfileList(corpId, corpName, readOnly, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving getAllBillingMdns- ",
					e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, licensePackDTOs);
		return licensePackDTOs;
	}

	public Collection<KnLicenseSubDTO> getLicenseSubs(int pamAccId, int corpId,
													  String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "getLicenseSubs(int,  int, pttServerId,boolean,KnPersisterTxn)";
		knLogger.entry(methodName, pamAccId, pttServerId, persisterTxn);
		Collection<KnLicenseSubDTO> licensePackDTOs = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
			licensePackDTOs = corpXdmDao.getLicenseSubscriber(pamAccId, corpId, readOnly, persisterTxn);
			if (licensePackDTOs.isEmpty()) {
				throw new KnCorpBOException(
						KnErrorCodes.BOEntity.NO_BILLING_PROFILE_FOUND_FOR_CORP_PROFILE,
						"Billing Profile Information not found");
			}
		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving getLicenseSubs- ",
					e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, licensePackDTOs);
		return licensePackDTOs;
	}

	public KnCorpSubscriberDTO getPamAccId(String billingNumber, String pttServerId, boolean readOnly,
										   KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "getPamAccId(int,  String,boolean, KnPersisterTxn)";
		knLogger.entry(methodName, billingNumber, pttServerId, persisterTxn);
		KnCorpSubscriberDTO knCorpSubscriberDTO = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
			knCorpSubscriberDTO = corpXdmDao.getPamAccId(billingNumber, readOnly, persisterTxn);

		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving PamAccId- ",
					e);
			if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
				throw new KnCorpBOException(
						KnErrorCodes.BOEntity.INVALID_BILLING_NUMBER,
						"PamAccount ID not found for billing Number", e);
			}
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, knCorpSubscriberDTO);
		return knCorpSubscriberDTO;
	}

	public List<String> getMarkForDeletionPseudoMdns(int pamAccid, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "getMarkForDeletionPseudoMdns(int, int , String,boolean, KnPersisterTxn)";
		knLogger.entry(methodName, pamAccid, pttServerId, persisterTxn);
		List<String> pseudoList = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
			pseudoList = corpXdmDao.getMarkForDeletionPseudoMdns(pamAccid, readOnly, persisterTxn);

		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving getAllBillingMdns- ",
					e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, pseudoList);
		return pseudoList;
	}

	public void updateUnMarkList(Collection<String> unMarklist, int pamAccId,
			String xdmsHomePttId, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "updateUnMarkList(Collection, String , KnPersisterTxn)";
		knLogger.entry(methodName, unMarklist, xdmsHomePttId, persisterTxn);
		List<String> pseudoList = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
			corpXdmDao.updateUnMarkList(unMarklist, pamAccId, xdmsHomePttId,
					persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving getAllBillingMdns- ",
					e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, pseudoList);
	}

	public void updateMarkList(Collection<String> marklist, int pamAccId,
			String xdmsHomePttId, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "updateUnMarkList(Collection, String , KnPersisterTxn)";
		knLogger.entry(methodName, marklist, xdmsHomePttId, persisterTxn);
		List<String> pseudoList = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
			corpXdmDao.updateMarkList(marklist, pamAccId, xdmsHomePttId,
					persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving getAllBillingMdns- ",
					e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, pseudoList);
	}

	public void updateBillingName(String billingMDN, String billingName, String xdmsHomePttId, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "updateBillingName(String, String, String, KnPersisterTxn)";
		knLogger.entry(methodName, KnGDPRTemplate.mdn(billingMDN), KnGDPRTemplate.name(billingName), KnGDPRTemplate.mcpttId(xdmsHomePttId), persisterTxn);
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
			corpXdmDao.updateBillingName(billingMDN, billingName, xdmsHomePttId, persisterTxn);

		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while updating Billing Name", e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName);
	}

	public void checkListSize(Collection<String> marklist, Collection<String> unMarklist)
			throws KnCorpBOException {
		final String methodName = "checkListSize(marklist, unMarklist)";

		knLogger.entry(methodName, marklist, unMarklist);
			if (marklist != null &&  marklist.size() > KnConstants.MARKLIST_REQUEST_SIZE) {
				throw new KnCorpBOException(
						KnErrorCodes.BOEntity.MARKLIST_REQUEST_SIZE_EXCEEDS_MAXIMUM_ALLOWED_PER_REQUEST,
						"Marklist size exceeds allowed request size");
			}
			if (unMarklist != null &&  unMarklist.size()  > KnConstants.MARKLIST_REQUEST_SIZE) {
				throw new KnCorpBOException(
						KnErrorCodes.BOEntity.UNMARKLIST_REQUEST_SIZE_EXCEEDS_MAXIMUM_ALLOWED_PER_REQUEST,
						"Marklist size exceeds allowed request size");
			}
	}

	public List<Integer> getAllPamAccountIdforCorp(int corpId,
												   String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "updateUnMarkList(Collection, String ,boolean, KnPersisterTxn)";
		knLogger.entry(methodName, corpId, KnGDPRTemplate.mcpttId(xdmsHomePttId), persisterTxn);
		List<Integer> pseudoList = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
			pseudoList = corpXdmDao.getAllPamAccountIdforCorp(corpId, xdmsHomePttId, readOnly, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving getAllBillingMdns- ",
					e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, pseudoList);
		return pseudoList;
	}

	public long updatePamAccountEtag(int pamAccId,
			String xdmsHomePttId, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "updatePamAccountEtag(pamAccId, String , KnPersisterTxn)";
		knLogger.entry(methodName, pamAccId, KnGDPRTemplate.mcpttId(xdmsHomePttId), persisterTxn);
		long eTag ;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
			eTag = corpXdmDao.updatePamAccountEtag(pamAccId,
					xdmsHomePttId, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(
					methodName,
					"KnDAOException occured while retrieving getAllBillingMdns- ",
					e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
					e);
		}
		knLogger.debug(methodName, pamAccId);
		return eTag;
	}

	public KnCorpSubscriberDTO getPamAccountId(String billingNumber, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "getPamAccountId(int,  String, KnPersisterTxn)";
		knLogger.entry(methodName, billingNumber, pttServerId, persisterTxn);
		KnCorpSubscriberDTO knCorpSubscriberDTO = null;
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
			knCorpSubscriberDTO = corpXdmDao.getPamAccountId(billingNumber, persisterTxn);
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),e);
		}
		knLogger.debug(methodName, knCorpSubscriberDTO);
		return knCorpSubscriberDTO;
	}
}
