/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.xdmintf.impl;

import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMCorpRespDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.IResponseHandler;

public class KnDroppedRequestHandlerImpl implements IRequestHandlerIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnDroppedRequestHandlerImpl.class);
    private IResponseHandler respHandler;
    public KnDroppedRequestHandlerImpl() {
        final String methodName="KnCorpRequestHandlerImpl()";
        knLogger.debug(methodName, "Initializing Response handler");
        respHandler = new KnResponseHandlerImpl();
        knLogger.debug(methodName, "Response handler initialized");
    }
    @Override
    public boolean processRequest(KnMessage msgObj) {

        final String methodName = "processRequest(KnMessage)";
        knLogger.entry(methodName, msgObj);
        String correlationId = msgObj.getCorrelationId();
        String featureId = msgObj.getFeatureId();
        knLogger.info(methodName, "Request dropping for correlationId - ", correlationId);
        IXDMResponseDTO respDto;
        respDto = getResponseObj(featureId);
        // Get the response object with proper timed out error code based on the featureId in msgObj.
        return respHandler.processResponse(respDto, msgObj);
    }

    private IXDMResponseDTO getResponseObj(String featureId) {
        IXDMResponseDTO responseDTO = null;
        switch (featureId){
            case KnConstants.FEATURE_ID_PROV:
                responseDTO = new KnXDMRespDTO();
                break;
            case KnConstants.FEATURE_ID_PUB:
                responseDTO =  new KnXDMRespDTO();
                break;
            case KnConstants.FEATURE_ID_CORP:
                responseDTO = new KnXDMCorpRespDTO();
                break;

        }
        if(null != responseDTO){
            responseDTO.setResponseStatus(1);
            responseDTO.setResponseCode(KnErrorCodes.SERVER_BUSY_REQUEST_DROPPED);
            responseDTO.setResponseMessage("Request dropped due to thread pool queue exhausted.");
        }
        return responseDTO;
    }
}
