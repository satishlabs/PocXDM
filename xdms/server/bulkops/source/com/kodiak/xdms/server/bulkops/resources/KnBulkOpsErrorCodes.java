package com.kodiak.xdms.server.bulkops.resources;

import com.kodiak.xdms.server.common.resources.KnErrorCodes;

public class KnBulkOpsErrorCodes {
    /**
     * Initializer class.
     * Error Code series 13301 - 13350
     */
    public static final class Initializer {
        public static final String BULK_PROV_INIT_FAILED = "BULKOPSLIB.Initializer.KNEC-SP13301";
    }

    /**
     * BO Entity class
     * Error Code series 13351 - 13800
     */
    public static final class BOEntity extends KnErrorCodes.BOEntity {
        public static final String XDMS_PTT_ID_NOT_FOUND = "BOENTITY.KNEC-SP13351";
        public static final String CLIENT_TYPE_DISABLED="BOEntity.KNEC-SP13352";
        public static final String MAX_SUBS_LIMIT_REACHED_FOR_POC = "BOENTITY.KNEC-SP13353";
//        public static final String EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS = "BOENTITY.KNEC-SP13354";
        public static final String INVALID_SUBSCRIPTION_TYPE = "BOENTITY.KNEC-SP13355";
        public static final String POC_DONOR_RADIO_DISABLED = "BOENTITY.KNEC-SP13356";
        public static final String THIRD_PARTY_POC_CLIENT_DISABLED = "BOENTITY.KNEC-SP13357";
        public static final String INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS = "BOENTITY.KNEC-SP13358";
        public static final String MAX_SUBS_LIMIT_REACHED_FOR_LICENSE = "BOENTITY.KNEC-SP13359";
        public static final String BULK_SUBSCRIBER_VALIDATION_FAILED = "BOENTITY.KNEC-SP13360";
        public static final String ALL_SUBSCRIBERS_VALIDATION_FAILED = "BOENTITY.KNEC-SP13361";
        public static final String MDN_ALREADY_EXISTS_IN_PAM_ACCINFO = "BOENTITY.KNEC-SP13362";
        public static final String MDN_ALREADY_EXISTS_AS_ALIAS = "BOENTITY.KNEC-SP13363";
        public static final String MDN_ALREADY_EXISTS_AS_EXT_SUBS = "BOENTITY.KNEC-SP13364";
        public static final String ERROR_CODE_INVALID_DTO_PASSED = "BOENTITY.KNEC-SP13365";
        public static final String INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS = "BOENTITY.KNEC-SP13366";
        public static final String MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO="BOENTITY.KNEC-SP13367";
        public static final String MISMATCH_IN_SUBS_DOC_ETAG = "BOENTITY.KNEC-SP13368";
//        public static final String POC_HOME_NOT_CONFIGURED = "BOENTITY.KNEC-SP13369";
        public static final String SUBS_PARTITION_CONFIG_NOT_FOUND = "BOENTITY.KNEC-SP13370";
        public static final String INVALID_HIERARCHY_REQUEST = "BOENTITY.KNEC-SP13371";

        public static final String POC_SERVER_MAP_NOT_FOUND = "BOENTITY.KNEC-SP13372";
        public static final String ACTIVE_FS2_GENERATION_FAILED = "BOENTITY.KNEC-SP13373";
        public static final String MDN_PRESENT_IN_KUIDPPOOL="BOEntity.KNEC-SP13374";
        public static final String MDN_ALREADY_EXISTS_AS_MCSID="BOEntity.KNEC-SP13375";
        public static final String BULK_DEVICE_CREATION_FAILED="BOEntity.KNEC-SP13376";
        public static final String BULK_NNI_PROFILE_CREATION_FAILED="BOEntity.KNEC-SP13377";
        public static final String APN_INFO_NOT_FOUND = "BOEntity.KNEC-SP13378";
        public static final String ACCOUNT_ID_CHANGE_NOT_ALLOWED = "BOENTITY.KNEC-SP13379";
        public static final String EXTCORPID_MISMATCH = "BOENTITY.KNEC-SP13380";
        public static final String CORP_PROFILE_NOT_FOUND = "BOENTITY.KNEC-SP13381";
        public static final String RESTRICTED_CLIENT_TYPE = "BOENTITY.KNEC-SP13382";
        public static final String NO_CHANGE_IN_PROFILE = "BOENTITY.KNEC-SP13383";
        public static final String PKG_NOT_FOUND = "BOENTITY.KNEC-SP13384";
        public static final String TIERPKG_ASSIGNED = "BOENTITY.KNEC-SP13385";
        public static final String QPPPKG_ASSIGNED = "BOENTITY.KNEC-SP13386";
        public static final String ACCOUNTID_VALIDATION_FAILED= "BOENTITY.KNEC-SP13389";
        public static final String MAX_SUBS_LIMIT_REACHED_FOR_CORP= "BOENTITY.KNEC-SP13390";
        public static final String SUBSCRIBER_IN_PRE_PROVISIONED_STATE = "BOENTITY.KNEC-SP13391";
        public static final String MCSID_SUBSCRIPTION_TYPE_MISMATCH = "BOENTITY.KNEC-SP13392";
        public static final String MCSID_ACCOUNT_ID_MISMATCH = "BOENTITY.KNEC-SP13394";
        public static final String MCSID_CLIENT_TYPE_MISMATCH = "BOENTITY.KNEC-SP13395";
        public static final String BULK_SUBSCRIBER_NOT_FOUND = "BOENTITY.KNEC-SP13396";
    }

    /**
     * Validator Class
     * Error Code series 13801 - 14100
     */
    public static final class Validator extends KnErrorCodes.Validator {
        public static final String ERROR_CODE_INVALID_OPERATION = "Validator.KNEC-BOPS13801";
        public static final String ERROR_CODE_INTERNAL_ERROR = "Validator.KNEC-BOPS13802";
        public static final String ERROR_CODE_PARTIAL_SUCCESS = "Validator.KNEC-BOPS13803";
    }

    /**
     * DAO Class
     * Error Code series 14101 - 14300
     */
    public static final class DAO extends KnErrorCodes.DAO {
        public static final String BULK_CONTACTLIST_INSERT_FAILED = "DAO.KNEC-BOPS14101";
    }


    /**
     * Authorizer class.
     * Error Code series 14301 - 14600
     */
    public static final class Authorizer extends KnErrorCodes.Authorizer {
    }
}
