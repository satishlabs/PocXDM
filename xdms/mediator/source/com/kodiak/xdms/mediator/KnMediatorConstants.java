/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/6/11       7.0
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
public class KnMediatorConstants {

    public static final String CREATE_SUBS_SUCCESS = "CREATE_SUBS_SUCCESS";
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    public static final String ERROR_CODE_INIT_FAILED = "Validator.KNEC-MD30001";
    public static final String ERROR_CODE_INVALID_OPERATION = "Validator.KNEC-MD30002";
    public static final String ERROR_CODE_INVALID_DTO_PASSED = "Validator.KNEC-MD30003";
    public static final String ERROR_CODE_INTERNAL_ERROR = "Validator.KNEC-MD30004";
    public static final String ERROR_CODE_DOC_NOT_MODIFIED = "Validator.KNEC-MD30011";
    public static final String PAMACCOUNT_DELETE_IN_PROGRESS = "Validator.KNEC-MD12096";
    public static final String ERROR_CODE_MDN_PRESENT_IN_PAMACCINFO = "Validator.KNEC-MD13007";
    public static final String INVALID_RATE_PLAN_REQUEST = "Validator.KNEC-MD12098";
    public static final String INVALID_LICENSE_LIMIT = "Validator.KNEC-MD20140";
    //error code for documentNot created or deleted or syncgateway unable to process request
    public static final String ERROR_INVALID_SYNCGW_REQUEST = "KNEC-MD10525";
    public static final String ERROR_USER_DOC_NOT_CREATED = "BOEntity.KNEC-MD10525";
    public static final String MESSAGE_TTL_XDMPROVQ = "MESSAGE_TTL_XDMPROVQ";
    public static final String SUCCESS_CODE = "KNEC-MD00000";
    public static final int intRange = 2147483647;

    public static final String XDM_ASYNC_NTFY_THRESHOLD = "XDM_ASYNC_NTFY_THRESHOLD";
    public static final int SUBSCRIBER_PROVISIONED = 0;

    //Constant for CUSTOM PROV CLASS NAME
    public static final String CUSTOM_PROV_INVOKER = "CustomProvInvoker";
    public static final String CUSTOM_PROV_ACTION = "ACTION";
    public static final String CUSTOM_PROV_VIEW_OP = "View";
    public static final String CUSTOM_PROV_ADD_PAMACCOUNT_OP = "AddPAMAcc";
    public static final String CUSTOM_PROV_MODIFY_PAMACCOUNT_OP = "ModifyPAMAcc";
    public static final String CUSTOM_PROV_CANCEL_PAMACCOUNT_OP = "CancelPAMAcc";
    public static final String CUSTOM_PROV_VIEW_PAMACCOUNT_OP = "ViewPAMAcc";
    public static final String BILLING_MDN = "BILLING_MDN";
    public static final Integer OFF_MODE=0;
    public static final Integer ON_MODE=1;


    public static final String CORP_ID = "CORP_ID";
    public static final String ACCOUNT_TYPE_INDICATOR = "ACCOUNT_TYPE_INDICATOR";
    public static final String RATE_PLAN = "RATE_PLAN";
    public static final int DEFAULT_NOTIFY_SIZE = 4;
    public static final int CORP_EXTERNAL_SUBSTYPE_MPTT_REQUEST = 2;
    public static final int CORP_EXTERNAL_SUBSTYPE_MPTT_DB = 1;
    public static final int CORP_EXTERNAL_SUBSTYPE_KODIAK_REQUEST = 1;
    public static final int CORP_EXTERNAL_SUBSTYPE_KODIAK_DB = 0;
    public static final int CORP_BROADCAST_GROUP_TYPE = 3;
    public static final int DISP_CLIENT_TYPE=3;
    public static final int ABDG_Enable=1;
    
    //Audit constants.
    public static final String PAM_AUDIT_CREATE_LICENSE_PACK = "createLicensePack";
    public static final String PAM_AUDIT_UPDATE_LICENSE_PACK = "updateLicensePack";
    public static final String PAM_AUDIT_DELETE_LICENSE_PACK = "deleteLicensePack";
    public static final String PAM_AUDIT_CHANGE_SERVICE_AUTH_STATUS = "changePAMServiceAuthStatus";
    public static final String PAM_AUDIT_CHANGE_BILLING_NUMBER = "changeBillingNumber";
    public static final String PAM_AUDIT_UPGRADE_LICENSE_PACK = "upgradeLicensePack";
    public static final String PAM_AUDIT_DOWNGRADE_LICENSE_PACK = "downgradeLicensePack";
    public static final String PAM_AUDIT_GET_LICENSE_PACK_PROFILE = "getLicensePackProfile";
    public static final String PAM_AUDIT_UPDATE_SUBSCRIBER_FS = "updateLicensePackFS";

    public static final String UPDATE_BAN_AUDIT = "UpdateBan";
    
    public static final int LICENSE_FILTER_TYPE = 0;    
    public static final String AUDIT_LOGGER_CONSTANT = "4005";
    public static final String AUDIT_PROVLOGGER_CONSTANT = "4000";
    public static final String BULK_PROV_BATCH_SIZE = "BULK_PROV_BATCH_SIZE";

    public static final String ACTION = "ACTION";

    public static enum PAM_ACCOUNT_STATE {
        PROVISION(0),
        ACTIVE(2),
        SUSPEND(3),
        DELETE_IN_PROGRESS(4);
        int accountState;

        PAM_ACCOUNT_STATE(int type) {
            accountState = type;
        }

        public int value() {
            return accountState;
        }

    }

    public enum ACTIONS {
        ADD(1), MODIFY(2), SUSPEND(3), RESUME(4), CANCEL(5), VIEW(6), CHANGEMDN(7);

        int action;

        ACTIONS(int action) {
            this.action = action;
        }

        public int value() {
            return action;
        }
    }

    public static enum MEDIATOR_NAME{
        NNIMEDIATOR;
    }
    
    public static enum XDM_BULK_ORDER_INFO {
    	BULKORDER_ID("BULKORDER_ID"), BULKORDER_TYPE("BULKORDER_TYPE"), CHANGE_LEVEL("CHANGE_LEVEL"), CORP_ID("CORP_ID"), BO_REQ_OBJECT("BO_REQ_OBJECT"),
    	BO_REQ_OBJECT_VERSION("BO_REQ_OBJECT_VERSION"), INSERTION_TIME("INSERTION_TIME"), STATUS("STATUS"), COMPLETION_TIME("COMPLETION_TIME"), 
    	BO_RESP_OBJECT("BO_RESP_OBJECT");
    	
    	String column;
    	
    	XDM_BULK_ORDER_INFO(String column) {
    		this.column = column;
    	}
    	
    	public String value() {
    		return column;
    	}
    }
    public static final int THIRD_PARTY_CLIENT_TYPE=6;
    public static final String DYNAPI_SERVICE_ENABLED = "DYNAPI_SERVICE_ENABLED";


    //Notification changes
    public static final String MSISDN = "MSISDN";
    public static final String NONAME_SPACE_SCH_LOC = "KodiakProvisioningResponse.xsd";
    public static final String SYSTEM = "Kodiak";
    public static final String PROVISIONING_CARRIER = "PROVISIONING_CARRIER";
    public static final String SENDER_LOGIN = "SENDER_LOGIN";
    public static final String SENDER_PASSWORD = "SENDER_PASSWORD";
    public static final String ERROR_CODE_INTERNAL_SERVER_ERROR = "KNEC-WPT-IDS-INTERNAL-SERVER-ERROR";
    public static final String ERROR_CODE_MAPPINGS_PROPS = "/errorCodeMapping.properties";
    public static final String PAM_ERROR_CODE_MAPPINGS_PROPS = "/PamErrorCodeMapping.properties";
    public static final String ERROR_CODE_MAPPINGS_FILE = "/client-errorcodes.xml";
    public static final String PAM_ERROR_CODE_MAPPINGS_FILE = "/pam-client-errorcodes.xml";
   
    public static final String SOAP_PAM_ERROR_CODE_MAPPINGS_FILE = "/SoapPamInbounderrorcodes.xml";
    public static final String SOAP_PAM_ERROR_CODE_MAPPINGS_PROPS = "/SoapPamAccErrorCodeMapping.properties";
    public static final String ERROR_CODE_INVALID_DATA = "KNEC-WPT-IDS-INVALID-DATA";


    public static final Map<String, String> ERROR_CODES_MAP = new HashMap<String, String>() {
        {
            // Mediation layer Error codes mapping
            put("MD30001", ERROR_CODE_INTERNAL_SERVER_ERROR);
            put("MD30002", ERROR_CODE_INTERNAL_SERVER_ERROR);

            // Common Lib Specific Error Codes
            put("CM10001", ERROR_CODE_INTERNAL_SERVER_ERROR);
            put("CM10002", ERROR_CODE_INVALID_DATA);
        }
    };
    public static final String ERROR_CODE_PREFIX = "KNEC-MD";

    public static enum SUBS_CLIENT_TYPE {
        UNKNOWN(0),
        HANDSET(1),
        DESKTOP(2),
        DISPATCH_CLIENT(3),
        POC_DONOR_RADIO(4),
        POC_WIFIONLY(5),
        THIRDPARTYPOCCLIENT(6),
        PTT_RADIO_HANDSET_CLIENT(14);


        int subsClientType;

        SUBS_CLIENT_TYPE(int type) {
            subsClientType = type;
        }

        public int value() {
            return subsClientType;
        }


    }


    //imei enhancement
    public static final String IDS_IMEI_MAJOR_CODE = "900";
    public static final String IDS_IMEI_RESPONSE = "Success";
    public static final String IDS_3PP_DEFAULT_NTFY_URL="IDS_3PP_DEFAULT_NTFY_URL";

    public static final String UPDATEBAN = "UpdateBAN";
    public static final String OPSCLI_PAM_NTFY_URL = "OPSCLI_PAM_NTFY_URL";

    public static final String INTERNAL_SERVER_ERROR = "KNEC-IDS1002";

    public static final String PAM_ERROR_CODE_INTERNAL_SERVER_ERROR = "KNEC-WPT-PAM-INTERNAL-SERVER-ERROR";


    private static ArrayList listOfTriggerTimings() {
        ArrayList<Integer> listOfTriggerTimings = new ArrayList<Integer>();
       // listOfTriggerTimings.add(0);
        listOfTriggerTimings.add(5);
        listOfTriggerTimings.add(15);
        listOfTriggerTimings.add(30);
        listOfTriggerTimings.add(60);
        listOfTriggerTimings.add(90);
        listOfTriggerTimings.add(120);
        listOfTriggerTimings.add(180);
        listOfTriggerTimings.add(240);
        listOfTriggerTimings.add(300);
        listOfTriggerTimings.add(360);
        return listOfTriggerTimings;

    }

    public static ArrayList<Integer> listOfTriggerTimings = listOfTriggerTimings();

    public static final String ERROR_CONFIG_FILE = "ErrorConfigFile";
    public static final String PAM_ERROR_CONFIG_FILE = "PamErrorConfigFile";
    public static final String ERROR_CONFIG = "error-config";
    public static final String ERROR_CODE = "error-code";
    public static final String ERROR_MAJOR_CODE = "major-code";
    public static final String ERROR_DESCRIPTION = "description";
    public static final String SERVICE_ERROR_CODE = "service-error-code";
    public static final String ERROR_MSG = "message";
    public static final String HTTP_STATUS_CODE = "http-status-code";
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_SPACING = " ";
    public static final String SQUARE_OPENING = "[";
    public static final String SQUARE_CLOSING = "]";
    public static final String ERROR_LEVEL = "0";
    public static final String INVALID_INPUT = "Invalid input";
    public static final String IMEI_CHANGED = "IMEI_CHANGED";
    public static final String CLIENT_TYPE = "CLIENT_TYPE";
    public static final String ACTIONRATEPLANS = "ACTIONRATEPLANS";
    public static final String NOTIFICATION_URL = "3PP_NOTIFICATION_URL";
    public static final String PAM_NOTIFICATION_URL = "PAM_NOTIFICATION_URL";
    public static final String IDS_TRANSACTION_ID = "IDS_TRANSACTION_ID";
    public static final String PRODUCTTAG = "PRODUCTTAG";


    public static final String PARAMNAME = "IDS_3PP_DEFAULT_NTFY_URL";
    public static final String IDS_IMEI_CHANGE_NTFY_SMS = "IDS_IMEI_CHANGE_NTFY_SMS";
    public static final String IDS_CREATE_HS_SUBSCR_NTFY_SMS = "IDS_CREATE_HS_SUBSCR_NTFY_SMS";
    public static final String IDS_CREATE_HS_SUBSCR_NTFY_SMS_PTTRADIO = "IDS_CREATE_HS_SUBSCR_NTFY_SMS_PTTRADIO";
    public static final String IDS_IMEI_CHANGE_NTFY_SMS_PTTRADIO = "IDS_IMEI_CHANGE_NTFY_SMS_PTTRADIO";
    public static final String UPDATEBAN_MDN = "UPDATEBAN_MSISDN_VALUES";
    
    public static final String INTERFACE = "INTERFACE";

	public enum INTERFACETYPE {
		REST("REST"), SOAP("SOAP");

		private String type;

		private INTERFACETYPE(String s) {
			type = s;
		}

		public String getType() {
			return type;
		}
	}

	public enum NOTIFICATIONACTIONS {
		ADD("Add"), CANCEL("Cancel"), MODIFY("Modify"), RESUME("Resume"), SUSPEND("Suspend"),
		DELETE("Delete"), CHANGEMDN("ChangeMDN"), VIEW("View");
		String action;

		NOTIFICATIONACTIONS(String action) {
		this.action = action;
		}

		public String value() {
		return action;
		}
		}
}
