/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMLINotifyEventReqDTO.java
 * Subsystem:  XDM
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Shashank Tewari            March 10, 2021                11.2
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnXDMLINotifyEventReqDTO{

	@JsonProperty("Payload")
	private KnXDMLIPayload payload;

	public KnXDMLIPayload getPayload() {
		return payload;
	}

	public void setPayload(KnXDMLIPayload payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "KnXDMLINotifyEventReqDTO{" +
				"payload=" + payload +
				'}';
	}
}



