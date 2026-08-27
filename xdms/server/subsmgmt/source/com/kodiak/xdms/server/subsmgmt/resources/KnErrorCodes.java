/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnErrorCodes.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/25/10       7.0
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
package com.kodiak.xdms.server.subsmgmt.resources;

public class KnErrorCodes {

    /**
     * Initializer class.
     * Error Code series 12001 - 12050
     */
    public static final class Initializer {
        public static final String PROV_INIT_FAILED = "PROVLIB.Initializer.KNEC-SP12001";
    }

    /**
     * BO Entity class
     * Error Code series 12051 - 12500
     */

    public static final class BOEntity extends com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity {
        public static final String SUBSCRIBER_ALREADY_EXISTS = "BOENTITY.KNEC-SP12051";
        public static final String POC_SERVER_MAP_NOT_FOUND = "BOENTITY.KNEC-SP12052";
        public static final String PRESENCE_SERVER_MAP_NOT_FOUND = "BOENTITY.KNEC-SP12053";
        public static final String CORP_PROFILE_ALREADY_EXISTS = "BOENTITY.KNEC-SP12054";
        public static final String XDMS_PTT_ID_NOT_FOUND = "BOENTITY.KNEC-SP12055";
        public static final String ROAMING_CLUSTER_NOT_FOUND = "BOENTITY.KNEC-SP12056";
        public static final String SUBSCRIBER_INFO_NOT_FOUND = "BOENTITY.KNEC-SP12057";
        public static final String SUBSCRIBER_ROAMING_INFO_NOT_FOUND = "BOENTITY.KNEC-SP12058";
        public static final String INVALID_SUBSCRIPTION_TYPE = "BOENTITY.KNEC-SP12059";
        //        public static final String DISABLE_CORP_TYPE_NOT_ALLOWED = "BOENTITY.KNEC-SP12060";
//        public static final String PUB_TO_CORP_NOT_ALLOWED = "BOENTITY.KNEC-SP12061";
//        public static final String CORP_PUB_TO_PUB_NOT_ALLOWED = "BOENTITY.KNEC-SP12062";
        //12063 not in use can be updated for later use cases
        public static final String EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS = "BOENTITY.KNEC-SP12063";
        public static final String UPDATE_PAIRING_IND_NOT_ALLOWED = "BOENTITY.KNEC-SP12064";
        public static final String SUBS_ALREADY_ACTIVATED = "BOENTITY.KNEC-SP12065";
        public static final String SUBS_ALREADY_DEACTIVATED = "BOENTITY.KNEC-SP12066";
        public static final String PR_SVC_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12067";
        public static final String POC_SVC_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12068";
        public static final String POC_REGISTRAR_SVC_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12069";
        public static final String DIAL_PLAN_INFO_NOT_FOUND = "BOENTITY.KNEC-SP12070";
        public static final String XDMS_DOC_SUB_PRX_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12071";
        public static final String INSTA_POC_SVC_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12072";
        public static final String NO_CHG_IN_PROFILE = "BOENTITY.KNEC-SP12073";
        public static final String MAX_SUBS_LIMIT_REACHED_FOR_LICENSE = "BOENTITY.KNEC-SP12074";
        public static final String MISMATCH_IN_SUBS_DOC_ETAG = "BOENTITY.KNEC-SP12075";
        public static final String NO_CHG_IN_ETAG = "BOENTITY.KNEC-SP12076";
        public static final String SUBS_IS_NOT_DEACTIVATED = "BOENTITY.KNEC-SP12077";
        public static final String SUBS_IS_NOT_ACTIVATED = "BOENTITY.KNEC-SP12078";
        public static final String SUBS_IS_DEACTIVATED = "BOENTITY.KNEC-SP12079";
        public static final String MISMATCH_IN_PAIRING_IND = "BOENTITY.KNEC-SP12080";
        public static final String LOCATION_SERVICE_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12081";
        public static final String INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS = "BOENTITY.KNEC-SP12082";
        public static final String UNAUTHORIZED_SUBS_CLIENT_TYPE = "BOENTITY.KNEC-SP12083";
        public static final String PAIRING_IND_NOT_ALLOWED_POC_DONOR_RADIO = "BOENTITY.KNEC-SP12084";
        public static final String POC_DONOR_RADIO_DISABLED = "BOENTITY.KNEC-SP12085";
        public static final String UNSUPPORTED_DEVICE = "BOENTITY.KNEC-SP12086";
        public static final String EXT_CORP_ID_EXISTING = "BOENTITY.KNEC-SP12087";
        public static final String SW_PKG_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12088";
        public static final String FEATURE_ACCESS_INFO_NOT_FOUND = "BOENTITY.KNEC-SP12089";
        public static final String SIP_PROXY_SVC_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12090";
        public static final String PAMACCOUNT_ALREADY_EXISTS = "BOENTITY.KNEC-SP12091";
        public static final String PAMACCOUNT_NOT_FOUND = "BOENTITY.KNEC-SP12092";
        public static final String PAM_SVC_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12093";
        public static final String MAX_SUBS_LIMIT_REACHED_FOR_PAMACCOUNT = "BOENTITY.KNEC-SP12094";
        public static final String MAX_SUBS_LIMIT_REACHED_FOR_POC = "BOENTITY.KNEC-SP12095";
        public static final String PAMACCOUNT_DELETE_IN_PROGRESS = "BOENTITY.KNEC-SP12096";
        public static final String SUBS_PARTITION_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12097";
        public static final String INVALID_RATE_PLAN_REQUEST = "BOENTITY.KNEC-SP12098";
        public static final String SUBSCRIBER_ALREADY_EXISTS_AS_EXT_SUBS = "BOENTITY.KNEC-SP12099";
        public static final String SUBSCRIBER_NOT_EXISTS_AS_EXT_SUBS = "BOENTITY.KNEC-SP12100";
        public static final String APN_INFO_NOT_FOUND = "BOENTITY.KNEC-SP12101";
        public static final String BLACKLISTED_DEVICE = "BOENTITY.KNEC-SP12102";
        public static final String THIRD_PARTY_POC_CLIENT_DISABLED = "BOENTITY.KNEC-SP12103";
        public static final String BANID_IS_IN_PROGRESS = "BOENTITY.KNEC-SP12104";
        public static final String BAN_ID_DOESNOT_EXIST= "BOEntity.KNEC-SP12105";
        public static final String BAN_ID_THROTTLE_LIMIT_REACHED="BOEntity.KNEC-SP12106";
        public static final String MDN_PRESENT_IN_KUIDPPOOL="BOEntity.KNEC-SP12107";
        public static final String MDN_PRESENT_IN_PAMACCOUNTINFO="BOEntity.KNEC-SP12108";
        public static final String MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO="BOEntity.KNEC-SP12109";

        public static final String EXT_CORP_ID_DOES_NOT_EXIST = "BOEntity.KNEC-SP12110";
        public static final String NO_CHANGE_IN_AUTO_PAIR_INDICATOR = "BOEntity.KNEC-SP12111";
        public static final String CORP_AUTO_PAIR_LIMIT_REACHED = "BOEntity.KNEC-SP12112";
        public static final String MANDATORY_PARAMETERS_ARE_MISSING = "BOEntity.KNEC-SP12113";
        //PAM error codes
        public static final String CORPID_CHANGE_NOT_ALLOWED = "BOEntity.KNEC-CM10516";

        //Tpams Error codes
        public static final String NO_FREE_PSEUDOMDN_IN_POOL="BOEntity.KNEC-SP12115";
        public static final String BILLINGMDN_IS_SUSPEND="BOEntity.KNEC-SP12116";
        public static final String MDN_IS_IN_NOT_USED_STATE="BOEntity.KNEC-SP12118";
        public static final String MDN_ACTIVATION_CODE_NOT_ASSOCIATED="BOEntity.KNEC-SP12120";

        //client type disabled error code
        public static final String CLIENT_TYPE_DISABLED="BOEntity.KNEC-SP12121";

        public static final String VOCODERID_MISMATCH = "BOEntity.KNEC-SP12123";

        public static final String PTTRADIOCLIENTBIT_DISABLED = "BOENTITY.KNEC-SP12124";
        public static final String INVALID_CLIENT = "BOENTITY.KNEC-SP12125";
        public static final String SUBSCRIBER_NOT_FOUND_FOR_FS = "BOENTITY.KNEC-SP12126";

        public static final String INVALID_REQUEST_DATA = "Validator.KNEC-SP40506";
        public static final String TIERPKG_ASSIGNED = "BOENTITY.KNEC-SP12127";
        public static final String DUPLICATE_UFMI = "BOENTITY.KNEC-SP12128";
        public static final String IDEN_INTEROP_DISABLED = "BOENTITY.KNEC-SP12129";
        public static final String PKG_NOT_FOUND = "BOENTITY.KNEC-SP12130";
        public static final String MDN_PRESENT_AS_ALIASMDN = "BOENTITY.KNEC-SP12131";
        public static final String QPPPKG_ASSIGNED = "BOENTITY.KNEC-SP12132";
        public static final String TP_VENDOR_NOT_EXIST = "BOENTITY.KNEC-SP12133";
        public static final String TP_USER_ALREADY_EXIST = "BOENTITY.KNEC-SP12134";
        public static final String TP_USER_NOT_EXIST = "BOENTITY.KNEC-SP12135";
        public static final String TP_USERID_VENDORID_NOT_ASSOSIATED = "BOENTITY.KNEC-SP12136";
        public static final String TP_USERID_MDN_NOT_ASSOSIATED = "BOENTITY.KNEC-SP12137";
        public static final String TP_MDN_NOT_EXIST = "BOENTITY.KNEC-SP12138";
        public static final String TP_MDN_VENDORID_NOT_ASSOSIATED = "BOENTITY.KNEC-SP12139";
        public static final String USER_ID_ALREADY_EXISTS = "BOENTITY.KNEC-SP12140";
        public static final String MCSIDS_ALREADY_EXISTS = "BOENTITY.KNEC-SP12141";
        public static final String INVALID_MCSIDS = "BOENTITY.KNEC-SP12142";
        public static final String LICENSE_SUBS_NOT_FOUND = "BOENTITY.KNEC-SP12143";
        public static final String MCXIDS_NOT_ALLOWED_FOR_KODIAK_CLIENTS = "BOENTITY.KNEC-SP12144";
        public static final String SWITCH_NOT_ALLOWED= "BOENTITY.KNEC-SP24802";
        public static final String DEVICE_ALREADY_PRESENT = "BOENTITY.KNEC-SP12145";
        public static final String DEVICE_PROFILE_NOT_FOUND = "BOENTITY.KNEC-SP12146";
        public static final String INVALID_MCDEVICE = "BOENTITY.KNEC-SP12147";
        public static final String ACCOUNT_ID_CHANGE_NOT_ALLOWED="BOENTITY.KNEC-SP12148";
        public static final String SUBSCRIPTION_TYPE_CHANGE_NOT_ALLOWED="BOENTITY.KNEC-SP12149";
        public static final String NOT_STANDALONECAMERA_CLIENT = "BOENTITY.KNEC-SP12150";
        public static final String DUPLICATE_ALIAS_INFO_NOT_ALLOWED="BOENTITY.KNEC-SP12151";
        public static final String REMOVE_ALIAS_INFO_NOT_EXIST="BOENTITY.KNEC-SP12152";
        public static final String SUBSCRIBER_ALREADY_HAS_DEVICE = "BOENTITY.KNEC-SP12153";
        public static final String SUBSCRIBER_AND_DEVICE_NOT_PART_OF_SAME_CORPORATE = "BOENTITY.KNEC-SP12154";
        public static final String DEVICE_ALREADY_TAGGED = "BOENTITY.KNEC-SP12155";
        public static final String DEVICE_NOT_PRESENT = "BOENTITY.KNEC-SP12156";
        public static final String INVALID_CORP_PROFILE = "BOENTITY.KNEC-CM10527";
        public static final String EXTERNAL_GATEWAY_ID_NOT_EXIST = "BOEntity.KNEC-CM10559";
        public static final String LOCATION_ENABLE_TO_DISABLE_NOT_ALLOWED = "BOEntity.KNEC-CM10561";
        public static final String CLIENT_IS_IN_EMER = "BOENTITY.KNEC-SP12157";

    }

    /**
     * Validator Class
     * Error Code series 12501 - 12800
     */
    public static final class Validator extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Validator {

        public static final String INVALID_PUBLIC_SUBSCRIPTION_TYPE = "Validator.KNEC-SP12501";
        public static final String INVALID_CORP_SUBSCRIPTION_TYPE = "Validator.KNEC-SP12502";
        public static final String INVALID_PAY_TYPE = "Validator.KNEC-SP12503";
        public static final String MAX_SUBS_LIMIT_REACHED_FOR_CORP = "Validator.KNEC-SP12504";
        public static final String PAIRING_IND_NOT_ALLOWED = "Validator.KNEC-SP12505";
        public static final String INVALID_ROAMING_ID = "Validator.KNEC-SP12506";
        public static final String INVALID_SERVICE_AUTH_STATUS = "Validator.KNEC-SP12507";
        public static final String SUBSCRIBER_IS_NOT_ACTIVATED = "Validator.KNEC-SP12508";
        public static final String SUBSCRIBER_IS_IN_DEACTIVATED_STATE = "Validator.KNEC-SP12509";
        //        public static final String UPDATE_EXTCORPID_NOT_ALLOWED = "Validator.KNEC-SP12510";
        public static final String SUBSCRIBER_IS_IN_DEACTIVATED_STATE_AUTH_USER = "Validator.KNEC-SP12510";
        public static final String INVALID_SUBS_CLIENT_TYPE = "Validator.KNEC-SP12511";
        public static final String INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS = "BOENTITY.KNEC-SP12119";


        //PAM Error codes
        public static final String INVALID_REQUEST = "Validator.KNEC-SP12512";
        public static final String CHANGE_MDN_NOT_ALLOWED_FOR_LMR = "Validator.KNEC-SP12513";
        public static final String  INVALID_PROV_FS="Validator.KNEC-SP12514";
        public static final String INVALID_LICENSE_TYPE = "Validator.KNEC-SP12515";
        public static final String INVALID_FIRST_NET_INDICATOR = "Validator.KNEC-SP12516";
        public static final String INVALID_MCS_COMPLIANCE = "Validator.KNEC-SP12517";
        public static final String INVALID_TOKEN_MCPTTID = "Validator.KNEC-SP12518";
        public static final String CHANGE_MDN_NOT_ALLOWED_FOR_4RE_CAMERA = "Validator.KNEC-SP12519";


    }

    /**
     * DAO Class
     * Error Code series 12801 - 13000
     */

    public static final class DAO extends com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO {
    }

    /**
     * Authorizer class.
     * Error Code series 13000 - 13300
     */
    public static final class Authorizer extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Authorizer {
    }
}
