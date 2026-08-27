/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.commdto.common.KnUserEmergencyAttributes;
import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpGroupInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpSubsProvInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsDestEmergencyAttributes;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGrpMemListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CLIENT_TYPE_CAT_UI;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;

public class KnMCXGroupCleanUpOnMCPTTDisabledBitTask implements Runnable {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMCXGroupCleanUpOnMCPTTDisabledBitTask.class);

    private Integer corpId;
    private String mdn;
    private String txnId;
    private KnGeneralCacheUtil cacheUtil = KnGeneralCacheUtil.getInstance();
    private KnCorpGroupInfoUtil groupInfoUtil= new KnCorpGroupInfoUtil();
    private KnCorpCommonInfoUtil commonInfoUtil= new KnCorpCommonInfoUtil();
    private final Integer MCX_GROUP_INDICATOR=1;
    private KnXDMCorpMediator corpMediator= KnXDMCorpMediator.getInstance();
    private KnXDMCommonMediator commonMediator= KnXDMCommonMediator.getInstance();
    private IXcapDiffNotifierIntf notifier= new KnXcapDiffNotifierImpl();
    private KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();
    private IProvClientIntf provClientIntf= KnProvClientImpl.getInstance();
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil=new KnCorpSubsProvInfoUtil();


    KnMCXGroupCleanUpOnMCPTTDisabledBitTask(Integer corpId,String mdn,String txnId){
        this.corpId=corpId;
        this.mdn=mdn;
        this.txnId=txnId;
    }

    @Override
    public void run() {
        runTask();
    }

    private void runTask(){
        final String methodName="runTask()";
        KnPersisterTxn mcxCleanUpTxn =null;
        KnPersisterTxn mcxCleanUpTaskTxn =null;
        try {
            mcxCleanUpTxn = KnPersisterTxn.getPersisterTxn();
            //transaction per mdn
            mcxCleanUpTxn.open();
            knLogger.info(methodName,"Entry :"," txnId :",txnId ," mdn :",mdn);
            cacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, mcxCleanUpTxn);
            String xdmHome = corpProfile.getXdmsHome();
            List<String> mdnList=new ArrayList<>();
            mdnList.add(mdn);
            Map<Integer, List<KnCorpGrpMemListDTO>> mdnAssignedGroups = groupInfoUtil.getSubsGroupIdListMap(mdnList, xdmHome, mcxCleanUpTxn);
            ArrayList<KnCorpGroupDTO> groupBasicInfo = groupInfoUtil.getGroupBasicInfoList(mdnAssignedGroups.keySet(), xdmHome, mcxCleanUpTxn);
            //filter mcxGroup
            List<KnCorpGroupDTO> mcxGroups = groupBasicInfo.stream().filter(e -> e.getMcxGrpInd().equals(MCX_GROUP_INDICATOR)).collect(Collectors.toList());
            knLogger.debug(methodName," mcxGroups :",mcxGroups);

            Collection<KnCorpAddlTGInfoDTO> mdnTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmHome, mcxCleanUpTxn);
            knLogger.debug(methodName," mdnTGList :",mdnTGList);
            List<Integer> mdnTGIds = mdnTGList.stream().map(KnCorpAddlTGInfoDTO::getGroupId).collect(Collectors.toList());

            KnIPTalkGroupDTO campedDto = new KnIPTalkGroupDTO();
            campedDto.setMdn(mdn);
            List<KnCorpTGSPersistDTO> campedGrpList=groupInfoUtil.getSubsCampedGrp(campedDto,xdmHome, mcxCleanUpTxn);
            knLogger.debug(methodName," campedGrpList :",campedGrpList);
            List<Integer> campedGroupIds = campedGrpList.stream().map(KnCorpTGSPersistDTO::getGroupId).collect(Collectors.toList());

            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributes =
                    corpSubsProvInfoUtil.getEmergAttributes(mdn,xdmHome, mcxCleanUpTxn);
            knLogger.debug(methodName," mdnEmergAttributes :",mdnEmergAttributes);
            List<String> emgGroupId = mdnEmergAttributes.stream().map(KnSubsDestEmergencyAttributes::getEmergDest).collect(Collectors.toList());
            mcxCleanUpTxn.save();

            for(KnCorpGroupDTO mcxGroupsInfo:mcxGroups){
                try {
                    mcxCleanUpTaskTxn = KnPersisterTxn.getPersisterTxn();
                    //transaction per mdn
                    mcxCleanUpTaskTxn.open();
                    KnXDMCorpGroupInfoRequestDTO xdmRequestDto = new KnXDMCorpGroupInfoRequestDTO();
                    LinkedList<String> removeMdnList = new LinkedList<>();
                    //remove mdn from group
                    int groupId = mcxGroupsInfo.getGroupId();
                    removeMdnList.add(mdn);
                    xdmRequestDto.setRemovedMdnList(removeMdnList);
                    xdmRequestDto.setGroupId(String.valueOf(groupId));
                    xdmRequestDto.setCorpId(String.valueOf(corpId));
                    xdmRequestDto.setClientType(CLIENT_TYPE_CAT_UI);
                    xdmRequestDto.setETag("-9999");
                    //xdmRequestDto.setUpmCall(true);
                    KnCorpResponseDTO removeGroupMemPropResp = corpMediator.modifyCorpGroup(xdmRequestDto, mcxCleanUpTaskTxn);
                    knLogger.debug(methodName, "removeGroupMemPropResp :", removeGroupMemPropResp);

                    commonMediator.startNotifyMicroServicesJob(removeGroupMemPropResp.getChangeLogMap(), removeGroupMemPropResp.getMdnCorpId(),
                            removeGroupMemPropResp.getGroupCreatedBy(), removeGroupMemPropResp.getLmrIntropCapable(),
                            removeGroupMemPropResp.getMcxGrpInd(), removeGroupMemPropResp.getOldLmrInteropFlag());
                    Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(removeGroupMemPropResp);
                    boolean removeGroupMemPropisNotified = notifier.sendXcapDiffNotifications(xcapDiffList, mcxCleanUpTaskTxn);
                    knLogger.debug(methodName, "removeGroupMemPropisNotified :", removeGroupMemPropisNotified);
                    //getAll mcsXcapUri's for sending MCS Group notifications
                    Set<String> allMcsXcapUris = genInfoUtil.getMCSXCAPRootURIs(mcxCleanUpTaskTxn);
                    //send MCSGroup notification
                    boolean status = commonMediator.sendMCSGRPNotification(removeGroupMemPropResp, allMcsXcapUris, corpId);
                    knLogger.debug(methodName, "sending Group notification Status: ", status);

                    if (mdnTGIds.contains(groupId)) {
                        //remove from zone and channel,if exists in DG.SUBSCRPTTRADIOTGLIST
                        KnXDMAddlTalkGroupRequestDTO removeTalkGrpDTO = new KnXDMAddlTalkGroupRequestDTO();
                        KnXDMAddlTalkGroupInfoDTO tgGroupInfo = new KnXDMAddlTalkGroupInfoDTO();
                        Collection<KnXDMAddlTalkGroupInfoDTO> addedTGList = new ArrayList<>();
                        Collection<KnXDMAddlTalkGroupInfoDTO> modifyTGList = new ArrayList<>();
                        List<KnXDMAddlTalkGroupInfoDTO> removeTGList = new ArrayList<>();
                        tgGroupInfo.setGroupId(groupId);
                        removeTGList.add(tgGroupInfo);
                        removeTalkGrpDTO.setCorpId(String.valueOf(corpId));
                        removeTalkGrpDTO.setAddedAddlTgList(addedTGList);
                        removeTalkGrpDTO.setModifiedAddlTgList(modifyTGList);
                        removeTalkGrpDTO.setRemovedAddlTgList(removeTGList);
                        removeTalkGrpDTO.setMdn(mdn);
                        //removeTalkGrpDTO.setUpmCall(true);
                        KnCorpResponseDTO removeTGListResp = corpMediator.modifySubscriberTGList(removeTalkGrpDTO, mcxCleanUpTaskTxn);
                        knLogger.debug(methodName, "removeTGListResp :", removeTGListResp);
                        //notifications
                        Collection<KnXcapDiffDirChgNotifyDTO> removeTGListXcapDiffList = commonMediator.prepareNotification(removeTGListResp);
                        boolean removeTGListisNotified = notifier.sendXcapDiffNotifications(removeTGListXcapDiffList, mcxCleanUpTaskTxn);
                        knLogger.info(methodName, "removeTGListisNotified Notification status - ", removeTGListisNotified);
                    }

                    //remove priority ,if exists CAMPEDGROUPINFO
                    if (campedGroupIds.contains(groupId)) {
                        KnXDMTalkGroupRequestDTO campedGroupReq = new KnXDMTalkGroupRequestDTO();
                        List<KnXDMTalkGroupInfoDTO> addedCampGrpList = new ArrayList<>();
                        List<KnXDMTalkGroupInfoDTO> modifyTalkGroupInfo = new ArrayList<>();
                        List<KnXDMTalkGroupInfoDTO> removeTalkGroupInfo = new ArrayList<>();

                        KnXDMTalkGroupInfoDTO CampGrpInfo = new KnXDMTalkGroupInfoDTO();
                        CampGrpInfo.setGroupId(groupId);
                        removeTalkGroupInfo.add(CampGrpInfo);

                        campedGroupReq.setCorpId(String.valueOf(corpId));
                        campedGroupReq.setMdn(mdn);
                        campedGroupReq.setMode(1);
                        campedGroupReq.setETag("-9999");
                        campedGroupReq.setAddedCampGrpList(addedCampGrpList);
                        campedGroupReq.setModifiedCampGrpList(modifyTalkGroupInfo);
                        campedGroupReq.setRemovedCampGrpList(removeTalkGroupInfo);
                        //campedGroupReq.setUpmCall(true);
                        KnCorpResponseDTO removeScanListResp = corpMediator.modifySubscriberScanList(campedGroupReq, mcxCleanUpTaskTxn);
                        knLogger.debug(methodName, "removeScanListResp :", removeScanListResp);
                        //notifation
                        Collection<KnXcapDiffDirChgNotifyDTO> removeScanListRespxcapDiffList = commonMediator.prepareNotification(removeScanListResp);
                        notifier.setMaxNotfnsPerJob(2);
                        boolean removeScanListRespisNotified = notifier.sendXcapDiffNotifications(removeScanListRespxcapDiffList, mcxCleanUpTaskTxn);
                        knLogger.debug(methodName, " removeScanListRespisNotified - ", removeScanListRespisNotified);
                    }
                    //remove if Emergency type group,DG.EMERGENCY_SUBSCR_DESTINFO
                    if (emgGroupId.contains(String.valueOf(groupId))) {
                        KnCorpSubsEmergencyRequestDTO xdmRequestDTO = new KnCorpSubsEmergencyRequestDTO();
                        KnUserEmergencyAttributes emergencyAttributes = new KnUserEmergencyAttributes();
                        emergencyAttributes.setMdn(mdn);
                        emergencyAttributes.setEmergCallType(1);
                        emergencyAttributes.setEmergCancelPermission(0);
                        emergencyAttributes.setEmergOriginBitSet(0);
                        emergencyAttributes.setEmergTermBitSet(0);
                        emergencyAttributes.setEmergLMRBehavior(0);
                        emergencyAttributes.setEmergInitPermission(0);
                        //1 - User Selected Destination, 2 - Cat/Admin Configured Destination
                        //DestType 1 - Group, 2 - Contact,DestCategory 1 - Primary, 2 - Secondary,DestURI GroupId or ContactId
                        emergencyAttributes.setEmergDestType(1);
                        emergencyAttributes.setPriDestination(null);
                        xdmRequestDTO.setEmergencyAttributes(emergencyAttributes);
                        xdmRequestDTO.setCorpId(String.valueOf(corpId));
                        //xdmRequestDTO.setUpmCall(true);
                        knLogger.debug(methodName, "xdmRequestDTO :", xdmRequestDTO);
                        KnCorpResponseDTO libRespDto = corpMediator.setSubsEmergencyAttributes(xdmRequestDTO, mcxCleanUpTaskTxn);
                        if (libRespDto.isProfileChanged()) {
                            KnOPProvDTO provRespDTO = provClientIntf.sendConfigDocNotification(xdmRequestDTO.getEmergencyAttributes().getMdn(), mcxCleanUpTaskTxn);
                            commonMediator.sendXcapNotification(provRespDTO.getDirChgDTO(), null);
                        }
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_SET_EMERGENCY_ATTRIBUTES_REQ_SUCC);
                        Collection<KnXcapDiffDirChgNotifyDTO> emergencyXcapDiffList = commonMediator.prepareNotification(libRespDto);
                        boolean isNotified = notifier.sendXcapDiffNotifications(emergencyXcapDiffList, mcxCleanUpTaskTxn);
                        knLogger.debug(methodName, "emergencyXcapDiffList notification status - ", isNotified);
                    }

                    mcxCleanUpTaskTxn.save();
                    knLogger.info(methodName, " Done clean up for groupId: ", groupId);
                }catch (Throwable ex){
                    knLogger.error(methodName," Throwable :",ex.getMessage());
                    rollback(mcxCleanUpTaskTxn);
                }
            }

            cacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());

            knLogger.info(methodName,"Exit :"," txnId :",txnId ," mdn :",mdn);
        } catch (KnPersistenceException e) {
            updateFailedAsyncJobTaskStatus(txnId);
            rollback(mcxCleanUpTxn);
            knLogger.error(methodName," KnPersistenceException :",e.getMessage());
        }catch (Throwable e) {
            updateFailedAsyncJobTaskStatus(txnId);
            rollback(mcxCleanUpTxn);
            knLogger.error(methodName," Throwable :",e.getMessage());
        }
    }

    private void updateFailedAsyncJobTaskStatus(String failedTxn){
        String methodName = "updateFailedAsyncJobTaskStatus()";
        try {
            knLogger.error(methodName," failedTxn :", failedTxn);
            cacheUtil.updateAsyncJobStatus(failedTxn, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
        } catch (Exception e) {
            knLogger.error(methodName," Exception :",e.getMessage());
        }
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }
}
