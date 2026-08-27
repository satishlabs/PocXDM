/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     24/12/13         7.7.0
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
package com.kodiak.common.commdto.response;

import java.util.List;

public class KnCorpPAMSubsRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622623449L;

    private List<String> freePAMSubsList;

    public List<String> getFreePAMSubsList() {
        return freePAMSubsList;
    }

    public void setFreePAMSubsList(List<String> freePAMSubsList) {
        this.freePAMSubsList = freePAMSubsList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("freePAMSubsList - ").append(freePAMSubsList);
        return sb.toString();
    }
}
