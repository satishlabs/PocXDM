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
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;

import java.sql.*;
import java.util.*;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPOCContactListDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Ajit Kumar             Aug 01, 2011           7.0
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
public class KnPOCContactListDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCContactListDAO.class);

    public static final String TABLENAME = "DG.XDM_POCCONTACTLIST";

    public static final String MDN = "MDN";
    public static final String CONTACT_MDN = "CONTACTMDN";
    public static final String UFMI = "UFMI";
    public static final String CONTACT_TYPE = "CONTACT_TYPE";

    public static final int CONTACT_TYPE_3 = 3;
    public static final int CONTACT_TYPE_2 = 2;
    public String pttServerId = null;

    public KnPOCContactListDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public static final String QRY_INSERT_MEMBER = "INSERT INTO " + TABLENAME + " VALUES(?,?,?,?)";
    public static final String QRY_DELETE_MEMBER = "DELETE FROM " + TABLENAME + " WHERE " + MDN +
            " = ? AND " + CONTACT_MDN + " = ?";
    public static final String QRY_SELECT_MEMBER = "SELECT " + MDN + ", " + CONTACT_MDN +
            " FROM " + TABLENAME + " WHERE " + MDN +
            " = ? AND " + CONTACT_MDN + " = ?";
    public static final String QRY_COUNT_CONTACT_MEMS = "SELECT COUNT(" + CONTACT_MDN + ") FROM " + TABLENAME
            + " WHERE " + MDN + " = ?";
    public static final String QRY_SELECT_MEMBERS_IN = "SELECT " + MDN + ", " + CONTACT_MDN
            + " FROM " + TABLENAME + " WHERE " + MDN + " IN ";
    public static final String QRY_DELETE_ALL_CONTACTS = "DELETE FROM " + TABLENAME + " WHERE " +
            MDN + " = ? ";
    public static final String QRY_UPDATE_MEMBER = "UPDATE " + TABLENAME + " SET " + CONTACT_MDN + " = ? WHERE " +
            MDN + " = ?";
    public static final String QRY_UPDATE_MDN = "UPDATE " + TABLENAME + " SET " + MDN + " = ? WHERE " +
            MDN + " = ?";
    public static final String QRY_DELETE_ALL_CONTACTS_4_MDNS = "DELETE FROM " + TABLENAME + " WHERE " +
            MDN + " IN ";

    public static final String QRY_SELECT_UFMI_FOR_CONTACT_MDNS = "SELECT " + CONTACT_MDN + ", " + UFMI
            + " FROM " + TABLENAME + " WHERE " + CONTACT_MDN + " IN ";

    public static final String QRY_UPDATE_UFMI_FOR_CONTACTMDN = "UPDATE " + TABLENAME + " SET " + UFMI + " = ? WHERE " +
            MDN + " = ? AND "+CONTACT_MDN+"=?";

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
     * @param mdn
     * @param contactMDNs
     * @param persisterTxn
     * @throws KnDAOException
     *
     */
    public void addContacts(String mdn, Collection<KnMemberDTO> members,
                            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "addContacts(String, Collection<String>, KnPersisterTxn)";
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

            for (KnMemberDTO contactMDN : members) {
                pStatement.setString(1, mdn);
                pStatement.setString(2, contactMDN.getMemberMdn());
                pStatement.setInt(3, CONTACT_TYPE_2);
                if(contactMDN.getUfmi() != null){
                    pStatement.setInt(3, CONTACT_TYPE_3);
                }
                pStatement.setString(4, contactMDN.getUfmi());
                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Insert member(s) to poccontactList:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add member(s) to poccontactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
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
            throw KnDbUtil.processException(e, "Failed to add member(s) to poccontactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : poccontactList ->" + KnGDPRTemplate.mdn(mdn));
        }
    }

    /**
     * @param mdn
     * @param newMdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateMdn(String mdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateMdn(String, String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : Mdn :" + KnGDPRTemplate.mdn(mdn) + ", New Mdn :" + KnGDPRTemplate.mdn(newMdn));

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

            query = QRY_UPDATE_MDN;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, newMdn);
            pStatement.setString(2, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Mdn for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Mdn for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
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
            throw KnDbUtil.processException(e, "Failed to update Mdn for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }
    }


    /**
     * @param mdn
     * @param contactMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     *
     */
    public boolean checkContactDetails(String mdn, String contactMdn, boolean readonly,
                                       KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "checkContactDetails(String, String, boolean, KnPersisterTxn)";
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

            pStatement.setString(1, mdn);
            pStatement.setString(2, contactMdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                result = true;
            }
            knLogger.debug( methodName, "QUERY : Completed : result" + result);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "check Contact Details in POC contactList:  " + KnGDPRTemplate.mdn(contactMdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to check Contact Details in POC contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
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
            throw KnDbUtil.processException(e, "Failed to check Contact Details in POC contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : check Contact Details ->" + result);
        }
        return result;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     *
     */
    public int countContactMembers(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "countContactMembers(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int result = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_COUNT_CONTACT_MEMS;

            //conn = persisterTxn.getDBConnection(pttServerId, true);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);

            pStatement.setString(1, mdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            knLogger.debug( methodName, "QUERY : Completed : result" + result);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "count Contact mdn in poc contactList:  " + result);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to count Contact mdn in poc contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
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
            throw KnDbUtil.processException(e, "Failed to count Contact mdn in poc contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Count Contact Details ->" + result);
        }
        return result;
    }


    /**
     * @param mdns
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     *
     */
    public Map<String, Collection<String>> getContactMDNsForMDNs(Collection<String> mdns, boolean readonly,
                                                                 KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getContactMDNsForMDNs(Collection<String>, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Collection<String>> mdnVscontactMDNMap = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_MEMBERS_IN).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();

            //conn = persisterTxn.getDBConnection(pttServerId, readonly);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            statement = conn.createStatement();

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Completed : ");

            if (rs.next()) {
                mdnVscontactMDNMap = new HashMap<String, Collection<String>>();

                do {
                    String mdn = rs.getString(1).trim();
                    String contactMDN = rs.getString(2).trim();

                    Collection<String> contactMDNList = mdnVscontactMDNMap.get(mdn);
                    if (null == contactMDNList) {
                        contactMDNList = new ArrayList<String>();
                        mdnVscontactMDNMap.put(mdn, contactMDNList);
                    }
                    contactMDNList.add(contactMDN);
                } while (rs.next());
            }


            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "contact mdn in poccontactLists:  " + KnGDPRTemplate.mapKeyValueListMdn(mdnVscontactMDNMap));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to get contact mdn in poccontactLists - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
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
            throw KnDbUtil.processException(e, "Failed to get contact mdn in poc contactLists - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug( methodName, "EXIT : Contact mdns ->" + KnGDPRTemplate.mapKeyValueListMdn(mdnVscontactMDNMap));
        }
        return mdnVscontactMDNMap;
    }


    /**
     * @param mdn
     * @param contactMDNs
     * @param persisterTxn
     * @throws KnDAOException
     *
     */
    public void deleteContacts(String mdn, Collection<String> contactMDNs,
                               KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContacts(String, Collection<String>, KnPersisterTxn)";
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

            for (String contact : contactMDNs) {
                pStatement.setString(1, mdn);
                pStatement.setString(2, contact);

                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Delete member(s) to poc contactList:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete member(s) to poc contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
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
            throw KnDbUtil.processException(e, "Failed to Delete member(s) to poc contactList - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT :poc contactList ->" + KnGDPRTemplate.mdn(mdn));
        }
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     *
     */
    public void deleteAllContacts(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllContacts(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int count;
        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_DELETE_ALL_CONTACTS;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            count = pStatement.executeUpdate();

            knLogger.debug( methodName, "QUERY : Completed : "+ count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Delete all contacts for mdns:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete all contacts for mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
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
            throw KnDbUtil.processException(e, "Failed to Delete all contacts from mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : contactListIds ->" + KnGDPRTemplate.mdn(mdn));
        }
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     *
     */
    public void deleteAllContacts(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteAllContacts(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement  statement = null;
        ResultSet rs = null;
        String query = null;
        int count;
        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_DELETE_ALL_CONTACTS_4_MDNS).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();


            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            count = statement.executeUpdate(query);

            knLogger.debug( methodName, "QUERY : Completed : "+ count);

            knLogger.info( methodName, "Delete all contacts for mdns:  " + KnGDPRTemplate.mdnList(mdns));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Delete all contacts for mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Delete all contacts from mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }
    }


    public Map<String, String> getUfmiForContactMDNs(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getUfmiForContactMDNs(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;
        Map<String, String> contactMdnVsUfmiMap = null;

        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_UFMI_FOR_CONTACT_MDNS).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();

            //conn = persisterTxn.getDBConnection(pttServerId, readonly);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            statement = conn.createStatement();

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " + persisterTxn);
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Completed : ");

            if (rs.next()) {
                contactMdnVsUfmiMap = new HashMap<String, String>();

                do {
                    String contactMDN = rs.getString(1);
                    String ufmi = rs.getString(2);
                    contactMdnVsUfmiMap.put(contactMDN.trim(),ufmi);
                } while (rs.next());
            }
            knLogger.info( methodName, "ufmi in poccontactLists:  " + KnGDPRTemplate.mdnMap(contactMdnVsUfmiMap));
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to get ufmi mdn in poc contactLists - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug( methodName, "EXIT : Contact mdns and their ufmi ->" + KnGDPRTemplate.mdnMap(contactMdnVsUfmiMap));
        }
        return contactMdnVsUfmiMap;
    }

    public void updateContactUfmi(String mdn, String contactMDN, String ufmi, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdn(String, String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : Mdn :" , KnGDPRTemplate.mdn(mdn) , ", contact Mdn :" , KnGDPRTemplate.mdn(contactMDN), "UFMI", ufmi);

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {


            query = QRY_UPDATE_UFMI_FOR_CONTACTMDN;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, ufmi);
            pStatement.setString(2, mdn);
            pStatement.setString(3, contactMDN);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);


            knLogger.info( methodName, "Updated UFMI for contact for MDN:  " + KnGDPRTemplate.mdn(mdn));

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update Ufmi for contact MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCCONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }
    }
}
