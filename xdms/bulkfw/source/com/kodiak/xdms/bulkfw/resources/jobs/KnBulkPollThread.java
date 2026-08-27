/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.resources.jobs;

import java.util.List;

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.bulkfw.controller.KnBulkFwController;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;

public class KnBulkPollThread implements Runnable{
	private static final KnLogger knLogger = KnLogger.getLogger(KnBulkPollThread.class);
	
	@Override
	public void run() {
		final String methodName = "run()";
		try {
			
			if (KnStatusManagerClient.getCurrentState() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
				knLogger.info(methodName, "KnBulkPollThread current state of card - ", KnStatusManagerClient.getCurrentState());
				KnBulkFwController bulkFwController = (KnBulkFwController) KnSpringContextProvider.getApplicationContext().getBean("knBulkFwController");
				List<KnBulkDTO> bulkOrderJobs = bulkFwController.getPendingBulkOrdersDetails();
				if (bulkOrderJobs != null) {
					knLogger.info(methodName, "Got pending bulk order details-", bulkOrderJobs.size());
					for (KnBulkDTO knBulkDTO : bulkOrderJobs) {
						bulkFwController.newJobAlert(null, knBulkDTO);
					}
				}
			}
			
		} catch (Exception e) {
			 knLogger.error(methodName, "Failed to add new job to job store", e);
		}
	}

}
