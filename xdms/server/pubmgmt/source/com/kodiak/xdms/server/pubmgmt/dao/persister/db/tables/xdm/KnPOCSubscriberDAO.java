/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPOCSubscriberDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Chandrashekar H S     June, 2015           8.0
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

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;


import java.sql.*;
import java.util.*;

public class KnPOCSubscriberDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCSubscriberDAO.class);

    public String pttServerId = null;
    public KnPOCSubscriberDAO(String pttServerId) { this.pttServerId = pttServerId; }

    public static final String TABLENAME = "DG.POCSUBSCRINFO";
    public static final String MDN = "MDN";
    public static final String SERVICE_STATUS_AUTHUSER = "SERVICE_STATUS_AUTHUSER";
    public static final String CORPID = "CORPID";
    public static final String DISCREET_ENABLED = "DISCREET_ENABLED";
    public static final String SERVICEAUTHSTATUS = "SERVICEAUTHSTATUS";
    public static final String LASTPROFILEUPDATETIME = "LASTPROFILEUPDATETIME";

    public static final String QRY_SELECT_MEMBER_TYPE = "SELECT MDN, CLIENT_TYPE FROM " + TABLENAME + " WHERE " + MDN + " IN ";
    public static final String QRY_SELECT_SERVICE_STATUS_AUTHUSER = "SELECT MDN, SERVICE_STATUS_AUTHUSER FROM " + TABLENAME + " WHERE " + MDN + " IN ";
    public static final String QRY_SELECT_CORPID_FOR_MDN = "SELECT MDN, CORPID FROM " + TABLENAME + " WHERE " + MDN + " IN ";
    public static final String UPDATE_DISCREEET_LISTENER_STATUS = "UPDATE " +TABLENAME +" SET "+DISCREET_ENABLED+"=?, "+ LASTPROFILEUPDATETIME + "=? WHERE "+ MDN+ " IN ";
    public static final String UPDATE_SERVICE_AUTH_STATUS = "UPDATE " +TABLENAME +" SET "+SERVICE_STATUS_AUTHUSER+"=?, "+ SERVICEAUTHSTATUS + "=?, "+ LASTPROFILEUPDATETIME + "=? WHERE "+ MDN+ " IN ";

    public static final String QRY_SELECT_MEMBER_DETAILS = "SELECT MDN, CLIENT_TYPE, CORPID, PUBLICSUBSCRIPTIONTYPE, CORPSUBSCRIPTIONTYPE FROM " + TABLENAME + " WHERE " + MDN + " IN ";
    
    public static final String GET_REAL_MDN= "SELECT DISTINCT A.MDN FROM DG.POCSUBSCRINFO A ,DG.POCSUBSCRINFO B WHERE A.MC_ID = B.MC_ID AND (A.USERPROFILEINDEX=0 OR A.USERPROFILEINDEX IS NULL) AND B.MDN=?";
    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public List<KnMemberDTO> getMembersClientType(List<String> mdns,boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException{
        final String methodName = "getMembersClientType(String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        List<KnMemberDTO> memberDTOList =  new ArrayList<KnMemberDTO>();
        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_MEMBER_TYPE).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();
            knLogger.debug( methodName, "QUERY : Executing " , query );
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Excecuted.");

            if (rs.next()) {
                do {
                    KnMemberDTO knMemberDTO = new KnMemberDTO();
                    knMemberDTO.setMemberMdn(rs.getString(1));
                    knMemberDTO.setClientType(rs.getString(2));
                    memberDTOList.add(knMemberDTO);
                } while(rs.next());
            }
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve clientType for Mdns- " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve clientType for Mdns - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }
        knLogger.debug(methodName, "Exit : ", memberDTOList);
        return memberDTOList;
    }

    public Map<String, Integer> getUserServiceStatus(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getUserServiceStatus(AuthList<String> ,boolean, KnPersisterTxn)";
        knLogger.debug( methodName, KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;
        Map<String , Integer> mdnVsServiceAuthMap = null;
        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_SERVICE_STATUS_AUTHUSER).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            statement = conn.createStatement();
            knLogger.debug( methodName, "QUERY : Executing " , query );
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Excecuted.");

            if (rs.next()) {
                mdnVsServiceAuthMap = new HashMap<>();
                do {
                    mdnVsServiceAuthMap.put(rs.getString(MDN).trim(), rs.getInt(SERVICE_STATUS_AUTHUSER));
                } while(rs.next());
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve user service auth status for Mdns - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }
        knLogger.debug(methodName, "Exit : ", mdnVsServiceAuthMap);
        return mdnVsServiceAuthMap;
    }

    public Map<String, Integer> getTargetMdnsCorpid(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException{
        final String methodName = "getTargetMdnsCorpid(AuthList<String> , KnPersisterTxn)";
        knLogger.debug( methodName, KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;
        Map<String , Integer> mdnCorpIdMap = null;
        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_CORPID_FOR_MDN).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();
            knLogger.debug( methodName, "QUERY : Executing " , query );
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Excecuted.");

            if (rs.next()) {
                mdnCorpIdMap = new HashMap<>();
                do {
                    mdnCorpIdMap.put(rs.getString(MDN).trim(), rs.getInt(CORPID));
                } while(rs.next());
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpid for Mdns - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }
        knLogger.debug(methodName, "Exit : ", mdnCorpIdMap);
        return mdnCorpIdMap;
    }

    public void updateDiscreetEnabledForTarget(List<String> targetMdns, int discreetListenerStatus, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateDiscreetEnabledForTarget(String, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {

        	 StringBuffer buffer = new StringBuffer(200);
             buffer.append(UPDATE_DISCREEET_LISTENER_STATUS).append(KnDbUtil.convertListToStringBuffer(targetMdns));
             query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, discreetListenerStatus);
            pStatement.setLong(2,lastProfileUpdateTime);
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : "  ,persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info( methodName, "Updated discreetListenerStatus for target mdn :  " + KnGDPRTemplate.mdnList(targetMdns));
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update discreetListenerStatus for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStatement);
            knLogger.debug( methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdnList(targetMdns));
        }
    }

    public void updateServiceAuthStatusForTarget(List<String> targetMdns, int userServStatus, int finalEerviceAuthStatus, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateServiceAuthStatusForTarget(String, int, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {

        	StringBuffer buffer = new StringBuffer(200);
            buffer.append(UPDATE_SERVICE_AUTH_STATUS).append(KnDbUtil.convertListToStringBuffer(targetMdns));
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, userServStatus);
            pStatement.setInt(2, finalEerviceAuthStatus);
            pStatement.setLong(3, lastProfileUpdateTime);
            
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : "  ,persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info( methodName, "Updated Service auth status for target mdn :  " + KnGDPRTemplate.mdnList(targetMdns));
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update discreetListenerStatus for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdnList(targetMdns));
        }
    }

    /**
     * Method to retrieve the subscriber details.
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public List<KnMemberDTO> getMembersDetails(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException{
        final String methodName = "getMembersDetails(String, KnPersisterTxn)";
        knLogger.debug( methodName,KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        List<KnMemberDTO> memberDTOList =  new ArrayList<KnMemberDTO>();
        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_MEMBER_DETAILS).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            statement = conn.createStatement();
            knLogger.debug( methodName, "QUERY : Executing " , query );
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Excecuted.");

            if (rs.next()) {
                do {
                    KnMemberDTO knMemberDTO = new KnMemberDTO();
                    knMemberDTO.setMemberMdn(rs.getString(1).trim());
                    knMemberDTO.setClientType(rs.getString(2));
                    knMemberDTO.setCorpId(rs.getInt(3));
                    knMemberDTO.setSubscriptionType(KnDbUtil.getMappedSubscriptionType(rs.getInt(4), rs.getInt(5)));
                    memberDTOList.add(knMemberDTO);
                } while(rs.next());
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve clientType for Mdns - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
        }
        knLogger.debug(methodName, "Exit : ", memberDTOList);
        return memberDTOList;
    }
    
    public String getRealMdnForProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException
    { final String methodName = "getRealMdns(String, KnPersisterTxn)";
    knLogger.debug(methodName, "ENTRY Point : mdn - ", KnGDPRTemplate.mdn(mdn));
    PreparedStatement pstmt = null;
    String query = null;
    ResultSet rs = null;
    String realMdn = null;
    try {
        Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
        query = GET_REAL_MDN;
        knLogger.debug(methodName, "Executing query - ", query);
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, mdn.trim());
        rs = pstmt.executeQuery();
        knLogger.debug(methodName, "Query executed successfully.");
        while (rs.next()) {
            realMdn = rs.getString(1).trim();
        }
        knLogger.debug(methodName, "EXIT. Real Mdn  - ", KnGDPRTemplate.mdn(realMdn));
    } catch (Exception e) {
        knLogger.error( methodName, "Unexpected Exception - " + e);
        throw KnDbUtil.processException(e, "Failed to get Real Mdn For ProfileMdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
    } finally {
    	 KnDbUtil.closeResultSet(rs);
         KnDbUtil.closePreparedStatement(pstmt);
         knLogger.debug(methodName, "EXIT");
    }
    return realMdn;
    }
}
