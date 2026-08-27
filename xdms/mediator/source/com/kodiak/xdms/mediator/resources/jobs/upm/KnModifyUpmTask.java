/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.commdto.common.KnCorpGroupContactDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnTalkGrpScanModeDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpEXDMSNotifyDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsAddlInfoPersistDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KnModifyUpmTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyUpmTask.class);

    private ICorpClientIntf corpClientIntf;
    private KnXDMCommonMediator commonMediator;

    private String payLoad;
    private String corpId;
    private KnPersisterTxn modifyUpmTranxn;
    private KnGenInfoUtil genInfoUtil;

    public KnModifyUpmTask(String payload,String corpId
            ,KnPersisterTxn modifyUpmTranxn){
        corpClientIntf = new KnCorpClientImpl();

        this.payLoad=payload;
        this.corpId=corpId;
        commonMediator = KnXDMCommonMediator.getInstance();
        this.modifyUpmTranxn=modifyUpmTranxn;
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName="KnModifyUpmTask()";
        KnPersisterTxn modifyUpmTxn =modifyUpmTranxn;
        KnTaskResult taskResult=new KnTaskResult();
        knLogger.info(methodName,"ENTRY");
        try{
            KnIPUserProfileDTO ipUserProfileDTO = KnCorpCommonInfoUtil.jsonToObject(payLoad, KnIPUserProfileDTO.class);
            knLogger.debug(methodName," ipUserProfileDTO:",ipUserProfileDTO);
            KnCorpResponseDTO corpResponseDTO = corpClientIntf.getProfileMdnByUPId(ipUserProfileDTO, false, modifyUpmTxn);
            List<String> mdnList = corpResponseDTO.getMdnList();
            String tgscMode = ipUserProfileDTO.getModifiedUserProfileDTO().getTgscMode();
            if(tgscMode != null){
                int intTgscMode = Integer.parseInt(tgscMode);
                genInfoUtil.updateSubsTalkGrpScanMode(mdnList,intTgscMode,modifyUpmTxn);
            }
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnSubsAddlInfoPersistDTO subsProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
            if(ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileName() != null){
                String userProfileName=ipUserProfileDTO.getModifiedUserProfileDTO().getUserProfileName();
                subsProfilePersistDTO.setUserProfileName(userProfileName);
                subsProfilePersistDTO.setMdns(mdnList);
                knLogger.debug(methodName, "MDNs in subsProfilePersistDTO", subsProfilePersistDTO.getMdns());
                provXDMServerDAO.updateUserProfileName(subsProfilePersistDTO,modifyUpmTxn);

            }else{
                Map<String,String> profileNames=provXDMServerDAO.selectUserProfileNameList(mdnList,modifyUpmTxn);
                List<String> mdns= new ArrayList<>();
                for (Map.Entry<String,String> entry : profileNames.entrySet()){
                    if(entry.getValue() ==null){
                        mdns.add(entry.getKey());
                    }
                }
                subsProfilePersistDTO.setMdns(mdns);
                subsProfilePersistDTO.setUserProfileName(ipUserProfileDTO.getUserProfileName());
                knLogger.debug(methodName, "MDNs in subsProfilePersistDTO", subsProfilePersistDTO.getMdns());
                provXDMServerDAO.updateUserProfileName(subsProfilePersistDTO,modifyUpmTxn);
            }
            corpResponseDTO = corpClientIntf.modifyCBUserProfile(ipUserProfileDTO, modifyUpmTxn);
            if(corpResponseDTO != null){
                if (corpResponseDTO.getMcxGrpMemberShipMap()!= null && !corpResponseDTO.getMcxGrpMemberShipMap().isEmpty()) {
                    List<KnCorpEXDMSNotifyDto> microserviceNotify = new ArrayList<>();
                    Map<Integer, KnCorpGroupContactDTO> mcxgroupMap = corpResponseDTO.getMcxGrpMemberShipMap();
                    for(Integer key : mcxgroupMap.keySet()) {
                        List<KnCorpEXDMSNotifyDto> grpNotifyDtoList = commonMediator.getModifyMcxGrpMicroSrvNotifyDto(corpResponseDTO,
                                Integer.parseInt(corpId), key, corpResponseDTO.getUserProfileId(), mcxgroupMap.get(key), null);
                        microserviceNotify.addAll(grpNotifyDtoList);
                    }
                    if (!microserviceNotify.isEmpty()){
                        commonMediator.startNotifyMicroServicesJob(microserviceNotify);
                    }
                }
            }
            knLogger.info(methodName, "Successfully modified the UPM");
            taskResult.setTaskStatus(KnConstants.STATUS_SUCCESS);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            taskResult.setTaskStatus(KnConstants.STATUS_FAILURE);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            taskResult.setTaskStatus(KnConstants.STATUS_FAILURE);
        }

        return taskResult;
    }



}
