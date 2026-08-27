/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPCorpActivationDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya      Nov 28, 2011      7.2
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpMailInfoDTO;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

public class KnIPCorpActivationDTO extends KnCorpInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676184L;

    private String mdn;
    private String activationCode;
    private String lang;
    private KnCorpMailInfoDTO mailInfoDto;
    private Collection<String> mdnList;

    private String operationType;
    private int clientType;
    private String performer;
    private IAuthDTO authDTO;
    private String entityId;

    private Map<String, Object> customParamMap;

    private Timestamp activationTimestamp;
    private Timestamp expiryTimestamp;
    private String tempPwd;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }


    public String getObjectId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getProfile() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setProfile(String profile) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;

    }

    public KnCorpMailInfoDTO getMailInfoDto() {
        return mailInfoDto;
    }

    public void setMailInfoDto(KnCorpMailInfoDTO mailInfoDto) {
        this.mailInfoDto = mailInfoDto;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public Timestamp getActivationTimestamp() {
        return activationTimestamp;
    }

    public void setActivationTimestamp(Timestamp activationTimestamp) {
        this.activationTimestamp = activationTimestamp;
    }

    public Collection<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(Collection<String> mdnList) {
        this.mdnList = mdnList;
    }

    public Timestamp getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(Timestamp expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    public String getTempPwd() {
        return tempPwd;
    }

    public void setTempPwd(String tempPwd) {
        this.tempPwd = tempPwd;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append("activationCode - ").append(activationCode)
                .append("lang - ").append(lang)
                .append("mailInfoDto - ").append(mailInfoDto)
                .append("customParamMap - ").append(customParamMap)
                .append("activationTimestamp - ").append(activationTimestamp)
                .append("expiryTimestamp - ").append(expiryTimestamp)
                .append("tempPwd - ").append(tempPwd);
        return sb.toString();
    }
}
