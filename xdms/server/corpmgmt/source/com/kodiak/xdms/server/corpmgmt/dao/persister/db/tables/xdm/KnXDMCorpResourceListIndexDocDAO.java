/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpResourceListIndexDoc.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        27-01-2011      7.0
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
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.getResourceListDocumentURI;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.MDNLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMCorpResourceListIndexDocDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpResourceListIndexDocDAO.class);
    private String CLASS = KnXDMCorpResourceListIndexDocDAO.class.getName();

    public String pttServerId = null;

    KnXDMCorpResourceListIndexDocDAO(String pttServerId) {
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
    public Map<String, KnOPDocChgDTO> updateEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateEtag(mdnList, persisterTxn)";
        knLogger.entry(methodName, "ENTRY : mdnList size " , mdnList.size());
        Map<String, KnOPDocChgDTO> subscDocChngMap = new HashMap<String, KnOPDocChgDTO>();
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var knOPDocChgDTOMap = updateEtagForSubMdn(subsList, persisterTxn);
            if(knOPDocChgDTOMap != null && !knOPDocChgDTOMap.isEmpty())
            {
                subscDocChngMap.putAll(knOPDocChgDTOMap);
            }
        }
        knLogger.exit(methodName, "EXIT : subscDocChngMap size " , subscDocChngMap == null ? 0 : subscDocChngMap.size());
        return subscDocChngMap;

    }
    private Map<String, KnOPDocChgDTO> updateEtagForSubMdn(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateEtagForSubMdn(mdnList, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));

        Connection conn;
        boolean ownedTxn = false;
       // Statement stmtSelect = null;
        PreparedStatement pstmtUpdate=null;
        PreparedStatement pstmtSelect=null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;
        int index = 1;
//        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        List<String> subsList = new ArrayList<String>(mdnList);
        Collections.sort(subsList);
        knLogger.debug( methodName, "MDN list after sorting  - ", subsList == null ? subsList : KnGDPRTemplate.mdnList(subsList));
        Map<String, KnOPDocChgDTO> subscDocChngMap = new HashMap<String, KnOPDocChgDTO>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            selectQuery = queryMapper.getQuery(KnPersisterConstants.SELECT_RESOURCE_LIST_ETAG);
            knLogger.debug( methodName, "Executing Query - " , "'" , selectQuery , "'");
            //selectQuery = replaceContactWithValue(selectQuery, MDNLIST, formCommaSeperatedIdList(subsList));
            selectQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,selectQuery,"MDNLIST");
            pstmtSelect = conn.prepareStatement(selectQuery);
            for(String mdn : mdnList){
                pstmtSelect.setString(index++,mdn);
            }
            rs = pstmtSelect.executeQuery();
                knLogger.debug( methodName, "Query completed successfully");
                Map<String, Integer> mdnEtagMap = new HashMap<String, Integer>();
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    knLogger.debug( methodName, "mdn -  ->" , KnGDPRTemplate.mdn(mdn) , "<----");
                    mdnEtagMap.put(mdn.trim(), rs.getInt(2));
                }
                knLogger.debug( methodName, "mdnEtagMap -  " , KnGDPRTemplate.mapKeyMdn(mdnEtagMap));
                conn = persisterTxn.getDBConnection(pttServerId, false);
                updateQuery = queryMapper.getQuery(KnPersisterConstants.UPDATE_RESOURCE_LIST_ETAG);
                pstmtUpdate = conn.prepareStatement(updateQuery);
                for (String mdn : mdnEtagMap.keySet()) {
                    KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                    knLogger.debug( methodName, "Mdn -  " , KnGDPRTemplate.mdn(mdn));
                    mdn = mdn.trim();
                    int etag = mdnEtagMap.get(mdn);
                    docChngDto.setPrevEtag(String.valueOf(etag));
                    knLogger.debug( methodName, "etag -  " , etag);
                    pstmtUpdate.setInt(1, ++etag);
                    pstmtUpdate.setString(2, mdn);
                    knLogger.debug( methodName, "Executing query - " , "'" , updateQuery , "'");
                    pstmtUpdate.addBatch();
                    docChngDto.setDocType(1);
                    docChngDto.setNewEtag(String.valueOf(etag));
                    docChngDto.setDocUri(getResourceListDocumentURI(mdn));
                    docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    subscDocChngMap.put(mdn, docChngDto);
                }
                List<String> tempList=new ArrayList<String>(subsList);
                tempList.removeAll(mdnEtagMap.keySet());
                knLogger.debug( methodName, "Mdns whose etag not updated in DG.XDM_CORPRESOURCELISTINDEXDOC - " , tempList == null ? tempList : KnGDPRTemplate.mdnList(tempList));
                pstmtUpdate.executeBatch();
            knLogger.debug( methodName, "Query executed successfully ");
            if (ownedTxn) {
                persisterTxn.save();
            }
            return subscDocChngMap;

        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error( methodName, "KnDAOException occured while updating the Etags - " , e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error( methodName, "Unexpected Exception occured while updating the Etags- " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while updating the Etags " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
           // KnDbUtil.closeStatement(stmtSelect);
            KnDbUtil.closeStatement(pstmtUpdate);
            KnDbUtil.closeStatement(pstmtSelect);
            knLogger.debug( methodName, "mdnList - " , mdnList == null ?mdnList : KnGDPRTemplate.mdnList(mdnList), "Exit : The size of the map- " , subscDocChngMap.size());
        }
    }

    public void updateEtagForSubMdn(Map<String,Integer> mdnEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateEtagForSubMdn(mdnList, persisterTxn)";
        Connection conn;
        boolean ownedTxn = false;
        PreparedStatement pstmtUpdate=null;
        String updateQuery = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            knLogger.debug( methodName, "mdnEtagMap -  " , KnGDPRTemplate.mapKeyMdn(mdnEtagMap));
            conn = persisterTxn.getDBConnection(pttServerId, false);
            updateQuery = queryMapper.getQuery(KnPersisterConstants.UPDATE_RESOURCE_LIST_ETAG);
            pstmtUpdate = conn.prepareStatement(updateQuery);
            for (String mdn : mdnEtagMap.keySet()) {
                knLogger.debug( methodName, "Mdn -  " , KnGDPRTemplate.mdn(mdn));
                mdn = mdn.trim();
                int etag = mdnEtagMap.get(mdn);
                knLogger.debug( methodName, "etag -  " , etag);
                pstmtUpdate.setInt(1, etag + 1);
                pstmtUpdate.setString(2, mdn);
                pstmtUpdate.addBatch();
                knLogger.debug( methodName, "Executing query - " , "'" , updateQuery , "'");
            }
            pstmtUpdate.executeBatch();
            knLogger.debug( methodName, "Query executed successfully ");
            if (ownedTxn) {
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the Etags - " , e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error( methodName, "Unexpected Exception occured while updating the Etags- " ,
                    e);
            throw KnDbUtil.processException(e, "Failed while updating the Etags " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, "" + " , " + updateQuery);
        } finally {
            KnDbUtil.closeStatement(pstmtUpdate);
        }
    }

    public int selectSubscribersDocumentEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscribersDocumentEtag(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        int etag = 0;
        try {
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_RESOURCE_LIST_ETAG);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing Query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                etag = rs.getInt(2);
            }
            knLogger.debug(methodName, "Query completed successfully");
            return etag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while selecting the Etag - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while selecting the Etag- ",
                    e);
            throw KnDbUtil.processException(e, "Failed while select of the Etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "inputMdn - ", KnGDPRTemplate.mdn(mdn), "Exit : The Etag for subsc Doc is - ", etag);
        }
    }

    public Map<String,Integer> selectSubscribersDocumentEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscribersDocumentEtag(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdnList(mdnList));
        Map<String,Integer> etagMap = new HashMap<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_RESOURCE_LIST_ETAG);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing Query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                etagMap.put(rs.getString(1).trim(),rs.getInt(2));
            }
            knLogger.debug(methodName, "Query completed successfully");
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            return etagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while selecting the Etag - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while selecting the Etag- ",
                    e);
            throw KnDbUtil.processException(e, "Failed while select of the Etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "inputMdn - ", KnGDPRTemplate.mdnList(mdnList), "Exit : The Etag for subsc Doc is - ", etagMap);
        }
    }

}
