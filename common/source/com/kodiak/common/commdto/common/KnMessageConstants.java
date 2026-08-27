/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: SureshKumar G
 * Date: Dec 14, 2010
 * Time: 5:24:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnMessageConstants {

    public static final int MANAGEMENT_PORT = 15672;
    public static final int TLS_MANAGEMENT_PORT = 15671;
    public static final int AMQP_TLS_MANAGEMENT_PORT = 5671;
    public static final String REST_QRY_FETCH_CONSUMERS = "/api/consumers/";
    public static final String CONN_FACTORY_KEY = "connection.factory.key";
    public static final String CONN_FACTORY_VALUE = "connection.factory.value";
    public static final String CONN_PROV_URL_KEY = "connection.provider.url.key";
    public static final String CONN_FACTORY_NAMES = "connectionFactoryNames";
    public static final String CARD_TYPE_KEY = "cardType";
    public static final String LOCAL_IP = "localIP";
    public static final String QUEUE_NAMES = "queueNames";
    public static final String PROTOCOL = "connection.protocol";
    public static final String PORT_KEY = "connection.port";
    public static final String MQ_INACTIVITY_DURATION = "connection.inactivity.duration";

    public static final String STUB_MODE = "stubMode";
    public static final String STUB_MODE_REMOTE_IP = "stubModeRemoteIp";
    public static final String MIN_CONSUMER_PUB_THREADPOOL_SIZE = "pubThreadPoolMinSize";
    public static final String MAX_CONSUMER_PUB_THREADPOOL_SIZE = "pubThreadPoolMaxSize";

    public static final String MIN_CONSUMER_CORP_GET_THREADPOOL_SIZE = "corpGetThreadPoolMinSize";
    public static final String MAX_CONSUMER_CORP_GET_THREADPOOL_SIZE = "corpGetThreadPoolMaxSize";
    public static final String MIN_CONSUMER_CORP_POST_THREADPOOL_SIZE = "corpPostThreadPoolMinSize";
    public static final String MAX_CONSUMER_CORP_POST_THREADPOOL_SIZE = "corpPostThreadPoolMaxSize";

    public static final String MIN_CONSUMER_PROV_GET_THREADPOOL_SIZE = "provGetThreadPoolMinSize";
    public static final String MAX_CONSUMER_PROV_GET_THREADPOOL_SIZE = "provGetThreadPoolMaxSize";
    public static final String MIN_CONSUMER_PROV_POST_THREADPOOL_SIZE = "provPostThreadPoolMinSize";
    public static final String MAX_CONSUMER_PROV_POST_THREADPOOL_SIZE = "provPostThreadPoolMaxSize";

    public static final String MIN_CONSUMER_NNI_THREADPOOL_SIZE = "nniThreadPoolMinSize";
    public static final String MAX_CONSUMER_NNI_THREADPOOL_SIZE = "nniThreadPoolMaxSize";
    public static final String MIN_CONSUMER_DROPPING_THREADPOOL_SIZE = "droppingThreadPoolMinSize";
    public static final String MAX_CONSUMER_DROPPING_THREADPOOL_SIZE = "droppingThreadPoolMaxSize";
    public static final String MAX_PROV_THREADPOOL_QUEUE_SIZE = "provThreadPoolMaxQueueSize";
    public static final String MAX_CORP_THREADPOOL_QUEUE_SIZE = "corpThreadPoolMaxQueueSize";
    public static final String MAX_PUB_THREADPOOL_QUEUE_SIZE = "pubThreadPoolMaxQueueSize";
    public static final String MAX_NNI_THREADPOOL_QUEUE_SIZE = "nniThreadPoolMaxQueueSize";
    public static final String MIN_XDMDATA_INTF_THREADPOOL_SIZE = "xdmDataIntfThreadPoolMinSize";
    public static final String MAX_XDMDATA_INTF_THREADPOOL_SIZE = "xdmDataIntfThreadPoolMaxSize";
    public static final String MAX_XDMDATA_INTF_THREADPOOL_QUEUE_SIZE = "xdmDataIntfThreadPoolMaxQueueSize";
    public static final String MIN_LOGIN_NOTIFY_THREADPOOL_SIZE = "loginNotifyThreadPoolMinSize";
    public static final String MAX_LOGIN_NOTIFY_THREADPOOL_SIZE = "loginNotifyThreadPoolMaxSize";
    public static final String MAX_LOGIN_NOTIFY_THREADPOOL_QUEUE_SIZE = "loginNotifyThreadPoolMaxQueueSize";
    public static final String CARD_TYPE_WEBSERVER = "1";
    public static final String CARD_TYPE_XDMSERVER = "2";
    public static final String CARD_TYPE_COMBO_WEB_XDM = "3";

    public static final String DBMGR_FILE_NAME = "dbmgr.props";
    public static final String JNDI_PROPS_FILE_NAME = "/jndi.properties";
    public static final String DBDSN = "DBDSN";
    public static final String IPADDRESS = "IPADDRESS";

    //stores the Connection Factory look up name
    public static final String CONNECTION_FACTORY = "ConnectionFactory";

    public static final String QUEUE_TYPE_PROVISIONING = "1";
    public static final String QUEUE_TYPE_PUBLIC_GROUP_MGMT = "2";
    public static final String QUEUE_TYPE_CORPORATE_GROUP_MGMT = "3";
    public static final String QUEUE_TYPE_COMMON = "4";

    public static final String PROVISIONING = "PROV";
    public static final String PROVISIONING_POST = "PROV-POST";
    public static final String CORPORATE = "CORP";
    public static final String CORPORATE_POST = "CORP-POST";
    public static final String PUBLIC = "PUB";
    public static final String NNI = "NNI";
    public static final String XDMDATAINTF = "XDMDATAINTF";
    public static final String LIRMQEVENT = "LIRMQEVENT";
    public static final String LOGINNOTIFY = "LOGINNOTIFY";
    public static final String DROPPING = "DROPPING";

    public static final String PROV_XDM_QUEUE = "XDMPROVQ1";
    public static final String PROV_XDM_WRITE_QUEUE = "XDMPROVWriteQ1";
    public static final String CORP_XDM_WRITE_QUEUE = "XDMCORPWriteQ1";
    public static final String CORP_XDM_QUEUE = "XDMCORPQ1";
    public static final String PUB_XDM_QUEUE = "XDMPUBQ";
    public static final String NNI_XDM_QUEUE = "XDMNNIQ";
    public static final String PROV_WEB_QUEUE = "WEBPROVQ";
    public static final String CORP_WEB_QUEUE = "WEBCORPQ";
    public static final String PUB_WEB_QUEUE = "WEBPUBQ";
    public static final String NNI_WEB_QUEUE = "WEBNNIQ";
    public static final String XDM_DATA_INTF_QUEUE = "XDMDATAQ";
    public static final String JMS_TYPE_DROPPED_REQUEST = "1";
    public static final String DROPPED_REQUEST = "DROPPEDREQUEST";
    public static final String XDMLOGINNOTIFYEVENTQ1 = "XDMLOGINNOTIFYEVENTQ1";
    public static final String XDMLINOTIFYEVENTQ = "XDMLINOTIFYEVENTQ1";

    //MINT-26215 : Hardcoded the prefetch count for the queues
    public static final int PROV_XDM_QUEUE_PREFETCH_COUNT = 8;
    public static final int PROV_XDM_WRITE_QUEUE_PREFETCH_COUNT = 6;
    public static final int CORP_XDM_QUEUE_PREFETCH_COUNT = 6;
    public static final int CORP_XDM_WRITE_QUEUE_PREFETCH_COUNT = 5;
    public static final int PUB_XDM_QUEUE_PREFETCH_COUNT = 20;
    public static final int XDMDATAINTF_QUEUE_PREFETCH_COUNT = 6;
    public static final int XDMLOGINNOTIFYEVENT_QUEUE_PREFETCH_COUNT = 4;

    public static final String PUBLISHER_ID_DEFAULT = "DEFAULT";

    public static final String DELIMETER_COMMA = ",";
    public static final String RMQ_TLS_FLAG = "RMQ_TLS_FLAG";
    public static final String RMQ_AMQP_TLS_FLAG="RMQ_AMQP_TLS_FLAG";
    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";
    public static final String CLUSTERID_ENV_NAME = "CLUSTERID_ENV_NAME";
    public static final Map<String, String> QUEUE_TYPE_VS_NAME_MAP = new HashMap<String, String>() {
        {
            put(QUEUE_TYPE_PROVISIONING, "ProvQueue");
            put(QUEUE_TYPE_PUBLIC_GROUP_MGMT, "PublicGrpMgmtQueue");
            put(QUEUE_TYPE_CORPORATE_GROUP_MGMT, "CorporateGrpMgmtQueue");
            put(QUEUE_TYPE_COMMON, "CommonQueue");
        }
    };


}
