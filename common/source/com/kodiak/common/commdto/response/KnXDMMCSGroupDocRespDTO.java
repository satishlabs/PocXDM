/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMemberDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMCDATAServiceConfigRespDTO.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      12/07/2019    9.1
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

public class KnXDMMCSGroupDocRespDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = 6613773386312744671L;

    private String lsURI;
    private String lsName;
    private Collection<KnXDMMdnInfoDTO> userdtoOMA;
    private String onnwMaxParticipantCount;
    private Integer onnwHangTimer;
    private boolean onnwAudoCutIn;
    private String protectMedia;
    private String protectTC;
    private String allowSDS;
    private String allowFD;
    private String allowCM;
    private String tranControl;
    private String rxControl;
    private String allowEnhancedStatus;
    private String onnwSDS;
    private String onnwFD;
    private String onnwAR;
    private List<KnXDMOSMInfoRequestDTO> operationalValues;
    private String encodingNames;
    private Collection<String> actions;
    private Integer mcxGrpInd;
    private String ugwConfig;
    private String isPreConfiguredGroup;

    public String getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(String isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public String getUgwConfig() {
        return ugwConfig;
    }

    public void setUgwConfig(String ugwConfig) {
        this.ugwConfig = ugwConfig;
    }

    public Integer getMcxGrpInd() {
		return mcxGrpInd;
	}

	public void setMcxGrpInd(Integer mcxGrpInd) {
		this.mcxGrpInd = mcxGrpInd;
	}

	public String getLsURI() {
        return lsURI;
    }

    public void setLsURI(String lsURI) {
        this.lsURI = lsURI;
    }

    public String getLsName() {
        return lsName;
    }

    public void setLsName(String lsName) {
        this.lsName = lsName;
    }

    public Collection<KnXDMMdnInfoDTO> getUserdtoOMA() {return userdtoOMA; }

    public void setUserdtoOMA(Collection<KnXDMMdnInfoDTO> userdtoOMA) {
        this.userdtoOMA = userdtoOMA;
    }

    public String getOnnwMaxParticipantCount() {
        return onnwMaxParticipantCount;
    }

    public void setOnnwMaxParticipantCount(String onnwMaxParticipantCount) {
        this.onnwMaxParticipantCount = onnwMaxParticipantCount;
    }

    public Integer getOnnwHangTimer() {return onnwHangTimer;}

    public void setOnnwHangTimer(Integer onnwHangTimer) {this.onnwHangTimer = onnwHangTimer; }

    public boolean isOnnwAudoCutIn() {return onnwAudoCutIn; }

    public void setOnnwAudoCutIn(boolean onnwAudoCutIn) {this.onnwAudoCutIn = onnwAudoCutIn; }

    public String getProtectMedia() {return protectMedia; }

    public void setProtectMedia(String protectMedia) {this.protectMedia = protectMedia; }

    public String getProtectTC() {return protectTC; }

    public void setProtectTC(String protectTC) {this.protectTC = protectTC; }

    public String getAllowSDS() {return allowSDS; }

    public void setAllowSDS(String allowSDS) {this.allowSDS = allowSDS; }

    public String getAllowFD() {return allowFD; }

    public void setAllowFD(String allowFD) {this.allowFD = allowFD; }

    public String getAllowCM() {return allowCM; }

    public void setAllowCM(String allowCM) {this.allowCM = allowCM; }

    public String getTranControl() {return tranControl; }

    public void setTranControl(String tranControl) {this.tranControl = tranControl; }

    public String getRxControl() {return rxControl; }

    public void setRxControl(String rxControl) {this.rxControl = rxControl; }

    public String getAllowEnhancedStatus() {return allowEnhancedStatus; }

    public void setAllowEnhancedStatus(String allowEnhancedStatus) {this.allowEnhancedStatus = allowEnhancedStatus; }

    public String getOnnwSDS() {return onnwSDS; }

    public void setOnnwSDS(String onnwSDS) {this.onnwSDS = onnwSDS; }

    public String getOnnwFD() {return onnwFD; }

    public void setOnnwFD(String onnwFD) {this.onnwFD = onnwFD; }

    public String getOnnwAR() {return onnwAR; }

    public void setOnnwAR(String onnwAR) {this.onnwAR = onnwAR; }

    public List<KnXDMOSMInfoRequestDTO> getOperationalValues() {return operationalValues; }

    public void setOperationalValues(List<KnXDMOSMInfoRequestDTO> operationalValues) {this.operationalValues = operationalValues; }

    public String getEncodingNames() {return encodingNames; }

    public void setEncodingNames(String encodingNames) {this.encodingNames = encodingNames; }

    public Collection<String> getActions() {return actions; }

    public void setActions(Collection<String> actions) {this.actions = actions; }

    @Override
    public String toString() {
        return "KnXDMMCSGroupDocRespDTO{" +
                "lsURI='" + lsURI + '\'' +
                ", lsName='" + lsName + '\'' +
                ", userdtoOMA=" + userdtoOMA +
                ", onnwMaxParticipantCount='" + onnwMaxParticipantCount + '\'' +
                ", onnwHangTimer='" + onnwHangTimer + '\'' +
                ", onnwAudoCutIn='" + onnwAudoCutIn + '\'' +
                ", protectMedia='" + protectMedia + '\'' +
                ", protectTC='" + protectTC + '\'' +
                ", allowSDS='" + allowSDS + '\'' +
                ", allowFD='" + allowFD + '\'' +
                ", allowCM='" + allowCM + '\'' +
                ", tranControl='" + tranControl + '\'' +
                ", rxControl='" + rxControl + '\'' +
                ", allowEnhancedStatus='" + allowEnhancedStatus + '\'' +
                ", onnwSDS='" + onnwSDS + '\'' +
                ", onnwFD='" + onnwFD + '\'' +
                ", onnwAR='" + onnwAR + '\'' +
                ", operationalValues='" + operationalValues + '\'' +
                ", encodingNames=" + encodingNames +
                ", actions=" + actions +
                ", mcxGrpInd="+mcxGrpInd+
                ", ugwConfig="+ugwConfig+
                ", isPreConfiguredGroup=" +isPreConfiguredGroup +
                '}';
    }
}
