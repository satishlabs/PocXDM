/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

public class KnEmailConfigDTO {
    private int emailAccID;
    private String smtp_Port;
    private int enableAuthentication;
    private String smtp_Host;
    private String transport_Type;
    private String displayName;
    private String smtp_UserName;
    private String smtp_Password;
    private String description;

    public int getEmailAccID() {
        return emailAccID;
    }

    public void setEmailAccID(int emailAccID) {
        this.emailAccID = emailAccID;
    }

    public String getSmtp_Port() {
        return smtp_Port;
    }

    public void setSmtp_Port(String smtp_Port) {
        this.smtp_Port = smtp_Port;
    }

    public int getEnableAuthentication() {
        return enableAuthentication;
    }

    public void setEnableAuthentication(int enableAuthentication) {
        this.enableAuthentication = enableAuthentication;
    }

    public String getSmtp_Host() {
        return smtp_Host;
    }

    public void setSmtp_Host(String smtp_Host) {
        this.smtp_Host = smtp_Host;
    }

    public String getTransport_Type() {
        return transport_Type;
    }

    public void setTransport_Type(String transport_Type) {
        this.transport_Type = transport_Type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSmtp_UserName() {
        return smtp_UserName;
    }

    public void setSmtp_UserName(String smtp_UserName) {
        this.smtp_UserName = smtp_UserName;
    }

    public String getSmtp_Password() {
        return smtp_Password;
    }

    public void setSmtp_Password(String smtp_Password) {
        this.smtp_Password = smtp_Password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(" emailAccID - ").append(emailAccID)
                .append(", smtp_Port - ").append(smtp_Port)
                .append(", smtp_Host - ").append(smtp_Host)
                .append(", enableAuthentication - ").append(enableAuthentication)
                .append(", transport_Type - ").append(transport_Type)
                .append(", displayName - ").append(displayName)
                .append(", smtp_UserName - ").append(smtp_UserName)
                .append(", smtp_Password - ").append(smtp_Password)
                .append(", description - ").append(description);
        return strBuffer.toString();
    }
}
