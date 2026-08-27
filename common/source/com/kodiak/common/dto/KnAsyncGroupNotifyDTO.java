/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.common.dto;

import java.util.List;

public class KnAsyncGroupNotifyDTO {
    List<String> addedMembers;
    List<String> removedMembers;

    public List<String> getAddedMembers() {
        return addedMembers;
    }

    public void setAddedMembers(List<String> addedMembers) {
        this.addedMembers = addedMembers;
    }

    public List<String> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(List<String> removedMembers) {
        this.removedMembers = removedMembers;
    }
}
