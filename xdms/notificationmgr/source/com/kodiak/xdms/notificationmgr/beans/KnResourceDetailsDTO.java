/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.beans;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.StringJoiner;

/**
 * Created by abhishek on 25/10/16.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class KnResourceDetailsDTO {
    private String resourceType;
    private String etag;
    private String updateTime;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }


    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KnResourceDetailsDTO.class.getSimpleName() + "[", "]")
                .add("resourceType='" + resourceType + "'")
                .add("etag='" + etag + "'")
                .add("updateTime='" + updateTime + "'")
                .toString();
    }
}

