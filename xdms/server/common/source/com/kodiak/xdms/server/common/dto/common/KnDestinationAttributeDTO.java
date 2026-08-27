/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;


import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class KnDestinationAttributeDTO {
    private Integer destType;
    private String destURI;
    private Integer destCat;

    public KnDestinationAttributeDTO(){}
    public KnDestinationAttributeDTO(Integer destType, String destURI, Integer destCat) {
        this.destType = destType;
        this.destURI = destURI;
        this.destCat = destCat;
    }

    public Integer getDestType() {
        return destType;
    }

    public void setDestType(Integer destType) {
        this.destType = destType;
    }

    public String getDestURI() {
        return destURI;
    }

    public void setDestURI(String destURI) {
        this.destURI = destURI;
    }

    public Integer getDestCat() {
        return destCat;
    }

    public void setDestCat(Integer destCat) {
        this.destCat = destCat;
    }

    @Override
    public String toString() {
        return "KnDestinationAttributeDTO{" +
                "destType=" + destType +
                ", destURI='" + destURI + '\'' +
                ", destCat=" + destCat +
                '}';
    }
}
