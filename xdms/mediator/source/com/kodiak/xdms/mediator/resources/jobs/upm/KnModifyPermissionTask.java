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
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAuthUserPermissionInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpModifyUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileMCPTTConfig;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnTargetMdnPermBitInfo;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnModifyPermissionTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyPermissionTask.class);

    private KnXDMCorpMediator corpMediator;
    private KnXDMProvMediator provMediator;

    private String corpId;
    private String payLoad;
    private KnPersisterTxn modifyUpmTranxn;
    private String profileMdn;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;
    private ICorpClientIntf corpClientIntf;

    public KnModifyPermissionTask(String corpId,String payLoad
            ,KnPersisterTxn modifyUpmTranxn,String profileMdn){
        corpMediator = KnXDMCorpMediator.getInstance();
        provMediator = KnXDMProvMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        corpClientIntf = new KnCorpClientImpl();

        this.corpId=corpId;
        this.payLoad=payLoad;
        this.modifyUpmTranxn=modifyUpmTranxn;
        this.profileMdn=profileMdn;
    }


    @Override
    public KnTaskResult executeTask() {
        final String methodName="KnModifyPermissionTask()";
        KnPersisterTxn targetPermissionTxn =modifyUpmTranxn;
        KnTaskResult taskResult=new KnTaskResult();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        try{
            knLogger.info(methodName, "profileMdn ", KnGDPRTemplate.mdn(profileMdn));

            KnCorpSubMCPTTInfoRequestDTO knCorpSubMCPTTInfoRequestDTO = new KnCorpSubMCPTTInfoRequestDTO();

            KnIPUserProfileDTO ipUserProfilePermDTO=KnCorpCommonInfoUtil.jsonToObject(payLoad, KnIPUserProfileDTO.class);
            KnCorpModifyUserProfileDTO modifyUpmReq=ipUserProfilePermDTO.getModifiedUserProfileDTO();

            knLogger.debug(methodName,"modifyUpmReq permission:  ",ipUserProfilePermDTO);

            Set<KnCorpUserProfileMCPTTConfig> modifyMcpttPermConfigs=new HashSet<>();
            modifyMcpttPermConfigs.addAll(modifyUpmReq.getAddedMcpttPermissionsConfig());
            modifyMcpttPermConfigs.addAll(modifyUpmReq.getModifiedPermissionsConfig());
            modifyMcpttPermConfigs.addAll(modifyUpmReq.getRemovedMcpttPermissionsConfig());

            knLogger.debug(methodName,"modifyMcpttPermConfigs:",modifyMcpttPermConfigs);
            //adding profile mdns
            Set<KnCorpUserProfileMCPTTConfig> mcpttPermsProfileMdns=new HashSet<>();
            if(!modifyMcpttPermConfigs.isEmpty()){
                for(KnCorpUserProfileMCPTTConfig tu:modifyMcpttPermConfigs){
                    List<String> profileMdns = provMediator.getMdnForUPM(tu.getMdn(), targetPermissionTxn);
                    profileMdns.remove(tu.getMdn());
                    for(String profileMdn:profileMdns){
                        mcpttPermsProfileMdns.add(new KnCorpUserProfileMCPTTConfig(profileMdn,tu.getPermBitSet()));
                    }
                }
            }
            knLogger.debug(methodName,"mcpttPermsProfileMdns :",mcpttPermsProfileMdns);
            if (!modifyMcpttPermConfigs.isEmpty() && profileMdn != null) {
                Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new HashSet<>();
                Collection<KnTargetMdnPermBitInfo> targetProfileMdnPermBitInfos = new HashSet<>();
                    //mcptt config for base mdn
                    modifyMcpttPermConfigs.forEach(userProfileMCPTTConfig -> {
                        KnTargetMdnPermBitInfo knTargetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
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
                        KnTargetMdnPermBitInfo knTargetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
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
                    knCorpSubMCPTTInfoRequestDTO.setUpmCall(true);
                    knLogger.debug(methodName, "calling set Target Permissions", knCorpSubMCPTTInfoRequestDTO);
                KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
                ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(corpId));
                ipAuthUserPermissionInfoDTO.setAuthorizedMdn(profileMdn);
                ipAuthUserPermissionInfoDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
                ipAuthUserPermissionInfoDTO.setTargetProfileMdnPermissionBitInfoList(targetProfileMdnPermBitInfos);
                ipAuthUserPermissionInfoDTO.setUpmCall(true);
                knLogger.debug(methodName, "setTargetPermissionsAssignUpmCall");
                KnCorpResponseDTO targetPermResponse = corpClientIntf.setBulkTargetPermissions(ipAuthUserPermissionInfoDTO, targetPermissionTxn);
                knLogger.debug(methodName, "Done with Set Target Permission targetPermResponse", targetPermResponse);
                taskResult.setPermissionEtagToBeUpdated(true);
            }
            taskResult.setTaskStatus(STATUS_SUCCESS);
            knLogger.info(methodName,"Done modifying permission");
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed Modify Permission upm ", profileMdn);
            knLogger.error(methodName, "Failed Failed Modify Permission upm ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "Modify PermissionTask", KnAuditHelper.STATUS.FAILURE, "Modify PermissionTask request failed"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed Modify Permission upm ", profileMdn);
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "Modify PermissionTask", KnAuditHelper.STATUS.FAILURE, "Modify PermissionTask request failed"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;
    }

}
