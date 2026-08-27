/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnProvXDMServerDAO.java
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
package com.kodiak.xdms.server.subsmgmt.dao.persister.db;

import com.kodiak.common.commdto.common.KnExtGatewayInfoDTO;
import com.kodiak.common.commdto.common.KnSubsCameraInfo;
import com.kodiak.common.commdto.response.KnCorporateProfilepersistDTO1;
import com.kodiak.common.commdto.response.KnExtGWProfileInfoDTO;
import com.kodiak.common.commdto.response.KnXDMCorpAccountsListDTO;
import com.kodiak.common.commdto.response.KnXDMExtGWProfileListDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnTriePrefixUtility;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.KnProvTablesRegistry;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.*;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;

import java.sql.*;
import java.util.*;

public class KnProvXDMServerDAO implements IProvXDMServerDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnProvXDMServerDAO.class);
    private String xdmPttServerId;
    private KnGenInfoUtil genInfoUtil;


    private static final String QRY_SELECT_SUBS_POCSERVERMAP = "SELECT SUBSCRIBERPREFIX, POC_PTTSERVERID FROM DG.SUBS_POCSERVERMAP";

    private static final String QRY_SELECT_SUBS_PRESENCESERVERMAP = "SELECT SUBSCRIBERPREFIX, PR_PTTSERVERID FROM DG.SUBS_PRSERVERMAP";

    private static final String QRY_SELECT_ROAMING_CLUSTER_INFO = "SELECT CLUSTERID, CLUSTERNAME FROM DG.ROAMINGCLUSTERINFO";
    private static final String QRY_SELECT_CORPID_OF_EXT_SUBS = "SELECT CORPID FROM DG.EXTCORPCONTACT WHERE CONTACTMDN=?";
    private static final String QRY_SELECT_CLUSTERIDS_OF_ROAMINGTYPES = "SELECT DISTINCT ROAMINGTYPE,CLUSTERID FROM DG.ROAMINGMCCMNCINFO";
    private static final String QRY_SUBSCOUNT_FOR_CORPID_CLIENT = "SELECT COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE  CORPID = ? AND (USERPROFILEINDEX = 0 OR USERPROFILEINDEX IS NULL) AND CLIENT_TYPE IN CLIENT_TYPE_LIST";
    private static final String QRY_SELECT_PROFILEID_FOR_PROFTYPE = "SELECT PROFILEID FROM DG.GENERIC_NNI_PROFILE WHERE PROFILE_TYPE = ? ";
    private static final String QRY_SELECT_MDN_FROM_PSEUDOMDN_POOL = "SELECT PSEUDO_MDN FROM DG.PSEUDO_NUMBER_POOL WHERE PSEUDO_MDN = ?";
    private static final String QRY_SELECT_PAMACCID_FROM_PAMACCINFO = "SELECT BILLINGMDN FROM DG.PAMACCOUNTINFO WHERE EXTERNALPAMACCID = ?";

    public KnProvXDMServerDAO(String xdmPttServerId) {
        this.xdmPttServerId = xdmPttServerId;
        this.genInfoUtil = KnGenInfoUtil.getInstance();
    }

    /**
     * method to retrieve the Subscriber Poc Server Map which is subscriber prefix, poc_PttServerId
     *
     * @param persisterTxn KnPersisterTxn
     * @return Map
     * @throws KnDAOException exception
     */
    public KnTriePrefixUtility<String> retrieveSubsPoCServerMap(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePoCServerMap()";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        KnTriePrefixUtility<String> pocServerMap = null;
        boolean recordExists = false;
        knLogger.debug(methodName, "ENTRY: Txn - ", persisterTxn);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            query = QRY_SELECT_SUBS_POCSERVERMAP;

            knLogger.debug(methodName, "getting Connection for PTT ID - ", xdmPttServerId);
            if (ownedTxn) {
                conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            } else {
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            }

            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            pocServerMap = new KnTriePrefixUtility<String>();
            while (rs.next()) {
                String subscriberPrefix = rs.getString(1);
                String poc_pttServerId = rs.getString(2);
                pocServerMap.put(subscriberPrefix, poc_pttServerId);
                recordExists = true;
            }

            if (!recordExists) {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found", xdmPttServerId, KnProvDAOSourceTypes.POC_SERVER_MAP, query);
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "Unexpected exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "failed to retrieve poc Server Map", xdmPttServerId, KnProvDAOSourceTypes.POC_SERVER_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Poc Server Map -> ", pocServerMap);
        }

        return pocServerMap;

    }

    /**
     * method to retrieve the Subscriber Presence Server Map which is subscriber prefix, pr_PttServerId
     *
     * @param persisterTxn KnPersisterTxn
     * @return Map
     * @throws KnDAOException exception
     */
    public KnTriePrefixUtility<String> retrieveSubsPresenceServerMap(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSubsPresenceServerMap(KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        KnTriePrefixUtility<String> presenceServerMap = null;
        boolean recordExists = false;
        knLogger.debug(methodName, "ENTRY: Txn - ", persisterTxn);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            query = QRY_SELECT_SUBS_PRESENCESERVERMAP;
            if (ownedTxn) {
                conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            } else {
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            }
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            presenceServerMap = new KnTriePrefixUtility<String>();
            while (rs.next()) {
                String subscriberPrefix = rs.getString(1);
                String pr_pttServerId = rs.getString(2);
                presenceServerMap.put(subscriberPrefix, pr_pttServerId);
                recordExists = true;
            }

            if (!recordExists) {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found", xdmPttServerId, KnProvDAOSourceTypes.PRESENCE_SERVER_MAP, query);
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "Unexpected exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "failed to retrieve Presence Server Map", xdmPttServerId, KnProvDAOSourceTypes.PRESENCE_SERVER_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Presence Server Map -> ", presenceServerMap);
        }

        return presenceServerMap;

    }

    /**
     * Method Create Subscriber Profile
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException exception
     */
	public void createSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO,Boolean userProfileCreate, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubscrProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: Create Subscriber Profile ", KnGDPRTemplate.mdn(subsProfilePersistDTO.getMdn()));

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            pocSubscrInfoDAO.insert(subsProfilePersistDTO, userProfileCreate,persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.info(methodName, "EXIT: Create Subscriber Profile ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while creating the Subscriber profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }
    public void updateSubsFS(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException{

        String methodName = "updateSubsFS(KnSubsProfilePersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", subsProfilePersistDTO);
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateSubsFS(subsProfilePersistDTO, persisterTxn);
    }
    /**
     * method to update Subscriber Profile
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", subsProfilePersistDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.update(subsProfilePersistDTO, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update Subscriber Profile ");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while updating the Subscriber profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    /**
     * method to update Subscriber Profile
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateUserAgnet(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateUserAgnet(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update User Agent of a  Subscriber Profile with DTO - ", subsProfilePersistDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateUserAgent(subsProfilePersistDTO, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update Subscriber Profile ");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while updating the user agent of a  Subscriber profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT: update Subscriber Profile ");
    }


    /**
     * method for Create Subscriber Roaming Profile
     *
     * @param mdn              String
     * @param roamingClusterId ArrayList<Integer>
     * @param persisterTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void createSubscrRoamingProfile(String mdn, ArrayList<Integer> roamingClusterId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubscrRoamingProfile(mdn, ArrayList<Integer>, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Create Subscr Roaming Profile - ", KnGDPRTemplate.mdn(mdn));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            subsProfilePersistDTO.setRoamingTypes(roamingClusterId);
            KnPOCSubscrRoamingProfileDAO subscrRoamingProfileDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrRoaminProfileDAO(xdmPttServerId);
            subscrRoamingProfileDAO.insert(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            knLogger.info(methodName, "EXIT: Create Subscriber Roaming Profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while creating the Subscriber roaming profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, null);
        }
    }

    /**
     * Method to create Corporate Profile
     *
     * @param corpProfilePersistDTO KnCorpProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @return int (corp Id)
     * @throws KnDAOException exception DB Layer
     */
    public int createCorporateProfile(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createCorporateProfile(KnCorpProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        int corpId = -1;
        knLogger.info(methodName, "ENTRY: Create Corporate Profile ", corpProfilePersistDTO);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            corpId = genInfoUtil.retrieveIdForTable(KnProvConstants.TABLE_CORP_INFO, xdmPttServerId,
                    KnProvConstants.TABLE_CORP_INFO_COLUMN, false, KnConstants.DUAL_DATA_STORE);

            corpProfilePersistDTO.setCorpId(corpId);
            long profileCreationTime = Calendar.getInstance().getTimeInMillis();
            corpProfilePersistDTO.setProfileCreationTime(profileCreationTime);
            corpProfilePersistDTO.setLastProfileUpdateTime(profileCreationTime);
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpInfoDAO.insert(corpProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while creating the Corporate profile", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
        knLogger.info(methodName, "EXIT: Create Corporate Profile");
        return corpId;
    }

    /**
     * method to retrieve Corporation Id
     *
     * @param extCorpId    String
     * @param persisterTxn KnPersisterTxn
     * @return int (corp Id)
     * @throws KnDAOException exception of DB Layer
     */
    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveCorporationId(String, KnPersisterTxn)";
        int corporationId = -1;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: retrieve Corporation Id");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corporationId = corpInfoDAO.getCorpId(extCorpId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Corporate Id", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
        knLogger.debug(methodName, "EXIT: Retrieve Corporate Id");
        return corporationId;
    }

    public String retrieveExtCorporationId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveExtCorporationId(String, KnPersisterTxn)";
        String extCorpId = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: retrieve Ext Corporation Id");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            extCorpId = corpInfoDAO.getExtCorpId(corpId, persisterTxn);
            if (extCorpId != null) {
                extCorpId = extCorpId.trim();
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Ext Corporate Id", xdmPttServerId,
                    KnProvDAOSourceTypes.CORPINFO, null);
        }
        knLogger.info(methodName, "EXIT: retrieve Ext Corporate Id - ", extCorpId);

        return extCorpId;
    }


    public Integer retrievePrivacyAmbDiscListenFlag(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePrivacyAmbDiscListenFlag(String, KnPersisterTxn)";
       // boolean privacyAmbDiscListenFlag = false;
       // boolean privacyAmbDiscListenFlagInXdmsSvcConfig = false;
        Integer privacyAmbDiscListen=null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "--->ENTRY: retrieve privacyAmbDiscListen value");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            knLogger.debug( methodName, "xdmPttServerId::"+xdmPttServerId);
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            privacyAmbDiscListen = corpInfoDAO.getPrivacyAmbDiscListenFlag(corpId, persisterTxn);

            knLogger.debug( methodName, "corpInfoDAO::"+corpInfoDAO+" corpId::"+corpId+" privacyAmbDiscListen::"+privacyAmbDiscListen);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the privacyAmbDiscListenFlag", xdmPttServerId,
                    KnProvDAOSourceTypes.CORPINFO, null);
        }
        knLogger.info(methodName, "EXIT: retrieve privacyAmbDiscListen - ", privacyAmbDiscListen);

        return privacyAmbDiscListen;
    }

    /**
     * method for the retrieve Corporate Profile
     *
     * @param extCorpId    String
     * @param persisterTxn KnPersisterTxn
     * @return KnCorpProfileRespDTO
     * @throws KnDAOException exception DB Layer
     */
    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        return retrieveCorporateProfile(extCorpId, false, persisterTxn);
    }

    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveCorporateProfile(String, boolean, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: retrieve Corporation profile");
        KnOPCorpProfileInfoDTO respDTO;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
            corpProfilePersistDTO.setExtCorpId(extCorpId);

            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            respDTO = corpInfoDAO.getCorpProfileInfo(corpProfilePersistDTO, readOnly, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: retrieve Corporate Profile ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Corporate Id", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }

        return respDTO;
    }

    /**
     * method for retrieve Corporate Subscriber Count (list Corporate Subscribers)
     *
     * @param corpId       int
     * @param persisterTxn KnPersisterTxn
     * @return int (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public int retrieveCorpSubscriberCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveCorpSubscriberCount(int, KnPersisterTxn)";
        boolean ownedTxn = false;
        int corpSubscriberCount = -1;

        knLogger.debug(methodName, "ENTRY: Retrieving the Corporation [", corpId, "] Subscriber Count");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            corpSubscriberCount = subscrInfoDAO.getCorpSubscriberCnt(corpId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }


        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Corporation Subscriber Count ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }

        knLogger.info(methodName, "EXIT: Retrieved the Corporation [", corpId, "] Subscriber Count - ", corpSubscriberCount);

        return corpSubscriberCount;
    }

    /**
     * method for Retrieve Roaming Cluster Id (configuration)
     *
     * @param persisterTxn KnPersisterTxn
     * @return Map<Integer, String>
     * @throws KnDAOException exception from DB layer
     */
    public Map<Integer, String> retrieveRoamingClusterId(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveRoamingClusterId(KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, String> roamingClusterListMap = null;
        knLogger.debug(methodName, "ENTRY: Roaming Cluster List; Txn - ", persisterTxn);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            query = QRY_SELECT_ROAMING_CLUSTER_INFO;

            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            roamingClusterListMap = new HashMap<Integer, String>();
            while (rs.next()) {
                roamingClusterListMap.put(rs.getInt("CLUSTERID"), rs.getString("CLUSTERNAME"));
            }

            if (roamingClusterListMap == null || roamingClusterListMap.isEmpty()) {
                knLogger.debug(methodName, "Roaming cluster Id are not configured");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found", xdmPttServerId, "ROAMINGCLUSTERINFO", query);
            } else {
                knLogger.debug(methodName, "Retrieved Roaming Cluster Id - ", roamingClusterListMap.toString());
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "Unexpected exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "failed to retrieve roaming cluster info", xdmPttServerId, "ROAMINGCLUSTERINFO", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Roaming Cluster List -> ", roamingClusterListMap.toString());
        }

        return roamingClusterListMap;
    }

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSubscriberInfo(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subsProfileDTO = subscrInfoDAO.selectSubscriberProfile(mdn, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving Subscriber Info for mdn - " + mdn, xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO," AciveFs :",subsProfileDTO.getActiveFS2());

        return subsProfileDTO;
    }

    public Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> retrieveSubscriberInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "retrieveSubscriberInfo(List<String>, KnPersisterTxn)";
        Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> subsProfileMap = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info for mdn list - ", KnGDPRTemplate.mdnList(mdnList));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subsProfileMap = subscrInfoDAO.selectSubscriberProfile(mdnList, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving Subscriber Info for mdn list - " + KnGDPRTemplate.mdnList(mdnList), xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info for mdn list - ", KnGDPRTemplate.mdnList(mdnList));

        return subsProfileMap;
    }

    public KnOPSubsProfileInfoDTO retrieveBaseMdnByMcpttId(String mcpttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveBaseMdnByMcpttId(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info mcpttId - ",KnGDPRTemplate.mcpttId(mcpttId));
        try {
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subsProfileDTO = subscrInfoDAO.retrieveBaseMdnByMcpttId(mcpttId, persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving Subscriber Info for mcpttId - " +KnGDPRTemplate.mcpttId(mcpttId), xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO," AciveFs :",subsProfileDTO.getActiveFS2());

        return subsProfileDTO;
    }

    public KnOPSubsAddlInfoProfileDTO retrieveSubscrAddlInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSubscrAddlInfo(String, KnPersisterTxn)";
        KnOPSubsAddlInfoProfileDTO subsAddlInfoProfileDTO;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber AddlInfo");
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subsAddlInfoProfileDTO = subscrInfoDAO.selectSubscriberAddlProfile(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber AddlInfo - ", subsAddlInfoProfileDTO);
        return subsAddlInfoProfileDTO;
    }

    public int retrieveMdnServiceAuthStatusByUserId(String userId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveMdnServiceAuthStatusByUserId(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        int serviceAuthStatus = -1;
        knLogger.debug(methodName, "ENTRY: retrieve ServiceAuthStatus for userId -  ", KnGDPRTemplate.userId(userId));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            serviceAuthStatus = pocSubscrInfoDAO.getServiceAuthStatusByUserId(userId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving service auth status for userId - " + KnGDPRTemplate.mdn(userId), xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.info(methodName, "EXIT:Retrieved serviceAuthStatus - ", serviceAuthStatus);

        return serviceAuthStatus;
    }

    public void activateSubscriber(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "activateSubscriber(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: activate subscriber - ", subsProfilePersistDTO);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.activateSubscriber(subsProfilePersistDTO, persisterTxn);
            knLogger.debug(methodName, "activated Subscriber successfully");

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: activate Subscriber");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while activate Subscriber ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    /**
     * method to retrieve the Subscriber Roaming Profile from the table PoCSubscrRoamingProfile
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return ArrayList<Integer>
     * @throws KnDAOException
     */
    public ArrayList<Integer> retrieveSubscrRoamingProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSubscRoamingProfile(String, KnpersisterTxn)";
        boolean ownedTxn = false;
        ArrayList<Integer> roamingTypes;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Roaming Profile for mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrRoamingProfileDAO subscrRoamingProfile = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrRoaminProfileDAO(xdmPttServerId);
            roamingTypes = subscrRoamingProfile.selectRoamingTypes(mdn, persisterTxn);
            knLogger.debug(methodName, "Roaming types list - ", roamingTypes, " for the mdn - ", KnGDPRTemplate.mdn(mdn));
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Subscriber roaming profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, null);
        }
        knLogger.debug(methodName, "EXIT: retrieve Subscriber Roaming Profile");

        return roamingTypes;
    }

    /**
     * method to delete to subscriber roaming profile
     * this method will invoke the table dao to delete the profile
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void deleteSubscrRoamingProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscrRoamingProfile(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: delete Subscriber Roaming Profile");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            KnPOCSubscrRoamingProfileDAO subscrRoamingProfileDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrRoaminProfileDAO(xdmPttServerId);
            subscrRoamingProfileDAO.delete(subsProfilePersistDTO, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: delete Subscriber Roaming Profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while deleting the Subscriber roaming profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, null);
        }

    }

    /**
     * method to update the service auth status
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
/*    public void updateServiceAuthStatus(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServiceAuthStatus(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Service Auth Status ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateServiceAuthStatus(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Service Auth Status");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update service auth Status ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }*/
// returned type of the  request is modified now
    public KnSubsProfilePersistDTO updateServiceAuthStatus(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServiceAuthStatus(KnSubsProfilePersistDTO, KnPersisterTxn)";
        KnSubsProfilePersistDTO knSubsProfilePersistDTO= null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Service Auth Status ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
    }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            knSubsProfilePersistDTO  = subscrInfoDAO.updateServiceAuthStatus(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Service Auth Status");

        } catch (KnDAOException e) {
            knLogger.error(methodName, KnConstants.DAO_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, KnConstants.UNEXPECTED_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update service auth Status ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        return knSubsProfilePersistDTO;
    }

    public KnSubsProfilePersistDTO updateServiceAuthStatusForUPM(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServiceAuthStatusForUPM(KnSubsProfilePersistDTO, KnPersisterTxn)";
        KnSubsProfilePersistDTO knSubsProfilePersistDTO= null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Service Auth Status ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
           // knSubsProfilePersistDTO  = subscrInfoDAO.updateServiceAuthStatus(subsProfilePersistDTO, persisterTxn);
            knSubsProfilePersistDTO  = subscrInfoDAO.updateServiceAuthStatusForUPM(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Service Auth Status");

        } catch (KnDAOException e) {
            knLogger.error(methodName, KnConstants.DAO_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, KnConstants.UNEXPECTED_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update service auth Status ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        return knSubsProfilePersistDTO;
    }

    /**
     * method to update the Subscriber Profile etag
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void updateLastProfileUpdateTime(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileUpdateTime(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update Last Profile Update Time ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateLastProfileUpdateTime(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Last Profile Update Time");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update Last Profile Update Time", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    public void deleteCorporateProfile(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorporateProfile(int, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: Delete Corporate Profile for CorpId - ", corpId);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
            corpProfilePersistDTO.setCorpId(corpId);

            KnCorpAddlInfoPersistDTO corpAddlInfoPersistDTO = new KnCorpAddlInfoPersistDTO();
            corpAddlInfoPersistDTO.setCorpId(corpId);

            KnCorpAddlInfoDAO corpAddlInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpAddlInfoDAO(xdmPttServerId);
            corpAddlInfoDAO.deleteCorpAddlInfo(corpAddlInfoPersistDTO,persisterTxn);


            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            //deleteing osm for corp.
            corpInfoDAO.onDeleteCorpProfile(corpId, persisterTxn);
            //CORPSRSMAPPING
            corpInfoDAO.deleteCorpSrsMapping(corpProfilePersistDTO, persisterTxn);
            //deletint the record from DG.RECORDING_TARGET_INFO.
            String target = corpId + "_" + "*";
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            commonXDMServerDAO.deleteRecordingInfoTargetByTarget(new ArrayList<>(List.of(target)), persisterTxn);

            /**
             * UCSPROVCONFIG-7618:
             * delete the new tables introduced as part of Role-Based and Hierarchy initiatives.
             * 1. DG.CORP_PTTSETTING_MAP
             * 2. DG.ANCHOR_POC_INFO
             * 3. DG.CORP_HIERARCHY_GEOCODE_MAP
             * 4. DG.CORP_HIERARCHY_DETAILS
             * 5. DG.CORP_HIERARCHY_DEPTH
             */
            corpInfoDAO.deleteRoleBasedAndHierarchyCorpData(corpId, persisterTxn);

            //POCCORPCONFIGINFO
            corpInfoDAO.deleteCorpConfigInfo(corpProfilePersistDTO, persisterTxn);
            //POCCORPINFO
            corpInfoDAO.delete(corpProfilePersistDTO, persisterTxn);


            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: delete corporate profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while delete Corporate Profile", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }

    }

    public void deleteSubscriberProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscriberProfile(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: delete Subscriber Profile ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);

            KnSubsAddlInfoPersistDTO subsAddlInfoPersistDTO = new KnSubsAddlInfoPersistDTO();
            subsAddlInfoPersistDTO.setMdn(mdn);

            KnPOCSubscrAddlInfoDAO subscrAddlInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
            subscrAddlInfoDAO.deleteSubsAddlInfo(subsAddlInfoPersistDTO, persisterTxn);

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.delete(subsProfilePersistDTO, persisterTxn);

            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            //deleting the record from DG.RECORDING_TARGET_INFO.
            commonXDMServerDAO.deleteRecordingInfoTargetByTarget(Collections.singleton(mdn), persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: delete Subscriber profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while delete Subscriber Profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    /**
     * method to retrieve the Presence Service Config
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnPresenceServiceConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnPresenceServiceConfigDTO retrievePresenceServiceConfig(String presencePttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePresenceServiceConfig(String, KnPersisterTxn)";
        KnPresenceServiceConfigDTO preServConfigDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve presence service config");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from presence Service Config
            KnPresenceServiceConfigDAO preServConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createrPresenceServerConfigDAO(xdmPttServerId);
            preServConfigDTO = preServConfigDAO.selectPresenceServiceConfig(presencePttServerId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving  presence service config ", xdmPttServerId, KnProvDAOSourceTypes.PRESENCESRVCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved  presence service config - ", preServConfigDTO);

        return preServConfigDTO;
    }

    /**
     * method to retrieve the poc Service Config
     *
     * @param pocPttServerId
     * @param persisterTxn   KnPersisterTxn
     * @return KnPOCSvcConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnPOCSvcConfigDTO retrievePOCSvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePOCSvcConfig(String, KnPersisterTxn)";
        KnPOCSvcConfigDTO pocSvcConfigDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve POC service config");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from POC Service Config
            KnPOCSvcConfigDAO pocSvcConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSvcConfigDAO(xdmPttServerId);
            pocSvcConfigDTO = pocSvcConfigDAO.selectPOCSvcConfig(pocPttServerId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed while retrieving  POC service config ", xdmPttServerId,
                    KnProvDAOSourceTypes.POCSRVCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved  POC service config - ", pocSvcConfigDTO);

        return pocSvcConfigDTO;
    }

    /**
     * method to retrieve the POC Registrar Service Config
     *
     * @param pocPttServerId
     * @param persisterTxn   KnPersisterTxn
     * @return KnPOCRegistrarSrvcConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnPOCRegistrarSrvcConfigDTO retrievePOCregistrarSrvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePOCregistrarSrvcConfig( String,KnPersisterTxn)";
        KnPOCRegistrarSrvcConfigDTO pocRegistrarSrvcConfigDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve POC Registrar service config");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from Registrar Service Config
            KnPOCRegistrarSrvcConfigDAO pocRegistrarSrvcConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCRegistrarSrvcConfigDAO(xdmPttServerId);
            pocRegistrarSrvcConfigDTO = pocRegistrarSrvcConfigDAO.selectPOCRegistrarSrvcConfig(pocPttServerId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving POC Registrar service config ", xdmPttServerId, KnProvDAOSourceTypes.POCREGISTRARSRVCCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved POC Registrar service config - ", pocRegistrarSrvcConfigDTO);

        return pocRegistrarSrvcConfigDTO;
    }

    /**
     * method to retrieve the POC Registrar Service Config
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnPOCRegistrarSrvcConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnSIPProxySvcConfigDTO retrieveSIPProxySvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSIPProxySvcConfig( String,KnPersisterTxn)";
        KnSIPProxySvcConfigDTO sipProxySvcConfigDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve SIP Proxy service config", pocPttServerId);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting sip proxy config from SIPProxyConfig
            KnSIPProxySvcConfigDAO sipProxySvcConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSIPProxySvcConfigDAO(xdmPttServerId);
            sipProxySvcConfigDTO = sipProxySvcConfigDAO.selectSIPProxySvcConfig(pocPttServerId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving SIP Proxy service config ", xdmPttServerId, KnProvDAOSourceTypes.SIPPROXYSVCCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved SIP Proxy service config - ", sipProxySvcConfigDTO);

        return sipProxySvcConfigDTO;
    }

    /**
     * method to retrieve the POC Feature Access Info
     *
     * @param featureAccIndex int
     * @param persisterTxn    KnPersisterTxn
     * @return KnFeatureAccessInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnFeatureAccessNumberInfoDTO retrieveFeatureAccessNumberInfo(int featureAccIndex, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveFeatureAccessInfo( int, KnPersisterTxn)";
        KnFeatureAccessNumberInfoDTO featureAccessNumberInfoDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve feature access number info");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from Registrar Service Config
            KnFeatureAccessNumberInfoDAO featureAccessInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createFeatureAccessNumberInfoDAO(xdmPttServerId);
            featureAccessNumberInfoDTO = featureAccessInfoDAO.selectFeatureAccessNumberInfo(featureAccIndex, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed while retrieving  feature access number info ", xdmPttServerId, KnProvDAOSourceTypes.FEATUREACCESSNUMBERINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved  feature access number info - ", featureAccessNumberInfoDTO);

        return featureAccessNumberInfoDTO;
    }

    /**
     * method to retrieve the POC Feature Access Info
     *
     * @param featureAccIndex int
     * @param persisterTxn    KnPersisterTxn
     * @return KnFeatureAccessInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnFeatureAccessInfoDTO retrieveFeatureAccessInfo(int featureAccIndex, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveFeatureAccessInfo( int, KnPersisterTxn)";
        KnFeatureAccessInfoDTO featureAccessInfoDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve feature access info");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from Registrar Service Config
            KnFeatureAccessInfoDAO featureAccessInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createFeatureAccessInfoDAO(xdmPttServerId);
            featureAccessInfoDTO = featureAccessInfoDAO.selectFeatureAccessInfo(featureAccIndex, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed while retrieving  feature access info ", xdmPttServerId, KnProvDAOSourceTypes.FEATUREACCESSINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved  feature access info - ", featureAccessInfoDTO);

        return featureAccessInfoDTO;
    }


    /**
     * method to retrieve the software package Config
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnSWPkgConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public ArrayList<KnSWPkgConfigDTO> retrieveSWPkgConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSWPkgConfig( KnPersisterTxn)";
        ArrayList<KnSWPkgConfigDTO> swPkgConfigDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve SW Pkg config");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from XDMS Doc SubscriberPrefix Config
            KnSWPkgConfigDAO swPkgConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSWPkgConfigDAO(xdmPttServerId);
            swPkgConfigDTO = swPkgConfigDAO.selectSWPkgConfig(persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving SW Pkg config ",
                    xdmPttServerId, KnProvDAOSourceTypes.SWPKGCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved SW Pkg config - ", swPkgConfigDTO);

        return swPkgConfigDTO;
    }

    /**
     * method to retrieve the XDMS Service Config
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnXDMSSvcConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnXDMSSvcConfigDTO retrieveXDMSSvcConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveXDMSSvcConfig( KnPersisterTxn)";
        KnXDMSSvcConfigDTO xdmsSvcConfigDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve XDMS service config");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from XDMS Service Config
            KnXDMSSvcConfigDAO xdmsSvcConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createXDMSSvcConfigDAO(xdmPttServerId);
            xdmsSvcConfigDTO = xdmsSvcConfigDAO.selectXDMSSrvConfig(persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving XDMS service config ", xdmPttServerId, KnProvDAOSourceTypes.XDMSSRVCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved XDMS service config - ", xdmsSvcConfigDTO);

        return xdmsSvcConfigDTO;
    }

    /**
     * method to retrieve the Dial Plan Info
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnDialPlanInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnDialPlanInfoDTO retrieveDialPlanInfo(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveDialPlanInfo( KnPersisterTxn)";
        KnDialPlanInfoDTO dialPlanInfoDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve Dial plan info");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from Dial Plan Info
            KnDialPlanInfoDAO dialPlanInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createDialPlanInfoDAO(xdmPttServerId);
            dialPlanInfoDTO = dialPlanInfoDAO.selectDialPlanInfo(persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving  Dial plan info ", xdmPttServerId, KnProvDAOSourceTypes.DIALPLANINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved  Dial plan info - ", dialPlanInfoDTO);

        return dialPlanInfoDTO;
    }

    /**
     * method to retrieve the XDMS Doc Sub Prefix Config
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnXDMSDocSubPrxConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnXDMSDocSubPrxConfigDTO retrieveXDMSDocSubPrxConfig(String presPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveXDMSDocSubPrxConfig(String, KnPersisterTxn)";
        KnXDMSDocSubPrxConfigDTO xdmsDocSubPrxConfigDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve XDMS doc subscriber prefix config");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from XDMS Doc SubscriberPrefix Config
            KnXDMSDocSubPrxConfigDAO xdmsDocSubPrxConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createXDMSDocSubPrxConfigDAO(xdmPttServerId);
            xdmsDocSubPrxConfigDTO = xdmsDocSubPrxConfigDAO.selectXDMSDocSubPrxConfig(presPttServerId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving  XDMS doc subscriber prefix config ",
                    xdmPttServerId, KnProvDAOSourceTypes.XDMSDOCSUBPRXCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved  XDMS doc subscriber prefix config - ", xdmsDocSubPrxConfigDTO);

        return xdmsDocSubPrxConfigDTO;
    }

    /**
     * method to retrieve the Insta poc Service Config
     *
     * @param pocPttServerId
     * @param persisterTxn   KnPersisterTxn
     * @return KnInstaPOCSrvcCfgDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnInstaPOCSrvcCfgDTO retrieveInstaPOCSrvcCfg(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveInstaPOCSrvcCfg(String, KnPersisterTxn)";
        KnInstaPOCSrvcCfgDTO instaPOCSrvcCfgDTO = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTY: Retrieve insta POC service config");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // getting doc config info from Insta POC service config
            KnInstaPOCSrvcCfgDAO instaPOCSrvcCfgDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createInstaPOCSrvcCfgDAO(xdmPttServerId);
            instaPOCSrvcCfgDTO = instaPOCSrvcCfgDAO.selectInstaPOCsrvcCfg(pocPttServerId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving insta POC service config ",
                    xdmPttServerId, KnProvDAOSourceTypes.INSTAPOCSRVCONFIG, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved insta POC service config - ", instaPOCSrvcCfgDTO);

        return instaPOCSrvcCfgDTO;
    }

    /**
     * method to update the corp profile etag
     *
     * @param corpId       int Corporation Id
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateCorpProfileLastUpdateTime(int corpId, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpProfileLastUpdateTime(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update Corp Profile lastUpdateTimer ", corpId);
        try {

            //opening the transaction if null is received
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
            corpProfilePersistDTO.setCorpId(corpId);
            corpProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);

            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpInfoDAO.updateLastProfileTime(corpProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update lastUpdate Timer for Corp profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update lastUpdate Timer for Corp profile", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
    }

    /**
     * method to update the external corp ID And last update time
     *
     * @param corpProfilePersistDTO KnCorpProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */

    public void updateExternalCorpID(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateExternalCorpID(KnCorpProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update External Corp ID ");
        try {

            //opening the transaction if null is received
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            corpProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpInfoDAO.updateExtCorpID(corpProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update External Corp ID");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update External Corp ID", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
    }

    @Override
    public void updateAccountIdForSubscr(String accountId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateAccountIdForSubscr(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:update accountid of subscriber in pocsubscrinfo table with accountId - ", accountId);

        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        pocSubscrInfoDAO.updateAccountIdForSubscr(accountId, corpId, persisterTxn);

        knLogger.debug(methodName, "EXIT:Updated AccountId for subscribers - ");
    }

    /**
     * method to create the subscriber profile
     * this method is used by the change MDN operation to for creation of new profile.
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
/*
    public void createNewSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createNewSubscrProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Create New Subscriber Profile ", subsProfilePersistDTO.getMdn());

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            pocSubscrInfoDAO.createSubsProfile(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Create new Subscriber Profile ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while creating the new Subscriber profile", xdmPttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }
*/

    public void createNewSubscrAddlProfile(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createNewSubscrAddlProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Create New Subscriber_AddlInfo Profile ", KnGDPRTemplate.mdn(subsProfilePersistDTO.getMdn()));
        KnPOCSubscrAddlInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        pocSubscrInfoDAO.createSubsAddlInfoProfile(subsProfilePersistDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT: Create new Subscriber_AddlInfo Profile ");

    }


    public KnSubsProfilePersistDTO createNewSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createNewSubscrProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        KnSubsProfilePersistDTO knSubsProfilePersistDTO= null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Create New Subscriber Profile ", KnGDPRTemplate.mdn(subsProfilePersistDTO.getMdn()));

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            knSubsProfilePersistDTO= pocSubscrInfoDAO.createSubsProfile(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Create new Subscriber Profile ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, KnConstants.DAO_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, KnConstants.UNEXPECTED_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while creating the new Subscriber profile", xdmPttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        return  knSubsProfilePersistDTO;
    }
    /**
     * method to retrieve the Client Password
     *
     * @param oldMDN       String old Subscriber
     * @param persisterTxn KnPersisterTxn
     * @return String client Password
     * @throws KnDAOException DB Layer Exception
     */
    public String retrieveClientPassword(String oldMDN, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveClientPassword(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        String clientPassword = null;
        knLogger.debug(methodName, "ENTRY: retrieve Client Password - ", KnGDPRTemplate.mdn(oldMDN));

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            clientPassword = pocSubscrInfoDAO.retrieveClientPassword(oldMDN, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the client password", xdmPttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT: retrieve client password ", clientPassword);
        return clientPassword;
    }

    /**
     * method to update the MDN of the Subscriber Roaming Profile
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateSubscrRoamingProfileMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrRoamingProfileMdn(String, String, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update Subscr roaming Profile: oldmdn : ", KnGDPRTemplate.mdn(oldMdn), ", new MDN: ", KnGDPRTemplate.mdn(newMdn));
        

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrRoamingProfileDAO pocSubscrRoamingProfileInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().
                    createPOCSubscrRoaminProfileDAO(xdmPttServerId);
            pocSubscrRoamingProfileInfoDAO.updateMdn(oldMdn, newMdn, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT:  updating Subscriber roaming profile mdn ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while updating Subscriber roaming profile mdn", xdmPttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, null);
        }
    }

    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @return int (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public int retrieveSubscriberCount(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSubscriberCount(KnPersisterTxn)";
        boolean ownedTxn = false;
        int subscriberCount = -1;

        knLogger.debug(methodName, "ENTRY: Retrieving the Subscriber Count");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscriberCount = subscrInfoDAO.getSubscriberCount(persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }


        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Subscriber Count ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT: Retrieved the Subscriber Count - ", subscriberCount);

        return subscriberCount;
    }


    /**
     * method to retrieve the Signaling Cad Name
     *
     * @param pttServerId  String
     * @param persisterTxn String
     * @return String - Signaling card name
     * @throws KnDAOException DB Layer Exception
     */
    public String retrieveSignalingCardName(String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSignalingCardName(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: retrieve Signaling Card Name for pttserver id - ", pttServerId, "",
                " with transaction - ", persisterTxn);

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String signalingCardName = null;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = "SELECT SIGNALINGCARD_NAME FROM DG.SIGNALINGCARDINFO WHERE PTTSERVERID = ?";

            knLogger.debug(methodName, "getting Connection for PTT ID - ", xdmPttServerId);
            if (ownedTxn) {
                conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            } else {
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            }

            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pttServerId);
            knLogger.debug(methodName, "QUERY: Executing with PttSerer Id ", pttServerId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                signalingCardName = rs.getString("SIGNALINGCARD_NAME");
            }

            if (signalingCardName == null || signalingCardName.isEmpty()) {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Record Found", pttServerId,
                        KnProvDAOSourceTypes.SIGNALINGCARDINFO, query);
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the signaling card info ", xdmPttServerId,
                    KnProvDAOSourceTypes.SIGNALINGCARDINFO, null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: Signaling card name for ptt server id [ ", pttServerId, "] is "
                    , signalingCardName);
        }
        return signalingCardName;
    }

    public KnLocationServiceConfigDTO retrieveLocationSrvcConfig(String presencePttId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "retrieveLocationSrvcConfig(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve Location Service Config for presence pttId - ", presencePttId,
                " with Txn - ", persisterTxn);
        String query = null;
        Connection conn;
        PreparedStatement pStmt;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnLocationServiceConfigDTO locationSvcConfigDTO = new KnLocationServiceConfigDTO();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction ");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = "SELECT PTTSERVERID, LOCATIONPUBLISHINTERVAL, LOCATION_SVC_ENABLED FROM DG.LOCATIONSERVICECONFIG " +
                    "WHERE PTTSERVERID =?";

            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, presencePttId);

            knLogger.debug(methodName, "Query: Executing - ", query, " with pttid - ", presencePttId);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                locationSvcConfigDTO.setPttServerId(rs.getString("PTTSERVERID"));
                locationSvcConfigDTO.setLocationPublishInterval(rs.getInt("LOCATIONPUBLISHINTERVAL"));
                locationSvcConfigDTO.setLocation_Svc_Enabled(rs.getInt("LOCATION_SVC_ENABLED"));

            } else {
                knLogger.error(methodName, "Location Service config doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Location Service config Doesnt exist",
                        presencePttId, KnProvDAOSourceTypes.LOCATION_SERVICE_CONFIG, query);
            }

            if (ownedTxn) {
                persisterTxn.save();
                knLogger.debug(methodName, "Saving the Transaction");
            }
            knLogger.debug(methodName, "returning location Srvc Config ", locationSvcConfigDTO);
            return locationSvcConfigDTO;

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred - ", dbConne);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred - ", sqlE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to retrieve Location Service config - " + sqlE.getMessage(),
                    presencePttId, KnProvDAOSourceTypes.LOCATION_SERVICE_CONFIG, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Location Service config - " + e.getMessage(),
                    presencePttId, KnProvDAOSourceTypes.LOCATION_SERVICE_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT : retrieve Location Service config - ", locationSvcConfigDTO
            );
        }
    }

    public void updateActiveFS(KnSubsProfilePersistDTO subsProfilePersistDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateActiveFS(KnSubsProfilePersistDTO, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update Active FS with Profile DTO - ", subsProfilePersistDTO,
                ", txn - ", persisterTxn);
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateActiveFS(subsProfilePersistDTO, readOnly, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Active FS ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred- ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred- ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while updating Active FS", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    /**
     * currently this method will update only Corporate Name and Last Profile Time
     *
     * @param corpProfilePersistDTO KnCorpProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Exception
     */
    public void updateCorporateProfile(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateProfile(KnCorpProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update Corporate Profile ", corpProfilePersistDTO);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            corpProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpInfoDAO.update(corpProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Corporate Profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update Corporate profile", xdmPttServerId,
                    KnProvDAOSourceTypes.CORPINFO, null);
        }
    }

    /**
     * Method Create PAM Account
     *
     * @param pamAccPersistDTO KnPAMAccPersistDTO
     * @param persisterTxn     KnPersisterTxn
     * @throws KnDAOException exception
     */
    public int createPAMAccountInfo(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        int pamAccId = 0;
        pamAccId = genInfoUtil.retrieveIdForTable(KnProvConstants.TABLE_PAM_ACC_INFO, xdmPttServerId,
                KnProvConstants.TABLE_PAM_ACC_INFO_COLUMN, false, KnConstants.DUAL_DATA_STORE);

        pamAccPersistDTO.setPamAccId(pamAccId);
        long profileCreationTime = Calendar.getInstance().getTimeInMillis();
        pamAccPersistDTO.setCreationTime(profileCreationTime);
        pamAccPersistDTO.setLastUpdateTime(profileCreationTime);
        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.insert(pamAccPersistDTO, persisterTxn);

        return pamAccId;
    }

    /**
     * method to update Subscriber Profile
     *
     * @param pamAccPersistDTO KnPAMAccPersistDTO
     * @param persisterTxn     KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updatePAMAccountInfo(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.update(pamAccPersistDTO, persisterTxn);

    }

    public void updatePAMBillingMDN(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        pamAccPersistDTO.setLastUpdateTime(Calendar.getInstance().getTimeInMillis());
        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.updateBillingMDN(pamAccPersistDTO, persisterTxn);

    }

    public void deletePAMAccountInfo(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.delete(pamAccPersistDTO, persisterTxn);

    }

    /**
     * method to retrieve PAM Account Id
     *
     * @param extPAMAccId  String
     * @param persisterTxn KnPersisterTxn
     * @return int (corp Id)
     * @throws KnDAOException exception of DB Layer
     */
    public int retrievePAMAccId(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        int pamAccId = -1;

        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccId = pamAccInfoDAO.getPAMAccId(extPAMAccId, persisterTxn);

        return pamAccId;
    }

    public KnOPPAMAccInfoDTO retrievePAMAccInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnOPPAMAccInfoDTO pamAccInfo = null;

        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfo = pamAccInfoDAO.getPAMAccountInfo(extPAMAccId, persisterTxn);

        return pamAccInfo;
    }

    public KnOPPAMAccInfoDTO retrievePAMAccInfoFromId(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnOPPAMAccInfoDTO pamAccInfo = null;

        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfo = pamAccInfoDAO.getPAMAccInfoFromId(pamAccId, readOnly, persisterTxn);

        return pamAccInfo;
    }

    /**
     * Method Create PAM Subs profile
     *
     * @param pamAccPersistDTO KnPAMAccPersistDTO
     * @param persisterTxn     KnPersisterTxn
     * @throws KnDAOException exception
     */
    public void createPAMSubsProfile(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        long profileCreationTime = Calendar.getInstance().getTimeInMillis();
        pamAccPersistDTO.getProfileDetails().setCreationTime(profileCreationTime);
        pamAccPersistDTO.getProfileDetails().setLastUpdateTime(profileCreationTime);
        KnPAMSubscrProfInfoDAO pamSubsProfInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsProfInfoDAO(xdmPttServerId);
        pamSubsProfInfoDAO.insert(pamAccPersistDTO, persisterTxn);

    }

    /**
     * Method Create PAM Account
     *
     * @param pamAccPersistDTO KnPAMAccPersistDTO
     * @param persisterTxn     KnPersisterTxn
     * @throws KnDAOException exception
     */
    public void updatePAMSubsProfile(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        long profileCreationTime = Calendar.getInstance().getTimeInMillis();
        pamAccPersistDTO.setLastUpdateTime(profileCreationTime);
        KnPAMSubscrProfInfoDAO pamSubsProfInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsProfInfoDAO(xdmPttServerId);
        pamSubsProfInfoDAO.update(pamAccPersistDTO, persisterTxn);

    }

    /**
     * Method Create PAM Account
     *
     * @param pamAccPersistDTO KnPAMAccPersistDTO
     * @param persisterTxn     KnPersisterTxn
     * @throws KnDAOException exception
     */
    public void deletePAMSubsProfile(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnPAMSubscrProfInfoDAO pamSubsProfInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsProfInfoDAO(xdmPttServerId);
        pamSubsProfInfoDAO.delete(pamAccPersistDTO, persisterTxn);

    }


    public KnPAMSubsProfInfoDTO getPAMSubsProfInfo(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPAMSubsProfInfoDTO subsProfInfoDTO = null;
        KnPAMSubscrProfInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsProfInfoDAO(xdmPttServerId);
        subsProfInfoDTO = pamAccInfoDAO.getPAMSubsProfInfo(pamAccId, persisterTxn);

        return subsProfInfoDTO;
    }

    public KnPAMSubsProfInfoDTO retrievePAMSubsProfInfo(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPAMSubsProfInfoDTO subsProfInfoDTO = null;

        KnPAMSubscrProfInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsProfInfoDAO(xdmPttServerId);
        subsProfInfoDTO = pamAccInfoDAO.retrievePAMSubsProfInfo(pamAccId, persisterTxn);

        return subsProfInfoDTO;
    }

    /**
     * method to retrieve the poc Service Config
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnPOCSvcConfigDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnPAMSvcConfigDTO retrievePAMSvcConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPAMSvcConfigDTO pamSvcConfigDTO = null;

        // getting doc config info from POC Service Config
        KnPAMSvcConfigDAO pamSvcConfigDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSvcConfigDAO(xdmPttServerId);
        pamSvcConfigDTO = pamSvcConfigDAO.selectPAMSvcConfig(persisterTxn);

        return pamSvcConfigDTO;
    }

    /**
     * Method Create Subscriber Profile
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO[]
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException exception
     */
    public void createSubscrProfile(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubscrProfile(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Create Subscriber Profile ", subsProfilePersistDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            pocSubscrInfoDAO.insert(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Create Subscriber Profile ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while creating the Subscriber profile", xdmPttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, null);
        }
    }

    /**
     * method to update Subscriber Profile
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO []
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateSubscrProfile(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrProfile(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", subsProfilePersistDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.update(subsProfilePersistDTO, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update Subscriber Profile ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update the Subscriber profile", xdmPttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, null);
        }
    }

    public void deleteSubscriberProfile(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscriberProfile(List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: delete Subscriber Profile ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            KnPOCSubscrAddlInfoDAO subscrAddlInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
            KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO= KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
            subsAddOnPkgInfoDAO.deleteBulkSubAddlOnPkgs(mdns, persisterTxn);
            subscrAddlInfoDAO.deleteSubsAddlInfo(mdns, persisterTxn);
            subscrInfoDAO.delete(mdns, persisterTxn);


            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.info(methodName, "EXIT: delete Subscriber profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while delete Subscriber Profile", xdmPttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, null);
        }
    }

    /**
     * method for Create Subscriber Roaming Profile
     *
     * @param mdns             String []
     * @param roamingClusterId ArrayList<Integer>
     * @param persisterTxn     KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void createSubscrRoamingProfile(List<String> mdns, List<Integer> roamingClusterId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubscrRoamingProfile(List mdns, ArrayList<Integer>, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Create Subscr Roaming Profile - ", mdns.size());
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnPOCSubscrRoamingProfileDAO subscrRoamingProfileDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrRoaminProfileDAO(xdmPttServerId);
            subscrRoamingProfileDAO.insert(mdns, roamingClusterId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Create Subscriber Roaming Profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while creating the Subscriber roaming profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, null);
        }
    }

    /**
     * method to update the MDN of the Subscriber Roaming Profile
     *
     * @param oldMdns      List<String>
     * @param newMdns      List<String>
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateSubscrRoamingProfileMdn(List<String> oldMdns, List<String> newMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrRoamingProfileMdn(List<String>, List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update Subscr roaming Profile: oldmdn : ", oldMdns.size(), ", new MDN: ", newMdns.size()
        );

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrRoamingProfileDAO pocSubscrRoamingProfileInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().
                    createPOCSubscrRoaminProfileDAO(xdmPttServerId);
            pocSubscrRoamingProfileInfoDAO.updateMdn(oldMdns, newMdns, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT:  updating Subscriber roaming profile mdn ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while updating Subscriber roaming profile mdn", xdmPttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, null);
        }
    }

    /**
     * method to delete to subscriber roaming profile
     * this method will invoke the table dao to delete the profile
     *
     * @param mdns         String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void deleteSubscrRoamingProfile(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscrRoamingProfile(List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: delete Subscriber Roaming Profile");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnPOCSubscrRoamingProfileDAO subscrRoamingProfileDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrRoaminProfileDAO(xdmPttServerId);
            subscrRoamingProfileDAO.delete(mdns, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.info(methodName, "EXIT: delete Subscriber Roaming Profile");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while deleting the Subscriber roaming profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, null);
        }

    }

    @Override
    public void deleteSubscrRoamingProfile(String mdn, ArrayList<Integer> roamingClusterId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscrRoamingProfile(String,ArrayList<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: delete Subscriber Roaming Profile for mdn", KnGDPRTemplate.mdn(mdn), "with roamingclusterIds", roamingClusterId);
        KnPOCSubscrRoamingProfileDAO subscrRoamingProfileDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrRoaminProfileDAO(xdmPttServerId);
        subscrRoamingProfileDAO.deleteRoamingType(mdn, roamingClusterId, persisterTxn);
        knLogger.debug(methodName, "EXIT: delete Subscriber Roaming Profile");

    }

    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @return int (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public int retrieveSubsCountforPAM(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        int subscriberCount = -1;

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscriberCount = subscrInfoDAO.getSubsCountforPAM(pamAccId, persisterTxn);

        return subscriberCount;
    }


    /**
     * method to update the service auth status
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateServiceAuthStatus(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServiceAuthStatus(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Service Auth Status ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateServiceAuthStatus(subsProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Service Auth Status");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update service auth Status ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }


    /**
     * method for retrieve billing mdn's list of pseudomdn and its respective data.
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<KnOPSubsProfileInfoDTO> (contains mdn and Service Auth status)
     * @throws KnDAOException exception DB Layer
     */
    public List<KnOPSubsProfileInfoDTO> retrievePAMAccountMDNsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<KnOPSubsProfileInfoDTO> pamProfileDetails;

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        pamProfileDetails = subscrInfoDAO.getPAMAccountMdnsDetails(pamAccId, persisterTxn);

        return pamProfileDetails;
    }


    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<String> mdns;

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdns = subscrInfoDAO.getPAMAccountMdns(pamAccId, persisterTxn);

        return mdns;
    }

    public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return subscrInfoDAO.getPAMAccountMdnsByInsertionTime(pamAccId, insertionTime, persisterTxn);
    }


    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<String> mdns;

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdns = subscrInfoDAO.getPAMAccountMdns(pamAccId, fetchSize, listMdn, persisterTxn);

        return mdns;
    }

    @Override
    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<String> mdns;

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdns = subscrInfoDAO.getPAMAccountMdns(pamAccId, startMdn, endMdn, fetchSize, persisterTxn);

        return mdns;
    }

    /**
     * method to retrieve the Subscriber Partition Config Info
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnSubsPartitionConfigDTO
     * @throws KnDAOException DB exception
     */
    public KnSubsPartitionConfigDTO retrievePartitionConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePartitionConfig(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: retrieve Subs Partition Config");
        KnSubsPartitionConfigDTO configDTO = null;
        PreparedStatement stmt = null;
        Connection conn;
        ResultSet resultSet = null;
        String query = "SELECT PTTServerId, MDNPartitionType_POC, EnableCorpAccountAnchoring, MaxSubscrPerXDMS, " +
                "MaxSubscrPerPoC FROM DG.SubscrPartitioningConfig WHERE PTTServerId =?";
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            stmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "QUERY: Executing ", query);
            stmt.setString(1,xdmPttServerId);
            resultSet = stmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", resultSet);
            if (resultSet.next()) {
                configDTO = new KnSubsPartitionConfigDTO();
                configDTO.setPttServerId(resultSet.getString("PTTServerId"));
                configDTO.setMdnPartitionTypePOC(resultSet.getInt("MDNPartitionType_POC"));
                configDTO.setEnableCorpAccAnch(resultSet.getInt("EnableCorpAccountAnchoring"));
                configDTO.setMaxSubsPerXDMS(resultSet.getInt("MaxSubscrPerXDMS"));
                configDTO.setMaxSubsPerPoC(resultSet.getInt("MaxSubscrPerPoC"));
            }

            if (configDTO == null) {
                knLogger.error(methodName, "Subscr Partition config is not configured");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Partition config not found",
                        xdmPttServerId, KnProvDAOSourceTypes.SUBS_PARTITION_CONFIG, query);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve poc Server Map", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBS_PARTITION_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : Subs Partition Config DTO -> ", configDTO);
        return configDTO;
    }

    /**
     * method to retrieve the Subscriber Partition Config Info
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnSubsPartitionConfigDTO
     * @throws KnDAOException DB exception
     */
    public Map<String, List<String>> retrievePttServerIds(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePartitionConfig(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: retrieve Subs Partition Config");
        Map<String, List<String>> pttServerInfo = null;
        Statement stmt = null;
        Connection conn;
        ResultSet resultSet = null;
        String query = " SELECT DISTINCT(PTTSERVERID), SIGNALINGCARDTYPE FROM DG.PTTSERVERIPINFO WHERE SIGNALINGCARDTYPE IN (32,34)";
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "QUERY: Executing ", query);
            resultSet = stmt.executeQuery(query);
            knLogger.debug(methodName, "QUERY: Executed ", resultSet);
            if (resultSet.next()) {
                List<String> pocPttIdList = new ArrayList<String>();
                List<String> prPttIdList = new ArrayList<String>();
                do {
                    int cardType = resultSet.getInt("SIGNALINGCARDTYPE");
                    String pttServerId = resultSet.getString("PTTSERVERID");
                    if (cardType == KnProvConstants.POC_CARD_TYPE ||
                            cardType == KnProvConstants.GEO_POC_CARD_TYPE) {
                        pocPttIdList.add(pttServerId);
                    } else if (cardType == KnProvConstants.PRESENCE_CARD_TYPE ||
                            cardType == KnProvConstants.GEO_PRESENCE_CARD_TYPE) {
                        prPttIdList.add(pttServerId);
                    }
                } while (resultSet.next());
                pttServerInfo = new HashMap<String, List<String>>();
                pttServerInfo.put(KnProvConstants.POC_PTT_ID_KEY, pocPttIdList);
                pttServerInfo.put(KnProvConstants.PR_PTT_ID_KEY, prPttIdList);
            }

            if (pttServerInfo == null) {
                knLogger.error(methodName, "ptt server info is not configured");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "ptt server info not found",
                        xdmPttServerId, KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve ptt server info", xdmPttServerId,
                    KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : ptt server info -> ", pttServerInfo);
        return pttServerInfo;
    }

    /**
     * method to retrieve the Ptt servers Info by cluster id
     *
     * @param clusterId the cluster identifier to query
     * @param persisterTxn the database transaction context
     * @return a collection of PTT server information for the specified cluster
     * @throws KnDAOException DB exception
     */
    public Map<String, List<String>> retrievePttServerIdsByClusterId(int clusterId,KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "retrievePttServerIdsByClusterId(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: retrieve Subs Partition Config");
        Map<String, List<String>> pttServerInfo = null;
        PreparedStatement stmt = null;
        Connection conn;
        ResultSet resultSet = null;
        String query = "SELECT DISTINCT PSI.PTTSERVERID AS PTTSERVERID, PSI.SIGNALINGCARDTYPE AS SIGNALINGCARDTYPE FROM DG.PTTSERVERIPINFO PSI INNER JOIN " +
                "DG.SIGNALINGCARD_SITE_MAP SSM ON PSI.SIGNALINGCARDID = SSM.SIGNALINGCARDID " +
                "WHERE SSM.CLUSTERID = ? and PSI.SIGNALINGCARDTYPE IN (32,34)";
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            stmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "QUERY: Executing ", query);
            stmt.setInt(1,clusterId);
            resultSet = stmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", resultSet);
            if (resultSet.next()) {
                List<String> pocPttIdList = new ArrayList<String>();
                List<String> prPttIdList = new ArrayList<String>();
                do {
                    int cardType = resultSet.getInt("SIGNALINGCARDTYPE");
                    String pttServerId = resultSet.getString("PTTSERVERID");
                    if (cardType == KnProvConstants.POC_CARD_TYPE ||
                            cardType == KnProvConstants.GEO_POC_CARD_TYPE) {
                        pocPttIdList.add(pttServerId);
                    } else if (cardType == KnProvConstants.PRESENCE_CARD_TYPE ||
                            cardType == KnProvConstants.GEO_PRESENCE_CARD_TYPE) {
                        prPttIdList.add(pttServerId);
                    }
                } while (resultSet.next());
                pttServerInfo = new HashMap<String, List<String>>();
                pttServerInfo.put(KnProvConstants.POC_PTT_ID_KEY, pocPttIdList);
                pttServerInfo.put(KnProvConstants.PR_PTT_ID_KEY, prPttIdList);
            }

            if (pttServerInfo == null) {
                knLogger.error(methodName, "ptt server info is not configured");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "ptt server info not found",
                        xdmPttServerId, KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve ptt server info", xdmPttServerId,
                    KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : ptt server info -> ", pttServerInfo);
        return pttServerInfo;
    }

    /**
     * Method to retrieve the Subscriber count for the respoective POC/Presence/XDMS Ptt Server Ids
     *
     * @param pttServerIds
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getSubsCount(List<String> pttServerIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsCount(List<String>";
        knLogger.debug(methodName, "ENTRY: Retrieve Subs Count");
        knLogger.debug(methodName, "get subs count for pttServerIds", pttServerIds);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        Map<String, Integer> pttServerSubsCount = null;
        int index = 1;
        StringBuilder qryBuilder = new StringBuilder(" SELECT PTTSERVERID, SUBSCRIBERCOUNT ")
                .append("FROM DG.ServerCapacityUtilInfo WHERE PTTSERVERID IN (PTTSERVERIDS)");
        
        String query = qryBuilder.toString();
        query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(pttServerIds, query, "PTTSERVERIDS");
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "QUERY: Executing ", query);
             
                for(String pttServerId: pttServerIds)
                	pStmt.setString(index++, pttServerId);
             
            resultSet = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", resultSet);
            if (resultSet != null) {
                while (resultSet.next()) {
                    String pttServerId = resultSet.getString("PTTSERVERID");
                    int subsCount = resultSet.getInt("SUBSCRIBERCOUNT");
                    if (pttServerSubsCount == null) {
                        pttServerSubsCount = new HashMap<String, Integer>();
                    }
                    pttServerSubsCount.put(pttServerId, subsCount);
                }
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve ptt server subscriber count", xdmPttServerId,
                    KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : ptt server subscriber count -> ", pttServerSubsCount);
        return pttServerSubsCount;
    }

    /**
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Boolean isPrInPocEnabled(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isPrInPoCEnabled(KnPersisterTxn)";
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        boolean prInPocEnabled = false;
        StringBuilder qryBuilder = new StringBuilder(" SELECT PARAMNAME, PARAMVALUE ")
                .append("FROM DG.RTXENVVARIABLEINFO WHERE PARAMNAME = '");
        qryBuilder.append("ENABLE_PR_IN_POC").append("'").append(" AND ");
        qryBuilder.append("PTTSERVERID = ?");
        String query = qryBuilder.toString();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            stmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "QUERY: Executing ", query);
            stmt.setString(1,xdmPttServerId);
            resultSet = stmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", resultSet);

            if (resultSet.next()) {
                String paramValue = resultSet.getString("PARAMVALUE");
                prInPocEnabled = KnProvConstants.PR_IN_POC_ENABLED.equals(paramValue);
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve ENV Variable info ", xdmPttServerId,
                    KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.info(methodName, "EXIT : PR IN POC ENABLED -> ", prInPocEnabled);
        return prInPocEnabled;
    }

    /**
     * method to get the Pre Assign Corp Home for Corporation (Ext Corp Id)
     *
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public String getPreAssignCorpHome(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPreAssignCorpHome(String, KnPersisterTxn)";
        String preAssignCorpHome = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        StringBuilder qryBuilder = new StringBuilder(" SELECT POCHOME ")
                .append("FROM DG.PREASSIGNEDCORPHOME WHERE EXTCORPID = ?");
        //qryBuilder.append(extCorpId).append("'");
        String query = qryBuilder.toString();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, extCorpId);
            knLogger.debug(methodName, "QUERY: Executing ", query);
            resultSet = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", resultSet);
            if (resultSet != null) {
                while (resultSet.next()) {
                    preAssignCorpHome = resultSet.getString("POCHOME");
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to pre assigned corp home", xdmPttServerId,
                    KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT : pre assigned corp home -> ", preAssignCorpHome);
        return preAssignCorpHome;
    }

    /**
     * Method to get Poc Subscriber Capacity Config
     *
     * @param pttServerIds poc PttServerId List
     * @param persisterTxn DB transaction
     * @return Poc Subscriber capacity config DTO
     * @throws KnDAOException DB Exception
     */
    public Map<String, KnPocSubsCapConfigDTO> getPocSubsCapacityConfig(List<String> pttServerIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getPocSubsCapacityConfig(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entery getPocSubsCapacityConfig");
        Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap = new HashMap<String, KnPocSubsCapConfigDTO>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        int index = 1;
        StringBuilder qryBuilder = new StringBuilder(" SELECT PTTSERVERID, MAXSUBSCRIBERS, ALLOWSUBSCRPROVISIONING ")
                .append("FROM DG.POCSUBSCRCAPACITYCONFIG WHERE PTTSERVERID IN (MDNLIST)");
        String query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(pttServerIds, qryBuilder.toString(),"MDNLIST");
//        for(String pttServerId : pttServerIds) {
//        	qryBuilder.append("?,");
//        }
//        qryBuilder.deleteCharAt( qryBuilder.length() -1 ).toString();
//        qryBuilder.append(")");
//        String query = qryBuilder.toString();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pStmt = conn.prepareStatement(query);
            for(String pttServerId : pttServerIds) {
            	pStmt.setString(index++, pttServerId);
            }
            knLogger.debug(methodName, "QUERY: Executing ", query);
            resultSet = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", resultSet);

            while (resultSet.next()) {
                KnPocSubsCapConfigDTO pocSubsCapConfigDTO = new KnPocSubsCapConfigDTO();
                pocSubsCapConfigDTO.setPttServerId(resultSet.getString("PTTSERVERID"));
                pocSubsCapConfigDTO.setMaxSubsLimit(resultSet.getInt("MAXSUBSCRIBERS"));
                pocSubsCapConfigDTO.setAllowProv(resultSet.getInt("ALLOWSUBSCRPROVISIONING"));
                pocSubsCapConfigMap.put(resultSet.getString("PTTSERVERID"), pocSubsCapConfigDTO);
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to getPocSubsCapacityConfig ", xdmPttServerId,
                    KnProvDAOSourceTypes.POCSUBSCR_CAPACITY_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : getPocSubsCapacityConfig -> ", pocSubsCapConfigMap);
        return pocSubsCapConfigMap;
    }

    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException exception DB Layer
     */
    public void updatePAMAccState(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        long profileCreationTime = Calendar.getInstance().getTimeInMillis();
        pamAccPersistDTO.setLastUpdateTime(profileCreationTime);
        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.updatePAMAccState(pamAccPersistDTO, persisterTxn);

    }


    /**
     * method for updating pam account last update time
     *
     * @param int          pamAccId
     * @param long         lastProfileUpdatetime
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException exception DB Layer
     */
    @Override
    public void updatePAMAccLastProfileUpdatedTime(int pamAccId, long lastProfileUpdatetime, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.updatePAMAccLastProfileUpdatedTime(pamAccId, lastProfileUpdatetime, persisterTxn);
    }


    /**
     * method to update the external corp ID And last update time
     *
     * @param
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */

    public void updateCorpName(String extCorpId, String corpName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpName(KnCorpProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
        knLogger.debug(methodName, "ENTRY: update updateCorpName ");
        try {

            //opening the transaction if null is received
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            corpProfilePersistDTO.setCorporateName(corpName);
            corpProfilePersistDTO.setCorpId(retrieveCorporationId(extCorpId, persisterTxn));
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            corpProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpInfoDAO.updateCorpName(corpProfilePersistDTO, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: updateCorpName");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to updateCorpName", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
    }


    /**
     * method to update the Subscriber Profile etag
     *
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void updateLastProfileUpdateTimeForPamAccId(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileUpdateTime(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update Last Profile Update Time ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateLastProfileUpdateTimeForPamAccId(pamAccId, lastProfileUpdateTime, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Last Profile Update Time");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update Last Profile Update Time", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException exception DB Layer
     */
    public void updatePAMAccMaxSub(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        long profileCreationTime = Calendar.getInstance().getTimeInMillis();
        pamAccPersistDTO.setLastUpdateTime(profileCreationTime);
        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.updatePAMAccMaxSub(pamAccPersistDTO, persisterTxn);

    }

    /**
     * method for update  PAM Acc name
     *
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException exception DB Layer
     */
    public void updatePAMAccName(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updatePAMAccName(KnPAMAccPersistDTO, KnPersisterTxn)";
        knLogger.entry(methodName, pamAccPersistDTO, persisterTxn);

        long profileCreationTime = Calendar.getInstance().getTimeInMillis();
        pamAccPersistDTO.setLastUpdateTime(profileCreationTime);
        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfoDAO.updatePAMAccName(pamAccPersistDTO, persisterTxn);

        knLogger.exit(methodName);
    }

    public Map<String, KnOPSubsProfileInfoDTO> retrieveNotificationDetails4mdns(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveNotificationDetails4mdns(List, KnPersisterTxn)";
        Map<String, KnOPSubsProfileInfoDTO> resultMap = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            resultMap = subscrInfoDAO.retrieveNotificationDetails4mdns(mdns, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving Subscriber Info for mdn - " + mdns, xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }

        return resultMap;
    }

    /**
     * Method to update the Subscriber count for the respective POC/Presence/XDMS Ptt Server Ids
     *
     * @param pttServerIds
     * @return
     * @throws KnDAOException
     */
    public void updateServerCapacityUtil(List<String> pttServerIds, KnProvConstants.COUNT delimiter, int count, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServerCapacityUtil(List<String>";
        knLogger.debug(methodName, "ENTRY: update Subs Count in Server Capacity Util");
        knLogger.debug(methodName, "delimiter :", delimiter, " pttServerIds :", pttServerIds);
        Connection conn;
        Statement stmt = null;
        StringBuilder qryBuilder = new StringBuilder(" UPDATE DG.SERVERCAPACITYUTILINFO set SUBSCRIBERCOUNT = SUBSCRIBERCOUNT " + delimiter.value() + " " + count + " where PTTSERVERID in (");

        int i = 0;
        for (String pttServerId : pttServerIds) {
            qryBuilder.append("'");
            qryBuilder.append(pttServerId);
            qryBuilder.append("'");
            if (i++ < (pttServerIds.size() - 1)) {
                qryBuilder.append(", ");
            }
        }
        qryBuilder.append(")");
        String query = qryBuilder.toString();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "QUERY: Executing ", query);
            int cnt = stmt.executeUpdate(query);
            knLogger.debug(methodName, "QUERY: Executed count: ", cnt);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to update subscriber count for ptt server ", xdmPttServerId,
                    KnProvDAOSourceTypes.SERVERCAPACITYUTILINFO, query);
        } finally {
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : update Server Capacity Util ");
    }

    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<String> mdns;
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdns = subscrInfoDAO.getPAMAccountProvMdns(pamAccId, persisterTxn);

        return mdns;
    }

    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<String> mdns;

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdns = subscrInfoDAO.getPamAccLastSequenceMdns(pamAccId, start, end, persisterTxn);

        return mdns;
    }

    /**
     * Retrieves a a list of subscriber profiles for Dispatcher changes
     *
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public List<KnOPSubsDispatcherDTO> retrieveBulkSubscribers(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveBulkSubscribers(List,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve Bulk Subscribers Profile For Dispatcher changes");
        List<KnOPSubsDispatcherDTO> listOfDTOs = null;
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        listOfDTOs = subscrInfoDAO.retrieveBulkSubscribersInfo(mdns, persisterTxn);
        knLogger.debug(methodName, "EXIT: Retrieving Bulk Subscribers Profile For Dispatcher changes");
        return listOfDTOs;
    }

    /**
     * updates a list of subscribers for Dispatcher changes
     *
     * @param subsProfilePersistDTOs
     * @param persisterTxn
     * @throws KnDAOException
     */
    @Override
    public void updateBulkSubscrProfile(List<KnOPSubsDispatcherDTO> subsProfilePersistDTOs, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkSubscrProfile(List,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update Bulk Subscribers Profile For Dispatcher changes");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateBulkSubscrProfile(subsProfilePersistDTOs, persisterTxn);
        knLogger.debug(methodName, "Updating Bulk Subscribers Profile For Dispatcher changes ");


    }

    /**
     * @param extSubsPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    //@Override
    public void createExtSubscriber(KnExtSubsPersistDTO extSubsPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createExtSubscriber(KnExtSubsPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Create External Subscriber Profile");

        KnExtSubscriberInfoDAO extSubscriberInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createExtSubsInfoDA0(xdmPttServerId);
        extSubscriberInfoDAO.insert(extSubsPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: Create External Subscriber Profile ");

    }

    /**
     * -->Get extSubsPersistDTO (KnExtSubsPersistDTO type) in the request
     * -->call the delete method by passing the extSubsPersistDTO
     *
     * @param extSubsPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    //@Override
    public void deleteExtSubscriber(KnExtSubsPersistDTO extSubsPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteExtSubscriber(KnExtSubsPersistDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Delete External Subscriber Profile");

        KnExtSubscriberInfoDAO extSubscriberInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createExtSubsInfoDA0(xdmPttServerId);
        extSubscriberInfoDAO.delete(extSubsPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: Delete External Subscriber profile");

    }

    /**
     * -->Get extSubsPersistDTO (KnExtSubsPersistDTO type) in the request
     * -->call the update method by passing the extSubsPersistDTO
     *
     * @param extSubsPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    ////@Override
    public void updateExtSubscriber(KnExtSubsPersistDTO extSubsPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateExtSubscriber(KnExtSubsPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update External Subscriber Profile");

        KnExtSubscriberInfoDAO extSubscriberInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createExtSubsInfoDA0(xdmPttServerId);
        extSubscriberInfoDAO.update(extSubsPersistDTO, persisterTxn);
        knLogger.debug(methodName, "Update External Subscriber Profile ");


    }

    /**
     * @param extMdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    //@Override
    public KnExtSubsPersistDTO getExtSubscriberInfo(ArrayList<String> extMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtSubscriberInfo(KnExtSubsPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Get External Subscriber Profile");
        KnExtSubsPersistDTO subsPersistDTO = null;

        KnExtSubscriberInfoDAO extSubscriberInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createExtSubsInfoDA0(xdmPttServerId);
        subsPersistDTO = extSubscriberInfoDAO.getExtSubscrInfo(extMdnList, persisterTxn);

         /*catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed to Get External Subscriber Profile", xdmPttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, null);
        }*/
        knLogger.debug(methodName, "EXIT:Get External Subscriber Profile With Response DTO", subsPersistDTO);

        return subsPersistDTO;
    }

    /**
     * -->Get the mdn for the External Subscriber for which CorpId list is to be retrieved
     * -->Execute the query for getting the corpId list by passing the mdn
     * -->Store the result in a list and sends back the listOfCorpIds as response
     * -->Even if any exception is thrown in the middle it will never send the list as null.
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    //@Override
    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdsForExtSubscriber(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Get CorpIds For External Subscriber Profile For MDN", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        List<Integer> listOfCorpIds = new ArrayList<>();
        try {

            query = QRY_SELECT_CORPID_OF_EXT_SUBS;
            knLogger.debug(methodName, "getting Connection for PTT ID - ", xdmPttServerId);
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            while (rs.next()) {
                listOfCorpIds.add(rs.getInt(1));

            }

        } catch (SQLException e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to Get CorpId list", xdmPttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT :Get CorpIds For External Subscriber Profile With CorpId List", listOfCorpIds);
        }
        return listOfCorpIds;
    }

    public void addSubApn(String mdn, int apnId, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "addSubApn(String , int ,  KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn), apnId, persistTxn);
        try {
            KnSubsAPNInfoDAO subsAPNInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAPNInfoDA0
                    (xdmPttServerId);
            subsAPNInfoDAO.addSubApn(mdn, apnId, persistTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed while creating the Subscribers Apn profile", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, null);
        }
    }

    public void updateSubApn(String mdn, int apnId, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "updateSubApn(String , int ,  KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn), apnId, persistTxn);
        try {
            KnSubsAPNInfoDAO subsAPNInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAPNInfoDA0
                    (xdmPttServerId);
            subsAPNInfoDAO.updateSubApnId(mdn, apnId, persistTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed while creating the Subscribers Apn profile", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, null);
        }
    }

    public Map<String, Integer> selectSubApn(List<String> mdnList, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "selectSubApn(String,boolean,  KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdnList), persistTxn);
        Map<String, Integer> apnProfile = null;
        try {
            KnSubsAPNInfoDAO subsAPNInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAPNInfoDA0(xdmPttServerId);
            apnProfile = subsAPNInfoDAO.selectSubApn(mdnList, readOnly, persistTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed while Select the Subscribers Apn profile", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, null);
        }
        return apnProfile;
    }


    public void deleteSubApn(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "deleteSubApn(String ,   KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn), persistTxn);
        try {
            KnSubsAPNInfoDAO subsAPNInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAPNInfoDA0
                    (xdmPttServerId);
            subsAPNInfoDAO.deleteSubApnInfo(mdn, persistTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed while creating the Subscribers Apn profile", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, null);
        }
    }


    public Integer getAPNId(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAPNId(String mdn, KnPersisterTxn)";
        knLogger.info(methodName, "Not Implemented");
        return null;
    }

    public void deleteSubApn(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "deleteSubApn(List<String> ,   KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdnList), persistTxn);
        try {
            KnSubsAPNInfoDAO subsAPNInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAPNInfoDA0
                    (xdmPttServerId);
            subsAPNInfoDAO.deleteSubApnInfo(mdnList, persistTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed while creating the Subscribers Apn profile", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, null);
        }
    }

    public void addSubApn(List<String> mdns, int apnId, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "addSubApn(List<String>  , int ,  KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdns), apnId, persistTxn);
        try {
            KnSubsAPNInfoDAO subsAPNInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAPNInfoDA0
                    (xdmPttServerId);
            subsAPNInfoDAO.addSubApn(mdns, apnId, persistTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed while creating the Subscribers Apn profile", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, null);
        }
    }

    @Override
    public Map<Integer, List<Integer>> retrieveRoamingTypeClusterIds(KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "retrieveRoamingTypeClusterIds(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : RoamingType,ClusterId map -> ");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, List<Integer>> roamingTypeClusterIdMap = new HashMap<>();
        try {
            query = QRY_SELECT_CLUSTERIDS_OF_ROAMINGTYPES;
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            while (rs.next()) {
                if (roamingTypeClusterIdMap.containsKey(rs.getInt("ROAMINGTYPE"))) {
                    ArrayList<Integer> al1 = (ArrayList<Integer>) roamingTypeClusterIdMap.get(rs.getInt("ROAMINGTYPE"));
                    al1.add(rs.getInt("CLUSTERID"));
                    roamingTypeClusterIdMap.put(rs.getInt("ROAMINGTYPE"), al1);
                } else {
                    ArrayList<Integer> al2 = new ArrayList<>();
                    al2.add(rs.getInt("CLUSTERID"));
                    roamingTypeClusterIdMap.put(rs.getInt("ROAMINGTYPE"), al2);
                }
            }
            knLogger.debug(methodName, "EXIT : RoamingType,ClusterId map -> ", roamingTypeClusterIdMap);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve RoamingType,ClusterId map", xdmPttServerId, KnProvDAOSourceTypes.ROAMINGMCCMNCINFO, query);
        }
        return roamingTypeClusterIdMap;
    }

    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "retrievePAMAccIdForCorpId(int)";
        knLogger.debug(methodName, "ENTRY : corpId", corpId);

        KnPAMSubscrProfInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsProfInfoDAO(xdmPttServerId);
        return pamAccInfoDAO.retrievePAMAccIdForCorpId(corpId, persisterTxn);

    }

    @Override
    public void updateBulkSubscrCorpId(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateBulkSubscrCorpId(KnBulkSubsProfilePersistDTO)";
        knLogger.info(methodName, "ENTRY : subsProfilePersistDTO-", subsProfilePersistDTO);
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        pocSubscrInfoDAO.updateBulkSubscrCorpId(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT : ");

    }

    @Override
    public void updatePAMSubsProfCorpId(KnPAMAccPersistDTO pamAccPersistDTO, int oldCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updatePAMSubsProfCorpId(KnPAMAccPersistDTO)";
        knLogger.debug(methodName, "ENTRY : ", pamAccPersistDTO);
        KnPAMSubscrProfInfoDAO pamSubsProfInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsProfInfoDAO(xdmPttServerId);
        pamSubsProfInfoDAO.updatePAMSubsProfCorpId(pamAccPersistDTO, oldCorpId, persisterTxn);
        knLogger.debug(methodName, "EXIT : ");
    }

    @Override
    public void updateBulkSubsLastProfileUpdateTime(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateBulkSubsLastProfileUpdateTime(KnBulkSubsProfilePersistDTO)";
        knLogger.info(methodName, "ENTRY : subsProfilePersistDTO-", subsProfilePersistDTO);
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        pocSubscrInfoDAO.updateLastProfileUpdateTime(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT : ");
    }

    public int getSubsCountOfClientTypeForCorp(int corpId, List<Integer> clientTypeList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsCountOfClientTypeForCorp(int,List<Integer>)";
        knLogger.debug(methodName, "ENTRY: Retrieve Subs Count for corpid", corpId);
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int subsCount = 0;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Querying based on corpId");
            query = QRY_SUBSCOUNT_FOR_CORPID_CLIENT.replaceAll("CLIENT_TYPE_LIST", KnDbUtil.convertListToIntBuffer(clientTypeList).toString());
            knLogger.debug(methodName, "query- ", query);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query Executed");
            if (rs.next()) {
                subsCount = rs.getInt(1);
            }
            KnDbUtil.closeResultSet(rs);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to Subscriber Count", xdmPttServerId,
                    KnProvDAOSourceTypes.PTTSERVER_IP_INFO, query);
        }
        knLogger.debug(methodName, "EXIT : Subscriber Count ", subsCount);
        return subsCount;
    }

    public void updateLinkedGwKeyOfCorp(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "updateLinkedGwKeyOfCorp(KnCorpProfilePersistDTO)";
        knLogger.debug(methodName, "ENTRY: update Corporate Profile ", corpProfilePersistDTO);
        KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
        corpInfoDAO.updateLinkedGwKeyOfCorp(corpProfilePersistDTO, persistTxn);
    }

    public void updateEtag4corpNNIRefId(KnCorpGwLinkedAccInfoDTO corpGwLinkedAccInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateEtag4corpNNIRefId(String,long)";
        knLogger.debug(methodName, "ENTRY: ", corpGwLinkedAccInfoDTO);
        KnCorpGwLinkedAccInfoDAO corpGwLinkedAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpGwLinkedAccInfoDAO(xdmPttServerId);
        corpGwLinkedAccInfoDAO.updateEtagForCorpNNIRefId(corpGwLinkedAccInfoDTO, persisterTxn);

    }

    public void createNNISubscrProfile(KnBulkNNISubsProfilePersistDTO nniSubsProfilePersistDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "createNNISubscrProfile(KnBulkNNISubsProfilePersistDTO)";
        knLogger.debug(methodName, "ENTRY : ", nniSubsProfilePersistDTO);
        KnPOCNNISubscrInfoDAO pocnniSubscrInfoDA0 = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCNNISubscrInfoDA0(xdmPttServerId);
        pocnniSubscrInfoDA0.insert(nniSubsProfilePersistDTO, persistTxn);
    }

    public int getProfileIdForNNISubscriber(int profileType, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "getProfileIdForNNISubscriber(int)";
        knLogger.debug(methodName, "ENTRY : profileType-", profileType);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int profileId = 0;
        try {
            query = QRY_SELECT_PROFILEID_FOR_PROFTYPE;
            conn = persistTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, profileType);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            if (rs.next()) {
                profileId = rs.getInt(1);
            } else {
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found");
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve profileId", xdmPttServerId, KnProvDAOSourceTypes.GENERICNNIPROFILE, query);
        }
        knLogger.debug(methodName, "EXIT : profileId-", profileId);
        return profileId;

    }

    public void deleteNNISubscrProfile(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "deleteNNISubscrProfile(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnPOCNNISubscrInfoDAO pocnniSubscrInfoDA0 = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCNNISubscrInfoDA0(xdmPttServerId);
        pocnniSubscrInfoDA0.delete(mdns, persistTxn);
    }

    public boolean isMdnPresentInPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "isMdnPresentInPamAccInfo(String)";
        knLogger.debug(methodName, "ENTRY : mdn-", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean response = false;
        try {
            query = QRY_SELECT_PAMACCID_FROM_PAMACCINFO;

            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing the Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            if (rs.next()) {
                response = true;
                knLogger.debug(methodName, "Mdn is  present in pamaccinfo");
            }
            KnDbUtil.closeResultSet(rs);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve billing mdn from pamaccinfo", xdmPttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        }
        knLogger.debug(methodName, "EXIT : response-", response);
        return response;

    }

    /**
     * method for retrieve Subscriber
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public boolean isMdnExistInPocSubsInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        boolean isExist = subscrInfoDAO.isMDNExist(mdn, persisterTxn);

        return isExist;
    }


	@Override
	public KnPAMAccPoolUsageDTO getFirstUnusedMdnFromPAMAccPoolUsage(String billingMDN, KnPersisterTxn persisterTxn) throws KnDAOException {
		KnPAMAccPoolUsageDAO pamAccPoolUsageDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccPoolUsageDAO(xdmPttServerId);
		return pamAccPoolUsageDAO.getFirstUnusedMdnFromPAMAccPoolUsage(billingMDN, persisterTxn);
	}

	@Override
	public void createTPUserMDNMap(KnTPUserPersistDTO tpUserPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
		int tpUserId = genInfoUtil.retrieveIdForTable(KnProvConstants.TABLE_TP_USER_MDN_MAP, xdmPttServerId,
                KnProvConstants.TABLE_TP_USER_MDN_MAP_COLUMN, false, KnConstants.DUAL_DATA_STORE);
		long accCreationTime = Calendar.getInstance().getTimeInMillis();

		tpUserPersistDTO.setTpUserId(tpUserId);
		tpUserPersistDTO.setCreationTime(accCreationTime);

		KnTPUserMDNMapDAO tpUserMDNMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTPUserMDNMapDAO(xdmPttServerId);
		tpUserMDNMapDAO.insert(tpUserPersistDTO, persisterTxn);
	}

	@Override
	public KnTPUserAccountDTO retrieveTPUserAccountForMDN(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
		KnTPUserMDNMapDAO tpUserMDNMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTPUserMDNMapDAO(xdmPttServerId);
		return tpUserMDNMapDAO.retrieveTPUserAccountForMDN(mdn, persistTxn);
	}

	@Override
	public int retrievePAMAccPoolUsageForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		KnPAMAccPoolUsageDAO pamAccPoolUsageDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccPoolUsageDAO(xdmPttServerId);
		return pamAccPoolUsageDAO.retrievePAMAccPoolUsageForMDN(mdn, persisterTxn);
	}

	@Override
	public void updateTPUserMDNMapByMDN(KnTPUserPersistDTO tpUserPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
		KnTPUserMDNMapDAO tpUserMDNMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTPUserMDNMapDAO(xdmPttServerId);
		tpUserMDNMapDAO.updateTPUserMDNMapByMDN(tpUserPersistDTO, persisterTxn);

	}

	@Override
	public void deleteTPUserMDNMap(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		KnTPUserMDNMapDAO tpUserMDNMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTPUserMDNMapDAO(xdmPttServerId);
		tpUserMDNMapDAO.deleteTPUserMDNMap(persistenceDTO, persistTxn);
	}

	@Override
	public void createPAMAccPoolUsage(KnPAMAccPoolPersistDTO pamAccPoolPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
		KnPAMAccPoolUsageDAO pamAccPoolUsageDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccPoolUsageDAO(xdmPttServerId);
		pamAccPoolUsageDAO.createPAMAccPoolUsage(pamAccPoolPersistDTO, persisterTxn);
	}

	@Override
	public void deletePAMAccPoolUsage(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
		KnPAMAccPoolUsageDAO pamAccPoolUsageDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccPoolUsageDAO(xdmPttServerId);
		pamAccPoolUsageDAO.deletePAMAccPoolUsage(mdns, persisterTxn);

	}

	@Override
	public void updateSubsContactListID(int contactListID, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
		pocSubscrInfoDAO.updateSubsContactListID(contactListID, mdn, persisterTxn);
	}

    @Override
    public void createClientSuppVocoder(List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs, KnPersisterTxn persisterTxn)throws KnDAOException{
        KnClientSuppVocodersDAO clientSuppVocodersDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().clientSuppVocoderDAO(xdmPttServerId);
        clientSuppVocodersDAO.insert(clientVocoderProfilePersistDTOs,persisterTxn);
    }

    @Override
    public void deleteClientSuppVocoder(String mdn, KnPersisterTxn persisterTxn)throws KnDAOException {
        KnClientSuppVocodersDAO clientSuppVocodersDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().clientSuppVocoderDAO(xdmPttServerId);
        clientSuppVocodersDAO.delete(mdn,persisterTxn);
    }
    @Override
    public void deleteClientSuppVocoder(List<String> mdnList, KnPersisterTxn persisterTxn)throws KnDAOException {
        KnClientSuppVocodersDAO clientSuppVocodersDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().clientSuppVocoderDAO(xdmPttServerId);
        clientSuppVocodersDAO.delete(mdnList,persisterTxn);
    }
    @Override
    public void deleteSubsAddlInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscrInfoDAO.deleteSubsAddlInfo(mdn, persisterTxn);
    }

    @Override
    public Map<Integer, Integer> retrieveClientSuppVocoder(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnClientSuppVocodersDAO clientSuppVocodersDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().clientSuppVocoderDAO(xdmPttServerId);
        Map<Integer, Integer> clientVocoderProfile = clientSuppVocodersDAO.fetchClientSuppVocoder(mdn, readOnly, persisterTxn);
        return clientVocoderProfile;
    }

    @Override
    public void updateSubscVocoderId(String mdn, Integer vocoderId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateVocoderID(mdn, vocoderId, readOnly, persisterTxn);
    }

    @Override
    public KnOPPAMAccInfoDTO retrieveLicensePackInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnOPPAMAccInfoDTO pamAccInfo = null;

        KnPAMAccInfoDAO pamAccInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMAccountInfoDAO(xdmPttServerId);
        pamAccInfo = pamAccInfoDAO.retrieveLicensePackInfo(extPAMAccId, persisterTxn);

        return pamAccInfo;
    }

    public KnOPSubsProfileInfoDTO selectSubsProfileInfo(List<String> mdnList, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubsProfileInfo(String,boolean, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        try {
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subsProfileDTO = subscrInfoDAO.selectSubsProfileInfo(mdnList, readOnly, persistTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, KnConstants.DAO_EXCEPTION_OCCURED, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, KnConstants.UNEXPECTED_EXCEPTION_OCCURED, e);
            throw KnDbUtil.processException(e, "Failed while retrieving Subscriber Info for mdn - ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);
        return subsProfileDTO;
    }

    public Collection<KnSubsProfileDTO> fetchSubsSpecificDetailsForBulkMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        return fetchSubsSpecificDetailsForBulkMdns(mdnList, false, persisterTxn);
    }

    @Override
    public Collection<KnSubsProfileDTO> fetchSubsSpecificDetailsForBulkMdns(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);

        return subscrInfoDAO.fetchSubsSpecificDetailsForBulkMdns(mdnList, readOnly, persisterTxn);
    }

    /**
     * method to update ConvergedClient Profile
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    @Override
    public void updateConvergedClientProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateConvergedClientProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", subsProfilePersistDTO);
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateConvergedClientProfile(subsProfilePersistDTO, persisterTxn);
            knLogger.debug(methodName, "EXIT: update Subscriber Profile ");
    }

	@Override
	public KnSubsProfilePersistDTO updateSubscriberUserID(KnSubsProfilePersistDTO subsProfilePersistDTO,
			KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "updateSubscrUserID(KnSubsProfilePersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:  update Subscriber userID with DTO - ", subsProfilePersistDTO);
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateSubscriberUserID(subsProfilePersistDTO,persisterTxn);
            knLogger.debug(methodName, "EXIT: update Subscriber userID ");
		return null;
	}



    @Override
    public void deleteMCPTTProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().deleteMCPTTInfoDAO(xdmPttServerId);
        mcpttPermInfoDAO.deleteMdn(mdn, persisterTxn);

    }

    public void deleteTargetEntry(String authMdn,String targetMdn, KnPersisterTxn persistTxn) throws KnDAOException{
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().deleteMCPTTInfoDAO(xdmPttServerId);
        mcpttPermInfoDAO.deleteTargetEntry(authMdn,targetMdn, persistTxn);

    }

    @Override
    public void deleteAuthorizationDocProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnAuthorizationDocDAO authorizationDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().deleteAuthorizationDocDAO(xdmPttServerId);
        authorizationDocDAO.deleteMdn(mdn, persisterTxn);
    }

    @Override
    public List<String> fetchMCPTTAuthorizedMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().fetchMCPTTAuthorizedMdn(xdmPttServerId);
        return  mcpttPermInfoDAO.fetchAuthorizedMdn(mdn, persisterTxn);
    }

    @Override
    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().fetchMCPTTAuthorizedMdn(xdmPttServerId);
        return  mcpttPermInfoDAO.getMCPTTPermInfo(mdn, persisterTxn);
    }

    @Override
    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfoForTargetMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().fetchMCPTTAuthorizedMdn(xdmPttServerId);
        return  mcpttPermInfoDAO.getMCPTTPermInfoForTargetMdn(mdn, persisterTxn);
    }

    @Override
    public void createNewMdnInAuthrizationDoc(String mdn, Long etag, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnAuthorizationDocDAO authorizationDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().updateAuthorizedDocEtag(xdmPttServerId);
        authorizationDocDAO.insert(mdn,etag,persisterTxn);
    }

    @Override
    public void updateAuthorizationDocEtag(String mdn, Long etag, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnAuthorizationDocDAO authorizationDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().updateAuthorizedDocEtag(xdmPttServerId);
        authorizationDocDAO.updateEtag(mdn,etag,persisterTxn);
    }

    @Override
    public void updateMCPTTTargetMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().updateMCPTTTargetMdn(xdmPttServerId);
        mcpttPermInfoDAO.updateTargetMdn(oldMdn, newMdn, persisterTxn);
    }

    @Override
    public void updateMCPTTAuthorizedMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().updateMCPTTAuthorizedMdn(xdmPttServerId);
        mcpttPermInfoDAO.updateAuthorizedMdn(oldMdn,newMdn, persisterTxn);
    }

    @Override
    public void disableDiscreetEnabled(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().updateMCPTTTargetMdn(xdmPttServerId);
        mcpttPermInfoDAO.disableDiscreetEnabled(mdn,persisterTxn);
    }

    @Override
    public void updatePermBit(String mdn, long permBit, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnMCPTTPermInfoDAO mcpttPermInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().updateMCPTTTargetMdn(xdmPttServerId);
        mcpttPermInfoDAO.updatePermBit(mdn,permBit,persisterTxn);
    }

	@Override
	public KnOPSubsProfileInfoDTO retrieveSubscriberInfoForUFMI(String ufmi, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "retrieveSubscriberInfoForUFMI(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileDTO = subscrInfoDAO.selectSubscriberProfileForUFMI(ufmi, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);

        return subsProfileDTO;
	}

	@Override
	public void createSubAddOnPkgs(String mdn, List<String> addOnPkgs, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		 String methodName = "createSubAddOnPkgs(String mdn, List<String> addOnPkgs, KnPersisterTxn persisterTxn)";
	     knLogger.info(methodName, "ENTRY: Create Subscriber addon package ", "mdn -",KnGDPRTemplate.mdn(mdn),"addOnPkgs",addOnPkgs);
	     KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
	     subsAddOnPkgInfoDAO.createSubAddOnPkgs(mdn, addOnPkgs, persisterTxn);
	     knLogger.info(methodName, "EXIT: Create Subscriber addon package ");

	}

    @Override
    public void createSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "createSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: Create Subscriber addlnfo package ", "KnSubsAddlInfoPersistDTO -", subsProfilePersistDTO);
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.createSubscrPkgAddlInfo(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT: Create Subscriber addlnfo package ");
    }

	@Override
	public void updateSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: updateSubscrPkgAddlInfo ", "KnSubsAddlInfoPersistDTO -",subsProfilePersistDTO);
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateSubscrPkgAddlInfo(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT: update Subscriber addlnfo package ");

	}

    @Override
    public void updateSubscrPkgAddlInfoforProfileMdn(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateSubscrPkgAddlInfoForProfileMdn(subsProfilePersistDTO, persisterTxn);
    }

    @Override
    public void updateSubscrOnBoardingMail(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateSubscrOnBoardingMail(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: updateSubscrPkgAddlInfo ", "KnSubsAddlInfoPersistDTO -",subsProfilePersistDTO);
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateSubscrOnBoardingMail(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT: update Subscriber addlnfo package ");

    }

	@Override
	public void deleteSubAddlOnPkgs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		 String methodName = "deleteSubAddlOnPkgs(String mdn, KnPersisterTxn persisterTxn)";
	     knLogger.info(methodName, "ENTRY: Delete Subscriber addon package ", "mdn -",KnGDPRTemplate.mdn(mdn));
	     KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
	     subsAddOnPkgInfoDAO.deleteSubAddlOnPkgs(mdn, persisterTxn);
	     knLogger.info(methodName, "EXIT: delete Subscriber addlnfo package ");
	}

    @Override
    public void deleteSubAddlOnPkgs(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubAddlOnPkgs(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: Delete Subscriber addon package ", "mdn -",KnGDPRTemplate.mdnList(mdnList));
        KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
        subsAddOnPkgInfoDAO.deleteSubAddlOnPkgs(mdnList, persisterTxn);
        knLogger.info(methodName, "EXIT: delete Subscriber addlnfo package ");
    }

	@Override
	public List<String> selectSubAddOnPkgs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		 String methodName = "selectSubAddOnPkgs(String mdn, KnPersisterTxn persistTxn)";
	     knLogger.info(methodName, "ENTRY: Select Subscriber addon package ", "mdn -",KnGDPRTemplate.mdn(mdn));
	     List<String> addonPkgCodes=null;
         KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
         addonPkgCodes= subsAddOnPkgInfoDAO.selectSubAddOnPkgs(mdn, persisterTxn);
         knLogger.info(methodName, "EXIT: Select Subscriber addon package ",addonPkgCodes);
         return addonPkgCodes;
	}

    @Override
    public Map<String, KnSubsAddlInfoDTO> retrieveSubscrAddlInfo(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "retrieveSubscrAddlInfo(List<String> mdns,boolean KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: retrieve Subscribers additional profile ", "mdnList -", KnGDPRTemplate.mdnList(mdns));
        Map<String, KnSubsAddlInfoDTO> subsAddlInfoProfileDTOMap = null;
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subsAddlInfoProfileDTOMap = subscrInfoDAO.selectSubscriberAddlProfile(mdns, readOnly, persisterTxn);
        knLogger.info(methodName, "EXIT: retrieve Subscribers additional profile ");
        return subsAddlInfoProfileDTOMap;
    }


	@Override
	public List<String> selectPAMSubAddOnPkgs(int pamAccId, KnPersisterTxn persistTxn) throws KnDAOException {
		String methodName = "selectPAMSubAddOnPkgs(int pamAccId, KnPersisterTxn persistTxn)";
        knLogger.info(methodName, "ENTRY: Select Subscribers addon package list", "pamAccId -",pamAccId);
        List<String> subsAddonPkgCodeList=null;
		 KnPAMSubsAddOnPkgInfoDAO pamSubsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsAddOnPkgInfoDAO(xdmPttServerId);
		 subsAddonPkgCodeList = pamSubsAddOnPkgInfoDAO.selectPAMSubAddOnPkgs(pamAccId, persistTxn);
		 knLogger.info(methodName, "EXIT: Select Subscriber addon package ",subsAddonPkgCodeList);
	     return subsAddonPkgCodeList;
	}

	@Override
	public void createPAMSubAddOnPkgs(int pamAccId, List<String> addOnPkgs, KnPersisterTxn persistTxn)
			throws KnDAOException {
		String methodName = "createPAMSubAddOnPkgs(int pamAccId, List<String> addOnPkgs, KnPersisterTxn persistTxn)";
        knLogger.info(methodName, "ENTRY: create Subscribers addon package list", " pamAccId - ",pamAccId," addonPkgs ",addOnPkgs);
		 KnPAMSubsAddOnPkgInfoDAO pamSubsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsAddOnPkgInfoDAO(xdmPttServerId);
		 pamSubsAddOnPkgInfoDAO.createPAMSubAddOnPkgs(pamAccId,addOnPkgs, persistTxn);
		 knLogger.info(methodName, "EXIT: create Subscribers addon package list ");

	}

	@Override
	public void deletePAMSubAddlOnPkgs(int pamAccId, KnPersisterTxn persistTxn) throws KnDAOException {
		String methodName = "deletePAMSubAddlOnPkgs(int pamAccId, KnPersisterTxn persistTxn)";
        knLogger.info(methodName, "ENTRY: delete Subscribers addon package list", " pamAccId - ",pamAccId);
		 KnPAMSubsAddOnPkgInfoDAO pamSubsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPAMSubsAddOnPkgInfoDAO(xdmPttServerId);
		 pamSubsAddOnPkgInfoDAO.deletePAMSubAddlOnPkgs(pamAccId, persistTxn);
		 knLogger.info(methodName, "EXIT: delete Subscribers addon package list ");
	}

	@Override
	public Map<String, List<String>> selectSubAddOnPkgs(List<String> mdns, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "selectSubAddOnPkgs(List<String> mdns, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: Select Subscribers addon package list", "mdns -",KnGDPRTemplate.mdnList(mdns));
        Map<String,List<String>> subsAddonPkgCodeMap=null;
        KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
        subsAddonPkgCodeMap= subsAddOnPkgInfoDAO.selectSubAddOnPkgs(mdns, persisterTxn);
        knLogger.info(methodName, "EXIT: Select Subscriber addon package ",subsAddonPkgCodeMap);
        return subsAddonPkgCodeMap;
	}

	@Override
	public void createBulkSubAddOnPkgs(List<String> mdns, List<String> addOnPkgs, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "createBulkSubAddOnPkgs(List<String> mdn, List<String> addOnPkgs, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: create Subscribers addon pkgs  ", " mdns - ",mdns, " addOnPkgs - ",addOnPkgs);
        KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
        subsAddOnPkgInfoDAO.createBulkSubAddOnPkgs(mdns,addOnPkgs, persisterTxn);
        knLogger.info(methodName, "EXIT: create Subscribers additional profile ");
	}

	@Override
	public void deleteBulkSubAddlOnPkgs(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
		String methodName = "deleteBulkSubAddlOnPkgs(List<String> mdns, KnPersisterTxn persistTxn)";
        knLogger.info(methodName, "ENTRY: delete Subscribers addon pkgs  ", " mdns - ",KnGDPRTemplate.mdnList(mdns));
        KnSubsAddOnPkgInfoDAO subsAddOnPkgInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAddOnPkgInfoDAO(xdmPttServerId);
        subsAddOnPkgInfoDAO.deleteBulkSubAddlOnPkgs(mdns, persistTxn);
        knLogger.info(methodName, "EXIT: delete Subscribers addon pkgs ");
	}

	@Override
	public void createBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "createBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: create Subscribers additional profile ", "subsProfilePersistDTO - ",subsProfilePersistDTO);
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.createBulkSubscrPkgAddlInfo(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT: create Subscribers additional profile ");

	}

	@Override
	public void updateBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: update Subscribers additional profile ", "subsProfilePersistDTO -",subsProfilePersistDTO);
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateBulkSubscrPkgAddlInfo(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT: update Subscribers additional profile ");

	}

	@Override
	public KnOPSubsProfileInfoDTO selectSubscriberProfileByUserId(String userId, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "selectSubscriberProfileByUserId(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileDTO = subscrInfoDAO.selectSubscriberProfileByUserId(userId, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);
        return subsProfileDTO;
	}

	@Override
	public KnOPSubsProfileInfoDTO selectSubscriberProfileByAliasMdn(String aliasMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "selectSubscriberProfileByAliasMdn(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileDTO = subscrInfoDAO.selectSubscriberProfileByAliasMdn(aliasMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);
        return subsProfileDTO;
	}

    @Override
    public KnOPSubsProfileInfoDTO selectSubscriberProfileByMcpttId(String mcpttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileByMcpttId(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileDTO = subscrInfoDAO.selectSubscriberProfileByMcpttId(mcpttId, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);
        return subsProfileDTO;
    }

    public Map<String, Map<Integer, String>> selectProfileIdMDNsByMcpttIds(List<String> mcpttIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectProfileIdMDNsByMcpttIds(String,boolean, KnPersisterTxn)";
        Map<String, Map<Integer, String>> profileIdMdns = new HashMap<>();
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        profileIdMdns = subscrInfoDAO.selectProfileIdMDNsByMcpttId(mcpttIds, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber profileIds and Mdn - ", KnGDPRTemplate.mcpttIdAndProfileMdnMap(profileIdMdns));
        return profileIdMdns;
    }

	@Override
	public KnTPVendorDetailsDTO retrieveTPVendorDetails(String vendorID, KnPersisterTxn persisterTxn)throws KnDAOException {
		KnTPVendorDetailsDTO vendorDetailsDTO = null;
		KnTPAccountDAO tpAccountDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createKnTPAccountDAO(xdmPttServerId);
		vendorDetailsDTO = tpAccountDAO.retrieveVendorDetails(vendorID, persisterTxn);
		return vendorDetailsDTO;
	}

	@Override
	public KnTPUserAccountDTO getTPUserDetails(String userName, KnPersisterTxn persistTxn) throws KnDAOException {
		KnTPUserAccountDTO userAccountDTO = null;
		KnTPUserMDNMapDAO tpUserMDNMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTPUserMDNMapDAO(xdmPttServerId);
		userAccountDTO = tpUserMDNMapDAO.getTPUserDetails(userName, persistTxn);
		return userAccountDTO;
	}

    @Override
    public long getTGSSDocEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTGSSDocEtag(String, KnPersisterTxn)";
        KnSimulSessionDocDAO ssessionDocDAO = null;
        knLogger.debug(methodName, "ENTRY: getTGSSDocEtag");
        ssessionDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTGSSDocDAO(xdmPttServerId);
        long etag = ssessionDocDAO.getSSDocEtag(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT: getTGSSDocEtag",etag);

        return etag;
    }

    @Override
    public void createTGSSDoc(String mdn, long etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createTGSSDoc(String,long, KnPersisterTxn)";
        KnSimulSessionDocDAO ssessionDocDAO = null;
        knLogger.debug(methodName, "ENTRY: createTGSSDoc");
        ssessionDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTGSSDocDAO(xdmPttServerId);
        ssessionDocDAO.insertMdn(mdn, etag, persisterTxn);
        knLogger.debug(methodName, "EXIT: createTGSSDoc");
    }

    @Override
    public void deleteTGSSDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteTGSSDoc(String, KnPersisterTxn)";
        KnSimulSessionDocDAO ssessionDocDAO = null;
        knLogger.debug(methodName, "ENTRY: deleteTGSSDoc");
        ssessionDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTGSSDocDAO(xdmPttServerId);
        ssessionDocDAO.deleteMdn(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT: deleteTGSSDoc");

    }

    @Override
    public void deleteTGSSDoc(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteTGSSDoc(String, KnPersisterTxn)";
        KnSimulSessionDocDAO ssessionDocDAO = null;
        knLogger.debug(methodName, "ENTRY: deleteTGSSDoc");
        ssessionDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createTGSSDocDAO(xdmPttServerId);
        ssessionDocDAO.deleteMdn(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT: deleteTGSSDoc");

    }

    @Override
    public List<Integer> selectSSChannelGrpInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSSChannelGrpInfo(String, KnPersisterTxn)";
        List<Integer> groupIds = null;
        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO = null;
        knLogger.debug(methodName, "ENTRY: selectSSChannelGrpInfo");
        ssChannelGroupInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSSChannelGroupInfoDAO(xdmPttServerId);
        groupIds = ssChannelGroupInfoDAO.getSSGroupIds(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT: selectSSChannelGrpInfo");
        return groupIds;
    }

    @Override
    public void createSSChannelGrpInfo(String mdn, List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSSChannelGrpInfo(String,List<Integer>,KnPersisterTxn)";
        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO =  null;
        knLogger.debug(methodName, "ENTRY: createSSChannelGrpInfo");
        ssChannelGroupInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSSChannelGroupInfoDAO(xdmPttServerId);
        ssChannelGroupInfoDAO.createMdnGroupIds(mdn, groupIds, persisterTxn);
        knLogger.debug(methodName, "EXIT: createSSChannelGrpInfo");

    }

    @Override
    public void deleteSSChannelGrpInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
      String methodName = "deleteSSChannelGrpInfo(String,KnPersisterTxn)";
        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO =  null;
        knLogger.debug(methodName, "ENTRY: deleteSSChannelGrpInfo");
        ssChannelGroupInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSSChannelGroupInfoDAO(xdmPttServerId);
        ssChannelGroupInfoDAO.deleteMdn(mdn,persisterTxn );
        knLogger.debug(methodName, "EXIT: deleteSSChannelGrpInfo");

    }

    @Override
    public void deleteSSChannelGrpInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSSChannelGrpInfo(String,KnPersisterTxn)";
        KnSSChannelGroupInfoDAO ssChannelGroupInfoDAO =  null;
        knLogger.debug(methodName, "ENTRY: deleteSSChannelGrpInfo");
        ssChannelGroupInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSSChannelGroupInfoDAO(xdmPttServerId);
        ssChannelGroupInfoDAO.deleteMdn(mdnList,persisterTxn );
        knLogger.debug(methodName, "EXIT: deleteSSChannelGrpInfo");

    }

	@Override
	public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO,
			KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "searchCorpAddressBook(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: searchCorpAddressBook");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileDTO = subscrInfoDAO.searchCorpAddressBook(subscriberDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT: searchCorpAddressBook - ", subsProfileDTO);
        return subsProfileDTO;
	}

	@Override
	public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCSIds(List<String> mcsIds,
			KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "selectSubscriberProfileByMCSIds(List, KnPersisterTxn)";
		List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = null;
        knLogger.debug(methodName, "ENTRY: selectSubscriberProfileByMCSIds");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileInfoDTOList = subscrInfoDAO.selectSubscriberProfileByMCSIds(mcsIds, persisterTxn);
        knLogger.debug(methodName, "EXIT: selectSubscriberProfileByMCSIds - ", subsProfileInfoDTOList);
        return subsProfileInfoDTOList;
	}

	@Override
	public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMDNorAliasMdn(List<String> mdns,
			KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "selectSubscriberProfileByMDNorAliasMdn(List, KnPersisterTxn)";
		List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = null;
        knLogger.debug(methodName, "ENTRY: selectSubscriberProfileByMDNorAliasMdn");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileInfoDTOList = subscrInfoDAO.selectSubscriberProfileByMDNorAliasMdn(mdns, persisterTxn);
        knLogger.debug(methodName, "EXIT: selectSubscriberProfileByMDNorAliasMdn - ", subsProfileInfoDTOList);
        return subsProfileInfoDTOList;
	}

    @Override
    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByAliasMdn(List<String> mdns,
                                                                               KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileByAliasMdn(List, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = null;
        knLogger.debug(methodName, "ENTRY: selectSubscriberProfileByAliasMdn");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileInfoDTOList = subscrInfoDAO.selectSubscriberProfileByAliasMdn(mdns, persisterTxn);
        knLogger.debug(methodName, "EXIT: selectSubscriberProfileByAliasMdn - ", subsProfileInfoDTOList);
        return subsProfileInfoDTOList;
    }

    @Override
    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCDataIds(List<String> mcDataIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileByMCDataIds(List, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = null;
        knLogger.debug(methodName, "ENTRY: selectSubscriberProfileByMCDataIds");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileInfoDTOList = subscrInfoDAO.selectSubscriberProfileByMCDataIds(mcDataIds, persisterTxn);
        knLogger.debug(methodName, "EXIT: selectSubscriberProfileByMCDataIds - ", subsProfileInfoDTOList);
        return subsProfileInfoDTOList;
    }

    @Override
    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByUserIds(List<String> userIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileByUserIds(List, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = null;
        knLogger.debug(methodName, "ENTRY: selectSubscriberProfileByUserIds");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileInfoDTOList = subscrInfoDAO.selectSubscriberProfileByUserIds(userIds, persisterTxn);
        knLogger.debug(methodName, "EXIT: selectSubscriberProfileByMCDataIds - ", subsProfileInfoDTOList);
        return subsProfileInfoDTOList;
    }

		    @Override
    public void updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO,
                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updatePrivacyOptStatus(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: privacyOptStatus");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        knLogger.debug( methodName, " subscriberDTO::"+subscriberDTO);
        subscrInfoDAO.updatePrivacyOptStatus(subscriberDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT: privacyOptStatus - ", subsProfileDTO);
        //return subsProfileDTO;
    }

	@Override
	public void updateMCSIds(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateMCSIds(KnSubsProfilePersistDTO, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY: updateMCSIds");
		KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry()
				.createPOCSubscrInfoDA0(xdmPttServerId);
		subscrInfoDAO.updateMCSIds(subsProfilePersistDTO, persisterTxn);
		knLogger.debug(methodName, "EXIT: updateMCSIds - ");

	}

    @Override
    public String getMdnForUnassign(String baseMdn,String userProfileId,
                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnForUnassign(String,String, KnPersisterTxn)";
        String mdn = null;
        knLogger.debug(methodName, "ENTRY: getMdnForUnassign");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdn = subscrInfoDAO.getMdnForUnassign(baseMdn,userProfileId, persisterTxn);
        knLogger.debug(methodName, "EXIT: getMdnForUnassign - ", KnGDPRTemplate.mdn(mdn));
        return mdn;
    }

    public Map<String, KnOPSubsProfileInfoDTO> getProfileMdnNupmfsByBaseMdn(String baseMdn,
                                                                            KnPersisterTxn persisterTxn) throws KnDAOException {
        return getProfileMdnNupmfsByBaseMdn(baseMdn, false, persisterTxn);
    }

    @Override
    public Map<String, KnOPSubsProfileInfoDTO> getProfileMdnNupmfsByBaseMdn(String baseMdn, boolean readOnly,
                                                                            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnNupmfsByBaseMdn(String, boolean, KnPersisterTxn)";
        Map<String, KnOPSubsProfileInfoDTO> mdnList = new HashMap<>();
        knLogger.debug(methodName, "ENTRY: getMdnForUPM");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdnList = subscrInfoDAO.getProfileMdnNupmfsByBaseMdn(baseMdn, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT: mdnList- ", KnGDPRTemplate.mapKeyMdn(mdnList));
        return mdnList;
    }

    @Override
    public List<String> getMdnForUPMList(List<String> baseMdn,
                                         KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnForUPM(List<String>, KnPersisterTxn)";
        List<String> mdnList = new ArrayList<>();
        knLogger.debug(methodName, "ENTRY: getMdnForUPM");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        mdnList = subscrInfoDAO.getMdnForUPMList(baseMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT: getMdnForUPM - ", KnGDPRTemplate.mdnList(mdnList));
        return mdnList;
    }

    @Override
    public void updateSubscrUserProfileFS(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrUserProfileFS(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", subsProfilePersistDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateSubscrUserProfileFS(subsProfilePersistDTO, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update Subscriber Profile ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update the Subscriber profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    @Override
    public void updateSubscrTS(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrTS(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Subscriber TS mdnList - ", KnGDPRTemplate.mdnList(mdnList));

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateSubscrTS(mdnList, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update Subscriber TS ");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update the Subscriber profile", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

    @Override
    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoForUFMIForAssignUserProfile(String ufmi, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "retrieveSubscriberInfoForUFMIForAssignUserProfile(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileDTO = subscrInfoDAO.selectSubscriberProfileForUFMIForAssignUserProfile(ufmi, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);

        return subsProfileDTO;
    }

    @Override
    public KnOPSubsProfileInfoDTO selectSubscriberProfileByAliasMdnForAssignUserProfile(String aliasMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileByAliasMdnForAssignUserProfile(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subsProfileDTO = subscrInfoDAO.selectSubscriberProfileByAliasMdnForAssignUserProfile(aliasMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);
        return subsProfileDTO;
    }
    
	@Override	    
	public void updateMCSIdsForUserProfiles(KnSubsProfilePersistDTO subsProfilePersistDTO,String mcId,String mcDataId,String mcVideoId,String mcPttId,KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateMCSIdsForUserProfiles(KnSubsProfilePersistDTO,String,String,String,String KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY: updateMCSIdsForUserProfiles");
		KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry()
				.createPOCSubscrInfoDA0(xdmPttServerId);
		subscrInfoDAO.updateMCSIdsForUserProfiles(subsProfilePersistDTO, mcId, mcDataId, mcVideoId, mcPttId,
				persisterTxn);
		knLogger.debug(methodName, "EXIT: updateMCSIdsForUserProfiles - ");

	}
	
	@Override
	public String getProfileName(int corpId, Integer userProfileIndex) throws KnDAOException {
		String methodName = "getProfileName(int, Integer)";
		knLogger.debug(methodName, "ENTRY: getProfileName");
		KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry()
				.createPOCSubscrInfoDA0(xdmPttServerId);
		knLogger.debug(methodName, "EXIT: getProfileName - ");
		return subscrInfoDAO.getProfileName(corpId, userProfileIndex);

	}

    public void updateMdnFiledsNActiveFS(KnSubsProfilePersistDTO subsProfilePersistDTO
            ,Map<String, String> mdnActivsFsMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdnFiledsNActiveFS()";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: - ", subsProfilePersistDTO ," mdnActivsFsMap ",mdnActivsFsMap);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateMdnFiledsNActiveFS(subsProfilePersistDTO,mdnActivsFsMap, persisterTxn);
            knLogger.debug(methodName, "updating Subscriber successfully");

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: update Subscriber");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update Subscriber ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
    }

	@Override
	public void updateProfileMdnDetails(KnSubsProfilePersistDTO subsProfilePersistDTO,List<String> UserProfileMdns, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateProfileMdnDetails()";
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: - ","UserProfileMdns--",KnGDPRTemplate.mdnList(UserProfileMdns),"subsProfilePersistDTO--",subsProfilePersistDTO );
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subscrInfoDAO.updateProfileMdnDetails(subsProfilePersistDTO,UserProfileMdns,persisterTxn);
            knLogger.debug(methodName, "updateProfileMdnDetails successfully");

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: updateProfileMdnDetails");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update Profile Mdn Details ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
		
	}
	
	@Override
	public Map<String, String> createUserProfileMdnMap(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "createUserProfileMdnMap(KnSubsProfilePersistDTO, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY: Create User Profile MDN Map ");
		KnUserProfileMdnMapDAO userProfileMdnMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry()
				.createUserProfileMdnMapDAO(xdmPttServerId);
       	knLogger.debug(methodName, "EXIT: createUserProfileMdnMap ");
		return userProfileMdnMapDAO.insertUserProfileMdn(subsProfilePersistDTO, persisterTxn);
		
	}

	@Override
	public void deleteUserProfileMdnMap(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "deleteSubscriberProfile(String, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY: delete Subscriber Profile ");
		KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
		subsProfilePersistDTO.setMdn(mdn);
		KnSubsAddlInfoPersistDTO subsAddlInfoPersistDTO = new KnSubsAddlInfoPersistDTO();
		subsAddlInfoPersistDTO.setMdn(mdn);
		KnUserProfileMdnMapDAO userProfileMdnMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry()
				.createUserProfileMdnMapDAO(xdmPttServerId);
		userProfileMdnMapDAO.delete(subsProfilePersistDTO, persisterTxn);

	}

	@Override
	public Map<String, String> getUserProfileMdnMap(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "deleteSubscriberProfile(String, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY: get User Profile Mdn Map ");
		KnUserProfileMdnMapDAO userProfileMdnMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry()
				.createUserProfileMdnMapDAO(xdmPttServerId);
		return userProfileMdnMapDAO.getUserProfileMdnMap(baseMdn, persisterTxn);
	}
	
	@Override
		public void updateUserProfileMdnMap(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateUserProfileMdnMap(KnSubsProfilePersistDTO, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY: update User Profile Mdn Map ");
		KnUserProfileMdnMapDAO userProfileMdnMapDAO = KnProvTablesRegistry.getProvXDMTablesRegistry()
				.createUserProfileMdnMapDAO(xdmPttServerId);
		userProfileMdnMapDAO.update(subsProfilePersistDTO, persisterTxn);
		knLogger.debug(methodName, "EXIT: updateUserProfileMdnMap ");

	}

    @Override
    public Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdate(List<String> profileMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "DAO.profileMdnEtagUpdate()";
        Map<String, Collection<KnDocChangeListDTO>> etagMap= null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: profile mdn: ",KnGDPRTemplate.mdnList(profileMdnList));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            etagMap  = subscrInfoDAO.profileMdnEtagUpdate(profileMdnList, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: Update Service Auth Status");

        } catch (KnDAOException e) {
            knLogger.error(methodName, KnConstants.DAO_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, KnConstants.UNEXPECTED_EXCEPTION_OCCURED, e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while update service auth Status ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        return etagMap;
    }

    public void updateSubApnIdForMdnList(List<String> mdnList, int apnId, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "updateSubApnIdForMdnList()";
        try {
            KnSubsAPNInfoDAO subsAPNInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createSubsAPNInfoDA0
                    (xdmPttServerId);
            subsAPNInfoDAO.updateSubApnIdForMdnList(mdnList, apnId, readOnly, persistTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw KnDbUtil.processException(e, "Failed while creating the Subscribers Apn profile", xdmPttServerId,
                    KnProvDAOSourceTypes.SUBSCRIBERAPNINFO, null);
        }
    }

    @Override
    public List<String> getBaseMdnByProfileMdn(List<String> profileMdnList,
                                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return subscrInfoDAO.getBaseMdnByProfileMdn(profileMdnList, persisterTxn);
    }

    @Override
    public KnOPSubsProfileInfoDTO selectUserProfileIdsByProfileMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return subscrInfoDAO.selectUserProfileIdsByProfileMdns(mdnList, persisterTxn);
    }

    @Override
    public void updateUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: update user profile name ", "subsProfilePersistDTO - ",subsProfilePersistDTO);
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateUserProfileName(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT: update User Profile Name ");
    }

    @Override
    public String selectUserProfileName(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: select user profile name ", "msn - ",KnGDPRTemplate.mdn(mdn));
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        String userProfileName=subscrInfoDAO.selectUserProfileName(mdn, persisterTxn);
        knLogger.info(methodName, "EXIT: selct User Profile Name ");
        return userProfileName;
    }

    @Override
    public Map<String, String> selectUserProfileNameList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: select user profile name ", "mdn - ",KnGDPRTemplate.mdnList(mdnList));
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        Map<String, String> userProfileNameList=subscrInfoDAO.selectUserProfileNameList(mdnList, persisterTxn);
        knLogger.info(methodName, "EXIT: selct User Profile Name ");
        return userProfileNameList;
    }

    @Override
    public String getSubClientSettings(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubClientSettings(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: Get Client Settings ", "mdn - ",KnGDPRTemplate.mdn(mdn));
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        //String recordingStatus=subscrInfoDAO.getSubClientSettings(mdn, persisterTxn);
        knLogger.info(methodName, "EXIT: selct User Profile Name ");
        //return recordingStatus;
        return null;
    }

    @Override
    public KnOPSubsProfileInfoDTO getSubscrClientSettings(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubClientSettings(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: Get Client Settings ", "mdn - ",KnGDPRTemplate.mdn(mdn));
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        KnOPSubsProfileInfoDTO subsProfileInfoDTO=subscrInfoDAO.getSubClientSettings(mdn, readOnly, persisterTxn);
        knLogger.info(methodName, "EXIT: selct Client Recording Status " + subsProfileInfoDTO);
        return subsProfileInfoDTO;
    }

    @Override
    public KnOPSubsAddlInfoProfileDTO setSubscrClientAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "setSubscrClientAddlInfo(KnSubsAddlInfoPersistDTO, KnPersisterTxn)";
        KnOPSubsAddlInfoProfileDTO subsAddlInfoProfileDTO=null;
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber AddlInfo");
        KnPOCSubscrAddlInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrAddlInfoDA0(xdmPttServerId);
        subscrInfoDAO.setSubscrClientAddlInfo(subsProfilePersistDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT:Set Subscriber AddlInfo done - ");
        return subsAddlInfoProfileDTO;
    }

    @Override
    public List<String> getProfileMdnListByBaseMdn(String baseMdn,
                                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return subscrInfoDAO.getProfileMdnListByBaseMdn(baseMdn, persisterTxn);
    }

    @Override
    public void clearUserProfileAssignment(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscrInfoDAO.clearUserProfileAssignment(mdnList, persisterTxn);
    }

    @Override
    public void updateToPrivacyOptStatus(Map<String,Integer> mapListForPrivacy,
                                         KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateToPrivacyOptStatus(mapListForPrivacy, persisterTxn);
    }

 	@Override
    public void createSubscriberCameraInfo(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "createSubscriberCameraInfo()";
        knLogger.info(methodName, "ENTRY: createSubscriberCameraInfo ", "subsProfilePersistDTO -", subsProfilePersistDTO);
        KnSubsCameraInfoDAO cameraInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCameraInfoDAO(xdmPttServerId);
        cameraInfoDAO.createSubscriberCameraInfo(subsProfilePersistDTO, persisterTxn);
        knLogger.info(methodName, "EXIT:  createSubscriberCameraInfo ");
    }

    @Override
    public Map<String, KnSubsCameraInfo> getSubscriberCameraInfo(Collection<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberCameraInfo()";
        knLogger.info(methodName, "ENTRY:  mdns- ", KnGDPRTemplate.mdnList(mdns));
        KnSubsCameraInfoDAO cameraInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCameraInfoDAO(xdmPttServerId);
        Map<String, KnSubsCameraInfo> subsCameraInfoMap = cameraInfoDAO.getSubscriberCameraInfo(mdns, readOnly, persisterTxn);
        knLogger.info(methodName, "EXIT:  ");

        return subsCameraInfoMap;
    }

    @Override
    public Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfo(Map<String, String> aliasIdIssuerMap, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubsAliasIdInfo()";
        knLogger.info(methodName, "ENTRY: aliasIdIssuerMap -", aliasIdIssuerMap);
        KnSubsAliasIdInfoDAO knSubsAliasIdInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createAliasIdInfoDAO(xdmPttServerId);
        Map<String, List<KnSubsAliasInfoDTO>> subsAliasIdMap = knSubsAliasIdInfoDAO.selectSubsAliasIdInfo(aliasIdIssuerMap, readOnly, persisterTxn);
        knLogger.info(methodName, "EXIT: ");
        return subsAliasIdMap;
    }

    @Override
    public Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfoByMdn(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubsAliasIdInfoByMdn(Collection<String>)";
        knLogger.info(methodName, "ENTRY: mdnList -", KnGDPRTemplate.mdnList(mdnList));
        KnSubsAliasIdInfoDAO knSubsAliasIdInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createAliasIdInfoDAO(xdmPttServerId);
        Map<String,List<KnSubsAliasInfoDTO>> subsAliasIdMap = knSubsAliasIdInfoDAO.selectSubsAliasIdInfoByMdn(mdnList,persisterTxn);
        knLogger.info(methodName, "EXIT: ");
        return subsAliasIdMap;
    }

    @Override
    public void insertIntoSubsAliasId(List<KnSubsAliasInfoDTO> aliasInfoList, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertIntoSubsAliasId(List<KnSubsAliasInfoDTO>,mdn,persisterTxn)";
        knLogger.info(methodName, "ENTRY: mdn -", KnGDPRTemplate.mdn(mdn));
        KnSubsAliasIdInfoDAO knSubsAliasIdInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createAliasIdInfoDAO(xdmPttServerId);
        knSubsAliasIdInfoDAO.insertIntoSubsAliasId(aliasInfoList,mdn,persisterTxn);
        knLogger.info(methodName, "EXIT: ");
    }

    @Override
    public void updateSubscriberCameraInfo(String mdn,KnSubsCameraInfo cameraInfo, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnSubsCameraInfoDAO cameraInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCameraInfoDAO(xdmPttServerId);
        cameraInfoDAO.updateSubscriberCameraInfo(mdn,cameraInfo, persisterTxn);
    }

    @Override
    public void deleteSubscriberCameraInfo(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnSubsCameraInfoDAO cameraInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCameraInfoDAO(xdmPttServerId);
        cameraInfoDAO.deleteSubscriberCameraInfo(mdn, persisterTxn);
    }

    @Override
    public void deleteSubsAliasId(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubsAliasId(String,persisterTxn)";
        knLogger.info(methodName, "ENTRY: mdn -", KnGDPRTemplate.mdn(mdn));
        KnSubsAliasIdInfoDAO knSubsAliasIdInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createAliasIdInfoDAO(xdmPttServerId);
        knSubsAliasIdInfoDAO.deleteSubsAliasId(mdn,persisterTxn);
        knLogger.info(methodName, "EXIT: ");

    }

    @Override
    public void deleteSubsAliasId(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubsAliasId(String,persisterTxn)";
        knLogger.info(methodName, "ENTRY: mdn -", KnGDPRTemplate.mdnList(mdnList));
        KnSubsAliasIdInfoDAO knSubsAliasIdInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createAliasIdInfoDAO(xdmPttServerId);
        knSubsAliasIdInfoDAO.deleteSubsAliasId(mdnList,persisterTxn);
        knLogger.info(methodName, "EXIT: ");

    }

    @Override
    public Map<String,List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfoByAliasId(Map<String, String> aliasIdIssuerMap, KnPersisterTxn persisterTxn) throws KnDAOException
    {
        String methodName = "selectSubsAliasIdInfoByAliasId()";
        knLogger.info(methodName, "ENTRY: aliasIdIssuerMap -", aliasIdIssuerMap);
        KnSubsAliasIdInfoDAO knSubsAliasIdInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createAliasIdInfoDAO(xdmPttServerId);
        Map<String,List<KnSubsAliasInfoDTO>> aliasIdMap = knSubsAliasIdInfoDAO.selectSubsAliasIdInfoByAliasId(aliasIdIssuerMap,persisterTxn);
        knLogger.info(methodName, "EXIT: ");
        return aliasIdMap;

    }

    @Override
    public void removeSubsAliasId(String mdn, List<KnSubsAliasInfoDTO> removeAliasInfoList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "RemoveSubsAliasId( mdn,  removeAliasInfoList, persisterTxn)";
        knLogger.info(methodName, "ENTRY: mdn -", KnGDPRTemplate.mdn(mdn));
        KnSubsAliasIdInfoDAO knSubsAliasIdInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createAliasIdInfoDAO(xdmPttServerId);
        knSubsAliasIdInfoDAO.deleteSubsAliasId(mdn,removeAliasInfoList,persisterTxn);
        knLogger.info(methodName, "EXIT: ");
    }

    @Override
    public void updateDefaultProfileFlag(String mcId,int corpId,int defaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        subscrInfoDAO.updateDefaultProfileFlag(mcId,corpId,defaultProfile, persisterTxn);
    }
    public int getMaxUserProfileIndex(String baseMdn,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return subscrInfoDAO.getMaxUserProfileIndex(baseMdn, persisterTxn);
    }

    @Override
    public Map<String, String> getBaseMdnListForRequestingMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getBaseMdnListForRequestingMdnList(List<String> mdnList, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: ", "mdnList - ",KnGDPRTemplate.mdnList(mdnList));
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        Map<String, String> baseMdnListForAReqMdnList=subscrInfoDAO.getBaseMdnListForRequestingMdnList(mdnList, persisterTxn);
        knLogger.info(methodName, "EXIT:");
        return baseMdnListForAReqMdnList;
    }

    @Override
    public KnOPSubsProfileInfoDTO getSubscriberProfileIfExist(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberProfileIfExist(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY: get Subscriber Info if exist");
        try {
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            subsProfileDTO = subscrInfoDAO.selectSubscriberProfileIfExist(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving Subscriber Info for mdn - " + mdn, xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.debug(methodName, "EXIT:Retrieved Subscriber Info - ", subsProfileDTO);

        return subsProfileDTO;
    }

    @Override
    public int retrieveCorpProfileCleanUp(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveCorpProfileCleanUp(int, KnPersisterTxn)";
        int corpProfileCleanUp;
        try {
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpProfileCleanUp = corpInfoDAO.retrieveCorpProfileCleanUp(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred -", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred -", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the Corporate Id", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
        return corpProfileCleanUp;
    }

    public int createCorporateAccount(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createCorporateAccount()";
        int corpId = -1;
        try {
            corpId = genInfoUtil.retrieveIdForTable(KnProvConstants.TABLE_CORP_INFO, xdmPttServerId,
                    KnProvConstants.TABLE_CORP_INFO_COLUMN, false, KnConstants.DUAL_DATA_STORE);

            corpProfilePersistDTO.setCorpId(corpId);
            long profileCreationTime = Calendar.getInstance().getTimeInMillis();
            corpProfilePersistDTO.setProfileCreationTime(profileCreationTime);
            corpProfilePersistDTO.setLastProfileUpdateTime(profileCreationTime);
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpInfoDAO.createCorporateAccount(corpProfilePersistDTO, persisterTxn);
            return corpId;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while creating the Corporate profile", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
    }

    @Override
    public int updateCorporateAccount(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateAccount()";
        int corpId = -1;
        try {
            corpId =corpProfilePersistDTO.getCorpId();
            long profileCreationTime = Calendar.getInstance().getTimeInMillis();
            corpProfilePersistDTO.setProfileCreationTime(profileCreationTime);
            corpProfilePersistDTO.setLastProfileUpdateTime(profileCreationTime);
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpInfoDAO.updateCorporateAccount(corpProfilePersistDTO, persisterTxn);
            return corpId;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the Corporate profile", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
    }

    public int getCorpId(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {

        final String methodName = "getCorpId(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            //ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            return corpInfoDAO.selectCorpId(extCorpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving corpId", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "CorpId Doesn't exist. Rethrowing Exception - ", e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CORPORATE_PROFILE, "Corp Profile doesnot exist", e);
            }
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int fetchCorpSubscriberCount(String extCorpid, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchCorpSubscriberCount(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        int corpSubscriberCount = -1;
        knLogger.debug(methodName, "ENTRY: Retrieving the Corporation [", extCorpid, "] Subscriber Count");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            corpSubscriberCount = subscrInfoDAO.fetchCorpSubscriberCnt(extCorpid, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Corporation Subscriber Count ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.info(methodName, "EXIT: Retrieved the Corporation [", extCorpid, "] Subscriber Count - ", corpSubscriberCount);
        return corpSubscriberCount;
    }

    public void deleteCorporateAccount(String extCorpId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorporateAccount(int, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: Delete Corporate Account - ", "corpId : ", corpId, "extCorpId: ", extCorpId);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);

            //PREASSIGNEDCORPHOME
            corpInfoDAO.deletePreassignedCorpHome(extCorpId, persisterTxn);
            //POCCORP_ADDLINFO
            corpInfoDAO.deleteCorpAddlInfo(corpId, persisterTxn);
            //POCCORPINFO
            corpInfoDAO.deleteCorpInfo(corpId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT: delete corporate account");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while delete Corporate Account", xdmPttServerId, KnProvDAOSourceTypes.CORPINFO, null);
        }
    }
    @Override
    public KnCorporateProfilepersistDTO1 getCorpID(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return subscrInfoDAO.getCorpID(extCorpId, persisterTxn);
    }
    @Override
    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(int CorpId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {
        KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return subscrInfoDAO.getCorporateAccountDetails(CorpId, persisterTxn);
    }
    @Override
    public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken) throws KnDAOException {
        String methodName = "retrieveCorporationAccountsList(KnPersisterTxn)";
        KnXDMCorpAccountsListDTO corpAccList = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: retrieve Corporation Accounts List");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnCorpInfoDAO corpInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createCorpInfoDAO(xdmPttServerId);
            corpAccList = corpInfoDAO.getCorpAccountsList(persisterTxn,fetchSize,nextToken);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving Corporation Accounts List", xdmPttServerId,
                    KnProvDAOSourceTypes.CORPINFO, null);
        }
        knLogger.info(methodName, "EXIT: retrieve Corporation Accounts List - ", corpAccList);

        return corpAccList;
    }
    /**
     * method for retrieve Corporate Subscriber Count (list Corporate Subscribers) including profile
     *
     * @param corpId       int
     * @param persisterTxn KnPersisterTxn
     * @return int (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public int retrieveCorpSubscriberWithProfileCnt(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveCorpSubscriberWithProfileCnt(int, KnPersisterTxn)";
        boolean ownedTxn = false;
        int corpSubscriberCount = -1;

        knLogger.debug(methodName, "ENTRY: Retrieving the Corporation [", corpId, "] Subscriber Count");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO subscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            corpSubscriberCount = subscrInfoDAO.getCorpSubscriberWithProfileCnt(corpId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }


        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred -", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the Corporation Subscriber Count ", xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }

        knLogger.info(methodName, "EXIT: Retrieved the Corporation [", corpId, "] Subscriber Count - ", corpSubscriberCount);

        return corpSubscriberCount;
    }

    @Override
    public KnExtGatewayInfoDTO getExtGatewayDetails(String extGatewayId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnAmperGatewayDAO amperGatewayDao = KnProvTablesRegistry.getProvXDMTablesRegistry().createAmperGatewayDAO(xdmPttServerId);
        return amperGatewayDao.getExtGatewayDetails(extGatewayId, persisterTxn);
    }

    @Override
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberServiceAuthStatus(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        Map<String, Integer> serviceAuthStatus;
        knLogger.debug(methodName, "ENTRY: retrieve ServiceAuthStatus for mdn -  ", KnGDPRTemplate.mdnList(mdns));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
            serviceAuthStatus = pocSubscrInfoDAO.getSubscriberServiceAuthStatus(mdns, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving service auth status for mdn - " + KnGDPRTemplate.mdnList(mdns), xdmPttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, null);
        }
        knLogger.info(methodName, "EXIT:Retrieved serviceAuthStatus - ", serviceAuthStatus);

        return serviceAuthStatus;
    }


    @Override
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException {
        String methodName = "retrieveExtGWProfileList()";
        KnXDMExtGWProfileListDTO extGWProfileListDTO = new KnXDMExtGWProfileListDTO();
        knLogger.debug(methodName, "ENTRY: retrieve external GW Profile List");
        try {
            KnAmperGatewayDAO amperGatewayDao = KnProvTablesRegistry.getProvXDMTablesRegistry().createAmperGatewayDAO(xdmPttServerId);
            List<KnExtGWProfileInfoDTO> knExtGWProfileList = amperGatewayDao.getExtGWProfileList();
            extGWProfileListDTO.setExtGatewayInfoList(knExtGWProfileList);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving external GW Profile List", xdmPttServerId, KnProvDAOSourceTypes.EXT_GATEWAY_INFO, null);
        }
        knLogger.info(methodName, "EXIT: retrieve external GW Profile List - ", extGWProfileListDTO);

        return extGWProfileListDTO;
    }

    @Override
    public List<Integer> getGroupIdsList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return pocSubscrInfoDAO.getGroupIdsList(mdn, persisterTxn);
    }

    @Override
    public List<Integer> getSharedGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return pocSubscrInfoDAO.getSharedGroupList(mdn, persisterTxn);
    }

    @Override
    public Map<String, List<Map<Integer, String>>> getGroupInfo(List<Integer> corpGroupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return pocSubscrInfoDAO.getGroupInfo(corpGroupIds, persisterTxn);
    }

    @Override
    public String getAddDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return pocSubscrInfoDAO.getAddDeviceInfo(mdn, persisterTxn);
    }

    @Override
    public Map<String, Integer> getZoneAndChannerConfigValues(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return pocSubscrInfoDAO.getZoneAndChannerConfigValues(extCorpId, persisterTxn);
    }

    @Override
    public List<Integer> getBroadcasterGroupIds(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        return pocSubscrInfoDAO.getBroadcasterGroupIds(mdn, persisterTxn);
    }

    @Override
    public void insertSubsAddlTGList(Collection<KnSubsAddlTGInfoDTO> subsAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPOCSubscrInfoDA0(xdmPttServerId);
        pocSubscrInfoDAO.insertSubsAddlTGList(subsAddlTGInfoDTOS, persisterTxn);
    }

    @Override
    public void updateEmergencyDocEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnEmergencyDocDAO emergencyDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createEmergencyDocDAO(xdmPttServerId);
        emergencyDocDAO.updateEmergencyDocEtag(mdn, persisterTxn);
    }

}