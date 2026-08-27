/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnXDMSpringBatchAudit.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 13, 2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.auditjobs.springbatch;

import java.util.List;
import java.util.Map;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;

/**
 * @author Ravi Shanker .P
 * 
 */
public class KnXDMSpringBatchAudit extends KnAbstractJob {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSpringBatchAudit.class);

	private static final long serialVersionUID = 3313229722918485971L;
	private final String className = KnXDMSpringBatchAudit.class.getName();
	private KnXDMSpringBatchDbUtil springDbUtil;

	public KnXDMSpringBatchAudit() {
		String pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
		springDbUtil = new KnXDMSpringBatchDbUtil(pttServerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kodiak.frameworks.jobscheduler.KnAbstractJob#executeTask()
	 */
	@Override
	public boolean executeTask() throws KnJobSchedulerException {
		String methodName = "executeTask()";
		knLogger.info( methodName, "ENTRY: start of Spring Batch cleanup");
		KnPersisterTxn persisterTxn = null;
		boolean jobStatus = false;
		try {
			persisterTxn = KnPersisterTxn.getPersisterTxn();
			knLogger.debug( methodName, "Opening the transaction");
			persisterTxn.open();

			// get job details which are completed, failed but not in started
			// state
			Map<Integer, List<Integer>> cleanupJobDetails = springDbUtil.getCleanupJobDetails(persisterTxn);

			if (cleanupJobDetails != null && !cleanupJobDetails.isEmpty()) {
				// calling for delete job details
				springDbUtil.deleteJobDetails(cleanupJobDetails, persisterTxn);
			}

			knLogger.debug( methodName, "saving the transaction");
			persisterTxn.save();

		} catch (KnDAOException e) {
			knLogger.error( methodName, "DAO Exception occurred");
			knLogger.error( methodName, e);
			KnDbUtil.rollback(persisterTxn);

		} catch (Exception e) {
			knLogger.error( methodName, "DAO Exception occurred");
			knLogger.error( methodName, e);
			KnDbUtil.rollback(persisterTxn);
		}

		return jobStatus;
	}

	public String getCronExpression() {
		String methodName = "getCronExpression()";
		knLogger.info( methodName, "ENTRY: Get Cron Expression - ");
		// timer is set to every 1 hour
		String timer = "1";
		String cronExpression = "0 0 */" + timer + " ? * *";
		knLogger.info( methodName, "EXIT: Cron Expression - ", cronExpression);
		return cronExpression;
	}

}
