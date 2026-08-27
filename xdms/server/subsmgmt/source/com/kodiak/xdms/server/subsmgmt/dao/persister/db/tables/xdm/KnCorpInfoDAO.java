/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnCorpInfoDAO.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
 * *******************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.response.KnXDMCorpAccountsList;
import com.kodiak.common.commdto.response.KnXDMCorpAccountsListDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants.EXECUTOR;
import com.kodiak.frameworks.dbfw.collectors.KnSqlJobCollector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class KnCorpInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpInfoDAO.class);

    public static final String className = KnCorpInfoDAO.class.getName();
    private String pttServerId = null;

    private static final String CORPID = "CORPID";
    private static final String EXTCORPID = "EXTCORPID";
    private static final String PRIVACY_AMB_DISC_LISTEN = "PRIVACY_AMB_DISC_LISTEN";
    private static final String XDMSHOME = "XDMSHOME";
    private static final String PROFILE_CREATION_TIME = "PROFILECREATIONTIME";
    private static final String LAST_PROFILE_UPDATE_TIME = "LASTPROFILEUPDATETIME";
    private static final String CORPNAME = "CORPNAME";
    private static final String CORPFS1 = "CORPFS1";
    private static final String OPSCORPFS1 = "OPSCORPFS1";
    private static final String CORPFS2 = "CORPFS2";
    private static final String OPSCORPFS2 = "OPSCORPFS2";
    private static final String MAX_SUBSCRS = "MAXSUBSCRS";
    private static final String MAX_CORP_LISTS = "MAXCORPLISTS";
    private static final String MAX_MEMBERS_PER_CORP_LIST = "MAXMEMBERSPERCORPLIST";
    private static final String MAX_CORP_GROUPS = "MAXCORPGROUPS";
    private static final String MAX_MEMBERS_PER_CORP_GROUP = "MAXMEMBERSPERCORPGROUP";
    private static final String PAIRED_CONTACT_LIST_ID = "PAIREDCONTACTLISTID";
    private static final String POCHOME = "POCHOME";
    private static final String LINKED_GW_KEY="LINKED_GW_KEY";
    private static final String SUBSCR_DEF_PTTRADIO="SUBSCR_DEF_PTTRADIO";
    private static final String TABLENAME = "DG.POCCORPINFO";

    private static final String DYNAMIC_QOS_FLAG = "DYNAMICQOSFLAG";
    private static final String CORP_HIERARCHY = "CORP_HIERARCHY";
    private static final String WEB_DISPATCH_ENABLED = "WEB_DISPATCH_ENABLED";
    private static final String FEATURE_REL_VERSION = "FEATURE_REL_VERSION";
    private static final String PREASSIGNEDCORPHOME = "DG.PREASSIGNEDCORPHOME";
    private static final String POCCORP_ADDLINFO = "DG.POCCORP_ADDLINFO";
    private static final String XDMCORPFS2_SET = "XDMCORPFS2_SET";
    private static final int UNIQUE_CONSTRAINT_ERROR_CODE = 907;

    private static final String SELECT_QRY = "SELECT ";
    private static final String INSERT_QRY = "INSERT INTO TABLE " + TABLENAME;
    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET ";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + CORPID + " = ?";
    private static final String SELECT_CORP_ID_QRY = "SELECT " + CORPID + " FROM " + TABLENAME + " WHERE " +
            EXTCORPID + "=?";
    private static final String SELECT_CORP_PROFILE_QRY = "SELECT * FROM " + TABLENAME + " WHERE " +
            EXTCORPID + "= ?";

    private static final String QRY_CORP_PROFILE_CLEANUP = "SELECT MAXPREDEFINEDTMPLTCNT FROM " + TABLENAME + " WHERE " +
            CORPID + "= ?";
    private static final String SELECT_CORP_PROFILE_4_CORPID = "SELECT * FROM " + TABLENAME + " WHERE " +
            CORPID + " = ?";
    private static final String SELECT_EXT_CORP_ID = "SELECT " + EXTCORPID + " FROM " + TABLENAME + " WHERE " +
            CORPID + "=?";

    private static final String SELECT_PRIVACY_AMB_DISC_LISTEN = "SELECT " + PRIVACY_AMB_DISC_LISTEN + " FROM " + TABLENAME + " WHERE " +
            CORPID + "=?";

    private static final String UPDATE_LAST_PROFILE_TIME_QRY = UPDATE_QRY + LAST_PROFILE_UPDATE_TIME + "=? " +
            " WHERE " + CORPID + "=?";
    private static final String UPDATE_LINKED_GW_KEY_QRY="UPDATE DG.POCCORPINFO SET LINKED_GW_KEY = ? WHERE EXTCORPID = ? ";

    private static final String DELETE_CORPSRSMAPPING_QRY= "DELETE FROM DG.CORPSRSMAPPING WHERE " + CORPID + " = ?";

    private static final String DELETE_CORPCONFIGINFO_QRY = "DELETE FROM DG.POCCORPCONFIGINFO WHERE " + CORPID + " = ?";

    private static final String DELETE_PREASSIGNEDCORPHOME = "DELETE FROM " + PREASSIGNEDCORPHOME + " WHERE " + EXTCORPID + " = ?";

    private static final String DELETE_POCCORP_ADDLINFO = "DELETE FROM " + POCCORP_ADDLINFO + " WHERE " + CORPID + " = ?";
    private static final String SELECT_CORP_ACC_LIST_QRY = "SELECT ROWS ? TO ? " + EXTCORPID + " ," + CORPNAME + "," + XDMCORPFS2_SET + " FROM " + TABLENAME;

    public KnCorpInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    // We have revert back the changes, because same 10 param are available for system level also. so corporate level is not required.

    /**
     * method to Create Corporate Profile
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persistTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersiterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY : Create Corp Profile ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Open the Transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.debug( methodName, "Request to Create Profile with data - " , corpInfoDto);
            int corpId = corpInfoDto.getCorpId();
            String extCorpId = corpInfoDto.getExtCorpId();
            String XDMSHome = corpInfoDto.getXDMSHome();
            Long profileCreationTime = corpInfoDto.getProfileCreationTime();
            Long lastProfileUpdateTime = corpInfoDto.getLastProfileUpdateTime();
            String corpName = corpInfoDto.getCorporateName();
            String corpFS2 = corpInfoDto.getCorpFS2();
            String opsCorpFS2 = corpInfoDto.getOpsCorpFS2();
            int dynamicQosFlag=corpInfoDto.getDynamicQosFlag();
            int CorpHierarchyType = corpInfoDto.getHierarchyType().value();

//            int maxSubscrs = corpInfoDto.getMaxSubscribers();
//            int maxCorpLists = corpInfoDto.getMaxCorpLists();
//            int maxMembersPerCorpLists = corpInfoDto.getMaxMembersPerCorpList();
//            int maxCorpGroups = corpInfoDto.getMaxCorpGroups();
//            int maxMembersPerCorpGroup = corpInfoDto.getMaxMembersPerCorpGroup();
            String pocHome = corpInfoDto.getPocHome();
            String featRelVersion = String.valueOf(corpInfoDto.getFeatureRelVersion());
            String xdmCorpFS2Set = corpInfoDto.getXdmCorpFS2Set();

            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(CORPID);
            queryFields.add(EXTCORPID);
            queryFields.add(XDMSHOME);
            queryFields.add(PROFILE_CREATION_TIME);
            queryFields.add(LAST_PROFILE_UPDATE_TIME);
//            queryFields.add(MAX_SUBSCRS);
//            queryFields.add(MAX_CORP_LISTS);
//            queryFields.add(MAX_MEMBERS_PER_CORP_LIST);
//            queryFields.add(MAX_CORP_GROUPS);
//            queryFields.add(MAX_MEMBERS_PER_CORP_GROUP);
            queryFields.add(POCHOME);
            if (corpName != null) {
                queryFields.add(CORPNAME);
            }
            queryFields.add(CORPFS1);
            queryFields.add(OPSCORPFS1);
            queryFields.add(DYNAMIC_QOS_FLAG);
            queryFields.add(CORP_HIERARCHY);
            queryFields.add(CORPFS2);
            queryFields.add(OPSCORPFS2);
            queryFields.add(FEATURE_REL_VERSION);
            queryFields.add(XDMCORPFS2_SET);
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setInt(++columnIndex, corpId);
            pStmt.setString(++columnIndex, extCorpId);
            pStmt.setString(++columnIndex, XDMSHome);
            pStmt.setLong(++columnIndex, profileCreationTime);
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
//            pStmt.setInt(++columnIndex, maxSubscrs);
//            pStmt.setInt(++columnIndex, maxCorpLists);
//            pStmt.setInt(++columnIndex, maxMembersPerCorpLists);
//            pStmt.setInt(++columnIndex, maxCorpGroups);
//            pStmt.setInt(++columnIndex, maxMembersPerCorpGroup);
            pStmt.setString(++columnIndex, pocHome);
            if (corpName != null) {
                pStmt.setString(++columnIndex, corpName);
            }
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(corpFS2));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(opsCorpFS2));
            pStmt.setInt(++columnIndex, dynamicQosFlag);
            pStmt.setInt(++columnIndex, CorpHierarchyType);
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(corpFS2));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(opsCorpFS2));
            pStmt.setString(++columnIndex,featRelVersion);
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(xdmCorpFS2Set));

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", DTO - " , persistenceDTO
            );
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occurred");
            knLogger.error( methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception Occurred:", sqlE);
            //Because of race condition create subscriber flow was failing to handle that the below condition is added.
            if (sqlE.getErrorCode() == UNIQUE_CONSTRAINT_ERROR_CODE) {
                knLogger.error(methodName, "Unique constraint exception occurred ", sqlE);
                if (ownedTxn) {
                    knLogger.debug(methodName, "Saving the transaction ");
                    persistTxn.save();
                }
            } else {
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw KnDbUtil.processException(sqlE, "Failed to create Corp Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to create Corp Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : create Corp Profile");
        }
    }



    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "update(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY : update Corp Profile ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Open the Transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.debug( methodName, "Request to update Profile with data - " , corpInfoDto);

                //Go for async updation.
                knLogger.debug( methodName,"To start async update");
                int corpId = corpInfoDto.getCorpId();
                String corpName=corpInfoDto.getCorporateName();
                Map<String,String> asyncInput = new HashMap<>();
                asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, pttServerId);
                asyncInput.put(KnDbSyncFwConstants.CORP_ID, String.valueOf(corpId));
                asyncInput.put(KnDbSyncFwConstants.CORP_NAME, String.valueOf(corpName));
                knLogger.debug( methodName," Async inputs", asyncInput);
                KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
                int serviceType = EXECUTOR.ETAG_UPDATE.value();
                collector.collect(serviceType, asyncInput);
                knLogger.info( methodName,"Updated to collector",serviceType);

                /*updateSync(persistenceDTO,persistTxn);*/
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Corp Profile - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * currently this method updates only Corp Name and Last profile time only
     *
     * @param persistenceDTO KnCorpProfilePersistDTO
     * @param persistTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateSync(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateSync(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY : update Corp Profile ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Open the Transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.debug( methodName, "Request to update Profile with data - " , corpInfoDto);
            int corpId = corpInfoDto.getCorpId();
            Long lastProfileUpdateTime = corpInfoDto.getLastProfileUpdateTime();
            String corpName = corpInfoDto.getCorporateName();
            String pocHome = corpInfoDto.getPocHome();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append("UPDATE ").append(TABLENAME).append(" SET ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?");
            if (corpName != null) {
                queryBuffer.append(", ");
                queryBuffer.append(CORPNAME).append("=?");
            }
            if (pocHome != null && !pocHome.equals("")) {
                queryBuffer.append(", ");
                queryBuffer.append(POCHOME).append("=?");
            }


            queryBuffer.append(" WHERE ").append(CORPID).append("=?");

            query = queryBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (corpName != null) {
                pStmt.setString(++columnIndex, corpName);
            }
            if (pocHome != null && !pocHome.equalsIgnoreCase("")) {
                pStmt.setString(++columnIndex, pocHome);
            }

            pStmt.setInt(++columnIndex, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", DTO - " , persistenceDTO
            );
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction ");
                persistTxn.save();
            }
            knLogger.info( methodName, "EXIT : update Corp Profile");

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occurred");
            knLogger.error( methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occurred");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Corp Profile - " + sqlE.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Corp Profile - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * currently this method updates only External Corp ID and Last profile time only
     *
     * @param persistenceDTO KnCorpProfilePersistDTO
     * @param persistTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateExtCorpID(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateExtCorpID(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY : update External Corp ID ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Open the Transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.debug( methodName, "Request to update Profile with data - " , corpInfoDto);
            int corpId = corpInfoDto.getCorpId();
            Long lastProfileUpdateTime = corpInfoDto.getLastProfileUpdateTime();
            String extCorpID = corpInfoDto.getExtCorpId();

            StringBuffer queryBuffer = new StringBuffer();
            queryBuffer.append("UPDATE ").append(TABLENAME).append(" SET ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?");
            if (extCorpID != null && !extCorpID.equals("")) {
                queryBuffer.append(", ");
                queryBuffer.append(EXTCORPID).append("=?");
            }
            queryBuffer.append(" WHERE ").append(CORPID).append("=?");

            query = queryBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (extCorpID != null && !extCorpID.equalsIgnoreCase("")) {
                pStmt.setString(++columnIndex, extCorpID);
            }
            pStmt.setInt(++columnIndex, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", DTO - " , persistenceDTO
            );
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occurred");
            knLogger.error( methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occurred");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Ext Corp ID - " + sqlE.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Ext Corp ID - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update Ext Corp ID ");
        }
    }

    /**
     * method to delete the Corporate Profile
     *
     * @param persistenceDTO IPersistenceDTO (KnCorpProfilePersistDTO)
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DAO Layer Exception
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "delete(IPersistenceDTO, persisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY: Delete Corp Profile ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            int corpId = corpInfoDto.getCorpId();

            query = DELETE_QRY;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query - " , query , ", DTO - " , persistenceDTO
            );
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete corporate profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete corporate profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete corporate profile - ");
        }

    }

    public void deleteCorpConfigInfo(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpConfigInfo(IPersistenceDTO, persisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY: Delete the entry from pocCorpConfigInfo ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            int corpId = corpInfoDto.getCorpId();

            query = DELETE_CORPCONFIGINFO_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query - " , query , ", DTO - " , persistenceDTO
            );
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete corpconfiginfo entry- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCCORPCONFIGINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete corpconfiginfo entry - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCCORPCONFIGINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete corpconfiginfo entry - ");
        }

    }

    public void onDeleteCorpProfile(int corpId,KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "onDeleteCorpProfile()";
        knLogger.debug(methodName, "Entry ",corpId);
        PreparedStatement pstmt = null;
        StringBuilder osmDeleteQuery=new StringBuilder();
        StringBuilder corpOSMDelteQuery=new StringBuilder();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            osmDeleteQuery.append("DELETE FROM DG.OSMLISTINFO WHERE OSMLISTID IN (SELECT OSMLISTID FROM DG.CORPOSMINFO WHERE CORPID=?)");
            knLogger.debug( methodName, "Executing ON_CORP_DELETE_OSMLISTINFO  query -" ,osmDeleteQuery );
            pstmt = conn.prepareStatement(osmDeleteQuery.toString());
            pstmt.setInt(1, corpId);
            int osmInfoRecordDeleted=pstmt.executeUpdate();


            corpOSMDelteQuery.append("DELETE FROM DG.CORPOSMINFO WHERE CORPID=?");
            knLogger.debug( methodName, "Executing ON_CORP_DELETE_CORP_OSM query -", corpOSMDelteQuery);
            pstmt = conn.prepareStatement(corpOSMDelteQuery.toString());
            pstmt.setInt(1, corpId);
            int corpOsmRecordDelted=pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT:ON_CORP_DELETE_CORP_OSM del count",
                    corpOsmRecordDelted,"ON_CORP_DELETE_OSMLISTINFO delete count",osmInfoRecordDeleted);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while deleting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    pttServerId, KnProvDAOSourceTypes.XDM_CORP_OSM, osmDeleteQuery.toString());
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        knLogger.info( methodName, "not Implemented");
        return null;
    }

    /**
     * method to retrieve the Corporation ID
     *
     * @param extCorpId    String
     * @param persisterTxn KnPersisterTxn
     * @return int corporation id for the external corp Id
     * @throws KnDAOException DAO Layer exception
     */
    public int getCorpId(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpId(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        int corpId = -1;

        knLogger.debug( methodName, "ENTRY : retrieve CorpId from CorpInfo");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            query = SELECT_CORP_ID_QRY;
            if (ownedTxn) {
                //conn = persisterTxn.getDBConnection(pttServerId, true);
                conn = persisterTxn.getDBConnection(pttServerId,KnDBConst.DataStores.XDM_SHARED_DATA , true);
            } else {
                conn = persisterTxn.getDBConnection(pttServerId,KnDBConst.DataStores.XDM_SHARED_DATA , false);
                //conn = persisterTxn.getDBConnection(pttServerId, false);
            }

            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, extCorpId);

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", ext Corp Id - " , extCorpId
            );
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (rs != null) {
                while (rs.next()) {
                    corpId = rs.getInt(CORPID);
                }
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persisterTxn.save();
            }

            return corpId;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to Select Corp ID - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Select Corp ID - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info( methodName, "EXIT : select corpId from CorpInfo  - " ,corpId);
        }
    }

    /**
     * method to retrieve the External Corp Id for the internal Corp Id
     *
     * @param corpId     int
     * @param persistTxn KnPersisterTxn
     * @return String ext Corp Id
     * @throws KnDAOException DB Layer Exception
     */
    public String getExtCorpId(int corpId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getCorpId(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        String extCorpId = null;
        knLogger.debug( methodName, "ENTRY : retrieve CorpId from CorpInfo");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            query = SELECT_EXT_CORP_ID;
            if (ownedTxn) {
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, true);
            } else {
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, false);
            }
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", Corp Id - " , corpId
            );
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (rs.next()) {
                extCorpId = rs.getString(EXTCORPID);
            } else {
                knLogger.error( methodName, "Corporate Profile Doesnt exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.SERVER_ERROR, "Corporate Profile Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.CORPINFO, query);
            }
            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }

            return extCorpId;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to Select extCorp ID - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Select extCorp ID - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info( methodName, "EXIT : select extcorpId from CorpInfo  - ",extCorpId);
        }
    }


    public Integer getPrivacyAmbDiscListenFlag(int corpId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getPrivacyAmbDiscListenFlag(int, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        //boolean privacyAmbDiscListenFlag=false;
        Integer privacyAmbDiscListen=null;
        knLogger.debug( methodName, "ENTRY : retrieve PrivacyAmbDiscListenFlag from CorpInfo");
        knLogger.debug( methodName, "corpId::"+corpId);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            query = SELECT_PRIVACY_AMB_DISC_LISTEN;
            if (ownedTxn) {
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, true);
            } else {
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, false);
            }
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", Corp Id - " , corpId
            );
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (rs.next()) {
                if(rs.getString(PRIVACY_AMB_DISC_LISTEN)!=null) {
                    privacyAmbDiscListen = Integer.parseInt(rs.getString(PRIVACY_AMB_DISC_LISTEN));
                }
                else
                {
                    privacyAmbDiscListen = null;

                }
                /*if(privacyAmbDiscListen!=null && privacyAmbDiscListen==1)
                {
                    privacyAmbDiscListenFlag=true;
                }*/
                knLogger.debug( methodName, "privacyAmbDiscListen::"+privacyAmbDiscListen);
            } else {
                knLogger.error( methodName, "Row not found");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Row not found", pttServerId,
                        KnProvDAOSourceTypes.CORPINFO, query);
            }
            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }

            return privacyAmbDiscListen;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to Select Privacy Ambient Disc Listen - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Select Privacy Ambient Disc Listen - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info( methodName, "EXIT : select Privacy Ambient Disc Listen from CorpInfo  - ",privacyAmbDiscListen);
        }
    }

    /**
     * method to retrieve the complete corporate profile
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persistTxn     KnPersisterTxn
     * @return KnOPCorpProfileInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnOPCorpProfileInfoDTO getCorpProfileInfo(IPersistenceDTO persistenceDTO, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getCorpId(IPersistenceDTO, boolean, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnOPCorpProfileInfoDTO respDTO;
        knLogger.debug( methodName, "ENTRY : retrieve CorpId from CorpInfo");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoPersistDto = (KnCorpProfilePersistDTO) persistenceDTO;
            String extCorpId = corpInfoPersistDto.getExtCorpId();

            query = SELECT_CORP_PROFILE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            //conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, extCorpId);

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", Ext Corp Id - " , extCorpId
            );
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            respDTO = new KnOPCorpProfileInfoDTO();

            if (rs != null) {
                while (rs.next()) {
                    respDTO.setCorpId(rs.getInt(CORPID));
                    respDTO.setExtCorpId(rs.getString(EXTCORPID));
                    respDTO.setCorporateName(rs.getString(CORPNAME));
                    respDTO.setPairedContactListId(rs.getInt(PAIRED_CONTACT_LIST_ID));
                    respDTO.setMaxSubscribers(rs.getInt(MAX_SUBSCRS));
                    respDTO.setMaxCorpGroups(rs.getInt(MAX_CORP_GROUPS));
                    respDTO.setMaxMembersPerCorpGroup(rs.getInt(MAX_MEMBERS_PER_CORP_GROUP));
                    respDTO.setMaxCorpLists(rs.getInt(MAX_CORP_LISTS));
                    respDTO.setMaxMembersPerCorpList(rs.getInt(MAX_MEMBERS_PER_CORP_LIST));
                    respDTO.setPocHome(rs.getString(POCHOME));
                    String corpFs2=rs.getString(CORPFS2)!=null?rs.getString(CORPFS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CORPFS1));
                    respDTO.setCorpFS2(corpFs2);
                    String opsCorpFs2=rs.getString(OPSCORPFS2)!=null?rs.getString(OPSCORPFS2):KnGeneralUtil.convertLongToHexString(rs.getLong(OPSCORPFS1));
                    respDTO.setOpsCorpFS2(opsCorpFs2);
                    respDTO.setXDMSHome(rs.getString(XDMSHOME));
                    respDTO.setLinkedGwKey(rs.getString(LINKED_GW_KEY));
                    respDTO.setDynamicQosFlag(rs.getInt(DYNAMIC_QOS_FLAG));
                    respDTO.setHierarchyType(HIERARCHY_TYPE.validate(rs.getInt(CORP_HIERARCHY)));
                    respDTO.setWebDispatchEnabled(rs.getInt(WEB_DISPATCH_ENABLED));
                    respDTO.setSubsDefPttRadio(rs.getInt(SUBSCR_DEF_PTTRADIO));
                }
            } else {
                knLogger.error( methodName, "Corporate Profile Doesnt Exist");
//                if (ownedTxn) persistTxn.rollback();
//                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Corporate profile doesnt exist", pttServerId,
//                        KnProvDAOSourceTypes.CORPINFO, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }

            return respDTO;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to Select Corp Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Select Corp Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : select corp Profile from CorpInfo  - ");
        }
    }

    public void updateLastProfileTime(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileTime(KnCorpProfilePersistDTO, KnPersisterTxn";

        KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
        knLogger.debug( methodName, "Request to update Profile with data - " , corpInfoDto);

        //Go for async updation.
        knLogger.debug( methodName,"To start async update");
        int corpId = corpInfoDto.getCorpId();
        Map<String,String> asyncInput = new HashMap<>();
        asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, pttServerId);
        asyncInput.put(KnDbSyncFwConstants.CORP_ID, String.valueOf(corpId));
        knLogger.debug( methodName," Async inputs", asyncInput);
        KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
        int serviceType = EXECUTOR.ETAG_UPDATE.value();
        collector.collect(serviceType, asyncInput);
        knLogger.info( methodName,"Updated to collector",serviceType);
    }

    /**
     * method to update the last profile update timer
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateLastProfileTimeSync(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileTime(KnCorpProfilePersistDTO, KnPersisterTxn";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.info( methodName, "ENTRY: update lastProfile Time Corp Profile ");
        try {
            //open transaction if the received txn is null
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            int corpId = corpInfoDto.getCorpId();
            long profileUpdateTime = corpInfoDto.getLastProfileUpdateTime();

            query = UPDATE_LAST_PROFILE_TIME_QRY;
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId,KnDBConst.DataStores.XDM_SHARED_DATA , false);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, profileUpdateTime);
            pStmt.setInt(2, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query - " , query);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to  update lastProfile Time for corporate profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to  update lastProfile Time for corporate profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT :  update lastProfile Time for corporate profile - ");
        }

    }

    /**
     * currently this method updates only External Corp ID and Last profile time only
     *
     * @param persistenceDTO KnCorpProfilePersistDTO
     * @param persistTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateCorpName(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateExtCorpID(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY : update corpName ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Open the Transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.debug( methodName, "Request to update Profile with data - " , corpInfoDto);
            int corpId = corpInfoDto.getCorpId();
            Long lastProfileUpdateTime = corpInfoDto.getLastProfileUpdateTime();
            String corpName = corpInfoDto.getCorporateName();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append("UPDATE ").append(TABLENAME).append(" SET ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?");
            if (corpName != null) {
                queryBuffer.append(", ");
                queryBuffer.append(CORPNAME).append("=?");
            }
            queryBuffer.append(" WHERE ").append(CORPID).append("=?");

            query = queryBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (corpName != null) {
                pStmt.setString(++columnIndex, corpName);
            }
            pStmt.setInt(++columnIndex, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", DTO - " , persistenceDTO
            );
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occurred");
            knLogger.error( methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occurred");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update corpName- " + sqlE.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update corpName- " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update corpName ");
        }
    }


    public void updateLinkedGwKeyOfCorp(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)throws KnDAOException{
        String methodName = "updateLinkedGwKeyOfCorp(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug( methodName, "ENTRY : update linked gw key ");
        try {
            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.debug( methodName, "Request to update Profile with data - " , corpInfoDto);
            String extCorpId=corpInfoDto.getExtCorpId();
            String linkedGwKey=corpInfoDto.getLinkedGwKey();
            query=UPDATE_LINKED_GW_KEY_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setString(++columnIndex, linkedGwKey);
            pStmt.setString(++columnIndex, extCorpId);
            knLogger.debug( methodName, "QUERY: Executing the Query " , query , ", DTO - " , persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");
        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occurred");
            knLogger.error( methodName, dbConne);
            throw dbConne;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to update linked Gw key- " + e.getMessage(), pttServerId,KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void deleteCorpSrsMapping(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpSrsMapping(IPersistenceDTO, persisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug( methodName, "ENTRY: Delete deleteCorpSrsMapping Profile ");
        try {

            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            int corpId = corpInfoDto.getCorpId();

            query = DELETE_CORPSRSMAPPING_QRY;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query - " , query , ", DTO - " , persistenceDTO
            );
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

        }   catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            knLogger.error( methodName, e);
            throw KnDbUtil.processException(e, "Failed to delete deleteCorpSrsMapping profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPSRSMAPPING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }

    }

    public void createCorporateAccount(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "createCorporateAccount()";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.info(methodName, "Request to Create Profile with data - ", corpInfoDto);
            int corpId = corpInfoDto.getCorpId();
            String extCorpId = corpInfoDto.getExtCorpId();
            String XDMSHome = corpInfoDto.getXDMSHome();
            Long profileCreationTime = corpInfoDto.getProfileCreationTime();
            Long lastProfileUpdateTime = corpInfoDto.getLastProfileUpdateTime();
            String corpName = corpInfoDto.getCorporateName();
            String corpFS2 = corpInfoDto.getCorpFS2();
            String opsCorpFS2 = corpInfoDto.getOpsCorpFS2();
            int dynamicQosFlag = corpInfoDto.getDynamicQosFlag();
            String xdmCorpFS2Set = corpInfoDto.getXdmCorpFS2Set();
            //int CorpHierarchyType = corpInfoDto.getHierarchyType().value();

            String pocHome = corpInfoDto.getPocHome();
            String featRelVersion = String.valueOf(corpInfoDto.getFeatureRelVersion());
            int maxSubscribers = corpInfoDto.getMaxSubscribers();
            int maxCorpList = corpInfoDto.getMaxCorpLists();
            int maxMemPerCorpList = corpInfoDto.getMaxMembersPerCorpList();
            int maxCropGroups = corpInfoDto.getMaxCorpGroups();
            int maxDispGroups = corpInfoDto.getMaxDispatchGroups();
            int maxMemPerDispatchGroup = corpInfoDto.getMaxMemPerDispatchGroup();
            Boolean dispatchEnabled = corpInfoDto.getDispatchEnabled();
            int maxExtSubsPerCorp = corpInfoDto.getMaxExtSubsPerCorp();
            Boolean interopEnabled = corpInfoDto.getIsInterOpEnabled();
            int maxRadioChannels = corpInfoDto.getMaxRadioChannels();
            int maxZones = corpInfoDto.getMaxZones();
            int maxChannelsPerZone = corpInfoDto.getMaxChannelsPerZone();
            int maxLargeTalkGroup = corpInfoDto.getMaxLargeTalkGroup();
            int maxGroupProfile = corpInfoDto.getMaxGroupProfile();
            int maxUserProfile = corpInfoDto.getMaxUserProfile();
            int maxAssignProfiles = corpInfoDto.getMaxAssignProfiles();
            int explicitlyProvisioned = 1;
            //int maxCorpHiearchyLevel = corpInfoDto.getMaxCorpHiearchyLevel();

            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(CORPID);
            queryFields.add(EXTCORPID);
            if (null != XDMSHome) {
                queryFields.add(XDMSHOME);
            }
            queryFields.add(PROFILE_CREATION_TIME);
            queryFields.add(LAST_PROFILE_UPDATE_TIME);
            if (null != pocHome) {
                queryFields.add(POCHOME);
            }
            if (null != corpName) {
                queryFields.add(CORPNAME);
            }
            if (null != corpFS2) {
                queryFields.add(CORPFS1);
            }
            if (null != opsCorpFS2) {
                queryFields.add(OPSCORPFS1);
            }
            queryFields.add(DYNAMIC_QOS_FLAG);
            //queryFields.add(CORP_HIERARCHY);
            if (null != corpFS2) {
                queryFields.add(CORPFS2);
            }
            if (null != opsCorpFS2) {
                queryFields.add(OPSCORPFS2);
            }
            if (null != featRelVersion) {
                queryFields.add(FEATURE_REL_VERSION);
            }
            if (maxSubscribers > 0) {
                queryFields.add("MAXSUBSCRS");
            }
            if (maxCorpList > 0) {
                queryFields.add("MAXCORPLISTS");
            }
            if (maxMemPerCorpList > 0) {
                queryFields.add("MAXMEMBERSPERCORPLIST");
            }
            if (maxCropGroups > 0) {
                queryFields.add("MAXCORPGROUPS");
            }
            if (maxDispGroups > 0) {
                queryFields.add("MAXDISPATCHGRPS");
            }
            if (maxMemPerDispatchGroup > 0) {
                queryFields.add("MAXMEMBERSPERDISPATCHGRP");
            }
            if (maxExtSubsPerCorp > 0) {
                queryFields.add("MAX_NNI_SUBSCRS");
            }
            if (null != dispatchEnabled) {
                queryFields.add("WEB_DISPATCH_ENABLED");
            }
            if (null != interopEnabled) {
                queryFields.add("INTEROP_LICENSE_TYPE");
            }
            if (maxRadioChannels > 0) {
                queryFields.add("MAXRADIOCHANNELS");
            }
            if (maxZones > 0) {
                queryFields.add("MAXZONES");
            }
            if (maxChannelsPerZone > 0) {
                queryFields.add("MAXCHANNELSPERZONE");
            }
            if (maxLargeTalkGroup > 0) {
                queryFields.add("MAX_LRGAB_TALKGRP");
            }
            if (maxGroupProfile > 0) {
                queryFields.add("MAX_GRP_PROFILES");
            }
            if (maxUserProfile > 0) {
                queryFields.add("MAX_USER_PROFILES");
            }
            if (maxAssignProfiles > 0) {
                queryFields.add("MAX_USERPROFILES_PERSUB");
            }
            queryFields.add("MAXPREDEFINEDTMPLTCNT");
            if (null != xdmCorpFS2Set) {
                queryFields.add(XDMCORPFS2_SET);
            }
            //queryFields.add("MAX_CORP_HIERARCHY_LEVELS");
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setInt(++columnIndex, corpId);
            pStmt.setString(++columnIndex, extCorpId);
            if (null != XDMSHome) {
                pStmt.setString(++columnIndex, XDMSHome);
            }
            pStmt.setLong(++columnIndex, profileCreationTime);
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (null != pocHome) {
                pStmt.setString(++columnIndex, pocHome);
            }
            if (null != corpName) {
                pStmt.setString(++columnIndex, corpName);
            }
            if (null != corpFS2) {
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(corpFS2));
            }
            if (null != opsCorpFS2) {
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(opsCorpFS2));
            }
            pStmt.setInt(++columnIndex, dynamicQosFlag);
            // pStmt.setInt(++columnIndex, CorpHierarchyType);
            if (null != corpFS2) {
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(corpFS2));
            }
            if (null != opsCorpFS2) {
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(opsCorpFS2));
            }
            if (null != featRelVersion) {
                pStmt.setString(++columnIndex, featRelVersion);
            }
            if (maxSubscribers > 0) {
                pStmt.setInt(++columnIndex, maxSubscribers);
            }
            if (maxCorpList > 0) {
                pStmt.setInt(++columnIndex, maxCorpList);
            }
            if (maxMemPerCorpList > 0) {
                pStmt.setInt(++columnIndex, maxMemPerCorpList);
            }
            if (maxCropGroups > 0) {
                pStmt.setInt(++columnIndex, maxCropGroups);
            }
            if (maxDispGroups > 0) {
                pStmt.setInt(++columnIndex, maxDispGroups);
            }
            if (maxMemPerDispatchGroup > 0) {
                pStmt.setInt(++columnIndex, maxMemPerDispatchGroup);
            }
            if (maxExtSubsPerCorp > 0) {
                pStmt.setInt(++columnIndex, maxExtSubsPerCorp);
            }
            if (null != dispatchEnabled) {
                pStmt.setInt(++columnIndex, dispatchEnabled.equals(Boolean.TRUE) ? 1 : 0);
            }
            if (null != interopEnabled) {
                pStmt.setInt(++columnIndex, interopEnabled.equals(Boolean.TRUE) ? 1 : 0);
            }
            if (maxRadioChannels > 0) {
                pStmt.setInt(++columnIndex, maxRadioChannels);
            }
            if (maxZones > 0) {
                pStmt.setInt(++columnIndex, maxZones);
            }
            if (maxChannelsPerZone > 0) {
                pStmt.setInt(++columnIndex, maxChannelsPerZone);
            }
            if (maxLargeTalkGroup > 0) {
                pStmt.setInt(++columnIndex, maxLargeTalkGroup);
            }
            if (maxGroupProfile > 0) {
                pStmt.setInt(++columnIndex, maxGroupProfile);
            }
            if (maxUserProfile > 0) {
                pStmt.setInt(++columnIndex, maxUserProfile);
            }
            if (maxAssignProfiles > 0) {
                pStmt.setInt(++columnIndex, maxAssignProfiles);
            }
            pStmt.setInt(++columnIndex, explicitlyProvisioned);
            if(null != xdmCorpFS2Set){
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(xdmCorpFS2Set));
            }
           // pStmt.setInt(++columnIndex, maxCorpHiearchyLevel);
            knLogger.debug(methodName, "QUERY: Executing the Query ", query, ", DTO - ", persistenceDTO);
            int count = pStmt.executeUpdate();
            knLogger.info(methodName, " Exit count:", count);
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, " KnDAOException :", dbConne);
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, " SQLException :", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to create Corp Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, " Exception :", e);
            throw KnDbUtil.processException(e, "Failed to create Corp Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }
    public void updateCorporateAccount(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateCorporateAccount()";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            KnCorpProfilePersistDTO corpInfoDto = (KnCorpProfilePersistDTO) persistenceDTO;
            knLogger.info(methodName, "Request to update Profile with data - ", corpInfoDto);
            int corpId = corpInfoDto.getCorpId();
            String extCorpId = corpInfoDto.getExtCorpId();
            Long lastProfileUpdateTime = corpInfoDto.getLastProfileUpdateTime();
            String corpName = corpInfoDto.getCorporateName();
            int maxSubscribers = corpInfoDto.getMaxSubscribers();
            int maxCorpList = corpInfoDto.getMaxCorpLists();
            int maxMemPerCorpList = corpInfoDto.getMaxMembersPerCorpList();
            int maxCorpGroups = corpInfoDto.getMaxCorpGroups();
            int maxMemPerCorpGrp = corpInfoDto.getMaxMembersPerCorpGroup();
            int maxExtSubsPerCorp = corpInfoDto.getMaxExtSubsPerCorp();
            int maxDispGroups = corpInfoDto.getMaxDispatchGroups();
            int maxMemPerDispatchGroup = corpInfoDto.getMaxMemPerDispatchGroup();
            Boolean dispatchEnabled = corpInfoDto.getDispatchEnabled();
            Boolean interopEnabled = corpInfoDto.getIsInterOpEnabled();
            int maxMemPerBCGroup =corpInfoDto.getMaxMemPerBCGrp();
            int maxChannelAllowd=corpInfoDto.getMaxRadioChannels();
            int maxZones = corpInfoDto.getMaxZones();
            int maxChannelsPerZone = corpInfoDto.getMaxChannelsPerZone();
            int maxLargeTalkGroup = corpInfoDto.getMaxLargeTalkGroup();
            int maxGroupProfile = corpInfoDto.getMaxGroupProfile();
            int maxUserProfile = corpInfoDto.getMaxUserProfile();
            int maxAssignProfiles = corpInfoDto.getMaxAssignProfiles();
            String corpFS2 = corpInfoDto.getCorpFS2();
            String xdmCorpFS2Set = corpInfoDto.getXdmCorpFS2Set();
            //sync update
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append("UPDATE ").append(TABLENAME).append(" SET ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?");
            if (null != corpName)  {
                queryBuffer.append(", ");
                queryBuffer.append(CORPNAME).append("=?");
            }
            if(maxSubscribers>0) {
                queryBuffer.append(", ");
                queryBuffer.append(MAX_SUBSCRS).append("=?");
            }
            if(maxCorpList>0){
                queryBuffer.append(", ");
                queryBuffer.append(MAX_CORP_LISTS).append("=?");
            }
            if(maxMemPerCorpList>0) {
                queryBuffer.append(", ");
                queryBuffer.append(MAX_MEMBERS_PER_CORP_LIST).append("=?");
            }
            if(maxCorpGroups>0){
                queryBuffer.append(", ");
                queryBuffer.append(MAX_CORP_GROUPS).append("=?");
            }
            if(maxMemPerCorpGrp>0){
                queryBuffer.append(", ");
                queryBuffer.append(MAX_MEMBERS_PER_CORP_GROUP).append("=?");
            }
            if(maxDispGroups>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAXDISPATCHGRPS").append("=?");
            }
            if(maxMemPerDispatchGroup>0) {
                queryBuffer.append(", ");
                queryBuffer.append("MAXMEMBERSPERDISPATCHGRP").append("=?");
            }
            if(null != dispatchEnabled) {
                queryBuffer.append(", ");
                queryBuffer.append("WEB_DISPATCH_ENABLED").append("=?");
            }
            if(maxExtSubsPerCorp>0) {
                queryBuffer.append(", ");
                queryBuffer.append("MAX_NNI_SUBSCRS").append("=?");
            }
            if(null != interopEnabled) {
                queryBuffer.append(", ");
                queryBuffer.append("INTEROP_LICENSE_TYPE").append("=?");
            }
            if(maxMemPerBCGroup>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAX_MEMBERS_PER_BG").append("=?");
            }
            if(maxChannelAllowd>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAXRADIOCHANNELS").append("=?");
            }
            if(maxZones>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAXZONES").append("=?");
            }
            if(maxChannelsPerZone>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAXCHANNELSPERZONE").append("=?");
            }
            if(maxLargeTalkGroup>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAX_LRGAB_TALKGRP").append("=?");
            }
            if(maxGroupProfile>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAX_GRP_PROFILES").append("=?");
            }
            if(maxUserProfile>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAX_USER_PROFILES").append("=?");
            }
            if(maxAssignProfiles>0){
                queryBuffer.append(", ");
                queryBuffer.append("MAX_USERPROFILES_PERSUB").append("=?");
            }
            if ((corpFS2 != null) && (!corpFS2.isEmpty())) {
                queryBuffer.append(", ");
                queryBuffer.append(CORPFS2).append("=?");
            }
            if ((xdmCorpFS2Set != null) && (!xdmCorpFS2Set.isEmpty())) {
                queryBuffer.append(", ");
                queryBuffer.append(XDMCORPFS2_SET).append("=?");
            }
            queryBuffer.append(" WHERE ").append(CORPID).append("=?");
            query = queryBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if(null != corpName) {
                pStmt.setString(++columnIndex, corpName);
            }
            if(maxSubscribers>0) {
                pStmt.setInt(++columnIndex, maxSubscribers);
            }
            if(maxCorpList>0) {
                pStmt.setInt(++columnIndex, maxCorpList);
            }
            if(maxMemPerCorpList>0) {
                pStmt.setInt(++columnIndex, maxMemPerCorpList);
            }
            if(maxCorpGroups>0) {
                pStmt.setInt(++columnIndex, maxCorpGroups);
            }
            if(maxMemPerCorpGrp>0) {
                pStmt.setInt(++columnIndex, maxMemPerCorpGrp);
            }
            if(maxDispGroups>0) {
                pStmt.setInt(++columnIndex, maxDispGroups);
            }
            if(maxMemPerDispatchGroup>0) {
                pStmt.setInt(++columnIndex, maxMemPerDispatchGroup);
            }
            if(null != dispatchEnabled) {
                pStmt.setInt(++columnIndex, dispatchEnabled.equals(Boolean.TRUE) ? 1 : 0);
            }
            if(maxExtSubsPerCorp>0) {
                pStmt.setInt(++columnIndex, maxExtSubsPerCorp);
            }
            if(null != interopEnabled) {
                pStmt.setInt(++columnIndex, interopEnabled.equals(Boolean.TRUE) ? 1 : 0);
            }
            if(maxMemPerBCGroup>0){
                pStmt.setInt(++columnIndex, maxMemPerBCGroup);
            }
            if(maxChannelAllowd>0){
                pStmt.setInt(++columnIndex, maxChannelAllowd);
            }
            if(maxZones>0) {
                pStmt.setInt(++columnIndex, maxZones);
            }
            if(maxChannelsPerZone>0) {
                pStmt.setInt(++columnIndex, maxChannelsPerZone);
            }
            if(maxLargeTalkGroup>0) {
                pStmt.setInt(++columnIndex, maxLargeTalkGroup);
            }
            if(maxGroupProfile>0) {
                pStmt.setInt(++columnIndex, maxGroupProfile);
            }
            if(maxUserProfile>0) {
                pStmt.setInt(++columnIndex, maxUserProfile);
            }
            if(maxAssignProfiles>0) {
                pStmt.setInt(++columnIndex, maxAssignProfiles);
            }
            if ((corpFS2 != null) && (!corpFS2.isEmpty())) {
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(corpFS2));
            }
            if ((xdmCorpFS2Set != null) && (!xdmCorpFS2Set.isEmpty())) {
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(xdmCorpFS2Set));
            }
            if(corpId>0) {
                pStmt.setInt(++columnIndex, corpId);
            }
            knLogger.debug(methodName, "QUERY: Executing the Query ", query, ", DTO - ", persistenceDTO);
            int count = pStmt.executeUpdate();
            knLogger.info(methodName, " Exit count:", count);
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, " KnDAOException :", dbConne);
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, " SQLException :", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update Corp Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, " Exception :", e);
            throw KnDbUtil.processException(e, "Failed to update Corp Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public int selectCorpId(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectCorpId(String, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : extCorpId - " , extCorpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int corpId = -1;
        try {
            query = SELECT_CORP_ID_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, extCorpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                corpId = rs.getInt(1);
            } else {
                knLogger.error( methodName, "Corp Profile does not exists. corpId - " , corpId);
                throw new KnDBPersistenceException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND,
                        "corpId not found.", pttServerId, TABLENAME, query);
            }
            return corpId;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the corpId- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while  retrieving the corpId  - "
                    + e);
            throw KnDbUtil .processException(e, "Failed while  retrieving the corporate etag " + e,
                    pttServerId, TABLENAME, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "extCorpId - " , extCorpId, "EXIT : corpId - " , corpId);
        }
    }

    public void deletePreassignedCorpHome(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deletePreassignedCorpHome(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : extCorpId - ", extCorpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            query = DELETE_PREASSIGNEDCORPHOME;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, extCorpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully ");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the record from PREASSIGNEDCORPHOME - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting the record from PREASSIGNEDCORPHOME - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting the record from PREASSIGNEDCORPHOME - " + e,
                    pttServerId, PREASSIGNEDCORPHOME, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteCorpAddlInfo(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpAddlInfo(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            query = DELETE_POCCORP_ADDLINFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully ");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting record from POCCORP_ADDLINFO - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting record from POCCORP_ADDLINFO - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting the record from POCCORP_ADDLINFO - " + e,
                    pttServerId, POCCORP_ADDLINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteCorpInfo(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpInfo(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            query = DELETE_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully ");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the record from POCCORPINFO - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting the record from POCCORPINFO - " , e);
            throw KnDbUtil.processException(e, "Failed while deleting the record from POCCORPINFO - " + e,
                    pttServerId, TABLENAME, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public KnXDMCorpAccountsListDTO getCorpAccountsList(KnPersisterTxn persistTxn,String fetchSize, String nextToken) throws KnDAOException {
        String methodName = "getCorpAccountsList(KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnXDMCorpAccountsListDTO corpAccountsListDTO = new KnXDMCorpAccountsListDTO();
        knLogger.debug(methodName, "ENTRY : Retrieve AccountId (ExtCorpID) and CorpName from CorpInfo");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            query = SELECT_CORP_ACC_LIST_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int startIndex = KnGeneralUtil.getStartIndex(Integer.parseInt(fetchSize), Integer.parseInt(nextToken));
            int endIndex = KnGeneralUtil.getEndIndex(Integer.parseInt(fetchSize), Integer.parseInt(nextToken));
            pStmt.setInt(1, startIndex);
            pStmt.setInt(2, endIndex);
            knLogger.debug(methodName, "QUERY: Executing the Query ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed the Query");

            KnXDMCorpAccountsListDTO xdmResponse = new KnXDMCorpAccountsListDTO();
            List<KnXDMCorpAccountsList> corpAccountsList = new ArrayList<>();

            if (rs != null) {
                while (rs.next()) {
                    KnXDMCorpAccountsList corpList = new KnXDMCorpAccountsList();
                    if(null != rs.getString(EXTCORPID))
                    {
                        corpList.setAccountId(rs.getString(EXTCORPID).trim());
                    }
                    if(null != rs.getString(CORPNAME)){
                        corpList.setCorporateName(rs.getString(CORPNAME).trim());
                    }
                    if(null != rs.getString(XDMCORPFS2_SET)){
                        String xdmCorpFS2Set = rs.getString(XDMCORPFS2_SET);
                        Boolean locationEnabled = KnGeneralUtil.getFeatureBitValue(xdmCorpFS2Set, com.kodiak.common.resources.KnConstants.XDMCORPFS2_SET.LOCATION_ENABLED.value());
                        corpList.setLocationEnabled(locationEnabled);
                    }
                    corpAccountsList.add(corpList);
                }
                corpAccountsListDTO.setXdmCorpAccList(corpAccountsList);

            } else {
                knLogger.error(methodName, "Corporate Account List not found");
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction");
                persistTxn.save();
            }
            return corpAccountsListDTO;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to Select Corp Account List - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Select Corp Account List - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Select Corp Account List - ");
        }
    }

    /**
     * method for Retrieve corpProfileCleanUp value
     *
     * @param corpId
     * @param persistTxn
     * @return
     * @throws KnDAOException
     */
    public int retrieveCorpProfileCleanUp(int corpId, KnPersisterTxn persistTxn) throws KnDAOException, SQLException {
        String methodName = "retrieveCorpProfileCleanUp(int, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int corpProfileCleanUp = -1;
        knLogger.info(methodName, "ENTRY: Retrieving the Corporation [", corpId, "] ProfileCleanUpFlag");
        try {
            query = QRY_CORP_PROFILE_CLEANUP;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs != null && rs.next()) {
                corpProfileCleanUp = rs.getInt(1);
            } else {
                knLogger.error(methodName, "Corporate Profile doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Corporate Profile does not exist",
                        pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
            }
            return corpProfileCleanUp;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occured");
            throw KnDbUtil.processException(sqlE, "Failed to Select Corp Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to Select Corp Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info(methodName, "EXIT : select corpProfileCleanUp from CorpInfo  - ", corpProfileCleanUp);
        }
    }


    /**
     * method to delete role based and hierarchy corp data when the corporate profile is deleted
     * added as part of UCSPROVCONFIG-7618
     * @param corpId
     * @param persisterTxn
     * @throws KnDAOException
     */
    /**
     * Deletes corp-related role and hierarchy data before the corporate profile itself is removed.
     * Added as part of UCSPROVCONFIG-7618.
     */
    public void deleteRoleBasedAndHierarchyCorpData(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteRoleBasedAndHierarchyCorpData(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Deleting role based and hierarchy corp data for corpId - ", corpId);

        final String deletePttSettingMapSql = "DELETE FROM DG.CORP_PTTSETTING_MAP WHERE CORPID=?";
        final String deletePttSettingDocSql = "DELETE FROM DG.CORP_PTTSETTING_DOC WHERE CORPID=?";
        final String deleteAnchorPocInfoSql = "DELETE FROM DG.ANCHOR_POC_INFO WHERE CORPID=?";
        final String deleteHierarchyGeocodeMapSql = "DELETE FROM DG.CORP_HIERARCHY_GEOCODE_MAP WHERE CORPID=?";
        final String deleteHierarchyDepthSql = "DELETE FROM DG.CORP_HIERARCHY_DEPTH WHERE CORPID=?";
        final String deleteHierarchyDetailsSql = "DELETE FROM DG.CORP_HIERARCHY_DETAILS WHERE CORPID=?";

        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            PreparedStatement pstmt = null;

            try {
                // DG.CORP_PTTSETTING_MAP
                try {
                    knLogger.debug(methodName, "Executing ON_CORP_DELETE_PTT_SETTING_MAP query - ", deletePttSettingMapSql);
                    pstmt = conn.prepareStatement(deletePttSettingMapSql);
                    pstmt.setInt(1, corpId);
                    int count = pstmt.executeUpdate();
                    knLogger.debug(methodName, "Query executed successfully - ", count);
                } catch (Exception e) {
                    knLogger.error(methodName, "SQLException occurred while deleting - ", e);
                    throw KnDbUtil.processException(
                            e,
                            "Failed while deleting  " + e,
                            pttServerId,
                            KnProvDAOSourceTypes.CORP_PTT_SETTING_MAP,
                            deletePttSettingMapSql
                    );
                } finally {
                    KnDbUtil.closePreparedStatement(pstmt);
                }

                // DG.CORP_PTTSETTING_DOC
                try {
                    knLogger.debug(methodName, "Executing ON_CORP_DELETE_PTT_SETTING_DOC query - ", deletePttSettingDocSql);
                    pstmt = conn.prepareStatement(deletePttSettingDocSql);
                    pstmt.setInt(1, corpId);
                    int count = pstmt.executeUpdate();
                    knLogger.debug(methodName, "Query executed successfully - ", count);
                } catch (Exception e) {
                    knLogger.error(methodName, "SQLException occurred while deleting - ", e);
                    throw KnDbUtil.processException(
                            e,
                            "Failed while deleting  " + e,
                            pttServerId,
                            KnProvDAOSourceTypes.CORP_PTT_SETTING_DOC,
                            deletePttSettingDocSql
                    );
                } finally {
                    KnDbUtil.closePreparedStatement(pstmt);
                }

                // DG.ANCHOR_POC_INFO
                try {
                    knLogger.debug(methodName, "Executing ON_CORP_DELETE_ANCHOR_POC_INFO query - ", deleteAnchorPocInfoSql);
                    pstmt = conn.prepareStatement(deleteAnchorPocInfoSql);
                    pstmt.setInt(1, corpId);
                    int count = pstmt.executeUpdate();
                    knLogger.debug(methodName, "Query executed successfully - ", count);
                } catch (Exception e) {
                    knLogger.error(methodName, "SQLException occurred while deleting - ", e);
                    throw KnDbUtil.processException(
                            e,
                            "Failed while deleting  " + e,
                            pttServerId,
                            KnProvDAOSourceTypes.ANCHOR_POC_INFO,
                            deletePttSettingDocSql
                    );
                } finally {
                    KnDbUtil.closePreparedStatement(pstmt);
                }

                // DG.CORP_HIERARCHY_GEOCODE_MAP
                try {
                    knLogger.debug(methodName, "Executing ON_CORP_DELETE_HIERARCHY_GEOCODE_MAP query - ", deleteHierarchyGeocodeMapSql);
                    pstmt = conn.prepareStatement(deleteHierarchyGeocodeMapSql);
                    pstmt.setInt(1, corpId);
                    int count = pstmt.executeUpdate();
                    knLogger.debug(methodName, "Query executed successfully - ", count);
                } catch (Exception e) {
                    knLogger.error(methodName, "SQLException occurred while deleting - ", e);
                    throw KnDbUtil.processException(
                            e,
                            "Failed while deleting  " + e,
                            pttServerId,
                            KnProvDAOSourceTypes.CORP_HIERARCHY_GEOCODE_MAP,
                            deletePttSettingDocSql
                    );
                } finally {
                    KnDbUtil.closePreparedStatement(pstmt);
                }

                // DG.CORP_HIERARCHY_DEPTH
                try {
                    knLogger.debug(methodName, "Executing ON_CORP_DELETE_HIERARCHY_DEPTH query - ", deleteHierarchyDepthSql);
                    pstmt = conn.prepareStatement(deleteHierarchyDepthSql);
                    pstmt.setInt(1, corpId);
                    int count = pstmt.executeUpdate();
                    knLogger.debug(methodName, "Query executed successfully - ", count);
                } catch (Exception e) {
                    knLogger.error(methodName, "SQLException occurred while deleting - ", e);
                    throw KnDbUtil.processException(
                            e,
                            "Failed while deleting  " + e,
                            pttServerId,
                            KnProvDAOSourceTypes.CORP_HIERARCHY_DEPTH,
                            deletePttSettingDocSql
                    );
                } finally {
                    KnDbUtil.closePreparedStatement(pstmt);
                }

                // DG.CORP_HIERARCHY_DETAILS
                try {
                    knLogger.debug(methodName, "Executing ON_CORP_DELETE_HIERARCHY_DETAILS query - ", deleteHierarchyDetailsSql);
                    pstmt = conn.prepareStatement(deleteHierarchyDetailsSql);
                    pstmt.setInt(1, corpId);
                    int count = pstmt.executeUpdate();
                    knLogger.debug(methodName, "Query executed successfully - ", count);
                } catch (Exception e) {
                    knLogger.error(methodName, "SQLException occurred while deleting - ", e);
                    throw KnDbUtil.processException(
                            e,
                            "Failed while deleting  " + e,
                            pttServerId,
                            KnProvDAOSourceTypes.CORP_HIERARCHY_DETAILS,
                            deletePttSettingDocSql
                    );
                } finally {
                    KnDbUtil.closePreparedStatement(pstmt);
                }

            } catch (Exception e) {
                knLogger.error(methodName, "Unexpected Exception - ", e);
                throw e;
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleting  - ", e);
            throw e;
        }
    }
}
