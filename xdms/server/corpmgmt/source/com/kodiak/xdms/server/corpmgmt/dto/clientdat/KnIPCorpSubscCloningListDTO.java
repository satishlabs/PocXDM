/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPCorpSubscContactListDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 20, 2011      7.0
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


import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnBulkGroupCloningDTO;

import java.util.HashMap;
import java.util.Map;

public class KnIPCorpSubscCloningListDTO extends KnIPCorpInfoDTO {

    private static final long serialVersionUID = -4964235171567705099L;

    private String cloningBitSet;

    private Map<Integer,Boolean> validationMap = new HashMap<>();

    public Map<Integer, Boolean> getValidationMap() {
        return validationMap;
    }

    private void setValidationMap(Map<Integer, Boolean> validationMap) {
        this.validationMap = validationMap;
    }

    private String sourceMdn;

    private String subscriberMdn;//targetMdn

    private Map<String, Object> customParamMap;

    private KnConstants.HIERARCHY_TYPE hierarchyType;
    @Override
    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getSourceMdn() {
        return sourceMdn;
    }

    public void setSourceMdn(String sourceMdn) {
        this.sourceMdn = sourceMdn;
    }

    public String getSubscriberMdn() {
        return subscriberMdn;
    }

    public void setSubscriberMdn(String subscriberMdn) {
        this.subscriberMdn = subscriberMdn;
    }

    public String getCloningBitSet() {
        return cloningBitSet;
    }

    public void setCloningBitSet(String cloningBitSet) {
        this.cloningBitSet = cloningBitSet;
    }


    //for contact
    private KnIPCorpSubscContactListDTO corpSubscContactListDTO;

    public KnIPCorpSubscContactListDTO getCorpSubscContactListDTO() {
        return corpSubscContactListDTO;
    }

    public void setCorpSubscContactListDTO(KnIPCorpSubscContactListDTO corpSubscContactListDTO) {
        this.corpSubscContactListDTO = corpSubscContactListDTO;
    }

    //for group
    private KnIPSubscriberInfoDTO ipSubscriberInfoDTO;

    public KnIPSubscriberInfoDTO getIpSubscriberInfoDTO() {
        return ipSubscriberInfoDTO;
    }

    public void setIpSubscriberInfoDTO(KnIPSubscriberInfoDTO ipSubscriberInfoDTO) {
        this.ipSubscriberInfoDTO = ipSubscriberInfoDTO;
    }

    //================================================================================================
    private KnBulkGroupCloningDTO bulkGroupCloningDTO;

    public KnBulkGroupCloningDTO getBulkGroupCloningDTO() {
        return bulkGroupCloningDTO;
    }

    public void setBulkGroupCloningDTO(KnBulkGroupCloningDTO bulkGroupCloningDTO) {
        this.bulkGroupCloningDTO = bulkGroupCloningDTO;
    }

    //for emergency
    private KnIPEmergencyInfoDTO ipEmergencyInfoDTO;

    public KnIPEmergencyInfoDTO getIpEmergencyInfoDTO() {
        return ipEmergencyInfoDTO;
    }

    public void setIpEmergencyInfoDTO(KnIPEmergencyInfoDTO ipEmergencyInfoDTO) {
        this.ipEmergencyInfoDTO = ipEmergencyInfoDTO;
    }

    //for permission
    private KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO;

    public KnIPAuthUserPermissionInfoDTO getIpAuthUserPermissionInfoDTO() {
        return ipAuthUserPermissionInfoDTO;
    }

    public void setIpAuthUserPermissionInfoDTO(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO) {
        this.ipAuthUserPermissionInfoDTO = ipAuthUserPermissionInfoDTO;
    }


    //scanlist
    private KnIPTalkGroupDTO modifySubscriberTGListRequest;

    private KnIPTalkGroupDTO modifySubscriberScanListRequest;

    public KnIPTalkGroupDTO getModifySubscriberTGListRequest() {
        return modifySubscriberTGListRequest;
    }

    public void setModifySubscriberTGListRequest(KnIPTalkGroupDTO modifySubscriberTGListRequest) {
        this.modifySubscriberTGListRequest = modifySubscriberTGListRequest;
    }

    public KnIPTalkGroupDTO getModifySubscriberScanListRequest() {
        return modifySubscriberScanListRequest;
    }

    public void setModifySubscriberScanListRequest(KnIPTalkGroupDTO modifySubscriberScanListRequest) {
        this.modifySubscriberScanListRequest = modifySubscriberScanListRequest;
    }

    //corp admin fs

    private KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO;

    public KnIPSubscrFeatureInfoDTO getIpSubscrFeatureInfoDTO() {
        return ipSubscrFeatureInfoDTO;
    }

    public void setIpSubscrFeatureInfoDTO(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO) {
        this.ipSubscrFeatureInfoDTO = ipSubscrFeatureInfoDTO;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("KnIPCorpSubscCloningListDTO{");
        sb.append("cloningBitSet='").append(cloningBitSet).append('\'');
        sb.append(", validationMap=").append(validationMap);
        sb.append(", sourceMdn='").append(sourceMdn).append('\'');
        sb.append(", subscriberMdn='").append(subscriberMdn).append('\'');
        sb.append(", customParamMap=").append(customParamMap);
        sb.append(", hierarchyType=").append(hierarchyType);
        sb.append(", corpSubscContactListDTO=").append(corpSubscContactListDTO);
        sb.append(", ipSubscriberInfoDTO=").append(ipSubscriberInfoDTO);
        sb.append(", ipEmergencyInfoDTO=").append(ipEmergencyInfoDTO);
        sb.append(", ipAuthUserPermissionInfoDTO=").append(ipAuthUserPermissionInfoDTO);
        sb.append('}');
        return sb.toString();
    }
}
