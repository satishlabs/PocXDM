/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

/**
 * ************************************************************************
 * <p>
 * File name:  KnGeneralCacheUtil.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jun 18, 2018                9.0
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

import com.kodiak.common.cache.ICache;
import com.kodiak.common.cache.KnCache;
import com.kodiak.common.cache.KnCacheKeys;
import com.kodiak.common.commdto.request.KnPUBContactAddonAliasDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCache;
import com.kodiak.common.ggcache.dao.KnSIPRegistrationStatusDAO;
import com.kodiak.common.ggcache.dto.*;
import com.kodiak.logger.KnLogger;

import java.sql.SQLException;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.BACKWARD_COMPATIBILTY_REQ;
import static com.kodiak.common.resources.KnConstants.CONTROLLED_UPGRADE_VERSION;

public class KnGeneralCacheUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGeneralCacheUtil.class);

    private static final int GG_MAX_RETRY_COUNT = 3;
    private static final long GG_RETRY_SLEEP_MS = 10_000L;
    private static KnGeneralCacheUtil instance;
    private static KnGeneralUtil generalUtil = new KnGeneralUtil();
    public static KnSIPRegistrationStatusDAO sipRegistrationStatusDAO = new KnSIPRegistrationStatusDAO();

    public static synchronized KnGeneralCacheUtil getInstance() {
        if (instance == null) {
            instance = new KnGeneralCacheUtil();
        }
        return instance;
    }

    public Map<String, KnCorpServiceInfoDTO> getCorpServiceType(int corpId) throws KnDAOException {
        String methodName = "getCorpServiceType(int)";
        knLogger.info(methodName, "ENTRY :");
        Map<String, KnCorpServiceInfoDTO> corpServiceInfoDTOMap;
        try {
            KnCache cache = KnCache.getInstance();
            ICache cacheManager = cache.getCacheManager();
            corpServiceInfoDTOMap = (Map<String, KnCorpServiceInfoDTO>) cacheManager.get(KnCacheKeys.CORP_SERVICES_MAP + corpId);
            if (corpServiceInfoDTOMap == null) {
                knLogger.info(methodName, " reading from GG ");
                corpServiceInfoDTOMap = KnGGCache.corpServiceMapDAO.getCorpServiceType(corpId);
                //cacheManager.add(KnCacheKeys.CORP_SERVICES_MAP + corpId, corpServiceInfoDTOMap, KnConstants.CACHE_EXPIRY_TIME);
                knLogger.info(methodName, " fetched values from GG : " + corpServiceInfoDTOMap);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORP_SERVICES_MAP", null);
        }
        return corpServiceInfoDTOMap;
    }

    public Map<String, KnServiceGroupInfoDTO> getSystemServiceMapByGrpId(int serviceGrpId) throws KnDAOException {
        String methodName = "getSystemServiceMapByGrpId(int)";
        knLogger.info(methodName, "ENTRY :");
        Map<String, KnServiceGroupInfoDTO> serviceGroupInfoDTOMap;
        try {
            KnCache cache = KnCache.getInstance();
            ICache cacheManager = cache.getCacheManager();
            serviceGroupInfoDTOMap = (Map<String, KnServiceGroupInfoDTO>) cacheManager.get(KnCacheKeys.SERVICE_GROUP_INFO);
            if (serviceGroupInfoDTOMap == null) {
                knLogger.info(methodName, " reading from GG ");
                serviceGroupInfoDTOMap = KnGGCache.systemServiceMapDAO.getSystemServiceMapByGrpId(serviceGrpId);
                //cacheManager.add(KnCacheKeys.SERVICE_GROUP_INFO, serviceGroupInfoDTOMap, KnConstants.CACHE_EXPIRY_TIME);
                knLogger.info(methodName, " fetched values from GG : " + serviceGroupInfoDTOMap);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SERVICE_GROUP_INFO", null);
        }
        return serviceGroupInfoDTOMap;
    }

    public Map<String, KnServiceGroupInfoDTO> getSystemServiceMapByType(String serviceType) throws KnDAOException {
        String methodName = "getSystemServiceMapByType(String)";
        knLogger.info(methodName, "ENTRY :");
        Map<String, KnServiceGroupInfoDTO> serviceGroupInfoDTOMap;
        try {
            KnCache cache = KnCache.getInstance();
            ICache cacheManager = cache.getCacheManager();
            serviceGroupInfoDTOMap = (Map<String, KnServiceGroupInfoDTO>) cacheManager.get(KnCacheKeys.SERVICE_GROUP_INFO);
            if (serviceGroupInfoDTOMap == null) {
                knLogger.info(methodName, " reading from GG ");
                serviceGroupInfoDTOMap = KnGGCache.systemServiceMapDAO.getSystemServiceMapByType(serviceType);
                //cacheManager.add(KnCacheKeys.SERVICE_GROUP_INFO, serviceGroupInfoDTOMap, KnConstants.CACHE_EXPIRY_TIME);
                knLogger.info(methodName, " fetched values from GG : " + serviceGroupInfoDTOMap);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SERVICE_GROUP_INFO", null);
        }
        return serviceGroupInfoDTOMap;
    }

    // Getting OIDC flag based in CorpLevel/SystemLevel
    public boolean isOIDCApplicable(int corpId, String service) throws KnDAOException {
        String methodName = "isOIDCApplicable(int, String)";
        boolean ctrlUpgrade = false;
        Map<String, KnCorpServiceInfoDTO> corpServiceMap = getCorpServiceType(corpId);
        if(corpServiceMap.containsKey(service)){
            Map<String, KnServiceGroupInfoDTO> systemService = getSystemServiceMapByGrpId(corpServiceMap.get(service).getServiceGrpId());
            if(systemService.containsKey(service)){
                String versionId = systemService.get(service).getServiceVer();
                String[] pv = versionId.split("\\.");
                if(Integer.parseInt(pv[0]) >= CONTROLLED_UPGRADE_VERSION){
                    ctrlUpgrade = true;
                }
            }
        } else {
            Map<String, KnServiceGroupInfoDTO> systemService = getSystemServiceMapByType(service);
            if(systemService.containsKey(service)){
                String versionId = systemService.get(service).getServiceVer();
                String[] pv = versionId.split("\\.");
                if(Integer.parseInt(pv[0]) >= CONTROLLED_UPGRADE_VERSION){
                    ctrlUpgrade = true;
                }
            }
        }
        knLogger.info(methodName, "ctrlUpgrade -- ", ctrlUpgrade);
        return ctrlUpgrade;
    }

    // Getting swVersion based in CorpLevel/SystemLevel
    public String swVersionId(int corpId, String service) throws KnDAOException {
        String methodName = "swVersionId(int, String)";
        String versionId = null;
        Map<String, KnCorpServiceInfoDTO> corpServiceMap = getCorpServiceType(corpId);
        if(corpServiceMap.containsKey(service)){
            Map<String, KnServiceGroupInfoDTO> systemService = getSystemServiceMapByGrpId(corpServiceMap.get(service).getServiceGrpId());
            if(systemService.containsKey(service)){
                versionId = systemService.get(service).getServiceVer();
            }
        } else {
            Map<String, KnServiceGroupInfoDTO> systemService = getSystemServiceMapByType(service);
            if(systemService.containsKey(service)){
                versionId = systemService.get(service).getServiceVer();
            }
        }
        knLogger.info(methodName, "versionId -- ", versionId);
        return versionId;
    }

    public boolean isUnUpgradedPOCSERVER(String pttServerId) throws KnDAOException {
        String methodName = "isUnUpgradedPOCSERVER(String)";
        knLogger.info(methodName, "ENTRY :");
        Map<String, String> envValue = retrieveDefProfile();
        if (envValue != null && envValue.get(BACKWARD_COMPATIBILTY_REQ) != null) {
            if (Integer.parseInt(envValue.get(BACKWARD_COMPATIBILTY_REQ)) == 0) {
                return false;
            }
        }
        boolean status = true;
        Integer count;
        try {
            KnCache cache = KnCache.getInstance();
            ICache cacheManager = cache.getCacheManager();
            count = (Integer) cacheManager.get(KnCacheKeys.UNUPGRADED_POCSERVER_LIST);
            if (count == null) {
                knLogger.info(methodName, " reading from GG ");
                count = KnGGCache.unUpgradedPoCServerListDAO.isUnUpgradedPOCSERVER(pttServerId);
                //cacheManager.add(KnCacheKeys.UNUPGRADED_POCSERVER_LIST, count, KnConstants.CACHE_EXPIRY_TIME);
                knLogger.info(methodName, " fetched values from GG : " + count);
            }
            if(count <= 0) status = false;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "UNUPGRADED_POCSERVER_LIST", null);
        }
        return status;
    }

    public Map<String, String> getOidcClientIDSecret(String key) throws KnDAOException {
        String methodName = "getOidcClientIDSecret()";
        knLogger.info(methodName, "ENTRY :");
        Map<String, String> oidcClientSecretMap;
        try {
            KnCache cache = KnCache.getInstance();
            ICache cacheManager = cache.getCacheManager();
            oidcClientSecretMap = (Map) cacheManager.get(KnCacheKeys.OIDC_CLIENT_ID_SECRET);
            if ((oidcClientSecretMap == null) || !(oidcClientSecretMap.containsKey(key))) {
                knLogger.info(methodName, " reading from GG ");
                oidcClientSecretMap = KnGGCache.oidcClientIdSecretDAO.getOidcClientIDSecret();
                if(!oidcClientSecretMap.isEmpty()){
                    cacheManager.add(KnCacheKeys.OIDC_CLIENT_ID_SECRET, oidcClientSecretMap, KnConstants.CACHE_EXPIRY_TIME);
                }
                knLogger.info(methodName, " fetched values from GG : " + oidcClientSecretMap);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "OIDC_CLIENT_ID_SECRET", null);
        }
        return oidcClientSecretMap;
    }

    public void deleteOldOidcTmpPwd(String mdn) throws KnDAOException {
        String methodName = "deleteOldOidcTmpPwd()";
        knLogger.info(methodName, "ENTRY :");
        try {
            KnGGCache.subscriberAddlInfoDAO.deleteOidcTmpPwd(mdn);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SUBSCRIBERADDLINFO", null);
        }
    }

    public void insertOidcTmpPwd(KnOidcTmpPwdDTO oidcTmpPwdDTO) throws KnDAOException {
        String methodName = "insertOidcTmpPwd()";
        knLogger.info(methodName, "ENTRY :");
        try {
            KnGGCache.subscriberAddlInfoDAO.insertOidcTmpPwd(oidcTmpPwdDTO);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SUBSCRIBERADDLINFO", null);
        }
    }

    public KnOidcTmpPwdDTO selectOidcTmpPwd(String mdn) throws KnDAOException {
        String methodName = "selectOidcTmpPwd()";
        knLogger.info(methodName, "ENTRY :");
        KnOidcTmpPwdDTO oidcTmpPwdDTO = null;
        try {
            oidcTmpPwdDTO = KnGGCache.subscriberAddlInfoDAO.selectOidcTmpPwd(mdn);
            knLogger.info(methodName, " fetched values from GG : " + oidcTmpPwdDTO);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SUBSCRIBERADDLINFO", null);
        }
        return oidcTmpPwdDTO;
    }

    private Map<String, String> retrieveDefProfile() throws KnDAOException {
        Collection<String> keyList = new ArrayList<String>();
        keyList.add(BACKWARD_COMPATIBILTY_REQ);
        return generalUtil.retrieveConfig(keyList);
    }

    public List<String> retriveExpPasswordMDNs(Long time) throws KnDAOException {
        String methodName = "retriveExpPasswordMDNs(Long)";
        knLogger.info(methodName, "ENTRY :");
        List<String> mdns = new ArrayList<>();
        try {
            mdns = KnGGCache.subscriberAddlInfoDAO.retriveExpPasswordMDNs(time);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SUBSCRIBERADDLINFO", null);
        }
        return mdns;
    }

    public int deleteExpPasswordMDNs(List<String> mdns) throws KnDAOException {
        String methodName = "deleteExpPasswordMDNs(List<String>)";
        knLogger.info(methodName, "ENTRY :");
        int deleteCount = 0;
        try {
            deleteCount = KnGGCache.subscriberAddlInfoDAO.deleteExpPasswordMDNs(mdns);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SUBSCRIBERADDLINFO", null);
        }
        return deleteCount;
    }

    public String retrieveKUIDPrefix() throws KnDAOException {
		String methodName = "retrieveKUIDPrefix()";
		knLogger.info(methodName, "ENTRY :");
		String kuidPrefix = null;
		try {
			KnCache cache = KnCache.getInstance();
			ICache cacheManager = cache.getCacheManager();
			kuidPrefix = (String) cacheManager.get(KnCacheKeys.KOD_UID_CONFIG);
			if (kuidPrefix == null) {
				knLogger.info(methodName, " reading from GG ");
				kuidPrefix = KnGGCache.kodUidConfigDAO.retrieveKUIDPrefix();
				cacheManager.add(KnCacheKeys.KOD_UID_CONFIG, kuidPrefix, KnConstants.CACHE_EXPIRY_TIME);
				knLogger.info(methodName, " fetched values from GG : " + kuidPrefix);
			}
		} catch (SQLException e) {
			knLogger.error(methodName, "SQLException occurred - ", e);
			throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "KOD_UID_CONFIG", null);
		}
		return kuidPrefix;
	}

    public KnDefaultMCSClientInfo retrieveDefaultMCSClientInfo() throws KnDAOException {
    	 String methodName = "retriveDefaultPVAndClientFS(List<String>)";
         knLogger.info(methodName, "ENTRY :");
         KnDefaultMCSClientInfo defaultMCSClientInfo = null;
         try {
        	 defaultMCSClientInfo = KnGGCache.knMCSClientInfoDAO.retrieveDefaultMCSClientInfo();
         } catch (SQLException e) {
             knLogger.error(methodName, "SQLException occurred - ", e);
             throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "MCS_CLIENT_INFO", null);
         }
         return defaultMCSClientInfo;
    }

    public void createAsyncJob(KnAsyncJobDTO asyncJobDTO) throws KnDAOException{
        String methodName = "createAsyncJob(KnAsyncJobDTO)";
        try{
            KnGGCache.knAsyncJobNotifyDAO.insert(asyncJobDTO);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    public int deleteAsyncJob(Collection<String> txnIds) throws  KnDAOException{
        String methodName = "deleteAsyncJob(Collection<String>)";
        knLogger.info(methodName,"ETNRY : - ",txnIds);
        int count = 0;
        try{
            count = KnGGCache.knAsyncJobNotifyDAO.delete(txnIds);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return count;
    }

    public void updateAsyncJobStatus(String txnId, int opStatus) throws KnDAOException{
        String methodName = "updateAsyncJobStatus(String,int)";
        knLogger.info(methodName,"ETNRY : - ",txnId,"opStatus - ",opStatus);
        try{
            KnGGCache.knAsyncJobNotifyDAO.updateJobStatus(txnId,opStatus );
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    /**
     * Updates job status to FAILURE with up to GG_MAX_RETRY_COUNT retries and
     * GG_RETRY_SLEEP_MS sleep between attempts. Use this only when marking a job
     * as failed, to avoid the job staying stuck in INPROGRESS if GG is momentarily down.
     */
    public void updateFailedJobStatusWithRetry(String txnId, int opStatus) throws KnDAOException {
        String methodName = "updateFailedJobStatusWithRetry(String,int)";
        knLogger.info(methodName, "ENTRY : txnId - ", txnId, "opStatus - ", opStatus);
        int attempt = 0;
        KnDAOException lastException = null;
        while (attempt < GG_MAX_RETRY_COUNT) {
            attempt++;
            try {
                updateAsyncJobStatus(txnId, opStatus);
                knLogger.info(methodName, "Successfully updated job status on attempt ", attempt);
                return;
            } catch (KnDAOException e) {
                lastException = e;
                knLogger.error(methodName, "Failed to update job status on attempt " + attempt + "/" + GG_MAX_RETRY_COUNT
                        + " txnId - ", txnId);
                if (attempt < GG_MAX_RETRY_COUNT) {
                    try {
                        knLogger.info(methodName, "Retrying after " + GG_RETRY_SLEEP_MS + "ms ...");
                        Thread.sleep(GG_RETRY_SLEEP_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw lastException;
                    }
                }
            }
        }
        knLogger.error(methodName, "All " + GG_MAX_RETRY_COUNT + " retry attempts exhausted for txnId - ", txnId);
        throw lastException;
    }

    public void insertTempStaleRecords(String txnId, int opStatus, List<String> profileMdns, String profileId, int corpId, String payload, int opType) throws KnDAOException {
        String methodName = "insertTempStaleRecords(String,int)";
        try {
            KnGGCache.knAsyncJobNotifyDAO.insertTempStaleRecords(txnId, opStatus, profileMdns, profileId, corpId, payload, opType);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    public List<KnAsyncJobDTO> getAllLongRunningJobsForCorp(String corpId, long time) throws KnDAOException{
        String methodName = "getAllLongRunningJobsForCorp(String,int)";
        List<KnAsyncJobDTO> jobNotifyDTOS = null;
        knLogger.info(methodName,"ETNRY : - corpId",corpId,"time - ",time);
        try{
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getAllLongRunningJobsForCorp(corpId,time );
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }

    public List<KnAsyncJobDTO> getAllLongRunningJobs(long time) throws KnDAOException{
        String methodName = "getAllLongRunningJobs(int)";
        List<KnAsyncJobDTO> jobNotifyDTOS = null;
        knLogger.info(methodName,"ETNRY : - time - ",time);
        try{
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getAllLongRunningJobs(time);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }


    public boolean createAsyncJobTask(KnAsyncJobTaskDTO knAsyncJobTaskDTO) throws KnDAOException{
        String methodName = "createAsyncJobTask(KnUPMJobTaskReqDTO)";
        knLogger.info(methodName, "ENTRY :");
        boolean result = false;
        try{
            result = KnGGCache.knAsyncJobTaskDAO.createAsyncJobTask(knAsyncJobTaskDTO);

        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_TASK", null);
        }
        return result;
    }

    public void createAsyncJobTask(Collection<KnAsyncJobTaskDTO> knAsyncJobTaskDTOS) throws KnDAOException{
        String methodName = "createAsyncJobTask(Collection<KnUPMJobTaskReqDTO>)";
        knLogger.info(methodName,"ETNRY :");
        try{
            KnGGCache.knAsyncJobTaskDAO.createAsyncJobTASK(knAsyncJobTaskDTOS);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_TASK", null);
        }
    }

    public void updateAsyncJobTaskStatus(String taskId,int status) throws KnDAOException{

        String methodName = "updateAsyncJobTaskStatus(String,int)";
        knLogger.info(methodName,"ETNRY : - ",taskId,"task status - ",status);
        try{
            KnGGCache.knAsyncJobTaskDAO.updateJobTaskStatus(taskId,status);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_TASK", null);
        }
    }

    public int deleteAsyncJobTasks(Collection<String> taskIds) throws KnDAOException{
        String methodName = "deleteAsyncJobTasks(Collection<String>)";
        knLogger.info(methodName,"ETNRY : - taskIds",taskIds);
        int count = 0;
        try{
            count = KnGGCache.knAsyncJobTaskDAO.deleteAsyncJObTasks(taskIds);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_TASK", null);
        }
        return count;
    }

    public int getAsyncJobTaskStatus(String taskId) throws KnDAOException{
        String methodName = "getAsyncJobTaskStatus(String)";
        int status = -1;
        knLogger.info(methodName,"ETNRY : - taskId",taskId);
        try{
             status = KnGGCache.knAsyncJobTaskDAO.getTaskStatus(taskId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_TASK", null);
        }
        knLogger.info(methodName,"EXIT: - taskId - ",taskId,"status - ",status);
        return status;
    }


    public Map<String,Integer> getAsyncJobTaskStatusByJobId(String jobId) throws KnDAOException{
        String methodName = "getAsyncJobTaskStatusByJobId(String)";
        knLogger.info(methodName,"ETNRY : - jobId",jobId);
        Map<String,Integer> taskStatusMap = null;
        try{
            taskStatusMap = KnGGCache.knAsyncJobTaskDAO.getTaskStatusByJobId(jobId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_TASK", null);
        }
        knLogger.info(methodName,"EXIT: - taskStatusMap - ",taskStatusMap);
        return taskStatusMap;
    }


    public void updateProfileMdn(String profileMdn,String taskId) throws KnDAOException{

        String methodName = "updateUPMJobTaskStatus(String,int)";
        knLogger.info(methodName,"ETNRY : - profileMdn",KnGDPRTemplate.mdn(profileMdn),"taskId ",taskId);
        try{
            KnGGCache.knAsyncJobTaskDAO.updateProfileMdn(profileMdn,taskId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_TASK", null);
        }
    }

    public String getProfileMdnByTaskId(long taskId) throws KnDAOException{
        String methodName = "getProfileMdnByTaskId()";
        knLogger.info(methodName,"ETNRY : - taskId ",taskId);
        try{
            return KnGGCache.knAsyncJobTaskDAO.getProfileMdnByTaskId(taskId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    public int updateCatTxnStatus(String txnId, int opStatus, String opMsg, int msgStatus) throws KnDAOException{
        String methodName = "updateCATTXNStatus(String,int)";
        knLogger.info(methodName,"ETNRY : - ",txnId,"opStatus - ",opStatus,"opMsg - ",opMsg,"msgStatus - ",msgStatus);
        try{
            return KnGGCache.knCatAsyncTxnInfoDAO.updateTxnStatus(txnId,opStatus,opMsg,msgStatus);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CAT_ASYNC_TXN_INFO", null);
        }
    }
    public List<Integer> getJobStatusByProfileId(String userProfileId) throws KnDAOException{
        String methodName = "getJobStatusByProfileId(String)";
        List<Integer> jobStatusList = null;
        knLogger.info(methodName,"ETNRY : - userProfileId",userProfileId);
        try{
            jobStatusList = KnGGCache.knAsyncJobNotifyDAO.getJobStatusByProfileId(userProfileId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobStatusList;
    }

	public String getRegisterPOCHomeByDeviceIMPI(String deviceIMPI)  throws KnDAOException {
   	 String methodName = "getRegisterPOCHomeByDeviceIMPI(String)";
        knLogger.info(methodName, "ENTRY :");
        String registeredHome = null;
        try {
        	registeredHome = KnGGCache.sipRegistrationStatusDAO.getRegisterPOCHomeByDeviceIMPI(deviceIMPI);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SIPREGISTRATIONSTATUS", null);
        }
        return registeredHome;
	}

    public static void insertPUBContactAddonAliasInfo(HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> contactAddonAliasMapDTO) throws KnDAOException {
        String methodName = "insertPUBContactAddonAliasInfo(HashMap<String,KnPUBContactAddonAliasDTO>)";
        knLogger.info(methodName, "ENTRY :");
        try {
            KnGGCache.addonAliasDAO.insertPUBContactAddonAliasInfo(contactAddonAliasMapDTO);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "GG", "PUB_CONTACT_ADDONALIASIDS", null);
        }
    }


    public static ArrayList<KnPUBContactAddonAliasDTO> getPUBContactAddonAliasInfo(KnPUBContactAddonAliasDTO contactAddonAliasDTO) throws KnDAOException {
        String methodName = "getPUBContactAddonAliasInfo(KnPUBContactAddonAliasDTO)";
        knLogger.info(methodName, "ENTRY :");
        ArrayList<KnPUBContactAddonAliasDTO> contactAddonAliasDTOS;
        try {
            contactAddonAliasDTOS=KnGGCache.addonAliasDAO.getPUBContactAddonAliasInfo(contactAddonAliasDTO);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "GG", "PUB_CONTACT_ADDONALIASIDS", null);
        }
        return contactAddonAliasDTOS;
    }

    public static void deletePUBContactAddonAliasInfo(ArrayList<KnPUBContactAddonAliasDTO> pubContactAddonAliasDTOS) throws KnDAOException {
        String methodName = "deletePUBContactAddonAliasInfo(KnPUBContactAddonAliasDTO)";
        knLogger.info(methodName, "ENTRY :");
        try {
            KnGGCache.addonAliasDAO.deletePUBContactAddonAliasInfo(pubContactAddonAliasDTOS);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "GG", "PUB_CONTACT_ADDONALIASIDS", null);
        }
    }

public List<Integer> getJobStatusByCorpId(int corpId) throws KnDAOException{
        String methodName = "getJobStatusByCorpId(String)";
        List<Integer> jobStatusList = null;
        knLogger.info(methodName,"ETNRY : - corpId",corpId);
        try{
            jobStatusList = KnGGCache.knAsyncJobNotifyDAO.getJobStatusByCorpId(corpId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobStatusList;
    }
    public List<Integer> getJobStatusForWatcherByCorpId(int corpId) throws KnDAOException{
        String methodName = "getJobStatusForWatcherByCorpId(int)";
        List<Integer> jobStatusList = null;
        knLogger.info(methodName,"ETNRY : - corpId",corpId);
        try{
            jobStatusList = KnGGCache.knAsyncJobNotifyDAO.getJobStatusForWatcherByCorpId(corpId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobStatusList;
    }


    public int updateAllAsyncJobStatus(Set<String> txnIds, int opStatus) throws KnDAOException{
        String methodName = "updateAllAsyncJobStatus(Set,int)";
        knLogger.info(methodName,"ETNRY : - ",txnIds,"opStatus - ",opStatus);
        int count = 0;
        try{
            count = KnGGCache.knAsyncJobNotifyDAO.updateAllAsyncJobStatus(txnIds,opStatus );
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return count;
    }

    public List<KnAsyncJobDTO> getJobsByStatus(Collection<Integer> status) throws KnDAOException{
        String methodName = "getJobsByStatus(Collection<Integer>)";
        List<KnAsyncJobDTO> jobNotifyDTOS = null;
        knLogger.info(methodName,"ETNRY : - status - ",status);
        try{
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getJobsByStatus(status);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }

    public LinkedHashMap<Integer, Map<Integer, String>> getStatusCorpIdMap(Collection<Integer> statusList) throws KnDAOException {
        String methodName = "getStatusCorpIdMap()";
        LinkedHashMap<Integer, Map<Integer, String>> jobNotifyDTOS = null;
        try {
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getStatusCorpIdMap(statusList);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }

    public int deleteAsyncJObTasksByTxnIdID(Collection<String> txnIds) throws KnDAOException {
        String methodName = "deleteAsyncJObTasksByTxnIdID(Collection<String>)";
        int count = 0;
        knLogger.info(methodName,"ETNRY : - txnIds - ",txnIds);
        try{
            count = KnGGCache.knAsyncJobTaskDAO.deleteAsyncJObTasksByTxnIdID(txnIds);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return count;
    }

    public LinkedHashMap<String, KnAsyncJobDTO> getNewJobs(int jobsToBePulled, Map<Integer, String> corpIdMap) throws KnDAOException {
        String methodName = "getNewJobs";
        LinkedHashMap<String, KnAsyncJobDTO> jobs = null;
        try {
            jobs = KnGGCache.knAsyncJobNotifyDAO.getNewJobs(jobsToBePulled, corpIdMap);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobs;
    }

    public void updateCATTXNStatus(Collection<String> txnIds, int opStatus,String opMsg,int msgStatus) throws KnDAOException{
        String methodName = "updateCATTXNStatus(Collection,int)";
        knLogger.info(methodName,"ETNRY : - ",txnIds,"opStatus - ",opStatus,"opMsg - ",opMsg,"msgStatus - ",msgStatus);
        try{
            KnGGCache.knCatAsyncTxnInfoDAO.updateTxnStatus(txnIds, opStatus, opMsg, msgStatus);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CAT_ASYNC_TXN_INFO", null);
        }
    }

    public Map<String,String> getOnlineSubcribersListByMdn(List<String> mdnList)  throws KnDAOException {
        String methodName = "getOnlineSubcribersListByMdn()";
        Map<String,String> onlineSubscriber = null;
        try {
            onlineSubscriber = KnGGCache.sipRegistrationStatusDAO.getOnlineSubcribersListByMdn(mdnList);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SIPREGISTRATIONSTATUS", null);
        }
        return onlineSubscriber;
    }
    public List<KnCorpTrustMatrixDTO> getSharedCorpMatrix(String extCorpId) throws KnDAOException{
        String methodName = "getSharedCorpMatrix(String)";
        knLogger.info(methodName,"ETNRY : - ",extCorpId);
        List<KnCorpTrustMatrixDTO> trustMatrixDTOs= null;
        try{
            trustMatrixDTOs =  KnGGCache.trustMatrixDAO.getSharedCorpMatrix(extCorpId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORP_SHARING_TRUST_MATRIX", null);
        }
        return trustMatrixDTOs;
    }

    public KnCorpTrustMatrixDTO getExactCorpTrustMatrixEntry(String extCorpId, String sharedExtCorpId) throws KnDAOException {
        String methodName = "getExactCorpTrustMatrixEntry(String, String)";
        knLogger.info(methodName, "ENTRY extCorpId=", extCorpId, " sharedExtCorpId=", sharedExtCorpId);
        try {
            List<KnCorpTrustMatrixDTO> list = KnGGCache.trustMatrixDAO.getSharedCorpMatrix(extCorpId);
            if (list != null) {
                for (KnCorpTrustMatrixDTO dto : list) {
                    if (sharedExtCorpId.trim().equals(dto.getSharedExtCorpId())) {
                        return dto;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORP_SHARING_TRUST_MATRIX", null);
        }
    }

    public List<KnCorpTrustMatrixDTO> getSharedCorpMatrixBySharedCorpId(String sharedExtCorpId) throws KnDAOException{
        String methodName = "getSharedCorpMatrixBySharedCorpId(String)";
        knLogger.info(methodName,"ETNRY : - ",sharedExtCorpId);
        List<KnCorpTrustMatrixDTO> trustMatrixDTOs= null;
        try{
            trustMatrixDTOs =  KnGGCache.trustMatrixDAO.getSharedCorpMatrixBySharedCorpId(sharedExtCorpId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORP_SHARING_TRUST_MATRIX", null);
        }
        return trustMatrixDTOs;
    }

    public void deleteCorpMatrixByOwnedCorpId(String ownedExtCorpIds) throws KnDAOException{
        String methodName = "deleteCorpMatrixByOwnedCorpId()";
        try{
            KnGGCache.trustMatrixDAO.deleteCorpMatrixByOwnedCorpId(ownedExtCorpIds);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORP_SHARING_TRUST_MATRIX", null);
        }
    }
    public Map<Integer,KnDefaultMCSClientInfo> retrieveMCSClientInfo() throws KnDAOException {
        String methodName = "retrieveMCSClientInfo(List<String>)";
        knLogger.info(methodName, "ENTRY :");
        Map<Integer,KnDefaultMCSClientInfo> mcsClientInfoMap;
        try {
            mcsClientInfoMap = KnGGCache.knMCSClientInfoDAO.retrieveMCSClientInfo();
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "MCS_CLIENT_INFO", null);
        }
        return mcsClientInfoMap;
    }

    public List<KnAsyncJobDTO> getJobsByStatusAndUpdateTime(Collection<Integer> status,long updatedTimeInMs) throws KnDAOException{
        String methodName = "getJobsByStatusAndUpdateTime(Collection<Integer>)";
        List<KnAsyncJobDTO> jobNotifyDTOS = null;
        knLogger.debug(methodName, "input - status", status," updatedTimeInMs - ",updatedTimeInMs);
        try{
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getJobsByStatusAndUpdateTime(status,updatedTimeInMs);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }

    public List<KnAsyncJobDTO> getJobsByTxnIds(Collection<String> txnIds) throws KnDAOException{
        String methodName = "getJobsByTxnIds(Collection<Integer>)";
        List<KnAsyncJobDTO> jobNotifyDTOS = null;
        knLogger.debug(methodName, "input - txnIds", txnIds);
        try{
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getJobsByTxnIds(txnIds);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }

    public String getActivatedClientFS2(Integer cameraType,Integer clientType) throws KnDAOException {
        String methodName = "getActivatedClientFS2(cameraType,clientType)";
        knLogger.info(methodName, "ENTRY :");
       String clientFS2 = null;
        try {
            clientFS2=KnGGCache.activatedClientInfoDAO.getActivatedClientFS2(cameraType,clientType);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "GG", "PRE_ACTIVATED_CLIENTINFO", null);
        }
        return clientFS2;
    }

    public Map<String,Integer> getJobStatusByOperationId(int operationId) throws KnDAOException{
        String methodName = "getJobStatusByOperationId()";
        Map<String,Integer>  jobNotifyDTOS = null;
        try{
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getJobStatusByOperationId(operationId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }

    public Map<String,KnAsyncJobDTO> getJobStatusByOperationIdAndStatus(int operationId,int status) throws KnDAOException{
        String methodName = "getJobStatusByOperationIdAndStatus()";
        Map<String,KnAsyncJobDTO>  jobNotifyDTOS = null;
        try{
            jobNotifyDTOS = KnGGCache.knAsyncJobNotifyDAO.getJobStatusByOperationIdAndStatus(operationId,status);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobNotifyDTOS;
    }

    public void insertUserProfileInfo(KnUserProfileInfoDTO userProfileInfo) throws  KnDAOException{
        String methodName = "insertUserProfileInfo()";
        try{
            KnGGCache.userProfileInfoDAO.insert(userProfileInfo);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    public void updateUserProfileInfo(KnUserProfileInfoDTO userProfileInfo) throws  KnDAOException{
        String methodName = "updateUserProfileInfo()";
        try{
            KnGGCache.userProfileInfoDAO.update(userProfileInfo);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    public void deleteUserProfileInfo(String userProfileId) throws  KnDAOException{
        String methodName = "deleteUserProfileInfo()";
        try{
            KnGGCache.userProfileInfoDAO.delete(userProfileId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    public KnUserProfileInfoDTO getUserProfileInfoByProfileId(String userProfileId) throws  KnDAOException{
        String methodName = "getUserProfileInfoByProfileId()";
        KnUserProfileInfoDTO userProfileInfo=null;
        try{
            userProfileInfo =KnGGCache.userProfileInfoDAO.getUserProfileInfoByProfileId(userProfileId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }

        return userProfileInfo;
    }
    public List<KnCorpTrustMatrixDTO> getSharedCorpMatrixByExtAndSharingFeature(String extCorpId) throws KnDAOException{
        String methodName = "getSharedCorpMatrixByExtAndSharingFeature()";
        List<KnCorpTrustMatrixDTO> trustMatrixDTOs= null;
        try{
            trustMatrixDTOs =  KnGGCache.trustMatrixDAO.getSharedCorpMatrixByExtAndSharingFeature(extCorpId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORP_SHARING_TRUST_MATRIX", null);
        }
        return trustMatrixDTOs;
    }
    public KnCorpGrpLmrExtnDTO getCorpGrpLmrExtn(int corpId,int corpGroupId) throws KnDAOException{
        String methodName = "getCorpGrpLmrExtn(int, int)";
        KnCorpGrpLmrExtnDTO corpGrpLmrExtnDTO= null;
        try{
            corpGrpLmrExtnDTO =  KnGGCache.corpGrpLmrExtnDAO.getCorpGrpLmrExtn(corpId,corpGroupId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORPGRP_LMREXTN", null);
        }
        return corpGrpLmrExtnDTO;
    }

    public void insertGrpLmrExtn(int corpId, int groupId,String ugwConfig) throws KnDAOException{
        String methodName = "insertGrpLmrExtn()";
        try{
            KnGGCache.corpGrpLmrExtnDAO.insertGrpLmrExtn(corpId,groupId,ugwConfig);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORPGRP_LMREXTN", null);
        }
    }

    public void updateGrpLmrExtn(int corpId, int groupId,String ugwConfig) throws KnDAOException{
        String methodName = "updateGrpLmrExtn()";
        try{
            KnGGCache.corpGrpLmrExtnDAO.updateGrpLmrExtn(corpId,groupId,ugwConfig);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORPGRP_LMREXTN", null);
        }
    }

    public void deleteGrpLmrExtn(int corpId, int groupId) throws KnDAOException{
        String methodName = "deleteGrpLmrExtn()";
        try{
            KnGGCache.corpGrpLmrExtnDAO.deleteGrpLmrExtn(corpId,groupId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "CORPGRP_LMREXTN", null);
        }
    }

    public Map<String,Integer> getActiveMdn(String basemdn) throws KnDAOException{
        String methodName = "getActiveMdn()";
        Map<String,Integer> activeMdn=null;
        try{
            activeMdn = KnGGCache.sipRegistrationStatusDAO.getActiveMdn(basemdn);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "get active mdn", null);
        }
        return activeMdn;
    }

    public void deleteStaleAsyncJob(KnAsyncJobDTO asyncJobDTO) throws KnDAOException{
        String methodName = "deleteStaleAsyncJob(KnAsyncJobDTO)";
        try{
            KnGGCache.knAsyncJobNotifyDAO.deleteStaleAsyncJob(asyncJobDTO);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    @Deprecated
    public List<String> getCATStaleTransactionIds() throws KnDAOException{
        String methodName = "getCATStaleTransactionIds()";
        List<String> transactionIds = null;
        try{
            transactionIds = KnGGCache.knAsyncJobNotifyDAO.getCATStaleTransactionIds();
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return transactionIds;
    }

    public List<Integer> getAsyncJobstatusList(String userProfileId, String mdn) throws KnDAOException {
        String methodName = "getAsyncJobstatusList(String,String)";
        List<Integer> statusList = new ArrayList<>();
        try {
            statusList = KnGGCache.knAsyncJobNotifyDAO.getAsyncJobstatusList(userProfileId,mdn);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return statusList;
    }

    public void updateCatTxnStatusGg(String txnId, int opStatus, String opMsg, int msgStatus) throws KnDAOException {
        String methodName = "updateCatTxnStatusGg(String,int,String,int)";
        int retry = 0;
        int count = 0;
        try {
            while (retry < 3) {
                count = updateCatTxnStatus(txnId, opStatus, opMsg, msgStatus);
                if (count == 1) {
                    break;
                }
                knLogger.info(methodName, "count ", count, " retrying after 2s ");
                retry++;
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            knLogger.error(methodName, "InterruptedException while updating updateCATTXNStatusGG - ", e);
        }

    }

    /**
     * Updates CAT transaction status to FAILURE with up to GG_MAX_RETRY_COUNT retries
     * and GG_RETRY_SLEEP_MS sleep between attempts. Use this only when marking a
     * transaction as failed, to avoid the status not being updated if GG is momentarily down.
     */
    public void updateFailedCatTxnStatusWithRetry(String txnId, int opStatus, String opMsg, int msgStatus) throws KnDAOException {
        String methodName = "updateFailedCatTxnStatusWithRetry(String,int,String,int)";
        knLogger.info(methodName, "ENTRY : txnId - ", txnId, "opStatus - ", opStatus);
        int attempt = 0;
        KnDAOException lastException = null;
        while (attempt < GG_MAX_RETRY_COUNT) {
            attempt++;
            try {
                updateCatTxnStatusGg(txnId, opStatus, opMsg, msgStatus);
                knLogger.info(methodName, "Successfully updated CAT txn status on attempt ", attempt);
                return;
            } catch (KnDAOException e) {
                lastException = e;
                knLogger.error(methodName, "Failed to update CAT txn status on attempt " + attempt + "/" + GG_MAX_RETRY_COUNT
                        + " txnId - ", txnId);
                if (attempt < GG_MAX_RETRY_COUNT) {
                    try {
                        knLogger.info(methodName, "Retrying after " + GG_RETRY_SLEEP_MS + "ms ...");
                        Thread.sleep(GG_RETRY_SLEEP_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw lastException;
                    }
                }
            }
        }
        knLogger.error(methodName, "All " + GG_MAX_RETRY_COUNT + " retry attempts exhausted for txnId - ", txnId);
        throw lastException;
    }

    public void insert(String oldFeatureBit, String crntFeatureBit, int status) throws KnDAOException {
        String methodName = "insert(String,String,int)";
        try {
            KnGGCache.knFeatureBitRollBackInfoDAO.insert(oldFeatureBit, crntFeatureBit, status);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "GG", "FEATUREBIT_ROLLBACK_INFO", null);
        }
    }

    public void update(int status) throws KnDAOException {
        String methodName = "update(int)";
        try {
            KnGGCache.knFeatureBitRollBackInfoDAO.update(status);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "GG", "FEATUREBIT_ROLLBACK_INFO", null);
        }
    }

    public void delete() throws KnDAOException {
        String methodName = "delete(String,String,int)";
        try {
            KnGGCache.knFeatureBitRollBackInfoDAO.delete();
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "GG", "FEATUREBIT_ROLLBACK_INFO", null);
        }
    }

    public Collection<String> getStaleTransactionInAsyncTask() throws KnDAOException {
        String methodName = "getStaleTransactionInAsyncTask()";
        Collection<String> transactionIds;
        try {
            transactionIds = KnGGCache.knAsyncJobTaskDAO.getStaleTransactionInAsyncTask();
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return transactionIds;
    }

    public void deleteStaleFromGG(String userProfileId) throws SQLException {
        final String methodName = "deleteStaleFromGG";
        knLogger.debug(methodName, "UserProfileId:", userProfileId);
        try {
            KnGGCache.knAsyncJobNotifyDAO.deleteStaleFromGG(userProfileId);
        } catch (SQLException e) {
            knLogger.error("SQLException Occurred: ",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public LinkedHashMap<String, KnAsyncJobDTO> getRetryJobs(int jobsToBePulled) throws KnDAOException {
        String methodName = "getRetryJobs";
        LinkedHashMap<String, KnAsyncJobDTO> jobs = null;
        knLogger.info(methodName, "ETNRY : ");
        try {
            jobs = KnGGCache.knAsyncJobNotifyDAO.getRetryJobs(jobsToBePulled);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return jobs;
    }

    public LinkedHashMap<String,KnAsyncJobDTO> getWatchersDetails(int jobsToBePulled, Map<Integer, String> corpIdMap) throws KnDAOException {
        String methodName = "getWatchersDetails";
        LinkedHashMap<String,KnAsyncJobDTO> knAsyncJobDTOS = null;
        try {
            knAsyncJobDTOS = KnGGCache.knAsyncJobNotifyDAO.getWatchersDetails(jobsToBePulled, corpIdMap);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return knAsyncJobDTOS;
    }

    public int updateWatcherState(Set<String> txnIds, int opStatus) throws KnDAOException {
        String methodName = "updateWatecherState(Set,int)";
        int count = 0;
        try {
            count = KnGGCache.knAsyncJobNotifyDAO.updateWatcherState(txnIds, opStatus);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return count;
    }

    public List<KnAsyncJobDTO> getWatchersDetailsRunningjobs(long timeInMs) throws KnDAOException {
        String methodName = "getWatchersDetailsRunningjobs";
        List<KnAsyncJobDTO> knAsyncJobDTOS = null;
        try {
            knAsyncJobDTOS = KnGGCache.knAsyncJobNotifyDAO.getWatchersDetailsRunningjobs(timeInMs);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return knAsyncJobDTOS;
    }

    public Map<String, String> getInProgressJobs(int opStatus) throws KnDAOException {
        String methodName = "updateAsyncJobStatus(String,int)";
        Map<String, String> transactionIds = new HashMap<>();
        try {
            transactionIds = KnGGCache.knAsyncJobNotifyDAO.getInProgressJobs(opStatus);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return transactionIds;
    }

    public void deleteCompletedCorpIdFromGG(List<Integer> corpId) throws KnDAOException{
        String methodName = "deleteCompletedCorpIdFromGG()";
        try{
            KnGGCache.knAsyncJobNotifyDAO.deleteCompletedCorpIdFromGG(corpId);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
    }

    public int getTransactionCountBasedOnStatus() throws KnDAOException {
        String methodName = "getTransactionCountBasedOnStatus";
        int count = 0;
        try {
            count = KnGGCache.knAsyncJobNotifyDAO.getTransactionCountBasedOnStatus();
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "ASYNC_JOB_NOTIFY", null);
        }
        return count;
    }

    public boolean isClientInEmergency(String mdn) throws KnDAOException {
        String methodName = "isClientInEmergency(String)";
        knLogger.info(methodName, "ENTRY :");
        boolean clientInEmerg = false;
        try {
            clientInEmerg = KnGGCache.subscriberAddlInfoDAO.isClientInEmergency(mdn);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", "XDM", "SUBSCRIBERADDLINFO", null);
        }
        knLogger.debug(methodName, "Exit clientInEmerg :", clientInEmerg);
        return clientInEmerg;
    }

}
