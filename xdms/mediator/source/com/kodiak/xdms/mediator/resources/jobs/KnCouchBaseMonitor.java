/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import com.kodiak.xdms.server.common.cb.util.KnCBSRepository;
import com.kodiak.xdms.server.common.cb.util.KnCouchDbManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Shashank
 * Below class defines the way of couchBase service monitoring
 */
public class KnCouchBaseMonitor implements Runnable {

    private static KnJobConstants.SERVICE_STATES state = KnJobConstants.SERVICE_STATES.SERVICE_INIT_STATE;
    private static final KnLogger knLogger = KnLogger.getLogger(KnCouchBaseMonitor.class);
    private static KnCBSRepository cbMgr;
    public KnCouchBaseMonitor(){
        cbMgr = KnCBSRepository.getInstance();
        if (cbMgr != null) {
            //logging the successful initialization of KnCBSRepository instance
            knLogger.info("KnCBSRepository instance initialized successfully. CBS cluster status updates will be performed");
            cbMgr.initCBSdkQueryFQDN(false);
        }else {
            //logging the warning if KnCBSRepository instance is null
            knLogger.warn("KnCBSRepository instance is null. CBS cluster status updates will be skipped.");
        }
    }

    @Override
    public void run() {
        // doMonitor();
    }

    private void doMonitor() {
        String methodName = "doMonitor()";
        knLogger.info(methodName, "Starting health check for CouchBase");
        try {
            if (cbMgr != null) {
                String cbHealthCheckUP = getCBHealthCheckUP();
                knLogger.info(methodName, "State : " ,state +" and latest local CouchBase HealthCheckUP value is: " + cbHealthCheckUP,
                        " pocbucketdisableFlag ", KnCBSRepository.pocbucketdisableFlag);
                //   String serviceStatus = getStatusFromOutput(cbHealthCheckUP);
                if (cbHealthCheckUP.equalsIgnoreCase("passing")) {
                    if (state != KnJobConstants.SERVICE_STATES.SERVICE_UP_STATE) {
                        state = KnJobConstants.SERVICE_STATES.SERVICE_INIT_STATE;
                        // clear Alarm alert for local cluster
                        cbMgr.clearAlarm(false);
                        // close the prev connection
                        cbMgr.closePrevCouchBaseConnection();
                        // initialize  bucket with local cluster
                        cbMgr.initCBSdk(false);
                        if(!KnCBSRepository.pocbucketdisableFlag) {
                            state = KnJobConstants.SERVICE_STATES.SERVICE_UP_STATE;
                            knLogger.info(methodName, "updated State : " ,state);
                        }
                    }
                    if(KnCBSRepository.pocbucketdisableFlag){
                        cbMgr.clearAlarm(true);
                        cbMgr.closePrevCouchBaseConnection();
                        // initialize  bucket with local cluster
                        cbMgr.initCBSdk(false);
                    }
                } else {
                    if (state != KnJobConstants.SERVICE_STATES.SERVICE_DOWN_STATE) {
                        state = KnJobConstants.SERVICE_STATES.SERVICE_INIT_STATE;
                        // Generate Alert for local cluster
                        cbMgr.generateAlarm(false);
                        cbMgr.clearAlarm(true);
                        // close the prev connection
                        cbMgr.closePrevCouchBaseConnection();
                        // reinitialize  bucket with remote cluster
                        cbMgr.initCBSdk(true);
                        if(!KnCBSRepository.pocbucketdisableFlag)
                            state = KnJobConstants.SERVICE_STATES.SERVICE_DOWN_STATE;
                    }
                    if(KnCBSRepository.pocbucketdisableFlag){
                        cbMgr.generateAlarm(true);
                        cbMgr.clearAlarm(false);
                        cbMgr.closePrevCouchBaseConnection();
                        // initialize  bucket with local cluster
                        cbMgr.initCBSdk(true);
                    }
                }
            } else {
                knLogger.info(methodName, "CB Manager is not initialized yet" );
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured in CouchBase Monitor:", e);
            state = KnJobConstants.SERVICE_STATES.SERVICE_DOWN_STATE;
        } catch (Throwable e) {
            knLogger.error(methodName, "Error occured in CouchBase Monitor:", e);
            state = KnJobConstants.SERVICE_STATES.SERVICE_DOWN_STATE;
        }
    }

    /**
     * API to get the document and return in Form of JSONObject
     */
    private String getCBHealthCheckUP() {
        String methodName = "getCBHealthCheckUP(String )";
        knLogger.debug(methodName, "Entry: ");
        String response = null;
        try {
            String url = "http://localhost:8500/v1/health/service/prod-CBS-V1";
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            RestTemplate template = new RestTemplate();
            knLogger.debug(methodName + ", Before request Entity is proper");
            ResponseEntity<String> responseEntity = template.exchange(url, HttpMethod.GET, entity, String.class);
            if (responseEntity != null) {
                response = getStatusFromOutput(responseEntity.getBody());
            }
        } catch (IOException e) {
            knLogger.error("Exception occured while parsing response :" + response + "  ", e);
        }
        knLogger.info(methodName, "GET response:", response);
        return response;
    }

    private static String getStatusFromOutput(String inputJson) throws IOException {
        //String s = "[{\"Node\":{\"ID\":\"d5939e6b-296f-ca80-5d02-2b778576d890\",\"Node\":\"CBS-20\",\"Address\":\"10.2.1.36\",\"Datacenter\":\"1\",\"TaggedAddresses\":{\"lan\":\"10.2.1.36\",\"wan\":\"10.2.1.36\"},\"Meta\":{\"consul-network-segment\":\"\"},\"CreateIndex\":492554,\"ModifyIndex\":492554},\"Service\":{\"ID\":\"prod-CBS-V1\",\"Service\":\"prod-CBS-V1\",\"Tags\":[\"CBS-20\"],\"Address\":\"10.2.1.36\",\"Meta\":null,\"Port\":0,\"Weights\":{\"Passing\":1,\"Warning\":1},\"EnableTagOverride\":false,\"Proxy\":{\"MeshGateway\":{}},\"Connect\":{},\"CreateIndex\":492559,\"ModifyIndex\":492559},\"Checks\":[{\"Node\":\"CBS-20\",\"CheckID\":\"CBS_DEP_HEALTH\",\"Name\":\"CBS_DEP_HEALTH check\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"\",\"ServiceID\":\"\",\"ServiceName\":\"\",\"ServiceTags\":[],\"Definition\":{},\"CreateIndex\":492581,\"ModifyIndex\":492637},{\"Node\":\"CBS-20\",\"CheckID\":\"CONSUL_SERVER_CHECK\",\"Name\":\"\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current\\n                                 Dload  Upload   Total   Spent    Left  Speed\\n\\r  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0\\r100    16  100    16    0     0   7187      0 --:--:-- --:--:-- --:--:--  8000\\n  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current\\n                                 Dload  Upload   Total   Spent    Left  Speed\\n\\r  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0\\r100  2806    0  2806    0     0  2594k      0 --:--:-- --:--:-- --:--:-- 2740k\\n\",\"ServiceID\":\"\",\"ServiceName\":\"\",\"ServiceTags\":[],\"Definition\":{},\"CreateIndex\":492577,\"ModifyIndex\":515922},{\"Node\":\"CBS-20\",\"CheckID\":\"CONTAINER_PLATFORM_HEALTH\",\"Name\":\"CONTAINER_PLATFORM_HEALTH check\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"\",\"ServiceID\":\"\",\"ServiceName\":\"\",\"ServiceTags\":[],\"Definition\":{},\"CreateIndex\":492585,\"ModifyIndex\":492585},{\"Node\":\"CBS-20\",\"CheckID\":\"serfHealth\",\"Name\":\"Serf Health Status\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"Agent alive and reachable\",\"ServiceID\":\"\",\"ServiceName\":\"\",\"ServiceTags\":[],\"Definition\":{},\"CreateIndex\":492623,\"ModifyIndex\":492623},{\"Node\":\"CBS-20\",\"CheckID\":\"service:prod-CBS-V1\",\"Name\":\"Service 'prod-CBS-V1' check\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"\",\"ServiceID\":\"prod-CBS-V1\",\"ServiceName\":\"prod-CBS-V1\",\"ServiceTags\":[\"CBS-20\"],\"Definition\":{},\"CreateIndex\":492559,\"ModifyIndex\":492811}]}]";
        if (inputJson == null || inputJson.isBlank())
            return "Failed";
        ObjectMapper mapper = new ObjectMapper();
        //TypeFactory typeFactory = mapper.getTypeFactory();
        HashMap[] object = mapper.readValue(inputJson, HashMap[].class);
        for (HashMap hm : object) {
            if (!hm.containsKey("Checks"))
                continue;
            ;
            List<HashMap> checks = (List<HashMap>) hm.get("Checks");
            for (HashMap internalHashMap : checks) {
                if (String.valueOf(internalHashMap.get("CheckID")).equalsIgnoreCase("service:prod-CBS-V1")) {
                    return String.valueOf(internalHashMap.get("Status"));
                }
            }
        }
        return "Failed";
    }

}
