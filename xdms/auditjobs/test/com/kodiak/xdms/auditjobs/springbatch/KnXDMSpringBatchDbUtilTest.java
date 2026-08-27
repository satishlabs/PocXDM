/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnXDMSpringBatchDbUtilTest.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 15, 2012   7.4
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kodiak.common.dao.KnPersisterTxn;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.loader.KnInitializer;
import com.kodiak.logger.KnLogger;

/**
 * @author Ravi Shanker .P
 * 
 */
public class KnXDMSpringBatchDbUtilTest {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSpringBatchDbUtilTest.class);
	private final String CLASS_NAME = KnXDMSpringBatchDbUtilTest.class.getName();
	KnXDMSpringBatchDbUtil dbUtil = null;
	String pttServerId;
	Map<Integer, List<Integer>> deleteJobDetails;

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
		pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
		dbUtil = new KnXDMSpringBatchDbUtil(pttServerId);
		this.deleteJobDetails = new HashMap<Integer, List<Integer>>();
		List<Integer> jobExeList = new ArrayList<Integer>();
		jobExeList.add(83);
		this.deleteJobDetails.put(81, jobExeList);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.kodiak.xdms.auditjobs.springbatch.KnXDMSpringBatchDbUtil#getCleanupJobDetails(KnPersisterTxn)}
	 * .
	 */
	@Test
	public void testGetCleanupJobDetails() {
		try {
			Map<Integer, List<Integer>> jobDetails = dbUtil.getCleanupJobDetails(null);

			System.out.println("Job Details =" + jobDetails);
		} catch (KnDAOException e) {
			knLogger.error( "testGetCleanupJobDetails", "Exception", e);

		}

	}

	/**
	 * Test method for
	 * {@link com.kodiak.xdms.auditjobs.springbatch.KnXDMSpringBatchDbUtil#deleteJobDetails(java.util.Map, KnPersisterTxn)}
	 * .
	 */
	@Test
	public void testDeleteJobDetails() {
		try {
			dbUtil.deleteJobDetails(deleteJobDetails, null);
		} catch (KnDAOException e) {
			knLogger.error( "testDeleteJobDetails", "Exception", e);

		}
	}

}
