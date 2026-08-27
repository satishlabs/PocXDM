/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/16/11
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnMailInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676121L;

    private String to;
    private String from;
    private String subject;
    private String body;
    private String objectId;


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getObjectId() {
        return objectId;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(" to - ").append(to)
                .append(", from - ").append(from)
                .append(", subject - ").append(subject)
                .append(", body - ").append(body)
                .append(", Object_Id - ").append(objectId);
        return strBuffer.toString();
    }
}
