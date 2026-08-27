/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.commdto.common.KnMessageConstants;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.messaging.resources.KnFqdnInfo;
import com.kodiak.frameworks.messaging.resources.KnMsgConsumerInitializer;
import com.kodiak.frameworks.messaging.resources.KnQueueDetails;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.xdms.server.common.cb.util.KnCBSRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.kodiak.common.resources.KnConstants.*;
public class KnConsulConfig {
	private static final KnLogger knLogger = KnLogger.getLogger(KnConsulConfig.class);

	// CBS Cluster Status Constants: 0 = DOWN, 2 = UP
	private static final int CBS_CLUSTER_STATUS_DOWN = 0;
	private static final int CBS_CLUSTER_STATUS_UP = 2;

    private int clusterStatusLocal;
    private int clusterStatusRemote;
	private static KnConsulConfig instance;
    private KnCBSRepository cbMgr; // KnCBSRepository instance loaded via reflection
	// Static variables to persist state globally across the application
	private static int localPrevState = -1; // Initialized to -1 to ensure first check triggers
	private static int remotePrevState = -1;
	private KnConsulConfig(String configPath) {
		this.readConsulProps(initConsulConfig(configPath));
        this.cbMgr = KnCBSRepository.getInstance();
        if(this.cbMgr == null) {
            knLogger.warn("KnCBSRepository instance is null. CBS cluster status updates will be skipped.");
        }
	}

	public static synchronized KnConsulConfig getInstance(String configPath) {
		if (instance == null) {
			instance = new KnConsulConfig(configPath);
            instance.cbMgr = KnCBSRepository.getInstance();
                if(instance.cbMgr == null) {
                    knLogger.warn("KnCBSRepository instance is null. CBS cluster status updates will be skipped.");
                }
		}
        else {
            knLogger.warn("KnConsulConfig instance is not null.");
        }
		return instance;
	}

	public static synchronized KnConsulConfig getInstance() {
		if (instance == null) {
			knLogger.fatal("getInstance()", "Consul Config Manager not initialized");
			throw new IllegalStateException("KnConsulConfig not initialized. Call getInstance(String) first.");
		}
		return instance;
	}
	 

	private Properties initConsulConfig(String configPath) {
		String methodName = "initConsulConfig(String)";
		knLogger.info(methodName, "Entry :");
		Properties consulProps = new Properties();
		FileInputStream ipStream = null;
		try {
			String configFile=System.getProperty("activeRelDir") + File.separator +configPath;
			ipStream = new FileInputStream(configFile);
			consulProps.load(ipStream);
			knLogger.info(methodName, "ConsulProps Properties loaded");

		} catch (IOException e) {
			knLogger.info(methodName, "ConsulProps Properties : ", consulProps);
			knLogger.error(methodName, "Exception occurred!!!", e);
		} finally {
			if (ipStream != null) {
				try {
					ipStream.close();
				} catch (IOException e) {
					knLogger.error(methodName, "Exception occurred while closing stream", e);
				}
			}
		}
		//knLogger.info(methodName, "ConsulProps Properties : ", rmqProps);
		return consulProps;
	}

	public void loadRuntimeProps(Properties ptxConfigProps) {
		String methodName = "loadRuntimeProps()";
		knLogger.info(methodName, "Entry : ");
		updateAppsCBSClusterStatus(ptxConfigProps);
		knLogger.info(methodName, "Exit : ");
	}

    /**
     * Updates the CBS cluster status based on the provided configuration properties.
     * <p>
     * This method reads the local and remote CBS cluster status from the given properties,
     * logs the received values, and updates the cluster state accordingly. It uses reflection
     * to invoke methods on the CBS Repository manager (`cbMgr`) to handle alarms and
     * initialize or close cluster connections based on the status values.
     * <ul>
     *   <li>If both local and remote statuses are DOWN, it generates an alarm and closes the connection.</li>
     *   <li>If the local status is UP and has changed, it clears the alarm and initializes the local cluster.</li>
     *   <li>If the remote status is UP and has changed, it clears the alarm and initializes the remote cluster.</li>
     *   <li>If neither status is 0 nor 2, no action is taken.</li>
     * </ul>
     * If the CBS Repository manager is not available, the update is skipped.
     *
     * @param configProps the configuration properties containing CBS cluster status values
     */
	private void updateAppsCBSClusterStatus(Properties configProps) {
        String methodName = "UpdateAppsCBSClusterStatus()";
        knLogger.info(methodName, "Entry :");
        try {
            int appsCBSclusterStatusLocal = Integer.parseInt(configProps.getProperty(KnConstants.APPS_CBS_CLUSTER_STATUS_LOCAL).trim());
            int appsCBSclusterStatusRemote = Integer.parseInt(configProps.getProperty(KnConstants.APPS_CBS_CLUSTER_STATUS_REMOTE).trim());
            knLogger.info(methodName, "APPS_CBS_CLUSTER_STATUS_LOCAL received: ", appsCBSclusterStatusLocal + "Previous APPS_CBS_CLUSTER_STATUS_LOCAL: ", localPrevState);
            knLogger.info(methodName,  "APPS_CBS_CLUSTER_STATUS_REMOTE received: ", appsCBSclusterStatusRemote + "Previous APPS_CBS_CLUSTER_STATUS_REMOTE: ", remotePrevState);

            if (cbMgr == null) {
                knLogger.warn(methodName, "CBS Repository not available, skipping CBS cluster status update");
                return;
            }
            // if (appsCBSclusterStatusLocal == localPrevState && appsCBSclusterStatusRemote == remotePrevState) {
            //     knLogger.warn(methodName, "Received CBS cluster status values are same as previous. No state change detected.");
            //     return;
            // }

            if (appsCBSclusterStatusLocal == CBS_CLUSTER_STATUS_DOWN && appsCBSclusterStatusRemote == CBS_CLUSTER_STATUS_DOWN) {
                // If 0 for both local and remote, both consumers are down, status is critical
                knLogger.info(methodName, "APPS_CBS_CLUSTER_STATUS values are 0 (Critical). Stopping remote connection.");
                cbMgr.cbsClusterGenerateAlarm(false); // local cluster alarm
                cbMgr.closePrevCouchBaseConnection();
            } else if (appsCBSclusterStatusLocal == CBS_CLUSTER_STATUS_UP) {
                // If 2, consumer is up, status is passing
                knLogger.info(methodName, "APPS_CBS_CLUSTER_STATUS_LOCAL is 2 (Passing). Starting local connection.");
                // Start the local cluster connection should be make up
                cbMgr.cbsClusterClearAlarm(false);
                // initialize bucket with local cluster
                cbMgr.initCBSdkQueryFQDN(false);
            } else if (appsCBSclusterStatusRemote == CBS_CLUSTER_STATUS_UP) {
                // If 2, consumer is up, status is passing
                knLogger.info(methodName, "APPS_CBS_CLUSTER_STATUS_REMOTE is 2 (Passing). Starting remote connection.");
                cbMgr.cbsClusterClearAlarm(false);
                // Start the remote cluster connection should be make up
                cbMgr.initCBSdkQueryFQDN(true);
            } else {
                knLogger.info(methodName, "APPS_CBS_CLUSTER_STATUS is neither 0 nor 2. No action taken.");
            }

            localPrevState = appsCBSclusterStatusLocal;
            remotePrevState = appsCBSclusterStatusRemote;
            // Log the updated states of localPrevState and RemotePrevState
            knLogger.info(methodName, "Updated APPS_CBS_CLUSTER_STATUS_LOCAL: ", localPrevState + "Updated APPS_CBS_CLUSTER_STATUS_REMOTE: ", remotePrevState);

        } catch (NumberFormatException e) {
            knLogger.error(methodName, "Error parsing APPS_CBS_CLUSTER_STATUS value", e);
        } finally {
            knLogger.info(methodName, "Exit :");
        }
    }

	private void readConsulProps(Properties configProps) {
		String methodName = "readConsulProps()";
        String localVal = configProps.getProperty(KnConstants.APPS_CBS_CLUSTER_STATUS_LOCAL, "2");
        String remoteVal = configProps.getProperty(KnConstants.APPS_CBS_CLUSTER_STATUS_REMOTE, "2");
        try {
            clusterStatusLocal = Integer.parseInt(localVal.trim());
        } catch (NumberFormatException e) {
            knLogger.error(methodName, "Invalid APPS_CBS_CLUSTER_STATUS_LOCAL value, defaulting to UP(2): " + localVal, e);
            clusterStatusLocal = CBS_CLUSTER_STATUS_UP;
        }
        knLogger.info(methodName, "ClusterStatusLocal : ", clusterStatusLocal);
        try {
            clusterStatusRemote = Integer.parseInt(remoteVal.trim());
        } catch (NumberFormatException e) {
            knLogger.error(methodName, "Invalid APPS_CBS_CLUSTER_STATUS_REMOTE value, defaulting to UP(2): " + remoteVal, e);
            clusterStatusRemote = CBS_CLUSTER_STATUS_UP;
        }
        knLogger.info(methodName, "ClusterStatusRemote : ", clusterStatusRemote);
        logDetails();
	}

	protected void logDetails() {
		String method="logDetails";
		knLogger.info(method,"KnConsulConfig [clusterStatusLocal=" + clusterStatusLocal + "clusterStatusRemote=" + clusterStatusRemote + "]");
	}

}
