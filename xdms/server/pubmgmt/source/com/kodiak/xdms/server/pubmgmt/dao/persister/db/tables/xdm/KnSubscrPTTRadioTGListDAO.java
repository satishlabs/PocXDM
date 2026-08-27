/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnGroupUsageDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnSubscrPTTRadioTGListDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSubscrPTTRadioTGListDAO.class);

    public String pttServerId = null;

    public KnSubscrPTTRadioTGListDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String TABLENAME = "DG.SUBSCRPTTRADIOTGLIST";
    public static final String MDN ="MDN";
    public static final String GROUPID = "GROUPID";
    public static final String ZONEID = "ZONEID";
    public static final String ZONENAME = "ZONENAME";
    public static final String CHANNELID = "CHANNELID";


    public static final String GET_SUBSCRPTTRADIOTGLIST = "SELECT MDN, GROUPID, ZONEID, ZONENAME, CHANNELID FROM DG.SUBSCRPTTRADIOTGLIST WHERE MDN = ?;";
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


    public List<KnGroupUsageDTO> getSubscrPTTRadioTGList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscrPTTRadioTGList(String, KnPersisterTxn)";
        knLogger.debug( methodName, "AuthEntry : ");
        List<KnGroupUsageDTO> groupUsageDTOS = new ArrayList<>();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = GET_SUBSCRPTTRADIOTGLIST;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            KnGroupUsageDTO knGroupUsageDTO= null;
            if (rs.next()) {
                do {
                    knGroupUsageDTO = new KnGroupUsageDTO();
                    knGroupUsageDTO.setGroupId(rs.getInt(2));
                    knGroupUsageDTO.setZoneId(rs.getInt(3));
                    knGroupUsageDTO.setZoneName(rs.getString(4));
                    knGroupUsageDTO.setChannelId(rs.getInt(5));
                    groupUsageDTOS.add(knGroupUsageDTO);
                }while (rs.next());
            } /*else {
                // throw back exception
                knLogger.error( methodName, "No getSubscrPTTRadioTGList doc Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No SubscrPTTRadioGroupList doc Info found. Query ->" + query);
            }*/
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve getSubscrPTTRadioTGList doc for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SUBSCRPTTRADIOGROUPLISTDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.info( methodName, "Returning getSubscrPTTRadioTGList details - " ,  groupUsageDTOS);
        return groupUsageDTOS;
    }
}
