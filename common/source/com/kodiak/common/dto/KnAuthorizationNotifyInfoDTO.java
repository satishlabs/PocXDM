/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.common.dto;

import java.util.List;

public class KnAuthorizationNotifyInfoDTO {
    private List<String> ListOfAuthorizationMdns;
    private List<KnTargetPerInfo> ListOfTargets;

    private List<String> addedTargets;
    private List<String> removedTargets;
    public List<String> getListOfAuthorizationMdns() {
        return ListOfAuthorizationMdns;
    }

    public void setListOfAuthorizationMdns(List<String> listOfAuthorizationMdns) {
        ListOfAuthorizationMdns = listOfAuthorizationMdns;
    }

    public List<KnTargetPerInfo> getListOfTargets() {
        return ListOfTargets;
    }

    public void setListOfTargets(List<KnTargetPerInfo> listOfTargets) {
        ListOfTargets = listOfTargets;
    }

    public List<String> getAddedTargets() { return addedTargets; }

    public void setAddedTargets(List<String> addedTargets) { this.addedTargets = addedTargets;}

    public List<String> getRemovedTargets() { return removedTargets;}

    public void setRemovedTargets(List<String> removedTargets) { this.removedTargets = removedTargets; }

}
