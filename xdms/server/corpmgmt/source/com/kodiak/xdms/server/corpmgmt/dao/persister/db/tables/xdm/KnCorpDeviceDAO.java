/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;
import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeviceInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks;
import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_ALL_SUBSCRIBER_PAGINATED;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_CORP_DEVICE_COUNT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnCorpDeviceDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpDeviceDAO.class);

    public String pttServerId = null;

    KnCorpDeviceDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String APPEND_INPUT = "APPENDINPUT";

    public static final String APPEND_GET_ALL_SUBSCRIBER_NEXTTOKEN = " ROWS ? to ? ";

    public static final String DEVICE_NAME = "DEVICE_NAME";

    public static final String DEVICE_SUBSCR_MDN = "DEVICE_SUBSCR_MDN";

    public static final String REQ_DEVICEID = "REQ_DEVICEID";

    public static final String DEVICEIDLIST = "DEVICEIDLIST";
    public static final String DEVICEID = "DEVICEID";
    public static final String DEVICE_SERIAL_NO = "DEVICE_SERIAL_NO";
    public static final String IMEI1 = "IMEI1";
    public static final String IMEI2 = "IMEI2";
    public static final String DEVICE_VERSION = "DEVICE_VERSION";
    public static final String DEVICEADDLINFO = "DEVICEADDLINFO";

    private static final String UPDATE_LAST_USED_BY_MDN="UPDATE DG.DEVICE_INFO SET DEVICELASTUSED = ? WHERE DEVICEID = ? ";

//    private static final String GET_DEVICE_INFO="SELECT DEVICEID, DEVICESTATUS,DEVICEACTTS,DEVICELASTUSED,DEVICE_CLIENTID,DEVICE_SHARED,DEVICE_IMPI,DEVICE_TYPE,DEVICE_NAME,REQ_DEVICEID,CORPID,DEVICE_SUBSCR_MDN,DEVICEDIGESTPASSWD,DEVICE_CREATED_AS,DEVICE_CLIENTID FROM DG.DEVICE_INFO  WHERE DEVICEID = ? AND CORPID=? ";

    private static final String GET_DEVICE_INFO="SELECT DI.DEVICEID, DI.DEVICESTATUS,DI.DEVICEACTTS,DI.DEVICELASTUSED,DI.DEVICE_CLIENTID,DI.DEVICE_SHARED,DI.DEVICE_IMPI,DI.DEVICE_TYPE,DI.DEVICE_NAME,DI.REQ_DEVICEID,DI.CORPID,DI.DEVICE_SUBSCR_MDN,DI.DEVICEDIGESTPASSWD,DI.DEVICE_CREATED_AS,DI.DEVICE_CLIENTID,PSI.SUBSCRIBERFS2 FROM DG.DEVICE_INFO DI LEFT JOIN DG.POCSUBSCRINFO PSI ON DI.DEVICE_SUBSCR_MDN = PSI.MDN WHERE DI.CORPID=? AND DI.REQ_DEVICEID = ?";

    private static final String GET_DEVICEADDL_INFO="SELECT DEVICEID,DEVICE_SERIAL_NO,IMEI1,IMEI2,DEVICE_VERSION,DEVICEADDLINFO FROM DG.DEVICE_ADDLINFO WHERE DEVICEID=?";

    private static final String GET_DEVICEADDL_INFO_MAP = "SELECT DEVICEID, DEVICE_SERIAL_NO, IMEI1, IMEI2, DEVICE_VERSION, DEVICEADDLINFO FROM DG.DEVICE_ADDLINFO WHERE DEVICEID IN (" + DEVICEIDLIST + ")";

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Unimplemented Methods");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("update", "Unimplemented Methods");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("delete", "Unimplemented Methods");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("select", "Unimplemented Methods");
        return null;
    }

    public List<KnDeviceDetailsDTO> getDeviceList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceList(int corpId,Integer filterType,Integer fetchSize,Integer nextToken,Integer, boolean, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId", corpId, "filterType", filterType, "fetchSize", fetchSize, "nextToken", nextToken);
        List<KnDeviceDetailsDTO> deviceDetails = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            knLogger.debug(methodName, "Query Mapper - ", queryMapper);
            if (fetchSize != 0) {
                query = queryMapper.getQuery(GET_ALL_SUBSCRIBER_PAGINATED);
                query = replaceContactWithValue(query, APPEND_INPUT, APPEND_GET_ALL_SUBSCRIBER_NEXTTOKEN);
                int startIndex = KnCorpUtil.getStartIndex(fetchSize, nextToken);
                int endIndex = KnCorpUtil.getEndIndex(fetchSize, nextToken);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, startIndex);
                pstmt.setInt(2, endIndex);
                pstmt.setInt(3, corpId);
            } else {
                query = queryMapper.getQuery(GET_ALL_SUBSCRIBER_PAGINATED);
                query = replaceContactWithValue(query, APPEND_INPUT, "");
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, corpId);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnDeviceDetailsDTO deviceDetail = new KnDeviceDetailsDTO();
                deviceDetail.setSubscriberFs2(rs.getString(3));
                if (null != rs.getString(DEVICE_SUBSCR_MDN)) {
                    deviceDetail.setDeviceSubscrMdn(rs.getString(DEVICE_SUBSCR_MDN).trim());
                }
                deviceDetail.setDeviceId(rs.getString(5));
                deviceDetail.setDeviceType(rs.getInt(6));
                if (null != rs.getString(DEVICE_NAME)) {
                    deviceDetail.setDeviceName(new String(rs.getBytes(DEVICE_NAME), StandardCharsets.UTF_8));
                }
                deviceDetails.add(deviceDetail);
            }
            knLogger.debug(methodName, "Exit: deviceDetails", deviceDetails.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the device details",
                    pttServerId, "", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return deviceDetails;
    }

    public Map<String, KnXDMDeviceAddlInfoDTO> getDeviceAddInfoMap(List<String> deviceList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceAddInfoMap(List<String> deviceList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY : deviceList size ", deviceList.size());
        Map<String, KnXDMDeviceAddlInfoDTO> deviceAddlInfoDTOMap = new HashMap<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            var subsLists = KnGeneralUtil.splitList(deviceList, BULK_UPDATE_SIZE);
            query = GET_DEVICEADDL_INFO_MAP;
            for (var subsList : subsLists) {
                int index = 1;
                query = replaceContactWithValue(query, DEVICEIDLIST, formCommaSeperatedQuesMarks(subsList));
                pstmt = conn.prepareStatement(query);
                for (String deviceId : deviceList) {
                    pstmt.setString(index++, deviceId);
                }
                knLogger.debug(methodName, "Executing query - ", query);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    KnXDMDeviceAddlInfoDTO xdmDeviceAddlInfoDTO = new KnXDMDeviceAddlInfoDTO();
                    xdmDeviceAddlInfoDTO.setDeviceSerialNo(rs.getString(DEVICE_SERIAL_NO));
                    xdmDeviceAddlInfoDTO.setDeviceIMEI1(rs.getString(IMEI1));
                    xdmDeviceAddlInfoDTO.setDeviceIMEI2(rs.getString(IMEI2));
                    xdmDeviceAddlInfoDTO.setDeviceVersion(rs.getString(DEVICE_VERSION));
                    if (rs.getString(DEVICEADDLINFO) != null) {
                        xdmDeviceAddlInfoDTO.setDeviceInfo(new String(rs.getBytes(DEVICEADDLINFO), StandardCharsets.UTF_8));
                    }
                    deviceAddlInfoDTOMap.put(rs.getString(DEVICEID), xdmDeviceAddlInfoDTO);
                }
            }
            knLogger.debug(methodName, "Exit: deviceAddlInfoDTOMap size", deviceAddlInfoDTOMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the deviceAddlInfoDTOMap",
                    pttServerId, "", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return deviceAddlInfoDTOMap;
    }

    public KnCorpDeviceInfoRespDTO getDeviceInfo(KnIPDeviceInfoDTO deviceInfoDTO, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getDeviceInfo()";
        String query = GET_DEVICE_INFO;
        Connection conn;
        PreparedStatement pstmt=null;
        PreparedStatement pStmtAddl=null;
        ResultSet rs = null;
        ResultSet rsAdd = null;
        String deviceId = null;
        KnCorpDeviceInfoRespDTO knXDMDeviceList = new KnCorpDeviceInfoRespDTO();
        KnDeviceDetailsDTO knXDMDeviceProvDTO = null;
        KnXDMDeviceAddlInfoDTO knXDMDeviceAddlInfoDTO = null;
        knLogger.debug( methodName, "ENTRY : Device last used ");
        knLogger.debug(methodName, "ENTRY : CorpId", deviceInfoDTO.getCorpId(), "DeviceId", deviceInfoDTO.getDeviceId());
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, deviceInfoDTO.getCorpId());
            pstmt.setString(2, deviceInfoDTO.getDeviceId());
            knLogger.debug( methodName, "QUERY: Executing the Query " , query);
            rs=pstmt.executeQuery();
            while(rs.next()){
                knXDMDeviceProvDTO = new KnDeviceDetailsDTO();
                knXDMDeviceProvDTO.setDeviceId(rs.getString(1));
                knXDMDeviceProvDTO.setDeviceType(rs.getInt(8));
                if (null != rs.getString(DEVICE_NAME)) {
                    knXDMDeviceProvDTO.setDeviceName(new String(rs.getBytes(DEVICE_NAME), StandardCharsets.UTF_8));
                }
                if(null != rs.getString(DEVICE_SUBSCR_MDN)) {
                    knXDMDeviceProvDTO.setDeviceSubscrMdn(rs.getString(DEVICE_SUBSCR_MDN).trim());
                }
                if(null != rs.getString(REQ_DEVICEID)) {
                    knXDMDeviceProvDTO.setReqDeviceId(rs.getString(REQ_DEVICEID));
                }
                knXDMDeviceProvDTO.setSubscriberFs2(rs.getString(16));
                deviceId = knXDMDeviceProvDTO.getDeviceId();
            }
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if(deviceId != null) {
                String queryAddlnInfo = GET_DEVICEADDL_INFO;
                pStmtAddl = conn.prepareStatement(queryAddlnInfo);
                pStmtAddl.setString(1, deviceId);
                knLogger.debug(methodName, "QUERY: Executing the Query ", queryAddlnInfo);
                rsAdd = pStmtAddl.executeQuery();
                if (rsAdd.next()) {
                    knXDMDeviceAddlInfoDTO = new KnXDMDeviceAddlInfoDTO();
                    knXDMDeviceAddlInfoDTO.setDeviceSerialNo(rsAdd.getString(2));
                    knXDMDeviceAddlInfoDTO.setDeviceIMEI1(rsAdd.getString(3));
                    knXDMDeviceAddlInfoDTO.setDeviceIMEI2(rsAdd.getString(4));
                    knXDMDeviceAddlInfoDTO.setDeviceVersion(rsAdd.getString(5));
                    if (null != rsAdd.getString(6)) {
                        knXDMDeviceAddlInfoDTO.setDeviceInfo(new String(rsAdd.getBytes(6), StandardCharsets.UTF_8));
                    }
                }
            }
            knXDMDeviceList.setDeviceAddlInfo(knXDMDeviceAddlInfoDTO);
            knXDMDeviceList.setDeviceInfo(knXDMDeviceProvDTO);

            return knXDMDeviceList;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the device details",
                    pttServerId, "", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(rsAdd);
            KnDbUtil.closePreparedStatement(pstmt);
            KnDbUtil.closePreparedStatement(pStmtAddl);
        }
    }

    public int getCorpDeviceCount(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpDeviceCount(int, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : corpId=", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORP_DEVICE_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query:", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "EXIT : device count of the corp=", count);
        return count;
    }
}
