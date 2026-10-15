/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDestroyPoolThread.java
 * Subsystem:
 * <p/>
 * Name                 Date          Release
 * -----------------    -----------   -------
 * WebApps              29/04/14      7.9
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
package com.kodiak.dbmgr;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;

class KnBusyIdleAuditThread extends TimerTask {
	private static final KnLogger knLogger = KnLogger.getLogger(KnBusyIdleAuditThread.class);
	private static Timer timevalue = new Timer("KnBusyIdleAuditThread");
	final public HashMap<String, KnDBConnectionDetails> dBConnectionDetailsMap;

	KnBusyIdleAuditThread(HashMap<String, KnDBConnectionDetails> dBConnectionDetailsMap) {
		this.dBConnectionDetailsMap = dBConnectionDetailsMap;
	}

	@Override
	public void run() {
		final String methodName = "run";
		for (Map.Entry<String, KnDBConnectionDetails> entry : dBConnectionDetailsMap.entrySet()) {
			KnDBConnectionDetails dBConnectionDetails = entry.getValue();
			ComboPooledDataSource dataSource = dBConnectionDetails.getComboPooledDataSource();
			try {
				if(dataSource != null){
					knLogger.info("run","Busy Connection",dataSource.getNumBusyConnections(),"Idle Connection",dataSource.getNumIdleConnections());
					// If Busy Connection is greater than 80% of max pool size
					if(dataSource.getNumBusyConnections() > dataSource.getMaxPoolSize() * 0.80) {
						knLogger.error(methodName, "Generating Alarm","JDBC URL",dataSource.getJdbcUrl());
						String serviceName = KnDBProps.getInstance().getIpAddress() + ":" + dBConnectionDetails.getIpAddress()+":Data Store:"+dBConnectionDetails.getDbConnectionString();
						knLogger.error(methodName, "serviceName",serviceName,"Alarm Id 6137");
						KnAlarmGeneratorUtil.generateAlarm2(6137, KnAlarmConstants.SEVERITY_MAJOR,serviceName);
					}
				}
			} catch (SQLException e) {
				knLogger.error(methodName,"Exception While Getting Busy And Idle Conenction From DataSource",e);
			}
			
		}
	}

	public void start() {
		final String methodName = "start";
		knLogger.debug(methodName, "Starting KnBusyIdleAuditThread");
		timevalue.schedule(this, new Date(System.currentTimeMillis()), KnDBProps.getInstance().getBusyIdleAuditInterval() * 1000);
		knLogger.debug(methodName, "Started KnBusyIdleAuditThread");
	}
}