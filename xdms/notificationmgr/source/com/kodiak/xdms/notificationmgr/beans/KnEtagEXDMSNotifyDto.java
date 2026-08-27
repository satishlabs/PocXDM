/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.beans;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.StringJoiner;

/**
 * Created by abhishek on 25/10/16.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class KnEtagEXDMSNotifyDto extends KnEXDMSNotifyDto {
    private String resourceId;
    private List<KnResourceDetailsDTO> resourceDetails;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<KnResourceDetailsDTO> getResourceDetails() {
        return resourceDetails;
    }

    public void setResourceDetails(List<KnResourceDetailsDTO> resourceDetails) {
        this.resourceDetails = resourceDetails;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KnEtagEXDMSNotifyDto.class.getSimpleName() + "[", "]")
                .add("resourceId='" + resourceId + "'")
                .add("resourceDetails=" + resourceDetails)
                .toString();
    }
}

