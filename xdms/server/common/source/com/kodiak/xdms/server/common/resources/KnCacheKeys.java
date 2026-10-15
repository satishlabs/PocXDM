/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/4/11   7.0
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
 * *************************************************************************
 */

package com.kodiak.xdms.server.common.resources;

public class KnCacheKeys {
    public static final String DBMGR_PROPS = "commonlib.dbmgrprops";
    public static final String XDMS_SVC_CONFIG = "commonlib.xdmsserviceconfig";
    public static final String XDMS_PTTSERVER_ID = "commonlib.xdmpttserverid";
    public static final String XDMS_SUBS_SYS_CONFIG = "commonlib.xdmssubsysconfig";
    public static final String DIAL_PLAN_INFO = "commonlib.dialplaninfo";
    public static final String POC_SUPPORTED_DEVICES = "commonlib.pocsupporteddevices";
    public static final String CUSTOM_CONFIG = "commonlib.customconfig";
    public static final String EXTERNAL_PROFILE_LIST = "commonlib.extprofile";
    public static final String APN_XCAP_ROOT_URIS = "xcap-root-uri";
    public static final String DEFAULT_APN_INFO = "default-apn-info";
    public static final String APN_INFO = "provlib.apninfo";
    public static final String DEFAULT_APN_NAME="default-apn-name";
    public static final String POC_BLACKLIST_DEVICES = "commonlib.pocblacklistdevices";
    public static final String ACTIVATIONCODECONFIG = "commonlib.activationcodeconfig";
    public static final String NNI_FEATURE_STATUS = "commonlib.nnifeatureStatus";
    public static final String NNI_PROFILES = "commonlib.nniprofiles";
    public static final String WEBCARD_IP_LIST = "webcard-ips-list";
    public static final String RTX_VALUE_CONFIG = "commonlib.rtxconfigvalue";
    public static final String CLIENT_TYPE_CONFIG = "client-type-config";
    public static final String APNQOS_INFO = "provlib.apnqos";
    public static final String APN_CONFIG_INFO = "apnConfig.apnqos";
    public static final String SUPP_VOCODER_PROFILE = "suppVocoder.profile";
    public static final String MS_COMMON_CONFIG = "commonlib.mscommonconfig";
    public static final String MS_CLUSTER_CONFIG = "commonlib.msclusterconfig";
    public static final String MS_SVC_CONFIG_DOC = "commonlib.mssrvconfig";
    public static final String MQ_QUEUE_ROUTING_KEYS = "commonlib.queueroutingkeys";

    //key to fetch the cache object for PTX URLS
    public static final String PTX_URLS_CAUCH_BASE = "commonlib.xdmscauchbaseurls";
    //needed to store the cache with a key
    public static final String MQ_SERVICE_CONFIG_INFO= "commonlib.mqservicefqdn";
    
    public static final String DATA_PACKAGE_INFO = "commonlib.datapackageinfo";
    public static final String ADDL_PROFILE_INFO_BY_PKGTYPE = "commonlib.addlprofileinfobypkgid";
    public static final String QPP_PCRF_PROFILE = "commonlib.qpppcrfprofile";
    public static final String POC_SVC_CONFIG = "commonlib.pocsvcconfig";
    public static final String CLUSTER_ID_MAP = "commonlib.clusterIdMap";
}
