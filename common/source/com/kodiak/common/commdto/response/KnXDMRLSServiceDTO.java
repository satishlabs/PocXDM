/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;

import java.util.Arrays;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMRLSServiceDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Mar 11, 2011           7.0
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
public class KnXDMRLSServiceDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676162L;

    private String serviceUri;

    private String resourceListUri;

    private String[] packages;


    /**
     * @return
     */
    public String getResourceListUri() {
        return resourceListUri;
    }

    /**
     * @param resourceListUri
     */
    public void setResourceListUri(String resourceListUri) {
        this.resourceListUri = resourceListUri;
    }

    /**
     * @return
     */
    public String getServiceUri() {
        return serviceUri;
    }

    /**
     * @param serviceUri
     */
    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    /**
     * @return
     */
    public String[] getPackages() {
        return packages;
    }

    /**
     * @param pkgs
     */
    public void setPackages(String[] pkgs) {
        if (pkgs != null) {
            this.packages = Arrays.copyOf(pkgs, pkgs.length);
    }
    }

    /**
     * @return
     */
    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(" Service Uri - ").append(serviceUri)
                .append(", ResourceList Uri - ").append(resourceListUri)
                .append(", Packages - ").append(packages);

        return strBuffer.toString();
    }

    /**
     * @return
     */
    public String getObjectId() {
        return serviceUri;
    }
}
