package com.kodiak.xdms.server.bulkops.resources;

public class KnBulkOpsConstants {
    public static final String LIBRARY_NAME = "BULKOPSLIB";
    public static final int XDMS_POC_CAPACITY_REACHED_ALARMCODE = 17602;
    public static final int XDMMANAGEDOBJECT_CLASSTYPE = 5;
    public static final String PROVISIONING = "PROVISIONING";
    public static final String PAM = "PAM";
    public static int SUBSCR_DEF_PTTRADIO_ENABLED=1;
    public static final int ENABLED = 1;
    public static final String CLUSTERID_ENV_NAME = "CLUSTERID";
    public static final String DEVICE_SHARING_FEATURE_FLAG = "DEVICE_SHARING_FEATURE_FLAG";
    public static final String POCHOME_AUTOASSIGN_FLAG = "POCHOME_AUTOASSIGN_FLAG";
    public static final String AUTO_DEVICESHARE_WIFI_CC="AUTO_DEVICESHARE_WIFI_CC";
    public static final String CORP_AUTO_PAIRING_SIZE = "CORP_AUTO_PAIRING_SIZE";
    public static final String ENABLE_CORP_AUTO_PAIRING = "ENABLE_CORP_AUTO_PAIRING";
    public static final int AUTO_PAIR_ENABLED = 1;
    public static final Integer USER_LICENSE_TYPE = 1;
    public static final String MSG_SYNC_RESPONSE = "sync-response";
    public static final int WEB_DISPATCH_ENABLED = 1;
    public static final String UPDATE_AUTH_STATUS_SUCCESS = "UPDATE Subscriber Auth Status Success";


    public static final String SUCCESS_CODE = "KNEC-BOPS00000";

    // Corp Anchor POC Home Constants
    public static final String POC_PTT_ID_KEY = "POC_PTT_ID";
    public static final String UPDATE_CORP_HOME = "UPDATE_CORP_HOME";
    public static final String MDN_POC_MAP_KEY = "MDN_POC_MAP";
    public static final int POC_CARD_TYPE = 32;
    public static final int GEO_POC_CARD_TYPE = 33;

    //global pr in poc flag
    public static final String PR_IN_POC_ENABLED = "1";

    //boolean flags
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    public static final String TELURI = "tel:+";

    public static final Integer USER_PROFILE_INDEX=0;
    public static final int IS_DEFAULT_PROFILE = 1;

    public static final String BASE_PKGCODE = "BASE_PKGCODE";
    public static final Integer TIER_PKG_TYPE = 1;
    public static final Integer ADDON_PKG_TYPE = 2;
    public static final String ADD_ACTION = "Add";
    public static final int ADDON_DATA_PKG_TYPE=1;
    public static final int QPP_DATA_PKG_TYPE=2;
    public static final int DEFAULT_QPP_ID=0;
    public static final Integer DEFAULT_PROFILE_ID = 1;
    public static final Integer DEFAULT_DATAPKG_ID = 1;

    public static final String AWARE_CLIENTS = "6|12|13|18";

    public static final Integer USER_LICENSE_TYPE_DISABLED = 0;
    public static final Integer MCSCOMPLIANCE = 1;
    public static final Integer KODIAK_CLIENT = 0;
    public  static  final int MAX_OIDC_PASSWORD_LENGTH = 12;

    // Device related constants
    public static final int IMPLICIT_DEVICE = 1;
    public static final int DEVICESHARED = 1;
    public static final int DEVICE_ACTIVATION_ACTIVATED = 2;

    public static final String ROAM_NA_CLIENT_TYPES = "ROAM_NA_CLIENT_TYPES";
    public static final int DEFAULT_ROAMING_CLUSTER_ID = 1;

    // XDM Table Constants
    public static final int INITIAL_ETAG = 1;
    public static final int TIME_SLOT_TYPE = 0;

    public static final String CREATE_BULK_SUBSCRIBER_SUCCESS = "CREATE BULK SUBSCRIBER SUCCESS";
    public static final String DELETE_BULK_SUBSCRIBER_SUCCESS = "DELETE BULK SUBSCRIBER SUCCESS";
    public static final String MCS_TEMP_PASSWORD_EXPIRY = "MCS_TEMP_PASSWORD_EXPIRY";

    public static enum APP_ID {
        USERMCSCLIENTS("USERMCSCLIENTS"),
        HANDSET_STANDARD("HANDSET_STANDARD"),
        DISPATCHER("DISPATCHER");

        String appId;

        APP_ID(String appId) {
            this.appId = appId;
        }

        public String value() {
            return appId;
        }
    }

    public enum RESPONSE_MESSAGE {
        CREATE_BULK_SUBSCRIBER_SUCCESS("Bulk Subscriber Creation Successful"),
        UPDATE_BULK_SUBSCRIBER_SUCCESS("Bulk Subscriber Update Successful"),
        DELETE_BULK_SUBSCRIBER_SUCCESS("Bulk Subscriber Deletion Successful"),
        UPDATE_BULK_AUTH_STATUS_SUCCESS("Bulk Subscriber Auth Status Update Successful"),

        CREATE_BULK_SUBSCRIBER_FAILED("Bulk Subscriber Creation Failed"),
        UPDATE_BULK_SUBSCRIBER_FAILED("Bulk Subscriber Update Failed"),
        DELETE_BULK_SUBSCRIBER_FAILED("Bulk Subscriber Deletion Failed"),
        UPDATE_BULK_AUTH_STATUS_FAILED("Bulk Subscriber Auth Status Update Failed"),

        CREATE_BULK_SUBSCRIBER_PARTIAL_SUCCESS("Bulk Subscriber Creation Partially Successful"),
        UPDATE_BULK_SUBSCRIBER_PARTIAL_SUCCESS("Bulk Subscriber Update Partially Successful"),
        DELETE_BULK_SUBSCRIBER_PARTIAL_SUCCESS("Bulk Subscriber Deletion Partially Successful"),
        UPDATE_BULK_AUTH_STATUS_PARTIAL_SUCCESS("Bulk Subscriber Auth Status Update Partially Successful");

        String message;
        RESPONSE_MESSAGE(String message) {
            this.message = message;
        }

        public String value() {
            return message;
        }
    }

    public enum BulkOperations {
        CREATEBULKSUBSCRIBER,
        UPDATEBULKSUBSCRIBER,
        UPDATEBULKAUTHSTATUS,
        DELETEBULKSUBSCRIBER
    }

    public static enum DEVICE_STATUS_OP {

        ACTIVATED(1),
        DEACTIVATED(2);

        int deviceStatusOp;

        DEVICE_STATUS_OP(int status) {
            this.deviceStatusOp = status;
        }

        public int value() {
            return deviceStatusOp;
        }
    }

    public static enum DEVICE_TYPE {

        DEFAULT_DEVICE(0),
        RADIO_NEXT_DEVICE(1),
        MC_DEVICE(2),
        TABLET(3),
        DISPATCHER(4),
        MOTOTRBO(5),
        RG170(6),
        RG275(7),
        TLK_100(8),
        TLK_150(9),
        SLN_1000(10),
        EVOLVE(11),
        ION(12);

        int type;

        DEVICE_TYPE(int status) {
            this.type = status;
        }

        public int Value() {
            return type;
        }

    }

    public static enum MCDEVICESHARED_TYPE {
        NOT_SHARED(0),
        SHARED(1);

        int sharedType;

        MCDEVICESHARED_TYPE(int sharedType) {
            this.sharedType = sharedType;
        }

        public int Value() {
            return sharedType;
        }
    }

    public static enum SUBSCRIBERS_CLIENT_TYPE {
        UNKNOWN(0),
        HANDSET(1),
        DESKTOP(2),
        DISPATCH(3),
        POCDONORRADIO(4),
        WIFIONLY(5),
        THIRDPARTYPOCCLIENT(6),
        POC_NNI_Alias_MDN(7),
        POC_NNI_Group_MDN(8),
        // RESERVED(9), reserved for future use
        CROSS_CARRIER_PTT_CLIENT(10),
        PDVCONNECT(11),
        THIRDPARTYDISPATCHERCLIENT(12),
        MOBILE_CLIENT(13),

        //LMR client type changes
        PTTRADIOHANDSETCLIENT(14),
        PTTRADIOCROSSCARRIERCLIENT(15),
        PTTRADIOWIFIONLYCLIENT(16),
        SGMDNPATCH(17),
        DATAGROUPMDN(18),
        STANDALONECAMERA(19);

        int clientType;

        SUBSCRIBERS_CLIENT_TYPE(int clientType) {
            this.clientType = clientType;
        }

        public int value() {
            return clientType;
        }
    }

    public static enum CLIENT_TYPE_CONFIGURATION {
        ENABLED(1),
        DISABLED(0);

        int status;

        CLIENT_TYPE_CONFIGURATION(int type){status = type;}

        public int value(){return status;}

    }

    public static enum HIERARCHY_TYPE {
        HIERARCHY(2),
        NON_HIERARCHY(1);
        int hierarchyType;

        HIERARCHY_TYPE(int hierarchyType) {
            this.hierarchyType = hierarchyType;
        }

        public int value() {
            return this.hierarchyType;
        }

        public static HIERARCHY_TYPE validate(int hierarchy) {
            for (HIERARCHY_TYPE hierarchyType : values()) {
                if (hierarchyType.value() == hierarchy) {
                    return hierarchyType;
                }
            }
            return null;
        }
    }

    public static enum SERVICE_AUTH_STATUS {
        PROVISIONED(0),
        UNUSED(1),
        ACTIVATED(2),
        DEACTIVATED(3),
        DELETION_IN_PROGRESS(4),
        MARKED_FOR_ASYNC_DELETION(10),
        PRE_PROVISIONED(99);

        int serviceAuthStatus;

        SERVICE_AUTH_STATUS(int status) {
            this.serviceAuthStatus = status;
        }

        public int value() {
            return serviceAuthStatus;
        }

        public static SERVICE_AUTH_STATUS fromValue(int value) {
            for (SERVICE_AUTH_STATUS status : SERVICE_AUTH_STATUS.values()) {
                if (status.value() == value) {
                    return status;
                }
            }
            return PROVISIONED;
        }
    }

    public static enum SERVICE_STATUS_AUTHUSER {

        PROVISIONED(0),
        ACTIVATED(2),
        DEACTIVATED(3);

        int serviceStatusAuthUser;
        SERVICE_STATUS_AUTHUSER(int status){
            this.serviceStatusAuthUser = status;
        }
        public int value() {
            return serviceStatusAuthUser;
        }
    }

    public static enum RESPONSE_STATUS {
        SUCCESS(0), FAILURE(1);
        int responseStatus;

        RESPONSE_STATUS(int responseStatus) {
            this.responseStatus = responseStatus;
        }

        public int value() {
            return responseStatus;
        }
    }

    public static enum PUBLIC_SUBSCRIPTION_TYPE {
        NONE(0), PUBLIC(1);

        int publicSubscriptionType;

        PUBLIC_SUBSCRIPTION_TYPE(int type) {
            this.publicSubscriptionType = type;
        }

        public int value() {
            return publicSubscriptionType;
        }
    }

    public static enum CORP_SUBSCRIPTION_TYPE {
        NONE(0), CORPORATE(1);

        int corpSubscriptionType;

        CORP_SUBSCRIPTION_TYPE(int type) {
            this.corpSubscriptionType = type;
        }

        public int value() {
            return corpSubscriptionType;
        }
    }

    public static enum POC_DONOR_RADIO {
        ENABLED(1),
        DISABLED(0);

        int status;

        POC_DONOR_RADIO(int type) {
            status = type;
        }

        public int value() {
            return status;
        }
    }

    public static enum THIRD_PARTY_POC_CLIENT {
        ENABLED(1),
        DISABLED(0);

        int status;

        THIRD_PARTY_POC_CLIENT(int type) {
            status = type;
        }

        public int value() {
            return status;
        }
    }

    public static enum DISPATCH_GROUP_MEMBER {
        NOT_MEMBER(0),
        MEMBER(1);

        int member;

        DISPATCH_GROUP_MEMBER(int grpMember) {
            member = grpMember;
        }

        public int value() {
            return member;
        }
    }

    public static enum VALIDATION_STATUS {
        PASSED(0),
        FAILED(1);

        int status;

        VALIDATION_STATUS(int status) {
            this.status = status;
        }

        public int value() {
            return status;
        }
    }

    public static enum CORP_ACC_ANCHORING {
        DISABLED(0),
        ENABLED(1);

        int status;

        CORP_ACC_ANCHORING(int status) {
            this.status = status;
        }

        public int value() {
            return status;
        }
    }

    public static enum PARTITION_TYPE {
        MDN_BASED(1),
        LOAD_BASED(2);

        int partitionType;

        PARTITION_TYPE(int type) {
            partitionType = type;
        }

        public int value() {
            return partitionType;
        }
    }

    public static enum ALLOW_POC_PROV {
        DONT_ALLOW(0),
        ALLOW_PROV(1),
        ALLOW_ONLY_ANCH(2);

        int allowProv;

        ALLOW_POC_PROV(int type) {
            allowProv = type;
        }

        public int value() {
            return allowProv;
        }
    }

    public static enum XDMCORPFS2_SET {
        LOCATION_ENABLED(1),
        EMERGENCY_CONF_TIMER_FEATURE(2);

        int xdmCorpFS2_Bit;

        XDMCORPFS2_SET(int type) {
            this.xdmCorpFS2_Bit = type;
        }

        public int value() {
            return xdmCorpFS2_Bit;
        }
    }

    public static enum FEATURE_SET {
        INSTAPOC(0),
        MISSEDCALLALERT(1),
        CALLENDEDALERT(2),
        LOCATIONSUBSCRIPTION(3),
        SUPERVISORYOVERRIDE(4),
        BROADCASTGROUPSERVICE(5),
        RESTRICTAVAILABILITY(6),
        SECONDARYPRESENCE(7),
        SESSPARTTOORIG(8),
        SESSPARTTOTERM(9),
        MIDCALLADDPARTBYORIG(10),
        MIDCALLEXPELPARTBYORIG(11),
        MIDCALLADDPARTBYTERM(12),
        MIDCALLEXPELPARTBYTERM(13),
        TUSMS(14),
        XCAPDOCDIFF(15),
        POCOVERWIFI(16),
        HTTPSSUPPORT(17),
        PAGMSGSUPPORT(18),
        RELOGINMSGSUPPORT(19),
        TLKGRPSELSERVER(20),
        PRUPDATEREDUCTION(21),
        JITTERSTATS(22),
        HISTBASEDPRES(23),
        DECOUPPRESCALL(24),
        PERICLIENTLOGS(25),
        ONDEMCLIENTLOGS(26),
        ONDEMLOCATION(27),
        TLKGRPSCANCLIENT(28),
        BATTERYUSAGEOPTIMIZATION(29),
        CLIENTUISTATS(30),
        TLKGRPSELCLIENT(31),
        PUSHTOTEXT(33),
        PUSHTOMULTIMEDIA(34),
        PUSHTOLOCATION(35),
        URGENTMSGORIG(36),
        GROUPMEMLOC(38),
        GEOFENCEFEATURE(39),
        GCMPUSHNOTIFICATION(40),
        REMOTEPUSHNOTIFICATION(43),
        PTTRADIOCLIENT(44),
        XCAPCOUCHCLIENT(45),
        VOICEMSGFALLBACK(46),
        PTTRECORDINONCLIENT(47),
        MULTISIMULTANEOUSSESSION(48),
        BREADCUMB(49),
        INSTADRX(50),
        USERENABLE(51),
        INTEROPFEATURE(53),
        AMBIENTLISTENING(54),
        DISCRETELISTENING(55),
        USERCHECK(56),
        USERENABLEDISABLE(57),
        EMERGENCY(58),
        ABDG_GROUP_MEMBER(59),
        ABDG_GROUP_OWNER(60),
        MCDATA_SDS_FEATURE(64),
        MCDATA_FD_FEATURE(65),
        OPERATIONALSTATUSMESSAGING(66),
        MCVIDEOTX(67),
        MCVIDEORX(68),
        MCVIDEOGROUPRX(69),
        MCVIDEOCONFIRMEDPULL(70),
        MCVIDEOUNCONFIRMEDPULL(71),
        AFFILIATIONFEATURE(78),
        MCPTT_COMPLAIANCE_BIT(81),
        MCPTT_VIA_GW(82),
        MCDEVICE(84),
        USER_PROFILE_MGMT_BIT(76),
        WDSPATCHING(89),
        WDSRECORDING(90),
        DATA_GROUP_OP(93),
        DATA_INTER_OP(106),
        MCX_GROUP_REGROUP_FLAG_BIT(111),
        ASTRO_FREQUENCT_SELECT_BIT(118),
        ASTRO_CODED_AND_CLEAR_BIT(119),
        COMMON_CONTACT_LIST(121),
        PTT_RECORDING_FLAG(126),
        DATA_RECORDING_FLAG(127),
        VIDEO_RECORDING_FLAG(128),
        SELF_DND_PRIVILEGE(136),
        LARGE_AGENCY_DISPATCH(139);

        int featureSet;

        FEATURE_SET(int type) {
            this.featureSet = type;
        }

        public int value() {
            return featureSet;
        }
    }
}
