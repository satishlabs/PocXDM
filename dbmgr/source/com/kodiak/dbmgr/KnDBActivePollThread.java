/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBActivePollThread.java
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

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;

import java.util.*;

import static com.kodiak.utilities.generatealarmutil.KnAlarmConstants.*;

class KnDBActivePollThread extends TimerTask {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDBActivePollThread.class);
	private KnDBConnectionHandler dBConnectionHandler;
	private Timer timer = new Timer("KnDBActivePollThread");
	private KnDBProps dBProps;

	public KnDBActivePollThread(KnDBConnectionHandler pDBConnHandler) {
		this.dBConnectionHandler = pDBConnHandler;
		try {
			dBProps = KnDBProps.getInstance();
		} catch (Exception e) {
			knLogger.error("KnDBActivePollThread()", "Exception while getting dbprops", e);
		}
	}

	@Override
	public void run() {
		String methodName = "run()";
		knLogger.entry(methodName);
		checkActiveEMSConnection();
		checkActivePttServers();
		knLogger.exit(methodName);
	}

	public void checkActiveEMSConnection() {
		String methodName = "checkActiveEMSConnection()";
		knLogger.entry(methodName);
		KnDBConnectionDetails dBConnectionDetails;
		try {
			if (KnEMSInfo.getInstance().getDsn().equalsIgnoreCase(dBProps.getDbDsn())) {
				knLogger.debug(methodName, "Local Connection, No need to destroy connection pool");
			} else {
				KnDBManagerDAO dbManagerDAO = KnDBManagerDAO.getInstance();
				KnConnectionDetailDTO connectionDetailDTO = dbManagerDAO.getEMSConnDetail();
				dBConnectionDetails = dBConnectionHandler.dBConnectionDetailsMap.get(KnEMSInfo.getInstance().getDsn());
				if (dBConnectionDetails != null) {
					if (dBConnectionDetails.getIpAddress().equalsIgnoreCase(connectionDetailDTO.getIpAddress())) {
						knLogger.info(methodName, "Connections are with Active IP Already, CurrentIP", dBConnectionDetails.getIpAddress(),
								"Active IP", connectionDetailDTO.getIpAddress());
					} else {
						//INT107052 changed key to connectionDetailDTO.getDsn()
						if (dBConnectionHandler.getSocketStatus(connectionDetailDTO.getIpAddress(), connectionDetailDTO.getPort())
								&& KnDBUtil.executeTTIsqlCS(connectionDetailDTO.getIpAddress(), connectionDetailDTO.getDsn(),
										connectionDetailDTO.getUserName(), connectionDetailDTO.getPassword(), KnDBConst.TTC_TIMEOUT_VALUE) == 0) {
							KnDBEvent lEventObj = new KnDBEvent();
							lEventObj.mEventName = KnDBConst.CONN_ACTIVE_DB_EVT;
							lEventObj.mDBID = KnEMSInfo.getInstance().getDsn();
							lEventObj.mIPAddress = connectionDetailDTO.getIpAddress();
							lEventObj.mDBConnDetailObj = dBConnectionDetails;
							KnDBManager.getDBManagerInstance().postEvent(lEventObj);
							knLogger.debug(methodName, "Posting Event to destroy pool","lEventObj", lEventObj);
						} else {
							knLogger.error(methodName, "Active DB is not pingable.");
							throw new Exception("Active DB is not pingable");
						}
					}
				}
			}
			clearAlarm();
		} catch (Exception ex) {
			knLogger.error(methodName, "Switch Over Case will not work properly", ex);
			generateAlarm();
		}
		knLogger.exit(methodName);
	}

	public void checkActivePttServers() {
		String methodName = "checkActivePttServers()";
		knLogger.entry(methodName);
		String pttServerId;
		try {
			KnDBManagerDAO dbManagerDAO = KnDBManagerDAO.getInstance();
			List<KnConnectionDetailDTO> connectionDetailDTOs = dbManagerDAO.getPttServerConnDetails();
			for (KnConnectionDetailDTO connectionDetailDTO : connectionDetailDTOs) {
				pttServerId = connectionDetailDTO.getPttserverId();
				knLogger.debug(methodName, "Audit Thread checking pttServerId", pttServerId);

				if (dBProps.getDbDsn().contains(pttServerId)) {
					knLogger.debug(methodName, "Local Connection, No need to destroy connection pool");
				} else {
					HashMap<String, KnDBConnectionDetails> dBConnectionDetailsMap = dBConnectionHandler.dBConnectionDetailsMap;
					Set<String> keys = dBConnectionDetailsMap.keySet();
					for (String key : keys) {
						if (key.contains(pttServerId)) {
							KnDBConnectionDetails lDBConnDetailObj = dBConnectionDetailsMap.get(key);
							if (lDBConnDetailObj != null) {
								if (lDBConnDetailObj.getIpAddress().equalsIgnoreCase(connectionDetailDTO.getIpAddress())) {
									knLogger.debug(methodName, "Connection Already with Active DB IP ADDRESS", lDBConnDetailObj);
								} else {
									if (dBConnectionHandler.getSocketStatus(connectionDetailDTO.getIpAddress(), dBProps.getPttDbPort())
											&& KnDBUtil.executeTTIsqlCS(connectionDetailDTO.getIpAddress(), connectionDetailDTO.getDsn(), connectionDetailDTO.getUserName(),
													connectionDetailDTO.getPassword(), KnDBConst.TTC_TIMEOUT_VALUE) == 0) {
										KnDBEvent lEventObj = new KnDBEvent();
										lEventObj.mDBConnDetailObj = lDBConnDetailObj;
										lEventObj.mEventName = KnDBConst.CONN_ACTIVE_DB_EVT;
										lEventObj.mDBID = key;
										lEventObj.mIPAddress = connectionDetailDTO.getIpAddress();
										KnDBManager.getDBManagerInstance().postEvent(lEventObj);
										knLogger.debug(methodName, "Posting Event to destroy pool","lEventObj", lEventObj);
									} else {
										knLogger.error(methodName, "Active IP Not Pingabble.");
									}
								}
							}
						}
					}
				}
			}
            KnAlarmGeneratorUtil.generateAlarmRest(UNABLE_TO_GET_CONNECTION_FROM_TT, SEVERITY_CLEAR, XDMMANAGEDOBJECT_CLASSTYPE, dBProps.getIpAddress());
		} catch (Exception ex) {
			knLogger.error(methodName, "Switch Over Case will not work properly", ex);
            KnAlarmGeneratorUtil.generateAlarmRest(UNABLE_TO_GET_CONNECTION_FROM_TT, SEVERITY_CRITICAL, XDMMANAGEDOBJECT_CLASSTYPE, dBProps.getIpAddress());
		}
		knLogger.exit(methodName);
	}

	public void startAudit(long interval) {
		knLogger.debug("startAudit()", "interval", interval);
		Date lCurrentTime = new Date(System.currentTimeMillis());
		timer.schedule(this, lCurrentTime, interval);
	}

	private static void clearAlarm() {
		String methodName = "clearAlarm()";
		KnEMSInfo emsInfo = KnEMSInfo.getInstance();
		String serviceName = KnDBProps.getInstance().getIpAddress() + ":" + emsInfo.getIpAddress();
		knLogger.warn(methodName, " serviceName :",serviceName,"Alarm Id :",KnAlarmConstants.UNABLE_TO_CONNECT_EMS);
		KnAlarmGeneratorUtil.generateAlarm2(KnAlarmConstants.UNABLE_TO_CONNECT_EMS, KnAlarmConstants.SEVERITY_CLEAR, serviceName);
	}

	private void generateAlarm() {
		String methodName = "generateAlarm()";
		KnEMSInfo emsInfo = KnEMSInfo.getInstance();
		String serviceName = KnDBProps.getInstance().getIpAddress() + ":" + emsInfo.getIpAddress();
		knLogger.warn(methodName, " serviceName :",serviceName,"Alarm Id :",KnAlarmConstants.UNABLE_TO_CONNECT_EMS);
		KnAlarmGeneratorUtil.generateAlarm2(KnAlarmConstants.UNABLE_TO_CONNECT_EMS, KnAlarmConstants.SEVERITY_CRITICAL,serviceName);
	}
}