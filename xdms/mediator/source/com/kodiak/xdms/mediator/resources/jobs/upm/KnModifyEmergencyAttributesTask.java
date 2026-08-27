/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.*;
import com.kodiak.common.commdto.request.KnCorpSubsEmergencyRequestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpModifyUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDestinationAttributeDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsEmergencyConfigDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnModifyEmergencyAttributesTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyEmergencyAttributesTask.class);

    private KnXDMCorpMediator corpMediator;
    private IProvClientIntf provClientIntf;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;

    private String corpId;
    private String payLoad;
    private KnPersisterTxn modifyUpmTranxn;
    private String profileMdn;

    public KnModifyEmergencyAttributesTask(String corpId, String payLoad
            ,KnPersisterTxn modifyUpmTranxn,String profileMdn){
        corpMediator = KnXDMCorpMediator.getInstance();
        provClientIntf = KnProvClientImpl.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();

        this.corpId=corpId;
        this.payLoad=payLoad;
        this.modifyUpmTranxn=modifyUpmTranxn;
        this.profileMdn=profileMdn;
    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName="KnModifyEmergencyAttributesTask()";
        KnPersisterTxn emergencyAttrTxn =modifyUpmTranxn;
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        KnTaskResult taskResult=new KnTaskResult();
        knLogger.info(methodName,"ENTRY::");
        try{
            knLogger.debug(methodName,"profileMdn ", KnGDPRTemplate.mdn(profileMdn));

            KnIPUserProfileDTO ipUserProfilePermDTO = KnCorpCommonInfoUtil.jsonToObject(payLoad, KnIPUserProfileDTO.class);
            KnCorpModifyUserProfileDTO modifyUpmReq = ipUserProfilePermDTO.getModifiedUserProfileDTO();
            knLogger.debug(methodName, "modifyUpmReq:  ", modifyUpmReq);
            KnXDMCorpUserProfileRespDTO userProfile = new KnXDMCorpUserProfileRespDTO();
            KnSubsEmergencyConfigDTO emergencyConfig = modifyUpmReq.getEmergencyConfig();
            knLogger.debug(methodName, "emergencyConfig:  ", emergencyConfig);
            KnXDMCorpUserProfileDTO xdmUserProfile = new KnXDMCorpUserProfileDTO();
            if (emergencyConfig != null && emergencyConfig.getDestType() != null && profileMdn != null) {
                KnCorpSubsEmergencyRequestDTO xdmRequestDTO = new KnCorpSubsEmergencyRequestDTO();
                KnXDMEmergencyConfig emergencyAttributes = new KnXDMEmergencyConfig();
                //KnUserEmergencyAttributes emergencyAttributes = new KnUserEmergencyAttributes();
                userProfile.setMdn(profileMdn);
                emergencyAttributes.setEmergCallType(String.valueOf(emergencyConfig.getCallType()));
                emergencyAttributes.setEmergCancelPermission(String.valueOf(emergencyConfig.getCancelPermission()));
                if (null != emergencyConfig.getOrigBitset()) {
                    emergencyAttributes.setEmergOriginBitSet(String.valueOf(emergencyConfig.getOrigBitset()));
                }
                if (null != emergencyConfig.getTermBitset()) {
                    emergencyAttributes.setEmergTermBitSet(String.valueOf(emergencyConfig.getTermBitset()));
                }
                if (null != emergencyConfig.getLmrBehavior()) {
                    emergencyAttributes.setEmergLMRBehavior(String.valueOf(emergencyConfig.getLmrBehavior()));
                }
                emergencyAttributes.setEmergInitPermission(String.valueOf(emergencyConfig.getPermission()));
                if (null != emergencyConfig.getEmergConfigTimer()) {
                    emergencyAttributes.setEmergConfigTimer(emergencyConfig.getEmergConfigTimer());
                }
                //to avoid null check in for
                Set<KnDestinationAttributeDTO> destAttributes = new HashSet<>();
                destAttributes = emergencyConfig.getDestAttributes();
                //1 - User Selected Destination, 2 - Cat/Admin Configured Destination
                emergencyAttributes.setEmergDestType(String.valueOf(emergencyConfig.getDestType()));
                Set<KnXDMEmergencyDestAttributes> xdmemergDestAttributes = new HashSet<>();
                if (destAttributes != null && !destAttributes.isEmpty()) {
                    for (KnDestinationAttributeDTO emgDestAttr : destAttributes) {
                        if (emgDestAttr != null && emgDestAttr.getDestCat() != null) {
                            KnXDMEmergencyDestAttributes xdmEmergencyDestAttribute = new KnXDMEmergencyDestAttributes();
                            //DestCategory 1 - Primary, 2 - Secondary
                            if (emgDestAttr.getDestCat().equals(1)) {
                                //DestURI GroupId or ContactId
                                xdmEmergencyDestAttribute.setDestAttributeCategory("1");
                                xdmEmergencyDestAttribute.setDestAttributeUri(emgDestAttr.getDestURI());
                            } else {
                                xdmEmergencyDestAttribute.setDestAttributeCategory("2");
                                xdmEmergencyDestAttribute.setDestAttributeUri(emgDestAttr.getDestURI());
                            }
                            xdmemergDestAttributes.add(xdmEmergencyDestAttribute);
                            //DestType 1 - Group, 2 - Contact
                            //emgDestAttr.getDestType();
                        }
                    }
                }
                userProfile.setProfileMdn(profileMdn);
                emergencyAttributes.setEmergDestAttributes(xdmemergDestAttributes);
                xdmUserProfile.setEmergencyAttributes(emergencyAttributes);
                userProfile.setUserProfileInfo(xdmUserProfile);
                userProfile.setCorpId(corpId);
                knLogger.debug(methodName, "userProfile :", userProfile);
                KnCorpResponseDTO libRespDto = corpMediator.setUserProfileEmergencyAttributes(userProfile, emergencyAttrTxn);
                KnNotificationParamDTO notificationParamDTO = new KnNotificationParamDTO();
                notificationParamDTO.setPriority(KnConstants.NOTIFICATION_PRIORITY.HIGH.value());
                       /* if (libRespDto.isProfileChanged()) {
                            KnOPProvDTO provRespDTO = provClientIntf.sendConfigDocNotification(xdmRequestDTO.getEmergencyAttributes().getMdn(), emergencyAttrTxn);
                            commonMediator.sendXcapNotification(provRespDTO.getDirChgDTO(), null);
                        }*/
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_SET_EMERGENCY_ATTRIBUTES_REQ_SUCC);
                //MINT-20079 Change in emergency attributes check is added. If no emergency attributes are changed then suppressing kn-emergency-config notification.
                /*if (libRespDto.isEmergencyAttributesChanged()) {
                    Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(libRespDto);
                    boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, emergencyAttrTxn, notificationParamDTO);
                    knLogger.info(methodName, "Emergency attribute change notification sent - ", isNotified);
                }
                taskResult.setEmergencyEtagToBeUpdated(true);
                knLogger.debug(methodName, "libRespDto: ", libRespDto);
                }*/
                taskResult.setEmergencyEtagToBeUpdated(true);
                knLogger.debug(methodName, "libRespDto: ", libRespDto);
            }
            knLogger.info(methodName,"Done settting emergency attribute");
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed assign upm ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "ModifyEmergencyAttributes Task", KnAuditHelper.STATUS.FAILURE, "ModifyEmergencyAttributes request failed"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "ModifyEmergencyAttributes Task", KnAuditHelper.STATUS.FAILURE, "ModifyEmergencyAttributes request failed"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;
    }

}