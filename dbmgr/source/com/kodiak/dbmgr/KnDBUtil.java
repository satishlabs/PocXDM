/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBUtil.java
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

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.kodiak.logger.KnLogger;

class KnDBUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDBUtil.class);

	public static final String COMMONCONFIGFILE = "CommonConfig.properties";
	public static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;

	/**
	 * Call this method for checking server is reachable or not
	 * 
	 * @param pHost
	 *            hostname/ipaddress
	 * @param pPort
	 *            port number where server is running
	 * @param pDelay
	 *            milliseconds to wait for connection
	 * @return true if server is reachable
	 */

	public static boolean isServerReachable(String pHost, int pPort, int pDelay) {
		knLogger.debug("isServerReachable()", "pHost=", pHost, "pPort=", pPort, "pDelay" + pDelay);
		boolean lIsReachable = false;
		Socket lSock = null;
		try {
			InetAddress inetAddr = InetAddress.getByName(pHost);
			lSock = new Socket();
			lSock.bind(null);
			lSock.connect(new InetSocketAddress(inetAddr, pPort), pDelay);
			knLogger.debug("isServerReachable()", "isServerReachable()", "Server:" + pHost + "::is Reachable");
			lIsReachable = true;
		} catch (Exception ex) {
			knLogger.error("isServerReachable()", "Exception::", ex);
			lIsReachable = false;
		} finally {
			if (lSock != null) {
				try {
					lSock.close();
				} catch (Exception le) {
					knLogger.error("isServerReachable()", "Exception", le);
				}
			}
		}
		return lIsReachable;
	}

	// build a DB URL from KnDBProps Data
	public static String buildDBUrl() {
		String lMethName = "buildDBUrl()";
		knLogger.entry(lMethName);
		StringBuilder databaseUrl = new StringBuilder();
		KnDBProps mLocalDBProps = KnDBProps.getInstance();
		String dBType = mLocalDBProps.getConnectionType();
		switch (dBType) {
		case KnDBConst.LOCAL_TYPE:
			databaseUrl.append(KnDBConst.LOCAL_CONN_DRIVER_URL).append(KnDBConst.LOCALE_DSN).append(KnDBConst.EQUAL).append(mLocalDBProps.getDbDsn())
					.append(KnDBConst.COLON).append(KnDBConst.USER_ID).append(KnDBConst.EQUAL).append(mLocalDBProps.getUserID())
					.append(KnDBConst.COLON).append(KnDBConst.PASSWORD).append(KnDBConst.EQUAL).append(mLocalDBProps.getPassword());
			break;
		case KnDBConst.REMOTE_TYPE:
			databaseUrl.append(KnDBConst.REMOTE_CONN_DRIVER_URL).append(KnDBConst.TTC_SERVER).append(KnDBConst.EQUAL)
					.append(mLocalDBProps.getIpAddress()).append(KnDBConst.COLON).append(KnDBConst.TTC_SERVER_DSN).append(KnDBConst.EQUAL)
					.append(mLocalDBProps.getDbDsn()).append(KnDBConst.COLON).append(KnDBConst.USER_ID).append(KnDBConst.EQUAL)
					.append(mLocalDBProps.getUserID()).append(KnDBConst.COLON).append(KnDBConst.PASSWORD).append(KnDBConst.EQUAL)
					.append(mLocalDBProps.getPassword()).append(KnDBConst.COLON).append(KnDBConst.TCPPORT).append(KnDBConst.EQUAL)
					.append(mLocalDBProps.getTcpPort()).append(KnDBConst.COLON).append(KnDBConst.TTC_TIMEOUT).append(KnDBConst.EQUAL)
					.append(KnDBConst.TTC_TIMEOUT_VALUE);
		}
		//knLogger.debug(lMethName, "Exit,Datebase URL=" + databaseUrl);
		knLogger.debug(lMethName, "Exit");
		return databaseUrl.toString();
	}

	public static Connection getLocalDBConenction() throws SQLException {
		KnDBProps dBProps = KnDBProps.getInstance();
		return KnTimedConnection.getConnection(buildDBUrl(), dBProps.getUserID(), dBProps.getPassword(), 8000);
	}

	/**
	 * private method to sequentially close data base resource
	 * 
	 * @param resultSet
	 * @param statement
	 * @param preparedStatement
	 * @param connection
	 */
	public static void closeResource(ResultSet resultSet, Statement statement, PreparedStatement preparedStatement, Connection connection) {
		String methodName = "closeResource(ResultSet resultSet, Statement statement, PreparedStatement preparedStatement, Connection connection)";
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				knLogger.error(methodName, "SQLException" , e);
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				knLogger.error(methodName, "SQLException" , e);
			}
		}

		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				knLogger.error(methodName, "SQLException" , e);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				knLogger.info("closeResource", "SQLException" , e);
			}
		}

	}

	public static int executeTTIsqlCS(String ipAddress, String dsn, String userId, String password, String timeout) {
		StringBuilder runCmd = new StringBuilder(KnDBConst.TTI_SQLCS_SCRIPT_PATH).append(KnDBConst.TTI_SQLCS_SCRIPT).append(KnDBConst.SPACE)
				.append(ipAddress);
		runCmd.append(KnDBConst.SPACE).append(dsn);
		runCmd.append(KnDBConst.SPACE).append(userId);
		runCmd.append(KnDBConst.SPACE).append(password);
		runCmd.append(KnDBConst.SPACE).append(timeout);
		//setting tt ssl flag values
		ttSSLFlags(runCmd);
		return runCommand(runCmd.toString());
	}

	private static void ttSSLFlags(StringBuilder runCmd) {
		final String methodName = "ttSSLFlags()";
		knLogger.entry(methodName, "ssl_flag");
		FileInputStream fileInputStream = null;
		Properties commonConfigProps = new Properties();
		try {
			fileInputStream = new FileInputStream(commonConfigFile);
			commonConfigProps.load(fileInputStream);
			String ssl_flag = commonConfigProps.getProperty("IS_TIMESTEN_SSL_ENABLED");
			String wallet = commonConfigProps.getProperty("TIMESTEN_SSL_CLIENTWALLET");
			String encryption = commonConfigProps.getProperty("TIMESTEN_SSL_ENCRYPTIONTYPE");
			String cipherSuites = commonConfigProps.getProperty("TIMESTEN_SSL_CIPHERSUITES");
			String clientAuthentication = commonConfigProps.getProperty("TIMESTEN_SSL_CLIENTAUTH");
			runCmd.append(KnDBConst.SPACE).append(wallet);
			runCmd.append(KnDBConst.SPACE).append(encryption);
			runCmd.append(KnDBConst.SPACE).append(cipherSuites);
			runCmd.append(KnDBConst.SPACE).append(clientAuthentication);
			runCmd.append(KnDBConst.SPACE).append(ssl_flag);
		} catch (Exception e) {
			knLogger.error(methodName, " Error occurred while reading the file", e);
		}
	}

	public static int runCommand(String command) {
		String methodName = "runCommand()";
		knLogger.info(methodName, "Command : " + command);
		int status = -1;
		try {
			status = executeCommand(command);
		} catch (SecurityException exp) {
			knLogger.debug(methodName, "SecurityException", exp);
		} catch (IOException exp) {
			knLogger.debug(methodName, "IOException", exp);
		} catch (IllegalArgumentException exp) {
			knLogger.debug(methodName, "IllegalArgumentException", exp);
		} catch (Exception e) {
			knLogger.debug(methodName, "Exception", e);
		}
		return status;
	}

	private static int executeCommand(String cmdName) throws Exception {
		String mname = "executeCommand";
		OutputStream os = null;
		InputStream is = null;
		InputStream es = null;
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(cmdName);
			os = p.getOutputStream();
			is = p.getInputStream();
			es = p.getErrorStream();
			p.waitFor();
			int exitStatus = p.exitValue();
			knLogger.info(mname, "EXIT STATUS FOR COMMAND " + cmdName + " IS " + exitStatus);
			knLogger.info(mname, "SCRIPT OUTPUT :" + printStream(es));
			if (exitStatus != 0) {
				throw new Exception("Improper termination of script : Error in executing script :" + cmdName);
			}
			return exitStatus;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (es != null) {
					es.close();
				}
			} catch (IOException e) {
				knLogger.error(mname, "Exception", e);
			}
		}
	}

	private static String printStream(InputStream is) {
		try {
			int available = is.available();
			byte[] contents = new byte[available];
			return new String(contents);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String buildDBUrl(KnDBConnectionDetails pConnectionDetails) {
		String lMethName = "buildDBUrl(KnDBConnectionDetails pConnectionDetails)";
		knLogger.info(lMethName, "Entry,pConnectionDetails=" + pConnectionDetails);
		StringBuilder databaseUrl = new StringBuilder();
		String dBType = pConnectionDetails.getDbConnectiontype();
		switch (dBType) {
		case KnDBConst.LOCAL_TYPE:
			databaseUrl.append(KnDBConst.LOCAL_CONN_DRIVER_URL).append(KnDBConst.LOCALE_DSN).append(KnDBConst.EQUAL)
					.append(pConnectionDetails.getDbConnectionString()).append(KnDBConst.COLON).append(KnDBConst.USER_ID).append(KnDBConst.EQUAL)
					.append(pConnectionDetails.getUserId()).append(KnDBConst.COLON).append(KnDBConst.PASSWORD).append(KnDBConst.EQUAL)
					.append(pConnectionDetails.getPassword());
			break;
		case KnDBConst.REMOTE_TYPE:
			databaseUrl.append(KnDBConst.REMOTE_CONN_DRIVER_URL).append(KnDBConst.TTC_SERVER).append(KnDBConst.EQUAL)
					.append(pConnectionDetails.getIpAddress()).append(KnDBConst.COLON).append(KnDBConst.TTC_SERVER_DSN).append(KnDBConst.EQUAL)
					.append(pConnectionDetails.getDbConnectionString()).append(KnDBConst.COLON).append(KnDBConst.USER_ID).append(KnDBConst.EQUAL)
					.append(pConnectionDetails.getUserId()).append(KnDBConst.COLON).append(KnDBConst.PASSWORD).append(KnDBConst.EQUAL)
					.append(pConnectionDetails.getPassword()).append(KnDBConst.COLON).append(KnDBConst.TCPPORT).append(KnDBConst.EQUAL)
					.append(KnDBProps.getInstance().getTcpPort()).append(KnDBConst.COLON).append(KnDBConst.TTC_TIMEOUT).append(KnDBConst.EQUAL)
					.append(KnDBProps.getInstance().getTtcTimeout());
		}
		//knLogger.info(lMethName, "Exit,Datebase URL=" + databaseUrl);
		knLogger.info(lMethName, "Exit");
		return databaseUrl.toString();
	}
}
