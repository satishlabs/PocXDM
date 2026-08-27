/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.kodiak.xdms.server.pubmgmt.resources.KnPublicUtil.getComSepList;
import static com.kodiak.xdms.server.pubmgmt.resources.KnPublicUtil.getIntComSepList;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnContactListDAO.java
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
public class KnContactListDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnContactListDAO.class);

    public static final String CLASSNAME = KnContactListDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_CONTACTLIST";

    public static final String CONTACT_LIST_ID = "CONTACTLISTID";
    public static final String OWNER_MDN = "OWNERMDN";
    private static final String ID_LIST = "IDLIST";
    public static final String CONTACT_LIST_ID_LIST = "CONTACTLISTID_LIST";

    public String pttServerId = null;

    public KnContactListDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public static final String QRY_SELECT_CONTACT_LIST_IDS = "SELECT " + CONTACT_LIST_ID + " FROM " + TABLENAME
            + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_UPDATE_MDN = "UPDATE " + TABLENAME + " SET " + OWNER_MDN + " = ? "
            + " WHERE " + OWNER_MDN + " = ?";

    public static final String QRY_SELECT_CONTACT_LIST_IDS_4_MDNS = "SELECT " + CONTACT_LIST_ID + " FROM " + TABLENAME
            + " WHERE " + OWNER_MDN + " IN (" + ID_LIST + ")";

    public static final String QRY_SELECT_CORP_LIST_ID ="SELECT CLI.CORPLISTID FROM DG.CORPLISTINFO CLI, DG.CORPLISTDISTINFO CDI WHERE CDI.RECIPIENTMDN = ? " +
            "AND CLI.CORPLISTID=CDI.CORPLISTID AND (CLI.LISTDISTRIBUTIONPOLICY=3 OR CLI.LISTDISTRIBUTIONPOLICY=5) ;";

    public static final String QRY_SELECT_MEMBERMDN_FOR_CORPLISTID ="SELECT MEMBERMDN FROM DG.CORPLISTMEMBER WHERE CORPLISTID  IN (" + CONTACT_LIST_ID_LIST + ") AND MEMBERCORPID=?;";
    public static final String QRY_SELECT_MEMBERMDN_FOR_CORPLISTID_WITHOUT_CORPID ="SELECT MEMBERMDN FROM DG.CORPLISTMEMBER WHERE CORPLISTID  IN (" + CONTACT_LIST_ID_LIST + ");";

    public static final String QRY_SELECT_PRIVATE_CONTACTS_FOR_MDN="SELECT CONTACTMDN FROM DG.CORPCONTACTLIST WHERE MDN=?;";

   public static final String QRY_SELECT_COMMON_CORP_LIST_ID ="SELECT CLI.CORPLISTID FROM DG.CORPLISTINFO CLI, DG.CORPLISTDISTINFO CDI WHERE CDI.RECIPIENTMDN = ? " +
            "AND CLI.CORPLISTID=CDI.CORPLISTID AND CLI.LISTDISTRIBUTIONPOLICY=6;";

    public static final String QRY_GET_ALL_COMMON_CONTACTLIST_MDNS ="SELECT MEMBERMDN FROM DG.CORPLISTMEMBER WHERE CORPLISTID IN (SELECT CORPLISTID FROM DG.CORPLISTINFO WHERE CORPLISTID IN (SELECT CORPLISTID FROM DG.CORPLISTDISTINFO WHERE RECIPIENTMDN = ?) and LISTDISTRIBUTIONPOLICY = ?);";

    public static final String QRY_GET_SUBS_NON_COMMON_CONTACT_LIST = "SELECT MEMBERMDN FROM DG.CORPLISTMEMBER WHERE CORPLISTID IN (SELECT CORPLISTID FROM DG.CORPLISTINFO WHERE CORPLISTID IN (SELECT CORPLISTID FROM DG.CORPLISTDISTINFO WHERE RECIPIENTMDN = ?) and LISTDISTRIBUTIONPOLICY != ?);";

    public static final String QRY_GET_ALL_CONTACT_LIST ="select MEMBERMDN from DG.CORPLISTMEMBER where CORPLISTID in (select CORPLISTID from DG.CORPLISTDISTINFO where RECIPIENTMDN = ?);";

    public static final String  XDM_CORP_LIST_INFO = "XDM_CORP_LIST_INFO";

    public static final int DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT = 6;
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
     * @param mdn
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<Integer> getContactListIdsForMdn(String mdn, boolean readonly,
                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getContactListIdsForMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        Collection<Integer> contactListIds = new ArrayList<>();

        try {
            query = QRY_SELECT_CONTACT_LIST_IDS;
            if(null != mdn && !mdn.isEmpty()) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
                pStatement = conn.prepareStatement(query);
                pStatement.setString(1, mdn);
                knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
                rs = pStatement.executeQuery();
                knLogger.debug(methodName, "QUERY : Completed.");

                if (rs.next()) {
                    contactListIds = new ArrayList<Integer>();
                    do {
                        contactListIds.add(rs.getInt(1));
                    } while (rs.next());
                } else {
                    // Throw back Exception
                    knLogger.info(methodName, "No Contact Info found");
//                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Contact Info found. Query ->" + query);
                }
            }

            knLogger.info(methodName, "Returning contactListIds - ", contactListIds);
            return contactListIds;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }



    public Integer getCorpListIdForMDN(String mdn, boolean readonly,
                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpListIdForMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        Integer corpListId= null;

        try {
            query = QRY_SELECT_CORP_LIST_ID;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                corpListId = rs.getInt(1);

            } else {
                // Throw back Exception
                knLogger.info(methodName, "No Contact Info found");
                //throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Contact Info found. Query ->" + query);
            }
            knLogger.info(methodName, "Returning corpListId - ", corpListId);
            return corpListId;
        }
        catch (KnDAOException e) {
                knLogger.error(methodName, "DAO Exception - ", e);
                throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

    public List<Integer> getCommonCorpListIdForMDN(String mdn, boolean readonly,
                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCommonCorpListIdForMDN(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        var corpListIds= new ArrayList<Integer>();

        try {
            query = QRY_SELECT_COMMON_CORP_LIST_ID;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            while (rs.next()) {
                corpListIds.add(rs.getInt(1));

            }
            knLogger.info(methodName, "Returning corpListId - ", corpListIds);
            return corpListIds;
        }
        catch (KnDAOException e) {
                knLogger.error(methodName, "DAO Exception - ", e);
                throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }


    public Collection<String> getMemberMdnsForCorpListId(Collection<Integer> corpListIds, Integer corpId,boolean readonly,
                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMemberMdnsForCorpListId(List<Integer>,Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : " + corpListIds);

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> memberMdnsForCorpListId= new ArrayList<String>();

        try {
            if(corpId != null)
                 query = QRY_SELECT_MEMBERMDN_FOR_CORPLISTID.replaceAll(CONTACT_LIST_ID_LIST, getIntComSepList(corpListIds));
             else
                query = QRY_SELECT_MEMBERMDN_FOR_CORPLISTID_WITHOUT_CORPID.replaceAll(CONTACT_LIST_ID_LIST, getIntComSepList(corpListIds));

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pStatement = conn.prepareStatement(query);
           // pStatement.setInt(1, corpListId);
            if(corpId != null) pStatement.setInt(1, corpId);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                memberMdnsForCorpListId = new ArrayList<>();
                do {
                    memberMdnsForCorpListId.add(rs.getString(1).trim());
                } while (rs.next());
            } else {
                // Throw back Exception
                knLogger.info(methodName, "No Contact Info found");
               // throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Contact Info found. Query ->" + query);
            }
            knLogger.debug(methodName, "Returning memberMdnsForCorpListId - ", memberMdnsForCorpListId);
            knLogger.info(methodName, "Returning memberMdnsForCorpListId size - ", memberMdnsForCorpListId.size());
            return memberMdnsForCorpListId;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
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
        knLogger.debug(methodName, "Entry : Mdn :", mdn, ", New Mdn :", KnGDPRTemplate.mdn(newMdn));

        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {

            query = QRY_UPDATE_MDN;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, newMdn);
            pStatement.setString(2, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.", count);


            knLogger.info(methodName, "Updated Mdn for MDN:  ", KnGDPRTemplate.mdn(mdn));
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to update Mdn for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    /**
     * @param mdns
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<Integer> getContactListIdsForMdn(List<String> mdns, boolean readonly,
                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getContactListIdsForMdn(List, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        Collection<Integer> contactListIds = new ArrayList<Integer>();

        try {
            query = QRY_SELECT_CONTACT_LIST_IDS_4_MDNS.replaceAll(ID_LIST, getComSepList(mdns));

            //conn = persisterTxn.getDBConnection(pttServerId, readonly);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            statement = conn.createStatement();
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = statement.executeQuery(query);
            knLogger.debug(methodName, "QUERY : Completed.");

            while (rs.next()) {
                contactListIds.add(rs.getInt(1));
            }
            knLogger.info(methodName, "Returning contactListIds - ", contactListIds);
            return contactListIds;
        } catch (Exception e) {
            knLogger.error(methodName, " Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }

    }
    public Collection<String> getPrivateContactsForMDN(String mdn,Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException 
    {
        String methodName = "getPrivateContactsForMDN(String,Integer, KnPersisterTxn)";
		knLogger.debug(methodName, "Entry : " + KnGDPRTemplate.mdn(mdn) + corpId);

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> memberMdnsForCorpListId= new ArrayList<String>();;

		try {
			query = QRY_SELECT_PRIVATE_CONTACTS_FOR_MDN;
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
			pStatement = conn.prepareStatement(query);
			pStatement.setString(1, mdn);
		//	pStatement.setInt(2, corpId);
			knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
			rs = pStatement.executeQuery();
			knLogger.debug(methodName, "QUERY : Completed.");

			while (rs.next()) {
				memberMdnsForCorpListId.add(rs.getString(1).trim());
			}
			knLogger.debug(methodName, "Returning PrivateContactsForMDN - ", memberMdnsForCorpListId);
            knLogger.info(methodName, "Returning PrivateContactsForMDN size- ", memberMdnsForCorpListId.size());
			return memberMdnsForCorpListId;
		} catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }

    }

    public List<String> getCommonContactListForMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCommonContactListForMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn ", KnGDPRTemplate.mdn(mdn));
        List<String> listOfContactMdns = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpId = 0;
        try {
            query = QRY_GET_ALL_COMMON_CONTACTLIST_MDNS;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                listOfContactMdns.add((rs.getString(1)).trim());
            }
            return listOfContactMdns;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving contact mdns - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving contact mdns - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getCorpIdFromCorpListInfo " + e,
                    pttServerId, XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT Fetched the contact mdn details  - ", KnGDPRTemplate.mdnList(listOfContactMdns));
        }
    }

    public List<String> getNonCommonContactListForMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getNonCommonContactListForMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn ", KnGDPRTemplate.mdn(mdn));
        List<String> listOfContactMdns = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpId = 0;
        try {
            query = QRY_GET_SUBS_NON_COMMON_CONTACT_LIST;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                listOfContactMdns.add(rs.getString(1).trim());
            }
            return listOfContactMdns;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving SublistIds - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving SublistIds - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getCorpIdFromCorpListInfo " + e,
                    pttServerId, XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT Fetched the corpId of sublist  :  - ", KnGDPRTemplate.mdnList(listOfContactMdns));
        }
    }

}
