/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business.impl;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.subsmgmt.resources.KnSubConfigConstants;

import java.util.HashMap;
import java.util.Map;


public class KnSubsConfigParams {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsConfigParams.class);

    /**
     * Returns 1.x Version Map ids
     *
     * @param mapServerIds map of PttServer IDs
     * @return
     */
    public Map<String, Map<String, String>> getParamKeys(String pVersion, Map<String, String> mapServerIds) {
        String methodName = "getParamKeys(String ,Map<String,String>)";
        Map<String, Map<String, String>> mapVersion = new HashMap<String, Map<String, String>>();

        switch (pVersion) {
            case KnSubConfigConstants.PV_1:
                knLogger.debug(methodName, "PV Version 1.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_REGISTRAR_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_REGISTRAR_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_SESSION_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_SESSION_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_POC_SETTINGS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_POC_SETTINGS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_IPA_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_IPA_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_PRESENCE_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_RLS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_RLS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_XDMS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_XDMS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAX_MEMBERS_PER_CORP_GROUP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.REGISTER_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PUBLISH_POC_SETTINGS_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PUBLISH_PRESENCE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.INVITE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RLS_SUBSCRIPTION_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUPPORT_PRE_ESTABLISHED_SESSION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUPPORT_SIMULTANEOUS_SESSION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_SUBSCRIPTION_PROXY_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XDMS_SUBSCRIPTION_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.NUM_OF_WAKEUP_TRIGGERS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WAKEUP_TIME_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RLS_SERVICE_TEMPLATE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PRESENCE_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LOCATION_PUBLISH_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ROAMING_ALLOWED, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ROAMING_BIT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_DEBOUNCE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_DURATION, mapServerIds);

                // mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                break;

            case KnSubConfigConstants.PV_2:
                knLogger.debug(methodName, "PV Version 2.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_REGISTRAR_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_REGISTRAR_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_SESSION_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_SESSION_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_POC_SETTINGS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_IPA_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_IPA_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_PRESENCE_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_RLS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_RLS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_XDMS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_XDMS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAX_MEMBERS_PER_CORP_GROUP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.REGISTER_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PUBLISH_POC_SETTINGS_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PUBLISH_PRESENCE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.INVITE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RLS_SUBSCRIPTION_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUPPORT_PRE_ESTABLISHED_SESSION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUPPORT_SIMULTANEOUS_SESSION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GEO_SUBSCRIPTION_PROXY_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XDMS_SUBSCRIPTION_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.NUM_OF_WAKEUP_TRIGGERS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WAKEUP_TIME_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RLS_SERVICE_TEMPLATE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PRESENCE_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LOCATION_PUBLISH_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ROAMING_ALLOWED, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ROAMING_BIT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_DEBOUNCE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_DURATION, mapServerIds);

                //mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                break;

            case KnSubConfigConstants.PV_3:
                knLogger.debug(methodName, "PV Version 3.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_SESSION_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.REGISTER_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_REGISTRAR_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_POC_SETTINGS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_RLS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_XDMS_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_IPA_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XDMS_SUBSCRIPTION_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.NUM_OF_WAKEUP_TRIGGERS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WAKEUP_TIME_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RLS_SERVICE_TEMPLATE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PUBLISH_POC_SETTINGS_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PRESENCE_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LOCATION_PUBLISH_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ROAMING_ALLOWED, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ROAMING_BIT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_DEBOUNCE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.INVITE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RLS_SUBSCRIPTION_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_DURATION, mapServerIds);

                mapVersion.put(KnSubConfigConstants.IP_DEBOUNCE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRECALL_KA_DURATION, mapServerIds);

                // mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PUBLISH_PRESENCE_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAX_MEMBERS_PER_CORP_GROUP, mapServerIds);
                break;

            case KnSubConfigConstants.PV_5:
                knLogger.debug(methodName, "PV Version 5.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                break;
            case KnSubConfigConstants.PV_6:
                knLogger.debug(methodName, "PV Version 6.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                break;
            case KnSubConfigConstants.PV_7:
                knLogger.debug(methodName, "PV Version 7.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                break;

            case KnSubConfigConstants.PV_8:
                knLogger.debug(methodName, "PV Version 8.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                break;

            case KnSubConfigConstants.PV_9:
                knLogger.debug(methodName, "PV Version 9.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);
                break;

            case KnSubConfigConstants.PV_10:
                knLogger.debug(methodName, "PV Version 10.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);
                break;

            case KnSubConfigConstants.PV_11:
                knLogger.debug(methodName, "PV Version 11.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);
                break;

            case KnSubConfigConstants.PV_12:
                knLogger.debug(methodName, "PV Version 12.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);
                break;

            case KnSubConfigConstants.PV_13:
                knLogger.debug(methodName, "PV Version 13.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);
                break;
            case KnSubConfigConstants.PV_14:
            case KnSubConfigConstants.PV_15:
                knLogger.debug(methodName, "PV Version 14.x or 15.x ");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);


                break;

            case KnSubConfigConstants.PV_16:
            case KnSubConfigConstants.PV_17:
            case KnSubConfigConstants.PV_18:
                knLogger.debug(methodName, "PV Version 16.x ,17.x ,18.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);
                break;

            case KnSubConfigConstants.PV_19:
                knLogger.debug(methodName, "PV Version 19.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);
                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);
                break;

            case KnSubConfigConstants.PV_20:
                knLogger.debug(methodName, "PV Version 20.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES,mapServerIds);

                break;

            case KnSubConfigConstants.PV_21:
            case KnSubConfigConstants.PV_22:
                knLogger.debug(methodName, "PV Version 21.x or 22.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES,mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS,mapServerIds);
                break;

            case KnSubConfigConstants.PV_23:
                knLogger.debug(methodName, "PV Version 23.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES,mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS,mapServerIds);

                //PV23
                mapVersion.put(KnSubConfigConstants.MAX_VIDEO_SESSIONS, mapServerIds);
                break;

            case KnSubConfigConstants.PV_24:
                knLogger.debug(methodName, "PV Version 24.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES,mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS,mapServerIds);

                //PV23
                mapVersion.put(KnSubConfigConstants.MAX_VIDEO_SESSIONS, mapServerIds);

                //PV24
                mapVersion.put(KnSubConfigConstants.M_MEM_USR_REGRP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_USR_REGRPS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_SERV_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_DEFAULT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MIN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MAX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_LIST, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_BTF_PER_OWNER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_GRP_REGRPS, mapServerIds);
                break;

            case KnSubConfigConstants.PV_25:
                knLogger.debug(methodName, "PV Version 25.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES,mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS,mapServerIds);

                //PV23
                mapVersion.put(KnSubConfigConstants.MAX_VIDEO_SESSIONS, mapServerIds);

                //PV24
                mapVersion.put(KnSubConfigConstants.M_MEM_USR_REGRP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_USR_REGRPS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_SERV_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_DEFAULT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MIN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MAX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_LIST, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_BTF_PER_OWNER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_GRP_REGRPS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ALTITUDE_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.VERTICALACCURACY_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_MISSING_TIMER, mapServerIds);
                break;

            case KnSubConfigConstants.PV_26:
                knLogger.debug(methodName, "PV Version 26.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES,mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS,mapServerIds);

                //PV23
                mapVersion.put(KnSubConfigConstants.MAX_VIDEO_SESSIONS, mapServerIds);

                //PV24
                mapVersion.put(KnSubConfigConstants.M_MEM_USR_REGRP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_USR_REGRPS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_SERV_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_DEFAULT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MIN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MAX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_LIST, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_BTF_PER_OWNER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_GRP_REGRPS, mapServerIds);

                //PV25
                mapVersion.put(KnSubConfigConstants.ALTITUDE_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.VERTICALACCURACY_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_MISSING_TIMER, mapServerIds);
                break;

            case KnSubConfigConstants.PV_27:
                knLogger.debug(methodName, "PV Version 27.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI,mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG,mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO,mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W,mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W,mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I,mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP,mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L,mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S,mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S,mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS,mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES,mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS,mapServerIds);

                //PV23
                mapVersion.put(KnSubConfigConstants.MAX_VIDEO_SESSIONS, mapServerIds);

                //PV24
                mapVersion.put(KnSubConfigConstants.M_MEM_USR_REGRP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_USR_REGRPS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_SERV_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_DEFAULT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MIN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MAX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_LIST, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_BTF_PER_OWNER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_GRP_REGRPS, mapServerIds);

                //PV25
                mapVersion.put(KnSubConfigConstants.ALTITUDE_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.VERTICALACCURACY_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_MISSING_TIMER, mapServerIds);

                //PV27
                mapVersion.put(KnSubConfigConstants.MAX_CORPORATE_GROUPS_LARGE_DISPATCH, mapServerIds);
                break;

            case KnSubConfigConstants.PV_28:
                knLogger.debug(methodName, "PV Version 28.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG, mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W, mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S, mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES, mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS, mapServerIds);

                //PV23
                mapVersion.put(KnSubConfigConstants.MAX_VIDEO_SESSIONS, mapServerIds);

                //PV24
                mapVersion.put(KnSubConfigConstants.M_MEM_USR_REGRP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_USR_REGRPS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_SERV_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_DEFAULT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MIN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MAX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_LIST, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_BTF_PER_OWNER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_GRP_REGRPS, mapServerIds);

                //PV25
                mapVersion.put(KnSubConfigConstants.ALTITUDE_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.VERTICALACCURACY_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_MISSING_TIMER, mapServerIds);

                //PV27
                mapVersion.put(KnSubConfigConstants.MAX_CORPORATE_GROUPS_LARGE_DISPATCH, mapServerIds);

            case KnSubConfigConstants.PV_29:
                knLogger.debug(methodName, "PV Version 29.x");
                mapVersion = getCommonKeys(mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PRIMARY_PROXY_ROUTE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FLOOR_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS_TON, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_DOWN_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_UP_TIMER_START_VAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LTE_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER3, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER4, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER5, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SESSION_RECOVERY_TIMER6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WF_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_KA_PACKET_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_WP_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WP_G_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ST_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_CG_BM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_PG_AG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.S_N_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_GP_U, mapServerIds);
                //changes 7.7.1
                mapVersion.put(KnSubConfigConstants.ODL_FREQ1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_FREQ2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR1, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ODL_DUR2, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_CON_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.T_FLT_SNAP_ODL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_ODL_REQ, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TCP_KTMC_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SSRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TP_T_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_PRT_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LG_CLIENTURI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.LogServerWSContext, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GPPR_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_PP_R, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CRU_V6, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_CG_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.XCAP_ROOT_URI_WIFI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_SN_CHG, mapServerIds);

                mapVersion.put(KnSubConfigConstants.MSCL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.UPRIO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.AMRFP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SUI_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_W, mapServerIds);
                //8.0
                mapVersion.put(KnSubConfigConstants.P_WS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.TR_ICE, mapServerIds);
                //8.1
                mapVersion.put(KnSubConfigConstants.DISP_LIST_RR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_WS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WS_CAE_T, mapServerIds);
                //9
                mapVersion.put(KnSubConfigConstants.NEG_C_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CLIENTCAP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IPA__A_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.C_T_L_INL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_SC_L_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.RAD_CH_L_S, mapServerIds);

                //8.1.2 PV - 10, couch-sync-mobile
                mapVersion.put(KnSubConfigConstants.M_T_MSG_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MM_MSG_S_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_DR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_RC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.P2M_FT_GT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_UDP_MSG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GF_FN_MU, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ON_LOC_U_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_R_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CB_B_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_A_M, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_HB_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_VM_FBL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MAP_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_G_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.G_API_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.SGW_L_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_S_RP_I, mapServerIds);
                mapVersion.put(KnSubConfigConstants.L_E_I, mapServerIds);

                //8.3 PV - 11.0
                mapVersion.put(KnSubConfigConstants.C_R_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_GRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PTTR_NGRP_HT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SUBS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_CORP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.DRX_C_SYSTEM, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_PW_T, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_MR, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CQI_DISP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL, mapServerIds);

                //PV 12
                mapVersion.put(KnSubConfigConstants.IDEN_INTEROP, mapServerIds);

                //PV 13
                mapVersion.put(KnSubConfigConstants.M_AB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_G, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_LAB_U, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_TG_CH, mapServerIds);
                mapVersion.put(KnSubConfigConstants.N_CH_ZN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_N, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_P_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_ZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.QPPPackId_NZERO, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ABDG_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KUID_PREFIX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.R_E_A_IND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.E_TG_S_MODE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_CNT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_LEN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SM_DST, mapServerIds);

                //pv 14
                mapVersion.put(KnSubConfigConstants.M_DY_SS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_DD_SS, mapServerIds);

                //pv 16
                mapVersion.put(KnSubConfigConstants.ESRI_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_C_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_G_W_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_CL_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.ESRI_SEC_KEY, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_F_M_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_IDLE_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.V_M_FLR_HLD_TIME, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IS_PDS_E, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_C_L, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PDS_M_P_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.FDS_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SDS_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_A_FL_S, mapServerIds);
                mapVersion.put(KnSubConfigConstants.D_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_FL_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_MSG_TTL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.OSM_L_E_I, mapServerIds);

                //pv 17
                mapVersion.put(KnSubConfigConstants.A_CRTE_PC, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_PG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.A_CRTE_AG, mapServerIds);

                //pv 19
                mapVersion.put(KnSubConfigConstants.CSK_UPLOAD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_VALIDITY_IOS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_OL_I, mapServerIds);

                //pv 20
                mapVersion.put(KnSubConfigConstants.USR_KEY_MAT_OL_P, mapServerIds);
                mapVersion.put(KnSubConfigConstants.CSK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.PCK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMK_SKEW, mapServerIds);
                mapVersion.put(KnSubConfigConstants.STR_ENCRYPT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MDSI_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_URI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_C, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MSGSTORE_URI_W, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_SDS_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SIMUL_FD_TXNS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_SRCH_ENTRIES, mapServerIds);

                //PV 21
                mapVersion.put(KnSubConfigConstants.RECORDING_STATUS, mapServerIds);

                //PV23
                mapVersion.put(KnSubConfigConstants.MAX_VIDEO_SESSIONS, mapServerIds);

                //PV24
                mapVersion.put(KnSubConfigConstants.M_MEM_USR_REGRP, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_USR_REGRPS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.GMS_SERV_ID, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_DEFAULT, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MIN, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_DURATION_MAX, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_LIST, mapServerIds);
                mapVersion.put(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_BTF_PER_OWNER, mapServerIds);
                mapVersion.put(KnSubConfigConstants.M_GRP_REGRPS, mapServerIds);

                //PV25
                mapVersion.put(KnSubConfigConstants.ALTITUDE_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.VERTICALACCURACY_FLAG, mapServerIds);
                mapVersion.put(KnSubConfigConstants.MEDIA_MISSING_TIMER, mapServerIds);

                //PV27
                mapVersion.put(KnSubConfigConstants.MAX_CORPORATE_GROUPS_LARGE_DISPATCH, mapServerIds);

                //PV 29
                mapVersion.put(KnSubConfigConstants.KPI_REP_AUD_INTERVAL, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KPI_REP_UP_RAND, mapServerIds);
                mapVersion.put(KnSubConfigConstants.KPI_REP_MAX_CHUNK_SIZE, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_MULTI, mapServerIds);
                mapVersion.put(KnSubConfigConstants.IP_V_PREF_MULTI, mapServerIds);
                //PV29+
                mapVersion.put(KnSubConfigConstants.MULTICAST_KA_INTERVAL, mapServerIds);

        }
        return mapVersion;
    }

    public Map<String, Map<String, String>> getCommonKeys(Map<String, String> mapServerIds) {
        Map<String, Map<String, String>> mapCommonVersion = new HashMap<String, Map<String, String>>();
        mapCommonVersion.put(KnSubConfigConstants.MDN, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.XCAP_ROOT_URI, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MAX_PUBLIC_CONTACTS, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MAX_CORPORATE_CONTACTS, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MAX_PUBLIC_GROUPS, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MAX_CORPORATE_GROUPS, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MAX_MEMBERS_PER_PUBLIC_GROUP, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MAX_ADHOC_GROUP_SIZE, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MAX_TALKBURST_DURATION, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.CONFERENCE_FACTORY_URI, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.TBCP_REQUEST_TIMER, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.TBCP_RELEASE_TIMER, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MEDIA_END_TIMER, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MEDIA_IDLE_TIMER, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MEDIA_INTRABURST_INTERVAL, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.NUM_KA_MEDIA_PACKETS, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.MEDIA_PAYLOAD_LENGTH, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.LOCATION_DEBOUNCING_TIMER, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.CONFERENCE_URI_TEMPLATE, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.NUM_OF_RETRIES, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.NUMBER_OF_TBCP_RETRIES, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.PRESENCE_PUBLISH_THROTTLE_TIMER, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.OCTET_SIZE, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.INSTA_POC, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.NUM_OF_BURST_PER_TRIGGER, mapServerIds);
        mapCommonVersion.put(KnSubConfigConstants.TU_SMS_ADDRESS, mapServerIds);
        return mapCommonVersion;

    }
}
