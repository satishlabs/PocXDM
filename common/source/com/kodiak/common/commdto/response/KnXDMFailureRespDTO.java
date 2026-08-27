/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMRespDetailDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.common.commdto.response;

import java.io.Serializable;

public class KnXDMFailureRespDTO  implements Serializable{

    private static final long serialVersionUID = 7526471155622776155L;

    private String attribute;
    private String value;
    private String fcode;
    private String msg;

    public KnXDMFailureRespDTO(){

    }
    public KnXDMFailureRespDTO(String attribute, String value, String fcode, String msg) {
        this.attribute = attribute;
        this.value = value;
        this.fcode = fcode;
        this.msg = msg;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder(100);
        sb.append("fCode  ->").append(fcode)
          .append("msg  ->").append(msg)
          .append("value  ->").append(value)
          .append("attribute  ->").append(attribute);
        return sb.toString();
    }
}
