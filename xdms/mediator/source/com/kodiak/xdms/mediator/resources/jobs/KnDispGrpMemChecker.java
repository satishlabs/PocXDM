/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnDispGrpMemChecker.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya        30-11-2011      7.2
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.request.KnCorpDispMemReqDTO;
import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;
import com.kodiak.common.commdto.response.KnXDMCorpDispatchGrpMemberJobRespDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dto.KnPayloadIP;
import com.kodiak.common.dto.KnPendingTxnInfoDTO;
import com.kodiak.common.dto.KncontactNotifyInfoDTO;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.impl.KnXDMMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpContactInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpGroupInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpSublistInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdateSubsInfoDTO;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.*;
import static com.kodiak.common.resources.KnGeneralUtil.ASYNC_OPS_ID.NOT_ASSINGED;

public class KnDispGrpMemChecker extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnDispGrpMemChecker.class);

    private static final long serialVersionUID = 7526471155622676267L;

    private KnXDMCorpMediator corpMediator;
    private KnXDMMediator xdmMediator;
    private Collection<String> deletedMembers;
    private Collection<String> addedMembers;
    private Collection<String> addedLocWatcher;
    private Collection<String> removedLocWatcher;
    private IXcapDiffNotifierIntf notifier;
    private int maxNotificationSize;
    private int corpId;
    private KnCorpDispMemReqDTO dispatchGrpMemberJobDto;
    private String updatedMdn;
    private ICorpClientIntf corpClientIntf;
    private KnXDMCommonMediator commonMediator = null;
    private boolean upmCall = Boolean.FALSE;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private boolean corplevelLocationFalg ;
    private KnCorpCommonInfoUtil commonInfoUtil;
    public KnDispGrpMemChecker() {
        corpMediator = KnXDMCorpMediator.getInstance();
        xdmMediator = KnXDMMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        corpClientIntf = new KnCorpClientImpl();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        commonMediator = KnXDMCommonMediator.getInstance();
        contactInfoUtil = new KnCorpContactInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    public void setMaxNotificationSize(int maxNotificationSize) {
        this.maxNotificationSize = maxNotificationSize;
        notifier.setMaxNotfnsPerJob(this.maxNotificationSize);
    }

    public void setDeletedMembers(Collection<String> deletedMembers) {
        this.deletedMembers = deletedMembers;
    }

    public void setAddedMembers(Collection<String> addedMembers) {
        this.addedMembers = addedMembers;
    }

    public void setAddedLocWatcher(Collection<String> addedLocWatcher) {
        this.addedLocWatcher = addedLocWatcher;
    }

    public void setRemovedLocWatcher(Collection<String> removedLocWatcher) {
        this.removedLocWatcher = removedLocWatcher;
    }

    public String getUpdatedMdn() {
        return updatedMdn;
    }

    public void setUpdatedMdn(String updatedMdn) {
        this.updatedMdn = updatedMdn;
    }

    public Collection<String> getDeletedMembers() {
        return deletedMembers;
    }

    public Collection<String> getAddedMembers() {
        return addedMembers;
    }

    public Collection<String> getAddedLocWatcher() {
        return addedLocWatcher;
    }

    public Collection<String> getRemovedLocWatcher() {
        return removedLocWatcher;
    }

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        knLogger.info(methodName, "ENTRY : Job Execution starts - ");
        knLogger.debug(methodName, "Properties: addedMembers - ", KnGDPRTemplate.mdnList(addedMembers), ", deletedMembers - ", KnGDPRTemplate.mdnList(deletedMembers));
        knLogger.debug(methodName, "Properties: addedLocWatcher - ", addedLocWatcher, ", removedLocWatcher - ", removedLocWatcher);
        boolean isSuccess = false;
        KnXDMCorpDispatchGrpMemberJobRespDTO respDTO = null;
        List<KnXDMSubsProvInfoDTO> subsProvInputDTOList = new ArrayList<KnXDMSubsProvInfoDTO>();
        KnPersisterTxn persisterTxn = null;
        try {
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            Collection<KnXDMCorpContactDTO> addedMemberList = new ArrayList<KnXDMCorpContactDTO>();
            if (addedMembers != null && addedMembers.size() > 0) {
                Collection<String> profileMdns = contactInfoUtil.getProfileMdnsForDisContact(addedMembers, localPttId, corpId, DISABLED, persisterTxn);
                if (!profileMdns.isEmpty()) {
                    addedMembers.addAll(profileMdns);
                }
                for (String mdn : addedMembers) {
                    KnXDMCorpContactDTO contactDTO = new KnXDMCorpContactDTO();
                    contactDTO.setMdn(mdn);
                    addedMemberList.add(contactDTO);
                }
            }
            knLogger.debug(methodName, "addedMemberList - ", addedMemberList);
            Collection<KnXDMCorpContactDTO> deletedMemberList = new ArrayList<KnXDMCorpContactDTO>();
            if (deletedMembers != null && deletedMembers.size() > 0) {
                Collection<String> profileMdns = contactInfoUtil.getProfileMdnsForDisContact(deletedMembers, localPttId, corpId, ENABLED, persisterTxn);
                if (!profileMdns.isEmpty()) {
                    deletedMembers.addAll(profileMdns);
                }
                for (String mdn : deletedMembers) {
                    KnXDMCorpContactDTO contactDTO = new KnXDMCorpContactDTO();
                    contactDTO.setMdn(mdn);
                    deletedMemberList.add(contactDTO);
                }
            }
            HashSet<String> delMembersSet = deletedMembers != null ? new HashSet<>(deletedMembers) : new HashSet<>();
            if (addedMembers != null && deletedMembers != null && !addedMembers.isEmpty() && !deletedMembers.isEmpty()) {
                Set<String> addedMembersSet = new HashSet<>(addedMembers);
                delMembersSet.removeAll(addedMembersSet);
            }
            String xdmcorpfs2Set = commonInfoUtil.selectCorpFS(corpId,true, persisterTxn);
            if (xdmcorpfs2Set != null) {
                corplevelLocationFalg = KnGeneralUtil.getFeatureBitValue(xdmcorpfs2Set, 1);
            }
            knLogger.info(methodName , "corplevelLocationFalg - ", corplevelLocationFalg);
            dispatchGrpMemberJobDto = new KnCorpDispMemReqDTO();
            if (!corplevelLocationFalg && delMembersSet != null && !delMembersSet.isEmpty()) {
                dispatchGrpMemberJobDto.setDeletedgroupMemberList(deletedMemberList);
            } else if (corplevelLocationFalg && delMembersSet != null && !delMembersSet.isEmpty()) {
                String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
                ArrayList<String> intNonDispMembers = contactInfoUtil.getInternalNonDispatchMember(delMembersSet, corpId, xdmsHome, persisterTxn);
                Map<String, KnCorpSubscriberDTO> subscBasicDetails = contactInfoUtil.getSubscIsMemOfDispGrpDetails(intNonDispMembers, xdmsHome, persisterTxn);
                //Checking if any deleted is part of a dispatch group
                Collection<String> nonDispGrpMember = groupInfoUtil.getSubsNotInDispatchGroupFromMDNList(delMembersSet, corpId, xdmsHome, persisterTxn);
                //Checking if the delete subscriber belongs to  a dispatcher contactList
                Collection<String> nonDispContact = contactInfoUtil.getDispContacts(nonDispGrpMember, corpId, xdmsHome, persisterTxn);
                // checking if the deleted members belong to any locWatcher.
                Collection<String> nonLocWatcherMember = groupInfoUtil.getLocWatcherMdn(nonDispGrpMember, corpId, xdmsHome, persisterTxn);
                knLogger.info(methodName, "nonLocWatcherMember -- ", KnGDPRTemplate.mdnList(nonLocWatcherMember),
                        "nonDispGrpMember", KnGDPRTemplate.mdnList(nonDispGrpMember), "nonDispContact", KnGDPRTemplate.mdnList(nonDispContact));
                nonLocWatcherMember.addAll(nonDispContact);
                HashSet<String> delMembersSet1 = new HashSet<>();
                if (nonDispGrpMember != null) {
                    for (String mdn : nonDispGrpMember) {
                        if (subscBasicDetails != null && subscBasicDetails.get(mdn) != null) {
                            Integer dispMemInd = subscBasicDetails.get(mdn).getDispatchGrpmember();
                            if (dispMemInd != null && dispMemInd == 1) {
                                if (!nonLocWatcherMember.contains(mdn)) {
                                    delMembersSet1.add(mdn);
                                }
                            }
                        }
                    }
                }
                if (!delMembersSet1.isEmpty()) {
                    commonInfoUtil.updateDispMem(delMembersSet, persisterTxn);
                }
            }
            dispatchGrpMemberJobDto.setAddedgroupMemberList(addedMemberList);
            dispatchGrpMemberJobDto.setCorpId(this.corpId);
            dispatchGrpMemberJobDto.setUpdatedMdn(updatedMdn);

            respDTO = corpMediator.getSubscDetailsToUpdateIsDispatchMem(dispatchGrpMemberJobDto, persisterTxn);
            if (KnConstants.RESPONSE_STATUS.SUCCESS.value() != respDTO.getResponseStatus()) {
                throw new KnXDMServerException(respDTO.getResponseCode(), respDTO.getResponseMessage());
            }
            subsProvInputDTOList = respDTO.getSubscUpdateList();
            knLogger.debug(methodName, "subsProvInputDTOList - ", subsProvInputDTOList);
            List<String> mdns = new ArrayList<>();
            Map<String, Boolean> mdnMapList = new HashMap<>();
            KnXDMRespDTO xdmRespDTO = null;
            if (subsProvInputDTOList != null && !subsProvInputDTOList.isEmpty()) {
                knLogger.debug(methodName, "Calling Prov API - ");
                Collection<List<KnXDMSubsProvInfoDTO>> subSubscriberList = splitList(subsProvInputDTOList, 50);
                for (List<KnXDMSubsProvInfoDTO> subsList : subSubscriberList) {
                    HashMap<String, Integer> subDispDat = new HashMap<String, Integer>();
                    for (KnXDMSubsProvInfoDTO sub : subsList) {
                        subDispDat.put(sub.getMdn(), sub.getDispatchGroupMember());
                        mdns.add(sub.getMdn());
                    }
                    xdmRespDTO = (KnXDMRespDTO) xdmMediator.updateDispForSubscribers(subDispDat, ONDEMLOCATION.value());
                    mdnMapList.putAll(xdmRespDTO.getMdnListMap());
                }
                isSuccess = true;
            }

            Map<String, Integer> locWatcherMap = new HashMap<>();
            if(addedLocWatcher != null){
                for (String locWatcher : addedLocWatcher){
                    locWatcherMap.put(locWatcher, ENABLED);
                }
            }
            if (removedLocWatcher != null && !corplevelLocationFalg) {
                for (String removedLocWatcher : removedLocWatcher){
                    locWatcherMap.put(removedLocWatcher, DISABLED);
                }
            }
            if (!locWatcherMap.isEmpty()) {
                knLogger.debug(methodName, "Calling Prov API - LocWatcher " + locWatcherMap, " upmCall- " , upmCall);
                xdmRespDTO = (KnXDMRespDTO) xdmMediator.updateDispForSubscribers(locWatcherMap, LOCATIONSUBSCRIPTION.value());
               if(upmCall) {
                   xdmRespDTO = (KnXDMRespDTO) xdmMediator.updateDispForSubscribers(locWatcherMap, ONDEMLOCATION.value());
               }
                mdnMapList.putAll(xdmRespDTO.getMdnListMap());
                isSuccess = true;
            }
            persisterTxn.save();
            knLogger.debug(methodName, "mdns - ", KnGDPRTemplate.mdnList(mdns), "xdmRespDTO - ", xdmRespDTO);
            // Calling eTag update for reverse look up. :
            if(!mdns.isEmpty() && !mdnMapList.isEmpty()) {
                commonMediator.initiateEtagMgmtJob(mdnMapList);
            }
        } catch (KnXDMServerException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "KnCorpBOException occured while executing the job - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occured while executing the job - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        } finally {
            knLogger.info(methodName, "EXIT:");
        }
        return isSuccess;
    }

    public List<String> executeTaskForAsyncPath(String pttServerId) throws KnJobSchedulerException {
        String methodName = "executeTaskForAsyncPath()";
        knLogger.info(methodName, "ENTRY : Job Execution starts - ");
        knLogger.debug(methodName, "Properties: addedMembers - ", KnGDPRTemplate.mdnList(addedMembers), ", deletedMembers - ", KnGDPRTemplate.mdnList(deletedMembers));
        knLogger.debug(methodName, "Properties: addedLocWatcher - ", addedLocWatcher, ", removedLocWatcher - ", removedLocWatcher);
        boolean isSuccess = false;
        KnXDMCorpDispatchGrpMemberJobRespDTO respDTO = null;
        List<KnXDMSubsProvInfoDTO> subsProvInputDTOList = new ArrayList<KnXDMSubsProvInfoDTO>();
        KnPersisterTxn persisterTxn = null;
        List<String> notifiedMdnList = new ArrayList<>();
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            Collection<KnXDMCorpContactDTO> addedMemberList = new ArrayList<KnXDMCorpContactDTO>();
            if (addedMembers != null && addedMembers.size() > 0) {
                for (String mdn : addedMembers) {
                    KnXDMCorpContactDTO contactDTO = new KnXDMCorpContactDTO();
                    contactDTO.setMdn(mdn);
                    addedMemberList.add(contactDTO);
                }
            }
            knLogger.debug(methodName, "addedMemberList - ", addedMemberList);
            Collection<KnXDMCorpContactDTO> deletedMemberList = new ArrayList<KnXDMCorpContactDTO>();
            if (deletedMembers != null && deletedMembers.size() > 0) {
                for (String mdn : deletedMembers) {
                    KnXDMCorpContactDTO contactDTO = new KnXDMCorpContactDTO();
                    contactDTO.setMdn(mdn);
                    deletedMemberList.add(contactDTO);
                }
            }
            dispatchGrpMemberJobDto = new KnCorpDispMemReqDTO();
            knLogger.debug(methodName, "deletedMemberList - ", deletedMemberList);
            dispatchGrpMemberJobDto.setAddedgroupMemberList(addedMemberList);
            dispatchGrpMemberJobDto.setDeletedgroupMemberList(deletedMemberList);
            dispatchGrpMemberJobDto.setCorpId(this.corpId);
            dispatchGrpMemberJobDto.setUpdatedMdn(updatedMdn);

            respDTO = corpMediator.getSubscDetailsToUpdateIsDispatchMem(dispatchGrpMemberJobDto, persisterTxn);
            if (KnConstants.RESPONSE_STATUS.SUCCESS.value() != respDTO.getResponseStatus()) {
                throw new KnXDMServerException(respDTO.getResponseCode(), respDTO.getResponseMessage());
            }
            subsProvInputDTOList = respDTO.getSubscUpdateList();
            knLogger.debug(methodName, "subsProvInputDTOList - ", subsProvInputDTOList);
            List<String> mdns = new ArrayList<>();
            Map<String, Boolean> mdnMapList = new HashMap<>();
            KnXDMRespDTO xdmRespDTO = null;
            if (subsProvInputDTOList != null && !subsProvInputDTOList.isEmpty()) {
                knLogger.debug(methodName, "Calling Prov API - ");
                Collection<List<KnXDMSubsProvInfoDTO>> subSubscriberList = splitList(subsProvInputDTOList, 50);
                for (List<KnXDMSubsProvInfoDTO> subsList : subSubscriberList) {
                    HashMap<String, Integer> subDispDat = new HashMap<String, Integer>();
                    for (KnXDMSubsProvInfoDTO sub : subsList) {
                        subDispDat.put(sub.getMdn(), sub.getDispatchGroupMember());
                        mdns.add(sub.getMdn());
                    }
                    xdmRespDTO = (KnXDMRespDTO) xdmMediator.updateDispForSubscribers(subDispDat, ONDEMLOCATION.value());
                    mdnMapList.putAll(xdmRespDTO.getMdnListMap());
                    notifiedMdnList.addAll(xdmRespDTO.getNotifiedMdnList());
                }
                isSuccess = true;
            }

            Map<String, Integer> locWatcherMap = new HashMap<>();
            if(addedLocWatcher != null){
                for (String locWatcher : addedLocWatcher){
                    locWatcherMap.put(locWatcher, ENABLED);
                }
            }
            if(removedLocWatcher != null){
                for (String removedLocWatcher : removedLocWatcher){
                    locWatcherMap.put(removedLocWatcher, DISABLED);
                }
            }
            if (!locWatcherMap.isEmpty()) {
                knLogger.debug(methodName, "Calling Prov API - LocWatcher " + locWatcherMap, " upmCall- " , upmCall);
                xdmRespDTO = (KnXDMRespDTO) xdmMediator.updateDispForSubscribers(locWatcherMap, LOCATIONSUBSCRIPTION.value());
                if(upmCall) {
                    xdmRespDTO = (KnXDMRespDTO) xdmMediator.updateDispForSubscribers(locWatcherMap, ONDEMLOCATION.value());
                }
                mdnMapList.putAll(xdmRespDTO.getMdnListMap());
                notifiedMdnList.addAll(xdmRespDTO.getNotifiedMdnList());
                isSuccess = true;
            }
            persisterTxn.save();
            knLogger.debug(methodName, "mdns - ", KnGDPRTemplate.mdnList(mdns), "xdmRespDTO - ", xdmRespDTO);
            // Calling eTag update for reverse look up. :
            if(!mdns.isEmpty() && !mdnMapList.isEmpty()) {
                //commonMediator.initiateEtagMgmtJob(mdnMapList); //jusbin
                insertMdnsTOAsyncJob(mdns, pttServerId);
            }
        } catch (KnXDMServerException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "KnCorpBOException occured while executing the job - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occured while executing the job - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        } finally {
            knLogger.info(methodName, "EXIT:");
        }
        return notifiedMdnList;
    }

    private void insertMdnsTOAsyncJob(List<String> mdns, String pttServerId) {
        String methodName = "insertMdnsTOAsyncJob(mdns)";
        knLogger.debug(methodName, "Entry - ");
        try {
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(pttServerId);
            mdns.forEach(mdn->{
                KnPayloadIP knPayloadIP = new KnPayloadIP();
                BitSet taskBitSet = new BitSet();
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.notifyAllGroupMembers.get(),true);
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isContactNotifyRequired.get(),true);
                //Since we are updating only the etag any setting would work here,,added,removed,replaced
                KncontactNotifyInfoDTO kncontactNotifyInfoDTO = new KncontactNotifyInfoDTO();
                kncontactNotifyInfoDTO.setAddedContatcs(Collections.singletonList(mdn));
                knPayloadIP.setKncontactNotifyInfoDTO(kncontactNotifyInfoDTO);
                KnPendingTxnInfoDTO pendingTxnInfoDTO = new KnPendingTxnInfoDTO();
                pendingTxnInfoDTO.setEntityId(mdn);
                pendingTxnInfoDTO.setTxnId(UUID.randomUUID().toString());
                pendingTxnInfoDTO.setPayload(KnGeneralUtil.convertKnPaylaodIPToJsonString(knPayloadIP));
                pendingTxnInfoDTO.setPriority(1);
                pendingTxnInfoDTO.setEntityType(1);
                pendingTxnInfoDTO.setCorpId(0);
                pendingTxnInfoDTO.setOpsId(NOT_ASSINGED.get());
                pendingTxnInfoDTO.setEntityType(KnGeneralUtil.EntityName.MDN.get());
                pendingTxnInfoDTO.setNotifyFlag(2);
                pendingTxnInfoDTO.setTaskBitSet(KnGeneralUtil.convertBitSetToLong(taskBitSet));
                try {
                    xdmDAO.insertIntoAsyncTable(pendingTxnInfoDTO, null);
                } catch (KnDAOException e) {
                    knLogger.debug(methodName, "Insertion failed for mdn ", mdn , e);
                }

            });

        } catch (Exception e) {
            knLogger.error("insertMdnsTOAsyncJob()", "Error while inserting mdns to async job - ", e);
        }
    }

    private Collection<KnXcapDiffDirChgNotifyDTO> prepareNotification(Collection<KnOPUpdateSubsInfoDTO> provResp) {
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs = new ArrayList<KnXcapDiffDirChgNotifyDTO>();
        String methodName = "prepareNotification(provResp)";
        knLogger.debug(methodName, "provResp - ", provResp);
        for (KnOPUpdateSubsInfoDTO subsInfoDTO : provResp) {
            KnXcapDiffDirChgNotifyDTO xcapDiffDirChgNotifyDTO = new KnXcapDiffDirChgNotifyDTO();
            xcapDiffDirChgNotifyDTO.setXcapRootUri(subsInfoDTO.getDirChgDTO().getXcapRootURI());
            xcapDiffDirChgNotifyDTO.setDirNewEtag(subsInfoDTO.getDirChgDTO().getDirNewEtag());
            xcapDiffDirChgNotifyDTO.setDirPrevEtag(subsInfoDTO.getDirChgDTO().getDirPrevEtag());
            xcapDiffDirChgNotifyDTO.setDirURI(subsInfoDTO.getDirChgDTO().getDirUri());
            Collection<KnOPDocChgDTO> docChgDTOColl = subsInfoDTO.getDirChgDTO().getDocChgDTO();
            knLogger.debug(methodName, "docChgDTOColl - ", docChgDTOColl);
            Collection<KnXcapDiffDocDTO> docDiffObj = new ArrayList<KnXcapDiffDocDTO>();
            if (docChgDTOColl != null) {
                for (KnOPDocChgDTO docChgDTO : docChgDTOColl) {
                    KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                    xcapDiffDocDTO.setDocChangeType(docChgDTO.getDocumentChgType());
                    xcapDiffDocDTO.setDocEtag(docChgDTO.getNewEtag());
                    xcapDiffDocDTO.setDocumentSelector(docChgDTO.getDocUri());
                    xcapDiffDocDTO.setDocUri(docChgDTO.getEntryUri());
                    docDiffObj.add(xcapDiffDocDTO);
                    knLogger.debug(methodName, "xcapDiffDocDTO - ", xcapDiffDocDTO);
                }
            }

            xcapDiffDirChgNotifyDTO.setDocDiffObj(docDiffObj);
            xcapDiffDirChgNotifyDTO.setPocHome(subsInfoDTO.getDirChgDTO().getPocHome());
            xcapDiffDirChgNotifyDTO.setPresenceHome(subsInfoDTO.getDirChgDTO().getPresenceHome());
            xcapDiffNotifyDTOs.add(xcapDiffDirChgNotifyDTO);
        }
        knLogger.debug(methodName, "xcapDiffNotifyDTOs - ", xcapDiffNotifyDTOs);
        return xcapDiffNotifyDTOs;
    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    private Collection<List<KnXDMSubsProvInfoDTO>> splitList(List<KnXDMSubsProvInfoDTO> initialList, int sublistSize) {

        String methodName = "splitList(initialList, sublistSize)";
        knLogger.debug(methodName, "Splitting initialList. sublistSize - ", sublistSize);
        int noOfSublists = initialList.size() % sublistSize == 0 ? initialList.size() / sublistSize
                : (initialList.size() / sublistSize) + 1;
        Collection<List<KnXDMSubsProvInfoDTO>> jobList = new ArrayList<List<KnXDMSubsProvInfoDTO>>(noOfSublists);
        for (int i = 0; i < noOfSublists; i++) {
            int maxLength = ((i + 1) * sublistSize > initialList.size()) ? initialList.size()
                    : (i + 1) * sublistSize;
            List<KnXDMSubsProvInfoDTO> subList = new ArrayList<KnXDMSubsProvInfoDTO>(
                    initialList.subList(i * sublistSize, maxLength));
            jobList.add(subList);
        }
        return jobList;
    }

    @Override
    public String toString() {
        return "KnDispGrpMemChecker{" +
                "corpMediator=" + corpMediator +
                ", xdmMediator=" + xdmMediator +
                ", deletedMembers=" + KnGDPRTemplate.mdnList(deletedMembers) +
                ", addedMembers=" + KnGDPRTemplate.mdnList(addedMembers) +
                ", addedLocWatcher=" + addedLocWatcher +
                ", removedLocWatcher=" + removedLocWatcher +
                ", notifier=" + notifier +
                ", maxNotificationSize=" + maxNotificationSize +
                ", corpId=" + corpId +
                ", dispatchGrpMemberJobDto=" + dispatchGrpMemberJobDto +
                ", updatedMdn='" + KnGDPRTemplate.mdn(updatedMdn) + '\'' +
                ", corpClientIntf=" + corpClientIntf +
                ", commonMediator=" + commonMediator +
                '}';
    }
}
