/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mcsnotifymgr.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
/**
 * ************************************************************************
 * <p>
 * File name:  KnMCSNotifyJob.java
 * Subsystem:  XDMS
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Venkata Sudhakar            Dec 23, 2019                10.0+
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

public class KnMCSNotifyJob extends KnAbstractJob{
        private static final KnLogger knLogger = KnLogger.getLogger(KnMCSNotifyJob.class);
        private KnMCSNotifyDTO notifyDto;
        private static int RETRY_COUNT = 2;
        private static String MCS_NOTIFY = "XCAPSNNotifyQueue1";
        private KnRmqMessagePublisher messagingFwr;
        private String defaultJobName = "MCSNOTIFYJOB";
        private int TTLINMS = 30000;

        public KnMCSNotifyJob(KnMCSNotifyDTO notifyDto) {
            setJobName(defaultJobName);
            this.notifyDto = notifyDto;
            messagingFwr = KnRmqMessagePublisher.getInstance();
        }

        @Override
        public boolean executeTask() throws KnJobSchedulerException {
            String methodName = "executeTask()";
            boolean status = false;
            knLogger.info(methodName, "Entry - ", notifyDto);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                knLogger.debug(methodName, "opening the transaction ");
                String routingKey = KnRmqConfig.getInstance().getMcsNotificationBindingKey();
                String json = objectMapper.writeValueAsString(notifyDto);
                knLogger.info(methodName, "Publishing json - ", json, ", routingKey - ", routingKey);
                KnMessage request = new KnMessage();
                request.setDestRoutingKey(routingKey);
                request.setSync(Boolean.FALSE);
                request.setPayLoad(json);
                request.setDestQueueName(MCS_NOTIFY);
                request.setMessageTTL(TTLINMS);
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
                knLogger.info(methodName, "Message Published");
            } catch (Exception e) {
                knLogger.error(methodName, "Exception occurred ", e);
            }
            if(status){
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_MCSXCAP_NOTIFY_PUBLISHED);
            }
            return status;
        }


        private boolean sendMessage(KnMessage request) {
            final String methodName = "sendMessage()";
            boolean status = false;
            try {
                messagingFwr.sendMessage(request);
                status = true;
            } catch (KnMessageException e) {
                knLogger.error(methodName, "KnMessageException occurred while sending message - ", e);

            }catch (Exception e){
                knLogger.error(methodName, "Exception occurred while sending message - ", e);
            }

            return status;
        }
}
