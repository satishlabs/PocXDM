/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkConfigDocDiffAuditTest.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 16, 2012   7.4
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
package com.kodiak.xdms.auditjobs.bulkfw;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.kodiak.common.loader.KnInitializer;
import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;

/**
 * @author Ravi Shanker .P
 * 
 */
public class KnBulkConfigDocDiffAuditTest {
	private static final KnLogger knLogger = KnLogger.getLogger(KnBulkConfigDocDiffAuditTest.class);
	private final String CLASS_NAME = KnBulkConfigDocDiffAuditTest.class.getName();
	private KnBulkConfigDocDiffAudit docDiffAudit = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		KnInitializer.getInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		docDiffAudit = new KnBulkConfigDocDiffAudit();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.kodiak.xdms.auditjobs.bulkfw.KnBulkConfigDocDiffAudit#executeTask()}
	 * .
	 */
	@Test
	public void testExecuteTask() {
		try {
			docDiffAudit.executeTask();
		} catch (KnJobSchedulerException e) {
			knLogger.error( "testExecuteTask()", "Exception", e);

		}
	}

}
