package com.kodiak.xdms.server.bulkops.resources.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyBulkDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsJobConstants;
import static com.kodiak.xdms.server.bulkops.resources.KnBulkOpsJobConstants.*;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;



public class KnBulkOpsMicroServiceNotifyJob extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsMicroServiceNotifyJob.class);
    private KnEXDMSNotifyDto notifyDto;
    //private KnMqServiceConfig rmqInfoDto;
    private static int RETRY_COUNT = 2;
    private static String MICROSERVICE_NOTIFY = "MICROSERVICE_NOTIFY";
    private KnRmqMessagePublisher messagingFwr;
    private KnAuditHelper audit;
    private static final String CORP_AUDIT="4002";
    private static final String EVENT_TYPE  = "XDM_NOTIFY_EVENT";

    public KnBulkOpsMicroServiceNotifyJob(KnEXDMSNotifyDto notifyDto) {
        this.notifyDto = notifyDto;
        messagingFwr = KnRmqMessagePublisher.getInstance();
        audit =  KnAuditHelper.getAuditLogger(CORP_AUDIT);
    }

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        boolean status = false;
        knLogger.info(methodName, "Entry - ", notifyDto);
        Map<String, Object> headers = new HashMap<>();
        headers.put(SERVICE_ID, KnBulkOpsJobConstants.SERVICE_ID_VALUE.XDM.value());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            knLogger.debug(methodName, "opening the transaction ");
            String routingKey = null;
            String federatedRoutingKey = null;
            int notifyEvenType = notifyDto.getNotifyEventType();
            if(notifyDto.getId().contains(UNSERSCORE)){
                headers.put(RESOURCE_ID, notifyDto.getId().split("_")[1]);
            } else if(SERVICE_AUTH_STATUS.equals(notifyDto.getType())) {
                headers.put(RESOURCE_IDS, ((KnSubscrEXDMSNotifyBulkDTO) notifyDto).getMdnList().stream()
                        .map(KnSubscrEXDMSNotifyDto::getMdn).collect(Collectors.toList()));
            }
            switch (notifyEvenType) {
                case 1:
                    //Get the routing key for user notify
                    routingKey = KnRmqConfig.getInstance().getUserNotifyEventBindingKey();
                    headers.put(RESOURCE_TYPE, KnBulkOpsJobConstants.RESOURCE_TYPE_VALUE.MDN.value());
                    federatedRoutingKey= KnConstants.USER_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                case 2:
                    //Get routing key for group notify
                    routingKey = KnRmqConfig.getInstance().getGrpNotifyEventBindingKey();
                    headers.put(RESOURCE_TYPE, KnBulkOpsJobConstants.RESOURCE_TYPE_VALUE.GROUP.value());
                    federatedRoutingKey=KnConstants.GROUP_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                case 3:
                    //Get routing key for contact notify
                    routingKey = KnRmqConfig.getInstance().getContactNotifyBindingKey();
                    break;
                case 4:
                    //Get routing key for Client PV upgrade
                    routingKey = KnRmqConfig.getInstance().getClientPVUpgradeBindingKey();
                    federatedRoutingKey=KnConstants.USER_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                case 5:
                    //Get the routing key for corp notify
                    routingKey = KnRmqConfig.getInstance().getUserNotifyEventBindingKey();
                    headers.put(RESOURCE_TYPE, KnBulkOpsJobConstants.RESOURCE_TYPE_VALUE.CORP.value());
                    federatedRoutingKey=KnConstants.USER_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                case 6:
                    //Get routing key for bulk group properties notify
                    routingKey = KnRmqConfig.getInstance().getGrpNotifyEventBindingKey();
                    headers.put(RESOURCE_TYPE, KnBulkOpsJobConstants.RESOURCE_TYPE_VALUE.BULK_GROUP_PROPERTIES.value());
                    federatedRoutingKey=KnConstants.GROUP_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                default:
                    knLogger.warn(methodName, "Invalid Notify Event Type", notifyEvenType);
            }

            String json = objectMapper.writeValueAsString(notifyDto);
            knLogger.info(methodName, "Publishing json - ", json, ", routingKey - ", routingKey);
            KnMessage request = new KnMessage();
            request.setDestRoutingKey(routingKey);
            //request.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            request.setSync(Boolean.FALSE);
            request.setPayLoad(json);
            request.setDestQueueName(MICROSERVICE_NOTIFY);
            request.setRmqMsgHeader(headers);
            status = sendMessage(request, notifyDto.getId());
            if (null != federatedRoutingKey) {
                boolean status1 = false;
                request.setDestRoutingKey(federatedRoutingKey);
                request.setSrcExchangeName("FederatedTopicExchange");
                knLogger.info(methodName, "Inside Publishing json - ", json, ", routingKey - ", request.getDestRoutingKey());
                knLogger.info(methodName, "Message Publish with request second publish" + request);
                status1 = sendMessage(request, notifyDto.getId());
                if (!status1) {
                    knLogger.debug(methodName, "Message Publish with request second publish  - retrying");
                    for (int i = 0; i < RETRY_COUNT; i++) {
                        status1 = sendMessage(request, notifyDto.getId());
                        if (status1)
                            break;
                    }
                    knLogger.error(methodName, "Message Publish Failed to other site - after retry also");
                }
            }
            if (!status) {
                knLogger.debug(methodName, "Message Publish Failed - retrying");
                for (int i = 0; i < RETRY_COUNT; i++) {
                    status = sendMessage(request, notifyDto.getId());
                    if (status)
                        break;
                }
                knLogger.error(methodName, "Message Publish Failed - after retry also");
            }
            knLogger.info(methodName, "Message Published");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred ", e);
        }
        return status;
    }

    private boolean sendMessage(KnMessage request, String id) {
        final String methodName = "sendMessage()";
        boolean status = false;
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

}