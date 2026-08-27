/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnXDMSSvcConfigDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit KUmar           Jan 15, 2011       7.0
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


public class KnXDMSSvcConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSSvcConfigDAO.class);

    private static final String className = KnXDMSSvcConfigDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.XDMS_SVC_CONFIG";

    private static final String PTT_SERVER_ID = "PTTSERVERID";

    private static final String XCAP_ROOT_URI = "XCAPROOTURI";
    // private static final String PRIMARY_XDMS_URI = "PRIMARYXDMSURI";
    //  private static final String GEO_XDMS_URI = "GEOXDMSURI";
    private static final String MAX_PUBLIC_CONTACTS_PER_SUBSCR = "MAXPUBLICCONTACTSPERSUBSCR";
    private static final String MAX_PUBLIC_POC_GRPS_PER_SUBSCR = "MAXPUBLICPOCGRPSPERSUBSCR";
    private static final String PRIVACY_AMB_DISC_LISTEN = "PRIVACY_AMB_DISC_LISTEN";
    private static final String MAX_CORP_GRPS_PER_SUBSCR = "MAXCORPGRPSPERSUBSCR";
    private static final String MAX_CORP_GRPS_PER_LARGE_DISPATCH = "MAXCORPGRPSPERLARGEDISPATCH";
    private static final String MAX_MEMBERS_PER_PUBLIC_POC_GRP = "MAXMEMBERSPERPUBLICPOCGRP";
    private static final String AUTH_REALM = "AUTH_REALM";
    private static final String PUBLIC_POC_GRP_CONFURITEMPLATE = "PUBLICPOCGRP_CONFURITEMPLATE";
    private static final String MAX_CORP_CONTACTS_PER_SUBSCR = "MAXCORPCONTACTSPERSUBSCR";
    private static final String CORP_POC_GRP_CONF_URI_TEMPLATE = "CORPPOCGRP_CONFURITEMPLATE";
    private static final String MAX_SUBSCR_PER_CORP = "MAX_CORP_SUBSCRS";
    private static final String MAX_SUBLISTS_PER_CORP = "MAXSUBLISTSPERCORP";
    private static final String MAX_MEMBERS_PER_CORP_SUBLIST = "MAXMEMBERSPERCORPSUBLIST";

    private static final String MAX_EXTCONTACTS_PER_CORP = "MAXEXTCONTACTSPERCORP";
    private static final String MAX_POC_GRPS_PER_CORP = "MAXPOCGRPSPERCORP";
    private static final String MAX_MEMBERS_PER_CORP_POC_GRP = "MAXMEMBERSPERCORPPOCGRP";


    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + XCAP_ROOT_URI + ", " + MAX_PUBLIC_CONTACTS_PER_SUBSCR + ", " + MAX_PUBLIC_POC_GRPS_PER_SUBSCR + ", "
            + MAX_CORP_GRPS_PER_SUBSCR + ", " + MAX_CORP_GRPS_PER_LARGE_DISPATCH + ", " + MAX_MEMBERS_PER_PUBLIC_POC_GRP + ", " + AUTH_REALM + ", "
            + PUBLIC_POC_GRP_CONFURITEMPLATE + ", " + MAX_CORP_CONTACTS_PER_SUBSCR + ", " + CORP_POC_GRP_CONF_URI_TEMPLATE + ", "
            + MAX_SUBSCR_PER_CORP + ", " + MAX_SUBLISTS_PER_CORP + ", " + MAX_MEMBERS_PER_CORP_SUBLIST + ", "
            + MAX_EXTCONTACTS_PER_CORP + ", " + MAX_POC_GRPS_PER_CORP + ", " + MAX_MEMBERS_PER_CORP_POC_GRP + ", " + PRIVACY_AMB_DISC_LISTEN
            + " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";


    public KnXDMSSvcConfigDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public KnXDMSSvcConfigDTO selectXDMSSrvConfig(KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "selectXDMSSrvConfig(KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnXDMSSvcConfigDTO svcConfigDTO = new KnXDMSSvcConfigDTO();
        knLogger.info( methodName, "ENTRY: Select XDMS  Svc Config ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                ownedTxn = true;
            }

            query = SELECT_ALL_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pttServerId);

            knLogger.debug( methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");


            if (rs.next()) {
                svcConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                svcConfigDTO.setXCAPRootURI(rs.getString(XCAP_ROOT_URI));
                // svcConfigDTO.setPrimaryXDMSURI(rs.getString(PRIMARY_XDMS_URI));
                //  svcConfigDTO.setGeoXDMSURI(rs.getString(GEO_XDMS_URI));
                svcConfigDTO.setMaxPublicContactsPerSubscr(rs.getInt(MAX_PUBLIC_CONTACTS_PER_SUBSCR));
                svcConfigDTO.setMaxPublicPOCGrpsPerSubscr(rs.getInt(MAX_PUBLIC_POC_GRPS_PER_SUBSCR));
                svcConfigDTO.setMaxCorpGrpsPerSubscr(rs.getInt(MAX_CORP_GRPS_PER_SUBSCR));
                svcConfigDTO.setMaxCorpGrpsPerLargeDispatch(rs.getInt(MAX_CORP_GRPS_PER_LARGE_DISPATCH));
                svcConfigDTO.setMaxMembersPerPublicPOCGrp(rs.getInt(MAX_MEMBERS_PER_PUBLIC_POC_GRP));
                svcConfigDTO.setAuth_realm(rs.getString(AUTH_REALM));
                svcConfigDTO.setPublicPocGrp_ConfURITemplate(rs.getString(PUBLIC_POC_GRP_CONFURITEMPLATE));
                svcConfigDTO.setMaxCorpContactsPerSubscr(rs.getInt(MAX_CORP_CONTACTS_PER_SUBSCR));
                svcConfigDTO.setCorpPocGrp_ConfURITemplate(rs.getString(CORP_POC_GRP_CONF_URI_TEMPLATE));
                svcConfigDTO.setMaxSubscrPerCorp(rs.getInt(MAX_SUBSCR_PER_CORP));
                svcConfigDTO.setMaxSublistsPerCorp(rs.getInt(MAX_SUBLISTS_PER_CORP));
                svcConfigDTO.setMaxMembersPerCorpSublist(rs.getInt(MAX_MEMBERS_PER_CORP_SUBLIST));
                svcConfigDTO.setMaxExtContactsPerCorp(rs.getInt(MAX_EXTCONTACTS_PER_CORP));
                // MaxPOCGrpsPerCorp - now fetched from MICROSVCS_COMMONCONFIG, will be set later
                // svcConfigDTO.setMaxPOCGrpsPerCorp(rs.getInt(MAX_POC_GRPS_PER_CORP));
                svcConfigDTO.setMaxMembersPerCorpPOCGrp(rs.getInt(MAX_MEMBERS_PER_CORP_POC_GRP));
                
                // Fetch MAXPOCGRPSPERCORP from MICROSVCS_COMMONCONFIG
                PreparedStatement pStmtMaxPOC = null;
                ResultSet rsMaxPOC = null;
                try {
                    String queryMaxPOC = "SELECT PARAMVALUE FROM DG.MICROSVCS_COMMONCONFIG WHERE PARAMNAME = 'MAXPOCGRPSPERCORP'";
                    pStmtMaxPOC = conn.prepareStatement(queryMaxPOC);
                    knLogger.debug(methodName, "Query: Executing Query - ", queryMaxPOC);
                    rsMaxPOC = pStmtMaxPOC.executeQuery();
                    if (rsMaxPOC.next()) {
                        svcConfigDTO.setMaxPOCGrpsPerCorp(rsMaxPOC.getInt("PARAMVALUE"));
                        knLogger.debug(methodName, "Retrieved MAXPOCGRPSPERCORP from MICROSVCS_COMMONCONFIG: ", rsMaxPOC.getInt("PARAMVALUE"));
                    } else {
                        knLogger.warn(methodName, "MAXPOCGRPSPERCORP not found in MICROSVCS_COMMONCONFIG");
                    }
                } catch (SQLException sqlE) {
                    knLogger.error(methodName, "Error fetching MAXPOCGRPSPERCORP from MICROSVCS_COMMONCONFIG: ", sqlE);
                } finally {
                    KnDbUtil.closeResultSet(rsMaxPOC);
                    KnDbUtil.closeStatement(pStmtMaxPOC);
                }

            } else {
                knLogger.error( methodName, "XDMS Service Config doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "XDMS Service Config Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.XDMSSRVCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning XDMS Svc Config ", svcConfigDTO);
            return svcConfigDTO;

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select XDMS Service Config - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.XDMSSRVCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select XDMS Service Config - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.XDMSSRVCONFIG, query);
        } finally {
             KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select XDMS Service Config");
        }
    }
}
