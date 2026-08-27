/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnDbUtil.java
 * Subsystem:   Server Common Lib
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/7/11       7.0
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
 * *******************************************************************************
 */

package com.kodiak.xdms.server.common.dao.persister.db.util;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnDbUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDbUtil.class);
    private static final String className = KnDbUtil.class.getName();

    public static final String PTTSERVERID = "$PTTSERVERID";

    /**
     * Close a connection object.
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                knLogger.info( "closeConnection");
            }
        } catch (Exception e) {
            knLogger.warn( "closeConnection",
                    "error while closing conn object - " + e.getMessage());
        }
    }

    /**
     * Close a statement object.
     *
     * @param stmt
     */
    public static void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            knLogger.warn( "closeStatement",
                    "error while closing statement object - " + e.getMessage());
        }
    }

    /**
     * Close a result set object.
     *
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            knLogger.warn( "close",
                    "error while closing resultset object - " , e.getMessage());
        }
    }

    /**
     * Close a PrepareStatement object.
     *
     * @param ps
     */
    public static void closePrepareStmt(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            knLogger.warn( "close",
                    "error while closing Prepared statement object - " , e.getMessage());
        }
    }

    /**
     * Close a prepared statement object.
     *
     * @param pstmt
     */
    public static void closePreparedStatement(PreparedStatement pstmt) {
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            knLogger.warn( "closeStatement",
                    "error while closing statement object - " + e.getMessage());
        }
    }

    /**
     * this method is used to convert collection of objects into stringbuffer.
     *
     * @param arrayList
     * @return SringBuffer
     */
    public static StringBuffer convertListToStringBuffer(Collection arrayList) {
        StringBuffer buffer = new StringBuffer(200);
        buffer.setLength(0);
        if(!(arrayList==null || arrayList.isEmpty())){
        buffer.append(" ('");
            for (Object anArrayList : arrayList) {
                buffer.append((String) anArrayList).append("', '");
        }
        int len = buffer.length();
            buffer.delete(len - 4, len);
            buffer.append("') ");
        } else{
            buffer.append("( )");

        }

        return buffer;
    }

    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }
    /**
     * @param arrayList
     * @return
     */
    public static StringBuffer convertListToIntBuffer(Collection<Integer> arrayList) {
        StringBuffer buffer = new StringBuffer(200);
        buffer.setLength(0);
        buffer.append(" (");
        for (Integer token : arrayList) {
            buffer.append(token).append(",");
        }
        int len = buffer.length();
        buffer.delete(len - 1, len);
        buffer.append(") ");
        return buffer;
    }

    /**
     * Process the Exception and throw the respective application related Exception with error
     * code.
     *
     * @param e           the SQLException object
     * @param message     the message to be displayed
     * @param pttServerId the PttServerId of the signaling card
     * @param source
     * @param qry         the SQL query
     * @throws KnDBPersistenceException
     * @throws KnDBConnectionException
     */
    public static KnDAOException processException(Exception e, String message, String pttServerId,
                                                  String source, String qry)
            throws KnDBPersistenceException, KnDBConnectionException {
        if (e instanceof SQLException) {
            return processSQLException((SQLException) e, message, pttServerId, source, qry);
        } else if (e instanceof KnXDMServerSystemException) {
            // Its a run time exception. so throw as such
            throw (KnXDMServerSystemException) e;
        } else {
            //may be some null pointer error.
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, e);
        }
    }

    /**
     * Process SQL Exception
     *
     * @param sqle        the SQLException object
     * @param message     the message to be displayed
     * @param pttServerId the PttServerId of the signaling card
     * @param qry         the SQL query
     * @throws KnDBPersistenceException
     * @throws KnDBConnectionException
     */
    private static KnDAOException processSQLException(SQLException sqle, String message,
                                                      String pttServerId, String source, String qry)
            throws KnDBPersistenceException, KnDBConnectionException {

        String methodName = "processSQLException()";
        int errorCode = sqle.getErrorCode();
        String sqlState = sqle.getSQLState();
        knLogger.debug( "processSQLException", "ErrCode: " + errorCode + ", SQL State: " + sqlState + ", Msg: " + sqle.getMessage());
        knLogger.debug( methodName, "Irrecoverable SQL System Error occured, Contact Administrator. Excep :" + sqle);

        if (errorCode < 1000) {
            switch (errorCode) {
                // there are occassions where the error code is 0 due to the bugs in jdbc driver.
                case 0:
                    return new KnDBPersistenceException(KnErrorCodes.DAO.SQL_EXCEPTION, message, sqle, pttServerId, source, qry);
                // connection failed bcos of maximum no of connections reached.
                case 702:
                case 799:
                case 846:
                case 994:
                    return new KnDBConnectionException(KnErrorCodes.DAO.CONNECTION_FAILED, message, pttServerId);
                case 711: //server is busy doing data store creation..
                    return new KnDBConnectionException(KnErrorCodes.DAO.SERVER_BUSY, message, sqle, pttServerId);
                case 907: // Unique constraints error
                    return new KnDBPersistenceException(KnErrorCodes.DAO.ROW_ALREADY_EXISTS, message, sqle, pttServerId, source, qry);
                case 910:
                    return new KnDBPersistenceException(KnErrorCodes.DAO.ROW_ALREADY_DELETED, message, sqle, pttServerId, source, qry);
                case 982:
                    return new KnDBPersistenceException(KnErrorCodes.DAO.COLUMN_WIDTH_EXCEEDED, message, sqle, pttServerId, source, qry);
            }
            //TimesTen server specific errors. Its highly critical and may need Technical support from TimesTen.
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else {
            //throwing connection failed for all timesten error (rollback will be happen for these all errors codes)
            throw new KnDBConnectionException(KnErrorCodes.DAO.CONNECTION_FAILED, message, pttServerId);
        }

        /* }else if (errorCode >= 1000 && errorCode < 2000) {
            //Query Errors like syntax errors, mallformed identifiers and tables name etc.
            //these errors cant be corrected with out code change
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 2000 && errorCode < 3000) {
            //Metadata errors like data size exceeds, table or column not found etc..
            //these errors cant be corrected with out code change
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 3000 && errorCode < 4000) {
            //keys and join related issues.
            //these errors cant be corrected with out code change
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 4000 && errorCode < 5000) {
            //Internal server errorrs. Its highly critical and may need Technical support from TimesTen
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 5000 && errorCode < 5000) {
            //TimesTen - Oracle interworking issues
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 6000 && errorCode < 5000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 7000 && errorCode < 5000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 8000 && errorCode < 5000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 9000 && errorCode < 5000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 10000 && errorCode < 11000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 11000 && errorCode < 12000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 12000 && errorCode < 14000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 20000 && errorCode < 31000) {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else {
            throw new KnXDMServerSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
       } */
        }


    public static String getInsertQuery(String tableName, ArrayList<String> queryFields) {
        String methodName = "getInsertQuery(String, ArrayList<String>)";

        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("INSERT INTO ");
        strBuffer.append(tableName);
        strBuffer.append("(");
        for (int i = 0; i < queryFields.size(); i++) {
            strBuffer.append(queryFields.get(i));
            if (i < queryFields.size() - 1) {
                strBuffer.append(", ");
            }
        }
        strBuffer.append(") ");
        strBuffer.append("VALUES(");
        for (int i = 0; i < queryFields.size(); i++) {
            strBuffer.append("?");
            if (i < queryFields.size() - 1) {
                strBuffer.append(", ");
            }
        }
        strBuffer.append(")");
        knLogger.debug( methodName, "Query generated - " + strBuffer.toString());
        return strBuffer.toString();
    }

    /**
     * This method will replace the first occurrence of ? with value in the input query.
     * this is written as part of moving prepared statement to statement for queries mixed with IN operator and ?.
     *
     * @param query the sql query
     * @param value values to be replaced in the query
     * @return  replaced query.
     */
    public static String replaceValInQry(String query, String value) {
        if (null != query) {
            query = query.replaceFirst("\\?", "'" + value + "'");
        }
        return query;
    }

    /**
     * This method will replace the first occurrence of ? with value in the input query.
     * this is written as part of moving prepared statement to statement for queries mixed with IN operator and ?.
     *
     * @param query the sql query
     * @param value values to be replaced in the query
     * @return  replaced query.
     */
    public static String replaceValInQry(String query, int value) {
        if (null != query) {
            String str = String.valueOf(value);
            query = query.replaceFirst("\\?", str);
        }
        return query;
    }

    public static String replaceValInQry(String query, long value) {
        if (null != query) {
            String str = String.valueOf(value);
            query = query.replaceFirst("\\?", str);
        }
        return query;
    }


    public static Collection<Collection<String>> getCollectionList(List<String> list, int size) {
        Collection<Collection<String>> collecList = new ArrayList<Collection<String>>();
        if (list.size() <= size) {
            collecList.add(list);
            return collecList;
        }
        int length = list.size() / size;
        int endIndex = size;
        int startIndex = 0;
        for (int i = 0; i < length; i++) {
            Collection<String> subList = list.subList(startIndex, endIndex);
            collecList.add(subList);
            startIndex = endIndex;
            endIndex = endIndex + size;
        }
        endIndex = endIndex - size;
        if (endIndex < list.size()) {
             Collection<String> subList = list.subList(endIndex, list.size());
            collecList.add(subList);
        }
        return collecList;
    }

    public static List<List<Integer>> getLists(List<Integer> list, int size) {
        List<List<Integer>> collecList = new ArrayList<>();
        if (list.size() <= size) {
            collecList.add(list);
            return collecList;
        }
        int length = list.size() / size;
        int endIndex = size;
        int startIndex = 0;
        for (int i = 0; i < length; i++) {
            List<Integer> subList = list.subList(startIndex, endIndex);
            collecList.add(subList);
            startIndex = endIndex;
            endIndex = endIndex + size;
        }
        endIndex = endIndex - size;
        if (endIndex < list.size()) {
            List<Integer> subList = list.subList(endIndex, list.size());
            collecList.add(subList);
        }
        return collecList;
    }

    /**
     * Method to get the final subscription type from public and corporate subscription types.
     * @param publicSubscType
     * @param corpSubscType
     * @return
     */
    public static int getMappedSubscriptionType(int publicSubscType, int corpSubscType) {

        String methodName = "getMappedSubscriptionType(publicSubscType, corpSubscType)";
        int subscriptionType = 0;
        knLogger.debug( methodName, "PiublicSubscType - " , publicSubscType , ", CorpSubscType - " , corpSubscType);
        if (publicSubscType == 1 && corpSubscType == 0) {
            subscriptionType = 0;
        } else if (publicSubscType == 0 && corpSubscType == 1) {
            subscriptionType = 1;
        } else if (publicSubscType == 1 && corpSubscType == 1) {
            subscriptionType = 2;
        } else {
            knLogger.debug( methodName, "Unknown type, mapping to default type - 0");
        }
        return subscriptionType;
    }

    public static String formIntegerCommaSeperatedIdList(Collection<Integer> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return "";
        }
        for (Integer str : collectionStr) {
            buffer = buffer.append(str).append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String formLongCommaSeperatedIdList(Collection<Long> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return "";
        }
        for (Long str : collectionStr) {
            buffer = buffer.append(str).append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String formCommaSeperatedIdList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("'").append(mdn).append("',");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }
}