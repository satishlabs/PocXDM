package com.kodiak.xdms.server.bulkops.utils;

import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.*;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.mcsnotifymgr.KnMCSDocChangeNotifier;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocChangeListDto;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.mcsnotifymgr.resources.KnMCSNotifyConstants;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dao.KnSimulSessionDocDAO;
import com.kodiak.xdms.server.bulkops.dao.KnXDMDirectoryDAO;
import com.kodiak.xdms.server.bulkops.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnChangeAuthStatusBulkResult;
import com.kodiak.xdms.server.bulkops.dto.common.KnOPChgAuthStatusRespDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsJobConstants;
import com.kodiak.xdms.server.bulkops.resources.jobs.KnBulkOpsMicroServiceNotifyJob;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnDeleteDeviceDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;


public class KnBulkOpsNotifyUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsNotifyUtil.class);
    private static KnBulkOpsNotifyUtil instance = null;
    private KnJobSchedulerImpl scheduler;
    private KnMCSDocChangeNotifier mcsDocChangeNotifier = KnMCSDocChangeNotifier.getInstance();
    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
    private KnGeneralCacheUtil generalCacheUtil = KnGeneralCacheUtil.getInstance();



    public KnBulkOpsNotifyUtil() {
        scheduler = KnJobSchedulerImpl.getInstance();
    }

    public static synchronized KnBulkOpsNotifyUtil getInstance() {
        if (instance == null) {
            instance = new KnBulkOpsNotifyUtil();
        }
        return instance;
    }

    public void startNotifyMicroServicesJob(List<? extends KnEXDMSNotifyDto> notifyDtoList) throws KnException {
        String methodName = "startNotifyMicroServicesJob(List)";
        try {
            knLogger.info(methodName, "Entry");
            knLogger.debug(methodName, "Entry - ", notifyDtoList);
            if (notifyDtoList != null && !notifyDtoList.isEmpty()) {
                knLogger.info(methodName, "notifyDtoList size:",notifyDtoList.size());
                List<KnBulkOpsMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnEXDMSNotifyDto notifyDto : notifyDtoList) {
                    KnBulkOpsMicroServiceNotifyJob job = new KnBulkOpsMicroServiceNotifyJob(notifyDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, KnBulkOpsJobConstants.JOB_MICRO_SERVICE_NOTIFY);
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

    public void sendDeleteDeviceNotification(List<String> deviceIMPIList) {
        String methodName = "sendDeleteDeviceNotification";
        String registerPocHome = null;
        try {
            if (null != deviceIMPIList && !deviceIMPIList.isEmpty()) {
                knLogger.debug(methodName, "ENTRY::", deviceIMPIList.size());
                registerPocHome = KnGeneralCacheUtil.getInstance()
                        .getRegisterPOCHomeByDeviceIMPI(deviceIMPIList.get(0));
                if (null != registerPocHome && !registerPocHome.isEmpty()) {
                    KnDeleteDeviceDTO deleteDeviceDTO = new KnDeleteDeviceDTO();
                    deleteDeviceDTO.setDeviceIMPIList(deviceIMPIList);
                    deleteDeviceDTO.setPocHome(registerPocHome);
                    boolean notifyStatus = KnXcapDiffNotifier.getInstance()
                            .generateDeleteDeviceNotification(deleteDeviceDTO);
                    knLogger.info(methodName, "Notification send status ", notifyStatus);
                }
            }
        } catch (Exception ex) {
            knLogger.error(methodName, "Failed to sendDeleteDeviceNotify:: ", ex);
        }
    }

    public boolean prepareMcxNotifyForProfileMdns(KnOPChgAuthStatusRespDTO respDto) {
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

    public boolean getXcapMobileSyncFlag(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getXcapMobileSyncFlag()";
        knLogger.debug(methodName, "ENTRY");
        /*UCSPLATFORM-7936 : Removed the code below as the flag is now always considered enabled for sending the MCS notification.
          This change aligns with UCSPLATFORM-7936, where the flag dependency has been removed.*/
        return true;
    }

    public void setKnSubscriberExdmsNotifyDto(com.kodiak.xdms.server.bulkops.dto.common.KnOPChgAuthStatusRespDTO provRespDTO, Integer subscriberClientType, KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDTO) {
        knSubscrEXDMSNotifyDTO.setCorpid(provRespDTO.getCorpid());
        knSubscrEXDMSNotifyDTO.setAuthStatus(provRespDTO.getAuthStatus());
        knSubscrEXDMSNotifyDTO.setClientType(subscriberClientType);
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
     *
     * @param mdn
     * @return
     */
    public String generateTGSSListSelUri(String mdn,Boolean isAdd) {
        String methodName = "generateTGSSListSelUri";
        knLogger.debug(methodName, "generating TGSS List uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-tgss-list%22%5D/entry%5B@uri=%22kn-tgss-list/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D");
        //in remove doc case etag should not be part of notification
        if(isAdd == Boolean.TRUE) {
            strBuffer.append("/@etag");
        }
        knLogger.debug(methodName, "generated TGSS List uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn), " is - ", strBuffer.toString());

        return  strBuffer.toString();
    }


    /**
     * Bulk TGSS doc action: evaluate MSS bit changes for all MDNs, perform bulk DB reads and updates,
     * and return per-MDN KnOPProvDTO with DirChgDTO populated where applicable. No notifications here.
     */
    public Map<String, KnOPProvDTO> actionOnTGSSDocBulk(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                        KnChangeAuthStatusBulkResult updateAuthStatusResponseMap,
                                                        KnPersisterTxn persisterTxn)
            throws KnProvBOException, KnDAOException, KnBulkOpsException {
        String methodName = "actionOnTGSSDocBulk(KnXDMBulkSubsProvInfoDTO, KnChangeAuthStatusBulkResult, KnPersisterTxn)";
        List<String> mdnList = new ArrayList<>(updateAuthStatusResponseMap.getResponses().keySet());
        knLogger.debug(methodName, "Entry: mdn count=", mdnList.size());
        Map<String, KnOPProvDTO> resultMap = new HashMap<>();
        if (mdnList.isEmpty()) {
            return resultMap;
        }
        // Determine old/new MSS bit per MDN
        Map<String, Boolean> mssEnableBitNewMap = new HashMap<>();
        Map<String, Boolean> mssEnableBitOldMap = new HashMap<>();
        for (String mdn : mdnList) {
            KnOPChgAuthStatusRespDTO subsProfile = updateAuthStatusResponseMap.getResponses().get(mdn);
            if(subsProfile.getActiveFs2()!=null && bulkSubsProvInfoDTO.getActiveFS2()!=null) {
                boolean mssEnableBitNew = featureSetUtil.getFeatureBitValue(subsProfile.getActiveFs2(), KnConstants.FEATURE_SET.MULTISIMULTANEOUSSESSION.value());
                boolean mssEnableBitOld = featureSetUtil.getFeatureBitValue(bulkSubsProvInfoDTO.getActiveFS2(), KnConstants.FEATURE_SET.MULTISIMULTANEOUSSESSION.value());
                mssEnableBitNewMap.put(mdn, mssEnableBitNew);
                mssEnableBitOldMap.put(mdn, mssEnableBitOld);
            }
        }
        // remove mdn from mdnList if mdn is not present in old and new map to avoid unnecessary processing
        mdnList.removeIf(mdn -> !mssEnableBitNewMap.containsKey(mdn) && !mssEnableBitOldMap.containsKey(mdn));
        // Partition MDNs strictly where bits differ
        List<String> mdnsToCreate = new ArrayList<>();
        List<String> mdnsToDelete = new ArrayList<>();
        for (String mdn : mdnList) {
            boolean mssEnableBitNew = mssEnableBitNewMap.get(mdn);
            boolean mssEnableBitOld = mssEnableBitOldMap.get(mdn);
            if (mssEnableBitNew != mssEnableBitOld) {
                if (mssEnableBitNew) {
                    mdnsToCreate.add(mdn);
                } else {
                    mdnsToDelete.add(mdn);
                }
            }
        }

        KnSimulSessionDocDAO simulDao = new KnSimulSessionDocDAO();
        // Bulk read existing ETAGs for MDNs needing create (to compute new etag) and delete (optional)
        Map<String, Long> etags = simulDao.getSSDocEtags(mdnList, persisterTxn);
        // Prepare bulk inserts: only those that don't exist yet; set new etag = prior + 1 (missing => -1 + 1 = 0)
        List<Object[]> insertParams = new ArrayList<>();
        for (String mdn : mdnsToCreate) {
            long oldEtag = etags.get(mdn);
            long newEtag = oldEtag + 1L;
            if (newEtag == 0){
            knLogger.info(methodName, "Creating TGSS doc for mdn ", KnGDPRTemplate.mdn(mdn),
                    " with initial etag 0");
                insertParams.add(new Object[]{mdn, newEtag});
            }
        }
        // Bulk insert for mdnsToCreate
        if (!insertParams.isEmpty()) {
            simulDao.insertMdns(insertParams, persisterTxn);
        }
        // Bulk delete for mdnsToDelete
        if (!mdnsToDelete.isEmpty()) {
            simulDao.deleteMdns(mdnsToDelete, persisterTxn);
        }
        // Bulk update directory etags for affected MDNs
        List<String> affectedMdns = new ArrayList<>();
        affectedMdns.addAll(mdnsToCreate);
        affectedMdns.addAll(mdnsToDelete);
        Map<String, Integer> currentDirEtags = java.util.Collections.emptyMap();
        if (!affectedMdns.isEmpty()) {
            KnXDMDirectoryDAO dirDao = new KnXDMDirectoryDAO();
            // Fetch fresh current etags to avoid staleness from response object
            currentDirEtags = dirDao.getCurrentEtagsForDirDoc(affectedMdns, persisterTxn);
            // Then perform bulk increment/update
            dirDao.updateEtagForDirDocOfMdnList(affectedMdns, persisterTxn);
        }
        // Build per-MDN response DTOs with DirChgDTO where change occurred
        for (String mdn : affectedMdns) {
            KnOPProvDTO opProv = new KnOPProvDTO();
            try {
                boolean mssEnableBitNew = mssEnableBitNewMap.get(mdn);
                // Prepare doc change
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(mssEnableBitNew ? KnConstants.DOC_CHANGE_TYPE.REPLACE.value() : KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
                docChgDTO.setDocUri(generateTGSSListSelUri(mdn, mssEnableBitNew));
                if (mssEnableBitNew) {
                    long prior = etags.get(mdn);
                    long newEtag = prior + 1L;
                    docChgDTO.setNewEtag(String.valueOf(newEtag));
                }
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<>();
                chgDocList.add(docChgDTO);
                // Populate directory change DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                KnOPSubsProfileInfoDTO subsInfo = updateAuthStatusResponseMap.getSubscriberProfiles().get(mdn);
                dirChgDTO.setPocHome(subsInfo.getPoCHome());
                dirChgDTO.setPresenceHome(subsInfo.getPresenceHome());
                dirChgDTO.setDocChgDTO(chgDocList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                Integer prevDirEtag = currentDirEtags.getOrDefault(mdn, 0);
                dirChgDTO.setDirPrevEtag(String.valueOf(prevDirEtag));
                dirChgDTO.setDirNewEtag(String.valueOf(prevDirEtag + 1));
                dirChgDTO.setProtoVersion(subsInfo.getClientPVmajorVer() + "." + subsInfo.getClientPVminorVer());
                dirChgDTO.setClientType(subsInfo.getSubsClientType());
                opProv.setDirChgDTO(dirChgDTO);
            } catch (Exception e) {
                knLogger.error(methodName, "Failed to build DirChgDTO for mdn ", KnGDPRTemplate.mdn(mdn), e);
            }
            resultMap.put(mdn, opProv);
        }
        knLogger.debug(methodName, "Exit: affected mdns=", affectedMdns.size());
        return resultMap;
    }

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

    public boolean addJob(KnAsyncJobDTO asyncJobDTO) {
        String methodName = "addJob(KnAsyncJobDTO)";
        boolean result = false;
        knLogger.info(methodName, "Entry:-");
        try {
            generalCacheUtil.createAsyncJob(asyncJobDTO);
            knLogger.info(methodName, "Job Created Successfully:-");
            result = true;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "exception while creating job - ", e.getMessage());
        }

        return result;
    }
}