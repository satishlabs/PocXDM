/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnContactListMemberDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnContactListMemberDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnContactListMemberDAO.class);

    public static final String CLASSNAME = KnContactListMemberDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_CONTACTLISTMEMBER";

    public static final String CONTACT_LIST_ID = "CONTACTLISTID";
    public static final String MEMBER_MDN = "MEMBERMDN";
    public static final String MEMBER_NAME = "MEMBERNAME";

    public String pttServerId = null;
    
    public KnContactListMemberDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String QRY_INSERT_MEMBER = "INSERT INTO " + TABLENAME + " VALUES(?,?,?)";
    public static final String QRY_DELETE_MEMBER = "DELETE FROM " + TABLENAME + " WHERE " + CONTACT_LIST_ID +
            " = ? AND " + MEMBER_MDN + " = ?";
    public static final String QRY_SELECT_MEMBER = "SELECT " + CONTACT_LIST_ID + ", " + MEMBER_MDN +
            " FROM " + TABLENAME + " WHERE " + CONTACT_LIST_ID +
            " = ? AND " + MEMBER_MDN + " = ?";
    public static final String QRY_COUNT_CONTACT_MEMS = "SELECT COUNT(" + MEMBER_MDN + ") FROM " + TABLENAME
            + " WHERE " + CONTACT_LIST_ID + " = ?";
    public static final String QRY_SELECT_MEMBERS_IN = "SELECT " + CONTACT_LIST_ID + ", " + MEMBER_MDN
    		+","+MEMBER_NAME+ " FROM " + TABLENAME + " WHERE " + CONTACT_LIST_ID + " IN ";
    public static final String QRY_DELETE_ALL_CONTACTS = "DELETE FROM " + TABLENAME + " WHERE " +
            CONTACT_LIST_ID + " = ?";
    public static final String QRY_UPDATE_MEMBER = "UPDATE " + TABLENAME + " SET " + MEMBER_NAME + " = ? WHERE " +
            CONTACT_LIST_ID + " = ? AND " + MEMBER_MDN + " = ?";

    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("select", "Not Implemented");
        return null;
    }


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addContacts(int contactListId, Collection<KnMemberDTO> members,
                            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addContacts(int, Collection<KnMemberDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {

            query = QRY_INSERT_MEMBER;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            for (KnMemberDTO member : members) {
                pStatement.setInt(1, contactListId);
                pStatement.setString(2, member.getMemberMdn());
				//Multilingual revert changes - Converting String to Bytes before inserting into DB
                if(member.getMemberName() != null)
                	pStatement.setString(3, new String(member.getMemberName().getBytes("UTF-8"),"8859_1"));
                pStatement.addBatch();
            }
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            int[] count = pStatement.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.", count);

            knLogger.info(methodName, "Insert member(s) to contactList:  ", contactListId);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to add member(s) to contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void modifyContacts(int contactListId, Collection<KnMemberDTO> members,
                               KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "modifyContacts(int, Collection<KnMemberDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {
            query = QRY_UPDATE_MEMBER;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStatement = conn.prepareStatement(query);

            for (KnMemberDTO member : members) {
                //multilingual revert changes
            	if(member.getMemberName() != null)
            		pStatement.setString(1, new String(member.getMemberName().getBytes("UTF-8"),"8859_1"));
                pStatement.setInt(2, contactListId);
                pStatement.setString(3, member.getMemberMdn());

                pStatement.addBatch();
            }
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            pStatement.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.");


            knLogger.info(methodName, "Update member(s) to Contact List:  ", contactListId);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update member(s) to Contact List - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }


    /**
     * @param contactListId
     * @param memberMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean checkContactDetails(int contactListId, String memberMdn, boolean readonly,
                                       KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "checkContactDetails(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        boolean result = false;

        try {

            query = QRY_SELECT_MEMBER;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            // conn = persisterTxn.getDBConnection(pttServerId, readonly);
            pStatement = conn.prepareStatement(query);

            pStatement.setInt(1, contactListId);
            pStatement.setString(2, memberMdn);

            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                result = true;
            }
            knLogger.debug(methodName, "QUERY : Completed : result", result);

            knLogger.debug(methodName, "EXIT : check Contact Details ->", result);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to check Contact Details in contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        return result;
    }


    /**
     * @param contactListId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int countContactMembers(int contactListId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "checkContactDetails(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int result = 0;

        try {


            query = QRY_COUNT_CONTACT_MEMS;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            //conn = persisterTxn.getDBConnection(pttServerId, true);
            pStatement = conn.prepareStatement(query);

            pStatement.setInt(1, contactListId);

            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            knLogger.debug(methodName, "QUERY : Completed : result", result);


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to count Contact mem in contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : Count Contact Details ->", result);
        }
        return result;
    }


    /**
     * @param contactListIds
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<KnMemberDTO>> getMembersForContactIds(Collection<Integer> contactListIds, boolean readonly,
                                                                         KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getMembersForContactIds(Collection<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Collection<KnMemberDTO>> contactVsMembersMap = null;

        try {
            StringBuilder buffer = new StringBuilder(200);
            buffer.append(QRY_SELECT_MEMBERS_IN).append(KnDbUtil.convertListToIntBuffer(contactListIds));
            query = buffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            //conn = persisterTxn.getDBConnection(pttServerId, readonly);
            statement = conn.createStatement();

            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = statement.executeQuery(query);
            knLogger.debug(methodName, "QUERY : Completed : ");

            if (rs.next()) {
                contactVsMembersMap = new HashMap<Integer, Collection<KnMemberDTO>>();

                do {
                    Integer listId = rs.getInt(1);
                    String listMem = rs.getString(2).trim();
                    //Multilingual revert Changes - Converting Bytes to String after fetching from DB
                    String memName =null;
                    if(rs.getString(3) != null)
                    	memName=  new String(rs.getString(3).getBytes("8859_1"),"UTF-8");
                    KnMemberDTO memDetials = new KnMemberDTO();
                    memDetials.setMemberMdn(listMem);
                    memDetials.setMemberName(memName);
                    Collection<KnMemberDTO> memList = contactVsMembersMap.get(listId);
                    if (null == memList) {
                        memList = new ArrayList<KnMemberDTO>();
                        contactVsMembersMap.put(listId, memList);
                    }
                    memList.add(memDetials);
                } while (rs.next());
            }

            knLogger.info(methodName, "Member Details in contactLists:  ", contactVsMembersMap);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Member Details in contactLists - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
        }
        return contactVsMembersMap;
    }


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteContacts(int contactListId, Collection<String> members,
                               KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContacts(int, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {

            query = QRY_DELETE_MEMBER;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            for (String member : members) {
                pStatement.setInt(1, contactListId);
                pStatement.setString(2, member);

                pStatement.addBatch();
            }
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            pStatement.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.");

            knLogger.info(methodName, "Delete member(s) to contactList:  ", contactListId);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Delete member(s) to contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }


    /**
     * @param contactListIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllContacts(Collection<Integer> contactListIds, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllContacts(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", contactListIds);

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;

        try {

            //StringBuilder buffer = new StringBuilder(200);
            //buffer.append(QRY_DELETE_ALL_CONTACTS).append(KnDbUtil.convertListToIntBuffer(contactListIds));
            //query = buffer.toString();
            query = QRY_DELETE_ALL_CONTACTS;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);

            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            for(Integer contactListId : contactListIds){
                pstmt.setInt(1, contactListId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed : ");

            knLogger.info(methodName, "Delete all contacts from contactListIds:  ", contactListIds);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to Delete all contacts from contactListIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

}
