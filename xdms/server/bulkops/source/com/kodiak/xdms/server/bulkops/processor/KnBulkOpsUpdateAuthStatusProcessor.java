package com.kodiak.xdms.server.bulkops.processor;

import com.kodiak.common.commdto.common.KnBulkSubscriberEntry;
import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnBulkOpsErrorDetail;
import com.kodiak.common.commdto.response.KnXDMBulkOpsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralProfileUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnDeRegisterNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnProfileNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.KnResponseHandlerImpl;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsXDMServerDAO;
import com.kodiak.xdms.server.bulkops.dao.KnCorpInfoDAO;
import com.kodiak.xdms.server.bulkops.dao.KnPOCSubscrInfoDAO;
import com.kodiak.xdms.server.bulkops.dao.KnXDMDirectoryDAO;
import com.kodiak.xdms.server.bulkops.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.bulkops.dto.common.*;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants.SERVICE_AUTH_STATUS;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsInfoUtil;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsNotifyUtil;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;


import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.CHANGE_SERVICE_AUTH_STATUS;
import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;
import static com.kodiak.common.resources.KnConstants.SOFTWARE_PKG.POC_SOAP_CSR;
import static com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes.BOEntity.*;


public class KnBulkOpsUpdateAuthStatusProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsUpdateAuthStatusProcessor.class);
    private static KnBulkOpsUpdateAuthStatusProcessor instance = null;
    private KnResponseHandlerImpl responseHandler = new KnResponseHandlerImpl();
    private KnBulkOpsInfoUtil knBulkOpsInfoUtil = KnBulkOpsInfoUtil.getInstance();
    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    public static final String TEL_URI_TEMPLATE = "tel:+";
    KnBulkOpsNotifyUtil bulkOpsNotifyUtil = KnBulkOpsNotifyUtil.getInstance();
    IXcapDiffNotifierIntf notifier = new KnXcapDiffNotifierImpl();


    public static synchronized KnBulkOpsUpdateAuthStatusProcessor getInstance() {
        if (instance == null) {
            instance = new KnBulkOpsUpdateAuthStatusProcessor();
        }
        return instance;
    }

    public IXDMResponseDTO updateBulkAuthStatus(IXDMRequestDTO requestDTO) {
        String methodName = "updateBulkAuthStatus(IXDMRequestDTO)";
        knLogger.info(methodName, "ENTRY: Received Request DTO - ", requestDTO);
        //initialise start time
        Long startTime = System.currentTimeMillis();
        KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO = null;
        KnXDMBulkOpsRespDTO responseDTO = null;
        KnPersisterTxn persisterTxn = null;
        boolean ownedTxn = (persisterTxn == null);
        List<String> mdns = new ArrayList<>();
        Map<String, KnBulkOpsErrorDetail> failureMdns = new HashMap<>();
        List<String> validMdns = new ArrayList<>();
        com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE hierarchyTypeFlag= null;

        try {
            // validate and extract bulk subs prov info DTO
            bulkSubsProvInfoDTO = validateAndExtractBulkSubsProvInfoDTO(requestDTO, responseDTO, persisterTxn);
            if (bulkSubsProvInfoDTO == null) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_UPDATE_AUTH_STATUS_FAILURE);
                return responseDTO;
            }

            // extract mdn list
            mdns = extractMdnList(bulkSubsProvInfoDTO.getSubscriberList());
            // validate hierarchy for all subscribers
            hierarchyTypeFlag = KnGeneralUtil.getHierarchyInterfaceType(POC_SOAP_CSR);

            if (hierarchyTypeFlag!= null) {
                Map<String, KnBulkOpsErrorDetail> hierarchyValidationFailures = validateHierarchy(mdns, hierarchyTypeFlag);
                failureMdns.putAll(hierarchyValidationFailures);
                validMdns = new ArrayList<>(mdns.stream().filter(mdn -> !hierarchyValidationFailures.containsKey(mdn)).toList());
            } else {
                validMdns = new ArrayList<>(mdns);
            }
            knLogger.debug(methodName, "Valid MDNs after hierarchy validation - ", KnGDPRTemplate.mdnList(validMdns));
            // ensure transaction is open
            persisterTxn = ensureTransactionOpen(persisterTxn, methodName);
            knLogger.debug(methodName, "MDNs for auth status update - ", KnGDPRTemplate.mdnList(validMdns));


            // validate subscriber auth status
            failureMdns.putAll(validateSubAuthStatus(validMdns, persisterTxn));
            //don't process invalid mdns
            validMdns = new ArrayList<>(validMdns.stream().filter(mdn -> !failureMdns.containsKey(mdn)).toList());
            knLogger.debug(methodName, "Valid MDNs after auth status validation - ", KnGDPRTemplate.mdnList(new ArrayList<>(failureMdns.keySet())));

            // get subscriber profiles
            //alias/userid/mdn based fetch is done in the current flow that's used in notification
            // a fetch based on mdn is used for update operation
            //skipped the alias based impl for now as the input dto contains only mdn list and is used in notification
            //will be revisited if needed
            // TO-DO check mcptcompliance flag usage here
            Map<String, KnOPSubsProfileInfoDTO> subscriberProfiles =
                    getSubscriberProfiles(
                            validMdns, bulkSubsProvInfoDTO,
                            persisterTxn);
            //if subscriber profile doesn't exist for any mdn, add to failure list
            for (String mdn : validMdns) {
                if (!subscriberProfiles.containsKey(mdn)) {
                    knLogger.error(methodName, "Subscriber Profile doesn't exist for MDN - ", KnGDPRTemplate.mdn(mdn));
                    KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                    knBulkOpsErrorDetail.setMdn(mdn);
                    knBulkOpsErrorDetail.setErrorCode(KnErrorCodes.DAO.ROW_NOT_FOUND);
                    knBulkOpsErrorDetail.setErrorMessage("Subscriber Profile does not exist");
                    failureMdns.put(mdn, knBulkOpsErrorDetail);
                }
                else{
                    knLogger.debug(methodName, "Subscriber profile for mdn - ", KnGDPRTemplate.mdn(mdn));
                    //validate account id in subscriber profile matches with account id in request DTO
                    String accountIdInProfile = subscriberProfiles.get(mdn).getAccountId().
                            replaceAll("\\s+", "");
                    if(!(accountIdInProfile.equals(bulkSubsProvInfoDTO.getAccountId()))){
                        knLogger.error(methodName, "Subscriber Profile account id doesn't match with account id" +
                                " for MDN - ", KnGDPRTemplate.mdn(mdn));
                        KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                        knBulkOpsErrorDetail.setMdn(mdn);
                        knBulkOpsErrorDetail.setErrorCode(KnBulkOpsErrorCodes.BOEntity.ACCOUNTID_VALIDATION_FAILED);
                        knBulkOpsErrorDetail.setErrorMessage("Subscriber Profile account id doesn't match with" +
                                " account id in request");
                        failureMdns.put(mdn, knBulkOpsErrorDetail);
                    }
                }
            }

            // Create a mutable copy for further processing
            validMdns = new ArrayList<>(validMdns.stream().filter(mdn -> !failureMdns.containsKey(mdn)).toList());
            knLogger.debug(methodName, "Valid MDNs after subscriber profile validation and account id validation" +
                    " going for service auth change- ", KnGDPRTemplate.mdnList(validMdns));
            //change service auth status for valid mdns
            KnChangeAuthStatusBulkResult updateAuthStatusResponseMap = changeServiceAuthStatusForSubscribers(
                    bulkSubsProvInfoDTO,
                    subscriberProfiles,
                    validMdns,
                    persisterTxn);

            // Merge failures from changeServiceAuthStatusForSubscribers
            failureMdns.putAll(updateAuthStatusResponseMap.getFailureMdns());

            Map<String, KnOPChgAuthStatusRespDTO> responseMap = updateAuthStatusResponseMap.getResponses();
            //Notification logic starts here
            List<String> deviceIMPI = new ArrayList<>();
            List<KnXcapDiffNotifyDTO> xcapDiffList = new ArrayList<>();
            List<com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyBulkDTO> KnSubscrEXDMSNotifyBulkDToList = new ArrayList<com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyBulkDTO>();
            boolean xcapMobileSync = bulkOpsNotifyUtil.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync",xcapMobileSync);
            List<com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
            KnNotificationParamDTO knNotificationParamDTO = new KnNotificationParamDTO();
            knNotificationParamDTO.setPriority(KnConstants.NOTIFICATION_PRIORITY.HIGH.value());

            Map<String, KnOPProvDTO> opProvDTOMap = bulkOpsNotifyUtil.actionOnTGSSDocBulk(bulkSubsProvInfoDTO,
                    updateAuthStatusResponseMap,
                    persisterTxn);
            // in the map if DirChgDTO not null then bulk send notification for TGSS doc change
            opProvDTOMap.entrySet().stream().forEach(e->{
                KnOPProvDTO opProvDTO = e.getValue();
                if(opProvDTO.getDirChgDTO()!=null){
                    knLogger.debug(methodName, "TGSS doc changed sending notification");
//                           commonMediator.sendXcapNotification(opProvDTO.getDirChgDTO(),
//                           String.valueOf(subscriberProfiles.get(e.getKey()).getClientPVmajorVer()),
//                           knNotificationParamDTO );
                }
            });
            for(var entry : responseMap.entrySet()){
                KnOPChgAuthStatusRespDTO opChgValue = entry.getValue();

                if(opChgValue.isDeleteDeviceNotify()){
                    deviceIMPI.add(entry.getKey());
                }
                if (xcapMobileSync) {
                    com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyBulkDTO knSubscrEXDMSNotifyBulkDTO = new com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyBulkDTO();
                    //  List contain description of the data: variable name is mdnList as json o/p req
                    knSubscrEXDMSNotifyBulkDTO.setId(CHANGE_SERVICE_AUTH_STATUS.value());
                    knSubscrEXDMSNotifyBulkDTO.setType(CHANGE_SERVICE_AUTH_STATUS.value());
                    knSubscrEXDMSNotifyBulkDTO.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    knSubscrEXDMSNotifyBulkDTO.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());


                    com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDTO = new com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto();
                    knSubscrEXDMSNotifyDTO.setMdnList(validMdns);
                    bulkOpsNotifyUtil.setKnSubscriberExdmsNotifyDto(opChgValue, bulkSubsProvInfoDTO.getSubscriberClientType(), knSubscrEXDMSNotifyDTO);
                    notifyDtoList.add(knSubscrEXDMSNotifyDTO);
                    knLogger.debug(methodName, "knSubscrEXDMSNotifyDTO :", knSubscrEXDMSNotifyDTO.toString());

                    if (opChgValue.getProfileMdnList() != null && !opChgValue.getProfileMdnList().isEmpty()) {
                        KnXDMBulkSubsProvInfoDTO finalBulkSubsProvInfoDTO = bulkSubsProvInfoDTO;
                        opChgValue.getProfileMdnList().stream().forEach(profileMdn -> {
                            com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto profileMdnSubscrEXDMSNotifyDTO = new KnSubscrEXDMSNotifyDto();
                            profileMdnSubscrEXDMSNotifyDTO.setMdn(profileMdn);
                            bulkOpsNotifyUtil.setKnSubscriberExdmsNotifyDto(opChgValue, finalBulkSubsProvInfoDTO.getSubscriberClientType(), profileMdnSubscrEXDMSNotifyDTO);
                            notifyDtoList.add(profileMdnSubscrEXDMSNotifyDTO);
                        });
                    }
                    knSubscrEXDMSNotifyBulkDTO.setMdnList(notifyDtoList);
                    KnSubscrEXDMSNotifyBulkDToList.add(knSubscrEXDMSNotifyBulkDTO);
                    knLogger.debug(methodName, "KnSubscrEXDMSNotifyBulkDToList :", KnSubscrEXDMSNotifyBulkDToList);
                }
                KnConstants.MESSAGE_TYPE action =  KnConstants.MESSAGE_TYPE.NO_ACTION;
                List<KnOPDirChgDTO> dirChgDTOs = opChgValue.getDirChgDTOs();
                Integer serviceAuthStatus = bulkSubsProvInfoDTO.getServiceAuthStatus().value();
                if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()
                        || serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_DEACTIVATED);
                }
                if (dirChgDTOs != null&&!dirChgDTOs.isEmpty()) {
                    for(KnOPDirChgDTO dirChgDTO:dirChgDTOs) {
                        KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
                        if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()
                                || serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                            //KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_DEACTIVATED);
                            if ((dirChgDTO.getProtoVersion().matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX))
                                    && (action.equals(KnConstants.MESSAGE_TYPE.CLIENT_TYPE_CHANGE) || action.equals(KnConstants.MESSAGE_TYPE.USER_CREDENTIAL_CHANGE))) {
                                KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                                if (action.equals(KnConstants.MESSAGE_TYPE.CLIENT_TYPE_CHANGE)) {
                                    profileNotifyDTO.setClientTypeChange(String.valueOf(opChgValue.getSubsClientType()));
                                    xcapDiffNotifyDTO.setReason(KnConstants.REASON.CLIENT_TYPE_CHANGE.value());
                                } else if (action.equals(KnConstants.MESSAGE_TYPE.USER_CREDENTIAL_CHANGE)) {
                                    profileNotifyDTO.setCredentialChange(dirChgDTO.getMdn());
                                    xcapDiffNotifyDTO.setReason(KnConstants.REASON.USER_CREDENTIAL_CHANGE.value());
                                }
                                if (opChgValue.getProfileMdnList() != null && !opChgValue.getProfileMdnList().isEmpty()) {
                                    opChgValue.getProfileMdnList().stream().forEach(profileMdn -> {
                                        if (profileMdn.equalsIgnoreCase(dirChgDTO.getMdn())) {
                                            profileNotifyDTO.setLastProfileUpdateTimestamp(String.valueOf(opChgValue.getEtag()));
                                        }
                                    });
                                }
                                if (opChgValue.getMdn().equalsIgnoreCase(dirChgDTO.getMdn())) {
                                    profileNotifyDTO.setLastProfileUpdateTimestamp(String.valueOf(opChgValue.getEtag()));
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
                            xcapDocList.add(xcapDiffDocDTO);
                        }

                        xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
                        xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
                        xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                        xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                        xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                        xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
                        xcapDiffNotifyDTO.setPocHome(opChgValue.getPocServerHome());
                        xcapDiffNotifyDTO.setPresenceHome(opChgValue.getPresenceServerHome());
                        xcapDiffList.add(xcapDiffNotifyDTO);
                    }
                }
            }
            knLogger.debug(methodName, "Publishing micro service notify - ");
            bulkOpsNotifyUtil.startNotifyMicroServicesJob(KnSubscrEXDMSNotifyBulkDToList);
            List<String> deviceImpiToNotify = deviceIMPI.stream()
                    .map(element -> element + TEL_URI_TEMPLATE)
                    .collect(Collectors.toList());
            bulkOpsNotifyUtil.sendDeleteDeviceNotification(deviceImpiToNotify);
            knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffList);
            boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, knNotificationParamDTO);
            knLogger.debug(methodName, "Notification status - ", isNotified);
            //boolean profileMdnNotifystatus = bulkOpsNotifyUtil.prepareMcxNotifyForProfileMdns(provRespDTO);

            if (ownedTxn) {
                persisterTxn.save();
            }
            if(!failureMdns.isEmpty()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_UPDATE_AUTH_STATUS_FAILURE);
            }
            responseDTO = new KnXDMBulkOpsRespDTO();
            responseDTO.setFailureMdnCount(failureMdns.size());
            responseDTO.setSuccessMdns(getSuccessMdns(updateAuthStatusResponseMap));
            responseDTO.setSuccessMdnCount((updateAuthStatusResponseMap.getResponses().size()));
            responseDTO.setFailureMdns(failureMdns.values().stream().toList());
            responseDTO.setTransactionId(requestDTO.getTransactionId());
            responseDTO.setProcessingTimeMs(System.currentTimeMillis()-startTime);
            responseDTO.setTotalCount(mdns.size());
            responseDTO.setOIDCApplicable(Boolean.TRUE);

            // Set corpId from any existing subscriber profile
            for (KnOPSubsProfileInfoDTO profile : subscriberProfiles.values()) {
                if (profile != null) {
                    responseDTO.setCorpId(profile.getCorpId());
                    break;
                }
            }

            knBulkOpsInfoUtil.finalizePartialResponse(responseDTO, mdns.size(), KnBulkOpsConstants.BulkOperations.UPDATEBULKAUTHSTATUS);

            //notification implementation can be added here?
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_UPDATE_AUTH_STATUS_FAILURE);
            return knBulkOpsInfoUtil.buildFailureResponse(bulkSubsProvInfoDTO, e, startTime);
        }
        knLogger.debug(methodName, "response dtop", responseDTO.toString());

        return responseDTO;
    }

    private List<KnBulkSubscriberEntry> getSuccessMdns(KnChangeAuthStatusBulkResult updateAuthStatusResponseMap) {
        List<KnBulkSubscriberEntry> successMdns = updateAuthStatusResponseMap.getResponses().entrySet().stream().map(e -> {
            KnBulkSubscriberEntry entry = new KnBulkSubscriberEntry();
            entry.setMdn(e.getKey());
            // dispatch type sourced from subscriber client type
            entry.setDispatchType(e.getValue().getDispatchType());
            // auth status set earlier on the response DTO
            entry.setServiceAuthStatus(e.getValue().getAuthStatus());
            return entry;
        }).toList();
        return successMdns;
    }

    private KnChangeAuthStatusBulkResult changeServiceAuthStatusForSubscribers(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO, Map<String, KnOPSubsProfileInfoDTO> subscriberProfiles,
            List<String> validMdns, KnPersisterTxn persisterTxn) throws KnDAOException, KnBulkOpsException, KnBOException, KnProvBOException {
        String methodName = "changeServiceAuthStatusForSubscribers";
        List<KnSubsProfilePersistDTO> updateProfileList = new ArrayList<>();
        List<KnCorpProfilePersistDTO> updateCorpProfileList = new ArrayList<>();
        List<KnDeviceInfoPersistDTO> updateDeviceInfoList = new ArrayList<>();
        Map<String, KnOPChgAuthStatusRespDTO> updateRespMap = new HashMap<>();
        Map<String, KnBulkOpsErrorDetail> failureMdns = new HashMap<>();


        //service auth status to be set
        Integer inputSvcAuthStatus = bulkSubsProvInfoDTO.getServiceAuthStatus().value();
        boolean isCompensation = bulkSubsProvInfoDTO.isCompensationRequest();
        Map<String, KnOPSubsProfileInfoDTO> subsProfileMap = getSubscriberProfiles(validMdns, bulkSubsProvInfoDTO, persisterTxn);
        Map<String, KnOPChgAuthStatusRespDTO> responseMap = new HashMap<>();
        Map<String, String> mdntoXcapRootUriMap = new HashMap<>();
        knLogger.debug(methodName, "Batch retrieved profiles for", subsProfileMap.size(), "subscribers");
        long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();

        // Collect MDNs that need XCAP Root URI (non-NNI clients)
        List<String> mdnsNeedingXcapUri = new ArrayList<>();
        for (Map.Entry<String, KnOPSubsProfileInfoDTO> entry : subsProfileMap.entrySet()) {
            String mdn = entry.getKey();
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = entry.getValue();
            if (subsProfileInfoDTO.getSubsClientType() != KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() &&
                    subsProfileInfoDTO.getSubsClientType() != KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                mdnsNeedingXcapUri.add(mdn);
            }
        }

        // Get XCAP Root URIs for the filtered MDN list
        if (!mdnsNeedingXcapUri.isEmpty()) {
            mdntoXcapRootUriMap = knBulkOpsInfoUtil.getXCAPRootURI(mdnsNeedingXcapUri, persisterTxn);
        }
        // Stream through the mdntoXcapRootUriMap and if the mdn is not present remove it from validMdns and set failure map
        //check if mdns needing xcap uri is not size 0 or empty

        for (String mdn : mdnsNeedingXcapUri) {
            if (!mdntoXcapRootUriMap.containsKey(mdn)) {
                knLogger.error(methodName, "XCAP Root URI not found for MDN - ", KnGDPRTemplate.mdn(mdn));
                KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                knBulkOpsErrorDetail.setMdn(mdn);
                knBulkOpsErrorDetail.setErrorCode(KnErrorCodes.DAO.ROW_NOT_FOUND);
                knBulkOpsErrorDetail.setErrorMessage("XCAP Root URI not found");
                failureMdns.put(mdn, knBulkOpsErrorDetail);
                knLogger.debug(methodName, "validMdns " + validMdns);
                validMdns.remove(mdn);
            }
        }


        for (String mdn : validMdns) {
            KnOPSubsProfileInfoDTO subProfileInfo = subsProfileMap.get(mdn);
            Integer svcStatusAuthUser = subProfileInfo.getServiceStatusAuthUser();
            Integer serviceAuthStatus = subProfileInfo.getServiceAuthStatus();


            //check if the mdn is a pseudo mdn then restrict the operation
            if (subProfileInfo.getPamAccId() != null && subProfileInfo.getPamAccId() != 0) {
                if ((bulkSubsProvInfoDTO.getClientType() != com.kodiak.common.resources.KnConstants.CLIENT_TYPE_CAT_UI)
                        && (bulkSubsProvInfoDTO.getClientType() != com.kodiak.common.resources.KnConstants.CLIENT_TYPE_REST)) {

                    knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed ", KnGDPRTemplate.mdn(mdn));
                    KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                    knBulkOpsErrorDetail.setMdn(mdn);
                    knBulkOpsErrorDetail.setErrorCode(KnBulkOpsErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO);
                    knBulkOpsErrorDetail.setErrorMessage("Mdn is present as a pseudo mdn in POCSUBSCRINFO");
                    failureMdns.put(mdn,knBulkOpsErrorDetail);
                    continue;
                }
            }

            long etag = bulkSubsProvInfoDTO.getIfMatch();
            // Get current etag
            long currentEtag = subProfileInfo.getLastProfileUpdateTime();
            // Skip etag validation for compensation requests since the original operation
            // already updated lastProfileUpdateTime, and the compensation payload carries
            // the old etag from the original request
            if (!isCompensation && etag > 0 && currentEtag != etag) {
                knLogger.error(methodName, "Etag mismatch", etag, " for mdn ", KnGDPRTemplate.mdn(mdn), " current etag ", currentEtag);
                KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                knBulkOpsErrorDetail.setMdn(mdn);
                knBulkOpsErrorDetail.setErrorCode(KnBulkOpsErrorCodes.BOEntity.MISMATCH_IN_SUBS_DOC_ETAG);
                knBulkOpsErrorDetail.setErrorMessage("Etag mismatch");
                failureMdns.put(mdn, knBulkOpsErrorDetail);
                continue;
            }
            //skipped the validation framework call to validate KnSubsProfilePersistDTO
            String newClientPassword = subProfileInfo.getClientPassword();
            String newUserAgent = subProfileInfo.getUserAgent();

            // Determine the target service auth status based on compensation flag
            int targetSvcAuthStatus;
            if (isCompensation) {
                // Saga compensation: revert to previous service auth status stored in DB
                // Note: previousServiceAuthStatus can be 0 (PROVISIONED) which is a valid state
                if (subProfileInfo.getPreviousServiceAuthStatus() != null) {
                    targetSvcAuthStatus = subProfileInfo.getPreviousServiceAuthStatus();
                    knLogger.info(methodName, "Compensation request: reverting MDN ", KnGDPRTemplate.mdn(mdn),
                            " from current auth status ", serviceAuthStatus,
                            " to previous auth status ", targetSvcAuthStatus);
                } else {
                    knLogger.error(methodName, "Compensation request but no previous auth status found for MDN ", KnGDPRTemplate.mdn(mdn));
                    KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                    knBulkOpsErrorDetail.setMdn(mdn);
                    knBulkOpsErrorDetail.setErrorCode(KnBulkOpsErrorCodes.BOEntity.MISMATCH_IN_SUBS_DOC_ETAG);
                    knBulkOpsErrorDetail.setErrorMessage("No previous service auth status available for compensation");
                    failureMdns.put(mdn, knBulkOpsErrorDetail);
                    continue;
                }
            } else {
                targetSvcAuthStatus = inputSvcAuthStatus;
            }

            if (targetSvcAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                newUserAgent = null;
                newClientPassword = null;
            }

            //populate persist DTO for the DB updates.
            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            if (isCompensation) {
                // For compensation, set the target status directly without recalculation
                subsProfilePersistDTO.setServiceAuthStatus(targetSvcAuthStatus);
                subsProfilePersistDTO.setServiceStatusOp(targetSvcAuthStatus);
            } else {
                subsProfilePersistDTO.setServiceAuthStatus(KnGenInfoUtil.calculateServiceAuthStatus(targetSvcAuthStatus, svcStatusAuthUser));
                subsProfilePersistDTO.setServiceStatusOp(targetSvcAuthStatus);
            }
            subsProfilePersistDTO.setClientPassword(newClientPassword);
            subsProfilePersistDTO.setUserAgent(newUserAgent);
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);

            if (subProfileInfo.getCorpId() > 0) {
                long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                KnCorpProfilePersistDTO knCorpProfilePersistDTO = new KnCorpProfilePersistDTO(
                );
                knCorpProfilePersistDTO.setCorpId(subProfileInfo.getCorpId());
                knCorpProfilePersistDTO.setLastProfileUpdateTime(corpProfileUpdateTime);
                updateCorpProfileList.add(knCorpProfilePersistDTO);
                //call bulk update corp profile last update time after processing all subscribers
                knLogger.debug(methodName, "corp profile to be updated for corpId ", subProfileInfo.getCorpId());
            }
            KnSubsProfilePersistDTO responseSubsProfilePersistDTO = new KnSubsProfilePersistDTO();
            boolean isSubsrUpdateRequired = true;
            if (subsProfilePersistDTO.getServiceAuthStatus() == serviceAuthStatus) {
                isSubsrUpdateRequired = false;
                responseSubsProfilePersistDTO = subsProfilePersistDTO; // to be removed later
                knLogger.info(methodName, "Service Auth Status is same as existing, so no update required for mdn ", KnGDPRTemplate.mdn(mdn));
                KnOPChgAuthStatusRespDTO responseDTO = new KnOPChgAuthStatusRespDTO();
                KnOPSubsProfileInfoDTO subsProfileInfoDTO = subsProfileMap.get(mdn);
                responseDTO.setMdn(subsProfileInfoDTO.getMdn());
                responseDTO.setPocServerHome(subsProfileInfoDTO.getPoCHome());
                responseDTO.setPresenceServerHome(subsProfileInfoDTO.getPresenceHome());
                responseDTO.setResponseMessage(KnBulkOpsConstants.UPDATE_AUTH_STATUS_SUCCESS);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                responseDTO.setEtag(lastProfileUpdateTime);
                responseDTO.setSubsClientType(subsProfileInfoDTO.getSubsClientType());
                responseDTO.setDispatchType(subsProfileInfoDTO.getDispatchType());
                updateRespMap.put(mdn, responseDTO);
            } else {
                knLogger.info(methodName, "Updating service auth status ");
                if (isCompensation) {
                    // Saga compensation: do NOT update previousServiceAuthStatus - keep it as-is
                    // This preserves idempotency so the compensation can be retried safely
                    knLogger.info(methodName, "Compensation: preserving previousServiceAuthStatus as-is for mdn ", KnGDPRTemplate.mdn(mdn));
                    subsProfilePersistDTO.setPreviousServiceAuthStatusToStore(
                            subProfileInfo.getPreviousServiceAuthStatus() != null
                                    ? subProfileInfo.getPreviousServiceAuthStatus() : 0);
                } else {
                    // Always store current serviceAuthStatus as previous before changing
                    // This is needed so saga compensation can revert to the correct state
                    subsProfilePersistDTO.setPreviousServiceAuthStatusToStore(serviceAuthStatus);

                    int prevServiceAuthStatusFromDB;
                    if (subProfileInfo.getPreviousServiceAuthStatus() != null) {
                        prevServiceAuthStatusFromDB = subProfileInfo.getPreviousServiceAuthStatus();
                    } else {
                        prevServiceAuthStatusFromDB = subsProfilePersistDTO.getServiceAuthStatus();
                    }

                    if (subsProfilePersistDTO.getServiceAuthStatus() == 2) {
                        subsProfilePersistDTO.setServiceAuthStatus(prevServiceAuthStatusFromDB);
                        subsProfilePersistDTO.setServiceStatusOp(prevServiceAuthStatusFromDB);
                    }
                }
            }

            if (isSubsrUpdateRequired) {
                updateProfileList.add(subsProfilePersistDTO);
            }

            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subProfileInfo.getSubsFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.MCDEVICE.value());

            if (((KnConstants.MCSCOMPLIANCE.equals(subsProfileMap.get(mdn).getMcpttCompliance())) || bitEnabled) && subsProfilePersistDTO.getServiceAuthStatus() == 2) {
                KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                deviceInfoPersistDTO.setDeviceStatus(KnBulkOpsConstants.DEVICE_STATUS_OP.ACTIVATED.value());
                updateDeviceInfoList.add(deviceInfoPersistDTO);

            } else if (((KnConstants.MCSCOMPLIANCE.equals(subsProfileMap.get(mdn).getMcpttCompliance())) || bitEnabled) && subsProfilePersistDTO.getServiceAuthStatus() == 3) {
                KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                deviceInfoPersistDTO.setDeviceStatus(KnBulkOpsConstants.DEVICE_STATUS_OP.DEACTIVATED.value());
                deviceInfoPersistDTO.setDeviceDigestPassword(null);
                KnOPChgAuthStatusRespDTO responseDTO = new KnOPChgAuthStatusRespDTO();
                responseDTO.setDeleteDeviceNotify(true);
                updateDeviceInfoList.add(deviceInfoPersistDTO);
                responseMap.put(mdn, responseDTO);
            }
        }

        //do bulk corp update
        if (!updateCorpProfileList.isEmpty()) {
            knLogger.debug(methodName, "Bulk updating corp profiles for ", updateCorpProfileList.size(), " corporations");
            KnCorpInfoDAO knCorpInfoDAO = new KnCorpInfoDAO();
            knCorpInfoDAO.bulkUpdateLastProfileTime(updateCorpProfileList, persisterTxn);
        }

        //stream through the updateProfileList and call getMdnForUPM for each mdn and
        // set the mdnList in subsProfilePersistDTO
        //do a bulk read
        List<String> mdnsTobeUpdated = updateProfileList.stream()
                .map(KnSubsProfilePersistDTO::getMdn)
                .toList();

        // call getProfileMdnNupmfsByBaseMdn for each MDN
        // This method returns Map<String, KnOPSubsProfileInfoDTO> with UPM FS data
        // and only includes MDNs where userProfileIndex is 0 or null (real MDNs, not profile MDNs)
        KnPOCSubscrInfoDAO knPOCSubscrInfoDAO = new KnPOCSubscrInfoDAO();
        Map<String, List<String>> profileMdnsMap = new HashMap<>();
        
        try {
            // Get xdmPttServerId and create DAO
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            
            for (String baseMdn : mdnsTobeUpdated) {
                try {
                    // getProfileMdnNupmfsByBaseMdn returns all profile MDNs for a base MDN
                    // This includes UPM logic and userProfileIndex check
                    Map<String, com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO> mdnUpmFsMap = provXdmServerDAO.getProfileMdnNupmfsByBaseMdn(baseMdn, false, persisterTxn);
                    if (mdnUpmFsMap != null && !mdnUpmFsMap.isEmpty()) {
                        List<String> mdnList = new ArrayList<>(mdnUpmFsMap.keySet());
                        profileMdnsMap.put(baseMdn, mdnList);
                        knLogger.debug(methodName, "Retrieved ", mdnList.size(), " profile MDNs for base MDN ", KnGDPRTemplate.mdn(baseMdn));
                    } else {
                        // If no profile MDNs found, use just the base MDN
                        profileMdnsMap.put(baseMdn, Arrays.asList(baseMdn));
                    }
                } catch (Exception e) {
                    knLogger.error(methodName, "Error fetching profile MDNs for base MDN ", KnGDPRTemplate.mdn(baseMdn), e);
                    // If error, set just the base MDN itself
                    profileMdnsMap.put(baseMdn, Arrays.asList(baseMdn));
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Error initializing DAO for UPM logic, falling back to base MDNs only", e);
            // If DAO initialization fails, fall back to using just the base MDNs
            for (String baseMdn : mdnsTobeUpdated) {
                profileMdnsMap.put(baseMdn, Arrays.asList(baseMdn));
            }
        }

        for (KnSubsProfilePersistDTO subsProfilePersistDTO : updateProfileList) {
            List<String> allMdnList = profileMdnsMap.get(subsProfilePersistDTO.getMdn());
            if (allMdnList == null || allMdnList.isEmpty()) {
                // If no profile MDNs found, use just the base MDN
                allMdnList = Arrays.asList(subsProfilePersistDTO.getMdn());
            }
            subsProfilePersistDTO.setMdnList(allMdnList);
            knLogger.debug(methodName, "Set mdnList for ", subsProfilePersistDTO.getMdn(), " with ", allMdnList.size(), " MDNs");
        }

        //call bulk update method
        if (!updateProfileList.isEmpty()) {
            knLogger.debug(methodName, "Bulk updating subscriber profiles for ", updateProfileList.size(), " subscribers");
            KnBulkUpdateStatusDTO knBulkUpdateStatusDTO = knPOCSubscrInfoDAO.bulkUpdateServiceAuthStatusForUPM(updateProfileList,
                    persisterTxn);
            failureMdns.putAll(knBulkUpdateStatusDTO.getExceptions());
        }

        KnBulkOpsXDMServerDAO knBulkOpsXDMServerDAO = new KnBulkOpsXDMServerDAO();
        //do bulk device info update
        if (!updateDeviceInfoList.isEmpty()) {
            knLogger.debug(methodName, "Bulk updating device info for ", updateDeviceInfoList.size(), " devices");
            knBulkOpsXDMServerDAO.updateDeviceInfoStatusAndPasswordBulk(updateDeviceInfoList, persisterTxn);
        }

        //get etag map from dao call
        KnXDMDirectoryDAO knXDMDirectoryDAO = new KnXDMDirectoryDAO();
        Map<String, Integer> mdnToEtagMap = knXDMDirectoryDAO.getCurrentEtagsForDirDoc(mdnsTobeUpdated, persisterTxn);

        //update etag in directory doc for mdns
        knXDMDirectoryDAO.updateEtagForDirDocOfMdnList(mdnsTobeUpdated, persisterTxn);

        List<KnOPDirChgDTO> dirChangeDtos = new ArrayList<>();
        Map<String, List<String>> profileMdnEtagMap = new HashMap<>();
        // set in response map for successful mdns, get from the responseMap if already present
        for (String mdn : mdnsTobeUpdated) {
            KnOPChgAuthStatusRespDTO responseDTO = responseMap.get(mdn);
            if (responseDTO == null) {
                responseDTO = new KnOPChgAuthStatusRespDTO();
            }
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = subsProfileMap.get(mdn);
            responseDTO.setMdn(subsProfileInfoDTO.getMdn());
            responseDTO.setPocServerHome(subsProfileInfoDTO.getPoCHome());
            responseDTO.setPresenceServerHome(subsProfileInfoDTO.getPresenceHome());
            responseDTO.setResponseMessage(KnBulkOpsConstants.UPDATE_AUTH_STATUS_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setSubsClientType(subsProfileInfoDTO.getSubsClientType());
            responseDTO.setDispatchType(subsProfileInfoDTO.getDispatchType());

            List<String> allMdnList = profileMdnsMap.get(mdn);
            List<String> profileMdnList = new ArrayList<>(allMdnList);
            profileMdnList.remove(mdn);


            if (subsProfileInfoDTO.getSubsClientType() != KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() &&
                    subsProfileInfoDTO.getSubsClientType() != KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                for (String mdns : allMdnList) {
                    KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                    docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    String subsConfigDocUri = knBulkOpsInfoUtil.generateSubsConfigSelUri(mdns);
                    docChgDTO.setDocUri(subsConfigDocUri);
                    docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
                    chgDocList.add(docChgDTO);

                    //populating the XDM Directory DTO
                    KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                    dirChgDTO.setXcapRootURI(mdntoXcapRootUriMap.get(mdn));
                    dirChgDTO.setPocHome(subsProfileInfoDTO.getPoCHome());
                    dirChgDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
                    dirChgDTO.setDocChgDTO(chgDocList);
                    String dirDocUri = genInfoUtil.generateDirDocUri(mdns);
                    dirChgDTO.setDirUri(dirDocUri);
                    int previousEtag = mdnToEtagMap.getOrDefault(mdn, 0);
                    dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                    int newEtag = previousEtag + 1;
                    dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                    dirChgDTO.setProtoVersion(String.valueOf(subsProfileInfoDTO.getClientPVmajorVer()));
                    dirChgDTO.setClientType(subsProfileInfoDTO.getSubsClientType());
                    dirChgDTO.setMdn(mdns);
                    dirChangeDtos.add(dirChgDTO);
                }

                //populating the Dir chg DTO to response
                responseDTO.setDirChgDTOs(dirChangeDtos);
                // code for new RestApi call
                // setting the activeFS ,mdn ,issyncdisabled
                responseDTO.setMdn(subsProfileInfoDTO.getMdn());
                responseDTO.setActiveFs2(subsProfileInfoDTO.getActiveFS2());
                responseDTO.setCorpid(subsProfileInfoDTO.getCorpId());
                responseDTO.setAuthStatus(updateProfileList.stream().filter(dto -> dto.getMdn().equals(mdn)).findFirst().get().getServiceAuthStatus());
                responseDTO.setSyncDisabled(true);
                responseDTO.setProfileMdnList(profileMdnList);
                responseDTO.setSubsFS2(subsProfileInfoDTO.getSubsFS2());
                responseDTO.setMcId(subsProfileInfoDTO.getMcId());
                responseDTO.setMcDataId(subsProfileInfoDTO.getMcDataId());
                responseDTO.setMcPttId(subsProfileInfoDTO.getMcpttId());
                responseDTO.setMcVideoId(subsProfileInfoDTO.getMcVideoId());
                responseDTO.setNetworkName(subsProfileInfoDTO.getNetworkName());
                responseDTO.setMdnList(allMdnList);

                if (updateProfileList.stream().filter(dto ->
                        dto.getMdn().equals(mdn)).findFirst().get().getServiceAuthStatus() ==
                        KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()) {
                    responseDTO.setSyncDisabled(false);
                }
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_ACTIVATED);
                knLogger.debug(methodName, "Response DTO - ", responseDTO.getActiveFs2(), " subsProfileInfoDTO: ", subsProfileInfoDTO);
                knLogger.debug(methodName, "directoryChange DTO - ", dirChangeDtos);

            }
            //collect all profile mdns etag updates into a map and move this logic outside this dao layer
            profileMdnEtagMap.put(mdn, profileMdnList);


            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            knLogger.info(methodName, "EXIT: change service auth status of Subscriber operation ");
            updateRespMap.put(mdn, responseDTO);
        }

        // Call profileMdnEtagUpdate once outside the loop with accumulated mappings
        Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdateResult = knPOCSubscrInfoDAO.profileMdnEtagUpdate(profileMdnEtagMap, persisterTxn);

        // Set the result in each response DTO
        for (String mdn : mdnsTobeUpdated) {
            KnOPChgAuthStatusRespDTO responseDTO = updateRespMap.get(mdn);
            responseDTO.setProfileMdnEtagMap(profileMdnEtagUpdateResult);
            if (profileMdnEtagUpdateResult != null && !profileMdnEtagUpdateResult.isEmpty()) {
                List<String> profileMdnList = profileMdnEtagMap.get(mdn);
                if (profileMdnList != null) {
                    responseDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(profileMdnList, persisterTxn, true));
                }
            }
        }

        // Build and return aggregated result
        return new KnChangeAuthStatusBulkResult(updateRespMap, failureMdns,subscriberProfiles);
    }


    private Map<String, KnOPSubsProfileInfoDTO> getSubscriberProfiles(List<String> mdns,
                                                                      KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                                      KnPersisterTxn persisterTxn) throws KnBulkOpsException, KnDAOException {
        KnPOCSubscrInfoDAO knPOCSubscrInfoDAO = new KnPOCSubscrInfoDAO();
        return knPOCSubscrInfoDAO.selectSubscriberProfiles(mdns, persisterTxn);
    }


    /**
     * Validates the incoming request DTO and extracts KnXDMBulkSubsProvInfoDTO.
     * If invalid, populates responseDTO with error details and returns null.
     */
    private KnXDMBulkSubsProvInfoDTO validateAndExtractBulkSubsProvInfoDTO(IXDMRequestDTO requestDTO,
                                                                           IXDMResponseDTO responseDTO,
                                                                           KnPersisterTxn persisterTxn)
            throws KnPersistenceException {
        String methodName = "validateAndExtractBulkSubsProvInfoDTO(IXDMRequestDTO, KnXDMBulkOpsRespDTO)";

        if (requestDTO instanceof KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO) {
            knLogger.debug(methodName, "received DTO for update Bulk AuthStatus - ", bulkSubsProvInfoDTO);
            return bulkSubsProvInfoDTO;
        } else {
            knLogger.error(methodName, "received an Invalid DTO for update Bulk AuthStatus op - ", requestDTO);
            responseDTO.setResponseMessage("Invalid DTO is passed");
            responseDTO.setResponseCode(KnBulkOpsErrorCodes.BOEntity.ERROR_CODE_INVALID_DTO_PASSED);
            responseDTO.setResponseStatus(KnBulkOpsConstants.RESPONSE_STATUS.FAILURE.value());
            // TODO: Increment Stats for failure
            return null;
        }
    }


    public Map<String, KnBulkOpsErrorDetail> validateSubAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnBulkOpsException, KnDAOException {
        String methodName = "validateSubAuthStatus";
        Map<String, KnBulkOpsErrorDetail> invalidAuthStatusMdns = new HashMap<>();

        if (mdns != null && !mdns.isEmpty() && mdns.stream().anyMatch(Objects::nonNull)) {
            knLogger.debug(methodName, "Validating MDNs service auth status - ", KnGDPRTemplate.mdnList(mdns));

            // Fetch the map containing MDN and service auth status
            KnPOCSubscrInfoDAO knPOCSubscrInfoDAO = new KnPOCSubscrInfoDAO();
            Map<String, Integer> subscriberServiceAuthStatusMap = knPOCSubscrInfoDAO.getSubscriberServiceAuthStatus(mdns, persisterTxn);

            for (Map.Entry<String, Integer> entry : subscriberServiceAuthStatusMap.entrySet()) {
                String currentMdn = entry.getKey().replaceAll("\\s+", "");;
                Integer subscriberServiceAuthStatus = entry.getValue();

                // Validate the service auth status for each MDN
                if (subscriberServiceAuthStatus != null
                        && SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value() == subscriberServiceAuthStatus) {
                    knLogger.error(methodName, "Inactive subscriber deletion in-progress for MDN=", KnGDPRTemplate.mdn(currentMdn), " ServiceAuthStatus: ", subscriberServiceAuthStatus);
                    KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                    knBulkOpsErrorDetail.setMdn(currentMdn);
                    knBulkOpsErrorDetail.setErrorCode(INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS);
                    knBulkOpsErrorDetail.setErrorMessage("Inactive subscriber deletion in-progress for MDN: " + currentMdn);
                    invalidAuthStatusMdns.put(currentMdn, knBulkOpsErrorDetail);
                }

                if (subscriberServiceAuthStatus != null
                        && SERVICE_AUTH_STATUS.PRE_PROVISIONED.value() == subscriberServiceAuthStatus) {
                    knLogger.error(methodName, "Subscriber in pre-provisioned state for MDN=", KnGDPRTemplate.mdn(currentMdn), " ServiceAuthStatus: ", subscriberServiceAuthStatus);
                    KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                    knBulkOpsErrorDetail.setMdn(currentMdn);
                    knBulkOpsErrorDetail.setErrorCode(SUBSCRIBER_IN_PRE_PROVISIONED_STATE);
                    knBulkOpsErrorDetail.setErrorMessage("Suspend/Resume operation is not allowed for a subscriber in Pre-Provisioned state.");
                    invalidAuthStatusMdns.put(currentMdn, knBulkOpsErrorDetail);
                }
            }
        }
        knLogger.info(methodName, "Mdns marked for async delete / preprovisioned state", invalidAuthStatusMdns.size());
        return invalidAuthStatusMdns;
    }

    public Map<String, KnBulkOpsErrorDetail> validateHierarchy(List<String> mdns, com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE hierarchyType)
            throws KnDAOException {
        String methodName = "validateHierarchy";
        Map<String, KnBulkOpsErrorDetail> failedHierarchyMdns = new HashMap<>();
        // Validate MDN hierarchy
        for (String mdn : mdns) {
            if (!KnGeneralProfileUtil.validateMDNCCAndHierarchy(mdn, hierarchyType)) {
                knLogger.error(methodName, "Invalid MDN hierarchy for MDN: ", KnGDPRTemplate.mdn(mdn), " HierarchyType: ", hierarchyType);
                KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                knBulkOpsErrorDetail.setMdn(mdn);
                knBulkOpsErrorDetail.setErrorCode(INVALID_HIERARCHY_REQUEST);
                knBulkOpsErrorDetail.setErrorMessage("Invalid MDN hierarchy passed");
                failedHierarchyMdns.put(mdn, knBulkOpsErrorDetail);
            }
        }

        knLogger.debug(methodName, "Hierarchy validation failed for ", failedHierarchyMdns.size(), " MDNs");
        return failedHierarchyMdns;
    }

    private List<String> extractMdnList(List<KnBulkSubscriberEntry> subscriberList) {
        List<String> mdnList = new ArrayList<>(subscriberList.size());
        for (KnBulkSubscriberEntry entry : subscriberList) {
            if (entry != null && entry.getMdn() != null && !entry.getMdn().trim().isEmpty()) {
                mdnList.add(entry.getMdn().trim());
            }
        }
        return mdnList;
    }

    /**
     * Opens the transaction if null and returns the (possibly new) transaction.
     */
    private KnPersisterTxn ensureTransactionOpen(KnPersisterTxn txn, String methodName) throws KnPersistenceException {
        if (txn == null) {
            txn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening transaction");
            txn.open();

        }
        return txn;
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
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }

}

//To do next
//fix dao and changeserviceauth method
//refactor later
