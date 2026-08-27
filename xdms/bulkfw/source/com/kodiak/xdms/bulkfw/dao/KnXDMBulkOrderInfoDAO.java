/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dao;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMBulkOrderInfoDAO.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
 * ************************************************************************
 */
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class KnXDMBulkOrderInfoDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMBulkOrderInfoDAO.class);

    private String pttServerId;
    private final String BULKORDER_ID = "BULKORDER_ID";
    private final String bulkOrderType = "BULKORDER_TYPE";
    private final String changeLevel = "CHANGE_LEVEL";
    private final String corpId = "CORP_ID";
    private final String boReqObject = "BO_REQ_OBJECT";
    private final String boReqObjVersion = "BO_REQ_OBJECT_VERSION";
    private final String insertionTime = "INSERTION_TIME";
    private final String STATUS = "STATUS";
    private final String completionTime = "COMPLETION_TIME";

    private final String tableName = KnBulkFwConstants.BO_INFO_TABLE_NAME;

    private final String GET_ALL_BULK_ORDERS_QRY = "SELECT BULKORDER_ID, BULKORDER_TYPE, CHANGE_LEVEL, CORP_ID, BO_REQ_OBJECT, BO_REQ_OBJECT_VERSION, INSERTION_TIME, "
       		+ "STATUS, COMPLETION_TIME FROM DG.XDM_BULK_ORDER_INFO WHERE STATUS IN (0) AND INSERTION_TIME < ? ORDER BY INSERTION_TIME";

    public KnXDMBulkOrderInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * Inserts the bulkOrderInfoDTO
     * @param bulkOrderInfoDTO
     * @param persistTxn
     * @throws KnDAOException
     */
    public void insert(KnBulkDTO bulkOrderInfoDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "insert(KnBulkOrderInfoPersistDTO, KnPersisterTxn)";
        knLogger.entry(methodName, bulkOrderInfoDTO, persistTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String insertQry = null;
        InputStream is = null;
        try {

            Integer boCorpId = bulkOrderInfoDTO.getCorpId();
            Object bulkOrderObj = bulkOrderInfoDTO.getBulkOrderReqObj();
            if (bulkOrderObj == null)
                bulkOrderObj = bulkOrderInfoDTO.getBulkOrderObj();
            String bulkOrderObjVersion = bulkOrderInfoDTO.getBulkOrderObjVersion();

            ArrayList<String> queryFields = new ArrayList<>();
            queryFields.add(BULKORDER_ID);
            queryFields.add(bulkOrderType);
            queryFields.add(changeLevel);
            if (boCorpId != null) {
                queryFields.add(corpId);
            }
            if (bulkOrderObj != null) {
                queryFields.add(boReqObject);
            }
            if (bulkOrderInfoDTO.getBulkOrderObjVersion() != null) {
                queryFields.add(boReqObjVersion);
            }
            queryFields.add(insertionTime);
            queryFields.add(STATUS);

            insertQry = KnDbUtil.getInsertQuery(tableName, queryFields);

            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(insertQry);
            int columnIndex = 0;
            pStmt.setInt(++columnIndex, bulkOrderInfoDTO.getBulkOrderId());
            pStmt.setInt(++columnIndex, bulkOrderInfoDTO.getBulkOrderType());
            pStmt.setInt(++columnIndex, bulkOrderInfoDTO.getChangeLevel());
            if (boCorpId != null) {
                pStmt.setLong(++columnIndex, boCorpId);
            }

            if (bulkOrderObj != null) {
                if (bulkOrderObj instanceof String) {
                    String xmlDoc = String.valueOf(bulkOrderObj);
                    is = new ByteArrayInputStream(xmlDoc.getBytes("UTF-8"));
                    knLogger.debug(methodName, "in if block ");
                    pStmt.setBinaryStream(++columnIndex, is, xmlDoc.length());
                } else {
                    knLogger.debug(methodName, "in Else block ");
                    is = KnDbUtil.convertToInputStream(bulkOrderObj);
                    pStmt.setBinaryStream(++columnIndex, is, is.available());
                }
            }

            if (bulkOrderObjVersion != null) {
                pStmt.setString(++columnIndex, bulkOrderObjVersion);
            }

            pStmt.setLong(++columnIndex, bulkOrderInfoDTO.getInsertionTime());
            pStmt.setInt(++columnIndex, bulkOrderInfoDTO.getStatus());

            knLogger.debug(methodName, "QUERY: Executing Qry ", insertQry, " with ", bulkOrderInfoDTO);

            int result = pStmt.executeUpdate();

            knLogger.debug(methodName, "Query Executed with result - ", result);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to create Bulk Order Info - " + e.getMessage(), pttServerId, tableName, insertQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to create Bulk Order Info - " + e.getMessage(), pttServerId, tableName, insertQry);
        } finally {
            KnDbUtil.closeInputStream(is);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.exit(methodName);

    }

    /**
     * Updates the BulkorderInfo with the provided data
     * @param bulkOrderInfoDTO
     * @param persistTxn
     * @throws KnDAOException
     */
    public void updateJobStatus(KnBulkDTO bulkOrderInfoDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "updateJobStatus(bulkOrderInfoDTO, persistTxn)";
        knLogger.entry(methodName, bulkOrderInfoDTO, persistTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String updateQry = "UPDATE " + tableName + " SET ";
        InputStream input = null;
        ByteArrayOutputStream byteArray;
        ObjectOutputStream objop;
        try {

            int boStatus = bulkOrderInfoDTO.getStatus();
            Long boCompletionTime = bulkOrderInfoDTO.getCompletionTime();
            int bulkOrderId = bulkOrderInfoDTO.getBulkOrderId();
            Object respObj = bulkOrderInfoDTO.getBulkOrderRespObj();
            StringBuilder qryBuffer = new StringBuilder();
            qryBuffer.append(updateQry);
            qryBuffer.append(STATUS).append("=?");
            if (boCompletionTime != null) {
                qryBuffer.append(", ");
                qryBuffer.append(completionTime).append("=? ");
            }
            if (respObj != null) {
                qryBuffer.append(", ");
                String boRespObject = "BO_RESP_OBJECT";
                qryBuffer.append(boRespObject).append("=? ");
            }

            qryBuffer.append("WHERE ").append(BULKORDER_ID).append("=?");
            knLogger.debug(methodName, "QUERY: ", qryBuffer.toString());
            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(qryBuffer.toString());
            int columnIndex = 0;
            pStmt.setInt(++columnIndex, boStatus);
            if (boCompletionTime != null) {
                pStmt.setLong(++columnIndex, boCompletionTime);
            }
            if (respObj != null) {
                byteArray = new ByteArrayOutputStream();
                objop = new ObjectOutputStream(byteArray);
                objop.writeObject(respObj);
                objop.flush();
                objop.close();
                byte[] bytes = byteArray.toByteArray();
                knLogger.debug(methodName, "columnIndex: ", columnIndex);
                input = new ByteArrayInputStream(bytes);
                pStmt.setBinaryStream(++columnIndex, input, input.available());
            }
            pStmt.setInt(++columnIndex, bulkOrderId);

            knLogger.debug(methodName, "QUERY: Executing ", qryBuffer.toString(), " with ", bulkOrderInfoDTO);
            int result = pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed", result);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to update Bulk Order Info - " + e.getMessage(), pttServerId, tableName, updateQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Bulk Order Info - " + e.getMessage(), pttServerId, tableName, updateQry);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeInputStream(input);
        }
        knLogger.info(methodName, "EXIT : update Bulk Order Info");
    }

    /**
     * Converts byte stream to Object
     * @param requestBuff
     * @return
     */
    private KnMessage getObjectFromStream(byte[] requestBuff) {
        String methodName = "getObjectFromStream(byte[])";
        knLogger.debug(methodName, "ENTRY ", requestBuff.length);
        KnMessage msg = null;
        try {
            ObjectInputStream objectIn;
            objectIn = new ObjectInputStream(new ByteArrayInputStream(requestBuff));
            msg = (KnMessage) objectIn.readObject();
            objectIn.close();
        } catch (IOException e) {
            knLogger.error(methodName, "IO Exception occurred", e);
        } catch (ClassNotFoundException e) {
            knLogger.error(methodName, "ClassNotFoundException occurred", e);
        }
        knLogger.info(methodName, "EXIT ");
        return msg;
    }

    /**
     * Retrives the BulkOrder Info
     * @param bulkOrderId
     * @param persistTxn
     * @return
     * @throws KnDAOException
     */
    public KnBulkDTO getBulkOrderDetails(final int bulkOrderId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getBulkOrderDetails(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: get Bulk Order Details ", bulkOrderId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        InputStream is = null;
        KnBulkDTO bulkOrderInfoDTO = null;
        ByteArrayOutputStream bytearray = null;

        StringBuilder getQry = new StringBuilder();
        getQry.append("SELECT ");
        getQry.append(BULKORDER_ID).append(", ").append(bulkOrderType).append(", ").append(changeLevel).append(", ");
        getQry.append(corpId).append(", ").append(boReqObject).append(", ").append(boReqObjVersion).append(", ");
        getQry.append(insertionTime).append(", ").append(STATUS).append(", ").append(completionTime);
        getQry.append(" FROM ").append(tableName);
        getQry.append(" WHERE ").append(BULKORDER_ID).append("=?");

        final String getDetailsQry = getQry.toString();

        try {

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(getDetailsQry);
            pStmt.setInt(1, bulkOrderId);

            knLogger.debug(methodName, "QUERY: Executing ", getDetailsQry, " with ", bulkOrderId);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", rs);

            if (rs.next()) {
                bulkOrderInfoDTO = new KnBulkDTO();
                bulkOrderInfoDTO.setBulkOrderId(rs.getInt(BULKORDER_ID));
                bulkOrderInfoDTO.setBulkOrderType(rs.getInt(bulkOrderType));
                bulkOrderInfoDTO.setChangeLevel(rs.getInt(changeLevel));
                bulkOrderInfoDTO.setCorpId(rs.getInt(corpId));
                is = rs.getBinaryStream(boReqObject);
                byte[] buf = new byte[is.available()];
                bytearray = new ByteArrayOutputStream();
                int readbytes;
                while ((readbytes = is.read(buf, 0, buf.length)) != -1) {
                    bytearray.write(buf, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderObj(bytearray.toByteArray());
                bulkOrderInfoDTO.setBulkOrderReqObj(getObjectFromStream(bytearray.toByteArray()));
                bulkOrderInfoDTO.setBulkOrderObjVersion(rs.getString(boReqObjVersion));
                bulkOrderInfoDTO.setInsertionTime(rs.getLong(insertionTime));
                bulkOrderInfoDTO.setStatus(rs.getInt(STATUS));
                bulkOrderInfoDTO.setCompletionTime(rs.getLong(completionTime));
            }

            knLogger.debug(methodName, "Bulk Order Info DTO - ", bulkOrderInfoDTO);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            try {
                if (is != null) {
                    is.close();
                }
                if (bytearray != null) {
                	bytearray.close();
                }
            } catch (IOException e) {
                knLogger.error(methodName, "IO Exception while closing stream ", e);
            }
        }
        knLogger.info(methodName, "EXIT : get Bulk Order Infor");

        return bulkOrderInfoDTO;
    }

    /**
     * Retrives PamAccountID for BulkOrder
     * @param bulkOrderId
     * @param persistTxn
     * @return
     * @throws KnDAOException
     */
    public int getPamAccountId(int bulkOrderId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getPamAccountId(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: get PamaccountId ", bulkOrderId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int pamAccountId = 0;
        StringBuilder getQry = new StringBuilder();
        getQry.append("SELECT ");
        getQry.append(corpId);
        getQry.append(" FROM ").append(tableName);
        getQry.append(" WHERE ").append(BULKORDER_ID).append("=?");

        final String getDetailsQry = getQry.toString();
        try {

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(getDetailsQry);
            pStmt.setInt(1, bulkOrderId);

            knLogger.debug(methodName, "QUERY: Executing ", getDetailsQry, " with ", bulkOrderId);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", rs);

            if (rs.next()) {
                pamAccountId = rs.getInt(corpId);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT : PamAccountId ", pamAccountId);
        return pamAccountId;
    }

    /**
     * Deletes the BulkOrder table entry for the list of BulkOrders
     * @param bulkOrderIdList
     * @param persistTxn
     * @throws KnDAOException
     */
    public void delete(List<Integer> bulkOrderIdList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(KnBulkOrderInfoPersistDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Delete Bulk Order Info");

        Connection conn;
        PreparedStatement pStmt = null;

        String deleteQry = "DELETE FROM " + tableName + " WHERE " + BULKORDER_ID + "=? AND STATUS NOT IN (1)";
        List<Integer> processBoList = new ArrayList<>(bulkOrderIdList);
        List<Integer> failedBoList = new ArrayList<>();

        try {
            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(deleteQry);

            while (processBoList.size() > 0) {
                for (Integer bulkOrderId : bulkOrderIdList) {
                    pStmt.setInt(1, bulkOrderId);
                    pStmt.addBatch();
                }
                knLogger.debug(methodName, "QUERY: Executing ", deleteQry, " with ", bulkOrderIdList);

                try {
                    int[] updateCount = pStmt.executeBatch();
                    if (updateCount.length == processBoList.size()) {
                        processBoList.removeAll(processBoList);
                    }
                } catch (BatchUpdateException e) {
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedBoList.add(processBoList.get(failedRow));
                        processBoList = processBoList.subList(0, failedRow + 1);
                    }

                }
            }

            knLogger.debug(methodName, "QUERY : Completed.");
            knLogger.warn(methodName, "FAILED BO List - ", failedBoList);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to delete Bulk Order Info - " + e.getMessage(), pttServerId, tableName, deleteQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Bulk Order Info - " + e.getMessage(), pttServerId, tableName, deleteQry);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT : delete Bulk Order Infor");

    }

    public List<KnBulkDTO> getPendingBulkOrderDetails(long expiredtime, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getPendingBulkOrderDetails(KnPersisterTxn)";
        knLogger.entry(methodName, expiredtime);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        InputStream is = null;
        KnBulkDTO bulkOrderInfoDTO = null;
        ByteArrayOutputStream bytearray = null;
        List<KnBulkDTO> bulkOrderList = new ArrayList<>();

        try {

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(GET_ALL_BULK_ORDERS_QRY);
            pStmt.setLong(1, expiredtime);

            knLogger.debug(methodName, "QUERY: Executing ", GET_ALL_BULK_ORDERS_QRY, " with ", expiredtime);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", rs);

            if (rs.next()) {
                bulkOrderInfoDTO = new KnBulkDTO();
                bulkOrderInfoDTO.setBulkOrderId(rs.getInt(BULKORDER_ID));
                bulkOrderInfoDTO.setBulkOrderType(rs.getInt(bulkOrderType));
                bulkOrderInfoDTO.setChangeLevel(rs.getInt(changeLevel));
                bulkOrderInfoDTO.setCorpId(rs.getInt(corpId));
                is = rs.getBinaryStream(boReqObject);
                byte[] buf = new byte[is.available()];
                bytearray = new ByteArrayOutputStream();
                int readbytes;
                while ((readbytes = is.read(buf, 0, buf.length)) != -1) {
                    bytearray.write(buf, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderObj(bytearray.toByteArray());
                bulkOrderInfoDTO.setBulkOrderReqObj(getObjectFromStream(bytearray.toByteArray()));
                bulkOrderInfoDTO.setBulkOrderObjVersion(rs.getString(boReqObjVersion));
                bulkOrderInfoDTO.setInsertionTime(rs.getLong(insertionTime));
                bulkOrderInfoDTO.setStatus(rs.getInt(STATUS));
                bulkOrderInfoDTO.setCompletionTime(rs.getLong(completionTime));
                bulkOrderList.add(bulkOrderInfoDTO);
            }

            knLogger.debug(methodName, "Bulk Order Info DTO - ", bulkOrderInfoDTO);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, GET_ALL_BULK_ORDERS_QRY);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            try {
                if (is != null) {
                    is.close();
                }
                if (bytearray != null) {
                	bytearray.close();
                }
            } catch (IOException e) {
                knLogger.error(methodName, "IO Exception while closing stream ", e);
            }
        }
        
        knLogger.exit(methodName, bulkOrderList);
        return bulkOrderList;
    }

}
