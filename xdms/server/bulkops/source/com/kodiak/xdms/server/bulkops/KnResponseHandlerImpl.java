package com.kodiak.xdms.server.bulkops;

import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.KnRmqConnectionManager;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;

public class KnResponseHandlerImpl implements IResponseHandler {
    private static final KnLogger knLogger = KnLogger.getLogger(KnResponseHandlerImpl.class);
    private static final String CLASS = KnResponseHandlerImpl.class.getName();

    private KnRmqMessagePublisher msgFw;
    private KnRmqConnectionManager connectionManager;

    public KnResponseHandlerImpl() {
        knLogger.info("Constructor", "Creating instance of KnResponseHandlerImpl");
        /*try {
        } catch (KnMessageException e) {
            knLogger.fatal( "Construtor", "Failed to initialize the Msg FW - ", e);
        }*/
        msgFw = KnRmqMessagePublisher.getInstance();
        connectionManager = KnRmqConnectionManager.getInstance();
    }

    public boolean deleteAppIntfMsg(KnMessage reqMsg) {
        String methodName = "deleteAppIntfMsg(reqMsg)";
        knLogger.debug(methodName, "Entry:");
        boolean resultStatus = true;
        try {
            connectionManager.sendAck(reqMsg.getDestQueueName(), reqMsg.getDeliveryTag());
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to ack");
            knLogger.error(methodName, e);
            resultStatus = false;
        }
        return resultStatus;
    }

    @Override
    public boolean processResponse(IXDMResponseDTO respDto, KnMessage reqMsg, boolean synch_response) {
        String methodName = "processResponse(respDto, reqMsg)";
        knLogger.debug(methodName, "Entry: Process Response with RESP_DTO - ", respDto, ", REQ_MSG - ", reqMsg);
        KnMessage respMsg = new KnMessage();
        boolean resultStatus = true;
        try {
            respMsg.setCorrelationId(reqMsg.getCorrelationId());
            // respMsg.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            respMsg.setSync(false);
            respMsg.setPayLoad(respDto);
            respMsg.setFeatureId(reqMsg.getFeatureId());
            respMsg.setMessageTTL(reqMsg.getMessageTTL());
            respMsg.setUpgrade(reqMsg.isUpgrade());
            if (null != respDto && null != respDto.getDestQueueName()) {
                respMsg.setDestQueueName(respDto.getDestQueueName());
            } else {
                respMsg.setDestQueueName(reqMsg.getSrcQueueName());
            }
            //respMsg.setDestIPAdress(reqMsg.getSrcIPAddress());
            // respMsg.setDestPttServerId(respMsg.getDestPttServerId());
            respMsg.setDestRoutingKey(reqMsg.getSrcRoutingKey());
            respMsg.setSrcExchangeName(reqMsg.getSrcExchangeName());
            if (synch_response) {
                respMsg.setAmqpMsgType(KnBulkOpsConstants.MSG_SYNC_RESPONSE);
            }
            respMsg.setRespStatus(KnMessage.RESULT_STATUS.SUCCESS);// TODO move from here....
            knLogger.debug(methodName, "Response msg recieved - ", respDto);

            msgFw.sendMessage(respMsg);
            /*KnMessage msg = msgFw.processRequest(respMsg);
            KnMessage.RESULT_STATUS status = msg.getRespStatus();
            knLogger.info( methodName, "Status - ", status.value());*/
        } catch (Exception e) {
            knLogger.error(methodName, "KnMessageException occured while sending back response - ", e);
            resultStatus = false;
            knLogger.error(methodName, e);
        }
        try {
            knLogger.debug(methodName, "Deleting the Msg from the App Interface Table");
            //msgFw.sendAck(reqMsg.getDestQueueName(), reqMsg.getDeliveryTag());
            connectionManager.sendAck(reqMsg.getDestQueueName(), reqMsg.getDeliveryTag());
//            xdmIntfDAO.deleteAppIntfMsg(respMsg);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to delete the msg from the App Intf Table");
            knLogger.error(methodName, e);
        }
        return resultStatus;
    }

    @Override
    public boolean processResponse(IXDMResponseDTO respDto, KnMessage reqMsg) {
        return false;
    }

    @Override
    public boolean processJsonResponse(IXDMResponseDTO respDto, KnMessage reqMsg) {
        return false;
    }

    @Override
    public boolean processCorpBanFanJsonResponse(IXDMResponseDTO respDto, KnMessage reqMsg) {
        return false;
    }


}
