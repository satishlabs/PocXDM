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
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnXDMCorpGroupMemberListDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupMemberListDAO.class);
    private static final String TABLENAME = "DG.CORPGROUPMEMBERLIST";
    private String pttServerId;
    private static final String MDN = "MEMBERMDN";
    private static final String MDN_LIST = "MDNLIST";
    private static final String IS_AFFILIATION_ENABLED = "IS_AFFILIATION_ENABLED";
    public static final String QRY_UPDATE_AFFILIATION = "UPDATE " + TABLENAME + " SET " + IS_AFFILIATION_ENABLED + " = ?"
            + " WHERE " + MDN + " = ?";

    public static final String QRY_SELECT_AUTHORIZED_GROUP = "SELECT CGML.CORPGROUPID, CGML.MEMBERMDN FROM DG.CORPGROUPMEMBERLIST CGML, DG.CORPGROUPINFO CGI WHERE CGI.CORPGROUPID=CGML.CORPGROUPID AND CGML.MEMBERMDN IN (" + MDN_LIST + ")";

    public static final String QRY_SELECT_AUTHORIZED_PRE_CONFIG_GROUP = "SELECT CORPGROUPID FROM DG.CORPGROUPINFO WHERE CORPID =? AND IS_PRECONFIG_GRP = 1";

    public KnXDMCorpGroupMemberListDAO(String pttServerId) {
        this.pttServerId = pttServerId;
        // TODO Auto-generated constructor stub
    }

    public void updateAffForCorpGrpMemList(String mdn, int isAffiliationEnabled, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAffForCorpGrpMemList(KnSubsProfilePersistDTO, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : " + KnGDPRTemplate.mdn(mdn) + isAffiliationEnabled);
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {
            query = QRY_UPDATE_AFFILIATION;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, isAffiliationEnabled);
            pStatement.setString(2, mdn);
            knLogger.debug(methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info(methodName, "Updated count:  " + count);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update Affiliation Flag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update Affiliation Flag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(mdn));
        }
    }
    
    public void updateAffForCorpGrpMemList(Map<String, String> mdnIsAffiliationEnabledMap, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAffForCorpGrpMemList(Map<String, String>, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : " + "mdnIsAffiliationEnabledMap" + mdnIsAffiliationEnabledMap);
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {
            query = QRY_UPDATE_AFFILIATION;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            for (Map.Entry mdnInfo : mdnIsAffiliationEnabledMap.entrySet()) {
                pStatement.setInt(1, Integer.parseInt(mdnInfo.getValue().toString()));
                pStatement.setString(2, mdnInfo.getKey().toString());
                pStatement.addBatch();
            }
            int[] executeBatch = pStatement.executeBatch();
            knLogger.debug(methodName, "QUERY : Executed " + query + ", persisterTxn : " +
                    persisterTxn);
            
            knLogger.debug(methodName, "executeBatchCount " + executeBatch);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update Affiliation Flag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update Affiliation Flag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : mdnIsAffiliationEnabledMap ->" + KnGDPRTemplate.mdnMap(mdnIsAffiliationEnabledMap));
        }
    }


    public Map<String,List<Integer>> selectCorpGroupId(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectCorpGroupId(corpId, List<String>, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList - ",KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpGroupId = 0;
        String memberMDN = null;
        List<Integer> corpgroupIds = null;
        Map<String, List<Integer>> memberGroupIdsMap = new HashMap<>();
        try {
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = QRY_SELECT_AUTHORIZED_GROUP.replaceAll(MDN_LIST, KnGeneralUtil.getComSepList(mdnList));
            knLogger.debug(methodName,"QRY_SELECT_AUTHORIZED_GROUP Query ",query);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");
            while (rs.next()) {
                corpGroupId = rs.getInt(1);
                memberMDN = rs.getString(2).trim();
                if (memberGroupIdsMap.get(memberMDN) == null) {
                    corpgroupIds = new ArrayList<>();
                    corpgroupIds.add(corpGroupId);
                    memberGroupIdsMap.put(memberMDN, corpgroupIds);
                } else {
                    List<Integer> cpgroupIds = memberGroupIdsMap.get(memberMDN);
                    cpgroupIds.add((corpGroupId));
                }
            }
            knLogger.debug(methodName, "MemberGroupMap  - " + memberGroupIdsMap);
            return memberGroupIdsMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnPersistenceException occured while retrieving corpGroupId for mdnList - " + KnGDPRTemplate.mdnList(mdnList) + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving corpGroupId for mdnList" + KnGDPRTemplate.mdnList(mdnList) +", " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpGroupId - " + e,
                    pttServerId, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT : corpGroupIds - " + corpgroupIds);
        }
    }

   public List<Integer> selectOwnerPreConfigCorpGroupId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectOwnerPreConfigCorpGroupId(mdn, List<String>, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ",corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpGroupId = 0;
        int groupShared = 0;
        List<Integer> groupIdList = new ArrayList<>();
        try {
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = QRY_SELECT_AUTHORIZED_PRE_CONFIG_GROUP;
            knLogger.debug(methodName,"QRY_SELECT_AUTHORIZED_GROUP Query ",query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,  corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");
            while (rs.next()) {
                corpGroupId = rs.getInt(1);
                groupIdList.add(corpGroupId);
            }
            knLogger.debug(methodName, "groupIdList  - " + groupIdList);
            return groupIdList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnPersistenceException occured while retrieving corpGroupId for corpId - " + corpId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving corpGroupId for mdnList" + corpId +", " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpGroupId - " + e,
                    pttServerId, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT : groupIdList - " + groupIdList);
        }
    }


}
