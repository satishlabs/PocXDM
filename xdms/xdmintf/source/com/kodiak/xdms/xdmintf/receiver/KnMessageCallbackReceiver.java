/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.xdmintf.receiver;

import com.kodiak.common.commdto.common.KnMessageConstants;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.callbackHandler.IMessageCallback;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.impl.*;
import com.kodiak.xdms.xdmintf.resources.KnXDMIntfConstants;
import com.kodiak.xdms.server.bulkops.facade.KnBulkOpsFacade;

public class KnMessageCallbackReceiver implements IMessageCallback, IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMessageCallbackReceiver.class);

    private String featureId;
    private IRequestHandlerIntf reqHandlerIntf = null;
    private String queue;
    private int cardStatus = 0;

    public KnMessageCallbackReceiver(String featureId) {
        this.featureId = featureId;
        knLogger.debug("KnMessageCallbackReceiver featureId :",featureId);
        if (KnXDMIntfConstants.FEATURE_ID_PROV.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnProvRequestHandlerImpl();
            queue = KnRmqConfig.getInstance().getProvqueueInfo().get(0).getQueuename();
        } else if (KnXDMIntfConstants.FEATURE_ID_PROV_POST.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnProvRequestHandlerImpl();
            queue = KnMessageConstants.PROV_XDM_WRITE_QUEUE;
        } else if (KnXDMIntfConstants.FEATURE_ID_PUB.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnPubRequestHandlerImpl();
            queue = KnRmqConfig.getInstance().getPubqueueInfo().get(0).getQueuename();
        } else if (KnXDMIntfConstants.FEATURE_ID_CORP_POST.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnCorpRequestHandlerImpl();
            queue = KnMessageConstants.CORP_XDM_WRITE_QUEUE;
        } else if (KnXDMIntfConstants.FEATURE_ID_CORP.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnCorpRequestHandlerImpl();
            queue = KnRmqConfig.getInstance().getCorpqueueInfo().get(0).getQueuename();
        } else if (KnXDMIntfConstants.FEATURE_ID_XDM_DATA_INTF.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnXDMdataIntfRequestHandlerImpl();
            queue = KnRmqConfig.getInstance().getDataintfqueueInfo().get(0).getQueuename();
        }else if(KnXDMIntfConstants.FEATURE_ID_DROPPED_REQUEST.equalsIgnoreCase(featureId)){
            reqHandlerIntf = new KnDroppedRequestHandlerImpl();
            queue = KnConstants.DROPPED_REQUEST;
        }
        else if (KnXDMIntfConstants.FEATURE_ID_LOGIN_NOTIFY_EVENTS.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnLoginNotifyEventsRequestHandlerImpl();
            queue = "XDMLOGINNOTIFYEVENTQ1";
        }
        else if (KnXDMIntfConstants.FEATURE_ID_LI_NOTIFY_EVENTS.equalsIgnoreCase(featureId)) {
            reqHandlerIntf = new KnLINotifyEventsRequestHandlerImpl();
            queue = KnRmqConfig.getInstance().getLiEventQueueInfo().get(0).getQueuename();
        }
		 else {
            knLogger.fatal("NO QUEUE IS CONFIGURED FOR THE FEATURE ID");
        }
    }


    public boolean receiveMessage(KnMessage msgObj) {
        // Received a call back from Messsaging FW
        String methodName = "receiveMessage";
        knLogger.debug(methodName, "Callback Received: ", msgObj);
        boolean requestStatus;
        //if () {
        requestStatus = processRequest(msgObj);
//        } else {
//            KnLogger.log(CLASS, methodName, KnLogger.DEBUG, "No further processing of message - [", msgObj, "]",
//                    "since card is not active");
//            requestStatus = false;
//        }

        return requestStatus;
    }

    public String getQueueName() {
        return queue;
    }

    /**
     * pass the message object to respective message handler for processing
     *
     * @param reqMsgObj
     * @return
     */
    private boolean processRequest(KnMessage reqMsgObj) {
        String methodName = "processRequest";
        knLogger.debug(methodName, "ENTRY : ");
        // new peg is added to get the total of all the requests received from the web cards
        Object payLoad = reqMsgObj.getPayLoad();
        if (payLoad instanceof IXDMRequestDTO) {
            IXDMRequestDTO inputDTO = (IXDMRequestDTO) payLoad;
            int operationId = Integer.parseInt(inputDTO.getOperationType());
            knLogger.info(methodName, "OperationId received - ", operationId);
            if (operationId == KnConstants.OPERATION_ID_HEALTH_PING) {
                return reqHandlerIntf.processRequest(reqMsgObj);
            } else if (cardStatus == KnStatusMgrConstants.CARD_STATES.ACTIVE.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_REQ_RCVD_FROM_WEB_CARDS);
                if (reqMsgObj.getDestQueueName().equals(KnRmqConfig.getInstance().getProvqueueInfo().get(0).getQueuename())) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_PROV_REQ_RCVD_FROM_WEB_CARDS);
                }else if (reqMsgObj.getDestQueueName().equals(KnMessageConstants.PROV_XDM_WRITE_QUEUE)) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_PROV_REQ_RCVD_FROM_WEB_CARDS);
                } else if (reqMsgObj.getDestQueueName().equals(KnRmqConfig.getInstance().getPubqueueInfo().get(0).getQueuename())) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_XCAP_REQ_RCVD_FROM_WEB_CARDS);
                } else if (reqMsgObj.getDestQueueName().equals(KnRmqConfig.getInstance().getCorpqueueInfo().get(0).getQueuename())) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_CORP_REQ_RCVD_FROM_WEB_CARDS);
                } else if (reqMsgObj.getDestQueueName().equals(KnMessageConstants.CORP_XDM_WRITE_QUEUE)) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_CORP_REQ_RCVD_FROM_WEB_CARDS);
                }
                if (operationId == KnConstants.OP_ID_CREATE_BULK_SUBSCRIBER
                        || operationId == KnConstants.OP_ID_UPDATE_BULK_SUBSCRIBER
                        || operationId == KnConstants.OP_ID_DELETE_BULK_SUBSCRIBER
                        || operationId == KnConstants.OP_ID_UPDATE_BULK_AUTH_STATUS) {
                    KnBulkOpsFacade bulkOpsFacade = new KnBulkOpsFacade();
                    return bulkOpsFacade.processRequest(reqMsgObj);
                }
                return reqHandlerIntf.processRequest(reqMsgObj);
            }
		} else if (reqMsgObj.getDestQueueName().equals("XDMLOGINNOTIFYEVENTQ1")) {
			if (payLoad instanceof String) {
				return reqHandlerIntf.processRequest(reqMsgObj);
			}
		}
        else if (reqMsgObj.getDestQueueName().equals(KnMessageConstants.XDMLINOTIFYEVENTQ)) {
            knLogger.info(methodName,"ENTRY in XDMLINOTIFYEVENT");
            if (payLoad instanceof String) {
                return reqHandlerIntf.processRequest(reqMsgObj);
            }
        }
        else if (payLoad instanceof String) {
			KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_CORP_REQ_RCVD_FROM_WEB_CARDS);
			return reqHandlerIntf.processRequest(reqMsgObj);
		}
        return false;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState,
                "; Current State - ", currentState + "]");
        cardStatus = currentState.value();
    }
}
