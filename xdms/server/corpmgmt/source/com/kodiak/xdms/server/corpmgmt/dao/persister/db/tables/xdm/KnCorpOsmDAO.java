/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.request.KnCorpOperationStatusMesssageInfoDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.commdto.response.KnXDMCorpOSMGroupList;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMGroupListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListDetailsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_OSM_GROUP_LIST;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.OSMLISTID;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnCorpOsmDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpOsmDAO.class);

    private static final String CLASS = KnCorpGroupInfoDAO.class.getName();

    private String xdmsHome;

    public KnCorpOsmDAO(String xdmsHome) {
        this.xdmsHome = xdmsHome;
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

    public void createOsmList(KnCorpOSMPersistDTO corpOSMPersistDTO,
                              KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "createOsmList()";
        knLogger.debug(methodName, "Entry ",corpOSMPersistDTO);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Set<KnXDMOSMInfoRequestDTO> addedOSMMsgList = corpOSMPersistDTO.getAddedOSMMsgList();
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CREATE_CORPOSMINFO);
            knLogger.debug( methodName, "Executing query -" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(corpOSMPersistDTO.getInternalCorpId()));
                pstmt.setInt(2, Integer.parseInt(corpOSMPersistDTO.getOSMListId()));
                pstmt.setString(3, corpOSMPersistDTO.getOSMListName());
                pstmt.setInt(4, Integer.parseInt(corpOSMPersistDTO.getIsDefault()));
                pstmt.setString(5, corpOSMPersistDTO.getHierarchyId());
                knLogger.debug(methodName, "Inserting OSM with hierarchyId: ", corpOSMPersistDTO.getHierarchyId());
                pstmt.executeUpdate();
            query = queryMapper.getQuery(CREATE_OSMLISTINFO);
            knLogger.debug(methodName, "Executing query -", "'", query, "'");
            KnDbUtil.closePreparedStatement(pstmt);
            pstmt = conn.prepareStatement(query);
            for (KnXDMOSMInfoRequestDTO osmInfo : addedOSMMsgList) {
                pstmt.setInt(1, Integer.parseInt(corpOSMPersistDTO.getOSMListId()));
                pstmt.setInt(2, Integer.parseInt(osmInfo.getMsgId()));
                pstmt.setString(3, osmInfo.getMsg());
                pstmt.setString(4, osmInfo.getMsgShortText());
                pstmt.setInt(5, Integer.parseInt(osmInfo.getMsgOrderId()));
                pstmt.setInt(6, Integer.parseInt(osmInfo.getMsgType()));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void updateOsmList(KnCorpOSMPersistDTO corpOSMPersistDTO,
                              KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "updateOsmList()";
        knLogger.debug(methodName, "Entry ",corpOSMPersistDTO);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Set<KnXDMOSMInfoRequestDTO> addedOSMMsgList = corpOSMPersistDTO.getAddedOSMMsgList();
            Set<KnXDMOSMInfoRequestDTO> modifiedOSMMsgList = corpOSMPersistDTO.getModifiedOSMMsgList();
            Set<KnXDMOSMInfoRequestDTO> removedOSMMsgList = corpOSMPersistDTO.getRemovedOSMMsgList();
            String osmListId = corpOSMPersistDTO.getOSMListId();
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_CORPOSMINFO);
            knLogger.debug( methodName, "Executing query UPDATE_CORPOSMINFO-" , query );
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpOSMPersistDTO.getOSMListName());
            pstmt.setInt(2,Integer.parseInt(corpOSMPersistDTO.getIsDefault()));
            pstmt.setInt(3,Integer.parseInt(corpOSMPersistDTO.getInternalCorpId()));
            pstmt.setInt(4,Integer.parseInt(osmListId));
            pstmt.executeUpdate();
            if(modifiedOSMMsgList!=null) {
                    query = queryMapper.getQuery(UPDATE_OSMLISTINFO);
                    knLogger.debug(methodName, "Executing update OSM INFO query  UPDATE_OSMLISTINFO-", query);
                    KnDbUtil.closePreparedStatement(pstmt);
                    pstmt = conn.prepareStatement(query);
                    for (KnXDMOSMInfoRequestDTO osmInfo : modifiedOSMMsgList) {
                        pstmt.setInt(1, Integer.parseInt(osmInfo.getMsgId()));
                        pstmt.setString(2, osmInfo.getMsg());
                        pstmt.setString(3, osmInfo.getMsgShortText());
                        pstmt.setInt(4, Integer.parseInt(osmInfo.getMsgOrderId()));
                        pstmt.setInt(5, Integer.parseInt(osmInfo.getMsgType()));
                        pstmt.setInt(6, Integer.parseInt(osmListId));
                        pstmt.setInt(7, Integer.parseInt(osmInfo.getMsgId()));
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
            }
            if(addedOSMMsgList!=null) {
                query = queryMapper.getQuery(CREATE_OSMLISTINFO);
                knLogger.debug(methodName, "Executing query CREATE_OSMLISTINFO -", query);
                KnDbUtil.closePreparedStatement(pstmt);
                pstmt = conn.prepareStatement(query);
                for (KnXDMOSMInfoRequestDTO osmInfo : addedOSMMsgList) {
                    pstmt.setInt(1, Integer.parseInt(corpOSMPersistDTO.getOSMListId()));
                    pstmt.setInt(2, Integer.parseInt(osmInfo.getMsgId()));
                    pstmt.setString(3, osmInfo.getMsg());
                    pstmt.setString(4, osmInfo.getMsgShortText());
                    pstmt.setInt(5, Integer.parseInt(osmInfo.getMsgOrderId()));
                    pstmt.setInt(6, Integer.parseInt(osmInfo.getMsgType()));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            if(removedOSMMsgList!=null) {
                query = queryMapper.getQuery(UPDATE_REMOVE_OSMLISTINFO);
                knLogger.debug(methodName, "Executing query UPDATE_REMOVE_OSMLISTINFO -", "'", query, "'");
                KnDbUtil.closePreparedStatement(pstmt);
                pstmt = conn.prepareStatement(query);
                for (KnXDMOSMInfoRequestDTO osmInfo : removedOSMMsgList) {
                    pstmt.setInt(1, Integer.parseInt(corpOSMPersistDTO.getOSMListId()));
                    pstmt.setInt(2, Integer.parseInt(osmInfo.getMsgId()));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            knLogger.debug( methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteOsmList(String corpId,String osmListId,
                              KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "deleteOsmList()";
        knLogger.debug(methodName, "Entry ",corpId,osmListId);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_OSMLISTINFO);
            knLogger.debug( methodName, "Executing DELETE_OSMLISTINFO  query -" ,query );
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(osmListId));
            int osmInfoRecordDeleted=pstmt.executeUpdate();

            query = queryMapper.getQuery(DELETE_OSM_CORPGROUPINFO);
            knLogger.debug( methodName, "Executing DELETE_OSM_CORPGROUPINFO query -", query);
            KnDbUtil.closePreparedStatement(pstmt);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            pstmt.setInt(2, Integer.parseInt(osmListId));
            int corpGroupOsmRecordDelted=pstmt.executeUpdate();

            query = queryMapper.getQuery(DELETE_CORP_OSMLISTINFO);
            knLogger.debug( methodName, "Executing DELETE_CORP_OSMLISTINFO query -", query);
            KnDbUtil.closePreparedStatement(pstmt);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            pstmt.setInt(2, Integer.parseInt(osmListId));
            int corpOsmRecordDelted=pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT:DELETE_CORP_OSMLISTINFO del count",
                    corpOsmRecordDelted,"DELETE_OSMLISTINFO delete count",osmInfoRecordDeleted,
                    ": corpGroupOsmRecordDelted :",corpGroupOsmRecordDelted);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * This method returns the MAP<OSMLISTID,ISDEFAULT>: SELECT OSMLISTID,ISDEFAULT FROM DG.CORPOSMINFO WHERE CORPID=?;
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer,Integer> getOSMListIdAndDefaultMap(String corpId, boolean readOnly,
                                                          KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "getOSMListIdAndDefaultMap(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry ",corpId);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        Map<Integer,Integer> map=new ConcurrentHashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CORP_OSMLISTID_DEFAULT);
            knLogger.debug( methodName, "Executing query - CORP_OSMLISTID_DEFAULT " ,query );
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            result = pstmt.executeQuery();
            while(result.next()){
                map.put(result.getInt(1),result.getInt(2));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully.OSM ListID and default map ",map);
        return map;
    }


    public Map<Integer,Integer> getOSMListIdAndDefaultMapByHierarchyId(String corpId, boolean readOnly,
                                                          KnPersisterTxn persisterTxn,String hierarchyId)throws KnDAOException {
        String methodName = "getOSMListIdAndDefaultMapByHierarchyId(String, boolean, KnPersisterTxn, String)";
        knLogger.debug(methodName, "Entry ",corpId);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        Map<Integer,Integer> map=new ConcurrentHashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CORP_OSMLISTID_DEFAULT_BY_HIERARCHY_ID);
            knLogger.debug( methodName, "Executing query - CORP_OSMLISTID_DEFAULT " ,query );
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            pstmt.setString(2,hierarchyId);
            result = pstmt.executeQuery();
            while(result.next()){
                map.put(result.getInt(1),result.getInt(2));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getting OSMIdAndDefaultMapByHierarchyId  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occurred while getting OSMIdAndDefaultMapByHierarchyId - " , e);
            throw KnDbUtil.processException(e, "Failed while getting OSMIdAndDefaultMapByHierarchyId  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully.OSM ListID and default map by hierarchyId ",map);
        return map;
    }

    /**
     * This method gets the osm list:
     * SELECT OSMLISTID, OSMLISTNAME, ISDEFAULT FROM DG.CORPOSMINFO WHERE CORPID=?;
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpOSMInfoListRespDTO getOSMListByCorp(String corpId, boolean readOnly,
                                                     KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "getOSMListByCorp(String, boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry ",corpId);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        KnCorpOSMInfoListRespDTO response=new KnCorpOSMInfoListRespDTO();
        Set<KnCorpOperationStatusMesssageInfoDTO> OSMListInfo=new HashSet<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPOSMINFO_LIST);
            knLogger.debug( methodName, "Executing GET_CORPOSMINFO_LIST query -" ,query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            result = pstmt.executeQuery();
            while(result.next()){
                OSMListInfo.add(new KnCorpOperationStatusMesssageInfoDTO(String.valueOf(result.getInt(1)),
                        result.getString(2),String.valueOf(result.getInt(3)),String.valueOf(result.getInt(4))));
            }
            response.setOSMListInfo(OSMListInfo);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully.OSM LIST in CORP ",response);
        return response;
    }

    /**
     * This method gets the osm list:
     * SELECT OSMLISTID, OSMLISTNAME, ISDEFAULT FROM DG.CORPOSMINFO WHERE CORPID=?;
     * @param corpId
     * @param hierarchyId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpOSMInfoListRespDTO getOSMListByCorpAndHierarchyId(String corpId, String hierarchyId, boolean readOnly,
                                                     KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "getOSMListByCorp(String, boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry ",corpId);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        KnCorpOSMInfoListRespDTO response=new KnCorpOSMInfoListRespDTO();
        Set<KnCorpOperationStatusMesssageInfoDTO> OSMListInfo=new HashSet<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPOSMINFO_LIST_BY_HIERARCHY);
            knLogger.debug( methodName, "Executing GET_CORPOSMINFO_LIST_BY_HIERARCHY query -" ,query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            pstmt.setString(2, hierarchyId);
            result = pstmt.executeQuery();
            while(result.next()){
                OSMListInfo.add(new KnCorpOperationStatusMesssageInfoDTO(String.valueOf(result.getInt(1)),
                        result.getString(2),String.valueOf(result.getInt(3)),String.valueOf(result.getInt(4))));
            }
            response.setOSMListInfo(OSMListInfo);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully.OSM LIST in CORP ",response);
        return response;
    }

    /**
     * This method gets the list details by corpId and osmListId:
     * SELECT OSMLISTID,OSMLISTNAME,ISDEFAULT FROM DG.CORPOSMINFO WHERE CORPID=? AND OSMLISTID=?;
     * @param corpId
     * @param osmListId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetailsByListId(String corpId, String osmListId,boolean readOnly,
                                                                    KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "getOSMListDetailsByListId()";
        knLogger.info(methodName, "Entry ",corpId,osmListId,readOnly);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        KnCorpOSMInfoListDetailsRespDTO response=new KnCorpOSMInfoListDetailsRespDTO();
        KnCorpOperationStatusMesssageInfoDTO OSMListDetails=new KnCorpOperationStatusMesssageInfoDTO();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPOSMINFO_LIST_DETAILS);
            knLogger.debug( methodName, "Executing GET_CORPOSMINFO_LIST_DETAILS query -" ,  query );
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            pstmt.setInt(2, Integer.parseInt(osmListId));
            result = pstmt.executeQuery();
            while(result.next()){
                OSMListDetails.setOSMListId(String.valueOf(result.getInt(1)));
                OSMListDetails.setOSMListName(result.getString(2));
                OSMListDetails.setIsDefault(String.valueOf(result.getInt(3)));
            }
            List<KnXDMOSMInfoRequestDTO> OSMMsgInfo= getUniqueFlieldsOSMInfoList(osmListId,readOnly,persisterTxn);
            OSMListDetails.setOSMMsgInfo(new HashSet<KnXDMOSMInfoRequestDTO>(OSMMsgInfo));
            response.setOSMListDetails(OSMListDetails);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully.OSM LIST in CORP ",response);
        return response;
    }

    /**
     * SELECT OSMID,OSMORDERID,OSMTYPE,OSMSHORTTEXT,OSMESSAGE from DG.OSMLISTINFO where OSMLISTID=?;
     * This method gets the OSMLISTINFO list.
     * @param osmListId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnXDMOSMInfoRequestDTO> getUniqueFlieldsOSMInfoList(String osmListId,boolean readOnly,
                                                                    KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "getUniqueFlieldsOSMInfoList()";
        knLogger.info(methodName, "Entry ",osmListId,readOnly);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        List<KnXDMOSMInfoRequestDTO> OSMListInfo=new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_OSM_INFO_LIST);
            knLogger.debug( methodName, "Executing GET_OSM_INFO_LIST query -" , query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(osmListId));
            result = pstmt.executeQuery();
            while(result.next()){
                OSMListInfo.add(new KnXDMOSMInfoRequestDTO(String.valueOf(result.getInt(1))
                        ,String.valueOf(result.getInt(2)),
                        String.valueOf(result.getInt(3)),
                        result.getString(4),
                        result.getString(5)));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully.OSM LIST  ",OSMListInfo);
        return OSMListInfo;
    }

    public void assignOSMIdToGroup(String corpId, String osmListId,
                                      Collection<String> assignedOSMIdToGroupIds,
                                      Collection<String> removedOSMIdFromGroupIds,
                                      String xdmsHome, KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "assignOSMIdToGroup()";
        knLogger.debug(methodName, "Entry ",corpId,osmListId,assignedOSMIdToGroupIds,removedOSMIdFromGroupIds);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if(assignedOSMIdToGroupIds!=null) {
                query = queryMapper.getQuery(ASSIGN_OSM_TO_GROUP);
                query = replaceContactWithValue(query, GROUPIDS, formCommaSeperatedIdList(assignedOSMIdToGroupIds));
                knLogger.debug(methodName, "Executing query ASSIGN_OSM_TO_GROUP for assignedOSMIdToGroupIds -", query);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(osmListId));
                pstmt.setInt(2, Integer.parseInt(corpId));
                int count=pstmt.executeUpdate();
                knLogger.debug( methodName, "EXIT: ASSIGN_OSM_TO_GROUP Query executed successfully for assignedOSMIdToGroupIds count-",count);
            }

            if(removedOSMIdFromGroupIds!=null){
                query = queryMapper.getQuery(ASSIGN_OSM_TO_GROUP);
                query = replaceContactWithValue(query, GROUPIDS, formCommaSeperatedIdList(removedOSMIdFromGroupIds));
                knLogger.debug(methodName, "Executing query ASSIGN_OSM_TO_GROUP for removedOSMIdFromGroupIds-", query);
                KnDbUtil.closePreparedStatement(pstmt);
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, null);
                pstmt.setInt(2, Integer.parseInt(corpId));
                int count=pstmt.executeUpdate();
                knLogger.debug( methodName, "EXIT: ASSIGN_OSM_TO_GROUP Query executed successfully for removedOSMIdFromGroupIds count-",count);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void onDeleteCorpProfile(int corpId,KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "onDeleteCorpProfile()";
        knLogger.debug(methodName, "Entry ",corpId);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(ON_CORP_DELETE_OSMLISTINFO);
            knLogger.debug( methodName, "Executing ON_CORP_DELETE_OSMLISTINFO  query -" ,query );
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            int osmInfoRecordDeleted=pstmt.executeUpdate();

            query = queryMapper.getQuery(ON_CORP_DELETE_CORP_OSM);
            knLogger.debug( methodName, "Executing ON_CORP_DELETE_CORP_OSM query -", query);
            KnDbUtil.closePreparedStatement(pstmt);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            int corpOsmRecordDelted=pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT:ON_CORP_DELETE_CORP_OSM del count",
                    corpOsmRecordDelted,"ON_CORP_DELETE_OSMLISTINFO delete count",osmInfoRecordDeleted);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public KnCorpOSMGroupListRespDTO getOSMGroupListByCorpAndOSMId(String corpId,
                                                                   Collection<String> OSMListIds, boolean readOnly, KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "getOSMGroupListByCorpAndOSMId(String, Collection<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry ",corpId,OSMListIds);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        KnCorpOSMGroupListRespDTO response=new KnCorpOSMGroupListRespDTO();
        List<KnXDMCorpOSMGroupList> OSMIdListMap=new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_OSM_GROUP_LIST);
            query = replaceContactWithValue(query, OSMLISTID, formCommaSeperatedIdList(OSMListIds));
            knLogger.debug( methodName, "Executing GET_OSM_GROUP_LIST query -" ,query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            result = pstmt.executeQuery();
            while(result.next()){
                OSMIdListMap.add(new KnXDMCorpOSMGroupList
                        (String.valueOf(result.getInt(1)),String.valueOf(result.getInt(2))));
            }

            response.setOSMIdListMap(OSMIdListMap);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getOSMGroupListByCorpAndOSMId  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while getOSMGroupListByCorpAndOSMId - " , e);
            throw KnDbUtil.processException(e, "Failed while getOSMGroupListByCorpAndOSMId  " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_OSM, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully",response);
        return response;
    }
}
