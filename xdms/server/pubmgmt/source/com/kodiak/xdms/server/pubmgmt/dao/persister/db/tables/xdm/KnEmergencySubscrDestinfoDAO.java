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
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnEmgrDestinationInfoDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnEmergencySubscrDestinfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnEmergencySubscrDestinfoDAO.class);

    public String pttServerId = null;

    public KnEmergencySubscrDestinfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String TABLENAME = "DG.EMERGENCY_SUBSCR_DESTINFO";
    public static final String MDN = "MDN";
    public static final String EMERGDESTPRIORITY = "EMERGDESTPRIORITY";
    public static final String EMERGDESTTYPE = "EMERGDESTTYPE";
    public static final String EMERGDEST = "EMERGDEST";
    public static final String QUERY_GET_EMERGENCY_SUBSCR_DESTINFO = "SELECT "+ EMERGDESTPRIORITY + ", " +
            EMERGDEST + ", " + EMERGDESTTYPE + " FROM " + TABLENAME + " WHERE " +MDN + "=?";


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

    public List<KnEmgrDestinationInfoDTO> getEmergencySubscrDestinfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getEmgrConfig(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        List<KnEmgrDestinationInfoDTO> destinationInfoDTOS = new ArrayList<>();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = QUERY_GET_EMERGENCY_SUBSCR_DESTINFO;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                do {
                    KnEmgrDestinationInfoDTO infoDTO = new KnEmgrDestinationInfoDTO();
                    infoDTO.setPriority(rs.getInt(EMERGDESTPRIORITY));
                    infoDTO.setType(rs.getInt(EMERGDESTTYPE));
                    infoDTO.setDestination(rs.getString(EMERGDEST));
                    destinationInfoDTOS.add(infoDTO);
                } while(rs.next());
            } /*else {
                // throw back exception
                knLogger.error( methodName, "No Emgr Destination info  found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Emgr Destination Info found. Query ->" + query);
            }*/

        } catch (KnDAOException e) {
        knLogger.error( methodName, "DAO Exception - " + e);
        throw e;
        }catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve Emerg Destination for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EMERGENCYDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info( methodName, "Returning Authorization doc details - " ,  destinationInfoDTOS);
        return destinationInfoDTOS;
    }
}
