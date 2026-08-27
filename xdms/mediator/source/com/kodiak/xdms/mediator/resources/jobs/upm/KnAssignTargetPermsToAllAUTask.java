/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.KnTargetMdnPermissionBitInfo;
import com.kodiak.common.commdto.request.KnCorpSubMCPTTInfoRequestDTO;
import com.kodiak.common.commdto.request.KnCorpSubsResquestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpAuthUserPermissionRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.ArrayList;
import java.util.Collection;

import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnAssignTargetPermsToAllAUTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAssignTargetPermsToAllAUTask.class);

    private String corpId;
    private String profileMdn;
    private String baseMdn;
    private KnPersisterTxn assignSubsTxn;

    private KnXDMCorpMediator corpMediator;

    public KnAssignTargetPermsToAllAUTask(String profileMdn, String corpId
            ,String baseMdn,KnPersisterTxn assignSubsTxn){
        this.profileMdn=profileMdn;
        this.corpId=corpId;
        this.baseMdn=baseMdn;
        this.assignSubsTxn=assignSubsTxn;

        corpMediator = KnXDMCorpMediator.getInstance();
    }

    @Override
    public KnTaskResult executeTask() {

        final String methodName="setPermissionToAllAU_executeTask()";
        KnPersisterTxn permAUTxn =assignSubsTxn;
        KnTaskResult taskResult=new KnTaskResult();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        try{
            knLogger.info(methodName," baseMdn:", KnGDPRTemplate.mdn(baseMdn)," corpId",corpId," profileMdn:",KnGDPRTemplate.mdn(profileMdn));

            KnCorpSubMCPTTInfoRequestDTO knCorpSubMCPTTInfoRequestDTO = new KnCorpSubMCPTTInfoRequestDTO();

            KnCorpSubsResquestDTO xdmRequestDTO =new KnCorpSubsResquestDTO();
            xdmRequestDTO.setCorpId(corpId);
            xdmRequestDTO.setSubscriberMdn(baseMdn);
            xdmRequestDTO.setUpmFlag(true);
            //getting all AU for TU
            KnXDMCorpAuthUserPermissionRespDTO authorizedMdnList = corpMediator.getAllAuthorizedMdnList(xdmRequestDTO, permAUTxn);
            Collection<KnTargetMdnPermissionBitInfo> auList = authorizedMdnList.getTargetMdnPermissionBitInfo();
            knLogger.debug(methodName," authorizedMdnList :",auList);

            if(auList!=null&&!auList.isEmpty()){
            for(KnTargetMdnPermissionBitInfo au:auList){
                Collection<KnTargetMdnPermissionBitInfo> targetMdnPermBitInfos=new ArrayList<>();
                KnTargetMdnPermissionBitInfo knTargetMdnPermBitInfo = new KnTargetMdnPermissionBitInfo();
                knTargetMdnPermBitInfo.setAmbientListening(au.getAmbientListening());
                knTargetMdnPermBitInfo.setDiscreteListening(au.getDiscreteListening());
                knTargetMdnPermBitInfo.setUserCheck(au.getUserCheck());
                knTargetMdnPermBitInfo.setUserEnable(au.getUserEnable());
                knTargetMdnPermBitInfo.setEmergPermission(au.getEmergPermission());
                knTargetMdnPermBitInfo.setMcVideoUnConfirmedPull(au.getMcVideoUnConfirmedPull());
                knTargetMdnPermBitInfo.setMdn(profileMdn);
                targetMdnPermBitInfos.add(knTargetMdnPermBitInfo);

                knCorpSubMCPTTInfoRequestDTO.setCorpId(corpId);
                knCorpSubMCPTTInfoRequestDTO.setAuthorizedMdn(au.getMdn());
                knCorpSubMCPTTInfoRequestDTO.setTargetMdnPermissionBitInfoList(new ArrayList<>());
                knCorpSubMCPTTInfoRequestDTO.setTargetProfileMdnPermissionBitInfoList(targetMdnPermBitInfos);
                knCorpSubMCPTTInfoRequestDTO.setUpmCall(true);
                knCorpSubMCPTTInfoRequestDTO.setAllAuTask(true);
                knLogger.debug(methodName, "setting all AU with profile mdn as TU:", knCorpSubMCPTTInfoRequestDTO);
                KnCorpResponseDTO assignPermissionToAUResp =corpMediator.setBulkTargetPermissions(knCorpSubMCPTTInfoRequestDTO, permAUTxn);
                    if (KnConstants.RESPONSE_STATUS.FAILURE.value() == assignPermissionToAUResp.getStatus()) {
                        knLogger.debug(methodName, "assign permission to all AU Operation Failed");
                        throw new KnXDMServerException(assignPermissionToAUResp.getStatusCode(), assignPermissionToAUResp.getMessage());
                    }
                }
                knLogger.info(methodName, "Done with Set Target Permission for all AU's");
            }
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed assign upm ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignTargetPermsToAllAUTask Task", KnAuditHelper.STATUS.FAILURE, "AssignTargetPermsToAllAUTask Task request failed"+e.getErrorCode());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignTargetPermsToAllAUTask Task", KnAuditHelper.STATUS.FAILURE, "AssignTargetPermsToAllAUTask Task request failed"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;
    }

}
