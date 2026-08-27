/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * Created by abhishek on 24/10/16.
 */
public class KnSyncGwUrlDTO  implements IIdentifier {

   private String priServiceFqdn;
    private String geoServiceFqdn;
    private String priSyncAdminPort;
    private String geoSyncAdminPort;
    private String priPtxBucketName;
    private String geoPtxBucketName;

    public String getGeoServiceFqdn() {
        return geoServiceFqdn;
    }

    public void setGeoServiceFqdn(String geoServiceFqdn) {
        this.geoServiceFqdn = geoServiceFqdn;
    }

    public String getPriSyncAdminPort() {
        return priSyncAdminPort;
    }

    public void setPriSyncAdminPort(String priSyncAdminPort) {
        this.priSyncAdminPort = priSyncAdminPort;
    }

    public String getGeoSyncAdminPort() {
        return geoSyncAdminPort;
    }

    public void setGeoSyncAdminPort(String geoSyncAdminPort) {
        this.geoSyncAdminPort = geoSyncAdminPort;
    }

    public String getPriPtxBucketName() {
        return priPtxBucketName;
    }

    public void setPriPtxBucketName(String priPtxBucketName) {
        this.priPtxBucketName = priPtxBucketName;
    }

    public String getGeoPtxBucketName() {
        return geoPtxBucketName;
    }

    public void setGeoPtxBucketName(String geoPtxBucketName) {
        this.geoPtxBucketName = geoPtxBucketName;
    }

    public String getPriServiceFqdn() {
        return priServiceFqdn;
    }

    public void setPriServiceFqdn(String priServiceFqdn) {
        this.priServiceFqdn = priServiceFqdn;
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
