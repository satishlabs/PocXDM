/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpInfo.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        21-02-2011      7.0
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
import com.kodiak.common.exception.KnException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.kodiak.frameworks.dbfw.collectors.KnSqlJobCollector;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants.EXECUTOR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import com.kodiak.logger.KnLogger;

import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.replaceValInQry;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpInfoDAO.class);

    private String pttServerId;
    private static final String CORPID = "CORPID";
    private static final String EXTCORPID = "EXTCORPID";
    private static final String LARGE_GROUP_SUPPORTED = "LARGE_GROUP_SUPPORTED";

    private String CLASS = KnXDMCorpInfoDAO.class.getName();

    KnXDMCorpInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
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

    public int selectCorpId(String extCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectCorpId(String,boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : extCorpId - ", extCorpId, " readOnly :", readOnly);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int corpId = -1;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORPID);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, extCorpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                corpId = rs.getInt(1);
            } else {
                knLogger.error( methodName, "Corp Profile does not exists. corpId - " , corpId);
                throw new KnDBPersistenceException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND,
                        "corpId not found.", pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
            }
            return corpId;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the corpId- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while  retrieving the corpId  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "inputExtCorpId - " , extCorpId, "EXIT : corpId - " , corpId);
        }
    }

    public long getCorporateEtag(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorporateEtag(int,boolean, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : corpId - " , corpId, " readOnly :", readOnly);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Long etag = 0l;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORPORATE_ETAG);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                etag = rs.getLong(1);
            }
            return etag;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the corporate etag- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while  retrieving the corporate etag  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "EXIT: Etag", etag);
        }
    }

    public void updateCorpPairedContactListId(int corpId, int pairedContListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpPairedContactListId(int, int, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : corpId - " , corpId , " pairedContListId - " , pairedContListId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.UPDATE_CORP_PAIREDCONTACTLISTID);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //long currentTime = Calendar.getInstance().getTimeInMillis();
            if(pairedContListId >0 ) {
                pstmt.setInt(1, pairedContListId);
            }else{
                pstmt.setNull(1,java.sql.Types.INTEGER);
            }
            pstmt.setInt(2, corpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the corporate PairedContactListId- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while  updating the corporate PairedContactListId  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  updating the corporate PairedContactListId " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        }
    }

    
    public long updateCorporateEtag(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateEtag(int, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : corpId - " , corpId);
        //Go for async updation.
    	knLogger.debug( methodName,"To start async update");
    	Map<String,String> asyncInput = new HashMap<>();
    	asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, pttServerId);
    	asyncInput.put(KnDbSyncFwConstants.CORP_ID, String.valueOf(corpId));
    	knLogger.debug( methodName," Async inputs", asyncInput);
    	KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
    	int serviceType = EXECUTOR.ETAG_UPDATE.value();
    	collector.collect(serviceType, asyncInput);
    	knLogger.debug( methodName,"Updated to collector",serviceType);
    	long currentTime = Calendar.getInstance().getTimeInMillis();
    	return currentTime;
    }
    
    public long updateCorporateEtagSync(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateEtagSync(int, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : corpId - " , corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_CORP_ETAG);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            long currentTime = Calendar.getInstance().getTimeInMillis();
            pstmt.setLong(1, currentTime);
            pstmt.setInt(2, corpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully");
            return currentTime;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the corporate etag- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while  updating the corporate etag  - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "EXIT");
        }
    }

    
    public void updateCorporateEtagForIdList(Collection<Integer> corpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateEtagForIdList(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : corpIdList - " , corpIdList);
        List<Integer> corpIds = new ArrayList<Integer>(corpIdList);
        Collections.sort(corpIds);
        Map<String,String> asyncInput = new HashMap<>();
    	asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, pttServerId);
    	KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
    	int serviceType = EXECUTOR.ETAG_UPDATE.value();
    	for (int corpId : corpIds) {
    		asyncInput.put(KnDbSyncFwConstants.CORP_ID, String.valueOf(corpId));
    		collector.collect(serviceType, asyncInput);
        	knLogger.debug( methodName,"Updated to collector",serviceType, asyncInput);
    	}
    	knLogger.debug( methodName, "EXIT");
    }
    
    public void updateCorporateEtagForIdListSync(Collection<Integer> corpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateEtagForIdListSync(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : corpIdList - " , corpIdList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        List<Integer> corpIds = new ArrayList<Integer>(corpIdList);
        Collections.sort(corpIds);
        knLogger.debug( methodName, "corpIdList  after sorting  - ", corpIds);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_CORP_ETAG);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (int corpId : corpIds) {
                pstmt.setLong(1, Calendar.getInstance().getTimeInMillis());
                pstmt.setInt(2, corpId);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeBatch();
            knLogger.debug( methodName, "Query executed successfully");

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the corporate etag- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while  updating the corporate etag  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  updating the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "EXIT");
        }
    }

    /**
     * Takes input internal corpids and provides a map of external to internal corpid mapping
     * @param corpIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getCorpIdMap(Set<Integer> corpIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdMap(Set<Integer>,boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : corpIds - ", corpIds, " readOnly :", readOnly);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> corpIDMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORPIDMAP_BY_CORPID);
            String corpIdStr = formIntegerCommaSeperatedIdList(corpIds);
            query = replaceContactWithValue(query, CORPIDLIST, corpIdStr);
            knLogger.debug(methodName, "Executing query- ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                corpIDMap.put(rs.getString(EXTCORPID).trim(), rs.getInt(CORPID));
            }
            knLogger.info(methodName, "corpIDMap.size - ", corpIDMap.size());
            return corpIDMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the corpId- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  retrieving the corpId  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate data " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "corpIdMap - ", corpIDMap, "EXIT : corpIds - ", corpIds);
        }
    }

    /**
     * Takes input external corpids and provides a map of external to internal corpid mapping
     * @param extCorpIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getCorpIdMapByExtCorpIds(Set<String> extCorpIds, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getCorpIdMapByExtCorpIds(Set<String>,boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : extCorpIds - ", extCorpIds, " readOnly :", readOnly);
        Connection conn;
        PreparedStatement stmt = null;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        Map<String, Integer> corpIDMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORPIDMAP_BY_EXTCORPID);
            String corpIdStr = formCommaSeperatedIdList(extCorpIds);
            //query = replaceContactWithValue(query, EXTCORPIDLIST, corpIdStr);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(extCorpIds,query,"EXTCORPIDLIST");
            knLogger.debug(methodName, "Executing query- ", query);
            stmt = conn.prepareStatement(query);
            for(String ext : extCorpIds){
                stmt.setString(index++,ext);
            }
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                corpIDMap.put(rs.getString(EXTCORPID).trim(), rs.getInt(CORPID));
            }
            knLogger.info(methodName, "corpIDMap.size - ", corpIDMap.size());
            return corpIDMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the corpId- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  retrieving the corpId  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate data " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "corpIdMap - ", corpIDMap, "EXIT : extCorpIds - ", extCorpIds);
        }
    }

    public String selectCorporateFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectCorpId(String, boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : extCorpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        String corpFS = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPFS2_BY_CORPID);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                corpFS = rs.getString(1);
            } else {
                knLogger.error(methodName, "Corp Profile does not exists. corpFS - ", corpFS);
                throw new KnDBPersistenceException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND,
                        "corpFS not found.", pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
            }
            return corpFS;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving the corpFS- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occurred while  retrieving the corpFS  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "input CorpId - ", corpId, "EXIT : corpId - ", corpId);
        }
    }

    public void updateCorpFs(String corpFs,String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpFs(String, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId," corpFs " +corpFs);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_CORPFS2_BY_CORPID);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, KnGeneralUtil.getFeatureSet(corpFs));
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updating the corpFS- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occurred while  updating the corpFS  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the updating corpfs " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<Integer, Integer> getCorpIdAndLargeGroupFlagMap(Collection<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdAndLargeGroupFlagMap(Collection<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : corpIds - ", corpIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Integer> corpIDMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPID_LARGEGROUPFLAG);
            String corpIdStr = formIntegerCommaSeperatedIdList(corpIds);
            query = replaceContactWithValue(query, CORPIDLIST, corpIdStr);
            knLogger.debug(methodName, "Executing query- ", query);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                corpIDMap.put(rs.getInt(CORPID), rs.getInt(LARGE_GROUP_SUPPORTED));
            }
            knLogger.info(methodName, "corpIDMap.size - ", corpIDMap.size());
            return corpIDMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the corpId- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  retrieving the corpId  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate data " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public long getCorporateEtagOnCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorporateEtagOnCorpId(int,corpId, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : corpId - " , corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Long catAccessPermUpdateTSDbValue = 0l;
        try {
            query = "SELECT PERMSET_UPDATETIME FROM DG.POCCORPINFO WHERE CORPID = ?;";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                catAccessPermUpdateTSDbValue = rs.getLong(1);
            }
            return catAccessPermUpdateTSDbValue;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the corporate etag- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while  retrieving the corporate etag  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "EXIT: Etag", catAccessPermUpdateTSDbValue);
        }
    }

    public String updateCatAccessPermSet(String extCorpId, String catAccessPermSet, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCatAccessPermSet(String, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : extCorpId - ", extCorpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_CAT_PER_SET);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            long currentTime = Calendar.getInstance().getTimeInMillis();
            pstmt.setLong(1, currentTime);
            pstmt.setInt(2, Integer.parseInt(catAccessPermSet));
            pstmt.setString(3, extCorpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug(methodName, "Query executed successfully");
            return String.valueOf(currentTime);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the corporate etag- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  updating the corporate etag  - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT");
        }
    }

    public String selectCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectCorpFS(String, boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : extCorpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        String corpFS = null;
        try {
            query = "SELECT XDMCORPFS2_SET FROM DG.POCCORPINFO WHERE CORPID = ?;";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                corpFS = rs.getString(1);
            } else {
                knLogger.error(methodName, "Corp Profile does not exists. corpFS - ", corpFS);
                throw new KnDBPersistenceException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND,
                        "corpFS not found.", pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
            }
            return corpFS;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving the corpFS- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occurred while  retrieving the corpFS  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "input CorpId - ", corpId, "EXIT : corpId - ", corpId);
        }
    }

    public void updateDispMem(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateDispMem(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ", mdnList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_DISP_MEM);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            int count = pstmt.executeUpdate();
            knLogger.debug(methodName, "Query executed successfully" , count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the corporate etag- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  updating the corporate etag  - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT");
        }
    }
}
