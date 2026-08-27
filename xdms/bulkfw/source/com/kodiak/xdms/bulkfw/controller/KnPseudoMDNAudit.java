/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.controller;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPseudoMDNAudit.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
 * ************************************************************************
 */
import com.kodiak.common.dao.KnDBConfigInfo;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.kuidgenerator.KnKUIDConstants;
import com.kodiak.utilities.kuidgenerator.KnKUIDGenerator;
import com.kodiak.xdms.bulkfw.KnBulkFwInitializer;
import com.kodiak.xdms.bulkfw.dao.KnSpringBatchDAO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;

public class KnPseudoMDNAudit implements Runnable
{
	private static final KnLogger knLogger = KnLogger.getLogger(KnPseudoMDNAudit.class);
	private KnKUIDGenerator kuidGenerator;
	private int upperWaterMark;
	private int pseudoMdnCheckInterval;
	private KnSpringBatchDAO springBatchDAO;
	private String pttServerId = null;


	public KnPseudoMDNAudit() throws KnException {
		super();
		init();
	}

	private void init() throws KnException {
		String methodName = "init()";
		knLogger.info(methodName, "PseudoMdnAudit Initializing");
		this.upperWaterMark = KnBulkFwInitializer.upperWaterMark;
		kuidGenerator = KnKUIDGenerator.getInstance();
		KnDBConfigInfo dbConfigInfo = KnDbUtil.getDBConfigInfo();
		pttServerId = dbConfigInfo.getLocalPttId();
		springBatchDAO = new KnSpringBatchDAO(pttServerId);
		pseudoMdnCheckInterval = KnBulkFwConstants.PSEUDO_MDN_CHECK_INTEFVAL * 60 * 1000;
		 knLogger.debug( methodName, "Initialized PseudoMdnAudit with interval -", pseudoMdnCheckInterval, "upperWaterMark -" , upperWaterMark );
	}

	@Override
	public void run() {
		String methodName = "run()";

		while(true)	{
			try {
				knLogger.info(methodName, "PseudoMdnAudit acting....");
				kuidGenerator.validateAvailableMdns(upperWaterMark,KnKUIDConstants.COUNTRYCODE);
				springBatchDAO.removeSpringBatchHistoryTableData();
				Thread.sleep(pseudoMdnCheckInterval);
			}
			catch (KnException | InterruptedException ignored) {

			}
		}
	}

}
