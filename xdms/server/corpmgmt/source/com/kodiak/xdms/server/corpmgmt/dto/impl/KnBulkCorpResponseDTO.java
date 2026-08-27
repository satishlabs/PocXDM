/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpResponseDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
 * <p/>
 * <p/>
 * Copyright (c) 2006  Kodiak Networks (India) Pvt. Ltd.
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
package com.kodiak.xdms.server.corpmgmt.dto.impl;


import com.kodiak.xdms.server.corpmgmt.dto.ICorpResponseDTO;

import java.util.Collection;
import java.util.Map;

public class KnBulkCorpResponseDTO extends KnCorpResponseDTO implements ICorpResponseDTO {

    private Map<Integer,Collection<String>> enabledDispatchMemListMap;

    public Map<Integer, Collection<String>> getEnabledDispatchMemListMap() {
        return enabledDispatchMemListMap;
    }

    public void setEnabledDispatchMemListMap(Map<Integer, Collection<String>> enabledDispatchMemListMap) {
        this.enabledDispatchMemListMap = enabledDispatchMemListMap;
    }

    @Override
    public String toString() {
        return "KnBulkCorpResponseDTO{" +
                "enabledDispatchMemListMap=" + enabledDispatchMemListMap +
                '}';
    }
}
