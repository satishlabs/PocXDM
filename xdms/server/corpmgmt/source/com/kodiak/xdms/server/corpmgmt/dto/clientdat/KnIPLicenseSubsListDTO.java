/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPLicenseSubsListDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
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
 */package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import java.util.Collection;
import java.util.Map;



public class KnIPLicenseSubsListDTO extends KnIPCorpInfoDTO {

    private static final long serialVersionUID = 7526471155622676196L;

    private String billingNumber;
    private Collection<String> markList;
    private Collection<String> unmarkList;
    private Map<String, Object> customParamMap;
    private String billingName;

    public String getBillingName() {
		return billingName;
	}

	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	public String getBillingNumber() {
        return billingNumber;
    }

    public void setBillingNumber(String billingNumber) {
        this.billingNumber = billingNumber;
    }

    public Collection<String> getMarkList() {
        return markList;
    }

    public void setMarkList(Collection<String> markList) {
        this.markList = markList;
    }

    public Collection<String> getUnmarkList() {
        return unmarkList;
    }

    public void setUnmarkList(Collection<String> unmarkList) {
        this.unmarkList = unmarkList;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }
}
