/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpListDistInfoDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        26-01-2011      7.0
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
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpSublistListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpListDistInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpListDistInfoDAO.class);

    private String CLASS = KnXDMCorpListDistInfoDAO.class.getName();

    public String pttServerId = null;

    public KnXDMCorpListDistInfoDAO(String pttServerId) {
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

    public Collection<Integer> getSubsMappedSublistId(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsMappedSublistId(IPersistenceDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : persistenceDTO - ", persistenceDTO);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<Integer> sublistIdMapped = new ArrayList<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SUBSCRIBER_MAPPED_SUBLIST_FROM_CORP_LIST);
            KnCorpSublistListPersistDTO sublistListDTO = new KnCorpSublistListPersistDTO();
            if (persistenceDTO instanceof KnCorpSublistListPersistDTO) {
                sublistListDTO = (KnCorpSublistListPersistDTO) persistenceDTO;
            }
            knLogger.debug( methodName, "sublistListDTO :  " , sublistListDTO.toString());
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistList = formIntegerCommaSeperatedIdList(sublistListDTO.getRemovedSublistIds());
            query = replaceContactWithValue(query, SUBLISTID, sublistList);
            query = KnDbUtil.replaceValInQry(query, sublistListDTO.getMdn());
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                int sublistId = rs.getInt(1);
                sublistIdMapped.add(sublistId);
            }
            return sublistIdMapped;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving subscribers mapped sublist ids - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving subscribers mapped sublist ids - " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while retrieving subscribers mapped sublist ids " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : subscriber Mapped sublist list size is -", sublistIdMapped.size());
        }
    }

    public void pushSublistToSubscriber(Collection<Integer> addedSubListId, KnCorpSubscriberDTO subsDTO,
                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "pushSublistToSubscriber(Collection<Integer>, KnCorpSubscriberDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : addedSubListId - ", addedSubListId, " ,subsDTO - ", subsDTO.toString());
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_LIST_DIST_INFO);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            String subscribersMdn = subsDTO.getMdn();
            for (int sublistId : addedSubListId) {
                pstmt.setInt(1, sublistId);
                pstmt.setString(2, subscribersMdn);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executinng query -  " , "'" , query , "'");
            pstmt.executeBatch();             knLogger.debug(methodName, "EXIT : Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while pushing sublist to subscriber - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while pushing sublist to subscriber - " , e);
            throw KnDbUtil.processException(e, "Failed while pushing sublist to subscriber - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        }
        finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void removeSublistMappingForSubscriber(Collection<Integer> removeSubListId, KnCorpSubscriberDTO subsDTO,
                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "removeSublistMappingForSubscriber(Collection<Integer> , KnCorpSubscriberDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : removeSubListId - ", removeSubListId, ", subsDTO - ", subsDTO.toString(), ", txn - ", persisterTxn);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_LIST_FOR_SUBSCRIBER);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<String> subscribersMdnList = new ArrayList<String>();
            if(null != subsDTO.getMdnList() && !subsDTO.getMdnList().isEmpty()){
                subscribersMdnList.addAll(subsDTO.getMdnList());
            } else {
                subscribersMdnList.add(subsDTO.getMdn());
            }
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executinng query -  " , "'" , query , "'");
            for (String subscribersMdn : subscribersMdnList) {
                for (int sublistId : removeSubListId) {
                    pstmt.setString(1, subscribersMdn);
                    pstmt.setInt(2, sublistId);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while remove sublist for subscriber - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while remove sublist for subscriber - " , e);
            throw KnDbUtil.processException(e, "Failed to removeSublistMappingForSubscriber " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        }finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<Integer, Collection<String>> selectSublistPushedFrmList(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectSublistPushedFrmList(KnIPCorpSublistSubscDistDTO,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : distDTO - ", distDTO);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        Map<Integer, Collection<String>> sublistMapList = new HashMap<Integer, Collection<String>>();
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SUBLIST_ALREADY_PUSHED_TO_SUBSCRIBERS);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistListStr = formIntegerCommaSeperatedIdList(distDTO.getSublistIds());
            query = replaceContactWithValue(query, SUBLISTID, sublistListStr);
            //pstmt.setString(1, sublistListStr);
            String mdnListStr = formCommaSeperatedIdList(distDTO.getMdnList());
            query = replaceContactWithValue(query, MDNLIST, mdnListStr);
            //pstmt.setString(2, mdnListStr);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query -  ", "'", query, "'");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int sublistId = rs.getInt(1);
                Collection<String> mdnList = sublistMapList.get(sublistId);
                if (mdnList == null) {
                    mdnList = new ArrayList<String>();
                }
                mdnList.add(rs.getString(2).trim());
                sublistMapList.put(sublistId, mdnList);
            }
            knLogger.debug( methodName, "Query executed successfully");
            return sublistMapList;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the sublist pushed to subscriber - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the sublist pushed to subscriber - " , e);
            throw KnDbUtil.processException(e, "Failed while getting the sublist pushed to subscriber " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : sublist pushed to the subscriber list size is- ", sublistMapList.size());
        }
    }

    public Map<Integer, Integer>  sublistPushedToSubscribersAndCount(LinkedList<Integer> sublistIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectSublistPushed(String, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistIds  ", sublistIds);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<Integer, Integer>  sublistIdsAndCount = new HashMap<>();
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SUBLIST_PUSHED_TO_SUBSCRIBERS_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistListIds = formIntegerCommaSeperatedIdList(sublistIds);
            query = replaceContactWithValue(query, SUBLISTID, sublistListIds);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sublistIdsAndCount.put(rs.getInt(1), rs.getInt(2));
            }
            knLogger.debug( methodName, "Query executed successfully");
            return sublistIdsAndCount;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the sublist pushed to subscriber - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the sublist pushed to subscriber - " , e);
            throw KnDbUtil.processException(e, "Failed while getting the sublist pushed to subscriber " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : sublist pushed to the subscriber list size is- ", sublistIdsAndCount.size());
        }
    }

    public LinkedList<Integer> selectSublistPushed(String MDN, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectSublistPushed(String, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : MDN -,corpId  ", KnGDPRTemplate.mdn(MDN), corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LinkedList<Integer> sublistIds = new LinkedList<>();
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SUBLIST_PUSHED_TO_SUBSCRIBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, MDN);
            pstmt.setInt(2, SHARED_DISTRBUTION_POLICY);
            pstmt.setInt(3, corpId);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sublistIds.add(rs.getInt(1));
            }
            knLogger.debug( methodName, "Query executed successfully");
            return sublistIds;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the sublist pushed to subscriber - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the sublist pushed to subscriber - " , e);
            throw KnDbUtil.processException(e, "Failed while getting the sublist pushed to subscriber " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : sublist pushed to the subscriber list size is- ", sublistIds.size());
        }
    }

    public void pushSublistListToSubscriberList(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "pushSublistListToSubscriberList(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : distDTO  - ", distDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_LIST_DIST_INFO);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (String mdn : distDTO.getMdnList()) {
                for (int sublist : distDTO.getSublistIds()) {
                    pstmt.setInt(1, sublist);
                    pstmt.setString(2, mdn);
                    pstmt.addBatch();
                }
            }
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting into the list distribution table - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while inserting into the list distribution table - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting into the list distribution table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String, String> getSublistPushedSublistCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSublistPushedSublistCntForSubsc(KnIPCorpSublistSubscDistDTO,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : subsRequestDTO - ", subsRequestDTO);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        Map<String, String> mdnSublistCntMap = new HashMap<String, String>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSC_SUBLIST_COUNT_PER_SUBSC);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistListStr = formIntegerCommaSeperatedIdList(subsRequestDTO.getSublistIds());
            //pstmt.setString(1, sublistListStr);
            query = replaceContactWithValue(query, SUBLISTID, sublistListStr);
            String mdnListStr = formCommaSeperatedIdList(subsRequestDTO.getMdnList());
            //pstmt.setString(2, mdnListStr);
            query = replaceContactWithValue(query, MDNLIST, mdnListStr);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query -  ", "'", query, "'");
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                mdnSublistCntMap.put(rs.getString(1).trim(), Integer.toString(rs.getInt(2)));
            }
            return mdnSublistCntMap;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the sublist count pushed to subscriber - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the sublist count pushed to subscriber- " ,
                    e);
            throw KnDbUtil.processException(e, "Failed to getSublistPushedSublistCntForSubsc " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : Response list size returned - ", mdnSublistCntMap.size());
        }
    }


    public void removeSubscribersSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "removeSubscribersSublist(KnIPCorpSublistSubscDistDTO,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY  : subsRequestDTO - ", subsRequestDTO);
        Connection conn;
        PreparedStatement pstmt=null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_LIST_FOR_SUBSCRIBER);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            for(String mdn : subsRequestDTO.getMdnList()){
                for(int sublistId : subsRequestDTO.getSublistIds()){
                    pstmt.setString(1,mdn);
                    pstmt.setInt(2,sublistId);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while removing subsrcibers sublist lists- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while removing subsrcibers sublist lists - " , e);
            throw KnDbUtil.processException(e, "Failed while removing subsrcibers sublist lists " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        }
        finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }


    public Collection<String> selectSubscribersDistToSublist(int sublistId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscribersDistToSublist(int,boolean,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<String> subscribersMdnList = new ArrayList<String>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSC_DIST_TO_SUBLIST);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                subscribersMdnList.add(rs.getString(1).trim());
            }
            return subscribersMdnList;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching subscribers having the sublist pushed - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while fetching subscribers having the sublist pushed- " , e);
            throw KnDbUtil.processException(e, "Failed while fetching subscribers having the sublist pushed " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT :  the subscriber list size recieved - ", subscribersMdnList.size());
        }
    }

    public LinkedHashSet<String> selectSubsDistributionToMultipleSublist(Collection<Integer> sublistIds, String MDN, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubsDistributionToMultipleSublist(Collection<Integer>, String,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - mdn -", sublistIds, KnGDPRTemplate.mdn(MDN));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LinkedHashSet<String> subscribersMdnList = new LinkedHashSet<>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSC_DIST_TO_ALL_SUBLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIds));
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, MDN);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                subscribersMdnList.add(rs.getString(1).trim());
            }
            return subscribersMdnList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching subscribers having the sublist pushed - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while fetching subscribers having the sublist pushed- " , e);
            throw KnDbUtil.processException(e, "Failed while fetching subscribers having the sublist pushed " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT :  the subscriber list size recieved - ", subscribersMdnList.size());
        }
    }


    public Set<Integer> SelectCorpListIdOnlyPushedViaPrivateGroupList(Collection<Integer> sublistIds, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "SelectCorpListIdOnlyPushedViaPrivateGroupList(Collection<Integer>, String,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - mdn -", sublistIds, KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Set<Integer> corpListIds = new HashSet<>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CORPLISTID_PUSHED_VIA_PRIVATE_GROUPLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIds));
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                corpListIds.add(rs.getInt(1));
            }
            return corpListIds;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching subscribers having the sublist pushed - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while fetching CorplistID having the sublist pushed- " , e);
            throw KnDbUtil.processException(e, "Failed while fetching CorplistID having the sublist pushed " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT :  the corpListIds size recieved - ", corpListIds.size());
        }
    }

    public void deleteCorpListDistReference(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpListDistReference(int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_LIST__REF_FOR_ALL_SUBSCRIBERS);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while corp list distribution reference - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while corp list distribution reference- " , e);
            throw KnDbUtil.processException(e, "Failed to deleteCorpListDistReference " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteCorpListDistReference(List<Integer> sublistId, List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpListDistReference(List<Integer>, List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistId - ", sublistId, ", mdnList - ", mdnList);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            query = "DELETE FROM DG.CORPLISTDISTINFO WHERE CORPLISTID = ? AND RECIPIENTMDN = ?";
//            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks("SUBLISTID", sublistId, query);
//            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query -  ", "'", query, "'");
            for (String mdn : mdnList) {
                for (int sublistIdVal : sublistId) {
                    pstmt.setInt(1, sublistIdVal);
                    pstmt.setString(2, mdn);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException | SQLException e) {
            knLogger.error(methodName, "KnDAOException occured while corp list distribution reference - ", e);
        }finally {
            KnDbUtil.closePreparedStatement(pstmt);
            //KnDbUtil.closeConnection(conn);
        }
    }


    public Map<Integer, Collection<KnCorpSubscriberDTO>> getSublistListDistributionList(Collection<Integer> sublistList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSublistListDistributionList(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistList - ", sublistList);
         Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<KnCorpSubscriberDTO>> sublistDistInfo = new HashMap<Integer, Collection<KnCorpSubscriberDTO>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_DIST_INFO);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistList));
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                int listId = rs.getInt(1);
                Collection<KnCorpSubscriberDTO> mdnList = sublistDistInfo.get(listId);
                if (mdnList == null) {
                    mdnList = new ArrayList<KnCorpSubscriberDTO>();
                }
                KnCorpSubscriberDTO subscriberDTO=new KnCorpSubscriberDTO(rs.getString(2).trim(),null,rs.getInt(3));
                mdnList.add(subscriberDTO);
                sublistDistInfo.put(listId, mdnList);
            }             return sublistDistInfo;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while corp list distribution reference - " , e);
           throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while corp list distribution reference- " , e);
            throw KnDbUtil.processException(e, "Failed to getSublistListDistributionList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT:Returning the distribution list of size  - ", sublistDistInfo.size());
        }
    }

    public void deleteAllCorpListDistReference(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllCorpListDistReference(Collection<Integer>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistIdsList - ", sublistIdsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_CORP_LIST__REF_FOR_ALL_SUBSCRIBERS);
            //query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIdsList));
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            for(Integer sublistId : sublistIdsList) {
                pstmt.setInt(1, sublistId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while corp list distribution reference - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while corp list distribution reference- " , e);
            throw KnDbUtil.processException(e, "Failed to deleteCorpListDistReference " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Collection<KnCorpSublistDTO> getSubscMappedSublistListWithMemberCount(KnIPCorpContactDTO contactDTO,
                                                                                 int corpListId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscMappedSublistListWithMemberCount(KnIPCorpContactDTO,int,boolean,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : contactDTO - ", contactDTO.toString() , " ,corpListId - ", corpListId);
        Connection conn;
        PreparedStatement pstmt = null, pstmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String query = null;
        Collection<KnCorpSublistDTO> sublistList;
        try {
            Map<Integer, KnCorpSublistDTO> subListMap = new HashMap<Integer, KnCorpSublistDTO>();
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_INFO_FOR_SUBSC);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, contactDTO.getMdn());
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int sublistId = rs.getInt(1);
                if (sublistId != corpListId) {
                    KnCorpSublistDTO sublist = new KnCorpSublistDTO();
                    sublist.setSublistId(sublistId);
                    //multilingual revert changes
                    if(rs.getString(2) != null)
                    	sublist.setSublistName(new String(rs.getString(2).getBytes("8859_1"),"UTF-8"));
                    sublist.setSublistType(rs.getInt(3));
                    if(KnConstants.DIST_POLICY_USER_PROFILE == rs.getInt(4)){
                        sublist.setUserProfileListType(ENABLED);
                    }
                    if(KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT == rs.getInt(4)){
                        sublist.setUserProfileListType(ENABLED);
                        sublist.setListDistribution(ENABLED);
                    }
                    subListMap.put(sublistId, sublist);
                }
            }
            knLogger.debug( methodName, "subListMap - " , subListMap);
            Collection<Integer> subListIds = subListMap.keySet();
            String queryMemCount = queryMapper.getQuery(SELECT_SUBLIST_MEMBERCOUNT_FOR_SUBSC);
            queryMemCount = replaceContactWithValue(queryMemCount, SUBLISTID, formIntegerCommaSeperatedIdList(subListIds));
            pstmt1 = conn.prepareStatement(queryMemCount);
            knLogger.debug( methodName, "Executing query -  " , "'" , queryMemCount , "'");
            rs1 = pstmt1.executeQuery();
            while (rs1.next()) {
                int sublistId = rs1.getInt(1);
                KnCorpSublistDTO sublist = subListMap.get(sublistId);
                sublist.setMemberCount(rs1.getInt(2));
                subListMap.put(sublistId, sublist);
            }
            knLogger.debug(methodName, "Query executed successfully, subListMap - ", subListMap);
            sublistList = subListMap.values();
            knLogger.debug( methodName, "Sublist List for the Subscriber returned - " , sublistList);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getSubscMappedSublistListWithMemberCount - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getSubscMappedSublistListWithMemberCounte- " ,
                    e);
            throw KnDbUtil.processException(e, "Failed to getSubscMappedSublistListWithMemberCount " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(rs1);
            KnDbUtil.closePreparedStatement(pstmt);
            KnDbUtil.closePreparedStatement(pstmt1);
        }
        knLogger.debug(methodName, "EXIT: Sublist List for the Subscriber returned -", sublistList);
        return sublistList;
    }

    public int getSubListMemberCount(int subListId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubListMemberCount()";
        knLogger.debug(methodName, "ENTRY : subListIds - ", subListId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String query = null;
        int subListMemeberCount=0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String queryMemCount = queryMapper.getQuery(SELECT_SUBLIST_MEMBERCOUNT_FOR_SUBSC);
            Collection<Integer> subListIds = new ArrayList<>();
            subListIds.add(subListId);
            queryMemCount = replaceContactWithValue(queryMemCount, SUBLISTID, formIntegerCommaSeperatedIdList(subListIds));
            pstmt = conn.prepareStatement(queryMemCount);
            knLogger.debug( methodName, "Executing query -  " , "'" , queryMemCount , "'");
            rs1 = pstmt.executeQuery();
            while (rs1.next()) {
                subListMemeberCount=rs1.getInt(2);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred  " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured  " ,
                    e);
            throw KnDbUtil.processException(e, "Failed to getSublistListMemberCount " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(rs1);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT: subListMemeberCount: ", subListMemeberCount);
        return subListMemeberCount;
    }

    /**
     * This method deleted the Sublist distribution entries for sublistIds.
     * @param sublistIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpListDistribution(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpListDistribution(List<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistIds - ", sublistIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_LIST__REF_FOR_ALL_SUBSCRIBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for(int listId : sublistIds){
                pstmt.setInt(1, listId);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query -  ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to deleteCorpListDist",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * This method deleted the Sublist distribution entries for mdnList.
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpListDistForMdn(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpListDistForMdn(List<String>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : MdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_LIST_DIST_FOR_MDN);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query -  ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit:Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to deleteCorpListDistForMdn",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }


    public Map<String, Integer> commContctListCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "commContctListCntForSubsc(KnIPCorpSublistSubscDistDTO,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : subsRequestDTO - ", subsRequestDTO);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        Map<String, Integer> mdnSublistCntMap = new HashMap<>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_COMM_CNTCTLIST_COUNT_PER_SUBSC);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
           /* String sublistListStr = formIntegerCommaSeperatedIdList(subsRequestDTO.getSublistIds());
            query = replaceContactWithValue(query, SUBLISTID, sublistListStr);*/
            String mdnListStr = formCommaSeperatedIdList(subsRequestDTO.getMdnList());
            query = replaceContactWithValue(query, MDNLIST, mdnListStr);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query -  ", "'", query, "'");
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                mdnSublistCntMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            return mdnSublistCntMap;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the sublist count assign to subscriber - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the sublist count assign to subscriber- " ,
                    e);
            throw KnDbUtil.processException(e, "Failed to getSublistPushedSublistCntForSubsc " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_DIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : Response list size returned - ", mdnSublistCntMap.size());
        }
    }

}
