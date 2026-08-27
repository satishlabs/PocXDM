/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnPOCSvcConfigDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by venkata sudhakar talluri on 23-1-2019
 * Table Name :- DG.POC_SVC_CONFIG
 */


public class KnPOCSvcConfigDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCSvcConfigDAO.class);
    private String pttServerId;

    public static final String TABLENAME = "DG.POC_SVC_CONFIG";
    private static final String PTT_SERVER_ID = "PTTSERVERID";
    private static final String PRIMARY_POC_SERVER_URI = "PRIMARYPOCSERVERURI";
    //private static final String GEO_POC_SERVER_URI = "GEOPOCSERVERURI";
    private static final String POC_CONF_FACTORY_URI = "POC_CONFFACTORYURI";
    private static final String PREEST_SESSION_VALIDITY = "PREESTSESSIONVALIDITY";
    private static final String POC_PUBLISH_VALIDITY = "POCPUBLISHVALIDITY";
    private static final String ENABLE_INSTAPOC = "ENABLEINSTAPOC";
    private static final String MAX_LEGS_IN_ADHOC_GRP_CALL = "MAXLEGSINADHOCGRPCALL";
    private static final String MAX_TERM_LEGS_IN_POC_GRP_CALL = "MAXTERMLEGSINPOCGRPCALL";
    private static final String TB_CP_REQ_TIMEOUT = "TBCPREQTIMEOUT";
    private static final String NUM_TBCP_RETRIES_BY_CLIENT = "NUMTBCPRETRIESBYCLIENT";
    private static final String MEDIA_PORT_REF_RESH_TIME = "MEDIAPORTREFRESHTIME";
    private static final String NUM_MEDIA_PORT_KA_MSGS = "NUMMEDIAPORTKAMSGS";
    private static final String MEDIA_PORT_KA_MSG_SIZE = "MEDIAPORTKAMSGSIZE";
    private static final String MEDIA_IDLE_TIMER = "MEDIAIDLETIMER";
    private static final String MAX_FLOOR_HOLD_DURATION = "MAXFLOORHOLDDURATION";
    private static final String FLOOR_ID_LEDETECTION_TIMER = "FLOORIDLEDETECTIONTIMER";
    private static final String MAX_TALK_BURST_GRACE_TIME = "MAXTALKBURSTGRACETIME";
    private static final String ENABLE_SUPERVISOR_OVERRIDE = "ENABLE_SUPERVISORY_OVERRIDE";
    private static final String ENABLE_MISSED_CALL_ALERT = "ENABLE_MISSED_CALL_ALERT";
    private static final String ENABLE_CALL_ENDED_ALERT = "ENABLE_CALL_ENDED_ALERT";
    private static final String SIP_RETRY_TIMER = "SIPRETRYTIMER";
    private static final String SIP_RETRY_TIMER_BACK_OFF = "SIPRETRYTIMERBACKOFF";
    private static final String IP_DEBOUNCE_TIMER = "IPDEBOUNCETIMER";
    private static final String CLIENT_TBCP_FlOOR_REQ_RETRY_TIMER = "Client_TBCPFloorReqRetryTimer";
    private static final String CLIENT_TBCP_REL_REQ_RETRY_TIMER = "Client_TBCPRelReqRetryTimer";
    private static final String PRE_CALL_NUM_MEDIA_PORT_KA_MSGS = "PreCallNumMediaPortKAMsgs";
    private static final String PRE_CALL_MEDIA_PORT_KA_MSG_SIZE = "PrecallMediaPortKAMsgSize";
    private static final String PRE_Call_MEDIA_PORT_KA_INTERVAL = "PreCallMediaPortKAInterval";
    private static final String MEDIA_PORT_KA_INTERVAL = "MediaPortKAInterval";
    private static final String PRE_CALL_MEDIA_KA_DURATION = "PreCallMediaKADuration";
    private static final String MEDIA_SECURE_SESSION_REFRESH_INTVL = "MEDIASECURESESSIONREFRESHINTVL";
    private static final String CLIENT_IN_CALL_SUSPEND_TIMER = "CLIENTINCALLSUSPENDTIMER";
    private static final String ENABLE_POC_WIFI = "ENABLEPOCWIFI";
    private static final String DYNAMIC_QOS_FLAG = "DYNAMICQOSFLAG";
    public static final String MAXSIMULDEDICATESESSION = "MAXSIMULDEDICATEDSESSION";
    public static final String MAXSIMULDYNAMICSESSION = "MAXSIMULDYNAMICSESSION";
    public static final String MCVIDEOFLOORHOLDTIMER = "MCVIDEOFLOORHOLDTIMER";


    public static final String GET_MAX_SSDS = "SELECT " + MAXSIMULDEDICATESESSION + " FROM " + TABLENAME + " WHERE PTTSERVERID =?";

   // public static final String GET_MAXSIMULDYNAMICSESSION = "SELECT " + MAXSIMULDYNAMICSESSION + " FROM " + TABLENAME + " WHERE PTTSERVERID =?";
    
	private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + PRIMARY_POC_SERVER_URI + ", " + MAX_LEGS_IN_ADHOC_GRP_CALL + ", " + POC_CONF_FACTORY_URI + ", " +
            TB_CP_REQ_TIMEOUT + ", " + FLOOR_ID_LEDETECTION_TIMER + ", " + MEDIA_IDLE_TIMER + ", " +
            MEDIA_PORT_REF_RESH_TIME + ", " + POC_PUBLISH_VALIDITY + ", " + PREEST_SESSION_VALIDITY + ", " +
            NUM_MEDIA_PORT_KA_MSGS + ", " + MEDIA_PORT_KA_MSG_SIZE + ", " + NUM_TBCP_RETRIES_BY_CLIENT + ", " +
            ENABLE_INSTAPOC + ", " + MAX_FLOOR_HOLD_DURATION + ", " + MAX_TERM_LEGS_IN_POC_GRP_CALL + ", " +
            MAX_TALK_BURST_GRACE_TIME + ", " + ENABLE_SUPERVISOR_OVERRIDE + ", " + ENABLE_SUPERVISOR_OVERRIDE + ", " +
            ENABLE_MISSED_CALL_ALERT + ", " + ENABLE_MISSED_CALL_ALERT + ", " + ENABLE_CALL_ENDED_ALERT + ", " +
            SIP_RETRY_TIMER + ", " + SIP_RETRY_TIMER_BACK_OFF + ", " + SIP_RETRY_TIMER_BACK_OFF + ", " +
            IP_DEBOUNCE_TIMER + ", " + CLIENT_TBCP_FlOOR_REQ_RETRY_TIMER + ", " +
            CLIENT_TBCP_REL_REQ_RETRY_TIMER + ", " + PRE_CALL_NUM_MEDIA_PORT_KA_MSGS + ", " +
            PRE_CALL_MEDIA_PORT_KA_MSG_SIZE + ", " + PRE_Call_MEDIA_PORT_KA_INTERVAL + ", " +
            MEDIA_PORT_KA_INTERVAL + ", " + PRE_CALL_MEDIA_KA_DURATION + ", " + MEDIA_SECURE_SESSION_REFRESH_INTVL + ", " + CLIENT_IN_CALL_SUSPEND_TIMER + ", " + ENABLE_POC_WIFI + ", " + DYNAMIC_QOS_FLAG +
            ","+MAXSIMULDEDICATESESSION+","+MAXSIMULDYNAMICSESSION+" , "+MCVIDEOFLOORHOLDTIMER+
            " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";

    public KnPOCSvcConfigDAO(String pttServerId) {
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

    public int getMaxSSDDSessionCnt(KnPersisterTxn persisterTxn,String pocPttServerId) throws KnDAOException {
        final String methodName = "getMaxSSDDSessionCnt(KnPersisterTxn,String)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int maxSSDDYCnt = 0;

        try {
            query = GET_MAX_SSDS;
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, pocPttServerId);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                maxSSDDYCnt = rs.getInt(1);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve MAXSIMULDEDICATEDSESSION for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POC_SVC_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.info(methodName, "Returning MAXSIMULDEDICATEDSESSION count - ", maxSSDDYCnt);
        return maxSSDDYCnt;
    }

	
	   public KnPOCSvcConfigDTO selectPOCSvcConfig(String pocPttServerId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectPOCSvcConfig(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnPOCSvcConfigDTO srvcConfigDTO = new KnPOCSvcConfigDTO();
        knLogger.info( methodName, "--->ENTRY: Select POC SVC Config ");
        try {

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pocPttServerId);

            knLogger.debug( methodName, "--->Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "--->Query: Executed ");


            if (rs.next()) {
                srvcConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                srvcConfigDTO.setPrimaryPOCServerURI(rs.getString(PRIMARY_POC_SERVER_URI));
               // srvcConfigDTO.setGeoPOCServerURI(rs.getString(GEO_POC_SERVER_URI));
                srvcConfigDTO.setMaxLegsInAdhocGrpCall(rs.getInt(MAX_LEGS_IN_ADHOC_GRP_CALL));
                srvcConfigDTO.setPOC_ConfFactoryURI(rs.getString(POC_CONF_FACTORY_URI));
                srvcConfigDTO.setTBCPReqTimeout(rs.getInt(TB_CP_REQ_TIMEOUT));
                srvcConfigDTO.setFloorIdleDetectionTimer(rs.getInt(FLOOR_ID_LEDETECTION_TIMER));
                srvcConfigDTO.setMediaIdleTimer(rs.getInt(MEDIA_IDLE_TIMER));
                srvcConfigDTO.setMediaPortRefreshTime(rs.getInt(MEDIA_PORT_REF_RESH_TIME));
                srvcConfigDTO.setPOCPublishValidity(rs.getInt(POC_PUBLISH_VALIDITY));
                srvcConfigDTO.setPreestSessionValidity(rs.getInt(PREEST_SESSION_VALIDITY));
                srvcConfigDTO.setNumMediaPortKAMsgs(rs.getInt(NUM_MEDIA_PORT_KA_MSGS));
                srvcConfigDTO.setMediaPortKAMsgSize(rs.getInt(MEDIA_PORT_KA_MSG_SIZE));
                srvcConfigDTO.setNumTBCPRetriesByClient(rs.getInt(NUM_TBCP_RETRIES_BY_CLIENT));
                srvcConfigDTO.setEnableInstaPOC(rs.getInt(ENABLE_INSTAPOC));
                srvcConfigDTO.setMaxFloorHoldDuration(rs.getInt(MAX_FLOOR_HOLD_DURATION));
                srvcConfigDTO.setMaxTermLegsInPOCGrpCall(rs.getInt(MAX_TERM_LEGS_IN_POC_GRP_CALL));
                srvcConfigDTO.setMaxTalkBurstGraceTime(rs.getInt(MAX_TALK_BURST_GRACE_TIME));
                srvcConfigDTO.setEnableSuperVisoryOverride(rs.getInt(ENABLE_SUPERVISOR_OVERRIDE));
                srvcConfigDTO.setEnableMissedCallAlert(rs.getInt(ENABLE_MISSED_CALL_ALERT));
                srvcConfigDTO.setEnableCallEndedAlert(rs.getInt(ENABLE_CALL_ENDED_ALERT));
                srvcConfigDTO.setSipRetryTimer(rs.getInt(SIP_RETRY_TIMER));
                srvcConfigDTO.setSipRetryTimerBackOff(rs.getInt(SIP_RETRY_TIMER_BACK_OFF));
                srvcConfigDTO.setClient_TBCPFloorReqRetryTimer(rs.getInt(CLIENT_TBCP_FlOOR_REQ_RETRY_TIMER));
                srvcConfigDTO.setClient_TBCPRelReqRetryTimer(rs.getInt(CLIENT_TBCP_REL_REQ_RETRY_TIMER));
                srvcConfigDTO.setPreCallNumMediaPortKAMsgs(rs.getInt(PRE_CALL_NUM_MEDIA_PORT_KA_MSGS));
                srvcConfigDTO.setPreCallMediaPortKAInterval(rs.getInt(PRE_Call_MEDIA_PORT_KA_INTERVAL));
                srvcConfigDTO.setPrecallMediaPortKAMsgSize(rs.getInt(PRE_CALL_MEDIA_PORT_KA_MSG_SIZE));
                srvcConfigDTO.setMediaPortKAInterval(rs.getInt(MEDIA_PORT_KA_INTERVAL));
                srvcConfigDTO.setPreCallMediaKADuration(rs.getInt(PRE_CALL_MEDIA_KA_DURATION));
                srvcConfigDTO.setIpDebounceTimer(rs.getInt(IP_DEBOUNCE_TIMER));
                srvcConfigDTO.setMediaSecureSesRefIntvl(rs.getInt(MEDIA_SECURE_SESSION_REFRESH_INTVL));
                srvcConfigDTO.setClientInCallSusTimer(rs.getInt(CLIENT_IN_CALL_SUSPEND_TIMER));
                srvcConfigDTO.setEnablePocWifi(rs.getInt(ENABLE_POC_WIFI));
                srvcConfigDTO.setDynamicQosFlag(rs.getInt(DYNAMIC_QOS_FLAG));
                srvcConfigDTO.setMaxSDYSession(rs.getInt(MAXSIMULDYNAMICSESSION));
                srvcConfigDTO.setMaxSDDSession(rs.getInt(MAXSIMULDEDICATESESSION));
                srvcConfigDTO.setMcvideofloorholdtimer(rs.getInt(MCVIDEOFLOORHOLDTIMER));
            } else {
                knLogger.error( methodName, "--->POC Service config doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "POC Service config Doesnt exist",
                        pttServerId, KnDAOSourceTypes.POC_SVC_CONFIG, query);
            }

            knLogger.debug( methodName, "returning poc service Config ", srvcConfigDTO);
            return srvcConfigDTO;

        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to selectPOC Service config - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.POC_SVC_CONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select POC Service config - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POC_SVC_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info( methodName, "--->EXIT : select POC Service config");
        }
    }


}
