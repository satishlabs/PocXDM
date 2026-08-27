/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Map;

public class KnXDMSubsStatsRespDTO extends KnXDMCorpRespDTO{
    private static final long serialVersionUID = -7390621870229082301L;

    private Integer activatedSubscribers;
    private Integer deactivateSubscribers;
    private Integer provisionedSubscribers;
    private Map<Integer, Integer> ClientTypeSubscribers;

    public Integer getActivatedSubscribers() {
        return activatedSubscribers;
    }

    public void setActivatedSubscribers(Integer activatedSubscribers) {
        this.activatedSubscribers = activatedSubscribers;
    }

    public Integer getDeactivateSubscribers() {
        return deactivateSubscribers;
    }

    public void setDeactivateSubscribers(Integer deactivateSubscribers) {
        this.deactivateSubscribers = deactivateSubscribers;
    }

    public Integer getProvisionedSubscribers() {
        return provisionedSubscribers;
    }

    public void setProvisionedSubscribers(Integer provisionedSubscribers) {
        this.provisionedSubscribers = provisionedSubscribers;
    }

    public Map<Integer, Integer> getClientTypeSubscribers() {
        return ClientTypeSubscribers;
    }

    public void setClientTypeSubscribers(Map<Integer, Integer> clientTypeSubscribers) {
        ClientTypeSubscribers = clientTypeSubscribers;
    }

    @Override
    public String toString() {
        return "KnXDMSubsStatsRespDTO{" +
                "activatedSubscribers=" + activatedSubscribers +
                ", deactivateSubscribers=" + deactivateSubscribers +
                ", provisionedSubscribers=" + provisionedSubscribers +
                ", ClientTypeSubscribers=" + ClientTypeSubscribers +
                '}';
    }
}