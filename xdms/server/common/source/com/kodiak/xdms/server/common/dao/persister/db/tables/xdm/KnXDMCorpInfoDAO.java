/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpInfoDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 10, 2011      7.0
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
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dto.KnPendingTxnInfoDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpGpInfoDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class KnXDMCorpInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpInfoDAO.class);

    private static final String CLASS = KnXDMCorpInfoDAO.class.getName();
    private String xdmsHome;
    private static int GROUP_CREATED_BY_ABDG = 2;
    private static int RETRY_COUNT = 0;
    private static Random random = new Random();

    static AtomicLong counter;

    KnXDMCorpInfoDAO(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    static {
        try {
            // Fetch max RECID from DB
            long maxRecId = getMaxRecId();
            knLogger.error("maxRecId from DB: " + maxRecId);
            counter = new AtomicLong(maxRecId+1);
        } catch (Exception e) {
            knLogger.error("Error fetching max RECID from DB: " + e.getMessage());
            counter = new AtomicLong(1);  // Default to 1 if DB query fails
        }
    }

    private static long getMaxRecId() {
        final String methodName = "getMaxRecId()";
        knLogger.debug(methodName, "ENTRY : Fetch max recId - ");

        String query = "SELECT MAX(RECID) FROM DG.PENDING_TXN_INFO";
        KnPersisterTxn persisterTxn;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            String pttServerId = genInfoUtil.retrieveLocalXDMPttServerId();

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the transaction");
            persisterTxn.open();

            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            rs = pStmt.executeQuery();

            knLogger.debug(methodName, "Successfully Executed query - {}", query);
            return rs.next() && rs.getObject(1) != null ? rs.getLong(1) : 0L; // Return max RECID if found, else return 0
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve xdm Ptt Server Id", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while retrieving MaxRecIdFromDb", e);
        } finally {
            knLogger.info( methodName, "EXIT : max recId");
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        return 0L;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public KnCorpProfilePersistDTO selectCorpInfo(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectCorpInfo(corpId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId - " + corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String query = null;
        KnCorpProfilePersistDTO profilePersistDTO = null;
        Connection conn = null;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(xdmsHome), true);
                ownedTxn = true;
            }

            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "SELECT EXTCORPID, XDMSHOME, PROFILECREATIONTIME, LASTPROFILEUPDATETIME, CORPNAME, " +
                    "PAIREDCONTACTLISTID, MAXCORPLISTS, MAXMEMBERSPERCORPLIST, MAXCORPGROUPS, MAXMEMBERSPERCORPGROUP, " +
                    "MaxDispatchGrps, MaxMembersPerDispatchGrp, LASTPROFILEUPDATETIME, POCHOME, CORPFS1 , MAX_NNI_SUBSCRS,   " +
                    "MAX_MEMBERS_PER_BG,LINKED_GW_KEY, NEXTGENCAT_ENABLED, MAXTEXTMSGSIZE, MAXMMMSGSIZECELL, MAXMMMSGSIZEWIFI, " +
                    "DELIVERYRECEIPTFLAG, READREPORTFLAG, MSG_TTL, MAXPREDEFINEDMSGCNT, MAXPREDEFINEDTMPLTCNT, MAXUSERDEFINEDMSGCNT, " +
                    "FLEETMEMBERGEOTAGFLAG, OPSCORPFS1, WEB_DISPATCH_ENABLED, MAX_ABDG_TALKGRP, MAX_LRGAB_TALKGRP, " +
                    "MAX_USR_LRGAB_TALKGRP, MAX_ABDG_PER_GRPOWNER, MAX_ABDG_PER_GRPMEMBER, MAXCHANNELSPERZONE, MAXRADIOCHANNELS, " +
                    "MAXZONES, LARGE_GROUP_SUPPORTED, MAXSIMULDEDICATEDSESSION, MAXSIMULDYNAMICSESSION, CORPFS2, OPSCORPFS2, " +
                    "LMRDATAINTROPFLAG, PRIVACY_AMB_DISC_LISTEN, MCVIDEOFLOORHOLDTIMER,USER_PROFILE_MGMT, MAX_USER_PROFILES, " +
                    "MAX_USERPROFILES_PERSUB, GROUP_PROFILE_MGMT, MAX_GRP_PROFILES, GROUP_SHARING_FLAG, UPM_SHARING_FLAG,COMMON_CONTACTLIST_SUP, " +
                    "CORP_HIERARCHY, CATACCESS_PERMSET, PERMSET_UPDATETIME, XDMCORPFS2_SET FROM DG.POCCORPINFO WHERE CORPID=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.valueOf(corpId));
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            if (rs.next()) {
                profilePersistDTO = new KnCorpProfilePersistDTO();
                profilePersistDTO.setCorpId(Integer.parseInt(corpId));
                profilePersistDTO.setExtCorpId(rs.getString(1));
                profilePersistDTO.setXdmsHome(rs.getString(2));
                profilePersistDTO.setProfileLastUpdated(rs.getLong(4));
                profilePersistDTO.setNetworkName(rs.getString(5));
                profilePersistDTO.setPairedContactListId(rs.getInt(6));
                profilePersistDTO.setMaxCorpLists(rs.getInt(7));
                profilePersistDTO.setMaxMemPerCorpList(rs.getInt(8));
                profilePersistDTO.setMaxCorpGroups(rs.getInt(9));
                profilePersistDTO.setMaxMemPerCorpGroup(rs.getInt(10));
                profilePersistDTO.setMaxDispatchGroup(rs.getInt(11));
                profilePersistDTO.setMaxMembersPerDispatchGroup(rs.getInt(12));
                profilePersistDTO.setCorpMasterListEtag(rs.getLong(13));
                profilePersistDTO.setPocHome(rs.getString(14));
                profilePersistDTO.setMaxExtSubsPerCorp(rs.getInt(16));
                profilePersistDTO.setMaxMemPerBCGrp(rs.getInt(17));
             //   profilePersistDTO.setActiveFS1(rs.getLong(16));
                profilePersistDTO.setLinkedGwKey(rs.getString(18));
                profilePersistDTO.setNxtGenCatEnabled(rs.getInt(19));
                profilePersistDTO.setMaxTextMsgSize(rs.getInt(20));
                profilePersistDTO.setMaxMmmsgSizeCell(rs.getInt(21));
                profilePersistDTO.setMaxMmmsgSizeWifi(rs.getInt(22));
                profilePersistDTO.setDeliveryReceiptFlag(rs.getInt(23));
                profilePersistDTO.setReadReportFlag(rs.getInt(24));
                profilePersistDTO.setMsgTtl(rs.getInt(25));
                profilePersistDTO.setMaxPredefinedMsgCnt(rs.getInt(26));
                profilePersistDTO.setMaxPredefinedTmpltCnt(rs.getInt(27));
                profilePersistDTO.setMaxUserDefinedMsgCnt(rs.getInt(28));
                profilePersistDTO.setFleetMemberGeoTagFlag(rs.getInt(29));
                profilePersistDTO.setWebDispatchEnabled(rs.getInt(31));
                profilePersistDTO.setMaxAbdgTalkGrp(rs.getInt(32));
                profilePersistDTO.setMaxLrGabTalkGroup(rs.getInt(33));
                profilePersistDTO.setMaxUsrLrGabGroup(rs.getInt(34));
                profilePersistDTO.setMaxAbdgPerGrpOwner(rs.getInt(35));
                profilePersistDTO.setMaxAbdgPerGrpMember(rs.getInt(36));
                profilePersistDTO.setMaxChannelsPerZone(rs.getInt(37));
                profilePersistDTO.setMaxRadioChannels(rs.getInt(38));
                profilePersistDTO.setMaxZones(rs.getInt(39));
                profilePersistDTO.setLargeGrpSupported(rs.getInt(40));
                profilePersistDTO.setMaxSDDSession(rs.getInt(41));
                profilePersistDTO.setMaxSDYSession(rs.getInt(42));
                String corpFS = rs.getString(43) != null ? rs.getString(43) : KnGeneralUtil.convertLongToHexString(rs.getLong(15));
                profilePersistDTO.setCorpFS2(corpFS);
                String opsCorpFS = rs.getString(44) != null ? rs.getString(44) : KnGeneralUtil.convertLongToHexString(rs.getLong(30));
                profilePersistDTO.setOpsCorpFs2(opsCorpFS);
                profilePersistDTO.setLmrIntropFlag(rs.getInt(45));
				//profilePersistDTO.setPrivacyAmbDiscListenFlag(rs.getInt(46));
                Integer mcVideoFlTimer=(Integer)rs.getObject(47);
                profilePersistDTO.setMcvideoFloorHoldTimer(mcVideoFlTimer);
				if (rs.getString("PRIVACY_AMB_DISC_LISTEN") != null) {
                    profilePersistDTO.setPrivacyAmbDiscListenFlag(Integer.parseInt(rs.getString("PRIVACY_AMB_DISC_LISTEN")));
                } else {
                    profilePersistDTO.setPrivacyAmbDiscListenFlag(null);
                }
                profilePersistDTO.setUserProfileMgmt(rs.getString("USER_PROFILE_MGMT") != null ?Integer.valueOf(rs.getString("USER_PROFILE_MGMT")):null);
                profilePersistDTO.setMaxUserProfiles(rs.getString("MAX_USER_PROFILES") != null ?Integer.valueOf(rs.getString("MAX_USER_PROFILES")):null);
                profilePersistDTO.setMaxAssignProfiles(rs.getString("MAX_USERPROFILES_PERSUB") != null ?Integer.valueOf(rs.getString("MAX_USERPROFILES_PERSUB")):null);
                profilePersistDTO.setGroupProfileMgmt(rs.getString("GROUP_PROFILE_MGMT") != null ?Integer.valueOf(rs.getString("GROUP_PROFILE_MGMT")):null);
                profilePersistDTO.setMaxgGroupProfiles(rs.getString("MAX_GRP_PROFILES") != null ?Integer.valueOf(rs.getString("MAX_GRP_PROFILES")):null);
                profilePersistDTO.setGroupSharingFeature(rs.getString("GROUP_SHARING_FLAG") != null ?Integer.valueOf(rs.getString("GROUP_SHARING_FLAG")):null);
                profilePersistDTO.setUserProfileSharingFeature(rs.getString("UPM_SHARING_FLAG") != null ?Integer.valueOf(rs.getString("UPM_SHARING_FLAG")):null);
                profilePersistDTO.setCommonContactListSupport(rs.getString("COMMON_CONTACTLIST_SUP") !=null ?Integer.valueOf(rs.getString("COMMON_CONTACTLIST_SUP")):null);
                profilePersistDTO.setHierarchyType(rs.getInt("CORP_HIERARCHY"));
                profilePersistDTO.setCatAccessPermSet(String.valueOf(rs.getInt("CATACCESS_PERMSET")));
                profilePersistDTO.setCatAccessPermUpdateTS(String.valueOf(rs.getLong("PERMSET_UPDATETIME")));
                profilePersistDTO.setXdmCorpFS2Set(rs.getString("XDMCORPFS2_SET"));
            } else {
                knLogger.error( methodName, "Corporate Profile not found for CorpId - " + corpId);
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Corporate Profile not found");
            }
            knLogger.debug( methodName, "Corporate Profile found - " + profilePersistDTO);
            return profilePersistDTO;

        } catch (KnPersistenceException e) {
            knLogger.error( methodName, "KnPersistenceException occured while retrieving CorpProfile Details for corpId - " + corpId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    xdmsHome, KnDAOSourceTypes.POCCORPINFO, query);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnPersistenceException occured while retrieving CorpProfile Details for corpId - " +  corpId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected exception occured while retrieving CorpProfile Details for corpId - " + corpId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve CorpProfile Details - " + e,
                    xdmsHome, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.info(methodName, "Exit : corpId - " + corpId);
        }
    }
    public List<Integer> selectCorpGroupId(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectCorpGroupId(corpId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId - " + corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpGroupId = 0;
        List<Integer> corpgroupIds= new ArrayList<>();
        try {
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "SELECT CORPGROUPID " +
                    " FROM DG.CORPGROUPINFO WHERE CORPID=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.valueOf(corpId));
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            while (rs.next()) {
                corpGroupId = rs.getInt(1);
                corpgroupIds.add(corpGroupId);
            } /* else {
                knLogger.error( methodName, "corpGroupId not found for CorpId - " + corpId);
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "corpGroupId not found");
            }*/
            knLogger.debug( methodName, "corpGroupIds found - " + corpgroupIds);
            return corpgroupIds;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnPersistenceException occured while retrieving corpGroupId for corpId - " +  corpId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected exception occured while retrieving corpGroupId for corpId - " + corpId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpGroupId - " + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info( methodName, "inputCorpId - " + corpId, "EXIT : corpGroupIds - " + corpgroupIds);
        }
    }

    public String getGroupName(String groupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getGroupName(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupId - " + groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String groupName="";
        try {
            //Connection conn = persisterTxn.getDBConnection(xdmsHome, false);
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "SELECT GROUPDISPLAYNAME " +
                    " FROM DG.CORPGROUPINFO WHERE CORPGROUPID=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.valueOf(groupId));
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");

            if (rs.next()) {
                groupName = rs.getString(1);
            } else {
                knLogger.error( methodName, "groupName not found for corpgroupId - " + groupId);
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "groupName not found");
            }
            knLogger.debug( methodName, "groupName found - " + groupName);
            return groupName;

        } catch (KnPersistenceException e) {
            knLogger.error( methodName, "KnPersistenceException occured while retrieving groupName for groupId - " + groupId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnPersistenceException occured while retrieving corpGroupId for groupId - " +  groupId + ", " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected exception occured while retrieving corpGroupId for groupId - " + groupId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpGroupId - " + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info( methodName, "groupId - " + groupId, "EXIT : groupName - " + groupName);
        }
    }

    public Map<String,Boolean> getGroupMemberList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "custom : getGroupMemberList(groupId, persisterTxn)";
        knLogger.debug( methodName, "Entry : groupId - " , groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT MEMBERMDN,IS_SUPERVISOR FROM DG.CORPGROUPMEMBERLIST WHERE CORPGROUPID=?;";
        Map<String,Boolean> groupMemList = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //  Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupMemList.put(rs.getString(1).trim(),rs.getBoolean(2));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getGroupMemberList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getGroupMemberList - " ,e);
            throw KnDbUtil.processException(e, "Failed while getGroupMemberList -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. groupMemList - " , groupMemList);
        }
        return groupMemList;
    }


    public Map<String,Boolean> getGroupMemberListWithBC(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "custom : getGroupMemberListWithBC(groupId, persisterTxn)";
        knLogger.debug( methodName, "Entry : groupId - " , groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT MEMBERMDN,IS_BROADCASTER FROM DG.CORPGROUPMEMBERLIST WHERE CORPGROUPID=?;";
        Map<String,Boolean> groupMemList = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //  Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupMemList.put(rs.getString(1).trim(),rs.getBoolean(2));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getGroupMemberList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getGroupMemberList - " ,e);
            throw KnDbUtil.processException(e, "Failed while getGroupMemberList -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. groupMemList - " , groupMemList);
        }
        return groupMemList;
    }
    public Integer getMemberCountFromMemberList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMemberCountFromMemberList(groupId, persisterTxn)";
        knLogger.debug(methodName, "Entry : groupId - ", groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer memberCount = 0;
        String query = "SELECT MEMBERCOUNT FROM DG.CORPGROUPMEMBERCOUNT WHERE  CORPGROUPID=?;";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            //  Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getObject(1) != null) {
                    memberCount = rs.getInt(1);
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getMemberCountFromMemberList  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getMemberCountFromMemberList - ", e);
            throw KnDbUtil.processException(e, "Failed while getMemberCountFromMemberList -" + e,
                    xdmsHome, KnDAOSourceTypes.CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT. memberCount - ", memberCount);
        }
        return memberCount;
    }


    public KnCorpGpInfoDTO getCorpGroupInfoList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "custom : getCorpGroupInfoList(groupId, persisterTxn)";
        knLogger.debug( methodName, "Entry : groupId - " , groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT CORPGROUPID,CORPID,GROUPDISPLAYNAME,GROUPMEMBERLISTID,GROUPDISTRIBUTIONPOLICY,GROUPTYPE,POCHOME," +
                "GROUPNAME,HANGTIMEOUT,OSMLISTID,AVATAR_ID,IS_LARGEGROUP,IS_PRECONFIG_GRP,GROUP_CREATED_BY,GROUP_OWNER,VIDEO_PERMISSION FROM DG.CORPGROUPINFO WHERE CORPGROUPID=?;";
        KnCorpGpInfoDTO knCorpGpInfoDTO=new KnCorpGpInfoDTO();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //  Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                knCorpGpInfoDTO.setCorpgroupId(rs.getInt(1));
                knCorpGpInfoDTO.setCorpId(rs.getInt(2));
                knCorpGpInfoDTO.setGroupDisplayName(new String(rs.getString(3).trim().getBytes("8859_1"),"UTF-8"));
                knCorpGpInfoDTO.setGroupMemListId(rs.getInt(4));
                knCorpGpInfoDTO.setGroupDistPolicy(rs.getInt(5));
                knCorpGpInfoDTO.setGroupType(rs.getString(6));
                knCorpGpInfoDTO.setPocHome(rs.getString(7));
                if (rs.getString(8) != null) {
                    knCorpGpInfoDTO.setGroupName(rs.getString(8).trim());
                }
                knCorpGpInfoDTO.setHangTimeOut(rs.getInt(9));
                knCorpGpInfoDTO.setOsmLisId(rs.getInt(10));
                knCorpGpInfoDTO.setAvatarId(rs.getInt(11));
                if(rs.getInt(12) == 2) {
                    knCorpGpInfoDTO.setMcxGrpInd(1);
                }else{
                    knCorpGpInfoDTO.setMcxGrpInd(0);
                }
                knCorpGpInfoDTO.setIsPreConfiguredGroup(rs.getString(13));
                knCorpGpInfoDTO.setGroupCreatedBy(rs.getInt(14));
                if (rs.getString(15) != null) {
                    knCorpGpInfoDTO.setGroupOwner(rs.getString(15).trim());
                }
                if (rs.getObject("IS_LARGEGROUP") != null) {
                    knCorpGpInfoDTO.setIsLargeGroup(rs.getInt("IS_LARGEGROUP"));
                } else {
                    knCorpGpInfoDTO.setIsLargeGroup(KnConstants.LARGE_GROUP_DISABLED);
                }
                if (rs.getObject("VIDEO_PERMISSION") != null) {
                    knCorpGpInfoDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                }
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getCorpGroupInfoList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getCorpGroupInfoList - " ,e);
            throw KnDbUtil.processException(e, "Failed while getCorpGroupInfoList -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. getCorpGroupInfoList - " , knCorpGpInfoDTO);
        }
        return knCorpGpInfoDTO;
    }

    public String getSharedCorpGroup(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedCorpGroup(groupId, persisterTxn)";
        knLogger.debug( methodName, "Entry : groupId - " , groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sharedCorpid = null;
        String query = "SELECT SHAREDCORPID FROM DG.CORPGRP_SHAREDLIST WHERE CORPGROUPID=?;";
        KnCorpGpInfoDTO knCorpGpInfoDTO=new KnCorpGpInfoDTO();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //  Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sharedCorpid = String.valueOf(rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getCorpGroupInfoList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getCorpGroupInfoList - " ,e);
            throw KnDbUtil.processException(e, "Failed to get SharedCorpId -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. getSharedCorpId - " , knCorpGpInfoDTO);
        }
        return sharedCorpid;
    }

    public Map<Integer,Integer> getZoneChannelMap(int groupId,String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "DAO.getZoneChannelMap()";
        knLogger.debug( methodName, "Entry : groupId - " , groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String query = "SELECT ZONEID,CHANNELID FROM DG.SUBSCRPTTRADIOTGLIST WHERE GROUPID=? AND MDN=? ";
        Map<Integer,Integer> zoneChannelMap=new TreeMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            pstmt.setString(2,mdn);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                zoneChannelMap.put(rs.getInt("ZONEID"),rs.getInt("CHANNELID"));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getSubsPttTgList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getSubsPttTgList - " ,e);
            throw KnDbUtil.processException(e, "Failed while getSubsPttTgList -" + e,
                    xdmsHome, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. getSubsPttTgList zoneChannelMap- " , zoneChannelMap);
        }
        return zoneChannelMap;
    }

    public KnXDMTalkGroupInfoDTO getGroupPriority(int groupId,String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "custom : getCorpGroupInfoList(groupId, persisterTxn)";
        knLogger.debug( methodName, "Entry : groupId - " , groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String query = "SELECT PRIORITY FROM DG.CAMPEDGROUPINFO where GROUPID=? and MDN=?";
        KnXDMTalkGroupInfoDTO corpAddlTGInfoDTO=new KnXDMTalkGroupInfoDTO();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            pstmt.setString(2,mdn);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                corpAddlTGInfoDTO.setPriority(rs.getInt("PRIORITY"));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getSubsPttTgList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getSubsPttTgList - " ,e);
            throw KnDbUtil.processException(e, "Failed while getSubsPttTgList -" + e,
                    xdmsHome, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. getSubsPttTgList - " , corpAddlTGInfoDTO);
        }
        return corpAddlTGInfoDTO;
    }

    public List<KnCorpGpInfoDTO> getCorpGroupInfoDetails(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "custom : getCorpGroupInfoList(List<Integer>, persisterTxn)";
        knLogger.debug( methodName, "Entry : groupIds - " , groupIds);
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT CORPGROUPID,CORPID,GROUPDISPLAYNAME,GROUPMEMBERLISTID,GROUPDISTRIBUTIONPOLICY," +
                "GROUPTYPE,POCHOME, GROUPNAME,HANGTIMEOUT,OSMLISTID,AVATAR_ID, IS_LARGEGROUP FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN(GROUPIDS)";
        List<KnCorpGpInfoDTO> knCorpGpInfoDTOs = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, "GROUPIDS", formIntegerCommaSeperatedIdList(groupIds));
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Executing query - " , query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                KnCorpGpInfoDTO knCorpGpInfoDTO = new KnCorpGpInfoDTO();
                knCorpGpInfoDTO.setCorpgroupId(rs.getInt(1));
                knCorpGpInfoDTO.setCorpId(rs.getInt(2));
                if (rs.getString(3) != null) {
                    knCorpGpInfoDTO.setGroupDisplayName(rs.getString(3).trim());
                }
                knCorpGpInfoDTO.setGroupMemListId(rs.getInt(4));
                knCorpGpInfoDTO.setGroupDistPolicy(rs.getInt(5));
                knCorpGpInfoDTO.setGroupType(rs.getString(6));
                knCorpGpInfoDTO.setPocHome(rs.getString(7));
                if (rs.getString(8) != null) {
                    knCorpGpInfoDTO.setGroupName(rs.getString(8).trim());
                }
                knCorpGpInfoDTO.setHangTimeOut(rs.getInt(9));
                knCorpGpInfoDTO.setOsmLisId(rs.getInt(10));
                knCorpGpInfoDTO.setAvatarId(rs.getInt(11));
                if(rs.getInt(12) == 2) {
                    knCorpGpInfoDTO.setMcxGrpInd(1);
                }else {
                    knCorpGpInfoDTO.setMcxGrpInd(0);
                }
                knCorpGpInfoDTOs.add(knCorpGpInfoDTO);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getCorpGroupInfoList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getCorpGroupInfoList - " ,e);
            throw KnDbUtil.processException(e, "Failed while getCorpGroupInfoList -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug( methodName, "EXIT. getCorpGroupInfoList - " , knCorpGpInfoDTOs);
        }
        return knCorpGpInfoDTOs;
    }

    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }

    public static String formIntegerCommaSeperatedIdList(Collection<Integer> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return "";
        }
        for (Integer str : collectionStr) {
            buffer = buffer.append(str).append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public List<Integer> getSubsAbdgGroupList(String subsMdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubsAbdgGroupList(String , KnPersisterTxn)";
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = "SELECT G.CORPGROUPID FROM DG.CORPGROUPINFO G ,DG.CORPGROUPMEMBERLIST GROUPDIS" +
                    " WHERE GROUPDIS.CORPGROUPID = G.CORPGROUPID AND GROUPDIS.MEMBERMDN = ? AND G.GROUP_CREATED_BY = ?;";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, subsMdn);
            pstmt.setInt(2, GROUP_CREATED_BY_ABDG);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ", query);
            List<Integer> groupList = new ArrayList();
            while (rs.next()) {
                groupList.add(rs.getInt(1));
            }

            knLogger.info(methodName, "subsMdn - ", KnGDPRTemplate.mdn(subsMdn), "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving abdgGroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    xdmsHome, "CORPGROUPINFO", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Collection<KnCorpGpInfoDTO> getSubscriberGroupList(String subsMdn, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubscriberGroupList(String , boolean, KnPersisterTxn)";
        String query = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = "SELECT G.CORPGROUPID, G.ETAG, G.GROUPTYPE, G.GROUP_CREATED_BY, G.IS_LARGEGROUP, G.IS_PRECONFIG_GRP FROM DG.CORPGROUPINFO G ,DG.CORPGROUPMEMBERLIST GROUPDIS" +
                    " WHERE GROUPDIS.CORPGROUPID = G.CORPGROUPID AND GROUPDIS.MEMBERMDN = ?;";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, subsMdn);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ", query);
            Collection<KnCorpGpInfoDTO> groupList = new ArrayList<KnCorpGpInfoDTO>();
            while (rs.next()) {
                KnCorpGpInfoDTO groupInfo = new KnCorpGpInfoDTO();
                groupInfo.setCorpgroupId(rs.getInt(1));
                groupInfo.setGroupEtag(rs.getInt(2));
                groupInfo.setGroupType(String.valueOf(rs.getInt(3)));
                groupInfo.setGroupCreatedBy(rs.getInt(4));
                if (rs.getInt(5) == 2) {
                    groupInfo.setMcxGrpInd(1);
                } else {
                    groupInfo.setMcxGrpInd(0);
                }
                groupInfo.setIsPreConfiguredGroup(String.valueOf(rs.getInt(6)));
                groupList.add(groupInfo);
            }

            knLogger.info(methodName, "subsMdn - ", KnGDPRTemplate.mdn(subsMdn), "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving subscriber groupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    xdmsHome, "CORPGROUPINFO", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<Integer> getSharedCorpIds(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedCorpIds(int,KnPersisterTxn)";
        knLogger.info( methodName, "Entry : groupId - " , groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> sharedCorpids = new ArrayList<>();
        String query = "SELECT SHAREDCORPID FROM DG.CORPGRP_SHAREDLIST WHERE CORPGROUPID=?;";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sharedCorpids.add(rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getSharedCorpIds  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getSharedCorpIds - ", e);
            throw KnDbUtil.processException(e, "Failed to get SharedCorpIds -"+ e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName,"Exit :",sharedCorpids);
        return sharedCorpids;
    }

    public List<Integer> getUpmSharedCorpIds(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUpmSharedCorpIds(int,KnPersisterTxn)";
        knLogger.info(methodName, "Entry : userProfileId - ", userProfileId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> sharedCorpids = new ArrayList<>();
        String query = "SELECT SHAREDCORPID FROM DG.USERPROFILE_SHAREDLIST WHERE USERPROFILEID=?";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userProfileId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sharedCorpids.add(rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getSharedCorpIds  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getSharedCorpIds - ", e);
            throw KnDbUtil.processException(e, "Failed to get SharedCorpIds -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "Exit :", sharedCorpids);
        return sharedCorpids;
    }

    public void updateSipRecordingFlag(Integer sipRecordingFlag, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSipRecordingFlag(Integer, int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : CorpId - ", corpId, " sipRecordingFlag -", sipRecordingFlag);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "UPDATE DG.POCCORPINFO SET SIPRECORDINGFLAG=? WHERE CORPID=?";
            pstmt = conn.prepareStatement(query);
            if (null != sipRecordingFlag) {
                pstmt.setInt(1, sipRecordingFlag);
            } else {
                pstmt.setNull(1, Types.NULL);
            }
            pstmt.setInt(2, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnPersistenceException occurred while updating sipRecordingFlag for corpId - ", corpId, ", ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while updating sipRecordingFlag for corpId - ", corpId, ", ", e);
            throw KnDbUtil.processException(e, "Failed to updating sipRecordingFlag - " + e,
                    xdmsHome, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "inputCorpId - ", corpId, "EXIT : sipRecordingFlag - ", sipRecordingFlag);
        }
    }

    public List<String> getAllCorpIds() throws KnDAOException {
        String methodName = "getAllCorpIds()";
        knLogger.debug(methodName, "ENTRY : ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Connection conn = null;
        List<String> corpIds = new ArrayList<>();
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(xdmsHome), false);
            query = "SELECT CORPID FROM DG.POCCORPINFO";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ");
            while (rs.next()) {
                corpIds.add(rs.getString(1));
            }
            knLogger.debug(methodName, "corpIds found - ", corpIds.size());
            return corpIds;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while retrieving corpIds - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve corpIds - " + e,
                    xdmsHome, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            KnDbUtil.closeConnection(conn);
            knLogger.info(methodName, "EXIT : corpIds - ", corpIds.size());
        }
    }


    public List<String> getTgssGroupExtM(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTgssGroupExtM(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : MDN - ", mdn);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> tgssGroup = new ArrayList<>();
        String query = "SELECT CORPGROUPID FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN " +
                "(SELECT GROUPID FROM DG.SSCHANNELGROUPINFO WHERE MDN = ?);";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tgssGroup.add(rs.getString(1));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getTgssGroupExtM  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getTgssGroupExtM - ", e);
            throw KnDbUtil.processException(e, "Failed to get SharedCorpIds -" + e,
                    xdmsHome, com.kodiak.xdms.server.common.resources.KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "Exit :", tgssGroup);
        return tgssGroup;
    }

    public int getSubsClientType(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsClientType(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : MDN - ", mdn);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int clientType = 0;
        String query = "SELECT CLIENT_TYPE FROM DG.POCSUBSCRINFO WHERE MDN = ?";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                clientType = (rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getSubsClientType  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getSubsClientType - ", e);
            throw KnDbUtil.processException(e, "Failed to get clientType -" + e,
                    xdmsHome, com.kodiak.xdms.server.common.resources.KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "Exit :", clientType);
        return clientType;
    }


    public String selectXDMCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectCorpFS(String, boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : extCorpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        String xdmCorpFS = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = "SELECT XDMCORPFS2_SET FROM DG.POCCORPINFO WHERE CORPID = ?;";
            conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                xdmCorpFS = rs.getString(1);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving the xdmCorpFS - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occurred while  retrieving the xdmCorpFS  - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate etag " + e,
                    xdmsHome, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "input CorpId - ", corpId, "EXIT : corpId - ", corpId);
        }
        return xdmCorpFS;
    }

    public Boolean IsMdnPresentAsGroupMember(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "IsMdnPresentAsGroupMember(String, persisterTxn)";
        knLogger.debug( methodName, "Entry : MDN - " , KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT COUNT(1) FROM DG.CORPGROUPMEMBERLIST WHERE MEMBERMDN =? ";
        int count = 0;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while getGroupMemberList  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while getGroupMemberList - " ,e);
            throw KnDbUtil.processException(e, "Failed while getGroupMemberList -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. mdn count in memberlist table - " , count);
        }
        return count>0;
    }

    public Boolean IsMdnPresentAsContact(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "IsMdnPresentAsContact(String, persisterTxn)";
        knLogger.debug( methodName, "Entry : MDN - " , KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT COUNT(1) FROM DG.CORPCONTACTLIST WHERE CONTACTMDN =? ";
        int count = 0;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while DG.CORPCONTACTLIST  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while DG.CORPCONTACTLIST - " ,e);
            throw KnDbUtil.processException(e, "Failed while DG.CORPCONTACTLIST -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. mdn count in DG.CORPCONTACTLIST table - " , count);
        }
        return count>0;
    }

    public Boolean IsMdnPresentAsTarget(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "IsMdnPresentAsTarget(String, persisterTxn)";
        knLogger.debug( methodName, "Entry : MDN - " , KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT COUNT(1) FROM DG.MCPTT_PERM_INFO WHERE TARGET_MDN =? ";
        int count = 0;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while DG.MCPTT_PERM_INFO  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while DG.MCPTT_PERM_INFO - " ,e);
            throw KnDbUtil.processException(e, "Failed while DG.MCPTT_PERM_INFO -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. mdn count in DG.MCPTT_PERM_INFO table - " , count);
        }
        return count>0;
    }

    public Boolean IsMdnPresentAsDestination(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "IsMdnPresentAsDestination(String, persisterTxn)";
        knLogger.debug( methodName, "Entry : MDN - " , KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT COUNT(1) FROM DG.EMERGENCY_SUBSCR_DESTINFO WHERE EMERGDEST =? ";
        int count = 0;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug( methodName, "Executing query - " , query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while DG.EMERGENCY_SUBSCR_DESTINFO  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while DG.EMERGENCY_SUBSCR_DESTINFO - " ,e);
            throw KnDbUtil.processException(e, "Failed while DG.EMERGENCY_SUBSCR_DESTINFO -" + e,
                    xdmsHome, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT. mdn count in DG.EMERGENCY_SUBSCR_DESTINFO table - " , count);
        }
        return count>0;
    }

    public void insertIntoAsyncTable(KnPendingTxnInfoDTO notifyDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "insert(KnPendingTxnInfoDTO)";
        knLogger.info(methodName, " ENTRY-: KnPendingTxnInfoDTO ", notifyDTO);
        Connection conn = null;
        PreparedStatement pStmt = null;

        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            String TABLENAME = "DG.PENDING_TXN_INFO";
            String QUERY = "INSERT INTO " + TABLENAME + " (RECID,INSERTION_TIME,STATUS,TXN_ID,PRIORITY,ENTITY_TYPE,ENTITY_ID,CORPID,OPS_ID,TASK_BITSET,TASK_STATUS_BITSET,CURRENT_TASK,NOTIFY_FLAG,PROCESS_INFO,PAYLOAD,RETRY_COUNT)" +
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            long insertionTime = Instant.now().toEpochMilli();
            knLogger.debug(methodName, "query - ", QUERY);
            conn = persisterTxn.getDBConnection(xdmsHome,true);
            if (notifyDTO.getEntityIds() != null && !notifyDTO.getEntityIds().isEmpty()) {
                knLogger.debug(methodName,"EntityIds size - ",notifyDTO.getEntityIds().size());
                pStmt = conn.prepareStatement(QUERY);
                for (String entityId : notifyDTO.getEntityIds()) {
                    pStmt.setLong(1, counter.getAndIncrement());
                    pStmt.setLong(2, insertionTime);
                    pStmt.setInt(3, 0);
                    pStmt.setString(4, notifyDTO.getTxnId());
                    pStmt.setInt(5, notifyDTO.getPriority());
                    pStmt.setInt(6, notifyDTO.getEntityType());
                    pStmt.setString(7, entityId);
                    pStmt.setInt(8, notifyDTO.getCorpId());
                    pStmt.setInt(9, notifyDTO.getOpsId());
                    if (notifyDTO.getTaskBitSet() != null) {
                        pStmt.setLong(10, notifyDTO.getTaskBitSet());
                    } else {
                        pStmt.setNull(10, java.sql.Types.BIGINT);
                    }
                    if (notifyDTO.getTaskStatusBitSet() != null) {
                        pStmt.setLong(11, notifyDTO.getTaskStatusBitSet());
                    } else {
                        pStmt.setNull(11, java.sql.Types.BIGINT);
                    }
                    if (notifyDTO.getCurrentTask() != null) {
                        pStmt.setInt(12, notifyDTO.getCurrentTask());
                    } else {
                        pStmt.setNull(12, java.sql.Types.INTEGER);
                    }
                    if (notifyDTO.getNotifyFlag() != null) {
                        pStmt.setInt(13, notifyDTO.getNotifyFlag());
                    } else {
                        pStmt.setNull(13, java.sql.Types.INTEGER);
                    }
                    pStmt.setString(14, notifyDTO.getProcessInfo());
                    pStmt.setString(15, notifyDTO.getPayload());
                    pStmt.setInt(16, RETRY_COUNT);
                    pStmt.addBatch();
                }
                int[] batchResults = pStmt.executeBatch();
                knLogger.debug(methodName, "Batch executed - Total records inserted: ", batchResults.length);
            } else {
                pStmt = conn.prepareStatement(QUERY);
                pStmt.setLong(1, counter.getAndIncrement());
                pStmt.setLong(2, insertionTime);
                pStmt.setInt(3, 0);
                pStmt.setString(4, notifyDTO.getTxnId());  // (need to create uiqueid fro here or from Payload)
                pStmt.setInt(5, notifyDTO.getPriority());
                pStmt.setInt(6, notifyDTO.getEntityType());
                pStmt.setString(7, notifyDTO.getEntityId());
                pStmt.setInt(8, notifyDTO.getCorpId());
                pStmt.setInt(9, notifyDTO.getOpsId());
                if (notifyDTO.getTaskBitSet() != null) {
                    pStmt.setLong(10, notifyDTO.getTaskBitSet());
                } else {
                    pStmt.setNull(10, java.sql.Types.BIGINT);
                }
                if (notifyDTO.getTaskStatusBitSet() != null) {
                    pStmt.setLong(11, notifyDTO.getTaskStatusBitSet());
                } else {
                    pStmt.setNull(11, java.sql.Types.BIGINT);
                }
                if (notifyDTO.getCurrentTask() != null) {
                    pStmt.setInt(12, notifyDTO.getCurrentTask());
                } else {
                    pStmt.setNull(12, java.sql.Types.INTEGER);
                }
                if (notifyDTO.getNotifyFlag() != null) {
                    pStmt.setInt(13, notifyDTO.getNotifyFlag());
                } else {
                    pStmt.setNull(13, java.sql.Types.INTEGER);
                }
                pStmt.setString(14, notifyDTO.getProcessInfo());
                pStmt.setString(15, notifyDTO.getPayload());
                pStmt.setInt(16, RETRY_COUNT);
                pStmt.executeUpdate();
            }
            knLogger.debug(methodName, "Query executed successfully " + QUERY);
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
        } catch (Exception e) {
            if(ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.debug(methodName, "Exception Occured ", e);
            throw new KnDAOException("Exception :", e.getMessage(), e);
        }
        finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.info(methodName, "Exit : ");
    }


}
