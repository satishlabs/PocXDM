/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpInfo.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya      Nov 20, 2011      7.2
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
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnMailInfoDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

public class KnXDMCorpClientActRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622776157L;

    private String mdn;
    private String lang;
    private KnMailInfoDTO mailInfoDTO;
    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;

    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String activationCode;
    private String countryCode;
    private Collection<String> mdnList;
    private String otp;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String tmpPwd;
    private String userId;
    private String tmpPwdExpiry;
    private String msgId;

    @Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}


    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public KnMailInfoDTO getMailInfoDTO() {
        return mailInfoDTO;
    }

    public void setMailInfoDTO(KnMailInfoDTO mailInfoDTO) {
        this.mailInfoDTO = mailInfoDTO;
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

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Collection<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(Collection<String> mdnList) {
        this.mdnList = mdnList;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getTmpPwd() {
        return tmpPwd;
    }

    public void setTmpPwd(String tmpPwd) {
        this.tmpPwd = tmpPwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTmpPwdExpiry() {
        return tmpPwdExpiry;
    }

    public void setTmpPwdExpiry(String tmpPwdExpiry) {
        this.tmpPwdExpiry = tmpPwdExpiry;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", AuthDTO - ").append(authDTO)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", transactionId - ").append(transactionId)
                .append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", activationCode - ").append(activationCode)
                .append(", mailInfoDTO - ").append(mailInfoDTO)
                .append(", otp - ").append(otp)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", mailInfoDTO - ").append(mailInfoDTO)
                .append(", tmpPwd - ").append(tmpPwd)
                .append(", tmpPwdExpiry - ").append(tmpPwdExpiry)
                .append(", msgId - ").append(msgId)
                .append(", userId - ").append(KnGDPRTemplate.userId(userId));
        return sb.toString();

    }
}
