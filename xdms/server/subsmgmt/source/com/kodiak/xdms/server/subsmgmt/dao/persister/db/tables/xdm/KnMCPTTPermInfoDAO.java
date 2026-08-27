/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnMCPTTPermInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class KnMCPTTPermInfoDAO implements ITableDAO {


    private static final KnLogger knLogger = KnLogger.getLogger(KnMCPTTPermInfoDAO.class);

    private static final String className = KnMCPTTPermInfoDAO.class.getName();
    private String pttServerId;
    private static final String TABLENAME = "DG.MCPTT_PERM_INFO";
    public static final String AUTHORIZED_MDN = "AUTHORIZED_MDN";
    public static final String TARGET_MDN = "TARGET_MDN";
    public static final String PERM_BITSET = "PERM_BITSET";
    public static final String DISCREET_ENABLED = "DISCREET_ENABLED";
    public static final String COMMA = ",";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLENAME + " WHERE TARGET_MDN =? OR AUTHORIZED_MDN =?";
    private static final String SELECT_QUERY = "SELECT AUTHORIZED_MDN FROM " + TABLENAME + " WHERE TARGET_MDN = ?";
    private static final String UPDATE_TARGET_MDN = "UPDATE " + TABLENAME + " SET TARGET_MDN = ?" + " WHERE TARGET_MDN =?";
    private static final String UPDATE_AUTHORIZED_MDN = "UPDATE " + TABLENAME + " SET AUTHORIZED_MDN = ?" + " WHERE AUTHORIZED_MDN =?";
    private static final String DISABLE_DISCREET_ENABLED = "UPDATE " + TABLENAME + " SET DISCREET_ENABLED = 0" + " WHERE AUTHORIZED_MDN =?";
    private static final String UPDATE_PERM_BIT = "UPDATE " + TABLENAME + " SET PERM_BITSET = ?" + " WHERE AUTHORIZED_MDN =?";
    public static final String SELECT_MCPTT_PERM_INFO = "SELECT "+ AUTHORIZED_MDN + COMMA + TARGET_MDN + COMMA + PERM_BITSET + COMMA + DISCREET_ENABLED + " FROM "+ TABLENAME + " WHERE "+ AUTHORIZED_MDN+"=?";
    //Privacy Opt status starts
    public static final String SELECT_MCPTT_PERM_INFO_FOR_TARGET_MDN = "SELECT "+ AUTHORIZED_MDN + COMMA + TARGET_MDN + COMMA + PERM_BITSET + COMMA + DISCREET_ENABLED + " FROM "+ TABLENAME + " WHERE "+ TARGET_MDN+"=?";
    //Privacy Opt Status ends
    private static final String DELETE_TARGET_MDN_ENTRY = "DELETE FROM " + TABLENAME + " WHERE TARGET_MDN =? AND AUTHORIZED_MDN =?";


    public KnMCPTTPermInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public List<String> fetchAuthorizedMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "fetchAuthorizedMdn(Mdn,KnPersisterTxn)";
        String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<String> authMdnList = null;

        knLogger.info(methodName, "ENTRY: fetch Authorized MDN's for Target_MDN - ", KnGDPRTemplate.mdn(mdn), " Persist ", persisterTxn);

        try {
            query = SELECT_QUERY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(SELECT_QUERY);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            authMdnList = new ArrayList<>();
            if (rs != null) {
                while (rs.next()) {
                    authMdnList.add(rs.getString("AUTHORIZED_MDN").trim());
                }
            }
            knLogger.debug(methodName, "Authorized MDN fetched for Target MDN -", KnGDPRTemplate.mdn(mdn), " : ", authMdnList);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve mdn  list - "+ e.getMessage(), pttServerId, KnProvDAOSourceTypes.AUTHORIZATION_DOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        return authMdnList;
    }

    public void deleteMdn(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "deleteMdn(Mdn,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: delete if MDN present as target/authorized MDN", KnGDPRTemplate.mdn(mdn), " Persist ", persistTxn);

        try {
            query = DELETE_QUERY;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA,KnGDPRTemplate.mdn(mdn));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(DELETE_QUERY);
            pStmt.setString(1, mdn);
            pStmt.setString(2, mdn);
            knLogger.debug(methodName, "Query: Executing- ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to delete target/authorized mdn- "+ e.getMessage(), pttServerId, KnProvDAOSourceTypes.MCPTT_PERM_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void select(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "select(Mdn, persisterTxn)";
        String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        knLogger.debug(methodName, "ENTRY: select if MDN present as target MDN in MCPTT table", KnGDPRTemplate.mdn(mdn), " Persist ", persistTxn);

        try {
            query = SELECT_QUERY;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(mdn));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(SELECT_QUERY);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing- ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

        } catch (Exception e) {

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void updateTargetMdn(String oldMdn, String newMdn, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "updateTargetMdn(Mdn,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: updateTargetMdn for MCPTT profile oldMdn -", KnGDPRTemplate.mdn(oldMdn), "newMdn - ", KnGDPRTemplate.mdn(newMdn), " Persist ", persistTxn);

        try {
            query = UPDATE_TARGET_MDN;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(oldMdn));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(UPDATE_TARGET_MDN);
            pStmt.setString(1, newMdn);
            pStmt.setString(2, oldMdn);
            knLogger.debug(methodName, "Query: Executing- ", query, ", oldmdn - ", KnGDPRTemplate.mdn(oldMdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to update target mdn- "+ e.getMessage(), pttServerId, KnProvDAOSourceTypes.MCPTT_PERM_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void updateAuthorizedMdn(String oldMdn, String newMdn, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "updateAuthorizedMdn(Mdn,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: updateAuthorizedMdn for MCPTT profile oldMdn -", KnGDPRTemplate.mdn(oldMdn), "newMdn - ", KnGDPRTemplate.mdn(newMdn), " Persist ", persistTxn);

        try {
            query = UPDATE_AUTHORIZED_MDN;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(oldMdn));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(UPDATE_AUTHORIZED_MDN);
            pStmt.setString(1, newMdn);
            pStmt.setString(2, oldMdn);
            knLogger.debug(methodName, "Query: Executing- ", query, ", oldmdn - ", KnGDPRTemplate.mdn(oldMdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to update target mdn- "+ e.getMessage(), pttServerId, KnProvDAOSourceTypes.MCPTT_PERM_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void disableDiscreetEnabled(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "disableDiscreetEnabled(mdn persisteerTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: Disable discreet_enable bit for all authorized mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            query = DISABLE_DISCREET_ENABLED;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(mdn));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(DISABLE_DISCREET_ENABLED);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing- ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to update discreet enable for mdn- "+ e.getMessage(), pttServerId, KnProvDAOSourceTypes.MCPTT_PERM_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void updatePermBit(String mdn, long permBit, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updatePermBit(mdn permBit persisteerTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: updatePermBit bit for  authorized mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            query = UPDATE_PERM_BIT;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(mdn));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(UPDATE_PERM_BIT);
            pStmt.setLong(1, permBit);
            pStmt.setString(2, mdn);
            knLogger.debug(methodName, "Query: Executing- ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn), " perm bit -", permBit);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to update perm bit for mdn- "+ e.getMessage(), pttServerId, KnProvDAOSourceTypes.MCPTT_PERM_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

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


    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMCPTTPermInfo(String, KnPersisterTxn)";
        knLogger.info(methodName, "AuthEntry :", KnGDPRTemplate.mdn(mdn));
        List<KnMCPTTPermInfoDTO> mcpttPermInfoDTOS = new ArrayList<>();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = SELECT_MCPTT_PERM_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                KnMCPTTPermInfoDTO mcpttPermInfoDTO = null;
                do {
                    mcpttPermInfoDTO = new KnMCPTTPermInfoDTO();
                    mcpttPermInfoDTO.setTargetMdn(rs.getString(TARGET_MDN).trim());
                    mcpttPermInfoDTO.setPermBitset(rs.getLong(PERM_BITSET));
                    mcpttPermInfoDTO.setDiscreetEnabled(rs.getInt(DISCREET_ENABLED));
                    mcpttPermInfoDTOS.add(mcpttPermInfoDTO);
                }while (rs.next());
            }
            knLogger.info( methodName, "Returning MCPTT permission info  details - " ,  mcpttPermInfoDTOS);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve  MCPTT permission info for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_MCPTTPERMINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT :  ->", mcpttPermInfoDTOS);
        }
        return mcpttPermInfoDTOS;
    }

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfoForTargetMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMCPTTPermInfoForTargetMdn(String, KnPersisterTxn)";
        knLogger.info(methodName, "AuthEntry :", KnGDPRTemplate.mdn(mdn));
        List<KnMCPTTPermInfoDTO> mcpttPermInfoDTOS = new ArrayList<>();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = SELECT_MCPTT_PERM_INFO_FOR_TARGET_MDN;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                KnMCPTTPermInfoDTO mcpttPermInfoDTO = null;
                do {
                    mcpttPermInfoDTO = new KnMCPTTPermInfoDTO();
                    mcpttPermInfoDTO.setTargetMdn(rs.getString(TARGET_MDN).trim());
                    mcpttPermInfoDTO.setAuthorizedMdn(rs.getString(AUTHORIZED_MDN).trim());
                    mcpttPermInfoDTO.setPermBitset(rs.getLong(PERM_BITSET));
                    mcpttPermInfoDTO.setDiscreetEnabled(rs.getInt(DISCREET_ENABLED));
                    mcpttPermInfoDTOS.add(mcpttPermInfoDTO);
                }while (rs.next());
            }
            knLogger.info( methodName, "Returning MCPTT permission info  details - " ,  mcpttPermInfoDTOS);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve  MCPTT permission info for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_MCPTTPERMINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT :  ->", mcpttPermInfoDTOS);
        }
        return mcpttPermInfoDTOS;
    }

    public void deleteTargetEntry(String authMdn,String targetMdn, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "deleteTargetEntry(String,String,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: delete if MDN present as target & authorized MDN", authMdn, KnGDPRTemplate.mdn(targetMdn)," Persist ", persistTxn);

        try {
            query = DELETE_TARGET_MDN_ENTRY;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(DELETE_TARGET_MDN_ENTRY);
            pStmt.setString(1, targetMdn);
            pStmt.setString(2, authMdn);
            knLogger.debug(methodName, "Query: Executing- ", query, ", mdn - ", KnGDPRTemplate.mdn(targetMdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to delete target/authorized mdn- "+ e.getMessage(), pttServerId, KnProvDAOSourceTypes.MCPTT_PERM_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }
}