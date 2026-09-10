/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnEtagEXDMSNotifyDto;
import com.kodiak.xdms.notificationmgr.beans.KnResourceDetailsDTO;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.LINE_SAPERATOR;
import static com.kodiak.xdms.notificationmgr.resources.KnJobConstants.*;
import static com.kodiak.xdms.notificationmgr.resources.KnJobConstants.RESOURCE_TYPE;

public class KnEtagMcsSendNotification implements Runnable {
    private static final KnLogger knLogger = KnLogger.getLogger(KnEtagMcsSendNotification.class);

    private String mdn;
    private Map<String, Integer> mapOfEtag;
    private KnRmqMessagePublisher messagingFwr;
    private KnAuditHelper audit;
    private static final String CORP_AUDIT="4002";
    private static String MICROSERVICE_NOTIFY = "MICROSERVICE_NOTIFY";
    private static final String EVENT_TYPE  = "XDM_NOTIFY_EVENT";
    public static final int RETRY_COUNT = 3;

    public KnEtagMcsSendNotification(String mdn, Map<String, Integer> mapOfEtag) {
        this.mdn = mdn;
        this.mapOfEtag = mapOfEtag;
        messagingFwr = KnRmqMessagePublisher.getInstance();
        audit =  KnAuditHelper.getAuditLogger(CORP_AUDIT);
    }

    @Override
    public void run() {
        String methodName = "executeTask()";
        boolean status = false;
        KnEtagEXDMSNotifyDto notifyDto = prepareEtagNotification(mdn, mapOfEtag);
        knLogger.info(methodName, "Entry - ", notifyDto);
        Map<String, Object> headers = new HashMap<>();
        headers.put(SERVICE_ID, KnJobConstants.SERVICE_ID_VALUE.XDM.value());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            knLogger.debug(methodName, "opening the transaction ");
            headers.put(RESOURCE_TYPE, RESOURCE_TYPE_VALUE.MDN.value());
            String routingKey = KnRmqConfig.getInstance().getEtagNotifyEventBindingKey();
            //if KnRmqConfig.getInstance().getEtagNotifyEventBindingKey() null then use default routing key
            if (routingKey == null || routingKey.isEmpty()) {
                routingKey = KnConstants.ETAG_EVENT_ROUTING_KEY;
            }
            String federatedRoutingKey = KnConstants.ETAG_EVENT_FEDERATED_ROUTING_KEY;
            String json = objectMapper.writeValueAsString(notifyDto);
            knLogger.info(methodName, "Publishing json - ", json, ", routingKey - ", routingKey);
            KnMessage request = new KnMessage();
            request.setDestRoutingKey(routingKey);
            //request.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            request.setSync(Boolean.FALSE);
            request.setPayLoad(json);
            request.setDestQueueName(MICROSERVICE_NOTIFY);
            request.setRmqMsgHeader(headers);
            status = sendMessage(request);
            if (null != federatedRoutingKey) {
                boolean status1 = false;
                request.setDestRoutingKey(federatedRoutingKey);
                request.setSrcExchangeName("FederatedTopicExchange");
                knLogger.info(methodName, "Inside Publishing json - ", json, ", routingKey - ", request.getDestRoutingKey());
                knLogger.info(methodName, "Message Publish with request second publish" + request);
                status1 = sendMessage(request);
                if (!status1) {
                    knLogger.debug(methodName, "Message Publish with request second publish  - retrying");
                    for (int i = 0; i < RETRY_COUNT; i++) {
                        status1 = sendMessage(request);
                        if (status1)
                            break;
                    }
                    knLogger.error(methodName, "Message Publish Failed to other site - after retry also");
                }
            }
            if (!status) {
                knLogger.debug(methodName, "Message Publish Failed - retrying");
                for (int i = 0; i < RETRY_COUNT; i++) {
                    status = sendMessage(request);
                    if (status)
                        break;
                }
                knLogger.error(methodName, "Message Publish Failed - after retry also");
            }
            knLogger.info(methodName, "Message Published");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred ", e);
        }
    }

    private boolean sendMessage(KnMessage request) {
        final String methodName = "sendMessage()";
        boolean status = false;
        String id = KnConstants.MICROSERVICES_EVENT_TYPE.ETAG_UPDATE.value() + LINE_SAPERATOR + mdn;
        try {
            messagingFwr.sendMessage(request);
            status = true;
        } catch (KnMessageException e) {
            knLogger.error(methodName, "KnMessageException occurred while sending message - ", e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while sending message - ", e);
        }
        if (status) {
            audit.writeAuditMessage("CID:" + id, EVENT_TYPE, KnAuditHelper.STATUS.SUCCESS, request.getDestRoutingKey());
        } else {
            audit.writeAuditMessage("CID:" + id, EVENT_TYPE, KnAuditHelper.STATUS.FAILURE, request.getDestRoutingKey());
        }
        return status;
    }


    private KnEtagEXDMSNotifyDto prepareEtagNotification(String mdn, Map<String, Integer> etagMap) {
        String methodName = "prepareEtagNotification(KnXcapDiffNotifyDTO)";

        KnEtagEXDMSNotifyDto etagNotifyDto = new KnEtagEXDMSNotifyDto();
        KnResourceDetailsDTO resourceDetailsDTO = new KnResourceDetailsDTO();
        etagNotifyDto.setResourceId(mdn);
        etagNotifyDto.setNotifyEventType(7);
        resourceDetailsDTO.setResourceType("DIRECTORY_ETAG");
        if (etagMap.get(mdn) != null) {
            resourceDetailsDTO.setEtag(String.valueOf(etagMap.get(mdn)));
        }
        resourceDetailsDTO.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        etagNotifyDto.setResourceDetails(Collections.singletonList(resourceDetailsDTO));
        knLogger.debug(methodName, "Etag Notify DTO : ", etagNotifyDto);
        return etagNotifyDto;
    }
}
