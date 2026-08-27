/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMCDataUEConfigRespDTO.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      28/06/2019    9.1
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMMCDataUEConfigRespDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = -2522574492063787041L;
    private String mcs_domain_name ;
    private String mcdata_ue_config_name;
    private int  maxsimulsdstxn;
    private int  maxsimulfdtxn;
    private int  maxsimuldatatrans;
    private int  maxsimuldatatransgrp;
    private int  maxsimuldatarecep;
    private int  maxsimuldatarecepgrp;
    private boolean  ip_pref_on_cell_intf;
    private String sipProxyURI;
    private String geoSipProxyURI;

    public String getMcs_domain_name() {
        return mcs_domain_name;
    }

    public void setMcs_domain_name(String mcs_domain_name) {
        this.mcs_domain_name = mcs_domain_name;
    }

    public String getMcdata_ue_config_name() {
        return mcdata_ue_config_name;
    }

    public void setMcdata_ue_config_name(String mcdata_ue_config_name) {
        this.mcdata_ue_config_name = mcdata_ue_config_name;
    }

    public int getMaxsimulsdstxn() {
        return maxsimulsdstxn;
    }

    public void setMaxsimulsdstxn(int maxsimulsdstxn) {
        this.maxsimulsdstxn = maxsimulsdstxn;
    }

    public int getMaxsimulfdtxn() {
        return maxsimulfdtxn;
    }

    public void setMaxsimulfdtxn(int maxsimulfdtxn) {
        this.maxsimulfdtxn = maxsimulfdtxn;
    }

    public int getMaxsimuldatatrans() {
        return maxsimuldatatrans;
    }

    public void setMaxsimuldatatrans(int maxsimuldatatrans) {
        this.maxsimuldatatrans = maxsimuldatatrans;
    }

    public int getMaxsimuldatatransgrp() {
        return maxsimuldatatransgrp;
    }

    public void setMaxsimuldatatransgrp(int maxsimuldatatransgrp) {
        this.maxsimuldatatransgrp = maxsimuldatatransgrp;
    }

    public int getMaxsimuldatarecep() {
        return maxsimuldatarecep;
    }

    public void setMaxsimuldatarecep(int maxsimuldatarecep) {
        this.maxsimuldatarecep = maxsimuldatarecep;
    }

    public int getMaxsimuldatarecepgrp() {
        return maxsimuldatarecepgrp;
    }

    public void setMaxsimuldatarecepgrp(int maxsimuldatarecepgrp) {
        this.maxsimuldatarecepgrp = maxsimuldatarecepgrp;
    }

    public boolean isIp_pref_on_cell_intf() {
        return ip_pref_on_cell_intf;
    }

    public void setIp_pref_on_cell_intf(boolean ip_pref_on_cell_intf) {
        this.ip_pref_on_cell_intf = ip_pref_on_cell_intf;
    }
    public String getSipProxyURI() { return sipProxyURI; }

    public void setSipProxyURI(String sipProxyURI) { this.sipProxyURI = sipProxyURI; }

    public String getGeoSipProxyURI() {	return geoSipProxyURI; }

    public void setGeoSipProxyURI(String geoSipProxyURI) { this.geoSipProxyURI = geoSipProxyURI; }


    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(" mcs_domain_name - ").append(mcs_domain_name)
                .append(", mcdata_ue_config_name - ").append(mcdata_ue_config_name)
                .append(", maxsimulsdstxn - ").append(maxsimulsdstxn)
                .append(", maxsimulfdtxn - ").append(maxsimulfdtxn)
                .append(", maxsimuldatatrans - ").append(maxsimuldatatrans)
                .append(", maxsimuldatatransgrp - ").append(maxsimuldatatransgrp)
                .append(", maxsimuldatarecep - ").append(maxsimuldatarecep)
                .append(", maxsimuldatarecepgrp - ").append(maxsimuldatarecepgrp)
                .append(", ip_pref_on_cell_intf - ").append(ip_pref_on_cell_intf)
                .append(", sipProxyURI - ").append(sipProxyURI)
                .append(", geoSipProxyURI - ").append(geoSipProxyURI);
        return strBuffer.toString();
    }
}
