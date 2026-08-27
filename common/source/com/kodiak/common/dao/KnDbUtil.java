/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 * File name:   KnDbUtil.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 18, 2010        7.0
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
 * ******************************************************************************
 */
package com.kodiak.common.dao;

import com.kodiak.common.exception.KnException;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnErrorCodes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class KnDbUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDbUtil.class);

    public static final String PTTSERVERID = "$PTTSERVERID";
    private static KnDBConfigInfo configInfo = null;

    /**
     * Close a connection object.
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            knLogger.warn( "closeConnection", "error while closing conn object - " , e.getMessage()
            );
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
            knLogger.warn( "closeStatement", "error while closing statement object - " , e.getMessage()
            );
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
            knLogger.warn( "closeResultSet", "error while closing resultset object - " , e.getMessage()
            );
        }
    }

    /**
     * Close a input stream.
     *
     * @param inputStream
     */
    public static void closeInputStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            knLogger.warn( "closeInputStream", "IOException occurred while closing inputStream object - " , e.getMessage()
            );
        }
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
     * @throws KnPersistenceException
     * @throws KnConnectionException
     */
    public static KnDAOException processException(Exception e, String message, String pttServerId,
                                                  String source, String qry)
            throws KnPersistenceException, KnConnectionException {
        if (e instanceof SQLException) {
            return processSQLException((SQLException) e, message, pttServerId, source, qry);
        } else if (e instanceof KnSystemException) {
            // Its a run time exception. so throw as such
            throw (KnSystemException) e;
        } else {
            //may be some null pointer error.
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, e);
        }
    }

    /**
     * Process SQL Exception
     *
     * @param sqle        the SQLException object
     * @param message     the message to be displayed
     * @param pttServerId the PttServerId of the signaling card
     * @param qry         the SQL query
     * @throws KnPersistenceException
     * @throws KnConnectionException
     */
    private static KnDAOException processSQLException(SQLException sqle, String message,
                                                      String pttServerId, String source, String qry)
            throws KnPersistenceException, KnConnectionException {

        String methodName = "processSQLException()";
        int errorCode = sqle.getErrorCode();
        String sqlState = sqle.getSQLState();
        knLogger.debug( "processSQLException", "ErrCode: " , errorCode , ", SQL State: " , sqlState , ", Msg: " , sqle.getMessage());
        knLogger.debug( methodName, "Irrecoverable SQL System Error occured, Contact Administrator. Excep :" , sqle);

        if (errorCode < 1000) {
            switch (errorCode) {
                // there are occassions where the error code is 0 due to the bugs in jdbc driver.
                case 0:
                    return new KnPersistenceException(KnErrorCodes.DAO.SQL_EXCEPTION, message, sqle, pttServerId, source, qry);
                // connection failed bcos of maximum no of connections reached.
                case 702:
                case 799:
                case 846:
                case 994:
                    return new KnConnectionException(KnErrorCodes.DAO.CONNECTION_FAILED, message, pttServerId);
                case 711: //server is busy doing data store creation..
                    return new KnConnectionException(KnErrorCodes.DAO.SERVER_BUSY, message, sqle, pttServerId);
                case 907: // Unique constraints error
                    return new KnPersistenceException(KnErrorCodes.DAO.ROW_ALREADY_EXISTS, message, sqle, pttServerId, source, qry);
                case 910:
                    return new KnPersistenceException(KnErrorCodes.DAO.ROW_ALREADY_DELETED, message, sqle, pttServerId, source, qry);
                case 982:
                    return new KnPersistenceException(KnErrorCodes.DAO.COLUMN_WIDTH_EXCEEDED, message, sqle, pttServerId, source, qry);
            }
            //TimesTen server specific errors. Its highly critical and may need Technical support from TimesTen.
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 1000 && errorCode < 2000) {
            //Query Errors like syntax errors, mallformed identifiers and tables name etc.
            //these errors cant be corrected with out code change
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 2000 && errorCode < 3000) {
            //Metadata errors like data size exceeds, table or column not found etc..
            //these errors cant be corrected with out code change
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 3000 && errorCode < 4000) {
            //keys and join related issues.
            //these errors cant be corrected with out code change
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 4000 && errorCode < 5000) {
            //Internal server errorrs. Its highly critical and may need Technical support from TimesTen
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 5000 && errorCode < 5000) {
            //TimesTen - Oracle interworking issues
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 6000 && errorCode < 5000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 7000 && errorCode < 5000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 8000 && errorCode < 5000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 9000 && errorCode < 5000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 10000 && errorCode < 11000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 11000 && errorCode < 12000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 12000 && errorCode < 14000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else if (errorCode >= 20000 && errorCode < 31000) {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        } else {
            throw new KnSystemException(KnErrorCodes.DAO.INTERNAL_ERROR, message, sqle);
        }
    }

    public static KnDBConfigInfo getDBConfigInfo() {
        String methodName = "getDBConfigInfo()";
        knLogger.debug( methodName, "Entry :");
        if (configInfo == null) {
            Properties props = new Properties();
            try {
                FileInputStream ipStream = new FileInputStream(KnConstants.DB_FILE_PATH);
                props.load(ipStream);
                ipStream.close();

                knLogger.debug( methodName, "DB Properties : " , props);
                // load db properties in config bean.
                configInfo = new KnDBConfigInfo(props);
            } catch (Exception ex) {
                knLogger.error( methodName, "Local DB Props Not Initialized :" , ex.getMessage());
            }
        }
        return configInfo;
    }


    /**
     * @param obj Object
     * @return byte[]
     * @throws KnException
     *          exception
     */
    public static byte[] toByteArray(Object obj) throws KnException {
        String methodName = "toByteArray(Object)";
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            knLogger.error( methodName, "IO Exception - " , ex);
            throw new KnException("CM1001", "Unexpected Exception while converting object.");
        }
        return bytes;
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
        if (!(arrayList == null || arrayList.isEmpty())) {
            buffer.append(" ('");
            for (Object anArrayList : arrayList) {
                buffer.append((String) anArrayList).append("', '");
            }
            int len = buffer.length();
            buffer.delete(len - 4, len);
            buffer.append("') ");
        } else {
            buffer.append("( )");

        }

        return buffer;
    }

    public static StringBuffer convertListToNvarcharStringBuffer(Collection arrayList) {
        StringBuffer buffer = new StringBuffer(200);
        buffer.setLength(0);
        if (!(arrayList == null || arrayList.isEmpty())) {
            buffer.append(" (N'");
            for (Object anArrayList : arrayList) {
                buffer.append((String) anArrayList).append("', N'");
            }
            int len = buffer.length();
            buffer.delete(len - 5, len);
            buffer.append("') ");
        } else {
            buffer.append("( )");

        }

        return buffer;
    }

    public static StringBuffer convertListToNvarcharStringBufferLowerCase(Collection arrayList) {
        StringBuffer buffer = new StringBuffer(200);
        buffer.setLength(0);
        if (!(arrayList == null || arrayList.isEmpty())) {
            buffer.append(" (N'");
            for (Object anArrayList : arrayList) {
                buffer.append(((String) anArrayList).toLowerCase()).append("', N'");
            }
            int len = buffer.length();
            buffer.delete(len - 5, len);
            buffer.append("') ");
        } else {
            buffer.append("( )");

        }

        return buffer;
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
        knLogger.debug( methodName, "Query generated - ", strBuffer.toString());
        return strBuffer.toString();
    }

    public static String getUpdateCorpGroupPropsQuery(String tableName, ArrayList<String> queryFields) {
        String methodName = "getInsertQuery(String, ArrayList<String>)";

        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("UPDATE ");
        strBuffer.append(tableName);
        strBuffer.append(" SET ");
        for (int i = 0; i < queryFields.size(); i++) {
            strBuffer.append(queryFields.get(i));
            if (i < queryFields.size() - 1) {
                strBuffer.append("=? , ");
            } else if (i == queryFields.size() - 1) {
                strBuffer.append("=? ");
            }
        }
        strBuffer.append("WHERE CORPGROUPID = ?");
        knLogger.debug(methodName, "Query generated - ", strBuffer.toString());

        return strBuffer.toString();
    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    public static void rollback(KnPersisterTxn txn) {
        try {
        	if (txn != null) {
        		txn.rollback();
        	}
        } catch (Exception e) {
            knLogger.error( "rollback(txn)", "Failed to rollback the transaction.");
        }
    }


    public static InputStream convertToInputStream(Object obj) throws KnException {
    	String methodName = "convertObjInputStream(Object)";
    	knLogger.debug( methodName, "Convert to Input Stream");
    	InputStream is = null;

    	try {
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream oStream = new ObjectOutputStream(outputStream);
			oStream.writeObject(obj);
			oStream.flush();
			oStream.close();
			is = new ByteArrayInputStream(outputStream.toByteArray());

		} catch (IOException e) {
			knLogger.error( methodName, "IO Exception - " , e);
            throw new KnException("CM1001", "Unexpected Exception while converting object.");
		}

    	return is;
    }
    public static String formCommaSeperatedQuesMarks(Collection<String> collectionStr, String qryBuilder,String constant) {
    	String methodName = "formCommaSeperatedQuesMarks(Collection<String> collectionStr, StringBuilder qryBuilder)";
    	knLogger.debug( methodName, "Entry ::");
    	String query = null;
    	StringBuilder quesMark = new StringBuilder();
    	if(collectionStr != null && !collectionStr.isEmpty()) {
        for(String pttServerId : collectionStr) {
        	quesMark.append("?,");
        }
        query = qryBuilder.replaceAll(constant, quesMark.deleteCharAt(quesMark.length() -1 ).toString());
    	}  
    	else {
    		query = qryBuilder.replaceAll(constant, "");
    	}
    	knLogger.debug( methodName, "Query generated -  ::",query);
        return query;
    }

    public static String formCommaSeperatedQuesMarksAnyType(Collection<?> collectionStr, String qryBuilder,String constant) {
        String methodName = "formCommaSeperatedQuesMarks(Collection<String> collectionStr, StringBuilder qryBuilder)";
        knLogger.debug( methodName, "Entry ::");
        String query = null;
        StringBuilder quesMark = new StringBuilder();
        if(collectionStr != null && !collectionStr.isEmpty()) {
            for(Object pttServerId : collectionStr) {
                quesMark.append("?,");
            }
            query = qryBuilder.replaceAll(constant, quesMark.deleteCharAt(quesMark.length() -1 ).toString());
        }
        else {
            query = qryBuilder.replaceAll(constant, "");
        }
        knLogger.debug( methodName, "Query generated -  ::",query);
        return query;
    }

    public static String formCommaSeperatedQuesMarks(String constant,Collection<Integer> collectionStr, String qryBuilder) {
    	String methodName = "formCommaSeperatedQuesMarks(Collection<String> collectionStr, StringBuilder qryBuilder)";
    	knLogger.debug( methodName, "Entry ::");
    	String query = null;
    	StringBuilder quesMark = new StringBuilder();
    	if(collectionStr != null && !collectionStr.isEmpty()) {
        for(Integer pttServerId : collectionStr) {
        	quesMark.append("?,");
        }
        query = qryBuilder.replaceAll(constant, quesMark.deleteCharAt(quesMark.length() -1 ).toString());
    	}  
    	else {
    		query = qryBuilder.replaceAll(constant, "");
    	}
    	knLogger.debug( methodName, "Query generated -  ::",query);
        return query;
    }

    public static String formCommaSeperatedQuesMarks(String constant, int size, String qryBuilder) {
        String methodName = "formCommaSeperatedQuesMarks(Collection<String> collectionStr, StringBuilder qryBuilder)";
        knLogger.debug(methodName, "Entry ::");
        String query = null;
        StringBuilder quesMark = new StringBuilder();
        if (size != 0) {
            for (int i = 0; i < size; i++)
                quesMark.append("?,");
            query = qryBuilder.replaceAll(constant, quesMark.deleteCharAt(quesMark.length() - 1).toString());
        } else {
            query = qryBuilder.replaceAll(constant, "");
        }
        knLogger.debug(methodName, "Query generated -  ::", query);
        return query;
    }

    public static String formCommaSeperatedQuesMarks(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("?").append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

}
