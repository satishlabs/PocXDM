/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   KnXDMCommonMediator.java
 * Subsystem:   XDMS Mediator
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Jan 11, 2011     7.0
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
 * ******************************************************************************
 */
package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMCorpUserDetailsRespDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnCorpSubsUserDetailsRespDTO;
import com.kodiak.common.commdto.response.KnXDMSubsAliasDetailsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.ggcache.dto.KnOidcTmpPwdDTO;
import com.kodiak.common.resources.*;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.common.KnRmqConnectionManager;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.xdms.mcsnotifymgr.KnMCSDocChangeNotifier;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocChangeListDto;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.mcsnotifymgr.resources.KnMCSNotifyConstants;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import com.kodiak.xdms.mediator.resources.jobs.KnEtagMgmtThread;
import com.kodiak.xdms.mediator.resources.jobs.KnMicroServiceNotifyJob;
import com.kodiak.xdms.mediator.util.KnNotificationService;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.*;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnDeleteDeviceDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpGroupInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpPTTSettingDocRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.KnPubDBTablesRegistry;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnPOCContactListDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubEXDMSNotifyDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubNotifyDetailsDTO;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPChgAuthStatusRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvOperationTypes;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.mediator.resources.KnJobConstants.ETAG_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;

public class KnXDMCommonMediator {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCommonMediator.class);

    //private IJmsMessagingClientIntf msgFwInstance;
    private KnRmqMessagePublisher msgFw;
    private KnRmqConnectionManager connectionManager;
    private static boolean isInitialized = false;
    private static KnXDMCommonMediator instance = null;
    private final String APP_INTF_TABLE_NAME = "DG.APPLICATIONINTERFACE";
    private String xdmPttServerId = null;
    private IXcapDiffNotifierIntf notifier;
    private static final int DIFF_SIZE = 20;
    //The job scheduler
    private KnJobSchedulerImpl scheduler;
    private KnGenInfoUtil genInfoUtil;
    private KnGeneralPasswordUtil pwdUtil;
    private KnGeneralCacheUtil cacheUtil;
    private static ExecutorService service;
    private KnEncryptionDecryptionUtil encryptionDecryptionUtil;
    private KnMCSDocChangeNotifier mcsDocChangeNotifier;
    private KnProvInfoUtil provInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private static final KnCorpGroupInfoUtil groupInfoUtil = new KnCorpGroupInfoUtil();
    KnPersisterTxn persisterTxn = null;
    IProvClientIntf provClientIntf;
    private com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpPTTSettingsUtil pttSettingsUtil;

    /**
     * making the class to the singleton
     */
    private KnXDMCommonMediator() {
        //get the instance of the Message FWK for response messages.
        knLogger.info("KnXDMCommonMediator()", "Creating instance of KnXDMCommonMediator");
        //try {
        msgFw = KnRmqMessagePublisher.getInstance();
        connectionManager = KnRmqConnectionManager.getInstance();
        xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        notifier = new KnXcapDiffNotifierImpl();
        scheduler = KnJobSchedulerImpl.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        service = KnThreadExecutors.newDynamicThreadPool(5, 5, 0L, ETAG_MGMT);
        pwdUtil = KnGeneralPasswordUtil.getInstance();
        cacheUtil = KnGeneralCacheUtil.getInstance();
        encryptionDecryptionUtil = KnEncryptionDecryptionUtil.getInstance();
        mcsDocChangeNotifier = KnMCSDocChangeNotifier.getInstance();
        provInfoUtil = new KnProvInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        provClientIntf = KnProvClientImpl.getInstance();
        pttSettingsUtil = new com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpPTTSettingsUtil();
        /*} catch (KnMessageException e) {
            knLogger.error( "Construtor", "Failed to initialize the Msg FW");
            knLogger.error( "Constructor", e);
            throw new KnSystemError(KnMediatorConstants.ERROR_CODE_INIT_FAILED, "Failed to Initialize XDM Mediator", e);
        }*/
    }

    /**
     * method to return the singleton instance of class
     *
     * @return KnXDMCommonMediator class
     */
    public static KnXDMCommonMediator getInstance() {
        if (!isInitialized) {
            instance = new KnXDMCommonMediator();
            isInitialized = true;
        }
        return instance;
    }

    public void sendResponse(IXDMResponseDTO respDTO, KnMessage receivedMessage, KnException e, String version) {
        String methodName = "sendResponse(KnXDMResDTO, KnException)";
        knLogger.debug(methodName, "response", respDTO, " version ", version);
        if (e != null) {
            respDTO.setResponseCode(e.getErrorCode());
            respDTO.setResponseMessage(e.getErrorMessage());
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        }
        if (version != null && version.matches(com.kodiak.xdms.server.common.resources.KnConstants.VERSION)) {
            knLogger.debug(methodName, "web card version - ", version);
            KnNotificationService.notifyResponse(respDTO, version);
        } else {
            receivedMessage.setUpgrade(true);
            boolean result = sendRespPayLoadToMsgFW(respDTO, receivedMessage, false);
            if (result) {
                knLogger.info(methodName, "Successfully sent the response");
            } else {
                knLogger.error(methodName, "Failed to send the response");
            }
        }

    }

    /**
     * method to send the Failure Response
     *
     * @param respDTO         IXDMResponseDTO
     * @param receivedMessage KnMessage
     * @param e               KnException
     */
    public void sendFailureResponse(IXDMResponseDTO respDTO, KnMessage receivedMessage, KnException e) {
        String methodName = "sendFailureResponse(KnXDMResDTO, KnException)";
        if (e != null) {
            respDTO.setResponseCode(e.getErrorCode());
            respDTO.setResponseMessage(e.getErrorMessage());
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        }
        boolean result = sendRespPayLoadToMsgFW(respDTO, receivedMessage);
        if (result) {
            knLogger.info(methodName, "Successfully sent the response");
        } else {
            knLogger.error(methodName, "Failed to send the response");
        }
    }

    public void genericFailureAsynResponse(IXDMResponseDTO respDTO, KnMessage receivedMessage, Exception e) {
        String methodName = "genericFailureResponse(IXDMResponseDTO, KnMessage, Exception)";
        respDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR);
        respDTO.setResponseMessage(e.getMessage());
        respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        boolean result = sendRespPayLoadToMsgFW(respDTO, receivedMessage, false);
        if (result) {
            knLogger.info(methodName, "Successfully sent the response");
        } else {
            knLogger.error(methodName, "Failed to send the response");
        }
    }

    public void genericFailureResponse(IXDMResponseDTO respDTO, KnMessage receivedMessage, Exception e) {
        String methodName = "genericFailureResponse(IXDMResponseDTO, KnMessage, Exception)";
        respDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR);
        respDTO.setResponseMessage(e.getMessage());
        respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        boolean result = sendRespPayLoadToMsgFW(respDTO, receivedMessage);
        if (result) {
            knLogger.info(methodName, "Successfully sent the response");
        } else {
            knLogger.error(methodName, "Failed to send the response");
        }
    }


    public IXDMResponseDTO getFailureResponse(IXDMResponseDTO respDTO, Exception e) {
        String methodName = "getFailureResponse(IXDMResponseDTO, Exception)";
        if (e instanceof KnException) {
            respDTO.setResponseCode(((KnException) e).getErrorCode());
            respDTO.setResponseMessage(((KnException) e).getErrorMessage());
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        } else {
            respDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR);
            respDTO.setResponseMessage(e.getMessage());
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        }
        knLogger.debug(methodName, "Response DTO  - ", respDTO);
        return respDTO;
    }


    public IXDMResponseDTO getSuccessResponse(IXDMResponseDTO respDTO) {
        String methodName = "getSuccessResponse(IXDMResponseDTO)";
        respDTO.setResponseCode(KnMediatorConstants.SUCCESS_CODE);
        respDTO.setResponseMessage("SUCCESSFULLY executed the operation");
        respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
        knLogger.debug(methodName, "Success Response DTO - ", respDTO);
        return respDTO;
    }

    public void sendSuccessResponse(IXDMResponseDTO respDTO, KnMessage receivedMessage) {
        String methodName = "sendSuccessResponse(KnXDMRespDTO)";
        respDTO.setResponseCode(KnMediatorConstants.SUCCESS_CODE);
        respDTO.setResponseMessage("SUCCESSFULLY executed the operation");
        respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
        boolean result = sendRespPayLoadToMsgFW(respDTO, receivedMessage);

        if (result) {
            knLogger.info(methodName, "Successfully sent the response");
        } else {
            knLogger.error(methodName, "Failed to send the response");
        }
    }


    /**
     * This method posts the response back to Messaging FW to correlate the request
     * This is used only for posting isAlive response back to Health Manager.
     *
     * @param respDTO
     * @param receivedMessage
     */
    public void postSuccessResponse(IXDMResponseDTO respDTO, KnMessage receivedMessage) {
        String methodName = "postSuccessResponse(KnXDMRespDTO)";
        respDTO.setResponseCode(KnMediatorConstants.SUCCESS_CODE);
        respDTO.setResponseMessage("SUCCESSFULLY executed the operation");
        respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
        boolean result = postRespPayLoadToMsgFW(respDTO, receivedMessage);

        if (result) {
            knLogger.debug(methodName, "Successfully sent the response");
        } else {
            knLogger.error(methodName, "Failed to send the response");
        }
        try {
            knLogger.debug(methodName, "Deleting the Msg from the App Interface Table");
            KnMessage message = new KnMessage();
            message.setCorrelationId(receivedMessage.getCorrelationId());
            //message.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            message.setSync(false);
            message.setPayLoad(respDTO);
            message.setFeatureId(receivedMessage.getFeatureId());
            message.setMessageTTL(receivedMessage.getMessageTTL());
            message.setDestQueueName(receivedMessage.getSrcQueueName());
            message.setDestIPAdress(receivedMessage.getSrcIPAddress());
            message.setAmqpMsgType("sync-response");
            message.setRespStatus(KnMessage.RESULT_STATUS.SUCCESS);
            if (!receivedMessage.getSrcQueueName().equals(receivedMessage.getDestQueueName())) {
                knLogger.debug(methodName, "Deleting Round trip Message ...");
                deleteAppIntfMsg(message);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Failed to delete the msg from the App Intf Table");
            knLogger.error(methodName, e);
        }

    }

    /**
     * This method posts the response back to Messaging FW to correlate the request
     * This is used only for posting isAlive response back to Health Manager.
     *
     * @param respDTO
     * @param receivedMessage
     * @return
     */
    public boolean postRespPayLoadToMsgFW(IXDMResponseDTO respDTO, KnMessage receivedMessage) {
        String methodName = "postRespPayLoadToMsgFW(KnXDMRespDTO)";
        knLogger.debug(methodName, "Sending pay load msg");
        knLogger.debug(methodName, "Pay Load received to send to msgFW - ", respDTO);
        boolean requestProcessed = false;
        KnMessage message = new KnMessage();
        message.setCorrelationId(receivedMessage.getCorrelationId());
        //message.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
        message.setSync(false);
        message.setPayLoad(respDTO);
        message.setFeatureId(receivedMessage.getFeatureId());
        message.setMessageTTL(receivedMessage.getMessageTTL());
        message.setDestQueueName(receivedMessage.getSrcQueueName());
        message.setDestIPAdress(receivedMessage.getSrcIPAddress());
        message.setAmqpMsgType("sync-response");
        message.setRespStatus(KnMessage.RESULT_STATUS.SUCCESS);
        message.setSrcExchangeName(receivedMessage.getSrcExchangeName());
        knLogger.debug(methodName, "Message object being sent to the server - ", message);
        try {
            knLogger.debug(methodName, "Src address  ", receivedMessage.getSrcIPAddress(), " Destination add ", receivedMessage.getDestIPAdress());
            if (receivedMessage.getSrcIPAddress().equalsIgnoreCase(receivedMessage.getDestIPAdress())) {
                msgFw.sendMessage(message);
            } else {
                knLogger.debug(methodName, "Message object being sent to the server - ", message);
                msgFw.sendMessage(message);
            }

            requestProcessed = true;
        } catch (KnMessageException e) {
            knLogger.fatal(methodName, "Failed to send message to the Msg FW");
            knLogger.error(methodName, e);
//            throw new KnSystemError(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Failed to send response to msg FW", e);
        } catch (Exception e) {
            knLogger.fatal(methodName, "Failed to send message to the Msg FW");
            knLogger.error(methodName, e);
//            throw new KnSystemError(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Failed to send response to msg FW", e);
        }

        return requestProcessed;
    }

    public boolean sendRespPayLoadToMsgFW(IXDMResponseDTO respDTO, KnMessage receivedMessage) {
        return sendRespPayLoadToMsgFW(respDTO, receivedMessage, true);
    }

    /**
     * @param respDTO
     * @param receivedMessage
     * @return
     */
    public boolean sendRespPayLoadToMsgFW(IXDMResponseDTO respDTO, KnMessage receivedMessage, boolean deleteAppInt) {
        String methodName = "sendRespPayLoadToMsgFW(KnXDMRespDTO)";
        knLogger.debug(methodName, "Sending pay load msg");
        knLogger.debug(methodName, "Pay Load received to send to msgFW - ", respDTO);
        boolean requestProcessed = false;
        try {
            KnMessage message = new KnMessage();
            message.setCorrelationId(receivedMessage.getCorrelationId());
            // message.setMsgType(KnAsyncConstant.MESSAGE_TYPE.OBJECT);
            message.setSync(false);
            message.setPayLoad(respDTO);
            message.setFeatureId(receivedMessage.getFeatureId());
            message.setMessageTTL(receivedMessage.getMessageTTL());
            message.setDestQueueName(receivedMessage.getSrcQueueName());
            message.setDestRoutingKey(receivedMessage.getSrcRoutingKey());
            message.setUpgrade(receivedMessage.isUpgrade());
            message.setSrcExchangeName(receivedMessage.getSrcExchangeName());
            if (deleteAppInt)
                message.setAmqpMsgType("sync-response");
            message.setRespStatus(KnMessage.RESULT_STATUS.SUCCESS);// TODO move from here..
            knLogger.debug(methodName, "Message object being sent to the server - ", message);

            msgFw.sendMessage(message);
            requestProcessed = true;
            /*if (deleteAppInt) {
                knLogger.debug( methodName, "Deleting the Msg from the App Interface Table");
                deleteAppIntfMsg(message);
            }*/
        } catch (KnMessageException e) {
            knLogger.fatal(methodName, "Failed to send message to the Msg FW");
            knLogger.error(methodName, e);
            throw new KnSystemException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Failed to send response to msg FW", e);
        } catch (Exception e) {
            knLogger.fatal(methodName, "Failed to send message to the Msg FW");
            knLogger.error(methodName, e);
            throw new KnSystemException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Failed to send response to msg FW", e);
        }

        return requestProcessed;
    }

    public boolean sendXcapNotification(KnOPDirChgDTO dirChgDTO) {
        String methodName = "sendXcapNotification(KnOPProvDTO)";
        knLogger.debug(methodName, "dir Change DTO :", dirChgDTO);
        return sendXcapNotification(dirChgDTO, null);
    }

    public boolean sendXcapNotification(KnOPDirChgDTO dirChgDTO, String pv) {
        return sendXcapNotification(dirChgDTO, pv, null);
    }

    /**
     * method to send the notification
     *
     * @param dirChgDTO KnOPDirChgDTO
     * @return status of Notification
     */
    public boolean sendXcapNotification(KnOPDirChgDTO dirChgDTO, String pv, KnNotificationParamDTO notificationParamDTO) {
        String methodName = "sendXcapNotification(KnOPProvDTO, String)";
        if (null == dirChgDTO) {
            knLogger.info(methodName, "dir Change DTO is null hence returning false- ");
            return false;
        }
        knLogger.debug(methodName, "dir Change DTO - ", dirChgDTO, " ProtocolVersion :", pv);
        return sendNotification(null, dirChgDTO, pv, notificationParamDTO);

        //populate the KnXcapDiffNotifyDTO
        // Notification requires the following params
        // 1. Dir Uri, 2. Dir prev etag, 3. Dir new etag,
        // 4. Doc Uri, 5. Doc new etag
        /*    KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
  Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<KnXcapDiffDocDTO>();
  Collection<KnOPDocChgDTO> chgDocList = dirChgDTO.getDocChgDTO();
  if (chgDocList != null) {
      for (KnOPDocChgDTO chgDocDTO : chgDocList) {
          KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
          xcapDiffDocDTO.setDocChangeType(chgDocDTO.getDocumentChgType());
          xcapDiffDocDTO.setDocEtag(chgDocDTO.getNewEtag());
          xcapDiffDocDTO.setDocumentSelector(chgDocDTO.getDocUri());
          xcapDocList.add(xcapDiffDocDTO);
      }
  }

  xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
  xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
  xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
  xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
  xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
  xcapDiffNotifyDTO.setProtocolVersion(pv);
  xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
  xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());

  knLogger.debug( methodName, "XCAP Diff Notify DTO - ", xcapDiffNotifyDTO);

  //sending the notification
  KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
  boolean notificationStatus = xcapDiffNotifier.generateDirNotification(xcapDiffNotifyDTO);
  if (notificationStatus) {
      knLogger.debug( methodName, "Successfully sent the notification");
  }

  return notificationStatus;     */

    }

    /**
     * method to send the notification
     *
     * @param dirChgDTO KnOPDirChgDTO
     * @return status of Notification
     */

    public boolean sendProfileNotification(KnProfileNotifyDTO profileNotifyDTO, KnOPDirChgDTO dirChgDTO, String pv) {
        String methodName = "sendProfileNotification(KnProfileNotifyDTO,KnOPProvDTO,String)";

        knLogger.debug(methodName, "profileNotifyDTO -", profileNotifyDTO, "dir Change DTO - ", dirChgDTO, " ProtocolVersion :", pv);
        return sendNotification(profileNotifyDTO, dirChgDTO, pv);
    }

    private boolean sendNotification(KnProfileNotifyDTO profileNotifyDTO, KnOPDirChgDTO dirChgDTO, String pv) {
        return sendNotification(profileNotifyDTO, dirChgDTO, pv, null);
    }

    /**
     * method to send the notification
     *
     * @param dirChgDTO KnOPDirChgDTO
     * @return status of Notification
     */

    private boolean sendNotification(KnProfileNotifyDTO profileNotifyDTO, KnOPDirChgDTO dirChgDTO, String pv, KnNotificationParamDTO notificationParamDTO) {
        String methodName = "sendNotification(KnOPProvDTO,String)";

        KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
        if (profileNotifyDTO != null) {
            xcapDiffNotifyDTO.setProfileNotify(true);
            xcapDiffNotifyDTO.setProfileNotifyDTO(profileNotifyDTO);
        }
        Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<KnXcapDiffDocDTO>();
        Collection<KnOPDocChgDTO> chgDocList = dirChgDTO.getDocChgDTO();
        if (chgDocList != null) {
            for (KnOPDocChgDTO chgDocDTO : chgDocList) {
                KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                xcapDiffDocDTO.setDocChangeType(chgDocDTO.getDocumentChgType());
                xcapDiffDocDTO.setDocEtag(chgDocDTO.getNewEtag());
                xcapDiffDocDTO.setDocumentSelector(chgDocDTO.getDocUri());
                xcapDiffDocDTO.setPushNotifyEnabled(dirChgDTO.isPushNotifyEnabled());
                xcapDiffDocDTO.setVideoPermission(chgDocDTO.getVideoPermission());
                xcapDocList.add(xcapDiffDocDTO);
            }
        }

        xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
        xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
        xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
        xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
        xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
        xcapDiffNotifyDTO.setProtocolVersion(pv);
        xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
        xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
        xcapDiffNotifyDTO.setNtfyOnAnyMDN(dirChgDTO.getNtfyOnAnyMDN());

        knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffNotifyDTO);

        KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        boolean notificationStatus = notifier.sendXcapDiffNotifications(xcapDiffNotifyDTO, notificationParamDTO);

        if (notificationStatus) {
            knLogger.debug(methodName, "Successfully sent the notification");
        }
        return notificationStatus;

    }

    /**
     * This method is used for deletion of data from Application Interface Table tables.
     *
     * @param msgDTO KnMessage
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteAppIntfMsg(KnMessage msgDTO) throws KnDAOException {
        String methodName = "delete(KnMessage)";
        knLogger.debug(methodName, "Entry: delete message - ", msgDTO);
        PreparedStatement pStmt = null;
        Connection conn = null;
        KnPersisterTxn persisterTxn = null;
        String query = null;
        try {
            xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "opening the transaction");
            persisterTxn.open();
            StringBuilder queryBuffer = new StringBuilder(700);
            queryBuffer.append("DELETE FROM ").append(APP_INTF_TABLE_NAME).append(" WHERE ");
            queryBuffer.append(" SRCID=? AND TXNID=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, msgDTO.getDestIPAdress());
            pStmt.setString(2, msgDTO.getCorrelationId());
            knLogger.debug(methodName, "QUERY: Executing - ", query, " with DTO - ", msgDTO);
            int result = pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed - ", result);

            if (result <= 0) {
                knLogger.warn(methodName, "No Record found fro Deletion");
            }
            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();
            knLogger.info(methodName, "EXIT : Deleted entry for message - ", msgDTO);
        } catch (Exception e) {
            if (persisterTxn != null)
                persisterTxn.rollback();
            knLogger.error(methodName, "Exception ... ", e);
            throw new KnDAOException("", e.getMessage(), e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }


    public List<KnBulkOrderInfoDTO> getCompletedBulkOrdersInfo(long expiredtime) {
        String methodName = "getCompletedBulkOrdersInfo()";
        knLogger.debug(methodName, "ENTRY: getCompletedBulkOrdersInfo ");
        KnPersisterTxn persisterTxn = null;
        List<KnBulkOrderInfoDTO> bulkOrderInfoDTOs = new ArrayList<>();
        try {

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the Transaction");
            persisterTxn.open();

            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            bulkOrderInfoDTOs = xdmServerDAO.getCompletedBulkOrdersInfo(expiredtime, persisterTxn);


            persisterTxn.save();


        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction");
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        } finally {
            knLogger.debug(methodName, "EXIT: get Completed Bulk orders Info - ", bulkOrderInfoDTOs);
        }
        return bulkOrderInfoDTOs;
    }

    public int getBulkOrderCount(int bulkOrderId) {
        String methodName = "getCountOfBulkOrder(int)";
        knLogger.debug(methodName, "ENTRY: bulkOrderId ", bulkOrderId);
        KnPersisterTxn persisterTxn = null;
        int bulkOrderCount = 0;
        try {

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the Transaction");
            persisterTxn.open();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            bulkOrderCount = xdmServerDAO.getBulkOrderCount(bulkOrderId, persisterTxn);
            persisterTxn.save();
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction");
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        }
        knLogger.debug(methodName, "EXIT : bulkOrderCount ", bulkOrderCount);
        return bulkOrderCount;
    }


    public KnBulkOrderInfoDTO getCompletedBulkOrderInfo(int bulkOrderID) {
        String methodName = "getCompletedBulkOrderInfo(int)";
        knLogger.entry(methodName, bulkOrderID);
        KnPersisterTxn persisterTxn = null;
        KnBulkOrderInfoDTO bulkOrderInfoDTO = new KnBulkOrderInfoDTO();
        try {

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();

            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            bulkOrderInfoDTO = xdmServerDAO.getCompletedBulkOrderDetail(bulkOrderID, persisterTxn);

            persisterTxn.save();

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            rollback(persisterTxn);
        }
        knLogger.debug(methodName, bulkOrderInfoDTO);
        return bulkOrderInfoDTO;
    }

    public void deleteBulkOrders(List<Integer> bulkOrders) {
        String methodName = "deleteBulkOrders(List<Integer> )";
        knLogger.info(methodName, "ENTRY: deleteBulkOrders ", bulkOrders);
        KnPersisterTxn persisterTxn = null;
        try {

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the Transaction");
            persisterTxn.open();

            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.deleteBulkOrders(bulkOrders, persisterTxn);


            persisterTxn.save();


        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction");
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        } finally {
            knLogger.info(methodName, "EXIT: deleted Bulk orders Info ");
        }
    }


    public void updateBulkOrders(List<Integer> bulkOrders) {
        String methodName = "updateBulkOrders(List<Integer> )";
        knLogger.info(methodName, "ENTRY: updateBulkOrders ", bulkOrders);
        KnPersisterTxn persisterTxn = null;
        try {

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the Transaction");
            persisterTxn.open();

            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.updateBulkOrders(bulkOrders, persisterTxn);


            persisterTxn.save();


        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction");
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            knLogger.error(methodName, e);
            rollback(persisterTxn);
        } finally {
            knLogger.info(methodName, "EXIT: update Bulk orders Info ");
        }
    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    public boolean sendTGSModeChangeNotification(Map<String, KnTGSModeChgDTO> tgsModeChgDTOMap) {
        String methodName = "sendTGSModeChangeNotification(Map<String, KnTGSModeChgDTO>)";
        boolean status = false;
        knLogger.entry(methodName, "tgsModeChgDTOMap", KnGDPRTemplate.mapKeyMdn(tgsModeChgDTOMap));
        if (tgsModeChgDTOMap != null && !tgsModeChgDTOMap.isEmpty()) {
            List<KnSEHNotifyDTO> xcapDiffList = prepareTSGModeCngNotification(tgsModeChgDTOMap);
            notifier.setMaxNotfnsPerJob(3);
            status = notifier.sendSEHNotifications(xcapDiffList);
        } else {
            status = true;
        }
        knLogger.exit(methodName, "status", status);
        return status;
    }

    private List<KnSEHNotifyDTO> prepareTSGModeCngNotification(Map<String, KnTGSModeChgDTO> tgsModeChgDTOMap) {
        String methodName = "prepareTSGModeCngNotification(Map<String, KnTGSModeChgDTO>)";
        List<KnSEHNotifyDTO> sehNotifyDTOList = new ArrayList<>();
        for (Map.Entry<String, KnTGSModeChgDTO> entry : tgsModeChgDTOMap.entrySet()) {
            KnTGSModeChgDTO modeChgDTO = entry.getValue();
            KnTGSModeNotifyDTO modeNotifyDTO = new KnTGSModeNotifyDTO();
            modeNotifyDTO.setPocHome(modeChgDTO.getPocHome());
            modeNotifyDTO.setMdn(entry.getKey());
            modeNotifyDTO.setPresenceHome(modeChgDTO.getPresenceHome());
            // modeNotifyDTO.setScanCap(modeChgDTO.getScanCap());
            modeNotifyDTO.setTgsMode(modeChgDTO.getTgsMode());
            modeNotifyDTO.setAction(com.kodiak.xdms.server.common.resources.KnConstants.MESSAGE_TYPE.TGSC_MODE_CHANGE.value());
            KnSEHNotifyDTO sehNotifyDTO = new KnSEHNotifyDTO();
            sehNotifyDTO.setTgsModeNotify(Boolean.TRUE);
            sehNotifyDTO.setTgsModeNotifyDTO(modeNotifyDTO);
            sehNotifyDTOList.add(sehNotifyDTO);

        }
        knLogger.debug(methodName, "TGSModeNotifications - ", sehNotifyDTOList);
        return sehNotifyDTOList;
    }

    /**
     * @param dirChgDTO KnOPDirChgDTO
     * @return notificationStatus
     */
    public boolean sendPublicNotification(KnOPDirChgDTO dirChgDTO) {

        String methodName = "sendPublicNotification";

        // Populate the KnXcapDiffNotifyDTO
        // Send the notification
        knLogger.debug(methodName, "Populating the Notificaton: DTO : ", dirChgDTO);
        KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();

        Collection<KnXcapDiffDocDTO> diffDocList = new ArrayList<KnXcapDiffDocDTO>();
        Collection<KnOPDocChgDTO> docChgList = dirChgDTO.getDocChgDTO();

        for (KnOPDocChgDTO docChgDTO : docChgList) {
            KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
            xcapDiffDocDTO.setDocChangeType(docChgDTO.getDocumentChgType());
            xcapDiffDocDTO.setDocEtag(docChgDTO.getNewEtag());
            xcapDiffDocDTO.setDocumentSelector(docChgDTO.getDocUri());
            xcapDiffDocDTO.setDocUri(docChgDTO.getEntryUri());
            xcapDiffDocDTO.setVideoPermission(docChgDTO.getVideoPermission());
            diffDocList.add(xcapDiffDocDTO);
        }
        xcapDiffNotifyDTO.setDocDiffObj(diffDocList);
        xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
        xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
        xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
        xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
        xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
        xcapDiffNotifyDTO.setNtfyOnAnyMDN(dirChgDTO.getNtfyOnAnyMDN());
        knLogger.debug(methodName, "Getting the Notifier Instance:");
        KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        knLogger.debug(methodName, "Sending the Notificaton: Diff DTO : ", xcapDiffNotifyDTO);
        boolean notificationStatus = notifier.sendXcapDiffNotifications(xcapDiffNotifyDTO);
        knLogger.debug(methodName, "Notificaton Status :", notificationStatus);

        return notificationStatus;
    }

    public KnLIEventDTO populateContactDTO(String ownerMdn, List<String> addMem, List<String> delMem, String errorCode, String mcpttId) {
        String methodName = "populateContactDTO()";
        KnLIEventDTO eventDTO = new KnLIEventDTO();
        eventDTO.setMdn(ownerMdn);
        eventDTO.setMcpttId(mcpttId);
        if (addMem != null) {
            eventDTO.setAddedMembers(addMem);
        }
        if (delMem != null) {
            eventDTO.setDeletedMembers(delMem);
        }
        eventDTO.setAction(4);
        eventDTO.setDocumentType(1);
        eventDTO.setPttServerId(xdmPttServerId);
        eventDTO.setErrorCode(errorCode);
        knLogger.debug(methodName, "populate the dto", eventDTO);
        return eventDTO;
    }


    public KnLIEventDTO populateGroupDTO(String ownerMdn, List<String> addMem, List<String> delMem, int grpAction, String groupUri,
                                         String errorCode, String mcpttId) {
        String methodName = "populateGroupDTO()";
        KnLIEventDTO eventDTO = new KnLIEventDTO();
        eventDTO.setMdn(ownerMdn);
        eventDTO.setMcpttId(mcpttId);
        eventDTO.setAddedMembers(addMem);
        eventDTO.setDeletedMembers(delMem);
        eventDTO.setDocumentType(2);
        eventDTO.setAction(grpAction);
        eventDTO.setPttServerId(xdmPttServerId);
        eventDTO.setGroupURI(groupUri);
        eventDTO.setErrorCode(errorCode);
        knLogger.debug(methodName, "populate the dto", eventDTO);
        return eventDTO;
    }


    public LinkedList<KnLIEventDTO> liContactLogging(IXDMResponseDTO respDTO, IXDMRequestDTO requestDTO) {
        LinkedList<KnLIEventDTO> eventDTOList = new LinkedList<KnLIEventDTO>();
        String methodName = "liContactLogging";
        KnLIEventDTO eventDTO = null;
        KnPersisterTxn persisterTXn = null;
        try {
            String ownerMdn = null;
            String mcpttId = null;
            List<String> list = new ArrayList<String>();
            List<String> removedList = new ArrayList<String>();
            int operationID = 0;

            persisterTXn = KnPersisterTxn.getPersisterTxn();
            persisterTXn.open();
            
            if (requestDTO instanceof KnXDMContactListInfoDTO) {
                KnXDMContactListInfoDTO contactDTO = (KnXDMContactListInfoDTO) requestDTO;
                ownerMdn = contactDTO.getOwnerMdn();
                operationID = Integer.parseInt(contactDTO.getOperationType());
                for (KnXDMMdnInfoDTO member : contactDTO.getMembers()) {
                    knLogger.debug(methodName, "sending LI NOTIFICATION ...", member);
                    list.add(member.getMdn());
                }
            } else if (requestDTO instanceof KnXDMDynContactInfoDTO) {
                KnXDMDynContactInfoDTO contactDTO = (KnXDMDynContactInfoDTO) requestDTO;
                ownerMdn = contactDTO.getOwnerMdn();
                mcpttId=contactDTO.getMcpttId();
                operationID = Integer.parseInt(contactDTO.getOperationType());
                if (contactDTO.getRemovedContList() != null) {
                    removedList.addAll(contactDTO.getRemovedContList());
                }
                if (contactDTO.getContactsList() != null) {
                    for (KnXDMMdnInfoDTO member : contactDTO.getContactsList()) {
                        list.add(member.getMdn());
                    }
                }
            }
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(ownerMdn, PUBLIC_PROFILE,
                    false, persisterTXn);
            
            persisterTXn.save();
            knLogger.debug(methodName, "mdnMcpptId ", subscProfile);
            mcpttId = subscProfile.getMcpttId();

            boolean flag = KnLIEventHandler.isTargetMDN(ownerMdn);
            // Sending LI NOTIFICATION ..
            if (flag&&mcpttId!=null) {
                if (operationID == KnConstants.OPERATION_ID_ADD_CONTACTS) {
                    eventDTO = populateContactDTO(ownerMdn, list, null, respDTO.getResponseCode(),mcpttId);
                    knLogger.debug(methodName, "event DTO ", eventDTO);
                } else if (operationID == KnConstants.OPERATION_ID_DELETE_CONTACTS) {
                    eventDTO = populateContactDTO(ownerMdn, null, list, respDTO.getResponseCode(),mcpttId);
                } else if (operationID == KnConstants.OPERATION_ID_MODIFY_DYNAMIC_CONTACT) {
                    eventDTO = populateContactDTO(ownerMdn, list, removedList, respDTO.getResponseCode(),mcpttId);
                }
                eventDTOList.add(eventDTO);
                //send the dto .as of LI Notification . ...
                knLogger.info(methodName, "Send of LI NOTIFICATION ... event DTO ", eventDTO);
            } else {
                knLogger.info(methodName, "Skip of send LI NOTIFICATION ...");
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            rollback(persisterTXn);
        }
        return eventDTOList;
    }


    public LinkedList<KnLIEventDTO> liGroupLogging(IXDMResponseDTO respDTO, KnXDMGroupInfoDTO groupDTO) {
        String methodName = "liGroupLogging";
        knLogger.debug(methodName, "enter into liGroupLogging ...& groupDTO ", groupDTO);
        boolean flag = KnLIEventHandler.isTargetMDN(groupDTO.getOwnerMdn());
        // Sending LI NOTIFICATION ..
        List<String> memberlist = new ArrayList<String>();
        LinkedList<KnLIEventDTO> eventDTOList = new LinkedList<KnLIEventDTO>();
        String mcpttId = null;
        try {
            KnPersisterTxn localPersisterTxn = KnPersisterTxn.getPersisterTxn();
            try{
                localPersisterTxn.open();
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(groupDTO.getOwnerMdn(), PUBLIC_PROFILE,
                        false, localPersisterTxn);
                localPersisterTxn.save();
                knLogger.debug(methodName, "mdnMcpptId12", subscProfile);
                mcpttId = subscProfile.getMcpttId();
            } catch (Exception e) {
                knLogger.error(methodName, "Exception ", e," getTransactionStatus :",localPersisterTxn.getTransactionStatus());
                try {
                    localPersisterTxn.rollback();
                } catch (KnPersistenceException ex) {
                    knLogger.error(methodName, "Exception txn rolled back ", ex);
                }
            }
            if (flag&&mcpttId!=null) {
            if (Integer.parseInt(groupDTO.getOperationType()) != KnConstants.OPERATION_ID_DELETE_GROUP) {
                for (KnXDMGroupMdnInfoDTO member : groupDTO.getGrpMembers()) {
                    memberlist.add(member.getMdn());
                }
            }
            KnLIEventDTO eventDTO = new KnLIEventDTO();
            //addGroup Member ..
            if (Integer.parseInt(groupDTO.getOperationType()) == KnConstants.OPERATION_ID_ADD_GROUP_MEMBERS) {
                knLogger.debug(methodName, "Send of LI NOTIFICATION ... for operation ADD_GROUP_MEMBERS ");
                eventDTO = populateGroupDTO(groupDTO.getOwnerMdn(), memberlist, null, 2, constructGroupUri(groupDTO.getOwnerMdn(), groupDTO.getGroupName()), respDTO.getResponseCode(),mcpttId);
            } else if (Integer.parseInt(groupDTO.getOperationType()) == KnConstants.OPERATION_ID_DELETE_GROUP_MEMBERS) {
                // for delete group member group Action should be modify ie. 2.
                knLogger.debug(methodName, "Send of LI NOTIFICATION ... for operation DELETE_GROUP_MEMBERS ");
                eventDTO = populateGroupDTO(groupDTO.getOwnerMdn(), null, memberlist, 2, constructGroupUri(groupDTO.getOwnerMdn(), groupDTO.getGroupName()), respDTO.getResponseCode(),mcpttId);
            } else if (Integer.parseInt(groupDTO.getOperationType()) == KnConstants.OPERATION_ID_DELETE_GROUP) {
                knLogger.debug(methodName, "Send of LI NOTIFICATION ... for operation DELETE_GROUP");
                eventDTO = populateGroupDTO(groupDTO.getOwnerMdn(), null, null, 3, constructGroupUri(groupDTO.getOwnerMdn(), groupDTO.getGroupName()), respDTO.getResponseCode(),mcpttId);
            } else if (Integer.parseInt(groupDTO.getOperationType()) == KnConstants.OPERATION_ID_CREATE_GROUP) {
                knLogger.debug(methodName, "Send of LI NOTIFICATION ... for operation CREATE_GROUP ");
                eventDTO = populateGroupDTO(groupDTO.getOwnerMdn(), memberlist, null, 1, constructGroupUri(groupDTO.getOwnerMdn(), groupDTO.getGroupName()), respDTO.getResponseCode(),mcpttId);
            } else if (Integer.parseInt(groupDTO.getOperationType()) == KnConstants.OPERATION_ID_MODIFY_GROUP) {
                knLogger.debug(methodName, "Send of LI NOTIFICATION ... for operation MODIFY_GROUP ");
                eventDTOList.add(populateGroupDTO(groupDTO.getOwnerMdn(), null, null, 3, constructGroupUri(groupDTO.getOwnerMdn(), groupDTO.getGroupName()), respDTO.getResponseCode(),mcpttId));
                eventDTO = populateGroupDTO(groupDTO.getOwnerMdn(), memberlist, null, 1, constructGroupUri(groupDTO.getOwnerMdn(), groupDTO.getGroupName()), respDTO.getResponseCode(),mcpttId);
            }
            eventDTOList.add(eventDTO);
            //send the dto .as of LI Notification . ...
            knLogger.debug(methodName, "Send of LI NOTIFICATION ... of eventDTOList ", eventDTOList);
            //  KnLIEventHandler.logLItEvent(eventDTOList);
            } else {
                knLogger.debug(methodName, "Skip of send LI NOTIFICATION ...");
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
        }
        return eventDTOList;
    }


    private String constructGroupUri(String mdn, String groupName) {
        String methodName = "constructGroupUri(String, String)";
        knLogger.debug(methodName, "generating List service uri for mdn - " + KnGDPRTemplate.mdn(mdn));
        StringBuffer stringBuffer = new StringBuffer(100);
        stringBuffer.append("tel:+");
        stringBuffer.append(mdn);
        stringBuffer.append(";org.openmobilealliance.groups=");
        stringBuffer.append(groupName);
        knLogger.debug(methodName, "generated List service uri for mdn - " +
        		KnGDPRTemplate.mdn(mdn) + " is - " + stringBuffer.toString());
        return stringBuffer.toString();

    }

    public String getXdmPttServerId() {
        return xdmPttServerId;
    }


    public LinkedList<KnLIEventDTO> getEventDTOList(String ownerMdn, String operationType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEventDTOList()";
        String xdmServerId = getXdmPttServerId();
        boolean flag = KnLIEventHandler.isTargetMDN(ownerMdn);
        LinkedList<KnLIEventDTO> eventDTOList = new LinkedList<KnLIEventDTO>();
        KnLIEventDTO eventDTO = null;
        if (flag) {
            //LI Changes ...
        	KnSubsProfileDTO subscProfile = new KnSubsProfileDTO();
			try {
				subscProfile = commonInfoUtil.getProfileDetails(ownerMdn, PUBLIC_PROFILE,
				        false, persisterTxn);
			} catch (KnCorpBOException e) {
				// TODO Auto-generated catch block
				knLogger.debug(methodName, "Exception is ",e);
			}
        	String mcpttId = subscProfile.getMcpttId();
            List<String> ownerMdnList = new ArrayList<>();
            ownerMdnList.add(ownerMdn);
            KnPOCContactListDAO pocContactListDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPOCContactListDAO(xdmServerId);
            Map<String, Collection<String>> contactListIds = pocContactListDAO.getContactMDNsForMDNs(ownerMdnList, false, persisterTxn);
            ArrayList memberMdnList = new ArrayList();
            if (contactListIds != null) {
                for (Map.Entry<String, Collection<String>> entry : contactListIds.entrySet()) {
                    memberMdnList = (ArrayList) entry.getValue();
                }
                knLogger.debug(methodName, " members mdn list ", KnGDPRTemplate.mdnList(memberMdnList));
                if (operationType.equalsIgnoreCase(KnProvOperationTypes.UPDATE_SUBSCRIBER)) {
                    eventDTO = populateContactDTO(ownerMdn, null,
                            memberMdnList, KnMediatorConstants.SUCCESS_CODE,mcpttId);
                }
                eventDTOList.add(eventDTO);
                //send the dto .as of LI Notification . ...
                knLogger.debug(methodName, " send LI NOTIFICATION for  contacts ...Contact event DTO ", eventDTO);
            }
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            Collection<String> listServiceUris = xdmServerDAO.getAllListServiceUrisForMdn(ownerMdn, persisterTxn);
            // LI Notification ..  for groups
            //to do .. (get the Target MDN from LI UTITILY )
            KnLIEventDTO eventgrpDTO = null;
            for (String uri : emptyIfNull(listServiceUris)) {
                if (operationType.equalsIgnoreCase(KnProvOperationTypes.UPDATE_SUBSCRIBER)) {
                    eventgrpDTO = populateGroupDTO(ownerMdn, null, null, 3, uri,
                            KnMediatorConstants.SUCCESS_CODE,mcpttId);
                }
                //send the dto .as of LI Notification . ...
                knLogger.debug(methodName, " send LI NOTIFICATION for  groups ...Group Event DTO", eventgrpDTO);
                eventDTOList.add(eventgrpDTO);
            }
            knLogger.info(methodName, "sending LI NOTIFICATION ...event DTO List ", eventDTOList);
        } else {
            knLogger.info(methodName, "Skip of send LI NOTIFICATION ...");
        }
        return eventDTOList;
    }

    private static Collection<String> emptyIfNull(Collection<String> other) {
        return other == null ? Collections.EMPTY_LIST : other;
    }

    public Collection<KnXcapDiffDirChgNotifyDTO> prepareNotification(KnCorpResponseDTO respDto) {
        String methodName = "prepareNotifications(KnCorpResponseDTO)";
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = new ArrayList<>();
        if (respDto.getOnlineMdnList() != null && !respDto.getOnlineMdnList().isEmpty()) {
            removeOffLineMdns(respDto);
        }
        Map<String, KnOPDirChgDTO> changeLogMap = respDto.getChangeLogMap();
        int isAbdgGroup_t = 0;
        if (changeLogMap != null) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                KnOPDirChgDTO dirChgDTO = entry.getValue();
                List<KnXcapDiffDocDTO> diffDocList = new ArrayList<>();
                if (null != dirChgDTO) {
                    Collection<KnOPDocChgDTO> doclist = dirChgDTO.getDocChgDTO();
                    if (doclist != null) {
                        for (KnOPDocChgDTO docDto : doclist) {
                            KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                            xcapDiffDocDTO.setDocumentSelector(docDto.getDocUri());
                            xcapDiffDocDTO.setDocUri(docDto.getEntryUri());
                            xcapDiffDocDTO.setDocChangeType(docDto.getDocumentChgType());
                            xcapDiffDocDTO.setDocEtag(docDto.getNewEtag());
                            // Added the setting of the parameters needed for the
                            // xcap doc diff notifications
                            Collection<KnSubscriberDTO> addedContactList = docDto.getAddedContactList();
                            int addContLstSize = 0;
                            if (addedContactList != null) {
                                addContLstSize = addedContactList.size();
                            }
                            Collection<String> deletedContactList = docDto.getRemovedContactList();
                            int delContLstSize = 0;
                            if (deletedContactList != null) {
                                delContLstSize = deletedContactList.size();
                            }
                            if (addContLstSize + delContLstSize <= DIFF_SIZE) {
                                if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                    xcapDiffDocDTO.setAddedContactList(addedContactList);
                                    xcapDiffDocDTO.setRemovedContactList(deletedContactList);
                                }
                            }
                            xcapDiffDocDTO.setGroupName(docDto.getGroupName());
                            xcapDiffDocDTO.setOsmListChanged(docDto.isOsmListChanged());
                            xcapDiffDocDTO.setGroupMemCount(docDto.getGroupMemCount());
                            xcapDiffDocDTO.setAvatar(docDto.getAvatar());
                            if (docDto.getIsAbdgGroup() != null && docDto.getIsAbdgGroup() != 0) {
                                isAbdgGroup_t = docDto.getIsAbdgGroup();
                            }
                            xcapDiffDocDTO.setIsAbdgGroup(docDto.getIsAbdgGroup());
                            xcapDiffDocDTO.setIsMcxGroup(docDto.getIsMcxGroup());
                            xcapDiffDocDTO.setIsPreConfigGroup(docDto.getIsPreConfigGroup());
                            xcapDiffDocDTO.setExternalCorpGroup(docDto.getExternalCorpGroup());
                            xcapDiffDocDTO.setVideoPermission(docDto.getVideoPermission());

                            Collection<KnSubscriberDTO> addedGrpMemList = docDto.getAddedGroupMembers();
                            int addGrpMemLstSize = 0;
                            if (addedGrpMemList != null) {
                                addGrpMemLstSize = addedGrpMemList.size();
                            }
                            Collection<String> deletedGrpMemList = docDto.getRemovedGroupMembers();
                            int delGrpMemLstSize = 0;
                            if (deletedGrpMemList != null) {
                                delGrpMemLstSize = deletedGrpMemList.size();
                            }
                            Collection<KnSubscriberDTO> modGrpMemList = docDto.getModifiedGrpMembers();
                            int modGrpMemLstSize = 0;
                            if (modGrpMemList != null) {
                                modGrpMemLstSize = modGrpMemList.size();
                            }

                            if (addGrpMemLstSize + delGrpMemLstSize + modGrpMemLstSize <= DIFF_SIZE) {
                                if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                    xcapDiffDocDTO.setAddedGroupMembers(addedGrpMemList);
                                    xcapDiffDocDTO.setRemovedGroupMembers(deletedGrpMemList);
                                    xcapDiffDocDTO.setModifiedGrpMembers(modGrpMemList);
                                }
                            }
                            Collection<KnSubscriberDTO> modContactList = docDto.getModifiedContactMembers();
                            int modContLstSize = 0;
                            if (modContactList != null) {
                                modContLstSize = modContactList.size();
                            }
                            if (modContLstSize <= DIFF_SIZE) {
                                if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                    xcapDiffDocDTO.setModifiedContactList(docDto.getModifiedContactMembers());
                                }
                            }

                            Collection<KnTargetPermsInfoDTO> addedTargetList = docDto.getAddedTargetList();
                            int addedTargetLstSize = 0;
                            if (addedTargetList != null) {
                                addedTargetLstSize = addedTargetList.size();
                            }
                            Collection<KnTargetPermsInfoDTO> modifiedTargetList = docDto.getModifiedTargetList();
                            int modTargetLstSize = 0;
                            if (modifiedTargetList != null) {
                                modTargetLstSize = modifiedTargetList.size();
                            }
                            Collection<String> removedTargetList = docDto.getRemovedTargetList();
                            int removedTargetLstSize = 0;
                            if (removedTargetList != null) {
                                removedTargetLstSize = removedTargetList.size();
                            }
                            if (addedTargetLstSize + modTargetLstSize + removedTargetLstSize <= DIFF_SIZE) {
                                if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                    xcapDiffDocDTO.setAddedTargetList(addedTargetList);
                                    xcapDiffDocDTO.setModifiedTargetList(modifiedTargetList);
                                    xcapDiffDocDTO.setRemovedTargetList(removedTargetList);
                                }
                            }

                            Collection<KnEmergencyInfoDTO> addedEmergDest = docDto.getAddedDestList();
                            int addedEmergDestSize = 0;
                            if (addedEmergDest != null) {
                                addedEmergDestSize = addedEmergDest.size();
                            }
                            Collection<KnEmergencyInfoDTO> updatedEmergDest = docDto.getModifiedDestList();
                            int updatedEmergDestSize = 0;
                            if (updatedEmergDest != null) {
                                updatedEmergDestSize = updatedEmergDest.size();
                            }
                            Collection<String> deletedEmergDest = docDto.getRemovedDestList();
                            int removedEmergDestSize = 0;
                            if (deletedEmergDest != null) {
                                removedEmergDestSize = deletedEmergDest.size();
                            }
                            if (addedEmergDestSize + updatedEmergDestSize + removedEmergDestSize <= DIFF_SIZE) {
                                if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                    xcapDiffDocDTO.setAddedDestList(addedEmergDest);
                                    xcapDiffDocDTO.setModifiedDestList(updatedEmergDest);
                                    xcapDiffDocDTO.setRemovedDestList(deletedEmergDest);
                                    xcapDiffDocDTO.setAddedEmergAttributes(docDto.getAddedEmerAttributes());
                                    xcapDiffDocDTO.setModifiedEmergAttributes(docDto.getModifiedEmerAttributes());
                                    xcapDiffDocDTO.setRemovedEmergAttributes(docDto.getRemovedEmerAttributes());
                                }
                            }

                            Collection<KnXDMAddlTalkGroupInfoDTO> addedAddlTGList = docDto.getAddedAddlTGList();
                            int addedAddlTGListSize = 0;
                            if (addedAddlTGList != null) {
                                addedAddlTGListSize = addedAddlTGList.size();
                            }
                            Collection<KnXDMAddlTalkGroupInfoDTO> modifiedDestList = docDto.getModifiedAddlTGList();
                            int modifiedDestListSize = 0;
                            if (modifiedDestList != null) {
                                modifiedDestListSize = modifiedDestList.size();
                            }
                            Collection<KnXDMAddlTalkGroupInfoDTO> deletedAddlTGList = docDto.getRemovedAddlTGList();
                            int deletedAddlTGListSize = 0;
                            if (deletedAddlTGList != null) {
                                deletedAddlTGListSize = deletedAddlTGList.size();
                            }
                            if (addedAddlTGListSize + modifiedDestListSize + deletedAddlTGListSize <= DIFF_SIZE) {
                                if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                    xcapDiffDocDTO.setAddedAddlTGList(addedAddlTGList);
                                    xcapDiffDocDTO.setModifyAddlTGList(modifiedDestList);
                                    xcapDiffDocDTO.setRemovedAddlTGList(deletedAddlTGList);
                                }
                            }
                            xcapDiffDocDTO.setPrevDocEtag(docDto.getPrevEtag());
                            diffDocList.add(xcapDiffDocDTO);
                        }
                    }
                    KnXcapDiffDirChgNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffDirChgNotifyDTO();
                    xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                    Collections.sort(diffDocList);
                    xcapDiffNotifyDTO.setDocDiffObj(diffDocList);
                    xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
                    xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                    xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                    xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                    xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                    xcapDiffNotifyDTO.setNotfnCapability(dirChgDTO.isNotfnCapability());
                    xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
                    xcapDiffNotifyDTO.setPushNotifyEnabled(dirChgDTO.isPushNotifyEnabled());
                    xcapDiffNotifyDTO.setNtfyOnAnyMDN(dirChgDTO.getNtfyOnAnyMDN());
                    if (isAbdgGroup_t == KnMediatorConstants.ABDG_Enable && dirChgDTO.getClientType() != KnMediatorConstants.DISP_CLIENT_TYPE) {
                        xcapDiffNotifyDTO.setNtfyOnAnyMDN(1);
                    }
                    xcapDiffList.add(xcapDiffNotifyDTO);
                }
            }
        }
        knLogger.debug(methodName, "xcapNotifications - ", xcapDiffList);
        return xcapDiffList;
    }
// Sent by sanjiv A for notification

    /**
     * Method to initiate the micro service notification job.
     *
     * @param notifyDtoList
     * @throws KnException
     */
 /*   public void notifyMicroServices(List<? extends KnEXDMSNotifyDto> notifyDtoList) throws KnException {
        String methodName = "notifyMicroServices()";
        try {
            knLogger.info(methodName, "Entry");
            knLogger.debug(methodName, "Entry - ", notifyDtoList);
            List<KnMicroServiceNotifyJob> jobList = new ArrayList<>();
            KnRMQInfoDto rmqInfoDto = null; //todo get the rmq information from cache
            for (KnEXDMSNotifyDto notifyDto : notifyDtoList) {
                KnMicroServiceNotifyJob job = new KnMicroServiceNotifyJob(notifyDto, rmqInfoDto);
                jobList.add(job);
            }
            scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);
            knLogger.debug(methodName, "Exist, job submitted");
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occured", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occured while starting job", e);
        }
    }
*/

    /**
     * Method to initiate the micro service notification job.
     *
     * @param notifyDtoList
     * @throws KnException
     */
    public void startNotifyMicroServicesJob(List<? extends KnEXDMSNotifyDto> notifyDtoList) throws KnException {
        String methodName = "startNotifyMicroServicesJob(List)";
        try {
            knLogger.info(methodName, "Entry");
            knLogger.debug(methodName, "Entry - ", notifyDtoList);
            if (notifyDtoList != null && !notifyDtoList.isEmpty()) {
                knLogger.info(methodName, "notifyDtoList size:",notifyDtoList.size());
                List<KnMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnEXDMSNotifyDto notifyDto : notifyDtoList) {
                    KnMicroServiceNotifyJob job = new KnMicroServiceNotifyJob(notifyDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);
                knLogger.info(methodName, "Exit, job submitted");
            }
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occured", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occured while starting job", e);
        }
    }


    /**
     * Method to initiate the micro service notification job for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @throws KnException
     */
    public void startNotifyMicroServicesJob(Map<String, KnOPDirChgDTO> changeLogMap, int corpId) throws KnException {
        String methodName = "startNotifyMicroServicesJob(Map)";
        try {
            knLogger.info(methodName, "Entry");
            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                List<KnCorpEXDMSNotifyDto> notifyDtoList = getModifiedNotifyJson(changeLogMap, corpId, null, null, null,null,null);
                knLogger.debug(methodName, "notifyDtoList - ", notifyDtoList);
                knLogger.info(methodName, "notifyDtoList size:",notifyDtoList.size());
                List<KnMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnCorpEXDMSNotifyDto corpEXDMSNotifyDto : notifyDtoList) {
                    KnMicroServiceNotifyJob job = new KnMicroServiceNotifyJob(corpEXDMSNotifyDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);
                knLogger.debug(methodName, "Exist, job submitted");
            }
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occured", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occured while starting job", e);
        }
    }

    /**
     * Method to initiate the micro service notification job for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @throws KnException
     */
    public void startNotifyMicroServicesJob(Map<String, KnOPDirChgDTO> changeLogMap, int corpId,
                                            int createdBy, Integer lmrIntropFlag, Integer mcxInd) throws KnException {
        String methodName = "startNotifyMicroServicesJob(Map<String>)";
        try {
            knLogger.info(methodName, "Entry");
            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                List<KnCorpEXDMSNotifyDto> notifyDtoList = getModifiedNotifyJson(changeLogMap, corpId, createdBy, lmrIntropFlag, mcxInd, null,null);
                knLogger.debug(methodName, "notifyDtoList - ", notifyDtoList);
                knLogger.info(methodName, "notifyDtoList size - ", notifyDtoList.size());

                List<KnMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnCorpEXDMSNotifyDto corpEXDMSNotifyDto : notifyDtoList) {
                    KnMicroServiceNotifyJob job = new KnMicroServiceNotifyJob(corpEXDMSNotifyDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);
                knLogger.debug(methodName, "Exist, job submitted");
            }
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occured", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occured while starting job", e);
        }
    }

    /**
     * Method to initiate the micro service notification job for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @throws KnException
     */
    public void startNotifyMicroServicesJob(Map<String, KnOPDirChgDTO> changeLogMap, int corpId,
                                            int createdBy, Integer lmrIntropFlag, Integer mcxInd, Integer oldLmr) throws KnException {
        String methodName = "startNotifyMicroServicesJob(Map<String>)";
        try {
            knLogger.info(methodName, "Entry");
            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                List<KnCorpEXDMSNotifyDto> notifyDtoList = getModifiedNotifyJson(changeLogMap, corpId, createdBy, lmrIntropFlag, mcxInd, null, null, oldLmr, null);
                knLogger.debug(methodName, "notifyDtoList - ", notifyDtoList);
                knLogger.info(methodName, "notifyDtoList size - ", notifyDtoList.size());

                List<KnMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnCorpEXDMSNotifyDto corpEXDMSNotifyDto : notifyDtoList) {
                    KnMicroServiceNotifyJob job = new KnMicroServiceNotifyJob(corpEXDMSNotifyDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);
                knLogger.info(methodName, "Exit, job submitted");
            }
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occured", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occured while starting job", e);
        }
    }

    /**
     * Method to initiate the micro service notification job for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @throws KnException
     */
    public void startNotifyMicroServicesJobModifyGroup(Map<String, KnOPDirChgDTO> changeLogMap, int corpId, int createdBy, Integer
            lmrIntropFlag, Integer mcxInd, Map<Integer, KnCorpGroupInfoDTO> groupSharedMap, String grpSIPUri,
                                                       Integer oldLmr, String recordingFs) throws KnException {
        String methodName = "startNotifyMicroServicesJobModifyGroup()";
        try {
            knLogger.info(methodName, "Entry recordingFs:", recordingFs);
            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                List<KnCorpEXDMSNotifyDto> notifyDtoList = getModifiedNotifyJson(changeLogMap, corpId, createdBy,
                        lmrIntropFlag, mcxInd, groupSharedMap, grpSIPUri, oldLmr, recordingFs);
                knLogger.debug(methodName, "notifyDtoList - ", notifyDtoList);
                knLogger.info(methodName, "notifyDtoList size:",notifyDtoList.size());

                List<KnMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnCorpEXDMSNotifyDto corpEXDMSNotifyDto : notifyDtoList) {
                    KnMicroServiceNotifyJob job = new KnMicroServiceNotifyJob(corpEXDMSNotifyDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);
                knLogger.debug(methodName, "Exist, job submitted");
            } else if (groupSharedMap != null && !groupSharedMap.isEmpty()) {
                List<KnCorpEXDMSNotifyDto> notifyDtoList = getGroupSharedModifiedNotifyJson(corpId, createdBy,
                        lmrIntropFlag, mcxInd, groupSharedMap, grpSIPUri, recordingFs,oldLmr);
                knLogger.debug(methodName, "notifyDtoList - ", notifyDtoList);
                knLogger.info(methodName, "notifyDtoList size:", notifyDtoList.size());
                    List<KnMicroServiceNotifyJob> jobList = new ArrayList<>();
                    for (KnCorpEXDMSNotifyDto corpEXDMSNotifyDto : notifyDtoList) {
                        KnMicroServiceNotifyJob job = new KnMicroServiceNotifyJob(corpEXDMSNotifyDto);
                        jobList.add(job);
                    }
                    scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);
                    knLogger.debug(methodName, "Exist, job submitted");

            } else {
                knLogger.debug(methodName, "Exit, no job submitted");
            }
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occurred", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occurred while starting job", e);
        }
    }
    /**
     * Method to get the group diff data from the change log map for sending micro services notification for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @return
     */
    private static List<KnCorpEXDMSNotifyDto> getModifiedNotifyJson(Map<String, KnOPDirChgDTO> changeLogMap, int corpId,
                                                                    Integer createdBy, Integer lmrIntrop, Integer mcxInd,
                                                                    Map<Integer, KnCorpGroupInfoDTO> groupSharedMap,
                                                                    String grpSIPUri) {
        final String methodName = "getModifiedNotifyJson()";
        knLogger.info(methodName, "Entry grpSIPUri:",grpSIPUri," lmrIntrop:",lmrIntrop);
        knLogger.debug(methodName, KnGDPRTemplate.mapKeyMdn(changeLogMap));
        List<KnCorpEXDMSNotifyDto> corpEXDMSNotifyDtoList = new ArrayList<>();
        Map<Integer, KnCorpEXDMSNotifyDto> modifiedMap = new HashMap<>();
        Map<Integer, KnCorpEXDMSNotifyDto> deletedMap = new HashMap<>();
        Map<String, KnCorpEXDMSNotifyDto> modifiedContactMap = new HashMap<>();
        Map<Integer, Integer> groupTypeMap = getGroupTypeMapSafely(changeLogMap);
        if (changeLogMap != null && !changeLogMap.isEmpty()) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                knLogger.debug(methodName, "MDN - ", KnGDPRTemplate.mdn(entry.getKey()));
                String mdn = entry.getKey();
                KnOPDirChgDTO opDirChgDTO = entry.getValue();
                if (null != opDirChgDTO) {
                    Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                    knLogger.debug(methodName, "changeDocList - ", changeDocList);
                    for (KnOPDocChgDTO docChgDTO : changeDocList) {
                        knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                        //If the document type is corporate group
                        if (docChgDTO.getDocUri().contains(KnConstants.APP_UID_CORP_GROUP)) {
                            Integer resolvedGroupType = groupTypeMap.get(docChgDTO.getGroupId());
                            if (resolvedGroupType == null) {
                                resolvedGroupType = docChgDTO.getGroupType();
                            }

                            KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = null;
                            if ((docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value())
                                    && !(modifiedMap.containsKey(docChgDTO.getGroupId()))) {
                                //A group can get deleted by some other operations like modify/delete sublist.
                                //In this flow delete group notification will be send out to all the existing members. No replace notify will be available for those groups
                                //But a broadcast group may have only remove notification though the group exist which is not possible for other group type.
                                //Since broadcast group notify does not have diff data, microservices will be notified with previous etag as -1 so that they can fetch the group details.
                                knLogger.debug(methodName, " Remove doc type for - ", docChgDTO.getGroupId());
                                corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                                corpEXDMSNotifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setGrpSIPUri(KnGenInfoUtil.mcpttGroupUri(docChgDTO.getGroupId(), corpId, grpSIPUri));
                                corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setGrpName(docChgDTO.getGroupDisplayName());
                                corpEXDMSNotifyDto.setCorpid(corpId);
                                corpEXDMSNotifyDto.setGrpType(resolvedGroupType);
                                if (createdBy != null) corpEXDMSNotifyDto.setCreatedBy(createdBy);
                                corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                                corpEXDMSNotifyDto.setLmrInteropFlag(lmrIntrop);
                                if (mcxInd != null) {
                                    corpEXDMSNotifyDto.setGrpCategoryInd(mcxInd);
                                }
                                corpEXDMSNotifyDto.setLargeGroup(docChgDTO.isLargeGroup());
                                corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                                //MINT-23959 , deleteGroup event will be sent only in deleteGroup API call
                                //corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                                //corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                                corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                                corpEXDMSNotifyDto.setOsmIdChanged(docChgDTO.isOsmListChanged());
                                if (resolvedGroupType == 3) {
                                    knLogger.debug(methodName, "Broadcast group type");
                                    corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                                    corpEXDMSNotifyDto.setPreviousEtag(-1);
                                    modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                                } else {
                                    knLogger.debug(methodName, " Non Broadcast - ", resolvedGroupType);
                                    //corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                                    deletedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                                }

                            } else if (docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                                knLogger.debug(methodName, "Replace doc type for - ", docChgDTO.getGroupId());
                                // group modified populating for group modified data
                                corpEXDMSNotifyDto = modifiedMap.get(docChgDTO.getGroupId());
                                if (null == corpEXDMSNotifyDto) {
                                    corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                                }
                                deletedMap.remove(docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setGrpType(resolvedGroupType);
                                corpEXDMSNotifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setGrpSIPUri(KnGenInfoUtil.mcpttGroupUri(docChgDTO.getGroupId(), corpId, grpSIPUri));
                                corpEXDMSNotifyDto.setLmrInteropFlag(lmrIntrop);
                                if (createdBy != null) corpEXDMSNotifyDto.setCreatedBy(createdBy);
                                corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                                corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                                corpEXDMSNotifyDto.setCorpid(corpId);
                                corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                                corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                                corpEXDMSNotifyDto.setGrpName(docChgDTO.getGroupDisplayName());
                                if (groupSharedMap != null) {
                                    if (groupSharedMap.get(docChgDTO.getGroupId()) != null) {
                                        corpEXDMSNotifyDto.setGrpShare(groupSharedMap.get(docChgDTO.getGroupId()).getGrpShared());
                                    }
                                }
                                corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                                corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setOsmIdChanged(docChgDTO.isOsmListChanged());
                                if (resolvedGroupType == 3) {
                                    knLogger.debug(methodName, " Broadcast group ");
                                    corpEXDMSNotifyDto.setPreviousEtag(-1);
                                }
                                if (mcxInd != null) {
                                    corpEXDMSNotifyDto.setGrpCategoryInd(mcxInd);
                                }
                                Collection<KnSubscriberDTO> addedGroupMembers = docChgDTO.getAddedGroupMembers();
                                Collection<KnSubscriberDTO> addedEmptyGroupMembers = docChgDTO.getAddedEmptyGroupMembers();
                                knLogger.debug(methodName, " addedEmptyGroupMembers :", addedEmptyGroupMembers);
                                knLogger.debug(methodName, " addedGroupMembers :", addedGroupMembers);
                                Collection<KnSubscriberDTO> addGrpMems = null;
                                if (addedGroupMembers != null && !addedGroupMembers.isEmpty()) {
                                    addGrpMems = addedGroupMembers;
                                }
                                if (addedEmptyGroupMembers != null && !addedEmptyGroupMembers.isEmpty()) {
                                    addGrpMems = addedEmptyGroupMembers;
                                }

                                if (addGrpMems != null && !addGrpMems.isEmpty()) {
                                    knLogger.debug(methodName, " addGrpMems - ", addGrpMems);
                                    List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
                                    List<String> addedDistMem = new ArrayList<>();
                                    for (KnSubscriberDTO dto : addGrpMems) {
                                        KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                        member.setMdn(dto.getMdn());
                                        if (dto.getClientType() != 0) {
                                            member.setClientType(dto.getClientType());
                                        }
                                        member.setSupervisor(dto.getSupervisory());
                                        member.setName(dto.getNetworkName());
                                        if (dto.getLocWatcher() == 1) {
                                            member.setIsLocWatcher(Boolean.TRUE);
                                        } else {
                                            member.setIsLocWatcher(Boolean.FALSE);
                                        }
                                        member.setIsOSMAuthorize(dto.getIsOSMAuthorize());
                                        member.setContactType(getXcapContactType(dto.getContact_type()));
                                        String callPermission = dto.getCallPermission();
                                        if (callPermission != null) {
                                            String[] callPerm = callPermission.split(",");
                                            if (callPerm != null && callPerm.length > 2) {
                                                member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                                member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                                member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                            }
                                        } else {
                                            member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                            member.setCallReceivePermission(dto.getCallReceivePermission());
                                            member.setInCallPermission(dto.getInCallPermission());
                                        }
                                        String videoCallPermission = dto.getVideoCallPermission();
                                        if (videoCallPermission != null) {
                                            String[] videoCallPerm = videoCallPermission.split(",");
                                            if (videoCallPerm != null && videoCallPerm.length > 2) {
                                                member.setVideoCallInitiatePermission(Integer.parseInt(videoCallPerm[0]));
                                                member.setVideoCallReceivePermission(Integer.parseInt(videoCallPerm[1]));
                                                member.setVideoInCallPermission(Integer.parseInt(videoCallPerm[2]));
                                            }
                                        } else {
                                            member.setVideoCallInitiatePermission(dto.getVideoCallInitiatePermission());
                                            member.setVideoCallReceivePermission(dto.getVideoCallReceivePermission());
                                            member.setVideoInCallPermission(dto.getVideoInCallPermission());
                                        }
                                        if (dto.getMemberCorpId() != 0) {
                                            member.setMemberCorpId(dto.getMemberCorpId());
                                        }
                                        addedMemebrs.add(member);
                                        if (dto.getContact_type() == 0) {
                                            //if the added member is internal subscriber add in distribution list
                                            addedDistMem.add(dto.getMdn());
                                        }
                                        member.setBroadcaster(dto.getBroadcaster() == 1);
                                        member.setIsAffiliationEnabled(dto.getIsAffiliationEnabled());
                                    }
                                    corpEXDMSNotifyDto.setAddedMemebrs(addedMemebrs);
                                    corpEXDMSNotifyDto.setAddedGrpDistMems(addedDistMem);
                                    knLogger.debug(methodName, " addedMemebrs - ", addedMemebrs);
                                    knLogger.debug(methodName, " addedDistMem - ", KnGDPRTemplate.mdnList(addedDistMem));
                                }
                                if (docChgDTO.getRemovedGroupMembers() != null && !docChgDTO.getRemovedGroupMembers().isEmpty()) {
                                    knLogger.debug(methodName, " docChgDTO.getRemovedGroupMembers() - ", docChgDTO.getRemovedGroupMembers());
                                    corpEXDMSNotifyDto.setRemovedMembers(new ArrayList<>(docChgDTO.getRemovedGroupMembers()));
                                    corpEXDMSNotifyDto.setRemovedGrpDistMems(new ArrayList<>(docChgDTO.getRemovedGroupMembers()));
                                }
                                if (docChgDTO.getModifiedGrpMembers() != null && !docChgDTO.getModifiedGrpMembers().isEmpty()) {
                                    List<KnEXDMSGrpMemberDto> modGrpMems = new ArrayList<>();
                                    knLogger.debug(methodName, " docChgDTO.getModifiedGrpMembers() - ", docChgDTO.getModifiedGrpMembers());
                                    for (KnSubscriberDTO dto : docChgDTO.getModifiedGrpMembers()) {
                                        KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                        member.setMdn(dto.getMdn());
                                        if (dto.getClientType() != 0) {
                                            member.setClientType(dto.getClientType());
                                        }
                                        member.setSupervisor(dto.getSupervisory());
                                        member.setName(dto.getNetworkName());
                                        if (dto.getLocWatcher() == 1) {
                                            member.setIsLocWatcher(Boolean.TRUE);
                                        } else {
                                            member.setIsLocWatcher(Boolean.FALSE);
                                        }
                                        if (dto.getContact_type() != 0)
                                            member.setContactType(dto.getContact_type());
                                        member.setIsOSMAuthorize(dto.getIsOSMAuthorize());
                                        String callPermission = dto.getCallPermission();
                                        if (callPermission != null) {
                                            String[] callPerm = callPermission.split(",");
                                            if (callPerm != null && callPerm.length > 2) {
                                                member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                                member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                                member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                            }
                                        } else {
                                            member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                            member.setCallReceivePermission(dto.getCallReceivePermission());
                                            member.setInCallPermission(dto.getInCallPermission());
                                        }
                                        String videoCallPermission = dto.getVideoCallPermission();
                                        if (videoCallPermission != null) {
                                            String[] videoCallPerm = videoCallPermission.split(",");
                                            if (videoCallPerm != null && videoCallPerm.length > 2) {
                                                member.setVideoCallInitiatePermission(Integer.parseInt(videoCallPerm[0]));
                                                member.setVideoCallReceivePermission(Integer.parseInt(videoCallPerm[1]));
                                                member.setVideoInCallPermission(Integer.parseInt(videoCallPerm[2]));
                                            }
                                        } else {
                                            member.setVideoCallInitiatePermission(dto.getVideoCallInitiatePermission());
                                            member.setVideoCallReceivePermission(dto.getVideoCallReceivePermission());
                                            member.setVideoInCallPermission(dto.getVideoInCallPermission());
                                        }
                                        member.setBroadcaster(dto.getBroadcaster() == 1);
                                        modGrpMems.add(member);
                                    }
                                    knLogger.debug(methodName, " modGrpMems - ", modGrpMems);
                                    corpEXDMSNotifyDto.setModifiedMembers(modGrpMems);
                                }
                                if ((corpEXDMSNotifyDto.getAddedMemebrs() == null || corpEXDMSNotifyDto.getAddedMemebrs().isEmpty()) &&
                                        (corpEXDMSNotifyDto.getRemovedMembers() == null || corpEXDMSNotifyDto.getRemovedMembers().isEmpty()) &&
                                        (corpEXDMSNotifyDto.getModifiedMembers() == null || corpEXDMSNotifyDto.getModifiedMembers().isEmpty()) &&
                                        corpEXDMSNotifyDto.getGrpName() == null) {
                                    corpEXDMSNotifyDto.setPreviousEtag(-1);
                                }
                                modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                            } else if (docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()
                                    && !modifiedMap.containsKey(docChgDTO.getGroupId())) {
                                knLogger.debug(methodName, " Add doc type for - ", docChgDTO.getGroupId());
                                //Only broadcast group is being taken care in add notify type.
                                corpEXDMSNotifyDto = modifiedMap.get(docChgDTO.getGroupId());
                                if (null == corpEXDMSNotifyDto) {
                                    corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                                }
                                deletedMap.remove(docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setGrpSIPUri(KnGenInfoUtil.mcpttGroupUri(docChgDTO.getGroupId(), corpId, grpSIPUri));
                                corpEXDMSNotifyDto.setLmrInteropFlag(lmrIntrop);
                                corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                                if (createdBy != null) corpEXDMSNotifyDto.setCreatedBy(createdBy);
                                corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                                corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                                corpEXDMSNotifyDto.setCorpid(corpId);
                                corpEXDMSNotifyDto.setGrpType(docChgDTO.getGroupType());
                                corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                                corpEXDMSNotifyDto.setPreviousEtag(-1);
                                corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                                corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                                corpEXDMSNotifyDto.setOsmIdChanged(docChgDTO.isOsmListChanged());
                                corpEXDMSNotifyDto.setLargeGroup(docChgDTO.isLargeGroup());
                                if (mcxInd != null) {
                                    corpEXDMSNotifyDto.setGrpCategoryInd(mcxInd);
                                }
                                modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                            }
                        } else if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {
                            KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = null;
                            if ((docChgDTO.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value())
                                    && !(modifiedContactMap.containsKey(mdn))) {
                                corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                                corpEXDMSNotifyDto.setCorpid(corpId);
                                corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                                corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                                corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value());
                                corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value() + "_" + mdn);
                                corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                                corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CONTACT_NOTIFY_EVENTS.value());
                                corpEXDMSNotifyDto.setMdn(mdn);
                                corpEXDMSNotifyDto.setClientType(opDirChgDTO.getClientType());

                                Collection<KnSubscriberDTO> addedMembers = docChgDTO.getAddedContactList();
                                if (addedMembers != null && !addedMembers.isEmpty()) {
                                    knLogger.debug(methodName, " addedMembers - ", addedMembers);
                                    List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
                                    for (KnSubscriberDTO dto : addedMembers) {
                                        KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                        member.setMdn(dto.getMdn());
                                        if (dto.getClientType() != 0) {
                                            member.setClientType(dto.getClientType());
                                        }
                                        member.setName(dto.getNetworkName());
                                        member.setContactType(getXcapContactType(dto.getContact_type()));
                                        String callPermission = dto.getCallPermission();
                                        if (callPermission != null) {
                                            String[] callPerm = callPermission.split(",");
                                            if (callPerm != null && callPerm.length > 2) {
                                                member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                                member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                                member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                            }
                                        } else {
                                            member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                            member.setCallReceivePermission(dto.getCallReceivePermission());
                                            member.setInCallPermission(dto.getInCallPermission());
                                        }
                                        member.setBroadcaster(dto.getBroadcaster() == 1);
                                        addedMemebrs.add(member);
                                    }
                                    corpEXDMSNotifyDto.setAddedMemebrs(addedMemebrs);
                                    knLogger.debug(methodName, " addedMemebrs - ", addedMemebrs);
                                }

                                Collection<KnSubscriberDTO> modifiedMembers = docChgDTO.getModifiedContactMembers();
                                if (modifiedMembers != null && !modifiedMembers.isEmpty()) {
                                    knLogger.debug(methodName, " modifiedMembers - ", modifiedMembers);
                                    List<KnEXDMSGrpMemberDto> modifiedMemebrs = new ArrayList<>();
                                    for (KnSubscriberDTO dto : modifiedMembers) {
                                        KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                        member.setMdn(dto.getMdn());
                                        if (dto.getClientType() != 0) {
                                            member.setClientType(dto.getClientType());
                                        }
                                        member.setName(dto.getNetworkName());
                                        member.setContactType(getXcapContactType(dto.getContact_type()));
                                        String callPermission = dto.getCallPermission();
                                        if (callPermission != null) {
                                            String[] callPerm = callPermission.split(",");
                                            if (callPerm != null && callPerm.length > 2) {
                                                member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                                member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                                member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                            }
                                        } else {
                                            member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                            member.setCallReceivePermission(dto.getCallReceivePermission());
                                            member.setInCallPermission(dto.getInCallPermission());
                                        }
                                        member.setBroadcaster(dto.getBroadcaster() == 1);
                                        modifiedMemebrs.add(member);
                                    }
                                    corpEXDMSNotifyDto.setModifiedMembers(modifiedMemebrs);
                                    knLogger.debug(methodName, " modifiedMemebrs - ", modifiedMemebrs);
                                }

                                if (docChgDTO.getRemovedContactList() != null && !docChgDTO.getRemovedContactList().isEmpty()) {
                                    knLogger.debug(methodName, " docChgDTO.getRemovedContactList() - ", docChgDTO.getRemovedContactList());
                                    corpEXDMSNotifyDto.setRemovedMembers(new ArrayList<>(docChgDTO.getRemovedContactList()));
                                }
                                modifiedContactMap.put(mdn, corpEXDMSNotifyDto);
                            }
                        }
                    }
                }
            }
        }
        knLogger.debug(methodName, " modifiedMap - ", modifiedMap);
        knLogger.debug(methodName, " deletedMap - ", deletedMap);
        knLogger.debug(methodName, " modifiedContactMap - ", KnGDPRTemplate.mapKeyMdn(modifiedContactMap));
        corpEXDMSNotifyDtoList.addAll(modifiedMap.values());
        corpEXDMSNotifyDtoList.addAll(deletedMap.values());
        corpEXDMSNotifyDtoList.addAll(modifiedContactMap.values());
        knLogger.debug(methodName, " Returning - ", corpEXDMSNotifyDtoList);
        return corpEXDMSNotifyDtoList;
    }

    /**
     * Method to get the group diff data from the change log map for sending micro services notification for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @return
     */
    private static Map<Integer, Integer> getGroupTypeMapSafely(Map<String, KnOPDirChgDTO> changeLogMap) {
        String methodName = "getGroupTypeMapSafely()";
        try {
            Set<Integer> groupIds = new HashSet<Integer>();
            knLogger.debug(methodName, "Entry changeLogMap size - ", changeLogMap != null ? changeLogMap.size() : 0);
            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                    KnOPDirChgDTO opDirChgDTO = entry.getValue();
                    if (opDirChgDTO == null) {
                        continue;
                    }
                    Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                    if (changeDocList == null || changeDocList.isEmpty()) {
                        continue;
                    }
                    for (KnOPDocChgDTO docChgDTO : changeDocList) {
                        if (docChgDTO != null && docChgDTO.getDocUri() != null
                                && docChgDTO.getDocUri().contains(KnConstants.APP_UID_CORP_GROUP)) {
                            groupIds.add(docChgDTO.getGroupId());
                        }
                    }
                }
            }
            knLogger.debug(methodName, "Collected groupIds - ", groupIds);
            return groupInfoUtil.getGroupTypeMap(groupIds, KnDbUtil.getDBConfigInfo().getLocalPttId(), null);
        } catch (Throwable t) {
            knLogger.error(methodName, "Failed to resolve group type map, continuing without it - ", t);
            return Collections.emptyMap();
        }
    }

    private static List<KnCorpEXDMSNotifyDto> getModifiedNotifyJson(Map<String, KnOPDirChgDTO> changeLogMap, int corpId,
                                                                    Integer createdBy, Integer lmrIntrop, Integer mcxInd,
                                                                    Map<Integer, KnCorpGroupInfoDTO> groupSharedMap,
                                                                    String grpSIPUri, Integer oldLmr, String recordingFs) {
        final String methodName = "getModifiedNotifyJson()";
        knLogger.info(methodName, "Entry grpSIPUri:", grpSIPUri, " lmrIntrop:", lmrIntrop, " recordingFs :", recordingFs);
        knLogger.debug(methodName, KnGDPRTemplate.mapKeyMdn(changeLogMap));
        List<KnCorpEXDMSNotifyDto> corpEXDMSNotifyDtoList = new ArrayList<>();
        Map<Integer, KnCorpEXDMSNotifyDto> modifiedMap = new HashMap<>();
        Map<Integer, KnCorpEXDMSNotifyDto> deletedMap = new HashMap<>();
        Map<String, KnCorpEXDMSNotifyDto> modifiedContactMap = new HashMap<>();
        Map<Integer, Integer> groupTypeMap = getGroupTypeMapSafely(changeLogMap);
        if (changeLogMap != null && !changeLogMap.isEmpty()) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                knLogger.debug(methodName, "MDN - ", KnGDPRTemplate.mdn(entry.getKey()));
                String mdn = entry.getKey();
                KnOPDirChgDTO opDirChgDTO = entry.getValue();
                Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                knLogger.debug(methodName, "changeDocList - ", changeDocList);
                for (KnOPDocChgDTO docChgDTO : changeDocList) {
                    knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                    //If the document type is corporate group
                    if (docChgDTO.getDocUri().contains(KnConstants.APP_UID_CORP_GROUP)) {

                        KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = null;
                        Integer resolvedGroupType = groupTypeMap.get(docChgDTO.getGroupId());
                        if (resolvedGroupType == null) {
                            resolvedGroupType = docChgDTO.getGroupType();
                        }
                        if ((docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value())
                                && !(modifiedMap.containsKey(docChgDTO.getGroupId()))) {
                            //A group can get deleted by some other operations like modify/delete sublist.
                            //In this flow delete group notification will be send out to all the existing members. No replace notify will be available for those groups
                            //But a broadcast group may have only remove notification though the group exist which is not possible for other group type.
                            //Since broadcast group notify does not have diff data, microservices will be notified with previous etag as -1 so that they can fetch the group details.
                            knLogger.debug(methodName, " Remove doc type for - ", docChgDTO.getGroupId());
                            corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            corpEXDMSNotifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpSIPUri(KnGenInfoUtil.mcpttGroupUri(docChgDTO.getGroupId(),corpId,grpSIPUri));
                            corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpName(docChgDTO.getGroupDisplayName());
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setGrpType(resolvedGroupType);
                            if (createdBy != null) corpEXDMSNotifyDto.setCreatedBy(createdBy);
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setLmrInteropFlag(lmrIntrop);
                            corpEXDMSNotifyDto.setRecordingFs(recordingFs);
                            if(mcxInd != null) {
                                corpEXDMSNotifyDto.setGrpCategoryInd(mcxInd);
                            }
                            corpEXDMSNotifyDto.setLargeGroup(docChgDTO.isLargeGroup());
                            corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                            //MINT-23959 , deleteGroup event will be sent only in deleteGroup API call
                            //corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                            //corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setOsmIdChanged(docChgDTO.isOsmListChanged());
                            if (resolvedGroupType == 3) {
                                knLogger.debug(methodName, "Broadcast group type");
                                corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                                corpEXDMSNotifyDto.setPreviousEtag(-1);
                                if (null != oldLmr) {
                                    corpEXDMSNotifyDto.setOldLmrInteropFlag(oldLmr);
                                }
                                modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                            } else {
                                knLogger.debug(methodName, " Non Broadcast - ", resolvedGroupType);
                                //corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                                deletedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                            }

                        } else if (docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            knLogger.debug(methodName, "Replace doc type for - ", docChgDTO.getGroupId());
                            // group modified populating for group modified data
                            corpEXDMSNotifyDto = modifiedMap.get(docChgDTO.getGroupId());
                            if (null == corpEXDMSNotifyDto) {
                                corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            }
                            deletedMap.remove(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpType(resolvedGroupType);
                            corpEXDMSNotifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpSIPUri(KnGenInfoUtil.mcpttGroupUri(docChgDTO.getGroupId(),corpId,grpSIPUri));
                            corpEXDMSNotifyDto.setLmrInteropFlag(lmrIntrop);
                            corpEXDMSNotifyDto.setRecordingFs(recordingFs);

                            //Extract pocHome from opDirChgDTO for group rehome notification
                            if (opDirChgDTO != null && opDirChgDTO.getPocHome() != null) {
                                corpEXDMSNotifyDto.setPocHome(opDirChgDTO.getPocHome());
                                knLogger.debug(methodName, "Set pocHome for groupId ", docChgDTO.getGroupId(), " - ", opDirChgDTO.getPocHome());
                            }

                            if (createdBy != null) corpEXDMSNotifyDto.setCreatedBy(createdBy);
                            corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                            corpEXDMSNotifyDto.setGrpName(docChgDTO.getGroupDisplayName());
                            if(groupSharedMap != null){
                                if(groupSharedMap.get(docChgDTO.getGroupId())!=null) {
                                    corpEXDMSNotifyDto.setGrpShare(groupSharedMap.get(docChgDTO.getGroupId()).getGrpShared());
                                }
                            }
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                            if (null != oldLmr) {
                                corpEXDMSNotifyDto.setOldLmrInteropFlag(oldLmr);
                            }
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setOsmIdChanged(docChgDTO.isOsmListChanged());
                            if (resolvedGroupType == 3) {
                                knLogger.debug(methodName, " Broadcast group ");
                                corpEXDMSNotifyDto.setPreviousEtag(-1);
                            }
                            if(mcxInd != null) {
                                corpEXDMSNotifyDto.setGrpCategoryInd(mcxInd);
                            }
                            Collection<KnSubscriberDTO> addedGroupMembers = docChgDTO.getAddedGroupMembers();
                            Collection<KnSubscriberDTO> addedEmptyGroupMembers = docChgDTO.getAddedEmptyGroupMembers();
                            knLogger.debug(methodName," addedEmptyGroupMembers :",addedEmptyGroupMembers);
                            knLogger.debug(methodName," addedGroupMembers :",addedGroupMembers);
                            Collection<KnSubscriberDTO> addGrpMems = null;
                            if(addedGroupMembers!=null&&!addedGroupMembers.isEmpty()){
                                addGrpMems=addedGroupMembers;
                            }
                            if(addedEmptyGroupMembers!=null&&!addedEmptyGroupMembers.isEmpty()){
                                addGrpMems=addedEmptyGroupMembers;
                            }

                            if (addGrpMems != null && !addGrpMems.isEmpty()) {
                                knLogger.debug(methodName, " addGrpMems - ", addGrpMems);
                                List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
                                List<String> addedDistMem = new ArrayList<>();
                                for (KnSubscriberDTO dto : addGrpMems) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    if (dto.getClientType() != 0) {
                                        member.setClientType(dto.getClientType());
                                    }
                                    member.setSupervisor(dto.getSupervisory());
                                    member.setName(dto.getNetworkName());
                                    if (dto.getLocWatcher() == 1) {
                                        member.setIsLocWatcher(Boolean.TRUE);
                                    } else {
                                        member.setIsLocWatcher(Boolean.FALSE);
                                    }
                                    member.setIsOSMAuthorize(dto.getIsOSMAuthorize());
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    String callPermission=dto.getCallPermission();
                                    if(callPermission!=null){
                                        String[] callPerm = callPermission.split(",");
                                        if(callPerm!=null && callPerm.length>2){
                                            member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                            member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                            member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                        }
                                    }
                                    else{
                                        member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                        member.setCallReceivePermission(dto.getCallReceivePermission());
                                        member.setInCallPermission(dto.getInCallPermission());
                                    }String videoCallPermission = dto.getVideoCallPermission();
                                    if (videoCallPermission != null) {
                                        String[] videoCallPerm = videoCallPermission.split(",");
                                        if (videoCallPerm != null && videoCallPerm.length > 2) {
                                            member.setVideoCallInitiatePermission(Integer.parseInt(videoCallPerm[0]));
                                            member.setVideoCallReceivePermission(Integer.parseInt(videoCallPerm[1]));
                                            member.setVideoInCallPermission(Integer.parseInt(videoCallPerm[2]));
                                        }
                                    } else {
                                        member.setVideoCallInitiatePermission(dto.getVideoCallInitiatePermission());
                                        member.setVideoCallReceivePermission(dto.getVideoCallReceivePermission());
                                        member.setVideoInCallPermission(dto.getVideoInCallPermission());
                                    }
                                    if(dto.getMemberCorpId() != 0){
                                        member.setMemberCorpId(dto.getMemberCorpId());
                                    }
                                    addedMemebrs.add(member);
                                    if (dto.getContact_type() == 0) {
                                        //if the added member is internal subscriber add in distribution list
                                        addedDistMem.add(dto.getMdn());
                                    }
                                    member.setBroadcaster(dto.getBroadcaster()==1);
                                    member.setIsAffiliationEnabled(dto.getIsAffiliationEnabled());
                                }
                                corpEXDMSNotifyDto.setAddedMemebrs(addedMemebrs);
                                corpEXDMSNotifyDto.setAddedGrpDistMems(addedDistMem);
                                knLogger.debug(methodName, " addedMemebrs - ", addedMemebrs);
                                knLogger.debug(methodName, " addedDistMem - ", KnGDPRTemplate.mdnList(addedDistMem));
                            }
                            if (docChgDTO.getRemovedGroupMembers() != null && !docChgDTO.getRemovedGroupMembers().isEmpty()) {
                                knLogger.debug(methodName, " docChgDTO.getRemovedGroupMembers() - ", docChgDTO.getRemovedGroupMembers());
                                corpEXDMSNotifyDto.setRemovedMembers(new ArrayList<>(docChgDTO.getRemovedGroupMembers()));
                                corpEXDMSNotifyDto.setRemovedGrpDistMems(new ArrayList<>(docChgDTO.getRemovedGroupMembers()));
                            }
                            if (docChgDTO.getModifiedGrpMembers() != null && !docChgDTO.getModifiedGrpMembers().isEmpty()) {
                                List<KnEXDMSGrpMemberDto> modGrpMems = new ArrayList<>();
                                knLogger.debug(methodName, " docChgDTO.getModifiedGrpMembers() - ", docChgDTO.getModifiedGrpMembers());
                                for (KnSubscriberDTO dto : docChgDTO.getModifiedGrpMembers()) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    if (dto.getClientType() != 0) {
                                        member.setClientType(dto.getClientType());
                                    }
                                    member.setSupervisor(dto.getSupervisory());
                                    member.setName(dto.getNetworkName());
                                    if (dto.getLocWatcher() == 1) {
                                        member.setIsLocWatcher(Boolean.TRUE);
                                    } else {
                                        member.setIsLocWatcher(Boolean.FALSE);
                                    }
                                    if (dto.getContact_type() != 0)
                                        member.setContactType(dto.getContact_type());
                                    member.setIsOSMAuthorize(dto.getIsOSMAuthorize());
                                    String callPermission=dto.getCallPermission();
                                    if(callPermission!=null){
                                        String[] callPerm = callPermission.split(",");
                                        if(callPerm!=null && callPerm.length>2){
                                            member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                            member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                            member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                        }
                                    }
                                    else{
                                        member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                        member.setCallReceivePermission(dto.getCallReceivePermission());
                                        member.setInCallPermission(dto.getInCallPermission());
                                    }String videoCallPermission = dto.getVideoCallPermission();
                                    if (videoCallPermission != null) {
                                        String[] videoCallPerm = videoCallPermission.split(",");
                                        if (videoCallPerm != null && videoCallPerm.length > 2) {
                                            member.setVideoCallInitiatePermission(Integer.parseInt(videoCallPerm[0]));
                                            member.setVideoCallReceivePermission(Integer.parseInt(videoCallPerm[1]));
                                            member.setVideoInCallPermission(Integer.parseInt(videoCallPerm[2]));
                                        }
                                    } else {
                                        member.setVideoCallInitiatePermission(dto.getVideoCallInitiatePermission());
                                        member.setVideoCallReceivePermission(dto.getVideoCallReceivePermission());
                                        member.setVideoInCallPermission(dto.getVideoInCallPermission());
                                    }
                                    member.setBroadcaster(dto.getBroadcaster()==1);
                                    modGrpMems.add(member);
                                }
                                knLogger.debug(methodName, " modGrpMems - ", modGrpMems);
                                corpEXDMSNotifyDto.setModifiedMembers(modGrpMems);
                            }
                            if ((corpEXDMSNotifyDto.getAddedMemebrs() == null || corpEXDMSNotifyDto.getAddedMemebrs().isEmpty()) &&
                                    (corpEXDMSNotifyDto.getRemovedMembers() == null || corpEXDMSNotifyDto.getRemovedMembers().isEmpty()) &&
                                    (corpEXDMSNotifyDto.getModifiedMembers() == null || corpEXDMSNotifyDto.getModifiedMembers().isEmpty()) &&
                                    corpEXDMSNotifyDto.getGrpName() == null) {
                                corpEXDMSNotifyDto.setPreviousEtag(-1);
                            }
                            modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                        } else if (docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()
                                && !modifiedMap.containsKey(docChgDTO.getGroupId())) {
                            knLogger.debug(methodName, " Add doc type for - ", docChgDTO.getGroupId());
                            //Only broadcast group is being taken care in add notify type.
                            corpEXDMSNotifyDto = modifiedMap.get(docChgDTO.getGroupId());
                            if (null == corpEXDMSNotifyDto) {
                                corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            }
                            deletedMap.remove(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpSIPUri(KnGenInfoUtil.mcpttGroupUri(docChgDTO.getGroupId(),corpId,grpSIPUri));
                            corpEXDMSNotifyDto.setLmrInteropFlag(lmrIntrop);
                            corpEXDMSNotifyDto.setRecordingFs(recordingFs);
                            corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                            if (createdBy != null) corpEXDMSNotifyDto.setCreatedBy(createdBy);
                            corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setGrpType(resolvedGroupType);
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(-1);
                            if (null != oldLmr) {
                                corpEXDMSNotifyDto.setOldLmrInteropFlag(oldLmr);
                            }
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setOsmIdChanged(docChgDTO.isOsmListChanged());
                            corpEXDMSNotifyDto.setLargeGroup(docChgDTO.isLargeGroup());
                            if(mcxInd != null) {
                                corpEXDMSNotifyDto.setGrpCategoryInd(mcxInd);
                            }
                            modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                        }
                    } else if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {
                        KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = null;
                        if ((docChgDTO.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value())
                                && !(modifiedContactMap.containsKey(mdn))) {
                            corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value());
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value() + "_" + mdn);
                            corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CONTACT_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setMdn(mdn);
                            corpEXDMSNotifyDto.setClientType(opDirChgDTO.getClientType());

                            Collection<KnSubscriberDTO> addedMembers = docChgDTO.getAddedContactList();
                            if (addedMembers != null && !addedMembers.isEmpty()) {
                                knLogger.debug(methodName, " addedMembers - ", addedMembers);
                                List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
                                for (KnSubscriberDTO dto : addedMembers) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    if (dto.getClientType() != 0) {
                                        member.setClientType(dto.getClientType());
                                    }
                                    member.setName(dto.getNetworkName());
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    String callPermission=dto.getCallPermission();
                                    if(callPermission!=null){
                                        String[] callPerm = callPermission.split(",");
                                        if(callPerm!=null && callPerm.length>2){
                                            member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                            member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                            member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                        }
                                    }
                                    else{
                                        member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                        member.setCallReceivePermission(dto.getCallReceivePermission());
                                        member.setInCallPermission(dto.getInCallPermission());
                                    }
                                    member.setBroadcaster(dto.getBroadcaster()==1);
                                    addedMemebrs.add(member);
                                }
                                corpEXDMSNotifyDto.setAddedMemebrs(addedMemebrs);
                                knLogger.debug(methodName, " addedMemebrs - ", addedMemebrs);
                            }

                            Collection<KnSubscriberDTO> modifiedMembers = docChgDTO.getModifiedContactMembers();
                            if (modifiedMembers != null && !modifiedMembers.isEmpty()) {
                                knLogger.debug(methodName, " modifiedMembers - ", modifiedMembers);
                                List<KnEXDMSGrpMemberDto> modifiedMemebrs = new ArrayList<>();
                                for (KnSubscriberDTO dto : modifiedMembers) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    if (dto.getClientType() != 0) {
                                        member.setClientType(dto.getClientType());
                                    }
                                    member.setName(dto.getNetworkName());
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    String callPermission=dto.getCallPermission();
                                    if(callPermission!=null){
                                        String[] callPerm = callPermission.split(",");
                                        if(callPerm!=null && callPerm.length>2){
                                            member.setCallInitiatePermission(Integer.parseInt(callPerm[0]));
                                            member.setCallReceivePermission(Integer.parseInt(callPerm[1]));
                                            member.setInCallPermission(Integer.parseInt(callPerm[2]));
                                        }
                                    }
                                    else{
                                        member.setCallInitiatePermission(dto.getCallInitiatePermission());
                                        member.setCallReceivePermission(dto.getCallReceivePermission());
                                        member.setInCallPermission(dto.getInCallPermission());
                                    }
                                    member.setBroadcaster(dto.getBroadcaster()==1);
                                    modifiedMemebrs.add(member);
                                }
                                corpEXDMSNotifyDto.setModifiedMembers(modifiedMemebrs);
                                knLogger.debug(methodName, " modifiedMemebrs - ", modifiedMemebrs);
                            }

                            if (docChgDTO.getRemovedContactList() != null && !docChgDTO.getRemovedContactList().isEmpty()) {
                                knLogger.debug(methodName, " docChgDTO.getRemovedContactList() - ", docChgDTO.getRemovedContactList());
                                corpEXDMSNotifyDto.setRemovedMembers(new ArrayList<>(docChgDTO.getRemovedContactList()));
                            }
                            modifiedContactMap.put(mdn, corpEXDMSNotifyDto);
                        }
                    }
                }
            }
        }
        knLogger.debug(methodName, " modifiedMap - ", modifiedMap);
        knLogger.debug(methodName, " deletedMap - ", deletedMap);
        knLogger.debug(methodName, " modifiedContactMap - ", KnGDPRTemplate.mapKeyMdn(modifiedContactMap));
        corpEXDMSNotifyDtoList.addAll(modifiedMap.values());
        corpEXDMSNotifyDtoList.addAll(deletedMap.values());
        corpEXDMSNotifyDtoList.addAll(modifiedContactMap.values());
        knLogger.debug(methodName, " Returning - ", corpEXDMSNotifyDtoList);
        return corpEXDMSNotifyDtoList;
    }

    public List<KnCorpEXDMSNotifyDto> getGrpMicroSrvNotifyDto(KnCorpGroupInfoRespDTO respDto, int corpId) {
        String methodName = "getGrpMicroSrvNotifyDto()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "respDto - ", respDto);
        Collection<KnCorpContactDTO> addedMems = respDto.getAddedGroupMember();
        Collection<String> grpDistList = respDto.getAddedGroupDistributionMember();
        List<KnCorpEXDMSNotifyDto> exdmsNotifyDtoList = new ArrayList<>();
        KnCorpEXDMSNotifyDto notifyDto = new KnCorpEXDMSNotifyDto();
        notifyDto.setCorpid(corpId);
        notifyDto.setGrpId(respDto.getGroupInfoDTO().getGroupId());
        notifyDto.setGrpName(respDto.getGroupInfoDTO().getGroupDisplayName());
        if (respDto.getEtag() != null) {
            notifyDto.setEtag(Integer.parseInt(respDto.getEtag()));
        }
        notifyDto.setGrpType(respDto.getGroupInfoDTO().getGroupType());
        notifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + respDto.getGroupInfoDTO().getGroupId());
        notifyDto.setGrpSIPUri(respDto.getGroupInfoDTO().getGrpSIPUri());
        notifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
        notifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.CREATE_CORP_GROUP.value() + "_" + respDto.getGroupInfoDTO().getGroupId());
        notifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.CREATE_CORP_GROUP.value());
        notifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        notifyDto.setCreatedBy(respDto.getGroupCreatedBy());
        notifyDto.setLmrInteropFlag(respDto.getGroupInfoDTO().getUgwInterop());
        notifyDto.setGrpCategoryInd(respDto.getMcxGrpInd());
        notifyDto.setGrpShare(respDto.getGroupInfoDTO().getGrpShared());
        List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
        for (KnCorpContactDTO dto : addedMems) {
            KnEXDMSGrpMemberDto mem = new KnEXDMSGrpMemberDto();
            mem.setMdn(dto.getMdn());
            mem.setName(dto.getName());
            if (dto.getClientType() != 0) {
                mem.setClientType(dto.getClientType());
            }
            mem.setContactType(dto.getContact_type());
            mem.setBroadcaster(dto.getBroadcaster() == 1);
            mem.setIsLocWatcher(dto.getLocWatcher() == 1);
            mem.setSupervisor(dto.getSupervisory());
            mem.setIsOSMAuthorize(dto.getIsOSMAuthorize());
            addedMemebrs.add(mem);
        }
        knLogger.debug(methodName, "addedMemebrs - ", addedMemebrs);
        if(respDto.getMcxGrpInd() != 1) {
        notifyDto.setAddedMemebrs(addedMemebrs);
            if (grpDistList != null) {
                notifyDto.setGrpDistMems(new ArrayList<>(grpDistList));
            }
        }
        if(respDto.getGroupInfoDTO().getIsPreConfiguredGroup() != null){
        notifyDto.setIsPreConfigGroup(respDto.getGroupInfoDTO().getIsPreConfiguredGroup());
        }
        knLogger.debug(methodName, "notifyDto - ", notifyDto);
        exdmsNotifyDtoList.add(notifyDto);
        return exdmsNotifyDtoList;
    }

    public List<KnCorpEXDMSNotifyDto> getModifyMcxGrpMicroSrvNotifyDto(KnCorpResponseDTO respDto, int corpId, int groupId, String profileId,
                                                                       com.kodiak.common.commdto.common.KnCorpGroupContactDTO memberPr, Integer oldLmr) {
        return getModifyMcxGrpMicroSrvNotifyDto(respDto, corpId, groupId, profileId, memberPr, oldLmr,null);
    }

    public List<KnCorpEXDMSNotifyDto> getModifyMcxGrpMicroSrvNotifyDto(KnCorpResponseDTO respDto, int corpId, int groupId, String profileId,
                                                                       com.kodiak.common.commdto.common.KnCorpGroupContactDTO memberPr, Integer oldLmr, Integer groupType) {
        String methodName = "getModifyMcxGrpMicroSrvNotifyDto()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "respDto - ", respDto);
        List<KnCorpEXDMSNotifyDto> exdmsNotifyDtoList = new ArrayList<>();
        KnCorpEXDMSNotifyDto notifyDto = new KnCorpEXDMSNotifyDto();
        notifyDto.setCorpid(corpId);
        notifyDto.setGrpId(groupId);
        notifyDto.setGrpType(groupType);
        if (null != oldLmr) {
            notifyDto.setOldLmrInteropFlag(oldLmr);
        }
        if (respDto.getEtag() != null) {
            notifyDto.setEtag(Integer.parseInt(respDto.getEtag()));
        }
        notifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + groupId);
        notifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
        notifyDto.setId(MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + groupId);
        notifyDto.setType(MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
        notifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        notifyDto.setCreatedBy(respDto.getGroupCreatedBy());
        notifyDto.setGrpCategoryInd(respDto.getMcxGrpInd());
        notifyDto.setProfileId(profileId);
        notifyDto.setMcxGrpMemProperties(new KnMcxGrpMemberPropsDto(memberPr.getIsSupervisor(),memberPr.getIsBroadcaster()
                ,memberPr.getIsLocSupervisor(),memberPr.getIsOSMAuthorized(),
                memberPr.getCallInitiateAllowed(),
                memberPr.getCallTerminateAllowed(),memberPr.getIncallAllowed(),
                memberPr.getVideoCallInitiatePermission(),
                memberPr.getVideoCallReceivePermission(),
                memberPr.getVideoInCallPermission()));
        knLogger.info(methodName, "notifyDto - ", notifyDto);
        exdmsNotifyDtoList.add(notifyDto);
        return exdmsNotifyDtoList;
    }

    public List<KnCorpEXDMSNotifyDto> getContactMicroSrvNotifyDto(Map<String, KnOPDirChgDTO> changeLogMap, int corpId) {
        String methodName = "getContactMicroSrvNotifyDto()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "changeLogMap - ", changeLogMap == null ?  changeLogMap : KnGDPRTemplate.mapKeyMdn(changeLogMap));
        List<KnCorpEXDMSNotifyDto> corpEXDMSNotifyDtoList = new ArrayList<>();
        Map<String, KnCorpEXDMSNotifyDto> modifiedContactMap = new HashMap<>();
        if (changeLogMap != null && !changeLogMap.isEmpty()) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                knLogger.debug(methodName, "MDN - ", KnGDPRTemplate.mdn(entry.getKey()));
                String mdn = entry.getKey();
                KnOPDirChgDTO opDirChgDTO = entry.getValue();
                Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                knLogger.debug(methodName, "changeDocList - ", changeDocList);
                for (KnOPDocChgDTO docChgDTO : changeDocList) {
                    //If the document type is corporate group
                    if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {
                        KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = null;
                        if ((docChgDTO.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value())
                                && !(modifiedContactMap.containsKey(mdn))) {
                            corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value());
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value() + "_" + mdn);
                            corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CONTACT_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setMdn(mdn);
                            corpEXDMSNotifyDto.setClientType(opDirChgDTO.getClientType());

                            Collection<KnSubscriberDTO> addedMembers = docChgDTO.getAddedContactList();
                            if (addedMembers != null && !addedMembers.isEmpty()) {
                                knLogger.debug(methodName, " addedMembers - ", addedMembers);
                                List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
                                for (KnSubscriberDTO dto : addedMembers) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    if (dto.getClientType() != 0) {
                                        member.setClientType(dto.getClientType());
                                    }
                                    member.setName(dto.getNetworkName());
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    addedMemebrs.add(member);
                                }
                                corpEXDMSNotifyDto.setAddedMemebrs(addedMemebrs);
                                knLogger.debug(methodName, " addedMemebrs - ", addedMemebrs);
                            }

                            Collection<KnSubscriberDTO> modifiedMembers = docChgDTO.getModifiedContactMembers();
                            if (modifiedMembers != null && !modifiedMembers.isEmpty()) {
                                knLogger.debug(methodName, " modifiedMembers - ", modifiedMembers);
                                List<KnEXDMSGrpMemberDto> modifiedMemebrs = new ArrayList<>();
                                for (KnSubscriberDTO dto : modifiedMembers) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    if (dto.getClientType() != 0) {
                                        member.setClientType(dto.getClientType());
                                    }
                                    member.setName(dto.getNetworkName());
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    modifiedMemebrs.add(member);
                                }
                                corpEXDMSNotifyDto.setModifiedMembers(modifiedMemebrs);
                                knLogger.debug(methodName, " modifiedMemebrs - ", modifiedMemebrs);
                            }

                            if (docChgDTO.getRemovedContactList() != null && !docChgDTO.getRemovedContactList().isEmpty()) {
                                knLogger.debug(methodName, " docChgDTO.getRemovedContactList() - ", docChgDTO.getRemovedContactList());
                                corpEXDMSNotifyDto.setRemovedMembers(new ArrayList<>(docChgDTO.getRemovedContactList()));
                            }
                            modifiedContactMap.put(mdn, corpEXDMSNotifyDto);
                        }
                    }
                }
            }
        }
        knLogger.debug(methodName, " modifiedContactMap - ", KnGDPRTemplate.mapKeyMdn(modifiedContactMap));
        corpEXDMSNotifyDtoList.addAll(modifiedContactMap.values());
        knLogger.debug(methodName, " Returning - ", corpEXDMSNotifyDtoList);
        return corpEXDMSNotifyDtoList;
    }


    public List<KnPubEXDMSNotifyDTO> getPubGrpMicroSrvNotifyDto(KnOpPubResponse respDto) {
        String methodName = "getPubGrpMicroSrvNotifyDto()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "respDto - ", respDto);
        KnPubNotifyDetailsDTO pubNotifyDetailsDTO = respDto.getPubNotifyDetailsDTO();
        Collection<KnGroupMemberDTO> addedMems = pubNotifyDetailsDTO.getAddedMembers();
        Collection<KnGroupMemberDTO> modifiedMems = pubNotifyDetailsDTO.getModifiedMembers();
        Collection<String> removedMems = pubNotifyDetailsDTO.getRemovedMembers();
        List<KnPubEXDMSNotifyDTO> exdmsNotifyDtoList = new ArrayList<>();
        KnPubEXDMSNotifyDTO notifyDto = new KnPubEXDMSNotifyDTO();
        notifyDto.setOwnerMdn(pubNotifyDetailsDTO.getOwnerMdn());
        notifyDto.setClientType(pubNotifyDetailsDTO.getClientType());
        notifyDto.setGrpId(pubNotifyDetailsDTO.getGrpId());
        notifyDto.setGrpName(pubNotifyDetailsDTO.getGrpName());
        if (pubNotifyDetailsDTO.getDocOldEtag() != null) {
            notifyDto.setPreviousEtag(Integer.parseInt(pubNotifyDetailsDTO.getDocOldEtag()));
        }
        if (pubNotifyDetailsDTO.getDocNewEtag() != null) {
            notifyDto.setEtag(Integer.parseInt(pubNotifyDetailsDTO.getDocNewEtag()));
        }
        if (KnConstants.DOC_CHANGE_TYPE.ADD.value() == pubNotifyDetailsDTO.getGroupState()) {
            notifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.CREATE_PUB_GROUP.value() + "_" + pubNotifyDetailsDTO.getGrpId());
            notifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.CREATE_PUB_GROUP.value());
        } else if (KnConstants.DOC_CHANGE_TYPE.REPLACE.value() == pubNotifyDetailsDTO.getGroupState()) {
            notifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_PUB_GROUP.value() + "_" + pubNotifyDetailsDTO.getGrpId());
            notifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_PUB_GROUP.value());
        } else if (KnConstants.DOC_CHANGE_TYPE.REMOVE.value() == pubNotifyDetailsDTO.getGroupState()) {
            notifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_PUB_GROUP.value() + "_" + pubNotifyDetailsDTO.getGrpId());
            notifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_PUB_GROUP.value());
        }
        notifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
        notifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
        if (addedMems != null && !addedMems.isEmpty()) {
            for (KnGroupMemberDTO dto : addedMems) {
                KnEXDMSGrpMemberDto mem = new KnEXDMSGrpMemberDto();
                mem.setMdn(dto.getMemberMdn());
                mem.setName(dto.getMemberName());
                addedMemebrs.add(mem);
            }
            knLogger.debug(methodName, "addedMemebrs - ", addedMemebrs);
            notifyDto.setAddedMemebrs(addedMemebrs);
        }
        List<KnEXDMSGrpMemberDto> modifiedMemebrs = new ArrayList<>();
        if (modifiedMems != null && !modifiedMems.isEmpty()) {
            for (KnGroupMemberDTO dto : modifiedMems) {
                KnEXDMSGrpMemberDto mem = new KnEXDMSGrpMemberDto();
                mem.setMdn(dto.getMemberMdn());
                mem.setName(dto.getMemberName());
                modifiedMemebrs.add(mem);
            }
            knLogger.debug(methodName, "modifiedMemebrs - ", modifiedMemebrs);
            notifyDto.setModifiedMembers(modifiedMemebrs);
        }
        if (removedMems != null && !removedMems.isEmpty()) {
            notifyDto.setRemovedMembers(new ArrayList<>(removedMems));
        }
        knLogger.debug(methodName, "notifyDto - ", notifyDto);
        exdmsNotifyDtoList.add(notifyDto);
        return exdmsNotifyDtoList;
    }

    public boolean getXcapMobileSyncFlag(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getXcapMobileSyncFlag()";
        knLogger.debug(methodName, "ENTRY");
        /*UCSPLATFORM-7936 : Removed the code below as the flag is now always considered enabled for sending the MCS notification.
          This change aligns with UCSPLATFORM-7936, where the flag dependency has been removed.*/
        return true;
    }


    public KnMqServiceConfig retrieveServerConfDetails(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveServerConfDetails()";
        int clusterId = Integer.parseInt(System.getenv(KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, " clusterId - ", clusterId);
        KnMqServiceConfig mqServiceConfig = genInfoUtil.retrieveServerConfDetails(persisterTxn).get(clusterId);
        knLogger.debug(methodName, " mqServiceConfig - ", mqServiceConfig);
        return mqServiceConfig;
    }

    private static int getXcapContactType(int dbContType) {
        int xcapContType = 0;
        switch (dbContType) {
            case 1:
                xcapContType = 2;
                break;
            case 2:
                xcapContType = 1;
                break;
        }
        return xcapContType;

    }

    public String decodeBase64(String encodedStr) {
        String methodName = "decodeBase64(String)";
        String decodedStr = "";
        try {
            if (encodedStr != null && !encodedStr.trim().equals("")) {
                String temp = encodedStr.trim();
                byte[] strInBytes = temp.getBytes();
                byte[] encodedBytes = Base64.getDecoder().decode(strInBytes);
                decodedStr = new String(encodedBytes);
                knLogger.debug(methodName, " base64 decoding userId - ", decodedStr);
            }
        } catch (Exception e) {
            decodedStr = "INVALID-" + encodedStr.toUpperCase();
            knLogger.debug(methodName, " base64 decoding failed for String - ", encodedStr, e);
        }
        return decodedStr.trim();
    }

    public void clientIntfValidator(KnXDMCorpGroupInfoRequestDTO xdmRequestDto, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnDAOException {
        String methodName = "clientIntfValidator(KnXDMCorpGroupInfoRequestDTO)";
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "clusterId :", clusterId);
        Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
        if (DYNAMIC_CGMT_INTF == xdmRequestDto.getClientType()) {
            knLogger.info(methodName, "request from Dynamic C&G interface");
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
            boolean dynamicFlag = false;
            if (null != dynamicBasedFlgValue) {
                dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
            }
            if (!dynamicFlag) {
                knLogger.error(methodName, "Dynamic C&G flag is not enabled for Dynamic group operations ", dynamicFlag);
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.DYNAMIC_FLAG_DISABLED, "Dynamic C&G flag is disabled");
            }
        } else if (AREA_BASED_DYNAMIC_GROUP == xdmRequestDto.getClientType()) {
            knLogger.info(methodName, "request from Area Based Dynamic interface");
            String areaBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ABDGFEATUREFLAG.value());
            boolean areaBasedFlg = false;
            if (null != areaBasedFlgValue) {
                areaBasedFlg = Integer.parseInt(areaBasedFlgValue) == ENABLED;
            }
            if (!areaBasedFlg) {
                knLogger.error(methodName, "ABDG System flag is not enabled for ABDG group operations ", areaBasedFlg);
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.ABDG_FLAG_DISABLED,
                        "ABDG flag is disabled");
            }
        } else {
            //validate corp Hierarchy
            if (!KnGeneralProfileUtil.validateIntCorpCCAndHierarchy(Integer.parseInt(xdmRequestDto.getCorpId()), xdmRequestDto.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the corp : ", xdmRequestDto.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST,
                        "Invalid Corp hierarchy passed");
            }
        }
    }

    public void veryLargeGrpFlagValidator(KnXDMCorpGroupInfoRequestDTO xdmRequestDto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
    	  String methodName = "veryLargeGrpValidator(KnXDMCorpGroupInfoRequestDTO)";
    	   int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
           knLogger.debug(methodName, "clusterId :", clusterId);
    	  Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
    	  if (xdmRequestDto.getMcxGrpInd() != null && KnConstants.MCX_GROUP_INDICATOR == xdmRequestDto.getMcxGrpInd()) {

    		    String vlgFlagvalue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.VLARGE_GROUP_SUPPORTED.value());
                boolean vlgFlag = false;
                if (null != vlgFlagvalue && !vlgFlagvalue.isEmpty()) {
                	vlgFlag = Integer.parseInt(vlgFlagvalue) == ENABLED;
                }
                if (!vlgFlag) {
                    knLogger.error(methodName, "VLARGE_GROUP_SUPPORTED flag is not enabled for VLG operations ", vlgFlag);
                    throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.VLG_FLAG_DISABLED, "VLG flag is disabled");
                }
    	  }
    }

    public String genTempPassword(String clientPassword, int clientPvVersion, String appId) {
        final String methodName = "genTempPassword(String)";
        String password = pwdUtil.generatePassword(KnConstants.MAX_OIDC_PASSWORD_LENGTH, appId);
        knLogger.debug(methodName, "password", password);
        if (clientPvVersion > PROTOCOL_VERSION_0 && clientPvVersion <= PROTOCOL_VERSION_13) {
            StringBuffer finalPwd = new StringBuffer();
            finalPwd.append(clientPassword.substring(0, 6)).append(PWD_SUFFIX);
            password = finalPwd.toString();
            knLogger.debug(methodName, "passwordPv", password);
        }
        return password;
    }

    public KnCorpSubsUserDetailsRespDTO populateRespIDMProfile(KnCorpSubsUserDetailsRespDTO responseDTO, KnXDMSubsAliasDetailsRespDTO subsProfileInfoDTO) {
        String methodName = "populateRespIDMProfile(KnCorpSubsUserDetailsRespDTO, KnXDMSubsAliasDetailsRespDTO)";
        knLogger.debug(methodName, "populating the XDM Response DTO ");
        if (subsProfileInfoDTO.getUserid() != null) {
            KnXDMCorpUserDetailsRespDTO idmSubscriberDTO = new KnXDMCorpUserDetailsRespDTO();
            idmSubscriberDTO.setMdn(subsProfileInfoDTO.getUserid());
            idmSubscriberDTO.setUserId(subsProfileInfoDTO.getEmail());
            idmSubscriberDTO.setAccState(subsProfileInfoDTO.getAccstate());
            if (subsProfileInfoDTO.getAttributes() != null) {
                Map<String, String> oidcAttributes = subsProfileInfoDTO.getAttributes();
                if (oidcAttributes.get(CREATION_DATE_OIDC) != null)
                    idmSubscriberDTO.setCreationDate(oidcAttributes.get(CREATION_DATE_OIDC));
            }
            responseDTO.setIdmSubscriberDTO(idmSubscriberDTO);
        }
        responseDTO.setStatus(subsProfileInfoDTO.getStatus());
        responseDTO.setStatusCode(subsProfileInfoDTO.getStatusCode());
        return responseDTO;
    }

    public void initiateEtagMgmtJob(Map<String, Boolean> mdnListMap) {
        String methodName = "initiateEtagMgmtJob(String)";
        knLogger.info(methodName, "etagMgmtThread", KnGDPRTemplate.mapKeyMdn(mdnListMap));
        try {
            mdnListMap.forEach((mdn, activeFsChanged) -> {
                if (activeFsChanged) {
                    KnEtagMgmtThread etagMgmtThread = new KnEtagMgmtThread(mdn);
                    knLogger.debug(methodName, "etagMgmtThread", etagMgmtThread);
                    service.submit(etagMgmtThread);
                }
            });
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to Start KnEtagMgmtThread thread", e);
        }
    }

    public void initiateEtagMgmtJob(Map<String, Boolean> mdnListMap, String operationType) {
        String methodName = "initiateEtagMgmtJob()";
        knLogger.info(methodName, "etagMgmtThread", KnGDPRTemplate.mapKeyMdn(mdnListMap), operationType);
        try {
            mdnListMap.forEach((mdn, activeFsChanged) -> {
                if (activeFsChanged) {
                    KnEtagMgmtThread etagMgmtThread = new KnEtagMgmtThread(mdn, operationType);
                    knLogger.debug(methodName, "etagMgmtThread", etagMgmtThread);
                    service.submit(etagMgmtThread);
                }
            });
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to Start KnEtagMgmtThread thread", e);
        }
    }

    public KnXDMSubsAliasDetailsRespDTO createOidcProfile(KnCorpResponseDTO corpResponseDTO, boolean isOIDCApplicable,
                                                          String appId, String idmFqdn) throws Exception {
        String methodName = "createOidcProfile(KnXDMCorpGroupInfoRequestDTO)";
        KnXDMSubsAliasDetailsRespDTO oidcResponseDTO = null;
        KnXDMSubsAliasDetailsReqDTO oidcSubscriberDTO = corpResponseDTO.getOidcSubscriberDTO();
        if (!appId.equals(APP_ID.HANDSET_STANDARD.value())) {
            String tmpPwd = pwdUtil.generatePassword(KnConstants.MAX_OIDC_PASSWORD_LENGTH, appId);
            oidcSubscriberDTO.setPwd(tmpPwd);
            oidcSubscriberDTO.setGeneratepwd(tmpPwd == null);
            List<String> actions = new ArrayList<>();
            actions.add(TMP_PWD_MODE.VERIFICATION_MAIL.value());
            Map<String, Object> oidcAttributes = oidcSubscriberDTO.getAttributes();
            if (oidcAttributes != null) {
                oidcAttributes.put(ACTIONS, actions);
            }
        }
        if ((isOIDCApplicable && appId.equals(APP_ID.DISPATCHER.value())) || (appId.equals(APP_ID.HANDSET_STANDARD.value()))) {
            knLogger.debug(methodName, "oidcSubscriberDTO - ", oidcSubscriberDTO, "idmFqdn - ", idmFqdn);
            String oldUserId = null;
            if (corpResponseDTO.isUserOverriden()) {
                oldUserId = (String) corpResponseDTO.getResponseMap().get(com.kodiak.common.resources.KnConstants.OLD_USER_ID);
            }
            oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(oidcSubscriberDTO, appId,
                    idmFqdn, corpResponseDTO.isUserOverriden(), oldUserId);
            knLogger.debug(methodName, "oidcResponseDTO", oidcResponseDTO);
        }
        return oidcResponseDTO;
    }

    public KnXDMSubsAliasDetailsRespDTO createOidcDeviceSharingProfile(KnOPSubsProfileInfoDTO subsProfileInfoDTO,
                                                                       KnCorpResponseDTO corpResponseDTO, String appId,
                                                                       String idmFqdn, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "createOidcDeviceProfile(KnXDMCorpGroupInfoRequestDTO)";
        KnXDMSubsAliasDetailsRespDTO oidcResponseDTO = null;
        int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
        String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
        String pwdExpiry = microServicesParamNameValueMap.get(TEMP_PASSWORD_EXPIRY);
        knLogger.info(methodName, "deviceSharingFlag", deviceSharingFlag);
        KnXDMSubsAliasDetailsReqDTO oidcSubscriberDTO = corpResponseDTO.getOidcSubscriberDTO();
        KnXDMSubsAliasDetailsReqDTO oidcSubscriberModifyDTO = null;
        Map<String, Object> oidcAttributes = new HashMap<String, Object>();
        String extCorpId = subsProfileInfoDTO.getExtCorpId();
        String sendAccountMail = String.valueOf(subsProfileInfoDTO.getSendAccountMail());
        if (!appId.equals(APP_ID.HANDSET_STANDARD.value()) || corpResponseDTO.isUserIdChanged()) {
            String action = null;
            if (null != sendAccountMail) {
                if (sendAccountMail.equals(String.valueOf(SEND_ACCOUNT_MAIL)))
                    action = TMP_PWD_MODE.VERIFICATION_MAIL.value();
            if(corpResponseDTO.isUserIdChanged() && appId.equals(APP_ID.HANDSET_STANDARD.value())){
                action = TMP_PWD_MODE.PASSWORD_INFO_MAIL.value();
            }
            if (appId.equals(APP_ID.USERMCSCLIENTS.value()) && sendAccountMail.equals(String.valueOf(SEND_ACCOUNT_MAIL))) {
                action = TMP_PWD_MODE.PASSWORD_INFO_MAIL.value();
                pwdExpiry = microServicesParamNameValueMap.get(KnConstants.MCS_TEMP_PASSWORD_EXPIRY);
            }

            List<String> actions = new ArrayList<>();
            actions.add(action);
            oidcAttributes = oidcSubscriberDTO.getAttributes();
            if (appId.equals(APP_ID.DISPATCHER.value())) {
                oidcAttributes.put(CORP_ID_EXT, extCorpId);
            }
                if ( null != sendAccountMail && sendAccountMail.equals(String.valueOf(SEND_ACCOUNT_MAIL))) {
                if (oidcAttributes != null) {
                    List<String> setActions = (List<String>) oidcAttributes.get(ACTIONS);
                    setActions.add(action);
                    oidcAttributes.put(ACTIONS, actions);
                } else {
                    oidcAttributes = new HashMap<>();
                    oidcAttributes.put(ACTIONS, actions);
                    oidcSubscriberDTO.setAttributes(oidcAttributes);
                }
            }
        }
    }
        int passwordExpiry = 0;
        if (pwdExpiry != null) passwordExpiry = Integer.parseInt(pwdExpiry);
        long currentTimeInMilliSecond = System.currentTimeMillis();
        long pwdExpiryInMilli = currentTimeInMilliSecond + (1000 * 60 * passwordExpiry);
        String randomPassword = pwdUtil.generatePassword(KnConstants.MAX_OIDC_PASSWORD_LENGTH, appId);
        Boolean tmpPwd = Boolean.FALSE;
        if (subsProfileInfoDTO.getLicenseType() == ENABLED) {
            tmpPwd = Boolean.TRUE;
            oidcSubscriberDTO.setTemppwd(tmpPwd);
            oidcSubscriberDTO.setPwd(randomPassword);
            oidcSubscriberDTO.setGeneratepwd(randomPassword == null);
            oidcSubscriberDTO.setPwdexpiry(pwdExpiryInMilli);
        }
        if (APP_ID.DISPATCHER.value().equals(appId)) {
            oidcSubscriberDTO.setPwd(randomPassword);
            oidcSubscriberDTO.setGeneratepwd(randomPassword == null);
        }

        knLogger.debug(methodName, "oidcSubscriberDTO - ", oidcSubscriberDTO, "idmFqdn - ", idmFqdn);

        String oldUserId = null;
        String oldAliasMdn = null;
        if (corpResponseDTO.isUserOverriden()) {
            oldUserId = (String) corpResponseDTO.getResponseMap().get(com.kodiak.common.resources.KnConstants.OLD_USER_ID);
        }
        if (corpResponseDTO.isAliasOverriden()) {
            oldAliasMdn = (String) corpResponseDTO.getResponseMap().get(com.kodiak.common.resources.KnConstants.OLD_ALIAS_MDN);
        }
        knLogger.debug(methodName, "oldUserId - ", oldUserId, "oldAliasMdn - ", KnGDPRTemplate.mdn(oldAliasMdn));
        String delUserForPassword = null;
        String insUserForPassword = null;
        if (corpResponseDTO.isAliasOverriden()) {
            if (subsProfileInfoDTO.getAliasMdn() != null) {
                knLogger.debug(methodName, "Entry : 1");
                delUserForPassword = subsProfileInfoDTO.getMdn();
                insUserForPassword = subsProfileInfoDTO.getAliasMdn();
                if (subsProfileInfoDTO.getUserId() != null)
                    KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, subsProfileInfoDTO.getMdn());
                oidcSubscriberModifyDTO = new KnXDMSubsAliasDetailsReqDTO();
                oidcSubscriberModifyDTO.setNewuserid(subsProfileInfoDTO.getAliasMdn() != null && !subsProfileInfoDTO.getAliasMdn().equals(oldAliasMdn)
                        ? subsProfileInfoDTO.getAliasMdn() : null);
                oidcSubscriberModifyDTO.setNewemail(corpResponseDTO.isUserOverriden() && !subsProfileInfoDTO.getUserId().equals(oldUserId)
                        ? subsProfileInfoDTO.getUserId() : null);
                oidcSubscriberModifyDTO.setPwd(randomPassword);
                oidcSubscriberModifyDTO.setGeneratepwd(randomPassword == null);
                oidcSubscriberModifyDTO.setPwdexpiry(pwdExpiryInMilli);
                oidcSubscriberModifyDTO.setAttributes(oidcSubscriberDTO.getAttributes());
                oidcSubscriberModifyDTO.setTemppwd(tmpPwd);
                if (oidcSubscriberModifyDTO.getNewuserid() != null || oidcSubscriberModifyDTO.getNewemail() != null) {
                    oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().modifyIDMUserForOIDCMgmt(oidcSubscriberModifyDTO, appId,
                            idmFqdn, oldAliasMdn);
                }

            } else {
                knLogger.debug(methodName, "Entry : 2");
                delUserForPassword = oldAliasMdn;
                insUserForPassword = subsProfileInfoDTO.getMdn();
                KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, oldAliasMdn);
                oidcSubscriberDTO.setUserid(subsProfileInfoDTO.getMdn());
                oidcSubscriberDTO.setPwd(randomPassword);
                oidcSubscriberDTO.setGeneratepwd(randomPassword == null);
                oidcSubscriberDTO.setPwdexpiry(pwdExpiryInMilli);
                oidcSubscriberDTO.setTemppwd(tmpPwd);
                oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(oidcSubscriberDTO, appId,
                        idmFqdn, false, null);
            }
        } else if (corpResponseDTO.isUserOverriden()) {
            delUserForPassword = subsProfileInfoDTO.getMdn();
            insUserForPassword = subsProfileInfoDTO.getMdn();
            if (subsProfileInfoDTO.getUserId() != null) {
                knLogger.debug(methodName, "Entry : 3");
                String oldUserName = subsProfileInfoDTO.getMdn();
                //XDM-11546 If AliasMdn already exists, new password to be updated in the GG.
                if (subsProfileInfoDTO.getAliasMdn() != null) {
                    delUserForPassword = subsProfileInfoDTO.getAliasMdn();
                    insUserForPassword = subsProfileInfoDTO.getAliasMdn();
                    oidcSubscriberDTO.setUserid(subsProfileInfoDTO.getAliasMdn());
                    oldUserName = subsProfileInfoDTO.getAliasMdn();
                }
                addMCSComplianceAttributes(oidcSubscriberDTO, subsProfileInfoDTO, subsProfileInfoDTO.getMdn(), persisterTxn);
                oidcSubscriberModifyDTO = new KnXDMSubsAliasDetailsReqDTO();
                oidcSubscriberModifyDTO.setNewemail(subsProfileInfoDTO.getUserId());
                oidcSubscriberModifyDTO.setPwd(randomPassword);
                oidcSubscriberModifyDTO.setGeneratepwd(randomPassword == null);
                oidcSubscriberModifyDTO.setPwdexpiry(pwdExpiryInMilli);
                oidcSubscriberModifyDTO.setAttributes(oidcSubscriberDTO.getAttributes());
                oidcSubscriberModifyDTO.setTemppwd(tmpPwd);
                oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().modifyIDMUserForOIDCMgmt(oidcSubscriberModifyDTO, appId,
                        idmFqdn, oldUserName);
            } else {
                knLogger.debug(methodName, "Entry : 4");
                KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, subsProfileInfoDTO.getMdn());
                oidcSubscriberDTO.setUserid(subsProfileInfoDTO.getMdn());
                oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(oidcSubscriberDTO, appId,
                        idmFqdn, false, null);
            }
        } else if (subsProfileInfoDTO.getAliasMdn() != null) {
            knLogger.debug(methodName, "Entry : 5");
            delUserForPassword = subsProfileInfoDTO.getMdn();
            insUserForPassword = subsProfileInfoDTO.getAliasMdn();
            KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, subsProfileInfoDTO.getMdn());
            oidcSubscriberDTO.setUserid(subsProfileInfoDTO.getAliasMdn());
            oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(oidcSubscriberDTO, appId,
                    idmFqdn, false, null);
        } else if (subsProfileInfoDTO.getUserId() != null) {
            knLogger.debug(methodName, "Entry : 6");
            delUserForPassword = subsProfileInfoDTO.getMdn();
            insUserForPassword = subsProfileInfoDTO.getMdn();
            KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, subsProfileInfoDTO.getMdn());
            oidcSubscriberDTO.setUserid(subsProfileInfoDTO.getMdn());
            addMCSComplianceAttributes(oidcSubscriberDTO, subsProfileInfoDTO, subsProfileInfoDTO.getMdn(), persisterTxn);
            oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(oidcSubscriberDTO, appId,
                    idmFqdn, false, null);
        } else {
            knLogger.debug(methodName, "Entry : 7");
            delUserForPassword = subsProfileInfoDTO.getMdn();
            insUserForPassword = subsProfileInfoDTO.getMdn();
            oidcSubscriberDTO.setUserid(subsProfileInfoDTO.getMdn());
            oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(oidcSubscriberDTO, appId,
                    idmFqdn, false, null);
        }
        knLogger.debug(methodName, "oidcResponseDTO", oidcResponseDTO);
        List<String> delMdns = new ArrayList<>();
        delMdns.add(delUserForPassword);
        delMdns.add(insUserForPassword);
        cacheUtil.deleteExpPasswordMDNs(delMdns);
        KnOidcTmpPwdDTO oidcTmpPwdDTO = new KnOidcTmpPwdDTO();
        if (randomPassword != null) {
            String encryptedPwd = encryptionDecryptionUtil.encrypt(oidcSubscriberDTO.getUserid(), randomPassword);
            String derivedKey = KnGeneralUtil.convertToHex(encryptedPwd.getBytes());
            knLogger.debug(methodName, "Derived key after generation and convertion: ", derivedKey);
            oidcTmpPwdDTO.setTmpPwd(KnGeneralUtil.convertHexToAscii(derivedKey));
            oidcTmpPwdDTO.setMdn(insUserForPassword);
            oidcTmpPwdDTO.setTmpPwdCreationTS(String.valueOf(currentTimeInMilliSecond));
            oidcTmpPwdDTO.setTmpPwdExpiry(String.valueOf(pwdExpiryInMilli));
            cacheUtil.insertOidcTmpPwd(oidcTmpPwdDTO);
        }
        return oidcResponseDTO;
    }

    public KnXDMSubsAliasDetailsRespDTO createOidcDeviceSharingProfileWithMCSIds(KnOPSubsProfileInfoDTO subsProfileInfoDTO,
                                                                                 String appId, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "createOidcDeviceSharingProfileWithMCSIds(KnXDMCorpGroupInfoRequestDTO)";
        KnXDMSubsAliasDetailsRespDTO oidcResponseDTO = null;
        KnXDMSubsAliasDetailsReqDTO subsAliasDetailsReqDTO = new KnXDMSubsAliasDetailsReqDTO();
        int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
        String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
        String pwdExpiry = microServicesParamNameValueMap.get(TEMP_PASSWORD_EXPIRY);
        knLogger.info(methodName, "deviceSharingFlag", deviceSharingFlag);
        String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
        String oidcUserId = null;
        if (subsProfileInfoDTO.getAliasMdn() != null) {
            oidcUserId = subsProfileInfoDTO.getAliasMdn();
        } else {
            oidcUserId = subsProfileInfoDTO.getMdn();
        }

        if(DISPATCH_CLIENT==subsProfileInfoDTO.getSubsClientType() && subsProfileInfoDTO.getUserId() != null){
            if(null!=subsProfileInfoDTO.getOldUserId()){
                oidcUserId=subsProfileInfoDTO.getOldUserId();
            }else{
                oidcUserId=subsProfileInfoDTO.getUserId();
            }

        }

        KnXDMSubsAliasDetailsReqDTO oidcSubsDto = new KnXDMSubsAliasDetailsReqDTO();

        oidcSubsDto.setUserid(oidcUserId);
        oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance()
                .notifyIDMIntfForOIDCMgmt(oidcSubsDto, appId, idmFqdn, null, HttpMethod.GET);
        knLogger.info(methodName, "Get oidcResponseDTO", oidcResponseDTO);

        Map<String, Object> oidcAttributes = new HashMap<>();
        oidcAttributes.put(MCPTT_ID_OIDC, subsProfileInfoDTO.getMdn());
        oidcAttributes.put(MCVIDEO_ID_OIDC, subsProfileInfoDTO.getMcVideoId());
        oidcAttributes.put(MCDATA_ID_OIDC, subsProfileInfoDTO.getMcDataId());
        if (appId.equals(APP_ID.DISPATCHER.value())) {
            oidcAttributes.put(CORP_ID_EXT, subsProfileInfoDTO.getExtCorpId());
        }
        int passwordExpiry = 0;
        if (pwdExpiry != null) passwordExpiry = Integer.parseInt(pwdExpiry);
        long currentTimeInMilliSecond = System.currentTimeMillis();
        long pwdExpiryInMilli = currentTimeInMilliSecond + (1000 * 60 * passwordExpiry);
        String randomPassword = pwdUtil.generatePassword(KnConstants.MAX_OIDC_PASSWORD_LENGTH, appId);
        subsAliasDetailsReqDTO.setTemppwd(Boolean.FALSE);
        subsAliasDetailsReqDTO.setPwd(randomPassword);
        subsAliasDetailsReqDTO.setGeneratepwd(randomPassword == null);
        if (subsProfileInfoDTO.getLicenseType() == ENABLED) {
            subsAliasDetailsReqDTO.setTemppwd(Boolean.TRUE);
            subsAliasDetailsReqDTO.setPwdexpiry(pwdExpiryInMilli);
        }
        if (HttpStatus.NOT_FOUND.equals(oidcResponseDTO.getStatusCode())) {
            knLogger.debug(methodName, "Entry: 1");
            subsAliasDetailsReqDTO.setUserid(oidcUserId);
            /*
             * If mcpttCompliance flag == 1 then send DeviceImpl,DeviceImpu,networkName Scopes,SipDigestPwd(plain pwd not HA1)
             * while creating oidcProfile
             * Currently this is mcpttCompliance enabled only for TP users and these users will be created in new realm with appId = 10
             * */
            if (subsProfileInfoDTO.getMcpttCompliance() == MCPTT_COMPLIANCE_ENABLED) {
                xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                String realm = xdmsServiceConfigDTO.getAuthRealm();
                knLogger.debug(methodName, "realm ", realm);
                String deviceImpl = KnConstants.TELURI + oidcUserId;

                String sipRandomPwdPlain = randomPassword != null ? randomPassword : pwdUtil.generatePassword(KnConstants.MAX_OIDC_PASSWORD_LENGTH);
                String sipdigestpwd = KnGeneralUtil.generateMD5(deviceImpl.concat(COLON).concat(realm).concat(COLON).concat(sipRandomPwdPlain));
                knLogger.debug(methodName, "generated MD5 ", sipdigestpwd);

                String deviceImpu = genInfoUtil.getDeviceImpl(deviceImpl, persisterTxn);
                oidcAttributes.put(MCPTT_ID_OIDC, subsProfileInfoDTO.getMcpttId());
                oidcAttributes.put(MC_ID_OIDC, subsProfileInfoDTO.getMcId());
                oidcAttributes.put(NETWORK_NAME, subsProfileInfoDTO.getNetworkName());
                oidcAttributes.put(DIGEST_PWD_OIDC, sipRandomPwdPlain);
                oidcAttributes.put(DEVICEIMPL_OIDC, deviceImpl);
                oidcAttributes.put(DEVICEIMPU_OIDC, deviceImpu);
                oidcAttributes.put(MCS_SCOPES_OIDC, KnGeneralUtil.populateOidcScopes(subsProfileInfoDTO.getActiveFS2()));
                knLogger.debug(methodName, "updating digest ",  KnGDPRTemplate.userId(oidcUserId));
                genInfoUtil.updateSipDigestPwd(oidcUserId, sipdigestpwd, persisterTxn);
                knLogger.debug(methodName, "updating digest ",  KnGDPRTemplate.userId(oidcUserId));
            }
            subsAliasDetailsReqDTO.setAttributes(oidcAttributes);
            subsAliasDetailsReqDTO.setEmail(subsProfileInfoDTO.getUserId());
            /*if(subsProfileInfoDTO.getSubsClientType()==DISPATCH_CLIENT){
                subsAliasDetailsReqDTO.setEmail(null);
            }*/
            oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(subsAliasDetailsReqDTO, appId,
                    idmFqdn, false, null);
        } else {
            knLogger.debug(methodName, "Entry: 2");
            boolean profileChange = false;
            Map<String, String> dbOidcAttributes = oidcResponseDTO.getAttributes();
            if (dbOidcAttributes != null) {
                if (dbOidcAttributes.get(MCPTT_ID_OIDC) != null && !dbOidcAttributes.get(MCPTT_ID_OIDC).equals(subsProfileInfoDTO.getMdn())) {
                    profileChange = true;
                } else if (dbOidcAttributes.get(MCVIDEO_ID_OIDC) != null && !dbOidcAttributes.get(MCVIDEO_ID_OIDC).equals(subsProfileInfoDTO.getMcVideoId())) {
                    profileChange = true;
                } else if (dbOidcAttributes.get(MCDATA_ID_OIDC) != null && !dbOidcAttributes.get(MCDATA_ID_OIDC).equals(subsProfileInfoDTO.getMcDataId())) {
                    profileChange = true;
                } else if (dbOidcAttributes.get(MC_ID_OIDC) != null && !dbOidcAttributes.get(MC_ID_OIDC).equals(subsProfileInfoDTO.getMcDataId())) {
                    profileChange = true;
                }
            }
            if (oidcResponseDTO.getEmail() != null && !oidcResponseDTO.getEmail().equals(subsProfileInfoDTO.getUserId())) {
                profileChange = true;
                subsAliasDetailsReqDTO.setNewemail(subsProfileInfoDTO.getUserId());
            }
            if(null!=oidcResponseDTO.getUserid()&&subsProfileInfoDTO.getSubsClientType().equals(DISPATCH_CLIENT)&& oidcResponseDTO.getUserid().equals(subsProfileInfoDTO.getUserId())){
                profileChange = true;
                subsAliasDetailsReqDTO.setUserid(subsProfileInfoDTO.getUserId());
                //subsAliasDetailsReqDTO.setEmail(subsProfileInfoDTO.getUserId());
            }
            subsAliasDetailsReqDTO.setAttributes(oidcAttributes);
            if (profileChange) {
                oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().modifyIDMUserForOIDCMgmt(subsAliasDetailsReqDTO, appId,
                        idmFqdn, oidcUserId);
            }
        }
        knLogger.debug(methodName, "oidcResponseDTO", oidcResponseDTO);
        if (randomPassword != null && subsAliasDetailsReqDTO.isTemppwd() != null && subsAliasDetailsReqDTO.isTemppwd() && subsProfileInfoDTO.getLicenseType() == ENABLED) {
            cacheUtil.deleteOldOidcTmpPwd(oidcUserId);
            KnOidcTmpPwdDTO oidcTmpPwdDTO = new KnOidcTmpPwdDTO();
            String encryptedPwd = encryptionDecryptionUtil.encrypt(oidcUserId, randomPassword);
            String derivedKey = KnGeneralUtil.convertToHex(encryptedPwd.getBytes());
            knLogger.debug(methodName, "Derived key after generation and convertion: ", derivedKey);
            oidcTmpPwdDTO.setTmpPwd(KnGeneralUtil.convertHexToAscii(derivedKey));
            oidcTmpPwdDTO.setMdn(oidcUserId);
            oidcTmpPwdDTO.setTmpPwdCreationTS(String.valueOf(currentTimeInMilliSecond));
            oidcTmpPwdDTO.setTmpPwdExpiry(String.valueOf(pwdExpiryInMilli));
            cacheUtil.insertOidcTmpPwd(oidcTmpPwdDTO);
        }
        return oidcResponseDTO;
    }
    public KnXDMSubsAliasDetailsReqDTO getSubscriberAliaRequestDTO(KnOPSubsProfileInfoDTO subsProfile,String appId,KnPersisterTxn persisterTxn) {
        String methodName = "getSubscriberAliaRequestDTO(KnOPSubsProfileInfoDTO,String,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: getSubscriberAliaRequestDTO - ", subsProfile, " appId - ", appId);
        KnXDMSubsAliasDetailsReqDTO subsAliasDetailsReqDTO = new KnXDMSubsAliasDetailsReqDTO();
        try {
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
            String pwdExpiry = microServicesParamNameValueMap.get(TEMP_PASSWORD_EXPIRY);
            int passwordExpiry = 0;
            if (pwdExpiry != null) passwordExpiry = Integer.parseInt(pwdExpiry);
            long currentTimeInMilliSecond = System.currentTimeMillis();
            long pwdExpiryInMilli = currentTimeInMilliSecond + (1000 * 60 * passwordExpiry);
            String randomPassword = pwdUtil.generatePassword(com.kodiak.common.resources.KnConstants.MAX_OIDC_PASSWORD_LENGTH, appId);

            subsAliasDetailsReqDTO.setTemppwd(Boolean.FALSE);
            subsAliasDetailsReqDTO.setPwd(randomPassword);
            subsAliasDetailsReqDTO.setGeneratepwd(randomPassword == null);
            if (subsProfile.getLicenseType() == com.kodiak.common.resources.KnConstants.ENABLED) {
                subsAliasDetailsReqDTO.setTemppwd(Boolean.TRUE);
                subsAliasDetailsReqDTO.setPwdexpiry(pwdExpiryInMilli);
            }
            String oidcUserId = null;
            if (subsProfile.getAliasMdn() != null) {
                oidcUserId = subsProfile.getAliasMdn();
            } else {
                oidcUserId = subsProfile.getMdn();
            }
            xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            String realm = xdmsServiceConfigDTO.getAuthRealm();
            knLogger.debug(methodName, "realm ", realm);
            String deviceImpl = com.kodiak.common.resources.KnConstants.TELURI + oidcUserId;
            String deviceImpu = genInfoUtil.getDeviceImpl(deviceImpl, persisterTxn);
            String sipRandomPwdPlain = randomPassword != null ? randomPassword : pwdUtil.generatePassword(com.kodiak.common.resources.KnConstants.MAX_OIDC_PASSWORD_LENGTH);
            String sipdigestpwd = KnGeneralUtil.generateMD5(deviceImpl.concat(COLON).concat(realm).concat(COLON).concat(sipRandomPwdPlain));
            Map<String, Object> oidcAttributes = new HashMap<>();
            oidcAttributes.put(MCPTT_ID_OIDC, subsProfile.getMdn());
            oidcAttributes.put(MCVIDEO_ID_OIDC, subsProfile.getMcVideoId());
            oidcAttributes.put(MCDATA_ID_OIDC, subsProfile.getMcDataId());
            oidcAttributes.put(MCPTT_ID_OIDC, subsProfile.getMcpttId());
            oidcAttributes.put(MC_ID_OIDC, subsProfile.getMcId());
            oidcAttributes.put(NETWORK_NAME, subsProfile.getNetworkName());
            oidcAttributes.put(DIGEST_PWD_OIDC, sipRandomPwdPlain);
            oidcAttributes.put(DEVICEIMPL_OIDC, deviceImpl);
            oidcAttributes.put(DEVICEIMPU_OIDC, deviceImpu);
            oidcAttributes.put(MCS_SCOPES_OIDC, KnGeneralUtil.populateOidcScopes(subsProfile.getActiveFS2()));
            subsAliasDetailsReqDTO.setUserid(oidcUserId);
            subsAliasDetailsReqDTO.setAttributes(oidcAttributes);
            subsAliasDetailsReqDTO.setEmail(subsProfile.getUserId());
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ... ", e);

        }
        return subsAliasDetailsReqDTO;
    }

    public KnOidcTmpPwdDTO getDecryptedPwd(String userId) throws Exception {
        String methodName = "getDecryptedPwd(String)";
        KnOidcTmpPwdDTO oidcTmpPwdDTO = cacheUtil.selectOidcTmpPwd(userId);
        String password = null;
        if (oidcTmpPwdDTO != null) {
            String hexString = KnGeneralUtil.convertAsciiToHex(oidcTmpPwdDTO.getTmpPwd());
            String encryptedText = new String(KnGeneralUtil.hexStringToByteArray(hexString));
            knLogger.debug(methodName, "encryptedText-", encryptedText);
            password = encryptionDecryptionUtil.decrypt(userId, encryptedText);
            oidcTmpPwdDTO.setTmpPwd(password);
        }
        knLogger.debug(methodName, "oidcTmpPwdDTO-", oidcTmpPwdDTO);
        return oidcTmpPwdDTO;
    }

    public KnXDMSubsAliasDetailsReqDTO addMCSComplianceAttributes(KnXDMSubsAliasDetailsReqDTO oidcSubscriberDTO, KnOPSubsProfileInfoDTO subsProfileInfoDTO, String userId, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "addMCSComplianceAttributes(KnXDMSubsAliasDetailsReqDTO,String)";
        if (subsProfileInfoDTO.getMcpttCompliance() == MCPTT_COMPLIANCE_ENABLED) {
            knLogger.debug(methodName, "userId-",  KnGDPRTemplate.userId(userId));
            if (null != userId && userId.length() > 1) {
                userId = userId.trim();
            }
            knLogger.debug(methodName, "userId-",  KnGDPRTemplate.userId(userId));
            Map<String, Object> oidcAttributes = oidcSubscriberDTO.getAttributes();
            String deviceImpl = subsProfileInfoDTO.getMcId();
            String randomPassword = pwdUtil.generateRandomPassword(KnConstants.MAX_OIDC_PASSWORD_LENGTH);
            String deviceImpu = genInfoUtil.getDeviceImpl(deviceImpl, persisterTxn);
            oidcAttributes.put(MCPTT_ID_OIDC, subsProfileInfoDTO.getMcpttId());
            oidcAttributes.put(MCVIDEO_ID_OIDC, subsProfileInfoDTO.getMcVideoId());
            oidcAttributes.put(MCDATA_ID_OIDC, subsProfileInfoDTO.getMcDataId());
            oidcAttributes.put(MC_ID_OIDC, subsProfileInfoDTO.getMcId());
            oidcAttributes.put(NETWORK_NAME, subsProfileInfoDTO.getNetworkName());
            oidcAttributes.put(DIGEST_PWD_OIDC, randomPassword);
            oidcAttributes.put(DEVICEIMPL_OIDC, deviceImpl);
            oidcAttributes.put(DEVICEIMPU_OIDC, deviceImpu);
            oidcAttributes.put(MCS_SCOPES_OIDC, KnGeneralUtil.populateOidcScopes(subsProfileInfoDTO.getActiveFS2()));
            knLogger.debug(methodName, "updating digest ",  KnGDPRTemplate.userId(userId));
            xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            String realm = xdmsServiceConfigDTO.getAuthRealm();
            knLogger.debug(methodName, "realm ", realm);
            String sipdigestpwd = KnGeneralUtil.generateMD5(deviceImpu.concat(COLON).concat(realm).concat(COLON).concat(randomPassword));
            genInfoUtil.updateSipDigestPwd(userId, sipdigestpwd, persisterTxn);
            knLogger.debug(methodName, "updating digest ",  KnGDPRTemplate.userId(userId));
        }
        knLogger.debug(methodName, "oidcSubscriberDTO - ", oidcSubscriberDTO);
        return oidcSubscriberDTO;
    }

    /**
     * Utitlity method for creating JobNotifyDTO
     *
     * @param corpId
     * @param txnId
     * @param userProfileId
     * @param opType
     * @param resourceEntity
     * @param resourceType
     * @return
     */
    public KnAsyncJobDTO createJobNotifyDTO(String corpId, String txnId, String userProfileId
            , int opType, String resourceEntity, int resourceType, String payload, int status, String callbackUri) {

        KnAsyncJobDTO JobReqDTO = new KnAsyncJobDTO();
        JobReqDTO.setTxnId(txnId);
        JobReqDTO.setCreationTime(String.valueOf(Instant.now().toEpochMilli()));
        JobReqDTO.setUserProfileId(userProfileId);
        JobReqDTO.setOpType(opType);
        JobReqDTO.setOpStatus(status);
        JobReqDTO.setResourceEntity(resourceEntity);
        JobReqDTO.setResourceType(resourceType);
        JobReqDTO.setCorpId(Integer.parseInt(corpId));
        JobReqDTO.setUpdationTime(String.valueOf(Instant.now().toEpochMilli()));
        JobReqDTO.setPayLoad(payload);
        JobReqDTO.setCallBackUri(callbackUri);

        return JobReqDTO;
    }

    public boolean sendMCSNotification(KnOPDirChgDTO opDirChgDTO, String mcId, String mcsXcapRootUri) {
        return sendMCSNotification(opDirChgDTO, mcId, mcsXcapRootUri, null);
    }

    /**
     * Utility method for sending notification
     *
     * @param opDirChgDTO
     * @param mcId
     * @return
     */

    public boolean sendMCSNotification(KnOPDirChgDTO opDirChgDTO, String mcId, String mcsXcapRootUri, KnNotificationParamDTO knNotificationParamDTO) {

        String methodName = "sendMCSNotification(KnOPDirChgDTO,String)";
        boolean isSuccess = false;

        knLogger.info(methodName, "inside sendMCS Notification");
        knLogger.debug(methodName, "opDirChgDTO", opDirChgDTO, "mcId",  KnGDPRTemplate.mcId(mcId));

        KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
        if (opDirChgDTO != null) {
            List<KnDocumentChangeDTO> mcsDocumentChangeDTOS = new ArrayList<>();
            mcsNotifyDTO.setXcapRootUri(opDirChgDTO.getXcapRootURI());
            mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());
            KnDocumentChangeDTO mcsDocumentChangeDTO = new KnDocumentChangeDTO();
            Collection<KnOPDocChgDTO> docChgDTOCollection = opDirChgDTO.getDocChgDTO();
            if (docChgDTOCollection != null && !docChgDTOCollection.isEmpty()) {
                List<KnDocChangeListDto> docChangeList = new ArrayList<>();
                docChgDTOCollection.stream().forEach(knOPDocChgDTO -> {
                    String mdn = getMDNFromURI(knOPDocChgDTO.getDocUri());
                    mcsDocumentChangeDTO.setMdn(mdn);
                    mcsDocumentChangeDTO.setDocType(KnMCSNotifyConstants.DOCTYPE.MDN.value());
                    KnDocChangeListDto knDocChangeListDTO = new KnDocChangeListDto();
                    knDocChangeListDTO.setDocUriList(buildMCSDOCURI(knOPDocChgDTO.getDocUri(), mcId));
                    knDocChangeListDTO.setPreviousEtag(knOPDocChgDTO.getPrevEtag());
                    knDocChangeListDTO.setNewEtag(knOPDocChgDTO.getNewEtag());
                    docChangeList.add(knDocChangeListDTO);
                });
                mcsDocumentChangeDTO.setDocChangeList(docChangeList);
                mcsDocumentChangeDTOS.add(mcsDocumentChangeDTO);
            }
            mcsNotifyDTO.setDocumentChange(mcsDocumentChangeDTOS);
            isSuccess = mcsDocChangeNotifier.generateMCSNotification(Collections.singletonList(mcsNotifyDTO), knNotificationParamDTO, null);
        }
        knLogger.info(methodName, "EXIT: sendMCS Notification - ", isSuccess);
        return isSuccess;
    }

    public String getMDNFromURI(String xcapDirURI) {
        String methodName = "getMDNFromURI(String)";
        knLogger.info(methodName, "Entry", xcapDirURI);
        String mdn = null;
        try {
            mdn = xcapDirURI.split("tel:\\+")[1].split("/")[0];
            knLogger.info(methodName, "mdn",  KnGDPRTemplate.mdn(mdn));
        } catch (Exception e) {
            knLogger.error(methodName, e);
        }
        knLogger.info(methodName, "Exit", KnGDPRTemplate.mdn(mdn));
        return mdn;
    }

    /**
     * Utility method for sending notification
     *
     * @param respDto
     * @param corpId
     * @return
     */

    public boolean sendMCSGRPNotification(KnCorpResponseDTO respDto, Set<String> mcsXcapRootUris, int corpId) {

        String methodName = "sendMCSGRPNotification(KnCorpResponseDTO,Set<String),int";
        boolean isSuccess = false;

        knLogger.info(methodName, "inside sendMCS Notification");
        knLogger.debug(methodName, "respDto", respDto, "mcsXcapRootUris", mcsXcapRootUris);

        Set<Integer> groupIds = new HashSet<>();
        Map<String, KnOPDirChgDTO> changeLogMap = respDto.getChangeLogMap();

        int deletedGroupId = respDto.getDeletedGroupId();
        if (deletedGroupId > 0) {
            groupIds.add(deletedGroupId);

        } else {
        if (changeLogMap != null && !changeLogMap.isEmpty()) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                    knLogger.debug(methodName, "MDN - ", KnGDPRTemplate.mdn(entry.getKey()));
                    String mdn = entry.getKey();
                KnOPDirChgDTO opDirChgDTO = entry.getValue();
                Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                knLogger.debug(methodName, "changeDocList - ", changeDocList);

                for (KnOPDocChgDTO docChgDTO : changeDocList) {
                    knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                        if (docChgDTO.getDocUri().contains(KnConstants.APP_UID_CORP_GROUP))
                        groupIds.add(docChgDTO.getGroupId());
                    }
                }
            }
            knLogger.debug(methodName,"sendaccntmail");
        }


        knLogger.debug(methodName, "uniqueue groupId's", groupIds);

                KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();

        mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());

        if (!groupIds.isEmpty()) {
                    List<KnDocumentChangeDTO> mcsDocumentChangeDTOS = new ArrayList<>();

                    KnDocumentChangeDTO mcsDocumentChangeDTO = new KnDocumentChangeDTO();
                    mcsDocumentChangeDTO.setDocType(KnMCSNotifyConstants.DOCTYPE.GROUP.value());

                    if (changeLogMap != null && !changeLogMap.isEmpty()) {
                        List<KnDocChangeListDto> docChangeList = new ArrayList<>();
                        int i = 0;
                        for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                            KnOPDirChgDTO opDirChgDTO = entry.getValue();
                            String pocHome = opDirChgDTO.getPocHome();
                            Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                            knLogger.debug(methodName, "changeDocList - ", changeDocList);
                            for (KnOPDocChgDTO docChgDTO : changeDocList) {
                                knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                                int groupId = respDto.getDeletedGroupId();

                        if(respDto.getMdnCorpId()>0){
                            corpId = respDto.getMdnCorpId();
                        }
                                if (groupId <= 0) {
                                    groupId = docChgDTO.getGroupId();
                                }
                                //If the document type is corporate group
                                if (groupIds.contains(groupId) && i < groupIds.size()) {
                            String groupUri = buildMCSGRPURI(corpId, groupId, docChgDTO.getDocUri(), pocHome);
                                    if(groupUri!=null && groupUri.trim().length()>0) {
                                        KnDocChangeListDto knDocChangeListDTO = new KnDocChangeListDto();
                                        knDocChangeListDTO.setDocUri(groupUri);
                                        knDocChangeListDTO.setPreviousEtag(docChgDTO.getPrevEtag());
                                        knDocChangeListDTO.setNewEtag(docChgDTO.getNewEtag());
                                        docChangeList.add(knDocChangeListDTO);
                                    }
                                    i++;
                                }
                            }
                        }
                        mcsDocumentChangeDTO.setDocChangeList(docChangeList);
                        mcsDocumentChangeDTOS.add(mcsDocumentChangeDTO);
                    }
                    mcsNotifyDTO.setDocumentChange(mcsDocumentChangeDTOS);
            knLogger.debug(methodName, "mcsXcapRootUris - ", mcsXcapRootUris);

            if (null != mcsXcapRootUris && !mcsXcapRootUris.isEmpty()) {
                for (String xcapRootUri : mcsXcapRootUris) {
                    mcsNotifyDTO.setXcapRootUri(xcapRootUri);
                    mcsNotifyDTO.getDocumentChange().forEach(knDocumentChangeDTO -> {
                        knDocumentChangeDTO.setXcapRootUri(xcapRootUri);
                    });
                    knLogger.debug(methodName, "after updating xcap root uri - ", mcsNotifyDTO);
                isSuccess = mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTO);
            }

        }
        }

        knLogger.debug(methodName, "sendMCSCGRP status - ", isSuccess);
        return isSuccess;
    }

    /**
     * Utility method for sending notification
     *
     * @param respDto
     * @param corpId
     * @return
     */

    public boolean sendMCSGRPNotificationWatcher(KnCorpResponseDTO respDto, Set<String> mcsXcapRootUris, int corpId , String pocHome) {

        String methodName = "sendMCSGRPNotificationWatcher(KnCorpResponseDTO,Set<String),int";
        boolean isSuccess = false;

        knLogger.info(methodName, "inside sendMCS Notification");
        knLogger.debug(methodName, "respDto", respDto, "mcsXcapRootUris", mcsXcapRootUris);

        Set<Integer> groupIds = new HashSet<>();
        Map<String, KnOPDirChgDTO> changeLogMap = respDto.getChangeLogMap();
        int deletedGroupId = respDto.getDeletedGroupId();
        if (deletedGroupId > 0) {
            groupIds.add(deletedGroupId);

        } else {
            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                    knLogger.debug(methodName, "MDN - ", KnGDPRTemplate.mdn(entry.getKey()));
                    String mdn = entry.getKey();
                    KnOPDirChgDTO opDirChgDTO = entry.getValue();
                    Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                    knLogger.debug(methodName, "changeDocList - ", changeDocList);

                    for (KnOPDocChgDTO docChgDTO : changeDocList) {
                        knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                        if (docChgDTO.getDocUri().contains(KnConstants.APP_UID_CORP_GROUP))
                            groupIds.add(docChgDTO.getGroupId());
                    }
                }
            }
            knLogger.debug(methodName,"sendaccntmail");
        }


        knLogger.debug(methodName, "uniqueue groupId's", groupIds);

        KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();

        mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());

        if (!groupIds.isEmpty()) {
            List<KnDocumentChangeDTO> mcsDocumentChangeDTOS = new ArrayList<>();

            KnDocumentChangeDTO mcsDocumentChangeDTO = new KnDocumentChangeDTO();
            mcsDocumentChangeDTO.setDocType(KnMCSNotifyConstants.DOCTYPE.GROUP.value());

            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                List<KnDocChangeListDto> docChangeList = new ArrayList<>();
                int i = 0;
                for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                    KnOPDirChgDTO opDirChgDTO = entry.getValue();
                    if(null !=  opDirChgDTO.getPocHome()){
                         pocHome = opDirChgDTO.getPocHome();
                    }

                    Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                    knLogger.debug(methodName, "changeDocList - ", changeDocList);
                    for (KnOPDocChgDTO docChgDTO : changeDocList) {
                        knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                        int groupId = respDto.getDeletedGroupId();

                        if(respDto.getMdnCorpId()>0){
                            corpId = respDto.getMdnCorpId();
                        }
                        if (groupId <= 0) {
                            groupId = docChgDTO.getGroupId();
                        }
                        //If the document type is corporate group
                        if (groupIds.contains(groupId) && i < groupIds.size()) {
                            String groupUri = buildMCSGRPURI(corpId, groupId, docChgDTO.getDocUri(), pocHome);
                            if(groupUri!=null && groupUri.trim().length()>0) {
                                KnDocChangeListDto knDocChangeListDTO = new KnDocChangeListDto();
                                knDocChangeListDTO.setDocUri(groupUri);
                                knDocChangeListDTO.setPreviousEtag(docChgDTO.getPrevEtag());
                                knDocChangeListDTO.setNewEtag(docChgDTO.getNewEtag());
                                docChangeList.add(knDocChangeListDTO);
                            }
                            i++;
                        }
                    }
                }
                mcsDocumentChangeDTO.setDocChangeList(docChangeList);
                mcsDocumentChangeDTOS.add(mcsDocumentChangeDTO);
            }
            mcsNotifyDTO.setDocumentChange(mcsDocumentChangeDTOS);
            knLogger.debug(methodName, "mcsXcapRootUris - ", mcsXcapRootUris);

            if (null != mcsXcapRootUris && !mcsXcapRootUris.isEmpty()) {
                for (String xcapRootUri : mcsXcapRootUris) {
                    mcsNotifyDTO.setXcapRootUri(xcapRootUri);
                    mcsNotifyDTO.getDocumentChange().forEach(knDocumentChangeDTO -> {
                        knDocumentChangeDTO.setXcapRootUri(xcapRootUri);
                    });
                    knLogger.debug(methodName, "after updating xcap root uri - ", mcsNotifyDTO);
                    isSuccess = mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTO);
                }

            }
        }

        knLogger.debug(methodName, "sendMCSCGRP status - ", isSuccess);
        return isSuccess;
    }

    public boolean prepareMcxNotifyForProfileMdns(KnCorpResponseDTO respDto) {
        String methodName = "prepareMcxNotifyForProfileMdns(KnCorpResponseDTO)";
        Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = respDto.getProfileMdnEtagMap();
        Map<String, String> profileMdnXcapRootUri = respDto.getMcsXcapRootUriMap();
        knLogger.debug(methodName, "respDto", profileMdnEtagMap, "profileMdnXcapRootUri", KnGDPRTemplate.mdnMap(profileMdnXcapRootUri));
        boolean isNotifySent = false;
        if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
            KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
            mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());
            mcsNotifyDTO.setDocumentChange(profileMdnEtagMap.entrySet().stream()
                    .map(m -> new KnDocumentChangeDTO(KnMCSNotifyConstants.DOCTYPE.MDN.value(), m.getKey(), profileMdnXcapRootUri != null
                            ? profileMdnXcapRootUri.get(m.getKey()) : null, m.getValue().stream().map(doc -> new KnDocChangeListDto(doc.getDocUri(),
                            doc.getNewEtag(), doc.getPreviousEtag(), doc.getExists())).collect(Collectors.toList())))
                    .collect(Collectors.toList()));
            knLogger.debug(methodName, "mcsNotifyDTO", mcsNotifyDTO);
            isNotifySent = mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTO);
        }
        return isNotifySent;
    }
    
    public boolean prepareMcxNotifyForProfileMdns(Map<String, Collection<com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO>> profileMdnEtagMap, Map<String, String> profileMdnXcapRootUri) {
        String methodName = "prepareMcxNotifyForProfileMdns(KnCorpResponseDTO)";
        knLogger.debug(methodName, "respDto", profileMdnEtagMap, "profileMdnXcapRootUri", profileMdnXcapRootUri);
        boolean isNotifySent = false;
        if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
            KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
            mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());
            mcsNotifyDTO.setDocumentChange(profileMdnEtagMap.entrySet().stream()
                    .map(m -> new KnDocumentChangeDTO(KnMCSNotifyConstants.DOCTYPE.MDN.value(), m.getKey(), profileMdnXcapRootUri != null
                            ? profileMdnXcapRootUri.get(m.getKey()) : null, m.getValue().stream().map(doc -> new KnDocChangeListDto(doc.getDocUri(),
                            doc.getNewEtag(), doc.getPreviousEtag(), doc.getExists())).collect(Collectors.toList())))
                    .collect(Collectors.toList()));
            knLogger.debug(methodName, "mcsNotifyDTO", mcsNotifyDTO);
            isNotifySent = mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTO);
        }
        return isNotifySent;
    }

    public boolean prepareMcxNotifyForProfileMdns(KnOPChgAuthStatusRespDTO respDto) {
        String methodName = "prepareMcxNotifyForProfileMdns(KnCorpResponseDTO)";
        Map<String, Collection<com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO>> profileMdnEtagMap = respDto.getProfileMdnEtagMap();
        Map<String, String> profileMdnXcapRootUri = respDto.getMcsXcapRootUriMap();
        knLogger.debug(methodName, "respDto", profileMdnEtagMap, "profileMdnXcapRootUri", KnGDPRTemplate.mdnMap(profileMdnXcapRootUri));
        boolean isNotifySent = false;
        if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
            KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
            mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());
            mcsNotifyDTO.setDocumentChange(profileMdnEtagMap.entrySet().stream()
                    .map(m -> new KnDocumentChangeDTO(KnMCSNotifyConstants.DOCTYPE.MDN.value(), m.getKey(), profileMdnXcapRootUri != null
                            ? profileMdnXcapRootUri.get(m.getKey()) : null, m.getValue().stream().map(doc -> new KnDocChangeListDto(doc.getDocUri(),
                            doc.getNewEtag(), doc.getPreviousEtag(), doc.getExists())).collect(Collectors.toList())))
                    .collect(Collectors.toList()));
            knLogger.debug(methodName, "mcsNotifyDTO", mcsNotifyDTO);
            isNotifySent = mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTO);
        }
        return isNotifySent;
    }

    public List<String> buildMCSDOCURI(String docUri, String mcId) {
        String methodName = "buildMCSDOCURI(String)";
        List<String> docUriList = new ArrayList<>();
        if (docUri.contains(APP_UID_SUBSCRIBER_CONFIG)) {
                docUriList.add(String.format("org.3gpp.%s.user-profile/users/%s/user-profile.xml","mcptt",mcId));
                docUriList.add(String.format("org.3gpp.%s.user-profile/users/%s/user-profile.xml","mcdata",mcId));
                docUriList.add(String.format("org.3gpp.%s.user-profile/users/%s/user-profile.xml","mcvideo",mcId));
        }
        knLogger.info(methodName, "EXIT: ", docUriList);
        return docUriList;
    }


    public String buildMCSGRPURI(int corpId, int groupId,String docUri,String pocHome) {
        String methodName = "buildMCSGRPURI(int,int,String)";
        knLogger.info(methodName, "Entry", corpId,"groupId - ",groupId,"docUri-",docUri);

        StringBuilder mcsDocUri = new StringBuilder();
        String sipProxyUri = null;
        try {
            KnSIPProxySvcConfigDTO knSIPProxySvcConfigDTO= provInfoUtil.retrieveSIPProxySvcConfig(pocHome);
            sipProxyUri = knSIPProxySvcConfigDTO.getSipProxyURI();
        }catch (Exception e){
            knLogger.warn(methodName, "exception while fetching sip proxy config");
        }

        if (docUri.contains(KnConstants.APP_UID_CORP_GROUP)) {
            mcsDocUri.append("org.openmobilealliance.groups/global/byGroupID/")
                    .append(SIP)
                    .append(corpId)
                    .append(DOT)
                    .append(groupId)
                    .append(AT)
                    .append(sipProxyUri);

        }
        knLogger.info(methodName, "Exit:", mcsDocUri);
        return mcsDocUri.toString();
    }
    
    public void sendDeleteDeviceNotification(String  deviceIMPI) {
		String methodName="sendDeleteDeviceNotification";
		String registerPocHome = null;
		try {
			registerPocHome = KnGeneralCacheUtil.getInstance()
					.getRegisterPOCHomeByDeviceIMPI(deviceIMPI);
		} catch (Exception ex) {
			knLogger.error(methodName, "Failed to retrive device registerPocHome ", ex);
		}
		knLogger.info(methodName, "device POCHOME - ", registerPocHome);

		if (registerPocHome != null) {
			KnDeleteDeviceDTO deleteDeviceDTO = new KnDeleteDeviceDTO();
			deleteDeviceDTO.setDeviceIMPI(deviceIMPI);
			deleteDeviceDTO.setPocHome(registerPocHome);
			boolean notifyStatus = KnXcapDiffNotifier.getInstance()
					.generateDeleteDeviceNotification(deleteDeviceDTO);
			knLogger.info(methodName, "Notification send status ", notifyStatus);
		} else {
			knLogger.info(methodName, "Notification skipped device POCHOME is   ", registerPocHome);
		}
	}

    /**
     * Method to get the group diff data without changeLog Map and only for groupSharing flag for sending micro services notification for group change events.
     * @param corpId
     * @param createdBy
     * @param lmrIntrop
     * @param mcxInd
     * @param groupSharedMap
     * @return
     */
    private static List<KnCorpEXDMSNotifyDto> getGroupSharedModifiedNotifyJson(int corpId, Integer createdBy, Integer lmrIntrop, Integer mcxInd,
                                                                               Map<Integer, KnCorpGroupInfoDTO> groupSharedMap, String grpSIPUri,
                                                                               String recordingFs,Integer oldLmr) {
        final String methodName = "getGroupSharedModifiedNotifyJson()";
        knLogger.debug(methodName, "Entry groupSharedMap - ",groupSharedMap," grpSIPUri :",grpSIPUri," lmrIntrop :",lmrIntrop);
        List<KnCorpEXDMSNotifyDto> corpEXDMSNotifyDtoList = new ArrayList<>();
        Map<Integer, KnCorpEXDMSNotifyDto> modifiedMap = new HashMap<>();
        if (groupSharedMap != null && !groupSharedMap.isEmpty()) {
            for (KnCorpGroupInfoDTO grp : groupSharedMap.values()) {
                Integer groupId = grp.getGroupId();
                knLogger.debug(methodName, "Replace doc type for group id- ", groupId);
                // group modified populating for group modified data

                KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                corpEXDMSNotifyDto.setGrpId(groupId);
                corpEXDMSNotifyDto.setGrpType(grp.getGroupType());
                corpEXDMSNotifyDto.setGrpSIPUri(KnGenInfoUtil.mcpttGroupUri(groupId,corpId,grpSIPUri));
                corpEXDMSNotifyDto.setLmrInteropFlag(lmrIntrop);
                corpEXDMSNotifyDto.setRecordingFs(recordingFs);
                if (null != grp.getGroupDisplayName()) {
                    corpEXDMSNotifyDto.setGrpName(grp.getGroupDisplayName());
                }
                corpEXDMSNotifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + groupId);
                if (createdBy != null) corpEXDMSNotifyDto.setCreatedBy(createdBy);
                corpEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                corpEXDMSNotifyDto.setCorpid(corpId);
                corpEXDMSNotifyDto.setOsmIdChanged(true);
                corpEXDMSNotifyDto.setGrpShare(grp.getGrpShared());
                corpEXDMSNotifyDto.setEtag(grp.getETag());
                corpEXDMSNotifyDto.setPreviousEtag(grp.getETag()-1);
                if(mcxInd !=null)
                    corpEXDMSNotifyDto.setGrpCategoryInd(mcxInd);
                if (null != oldLmr) {
                    corpEXDMSNotifyDto.setOldLmrInteropFlag(oldLmr);
                }
                corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + groupId);
                modifiedMap.put(groupId, corpEXDMSNotifyDto);
            }
        }

        knLogger.debug(methodName, " modifiedMap - ", modifiedMap);
        corpEXDMSNotifyDtoList.addAll(modifiedMap.values());
        knLogger.debug(methodName, " Returning - ", corpEXDMSNotifyDtoList);
        return corpEXDMSNotifyDtoList;
    }

 public String getCommaSeparated(List<Integer> idList) {
        String methodName="getCommaSeparated()";
        knLogger.debug(methodName, idList);
        String resp = null;
        if(idList != null && !idList.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for(Integer id : idList){
                sb.append(id).append(",");
            }
            resp = sb.toString();
            resp = resp.substring(0, resp.length()-1);
        }
        knLogger.debug(methodName, resp);
        return resp;
    }

    /**
     * create group micro service event formation for create bulk groups
     * @param respDto
     * @param corpId
     * @return
     */
    public List<KnCorpEXDMSNotifyDto> getBulkGrpMicroSrvNotifyDto(KnCorpGroupInfoRespDTO respDto, int corpId) {
        String methodName = "getBulkGrpMicroSrvNotifyDto()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "respDto - ", respDto);
        List<KnCorpEXDMSNotifyDto> exdmsNotifyDtoList = new ArrayList<>();
        //getGroupInfoList as loop
        List<KnCorpGroupInfoDTO> groupInfoDTOList = respDto.getGroupInfoDTOList();
        if (groupInfoDTOList != null && !groupInfoDTOList.isEmpty()) {
            groupInfoDTOList.forEach(groupInfo -> {
                KnCorpEXDMSNotifyDto notifyDto = new KnCorpEXDMSNotifyDto();
                notifyDto.setCorpid(corpId);
                notifyDto.setGrpId(groupInfo.getGroupId());
                notifyDto.setGrpName(groupInfo.getGroupDisplayName());
                notifyDto.setEtag(groupInfo.getETag());
                notifyDto.setGrpType(groupInfo.getGroupType());
                notifyDto.setGrpUri("kn-corp-groups=" + corpId + "_" + groupInfo.getGroupId());
                notifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                notifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.CREATE_CORP_GROUP.value() + "_" + groupInfo.getGroupId());
                notifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.CREATE_CORP_GROUP.value());
                notifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                notifyDto.setCreatedBy(respDto.getGroupCreatedBy());
                notifyDto.setLmrInteropFlag(groupInfo.getLmrInteropCapable());
                notifyDto.setGrpCategoryInd(respDto.getMcxGrpInd());
                notifyDto.setGrpShare(groupInfo.getGrpShared());

                knLogger.debug(methodName, "notifyDto - ", notifyDto);
                exdmsNotifyDtoList.add(notifyDto);
            });
        }
        knLogger.debug(methodName, "exdmsNotifyDtoList - ", exdmsNotifyDtoList);
        //end for loop
        return exdmsNotifyDtoList;
    }

    /**
     * delete group micro service event formation
     * @param respDto
     * @return
     */
    public List<KnCorpEXDMSNotifyDto> getDeleteGrpMicroSrvNotifyDto(KnCorpResponseDTO corpResp) {
        String methodName = "getDeleteGrpMicroSrvNotifyDto()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "respDto - ", corpResp);
        List<KnCorpEXDMSNotifyDto> delGrpNotifyList = new ArrayList<>();
        Map<Integer, String> delGrpPocHomeMap = corpResp.getDelGrpPocHomeMap();
        if (delGrpPocHomeMap != null && !delGrpPocHomeMap.isEmpty()) {
            Set<Integer> grpIdList = delGrpPocHomeMap.keySet();
            for (Integer grpId : grpIdList) {
                KnCorpEXDMSNotifyDto notifyDto = new KnCorpEXDMSNotifyDto();
                notifyDto.setType(MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                notifyDto.setId(MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value() + "_" + grpId);
                notifyDto.setNotifyEventType(MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                notifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                notifyDto.setGrpId(grpId);
                notifyDto.setCorpid(corpResp.getMdnCorpId());
                notifyDto.setCreatedBy(corpResp.getGroupCreatedBy());
                String pocHome = delGrpPocHomeMap.get(grpId);
                notifyDto.setPocHome(pocHome);

                knLogger.info(methodName, "poc home value : ", pocHome, " grpId - ", grpId);
                knLogger.debug(methodName, "notifyDto - ", notifyDto);
                delGrpNotifyList.add(notifyDto);


            }
        }

        knLogger.debug(methodName, "exdmsNotifyDtoList - ", delGrpNotifyList);
        return delGrpNotifyList;
    }

    //removes all offline mdn from the object
    public void removeOffLineMdns(KnCorpResponseDTO respDto){
        if(respDto.getChangeLogMap()!=null&&!respDto.getChangeLogMap().isEmpty()){
            respDto.getChangeLogMap().keySet().removeIf(e->!respDto.getOnlineMdnList().contains(e));
        }
    }

    public KnOPDirChgDTO fetchMdnsFromDirChgDto(KnOPDirChgDTO dirChgDTO, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "fetchMdnsFromDirChgDto";
        knLogger.debug(methodName,"dirChgDTO.getDirUri() : ", dirChgDTO.getDirUri());
        String dirChngMdn = dirChgDTO.getDirUri().substring(dirChgDTO.getDirUri().indexOf("tel:+") + 5, commonInfoUtil.ordinalIndexOf(dirChgDTO.getDirUri(), "/", 3));
        // Filtering to get all the mdns from the docUri
        List<String> presentMdnList = dirChgDTO.getDocChgDTO().stream().map(x -> x.getDocUri().substring(x.getDocUri().indexOf("tel:+") + 5,
                commonInfoUtil.ordinalIndexOf(x.getDocUri(), "/", 5))).collect(Collectors.toList());
        presentMdnList.add(dirChngMdn);
        // Filtering all the active mdn's
        List<String> activeMdnList = commonInfoUtil.getActiveMdns(presentMdnList, pttServerId, null);
        // get All mdns which are PV<13 (send notifications to all if PV<13)
        List<String> mdnsLessThanThirteenPv = commonInfoUtil.getMdnsLessThanThirteenPv(presentMdnList, pttServerId, null);
        if (!mdnsLessThanThirteenPv.isEmpty()) {
            activeMdnList.addAll(mdnsLessThanThirteenPv);
        }
        Set<String> activeMdnSet = new HashSet<>(activeMdnList);
        // Collecting only active DocUri
        List<KnOPDocChgDTO> activeDocChgDto = dirChgDTO.getDocChgDTO().stream().filter(x -> activeMdnSet.contains(x.getDocUri().
                substring(x.getDocUri().indexOf("tel:+") + 5, commonInfoUtil.ordinalIndexOf(x.getDocUri(), "/", 5)))).collect(Collectors.toList());
        if (!activeMdnSet.contains(dirChngMdn)) {
            knLogger.debug(methodName, "No activeMdns found");
            return null;
        }

        // Returning the active DocUri for further processing
        knLogger.info(methodName, "EXIT :- ", activeDocChgDto.size());
        dirChgDTO.setDocChgDTO(activeDocChgDto);
        return dirChgDTO;
    }

    public KnXcapDiffNotifyDTO fetchMdnsFromXcapDiffNotifyObj(KnXcapDiffNotifyDTO xcapDiffNotifyObj, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "fetchMdnsFromXcapDiffNotifyObj";
        knLogger.debug(methodName, "Entry: ", xcapDiffNotifyObj.getDirURI());
        String dirMdn = xcapDiffNotifyObj.getDirURI().substring(xcapDiffNotifyObj.getDirURI().indexOf("tel:+") + 5,
                commonInfoUtil.ordinalIndexOf(xcapDiffNotifyObj.getDirURI(), "/", 3));
        // Filtering to get all the mdns from the DocumentSelector
        List<String> presentMdnList = new ArrayList<>();
        if (null != xcapDiffNotifyObj.getDocDiffObj()) {
            presentMdnList = xcapDiffNotifyObj.getDocDiffObj().stream().map(x -> x.getDocumentSelector().substring(x.getDocumentSelector().indexOf("tel:+") + 5,
                    commonInfoUtil.ordinalIndexOf(x.getDocumentSelector(), "/", 5))).collect(Collectors.toList());
        }
        presentMdnList.add(dirMdn);
        // Filtering all the active mdn's
        List<String> activeMdnList = commonInfoUtil.getActiveMdns(presentMdnList, pttServerId, null);
        // get All mdns which are PV<13 (send notifications to all if PV<13)
        List<String> mdnsLessThanThirteenPv = commonInfoUtil.getMdnsLessThanThirteenPv(presentMdnList, pttServerId, null);
        if (!mdnsLessThanThirteenPv.isEmpty()) {
            activeMdnList.addAll(mdnsLessThanThirteenPv);
        }
        Set<String> activeMdnSet = new HashSet<>(activeMdnList);
        if (!activeMdnSet.isEmpty() && activeMdnSet.contains(dirMdn)) {
            // Collecting only active DocumentSelector
            if (null != xcapDiffNotifyObj.getDocDiffObj()) {
                List<KnXcapDiffDocDTO> xcapDiffDocDtos = xcapDiffNotifyObj.getDocDiffObj().stream().filter(x -> activeMdnSet.contains(x.getDocumentSelector().
                        substring(x.getDocumentSelector().indexOf("tel:+") + 5, commonInfoUtil.ordinalIndexOf(x.getDocumentSelector(), "/", 5)))).collect(Collectors.toList());
                xcapDiffNotifyObj.setDocDiffObj(xcapDiffDocDtos);
            }
        } else {
            xcapDiffNotifyObj = null;
        }
        // Returning the active DocUri for further processing
        knLogger.info(methodName, "EXIT :- ", xcapDiffNotifyObj);
        return xcapDiffNotifyObj;
    }

    public Collection<KnXcapDiffDirChgNotifyDTO> fetchXcapDiffList(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "fetchXcapDiffList";
        knLogger.debug(methodName, "Entry: ", xcapDiffList);
        List<KnXcapDiffDirChgNotifyDTO> xcapDiffDirChgNotifyDtos = null;
        if (xcapDiffList != null && !xcapDiffList.isEmpty()) {
            // Filtering to get all the mdns from the DocumentSelector
            List<String> presentMdnList = xcapDiffList.stream()
                    .map(x -> {
                        String dirURI = x.getDirURI();
                        if (dirURI != null) {
                            int startIndex = dirURI.indexOf("tel:+") + 5;
                            int endIndex = commonInfoUtil.ordinalIndexOf(dirURI, "/", 3);
                            if (startIndex >= 5 && endIndex > startIndex) {
                                return dirURI.substring(startIndex, endIndex);
                            }
                        } else {
                            knLogger.warn(methodName, " directory Uri is Null in  xcapDiffList: ", xcapDiffList);
                        }
                        return null; // or a default value if needed
                    })
                    .filter(Objects::nonNull) // to remove null values from the list
                    .collect(Collectors.toList());

            // Filtering all the active mdn's whose PV>=13
            List<String> activeMdnList = commonInfoUtil.getActiveMdns(presentMdnList, pttServerId, null);
            // get All mdns which are PV<13 (send notifications to all if PV<13)
            List<String> mdnsLessThanThirteenPv = commonInfoUtil.getMdnsLessThanThirteenPv(presentMdnList, pttServerId, null);
            if (!mdnsLessThanThirteenPv.isEmpty()) {
                activeMdnList.addAll(mdnsLessThanThirteenPv);
            }
            // Collecting only active DocumentSelector
            xcapDiffDirChgNotifyDtos = xcapDiffList.stream()
                    .filter(x -> {
                        String dirURI = x.getDirURI();
                        return dirURI != null && activeMdnList.contains(dirURI.substring(dirURI.indexOf("tel:+") + 5, commonInfoUtil.ordinalIndexOf(dirURI, "/", 3)));
                    })
                    .collect(Collectors.toList());
            // Returning the active DocUri for further processing
            knLogger.info(methodName, "EXIT :- ", xcapDiffDirChgNotifyDtos.size());
        }
        return xcapDiffDirChgNotifyDtos;
    }

    public KnCorpResponseDTO fetchActiveProfileMdns(KnCorpResponseDTO respDto, String pttServerId) throws KnCorpBOException {
        Set<String> presentMdnSet = respDto.getProfileMdnEtagMap().keySet();
        List<String> presentMdnList = new ArrayList<>(presentMdnSet);
        // Filtering all the active mdn's
        List<String> activeMdnList = commonInfoUtil.getActiveMdns(presentMdnList, pttServerId, null);
        // get All mdns which are PV<13 (send notifications to all if PV<13)
        List<String> mdnsLessThanThirteenPv = commonInfoUtil.getMdnsLessThanThirteenPv(presentMdnList, pttServerId, null);
        if (!mdnsLessThanThirteenPv.isEmpty()) {
            activeMdnList.addAll(mdnsLessThanThirteenPv);
        }
        // Collecting only active mdn
        for (String mdn : activeMdnList) {
            if (!respDto.getProfileMdnEtagMap().containsKey(mdn)) {
                respDto.getProfileMdnEtagMap().remove(mdn);
            }
        }
        // Returning the active DocUri for further processing
        return respDto;
    }

    public KnOPUpdateSubsInfoDTO fetchActiveProfileMdns(KnOPUpdateSubsInfoDTO respDto, String pttServerId) throws KnCorpBOException {
        Set<String> presentMdnSet = respDto.getProfileMdnEtagMap().keySet();
        List<String> presentMdnList = new ArrayList<>(presentMdnSet);
        // Filtering all the active mdn's
        List<String> activeMdnList = commonInfoUtil.getActiveMdns(presentMdnList, pttServerId, null);
        // get All mdns which are PV<13 (send notifications to all if PV<13)
        List<String> mdnsLessThanThirteenPv = commonInfoUtil.getMdnsLessThanThirteenPv(presentMdnList, pttServerId, null);
        if (!mdnsLessThanThirteenPv.isEmpty()) {
            activeMdnList.addAll(mdnsLessThanThirteenPv);
        }
        // Collecting only active mdn
        for (String mdn : activeMdnList) {
            if (!respDto.getProfileMdnEtagMap().containsKey(mdn)) {
                respDto.getProfileMdnEtagMap().remove(mdn);
            }
        }
        // Returning the active DocUri for further processing
        return respDto;
    }

    /*
     * Ensure that the mdn is not null or empty, then validate the subscriber's authorization status.
     * If the authorization status is marked as 'async delete', throw an exception to indicate that the deletion is in progress.
     */
    public void validateSubAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnProvException, KnDAOException {
        String methodName = "validateSubAuthStatus";

        if (mdns != null && !mdns.isEmpty() && mdns.stream().anyMatch(Objects::nonNull)) {
            knLogger.debug(methodName, "Validating MDNs service auth status - ", mdns);

            // Fetch the map containing MDN and service auth status
            Map<String, Integer> subscriberServiceAuthStatusMap = provClientIntf.getSubscriberServiceAuthStatus(mdns, persisterTxn);

            for (Map.Entry<String, Integer> entry : subscriberServiceAuthStatusMap.entrySet()) {
                String currentMdn = entry.getKey();
                Integer subscriberServiceAuthStatus = entry.getValue();

                // Validate the service auth status for each MDN
                if (subscriberServiceAuthStatus != null
                        && com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value() == subscriberServiceAuthStatus) {
                    knLogger.error(methodName, "Inactive subscriber deletion in-progress for MDN=",currentMdn, " ServiceAuthStatus: ", subscriberServiceAuthStatus);
                    throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.Validator.INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS, "Inactive subscriber deletion in-progress for MDN: " + currentMdn
                    );
                }
            }
        }
    }

    /*
     * Validate the subscriber's authorization status by UserId.
     * If the authorization status is marked as 'async delete', throw an exception to indicate that the deletion is in progress.
     */
    public void validateSubAuthStatusByUserId(String userId, KnPersisterTxn persisterTxn) throws KnProvException, KnDAOException {
        String methodName = "validateSubAuthStatusByUserId";
        if (userId!=null && !userId.isEmpty()) {
            knLogger.debug(methodName, "Validating UserId service auth status for UserId- ", userId);
            int subscriberServiceAuthStatus = provClientIntf.getSubscriberServiceAuthStatusByUserId(userId, persisterTxn);
                if (subscriberServiceAuthStatus == com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value()) {
                    knLogger.error(methodName, "Inactive subscriber deletion in-progress for UserId=",userId, " ServiceAuthStatus: ", subscriberServiceAuthStatus);
                    throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.Validator.INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS, "Inactive subscriber deletion in-progress for UserId: " + userId
                    );
                }
            knLogger.info(methodName, "EXIT: subscriberServiceAuthStatus ", subscriberServiceAuthStatus);
        }
    }

    private String getHierarchyId(Map<String, Object> customParamMap) {
        String methodName = "getHierarchyId()";
        String hierarchyId = null;
        if (customParamMap == null || customParamMap.isEmpty()) {
            return null;
        }
        knLogger.debug(methodName, "customParamMap", customParamMap);
        try {
            if ("3".equals(customParamMap.get(KnConstants.IDTYPE))) {
                Object idListObject = customParamMap.get(KnConstants.IDTYPE);
                if (idListObject == null) {
                    return null;
                } else {
                    if (idListObject instanceof Collection<?>) {
                        List<?> idList = (List<?>) idListObject;
                        if (idList.isEmpty() || idList.get(0) == null) {
                            return null;
                        }
                        hierarchyId = String.valueOf(idList.get(0));
                    }
                }
            } else {
                knLogger.debug(methodName, "Idtype is not 3", customParamMap.get(KnConstants.IDTYPE));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Execption in getting hierarchyId", e);
        }
        knLogger.debug(methodName, "hierarchyId", hierarchyId);
        return hierarchyId;
    }
    public String getDefaultPTTSettingDocValue(KnOPSubsProfileInfoDTO subsProfileInfoDTO ,KnPersisterTxn persisterTxn ){
        String methodName="";
        String pttSettingDocId = null;
        // Determine PTT Setting Doc ID with proper fallback logic
        int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        try {
            String hierarchyId = subsProfileInfoDTO.getHierarchyId();
            String systemDefaultPttSettingDoc = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(com.kodiak.xdms.server.common.resources.KnConstants.SYSTEM_DEFAULT_PTTSETTINGDOCID);
            knLogger.debug(methodName, "Subs addl - ptt setting docId:", subsProfileInfoDTO.getPttSettingDocId()," ,Hierarchy ID:",hierarchyId, ", system default -", systemDefaultPttSettingDoc);
            // Priority: Subscriber's assigned doc > Corp default doc > System default doc

            if (subsProfileInfoDTO.getPttSettingDocId() != null && !subsProfileInfoDTO.getPttSettingDocId().isEmpty()) {
                // Use subscriber's assigned PTT setting doc
                pttSettingDocId = subsProfileInfoDTO.getPttSettingDocId();
                knLogger.debug(methodName, "Using subscriber's assigned PTT setting doc:", pttSettingDocId);
            } else {
                // Fallback to corp-level default
                Set<KnCorpPTTSettingDocRespDTO> pttSettingDocIds = pttSettingsUtil.getPTTSettingDocIds(subsProfileInfoDTO.getCorpId(), hierarchyId, xdmPttServerId, persisterTxn);

                if (pttSettingDocIds != null && !pttSettingDocIds.isEmpty()) {
                    pttSettingDocId = pttSettingDocIds.stream()
                            .filter(doc -> doc.getIsDefault() == 1)
                            .map(KnCorpPTTSettingDocRespDTO::getPttSettingId)
                            .filter(id -> id != null && !id.isBlank())
                            .findFirst()
                            .orElse(systemDefaultPttSettingDoc);
                    knLogger.debug(methodName, "Using corp-level default PTT setting doc:", pttSettingDocId);
                } else {
                    // Fallback to system default
                    pttSettingDocId = systemDefaultPttSettingDoc;
                    knLogger.debug(methodName, "No corp-level default found, using system default PTT setting doc:", pttSettingDocId);
                }
            }
        }catch (Exception e){
            knLogger.error(methodName, "Unexpected exception while resolving default PTT setting doc value - ", e);
        }
        return pttSettingDocId;
    }

}