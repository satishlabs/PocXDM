/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;

import com.kodiak.common.dao.*;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.messaging.common.KnRmqConnectionManager;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mcsnotifymgr.KnMCSDocChangeNotifier;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocChangeListDto;
import com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.mcsnotifymgr.resources.KnMCSNotifyConstants;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;
import com.kodiak.xdms.server.common.dto.common.KnSIPProxySvcConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpEXDMSNotifyDto;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSGrpMemberDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpInfoResDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvCacheKeys;

import java.time.Instant;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;

/**
 * Created by abhishek on 7/11/16.
 */
public class KnPAMMicroServiceCommon {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMMicroServiceCommon.class);

    public static final String JOB_NAME = "DISPATCH_GROUP_MEMEBER";
    public static final String JOB_GROUP_NAME = "DISPATCHR_GROUP_JOB";
    public static final String JOB_MICRO_SERVICE_NOTIFY = "MICRO_SERVICE_NOTIFY";
    private KnJobSchedulerImpl scheduler;
    private KnGenInfoUtil genInfoUtil;
    private IXcapDiffNotifierIntf notifier;
    private String xdmPttServerId = null;
    //private IJmsMessagingClientIntf msgFwInstance;
    private static boolean isInitialized = false;
    private static volatile KnPAMMicroServiceCommon instance = null;
    private KnRmqMessagePublisher msgFw;
    private KnRmqConnectionManager connectionManager;
    private KnMCSDocChangeNotifier mcsDocChangeNotifier;
    private KnGeneralCacheUtil generalCacheUtil;

    /**
     * making the class to the singleton
     */
    private KnPAMMicroServiceCommon() {
        //get the instance of the Message FWK for response messages.
        knLogger.info( "KnPAMMicroServiceCommon()", "Creating instance of KnPAMMicroServiceCommon");
        //try {
            msgFw = KnRmqMessagePublisher.getInstance();
            connectionManager = KnRmqConnectionManager.getInstance();
            xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            notifier = new KnXcapDiffNotifierImpl();
            scheduler = KnJobSchedulerImpl.getInstance();
            genInfoUtil = KnGenInfoUtil.getInstance();
             mcsDocChangeNotifier = KnMCSDocChangeNotifier.getInstance();
           generalCacheUtil = KnGeneralCacheUtil.getInstance();
        /*} catch (KnMessageException e) {
            knLogger.error( "Construtor", "Failed to initialize the Msg FW");
            knLogger.error( "Constructor", e);
            throw new KnSystemError(KnErrorCodes.Initializer.INTERNAL_ERROR, "Failed to Initialize XDM Mediator", e);
        }*/
    }


    /**
     * method to return the singleton instance of class
     *
     * @return KnXDMCommonMediator class
     */
    public static KnPAMMicroServiceCommon getInstance() {
        if (instance == null) {
            synchronized (KnPAMMicroServiceCommon.class){
                //if (instance == null) {
                    instance = new KnPAMMicroServiceCommon();
               // }
            }
        }
        return instance ;
    }
    /**
     * Method to initiate the micro service notification job.
     *
     * @param notifyDtoList
     * @throws KnException
     */
    public void startNotifyMicroServicesJob(List<? extends KnEXDMSNotifyDto> notifyDtoList, KnMqServiceConfig rmqInfoDto) throws KnException {
        String methodName = "startNotifyMicroServicesJob()";
        try {
            knLogger.info(methodName, "Entry");
            knLogger.debug(methodName, "Entry - ", notifyDtoList);
            if(notifyDtoList !=null && !notifyDtoList.isEmpty()){
                knLogger.info(methodName, "notifyDtoList size:",notifyDtoList.size());
                List<KnPAMMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnEXDMSNotifyDto notifyDto : notifyDtoList) {
                    KnPAMMicroServiceNotifyJob job = new KnPAMMicroServiceNotifyJob(notifyDto, rmqInfoDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, JOB_MICRO_SERVICE_NOTIFY);
                knLogger.debug(methodName, "Exist, job submitted");
            }
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occured", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occured while starting job", e);
        }
    }

    /**
     * Method to initiate the micro service notification job for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @throws KnException
     */
    public void startNotifyMicroServicesJob(Map<String, KnOPDirChgDTO> changeLogMap, int corpId, KnMqServiceConfig rmqInfoDto) throws KnException {
        String methodName = "startNotifyMicroServicesJob()";
        try {
            knLogger.info(methodName, "Entry");
            if(changeLogMap != null && !changeLogMap.isEmpty()){
                List<KnCorpEXDMSNotifyDto> notifyDtoList = getModifiedNotifyJson(changeLogMap, corpId);
                knLogger.debug(methodName, "notifyDtoList - ", notifyDtoList);
                knLogger.info(methodName, "notifyDtoList size:",notifyDtoList.size());
                List<KnPAMMicroServiceNotifyJob> jobList = new ArrayList<>();
                for (KnCorpEXDMSNotifyDto corpEXDMSNotifyDto : notifyDtoList) {
                    KnPAMMicroServiceNotifyJob job = new KnPAMMicroServiceNotifyJob(corpEXDMSNotifyDto, rmqInfoDto);
                    jobList.add(job);
                }
                scheduler.addRamJob(jobList, JOB_MICRO_SERVICE_NOTIFY);
                knLogger.debug(methodName, "Exist, job submitted");
            }
        } catch (KnJobSchedulerException jsex) {
            knLogger.error(methodName, "KnJobSchedulerException occured", jsex);
            throw jsex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured", e);
            throw new KnException(KnErrorCodes.Initializer.INTERNAL_ERROR, "Exception occured while starting job", e);
        }
    }

    public boolean getXcapMobileSyncFlag(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getXcapMobileSyncFlag()";
        knLogger.debug(methodName, "ENTRY");
        /*UCSPLATFORM-7936 : Removed the code below as the flag is now always considered enabled for sending the MCS notification.
          This change aligns with UCSPLATFORM-7936, where the flag dependency has been removed.*/
        return true;
    }

    public KnMqServiceConfig retrieveServerConfDetails(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveServerConfDetails()";
        int clusterId = Integer.parseInt(System.getenv(KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, " clusterId - ", clusterId);
        KnMqServiceConfig mqServiceConfig = genInfoUtil.retrieveServerConfDetails(persisterTxn).get(clusterId);
        knLogger.debug(methodName, " mqServiceConfig - ", mqServiceConfig);
        return mqServiceConfig;
    }

    /**
     * Method to get the group diff data from the change log map for sending micro services notification for group change events.
     *
     * @param changeLogMap
     * @param corpId
     * @return
     */
    private static List<KnCorpEXDMSNotifyDto> getModifiedNotifyJson(Map<String, KnOPDirChgDTO> changeLogMap, int corpId) {
        final String methodName = "getModifiedGroupJson()";
        knLogger.info(methodName, "Entry");
        knLogger.debug(methodName, changeLogMap == null ? changeLogMap : KnGDPRTemplate.mapKeyMdn(changeLogMap));
        List<KnCorpEXDMSNotifyDto> corpEXDMSNotifyDtoList = new ArrayList<>();
        Map<Integer, KnCorpEXDMSNotifyDto> modifiedMap = new HashMap<>();
        Map<Integer, KnCorpEXDMSNotifyDto> deletedMap = new HashMap<>();
        Map<String, KnCorpEXDMSNotifyDto> modifiedContactMap = new HashMap<>();
        if (changeLogMap != null && !changeLogMap.isEmpty()) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                knLogger.debug(methodName, "MDN - ", KnGDPRTemplate.mdn(entry.getKey()));
                String mdn = entry.getKey();
                KnOPDirChgDTO opDirChgDTO = entry.getValue();
                Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                knLogger.debug(methodName, "changeDocList - ", changeDocList);
                for (KnOPDocChgDTO docChgDTO : changeDocList) {
                    knLogger.info(methodName, "docChgDTO  - ", docChgDTO);
                    //If the document type is corporate group
                    if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {

                        KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = null;
                        if ((docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value())
                                && !(modifiedMap.containsKey(docChgDTO.getGroupId()))) {
                            //A group can get deleted by some other operations like modify/delete sublist.
                            //In this flow delete group notification will be send out to all the existing members. No replace notify will be available for those groups
                            //But a broadcast group may have only remove notification though the group exist which is not possible for other group type.
                            //Since broadcast group notify does not have diff data, microservices will be notified with previous etag as -1 so that they can fetch the group details.
                            knLogger.debug(methodName, " Remove doc type for - ", docChgDTO.getGroupId());
                            corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setGrpType(docChgDTO.getGroupType());
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setVer(KnConstants.MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                            if (docChgDTO.getGroupType() == 3) {
                                knLogger.debug(methodName, "Broadcast group type");
                                corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                                corpEXDMSNotifyDto.setPreviousEtag(-1);
                                modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                            } else {
                                knLogger.debug(methodName, " Non Broadcast - ", docChgDTO.getGroupType());
                                corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                                deletedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                            }

                        } else if (docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            knLogger.debug(methodName, "Replace doc type for - ", docChgDTO.getGroupId());
                            // group modified populating for group modified data
                            corpEXDMSNotifyDto = modifiedMap.get(docChgDTO.getGroupId());
                            if (null == corpEXDMSNotifyDto) {
                                corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            }
                            deletedMap.remove(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setVer(KnConstants.MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                            corpEXDMSNotifyDto.setGrpName(docChgDTO.getGroupName());
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                            if (docChgDTO.getGroupType() == 3) {
                                knLogger.debug(methodName, " Broadcast group ");
                                corpEXDMSNotifyDto.setPreviousEtag(-1);
                            }

                            Collection<KnSubscriberDTO> addGrpMems = docChgDTO.getAddedGroupMembers();
                            if (addGrpMems != null && !addGrpMems.isEmpty()) {
                                knLogger.debug(methodName, " addGrpMems - ", addGrpMems);
                                List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
                                List<String> addedDistMem = new ArrayList<>();
                                for (KnSubscriberDTO dto : addGrpMems) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    member.setClientType(dto.getClientType());
                                    member.setSupervisor(dto.getSupervisory());
                                    member.setName(dto.getNetworkName());
                                    if (dto.getLocWatcher() == 1) {
                                        member.setIsLocWatcher(Boolean.TRUE);
                                    } else {
                                        member.setIsLocWatcher(Boolean.FALSE);
                                    }
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    addedMemebrs.add(member);
                                    if (dto.getContact_type() == 0) {
                                        //if the added member is internal subscriber add in distribution list
                                        addedDistMem.add(dto.getMdn());
                                    }
                                }
                                corpEXDMSNotifyDto.setAddedMemebrs(addedMemebrs);
                                corpEXDMSNotifyDto.setAddedGrpDistMems(addedDistMem);
                                knLogger.debug(methodName, " addedMemebrs - ", addedMemebrs);
                                knLogger.debug(methodName, " addedDistMem - ", KnGDPRTemplate.mdnList(addedDistMem));
                            }
                            if (docChgDTO.getRemovedGroupMembers() != null && !docChgDTO.getRemovedGroupMembers().isEmpty()) {
                                knLogger.debug(methodName, " docChgDTO.getRemovedGroupMembers() - ", KnGDPRTemplate.mdnList(docChgDTO.getRemovedGroupMembers()));
                                corpEXDMSNotifyDto.setRemovedMembers(new ArrayList<>(docChgDTO.getRemovedGroupMembers()));
                                corpEXDMSNotifyDto.setRemovedGrpDistMems(new ArrayList<>(docChgDTO.getRemovedGroupMembers()));
                            }
                            if (docChgDTO.getModifiedGrpMembers() != null && !docChgDTO.getModifiedGrpMembers().isEmpty()) {
                                List<KnEXDMSGrpMemberDto> modGrpMems = new ArrayList<>();
                                knLogger.debug(methodName, " docChgDTO.getModifiedGrpMembers() - ", docChgDTO.getModifiedGrpMembers());
                                for (KnSubscriberDTO dto : docChgDTO.getModifiedGrpMembers()) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    if (dto.getClientType() != 0) {
                                        member.setClientType(dto.getClientType());
                                    }
                                    if (dto.getSupervisory() != 0) {
                                        member.setSupervisor(dto.getSupervisory());
                                    }

                                    member.setName(dto.getNetworkName());
                                    if (dto.getLocWatcher() == 1) {
                                        member.setIsLocWatcher(Boolean.TRUE);
                                    } else {
                                        member.setIsLocWatcher(Boolean.FALSE);
                                    }
                                    if (dto.getContact_type() != 0)
                                        member.setContactType(dto.getContact_type());
                                    modGrpMems.add(member);
                                }
                                knLogger.debug(methodName, " modGrpMems - ", modGrpMems);
                                corpEXDMSNotifyDto.setModifiedMembers(modGrpMems);
                            }
                            if((corpEXDMSNotifyDto.getAddedMemebrs() == null || corpEXDMSNotifyDto.getAddedMemebrs().isEmpty()) &&
                                    (corpEXDMSNotifyDto.getRemovedMembers() == null || corpEXDMSNotifyDto.getRemovedMembers().isEmpty()) &&
                                    (corpEXDMSNotifyDto.getModifiedMembers() == null || corpEXDMSNotifyDto.getModifiedMembers().isEmpty()) &&
                                    corpEXDMSNotifyDto.getGrpName() == null ){
                                corpEXDMSNotifyDto.setPreviousEtag(-1);
                            }
                            modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                        } else if (docChgDTO.getDocType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()
                                && !modifiedMap.containsKey(docChgDTO.getGroupId())) {
                            knLogger.debug(methodName, " Add doc type for - ", docChgDTO.getGroupId());
                            //Only broadcast group is being taken care in add notify type.
                            corpEXDMSNotifyDto = modifiedMap.get(docChgDTO.getGroupId());
                            if (null == corpEXDMSNotifyDto) {
                                corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            }
                            deletedMap.remove(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setGrpId(docChgDTO.getGroupId());
                            corpEXDMSNotifyDto.setVer(KnConstants.MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setGrpType(docChgDTO.getGroupType());
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(-1);
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value());
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP_GROUP.value() + "_" + docChgDTO.getGroupId());
                            modifiedMap.put(docChgDTO.getGroupId(), corpEXDMSNotifyDto);
                        }
                    } else if(docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)){
                        KnCorpEXDMSNotifyDto corpEXDMSNotifyDto = null;
                        if ((docChgDTO.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value())
                                && !(modifiedContactMap.containsKey(mdn))) {
                            knLogger.debug(methodName, " Remove doc type for - ", docChgDTO.getGroupId());
                            corpEXDMSNotifyDto = new KnCorpEXDMSNotifyDto();
                            corpEXDMSNotifyDto.setCorpid(corpId);
                            corpEXDMSNotifyDto.setEtag(Integer.parseInt(docChgDTO.getNewEtag()));
                            corpEXDMSNotifyDto.setPreviousEtag(Integer.parseInt(docChgDTO.getPrevEtag()));
                            corpEXDMSNotifyDto.setType(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value());
                            corpEXDMSNotifyDto.setId(KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBS_CONTACT.value() + "_" + mdn);
                            corpEXDMSNotifyDto.setVer(KnConstants.MICROSERVICE_NOTIFY_DOC_VER);
                            corpEXDMSNotifyDto.setNotifyEventType(KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CONTACT_NOTIFY_EVENTS.value());
                            corpEXDMSNotifyDto.setMdn(mdn);
                            corpEXDMSNotifyDto.setClientType(opDirChgDTO.getClientType());

                            Collection<KnSubscriberDTO> addedMembers = docChgDTO.getAddedContactList();
                            if (addedMembers != null && !addedMembers.isEmpty()) {
                                knLogger.debug(methodName, " addedMembers - ", addedMembers);
                                List<KnEXDMSGrpMemberDto> addedMemebrs = new ArrayList<>();
                                for (KnSubscriberDTO dto : addedMembers) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    member.setClientType(dto.getClientType());
                                    member.setName(dto.getNetworkName());
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    addedMemebrs.add(member);
                                }
                                corpEXDMSNotifyDto.setAddedMemebrs(addedMemebrs);
                                knLogger.debug(methodName, " addedMemebrs - ", addedMemebrs);
                            }

                            Collection<KnSubscriberDTO> modifiedMembers = docChgDTO.getModifiedContactMembers();
                            if (modifiedMembers != null && !modifiedMembers.isEmpty()) {
                                knLogger.debug(methodName, " modifiedMembers - ", modifiedMembers);
                                List<KnEXDMSGrpMemberDto> modifiedMemebrs = new ArrayList<>();
                                for (KnSubscriberDTO dto : modifiedMembers) {
                                    KnEXDMSGrpMemberDto member = new KnEXDMSGrpMemberDto();
                                    member.setMdn(dto.getMdn());
                                    member.setClientType(dto.getClientType());
                                    member.setName(dto.getNetworkName());
                                    member.setContactType(getXcapContactType(dto.getContact_type()));
                                    modifiedMemebrs.add(member);
                                }
                                corpEXDMSNotifyDto.setModifiedMembers(modifiedMemebrs);
                                knLogger.debug(methodName, " modifiedMemebrs - ", modifiedMemebrs);
                            }

                            if (docChgDTO.getRemovedContactList() != null && !docChgDTO.getRemovedContactList().isEmpty()) {
                                knLogger.debug(methodName, " docChgDTO.getRemovedContactList() - ", KnGDPRTemplate.mdnList(docChgDTO.getRemovedContactList()));
                                corpEXDMSNotifyDto.setRemovedMembers(new ArrayList<>(docChgDTO.getRemovedContactList()));
                            }
                            modifiedContactMap.put(mdn, corpEXDMSNotifyDto);
                        }
                    }
                }
            }
        }
        knLogger.debug(methodName, " modifiedMap - ", modifiedMap);
        knLogger.debug(methodName, " deletedMap - ", deletedMap);
        knLogger.debug(methodName, " modifiedContactMap - ", KnGDPRTemplate.mapKeyMdn(modifiedContactMap));
        corpEXDMSNotifyDtoList.addAll(modifiedMap.values());
        corpEXDMSNotifyDtoList.addAll(deletedMap.values());
        corpEXDMSNotifyDtoList.addAll(modifiedContactMap.values());
        knLogger.debug(methodName, " Returning - ", corpEXDMSNotifyDtoList);
        return corpEXDMSNotifyDtoList;
    }


    private static int getXcapContactType(int dbContType) {
        int xcapContType = 0;
        switch (dbContType) {
            case 1:
                xcapContType = 2;
                break;
            case 2:
                xcapContType = 1;
                break;
        }
        return xcapContType;

    }

    /**
     * Utitlity method for creating JobNotifyDTO
     *
     * @param corpId
     * @param txnId
     * @param userProfileId
     * @param opType
     * @param resourceEntity
     * @param resourceType
     * @return
     */
    public KnAsyncJobDTO createJobNotifyDTO(String corpId, String txnId, String userProfileId
            , int opType, String resourceEntity, int resourceType, String payload) {

        KnAsyncJobDTO JobReqDTO = new KnAsyncJobDTO();
        JobReqDTO.setTxnId(txnId);
        JobReqDTO.setCreationTime(String.valueOf(Instant.now().toEpochMilli()));
        JobReqDTO.setUserProfileId(userProfileId);
        JobReqDTO.setOpType(opType);
        JobReqDTO.setOpStatus(com.kodiak.xdms.server.common.resources.KnConstants.UPM_JOB_STATUS.NEW.Value());
        JobReqDTO.setResourceEntity(resourceEntity);
        JobReqDTO.setResourceType(resourceType);
        JobReqDTO.setCorpId(Integer.parseInt(corpId));
        JobReqDTO.setUpdationTime(String.valueOf(Instant.now().toEpochMilli()));
        JobReqDTO.setPayLoad(payload);

        return JobReqDTO;
    }

    public String getMDNFromURI(String xcapDirURI) {
        String methodName = "getMDNFromURI(String)";
        knLogger.info(methodName, "Entry", xcapDirURI);
        String mdn = null;
        try {
            mdn = xcapDirURI.split("tel:\\+")[1].split("/")[0];
            knLogger.info(methodName, "mdn", mdn);
        } catch (Exception e) {
            knLogger.error(methodName, e);
        }
        knLogger.info(methodName, "Exit", mdn);
        return mdn;
    }


    /**
     * Utility method for sending notification
     *
     * @param opDirChgDTO
     * @param mcId
     * @return
     */

    public boolean sendMCSNotification(KnOPDirChgDTO opDirChgDTO, String mcId, String mcsXcapRootUri) {

        String methodName = "sendMCSNotification(KnOPDirChgDTO,String)";
        boolean isSuccess = false;

        knLogger.info(methodName, "inside sendMCS Notification");
        knLogger.debug(methodName, "opDirChgDTO", opDirChgDTO, "mcId", mcId);

        KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
        if (opDirChgDTO != null) {
            List<KnDocumentChangeDTO> mcsDocumentChangeDTOS = new ArrayList<>();
            mcsNotifyDTO.setXcapRootUri(opDirChgDTO.getXcapRootURI());
            mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());
            KnDocumentChangeDTO mcsDocumentChangeDTO = new KnDocumentChangeDTO();
            Collection<KnOPDocChgDTO> docChgDTOCollection = opDirChgDTO.getDocChgDTO();
            if (docChgDTOCollection != null && !docChgDTOCollection.isEmpty()) {
                List<KnDocChangeListDto> docChangeList = new ArrayList<>();
                docChgDTOCollection.stream().forEach(knOPDocChgDTO -> {
                    String mdn = getMDNFromURI(knOPDocChgDTO.getDocUri());
                    mcsDocumentChangeDTO.setMdn(mdn);
                    mcsDocumentChangeDTO.setDocType(KnMCSNotifyConstants.DOCTYPE.MDN.value());
                    KnDocChangeListDto knDocChangeListDTO = new KnDocChangeListDto();
                    knDocChangeListDTO.setDocUriList(buildMCSDOCURI(knOPDocChgDTO.getDocUri(), mcId));
                    knDocChangeListDTO.setPreviousEtag(knOPDocChgDTO.getPrevEtag());
                    knDocChangeListDTO.setNewEtag(knOPDocChgDTO.getNewEtag());
                    docChangeList.add(knDocChangeListDTO);
                });
                mcsDocumentChangeDTO.setDocChangeList(docChangeList);
                mcsDocumentChangeDTOS.add(mcsDocumentChangeDTO);
            }
            mcsNotifyDTO.setDocumentChange(mcsDocumentChangeDTOS);
            isSuccess = mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTO);
        }
        knLogger.info(methodName, "EXIT: sendMCS Notification - ", isSuccess);
        return isSuccess;
    }
    public List<String> buildMCSDOCURI(String docUri, String mcId) {
        String methodName = "buildMCSDOCURI(String)";
        List<String> docUriList = new ArrayList<>();
        if (docUri.contains(APP_UID_SUBSCRIBER_CONFIG)) {
            docUriList.add(String.format("org.3gpp.%s.user-profile/users/%s/user-profile.xml","mcptt",mcId));
            docUriList.add(String.format("org.3gpp.%s.user-profile/users/%s/user-profile.xml","mcdata",mcId));
            docUriList.add(String.format("org.3gpp.%s.user-profile/users/%s/user-profile.xml","mcvideo",mcId));
        }
        knLogger.info(methodName, "EXIT: ", docUriList);
        return docUriList;
    }

    /**
     * Utility method for sending notification
     *
     * @param respDto
     * @param corpId
     * @return
     */

    public boolean sendMCSGRPNotification(KnCorpResponseDTO respDto, Set<String> mcsXcapRootUris, int corpId) {

        String methodName = "sendMCSGRPNotification(KnCorpResponseDTO,Set<String),int";
        boolean isSuccess = false;

        knLogger.info(methodName, "inside sendMCS Notification");
        knLogger.debug(methodName, "respDto", respDto, "mcsXcapRootUris", mcsXcapRootUris);

        Set<Integer> groupIds = new HashSet<>();
        Map<String, KnOPDirChgDTO> changeLogMap = respDto.getChangeLogMap();

        int deletedGroupId = respDto.getDeletedGroupId();
        if (deletedGroupId > 0) {
            groupIds.add(deletedGroupId);

        } else {
            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                    knLogger.debug(methodName, "MDN - ", entry.getKey());
                    String mdn = entry.getKey();
                    KnOPDirChgDTO opDirChgDTO = entry.getValue();
                    Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                    knLogger.debug(methodName, "changeDocList - ", changeDocList);

                    for (KnOPDocChgDTO docChgDTO : changeDocList) {
                        knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                        if (docChgDTO.getDocUri().contains(KnConstants.APP_UID_CORP_GROUP))
                            groupIds.add(docChgDTO.getGroupId());
                    }
                }
            }
        }


        knLogger.debug(methodName, "uniqueue groupId's", groupIds);

        KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();

        mcsNotifyDTO.setNotifyType(KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());

        if (!groupIds.isEmpty()) {
            List<KnDocumentChangeDTO> mcsDocumentChangeDTOS = new ArrayList<>();

            KnDocumentChangeDTO mcsDocumentChangeDTO = new KnDocumentChangeDTO();
            mcsDocumentChangeDTO.setDocType(KnMCSNotifyConstants.DOCTYPE.GROUP.value());

            if (changeLogMap != null && !changeLogMap.isEmpty()) {
                List<KnDocChangeListDto> docChangeList = new ArrayList<>();
                int i = 0;
                for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                    KnOPDirChgDTO opDirChgDTO = entry.getValue();
                    String pocHome = opDirChgDTO.getPocHome();
                    Collection<KnOPDocChgDTO> changeDocList = opDirChgDTO.getDocChgDTO();
                    knLogger.debug(methodName, "changeDocList - ", changeDocList);
                    for (KnOPDocChgDTO docChgDTO : changeDocList) {
                        knLogger.debug(methodName, "docChgDTO  - ", docChgDTO);
                        int groupId = respDto.getDeletedGroupId();

                        if(respDto.getMdnCorpId()>0){
                            corpId = respDto.getMdnCorpId();
                        }
                        if (groupId <= 0) {
                            groupId = docChgDTO.getGroupId();
                        }
                        //If the document type is corporate group
                        if (groupIds.contains(groupId) && i < groupIds.size()) {
                            String groupUri = buildMCSGRPURI(corpId, groupId, docChgDTO.getDocUri(), pocHome);
                            if(groupUri!=null && groupUri.trim().length()>0) {
                                KnDocChangeListDto knDocChangeListDTO = new KnDocChangeListDto();
                                knDocChangeListDTO.setDocUri(groupUri);
                                knDocChangeListDTO.setPreviousEtag(docChgDTO.getPrevEtag());
                                knDocChangeListDTO.setNewEtag(docChgDTO.getNewEtag());
                                docChangeList.add(knDocChangeListDTO);
                            }
                            i++;
                        }
                    }
                }
                mcsDocumentChangeDTO.setDocChangeList(docChangeList);
                mcsDocumentChangeDTOS.add(mcsDocumentChangeDTO);
            }
            mcsNotifyDTO.setDocumentChange(mcsDocumentChangeDTOS);
            knLogger.debug(methodName, "mcsXcapRootUris - ", mcsXcapRootUris);

            if (null != mcsXcapRootUris && !mcsXcapRootUris.isEmpty()) {
                for (String xcapRootUri : mcsXcapRootUris) {
                    mcsNotifyDTO.setXcapRootUri(xcapRootUri);
                    mcsNotifyDTO.getDocumentChange().forEach(knDocumentChangeDTO -> {
                        knDocumentChangeDTO.setXcapRootUri(xcapRootUri);
                    });
                    knLogger.debug(methodName, "after updating xcap root uri - ", mcsNotifyDTO);
                    isSuccess = mcsDocChangeNotifier.generateMCSNotification(mcsNotifyDTO);
                }

            }
        }
        return isSuccess;
    }

    public String buildMCSGRPURI(int corpId, int groupId,String docUri,String pocHome) {
        String methodName = "buildMCSGRPURI(int,int,String)";
        knLogger.info(methodName, "Entry", corpId,"groupId - ",groupId,"docUri-",docUri);

        StringBuilder mcsDocUri = new StringBuilder();
        String sipProxyUri = null;
        try {
            KnSIPProxySvcConfigDTO knSIPProxySvcConfigDTO= retrieveSIPProxySvcConfig(pocHome);
            sipProxyUri = knSIPProxySvcConfigDTO.getSipProxyURI();
        }catch (Exception e){
            knLogger.error(methodName,"exception while fetching sip proxy config");
        }

        if (docUri.contains(KnConstants.APP_UID_CORP_GROUP)) {
            mcsDocUri.append("org.openmobilealliance.groups/global/byGroupID/")
                    .append(SIP)
                    .append(corpId)
                    .append(DOT)
                    .append(groupId)
                    .append(AT)
                    .append(sipProxyUri);

        }
        knLogger.info(methodName, "Exit:", mcsDocUri);
        return mcsDocUri.toString();
    }
    /**
     * DG.SIPProxySvcConfig
     * method to retrieve the SIP proxy Svc Config Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param pocPttServerId
     * @return KnSipProxySvcConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnSIPProxySvcConfigDTO retrieveSIPProxySvcConfig(String pocPttServerId) throws KnProvBOException {
        String methodName = "retrieveSIPProxySvcConfig(String)";
        knLogger.debug(methodName, "ENTRY: retrieve SIP proxy Svc Config f - ", pocPttServerId);
        KnSIPProxySvcConfigDTO sipProxySvcConfigDTO = null;
        Map<String, KnSIPProxySvcConfigDTO> sipProxyvcConfigMap = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();

            sipProxyvcConfigMap = (Map<String, KnSIPProxySvcConfigDTO>) cacheManager.get(KnProvCacheKeys.SIP_PROXY_SVC_CONFIG);
            if (sipProxyvcConfigMap != null && sipProxyvcConfigMap.containsKey(pocPttServerId)) {
                sipProxySvcConfigDTO = sipProxyvcConfigMap.get(pocPttServerId);
            }
            knLogger.debug(methodName, "retrieved  SIP proxy Svc Config DTO from cache - ", sipProxySvcConfigDTO);

            if (sipProxySvcConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the SIP proxy Svc Config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                sipProxySvcConfigDTO = provXdmServerDAO.retrieveSIPProxySvcConfig(pocPttServerId, null);
                if (sipProxyvcConfigMap == null) {
                    sipProxyvcConfigMap = new HashMap<String, KnSIPProxySvcConfigDTO>();
                    sipProxyvcConfigMap.put(pocPttServerId, sipProxySvcConfigDTO);
                } else {
                    sipProxyvcConfigMap.put(pocPttServerId, sipProxySvcConfigDTO);
                }
                cacheManager.put(KnProvCacheKeys.SIP_PROXY_SVC_CONFIG, sipProxyvcConfigMap);
            }


            knLogger.debug(methodName, "SIP proxy Svc Config DTO -  ", sipProxySvcConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.SIP_PROXY_SVC_CONFIG_NOT_FOUND,
                        "SIP proxy Svc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);


        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC registrar service config ", e);
        }
        knLogger.debug(methodName, "EXIT : POC registrar Service Config -> ", sipProxySvcConfigDTO);

        return sipProxySvcConfigDTO;
    }

    public boolean addJob(KnAsyncJobDTO asyncJobDTO) {
        String methodName = "addJob(KnAsyncJobDTO)";
        boolean result = false;
        knLogger.info(methodName,"Entry:-");
        try {
            generalCacheUtil.createAsyncJob(asyncJobDTO);
            knLogger.info(methodName,"Job Created Successfully:-");
            result = true;
        } catch (KnDAOException e) {
            knLogger.error(methodName,"exception while creating job - ",e.getMessage());
        }
        return result;
    }

    public List<KnCorpEXDMSNotifyDto> getDeleteGrpMicroSrvNotifyDto(KnCorpInfoResDTO respDto) {
        String methodName = "getDeleteGrpMicroSrvNotifyDto()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "respDto - ", respDto);
        List<KnCorpEXDMSNotifyDto> delGrpNotifyList = new ArrayList<>();
        Map<Integer, String> delGrpPocHomeMap = respDto.getDelGrpPocHomeMap();
        if (delGrpPocHomeMap != null && !delGrpPocHomeMap.isEmpty()) {
            Set<Integer> grpIdList = delGrpPocHomeMap.keySet();
            for (Integer grpId : grpIdList) {
                KnCorpEXDMSNotifyDto notifyDto = new KnCorpEXDMSNotifyDto();
                notifyDto.setType(MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value());
                notifyDto.setId(MICROSERVICES_EVENT_TYPE.DELETE_CORP_GROUP.value() + "_" + grpId);
                notifyDto.setNotifyEventType(MICROSERVICES_NOTIFY_EVENT_TYPE.GROUP_NOTIFY_EVENTS.value());
                notifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                notifyDto.setGrpId(grpId);
                notifyDto.setCorpid(respDto.getMdnCorpId());
                notifyDto.setCreatedBy(respDto.getGroupCreatedBy());
                String pocHome = delGrpPocHomeMap.get(grpId);
                notifyDto.setPocHome(pocHome);

                knLogger.info(methodName, "poc home value : ", pocHome, " grpId - ", grpId);
                knLogger.debug(methodName, "notifyDto - ", notifyDto);
                delGrpNotifyList.add(notifyDto);
            }
        }

        knLogger.debug(methodName, "exdmsNotifyDtoList - ", delGrpNotifyList);

        return delGrpNotifyList;
    }
}
