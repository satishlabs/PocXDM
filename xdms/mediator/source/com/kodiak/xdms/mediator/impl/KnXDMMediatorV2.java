/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.xdms.mediator.impl;

import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dto.*;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.*;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.library.activation.clientintf.impl.KnActivationClientIntf;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.utilities.syncgateway.dto.KnSubscriberInfoDTO;
import com.kodiak.utilities.syncgateway.dto.KnSyncResponseDTO;
import com.kodiak.xdms.mediator.IXDMMediatorIntf;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.mediator.helper.*;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.mediator.resources.jobs.KnDispGrpMemChecker;
import com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnUPMJobScheduler;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.*;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnDeleteDeviceDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpSublistInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpSubsProvInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpFailedData;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.processor.KnCorpDocCleanUpProcessor;
import com.kodiak.xdms.server.corpmgmt.processor.KnCorpUpdateSubscProcessor;
import com.kodiak.xdms.server.corpmgmt.processor.KnProvChangeMdnProcessor;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyBulkDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyDto;
import com.kodiak.xdms.server.subsmgmt.processor.KnProvCreateSubscProcessor;
import com.kodiak.xdms.server.subsmgmt.processor.KnProvDeleteSubscProcessor;
import com.kodiak.xdms.server.subsmgmt.processor.KnProvUpdateSubscProcessor;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvEntityTypes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvOperationTypes;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.*;
import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;
import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.CHANGE_SERVICE_AUTH_STATUS;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_18;
import static com.kodiak.common.resources.KnGeneralUtil.ONLY_ETAG_UPDATE;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH;
import static com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.common.resources.KnConstants.TRUE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DISPATCH_CLIENT;
import static com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.NO_CHG_IN_PROFILE;
import static com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants.TEL_URI_TEMPLATE;

public class KnXDMMediatorV2 implements IXDMMediatorIntf {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMMediatorV2.class);

    private static Long totalProcessingTime = 0L;
    private static Long totalRequestCount = 0L;

    private KnXDMProvMediator provMediator = null;
    private KnXDMCommonMediator commonMediator = null;
    private KnXDMPubDocMediator pubDocMediator = null;
    private KnXDMCorpMediator corpMediator = null;
    private KnXDMPubMediator pubMediator = null;
    IProvClientIntf provClientIntf;
    private static KnXDMMediatorV2 instance = null;
    private static boolean isInitialized = false;
    private int maxNotificationSize;
    private KnJobSchedulerImpl scheduler;
    private int msgTruncationLimit = 800; // default - 800
    private KnAuditHelper auditPublic;
    private KnAuditHelper auditPam;
    IXcapDiffNotifierIntf notifier;
    private static final String EMPTY_STRING = "";
    private ICorpClientIntf corpClientIntf;
    private KnXDMPamHelper xdmPamHelper = null;
    private static int BULK_BATCH_SIZE = 50;
    private KnGenInfoUtil genInfoUtil;
    private KnActivationClientIntf clientIntf;
    private KnGeneralCacheUtil generalCacheUtil;
    private static KnGeneralUtil generalUtil;
    private KnEncryptionDecryptionUtil encryptionDecryptionUtil;
    private KnGeneralPasswordUtil pwdUtil;
    private KnUPMJobScheduler upmJobScheduler;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnProvInfoUtil provInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnXDMMediatorHelper xdmMediatorHelper;
    private static final String UNEXPECTED_EXCEPTION = "Un-Expected Exception occurred";
    private static final String TRANSACTION_EXCEPTION = "Failed to get the Transaction";
    private static final String TRANSACTION_COMMIT = "Saving the transaction";
    private static final String TRANSACTION_OPEN = "Opening the Transaction";


    private KnXDMMediatorV2() {
        commonMediator = KnXDMCommonMediator.getInstance();
        provMediator = KnXDMProvMediator.getInstance();
        pubDocMediator = KnXDMPubDocMediator.getInstance();
        pubMediator = KnXDMPubMediator.getInstance();
        corpMediator = KnXDMCorpMediator.getInstance();
        provClientIntf = KnProvClientImpl.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        maxNotificationSize = 800; // TODO read from SVC config
        scheduler = KnJobSchedulerImpl.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        corpClientIntf = new KnCorpClientImpl();
        xdmPamHelper = KnXDMPamHelper.getInstance();
        // removed setting of the synch notify threshold
        // notifier.setSyncNotfyThresold(syncNotifyThresold);
        scheduler = KnJobSchedulerImpl.getInstance();
        knLogger.info("KnXDMMediatorV2()", "Initializing Audit");
        auditPublic = KnAuditHelper.getAuditLogger("4001");
        auditPam = KnAuditHelper.getAuditLogger(KnMediatorConstants.AUDIT_LOGGER_CONSTANT);
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        generalUtil = new KnGeneralUtil();
        encryptionDecryptionUtil = KnEncryptionDecryptionUtil.getInstance();
        pwdUtil = KnGeneralPasswordUtil.getInstance();
        upmJobScheduler = KnUPMJobScheduler.getInstance();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        provInfoUtil = new KnProvInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        xdmMediatorHelper = KnXDMMediatorHelper.getInstance();
        knLogger.info("KnXDMMediatorV2()", "Audit initialized - ");
    }

    public static KnXDMMediatorV2 getInstance() {
        if (!isInitialized) {
            knLogger.debug("getInstance", "Initializing the XDM Mediator V2");
            instance = new KnXDMMediatorV2();
            isInitialized = true;
        }
        return instance;
    }

    @Override
    public IXDMResponseDTO authenticate(IXDMRequestDTO authRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpMasterList(IXDMRequestDTO contactRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubscContactList(IXDMRequestDTO contactRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyCorpSubscContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO pushSublists(IXDMRequestDTO sublistRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO addCorpContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyCorpContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeCorpContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpContactDetails(IXDMRequestDTO conactRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSublistDetails(IXDMRequestDTO sublistReqDto) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAllSublist(IXDMRequestDTO corpInfoDto) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDistributionList(IXDMRequestDTO distributionInfoDto) {
        return null;
    }

    @Override
    public IXDMResponseDTO createCorpGroup(KnMessage message, Boolean isWithProfileReq) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyCorpGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO groupRehome(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteCorpGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpGroupDetails(IXDMRequestDTO groupInfoDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpGroupList(IXDMRequestDTO corpInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubscriberGroupList(IXDMRequestDTO subscriberCorpInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPocLinkedGroupList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberEmailId(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO saveClientActivationMail(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateActivationCodes(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMailInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendActivationMail(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendMail(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySubscCorpFeature(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySubscriberScanList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAllBillingMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getLicenseSubs(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateBillingName(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO markSubsForDeletion(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO licenseAuthenticate(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrReverseContacts(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrSublists(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSubscribersContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSubscribersAllSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSubscribersAllGroups(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAllCorpSubscrFeatureSets(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpAdminFS(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getActivationCode(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubscriberDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpExtContactDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrActivationCode(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateOTP(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO validateOTP(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpBanFanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO switchConvergedClient(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateActivationCodeIDMIntf(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getLITargetInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubsUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO resetCorpSubsUserPassword(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO resendCorpSubsVerificationEmail(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setTargetPermissions(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getTargetPermissions(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAuthorizedMdnList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setSubsEmergencyAttributes(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubsEmergencyAttributes(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateSubsAliasEntities(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateTempPassword(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserEmergDest(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySubscriberTGList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberTGList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteTGList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendSMS(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendTempPassword(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO addBulkGroupsToSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO createSubsATGScanList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO createOSMList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateOSMList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteOSMList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getOSMList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getOSMListDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignOSMIdToGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getOSMGroupList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpProfileByEntities(IXDMRequestDTO authRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpSubscriberMCSIds(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPoCConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMobileSyncLocSupervisors(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileListByName(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberUserProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO unassignUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileSubscriberList(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateDefaultprofile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendTrkMaterial(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAsyncOpStatus(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupsUGWConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignCommonContactList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO unAssignCommonContactList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupStats(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberStats(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceStats(IXDMRequestDTO sublistReqDto) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyBulkGroupProperties(IXDMRequestDTO groupInfoDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createCorpAccount(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpAccount(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteCorpAccount(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateAccountDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateAccountsList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteHierarchy(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO allocateSubs(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO unAllocateSubs(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createSubscriber(IXDMRequestDTO requestDTO) {

        String methodName = "createSubscriber(KnMessage)";
        knLogger.info(methodName, "ENTRY: create Subscriber with input - ", requestDTO);
        KnPersisterTxn persisterTxn = null;
        KnXDMSubsProvInfoDTO subsProvInputDTO = null;
        KnXDMCreateRespDTO responseDTO = new KnXDMCreateRespDTO();

        try {
            // verify if the input DTO is KnXDMSubsProvInfoDTO
            // inputDTO = (IXDMRequestDTO) message.getPayLoad();
            //KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_DG_MDN_CREATED_REQ);
            if (requestDTO instanceof KnXDMSubsProvInfoDTO) {
                subsProvInputDTO = (KnXDMSubsProvInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO for create Subscriber - ", subsProvInputDTO);
            } else {
                knLogger.error(methodName, "received and Invalid DTO for create subscriber op - ", requestDTO);
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_CREATION_FAILURE);
                // commonMediator.sendFailureResponse(responseDTO, message,
                // null);
                return responseDTO;
            }
            if (null != subsProvInputDTO.getSubsFS2() && KnGeneralUtil.getFeatureBitValue(subsProvInputDTO.getSubsFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.MCPTT_VIA_GW.value())) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_MCS_CLIENTS_CREATED_REQ);
            }

            //Validating the  Corporate Hirarchy

            if (subsProvInputDTO.getExtCorpId() != null && !KnGeneralProfileUtil.validateExtCorpCCAndHierarchy(subsProvInputDTO.getExtCorpId(),
                    subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the corp : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid Corp hierarchy passed");
            }

            //Validating the  MDN Hirarchy
            if (!KnGeneralProfileUtil.validateMDNCCAndHierarchy(subsProvInputDTO.getMdn(), subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the mdn : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid MDN hierarchy passed");
            }

            // populate the Subscriber Prov library DTO
            subsProvInputDTO.setAutoPair(subsProvInputDTO.getPairingInd());
            // to deprecate existing autopair logic because of new logic
            subsProvInputDTO.setPairingInd(Boolean.FALSE);
            if (null == subsProvInputDTO.getNetworkName() || subsProvInputDTO.getNetworkName().isEmpty()) {
                subsProvInputDTO.setNetworkName(subsProvInputDTO.getMdn());
            }

            // Ensure that the operation type is not null or empty, then validate the subscriber's authorization status.
            if (subsProvInputDTO.getOperationType() != null && !subsProvInputDTO.getOperationType().isEmpty()) {
                knLogger.debug(methodName, "OperationType present", subsProvInputDTO.getOperationType());
                commonMediator.validateSubAuthStatus(Collections.singletonList(subsProvInputDTO.getMdn()), persisterTxn);
            }

            KnIPSubsProvInfoDTO subsProvInfoDTO = provMediator.populateSubsProvInfoDTO(subsProvInputDTO);

            Integer subsClientType = subsProvInfoDTO.getSubsClientType();
            // checking if any the client type = null then add the default client type
            if (subsClientType == null) {
                subsClientType = KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value();
                subsProvInfoDTO.setSubsClientType(subsClientType);
            }
            knLogger.debug(methodName, "Subscriber Client type- ", subsClientType);
            //retriving from db/cache client type config check is enable or disable
            //LMR client type changes
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_DG_MDN_CREATED_REQ);
            }
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {

                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                    knLogger.error(methodName, "Client type is disabled");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                }
            }
            // calling the Prov Library
            //KnOPCreateSubsInfoDTO respDTO1 = provClientIntf.createSubscriber(subsProvInfoDTO, persisterTxn);

            //*****Calling processCreateSub method to create subscriber******
            // Opening the transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the Transaction");
            persisterTxn.open();
            subsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
            subsProvInfoDTO.setOperationType(KnProvOperationTypes.CREATE_SUBSCRIBER);
            KnProvCreateSubscProcessor knProvCreateSubscProcessor = new KnProvCreateSubscProcessor();
            KnOPCreateSubsInfoDTO respDTO = knProvCreateSubscProcessor.processCreateSub(subsProvInfoDTO, persisterTxn);

            knLogger.debug(methodName, "Successfully Create Subscriber Profile with resp DTO ANJ - ", respDTO);
            // verify if the Corporate Subscription type is enabled
            // if enabled check the pairing ind is enabled to perform the
            // internal contact pairing
            int corporateSubscriptionType = -1;
            corporateSubscriptionType = subsProvInputDTO.getCorporateSubscriptionType();
            KnXDMCorpRespDTO corpResp = null;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {

                // calling corp library to perform Corp Auto pairing
                Boolean corpAutoParing = respDTO.getCorpAutoPairing();
                Boolean isOldCorp = respDTO.getIsOldCorp();

                if (corpAutoParing != null && isOldCorp != null) {
                    if (isOldCorp) {
                        knLogger.debug(methodName, "Corp exist. call enable/disble for corp Auto pairing");
                        if (corpAutoParing) {
                            KnXDMCorpSubscInfoRequestDTO contactRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
                            contactRequestDTO.setCorpId(String.valueOf(respDTO.getCorpId()));
                            contactRequestDTO.setSubscriberMdn(subsProvInputDTO.getMdn());
                            contactRequestDTO.setName(subsProvInfoDTO.getNetworkName());
                            contactRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                            corpResp = corpMediator.addToPairingList(contactRequestDTO, persisterTxn);
                        } else {
                            // disable auto pairing
                            KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                            corpInfoRequestDTO.setEnableAutoPair(Boolean.FALSE);
                            corpInfoRequestDTO.setCorpId(String.valueOf(respDTO.getCorpId()));
                            corpInfoRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                            corpResp = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);
                        }

                    } else { //corporate not exist
                        if (corpAutoParing) {
                            knLogger.debug(methodName, "Corp does exist hence created new. call enable auto pairing");
                            KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                            corpInfoRequestDTO.setEnableAutoPair(Boolean.TRUE);
                            corpInfoRequestDTO.setCorpId(String.valueOf(respDTO.getCorpId()));
                            corpInfoRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                            corpResp = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);
                        }
                    }

                    knLogger.debug(methodName, "Corp Autopairing response - ", corpResp);
                    if (corpResp.getResponseStatus() != KnMediatorConstants.SUCCESS) {
                        throw new KnXDMServerException(corpResp.getResponseCode(), corpResp.getResponseMessage());
                    }
                }

            }
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                int corpId = respDTO.getCorpId();

                boolean isRequiredCorpNotified = commonInfoUtil.isRequiredCorpNotified(respDTO.getSubsFS2(), corpId, persisterTxn);
                if (isRequiredCorpNotified) {

                    KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, true, persisterTxn);
                    knLogger.debug(methodName, "Corporate Profile - ", corpProfile);

                    //Update the UGWINTEROP
                    commonInfoUtil.updateLmrInterOpInDB(corpId, com.kodiak.common.resources.KnConstants.ENABLED, persisterTxn);
                    //Notify to MCS CORP
                    KnCorporateExdmsNotifyDto corporateExdmsNotifyDto = commonInfoUtil.formCorpNotifyPayload(CREATE_CORP.value(), corpProfile, com.kodiak.common.resources.KnConstants.ENABLED, MICROSERVICES_NOTIFY_EVENT_TYPE.CORPORATE_EVENT.value());
                    knLogger.info(methodName, "Publishing micro service notify for corp- ", corporateExdmsNotifyDto);
                    commonMediator.startNotifyMicroServicesJob(List.of(corporateExdmsNotifyDto));

                }
            }

            //Get the xcap mobile sync flag
            boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);
            if (xcapMobileSync) {
                //  KnMqServiceConfig rmqInfoDto = commonMediator.retrieveServerConfDetails(persisterTxn);
                KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                knSubscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                knSubscrEXDMSNotifyDto.setCorpid(respDTO.getCorpId());
                knSubscrEXDMSNotifyDto.setMdn(subsProvInputDTO.getMdn());
                knSubscrEXDMSNotifyDto.setClientType(subsClientType);
                knSubscrEXDMSNotifyDto.setLastProfileUpdateTime(respDTO.getLastUpdateprofileTime());
                knSubscrEXDMSNotifyDto.setId(CREATE_SUBSCR.value() + KnConstants.LINE_SAPERATOR + subsProvInputDTO.getMdn());
                knSubscrEXDMSNotifyDto.setType(CREATE_SUBSCR.value());
                knSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                knSubscrEXDMSNotifyDto.setSubsFS2(respDTO.getSubsFS2());
                knSubscrEXDMSNotifyDto.setMcId(respDTO.getMcId());
                knSubscrEXDMSNotifyDto.setMcdataId(respDTO.getMcDataId());
                knSubscrEXDMSNotifyDto.setMcpttId(respDTO.getMcPttId());
                knSubscrEXDMSNotifyDto.setMcvideoId(respDTO.getMcVideoId());
                knSubscrEXDMSNotifyDto.setNetworkName(respDTO.getNetworkName());
                knSubscrEXDMSNotifyDto.setDeviceId(subsProvInputDTO.getMdn());
                knSubscrEXDMSNotifyDto.setAuthStatus(respDTO.getServiceAuthStatus());
                List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<KnSubscrEXDMSNotifyDto>();
                notifyDtoList.add(knSubscrEXDMSNotifyDto);
                knLogger.info(methodName, "Publishing micro service notify for user- ");
                commonMediator.startNotifyMicroServicesJob(notifyDtoList);
            }
            KnPayloadIP payLaod = new KnPayloadIP();
            BitSet taskBitSet = new BitSet(10);
            KnPendingTxnInfoDTO pendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            payLaod.setCorpId(String.valueOf(respDTO.getCorpId()));
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isCorpCreateIDMProileRequired.get(), true);
            pendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
            pendingTxnInfoDTO.setOpsId(KnGeneralUtil.ASYNC_OPS_ID.CREATE_SUBSCRIBER.get());
            knLogger.debug(" Create Subscriber Async  Data insertion call :");
            knLogger.debug(" Response :", respDTO.getXdmsHome());

            insertDataIntoAsynkFwkTable(subsProvInputDTO.getMdn(), respDTO.getCorpId(), pendingTxnInfoDTO, payLaod, respDTO.getXdmsHome(), persisterTxn);

            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();

            // updating the pegs

            Collection<Integer> successPegs = new ArrayList<Integer>();
            successPegs.add(KnOMConstants.XDM_NUM_SUBSCR_CREATED);
            Collection<Integer> listOfPegs = respDTO.getSuccessPegs();
            if (listOfPegs != null && !listOfPegs.isEmpty()) {
                for (int pegId : listOfPegs) {
                    successPegs.add(pegId);
                }
            }
            KnStatisticsManagerImpl.getInstance().increment(successPegs);

            responseDTO = (KnXDMCreateRespDTO) commonMediator.getSuccessResponse(responseDTO);

            responseDTO.setCorpId(respDTO.getCorpId());
            responseDTO.setCorpName(respDTO.getCorpName());
            responseDTO.setNetworkName(respDTO.getNetworkName());

            if (corpResp != null && corpResp.getEnabledDispatchMemList() != null && !corpResp.getEnabledDispatchMemList().isEmpty()) {
                initiateDispatchGrpJob(corpResp.getEnabledDispatchMemList(), null, null,
                        null, respDTO.getCorpId());
            }
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DATAGROUPMDN.value() && responseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.SUCCESS.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_DG_MDN_CREATED_REQ_SUCC);
            }
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_CREATION_FAILURE);
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to create Subscriber ", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_CREATION_FAILURE);
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_CREATION_FAILURE);
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getFailureResponse(responseDTO, e);
        }
        knLogger.debug(methodName, "responseDTO type ", responseDTO.getClass().getName());
        knLogger.info(methodName, "EXIT: create Subscriber - ", responseDTO);
        return responseDTO;

    }

    @Override
    public IXDMResponseDTO updateSubscriber(KnMessage message) {
        String methodName = "updateSubscriber(KnMessage) in V2";
        KnPersisterTxn persisterTxn = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        KnXDMSubsProvInfoDTO subsProvInputDTO = null;
        Object payLoad = message.getPayLoad();
        IXDMRequestDTO requestDTO = null;

        KnCorpUpdateSubscProcessor corpUpdateSubscProcessor = new KnCorpUpdateSubscProcessor(); // New Processor class for CORP Update
        BitSet taskBitSet = new BitSet(10);

        try {
            // validating if the received input DTO is KnXDMSubsProvInfoDTO
            if (payLoad instanceof IXDMRequestDTO) {
                requestDTO = (IXDMRequestDTO) payLoad;
            }
            knLogger.info(methodName, "Entry Received from the input DTO - ", requestDTO);
            if (requestDTO instanceof KnXDMSubsProvInfoDTO) {
                subsProvInputDTO = (KnXDMSubsProvInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO for update Subscriber - ", subsProvInputDTO);
            } else {
                knLogger.error(methodName, "receive an Invalid DTO for update Subscriber op - ", requestDTO);
                responseDTO = new KnXDMRespDTO();
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
                return responseDTO;
            }
            //Validating the  CORP Hirarchy
            if (subsProvInputDTO.getExtCorpId() != null && !KnGeneralProfileUtil.validateExtCorpCCAndHierarchy(subsProvInputDTO.getExtCorpId(),
                    subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the corp : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid Corp hierarchy passed");
            }
            //Validating the  MDN Hirarchy
            if (!KnGeneralProfileUtil.validateMDNCCAndHierarchy(subsProvInputDTO.getMdn(), subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the mdn : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid MDN hierarchy passed");
            }

            // Ensure that the operation type is not null or empty, then validate the subscriber's authorization status.
            if (subsProvInputDTO.getOperationType() != null && !subsProvInputDTO.getOperationType().isEmpty()) {
                commonMediator.validateSubAuthStatus(Collections.singletonList(subsProvInputDTO.getMdn()), persisterTxn);
            }

            KnOPUpdateSubsInfoDTO provRespDTO = null;
            // retrieving the subscriber profile to verify if there is a change in the Subscription Type
            knLogger.debug(methodName, "retrieving the subscriber profile for mdn ");
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(subsProvInputDTO.getMdn());
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberInfoDTO, null); // UPD-SUBS - passing txn as null;
            // Basic subscr. details might not work here;

            Integer subsClientType = subsProfileInfoDTO.getSubsClientType();
            if (subsClientType == DISPATCH_CLIENT) {
                knLogger.debug(methodName, "dispatch onboarding email set ");
                subsProfileInfoDTO.setOnBoardingEmailReqd(1);
            }

            //retriving from db/cache client type config check is enable or disable
            //LMR client type changes
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {

                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, null);  // UPD-SUBS - passing txn as null; updated the DAO method.
                if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                    knLogger.error(methodName, "Client type is disabled");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                }
            }

            // populate the Subscriber Prov library DTO
            subsProvInputDTO.setAutoPair(subsProvInputDTO.getPairingInd());
            // to deprecate existing autopair logic because of new logic
            subsProvInputDTO.setPairingInd(Boolean.FALSE);
            if ((null == subsProvInputDTO.getNetworkName() || subsProvInputDTO.getNetworkName().isEmpty())
                    && (null == subsProfileInfoDTO.getNetworkName() || subsProfileInfoDTO.getNetworkName().isEmpty())) {
                subsProvInputDTO.setNetworkName(subsProvInputDTO.getMdn());
            }

            // opening the transaction, moved from top to bottom.
            persisterTxn = KnPersisterTxn.getPersisterTxn(); // UPD-SUBS - Moved after getSubscriberDetails
            knLogger.debug(methodName, "opening the transaction");
            persisterTxn.open();

            KnIPSubsProvInfoDTO subsProvInfoDTO = provMediator.populateSubsProvInfoDTO(subsProvInputDTO);
            // invoking the library for update subscriber
            provRespDTO = provClientIntf.updateSubscriber(subsProvInfoDTO, persisterTxn);  // UPD-SUBS - Keep all in SYNC
            knLogger.debug(methodName, "Response received from Server - ", provRespDTO);

            KnPayloadIP knPayloadIP = new KnPayloadIP();
            Map<String, Object> knPayloadCarrier = knPayloadIP.getKnPayloadCarrier();
            knPayloadIP.setKnPayloadCarrier(knPayloadCarrier);
            knPayloadIP.setMdn(subscriberInfoDTO.getMdn());

            if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == provRespDTO.getResponseStatus()) {
                if (provRespDTO.isUpgradePkg()) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_SUCCESS);
                    if (provRespDTO.isAutoAssignSkip()) {
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_AUTO_ASSIGN_SKIP);
                    } else {
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_AUTO_ASSIGN_SUCCESS);
                    }
                }
            }
            if (KnConstants.RESPONSE_STATUS.SUCCESS.value() != provRespDTO.getResponseStatus()) {
                knLogger.error(methodName, "Failed to Update subscriber Info : ", provRespDTO.getResponseStatus());
                rollback(persisterTxn);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
                return commonMediator.getFailureResponse(responseDTO, new KnXDMServerException(String.valueOf(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE), "Failed to Update subscriber Info"));
            } else if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == provRespDTO.getResponseStatus()
                    && subsProfileInfoDTO.getOnBoardingEmailReqd() == ON_BOARDING_MAIL.REQUIRED.value()) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.createOidcDeviceSharingProfile.get(), true);
            } else if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == provRespDTO.getResponseStatus() && subsProfileInfoDTO.getSubsClientType() == DISPATCH_CLIENT) {
                knPayloadIP.setOldUserId(subsProfileInfoDTO.getUserId());
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.dispatchClient.get(), true);
            }

            // Move the entire if else to ASYNC ###################### Start here
            /*if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == provRespDTO.getResponseStatus()
                    && subsProfileInfoDTO.getOnBoardingEmailReqd() == com.kodiak.common.resources.KnConstants.ON_BOARDING_MAIL.REQUIRED.value()) {
                KnOPSubsProfileInfoDTO subsProfileInfo = provClientIntf.getSubscriberDetails(subscriberInfoDTO, persisterTxn);  // UPD-SUBS - Move to ASYNC
                KnXDMSubsAliasDetailsRespDTO oidcResponseDTO = null;
                boolean isOIDCApplicable = generalCacheUtil.isOIDCApplicable(subsProfileInfo.getCorpId(), WEBDISPATCHER);
                String appId = subsProfileInfo.getDispatchType() == DISPATCH_TYPE_WEB ? com.kodiak.common.resources.KnConstants.APP_ID.DISPATCHER.value() : com.kodiak.common.resources.KnConstants.APP_ID.HANDSET_STANDARD.value();

                if(subsProfileInfoDTO.getMcpttCompliance() == MCPTT_COMPLIANCE_ENABLED){
                    oidcResponseDTO = commonMediator.createOidcDeviceSharingProfileWithMCSIds(subsProfileInfo, APP_ID.USERMCSCLIENTS.value(), persisterTxn);  // UPD-SUBS -Move to ASYNC
                }else if (subsProfileInfo.getClientPVmajorVer() == PROTOCOL_VERSION_0 || subsProfileInfo.getClientPVmajorVer() > PROTOCOL_VERSION_13) {
                    oidcResponseDTO = commonMediator.createOidcDeviceSharingProfileWithMCSIds(subsProfileInfo, appId, persisterTxn);  // UPD-SUBS -Move to ASYNC
                } else {
                    if ((isOIDCApplicable && appId.equals(APP_ID.DISPATCHER.value())) || (appId.equals(APP_ID.HANDSET_STANDARD.value()))) {
                        oidcResponseDTO = commonMediator.createOidcDeviceSharingProfileWithMCSIds(subsProfileInfo, appId, persisterTxn);  // UPD-SUBS -Move to ASYNC
                    }
                }
                int failureStatus = oidcResponseDTO != null ? oidcResponseDTO.getStatus() : KnConstants.RESPONSE_STATUS.FAILURE.value();
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == failureStatus) {
                    knLogger.error(methodName, "Error for updating profile in IDM - ", failureStatus);
                    responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR);
                    responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                    throw new KnXDMServerException(responseDTO.getResponseCode(), "Unable to Create IDM Profile");
                }
            } else if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == provRespDTO.getResponseStatus() && subsProfileInfoDTO.getSubsClientType() == DISPATCH_CLIENT) {
                knLogger.debug(methodName, "inside new If ");
                KnOPSubsProfileInfoDTO subsProfileInfo = provClientIntf.getSubscriberDetails(subscriberInfoDTO, persisterTxn);  // UPD-SUBS -Move to ASYNC
                //subsProfileInfo.setOnBoardingEmailReqd(ON_BOARDING_MAIL.NOT_REQUIRED.value());
                String appId =  APP_ID.DISPATCHER.value() ;
                KnXDMSubsAliasDetailsRespDTO oidcResponseDTO=null;
                String oldDbuserId=null;
                if(null!=subsProfileInfoDTO.getUserId()) {
                    oldDbuserId = subsProfileInfoDTO.getUserId();
                    subsProfileInfo.setOldUserId(oldDbuserId);
                }

                if (null!=subsProvInputDTO.getUserId()&& !subsProvInputDTO.getUserId().isEmpty() && null!= subsProfileInfo.getUserId()&&!subsProfileInfo.getUserId().isEmpty()&&!subsProfileInfo.getUserId().equals(oldDbuserId)) {  //null!=subsProfileInfoDTO.getUserId()&&
                    knLogger.debug(methodName, "inside to createOidcDeviceSharingProfileWithMCSIds ");
                    oidcResponseDTO = commonMediator.createOidcDeviceSharingProfileWithMCSIds(subsProfileInfo, appId, persisterTxn);  // UPD-SUBS -Move to ASYNC
                    int failureStatus = oidcResponseDTO != null ? oidcResponseDTO.getStatus() : KnConstants.RESPONSE_STATUS.FAILURE.value();
                    if (KnConstants.RESPONSE_STATUS.FAILURE.value() == failureStatus) {
                        knLogger.error(methodName, "Error for updating profile in IDM - ", failureStatus);
                        responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR);
                        responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                        throw new KnXDMServerException(responseDTO.getResponseCode(), "Unable to Create IDM Profile");
                    }
                }
            }*/
            // Move the entire if else to ASYNC ###################### End here

            // if client type changed set auth status to provisioned
            knLogger.debug(methodName, "Subs clientType :", subsProfileInfoDTO.getSubsClientType(), " subsProvInputDTO.getSubscriberClientType() :",
                    subsProvInputDTO.getSubscriberClientType());
            int oldSubsClientType = subsProfileInfoDTO.getSubsClientType();
            int newSubsClientType = 0;
            if (subsProvInputDTO.getSubscriberClientType() != null)
                newSubsClientType = subsProvInputDTO.getSubscriberClientType();

            // verify that if the client Type has been changed form HANDSET to PTTRADIOHANDSETCLIENT and vice versa
            boolean isPTTRadioClientTypeChange = false;
            if (oldSubsClientType != newSubsClientType) {
                String str = "" + KnMediatorConstants.SUBS_CLIENT_TYPE.HANDSET.value() + "|"
                        + KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() + "," + com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.CROSSCARRIER.value()
                        + "|" + KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() + ","
                        + com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value() + "|" + KnMediatorConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
                        + "";
                String[] regEx1 = str.split(",");
                for (int i = 0; i < regEx1.length; i++) {
                    if (String.valueOf(oldSubsClientType).matches(regEx1[i])
                            && String.valueOf(newSubsClientType).matches(regEx1[i])) {
                        isPTTRadioClientTypeChange = true;
                        knLogger.info(methodName, "Client type change skipped");
                    }
                }
            }

            // Subscriber Client Type update - here - Check done.
            if (newSubsClientType != 0 && oldSubsClientType != newSubsClientType && !isPTTRadioClientTypeChange) {
                knLogger.debug(methodName, "client Type changed. setting service auth status to Provisioning");
                KnXDMSubsStatusInfoDTO subsStatusInfoDTO = new KnXDMSubsStatusInfoDTO();
                subsStatusInfoDTO.setMdn(subsProvInputDTO.getMdn());
                subsStatusInfoDTO.setClientType(requestDTO.getClientType());
                subsStatusInfoDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value());
                KnConstants.MESSAGE_TYPE action = KnConstants.MESSAGE_TYPE.CLIENT_TYPE_CHANGE;
                IXDMResponseDTO respDTO = changeServiceAuthStatus(subsStatusInfoDTO, persisterTxn, action);  // UPD-SUBS - Keep in Sync
                if (respDTO.getResponseStatus() != KnConstants.STATUS_SUCCESS) {
                    throw new KnProvBOException(respDTO.getResponseCode(), respDTO.getResponseMessage());

                }

                knLogger.debug(methodName, "response received from Server - ", provRespDTO);
            }

            // populating the corporate Library DTO
            KnXDMCorpSubscInfoRequestDTO corpSubsRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
            corpSubsRequestDTO.setSubscriberMdn(subsProvInputDTO.getMdn());
            if (subsProvInputDTO.getPublicSubscriptionType() == -1) {
                corpSubsRequestDTO.setNewPublicSubscriptionType(subsProfileInfoDTO.getPublicSubscriptionType());
            } else {
                corpSubsRequestDTO.setNewPublicSubscriptionType(subsProvInputDTO.getPublicSubscriptionType());
            }

            if (subsProvInputDTO.getCorporateSubscriptionType() == -1) {
                corpSubsRequestDTO.setNewCorpSubscriptionType(subsProfileInfoDTO.getCorporateSubscriptionType());
            } else {
                corpSubsRequestDTO.setNewCorpSubscriptionType(subsProvInputDTO.getCorporateSubscriptionType());
            }
            corpSubsRequestDTO.setPublicSubscriptionType(subsProfileInfoDTO.getPublicSubscriptionType());
            corpSubsRequestDTO.setCorpSubscriptionType(subsProfileInfoDTO.getCorporateSubscriptionType());
            corpSubsRequestDTO.setClientType(subsProfileInfoDTO.getSubsClientType());
            corpSubsRequestDTO.setNewClientType(subsProfileInfoDTO.getSubsClientType());
            corpSubsRequestDTO.setCorpId(String.valueOf(subsProfileInfoDTO.getCorpId()));
            corpSubsRequestDTO.setNewCorpId(provRespDTO.getCorpId());
            corpSubsRequestDTO.setName(subsProvInputDTO.getNetworkName());
            corpSubsRequestDTO.setAutoPairingFlag(subsProvInputDTO.getPairingInd());
            corpSubsRequestDTO.setOldName(subsProfileInfoDTO.getNetworkName());
            corpSubsRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
            corpSubsRequestDTO.setActiveFs2(subsProfileInfoDTO.getActiveFS2());
            LinkedList<KnLIEventDTO> eventDTOList = new LinkedList<KnLIEventDTO>();

            KnCorpResponseDTO corpResp = null; //corpMediator.updateSubscriber(corpSubsRequestDTO, persisterTxn);  // UPD-SUBS-Controller - TODO New call
            // Instead of calling the controller, create a new method and move the sync calls there.
            int oldCorpId = subsProfileInfoDTO.getCorpId();
            int newCorpId = provRespDTO.getCorpId();

            if (oldCorpId != newCorpId) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.updateCorpId.get(), true);
            }
            corpResp = corpUpdateSubscProcessor.processUpdateSubscriber(corpSubsRequestDTO, knPayloadIP, taskBitSet, persisterTxn);
            // New call above: instead of corpMediator.updateSubscriber(corpSubsRequestDTO, persisterTxn);
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            KnPendingTxnInfoDTO pendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            if (corpResp.isDeleteSubscribersCorpData()) {
                populateKnPayLoadIP(knPayloadIP, taskBitSet, pendingTxnInfoDTO, subsProvInputDTO.getMdn(), xdmPttServerId, persisterTxn);
            }
            //suppressing notification due to name change.
            if (!corpResp.isDeleteSubscribersCorpData() && provRespDTO.isSubsNameChanged()) {
                pendingTxnInfoDTO.setNotifyFlag(ONLY_ETAG_UPDATE);
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.subscriberNameChange.get(), true);
            }
            knPayloadIP.setCorpId(String.valueOf(corpResp.getMdnCorpId()));

            KnXDMCorpRespDTO corpRespD = populateXdmResponse(corpResp);
            int status = corpRespD.getResponseStatus();
            knLogger.debug(methodName, " corp Library response - ", corpResp);
            if (status != 0) {
                throw new KnXDMServerException(corpRespD.getResponseCode(), corpRespD.getResponseMessage());
            }
            knLogger.debug(methodName, "Update is successful from Corporate Library");

            // Need to remove auto pairing from the new call flow. ***** ####

            // calling corp library to perform Corp AUTO pairing
            // UPD-SUBS - Hold on this operation in the current enhancement.
            // Boolean corpAutoParing = provRespDTO.getCorpAutoPairing();
            // Boolean isOldCorp = provRespDTO.getIsOldCorp();
            //KnXDMCorpRespDTO corporateRespDto = null;
            /*if (corpAutoParing != null && isOldCorp != null) {
                if (isOldCorp) {
                    knLogger.debug(methodName, "Corp exist. call enable/disble for corp Auto pairing");
                    if (corpAutoParing) {
                        KnXDMCorpSubscInfoRequestDTO contactRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
                        contactRequestDTO.setCorpId(String.valueOf(provRespDTO.getCorpId()));
                        contactRequestDTO.setSubscriberMdn(subsProvInputDTO.getMdn());
                        contactRequestDTO.setName(subsProvInfoDTO.getNetworkName());
                        contactRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                        corporateRespDto = corpMediator.addToPairingList(contactRequestDTO, persisterTxn);  // UPD-SUBS - Keep in Sync
                    } else {
                        // disable auto pairing
                        KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                        corpInfoRequestDTO.setEnableAutoPair(Boolean.FALSE);
                        corpInfoRequestDTO.setCorpId(String.valueOf(provRespDTO.getCorpId()));
                        corpInfoRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                        corporateRespDto = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);  // UPD-SUBS - Keep in Sync
                    }

                } else { //corporate not exist
                    if (corpAutoParing) {
                        knLogger.debug(methodName, "Corp does exist hence created new. call enable auto pairing");
                        KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                        corpInfoRequestDTO.setEnableAutoPair(Boolean.TRUE);
                        corpInfoRequestDTO.setCorpId(String.valueOf(provRespDTO.getCorpId()));
                        corpInfoRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                        corporateRespDto = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);  // UPD-SUBS - Keep in Sync
                    }
                }

                knLogger.debug(methodName, "Corp Autopairing response - ", corpResp);
                if (corporateRespDto.getResponseStatus() != KnMediatorConstants.SUCCESS) {
                    throw new KnXDMServerException(corporateRespDto.getResponseCode(), corporateRespDto.getResponseMessage());
                }
            }*/ // Need to remove auto pairing from the new call flow.

            // }
            // int oldCorpSubscriptionType =
            // subsProfileInfoDTO.getCorporateSubscriptionType();
            int oldPublicSubscriptionType = subsProfileInfoDTO.getPublicSubscriptionType();
            // int newCorpSubscriptionType =
            // subsProvInputDTO.getCorporateSubscriptionType();
            int newPublicSubscriptionType = subsProvInputDTO.getPublicSubscriptionType();

            // check if public is updated to NONE

            if ((oldPublicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC
                    .value() && newPublicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.NONE.value())
                    || (newSubsClientType != 0 && oldSubsClientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()
                    && newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value())) {

                knLogger.debug(methodName, "invoke the public library for deletion of public data");
                String ownerMdn = subsProvInputDTO.getMdn();
                // Get all contacts and ListService URI s of group with LI Event
                // DTO
                eventDTOList = commonMediator.getEventDTOList(ownerMdn, KnProvOperationTypes.UPDATE_SUBSCRIBER, persisterTxn);  // UPD-SUBS - Keep in SYNC
                pubDocMediator.deleteAllContactsAndGroups(subsProvInputDTO.getMdn(), persisterTxn);  // UPD-SUBS - Keep in SYNC
                knLogger.debug(methodName, "successfully delete all public data");
            }

            // Invoke the cleanup of OLD Corporate profile.
            if (taskBitSet.get(KnGeneralUtil.ASYNC_TASK_ID.updateCorpId.get())) {
                KnOPProvDTO corpProfileRespDTO = provMediator.deleteCorpProfile(oldCorpId, persisterTxn);  // UPD-SUBS - Keep to SYNC
                knLogger.debug(methodName, "Delete old corp profile response - ", corpProfileRespDTO);
            }

            boolean OldGrpBroadcastBit = KnGeneralUtil.getFeatureBitValue(subsProfileInfoDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.BROADCASTGROUPSERVICE.value());
            boolean newGrpBroadcastBit = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.BROADCASTGROUPSERVICE.value());
            KnCorpResponseDTO corpRespDto = null;

            if (!newGrpBroadcastBit && OldGrpBroadcastBit) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.updateGrpBCAndSubsChangeLogMap.get());
                /*corpRespDto = corpMediator.updateGrpBroadcasters(subscriberInfoDTO.getMdn(), persisterTxn);
                knLogger.debug(methodName, "disabled group broadcast members corp Library response - ", corpRespDto);
                if (corpRespDto.getStatus() != 0) {
                    throw new KnXDMServerException(corpRespDto.getStatusCode(), corpRespDto.getMessage());
                }*/
            }

            knLogger.debug(methodName, subsProfileInfoDTO.getActiveFS2(), provRespDTO.getActiveFS2(), subscriberInfoDTO.getCorpId());
            knLogger.debug(methodName, subscriberInfoDTO.getMdn(), provRespDTO.getActiveFS2(), subsProfileInfoDTO.getActiveFS2());
            boolean oldReGroupBit = KnGeneralUtil.getFeatureBitValue(
                    subsProfileInfoDTO.getActiveFS2(),
                    com.kodiak.common.resources.KnConstants.FEATURE_SET.MCX_GROUP_REGROUP_FLAG_BIT.value());
            boolean newReGroupBit = KnGeneralUtil.getFeatureBitValue(
                    provRespDTO.getActiveFS2(),
                    com.kodiak.common.resources.KnConstants.FEATURE_SET.MCX_GROUP_REGROUP_FLAG_BIT.value());
            if (oldReGroupBit != newReGroupBit) {
                knLogger.info(methodName, "notification for preconfigure group");
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.groupNotifyOnActiveFsChange.get(), true);

                knPayloadCarrier.put(KnPayloadIdentifier.KnPLIden.OLD_ACTIVE_FS.name(), subsProfileInfoDTO.getActiveFS2());
                knPayloadCarrier.put(KnPayloadIdentifier.KnPLIden.NEW_ACTIVE_FS.name(), provRespDTO.getActiveFS2());
            }

            // sending the notification
            String pv = String.valueOf(subsProfileInfoDTO.getClientPVmajorVer());
            knLogger.debug(methodName, "PV :", pv);

            KnOPProvDTO opProvDTO = provClientIntf.actionOnTGSSDoc(subscriberInfoDTO.getMdn(), provRespDTO.getActiveFS2(), subsProfileInfoDTO.getActiveFS2(), persisterTxn);  // UPD-SUBS - Keep in SYNC
            KnCorporateExdmsNotifyDto corporateExdmsNotifyDto = null;

            // Move this block to ASYNC - TODO - Deep look to see if its required or not.
            if (subsProfileInfoDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                int corpId = provRespDTO.getCorpId();
                boolean isRequiredCorpNotified = commonInfoUtil.isRequiredCorpNotified(provRespDTO.getSubsFS2(), corpId, persisterTxn);  // UPD-SUBS -
                if (isRequiredCorpNotified) {
                    KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, true, persisterTxn);  // UPD-SUBS -
                    knLogger.debug(methodName, "Corporate Profile - ", corpProfile);

                    //Update the UGWINTEROP
                    commonInfoUtil.updateLmrInterOpInDB(corpId, ENABLED, persisterTxn);  // UPD-SUBS -

                    //Notify to MCS
                    corporateExdmsNotifyDto = commonInfoUtil.formCorpNotifyPayload(MODIFY_CORP.value(), corpProfile, ENABLED,
                            com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CORPORATE_EVENT.value());
                    knLogger.info(methodName, "Publishing micro service notify for corp- ", corporateExdmsNotifyDto);
                }
            }


            //Get the xcap mobile sync flag
            boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);

            String mcsXcapRootUri = genInfoUtil.getMCSXCAPRootURI(subscriberInfoDTO.getMdn(), persisterTxn);
            Set<String> allMcsXcapUris = genInfoUtil.getMCSXCAPRootURIs(persisterTxn);
            pendingTxnInfoDTO.setOpsId(KnGeneralUtil.ASYNC_OPS_ID.UPDATE_SUBSCRIBER.get());

            //populating the payload for the async task
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.notifyXcapMobile.get());  // Notify ASYNC method to send xcap notifications.
            pendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
            insertDataIntoAsynkFwkTable(subsProvInputDTO.getMdn(), subsProfileInfoDTO.getCorpId(), pendingTxnInfoDTO,
                    knPayloadIP, xdmPttServerId, persisterTxn);

            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();  // UPD-SUBS - Final Save

            if (corporateExdmsNotifyDto != null) {
                knLogger.debug(methodName, " Sending notification for corp");
                commonMediator.startNotifyMicroServicesJob(List.of(corporateExdmsNotifyDto)); // keep in SYNC
            }

            //MC DEVICE NOTIFY
            if (provRespDTO.isDeleteDeviceNotify()) {
                sendDeleteDeviceNotification(provRespDTO.getDeviceIMPI()); // UPD-SUBS - Keep in SYNC
            }

            if (null != opProvDTO.getDirChgDTO()) {
                knLogger.debug(methodName, "opProvDTO", opProvDTO);
                KnOPDirChgDTO dirChgDTO = opProvDTO.getDirChgDTO();
                if (null != opProvDTO.getDirChgDTO().getDocChgDTO()) {
                    dirChgDTO = commonMediator.fetchMdnsFromDirChgDto(opProvDTO.getDirChgDTO(), xdmPttServerId, null);  // UPD-SUBS - Keep in SYNC
                }
                opProvDTO.setDirChgDTO(dirChgDTO);

                knLogger.debug(methodName, "TGSS doc changed sending notification");
                commonMediator.sendXcapNotification(opProvDTO.getDirChgDTO(), pv); // UPD-SUBS - Keep in SYNC
            }

            if (null != provRespDTO.getDirChgDTO()) {
                knLogger.debug(methodName, "provRespDTO", provRespDTO);
                KnOPDirChgDTO dirChgDTO = provRespDTO.getDirChgDTO();
                if (null != provRespDTO.getDirChgDTO().getDocChgDTO()) {
                    dirChgDTO = commonMediator.fetchMdnsFromDirChgDto(provRespDTO.getDirChgDTO(), xdmPttServerId, null);  // UPD-SUBS - Keep in SYNC
                }
                provRespDTO.setDirChgDTO(dirChgDTO);
            }

            if (oldSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()
                    && oldSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()
                    && oldSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                if ((null != provRespDTO.getDirChgDTO()) && (pv.matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX))
                        && (provRespDTO.isActiveFSChanged() || provRespDTO.isSubsNameChanged()
                        || provRespDTO.isSubsTypeChanged())) {
                    KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                    if (provRespDTO.isActiveFSChanged()) {
                        profileNotifyDTO.setActiveFeatureSetChange(subsProfileInfoDTO.getMdn());
                    }
                    if (provRespDTO.isSubsNameChanged()) {
                        profileNotifyDTO.setSubscriberNameChange(subsProfileInfoDTO.getMdn());
                    }
                    if (provRespDTO.isSubsTypeChanged()) {
                        profileNotifyDTO.setSubscriptionTypeChange(subsProfileInfoDTO.getMdn());
                    }
                    profileNotifyDTO.setMdn(subsProfileInfoDTO.getMdn());
                    profileNotifyDTO.setPocHome(provRespDTO.getDirChgDTO().getPocHome());
                    profileNotifyDTO.setPresenceHome(provRespDTO.getDirChgDTO().getPresenceHome());
                    profileNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());

                    commonMediator.sendProfileNotification(profileNotifyDTO, provRespDTO.getDirChgDTO(), pv); // Keep in SYNC - Self Notification
                } else {
                    commonMediator.sendXcapNotification(provRespDTO.getDirChgDTO(), pv); // Keep in SYNC - Self Notification

                    List<KnXcapDiffNotifyDTO> xcapDiffList = new ArrayList<>();
                    if (provRespDTO.getDirChgDTOs() != null && !provRespDTO.getDirChgDTOs().isEmpty()) {
                        for (KnOPDirChgDTO dirChgDTO : provRespDTO.getDirChgDTOs()) {
                            KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
                            Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<KnXcapDiffDocDTO>();
                            ArrayList<KnOPDocChgDTO> chgDocList = (ArrayList<KnOPDocChgDTO>) dirChgDTO.getDocChgDTO();
                            for (KnOPDocChgDTO chgDTO : chgDocList) {
                                KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                                xcapDiffDocDTO.setDocChangeType(chgDTO.getDocumentChgType());
                                xcapDiffDocDTO.setDocEtag(chgDTO.getNewEtag());
                                xcapDiffDocDTO.setDocumentSelector(chgDTO.getDocUri());
                                xcapDiffDocDTO.setVideoPermission(chgDTO.getVideoPermission());
                                xcapDocList.add(xcapDiffDocDTO);
                            }

                            xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
                            xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
                            xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                            xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                            xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                            xcapDiffNotifyDTO.setProtocolVersion(pv);
                            xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                            xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                            xcapDiffList.add(xcapDiffNotifyDTO);
                        }

                    }
                    knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffList);
                    KnNotificationParamDTO notificationParamDTO = new KnNotificationParamDTO();
                    notificationParamDTO.setCid(message.getCorrelationId());
                    boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, notificationParamDTO); // Keep in SYNC - Self Notification
                    knLogger.debug(methodName, "Notification status - ", isNotified);
                }
            }

            if (corpRespDto != null) { //hanlded above
                /*Map<String, KnOPDirChgDTO> updateSubcriberChangeLogMap = corpResp.getChangeLogMap();
                LinkedList<KnLIEventDTO> updateGrpBroadcasterLiEventList = corpRespDto.getLiEventList();
                knLogger.debug(methodName, "LI event list from the update grp broadcast call - ", updateGrpBroadcasterLiEventList);
                if (updateGrpBroadcasterLiEventList != null) {
                    eventDTOList.addAll(updateGrpBroadcasterLiEventList);
                }
                knLogger.debug(methodName, "eventDTOList after update Grpbrocast call - ", eventDTOList);
                Map<String, KnOPDirChgDTO> updateGrpBroadcastersChangeLogMap = corpRespDto.getChangeLogMap();
                Map<String, KnOPDirChgDTO> combinedChangeLogMap = new HashMap<>();
                if (updateSubcriberChangeLogMap != null) {
                    combinedChangeLogMap.putAll(updateSubcriberChangeLogMap);
                }
                if (updateGrpBroadcastersChangeLogMap != null) {
                    combinedChangeLogMap.putAll(updateGrpBroadcastersChangeLogMap);
                }
                corpResp.setChangeLogMap(combinedChangeLogMap); // Move to ASYNC - CORP -Resp.*/

                // knPayloadCarrier.put(corpRespDto.getClass().toString(), corpRespDto); // TODO - Look deeper and pass necessary params instead of DTO.

                // TODO - Perfoming action : eventDTOList.addAll(updateGrpBroadcasterLiEventList); and corpResp.setChangeLogMap(combinedChangeLogMap);
                // From the bove commented code.

            }

            /*LinkedList<KnLIEventDTO> liEventlist = corpResp.getLiEventList();
            knLogger.debug(methodName, "LI event list from the corporate module - ", liEventlist);
            if (liEventlist != null) {
                eventDTOList.addAll(liEventlist);
            }
            knLogger.debug(methodName, "eventDTOList final in end - ", eventDTOList);*/

            // prepare notification and send to notification mgr
            /*Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(corpResp);
            xcapDiffList = commonMediator.fetchXcapDiffList(xcapDiffList, xdmPttServerId, null);
            knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
            notifier.setMaxNotfnsPerJob(2);
            KnNotificationParamDTO notificationParamDTO = new KnNotificationParamDTO();
            notificationParamDTO.setCid(message.getCorrelationId());
            boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn, notificationParamDTO);  // UPD-SUBS - Move to ASYNC.
            knLogger.debug(methodName, "Notification status - ", isNotified);
            commonMediator.sendTGSModeChangeNotification(corpResp.getTgsModeChgMap()); // UPD-SUBS - Move to ASYNC.

            if(xcapMobileSync){
                knLogger.debug(methodName, "Publishing micro service notify - "); // UPD-SUBS - Move to ASYNC - TODO - take a deep Look
                commonMediator.startNotifyMicroServicesJob(corpResp.getChangeLogMap(), subsProfileInfoDTO.getCorpId());
            }*/

            // TODO - Perfoming action : commonMediator.startNotifyMicroServicesJob(corpResp.getChangeLogMap(), subsProfileInfoDTO.getCorpId());
            // From the above commented code.
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.notifyXcapMobile.get());  // Notify ASYNC method to send xcap notifications.

            // after success sending LI notification ..
            // send the dto .as of LI Notification . ...
            KnLIEventHandler.logLI(eventDTOList);
            // updating the Pegs
            Collection<Integer> successPegs = new ArrayList<Integer>();
            successPegs.add(KnOMConstants.XDM_NUM_SUBSCR_UPDATED);
            Collection<Integer> listOfPegs = provRespDTO.getSuccessPegs();
            if (listOfPegs != null && !listOfPegs.isEmpty()) {
                for (int pegId : listOfPegs) {
                    successPegs.add(pegId);
                }
            }
            KnStatisticsManagerImpl.getInstance().increment(successPegs);
            /*
             * Spawning a new thread for update of the DISPATCH_GRP_MEMBER FLAG
             * of Members of the dispatch groups that has been deleted
             */
            Collection<String> deletedMemberList = corpResp.getDisabledDispatchMemList();
            knLogger.debug(methodName, "deletedMemberList - ", KnGDPRTemplate.mdnList(deletedMemberList));
            // If deleted members list id not null and not empty start job to
            // update DISPATCH_GROUP_MEMBER field
            if (deletedMemberList != null && !deletedMemberList.isEmpty()) {
                knLogger.debug(methodName, "Calling Dispatch group member job - ");
                List<KnDispGrpMemChecker> jobList = new ArrayList<KnDispGrpMemChecker>(1);
                KnDispGrpMemChecker dispGrpMemCheckerJob = new KnDispGrpMemChecker();
                dispGrpMemCheckerJob.setMaxNotificationSize(maxNotificationSize);
                dispGrpMemCheckerJob.setUpdatedMdn(subsProvInputDTO.getMdn());
                // dispGrpMemCheckerJob.setSyncNotfyThresold(500);
                dispGrpMemCheckerJob.setCorpId(subsProfileInfoDTO.getCorpId());
                dispGrpMemCheckerJob.setDeletedMembers(corpResp.getDisabledDispatchMemList());
                jobList.add(dispGrpMemCheckerJob);
                try {
                    knLogger.debug(methodName, "Scheduling the Jobs - ", jobList);
                    scheduler.addRamJob(jobList, KnJobConstants.JOB_GROUP_NAME);
                } catch (KnJobSchedulerException e) {
                    knLogger.error(methodName, "KnJobSchedulerException occurred while ", "submitting Notification Job to Scheduler - " + e);
                    knLogger.error(methodName, e);
                }
            }

            if (xcapMobileSync) {
                sendModifySubscriberMicroserviceEventNotify(subsProvInputDTO, provRespDTO, subsProfileInfoDTO); // UPD-SUBS - Keep in SYNC. TODO - take a deep Look
                // Jusbin and Sanjiv decided to work onthe framework code to make this call async.
                // This is calling ": scheduler.addRamJob(jobList, KnJobConstants.JOB_MICRO_SERVICE_NOTIFY);" and posting the request.
            }

            if (provRespDTO.isActiveFSChanged()) {
                /*
                Commented Etag mgmt job which will be handled in the async path
                Keeping the OSM bit update below as it is not part of the async path
                 */
                KnXDMCorpMediator.updateGroupMemOsmBit(subsProvInputDTO.getMdn(), newSubsClientType, provRespDTO.getActiveFS2(), xdmPttServerId, null);

                boolean xcapCouchClientBit = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
                knLogger.debug(methodName, "xcapMobileSync:", xcapMobileSync, " xcapCouchClientBit:", xcapCouchClientBit, " PV:", subsProfileInfoDTO.getClientPVmajorVer());
                if (xcapMobileSync && (xcapCouchClientBit || subsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_18)) {
                    KnSubscrEXDMSNotifyDto subscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                    subscrEXDMSNotifyDto.setMdn(subsProvInputDTO.getMdn());
                    subscrEXDMSNotifyDto.setCorpid(provRespDTO.getCorpId());
                    subscrEXDMSNotifyDto.setActiveFS(KnGeneralUtil.convertHexStringToLong(provRespDTO.getActiveFS2()));
                    subscrEXDMSNotifyDto.setActiveFS2(provRespDTO.getActiveFS2());
                    subscrEXDMSNotifyDto.setOldActiveFS(provRespDTO.getOldActiveFS2());
                    subscrEXDMSNotifyDto.setPv(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                    subscrEXDMSNotifyDto.setId(FEATURE_BIT_CHANGE.value() + KnConstants.LINE_SAPERATOR + subsProvInputDTO.getMdn());
                    subscrEXDMSNotifyDto.setType(FEATURE_BIT_CHANGE.value());
                    subscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    subscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                    subscrEXDMSNotifyDto.setLastProfileUpdateTime(provRespDTO.getLastProfileUpdateTime());
                    List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<KnSubscrEXDMSNotifyDto>();
                    notifyDtoList.add(subscrEXDMSNotifyDto);

                    if (provRespDTO.getProfileMdnActivsFsMap() != null && !provRespDTO.getProfileMdnActivsFsMap().isEmpty()) {
                        for (Map.Entry profileMdnFsMap : provRespDTO.getProfileMdnActivsFsMap().entrySet()) {
                            KnSubscrEXDMSNotifyDto profilrMdnSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                            profilrMdnSubscrEXDMSNotifyDto.setMdn(profileMdnFsMap.getKey().toString());
                            profilrMdnSubscrEXDMSNotifyDto.setCorpid(provRespDTO.getCorpId());
                            profilrMdnSubscrEXDMSNotifyDto.setActiveFS(KnGeneralUtil.convertHexStringToLong(profileMdnFsMap.getValue().toString()));
                            profilrMdnSubscrEXDMSNotifyDto.setActiveFS2(profileMdnFsMap.getValue().toString());
                            if (null != provRespDTO.getMdnUpmFsMap().get(profileMdnFsMap.getKey().toString()).getOldActiveFS()) {
                                profilrMdnSubscrEXDMSNotifyDto.setOldActiveFS(provRespDTO.getMdnUpmFsMap().get(profileMdnFsMap.getKey().toString()).getOldActiveFS());
                            }
                            profilrMdnSubscrEXDMSNotifyDto.setPv(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                            profilrMdnSubscrEXDMSNotifyDto.setId(FEATURE_BIT_CHANGE.value() + KnConstants.LINE_SAPERATOR + profileMdnFsMap.getKey().toString());
                            profilrMdnSubscrEXDMSNotifyDto.setType(FEATURE_BIT_CHANGE.value());
                            profilrMdnSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                            profilrMdnSubscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                            profilrMdnSubscrEXDMSNotifyDto.setLastProfileUpdateTime(provRespDTO.getLastProfileUpdateTime());
                            notifyDtoList.add(profilrMdnSubscrEXDMSNotifyDto);
                        }
                    }

                    knLogger.info(methodName, "Publishing micro service notify  for User event - ");
                    commonMediator.startNotifyMicroServicesJob(notifyDtoList);
                    knLogger.debug(methodName, "micro service notify for activeFs change event - Sent");
                }
            }

            if (subsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_18) {
                knLogger.debug(methodName, "Sending MCS notification");
                commonMediator.sendMCSNotification(provRespDTO.getDirChgDTO(), subsProfileInfoDTO.getMcId(), mcsXcapRootUri); // Keep in SYnc
            }
            if (null != corpResp) {
                boolean notifyStatus = commonMediator.sendMCSGRPNotification(corpResp, allMcsXcapUris, subsProfileInfoDTO.getCorpId()); // Keep in SYnc
                knLogger.debug(methodName, "sending Group notification Status: ", notifyStatus);
            }
            if (null != provRespDTO && null != provRespDTO.getProfileMdnEtagMap()) {
                provRespDTO = commonMediator.fetchActiveProfileMdns(provRespDTO, xdmPttServerId);
                boolean profileNotifyStatus = commonMediator.prepareMcxNotifyForProfileMdns(provRespDTO.getProfileMdnEtagMap(), provRespDTO.getMcsXcapRootUriMap()); // Keep in SYnc
                knLogger.debug(methodName, "sending Profile notification Status: ", profileNotifyStatus);
            }

            if (provRespDTO.getUserProfileMdns() != null && !provRespDTO.getUserProfileMdns().isEmpty()) {
                KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
                ipUserProfileDTO.setCorpId(String.valueOf(subsProfileInfoDTO.getCorpId()));
                ipUserProfileDTO.setUserProfileMdns(provRespDTO.getUserProfileMdns());

                String deleteUpmJsonString = commonInfoUtil.ObjToJson(ipUserProfileDTO);
                Long transactionId = System.currentTimeMillis();
                knLogger.debug(methodName, "transactionId - ", transactionId);
                KnAsyncJobDTO knAsyncJobDTO = commonMediator.createJobNotifyDTO(String.valueOf(subsProfileInfoDTO.getCorpId()), transactionId.toString()
                        , null, KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value(), subsProvInputDTO.getMdn(),
                        KnConstants.UPM_RESOURCE_TYPE.MDN.Value(), deleteUpmJsonString, KnConstants.UPM_JOB_STATUS.NEW.Value(), null); // Move to ASYNC - In deep calls. TODO - Working on this by @jusbin
                upmJobScheduler.addJob(knAsyncJobDTO);

                // Keep the method call as it is and the async call process will be handled in the job scheduler classes.
            }

            // sending the response to the Client
            responseDTO.setEtag(provRespDTO.getEtag());
            knLogger.info(methodName, "EXIT: update Subscriber - ", responseDTO);

            return commonMediator.getSuccessResponse(responseDTO);

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);  // UPD-SUBS - Rollback
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to Update subscriber Info", e);
            rollback(persisterTxn);
            if (!e.getErrorCode().equals(NO_CHG_IN_PROFILE)) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
            }
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            rollback(persisterTxn);  // UPD-SUBS - Rollback
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
            return commonMediator.getFailureResponse(responseDTO, e);
        }

    }

    @Override
    public IXDMResponseDTO getSubscriberDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberConfigDocument(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO activateSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO changeServiceAuthStatus(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO forceSync(IXDMRequestDTO requestDTO) {
        return null;
    }

    public boolean prepareAndSendDeleteSubscriberRequest(String mdn) {
        try {
            KnMessage message = new KnMessage();
            KnXDMSubsInfoDTO subsInfoDTO = new KnXDMSubsInfoDTO();
            subsInfoDTO.setMdn(mdn);
            subsInfoDTO.setUpmCall(true);
            message.setPayLoad(subsInfoDTO);
            IXDMResponseDTO responseDTO = deleteSubscriber(message);
            return responseDTO.getResponseStatus() == KnMediatorConstants.SUCCESS;
        } catch (Exception e) {
            knLogger.error("prepareAndSendDeleteSubscriberRequest", "Exception occurred while deleting the subscriber - ", e);
            return false;
        }
    }

    @Override
    public IXDMResponseDTO deleteSubscriber(KnMessage message) {
        String methodName = "deleteSubscriber(KnMessage)";
        knLogger.debug(methodName, "Delete Subscriber Request ");
        KnPersisterTxn persisterTxn = null;
        KnXDMSubsInfoDTO subsInfoDTO = null;
        KnXDMCreateRespDTO responseDTO = new KnXDMCreateRespDTO();
        Object payLoad = message.getPayLoad();
        IXDMRequestDTO requestDTO = null;
        KnProvDeleteSubscProcessor provDeleteSubscProcessor = new KnProvDeleteSubscProcessor();
        KnCorpDocCleanUpProcessor corpDocCleanUpProcessor = new KnCorpDocCleanUpProcessor();
        try {
            // verifying the received input DTO is of type KnXDMSubsInfoDTO
            // inputDTO = (IXDMRequestDTO) message.getPayLoad();
            //KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_DG_MDN_DELETED_REQ);
            if (payLoad instanceof IXDMRequestDTO) {
                requestDTO = (IXDMRequestDTO) payLoad;
            }
            if (requestDTO instanceof KnXDMSubsInfoDTO) {
                subsInfoDTO = (KnXDMSubsInfoDTO) requestDTO;
                knLogger.info(methodName, "Received DTO for Delete Subscriber -  ", subsInfoDTO);
            } else {
                knLogger.error(methodName, "received and Invalid DTO for Delete subscriber op - ", requestDTO);
                responseDTO = new KnXDMCreateRespDTO();
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                return responseDTO;
            }
            String mdn = subsInfoDTO.getMdn();

            // Ensure that the operation type is not null or empty, then validate the subscriber's authorization status.
            if (subsInfoDTO.getOperationType() != null && !subsInfoDTO.getOperationType().isEmpty()) {
                commonMediator.validateSubAuthStatus(Collections.singletonList(mdn), persisterTxn);
            }

            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(mdn);
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getSubscriberDetailsBasic(subscriberInfoDTO, null);

            if (subsInfoDTO.isUpmCall()) {
                subsInfoDTO.setHierarchyType(subsProfileInfoDTO.getHierarchyType());
                subsInfoDTO.setClientType(subsProfileInfoDTO.getSubsClientType());
            }

            //Validating the  MDN Hirarchy
            if (!KnGeneralProfileUtil.validateMDNCCAndHierarchy(subsInfoDTO.getMdn(), subsInfoDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the mdn : ", subsInfoDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid MDN hierarchy passed");
            }


            // retrieve the subscriber profile
            // this profile info will be useful in getting the type of
            // subscriber
            // if subscriber is public then invoke public library for deleting
            // of all grps/contacts
            // if subscriber is corp then invoke corp library for deleting of
            // owned grps/contacts
            if (subsInfoDTO.getClientType() == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_DG_MDN_DELETED_REQ);
            }

            knLogger.debug(" xdmshome ", subsProfileInfoDTO.getXDMSHome());
            //check if the mdn is a pseudo mdn then restrict the operation
            if (subsProfileInfoDTO.getPamAccId() != null && subsProfileInfoDTO.getPamAccId() != 0) {
                knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO,
                        "Mdn is present as a pseudo mdn in POCSUBSCRINFO");
            }
            /*
            KnCorpProfileDTO corpProfileDetails = commonInfoUtil//
                    .getProfileDetails(String.valueOf(subsProfileInfoDTO.getCorpId()), CORP_PROFILE, false, persisterTxn); // xx*/


            //calling corp here
            knLogger.debug(methodName, "invoking the V2 corporate library ");
            // populating the corporate Library DTO
            KnIPCorpContactDTO contactRequestDTO = new KnIPCorpContactDTO();
            contactRequestDTO.setMdn(subsInfoDTO.getMdn());
            contactRequestDTO.setHierarchyType(subsInfoDTO.getHierarchyType());
            contactRequestDTO.setMcpttCompliance(subsProfileInfoDTO.getMcpttCompliance());
            contactRequestDTO.setCorpId(subsProfileInfoDTO.getCorpId());
            contactRequestDTO.setUpmCall(subsInfoDTO.isUpmCall());
            knLogger.debug(methodName, "invoking the corporate delete subscriber " + contactRequestDTO);
            //converting the subsmgmt file here
            KnSubsProfileDTO subsProfile = convertSubsProfile(subsProfileInfoDTO);
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            KnCorpResponseDTO corpResp = corpDocCleanUpProcessor.deleteSubscleanUpCorporateProfile(contactRequestDTO, subsProfile, xdmPttServerId, persisterTxn);
            //inserting data into async table to send notifcations in async way
            KnPayloadIP knPayloadIP = new KnPayloadIP();
            KnPendingTxnInfoDTO knPendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            knPendingTxnInfoDTO.setOpsId(KnGeneralUtil.ASYNC_OPS_ID.DELETE_SUBSCRIBER.get());
            BitSet taskBitSet = new BitSet();
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.deleteSubscriberProfile.get(), true);
            if (subsProfile.getClientType() == DISPATCH.value()
                    || subsProfile.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.initiateDispJobForDeletedMdn.get(), true); //where mdn=?
            }


            int status = corpResp.getStatus();
            knLogger.debug(methodName, " corp Library response - ", corpResp);
            if (status != 0) {
                throw new KnXDMServerException(corpResp.getStatusCode(), corpResp.getMessage());
            }
            knLogger.debug(methodName, "deleted the corporate data for mdn ");

            int publicSubscriptionType = subsProfileInfoDTO.getPublicSubscriptionType();
            if (publicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value()) {
                knLogger.debug(methodName, "invoking the public library");
                pubDocMediator.deleteAllContactsAndGroups(mdn, persisterTxn); // DEL - Delete Operation D1 Keep in SYNC JOB
                knLogger.debug(methodName, "deleted public data for mdn ");
            }

            final String[] baseMdn = {mdn};
            if (subsProfileInfoDTO.getMcpttId() != null) {
                subscriberInfoDTO.setMcPttIds(Collections.singletonList(subsProfileInfoDTO.getMcpttId()));
                try {
                    KnXDMProfileIdMdnMapRespDTO baseMdnRespDto = provClientIntf.getMdnProfileIdsForMcPttIds(subscriberInfoDTO, false, persisterTxn);
                    Map<String, Map<Integer, String>> profileMdnMap = baseMdnRespDto.getProfileIdMdnMap();
                    Map<Integer, String> baseMdnMapWithProfileIndex = profileMdnMap.get(subsProfileInfoDTO.getMcpttId());
                    Optional<Map.Entry<Integer, String>> baseMdnValue = baseMdnMapWithProfileIndex.entrySet().stream()
                            .filter(map -> map.getKey() == 0).findFirst();
                    baseMdnValue.ifPresent(opMap -> baseMdn[0] = baseMdnValue.get().getValue());
                } catch (KnProvBOException pbe) {
                    knLogger.error(methodName, "Profile not available, so continue further");
                }
            }
            // todo LI event for deleted subscriber if required.

            int corpId = subsProfileInfoDTO.getCorpId();
            KnCorpProfileDTO corpProfile = new KnCorpProfileDTO();
            if (corpId != 0) {
                corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, true, persisterTxn); // DEL - Read Operation R2
            }
            int lmrInteropFlag = DISABLED;
            KnCorpConfigInfoDto corpConfigInfoDto = commonInfoUtil.selectLmrInterOpInDB(corpId, persisterTxn);
            KnCorporateExdmsNotifyDto corporateExdmsNotifyDto = null;
            // invoking the prov library for Deleting subscriber profile
            //KnOPDeleteSubsRespDTO provRespDTO = provClientIntf.deleteSubscriber(subscriberInfoDTO, persisterTxn); // Original
            // DEL - Delete Operation D3 - Keep in SYNC JOB
            KnOPDeleteSubsRespDTO provRespDTO = provDeleteSubscProcessor.processDeleteSub(subscriberInfoDTO, persisterTxn, taskBitSet, subsProfileInfoDTO); //todo new
            knLogger.debug(methodName, "Delete Subscriber operation response - ", provRespDTO);
            //setting the corp subsc type

            knPayloadIP.setCorpSubscriptionType(String.valueOf(subsProfileInfoDTO.getCorporateSubscriptionType()));

            populateKnPayLoadIP(knPayloadIP, taskBitSet, knPendingTxnInfoDTO, mdn, xdmPttServerId, persisterTxn);
            knPendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
            insertDataIntoAsynkFwkTable(subsInfoDTO.getMdn(), corpProfile.getCorpId(), knPendingTxnInfoDTO, knPayloadIP, xdmPttServerId, persisterTxn);

            // Initializing the KnSubscriberInfoDTO for base RestAPI
            KnSubscriberInfoDTO knSubscriberInfoDTO = new KnSubscriberInfoDTO();
            // Fetching the boolean value for 45th bit, If it is true then we will call the new REST API.
            boolean xcapCouchClient = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
            knLogger.debug(methodName, "xcapCouchClient ", xcapCouchClient);
            // checking if 45th bit is 0/false or 1/true. If not 0/false then we make the rest call
            if (corpId != 0) {
                int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpId, persisterTxn);
                knLogger.debug(methodName, "corpSubsCount: ", corpSubsCount);
                if (corpSubsCount == 0) {
                    if (corpConfigInfoDto != null && corpConfigInfoDto.getParamValue() != null) {
                        lmrInteropFlag = Integer.parseInt(corpConfigInfoDto.getParamValue());
                        commonInfoUtil.deleteLmrInterOpInDB(corpId, persisterTxn);
                    }
                    //Notify to MCS
                    corpProfile.setNetworkName(null);
                    corporateExdmsNotifyDto = commonInfoUtil.formCorpNotifyPayload(DELETE_CORP.value(), corpProfile, lmrInteropFlag, com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CORPORATE_EVENT.value());
                } else if (corpSubsCount == 1) {
                    responseDTO.setLastSubscriber(true);
                }
            }

            //Get the xcap mobile sync flag
            boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
            Set<String> allMcsXcapUris = genInfoUtil.getMCSXCAPRootURIs(persisterTxn);
            //Remove mdn from mcpttPermissionsConfig from CBS upm doc
            KnIPUserProfileDTO delMcpttReqDto = new KnIPUserProfileDTO();
            delMcpttReqDto.setMdn(mdn);
            delMcpttReqDto.setCorpId(String.valueOf(subsProfileInfoDTO.getCorpId()));
            delMcpttReqDto.setPocPttServerId(subsProfileInfoDTO.getPocPttServerId());

            if (persisterTxn.getTransactionStatus() == KnCorpCommonInfoUtil.TXN_STATE.STARTED.ordinal()) {
                corpClientIntf.deleteMcpttPermConfig(delMcpttReqDto, persisterTxn);
            }
            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();
            // sending the notification
            // populate the KnXcapDiffNotifyDTO
            // Notification requires the following params
            // 1. Dir Uri, 2. Dir prev etag,
            if (subsProfileInfoDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()
                    && subsProfileInfoDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()
                    && subsProfileInfoDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
                KnOPDirChgDTO dirChgDTO = provRespDTO.getDirChgDTO();
                xcapDiffNotifyDTO.setDeRegisterNotify(true);
                KnDeRegisterNotifyDTO deRegisterNotifyDTO = new KnDeRegisterNotifyDTO();
                deRegisterNotifyDTO.setMdn(provRespDTO.getMdn());
                deRegisterNotifyDTO.setPocHome(provRespDTO.getPocServerHome());
                deRegisterNotifyDTO.setPresenceHome(provRespDTO.getPresenceServerHome());
                deRegisterNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.USER_DELETE.value());
                xcapDiffNotifyDTO.setDeRegisterNotifyDTO(deRegisterNotifyDTO);
                // Collection<KnXcapDiffDocDTO> xcapDocList = new
                // ArrayList<KnXcapDiffDocDTO>();
                // xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
                // xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
                xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                xcapDiffNotifyDTO.setReason(KnConstants.REASON.USER_DELETE.value());
                xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
                xcapDiffNotifyDTO.setPocHome(provRespDTO.getPocServerHome());
                xcapDiffNotifyDTO.setPresenceHome(provRespDTO.getPresenceServerHome());
                knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffNotifyDTO);

                // Filtering Inactive mdns, commented the changes as mdn go deleted from system
                //xcapDiffNotifyDTO = commonMediator.fetchMdnsFromXcapDiffNotifyObj(xcapDiffNotifyDTO, xdmPttServerId, null);
                //knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffNotifyDTO);

                KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
                boolean notificationStatus = notifier.sendXcapDiffNotifications(xcapDiffNotifyDTO);
                if (notificationStatus) {
                    knLogger.debug(methodName, "Successfully sent the notification");
                }
            }

            knLogger.debug(methodName, "xcapMobileSync:", xcapMobileSync);
            if (xcapMobileSync) {
                KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                knSubscrEXDMSNotifyDto.setCorpid(provRespDTO.getCorpId());
                knSubscrEXDMSNotifyDto.setMdn(provRespDTO.getMdn());
                knSubscrEXDMSNotifyDto.setId(DELETE_SUBSCR.value() + KnConstants.LINE_SAPERATOR + provRespDTO.getMdn());
                knSubscrEXDMSNotifyDto.setType(DELETE_SUBSCR.value());
                knSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                knSubscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                knSubscrEXDMSNotifyDto.setClientType(subsProfileInfoDTO.getSubsClientType());
                knSubscrEXDMSNotifyDto.setLastProfileUpdateTime(subsProfileInfoDTO.getLastProfileUpdateTime());
                knSubscrEXDMSNotifyDto.setBaseMdn(baseMdn[0]);
                knSubscrEXDMSNotifyDto.setMcpttId(subsProfileInfoDTO.getMcpttId());

                knSubscrEXDMSNotifyDto.setPocPttId(subsProfileInfoDTO.getPoCHome());
                knSubscrEXDMSNotifyDto.setSubsFS2(subsProfileInfoDTO.getSubsFS2());
                knSubscrEXDMSNotifyDto.setMcId(subsProfileInfoDTO.getMcId());
                knSubscrEXDMSNotifyDto.setMcdataId(subsProfileInfoDTO.getMcDataId());
                knSubscrEXDMSNotifyDto.setMcvideoId(subsProfileInfoDTO.getMcVideoId());
                knSubscrEXDMSNotifyDto.setNetworkName(subsProfileInfoDTO.getNetworkName());
                knSubscrEXDMSNotifyDto.setDeviceId(provRespDTO.getMdn());
                List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
                notifyDtoList.add(knSubscrEXDMSNotifyDto);
                knLogger.info(methodName, "Publishing micro service notify for user- ");
                commonMediator.startNotifyMicroServicesJob(notifyDtoList);
            }
            // updating the pegs
            if (!subsInfoDTO.isUpmCall()) {
                Collection<Integer> successPegs = new ArrayList<Integer>();
                successPegs.add(KnOMConstants.XDM_NUM_SUBSCR_DELETED);
                Collection<Integer> listOfPegs = provRespDTO.getSuccessPegs();
                if (listOfPegs != null && !listOfPegs.isEmpty()) {
                    for (int pegId : listOfPegs) {
                        successPegs.add(pegId);
                    }
                }
                KnStatisticsManagerImpl.getInstance().increment(successPegs);
            }
            // sending the response to the Client
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getSuccessResponse(responseDTO);
            responseDTO.setCorpId(provRespDTO.getCorpId());
            responseDTO.setCorpName(corpProfile.getNetworkName());
            /*
             * Spawning a new thread for update of the DISPATCH_GRP_MEMBER FLAG
             * of Members of the dispatch groups that has been deleted
             */
            // knLogger.debug(methodName, "deletedMemberList - ", KnGDPRTemplate.mdnList(deletedMemberList));
            // If deleted members list id not null and not empty start job to
            // update DISPATCH_GROUP_MEMBER field

            if (!provRespDTO.getUserProfileMdns().isEmpty()) {
                knLogger.debug(" profile Mdns are ", provRespDTO.getUserProfileMdns());
                KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
                ipUserProfileDTO.setCorpId(String.valueOf(subsProfileInfoDTO.getCorpId()));
                ipUserProfileDTO.setUserProfileMdns(provRespDTO.getUserProfileMdns());

                String deleteUpmJsonString = commonInfoUtil.ObjToJson(ipUserProfileDTO);
                Long transactionId = System.currentTimeMillis();
                knLogger.debug(methodName, "transactionId - ", transactionId);
                KnAsyncJobDTO knAsyncJobDTO = commonMediator.createJobNotifyDTO(String.valueOf(subsProfileInfoDTO.getCorpId()), transactionId.toString()
                        , null, KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value(), null,
                        KnConstants.UPM_RESOURCE_TYPE.MDN.Value(), deleteUpmJsonString, KnConstants.UPM_JOB_STATUS.NEW.Value(), null);
                upmJobScheduler.addJob(knAsyncJobDTO);
            }
            //MC DEVICE NOTIFY
            if (provRespDTO.isDeleteDeviceNotify()) {
                sendDeleteDeviceNotification(TEL_URI_TEMPLATE + mdn);
            }

            if (corporateExdmsNotifyDto != null) {
                knLogger.info(methodName, "Publishing micro service notify for Delete corp- ", corporateExdmsNotifyDto);
                commonMediator.startNotifyMicroServicesJob(List.of(corporateExdmsNotifyDto));
            }
            //place async jobs for clean up of group from user profile if any.

            if (subsInfoDTO.getClientType() == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DATAGROUPMDN.value() && responseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.SUCCESS.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_DG_MDN_DELETED_REQ_SUCC);
            }
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETION_FAILURE);
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to delete Subscriber Operation ", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETION_FAILURE);
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETION_FAILURE);
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getFailureResponse(responseDTO, e);
        } catch (Throwable e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETION_FAILURE);
            responseDTO = (KnXDMCreateRespDTO) commonMediator.getFailureResponse(responseDTO, new Exception(e));
        }
        knLogger.info(methodName, "EXIT: delete Subscriber operation - ", responseDTO);
        return responseDTO;
    }

    private KnSubsProfileDTO convertSubsProfile(KnOPSubsProfileInfoDTO subsProfileInfoDTO) {
        KnSubsProfileDTO subsProfile = new KnSubsProfileDTO();
        subsProfile.setMdn(subsProfileInfoDTO.getMdn());
        subsProfile.setCorpId(subsProfileInfoDTO.getCorpId());
        subsProfile.setUserProfileId(subsProfileInfoDTO.getUserProfileId());
        subsProfile.setClientType(subsProfileInfoDTO.getSubsClientType());
        subsProfile.setPocHome(subsProfileInfoDTO.getPoCHome());
        subsProfile.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
        subsProfile.setXdmsHome(subsProfileInfoDTO.getXDMSHome());
        subsProfile.setServiceAuthStatus(subsProfileInfoDTO.getServiceAuthStatus());
        subsProfile.setPublicSubscriptionType(subsProfileInfoDTO.getPublicSubscriptionType());
        subsProfile.setCorpSubscriptionType(subsProfileInfoDTO.getCorporateSubscriptionType());
        subsProfile.setLastProfileUpdateTime(subsProfileInfoDTO.getLastProfileUpdateTime());
        subsProfile.setContactListId(subsProfileInfoDTO.getCorpContactListId());
        subsProfile.setUserAgent(subsProfileInfoDTO.getUserAgent());
        subsProfile.setClientPassowrd(subsProfileInfoDTO.getClientPassword());
        subsProfile.setSubscriberEmail(subsProfileInfoDTO.getEmailAddress());
        subsProfile.setClientMajorVersion(subsProfileInfoDTO.getClientPVmajorVer());
        subsProfile.setClientMinorVersion(subsProfileInfoDTO.getClientPVminorVer());
        subsProfile.setAccountId(subsProfileInfoDTO.getAccountId());
        subsProfile.setPamAccId(subsProfileInfoDTO.getPamAccId());
        subsProfile.setDispatchType(subsProfileInfoDTO.getDispatchType());
        subsProfile.setDerivedKey(subsProfileInfoDTO.getDerivedKey());
        subsProfile.setUserId(subsProfileInfoDTO.getUserId());
        subsProfile.setNetworkName(subsProfileInfoDTO.getNetworkName());
        subsProfile.setUfmi(subsProfileInfoDTO.getUfmi());
        subsProfile.setServiceAuthStatusOP(subsProfileInfoDTO.getServiceStatusOp());
        subsProfile.setServiceAuthStatusAU(subsProfileInfoDTO.getServiceStatusAuthUser());
        subsProfile.setLicenseType(subsProfileInfoDTO.getLicenseType());
        subsProfile.setAliasMdn(subsProfileInfoDTO.getAliasMdn());
        subsProfile.setMcpttCompliance(subsProfileInfoDTO.getMcpttCompliance());
        subsProfile.setSubscriberFS2(subsProfileInfoDTO.getSubsFS2());
        subsProfile.setClientFS2(subsProfileInfoDTO.getClientFS2());
        subsProfile.setActiveFS2(subsProfileInfoDTO.getActiveFS2());
        subsProfile.setOpsFS2(subsProfileInfoDTO.getOpsFS2());
        subsProfile.setCorpAdminFS2(subsProfileInfoDTO.getCorpAdminFS2());
        subsProfile.setXdmsFS2(subsProfileInfoDTO.getXdmsFS2());
        subsProfile.setMcId(subsProfileInfoDTO.getMcId());
        subsProfile.setMcpttId(subsProfileInfoDTO.getMcpttId());
        subsProfile.setMcVideoId(subsProfileInfoDTO.getMcVideoId());
        subsProfile.setMcDataId(subsProfileInfoDTO.getMcDataId());
        subsProfile.setUserProfileIndex(subsProfileInfoDTO.getUserProfileIndex());
        subsProfile.setUserProfileFS2(subsProfileInfoDTO.getUserProfileFS2());
        subsProfile.setUserId(subsProfileInfoDTO.getUserId());
        subsProfile.setUserProfileId(subsProfileInfoDTO.getUserProfileId());
        subsProfile.setCameraType(subsProfileInfoDTO.getCameraType());
        return subsProfile;
    }

    @Override
    public IXDMResponseDTO updateSubscriberName(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCampedGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDefaultSubscriberProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateClientFS1(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateUserAgent(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteScanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateScanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberScanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public void addContacts(KnMessage message) {

    }

    @Override
    public void deleteContacts(KnMessage message) {

    }

    @Override
    public void modifyContacts(KnMessage message) {

    }

    @Override
    public void getContactList(KnMessage message) {

    }

    @Override
    public void getAllContactLists(KnMessage message) {

    }

    @Override
    public void createGroup(KnMessage message) {

    }

    @Override
    public void modifyGroupName(KnMessage message) {

    }

    @Override
    public void modifyGroupMember(KnMessage message) {

    }

    @Override
    public void deleteGroupMember(KnMessage message) {

    }

    @Override
    public void addGroupMember(KnMessage message) {

    }

    @Override
    public void deleteGroup(KnMessage message) {

    }

    @Override
    public void getGroupDetails(KnMessage message) {

    }

    @Override
    public void getGroupDocDetails(KnMessage message) {

    }

    @Override
    public IXDMResponseDTO getPubGroupDetails(KnMessage message) {
        return null;
    }

    @Override
    public void getDirectory(KnMessage message) {

    }

    @Override
    public void getRlsDoc(KnMessage message) {

    }

    @Override
    public void getGroupList(KnMessage message) {

    }

    @Override
    public IXDMResponseDTO getPubGroupList(KnMessage message) {
        return null;
    }

    @Override
    public void isAlive(KnMessage message) {

    }

    @Override
    public IXDMResponseDTO unKnownOperation(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO changeMdn(KnMessage message) {
        String methodName = "changeMdn(KnMessage)";
        knLogger.debug(methodName, "Change MDN request ");
        KnPersisterTxn persisterTxn = null;
        KnXDMChangeMDNInfoDTO changeMdnInputDTO = null;
        // UCSPROVCONFIG-9013 — return KnXDMCreateRespDTO (subclass of KnXDMRespDTO)
        // so downstream subsmgmt can recover corpId via instanceof check and trigger
        // CAT publish. Backward-compatible: existing instanceof KnXDMRespDTO checks
        // still match this subclass.
        KnXDMCreateRespDTO responseDTO = new KnXDMCreateRespDTO();
        Object payLoad = message.getPayLoad();
        IXDMRequestDTO requestDTO = null;
        long startTime = System.currentTimeMillis();
        try {
            // verify if the input DTO is of type KnXDMChangeMDNInfoDTO
            // IXDMRequestDTO inputDTO = (IXDMRequestDTO) message.getPayLoad();
            if (payLoad instanceof IXDMRequestDTO) {
                requestDTO = (IXDMRequestDTO) payLoad;
            }
            if (requestDTO instanceof KnXDMChangeMDNInfoDTO) {
                changeMdnInputDTO = (KnXDMChangeMDNInfoDTO) requestDTO;
                knLogger.info(methodName, "received DTO for change MDN - ", changeMdnInputDTO);
            } else {
                knLogger.error(methodName, "received an Invalid DTO for changed MDN op - ", requestDTO);
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                // commonMediator.sendFailureResponse(responseDTO, message,
                // null);
                return responseDTO;
            }

            //Validating the old MDN Hirarchy
            if (changeMdnInputDTO.getOldMDN() != null && !KnGeneralProfileUtil.validateMDNCCAndHierarchy(changeMdnInputDTO.getOldMDN(), changeMdnInputDTO.getHierarchyType())) {
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid MDN hierarchy passed");
            }

            //Validating the old MDN Hirarchy
            if (changeMdnInputDTO.getNewMdn() != null && !KnGeneralProfileUtil.validateMDNCCAndHierarchy(changeMdnInputDTO.getNewMdn(), changeMdnInputDTO.getHierarchyType())) {
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid MDN hierarchy passed");
            }

            // opening the transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "opening the transaction ");
            persisterTxn.open();

            // Ensure that the operation type is not null or empty, then validate the subscriber's authorization status.
            if (changeMdnInputDTO.getOperationType() != null && !changeMdnInputDTO.getOperationType().isEmpty()) {
                commonMediator.validateSubAuthStatus(Collections.singletonList(changeMdnInputDTO.getOldMDN()), persisterTxn);
            }

            // call Prov/Public/Corp libraries for change MDN API.
            // calling prov library
            KnIPChangeMDNInfoDTO changeMDNInfoDTO = new KnIPChangeMDNInfoDTO();
            changeMDNInfoDTO.setOldMDN(changeMdnInputDTO.getOldMDN());
            changeMDNInfoDTO.setNewMDN(changeMdnInputDTO.getNewMdn());
            changeMDNInfoDTO.setHierarchyType(changeMdnInputDTO.getHierarchyType());
            KnOPSubsProfileInfoDTO provRespDTO = provClientIntf.changeMDN(changeMDNInfoDTO, true, persisterTxn);
            knLogger.debug(methodName, "change MDN response from prov lib - ", provRespDTO);
            // UCSPROVCONFIG-9013 — propagate corpId so subsmgmt can scope CAT publish
            responseDTO.setCorpId(provRespDTO.getCorpId());


            ///async setting is done
            Integer subsClientType = provRespDTO.getSubsClientType();
            //retriving from db/cache client type config check is enable or disable
            //LMR client type changes
            knLogger.debug(methodName, "Subscriber client type for config check :", subsClientType);
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {

                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                    knLogger.error(methodName, "Client type is disabled");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                }
            }

            pubDocMediator.changeMdn(changeMDNInfoDTO, persisterTxn);
            knLogger.debug(methodName, "change MDN Success from Pub lib.");

            // int corpSubscriptionType =
            // provRespDTO.getCorporateSubscriptionType();
            // if (corpSubscriptionType ==
            // KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
            // populating the corporate Library DTO
            // invoking the corporate library
            //Calling the corp library //jusbin
            //KnProvChangeMdnProcessor provChangeMdnProcessor = new KnProvChangeMdnProcessor();
            KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
            contactDTO.setMdn(changeMdnInputDTO.getOldMDN());
            contactDTO.setNewMdn(changeMdnInputDTO.getNewMdn());
            contactDTO.setHierarchyType(changeMdnInputDTO.getHierarchyType());
            contactDTO.setMcpttCompliance(provRespDTO.getMcpttCompliance());
            KnProvChangeMdnProcessor provChangeMdnProcessor = new KnProvChangeMdnProcessor();
            KnCorpResponseDTO corpResp = provChangeMdnProcessor.changeMdn(contactDTO, persisterTxn); //here
            knLogger.debug(" Change mdn processing is done ");
            //async starts
            ///async setting
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(provRespDTO.getXDMSHome());
            KnPayloadIP knPayloadIP = provRespDTO.getKnPayloadIP();
            BitSet taskBitSet = new BitSet(10);
            if (xdmDAO.IsMdnPresentAsGroupMember(changeMdnInputDTO.getNewMdn(), persisterTxn)) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.notifyAllGroupMembers.get(), true);
                KnAsyncGroupNotifyDTO knAsyncGroupNotifyDTO = new KnAsyncGroupNotifyDTO();
                knAsyncGroupNotifyDTO.setAddedMembers(Collections.singletonList(changeMdnInputDTO.getNewMdn()));
                knAsyncGroupNotifyDTO.setRemovedMembers(Collections.singletonList(changeMdnInputDTO.getOldMDN()));
                knPayloadIP.setKnAsyncGroupNotifyDTO(knAsyncGroupNotifyDTO);
            }
            if (xdmDAO.IsMdnPresentAsContact(changeMdnInputDTO.getNewMdn(), persisterTxn)) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isContactNotifyRequired.get(), true);
                /*KnContactParamInfoDTO knNewContactDTO = new KnContactParamInfoDTO(changeMDNInfoDTO.getNewMDN(),null,0, String.valueOf(KnConstants.DOC_CHANGE_TYPE.ADD.value()));
                KnContactParamInfoDTO knOldContactDTO = new KnContactParamInfoDTO(changeMDNInfoDTO.getOldMDN(),null,0, String.valueOf(KnConstants.DOC_CHANGE_TYPE.REMOVE.value()));
                */
                KncontactNotifyInfoDTO kncontactNotifyInfoDTO = new KncontactNotifyInfoDTO();
                //kncontactNotifyInfoDTO.setListOfContacts(Arrays.asList(knNewContactDTO, knOldContactDTO));
                kncontactNotifyInfoDTO.setAddedContatcs(Collections.singletonList(changeMDNInfoDTO.getNewMDN()));
                kncontactNotifyInfoDTO.setRemovedContacts(Collections.singletonList(changeMDNInfoDTO.getOldMDN()));
                knPayloadIP.setKncontactNotifyInfoDTO(kncontactNotifyInfoDTO);
            }
            if (xdmDAO.IsMdnPresentAsDestination(changeMdnInputDTO.getNewMdn(), persisterTxn)) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isEmergencyConfigNotifyRequired.get(), true);
                KnEmergencyConfigNotifyInfoDTO knEmergencyConfigNotifyInfoDTO = new KnEmergencyConfigNotifyInfoDTO();
                emergcyDestInfo knEmergencyDestInfo = new emergcyDestInfo();
                knEmergencyDestInfo.setDestination(changeMdnInputDTO.getNewMdn());
                knEmergencyConfigNotifyInfoDTO.setemergencyDestinationListInfo(Collections.singletonList(knEmergencyDestInfo));
                knPayloadIP.setKnEmergencyConfigNotifyInfoDTO(knEmergencyConfigNotifyInfoDTO);
            }
            if (xdmDAO.IsMdnPresentAsTarget(changeMdnInputDTO.getNewMdn(), persisterTxn)) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isAuthorizationNotifyRequired.get(), true);
                KnAuthorizationNotifyInfoDTO knAuthorizationNotifyInfoDTO = new KnAuthorizationNotifyInfoDTO();
                knAuthorizationNotifyInfoDTO.setAddedTargets(Collections.singletonList(changeMdnInputDTO.getNewMdn()));
                knPayloadIP.setKnAuthorizationNotifyInfoDTO(knAuthorizationNotifyInfoDTO);
            }
            //taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isAuthorizationNotifyRequired.get(),xdmDAO.IsMdnPresentAsTarget(mdn, persisterTxn));
            //Emergency notification will be set in the below method
            KnPendingTxnInfoDTO pendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            pendingTxnInfoDTO.setOpsId(KnGeneralUtil.ASYNC_OPS_ID.CHANGE_MDN.get());
            knLogger.info(methodName, " taskBitSet ", taskBitSet);
            pendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
            insertDataIntoAsynkFwkTable(changeMdnInputDTO.getNewMdn(), provRespDTO.getCorpId(), pendingTxnInfoDTO, knPayloadIP, provRespDTO.getXDMSHome(), persisterTxn);
            //////
            /*int status = corpResp.getStatus();
            knLogger.debug(methodName, " corp Library response - ", corpResp);
            if (status != 0) {
                throw new KnXDMServerException(corpResp.getStatusCode(), corpResp.getMessage());
            }*/
            knLogger.debug(methodName, "Change MDN is successful from Corporate Library");
            //prepare notification and send to notification mgr
            Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(corpResp);
            knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
            KnNotificationParamDTO notificationParamDTO = new KnNotificationParamDTO();
            notificationParamDTO.setCid(message.getCorrelationId());
            boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn, notificationParamDTO);
            knLogger.debug(methodName, "Notification status - ", isNotified);

            knLogger.debug(methodName, "Sending LI notifications - ");
            KnLIEventHandler.logLI(corpResp.getLiEventList());
            knLogger.debug(methodName, "Li Notification Send status - ");
            // }

            provClientIntf.removeSubsInfo(changeMDNInfoDTO, persisterTxn);
            knLogger.debug(methodName,
                    "remove old subs info success from prov lib");


            // creating the Object for calling the cauch base RestAPI
            KnSubscriberInfoDTO knSubscriberInfoDTO = new KnSubscriberInfoDTO();

            // Fetching the boolean value for 45th bit, If it is true then we will call the new REST API.
            boolean xcapCouchClient = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), FEATURE_SET.XCAPCOUCHCLIENT.value());
            knLogger.debug(methodName, "xcapCouchClient - ", xcapCouchClient);
            // Checking if 45th bit is 0/false or 1/true. If not 0/false then we make the rest call
            if (xcapCouchClient) {
                // Populating the data KnSubscriberInfoDTO knSubscriberInfoDTO.
                knSubscriberInfoDTO.setMdn(changeMDNInfoDTO.getOldMDN());
                knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(knSubscriberInfoDTO.getMdn()));

                // Fetching the bucket URLS.
                Map<Integer, String> bucketUrlList = genInfoUtil.retrievePTXbucketUrls(persisterTxn);
                knLogger.debug(methodName, "bucketUrlList : ", bucketUrlList);

                KnFailedCBTxnLogDTO failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                genInfoUtil.deleteCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                for (Map.Entry<Integer, String> entry : bucketUrlList.entrySet()) {
                    List<String> bucketUrl = new ArrayList<>();
                    Integer curClusterId = entry.getKey();
                    knLogger.debug(methodName, "Updating/deleting profile in clusterId - ", curClusterId);
                    bucketUrl.add(entry.getValue());
                    //REST call for profile creation starts here.
                    KnSyncResponseDTO knSyncResponseDTO = KnManageSyncUserProfileUtil.getInstance().deleteSyncDocument(knSubscriberInfoDTO, bucketUrl);
                    knLogger.debug(methodName, "KnSyncResponseDTO status : ", knSyncResponseDTO.getStatus());
                    if (knSyncResponseDTO.getStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                        failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                        failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                        failedCBTxnLogDTO.setImpactedClusterId(entry.getKey());
                        failedCBTxnLogDTO.setTxnType(KnConstants.CB_PROFILE_TXN_TYPE.PROFILE_DELETE.value());
                        genInfoUtil.insertCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                    }
                }
            }


            //Get the xcap mobile sync flag
            boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);
            Set<String> allMcsXcapUris = genInfoUtil.getMCSXCAPRootURIs(persisterTxn);
            // saving the transaction
            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();
            // sending the notification
            // commonMediator.sendXcapNotification(provRespDTO.getDirChgtDTO());

            KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
            KnOPDirChgDTO dirChgDTO = provRespDTO.getDirChgtDTO();

            KnDeRegisterNotifyDTO deRegisterNotifyDTO = new KnDeRegisterNotifyDTO();
            deRegisterNotifyDTO.setMdn(provRespDTO.getMdn());
            deRegisterNotifyDTO.setNewMdn(changeMDNInfoDTO.getNewMDN());
            deRegisterNotifyDTO.setPocHome(provRespDTO.getPoCHome());
            deRegisterNotifyDTO.setPresenceHome(provRespDTO.getPresenceHome());
            deRegisterNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.MDN_CHANGE.value());
            xcapDiffNotifyDTO.setDeRegisterNotifyDTO(deRegisterNotifyDTO);
            xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
            xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
            xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
            xcapDiffNotifyDTO.setReason(KnConstants.REASON.MDN_CHANGE.value());
            xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
            xcapDiffNotifyDTO.setPocHome(provRespDTO.getPoCHome());
            xcapDiffNotifyDTO.setPresenceHome(provRespDTO.getPresenceHome());
            xcapDiffNotifyDTO.setChangeMdnNotify(true);
            xcapDiffNotifyDTO.setNtfyOnAnyMDN(dirChgDTO.getNtfyOnAnyMDN());
            knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffNotifyDTO);

            KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
            boolean notificationStatus = notifier.sendXcapDiffNotifications(xcapDiffNotifyDTO);

            for (KnOPDirChgDTO profileMdnDirChgDTO : provRespDTO.getDirChgDTOs()) {
                KnXcapDiffNotifyDTO profilrMdnXcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
                profilrMdnXcapDiffNotifyDTO.setDirPrevEtag(profileMdnDirChgDTO.getDirPrevEtag());
                profilrMdnXcapDiffNotifyDTO.setDirURI(profileMdnDirChgDTO.getDirUri());
                profilrMdnXcapDiffNotifyDTO.setXcapRootUri(profileMdnDirChgDTO.getXcapRootURI());
                profilrMdnXcapDiffNotifyDTO.setReason(KnConstants.REASON.MDN_CHANGE.value());
                profilrMdnXcapDiffNotifyDTO.setProtocolVersion(profileMdnDirChgDTO.getProtoVersion());
                profilrMdnXcapDiffNotifyDTO.setPocHome(provRespDTO.getPoCHome());
                profilrMdnXcapDiffNotifyDTO.setPresenceHome(provRespDTO.getPresenceHome());
                profilrMdnXcapDiffNotifyDTO.setNtfyOnAnyMDN(profileMdnDirChgDTO.getNtfyOnAnyMDN());
                knLogger.debug(methodName, "Notification DTO generated for Profile MDN- ", profilrMdnXcapDiffNotifyDTO);
                notificationStatus = notifier.sendXcapDiffNotifications(profilrMdnXcapDiffNotifyDTO);

            }

            if (notificationStatus) {
                knLogger.debug(methodName, "Successfully sent the notification");
            }

            if (xcapMobileSync) {
                knLogger.debug(methodName, "Publishing micro service notify for Group event - ");
                commonMediator.startNotifyMicroServicesJob(corpResp.getChangeLogMap(), provRespDTO.getCorpId());
            }
            //Notification to microservices
            if (xcapMobileSync) {

                KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                knSubscrEXDMSNotifyDto.setMdn(provRespDTO.getMdn());
                knSubscrEXDMSNotifyDto.setCorpid(provRespDTO.getCorpId());
                knSubscrEXDMSNotifyDto.setNewMdn(changeMdnInputDTO.getNewMdn());
                knSubscrEXDMSNotifyDto.setId(CHANGE_MDN.value() + KnConstants.LINE_SAPERATOR + provRespDTO.getMdn());
                knSubscrEXDMSNotifyDto.setType(CHANGE_MDN.value());
                knSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                knSubscrEXDMSNotifyDto.setNotifyEventType(MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                knSubscrEXDMSNotifyDto.setLastProfileUpdateTime(provRespDTO.getLastActivationTime());
                knSubscrEXDMSNotifyDto.setClientType(subsClientType);
                knSubscrEXDMSNotifyDto.setSubsFS2(provRespDTO.getSubsFS2());
                knSubscrEXDMSNotifyDto.setMcId(provRespDTO.getMcId());
                knSubscrEXDMSNotifyDto.setMcdataId(provRespDTO.getMcDataId());
                knSubscrEXDMSNotifyDto.setMcpttId(provRespDTO.getMcpttId());
                knSubscrEXDMSNotifyDto.setMcvideoId(provRespDTO.getMcVideoId());
                knSubscrEXDMSNotifyDto.setNetworkName(provRespDTO.getNetworkName());
                knSubscrEXDMSNotifyDto.setDeviceId(provRespDTO.getMdn());

                List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
                notifyDtoList.add(knSubscrEXDMSNotifyDto);

                knLogger.info(methodName, "Publishing micro service notify  for User event - ");
                commonMediator.startNotifyMicroServicesJob(notifyDtoList);
            }

            if (corpResp != null) {
                boolean notifyStatus = commonMediator.sendMCSGRPNotification(corpResp, allMcsXcapUris, provRespDTO.getCorpId());
                knLogger.debug(methodName, "sending Group notification Status: ", notifyStatus);
            }

            if (provRespDTO != null) {
                boolean profileNotifyStatus = commonMediator.prepareMcxNotifyForProfileMdns(provRespDTO.getProfileMdnEtagMap(), provRespDTO.getMcsXcapRootUriMap());
                knLogger.debug(methodName, "sending Profile notification Status: ", profileNotifyStatus);
            }
            // updating the performance pegs
            KnStatisticsManagerImpl.getInstance().increment(
                    KnOMConstants.XDM_NUM_SUBSCR_MDN_CHANGE);
            knLogger.info(methodName, "EXIT: Change MDN operation - ",
                    responseDTO);

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            totalProcessingTime += totalTime;
            Long avgTime = totalProcessingTime / ++totalRequestCount;
            knLogger.info(methodName, "Total Time taken for changeMdn - ", totalTime, " Avg Time - ", avgTime);
            // sending the response to the Client
            return commonMediator.getSuccessResponse(responseDTO);

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(
                    KnOMConstants.XDM_NUM_SUBSCR_MDN_CHANGE_FAILURE);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to Migrate Subscriber ", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(
                    KnOMConstants.XDM_NUM_SUBSCR_MDN_CHANGE_FAILURE);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(
                    KnOMConstants.XDM_NUM_SUBSCR_MDN_CHANGE_FAILURE);
            return commonMediator.getFailureResponse(responseDTO, e);
        }
    }

    @Override
    public IXDMResponseDTO createLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getLicensePackProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO changePAMServiceAuthStatus(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPAMAccountMDNsDetails(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO downgradeLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteExternalSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO changeBillingNumber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO upgradeLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateAutoPairing(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createTPUser(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateTPUser(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteTPUser(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateTPActivationCode(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSysConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public void modifyDynamicContacts(KnMessage message) {

    }

    @Override
    public IXDMResponseDTO getAuthorizationList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateAuthorizationList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getEmergencyConfigDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupUsageListDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public void createNonSharedGroup(KnMessage message) {

    }

    @Override
    public void deleteDynamicContacts(KnMessage message) {

    }

    @Override
    public void getDynamicContacts(KnMessage message) {

    }

    @Override
    public void getDynamicNonSharedGrpDetails(KnMessage message) {

    }

    @Override
    public void getDynamicNonSharedGrpList(KnMessage msgObj) {

    }

    @Override
    public void deleteDynamicNonSharedGrp(KnMessage message) {

    }

    @Override
    public void modifyNonSharedGroup(KnMessage msgObj) {

    }

    @Override
    public IXDMResponseDTO getPubContactList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deviceActivation(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO radioDeviceActivation(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO userLogin(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO upgradeOrDowngradLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubsEmergencyDetails(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberCorpGroupList(IXDMRequestDTO subscriberCorpInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO getTGSSList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateTGSSList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteTGSSList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO searchCorpAddressBook(IXDMRequestDTO subscriberInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateMCSIds(IXDMRequestDTO requestDTO) {
        String methodName = "updateMCSIds(KnMessage)";
        knLogger.info(methodName, "ENTRY: update MCSIds - ", requestDTO);
        KnPersisterTxn persisterTxn = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        KnXDMSubsProvInfoDTO subsProvInputDTO = null;
        KnOPUpdateSubsInfoDTO provRespDTO = null;
        KnPayloadIP payLaodIP = new KnPayloadIP();
        BitSet taskBitSet = new BitSet();
        try {
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_ASSIGN_MCXIDS_REQ);
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // validating if the received input DTO is KnXDMSubsProvInfoDTO
            knLogger.debug(methodName, "Received from the input DTO - ", requestDTO);
            if (requestDTO instanceof KnXDMSubsProvInfoDTO) {
                subsProvInputDTO = (KnXDMSubsProvInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO for update MCSIds - ", subsProvInputDTO);
            } else {
                knLogger.error(methodName, "receive an Invalid DTO for update MCSIds op - ", requestDTO);
                responseDTO = new KnXDMRespDTO();
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                return responseDTO;
            }
            knLogger.debug(methodName, "retrieving the subscriber profile for mdn ");
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(subsProvInputDTO.getMdn());
            subscriberInfoDTO.setAliasMdn(subsProvInputDTO.getAliasMdn());
            subscriberInfoDTO.setUserId(subsProvInputDTO.getUserId());
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberInfoDTO, persisterTxn);
            knLogger.debug(methodName, "subscriber profile ", subsProfileInfoDTO);

            //check if the mdn is a pseudo mdn then restrict the operation
            if (subsProfileInfoDTO.getPamAccId() != null && subsProfileInfoDTO.getPamAccId() != 0) {
                knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO,
                        "Mdn is present as a pseudo mdn in POCSUBSCRINFO");
            }

            //Validating the  CORP Hirarchy
            if (subsProvInputDTO.getExtCorpId() != null && !KnGeneralProfileUtil.validateExtCorpCCAndHierarchy(subsProfileInfoDTO.getAccountId(),
                    subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the corp : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid Corp hierarchy passed");
            }

            //Validating the  MDN Hirarchy
            if (!KnGeneralProfileUtil.validateMDNCCAndHierarchy(subsProfileInfoDTO.getMdn(), subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the mdn : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid MDN hierarchy passed");
            }


            Integer subsClientType = subsProfileInfoDTO.getSubsClientType();
            //retriving from db/cache client type config check is enable or disable
            //LMR client type changes
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {

                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                    knLogger.error(methodName, "Client type is disabled");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                }
            }


            KnIPSubsProvInfoDTO subsProvInfoDTO = new KnIPSubsProvInfoDTO();
            subsProvInfoDTO.setMdn(subsProfileInfoDTO.getMdn());
            subsProvInfoDTO.setMcId(subsProvInputDTO.getMcId());
            subsProvInfoDTO.setMcpttId(subsProvInputDTO.getMcpttId());
            subsProvInfoDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
            subsProvInfoDTO.setMcDataId(subsProvInputDTO.getMcVideoId());
            // opening the transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "opening the transaction");
            persisterTxn.open();

            //provRespDTO = provClientIntf.updateSubscriber(subsProvInfoDTO, persisterTxn);
            //*****Calling processUpdateSubscriber method to update subscriber******
            subsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
            subsProvInfoDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER);
            KnProvUpdateSubscProcessor knProvUpdateSubscProcessor = new KnProvUpdateSubscProcessor();
            provRespDTO = knProvUpdateSubscProcessor.processUpdateSubscriber(subsProvInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Response received from Server - ", provRespDTO);
            if (KnConstants.RESPONSE_STATUS.SUCCESS.value() != provRespDTO.getResponseStatus()) {
                knLogger.error(methodName, "Failed to Update subscriber Info : ", provRespDTO.getResponseStatus());
                rollback(persisterTxn);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
                return commonMediator.getFailureResponse(responseDTO, new KnXDMServerException(String.valueOf(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE), "Failed to Update subscriber Info"));
            } else if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == provRespDTO.getResponseStatus()) {
                // Sending the logic of 'createOidcDeviceSharingProfileWithMCSIds' to Async.
                knLogger.debug(methodName, "Invoking createOidcDeviceSharingProfileWithMCSIds as an ASYNC Job");
                // Remaining logic is set using the subsProfileInfo which is fetch using getSubscriberDetails with the mdn
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isCreateOidcDeviceSharingProfileWithMCSIds.get(), true);
            }
            //Get the xcap mobile sync flag
            boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);

            // sending the notification
            String pv = String.valueOf(subsProfileInfoDTO.getClientPVmajorVer());
            knLogger.debug(methodName, "PV :", pv);
            KnOPProvDTO opProvDTO = provClientIntf.actionOnTGSSDoc(subscriberInfoDTO.getMdn(), provRespDTO.getActiveFS2(), subsProfileInfoDTO.getActiveFS2(), persisterTxn);
            KnPendingTxnInfoDTO knPendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            knLogger.debug(" inserting into async table ");
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isCorpLastProfileUpdateRequired.get(), true);
            knPendingTxnInfoDTO.setOpsId(KnGeneralUtil.ASYNC_OPS_ID.UPDATE_MC_ID.get());
            payLaodIP.setCorpId(String.valueOf(provRespDTO.getCorpId()));
            knPendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
            insertDataIntoAsynkFwkTable(subsProfileInfoDTO.getMdn(), provRespDTO.getCorpId(), knPendingTxnInfoDTO, payLaodIP, xdmPttServerId, persisterTxn);
            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();
            if (opProvDTO.getDirChgDTO() != null) {
                knLogger.debug(methodName, "TGSS doc changed sending notification");
                commonMediator.sendXcapNotification(opProvDTO.getDirChgDTO(), pv);
            }

            knLogger.debug(methodName, "Subs clientType :", subsProfileInfoDTO.getSubsClientType(), " subsProvInputDTO.getSubscriberClientType() :",
                    subsProvInputDTO.getSubscriberClientType());
            int oldSubsClientType = subsProfileInfoDTO.getSubsClientType();
            if (oldSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() && oldSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                if ((pv.matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX))
                        && (provRespDTO.isActiveFSChanged() || provRespDTO.isSubsNameChanged() || provRespDTO.isSubsTypeChanged())) {
                    KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                    if (provRespDTO.isActiveFSChanged()) {
                        profileNotifyDTO.setActiveFeatureSetChange(subsProfileInfoDTO.getMdn());
                    }
                    if (provRespDTO.isSubsNameChanged()) {
                        profileNotifyDTO.setSubscriberNameChange(subsProfileInfoDTO.getMdn());
                    }
                    if (provRespDTO.isSubsTypeChanged()) {
                        profileNotifyDTO.setSubscriptionTypeChange(subsProfileInfoDTO.getMdn());
                    }
                    profileNotifyDTO.setMdn(subsProfileInfoDTO.getMdn());
                    profileNotifyDTO.setPocHome(provRespDTO.getDirChgDTO().getPocHome());
                    profileNotifyDTO.setPresenceHome(provRespDTO.getDirChgDTO().getPresenceHome());
                    profileNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());

                    commonMediator.sendProfileNotification(profileNotifyDTO, provRespDTO.getDirChgDTO(), pv);
                } else {
                    commonMediator.sendXcapNotification(provRespDTO.getDirChgDTO(), pv);

                }
            }


            if (provRespDTO.isActiveFSChanged()) {
                // Starting a thread for resourceList/GroupEtag/DirectoryEtag.
                /*Map<String, Boolean> mdnListMap = new HashMap<>();
                mdnListMap.put(subsProvInputDTO.getMdn(), provRespDTO.isActiveFSChanged());
                commonMediator.initiateEtagMgmtJob(mdnListMap,UPDATE_SUBSCRIBER);*/

                boolean xcapCouchClientBit = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), FEATURE_SET.XCAPCOUCHCLIENT.value());
                knLogger.debug(methodName, "xcapMobileSync:", xcapMobileSync, " xcapCouchClientBit:", xcapCouchClientBit, " PV:", subsProfileInfoDTO.getClientPVmajorVer());

                if (xcapMobileSync && (xcapCouchClientBit || subsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_18)) {
                    KnSubscrEXDMSNotifyDto subscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                    subscrEXDMSNotifyDto.setMdn(subsProvInputDTO.getMdn());
                    subscrEXDMSNotifyDto.setCorpid(provRespDTO.getCorpId());
                    subscrEXDMSNotifyDto.setActiveFS(KnGeneralUtil.convertHexStringToLong(provRespDTO.getActiveFS2()));
                    subscrEXDMSNotifyDto.setActiveFS2(provRespDTO.getActiveFS2());
                    subscrEXDMSNotifyDto.setOldActiveFS(provRespDTO.getOldActiveFS2());
                    subscrEXDMSNotifyDto.setPv(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                    subscrEXDMSNotifyDto.setId(FEATURE_BIT_CHANGE.value() + KnConstants.LINE_SAPERATOR + subsProvInputDTO.getMdn());
                    subscrEXDMSNotifyDto.setType(FEATURE_BIT_CHANGE.value());
                    subscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    subscrEXDMSNotifyDto.setNotifyEventType(MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                    List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<KnSubscrEXDMSNotifyDto>();
                    notifyDtoList.add(subscrEXDMSNotifyDto);
                    knLogger.info(methodName, "Publishing micro service notify  for User event - ");
                    commonMediator.startNotifyMicroServicesJob(notifyDtoList);
                    knLogger.debug(methodName, "micro service notify for activeFs change event - Sent");
                }
            }


            // sending the response to the Client
            responseDTO.setEtag(provRespDTO.getEtag());
            knLogger.info(methodName, "EXIT: update MCSIds - ", responseDTO);
            // updating the Pegs
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_ASSIGN_MCXIDS_REQ_SUCC);
            return commonMediator.getSuccessResponse(responseDTO);

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to update MCSIds Info", e);
            rollback(persisterTxn);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            rollback(persisterTxn);
            return commonMediator.getFailureResponse(responseDTO, e);
        }

    }

    @Override
    public IXDMResponseDTO updateUserId(IXDMRequestDTO requestDTO) {
        String methodName = "updateUserId(KnMessage)";
        knLogger.info(methodName, "ENTRY: update UserId - ", requestDTO);
        KnPersisterTxn persisterTxn = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        KnXDMSubsProvInfoDTO subsProvInputDTO = null;
        KnOPUpdateSubsInfoDTO provRespDTO = null;
        KnPayloadIP payLaodIP = new KnPayloadIP();
        BitSet taskBitSet = new BitSet();
        try {
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_ASSIGN_USER_ALIAS_ID_REQ);
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // validating if the received input DTO is KnXDMSubsProvInfoDTO
            knLogger.debug(methodName, "Received from the input DTO - ", requestDTO);
            if (requestDTO instanceof KnXDMSubsProvInfoDTO) {
                subsProvInputDTO = (KnXDMSubsProvInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO for update UserId - ", subsProvInputDTO);
            } else {
                knLogger.error(methodName, "receive an Invalid DTO for update UserId op - ", requestDTO);
                responseDTO = new KnXDMRespDTO();
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                return responseDTO;
            }
            knLogger.debug(methodName, "retrieving the subscriber profile for mdn ");
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(subsProvInputDTO.getMdn());
            subscriberInfoDTO.setAliasMdn(subsProvInputDTO.getAliasMdn());
            subscriberInfoDTO.setMcId(subsProvInputDTO.getMcId());
            subscriberInfoDTO.setMcPttId(subsProvInputDTO.getMcpttId());
            subscriberInfoDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
            subscriberInfoDTO.setMcDataId(subsProvInputDTO.getMcVideoId());
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberInfoDTO, persisterTxn);
            knLogger.debug(methodName, "subscriber profile ", subsProfileInfoDTO);

            //check if the mdn is a pseudo mdn then restrict the operation
            if (subsProfileInfoDTO.getPamAccId() != null && subsProfileInfoDTO.getPamAccId() != 0) {
                knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO,
                        "Mdn is present as a pseudo mdn in POCSUBSCRINFO");
            }

            //Validating the  CORP Hirarchy
            if (subsProvInputDTO.getExtCorpId() != null && !KnGeneralProfileUtil.validateExtCorpCCAndHierarchy(subsProfileInfoDTO.getAccountId(),
                    subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the corp : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid Corp hierarchy passed");
            }

            //Validating the  MDN Hirarchy
            if (!KnGeneralProfileUtil.validateMDNCCAndHierarchy(subsProfileInfoDTO.getMdn(), subsProvInputDTO.getHierarchyType())) {
                knLogger.error(methodName, "Hierarchy flag not valid for the mdn : ", subsProvInputDTO.getHierarchyType());
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST, "Invalid MDN hierarchy passed");
            }


            Integer subsClientType = subsProfileInfoDTO.getSubsClientType();
            //retriving from db/cache client type config check is enable or disable
            //LMR client type changes
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {

                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                    knLogger.error(methodName, "Client type is disabled");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                }
            }


            KnIPSubsProvInfoDTO subsProvInfoDTO = new KnIPSubsProvInfoDTO();
            subsProvInfoDTO.setMdn(subsProfileInfoDTO.getMdn());
            subsProvInfoDTO.setUserId(subsProvInputDTO.getUserId());
            // opening the transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "opening the transaction");
            persisterTxn.open();
            // invoking the library for updateUserId
            //*****Calling processUpdateSubscriber method to update subscriber******
            subsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
            subsProvInfoDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER);
            KnProvUpdateSubscProcessor knProvUpdateSubscProcessor = new KnProvUpdateSubscProcessor();
            provRespDTO = knProvUpdateSubscProcessor.processUpdateSubscriber(subsProvInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Response received from Server - ", provRespDTO);
            if (KnConstants.RESPONSE_STATUS.SUCCESS.value() != provRespDTO.getResponseStatus()) {
                knLogger.error(methodName, "Failed to Update subscriber Info : ", provRespDTO.getResponseStatus());
                rollback(persisterTxn);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
                return commonMediator.getFailureResponse(responseDTO, new KnXDMServerException(String.valueOf(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE), "Failed to Update subscriber Info"));
            } else if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == provRespDTO.getResponseStatus()) {
                // Sending the logic of 'createOidcDeviceSharingProfileWithMCSIds' to Async.
                knLogger.debug(methodName, "Invoking createOidcDeviceSharingProfileWithMCSIds as an ASYNC Job");
                // Remaining logic is set using the subsProfileInfo which is fetch using getSubscriberDetails with the mdn
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isCreateOidcDeviceSharingProfileWithMCSIds.get(), true);
            }

            // sending the notification
            String pv = String.valueOf(subsProfileInfoDTO.getClientPVmajorVer());
            knLogger.debug(methodName, "PV :", pv);

            KnOPProvDTO opProvDTO = provClientIntf.actionOnTGSSDoc(subscriberInfoDTO.getMdn(), provRespDTO.getActiveFS2(), subsProfileInfoDTO.getActiveFS2(), persisterTxn);
            KnPendingTxnInfoDTO knPendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            knLogger.debug(" inserting into async table ");
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isCorpLastProfileUpdateRequired.get(), true);
            payLaodIP.setCorpId(String.valueOf(provRespDTO.getCorpId()));
            knPendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
            knPendingTxnInfoDTO.setOpsId(KnGeneralUtil.ASYNC_OPS_ID.UPDATE_USER_ID.get());
            insertDataIntoAsynkFwkTable(subsProfileInfoDTO.getMdn(), provRespDTO.getCorpId(), knPendingTxnInfoDTO, payLaodIP, xdmPttServerId, persisterTxn);
            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();

            if (opProvDTO.getDirChgDTO() != null) {
                knLogger.debug(methodName, "TGSS doc changed sending notification");
                commonMediator.sendXcapNotification(opProvDTO.getDirChgDTO(), pv);
            }

            knLogger.debug(methodName, "Subs clientType :", subsProfileInfoDTO.getSubsClientType(), " subsProvInputDTO.getSubscriberClientType() :",
                    subsProvInputDTO.getSubscriberClientType());
            int oldSubsClientType = subsProfileInfoDTO.getSubsClientType();
            if (oldSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() && oldSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                if ((pv.matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX))
                        && (provRespDTO.isActiveFSChanged() || provRespDTO.isSubsNameChanged() || provRespDTO.isSubsTypeChanged())) {
                    KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                    if (provRespDTO.isActiveFSChanged()) {
                        profileNotifyDTO.setActiveFeatureSetChange(subsProfileInfoDTO.getMdn());
                    }
                    if (provRespDTO.isSubsNameChanged()) {
                        profileNotifyDTO.setSubscriberNameChange(subsProfileInfoDTO.getMdn());
                    }
                    if (provRespDTO.isSubsTypeChanged()) {
                        profileNotifyDTO.setSubscriptionTypeChange(subsProfileInfoDTO.getMdn());
                    }
                    profileNotifyDTO.setMdn(subsProfileInfoDTO.getMdn());
                    profileNotifyDTO.setPocHome(provRespDTO.getDirChgDTO().getPocHome());
                    profileNotifyDTO.setPresenceHome(provRespDTO.getDirChgDTO().getPresenceHome());
                    profileNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());

                    commonMediator.sendProfileNotification(profileNotifyDTO, provRespDTO.getDirChgDTO(), pv);
                } else {
                    commonMediator.sendXcapNotification(provRespDTO.getDirChgDTO(), pv);

                }
            }

            if (provRespDTO.isActiveFSChanged()) {
                // Starting a thread for resourceList/GroupEtag/DirectoryEtag.
                /*Map<String, Boolean> mdnListMap = new HashMap<>();
                mdnListMap.put(subsProvInputDTO.getMdn(), provRespDTO.isActiveFSChanged());
                commonMediator.initiateEtagMgmtJob(mdnListMap,UPDATE_SUBSCRIBER);*/

                //Get the xcap mobile sync flag
                boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
                boolean xcapCouchClientBit = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), FEATURE_SET.XCAPCOUCHCLIENT.value());
                knLogger.debug(methodName, "xcapMobileSync:", xcapMobileSync, " xcapCouchClientBit:", xcapCouchClientBit, " PV:", subsProfileInfoDTO.getClientPVmajorVer());
                if (xcapMobileSync && (xcapCouchClientBit || subsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_18)) {
                    KnSubscrEXDMSNotifyDto subscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                    subscrEXDMSNotifyDto.setMdn(subsProvInputDTO.getMdn());
                    subscrEXDMSNotifyDto.setCorpid(provRespDTO.getCorpId());
                    subscrEXDMSNotifyDto.setActiveFS(KnGeneralUtil.convertHexStringToLong(provRespDTO.getActiveFS2()));
                    subscrEXDMSNotifyDto.setActiveFS2(provRespDTO.getActiveFS2());
                    subscrEXDMSNotifyDto.setOldActiveFS(provRespDTO.getOldActiveFS2());
                    subscrEXDMSNotifyDto.setPv(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                    subscrEXDMSNotifyDto.setId(FEATURE_BIT_CHANGE.value() + KnConstants.LINE_SAPERATOR + subsProvInputDTO.getMdn());
                    subscrEXDMSNotifyDto.setType(FEATURE_BIT_CHANGE.value());
                    subscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    subscrEXDMSNotifyDto.setNotifyEventType(MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                    List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<KnSubscrEXDMSNotifyDto>();
                    notifyDtoList.add(subscrEXDMSNotifyDto);
                    knLogger.info(methodName, "Publishing micro service notify  for User event - ");
                    commonMediator.startNotifyMicroServicesJob(notifyDtoList);
                    knLogger.debug(methodName, "micro service notify for activeFs change event - Sent");
                }
            }


            // sending the response to the Client
            responseDTO.setEtag(provRespDTO.getEtag());
            knLogger.info(methodName, "EXIT: update UserId - ", responseDTO);
            // updating the Pegs
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_ASSIGN_USER_ALIASID_REQ_SUCC);
            return commonMediator.getSuccessResponse(responseDTO);

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to update UserId Info", e);
            rollback(persisterTxn);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            rollback(persisterTxn);
            return commonMediator.getFailureResponse(responseDTO, e);
        }
    }

    @Override
    public IXDMResponseDTO updateLicensePackSubsMCSIds(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateLicensePackSubsUserId(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updatePrivacyOptStatus(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCPTTUEConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCPTTUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCPTTServiceConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCDATAUEConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCDATAUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCDATAServiceConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCVideoUEConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCVideoUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCVideoServiceConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCSGroupDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMdnProfileIdsForMcPttIds(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCSUserDir(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO selectProfileMdn(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createDevice(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteDeviceInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyDevice(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO loginNotifyEvent(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubsGroupMembershipDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createBulkCorpGroup(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getProfileGroupList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createGroupProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupProfileDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO searchGroupProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createCorpGroupWithProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyGroupProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteBulkCorpGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteGroupProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAuthorizedUserList(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSharedCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserprofileidsByProfileMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrClientSettings(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setSubscrClientSettings(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMdnAuthorizationForGroupId(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyGroupsUGWConfig(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateFS(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorporateFS(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupsDetailsWithoutMembers(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO cloneContactsGroupsAndFeatures(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO setCATAccessPermission(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getExtGWProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    // PTT Setting
    @Override
    public IXDMResponseDTO createPTTSettingDoc(IXDMRequestDTO requestDTO)  { return null; }
    @Override
    public IXDMResponseDTO getPTTSettingDocList(IXDMRequestDTO requestDTO) {
        return null;
    }
    @Override
    public IXDMResponseDTO getPTTSettingDoc(IXDMRequestDTO requestDTO) {
        return null;
    }
    @Override
    public IXDMResponseDTO setDefaultPttSettingDoc(IXDMRequestDTO requestDTO) { return null; }

    @Override
    public IXDMResponseDTO deletePTTSettingDoc(IXDMRequestDTO requestDTO) { return null;}
    @Override
    public IXDMResponseDTO assignPttSettingToHierarchy(IXDMRequestDTO requestDTO) { return null;}
    @Override
    public IXDMResponseDTO unassignPttSettingToHierarchy(IXDMRequestDTO requestDTO) { return null;}

    @Override
    public IXDMResponseDTO assignPttSettingDocToMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO unassignPttSettingDocToMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPttSettingDocMdnList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMDNCountForPttSettingDocID(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignPttSettingToCorp(IXDMRequestDTO requestDTO) { return null;}

    @Override
    public IXDMResponseDTO unassignPttSettingToCorp(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyPTTSettingTemplate(IXDMRequestDTO requestDTO) {
        return null;
    }


    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }

    private void sendDeleteDeviceNotification(String deviceIMPI) {
        String methodName = "sendDeleteDeviceNotification";
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

    //Using this method data can be inserted into AsynkFwk table as per the use case
    //Exception will be thrown if any error occurs and the process should be terminated
    public static void insertDataIntoAsynkFwkTable(String mdn, int corpId, KnPendingTxnInfoDTO pendingTxnInfoDTO, KnPayloadIP knPayloadIP,
                                                   String pttServerId, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "insertDataIntoAsynkFwkTable";
        try {
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(pttServerId);
            boolean isDeleteMdnRequest = false;
            if (pendingTxnInfoDTO == null) {
                pendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            }
            pendingTxnInfoDTO.setRecId(1);//Should be changed
            pendingTxnInfoDTO.setCorpId(corpId);
            pendingTxnInfoDTO.setEntityId(mdn);
            pendingTxnInfoDTO.setTxnId(UUID.randomUUID().toString());
            if (knPayloadIP == null) {
                knPayloadIP = new KnPayloadIP();
            }
            pendingTxnInfoDTO.setPayload(KnGeneralUtil.convertKnPaylaodIPToJsonString(knPayloadIP));
            if (pendingTxnInfoDTO.getPayload().length() > 2000) {
                knLogger.error(methodName, "Payload size is more than 2000 bytes");
                throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Payload size is more than 2000 bytes");
            }
            pendingTxnInfoDTO.setPriority(1);
            pendingTxnInfoDTO.setEntityType(1);
            if (pendingTxnInfoDTO.getOpsId() == null) {
                pendingTxnInfoDTO.setOpsId(1);
            }
            pendingTxnInfoDTO.setEntityType(KnGeneralUtil.EntityName.MDN.get());
            xdmDAO.insertIntoAsyncTable(pendingTxnInfoDTO, persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Error while inserting data into AsynkFwk table", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed due to exception hence rejecting the request");
        }
        //insert this to database
    }

    public static KnPendingTxnInfoDTO populateKnPayLoadIP(KnPayloadIP knPayloadIP, BitSet taskBitSet, KnPendingTxnInfoDTO pendingTxnInfoDTO, String mdn, String pttserverId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "populateKnPayLoadIP";
        knLogger.debug(methodName, "ENTRY: populateKnPayLoadIP - ", mdn);
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(pttserverId);
        if (!taskBitSet.get(KnGeneralUtil.ASYNC_TASK_ID.notifyAllGroupMembers.get()) && xdmDAO.IsMdnPresentAsGroupMember(mdn, persisterTxn)) {
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.notifyAllGroupMembers.get(), true);
        }

        if (!taskBitSet.get(KnGeneralUtil.ASYNC_TASK_ID.isContactNotifyRequired.get()) && xdmDAO.IsMdnPresentAsContact(mdn, persisterTxn)) {
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isContactNotifyRequired.get(), true);
            KncontactNotifyInfoDTO kncontactNotifyInfoDTO = new KncontactNotifyInfoDTO();
            kncontactNotifyInfoDTO.setRemovedContacts(Collections.singletonList(mdn));
            knPayloadIP.setKncontactNotifyInfoDTO(kncontactNotifyInfoDTO);
        }

        if (!taskBitSet.get(KnGeneralUtil.ASYNC_TASK_ID.isAuthorizationNotifyRequired.get()) && xdmDAO.IsMdnPresentAsTarget(mdn, persisterTxn)) {
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isAuthorizationNotifyRequired.get(), true);
            KnAuthorizationNotifyInfoDTO knAuthorizationNotifyInfoDTO = new KnAuthorizationNotifyInfoDTO();
            knAuthorizationNotifyInfoDTO.setRemovedTargets(Collections.singletonList(mdn));
            knPayloadIP.setKnAuthorizationNotifyInfoDTO(knAuthorizationNotifyInfoDTO);
        }

        if (!taskBitSet.get(KnGeneralUtil.ASYNC_TASK_ID.isEmergencyConfigNotifyRequired.get()) && xdmDAO.IsMdnPresentAsDestination(mdn, persisterTxn)) {
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isEmergencyConfigNotifyRequired.get(), true);
            KnEmergencyConfigNotifyInfoDTO knEmergencyConfigNotifyInfoDTO = new KnEmergencyConfigNotifyInfoDTO();
            emergcyDestInfo knEmergencyDestInfo = new emergcyDestInfo();
            knEmergencyDestInfo.setDestination(mdn);
            knEmergencyConfigNotifyInfoDTO.setemergencyDestinationListInfo(Collections.singletonList(knEmergencyDestInfo));
            knPayloadIP.setKnEmergencyConfigNotifyInfoDTO(knEmergencyConfigNotifyInfoDTO);
        }
        pendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
        return pendingTxnInfoDTO;
    }

    /**
     * method to change Service Auth Status to re-activate or de-activate the
     * subscriber
     *
     * @param requestDTO   IXDMRequestDTOG
     * @param persisterTxn
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeServiceAuthStatus(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn, KnConstants.MESSAGE_TYPE action) {
        String methodName = "changeServiceAuthStatus(KnMessage)";
        knLogger.info(methodName, "ENTRY: change Service Auth Status - ", requestDTO);
        KnXDMSubsStatusInfoDTO subStatusInfoDTO = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        boolean ownedTxn = false;
        try {
            // verify if the inputDTO is of KnXDMSubsStatusInfoDTO
            // inputDTO = (IXDMRequestDTO) message.getPayLoad();
            if (requestDTO instanceof KnXDMSubsStatusInfoDTO) {
                subStatusInfoDTO = (KnXDMSubsStatusInfoDTO) requestDTO;
                knLogger.debug(methodName, "Received DTO for change Service Auth Status - ", subStatusInfoDTO);
            } else {
                knLogger.error(methodName, "received and Invalid DTO for change service auth status op - ", requestDTO);
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                // commonMediator.sendFailureResponse(responseDTO, message,
                // null);
                return responseDTO;
            }

            // opening the transaction
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            // populating the prov libraryDTO
            KnIPSubsProvInfoDTO subscriberInfoDTO = new KnIPSubsProvInfoDTO();
            subscriberInfoDTO.setMdn(subStatusInfoDTO.getMdn());
            subscriberInfoDTO.setIfMatch(subStatusInfoDTO.getIfMatch());
            subscriberInfoDTO.setIfNoneMatch(subStatusInfoDTO.getIfNoneMatch());
            subscriberInfoDTO.setServiceAuthStatus(subStatusInfoDTO
                    .getServiceAuthStatus());
            subscriberInfoDTO.setClientType(requestDTO.getClientType());

            KnIPSubscriberInfoDTO subsInfoDTO = new KnIPSubscriberInfoDTO();
            subsInfoDTO.setMdn(subscriberInfoDTO.getMdn());

            //corsscheck
            knLogger.debug(methodName, "retrieving the subscriber profile for mdn " + KnGDPRTemplate.mdn(subsInfoDTO.getMdn()));

            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subsInfoDTO, persisterTxn);

            String pv = String.valueOf(subsProfileInfoDTO.getClientPVmajorVer());
            subscriberInfoDTO.setMcpttCompliance(subsProfileInfoDTO.getMcpttCompliance());

            knLogger.debug(methodName, "pv :" + pv);

            // invoking the prov library
            KnOPChgAuthStatusRespDTO provRespDTO = provClientIntf
                    .changeServiceAuthStatus(subscriberInfoDTO, persisterTxn);
            Integer subsClientType = provRespDTO.getSubsClientType();
            //retriving from db client type config check is enable or disable
            knLogger.debug(methodName, "Subscriber client type :", subsClientType);
            //LMR client type changes
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                    || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {

                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                    knLogger.error(methodName, "Client type is disabled :", subsClientType);
                    throw new KnProvBOException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                }
            }

            knLogger.debug(methodName, "Response received from Server - ",
                    provRespDTO);

            //MC DEVICE NOTIFY
            if (provRespDTO.isDeleteDeviceNotify()) {
                sendDeleteDeviceNotification(TEL_URI_TEMPLATE + subscriberInfoDTO.getMdn());
            }

            //profile mdn notify
            boolean profileMdnNotifystatus = commonMediator.prepareMcxNotifyForProfileMdns(provRespDTO);
            knLogger.debug(methodName, "profileMdnNotifystatus :", profileMdnNotifystatus);

            KnSubscriberInfoDTO knSubscriberInfoDTO = new KnSubscriberInfoDTO();

            //Fetching the boolean value for 45th bit,
            // If it is true then we will call the new REST API.
            boolean xcapCouchClientBit = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFs2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
            knLogger.debug(methodName, "xcapCouchClientBit ", xcapCouchClientBit);

            //checking if 45th bit is 0/false or 1/true. If not 0/false then we make the rest call
            if (xcapCouchClientBit) {
                //Populating the data KnSubscriberInfoDTO knSubscriberInfoDTO.
                knSubscriberInfoDTO.setMdn(provRespDTO.getMdn());
                knSubscriberInfoDTO.setDisabled(provRespDTO.isSyncDisabled());
                knLogger.debug(methodName, "knSubscriberInfoDTO : mdn", KnGDPRTemplate.mdn(knSubscriberInfoDTO.getMdn()));
                knLogger.debug(methodName, "knSubscriberInfoDTO : sync", knSubscriberInfoDTO.isDisabled());

                //Storing the list of bucket urls in a list
                Map<Integer, String> bucketUrlList = genInfoUtil.retrievePTXbucketUrls(persisterTxn);
                knLogger.debug(methodName, "bucketUrlList : ", bucketUrlList);
                KnFailedCBTxnLogDTO failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                genInfoUtil.deleteCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                for (Map.Entry<Integer, String> entry : bucketUrlList.entrySet()) {
                    List<String> bucketUrl = new ArrayList<>();
                    Integer curClusterId = entry.getKey();
                    knLogger.debug(methodName, "Updating/deleting profile in clusterId - ", curClusterId);
                    bucketUrl.add(entry.getValue());
                    //REST call for profile creation starts here.
                    KnSyncResponseDTO knSyncResponseDTO = KnManageSyncUserProfileUtil.getInstance().updateSyncDocument(knSubscriberInfoDTO, bucketUrl);
                    knLogger.debug(methodName, "KnSyncResponseDTO status : ", knSyncResponseDTO.getStatus());
                    if (knSyncResponseDTO.getStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                        failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                        failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                        failedCBTxnLogDTO.setImpactedClusterId(entry.getKey());
                        failedCBTxnLogDTO.setTxnType(KnConstants.CB_PROFILE_TXN_TYPE.PROFILE_ENABLE.value());
                        if (knSubscriberInfoDTO.isDisabled()) {
                            failedCBTxnLogDTO.setTxnType(KnConstants.CB_PROFILE_TXN_TYPE.PROFILE_DISABLE.value());
                        }
                        genInfoUtil.insertCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                    }
                }
            }
            //Get the xcap mobile sync flag
            boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync", xcapMobileSync);
            KnOPProvDTO opProvDTO = provClientIntf.actionOnTGSSDoc(subsProfileInfoDTO.getMdn(), provRespDTO.getActiveFs2(), subsProfileInfoDTO.getActiveFS2(), persisterTxn);
            KnNotificationParamDTO knNotificationParamDTO = new KnNotificationParamDTO();
            knNotificationParamDTO.setPriority(KnConstants.NOTIFICATION_PRIORITY.HIGH.value());
            if (opProvDTO.getDirChgDTO() != null) {
                knLogger.debug(methodName, "TGSS doc changed sending notification");
                commonMediator.sendXcapNotification(opProvDTO.getDirChgDTO(), pv, knNotificationParamDTO);
            }

            String mcsXcapRootUri = genInfoUtil.getMCSXCAPRootURI(subsProfileInfoDTO.getMdn(), persisterTxn);

            // saving the transaction
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }


            if (subsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_18) {
                knLogger.debug(methodName, "Sending MCS notification");
                commonMediator.sendMCSNotification(opProvDTO.getDirChgDTO(), subsProfileInfoDTO.getMcId(), mcsXcapRootUri, knNotificationParamDTO);
            }
            knLogger.debug(methodName, "xcapMobileSync:", xcapMobileSync);
            if (xcapMobileSync) {
                KnSubscrEXDMSNotifyBulkDTO knSubscrEXDMSNotifyBulkDTO = new KnSubscrEXDMSNotifyBulkDTO();
                //  List contain description of the data: variable name is mdnList as json o/p req
                List<KnSubscrEXDMSNotifyBulkDTO> KnSubscrEXDMSNotifyBulkDToList = new ArrayList<KnSubscrEXDMSNotifyBulkDTO>();

                knSubscrEXDMSNotifyBulkDTO.setId(CHANGE_SERVICE_AUTH_STATUS.value());
                knSubscrEXDMSNotifyBulkDTO.setType(CHANGE_SERVICE_AUTH_STATUS.value());
                knSubscrEXDMSNotifyBulkDTO.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                knSubscrEXDMSNotifyBulkDTO.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());

                List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
                KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDTO = new KnSubscrEXDMSNotifyDto();
                knSubscrEXDMSNotifyDTO.setMdn(provRespDTO.getMdn());
                setKnSubscriberExdmsNotifyDto(provRespDTO, subsClientType, knSubscrEXDMSNotifyDTO);
                notifyDtoList.add(knSubscrEXDMSNotifyDTO);
                knLogger.debug(methodName, "knSubscrEXDMSNotifyDTO :", knSubscrEXDMSNotifyDTO.toString());

                if (provRespDTO.getProfileMdnList() != null && !provRespDTO.getProfileMdnList().isEmpty()) {
                    provRespDTO.getProfileMdnList().stream().forEach(profileMdn -> {
                        KnSubscrEXDMSNotifyDto profileMdnSubscrEXDMSNotifyDTO = new KnSubscrEXDMSNotifyDto();
                        profileMdnSubscrEXDMSNotifyDTO.setMdn(profileMdn);
                        setKnSubscriberExdmsNotifyDto(provRespDTO, subsClientType, profileMdnSubscrEXDMSNotifyDTO);
                        notifyDtoList.add(profileMdnSubscrEXDMSNotifyDTO);
                    });
                }
                knSubscrEXDMSNotifyBulkDTO.setMdnList(notifyDtoList);
                KnSubscrEXDMSNotifyBulkDToList.add(knSubscrEXDMSNotifyBulkDTO);
                knLogger.debug(methodName, "KnSubscrEXDMSNotifyBulkDToList :", KnSubscrEXDMSNotifyBulkDToList);

                knLogger.debug(methodName, "Publishing micro service notify - ");
                commonMediator.startNotifyMicroServicesJob(KnSubscrEXDMSNotifyBulkDToList);
            }

            // populate the KnXcapDiffNotifyDTO
            // Notification requires the following params
            // 1. Dir Uri, 2. Dir prev etag, 3. Dir new etag,
            // 4. Doc Uri, 5. Doc new etag
            List<KnXcapDiffNotifyDTO> xcapDiffList = new ArrayList<>();
            List<KnOPDirChgDTO> dirChgDTOs = provRespDTO.getDirChgDTOs();
            int serviceAuthStatus = subStatusInfoDTO.getServiceAuthStatus();
            if (dirChgDTOs != null && !dirChgDTOs.isEmpty()) {
                for (KnOPDirChgDTO dirChgDTO : dirChgDTOs) {
                    KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
                    if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()
                            || serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                        if ((dirChgDTO.getProtoVersion().matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX))
                                && (action.equals(KnConstants.MESSAGE_TYPE.CLIENT_TYPE_CHANGE) || action.equals(KnConstants.MESSAGE_TYPE.USER_CREDENTIAL_CHANGE))) {
                            KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                            if (action.equals(KnConstants.MESSAGE_TYPE.CLIENT_TYPE_CHANGE)) {
                                profileNotifyDTO.setClientTypeChange(String.valueOf(provRespDTO.getSubsClientType()));
                                xcapDiffNotifyDTO.setReason(KnConstants.REASON.CLIENT_TYPE_CHANGE.value());
                            } else if (action.equals(KnConstants.MESSAGE_TYPE.USER_CREDENTIAL_CHANGE)) {
                                profileNotifyDTO.setCredentialChange(dirChgDTO.getMdn());
                                xcapDiffNotifyDTO.setReason(KnConstants.REASON.USER_CREDENTIAL_CHANGE.value());
                            }
                            xcapDiffNotifyDTO.setProfileNotify(true);
                            profileNotifyDTO.setMdn(dirChgDTO.getMdn());
                            profileNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                            profileNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                            profileNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());
                            xcapDiffNotifyDTO.setProfileNotifyDTO(profileNotifyDTO);
                        } else if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                            xcapDiffNotifyDTO.setDeRegisterNotify(true);
                            KnDeRegisterNotifyDTO deRegisterNotifyDTO = new KnDeRegisterNotifyDTO();
                            deRegisterNotifyDTO.setMdn(dirChgDTO.getMdn());
                            deRegisterNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                            deRegisterNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                            deRegisterNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.USER_DEACTIVATE.value());
                            xcapDiffNotifyDTO.setReason(KnConstants.REASON.USER_DEACTIVATE.value());
                            xcapDiffNotifyDTO.setDeRegisterNotifyDTO(deRegisterNotifyDTO);
                        }

                    }
                    Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<KnXcapDiffDocDTO>();
                    ArrayList<KnOPDocChgDTO> chgDocList = (ArrayList<KnOPDocChgDTO>) dirChgDTO.getDocChgDTO();
                    for (KnOPDocChgDTO chgDTO : chgDocList) {
                        KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                        xcapDiffDocDTO.setDocChangeType(chgDTO.getDocumentChgType());
                        xcapDiffDocDTO.setDocEtag(chgDTO.getNewEtag());
                        xcapDiffDocDTO.setDocumentSelector(chgDTO.getDocUri());
                        xcapDiffDocDTO.setVideoPermission(chgDTO.getVideoPermission());
                        xcapDocList.add(xcapDiffDocDTO);
                    }

                    xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
                    xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
                    xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                    xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                    xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                    xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
                    xcapDiffNotifyDTO.setPocHome(provRespDTO.getPocServerHome());
                    xcapDiffNotifyDTO.setPresenceHome(provRespDTO.getPresenceServerHome());
                    xcapDiffList.add(xcapDiffNotifyDTO);
                }
                knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffList);
                boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, knNotificationParamDTO);
                knLogger.debug(methodName, "Notification status - ", isNotified);
            }

            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATED);
            if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_ACTIVATED);
            } else if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DEACTIVATED);
            }


            // sending the response to the Client
            responseDTO.setEtag(provRespDTO.getEtag());
            knLogger.info(methodName, "EXIT: change Service Auth Status - ", requestDTO);
            return commonMediator.getSuccessResponse(responseDTO);

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to Change service auth status of Subscriber ", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (!e.getErrorCode().equals(NO_CHG_IN_PROFILE)) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
            }
            return commonMediator.getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATE_FAILURE);
            return commonMediator.getFailureResponse(responseDTO, e);
        }
    }

    /**
     * This method sets all the required parameters into KnSubscrEXDMSNotifyDto for changeServiceAuthStatus method
     *
     * @param provRespDTO
     * @param subsClientType
     * @param knSubscrEXDMSNotifyDTO
     */
    private void setKnSubscriberExdmsNotifyDto(KnOPChgAuthStatusRespDTO provRespDTO, Integer subsClientType, KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDTO) {
        knSubscrEXDMSNotifyDTO.setCorpid(provRespDTO.getCorpid());
        knSubscrEXDMSNotifyDTO.setAuthStatus(provRespDTO.getAuthStatus());
        knSubscrEXDMSNotifyDTO.setClientType(subsClientType);
        knSubscrEXDMSNotifyDTO.setLastProfileUpdateTime(provRespDTO.getEtag());
        knSubscrEXDMSNotifyDTO.setSubsFS2(provRespDTO.getSubsFS2());
        knSubscrEXDMSNotifyDTO.setMcId(provRespDTO.getMcId());
        knSubscrEXDMSNotifyDTO.setMcdataId(provRespDTO.getMcDataId());
        knSubscrEXDMSNotifyDTO.setMcpttId(provRespDTO.getMcPttId());
        knSubscrEXDMSNotifyDTO.setMcvideoId(provRespDTO.getMcVideoId());
        knSubscrEXDMSNotifyDTO.setDeviceId(provRespDTO.getMdn());
        knSubscrEXDMSNotifyDTO.setNetworkName(provRespDTO.getNetworkName());
    }

    /**
     * This method constructs and sends microservice event notification for modify subscriber.
     *
     * @param subsProvInputDTO
     * @param provRespDTO
     * @param subsProfileInfoDTO contains the data in DB before update
     * @throws KnException
     */
    private void sendModifySubscriberMicroserviceEventNotify(KnXDMSubsProvInfoDTO subsProvInputDTO, KnOPUpdateSubsInfoDTO provRespDTO,
                                                             KnOPSubsProfileInfoDTO subsProfileInfoDTO) throws KnException {
        KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
        knSubscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
        knSubscrEXDMSNotifyDto.setId(MODIFY_SUBSCRIBER.value() + KnConstants.LINE_SAPERATOR + subsProvInputDTO.getMdn());
        knSubscrEXDMSNotifyDto.setType(MODIFY_SUBSCRIBER.value());
        knSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        knSubscrEXDMSNotifyDto.setMdn(subsProvInputDTO.getMdn());
        knSubscrEXDMSNotifyDto.setCorpid(provRespDTO.getCorpId());
        knSubscrEXDMSNotifyDto.setClientType(provRespDTO.getSubsClientType());
        knSubscrEXDMSNotifyDto.setLastProfileUpdateTime(provRespDTO.getLastProfileUpdateTime());
        knSubscrEXDMSNotifyDto.setSubsFS2(provRespDTO.getSubsFS2());
        knSubscrEXDMSNotifyDto.setMcId(provRespDTO.getMcId());
        knSubscrEXDMSNotifyDto.setMcdataId(provRespDTO.getMcDataId());
        knSubscrEXDMSNotifyDto.setMcpttId(provRespDTO.getMcPttId());
        knSubscrEXDMSNotifyDto.setMcvideoId(provRespDTO.getMcVideoId());
        knSubscrEXDMSNotifyDto.setNetworkName(subsProvInputDTO.getNetworkName());
        knSubscrEXDMSNotifyDto.setActiveFS2(provRespDTO.getActiveFS2());
        knSubscrEXDMSNotifyDto.setDeviceId(subsProvInputDTO.getMdn());
        knSubscrEXDMSNotifyDto.setAuthStatus(provRespDTO.getServiceAuthStatus());
        knSubscrEXDMSNotifyDto.setOldsubsFS2(subsProfileInfoDTO.getSubsFS2());
        List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
        notifyDtoList.add(knSubscrEXDMSNotifyDto);
        knLogger.info("sendModifySubscriberMicroserviceEventNotify", "Publishing micro service notify for user- ", notifyDtoList);
        commonMediator.startNotifyMicroServicesJob(notifyDtoList);
    }

    // Populating XDM response
    private KnXDMCorpRespDTO populateXdmResponse(KnCorpResponseDTO respDto) {

        String methodName = "populateXdmResponse(KnCorpResponseDTO respDto)";
        knLogger.entry(methodName);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        int status = respDto.getStatus();
        xdmRespDto.setResponseStatus(status);
        xdmRespDto.setResponseCode(respDto.getStatusCode());
        xdmRespDto.setResponseMessage(respDto.getMessage());
        Collection<KnCorpFailedData> failedDataList = respDto
                .getFailedDataList();
        String etag = respDto.getEtag();
        Collection<KnXDMFailureRespDTO> failureDetails = respDto.getFailureDetails();
        if (status == KnConstants.RESPONSE_STATUS.SUCCESS.value()) {
            if (failedDataList != null && !failedDataList.isEmpty()) {
                xdmRespDto
                        .setFailureDetails(populateFailureDetails(failedDataList));
            }
            if (etag != null) {
                xdmRespDto.setEtag(etag);
            }
        } else {
            if (failedDataList != null && !failedDataList.isEmpty()) {
                xdmRespDto.setFailureDetails(populateFailureDetails(failedDataList));
            }
            // validation fw consolidation error codes changes
            if (failureDetails != null && !failureDetails.isEmpty()) {
                Collection<KnXDMFailureRespDTO> finalFailureDetails = xdmRespDto.getFailureDetails();
                if (finalFailureDetails == null) {
                    xdmRespDto.setFailureDetails(failureDetails);
                } else {
                    finalFailureDetails.addAll(failureDetails);
                }
            }
        }
        knLogger.exit(methodName);
        return xdmRespDto;
    }

    // Populating XDM response
    private void populateXdmResponse(KnXDMCorpRespDTO xdmRespDto,
                                     KnCorpResponseDTO respDto) {
        int status = respDto.getStatus();
        xdmRespDto.setResponseStatus(status);
        xdmRespDto.setResponseCode(respDto.getStatusCode());
        xdmRespDto.setResponseMessage(respDto.getMessage());
        if (respDto.getIsSublistExists())
            xdmRespDto.setSublistExists(TRUE);
        Collection<KnCorpFailedData> failedDataList = respDto.getFailedDataList();
        Collection<KnXDMFailureRespDTO> failureDetails = respDto
                .getFailureDetails();
        String etag = respDto.getEtag();
        if (status == KnConstants.RESPONSE_STATUS.SUCCESS.value()) {
            if (failedDataList != null && !failedDataList.isEmpty()) {
                xdmRespDto.setFailureDetails(populateFailureDetails(failedDataList));
            }
            if (etag != null) {
                xdmRespDto.setEtag(etag);
            }
        } else {
            if (failedDataList != null && !failedDataList.isEmpty()) {
                xdmRespDto.setFailureDetails(populateFailureDetails(failedDataList));
            }
            // validation fw consolidation error codes changes
            if (failureDetails != null && !failureDetails.isEmpty()) {
                Collection<KnXDMFailureRespDTO> finalFailureDetails = xdmRespDto.getFailureDetails();
                if (finalFailureDetails == null) {
                    xdmRespDto.setFailureDetails(failureDetails);
                } else {
                    finalFailureDetails.addAll(failureDetails);
                }
            }
        }
    }

    private Collection<KnXDMFailureRespDTO> populateFailureDetails(
            Collection<KnCorpFailedData> failedDataList) {

        Collection<KnXDMFailureRespDTO> failedList = null;

        if (failedDataList == null || failedDataList.isEmpty()) {
            return failedList;
        }
        failedList = new ArrayList<KnXDMFailureRespDTO>(failedDataList.size());
        for (KnCorpFailedData failedData : failedDataList) {
            KnXDMFailureRespDTO failureDTO = new KnXDMFailureRespDTO(
                    failedData.getAttribute(),
                    getString(failedData.getValues()), failedData.getCode(),
                    failedData.getMsg());
            failedList.add(failureDTO);
        }
        return failedList;
    }

    private String getString(Collection<String> collectionStr) {
        StringBuffer sb = new StringBuffer(100);
        for (String str : collectionStr) {
            sb.append(str).append(",");
        }
        int indx = sb.lastIndexOf(",");
        if (indx > 0) {
            sb.deleteCharAt(indx);
        }
        return sb.toString();
    }

    private void initiateDispatchGrpJob(Collection<String> addedMdnList, Collection<String> deletedMdnList, Collection<String> addedLocWatcher,
                                        Collection<String> removedLocWatcher, int corpId) {
        String methodName = "initiateDispatchGrpJob(Collection<String>,Collection<String>,int)";
        // If added members list id not null and not empty start job to update
        // DISPATCH_GROUP_MEMBER field
        KnDispGrpMemChecker dispGrpMemCheckerJob = new KnDispGrpMemChecker();
        if (addedMdnList != null && !addedMdnList.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job Added- ");
            dispGrpMemCheckerJob.setAddedMembers(addedMdnList);
        }
        if (deletedMdnList != null && !deletedMdnList.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job removed- ");
            dispGrpMemCheckerJob.setDeletedMembers(deletedMdnList);
        }
        if (addedLocWatcher != null && !addedLocWatcher.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job addedLoc- ");
            dispGrpMemCheckerJob.setAddedLocWatcher(addedLocWatcher);
        }
        if (removedLocWatcher != null && !removedLocWatcher.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job removedLoc- ");
            dispGrpMemCheckerJob.setRemovedLocWatcher(removedLocWatcher);
        }
        List<KnDispGrpMemChecker> jobList = new ArrayList<KnDispGrpMemChecker>(
                1);
        dispGrpMemCheckerJob.setMaxNotificationSize(msgTruncationLimit);
        // dispGrpMemCheckerJob.setSyncNotfyThresold(syncNotifyThresold);
        dispGrpMemCheckerJob.setCorpId(corpId);
        jobList.add(dispGrpMemCheckerJob);
        try {
            knLogger.debug(methodName, "Scheduling the Jobs - ", jobList);
            scheduler.addRamJob(jobList, KnJobConstants.JOB_GROUP_NAME);
        } catch (KnJobSchedulerException e) {
            knLogger.error(methodName,
                    "KnJobSchedulerException occurred while ",
                    "submitting Notification Job to Scheduler - " + e);
        }
    }

    @Override
    public IXDMResponseDTO createHierarchy(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyHierarchy(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getRegions(IXDMRequestDTO requestDTO) {
        return null;
    }
}