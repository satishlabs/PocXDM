package com.kodiak.common.commdto.common;

import java.io.Serial;
import java.io.Serializable;

public class KnBulkSubscriberRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7526471155622676305L;

    private int corpId;
    private String xdmsHome;
    private int isExistingCorp;
    private boolean corpAutoPairing;
    private boolean isOldCorp;
    private Long lastUpdateprofileTime;
    private String internalDeviceId;
    private String extCorpId;
    private String subsFS2;
    private Integer subscriberClientType;
    private int licenseType;
    private int mcsCompliance;
    private boolean bitEnabled;
    private int deviceSharing;
    private long expiry;
    private Boolean isOIDCApplicable;

    public int getIsExistingCorp() {
        return isExistingCorp;
    }
    public void setIsExistingCorp(int isExistingCorp) {
        this.isExistingCorp = isExistingCorp;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }
    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public int getCorpId() {
        return corpId;
    }
    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Boolean getIsOldCorp() {
        return isOldCorp;
    }
    public void setIsOldCorp(Boolean isOldCorp) {
        this.isOldCorp = isOldCorp;
    }

    public Boolean getCorpAutoPairing() {
        return corpAutoPairing;
    }
    public void setCorpAutoPairing(Boolean corpAutoPairing) {
        this.corpAutoPairing = corpAutoPairing;
    }

    public String getSubsFS2() {
        return subsFS2;
    }
    public void setSubsFS2(String subsFS2) {
        this.subsFS2 = subsFS2;
    }

    public Long getLastUpdateprofileTime() {
        return lastUpdateprofileTime;
    }
    public void setLastUpdateprofileTime(Long lastUpdateprofileTime) {
        this.lastUpdateprofileTime = lastUpdateprofileTime;
    }

    public String getInternalDeviceId() {
        return internalDeviceId;
    }
    public void setInternalDeviceId(String internalDeviceId) {
        this.internalDeviceId = internalDeviceId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }
    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public Integer getSubscriberClientType() {
        return subscriberClientType;
    }
    public void setSubscriberClientType(Integer subscriberClientType) {
        this.subscriberClientType = subscriberClientType;
    }

    public int getLicenseType() {
        return licenseType;
    }
    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public int getMcsCompliance() {
        return mcsCompliance;
    }
    public void setMcsCompliance(int mcsCompliance) {
        this.mcsCompliance = mcsCompliance;
    }

    public boolean getBitEnabled() {
        return bitEnabled;
    }
    public void setBitEnabled(boolean bitEnabled) {
        this.bitEnabled = bitEnabled;
    }

    public int getDeviceSharing() {
        return deviceSharing;
    }
    public void setDeviceSharing(int deviceSharing) {
        this.deviceSharing = deviceSharing;
    }

    public long getExpiry() {
        return expiry;
    }
    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public Boolean getOIDCApplicable() {
        return isOIDCApplicable;
    }
    public void setOIDCApplicable(Boolean OIDCApplicable) {
        isOIDCApplicable = OIDCApplicable;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" [KnOPCreateSubsInfoDTO --> ")
                .append(", CORP_ID - ").append(corpId)
                .append(", corpAutoPairing - ").append(corpAutoPairing)
                .append(", isOldCorp - ").append(isOldCorp)
                .append(", extCorpId - ").append(extCorpId)
                .append(", internalDeviceId - ").append(internalDeviceId)
                .append(", subscriberClientType - ").append(subscriberClientType)
                .append(", licenseType - ").append(licenseType)
                .append(", mcsCompliance - ").append(mcsCompliance)
                .append(", bitEnabled - ").append(bitEnabled)
                .append(", deviceSharing - ").append(deviceSharing)
                .append(", expiry - ").append(expiry)
                .append(", isOIDCApplicable - ").append(isOIDCApplicable)
                .append("]");
        return strBuffer.toString();
    }
}
