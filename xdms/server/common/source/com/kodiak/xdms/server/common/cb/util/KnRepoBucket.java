/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.cb.util;

public enum KnRepoBucket {

    MCDATA("mccDataBucket"),
    PTX("ptxbucket"),
    PTT("pttbucket"),
    PTT_DATA("pttDataBucket"),
    PTX_CHNLD_DATA("ptxChnldDataBucket"),
    POC_DATA("pocdata"),
    FD_DATA("fdDataBucket");

    private String bucket;

    KnRepoBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getBucketName() {
        return bucket;
    }
}
