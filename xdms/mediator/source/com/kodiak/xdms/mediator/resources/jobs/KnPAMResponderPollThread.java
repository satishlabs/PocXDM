/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.mediator.helper.KnXDMPamResponseHandler;
import com.kodiak.xdms.mediator.helper.KnXlaDeleteEvent;
import com.kodiak.xdms.mediator.helper.KnXlaInsertEvent;
import com.kodiak.xdms.mediator.helper.KnXlaUpdateEvent;

public class KnPAMResponderPollThread implements Runnable{
	private static final KnLogger knLogger = KnLogger.getLogger(KnPAMResponderPollThread.class);
	
	@Override
	public void run() {
		final String methodName = "run()";
		
		try {
			if (KnStatusManagerClient.getCurrentState() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
				knLogger.info(methodName, " KnPAMResponderPollThread current state of card - ", KnStatusManagerClient.getCurrentState());
				KnXDMPamResponseHandler responseHandler = KnXDMPamResponseHandler.getInstance();
				KnXlaUpdateEvent xlaUpdateEvent = KnXlaUpdateEvent.getInstance();
				knLogger.debug(methodName, "xlaUpdateEvent.getInstance()", xlaUpdateEvent);
				KnXlaInsertEvent xlaInsertEvent = KnXlaInsertEvent.getInstance();
				knLogger.debug(methodName, "xlaInsertEvent.getInstance()", xlaInsertEvent);
				KnXlaDeleteEvent xlaDeleteEvent = KnXlaDeleteEvent.getInstance();
				knLogger.debug(methodName, "xlaDeleteEvent.getInstance()", xlaDeleteEvent);
				knLogger.debug(methodName, "sending  PAM notifications");
				responseHandler.sendPAMNotifications();
			}
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception occured", e);
		}
		
	}
}
