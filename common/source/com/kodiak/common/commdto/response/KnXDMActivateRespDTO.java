/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMActivateRespDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/22/10       7.0
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

package com.kodiak.common.commdto.response;


import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMActivateRespDTO extends KnXDMRespDTO {

    private static final long serialVersionUID = 7526471155622676149L;
    private String mdn;
    //stores the XCAPRootURI
    private String XCAPRootURI;
    //stores the XCAP Root URI WIFI
    private String XcapRootUri_Wifi;

    //stores the XUI
    private String XUI;
    //stores the active Feature Set
    private Long activeFeatureSet1;
    private String apnName;

    private int expiryTime;

    private String sgwRootUriCellular;
    private String sgwRootUriWifi;
    private String authUriCellular;
    private String authUriWifi;
    private String cbBukInfo;
    private String sgwAuthMec;
    private String ipvc;
    private String ipvcPrefC;
    private String ipvcPrefW;
    private String ipvcMulti;
    private String ipvcPrefMulti;
    private String sgwLocUriCellular;
    private String token;
    private String sgwLocUriWifi;
    private String subsClientType;
    private String activeFeatureSet2;

    /**
     * getter method for XCAP Root URI
     *
     * @return String
     */
    public String getXCAPRootURI() {
        return XCAPRootURI;
    }

    /**
     * setter method for XCAP Root URI
     *
     * @param XCAPRootURI String
     */
    public void setXCAPRootURI(String XCAPRootURI) {
        if (XCAPRootURI != null) {
            XCAPRootURI = XCAPRootURI.trim();
            if (XCAPRootURI.equals("")) {
                XCAPRootURI = null;
            }
        }
        this.XCAPRootURI = XCAPRootURI;
    }


    public String getXcapRootUri_Wifi() {
        return XcapRootUri_Wifi;
    }

    public void setXcapRootUri_Wifi(String xcapRootUri_Wifi) {
         if (XcapRootUri_Wifi != null) {
            XcapRootUri_Wifi = XcapRootUri_Wifi.trim();
            if (XcapRootUri_Wifi.equals("")) {
                XcapRootUri_Wifi = null;
            }
        }
        this. XcapRootUri_Wifi = xcapRootUri_Wifi;
    }

    /**
     * getter method for XUI
     *
     * @return String
     */
    public String getXUI() {
        return XUI;
    }

    /**
     * setter method for XUI
     *
     * @param XUI String
     */
    public void setXUI(String XUI) {
        if (XUI != null) {
            XUI = XUI.trim();
            if (XUI.equals("")) {
                XUI = null;
            }
        }
        this.XUI = XUI;
    }

    public String getMdn() {
          return mdn;
      }

      public void setMdn(String mdn) {
          super.setMdn(mdn);
          this.mdn = mdn;
      }

    /**
     * getter method for Active Feature Set
     * @return long
     */
    public long getActiveFeatureSet1() {
        return activeFeatureSet1;
    }

    /**
     * setter method for the active Feature Set
     * @param activeFeatureSet1 long
     */
    public void setActiveFeatureSet1(long activeFeatureSet1) {
        this.activeFeatureSet1 = activeFeatureSet1;
    }

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(int expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getSgwRootUriCellular() {
        return sgwRootUriCellular;
    }

    public void setSgwRootUriCellular(String sgwRootUriCellular) {
        this.sgwRootUriCellular = sgwRootUriCellular;
    }

    public String getSgwRootUriWifi() {
        return sgwRootUriWifi;
    }

    public void setSgwRootUriWifi(String sgwRootUriWifi) {
        this.sgwRootUriWifi = sgwRootUriWifi;
    }

    public String getAuthUriCellular() {
        return authUriCellular;
    }

    public void setAuthUriCellular(String authUriCellular) {
        this.authUriCellular = authUriCellular;
    }

    public String getAuthUriWifi() {
        return authUriWifi;
    }

    public void setAuthUriWifi(String authUriWifi) {
        this.authUriWifi = authUriWifi;
    }

    public String getCbBukInfo() {
        return cbBukInfo;
    }

    public void setCbBukInfo(String cbBukInfo) {
        this.cbBukInfo = cbBukInfo;
    }

    public String getSgwAuthMec() {
        return sgwAuthMec;
    }

    public void setSgwAuthMec(String sgwAuthMec) {
        this.sgwAuthMec = sgwAuthMec;
    }

    public String getIpvc() {
        return ipvc;
    }

    public void setIpvc(String ipvc) {
        this.ipvc = ipvc;
    }

    public String getIpvcPrefC() {
        return ipvcPrefC;
    }

    public void setIpvcPrefC(String ipvcPrefC) {
        this.ipvcPrefC = ipvcPrefC;
    }

    public String getIpvcPrefW() {
        return ipvcPrefW;
    }

    public void setIpvcPrefW(String ipvcPrefW) {
        this.ipvcPrefW = ipvcPrefW;
    }

    public String getIpvcMulti() {
        return ipvcMulti;
    }

    public void setIpvcMulti(String ipvcMulti) {
        this.ipvcMulti = ipvcMulti;
    }

    public String getIpvcPrefMulti() {
        return ipvcPrefMulti;
    }

    public void setIpvcPrefMulti(String ipvcPrefMulti) {
        this.ipvcPrefMulti = ipvcPrefMulti;
    }

    public String getSgwLocUriCellular() {
        return sgwLocUriCellular;
    }

    public void setSgwLocUriCellular(String sgwLocUriCellular) {
        this.sgwLocUriCellular = sgwLocUriCellular;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSgwLocUriWifi() {
        return sgwLocUriWifi;
    }

    public void setSgwLocUriWifi(String sgwLocUriWifi) {
        this.sgwLocUriWifi = sgwLocUriWifi;
    }

    public String getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(String subsClientType) {
        this.subsClientType = subsClientType;
    }

    public String getActiveFeatureSet2() {
		return activeFeatureSet2;
	}

	public void setActiveFeatureSet2(String activeFeatureSet2) {
		this.activeFeatureSet2 = activeFeatureSet2;
	}

	public String toString() {
        StringBuilder strBuffer = new StringBuilder(50);
        strBuffer.append(super.toString());
        strBuffer.append(", XCAP_ROOT_URI - ").append(XCAPRootURI);
        strBuffer.append(", XcapRootUri_Wifi - ").append(XcapRootUri_Wifi);
        strBuffer.append(", mdn - ").append(KnGDPRTemplate.mdn(mdn));
        strBuffer.append(", XUI - ").append(XUI);
        strBuffer.append(", ACTIVE_FEATURE_SET - ").append(activeFeatureSet1);
        strBuffer.append(" apnName ").append(apnName);
        strBuffer.append(" expiryTime ").append(expiryTime)
                .append(" sgwRootUriCellular - ").append(sgwRootUriCellular)
                .append(" sgwRootUriWifi - ").append(sgwRootUriWifi)
                .append(" authUriCellular - ").append(authUriCellular)
                .append(" authUriWifi - ").append(authUriWifi)
                .append(" cbBukInfo - ").append(cbBukInfo)
                .append(" sgwAuthMec - ").append(sgwAuthMec)
                .append(" ipvc - ").append(ipvc)
                .append(" ipvcPrefC - ").append(ipvcPrefC)
                .append(" ipvcPrefW - ").append(ipvcPrefW)
                .append(" ipvcMulti - ").append(ipvcMulti)
                .append(" ipvcPrefMulti - ").append(ipvcPrefMulti)
                .append(" sgwLocUriCellular - ").append(sgwLocUriCellular)
                .append(" sgwLocUriWifi - ").append(sgwLocUriWifi)
                .append(" token - ").append(token)
                .append(" subsClientType - ").append(subsClientType)
                .append(" activeFeatureSet2 - ").append(activeFeatureSet2);

        return strBuffer.toString();
    }


}
