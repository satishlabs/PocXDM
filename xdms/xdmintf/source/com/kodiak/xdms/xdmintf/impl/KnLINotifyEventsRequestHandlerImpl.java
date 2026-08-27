/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.xdmintf.impl;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.commdto.request.KnXDMLINotifyEventReqDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.commdto.common.KnKsekVaultInfo;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.lieventhandler.handler.KnLIUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.IResponseHandler;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;

import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.LI_SERVER_3GPP_COMPLIACE_FLAG;
import static com.kodiak.xdms.server.common.resources.KnConstants.FALSE;
import static com.kodiak.xdms.server.common.resources.KnConstants.TRUE;

/**
 * ************************************************************************
 * <p>
 * File name:  KnLINotifyEventsRequestHandlerImpl.java
 * Subsystem:  XDM
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Shashank Tewari            March 08, 2021                11.2
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnLINotifyEventsRequestHandlerImpl implements IRequestHandlerIntf {

	private static final KnLogger knLogger = KnLogger.getLogger(KnLINotifyEventsRequestHandlerImpl.class);

	private static final String SUCCESS_MSG = "Operation is successful";
	private static final String FAILURE_MSG = "Operation failed with error";
	private static final String FAILURE_MSG_INVALID_OPERATION = "Unsupported Operation";
	private static final String XDMDataIntfAudit = "4019";
	private static final int FAILURE = 1;

	private IXDMFacadeIntf facadeIntf;
	private IResponseHandler respHandler;
	private KnAuditHelper audit;

	public KnLINotifyEventsRequestHandlerImpl() {
		final String methodName = "KnLINotifyEventsRequestHandlerImpl()";
		facadeIntf = new KnXDMFacadeImpl();
		respHandler = new KnResponseHandlerImpl();
		knLogger.info(methodName, "Initializing XDMDataIntf Audit");
		audit = KnAuditHelper.getAuditLogger(XDMDataIntfAudit);
		knLogger.info(methodName, "XDMDataIntf Audit initialized - ", audit);
	}

	@Override
	public boolean processRequest(KnMessage msgObj) {
		final String methodName = "processRequestForLiEvent(KnMessage)";
		knLogger.info(methodName, "Entry ");
		boolean flag = TRUE;
		String decryptedMDN = null;
		try {
			KnGeneralUtil generalUtil = new KnGeneralUtil();
			KnXDMLINotifyEventReqDTO requestDTO = new KnXDMLINotifyEventReqDTO();
			if (KnLIUtil.isLIEncryption()){
				String encrMDN = null;
				Integer activationStatus = null;
				//flag = parseRequest(msgObj, requestDTO);
				Object payLoad = msgObj.getPayLoad();
				if (payLoad instanceof String) {
					knLogger.info(methodName,"The String request recived from the Rest service is ", payLoad);
					knLogger.info(methodName,"Convert the json string to object");
					ObjectMapper objectMapper = new ObjectMapper();
					try {
						requestDTO = objectMapper.readValue((String)payLoad, KnXDMLINotifyEventReqDTO.class);
						if(requestDTO !=null && requestDTO.getPayload() !=null){
							encrMDN = requestDTO.getPayload().getEncryptedMDN();
							if(requestDTO.getPayload().getTaskDetails() != null && requestDTO.getPayload().getTaskDetails().size()>0){
								activationStatus = requestDTO.getPayload().getTaskDetails().get(0).getActivationStatus();
							}
							knLogger.debug(methodName,"encrMDN ", KnGDPRTemplate.mdn(encrMDN)," activationStatus ",activationStatus);
							//Get the vault value
							KnKsekVaultInfo ksekVaultInfo = KnGeneralUtil.getKsekVaultInfo();
							knLogger.debug(methodName,"ksekVaultInfo = ",ksekVaultInfo);
							if(ksekVaultInfo !=null && ksekVaultInfo.getKsek() != null && ksekVaultInfo.getIv() != null){
								decryptedMDN = KnGenInfoUtil.decryptPayloadForLIEvent(ksekVaultInfo.getKsek(), encrMDN, ksekVaultInfo.getIv());
								knLogger.debug(methodName,"decryptedMDN : ",KnGDPRTemplate.mdn(decryptedMDN));
								if(activationStatus == null || activationStatus == 2){
									//delete entry
									knLogger.debug(methodName,"KnLIEventHandler before removing",KnGDPRTemplate.mdnList(KnLIEventHandler.getTargetMDNList()));
									KnLIEventHandler.getTargetMDNList().remove(decryptedMDN);
									knLogger.debug(methodName,"KnLIEventHandler after removing",KnGDPRTemplate.mdnList(KnLIEventHandler.getTargetMDNList()));
								}
								else if(activationStatus == 1){
									// insert entry
									knLogger.debug(methodName,"KnLIEventHandler before adding",KnGDPRTemplate.mdnList(KnLIEventHandler.getTargetMDNList()));
									KnLIEventHandler.getTargetMDNList().add(decryptedMDN);
									knLogger.debug(methodName,"KnLIEventHandler after adding",KnGDPRTemplate.mdnList(KnLIEventHandler.getTargetMDNList()));
								}
							}
							else{
								knLogger.error(methodName, "Can't proceed further. Because Vault values are not configured ");
							}
						}
					}  catch (JsonGenerationException e) {
						e.printStackTrace();
						knLogger.error(methodName,"JsonGenerationException", e);
					} catch (JsonMappingException e) {
						e.printStackTrace();
						knLogger.error(methodName,"JsonMappingException", e);
					} catch (IOException e) {
						e.printStackTrace();
						knLogger.error(methodName,"IOException", e);
					} catch (Exception e) {
						e.printStackTrace();
						knLogger.error(methodName,"Exception", e);
					}
				} else {
					knLogger.error(methodName, "Unexpected Class type - ", payLoad.getClass());
				}

			} else {
				knLogger.error(methodName, "Can't proceed further. Because LI_SERVER_3GPP_COMPLIACE_FLAG is not enabled ");
			}
		} catch (Exception e) {
			knLogger.error(methodName, "Exception occurred ", e);
			knLogger.error(methodName, "Can't proceed further. Dropping the request and returning false");
		}
		knLogger.debug(methodName, "decryptedMDN For LI ",KnGDPRTemplate.mdn(decryptedMDN));
		knLogger.info(methodName, "Exit ");
		return flag;
	}

	private void auditResponse(IXDMResponseDTO respDto, KnAuditHelper auditHelper, String corRelationId,
			String operation) {
		if (respDto.getResponseStatus() == 0) {
			auditHelper.writeAuditMessage(corRelationId, operation, KnAuditHelper.STATUS.SUCCESS, SUCCESS_MSG);
		} else {
			auditHelper.writeAuditMessage(corRelationId, operation, KnAuditHelper.STATUS.FAILURE, FAILURE_MSG,
					respDto.getResponseCode());
		}
	}

}