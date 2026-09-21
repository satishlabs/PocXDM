/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnXcapDiffNotifierImpl.java
 * Subsystem:   XCAP Notification mgr.
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       3/7/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.notificationmgr.impl;

import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;

import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;

import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.*;
import com.kodiak.xdms.notificationmgr.resources.KnSEHNotificationJob;
import com.kodiak.xdms.notificationmgr.resources.KnXcapNotifyConstants;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;


import static com.kodiak.common.dao.KnDbUtil.rollback;
import static com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier.getMDNFromURI;
import static com.kodiak.xdms.server.common.resources.KnConstants.USER_PROFILE_MGMT_BIT;

/**
 * The Notifier class implements the initial notification processing and load sharing logic for processing
 * the notifications either in the main thread or via multiple Jobs using worker thread pool
 */
public class KnXcapDiffNotifierImpl implements IXcapDiffNotifierIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapDiffNotifierImpl.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";

    //The notification processor utility
    private KnXcapDiffNotifier xcapDiffNotifier;
    //The job scheduler
    private KnJobSchedulerImpl scheduler;
    //The value indicates the max notifications thresold per Job
    private int maxNotfnsPerJob = -1;
    //The value indicates the thresold for processing notifications in main thread
    private int syncNotfyThresold = 500;

    private KnGenInfoUtil genInfoUtil;

    /**
     * Empty constructor
     */
    public KnXcapDiffNotifierImpl() {
        //notifyUtil = KnNotifyUtil.getInstance();
        //es = notifyUtil.getThreadExecutors();
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        scheduler = KnJobSchedulerImpl.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    public void setMaxNotfnsPerJob(int maxNotfnsPerJob) {
        this.maxNotfnsPerJob = maxNotfnsPerJob;
    }

    public void setSyncNotfyThresold(int syncNotfyThresold) {
        this.syncNotfyThresold = syncNotfyThresold;
    }

    /**
     * @param xcapDiffNotifyDTOs the notification objects
     * @param persisterTxn       the transaction object
     * @return boolean indicating success or failure of notification processing
     */
    public boolean sendXcapDiffNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs,
                                             KnPersisterTxn persisterTxn) {

       /* String methodName = "sendXcapDiffNotifications(Collection<KnXcapDiffNotifyDTO>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: sending xcap diff notifications");
        boolean isSuccess = false;

        if (xcapDiffNotifyDTOs != null && !xcapDiffNotifyDTOs.isEmpty()) {
            int notificationCount = xcapDiffNotifyDTOs.size();
            List<KnNotificationJob> jobList = new ArrayList<KnNotificationJob>(1);
            if (maxNotfnsPerJob > 0 && (notificationCount > maxNotfnsPerJob)) {
                //Process the notifications in multiple Jobs - load sharing
                knLogger.info( methodName, "Processing notifications in multiple Jobs. ",
                        "Notification count - ", notificationCount);
                //Initial notification list
                List<KnXcapDiffDirChgNotifyDTO> initialList = new ArrayList<KnXcapDiffDirChgNotifyDTO>(xcapDiffNotifyDTOs);
                //Splitting the notifications into multiple Jobs
                Collection<List<KnXcapDiffDirChgNotifyDTO>> notificationList = splitList(initialList, maxNotfnsPerJob);

                jobList = new ArrayList<KnNotificationJob>(notificationList.size());
                //Creating and Submitting multiple Jobs for the split notifications
                for (List<KnXcapDiffDirChgNotifyDTO> notifications : notificationList) {
                    if (notifications != null && !notifications.isEmpty()) {
                        KnNotificationJob notificationSubJob = new KnNotificationJob();
                        notificationSubJob.setNotifications(notifications);
                        jobList.add(notificationSubJob);
                    }
                }
            } else {
                //Process the notifications in single Job
                knLogger.info( methodName, "Processing notifications in single Job. ",
                        "Notification count - ", notificationCount);
                KnNotificationJob notificationJob = new KnNotificationJob();
                notificationJob.setNotifications(xcapDiffNotifyDTOs);
                jobList.add(notificationJob);
            }
            //Scheduling the Jobs
            try {
                knLogger.debug( methodName, "Scheduling the Jobs - ", jobList);
                scheduler.addRamJob(jobList, KnXcapNotifyConstants.DEFAULT_GROUPNAME);
                isSuccess = true;
            } catch (KnJobSchedulerException e) {
                knLogger.error( methodName, "KnJobSchedulerException occurred while ",
                        "submitting Notification Job to Scheduler - ", e);
            }
            //}
        }
        return isSuccess;*/

        return sendXcapDiffNotifications(xcapDiffNotifyDTOs, persisterTxn, null);
    }

    /**
     * @param xcapDiffNotifyDTOs Collection of KnXcapDiffNotifyDTO
     * @param persisterTxn       KnPersisterTxn DB Transaction Object
     * @return
     */
    public boolean sendXcapDiffNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs,
                                             KnPersisterTxn persisterTxn, KnNotificationParamDTO notificationParamDTO) {

        String methodName = "sendXcapDiffNotifications(Collection<KnXcapDiffNotifyDTO>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: sending xcap diff notifications :", notificationParamDTO);
        boolean isSuccess = false;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            if (xcapDiffNotifyDTOs != null && !xcapDiffNotifyDTOs.isEmpty()) {
                int watcherCount = xcapDiffNotifyDTOs.size();
                if (notificationParamDTO == null) {
                    notificationParamDTO = new KnNotificationParamDTO();
                }
                List<KnXcapDiffDirChgNotifyDTO> initialList = new ArrayList<>(xcapDiffNotifyDTOs);
                int notificationCount = xcapDiffNotifier.getXcapDiffDirChgNotifyCount(initialList);
                knLogger.info(methodName, "Notification count - ", notificationCount, " watcherCount - ", watcherCount);
                boolean saveNotificationEnabled = isSaveNotification(notificationParamDTO, notificationCount, watcherCount);
                boolean saveSuppressEnabled = isSaveSuppressNotification();
                knLogger.info(methodName, FLOW_TAG + " STEP-P0 Gate evaluation (DirChg). saveNotification="
                        + saveNotificationEnabled + " saveSuppress=" + saveSuppressEnabled
                        + " watcherCount=" + watcherCount + " notificationCount=" + notificationCount
                        + " priority=" + notificationParamDTO.getPriority());
                if (saveNotificationEnabled) {
                    knLogger.info(methodName, FLOW_TAG + " STEP-P1 Save-first entry (DirChg). watcherCount="
                            + watcherCount + " notificationCount=" + notificationCount);
                    // Queue commit is independent of tracker / post-processing.
                    xcapDiffNotifier.saveNotification(null, initialList, notificationParamDTO);
                    knLogger.info(methodName, FLOW_TAG + " STEP-P2 Saved notifications to DG.XCAP_PENDING_NOTIFYQ (DirChg)");

                    try {
                        LinkedHashSet<String> watcherMdns = extractWatcherMdnsFromDirChg(initialList);

                        if (xcapDiffNotifier.isOptimizedNotificationEnabled()) {
                            try {
                                xcapDiffNotifier.upsertMdnNotifyTracker(watcherMdns, null);
                                knLogger.info(methodName, FLOW_TAG + " STEP-P3 Upserted MDN tracker entries (DirChg). uniqueMdns="
                                        + watcherMdns.size());
                            } catch (Exception trackerEx) {
                                knLogger.warn(methodName, FLOW_TAG + " STEP-P3 MDN tracker upsert failed for DirChg; queue rows remain saved. uniqueMdns="
                                        + watcherMdns.size(), trackerEx);
                            }
                        } else {
                            knLogger.info(methodName, FLOW_TAG
                                    + " STEP-P3 Legacy mode – tracker upsert skipped (DirChg). uniqueMdns="
                                    + watcherMdns.size());
                        }

                        if (shouldTriggerImmediateEtagNotify()) {
                            xcapDiffNotifier.sendXcapDiffDirMicroserviceNotificationforEtagNotify(initialList);
                            knLogger.info(methodName, FLOW_TAG + " STEP-P4 Triggered microservice etag notify for DirChg batch");
                        } else {
                            knLogger.info(methodName, FLOW_TAG + " STEP-P4 Optimized mode ON - deferring etag notify to bundled worker path (DirChg)");
                        }
                    } catch (Throwable postSaveEx) {
                        knLogger.error(methodName, FLOW_TAG
                                + " STEP-ERR Post-save processing failed for DirChg; queue insert already committed", postSaveEx);
                    }
                } else if (saveSuppressEnabled) {
                    knLogger.info(methodName, FLOW_TAG + " STEP-PX Save-first bypassed; entering suppress flow (DirChg)");
                    knLogger.debug(methodName, "inside notification suppressed ");
                    LinkedHashSet<String> docSelAll = new LinkedHashSet<String>();
                    LinkedHashSet<KnMcsxcapMdnDTO> mdns = new LinkedHashSet<>();
                    for (KnXcapDiffDirChgNotifyDTO itr : initialList) {
                        if (itr.getDocDiffObj() != null) {
                            docSelAll.addAll(itr.getDocDiffObj().stream().map(KnXcapDiffDocDTO::getDocumentSelector).collect(Collectors.toSet()));
                        } else {
                            knLogger.debug(methodName, "get mdn from directory uri- ");
                            KnMcsxcapMdnDTO mcsxcapMdnDTO = new KnMcsxcapMdnDTO();
                            mcsxcapMdnDTO.setMdn(getMDNFromURI(itr.getDirURI()));
                            mdns.add(mcsxcapMdnDTO);
                        }
                    }
                    if (!mdns.isEmpty()) {
                        mdns.forEach(e -> docSelAll.add(e.getMdn()));
                        xcapDiffNotifier.sendMicroserviceNotificationforEtagNotify(docSelAll);
                    }
                    for (String itr : docSelAll) {
                        xcapDiffNotifier.populateSystemProfileMdnsForDocChange(mdns, itr);
                    }
                    knLogger.debug(methodName, "List of mdns to send for ssh increment KnXcapDiffNotifyDTO");
                    sendMdnsToDB(mdns.stream().map(KnMcsxcapMdnDTO::getMdn).collect(Collectors.toCollection(LinkedHashSet::new)), notificationParamDTO, null);
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_OIDCXCAP_NOTIFY_SUPPRESSED);
                } else {
                    knLogger.info(methodName, FLOW_TAG + " STEP-PX Save-first and suppress flow both skipped (DirChg)");
                    knLogger.info(methodName, "No Records are getting saved in DG.XCAP_PENDING_NOTIFYQ");
                }
                isSuccess = true;
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Persister failure in DirChg send path", e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Unexpected failure in DirChg send path", e);
            rollback(persisterTxn);
        }
        return isSuccess;
    }

    private Collection<List<KnXcapDiffDirChgNotifyDTO>> splitList(List<KnXcapDiffDirChgNotifyDTO> initialList, int sublistSize) {

        String methodName = "splitList(initialList, sublistSize)";
        knLogger.debug(methodName, "Splitting initialList. sublistSize - ", sublistSize);
        int noOfSublists = initialList.size() % sublistSize == 0 ? initialList.size() / sublistSize
                : (initialList.size() / sublistSize) + 1;
        Collection<List<KnXcapDiffDirChgNotifyDTO>> jobList = new ArrayList<List<KnXcapDiffDirChgNotifyDTO>>(noOfSublists);
        for (int i = 0; i < noOfSublists; i++) {
            int maxLength = ((i + 1) * sublistSize > initialList.size()) ? initialList.size()
                    : (i + 1) * sublistSize;
            List<KnXcapDiffDirChgNotifyDTO> subList = new ArrayList<KnXcapDiffDirChgNotifyDTO>(
                    initialList.subList(i * sublistSize, maxLength));
            jobList.add(subList);
        }
        return jobList;
    }

    public boolean sendXcapDiffNotifications(List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs) {

        /*String methodName = "sendXcapDiffNotifications(List<KnXcapDiffNotifyDTO>)";
        knLogger.info(methodName, "ENTRY: sending xcap diff notifications");
        boolean isSuccess = false;

        if (xcapDiffNotifyDTOs != null && !xcapDiffNotifyDTOs.isEmpty()) {
            int notificationCount = xcapDiffNotifyDTOs.size();
            List<KnXcapNotificationJob> jobList = new ArrayList<KnXcapNotificationJob>(1);
            if (maxNotfnsPerJob > 0 && (notificationCount > maxNotfnsPerJob)) {

                //Process the notifications in multiple Jobs - load sharing
                knLogger.info( methodName, "Processing notifications in multiple Jobs. ",
                        "Notification count - ", notificationCount);
                //Initial notification list
                List<KnXcapDiffNotifyDTO> initialList = new ArrayList<KnXcapDiffNotifyDTO>(xcapDiffNotifyDTOs);
                //Splitting the notifications into multiple Jobs
                Collection<List<KnXcapDiffNotifyDTO>> notificationList = splitJob(initialList, maxNotfnsPerJob);

                jobList = new ArrayList<KnXcapNotificationJob>(notificationList.size());
                //Creating and Submitting multiple Jobs for the split notifications
                for (List<KnXcapDiffNotifyDTO> notifications : notificationList) {
                    if (notifications != null && !notifications.isEmpty()) {
                        KnXcapNotificationJob notificationSubJob = new KnXcapNotificationJob();
                        notificationSubJob.setNotifications(notifications);
                        jobList.add(notificationSubJob);
                    }
                }
            } else {
                //Process the notifications in single Job
                knLogger.info( methodName, "Processing notifications in single Job. ",
                        "Notification count - ", notificationCount);
                KnXcapNotificationJob notificationJob = new KnXcapNotificationJob();
                notificationJob.setNotifications(xcapDiffNotifyDTOs);
                jobList.add(notificationJob);
            }
            //Scheduling the Jobs
            try {
                knLogger.debug( methodName, "Scheduling the Jobs - ", jobList);
                scheduler.addRamJob(jobList, KnXcapNotifyConstants.DEFAULT_JOBGROUPNAME);
                isSuccess = true;
            } catch (KnJobSchedulerException e) {
                knLogger.error( methodName, "KnJobSchedulerException occurred while ",
                        "submitting Notification Job to Scheduler - ", e);
            }
            //}
        }
        return isSuccess;*/

        return sendXcapDiffNotifications(xcapDiffNotifyDTOs, null);
    }

    public boolean sendXcapDiffNotifications(List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs, KnNotificationParamDTO notificationParamDTO) {
        String methodName = "sendXcapDiffNotifications(List<KnXcapDiffNotifyDTO>)";
        knLogger.info(methodName, "ENTRY: sending xcap diff notifications notificationParamDTO", notificationParamDTO);
        KnPersisterTxn persisterTxn = null;
        boolean isSuccess = false;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            if (xcapDiffNotifyDTOs != null && !xcapDiffNotifyDTOs.isEmpty()) {
                int watcherCount = xcapDiffNotifyDTOs.size();
                if (notificationParamDTO == null) {
                    notificationParamDTO = new KnNotificationParamDTO();
                }
                List<KnXcapDiffNotifyDTO> initialList = new ArrayList<>(xcapDiffNotifyDTOs);
                int notificationCount = xcapDiffNotifier.countGeneratedNotifications(initialList);
                knLogger.info(methodName, "Notification count - ", notificationCount, " watcherCount - ", watcherCount);
                boolean saveNotificationEnabled = isSaveNotification(notificationParamDTO, notificationCount, watcherCount);
                boolean saveSuppressEnabled = isSaveSuppressNotification();
                knLogger.info(methodName, FLOW_TAG + " STEP-P0 Gate evaluation (DiffList). saveNotification="
                        + saveNotificationEnabled + " saveSuppress=" + saveSuppressEnabled
                        + " watcherCount=" + watcherCount + " notificationCount=" + notificationCount
                        + " priority=" + notificationParamDTO.getPriority());
                if (saveNotificationEnabled) {
                    knLogger.info(methodName, FLOW_TAG + " STEP-P1 Save-first entry (DiffList). watcherCount="
                            + watcherCount + " notificationCount=" + notificationCount);
                    xcapDiffNotifier.saveNotification(initialList, notificationParamDTO, null);
                    knLogger.info(methodName, FLOW_TAG + " STEP-P2 Saved notifications to DG.XCAP_PENDING_NOTIFYQ (DiffList)");

                    try {
                        LinkedHashSet<String> watcherMdns = extractWatcherMdnsFromDiff(initialList);

                        if (xcapDiffNotifier.isOptimizedNotificationEnabled()) {
                            try {
                                xcapDiffNotifier.upsertMdnNotifyTracker(watcherMdns, null);
                                knLogger.info(methodName, FLOW_TAG + " STEP-P3 Upserted MDN tracker entries (DiffList). uniqueMdns="
                                        + watcherMdns.size());
                            } catch (Exception trackerEx) {
                                knLogger.warn(methodName, FLOW_TAG + " STEP-P3 MDN tracker upsert failed for DiffList; queue rows remain saved. uniqueMdns="
                                        + watcherMdns.size(), trackerEx);
                            }
                        } else {
                            knLogger.info(methodName, FLOW_TAG
                                    + " STEP-P3 Legacy mode – tracker upsert skipped (DiffList). uniqueMdns="
                                    + watcherMdns.size());
                        }

                        if (shouldTriggerImmediateEtagNotify()) {
                            xcapDiffNotifier.sendXcapDiffMicroserviceNotificationforEtagNotify(initialList);
                            knLogger.info(methodName, FLOW_TAG + " STEP-P4 Triggered microservice etag notify for DiffList batch");
                        } else {
                            knLogger.info(methodName, FLOW_TAG + " STEP-P4 Optimized mode ON - deferring etag notify to bundled worker path (DiffList)");
                        }
                    } catch (Throwable postSaveEx) {
                        knLogger.error(methodName, FLOW_TAG
                                + " STEP-ERR Post-save processing failed for DiffList; queue insert already committed", postSaveEx);
                    }
                } else if (saveSuppressEnabled) {
                    knLogger.info(methodName, FLOW_TAG + " STEP-PX Save-first bypassed; entering suppress flow (DiffList)");
                    knLogger.info(methodName, "inside notification suppressed ");
                    LinkedHashSet<String> docSelAll = new LinkedHashSet<String>();
                    LinkedHashSet<KnMcsxcapMdnDTO> mdns = new LinkedHashSet<>();
                    for (KnXcapDiffNotifyDTO itr : initialList) {
                        if (itr.getDocDiffObj() != null) {
                            docSelAll.addAll(itr.getDocDiffObj().stream().map(KnXcapDiffDocDTO::getDocumentSelector).collect(Collectors.toSet()));
                        } else {
                            knLogger.info(methodName, "get mdn from directory uri- ");
                            KnMcsxcapMdnDTO mcsxcapMdnDTO = new KnMcsxcapMdnDTO();
                            mcsxcapMdnDTO.setMdn(getMDNFromURI(itr.getDirURI()));
                            mdns.add(mcsxcapMdnDTO);
                        }
                    }
                    if (!mdns.isEmpty()) {
                        mdns.forEach(e -> docSelAll.add(e.getMdn()));
                        xcapDiffNotifier.sendMicroserviceNotificationforEtagNotify(docSelAll);
                    }
                    for (String itr : docSelAll) {
                        xcapDiffNotifier.populateSystemProfileMdnsForDocChange(mdns, itr);
                    }
                    knLogger.info(methodName, "List of mdns to send for ssh increment KnXcapDiffNotifyDTO");
                    sendMdnsToDB(mdns.stream().map(KnMcsxcapMdnDTO::getMdn).collect(Collectors.toCollection(LinkedHashSet::new)), notificationParamDTO, null);
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_OIDCXCAP_NOTIFY_SUPPRESSED);
                } else {
                    knLogger.info(methodName, FLOW_TAG + " STEP-PX Save-first and suppress flow both skipped (DiffList)");
                    knLogger.info(methodName, "No Records are getting saved in DG.XCAP_PENDING_NOTIFYQ");
                }
                isSuccess = true;
            }
            persisterTxn.save();
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Persister failure in DiffList send path", e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Unexpected failure in DiffList send path", e);
            rollback(persisterTxn);
        }
        return isSuccess;
    }

    @Override
    public void sendEtagMcsNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffDirChgNotifyDTOS) {
        xcapDiffNotifier.sendMicroserviceNotificationforEtagNotify(xcapDiffDirChgNotifyDTOS.stream().
                map(KnXcapDiffNotifyDTO -> getMDNFromURI(KnXcapDiffNotifyDTO.getDirURI())).
                collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    @Override
    public void sendEtagMcsNotifications(LinkedHashSet<String> mdnSet) {
        xcapDiffNotifier.sendMicroserviceNotificationforEtagNotify(mdnSet);
    }

    public boolean sendXcapDiffNotifications(KnXcapDiffNotifyDTO xcapDiffNotifyDTO) {
        String methodName = "sendXcapDiffNotifications(KnXcapDiffNotifyDTO)";
        return sendXcapDiffNotifications(xcapDiffNotifyDTO, null);
    }

    public boolean sendXcapDiffNotifications(KnXcapDiffNotifyDTO xcapDiffNotifyDTO, KnNotificationParamDTO notificationParamDTO) {
        String methodName = "sendXcapDiffNotifications(KnXcapDiffNotifyDTO, KnNotificationParamDTO)";
        knLogger.info(methodName, "ENTRY: sending xcap diff notifications : notificationParamDTO", notificationParamDTO);
        // Add detailed logging for xcapDiffNotifyDTO
        if (xcapDiffNotifyDTO != null) {
            knLogger.info(methodName, "xcapDiffNotifyDTO dirURI:", xcapDiffNotifyDTO.getDirURI());
            if (xcapDiffNotifyDTO.getDocDiffObj() != null) {
                knLogger.info(methodName, "Number of doc changes:", xcapDiffNotifyDTO.getDocDiffObj().size());
                for (com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO docDTO : xcapDiffNotifyDTO.getDocDiffObj()) {
                    knLogger.info(methodName, "DocDiffObj details:", docDTO);
                }
            } else {
                knLogger.info(methodName, "DocDiffObj is NULL");
            }
        }
        boolean isSuccess = false;
        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            if (notificationParamDTO == null) {
                notificationParamDTO = new KnNotificationParamDTO();
            }
            notificationParamDTO.setMsgType(1);
            if (xcapDiffNotifyDTO != null) {
                knLogger.info(methodName, "Notification ");
                int notificationCount = xcapDiffNotifier.countGeneratedNotifications(xcapDiffNotifyDTO);
                List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs = new ArrayList<>();
                xcapDiffNotifyDTOs.add(xcapDiffNotifyDTO);
                List<KnXcapDiffNotifyDTO> initialList = new ArrayList<>(xcapDiffNotifyDTOs);
                int watcherCount = initialList.size();
                knLogger.info(methodName, "Notification count - ", notificationCount, " watcherCount - ", watcherCount);
                boolean saveNotificationEnabled = isSaveNotification(notificationParamDTO, notificationCount, watcherCount);
                boolean saveSuppressEnabled = isSaveSuppressNotification();
                knLogger.info(methodName, FLOW_TAG + " STEP-P0 Gate evaluation (SingleDiff). saveNotification="
                        + saveNotificationEnabled + " saveSuppress=" + saveSuppressEnabled
                        + " watcherCount=" + watcherCount + " notificationCount=" + notificationCount
                        + " priority=" + notificationParamDTO.getPriority());
                if (saveNotificationEnabled) {
                    knLogger.info(methodName, FLOW_TAG + " STEP-P1 Save-first entry (SingleDiff). watcherCount="
                            + watcherCount + " notificationCount=" + notificationCount);
                    xcapDiffNotifier.saveNotification(initialList, notificationParamDTO, null);
                    knLogger.info(methodName, FLOW_TAG + " STEP-P2 Saved notifications to DG.XCAP_PENDING_NOTIFYQ (SingleDiff)");

                    try {
                        LinkedHashSet<String> watcherMdns = extractWatcherMdnsFromDiff(initialList);

                        if (xcapDiffNotifier.isOptimizedNotificationEnabled()) {
                            try {
                                xcapDiffNotifier.upsertMdnNotifyTracker(watcherMdns, null);
                                knLogger.info(methodName, FLOW_TAG + " STEP-P3 Upserted MDN tracker entries (SingleDiff). uniqueMdns="
                                        + watcherMdns.size());
                            } catch (Exception trackerEx) {
                                knLogger.warn(methodName, FLOW_TAG + " STEP-P3 MDN tracker upsert failed for SingleDiff; queue rows remain saved. uniqueMdns="
                                        + watcherMdns.size(), trackerEx);
                            }
                        } else {
                            knLogger.info(methodName, FLOW_TAG
                                    + " STEP-P3 Legacy mode – tracker upsert skipped (SingleDiff). uniqueMdns="
                                    + watcherMdns.size());
                        }

                        if (shouldTriggerImmediateEtagNotify()) {
                            xcapDiffNotifier.sendXcapDiffMicroserviceNotificationforEtagNotify(initialList);
                            knLogger.info(methodName, FLOW_TAG + " STEP-P4 Triggered microservice etag notify for SingleDiff batch");
                        } else {
                            knLogger.info(methodName, FLOW_TAG + " STEP-P4 Optimized mode ON - deferring etag notify to bundled worker path (SingleDiff)");
                        }
                    } catch (Throwable postSaveEx) {
                        knLogger.error(methodName, FLOW_TAG
                                + " STEP-ERR Post-save processing failed for SingleDiff; queue insert already committed", postSaveEx);
                    }
                } else if (saveSuppressEnabled) {
                    knLogger.info(methodName, FLOW_TAG + " STEP-PX Save-first bypassed; entering suppress flow (SingleDiff)");
                    knLogger.info(methodName, "inside notification suppressed ");
                    LinkedHashSet<String> docSelAll = new LinkedHashSet<String>();
                    LinkedHashSet<KnMcsxcapMdnDTO> mdns = new LinkedHashSet<>();
                    for (KnXcapDiffNotifyDTO itr : initialList) {
                        if (itr.getDocDiffObj() != null) {
                            docSelAll.addAll(itr.getDocDiffObj().stream().map(KnXcapDiffDocDTO::getDocumentSelector).collect(Collectors.toSet()));
                        } else {
                            knLogger.info(methodName, "get mdn from directory uri- ");
                            KnMcsxcapMdnDTO mcsxcapMdnDTO = new KnMcsxcapMdnDTO();
                            mcsxcapMdnDTO.setMdn(getMDNFromURI(itr.getDirURI()));
                            mdns.add(mcsxcapMdnDTO);
                        }
                    }
                    if (!mdns.isEmpty()) {
                        mdns.forEach(e -> docSelAll.add(e.getMdn()));
                        xcapDiffNotifier.sendMicroserviceNotificationforEtagNotify(docSelAll);
                    }
                    for (String itr : docSelAll) {
                        xcapDiffNotifier.populateSystemProfileMdnsForDocChange(mdns, itr);
                    }
                    knLogger.info(methodName, "List of mdns to send for ssh increment KnXcapDiffNotifyDTO");
                    sendMdnsToDB(mdns.stream().map(KnMcsxcapMdnDTO::getMdn).collect(Collectors.toCollection(LinkedHashSet::new)), notificationParamDTO, null);
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_OIDCXCAP_NOTIFY_SUPPRESSED);
                } else {
                    knLogger.info(methodName, FLOW_TAG + " STEP-PX Save-first and suppress flow both skipped (SingleDiff)");
                    knLogger.info(methodName, "No Records are getting saved in DG.XCAP_PENDING_NOTIFYQ");
                }
                isSuccess = true;
            }
            persisterTxn.save();
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Persister failure in SingleDiff send path", e);
            rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Unexpected failure in SingleDiff send path", e);
            rollback(persisterTxn);
        }
        return isSuccess;
    }

    private boolean isSaveNotification(KnNotificationParamDTO notificationParamDTO, int notificationCount, int watcherCount) {
        String methodName = "isSaveNotification";

        // In optimized mode, always persist to queue so epoch-based debulking can bundle
        // repeated changes for the same watcher MDN.
        if (xcapDiffNotifier.isOptimizedNotificationEnabled()) {
            knLogger.info(methodName, FLOW_TAG
                    + " STEP-P0A Optimized mode ON - forcing save-first enqueue. watcherCount="
                    + watcherCount + " notificationCount=" + notificationCount);
            return true;
        }

        boolean isSaveNotification = notificationParamDTO.getPriority() == 0 ||
                (!genInfoUtil.isSuppressWaterMark(watcherCount, xcapDiffNotifier.recordCount(null))
                        && notificationCount < genInfoUtil.getXdmMaxNotificationCount());
        knLogger.info(methodName, "isSaveNotification value : ", isSaveNotification);
        return isSaveNotification;
    }

    private boolean isSaveSuppressNotification() {
        String methodName = "isSaveSuppressNotification";
        boolean enabled = xcapDiffNotifier.totalRecordCount() < genInfoUtil.getMaxPendingNotifySize() * 10;
        knLogger.info(methodName, FLOW_TAG + " STEP-P0B Save-suppress gate evaluated. enabled=" + enabled);
        return enabled;
    }

    /**
     * Immediate etag notification should only run in legacy mode.
     * Optimized mode must defer send to poller/worker after epoch gating.
     */
    private boolean shouldTriggerImmediateEtagNotify() {
        return !xcapDiffNotifier.isOptimizedNotificationEnabled();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Watcher-MDN extraction helpers  (used for MDN_NOTIFY_TRACKER upserts)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Extracts the set of unique watcher MDNs from a list of directory-change notifications.
     *
     * <p>The MDN is resolved from the {@code dirURI} of each DTO via
     * {@link KnXcapDiffNotifier#getMDNFromURI(String)}.  A {@link LinkedHashSet} is used to
     * preserve insertion order while guaranteeing uniqueness.  Blank or null MDNs are silently
     * skipped so that a partially-populated DTO never causes a tracker row with an empty key.
     *
     * @param notifications the incoming directory-change notification list
     * @return ordered, deduplicated set of watcher MDNs; never {@code null}
     */
    private LinkedHashSet<String> extractWatcherMdnsFromDirChg(
            List<KnXcapDiffDirChgNotifyDTO> notifications) {

        String methodName = "extractWatcherMdnsFromDirChg";
        LinkedHashSet<String> mdns = new LinkedHashSet<>();

        if (notifications == null || notifications.isEmpty()) {
            knLogger.debug(methodName, "Empty notification list – returning empty MDN set");
            return mdns;
        }

        for (KnXcapDiffDirChgNotifyDTO dto : notifications) {
            if (dto == null) {
                continue;
            }
            String mdn = resolveWatcherMdn(dto.getMdn(), dto.getDirURI());
            if (mdn != null && xcapDiffNotifier.isRelatedXcapWatcher(mdn, dto.getDirURI())) {
                mdns.add(mdn.trim());
            }
        }

        knLogger.debug(methodName, "Extracted " + mdns.size() + " unique watcher MDN(s)");
        knLogger.info(methodName, FLOW_TAG + " STEP-P3A MDN extraction complete for DirChg. uniqueMdns=" + mdns.size());
        return mdns;
    }

    /**
     * Extracts the set of unique watcher MDNs from a list of diff notifications.
     *
     * <p>Behaviour is identical to {@link #extractWatcherMdnsFromDirChg} but operates on
     * {@link KnXcapDiffNotifyDTO} instances.
     *
     * @param notifications the incoming diff notification list
     * @return ordered, deduplicated set of watcher MDNs; never {@code null}
     */
    private LinkedHashSet<String> extractWatcherMdnsFromDiff(
            List<KnXcapDiffNotifyDTO> notifications) {

        String methodName = "extractWatcherMdnsFromDiff";
        LinkedHashSet<String> mdns = new LinkedHashSet<>();

        if (notifications == null || notifications.isEmpty()) {
            knLogger.debug(methodName, "Empty notification list – returning empty MDN set");
            return mdns;
        }

        for (KnXcapDiffNotifyDTO dto : notifications) {
            if (dto == null) {
                continue;
            }
            String mdn = resolveWatcherMdn(dto.getMdn(), dto.getDirURI());
            if (mdn != null && xcapDiffNotifier.isRelatedXcapWatcher(mdn, dto.getDirURI())) {
                mdns.add(mdn.trim());
            }
        }

        knLogger.debug(methodName, "Extracted " + mdns.size() + " unique watcher MDN(s)");
        knLogger.info(methodName, FLOW_TAG + " STEP-P3A MDN extraction complete for Diff. uniqueMdns=" + mdns.size());
        return mdns;
    }

    private String resolveWatcherMdn(String explicitMdn, String dirUri) {
        if (explicitMdn != null && !explicitMdn.trim().isEmpty()) {
            return explicitMdn.trim();
        }
        return getMDNFromURI(dirUri);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // End of watcher-MDN extraction helpers
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * private method to send suppressed mdn to db
     *
     * @param mdns LinkedHashSet<String>
     * @return void
     */
    private void sendMdnsToDB(LinkedHashSet<String> mdns, KnNotificationParamDTO notificationParamDTO, KnPersisterTxn persisterTxn) {
        String methodName = "sendMdnsToDB(LinkedHashSet<String>,KnNotificationParamDTO )";
        knLogger.info(methodName, "sending mdns to save in db ");
        List<KnMCSNotifyDTO> mcsNotifyDTOS = new ArrayList<>();
        var mdnListArray = new ArrayList<>(mdns);
        var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, KnConstants.BATCH_SIZE);
        for (var mdnBatchList : mdnSplitList) {
            KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
            mcsNotifyDTO.setSuppressMdn(new HashSet<>(mdnBatchList));
            mcsNotifyDTOS.add(mcsNotifyDTO);
        }
        xcapDiffNotifier.saveEtagMdnNotification(mcsNotifyDTOS, notificationParamDTO, persisterTxn);
    }

    public boolean sendSEHNotifications(List<KnSEHNotifyDTO> sehNotifyDTOs) {
        String methodName = "sendSEHNotifications(List<KnSEHNotifyDTO>)";
        knLogger.debug(methodName, "ENTRY: sending xcap diff notifications");
        boolean isSuccess = false;

        if (sehNotifyDTOs != null && !sehNotifyDTOs.isEmpty()) {
            int notificationCount = sehNotifyDTOs.size();
            List<KnSEHNotificationJob> jobList = new ArrayList<>(1);
            if (maxNotfnsPerJob > 0 && (notificationCount > maxNotfnsPerJob)) {
                //Process the notifications in multiple Jobs - load sharing
                knLogger.info(methodName, "Processing notifications in multiple Jobs. ",
                        "Notification count - ", notificationCount);
                //Initial notification list
                List<KnSEHNotifyDTO> initialList = new ArrayList<>(sehNotifyDTOs.size());
                //Splitting the notifications into multiple Jobs
                Collection<List<KnSEHNotifyDTO>> notificationList = splitSehJob(initialList, maxNotfnsPerJob);

                jobList = new ArrayList<>(notificationList.size());
                //Creating and Submitting multiple Jobs for the split notifications
                for (List<KnSEHNotifyDTO> notifications : notificationList) {
                    if (notifications != null && !notifications.isEmpty()) {
                        KnSEHNotificationJob notificationSubJob = new KnSEHNotificationJob();
                        notificationSubJob.setNotifications(notifications);
                        jobList.add(notificationSubJob);
                    }
                }
            } else {
                //Process the notifications in single Job
                knLogger.info(methodName, "Processing notifications in single Job. ",
                        "Notification count - ", notificationCount);
                KnSEHNotificationJob notificationJob = new KnSEHNotificationJob();
                notificationJob.setNotifications(sehNotifyDTOs);
                jobList.add(notificationJob);
            }
            //Scheduling the Jobs
            try {
                knLogger.debug(methodName, "Scheduling the Jobs - ", jobList);
                scheduler.addRamJob(jobList, KnXcapNotifyConstants.DEFAULT_JOBSEHNAME);
                isSuccess = true;
            } catch (KnJobSchedulerException e) {
                knLogger.error(methodName, "KnJobSchedulerException occurred while ",
                        "submitting Notification Job to Scheduler - ", e);
            }
            //}
        }
        return isSuccess;
    }

    private List<List<KnXcapDiffNotifyDTO>> splitJob(List<KnXcapDiffNotifyDTO> initialList, int sublistSize) {

        String methodName = "splitJob(KnXcapDiffNotifyDTO, sublistSize)";
        knLogger.debug(methodName, "Splitting initialList. sublistSize - ", sublistSize);
        int noOfSublists = initialList.size() % sublistSize == 0 ? initialList.size() / sublistSize
                : (initialList.size() / sublistSize) + 1;
        List<List<KnXcapDiffNotifyDTO>> jobList = new ArrayList<>(noOfSublists);
        for (int i = 0; i < noOfSublists; i++) {
            int maxLength = ((i + 1) * sublistSize > initialList.size()) ? initialList.size()
                    : (i + 1) * sublistSize;
            List<KnXcapDiffNotifyDTO> subList = new ArrayList<>(initialList.subList(i * sublistSize, maxLength));
            jobList.add(subList);
        }
        return jobList;
    }

    private List<List<KnSEHNotifyDTO>> splitSehJob(List<KnSEHNotifyDTO> initialList, int sublistSize) {
        String methodName = "splitJob(KnSEHNotifyDTO, sublistSize)";
        knLogger.debug(methodName, "Splitting initialList. sublistSize - ", sublistSize);
        int noOfSublists = initialList.size() % sublistSize == 0 ? initialList.size() / sublistSize
                : (initialList.size() / sublistSize) + 1;
        List<List<KnSEHNotifyDTO>> jobList = new ArrayList<>(noOfSublists);
        for (int i = 0; i < noOfSublists; i++) {
            int maxLength = ((i + 1) * sublistSize > initialList.size()) ? initialList.size()
                    : (i + 1) * sublistSize;
            List<KnSEHNotifyDTO> subList = new ArrayList<>(initialList.subList(i * sublistSize, maxLength));
            jobList.add(subList);
        }
        return jobList;
    }

    /**
     * private method to send suppressed mdn to db
     *
     * @param mdns LinkedHashSet<String>
     * @return void
     */
    public void filterUpmMdnAndSendNotification(List<String> mdns, KnNotificationParamDTO notificationParamDTO, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "filterUpmMdnAndSendNotification(List<String>,KnNotificationParamDTO, KnPersisterTxn)";
        knLogger.info(methodName," Entry:- ");
        try {
            if(notificationParamDTO == null){
                notificationParamDTO = new KnNotificationParamDTO();
            }
            //getting the subs info
            KnXDMSubsProfileRespDTO subsInfo = genInfoUtil.selectSubsProfileInfo(mdns, null);
            List<String> mdnList = subsInfo.getSubsRespDTO().stream()
                    .filter(dto -> KnGeneralUtil.getFeatureBitValue(dto.getActiveFS2(), USER_PROFILE_MGMT_BIT))
                    .map(KnXDMSubsProvDTO::getMdn)
                    .toList();
            List<KnMCSNotifyDTO> mcsNotifyDTOS = new ArrayList<>();
            var mdnListArray = new ArrayList<>(mdnList);
            var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, KnConstants.BATCH_SIZE);
            for (var mdnBatchList : mdnSplitList) {
                KnMCSNotifyDTO mcsNotifyDTO = new KnMCSNotifyDTO();
                mcsNotifyDTO.setSuppressMdn(new HashSet<>(mdnBatchList));
                mcsNotifyDTOS.add(mcsNotifyDTO);
            }
            xcapDiffNotifier.saveEtagMdnNotification(mcsNotifyDTOS, notificationParamDTO, persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_OIDCXCAP_NOTIFY_SUPPRESSED);
        }catch (KnBOException exp){
            knLogger.warn(methodName, " UPM capable Mdn notification sent failed ",exp);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to send notification ");
        }
    }

}
