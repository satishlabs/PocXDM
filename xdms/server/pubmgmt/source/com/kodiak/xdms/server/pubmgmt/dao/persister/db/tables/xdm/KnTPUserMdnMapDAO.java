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
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

public class KnTPUserMdnMapDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorporateInfoDAO.class);

    public static final String TABLENAME = "DG.THIRD_PARTY_USER_MDN_MAP";

    public String pttServerId = null;

    public KnTPUserMdnMapDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


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
    public static final String QRY_SELECT_MDNLIST = "SELECT MDN, THIRD_PARTY_ID FROM " + TABLENAME + " WHERE MDN IN (MDNLIST)";


    public Map<String, Integer> getMdnTPidMap(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException{
        String methodName = "getMdnListForTPid(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :-> ", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<String, Integer> mdnTPidMap = new HashMap<>();
        try {
            StringBuilder buffer = new StringBuilder(QRY_SELECT_MDNLIST);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,buffer.toString(),"MDNLIST");
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);
            knLogger.debug( methodName, "QUERY : Executing " , query );
            for(String mdn : mdnList)
            	pStatement.setString(index++, mdn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Excecuted.");
            while (rs.next()) {
                mdnTPidMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            for(String mdn : mdnList){
                if(!mdnTPidMap.containsKey(mdn)){
                    mdnTPidMap.put(mdn, 0);
                }
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve count for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.THIRD_PARTY_USER_MDN_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.debug(methodName, "Returning mdnList - ", mdnTPidMap);
        return mdnTPidMap;
    }
}
