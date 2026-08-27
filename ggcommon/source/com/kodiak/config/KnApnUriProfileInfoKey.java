/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.config;

import java.io.Serializable;

public class KnApnUriProfileInfoKey implements Serializable {
    private static final long serialVersionUID = -2502427457020997801L;
    private Integer apnId;
    private String paramName;

    public Integer getApnId() {
        return apnId;
    }

    public void setApnId(Integer apnId) {
        this.apnId = apnId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public String toString() {
        return "KnApnUriProfileInfoKey{" +
                "apnId=" + apnId +
                ", paramName='" + paramName + '\'' +
                '}';
    }
}
