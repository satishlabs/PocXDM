/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;

import com.kodiak.frameworks.messaging.common.KnRmqConnectionManager;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;

/**
 * Created by abhishek on 7/11/16.
 */
public class KnPAMMicroServiceNotifyJob extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMMicroServiceNotifyJob.class);
    private KnEXDMSNotifyDto notifyDto;
    private static int RETRY_COUNT = 2;
    //private KnJmsMessagingClientImpl messagingFwr;
    private KnRmqMessagePublisher msgFw;
    private KnRmqConnectionManager connectionManager;
    private static String MICROSERVICE_NOTIFY = "MICROSERVICE_NOTIFY";


    public KnPAMMicroServiceNotifyJob(KnEXDMSNotifyDto notifyDto, KnMqServiceConfig rmqInfoDto) {
        this.notifyDto = notifyDto;

       msgFw = KnRmqMessagePublisher.getInstance();
        connectionManager = KnRmqConnectionManager.getInstance();
    }



    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        boolean status = false;
        knLogger.info(methodName, "Entry - ", notifyDto);
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            knLogger.debug(methodName, "opening the transaction ");
            String routingKey = null;
            int notifyEvenType = notifyDto.getNotifyEventType();
            switch (notifyEvenType) {
                case 1:
                    //Get the routing key for user notify
                    routingKey = KnRmqConfig.getInstance().getUserNotifyEventBindingKey();
                    break;
                case 2:
                    //Get routing key for group notify
                    routingKey = KnRmqConfig.getInstance().getGrpNotifyEventBindingKey();
                    break;
                case 3:
                    //Get routing key for contact notify
                    routingKey = KnRmqConfig.getInstance().getContactNotifyBindingKey();
                    break;

                default:
                    knLogger.warn(methodName, "Invalid Notify Event Type", notifyEvenType);
            }

            String json = objectMapper.writeValueAsString(notifyDto);
            knLogger.debug(methodName, "json - ", json);
            knLogger.debug(methodName, "Publishing json - ", json, ", routingKey - ", routingKey);
            KnMessage request = new KnMessage();
            request.setDestRoutingKey(routingKey);
            //request.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            request.setSync(Boolean.FALSE);
            request.setPayLoad(json);
            request.setDestQueueName(MICROSERVICE_NOTIFY);
            status = sendMessage(request);
            if (!status) {
                knLogger.debug(methodName, "Message Publish Failed - retrying");
                for (int i = 0; i < RETRY_COUNT; i++) {
                    status = sendMessage(request);
                    if (status)
                        break;
                }
                knLogger.error(methodName, "Message Publish Failed - after retry also");
            }
            knLogger.debug(methodName, "Message Published");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred ", e);
        }
        return status;
    }


    private boolean sendMessage(KnMessage request) {
        final String methodName = "sendMessage()";
        boolean status = false;
        try {
            msgFw.sendMessage(request);
            status = true;
        } catch (KnMessageException e) {
            knLogger.error(methodName, "KnMessageException occurred while sending message - ", e);

        }catch (Exception e){
            knLogger.error(methodName, "Exception occurred while sending message - ", e);
        }

        return status;
    }

}
