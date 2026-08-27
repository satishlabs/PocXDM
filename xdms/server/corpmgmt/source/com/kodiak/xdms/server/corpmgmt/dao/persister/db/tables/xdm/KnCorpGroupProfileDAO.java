/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupProfileInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;

public class KnCorpGroupProfileDAO implements ITableDAO {

	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileDAO.class);
	private String pttServerId;
	private static final String CORPID = "CORPID";
	private static final String GRPPROFILEID = "GRPPROFILEID";
	private static final String GRPPROFILENAME = "GRPPROFILENAME";
	private static final String GRPTYPE = "GRPTYPE";
	private static final String AVATAR = "AVATAR";
	private static final String SERVICETYPE = "SERVICETYPE";
	private static final String OSMLISTID = "OSMLISTID";
	private static final String AUDIOCUTIN = "AUDIOCUTIN";
	private static final String MCXGRP = "MCXGRP";
	private static final String FEATURESALLOWED = "FEATURESALLOWED";
	private static final String CREATETIMESTAMP = "CREATETIMESTAMP";
	private static final String UPDATETIMESTAMP = "UPDATETIMESTAMP";
	private static final String GRPPROFILESTATUS = "GRPPROFILESTATUS";
	private static final String OVERRIDEDND = "OVERRIDEDND";
	private static final String GROUP_SHARED = "GROUP_SHARED";
	private static final String UGWINTEROP = "UGWINTEROP";

	KnCorpGroupProfileDAO(String pttServerId) {
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

	public KnCorpGroupProfileInfo getGroupProfileDetailById(Integer profileId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "getGroupProfileDetailById()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , profileId - ", profileId);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		KnCorpGroupProfileInfo profileInfo = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, readOnly);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SELECT_GROUP_PROFILE_BY_ID);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, corpId);
			pstmt.setInt(2, profileId);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");
			if (rs.next()) {
				profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar((Integer)rs.getObject(AVATAR));
				profileInfo.setServiceType((Integer)rs.getObject(SERVICETYPE));
				profileInfo.setOsmListId((Integer)rs.getObject(OSMLISTID));
				profileInfo.setAudioCutIn((Integer)rs.getObject(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd((Integer)rs.getObject(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				profileInfo.setUgwInterop((Integer)rs.getObject(UGWINTEROP));
			}

			knLogger.debug(methodName, "profileInfo - ", profileInfo);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return profileInfo;
	}

	public KnCorpGroupProfileInfo getGroupProfileDetailByName(String profileName, int corpId, boolean readOnly,
															  KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getGroupProfileDetailByName()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , profileName - ", profileName);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		KnCorpGroupProfileInfo profileInfo = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, readOnly);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SELECT_GROUP_PROFILE_BY_NAME);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			try {
				if (profileName != null)
					profileName = new String(profileName.getBytes("UTF-8"), "8859_1");
			} catch (UnsupportedEncodingException e1) {
				knLogger.error(methodName, e1);
			}
			pstmt.setInt(1, corpId);
			pstmt.setString(2, profileName);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");

			if (rs.next()) {
				profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar((Integer)rs.getObject(AVATAR));
				profileInfo.setServiceType((Integer)rs.getObject(SERVICETYPE));
				profileInfo.setOsmListId((Integer)rs.getObject(OSMLISTID));
				profileInfo.setAudioCutIn((Integer)rs.getObject(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd((Integer)rs.getObject(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				profileInfo.setUgwInterop((Integer)rs.getObject(UGWINTEROP));
			}

			knLogger.debug(methodName, "profileInfo - ", profileInfo);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return profileInfo;
	}

	public KnCorpGroupProfileInfo getGroupProfileDetailByIdAndHierarchyId(Integer profileId, int corpId, String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "getGroupProfileDetailByIdAndHierarchyId()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , profileId - ", profileId, " , hierarchyId - ", hierarchyId);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		KnCorpGroupProfileInfo profileInfo = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, readOnly);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SELECT_GROUP_PROFILE_BY_ID_AND_HIERARCHY_ID);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, corpId);
			pstmt.setInt(2, profileId);
			pstmt.setString(3, hierarchyId);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");
			if (rs.next()) {
				profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar((Integer)rs.getObject(AVATAR));
				profileInfo.setServiceType((Integer)rs.getObject(SERVICETYPE));
				profileInfo.setOsmListId((Integer)rs.getObject(OSMLISTID));
				profileInfo.setAudioCutIn((Integer)rs.getObject(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd((Integer)rs.getObject(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				profileInfo.setUgwInterop((Integer)rs.getObject(UGWINTEROP));
			}

			knLogger.debug(methodName, "profileInfo - ", profileInfo);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return profileInfo;
	}

	public KnCorpGroupProfileInfo getGroupProfileDetailByNameAndHierarchyId(String profileName, int corpId, String hierarchyId, boolean readOnly,
															  KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getGroupProfileDetailByNameAndHierarchyId()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , profileName - ", profileName, " , hierarchyId - ", hierarchyId);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		KnCorpGroupProfileInfo profileInfo = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, readOnly);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SELECT_GROUP_PROFILE_BY_NAME_AND_HIERARCHY_ID);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			try {
				if (profileName != null)
					profileName = new String(profileName.getBytes("UTF-8"), "8859_1");
			} catch (UnsupportedEncodingException e1) {
				knLogger.error(methodName, e1);
			}
			pstmt.setInt(1, corpId);
			pstmt.setString(2, profileName);
			pstmt.setString(3, hierarchyId);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");

			if (rs.next()) {
				profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar((Integer)rs.getObject(AVATAR));
				profileInfo.setServiceType((Integer)rs.getObject(SERVICETYPE));
				profileInfo.setOsmListId((Integer)rs.getObject(OSMLISTID));
				profileInfo.setAudioCutIn((Integer)rs.getObject(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd((Integer)rs.getObject(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				profileInfo.setUgwInterop((Integer)rs.getObject(UGWINTEROP));
			}

			knLogger.debug(methodName, "profileInfo - ", profileInfo);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return profileInfo;
	}

	public void createGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "createGroupProfile()";
        knLogger.debug(methodName, "ENTRY : groupProfilePersistDTO - ", groupProfilePersistDTO);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_GROUP_PROFILE);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupProfilePersistDTO.getGrpProfileId());
            pstmt.setInt(2, groupProfilePersistDTO.getCorpId());
            
            if (null != groupProfilePersistDTO.getGrpProfileName()) {
                try {
                    pstmt.setString(3, new String(groupProfilePersistDTO.getGrpProfileName().getBytes("UTF-8"), "8859_1"));
                } catch (UnsupportedEncodingException e) {
                    knLogger.error(methodName, "UTF-8 encoding exception - ", groupProfilePersistDTO.getGrpProfileName(), e);
                }
            }
			if (groupProfilePersistDTO.getGrpType() != null) {
				pstmt.setInt(4, groupProfilePersistDTO.getGrpType());
			} else {
				pstmt.setNull(4, java.sql.Types.INTEGER);
			}

			if (groupProfilePersistDTO.getGrpAvatar() != null) {
				pstmt.setInt(5, groupProfilePersistDTO.getGrpAvatar());
			} else {
				pstmt.setNull(5, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getGrpServiceType() != null) {
				pstmt.setInt(6, groupProfilePersistDTO.getGrpServiceType());
			} else {
				pstmt.setNull(6, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getGrpOSMListId() != null) {
				pstmt.setInt(7, Integer.valueOf(groupProfilePersistDTO.getGrpOSMListId()));
			} else {
				pstmt.setNull(7, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getAudioCutIn() != null) {
				pstmt.setInt(8, groupProfilePersistDTO.getAudioCutIn());
			} else {
				pstmt.setNull(8, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getMcxGroup() != null) {
				pstmt.setInt(9, groupProfilePersistDTO.getMcxGroup());
			} else {
				pstmt.setNull(9, java.sql.Types.INTEGER);
			}
            pstmt.setLong(10, groupProfilePersistDTO.getCreateTimeStamp());
            pstmt.setLong(11, groupProfilePersistDTO.getUpdateTimeStamp());
            pstmt.setInt(12, groupProfilePersistDTO.getGrpProfileStatus());
            pstmt.setInt(13, KnConstants.CORP_GROUP_DEFAULT_ALLOWED_FEATURE);

            if(groupProfilePersistDTO.getOverrideDND()!=null) {
				pstmt.setInt(14, groupProfilePersistDTO.getOverrideDND());
			} else {
				pstmt.setNull(14, java.sql.Types.INTEGER);
			}

			if (groupProfilePersistDTO.getGrpShared() != null) {
				pstmt.setInt(15, groupProfilePersistDTO.getGrpShared());
			} else {
				pstmt.setNull(15, java.sql.Types.INTEGER);
			}

			if (null != groupProfilePersistDTO.getUgwInterop()) {
				pstmt.setInt(16, Integer.parseInt(groupProfilePersistDTO.getUgwInterop()));
			} else {
				pstmt.setNull(16, java.sql.Types.INTEGER);
			}

            if(null != groupProfilePersistDTO.getHierarchyId()){
                pstmt.setString(17, groupProfilePersistDTO.getHierarchyId());
            }else {
                pstmt.setNull(17, Types.VARCHAR);
            }
            pstmt.execute();
            knLogger.debug( methodName, "Query executed successfully.",INSERT_GROUP_PROFILE);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to create Group profile " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
			KnDbUtil.closePreparedStatement(pstmt);
		}
	}

	public List<KnCorpGroupProfileInfo> getGroupProfileList(int corpId, Integer startIndex, Integer fetchSize, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getGroupProfileList()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , startIndex - ", startIndex, " , fetchSize - ", fetchSize);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		List<KnCorpGroupProfileInfo> groupProfileInfoList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SELECT_GROUP_PROFILE_LIST);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			if (null == startIndex) {
				startIndex = 1;
			}
			if (null == fetchSize) {
				fetchSize = 100;
			}
			pstmt.setInt(1, startIndex);
			int endIndex = startIndex + fetchSize - 1;
			pstmt.setInt(2, endIndex);
            pstmt.setInt(3, corpId);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");

			while (rs.next()) {
				KnCorpGroupProfileInfo profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar(rs.getInt(AVATAR));
				profileInfo.setServiceType(rs.getInt(SERVICETYPE));
				profileInfo.setOsmListId(rs.getInt(OSMLISTID));
				profileInfo.setAudioCutIn(rs.getInt(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd(rs.getInt(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				profileInfo.setUgwInterop((Integer)rs.getObject(UGWINTEROP));
				groupProfileInfoList.add(profileInfo);
			}

			knLogger.debug(methodName, "groupProfileInfoList - ", groupProfileInfoList);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return groupProfileInfoList;
	}

    public List<KnCorpGroupProfileInfo> getGroupProfileListByHierarchyId(int corpId, Integer startIndex, Integer fetchSize, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException {
        String methodName = "getGroupProfileListByHierarchyId()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , startIndex - ", startIndex, " , fetchSize - ", fetchSize, " ,hierarchyId - ",hierarchyId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnCorpGroupProfileInfo> groupProfileInfoList = new ArrayList<KnCorpGroupProfileInfo>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_PROFILE_LIST_BY_HIERARCHY_ID);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            if (null == startIndex) {
                startIndex = 1;
            }
            if (null == fetchSize) {
                fetchSize = 100;
            }
            pstmt.setInt(1, startIndex);
            int endIndex = startIndex + fetchSize - 1;
            pstmt.setInt(2, endIndex);
            pstmt.setInt(3, corpId);
            pstmt.setString(4,hierarchyId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");

            while (rs.next()) {
                KnCorpGroupProfileInfo profileInfo = new KnCorpGroupProfileInfo();
                profileInfo.setCorpId(rs.getInt(CORPID));
                profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
                String name = rs.getString(GRPPROFILENAME);
                try {
                    name = new String((name).getBytes("8859_1"), "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                    knLogger.error(methodName, e);
                }
                profileInfo.setProfileName(name);
                profileInfo.setGrpType(rs.getInt(GRPTYPE));
                profileInfo.setAvatar(rs.getInt(AVATAR));
                profileInfo.setServiceType(rs.getInt(SERVICETYPE));
                profileInfo.setOsmListId(rs.getInt(OSMLISTID));
                profileInfo.setAudioCutIn(rs.getInt(AUDIOCUTIN));
                profileInfo.setMcxGrp(rs.getInt(MCXGRP));
                profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
                profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
                profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
                profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
                profileInfo.setOverrideDnd(rs.getInt(OVERRIDEDND));
                profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
                profileInfo.setUgwInterop((Integer)rs.getObject(UGWINTEROP));
                groupProfileInfoList.add(profileInfo);
            }

            knLogger.debug(methodName, "groupProfileInfoList - ", groupProfileInfoList);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupProfileInfoList;
    }

	public int getGroupProfileCountByCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getGroupProfileCountByCorpId()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		int profileCount = 0;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SELECT_GROUP_PROFILE_COUNT_BY_CORP_ID);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, corpId);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");
			if (rs.next()) {
				
				profileCount=rs.getInt(1);
				
			}

			knLogger.debug(methodName, "profileCount - ", profileCount);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return profileCount;
	}

    public int getGroupProfileCountByCorpIdByHierarchyId(int corpId, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException {
        String methodName = "getGroupProfileCountByCorpId()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int profileCount = 0;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_PROFILE_COUNT_BY_CORP_ID_BY_HIERARCHY_ID);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2,hierarchyId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            if (rs.next()) {

                profileCount=rs.getInt(1);

            }

            knLogger.debug(methodName, "profileCount - ", profileCount);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return profileCount;
    }

	/**
	 *
	 * @param corpId
	 * @param startIndex
	 * @param fetchSize
	 * @param grpType
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	public List<KnCorpGroupProfileInfo> searchGroupProfileByGpType(int corpId, Integer startIndex, Integer fetchSize, Integer grpType,
                                                                   KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
		String methodName = "searchGroupProfileByGpType()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , startIndex - ", startIndex, " , fetchSize - ", fetchSize," grpType - ",grpType);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		List<KnCorpGroupProfileInfo> groupProfileInfoList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SEARCH_GROUP_PROFILE_LIST_BYTYPE);
			if (null != hierarchyId){
				query = query.replace("ORDER BY", " AND HIERARCHY_ID = ? ORDER BY ");
			}
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, startIndex);
			int endIndex = startIndex + fetchSize-1;
			knLogger.debug(methodName," endIndex -",endIndex);
			pstmt.setInt(2, endIndex);
			pstmt.setInt(3, corpId);
			pstmt.setInt(4, grpType);
			if (null != hierarchyId){
				pstmt.setString(5, hierarchyId);
			}
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");

			while (rs.next()) {
				KnCorpGroupProfileInfo profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar(rs.getInt(AVATAR));
				profileInfo.setServiceType(rs.getInt(SERVICETYPE));
				profileInfo.setOsmListId(rs.getInt(OSMLISTID));
				profileInfo.setAudioCutIn(rs.getInt(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd(rs.getInt(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				groupProfileInfoList.add(profileInfo);
			}

			knLogger.debug(methodName, "groupProfileInfoList - ", groupProfileInfoList);
		} catch (Exception e) {
			knLogger.error( methodName, "Exception  occurred while search Group Profile By GpType ",e);
			throw KnDbUtil.processException(e, "Failed to search Group Profile By GpType " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return groupProfileInfoList;
	}

	/**
	 *
	 * @param corpId
	 * @param startIndex
	 * @param fetchSize
	 * @param grpType
	 * @param grpProfileName
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	public List<KnCorpGroupProfileInfo> searchGroupProfileByNameAndType(int corpId, Integer startIndex, Integer fetchSize, Integer grpType,
                                                                        String grpProfileName, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
		String methodName = "searchGroupProfileByGpType()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , startIndex - ", startIndex, " , fetchSize - ", fetchSize," " +
				"grpType - ",grpType," grpProfileName - ",grpProfileName);

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		String gpProfileName = grpProfileName.replace("!", "!!").replace("_", "!_").replace("%", "!%");
		List<KnCorpGroupProfileInfo> groupProfileInfoList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SEARCH_GROUP_PROFILE_LIST_BYNAME_BYTYPE);
			if (null != hierarchyId){
				query = query.replace("ORDER BY", "AND HIERARCHY_ID = ? ORDER BY");
			}
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, startIndex);
			int endIndex = startIndex + fetchSize-1;
			pstmt.setInt(2, endIndex);
			pstmt.setInt(3, corpId);
			pstmt.setInt(4, grpType);
			pstmt.setString(5,"%"+gpProfileName.toUpperCase()+"%" );
			if (null != hierarchyId){
				pstmt.setString(6, hierarchyId);
			}
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");

			while (rs.next()) {
				KnCorpGroupProfileInfo profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar(rs.getInt(AVATAR));
				profileInfo.setServiceType(rs.getInt(SERVICETYPE));
				profileInfo.setOsmListId(rs.getInt(OSMLISTID));
				profileInfo.setAudioCutIn(rs.getInt(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd(rs.getInt(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				groupProfileInfoList.add(profileInfo);
			}
			knLogger.debug(methodName, "groupProfileInfoList - ", groupProfileInfoList);
		} catch (Exception e) {
			knLogger.error( methodName, "Exception  occurred while search GroupProfile By Name And Type  ",e);
			throw KnDbUtil.processException(e, "Failed to search GroupProfile By Name And Type " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return groupProfileInfoList;
	}

	/**
	 *
	 * @param corpId
	 * @param startIndex
	 * @param fetchSize
	 * @param grpProfileName
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	public List<KnCorpGroupProfileInfo> searchGroupProfileByProfileName(int corpId, Integer startIndex, Integer fetchSize,
                                                                        String grpProfileName, KnPersisterTxn persisterTxn,
																		String hierarchyId) throws KnDAOException {
		String methodName = "searchGroupProfileByProfileName()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , startIndex - ", startIndex, " , fetchSize - ", fetchSize," grpProfileName" ,grpProfileName);

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		String gpProfileName = grpProfileName.replace("!", "!!").replace("_", "!_").replace("%", "!%");
		List<KnCorpGroupProfileInfo> groupProfileInfoList = new ArrayList<KnCorpGroupProfileInfo>();
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SEARCH_GROUP_PROFILE_LIST_BYNAME);
			if (null != hierarchyId){
				query = query.replace("ORDER BY", " AND HIERARCHY_ID = ? ORDER BY ");
			}
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, startIndex);
			int endIndex = startIndex + fetchSize-1;
			pstmt.setInt(2, endIndex);
			pstmt.setInt(3, corpId);
			pstmt.setString(4,"%"+gpProfileName.toUpperCase()+"%" );
			if (null != hierarchyId){
				pstmt.setString(5, hierarchyId);
			}
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");

			while (rs.next()) {
				KnCorpGroupProfileInfo profileInfo = new KnCorpGroupProfileInfo();
				profileInfo.setCorpId(rs.getInt(CORPID));
				profileInfo.setProfileId(rs.getInt(GRPPROFILEID));
				String name = rs.getString(GRPPROFILENAME);
				try {
					name = new String((name).getBytes("8859_1"), "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, e);
				}
				profileInfo.setProfileName(name);
				profileInfo.setGrpType(rs.getInt(GRPTYPE));
				profileInfo.setAvatar(rs.getInt(AVATAR));
				profileInfo.setServiceType(rs.getInt(SERVICETYPE));
				profileInfo.setOsmListId(rs.getInt(OSMLISTID));
				profileInfo.setAudioCutIn(rs.getInt(AUDIOCUTIN));
				profileInfo.setMcxGrp(rs.getInt(MCXGRP));
				profileInfo.setFeatureAllowed(rs.getInt(FEATURESALLOWED));
				profileInfo.setCreateTimeStamp(rs.getLong(CREATETIMESTAMP));
				profileInfo.setUpdateTimeStamp(rs.getLong(UPDATETIMESTAMP));
				profileInfo.setGrpProfileStatus(rs.getInt(GRPPROFILESTATUS));
				profileInfo.setOverrideDnd(rs.getInt(OVERRIDEDND));
				profileInfo.setGrpShared(rs.getInt(GROUP_SHARED));
				groupProfileInfoList.add(profileInfo);
			}
			knLogger.debug(methodName, "groupProfileInfoList - ", groupProfileInfoList);
		} catch (Exception e) {
			knLogger.error( methodName, "Exception  occurred while search GroupProfile By ProfileName ",e);
			throw KnDbUtil.processException(e, "Failed to search GroupProfile By ProfileName " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return groupProfileInfoList;
	}

	public void updateGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "updateGroupProfile()";
		knLogger.debug(methodName, "ENTRY : - ", groupProfilePersistDTO);
		PreparedStatement pstmt = null;
		String query = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(UPDATE_GROUP_PROFILE);
			knLogger.debug( methodName, "query -", query);
			//UPDATE DG.CORPGROUPPROFILE SET GRPPROFILENAME = ?, AVATAR = ?, SERVICETYPE = ?, OSMLISTID = ?, AUDIOCUTIN = ?,
			// UPDATETIMESTAMP = ?, FEATURESALLOWED = ?, OVERRIDEDND = ? WHERE GRPPROFILEID = ?
			pstmt = conn.prepareStatement(query);
			if (null != groupProfilePersistDTO.getGrpProfileName()) {
				try {
					knLogger.debug(methodName, "GroupProfileName :: ",groupProfilePersistDTO.getGrpProfileName());
					pstmt.setString(1, new String(groupProfilePersistDTO.getGrpProfileName().getBytes("UTF-8"), "8859_1"));
				} catch (UnsupportedEncodingException e) {
					knLogger.error(methodName, "UTF-8 encoding exception - ", groupProfilePersistDTO.getGrpProfileName(), e);
				}
			}
			if (groupProfilePersistDTO.getGrpAvatar() != null) {
				pstmt.setInt(2, groupProfilePersistDTO.getGrpAvatar());
			} else {
				pstmt.setNull(2, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getGrpServiceType() != null) {
				pstmt.setInt(3, groupProfilePersistDTO.getGrpServiceType());
			} else {
				pstmt.setNull(3, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getGrpOSMListId() != null) {
				pstmt.setInt(4, Integer.valueOf(groupProfilePersistDTO.getGrpOSMListId()));
			} else {
				pstmt.setNull(4, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getAudioCutIn() != null) {
				pstmt.setInt(5, groupProfilePersistDTO.getAudioCutIn());
			} else {
				pstmt.setNull(5, java.sql.Types.INTEGER);
			}
			pstmt.setLong(6, groupProfilePersistDTO.getUpdateTimeStamp());

			pstmt.setInt(7, groupProfilePersistDTO.getFeatureAllowed());

			if (groupProfilePersistDTO.getOverrideDND() != null) {
				pstmt.setInt(8, groupProfilePersistDTO.getOverrideDND());
			} else {
				pstmt.setNull(8, java.sql.Types.INTEGER);
			}
			if (groupProfilePersistDTO.getGrpShared() != null) {
				pstmt.setInt(9, groupProfilePersistDTO.getGrpShared());
			} else {
				pstmt.setNull(9, java.sql.Types.INTEGER);
			}
			//Setting for ugwInterop
			if (null != groupProfilePersistDTO.getUgwInterop()) {
				knLogger.debug(methodName, "UgwInterop :: ", groupProfilePersistDTO.getUgwInterop());
				pstmt.setInt(10, Integer.parseInt(groupProfilePersistDTO.getUgwInterop()));
			} else {
				knLogger.debug(methodName, "UgwInterop :: ", java.sql.Types.INTEGER);
				pstmt.setNull(10, java.sql.Types.INTEGER);
			}
			pstmt.setInt(11, groupProfilePersistDTO.getGrpProfileId());

			pstmt.execute();
			knLogger.debug( methodName, "Query executed successfully.", UPDATE_GROUP_PROFILE);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to create Group profile " + e,
					pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closePreparedStatement(pstmt);
		}
	}

	/**
	 *
	 * @param profileId
	 * @param persisterTxn
	 * @throws KnDAOException
	 */
	public void deleteGroupProfile(Integer profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "deleteGroupProfile()";
		knLogger.debug(methodName, "ENTRY : - ", profileId);
		PreparedStatement pstmt = null;
		String query = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(DELETE_GROUP_PROFILE);
			knLogger.debug( methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,profileId );
			int count  = pstmt.executeUpdate();
			knLogger.debug( methodName, "No.of records deleted - ", count);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to delete Group profile " + e,
					pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closePreparedStatement(pstmt);
		}
	}

	/**
	 *
	 * @param corpId
	 * @param osmListId
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	public Set<Integer> getGroupProfileDetailByOsmListId(int corpId,int osmListId,KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "getGroupProfileDetailByOsmListId()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " , profileId - ", osmListId);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		Set<Integer> groupProfileIds = new HashSet<>();
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(SELECT_GROUP_PROFILE_BY_OSMLIST_ID);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, corpId);
			pstmt.setInt(2, osmListId);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");
			while (rs.next()) {
				int groupProfileId =  rs.getInt(GRPPROFILEID);
				groupProfileIds.add(groupProfileId);
			}

			knLogger.debug(methodName, "groupProfileIds - ", groupProfileIds);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return groupProfileIds;
	}

	/**
	 *
	 * @param osmListId
	 * @param corpId
	 * @param persisterTxn
	 * @throws KnDAOException
	 */
	public void deleteOSMListFromProfile(int osmListId,int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "deleteOSMListFromProfile()";
		knLogger.debug(methodName, "ENTRY : - ", osmListId);
		PreparedStatement pstmt = null;
		String query = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(DELETE_OSMLIST_ID_FROM_GRP_PROFILE);
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, corpId);
			pstmt.setInt(2, osmListId);

			int count = pstmt.executeUpdate();
			knLogger.debug(methodName, "Query executed successfully.", count);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to create Group profile " + e,
					pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closePreparedStatement(pstmt);
		}
	}

	/**
	 *
	 * @param corpId
	 * @param grpType
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	public int getGroupProfileCountByGrpType(int corpId, Integer grpType, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
		String methodName = "getGroupProfileCountByName()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " grpType" ,grpType);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		int count = 0;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(GET_GROUPPROFILE_CNT_BY_TYPE);
			if (null != hierarchyId){
				query = query.concat(" AND HIERARCHY_ID = ? ");
			}
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, corpId);
			pstmt.setInt(2, grpType);
			if (null != hierarchyId){
				pstmt.setString(3, hierarchyId);
			}
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");
			while (rs.next()) {
				count = rs.getInt(1);
			}
			knLogger.debug(methodName, "count - ", count);
		} catch (Exception e) {
			knLogger.error( methodName, "Exception  occurred get Group Profile Count By Type ",e);
			throw KnDbUtil.processException(e, "Failed to get count of  GroupProfile By Type " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return count;
	}

	/**
	 *
	 * @param corpId
	 * @param grpProfileName
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	public int getGroupProfileCountByName(int corpId, String grpProfileName, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
		String methodName = "getGroupProfileCountByName()";
		knLogger.debug(methodName, "ENTRY : corpId - ", corpId, " grpProfileName" ,grpProfileName);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		String gpProfileName = grpProfileName.replace("!", "!!").replace("_", "!_").replace("%", "!%");
		int count = 0;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(GET_GROUPPROFILE_CNT_BY_NAME);
			if (null != hierarchyId){
				query = query.concat(" AND HIERARCHY_ID = ? ");
			}
			knLogger.debug(methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, corpId);
			pstmt.setString(2,"%"+gpProfileName.toUpperCase()+"%" );
			if (null != hierarchyId){
				pstmt.setString(3, hierarchyId);
			}
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");
			while (rs.next()) {
				count = rs.getInt(1);
			}
			knLogger.debug(methodName, "count - ", count);
		} catch (Exception e) {
			knLogger.error( methodName, "Exception  occurred get Group Profile Count By Name ",e);
			throw KnDbUtil.processException(e, "Failed to get count of  GroupProfile By ProfileName " + e, pttServerId,
					KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closePreparedStatement(pstmt);
		}
		return count;
	}

	public void deleteGroupProfileByCorpId(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "deleteGroupProfileByCorpId(Integer)";
		knLogger.debug(methodName, "ENTRY  corpId: - ", corpId);
		PreparedStatement pstmt = null;
		String query = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, true);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(DELETE_CORPGROUPPROFILE_BY_CORPID);
			knLogger.debug( methodName, "query -", query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,corpId );
			int deletedGroupProfiles  = pstmt.executeUpdate();
			knLogger.debug( methodName, "deletedGroupProfiles - ", deletedGroupProfiles);
		} catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed to delete Group profile " + e,
					pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
		} finally {
			KnDbUtil.closePreparedStatement(pstmt);
		}
	}
}
