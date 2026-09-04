/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpListMemberDAO.java
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
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;


public class KnXDMCorpListMemberDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpListMemberDAO.class);

    public String pttServerId = null;

    KnXDMCorpListMemberDAO(String pttServerId) {
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


    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)
            throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Collection<KnCorpSubscriberDTO> getSubscriberPrivateContactListInfo(IPersistenceDTO persistenceDTO,
                                                                               int privatelistId,
                                                                               KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getSubscriberPrivateContactListInfo(IPersistenceDTO, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : dto - ", persistenceDTO, ", PrivateListId - ",
                privatelistId);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBS_PRIVTAE_CONTACT_TO_DELETE);
            query = KnDbUtil.replaceValInQry(query, privatelistId);
            KnCorpMdnListPersistDTO contactMdnListDTO = new KnCorpMdnListPersistDTO();
            if (persistenceDTO instanceof KnCorpMdnListPersistDTO) {
                contactMdnListDTO = (KnCorpMdnListPersistDTO) persistenceDTO;
            }
            int corpId = contactMdnListDTO.getCorpId();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String mdnList = formCommaSeperatedIdList(contactMdnListDTO.getRemovedMdnList());
            //pstmt.setString(2, mdnList);
            query = replaceContactWithValue(query, MDNLIST, mdnList);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(rs.getString(1).trim());
                int mdnCorpId = rs.getInt(2);
                subscriberDTO.setCorpId(mdnCorpId);
                if (corpId != mdnCorpId) {
                    subscriberDTO.setExternalContact(true);
                } else {
                    subscriberDTO.setExternalContact(false);
                }
                subscribersList.add(subscriberDTO);
            }
            knLogger.debug(methodName, "Retrieved subs contactList size - ", subscribersList.size());
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving " ,
                    "subs contactList - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving " ,
                    "subs contactList - " + e);
            throw KnDbUtil.processException(e, "Failed to getSubscriberPrivateContactListInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT :Retrieved subs contactList size -", subscribersList.size());
        }
    }


    public Collection<KnCorpSubscriberDTO> getSubscPrivateContactListDetails(int privateContactListId,
                                                                             KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscPrivateContactListDetails(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : privateContactListId - ", privateContactListId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PRIVATE_CONTACT_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, privateContactListId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully- ");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String contactMdn = rs.getString(1);
                subscriberDTO.setMdn(contactMdn.trim());
                subscriberDTO.setCorpId(rs.getInt(2));
                subscribersList.add(subscriberDTO);
            }
            knLogger.debug(methodName, "Exit: Retrieved private members size from DB - ", subscribersList.size());
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retieveing private members from DB - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retieveing private members from DB - ",
                    e);
            throw KnDbUtil.processException(e, "Failed  while retieveing private members from DB  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteSublistMembers(Collection<String> contactList, int sublistId,
                                     KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSublistMembers(Collection<String>, int, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : contactList -", KnGDPRTemplate.mdnList(contactList), " ,sublistId - ", sublistId);
        Connection conn;
        Statement stmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(contactList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(DELETE_SUBLIST_CONTACT_LIST);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(subsList));
                query = KnDbUtil.replaceValInQry(query, sublistId);
                knLogger.debug(methodName, "Executing query- ", "'", query, "'");
                stmt = conn.createStatement();
                stmt.executeUpdate(query);
            }
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while deleting members from sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while deleting members from sublist- " , e);
             throw KnDbUtil.processException(e, "Failed while deleting members from sublist " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        }finally{
             KnDbUtil.closeStatement(stmt);
        }
    }

    public Map<String, Integer> getSubscriberAdditionalContactCnt(KnIPCorpSublistSubscDistDTO distDTO,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberAdditionalContactCnt(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : distDTO - ", distDTO);
         Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> memAddContactCnt = new HashMap<String, Integer>();
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ADDITIONAL_CONTACT_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistStr = formIntegerCommaSeperatedIdList(distDTO.getSublistIds());
            query = replaceContactWithValue(query, SUBLISTID, sublistStr);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            for (String mdn : distDTO.getMdnList()) {
                pstmt.setString(1, mdn);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    int count = rs.getInt(1);
                    memAddContactCnt.put(mdn, count);
                }
                rs.close();
            }             knLogger.debug(methodName, "EXIT :Query executed successfully- , Subscribers contact count map size- ", memAddContactCnt.size());
            return memAddContactCnt;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting subscriber additional contact count - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting subscriber additional contact count - " , e);
            throw KnDbUtil.processException(e, "Failed while getting subscriber additional contact count " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public int getFinalMemberContactCount(Collection<Integer> finalSublistIds,
                                          Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList,
                                          KnMdnDetailsPersistDTO contactMdnPersistDto,
                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getFinalMemberContactCount(Collection<Integer>, Collection<KnCorpSubscriberDTO>," +
                " KnMdnDetailsPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : finalSublistIds - ", finalSublistIds, " ,mdnsToBeAddedToPrivateList - ",
                mdnsToBeAddedToPrivateList.toString() + " ,contactMdnPersistDto - " + contactMdnPersistDto.toString());
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_UNIQUE_MDN_FRM_SUBLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String subListStr = formIntegerCommaSeperatedIdList(finalSublistIds);
            //pstmt.setString(1, subListStr);
            query = replaceContactWithValue(query, SUBLISTID, subListStr);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            Collection<String> mdnList = new ArrayList<String>();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                if (!mdnList.contains(mdn)) {
                    mdnList.add(mdn);
                    count++;
                }
            }

            if (mdnsToBeAddedToPrivateList != null && !mdnsToBeAddedToPrivateList.isEmpty()) {
                for (KnCorpSubscriberDTO subsDTO : mdnsToBeAddedToPrivateList) {
                    if (!mdnList.contains(subsDTO.getMdn().trim())) {
                        count++;
                    }
                }
            }

            Collection<String> removedMdnList = contactMdnPersistDto.getMdnList();
            if (removedMdnList != null && !removedMdnList.isEmpty()) {
                for (String mdn : removedMdnList) {
                    if (!removedMdnList.contains(mdn)) {
                        count--;
                    }
                }
            }
            return count;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getting the final member count - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getting the final member count - " ,
                    e);
            throw KnDbUtil.processException(e, "Failed to getFinalMemberContactCount " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : Contact count - ", count);
        }
    }

    public Collection<String> getPoCSubscPrsntForSublistFrmMdnList(Collection<String> memberList, int corpListId,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPoCSubscPrsntForSublistFrmMdnList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : memberList - ", memberList == null ? memberList : KnGDPRTemplate.mdnList(memberList), ", corpListId - ", corpListId);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> membersInDb = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SELECTIVE_SUBLIST_MEMBERS_FRM_DB);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String memberLstStr = formCommaSeperatedIdList(memberList);
            query = replaceContactWithValue(query, MDNLIST, memberLstStr);
            query = KnDbUtil.replaceValInQry(query, corpListId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                membersInDb.add(rs.getString(1).trim());
            }
            knLogger.debug( methodName, "Query executed successfully.");
            return membersInDb;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the selective subscriber- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the selective subscriber - " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while retrieving the selective subscriber " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : member list size found in DB  - ", membersInDb.size());
        }
    }

    public Collection<KnCorpSubscriberDTO> getSublistContactList(int sublistId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSublistContactList(int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> membersInDb = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
             query = queryMapper.getQuery(GET_PRIVATE_CONTACT_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug( methodName, "Executing query-" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,sublistId);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully ");
            while (rs.next()) {
                KnCorpSubscriberDTO subscDTO = new KnCorpSubscriberDTO();
                subscDTO.setMdn(rs.getString(1).trim());
                subscDTO.setCorpId(rs.getInt(2));
                membersInDb.add(subscDTO);
            }
            knLogger.debug(methodName, "EXIT : Sublist member list size - ", membersInDb.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the contact list for the sublist  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return membersInDb;
    }


    public void deleteSubscPrivateContactList(Collection<String> removePrivateContactList, int privateListId,
                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscPrivateContactList(Collection<String>, int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : removePrivateContactList - ", removePrivateContactList, " ,privateListId - " + privateListId);
        Connection conn;
        Statement stmt = null;
        PreparedStatement pStmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //String mdnStr = formCommaSeperatedIdList(removePrivateContactList);
            //query = replaceContactWithValue(query, MDNLIST, mdnStr);
            var mdnListArray = new ArrayList<>(removePrivateContactList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(DELETE_FROM_PRIVATE_CONTACT_LIST);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                query = KnDbUtil.replaceValInQry(query, privateListId);
                knLogger.debug(methodName, "Executing query - ", "'", query, "'");
                int index = 1;
                pStmt = conn.prepareStatement(query);
                for (String pvtCont : subsList) {
                    pStmt.setString(index++, pvtCont);
                }
                pStmt.executeUpdate();
            }
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteing members from the private sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleteing members from the private sublist- " , e);
            throw KnDbUtil.processException(e, "Failed to deleteSubscPrivateContactList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        }
        finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeStatement(stmt);
        }
    }

    public void insertSublistMembers(Collection<KnCorpSubscriberDTO> finalMemberList, int sublistId,
                                     KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertSublistMembers(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistId", sublistId, ", finalMemberList - ", finalMemberList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_SUBLIST_CONTACT_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (KnCorpSubscriberDTO contactDto : finalMemberList) {
                pstmt.setInt(1, sublistId);
                pstmt.setString(2, contactDto.getMdn());
                int memberCorpId = contactDto.getCorpId();
                if (memberCorpId <= 0) {
                    pstmt.setNull(3, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(3, memberCorpId);
                }
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing Query- " , "'" , query , "'");
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting contact into the sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while inserting contact into the sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting contact into the sublist -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void insertSublistMembers(Collection<KnCorpSubscriberDTO> finalMemberList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertSublistMembers(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :, ", "finalMemberList - ", finalMemberList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_SUBLIST_CONTACT_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for(KnCorpSubscriberDTO subscriberDTO : finalMemberList){
                pstmt.setInt(1, subscriberDTO.getGrpSublistId());
                pstmt.setString(2, subscriberDTO.getMdn());
                int memberCorpId = subscriberDTO.getCorpId();
                if (memberCorpId <= 0) {
                    pstmt.setNull(3, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(3, memberCorpId);
                }
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing Query- " , "'" , query , "'");
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while inserting contact into the sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting contact into the sublist -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public void deleteAllSublistMembers(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllSublistMembers(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
//        KnCorpSublistDTO sublistInfo = new KnCorpSublistDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SUBLIST_MEMBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully ");
          } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting members from the sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting members from the sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting members from the sublist - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteAllSublistMembers(List<Integer> sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllSublistMembers(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "DELETE FROM DG.CORPLISTMEMBER WHERE CORPLISTID IN (SUBLISTIDS)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks("SUBLISTIDS",sublistId,query);
            pstmt = conn.prepareStatement(query);
            for(Integer itr : sublistId){
                pstmt.setInt(index++,itr);
            }
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully ");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting members from the sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting members from the sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting members from the sublist - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public KnMdnDetailsPersistDTO selectGroupPrivateMemberListInfoFromMdnList(KnCorpMdnListPersistDTO corpMdnListPersistDto,
                                                                              int groupMemberlistId,
                                                                              KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupPrivateMemberListInfoFromMdnList(KnCorpMdnListPersistDTO, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpMdnListPersistDto- ", corpMdnListPersistDto, ", groupMemberlistId - ", groupMemberlistId);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        KnMdnDetailsPersistDTO mdnPersistDTO = new KnMdnDetailsPersistDTO();
        try {
          KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SELECTIVE_SUBLIST_MEMBERS_FRM_DB);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //String mdnStr = formCommaSeperatedIdList(corpMdnListPersistDto.getMdnList());
            String mdnStr = formCommaSeperatedIdList(corpMdnListPersistDto.getRemovedMdnList());
            //pstmt.setString(2, mdnStr);
            query = replaceContactWithValue(query, MDNLIST, mdnStr);
            query = KnDbUtil.replaceValInQry(query, groupMemberlistId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully.");
            Collection<String> mdnList = new ArrayList<String>();
            while (rs.next()) {
                mdnList.add(rs.getString(1).trim());
            }
            mdnPersistDTO.setMdnList(mdnList);
            return mdnPersistDTO;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching group private List members - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while fetching group private List members - " , e);
            throw KnDbUtil.processException(e, "Failed while fetching group private List members" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT. Group private member List size- ", mdnPersistDTO.getMdnList().size());
        }
    }

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipList(Collection<String> mdnList,
                                                                              int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubcriberSublistMemberShipList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList) , ", corpId - ", corpId);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<String>> sublistListsMemberMap = new HashMap<Integer, Collection<String>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if (mdnList.size() == 1) {
                knLogger.debug("MdnList size : ",mdnList.size());
                query = queryMapper.getQuery(GET_SUBSCRIBERS_SUBLIST_MEMBERSHIP_LIST_SINGLE);
                pStmt = conn.prepareStatement(query);
                for (String mdn : mdnList) {
                    //query = KnDbUtil.replaceValInQry(query, mdn);
                    pStmt.setString(1,mdn);
                    pStmt.setInt(2,corpId);
                }
                //query = KnDbUtil.replaceValInQry(query, corpId);
            } else {
                knLogger.debug("MdnList size : ",mdnList.size());
                var mdnListArray = new ArrayList<>(mdnList);
                var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for (var subsList : subsLists) {
                    query = queryMapper.getQuery(GET_SUBSCRIBERS_SUBLIST_MEMBERSHIP_LIST);
                    //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
                    query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                    int index = 1;
                    //query = KnDbUtil.replaceValInQry(query, corpId);
                    pStmt = conn.prepareStatement(query);
                    for (String mdn : subsList) {
                        pStmt.setString(index++, mdn);
                    }
                    pStmt.setInt(index++, corpId);
                }
            }
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                int sublistId = rs.getInt(1);
                Collection<String> memberMdnList = sublistListsMemberMap.get(sublistId);
                if (memberMdnList == null) {
                    memberMdnList = new ArrayList<String>();
                }
                memberMdnList.add(rs.getString(2));
                sublistListsMemberMap.put(sublistId, memberMdnList);
            }
            return sublistListsMemberMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the subscribers sublist membership list -  " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the subscribers sublist membership list - " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while getting the subscribers sublist membership list  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : The sublist members map size is - ", sublistListsMemberMap.size());
        }
    }

    public void deleteMembersFromAllSublist(Map<Integer, Collection<String>> sublistMemberMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteMembersFromAllSublist(Map<Integer, Collection<String>>, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : sublistMemberMap - ", sublistMemberMap == null ? sublistMemberMap : KnGDPRTemplate.mapMdnAsValue(sublistMemberMap));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_MEMBER_FOR_ALL_SUBLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<Integer, Collection<String>> entry : sublistMemberMap.entrySet()) {
                Collection<String> mdnList = entry.getValue();
                Integer sublistId = entry.getKey();
                pstmt.setInt(1, sublistId);
                for (String mdn : mdnList) {
                    pstmt.setString(2, mdn);
                    pstmt.addBatch();
                }
                knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT :Query executed successfully. ");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the subscribers from the sublist -  " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting the subscribers from the sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting the subscribers from the sublist -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void insertMemberInAllSublists(Map<Integer, Collection<String>> sublistMemberMap, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertMemberInAllSublists(Map<Integer, Collection<String>>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : sublistMemberMap - ", sublistMemberMap == null ? sublistMemberMap : KnGDPRTemplate.mapMdnAsValue(sublistMemberMap), ", corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
//        Collection<Integer> sublistLists = null;
        try {
      KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_MEMBER_IN_ALL_SUBLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<Integer, Collection<String>> entry : sublistMemberMap.entrySet()) {
                Collection<String> mdnList = entry.getValue();
                Integer corplistId = entry.getKey();
                for (String mdn : mdnList) {
                    pstmt.setInt(1, corplistId);
                    pstmt.setString(2, mdn);
                    if (corpId == 0) {
                        pstmt.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        pstmt.setInt(3, corpId);
                    }
                    pstmt.addBatch();
                }
            }
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT :Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting the MDN to sublist -  " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while inserting the MDN to sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting the MDN to sublist-  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteAllCorpSublistMembers(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "deleteAllSublistMembers(Collection<Integer>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : sublistIdsList - ", sublistIdsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_SUBLIST_MEMBERS);
            //query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIdsList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            for(Integer sublistId : sublistIdsList) {
                pstmt.setInt(1, sublistId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting all members from the all sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting all members from all the sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting members from the all sublists- " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Collection<String> getFinalMemberGroupContactCount(Collection<Integer> sublistMappedToGroup,
                                                              Collection<KnCorpSubscriberDTO> privateMemberList,
                                                              Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList,
                                                              KnMdnDetailsPersistDTO contactMdnPersistDto,
                                                              KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getFinalMemberGroupContactCount(Collection<Integer>, Collection<KnCorpSubscriberDTO>, Collection<KnCorpSubscriberDTO>," +
                " KnMdnDetailsPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistMappedToGroup - ", sublistMappedToGroup, " , privateMemberList - ", privateMemberList,
                " ,mdnsToBeAddedToPrivateList - ", mdnsToBeAddedToPrivateList,
                " ,contactMdnPersistDto - ", contactMdnPersistDto);
       Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        String query = null;
        Collection<String> mdnList = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            /*  query = queryMapper.getQuery(GET_UNIQUE_MDN_FRM_SUBLIST);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            query = replaceContactWithValue(query, SUBLISTID, String.valueOf(groupMemberListId));
            //fetching the privatelist members
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            Collection<String> mdnList = new ArrayList<String>();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                if (!mdnList.contains(mdn)) {
                    mdnList.add(mdn);
                    count++;
                }
            }
            */
            for (KnCorpSubscriberDTO subsc : privateMemberList) {
                mdnList.add(subsc.getMdn());
                count++;
            }
            //removing the  removed members n request
            Collection<String> removedMdnList = contactMdnPersistDto.getMdnList();
            if (removedMdnList != null && !removedMdnList.isEmpty()) {
                for (String mdn : removedMdnList) {
                    if (mdnList.contains(mdn)) {
                        mdnList.remove(mdn);
                        count--;
                    }
                }
            }

            if (sublistMappedToGroup != null && !sublistMappedToGroup.isEmpty()) {
                query = queryMapper.getQuery(GET_UNIQUE_MDN_FRM_SUBLIST);
                String sublistStr = formIntegerCommaSeperatedIdList(sublistMappedToGroup);
                query = replaceContactWithValue(query, SUBLISTID, sublistStr);

                stmt = conn.createStatement();
                knLogger.debug( methodName, "Executing query - ", "'", query, "'");
                rs = stmt.executeQuery(query);
                //adding the sublist memebers only if not present in private list

                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    if (!mdnList.contains(mdn)) {
                        mdnList.add(mdn);
                        count++;
                    }
                }
            }
            if (mdnsToBeAddedToPrivateList != null && !mdnsToBeAddedToPrivateList.isEmpty()) {
                for (KnCorpSubscriberDTO subsDTO : mdnsToBeAddedToPrivateList) {
                    if (!mdnList.contains(subsDTO.getMdn())) {
                        mdnList.add(subsDTO.getMdn());
                        count++;
                    }
                }
            }

            return mdnList;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getting the final group member count - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getting the final group member count - " , e);
            throw KnDbUtil.processException(e, "Failed to getFinalMemberGroupContactCount " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : Group Members size is - ", mdnList.size());
        }

    }

    public void updateMemberCorpId(String corpId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMemberCorpId(String, String,  KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request corpId,- ", corpId, ", mdn - ", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pstmt = null;
//        ResultSet rs = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.UPDATE_SUBLIST_MEMBER_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            if (corpId == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, Integer.parseInt(corpId));
            }
            pstmt.setString(2, mdn);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Executed query successfully,Updated the contact corp id .");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the contact corp id  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updating the contact corp id - " , e);
            throw KnDbUtil.processException(e, "Failed while updating the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public ArrayList<Integer> getEmptySublistFrmList(ArrayList<Integer> sharedSublists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmptySublistFrmList(ArrayList<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : sharedSublists - ", sharedSublists);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ArrayList<Integer> emptySublist = new ArrayList<Integer>();
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_EMPTY_SUBLIST);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sharedSublists));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt(2) == 0) {
                    emptySublist.add(rs.getInt(1));
                }
            }
            knLogger.debug(methodName, "EXIT: Query executed successfully");
            return emptySublist;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting all members from the all sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting all members from all the sublist - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting members from the all sublists- " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersPrivateList(Map<String, Integer> subscPrivateListMap,
                                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersPrivateList(Map<String, Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : subscPrivateListMap - ", subscPrivateListMap);
        Connection conn;
        Statement stmt = null;
        String query = null;
        Map<String, Collection<KnCorpSubscriberDTO>> subscPrivateList = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBLIST_MEMBERS_FRM_DB);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(subscPrivateListMap.values()));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            Map<Integer, Collection<KnCorpSubscriberDTO>> sublistMembersMap = new HashMap<Integer, Collection<KnCorpSubscriberDTO>>();
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int listId = rs.getInt(3);
                Collection<KnCorpSubscriberDTO> subscList = sublistMembersMap.get(listId);
                if (subscList == null) {
                    subscList = new ArrayList<KnCorpSubscriberDTO>();
                }
                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                subsc.setMdn(rs.getString(1).trim());
                subsc.setCorpId(rs.getInt(2));
                subscList.add(subsc);
                sublistMembersMap.put(listId, subscList);
            }
            knLogger.debug( methodName, "Query executed successfully ");
            for (Map.Entry<String, Integer> entry : subscPrivateListMap.entrySet()) {
                int listId = entry.getValue();
                String mdn = entry.getKey();
                Collection<KnCorpSubscriberDTO> privateListmembers = sublistMembersMap.get(listId);
                if (privateListmembers != null && !privateListmembers.isEmpty()) {
                    subscPrivateList.put(mdn, privateListmembers);
                } else {
                    subscPrivateList.put(mdn, new ArrayList<KnCorpSubscriberDTO>());
                }
            }
            knLogger.debug(methodName, "EXIT.The subscriber Contact size is - ", subscPrivateList.size());
            return subscPrivateList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting all members from the all sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting all members from all the sublist - " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while deleting members from the all sublists- " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }

    public void insertCorplistMembers(Map<Integer, Collection<KnCorpSubscriberDTO>> listMemberMap,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertCorplistMembers(Map<Integer, Collection<KnCorpSubscriberDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : listMemberMap - ", listMemberMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
//        ResultSet rs;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_SUBLIST_CONTACT_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            final int batchSize = 100;
            int batchCount = 0;
            for (Map.Entry<Integer, Collection<KnCorpSubscriberDTO>> entry : listMemberMap.entrySet()) {
               Collection<KnCorpSubscriberDTO> subscList = entry.getValue();
               Integer sublistId = entry.getKey();
               for (KnCorpSubscriberDTO subsc : subscList) {
                   pstmt.setInt(1, sublistId);
                   pstmt.setString(2, subsc.getMdn());
                   pstmt.setInt(3, subsc.getCorpId());
                   pstmt.addBatch();
                   batchCount++;
                   if (batchCount % batchSize == 0) {
                       pstmt.executeBatch();
                       pstmt.clearBatch();
                   }
               }
            }
            pstmt.executeBatch();
            pstmt.clearBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting members to the list - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while inserting members to the list - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting members to the list- " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListForAllCorporate(Collection<String> mdnList,
                                                                                             KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubcriberSublistMemberShipListForAllCorporate(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<String>> sublistListsMemberMap = new HashMap<Integer, Collection<String>>();
        try {
           KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_SUBLIST_MEMBERSHIP_LIST_FOR_ALL_CORPORATE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            stmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                int sublistId = rs.getInt(1);
                Collection<String> memberMdnList = sublistListsMemberMap.get(sublistId);
                if (memberMdnList == null) {
                    memberMdnList = new ArrayList<String>();
                }
                memberMdnList.add(rs.getString(2));
                sublistListsMemberMap.put(sublistId, memberMdnList);
            }
            return sublistListsMemberMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the subscribers sublist membership list -  " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the subscribers sublist membership list - " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while getting the subscribers sublist membership list  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(stmt);
            knLogger.debug(methodName, "EXIT : The sublist members map size is - ", sublistListsMemberMap.size());
        }
    }

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListAsExtContact(Collection<String> mdnList,
                                                                                          int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubcriberSublistMemberShipListAsExtContact(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), ", corpId - ", corpId);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<String>> sublistListsMemberMap = new HashMap<Integer, Collection<String>>();
        try {
          KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if (mdnList.size() == 1) {
                query = queryMapper.getQuery(GET_EXT_CONTACT_SUBLIST_MEMBERSHIP_LIST_SINGLE);
                for (String mdn : mdnList) {
                    query = KnDbUtil.replaceValInQry(query, mdn);
                }
                query = KnDbUtil.replaceValInQry(query, corpId);
            } else {
                query = queryMapper.getQuery(GET_EXT_CONTACT_SUBLIST_MEMBERSHIP_LIST);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
                query = KnDbUtil.replaceValInQry(query, corpId);
            }
            knLogger.debug( methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                int sublistId = rs.getInt(1);
                Collection<String> memberMdnList = sublistListsMemberMap.get(sublistId);
                if (memberMdnList == null) {
                    memberMdnList = new ArrayList<String>();
                }
                memberMdnList.add(rs.getString(2));
                sublistListsMemberMap.put(sublistId, memberMdnList);
            }
            return sublistListsMemberMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the subscribers sublist membership list as ext contact-  " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while getting the subscribers sublist membership list as ext contact- " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while getting the subscribers sublist membership list as ext contact " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : The sublist members map as ext contact size is - ", sublistListsMemberMap.size());
        }
    }

    /**
     * This method query the dg.corplistmember table to get groups private members list
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getGroupPrivtMemLst(int groupId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupPrivtMemLst(int, int, boolean, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Integer> grpMemMap = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_PRIVATE_LIST_MEMBERS_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                grpMemMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
           knLogger.debug(methodName, "EXIT : The sublist members " , grpMemMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting the subscribers sublist membership ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
         return grpMemMap;
     }

    /**
     * This method queries the dg.corplistmember table and returns Map of collections of Internal and external members
     * @param sublistId
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, List<String>> getSublistMemMap(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSublistMemMap(int, int,boolean, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, List<String>> memMap = new HashMap<String, List<String>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            // query to fatch MEMBERMDN, MEMBERCORPID FROM DG.CORPLISTMEMBER table.
            query = queryMapper.getQuery(GET_SUBLIST_MEMBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query-", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully ");
            List<String> intMemList = new ArrayList<String>();
            List<String> extMemList = new ArrayList<String>();
            while (rs.next()) {
                int memCorpId = rs.getInt(2);
                if (memCorpId == corpId) {
                    intMemList.add(rs.getString(1).trim());
                } else {
                    extMemList.add(rs.getString(1).trim());
                }
            }
            memMap.put(KnConstants.INTERNAL, intMemList);
            memMap.put(KnConstants.EXTERNAL, extMemList);
            knLogger.info(methodName, "EXIT : internal mem size ", intMemList.size(), " ext mem size", extMemList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the sublist members",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return memMap;
    }

    /**
     * This method returns a Map of sublist id and members in mdnList
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<String>> getSubsSublistList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getSubsSublistList(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, List<String>> sublistListsMemberMap = new HashMap<Integer, List<String>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_SUBLIST_MEMBERSHIP_LIST_FOR_ALL_CORPORATE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                int sublistId = rs.getInt(1);
                List<String> memberMdnList = sublistListsMemberMap.get(sublistId);
                if (memberMdnList == null) {
                    memberMdnList = new ArrayList<String>();
                }
                memberMdnList.add(rs.getString(2));
                sublistListsMemberMap.put(sublistId, memberMdnList);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting the subscribers sublist membership list  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : The sublist members map size is ", sublistListsMemberMap.size());
        return sublistListsMemberMap;
    }

    /**
     * This methods returns the empty sublists fromsublistIds.
     * @param sublistIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getEmptySublistIds(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getEmptySublistIds(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : sublistIds - ", sublistIds);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Set<Integer> existingLstIds = new HashSet<Integer>();
        List<Integer> emptyList = new ArrayList<Integer>(sublistIds);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBLIST_MEMBERS_FRM_DB);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIds));
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                existingLstIds.add(rs.getInt(3));
            }
            emptyList.removeAll(existingLstIds);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting the subscribers sublist membership list  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : emptyList ", emptyList);
        return emptyList;
    }

    /**
     * This method deleted the sublist members in mdnList from dg.corplistmember table.
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpSublistMemList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpSublistMemList(List<String>,KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_SUBLIST_MEMBER_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing from corp list member ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public List<KnCorpSubscriberDTO> getSubscPrivateMemberList(int privateContactListId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getSubscPrivateMemberList(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : privateContactListId - ", privateContactListId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBLIST_MEMBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, privateContactListId);
            knLogger.debug( methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully- ");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String contactMdn = rs.getString(1);
                subscriberDTO.setMdn(contactMdn.trim());
                int memCorpId = rs.getInt(2);
                subscriberDTO.setCorpId(memCorpId);
                if(memCorpId >0){
                    subscriberDTO.setContact_type(KnConstants.INTERNAL_SUBSC_CLIENT_TYPE);
                }else{
                    subscriberDTO.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                }
                subscribersList.add(subscriberDTO);
            }
            knLogger.debug(methodName, "Retrieved private members size from DB - ", subscribersList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while retieveing private members from DB  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return subscribersList;
    }

    public List<KnCorpSubscriberDTO> getSublistDistinctMembers(Collection<Integer> addedSublistIds, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getSublistDistinctMembers(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : addedSublistIds - ", addedSublistIds);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        List<KnCorpSubscriberDTO> memberList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBLISTS_MEMBER_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(addedSublistIds));
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(rs.getString(1).trim());
                int memCorpId = rs.getInt(2);
                subscriberDTO.setCorpId(memCorpId);
                subscriberDTO.setContact_type(KnConstants.INTERNAL_SUBSC_CLIENT_TYPE);
                if( corpId != memCorpId){
                    subscriberDTO.setExternalContact(true);
                    if(memCorpId >0){
                        subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                    }else{
                        subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                    }
                }
                if (!memberList.contains(subscriberDTO)) {
                    memberList.add(subscriberDTO);
                }
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting the subscribers sublist membership list  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : memberList ", memberList);
        return memberList;
    }

    /**
     * This method is the retrieve the list of sublist where MDN exist a member.
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSubscrAllSublists(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscrAllSublists(String, boolean,KnPersisterTxn )";
        knLogger.debug(methodName, "Entry : mdn - ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<Integer> sublistIds = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_SUBLISTS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug( methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                sublistIds.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while querying corplistmember",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT : sublistIds ", sublistIds);
        return sublistIds;
    }

    public List<String> getSublistMembers(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException{

        String methodName = "getSublistMembers()";
        knLogger.debug(methodName, "Entry : sublistIds - ", sublistIds);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<String> mdnList = new HashSet<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBLIST_MEMBERS_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                mdnList.add(rs.getString(1).trim());
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while querying corplistmember",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT : sublistIds ", sublistIds);
        return new ArrayList<String>(mdnList);
    }


    public void deleteSublistMembersForRequestMDN(Collection<Integer> sublistIds, String memberMdn,
                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSublistMembersForRequestMDN(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : sublistIds -", sublistIds, " ,memberMdn - ", KnGDPRTemplate.mdn(memberMdn));
        Connection conn;
        Statement stmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SUBLIST_CONTACT_LIST_FOR_REQUEST_MDN);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIds));
            query = KnDbUtil.replaceValInQry(query, memberMdn);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug( methodName, "Executing query- ", "'", query, "'");
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while deleting members from sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while deleting members from sublist- " , e);
            throw KnDbUtil.processException(e, "Failed while deleting members from sublist " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeStatement(stmt);
        }
    }

    public List<String> getNonExisitingGroupMemberInPrivSublist(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getNonExisitingGroupMemberInPrivSublist()";
        knLogger.info(methodName, "Entry : groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> mdnList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_NONEXISITNG_GROUPMEMBER_IN_CORPLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                mdnList.add(rs.getString(1).trim());
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while querying corplistmember",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.info(methodName, "EXIT : mdnList ", KnGDPRTemplate.mdnList(mdnList));
        return mdnList;
    }
    public void deleteBulkSubscPrivateContactList(Map<Integer, List<String>> groupIdVsRemovedMdnsListMap, Map<Integer, Integer>
            groupIdVsGroupPrivateListMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteBulkSubscPrivateContactList(Map<Integer, List<String>>,  Map<Integer, Integer>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : groupIdVsRemovedMdnsListMap - ", groupIdVsRemovedMdnsListMap, " ,groupIdVsGroupPrivateListMap - " , groupIdVsGroupPrivateListMap);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //String mdnStr = formCommaSeperatedIdList(removePrivateContactList);
            //query = replaceContactWithValue(query, MDNLIST, mdnStr);
           for(var entry : groupIdVsRemovedMdnsListMap.entrySet()){
               int groupId = entry.getKey();
               List<String> removePrivateContactList = entry.getValue();
               int privateListId = groupIdVsGroupPrivateListMap.get(groupId);
               var mdnListArray = new ArrayList<>(removePrivateContactList);
               var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
               for (var subsList : subsLists) {
                   query = queryMapper.getQuery(DELETE_FROM_PRIVATE_CONTACT_LIST);
                   query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                   query = KnDbUtil.replaceValInQry(query, privateListId);
                   knLogger.debug(methodName, "Executing query - ", "'", query, "'");
                   int index = 1;
                   pStmt = conn.prepareStatement(query);
                   for (String pvtCont : subsList) {
                       pStmt.setString(index++, pvtCont);
                   }
                   pStmt.executeUpdate();
               }

              }
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteing members from the private sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleteing members from the private sublist- " , e);
            throw KnDbUtil.processException(e, "Failed to deleteSubscPrivateContactList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        }
        finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }


    public Map<Integer, List<String>> getNonExisitingGroupMemberInPrivSublistForGroups(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getNonExisitingGroupMemberInPrivSublistForGroups(Collection<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "Entry : groupIds - ", groupIds);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        var groupMdnListMap = new HashMap<Integer, List<String>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_NONEXISITNG_GROUPMEMBER_IN_CORPLIST_FOR_GROUPS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                Integer groupId = rs.getInt("CORPGROUPID");
                String mdn = rs.getString("MEMBERMDN");
                if (!groupMdnListMap.containsKey(groupId)) {
                    var mdns = new ArrayList<String>();
                    groupMdnListMap.put(groupId, mdns);
                }
                groupMdnListMap.get(groupId).add(mdn);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while querying corplistmember",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.info(methodName, "EXIT : mdnList ", KnGDPRTemplate.mapMdnAsListValue(groupMdnListMap));
        return groupMdnListMap;
    }
}