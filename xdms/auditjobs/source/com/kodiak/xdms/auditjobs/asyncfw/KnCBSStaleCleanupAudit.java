/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.auditjobs.asyncfw;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpUserProfileUtil;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpModifyUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileMCPTTConfig;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Audit that runs for every 12hrs and finds all stale jobs that are given in GG and do the cleanup for corresponding
 * userprofile
 */
public class KnCBSStaleCleanupAudit extends KnAbstractJob {

    private static final long serialVersionUID = 1L;
    private static final KnLogger knLogger = KnLogger.getLogger(KnCBSStaleCleanupAudit.class);
    private KnGeneralCacheUtil cacheUtil = KnGeneralCacheUtil.getInstance();
    private KnCorpUserProfileUtil userProfileUtil = new KnCorpUserProfileUtil();

    /**
     * @return
     * @throws KnJobSchedulerException
     */
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        knLogger.info(methodName, "ENTRY: Start of CBS stale cleanup job ");
        KnGeneralCacheUtil generalCacheUtil = new KnGeneralCacheUtil();
        try {

            //Fetch all stales jobs
            //build upm object
            // call similar modify upm Task

            List<Integer> jobStatus = new ArrayList<>();
            jobStatus.add(KnConstants.UPM_JOB_STATUS.STALE.Value());

            List<KnAsyncJobDTO> staleJobs = cacheUtil.getJobsByStatus(jobStatus);
            Set<String> txnIds = staleJobs.stream().map(KnAsyncJobDTO::getTxnId).collect(Collectors.toSet());
            knLogger.info(methodName, "staleJobs " + staleJobs);
            knLogger.info(methodName, "txnIds " + txnIds);
            for (KnAsyncJobDTO staleJob : staleJobs) {

                String payLoad = staleJob.getPayLoad();
                String userProfileId = staleJob.getUserProfileId();
                String txnId = staleJob.getTxnId();
                int corpId = staleJob.getCorpId();

                KnCorpUserProfileDTO userProfileDTO = KnCorpCommonInfoUtil.jsonToObject(payLoad, KnCorpUserProfileDTO.class);
                knLogger.debug(methodName," stale user profile - ",userProfileDTO);
                if (userProfileDTO != null) {
                    KnCorpModifyUserProfileDTO modifyUserProfileDTO = new KnCorpModifyUserProfileDTO();
                    Set<String> removeGroupIds = userProfileDTO.getGroupList().stream().map(KnCorpGroupListInfoDTO::getGroupID).map(String::valueOf).collect(Collectors.toSet());
                    if (removeGroupIds != null && !removeGroupIds.isEmpty()) {
                        knLogger.debug(methodName, "userprofileid", userProfileId, "removedGroupIds", removeGroupIds);
                        modifyUserProfileDTO.setRemovedGroupIdsList(removeGroupIds);
                    }

                    if (userProfileDTO.getContactListID() != null) {
                        knLogger.debug(methodName, "userprofileid", userProfileId, "set contactList Id to NULL", userProfileDTO.getContactListID());
                        modifyUserProfileDTO.setContactListID(-1);
                    }

                    Set<KnCorpUserProfileMCPTTConfig> mcpttConfigs = userProfileDTO.getMcpttPermissionsConfig();
                    if (mcpttConfigs != null && !mcpttConfigs.isEmpty()) {
                        knLogger.debug(methodName, "userprofileid", userProfileId, "mcpttConfigs", mcpttConfigs);
                        modifyUserProfileDTO.setRemovedMcpttPermissionsConfig(mcpttConfigs);
                    }

                    Set<KnCorpUserProfileMCPTTConfig> mcpttConfigsforCB = userProfileDTO.getMcpttPermissionsConfigIncb();
                    if (mcpttConfigsforCB != null && !mcpttConfigsforCB.isEmpty()) {
                        knLogger.debug(methodName, "userprofileid", userProfileId, "mcpttConfigs", mcpttConfigsforCB);
                        modifyUserProfileDTO.setAddedMcpttPermissionsConfig(mcpttConfigsforCB);
                    }

                    knLogger.debug(methodName, "updating userProfile", modifyUserProfileDTO);
                    try {
                        userProfileUtil.updateUserProfile(String.valueOf(corpId), userProfileId, modifyUserProfileDTO, null, null);
                    } catch (DocumentNotFoundException ex) {
                        knLogger.error(methodName, "DocumentNotFoundException:", ex);
                        generalCacheUtil.deleteStaleFromGG(userProfileId);
                    }
                    cacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
                }

            }
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "Exception while cleaning up user profile", e);
        } catch (KnDAOException | SQLException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            knLogger.error(methodName, e);
        }
        return false;


    }

    public String getCronExpression() {
        String methodName = "getCronExpression()";
        knLogger.info(methodName, "ENTRY: Get Cron Expression - ");
        // timer is set to every 60 minute
       // String cronExpression = "0,10,20,30,40,50,59 * * * * * *";
        String timer = "2";
        String cronExpression = "0 */" + timer + " * ? * *";
        knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
        return cronExpression;

    }
}
