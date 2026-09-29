/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;

/**
 * Slim notification payload published to XDMDataIntf when a corp group is rehomed.
 * <p>
 * Exact wire format (only non-null fields are serialized):
 * <pre>
 * {
 *   "id":             "GroupRehome_&lt;GroupId&gt;",
 *   "type":           "groupRehome",
 *   "ver":            "1.0",
 *   "grpId":          5,
 *   "corpid":         1,
 *   "lmrInteropFlag": 1,
 *   "pocHome":        "123"
 * }
 * </pre>
 * <p>
 * {@code id}, {@code type} and {@code ver} are inherited from {@link KnEXDMSNotifyDto}.
 * Only the rehome-specific fields are declared here, keeping the payload intentionally
 * lean compared to {@code KnCorpEXDMSNotifyDto}.
 * <p>
 * The inherited {@code notifyEventType} is still populated in memory (it is required by
 * {@code KnMicroServiceNotifyJob#executeTask()} to pick the AMQP routing key for group
 * events &mdash; auto-unboxing a null would NPE), but it is suppressed from the JSON
 * wire payload via {@link JsonIgnore} on the overridden getter so the emitted JSON
 * contains exactly the seven keys listed above.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class KnCorpRehomeNotifyDto extends KnEXDMSNotifyDto {

    private Integer grpId;
    private Integer corpid;
    private Integer lmrInteropFlag;
    private String pocHome;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden solely to add {@link JsonIgnore} so {@code notifyEventType} stays
     * available in memory for {@code KnMicroServiceNotifyJob} to route the message,
     * but is <strong>not</strong> emitted on the wire &mdash; keeping the rehome
     * payload at exactly the seven documented keys.
     */
    @Override
    @JsonIgnore
    public Integer getNotifyEventType() {
        return super.getNotifyEventType();
    }

    public Integer getGrpId() {
        return grpId;
    }

    public void setGrpId(Integer grpId) {
        this.grpId = grpId;
    }

    public Integer getCorpid() {
        return corpid;
    }

    public void setCorpid(Integer corpid) {
        this.corpid = corpid;
    }

    public Integer getLmrInteropFlag() {
        return lmrInteropFlag;
    }

    public void setLmrInteropFlag(Integer lmrInteropFlag) {
        this.lmrInteropFlag = lmrInteropFlag;
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    @Override
    public String toString() {
        return "KnCorpRehomeNotifyDto{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", ver='" + getVer() + '\'' +
                ", grpId=" + grpId +
                ", corpid=" + corpid +
                ", lmrInteropFlag=" + lmrInteropFlag +
                ", pocHome='" + pocHome + '\'' +
                '}';
    }
}
