/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 *
 */
package com.kodiak.xdms.server.common.cb.util;

import com.couchbase.client.core.env.CertificateAuthenticator;
import com.couchbase.client.core.env.IoEnvironment;
import com.couchbase.client.core.env.SecurityConfig;
import com.couchbase.client.core.env.TimeoutConfig;

import com.couchbase.client.java.*;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnEncryptionDecryptionUtil;
import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.logger.KnLogger;
import com.couchbase.client.java.env.ClusterEnvironment;

import java.io.File;
import java.nio.file.Paths;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.*;

import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;

import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnMicroSvcsCommonConfig;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.util.*;
import com.couchbase.client.java.Collection;

import com.couchbase.client.core.error.AmbiguousTimeoutException;
import com.couchbase.client.core.error.RequestCanceledException;
import com.couchbase.client.core.error.UnambiguousTimeoutException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;

/**
 * @author MVPG64
 * Below class defines the way of couchBase service monitoring
 */
public class KnCBSRepository
{
    private static final KnLogger logger = KnLogger.getLogger(KnCBSRepository.class);

    public static final long 	THRESHOLDTIME_INTERVAL 	= 5*60*1000;   //5 MIN IN MILLISEC
    private static Map<Integer, String> cbFQDNMap = new LinkedHashMap<>();
    private static Map<Integer, String> cbQueryFQDNMap = new LinkedHashMap<>();
    private static Map<Integer, String> pocBucketNameMap = new LinkedHashMap<>();
    private static Map<Integer, String> pocBucketPwdMap = new LinkedHashMap<>();
    private static Map<String, String> paramNameValueMapService = new LinkedHashMap<>();

    private static Cluster cluster;

    private static Bucket pocbucket;

    private static Collection pocDataBucketdfcol;
    private long prevFailureTimestamp=0;
    private static ClusterEnvironment env;
    private static final String ROOT_NODE="$";
    private static final String ARRAY="array";
    private static final String OBJECT="object";
    private Map<String, byte[]> keyMap = new HashMap<>();
    public static final String CB_SDK_SPECIAL_KEY = "cbSdkSpecialKey";
    public static String certPath;
    public static String IS_CBS_SSL_ENABLED="IS_CBS_SSL_ENABLED";
    public static String IS_SSL_ENABLED;
    public static Boolean isSSLEnabledForCluster = Boolean.FALSE;
    public static String ENABLED = "1";
    public static String kspwd="storepass";
    public static String bucketName = KnRepoBucket.POC_DATA.getBucketName();
    public static String keyStoreExt = ".jks";
    public static String jksPath;
    public static final String COMMONCONFIGFILE = "CommonConfig.properties";
    public static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;
    public static Properties commonConfigProps = new Properties();
    private static boolean remoteCluster;
    private static AtomicInteger atomicInteger = new AtomicInteger();


    public static Collection col;

    private static int clusterId;

    private static KnGenInfoUtil knGenInfoUtil;

    private static KnCBSRepository cbMgr;

    public static boolean pocbucketdisableFlag=false;

    static {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(commonConfigFile);
            commonConfigProps.load(fileInputStream);
            IS_SSL_ENABLED = commonConfigProps.getProperty("IS_CBS_SSL_ENABLED");
            certPath = commonConfigProps.getProperty("CBS_SSL_FILES_PATH");
            jksPath= certPath+"/"+bucketName+keyStoreExt;
        } catch (Exception e) {
            logger.debug("Exception Occured while fetcing the comminConfig File- ", e);
        }
    }


    private KnCBSRepository() {
        init();
    }

    public static synchronized KnCBSRepository getInstance() {
        knGenInfoUtil = KnGenInfoUtil.getInstance();
        if (cbMgr == null) {
            cbMgr = new KnCBSRepository();
        }
        return cbMgr;
    }
    private synchronized void init() {
        String methodName = "initCBConnection()";
        logger.info(methodName, "ENTRY:");
        KnPersisterTxn knPersisterTxn = null;
        try {
            clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            knPersisterTxn = KnPersisterTxn.getPersisterTxn();
            knPersisterTxn.open();
            Map<Integer, List<KnMicroSvcsCommonConfig>> configMap = knGenInfoUtil.retrieveMSSvcsCommonConfigMap(knPersisterTxn);
            knPersisterTxn.save();
            Set<Map.Entry<Integer, List<KnMicroSvcsCommonConfig>>> entrySet = configMap.entrySet();
            for (Map.Entry<Integer, List<KnMicroSvcsCommonConfig>> entry : entrySet) {
                List<KnMicroSvcsCommonConfig> commonConfigList = entry.getValue();
                for (KnMicroSvcsCommonConfig config : commonConfigList) {
                    if (CBS_FQDN_POCDATA.equals(config.getParamName())) {
                        cbFQDNMap.put(entry.getKey(), config.getParamValue());
                    }
                    if (CBS_FQDN.equals(config.getParamName())) {
                        cbQueryFQDNMap.put(entry.getKey(), config.getParamValue());
                    }
                    if (POCBUCKETNAME.equals(config.getParamName())) {
                        pocBucketNameMap.put(entry.getKey(), config.getParamValue());
                    }
                    if (CBS_BUCKET_PASSWORD.equals(config.getParamName())) {
                        pocBucketPwdMap.put(entry.getKey(), config.getParamValue());
                    }
                }
            }
        } catch (KnPersistenceException e) {
           logger.error(methodName, "exception while fetching common config details - ", e);
            KnDbUtil.rollback(knPersisterTxn);
            System.exit(1);
        } catch (Exception e) {
            logger.error(methodName, "exception while processing - ", e);
            KnDbUtil.rollback(knPersisterTxn);
            System.exit(1);
        }
        logger.info(methodName, "EXIT:");
    }


    private Bucket getPOCDataBucket() {

        return pocbucket;
    }

    public Cluster getPOCCluster() {

        return cluster;
    }

    public synchronized void initCBSdkQueryFQDN(boolean remoteClusterConnect) {
        String methodName = "initCBSdkQueryFQDN(boolean )";
        logger.info(methodName, "initializing CB java queryFQDN client."
                + ",pocdataBucket:- " + pocbucket );

        int retryCounter = 3; //try 3 times to acuire a cb connection else raise a fatal log
        KnPersisterTxn knPersisterTxn = null;
        logger.info(methodName, "ENTRY :", remoteClusterConnect);
        if (cbMgr != null) {
            logger.info(methodName, "CB Manager is not null");
            if (cbQueryFQDNMap.isEmpty()) {
                logger.info(methodName, "cbQueryFQDNMap is empty, invoking init() to populate it");
                init();
            }
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            remoteCluster = remoteClusterConnect;

            if (remoteClusterConnect) {
                clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            } else {
                clusterIds = clusterIds.stream().filter(t -> (t == clusterId)).collect(Collectors.toList());
            }
            pocDataBucketdfcol=null;

            outer:
            for (int localClusterId : clusterIds) {

                logger.info(methodName + ", sizeof for cluster ", clusterIds.size());
                for (int i = 1; i <= retryCounter; i++) {
                    try {
                        closePrevCouchBaseConnection();
                        logger.info(methodName + ", Trying to acquire bucket for cluster ", localClusterId + " in loop count : " + i);
                        knPersisterTxn = KnPersisterTxn.getPersisterTxn();
                        knPersisterTxn.open();
                        paramNameValueMapService = knGenInfoUtil.retrieveMSSvcsServiceConfig(localClusterId, knPersisterTxn);
                        knPersisterTxn.save();

                        if(ENABLED.equals(IS_SSL_ENABLED)) {
                            logger.debug(methodName,"Making a TLS connection to CouchBase");
                            isSSLEnabledForCluster = Boolean.TRUE;
                        }else{
                            logger.debug(methodName,"Making a Non-TLS connection to CouchBase");
                            isSSLEnabledForCluster = Boolean.FALSE;
                        }
                        initEnvironment();
                        initializeBucketQueryFQDN(localClusterId);
                        initializeCollection();
                        if (pocbucket != null && cluster != null) {
                            logger.info(methodName, "Acquired Bucket for cluster" +localClusterId+ " bucket name :"+pocbucket.toString(), " in loop count : ", i);
                            pocbucketdisableFlag=false;
                            break outer;
                        }
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        logger.error(methodName, "Bucket is null for cluster : ", localClusterId, " Exception message  : ", e.getMessage());
                        KnDbUtil.rollback(knPersisterTxn);
                        if (i >= retryCounter) {
                            pocbucketdisableFlag=true;
                            logger.fatal(methodName + ", service is unable to get couchbase bucket cluster : ", localClusterId, "Exception occurred ", e);
                        }
                    }
                }
                logger.info(methodName,"Initialization successfull");
            }

        }
    }

    public synchronized void initCBSdk(boolean remoteClusterConnect) {
        String methodName = "initCBSdk(boolean )";
        logger.info(methodName, "initializing CB java client."
                + ",pocdataBucket:- " + pocbucket );

        int retryCounter = 3; //try 3 times to acuire a cb connection else raise a fatal log
        KnPersisterTxn knPersisterTxn = null;
        logger.info(methodName, "ENTRY :", remoteClusterConnect);
        if (cbMgr != null) {
            logger.info(methodName, "CB Manager is not null");
            if (cbQueryFQDNMap.isEmpty()) {
                init();
            }
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            remoteCluster = remoteClusterConnect;

            if (remoteClusterConnect) {
                clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            } else {
                clusterIds = clusterIds.stream().filter(t -> (t == clusterId)).collect(Collectors.toList());
            }
            pocDataBucketdfcol=null;

            outer:
            for (int localClusterId : clusterIds) {

                logger.info(methodName + ", sizeof for cluster ", clusterIds.size());
                for (int i = 1; i <= retryCounter; i++) {
                    try {
                        closePrevCouchBaseConnection();
                        logger.info(methodName + ", Trying to acquire bucket for cluster ", localClusterId + " in loop count : " + i);
                        knPersisterTxn = KnPersisterTxn.getPersisterTxn();
                        knPersisterTxn.open();
                        paramNameValueMapService = knGenInfoUtil.retrieveMSSvcsServiceConfig(localClusterId, knPersisterTxn);
                        knPersisterTxn.save();

                        if(ENABLED.equals(IS_SSL_ENABLED)) {
                            logger.debug(methodName,"Making a TLS connection to CouchBase");
                            isSSLEnabledForCluster = Boolean.TRUE;
                        }else{
                            logger.debug(methodName,"Making a Non-TLS connection to CouchBase");
                            isSSLEnabledForCluster = Boolean.FALSE;
                        }
                        initEnvironment();
                        initializeBucket(localClusterId);
                        initializeCollection();
                        if (pocbucket != null && cluster != null) {
                            logger.info(methodName, "Acquired Bucket for cluster" +localClusterId+ " bucket name :"+pocbucket.toString(), " in loop count : ", i);
                            pocbucketdisableFlag=false;
                            break outer;
                        }
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        logger.error(methodName, "Bucket is null for cluster : ", localClusterId, " Exception message  : ", e.getMessage());
                        KnDbUtil.rollback(knPersisterTxn);
                        if (i >= retryCounter) {
                            pocbucketdisableFlag=true;
                            logger.fatal(methodName + ", service is unable to get couchbase bucket cluster : ", localClusterId, "Exception occurred ", e);
                        }
                    }
                }
                logger.info(methodName,"Initialization successfull");
            }

        }
    }



    private synchronized void reInitSdkOnException()
    {
        String methodName = "reInitSdkOnException()";
        if((prevFailureTimestamp > 0) && ((System.currentTimeMillis() - prevFailureTimestamp)  > THRESHOLDTIME_INTERVAL ))
        {
            logger.info(methodName, "re-initializing cb sdk");
            //just to verify and close existing cluster/env
            closePrevCouchBaseConnection();
            //re initiate cluster and environment
            //initCBSdk(false);
            initCBSdkQueryFQDN(false);
            prevFailureTimestamp = 0;
            pocbucketdisableFlag=true;
        } else if(prevFailureTimestamp == 0)
        {
            logger.info(methodName, "marked cb sdk failure");
            closePrevCouchBaseConnection();
           // initCBSdk(false);
            pocbucketdisableFlag=true;
            prevFailureTimestamp = System.currentTimeMillis();
        }
    }

    private synchronized void reInitSdk()
    {
        final String methodName="reInitSdk()";
        logger.info(methodName, "re-initializing cb sdk");
        //just to verify and close existing cluster/env
        closePrevCouchBaseConnection();
        //re initiate cluster and environment
        //initCBSdk(false);
        pocbucketdisableFlag=true;
    }

    private void initEnvironment() throws Exception {
        String methodName = "initEnvironment";
        if(isSSLEnabledForCluster){
            Optional<String> op = Optional.empty();
            env = ClusterEnvironment
                    .builder().ioEnvironment(IoEnvironment.eventLoopThreadCount(10))
                    .timeoutConfig(TimeoutConfig
                            .connectTimeout(Duration.ofMillis(5000))
                            .queryTimeout(Duration.ofMillis(7500)))
                    .securityConfig(SecurityConfig.enableTls(true).enableHostnameVerification(true).
                                    trustStore((Paths.get(jksPath)), kspwd, op)).build();
        }else {
            env = ClusterEnvironment
                    .builder().ioEnvironment(IoEnvironment.eventLoopThreadCount(10))//iopoolsize --should roughly correlate with the number of cores available to the JVM
                    .timeoutConfig(TimeoutConfig
                            .connectTimeout(Duration.ofMillis(2500))
                            .queryTimeout(Duration.ofMillis(7500))).build();
        }

        logger.info(methodName, "Initialized cluster environment");
    }



    private Cluster getCluster(int activeClusterid,String userName, String password) throws Exception {
        String methodName = "getCluster(String, Stiring)";
        Cluster localCluster = null;
        if (isSSLEnabledForCluster) {
            CertificateAuthenticator authenticator = null;
            try {
                authenticator = getAuthenticator(jksPath);
            } catch (Exception ex) {
                logger.error(methodName, "", ex);
            }
            localCluster = Cluster.connect(cbQueryFQDNMap.get(activeClusterid),
                    ClusterOptions.clusterOptions(authenticator).
                            environment(env));
        } else {
            localCluster = Cluster.connect(cbQueryFQDNMap.get(activeClusterid),
                    ClusterOptions.clusterOptions(userName, password)
                            .environment(env));
        }
        localCluster.waitUntilReady(Duration.ofSeconds(10));
        logger.info(methodName, "cluster created " + localCluster);
        return localCluster;
    }

    private CertificateAuthenticator getAuthenticator(String keyPath) throws Exception {
        KeyStore ksr = KeyStore.getInstance("JKS");
        ksr.load(new FileInputStream(keyPath), kspwd.toCharArray());
        return CertificateAuthenticator.fromKeyStore(ksr, kspwd);
    }

    public Cluster getCluster(){
        return cluster;
    }

    private void initializeBucket(int activeClusterid) {
        String methodName = "initializeBucket(int)";
        logger.info(methodName, "Initializing Bucket: ", activeClusterid);
        String cbsBucketPocDataUser = KnCommonVaultUtil.getKeyFromVault(CBS_BUCKET_POCDATA_USER_NAME_PATH, KnConstants.CBS_BUCKET_POCDATA_USER);
        String cbsBucketPocDataPw = KnCommonVaultUtil.getKeyFromVault(CBS_BUCKET_POCDATA_PW_PATH, KnConstants.CBS_BUCKET_POCDATA_PW);
        if (null != cbsBucketPocDataUser && null != cbsBucketPocDataPw) {
            Bucket tempBucketRef = openBucket(activeClusterid, pocBucketNameMap.get(activeClusterid), cbsBucketPocDataUser, cbsBucketPocDataPw);
            logger.info(methodName, "Initialized Bucket: " + tempBucketRef);
            if (tempBucketRef != null) {
                pocbucket = tempBucketRef;
                clearCBSTimeoutAlarm();
            }
        }
    }

    private void initializeBucketQueryFQDN(int activeClusterid) {
        String methodName = "initializeBucketQueryFQDN(int)";
        logger.info(methodName, "Initializing Bucket: ", activeClusterid);
        String cbsBucketPocDataUser = KnCommonVaultUtil.getKeyFromVault(CBS_BUCKET_POCDATA_USER_NAME_PATH, KnConstants.CBS_BUCKET_POCDATA_USER);
        String cbsBucketPocDataPw = KnCommonVaultUtil.getKeyFromVault(CBS_BUCKET_POCDATA_PW_PATH, KnConstants.CBS_BUCKET_POCDATA_PW);
        if (null != cbsBucketPocDataUser && null != cbsBucketPocDataPw) {
            Bucket tempBucketRef = openBucket(activeClusterid, pocBucketNameMap.get(activeClusterid), cbsBucketPocDataUser, cbsBucketPocDataPw);
            logger.info(methodName, "Initialized Bucket: " + tempBucketRef);
            if (tempBucketRef != null) {
                pocbucket = tempBucketRef;
                clearCBSTimeoutAlarmQueryFQDN();
            }
        }
    }

    private void initializeCollection() {
        logger.info("initializeCollection()", pocDataBucketdfcol == null, pocbucket != null);
        if (pocDataBucketdfcol == null && pocbucket != null) {
            pocDataBucketdfcol = openCollection(null, KnRepoBucket.POC_DATA);
        }
    }

    private Collection getCollection(String collectionName, KnRepoBucket repoBucket) {

       if (repoBucket.equals(KnRepoBucket.POC_DATA)){
            if (null==collectionName || collectionName.isEmpty()) {
                col = pocDataBucketdfcol;
             }
}
        return col;
    }

    public Collection getCollection() {
        return pocDataBucketdfcol;
    }

    private Collection openCollection(String collectionName, KnRepoBucket repoBucket){
        String methodName = "openCollection(String, KnRepoBucket)";
        Collection col= null;
        try {
            if (null==collectionName || collectionName.isEmpty()) {
                col = getBucket(repoBucket).defaultCollection();
            } else
                col = getBucket(repoBucket).collection(collectionName);
        }catch(Exception exp){
            exp.printStackTrace();
            logger.error(methodName,"Exception caught while initializing collection:", exp);
            reInitSdkOnException();
        }
        logger.info(methodName, "Initialized collection:"+collectionName+", bucket:"+repoBucket.getBucketName());
        return col;
    }

    private Bucket openBucket(int activeClusterid,String bucketName, String userName, String password) {
        String methodName = "openBucket(bucketName, userName, password)";
        Cluster tempCluster = null;
        Bucket returnBucket = null;
        try {
            tempCluster = getCluster(activeClusterid,userName,password);
            returnBucket = tempCluster.bucket(bucketName);
        } catch (Exception e) {
            logger.warn(methodName, " Exception while opening the bucket. UserName:" + userName, e);
            reInitSdkOnException();
        }
        if (tempCluster != null) {
            logger.info(methodName, "Initialized Bucket :" + returnBucket + " with Username : " + userName, " Cluster reference is changed now.");
            cluster = tempCluster;
        } else {
            logger.warn(methodName, "tempCluster is null for bucketName:" + bucketName + " userName:" + userName);
        }
        return returnBucket;
    }

    private  Bucket getBucket(KnRepoBucket repoBucket) {
        Bucket bucket=null;
        if (repoBucket.equals(KnRepoBucket.POC_DATA))
            bucket = getPOCDataBucket();
        return bucket;
    }



    public  void clearAlarm(boolean remoteClusterAlarm) {
        String methodName = "clearAlarm(boolean)";
        int localClusterId = clusterId;
        if (remoteClusterAlarm) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            localClusterId = clusterIds.get(0);
        }
        logger.info(methodName, "Clearing Critical CouchBase Alarm for Cluster  ", localClusterId);
        String moInstanceInfo = getMoInstanceInfo(localClusterId);
        KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_CONNECT_CB, KnAlarmConstants.SEVERITY_CLEAR, moInstanceInfo);
    }

    public  void generateAlarm(boolean remoteClusterAlarm) {
        String methodName = "generateAlarm()";
        int localClusterId = clusterId;
        if (remoteClusterAlarm) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            localClusterId = clusterIds.get(0);
        }
        logger.info(methodName, "Below calling critical CouchBase Alarm for Cluster  ", localClusterId);
        String moInstanceInfo = getMoInstanceInfo(localClusterId);
        KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_CONNECT_CB, KnAlarmConstants.SEVERITY_CRITICAL, moInstanceInfo);
    }

    public void cbsClusterGenerateAlarm(boolean remoteClusterAlarm) {
        final String methodName = "cbsClusterGenerateAlarm(boolean)";
        int localClusterId = clusterId;
        if (remoteClusterAlarm) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds.removeIf(t -> t == clusterId);
            if (!clusterIds.isEmpty()) {
                localClusterId = clusterIds.get(0);
            }
        }
        logger.info(methodName, "Triggering critical CouchBase Alarm for Cluster", localClusterId);
        String moInstanceInfo = getMoInstanceInfoQueryFQDN(localClusterId);
        KnAlarmGeneratorUtil.generateAlarm(
                KnAlarmConstants.CBS_CLUSTER_UPDATE_FAILURE_ALARMCODE,
                KnAlarmConstants.SEVERITY_CRITICAL,
                moInstanceInfo
        );
    }

    public void cbsClusterClearAlarm(boolean remoteClusterAlarm) {
        final String methodName = "cbsClusterClearAlarm(boolean)";
        int localClusterId = clusterId;
        if (remoteClusterAlarm) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds.removeIf(t -> t == clusterId);
            if (!clusterIds.isEmpty()) {
                localClusterId = clusterIds.get(0);
            }
        }
        logger.info(methodName, "Clearing Critical CouchBase Alarm for Cluster", localClusterId);
        String moInstanceInfo = getMoInstanceInfoQueryFQDN(localClusterId);
        KnAlarmGeneratorUtil.generateAlarm(
                KnAlarmConstants.CBS_CLUSTER_UPDATE_FAILURE_ALARMCODE,
                KnAlarmConstants.SEVERITY_CLEAR,
                moInstanceInfo
        );
    }

    public static void generateCBSTimeoutAlarm(Exception exception) {
        String methodName = "generateCBSTimeoutAlarm(Exception)";
        if (isActualCBSIssue(exception)) {
            logger.error(methodName, exception);
            generateCBSTimeoutAlarm();
        } else {
            logger.info(methodName, "No CBS alarm raised for exception : ", exception);
        }
    }

    private static boolean isActualCBSIssue(Throwable exception) {
        String methodName = "isActualCBSIssue(Throwable)";
        for (Throwable current = exception; current != null; current = current.getCause()) {
            if (current instanceof TimeoutException
                    || current instanceof AmbiguousTimeoutException
                    || current instanceof UnambiguousTimeoutException) {
                return true;
            }

            if (current instanceof RequestCanceledException && hasCBSConnectivityMessage(current.getMessage())) {
                return true;
            }
        }

        if (exception != null) {
            logger.warn(methodName, "Skipping CBS timeout alarm for non-CBS exception type : ", exception.getClass());
        }
        return false;
    }

    private static boolean hasCBSConnectivityMessage(String errorMsg) {
        if (errorMsg == null) {
            return false;
        }

        String normalizedMessage = errorMsg.toUpperCase(Locale.ROOT);
        return normalizedMessage.contains("TIMEOUT")
                || normalizedMessage.contains("SHUTDOWN")
                || normalizedMessage.contains("OFFLINE")
                || normalizedMessage.contains("DISCONNECTED");
    }

    private static void generateCBSTimeoutAlarm() {
        String methodName = "generateCBSTimeoutAlarm()";
        atomicInteger.incrementAndGet();
        logger.info(methodName," number of CB query time out: ",atomicInteger.get());
        if(atomicInteger.get()==3){
            int localClusterId = clusterId;
            if (remoteCluster) {
                List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
                clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
                localClusterId = clusterIds.get(0);
            }
            logger.info(methodName, "Below calling CouchBase query timeout Alarm for Cluster  ", localClusterId);
            String moInstanceInfo = getMoInstanceInfo(localClusterId);
            KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.CBS_QUERY_TIMEOUT, KnAlarmConstants.SEVERITY_CRITICAL, moInstanceInfo);
            atomicInteger.set(0);
        }
    }

    private static void clearCBSTimeoutAlarm() {
        String methodName = "clearCBSTimeoutAlarm()";
        int localClusterId = clusterId;
        logger.info(methodName, "TODO:-   localClusterId", localClusterId
             , " clusterId - " , clusterId , " remoteCluster," , remoteCluster );
        if (remoteCluster) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            localClusterId = clusterIds.get(0);
        }
        logger.info(methodName, "Clearing CouchBase query timeout Alarm for Cluster  ", localClusterId);
        String moInstanceInfo = getMoInstanceInfo(localClusterId);
        KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.CBS_QUERY_TIMEOUT, KnAlarmConstants.SEVERITY_CLEAR, moInstanceInfo);
    }

    private static void clearCBSTimeoutAlarmQueryFQDN() {
        String methodName = "clearCBSTimeoutAlarmQueryFQDN()";
        int localClusterId = clusterId;
        logger.info(methodName, "TODO:-   localClusterId", localClusterId
                , " clusterId - " , clusterId , " remoteCluster," , remoteCluster );
        if (remoteCluster) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            localClusterId = clusterIds.get(0);
        }
        logger.info(methodName, "Clearing CouchBase query timeout Alarm for Cluster  ", localClusterId);
        String moInstanceInfo = getMoInstanceInfo(localClusterId);
        KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.CBS_QUERY_TIMEOUT, KnAlarmConstants.SEVERITY_CLEAR, moInstanceInfo);
    }

    private static String getMoInstanceInfo(int localClusterId) {
        String methodName = "getMoInstanceInfo(clusterId)";
        String moInstanceInfo = null;
        String sourceIpAdd = System.getenv(KnConstants.LOCAL_IP_ADDRESS);
        String destIpAddPri = cbQueryFQDNMap.get(localClusterId);
        logger.debug(methodName, "sourceIpAdd == ", sourceIpAdd, " destIpAddPri == ", destIpAddPri);
        if (sourceIpAdd != null && destIpAddPri != null) {
            moInstanceInfo = sourceIpAdd.concat(KnConstants.COLON).concat(destIpAddPri);
        }
        logger.debug(methodName, "MoInstanceInfo == ", moInstanceInfo);
        return moInstanceInfo;
    }

    private static String getMoInstanceInfoQueryFQDN(int localClusterId) {
        String methodName = "getMoInstanceInfoQueryFQDN(clusterId)";
        String moInstanceInfo = null;
        String sourceIpAdd = System.getenv(KnConstants.LOCAL_IP_ADDRESS);
        String destIpAddPri = cbQueryFQDNMap.get(localClusterId);
        logger.debug(methodName, "sourceIpAdd == ", sourceIpAdd, " destIpAddPri == ", destIpAddPri);
        if (sourceIpAdd != null && destIpAddPri != null) {
            moInstanceInfo = sourceIpAdd.concat(KnConstants.COLON).concat(destIpAddPri);
        }
        logger.debug(methodName, "MoInstanceInfo == ", moInstanceInfo);
        return moInstanceInfo;
    }


    public <T> MutationResult saveDocument(String docId, T doc)
    {
        String methodName = "saveDocument(docId, doc, collectionName, repoBucket)";
        MutationResult resDoc=null;
        try {
            resDoc = pocDataBucketdfcol.upsert(docId, doc, UpsertOptions.upsertOptions().timeout(Duration.ofMillis(2500)));
            prevFailureTimestamp = 0;
        }catch(RequestCanceledException rce){
            logger.error(methodName,rce.getMessage());
            String errorMsg = rce.getMessage().toString();
            if(errorMsg.contains("SHUTDOWN")||errorMsg.contains("OFFLINE")||errorMsg.contains("DISCONNECTED")){
                 pocbucketdisableFlag = true;
               // reInitSdk();
            }
            throw new RuntimeException(errorMsg);
        }
        return resDoc;
    }

    public MutateInResult updateDocument(String docId, List<MutateInSpec> specs)
    {
        String methodName = "updateDocument(docId, specs)";
        MutateInResult result = null;
        try {
            result = pocDataBucketdfcol.mutateIn(docId, specs, MutateInOptions.mutateInOptions().timeout(Duration.ofMillis(2500)));
            prevFailureTimestamp = 0;
        } catch (RequestCanceledException rce) {
            logger.error(methodName, rce.getMessage());
            String errorMsg = rce.getMessage().toString();
            if (errorMsg.contains("SHUTDOWN") || errorMsg.contains("OFFLINE") || errorMsg.contains("DISCONNECTED")) {
                pocbucketdisableFlag = true;
            }
            throw new RuntimeException(errorMsg);
        }
        return result;
    }

    public GetResult findDocument(String docId, String collectionName, KnRepoBucket repoBucket){

        String methodName = "findDocument(docId, collectionName, repoBucket)";
        GetResult resDoc=null;
        try {
            resDoc = getCollection(collectionName, repoBucket).get(docId, GetOptions.getOptions().timeout(Duration.ofMillis(2500)));
            prevFailureTimestamp = 0;
        }catch(RuntimeException rce)
        {
            logger.error(methodName, "Get document failed docname:"+docId+" Exception:"+rce);
            logger.error(methodName,rce.getMessage());
            String errorMsg = rce.getMessage();
            if(errorMsg.contains("SHUTDOWN")||errorMsg.contains("OFFLINE")||errorMsg.contains("DISCONNECTED")){
                pocbucketdisableFlag = true;
                // reInitSdk();
            }
        }
        catch(Exception ex)
        {
            logger.error(methodName,  "Get document failed docname:"+docId+" Exception:"+ ex);
        }
        logger.debug(methodName,"Done");
        return resDoc;
    }

    public MutationResult removeDocument(String docId, String collectionName, KnRepoBucket repoBucket){
        String methodName = "removeDocument(docId, collectionName, repoBucket)";
        MutationResult resDoc=null;
        try {
            resDoc = getCollection(collectionName, repoBucket).remove(docId, RemoveOptions.removeOptions().timeout(Duration.ofMillis(2500)));
            prevFailureTimestamp = 0;
        }catch(RuntimeException rce)
        {
            logger.error(methodName, "Remove document failed docname:"+docId+" Exception:"+rce);
            logger.error(methodName,rce.getMessage());
            String errorMsg = rce.getMessage();
            if(errorMsg.contains("SHUTDOWN")||errorMsg.contains("OFFLINE")||errorMsg.contains("DISCONNECTED")){
                pocbucketdisableFlag = true;
                // reInitSdk();
            }
           /// enableflag;
        }
        catch(Exception ex)
        {
            logger.error(methodName,  "Remove document failed docname:"+docId+" Exception:"+ ex);
        }
        logger.debug(methodName,"Done");
        return resDoc;
    }


    public QueryResult getQueryResult(String query, JsonObject placeholderValues) {
        String methodName="getQueryResult(query,placeholderValues)";
        QueryResult queryResult=null;
        try{
            if(placeholderValues != null) {
                queryResult = cluster.query(query, QueryOptions.queryOptions().parameters(placeholderValues).timeout(Duration.ofMillis(7500)));
            }
        }catch(RuntimeException rce){
            logger.error(methodName, "getQueryResult failed query:"+query+" Exception:"+rce);
          //  reInitSdkOnException();
            logger.error(methodName,rce.getMessage());
            String errorMsg = rce.getMessage();
            if (errorMsg.contains("SHUTDOWN") || errorMsg.contains("OFFLINE") || errorMsg.contains("DISCONNECTED")) {
                pocbucketdisableFlag = true;
                // reInitSdk();
            }
        }catch(Exception exp){
            logger.error(methodName,  "Failed to execute the query:"+query+" placeholderValues:"+ placeholderValues);
            logger.error(methodName, "Exception:"+exp);
        }
        logger.debug(methodName,queryResult);
        logger.debug(methodName,"Done");
        return queryResult;
    }

    public void closePrevCouchBaseConnection() {
        String methodName = "closePrevCouchBaseConnection()";
        try {

            if (cluster != null) {
                closeCluster();
                pocbucket=null;
                pocDataBucketdfcol=null;
            }
            if (env != null) {
                env.shutdown();
            }
            logger.debug(methodName, "Closed the existing connection if opened");
        } catch (Exception e) {
            logger.error(methodName, "Exception occurred in closing Connection",e.getMessage());
        }
    }
    private void closeCluster() {
        String methodName = "closeCluster()";
        try {
            logger.info(methodName, " Cluster disconnect is called. - ", cluster);
            cluster.disconnect();
        } catch (Exception e) {
            logger.error(methodName, "Exception occurred in closing Connection", e);
        }
    }

}
