/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;

import static com.kodiak.utilities.generatealarmutil.KnAlarmConstants.*;
import static com.kodiak.common.resources.KnConstants.XDMMANAGEDOBJECT_CLASSTYPE;


/**
 * @author SunilBiradar
 * Below class defines the way of grid Gain service monitoring
 */
public class KnGridGainMonitor implements Runnable {

    private static KnJobConstants.SERVICE_STATES state = KnJobConstants.SERVICE_STATES.SERVICE_INIT_STATE;
    private static final KnLogger knLogger = KnLogger.getLogger(KnGridGainMonitor.class);

    public static KnGridGainMonitor getInstance() {
        return new KnGridGainMonitor();
    }

    @Override
    public void run() {
        doMonitor();
    }

    private void doMonitor() {
        String methodName = "doMonitor()";
        knLogger.info(methodName, "Starting health check for GridGain service");
        try {
            String ggHealthCheckup = getGgHealthCheckup();
            knLogger.info(methodName, "State: " + state + ", GridGain HealthCheckup: " + ggHealthCheckup);

            boolean isUp = "passing".equalsIgnoreCase(ggHealthCheckup);
            KnJobConstants.SERVICE_STATES newState = isUp ?
                    KnJobConstants.SERVICE_STATES.SERVICE_UP_STATE :
                    KnJobConstants.SERVICE_STATES.SERVICE_DOWN_STATE;

            if (state != newState) {
                state = KnJobConstants.SERVICE_STATES.SERVICE_INIT_STATE;
                try (Connection conn = KnGGConnection.getDBConnection();
                     PreparedStatement pStmt = conn.prepareStatement("SELECT COUNT(*) FROM DG.ASYNC_JOB_NOTIFY")) {
                    pStmt.executeQuery();
                    KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_GET_CONNECTION_FROM_GG, isUp ? SEVERITY_CLEAR : SEVERITY_CRITICAL,
                            XDMMANAGEDOBJECT_CLASSTYPE, "KnGGConnection");
                    state = newState;
                    knLogger.info(methodName, "Updated State: " + state);
                } catch (Exception e) {
                    KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.UNABLE_TO_GET_CONNECTION_FROM_GG, KnAlarmConstants.SEVERITY_CLEAR,
                            XDMMANAGEDOBJECT_CLASSTYPE, "KnGGConnection");
                    knLogger.error(methodName, "Exception in GridGain Monitor:", e);
                    state = KnJobConstants.SERVICE_STATES.SERVICE_DOWN_STATE;
                }
            }
        } catch (Throwable e) {
            knLogger.error(methodName, "Error in GridGain Monitor:", e);
            state = KnJobConstants.SERVICE_STATES.SERVICE_DOWN_STATE;
        }
    }

    private String getGgHealthCheckup() {
        String methodName = "getGgHealthCheckup";
        knLogger.debug(methodName, "Entry");
        String response = null;
        try {
            String url = "http://localhost:8500/v1/health/service/prod-GRIDGAIN-V1";
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> responseEntity = template.exchange(url, HttpMethod.GET, entity, String.class);
            if (responseEntity != null) {
                response = getStatusFromOutput(responseEntity.getBody());
            }
        } catch (IOException e) {
            knLogger.error("Exception parsing response: " + response, e);
        }
        knLogger.info(methodName, "GET response: " + response);
        return response;
    }

    private static String getStatusFromOutput(String inputJson) throws IOException {
        if (inputJson == null || inputJson.isBlank())
            return "Failed";
        ObjectMapper mapper = new ObjectMapper();
        HashMap[] object = mapper.readValue(inputJson, HashMap[].class);
        for (HashMap hm : object) {
            List<HashMap> checks = (List<HashMap>) hm.get("Checks");
            if (checks == null) continue;
            for (HashMap internalHashMap : checks) {
                if ("service:prod-GRIDGAIN-V1".equalsIgnoreCase(String.valueOf(internalHashMap.get("CheckID")))) {
                    return String.valueOf(internalHashMap.get("Status"));
                }
            }
        }
        return "Failed";
    }
}
