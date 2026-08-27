/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.xdmintf.impl;

import static com.kodiak.xdms.server.common.resources.KnConstants.FALSE;
import static com.kodiak.xdms.server.common.resources.KnConstants.TRUE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMLoginNotifyEventReqDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.IResponseHandler;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;

/**
 * ************************************************************************
 * <p>
 * File name: KnXDMdataIntfRequestHandlerImpl.java Subsystem: PoC
 * <p>
 * Name Date Release -------------------- ---------------- ------------------
 * Saurabh Kumar Sept 17, 2016 8.1.2
 * <p>
 * <p>
 * Manyata Tech Park' Greenheart Phase IV, Nagawara Bangalore - 560 045
 * www.kodiakptt.com All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Kodiak Networks.
 * ************************************************************************
 */

public class KnLoginNotifyEventsRequestHandlerImpl implements IRequestHandlerIntf {

	private static final KnLogger knLogger = KnLogger.getLogger(KnLoginNotifyEventsRequestHandlerImpl.class);

	private static final String SUCCESS_MSG = "Operation is successful";
	private static final String FAILURE_MSG = "Operation failed with error";
	private static final String FAILURE_MSG_INVALID_OPERATION = "Unsupported Operation";
	private static final String XDMDataIntfAudit = "4019";
	private static final int FAILURE = 1;

	private IXDMFacadeIntf facadeIntf;
	private IResponseHandler respHandler;
	private KnAuditHelper audit;

	public KnLoginNotifyEventsRequestHandlerImpl() {

		final String methodName = "KnLoginNotifyEventsRequestHandlerImpl()";
		facadeIntf = new KnXDMFacadeImpl();
		respHandler = new KnResponseHandlerImpl();
		knLogger.info(methodName, "Initializing XDMDataIntf Audit");
		audit = KnAuditHelper.getAuditLogger(XDMDataIntfAudit);
		knLogger.info(methodName, "XDMDataIntf Audit initialized - ", audit);
	}

	@Override
	public boolean processRequest(KnMessage msgObj) {
		
        knLogger.debug("Inside KnLoginNotifyEventsRequestHandlerImpl");

		final String methodName = "processRequest(KnMessage)";
		knLogger.entry(methodName, msgObj);

		Object payLoad = msgObj.getPayLoad();
		KnXDMLoginNotifyEventReqDTO requestDTO = new KnXDMLoginNotifyEventReqDTO();
		if (payLoad instanceof String) {
			  knLogger.info(methodName,"The String request recived from the Rest service is ", payLoad);
	            knLogger.info(methodName,"Convert the json string to object");
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				requestDTO = objectMapper.readValue((String)payLoad, KnXDMLoginNotifyEventReqDTO.class);
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
			knLogger.error(methodName, "Can't proceed further. Dropping the request and returning false");
			return FALSE;
		}
		knLogger.debug(methodName, "RequestDTO - ", requestDTO);
		 String operation = "loginNotifyEvent";

		if (requestDTO.getDeviceMdn() != null && requestDTO.getMcServiceBaseMDN() != null
				&& !requestDTO.getDeviceMdn().isBlank() && !requestDTO.getMcServiceBaseMDN().isBlank())
		{
			audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST,
					"Request received");
			IXDMResponseDTO respDto = facadeIntf.loginNotifyEvent((IXDMRequestDTO) requestDTO);

			knLogger.info(methodName, "respDto - ", respDto);
			auditResponse(respDto, audit, new StringBuilder("CID:").append(msgObj.getCorrelationId()).toString(),
					operation);
		}
		boolean isSent = respHandler.deleteAppIntfMsg(msgObj);
		return TRUE;
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