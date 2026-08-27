/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCPTTPermInfoDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by schandra on 26-12-2017.
 */
public class KnMCPTTPermInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMCPTTPermInfoDAO.class);

    public String pttServerId = null;

    public KnMCPTTPermInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String TABLE_NAME = "DG.MCPTT_PERM_INFO";
    public static final String CORPID = "CORPID";
    public static final String AUTHORIZED_MDN = "AUTHORIZED_MDN";
    public static final String TARGET_MDN = "TARGET_MDN";
    public static final String PERM_BITSET = "PERM_BITSET";
    public static final String DISCREET_ENABLED = "DISCREET_ENABLED";
    public static final String COMMA = ",";

    public static final String SELECT_MCPTT_PERM_INFO = "SELECT "+ AUTHORIZED_MDN + COMMA + TARGET_MDN + COMMA + PERM_BITSET + COMMA + DISCREET_ENABLED + " FROM "+ TABLE_NAME + " WHERE "+ AUTHORIZED_MDN+"=?";

    public static final String SELECT_MCPTT_PERM_INFO_ON_TARGET = "SELECT "+ AUTHORIZED_MDN + COMMA + TARGET_MDN + COMMA + PERM_BITSET + COMMA + DISCREET_ENABLED + " FROM "+ TABLE_NAME + " WHERE "+ AUTHORIZED_MDN + "=? AND "+ TARGET_MDN + "=?" ;

    public static final String UPDATE_DISCREEET_LISTENER_STATUS = "UPDATE " +TABLE_NAME +" SET "+DISCREET_ENABLED+"=? WHERE "+ AUTHORIZED_MDN+ "=? AND "+ TARGET_MDN+" IN ";

    public static final String SELECT_ALL_AUTHORIZED_MDNS_FOR_TARGET = "SELECT "+ AUTHORIZED_MDN + " FROM " +TABLE_NAME+ " WHERE " +TARGET_MDN+"=?";

    public static final String SELECT_MCPTT_PERM_INFO_ON_TARGETMDN = "SELECT "+ AUTHORIZED_MDN + COMMA + TARGET_MDN + COMMA + PERM_BITSET + COMMA + DISCREET_ENABLED + " FROM "+ TABLE_NAME + " WHERE "+ TARGET_MDN+"=?";

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

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMCPTTPermInfo(String,boolean, KnPersisterTxn)";
        knLogger.info(methodName, "AuthEntry :", KnGDPRTemplate.mdn(mdn));
        List<KnMCPTTPermInfoDTO> mcpttPermInfoDTOS = new ArrayList<>();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = SELECT_MCPTT_PERM_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);            pStatement = conn.prepareStatement(query);
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
            } else {
                // throw back exception
                knLogger.warn( methodName, "No  MCPTT permission info  found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No  MCPTT permission info found. Query ->" + query);
            }
            knLogger.info( methodName, "Returning MCPTT permission info  details - " ,  mcpttPermInfoDTOS);
        } catch (KnDAOException e) {
            if (e.getErrorCode().equals(KnErrorCodes.DAO.ROW_NOT_FOUND)) {
                knLogger.warn(methodName, "DAO Exception - " + e.getMessage());
            } else {
                knLogger.error(methodName, "DAO Exception - ", e);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ");
            throw KnDbUtil.processException(e, "Failed to retrieve MCPTT permission info for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_MCPTTPERMINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT :  ->", mcpttPermInfoDTOS);
        }
        return mcpttPermInfoDTOS;
    }

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfoOnTargetMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMCPTTPermInfoOnTargetMDN(String, KnPersisterTxn)";
        knLogger.info(methodName, "TargetMDN Entry :", KnGDPRTemplate.mdn(mdn));
        List<KnMCPTTPermInfoDTO> mcpttPermInfoDTOS = new ArrayList<>();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = SELECT_MCPTT_PERM_INFO_ON_TARGETMDN;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                KnMCPTTPermInfoDTO mcpttPermInfoDTO = null;
                do {
                    mcpttPermInfoDTO = new KnMCPTTPermInfoDTO();
                    mcpttPermInfoDTO.setAuthorizedMdn(rs.getString(AUTHORIZED_MDN).trim());
                    mcpttPermInfoDTO.setPermBitset(rs.getLong(PERM_BITSET));
                    mcpttPermInfoDTO.setDiscreetEnabled(rs.getInt(DISCREET_ENABLED));
                    mcpttPermInfoDTOS.add(mcpttPermInfoDTO);
                }while (rs.next());
            } else {
                // throw back exception
                knLogger.error( methodName, "No  MCPTT permission info  found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No  MCPTT permission info found. Query ->" + query);
            }
            knLogger.info( methodName, "Returning MCPTT permission info  details - " ,  mcpttPermInfoDTOS);
        }
        catch (KnDAOException e)
        {
            knLogger.error( methodName, "DAO Exception - " , e);
            throw e;
        }
        catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve  MCPTT permission info for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_MCPTTPERMINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT :  ->", mcpttPermInfoDTOS);
        }
        return mcpttPermInfoDTOS;
    }


    public KnMCPTTPermInfoDTO getMCPTTPermInfoOnTarget(String mdn, String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMCPTTPermInfo(String, KnPersisterTxn)";
        knLogger.info(methodName, "AuthEntry :", KnGDPRTemplate.mdn(mdn)," targetMdn -", KnGDPRTemplate.mdn(targetMdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnMCPTTPermInfoDTO mcpttPermInfoDTO = new KnMCPTTPermInfoDTO();

        try {
            query = SELECT_MCPTT_PERM_INFO_ON_TARGET;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            pStatement.setString(2, targetMdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");
            if (rs.next()) {
                mcpttPermInfoDTO.setTargetMdn(rs.getString(TARGET_MDN).trim());
                mcpttPermInfoDTO.setPermBitset(rs.getLong(PERM_BITSET));
                mcpttPermInfoDTO.setDiscreetEnabled(rs.getInt(DISCREET_ENABLED));
            } else {
                // throw back exception
                knLogger.error(methodName, "No  MCPTT permission info  found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No  MCPTT permission info found. Query ->" + query);
            }
            knLogger.info(methodName, "Returning MCPTT permission info  details - ", mcpttPermInfoDTO);
        }
        catch (KnDAOException e)
        {
            knLogger.error( methodName, "DAO Exception - " , e);
            throw e;
        }
        catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve  MCPTT permission info for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_MCPTTPERMINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT :  ->", mcpttPermInfoDTO);
        }
        return mcpttPermInfoDTO;
    }

    public KnMCPTTPermInfoDTO getMCPTTPermInfoByMdns(String mdn, String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMCPTTPermInfoByMdns()";
        knLogger.info(methodName, "AuthEntry :", KnGDPRTemplate.mdn(mdn)," targetMdn -",KnGDPRTemplate.mdn(targetMdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnMCPTTPermInfoDTO mcpttPermInfoDTO =null;

        try {
            query = SELECT_MCPTT_PERM_INFO_ON_TARGET;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            pStatement.setString(2, targetMdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");
            if (rs.next()) {
                mcpttPermInfoDTO = new KnMCPTTPermInfoDTO();
                mcpttPermInfoDTO.setTargetMdn(rs.getString(TARGET_MDN).trim());
                mcpttPermInfoDTO.setPermBitset(rs.getLong(PERM_BITSET));
                mcpttPermInfoDTO.setDiscreetEnabled(rs.getInt(DISCREET_ENABLED));
            } else {
                knLogger.error(methodName, "No  MCPTT permission info  found");
            }
            knLogger.info(methodName, "Returning MCPTT permission info  details - ", mcpttPermInfoDTO);
        }
        catch (KnDAOException e)
        {
            knLogger.error( methodName, "DAO Exception - " , e);
            throw e;
        }
        catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve  MCPTT permission info for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_MCPTTPERMINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT :  ->", mcpttPermInfoDTO);
        }
        return mcpttPermInfoDTO;
    }


    public void updateMCPTTDiscreetEnabled(String authMdn, List<String> targetMdns, int discreetListenerStatus,KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateAuthorizationDocDetails(String, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {

        	 StringBuffer buffer = new StringBuffer(200);
             buffer.append(UPDATE_DISCREEET_LISTENER_STATUS).append(KnDbUtil.convertListToStringBuffer(targetMdns));
             query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, discreetListenerStatus);
            pStatement.setString(2, authMdn.trim());
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : "  ,persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info( methodName, "Updated discreetListenerStatus for target mdn :  " +KnGDPRTemplate.mdnList(targetMdns));
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update discreetListenerStatus for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(authMdn));
        }
    }

    public List<String> getAllAuthMdsForTarget(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getAllAuthMdsForTarget(String, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(targetMdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        List<String> authMdns = new ArrayList<>();

        try {
            query = SELECT_ALL_AUTHORIZED_MDNS_FOR_TARGET;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, targetMdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            while (rs.next()) {
                authMdns.add(rs.getString(1).trim());
            }

        } catch (Exception e) {
            knLogger.error(methodName, " Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT");
        }
        knLogger.info(methodName, "Returning Authorized mdns - ",KnGDPRTemplate.mdnList(authMdns));
        return authMdns;
    }
}
