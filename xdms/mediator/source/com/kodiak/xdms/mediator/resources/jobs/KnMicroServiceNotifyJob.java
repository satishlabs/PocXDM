/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyBulkDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyDto;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.xdms.mediator.resources.KnJobConstants.*;


public class KnMicroServiceNotifyJob extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMicroServiceNotifyJob.class);
    private KnEXDMSNotifyDto notifyDto;
    //private KnMqServiceConfig rmqInfoDto;
    private static int RETRY_COUNT = 2;
    private static String MICROSERVICE_NOTIFY = "MICROSERVICE_NOTIFY";
    private KnRmqMessagePublisher messagingFwr;
    private KnAuditHelper audit;
    private static final String CORP_AUDIT="4002";
    private static final String EVENT_TYPE  = "XDM_NOTIFY_EVENT";
    private static final String ENABLED = "1";
    private static final String PROD_PREFIX = "prod.";
    private static final String BROADCAST_PREFIX = "prod.broadcast.";
    public static final String CROSSSITE_TOPIC_EXCHANGE = "CrossSiteTopicExchange";

    public KnMicroServiceNotifyJob(KnEXDMSNotifyDto notifyDto) {
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
        headers.put(SERVICE_ID, KnJobConstants.SERVICE_ID_VALUE.XDM.value());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            knLogger.debug(methodName, "opening the transaction ");
            String routingKey = null;
            String federatedRoutingKey = null;
            String routKey = null;
            String multiSiteFlag=null;
            KnMessage request = new KnMessage();
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
                    knLogger.info(methodName, "Routing key from config for corp notify - 1", routingKey);
                    multiSiteFlag = getMultiSiteDeploymentFlag();
                    if (multiSiteFlag != null && ENABLED.equals(multiSiteFlag.trim())) {
                        request.setSrcExchangeName(CROSSSITE_TOPIC_EXCHANGE);
                    }
                    headers.put(RESOURCE_TYPE, KnJobConstants.RESOURCE_TYPE_VALUE.MDN.value());
                    federatedRoutingKey=KnConstants.USER_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                case 2:
                    //Get routing key for group notify
                    routingKey = KnRmqConfig.getInstance().getGrpNotifyEventBindingKey();
                    knLogger.info(methodName, "Routing key from config for corp notify - 2", routingKey);
                    multiSiteFlag = getMultiSiteDeploymentFlag();
                    if (multiSiteFlag != null && ENABLED.equals(multiSiteFlag.trim())) {
                        request.setSrcExchangeName(CROSSSITE_TOPIC_EXCHANGE);
                    }
                    headers.put(RESOURCE_TYPE, KnJobConstants.RESOURCE_TYPE_VALUE.GROUP.value());
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
                    knLogger.info(methodName, "Routing key from config for corp notify - 5", routingKey);
                    multiSiteFlag = getMultiSiteDeploymentFlag();
                    if (multiSiteFlag != null && ENABLED.equals(multiSiteFlag.trim())) {
                        request.setSrcExchangeName(CROSSSITE_TOPIC_EXCHANGE);
                    }
                    headers.put(RESOURCE_TYPE, KnJobConstants.RESOURCE_TYPE_VALUE.CORP.value());
                    federatedRoutingKey=KnConstants.USER_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                case 6:
                    //Get routing key for bulk group properties notify
                    routingKey = KnRmqConfig.getInstance().getGrpNotifyEventBindingKey();
                    headers.put(RESOURCE_TYPE, KnJobConstants.RESOURCE_TYPE_VALUE.BULK_GROUP_PROPERTIES.value());
                    federatedRoutingKey=KnConstants.GROUP_EVENT_FEDERATED_ROUTING_KEY;
                    break;
                default:
                    knLogger.warn(methodName, "Invalid Notify Event Type", notifyEvenType);
            }

            String json = objectMapper.writeValueAsString(notifyDto);
            knLogger.info(methodName, "Publishing json - ", json, ", routingKey - ", routingKey);
            request.setDestRoutingKey(routingKey);
            //request.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            request.setSync(Boolean.FALSE);
            request.setPayLoad(json);
            request.setDestQueueName(MICROSERVICE_NOTIFY);
            request.setRmqMsgHeader(headers);
            status = sendMessage(request, notifyDto.getId());
            if (null != federatedRoutingKey && (multiSiteFlag == null || !ENABLED.equals(multiSiteFlag.trim()))) {
                boolean status1 = false;
                request.setDestRoutingKey(federatedRoutingKey);
                request.setSrcExchangeName("FederatedTopicExchange");
                knLogger.debug(methodName, "Inside Publishing json - ", json, ", routingKey - ", request.getDestRoutingKey());
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

    private String getBroadcastRoutingKey(String routingKey) {
        final String methodName = "getBroadcastRoutingKey()";
        String broadCastRoutKey = routingKey;
        if (routingKey.startsWith(PROD_PREFIX)) {
            broadCastRoutKey = BROADCAST_PREFIX + routingKey.substring(PROD_PREFIX.length());
        }
        knLogger.info(methodName, "broadCastRoutKey:", broadCastRoutKey);
        return broadCastRoutKey;
    }

    private String getMultiSiteDeploymentFlag() {
        final String methodName = "getMultiSiteDeploymentFlag()";
        String multiSiteDeploymentFlag = null;
        try {
            String localClusterId = System.getenv(CLUSTERID_ENV_NAME);
            Map<String, String> microServicesParamNameValueMap = KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(Integer.parseInt(localClusterId));
            multiSiteDeploymentFlag = microServicesParamNameValueMap.get(KnConstants.MICROSERVICES_COMMON_CONFIG.MULTISITE_DEPLOYMENT_FLAG.value());
            knLogger.info(methodName, "localClusterId - ", localClusterId,
                    ", multiSiteDeploymentFlag - ", multiSiteDeploymentFlag);

        } catch (NumberFormatException e) {
            knLogger.error(methodName, "Invalid cluster id in environment variable - " + CLUSTERID_ENV_NAME, e);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve microservice common config.", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while retrieving multisite deployment flag.", e);
        }
        return multiSiteDeploymentFlag;
    }
}
