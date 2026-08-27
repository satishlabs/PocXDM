/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsAddlInfoProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAddlInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsAddlInfoPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.*;
import java.util.*;

/**
 * Created by hanwar on 24-02-2017.
 * File name:   KnPOCSubscrAddlInfoDAO.java
 * Subsystem:   Provisioning Library
 */
public class KnPOCSubscrAddlInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnPOCSubscrAddlInfoDAO.class);

    private static final String className = com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnPOCSubscrAddlInfoDAO.class.getName();
    private String pttServerId;
    private static final String MDN = "MDN";
    private static final String TIME_SLOT_TYPE = "TIME_SLOT_TYPE";
    private static final String DRX_VALUE = "DRX_VALUE";
    private static final String DRX_THRESHOLD_COUNT = "DRX_THRESHOLD_COUNT";
    private static final String DRX_MO = "DRX_MO";
    private static final String DRX_MT = "DRX_MT";
    private static final String CALL_HISTORY_DURATION = "CALL_HISTORY_DURATION";
    private static final String DATA_PURGE_DURATION = "DATA_PURGE_DURATION";
    private static final String FIXEDTS_START_TIME = "FIXEDTS_START_TIME";
    private static final String FIXEDTS_END_TIME = "FIXEDTS_END_TIME";
    private static final String TABLENAME = "DG.POCSUBSCR_ADDLINFO";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ?";
    private static final String TIER_PKG_CODE = "TIER_PKG_CODE";
    private static final String DATA_PKG_ID = "DATA_PKG_ID";
    private static final String ONBOARDINGMAILS_REQ = "ONBOARDINGMAILS_REQ";
    private static final String PTT_SETTING_DOCID = "PTT_SETTING_DOCID";
    private static final String QRY_UPDATE_POCSUBSCR_ADDLINFO = "UPDATE DG.POCSUBSCR_ADDLINFO SET TIER_PKG_CODE = ?, DATA_PKG_ID = ?,ONBOARDINGMAILS_REQ = ? WHERE MDN = ? ";
    private static final String QRY_UPDATE_POCSUBSCR_ADDLINFO_ON_BOARD = "UPDATE DG.POCSUBSCR_ADDLINFO SET ONBOARDINGMAILS_REQ = ? WHERE MDN = ? ";
    private static final String QRY_INSERT_POCSUBSCR_ADDLINFO = "INSERT INTO DG.POCSUBSCR_ADDLINFO (MDN, TIME_SLOT_TYPE, TIER_PKG_CODE, DATA_PKG_ID, ONBOARDINGMAILS_REQ,USER_PROFILE_NAME) VALUES(?,?,?,?,?,?)";
    private static final String QRY_UPDATE_USER_PROFILE_NAME = "UPDATE DG.POCSUBSCR_ADDLINFO SET USER_PROFILE_NAME = ? WHERE MDN = ? ";
    private static final String QRY_SELECT_USER_PROFILE_NAME = "SELECT USER_PROFILE_NAME FROM  DG.POCSUBSCR_ADDLINFO WHERE MDN = ? ";
    private static final String QRY_SELECT_USER_PROFILE_NAME_LIST = "SELECT MDN,USER_PROFILE_NAME FROM  DG.POCSUBSCR_ADDLINFO WHERE MDN IN ";
    private static final String QRY_SELECT_RECORDING_STATUS ="SELECT RECORDING_STATUS FROM DG.POCSUBSCR_ADDLINFO WHERE MDN = ? ";

    private static final String RECORDING_STATUS = "RECORDING_STATUS";
    private static final String QRY_INSERT_POCSUBSCR_ADDLINFO_SUBSCR_CLIENT_SETTINGS ="INSERT INTO DG.POCSUBSCR_ADDLINFO (TIME_SLOT_TYPE,MDN, RECORDING_STATUS) VALUES(?,?,?)";
    private static final String QRY_SELECT_MDN = "SELECT MDN,RECORDING_STATUS FROM  DG.POCSUBSCR_ADDLINFO WHERE MDN = ?";
    private static final String QRY_UPDATE_SUBSCR_CLIENT_SETTINGS = "UPDATE DG.POCSUBSCR_ADDLINFO SET RECORDING_STATUS = ? WHERE MDN = ? ";
    private static final String QRY_UPDATE_POCSUBSCR_ADDLINFO_FOR_PROFILE_MDN = "UPDATE DG.POCSUBSCR_ADDLINFO SET TIER_PKG_CODE = ?, DATA_PKG_ID = ?, ONBOARDINGMAILS_REQ = ? WHERE MDN IN(PROFILEMDNLIST)";

    public KnPOCSubscrAddlInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }
    private static final String USER_PROFILE_NAME = "USER_PROFILE_NAME";


    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    /**
     * method to delete the subscriber profile
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persistTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    /**
     * method to delete the subscriber addlInfo profile
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persistTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void deleteSubsAddlInfo(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "deleteSubsAddlInfo(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: Delete Subscriber Profile from SUBSCR_ADDL_INFO");
        try {

            KnSubsAddlInfoPersistDTO subsInfoPersistDTO = (KnSubsAddlInfoPersistDTO) persistenceDTO;
            String mdn = subsInfoPersistDTO.getMdn();

            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Subscriber Profile from subsaddlinfo - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void deleteSubsAddlInfo(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "deleteSubsAddlInfo(List<String>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: Delete Subscriber Profile");
        try {
            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing - ", query);
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed mdns count:", count);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void createSubsAddlInfoProfile(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubsAddlInfoProfile(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        KnSubsAddlInfoPersistDTO subsInfoPersistDto = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: Create Subscriber AddlInfo Profile with DTO - ", persistenceDTO);

        try {
            subsInfoPersistDto = (KnSubsAddlInfoPersistDTO) persistenceDTO;
            String mdn = subsInfoPersistDto.getMdn();
            int timeSlotType = subsInfoPersistDto.getTimeSlotType();
            int drxValue = subsInfoPersistDto.getDrxValue();
            int drxThresCount = subsInfoPersistDto.getDrxThreshCount();
            int drxMO = subsInfoPersistDto.getDrxMO();
            int drxMT = subsInfoPersistDto.getDrxMT();
            int callHistory = subsInfoPersistDto.getCallHistoryDuration();
            int dataPurge = subsInfoPersistDto.getDataPurgeDuration();
            long fixedStartTime = subsInfoPersistDto.getfixedtsStartTime();
            long fixedEndTime = subsInfoPersistDto.getFixedtsEndTime();
            String tierPkgCode=subsInfoPersistDto.getTierPkgCode();
            int dataPkgId=subsInfoPersistDto.getDataPkgId();
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(MDN);
            queryFields.add(TIME_SLOT_TYPE);
            queryFields.add(DRX_VALUE);
            queryFields.add(DRX_THRESHOLD_COUNT);
            queryFields.add(DRX_MO);
            queryFields.add(DRX_MT);
            queryFields.add(CALL_HISTORY_DURATION);
            queryFields.add(DATA_PURGE_DURATION);
            queryFields.add(FIXEDTS_START_TIME);
            queryFields.add(FIXEDTS_END_TIME);
            queryFields.add(TIER_PKG_CODE);
            queryFields.add(DATA_PKG_ID);
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "ccreateSubsAddlInfoProfile : ", conn);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0; // will dynamically update the column index value as per the received values.
            pStmt.setString(++columnIndex, mdn);
            pStmt.setInt(++columnIndex, timeSlotType);
            pStmt.setInt(++columnIndex, drxValue);
            pStmt.setInt(++columnIndex, drxThresCount);
            pStmt.setInt(++columnIndex, drxMO);
            pStmt.setInt(++columnIndex, drxMT);

            pStmt.setInt(++columnIndex, callHistory);
            pStmt.setInt(++columnIndex, dataPurge);

            pStmt.setLong(++columnIndex, fixedStartTime);
            pStmt.setLong(++columnIndex, fixedEndTime);
            pStmt.setString(++columnIndex, tierPkgCode);
            pStmt.setInt(++columnIndex, dataPkgId);

            knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to create Subscriber AddlInfo Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Not Implemented");
        return null;
    }


    /**
     * method to retrieve the Subscriber AddlInfo Profile
     *
     * @param mdn        String
     * @param persistTxn KnPersisterTxn
     * @return KnOPSubsAddlInfoProfileDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnOPSubsAddlInfoProfileDTO selectSubscriberAddlProfile(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberAddlProfile(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsAddlInfoProfileDTO subsProfileInfoDto = null;
        knLogger.debug(methodName, "ENTRY: Select Subscriber AddlInfo Profile ");
        try {
            StringBuilder strBuffer = new StringBuilder();
            strBuffer.append("SELECT ");
            strBuffer.append(MDN).append(", ")
                    .append(TIME_SLOT_TYPE).append(", ")
                    .append(DRX_VALUE).append(", ")
                    .append(DRX_THRESHOLD_COUNT).append(", ")
                    .append(DRX_MO).append(", ")
                    .append(DRX_MT).append(", ")
                    .append(CALL_HISTORY_DURATION).append(", ")
                    .append(DATA_PURGE_DURATION).append(", ")
                    .append(FIXEDTS_START_TIME).append(", ")
                    .append(FIXEDTS_END_TIME).append(", ")
                    .append(TIER_PKG_CODE).append(", ")
                    .append(DATA_PKG_ID).append(", ")
                    .append(ONBOARDINGMAILS_REQ).append(", ")
                    .append(PTT_SETTING_DOCID);
            strBuffer.append(" FROM ").append(TABLENAME).append(" WHERE ");
            strBuffer.append(MDN).append("=?");

            query = strBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "selectSubscriberAddlInfoProfile ... ");

            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                subsProfileInfoDto = new KnOPSubsAddlInfoProfileDTO();
                subsProfileInfoDto.setMdn(rs.getString(MDN));
                subsProfileInfoDto.setTimeSlotType(rs.getInt(TIME_SLOT_TYPE));
                subsProfileInfoDto.setDrxValue(rs.getInt(DRX_VALUE));
                subsProfileInfoDto.setDrxThreshCount(rs.getInt(DRX_THRESHOLD_COUNT));
                subsProfileInfoDto.setDrxMO(rs.getInt(DRX_MO));
                subsProfileInfoDto.setDrxMT(rs.getInt(DRX_MT));
                subsProfileInfoDto.setCallHistoryDuration(rs.getInt(CALL_HISTORY_DURATION));
                subsProfileInfoDto.setDataPurgeDuration(rs.getInt(DATA_PURGE_DURATION));
                subsProfileInfoDto.setfixedtsStartTime(rs.getLong(FIXEDTS_START_TIME));
                subsProfileInfoDto.setFixedtsEndTime(rs.getLong(FIXEDTS_END_TIME));
                subsProfileInfoDto.setDataPkgId(rs.getInt(DATA_PKG_ID));
                subsProfileInfoDto.setTierPkgCode(rs.getString(TIER_PKG_CODE));
                subsProfileInfoDto.setOnBoardingMailReqd(rs.getInt(ONBOARDINGMAILS_REQ));
                subsProfileInfoDto.setPttSettingDocId(rs.getString(PTT_SETTING_DOCID));
            } else {
                knLogger.info(methodName, "Subscriber AddlInfo Profile doesn't exist");
            }
            knLogger.debug(methodName, "returning subscriber AddlInfo Profile Info ", subsProfileInfoDto);
            return subsProfileInfoDto;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber AddlInfo Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber AddlInfo Profile");
        }
    }

    public Map<String, KnSubsAddlInfoDTO> selectSubscriberAddlProfile(List<String> mdns, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberAddlProfile(List<String> mdns,boolean readOnly, KnPersisterTxn persistTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String, KnSubsAddlInfoDTO> subsAddlInfoDTOMap = new HashMap<>();
        knLogger.debug(methodName, "ENTRY: Select Subscriber AddlInfo Profiles ", KnGDPRTemplate.mdnList(mdns), "readOnly :", readOnly);
        try {
            StringBuilder strBuffer = new StringBuilder();
            strBuffer.append("SELECT ");
            strBuffer.append(MDN).append(", ")
                    .append(TIME_SLOT_TYPE).append(", ")
                    .append(DRX_VALUE).append(", ")
                    .append(DRX_THRESHOLD_COUNT).append(", ")
                    .append(DRX_MO).append(", ")
                    .append(DRX_MT).append(", ")
                    .append(CALL_HISTORY_DURATION).append(", ")
                    .append(DATA_PURGE_DURATION).append(", ")
                    .append(FIXEDTS_START_TIME).append(", ")
                    .append(FIXEDTS_END_TIME).append(", ")
                    .append(TIER_PKG_CODE).append(", ")
                    .append(DATA_PKG_ID);
            strBuffer.append(" FROM ").append(TABLENAME).append(" WHERE ").append(MDN).append(" IN (");
            for(int i=0;i<mdns.size();i++){
                strBuffer.append("?,");
            }
            strBuffer.deleteCharAt(strBuffer.length()-1);
            strBuffer.append(")");

            query = strBuffer.toString();
            knLogger.debug(methodName, "Query: Executing - ", query);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(query);
            int i=0;
            for (String mdn : mdns) {
                pStmt.setString(++i, mdn);
            }

            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                KnSubsAddlInfoDTO subsProfile = new KnSubsAddlInfoDTO();
                subsProfile.setMdn(rs.getString(MDN));
                subsProfile.setTimeSlotType(rs.getInt(TIME_SLOT_TYPE));
                subsProfile.setDrxValue(rs.getInt(DRX_VALUE));
                subsProfile.setDrxThreshCount(rs.getInt(DRX_THRESHOLD_COUNT));
                subsProfile.setDrxMO(rs.getInt(DRX_MO));
                subsProfile.setDrxMT(rs.getInt(DRX_MT));
                subsProfile.setCallHistoryDuration(rs.getInt(CALL_HISTORY_DURATION));
                subsProfile.setDataPurgeDuration(rs.getInt(DATA_PURGE_DURATION));
                subsProfile.setfixedtsStartTime(rs.getLong(FIXEDTS_START_TIME));
                subsProfile.setFixedtsEndTime(rs.getLong(FIXEDTS_END_TIME));
                subsProfile.setDataPkgId(rs.getInt(DATA_PKG_ID));
                subsProfile.setTierPkgCode(rs.getString(TIER_PKG_CODE));
                subsAddlInfoDTOMap.put(subsProfile.getMdn().trim(), subsProfile);
            }


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber AddlInfo Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber AddlInfo Profile");
        }

        return subsAddlInfoDTOMap;

    }

    public void updateSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:subsProfilePersistDTO", subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_UPDATE_POCSUBSCR_ADDLINFO);
            pStmt.setString(1, subsProfilePersistDTO.getTierPkgCode());
            pStmt.setInt(2, subsProfilePersistDTO.getDataPkgId());
            if (subsProfilePersistDTO.getOnBoardingMailReqd() != null) {
                pStmt.setInt(3, subsProfilePersistDTO.getOnBoardingMailReqd());
            } else {
                pStmt.setNull(3, java.sql.Types.INTEGER);
            }
            pStmt.setString(4, subsProfilePersistDTO.getMdn());
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_UPDATE_POCSUBSCR_ADDLINFO);
            pStmt.execute();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrPkgAddlInfo - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_UPDATE_POCSUBSCR_ADDLINFO);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
    }

    public void updateSubscrPkgAddlInfoForProfileMdn(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateSubscrPkgAddlInfoforProfileMdn(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY:subsProfilePersistDTO", subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            List<String> profileMDNlist = subsProfilePersistDTO.getProfilemdn();
            var profileMdnListArray = new ArrayList<>(profileMDNlist);
            var profileMdnList = KnGeneralUtil.splitList(profileMdnListArray, KnConstants.BULK_UPDATE_SIZE);
            for (var profileMdn : profileMdnList) {
                int index = 1;
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = KnGeneralUtil.replaceContactWithValue(QRY_UPDATE_POCSUBSCR_ADDLINFO_FOR_PROFILE_MDN, "PROFILEMDNLIST", com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(profileMdn));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                pStmt.setString(index++, subsProfilePersistDTO.getTierPkgCode());
                pStmt.setInt(index++, subsProfilePersistDTO.getDataPkgId());
                if (null != subsProfilePersistDTO.getOnBoardingMailReqd()) {
                    pStmt.setInt(index++, subsProfilePersistDTO.getOnBoardingMailReqd());
                } else {
                    pStmt.setNull(index++, java.sql.Types.INTEGER);
                }
                for (String mdn : profileMdn) {
                    pStmt.setString(index++, mdn);
                }
                knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
                pStmt.execute();
            }
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrPkgAddlInfo - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_UPDATE_POCSUBSCR_ADDLINFO);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "EXIT : Updated the pkg info for profileMdns");
        }
    }

    public void updateSubscrOnBoardingMail(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateSubscrOnBoardingMail(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:subsProfilePersistDTO", subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_UPDATE_POCSUBSCR_ADDLINFO_ON_BOARD);
            if (subsProfilePersistDTO.getOnBoardingMailReqd() != null) {
                pStmt.setInt(1, subsProfilePersistDTO.getOnBoardingMailReqd());
            } else {
                pStmt.setNull(1, java.sql.Types.INTEGER);
            }
            pStmt.setString(2, subsProfilePersistDTO.getMdn());
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_UPDATE_POCSUBSCR_ADDLINFO_ON_BOARD);
            pStmt.execute();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrOnBoardingMail - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_UPDATE_POCSUBSCR_ADDLINFO_ON_BOARD);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
    }

    public void createSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "createSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:subsProfilePersistDTO", subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_INSERT_POCSUBSCR_ADDLINFO);
            pStmt.setString(1, subsProfilePersistDTO.getMdn());
            pStmt.setInt(2, subsProfilePersistDTO.getTimeSlotType());
            pStmt.setString(3, subsProfilePersistDTO.getTierPkgCode());
            pStmt.setInt(4, subsProfilePersistDTO.getDataPkgId());
            if (subsProfilePersistDTO.getOnBoardingMailReqd() != null) {
                pStmt.setInt(5, subsProfilePersistDTO.getOnBoardingMailReqd());
            } else {
                pStmt.setNull(5, java.sql.Types.INTEGER);
            }
            if(subsProfilePersistDTO.getUserProfileName() != null){
                pStmt.setString(6,subsProfilePersistDTO.getUserProfileName());
            }else{
                pStmt.setNull(6, Types.NVARCHAR);
            }
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_INSERT_POCSUBSCR_ADDLINFO);
            pStmt.execute();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to createSubscrPkgAddlInfo - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_INSERT_POCSUBSCR_ADDLINFO);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
    }

    public void createBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName="createBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY:subsProfilePersistDTO",subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_INSERT_POCSUBSCR_ADDLINFO);

            for (String mdn : subsProfilePersistDTO.getMdns()) {
                pStmt.setString(1, mdn);
                pStmt.setInt(2,subsProfilePersistDTO.getTimeSlotType() );
                pStmt.setString(3,subsProfilePersistDTO.getTierPkgCode());
                pStmt.setInt(4,subsProfilePersistDTO.getDataPkgId());
                if (subsProfilePersistDTO.getOnBoardingMailReqd() != null) {
                    pStmt.setInt(5, subsProfilePersistDTO.getOnBoardingMailReqd());
                } else {
                    pStmt.setNull(5, java.sql.Types.INTEGER);
                }
                if(subsProfilePersistDTO.getUserProfileName() != null){
                    pStmt.setString(6,subsProfilePersistDTO.getUserProfileName());
                }else{
                    pStmt.setNull(6, Types.NVARCHAR);
                }
                pStmt.addBatch();
            }

            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_INSERT_POCSUBSCR_ADDLINFO);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");

        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to createBulkSubscrPkgAddlInfo - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_INSERT_POCSUBSCR_ADDLINFO);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
    }

    public void updateBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName="updateBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY:subsProfilePersistDTO",subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_UPDATE_POCSUBSCR_ADDLINFO);
            for (String mdn : subsProfilePersistDTO.getMdns()) {
                pStmt.setString(1,subsProfilePersistDTO.getTierPkgCode());
                pStmt.setInt(2,subsProfilePersistDTO.getDataPkgId());
                if (subsProfilePersistDTO.getOnBoardingMailReqd() != null) {
                    pStmt.setInt(3, subsProfilePersistDTO.getOnBoardingMailReqd());
                } else {
                    pStmt.setNull(3, java.sql.Types.INTEGER);
                }
                pStmt.setString(4,mdn);
                pStmt.addBatch();
            }

            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_UPDATE_POCSUBSCR_ADDLINFO);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");



        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateBulkSubscrPkgAddlInfo - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_UPDATE_POCSUBSCR_ADDLINFO);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }

    }

    public void updateUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:subsProfilePersistDTO", subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_UPDATE_USER_PROFILE_NAME);
            knLogger.debug("startin22g the job");
            String userProfileName=subsProfilePersistDTO.getUserProfileName();
            for (String mdn : subsProfilePersistDTO.getMdns()) {
                pStmt.setString(1, userProfileName);
                pStmt.setString(2,mdn);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_UPDATE_USER_PROFILE_NAME);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrPkgAddlInfo - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_UPDATE_USER_PROFILE_NAME);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
    }

    public String selectUserProfileName(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "selectUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String userProfileName=null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_SELECT_USER_PROFILE_NAME);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_SELECT_USER_PROFILE_NAME);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            if (rs.next()) {
                userProfileName =rs.getString(USER_PROFILE_NAME) ;
            } else {
                knLogger.info(methodName, "Subscriber Profile name not found");
            }
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrPkgAddlInfo - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_SELECT_USER_PROFILE_NAME);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        return userProfileName;
    }

    public Map<String,String> selectUserProfileNameList(List<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "selectUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:mdn", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String userProfileName=null;
        StringBuilder buffer = new StringBuilder(200);
        Statement statement = null;
        Map<String,String> profileNameMap=new HashMap<String,String>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            buffer.append(QRY_SELECT_USER_PROFILE_NAME_LIST).append(KnDbUtil.convertListToStringBuffer(mdnList));
            statement = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
            rs = statement.executeQuery(buffer.toString());
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                profileNameMap.put(rs.getString(1), rs.getString(2));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrPkgAddlInfo - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_SELECT_USER_PROFILE_NAME);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            KnDbUtil.closeStatement(statement);
        }
        return profileNameMap;
    }

    public KnOPSubsProfileInfoDTO getSubClientSettings(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubClientSettings(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDTO = new KnOPSubsProfileInfoDTO();
        Integer recordingStatus = 0;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(QRY_SELECT_RECORDING_STATUS);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_SELECT_RECORDING_STATUS);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            if (rs.next()) {
                if( rs.getObject(RECORDING_STATUS) != null ){
                    subsProfileInfoDTO.setRecordingStatus(Integer.parseInt(rs.getString(RECORDING_STATUS)));
                }else{
                    knLogger.debug(methodName,"Recording Status is null");
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Recording Setting for mdn - " +KnGDPRTemplate.mdn(mdn) + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRADDLINFO, QRY_SELECT_USER_PROFILE_NAME);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        return subsProfileInfoDTO;
    }

    /**
     * method to retrieve the Subscriber AddlInfo Profile
     *
     * @param subsProfilePersistDTO KnSubsAddlInfoPersistDTO
     * @param persistTxn            KnPersisterTxn
     * @return void
     * @throws KnDAOException DB Layer Exception
     */
    public void setSubscrClientAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "setSubscrClientAddlInfo(KnSubsAddlInfoPersistDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        PreparedStatement pStmtUpdate = null;
        PreparedStatement pStmtSelect = null;
        ResultSet rs = null;
        knLogger.debug(methodName, "ENTRY: Select Subscriber AddlInfo Profile ");
        knLogger.debug(methodName, "ENTRY: BeforeConnection ", subsProfilePersistDTO);

        knLogger.debug(methodName, "ENTRY:Recording Status", subsProfilePersistDTO.getRecodingStatus());
        if (null != subsProfilePersistDTO.getRecodingStatus()) {
            try {
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

                knLogger.debug("startin22g the job");
                pStmtSelect = conn.prepareStatement(QRY_SELECT_MDN);
                pStmtUpdate = conn.prepareStatement(QRY_UPDATE_SUBSCR_CLIENT_SETTINGS);
                pStmt = conn.prepareStatement(QRY_INSERT_POCSUBSCR_ADDLINFO_SUBSCR_CLIENT_SETTINGS);
                for (String mdn : subsProfilePersistDTO.getMdns()) {
                    String mDN = null;
                    pStmtSelect.setString(1,mdn);
                    rs = pStmtSelect.executeQuery();
                    while (rs.next()) {
                        mDN = rs.getString("MDN");
                    }
                    knLogger.debug(methodName,"Query for select Mdn : ",QRY_SELECT_MDN);
                    if (mDN != null) {
                        if (null != subsProfilePersistDTO.getRecodingStatus() && 0 != subsProfilePersistDTO.getRecodingStatus()) {
                            pStmtUpdate.setInt(1, subsProfilePersistDTO.getRecodingStatus());
                        } else {
                            pStmtUpdate.setNull(1, java.sql.Types.INTEGER);
                        }
                        pStmtUpdate.setString(2, mdn);
                        pStmtUpdate.executeUpdate();
                        knLogger.debug(methodName, "QUERY: Executing the Update Query - " , QRY_UPDATE_SUBSCR_CLIENT_SETTINGS);

                    } else {
                        pStmt.setInt(1, KnConstants.TIME_SLOT_TYPE);
                        pStmt.setString(2, mdn);
                        if (subsProfilePersistDTO.getRecodingStatus() != null) {
                            pStmt.setInt(3, subsProfilePersistDTO.getRecodingStatus());
                        } else {
                            pStmt.setNull(3, java.sql.Types.INTEGER);
                        }
                        pStmt.executeUpdate();
                        knLogger.debug(methodName, "QUERY: Executing the Insert Query - ", QRY_INSERT_POCSUBSCR_ADDLINFO_SUBSCR_CLIENT_SETTINGS);
                    }
                }

            } catch (Exception e) {
                knLogger.error(methodName, "Unexpected Exception - ", e);
                throw KnDbUtil.processException(e, "Failed to Add aor update the Subscriber AddlInfo Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRADDLINFO, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pStmtSelect);
                KnDbUtil.closePreparedStatement(pStmtUpdate);
                KnDbUtil.closePreparedStatement(pStmt);
                knLogger.info(methodName, "EXIT : select Subscriber AddlInfo Profile");
            }
        }
    }

}
