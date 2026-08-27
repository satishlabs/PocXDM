/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.request.KnXDMCorpContactListRequestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpModifyUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;


import java.util.ArrayList;
import java.util.Collection;

import static com.kodiak.xdms.server.common.resources.KnConstants.*;

public class KnModifyContactTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyContactTask.class);

    private KnXDMCorpMediator corpMediator;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;

    private String corpId;
    private String userProfileId;
    private String payLoad;
    private KnPersisterTxn modifyUpmTranxn;
    private String profileMdn;
    private Integer dbSublistId;

    public KnModifyContactTask(String corpId,String userProfileId,String payLoad
            ,KnPersisterTxn modifyUpmTranxn,String profileMdn,Integer dbSublistId){
        corpMediator = KnXDMCorpMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();

        this.corpId=corpId;
        this.userProfileId=userProfileId;
        this.payLoad=payLoad;
        this.modifyUpmTranxn=modifyUpmTranxn;
        this.profileMdn=profileMdn;
        this.dbSublistId=dbSublistId;
    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName="KnModifyContactTask()";
        KnPersisterTxn modifyContactTxn =modifyUpmTranxn;
        KnXDMCorpContactListRequestDTO xdmRequestDTO = new KnXDMCorpContactListRequestDTO();
        KnIPUserProfileDTO ipUserProfileDTO=new KnIPUserProfileDTO();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
        Integer addedSublistId=null;
        Integer removedSublistId=null;
        KnTaskResult taskResult=new KnTaskResult();
        try{
            ipUserProfileDTO.setCorpId(corpId);
            ipUserProfileDTO.setProfileId(userProfileId);

            knLogger.info(methodName, "modifyUpmReq payLoad:", payLoad);
            KnIPUserProfileDTO ipUserProfilePermDTO=KnCorpCommonInfoUtil.jsonToObject(payLoad, KnIPUserProfileDTO.class);
            KnCorpModifyUserProfileDTO modifyUpmReq=ipUserProfilePermDTO.getModifiedUserProfileDTO();
            knLogger.debug(methodName,"modifyUpmReq:",ipUserProfilePermDTO);
            //get the value from req
            Integer reqSubListId = modifyUpmReq.getContactListID();

            knLogger.debug(methodName,"reqSubListId:",reqSubListId,"dbSublistId:",dbSublistId);
            if(reqSubListId!=null&&!reqSubListId.equals(-1)){
                addedSublistId=reqSubListId;
                if(!reqSubListId.equals(dbSublistId)) {
                    removedSublistId = dbSublistId;
                }
            }else if(reqSubListId!=null&&reqSubListId.equals(-1)){
                removedSublistId=dbSublistId;
            }

            Collection<Integer> addedSublistIds=new ArrayList<>();
            if(addedSublistId!=null)
            addedSublistIds.add(addedSublistId);

            Collection<Integer> removedSublistIds=new ArrayList<>();
            if(removedSublistId!=null)
            removedSublistIds.add(removedSublistId);

            //adding target mdn of the UMP to contact list of profile mdn
            Collection<String> addedMdnList=new ArrayList<>();

            //removing target mdn of the UMP to contact list of profile mdn
            Collection<String> removedMdnList=new ArrayList<>();


            knLogger.debug(methodName, "addedMdnList  :", KnGDPRTemplate.mdnList(addedMdnList),"removedMdnList :",KnGDPRTemplate.mdnList(removedMdnList));
            knLogger.debug(methodName, "addedSublistIds  :", addedSublistIds,"removedSublistIds :",removedSublistIds);
            knLogger.info(methodName, "profileMdn ", KnGDPRTemplate.mdn(profileMdn));

            if (profileMdn != null && (!addedSublistIds.isEmpty() || !removedSublistIds.isEmpty())) {

                KnXDMCorpUserProfileRespDTO corpUserProfileRespDTO = new KnXDMCorpUserProfileRespDTO();

                corpUserProfileRespDTO.setCorpId(corpId);
                corpUserProfileRespDTO.setProfileMdn(profileMdn);
                corpUserProfileRespDTO.setAddedSublistIds(addedSublistIds);
                corpUserProfileRespDTO.setRemovedSublistIds(removedSublistIds);
                knLogger.debug(methodName, "setUserProfileContact");
                KnCorpResponseDTO corpRespDto = corpMediator.setUserProfileContact(corpUserProfileRespDTO, modifyContactTxn);

                knLogger.debug(methodName, "corpRespDto",corpRespDto);
                taskResult.setContactEtagToBeUpdated(true);
            }
            knLogger.info(methodName, "Done modify contact for profilemdnList :");
            taskResult.setTaskStatus(STATUS_SUCCESS);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed ModifyContact task ", profileMdn);
            knLogger.error(methodName, "Failed assign upm ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "ModifyContactTask", KnAuditHelper.STATUS.FAILURE, "ModifyContactTask request failed"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed ModifyContact task ", profileMdn);
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            audit.writeAuditMessage("Failed ProfileMdn:" + profileMdn, "ModifyContactTask", KnAuditHelper.STATUS.FAILURE, "ModifyContactTask request failed"+e.getMessage());
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;
    }

}
