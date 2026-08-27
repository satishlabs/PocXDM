/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import java.io.Serializable;

public class KnXDMOSMInfoRequestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8521463292501764351L;
    private String msgId;
    private String msgOrderId;
    private String msgType;
    private String msgShortText;
    private String msg;


    public KnXDMOSMInfoRequestDTO() {

    }

    public KnXDMOSMInfoRequestDTO(String msgId, String msgOrderId, String msgType, String msgShortText,
                                  String msg) {
        super();
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
        result = prime * result + ((msgOrderId == null) ? 0 : msgOrderId.hashCode());
        result = prime * result + ((msgShortText == null) ? 0 : msgShortText.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KnXDMOSMInfoRequestDTO other = (KnXDMOSMInfoRequestDTO) obj;
        if (msgId == null) {
            if (other.msgId != null)
                return false;
        } else if (!msgId.equals(other.msgId))
            return false;
        if (msgOrderId == null) {
            if (other.msgOrderId != null)
                return false;
        } else if (!msgOrderId.equals(other.msgOrderId))
            return false;
        if (msgShortText == null) {
            if (other.msgShortText != null)
                return false;
        } else if (!msgShortText.equals(other.msgShortText))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "KnXDMOSMInfoRequestDTO{" +
                "msgId='" + msgId + '\'' +
                ", msgOrderId='" + msgOrderId + '\'' +
                ", msgType='" + msgType + '\'' +
                ", msgShortText='" + msgShortText + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
