/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**This is the dto class used for storing the ANP table information which includes
 * apnid
 * dynamicQosFlag
 * apnXCAPUri
 * Created by abhishek on 2/3/16.
 */
public class KnAPNConfigDTO  implements IIdentifier {

    private Integer apnId;
    private Integer dynamicQosFlag;
    private String apnXCAPUri;
    private String ptxBucketUri;
    private String locDataUriCellular;
    private String apnName;
    private Integer isDefault;
    private String pttServerId;
    private String sipProxyUri;
    private String geoSipProxyUri;
    private String geoRegPrimF5Uri;
    private String geoRegGeoF5Uri;
    private String fdServiceUriCell;
    private String mcsXCAPUri;

    public Integer getDynamicQosFlag() {
        return dynamicQosFlag;
    }

    public void setDynamicQosFlag(Integer dynamicQosFlag) {
        this.dynamicQosFlag = dynamicQosFlag;
    }

    public String getApnXCAPUri() {
        return apnXCAPUri;
    }

    public void setApnXCAPUri(String apnXCAPUri) {
        this.apnXCAPUri = apnXCAPUri;
    }

    public Integer getApnId() {

        return apnId;
    }

    public void setApnId(Integer apnId) {
        this.apnId = apnId;
    }

    public String getPtxBucketUri() {
        return ptxBucketUri;
    }

    public void setPtxBucketUri(String ptxBucketUri) {
        this.ptxBucketUri = ptxBucketUri;
    }

    public String getLocDataUriCellular() {
        return locDataUriCellular;
    }

    public void setLocDataUriCellular(String locDataUriCellular) {
        this.locDataUriCellular = locDataUriCellular;
    }

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getSipProxyUri() {
        return sipProxyUri;
    }

    public void setSipProxyUri(String sipProxyUri) {
        this.sipProxyUri = sipProxyUri;
    }

    public String getGeoSipProxyUri() {
        return geoSipProxyUri;
    }

    public void setGeoSipProxyUri(String geoSipProxyUri) {
        this.geoSipProxyUri = geoSipProxyUri;
    }

    public String getGeoRegPrimF5Uri() {
        return geoRegPrimF5Uri;
    }

    public void setGeoRegPrimF5Uri(String geoRegPrimF5Uri) {
        this.geoRegPrimF5Uri = geoRegPrimF5Uri;
    }

    public String getGeoRegGeoF5Uri() {
        return geoRegGeoF5Uri;
    }

    public void setGeoRegGeoF5Uri(String geoRegGeoF5Uri) {
        this.geoRegGeoF5Uri = geoRegGeoF5Uri;
    }

    public String getFdServiceUriCell() {
        return fdServiceUriCell;
    }

    public void setFdServiceUriCell(String fdServiceUriCell) {
        this.fdServiceUriCell = fdServiceUriCell;
    }

    public String getMcsXCAPUri() { return mcsXCAPUri; }

    public void setMcsXCAPUri(String mcsXCAPUri) { this.mcsXCAPUri = mcsXCAPUri; }

    @Override
    public String toString() {
        return "KnAPNConfigDTO{" +
                "apnId=" + apnId +
                ", dynamicQosFlag=" + dynamicQosFlag +
                ", apnXCAPUri='" + apnXCAPUri + '\'' +
                ", ptxBucketUri='" + ptxBucketUri + '\'' +
                ", locDataUriCellular='" + locDataUriCellular + '\'' +
                ", apnName='" + apnName + '\'' +
                ", isDefault=" + isDefault +
                ", pttServerId='" + pttServerId + '\'' +
                ", sipProxyUri='" + sipProxyUri + '\'' +
                ", geoSipProxyUri='" + geoSipProxyUri + '\'' +
                ", geoRegPrimF5Uri='" + geoRegPrimF5Uri + '\'' +
                ", geoRegGeoF5Uri='" + geoRegGeoF5Uri + '\'' +
                ", fdServiceUriCell='" + fdServiceUriCell + '\'' +
                ", mcsXCAPUri='" + mcsXCAPUri + '\'' +
                '}';
    }

    @Override
    public String getObjectId() {
        return this.apnXCAPUri+this.apnId+this.dynamicQosFlag;
    }
}
