/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMDirectoryDAO.java
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
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.buildMCSDOC;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.getDirectoryURI;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMDirectoryDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMDirectoryDAO.class);

    private String CLASS = KnXDMDirectoryDAO.class.getName();
    public String pttServerId = null;

    KnXDMDirectoryDAO(String pttServerId) {
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

    public Map<String, KnOPDirChgDTO> updateEtag(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        String methodName = "updateEtag(persisterTxn)";
        if (etagMap == null) {
            etagMap = new HashMap<String, KnOPDirChgDTO>();
        }
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var knOPDirChgDTOMap = updateSubListEtag(subsList, persisterTxn, etagMap);
            if (knOPDirChgDTOMap != null && !knOPDirChgDTOMap.isEmpty()) {
                etagMap.putAll(knOPDirChgDTOMap);
            }
        }
        knLogger.exit(methodName, "EXIT : returnList size " , etagMap == null ? 0 : etagMap.size());
        return etagMap;
    }


    public Map<String, KnOPDirChgDTO> updateEtagForUpm(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        String methodName = "updateEtagForUpm(persisterTxn)";
        if (etagMap == null) {
            etagMap = new HashMap<>();
        }
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, UPM_BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var knOPDirChgDTOMap = updateSubListEtag(subsList, persisterTxn, etagMap);
            if (knOPDirChgDTOMap != null && !knOPDirChgDTOMap.isEmpty()) {
                etagMap.putAll(knOPDirChgDTOMap);
            }
        }
        knLogger.exit(methodName, "EXIT : returnList size " , etagMap == null ? 0 : etagMap.size());
        return etagMap;
    }

    public Map<String, KnOPDirChgDTO> selectEtag(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        String methodName = "selectEtag(persisterTxn)";
        if (etagMap == null) {
            etagMap = new HashMap<String, KnOPDirChgDTO>();
        }
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var knOPDirChgDTOMap = selectSubListEtag(subsList, persisterTxn, etagMap);
            if (!knOPDirChgDTOMap.isEmpty()) {
                etagMap.putAll(knOPDirChgDTOMap);
            }
        }
        knLogger.exit(methodName, "EXIT : returnList size " , etagMap == null ? 0 : etagMap.size());
        return etagMap;
    }


    private Map<String, KnOPDirChgDTO> updateSubListEtag(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        String methodName = "updateSubListEtag(persisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap), " mdnList -", getSize(mdnList));
        boolean ownedTxn = false;
        Connection readOnlyConn;
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;
        int index = 1;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            List<String> subsList = new ArrayList<String>(mdnList);
            Collections.sort(subsList);
            knLogger.debug(methodName, "MDN list after sorting  - ", KnGDPRTemplate.mdnList(subsList));
            readOnlyConn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            selectQuery = queryMapper.getQuery(SELECT_DIRECTORY_ETAG);
            //String mdnStr = formCommaSeperatedIdList(subsList);
            //selectQuery = replaceContactWithValue(selectQuery, MDNLIST, mdnStr);
            selectQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, selectQuery, "MDNLIST");
            knLogger.debug(methodName, "Executing query- ", "'", selectQuery, "'");
            pstmt = readOnlyConn.prepareStatement(selectQuery);
            for (String mdn : subsList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully ");
            Map<String, Integer> mdnEtagMap = new HashMap<String, Integer>();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                mdnEtagMap.put(mdn.trim(), rs.getInt(2));
            }
            connection = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            updateQuery = queryMapper.getQuery(UPDATE_DIRECTORY_ETAG);
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            rs = null;
            pstmt = null;
            pstmt = connection.prepareStatement(updateQuery);
            for (String mdn : mdnEtagMap.keySet()) {
                mdn = mdn.trim();
                int etag = mdnEtagMap.get(mdn);
                pstmt.setInt(1, ++etag);
                pstmt.setString(2, mdn);
                knLogger.debug(methodName, "Executing query- ", "'", updateQuery, "'");
                pstmt.addBatch();
                KnOPDirChgDTO direcChngDto = etagMap.get(mdn);
                if (direcChngDto == null) {
                    direcChngDto = new KnOPDirChgDTO();
                }
                direcChngDto.setDirUri(getDirectoryURI(mdn));
                direcChngDto.setDirNewEtag(String.valueOf(etag));
                direcChngDto.setDirPrevEtag(String.valueOf(--etag));
                etagMap.put(mdn, direcChngDto);
            }
            pstmt.executeBatch();
            List<String> tempList = new ArrayList<String>(subsList);
            tempList.removeAll(mdnEtagMap.keySet());
            knLogger.debug(methodName, "Query executed successfully", "Fetched the Etag details for subscribers :  ", KnGDPRTemplate.mapKeyMdn(etagMap), "Mdns whose etag not updated in DG.XDM_DIRECTORY- ", tempList == null ? tempList : KnGDPRTemplate.mdnList(tempList));
            if (ownedTxn) {
                persisterTxn.save();
            }
            return etagMap;
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while fetching and updating the Subscriber directory etag ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while fetching and updating the Subscriber directory etag - " + e);
            throw KnDbUtil.processException(e, "Failed while fetching and updating the Subscriber directory etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    private Map<String, KnOPDirChgDTO> selectSubListEtag(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        String methodName = "selectSubListEtag(persisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap), " mdnList -", getSize(mdnList));
        boolean ownedTxn = false;
        Connection readOnlyConn;
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;
        int index = 1;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            List<String> subsList = new ArrayList<String>(mdnList);
            Collections.sort(subsList);
            knLogger.debug(methodName, "MDN list after sorting  - ", KnGDPRTemplate.mdnList(subsList));
            readOnlyConn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            selectQuery = queryMapper.getQuery(SELECT_DIRECTORY_ETAG);
            //String mdnStr = formCommaSeperatedIdList(subsList);
            //selectQuery = replaceContactWithValue(selectQuery, MDNLIST, mdnStr);
            selectQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, selectQuery, "MDNLIST");
            knLogger.debug(methodName, "Executing query- ", "'", selectQuery, "'");
            pstmt = readOnlyConn.prepareStatement(selectQuery);
            for (String mdn : subsList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully ");
            Map<String, Integer> mdnEtagMap = new HashMap<String, Integer>();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                mdnEtagMap.put(mdn.trim(), rs.getInt(2));
            }
            /*connection = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            updateQuery = queryMapper.getQuery(UPDATE_DIRECTORY_ETAG);
            pstmt = connection.prepareStatement(updateQuery);*/
            for (String mdn : mdnEtagMap.keySet()) {
                mdn = mdn.trim();
                int etag = mdnEtagMap.get(mdn) + 1;
                //pstmt.setInt(1, ++etag);
                //pstmt.setString(2, mdn);
               // knLogger.debug(methodName, "Executing query- ", "'", updateQuery, "'");
                //pstmt.addBatch();
                KnOPDirChgDTO direcChngDto = etagMap.get(mdn);
                if (direcChngDto == null) {
                    direcChngDto = new KnOPDirChgDTO();
                }
                direcChngDto.setDirUri(getDirectoryURI(mdn));
                direcChngDto.setDirNewEtag(String.valueOf(etag));
                direcChngDto.setDirPrevEtag(String.valueOf(--etag));
                etagMap.put(mdn, direcChngDto);
            }
           // pstmt.executeBatch();
            List<String> tempList = new ArrayList<String>(subsList);
            tempList.removeAll(mdnEtagMap.keySet());
            knLogger.debug(methodName, "Query executed successfully", "Fetched the Etag details for subscribers :  ", KnGDPRTemplate.mapKeyMdn(etagMap), "Mdns whose etag not updated in DG.XDM_DIRECTORY- ", tempList == null ? tempList : KnGDPRTemplate.mdnList(tempList));
            if (ownedTxn) {
                persisterTxn.save();
            }
            return etagMap;
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while fetching and updating the Subscriber directory etag ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while fetching and updating the Subscriber directory etag - " + e);
            throw KnDbUtil.processException(e, "Failed while fetching and updating the Subscriber directory etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Integer> getAndUpdateDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getAndUpdateDirectoryEtag(persisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : mdnList -" , getSize(mdnList));

        Connection conn;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;

        Map<String, Integer> directoryEtagMap = new HashMap<>();

        try {
            if (mdnList == null || mdnList.isEmpty()) {
                return directoryEtagMap;
            }
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            List<String> subsList = new ArrayList<String>(mdnList);
            Collections.sort(subsList);
            knLogger.debug( methodName, "MDN list after sorting  - " , subsList);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var chunk : subsLists) {
                selectQuery = queryMapper.getQuery(SELECT_DIRECTORY_ETAG);
                //String mdnStr = formCommaSeperatedIdList(subsList);
                //selectQuery = replaceContactWithValue(selectQuery, MDNLIST, mdnStr);
                knLogger.debug(methodName, "Executing query", selectQuery);
                selectQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(chunk, selectQuery, "MDNLIST");
                pstmt = conn.prepareStatement(selectQuery);
                int index = 1;
                for (String mdn : chunk) {
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully ");
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    directoryEtagMap.put(mdn.trim(), rs.getInt(2));
                }
                KnDbUtil.closeResultSet(rs);
                rs = null;
                KnDbUtil.closeStatement(pstmt);
                pstmt = null;

                if (!chunk.isEmpty()) {
                    updateQuery = "UPDATE DG.XDM_DIRECTORY SET ETAG = ETAG + 1 WHERE MDN = ?";
                    pstmt = conn.prepareStatement(updateQuery);
                    for (String mdn : chunk) {
                        pstmt.setString(1, mdn);
                        pstmt.addBatch();
                    }
                    knLogger.debug(methodName, "Executing query- ", updateQuery);
                    pstmt.executeBatch();
                }
            }
            knLogger.debug(methodName, "Query executed successfully");

        }  catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while fetching and updating the Subscriber directory etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return directoryEtagMap;
    }

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnList
            ,String exists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsTS(List<String>,KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList)," exists ",exists);
        Connection conn;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;

        Map<String, Collection<KnDocChangeListDTO>> mdnDocMap=new HashMap<>();
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            final int mdnBatchSize = 1000;
            final Collection<List<String>> mdnChunk = mdnList.stream()
                    .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / mdnBatchSize))
                    .values();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            for(List<String> chunk:mdnChunk){
                //String mdnStr = formCommaSeperatedIdList(chunk);
                //selectQuery = replaceContactWithValue(selectQuery, MDNLIST, mdnStr);
                selectQuery = queryMapper.getQuery(GET_SUBS_MCID_UPDTS_BY_MDNS);
                selectQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(chunk,selectQuery,"MDNLIST");
                int index = 1;
                pstmt = conn.prepareStatement(selectQuery);
                for(String mdn : chunk){
                    pstmt.setString(index++,mdn);
                }
                rs = pstmt.executeQuery();
                knLogger.debug( methodName, "Query selectQuery :",selectQuery);
                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                Collection<KnDocChangeListDTO> dbRecList=null;
                updateQuery = queryMapper.getQuery(UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME);
                pstmt = conn.prepareStatement(updateQuery);
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    String activeFs=rs.getString(7)!=null?rs.getString(7): KnGeneralUtil.convertLongToHexString(rs.getLong(5));
                    boolean upmBit = KnGeneralUtil.getFeatureBitValue(activeFs,USER_PROFILE_MGMT_BIT);
                    int majorPv = rs.getInt(6);
                    //mdns pv >18 or pv 19 onwards and umfit should be enabled
                    if(majorPv>PROTOCOL_VERSION_18_X||upmBit){
                        pstmt.setLong(1, lastProfileUpdateTime);
                        pstmt.setString(2, mdn);
                        pstmt.addBatch();
                        dbRecList=new ArrayList<>();
                        dbRecList.addAll(buildMCSDOC(new String(rs.getBytes(2), StandardCharsets.UTF_8)
                                ,mdn
                                ,String.valueOf(rs.getInt(5))
                                ,String.valueOf(lastProfileUpdateTime)
                                ,String.valueOf(rs.getLong(3))
                                ,rs.getInt(4)
                                ,exists)
                        );
                        mdnDocMap.put(mdn,dbRecList);
                    }
                }
                pstmt.executeBatch();
            }
            knLogger.debug(methodName, "Query executed successfully mdnDocMap:-",mdnDocMap);
            return mdnDocMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured " + e);
            throw KnDbUtil.processException(e, "Failed updating subs TS" + e,
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTSandDirecEtag(Set<String> mdnList
            ,String exists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsTSandDirecEtag(List<String>,KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList)," exists ",exists);
        Connection conn;
        PreparedStatement pstmt=null;
        PreparedStatement updateTsPstmt = null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;

        Map<String, Integer> directoryEtagMap = new HashMap<>();
        Map<String, Collection<KnDocChangeListDTO>> mdnDocMap=new HashMap<>();
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            final Collection<List<String>> mdnChunk = mdnList.stream()
                    .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / BULK_UPDATE_SIZE))
                    .values();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            for(List<String> chunk:mdnChunk){
                selectQuery = queryMapper.getQuery(FETCH_ETAG_SUBSCRIBER_DETAILS_FROM_MDNLIST);
                selectQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(chunk,selectQuery,"MDNLIST");
                int index = 1;
                pstmt = conn.prepareStatement(selectQuery);
                for(String mdn : chunk){
                    pstmt.setString(index++,mdn);
                }
                rs = pstmt.executeQuery();
                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                Collection<KnDocChangeListDTO> dbRecList=null;
                updateQuery = queryMapper.getQuery(UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME);
                updateTsPstmt = conn.prepareStatement(updateQuery);
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    String activeFs=rs.getString(7)!=null?rs.getString(7): KnGeneralUtil.convertLongToHexString(rs.getLong(5));
                    boolean upmBit = KnGeneralUtil.getFeatureBitValue(activeFs,USER_PROFILE_MGMT_BIT);
                    int majorPv = rs.getInt(6);
                    // columnIndex 8 is current Etag
                    int newEtag = rs.getInt(8) + 1;
                    //mdns pv >18 or pv 19 onwards and upmBit should be enabled
                    if(majorPv>PROTOCOL_VERSION_18_X||upmBit){
                        updateTsPstmt.setLong(1, lastProfileUpdateTime);
                        updateTsPstmt.setString(2, mdn);
                        updateTsPstmt.addBatch();
                        dbRecList=new ArrayList<>();
                        dbRecList.addAll(buildMCSDOC(new String(rs.getBytes(2), StandardCharsets.UTF_8)
                                ,mdn
                                ,String.valueOf(rs.getInt(5))
                                ,String.valueOf(newEtag)
                                ,String.valueOf(rs.getLong(8))
                                ,rs.getInt(4)
                                ,exists)
                        );
                        mdnDocMap.put(mdn,dbRecList);
                    }
                    directoryEtagMap.put(mdn.trim(), rs.getInt(8));
                }
                updateTsPstmt.executeBatch();
                KnDbUtil.closePreparedStatement(updateTsPstmt);
                updateTsPstmt = null;
                KnDbUtil.closeResultSet(rs);
                rs = null;
                KnDbUtil.closeStatement(pstmt);
                pstmt = null;

                updateQuery = queryMapper.getQuery(UPDATE_DIRECTORY_ETAG);
                KnDbUtil.closeStatement(pstmt);
                pstmt = null;
                pstmt = conn.prepareStatement(updateQuery);
                for (String mdn : directoryEtagMap.keySet()) {
                    mdn = mdn.trim();
                    int etag = directoryEtagMap.get(mdn);
                    pstmt.setInt(1, ++etag);
                    pstmt.setString(2, mdn);
                    knLogger.debug(methodName, "Executing query- ", updateQuery);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                KnDbUtil.closeStatement(pstmt);
                pstmt = null;
            }
            knLogger.debug(methodName, "Query executed successfully mdnDocMap:-",mdnDocMap);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured " + e);
            throw KnDbUtil.processException(e, "Failed updating subs TS" + e,
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(updateTsPstmt);
            KnDbUtil.closeStatement(pstmt);
        }
        return mdnDocMap;
    }

    public void getAndUpdateBulkDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getAndUpdateBulkDirectoryEtag(persisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : mdnList -" , getSize(mdnList));

        Connection conn;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;

        Map<String, Integer> directoryEtagMap = null;
        final AtomicInteger counter = new AtomicInteger(0);
        final int mdnBatchSize = 1000;
        final Collection<List<String>> mdnChunk = mdnList.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / mdnBatchSize))
                .values();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            for(List<String> chunk:mdnChunk){
                selectQuery = queryMapper.getQuery(SELECT_DIRECTORY_ETAG);
                knLogger.debug( methodName, "Executing query- " , selectQuery );
                selectQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(chunk,selectQuery,"MDNLIST");
                pstmt = conn.prepareStatement(selectQuery);
                int index= 1;
                for(String mdn : chunk){
                    pstmt.setString(index++,mdn);
                }
                rs = pstmt.executeQuery();
                directoryEtagMap = new HashMap<>();
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    directoryEtagMap.put(mdn.trim(), rs.getInt(2));
                }
                knLogger.debug( methodName, "Query executed successfully directoryEtagMap:"
                        ,KnGDPRTemplate.mdnList(new ArrayList<>(directoryEtagMap.keySet())));

                updateQuery = queryMapper.getQuery(UPDATE_DIRECTORY_ETAG);
                pstmt = conn.prepareStatement(updateQuery);
                for (String mdn : directoryEtagMap.keySet()) {
                    mdn = mdn.trim();
                    int etag = directoryEtagMap.get(mdn);
                    pstmt.setInt(1, ++etag);
                    pstmt.setString(2, mdn);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            knLogger.debug(methodName, "Query executed successfully");
        }  catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while fetching and updating the Subscriber directory etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateBulkSubsTS(Set<String> mdnList
            , KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkSubsTS()";
        knLogger.debug( methodName, "ENTRY Point : mdnList - " , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        String updateQuery = null;
        try {
            if(mdnList!=null){
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                updateQuery = queryMapper.getQuery(UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME);
                pstmt = conn.prepareStatement(updateQuery);
                for (String mdn : mdnList) {
                    mdn = mdn.trim();
                    pstmt.setLong(1, lastProfileUpdateTime);
                    pstmt.setString(2, mdn);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured " + e);
            throw KnDbUtil.processException(e, "Failed updating subs TS" + e,
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName," Exit ");
        }
    }
}
