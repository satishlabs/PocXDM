/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     15/4/14         7.7.0
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
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_EXTERNAL_SUBSCRIBER_DETAILS;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.MDNLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnExtSubscrInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupMemberListDAO.class);

    private String pttServerId;

    KnExtSubscrInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Unimplemented Methods");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "update", "Unimplemented Methods");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "delete", "Unimplemented Methods");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "select", "Unimplemented Methods");
        return null;
    }

    public Map<String, KnCorpSubscriberDTO> getExtSubsMap(List<String> extSubsList, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getExtSubsMap(List<String>,boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Enrty :" , extSubsList.size());
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, KnCorpSubscriberDTO> extSubscrProfIdMap = new HashMap<>(extSubsList.size());
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            var mdnListArray = new ArrayList<>(extSubsList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_SUBSCR_DETAILS);
                //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(extSubsList));
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                int index = 1;
                knLogger.debug(methodName, "Executing query", query);
                stmt = conn.prepareStatement(query);
                for (String extSub : subsList) {
                    stmt.setString(index++, extSub);
                }
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    int subType = rs.getInt(2);
                    if (subType == 1) {
                        subType = 2;
                    }
                    subscriberDTO.setSubsType(subType);
                    extSubscrProfIdMap.put(rs.getString(1).trim(), subscriberDTO);
                }
            }
            knLogger.debug(methodName, "EXIT : ", extSubscrProfIdMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getAllSubscribersGroupList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return extSubscrProfIdMap;
    }

    /**
     * This method returns the distinct profileId and profile details for MDN in extSubslist.
     * @param extSubsList
     * @param persisterTxn
     * @return  Map<Integer, KnExtProfileDetails>
     * @throws KnDAOException
     */
    public  List<Integer> getExtSubsrProfilelist(List<String> extSubsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtSubsrProfileMap(List<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "Enrty :" , extSubsList.size());
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        Set<Integer> profileIdList = new HashSet<>(extSubsList.size());
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            var mdnListArray = new ArrayList<>(extSubsList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_SUBSCR_DETAILS);
                //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(extSubsList));
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                knLogger.debug(methodName, "Executing query", query);
                int index = 1;
                stmt = conn.prepareStatement(query);
                for (String extSub : subsList) {
                    stmt.setString(index++, extSub);
                }
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    profileIdList.add(KnGeneralUtil.getSubsType(rs.getInt(2)));
                }
            }
            knLogger.debug( methodName, "EXIT : " , profileIdList);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getAllSubscribersGroupList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return new ArrayList<>(profileIdList);
    }

    public String getExtSubsrProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtSubsrProfile(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Enrty :" , KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Connection conn;
        String extMdn = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_EXTERNAL_SUBSCRIBER_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                extMdn = rs.getString(1);

            }
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to getExtSubsrProfile ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return extMdn;
    }

    private Map<Byte, Boolean> prepareFeatureSetMap(String featureBit){
        String methodName = "prepareFeatureSetMap(long)";
        knLogger.debug( methodName, "ENTRY - ", featureBit);
        List<Byte> bitList = new ArrayList<Byte>(5);
        bitList.add((byte)5);
        bitList.add((byte)9);
        bitList.add((byte)14);
        bitList.add((byte)15);
        bitList.add((byte)22);
        Map<Byte, Boolean> bitMap = new HashMap<Byte, Boolean>(5);
        for (Byte i : bitList){
            boolean flag = KnGeneralUtil.getFeatureBitValue(featureBit, i);
            bitMap.put(i, flag);
        }
        knLogger.debug( methodName, "bitMap - ", bitMap);
        return bitMap;
    }
    /**
     * This method returns the distinct profileId and profile details for MDN in extSubslist.
     * @param extSubsList
     * @param persisterTxn
     * @return  Map<String, List<Integer>>
     * @throws KnDAOException
     */
    public  Map<String, Integer> getExtSubsrProfilelistMap(List<String> extSubsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtSubsrProfileMap(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :", extSubsList.size());
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        var returnData = new HashMap<String, Integer>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_SUBSCR_DETAILS);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(extSubsList, query, "MDNLIST");
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.prepareStatement(query);
            for (String extSub : extSubsList) {
                stmt.setString(index++, extSub);
            }
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            String mdn = null;
            int subType = 0;
            while (rs.next()) {
                mdn = rs.getString("MDN");
                subType = KnGeneralUtil.getSubsType(rs.getInt("PROFILE_ID"));
                returnData.put(mdn, subType);
            }
            knLogger.debug(methodName, "EXIT : ", returnData);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getAllSubscribersGroupList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return returnData;
    }
}
