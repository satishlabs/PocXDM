/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnClientVocoderProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by hanwar on 10-09-2016.
 */
public class KnClientSuppVocodersDAO implements ITableDAO{

    private static final KnLogger knLogger = KnLogger.getLogger(KnClientSuppVocodersDAO.class);

    private static final String className = KnClientSuppVocodersDAO.class.getName();
    private String pttServerId;
    private static final String TABLENAME = "DG.CLIENTSUPPORTEDVOCODERS";
    private static final String DELETE_QUERY = "DELETE FROM "+TABLENAME+" WHERE MDN =?";
    private static final String FETCH_QUERY = "SELECT PRIORITY, VOCODERID FROM "+TABLENAME+" WHERE MDN =?";

    public KnClientSuppVocodersDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public void insert(List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs,KnPersisterTxn persisterTxn)throws KnDAOException{

        String methodName = "insert(List<KnClientVocoderProfilePersistDTO>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: insert vocoders for  ", clientVocoderProfilePersistDTOs, " Persist ", persisterTxn);

        try {
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add("MDN");
            queryFields.add("PRIORITY");
            queryFields.add("VOCODERID");
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, clientVocoderProfilePersistDTOs);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(query);
            for (KnClientVocoderProfilePersistDTO persistDTO : clientVocoderProfilePersistDTOs){
                int columnIndex = 0;
                String mdn = persistDTO.getMdn();
                int priority = persistDTO.getPriority();
                int vocoderId = persistDTO.getVocoderId();
                pStmt.setString(++columnIndex,mdn);
                pStmt.setInt(++columnIndex,priority);
                pStmt.setInt(++columnIndex,vocoderId);
                pStmt.addBatch();
                knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", clientVocoderProfilePersistDTOs);
                knLogger.debug(methodName, "Query: Executed ");
            }
            pStmt.executeBatch();

        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to insert Subscriber vocoder list - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CLIENT_SUPP_VOCODER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void delete(String mdn,KnPersisterTxn persisterTxn)throws KnDAOException{

        String methodName = "delete(Mdn,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: delete MDN's existing vocoder ", KnGDPRTemplate.mdn(mdn), " Persist ", persisterTxn);

        try {
            query = DELETE_QUERY;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(mdn));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(DELETE_QUERY);
            pStmt.setString(1,mdn);
            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to insert Subscriber vocoder list - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CLIENT_SUPP_VOCODER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void delete(List<String> mdnList,KnPersisterTxn persisterTxn)throws KnDAOException{
        String methodName = "delete(Mdn,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        int index = 1;
        knLogger.debug(methodName, "ENTRY: delete MDN's existing vocoder ", KnGDPRTemplate.mdnList(mdnList), " Persist ", persisterTxn);
        try {
            query = "DELETE FROM DG.CLIENTSUPPORTEDVOCODERS WHERE MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdnList(mdnList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pStmt.setString(index++,mdn);
            }
            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdnList(mdnList));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to insert Subscriber vocoder list - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CLIENT_SUPP_VOCODER, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * Method to fetch client supported profileID (vocoderid) with priority for respective MDN from DB DG.ClientSuppVocoder
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public  Map<Integer, Integer> fetchClientSuppVocoder(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)throws KnDAOException{
        String methodName = "fetchClientSuppVocoder(Mdn, boolean, KnPersisterTxn)";
        String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<Integer, Integer> clientVocoderProfile = null;

        knLogger.info(methodName, "ENTRY: fetch MDN's existing vocoder ", KnGDPRTemplate.mdn(mdn), " Persist ", persisterTxn);

        try {
            query = FETCH_QUERY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(FETCH_QUERY);
            pStmt.setString(1,mdn);
            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            clientVocoderProfile = new TreeMap<>();
            if (rs !=null) {
                while (rs.next()) {
                    clientVocoderProfile.put(rs.getInt("PRIORITY"),rs.getInt("VOCODERID"));
                }
            }
            knLogger.debug(methodName,"VOCODERID fetched for MDN :",KnGDPRTemplate.mdn(mdn)," : ",clientVocoderProfile);
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to insert Subscriber vocoder list - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CLIENT_SUPP_VOCODER, query);
        }finally {
            com.kodiak.common.dao.KnDbUtil.closeResultSet(rs);
            com.kodiak.common.dao.KnDbUtil.closeStatement(pStmt);
        }
        return clientVocoderProfile;
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
}
