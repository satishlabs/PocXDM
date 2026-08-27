/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnXDMAuditJobs.java
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
package com.kodiak.xdms.auditjobs;

import java.util.ArrayList;
import java.util.List;

import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.IJobSchedulerIntf;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.xdms.auditjobs.asyncfw.KnAsyncFWAudit;
import com.kodiak.xdms.auditjobs.asyncfw.KnAsyncFWCleanUp;
import com.kodiak.xdms.auditjobs.bulkfw.KnBulkConfigDocDiffAudit;
import com.kodiak.xdms.auditjobs.asyncfw.KnCBSStaleCleanupAudit;
import com.kodiak.xdms.auditjobs.common.KnXDMSTempPasswordAudit;
import com.kodiak.xdms.auditjobs.springbatch.KnXDMSpringBatchAudit;
//import com.kodiak.xdms.mediator.resources.jobs.upm.KnInconsistentUPMDataAudit;

/**
 * Class which initializes the xdms audit jobs Get the scheduler instance and
 * assign the jobs to the scheduler.
 * 
 * @author Ravi Shanker .P
 * 
 */
public final class KnXDMAuditJobs {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMAuditJobs.class);
	private final String className = KnXDMAuditJobs.class.getName();
	private static KnXDMAuditJobs instance = null;
	private IJobSchedulerIntf jobScheduler;

	private KnXDMAuditJobs() {
		initialize();
	}

	/**
	 * singleton initialization of the Class
	 * 
	 * @return
	 */
	public static synchronized KnXDMAuditJobs createInstance() {
		if (instance == null) {
			instance = new KnXDMAuditJobs();
		}
		return instance;

	}

	private void initialize() {
		String methodName = "initializeI()";
		knLogger.info( methodName, "Initilization of XDM Audit Jobs");
		jobScheduler = KnJobSchedulerImpl.getInstance();

		try {
			/*List<KnAbstractJob> auditJobs = new ArrayList<KnAbstractJob>();
			KnXDMSpringBatchAudit batchAudit = new KnXDMSpringBatchAudit();
			KnBulkConfigDocDiffAudit docDiffAudit = new KnBulkConfigDocDiffAudit();
			auditJobs.add(batchAudit);
			auditJobs.add(docDiffAudit);
			knLogger.debug( methodName, "Assigning of Audit Jobs", auditJobs);
			jobScheduler.addRamCronJob(auditJobs, batchAudit.getCronExpression());*/
			KnXDMSTempPasswordAudit tempPassAudit = new KnXDMSTempPasswordAudit();
			//KnInconsistentUPMDataAudit inconsistentUPMDataAudit = new KnInconsistentUPMDataAudit();
			List<KnAbstractJob> tempPassAuditJob = new ArrayList<KnAbstractJob>();
			tempPassAuditJob.add(tempPassAudit);
			//tempPassAuditJob.add(inconsistentUPMDataAudit);
			knLogger.info( methodName, "Assigning of tempPassAudit Jobs", tempPassAuditJob);
			jobScheduler.addRamCronJob(tempPassAuditJob, tempPassAudit.getCronExpression());
			//jobScheduler.addRamCronJob(tempPassAuditJob, inconsistentUPMDataAudit.getCronExpression());
			KnAsyncFWAudit asyncFWAudit = new KnAsyncFWAudit();
			List<KnAbstractJob> asyncFWAuditJob = new ArrayList<>();
			asyncFWAuditJob.add(asyncFWAudit);
			knLogger.info( methodName, "Assigning of async FW AuditJobs", asyncFWAuditJob);
			jobScheduler.addRamCronJob(asyncFWAuditJob, asyncFWAudit.getCronExpression());

			KnAsyncFWCleanUp asyncFWCleanUp = new KnAsyncFWCleanUp();
			List<KnAbstractJob> asyncFWCleanUpJob = new ArrayList<>();
			asyncFWCleanUpJob.add(asyncFWCleanUp);
			knLogger.info( methodName, "Assigning of async FW cleanup AuditJobs", asyncFWCleanUpJob);
			jobScheduler.addRamCronJob(asyncFWCleanUpJob, asyncFWCleanUp.getCronExpression());

			KnCBSStaleCleanupAudit cbsStaleCleanupAudit = new KnCBSStaleCleanupAudit();
			List<KnAbstractJob> cbsStaleCleanupJob = new ArrayList<>();
			cbsStaleCleanupJob.add(cbsStaleCleanupAudit);
			knLogger.info( methodName, "CBS stale cleanup job for UPM", cbsStaleCleanupJob);
			jobScheduler.addRamCronJob(cbsStaleCleanupJob, cbsStaleCleanupAudit.getCronExpression());

		} catch (KnJobSchedulerException e) {
			knLogger.error( methodName, "Exception", e);
			knLogger.error( methodName, e);

		}

	}

}
