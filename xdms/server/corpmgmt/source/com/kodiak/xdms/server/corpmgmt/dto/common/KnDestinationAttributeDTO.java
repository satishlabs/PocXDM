/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

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
