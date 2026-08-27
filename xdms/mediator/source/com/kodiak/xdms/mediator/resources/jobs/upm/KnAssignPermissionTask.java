/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.KnTargetMdnPermissionBitInfo;
import com.kodiak.common.commdto.request.KnCorpSubMCPTTInfoRequestDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.helper.KnXDMProvMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileMCPTTConfig;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnAssignPermissionTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAssignPermissionTask.class);

    private KnXDMCorpMediator corpMediator;
    private KnXDMProvMediator provMediator;
    private String corpId;
    private String profileMdn;
    private String userProfileId;
    private KnCorpResponseDTO userProfile;
    private KnPersisterTxn assignSubsTxn;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;

    public KnAssignPermissionTask(String profileMdn, String corpId, String userProfileId
        ,KnCorpResponseDTO userProfile,KnPersisterTxn assignSubsTxn){
        corpMediator = KnXDMCorpMediator.getInstance();
        provMediator = KnXDMProvMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();

        this.profileMdn=profileMdn;
        this.corpId=corpId;
        this.userProfileId=userProfileId;
        this.userProfile=userProfile;
        this.assignSubsTxn=assignSubsTxn;
    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName="setTargetPermTask_executeTask()";
        KnPersisterTxn targetPermissionTxn =assignSubsTxn;
        KnTaskResult taskResult=new KnTaskResult();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        try{
            knLogger.info(methodName, "Setting target perm profileMdn :", KnGDPRTemplate.mdn(profileMdn), " corpId :", corpId);
            knLogger.debug(methodName," userProfileId :",userProfileId);
            KnCorpSubMCPTTInfoRequestDTO knCorpSubMCPTTInfoRequestDTO = new KnCorpSubMCPTTInfoRequestDTO();

            Collection<KnTargetMdnPermissionBitInfo> targetMdnPermBitInfos = new HashSet<>();
            Collection<KnTargetMdnPermissionBitInfo> targetProfileMdnPermBitInfos = new HashSet<>();

            KnCorpResponseDTO userProfileDetails = userProfile;

            Set<KnCorpUserProfileMCPTTConfig> userProfileMcpttConfigs =  userProfileDetails.getUserProfile().getMcpttPermissionsConfig();
            knLogger.debug(methodName," userProfileMcpttConfigs: ",userProfileMcpttConfigs);
            Set<KnCorpUserProfileMCPTTConfig> mcpttPermsProfileMdns=new HashSet<>();
            //adding profile mdns
            if(userProfileMcpttConfigs!=null&!userProfileMcpttConfigs.isEmpty()){
                for(KnCorpUserProfileMCPTTConfig tu:userProfileMcpttConfigs){
                    List<String> profileMdns = provMediator.getMdnForUPM(tu.getMdn(), targetPermissionTxn);
                    profileMdns.remove(tu.getMdn());
                    for(String profileMdn:profileMdns){
                        mcpttPermsProfileMdns.add(new KnCorpUserProfileMCPTTConfig(profileMdn,tu.getPermBitSet()));
                    }
                }
            }
            knLogger.debug(methodName,"mcpttPermsProfileMdns :",mcpttPermsProfileMdns);
            if(!userProfileMcpttConfigs.isEmpty()) {
                //mcptt config for base mdn
                userProfileMcpttConfigs.forEach(userProfileMCPTTConfig -> {
                    KnTargetMdnPermissionBitInfo knTargetMdnPermBitInfo = new KnTargetMdnPermissionBitInfo();
                    BitSet bitSet = KnGeneralUtil.convertLongToBitSet(userProfileMCPTTConfig.getPermBitSet());
                    knTargetMdnPermBitInfo.setAmbientListening(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setDiscreteListening(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setUserCheck(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERCHECK.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setUserEnable(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setEmergPermission(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setMcVideoUnConfirmedPull(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setMdn(userProfileMCPTTConfig.getMdn());
                    targetMdnPermBitInfos.add(knTargetMdnPermBitInfo);

                });
                //mcptt config for profile mdn
                mcpttPermsProfileMdns.forEach(profileMdnMcpttConfig -> {
                    KnTargetMdnPermissionBitInfo knTargetMdnPermBitInfo = new KnTargetMdnPermissionBitInfo();
                    BitSet bitSet = KnGeneralUtil.convertLongToBitSet(profileMdnMcpttConfig.getPermBitSet());
                    knTargetMdnPermBitInfo.setAmbientListening(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setDiscreteListening(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setUserCheck(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERCHECK.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setUserEnable(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setEmergPermission(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setMcVideoUnConfirmedPull(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value()) ? 1 : 0);
                    knTargetMdnPermBitInfo.setMdn(profileMdnMcpttConfig.getMdn());
                    targetProfileMdnPermBitInfos.add(knTargetMdnPermBitInfo);
                });
                //removing duplicate perm mdn entries,As CBS sometimes has duplicate perm base mdn.
                Set<KnTargetMdnPermissionBitInfo> targetBaseMdn = new HashSet<>(targetMdnPermBitInfos.stream()
                        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(KnTargetMdnPermissionBitInfo::getMdn)))));

                Set<KnTargetMdnPermissionBitInfo> targetProfileMdn = new HashSet<>(targetProfileMdnPermBitInfos.stream()
                        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(KnTargetMdnPermissionBitInfo::getMdn)))));

                knCorpSubMCPTTInfoRequestDTO.setCorpId(corpId);
                knCorpSubMCPTTInfoRequestDTO.setAuthorizedMdn(profileMdn);
                knCorpSubMCPTTInfoRequestDTO.setTargetMdnPermissionBitInfoList(targetBaseMdn);
                knCorpSubMCPTTInfoRequestDTO.setTargetProfileMdnPermissionBitInfoList(targetProfileMdn);
                knCorpSubMCPTTInfoRequestDTO.setUpmCall(true);
                knLogger.debug(methodName, "calling set Target Permissions", knCorpSubMCPTTInfoRequestDTO);
                KnCorpResponseDTO assignPermissionResp = corpMediator.setBulkTargetPermissions(knCorpSubMCPTTInfoRequestDTO, targetPermissionTxn);
                knLogger.debug(methodName," assignPermissionResp :",assignPermissionResp);
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == assignPermissionResp.getStatus()) {
                    knLogger.info(methodName, "assign upm permission Operation Failed:",profileMdn);
                    throw new KnXDMServerException(assignPermissionResp.getStatusCode(), assignPermissionResp.getMessage());
                }
                knLogger.info(methodName, "Done with Set Target Permission for profile mdn :", KnGDPRTemplate.mdn(profileMdn));
            }
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed assign upm permission :", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignPersmission Task", KnAuditHelper.STATUS.FAILURE, "AssignPersmission Task request failed"+":"+userProfile.getUserProfileId()+":"+e.getErrorCode());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred while assigning upm permission:", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignPersmission Task", KnAuditHelper.STATUS.FAILURE, "AssignPersmission Task request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;

    }
}
