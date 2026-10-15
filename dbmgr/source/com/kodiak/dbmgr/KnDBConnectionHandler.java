/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBConnectionHandler.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import com.kodiak.logger.KnLogger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

class KnDBConnectionHandler {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDBConnectionHandler.class);

	private Properties mC3p0Props = new Properties();
	final public HashMap<String, KnDBConnectionDetails> dBConnectionDetailsMap = new HashMap<String, KnDBConnectionDetails>();
	private KnDBProps mLocalDBProps = null;

	KnDBConnectionHandler() {
		mLocalDBProps = KnDBProps.getInstance();
	}

	public KnSqlConnection getConnection(String pDBID, boolean pReadOnly) throws Exception {
		String lMethodName = "getConnection(String pDBID,boolean)";
		KnSqlConnection lKnSqlConnection = null;
		knLogger.debug(lMethodName, "Entered Get Connection for PttserverID", pDBID);

		if (dBConnectionDetailsMap.containsKey(pDBID)) {
			lKnSqlConnection = getConnectionFromPool(pDBID, pReadOnly);
		} else {
			synchronized (dBConnectionDetailsMap) {
				if (dBConnectionDetailsMap.containsKey(pDBID)) {
					lKnSqlConnection = getConnectionFromPool(pDBID, pReadOnly);
				} else {
					KnDBConnectionDetails lDbConnDetailObj;
					String lPingableIpAddress;
					knLogger.info(lMethodName, "Creating the connectiondetails Object", pDBID);
					if (KnEMSInfo.getInstance().getDsn().equals(pDBID)) {
						lDbConnDetailObj = createConnDetailObjFromEMSInfo();
						knLogger.info(lMethodName, "Connection Created!! for", pDBID);
					} else {
						lDbConnDetailObj = createConnectionDetailObject(pDBID);
					}
					lPingableIpAddress = findPingableDBIpAddr(lDbConnDetailObj.getPttServerID(), lDbConnDetailObj);
					if (lPingableIpAddress != null) {
						lDbConnDetailObj.setIpAddress(lPingableIpAddress);
						createConnectionPool(lDbConnDetailObj.getPttServerID(), lDbConnDetailObj);
						dBConnectionDetailsMap.put(pDBID, lDbConnDetailObj);
						lKnSqlConnection = getConnectionFromPool(pDBID, pReadOnly);
					}
				}
			}
		}
		knLogger.info(lMethodName, "Leaving Get Connection for PttserverID", pDBID);
		return lKnSqlConnection;
	}

	private KnSqlConnection getConnectionFromPool(String pDBID, boolean pReadOnly) throws Exception {
		String lMethodName = "getConnectionFromPool(String pDBID,boolean)";
		KnSqlConnection lKnSqlConnection = null;
		KnDBConnectionDetails lDbConnDetailObj = dBConnectionDetailsMap.get(pDBID);
		ComboPooledDataSource lDataSource = lDbConnDetailObj.getComboPooledDataSource();
		if (lDataSource != null) {
			try {
				lKnSqlConnection = new KnSqlConnection(new KnTimedConnection().getConnection(lDataSource, 15000, pReadOnly), lDbConnDetailObj,
						pReadOnly);
			} catch (SQLException e) {
				knLogger.warn(lMethodName, "Busy Connection",lDataSource.getNumBusyConnections(),"Idle Connection",lDataSource.getNumIdleConnections());
				knLogger.error(lMethodName, "Exception while getting connection to " , pDBID, e);
				throw new Exception("Server is busy. Please try later.");
			}
		} else {
			synchronized (dBConnectionDetailsMap) {
				if (lDbConnDetailObj.getComboPooledDataSource() == null) {
					knLogger.debug(lMethodName, "Creating Data Source as it is null Becausre of prev failures");
					String lPingableIpAddress = findPingableDBIpAddr(lDbConnDetailObj.getPttServerID(), lDbConnDetailObj);
					if (lPingableIpAddress != null) {
						lDbConnDetailObj.setIpAddress(lPingableIpAddress);
						createConnectionPool(lDbConnDetailObj.getPttServerID(), lDbConnDetailObj);
						lDataSource = lDbConnDetailObj.getComboPooledDataSource();
						lKnSqlConnection = new KnSqlConnection(new KnTimedConnection().getConnection(lDataSource, 15000, pReadOnly), lDbConnDetailObj, pReadOnly);
					}
				}
			}
		}
		return lKnSqlConnection;
	}

	public String findPingableDBIpAddr(String pDbID, KnDBConnectionDetails pDbConnDetailObj) throws Exception {
		return findDBIpAddr(pDbID, null, pDbConnDetailObj, false);
	}

	private String findDBIpAddr(String pDbID, String pFailedIP, KnDBConnectionDetails pDbConnDetailObj, boolean pIsAlternateIp) throws Exception {
		String lMethName = "findDBIpAddr(DbID,FailedIP, DbConnDetailObj)";
		String lFinalIPAddr = null;
		String lDbUrl = null;
		Connection lDbConn = null;
		PreparedStatement lPrepStatement = null;
		ResultSet lResultSet = null;
		String lAlternateIp = null;
		String lActiveIP = null;
		// ArrayList lIPArrayList = new ArrayList();

		knLogger.debug(lMethName, "Entered::");

		if (pIsAlternateIp && pFailedIP == null) {
			knLogger.debug(lMethName, "Failed IP address Cann't be NULL in case pIsAlternateIp flag is TRUE::::: ");
			return null;
		}

		lDbUrl = KnDBUtil.buildDBUrl();

		if (KnEMSInfo.getInstance().getDsn().equalsIgnoreCase(pDbID)) {
			// changed to EMS DSN
			// instead of EMS DB key

			if ((mLocalDBProps.getDbDsn()).equalsIgnoreCase(pDbID)) {
				if (!(getSocketStatus(pDbConnDetailObj.getIpAddress(), pDbConnDetailObj.getDbPort()) && (KnDBUtil.executeTTIsqlCS(
						pDbConnDetailObj.getIpAddress(), pDbConnDetailObj.getDbConnectionString(), pDbConnDetailObj.getUserId(),
						pDbConnDetailObj.getPassword(), KnDBConst.TTC_TIMEOUT_VALUE) == 0))) {
					// if local EMS DB is not pingable return null
					knLogger.error(lMethName, pDbID
							, ":Connection returned was found to be null. Returning without creating connection pool data Source.");
					return null;
				}
				return pDbConnDetailObj.getIpAddress();
			}

			try {
				knLogger.debug(lMethName, "AN EMS CONNECTION");
				String lDbQuery = null;
				// how to get priority of ip addresses in this table.
				// In below query If pFailedIP==null it will return all the
				// records from the table and this what we need in case
				// pIsAlternateIp=false.
				if (pIsAlternateIp) {
					lDbQuery = "SELECT IPADDRESS,ISACTIVE FROM DG.EMSINFO where IPADDRESS<>'" + pFailedIP + "'";
				} else {
					lDbQuery = "SELECT IPADDRESS,ISACTIVE FROM DG.EMSINFO";
				}
				lDbConn = KnTimedConnection.getConnection(lDbUrl, mLocalDBProps.getUserID(), mLocalDBProps.getPassword(), 8000);
				lPrepStatement = lDbConn.prepareStatement(lDbQuery);
				lResultSet = lPrepStatement.executeQuery();
				while (lResultSet.next()) {
					lAlternateIp = lResultSet.getString("IPADDRESS");
					knLogger.debug(lMethName, "EMS CONNECTION - Connecting to EMS IP::" , lAlternateIp);

					if (!(getSocketStatus(lAlternateIp, pDbConnDetailObj.getDbPort()) && (KnDBUtil.executeTTIsqlCS(lAlternateIp,
							pDbConnDetailObj.getDbConnectionString(), pDbConnDetailObj.getUserId(), pDbConnDetailObj.getPassword(),
							KnDBConst.TTC_TIMEOUT_VALUE) == 0))) {
						knLogger.error(lMethName, ":Connection returned was found to be null. Returning without creating data Source.");
					} else {
						lFinalIPAddr = lAlternateIp;
						if (lResultSet.getString("ISACTIVE").equalsIgnoreCase("Y")) {
							// no need to check further the IP that is fetched
							// now is active and also reachable
							break;
						}
					}
				}
			} catch (Exception ex) {
				knLogger.error(lMethName, "Handling EMS Connection:", ex);
			} finally {
				closeResultSet(lResultSet);
				closeStatement(lPrepStatement);
				closeConnection(lDbConn);
			}
			knLogger.debug(lMethName, "AN EMS CONNECTION - Connection to EMS IP::" , lFinalIPAddr);

		} else { // PTTServer
			lActiveIP = null;
			lResultSet = null;
			lPrepStatement = null;
			knLogger.debug(lMethName, "A PTTServer CONNECTION::" + pDbID);
			if ((mLocalDBProps.getDbDsn()).contains(pDbID)) {
				if (!(getSocketStatus(pDbConnDetailObj.getIpAddress(), pDbConnDetailObj.getDbPort()) && (KnDBUtil.executeTTIsqlCS(
						pDbConnDetailObj.getIpAddress(), pDbConnDetailObj.getDbConnectionString(), pDbConnDetailObj.getUserId(),
						pDbConnDetailObj.getPassword(), KnDBConst.TTC_TIMEOUT_VALUE) == 0))) {

					knLogger.error(lMethName, pDbID , ":Local DB Connection Failed. Returning without creating connection pool data Source.");
					return null;
				}
				knLogger.debug(lMethName, "Return Local IP its local Connection::" , pDbID);
				return pDbConnDetailObj.getIpAddress();
			}

			try {
				lDbConn = KnTimedConnection.getConnection(lDbUrl, mLocalDBProps.getUserID(), mLocalDBProps.getPassword(), 8000);
				String lQueryActiveIP = "SELECT IPADDRESS FROM DG.PTTSERVERINFO WHERE PTTSERVERID='" + pDbID + "'";
				lPrepStatement = lDbConn.prepareStatement(lQueryActiveIP);
				lResultSet = lPrepStatement.executeQuery();
				if (lResultSet.next()) {
					lActiveIP = lResultSet.getString("IPADDRESS");
				}
				knLogger.debug(lMethName, "A PTTServer CONNECTION::" , lActiveIP);

				if ((pFailedIP != null) && (!pFailedIP.equalsIgnoreCase(lActiveIP))) {
					if (!(getSocketStatus(lActiveIP, pDbConnDetailObj.getDbPort()) && pIsAlternateIp)
							&& (KnDBUtil.executeTTIsqlCS(lActiveIP, pDbConnDetailObj.getDbConnectionString(), pDbConnDetailObj.getUserId(),
							pDbConnDetailObj.getPassword(), KnDBConst.TTC_TIMEOUT_VALUE) == 0)) {
						knLogger.error(lMethName, ":ACtive is not pingable for this DB ID.");
					} else {
						lFinalIPAddr = lActiveIP;
					}
				} else if (!pIsAlternateIp) {
					if (!(getSocketStatus(lActiveIP, pDbConnDetailObj.getDbPort()) && (KnDBUtil.executeTTIsqlCS(lActiveIP,
							pDbConnDetailObj.getDbConnectionString(), pDbConnDetailObj.getUserId(), pDbConnDetailObj.getPassword(),
							KnDBConst.TTC_TIMEOUT_VALUE) == 0))) {
						knLogger.error(lMethName, ":ACtive is not pingable for this DB ID.");
					} else {
						lFinalIPAddr = lActiveIP;
					}
				}
				closeResultSet(lResultSet);
				closeStatement(lPrepStatement);
				closeConnection(lDbConn);
				knLogger.debug(lMethName, "Active IP Value if pingabble::" , lFinalIPAddr);

				if (lFinalIPAddr == null) {
					knLogger.error(lMethName, "Active has failed trying other IP's");
					String lQueryIP = null;
					if (pIsAlternateIp) {
						lQueryIP = "SELECT S1.IPADDRESS FROM DG.SIGNALINGCARDINFO S1, DG. SIGNALINGCARDADDLINFO S2, DG.PTTSERVERINFO P, DG.JOINTSIGNALINGCARDINFO J WHERE S1.SIGNALINGCARDID=J.SIGNALINGCARDID AND S1.SIGNALINGCARDID=S2.SIGNALINGCARDID AND P.PTTSERVERID=J.PTTSERVERID AND J.PTTSERVERID='"
								+ pDbID + "' AND S1.IPADDRESS<>'" + pFailedIP + "' AND S1.IPADDRESS<>'" + lActiveIP + "'ORDER BY S2. PRIORITY desc;";
					} else {
						lQueryIP = "SELECT S1.IPADDRESS FROM DG.SIGNALINGCARDINFO S1, DG. SIGNALINGCARDADDLINFO S2, DG.PTTSERVERINFO P, DG.JOINTSIGNALINGCARDINFO J WHERE S1.SIGNALINGCARDID=J.SIGNALINGCARDID AND S1.SIGNALINGCARDID=S2.SIGNALINGCARDID AND P.PTTSERVERID=J.PTTSERVERID AND J.PTTSERVERID='"
								+ pDbID + "' AND S1.IPADDRESS<>'" + lActiveIP + "'ORDER BY S2. PRIORITY desc;";
					}
					lDbConn = KnTimedConnection.getConnection(lDbUrl, mLocalDBProps.getUserID(), mLocalDBProps.getPassword(), 8000);
					lPrepStatement = lDbConn.prepareStatement(lQueryIP);
					lResultSet = lPrepStatement.executeQuery();
					lAlternateIp = null;
					while (lResultSet.next()) {
						// first record is the highest priority as this is
						// shorted as per the priority
						lAlternateIp = lResultSet.getString("IPADDRESS");
						knLogger.error(lMethName, "IP Fetched Now is::" , lAlternateIp);
						if (!(getSocketStatus(lAlternateIp, pDbConnDetailObj.getDbPort()) && (KnDBUtil.executeTTIsqlCS(lAlternateIp,
								pDbConnDetailObj.getDbConnectionString(), pDbConnDetailObj.getUserId(), pDbConnDetailObj.getPassword(),
								KnDBConst.TTC_TIMEOUT_VALUE) == 0))) {
							knLogger.error(lMethName, "Socket Status or executeTTIsqlCS failed for IP" , lAlternateIp);
							continue;
						} else {
							lFinalIPAddr = lAlternateIp;
							break;
						}
					}
					closeResultSet(lResultSet);
					closeStatement(lPrepStatement);
					closeConnection(lDbConn);

				}
			} catch (Exception ex) {
				knLogger.error(lMethName, "Exception::", ex);
			}
		}

		return lFinalIPAddr;
	}

	public KnDBConnectionDetails createConnDetailObjFromEMSInfo() {
		String lMethName = "createConnDetailObjFromEMSInfo()";
		knLogger.debug(lMethName, "ENTRY");
		KnEMSInfo emsInfo = KnEMSInfo.getInstance();
		KnDBConnectionDetails lDBConnDetailObject = new KnDBConnectionDetails();
		lDBConnDetailObject.setIpAddress(emsInfo.getIpAddress());
		lDBConnDetailObject.setDbConnectiontype(KnDBConst.REMOTE_TYPE);
		lDBConnDetailObject.setDbConnectionString(emsInfo.getDsn());
		lDBConnDetailObject.setPttServerID(emsInfo.getDsn());
		lDBConnDetailObject.setDbPort(emsInfo.getPort());
		lDBConnDetailObject.setUserId(emsInfo.getUserId());
		lDBConnDetailObject.setPassword(emsInfo.getPassword());
		//knLogger.debug(lMethName, "EXIT", "DbCOnnectionObject", lDBConnDetailObject);
		knLogger.debug(lMethName, "EXIT");
		return lDBConnDetailObject;
	}

	public KnDBConnectionDetails createConnectionDetailObject(String pPttServerID) {
		String lMethName = "createConnectionDetailObject(pttServerID)";
		knLogger.entry(lMethName, "pttServerID", pPttServerID);
		KnDBConnectionDetails lDbConnObject = null;
		String dsnName = "DG_" + pPttServerID;
		pPttServerID = pPttServerID.substring(0, 6);
		try {
			KnDBManagerDAO dbManagerDAO = KnDBManagerDAO.getInstance();
			KnConnectionDetailDTO connectionDetailDTO = dbManagerDAO.getPttServerConnDetail(pPttServerID);
			if (connectionDetailDTO != null) {
				lDbConnObject = new KnDBConnectionDetails();
				lDbConnObject.setPttServerID(pPttServerID);
				lDbConnObject.setIpAddress(connectionDetailDTO.getIpAddress());
				lDbConnObject.setUserId(connectionDetailDTO.getUserName());
				lDbConnObject.setPassword(connectionDetailDTO.getPassword());
				lDbConnObject.setDbConnectionString(dsnName);
				if (lDbConnObject.getDbConnectionString().startsWith(mLocalDBProps.getDbDsn())
						&& KnDBConst.LOCAL_TYPE.equalsIgnoreCase(mLocalDBProps.getConnectionType())) {
					lDbConnObject.setDbConnectiontype(KnDBConst.LOCAL_TYPE);
				} else {
					lDbConnObject.setDbConnectiontype(KnDBConst.REMOTE_TYPE);
				}
				lDbConnObject.setDbPort(mLocalDBProps.getPttDbPort());

				if (mLocalDBProps.getDbDsn().contains(pPttServerID)) {
					knLogger.info(lMethName, "Creating connection pool with local DB for pttServerID : " , pPttServerID);
					lDbConnObject.setIpAddress(mLocalDBProps.getIpAddress());
				}
			} else {
				throw new Exception("DG.PTTSERVERINFO is not configured");
			}
		} catch (Exception e) {
			knLogger.error(lMethName, "Exception while creating connection detail object", e);
		}
		//knLogger.exit(lMethName, "lDbConnObject", lDbConnObject);
		knLogger.exit(lMethName, "EXIT");
		return lDbConnObject;
	}

	public void createConnectionPool(String pDBID, KnDBConnectionDetails pDBConnDetailObj) {
		String lMethodName = "createConnectionPool(String pDBID, KnDBConnectionDetails pDBConnDetailObj)";
		knLogger.info(lMethodName, "PttserverID : " , pDBID , "  KnDBConnectionDetails : " , pDBConnDetailObj);
		if (pDBID == null || pDBConnDetailObj == null) {
			return;
		}
		try {
			String lUserName = pDBConnDetailObj.getUserId();
			String lPassword = pDBConnDetailObj.getPassword();
			String lUrl = KnDBUtil.buildDBUrl(pDBConnDetailObj);
			//knLogger.debug(lMethodName, "Connection String:" , lUrl , "  userName:" , lUserName + ",password:" , lPassword);
			ComboPooledDataSource lPooledDataSource = new ComboPooledDataSource();
			setC3p0Properties(lPooledDataSource);
			lPooledDataSource.setDriverClass(KnDBConst.TIMESTEN_DRIVER);
			lPooledDataSource.setJdbcUrl(lUrl);
			lPooledDataSource.setUser(lUserName);
			lPooledDataSource.setPassword(lPassword);
			knLogger.debug(lMethodName, "Initial pool size", lPooledDataSource.getInitialPoolSize(), "Min Pool Size",
					lPooledDataSource.getMinPoolSize(), "Max Pool Size" , lPooledDataSource.getMaxPoolSize(),
					" MaxStatementsPerConnection" , lPooledDataSource.getMaxStatementsPerConnection());
			Thread.sleep(5000);
			knLogger.debug(lMethodName, "Initial BUSY", lPooledDataSource.getNumBusyConnections(), "Initial IDLE",
					lPooledDataSource.getNumIdleConnections());
			pDBConnDetailObj.setComboPooledDataSource(lPooledDataSource);
			knLogger.info(lMethodName, "CONNECTION POOL CREATED DSN", pDBConnDetailObj.getDbConnectionString(), "IPADDRESS",
					pDBConnDetailObj.getIpAddress());
		} catch (Exception e) {
			knLogger.error(lMethodName, "Exception while creating pool. Returning without creating data Source", e);
		}
	}

	public void closeResultSet(ResultSet pResultSet) {
		String lMethName = "closeResultSet";
		if (pResultSet != null) {
			try {
				pResultSet.close();
			} catch (Exception exp) {
				knLogger.error(lMethName, "Exception While Closing Result set", exp);
			}
		}
	}

	public void closeStatement(PreparedStatement pPrepStatement) {
		String lMethName = "closeStatement";
		if (pPrepStatement != null) {
			try {
				pPrepStatement.close();
			} catch (Exception exp) {
				knLogger.error(lMethName, "Exception While Closing Statement", exp);
			}
		}
	}

	public void closeConnection(Connection pConnection) {
		String lMethName = "closeConnection";
		if (pConnection != null) {
			try {
				pConnection.close();
			} catch (Exception exp) {
				knLogger.error(lMethName, "Exception While Closing Statement", exp);
			}
		}
	}

	public boolean getSocketStatus(String pIpAdddress, int pPort) {
		String methodName = "getSocketStatus()";
		boolean lReachable = KnDBUtil.isServerReachable(pIpAdddress, pPort, 10000);
		knLogger.info(methodName, "Socket Check Status < " , lReachable , ">" , pIpAdddress , " and Port is:" , pPort);
		return lReachable;
	}

	public void initProperties() throws IOException {
		String lActiveRelPath = System.getProperty("activeRelDir");
		try (FileInputStream lC3p0fStrteam = new FileInputStream(lActiveRelPath + File.separator + "c3p0.properties")) {
			knLogger.info("initProperties()", "Active Release Path = " , lActiveRelPath);
			mC3p0Props.load(lC3p0fStrteam);
		}
	}

	public Properties getC3p0Props() {
		return mC3p0Props;
	}

	public void setC3p0Properties(ComboPooledDataSource pDataSource) {
		Properties mC3p0Properties = getC3p0Props();
		pDataSource.setMaxStatements(Integer.parseInt(mC3p0Properties.getProperty("c3p0.maxStatements")));
		pDataSource.setMaxStatementsPerConnection(Integer.parseInt(mC3p0Properties.getProperty("c3p0.maxStatementsPerConnection", "150")));
		pDataSource.setMaxIdleTime(Integer.parseInt(mC3p0Properties.getProperty("c3p0.maxIdleTime")));
		pDataSource.setMaxIdleTimeExcessConnections(Integer.parseInt(mC3p0Properties.getProperty("c3p0.maxIdleTimeExcessConnections")));
		pDataSource.setInitialPoolSize(Integer.parseInt(mC3p0Properties.getProperty("c3p0.initialPoolSize")));
		pDataSource.setMinPoolSize(Integer.parseInt(mC3p0Properties.getProperty("c3p0.minPoolSize")));
		pDataSource.setMaxPoolSize(Integer.parseInt(mC3p0Properties.getProperty("c3p0.maxPoolSize")));
		pDataSource.setIdleConnectionTestPeriod(Integer.parseInt(mC3p0Properties.getProperty("c3p0.idleConnectionTestPeriod")));
		pDataSource.setPropertyCycle(Integer.parseInt(mC3p0Properties.getProperty("c3p0.propertyCycle")));
		pDataSource.setAcquireIncrement(Integer.parseInt(mC3p0Properties.getProperty("c3p0.acquireIncrement")));
		pDataSource.setTestConnectionOnCheckout(Boolean.parseBoolean(mC3p0Properties.getProperty("c3p0.testConnectionOnCheckout")));
		pDataSource.setAutoCommitOnClose(Boolean.parseBoolean(mC3p0Properties.getProperty("c3p0.autoCommitOnClose")));
		pDataSource.setForceIgnoreUnresolvedTransactions(Boolean.parseBoolean(mC3p0Properties.getProperty("c3p0.forceIgnoreUnresolvedTransactions")));
		pDataSource.setNumHelperThreads(Integer.parseInt(mC3p0Properties.getProperty("c3p0.numHelperThreads")));
		pDataSource.setCheckoutTimeout(Integer.parseInt(mC3p0Properties.getProperty("c3p0.checkoutTimeout")));
		pDataSource.setAcquireRetryAttempts(Integer.parseInt(mC3p0Properties.getProperty("c3p0.acquireRetryAttempts")));
		pDataSource.setAcquireRetryDelay(Integer.parseInt(mC3p0Properties.getProperty("c3p0.acquireRetryDelay")));
		pDataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(mC3p0Properties.getProperty("c3p0.breakAfterAcquireFailure")));
	}

	public HashMap<String, KnDBConnectionDetails> getdBConnectionDetailsMap() {
		return dBConnectionDetailsMap;
	}
}
