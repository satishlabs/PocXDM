/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpContactListDAO.java
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

import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnAllocatePocSubsUpdateDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.timesten.jdbc.TimesTenTypes;

import java.sql.*;
import java.util.*;

import com.kodiak.logger.KnLogger;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_SUBSCRIBER_CONTACT_LIST_SP;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpContactListDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpContactListDAO.class);
    public String pttServerId = null;

    KnXDMCorpContactListDAO(String pttServerId) {
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

      private int getSize(Collection dataList) {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    private int getMapSize(Map dataMap) {
        if (dataMap != null) {
            return dataMap.size();
        } else {
            return 0;
        }
    }

    public List<String> getCorpResourceList(KnIPCorpContactDTO contactDTO, int maxContacts, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getCorpResourceList(KnIPCorpContactDTO, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : contactDTO- ", contactDTO, " , maxContacts - ", maxContacts);
        String query = null;
        int maxErrorLen = 512;
        int returnval = -1;
        String errormsg;
        ResultSet cursor = null;
        CallableStatement stmt = null;
        List<String> contactList = new ArrayList<String>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            String mdn = contactDTO.getMdn();
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_CONTACT_LIST_SP);
            knLogger.debug( methodName, "Before call to stored procedure for mdn - ", KnGDPRTemplate.mdn(mdn));
            knLogger.debug( methodName, "Executing SP", query);
            stmt = conn.prepareCall(query);
            stmt.setString(1, mdn);
            stmt.setInt(2, maxContacts);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.registerOutParameter(4, TimesTenTypes.CURSOR);
            stmt.registerOutParameter(5, Types.VARCHAR, maxErrorLen);
            stmt.executeUpdate();
            knLogger.debug( methodName, "Stored Procedure executed successfully.");
            returnval = stmt.getInt(3);
            knLogger.debug( methodName, "stmt object - ", stmt.getClass());
            knLogger.debug( methodName, "stmt.getCursor(4) - ", stmt.getClass().getName());
            if (returnval != 0) {
                cursor = (ResultSet) stmt.getObject(4);
            }
            errormsg = stmt.getString(5);
            knLogger.debug( methodName, "Error Message obtained from Stored procedure - ", errormsg);
            if ( cursor != null && !cursor.wasNull()) {
                knLogger.debug( methodName, "class - ", cursor.getClass());
                while (cursor.next()) {
                    contactList.add(cursor.getString(2).trim());
                }
                knLogger.debug( methodName, "contactList size - ", contactList.size());
            }
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while fetching the subscribers resourcelist details ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(cursor);
            KnDbUtil.closeStatement(stmt);
        }
       return contactList;
    }

    public List<String> getCorpCommonContactList(String mdn, int maxContacts, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getCorpCommonContactList(KnIPCorpContactDTO, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdn- ", KnGDPRTemplate.mdn(mdn), " , maxContacts - ", maxContacts);
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        List<String> contactList = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ALL_COMMON_CONTACTLIST_MDNS);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                contactList.add(rs.getString(1).trim());
            }
            knLogger.debug( methodName, "common contactList size is - ", contactList.size());
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while fetching the subscribers common contact list details ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return contactList;
    }

    public Set<String> getCorpNonCommonContactList(String mdn, int maxContacts, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getCorpNonCommonContactList(KnIPCorpContactDTO, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdn- ", KnGDPRTemplate.mdn(mdn), " , maxContacts - ", maxContacts);
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Set<String> contactList = new HashSet<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBS_NON_COMMON_CONTACT_LIST);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                contactList.add(rs.getString(1).trim());
            }
            knLogger.debug( methodName, "Non Common contactList size is - ", contactList.size());
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while fetching the subscribers Non Common contact list details ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return contactList;
    }

    public Collection<String> getMappedSubscribersContactList(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getMappedSubscribersContactList(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList size - ", getSize(mdnList));
        String query = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Collection<String> subscList = new ArrayList<String>();
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCCRIBER_HAVING_FOLLOWING_MDNS_AS_MEMBERS);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            stmt = conn.prepareStatement(query);
            int paramIndex = 1;
            for (String mdn : mdnList) {
                stmt.setString(paramIndex++, mdn);
            }
            rs = stmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                if (!subscList.contains(mdn)) {
                    subscList.add(mdn);
                }
            }
            return subscList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching the subsribers resourcelist details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while fetching the subsribers resourcelist details - ", e);
            throw KnDbUtil.processException(e, "Failed while fetching the subsribers resourcelist details " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug( methodName,  "EXIT : Subscribers list returned is - ", KnGDPRTemplate.mdnList(subscList));
        }
    }

    public void insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>> mdnContactListMap,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : mdnContactListMap - ", mdnContactListMap.size());
        String query = null;
        PreparedStatement pstmt = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_CORP_CONTACT_LIST);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Query executed successfully.");
            for (Map.Entry<String, Collection<KnCorpSubscriberDTO>> entry : mdnContactListMap.entrySet()) {
                Collection<KnCorpSubscriberDTO> contacts = entry.getValue();
                String mdn = entry.getKey();
                for (KnCorpSubscriberDTO contactDto : contacts) {
                    //we do not want the  subcriber to be present as his own contact so we will skip if mdn and contactMdn is same
                    if (!contactDto.getMdn().equals(mdn)) {
                        pstmt.setString(1, mdn);
                        pstmt.setString(2, contactDto.getMdn());
                        int contactCorpId = contactDto.getCorpId();
                        if (contactCorpId <= 0) {
                            pstmt.setNull(3, java.sql.Types.INTEGER);
                        } else {
                            pstmt.setInt(3, contactCorpId);
                        }
                        pstmt.addBatch();
                    }
                }
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting the mdn contact list- ", e,"Data passed -",mdnContactListMap);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting the mdn contact list - ", e,"Data passed -",mdnContactListMap);
            throw KnDbUtil.processException(e, "Failed while inserting the mdn contact list " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public LinkedHashMap<String, LinkedList<Integer>> deleteMembersFromCorpContactList(LinkedHashMap<String, LinkedList<String>> mdnContactListMap,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteMembersFromCorpContactList(Map<String, Collection<String>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : mdnContactListMap - ", mdnContactListMap.size());
        String query = null;
        PreparedStatement pstmt = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_FROM_CORP_CONTACT_LIST_IF_NOT_CONTACT);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<String, LinkedList<String>> entry : mdnContactListMap.entrySet()) {
                Collection<String> contacts = entry.getValue();
                String mdn = entry.getKey();
                for (String contactMdn : contacts) {
                    pstmt.setString(1, mdn);
                    pstmt.setString(2, contactMdn);
                    pstmt.setString(3, mdn);
                    pstmt.setString(4, contactMdn);
                    pstmt.addBatch();
                }
            }
            int[] status = pstmt.executeBatch();
            LinkedHashMap<String, LinkedList<Integer>> deleteStatus = new LinkedHashMap<String, LinkedList<Integer>>();
            int i = 0;

            for(Map.Entry<String, LinkedList<String>> entry : mdnContactListMap.entrySet()){
                int j =0;
                LinkedList<Integer> statusList = new LinkedList<Integer>();
                for(String deleteMemberMdn : entry.getValue()){
                    statusList.add(status[i++]);
                }
                deleteStatus.put(entry.getKey(),statusList);
            }
            knLogger.debug( methodName, "Exit: Query executed successfully.", KnGDPRTemplate.mapKeyMdn(deleteStatus));
            return deleteStatus;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting the mdn contact list- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting the mdn contact list - ", e);
            throw KnDbUtil.processException(e, "Failed while inserting the mdn contact list " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Collection<String>> getSubscribersContactList(Collection<String> mdnList,
                                                                     KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersContactList(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point: mdnList size - ", getSize(mdnList));
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Map<String, Collection<String>> subscContactMap = new HashMap<String, Collection<String>>();
        boolean ownedTxn = false;
        Connection conn = null;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            //Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            ArrayList<String> mdnLists = new ArrayList<String>(mdnList);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnLists, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                int index = 1;
                query = queryMapper.getQuery(GET_SUBSCRIBERS_CONTACT_LIST);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subMdnList, query, MDNLIST);
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing query - ", query);
                for (String mdn : subMdnList)
                    pstmt.setString(index++, mdn);

                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    Collection<String> contactList = subscContactMap.get(mdn);
                    if (contactList == null) {
                        contactList = new ArrayList<String>();
                    }
                    String contactMdn = rs.getString(2).trim();
                    contactList.add(contactMdn);
                    subscContactMap.put(mdn, contactList);
                }
            }
            return subscContactMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while inserting the mdn contact list- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "SQLException occurred while inserting the mdn contact list - ", e);
            throw KnDbUtil.processException(e, "Failed while inserting the mdn contact list " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT. Subscriber Contact Map  size is - ", subscContactMap.size());
        }
    }

    public void updateOwnerMdnInContactList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateOwnerMdnInContactList(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : contactDTO - ", contactDTO);
        String query = null;
        PreparedStatement pstmt = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_OWNER_MDN_INT_CONTACT_LIST);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, contactDTO.getNewMdn());
            pstmt.setString(2, contactDTO.getMdn());
            pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the owner mdn in contact list- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while updating the owner mdn in contact list - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the owner mdn in contact list " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "Exit: Query executed successfully.");
        }
    }

    public void deleteSubscribersContactList(String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubscribersContactList(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ownerMdn - ", KnGDPRTemplate.mdn(ownerMdn));
        PreparedStatement pstmt = null;
        String query = null;
        try {

            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_CORP_CONTACT_LIST_FOR_MDN);
            knLogger.debug( methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ownerMdn);
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting from subscriber contact list table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting from corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to addGroupMembers in  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateContactCorpId(String corpId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateContactCorpId( String, String,  KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : InputDTO passed in request corpId,- ", corpId, ", mdn -", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.UPDATE_CONTACT_LIST_CORPID);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            if (corpId == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, Integer.parseInt(corpId));
            }
            pstmt.setString(2, mdn);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Executed query successfully,updated the contact corp id .");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the contact corp id  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updating the contact corp id - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void insertCorpContactListMembers(Map<String, Collection<KnCorpSubscriberDTO>> memberOfPrivateList,
                                             KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertCorpContactListMembers( Map<String, Collection<KnCorpSubscriberDTO>>, KnPersisterTxn )";
        knLogger.debug(methodName, "Entry : memberOfPrivateList size  - ", getMapSize(memberOfPrivateList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.INSERT_INTO_CORP_CONTACT_LIST);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<String, Collection<KnCorpSubscriberDTO>> entry : memberOfPrivateList.entrySet()) {
                Collection<KnCorpSubscriberDTO> subscList = entry.getValue();
                String mdn = entry.getKey();
                for (KnCorpSubscriberDTO subsc : subscList) {
                    pstmt.setString(1, mdn);
                    pstmt.setString(2, subsc.getMdn());
                    pstmt.setInt(3, subsc.getCorpId());
                    pstmt.addBatch();
                }
            }
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeBatch();             knLogger.debug(methodName, "EXIT: Executed query successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while insertCorpContactListMembers  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while insertCorpContactListMembers- ", e);
            throw KnDbUtil.processException(e, "Failed while insertCorpContactListMembers -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersContactDeatilsList(Collection<String> completeMdnList,
                                                                                         KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersContactDeatilsList(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point: completeMdnList - ", completeMdnList == null ? completeMdnList : KnGDPRTemplate.mdnList(completeMdnList) );
        String query = null;
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        int index = 1 ;
        Map<String, Collection<KnCorpSubscriberDTO>> subscContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_CONTACT_DETAILS);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(completeMdnList, query, MDNLIST);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pStmt = conn.prepareStatement(query);
            
             for(String completeMdn : completeMdnList)
            	 pStmt.setString(index++, completeMdn);
             
            rs = pStmt.executeQuery();
            
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                Collection<KnCorpSubscriberDTO> contactList = subscContactMap.get(mdn);
                if (contactList == null) {
                    contactList = new ArrayList<KnCorpSubscriberDTO>();
                }
                String contactMdn = rs.getString(2).trim();
                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                subsc.setMdn(contactMdn);
                subsc.setCorpId(rs.getInt(3));
                contactList.add(subsc);
                subscContactMap.put(mdn, contactList);
            }
            return subscContactMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting the mdn contact list- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while fetching the mdn contact list - ", e);
            throw KnDbUtil.processException(e, "Failed while fetaching the mdn contact list " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug( methodName, "EXIT: Query executed successfully,Subscriber Contact Map size is - ", subscContactMap.size());
        }
    }

    /**
     * Method to return a Map of MDN who has deleting MDN as contact with the deleting contact list.
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, List<String>> getSubsContactList(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsContactList(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point: mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        String query = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Map<String, List<String>> subscContactMap = new HashMap<String, List<String>>(mdnList.size());
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_CONTACTS_LIST);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.prepareStatement(query);
            int paramIndex = 1;
            for (String mdn : mdnList) {
                stmt.setString(paramIndex++, mdn);
            }
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                List<String> contactList = subscContactMap.get(mdn);
                if (contactList == null) {
                    contactList = new ArrayList<String>();
                }
                contactList.add(rs.getString(2).trim());
                subscContactMap.put(mdn, contactList);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetaching the mdn contact list ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT. Subscriber Contact Map ", KnGDPRTemplate.mapKeyMdn(subscContactMap));
        return subscContactMap;
    }

    /**
     * This method deleted the contactlist entries for mdnList.
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpContactList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpContactList(List<String>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_CONTACT_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pstmt.setString(1, mdn);
                pstmt.setString(2, mdn);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing from corp contact list ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public ArrayList<String> getDispContacts(Collection<String> nonDispGrpMember, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getDispContacts(Collection<String>,int,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : nonDispGrpMember - " , nonDispGrpMember);
        Connection conn;
        ArrayList<String> dispContactList = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            ArrayList<String> mdnList = new ArrayList<String>(nonDispGrpMember);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnList, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_DISPATCH_MEM_CONTACT);
                query = replaceContactWithValue(query, KnPersisterConstants.MDNLIST, formCommaSeperatedQuesMarks(subMdnList));
                pstmt = conn.prepareStatement(query);
                int paramIndex = 1;
                pstmt.setInt(paramIndex++, corpId);
                for (String mdn : subMdnList) {
                    pstmt.setString(paramIndex++, mdn);
                }
                pstmt.setInt(paramIndex++, corpId);
                knLogger.debug( methodName, "Executing query - ", query);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    dispContactList.add(rs.getString(1).trim());
                }
            }

            return dispContactList;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing from corp contact list ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method get the mdn info
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public Map<String, Integer> getPoCSubsDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPoCSubsDetails(List<String>, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> mdnMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_POC_SUBSCRIBER_INFO);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                knLogger.debug(methodName, "Query", query);
                stmt = conn.prepareStatement(query);
                int paramIndex = 1;
                for (String mdn : subsList) {
                    stmt.setString(paramIndex++, mdn);
                }
                rs = stmt.executeQuery();
                while (rs.next()) {
                    mdnMap.put(rs.getString(2).trim(), rs.getInt(4));
                }
            }
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the member details ",
                    pttServerId, KnDAOSourceTypes.SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return mdnMap;
    }

    public Collection<String> getSubscribersContactList(List<String> completeMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersContactList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point: completeMdnList - ", completeMdnList == null ? completeMdnList : KnGDPRTemplate.mdnList(completeMdnList));
        String query = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        boolean ownedTxn = false;
        Collection<String> subsMdns = new ArrayList<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_LIST);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(completeMdnList));
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            stmt = conn.prepareStatement(query);
            int paramIndex = 1;
            for (String mdn : completeMdnList) {
                stmt.setString(paramIndex++, mdn);
            }
            stmt.setInt(paramIndex++, corpId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                subsMdns.add(rs.getString(1).trim());
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
            return subsMdns;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error( methodName, "SQLException occured while fetching the contactMDN subscriber list - ", e);
            throw KnDbUtil.processException(e, "Failed while fetaching the contactMDN subscriber list " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug( methodName, "EXIT: Query executed successfully,Subscriber Contact Map size is - ", subsMdns.size());
        }
    }

    /**
     * This method deleted the contactlist entries for mdnList.
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpContactMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpContactMdnList(List<String>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_CONTACT_MDN_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            assert mdnList != null;
            for(String mdn : mdnList){
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing from corp contact list ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        }finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void updateLocwatcherMdn(String mdn, int locwatcher, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLocwatcherMdn(List<String>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdnList - ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_LOCWATCHER_MDN);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, locwatcher);
            pstmt.setString(2, mdn);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.execute();
            knLogger.debug(methodName, "Exit: Query executed successfully");
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (SQLException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while updating group memberList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<String> getProfileMdnsByMcids(Collection<String> mdnList, int dispGrpMem, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "DAO.getProfileMdnsByMcids()";
        knLogger.info(methodName, "Entry mdnList", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> profileMdnList = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            ArrayList<String> mdnLists = new ArrayList<String>(mdnList);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnLists, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_PROFILE_MDNS_FOR_LOC);
                query = replaceContactWithValue(query, KnPersisterConstants.MDNLIST, formCommaSeperatedQuesMarks(subMdnList));
                knLogger.debug(methodName, "query -", query);
                pstmt = conn.prepareStatement(query);
                int paramIndex = 1;
                pstmt.setInt(paramIndex++, dispGrpMem);
                for (String mdn : subMdnList) {
                    pstmt.setString(paramIndex++, mdn);
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    profileMdnList.add(rs.getString("MDN").trim());
                }
            }
            knLogger.debug(methodName, "Exit profileMdnList :", KnGDPRTemplate.mdnList(profileMdnList));


        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to selectProfileMdns " + e,
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return profileMdnList;
    }

    public boolean isCorpHierarchyMapped(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isCorpHierarchyMapped(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId - ", corpId, ", hierarchyId: ", hierarchyId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean isMapped = false;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPID_HIERARCHYID_MAP);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, hierarchyId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isMapped = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to validate CORPID-HIERARCHY_ID mapping",
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "isCorpHierarchyMapped:", isMapped);
        return isMapped;
    }


    public Map<String, Integer> getClusterId(Set<String> geoCodes, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getClusterId(Set<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: geoCodes - ", geoCodes);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<String, Integer> clusterIdMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CLUSTERID);
            query = replaceContactWithValue(query, GEOCODELIST, formCommaSeperatedQuesMarks(geoCodes));
            pstmt = conn.prepareStatement(query);
            for (String geoCode : geoCodes) {
                pstmt.setString(index++, geoCode);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                clusterIdMap.put(rs.getString("GEOCODE"), rs.getInt("CLUSTERID"));
            }
            knLogger.debug(methodName, "Exit: ClusterIdMap - ", clusterIdMap);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch ClusterId for GeoCodes: " + e,
                    pttServerId, KnDAOSourceTypes.DEPLOY_SITE_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return clusterIdMap;
    }

    public Map<Integer, String> getPocHome(int corpId, String hierarchyId, List<Integer> clusterIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocHome(String, String, List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId - ", corpId, ", hierarchyId - ", hierarchyId, ", clusterIds - ", clusterIds);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 3;
        Map<Integer, String> pocHomeMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POCHOME);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, CLUSTER_IDLIST, formCommaSeperatedIntegerQuesMarks(clusterIds));
            knLogger.debug(methodName, "query1:::", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, hierarchyId);
            for (int clusterId : clusterIds) {
                pstmt.setInt(index++, clusterId);
            }
            knLogger.debug(methodName, "query: ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                pocHomeMap.put(rs.getInt("CLUSTERID"), rs.getString("POCHOME"));
            }
            knLogger.debug(methodName, "Exit: PocHomeMap - ", pocHomeMap);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch PocHome for CorpId: " + corpId + ", HierarchyId: " + hierarchyId + ", ClusterIds: " + clusterIds, pttServerId, KnDAOSourceTypes.ANCHOR_POC_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }

        return pocHomeMap;
    }

    public void insertAnchorPocInfo(int corpId, String hierarchyId, int clusterId, String pocHome,
                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertAnchorPocInfo(int,String,int,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId - ", corpId, ", hierarchyId - ", hierarchyId,
                ", clusterId - ", clusterId, ", pocHome - ", pocHome);
        String customField1 = null;
        String customField2 = null;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_ANCHOR_INFO);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, hierarchyId);
            pstmt.setInt(3, clusterId);
            pstmt.setString(4, pocHome);
            pstmt.setString(5, customField1);
            pstmt.setString(6, customField2);
            int rowsInserted = pstmt.executeUpdate();
            knLogger.info(methodName, "Rows inserted: ", rowsInserted);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert into ANCHOR_POC_INFO: corpId - " + corpId +
                    ", hierarchyId - " + hierarchyId + ", clusterId - " + clusterId, pttServerId, KnDAOSourceTypes.ANCHOR_POC_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "EXIT");
    }

    public void updateAnchorPocInfo(int corpId, String hierarchyId, int clusterId, String pocHome,
                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateAnchorPocInfo(int,String,int,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId - ", corpId, ", hierarchyId - ", hierarchyId, ", clusterId - ", clusterId, ", pocHome - ", pocHome);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_INTO_ANCHOR_INFO);

            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, pocHome);
            pstmt.setInt(2, corpId);
            pstmt.setString(3, hierarchyId);
            pstmt.setInt(4, clusterId);

            int rowsUpdated = pstmt.executeUpdate();
            knLogger.info(methodName, "Rows updated: ", rowsUpdated);

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update ANCHOR_POC_INFO: corpId - " + corpId +
                    ", hierarchyId - " + hierarchyId + ", clusterId - " + clusterId, pttServerId, KnDAOSourceTypes.ANCHOR_POC_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "EXIT");
    }

    /**
     * Batch updates subscriber profile information in POCSUBSCRINFO table for allocation/unallocation operations.
     * 
     * <p><b>IMPORTANT - ALLOCATION/UNALLOCATION USE ONLY:</b></p>
     * This method is specifically designed for corporate hierarchy allocation and unallocation operations.
     * <b>DO NOT USE this method for other functionalities</b> as it has specific behavior for handling
     * service authentication status fields.
     * 
     * <p><b>SERVICE AUTH STATUS HANDLING:</b></p>
     * This method stores the SAME value of SERVICEAUTHSTATUS in both:
     * <ul>
     *   <li>SERVICEAUTHSTATUS column (current service authentication status)</li>
     *   <li>PREVSERVICEAUTHSTATUS column (previous service authentication status)</li>
     * </ul>
     * 
     * This is intentional for allocation/unallocation scenarios where the status transition
     * should maintain consistency between current and previous states during hierarchy operations.
     * 
     * <p><b>Updates performed:</b></p>
     * <ul>
     *   <li>Cluster ID</li>
     *   <li>POC Home server</li>
     *   <li>Presence Home server</li>
     *   <li>Service Auth Status (stored in both SERVICEAUTHSTATUS and PREVSERVICEAUTHSTATUS)</li>
     *   <li>Active Feature Set (ACTIVEFS2)</li>
     *   <li>Hierarchy ID</li>
     *   <li>Hierarchy Root</li>
     * </ul>
     * 
     * @param updatePocSubs List of KnAllocatePocSubsUpdateDTO containing subscriber update information for allocation/unallocation
     * @param persisterTxn Database transaction context. If null, a new transaction will be created and committed
     * @throws KnDAOException if database operation fails or connection issues occur
     * 
     * @see KnAllocatePocSubsUpdateDTO
     * @see com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpSubscrProfileController Allocation/Unallocation flows
     */
    public void updatePocSubsInfoBatch(List<KnAllocatePocSubsUpdateDTO> updatePocSubs, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updatePocSubsInfoBatch(int,String,int,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ", "updates size - ", updatePocSubs.size());
        PreparedStatement pstmt = null;
        String query = null;

        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_POCSUBSCRINFO);
            knLogger.debug(methodName, "Query Executed is : {}", query);
            pstmt = conn.prepareStatement(query);

            var batches = KnGeneralUtil.splitList(updatePocSubs, BULK_UPDATE_SIZE);
            for (var batch : batches) {
                for (KnAllocatePocSubsUpdateDTO allocatePocSubsUpdateDTO : batch) {
                    if (allocatePocSubsUpdateDTO.getClusterId() != null) {
                        pstmt.setInt(1, allocatePocSubsUpdateDTO.getClusterId());
                    } else {
                        pstmt.setNull(1, Types.INTEGER);
                    }
                    pstmt.setString(2, allocatePocSubsUpdateDTO.getPocHome());
                    pstmt.setString(3, allocatePocSubsUpdateDTO.getPresenceHome());
                    pstmt.setInt(4, allocatePocSubsUpdateDTO.getServiceAuthStatus());
                    pstmt.setString(5, allocatePocSubsUpdateDTO.getActiveFS2());
                    pstmt.setString(6, allocatePocSubsUpdateDTO.getHierarchyId());
                    pstmt.setString(7, allocatePocSubsUpdateDTO.getHierarchyRoot());
                    pstmt.setInt(8, allocatePocSubsUpdateDTO.getServiceAuthStatus());
                    pstmt.setLong(9, allocatePocSubsUpdateDTO.getLastProfileUpdateTime());
                    pstmt.setString(10, allocatePocSubsUpdateDTO.getMdn());

                    pstmt.addBatch();

                }
                pstmt.executeBatch();

            }

            knLogger.debug(methodName, "EXIT: Executed batch update successfully");
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (SQLException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update the POCSUBSCRINFO: pochome - ", pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "EXIT");
    }

    public String selectOpsCorporateFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws
            KnDAOException {
        String methodName = "selectOpsCorporateFS(int, boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        String corpFS = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_OPS_CORPFS2_BY_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                corpFS = rs.getString(1);
            } else {
                knLogger.error(methodName, "corpFS - ", corpFS);
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
            throw KnDbUtil.processException(e, "Failed while  retrieving the corpFS " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, "EXIT : corpFS - ", corpFS);
        }
    }

    public void deleteBulkSubscribersContactList(List<String> mdnList, KnPersisterTxn persisterTxn) throws 
        KnConnectionException, KnDBPersistenceException,KnDAOException {
        final String methodName = "deleteBulkSubscribersContactList(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ownerMdn - ", KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "DELETE FROM DG.CORPCONTACTLIST WHERE MDN = ?";
            //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            knLogger.debug( methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting from subscriber contact list table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting from corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to addGroupMembers in  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Finds all levels of hierarchy IDs linked from the given ID up to the root.
     * Table used: DG.CORP_HIERARCHY_DEPTH
     */
    public String traceToRoot(int corpId, String currentId, KnPersisterTxn persisterTxn) throws SQLException {
        final String methodName = "traceToRoot(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "==>inside traceToRoot : ");
        String parentId;
        try {
            parentId = getImmediateParent(corpId, currentId, persisterTxn);
            knLogger.debug(methodName, "==>parentId is : ", parentId);
        } catch (Exception e) {
            throw new SQLException("Failed to trace hierarchy root: " + e.getMessage(), e);
        }

        if (parentId == null || parentId.isEmpty()) {
            return currentId;
        }
        return traceToRoot(corpId, parentId, persisterTxn) + "." + currentId;
    }

    /**
     * Queries for the immediate parent (where DEPTH = 1)[cite: 50].
     */
    private String getImmediateParent(int corpId, String descendantId, KnPersisterTxn persisterTxn) throws SQLException {
        String methodName="getImmediateParent()";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String ancestorId = null;
        String query = "SELECT ANCESTOR_HIER_ID FROM DG.CORP_HIERARCHY_DEPTH WHERE CORPID = ? AND DESCENDANT_HIER_ID = ? AND DEPTH = 1";
        knLogger.debug(methodName, "==>query is : ", query);
        Connection conn = null;
        try {
            // Use the correct method for your environment to get DB connection
            knLogger.debug(methodName, "==>pttServerId is : " + pttServerId);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(/* add required parameters here */);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, descendantId);
            knLogger.debug(methodName, "==>pstmt is : " + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ancestorId = rs.getString("ANCESTOR_HIER_ID");
                knLogger.debug(methodName, "==>ancestorId is : ", ancestorId);
            }
        } catch (Exception e) {
            throw new SQLException("Failed to retrieve ancestor hierarchy: " + e.getMessage(), e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pstmt);
        }
        return ancestorId;
    }

    /**
     * Fetches geocodes mapped to each hierarchy ID for a given corporation.
     *
     * <p>This method builds a dynamic SQL query by expanding the {@code HIERARCHY_ID_LIST}
     * placeholder into a comma-separated list of bind markers ({@code ?}), then binds:
     * <ol>
     *   <li>{@code corpId} at parameter index 1</li>
     *   <li>Each hierarchy ID starting from parameter index 2</li>
     * </ol>
     *
     * <p>The result is aggregated into a map of:
     * <pre>{@code
     *   hierarchyId -> set of geocodes
     * }</pre>
     *
     * <p><b>Notes:</b>
     * <ul>
     *   <li>If multiple rows exist for the same hierarchy/geocode pair, the {@link Set} removes duplicates.</li>
     *   <li>Result set and statement resources are closed in {@code finally}.</li>
     *   <li>Database connection lifecycle is managed by the provided {@code persisterTxn}.</li>
     * </ul>
     *
     * @param corpId       corporation identifier used as the first query filter parameter
     * @param hierarchyIds hierarchy IDs to filter by; each value is bound to the expanded IN-clause
     * @param persisterTxn active persistence transaction used to obtain DB connection
     * @return map of hierarchy ID to the set of mapped geocodes; empty if no rows are found
     * @throws KnDAOException if DAO-layer errors occur directly, or when unexpected exceptions are wrapped
     */
    public Map<String, Set<String>> getHierarchyMappedGeocode(int corpId, Set<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {

        var methodName = "getHierarchyMappedGeocode(corpId, hierarchyIds, txnPrisister)";
        knLogger.debug(methodName, "ENTRY: corpId - ", corpId, ", hierarchyIds - ", hierarchyIds);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Set<String>> hierarchyMappedGeocode = new HashMap<>();
        String query;
        Connection conn;
        // Parameter index starts at 2 because index 1 is reserved for corpId.
        var index = 2;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            knLogger.debug(methodName, "==>PTT ServerId is : " + pttServerId);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            query = queryMapper.getQuery(KnPersisterConstants.GET_HIERARCHY_GEOCODE_MAP);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(hierarchyIds, query, "HIERARCHY_ID_LIST");
            knLogger.debug(methodName, "==>query is : ", query);

            pstmt = conn.prepareStatement(query);

            // Bind fixed filter parameter.
            pstmt.setInt(1, corpId);

            // Bind dynamic hierarchy ID parameters.
            for (String hierarchyId : hierarchyIds) {
                pstmt.setString(index++, hierarchyId);
            }
            rs = pstmt.executeQuery();
            // Aggregate rows into: hierarchyId -> unique geocodes.
            while (rs.next()) {
                String hierarchyId = rs.getString("HIERARCHY_ID");
                String geocode = rs.getString("GEOCODE");
                hierarchyMappedGeocode.computeIfAbsent(hierarchyId, k -> new HashSet<>()).add(geocode);
            }
        } catch (KnDAOException e) {
            // Connection establishment issue.
            knLogger.error(methodName, "KnDAOException occurred while fetching hierarchy mapped geocode- ", e);
            throw e;
        } catch (Exception e) {
            // Unknown issue.
            knLogger.error(methodName, "Unexpected Exception occurred while fetching hierarchy mapped geocode- ", e);
            throw KnDbUtil.processException(e, "Failed to fetch geocode for Hierarchy " + e, pttServerId,
                    KnDAOSourceTypes.XDM_HIERARCHY_MAPPED_GEOCODE, null);
        } finally {
            // Ensure JDBC resources are always closed.
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pstmt);
            knLogger.debug(methodName, "EXIT: hierarchyMappedGeocode - ", hierarchyMappedGeocode);
        }

        return hierarchyMappedGeocode;
    }

    public String getPocHomeByHierarchyIdFromAnchor(String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocHomeByHierarchyIdFromAnchor(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: hierarchyId - ", hierarchyId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String pocHome = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POCHOME_BY_HIERARCHYID_FROM_ANCHOR);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, hierarchyId);
            rs = pstmt.executeQuery();
            while(rs.next()){
                pocHome = rs.getString("POCHOME");
                if (pocHome != null && !pocHome.isEmpty() && !pocHome.trim().equals("0"))break; // Exit loop if a non-null POC home is found
            }
            knLogger.debug(methodName, "Exit: pocHome - ", pocHome);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch PocHome for HierarchyId: " + hierarchyId,
                    pttServerId, KnDAOSourceTypes.ANCHOR_POC_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return pocHome;

    }


    public String getPocHomeByGeocodes(Set<String> geocodes, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocHomeByGeocodes(Set<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: geocodes - ", geocodes);
        List<String> geocodeList = geocodes == null ? Collections.<String>emptyList() : new ArrayList<>(geocodes);
        if (geocodeList.isEmpty()) {
            return null;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String pocHome = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POCHOME_BY_GEOCODES);
            query = replaceContactWithValue(query, GEOCODELIST, formCommaSeperatedQuesMarks(geocodeList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            int paramIndex = 1;
            for (String geocode : geocodeList) {
                pstmt.setString(paramIndex++, geocode);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                pocHome = rs.getString("POCHOME");
                if (pocHome != null && !pocHome.isEmpty() && !pocHome.trim().equals("0")) break; // Exit loop if a non-null POC home is found
            }
            knLogger.debug(methodName, "Exit: pocHome - ", pocHome);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch PocHome for Geocodes: " + geocodes,
                    pttServerId, KnDAOSourceTypes.ANCHOR_POC_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return pocHome;
    }

}