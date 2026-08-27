/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPContactListDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
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
import java.util.LinkedList;
import java.util.Set;


public class KnIPCorpContactListDTO extends KnIPCorpInfoDTO {

    private static final long serialVersionUID = 7526471155622676187L;

    private Set<String> mdnList;

    private LinkedList<String> aliasMdnList;

    private LinkedList<String> userIdList;

    private Collection<KnCorpSubscriberDTO> contactList;

    private Collection<KnCorpSubscriberDTO> contactAliasMdnList;

    private Collection<KnCorpSubscriberDTO> contactUserIdList;


    public Set<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(Set<String> mdnList) {
        this.mdnList = mdnList;
    }

    public LinkedList<String> getAliasMdnList() {
        return aliasMdnList;
    }

    public void setAliasMdnList(LinkedList<String> aliasMdnList) {
        this.aliasMdnList = aliasMdnList;
    }

    public LinkedList<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(LinkedList<String> userIdList) {
        this.userIdList = userIdList;
    }

    public Collection<KnCorpSubscriberDTO> getContactList() {
        return contactList;
    }

    public void setContactList(Collection<KnCorpSubscriberDTO> contactList) {
        this.contactList = contactList;
    }

    public Collection<KnCorpSubscriberDTO> getContactAliasMdnList() {
        return contactAliasMdnList;
    }

    public void setContactAliasMdnList(Collection<KnCorpSubscriberDTO> contactAliasMdnList) {
        this.contactAliasMdnList = contactAliasMdnList;
    }

    public Collection<KnCorpSubscriberDTO> getContactUserIdList() {
        return contactUserIdList;
    }

    public void setContactUserIdList(Collection<KnCorpSubscriberDTO> contactUserIdList) {
        this.contactUserIdList = contactUserIdList;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", MdnList - ").append(KnGDPRTemplate.mdnList(mdnList))
                .append(", aliasMdnList - ").append(KnGDPRTemplate.mdnList(aliasMdnList))
                .append(", userIdList - ").append(userIdList)
                .append(", ContactList - ").append(contactList)
                .append(", contactAliasMdnList - ").append(contactAliasMdnList)
                .append(", contactUserIdList - ").append(contactUserIdList);
        return sb.toString();
    }
}
