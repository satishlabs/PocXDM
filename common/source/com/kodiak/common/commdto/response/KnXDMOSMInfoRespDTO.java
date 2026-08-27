/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.io.Serializable;

public class KnXDMOSMInfoRespDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8751361494010702147L;
    private String msgId;
    private String msgOrderId;
    private String msgType;
    private String msgShortText;
    private String msg;

    public KnXDMOSMInfoRespDTO(){}

    public KnXDMOSMInfoRespDTO(String msgId, String msgOrderId, String msgType, String msgShortText, String msg) {
        this.msgId = msgId;
        this.msgOrderId = msgOrderId;
        this.msgType = msgType;
        this.msgShortText = msgShortText;
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgOrderId() {
        return msgOrderId;
    }

    public void setMsgOrderId(String msgOrderId) {
        this.msgOrderId = msgOrderId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgShortText() {
        return msgShortText;
    }

    public void setMsgShortText(String msgShortText) {
        this.msgShortText = msgShortText;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "KnOSMInfoDTO [msgId=" + msgId + ", msgOrderId=" + msgOrderId + ", msgType=" + msgType
                + ", msgShortText=" + msgShortText + ", msg=" + msg + "]";
    }

}

