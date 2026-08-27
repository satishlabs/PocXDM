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

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.Map;


public class KnIPCorpSubscContactListDTO extends KnIPCorpInfoDTO {

    private static final long serialVersionUID = 7526471155622676196L;

    private String subscriberMdn;
    private Collection<String> addedMdnList;
    private Collection<String> removedMdnList;
    private Collection<Integer> addedSublistIds;
    private Collection<Integer> removedSublistIds;
    private Collection<KnCorpSubscriberDTO> externalContacts;
    private Collection<KnCorpSubscriberDTO> externalAliasMdnContacts;
    private Collection<KnCorpSubscriberDTO> externalUserIdContacts;
    private Map<String, Object> customParamMap;

    private Collection<String> removeCommonContactList;

    public Collection<String> getRemoveCommonContactList() {
        return removeCommonContactList;
    }

    public void setRemoveCommonContactList(Collection<String> removeCommonContactList) {
        this.removeCommonContactList = removeCommonContactList;
    }

    private String sourceMdn;

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

    public Collection<String> getAddedMdnList() {
        return addedMdnList;
    }

    public void setAddedMdnList(Collection<String> addedMdnList) {
        this.addedMdnList = addedMdnList;
    }

    public Collection<String> getRemovedMdnList() {
        return removedMdnList;
    }

    public void setRemovedMdnList(Collection<String> removedMdnList) {
        this.removedMdnList = removedMdnList;
    }

    public Collection<Integer> getAddedSublistIds() {
        return addedSublistIds;
    }

    public void setAddedSublistIds(Collection<Integer> addedSublistIds) {
        this.addedSublistIds = addedSublistIds;
    }

    public Collection<Integer> getRemovedSublistIds() {
        return removedSublistIds;
    }

    public void setRemovedSublistIds(Collection<Integer> removedSublistIds) {
        this.removedSublistIds = removedSublistIds;
    }

    public Collection<KnCorpSubscriberDTO> getExternalContacts() {
        return externalContacts;
    }

    public void setExternalContacts(Collection<KnCorpSubscriberDTO> externalContacts) {
        this.externalContacts = externalContacts;
    }

    public Collection<KnCorpSubscriberDTO> getExternalAliasMdnContacts() {
        return externalAliasMdnContacts;
    }

    public void setExternalAliasMdnContacts(Collection<KnCorpSubscriberDTO> externalAliasMdnContacts) {
        this.externalAliasMdnContacts = externalAliasMdnContacts;
    }

    public Collection<KnCorpSubscriberDTO> getExternalUserIdContacts() {
        return externalUserIdContacts;
    }

    public void setExternalUserIdContacts(Collection<KnCorpSubscriberDTO> externalUserIdContacts) {
        this.externalUserIdContacts = externalUserIdContacts;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("KnIPCorpSubscContactListDTO{");
        sb.append("subscriberMdn='").append(subscriberMdn).append('\'');
        sb.append(", addedMdnList=").append(addedMdnList);
        sb.append(", removedMdnList=").append(removedMdnList);
        sb.append(", addedSublistIds=").append(addedSublistIds);
        sb.append(", removedSublistIds=").append(removedSublistIds);
        sb.append(", externalContacts=").append(externalContacts);
        sb.append(", externalAliasMdnContacts=").append(externalAliasMdnContacts);
        sb.append(", externalUserIdContacts=").append(externalUserIdContacts);
        sb.append(", customParamMap=").append(customParamMap);
        sb.append(", removeCommonContactList=").append(removeCommonContactList);
        sb.append(", sourceMdn='").append(sourceMdn).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
