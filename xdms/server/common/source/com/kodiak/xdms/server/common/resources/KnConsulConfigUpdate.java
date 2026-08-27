/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.resources;

import com.kodiak.common.filewatcher.KnFilePropertiesUpdate;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class KnConsulConfigUpdate implements KnFilePropertiesUpdate {
	private static final KnLogger knLogger = KnLogger.getLogger(KnConsulConfigUpdate.class);
	private Properties consulConfigProps = new Properties();

	@Override
	public void updateProperties(String activeReleasePath, String fileName) 
	{
		String methodName = "updateConsulProperties()";
		knLogger.info(methodName , "Consul Configuration file is modified !!!");
		knLogger.info(methodName, "Consul XDM current state is ",KnStatusManagerClient.getCurrentState());
		if(KnConstants.RESPONSE_STATUS.SUCCESS.value() != readConfigFile())
		{
			knLogger.error(methodName,"Consul  Reading Configuration Failed returning ..");
			return;
		}
		
		if (KnStatusManagerClient.getCurrentState() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
            //log active release path and file name for better visibility
            knLogger.info(methodName, "Active Release Path: ", activeReleasePath, " File Name: ", fileName);
            KnConsulConfig consulConfig = KnConsulConfig.getInstance(fileName);
            synchronized (consulConfig) {
				knLogger.info(methodName, "Consul Updating Dynamic Properties");
				consulConfig.loadRuntimeProps(consulConfigProps);
			}
		} else {
			knLogger.info(methodName, "XDM current state is ",KnStatusManagerClient.getCurrentState()," skipping the binding queue");
		}
	}

	public int readConfigFile()
	{
		String methodName = "readConfigFile()";
		
		String activeReleasePath = KnConstants.ACTIVE_RELEASE_DIR;
		String serviceConfigFile = activeReleasePath + File.separator + "rmq-cluster-status.props";
		KnConstants.RESPONSE_STATUS status=KnConstants.RESPONSE_STATUS.FAILURE;
		knLogger.info(methodName , "serviceConfigFile", serviceConfigFile);
		try (FileInputStream ipStream = new FileInputStream(serviceConfigFile))
		{
			consulConfigProps.load(ipStream);
			status=KnConstants.RESPONSE_STATUS.SUCCESS;
		} catch (FileNotFoundException e) {
			knLogger.error(methodName, "Exception While Reading config File ", e);
			status= KnConstants.RESPONSE_STATUS.FAILURE;
		}
		catch (IOException e) {
			knLogger.error(methodName, "Exception While Reading config File ", e);
			status= KnConstants.RESPONSE_STATUS.FAILURE;
		}
		knLogger.info(methodName , "Exit response", status);
		return status.value();
		
	}


}
