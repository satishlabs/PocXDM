/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.auditjobs.common;

import java.util.List;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;

public class KnXDMSTempPasswordAudit extends KnAbstractJob {
	private static final long serialVersionUID = 1L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSTempPasswordAudit.class);
	private KnGeneralCacheUtil cacheUtil = KnGeneralCacheUtil.getInstance();

	public boolean executeTask() throws KnJobSchedulerException {
		String methodName = "executeTask()";
		long currentTime = System.currentTimeMillis();
		knLogger.info(methodName, "ENTRY: Start of Temp password Audit clean up  for exp Time " + currentTime);
		try {
			List<String> mdns = cacheUtil.retriveExpPasswordMDNs(currentTime);
			knLogger.info(methodName, "retrive MDNs for deletion " + KnGDPRTemplate.mdnList(mdns));
			int rowdeleted = 0;
			if (!mdns.isEmpty())
				rowdeleted = cacheUtil.deleteExpPasswordMDNs(mdns);
			knLogger.info(methodName, "number of  MDNs deleted " + rowdeleted);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "DAO Exception occurred");
			knLogger.error(methodName, e);
		}
		return false;

	}

	public String getCronExpression() {
		String methodName = "getCronExpression()";
		knLogger.info(methodName, "ENTRY: Get Cron Expression - ");
		// timer is set to every 1 minute
		String timer = "1";
		String cronExpression = "0 */" + timer + " * ? * *";
		knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
		return cronExpression;

	}
}
