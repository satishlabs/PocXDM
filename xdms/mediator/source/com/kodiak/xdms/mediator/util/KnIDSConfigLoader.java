/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.util;

import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.mediator.KnMediatorConstants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class KnIDSConfigLoader {
	private static final KnLogger knLogger = KnLogger.getLogger(KnIDSConfigLoader.class);

    private static final String CLASSNAME = KnIDSConfigLoader.class.getName();
    private static boolean isInitialized = false;

    private static KnIDSConfigLoader instance;
    private static Map<String, String> configMap = null;

    private KnIDSConfigLoader() {
    }

    public static synchronized KnIDSConfigLoader getInstance() {
        String methodName = "getInstance()";
        if (!isInitialized) {
            knLogger.info( methodName, "Entry .. ");
            instance = new KnIDSConfigLoader();
            isInitialized = true;

        }
        return instance;
    }

    /**
     * this method retruns the ParmaValue from the RTXENV table and stores in the config MAP.
     */
    public static Map<String, String> retreiveIDSConfig() {
        String methodName = "retreiveIDSConfig()";
        knLogger.debug( methodName, "ENTRY: retrieve Notification ");

        if (configMap != null) {
            knLogger.info( methodName, "Retrieving cached map - " + configMap);
            return configMap;
        }

        String pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();

        Connection conn = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        KnPersisterTxn persisterTxn = null;
        Map<String, String> configMap = null;
        try {
            knLogger.debug( methodName, "Opening the Transaction");
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();

            StringBuffer sqlQuery = new StringBuffer("SELECT PARAMNAME, PARAMVALUE FROM DG.RTXENVVARIABLEINFO WHERE PTTSERVERID = '" +
                    pttServerId + "' AND PARAMNAME IN ('");
            sqlQuery.append(KnMediatorConstants.IDS_3PP_DEFAULT_NTFY_URL).append("', '").
                    append(KnMediatorConstants.IDS_IMEI_CHANGE_NTFY_SMS).append("', '").
                    append(KnMediatorConstants.IDS_CREATE_HS_SUBSCR_NTFY_SMS).append("', '").
                    append(KnMediatorConstants.IDS_CREATE_HS_SUBSCR_NTFY_SMS_PTTRADIO).append("', '").
                    append(KnMediatorConstants.OPSCLI_PAM_NTFY_URL).append("', '").
                    append(KnMediatorConstants.IDS_IMEI_CHANGE_NTFY_SMS_PTTRADIO).append("', '").
                    append(KnMediatorConstants.UPDATEBAN_MDN);
            sqlQuery.append("')");

            String query = sqlQuery.toString();

            conn = persisterTxn.getDBConnection(pttServerId, true);
            stmt = conn.createStatement();

            knLogger.debug( methodName, "Executing the query - " + query);
            resultSet = stmt.executeQuery(query);
            knLogger.debug( methodName, "Executed the query - ");

            if (resultSet.next()) {
                configMap = new HashMap<String, String>();
                do {
                    String paramName = resultSet.getString("PARAMNAME");
                    String paramValue = resultSet.getString("PARAMVALUE");
                    configMap.put(paramName, paramValue);
                } while (resultSet.next());
            }

            persisterTxn.save();
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO exception - " + e);
            KnDbUtil.rollback(persisterTxn);
        } catch (Exception e) {
            knLogger.error( methodName, "Exception e ");
            knLogger.error( methodName, e);
            KnDbUtil.rollback(persisterTxn);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug( methodName, "EXIT: retrieve XDMServer PTTID ");
        }
        return configMap;
    }
}
