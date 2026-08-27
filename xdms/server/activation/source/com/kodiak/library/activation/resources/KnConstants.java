/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnConstants.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
 * ************************************************************************
 */
package com.kodiak.library.activation.resources;

import java.math.BigInteger;

public class KnConstants {
    //Library Name
    public static final String LIBRARY_NAME = "ACTLIB";

    public static final String CONFIG_PATH_NAME = "configPath";
    public static final String APP_CONFIG_FILE_NAME = "ApplicationConfig.properties";

    public static final String WEB_PTTSERVERID = "DBDSN";
    public static final String ACTIVATIONKEY_EXPIRED_TIME = "ACTIVATIONKEY_EXPTIME";

    public static final String SESSION_TIMEOUT_DURATION = "SESSION_TIMEOUT_DURATION";
    public static final String FORCED_TIMEOUT_DURATION = "FORCED_TIMEOUT_DURATION";
    public static final String SESSION_TIMEOUT_DELAY = "SESSION_TIMEOUT_DELAY";
    public static final String FORCED_TIMEOUT_DELAY = "FORCED_TIMEOUT_DELAY";
    public static final String REFRESH_INTERVAL = "REFRESH_INTERVAL";
    public static final String TTL_VALUE = "TTL";
    public static final String MESSAGE_TTL = "MESSAGE_TTL";

    public static final short POC_XDM_CARD_TYPE = 0;

    public static final String ACT_OPERATION_TYPE = "107";

    public static final String DEVICE_ACTIVATION_OPERATION_TYPE = "129";
    public static final String USER_LOGIN_OPERATION_TYPE = "130";
    public static final String RADIO_DEVICE_ACTIVATION = "138";

    public static final String HOME_SERVERID = "HOME_SERVERID";

    //Messaging Frwk Sync
    public static final boolean SYNC_ENABLED = true;
    public static final boolean SYNC_DISABLED = false;

    // Dail Plan Type
    public static final int NUMBERING_PLAN_TYPE_NATIONAL = 0;
    public static final int NUMBERING_PLAN_TYPE_INTERNATIONAL = 1;

    public static final int INT_MAX_VALUE = 65536; // range 0 - 65535
    public static final int INT_MIN_VALUE = 0;

    public static final String SUCCESS = "SUCCESS";

    public static final int CLIENT_TYPE_CSR = 3;
    public static final int CLIENT_TYPE_WEB = 1;
    public static final String CARD_TYPE_WPT = "WPT";
    public static final String APACHE_QUEUE_NAME = "ActiveMQ";

    public static final Integer SUBS_CLIENTTYPE_UNKNOWN = 0;
    public static final Integer SUBS_CLIENTTYPE_HANDSET = 1;
    public static final Integer SUBS_CLIENTTYPE_DESKTOP = 2;
    public static final Integer SUBS_CLIENTTYPE_DISPATCHCLIENT = 3;
    public static final Integer SUBS_CLIENTTYPE_WIFIONLYCLIENT = 5;
    public static final Integer SUBS_CLIENTTYPE_3RDPARTY_POCCLIENT = 6;
    public static final Integer SUBS_CLIENTTYPE_PTTRADIO_POCCLIENT = 14;

    public static final Integer SUBS_LICENSE_TYPE_REGULAR_STANDARD = 0;
    public static final Integer SUBS_LICENSE_TYPE_USER_STANDARD = 1;
    public static final Integer IMPLICIT_LOGIN_TYPE = 1;
    public static final Integer EXPLICIT_LOGIN_TYPE = 2;
    public static final Integer DEVICE_ACTIVATION_ACTIVATED = 1;


    public static final String SERVICENAME_HANDSET = "PoCService";
    public static final String SERVICENAME_DESKTOP = "PoCDesktopClientService";
    public static final String SERVICENAME_DISPATCHCLIENT = "PoCDispatchConsoleService";
    public static final String SERVICENAME_WIFIONLYCLIENT = "PoCWIFIOnly";
    public static final String SERVICENAME_3RDPARTY_POCCLIENT = "PoCThirdPartyClientService";
    public static final String SERVICENAME_PTT_RADIO_CLIENT = "PoCRadioClientService";

    public static final String CLIENT_ID = "ptths";
    public static final String CLIENT_ID_WSD = "pttwds";
    public static final String HS_CLIENT_SECRET = "KC_HANDSET_CLIENT_SECRET";
    public static final String WDS_CLIENT_SECRET = "KC_WDS_CLIENT_SECRET";

    //Third party constants
    public static final int THIRD_PARTY_EXPIRY_TIME = -1;
    
    
}