/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.common.KnSubsCameraInfo;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.*;

public class KnSubsCameraInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsCameraInfoDAO.class);
    private String pttServerId;

    public static final String MDNLIST = "MDNLIST";
    public static final String MDN = "MDN";
    public static final String IP_IDENTIFIER = "IP_IDENTIFIER";
    public static final String CAM_SERIAL_ID = "CAM_SERIAL_ID";

    private static final String QRY_INSERT_SUBS_CAMERA_INFO = "INSERT INTO DG.SUBSCR_CAMERA_INFO(MDN,IP_IDENTIFIER,CAM_SERIAL_ID) VALUES(?,?,?)";

    private static final String QRY_GET_SUBS_CAMERA_INFO = "SELECT MDN,IP_IDENTIFIER,CAM_SERIAL_ID FROM DG.SUBSCR_CAMERA_INFO WHERE MDN IN("+MDNLIST+")";

    private static final String QRY_GET_SUBSCR_CAMERA_INFO = "SELECT IP_IDENTIFIER,CAM_SERIAL_ID FROM DG.SUBSCR_CAMERA_INFO WHERE MDN = ?";

    private static final String QRY_UPDATE_SUBS_CAMERA_INFO = "UPDATE DG.SUBSCR_CAMERA_INFO SET IP_IDENTIFIER=?, CAM_SERIAL_ID=? WHERE MDN= ?";

    private static final String QRY_DELETE_SUBS_CAMERA_INFO = "DELETE FROM DG.SUBSCR_CAMERA_INFO WHERE MDN= ?";

    public KnSubsCameraInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        // TODO Auto-generated method stub
        return null;
    }


    public void createSubscriberCameraInfo(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "createSubscriberCameraInfo(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: with Subs CameraInfo ", subsProfilePersistDTO.getCameraInfo());
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_INSERT_SUBS_CAMERA_INFO);
            pStmt.setString(1, subsProfilePersistDTO.getMdn());
            pStmt.setString(2,subsProfilePersistDTO.getCameraInfo().getIpIdentifier());
            pStmt.setString(3, subsProfilePersistDTO.getCameraInfo().getCameraSerialId());
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_INSERT_SUBS_CAMERA_INFO);
            knLogger.debug(methodName, "Query: Executed :", count);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to create subs camera info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CAMERAINFO, QRY_INSERT_SUBS_CAMERA_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }

    }


    /**
     *
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnSubsCameraInfo> getSubscriberCameraInfo(Collection<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubscriberCameraInfo(KnSubsProfilePersistDTO subsProfilePersistDTO,boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdns ", KnGDPRTemplate.mdnList(mdns), " readOnly :", readOnly);
        Map<String, KnSubsCameraInfo> subsCameraInfoMap = new HashMap<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String query = QRY_GET_SUBS_CAMERA_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            String mdnListStr = KnGeneralUtil.formCommaSeperatedIdList(mdns);
            query = KnGeneralUtil.replaceContactWithValue(query, MDNLIST, mdnListStr);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            rs = stmt.executeQuery(query);
            while (rs.next()){
                KnSubsCameraInfo cameraInfo = new KnSubsCameraInfo();
                cameraInfo.setCameraSerialId(rs.getString(CAM_SERIAL_ID));
                cameraInfo.setIpIdentifier(rs.getString(IP_IDENTIFIER));
                subsCameraInfoMap.put(rs.getString(MDN).trim(),cameraInfo);
            }
            knLogger.debug(methodName, "Query: Executed :", subsCameraInfoMap.size());

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get subs camera info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CAMERAINFO, QRY_GET_SUBS_CAMERA_INFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }

        return subsCameraInfoMap;
    }

    public KnOPSubsProfileInfoDTO getSubscriberCameraInfo(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubscriberCameraInfo(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: with Subs CameraInfo for mdn ", mdn);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto=null;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_GET_SUBSCR_CAMERA_INFO);
            pStmt.setString(1, mdn);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_GET_SUBSCR_CAMERA_INFO);
            knLogger.debug(methodName, "Query: Executed :", rs.getFetchSize());

            if (rs.next()) {
                subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
               populateCameraInfo(subsProfileInfoDto, rs);
            } else {
                knLogger.error(methodName, "Camera info for Subscriber Profile doesn't exist");
        }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get subs camera info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CAMERAINFO, QRY_GET_SUBS_CAMERA_INFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
            return subsProfileInfoDto;
    }

    private KnOPSubsProfileInfoDTO populateCameraInfo(KnOPSubsProfileInfoDTO subsProfileInfoDto, ResultSet rs) throws SQLException {
        KnSubsCameraInfo cameraInfo=new KnSubsCameraInfo();
        cameraInfo.setIpIdentifier(rs.getString("IP_IDENTIFIER"));
        cameraInfo.setCameraSerialId(rs.getString("CAM_SERIAL_ID"));
        subsProfileInfoDto.setCameraInfo(cameraInfo);
        knLogger.debug("populateCameraInfo -","CameraSerialId ",cameraInfo.getCameraSerialId(),"CameraIpidentifier ",cameraInfo.getIpIdentifier());
        return subsProfileInfoDto;
    }

    public void updateSubscriberCameraInfo(String mdn,KnSubsCameraInfo cameraInfo, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateSubscriberCameraInfo()";
        knLogger.debug(methodName, "ENTRY: cameraInfo ", cameraInfo," mdn :",KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_UPDATE_SUBS_CAMERA_INFO);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_UPDATE_SUBS_CAMERA_INFO);
            pStmt.setString(1,cameraInfo.getIpIdentifier());
            pStmt.setString(2,cameraInfo.getCameraSerialId());
            pStmt.setString(3,mdn);
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed :", count);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e.getMessage());
            throw KnDbUtil.processException(e, "Failed to update subs camera info - " + e.getMessage()
                    , pttServerId, KnProvDAOSourceTypes.CAMERAINFO, QRY_UPDATE_SUBS_CAMERA_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void deleteSubscriberCameraInfo(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "deleteSubscriberCameraInfo()";
        knLogger.debug(methodName, "ENTRY: mdn :",KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_DELETE_SUBS_CAMERA_INFO);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_DELETE_SUBS_CAMERA_INFO);
            pStmt.setString(1,mdn);
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed :", count);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e.getMessage());
            throw KnDbUtil.processException(e, "Failed to delete subs camera info - " + e.getMessage()
                    , pttServerId, KnProvDAOSourceTypes.CAMERAINFO, QRY_DELETE_SUBS_CAMERA_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }
}
