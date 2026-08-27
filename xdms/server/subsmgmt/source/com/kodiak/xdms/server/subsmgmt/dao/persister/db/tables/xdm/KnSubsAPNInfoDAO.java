/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KnSubsAPNInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAPNInfoDAO.class);
    private String pttServerId;

    private static final String MDN = "MDN";
    private static final String APN_ID = "APNID";
    private static final String MDN_LIST = "MDNLIST";
    private static final String TABLENAME = "DG.SUBSCRAPNINFO";

    private static final String ADD_SUB_APN_INFO = "insert into DG.SUBSCRAPNINFO values (?,?)";
    private static final String UPDATE_SUB_APN_ID = "update DG.SUBSCRAPNINFO set APNID = ? where mdn = ?";
    private static final String DELETE_SUB_APN_INFO = "delete from DG.SUBSCRAPNINFO where mdn = ?";
    private static final String SELECT_SUB_APN_ID = "SELECT " + MDN + ", "+ APN_ID +" FROM " + TABLENAME + " WHERE " + MDN + " IN (MDNLIST)";
    private static final String UPDATE_APN_ID_FOR_MDN_LIST = "UPDATE DG.SUBSCRAPNINFO SET APNID = ? WHERE MDN IN (MDNLIST)";

    public KnSubsAPNInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    //  @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "insert(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug(methodName, "Method not Implemented");
    }

    //@Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "update(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug(methodName, "Method not Implemented");
    }

    //@Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "delete(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug(methodName, "Method not Implemented");
    }

    //@Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "select(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug(methodName, "Method not Implemented");
        return null;
    }


    public void addSubApn(String mdn, int apnId, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "addSubApn(String,int, KnPesisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.entry(methodName, KnGDPRTemplate.mdn(mdn), apnId, persistTxn);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(ADD_SUB_APN_INFO);
            pStmt.setString(1, mdn);
            pStmt.setInt(2, apnId);
            knLogger.debug(methodName, "Query: Executing - ", ADD_SUB_APN_INFO);
            int value = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ", value);
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to add Subscriber APN Profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, ADD_SUB_APN_INFO);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to add Subscriber APN Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, ADD_SUB_APN_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName);

    }

    public void updateSubApnId(String mdn, int apnId, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "updateSubApnId(String,int, KnPesisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.entry(methodName, KnGDPRTemplate.mdn(mdn), apnId, persistTxn);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(UPDATE_SUB_APN_ID);
            pStmt.setInt(1, apnId);
            pStmt.setString(2, mdn);
            knLogger.debug(methodName, "Query: Executing - ", UPDATE_SUB_APN_ID);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to add Subscriber APN Profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, UPDATE_SUB_APN_ID);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to add Subscriber APN Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, UPDATE_SUB_APN_ID);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName);

    }

    public Map<String, Integer> selectSubApn(List<String> mdnList, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "selectSubApn(String,boolean, KnPesisterTxn)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdnList), persistTxn);
        Map<String, Integer> apnProfile = new HashMap<>();
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,SELECT_SUB_APN_ID,"MDNLIST");
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for(String mdn : mdnList)
            	pStmt.setString(index++, mdn);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                apnProfile.put(rs.getString(1).trim(), rs.getInt(2));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber APN Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, SELECT_SUB_APN_ID);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "Exit");
        return apnProfile;
    }

        public void deleteSubApnInfo(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "deleteSubApnInfo(String, KnPesisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.entry(methodName, KnGDPRTemplate.mdn(mdn), persistTxn);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(DELETE_SUB_APN_INFO);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing - ", DELETE_SUB_APN_INFO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to delete Subscriber APN Profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, DELETE_SUB_APN_INFO);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Subscriber APN Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, DELETE_SUB_APN_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName);

    }


    public void deleteSubApnInfo(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "deleteSubApnInfo(List<String>, KnPesisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdnList), persistTxn);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(DELETE_SUB_APN_INFO);
            for (String mdn : mdnList) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing - ", DELETE_SUB_APN_INFO);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to delete Subscribers APN Profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, DELETE_SUB_APN_INFO);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Subscriber APN Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, DELETE_SUB_APN_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName);

    }

    public void addSubApn(List<String> mdns, int apnId, KnPersisterTxn persistTxn) throws KnDAOException{

        final String methodName = "addSubApn(String,int, KnPesisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdns), apnId, persistTxn);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(ADD_SUB_APN_INFO);
            for (String mdn : mdns) {
                pStmt.setInt(2, apnId);
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing - ", ADD_SUB_APN_INFO);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed "     );
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to add Subscriber APN Profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, ADD_SUB_APN_INFO);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to add Subscriber APN Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, ADD_SUB_APN_INFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName);

    }

    public void updateSubApnIdForMdnList(List<String> mdnList, int apnId, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "DAO.updateSubApnIdForMdnList ";
        Connection conn;
        PreparedStatement pStmt = null;
        int index = 2;
        knLogger.debug(methodName, "mdnList :", KnGDPRTemplate.mdnList(mdnList), " apnId :", apnId);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            String query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,UPDATE_APN_ID_FOR_MDN_LIST,"MDNLIST");
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, apnId);
            for(String mdn : mdnList)
            	pStmt.setString(index++, mdn);
            knLogger.debug(methodName, "Query: Executing - ", query);
            pStmt.executeUpdate();
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to update Subscriber APN Profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, UPDATE_APN_ID_FOR_MDN_LIST);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Subscriber APN Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, UPDATE_APN_ID_FOR_MDN_LIST);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "Query: Executed successfully :");
    }
}