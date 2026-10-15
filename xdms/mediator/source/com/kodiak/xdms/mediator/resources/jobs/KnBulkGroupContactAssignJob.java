/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkGroupContactAssignJob.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Jan 10, 2019      9.03
 * <p/>
 * <p/>
 * 9th Floor, MFar Manyata Tech Park
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
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

import com.kodiak.common.commdto.request.KnCorpDispMemReqDTO;
import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpBulkGroupJobRespDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.impl.KnXDMMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

public class KnBulkGroupContactAssignJob extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkGroupContactAssignJob.class);

    private static final long serialVersionUID = 7526471155622676245L;

    private KnXDMCorpMediator corpMediator;
    private KnXDMMediator xdmMediator;
    private IXcapDiffNotifierIntf notifier;
    private int maxNotificationSize;
    private int corpId;
    private KnXDMCommonMediator commonMediator = null;
    private Collection<Integer> grpIds;
    private String mdn;
    private KnCorpDispMemReqDTO bulkGrpContactAssignJob;

    public KnBulkGroupContactAssignJob() {
        corpMediator = KnXDMCorpMediator.getInstance();
        xdmMediator = KnXDMMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        commonMediator = KnXDMCommonMediator.getInstance();
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public void setMaxNotificationSize(int maxNotificationSize) {
        this.maxNotificationSize = maxNotificationSize;
        notifier.setMaxNotfnsPerJob(this.maxNotificationSize);
    }

    public void setGrpIds(Collection<Integer> grpIds) {
        this.grpIds = grpIds;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        knLogger.info(methodName, "ENTRY : Job Execution starts - ");
        knLogger.debug(methodName, "Properties: mdn - ", KnGDPRTemplate.mdn(mdn), ", grpIds - ", grpIds);
        boolean isSuccess = false;
        KnCorpBulkGroupJobRespDTO respDTO = null;
        List<KnXDMSubsProvInfoDTO> subsProvInputDTOList = new ArrayList<KnXDMSubsProvInfoDTO>();
        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            bulkGrpContactAssignJob = new KnCorpDispMemReqDTO();
            bulkGrpContactAssignJob.setCorpId(this.corpId);
            bulkGrpContactAssignJob.setToMdn(mdn);
            bulkGrpContactAssignJob.setGrpIds(grpIds);
            respDTO = corpMediator.contactPairingForBulkGroupProcess(bulkGrpContactAssignJob, persisterTxn);
            persisterTxn.save();
            subsProvInputDTOList = respDTO.getSubscIsDispMemDetailsList();
            knLogger.debug(methodName, "subsProvInputDTOList - ", subsProvInputDTOList);
            List<String> mdns = new ArrayList<>();
            Map<String, Boolean> mdnListMap = new HashMap<>();
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
                    xdmRespDTO = (KnXDMRespDTO) xdmMediator.updateDispForSubscribers(subDispDat, KnConstants.FEATURE_SET.ONDEMLOCATION.value());
                    if(xdmRespDTO.getMdnListMap() != null) mdnListMap.putAll(xdmRespDTO.getMdnListMap());
                }
                isSuccess = true;
            }
            knLogger.debug(methodName, "mdns - ", KnGDPRTemplate.mdnList(mdns), "xdmRespDTO - ", xdmRespDTO);
            Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(respDTO);
            knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
            notifier.setMaxNotfnsPerJob(2);
            boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
            knLogger.debug(methodName, "Notification status - ", isNotified);
            // Calling eTag update for reverse look up. :
            if (!mdns.isEmpty() && !mdnListMap.isEmpty()) {
                commonMediator.initiateEtagMgmtJob(mdnListMap);
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
}
