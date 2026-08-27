/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;


/**
 * Meta data for a cron job
 * @author bbinil
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value= Include.NON_NULL)
public class KnJobMetaData
{
	private int op_type;
	private String jobKey;
	private int triggerType;
	private Map<String,Object> dataMap;
	private String routingKey;
	private List<Integer> trgTimingsList;

	public KnJobMetaData(int op_type, String jobKey, int triggerType, Map<String, Object> dataMap, String routingKey, List<Integer> trgTimingsList) {
		this.op_type = op_type;
		this.jobKey = jobKey;
		this.triggerType = triggerType;
		this.dataMap = dataMap;
		this.routingKey = routingKey;
		this.trgTimingsList = trgTimingsList;
	}

	@JsonProperty("op")
	public int getOp_type() {
		return op_type;
	}
	@JsonProperty("op")
	public void setOp_type(int op_type) {
		this.op_type = op_type;
	}

	@JsonProperty("jobKey")
	public String getJobKey() {
		return jobKey;
	}
	@JsonProperty("jobKey")
	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	@JsonProperty("trgType")
	public int getTriggerType() {
		return triggerType;
	}
	@JsonProperty("trgType")
	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	@JsonProperty("dataMap")
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	@JsonProperty("dataMap")
	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	@JsonProperty("tgrRoutingKey")
	public String getRoutingKey() {
		return routingKey;
	}
	@JsonProperty("tgrRoutingKey")
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	@JsonProperty("triggerTimingList")
	public List<Integer> getTrgTimingsList() {
		return trgTimingsList;
	}
	@JsonProperty("triggerTimingList")
	public void setTrgTimingsList(List<Integer> trgTimingsList) {
		this.trgTimingsList = trgTimingsList;
	}
}
