/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.common.dto;

import java.util.List;

public class KnEmergencyConfigNotifyInfoDTO {
    List<String> mdnList;
    List<emergcyDestInfo> emergencyDestinationListInfo;

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public List<emergcyDestInfo> getemergencyDestinationListInfo() {
        return emergencyDestinationListInfo;
    }

    public void setemergencyDestinationListInfo(List<emergcyDestInfo> emergencyDestinationListInfo) {
        this.emergencyDestinationListInfo = emergencyDestinationListInfo;
    }
}
