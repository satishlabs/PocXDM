/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnBulkOrderInfoDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 5/3/13
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnXDMBulkOrderInfoDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMBulkOrderInfoDAO.class);
    private static final String className = KnXDMBulkOrderInfoDAO.class.getName();
    private final String BULK_ORDER_ID = "BULKORDER_ID";
    private final String BULK_ORDER_TYPE = "BULKORDER_TYPE";
    private final String CHANGE_LEVEL = "CHANGE_LEVEL";
    private final String CORP_ID = "CORP_ID";
    private final String BO_REQ_OBJECT = "BO_REQ_OBJECT";
    private final String BO_REQ_OBJECT_VERSION = "BO_REQ_OBJECT_VERSION";
    private final String INSERTION_TIME = "INSERTION_TIME";
    private final String STATUS = "STATUS";
    private final String COMPLETION_TIME = "COMPLETION_TIME";
    private final String BO_RESP_OBJECT = "BO_RESP_OBJECT";


    private final String tableName = "DG.XDM_BULK_ORDER_INFO";

    private String pttServerId;

    public KnXDMBulkOrderInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    private final String GET_BULK_ORDER_QRY = "SELECT BULKORDER_ID, BULKORDER_TYPE, CHANGE_LEVEL, CORP_ID, BO_REQ_OBJECT, "
    		+ " BO_REQ_OBJECT_VERSION, INSERTION_TIME, STATUS, COMPLETION_TIME, BO_RESP_OBJECT FROM DG.XDM_BULK_ORDER_INFO WHERE STATUS in(2,3) and BULKORDER_ID=?";
    private final String GET_BULK_ORDER_COUNT="SELECT COUNT(*) FROM DG.XDM_BULK_ORDER_INFO WHERE BULKORDER_ID = ?";

    private final String GET_ALL_BULK_ORDERS_QRY = "SELECT BULKORDER_ID, BULKORDER_TYPE, CHANGE_LEVEL, CORP_ID, BO_REQ_OBJECT, BO_REQ_OBJECT_VERSION, INSERTION_TIME, "
       		+ "STATUS, COMPLETION_TIME, BO_RESP_OBJECT FROM DG.XDM_BULK_ORDER_INFO WHERE STATUS IN (2, 3) AND COMPLETION_TIME < ? ORDER BY INSERTION_TIME";

    /**
     * @param bulkOrderId
     * @param persistTxn
     * @return
     * @throws KnDAOException
     *
     */
    public KnBulkOrderInfoDTO getBulkOrderDetails(final int bulkOrderId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getBulkOrderDetails(int, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY: get Bulk Order Details ", bulkOrderId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        KnBulkOrderInfoDTO bulkOrderInfoDTO = null;

        StringBuilder getQry = new StringBuilder();
        getQry.append("SELECT ");
        getQry.append(BULK_ORDER_ID).append(", ").append(BULK_ORDER_TYPE).append(", ").append(CHANGE_LEVEL).append(", ");
        getQry.append(CORP_ID).append(", ").append(BO_REQ_OBJECT).append(", ").append(BO_REQ_OBJECT_VERSION).append(", ");
        getQry.append(INSERTION_TIME).append(", ").append(STATUS).append(", ").append(COMPLETION_TIME).append(", ").append(BO_RESP_OBJECT);
        getQry.append(" FROM ").append(tableName);
        getQry.append(" WHERE ").append(BULK_ORDER_ID).append("=?");

        final String getDetailsQry = getQry.toString();

        try {

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(getDetailsQry);
            pStmt.setInt(1, bulkOrderId);

            knLogger.debug( methodName, "QUERY: Executing ", getDetailsQry, " with ", bulkOrderId);
            rs = pStmt.executeQuery();

            if (rs.next()) {
                bulkOrderInfoDTO = new KnBulkOrderInfoDTO();
                bulkOrderInfoDTO.setBulkOrderId(rs.getInt(BULK_ORDER_ID));
                bulkOrderInfoDTO.setBulkOrderType(rs.getInt(BULK_ORDER_TYPE));
                bulkOrderInfoDTO.setChangeLevel(rs.getInt(CHANGE_LEVEL));
                bulkOrderInfoDTO.setCorpId(rs.getInt(CORP_ID));
                InputStream is = rs.getBinaryStream(BO_REQ_OBJECT);

                byte[] buf = new byte[is.available()];
                ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
                int readbytes = 0;
                while ((readbytes = is.read(buf, 0, buf.length)) != -1) {
                    bytearray.write(buf, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderReqObj(bytearray.toByteArray());
                bulkOrderInfoDTO.setBulkOrderReqObjVersion(rs.getString(BO_REQ_OBJECT_VERSION));
                bulkOrderInfoDTO.setInsertionTime(rs.getLong(INSERTION_TIME));
                bulkOrderInfoDTO.setStatus(rs.getInt(STATUS));
                bulkOrderInfoDTO.setCompletionTime(rs.getLong(COMPLETION_TIME));
                InputStream isresp = rs.getBinaryStream(BO_RESP_OBJECT);
                byte[] bufresp = new byte[isresp.available()];
                bytearray = new ByteArrayOutputStream();
                readbytes = 0;
                while ((readbytes = isresp.read(bufresp, 0, bufresp.length)) != -1) {
                    bytearray.write(bufresp, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderRespObj(bytearray.toByteArray());

            }

            knLogger.debug( methodName, "Bulk Order Info DTO - ", bulkOrderInfoDTO);

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : get Bulk Order Info");

        }

        return bulkOrderInfoDTO;
    }


    /**
     * @param persistTxn
     * @return
     * @throws KnDAOException
     *
     */
    public List<KnBulkOrderInfoDTO> getCompletedBulkOrdersInfo(long expiredtime, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getCompletedBulkOrdersDetails(KnPersisterTxn)";
        knLogger.entry( methodName, expiredtime);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        InputStream is = null;
        InputStream isresp = null;
        ByteArrayOutputStream bytearray = null;

        List<KnBulkOrderInfoDTO> bulkOrderInfoDTOs = new ArrayList<KnBulkOrderInfoDTO>();
        KnBulkOrderInfoDTO bulkOrderInfoDTO;

        try {

            conn = persistTxn.getDBConnection(pttServerId, true);

            pStmt = conn.prepareStatement(GET_ALL_BULK_ORDERS_QRY);
            pStmt.setLong(1, expiredtime);

            knLogger.debug( methodName, "QUERY: Executing ", GET_ALL_BULK_ORDERS_QRY);
            rs = pStmt.executeQuery();

            while (rs.next()) {
                bulkOrderInfoDTO = new KnBulkOrderInfoDTO();
                bulkOrderInfoDTO.setBulkOrderId(rs.getInt(BULK_ORDER_ID));
                bulkOrderInfoDTO.setBulkOrderType(rs.getInt(BULK_ORDER_TYPE));
                bulkOrderInfoDTO.setChangeLevel(rs.getInt(CHANGE_LEVEL));
                bulkOrderInfoDTO.setCorpId(rs.getInt(CORP_ID));
                is = rs.getBinaryStream(BO_REQ_OBJECT);

                byte[] buf = new byte[is.available()];
                bytearray = new ByteArrayOutputStream();
                int readbytes = 0;
                while ((readbytes = is.read(buf, 0, buf.length)) != -1) {
                    bytearray.write(buf, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderReqObj(bytearray.toByteArray());
                bulkOrderInfoDTO.setBulkOrderReqObjVersion(rs.getString(BO_REQ_OBJECT_VERSION));
                bulkOrderInfoDTO.setInsertionTime(rs.getLong(INSERTION_TIME));
                bulkOrderInfoDTO.setStatus(rs.getInt(STATUS));
                bulkOrderInfoDTO.setCompletionTime(rs.getLong(COMPLETION_TIME));
                isresp = rs.getBinaryStream(BO_RESP_OBJECT);
                byte[] bufresp = new byte[isresp.available()];
                bytearray = new ByteArrayOutputStream();
                readbytes = 0;
                while ((readbytes = isresp.read(bufresp, 0, bufresp.length)) != -1) {
                    bytearray.write(bufresp, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderRespObj(bytearray.toByteArray());
                bulkOrderInfoDTOs.add(bulkOrderInfoDTO);
            }

            knLogger.debug( methodName, "Bulk Orders Info size - ", bulkOrderInfoDTOs.size());

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, GET_ALL_BULK_ORDERS_QRY);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, GET_ALL_BULK_ORDERS_QRY);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            try {
                if (is != null) {
                    is.close();
                }
                if (isresp != null) {
                	isresp.close();
                }
                if (bytearray != null) {
                	bytearray.close();
                }
            } catch (IOException e) {
                knLogger.error(methodName, "IO Exception while closing stream ", e);
            }
            knLogger.info( methodName, "EXIT : get Bulk Orders Info");

        }

        return bulkOrderInfoDTOs;
    }

    public int getBulkOrderCount(int bulkOrderId,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getBulkOrderCount(int)";
        knLogger.debug( methodName, "ENTRY: bulkOrderId-",bulkOrderId);
        Connection conn = null;
        //Statement stmt = null;
        PreparedStatement pStmt=null;
        ResultSet rs = null;
        int count=0;
        String query=GET_BULK_ORDER_COUNT;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, bulkOrderId);
            knLogger.debug(methodName,"E");

            knLogger.debug(methodName, "QUERY: Executing - ", query );
            rs=pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                count=rs.getInt(1);
            }

            knLogger.debug( methodName, "Count in Bulk Order table ", count);

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to get Bulk Order count - " + e.getMessage(), pttServerId, tableName, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Count - " + e.getMessage(), pttServerId, tableName, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName,"EXIT : ",count);
        return count;
    }

    /**
     * @param persistTxn
     * @param bulkOrderID
     * @return KnBulkOrderInfoDTO
     * @throws KnDAOException
     *
     */
    public KnBulkOrderInfoDTO getCompletedBulkOrderDetail(int bulkOrderID, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getCompletedBulkOrderDetail(int, KnPersisterTxn)";
        knLogger.entry( methodName, bulkOrderID);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnBulkOrderInfoDTO bulkOrderInfoDTO = null;

        try {

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(GET_BULK_ORDER_QRY);
            pStmt.setInt(1, bulkOrderID);

            rs = pStmt.executeQuery(GET_BULK_ORDER_QRY);

            while (rs.next()) {
                bulkOrderInfoDTO = new KnBulkOrderInfoDTO();
                bulkOrderInfoDTO.setBulkOrderId(rs.getInt(BULK_ORDER_ID));
                bulkOrderInfoDTO.setBulkOrderType(rs.getInt(BULK_ORDER_TYPE));
                bulkOrderInfoDTO.setChangeLevel(rs.getInt(CHANGE_LEVEL));
                bulkOrderInfoDTO.setCorpId(rs.getInt(CORP_ID));
                InputStream is = rs.getBinaryStream(BO_REQ_OBJECT);

                byte[] buf = new byte[is.available()];
                ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
                int readbytes = 0;
                while ((readbytes = is.read(buf, 0, buf.length)) != -1) {
                    bytearray.write(buf, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderReqObj(bytearray.toByteArray());
                bulkOrderInfoDTO.setBulkOrderReqObjVersion(rs.getString(BO_REQ_OBJECT_VERSION));
                bulkOrderInfoDTO.setInsertionTime(rs.getLong(INSERTION_TIME));
                bulkOrderInfoDTO.setStatus(rs.getInt(STATUS));
                bulkOrderInfoDTO.setCompletionTime(rs.getLong(COMPLETION_TIME));
                InputStream isresp = rs.getBinaryStream(BO_RESP_OBJECT);
                byte[] bufresp = new byte[isresp.available()];
                bytearray = new ByteArrayOutputStream();
                readbytes = 0;
                while ((readbytes = isresp.read(bufresp, 0, bufresp.length)) != -1) {
                    bytearray.write(bufresp, 0, readbytes);
                }
                bytearray.flush();
                bulkOrderInfoDTO.setBulkOrderRespObj(bytearray.toByteArray());
            }

        }  catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, GET_BULK_ORDER_QRY);

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, GET_BULK_ORDER_QRY);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : get Bulk Orders Info");
        }

        knLogger.exit(methodName, bulkOrderInfoDTO);
        return bulkOrderInfoDTO;
    }


    /**
     * @param bulkOrderIdList
     * @param persistTxn
     * @throws KnDAOException
     *
     */
    public void deleteBulkOrders(List<Integer> bulkOrderIdList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(KnBulkOrderInfoPersistDTO, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY: Delete Bulk Order Info", bulkOrderIdList);

        Connection conn;
        PreparedStatement pStmt = null;

        String deleteQry = "DELETE FROM " + tableName + " WHERE " + BULK_ORDER_ID + "=? AND STATUS in (2,3) ";
        try {
            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(deleteQry);

            for (Integer bulkOrderId : bulkOrderIdList) {
                pStmt.setInt(1, bulkOrderId);
                pStmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY: Executing ", deleteQry, " with ", bulkOrderIdList);

            try {
                int[] updateCount = pStmt.executeBatch();
                knLogger.debug( methodName, "Count ", updateCount.length);
            } catch (BatchUpdateException e) {
                knLogger.error( methodName, "BatchUpdateException  ", e);

            }

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to delete Bulk Order Info - " + e.getMessage(), pttServerId, tableName, deleteQry);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Bulk Order Info - " + e.getMessage(), pttServerId, tableName, deleteQry);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info( methodName, "EXIT : delete Bulk Order Infor");

    }

    /**
     * @param bulkOrderIdList
     * @param persistTxn
     * @throws KnDAOException
     *
     */
    public void updateBulkOrders(List<Integer> bulkOrderIdList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateBulkOrders(KnBulkOrderInfoPersistDTO, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY: update Bulk Order Info", bulkOrderIdList);

        Connection conn;
        PreparedStatement pStmt = null;

        String updateQry = "UPDATE " + tableName + " SET " + STATUS + "= 5  WHERE " + BULK_ORDER_ID + "=? ";
        try {
            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(updateQry);

            for (Integer bulkOrderId : bulkOrderIdList) {
                pStmt.setInt(1, bulkOrderId);
                pStmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY: Executing ", updateQry, " with ", bulkOrderIdList);

            try {
                int[] updateCount = pStmt.executeBatch();
                knLogger.debug( methodName, "Count ", updateCount.length);
            } catch (BatchUpdateException e) {
                knLogger.error( methodName, "BatchUpdateException  ", e);

            }

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to update Bulk Order Info - " + e.getMessage(), pttServerId, tableName, updateQry);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Bulk Order Info - " + e.getMessage(), pttServerId, tableName, updateQry);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info( methodName, "EXIT : update Bulk Order Info");

    }

}
