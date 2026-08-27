/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.cb.util;


import java.util.*;

import com.couchbase.client.core.env.SeedNode;
import com.couchbase.client.core.retry.BestEffortRetryStrategy;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.SerializableTranscoder;
import com.couchbase.client.java.env.ClusterEnvironment;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.json.JsonValueModule;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.kv.PersistTo;
import com.couchbase.client.java.kv.ReplicateTo;
import com.couchbase.client.java.kv.UpsertOptions;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnEncryptionDecryptionUtil;
import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnMicroSvcsCommonConfig;


import java.time.Duration;
import java.util.stream.Collectors;

import static com.couchbase.client.java.ClusterOptions.clusterOptions;


import static com.kodiak.common.resources.KnConstants.*;

public class KnCouchDbManager {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCouchDbManager.class);
    private static Map<Integer, String> cbFQDNMap = new LinkedHashMap<>();
    private static Map<Integer, String> cbQueryFQDNMap = new LinkedHashMap<>();
    private static Map<Integer, String> pocBucketNameMap = new LinkedHashMap<>();
    private static Map<Integer, String> pocBucketPwdMap = new LinkedHashMap<>();
    private static Map<String, String> paramNameValueMapService = new LinkedHashMap<>();
    private static Cluster cluster;
    private static Bucket bucket;
    private static ClusterEnvironment env;
    private static KnCouchDbManager cbMgr;
    private static KnGenInfoUtil knGenInfoUtil;
    private static int clusterId;
    private static Collection coll;
    private long prevFailureTimestamp=0;
    public static final long THRESHOLDTIME_INTERVAL = 5*60*1000;

    private Map<String, byte[]> keyMap = new HashMap<>();

    private KnCouchDbManager() {
        init();
    }

    public static synchronized KnCouchDbManager getInstance() {
        knGenInfoUtil = KnGenInfoUtil.getInstance();
        if (cbMgr == null) {
            cbMgr = new KnCouchDbManager();
        }
        return cbMgr;
    }

    private synchronized void init() {
        String methodName = "initCBConnection()";
        knLogger.info(methodName, "ENTRY:");
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

         /*   cbFQDNMap.keySet().forEach(clusterId -> {
                String moInstanceInfo = getMoInstanceInfo(clusterId);
                KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_CONNECT_CB, KnAlarmConstants.SEVERITY_CLEAR, moInstanceInfo);
                clusterAlarmMap.put(clusterId, 1);
            });
*/
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "exception while fetching common config details - ", e);
            KnDbUtil.rollback(knPersisterTxn);
            
        } catch (Exception e) {
            knLogger.error(methodName, "exception while processing - ", e);
            
            KnDbUtil.rollback(knPersisterTxn);
        }
        knLogger.info(methodName, "EXIT:");
    }

    public Bucket getBucket() {
        return bucket;
    }
    public Collection getCollection() {
        return coll;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public static void connectBucket(boolean remoteClusterConnect) {
        String methodName = "connectBucket(remoteConnect)";
        String decryptPassword = null;
        int retryCounter = 3; //try 3 times to acuire a cb connection else raise a fatal log
        long timeInMillis = 3000;
        KnPersisterTxn knPersisterTxn = null;
        knLogger.info(methodName, "ENTRY :", remoteClusterConnect);
        if (cbMgr != null) {
            knLogger.info(methodName, "CB Manager is not null");
            long startTime = 0l;
            long endTime = 0l;
            //  startTime = System.currentTimeMillis();
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            if (remoteClusterConnect) {
                clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            } else {
                clusterIds = clusterIds.stream().filter(t -> (t == clusterId)).collect(Collectors.toList());
            }
            outer:
            for (int clusterId : clusterIds) {
                knLogger.info(methodName + ", sizeof for cluster ", clusterIds.size());
                for (int i = 1; i <= retryCounter; i++) {
                    try {
                        knLogger.info(methodName + ", Trying to acquire bucket for cluster ", clusterId + " in loop count : " + i);
                        /*knPersisterTxn = KnPersisterTxn.getPersisterTxn();
                        knPersisterTxn.open();
                        paramNameValueMapService = knGenInfoUtil.retrieveMSSvcsServiceConfig(clusterId, knPersisterTxn);
                        knPersisterTxn.save();*/
                        String cbsBucketPocDataUser = KnCommonVaultUtil.getKeyFromVault(CBS_BUCKET_POCDATA_USER_NAME_PATH, KnConstants.CBS_BUCKET_POCDATA_USER);
                        String cbsBucketPocDataPw = KnCommonVaultUtil.getKeyFromVault(CBS_BUCKET_POCDATA_PW_PATH, KnConstants.CBS_BUCKET_POCDATA_PW);

                        cluster = Cluster.connect(cbQueryFQDNMap.get(clusterId), ClusterOptions.clusterOptions(cbsBucketPocDataUser, cbsBucketPocDataPw));
                        bucket = cluster.bucket(pocBucketNameMap.get(clusterId));

                        knLogger.info(methodName + ", bucket name for cluster ", bucket.name());

                        coll = bucket.defaultCollection();


                        if (bucket != null) {
                            clearAlarm(remoteClusterConnect);
                            knLogger.info(methodName, "Acquired Bucket for cluster : ", clusterId, " in loop collection : ", coll.toString(),"FQDN :",cbQueryFQDNMap.get(clusterId));
                            break outer;
                        }
                    } catch (Exception e) {
                        knLogger.error(methodName, "Bucket is null for cluster : ", clusterId, " Exception message  : ", e.getMessage());
                        KnDbUtil.rollback(knPersisterTxn);
                        if (i >= retryCounter) {
                            knLogger.fatal(methodName + ", service is unable to get couchbase bucket cluster : ", clusterId, "Exception occurred ", e);
                        }
                    }
                }
            }
            // endTime = System.currentTimeMillis();
            knLogger.info(methodName, "Exit: bucket : ", bucket);
        }
    }


    public static void clearAlarm(boolean remoteClusterAlarm) {
        String methodName = "clearAlarm()";
        if (remoteClusterAlarm) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            clusterId = clusterIds.get(0);
        }
        knLogger.info(methodName, "Clearing Critical CouchBase Alarm for Cluster  ", clusterId);
        String moInstanceInfo = getMoInstanceInfo(clusterId);
        KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_CONNECT_CB, KnAlarmConstants.SEVERITY_CLEAR, moInstanceInfo);
    }

    public static void generateAlarm(boolean remoteClusterAlarm) {
        String methodName = "generateAlarm()";
        if (remoteClusterAlarm) {
            List<Integer> clusterIds = new ArrayList<>(cbQueryFQDNMap.keySet());
            clusterIds = clusterIds.stream().filter(t -> (t != clusterId)).collect(Collectors.toList());
            clusterId = clusterIds.get(0);
        }
        knLogger.info(methodName, "Below calling critical CouchBase Alarm for Cluster  ", clusterId);
        String moInstanceInfo = getMoInstanceInfo(clusterId);
        KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_CONNECT_CB, KnAlarmConstants.SEVERITY_CRITICAL, moInstanceInfo);
    }

    private static String getMoInstanceInfo(int clusterId) {
        String methodName = "getMoInstanceInfo(clusterId)";
        String moInstanceInfo = null;
        String sourceIpAdd = System.getenv(KnConstants.LOCAL_IP_ADDRESS);
        String destIpAddPri = cbQueryFQDNMap.get(clusterId);
        knLogger.debug(methodName, "sourceIpAdd == ", sourceIpAdd, " destIpAddPri == ", destIpAddPri);
        if (sourceIpAdd != null && destIpAddPri != null) {
            moInstanceInfo = sourceIpAdd.concat(KnConstants.COLON).concat(destIpAddPri);
        }
        knLogger.debug(methodName, "MoInstanceInfo == ", moInstanceInfo);
        return moInstanceInfo;
    }

    public static  <T> MutationResult saveDocument(String docId, T doc)
    {
        String methodName = "saveDocument(docId, doc, collectionName, repoBucket)";
        MutationResult resDoc=null;
        try {
            UpsertOptions options = UpsertOptions.upsertOptions()
                    .expiry(Duration.ofSeconds(25))
                    .timeout(Duration.ofMillis(25000))
                    .retryStrategy(BestEffortRetryStrategy.withExponentialBackoff(Duration.ofMillis(25000),
                            Duration.ofMillis(25000), 5));
            resDoc = coll.upsert(docId, doc, options);

        }catch(RuntimeException runTimeException)
        {
            knLogger.error(methodName, "Update document failed docname:"+docId+" Exception:"+runTimeException);
            //reInitSdkOnException();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            knLogger.error(methodName,  "update document failed docname:"+docId+" Exception:"+ ex);
        }
        knLogger.debug(methodName,"Done");
        return resDoc;
    }

    public static QueryResult getQueryResult(String query, JsonObject placeholderValues) {
        String methodName="getQueryResult(query,placeholderValues)";
        QueryResult queryResult=null;
        try{
            if(placeholderValues != null) {
                queryResult = cluster.query(query, QueryOptions.queryOptions().parameters(placeholderValues).timeout(Duration.ofMillis(25000)));
            }
        }catch(RuntimeException runTimeException){
            knLogger.error(methodName, "getQueryResult failed query:"+query+" Exception:"+runTimeException);
            closePrevCouchBaseConnection();
            connectBucket(false);
        }catch(Exception exp){
            knLogger.error(methodName,  "Failed to execute the query:"+query+" placeholderValues:"+ placeholderValues);
            knLogger.error(methodName, "Exception:"+exp);
        }
        knLogger.debug(methodName,"Done");
        return queryResult;
    }
    public static void closePrevCouchBaseConnection() {
        String methodName = "closePrevCouchBaseConnection()";
        try {

            if (cluster != null) {
                cluster.disconnect();
            }
            if (env != null) {
                env.shutdown();
            }
            knLogger.debug(methodName, "Closed the existing connection if opened");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred in closing Connection");
        }
    }

}
