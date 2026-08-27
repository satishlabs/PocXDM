/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.response.KnCORPGroupStatsRespDTO;
import com.kodiak.common.commdto.response.KnCorpGroupStatsDTO;
import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubsStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class KnCorpStatsDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpStatsDAO.class);

    public String pttServerId = null;

    KnCorpStatsDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    //private static final String SELECT_GROUP_INFO="select GROUPTYPE, count(CORPGROUPID) as NOOFGROUPS, count(case when IS_LARGEGROUP=2 then 1 else null end) as MCXGROUPCOUNT,count(case when IS_LARGEGROUP=1 then 1 else null end) as LARGEGROUPS, count(case when LMR_INTEROP_CAPABLE=1 and IS_LARGEGROUP!=2 then 1 else null end) as LMRGROUPS, (count(CORPGROUPID)-count(case when IS_LARGEGROUP=1 then 1 else null end)-count(case when LMR_INTEROP_CAPABLE=1 then 1 else null end)-count(case when IS_LARGEGROUP=2 then 1 else null end)) as StdGroups, count(case when IS_LARGEGROUP='2' and LMR_INTEROP_CAPABLE='1'  then 1 else null end) as MCXLMRGROUPS, (count(case when IS_LARGEGROUP=2 then 1 else null end)-count(case when IS_LARGEGROUP='2' and LMR_INTEROP_CAPABLE='1'  then 1 else null end)) as MCXSTDGROUPS  from DG.CORPGROUPINFO where GROUPTYPE in (0,1,2,3) and CORPID = ? group by GROUPTYPE";

    private static final String SELECT_GROUP_INFO = "select GROUPTYPE, count(CORPGROUPID) as NOOFGROUPS, count(case when IS_LARGEGROUP=2 then 1 else null end) as MCXGROUPCOUNT,count(case when IS_LARGEGROUP=1 then 1 else null end) as LARGEGROUPS, count(case when LMR_INTEROP_CAPABLE=1 and IS_LARGEGROUP!=2 then 1 else null end) as LMRGROUPS, (count(CORPGROUPID)-count(case when IS_LARGEGROUP=1 then 1 else null end)-count(case when IS_LARGEGROUP=2 then 1 else null end)) as StdGroups, count(case when IS_LARGEGROUP='2' and LMR_INTEROP_CAPABLE='1'  then 1 else null end) as MCXLMRGROUPS, count(case when IS_LARGEGROUP=2 then 1 else null end) as MCXSTDGROUPS  from DG.CORPGROUPINFO where GROUPTYPE in (0,1,2,3) and CORPID = ? group by GROUPTYPE";

    private static final String SELECT_GROUP_INFO_BY_GROUPTYPE = "select GROUPTYPE, LMR_INTEROP_CAPABLE, IS_LARGEGROUP,UGWINTEROP from DG.CORPGROUPINFO WHERE CORPID = ? AND GROUPTYPE=?";

    private static final String GET_SUBS_STATS_FOR_SERVICE_AUTH_STATUS = "SELECT SERVICEAUTHSTATUS,COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE CORPID=? GROUP BY SERVICEAUTHSTATUS ORDER BY SERVICEAUTHSTATUS;";

    private static final String GET_SUBS_STATS_FOR_SERVICE_AUTH_STATUS_HIERARCHY_BY_BAN_ID = "SELECT SERVICEAUTHSTATUS,COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE MDN in (SELECT MDN FROM DG.SUBSCRIBER_ADDLINFO WHERE BAN_ID IN (IDLIST)) GROUP BY SERVICEAUTHSTATUS ORDER BY SERVICEAUTHSTATUS;";

    private static final String GET_SUBS_STATS_FOR_SERVICE_AUTH_STATUS_HIERARCHY_BY_FAN_ID = "SELECT SERVICEAUTHSTATUS,COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE MDN in (SELECT MDN FROM DG.SUBSCRIBER_ADDLINFO WHERE FAN_ID IN (IDLIST)) GROUP BY SERVICEAUTHSTATUS ORDER BY SERVICEAUTHSTATUS;";

    private static final String GET_SUBS_STATS_FOR_CLIENT_TYPE = "SELECT CLIENT_TYPE,COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE CORPID=? AND nvl(USERPROFILEINDEX,0)=0 GROUP BY CLIENT_TYPE ORDER BY CLIENT_TYPE;";

    private static final String GET_SUBS_STATS_FOR_CLIENT_TYPE_HIERARCHY_BAN_ID = "SELECT CLIENT_TYPE,COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE MDN in (SELECT MDN FROM DG.SUBSCRIBER_ADDLINFO WHERE BAN_ID IN (IDLIST)) AND nvl(USERPROFILEINDEX,0)=0 GROUP BY CLIENT_TYPE ORDER BY CLIENT_TYPE;";

    private static final String GET_SUBS_STATS_FOR_CLIENT_TYPE_HIERARCHY_FAN_ID = "SELECT CLIENT_TYPE,COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE MDN in (SELECT MDN FROM DG.SUBSCRIBER_ADDLINFO WHERE FAN_ID IN (IDLIST)) AND nvl(USERPROFILEINDEX,0)=0 GROUP BY CLIENT_TYPE ORDER BY CLIENT_TYPE;";

    private static final String QRY_SEL_DISTINCT_DEVICE_COUNT = "SELECT DEVICE_TYPE, COUNT (DEVICE_TYPE) AS DEVICE_COUNT FROM DG.DEVICE_INFO WHERE CORPID=? GROUP BY DEVICE_TYPE;";

    private static final Integer provisionedSubscriber_service_auth_status = 0;

    private static final Integer activateSubscriber_service_auth_status = 2;

    private static final Integer deactivateSubscriber_service_status = 3;

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


    public List<KnCORPGroupStatsRespDTO> getGroupStats(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupStats(int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "corpId ", corpId);

        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        KnCORPGroupStatsRespDTO knCORPGroupStatsRespDTO = null;
        KnCorpGroupStatsDTO knCorpGroupStatsDTO = null;
        List<KnCORPGroupStatsRespDTO> knCORPGroupStatsRespDTOList = new ArrayList<>();
        List<KnCORPGroupStatsRespDTO> mcxRespDTOList = new ArrayList<>();
        try {
            query = SELECT_GROUP_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, corpId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (!rs.getString("MCXGROUPCOUNT").equals("0")) {
                    knCorpGroupStatsDTO = new KnCorpGroupStatsDTO();
                    knCorpGroupStatsDTO.setGroupCount(rs.getString("MCXGROUPCOUNT"));
                    knCorpGroupStatsDTO.setLargeGroups("0");
                    knCorpGroupStatsDTO.setMcxGroups(rs.getString("MCXGROUPCOUNT"));
                    knCorpGroupStatsDTO.setStdGroups(rs.getString("MCXSTDGROUPS"));
                    knCorpGroupStatsDTO.setInteropGroups(rs.getString("MCXLMRGROUPS"));
                    knCORPGroupStatsRespDTO = new KnCORPGroupStatsRespDTO();
                    knCORPGroupStatsRespDTO.setGroupType("4");
                    knCORPGroupStatsRespDTO.setGroupStats(knCorpGroupStatsDTO);
                    mcxRespDTOList.add(knCORPGroupStatsRespDTO);
                    if (Integer.valueOf(rs.getString("NOOFGROUPS")) > Integer.valueOf(rs.getString("MCXGROUPCOUNT"))) {
                        knCORPGroupStatsRespDTO = new KnCORPGroupStatsRespDTO();
                        knCorpGroupStatsDTO = new KnCorpGroupStatsDTO();
                        knCORPGroupStatsRespDTO.setGroupType(String.valueOf(Integer.valueOf(rs.getInt("GROUPTYPE") + 1)));
                        knCorpGroupStatsDTO.setGroupCount(String.valueOf(Integer.valueOf(rs.getString("NOOFGROUPS")) - Integer.valueOf(rs.getString("MCXGROUPCOUNT"))));
                        knCorpGroupStatsDTO.setStdGroups(rs.getString("StdGroups"));
                        knCorpGroupStatsDTO.setInteropGroups(rs.getString("LMRGROUPS"));
                        knCorpGroupStatsDTO.setLargeGroups(rs.getString("LARGEGROUPS"));
                        knCorpGroupStatsDTO.setMcxGroups("0");
                        knCORPGroupStatsRespDTO.setGroupStats(knCorpGroupStatsDTO);
                        knCORPGroupStatsRespDTOList.add(knCORPGroupStatsRespDTO);
                    }
                } else {
                    knCORPGroupStatsRespDTO = new KnCORPGroupStatsRespDTO();
                    knCorpGroupStatsDTO = new KnCorpGroupStatsDTO();
                    knCORPGroupStatsRespDTO.setGroupType(String.valueOf(Integer.valueOf(rs.getInt("GROUPTYPE") + 1)));
                    knCorpGroupStatsDTO.setGroupCount(rs.getString("NOOFGROUPS"));
                    knCorpGroupStatsDTO.setStdGroups(rs.getString("StdGroups"));
                    knCorpGroupStatsDTO.setInteropGroups(rs.getString("LMRGROUPS"));
                    knCorpGroupStatsDTO.setLargeGroups(rs.getString("LARGEGROUPS"));
                    knCorpGroupStatsDTO.setMcxGroups(rs.getString("MCXGROUPCOUNT"));
                    knCORPGroupStatsRespDTO.setGroupStats(knCorpGroupStatsDTO);
                    knCORPGroupStatsRespDTOList.add(knCORPGroupStatsRespDTO);
                }
            }
            if (!mcxRespDTOList.isEmpty()) {
                knCORPGroupStatsRespDTOList.addAll(mcxRespDTOList);
            }
            return knCORPGroupStatsRespDTOList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching Group Stats ." + e, pttServerId,
                    KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(stmt);
            knLogger.debug(methodName, "EXIT : response ", knCORPGroupStatsRespDTOList);
        }
    }

    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO corpInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberStats()";
        String query = null;
        String queryForClientType = null;
        Connection conn;
        PreparedStatement pstmt = null;
        PreparedStatement pStmtAddl = null;
        ResultSet rs = null;
        ResultSet rsAdd = null;
        KnCorpSubsStatsRespDTO subsStatsRespDTO = new KnCorpSubsStatsRespDTO();
        Map<Integer, Integer> subsStatsMap = new HashMap<>();
        Map<Integer, Integer> clientTypeCount = new HashMap<>();
        knLogger.info(methodName, "ENTRY : corpInfoDTO", corpInfoDTO);
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if (null != corpInfoDTO.getHierarchyType() && null != corpInfoDTO.getCustomParamMap() && corpInfoDTO.getHierarchyType().equals(KnConstants.HIERARCHY_TYPE.HIERARCHY) && null != corpInfoDTO.getCustomParamMap().get(KnConstants.IDTYPE)) {
                Collection<String> idList = null;
                if (KnConstants.BAN_TYPE.equals(corpInfoDTO.getCustomParamMap().get(KnConstants.IDTYPE))) {
                    query = GET_SUBS_STATS_FOR_SERVICE_AUTH_STATUS_HIERARCHY_BY_BAN_ID;
                    queryForClientType = GET_SUBS_STATS_FOR_CLIENT_TYPE_HIERARCHY_BAN_ID;
                } else if (KnConstants.FAN_TYPE.equals(corpInfoDTO.getCustomParamMap().get(KnConstants.IDTYPE))) {
                    query = GET_SUBS_STATS_FOR_SERVICE_AUTH_STATUS_HIERARCHY_BY_FAN_ID;
                    queryForClientType = GET_SUBS_STATS_FOR_CLIENT_TYPE_HIERARCHY_FAN_ID;
                }
                idList = (Collection<String>) corpInfoDTO.getCustomParamMap().get(KnConstants.IDLIST);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(idList, query, KnConstants.IDLIST);
                queryForClientType = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(idList, queryForClientType, KnConstants.IDLIST);

                knLogger.debug(methodName, "QUERY: Executing the Query ", query, "idListCollection", idList);
                pstmt = conn.prepareStatement(query);
                int i = 1;
                for (String s : idList) {
                    pstmt.setInt(i++, Integer.parseInt(s));
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    subsStatsMap.put(rs.getInt(1), rs.getInt(2));
                }
                knLogger.debug(methodName, "QUERY: Executed the Query");
                if (!subsStatsMap.isEmpty()) { // if it is empty then warn with message
                    subsStatsRespDTO.setActivatedSubscribers(subsStatsMap.get(activateSubscriber_service_auth_status));
                    subsStatsRespDTO.setProvisionedSubscribers(subsStatsMap.get(provisionedSubscriber_service_auth_status));
                    subsStatsRespDTO.setDeactivateSubscribers(subsStatsMap.get(deactivateSubscriber_service_status));
                }
                knLogger.debug(methodName, "QUERY: Executing the Query idListCollection ", queryForClientType, "idListCollection", idList);
                clientTypeCount=getClientTypeStats(queryForClientType,idList,clientTypeCount,persisterTxn);
                subsStatsRespDTO.setClientTypeSubscribers(clientTypeCount);

            } else {
                query = GET_SUBS_STATS_FOR_SERVICE_AUTH_STATUS;
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, corpInfoDTO.getCorpId());
                knLogger.info(methodName, "QUERY: Executing the Query ", query);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    subsStatsMap.put(rs.getInt(1), rs.getInt(2));
                }
                if (!subsStatsMap.isEmpty()) {
                    subsStatsRespDTO.setActivatedSubscribers(subsStatsMap.get(activateSubscriber_service_auth_status));
                    subsStatsRespDTO.setProvisionedSubscribers(subsStatsMap.get(provisionedSubscriber_service_auth_status));
                    subsStatsRespDTO.setDeactivateSubscribers(subsStatsMap.get(deactivateSubscriber_service_status));
                }
                queryForClientType = GET_SUBS_STATS_FOR_CLIENT_TYPE;
                clientTypeCount=getClientTypeStats(queryForClientType,corpInfoDTO.getCorpId(),clientTypeCount,persisterTxn);
                subsStatsRespDTO.setClientTypeSubscribers(clientTypeCount);
            }
            return subsStatsRespDTO;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while getting the stats for Subscribers - ", e);
            throw KnDbUtil.processException(e, "Failed to get the subscriberStats ",
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(rsAdd);
            KnDbUtil.closePreparedStatement(pstmt);
            KnDbUtil.closePreparedStatement(pStmtAddl);
        }
    }

    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceStats(String, boolean, KnPersisterTxn)";
        String query = QRY_SEL_DISTINCT_DEVICE_COUNT;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        KnCorpDeviceStatsRespDTO deviceStatsRespDTO = new KnCorpDeviceStatsRespDTO();
        //List<String> deviceStatsMap=new ArrayList<String>();
        Map<Integer, Integer> deviceStatsMap = new HashMap<>(1);
        knLogger.debug(methodName, "ENTRY : CorpId", corpId, "DeviceId");
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpId);
            knLogger.debug(methodName, "QUERY: Executing the Query ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                deviceStatsMap.put(rs.getInt(1), (rs.getInt(2)));
            }
            deviceStatsRespDTO.setDeviceCountByDeviceType(deviceStatsMap);
            knLogger.debug(methodName, "QUERY: Executed the Query");

        } catch (SQLException | KnConnectionException e) {
            try {
                knLogger.error(methodName, "Exception occurred while getting the stats for Device - ", e);
                throw KnDbUtil.processException(e, "Failed to fetch Device Stats",
                        pttServerId, KnDAOSourceTypes.XDM_CORP_DEVICE_INFO, query);

            } catch (KnDAOException ex) {
                knLogger.error(methodName, "DAOException occurred while getting the stats for Device - ", e);
                throw KnDbUtil.processException(e, "Failed to fetch Device Stats",
                        pttServerId, KnDAOSourceTypes.XDM_CORP_DEVICE_INFO, query);
            }
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return deviceStatsRespDTO;
    }


    public static String formCommaSeperatedIdList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("'").append(mdn).append("',");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }


    public Map<Integer, Integer> getClientTypeStats(String queryForClientType, Collection<String> idList, Map<Integer, Integer> clientTypeCount, KnPersisterTxn persisterTxn) throws SQLException, KnConnectionException {
        String methodName = "getClientTypeStats(String queryForClientType,Collection<String> idList,KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "idList ", idList);
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            try (PreparedStatement pStmtAddl = conn.prepareStatement(queryForClientType)) {
                int j = 1;
                for (String s : idList) {
                    pStmtAddl.setInt(j++, Integer.parseInt(s));
                }
                try (ResultSet rsAdd = pStmtAddl.executeQuery()) {
                    while (rsAdd.next()) {
                        clientTypeCount.put(rsAdd.getInt(1), rsAdd.getInt(2));
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return clientTypeCount;
    }

    public Map<Integer, Integer> getClientTypeStats(String queryForClientType, int corpId, Map<Integer, Integer> clientTypeCount, KnPersisterTxn persisterTxn) throws SQLException, KnConnectionException {
        String methodName = "getClientTypeStats(String queryForClientType, int corpId,Map<Integer, Integer> clientTypeCount, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "corpId", corpId);
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            try (PreparedStatement pStmtAddl = conn.prepareStatement(queryForClientType)) {
                pStmtAddl.setInt(1, corpId);
                knLogger.info(methodName, "QUERY: Executing the Query ", queryForClientType);
                try (ResultSet rsAdd = pStmtAddl.executeQuery()) {
                    while (rsAdd.next()) {
                        clientTypeCount.put(rsAdd.getInt(1), rsAdd.getInt(2));
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return clientTypeCount;
    }
}