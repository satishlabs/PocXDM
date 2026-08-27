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
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnQPPProfileInfoDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class KnQPPProfileInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnQPPProfileInfoDAO.class);

    private static final String CLASS = KnQPPProfileInfoDAO.class.getName();
    private String xdmsHome;

    KnQPPProfileInfoDAO(String xdmsHome) {
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

    public KnQPPProfileInfoDTO getQPPDetails(Integer apnId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getQPPDetails(apnId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : apnId - " + apnId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String query = null;
        KnQPPProfileInfoDTO qppProfileInfoDTO= null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            Connection conn = persisterTxn.getDBConnection(xdmsHome,true);
            query = "SELECT RECORDID, QPPPACKID, APNID, USERMODE, CALLTYPEID, " +
                    "ISDEFAULT, QPPPCRFPROFILEID " +
                    " FROM DG.QPP_PACKAGE WHERE APNID=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, apnId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            if (rs.next()) {
                qppProfileInfoDTO = new KnQPPProfileInfoDTO();
                qppProfileInfoDTO.setApnId(apnId);
                qppProfileInfoDTO.setRecordId(rs.getInt(1));
                qppProfileInfoDTO.setQpppackId(rs.getInt(2));
                qppProfileInfoDTO.setUserMode(rs.getInt(4));
                qppProfileInfoDTO.setCallTypeId(rs.getInt(5));
                qppProfileInfoDTO.setIsDefault(rs.getInt(6));
                qppProfileInfoDTO.setQpppcrfprofileId(rs.getInt(7));
            } else {
                knLogger.error( methodName, "Qpp Profile not found for apnId - " + apnId);
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Qpp Profile not found");
            }
            knLogger.debug( methodName, "QPP Profile found - " + qppProfileInfoDTO);
            if (ownedTxn) persisterTxn.save();
            return qppProfileInfoDTO;

        } catch (KnPersistenceException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "KnPersistenceException occured while retrieving Qpp Details for apnId - " + apnId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    xdmsHome, KnDAOSourceTypes.QPP_PACKAGE, query);

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "KnPersistenceException occured while retrieving Qpp Details for apnId - " +  apnId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected exception occured while retrieving Qpp Details for apnId - " + apnId + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve Qpp Details - " + e,
                    xdmsHome, KnDAOSourceTypes.QPP_PACKAGE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "inputApnId - " + apnId, "EXIT : QppProfile - " + qppProfileInfoDTO);
        }
    }


    public Integer getQPPPCRFProfileId(Integer apnId, Integer qpppackId,KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getQPPPCRFProfileId(apnId, qpppackId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : apnId - " + apnId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String query = null;
        Integer QPPPCRFProfileId= 0;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            Connection conn = persisterTxn.getDBConnection(xdmsHome,true);
            query = "SELECT QPPPCRFProfileId " +
                    " FROM DG.QPP_PACKAGE WHERE  QPPPackId = ? AND APNID = ? AND UserMode=2 AND CallTypeId=4";

            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, qpppackId);
            pstmt.setInt(2, apnId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            if (rs.next()) {
                QPPPCRFProfileId=rs.getInt(1);
            } else {
                QPPPCRFProfileId=null;
                //knLogger.error( methodName, "QPPPCRFProfileId not found for apnId - " + apnId);
                //throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "QPPPCRFProfileId not found");
            }
            knLogger.debug( methodName, "QPPPCRFProfileId found - " + QPPPCRFProfileId);
            if (ownedTxn) persisterTxn.save();
            return QPPPCRFProfileId;

        } catch (KnPersistenceException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "KnPersistenceException occured while retrieving QPPPCRFProfileId for apnId - " + apnId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    xdmsHome, KnDAOSourceTypes.QPP_PACKAGE, query);

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "KnPersistenceException occured while retrieving QPPPCRFProfileId for apnId - " +  apnId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected exception occured while retrieving QPPPCRFProfileId for apnId - " + apnId + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve QPPPCRFProfileId - " + e,
                    xdmsHome, KnDAOSourceTypes.QPP_PACKAGE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "inputApnId - " + apnId, "EXIT : QPPPCRFProfileId - " + QPPPCRFProfileId);
        }
    }

    public Integer getQPPPCRFProfileIdForApnId(Integer apnId,KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getQPPPCRFProfileIdForApnId(apnId,persisterTxn)";
        knLogger.debug(methodName, "ENTRY : apnId - " + apnId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String query = null;
        Integer QPPPCRFProfileId= 0;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            Connection conn = persisterTxn.getDBConnection(xdmsHome,true);
            query = "SELECT QPPPCRFProfileId " +
                    " FROM DG.QPP_PACKAGE WHERE APNID = ? AND IsDefault=1 AND UserMode=2 AND CallTypeId=4";

            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, apnId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            if (rs.next()) {
                QPPPCRFProfileId=rs.getInt(1);
            } else {
              //  knLogger.error( methodName, "QPPPCRFProfileId not found for apnId - " + apnId);
               // throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "QPPPCRFProfileId not found");
                QPPPCRFProfileId=null;
            }
            knLogger.debug( methodName, "QPPPCRFProfileId found - " + QPPPCRFProfileId);
            if (ownedTxn) persisterTxn.save();
            return QPPPCRFProfileId;

        } catch (KnPersistenceException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "KnPersistenceException occured while retrieving QPPPCRFProfileId for apnId - " + apnId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    xdmsHome, KnDAOSourceTypes.QPP_PACKAGE, query);

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "KnPersistenceException occured while retrieving QPPPCRFProfileId for apnId - " +  apnId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected exception occured while retrieving QPPPCRFProfileId for apnId - " + apnId + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve QPPPCRFProfileId - " + e,
                    xdmsHome, KnDAOSourceTypes.QPP_PACKAGE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "inputApnId - " + apnId, "EXIT : QPPPCRFProfileId - " + QPPPCRFProfileId);
        }
    }

}
