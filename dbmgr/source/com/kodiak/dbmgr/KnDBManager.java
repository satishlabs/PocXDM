/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBManager.java
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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.kodiak.logger.KnLogger;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class KnDBManager extends Thread {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDBManager.class);
	private boolean mShutdown = false;
	private static KnDBManager mDbManager = new KnDBManager();
	private Vector<KnDBEvent> mEventQueue = new Vector<KnDBEvent>();
	private boolean mIsInitialized = false;
	private KnDBConnectionHandler mDBConnHandler = null;
	// Start INT24865
	ArrayList<KnDBEvent> mInvalidPool = new ArrayList<KnDBEvent>();

	// End INT24865

	private KnDBManager() {

	}

	public static KnDBManager getDBManagerInstance() {
		return mDbManager;
	}

	public void initDBManager(String clientId) {
		String methodName = "initDBManager(String pClientID)";
		knLogger.entry(methodName, "pClientID", clientId);
		if (!isInitialized()) {
			try {
				Class.forName(KnDBConst.TIMESTEN_DRIVER).newInstance();

				KnDBProps.getInstance().init();

				KnEMSInfo.getInstance().init();

				mDBConnHandler = new KnDBConnectionHandler();
				mDBConnHandler.initProperties();

				new KnDBActivePollThread(mDBConnHandler).startAudit(Long.parseLong(KnDBProps.getInstance().getActiveIPAuditInterval()));

				mDbManager.setName("DB-MANAGER-THREAD");
				mDbManager.start();

				// Start INT24865
				KnDestroyPoolThread.startDestroyPoolThread();
				// End INT24865
				
				new KnBusyIdleAuditThread(mDBConnHandler.getdBConnectionDetailsMap()).start();

				mIsInitialized = true;
				knLogger.exit("initDBManager(String)", "Initialized DB Manager with Client ID::" + clientId);
			} catch (Exception ex) {
				knLogger.error("initDBManager(String)", "DB Manager Not Initialized", ex);
			}
		}
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	void postEvent(KnDBEvent pEventObj) {
		knLogger.debug("postEvent()", "Event Name:" + pEventObj.mEventName);
		synchronized (mEventQueue) {
			if (!mShutdown) {
				mEventQueue.add(pEventObj);
				mEventQueue.notify();
			}
		}
	}

	@Override
	public void run() {
		KnDBEvent lEventObj = null;
		knLogger.debug("run()", "Entered run method");
		while (!mShutdown) {
			synchronized (mEventQueue) {
				try {
					if (mEventQueue.size() == 0) {
						mEventQueue.wait();
					}
				} catch (InterruptedException e) {
					knLogger.error("run()", "Interrupted Exception::", e);
				}
			}
			synchronized (mEventQueue) {
				lEventObj = mEventQueue.remove(0);
			}
			try {
				if (lEventObj != null) {
					knLogger.debug("run", "Event Queue Size::" + mEventQueue.size());
					processEvent(lEventObj);
				}
				lEventObj = null;
			} catch (Exception e) {
				knLogger.error("run", "Caught Exception", e);

			}
		}
	}

	private int processEvent(KnDBEvent pEvent) {
		knLogger.debug("ProcessEvent Entered with Event Name::", "Event::" + pEvent.mEventName);
		switch (pEvent.mEventName) {
		case KnDBConst.CONN_ACTIVE_DB_EVT:
			knLogger.debug("Processing Event::CONN_ACTIVE_DB_EVT::", "Event::" + pEvent.mEventName);
			connectToActiveDB(pEvent);
			break;
		// Start INT24865
		case KnDBConst.DESTROY_POOL_EVT:
			knLogger.debug("Processing Event::DESTROY_POOL_EVT::", "EVENT Name::" + pEvent.mEventName);
			processPoolDestroy(pEvent);
			break;
		case KnDBConst.DESTROY_POOL_NOTIFY_EVT:
			knLogger.debug("Processing Event::DESTROY_POOL_NOTIFY_EVT::", "Event Name::" + pEvent.mEventName);
			processPoolDestroyNotifyEvent();
			break;
		// End INT24865
		}
		return 0;
	}

	public Connection getConnection(String pDBID) {
		knLogger.debug("getConnection(pDBID)::", "PTTID::" + pDBID);
		return getConnection(pDBID, false);
	}

	public KnSqlConnection getConnection(String pDBID, boolean pReadOnly) {
		KnSqlConnection lConn = null;
		knLogger.debug("getConnection(pDBID,readOnly)::", "PTTID::" , pDBID);
		try {
			lConn = mDBConnHandler.getConnection(pDBID, pReadOnly);
		} catch (Exception e) {
			knLogger.error("getConnection(pDBID,readOnly)::", "Exception in getConection for PTTSERVER_ID::" + pDBID + "::", e);
		}
		return lConn;
	}

	private void connectToActiveDB(KnDBEvent pEvent) {
		KnDBConnectionDetails lDBConnDetailObj = null;
		knLogger.debug("connectToActiveDB()::", "Event Name::" + pEvent.mEventName);
		// Connect to Alternate PTTServer
		lDBConnDetailObj = mDBConnHandler.dBConnectionDetailsMap.get(pEvent.mDBID);
		knLogger.debug("connectToActiveDB()::current IP Address::" + lDBConnDetailObj.getIpAddress());
		knLogger.debug("connectToActiveDB()", "Active IP Address::" + pEvent.mIPAddress);
		if (lDBConnDetailObj != null && !(lDBConnDetailObj.getIpAddress().equalsIgnoreCase(pEvent.mIPAddress))) {
			synchronized (lDBConnDetailObj) {
				knLogger.debug("connectToActiveDB()", "Current IP Address::" + lDBConnDetailObj.getIpAddress());
				lDBConnDetailObj.setIpAddress(pEvent.mIPAddress);
				knLogger.debug("connectToActiveDB()", "Active's IP Address::" + pEvent.mDBConnDetailObj.getIpAddress());
				// Start INT24865
				postPoolDestroyPoolEvent(lDBConnDetailObj);
				// End INT24865
				knLogger.error("connectToActiveDB()", "setComboPooledDataSource == null");
				lDBConnDetailObj.setComboPooledDataSource(null);
			}
		} else {
			knLogger.debug("connectToActiveDB()", "Connection Already with Active DB IP ADDRESS");
		}
	}

	// Start INT24865
	void postPoolDestroyPoolEvent(KnDBConnectionDetails pDbDetailsObj) {
		String methodName = "postPoolDestroyPoolEvent(KnDBConnectionDetails pDbDetailsObj)";
		KnDBEvent lEvent = new KnDBEvent();
		lEvent.mDBConnDetailObj = pDbDetailsObj;
		lEvent.mIPAddress = pDbDetailsObj.getIpAddress();
		lEvent.mDBID = pDbDetailsObj.getPttServerID();
		lEvent.mComboPoolDataSrc = pDbDetailsObj.getComboPooledDataSource();
		lEvent.mEventName = KnDBConst.DESTROY_POOL_EVT;
		try {
			knLogger.info(methodName, "lEvent", lEvent);
			postEvent(lEvent);
		} catch (Exception e) {
			knLogger.error(methodName, "Exception while posting event to destroy pool", e);
		}
	}

	boolean processPoolDestroy(KnDBEvent pEvent) {
		boolean lSuccess = false;
		if (pEvent.mEventName != KnDBConst.DESTROY_POOL_EVT || pEvent.mComboPoolDataSrc == null) {
			knLogger.info("processPoolDestroy()", "Returning!!::" + pEvent.mEventName);
			return lSuccess;
		}
		final ComboPooledDataSource lComboPool = pEvent.mComboPoolDataSrc;
		try {
			if (lComboPool.getNumBusyConnections() == 0) {
				String lThreadName = "DESTROY_DB_POOL_" + pEvent.mDBID + "_" + pEvent.mIPAddress;
				knLogger.info("processPoolDestroy()", "To destroy Pool Details::IPADDRESS" + pEvent.mIPAddress + "::PttServerID::" + pEvent.mDBID);
				knLogger.info("Connection Pool Destroyed for JDBC URL", lComboPool.getJdbcUrl(), "DSN", lComboPool.getDataSourceName());
				new Thread(lThreadName) {
					@Override
					public void run() {
						try {
							DataSources.destroy(lComboPool);
						} catch (Exception e) {
							knLogger.error("processPoolDestroy()", "Exception", e);
						}
					}
				}.start();
				lSuccess = true;
			} else {
				knLogger.info("processPoolDestroy()", "Not destroying Pool as busy connections !=0 Details::IPADDRESS" + pEvent.mIPAddress
						+ "::PttServerID::" + pEvent.mDBID);

				lSuccess = false;
				synchronized (mInvalidPool) {
					mInvalidPool.add(pEvent);
				}
			}
		} catch (Exception ex) {
			knLogger.error("processPoolDestroy()", ex);
		}
		return lSuccess;

	}

	void processPoolDestroyNotifyEvent() {
		knLogger.info("processPoolDestroyNotifyEvent()", "Inavlid pool Size:: <" + mInvalidPool.size() + ">");
		Iterator<KnDBEvent> itr = mInvalidPool.iterator();
		while (itr.hasNext()) {
			KnDBEvent lDbEvent = itr.next();
			knLogger.error("processPoolDestroyNotifyEvent()", "Inavlid pool Size:: <" + lDbEvent.mComboPoolDataSrc + ">");
			postEvent(lDbEvent);
			itr.remove();
		}
	}
}
