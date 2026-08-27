/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpProfileDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 24, 2011      7.0
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
package com.kodiak.xdms.server.common.dto.common;


public class KnAPNProfileInfoDTO extends KnProfileDTO {

    private static final long serialVersionUID = 7526471155622676171L;

    //Ext-CorpId for the corporate Profile which is referred by the CAT tool
    private String pttserverId;
    //This is the paired contactListId for the corp
    private int apnId;
    //This is the max CorporateList that a corporate can have
    private String xcaprooturi;
    //This is the max members per CorporateList that a corporate can have
    private String clientloguri;
    //This is the max Groups that a Corporate can have
    private String sipproxyuri;
    //This is the max Members Per Groups that a Corporate can have
    private String geosipproxyuri;

    private String georeg_primf5uri;

    private String georeg_geof5uri;

    private String pri_wsproxyuri;

    private String geo_wsproxyuri;

    private String georeg_pri_wsproxyuri;

    private String georeg_geo_wsproxyuri;

    private Integer dynamicQosFlag;

    private String pttbucketuricell;
    private String ptxbucketuricell;
    private String locdatauricell;
    private String kodiak_maps_uri;
    private String kodiak_maps_geo_uri;
    private String oidcxcaprooturi;
    private String resourcepriorityemerg;
    private String resourceprioritynormal;

    private String esrimaps_uri;
    private String esrimaps_geo_uri;
    private String noncallsipproxyuri;
    private String geo_noncallsipproxyuri;
    private String mcsXcapRootUri;
    private String kmsUri;

    public String getPttserverId() {
        return pttserverId;
    }

    public void setPttserverId(String pttserverId) {
        this.pttserverId = pttserverId;
    }

    public int getApnId() {
        return apnId;
    }

    public void setApnId(int apnId) {
        this.apnId = apnId;
    }

    public String getXcaprooturi() {
        return xcaprooturi;
    }

    public void setXcaprooturi(String xcaprooturi) {
        this.xcaprooturi = xcaprooturi;
    }

    public String getClientloguri() {
        return clientloguri;
    }

    public void setClientloguri(String clientloguri) {
        this.clientloguri = clientloguri;
    }

    public String getSipproxyuri() {
        return sipproxyuri;
    }

    public void setSipproxyuri(String sipproxyuri) {
        this.sipproxyuri = sipproxyuri;
    }

    public String getGeosipproxyuri() {
        return geosipproxyuri;
    }

    public void setGeosipproxyuri(String geosipproxyuri) {
        this.geosipproxyuri = geosipproxyuri;
    }

    public String getGeoreg_primf5uri() {
        return georeg_primf5uri;
    }

    public void setGeoreg_primf5uri(String georeg_primf5uri) {
        this.georeg_primf5uri = georeg_primf5uri;
    }

    public String getGeoreg_geof5uri() {
        return georeg_geof5uri;
    }

    public void setGeoreg_geof5uri(String georeg_geof5uri) {
        this.georeg_geof5uri = georeg_geof5uri;
    }

    public String getPri_wsproxyuri() {
        return pri_wsproxyuri;
    }

    public void setPri_wsproxyuri(String pri_wsproxyuri) {
        this.pri_wsproxyuri = pri_wsproxyuri;
    }

    public String getGeo_wsproxyuri() {
        return geo_wsproxyuri;
    }

    public void setGeo_wsproxyuri(String geo_wsproxyuri) {
        this.geo_wsproxyuri = geo_wsproxyuri;
    }

    public String getGeoreg_pri_wsproxyuri() {
        return georeg_pri_wsproxyuri;
    }

    public void setGeoreg_pri_wsproxyuri(String georeg_pri_wsproxyuri) {
        this.georeg_pri_wsproxyuri = georeg_pri_wsproxyuri;
    }

    public String getGeoreg_geo_wsproxyuri() {
        return georeg_geo_wsproxyuri;
    }

    public void setGeoreg_geo_wsproxyuri(String georeg_geo_wsproxyuri) {
        this.georeg_geo_wsproxyuri = georeg_geo_wsproxyuri;
    }

    public Integer getDynamicQosFlag() {
        return dynamicQosFlag;
    }

    public void setDynamicQosFlag(Integer dynamicQosFlag) {
        this.dynamicQosFlag = dynamicQosFlag;
    }

    public String getPttbucketuricell() {
        return pttbucketuricell;
    }

    public void setPttbucketuricell(String pttbucketuricell) {
        this.pttbucketuricell = pttbucketuricell;
    }

    public String getPtxbucketuricell() {
        return ptxbucketuricell;
    }

    public void setPtxbucketuricell(String ptxbucketuricell) {
        this.ptxbucketuricell = ptxbucketuricell;
    }

    public String getLocdatauricell() {
        return locdatauricell;
    }

    public void setLocdatauricell(String locdatauricell) {
        this.locdatauricell = locdatauricell;
    }

    public String getKodiak_maps_uri() {
        return kodiak_maps_uri;
    }

    public void setKodiak_maps_uri(String kodiak_maps_uri) {
        this.kodiak_maps_uri = kodiak_maps_uri;
    }

    public String getKodiak_maps_geo_uri() {
        return kodiak_maps_geo_uri;
    }

    public void setKodiak_maps_geo_uri(String kodiak_maps_geo_uri) {
        this.kodiak_maps_geo_uri = kodiak_maps_geo_uri;
    }

    public String getOidcxcaprooturi() {
        return oidcxcaprooturi;
    }

    public void setOidcxcaprooturi(String oidcxcaprooturi) {
        this.oidcxcaprooturi = oidcxcaprooturi;
    }

    public String getResourcepriorityemerg() {
        return resourcepriorityemerg;
    }

    public void setResourcepriorityemerg(String resourcepriorityemerg) {
        this.resourcepriorityemerg = resourcepriorityemerg;
    }

    public String getResourceprioritynormal() {
        return resourceprioritynormal;
    }

    public void setResourceprioritynormal(String resourceprioritynormal) {
        this.resourceprioritynormal = resourceprioritynormal;
    }

    public String getEsrimaps_uri() {
        return esrimaps_uri;
    }

    public void setEsrimaps_uri(String esrimaps_uri) {
        this.esrimaps_uri = esrimaps_uri;
    }

    public String getEsrimaps_geo_uri() {
        return esrimaps_geo_uri;
    }

    public void setEsrimaps_geo_uri(String esrimaps_geo_uri) {
        this.esrimaps_geo_uri = esrimaps_geo_uri;
    }

    public String getNoncallsipproxyuri() {
        return noncallsipproxyuri;
    }

    public void setNoncallsipproxyuri(String noncallsipproxyuri) {
        this.noncallsipproxyuri = noncallsipproxyuri;
    }

    public String getGeo_noncallsipproxyuri() {
        return geo_noncallsipproxyuri;
    }

    public void setGeo_noncallsipproxyuri(String geo_noncallsipproxyuri) {
        this.geo_noncallsipproxyuri = geo_noncallsipproxyuri;
    }

    public String getMcsXcapRootUri() {
        return mcsXcapRootUri;
    }

    public void setMcsXcapRootUri(String mcsXcapRootUri) {
        this.mcsXcapRootUri = mcsXcapRootUri;
    }

    public String getKmsUri() {
        return kmsUri;
    }

    public void setKmsUri(String kmsUri) {
        this.kmsUri = kmsUri;
    }

    @Override
    public String toString() {
        return "KnAPNProfileInfoDTO{" +
                "pttserverId='" + pttserverId + '\'' +
                ", apnId=" + apnId +
                ", xcaprooturi='" + xcaprooturi + '\'' +
                ", clientloguri='" + clientloguri + '\'' +
                ", sipproxyuri='" + sipproxyuri + '\'' +
                ", geosipproxyuri='" + geosipproxyuri + '\'' +
                ", georeg_primf5uri='" + georeg_primf5uri + '\'' +
                ", georeg_geof5uri='" + georeg_geof5uri + '\'' +
                ", pri_wsproxyuri='" + pri_wsproxyuri + '\'' +
                ", geo_wsproxyuri='" + geo_wsproxyuri + '\'' +
                ", georeg_pri_wsproxyuri='" + georeg_pri_wsproxyuri + '\'' +
                ", georeg_geo_wsproxyuri='" + georeg_geo_wsproxyuri + '\'' +
                ", dynamicQosFlag=" + dynamicQosFlag +
                ", pttbucketuricell='" + pttbucketuricell + '\'' +
                ", ptxbucketuricell='" + ptxbucketuricell + '\'' +
                ", locdatauricell='" + locdatauricell + '\'' +
                ", kodiak_maps_uri='" + kodiak_maps_uri + '\'' +
                ", kodiak_maps_geo_uri='" + kodiak_maps_geo_uri + '\'' +
                ", oidcxcaprooturi='" + oidcxcaprooturi + '\'' +
                ", resourcepriorityemerg='" + resourcepriorityemerg + '\'' +
                ", resourceprioritynormal='" + resourceprioritynormal + '\'' +
                ", esrimaps_uri='" + esrimaps_uri + '\'' +
                ", esrimaps_geo_uri='" + esrimaps_geo_uri + '\'' +
                ", noncallsipproxyuri='" + noncallsipproxyuri + '\'' +
                ", geo_noncallsipproxyuri='" + geo_noncallsipproxyuri + '\'' +
                ", mcsXcapRootUri='" + mcsXcapRootUri + '\'' +
                ", kmsUri='" + kmsUri + '\'' +
                '}';
    }
}
