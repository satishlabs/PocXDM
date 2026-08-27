/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.common.dto;

public class emergcyDestInfo {
    /*TODO:
    * 1. DestinationType - Prim or Sec
    * 2. Destination - Mdn or GroupId
    * 3. Tag - Replace,Remove,Add
     */
    String destination;
    String tag;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
