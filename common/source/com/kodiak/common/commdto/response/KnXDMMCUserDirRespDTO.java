/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.knXDMUserDirInfoDTO;

import java.util.List;

public class KnXDMMCUserDirRespDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = -181195482891857797L;

    private List<knXDMUserDirInfoDTO> userDirInfoDTOS;
    private String xcapRootUri;

    public List<knXDMUserDirInfoDTO> getUserDirInfoDTOS() {
        return userDirInfoDTOS;
    }

    public void setUserDirInfoDTOS(List<knXDMUserDirInfoDTO> userDirInfoDTOS) {
        this.userDirInfoDTOS = userDirInfoDTOS;
    }

    public String getXcapRootUri() {
        return xcapRootUri;
    }

    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

    @Override
    public String toString() {
        return "KnXDMMCUserDirRespDTO{" +
                "userDirInfoDTOS=" + userDirInfoDTOS +
                "xcapRootUri=" + xcapRootUri +
                '}';
    }
}
