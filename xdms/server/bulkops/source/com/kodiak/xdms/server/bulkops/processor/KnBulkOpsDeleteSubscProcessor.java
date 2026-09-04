package com.kodiak.xdms.server.bulkops.processor;

import com.kodiak.common.commdto.common.KnBulkSubscriberEntry;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMBulkOpsRespDTO;
import com.kodiak.common.commdto.response.KnXDMProfileIdMdnMapRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dto.*;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnDeRegisterNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsCorpProfileDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsSubsProfileDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsXDMServerDAO;
import com.kodiak.common.commdto.common.KnBulkSubsProfileDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnMDNValidationResult;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsInfoUtil;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnDeleteDeviceDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPDeleteSubsRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyDto;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnTPUserPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsNotifyUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kodiak.common.dao.KnDbUtil.rollback;
import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_SUBSCR;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH;
import static com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.LMR_INTEROP_NON_CAPABLE;

public class KnBulkOpsDeleteSubscProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsDeleteSubscProcessor.class);
    private static KnBulkOpsDeleteSubscProcessor instance = null;

    IProvClientIntf provClientIntf = null;
    KnBulkOpsInfoUtil bulkOpsInfoUtil = KnBulkOpsInfoUtil.getInstance();
    KnBulkOpsNotifyUtil bulkOpsNotifyUtil = KnBulkOpsNotifyUtil.getInstance();
    KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    KnCorpCommonInfoUtil corpcommonInfoUtil = new KnCorpCommonInfoUtil();
    KnCorpGroupInfoUtil groupInfoUtil = new KnCorpGroupInfoUtil();
    KnCorpContactInfoUtil contactInfoUtil = new KnCorpContactInfoUtil();
    KnCorpUserProfileUtil corpUserProfileUtil = new KnCorpUserProfileUtil();
    KnCorpSublistInfoUtil sublistInfoUtil = new KnCorpSublistInfoUtil();
    KnCorpSubsProvInfoUtil corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
    KnCorpActivationInfoUtil activationInfoUtil = new KnCorpActivationInfoUtil();
    KnProvInfoUtil provInfoUtil = new KnProvInfoUtil();
    ICorpClientIntf corpClientIntf = new KnCorpClientImpl();
    IXcapDiffNotifierIntf notifier = new KnXcapDiffNotifierImpl();
    public static final String TEL_URI_TEMPLATE = "tel:+";
    /**
     * Immutable lookup of subscriber-client-type -> OM peg id (deletion counter).
     * Built once at class-load. Reads are lock-free and safe for concurrent use because
     * the underlying map is wrapped with {@link Collections#unmodifiableMap(Map)} and
     * never mutated after construction.
     */
    private static final Map<Integer, Integer> CLIENT_TYPE_TO_DELETE_PEG_ID;
    static {
        Map<Integer, Integer> m = new HashMap<>();
        m.put(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.DESKTOP.value(), KnOMConstants.XDM_NUM_DESKTOP_CLIENTS_DELETED);
        m.put(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value(), KnOMConstants.XDM_NUM_DISPATCH_CLIENTS_DELETED);
        m.put(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value(), KnOMConstants.XDM_NUM_LMR_INTEROP_CLIENTS_DELETED);
        m.put(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value(), KnOMConstants.XDM_NUM_3RD_PARTY_POC_CLIENTS_DELETED);
        m.put(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value(), KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_DELETED);
        m.put(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value(), KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value(), KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value(), KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value(), KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value(), KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value(), KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value(), KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value(), KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED);
        m.put(KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value(), KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED);
        CLIENT_TYPE_TO_DELETE_PEG_ID = Collections.unmodifiableMap(m);
    }
    KnCorpGroupProfileUtil groupProfilUtil = new KnCorpGroupProfileUtil();
    KnCorpUserProfileUtil userProfileUtil = new KnCorpUserProfileUtil();
    IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
    public static synchronized KnBulkOpsDeleteSubscProcessor getInstance() throws KnDAOException, KnProvBOException {
        if (instance == null) {
            instance = new KnBulkOpsDeleteSubscProcessor();
        }
        return instance;
    }

    private KnBulkOpsDeleteSubscProcessor() throws KnDAOException, KnProvBOException {
        provClientIntf = KnProvClientImpl.getInstance();
    }

    public IXDMResponseDTO deleteBulkSubscriber(IXDMRequestDTO requestDTO) throws Exception {
        String methodName = "deleteBulkSubscriber(IXDMRequestDTO)";
        long startTime = System.currentTimeMillis();

        knLogger.info(methodName, "ENTRY: Received Request DTO - ", requestDTO, " at ", new Date(startTime));
        boolean ownedTxn = false;
        KnPersisterTxn persisterTxn = null;
        KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO = null;
        KnXDMBulkOpsRespDTO responseDTO = new KnXDMBulkOpsRespDTO();
        try{
            // Validate and prepare request DTO
            if (requestDTO instanceof KnXDMBulkSubsProvInfoDTO) {
                bulkSubsProvInfoDTO = (KnXDMBulkSubsProvInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO for delete Bulk Subscriber - ", bulkSubsProvInfoDTO);
                bulkOpsInfoUtil.sortSubscriberListByMdn(bulkSubsProvInfoDTO);
            } else {
                knLogger.error(methodName, "received and Invalid DTO for delete Bulk subscriber op - ", requestDTO);
                responseDTO = new KnXDMBulkOpsRespDTO();
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnBulkOpsErrorCodes.BOEntity.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnBulkOpsConstants.RESPONSE_STATUS.FAILURE.value());
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_DELETION_FAILURE);
                return responseDTO;
            }

            int initialTotalMDNCount = bulkSubsProvInfoDTO.getSubscriberList().size();

            // Initialize DAOs and response
            KnXDMBulkOpsRespDTO partialRespDTO = bulkOpsInfoUtil.buildPartialResponse(bulkSubsProvInfoDTO);
            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO = new KnBulkOpsXDMServerDAO();
            KnBulkOpsCorpProfileDAO bulkOpsCorpProfileDAO = new KnBulkOpsCorpProfileDAO();
            KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO = new KnBulkOpsSubsProfileDAO();
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();

            com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE hierarchyType = KnGeneralUtil
                    .getHierarchyInterfaceType(com.kodiak.common.resources.KnConstants.SOFTWARE_PKG.POC_SOAP_CSR);
            knLogger.debug(methodName, "hierarchyInterfaceType for POC_SOAP_CSR ", hierarchyType);
            bulkSubsProvInfoDTO.setHierarchyType(hierarchyType);

            Map<String, KnBulkSubsProfileDTO> existingProfilesMap = bulkOpsXDMServerDAO.fetchSubscriberProfilesInBulk(
                    bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
                    persisterTxn);

            int corpId = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()).stream()
                    .map(existingProfilesMap::get)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(profile -> {
                        int id = profile.getCorpId();
                        partialRespDTO.setCorpId(id);
                        return id;
                    })
                    .orElse(0);

            validateDeleteBulkSubscriber(bulkSubsProvInfoDTO, partialRespDTO, bulkOpsXDMServerDAO,
                    bulkOpsSubsProfileDAO, bulkOpsCorpProfileDAO, existingProfilesMap, persisterTxn);

            // Skipping isUpmCall validation and setting clientType and Hierarchy as it is not present in deleteBulkSubscriber payload

            // Last corporate
            if(corpId!=0) {
                int corpSubsCount = bulkOpsSubsProfileDAO.retrieveCorpSubsCntNotMarkedForDeleteion(corpId, persisterTxn);
                if (corpSubsCount == bulkSubsProvInfoDTO.getSubscriberList().size()) {
                    partialRespDTO.setCorporateDeleted(true);
                }
            }

            // calling corp here
            knLogger.debug(methodName, "invoking the V2 corporate library ");
            String xdmPTTServerId = KnBulkOpsDBUtil.getXdmPttServerId();

            // Open Transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening transaction ");
            persisterTxn.open();
            ownedTxn = true;

            // Get CorpProfile Details
            KnCorpProfileDTO corpProfileDetails = new KnCorpProfileDTO();
            if (corpId != 0) {
                corpProfileDetails = commonInfoUtil
                        .getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            }

            deleteCorporateData(corpId, bulkSubsProvInfoDTO, existingProfilesMap, corpProfileDetails, partialRespDTO, xdmPTTServerId, persisterTxn);
            knLogger.debug(methodName, "deleted the corporate data for mdn ");

            KnPendingTxnInfoDTO knPendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            knPendingTxnInfoDTO.setOpsId(KnGeneralUtil.ASYNC_OPS_ID.DELETE_SUBSCRIBER.get());
            BitSet taskBitSet = new BitSet();
            taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.deleteSubscriberProfile.get(), true);

            boolean hasDispatchMdn = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList())
                    .stream()
                    .map(existingProfilesMap::get)
                    .filter(Objects::nonNull)
                    .anyMatch(profile -> profile.getSubsClientType() == DISPATCH.value()
                            || profile.getSubsClientType() == THIRDPARTYDISPATCHERCLIENT.value());
            if (hasDispatchMdn) {
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.initiateDispJobForDeletedMdn.get(), true); //where mdn=?
            }


            List<String> publicMdns = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList())
                    .stream()
                    .filter(mdn -> {
                        KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
                        return profile != null
                                && profile.getPublicSubscriptionType() == com.kodiak.xdms.server.common.resources.KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value();
                    })
                    .collect(Collectors.toList());
            if (!publicMdns.isEmpty()) {
                knLogger.debug(methodName, "invoking the public library for MDNs::", publicMdns.size());
                bulkOpsInfoUtil.deleteAllContactsAndGroups(publicMdns, xdmPTTServerId, persisterTxn); // DEL - Delete Operation D1 Keep in SYNC JOB
                knLogger.debug(methodName, "deleted public data for public MDNs");
            }

            List<String> reqMdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
            final Map<String, String> baseMdnMap = reqMdnList.stream()
                    .collect(Collectors.toMap(item -> item, item -> item, (existing, replacement) -> existing, HashMap::new));

            Map<String, String> mdnToMcpttIdMap = reqMdnList.stream()
                    .filter(mdn -> {
                        KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
                        return profile != null && profile.getMcpttId() != null;
                    })
                    .collect(Collectors.toMap(
                            mdn -> mdn,
                            mdn -> existingProfilesMap.get(mdn).getMcpttId(),
                            (existing, replacement) -> existing,
                            HashMap::new));

            List<String> mcpttIdList = mdnToMcpttIdMap.values().stream()
                    .distinct()
                    .collect(Collectors.toList());

            if (!mcpttIdList.isEmpty()) {
                try {
                    KnXDMProfileIdMdnMapRespDTO baseMdnRespDto = new KnXDMProfileIdMdnMapRespDTO();
                    baseMdnRespDto.setProfileIdMdnMap(provInfoUtil.retrieveProfileIdMDNsByMcpttIds(mcpttIdList, false, persisterTxn));
                    Map<String, Map<Integer, String>> profileMdnMap = baseMdnRespDto.getProfileIdMdnMap();
                    if (profileMdnMap != null && !profileMdnMap.isEmpty()) {
                        mdnToMcpttIdMap.forEach((mdn, mcpttId) -> {
                            Map<Integer, String> mdnByProfileIndex = profileMdnMap.get(mcpttId);
                            if (mdnByProfileIndex != null) {
                                String baseMdn = mdnByProfileIndex.get(USER_PROFILE_INDEX);
                                if (baseMdn != null && !baseMdn.isEmpty()) {
                                    baseMdnMap.put(mdn, baseMdn);
                                }
                            }
                        });
                    }
                } catch (KnProvBOException pbe) {
                    knLogger.error(methodName, "Profile not available, so continue further");
                }
            }

            int lmrInteropFlag = DISABLED;
            KnCorpConfigInfoDto corpConfigInfoDto = commonInfoUtil.selectLmrInterOpInDB(corpId, persisterTxn);
            KnCorporateExdmsNotifyDto corporateExdmsNotifyDto = null;
            // DEL - Delete Operation D3 - Keep in SYNC JOB
            KnOPDeleteSubsRespDTO provRespDTO = processDeleteSub(
                    bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
                    existingProfilesMap, xdmPTTServerId, persisterTxn);
            knLogger.debug(methodName, "Delete Subscriber operation response - ", provRespDTO);

            Map<String, Boolean> xcapCouchClientMap = new HashMap<>();
            for(String mdn: bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList())) {
                KnPayloadIP knPayloadIP = new KnPayloadIP();
                knPayloadIP.setCorpSubscriptionType(String.valueOf(existingProfilesMap.get(mdn).getCorporateSubscriptionType()));

                populateKnPayLoadIP(knPayloadIP, taskBitSet, knPendingTxnInfoDTO, mdn, xdmPTTServerId, persisterTxn);
                knPendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
                insertDataIntoAsynkFwkTable(mdn, corpProfileDetails.getCorpId(), knPendingTxnInfoDTO, knPayloadIP, xdmPTTServerId, persisterTxn);

                xcapCouchClientMap.put(mdn, KnGeneralUtil.getFeatureBitValue(
                            existingProfilesMap.get(mdn).getActiveFS2(),
                            com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value()
                        )
                );
            }

            if (corpId != 0) {
                int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpId, persisterTxn);
                knLogger.debug(methodName, "corpSubsCount: ", corpSubsCount);
                if (corpSubsCount == 0) {
                    if (corpConfigInfoDto != null && corpConfigInfoDto.getParamValue() != null) {
                        lmrInteropFlag = Integer.parseInt(corpConfigInfoDto.getParamValue());
                        commonInfoUtil.deleteLmrInterOpInDB(corpId, persisterTxn);
                    }
                    //Notify to MCS
                    corpProfileDetails.setNetworkName(null);
                    corporateExdmsNotifyDto = commonInfoUtil.formCorpNotifyPayload(DELETE_CORP.value(), corpProfileDetails, lmrInteropFlag, com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CORPORATE_EVENT.value());
                }
            }

            //Get the xcap mobile sync flag
            boolean xcapMobileSync = bulkOpsInfoUtil.getXcapMobileSyncFlag(persisterTxn);
            Set<String> allMcsXcapUris = genInfoUtil.getMCSXCAPRootURIs(persisterTxn);

            //Remove mdn from mcpttPermissionsConfig from CBS upm doc
            for(String mdn: bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList())) {
                KnIPUserProfileDTO delMcpttReqDto = new KnIPUserProfileDTO();
                delMcpttReqDto.setMdn(mdn);
                delMcpttReqDto.setCorpId(String.valueOf(corpId));
                delMcpttReqDto.setPocPttServerId(existingProfilesMap.get(mdn).getPocHome());

                if (persisterTxn.getTransactionStatus() == KnCorpCommonInfoUtil.TXN_STATE.STARTED.ordinal()) {
                    corpClientIntf.deleteMcpttPermConfig(delMcpttReqDto, persisterTxn);
                }
            }

            // Commit Transaction
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction ");
                persisterTxn.save();
            }

            // sending the notification
            boolean hasNonInteropMDN = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList())
                    .stream()
                    .map(existingProfilesMap::get)
                    .filter(Objects::nonNull)
                    .anyMatch(profile -> profile.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()
                            && profile.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()
                            && profile.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value());
            if (hasNonInteropMDN) {
                List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOList = new ArrayList<>();
                provRespDTO.getDirChgDTOs().forEach(dirChgDTO -> {
                    KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
                    xcapDiffNotifyDTO.setDeRegisterNotify(true);
                    KnDeRegisterNotifyDTO deRegisterNotifyDTO = new KnDeRegisterNotifyDTO();
                    deRegisterNotifyDTO.setMdn(dirChgDTO.getMdn());
                    deRegisterNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                    deRegisterNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                    deRegisterNotifyDTO.setAction(com.kodiak.xdms.server.common.resources.KnConstants.MESSAGE_TYPE.USER_DELETE.value());
                    xcapDiffNotifyDTO.setDeRegisterNotifyDTO(deRegisterNotifyDTO);
                    xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                    xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                    xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                    xcapDiffNotifyDTO.setReason(com.kodiak.xdms.server.common.resources.KnConstants.REASON.USER_DELETE.value());
                    xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
                    xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                    xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                    knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffNotifyDTO);

                    xcapDiffNotifyDTOList.add(xcapDiffNotifyDTO);
                });

                KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
                boolean notificationStatus = notifier.sendXcapDiffNotifications(xcapDiffNotifyDTOList);
                if (notificationStatus) {
                    knLogger.debug(methodName, "Successfully sent the notification");
                }
            }

            if(xcapMobileSync) {
                // Notify only MDNs that passed validation and were actually deleted; the request set (existingProfilesMap) still holds failed MDNs
                List<String> deletedMdns = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
                List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
                for (String mdn : deletedMdns) {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
                    if (profile == null) {
                        continue;
                    }
                    KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                    knSubscrEXDMSNotifyDto.setCorpid(corpId);
                    knSubscrEXDMSNotifyDto.setMdn(mdn);
                    knSubscrEXDMSNotifyDto.setId(DELETE_SUBSCR.value() + com.kodiak.xdms.server.common.resources.KnConstants.LINE_SAPERATOR + mdn);
                    knSubscrEXDMSNotifyDto.setType(DELETE_SUBSCR.value());
                    knSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    knSubscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                    knSubscrEXDMSNotifyDto.setClientType(profile.getSubsClientType());
                    knSubscrEXDMSNotifyDto.setLastProfileUpdateTime(profile.getLastProfileUpdateTime());
                    knSubscrEXDMSNotifyDto.setBaseMdn(baseMdnMap.get(mdn));
                    knSubscrEXDMSNotifyDto.setMcpttId(profile.getMcpttId());

                    knSubscrEXDMSNotifyDto.setPocPttId(profile.getPocHome());
                    knSubscrEXDMSNotifyDto.setSubsFS2(profile.getSubsFS2());
                    knSubscrEXDMSNotifyDto.setMcId(profile.getMcId());
                    knSubscrEXDMSNotifyDto.setMcdataId(profile.getMcDataId());
                    knSubscrEXDMSNotifyDto.setMcvideoId(profile.getMcVideoId());
                    knSubscrEXDMSNotifyDto.setNetworkName(profile.getNetworkName());
                    knSubscrEXDMSNotifyDto.setDeviceId(mdn);
                    notifyDtoList.add(knSubscrEXDMSNotifyDto);
                }
                if (!notifyDtoList.isEmpty()) {
                    knLogger.info(methodName, "Publishing micro service notify for user- ");
                    bulkOpsNotifyUtil.startNotifyMicroServicesJob(notifyDtoList);
                } else {
                    knLogger.info(methodName, "Skipping micro service notify - no successfully deleted MDNs ");
                }
            }

            KnGeneralCacheUtil generalCacheUtil = new KnGeneralCacheUtil();
            partialRespDTO.setOIDCApplicable(generalCacheUtil.isOIDCApplicable(corpId, WEBDISPATCHER));

            // Finalize the response based on success/failure counts
            int currentTotalMdnCount = bulkSubsProvInfoDTO.getSubscriberList().size();
            bulkOpsInfoUtil.finalizePartialResponse(partialRespDTO, initialTotalMDNCount, KnBulkOpsConstants.BulkOperations.DELETEBULKSUBSCRIBER);
            partialRespDTO.setFailureMdnCount(initialTotalMDNCount - currentTotalMdnCount);
            buildDeleteBulkSubscriberResponse(bulkSubsProvInfoDTO, existingProfilesMap, partialRespDTO);
            partialRespDTO.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            responseDTO = partialRespDTO;

            Collection<Integer> successPegs = new ArrayList<Integer>();
            successPegs.add(KnOMConstants.XDM_NUM_BULK_SUBSCR_DELETED);
            Collection<Integer> listOfPegs = provRespDTO.getSuccessPegs();
            if (listOfPegs != null && !listOfPegs.isEmpty()) {
                successPegs.addAll(listOfPegs);
            }
            KnStatisticsManagerImpl.getInstance().increment(successPegs);

            Map<String, List<String>> userProfileMdnMap = provRespDTO.getUserProfileMdnMap();
            if (userProfileMdnMap != null && !userProfileMdnMap.isEmpty()) {
                knLogger.debug(methodName, "userProfileMdnMap entries to process - ", userProfileMdnMap.size());
                for (Map.Entry<String, List<String>> entry : userProfileMdnMap.entrySet()) {
                    String mdn = entry.getKey();
                    List<String> upms = entry.getValue();
                    knLogger.debug(methodName, "User profile Mdns for mdn - ", KnGDPRTemplate.mdn(mdn), " are ", KnGDPRTemplate.mdnList(upms));
                    KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
                    ipUserProfileDTO.setCorpId(String.valueOf(corpId));
                    ipUserProfileDTO.setUserProfileMdns(upms);

                    String deleteUpmJsonString = commonInfoUtil.ObjToJson(ipUserProfileDTO);
                    Long transactionId = System.currentTimeMillis();
                    knLogger.debug(methodName, "transactionId - ", transactionId);
                    KnAsyncJobDTO knAsyncJobDTO = bulkOpsNotifyUtil.createJobNotifyDTO(String.valueOf(corpId), transactionId.toString()
                            , null, com.kodiak.xdms.server.common.resources.KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value(), null,
                            com.kodiak.xdms.server.common.resources.KnConstants.UPM_RESOURCE_TYPE.MDN.Value(), deleteUpmJsonString, com.kodiak.xdms.server.common.resources.KnConstants.UPM_JOB_STATUS.NEW.Value(), null);
                    bulkOpsNotifyUtil.addJob(knAsyncJobDTO);
                }
            }

            if (provRespDTO.isDeleteDeviceNotify()) {
                bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()).forEach(mdn ->
                    sendDeleteDeviceNotification(TEL_URI_TEMPLATE + mdn));
            }

            if (corporateExdmsNotifyDto != null) {
                knLogger.info(methodName, "Publishing micro service notify for Delete corp- ", corporateExdmsNotifyDto);
                bulkOpsNotifyUtil.startNotifyMicroServicesJob(List.of(corporateExdmsNotifyDto));
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            responseDTO = (KnXDMBulkOpsRespDTO) bulkOpsInfoUtil.buildFailureResponse(bulkSubsProvInfoDTO, e, startTime);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_DELETION_FAILURE);
        }

        knLogger.info(methodName, "EXIT: deleteBulkSubscriber operation - ", responseDTO);
        return responseDTO;
    }

    public void buildDeleteBulkSubscriberResponse(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            KnXDMBulkOpsRespDTO partialRespDTO) {

        String methodName = "buildDeleteBulkSubscriberResponse()";
        knLogger.debug(methodName, "ENTRY: Building Delete Bulk Subscriber Response");
        partialRespDTO.setResponseMessage(KnBulkOpsConstants.DELETE_BULK_SUBSCRIBER_SUCCESS);
        partialRespDTO.setSuccessMdns(bulkSubsProvInfoDTO.getSubscriberList());

        List<KnBulkSubscriberEntry> successMdns = new ArrayList<>();

        List<String> mdns = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
        mdns.forEach( mdn -> {
            KnBulkSubscriberEntry entry = new KnBulkSubscriberEntry();
            entry.setMdn(mdn);
            entry.setAliasMdn(existingProfilesMap.get(mdn) != null ? existingProfilesMap.get(mdn).getAliasMdn() : null);
            entry.setUserId(existingProfilesMap.get(mdn) != null ? existingProfilesMap.get(mdn).getUserId() : null);
            entry.setDispatchType(existingProfilesMap.get(mdn) != null ? existingProfilesMap.get(mdn).getDispatchType() : null);
            entry.setSubsClientType(existingProfilesMap.get(mdn) != null ? existingProfilesMap.get(mdn).getSubsClientType() : null);
            successMdns.add(entry);
        });
        partialRespDTO.setSuccessMdns(successMdns);

        knLogger.debug(methodName, "EXIT: Built Delete Bulk Subscriber Response - ", partialRespDTO);
    }

    // Using this method data can be inserted into Async framework table as per use case.
    private static void insertDataIntoAsynkFwkTable(String mdn, int corpId, KnPendingTxnInfoDTO pendingTxnInfoDTO,
                                                    KnPayloadIP knPayloadIP, String pttServerId, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "insertDataIntoAsynkFwkTable";
        try {
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(pttServerId);
            if (pendingTxnInfoDTO == null) {
                pendingTxnInfoDTO = new KnPendingTxnInfoDTO();
            }
            pendingTxnInfoDTO.setRecId(1);
            pendingTxnInfoDTO.setCorpId(corpId);
            pendingTxnInfoDTO.setEntityId(mdn);
            pendingTxnInfoDTO.setTxnId(UUID.randomUUID().toString());
            if (knPayloadIP == null) {
                knPayloadIP = new KnPayloadIP();
            }
            pendingTxnInfoDTO.setPayload(KnGeneralUtil.convertKnPaylaodIPToJsonString(knPayloadIP));
            if (pendingTxnInfoDTO.getPayload().length() > 2000) {
                knLogger.error(methodName, "Payload size is more than 2000 bytes");
                throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "Payload size is more than 2000 bytes");
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
            throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed due to exception hence rejecting the request");
        }
    }

    private static KnPendingTxnInfoDTO populateKnPayLoadIP(KnPayloadIP knPayloadIP, BitSet taskBitSet,
                                                           KnPendingTxnInfoDTO pendingTxnInfoDTO, String mdn,
                                                           String pttserverId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "populateKnPayLoadIP";
        knLogger.debug(methodName, "ENTRY: populateKnPayLoadIP - ", KnGDPRTemplate.mdn(mdn));
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

    public void deleteCorporateData(Integer corpId,
                                    KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                    Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
                                    KnCorpProfileDTO corpProfileDetails,
                                    KnXDMBulkOpsRespDTO partialRespDTO,
                                    String xdmPTTServerId,
                                    KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException {
        // Implementation for deleting corporate data
        String methodName = "deleteCorporateData()";
        knLogger.debug(methodName, "ENTRY: Delete Corporate Data - ", bulkSubsProvInfoDTO);

        KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();

        List<String> mdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());

        // Collect MCPTT-compliant MDNs using a thread-safe collector (parallelStream safe)
        List<String> mcsComplianceMDNList = mdnList.parallelStream()
                .filter(mdn -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
                    return profile != null && profile.getMcpttCompliance() == 1;
                })
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));

        Integer maxGroupsPerCRIClient = corpProfileDetails.getMaxGroupsPerSubsc();
        knLogger.debug(methodName, "value of max group per subscriber ", maxGroupsPerCRIClient);

        if (maxGroupsPerCRIClient != null && !mcsComplianceMDNList.isEmpty()) {
            Map<String, Integer> groupCountMap = groupInfoUtil.getSubsScrGroupCount(mcsComplianceMDNList, xdmPTTServerId, persisterTxn);
            knLogger.debug(methodName, "groupCountMap - ", groupCountMap, " maxGroupsPerCRIClient - ", maxGroupsPerCRIClient);

            KnMDNValidationResult subscriberListValidationResult = new KnMDNValidationResult();
            for (String mdn : mdnList) {
                Integer groupCount = (groupCountMap != null) ? groupCountMap.getOrDefault(mdn, 0) : 0;
                if (groupCount.compareTo(maxGroupsPerCRIClient) > 0) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnErrorCodes.Validator.MAX_GROUPS_PER_CRI_CLIENT_EXCEED,
                            "max groups per CRI client exceed for subscriber: " + mdn);
                } else {
                    subscriberListValidationResult.addValidMdn(mdn);
                }
            }

            knLogger.debug(methodName, "Corporate Data Validation Results - Valid: ", subscriberListValidationResult.getValidMdns().size(),
                    ", Invalid: ", subscriberListValidationResult.getInvalidMdns().size());

            bulkOpsInfoUtil.addInvalidMdnsToResponse(subscriberListValidationResult, partialRespDTO);
            int validMdnCount = bulkOpsInfoUtil.filterSubscriberListByValidMdns(bulkSubsProvInfoDTO, subscriberListValidationResult.getValidMdns());
            knLogger.info(methodName, "Processing ", validMdnCount, " valid MDNs after max-groups-per-CRI-client validation");
        }
        else {
            knLogger.debug(methodName, "Skipping max-groups validation (limit=", maxGroupsPerCRIClient, ", mcpttCompliantMdns=", mcsComplianceMDNList.size(), ")");
        }

        // Re-fetch MDNs from subscriber list so downstream operations only act on the MDNs
        // that survived the max-groups-per-CRI-client validation above.
        mdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process after max-groups-per-CRI-client validation");
            return;
        }

        // etag removal
        groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmPTTServerId, persisterTxn);

        //mdn-groupId-ZoneId-ChannelId
        Collection<KnCorpAddlTGInfoDTO> subsMdnAddlTGList = groupInfoUtil.getSubsAddlTGList(mdnList, xdmPTTServerId, persisterTxn);
        knLogger.debug(methodName, "subsAddlTGList - ", subsMdnAddlTGList);
        if (subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()) {
            groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmPTTServerId, persisterTxn);
        }

        if (corpId <= 0) {
            Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdnList, xdmPTTServerId, persisterTxn);//got from other corp
            knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
            if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                knLogger.debug(methodName, "Subscriber passed is of public subscription type, deleting the external contact data from other corporate");
                deleteExtContactDataFrmOtherCorp(corpId, mdnList, persisterTxn, corpIdExtContactNameMap, corpInOutParamDTO, xdmPTTServerId);
            }
            return;
        }

        List<Integer> corpContactListIdList = mdnList.stream()
                        .map(existingProfilesMap::get)
                        .filter(Objects::nonNull)
                        .map(KnBulkSubsProfileDTO::getCorpContactListId)
                        .filter(id -> id > 0)
                        .distinct()
                        .collect(Collectors.toList());

        Map<String, KnIPCorpContactDTO> corpContactDTOMap = mdnList.stream()
                .collect(Collectors.toMap(
                        mdn -> mdn, // Map Key
                        mdn -> {    // Map Value
                            KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
                            contactDTO.setMdn(mdn);
                            contactDTO.setCorpId(corpId);
                            return contactDTO;
                        }
                ));

        HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();

        // Removing isUPMCall as it is not required in BulkProvisioning

        Collection<KnCorpSublistDTO> listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(
                corpContactDTOMap, 0, xdmPTTServerId, persisterTxn, corpId
        );
        if (listOfDistSublistIds != null && !listOfDistSublistIds.isEmpty()) {
            Collection<Integer> removeListIds = new ArrayList<Integer>();
            for (KnCorpSublistDTO corpSubDto : listOfDistSublistIds) {
                removeListIds.add(corpSubDto.getSublistId());
            }
            knLogger.debug("removeListIds::", removeListIds);
            KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
            corpSubsDto.setMdnList(new ArrayList<>(corpContactDTOMap.keySet()));
            contactInfoUtil.removeSublistMappingForSubscriber(removeListIds, corpSubsDto, xdmPTTServerId, persisterTxn);
        }

        contactInfoUtil.deleteBulkSubscribersContactList(mdnList, xdmPTTServerId, persisterTxn);//where mdn=?
        contactInfoUtil.updateSubscribersContactCount(mdnList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmPTTServerId, persisterTxn);

        if (!corpContactListIdList.isEmpty()) {
            sublistInfoUtil.deleteSublist(corpContactListIdList, corpId, mdnList, xdmPTTServerId, persisterTxn);
        }

        LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
        LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
        Map<Integer, Collection<String>> groupDistributionList = new HashMap<Integer, Collection<String>>();

        Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdnList, xdmPTTServerId, persisterTxn);
        knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
        if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
            Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
            if (corpIdList != null && !corpIdList.isEmpty()) {
                // update the corporate etag.
                knLogger.debug(methodName, "corpIdList - ", corpIdList);
                contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmPTTServerId, persisterTxn);
            }//updating other corp etag is allowing in the sync path.. nn
        }

        //get all group ids where this mdn is member in all corporation.
        Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList,
                xdmPTTServerId, persisterTxn);
        knLogger.debug(methodName, "groupMemberMap :", groupMemberMap);
        Map<Integer, String> dispGrpNameMap = groupInfoUtil.getSupervisorGroupsAndName(mdnList, KnConstants.GROUP_DISPATCHER, xdmPTTServerId, persisterTxn);

        Collection<String> disabledOdlMember = new HashSet<>();
        Map<Integer, Integer> groupMapListForProfileUpdate = new HashMap<>();
        if (!groupMemberMap.keySet().isEmpty()) {
            Collection<Integer> allGroupIdList = groupMemberMap.keySet();
            List<Integer> otherGrpIdList = new ArrayList<>(allGroupIdList);

            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(allGroupIdList, xdmPTTServerId, persisterTxn);
            Map<Integer, Collection<KnCorpAddlTGInfoDTO>> existingAddlTGList = new HashMap<>();
            Collection<KnCorpAddlTGInfoDTO> delAddlTGList = new ArrayList<>();
            // Additional Talk Group:
            for (KnCorpAddlTGInfoDTO addlTg : subsAddlTGList) {
                Collection<KnCorpAddlTGInfoDTO> existAddl = null;
                if (existingAddlTGList.get(addlTg.getGroupId()) != null) {
                    existAddl = existingAddlTGList.get(addlTg.getGroupId());
                    existAddl.add(addlTg);
                } else {
                    existAddl = new ArrayList<>();
                    existAddl.add(addlTg);
                    existingAddlTGList.put(addlTg.getGroupId(), existAddl);
                }
            }

            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(allGroupIdList, xdmPTTServerId, persisterTxn);

            Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(allGroupIdList, xdmPTTServerId, persisterTxn);
            for (KnCorpGroupMemberDTO grpMem : grpMemberDetails) {
                disabledOdlMember.add(grpMem.getMdn());
            }

            for (int grpId : otherGrpIdList) {
                removeGrpMdnListMap.put(grpId, new LinkedList<>(mdnList));
                groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.ETAG_CHANGE.value());
            }

            Collection<Integer> groupInfoUpdate = new ArrayList<>();
            boolean hasSgOrGroupMdn = mdnList.stream()
                    .map(existingProfilesMap::get)
                    .filter(Objects::nonNull)
                    .anyMatch(profile -> profile.getSubsClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()
                            || profile.getSubsClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value());
            if (hasSgOrGroupMdn) {
                Map<Integer, Integer> sgMDNCountDetailsMap = groupInfoUtil.getSGCorpGroupMembersCount(allGroupIdList, xdmPTTServerId, persisterTxn);
                if (sgMDNCountDetailsMap != null) {
                    sgMDNCountDetailsMap.forEach((corpGroupId, SGMemberCount) -> {
                        if (SGMemberCount == 1) {
                            groupInfoUpdate.add(corpGroupId);
                        }
                    });
                }
                groupInfoUtil.modifyGroupLmrInteropCapable(LMR_INTEROP_NON_CAPABLE, groupInfoUpdate, xdmPTTServerId, persisterTxn);
            }

            //Update IS_LARGE group flag from 1 to 0 in case of Delete Subscriber procedure
            // when group member count becomes less than or equal to maximum normal talk group count.
            Map<Integer, Integer> currentGrpMemCount = groupInfoUtil.getGroupMemCount(allGroupIdList, xdmPTTServerId, persisterTxn);
            Map<Integer, Integer> isLargeGrpDisable = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : currentGrpMemCount.entrySet()) {
                int memCount = entry.getValue();
                if ((groupDetailsMap.get(entry.getKey()).getGroupType()) == KnConstants.STANDARD_GROUP &&
                        memCount <= corpProfileDetails.getMaxMemPerCorpGroup() && groupDetailsMap.get(entry.getKey()).isLargeGroup()) {
                    isLargeGrpDisable.put(entry.getKey(), 0);
                } else if ((groupDetailsMap.get(entry.getKey()).getGroupType()) == KnConstants.DISPATCH_GROUP &&
                        memCount <= corpProfileDetails.getMaxMembersPerDispatchGroup() && groupDetailsMap.get(entry.getKey()).isLargeGroup()) {
                    isLargeGrpDisable.put(entry.getKey(), 0);
                }
            }
            if (!isLargeGrpDisable.isEmpty()) {
                groupInfoUtil.updateIsLargeGrpFlag(isLargeGrpDisable, xdmPTTServerId, persisterTxn);
            }

            //Filter out the actually added members and deleted members as same member can be already present in the group via sublist etc
            //DAO calls to delete from the dg.corpgroupdistinfo table
            Map<Integer, Map<String, Collection<String>>> groupMemDelMap = new HashMap<Integer, Map<String, Collection<String>>>();
            for (int grpId : otherGrpIdList) {
                Map<String, Collection<String>> deletedMemberMap = new HashMap<String, Collection<String>>();
                deletedMemberMap.put(KnConstants.DELTED_MEMBERS, removeGrpMdnListMap.get(grpId));
                groupMemDelMap.put(grpId, deletedMemberMap);
            }
            //DAO calls to delete from the dg.corpgroupdistinfo table
            groupInfoUtil.deleteFrmCorpGroupDistInfo(groupMemDelMap, xdmPTTServerId, persisterTxn);

            //Determine the deleted and the modified groups
            Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(otherGrpIdList, xdmPTTServerId, persisterTxn);
            knLogger.debug(methodName, "groupListStatus :", groupListStatus);
            //retrieval of members
            groupDistributionList = groupInfoUtil.getGroupSubscriberDistList(allGroupIdList, xdmPTTServerId, persisterTxn);
            Collection<Integer> ownerGroupIds = groupInfoUtil.getOwnerGroupIds(mdnList, corpId, xdmPTTServerId, persisterTxn);
            knLogger.info(methodName, "ownerGroupIds: ", ownerGroupIds);
            // Delete corp groups with < 2 members
            Set<Integer> delGroupIdList = new HashSet<>();
            if (groupListStatus.get(KnConstants.DELETED) != null || !ownerGroupIds.isEmpty()) {
                delGroupIdList = new HashSet<>();
                if (groupListStatus.get(KnConstants.DELETED) != null) {
                    delGroupIdList.addAll(groupListStatus.get(KnConstants.DELETED).keySet());
                }
                delGroupIdList.addAll(ownerGroupIds);
                if (!delGroupIdList.isEmpty()) {
                    for (Integer grpId : delGroupIdList) {
                        removeGrpMdnListMap.remove(grpId);
                        delGroupMemStatus.remove(grpId);
                        if (existingAddlTGList.get(grpId) != null)
                            delAddlTGList.addAll(existingAddlTGList.get(grpId));
                        groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
                    }
                    if (groupListStatus.get(KnConstants.DELETED) != null) {
                        groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                    }
                    groupNameMap.putAll(dispGrpNameMap);
                }
            }

            // Additional Talk Group:
            if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {//Removing not and just keeping the deletion
                groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmPTTServerId, persisterTxn);
                subsAddlTGList.removeAll(delAddlTGList);
                subsAddlTGList.forEach(subsAddl -> {
                    if (groupDetailsMap.get(subsAddl.getGroupId()) != null)
                        subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                });
            }

            groupInfoUtil.emptyGroup(delGroupIdList, xdmPTTServerId, corpInOutParamDTO, persisterTxn);

            // Removing GrpHierarchy deletion as request is not supporting hierarchy type.

        }

        //updating etag and time here not commenting nn
        Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                null, String.valueOf(corpId), mdnList, "0", xdmPTTServerId, persisterTxn
        );
        // respDTO not required hence not fetching xcapRootUri for notification

        //Delete the MDN from External contact table.
        if (!corpIdExtContactNameMap.isEmpty()) {
            contactInfoUtil.deleteSusbcribersFromExtContactTables(mdnList, xdmPTTServerId, persisterTxn);
        }

        //Delete MCPTT related document.
        deleteMcpttDoc(mdnList, xdmPTTServerId, persisterTxn,false);

        // hence return type of the cleanUpSubsCampedGrps is irrelevant
        groupInfoUtil.cleanUpSubsCampedGrps(mdnList, xdmPTTServerId, persisterTxn);

        this.groupInfoUtil.updateGroupOwner(mdnList, null, corpId, xdmPTTServerId, persisterTxn);
        activationInfoUtil.deleteActivationCodeForMDN(mdnList, xdmPTTServerId, persisterTxn);

        knLogger.debug(methodName, "EXIT: Delete Corporate Data completed for ", mdnList.size(), " MDNs");
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

    public void validateDeleteBulkSubscriber(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                             KnXDMBulkOpsRespDTO partialRespDTO,
                                             KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                             KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
                                             KnBulkOpsCorpProfileDAO bulkOpsCorpProfileDAO,
                                             Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
                                             KnPersisterTxn persisterTxn)
            throws KnBulkOpsException, KnDAOException {
        String methodName = "validateDeleteBulkSubscriber()";
        knLogger.debug(methodName, "ENTRY: Validating Delete Bulk Subscriber Request - ", bulkSubsProvInfoDTO);

        // All Subscribers are Invalid
        if (existingProfilesMap == null || existingProfilesMap.isEmpty()) {
            knLogger.error(methodName, "No existing subscriber profiles found for the provided MDNs");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_NOT_FOUND, "Subscriber Not Found");
        }
        // Subscriber validations
        bulkOpsInfoUtil.validateSubscriberList(KnBulkOpsConstants.BulkOperations.DELETEBULKSUBSCRIBER,
                bulkSubsProvInfoDTO, bulkOpsXDMServerDAO, bulkOpsSubsProfileDAO, partialRespDTO, existingProfilesMap, persisterTxn);
        // Hierarchy Validations
        bulkOpsInfoUtil.validateHierarchy(bulkSubsProvInfoDTO, bulkOpsSubsProfileDAO, bulkOpsCorpProfileDAO, partialRespDTO, persisterTxn);
        // Subscriber AuthStatus Validations
        bulkOpsInfoUtil.validateSubAuthStatus(bulkSubsProvInfoDTO, bulkOpsSubsProfileDAO, partialRespDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: Delete Bulk Subscriber Request validation successful");
    }

    private void deleteExtContactDataFrmOtherCorp(Integer corpId, List<String> mdnList,
                                                  KnPersisterTxn persisterTxn, Map<Integer, String> corpIdExtContactNameMap,
                                                  KnCorpInOutParamDTO inOutParamDTO, String xdmPTTServerId) throws KnDAOException, KnCorpBOException {
        String methodName = "deleteExtContactDataFrmOtherCorp()";
        knLogger.debug(methodName, "ENTRY: Deleting External Contact Data from Other Corp");

        if (corpIdExtContactNameMap == null) {
            corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdnList, xdmPTTServerId, persisterTxn);//passed as param
        }
        knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
        Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
        knLogger.debug(methodName, "corpIdList - ", corpIdList);

        // get all sublist ids where this MDN is member in other corporation as ext contact.
        Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil
                .getSubcriberSublistMemberShipListAsExtContact(mdnList, corpId, xdmPTTServerId, persisterTxn); //externalSUslistId,mdn

        //get all group ids where this mdn is member in other corporation as ext contact.
        Map<Integer, Collection<String>> groupMemberMap = groupInfoUtil
                .getAllSubscribersGroupListAsExtContact(mdnList, corpId, xdmPTTServerId, persisterTxn); //externalGroupid,mdn


        Collection<Integer> sublistList = sublistMemberMap.keySet();
        Collection<Integer> groupIds = groupMemberMap.keySet();

        if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
            sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmPTTServerId, persisterTxn);
            sublistInfoUtil.fetchAndUpdateSublistEtag(sublistList, xdmPTTServerId, persisterTxn);
        }

        if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
            Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(groupIds, xdmPTTServerId, persisterTxn);
            // Delete corp groups with < 2 members
            if (groupListStatus.get(KnConstants.DELETED) != null) {
                Set<Integer> delGroupIdList = groupListStatus.get(KnConstants.DELETED).keySet();
                if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                    groupInfoUtil.deleteAllGroups(delGroupIdList, xdmPTTServerId, inOutParamDTO, persisterTxn);
                }
            }
        }

        Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
        if (sharedSublistSet != null && !sharedSublistSet.isEmpty()) {
            ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sharedSublistSet, xdmPTTServerId, persisterTxn);
            ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(sharedSublists, xdmPTTServerId, persisterTxn);
            if (emptySublist != null && !emptySublist.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(emptySublist, xdmPTTServerId, persisterTxn);
            }
        }

        // deleting the ext contact from ext contact table
        if (!corpIdExtContactNameMap.isEmpty()) {
            contactInfoUtil.deleteSusbcribersFromExtContactTables(mdnList, xdmPTTServerId, persisterTxn);
            contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmPTTServerId, persisterTxn);
        }
    }

    private KnOPDeleteSubsRespDTO processDeleteSub(List<String> mdnList, Map<String, KnBulkSubsProfileDTO> existingProfilesMap, String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException, KnBOException {
        String methodName = "processDeleteSub()";
        knLogger.debug(methodName, "ENTRY: Processing Delete Subscriber operation for MDNs");
        KnOPDeleteSubsRespDTO responseDTO = new KnOPDeleteSubsRespDTO();

        Collection<Integer> successPegs = collectDeletionPegsByClientType(mdnList, existingProfilesMap);
        IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        //getting the prev etag of directory
        Map<String,Integer> previousEtagMap = commonXdmServerDAO.getCurrentEtagForDirDoc(mdnList, persisterTxn);

        commonXdmServerDAO.deleteXDMDirectoryForMdn(mdnList, persisterTxn);
        knLogger.debug(methodName, "deleted the directory for Mdn");

        //deleting the XDM Contact List
        commonXdmServerDAO.deleteContactListForMdn(mdnList, persisterTxn);
        knLogger.debug(methodName, "deleted the Contact List for mdn");

        //deleting the XDM Contact List Doc Map
        commonXdmServerDAO.deleteContactListDocMapForMdn(mdnList, persisterTxn);
        knLogger.debug(methodName, "deleted the contact list doc map for Mdns - ", KnGDPRTemplate.mdnList(mdnList));

        //deleting the Corp Resource List Index Doc only for MDNs whose profile is CORPORATE-subscribed
        List<String> mdnListForCorpResource = mdnList.stream()
                .filter(m -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(m);
                    return profile != null
                            && profile.getCorporateSubscriptionType() == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value();
                })
                .collect(Collectors.toList());
        if (!mdnListForCorpResource.isEmpty()) {
            commonXdmServerDAO.deleteCorpResourceListIndexDoc(mdnListForCorpResource, persisterTxn);
            knLogger.debug(methodName, "deleted the Corp Resource List Index Doc for Mdns - ", KnGDPRTemplate.mdnList(mdnListForCorpResource));
        }

        IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

        //delete client vocoder ID from DG.ClientSuppVocoders
        provXDMServerDAO.deleteClientSuppVocoder(mdnList, persisterTxn);
        Map<String, String> xcapRooturi = genInfoUtil.getXCAPRootURI(mdnList, persisterTxn, false);
        //delete the subscriber roaming profile
        provXDMServerDAO.deleteSubscrRoamingProfile(mdnList, persisterTxn);
        knLogger.debug(methodName, "Delete the Subscriber Roaming Profile");

        List<String> mdnListForNNISubscrProfile = mdnList.stream()
                .filter(m -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(m);
                    return profile != null
                            && ( profile.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()
                            || profile.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value());
                })
                .collect(Collectors.toList());

        if (!mdnListForNNISubscrProfile.isEmpty()) {
            knLogger.debug(methodName, "Deleting NNI Subscriber profile");
            provXDMServerDAO.deleteNNISubscrProfile(mdnListForNNISubscrProfile, persisterTxn);
        }

        Map<String, String> deviceCreatedAsMap = commonXdmServerDAO.getDeviceCreatedAsMap(mdnList, persisterTxn);
        List<String> deviceIdList = mdnList.stream()
                .filter(m -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(m);
                    if (profile == null) {
                        return false;
                    }
                    boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(profile.getSubsFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.MCDEVICE.value());
                    boolean impliciteDevice = KnProvConstants.IMPLICIT_DEVICE.equals(deviceCreatedAsMap.get(m));
                    return MCSCOMPLIANCE == profile.getMcpttCompliance()
                            || bitEnabled
                            || profile.getLicenseType() == KnProvConstants.LICENSEN_TYPE_STANDARD
                            || impliciteDevice;
                })
                .collect(Collectors.toList());
        List<String> deleteDeviceNotifyMdnList = deviceIdList.stream()
                .filter(m -> !KnProvConstants.IMPLICIT_DEVICE.equals(deviceCreatedAsMap.get(m)))
                .collect(Collectors.toList());
        if (!deviceIdList.isEmpty()) {
            knLogger.debug(methodName, "Deleting device info for Mdns - ", KnGDPRTemplate.mdnList(deviceIdList));
            KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
            deviceInfoPersistDTO.setDeviceIdList(deviceIdList);
            commonXdmServerDAO.deleteDeviceImpiInfo(deviceInfoPersistDTO, persisterTxn);
            commonXdmServerDAO.deleteDeviceInfo(deviceInfoPersistDTO, persisterTxn);
            if (!deleteDeviceNotifyMdnList.isEmpty()) {
                responseDTO.setDeleteDeviceNotify(true);
                responseDTO.setDeleteDeviceNotifyMdnList(deleteDeviceNotifyMdnList);
                knLogger.debug(methodName, "Delete device notify Mdns - ", KnGDPRTemplate.mdnList(deleteDeviceNotifyMdnList));
            }
        }

        List<String> deleteSubscriberCameraInfoList = mdnList.stream()
                .filter(m -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(m);
                    return profile != null
                            && profile.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value();
                })
                .collect(Collectors.toList());
        if (!deleteSubscriberCameraInfoList.isEmpty()) {
            knLogger.debug(methodName, "Deleting subscriber camera info for Mdns - ", KnGDPRTemplate.mdnList(deleteSubscriberCameraInfoList));
            commonXdmServerDAO.deleteSubscriberCameraInfo(deleteSubscriberCameraInfoList, persisterTxn);
        }

        List<String> mdnListForTPUser = mdnList.stream()
                .filter(m -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(m);
                    return profile != null
                            && (profile.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()
                            || profile.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value()
                            || profile.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value());
                })
                .collect(Collectors.toList());
        if (!mdnListForTPUser.isEmpty()) {
            knLogger.debug(methodName, "Deleting TP or Mobile Subscriber profile for Mdns - ", KnGDPRTemplate.mdnList(mdnListForTPUser));
            provXDMServerDAO.deletePAMAccPoolUsage(mdnListForTPUser, persisterTxn);
            KnTPUserPersistDTO tpUserPersistDTO = new KnTPUserPersistDTO();
            tpUserPersistDTO.setMdnList(mdnListForTPUser);
            provXDMServerDAO.deleteTPUserMDNMap(tpUserPersistDTO, persisterTxn);
        }

        knLogger.debug(methodName, "Deleting subscriber addon packages for Mdns - ", KnGDPRTemplate.mdnList(mdnList));
        provXDMServerDAO.deleteSubAddlOnPkgs(mdnList, persisterTxn);
        knLogger.debug(methodName, "Deleting SS channel group info for Mdns - ", KnGDPRTemplate.mdnList(mdnList));
        provXDMServerDAO.deleteSSChannelGrpInfo(mdnList, persisterTxn);
        knLogger.debug(methodName, "Deleting TGSS doc for Mdns - ", KnGDPRTemplate.mdnList(mdnList));
        provXDMServerDAO.deleteTGSSDoc(mdnList, persisterTxn);
        knLogger.debug(methodName, "Deleting subscriber alias id for Mdns - ", KnGDPRTemplate.mdnList(mdnList));
        provXDMServerDAO.deleteSubsAliasId(mdnList, persisterTxn);

        List<String> UserProfileMdns = new ArrayList<>();
        Map<String, List<String>> userProfileMdnMap = new HashMap<>();
        List<String> mdnListForUPM = mdnList.stream()
                .filter(m -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(m);
                    return profile != null
                            && (profile.getUserProfileIndex() == null || profile.getUserProfileIndex().equals(USER_PROFILE_INDEX));
                })
                .collect(Collectors.toList());
        for (String mdn : mdnListForUPM) {
            List<String> peers = new ArrayList<>(getMdnForUPM(mdn, persisterTxn));
            peers.remove(mdn);
            if (!peers.isEmpty()) {
                UserProfileMdns.addAll(peers);
                userProfileMdnMap.put(mdn, peers);
            }
        }
        if (!UserProfileMdns.isEmpty()) {
            knLogger.debug(methodName, "Updating service auth status for profile Mdns - ", KnGDPRTemplate.mdnList(UserProfileMdns));
            KnSubsProfilePersistDTO upmDTO = new KnSubsProfilePersistDTO();
            upmDTO.setServiceAuthStatus(com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value());
            upmDTO.setServiceStatusOp(com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_STATUS_OP.DEACTIVATED.value());
            upmDTO.setLastProfileUpdateTime(Calendar.getInstance().getTimeInMillis());
            upmDTO.setMdnList(UserProfileMdns);
            provXDMServerDAO.updateServiceAuthStatusForUPM(upmDTO, persisterTxn);
        }

        List<String> selfMdnListForAsyncDeletion = mdnList.stream()
                .filter(m -> {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(m);
                    return profile != null
                            && profile.getCorporateSubscriptionType() == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value();
                })
                .collect(Collectors.toList());
        if (!selfMdnListForAsyncDeletion.isEmpty()) {
            knLogger.debug(methodName, "Updating service auth status for self Mdns - ", KnGDPRTemplate.mdnList(selfMdnListForAsyncDeletion));
            KnSubsProfilePersistDTO selfDTO = new KnSubsProfilePersistDTO();
            selfDTO.setServiceAuthStatus(com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value());
            selfDTO.setServiceStatusOp(com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_STATUS_OP.MARKED_FOR_ASYNC_DELETION.value());
            selfDTO.setLastProfileUpdateTime(Calendar.getInstance().getTimeInMillis());
            selfDTO.setMdnList(selfMdnListForAsyncDeletion);
            provXDMServerDAO.updateServiceAuthStatusForUPM(selfDTO, persisterTxn);
        }

        responseDTO.setResponseMessage(KnProvConstants.DELETE_BULK_SUBSCRIBER_SUCCESS);
        responseDTO.setResponseStatus(com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.SUCCESS.value());

        List<KnOPDirChgDTO> dirChgDTOList = new ArrayList<>();

        for (String mdn : mdnList) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile != null) {
                KnOPDirChgDTO dto = new KnOPDirChgDTO();
                dto.setXcapRootURI(xcapRooturi.get(mdn));
                dto.setPocHome(profile.getPocHome());
                dto.setPresenceHome(profile.getPresenceHome());
                dto.setDirUri(genInfoUtil.generateDirDocUri(mdn));
                Integer prevEtag = previousEtagMap.get(mdn);
                dto.setDirPrevEtag(String.valueOf(prevEtag == null ? 0 : prevEtag));
                dto.setProtoVersion(String.valueOf(profile.getClientPVmajorVer()));
                dto.setClientType(profile.getSubsClientType());
                dto.setMdn(mdn);
                dirChgDTOList.add(dto);
            }
        }
        responseDTO.setDirChgDTOs(dirChgDTOList);
        responseDTO.setUserProfileMdnMap(userProfileMdnMap);
        responseDTO.setSuccessPegs(successPegs);

        knLogger.debug(methodName, "EXIT: Delete Subscriber operation processed successfully");
        return responseDTO;
    }

    /**
     * Builds the deletion-success peg list for the given MDN batch. One peg entry is
     * added per MDN whose profile's subsClientType has a mapping in
     * {@link #CLIENT_TYPE_TO_DELETE_PEG_ID}, matching the single-MDN behaviour in
     * {@code KnProvDeleteSubscProcessor.processDeleteSub} (e.g. 20 dispatcher MDNs
     * contribute 20 dispatcher peg entries). The caller is responsible for
     * incrementing these pegs on the OM manager.
     * <p>
     * Thread-safety: the static lookup map is immutable and the returned collection
     * is method-local, so no shared mutable state is introduced.
     */
    private Collection<Integer> collectDeletionPegsByClientType(List<String> mdnList,
                                                                Map<String, KnBulkSubsProfileDTO> existingProfilesMap) {
        String methodName = "collectDeletionPegsByClientType()";
        Collection<Integer> successPegs = new ArrayList<>();
        if (mdnList == null || mdnList.isEmpty() || existingProfilesMap == null || existingProfilesMap.isEmpty()) {
            knLogger.debug(methodName, "Nothing to peg - empty mdnList or profileMap");
            return successPegs;
        }
        List<String> unmappedClientTypes = new ArrayList<>();
        for (String mdn : mdnList) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile == null) {
                continue;
            }
            Integer pegId = CLIENT_TYPE_TO_DELETE_PEG_ID.get(profile.getSubsClientType());
            if (pegId == null) {
                unmappedClientTypes.add(mdn + ":" + profile.getSubsClientType());
                continue;
            }
            successPegs.add(pegId);
        }
        if (!unmappedClientTypes.isEmpty()) {
            knLogger.debug(methodName, "No deletion peg mapping for subsClientType(s): ", unmappedClientTypes);
        }
        knLogger.debug(methodName, "Collected ", successPegs.size(), " deletion pegs for ", mdnList.size(), " MDNs");
        return successPegs;
    }

    public void deleteMcpttDoc(List<String> mdnList, String xdmsHomePttId, KnPersisterTxn persisterTxn, boolean lastSubscriber) throws KnCorpBOException {
        String methodName = "deleteMcpttDoc(String, String, Map<String, KnOPDirChgDTO>, KnPersisterTxn, boolean)";
        LinkedList<String> mdnLinkedList = new LinkedList<String>(mdnList);
        corpSubsProvInfoUtil.deleteFromMcpttPermInfoAuthMdn(mdnList, xdmsHomePttId, persisterTxn);//yy where auth mdn=?
        corpSubsProvInfoUtil.deleteFromAuthDoc(mdnLinkedList, xdmsHomePttId, persisterTxn);//yy
        corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdnList, xdmsHomePttId, persisterTxn);//yy
        corpSubsProvInfoUtil.deleteFromEmergDoc(mdnLinkedList, xdmsHomePttId, persisterTxn);//yy
        if (lastSubscriber) {//keep this data if its not the last susbcriber
            corpSubsProvInfoUtil.deleteFromEmergInfoForDest(mdnList, xdmsHomePttId, persisterTxn);//nn
            corpSubsProvInfoUtil.deleteFromMcpttPermInfoTargetMdn(mdnList, xdmsHomePttId, persisterTxn);//nn where targ mdn=?
        }
    }

    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getMdnForUPM(String, boolean, KnPersisterTxn)";
        boolean ownedTxn = false;
        //KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.debug(methodName, "ENTRY: getMdnForUPM for baseMdn:", KnGDPRTemplate.mdn(baseMdn));
        List<String> mdnList = new ArrayList<>();
        Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap = new HashMap<>();
        try {
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // retrieving the Subscriber Info
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            mdnUpmFsMap = provXdmServerDAO.getProfileMdnNupmfsByBaseMdn(baseMdn, true, persisterTxn);
            mdnList.addAll(mdnUpmFsMap.keySet());
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "getMdnForUPM doesn't exists");
            }
            throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while getMdnForUPM" + e);
            throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while getMdnForUPM", e);
        }
        knLogger.debug(methodName, "EXIT:  getMdnForUPM - ", KnGDPRTemplate.mdnList(mdnList));

        return mdnList;
    }
}
