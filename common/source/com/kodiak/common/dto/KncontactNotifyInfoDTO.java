/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.common.dto;

import java.util.List;

public class KncontactNotifyInfoDTO {
        private List<String> ListOfMdns;
        private List<KnContactParamInfoDTO> ListOfContacts;

        private List<String> addedContatcs;
        private List<String> removedContacts;
        private List<String> modifiedContacts;

        // Getters and setters
        public List<String> getListOfMdns() {
            return ListOfMdns;
        }

        public void setListOfMdns(List<String> listOfMdns) {
            ListOfMdns = listOfMdns;
        }

        public List<KnContactParamInfoDTO> getListOfContacts() {
            return ListOfContacts;
        }

        public void setListOfContacts(List<KnContactParamInfoDTO> listOfContacts) {
            ListOfContacts = listOfContacts;
        }

    public List<String> getAddedContatcs() { return addedContatcs; }

    public void setAddedContatcs(List<String> addedContatcs) { this.addedContatcs = addedContatcs; }

    public List<String> getRemovedContacts() { return removedContacts;}

    public void setRemovedContacts(List<String> removedContacts) { this.removedContacts = removedContacts; }

    public List<String> getModifiedContacts() { return modifiedContacts; }

    public void setModifiedContacts(List<String> modifiedContacts) { this.modifiedContacts = modifiedContacts; }
}
