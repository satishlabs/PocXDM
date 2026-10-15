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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.kodiak.logger.KnLogger;

class KnDestroyPoolThread extends TimerTask {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDestroyPoolThread.class);
	private static Timer timevalue = new Timer("DestroyPoolThread");
	private KnDBEvent mDestroyPoolNotifyEvent = null;

	KnDestroyPoolThread() {
		mDestroyPoolNotifyEvent = new KnDBEvent();
		mDestroyPoolNotifyEvent.mEventName = KnDBConst.DESTROY_POOL_NOTIFY_EVT;
	}

	@Override
	public void run() {
		try {
			KnDBManager.getDBManagerInstance().postEvent(mDestroyPoolNotifyEvent);
		} catch (Exception e) {
			knLogger.error("DestroyPoolThread:run()", "Exception", e);
		}
	}

	public static void startDestroyPoolThread() {
		KnDestroyPoolThread lDestroyPoolThread = new KnDestroyPoolThread();
		knLogger.debug("startDestroyPoolThread()", "Starting DestroyPoolThread");
		Date lCurrentTime = new Date(System.currentTimeMillis());
		timevalue.schedule(lDestroyPoolThread, lCurrentTime, (12 * 10 * 1000));
	}

	public static void stopDestroyPoolThread() {
		timevalue.cancel();
	}

}