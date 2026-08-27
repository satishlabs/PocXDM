/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name: KnErrorCodes.java
 * Subsystem:
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-03-2007 6.0
 *
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 **************************************************************************/
// ----------------------------------------------------------------
// The following code has been auto-generated :
// ----------------------------------------------------------------
package com.kodiak.xdms.server.common.resources;

/**
 * KnErrorCodes class.
 */
public class KnErrorCodes {
    public static final String MODULE_NAME = "ERRORCODES";

    /**
     * common error codes series among all modules 10001 - 10050
     * Last Used: 10005
     */


    /**
     * ConfigManager class.
     * Error code series 10051 - 10100
     */
    public static class ConfigManager {
        public static final String INTERNAL_ERROR = "ConfigManager.KNEC-CM10001";
        public static final String CONFIG_ERROR = "ConfigManager.KNEC-CM10002";
    }

    /**
     * ModuleLoader class.
     * Error code series 10151 - 10200
     * Last used:
     */
    public static class ModuleLoader {
        public static final String INTERNAL_ERROR = "ModuleLoader.KNEC-CM10001";
        public static final String CONFIG_ERROR = "ModuleLoader.KNEC-CM10002";
    }

    /**
     * ConfigInitializer class.
     * Error code series 10201 - 10250
     * Last used:
     */
    public static class ConfigInitializer {
        public static final String INTERNAL_ERROR = "ConfigInitializer.KNEC-CM10001";
        public static final String CONFIG_ERROR = "ConfigInitializer.KNEC-CM10002";
    }

    /**
     * CacheManager class.
     * Error code series 10251 - 10300
     * Last used: 10252
     */
    public static class CacheManager {
        public static final String INTERNAL_ERROR = "CacheManager.KNEC-CM10001";
        public static final String CONFIG_ERROR = "CacheManager.KNEC-CM10002";
        public static final String EMPTY_KEYS = "CacheManager.KNEC-CM10251";
        public static final String INVALID_MODULE = "CacheManager.KNEC-CM10252";
    }

    /**
     * Validator class.
     * Error Code series 10301 - 10350
     * Last used: 10304
     */
    public static class Validator {
        public static final String INTERNAL_ERROR = "Validator.KNEC-CM10001";
        public static final String CONFIG_ERROR = "Validator.KNEC-CM10002";
        public static final String CONS_BO_VAL_ERRORS = "Validator.KNEC-CM10301";
        public static final String CONS_DATA_VAL_EXPRN_ERRORS = "Validator.KNEC-CM10302";
        public static final String CONS_BO_VAL_EXPRN_ERRORS = "Validator.KNEC-CM10303";
        public static final String CONS_DATA_VAL_ERRORS = "Validator.KNEC-CM10304";
        public static final String MDN_NOT_OF_CORPORATION = "Validator.KNEC-CM10305";
        public static final String MDN_NOT_OF_VENDORID = "Validator.KNEC-CM10306";
        public static final String OWNERMDN_NOT_CORP_PUB_SUBS = "Validator.KNEC-CM10307";
        public static final String CONTACT_MDN_PUB_SUBS = "Validator.KNEC-CM10308";
        public static final String CONTACT_MDN_CLIENT_TYPE_INVALID = "Validator.KNEC-CM10309";
        public static final String MODIFYING_CONTACT_NOT_EXIST = "Validator.KNEC-CM10310";
        public static final String ADDED_CONTACT_ALRREADY_EXIST = "Validator.KNEC-CM10311";
        public static final String OWNER_MDN_NOT_TP_CLIENT = "Validator.KNEC-CM10312";
        public static final String MODIFING_GROUP_MEMBER_NOT_EXIST = "Validator.KNEC-CM10313";
        public static final String ADDED_GROUP_MEMBER_ALREADY_EXIST = "Validator.KNEC-CM10314";
        public static final String DYNAMIC_API_SERVICE_FLAG_DISABLED = "Validator.KNEC-CM10315";
    }

    /**
     * Authenticator class
     * Error code series 10351 - 10400
     * Last used: 10354
     */
    public static class Authenticator {
        public static final String INTERNAL_ERROR = "Authenticator.KNEC-CM10001";
        public static final String CONFIG_ERROR = "Authenticator.KNEC-CM10002";
        public static final String WRONG_PASSWORD = "Authenticator.KNEC-CM10005";
        public static final String NOT_ALLOWED = "Authenticator.KNEC-CM10351";
        public static final String SESSION_TIMEOUT = "Authenticator.KNEC-CM10352";
        public static final String AUTHENTICATION_FAILED = "Authenticator.KNEC-CM10353";
    }

    /**
     * Authorizer class.
     * Error code series 10401 - 10450
     * Last used: 10401
     */
    public static class Authorizer {
        public static final String INTERNAL_ERROR = "Authorizer.KNEC-CM10001";
        public static final String CONFIG_ERROR = "Authorizer.KNEC-CM10002";
        public static final String INVALID_PROFILE = "Authorizer.KNEC-CM10003";
        public static final String INVALID_SERVICE_STATUS = "Authorizer.KNEC-CM10401";
        public static final String INVALID_SUBSCRIPTION_TYPE = "Authorizer.KNEC-CM10402";
    }


    /**
     * DAO class
     * Error code series 10451 - 10500
     * Last used: 10467
     */
    public static class DAO {
        public static final String INTERNAL_ERROR = "DAO.KNEC-CM10001";
        public static final String PTT_SERVER_NOT_REACHABLE = "DAO.KNEC-CM10004";
        public static final String SERVER_BUSY = "DAO.KNEC-CM10451";
        public static final String ROW_NOT_FOUND = "DAO.KNEC-CM10452";
        public static final String CONNECTION_FAILED = "DAO.KNEC-CM10453";
        public static final String SERVER_ERROR = "DAO.KNEC-CM10454";
        public static final String SQL_QUERY_ERROR = "DAO.KNEC-CM10455";
        public static final String ROW_ALREADY_EXISTS = "DAO.KNEC-CM10456";
        public static final String ROW_ALREADY_DELETED = "DAO.KNEC-CM10457";
        public static final String INVALID_FACTORY_TYPE = "DAO.KNEC-CM10458";
        public static final String TXN_START_FAILED = "DAO.KNEC-CM10459";
        public static final String TXN_COMMIT_FAILED = "DAO.KNEC-CM10460";
        public static final String TXN_ROLLBACK_FAILED = "DAO.KNEC-CM10461";
        public static final String TXN_CONNECTION_NOT_AVAILABLE = "DAO.KNEC-CM10462";
        public static final String TXN_NOT_STARTED = "DAO.KNEC-CM10463";
        public static final String COLUMN_WIDTH_EXCEEDED = "DAO.KNEC-CM10464";
        public static final String INVALID_PTTSERVERID = "DAO.KNEC-CM10465";
        public static final String SQL_EXCEPTION = "DAO.KNEC-CM10466";
        public static final String INVALID_INPUT = "DAO.KNEC-CM10467";
        public static final String ROW_ALREADY_EXISTS_GROUP_DOC = "DAO.KNEC-CM10468";
    }

    /**
     * BOEntity class
     * Error code series 10501 - 11000
     * Last used: 10513
     */
    public static class BOEntity {
        public static final String SUBSCRIBER_DOES_NOT_BELONG_TO_CORP = "CORPLIB.BOEntity.KNEC-CP16034";
        public static final String INTERNAL_ERROR = "BOEntity.KNEC-CM10001";
        public static final String INVALID_PROFILE = "BOEntity.KNEC-CM10003";
        public static final String PTT_SERVER_NOT_REACHABLE = "BOEntity.KNEC-CM10004";
        public static final String WRONG_PASSWORD = "BOEEntity.KNEC-CM10005";

        public static final String SUBSC_NOT_REGISTERED = "BOEntity.KNEC-CM10501";
        public static final String INVALID_USER_ID = "BOEntity.KNEC-CM10502";
        public static final String PASSWORD_LOCKED_BY_ADMIN = "BOEntity.KNEC-CM10503";
        public static final String INVALID_OLD_PASSWORD = "BOEntity.KNEC-CM10504";
        public static final String INVALID_NEW_PASSWORD = "BOEntity.KNEC-CM10505";
        public static final String MESSAGE_PROCESSING_FAILED = "BOEntity.KNEC-CM10506";
        public static final String XDMS_SERVICE_CONFIG_NOT_FOUND = "BOEntity.KNEC-CM10507";
        public static final String XDMS_SUBSYSTEM_CONFIG_NOT_FOUND = "BOEntity.KNEC-CM10508";
        public static final String LICENCE_INFO_NOT_FOUND = "BOEntity.KNEC-CM10509";
        public static final String SERVER_CONFIGURATION_FAILURE = "BOEntity.KNEC-CM10510";
        public static final String INVALID_MDN = "BOEntity.KNEC-CM10511";
        public static final String UNSUPPORTED_OPERATION = "BOEntity.KNEC-CM10512";
        public static final String DIAL_PLAN_INFO_NOT_FOUND = "BOEntity.KNEC-CM10513";
        public static final String POC_SUPPORTED_DEVICES_NOT_FOUND = "BOEntity.KNEC-CM10514";
        public static final String POC_BLACKLISTED_DEVICES_NOT_FOUND = "BOEntity.KNEC-CM10519";
        public static final String SUBSCRIBER_IS_DEACTIVATED = "BOEntity.KNEC-CM10515";
        public static final String CORPID_CHANGE_NOT_ALLOWED = "BOEntity.KNEC-CM10516";
        public static final String SUBSCRIBERS_NOT_AVAILABLE = "BOEntity.KNEC-CM10517";
        public static final String UNSUPPORTED_APNNAME  =  "BOEntity.KNEC-CM10518";
        public static final String UNSUPPORTED_VERSION  =  "BOEntity.KNEC-CM10520";
        public static final String POC_UNAUTHORIZED_SUBS_CLIENT_TYPE = "BOENTITY.KNEC-SP12083";
        public static final String POC_SVC_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP12068";

        // For notifying if the bucket urls are not found
        public static final String XDMS_PTX_BUCKET_URLS_NOT_FOUND = "BOEntity.KNEC-CM10525";
        public static final String PSEUDO_NUMBER_NOT_AVAILABLE = "BOEntity.KNEC-CM10527";

        //Hierarchy error code
        public static final String INVALID_HIERARCHY_REQUEST = "BOEntity.KNEC-CM10526";
        public static final String INVALID_CORPORATE_PROFILE = "BOEntity.KNEC-CM10528";
        public static final String INVALID_THIRD_PARTY_ACCOUNT = "BOEntity.KNEC-CM10529";
        
        //Dynamic C&G & ABDG error code
        public static final String DYNAMIC_FLAG_DISABLED = "BOEntity.KNEC-CM10530";
        public static final String ABDG_FLAG_DISABLED = "BOEntity.KNEC-CM10531";
        public static final String VLG_FLAG_DISABLED = "BOEntity.KNEC-CM10533";
        public static final String CBS_NOT_REACHABLE = "BOEntity.KNEC-CM10534";

        public static final String UNAUTHORIZED_ERROR = "BOEntity.KNEC-CM10535";
        public static final String ACTIVE_SUBSCRIBERS_PRESENT = "BOEntity.KNEC-CM10536";

        public static final String CORPORATE_NAME_EXISTS = "BOEntity.KNEC-CM10537";
        public static final String EXTCORP_ACCOUNT_EXISTS = "BOEntity.KNEC-CM10538";
        public static final String EXTCORP_ACCOUNT_NOT_EXISTS = "BOEntity.KNEC-CM10539";
        public static final String MAX_SUBSCRIBER = "BOEntity.KNEC-CM10540";
        public static final String MAX_CORP_LISTS = "BOEntity.KNEC-CM10541";
        public static final String MAX_MEM_PER_CORPLIST = "BOEntity.KNEC-CM10542";
        public static final String MAX_CORP_GROUPS = "BOEntity.KNEC-CM10543";
        public static final String MAX_MEM_PER_CORPGROUP = "BOEntity.KNEC-CM10544";
        public static final String MAX_EXT_SUBS_PER_GROUP = "BOEntity.KNEC-CM10545";
        public static final String MAX_DISPATCHER_GROUPS = "BOEntity.KNEC-CM10546";
        public static final String MAX_MEM_PER_DISPATCHER_GROUP = "BOEntity.KNEC-CM10547";
        //public static final String IS_HIERARCHY_ENABLED="1";
        public static final String DISPATCHER_ENABLED = "BOEntity.KNEC-CM10548";

        public static final String IS_INTER_OP_ENABLED = "BOEntity.KNEC-CM10549";
        public static final String MAX_MEM_PER_BC_GROUP = "BOEntity.KNEC-CM10550";
        public static final String MAX_CHANNEL_ALLOWED = "BOEntity.KNEC-CM10551";
        public static final String MAX_ZONE_ALLOWED = "BOEntity.KNEC-CM10552";
        public static final String MAX_CHALLENS_PER_ZONE = "BOEntity.KNEC-CM10553";
        public static final String MAX_LGR_GROUP = "BOEntity.KNEC-CM10554";
        public static final String MAX_GRP_PROFILE = "BOEntity.KNEC-CM10555";
        public static final String MAX_USER_PROFILE = "BOEntity.KNEC-CM10556";
        public static final String MAX_ASSIGN_PROFILES = "BOEntity.KNEC-CM10557";
        public static final String LARGE_AGENCY_DISPATCH_FEATURE = "BOEntity.KNEC-CM10558";
        public static final String HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION = "BOEntity.KNEC-CM10559";
        public static final String HIERARCHY_HAS_ASSOCIATED_SUBSCRIBERS = "BOEntity.KNEC-CM10560";
        public static final String HIERARCHY_HAS_ASSOCIATED_GROUPS = "BOEntity.KNEC-CM10561";
        public static final String HIERARCHY_EXISTS_IN_THE_CORPORATE = "BOEntity.KNEC-CM10563";
        public static final String MODIFIED_LIST_MISSING = "BOEntity.KNEC-CM10564";
        public static final String GROUP_DELETETION_IS_NOT_ALLOWED_FOR_SHARED_CORPORATE = "BOEntity.KNEC-CM10565";
        public static final String SOURCE_HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION = "BOEntity.KNEC-CM10566";
        public static final String TARGET_HIERARCHY_ID_NOT_BELONGS_TO_THE_CORPORATION = "BOEntity.KNEC-CM10567";
        public static final String TRUST_MATRIX_ENTRY_NOT_FOUND = "BOEntity.KNEC-CM10568";
        public static final String CANNOT_DELETE_TRUST_MATRIX = "BOEntity.KNEC-CM10569";
        public static final String HIERARCHY_HAS_SHARED_GROUP = "BOEntity.KNEC-CM10570";
        public static final String HIERARCHY_HAS_ASSOCIATED_GROUP_PROFILES = "BOEntity.KNEC-CM10571";
        public static final String HIERARCHY_HAS_ASSOCIATED_USER_PROFILES = "BOEntity.KNEC-CM10572";
        public static final String HIERARCHY_HAS_ASSOCIATED_SUBLISTS = "BOEntity.KNEC-CM10573";
        public static final String HIERARCHY_HAS_ASSOCIATED_OSM_LIST_INFO = "BOEntity.KNEC-CM10574";
        public static final String HIERARCHY_HAS_ASSOCIATED_OSM_LIST = "BOEntity.KNEC-CM10575";
    }
}
