/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAuthUserPermissionInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnMcpttPermissionDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsDestEmergencyAttributes;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsEmergencyAttributes;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.getAuthDocumentURI;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.getEmergDocumentURI;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMCorpMcpttInfoDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMCorpMcpttInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpMcpttInfoDAO.class);
    private static final String DISCREET_ENABLED = "DISCREET_ENABLED";

    public String pttServerId = null;

    KnXDMCorpMcpttInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Unimplemented Methods");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("update", "Unimplemented Methods");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("delete", "Unimplemented Methods");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("select", "Unimplemented Methods");
        return null;
    }

    public Map<String, Long> getTargetUserPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly,
                                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTargetUserPermissions(KnIPAuthUserPermissionInfoDTO, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "getTargetUserPermissions", ipAuthUserPermissionInfoDTO);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<String, Long> targetMdnPermBitInfos = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_AUTH_USER_PERMISSIONS);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ipAuthUserPermissionInfoDTO.getAuthorizedMdn());
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while(rs.next()){
                targetMdnPermBitInfos.put(rs.getString(3).trim(), rs.getLong(4));
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getTargetUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.exit(methodName);
        return targetMdnPermBitInfos;
    }

    public Map<String, KnMcpttPermissionDTO> getAuthUserPermissions(String authMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAuthUserPermissions(String)";
        knLogger.debug(methodName, "getAuthUserPermissions", KnGDPRTemplate.mdn(authMdn), " readOnly :", readOnly);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, KnMcpttPermissionDTO> mcpttPermissionDTOS = new HashMap<>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_AUTH_USER_PERMISSIONS);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, authMdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            KnMcpttPermissionDTO permissionDTO = null;
            while(rs.next()){
                permissionDTO = new KnMcpttPermissionDTO();
                String targetMdn = rs.getString(3).trim();
                permissionDTO.setCorpid(rs.getInt(1));
                permissionDTO.setAuthMdn(rs.getString(2).trim());
                permissionDTO.setTargetMdn(targetMdn);
                permissionDTO.setMcpttPerms(rs.getLong(4));
                permissionDTO.setDiscreteEnabled((Integer) rs.getObject(DISCREET_ENABLED));
                permissionDTO.setCommonAu(rs.getInt(6));
                mcpttPermissionDTOS.put(targetMdn, permissionDTO);
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getAuthUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.exit(methodName);
        return mcpttPermissionDTOS;
    }

    public Map<String, KnMcpttPermissionDTO> getTargUserPermissions(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTargUserPermissions(String)";
        knLogger.debug(methodName, "getTargUserPermissions", KnGDPRTemplate.mdn(targetMdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, KnMcpttPermissionDTO> mcpttPermissionDTOS = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_TARG_USER_PERMISSIONS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, targetMdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            KnMcpttPermissionDTO permissionDTO = null;
            while(rs.next()){
                permissionDTO = new KnMcpttPermissionDTO();
                String authMdn = rs.getString(2).trim();
                permissionDTO.setCorpid(rs.getInt(1));
                permissionDTO.setAuthMdn(authMdn);
                permissionDTO.setTargetMdn(rs.getString(3).trim());
                permissionDTO.setMcpttPerms(rs.getLong(4));
                permissionDTO.setDiscreteEnabled((Integer) rs.getObject(DISCREET_ENABLED));
                mcpttPermissionDTOS.put(authMdn, permissionDTO);
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getTargUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return mcpttPermissionDTOS;
    }

    public void insertIntoMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoMcpttPermInfo(Collection<KnMcpttPermissionDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mcpttMappingDto - ", mcpttMappingDto);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_AUTH_USER_PERMISSIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnMcpttPermissionDTO targetMapping : mcpttMappingDto) {
                pstmt.setInt(1, targetMapping.getCorpid());
                pstmt.setString(2, targetMapping.getAuthMdn());
                pstmt.setString(3, targetMapping.getTargetMdn());
                pstmt.setLong(4, targetMapping.getMcpttPerms());
                if (targetMapping.getDiscreteEnabled() != null) {
                    pstmt.setInt(5, targetMapping.getDiscreteEnabled());
                } else {
                    pstmt.setNull(5, Types.INTEGER);
                }
                if(targetMapping.getCommonAu() != null && targetMapping.getCommonAu() == 1){
                    pstmt.setInt(6, targetMapping.getCommonAu());
                }else{
                    pstmt.setNull(6, Types.INTEGER);
                }

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromMcpttPermInfoAuthMdn(String authMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPermInfoAuthMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : authMdn - ", KnGDPRTemplate.mdn(authMdn));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_AUTH_MDN);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, authMdn);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete auth mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromMcpttPermInfoAuthMdn(List<String> authMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPermInfoAuthMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : authMdn - ", KnGDPRTemplate.mdnList(authMdn));
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "DELETE FROM DG.MCPTT_PERM_INFO WHERE AUTHORIZED_MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(authMdn,query,"MDNLIST");
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : authMdn) {
                pstmt.setString(index++, mdn);
            }
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete auth mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromMcpttPermInfoTargetMdn(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPermInfoTargetMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : targetMdn - ", KnGDPRTemplate.mdn(targetMdn));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_TARGET_MDN);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, targetMdn);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete target mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromMcpttPermInfoTargetMdn(List<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPermInfoTargetMdn(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : targetMdn - ", KnGDPRTemplate.mdnList(targetMdn));
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = "DELETE FROM DG.MCPTT_PERM_INFO WHERE TARGET_MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(targetMdn,query,"MDNLIST");
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(String mdn : targetMdn){
                pstmt.setString(index++, mdn);
            }
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete target mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateToMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateToMcpttPermInfo(Collection<KnMcpttPermissionDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mcpttMappingDto - ", mcpttMappingDto);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_AUTH_USER_PERMISSIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnMcpttPermissionDTO targetMapping : mcpttMappingDto) {
                pstmt.setLong(1, targetMapping.getMcpttPerms());
                if (targetMapping.getDiscreteEnabled() != null) {
                    pstmt.setInt(2, targetMapping.getDiscreteEnabled());
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }
                if (targetMapping.getCommonAu() != null && targetMapping.getCommonAu() ==1) {
                    pstmt.setInt(3, targetMapping.getCommonAu());
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }
                pstmt.setString(4, targetMapping.getAuthMdn());
                pstmt.setString(5, targetMapping.getTargetMdn());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateToMcpttPermInfoForPrivacyStatus(Map<String,Integer> mapListForPrivacy,KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateToMcpttPermInfoForPrivacyStatus(Map<String,Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Point : mapListForPrivacy - ", mapListForPrivacy);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_PRIVACY_OPT_STATUS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            
            for(String mdn:mapListForPrivacy.keySet()) {

                pstmt.setInt(1, mapListForPrivacy.get(mdn));
                pstmt.setLong(2, System.currentTimeMillis());
                pstmt.setString(3, mdn);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromMcpttPermInfo(String authMdn, Collection<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPermInfo(String, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : authMdn - ", KnGDPRTemplate.mdn(authMdn), "targetMdn: ", targetMdn == null ? targetMdn : KnGDPRTemplate.mdnList(targetMdn));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_AUTH_USER_PERMISSIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String targetMan : targetMdn) {
                pstmt.setString(1, authMdn);
                pstmt.setString(2, targetMan);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Integer> getAuthUserMappingCount(Collection<String> authMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAuthUserMappingCount(String)";
        knLogger.debug(methodName, "getAuthUserMappingCount", authMdnList == null ? authMdnList : KnGDPRTemplate.mdnList(authMdnList));
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> authMdnMappingCount = new HashMap<>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_AUTH_USER_MAPPING_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(authMdnList));
            query = KnDbUtil.replaceValInQry(query, corpId);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            while(rs.next()){
                authMdnMappingCount.put(rs.getString(1).trim(), rs.getInt(2));
            }
            authMdnList.forEach(authMdn -> {
                if(!authMdnMappingCount.containsKey(authMdn)){
                    authMdnMappingCount.put(authMdn, 0);
                }
            });
            knLogger.debug(methodName, "EXit: Query executed successfully");
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
        } catch (SQLException e) {
            if(ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while getAuthUserMappingCount",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.exit(methodName);
        return authMdnMappingCount;
    }

    public Map<String, KnOPDocChgDTO> deleteFromAuthorizationInfo(Collection<String> authMdnList, Map<String, KnOPDocChgDTO> authorizationMap,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromAuthorizationInfo(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : authMdn - ", authMdnList == null ? authMdnList : KnGDPRTemplate.mdnList(authMdnList));
        PreparedStatement pstmt = null;
        String query = null;
        boolean ownedTxn = false;
        if(authorizationMap == null){
            authorizationMap = new HashMap<String, KnOPDocChgDTO>();
        }
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_AUTH_USER_DOC);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String authMdn : authMdnList) {
                pstmt.setString(1, authMdn);
                KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                docChngDto.setDocType(1);
                docChngDto.setDocUri(getAuthDocumentURI(authMdn));
                docChngDto.setNewEtag("0");
                docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
                authorizationMap.put(authMdn, docChngDto);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            if(ownedTxn){
                persisterTxn.save();
            }
        } catch (SQLException e) {
            if(ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete Authorization Doc from DG.AUTHORIZATION_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        return authorizationMap;
    }

    public Map<String, Long> seleteFromAuthDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "seleteFromAuthDoc(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY mdnList : mdn - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Long> authEtagMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_AUTH_USER_DOC);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query 1 - ", query);
            rs = stmt.executeQuery(query);
            while(rs.next()){
                authEtagMap.put(rs.getString(1).trim(), rs.getLong(2));
            }
            knLogger.debug(methodName, "authEtagMap - ", authEtagMap);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete from DG.AUTHORIZATION_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return authEtagMap;
    }

    public void insertIntoAuthDoc(Collection<String> mdnList, Map<String, Long> authEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoAuthDoc(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY mdnList : mdn - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_AUTH_USER_DOC);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            if (mdnList != null) {
                for (String mdn : mdnList) {
                    long etag = System.currentTimeMillis();
                    pstmt.setString(1, mdn);
                    pstmt.setLong(2, etag);
                    pstmt.addBatch();
                }
            } else {
                for (Map.Entry<String, Long> etagMap : authEtagMap.entrySet()) {
                    pstmt.setString(1, etagMap.getKey());
                    pstmt.setLong(2, etagMap.getValue());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert to DG.AUTHORIZATION_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, KnOPDocChgDTO> insertOrUpdateAuthorizationInfo(Collection<String> authMdnList, Map<String, KnOPDocChgDTO> authorizationMap,
                                                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertOrUpdateAuthorizationInfo(Collection<String>, Map<String, KnOPDocChgDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : authMdnList - ", authMdnList == null ? authMdnList : KnGDPRTemplate.mdnList(authMdnList));
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection readOnlyConn;
        Connection connection;
        int index = 1;
        if (authorizationMap == null) {
            authorizationMap = new HashMap<String, KnOPDocChgDTO>();
        }
        try {
            readOnlyConn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_AUTH_USER_DOC);
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(authMdnList));
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(authMdnList, query, "MDNLIST");
            pstmt = readOnlyConn.prepareStatement(query);
            for (String mdn : authMdnList) {
                pstmt.setString(index++, mdn);
            }
            knLogger.debug(methodName, "Executing query 1 - ", query);
            rs = pstmt.executeQuery();
            Map<String, Long> authMap = new HashMap<>();
            while (rs.next()) {
                authMap.put(rs.getString(1).trim(), rs.getLong(2));
            }
            knLogger.debug(methodName, "authMap - ", authMap);
            Collection<String> authDocUpdate = authMdnList.stream().filter(authMap::containsKey).collect(Collectors.toList());
            knLogger.debug(methodName, "authDocUpdate - ", authDocUpdate);
            Collection<String> authDocAdd = authMdnList.stream().filter(authMdn -> !authMap.containsKey(authMdn)).collect(Collectors.toList());
            knLogger.debug(methodName, "authDocAdd - ", authDocAdd);
            if (authDocUpdate != null && !authDocUpdate.isEmpty()) {
                connection = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                query = queryMapper.getQuery(UPDATE_AUTH_USER_DOC);
                pstmt = connection.prepareStatement(query);
                for (String authMdn : authDocUpdate) {
                    long etag = System.currentTimeMillis();
                    pstmt.setLong(1, etag);
                    pstmt.setString(2, authMdn);
                    pstmt.addBatch();
                    knLogger.debug(methodName, "Executing query 2- ", query);
                    KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                    if (authMap.get(authMdn) != null) docChngDto.setPrevEtag(String.valueOf(authMap.get(authMdn)));
                    docChngDto.setDocType(1);
                    docChngDto.setDocUri(getAuthDocumentURI(authMdn));
                    docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    docChngDto.setNewEtag(String.valueOf(System.currentTimeMillis()));
                    authorizationMap.put(authMdn, docChngDto);
                }
            } else {
                connection = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                query = queryMapper.getQuery(INSERT_AUTH_USER_DOC);
                pstmt = connection.prepareStatement(query);
                for (String authMdn : authMdnList) {
                    long etag = System.currentTimeMillis();
                    pstmt.setString(1, authMdn);
                    pstmt.setLong(2, etag);
                    pstmt.addBatch();
                    knLogger.debug(methodName, "Executing query 3- ", query);
                    KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                    docChngDto.setDocType(1);
                    docChngDto.setDocUri(getAuthDocumentURI(authMdn));
                    docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.ADD.value());
                    docChngDto.setNewEtag(String.valueOf(etag));
                    authorizationMap.put(authMdn, docChngDto);
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertOrUpdate Authorization Doc from DG.AUTHORIZATION_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return authorizationMap;
    }

    public void deleteFromMcpttPerm(Map<String, Collection<String>> authTargetMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPerm(Map<String, Collection<String>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point :targetMdnList: ", authTargetMap == null ? authTargetMap : KnGDPRTemplate.mapKeyMdn(authTargetMap));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_AUTH_USER_PERMISSIONS);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for(Map.Entry<String, Collection<String>> entry : authTargetMap.entrySet()){
                for(String target : entry.getValue()){
                    pstmt.setString(1, entry.getKey());
                    pstmt.setString(2, target);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromMcpttPermInfo(Map<String, String> authTargetMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPermInfo(Map<String, String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point :targetMdnList: ", authTargetMap == null ? authTargetMap : KnGDPRTemplate.mdnMap(authTargetMap));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_AUTH_USER_PERMISSIONS);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for(Map.Entry<String, String> entry : authTargetMap.entrySet()){
                pstmt.setString(1, entry.getKey());
                pstmt.setString(2, entry.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Collection<String> getAuthMdnListFromTarget(Collection<String> targetMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAuthMdnListFromTarget(Collection<String>)";
        knLogger.debug(methodName, "getAuthMdnListFromTarget", targetMdnList == null ? targetMdnList : KnGDPRTemplate.mdnList(targetMdnList));
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<String> authTargetMap = new ArrayList<>();
        boolean ownedTxn = false;
        //MINT-14813 null pointer exception due to targetMdnList is null
        if(null == targetMdnList || targetMdnList.isEmpty()){
            return authTargetMap;
        }
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_AUTH_TARGET_MAPPING);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(targetMdnList));
            query = KnDbUtil.replaceValInQry(query, corpId);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            while(rs.next()){
                authTargetMap.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
        } catch (SQLException e) {
            if(ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while getAuthMdnListFromTarget",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.exit(methodName);
        return authTargetMap;
    }

    public Map<String, Integer> getAuthMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly,
                                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAuthMdnList(KnIPAuthUserPermissionInfoDTO, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "getAuthMdnList", ipAuthUserPermissionInfoDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> authMdnMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_AUTH_USER_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ipAuthUserPermissionInfoDTO.getTargetMdn());
            pstmt.setInt(2, ipAuthUserPermissionInfoDTO.getCorpId());
            pstmt.setInt(3, 1);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while(rs.next()){
                authMdnMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getAuthMdnList",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return authMdnMap;
    }

    public Collection<KnSubsDestEmergencyAttributes> getEmergDestAttributes(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergDestAttributes(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "getEmergDestAttributes", KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<KnSubsDestEmergencyAttributes> corpEmergencyAttributes = new ArrayList<>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_EMERGENCY_ATTRIBUTE);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            KnSubsDestEmergencyAttributes subsDestInfo = null;
            while(rs.next()){
                subsDestInfo = new KnSubsDestEmergencyAttributes();
                String subsMdn = rs.getString(1).trim();
                subsDestInfo.setMdn(subsMdn);
                subsDestInfo.setEmergDestPriority(rs.getInt(2));
                subsDestInfo.setEmergDestTypeMgmt(rs.getInt(3));
                subsDestInfo.setEmergDest(rs.getString(4));
                corpEmergencyAttributes.add(subsDestInfo);
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getEmergDestAttributes",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.exit(methodName);
        return corpEmergencyAttributes;
    }

    public Map<String, Collection<String>> getEmergUserDestMap(Collection<String> emergUserList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergUserDestMap(String)";
        knLogger.debug(methodName, "getEmergUserDestMap", emergUserList);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Collection<String>> emergUserMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_EMERGENCY_USER_DEST);
            query = replaceContactWithValue(query, EMERGMDNLIST, formCommaSeperatedIdList(emergUserList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            Collection<String> emergDestList = null;
            while(rs.next()){
                String emergUser = rs.getString(1).trim();
                String emergDest = rs.getString(4);
                if(emergUserMap.get(emergUser) != null){
                    emergDestList = emergUserMap.get(emergUser);
                    emergDestList.add(emergDest);
                } else {
                    emergDestList = new ArrayList<>();
                    emergDestList.add(emergDest);
                    emergUserMap.put(emergUser, emergDestList);
                }
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getEmergUserDestMap",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.exit(methodName);
        return emergUserMap;
    }

    public Map<String, Collection<String>> getEmergDestUserMap(Collection<String> emergDestList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergDestUserMap(String)";
        knLogger.debug(methodName, "getEmergDestUserMap", emergDestList);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Collection<String>> emergDestUserMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_EMERGENCY_DEST_USER);
            query = replaceContactWithValue(query, EMERGDESTLIST, formCommaSeperatedIdList(emergDestList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            Collection<String> emergUserList = null;
            while(rs.next()){
                String emergUser = rs.getString(1).trim();
                String emergDest = rs.getString(4);
                if(emergDestUserMap.get(emergDest) != null){
                    emergUserList = emergDestUserMap.get(emergDest);
                    emergUserList.add(emergUser);
                } else {
                    emergUserList = new ArrayList<>();
                    emergUserList.add(emergUser);
                    emergDestUserMap.put(emergDest, emergUserList);
                }
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getEmergUserDestMap",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.exit(methodName);
        return emergDestUserMap;
    }

    public Collection<KnSubsDestEmergencyAttributes> getEmergDestAttributesForDestination(String emergDest, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergDestAttributesForDestination(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "getEmergDestAttributesForDestination", KnGDPRTemplate.mdn(emergDest));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<KnSubsDestEmergencyAttributes> corpEmergencyAttributes = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_EMERGENCY_DESTINATIONS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, emergDest);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            KnSubsDestEmergencyAttributes subsDestInfo = null;
            while(rs.next()){
                subsDestInfo = new KnSubsDestEmergencyAttributes();
                String subsMdn = rs.getString(1).trim();
                subsDestInfo.setMdn(subsMdn);
                subsDestInfo.setEmergDestPriority(rs.getInt(2));
                subsDestInfo.setEmergDestTypeMgmt(rs.getInt(3));
                subsDestInfo.setEmergDest(rs.getString(4));
                corpEmergencyAttributes.add(subsDestInfo);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getEmergDestAttributesForDestination",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "cont-->", corpEmergencyAttributes.size());
        knLogger.exit(methodName);
        return corpEmergencyAttributes;
    }

    public KnSubsEmergencyAttributes getEmergSubsAttributes(String mdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergSubsAttributes(String,int,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "getEmergSubsAttributes", KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnSubsEmergencyAttributes subsEmergencyAttributes = new KnSubsDestEmergencyAttributes();
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_POC_EMERGENCY_ATTRIBUTE);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            if(rs.next()){
                subsEmergencyAttributes.setEmergCallType((Integer) rs.getObject(1));
                subsEmergencyAttributes.setEmergCnclPermission((Integer) rs.getObject(2));
                subsEmergencyAttributes.setEmergLmrBehaviour((Integer) rs.getObject(3));
                subsEmergencyAttributes.setEmergInitPermission((Integer) rs.getObject(4));
                subsEmergencyAttributes.setEmergDestTypeIntf((Integer) rs.getObject(5));
                subsEmergencyAttributes.setEmergOriginBitSet((Integer) rs.getObject(6));
                subsEmergencyAttributes.setEmergTermBitSet((Integer) rs.getObject(7));
                knLogger.debug(methodName, " doing getString() here :- ", rs.getString(8));
                subsEmergencyAttributes.setEmergConfigTimer(rs.getString(8));
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getEmergSubsAttributes",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.exit(methodName);
        return subsEmergencyAttributes;
    }

    public void deleteFromEmergSubsDestInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromEmergSubsDestInfo(String, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdn - ", KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_EMERGENCY_ATTRIBUTE);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.EMERGENCY_SUBSCR_DESTINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromEmergSubsDestInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromEmergSubsDestInfo(String, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdn - ", KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query ="DELETE FROM DG.EMERGENCY_SUBSCR_DESTINFO WHERE MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.EMERGENCY_SUBSCR_DESTINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromEmergInfoForDest(String emergDest, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromEmergInfoForDest(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY emergDest : mdn - ", KnGDPRTemplate.mdn(emergDest));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_EMERGENCY_DEST_ENTRY);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, emergDest);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.EMERGENCY_SUBSCR_DESTINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromEmergInfoForDest(List<String> emergDest, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromEmergInfoForDest(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY emergDest : mdn - ", KnGDPRTemplate.mdnList(emergDest));
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "DELETE FROM DG.EMERGENCY_SUBSCR_DESTINFO WHERE EMERGDEST IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(emergDest,query,"MDNLIST");
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(String emergDestStr : emergDest){
                pstmt.setString(index++, emergDestStr);
            }
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.EMERGENCY_SUBSCR_DESTINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateToEmergSubsDestInfo(KnSubsEmergencyAttributes subsEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateToEmergSubsDestInfo(KnSubsEmergencyAttributes, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : subsEmergencyAttributes - ", subsEmergencyAttributes);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_EMERG_POCSUBS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            if(subsEmergencyAttributes.getEmergCallType() != null) {
                pstmt.setInt(1, subsEmergencyAttributes.getEmergCallType());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            if(subsEmergencyAttributes.getEmergCnclPermission() != null) {
                pstmt.setInt(2, subsEmergencyAttributes.getEmergCnclPermission());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            if(subsEmergencyAttributes.getEmergLmrBehaviour() != null) {
                pstmt.setInt(3, subsEmergencyAttributes.getEmergLmrBehaviour());
            } else {
                pstmt.setInt(3, DISABLED);
            }
            if(subsEmergencyAttributes.getEmergInitPermission() != null) {
                pstmt.setInt(4, subsEmergencyAttributes.getEmergInitPermission());
            } else {
                pstmt.setInt(4, DISABLED);
            }
            if (null != subsEmergencyAttributes.getEmergConfigTimer()) {
                pstmt.setFloat(5, Float.parseFloat(subsEmergencyAttributes.getEmergConfigTimer()));
            } else {
                pstmt.setNull(5, Types.DECIMAL);
            }
            pstmt.setString(6, subsEmergencyAttributes.getMdn());
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update mcptt mapping DG.EMERGENCY_SUBSCR_DESTINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateToEmergSubsDestAddInfo(KnSubsEmergencyAttributes subsEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateToEmergSubsDestAddInfo(KnSubsEmergencyAttributes, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : subsEmergencyAttributes - ", subsEmergencyAttributes);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_EMERG_POCSUBS_ADD);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            if(subsEmergencyAttributes.getEmergDestTypeIntf() != null) {
                pstmt.setInt(1, subsEmergencyAttributes.getEmergDestTypeIntf());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            if(subsEmergencyAttributes.getEmergOriginBitSet() != null){
                pstmt.setInt(2, subsEmergencyAttributes.getEmergOriginBitSet());
            } else {
                pstmt.setInt(2, DEFAULT_EMERGENCY_BIT_SET);
            }
            if(subsEmergencyAttributes.getEmergTermBitSet() != null){
                pstmt.setInt(3, subsEmergencyAttributes.getEmergTermBitSet());
            } else {
                pstmt.setInt(3, DEFAULT_EMERGENCY_BIT_SET);
            }
            pstmt.setString(4, subsEmergencyAttributes.getMdn());
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update mcptt mapping DG.POCSUBSCR_ADDLINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void insertIntoEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : destEmergencyAttributes - ", destEmergencyAttributes);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_EMERGENCY_DEST_ATTIBUTES);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnSubsDestEmergencyAttributes subsDestEmergencyAttributes : destEmergencyAttributes) {
                pstmt.setString(1, subsDestEmergencyAttributes.getMdn());
                pstmt.setInt(2, subsDestEmergencyAttributes.getEmergDestPriority());
                pstmt.setInt(3, subsDestEmergencyAttributes.getEmergDestTypeMgmt());
                pstmt.setString(4, subsDestEmergencyAttributes.getEmergDest());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert mcptt mapping DG.EMERGENCY_SUBSCR_DESTINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : destEmergencyAttributes - ", destEmergencyAttributes);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_EMERGENCY_DEST_ATTIBUTES);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnSubsDestEmergencyAttributes subsDestEmergencyAttributes : destEmergencyAttributes) {
                pstmt.setInt(1, subsDestEmergencyAttributes.getEmergDestTypeMgmt());
                pstmt.setString(2, subsDestEmergencyAttributes.getEmergDest());
                pstmt.setString(3, subsDestEmergencyAttributes.getMdn());
                pstmt.setInt(4, subsDestEmergencyAttributes.getEmergDestPriority());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update mcptt mapping DG.EMERGENCY_SUBSCR_DESTINFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromEmergDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromEmergDoc(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY mdnList : mdn - ", mdnList == null ?mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_FROM_EMERGENCY_DOC);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete from DG.EMERGENCY_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Long> seleteFromEmergDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "seleteFromEmergDoc(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY mdnList : mdn - ", mdnList == null ?mdnList : KnGDPRTemplate.mdnList(mdnList));
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Long> emergEtagMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_EMERG_USER_DOC);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query 1 - ", query);
            rs = stmt.executeQuery(query);
            while(rs.next()){
                emergEtagMap.put(rs.getString(1).trim(), rs.getLong(2));
            }
            knLogger.debug(methodName, "authEtagMap - ", emergEtagMap);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete from DG.AUTHORIZATION_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return emergEtagMap;
    }

    public void insertIntoEmergDoc(Collection<String> mdnList, Map<String, Long> emergEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoEmergDoc(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY mdnList : mdn - ", mdnList == null ?mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_EMERG_USER_DOC);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            if (mdnList != null) {
                for (String mdn : mdnList) {
                    long etag = System.currentTimeMillis();
                    pstmt.setString(1, mdn);
                    pstmt.setLong(2, etag);
                    pstmt.addBatch();
                }
            } else {
                for (Map.Entry<String, Long> etagMap : emergEtagMap.entrySet()) {
                    pstmt.setString(1, etagMap.getKey());
                    pstmt.setLong(2, etagMap.getValue());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert to DG.AUTHORIZATION_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, KnOPDocChgDTO> insertOrUpdateEmergencyInfo(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertOrUpdateEmergencyInfo(Collection<String>,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList - ", mdnList == null ?mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt=null;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        Connection conn;
        Map<String, KnOPDocChgDTO> emergencyMap = new HashMap<String, KnOPDocChgDTO>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_EMERG_USER_DOC);
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query 1 - ", query);
            for(String mdn : mdnList){
                pstmt.setString(index++ , mdn);
            }
            rs = pstmt.executeQuery();
            Map<String, Long> emergMap = new HashMap<>();
            while(rs.next()){
                emergMap.put(rs.getString(1).trim(), rs.getLong(2));
            }
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            rs = null; pstmt = null;
            if(!emergMap.isEmpty()){
                query = queryMapper.getQuery(UPDATE_EMERG_USER_DOC);
                pstmt = conn.prepareStatement(query);
                for (String mdn : mdnList) {
                    long etag = System.currentTimeMillis();
                    pstmt.setLong(1, etag);
                    pstmt.setString(2, mdn);
                    pstmt.addBatch();
                    knLogger.debug(methodName, "Executing query 2- ", query);
                    KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                    if(emergMap.get(mdn) != null) docChngDto.setPrevEtag(String.valueOf(emergMap.get(mdn)));
                    docChngDto.setDocType(1);
                    docChngDto.setDocUri(getEmergDocumentURI(mdn));
                    docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    docChngDto.setNewEtag(String.valueOf(etag));
                    emergencyMap.put(mdn, docChngDto);
                }
            } else {
                query = queryMapper.getQuery(INSERT_EMERG_USER_DOC);
                pstmt = conn.prepareStatement(query);
                for (String mdn : mdnList) {
                    long etag = System.currentTimeMillis();
                    pstmt.setString(1, mdn);
                    pstmt.setLong(2, etag);
                    pstmt.addBatch();
                    knLogger.debug(methodName, "Executing query 3- ", query);
                    KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                    docChngDto.setDocType(1);
                    docChngDto.setDocUri(getEmergDocumentURI(mdn));
                    docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.ADD.value());
                    docChngDto.setNewEtag(String.valueOf(etag));
                    emergencyMap.put(mdn, docChngDto);
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertOrUpdate Emergency Doc from DG.EMERGENCY_DOC table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return emergencyMap;
    }

    public boolean isEmergencyDestExists(String emergDest, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isEmergencyDestExists(String, boolean, persisterTxn)";
        knLogger.debug(methodName, "isEmergencyDestExists", emergDest);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean destExists = false;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_EMERGENCY_DESTINATIONS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, emergDest);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            if(rs.next()){
                destExists = true;
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while isEmergencyDestExists",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return destExists;
    }

    /**
     * This method gets base and profile mdn map.
     * @param baseMdnList
     * @param persisterTxn
     * @return Map<String, List<String>>
     * @throws KnDAOException
     */
    public Map<String, List<String>> getMapOfProfileMdnByBaseMdn(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "DAO.getMapOfProfileMdnByBaseMdn(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "baseMdnList", baseMdnList == null ? baseMdnList : KnGDPRTemplate.mdnList(baseMdnList));
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        boolean ownedTxn = false;
        ResultSet rs = null;
        Map<String, List<String>> baseNprofileMdnMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if(baseMdnList!=null&&!baseMdnList.isEmpty()){
                //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
                if (persisterTxn != null) {
                    knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                    conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                    knLogger.debug(methodName, "if block");
                } else {
                    conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                    ownedTxn = true;
                    knLogger.debug(methodName, "else block");
                    knLogger.debug(methodName, "conn", conn);
                }
                query = queryMapper.getQuery(PROFILE_MDN_COUNT_BY_BASE_MDN);
                pstmt = conn.prepareStatement(query);
                for(String baseMdn:baseMdnList){
                    pstmt.setString(1, baseMdn);
                    rs = pstmt.executeQuery();
                    List<String> profileMdnList=new ArrayList<>();
                    while (rs.next()) {
                        profileMdnList.add(rs.getString("MDN").trim());
                    }
                    baseNprofileMdnMap.put(baseMdn,profileMdnList);
                }
                knLogger.debug(methodName,"baseNprofileMdnMap :",KnGDPRTemplate.mapKeyMdn(baseNprofileMdnMap));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getAuthUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return baseNprofileMdnMap;
    }

    public Map<String, Integer> getPrivacyOptStatus(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPrivacyOptStatus()";
        knLogger.debug(methodName, " mdnList : ", mdnList);
        Connection conn;
        PreparedStatement stmt = null;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        Map<String, Integer> privOptMdnMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PRIV_OPT_STATUS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            stmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for(String mdn : mdnList){
                stmt.setString(index++,mdn);
            }
            rs = stmt.executeQuery();
            while(rs.next()){
                privOptMdnMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed ",
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        }
        finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXit: privOptMdnMap : ",privOptMdnMap);
        return privOptMdnMap;
    }

    public void insertIntoMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profilemdnsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoMcpttPermInfo(Collection<KnMcpttPermissionDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mcpttMappingDto - ", mcpttMappingDto , "profileMdns -> ",KnGDPRTemplate.mdnList(profilemdnsList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_AUTH_USER_PERMISSIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(String profileMdn : profilemdnsList){
                knLogger.debug(" profile mdns ",profileMdn);
                for (KnMcpttPermissionDTO targetMapping : mcpttMappingDto) {
                        pstmt.setInt(1, targetMapping.getCorpid());
                        pstmt.setString(2, profileMdn);
                        pstmt.setString(3, targetMapping.getTargetMdn());
                        pstmt.setLong(4, targetMapping.getMcpttPerms());
                        if (targetMapping.getDiscreteEnabled() != null) {
                            pstmt.setInt(5, targetMapping.getDiscreteEnabled());
                        } else {
                            pstmt.setNull(5, Types.INTEGER);
                        }
                        if(targetMapping.getCommonAu() != null && targetMapping.getCommonAu() == 1) {
                            pstmt.setInt(6, targetMapping.getCommonAu());
                        }else{
                            pstmt.setNull(6, Types.INTEGER);
                        }
                        pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateToMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profileMdnsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateToMcpttPermInfo(Collection<KnMcpttPermissionDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mcpttMappingDto - ", mcpttMappingDto," profileMdns -> ",KnGDPRTemplate.mdnList(profileMdnsList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_AUTH_USER_PERMISSIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(String profileMdn : profileMdnsList) {
                for (KnMcpttPermissionDTO targetMapping : mcpttMappingDto) {
                    pstmt.setLong(1, targetMapping.getMcpttPerms());
                    if (targetMapping.getDiscreteEnabled() != null) {
                        pstmt.setInt(2, targetMapping.getDiscreteEnabled());
                    } else {
                        pstmt.setNull(2, Types.INTEGER);
                    }
                    if (targetMapping.getCommonAu() != null) {
                        pstmt.setInt(3, targetMapping.getCommonAu());
                    } else {
                        pstmt.setNull(3, Types.INTEGER);
                    }
                    pstmt.setString(4, profileMdn);
                    pstmt.setString(5, targetMapping.getTargetMdn());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteFromMcpttPermInfoForProfileMdns(List<String> authMdns, Collection<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromMcpttPermInfo(String, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : authMdn - ", KnGDPRTemplate.mdnList(authMdns), "targetMdn: ", targetMdn == null ? targetMdn : KnGDPRTemplate.mdnList(targetMdn));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_AUTH_USER_PERMISSIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(String profileMdn : authMdns) {
                for (String targetMan : targetMdn) {
                    pstmt.setString(1, profileMdn);
                    pstmt.setString(2, targetMan);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete mcptt mapping DG.MCPTT_PERM_INFO table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Integer> getCommonContactInfoFromMcpttPerm(String authMdn, boolean readOnly,
                                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCommonContactInfoFromMcpttPerm(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "authorized mdn :", KnGDPRTemplate.mdn(authMdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<String, Integer> targetMdnCommonInfos = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_AUTH_USER_PERMISSIONS);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, authMdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while(rs.next()){
                targetMdnCommonInfos.put(rs.getString(3).trim(), rs.getInt(6));
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getTargetUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.exit(methodName);
        return targetMdnCommonInfos;
    }

    public Map<String,List<String>> getAuAndCommonTuMapping(List<String> authMdns, List<String> targetMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCommonContactsForTheListOfAUs(KnIPAuthUserPermissionInfoDTO)";
        knLogger.debug(methodName, "authorized mdns :", KnGDPRTemplate.mdnList(authMdns), "target mdns ", KnGDPRTemplate.mdnList(targetMdns));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String,List<String>> targetMdnCommonInfos = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_COMMON_CONTACTS);
            knLogger.debug(methodName, "Executing query - ", query);
            String mdnListString = formCommaSeperatedIdList(authMdns);
            query = replaceContactWithValue(query, MDNLIST, mdnListString);
            String mdnListStringTarget = formCommaSeperatedIdList(targetMdns);
            query = replaceContactWithValue(query, TARGETMDNLIST, mdnListStringTarget);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, COMMON_CONTACT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (targetMdnCommonInfos.containsKey(rs.getString(1))) {
                    targetMdnCommonInfos.get(rs.getString(1)).add(rs.getString(2));
                } else {
                    targetMdnCommonInfos.put(rs.getString(1), new ArrayList<>());
                    targetMdnCommonInfos.get(rs.getString(1)).add(rs.getString(2));
                }
            }
            knLogger.debug(methodName, "EXit: Query executed successfully ");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getTargetUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return targetMdnCommonInfos;
    }

    public Map<String, List<KnMcpttPermissionDTO>> getTargetMdnListPermissions(List<String> authorizedMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTargetMdnListPermissions(KnIPAuthUserPermissionInfoDTO)";
        knLogger.debug(methodName, "authorized mdns :", KnGDPRTemplate.mdnList(authorizedMdns));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        KnMcpttPermissionDTO permissionDTO = null;
        Map<String,List<KnMcpttPermissionDTO>> permisisonsInfo = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_TARGET_MDNLIST_PERMISSIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            String mdnListString = formCommaSeperatedIdList(authorizedMdns);
            query = replaceContactWithValue(query, MDNLIST, mdnListString);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                permissionDTO = new KnMcpttPermissionDTO();
                String authMdn = (rs.getString(2).trim());
                permissionDTO.setCorpid(rs.getInt(1));
                permissionDTO.setAuthMdn(authMdn);
                permissionDTO.setTargetMdn(rs.getString(3).trim());
                permissionDTO.setMcpttPerms(rs.getLong(4));
                permissionDTO.setDiscreteEnabled((Integer) rs.getObject(DISCREET_ENABLED));
                permissionDTO.setCommonAu(rs.getInt(6));
                if(!permisisonsInfo.containsKey(authMdn)){
                    permisisonsInfo.put(authMdn,new ArrayList<>());
                    permisisonsInfo.get(authMdn).add(permissionDTO);
                }else{
                    permisisonsInfo.get(authMdn).add(permissionDTO);
                }
            }
            knLogger.debug(methodName, "EXit: Query executed successfully ");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getTargetMdnListPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return permisisonsInfo;
    }

    public List<String> getProfileMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "DAO.getProfileMdns()";
        knLogger.info(methodName, "Entry mdnList", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        boolean ownedTxn = false;
        ResultSet rs = null;
        List<String> profileMdnList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if (mdnList != null && !mdnList.isEmpty()) {
                //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                if (persisterTxn != null) {
                    knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                    conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                } else {
                    conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                    ownedTxn = true;
                }
                query = queryMapper.getQuery(GET_PROFILEMDNS);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, "MDNLIST");
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing query - ", query);
                for (String mdn : mdnList) {
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    profileMdnList.add(rs.getString("MDN").trim());
                }
                knLogger.debug(methodName, "Exit profileMdnList :", KnGDPRTemplate.mdnList(profileMdnList));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getAuthUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if(ownedTxn){
                KnDbUtil.closeConnection(conn);
            }
        }
        return profileMdnList;
    }

    public Map<String, Integer> getPaginatedAuthUserList(List<String> targetMdnList,int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPaginatedAuthUserList()";
        knLogger.debug(methodName, "getTargetUserPermissions", KnGDPRTemplate.mdnList(targetMdnList));
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> authMdnMap = new TreeMap<>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_PAGINATED_AUTH_MDN_INFO);
            query = KnCorpUtil.replaceContactWithValue(query, "TARGETMDNLIST", formCommaSeperatedIdList(targetMdnList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, startIndex);
            pstmt.setInt(2, endIndex);
            rs = pstmt.executeQuery();
            while(rs.next()){
                authMdnMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXit: Query executed successfully",authMdnMap);
        } catch (SQLException e) {
            if(ownedTxn){
                knLogger.debug(methodName, "rolling back the open transaction");
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while getTargetUserPermissions",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_AUTH_USER_PERMISSIONS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return authMdnMap;
    }

    public Map<String, Integer> getPaginatedDestinationOwnerMdns(List<String> destinationMdnList, int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPaginatedDestinationOwnerMdns()";
        knLogger.debug(methodName, "getEmergUserDestMap", KnGDPRTemplate.mdnList(destinationMdnList));
        Connection conn= null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<String, Integer> destOwerMap = new TreeMap<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_PAGINATED_DEST_OWNER_MDN_INFO);
            query = replaceContactWithValue(query, EMERGDESTLIST, formCommaSeperatedIdList(destinationMdnList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, startIndex);
            pstmt.setInt(2, endIndex);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while(rs.next()){
                destOwerMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while getEmergUserDestMap",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EMERGENCY_ATTRIBUTES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return destOwerMap;
    }
}


