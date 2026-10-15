/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBConst.java
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

public class KnDBConst {
	public static final String COLON = ";";
	public static final String EQUAL = "=";
	public static final String SPACE = " ";
	public static final String EMS_CONNECTION_DETAIL_SQL = "SELECT IPADDRESS,DBDATASOURCENAME,DBUID,EN_DBPWD,TT_REMOTE_PORT FROM DG.EMSINFO where ISACTIVE='Y'";
	public static final String PTTSERVER_CONNECTION_DETAILS_SQL = "SELECT PTTSERVERID,IPADDRESS,TTDATASOURCENAME,TTUID,EN_TTPWD FROM DG.PTTSERVERINFO WHERE DGNODETYPE = 1 AND (EMSID=0 OR EMSID IS NULL)";
	public static final String PTTSERVER_CONNECTION_DETAIL_SQL = "SELECT PTTSERVERID,IPADDRESS,TTDATASOURCENAME,TTUID,EN_TTPWD FROM DG.PTTSERVERINFO WHERE PTTSERVERID = ?";
	public static final String REMOTE_CONN_DRIVER_URL = "jdbc:timesten:client:";
	public static final String LOCAL_CONN_DRIVER_URL = "jdbc:timesten:direct:";
	public static final String TTC_SERVER = "TTC_Server";
	public static final String TTC_SERVER_DSN = "TTC_Server_DSN";
	public static final String LOCALE_DSN = "dsn";
	public static final String USER_ID = "uid";
	public static final String PASSWORD = "pwd";
	public static final String TTC_TIMEOUT = "TTC_Timeout";
	public static final String TCPPORT = "TCP_PORT";
	public static final String TIMESTEN_DRIVER = "com.timesten.jdbc.TimesTenDriver";
	public static final String TTC_TIMEOUT_VALUE = "20";
	public static final String LOCAL_TYPE = "Local";
	public static final String REMOTE_TYPE = "Remote";
	public static final String DB_PROPS_FILE_NAME = "dbmgr.props";
	public static final String TTI_SQLCS_SCRIPT_PATH = System.getenv("TTISQLCS_PATH") + File.separator;
	public static final String TTI_SQLCS_SCRIPT = "runTTIsqlCS.sh ";
	public static final int CONN_ACTIVE_DB_EVT = 1;
	public static final int DESTROY_POOL_EVT = 3;
	public static final int DESTROY_POOL_NOTIFY_EVT = 4;
	public static final String ACTIVE_REL_DIR_ENV_KEY = "activeRelDir";
	public static final int USERPROFILEINDEX = 0;
	public static final String T10_USER_NAME_PATH = "U/CMN/TTAP/U";
	public static final String T10_PASSWORD_NAME_PATH = "P/CMN/TTAP/P";
	public static final String DBMGR_DBUSERID = "U";
	public static final String DBMGR_DBPASSWORD = "P";

	public static enum DataStores {
		CONNECTEDSESSIONDATA(1), CLIENTLESSSUBSCRLS(2), CLSUBSCRNAMESTORE(3), CLIENTLESSSUBSCRPVTDATA(4), GCCLIENTREGISTRY(5), XDM_SHARED_DATA(6);
		int val;

		DataStores(int va) {
			val = va;
		}

		public int getValue() {
			return val;
		}
	}
	
	/**
	 * Used by application, Need to remove in future
	 * @return
	 */
	public static String getEmsDBKey(){
		return "EMSDSN";
	}
	
	public static final String SALT = "X@#5";
	public static final String SECRETKEY = "A1B8D95BA9EA5440F252C3";
	public static final String IVSTRING = "0000000000000000";
	public static final Integer KEYSIZE=256;
	public static final Integer ITERATION=1000;
}
