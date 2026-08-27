/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnResponseHandlerImpl.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 9, 2011      7.0
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
package com.kodiak.xdms.xdmintf.impl;

import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.KnRmqConnectionManager;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMCorpClientActResponseDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.commdto.common.KnCorpDetailsDTO;

import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.xdmintf.IResponseHandler;
import com.kodiak.xdms.xdmintf.resources.KnXDMIntfConstant;
import com.kodiak.xdms.xdmintf.resources.KnXDMIntfDAO;


import java.util.HashMap;
import java.util.Map;

import static com.kodiak.common.resources.KnErrorCodes.DAO.TXN_COMMIT_FAILED;


public class KnResponseHandlerImpl implements IResponseHandler {
    private static final KnLogger knLogger = KnLogger.getLogger(KnResponseHandlerImpl.class);

    private static final String CLASS = KnResponseHandlerImpl.class.getName();

    private KnRmqMessagePublisher msgFw;
    private KnRmqConnectionManager connectionManager;

    private KnXDMIntfDAO xdmIntfDAO = new KnXDMIntfDAO();

    public KnResponseHandlerImpl() {
        knLogger.info("Constructor", "Creating instance of KnResponseHandlerImpl");
        /*try {
        } catch (KnMessageException e) {
            knLogger.fatal( "Construtor", "Failed to initialize the Msg FW - ", e);
        }*/
        msgFw = KnRmqMessagePublisher.getInstance();
        connectionManager = KnRmqConnectionManager.getInstance();
    }

    public boolean processJsonResponse(IXDMResponseDTO respDto, KnMessage reqMsg) {

        String methodName = "processResponse(respDto, reqMsg)";
        knLogger.debug(methodName, "Entry: Process Response with RESP_DTO - ", respDto, ", REQ_MSG - ", reqMsg);
        KnMessage respMsg = new KnMessage();
        boolean resultStatus = true;
        try {
            respMsg.setCorrelationId(reqMsg.getCorrelationId());
            //respMsg.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            respMsg.setSync(false);
            respMsg.setFeatureId(reqMsg.getFeatureId());
            respMsg.setMessageTTL(reqMsg.getMessageTTL());
            respMsg.setUpgrade(reqMsg.isUpgrade());
            if (respDto != null) {
                if (respDto.getDestQueueName() != null) {
                    respMsg.setDestQueueName(respDto.getDestQueueName());
                } else {
                    respMsg.setDestQueueName(reqMsg.getSrcQueueName());
                }
            }
            KnXDMCorpClientActResponseDTO resp = (KnXDMCorpClientActResponseDTO) respDto;
            knLogger.info(methodName, "response data recieved", resp);
            Map<String, Object> responseMap = null;
            if (resp != null && resp.getResponseMap() != null) {
                if (respDto.getResponseCode().equals(TXN_COMMIT_FAILED)) {
                    responseMap = new HashMap<>();
                    responseMap.put("status", KnConstants.STATUS_FAILURE);
                    responseMap.put("statusCode", TXN_COMMIT_FAILED);
                    responseMap.put("message", "Could not complete action due to invalid action performed on DB, either DB " +
                            "record exists or Mandatory param not set for DB Txn");
                    resp.setResponseMap(responseMap);
                }
            }
            if (resp != null)
                respMsg.setPayLoad(mapToJson(resp.getResponseMap()));
            respMsg.setIDMRequest(Boolean.TRUE);
            respMsg.setDestIPAdress(reqMsg.getSrcIPAddress());
            respMsg.setDestRoutingKey(reqMsg.getSrcRoutingKey());
            respMsg.setAmqpMsgType("sync-response");
            respMsg.setSrcExchangeName(reqMsg.getSrcExchangeName());
            knLogger.debug(methodName, "Response msg recieved - ", respMsg);
            knLogger.debug(methodName, "Response msg recieved Payload- ", respMsg.getPayLoad());
            /*KnMessage msg = msgFw.processRequest(respMsg);
            KnMessage.RESULT_STATUS status = msg.getRespStatus();
            knLogger.info(methodName, "Status - ", status.value());*/
            msgFw.sendMessage(respMsg);
        } catch (Exception e) {
            knLogger.error(methodName, "KnMessageException occured while sending back response - ", e);
            resultStatus = false;
            knLogger.error(methodName, e);
        }

        try {
            knLogger.debug(methodName, "Sending ack to queue");
            connectionManager.sendAck(reqMsg.getDestQueueName(), reqMsg.getDeliveryTag());
            // msgFw.sendAck(reqMsg.getDestQueueName(), reqMsg.getDeliveryTag());
            //xdmIntfDAO.deleteAppIntfMsg(respMsg);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to send ack for queue ", reqMsg.getDestQueueName(), " - ", reqMsg.getDeliveryTag());
            knLogger.error(methodName, e);
        }
        return resultStatus;
    }


    public boolean processResponse(IXDMResponseDTO respDto, KnMessage reqMsg) {

        String methodName = "processResponse(respDto, reqMsg)";
        knLogger.debug(methodName, "Entry: Process Response with RESP_DTO - ", respDto, ", REQ_MSG - ", reqMsg);
        KnMessage respMsg = new KnMessage();
        boolean resultStatus = true;
        try {
            respMsg.setCorrelationId(reqMsg.getCorrelationId());
            respMsg.setUpgrade(reqMsg.isUpgrade());
            // respMsg.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            respMsg.setSync(false);
            respMsg.setPayLoad(respDto);
            respMsg.setFeatureId(reqMsg.getFeatureId());
            respMsg.setMessageTTL(reqMsg.getMessageTTL());
            if (respDto != null && respDto.getDestQueueName() != null) {
                respMsg.setDestQueueName(respDto.getDestQueueName());
            } else {
                respMsg.setDestQueueName(reqMsg.getSrcQueueName());
            }
            //respMsg.setDestIPAdress(reqMsg.getSrcIPAddress());
            // respMsg.setDestPttServerId(respMsg.getDestPttServerId());
            respMsg.setDestRoutingKey(reqMsg.getSrcRoutingKey());
            respMsg.setAmqpMsgType(KnXDMIntfConstant.MSG_SYNC_RESPONSE);
            respMsg.setRespStatus(KnMessage.RESULT_STATUS.SUCCESS);
            respMsg.setSrcExchangeName(reqMsg.getSrcExchangeName());// TODO move from here....
            knLogger.debug(methodName, "Response msg recieved - ", respDto);

            /*KnMessage msg = msgFw.processRequest(respMsg);
            KnMessage.RESULT_STATUS status = msg.getRespStatus();
            knLogger.info( methodName, "Status - ", status.value());*/
            msgFw.sendMessage(respMsg);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while sending back response - ", e);
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

    public String mapToJson(Map<String, Object> map) {
        String mapAsJson = null;
        try {
            mapAsJson = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            knLogger.error("mapToJson - Parsing error", e);
        } catch (Exception e) {
            knLogger.error("mapToJson", e);
        }
        knLogger.info("mapToJson", "json string obtained", mapAsJson);
        return mapAsJson;
    }

    public String mapToJson(Object map) {
        String mapAsJson = null;
        try {
            mapAsJson = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            knLogger.error("mapToJson - Parsing error", e);
        } catch (Exception e) {
            knLogger.error("mapToJson", e);
        }
        knLogger.info("mapToJson", "json string obtained", mapAsJson);
        return mapAsJson;
    }

    @Override
    public boolean processCorpBanFanJsonResponse(IXDMResponseDTO respDto, KnMessage reqMsg) {
        String methodName = "processCorpBanFanJsonResponse(respDto, reqMsg)";
        knLogger.debug(methodName, "Entry: Process Response with RESP_DTO - ", respDto, ", REQ_MSG - ", reqMsg);
        KnMessage respMsg = new KnMessage();
        boolean resultStatus = true;
        try {
            respMsg.setCorrelationId(reqMsg.getCorrelationId());
            //respMsg.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            respMsg.setSync(false);
            respMsg.setFeatureId(reqMsg.getFeatureId());
            respMsg.setMessageTTL(reqMsg.getMessageTTL());
            respMsg.setUpgrade(reqMsg.isUpgrade());
            if (respDto != null) {
                if (respDto.getDestQueueName() != null) {
                    respMsg.setDestQueueName(respDto.getDestQueueName());
                } else {
                    respMsg.setDestQueueName(reqMsg.getSrcQueueName());
                }
            }
            KnXDMCorpClientActResponseDTO resp = (KnXDMCorpClientActResponseDTO) respDto;
            knLogger.info(methodName, "response data recieved", resp);
            KnCorpDetailsDTO corpDetailsDTO = null;
            if (resp != null) corpDetailsDTO = resp.getCorpDetailsDTO();
            knLogger.info(methodName, "corpDetailsDTO response data recieved", corpDetailsDTO);
            if (respDto != null && respDto.getResponseStatus() == KnConstants.STATUS_FAILURE) {
                respMsg.setPayLoad(mapToJson(resp.getResponseMap()));
            } else if (respDto != null && respDto.getResponseStatus() == KnConstants.STATUS_SUCCESS) {
                respMsg.setPayLoad(mapToJson(corpDetailsDTO));
            }
            respMsg.setIDMRequest(Boolean.TRUE);
            respMsg.setDestIPAdress(reqMsg.getSrcIPAddress());
            respMsg.setDestRoutingKey(reqMsg.getSrcRoutingKey());
            respMsg.setAmqpMsgType("sync-response");
            respMsg.setSrcExchangeName(reqMsg.getSrcExchangeName());

            knLogger.debug(methodName, "Response msg recieved - ", respMsg);

            msgFw.sendMessage(respMsg);
            /*KnMessage msg = msgFw.processRequest(respMsg);
            KnMessage.RESULT_STATUS status = msg.getRespStatus();
            knLogger.info(methodName, "Status - ", status.value());*/
            //if(status.value())

        } catch (Exception e) {
            knLogger.error(methodName, "KnMessageException occured while sending back response - ", e);
            resultStatus = false;
            knLogger.error(methodName, e);
        }

        try {
            knLogger.debug(methodName, "Sending ack to queue");
            //msgFw.sendAck(reqMsg.getDestQueueName(), reqMsg.getDeliveryTag());
            connectionManager.sendAck(reqMsg.getDestQueueName(), reqMsg.getDeliveryTag());
            //xdmIntfDAO.deleteAppIntfMsg(respMsg);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to send ack for queue ", reqMsg.getDestQueueName(), " - ", reqMsg.getDeliveryTag());
            knLogger.error(methodName, e);
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
                respMsg.setAmqpMsgType(KnXDMIntfConstant.MSG_SYNC_RESPONSE);
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
}

/*KnMessage message = new KnMessage();
        message.setDestPttServerId(destpttServerId);
        message.setDestQueueName(DEST_QUEUE);
        message.setFeatureId(FEATURE_ID);
        message.setTTL(TTL);
        message.setMessageTTL(MESSAGE_TTL);
        message.setMsgType(KnAsyncConstant.MESSAGE_TYPE.DB);
        message.setSync(TRUE);
        message.setPayLoad(xdmRequest);
*/
