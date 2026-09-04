/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupMemberDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;

import java.util.Collection;
import java.util.ArrayList;
import java.sql.*;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPOCGroupMemberDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 13, 2011           7.0
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
public class KnPOCGroupMemberDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCGroupMemberDAO.class);

    public static final String CLASSNAME = KnPOCGroupMemberDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_POCGROUPMEMBER";

    public static final String POC_GROUP_ID     = "POCGROUPID";
    public static final String MEMBER_MDN       = "MEMBERMDN";
    public static final String MEMBER_NAME      = "MEMBERNAME";

    public String pttServerId = null;

    public KnPOCGroupMemberDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String QRY_INSERT_MEMBER = "INSERT INTO " + TABLENAME + " VALUES(?,?,?)";
    public static final String QRY_DELETE_MEMBER = "DELETE FROM " + TABLENAME + " WHERE " + POC_GROUP_ID +
            " = ? AND " + MEMBER_MDN + " = ?";
    public static final String QRY_DELETE_ALL_MEMBERS = "DELETE FROM " + TABLENAME + " WHERE " + POC_GROUP_ID + " = ?";
    public static final String QRY_SELECT_POC_GRP_MEMBERS = "SELECT " + MEMBER_MDN + ","+MEMBER_NAME+
            " FROM " + TABLENAME + " WHERE " + POC_GROUP_ID + " = ?";
    public static final String QRY_SELECT_GRP_MEM_COUNT = "SELECT COUNT(" + MEMBER_MDN + ") FROM " + TABLENAME
            + " WHERE " + POC_GROUP_ID + " = ?";
    public static final String QRY_SELECT_MEMBER = "SELECT " + POC_GROUP_ID + " FROM " + TABLENAME + " WHERE " + POC_GROUP_ID +
            " = ? AND " + MEMBER_MDN + " = ?";
    public static final String QRY_DELETE_MEMBERS_FROM_ALL_GROUPS = "DELETE FROM " + TABLENAME + " WHERE " +
            POC_GROUP_ID + " = ?";
    public static final String QRY_UPDATE_MEMBER = "UPDATE " + TABLENAME + " SET " + MEMBER_NAME + "  = ?  WHERE " +
            POC_GROUP_ID + " = ? AND " + MEMBER_MDN + " = ?";

    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "select", "Not Implemented");
        return null;
    }


    /**
     * @param pocGroupId
     * @param memberMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean checkGroupMemberExists(int pocGroupId, String memberMdn, boolean readonly,
                                          KnPersisterTxn persisterTxn) throws KnDAOException{

        String methodName = "checkGroupMemberExists(int, String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        boolean result = false;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_MEMBER;

            //conn = persisterTxn.getDBConnection(pttServerId, readonly);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pStatement = conn.prepareStatement(query);

            pStatement.setInt(1, pocGroupId);
            pStatement.setString(2, memberMdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", pocGroupId: " + pocGroupId +
                    ", memberMdn: " + KnGDPRTemplate.mdn(memberMdn) + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                result = true;
            }
            knLogger.debug( methodName, "QUERY : Completed : result" + result);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "checkGroupMemberExists:  " + KnGDPRTemplate.mdn(memberMdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to checkGroupMemberExists - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to checkGroupMemberExists - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : checkGroupMemberExists result ->" + result);
        }
        return result;
    }


    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addPocGroupMembers(int pocGroupId, Collection<KnGroupMemberDTO> members,
                                   KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addGroupMembers(int, Collection<KnGroupMemberDTO>, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_INSERT_MEMBER;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            for (KnGroupMemberDTO member : members) {
                pStatement.setInt(1, pocGroupId);
                pStatement.setString(2, member.getMemberMdn());
                //multilingual revert change
                if(null != member.getMemberName())
                {
                	pStatement.setString(3,  new String(member.getMemberName().getBytes("UTF-8"),"8859_1"));
                }
                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int[] count = pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Insert member(s) to POC Group:  " + pocGroupId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : PocGroupID ->" + pocGroupId);
        }
    }


    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void modifyPocGroupMembers(int pocGroupId, Collection<KnGroupMemberDTO> members,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "modifyPocGroupMembers(int, Collection<KnGroupMemberDTO>, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_MEMBER;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            for (KnGroupMemberDTO member : members) {
            	//multilingual revert change
            	if(null != member.getMemberName())
                {
                	pStatement.setString(1,  new String(member.getMemberName().getBytes("UTF-8"),"8859_1"));
                }
                pStatement.setInt(2, pocGroupId);
                pStatement.setString(3, member.getMemberMdn());

                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Update member(s) to POC Group:  " + pocGroupId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : PocGroupID ->" + pocGroupId);
        }
    }


    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupMembers(int pocGroupId, Collection<String> members,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroupMembers(int, Collection<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_DELETE_MEMBER;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            for (String member : members) {
                pStatement.setInt(1, pocGroupId);
                pStatement.setString(2, member);

                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Delete member(s) to POC Group:  " + pocGroupId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : PocGroupId ->" + pocGroupId);
        }
    }

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllPocGroupMembers(int pocGroupId,
                                         KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllPocGroupMembers(int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_DELETE_ALL_MEMBERS;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            pStatement.setInt(1, pocGroupId);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Delete member(s) to POC Group:  " + pocGroupId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete All member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete All member(s) to Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : PocGroupId ->" + pocGroupId);
        }
    }

    /**
     * Fetch all group members for given PoCGroupId
     *
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public Collection<KnGroupMemberDTO> getPocGroupMembers(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocGroupMembers(int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnGroupMemberDTO> groupMemberList = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_POC_GRP_MEMBERS;

            //conn = persisterTxn.getDBConnection(pttServerId, true);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);

            pStatement.setInt(1, pocGroupId);

            knLogger.debug( methodName, "QUERY : Executing " + query + " pocGroupId: " + pocGroupId
                    +  ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();

            if (rs.next()) {
                groupMemberList = new ArrayList<KnGroupMemberDTO>();
                do {
                    KnGroupMemberDTO groupMemberInfo = new KnGroupMemberDTO();
                    groupMemberInfo.setMemberMdn(rs.getString(1).trim());
                    //multilingual revert change
                    if(null != rs.getString(2))
                    {
                    	groupMemberInfo.setMemberName(new String(rs.getString(2).getBytes("8859_1"),"UTF-8"));
                    }
                    groupMemberList.add(groupMemberInfo);
                } while (rs.next());
            } else {
                //throw exception
                knLogger.error( methodName, "No Group Info found");
//                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning Group Member list - " + groupMemberList);
            return groupMemberList;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Group Members for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Group Members for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Group Members List ->" + groupMemberList);
        }
    }

    /**
     * Fetch group members count for given PoCGroupId
     *
     * @param pocGroupId
     * @param readonly
     * @param persisterTxn
     * @throws KnDAOException
     */
    public int getPocGroupMembersCount(int pocGroupId, boolean readonly,
                                       KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getPocGroupMembersCount(int, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int memCount = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_GRP_MEM_COUNT;

            //conn = persisterTxn.getDBConnection(pttServerId, readonly);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pStatement = conn.prepareStatement(query);

            pStatement.setInt(1, pocGroupId);

            knLogger.debug( methodName, "QUERY : Executing " + query + " pocGroupId: " + pocGroupId
                    +  ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();

            if (rs.next()) {
                memCount = rs.getInt(1);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning number of group members - " + memCount);
            return memCount;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve number of group members - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve number of group members - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Returning number of group members ->" + memCount);
        }
    }

    /**
     * @param groupsIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteMembersFromAllGroups(Collection<Integer> groupsIds, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "deleteMembersFromAllGroups(String, KnPersisterTxn)";
        knLogger.debug( methodName, groupsIds);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;

        try {

            //StringBuffer buffer = new StringBuffer(200);
            //buffer.append(QRY_DELETE_MEMBERS_FROM_ALL_GROUPS).append(KnDbUtil.convertListToIntBuffer(groupsIds));
            //query = buffer.toString();
            query = QRY_DELETE_MEMBERS_FROM_ALL_GROUPS;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            for(Integer groupId : groupsIds){
                pstmt.setInt(1, groupId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed : ");
            knLogger.info( methodName, "Delete members from all groups :groupsIds:  " + groupsIds);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to Delete members from all Groups :groupsIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Delete members from all Groups :groupsIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPMEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT");
        }
    }


}
