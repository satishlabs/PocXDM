/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupInfoDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 29, 2011      7.0
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
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GROUPCORPIDMAP;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;


public class KnCorpGroupInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupInfoDAO.class);

    private static final String CLASS = KnCorpGroupInfoDAO.class.getName();

    private String pttServerId;

    KnCorpGroupInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        try {
            if (persistenceDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistenceDTO;
                Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(KnPersisterConstants.INSERT_GROUP_INFO);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, groupPersistDTO.getGroupId());
                pstmt.setInt(2, groupPersistDTO.getCorpId());
                //multilingual revert change
                if(null != groupPersistDTO.getGroupDisplayName())
                {
                	try {
						pstmt.setString(3, new String(groupPersistDTO.getGroupDisplayName().getBytes("UTF-8"),"8859_1"));
					} catch (UnsupportedEncodingException e) {
						knLogger.error(methodName, "UTF-8 encoding exception - ", groupPersistDTO.getGroupDisplayName(), e);
					}
                }
                pstmt.setInt(4, groupPersistDTO.getGroupMemberListId());
                pstmt.setInt(5, groupPersistDTO.getGroupDistPolicy());
                pstmt.setInt(6, groupPersistDTO.getETag());
                pstmt.setLong(7, System.currentTimeMillis());
                pstmt.setLong(8, KnCorpUtil.mappGroupTypeToDB(groupPersistDTO.getGroupType()));
                pstmt.setString(9, groupPersistDTO.getPocHome());
                pstmt.setInt(10, groupPersistDTO.getOverrideDnd());
                if(groupPersistDTO.getAvatar()!=null)
                    pstmt.setInt(11, groupPersistDTO.getAvatar());
                else
                    pstmt.setNull(11, java.sql.Types.INTEGER);
                pstmt.setInt(12, groupPersistDTO.getLmrInteropCapable());
                if(groupPersistDTO.getEmergAutoFloorTimer() != null && groupPersistDTO.getEmergAutoFloorTimer() != 0) {
                    pstmt.setInt(13, groupPersistDTO.getEmergAutoFloorTimer());
                } else {
                    pstmt.setNull(13, java.sql.Types.INTEGER);
                }
                if(groupPersistDTO.getEmergHangTimeAddOn() != null && groupPersistDTO.getEmergHangTimeAddOn() != 0) {
                    pstmt.setInt(14, groupPersistDTO.getEmergHangTimeAddOn());
                } else {
                    pstmt.setNull(14, java.sql.Types.INTEGER);
                }
                if(groupPersistDTO.getEmergOverrideDND() != null && groupPersistDTO.getEmergOverrideDND() != 0) {
                    pstmt.setInt(15, groupPersistDTO.getEmergOverrideDND());
                } else {
                    pstmt.setNull(15, java.sql.Types.INTEGER);
                }
                if(groupPersistDTO.getHangTimeOut() != null && groupPersistDTO.getHangTimeOut() != 0) {
                    pstmt.setInt(16, groupPersistDTO.getHangTimeOut());
                } else {
                    pstmt.setNull(16, java.sql.Types.INTEGER);
                }
                pstmt.setInt(17, groupPersistDTO.getGroupCreatedBy());
                pstmt.setString(18, groupPersistDTO.getTpGroupOwner());
                if (groupPersistDTO.isLargeGroup() && (groupPersistDTO.getMcxGrpInd() == null || groupPersistDTO.getMcxGrpInd()
                        != KnConstants.MCX_GROUP_INDICATOR)) {
                    pstmt.setInt(19, 1);
                }else if ( groupPersistDTO.getMcxGrpInd() != null &&  groupPersistDTO.getMcxGrpInd() == KnConstants.MCX_GROUP_INDICATOR){
                    pstmt.setInt(19, 2);
                }else {
                    pstmt.setInt(19, 0);
                }
                if((groupPersistDTO.getOSMListId()!=null)&&!groupPersistDTO.getOSMListId().isEmpty()){
                    pstmt.setInt(20,
                            Integer.parseInt(groupPersistDTO.getOSMListId()));
                }else{
                    pstmt.setNull(20,java.sql.Types.INTEGER);
                }

                if((groupPersistDTO.getGroupServiceType()!=null)){
                    pstmt.setInt(21,groupPersistDTO.getGroupServiceType());
                }else{
                    pstmt.setNull(21,java.sql.Types.INTEGER);
                }
                if((groupPersistDTO.getFeatureAllowed()!=null)){
                    pstmt.setInt(22,groupPersistDTO.getFeatureAllowed());
                }else{
                    pstmt.setNull(22,java.sql.Types.INTEGER);
                }
                pstmt.setString(23, groupPersistDTO.getGroupProfileId());

                if((groupPersistDTO.getAudioCutIn()!=null)){
                    pstmt.setInt(24,groupPersistDTO.getAudioCutIn());
                }else{
                    pstmt.setNull(24,java.sql.Types.INTEGER);
                }

                if((groupPersistDTO.getGrpShared()!=null)){
                    pstmt.setInt(25,groupPersistDTO.getGrpShared());
                }else{
                    pstmt.setNull(25,java.sql.Types.INTEGER);
                }

                if((groupPersistDTO.getIsPreConfiguredGroup()!=null)){
                    pstmt.setInt(26,groupPersistDTO.getIsPreConfiguredGroup());
                }else{
                    pstmt.setNull(26,java.sql.Types.INTEGER);
                }

                if(groupPersistDTO.getUgwInterop() != null){
                    pstmt.setInt(27,groupPersistDTO.getUgwInterop());
                }else{
                    pstmt.setNull(27, Types.INTEGER);
                }

                if(groupPersistDTO.getAuthorizedLargeTG() != null){
                    pstmt.setInt(28,groupPersistDTO.getAuthorizedLargeTG());
                }else{
                    pstmt.setInt(28, 1);
                }

                if (groupPersistDTO.getClusterId() != null){
                    pstmt.setInt(29, groupPersistDTO.getClusterId());
                } else {
                    pstmt.setNull(29, Types.INTEGER);
                }


                if(groupPersistDTO.getHierarchyId() != null){
                    pstmt.setString(30, groupPersistDTO.getHierarchyId());
                }else {
                    pstmt.setNull(30, Types.VARCHAR);
                }


               if (groupPersistDTO.getVideoPermission() != null) {
                    pstmt.setInt(31, groupPersistDTO.getVideoPermission());
                } else {
                    pstmt.setInt(31, com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }

                if(groupPersistDTO.getHierarchyRoot() != null){
                    pstmt.setString(32, groupPersistDTO.getHierarchyRoot());
                }else {
                    pstmt.setNull(32, Types.VARCHAR);
                }

                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
                knLogger.debug( methodName, "EXIT: Query executed successfully.");
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while inserting into the group info table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
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

    /**
     * returns the group information from dg.corpgroupinfo table.
     * @param groupId
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpGroupInfoPersistDTO selectGroupInfo(int groupId, int corpId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall)
            throws KnDAOException , KnXDMServerException {
        String methodName = "selectGroupInfo(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId, " , corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        KnCorpGroupInfoPersistDTO groupPersistDTO = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if(!hiearchyCall){
            query = queryMapper.getQuery(GET_GROUP_BASIC_INFO);}
            else{
                query="SELECT  GI.GROUPDISPLAYNAME,GI.GROUPMEMBERLISTID, GI.GROUPDISTRIBUTIONPOLICY, GMC.MEMBERCOUNT, GI.ETAG, GI.GROUPTYPE, GI.OVERRIDE_PR, GI.AVATAR_ID, GI.EMERGAUTOFLOORTIMER, GI.EMERGHANGTIMERADDON, GI.EMERGDNDOVERRIDE, GI.HANGTIMEOUT, GI.GROUP_CREATED_BY, GI.GROUP_OWNER, GI.IS_LARGEGROUP, GI.OSMLISTID,GI.AUDIO_CUTIN,GI.GRP_SVC_TYPE,GI.GRP_PROFILE_ID,GI.POCHOME, GI.GROUP_SHARED, GI.IS_PRECONFIG_GRP , GI.CLUSTERID FROM DG.CORPGROUPINFO GI LEFT JOIN DG.CORPGROUPMEMBERCOUNT GMC ON GI.CORPGROUPID = GMC.CORPGROUPID WHERE GI.CORPGROUPID=? ;";
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            if(!hiearchyCall){
            pstmt.setInt(2, corpId);}
            knLogger.debug( methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(groupId);
                groupPersistDTO.setCorpId(corpId);
                //multilingual revert changes
                if(null != rs.getString(1))
                {
                	try {
                		groupPersistDTO.setGroupDisplayName(new String(rs.getString(1).trim().getBytes("8859_1"),"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(1), e);
					}
                }
                groupPersistDTO.setGroupMemberListId(rs.getInt(2));
                groupPersistDTO.setGroupDistPolicy(rs.getInt(3));
                groupPersistDTO.setGroupMemberCount(rs.getInt(4));
                groupPersistDTO.setETag(rs.getInt(5));
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(6)));
                groupPersistDTO.setOverrideDnd(rs.getInt(7));
                groupPersistDTO.setAvatar((Integer) rs.getObject(8));
                groupPersistDTO.setEmergAutoFloorTimer((Integer) rs.getObject(9));
                groupPersistDTO.setEmergHangTimeAddOn((Integer) rs.getObject(10));
                groupPersistDTO.setEmergOverrideDND((Integer) rs.getObject(11));
                groupPersistDTO.setHangTimeOut((Integer) rs.getObject(12));
                groupPersistDTO.setGroupCreatedBy(rs.getInt(13));
                groupPersistDTO.setTpGroupOwner(this.trimIfNotNull(rs.getString(14)));
                if(rs.getInt(15) == 1){
                    groupPersistDTO.setLargeGroup(true);
                }
                if(rs.getInt(15)==2) {
                	groupPersistDTO.setMcxGrpInd(1);
                }else {
                	groupPersistDTO.setMcxGrpInd(0);
                }
                groupPersistDTO.setOSMListId(String.valueOf(rs.getInt(16)));
                groupPersistDTO.setAudioCutIn((Integer)rs.getObject("AUDIO_CUTIN"));
                groupPersistDTO.setGroupServiceType((Integer)rs.getObject("GRP_SVC_TYPE"));
                groupPersistDTO.setGroupProfileId(rs.getString("GRP_PROFILE_ID"));
                groupPersistDTO.setGrpShared((Integer)rs.getObject("GROUP_SHARED"));
                //Added by DTXJ47 MCSJAVALIB-1577
                groupPersistDTO.setPocHome(rs.getString(20));
                groupPersistDTO.setIsPreConfiguredGroup((Integer)rs.getObject("IS_PRECONFIG_GRP"));
                Object clusterObj = rs.getObject("CLUSTERID");
                if (clusterObj != null) {
                    groupPersistDTO.setClusterId((Integer) clusterObj);
                }
            } else {
                // Check if group exists in shared corporate list
                query = "SELECT COUNT(*) FROM DG.CORPGRP_SHAREDLIST WHERE CORPGROUPID = ? AND SHAREDCORPID = ?";
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(1, groupId);
                preparedStatement.setInt(2, corpId);

                knLogger.debug(methodName, "Executing query -", "'", query, "'");
                resultSet = preparedStatement.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    knLogger.error(methodName, "Group deletion is not allowed from the shared corporate. GroupId - ", groupId);
                    throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DELETETION_IS_NOT_ALLOWED_FOR_SHARED_CORPORATE,
                            "Group deletion is not allowed from the shared corporate");

                } else {
                    knLogger.error(methodName, "Group does not exists. GroupId - ", groupId);
                    throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                            "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
                }
            }

            if(clientIntf != AREA_BASED_DYNAMIC_GROUP && groupPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()){
                knLogger.error( methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                        "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            knLogger.debug( methodName, "Exit: GroupInfo - ", groupPersistDTO);
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Group deletion is not allowed from the shared corporate. GroupId - ", groupId);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DELETETION_IS_NOT_ALLOWED_FOR_SHARED_CORPORATE,
                    "Group deletion is not allowed from the shared corporate");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            if (resultSet != null) {
                KnDbUtil.closeResultSet(resultSet);
            }
            if (preparedStatement != null) {
                KnDbUtil.closePreparedStatement(preparedStatement);
            }
            knLogger.debug(methodName, "EXIT :GroupInfo - ", groupPersistDTO);
        }
        return groupPersistDTO;
    }

    //returns null if no group exists with provided groupName, groupDisplayName
    public KnCorpGroupInfoPersistDTO selectGroupNameInfo(int corpId, String groupName, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        String methodName = "selectGroupNameInfo(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , groupName - ", groupName);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpGroupInfoPersistDTO groupPersistDTO = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
//            query = "SELECT GROUPDISPLAYNAME FROM DG.CORPGROUPINFO WHERE CORPID=? AND (GROUPDISPLAYNAME=?)";
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_INFO_BY_NAME);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            try {
            	if(groupName != null)
            		groupName=new String(groupName.getBytes("UTF-8"),"8859_1");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            pstmt.setString(2, groupName);
            knLogger.debug( methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setCorpId(corpId);
                //multilingual revert changes
                if(null != rs.getString(1))
                {
                	try {
						groupPersistDTO.setGroupDisplayName(new String(rs.getString(1).trim().getBytes("8859_1"),"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(1), e);
					}
                }
            }
            knLogger.debug( methodName, "Corporate grouInfo - ", groupPersistDTO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to selectGroupNameInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupPersistDTO;
    }

    public void updateGroupMemberListId(int groupId, int corpSublistId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateGroupMemberListId(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId, " ,corpSublistId - ", corpSublistId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            //query = "UPDATE DG.CorpGroupInfo SET GroupMemberListId = ? WHERE CorpGroupId = ?";
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_PRIVAT_LIST_ID);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpSublistId);
            pstmt.setInt(2, groupId);
            knLogger.debug( methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the groupInfo-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updating the groupInfo- ", e);
            throw KnDbUtil.processException(e, "Failed while updating the groupInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT: Query executed successfully.");
        }
    }

    public int getCorpGroupCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupCount(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //query = "SELECT COUNT(CorpGroupId) FROM DG.CorpGroupInfo WHERE CorpId = ?";
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORP_GROUP_COUNT);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.debug( methodName, "No of groups the corporate has - ", count);
            return count;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the group count for the corporate -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group count for the corporate - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count for the corporate -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT: No of groups the corporate has - ", count);
        }
    }

    public int getGroupPrivateListId(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupPrivateListId(int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : groupId - ", groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int sublistId = 0;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PRIVATE_LIST_OF_GROUP);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                sublistId = rs.getInt(1);
            }
            return sublistId;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the group private List Id -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group private List Id - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group private List Id -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT: Private List Id for Group - ", sublistId);
        }
    }

    public KnCorpGroupInfoPersistDTO selectGroupBasicInfo(int groupId, int corpId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException {
        String methodName = "selectGroupBasicInfo(int, int, int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : groupId - ", groupId, " , corpId - ", corpId);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpGroupInfoPersistDTO groupPersistDTO = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if(!hiearchyCall) {
                query = queryMapper.getQuery(GET_GROUP_BASIC_INFO_DETAILS);
            }else{
                query="SELECT GI.GROUPDISPLAYNAME, GI.GROUPMEMBERLISTID, GI.ETAG, GI.GROUPTYPE, GI.AVATAR_ID, GI.LMR_INTEROP_CAPABLE, GI.GROUP_OWNER, GI.GROUP_CREATED_BY, GI.IS_LARGEGROUP ,GI.GRP_PROFILE_ID, GI.GROUP_SHARED, GI.OVERRIDE_PR ,GI.IS_PRECONFIG_GRP,GI.UGWINTEROP, GI.RECORDING_FS, GI.AUTHORIZED_LARGE_TG FROM DG.CORPGROUPINFO GI WHERE GI.CORPGROUPID=?;";
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            if(!hiearchyCall){
            pstmt.setInt(2, corpId);}
            knLogger.debug( methodName, "Executing query -", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(groupId);
                groupPersistDTO.setCorpId(corpId);
                groupPersistDTO.setGrpOwnerCorpId(String.valueOf(corpId));
                //multilingual revert changes
                if(null != rs.getString(1))
                {
                	try {
						groupPersistDTO.setGroupDisplayName(new String(rs.getString(1).trim().getBytes("8859_1"),"UTF-8"));
					} catch (Exception e) {
						knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(1), e);
					}
                }
                groupPersistDTO.setGroupMemberListId(rs.getInt(2));
                groupPersistDTO.setETag(rs.getInt(3));
//              int groupType = rs.getInt(4);
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setLmrInteropCapable(rs.getInt(6));
                if(null != rs.getString(7)) {
                    groupPersistDTO.setTpGroupOwner(rs.getString(7).trim());
                }
                groupPersistDTO.setGroupCreatedBy(rs.getInt(8));
                if(rs.getInt(9) == 1){
                    groupPersistDTO.setLargeGroup(true);
                }
                if(rs.getInt(9) == 2){
                    groupPersistDTO.setMcxGrpInd(1);
                }else {
                	groupPersistDTO.setMcxGrpInd(0);
                }

                groupPersistDTO.setGroupProfileId(rs.getString(10));
                groupPersistDTO.setGrpShared((Integer) rs.getObject(11));
                groupPersistDTO.setOverrideDnd(rs.getInt(12));
                groupPersistDTO.setIsPreConfiguredGroup(rs.getInt(13));
                groupPersistDTO.setUgwInterop(null == rs.getObject(14) ? null : (Integer) rs.getObject(14));
                groupPersistDTO.setRecordingFs(String.valueOf(rs.getInt(15)));
                Object authorizedLargeTG = rs.getObject(16);
                groupPersistDTO.setAuthorizedLargeTG(authorizedLargeTG != null ?
                        (Integer) authorizedLargeTG : KnConstants.DEFAULT_AUTHORIZED_LARGE_TG_VALUE);
                if (!hiearchyCall) {
                    groupPersistDTO.setHierarchyId(rs.getString("HIERARCHY_ID"));
                }
            } else {
                knLogger.error( methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND, "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            if(clientIntf != AREA_BASED_DYNAMIC_GROUP && groupPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()){
                knLogger.error( methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                        "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            return groupPersistDTO;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while retrieving the group Info-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occurred while retrieving the group Info- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT :GroupInfo - ", groupPersistDTO);
        }
    }
    public KnCorpGroupInfoPersistDTO getGroupBasicInfoDetailsWithoutCorpId(int groupId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException {
        String methodName = "getGroupBasicInfoDetailsWithoutCorpId(int, int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : groupId - ", groupId);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpGroupInfoPersistDTO groupPersistDTO = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query="SELECT GI.GROUPDISPLAYNAME, GI.GROUPMEMBERLISTID, GI.ETAG, GI.GROUPTYPE, GI.AVATAR_ID, GI.LMR_INTEROP_CAPABLE, GI.GROUP_OWNER, GI.GROUP_CREATED_BY, GI.IS_LARGEGROUP ,GI.GRP_PROFILE_ID, GI.GROUP_SHARED, GI.OVERRIDE_PR ,GI.IS_PRECONFIG_GRP,GI.CORPID,GI.UGWINTEROP FROM DG.CORPGROUPINFO GI WHERE GI.CORPGROUPID=?;";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query -", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(groupId);
                //multilingual revert changes
                if(null != rs.getString(1))
                {
                	try {
						groupPersistDTO.setGroupDisplayName(new String(rs.getString(1).trim().getBytes("8859_1"),"UTF-8"));
					} catch (Exception e) {
						knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(1), e);
					}
                }
                groupPersistDTO.setGroupMemberListId(rs.getInt(2));
                groupPersistDTO.setETag(rs.getInt(3));
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setLmrInteropCapable(rs.getInt(6));
                if(null != rs.getString(7)) {
                    groupPersistDTO.setTpGroupOwner(rs.getString(7).trim());
                }
                groupPersistDTO.setGroupCreatedBy(rs.getInt(8));
                if(rs.getInt(9) == 1){
                    groupPersistDTO.setLargeGroup(true);
                }
                if(rs.getInt(9) == 2){
                    groupPersistDTO.setMcxGrpInd(1);
                }else {
                	groupPersistDTO.setMcxGrpInd(0);
                }

                groupPersistDTO.setGroupProfileId(rs.getString(10));
                groupPersistDTO.setGrpShared((Integer) rs.getObject(11));
                groupPersistDTO.setOverrideDnd(rs.getInt(12));
                groupPersistDTO.setIsPreConfiguredGroup(rs.getInt(13));
                int corpId  = rs.getInt(14);
                groupPersistDTO.setCorpId(corpId);
                groupPersistDTO.setGrpOwnerCorpId(String.valueOf(corpId));
                groupPersistDTO.setUgwInterop(null == rs.getObject(15) ? null : (Integer) rs.getObject(15));

            } else {
                knLogger.error( methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND, "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            if(clientIntf != AREA_BASED_DYNAMIC_GROUP && groupPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()){
                knLogger.error( methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                        "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            return groupPersistDTO;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the group Info-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while retrieving the group Info- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT :GroupInfo - ", groupPersistDTO);
        }
    }

    public Collection<Integer> getOwnerGroupIds(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOwnerGroupIds(int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<Integer> groupIds = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_OWNERS_GROUP_IDS);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, KnConstants.CREATED_BY.ABDG.value());
            pstmt.setString(3, mdn);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
            return groupIds;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group Ids - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group private List Id -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT: Private List Id for Group - ", groupIds);
        }
    }

    public Collection<Integer> getOwnerGroupIds(List<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOwnerGroupIds(int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index=3;
        Collection<Integer> groupIds = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "SELECT CORPGROUPID FROM DG.CORPGROUPINFO WHERE CORPID = ? AND GROUP_CREATED_BY = ? AND GROUP_OWNER IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, KnConstants.CREATED_BY.ABDG.value());
            for(String mdn : mdnList){
                pstmt.setString(index++, mdn);
            }
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
            return groupIds;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group Ids - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group private List Id -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT: Private List Id for Group - ", groupIds);
        }
    }
    
    private String trimIfNotNull(String input) {
    	if (input != null) {
    		input = input.trim();    		
    	}
    	return input;
    }

    public List<String> getExistingGroupName(List<String> grpNameList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExistingGroupName()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , grpNameList - ", grpNameList);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> existingNameList = new ArrayList<>();
        try {
            //String groupName=new String("".getBytes("UTF-8"),"8859_1");
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_NAMELIST_BY_NAMES);
            query = replaceContactWithValue(query, GROUP_NAMES, KnCorpUtil.formCommaSeperatedIdListQuotes(grpNameList));
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");

            while (rs.next()) {
                try {
                    existingNameList.add(new String((rs.getString(1)).getBytes("8859_1"),"UTF-8").trim());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            knLogger.debug( methodName, "existingNameList - ", existingNameList);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to selectGroupNameInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return existingNameList;
    }

    public void createBulkGroupInfoDetails(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "createBulkGroupInfoDetails()";
        knLogger.debug(methodName, "ENTRY : corpId - ", groupInfoPersistDTOList.get(0).getCorpId(), " , hierarchyId - ", groupInfoPersistDTOList.get(0).getHierarchyId());
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.INSERT_GROUP_INFO_BULK);
            pstmt = conn.prepareStatement(query);
            for(KnCorpGroupInfoPersistDTO groupPersistDTO: groupInfoPersistDTOList){
                pstmt.setInt(1, groupPersistDTO.getGroupId());
                pstmt.setInt(2, groupPersistDTO.getCorpId());
                //multilingual revert change
                if (null != groupPersistDTO.getGroupDisplayName()) {
                    try {
                        pstmt.setString(3, new String(groupPersistDTO.getGroupDisplayName().getBytes("UTF-8"), "8859_1"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", groupPersistDTO.getGroupDisplayName(), e);
                    }
                }
                pstmt.setInt(4, groupPersistDTO.getGroupMemberListId());
                pstmt.setInt(5, groupPersistDTO.getGroupDistPolicy());
                pstmt.setInt(6, groupPersistDTO.getETag());
                pstmt.setLong(7, System.currentTimeMillis());
                pstmt.setLong(8, KnCorpUtil.mappGroupTypeToDB(groupPersistDTO.getGroupType()));
                pstmt.setString(9, groupPersistDTO.getPocHome());
                pstmt.setInt(10, groupPersistDTO.getOverrideDnd());
                if (groupPersistDTO.getAvatar() != null)
                    pstmt.setInt(11, groupPersistDTO.getAvatar());
                else
                    pstmt.setNull(11, java.sql.Types.INTEGER);
                pstmt.setInt(12, groupPersistDTO.getLmrInteropCapable());
                if (groupPersistDTO.getEmergAutoFloorTimer() != null && groupPersistDTO.getEmergAutoFloorTimer() != 0) {
                    pstmt.setInt(13, groupPersistDTO.getEmergAutoFloorTimer());
                } else {
                    pstmt.setNull(13, java.sql.Types.INTEGER);
                }
                if (groupPersistDTO.getEmergHangTimeAddOn() != null && groupPersistDTO.getEmergHangTimeAddOn() != 0) {
                    pstmt.setInt(14, groupPersistDTO.getEmergHangTimeAddOn());
                } else {
                    pstmt.setNull(14, java.sql.Types.INTEGER);
                }
                if (groupPersistDTO.getEmergOverrideDND() != null && groupPersistDTO.getEmergOverrideDND() != 0) {
                    pstmt.setInt(15, groupPersistDTO.getEmergOverrideDND());
                } else {
                    pstmt.setNull(15, java.sql.Types.INTEGER);
                }
                if (groupPersistDTO.getHangTimeOut() != null && groupPersistDTO.getHangTimeOut() != 0) {
                    pstmt.setInt(16, groupPersistDTO.getHangTimeOut());
                } else {
                    pstmt.setNull(16, java.sql.Types.INTEGER);
                }
                pstmt.setInt(17, groupPersistDTO.getGroupCreatedBy());
                pstmt.setString(18, groupPersistDTO.getTpGroupOwner());
                if (groupPersistDTO.isLargeGroup()) {
                    pstmt.setInt(19, 1);
                } else if (groupPersistDTO.getMcxGrpInd() != null && groupPersistDTO.getMcxGrpInd() == KnConstants.MCX_GROUP_INDICATOR) {
                    pstmt.setInt(19, 2);
                } else {
                    pstmt.setInt(19, 0);
                }
                if ((groupPersistDTO.getOSMListId() != null) && !groupPersistDTO.getOSMListId().isEmpty()) {
                    pstmt.setInt(20,
                            Integer.parseInt(groupPersistDTO.getOSMListId()));
                } else {
                    pstmt.setNull(20, java.sql.Types.INTEGER);
                }
                if(null != groupPersistDTO.getGroupServiceType()){
                    pstmt.setInt(21, groupPersistDTO.getGroupServiceType());
                }else {
                    pstmt.setInt(21, KnConstants.CORP_GROUP_DEFAULT_SERVICE_TYPE);
                }
                if(null != groupPersistDTO.getFeatureAllowed()){
                    pstmt.setInt(22, groupPersistDTO.getFeatureAllowed());
                }else {
                    pstmt.setInt(22, KnConstants.CORP_GROUP_DEFAULT_ALLOWED_FEATURE);
                }

                pstmt.setString(23, groupPersistDTO.getGroupProfileId());

                if (groupPersistDTO.getAudioCutIn() != null) {
                    pstmt.setInt(24, groupPersistDTO.getAudioCutIn());
                } else {
                    pstmt.setNull(24, java.sql.Types.INTEGER);
                }

                if (groupPersistDTO.getGrpShared() != null) {
                    pstmt.setInt(25, groupPersistDTO.getGrpShared());
                } else {
                    pstmt.setNull(25, java.sql.Types.INTEGER);
                }
                if (groupPersistDTO.getUgwInterop() != null) {
                    pstmt.setInt(26, groupPersistDTO.getUgwInterop());
                } else {
                    pstmt.setNull(26, java.sql.Types.INTEGER);
                }
                if(groupPersistDTO.getClusterId() != null){
                    pstmt.setInt(27, groupPersistDTO.getClusterId());
                } else {
                    pstmt.setNull(27, java.sql.Types.INTEGER);
                }
                if(groupPersistDTO.getHierarchyId()!=null){
                    pstmt.setString(28,groupPersistDTO.getHierarchyId());
                }else {
                    pstmt.setNull(28, java.sql.Types.VARCHAR);
                }
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
            //}
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while inserting into the group info table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<KnCorpGroupInfoPersistDTO> selectProfileGroupList(KnIPCorpGroupProfileDTO groupProfileDTO, int
            maxMemPerGroup, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException{
        final String methodName = "selectProfileGroupList(int, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupProfileDTO - ", groupProfileDTO, ", maxMemPerGroup - ", maxMemPerGroup);
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if(groupProfileDTO.getStartIndex() == null || groupProfileDTO.getFetchSize() == null){
                query = queryMapper.getQuery(KnPersisterConstants.GET_PROFILE_GROUP_LIST);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, groupProfileDTO.getCorpId());
                pstmt.setString(2, String.valueOf(groupProfileDTO.getProfileId()));
            }else {
                query = queryMapper.getQuery(KnPersisterConstants.GET_PROFILE_GROUP_LIST_PAGINATED);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, groupProfileDTO.getStartIndex());
                int endIndex = groupProfileDTO.getStartIndex() + groupProfileDTO.getFetchSize() - 1;
                pstmt.setInt(2, endIndex);
                pstmt.setInt(3, groupProfileDTO.getCorpId());
                pstmt.setString(4, String.valueOf(groupProfileDTO.getProfileId()));
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            List<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                groupPersistDTO.setCorpId(groupProfileDTO.getCorpId());
                //multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                groupPersistDTO.setMemberCount(memCount);
                if (memCount > maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(5));
                groupPersistDTO.setGroupCreatedBy(rs.getInt(6));
                if(rs.getInt(7) == 1){
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if (rs.getInt(7) == 2){
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding the count
                    groupPersistDTO.setMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setGroupProfileId(rs.getString(8));
                groupPersistDTO.setGroupMemberListId(rs.getInt(9));
                groupPersistDTO.setETag(rs.getInt(10));
                groupPersistDTO.setGrpShared(rs.getInt(11));
                groupList.add(groupPersistDTO);
            }
            knLogger.debug(methodName, "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String, Integer> selectGroupCountByGroupProfileId(List<String> groupProfileIds, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupCountByGroupProfileId(List<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "groupProfileIds ", groupProfileIds);
        Map<String, Integer> groupProfileCountsMap = new HashMap<>();
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_PROFILE_COUNT);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(groupProfileIds, 1000);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        	for (Collection<String> splitGroupProfileIdList : collList) {
        		String finalQuery = replaceContactWithValue(query, GROUPPROFILEIDS, KnCorpUtil.formCommaSeperatedIdList(splitGroupProfileIdList));
        		knLogger.debug(methodName, "Executing query - ", finalQuery);
                stmt = conn.prepareStatement(finalQuery);
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                	groupProfileCountsMap.put(rs.getString(2), rs.getInt(1));
                }
        	}

            return groupProfileCountsMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching gorup profile count ." + e, pttServerId,
                    KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(stmt);
            knLogger.debug(methodName, "EXIT : gorup profile count ", groupProfileCountsMap);
        }
    }

    /**
     * returns the group information from dg.corpgroupinfo table.
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpGroupInfoPersistDTO selectGroupInfoByGroupId(int groupId, int clientIntf, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupInfoByGroupId(int, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpGroupInfoPersistDTO groupPersistDTO;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_BASIC_INFO_BY_GROUPID);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(groupId);
                //multilingual revert changes
                if(null != rs.getString(1))
                {
                    try {
                        groupPersistDTO.setGroupDisplayName(new String(rs.getString(1).trim().getBytes("8859_1"),"UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(1), e);
                    }
                }
                groupPersistDTO.setGroupMemberListId(rs.getInt(2));
                groupPersistDTO.setGroupDistPolicy(rs.getInt(3));
                groupPersistDTO.setGroupMemberCount(rs.getInt(4));
                groupPersistDTO.setETag(rs.getInt(5));
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(6)));
                groupPersistDTO.setOverrideDnd(rs.getInt(7));
                groupPersistDTO.setAvatar((Integer) rs.getObject(8));
                groupPersistDTO.setEmergAutoFloorTimer((Integer) rs.getObject(9));
                groupPersistDTO.setEmergHangTimeAddOn((Integer) rs.getObject(10));
                groupPersistDTO.setEmergOverrideDND((Integer) rs.getObject(11));
                groupPersistDTO.setHangTimeOut((Integer) rs.getObject(12));
                groupPersistDTO.setGroupCreatedBy(rs.getInt(13));
                groupPersistDTO.setTpGroupOwner(this.trimIfNotNull(rs.getString(14)));
                if(rs.getInt(15) == 1){
                    groupPersistDTO.setLargeGroup(true);
                }
                if(rs.getInt(15)==2) {
                    groupPersistDTO.setMcxGrpInd(1);
                }else {
                    groupPersistDTO.setMcxGrpInd(0);
                }
                groupPersistDTO.setOSMListId(String.valueOf(rs.getInt(16)));
                groupPersistDTO.setAudioCutIn((Integer)rs.getObject("AUDIO_CUTIN"));
                groupPersistDTO.setGroupServiceType((Integer)rs.getObject("GRP_SVC_TYPE"));
                groupPersistDTO.setGroupProfileId(rs.getString("GRP_PROFILE_ID"));
                groupPersistDTO.setGrpShared((Integer)rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setCorpId(rs.getInt("CORPID"));
                groupPersistDTO.setIsPreConfiguredGroup((Integer)rs.getObject("IS_PRECONFIG_GRP"));
                groupPersistDTO.setUgwInterop((Integer)rs.getObject("UGWINTEROP"));
                groupPersistDTO.setRecordingFs(String.valueOf(rs.getInt("RECORDING_FS")));
                if (null != (rs.getObject("AUTHORIZED_LARGE_TG"))) {
                    groupPersistDTO.setAuthorizedLargeTG(rs.getInt("AUTHORIZED_LARGE_TG"));
                } else {
                    groupPersistDTO.setAuthorizedLargeTG(KnConstants.DEFAULT_AUTHORIZED_LARGE_TG_VALUE);
                }
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    groupPersistDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                    knLogger.debug(methodName, "VIDEO_PERMISSION from DB - groupId:", groupId, ", videoPermission:", groupPersistDTO.getVideoPermission());
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                    knLogger.debug(methodName, "VIDEO_PERMISSION is NULL in DB, setting default - groupId:", groupId, ", default:", KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupPersistDTO.setHierarchyId(rs.getString("HIERARCHY_ID"));
                groupPersistDTO.setOwnerAgencyName(rs.getString("OWNER_AGENCY_NAME").trim());
                groupPersistDTO.setOwnerOrganizationName(null == rs.getString("OWNER_ORGANIZATION_NAME") ? null : rs.getString("OWNER_ORGANIZATION_NAME").trim());
            } else {
                knLogger.error( methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                        "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            if(clientIntf != AREA_BASED_DYNAMIC_GROUP && groupPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()){
                knLogger.error( methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                        "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            knLogger.info(methodName, "Exit: GroupInfo - ", groupPersistDTO);
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupPersistDTO;
    }

    public Map<Integer,KnCorpGroupInfoPersistDTO> getAllPreconfigGroupInfo(int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getAllPreconfigGroupInfo()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer,KnCorpGroupInfoPersistDTO> groupInfoMap=new HashMap<>();
        int preConfigGroup=1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ALL_PRECONFIG_GROUP_IN_CORP);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, preConfigGroup);
            knLogger.debug( methodName, "Executing query -",  query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                int groupId=rs.getInt("CORPGROUPID");
                groupPersistDTO.setGroupId(groupId);
                groupPersistDTO.setCorpId(rs.getInt("CORPID"));
                groupPersistDTO.setETag(rs.getInt("ETAG"));
                groupPersistDTO.setGroupType(rs.getInt("GROUPTYPE"));
                groupPersistDTO.setGroupCreatedBy(rs.getInt("GROUP_CREATED_BY"));
                groupPersistDTO.setTpGroupOwner(this.trimIfNotNull(rs.getString("GROUP_OWNER")));
                if(rs.getInt("IS_LARGEGROUP") == 1){
                    groupPersistDTO.setLargeGroup(true);
                }
                if(rs.getInt("IS_LARGEGROUP")==2) {
                    groupPersistDTO.setMcxGrpInd(1);
                }else {
                    groupPersistDTO.setMcxGrpInd(0);
                }
                groupPersistDTO.setGrpShared((Integer)rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setIsPreConfiguredGroup((Integer)rs.getObject("IS_PRECONFIG_GRP"));

                groupInfoMap.put(groupId,groupPersistDTO);
            }
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "Exit: groupInfoMap - ", groupInfoMap);
        return groupInfoMap;
    }

    /**
     * returns isPreConfigGroup values for list of groupIds
     * @param groupIdList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getPreConfigParamList(List<Integer> groupIdList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getIsPreConfiguredParamList(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdList - ", groupIdList);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<Integer> preConfigParamList = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PRECONFIG_PARAM_LIST);
            String groupIdsList = formIntegerCommaSeperatedIdList(groupIdList);
            query = replaceContactWithValue(query, GROUPIDLIST, groupIdsList);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if(rs!=null){
                while (rs.next()) {

                    if(null==rs.getString(1)){
                        preConfigParamList.add(0);
                    }
                    else{
                        preConfigParamList.add(((Integer)rs.getObject((1))));
                    }

                }
            }
            else {

                knLogger.error( methodName, "GroupIds do not exist. GroupIdList - ", groupIdList);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                        "GroupIds not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            knLogger.debug( methodName, "Exit: preConfigParamList - ", preConfigParamList);
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return preConfigParamList;
    }

    /**
     * returns the group information from dg.corpgroupinfo table without validation
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpGroupInfoPersistDTO getGroupBasicDetails(int groupId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupBasicDetails(int, int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : groupId - ", groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpGroupInfoPersistDTO groupPersistDTO = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = "SELECT GI.GROUPDISPLAYNAME, GI.GROUPMEMBERLISTID, GI.ETAG, GI.GROUPTYPE, GI.AVATAR_ID, GI.LMR_INTEROP_CAPABLE, GI.GROUP_OWNER, GI.GROUP_CREATED_BY, GI.IS_LARGEGROUP ,GI.GRP_PROFILE_ID, GI.GROUP_SHARED, GI.OVERRIDE_PR ,GI.IS_PRECONFIG_GRP,GI.CORPID,GI.UGWINTEROP FROM DG.CORPGROUPINFO GI WHERE GI.CORPGROUPID=?;";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query -", query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(groupId);
                //multilingual revert changes
                if (null != rs.getString(1)) {
                    try {
                        groupPersistDTO.setGroupDisplayName(new String(rs.getString(1).trim().getBytes("8859_1"), "UTF-8"));
                    } catch (Exception e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(1), e);
                    }
                }
                groupPersistDTO.setGroupMemberListId(rs.getInt(2));
                groupPersistDTO.setETag(rs.getInt(3));
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setLmrInteropCapable(rs.getInt(6));
                if (null != rs.getString(7)) {
                    groupPersistDTO.setTpGroupOwner(rs.getString(7).trim());
                }
                groupPersistDTO.setGroupCreatedBy(rs.getInt(8));
                if (rs.getInt(9) == 1) {
                    groupPersistDTO.setLargeGroup(true);
                }
                if (rs.getInt(9) == 2) {
                    groupPersistDTO.setMcxGrpInd(1);
                } else {
                    groupPersistDTO.setMcxGrpInd(0);
                }

                groupPersistDTO.setGroupProfileId(rs.getString(10));
                groupPersistDTO.setGrpShared((Integer) rs.getObject(11));
                groupPersistDTO.setOverrideDnd(rs.getInt(12));
                groupPersistDTO.setIsPreConfiguredGroup(rs.getInt(13));
                int corpId = rs.getInt(14);
                groupPersistDTO.setCorpId(corpId);
                groupPersistDTO.setGrpOwnerCorpId(String.valueOf(corpId));
                groupPersistDTO.setUgwInterop(rs.getInt(15));

            } /*else {
                knLogger.error(methodName, "Group does not exists. GroupId - ", groupId);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND, "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }*/
            return groupPersistDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the group Info-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while retrieving the group Info- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT :GroupInfo - ", groupPersistDTO);
        }
    }
    //TODO:- hiearchyCall need to clarify from naseeba
    public Map<Integer, KnCorpGroupInfoPersistDTO> selectBulkGroupBasicInfo(Map<Integer, Integer> groupCorpMap, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException {
        String methodName = "selectBulkGroupBasicInfo(int, int, int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : groupCorpMap - ", groupCorpMap);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        var resp = new HashMap<Integer, KnCorpGroupInfoPersistDTO>();
        try {
            if(groupCorpMap == null || groupCorpMap.isEmpty())
            {
                knLogger.info( methodName, "GroupCorpMap is empty - ", groupCorpMap);
                return resp;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_BASIC_GROUP_LIST_WITH_CORPID);
            String groupCorpQuestionMark = formCommaSeperatedBulkIntegerQuesMarks(groupCorpMap);
            query = replaceContactWithValue(query, GROUPCORPIDMAP, groupCorpQuestionMark);
            knLogger.debug( methodName, "Executing query -", query);
            pstmt = conn.prepareStatement(query);

            int index = 1;
            for(var entry : groupCorpMap.entrySet()) {
                pstmt.setInt(index++, entry.getKey());
                pstmt.setInt(index++, entry.getValue());
            }


            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO  groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                groupPersistDTO.setCorpId(rs.getInt(2));
                groupPersistDTO.setGrpOwnerCorpId(String.valueOf(rs.getInt(2)));
                //multilingual revert changes
                if(null != rs.getString(3))
                {
                    try {
                        groupPersistDTO.setGroupDisplayName(new String(rs.getString(3).trim().getBytes("8859_1"),"UTF-8"));
                    } catch (Exception e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(3), e);
                    }
                }
                groupPersistDTO.setGroupMemberListId(rs.getInt(4));
                groupPersistDTO.setETag(rs.getInt(5));
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(6)));
                groupPersistDTO.setLmrInteropCapable(rs.getInt(8));
                if(null != rs.getString(9)) {
                    groupPersistDTO.setTpGroupOwner(rs.getString(9).trim());
                }
                groupPersistDTO.setGroupCreatedBy(rs.getInt(10));
                if(rs.getInt(11) == 1){
                    groupPersistDTO.setLargeGroup(true);
                }
                if(rs.getInt(11) == 2){
                    groupPersistDTO.setMcxGrpInd(1);
                }else {
                    groupPersistDTO.setMcxGrpInd(0);
                }

                groupPersistDTO.setGroupProfileId(rs.getString(12));
                groupPersistDTO.setGrpShared((Integer) rs.getObject(13));
                groupPersistDTO.setOverrideDnd(rs.getInt(14));
                groupPersistDTO.setIsPreConfiguredGroup(rs.getInt(15));
                groupPersistDTO.setUgwInterop(null == rs.getObject(16) ? null : (Integer) rs.getObject(16));
                groupPersistDTO.setRecordingFs(String.valueOf(rs.getInt(17)));
                if(clientIntf != AREA_BASED_DYNAMIC_GROUP && groupPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()){
                    knLogger.error( methodName, "Group does not exists. groupCorpMap - ", groupCorpMap);
                    throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                            "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
                }
                resp.put(groupPersistDTO.getGroupId(), groupPersistDTO);
            } /*else {
                knLogger.error( methodName, "Group does not exists. groupCorpMap - ", groupCorpMap);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND, "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }*/

            return resp;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while retrieving the group Info-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occurred while retrieving the group Info- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT :groupCorpMap - ", groupCorpMap);
        }
    }
    //TODO:- hiearchyCall need to clarify from naseeba
    public Map<Integer, KnCorpGroupInfoPersistDTO> getBulkGroupBasicInfoDetailsWithoutCorpId(List<Integer> groupIds, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException {
        String methodName = "getBulkGroupBasicInfoDetailsWithoutCorpId(List<Integer>, int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : groupIds - ", groupIds);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        var resp = new HashMap<Integer, KnCorpGroupInfoPersistDTO>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_BASIC_GROUP_LIST_WITHOUT_CORPID);
            String groupIdsList = formIntegerCommaSeperatedIdList(groupIds);
            query = replaceContactWithValue(query, GROUPIDLIST, groupIdsList);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();


            knLogger.debug( methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                //multilingual revert changes
                if(null != rs.getString(2))
                {
                    try {
                        groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"),"UTF-8"));
                    } catch (Exception e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(2), e);
                    }
                }
                groupPersistDTO.setGroupMemberListId(rs.getInt(3));
                groupPersistDTO.setETag(rs.getInt(4));
                groupPersistDTO.setGroupType(KnCorpUtil.mappGroupTypeToApp(rs.getInt(5)));
                groupPersistDTO.setLmrInteropCapable(rs.getInt(7));
                if(null != rs.getString(8)) {
                    groupPersistDTO.setTpGroupOwner(rs.getString(8).trim());
                }
                groupPersistDTO.setGroupCreatedBy(rs.getInt(9));
                if(rs.getInt(10) == 1){
                    groupPersistDTO.setLargeGroup(true);
                }
                if(rs.getInt(10) == 2){
                    groupPersistDTO.setMcxGrpInd(1);
                }else {
                    groupPersistDTO.setMcxGrpInd(0);
                }

                groupPersistDTO.setGroupProfileId(rs.getString(11));
                groupPersistDTO.setGrpShared((Integer) rs.getObject(12));
                groupPersistDTO.setOverrideDnd(rs.getInt(13));
                groupPersistDTO.setIsPreConfiguredGroup(rs.getInt(14));
                int corpId  = rs.getInt(15);
                groupPersistDTO.setCorpId(corpId);
                groupPersistDTO.setGrpOwnerCorpId(String.valueOf(corpId));
                groupPersistDTO.setUgwInterop(null == rs.getObject(16) ? null : (Integer) rs.getObject(16));
                if(clientIntf != AREA_BASED_DYNAMIC_GROUP && groupPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()){
                    knLogger.error( methodName, "Group does not exists. GroupId - ", groupIds);
                    throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                            "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
                }
                resp.put(groupPersistDTO.getGroupId(), groupPersistDTO);

            }
            if (resp.isEmpty()) {
                knLogger.error( methodName, "Group does not exists. GroupIdss - ", groupIds);
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND, "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }

            return resp;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while retrieving the group Info-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occurred while retrieving the group Info- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to selectGroupInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT :GroupInfos - ", resp);
        }
    }

    public Map<Integer, Integer> getCorpIdfromGroupID(List<String> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdfromGroupID(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : groupIds - ", groupIds);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpId = 0;
        int groupId = 0;
        Map<Integer, Integer> corpGroupMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "SELECT CORPID, CORPGROUPID FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN (GROUPIDS);";
            query = replaceContactWithValue(query, "GROUPIDS", KnCorpUtil.formCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query -", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            while (rs.next()) {
                corpId = rs.getInt(1);
                groupId = rs.getInt(2);
                corpGroupMap.put(groupId, corpId);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the corpId-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while retrieving the corpId- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to select corpId " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT :corpGroupMap - ", corpGroupMap);
        }
        return corpGroupMap;
    }

    /**
     * Returns the list of MCX groups filtering from the given group ids.
     *
     * @param grpIdList
     * @param persisterTxn
     * @return List of MCX group ids
     * @throws KnDAOException
     */
    public List<Integer> getMCXGroups(List<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMCXGroups(List<Integer>)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        List<Integer> mcxGrpList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MCX_GROUPS);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            query = KnCorpUtil.replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Exeuting query - ", query);
            pstmt.setInt(1, MCX_GROUP_TYPE);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                mcxGrpList.add(rs.getInt(1));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while retrieving the MCX groups - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "Returning mcxGrpList - ", mcxGrpList);
        return mcxGrpList;
    }

    public void getHierarchyDetails(String corpId, List<String> removedHierarchyList, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnDAOException {
        String methodName = "getHierarchyDetails()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String placeholders = String.join(",", Collections.nCopies(removedHierarchyList.size(), "?"));
        String query = "SELECT COUNT(HIERARCHY_ID) FROM DG.CORP_HIERARCHY_DETAILS WHERE CORPID = ? AND HIERARCHY_ID IN (" + placeholders + ");";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt.setString(i + 2, removedHierarchyList.get(i));
            }
            knLogger.debug(methodName, "Executing query - ", query, " with corpId - ", corpId, " and hierarchyId - ", removedHierarchyList);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == removedHierarchyList.size()) {
                    knLogger.debug(methodName, "All hierarchy IDs exist for corpId - ", corpId);
                } else {
                    knLogger.debug(methodName, "Mismatch: Query count - ", count, ", List size - ", removedHierarchyList.size());
                    throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION,
                            "The provided hierarchy IDs do not match the corporation.");
                }
            } else {
                knLogger.debug(methodName, "No hierarchy IDs found for corpId - ", corpId);
                throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION,
                        "The provided hierarchy IDs do not belong to the corporation.");
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while retrieving hierarchy details - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve hierarchy details.", pttServerId, "DG.CORP_HIERARCHY_DETAILS", query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving hierarchy details - ", e);
            throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION,
                    "The provided hierarchy IDs do not belong to the corporation.");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void getSubscriberCountForHierarchyDeletion(String corpId, List<String> removedHierarchyList, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String methodName = "getSubscriberCountForHierarchyDeletion()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);

        // Handle empty list case
        if (removedHierarchyList == null || removedHierarchyList.isEmpty()) {
            knLogger.debug(methodName, "removedHierarchyList is null or empty, nothing to check");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String placeholders = String.join(",", Collections.nCopies(removedHierarchyList.size(), "?"));
        // Query returns count per hierarchy ID
        String query = "SELECT PSI.HIERARCHY_ID, COUNT(PSI.MDN), CHD.HIERARCHY_NAME FROM DG.POCSUBSCRINFO PSI "
                + " INNER JOIN DG.CORP_HIERARCHY_DETAILS CHD ON (PSI.CORPID = CHD.CORPID AND PSI.HIERARCHY_ID = CHD.HIERARCHY_ID) "
                + " WHERE PSI.CORPID = ? AND PSI.HIERARCHY_ID IN (" + placeholders + ") GROUP BY PSI.HIERARCHY_ID, CHD.HIERARCHY_NAME";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt.setString(i + 2, removedHierarchyList.get(i));
            }
            knLogger.debug(methodName, "Executing query - ", query, " with corpId - ", corpId, " and removedHierarchyList - ", removedHierarchyList);
            rs = pstmt.executeQuery();
            // Process each hierarchy ID's count
            while (rs.next()) {
                String hierarchyId = rs.getString(1);
                int count = rs.getInt(2);
                String hierarchyName = rs.getString(3);
                knLogger.debug(methodName, "Subscriber count for hierarchyId - ", hierarchyId, " is ", count);
                if (count > 0) {
                    throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.
                            BOEntity.HIERARCHY_HAS_ASSOCIATED_SUBSCRIBERS,
                            "Cannot delete '" + hierarchyName + "'. It has " + count + " associated subscriber" + (count > 1 ? "s" : "") + ". Remove or reassign the subscriber" + (count > 1 ? "s" : "") + " before deleting the organization");
                }
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while retrieving subscriber count - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subscriber count.", pttServerId, "DG.CORP_HIERARCHY_DETAILS", query);
        } catch (KnXDMServerException e) {
            throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_HAS_ASSOCIATED_SUBSCRIBERS,
                    e.getErrorMessage());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void getGroupCountForHierarchyId(String corpId, List<String> removedHierarchyList, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String methodName = "getGroupCountForHierarchyId()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);

        // Handle empty list case
        if (removedHierarchyList == null || removedHierarchyList.isEmpty()) {
            knLogger.debug(methodName, "removedHierarchyList is null or empty, nothing to check");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String placeholders = String.join(",", Collections.nCopies(removedHierarchyList.size(), "?"));
        // Query returns count per hierarchy ID
        String query = "SELECT CGI.HIERARCHY_ID, COUNT(CGI.CORPGROUPID), CHD.HIERARCHY_NAME FROM DG.CORPGROUPINFO CGI "
                + " INNER JOIN  DG.CORP_HIERARCHY_DETAILS CHD ON (CGI.CORPID = CHD.CORPID AND CHD.HIERARCHY_ID = CGI.HIERARCHY_ID) "
                + " WHERE CGI.CORPID = ? AND CGI.HIERARCHY_ID IN (" + placeholders + ") GROUP BY CGI.HIERARCHY_ID,CHD.HIERARCHY_NAME";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt.setString(i + 2, removedHierarchyList.get(i));
            }
            knLogger.debug(methodName, "Executing query - ", query, " with corpId - ", corpId, " and removedHierarchyList - ", removedHierarchyList);
            rs = pstmt.executeQuery();
            // Process each hierarchy ID's count
            while (rs.next()) {
                String hierarchyId = rs.getString(1);
                int count = rs.getInt(2);
                String hierarchyName = rs.getString(3);
                knLogger.debug(methodName, "Group count for hierarchyId - ", hierarchyId, " is ", count);
                if (count > 0) {
                    throw new KnXDMServerException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_HAS_ASSOCIATED_GROUPS,
                            "Cannot delete '" + hierarchyName + "' because it is associated with " + count + " group" + (count > 1 ? "s" : "") + ". Please remove or reassign the group" + (count > 1 ? "s" : "") + " before deleting the organization.");
                }
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while retrieving group count - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve group count.", pttServerId, "DG.CORPGROUPINFO", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    // -------------------------------------------------------------------------
    // Hierarchy deletion pre-checks — return violating hierarchyId→count map
    // -------------------------------------------------------------------------

    private List<List<String>> partitionList(List<String> list, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    public Map<String, Integer> checkGroupProfilesForHierarchyDeletion(String corpId, List<String> hierarchyIds,
            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "checkGroupProfilesForHierarchyDeletion()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", hierarchyIds count - ", hierarchyIds.size());
        Map<String, Integer> violations = new LinkedHashMap<>();
        if (hierarchyIds == null || hierarchyIds.isEmpty()) return violations;

        for (List<String> batch : partitionList(hierarchyIds, 100)) {
            String placeholders = String.join(",", Collections.nCopies(batch.size(), "?"));
            String query = "SELECT HIERARCHY_ID, COUNT(*) FROM DG.CORPGROUPPROFILE"
                    + " WHERE CORPID = ? AND HIERARCHY_ID IN (" + placeholders + ")"
                    + " GROUP BY HIERARCHY_ID";
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = persisterTxn.getDBConnection(pttServerId, false);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(corpId));
                for (int i = 0; i < batch.size(); i++) {
                    pstmt.setString(i + 2, batch.get(i));
                }
                knLogger.debug(methodName, "Executing query for batch size - ", batch.size());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String hierarchyId = rs.getString(1);
                    int count = rs.getInt(2);
                    if (count > 0) {
                        knLogger.debug(methodName, "Group profile violation: hierarchyId=", hierarchyId, " count=", count);
                        violations.put(hierarchyId, count);
                    }
                }
            } catch (SQLException e) {
                knLogger.error(methodName, "SQLException occurred - ", e);
                throw KnDbUtil.processException(e, "Failed to check group profiles for hierarchy.", pttServerId, "DG.CORPGROUPPROFILE", query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pstmt);
            }
        }
        knLogger.debug(methodName, "EXIT : violations found - ", violations.size());
        return violations;
    }

    public Map<String, Integer> checkSubListsForHierarchyDeletion(String corpId, List<String> hierarchyIds,
            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "checkSubListsForHierarchyDeletion()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", hierarchyIds count - ", hierarchyIds.size());
        Map<String, Integer> violations = new LinkedHashMap<>();
        if (hierarchyIds == null || hierarchyIds.isEmpty()) return violations;

        for (List<String> batch : partitionList(hierarchyIds, 100)) {
            String placeholders = String.join(",", Collections.nCopies(batch.size(), "?"));
            String query = "SELECT HIERARCHY_ID, COUNT(*) FROM DG.CORPLISTINFO"
                    + " WHERE CORPID = ? AND HIERARCHY_ID IN (" + placeholders + ")"
                    + " GROUP BY HIERARCHY_ID";
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(corpId));
                for (int i = 0; i < batch.size(); i++) {
                    pstmt.setString(i + 2, batch.get(i));
                }
                knLogger.debug(methodName, "Executing query for batch size - ", batch.size());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String hierarchyId = rs.getString(1);
                    int count = rs.getInt(2);
                    if (count > 0) {
                        knLogger.debug(methodName, "SubList violation: hierarchyId=", hierarchyId, " count=", count);
                        violations.put(hierarchyId, count);
                    }
                }
            } catch (SQLException e) {
                knLogger.error(methodName, "SQLException occurred - ", e);
                throw KnDbUtil.processException(e, "Failed to check sublists for hierarchy.", pttServerId, "DG.CORPLISTINFO", query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pstmt);
            }
        }
        knLogger.debug(methodName, "EXIT : violations found - ", violations.size());
        return violations;
    }

    public Map<String, Integer> checkOSMListInfoForHierarchyDeletion(List<String> hierarchyIds,
            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "checkOSMListInfoForHierarchyDeletion()";
        knLogger.debug(methodName, "ENTRY : hierarchyIds count - ", hierarchyIds.size());
        Map<String, Integer> violations = new LinkedHashMap<>();
        if (hierarchyIds == null || hierarchyIds.isEmpty()) return violations;

        for (List<String> batch : partitionList(hierarchyIds, 100)) {
            String placeholders = String.join(",", Collections.nCopies(batch.size(), "?"));
            String query = "SELECT HIERARCHY_ID, COUNT(*) FROM DG.OSMLISTINFO"
                    + " WHERE HIERARCHY_ID IN (" + placeholders + ")"
                    + " GROUP BY HIERARCHY_ID";
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                pstmt = conn.prepareStatement(query);
                for (int i = 0; i < batch.size(); i++) {
                    pstmt.setString(i + 1, batch.get(i));
                }
                knLogger.debug(methodName, "Executing query for batch size - ", batch.size());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String hierarchyId = rs.getString(1);
                    int count = rs.getInt(2);
                    if (count > 0) {
                        knLogger.debug(methodName, "OSMListInfo violation: hierarchyId=", hierarchyId, " count=", count);
                        violations.put(hierarchyId, count);
                    }
                }
            } catch (SQLException e) {
                knLogger.error(methodName, "SQLException occurred - ", e);
                throw KnDbUtil.processException(e, "Failed to check OSM list info for hierarchy.", pttServerId, "DG.OSMLISTINFO", query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pstmt);
            }
        }
        knLogger.debug(methodName, "EXIT : violations found - ", violations.size());
        return violations;
    }

    public Map<String, Integer> checkOSMListForHierarchyDeletion(String corpId, List<String> hierarchyIds,
            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "checkOSMListForHierarchyDeletion()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", hierarchyIds count - ", hierarchyIds.size());
        Map<String, Integer> violations = new LinkedHashMap<>();
        if (hierarchyIds == null || hierarchyIds.isEmpty()) return violations;

        for (List<String> batch : partitionList(hierarchyIds, 100)) {
            String placeholders = String.join(",", Collections.nCopies(batch.size(), "?"));
            String query = "SELECT HIERARCHY_ID, COUNT(*) FROM DG.CORPOSMINFO"
                    + " WHERE CORPID = ? AND HIERARCHY_ID IN (" + placeholders + ")"
                    + " GROUP BY HIERARCHY_ID";
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(corpId));
                for (int i = 0; i < batch.size(); i++) {
                    pstmt.setString(i + 2, batch.get(i));
                }
                knLogger.debug(methodName, "Executing query for batch size - ", batch.size());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String hierarchyId = rs.getString(1);
                    int count = rs.getInt(2);
                    if (count > 0) {
                        knLogger.debug(methodName, "OSMList violation: hierarchyId=", hierarchyId, " count=", count);
                        violations.put(hierarchyId, count);
                    }
                }
            } catch (SQLException e) {
                knLogger.error(methodName, "SQLException occurred - ", e);
                throw KnDbUtil.processException(e, "Failed to check OSM lists for hierarchy.", pttServerId, "DG.CORPOSMINFO", query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pstmt);
            }
        }
        knLogger.debug(methodName, "EXIT : violations found - ", violations.size());
        return violations;
    }

    public void deleteHierarchyDetails(String corpId, List<String> removedHierarchyList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteHierarchyDetails()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String placeholders = String.join(",", Collections.nCopies(removedHierarchyList.size(), "?"));
        String query = "DELETE FROM DG.CORP_HIERARCHY_DETAILS WHERE CORPID = ? AND HIERARCHY_ID IN (" + placeholders + ");";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt.setString(i + 2, removedHierarchyList.get(i));
            }
            knLogger.debug(methodName, "Executing query - ", query, " with corpId - ", corpId, " and hierarchyId - ", removedHierarchyList);
            int rowsAffected = pstmt.executeUpdate();
            knLogger.info(methodName, "Rows affected: ", rowsAffected);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while deleting hierarchy details - ", e);
            throw KnDbUtil.processException(e, "Failed to delete hierarchy details.", pttServerId, "DG.CORP_HIERARCHY_DETAILS", query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteHierarchyDepthDetails(String corpId, List<String> removedHierarchyList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteHierarchyDepthDetails()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);

        if (removedHierarchyList == null || removedHierarchyList.isEmpty()) {
            knLogger.debug(methodName, "removedHierarchyList is null or empty, nothing to delete");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        String placeholders = String.join(",", Collections.nCopies(removedHierarchyList.size(), "?"));


        String query1 = "DELETE FROM DG.CORP_HIERARCHY_DEPTH WHERE CORPID = ? AND ANCESTOR_HIER_ID IN (" + placeholders + ");";
        String query2 = "DELETE FROM DG.CORP_HIERARCHY_DEPTH WHERE CORPID = ? AND DESCENDANT_HIER_ID IN (" + placeholders + ");";

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);


            knLogger.debug(methodName, "Executing query1 - ", query1);
            pstmt1 = conn.prepareStatement(query1);
            pstmt1.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt1.setString(i + 2, removedHierarchyList.get(i));
            }
            int rowsAffected1 = pstmt1.executeUpdate();


            knLogger.debug(methodName, "Executing query2 - ", query2);
            pstmt2 = conn.prepareStatement(query2);
            pstmt2.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt2.setString(i + 2, removedHierarchyList.get(i));
            }
            int rowsAffected2 = pstmt2.executeUpdate();

            knLogger.info(methodName, "Total rows affected: ", (rowsAffected1 + rowsAffected2));

        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while deleting hierarchy depth - ", e);
            throw KnDbUtil.processException(e, "Failed to delete hierarchy depth.", pttServerId, "DG.CORP_HIERARCHY_DEPTH", query1 + " | " + query2);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt1);
            KnDbUtil.closePreparedStatement(pstmt2);
        }
    }

    public void deleteAnchorPocInfo(String corpId, List<String> removedHierarchyList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAnchorPocInfo()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String placeholders = String.join(",", Collections.nCopies(removedHierarchyList.size(), "?"));
        String query = "DELETE FROM DG.ANCHOR_POC_INFO WHERE CORPID = ? AND HIERARCHY_ID IN (" + placeholders + ");";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt.setString(i + 2, removedHierarchyList.get(i));
            }
            knLogger.debug(methodName, "Executing query - ", query, " with corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);
            int rowsAffected = pstmt.executeUpdate();
            knLogger.info(methodName, "Rows affected: ", rowsAffected);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while deleting anchor POC info - ", e);
            throw KnDbUtil.processException(e, "Failed to delete anchor POC info.", pttServerId, "DG.ANCHOR_POC_INFO", query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteHierarchyGeocodeMap(String corpId, List<String> removedHierarchyList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteHierarchyGeocodeMap()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", removedHierarchyList - ", removedHierarchyList);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String placeholders = String.join(",", Collections.nCopies(removedHierarchyList.size(), "?"));
        String query = "DELETE FROM DG.CORP_HIERARCHY_GEOCODE_MAP WHERE CORPID = ? AND HIERARCHY_ID IN (" + placeholders + ");";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            for (int i = 0; i < removedHierarchyList.size(); i++) {
                pstmt.setString(i + 2, removedHierarchyList.get(i));
            }
            knLogger.debug(methodName, "Executing query - ", query, " with corpId - ", corpId, " and hierarchyId - ", removedHierarchyList);
            int rowsAffected = pstmt.executeUpdate();
            knLogger.info(methodName, "Rows affected: ", rowsAffected);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred while deleting hierarchy geocode map - ", e);
            throw KnDbUtil.processException(e, "Failed to delete hierarchy geocode map.", pttServerId, "DG.CORP_HIERARCHY_GEOCODE_MAP", query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
}
