package com.kodiak.xdms.server.bulkops.facade;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.processor.KnBulkOpsDeleteSubscProcessor;
import com.kodiak.xdms.server.bulkops.processor.KnBulkOpsUpdateAuthStatusProcessor;
import com.kodiak.xdms.server.bulkops.processor.KnBulkOpsUpdateSubscProcessor;
import com.kodiak.xdms.server.bulkops.processor.KnBulkOpsCreateSubscProcessor;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.IResponseHandler;
import com.kodiak.xdms.server.bulkops.KnResponseHandlerImpl;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;

public class KnBulkOpsFacade {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsFacade.class);
    private IResponseHandler respHandler;
    private KnAuditHelper audit;

    public KnBulkOpsFacade() {
        respHandler = new KnResponseHandlerImpl();
        audit = KnAuditHelper.getAuditLogger(KnConstants.AUDIT.PROV.value());
    }
    public boolean processRequest(KnMessage msgObj) {
        String methodName = "processRequest(KnMessage)";
        knLogger.info(methodName, "ENTRY: Received Message Object - ", msgObj);
        int operationId = -1;
        boolean synch = true;
        boolean synch_response = true;

        Object payLoad = msgObj.getPayLoad();
        IXDMRequestDTO inputDTO = null;
        IXDMResponseDTO respDTO = null;

        KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO = null; // createBulkSubscriber, updateBulkSubscriber, updateBulkAuthStatus, deleteBulkSubscriber
        String operation = null;
        if (payLoad instanceof IXDMRequestDTO) {
            knLogger.debug(methodName, "Pay load Object is instance of IXDMRequestDTO");
            inputDTO = (IXDMRequestDTO) payLoad;
            String operationType = inputDTO.getOperationType();
            operationId = Integer.parseInt(operationType);
        } else {
            operationId = 0;
        }
        knLogger.debug(methodName, "operation id - ", operationId);
        long latency = 0;
        long requestTime = System.currentTimeMillis();
        try{
            switch (operationId) {
                case KnConstants.OP_ID_CREATE_BULK_SUBSCRIBER:
                    if (inputDTO instanceof KnXDMBulkSubsProvInfoDTO) {
                        bulkSubsProvInfoDTO = (KnXDMBulkSubsProvInfoDTO) inputDTO;
                        operation = "createBulkSubscriber";
                        audit.writeAuditMessage("CID:"+ msgObj.getCorrelationId(),operation,KnAuditHelper.STATUS.REQUEST,"Request received for -"+bulkSubsProvInfoDTO.getTransactionId());
                    }
                    KnBulkOpsCreateSubscProcessor bulkOpsCreateSubscProcessor = KnBulkOpsCreateSubscProcessor.getInstance();
                    respDTO = bulkOpsCreateSubscProcessor.createBulkSubscriber(bulkSubsProvInfoDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_BULK_SUBSCRIBER:
                    if (inputDTO instanceof KnXDMBulkSubsProvInfoDTO) {
                        bulkSubsProvInfoDTO = (KnXDMBulkSubsProvInfoDTO) inputDTO;
                        operation = "updateBulkSubscriber";
                        audit.writeAuditMessage("CID:"+ msgObj.getCorrelationId(),operation,KnAuditHelper.STATUS.REQUEST,"Request received for -"+bulkSubsProvInfoDTO.getTransactionId());
                    }
                    KnBulkOpsUpdateSubscProcessor bulkOpsUpdateSubscProcessor = KnBulkOpsUpdateSubscProcessor.getInstance();
                    respDTO = bulkOpsUpdateSubscProcessor.updateBulkSubscriber(bulkSubsProvInfoDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_BULK_AUTH_STATUS:
                    if (inputDTO instanceof KnXDMBulkSubsProvInfoDTO) {
                        bulkSubsProvInfoDTO = (KnXDMBulkSubsProvInfoDTO) inputDTO;
                        operation = "updateBulkAuthStatus";
                        audit.writeAuditMessage("CID:"+ msgObj.getCorrelationId(),operation,KnAuditHelper.STATUS.REQUEST,"Request received for -"+bulkSubsProvInfoDTO.getTransactionId());
                    }
                    KnBulkOpsUpdateAuthStatusProcessor bulkOpsUpdateAuthStatusProcessor = KnBulkOpsUpdateAuthStatusProcessor.getInstance();
                    respDTO = bulkOpsUpdateAuthStatusProcessor.updateBulkAuthStatus(bulkSubsProvInfoDTO);
                    break;
                case KnConstants.OP_ID_DELETE_BULK_SUBSCRIBER:
                    if (inputDTO instanceof KnXDMBulkSubsProvInfoDTO) {
                        bulkSubsProvInfoDTO = (KnXDMBulkSubsProvInfoDTO) inputDTO;
                        operation = "deleteBulkSubscriber";
                        audit.writeAuditMessage("CID:"+ msgObj.getCorrelationId(),operation,KnAuditHelper.STATUS.REQUEST,"Request received for -"+bulkSubsProvInfoDTO.getTransactionId());
                    }
                    KnBulkOpsDeleteSubscProcessor bulkOpsDeleteSubscProcessor = KnBulkOpsDeleteSubscProcessor.getInstance();
                    respDTO = bulkOpsDeleteSubscProcessor.deleteBulkSubscriber(bulkSubsProvInfoDTO);
                    break;
                default:
                    respDTO = new KnXDMRespDTO();
                    respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                    respDTO.setResponseMessage("Invalid Operation Type");
                    respDTO.setResponseCode(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INVALID_OPERATION);
            }
            if (respDTO != null) {
                {
                    if (respDTO.getResponseStatus() == 0) {
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.SUCCESS, "Operation Successful");
                    } else {
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.FAILURE, "Operation failed ", respDTO.getResponseCode());
                    }
                }
            }
        }catch (Exception e) {
            knLogger.error(methodName, "Exception in Request Processing", e);
            respDTO = new KnXDMRespDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
            respDTO.setResponseMessage("Invalid Operation Type");
            respDTO.setResponseCode(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INVALID_OPERATION);
        }catch (Error e) {
            knLogger.error(methodName, "Critical Error in Request Processing (possible class loading failure)", e);
            respDTO = new KnXDMRespDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
            respDTO.setResponseMessage("Internal Server Error");
            respDTO.setResponseCode(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INVALID_OPERATION);
        }

        boolean status = true;
        if (synch) {
            status = respHandler.processResponse(respDTO, msgObj, synch_response);
            knLogger.info(methodName, "Message Sent Status - ", status);
        } else {
            status = respHandler.deleteAppIntfMsg(msgObj);
            knLogger.info(methodName, "Message deleted from App interface table  status - ", status);
        }
        long responseTime = System.currentTimeMillis();
        latency = responseTime - requestTime;
        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.SOAP_PROV_AVG_LATENCY_PEG, (int) latency);
        return status;
    }
}
