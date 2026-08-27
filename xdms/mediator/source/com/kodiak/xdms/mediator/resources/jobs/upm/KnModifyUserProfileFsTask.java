/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.notificationmgr.beans.KnProfileNotifyDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnUserProfileFSDTO;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyDto;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnUserProfileFSProvDTO;

import java.util.ArrayList;
import java.util.List;

import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_18;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_SUCCESS;

public class KnModifyUserProfileFsTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyUserProfileFsTask.class);

    private IProvClientIntf provClientIntf;
    private KnXDMCommonMediator commonMediator;

    private String payLoad;
    private KnPersisterTxn modifyUpmTranxn;
    private String profileMdn;

    public KnModifyUserProfileFsTask(String payLoad
            ,KnPersisterTxn modifyUpmTranxn,String profileMdn){

        provClientIntf = KnProvClientImpl.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();

        this.payLoad=payLoad;
        this.modifyUpmTranxn=modifyUpmTranxn;
        this.profileMdn=profileMdn;
    }

    @Override
    public KnTaskResult executeTask() {

        final String methodName="KnModifyUserProfileFsTask()";
        KnPersisterTxn UPMFsTxn =modifyUpmTranxn;
        KnTaskResult taskResult=new KnTaskResult();

        try{
            KnIPUserProfileDTO ipUserProfileFsDTO=KnCorpCommonInfoUtil.jsonToObject(payLoad, KnIPUserProfileDTO.class);
            knLogger.info(methodName,"profileMdn ", KnGDPRTemplate.mdn(profileMdn));

            if(ipUserProfileFsDTO!=null&&profileMdn!=null){
                //setting upmfs from req
                KnUserProfileFSDTO userProfileFs = ipUserProfileFsDTO.getUserProfileFSDto();
                //upm prov dto
                knLogger.debug(methodName,"userProfileFs :",userProfileFs);
                KnUserProfileFSProvDTO userProfileFSProvDTO=null;
                if(userProfileFs!=null){
                    userProfileFSProvDTO=new KnUserProfileFSProvDTO();
                    userProfileFSProvDTO.setAmbientListening(userProfileFs.getAmbientListening());
                    userProfileFSProvDTO.setBrdcrmb(userProfileFs.getBrdcrmb());
                    userProfileFSProvDTO.setDiscreteListening(userProfileFs.getDiscreteListening());
                    userProfileFSProvDTO.setGeofnc(userProfileFs.getGeofnc());
                    userProfileFSProvDTO.setLocPublish(userProfileFs.getLocPublish());
                    userProfileFSProvDTO.setMcVideoConfirmedPull(userProfileFs.getMcVideoConfirmedPull());
                    userProfileFSProvDTO.setMcVideoGroupRx(userProfileFs.getMcVideoGroupRx());
                    userProfileFSProvDTO.setMcVideoRx(userProfileFs.getMcVideoRx());
                    userProfileFSProvDTO.setMcVideoTx(userProfileFs.getMcVideoTx());
                    userProfileFSProvDTO.setPtloc(userProfileFs.getPtloc());
                    userProfileFSProvDTO.setPtmd(userProfileFs.getPtmd());
                    userProfileFSProvDTO.setPtx(userProfileFs.getPtx());
                    userProfileFSProvDTO.setTgsclnt(userProfileFs.getTgsclnt());
                    userProfileFSProvDTO.setUserCheck(userProfileFs.getUserCheck());
                    userProfileFSProvDTO.setUserEnable(userProfileFs.getUserEnable());
                    userProfileFSProvDTO.setOsm(userProfileFs.getOsm());
                    userProfileFSProvDTO.setEmergency(userProfileFs.getEmergency());
                    userProfileFSProvDTO.setSelfDnDPrivilege(userProfileFs.getSelfDnDPrivilege());
                }

                knLogger.debug(methodName,"Entry updateSubscriberUserProfileFS userProfileFSProvDTO",userProfileFSProvDTO);
                KnIPSubsProvInfoDTO subsProfileInputDTO=new KnIPSubsProvInfoDTO();
                subsProfileInputDTO.setUserProfileFSProvDTO(userProfileFSProvDTO);
                subsProfileInputDTO.setMdn(profileMdn);
                KnOPUpdateSubsInfoDTO provResp=provClientIntf.updateSubscriberUserProfileFS(subsProfileInputDTO,UPMFsTxn);
                knLogger.debug(methodName,"provResp ",provResp);
                Integer pv = provResp.getClientPvMajorVersion();
                    //notification
                if (provResp.isActiveFSChanged()) {
                    KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                    if (provResp.isActiveFSChanged()) {
                        profileNotifyDTO.setActiveFeatureSetChange(profileMdn);
                    }
                    profileNotifyDTO.setMdn(profileMdn);
                    profileNotifyDTO.setPocHome(provResp.getDirChgDTO().getPocHome());
                    profileNotifyDTO.setPresenceHome(provResp.getDirChgDTO().getPresenceHome());
                    profileNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());
                    commonMediator.sendProfileNotification(profileNotifyDTO, provResp.getDirChgDTO(), String.valueOf(pv));
                    //Get the xcap mobile sync flag
                    boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(UPMFsTxn);
                    boolean xcapCouchClientBit = KnGeneralUtil.getFeatureBitValue(provResp.getActiveFS2(),
                            com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
                    knLogger.debug(methodName, "xcapMobileSync:", xcapMobileSync, " xcapCouchClientBit:",
                            xcapCouchClientBit, " PV:", pv);
                    if (xcapMobileSync && (xcapCouchClientBit || pv >= PROTOCOL_VERSION_18)) {
                        sendMCSEvent(provResp);
                    }
                } else {
                        commonMediator.sendXcapNotification(provResp.getDirChgDTO(), String.valueOf(pv));

                }
            }
            taskResult.setTaskStatus(STATUS_SUCCESS);
            knLogger.info(methodName,"Done updating user profile fs");
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
           // rollback(UPMFsTxn);
            taskResult.setTaskStatus(STATUS_FAILURE);
        }
        return taskResult;
    }

    private void sendMCSEvent(KnOPUpdateSubsInfoDTO provResp) throws KnException {
        String methodName = "sendMCSEvent(KnOPUpdateSubsInfoDTO)";
        KnSubscrEXDMSNotifyDto subscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
        subscrEXDMSNotifyDto.setMdn(profileMdn);
        subscrEXDMSNotifyDto.setCorpid(provResp.getCorpId());
        subscrEXDMSNotifyDto.setActiveFS(KnGeneralUtil.convertHexStringToLong(provResp.getActiveFS2()));
        subscrEXDMSNotifyDto.setActiveFS2(provResp.getActiveFS2());
        subscrEXDMSNotifyDto.setOldActiveFS(provResp.getOldActiveFS2());
        subscrEXDMSNotifyDto.setPv(String.valueOf(provResp.getClientPvMajorVersion()));
        subscrEXDMSNotifyDto.setId(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.FEATURE_BIT_CHANGE.value()
                + KnConstants.LINE_SAPERATOR + profileMdn);
        subscrEXDMSNotifyDto.setType(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.FEATURE_BIT_CHANGE.value());
        subscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        subscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.
                USER_NOTIFY_EVENTS.value());
        subscrEXDMSNotifyDto.setLastProfileUpdateTime(provResp.getLastProfileUpdateTime());
        List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<KnSubscrEXDMSNotifyDto>();
        notifyDtoList.add(subscrEXDMSNotifyDto);
        knLogger.info(methodName, "Publishing micro service notify  for User event - ");
        commonMediator.startNotifyMicroServicesJob(notifyDtoList);
        knLogger.debug(methodName, "micro service notify for activeFs change event - Sent");

    }
}
