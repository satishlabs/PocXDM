/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnRecordingTargetInfoDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * ************************************************************************
 * <p>
 * File name:  KnRecordingInfoDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Sudhendu K Nayak           25-january-2023                  12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

/**
 * DAO for operation on RECORDING_TARGET_INFO
 */
public class KnRecordingInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnRecordingInfoDAO.class);

    public String pttServerId;

    public KnRecordingInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    private final String RECORDING_TARGET_INFO = "RECORDING_TARGET_INFO";

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }

    /**
     * @param recordingTargetInfoDTO dto
     * @param persisterTxn           txn
     * @throws KnDAOException exception
     */
    public void createRecordingInfoForTarget(Collection<KnRecordingTargetInfoDTO> recordingTargetInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "createRecordingInfoForTarget(KnIPRecordingTargetInfoDTO,KnPersisterTxn)";
        knLogger.info(methodName, "Entry : ", recordingTargetInfoDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        final String INSERT_RECORDING_TARGET_INFO = " INSERT INTO DG.RECORDING_TARGET_INFO (TARGET, PRIRECIP, PRIRECPORT, SECRECIP, SECRECPORT,RECTYPE) VALUES (?, ?, ?, ?,?,?); ";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(INSERT_RECORDING_TARGET_INFO);
            for (KnRecordingTargetInfoDTO recordInfo : recordingTargetInfoDTO) {
                pstmt.setString(1, recordInfo.getTarget());
                pstmt.setString(2, recordInfo.getPriRecIp());
                pstmt.setInt(3, recordInfo.getPriRecPort());
                pstmt.setString(4, recordInfo.getSecRecIp());
                pstmt.setInt(5, recordInfo.getSecRecPort());
                pstmt.setInt(6, recordInfo.getRecType());
                pstmt.addBatch();
            }

            int[] result = pstmt.executeBatch();
            knLogger.info(methodName, "Exit: Query executed successfully.", result);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed while creating recording info target ", pttServerId, RECORDING_TARGET_INFO, INSERT_RECORDING_TARGET_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * @param targetRecTypeMap targetRecTypeMap
     * @param persisterTxn     txn
     * @throws KnDAOException exception
     */
    public void updateRecordingInfoTargetByTarget(Map<String, Integer> targetRecTypeMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateRecordingInfoTargetByTarget(String,KnPersisterTxn)";

        knLogger.info(methodName, "Entry : ", KnGDPRTemplate.mdnSet(targetRecTypeMap.keySet()));
        Connection conn;
        PreparedStatement pstmt = null;
        final String MODIFY_RECORDING_TARGET_INFO = " UPDATE DG.RECORDING_TARGET_INFO SET RECTYPE =? WHERE TARGET =? ";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(MODIFY_RECORDING_TARGET_INFO);
            for (Map.Entry<String, Integer> targetRecType : targetRecTypeMap.entrySet()) {
                pstmt.setInt(1, targetRecType.getValue());
                pstmt.setString(2, targetRecType.getKey());
                pstmt.addBatch();
            }
            int[] updateCount = pstmt.executeBatch();
            knLogger.info(methodName, "Exit: Query executed successfully.", updateCount);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed while updating recording info target ", pttServerId, RECORDING_TARGET_INFO, MODIFY_RECORDING_TARGET_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }


    /**
     * @param targetList   targetList
     * @param persisterTxn txn
     * @throws KnDAOException exception
     */
    public void deleteRecordingInfoTargetByTarget(Collection<String> targetList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteRecordingInfoTargetByTarget(String,KnPersisterTxn)";
        knLogger.info(methodName, "Entry : ", KnGDPRTemplate.mdnList(targetList));
        Connection conn;
        PreparedStatement pstmt = null;
        final String DELETE_RECORDING_TARGET_INFO = " DELETE FROM DG.RECORDING_TARGET_INFO WHERE TARGET IN(TARGETLIST) ";

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            final String finalQuery = KnGeneralUtil.replaceContactWithValue(DELETE_RECORDING_TARGET_INFO, "TARGETLIST", KnGeneralUtil.formCommaSeperatedIdList(targetList));
            pstmt = conn.prepareStatement(finalQuery);
            int deleteCount = pstmt.executeUpdate();
            knLogger.info(methodName, "Exit: Query executed successfully.", deleteCount);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed while deleting recording info target ", pttServerId, RECORDING_TARGET_INFO, DELETE_RECORDING_TARGET_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * @param targetList   targetList
     * @param persisterTxn txn
     * @return Collection<KnRecordingTargetInfoDTO>
     * @throws KnDAOException exception
     */
    public Collection<KnRecordingTargetInfoDTO> getRecordingInfoTargetByTarget(Collection<String> targetList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getRecordingInfoTargetByTarget(Collection<String>,KnPersisterTxn)";
        knLogger.info(methodName, "Entry : ", KnGDPRTemplate.mdnList(targetList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        final String GET_RECORDING_TARGET_INFO_BY_TARGET = " SELECT TARGET, PRIRECIP, PRIRECPORT, SECRECIP, SECRECPORT,RECTYPE FROM DG.RECORDING_TARGET_INFO WHERE TARGET IN(TARGETLIST) ";
        int index = 1;
        Collection<KnRecordingTargetInfoDTO> recordingTargetInfoList = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(targetList, GET_RECORDING_TARGET_INFO_BY_TARGET, "TARGETLIST");
            pstmt = conn.prepareStatement(query);

            for (String target : targetList) {
                pstmt.setString(index++, target);
            }
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                recordingTargetInfoList.add(new KnRecordingTargetInfoDTO()
                        .setTarget(resultSet.getString("TARGET"))
                        .setPriRecIp(resultSet.getString("PRIRECIP"))
                        .setPriRecPort(resultSet.getInt("PRIRECPORT"))
                        .setSecRecIp(resultSet.getString("SECRECIP"))
                        .setSecRecPort(resultSet.getInt("SECRECPORT"))
                        .setRecType(resultSet.getInt("RECTYPE")));
            }
            knLogger.info(methodName, "Exit: Query executed successfully.", recordingTargetInfoList.size());

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed while getting recording info target ", pttServerId, RECORDING_TARGET_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return recordingTargetInfoList;
    }

    public void deleteRecordingInfoByCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteRecordingInfoByCorpId(String,KnPersisterTxn)";
        knLogger.info(methodName, "Entry : ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String target = corpId+"_%";
        final String DELETE_RECORDING_TARGET_INFO = " DELETE FROM DG.RECORDING_TARGET_INFO WHERE TARGET LIKE ?";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(DELETE_RECORDING_TARGET_INFO);
            pstmt.setString(1,target);
            int deleteCount = pstmt.executeUpdate();
            knLogger.info(methodName, "Exit: Query executed successfully.", deleteCount);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured - ", e);
            throw KnDbUtil.processException(e, "Failed while deleting recording info target ", pttServerId, RECORDING_TARGET_INFO, DELETE_RECORDING_TARGET_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
}
