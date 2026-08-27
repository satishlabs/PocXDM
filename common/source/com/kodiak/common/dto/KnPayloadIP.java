/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.common.dto;

import com.kodiak.common.commdto.request.KnXDMSubsAliasDetailsReqDTO;

import java.util.*;

public class KnPayloadIP {

    private KnAsyncGroupNotifyDTO knAsyncGroupNotifyDTO;

    private boolean isMdnDeleted;

    private KncontactNotifyInfoDTO kncontactNotifyInfoDTO;

    private KnAuthorizationNotifyInfoDTO knAuthorizationNotifyInfoDTO;

    private KnEmergencyConfigNotifyInfoDTO knEmergencyConfigNotifyInfoDTO;


    private Map<String, Object> knPayloadCarrier;

    public Map<String, Object> getKnPayloadCarrier() {
        if (null == knPayloadCarrier) {
            return knPayloadCarrier = new Hashtable<String, Object>();
        }
        return knPayloadCarrier;
    }

    public void setKnPayloadCarrier(Map<String, Object> knPayloadCarrier) {
        this.knPayloadCarrier = knPayloadCarrier;
    }

    public KnXDMSubsAliasDetailsReqDTO getSubsAliasDetailsReqDTO() {
        return subsAliasDetailsReqDTO;
    }

    public void setSubsAliasDetailsReqDTO(KnXDMSubsAliasDetailsReqDTO subsAliasDetailsReqDTO) {
        this.subsAliasDetailsReqDTO = subsAliasDetailsReqDTO;
    }

    private KnXDMSubsAliasDetailsReqDTO subsAliasDetailsReqDTO;

    public String getCorpId() {

        return corpId;
    }

    public void setCorpId(String corpId) {

        this.corpId = corpId;
    }

    private String corpId;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    private String mdn;

    private String OldMdn;

    private String sipRandomPwdPlain;

    private String corpSubscriptionType;

    private String oldUserId;

    public String getOldUserId() { return oldUserId; }

    public void setOldUserId(String oldUserId) { this.oldUserId = oldUserId; }

    public String getCorpSubscriptionType() { return corpSubscriptionType; }

    public void setCorpSubscriptionType(String corpSubscriptionType) { this.corpSubscriptionType = corpSubscriptionType; }

    public String getOldMdn() { return OldMdn; }

    public void setOldMdn(String oldMdn) { OldMdn = oldMdn; }

    public String getSipRandomPwdPlain() { return sipRandomPwdPlain; }

    public void setSipRandomPwdPlain(String sipRandomPwdPlain) { this.sipRandomPwdPlain = sipRandomPwdPlain; }

    public KnEmergencyConfigNotifyInfoDTO getKnEmergencyConfigNotifyInfoDTO() {
        return knEmergencyConfigNotifyInfoDTO;
    }

    public void setKnEmergencyConfigNotifyInfoDTO(KnEmergencyConfigNotifyInfoDTO knEmergencyConfigNotifyInfoDTO) {
        this.knEmergencyConfigNotifyInfoDTO = knEmergencyConfigNotifyInfoDTO;
    }

    public boolean isMdnDeleted() {
        return isMdnDeleted;
    }

    public void setMdnDeleted(boolean mdnDeleted) {
        isMdnDeleted = mdnDeleted;
    }

    public KncontactNotifyInfoDTO getKncontactNotifyInfoDTO() {
        return kncontactNotifyInfoDTO;
    }

    public void setKncontactNotifyInfoDTO(KncontactNotifyInfoDTO kncontactNotifyInfoDTO) {
        this.kncontactNotifyInfoDTO = kncontactNotifyInfoDTO;
    }

    public KnAuthorizationNotifyInfoDTO getKnAuthorizationNotifyInfoDTO() {
        return knAuthorizationNotifyInfoDTO;
    }
    public void setKnAuthorizationNotifyInfoDTO(KnAuthorizationNotifyInfoDTO knAuthorizationNotifyInfoDTO) {
        this.knAuthorizationNotifyInfoDTO = knAuthorizationNotifyInfoDTO;
    }


    public KnAsyncGroupNotifyDTO getKnAsyncGroupNotifyDTO() {
        return knAsyncGroupNotifyDTO;
    }

    public void setKnAsyncGroupNotifyDTO(KnAsyncGroupNotifyDTO knAsyncGroupNotifyDTO) {
        this.knAsyncGroupNotifyDTO = knAsyncGroupNotifyDTO;
    }

}
