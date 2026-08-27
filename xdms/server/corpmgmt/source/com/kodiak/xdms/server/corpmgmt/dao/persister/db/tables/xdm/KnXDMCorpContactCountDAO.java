/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpContactCountDAO.java
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
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.sql.*;
import java.util.*;

import com.kodiak.logger.KnLogger;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.MDNLIST;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.SUBSCRIBER_CONTACT_COUNT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;


public class KnXDMCorpContactCountDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpContactCountDAO.class);

    public static final int BULK_UPDATE_SIZE = 1000;

    private String CLASS = KnXDMCorpContactCountDAO.class.getName();

    public String pttServerId = null;

    KnXDMCorpContactCountDAO(String pttServerId) {
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

    public Map<String, String> getAllSubscriberContactCount(Collection<String> mdnList, boolean readOnly,
                                                            KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getAllSubscriberContactCount(Collection<String>,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : Input DTO passed mdnList size is - ", getSize(mdnList));

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<String, String> subscribersContactCnt = new HashMap<String, String>();
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SUBSCRIBER_CONTACT_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, MDNLIST);
            pStmt = conn.prepareStatement(query);
            // pstmt.setString(1, mdnListStr);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            
            for(String mdn : mdnList)
            	pStmt.setString(index++, mdn);
            
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                int contactCount = rs.getInt(1);
                String mdn = rs.getString(2).trim();
                mdn = mdn.trim();
                subscribersContactCnt.put(mdn, String.valueOf(contactCount));
            }
            knLogger.debug( methodName, "List size from DB :  " , subscribersContactCnt.size());
            return subscribersContactCnt;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while get the subcribers contact count - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while get the subcribers contact count - " , e);
            throw KnDbUtil.processException(e, "Failed while get the subcribers contact count - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT :");
        }
    }


    public void updateSubscribersContactCount(Collection<String> mdnList, int maxSubscContactCount, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateSubscribersContactCount(Collection<String>, int, persisterTxn)";
        knLogger.debug(methodName, "Entry : Input DTO passed mdnList size - ", getSize(mdnList), ", maxSubscContactCount - ", maxSubscContactCount);
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(pttServerId, false);

            //int initialSbSize = 1000;
            int initialSbSize = 50;
            int noOfMdns = mdnList.size();
            /* if (noOfMdns < 50) {
                initialSbSize = noOfMdns * 16;
            }*/
            if (noOfMdns < 2) {
                initialSbSize = noOfMdns * 16;
            }
            Collection<String> mdnStrList = new ArrayList<String>();
            StringBuffer sb = new StringBuffer(initialSbSize);
            for (String mdnStr : mdnList) {
                int index = sb.lastIndexOf(",");
                //if (index >= 1000) {
                if (index >= 50) {
                    sb = sb.deleteCharAt(sb.lastIndexOf(","));
                    int commaLoc = sb.lastIndexOf(",");
                    String lastId = sb.substring(commaLoc, sb.length());
                    mdnStrList.add(sb.substring(0, commaLoc));
                    sb = new StringBuffer(initialSbSize);
                    sb.append(lastId.substring(1)).append(",");
                    sb.append(mdnStr).append(",");
                } else {
                    sb.append(mdnStr).append(",");
                }
            }

            if (sb.lastIndexOf(",") > 0) {
                sb = sb.deleteCharAt(sb.lastIndexOf(","));
                mdnStrList.add(sb.toString());
            }

            for (String mdnStr : mdnStrList) {
                CallableStatement stmt = conn.prepareCall("{call DG.Kdk_POCDataRetrieval.updateCorpContactCountForSubsc(?,?)}");
                stmt.setString(1, mdnStr);
                stmt.setInt(2, maxSubscContactCount);
                knLogger.debug( methodName, "Before call to stored procedure");
                stmt.executeUpdate();
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        knLogger.warn( methodName, " Exception occured while updating the subcribers contact count - " , e);
                    }
                }
                knLogger.debug( methodName, "Stored Procedure executed successfully.");
            }
            knLogger.debug( methodName, "EXIT :");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the subcribers contact count - " , e);
            throw e;
        } catch (SQLException e) {
            knLogger.error( methodName, "Unexpected SQLException occured while updating the subcribers contact count - " , e);
            for(Throwable th : e){
                processSublistsSQLException(th);
            }
            throw KnDbUtil.processException(e, "Failed while updating the subcribers contact count - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, "Callable statement");

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updating the subcribers contact count - " , e);
            throw KnDbUtil.processException(e, "Failed while updating the subcribers contact count - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, "Callable statement");
        }
    }

    public void updateSublistsSubscribersContactCount(Collection<Integer> sublistIdsList, int maxSubscContactCount, int
            maxGrpContactCount, int maxDispGrpContactCount, KnPersisterTxn persisterTxn, int maxBGMemCount) throws KnDAOException {
        String methodName = "updateSublistsSubscribersContactCount(Collection<Integer>,int,int,int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : Input DTO passed sublistIdsList size - ", getSize(sublistIdsList)
                , " , maxSubscContactCount - ", maxSubscContactCount, " ,maxGrpContactCount - ", maxGrpContactCount
                , " , maxDispGrpContactCount - ", maxDispGrpContactCount, ", maxBGMemCount - ", maxBGMemCount);
       Connection conn;
        try {
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            int initialSbSize = 1000;
            int noOfSublistIds = sublistIdsList.size();
            if (noOfSublistIds < 90) {
                initialSbSize = noOfSublistIds * 5;
            }
            Collection<String> sublistIdsStrList = new ArrayList<String>();
            StringBuffer sb = new StringBuffer(initialSbSize);
            for (int sublistId : sublistIdsList) {
                int index = sb.lastIndexOf(",");
                if (index >= 1000) {
                    sb = sb.deleteCharAt(sb.lastIndexOf(","));
                    int commaLoc = sb.lastIndexOf(",");
                    String lastId = sb.substring(commaLoc, sb.length());
                    sublistIdsStrList.add(sb.substring(0, commaLoc));
                    sb = new StringBuffer(initialSbSize);
                    sb.append(lastId.substring(1)).append(",");
                    sb.append(sublistId).append(",");
                } else {
                    sb.append(sublistId).append(",");
                }
            }

            if (sb.lastIndexOf(",") > 0) {
                sb = sb.deleteCharAt(sb.lastIndexOf(","));
                sublistIdsStrList.add(sb.toString());
            }

            for (String sublistIdStr : sublistIdsStrList) {
                CallableStatement stmt = conn.prepareCall("call DG.Kdk_POCDataRetrieval.updateCountsForCorpSublist(?,?,?,?,?)");
                stmt.setString(1, sublistIdStr);
                stmt.setInt(2, maxSubscContactCount);
                stmt.setInt(3, maxGrpContactCount);
                stmt.setInt(4, maxDispGrpContactCount);
                stmt.setInt(5, maxBGMemCount);
                stmt.executeUpdate();
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        knLogger.error( methodName, "Exception occured while updating the subcribers contact count via sublist - ", e);
                    }
                }
            }
           knLogger.debug( methodName, "Stored Procedure executed successfully.");
        } 
        catch(KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the subcribers contact count - " , e);
            throw e;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQLException occured while updating the subcribers contact count - " , e);

            for(Throwable th : e){
                processSublistsSQLException(th);
            }
            throw KnDbUtil.processException(e, "Failed while updating the subcribers contact count - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, "Callable statement");

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updating the subcribers contact count - " , e);
            throw KnDbUtil.processException(e, "Failed while updating the subcribers contact count - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, "Callable statement");
        }
    }

    /**
     * Thisw method is used to extract the ORA value tod ecide the error code to be thrown
     *
     * @param e
     * @throws KnDAOException
     */
    private void processSublistsSQLException(Throwable e) throws KnDAOException {

        int offset = e.getMessage().indexOf("ORA-");
        if (offset > 0) {
            offset = offset + 4;
            int errorMessage = Integer.valueOf(e.getMessage().substring(offset, (offset + 5)));
            switch (errorMessage) {

                case 20020:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_GRP_SIZE_REACHED,
                            "Max Group Size Reached");
                case 20021:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_DISPATCH_GROUP_LIMIT_EXCEEDED,
                            "Max Dispatch Group Size Reached");
                case 20019:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_SUBSCRIBER_CONTACT_EXCEEDED,
                            "Max subscribers contact Reached");
                case 20022:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_BROADCAST_GRP_SIZE_REACHED,
                            "Max Broadcast Group Size Reached");
                default:
                    break;
            }
        }
    }
    /**
     * Thisw method is used to extract the ORA value tod ecide the error code to be thrown
     *
     * @param e
     * @throws KnDAOException
     */
    private void processSQLException(SQLException e) throws KnDAOException {

        int offset = e.getMessage().indexOf("ORA-");
        if (offset > 0) {
            offset = offset + 4;
            int errorMessage = Integer.valueOf(e.getMessage().substring(offset, (offset + 5)));
            switch (errorMessage) {

                case 20020:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_GRP_SIZE_REACHED,
                            "Max Group Size Reached");
                case 20021:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_DISPATCH_GROUP_LIMIT_EXCEEDED,
                            "Max Dispatch Group Size Reached");
                case 20019:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_SUBSCRIBER_CONTACT_EXCEEDED,
                            "Max subscribers contact Reached");
                case 20022:
                    throw new KnDAOException(KnErrorCodes.DAO.MAX_BROADCAST_GRP_SIZE_REACHED,
                            "Max Broadcast Group Size Reached");

                default:
                    throw KnDbUtil.processException(e, "Failed while updating the subcribers contact count - " + e,
                            pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, "Callable statement");
            }
        } else {
            throw KnDbUtil.processException(e, "Failed while updating the subcribers contact count - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, "Callable statement");
        }
    }

    /**
     * this method returns the mdn and contact count.
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getSubsContactCount(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubsContactCount(List<String>,boolean, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : Input DTO passed mdnList size is - ", getSize(mdnList));
        Map<String, Integer> subscribersContactCnt = new HashMap<>();
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subSubsribersCorporateDetails = getSubscriberContactCount(subsList, readOnly, persisterTxn);
            if (subSubsribersCorporateDetails != null && !subSubsribersCorporateDetails.isEmpty()){
                subscribersContactCnt.putAll(subSubsribersCorporateDetails);
            }
        }

        knLogger.debug(methodName, "List size from DB :  ", subscribersContactCnt.size());

        return subscribersContactCnt;
    }
    /**
     * this method returns the mdn and contact count.
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private Map<String, Integer> getSubscriberContactCount(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscriberContactCount(List<String>,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : Input DTO passed mdnList size is - " , getSize(mdnList));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<String, Integer> subscribersContactCnt = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SUBSCRIBER_CONTACT_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, MDNLIST);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for(String mdn : mdnList)
              pStmt.setString(index++, mdn);
            
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                subscribersContactCnt.put(rs.getString(2).trim(), rs.getInt(1));
            }
            knLogger.debug(methodName, "List size from DB :  " , subscribersContactCnt.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while get the subcribers contact count - ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_CONTACT_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return subscribersContactCnt;
    }
}
