/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formIntegerCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMCorpTGSSGrpDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpTGSSGrpDAO.class);

    private String pttServerId = null;

    KnXDMCorpTGSSGrpDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

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

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public Map<String, Integer> getTGSSDocumentEtag(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTGSSDocumentEtag(List<String>, KnPersisterTxn )";
        knLogger.debug(methodName, "mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        Map<String, Integer> mdnEtagMap = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_TGSS_DOCUMENT_ETAG);
            Collection<String> mdnList = new ArrayList<>();
            for (String mdn : mdns) {
                if (mdn != null) {
                    mdnList.add(mdn);
                }
            }
            knLogger.debug(methodName, "mdnList - ", KnGDPRTemplate.mdnList(mdnList));
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdns,query,"MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for(String mdn : mdns){
                pStmt.setString(index++,mdn);
            }
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                mdnEtagMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            knLogger.debug(methodName, "EXIT. mdnEtagMapSize - ", mdnEtagMap.size());
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while retriving etags for mdns" + e,
                    pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return mdnEtagMap;
    }

    /**
     * @param grpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getMdnsForTGSSGrp(Integer grpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnsForTGSSGrp(int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupId - ", grpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        List<String> mdnList = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MDNS_FOR_TGSS_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, grpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                mdnList.add(rs.getString(1).trim());
            }
            return mdnList;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  retrieving Mdns For TGSS Grp  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving Mdns For TGSS Grp " + e,
                    pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT: mdnList", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        }
    }

    /**
     * @param grpIdList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteTGSSGrps(List<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteTGSSGrps(List, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : grpIdList - ", grpIdList);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_TGSS_GROUP_FOR_MDNLIST);
            Collection<String> grpIds = new ArrayList<>();
            for (Integer grp : grpIdList) {
                if (grp != null) {
                    grpIds.add(String.valueOf(grp));
                }
            }
            knLogger.debug(methodName, "grpIds - ", grpIds);
            query = replaceContactWithValue(query, GROUPIDS, formCommaSeperatedIdList(grpIds));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Exeuting query - ", query);
            stmt.executeQuery(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");

            knLogger.debug(methodName, "Query executed successfully");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  deleteting Mdns For TGSS Grps  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  deleteting Mdns For TGSS Grps " + e,
                    pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT: deleteTGSSGrps");
        }
    }

    /**
     * @param mdn
     * @param etag
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateSubsTGSSGrpEtag(String mdn, Integer etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsTGSSGrpEtag(mdn,etag, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn), " etg-", etag);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SUBS_TGSS_GRP_ETAG);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, etag);
            pstmt.setString(2, mdn.trim());
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            int cnt = pstmt.executeUpdate();
            knLogger.debug(methodName, "Query executed successfully " + cnt);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  updating etag For mdn  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while updating etag for mdn" + e,
                    pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    /**
     *
     * @param grpId
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteTGSSGrpsMdn(Integer grpId,List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        {
            String methodName = "deleteTGSSGrpsMdn(Integer,List, KnPersisterTxn )";
            knLogger.debug(methodName, "ENTRY : grpId - ", grpId," mdnList - ",mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
            Connection conn;
            PreparedStatement pStmt = null;
            String query = null;
            ResultSet rs = null;
            int index = 1;
            try {
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(DELETE_TGSS_GROUP_FOR_MDN);
                Collection<Integer> grpIds = new ArrayList<>();
                        grpIds.add(grpId);
                knLogger.debug(methodName, "grpIds - ", grpIds);
                //query = replaceContactWithValue(query, GROUPIDS,formIntegerCommaSeperatedIdList(grpIds));
                //query = replaceContactWithValue(query, MDNLIST,formCommaSeperatedIdList(mdnList));
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks("GROUPIDS",grpIds,query);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                pStmt = conn.prepareStatement(query);
                for(Integer grp : grpIds){
                    pStmt.setInt(index++,grp);
                }
                for(String mdn : mdnList){
                    pStmt.setString(index++,mdn);
                }
                knLogger.debug(methodName, "Exeuting query - ", query);
                pStmt.executeQuery();
                knLogger.debug(methodName, "Executing query - ", "'", query, "'");

                knLogger.debug(methodName, "Query executed successfully");
            } catch (Exception e) {
                knLogger.error(methodName, "Unexpected Exception  occured while deleteting Mdns-"+mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList)+" For Grp - "+grpId
                        + e);
                throw KnDbUtil.processException(e, "Failed while  deleteting Mdns For TGSS Grps " + e,
                        pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pStmt);
                knLogger.debug(methodName, "EXIT: deleteTGSSGrpsMdn");
            }
        }

    }
}
