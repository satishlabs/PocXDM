package com.kodiak.xdms.server.bulkops.dto.common;

import com.kodiak.common.commdto.response.KnBulkOpsErrorDetail;

import java.util.Map;

/**
 * Aggregated result for bulk change of service auth status.
 * Contains per-MDN success responses and the failure MDNs with error details.
 */
public class KnChangeAuthStatusBulkResult {
    private Map<String, KnOPChgAuthStatusRespDTO> responses;
    private Map<String, KnBulkOpsErrorDetail> failureMdns;
    Map<String, KnOPSubsProfileInfoDTO> subscriberProfiles;


    public KnChangeAuthStatusBulkResult() {}

    public KnChangeAuthStatusBulkResult(Map<String, KnOPChgAuthStatusRespDTO> responses,
                                        Map<String, KnBulkOpsErrorDetail> failureMdns,
                                        Map<String, KnOPSubsProfileInfoDTO> subscriberProfiles) {
        this.responses = responses;
        this.failureMdns = failureMdns;
        this.subscriberProfiles = subscriberProfiles;
    }

    public Map<String, KnOPChgAuthStatusRespDTO> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, KnOPChgAuthStatusRespDTO> responses) {
        this.responses = responses;
    }

    public Map<String, KnBulkOpsErrorDetail> getFailureMdns() {
        return failureMdns;
    }

    public void setFailureMdns(Map<String, KnBulkOpsErrorDetail> failureMdns) {
        this.failureMdns = failureMdns;
    }

    public Map<String, KnOPSubsProfileInfoDTO> getSubscriberProfiles() {
        return subscriberProfiles;
    }

    public void setSubscriberProfiles(Map<String, KnOPSubsProfileInfoDTO> subscriberProfiles) {
        this.subscriberProfiles = subscriberProfiles;
    }
}

