/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

public class KnUserLoginResponseDTO extends KnXDMRespDTO {

    private static final long serialVersionUID = 7526471155622775643L;

    private String xcapRootUri;
    private String xui;
    private Long activeFS1;
    private String apn;
    private String xcapRootUriWifi;
    private String sgwRUriC;
    private String sgwRUriW;
    private String cbBI;
    private String sgwAM;
    private String authUriC;
    private String authUriW;
    private String token;
    private Integer ipVC;
    private Integer ipVPrefC;
    private Integer ipVPrefW;
    private Integer ipVMulti;
    private Integer ipVPrefMulti;
    private Integer subsClientType;
    private String sgwLUriC;
    private String sgwLUriW;
    private String activeFS2;
    private Integer isOptInNeeded;
    private String mcsXcapRootUri;
    private String mcsXcapRootUriWifi;
    private String kmsUri;
    private String kmsUriWifi;
    private String mcsXui;
    private String mdsiUri;
    private String gmsUri;

    public String getMdsiUri() {
		return mdsiUri;
	}

	public void setMdsiUri(String mdsiUri) {
		this.mdsiUri = mdsiUri;
	}

	public String getGmsUri() {
		return gmsUri;
	}

	public void setGmsUri(String gmsUri) {
		this.gmsUri = gmsUri;
	}

	public Integer getIsOptInNeeded() {
        return isOptInNeeded;
    }

    public void setIsOptInNeeded(Integer isOptInNeeded) {
        this.isOptInNeeded = isOptInNeeded;
    }

    public String getXcapRootUri() {
        return xcapRootUri;
    }

    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

    public String getXui() {
        return xui;
    }

    public void setXui(String xui) {
        this.xui = xui;
    }

    public Long getActiveFS1() {
        return activeFS1;
    }

    public void setActiveFS1(Long activeFS1) {
        this.activeFS1 = activeFS1;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getXcapRootUriWifi() {
        return xcapRootUriWifi;
    }

    public void setXcapRootUriWifi(String xcapRootUriWifi) {
        this.xcapRootUriWifi = xcapRootUriWifi;
    }

    public String getSgwRUriC() {
        return sgwRUriC;
    }

    public void setSgwRUriC(String sgwRUriC) {
        this.sgwRUriC = sgwRUriC;
    }

    public String getSgwRUriW() {
        return sgwRUriW;
    }

    public void setSgwRUriW(String sgwRUriW) {
        this.sgwRUriW = sgwRUriW;
    }

    public String getCbBI() {
        return cbBI;
    }

    public void setCbBI(String cbBI) {
        this.cbBI = cbBI;
    }

    public String getSgwAM() {
        return sgwAM;
    }

    public void setSgwAM(String sgwAM) {
        this.sgwAM = sgwAM;
    }

    public String getAuthUriC() {
        return authUriC;
    }

    public void setAuthUriC(String authUriC) {
        this.authUriC = authUriC;
    }

    public String getAuthUriW() {
        return authUriW;
    }

    public void setAuthUriW(String authUriW) {
        this.authUriW = authUriW;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIpVC() {
        return ipVC;
    }

    public void setIpVC(Integer ipVC) {
        this.ipVC = ipVC;
    }

    public Integer getIpVPrefC() {
        return ipVPrefC;
    }

    public void setIpVPrefC(Integer ipVPrefC) {
        this.ipVPrefC = ipVPrefC;
    }

    public Integer getIpVPrefW() {
        return ipVPrefW;
    }

    public void setIpVPrefW(Integer ipVPrefW) {
        this.ipVPrefW = ipVPrefW;
    }

    public Integer getIpVMulti() {
        return ipVMulti;
    }

    public void setIpVMulti(Integer ipVMulti) {
        this.ipVMulti = ipVMulti;
    }

    public Integer getIpVPrefMulti() {
        return ipVPrefMulti;
    }

    public void setIpVPrefMulti(Integer ipVPrefMulti) {
        this.ipVPrefMulti = ipVPrefMulti;
    }

    public Integer getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(Integer subsClientType) {
        this.subsClientType = subsClientType;
    }

    public String getSgwLUriC() {
        return sgwLUriC;
    }

    public void setSgwLUriC(String sgwLUriC) {
        this.sgwLUriC = sgwLUriC;
    }

    public String getSgwLUriW() {
        return sgwLUriW;
    }

    public void setSgwLUriW(String sgwLUriW) {
        this.sgwLUriW = sgwLUriW;
    }

    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

    public String getMcsXcapRootUri() {
        return mcsXcapRootUri;
    }

    public void setMcsXcapRootUri(String mcsXcapRootUri) {
        this.mcsXcapRootUri = mcsXcapRootUri;
    }

    public String getMcsXcapRootUriWifi() {
        return mcsXcapRootUriWifi;
    }

    public void setMcsXcapRootUriWifi(String mcsXcapRootUriWifi) {
        this.mcsXcapRootUriWifi = mcsXcapRootUriWifi;
    }

    public String getKmsUri() {
        return kmsUri;
    }

    public void setKmsUri(String kmsUri) {
        this.kmsUri = kmsUri;
    }

    public String getKmsUriWifi() {
        return kmsUriWifi;
    }

    public void setKmsUriWifi(String kmsUriWifi) {
        this.kmsUriWifi = kmsUriWifi;
    }

    public String getMcsXui() {
        return mcsXui;
    }

    public void setMcsXui(String mcsXui) {
        this.mcsXui = mcsXui;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(50);
        strBuffer.append(super.toString());
        strBuffer.append(", [KnUserLoginResponseDTO - ")
                .append(", xcapRootUri - ").append(xcapRootUri)
                .append(", xui - ").append(xui)
                .append(", activeFS1 - ").append(activeFS1)
                .append(", apn - ").append(apn)
                .append(", xcapRootUriWifi- ").append(xcapRootUriWifi)
                .append(", sgwRUriC - ").append(sgwRUriC)
                .append(", sgwRUriW - ").append(sgwRUriW)
                .append(", cbBI - ").append(cbBI)
                .append(", sgwAM - ").append(sgwAM)
                .append(", authUriC - ").append(authUriC)
                .append(", authUriW - ").append(authUriW)
                .append(", token - ").append(token)
                .append(", ipVC - ").append(ipVC)
                .append(", ipVPrefC - ").append(ipVPrefC)
                .append(", ipVPrefW - ").append(ipVPrefW)
                .append(", ipVMulti - ").append(ipVMulti)
                .append(", ipVPrefMulti - ").append(ipVPrefMulti)
                .append(", sgwLUriC - ").append(sgwLUriC)
                .append(", sgwLUriW - ").append(sgwLUriW)
                .append(", Subscriber_Client_Type - ").append(subsClientType)
                .append(", activeFS2 - ").append(activeFS2)
                .append(", mcsXcapRootUri - ").append(mcsXcapRootUri)
                .append(", mcsXcapRootUriWifi - ").append(mcsXcapRootUriWifi)
                .append(", kmsUri - ").append(kmsUri)
                .append(", kmsUriWifi - ").append(kmsUriWifi)
                .append(", mcsXui - ").append(mcsXui)
                .append(", isOptInNeeded - ").append(isOptInNeeded)
                .append("]");

        return strBuffer.toString();
    }
}
