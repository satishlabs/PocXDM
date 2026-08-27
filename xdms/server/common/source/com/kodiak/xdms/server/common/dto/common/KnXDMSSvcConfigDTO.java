/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * *****************************************************************************
 * <p/>
 * Subsystem:   POC
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit kumar           Jab 15, 2011       7.0
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
 * *******************************************************************************
 */

public class KnXDMSSvcConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676183L;

    private String pttServerId;
    private String primaryXDMSURI;
    private String geoXDMSURI;
    private String xCAPRootURI;
    private String auth_realm;
    private int maxPublicContactsPerSubscr;
    private int maxPublicPOCGrpsPerSubscr;
    private int maxMembersPerPublicPOCGrp;
    private String publicPocGrp_ConfURITemplate;
    private int maxCorpContactsPerSubscr;
    private int maxCorpGrpsPerSubscr;
    private String corpPocGrp_ConfURITemplate;
    private int maxSubscrPerCorp;
    private int maxCorpGrpsPerLargeDispatch;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getPrimaryXDMSURI() {
        return primaryXDMSURI;
    }

    public void setPrimaryXDMSURI(String primaryXDMSURI) {
        this.primaryXDMSURI = primaryXDMSURI;
    }

    public String getGeoXDMSURI() {
        return geoXDMSURI;
    }

    public void setGeoXDMSURI(String geoXDMSURI) {
        this.geoXDMSURI = geoXDMSURI;
    }

    public String getXCAPRootURI() {
        return xCAPRootURI;
    }

    public void setXCAPRootURI(String xCAPRootURI) {
        this.xCAPRootURI = xCAPRootURI;
    }

    public String getAuth_realm() {
        return auth_realm;
    }

    public void setAuth_realm(String auth_realm) {
        this.auth_realm = auth_realm;
    }

    public int getMaxPublicContactsPerSubscr() {
        return maxPublicContactsPerSubscr;
    }

    public void setMaxPublicContactsPerSubscr(int maxPublicContactsPerSubscr) {
        this.maxPublicContactsPerSubscr = maxPublicContactsPerSubscr;
    }

    public int getMaxPublicPOCGrpsPerSubscr() {
        return maxPublicPOCGrpsPerSubscr;
    }

    public void setMaxPublicPOCGrpsPerSubscr(int maxPublicPOCGrpsPerSubscr) {
        this.maxPublicPOCGrpsPerSubscr = maxPublicPOCGrpsPerSubscr;
    }

    public int getMaxMembersPerPublicPOCGrp() {
        return maxMembersPerPublicPOCGrp;
    }

    public void setMaxMembersPerPublicPOCGrp(int maxMembersPerPublicPOCGrp) {
        this.maxMembersPerPublicPOCGrp = maxMembersPerPublicPOCGrp;
    }

    public String getPublicPocGrp_ConfURITemplate() {
        return publicPocGrp_ConfURITemplate;
    }

    public void setPublicPocGrp_ConfURITemplate(String publicPocGrp_ConfURITemplate) {
        this.publicPocGrp_ConfURITemplate = publicPocGrp_ConfURITemplate;
    }

    public int getMaxCorpContactsPerSubscr() {
        return maxCorpContactsPerSubscr;
    }

    public void setMaxCorpContactsPerSubscr(int maxCorpContactsPerSubscr) {
        this.maxCorpContactsPerSubscr = maxCorpContactsPerSubscr;
    }

    public int getMaxCorpGrpsPerSubscr() {
        return maxCorpGrpsPerSubscr;
    }

    public void setMaxCorpGrpsPerSubscr(int maxCorpGrpsPerSubscr) {
        this.maxCorpGrpsPerSubscr = maxCorpGrpsPerSubscr;
    }

    public String getCorpPocGrp_ConfURITemplate() {
        return corpPocGrp_ConfURITemplate;
    }

    public void setCorpPocGrp_ConfURITemplate(String corpPocGrp_ConfURITemplate) {
        this.corpPocGrp_ConfURITemplate = corpPocGrp_ConfURITemplate;
    }

    public int getMaxSubscrPerCorp() {
        return maxSubscrPerCorp;
    }

    public void setMaxSubscrPerCorp(int maxSubscrPerCorp) {
        this.maxSubscrPerCorp = maxSubscrPerCorp;
    }

    public int getMaxSublistsPerCorp() {
        return maxSublistsPerCorp;
    }

    public void setMaxSublistsPerCorp(int maxSublistsPerCorp) {
        this.maxSublistsPerCorp = maxSublistsPerCorp;
    }

    public int getMaxMembersPerCorpSublist() {
        return maxMembersPerCorpSublist;
    }

    public void setMaxMembersPerCorpSublist(int maxMembersPerCorpSublist) {
        this.maxMembersPerCorpSublist = maxMembersPerCorpSublist;
    }

    public int getMaxExtContactsPerCorp() {
        return maxExtContactsPerCorp;
    }

    public void setMaxExtContactsPerCorp(int maxExtContactsPerCorp) {
        this.maxExtContactsPerCorp = maxExtContactsPerCorp;
    }

    public int getMaxPOCGrpsPerCorp() {
        return maxPOCGrpsPerCorp;
    }

    public void setMaxPOCGrpsPerCorp(int maxPOCGrpsPerCorp) {
        this.maxPOCGrpsPerCorp = maxPOCGrpsPerCorp;
    }

    public int getMaxMembersPerCorpPOCGrp() {
        return maxMembersPerCorpPOCGrp;
    }

    public void setMaxMembersPerCorpPOCGrp(int maxMembersPerCorpPOCGrp) {
        this.maxMembersPerCorpPOCGrp = maxMembersPerCorpPOCGrp;
    }

    private int maxSublistsPerCorp;
    private int maxMembersPerCorpSublist;
    private int maxExtContactsPerCorp;
    private int maxPOCGrpsPerCorp;
    private int maxMembersPerCorpPOCGrp;

    public int getMaxCorpGrpsPerLargeDispatch() {
        return maxCorpGrpsPerLargeDispatch;
    }

    public void setMaxCorpGrpsPerLargeDispatch(int maxCorpGrpsPerLargeDispatch) {
        this.maxCorpGrpsPerLargeDispatch = maxCorpGrpsPerLargeDispatch;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", primaryXDMSURI - ").append(primaryXDMSURI);
        strBuffer.append(", geoXDMSURI - ").append(geoXDMSURI);
        strBuffer.append(", xCAPRootURI - ").append(xCAPRootURI);
        strBuffer.append(", auth_realm - ").append(auth_realm);
        strBuffer.append(", maxPublicContactsPerSubscr - ").append(maxPublicContactsPerSubscr);
        strBuffer.append(", maxPublicPOCGrpsPerSubscr - ").append(maxPublicPOCGrpsPerSubscr);
        strBuffer.append(", maxMembersPerPublicPOCGrp - ").append(maxMembersPerPublicPOCGrp);
        strBuffer.append(", publicPocGrp_ConfURITemplate - ").append(publicPocGrp_ConfURITemplate);
        strBuffer.append(", maxCorpContactsPerSubscr - ").append(maxCorpContactsPerSubscr);
        strBuffer.append(", maxCorpGrpsPerSubscr - ").append(maxCorpGrpsPerSubscr);
        strBuffer.append(", corpPocGrp_ConfURITemplate - ").append(corpPocGrp_ConfURITemplate);
        strBuffer.append(", maxSubscrPerCorp - ").append(maxSubscrPerCorp);
        strBuffer.append(", maxSublistsPerCorp - ").append(maxSublistsPerCorp);
        strBuffer.append(", maxMembersPerCorpSublist - ").append(maxMembersPerCorpSublist);
        strBuffer.append(", maxPOCGrpsPerCorp - ").append(maxPOCGrpsPerCorp);
        strBuffer.append(", maxMembersPerCorpPOCGrp - ").append(maxMembersPerCorpPOCGrp);
        strBuffer.append(", maxCorpGrpsPerLargeDispatch - ").append(maxCorpGrpsPerLargeDispatch);

        return strBuffer.toString();
    }


    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
