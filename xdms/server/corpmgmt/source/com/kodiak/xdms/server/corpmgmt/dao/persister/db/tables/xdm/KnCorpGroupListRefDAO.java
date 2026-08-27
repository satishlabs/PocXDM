/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupListRefDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 4, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import com.kodiak.logger.KnLogger;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.INSERT_INTO_CORP_LIST_REF;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.SUBLISTID;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formIntegerCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;


public class KnCorpGroupListRefDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupListRefDAO.class);

    private static final String CLASS = KnCorpGroupListRefDAO.class.getName();
    private String xdmsHome;

    KnCorpGroupListRefDAO(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Unimplemented Methods");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "update", "Unimplemented Methods");
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "delete", "Unimplemented Methods");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "select", "Unimplemented Methods");
        return null;
    }

      private int getSize(Collection dataList) {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    public void insert(int groupId, Collection<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "insert(int, Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : sublistIds size - ", getSize(sublistIds));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            //query = "INSERT INTO DG.CORPGROUP_LISTREF VALUES(?, ?)";
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_CORP_LIST_REF);
            knLogger.debug( methodName, "Executing query -" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);             for (int sublistId : sublistIds) {
                pstmt.setInt(1, groupId);
                pstmt.setInt(2, sublistId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting sublist refrence for group - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting sublist refrence for group - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting sublist refrence for group " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public void deleteAllCorpGroupListRef(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllCorpGroupListRef(Collection<Integer>,KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : sublistIdsList size - " , getSize(sublistIdsList));
        Connection conn;
        Statement stmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_GROUP_LIST_REF);
            knLogger.debug( methodName, "Executing query -  " , query);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIdsList));
            knLogger.debug( methodName, "Executing query -  " , query);
             conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(xdmsHome, false);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query -  " , query);
            stmt.executeUpdate(query);
            knLogger.debug( methodName, "Exit: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting corp group list reference - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting corp group list reference - " , e);
            throw KnDbUtil.processException(e, "Failed to deleteAllCorpGroupListRef " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        }
        finally {
            KnDbUtil.closeStatement(stmt);
        }
    }

    public void addBulkSublistsToGroup(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "addBulkSublistsToGroup()";
        knLogger.debug(methodName, "Entry : ", getSize(groupInfoPersistDTOList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_CORP_LIST_REF);
            knLogger.debug( methodName, "Executing query -", query);
            pstmt = conn.prepareStatement(query);
            for (KnCorpGroupInfoPersistDTO groupInfoPersistDTO : groupInfoPersistDTOList) {
                pstmt.setInt(1, groupInfoPersistDTO.getGroupId());
                pstmt.setInt(2, groupInfoPersistDTO.getGroupMemberListId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while inserting into the group info table " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }
}
