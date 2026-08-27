/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.cb.util;

public enum KnDocData {

    MCDATA_DATA("mcdata"),
    PTX_DATA("ptxdata"),
    PTT_DATA("pttdata"),
    PTX_CHNLD_DATA("ptxChnldData"),
    POC_DATA("pocdata"),
    FD_DATA("fdData");

    private String name;

    KnDocData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
