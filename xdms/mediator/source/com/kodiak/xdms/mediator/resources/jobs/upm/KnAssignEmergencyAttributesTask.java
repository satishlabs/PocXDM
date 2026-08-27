/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.KnUserEmergencyAttributes;
import com.kodiak.common.commdto.request.KnCorpSubsEmergencyRequestDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDestinationAttributeDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsEmergencyConfigDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.HashSet;
import java.util.Set;

import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnAssignEmergencyAttributesTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAssignEmergencyAttributesTask.class);

    private KnXDMCorpMediator corpMediator;
    private String corpId;
    private String profileMdn;
    private KnCorpResponseDTO userProfile;
    private KnPersisterTxn assignSubsTxn;
    private String hierarchyType;

    public KnAssignEmergencyAttributesTask(String profileMdn, String corpId
        ,KnCorpResponseDTO userProfile,String hierarchyType,KnPersisterTxn assignSubsTxn){

        corpMediator = KnXDMCorpMediator.getInstance();
        this.profileMdn=profileMdn;
        this.corpId=corpId;
        this.userProfile=userProfile;
        this.assignSubsTxn=assignSubsTxn;
        this.hierarchyType=hierarchyType;
    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName="setEmergencyAttributesTask_executeTask()";
        KnPersisterTxn emergencyAttrTxn =assignSubsTxn;
        KnTaskResult taskResult=new KnTaskResult();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        try{
            knLogger.debug(methodName,"profileMdn :", KnGDPRTemplate.mdn(profileMdn)," corpId :",corpId," userProfile :",userProfile,"hierarchyType:",hierarchyType);
            KnCorpResponseDTO userProfileDetails = userProfile;

            KnSubsEmergencyConfigDTO emergencyConfig = userProfileDetails.getUserProfile().getEmergencyConfig();
            knLogger.info(methodName, "emergencyConfig cb:", emergencyConfig);
            if(emergencyConfig!=null&&emergencyConfig.getDestType()!=null) {
                KnCorpSubsEmergencyRequestDTO xdmRequestDTO = new KnCorpSubsEmergencyRequestDTO();
                KnUserEmergencyAttributes emergencyAttributes = new KnUserEmergencyAttributes();
                emergencyAttributes.setMdn(profileMdn);
                emergencyAttributes.setEmergCallType(emergencyConfig.getCallType());
                emergencyAttributes.setEmergCancelPermission(emergencyConfig.getCancelPermission());
                emergencyAttributes.setEmergOriginBitSet(emergencyConfig.getOrigBitset());
                emergencyAttributes.setEmergTermBitSet(emergencyConfig.getTermBitset());
                emergencyAttributes.setEmergLMRBehavior(emergencyConfig.getLmrBehavior());
                emergencyAttributes.setEmergInitPermission(emergencyConfig.getPermission());
                //to avoid null check in for
                Set<KnDestinationAttributeDTO> destAttributes = new HashSet<>();
                destAttributes = emergencyConfig.getDestAttributes();
                //1 - User Selected Destination, 2 - Cat/Admin Configured Destination
                emergencyAttributes.setEmergDestType(emergencyConfig.getDestType());
                for (KnDestinationAttributeDTO emgDestAttr : destAttributes) {
                    if (emgDestAttr != null && emgDestAttr.getDestCat() != null) {
                        //DestCategory 1 - Primary, 2 - Secondary
                        if (emgDestAttr.getDestCat().equals(1)) {
                            //DestURI GroupId or ContactId
                            emergencyAttributes.setPriDestination(emgDestAttr.getDestURI());
                        } else {
                            emergencyAttributes.setSecDestination(emgDestAttr.getDestURI());
                        }
                        //DestType 1 - Group, 2 - Contact
                        //emgDestAttr.getDestType();
                    }
                }

                xdmRequestDTO.setEmergencyAttributes(emergencyAttributes);
                xdmRequestDTO.setCorpId(corpId);
                xdmRequestDTO.setUpmCall(true);
                if(hierarchyType!=null && Integer.parseInt(hierarchyType) ==  com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY.value())
                    xdmRequestDTO.setUpmHierarchyCall(true);
                knLogger.debug(methodName, " assign emergency xdmRequestDTO :", xdmRequestDTO);
                KnCorpResponseDTO emergencyConfigResponse = corpMediator.setBulkSubsEmergencyAttributes(xdmRequestDTO, emergencyAttrTxn);
                knLogger.debug(methodName, " emergencyConfigResponse:", emergencyConfigResponse);
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == emergencyConfigResponse.getStatus()) {
                    knLogger.debug(methodName, "assign upm emergency Operation Failed");
                    throw new KnXDMServerException(emergencyConfigResponse.getStatusCode(), emergencyConfigResponse.getMessage());
                }
                knLogger.info(methodName, "Done assigning upm emergency for profile mdn :", KnGDPRTemplate.mdn(profileMdn));
            }
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed assign upm ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignEmergencyAttributes Task", KnAuditHelper.STATUS.FAILURE, "AssignEmergencyAttributes request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "AssignEmergencyAttributes Task", KnAuditHelper.STATUS.FAILURE, "AssignEmergencyAttributes request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;
    }

}
