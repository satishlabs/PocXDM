/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.request.KnXDMCorpContactListRequestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.helper.KnXDMProvMediator;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnAssignContactTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAssignContactTask.class);


    private KnXDMCorpMediator corpMediator;
    private KnXDMProvMediator provMediator;
    private String corpId;
    private String profileMdn;
    private KnOPSubsProfileInfoDTO subsProfileInfo;
    private KnCorpResponseDTO userProfile;
    private KnPersisterTxn assignSubsTxn;

    public KnAssignContactTask(String profileMdn,String corpId
    ,KnOPSubsProfileInfoDTO subsProfileInfo,KnCorpResponseDTO userProfile,KnPersisterTxn assignSubsTxn){
        corpMediator = KnXDMCorpMediator.getInstance();
        provMediator = KnXDMProvMediator.getInstance();

        this.profileMdn=profileMdn;
        this.corpId=corpId;
        this.subsProfileInfo=subsProfileInfo;
        this.userProfile=userProfile;
        this.assignSubsTxn=assignSubsTxn;

    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName="assignContactTask_executeTask()";
        KnPersisterTxn assignContactTxn =assignSubsTxn;
        KnXDMCorpContactListRequestDTO xdmRequestDTO = new KnXDMCorpContactListRequestDTO();
        //KnXDMCorpUserProfileRespDTO xdmRequestDTO = new KnXDMCorpUserProfileRespDTO();
        KnTaskResult taskResult=new KnTaskResult();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        try{
            knLogger.info(methodName, " starting assign upm contact for mdn:", KnGDPRTemplate.mdn(profileMdn));
            knLogger.debug(methodName," userProfile :",userProfile);

            KnOPSubsProfileInfoDTO subsProfileInfoDTO =subsProfileInfo;
            KnIPSubsProvInfoDTO subsProvInfoDTO = provMediator.populateSubsProvInfoDTOForAssignUser(subsProfileInfoDTO);
            KnCorpResponseDTO userProfileDetails =userProfile;

            Integer contactListID=userProfileDetails.getUserProfile().getContactListID();
            knLogger.info(methodName, "contactListID :", contactListID);
            Collection<Integer> addedSublistIds=new ArrayList<>();
            if(contactListID!=null) {
                addedSublistIds.add(contactListID);

                Collection<Integer> removedSublistIds = new ArrayList<>();

                //adding target mdn of the UMP to conatct list of profile mdn
                Collection<String> addedMdnList = new ArrayList<>();
                Collection<String> removedMdnList = new ArrayList<>();

                xdmRequestDTO.setAddedSublistIds(addedSublistIds);
                xdmRequestDTO.setRemovedSublistIds(removedSublistIds);
                xdmRequestDTO.setAddedMdnList(addedMdnList);
                xdmRequestDTO.setRemovedMdnList(removedMdnList);
                xdmRequestDTO.setClientType(subsProvInfoDTO.getClientType());
                xdmRequestDTO.setEtag(-9999);
                xdmRequestDTO.setAuthDTO((IAuthDTO) subsProvInfoDTO.getAuthDTO());
                xdmRequestDTO.setCorpId(corpId);
                xdmRequestDTO.setOwnerMdn(profileMdn);
                xdmRequestDTO.setHierarchyType(KnConstants.HIERARCHY_TYPE.NON_HIERARCHY);
                xdmRequestDTO.setUpmCall(true);
                knLogger.debug(methodName, " assignContact xdmRequestDTO: ", xdmRequestDTO);
                KnCorpResponseDTO assignContact = corpMediator.modifyBulkCorpSubscContacts(xdmRequestDTO, assignContactTxn);
                //KnCorpResponseDTO assignContact = corpMediator.setUserProfileContact(xdmRequestDTO, assignContactTxn);
                knLogger.debug("assignContact response:", assignContact);
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == assignContact.getStatus()) {
                    knLogger.debug(methodName, "assignContact Operation Failed");
                    throw new KnXDMServerException(assignContact.getStatusCode(), assignContact.getMessage());
                }
                //KnLIEventHandler.logLI(assignContact.getLiEventList());
                knLogger.info(methodName, "Done assigning contact for profile mdn :", KnGDPRTemplate.mdn(profileMdn));
            }
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed assign upm contact :", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "assign upmTask", KnAuditHelper.STATUS.FAILURE, "assign upm request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred while assign upm contact:", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "assign upmTask", KnAuditHelper.STATUS.FAILURE, "assign upm request failed"+":"+userProfile.getUserProfileId()+":"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;
    }

}
