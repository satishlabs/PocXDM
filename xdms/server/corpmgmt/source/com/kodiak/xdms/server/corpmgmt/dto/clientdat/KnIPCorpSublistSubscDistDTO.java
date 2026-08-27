/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSublistSubscriberDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.Map;


public class KnIPCorpSublistSubscDistDTO extends KnIPCorpInfoDTO {

    private static final long serialVersionUID = 7526471155622676195L;

    private Collection<String> mdnList;
    private Collection<Integer> sublistIds;
    private Map<String, Object> customParamMap;
    private String commonSublistId;

    public Collection<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(Collection<String> mdnList) {
        this.mdnList = mdnList;
    }

    public Collection<Integer> getSublistIds() {
        return sublistIds;
    }

    public void setSublistIds(Collection<Integer> sublistIds) {
        this.sublistIds = sublistIds;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getCommonSublistId() {
        return commonSublistId;
    }

    public void setCommonSublistId(String commonSublistId) {
        this.commonSublistId = commonSublistId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("mdnList - ").append(KnGDPRTemplate.mdnList(mdnList))
                .append("sublistIds - ").append(sublistIds)
                .append("customParamMap - ").append(customParamMap)
                .append("commonSublistId - ").append(commonSublistId);
        return sb.toString();
    }
}
