/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.config;

import java.io.Serializable;

public class KnApnUriProfileInfo implements Serializable {

    private static final long serialVersionUID = 4612298731347106012L;
    private Integer recId;
    private String paramValue;

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "KnApnUriProfileInfo{" +
                "recId=" + recId +
                ", paramValue='" + paramValue + '\'' +
                '}';
    }
}
