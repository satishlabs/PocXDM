/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPAMDispGrpMemChecker.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnProfileNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDispatchDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpDispatchGrpMemInfoDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDispatchGrpMemInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPBulkRespDTO;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION;

public class KnPAMDispGrpMemChecker extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMDispGrpMemChecker.class);

    IProvClientIntf provClientIntf;
    ICorpClientIntf corpClientIntf;
    private Collection<String> deletedMembers;
    private IXcapDiffNotifierIntf notifier;
    private int corpId;
    public static final int DEFAULT_NOTIFY_SIZE = 4;

    public KnPAMDispGrpMemChecker() {
        provClientIntf = KnProvClientImpl.getInstance();
        corpClientIntf = new KnCorpClientImpl();
        notifier = new KnXcapDiffNotifierImpl();
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public void setDeletedMembers(Collection<String> deletedMembers) {
        this.deletedMembers = deletedMembers;
    }

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        final String methodName = "executeTask()";
        knLogger.entry(methodName, "deletedMembers - ", KnGDPRTemplate.mdnList(deletedMembers));
        boolean isSuccess = false;
        KnCorpDispatchGrpMemInfoRespDTO respDTO;
        List<KnXDMSubsProvInfoDTO> subsProvInputDTOList;
        KnPersisterTxn persisterTxn = null;
        Map<String, KnOPDispatchDirChgDTO> mdnDispatchChgDTOMap = null;
        KnOPBulkRespDTO provRespDTO = null;

        try {
            //opening transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();


            Collection<KnXDMCorpContactDTO> deletedMemberList = new ArrayList<>();
            if (deletedMembers != null && deletedMembers.size() > 0) {
                for (String mdn : deletedMembers) {
                    KnXDMCorpContactDTO contactDTO = new KnXDMCorpContactDTO();
                    contactDTO.setMdn(mdn);
                    deletedMemberList.add(contactDTO);
                }
            }
            KnIPCorpDispatchGrpMemInfoDto dispatchGrpMemberJobDto = new KnIPCorpDispatchGrpMemInfoDto();

            Collection<KnXDMCorpContactDTO> memberListToCheck = new ArrayList<>();
            memberListToCheck.addAll(deletedMemberList);
            dispatchGrpMemberJobDto.setGroupMemberListToChk(memberListToCheck);
            dispatchGrpMemberJobDto.setDeletedGroupMembers(deletedMemberList);
            dispatchGrpMemberJobDto.setCorpId(this.corpId);

            respDTO = corpClientIntf.getSubscDetailsToUpdateIsDispatchMem(dispatchGrpMemberJobDto, persisterTxn);
            if (KnConstants.RESPONSE_STATUS.SUCCESS.value() != respDTO.getStatus()) {
                throw new KnXDMServerException(respDTO.getStatusCode(), respDTO.getMessage());
            }
            subsProvInputDTOList = respDTO.getSubscIsDispMemDetailsList();
            knLogger.debug(methodName, "subsProvInputDTOList - ", subsProvInputDTOList);

            if (!subsProvInputDTOList.isEmpty()) {
                mdnDispatchChgDTOMap = new HashMap<>();
                knLogger.debug(methodName, "Calling Prov API - ");
                Collection<List<KnXDMSubsProvInfoDTO>> subSubscriberList = splitList(subsProvInputDTOList, 50);
                for (List<KnXDMSubsProvInfoDTO> subsList : subSubscriberList) {
                    HashMap<String, Integer> subDispDat = new HashMap<>();
                    for (KnXDMSubsProvInfoDTO sub : subsList) {
                        subDispDat.put(sub.getMdn(), sub.getDispatchGroupMember());
                    }
                    provRespDTO = provClientIntf.updateDispForSubscribers(subDispDat, ONDEMLOCATION.value(), persisterTxn);
                    knLogger.debug(methodName, "provRespDTO", provRespDTO);
                    mdnDispatchChgDTOMap.putAll(provRespDTO.getMdnDispatcherChgDTOMap());
                }
                isSuccess = true;
            }
            if (mdnDispatchChgDTOMap != null) {
                notifier.setMaxNotfnsPerJob(DEFAULT_NOTIFY_SIZE);
                boolean isNotified = notifier.sendXcapDiffNotifications(prepareNotification(mdnDispatchChgDTOMap, provRespDTO));
                knLogger.debug(methodName, "Notification status - ", isNotified);
            }
            persisterTxn.save();
        } catch (KnXDMServerException e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "KnCorpBOException occured while executing the job - ", e);
        } catch (Exception e) {
            rollback(persisterTxn);
            knLogger.error(methodName, "Unexpected Exception occured while executing the job - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.exit(methodName, isSuccess);
        return isSuccess;
    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.entry("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.", e);
        }
    }

    private Collection<List<KnXDMSubsProvInfoDTO>> splitList(List<KnXDMSubsProvInfoDTO> initialList, int sublistSize) {

        final String methodName = "splitList(initialList, sublistSize)";
        knLogger.entry(methodName, "Splitting initialList. sublistSize - ", sublistSize);
        int noOfSublists = initialList.size() % sublistSize == 0 ? initialList.size() / sublistSize
                : (initialList.size() / sublistSize) + 1;
        Collection<List<KnXDMSubsProvInfoDTO>> jobList = new ArrayList<>(noOfSublists);
        for (int i = 0; i < noOfSublists; i++) {
            int maxLength = ((i + 1) * sublistSize > initialList.size()) ? initialList.size()
                    : (i + 1) * sublistSize;
            List<KnXDMSubsProvInfoDTO> subList = new ArrayList<>(
                    initialList.subList(i * sublistSize, maxLength));
            jobList.add(subList);
        }
        knLogger.exit(methodName, jobList);
        return jobList;
    }

    /**
     * This method trims if the String passed to the method
     *
     * @param input String
     * @return result String
     */
    private static String trim(String input) {
        return input != null ? input.trim() : null;
    }

    /**
     * This method checks if the String passed to the method is Null or empty
     *
     * @param value String
     * @return result boolean
     */
    public static boolean isNullOrEmpty(String value) {
        boolean result = Boolean.FALSE;
        String EMPTY_STRING = "";
        if (value == null || EMPTY_STRING.equalsIgnoreCase(trim(value))) {
            result = Boolean.TRUE;
        }
        return result;
    }

    private List<KnXcapDiffNotifyDTO> prepareNotification(Map<String, KnOPDispatchDirChgDTO> dispatchDirChgDTOMap,
                                                          KnOPBulkRespDTO bulkRespDTO) {
        final String methodName = "prepareNotification(Map<String, KnOPDispatchDirChgDTO>, KnOPBulkRespDTO)";

        KnXcapDiffNotifyDTO xcapDiffNotifyDTO;
        List<KnXcapDiffNotifyDTO> xcapDiffList = new ArrayList<>();

        for (String mdn : dispatchDirChgDTOMap.keySet()) {
            xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
            KnOPDispatchDirChgDTO opDispatchDirChgDTO = dispatchDirChgDTOMap.get(mdn);
            if ((opDispatchDirChgDTO.getProtoVersion().matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX)) && (opDispatchDirChgDTO.isActiveFSChanged())) {

                KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                profileNotifyDTO.setActiveFeatureSetChange(mdn);
                xcapDiffNotifyDTO.setProfileNotify(true);
                profileNotifyDTO.setMdn(mdn);
                profileNotifyDTO.setPocHome(opDispatchDirChgDTO.getPocHome());
                profileNotifyDTO.setPresenceHome(opDispatchDirChgDTO.getPresenceHome());
                profileNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());
                xcapDiffNotifyDTO.setProfileNotifyDTO(profileNotifyDTO);

            }

            Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<>();
            ArrayList<KnOPDocChgDTO> chgDocList = (ArrayList<KnOPDocChgDTO>) opDispatchDirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO chgDTO : chgDocList) {
                KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                xcapDiffDocDTO.setDocChangeType(chgDTO.getDocumentChgType());
                xcapDiffDocDTO.setDocEtag(chgDTO.getNewEtag());
                xcapDiffDocDTO.setDocumentSelector(chgDTO.getDocUri());
                xcapDocList.add(xcapDiffDocDTO);
            }

            xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
            xcapDiffNotifyDTO.setDirNewEtag(opDispatchDirChgDTO.getDirNewEtag());
            xcapDiffNotifyDTO.setDirPrevEtag(opDispatchDirChgDTO.getDirPrevEtag());
            xcapDiffNotifyDTO.setDirURI(opDispatchDirChgDTO.getDirUri());
            xcapDiffNotifyDTO.setXcapRootUri(opDispatchDirChgDTO.getXcapRootURI());
            xcapDiffNotifyDTO.setProtocolVersion(opDispatchDirChgDTO.getProtoVersion());
            xcapDiffNotifyDTO.setPocHome(bulkRespDTO.getPocServerHome());
            xcapDiffNotifyDTO.setPresenceHome(bulkRespDTO.getPresenceServerHome());

            xcapDiffList.add(xcapDiffNotifyDTO);

        }
        knLogger.exit(methodName, xcapDiffList);
        return xcapDiffList;

    }
}