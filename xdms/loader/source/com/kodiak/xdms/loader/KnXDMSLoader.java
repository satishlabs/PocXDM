/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***********************************************************************
 * <p/>
 * File name:  KnXDMSLoader.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Rama Krishna         15-Jan-2010   7.0
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
 * ***********************************************************************
 */
package com.kodiak.xdms.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.kodiak.common.filewatcher.KnFileWatcher;
import com.kodiak.common.loader.KnInitializer;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.common.resources.KnThreadExecutors;
import com.kodiak.frameworks.asyncfwk.common.KnAsyncFailedCleanUpJob;
import com.kodiak.frameworks.asyncfwk.common.KnAsyncFailureJobHandler;
import com.kodiak.frameworks.asyncfwk.common.KnAsyncPendingTxnInfoPollerThread;
import com.kodiak.frameworks.asyncfwk.common.KnXcapNotifyBufferThread;
import com.kodiak.frameworks.asyncfwk.resources.KnJobStatusManager;
import com.kodiak.frameworks.confignotifier.watcher.KnConfigXlaRegistration;
import com.kodiak.frameworks.dbfw.KnDbSyncFwInitializer;
import com.kodiak.frameworks.healthmgr.services.KnHealthManager;
import com.kodiak.frameworks.messaging.jmsclient.intf.impl.KnJmsMessagingClientImpl;
import com.kodiak.frameworks.messaging.resources.KnMsgConsumerInitializer;
import com.kodiak.frameworks.messaging.resources.KnRmqConfigUpdate;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.processinvoker.IProcessInvokerIntf;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.auditjobs.recoveryaudit.KnLargeGroupRecovery;
import com.kodiak.utilities.springcontainer.KnAppContextLoader;
import com.kodiak.utilities.statusmgr.KnInitLRTMonitorAudit;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.xdms.auditjobs.KnXDMAuditJobs;
import com.kodiak.xdms.auditjobs.asyncfw.KnFeatureBitJob;
import com.kodiak.xdms.auditjobs.common.KnXDMSMcsIdAuditThread;
import com.kodiak.xdms.auditjobs.common.KnXDMSegIndUpdateThread;
import com.kodiak.xdms.bulkfw.KnBulkFwInitializer;
import com.kodiak.xdms.bulkfw.factory.BatchPropertiesUpdater;
import com.kodiak.xdms.bulkfw.resources.jobs.KnBulkPollThread;
import com.kodiak.xdms.mediator.resources.jobs.*;
import com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnRetryUpmJobsThread;
import com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnUPMJobMonitor;
import com.kodiak.xdms.notificationmgr.impl.KnEtagNotificationConsumer;
import com.kodiak.xdms.notificationmgr.impl.KnMCSXCAPNotifyConsumer;
import com.kodiak.xdms.notificationmgr.resources.KnXcapNotifyProcessor;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.resources.KnConsulConfigUpdate;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.pubmgmt.clientintf.impl.KnPubClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.xdmintf.impl.KnXDMMsgReceiverImpl;
import com.kodiak.xlahandler.KnLoggerXlaImpl;
import com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnLocationEnabledCorporateJob;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.NOTIFYJOB_AUDIT_INTRVAL;
import static com.kodiak.common.resources.KnConstants.LARGE_GROUP_AUDIT_INTRVAL;

/**
 * XDM startup script which will initializes all the modules
 */
public class KnXDMSLoader {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSLoader.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";

    private static final String PROCESS_CONFIG_XML = "processconfig.xml";
    private static final String ACTIVE_RELEASE_DIR = System.getProperty("activeRelDir");
    private static final String CUSTOM_AUDIT_JOB_NAME = "XDMCustomAuditJobs";
    private static final String CUSTOM_CONFIG_PATH = "CUSTOM_CONFIG_PATH";
    private static final String XDM = "xdm";
    private static final String APP_CONF_FILE = ACTIVE_RELEASE_DIR + File.separator + "appconf.properties";
    private static final String PAM_RESPONDER_POLL_TIME = "PAM_RESPONDER_POLL_TIME";
    private static final String BULK_REQUEST_POLL_TIME = "BULK_REQUEST_POLL_TIME";
    private static final String CBS_MONITOR_INTERVAL = "CBS_MONITOR_INTERVAL";


    public static void main(String[] args) {
        initialize();
    }

    private static void initialize() {
        final String methodName = "main()";
        String activeReleasePath = System.getProperty("activeRelDir");
        //1. initialize the logger, dbmgr & license parser
        KnInitializer.getInstance();

        //2. Messaging Framework Initialization
        String serviceConfigFile = "rmq-cluster-status.props";
        String[] serviceConfigFiles = {serviceConfigFile};

        try {
            KnFileWatcher.initWatcher(activeReleasePath, serviceConfigFiles, new KnConsulConfigUpdate());
        } catch (Exception e) {
            knLogger.error(methodName, "KnConsulConfigUpdate: failed to register for filewatcher Notifications:", e);
            System.exit(0);
        }

        try {
            KnFileWatcher.initWatcher(activeReleasePath, serviceConfigFiles, new KnRmqConfigUpdate());
        } catch (Exception e) {
            knLogger.error(methodName, "failed to register for filewatcher Notifications:", e);
            System.exit(0);
        }

        //3. Initializing the CBS cluser status defaule to local
        KnCouchBaseMonitor couchBaseMonitor = new KnCouchBaseMonitor();

        List<String> publisherIds = new ArrayList<>();
        publisherIds.add(KnConstants.PUBLISHER_ID_HM);
        publisherIds.add(KnConstants.PUBLISHER_ID_NNI_SYNC);
        KnJmsMessagingClientImpl.initialize(publisherIds, new ArrayList<String>(), serviceConfigFile);
        //KnJmsMessagingClientImpl.initialize(publisherIds, new ArrayList<String>(), KnMessageConstants.CARD_TYPE_XDMSERVER);

        //Initialize DB concurrent executor
        KnDbSyncFwInitializer.getInstance();

        //initializing Status manager
        KnStatusManagerClient statusThread = KnStatusManagerClient.getInstance("XDMS");
        statusThread.start();

        //initializing the Statistical Manager for performance pegs
        KnStatisticsManagerImpl.getInstance();

        knLogger.info(methodName, "BatchPropertiesUpdater to update the latest DBUsername and DBPassword....");
        BatchPropertiesUpdater.getProperty();

        // spring initialization
        KnAppContextLoader.getInstance(XDM);

        //3. Facade Initialization
        new KnXDMMsgReceiverImpl();

        //4. Server libraries initialization
        KnProvClientImpl.getInstance();
        new KnPubClientIntf();
        new KnCorpClientImpl();
        KnHealthManager.getInstance();
        //cache cleanup scheduler
        KnCacheCleanUpLoader.getInstance();
        // Registers cache  tables with XLA
        KnConfigXlaRegistration.getInstance();

        //custom flag initialization
        KnXDMSLoader.intialiseCustom();

        //Bulk Fw Initialization
        KnBulkFwInitializer.getInstance();
        //Logger XLA Handler initialization
        KnLoggerXlaImpl.getInstance();

        //start config watcher thread
        //KnThreadExecutors.newSingleThreadExecutor("ConfigWatcher").submit(new KnWatcherThread(ACTIVE_RELEASE_DIR + File.separator + watcherDir));
        //knLogger.info("main", "config watcher started !!!!");

        //initializing the Process Invoker for Custom Modules
        knLogger.info(methodName, "Initialization of LI Event Handler ....");
        KnLIEventHandler.getInstance();

        //initializing XDMS audit discreet enable job
        knLogger.info(methodName, "Initialization of XDMS discreet enable audit job ....");
        KnXDMDiscreetEnableLoader.getInstance();

        KnXDMSLoader.startBulkPollThreads();

        //sending the Node Identifier Message to APP Router;
        //KnNodeIdentifierDTO nodeIdentifierDTO = new KnNodeIdentifierDTO();
        // nodeIdentifierDTO.setClientIPAddress("127.0.0.1");
        // nodeIdentifierDTO.setClientPttServerId(KnDbUtil.getDBConfigInfo().getLocalPttId());
        // nodeIdentifierDTO.setClientSubSystemId(KnConstants.IPC_SUBSYS_XDMS);
        //nodeIdentifierDTO.setServerIPAddress("127.0.0.1");
        //nodeIdentifierDTO.setServerPort(KnConstants.APP_ROUTER_PORT);

        KnXDMAuditJobs.createInstance();
        KnMsgConsumerInitializer.getInstance();
        KnJobStatusManager.getInstance();

        knLogger.debug(methodName, "Initializing LRT autid");
        KnInitLRTMonitorAudit.startLrtMonitorAudit();
       /* try {
            knLogger.info(methodName, "Sending Node Identifier Message");
            new KnNodeIdentifier().sendNodeIdentifierMsg(nodeIdentifierDTO);
        } catch (KnNodeIdentiException e) {
            knLogger.error(methodName, "Failed sending the node identifier message to APP Router");
            knLogger.error(methodName, e);
            System.err.println("EXITING the Process because of failure in connecting the APP Router");
            System.exit(1);
        }*/
    }

    private static void intialiseCustom() {
        final String methodName = "intialiseCustom";
        knLogger.entry(methodName, "initialising custom by default");
        try {
            IProcessInvokerIntf processInvokerIntf = KnProcessInvokerImpl.getInstance(ACTIVE_RELEASE_DIR, PROCESS_CONFIG_XML);
            knLogger.info(methodName, "Loaded the custom hook by default successfully - ", processInvokerIntf);
            knLogger.debug(methodName, "invoking the custom audit Jobs");
            processInvokerIntf.invokeHook(CUSTOM_AUDIT_JOB_NAME, new Object());

        } catch (KnProcessInvokerException e) {
            knLogger.fatal(methodName, "failed to load custom configuration");
            knLogger.error(methodName, e);
            System.err.println("EXITING the Process because of failure in loading custom modules");
            System.exit(1);
        }
        knLogger.exit(methodName);
    }

    private static void startBulkPollThreads() {
        final String methodName = "startBulkPollThreads";
        knLogger.info(methodName, "Entry :");
        //in mins
        int pamRespPollTime = 5;
        int bulkReqPollTime = 5;
        try {
            /* // PDMBB-12581 Currently utilizes Consul health and a single CBS node, will be decommissioned since CBS is handling APPS CBS health status through multinode consul KV property
            long cbsMonitorInterval = 5000;
            try {
                cbsMonitorInterval = Long.parseLong(System.getenv(CBS_MONITOR_INTERVAL));
                knLogger.info(methodName, " cbsMonitorInterval ", cbsMonitorInterval);
                if (cbsMonitorInterval == 0) {
                    cbsMonitorInterval = 5000;
                }
            } catch (Exception e) {
                knLogger.error(methodName, "Exception in CBS Getting Property from conf so it will pick the default value . Exception MSG- ", e.getMessage());
            }
            */
            Object value = KnGeneralUtil.getPropertyValue(APP_CONF_FILE, PAM_RESPONDER_POLL_TIME);
            if (value != null) {
                pamRespPollTime = Integer.valueOf(value.toString());
            }
            value = KnGeneralUtil.getPropertyValue(APP_CONF_FILE, BULK_REQUEST_POLL_TIME);
            if (value != null) {
                bulkReqPollTime = Integer.valueOf(value.toString());
            }

            KnPAMResponderPollThread pamPollThread = new KnPAMResponderPollThread();
            ScheduledExecutorService pamService = KnThreadExecutors.newScheduledThreadPool(1, "pamRespPollThread");
            pamService.scheduleAtFixedRate(pamPollThread, 30, pamRespPollTime * 60, TimeUnit.SECONDS);

            KnBulkPollThread bulkPollThread = new KnBulkPollThread();
            ScheduledExecutorService bulkService = KnThreadExecutors.newScheduledThreadPool(1, "BulkReqPollThread");
            bulkService.scheduleAtFixedRate(bulkPollThread, 50, bulkReqPollTime * 60, TimeUnit.SECONDS);

            KnMCSXCAPNotifyConsumer knMCSXCAPNotifyConsumer = new KnMCSXCAPNotifyConsumer();
            ScheduledExecutorService mcsXcapNotifyService = KnThreadExecutors.newScheduledThreadPool(1, "mcsXcapNotifyThread");
            mcsXcapNotifyService.scheduleAtFixedRate(knMCSXCAPNotifyConsumer, 10, 1
                    , TimeUnit.SECONDS);

            KnEtagNotificationConsumer knEtagNotificationConsumer = new KnEtagNotificationConsumer();
            ScheduledExecutorService etagXcapNotifyService = KnThreadExecutors.newScheduledThreadPool(1, "mcsXcapNotifyThread");
            etagXcapNotifyService.scheduleAtFixedRate(knEtagNotificationConsumer, 10, 200
                    , TimeUnit.MILLISECONDS);

            KnXDMSMcsIdAuditThread knXDMSMcsIdAuditThread = new KnXDMSMcsIdAuditThread();
            ScheduledExecutorService knXDMSMcsIdAuditService = KnThreadExecutors.newScheduledThreadPool(1,
                    "knXDMSMcsIdAuditThread");
            knXDMSMcsIdAuditService.schedule(knXDMSMcsIdAuditThread, 1, TimeUnit.MINUTES);

            KnXDMSegIndUpdateThread knXDMsegInd = new KnXDMSegIndUpdateThread();
            ScheduledExecutorService knXDMSegmentIndicatorUpdateAuditService = KnThreadExecutors.newScheduledThreadPool(1,
                    "KnXDMSegIndUpdateThread");
            knXDMSegmentIndicatorUpdateAuditService.schedule(knXDMsegInd, 1, TimeUnit.MINUTES);

            KnUPMJobMonitor asyncFwJobMonitor = KnUPMJobMonitor.getInstance();
            ScheduledExecutorService asyncFwServSerevice = KnThreadExecutors.newScheduledThreadPool(1, "UPMJobMonitor");
            asyncFwServSerevice.scheduleAtFixedRate(asyncFwJobMonitor, 30, 2, TimeUnit.SECONDS);

            /*KnInconsistentUPMDataAuditThread inconsistentUPMDataAudit = new KnInconsistentUPMDataAuditThread();
            ScheduledExecutorService inconsistentUPMDataAuditExecutor = KnThreadExecutors.newScheduledThreadPool(1, "InconsistentUPMDataAudit");
            inconsistentUPMDataAuditExecutor.scheduleAtFixedRate(inconsistentUPMDataAudit, 30, 20, TimeUnit.SECONDS);*/

            KnRetryUpmJobsThread retryUpmJobs = KnRetryUpmJobsThread.getInstance();
            ScheduledExecutorService retryUpmJobsAuditExecutor = KnThreadExecutors.newScheduledThreadPool(1, "RetryUpmJobMonitor");
            retryUpmJobsAuditExecutor.scheduleAtFixedRate(retryUpmJobs, 1, 2, TimeUnit.SECONDS);

            //PDMBB-12581 Currently utilizes Consul health and a single CBS node, will be decommissioned since CBS is handling APPS CBS health status through multinode consul KV property
            //KnCouchBaseMonitor couchBaseMonitor = new KnCouchBaseMonitor();
            //ScheduledExecutorService couchBaseMonitorService = KnThreadExecutors.newScheduledThreadPool(1, "CouchBaseMonitor");
            //couchBaseMonitorService.scheduleAtFixedRate(couchBaseMonitor, 0, cbsMonitorInterval, TimeUnit.MILLISECONDS);

            KnMCXGroupCleanUpOnMCPTTDisabledBitAudit mcxCleanUpAudit = KnMCXGroupCleanUpOnMCPTTDisabledBitAudit.getInstance();
            ScheduledExecutorService mcxCleanUpAuditExecutor = KnThreadExecutors.newScheduledThreadPool(1, "MCXGroupCleanUPAudit");
            mcxCleanUpAuditExecutor.scheduleAtFixedRate(mcxCleanUpAudit, 30, 5, TimeUnit.SECONDS);

            KnFeatureBitJob knFeatureBitJob = KnFeatureBitJob.getInstance();
            ScheduledExecutorService knFeatureBitJobService = KnThreadExecutors.newScheduledThreadPool(1, "FeatureBitJobMonitor");
            knFeatureBitJobService.scheduleAtFixedRate(knFeatureBitJob, 50, 300, TimeUnit.SECONDS);

            KnRamJobStaleJobMonitor ramJobStaleJobMonitor = KnRamJobStaleJobMonitor.getInstance();
            ScheduledExecutorService knRamJobStaleJobMonitorService = KnThreadExecutors.newScheduledThreadPool(1, "RamJobMonitor");
            knRamJobStaleJobMonitorService.scheduleAtFixedRate(ramJobStaleJobMonitor, 0, 600, TimeUnit.SECONDS);

            //Below thread is used to send the watcher notification
            KnWatcherNotifyAudit watcherNotifyAudit = KnWatcherNotifyAudit.getInstance();
            ScheduledExecutorService watcherNotifyAuditEx = KnThreadExecutors.newScheduledThreadPool(1, "GroupWatcherNotifyAudit");
            watcherNotifyAuditEx.scheduleAtFixedRate(watcherNotifyAudit, 10, 1, TimeUnit.SECONDS);

            KnAsyncPendingTxnInfoPollerThread asyncPendingTxnInfoPollerThread = KnAsyncPendingTxnInfoPollerThread.getInstance();
            ScheduledExecutorService asyncPendingTxnExecutor = KnThreadExecutors.newScheduledThreadPool(1, "XDM_NOTIFY_POLLER");
            asyncPendingTxnExecutor.scheduleAtFixedRate(asyncPendingTxnInfoPollerThread, 5, 500, TimeUnit.MILLISECONDS);

            KnXcapNotifyBufferThread knXcapNotifyBufferThread = KnXcapNotifyBufferThread.getInstance();
            ScheduledExecutorService asyncNotifyBufferThreadExecutor = KnThreadExecutors.newScheduledThreadPool(1, "XDM_NTFY_BUFFER");
            asyncNotifyBufferThreadExecutor.scheduleAtFixedRate(knXcapNotifyBufferThread, 5, 250, TimeUnit.MILLISECONDS);

            KnAsyncFailureJobHandler knAsyncFailureJobHandler = KnAsyncFailureJobHandler.getInstance();
            ScheduledExecutorService failureJobExecutor = KnThreadExecutors.newScheduledThreadPool(1, "XDM_ASYNC_FAILURE_HANDLER");
            failureJobExecutor.scheduleAtFixedRate(knAsyncFailureJobHandler, 30, 180, TimeUnit.MINUTES);

            KnLocationEnabledCorporateJob knLocationEnabledCorporateJob = KnLocationEnabledCorporateJob.getInstance();
            ScheduledExecutorService locationEnabledCorporateJobExecutor = KnThreadExecutors.newScheduledThreadPool(1, "LocationEnabledCorporateJob");
            locationEnabledCorporateJobExecutor.scheduleAtFixedRate(knLocationEnabledCorporateJob, 30, 60, TimeUnit.SECONDS);

            KnAsyncFailedCleanUpJob knAsyncFailedCleanUpJob = KnAsyncFailedCleanUpJob.getInstance();
            ScheduledExecutorService knAsyncFailedCleanUpJobJobExecutor = KnThreadExecutors.newScheduledThreadPool(1, "XDM_ASYNC_CLEAN_UP_JOB");
            knAsyncFailedCleanUpJobJobExecutor.scheduleAtFixedRate(knAsyncFailedCleanUpJob, 12, 12, TimeUnit.HOURS);

            KnGridGainMonitor gridGainMonitor = KnGridGainMonitor.getInstance();
            ScheduledExecutorService gridGainMonitorService = KnThreadExecutors.newScheduledThreadPool(1, "GridGainMonitor");
            gridGainMonitorService.scheduleAtFixedRate(gridGainMonitor, 2, 2, TimeUnit.MINUTES);

            notificationPoller();

            startLargeGroupRecoverAudit();

            knLogger.info(methodName, "poll threads Started !!!!!!! ");

        } catch (Exception e) {
            knLogger.error(methodName, "failed to start bulk poll threads. exiting xdm");
            System.exit(1);
        }

    }

    /**
     * Schedules the XCAP notification poller thread.
     *
     * <p><b>Temporal Workflow Optimization (XCAP_NOTIFICATION_OPTIMIZED=1):</b><br>
     * When the optimized flag is enabled, the poller cadence is forced to <b>1 second</b>
     * so that epoch-eligibility of each watcher MDN can be evaluated quickly.
     * This is the "Hold and Gather" scheduler described in the epic.
     *
     * <p><b>Legacy behaviour (XCAP_NOTIFICATION_OPTIMIZED=0 or absent):</b><br>
     * The interval configured by {@code NOTIFYJOB_AUDIT_INTRVAL} is used (default 1 s).
     *
     * @throws KnBOException if cluster-id cannot be resolved or config retrieval fails
     */
    private static void notificationPoller() throws KnBOException {
        String methodName = "notificationPoller";
        knLogger.info(methodName, FLOW_TAG, "STEP-SCH1 Initializing XCAP notify poller scheduler");

        // ---------------------------------------------------------------
        // Step 1: Retrieve cluster-level runtime configuration
        // ---------------------------------------------------------------
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap =
                KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);

        // ---------------------------------------------------------------
        // Step 2: Determine poll interval
        //   - When temporal-optimized mode is ON  → always 1 second
        //     (ensures epoch expiry is detected within 1 second of crossing)
        //   - When off → use legacy NOTIFYJOB_AUDIT_INTRVAL (default 1 s)
        // ---------------------------------------------------------------
        String rawOptimizedFlag = microServicesParamNameValueMap.get(KnConstants.XCAP_NOTIFICATION_OPTIMIZED);
        String rawConfiguredInterval = microServicesParamNameValueMap.get(NOTIFYJOB_AUDIT_INTRVAL);
        boolean isXcapOptimized =
                KnConstants.ENABLED_STRING.equals(rawOptimizedFlag);

        int notifyJobAuditInterval;
        if (isXcapOptimized) {
            // Temporal batching: poller MUST fire every 1 second to honour epoch windows
            notifyJobAuditInterval = 1;
            knLogger.info(methodName,
                    "XCAP_NOTIFICATION_OPTIMIZED=1 → overriding poll interval to 1 second");
            knLogger.info(methodName, FLOW_TAG,
                    "STEP-SCH2 Optimized mode detected. rawFlag=", rawOptimizedFlag,
                    " configuredLegacyInterval=", rawConfiguredInterval,
                    " effectiveIntervalSeconds=", notifyJobAuditInterval);
        } else {
            // Legacy mode: respect operator-configured audit interval
            notifyJobAuditInterval = Integer.parseInt(rawConfiguredInterval != null ? rawConfiguredInterval : "1");
            knLogger.info(methodName,
                    "XCAP_NOTIFICATION_OPTIMIZED=0 → using configured interval =",
                    notifyJobAuditInterval, "seconds");
            knLogger.info(methodName, FLOW_TAG,
                    "STEP-SCH2 Legacy mode detected. rawFlag=", rawOptimizedFlag,
                    " configuredLegacyInterval=", rawConfiguredInterval,
                    " effectiveIntervalSeconds=", notifyJobAuditInterval);
        }

        // ---------------------------------------------------------------
        // Step 3: Schedule the processor at the determined cadence
        // ---------------------------------------------------------------
        KnXcapNotifyProcessor xcapNotifyProcessor = KnXcapNotifyProcessor.getInstance();
        ScheduledExecutorService notifyProcessorService =
                KnThreadExecutors.newScheduledThreadPool(1, "XcapNotifyProcessor");
        notifyProcessorService.scheduleAtFixedRate(
                xcapNotifyProcessor,
                30,                        // initial delay (seconds) – let system settle
                notifyJobAuditInterval,
                TimeUnit.SECONDS);

        knLogger.info(methodName,
                "XcapNotifyProcessor scheduled: initialDelay=30s, interval=",
                notifyJobAuditInterval, "s, optimizedMode=", isXcapOptimized);
        knLogger.info(methodName, FLOW_TAG,
                "STEP-SCH3 Scheduler active. initialDelay=30s interval=", notifyJobAuditInterval,
                " optimizedMode=", isXcapOptimized,
                " rawFlag=", rawOptimizedFlag,
                " configuredLegacyInterval=", rawConfiguredInterval);
    }


    private static void startLargeGroupRecoverAudit() throws KnBOException {
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);
        int notifyJobAuditInterval = Integer.parseInt(microServicesParamNameValueMap.get(LARGE_GROUP_AUDIT_INTRVAL) != null ? microServicesParamNameValueMap.get(LARGE_GROUP_AUDIT_INTRVAL) : "360");
        KnLargeGroupRecovery knLargeGroupRecovery = KnLargeGroupRecovery.getInstance();
        ScheduledExecutorService largeProcessorService = KnThreadExecutors.newScheduledThreadPool(1, "LargeProcessorService");
        largeProcessorService.scheduleAtFixedRate(knLargeGroupRecovery, 1, notifyJobAuditInterval, TimeUnit.MINUTES);
    }

}
