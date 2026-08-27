/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpInfoDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 10, 2011      7.0
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
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpConfigInfoDto;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class KnCorpConfigInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpConfigInfoDAO.class);

    String SELECT_CORP_CONFIG_INFO = "SELECT CORPID, PARAMNAME, PARAMVALUE FROM DG.POCCORPCONFIGINFO WHERE CORPID=?";
    String SELECT_CORP_CONFIG_INFO_ON_PARAMNAME = "SELECT PARAMVALUE FROM DG.POCCORPCONFIGINFO WHERE CORPID=? AND  PARAMNAME = ?";
    String INSERT_CORP_CONFIG_INFO_ON_PARAMNAME = "INSERT INTO DG.POCCORPCONFIGINFO(CORPID,PARAMNAME,PARAMVALUE) Values (?,?,?)";
    String UPDATE_CORP_CONFIG_INFO_ON_PARAMNAME = "UPDATE DG.POCCORPCONFIGINFO  SET PARAMVALUE = ? WHERE CORPID = ? AND PARAMNAME = ?" ;
    String DELETE_CORP_CONFIG_INFO_ON_PARAMNAME = "DELETE FROM DG.POCCORPCONFIGINFO WHERE CORPID=? AND  PARAMNAME = ?";
    private String xdmsHome;

    KnCorpConfigInfoDAO(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public List<KnCorpConfigInfoDto> selectCorpConfigInfo(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectCorpConfigInfo(corpId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId - " + corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String query = null;
        List<KnCorpConfigInfoDto> corpConfigInfoDtoList = new ArrayList<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = SELECT_CORP_CONFIG_INFO;
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            if (rs.next()) {
                KnCorpConfigInfoDto corpConfigInfoDto = new KnCorpConfigInfoDto();
                corpConfigInfoDto.setCorpId(1);
                corpConfigInfoDto.setParamName(rs.getString(2));
                corpConfigInfoDto.setParamValue(rs.getString(3));
                corpConfigInfoDtoList.add(corpConfigInfoDto);
            }

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "KnPersistenceException occured while retrieving CorpProfile Details for corpId - " + corpId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    xdmsHome, KnDAOSourceTypes.POC_CORP_CONFIG_INFO, query);

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error(methodName, "KnPersistenceException occured while retrieving corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve corpConfigInfoDtoList - " + e,
                    xdmsHome, KnDAOSourceTypes.POC_CORP_CONFIG_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "inputCorpId - " + corpId, "EXIT : corpConfigInfoDtoList - " + corpConfigInfoDtoList);
        }
        return  corpConfigInfoDtoList;
    }

    public KnCorpConfigInfoDto selectCorpConfigInfo(int corpId, String paramName, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectCorpConfigInfo(corpId,paramName, persisterTxn)";
        knLogger.info(methodName, "ENTRY : CorpId - ", corpId, " readOnly :", readOnly);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String query = null;
        KnCorpConfigInfoDto corpConfigInfoDto = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);

            query = SELECT_CORP_CONFIG_INFO_ON_PARAMNAME;
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, paramName);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            if (rs.next()) {
                 corpConfigInfoDto = new KnCorpConfigInfoDto();
                corpConfigInfoDto.setCorpId(corpId);
                corpConfigInfoDto.setParamName(paramName);
                corpConfigInfoDto.setParamValue(rs.getString(1));
            }

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "KnPersistenceException occured while retrieving CorpProfile Details for corpId - " + corpId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    xdmsHome, KnDAOSourceTypes.POC_CORP_CONFIG_INFO, query);

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error(methodName, "KnPersistenceException occured while retrieving corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve corpConfigInfoDtoList - " + e,
                    xdmsHome, KnDAOSourceTypes.POC_CORP_CONFIG_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT : corpConfigInfoDtoList - ", corpConfigInfoDto);
        }
        return  corpConfigInfoDto;
    }

    public void insertCorpConfigInfo(KnCorpConfigInfoDto corpConfigInfoDto, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertCorpConfigInfo(KnCorpConfigInfoDto, KnPersisterTxn)";
        int corpId = corpConfigInfoDto.getCorpId();
        knLogger.info(methodName, "ENTRY : corpConfigInfoDto - " ,corpConfigInfoDto);
        PreparedStatement pStmt = null;
        Connection conn;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = INSERT_CORP_CONFIG_INFO_ON_PARAMNAME;
            pStmt = conn.prepareStatement(query);

            pStmt.setInt(1, corpConfigInfoDto.getCorpId());
            pStmt.setString(2, corpConfigInfoDto.getParamName());
            pStmt.setString(3, corpConfigInfoDto.getParamValue());
            int count = pStmt.executeUpdate();
            knLogger.info(methodName, " Inserted:", count);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnPersistenceException occured while inserting corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpConfigInfoDtoList - " + e,
                    xdmsHome, KnDAOSourceTypes.POC_CORP_CONFIG_INFO, query);
        }finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void updateCorpConfigInfo(KnCorpConfigInfoDto corpConfigInfoDto, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateCorpConfigInfo(KnCorpConfigInfoDto, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : CorpId - ",corpConfigInfoDto);
        PreparedStatement pStmt = null;
        String query = null;
        int corpId = corpConfigInfoDto.getCorpId();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = UPDATE_CORP_CONFIG_INFO_ON_PARAMNAME;
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, corpConfigInfoDto.getParamValue());
            pStmt.setInt(2, corpConfigInfoDto.getCorpId());
            pStmt.setString(3, corpConfigInfoDto.getParamName());
            int count=pStmt.executeUpdate();
            knLogger.info( methodName, "QUERY : Completed.",count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnPersistenceException occured while Updating corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving corpConfigInfoDtoList for corpId - " + corpId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpConfigInfoDtoList - " + e,
                    xdmsHome, KnDAOSourceTypes.POC_CORP_CONFIG_INFO, query);
        }finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void deleteCorpConfigInfo(KnCorpConfigInfoDto corpConfigInfoDto, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpConfigInfo(corpId,paramName, persisterTxn)";
        knLogger.info(methodName, "ENTRY : corpConfigInfoDto - " + corpConfigInfoDto);
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String query = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            query = DELETE_CORP_CONFIG_INFO_ON_PARAMNAME;
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpConfigInfoDto.getCorpId());
            pstmt.setString(2, corpConfigInfoDto.getParamName());
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error(methodName, "KnPersistenceException occured while deleting corpConfigInfoDtoList for corpId - " + corpConfigInfoDto.getCorpId() + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while deleting corpConfigInfoDtoList for corpId - " + corpConfigInfoDto.getCorpId() + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to delete corpConfigInfo - " + e,
                    xdmsHome, KnDAOSourceTypes.POC_CORP_CONFIG_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
}
