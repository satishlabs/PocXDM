/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkConfigDocDiffDbUtilTest.java
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

import java.util.ArrayList;
import java.util.List;

import com.kodiak.common.dao.KnPersisterTxn;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.loader.KnInitializer;
import com.kodiak.logger.KnLogger;

/**
 * @author Ravi Shanker .P
 * 
 */
public class KnBulkConfigDocDiffDbUtilTest {
	private static final KnLogger knLogger = KnLogger.getLogger(KnBulkConfigDocDiffDbUtilTest.class);
	private final String CLASS_NAME = KnBulkConfigDocDiffDbUtil.class.getName();
	private KnBulkConfigDocDiffDbUtil dbUtil = null;
	private List<Integer> boList = null;

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
		dbUtil = new KnBulkConfigDocDiffDbUtil();
		boList = new ArrayList<Integer>();
		boList.add(1);
		boList.add(2);
		boList.add(3);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.kodiak.xdms.auditjobs.bulkfw.KnBulkConfigDocDiffDbUtil#
	 * .
	 */

	@Test
	public void testGetListOfSubsConfigCompletedJobs() {
		try {
			boList = dbUtil.getSubsConfCompletedJobList(null);
			System.out.println("Jobs List " + boList);
		} catch (KnDAOException e) {
			knLogger.error( "get", "Exception", e);

		}
	}

	/**
	 * Test method for
	 * {@link com.kodiak.xdms.auditjobs.bulkfw.KnBulkConfigDocDiffDbUtil#cleanUpSubsConfigBO(java.util.List, KnPersisterTxn)}
	 * .
	 */
	@Test
	public void testCleanUpSubsConfigBO() {

		try {
			dbUtil.cleanUpSubsConfigBO(boList, null);
		} catch (KnDAOException e) {
			knLogger.error( "testCleanUpSubsConfigBO", "Exception", e);

		}
	}

}
