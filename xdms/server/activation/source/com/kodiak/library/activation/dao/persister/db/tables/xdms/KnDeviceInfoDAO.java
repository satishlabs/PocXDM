/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.library.activation.dao.persister.db.tables.xdms;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.library.activation.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.library.activation.resources.KnDAOSourceTypes;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class KnDeviceInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnDeviceInfoDAO.class);
    public String pttServerId = null;

    private static final String QRY_SEL_BY_DEVICE_ID = "SELECT DEVICEID, DEVICESTATUS, DEVICEACTTS, DEVICELASTUSED, " +
            "DEVICEDIGESTPASSWD, DEVICE_IMPI FROM DG.DEVICE_INFO WHERE DEVICEID = ?";

    private static final String QRY_SEL_BY_REQ_DEVICE_ID = "SELECT DEVICEID, DEVICESTATUS, DEVICEACTTS, DEVICELASTUSED, " +
            "DEVICEDIGESTPASSWD , DEVICE_CREATED_AS , DEVICE_CLIENTID , DEVICE_SHARED ,DEVICE_IMPI FROM DG.DEVICE_INFO WHERE REQ_DEVICEID = ?";


    private static final String QRY_UPDATE_BY_DEVICE_ID = "UPDATE DG.DEVICE_INFO SET DEVICESTATUS = ?, DEVICEACTTS = ? , DEVICELASTUSED = ?, " +
            "DEVICEDIGESTPASSWD = ? ,DEVICE_TYPE = ?  WHERE DEVICEID = ?";

    private static final String QRY_UPDATE_BY_REQ_DEVICE_ID = "UPDATE DG.DEVICE_INFO SET DEVICESTATUS = ?, DEVICEACTTS = ? , DEVICELASTUSED = ?, " +
            "DEVICEDIGESTPASSWD = ? , DEVICE_CLIENTID = ? WHERE REQ_DEVICEID = ?";

    private static final String QRY_UPDATE_DEVICE_INFO_BY_DEVICE_ID = "UPDATE DG.DEVICE_INFO SET DEVICESTATUS = ?, DEVICEACTTS = ? , DEVICELASTUSED = ?, " +
            "DEVICEDIGESTPASSWD = ? ,DEVICE_TYPE = ?, DEVICE_IMPI = ? WHERE DEVICEID = ?";

    private static final String QRY_INSERT = "INSERT INTO DG.DEVICE_INFO (DEVICEID, DEVICESTATUS, DEVICEACTTS, DEVICELASTUSED, DEVICEDIGESTPASSWD, DEVICE_TYPE, DEVICE_IMPI, DEVICE_CREATED_AS, CORPID) VALUES (?,?,?,?,?,?,?,?,?);";

    public KnDeviceInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
        return null;
    }

    /**
     * @param deviceId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnDeviceInfoPersistDTO  getDeviceDetails(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceDetails()";
        knLogger.debug(methodName, "ENTRY : deviceId -> " + deviceId);
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnDeviceInfoPersistDTO deviceInfoDto = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_SEL_BY_DEVICE_ID);
            pStatement.setString(1, deviceId);
            knLogger.debug(methodName, "QUERY : Executing " + QRY_SEL_BY_DEVICE_ID + ", deviceId : " +
                    deviceId + ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");
            if (rs.next()) {
                deviceInfoDto = new KnDeviceInfoPersistDTO();
                deviceInfoDto.setDeviceId(deviceId);
                deviceInfoDto.setDeviceActTimeStamp(rs.getLong(1));
                deviceInfoDto.setDeviceStatus(rs.getInt(2));
                if (rs.getBytes("DEVICE_IMPI") != null) {
                    deviceInfoDto.setDeviceIMPI(new String(rs.getBytes("DEVICE_IMPI"), StandardCharsets.UTF_8));
                } else {
                    deviceInfoDto.setDeviceIMPI(null);
                }
                deviceInfoDto.setDeviceDigestPassword(rs.getString("DEVICEDIGESTPASSWD"));
                //deviceInfoDto.setDeviceLastUsed();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.debug(methodName, "Returning :", deviceInfoDto);
        return deviceInfoDto;
    }

    /**
     * @param reqDeviceId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnDeviceInfoPersistDTO getDeviceDetail(String reqDeviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceDetail()";
        knLogger.debug(methodName, "ENTRY : ReqDeviceId -> " + reqDeviceId);
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnDeviceInfoPersistDTO deviceInfoDto = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_SEL_BY_REQ_DEVICE_ID);
            pStatement.setString(1, reqDeviceId);
            knLogger.debug(methodName, "QUERY : Executing " + QRY_SEL_BY_REQ_DEVICE_ID + ", reqDeviceId : " +
                    reqDeviceId + ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");
            if (rs.next()) {
                deviceInfoDto = new KnDeviceInfoPersistDTO();
                deviceInfoDto.setDeviceId(rs.getString(1));
                deviceInfoDto.setDeviceStatus(rs.getInt(2));
                deviceInfoDto.setDeviceActTimeStamp(rs.getLong(3));
                deviceInfoDto.setDeviceLastUsed(rs.getLong(4));
                deviceInfoDto.setDeviceDigestPassword(rs.getString(5));
                deviceInfoDto.setDeviceCreatedAs(rs.getInt(6));
                deviceInfoDto.setDeviceClientId(rs.getString(7));
                deviceInfoDto.setDeviceshared(rs.getInt(8));
                deviceInfoDto.setDeviceIMPI(new String(rs.getBytes(9), StandardCharsets.UTF_8));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.debug(methodName, "Returning :", deviceInfoDto);
        return deviceInfoDto;
    }

    public void updateDeviceInfoWithImpi(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateDeviceInfoNull()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            byte[] deviceImpi = deviceInfo.getDeviceIMPI().getBytes(StandardCharsets.UTF_8);
            knLogger.info(methodName," deviceImpi :",deviceImpi);
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_UPDATE_DEVICE_INFO_BY_DEVICE_ID);
            pStatement.setInt(1, deviceInfo.getDeviceStatus());
            pStatement.setLong(2, deviceInfo.getDeviceActTimeStamp());
            pStatement.setLong(3, deviceInfo.getDeviceLastUsed());
            pStatement.setString(4, deviceInfo.getDeviceDigestPassword());
            pStatement.setInt(5, deviceInfo.getDeviceType());
            pStatement.setBytes(6,  deviceImpi);
            pStatement.setString(7, deviceInfo.getDeviceId());
            knLogger.debug(methodName, "QUERY : Executing " + QRY_UPDATE_DEVICE_INFO_BY_DEVICE_ID);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.warn(methodName,"Failed for device : ",deviceInfo);
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    public void updateDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //byte[] deviceImpi = deviceInfo.getDeviceIMPI().getBytes(StandardCharsets.UTF_8);
            //knLogger.info(methodName," deviceImpi :",deviceImpi);
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_UPDATE_BY_DEVICE_ID);
            pStatement.setInt(1, deviceInfo.getDeviceStatus());
            pStatement.setLong(2, deviceInfo.getDeviceActTimeStamp());
            pStatement.setLong(3, deviceInfo.getDeviceLastUsed());
            pStatement.setString(4, deviceInfo.getDeviceDigestPassword());
            pStatement.setInt(5, deviceInfo.getDeviceType());
            //pStatement.setBytes(6,  deviceImpi);
            pStatement.setString(6, deviceInfo.getDeviceId());
            knLogger.debug(methodName, "QUERY : Executing " + QRY_UPDATE_BY_DEVICE_ID);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }


    public void updateRadioDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateRadioDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_UPDATE_BY_REQ_DEVICE_ID);
            pStatement.setInt(1, deviceInfo.getDeviceStatus());
            pStatement.setLong(2, deviceInfo.getDeviceActTimeStamp());
            pStatement.setLong(3, deviceInfo.getDeviceLastUsed());
            pStatement.setString(4, deviceInfo.getDeviceDigestPassword());
            pStatement.setString(5, deviceInfo.getDeviceClientId());
            pStatement.setString(6, deviceInfo.getDeviceId());
            knLogger.debug(methodName, "QUERY : Executing " + QRY_UPDATE_BY_REQ_DEVICE_ID);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }



    public void addDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //open a txn if its not already opened
            byte[] deviceImpi = deviceInfo.getDeviceIMPI().getBytes(StandardCharsets.UTF_8);
            knLogger.info(methodName," deviceImpi :",deviceImpi);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_INSERT);
            pStatement.setString(1, deviceInfo.getDeviceId());
            pStatement.setInt(2, deviceInfo.getDeviceStatus());
            pStatement.setLong(3, deviceInfo.getDeviceActTimeStamp());
            pStatement.setLong(4, deviceInfo.getDeviceLastUsed());
            pStatement.setString(5, deviceInfo.getDeviceDigestPassword());
            pStatement.setInt(6, deviceInfo.getDeviceType());
            pStatement.setBytes(7, deviceImpi);
            pStatement.setInt(8, deviceInfo.getDeviceCreatedAs());
            if (deviceInfo.getCorpId() != null && deviceInfo.getCorpId() != 0) {
                pStatement.setInt(9, deviceInfo.getCorpId());
            } else {
                pStatement.setNull(9, java.sql.Types.INTEGER);
            }
            knLogger.debug(methodName, "QUERY : Executing " + QRY_INSERT);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

}
