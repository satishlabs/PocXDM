/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.beans;

import java.util.Map;

import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.dto.IIdentifier;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnNotifyPayloadDTO.java
 * Subsystem:  PoC XDMS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ajit Kumar           29-Sep-2012  7.4
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
 * ************************************************************************
 */

public class KnNotifyPayloadDTO implements IIdentifier {
    private byte[] payload;
    private String pttServerId;
    private int featureId;
    private String xmlLoad;
    private String iosMdn;
    private Map<String,KnXDMSubsProvDTO> mdnMaps;
    private String docType;
    private boolean immediateIosEligible = true;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public String getObjectId() {
        return pttServerId;
    }

    public String getIosMdn() {
		return iosMdn;
	}

	public void setIosMdn(String iosMdn) {
		this.iosMdn = iosMdn;
	}

	public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" KnNotifyPayloadDTO --> ");
        strBuffer.append(" payload - ").append(payload.length)
                .append(", pttServerId - ").append(pttServerId)
                .append(", featureId - ").append(featureId);
        return strBuffer.toString();
    }

	public String getXmlLoad() {
		return xmlLoad;
	}

	public void setXmlLoad(String xmlLoad) {
		this.xmlLoad = xmlLoad;
	}

	public Map<String,KnXDMSubsProvDTO> getMdnMaps() {
		return mdnMaps;
	}

	public void setMdnMaps(Map<String,KnXDMSubsProvDTO> mdnMaps) {
		this.mdnMaps = mdnMaps;
	}

    public boolean isImmediateIosEligible() {
        return immediateIosEligible;
    }

    public void setImmediateIosEligible(boolean immediateIosEligible) {
        this.immediateIosEligible = immediateIosEligible;
    }
}
